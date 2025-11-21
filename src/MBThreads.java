/* LibraryTag
 * © 2025 Frank Ambacher. All Rights Reserved.
 * For wishes, questions mail to anakor@gmx.net.
 */

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagField;
import org.jaudiotagger.tag.id3.AbstractID3v2Frame;
import org.jaudiotagger.tag.id3.framebody.FrameBodyTXXX;
import org.jaudiotagger.tag.id3.framebody.FrameBodyWXXX;

import io.aesy.musicbrainz.client.MusicBrainzClient;
import io.aesy.musicbrainz.entity.Artist;
import io.aesy.musicbrainz.entity.ReleaseGroup;

public abstract class MBThreads extends SwingWorker<Void, String> {
	protected JTextArea outArea;
    protected MusicBrainzClient client;
    protected String mbid;
    protected JProgressBar progress;
    protected int mblimit;
    protected boolean update;
    protected String basedir;
    protected StatusBar sb;
    static { Logger.getLogger("org.jaudiotagger").setLevel(Level.OFF); }
    public static String lastArtistName = null;
	
	public MBThreads(MusicBrainzClient client, String mbid ){
		this.client=client;
       	this.mbid=mbid;
        this.outArea = ((MusicBrainzPanel)LibraryTagWindow.frame.panel.currentPanel).outArea;
       	this.progress = ((MusicBrainzPanel)LibraryTagWindow.frame.panel.currentPanel).progress;
       	this.update = ((MusicBrainzPanel)LibraryTagWindow.frame.panel.currentPanel).update;
       	this.mblimit = ((MusicBrainzPanel)LibraryTagWindow.frame.panel.currentPanel).mblimit;
       	this.basedir = ((MusicBrainzPanel)LibraryTagWindow.frame.panel.currentPanel).basedir;
       	this.sb = LibraryTagWindow.frame.sb;
    }
	
	@Override
    protected Void doInBackground() throws Exception {
        executeTask();
        return null;
    }
	
	@Override
    protected void process(List<String> chunks) {
        for (String chunk : chunks) {
            appendToOut(chunk);
        }
    }
	
	@Override
    protected void done() {
        try {
            progress.setValue(0);
            sb.setStatus(1007); // "Ready" Status
        } catch (Exception e) {
            System.out.println("Error in done: " + e.getMessage());
            e.printStackTrace();
        }
    }
	
	protected void appendToOut(String text) {
		SwingUtilities.invokeLater(() -> outArea.append(text));
	}
	
	protected String normalizeString(String str) {
		int colon = str.indexOf(':');
		// if str has colon we cut the string 
		String cutstr = null;
		if (colon==-1) cutstr = str;
		else cutstr = str.substring(0, colon);
		return cutstr.replace(":", " -").replace("’", "'").trim();
	}
	
	/* **************
     * JAudioTagger *
     ****************/
	
	// For flac only
	/*protected String safeGet(Tag tag, String fieldId) {
		try {
			String value = tag.getFirst(fieldId);
			return (value != null && !value.trim().isEmpty()) ? value.trim() : "Not found";
		} catch (Exception e) { return "Not found"; }
	}*/
	
	private String getTXXXValue(Tag tag, String description) {
	    List<TagField> fields = tag.getFields("TXXX");
	    for (TagField field : fields) {
	        if (field instanceof AbstractID3v2Frame) {
	            AbstractID3v2Frame frame = (AbstractID3v2Frame) field;
	            if (frame.getBody() instanceof FrameBodyTXXX) {
	                FrameBodyTXXX body = (FrameBodyTXXX) frame.getBody();
	                if (description.equalsIgnoreCase(body.getDescription())) {
	                    String text = body.getText();
	                    if (text != null && !text.trim().isEmpty()) {
	                        return text.trim();
	                    }
	                }
	            }
	        }
	    }
	    return "Not found";
	}
	
	// For mixed flac and mp3 files
	protected String safeGet2(AudioFile f, FieldKey fieldKey) {
	    try {
	        Tag tag = f.getTag();
	        if (tag == null) return "Not found";

	        // Default-Routine
	        String value = tag.getFirst(fieldKey);
	        if (value != null && !value.trim().isEmpty()) {
	            return value.trim();
	        }

	        if (fieldKey == FieldKey.ARTIST) {
	        	String tpe1 = tag.getFirst("TPE1");
	        	String tpe2 = tag.getFirst("TPE2");
		            	            
	            if (tpe1 != null && !tpe1.trim().isEmpty()) { return tpe1.trim(); }
	            else if (tpe2 != null && !tpe2.trim().isEmpty()) { return tpe2.trim(); }
	        }
		        
	        if (fieldKey == FieldKey.MUSICBRAINZ_ARTISTID) {
	            String std = tag.getFirst("musicbrainz_artistid");
	            if (std != null && !std.trim().isEmpty()) {
	                return std.trim();
	            }
	            return getTXXXValue(tag, "musicbrainz_albumartistid");
	        }
	        return "Not found";
	    } catch (Exception e) { return "Not found"; }
	}
	
	protected void printAllFields(Tag tag) {
		System.out.println("\n--- All Tags in the file ---");
		Iterator<TagField> it = tag.getFields();
		while (it.hasNext()) {
			TagField field = it.next();
			System.out.println(field.getId() + " = " + tag.getFirst(field.getId()));
		}
		System.out.println("-----------------------------------\n");
	}
	
	protected void printAllTXXX(Tag tag) {
	    List<TagField> txxxFields = tag.getFields("TXXX");  // direkt alle TXXX
	    for (int i = 0; i < txxxFields.size(); i++) {
	        TagField field = txxxFields.get(i);
	        if (field instanceof AbstractID3v2Frame) {
	            FrameBodyTXXX body = (FrameBodyTXXX) ((AbstractID3v2Frame) field).getBody();
	            System.out.println("TXXX [" + i + "] '" + body.getDescription() + "' = '" + body.getText() + "'");
	        }
	    }
	}
	
	/* ****************
     * File-Functions *
     ******************/
	
	protected String getDBFlagsSpec( String dbflags ) {
    	if(dbflags==null) return null;
    	
    	String[] dbf_array = dbflags.split(";");
    	String   country=null, year, fromtake, totake;
    	boolean  alternate, bonus, broadcast, demo, isolated, live, outtake, remixed,
    			 stereo, mono, eps, quatro, singleA, singleB, ep, sampler, take,
    			 vinyl, vinylrest, stolen, rerecord, christian, instrumental, soundtrack,
    			 xmas, bad;
    	int		 pos ;
    	
    	for(String flag : dbf_array) {
    		country = dbf_array[0]; // is always the first field 
            year = dbf_array[1];  // is always the second field
            for( int i=2; i<dbf_array.length-2 ;i++ ) {
            	switch(dbf_array[i]) {
            		case "A":     alternate=true;                                       break;
            		case "B":     bonus=true;                                           break;
            		case "Bad":   bad=true;                                             break;
            		case "Bc":    broadcast=true;                                       break;
            		case "C":     christian=true;                                       break;
            		case "D":     demo=true;                                            break;
            		case "I":     instrumental=true;                                    break;
            		case "Iso":   isolated=true;                                        break;
            		case "L":     live=true;                                            break;
            		case "O":     outtake=true;                                         break;
            		case "R":     remixed=true;                                         break;
            		case "RR":    rerecord=true;                                        break;
            		case "St":    soundtrack=true;                                      break;
            		case "X":     stolen=true;                                          break;
            		case "S":     stereo=true; mono=false; eps=false; quatro=false;     break;
            		case "M":     mono=true; stereo=false; eps=false; quatro=false;     break;
            		case "Q":     quatro=true; mono=false; stereo=false; eps=false;     break;
            		case "EPS":   eps=true; mono=false; stereo=false; quatro=false;     break;
            		case "SA":    singleA=true; singleB=false; ep=false; sampler=false; break;
            		case "SB":    singleB=true; singleA=false; ep=false; sampler=false; break;
            		case "EP":    ep=true; singleA=false; singleB=false; sampler=false; break;
            		case "SMP":   sampler=true; singleA=false; singleB=false; ep=false; break;
            		case "T":     take=true; fromtake=null;  totake=null;               break;
            		case "VNL":   vinyl=true; vinylrest=false;                          break;
            		case "VR":    vinylrest=true; vinyl=false;                          break;
            		case "XMAS":  xmas=true;                                            break;
            		default:	  if( dbf_array[i].startsWith("T:") ) {
            			              String[] parts = dbf_array[i].split(": | /");
            			              take = true;
            			              fromtake = parts[1];
            			              if(parts.length==2) totake = null;
            						  else if(parts.length==3) totake = parts[2];
            						  else System.out.println("DBFlagSpec error "
            								  + dbf_array[i]);
            					  }                                                     break;
            	}
            }
    	}
    	for(String flag : dbf_array)
    	System.out.println(flag);
    	return country;
    }
	
	protected List<String> getFileLocations( String artistname , String title) throws IOException {
		return findFiles3(artistname, title, basedir, ".*\\.(flac|mp3|ogg)$");
	}
	
	protected List<String> findFiles( String artistname, String title, String start, String extensionPattern ) {
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
	
	protected List<String> findFiles2(String artistname, String title, String start, String extensionPattern) throws IOException {
	    final Pattern p = Pattern.compile(extensionPattern, Pattern.CASE_INSENSITIVE);
	    final String finalArtistname = artistname.toLowerCase();
	    final String finalTitle = title.toLowerCase();
	    List<String> foundFiles = new ArrayList<>();

	    Path startPath = Paths.get(start);
	    System.out.println("startPath=" + startPath + ", isDirectory=" + Files.isDirectory(startPath));
	    System.out.flush();
	    if (!Files.isDirectory(startPath)) {
	        System.out.println("startPath is not a directory, returning empty list");
	        System.out.flush();
	        return foundFiles;
	    }

	    String[] mediaDirs = {"FLAC", "MP3@320", "MP3@192", "MP3@128", "MP3EU", "MP3", "OGG", "Classical"};
	    for (String mediaDir : mediaDirs) {
	        Path mediaPath = startPath.resolve(mediaDir);
	        System.out.println("Checking media directory: " + mediaPath);
	        System.out.flush();
	        if (!Files.isDirectory(mediaPath)) {
	            System.out.println("Media directory does not exist or is not a directory: " + mediaPath);
	            System.out.flush();
	            continue;
	        }

	        try (Stream<Path> paths = Files.walk(mediaPath, FileVisitOption.FOLLOW_LINKS)) {
	            paths.filter(path -> {
	                try {
	                    return Files.isRegularFile(path);
	                } catch (Exception e) {
	                    System.err.println("Access error for path: " + path + ", skipping: " + e.getMessage());
	                    return false;
	                }
	            })
	            .filter(path -> {
	                try {
	                    boolean matches = p.matcher(path.getFileName().toString()).matches();
	                    if (matches) {
	                        //System.out.println("File matches extension pattern: " + path);
	                        System.out.flush();
	                    }
	                    return matches;
	                } catch (Exception e) {
	                    System.err.println("Error in extension filter for path: " + path + ", skipping: " + e.getMessage());
	                    System.err.flush();
	                    return false;
	                }
	            })
	            .filter(path -> {
	                try {
	                    String pathStr = path.toString().toLowerCase();
	                    boolean artistMatch = pathStr.contains(File.separator + finalArtistname + File.separator);
	                    Path parent = path.getParent();
	                    boolean titleMatch = parent != null && parent.getFileName() != null 
	                        && parent.getFileName().toString().toLowerCase().contains(finalTitle);
	                    boolean result = artistMatch && titleMatch;
	                    if (result) {
	                        System.out.println("Found matching file: " + path);
	                        System.out.flush();
	                    }
	                    return result;
	                } catch (Exception e) {
	                    System.err.println("Error in path filter for path: " + path + ", skipping: " + e.getMessage());
	                    System.err.flush();
	                    return false;
	                }
	            })
	            .map(path -> {
	                try {
	                    return path.toAbsolutePath().toString();
	                } catch (Exception e) {
	                    System.err.println("Error converting path to absolute: " + path + ", skipping: " + e.getMessage());
	                    System.err.flush();
	                    return null;
	                }
	            })
	            .filter(Objects::nonNull)
	            .forEach(foundFiles::add);
	        } catch (IOException e) {
	            System.err.println("IOException in Files.walk for " + mediaPath + ": " + e.getMessage());
	            System.err.flush();
	        } catch (Throwable t) {
	            System.err.println("Critical error in Files.walk for " + mediaPath + ": " + t.getClass().getSimpleName() + ": " + t.getMessage());
	            t.printStackTrace(System.err);
	            System.err.flush();
	        }
	    }

	    System.out.println("findFiles2 completed, found " + foundFiles.size() + " files");
	    System.out.flush();
	    return foundFiles;
	}
	
	// Files.walk with FindFiles logic
	protected List<String> findFiles3(String artistname, String title, String start, String extensionPattern) throws IOException {
	    final Pattern p = Pattern.compile(extensionPattern, Pattern.CASE_INSENSITIVE);
	    final String finalArtistname = artistname.toLowerCase();
	    final String finalTitle = title.toLowerCase();
	    List<String> foundFiles = new ArrayList<>();

	    Path startPath = Paths.get(start);
	    if (!Files.isDirectory(startPath)) {
	        System.out.println("startPath is not a directory, returning empty list");
	        System.out.flush();
	        return foundFiles;
	    }

	    String[] mediaDirs = {"FLAC", "MP3@320", "MP3@192", "MP3@128", "MP3EU", "MP3", "OGG", "Classical"};
	    for (String mediaDir : mediaDirs) {
	        Path mediaPath = startPath.resolve(mediaDir);
	        if (!Files.isDirectory(mediaPath)) continue;
	        
	        try (Stream<Path> paths = Files.walk(mediaPath, FileVisitOption.FOLLOW_LINKS)) {
	            Optional<Path> matchingDir = paths.filter(path -> {
	                try {
	                    String pathStr = path.toString().toLowerCase();
	                    boolean result = Files.isDirectory(path) && !Files.isSymbolicLink(path)
	                        && pathStr.contains(File.separator + finalArtistname + File.separator)
	                        && pathStr.contains(finalTitle);
	                    return result;
	                } catch (Exception e) {
	                    System.err.println("Access error for path: " + path + ", skipping: " + e.getMessage());
	                    System.err.flush();
	                    return false;
	                }
	            }).findFirst();

	            if (matchingDir.isPresent()) {
	                Path dir = matchingDir.get();
	                try (Stream<Path> files = Files.list(dir)) {
	                    files.filter(Files::isRegularFile)
	                         .filter(path -> {
	                             try {
	                                 boolean matches = p.matcher(path.getFileName().toString()).matches();
	                                 if (matches) {}
	                                 return matches;
	                             } catch (Exception e) {
	                                 System.err.println("Error in extension filter for path: " + path + ", skipping: " + e.getMessage());
	                                 System.err.flush();
	                                 return false;
	                             }
	                         })
	                         .map(path -> {
	                             try {
	                                 return path.toAbsolutePath().toString();
	                             } catch (Exception e) {
	                                 System.err.println("Error converting path to absolute: " + path + ", skipping: " + e.getMessage());
	                                 System.err.flush();
	                                 return null;
	                             }
	                         })
	                         .filter(Objects::nonNull)
	                         .forEach(foundFiles::add);
	                }
	                System.out.flush();
	                // Break on first directory, like findFiles
	                break;
	            }
	        } catch (IOException e) {
	            System.err.println("IOException in Files.walk for " + mediaPath + ": " + e.getMessage());
	            System.err.flush();
	        } catch (Throwable t) {
	            System.err.println("Critical error in Files.walk for " + mediaPath + ": " + t.getClass().getSimpleName() + ": " + t.getMessage());
	            t.printStackTrace(System.err);
	            System.err.flush();
	        }
	    }

	    System.out.flush();
	    return foundFiles;
	}
	
	// DebugVersion
	protected List<String> findFiles5(String artistname, String title, String start, String extensionPattern) throws IOException {
	    System.out.println("findFiles5 started: artistname=" + artistname + ", title=" + title + ", start=" + start);
	    final Pattern p = Pattern.compile(extensionPattern, Pattern.CASE_INSENSITIVE);
	    final String finalArtistname = artistname.toLowerCase();
	    final String finalTitle = title.toLowerCase();
	    List<String> foundFiles = new ArrayList<>();

	    Path startPath = Paths.get(start);
	    System.out.println("startPath=" + startPath + ", isDirectory=" + Files.isDirectory(startPath));
	    if (!Files.isDirectory(startPath)) {
	        System.out.println("startPath is not a directory, returning empty list");
	        return foundFiles;
	    }

	    try (Stream<Path> paths = Files.walk(startPath)) {
	        paths.filter(Files::isRegularFile)
	             .filter(path -> {
	                 try {
	                     return p.matcher(path.getFileName().toString()).matches();
	                 } catch (Exception e) {
	                     System.err.println("Error in extension filter for path: " + path + ", error: " + e.getMessage());
	                     return false;
	                 }
	             })
	             .filter(path -> {
	                 try {
	                     String pathStr = path.toString().toLowerCase();
	                     boolean artistMatch = pathStr.contains(File.separator + finalArtistname + File.separator);
	                     Path parent = path.getParent();
	                     boolean titleMatch = parent != null && parent.getFileName() != null 
	                         && parent.getFileName().toString().toLowerCase().contains(finalTitle);
	                     boolean result = artistMatch && titleMatch;
	                     if (result) {
	                         System.out.println("Found matching file: " + path);
	                     }
	                     return result;
	                 } catch (Exception e) {
	                     System.err.println("Error in path filter for path: " + path + ", error: " + e.getMessage());
	                     return false;
	                 }
	             })
	             .map(Path::toAbsolutePath)
	             .map(Path::toString)
	             .forEach(foundFiles::add);
	    } catch (IOException e) {
	        System.err.println("IOException in Files.walk: " + e.getMessage());
	        throw e;
	    } catch (Exception e) {
	        System.err.println("Unexpected error in Files.walk: " + e.getMessage());
	        throw new IOException("Unexpected error during file walking", e);
	    }

	    System.out.println("findFiles2 completed, found " + foundFiles.size() + " files");
	    return foundFiles;
	}
	
	protected abstract void executeTask() throws Exception;
}
