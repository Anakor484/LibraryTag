/* LibraryTag
 * © 2025 Frank Ambacher. All Rights Reserved.
 * For wishes, questions mail to anakor@gmx.net.
 */

import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import io.aesy.musicbrainz.client.MusicBrainzClient;
import io.aesy.musicbrainz.entity.ArtistCredit;
import io.aesy.musicbrainz.entity.Isrc;
import io.aesy.musicbrainz.entity.IsrcList;
import io.aesy.musicbrainz.entity.NameCredit;
import io.aesy.musicbrainz.entity.Recording;
import io.aesy.musicbrainz.entity.Release;

public class RecordingThread extends MBThreads {
	
    public RecordingThread( MusicBrainzClient client, String mbid ) {
    	super(client, mbid);
    }
    
    /* ****************
     * File-Functions *
     ******************/
    
    private void openFile(String filePath) {
    	Logger.getLogger("org.jaudiotagger").setLevel(Level.OFF);

	    try {
	        File audioFile = new File(filePath);
	        AudioFile f = AudioFileIO.read(audioFile);
	        Tag tag = f.getTag();
	        	        
	        //getDBFlagsSpec( safeGet(tag, "DBFLAGS") );
	        publish( "Found: " + safeGet2(f, FieldKey.ARTIST) + "-" + safeGet2(f, FieldKey.ALBUM) + "-" + safeGet2(f, FieldKey.TITLE) + "\n" );
	        if(!safeGet2(f, FieldKey.MUSICBRAINZ_ARTISTID).equals("Not found"))
	        	publish( "ArtistID were found: " + safeGet2(f, FieldKey.MUSICBRAINZ_ARTISTID)  + "\n" );
	        
	    } catch (Exception e) {
	        System.err.println("Error on read from " + filePath + ": " + e.getMessage());
	        e.printStackTrace();
	    }
	}
    
    // getArtistName() tries to get the artistname from a subcall.
    // the corresponding lookup call must have includeArtists() to get successful.
    // if we can't get the name, we try to get the name from lastArtistName, a static
    // variable in MBThreads. If this isn't successful too, we return null which
    // give an exception. 
    private String artistname;
    private String getArtistName(Recording recording) {
    	ArtistCredit artistCredit = recording.getArtistCredit();
        if (artistCredit != null && !artistCredit.getNameCredit().isEmpty()) {
            List<NameCredit> nameCreditList = artistCredit.getNameCredit();
        	for(NameCredit nameCredit : nameCreditList) {
        		if(!nameCredit.getArtist().getName().equals("null")) {
        			artistname = nameCredit.getArtist().getName();
        			break;
        		}
        	}
        	artistCredit.getNameCredit().forEach(name -> {
            	if(!name.getArtist().getName().equals("null")) {
            		artistname  = name.getArtist().getName();
            		return;
            	}
            });
        }
        if(!artistname.equals("null")) return artistname;
        else if(lastArtistName!=null) return lastArtistName;
        return null;
    }
    
    @Override
    public void executeTask() {
    	Recording recording = client.recording().withId(UUID.fromString(mbid)).includeArtists().includeReleases().lookup().get();
    	
    	lastArtistName = getArtistName(recording);
    	publish("Artist is: " + lastArtistName  + "\n");
    	
    	String albumTitle = null;
        List<Release> rL =recording.getReleaseList().getRelease();
        for(Release release : rL)
        	if(!release.getTitle().equals(null) && !release.getTitle().equals("null")) {
        		albumTitle = normalizeString(release.getTitle());
        		// ISRC currently isn't supported by client
        		//String isrc = getIsrcFromRec(release);
            	//System.out.println(isrc);
        		break;
        	}
        publish("Release is: " + albumTitle + "\n");
        
        // often the track indexes are placed into the title
        String recordingtitle=recording.getTitle();
        if( recordingtitle.indexOf(':')>-1 ) {
        	String[] tarray = recordingtitle.split("\\s*\\:");
        	recordingtitle = normalizeString(tarray[0].trim());
        }
        publish("Recording is: " + recordingtitle + "\n");
                         
        List<String> foundfiles = null;
        if (update) {
    		try {
    			foundfiles = getFileLocations( lastArtistName = getArtistName(recording), albumTitle );
    			if (foundfiles.size()>0) {
    				for(String file : foundfiles) {
    					if( file.toLowerCase().contains(recordingtitle.toLowerCase()) ) {
    						openFile(file);
    						break;
    					}	
    				}
    			}
    			publish("\n");
    		}
    		catch(Exception e) { System.out.println(e); }
    	}
    }
}
