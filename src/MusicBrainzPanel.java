/* LibraryTag
 * © 2025 Frank Ambacher. All Rights Reserved.
 * For wishes, questions mail to anakor@gmx.net.
 */

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.NoSuchElementException;
import java.util.Stack;
import java.util.UUID;
import java.util.regex.Pattern;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import io.aesy.musicbrainz.client.MusicBrainzClient;
import io.aesy.musicbrainz.client.MusicBrainzJerseyClient;
import io.aesy.musicbrainz.client.MusicBrainzJerseyClient.Builder;
import io.aesy.musicbrainz.client.MusicBrainzLookupRequest;
import io.aesy.musicbrainz.client.MusicBrainzResponse;
import io.aesy.musicbrainz.entity.Artist;
import io.aesy.musicbrainz.entity.Cdstub.TrackList.Track;
import io.aesy.musicbrainz.entity.Isrc;
import io.aesy.musicbrainz.entity.IsrcList;
import io.aesy.musicbrainz.entity.Medium;
import io.aesy.musicbrainz.entity.Medium.TrackList;
import io.aesy.musicbrainz.entity.MediumList;
import io.aesy.musicbrainz.entity.Recording;
import io.aesy.musicbrainz.entity.Release;
import io.aesy.musicbrainz.entity.ReleaseGroup;
import io.aesy.musicbrainz.entity.ReleaseList;
import io.aesy.musicbrainz.entity.TagList;
import io.aesy.musicbrainz.exception.MusicBrainzException;

public class MusicBrainzPanel extends JPanel implements ActionListener {
	/* Class variable */
	private static final long serialVersionUID = 1L;
	/* Instance variable */
	protected int mblimit;
	protected boolean update;
	protected String basedir;
	private GridBagLayout layout = new GridBagLayout();
	private JTextField searchField = new JTextField(new AlphaNumDocument(36),"",86);
	private JTextArea outArea = new JTextArea(14,79);
	private JScrollPane outPane = new JScrollPane(outArea);
	private JButton go = new JButton(new ImageIcon(MFTools.loadIcon("images/submit.png").getScaledInstance(18,18,Image.SCALE_DEFAULT )));
	//private JButton help = new JButton("Help");
	
	    
    /* Inner classes */
	
    private class LocalKeyAdapter extends KeyAdapter {
    	int ctrl=KeyEvent.CTRL_DOWN_MASK;
    	int alt=KeyEvent.ALT_DOWN_MASK;
    	int shift=KeyEvent.SHIFT_DOWN_MASK;
    	
    	public void keyPressed(KeyEvent e) {
    		if (e.getSource().equals(searchField)){
    			if ((e.getModifiersEx() & ctrl ) == ctrl) return;
    			if ((e.getModifiersEx() & alt ) == alt) return;
    			if ((e.getModifiersEx() & shift ) == shift) return;
    			switch(e.getKeyCode()) {
					case KeyEvent.VK_ENTER: findme(searchField.getText()); e.consume(); break;
					case KeyEvent.VK_F4: ; e.consume(); break;
					//default: System.out.println("Pressed: " + e.getExtendedKeyCode());
    			}
    		}
    	}
    }
    private LocalKeyAdapter keyListener = new LocalKeyAdapter();
    
    /* Constructor */
    public MusicBrainzPanel(){
    	setLayout(layout);
		setBorder(new TitledBorder("MusicBrainz"));
		configureControls();
		insertControls();
		mblimit = LibraryTagWindow.frame.setup.getLibraryTagLimit();
		update = LibraryTagWindow.frame.setup.getLibraryTagUpdate();
		basedir = LibraryTagWindow.frame.setup.getLibraryTagBasedir();
	}
    
    private void configureControls() {
    	searchField.addKeyListener(keyListener);
    	int size = searchField.getPreferredSize().height;
    	go.setPreferredSize( new Dimension(size+4,size+4));
    	go.addActionListener(this);
    	outArea.setForeground(Color.WHITE);
    	outArea.setBackground(Color.BLACK);
    	outArea.setEditable(false);
    	//help.setPreferredSize(new Dimension(104,26));
		//help.addActionListener(this);
	}
    
    private void insertControls() {
    	// Numbers are cells                                     x  y  xz yz s s  f   o
		addComponent(this,layout,new JLabel("MusicBrainz-MBID"), 0, 0, 1, 1, 1,0,'n','l');
		addComponent(this,layout,new JLabel("Output"),           0, 2, 1, 1, 1,0,'n','l');
		addComponent(this,layout,searchField,                    0, 1, 1, 1, 0,0,'b','l');
		addComponent(this,layout,outPane,                        0, 3, 2, 1, 0,0,'b','l');
		addComponent(this,layout,go,                             1, 1, 1, 1, 1,0,'n','r');
	}
    
    private void addComponent( Container cont, GridBagLayout gbl, Component c,
            int x, int y, int width, int height,
            double weightx, double weighty, int fill, int side ) {
    	GridBagConstraints gbc = new GridBagConstraints();
    	switch(fill) {
    		case 'n': gbc.fill = GridBagConstraints.NONE; break;
    		case 'b': gbc.fill = GridBagConstraints.BOTH; break;
    		case 'v': gbc.fill = GridBagConstraints.VERTICAL; break;
    		case 'h': gbc.fill = GridBagConstraints.HORIZONTAL; break;
    	}
    	switch(side) {
    		case 'l': gbc.anchor = GridBagConstraints.NORTHWEST; break;
    		case 'r': gbc.anchor = GridBagConstraints.NORTHEAST; break;
    	}
    	// we mustn't have insets for the panels
    	if (c.getClass().getName()!="javax.swing.JPanel")
    		gbc.insets=new Insets(5,5,5,5); // Make a little space between components				
    	gbc.gridx = x; gbc.gridy = y;
    	gbc.gridwidth = width;
    	gbc.gridheight = height;
    	gbc.weightx = weightx; gbc.weighty = weighty;
    	gbl.setConstraints( c, gbc );
    	cont.add( c );
    }
    
    /* ***************
     * GUI-Functions *
     *****************/
    
    protected void startPanel() {
		searchField.setEnabled(true);
		searchField.requestFocusInWindow();
	}
	
	private void clearPanel() {
		searchField.setText("");
	}
	
	/* ****************
     * File-Functions *
     ******************/
	
	public synchronized List<String> findArtist(String artistname, String title, String start, String extensionPattern) {
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
	    
	public synchronized List<String> getFileLocation( String artistname , String title) {
		return findArtist( artistname,title, basedir, "(.*.flac$)|(.*.mp3$)|(.*.ogg$)");
	}
		
	/* *****************
     * EVENT-Functions *
     *******************/
	private boolean countryPresent(List<Release> releases, String title, String c) {
		for( int i=0; i<releases.size(); i++ ) {
			if( releases.get(i).getTitle().equals(title) ) {
				String country = releases.get(i).getCountry();
				if( country==null ) country="";
				if (country.equals(c)) return true;
			}
		}
		return false;
	}
	
	private String getRelease( MusicBrainzClient client, Artist artist,
							String title, int trackcount, String preferredCountry, String preferredYear ) {
		String releaseID = null;
				
		List<Release> releaselist = client.release().withArtist(artist).limitBy(600).browse().get();
		System.out.println( releaselist.size() );
		
		// Look through releases if we got a release of
		/*if (countryPresent(releaselist, title, "XE")) preferredCountry="XE";
		else if (countryPresent(releaselist, title, "GB")) preferredCountry="GB";
		else if (countryPresent(releaselist, title, "US")) preferredCountry="US";
		else if (countryPresent(releaselist, title, "JP")) preferredCountry="JP";
		else if (countryPresent(releaselist, title, "DE")) preferredCountry="DE";
		else if (countryPresent(releaselist, title, ""))   preferredCountry="";
		else preferredCountry=null;*/
		
		for( int i=0; i<releaselist.size(); i++ ) {
			if( releaselist.get(i).getTitle().equals(title) ) {
				String country = releaselist.get(i).getCountry();
				String date = releaselist.get(i).getDate();
				if( country==null ) country="";
				if( date==null ) date="";
				
				System.out.println(i);
				System.out.println(releaselist.get(i).getTitle());
				System.out.println(releaselist.get(i).getDisambiguation());
				System.out.println(releaselist.get(i).getDate());
				System.out.println(releaselist.get(i).getCountry());
												
				if( country.equals(preferredCountry) )
				if( date.startsWith(preferredYear) )
				{
					title = releaselist.get(i).getTitle();
					releaseID = releaselist.get(i).getId();
					releaselist.get(i).getDate();
					
					System.out.println(title);
					System.out.println(releaselist.get(i).getCountry());
					System.out.println(releaselist.get(i).getDisambiguation());
					System.out.println(releaselist.get(i).getDate());
					System.out.println(releaselist.get(i).getStatus().getContent());
					
					// This is the wanted release
					if(client.recording().withReleaseId(UUID.fromString(releaseID)).browse().get().size()==trackcount) break;
				}	
			} /* if */
		}
		return releaseID;
	}
	
	private void mbstart() {
		//String id="8e66ea2b-b57b-47d9-8df0-df4630aeb8e5";
		//MusicBrainzResponse<Artist> response = client.artist().withId(UUID.fromString(id)).lookup();
		//System.out.println(response.getStatusCode());

		/*if (response instanceof MusicBrainzResponse.Failure) {
			String error = ((MusicBrainzResponse.Failure) response).getMessage();
			System.out.println(error);
		  } else if (response instanceof MusicBrainzResponse.Error) {
			MusicBrainzException error = ((MusicBrainzResponse.Error) response).getException();
			error.printStackTrace();
		  }*/
		
		MusicBrainzClient client = MusicBrainzJerseyClient.createWithDefaults();
		
		if(client!=null) {
			try {
				String artistID="c1d4f2ba-cf39-460c-9528-6b827d3417a1";	// Yes
								
				Artist artist = client.artist().withId(UUID.fromString(artistID)).lookup().get();
				String artistName = artist.getName();
				//System.out.println(artistName);
				// Get the preferred Release
				//String releaseID = getRelease(client, artist, "Fragile",11);
				String releaseID = getRelease(client, artist, "Fly From Here", 12, "JP", "2011");
				System.out.println(releaseID);
				
				if(releaseID!=null) {
					List<Recording> tracks = client.recording().withReleaseId(UUID.fromString(releaseID)).browse().get();
					for(int r=0; r<tracks.size(); r++) {
						String recordingTitle =tracks.get(r).getTitle();
						String recordingID = tracks.get(r).getId();
					
						System.out.println(recordingTitle);
						//System.out.println(recordingID);
						if(tracks.get(r).getDisambiguation()!=null) System.out.println(tracks.get(r).getDisambiguation());
						//System.out.println(tracks.get(r).getFirstReleaseDate());
						if(tracks.get(r).getIsrcList()!=null) System.out.println(tracks.get(r).getIsrcList());
					}
				}
				System.out.println("ready");
			}catch( NoSuchElementException e ) {
				StackTraceElement[] stacktrace = e.getStackTrace();
				int len = e.getStackTrace().length;
				for (int i = 0; i < len; i++)
					System.out.println(stacktrace[i].toString());
			}
		}
	}
	
	private String getMbidType(String mbid) {
		if(mbid.length()<36) return "False";
		if(mbid.chars().filter(ch -> ch =='-').count()<4) return "False";
		if(mbid.startsWith(" ")) return "False";
		MusicBrainzClient client = MusicBrainzJerseyClient.createWithDefaults();
		
		MusicBrainzResponse response = client.artist().withId(UUID.fromString(mbid)).lookup();
		if( response.getStatusCode()==200 ) return "Artist";
		
		response = client.releaseGroup().withId(UUID.fromString(mbid)).lookup();
		if( response.getStatusCode()==200 ) return "ReleaseGroup";
				
		response = client.release().withId(UUID.fromString(mbid)).lookup();
		if( response.getStatusCode()==200 ) return "Release";
				
		response = client.recording().withId(UUID.fromString(mbid)).lookup();
		if( response.getStatusCode()==200 ) return "Recording";
		
		System.out.println("Can't get MBID-Type");
		return "Unknown";
	}
	
	private void findme(String mbid) {
		String mbidtype = getMbidType(mbid);
		outArea.removeAll();
		outArea.append("This is a " + mbidtype + "-MBID\n");
		MusicBrainzClient client = MusicBrainzJerseyClient.createWithDefaults();
		
		try {
			switch (mbidtype) {
				case "Artist":
					//c1d4f2ba-cf39-460c-9528-6b827d3417a1 Yes
					//24202038-7b02-4444-96c2-cf2fc7b81308 Jon Anderson
					Artist artist = client.artist().withId(UUID.fromString(mbid)).lookup().get();
					outArea.append("Artist is: " + artist.getName() + "\n");
					List<ReleaseGroup> rGL = client.releaseGroup().withArtist(artist).limitBy(mblimit).browse().get();
					outArea.append("Artist has " + rGL.size() + " ReleaseGroups\n");
					outArea.append("Only Albums are listed:\n");
					for( int i=0; i<rGL.size(); i++ ) {
						if(rGL.get(i).getPrimaryType()!=null)
							if(rGL.get(i).getPrimaryType().getContent().equals("Album")) {
								String title = rGL.get(i).getTitle();
								if (title.length() > 38) title = title.substring(0, 38);
								outArea.append(title + ": " + rGL.get(i).getId() + "\n");
								if (update) {
									//System.out.println(basedir);
									//System.out.println(artist.getName());
									//System.out.println(artist.getType()); // Group/Person
									List<String> file = getFileLocation( artist.getName(), rGL.get(i).getTitle() );
									for(int l=0; l<file.size(); l++)
									System.out.println( file.get(l) );
								}
							}	
					}
					outArea.append("\n");
					System.out.println( "Ready." );
					clearPanel();
				break;
				case "ReleaseGroup":
					//b1176e7b-fa2e-3b28-959a-d8f55b5b6ccf
					ReleaseGroup rG = client.releaseGroup().withId(UUID.fromString(mbid)).lookup().get();
					outArea.append("ReleaseGroup is: " + rG.getTitle() + "\n");
					List<Release> releases = client.release().withReleaseGroup(rG).limitBy(mblimit).browse().get();
					outArea.append("ReleaseGroup has " + releases.size() + " Releases\n");
					for( int i=0; i<releases.size(); i++ ) {
						String title = releases.get(i).getTitle();
						String country = releases.get(i).getCountry();
						if(country==null) country="  "; // we format a little for outArea
						if (title.length() > 35) title = title.substring(0, 35);
						outArea.append(country + " " + title + ": " + releases.get(i).getId() + "\n");
					}
					outArea.append("\n");
					clearPanel();
				break;
				case "Release":
					//0fd838c4-6d48-4a39-8ef9-282d94c2de4c
					Release release = client.release().withId(UUID.fromString(mbid)).includeRecordings().lookup().get();
					outArea.append("Release is: " + release.getTitle() + "\n");
										
					List<Recording> recording = client.recording().withRelease(release).limitBy(mblimit).browse().get();
					outArea.append("Release has " + recording.size() + " Recordings\n");
					for( int i=0; i<recording.size(); i++ ) {
						String title = recording.get(i).getTitle();
						if (title.length() > 38) title = title.substring(0, 38);
						outArea.append(title + ": " + recording.get(i).getId() + "\n");
						System.out.println(recording.get(i).getIsrcList());
					}
					outArea.append("\n");
					clearPanel();
				break;
				case "Recording":
					//ac3f3784-3801-4785-b932-e20014522ed1
					//77b1e0a9-b859-4fa8-b946-baf585c5c084
					//3ec90fca-c135-4819-8e3a-98c443ef522f
					Recording rec = client.recording().withId(UUID.fromString(mbid)).includeReleases().lookup().get();
					outArea.append("Recording is: " + rec.getTitle() + "\n\n");
					System.out.println(rec.getIsrcList());
					clearPanel();
				break;	
			}
		} catch( NoSuchElementException e ) {
			StackTraceElement[] stacktrace = e.getStackTrace();
			int len = e.getStackTrace().length;
			for (int i = 0; i < len; i++)
				System.out.println(stacktrace[i].toString());
		}	
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(go)) {
			findme(searchField.getText());
			searchField.requestFocusInWindow();
		}
		//else if (e.getSource().equals(help)) System.out.println("Not implemented yet.");
		else System.out.println("Unknown event.");
	}
}
