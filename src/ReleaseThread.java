/* LibraryTag
 * © 2025 Frank Ambacher. All Rights Reserved.
 * For wishes, questions mail to anakor@gmx.net.
 */

import java.io.File;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import io.aesy.musicbrainz.client.MusicBrainzClient;
import io.aesy.musicbrainz.entity.ArtistCredit;
import io.aesy.musicbrainz.entity.NameCredit;
import io.aesy.musicbrainz.entity.Recording;
import io.aesy.musicbrainz.entity.Release;

public class ReleaseThread extends MBThreads {
	
    public ReleaseThread( MusicBrainzClient client, String mbid ) {
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
    private String getArtistName(Release release) {
    	ArtistCredit artistCredit = release.getArtistCredit();
        if (artistCredit != null && !artistCredit.getNameCredit().isEmpty()) {
            List<NameCredit> nameCreditList=artistCredit.getNameCredit();
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
    	int value=0; // holds the progress
    	
        Release release = client.release().withId(UUID.fromString(mbid)).includeArtists().lookup().get();
        String releasetitle = release.getTitle();
        String disambiguation = release.getDisambiguation();
        
        // often a disambiguation is placed into the title
        if( releasetitle.indexOf('(')>-1 && releasetitle.lastIndexOf(')')>-1 ) {
        	String[] tarray = releasetitle.split("\\s*\\(");
        	releasetitle = tarray[0].trim();
        	disambiguation = tarray[1].replaceAll("\\s*\\)$", "").trim();
        }
        publish("Release is: " + releasetitle + "\n");
        
        List<String> foundfiles = null;
        List<Recording> recordings = client.recording().withRelease(release).limitBy(mblimit).browse().get();
        publish("Release has " + recordings.size() + " Recordings\n\n");
        progress.setMaximum(recordings.size());
        	
        for( Recording recording : recordings ) {
        	String title = recording.getTitle();
        	publish(title + ": " + recording.getId() + "\n");
        	// currently not supported by client
        	// publish("ISRC is: " + recording.getIsrcList() + "\n");
        	        		
        	if (update) {
        		try {
        			foundfiles = getFileLocations( lastArtistName = getArtistName(release), releasetitle );
        			if (foundfiles.size()>0) {
        				for(String file : foundfiles) {
        					String searchString = normalizeString(recording.getTitle())
        						+ (recording.getDisambiguation() != null ? " (" + recording.getDisambiguation() + ")" : "");
        					if (file.toLowerCase().contains(searchString.toLowerCase())) {
        						openFile(file);
        						if(recording.getDisambiguation()==null) break;
        					}
        				}
        			}
        		}
        		catch(Exception e) { System.out.println(e); }
        	}
        	publish("\n");
        	progress.setValue(value++);
        }
        sb.setStatus(1007);
    }
 }
