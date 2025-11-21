/* LibraryTag
 * © 2025 Frank Ambacher. All Rights Reserved.
 * For wishes, questions mail to anakor@gmx.net.
 */

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagField;
import org.jaudiotagger.tag.id3.ID3v23Frames;
import org.jaudiotagger.tag.id3.ID3v24Frames;

import io.aesy.musicbrainz.client.MusicBrainzClient;
import io.aesy.musicbrainz.entity.Artist;
import io.aesy.musicbrainz.entity.ReleaseGroup;

public class ArtistThread extends MBThreads {
		
	public ArtistThread( MusicBrainzClient client, String mbid ) {
    	super( client, mbid );
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
        
    @Override
    protected void executeTask() throws Exception {
    	Artist artist = client.artist().withId(UUID.fromString(mbid)).lookup().get();
    	lastArtistName = artist.getName();
    	publish("Artist is: " + artist.getName() + "\n");
    	List<ReleaseGroup> rGL = client.releaseGroup().withArtist(artist).limitBy(mblimit).browse().get();
    	publish("Artist has " + rGL.size() + " ReleaseGroups \n");
    	publish("Only Albums are listed: \n\n");
    	progress.setMaximum(rGL.size());
    	int value=0;
    	for( ReleaseGroup rg : rGL ) {
    		if (rg.getPrimaryType() != null && rg.getPrimaryType().getContent().equals("Album")) {
    			String title = rg.getTitle();
    			publish(title + ": " + rg.getId() + " \n");
				if (update) {
                    List<String> foundFiles = getFileLocations(artist.getName(), normalizeString(title));
                    if (!foundFiles.isEmpty()) {
                    	for (String file : foundFiles) {
                    		openFile(file);
                        }
                    }
                }
				publish("\n");
				progress.setValue(value++);
			}
    	}
    }
}
