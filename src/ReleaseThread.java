/* LibraryTag
 * © 2025 Frank Ambacher. All Rights Reserved.
 * For wishes, questions mail to anakor@gmx.net.
 */

import java.io.File;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import javax.swing.JTextArea;
import io.aesy.musicbrainz.client.MusicBrainzClient;
import io.aesy.musicbrainz.entity.Recording;
import io.aesy.musicbrainz.entity.Release;

public class ReleaseThread extends MBThreads {
	
    public ReleaseThread( MusicBrainzClient client, String mbid, ExecutorService executor ) {
    	super(client, mbid, executor);
    }
    
    @Override
    public void run() {
        try {
        	//executor.submit(currentSpinner);
        	//executor.execute(currentSpinner);
        	Release release = client.release().withId(UUID.fromString(mbid)).includeRecordings().lookup().get();
        	outArea.append("Release is: " + release.getTitle() + "\n");
        	
        	List<Recording> recording = client.recording().withRelease(release).limitBy(mblimit).browse().get();
        	outArea.append("Release has " + recording.size() + " Recordings\n");
        	//stopSpinner();
        	
        	for( int i=0; i<recording.size(); i++ ) {
        		String title = recording.get(i).getTitle();
        		if (title.length() > 38) title = title.substring(0, 38);
        		//executor.execute(currentSpinner);
        		outArea.append(title + ": " + recording.get(i).getId() + "\n");
        		System.out.println(recording.get(i).getIsrcList());
        		
        		/*if (update) {
        			//outArea.append("Trying to find corresponding audio files. \n");
        			try {
        				//executor.execute(currentSpinner);
        				foundfiles = getFileLocations( artist.getName(), rGL.get(i).getTitle() );
        				if (foundfiles.size()>0) {
        					for(int l=0; l<foundfiles.size(); l++) {
        						outArea.append("Found: " + foundfiles.get(l) + " \n");
        					}
        				} //else outArea.append("No corresponding audio files found. \n");
        				//stopSpinner();
        			}
        			catch(Exception e) {
        				System.out.println(e);
        			}
        		}*/
        		//stopSpinner();
			}
        	outArea.append("\n");
        	sb.setStatus(1007);
        }
        catch( NoSuchElementException e ) {
			StackTraceElement[] stacktrace = e.getStackTrace();
			for (int i = 0; i < e.getStackTrace().length; i++) System.out.println(stacktrace[i].toString());
		} catch (Exception e) { e.printStackTrace(); }
    }
 }
