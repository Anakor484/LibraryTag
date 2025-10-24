/* LibraryTag
 * © 2025 Frank Ambacher. All Rights Reserved.
 * For wishes, questions mail to anakor@gmx.net.
 */

import java.io.File;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import io.aesy.musicbrainz.client.MusicBrainzClient;
import io.aesy.musicbrainz.entity.Artist;
import io.aesy.musicbrainz.entity.ReleaseGroup;

public class ArtistThread extends MBThreads {
	public ArtistThread( MusicBrainzClient client, String mbid ) {
    	super( client, mbid );
    }
        
    @Override
    public void run() {
        try {
        	SwingUtilities.invokeLater(() -> {
                spinner = new OutSpinner();
                spinner.execute();
            });
        	
        	Artist artist = client.artist().withId(UUID.fromString(mbid)).lookup().get();
        	outArea.append("Artist is: " + artist.getName() + "\n");
        	List<String> foundfiles=null;
        	List<ReleaseGroup> rGL = client.releaseGroup().withArtist(artist).limitBy(mblimit).browse().get();
        	outArea.append("Artist has " + rGL.size() + " ReleaseGroups \n");
        	outArea.append("Only Albums are listed: \n");
        	        					
        	for( int i=0; i<rGL.size(); i++ ) {
        		if(rGL.get(i).getPrimaryType()!=null)
				if(rGL.get(i).getPrimaryType().getContent().equals("Album")) {
					String title = rGL.get(i).getTitle();
					if (title.length() > 38) title = title.substring(0, 38);
					outArea.append(title + ": " + rGL.get(i).getId() + " \n");
					if (update) {
						//outArea.append("Trying to find corresponding audio files. \n");
						try {
							foundfiles = getFileLocations( artist.getName(), rGL.get(i).getTitle() );
							if (foundfiles.size()>0) {
								for(int l=0; l<foundfiles.size(); l++) {
									outArea.append("Found: " + foundfiles.get(l) + " \n");
								}
							} //else outArea.append("No corresponding audio files found. \n");
						}
						catch(Exception ex) {
							System.out.println(ex);
						}
					}
				}
        	}
        	outArea.append("\n");
        	sb.setStatus(1007);
        }
        catch( NoSuchElementException e ) {
			StackTraceElement[] stacktrace = e.getStackTrace();
			for (int i = 0; i < e.getStackTrace().length; i++) System.out.println(stacktrace[i].toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
 }
