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
import io.aesy.musicbrainz.entity.Release;
import io.aesy.musicbrainz.entity.ReleaseGroup;

public class ReleaseGroupThread extends MBThreads {
	
    public ReleaseGroupThread( MusicBrainzClient client, String mbid, ExecutorService executor ) {
    	super(client, mbid, executor);
    }
    
    @Override
    public void run() {
        try {
        	//executor.submit(currentSpinner);
        	//executor.execute(currentSpinner);
        	ReleaseGroup rG = client.releaseGroup().withId(UUID.fromString(mbid)).lookup().get();
        	outArea.append("ReleaseGroup is: " + rG.getTitle() + "\n");
        	
        	List<String> foundfiles=null;
        	List<Release> releases = client.release().withReleaseGroup(rG).limitBy(mblimit).browse().get();
        	outArea.append("ReleaseGroup has " + releases.size() + " Releases\n");
        	//stopSpinner();
        	
        	for( int i=0; i<releases.size(); i++ ) {
        		String title = releases.get(i).getTitle();
        		String country = releases.get(i).getCountry();
        		if(country==null) country="  "; // we format a little for outArea
        		if (title.length() > 35) title = title.substring(0, 35);
        		//executor.execute(currentSpinner);
        		outArea.append(country + " " + title + ": " + releases.get(i).getId() + "\n");
        		
        		/*if (update) {
        			//outArea.append("Trying to find corresponding audio files. \n");
        			try {
        				//executor.execute(currentSpinner);
        				foundfiles = getFileLocations( "Yes", releases.get(i).getTitle() );
        				if (foundfiles.size()>0) {
        					for(int l=0; l<foundfiles.size(); l++) {
        						outArea.append("Found: " + foundfiles.get(l) + " \n");
							}
						} //else outArea.append("No corresponding audio files found. \n");
						//stopSpinner();
					}
					catch(Exception ex) {
						System.out.println(ex);
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
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
 }
