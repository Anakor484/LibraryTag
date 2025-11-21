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

import javax.swing.JTextArea;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import io.aesy.musicbrainz.client.MusicBrainzClient;
import io.aesy.musicbrainz.entity.ArtistCredit;
import io.aesy.musicbrainz.entity.NameCredit;
import io.aesy.musicbrainz.entity.Release;
import io.aesy.musicbrainz.entity.ReleaseGroup;

public class ReleaseGroupThread extends MBThreads {
	
    public ReleaseGroupThread( MusicBrainzClient client, String mbid ) {
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
    private String getArtistName( ReleaseGroup rGroup ) {
    	ArtistCredit artistCredit = rGroup.getArtistCredit();
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
    protected void executeTask() throws Exception {
    	int value=0; // holds progress
    	ReleaseGroup rG = client.releaseGroup().withId(UUID.fromString(mbid)).includeArtists().lookup().get();
    	String primaryType = rG.getPrimaryType().getContent();
    	publish("ReleaseGroup is: " + rG.getTitle() + "\n");
    	
    	List<Release> rGL = client.release().withReleaseGroup(rG).limitBy(mblimit).browse().get();
    	publish("ReleaseGroup has " + rGL.size() + " Releases\n\n");
    	progress.setMaximum(rGL.size());
    	
    	String title = null;
    	if(primaryType!=null && primaryType.equals("Album"))
    	for( Release release : rGL ) {
    		title = normalizeString(release.getTitle());
    		// we format a little for outArea, country can be empty
    		String country = release.getCountry();
    		if(country==null) country="  ";
    		publish(country + " " + (title.length()>35 ? title.substring(0, 35) : title) + ": " + release.getId() + "\n");
    		progress.setValue(value++);
    	}
    	publish("\n");
    	
    	if (update) {
    		// We only want scan the files once, but list the releases in the outArea.
    		// We only use artist.name and release.title for searching files. This should be
    		// in all releases the same, it makes no matter which release we take
    		value=0;
    		List<String> foundfiles=null;
    		
    		try {
    			//System.out.println(getArtistName(rG));
    			foundfiles = getFileLocations( lastArtistName=getArtistName(rG), title );
    			if (foundfiles.size()>0) {
    				progress.setMaximum(foundfiles.size());
    				for(String file : foundfiles) {
    					openFile(file);
    				}
    				progress.setValue(value++);
    			}
    			lastArtistName=null;
    		}
    		catch(Exception ex) { System.out.println(ex); }
    	}
    	publish("\n");
    	sb.setStatus(1007);
    }
 }
