/* LibraryTag
 * © 2025 Frank Ambacher. All Rights Reserved.
 * For wishes, questions mail to anakor@gmx.net.
 */

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Stack;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import javax.swing.JTextArea;
import io.aesy.musicbrainz.client.MusicBrainzClient;
import io.aesy.musicbrainz.entity.Artist;
import io.aesy.musicbrainz.entity.ReleaseGroup;

public abstract class MBThreads implements Runnable {
	protected JTextArea outArea;
    protected MusicBrainzClient client;
    protected String mbid;
    protected OutSpinner currentSpinner;
    protected ExecutorService executor;
    protected int mblimit;
    protected boolean update;
    protected String basedir;
    protected StatusBar sb;
	
	public MBThreads(MusicBrainzClient client, String mbid, ExecutorService executor){
		this.client=client;
       	this.mbid=mbid;
       	this.executor=executor;
       	this.outArea = ((MusicBrainzPanel)LibraryTagWindow.frame.panel.currentPanel).outArea;
       	this.update = ((MusicBrainzPanel)LibraryTagWindow.frame.panel.currentPanel).update;
       	this.mblimit = ((MusicBrainzPanel)LibraryTagWindow.frame.panel.currentPanel).mblimit;
       	this.basedir = ((MusicBrainzPanel)LibraryTagWindow.frame.panel.currentPanel).basedir;
       	this.sb = LibraryTagWindow.frame.sb;
       	//currentSpinner = new OutSpinner(outArea);
	}
	
	public void run() {
    }
	
	private synchronized void stopSpinner() throws Exception {
		if (currentSpinner != null && !currentSpinner.isDone()) {
			currentSpinner.cancel(true); currentSpinner.done();
		}
	}
	
	protected synchronized List<String> getFileLocations( String artistname , String title) throws Exception {
		return findFiles( artistname,title, basedir, "(.*.flac$)|(.*.mp3$)|(.*.ogg$)");
	}
	
	protected synchronized List<String> findFiles( String artistname, String title, String start, String extensionPattern ) {
    	final Stack<File> dirs = new Stack<>();
        final File startdir = new File(start);
        final Pattern p = Pattern.compile(extensionPattern, Pattern.CASE_INSENSITIVE);
        artistname = artistname.toLowerCase();
        title = title.toLowerCase();
    	        
        List<String> foundFiles = new ArrayList<>();
    	        
        if (!startdir.isDirectory()) return foundFiles;
    	    
    	dirs.push(startdir);
    	while (!dirs.isEmpty()) {
    	   	File currentDir = dirs.pop();
    	    File[] files = currentDir.listFiles();
    	    if (files == null) continue; 
    	            
    	    for (File file : files) {
    	       	if (file.isDirectory()) {
    	       		String ff = file.getAbsolutePath().toLowerCase();
    	       		if (ff.contains("flac") || ff.contains("mp3") || ff.contains("ogg"))
    	            if (ff.contains(file.separatorChar + artistname + file.separatorChar) && ff.contains(title)) {
    	       			for (File nestedFile : file.listFiles()) {
    	                   	if (p.matcher(nestedFile.getName()).matches()) foundFiles.add(nestedFile.getAbsolutePath());
    	                }
    	                return foundFiles;
    	            } else dirs.push(file);
    	        }
    	    }
    	}
    	return foundFiles;
    }
}
