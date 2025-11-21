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
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultCaret;
import io.aesy.musicbrainz.client.MusicBrainzClient;
import io.aesy.musicbrainz.client.MusicBrainzJerseyClient;
import io.aesy.musicbrainz.client.MusicBrainzResponse;
import io.aesy.musicbrainz.entity.Artist;
import io.aesy.musicbrainz.entity.Recording;
import io.aesy.musicbrainz.entity.Release;

public class MusicBrainzPanel extends JPanel implements ActionListener {
	/* Class variable */
	private static final long serialVersionUID = 1L;
	/* Instance variable */
	protected int mblimit;
	protected boolean update;
	protected String basedir;
	protected String rw;
	private GridBagLayout layout = new GridBagLayout();
	protected JTextField searchField = new JTextField(new AlphaNumDocument(36),"",56);
	protected JProgressBar progress = new JProgressBar();
	protected JTextArea outArea = new JTextArea(24,78);
	private JScrollPane outPane = new JScrollPane(outArea,
			JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	private JButton go = new JButton(
			new ImageIcon(new IconLoader().loadIcon("forward.png").getScaledInstance(16,16,Image.SCALE_DEFAULT )));
	private JButton clear = new JButton(
			new ImageIcon(new IconLoader().loadIcon("clear.png").getScaledInstance(16,16,Image.SCALE_DEFAULT ))
			);
		    
    /* Inner classes */
	private class LocalKeyAdapter extends KeyAdapter {
    	public void keyPressed(KeyEvent e) {
    		if ((e.getModifiersEx() & (KeyEvent.CTRL_MASK | KeyEvent.ALT_MASK | KeyEvent.SHIFT_MASK)) != 0) return;
			if (e.getSource().equals(searchField)) {
    			switch(e.getKeyCode()) {
					case KeyEvent.VK_ENTER:
						try { findme(searchField.getText()); }
						catch(InterruptedException ei) {}
					break;
					//default: System.out.println("Pressed: " + e.getExtendedKeyCode());
    			}
    		}
    	}
    }
    private LocalKeyAdapter keyListener = new LocalKeyAdapter();
    
    
    /* ***************
     * EVENT-Methods *
     *****************/
    
    public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(go)) {
			try {
				findme(searchField.getText());
				searchField.requestFocusInWindow();
			}
			catch (InterruptedException ie) {}
		}
		else if (e.getSource().equals(clear)) outArea.setText("");
		else System.out.println("Unknown event.");
	}
    
    /* Constructor */
    public MusicBrainzPanel(){
    	setLayout(layout);
    	setBorder(new TitledBorder("MusicBrainz"));
    	configureControls();
		insertControls();
		
		mblimit = LibraryTagWindow.frame.setup.getLibraryTagLimit();
		update = LibraryTagWindow.frame.setup.getLibraryTagUpdate();
		basedir = LibraryTagWindow.frame.setup.getLibraryTagBasedir();
		rw = LibraryTagWindow.frame.setup.getLibraryTagRW();
		setFocus();
	}
    
    private void configureControls() {
    	searchField.addKeyListener(keyListener);
    	go.addActionListener(this);
    	outArea.setEditable(false);
    	outArea.setMargin(new Insets(2, 4, 2, 4));
    	
    	outPane.getViewport().setScrollMode(JViewport.BLIT_SCROLL_MODE);
    	progress.setStringPainted(true);
    	    	
    	// This needs JDK 5
    	DefaultCaret caret = (DefaultCaret)outArea.getCaret();
    	caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
    	// For JDK 1.4.2 use this instead
    	//outArea.append(...);
    	//outArea.setCaretPosition(outArea.getDocument().getLength());
    	clear.addActionListener(this);
    	clear.addKeyListener(keyListener);
	}
    
    private void insertControls() {
    	// Numbers are cells                                     x  y  xz yz s s  f   o
		addComponent(this,layout,new JLabel("MusicBrainz-MBID"), 0, 0, 1, 1, 0,0,'n','l');
		addComponent(this,layout,new JLabel("Output"),           0, 2, 1, 1, 0,0,'n','l');
		addComponent(this,layout,searchField,                    0, 1, 1, 1, 1,0,'b','l');
		addComponent(this,layout,progress,                       1, 1, 1, 1, 1,0,'b','l');
		addComponent(this,layout,outPane,                        0, 3, 3, 1, 1,1,'b','l');
		addComponent(this,layout,go,                             2, 1, 1, 1, 0,0,'n','r');
		addComponent(this,layout,clear,                          2, 4, 1, 1, 0,0,'n','r');
	}
    
    
    /* ***************
     * GUI-Functions *
     *****************/
    private void setFocus() {
    	SwingUtilities.invokeLater(() -> {
			searchField.setEnabled(true);
			searchField.requestFocusInWindow();
	    });
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
    
    
	
	/* *****************
     * EVENT-Functions *
     *******************/
	/*private boolean countryPresent(List<Release> releases, String title, String c) {
		for( int i=0; i<releases.size(); i++ ) {
			if( releases.get(i).getTitle().equals(title) ) {
				String country = releases.get(i).getCountry();
				if( country==null ) country="";
				if (country.equals(c)) return true;
			}
		}
		return false;
	}*/
		
	/*private void mbstart() {
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
		
		return "Unknown";
	}
	
	private void findme(String mbid) throws InterruptedException {
		String mbidtype = getMbidType(mbid);
		outArea.removeAll();
		outArea.append("This is a " + mbidtype + "-MBID\n");
		MusicBrainzClient client = MusicBrainzJerseyClient.createWithDefaults();
				
		try {
			switch (mbidtype) {
				case "Artist":
					//c1d4f2ba-cf39-460c-9528-6b827d3417a1 Yes
					//24202038-7b02-4444-96c2-cf2fc7b81308 Jon Anderson
					ArtistThread artistWorker = new ArtistThread(client, mbid);
					artistWorker.execute();
				break;
				case "ReleaseGroup":
					// b1176e7b-fa2e-3b28-959a-d8f55b5b6ccf Fragile
					// 94862bd6-6a24-3774-b136-860059c6376e
					// d85fd220-b4a7-3d98-baf1-7a53a5e5e255
					ReleaseGroupThread rGWorker = new ReleaseGroupThread(client, mbid);
					rGWorker.execute();
				break;
				case "Release":
					// 0fd838c4-6d48-4a39-8ef9-282d94c2de4c
					// 92ff841d-84ba-4651-abb6-3f76ff28b4dd
					// 04d7f5dd-e3eb-46dd-91ad-92754bb2c539
					ReleaseThread rWorker = new ReleaseThread(client, mbid);
					rWorker.execute();
				break;
				case "Recording":
					// ac3f3784-3801-4785-b932-e20014522ed1
					// 77b1e0a9-b859-4fa8-b946-baf585c5c084
					// 3ec90fca-c135-4819-8e3a-98c443ef522f
					// e69c188c-5cc6-421b-92ef-9c76a6068326
					RecordingThread recWorker = new RecordingThread(client, mbid);
					recWorker.execute();
				break;
				case "Unknown":  //Something went wrong
					System.out.println("Can't get MBID-Type");
				break;
			}
			searchField.setText("");
		} catch( NoSuchElementException e ) {
			StackTraceElement[] stacktrace = e.getStackTrace();
			int len = e.getStackTrace().length;
			for (int i = 0; i < len; i++)
				System.out.println(stacktrace[i].toString());
		}
	}
}
