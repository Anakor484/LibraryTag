/* LibraryTag
 * © 2025 Frank Ambacher. All Rights Reserved.
 * For wishes, questions mail to anakor@gmx.net.
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

public class HelpDlg extends JDialog implements WindowListener, ActionListener {
	/* Class Variables */
	private static final long serialVersionUID = 1L;
	
	/* Instance Variables */
	private ButtonBar bb = new ButtonBar();
	private JEditorPane htmlpane = null;
	private String[] history = new String[10];
	private int pos = 0;
	
	/* Inner Classes */
	protected class ButtonBar extends JPanel {
		JButton back = new JButton(
				new ImageIcon(new IconLoader().loadIcon("back.png").getScaledInstance(24,24,Image.SCALE_DEFAULT ))),
			home = new JButton(
				new ImageIcon(new IconLoader().loadIcon("home.png").getScaledInstance(24,24,Image.SCALE_DEFAULT ))),
			properties = new JButton(
				new ImageIcon(new IconLoader().loadIcon("properties.png").getScaledInstance(24,24,Image.SCALE_DEFAULT ))),
			done = new JButton(
				new ImageIcon(new IconLoader().loadIcon("ok.png").getScaledInstance(24,24,Image.SCALE_DEFAULT )));
		
		protected ButtonBar() {
			setLayout(new FlowLayout());
			add(back); add(home); add(properties); add(done);
		}
	}
	
	/* Events */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(bb.done)){ dispose(); }
		if (e.getSource().equals(bb.back)){ back(); }
		if (e.getSource().equals(bb.home)){ home(); }
		if (e.getSource().equals(bb.properties)){ properties(); }
	}
	
	/* Constructor */
	public HelpDlg(){
		super(LibraryTagWindow.frame, LibraryTag.bundle.getString("MI_Help"));
		setLayout(new BorderLayout());
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setMinimumSize(new Dimension(730,500));
		setMaximumSize(new Dimension(730,500));
		setPreferredSize(new Dimension(730,500));
		setIconImage(new IconLoader().loadIcon("LibraryTag.png"));
		setLocationByPlatform(true);
		htmlSetup();
		createBar();
		pack();
		addWindowListener(this);
		setVisible(true);
	}
	
	private void htmlSetup(){
		try { htmlpane = new JEditorPane((URL)getClass().getResource("/help/index.html")); }
		catch (IOException e) { return; }
		if( htmlpane==null ) return;
		htmlpane.setEditable(false);
		htmlpane.addHyperlinkListener(createHyperLinkListener());
		htmlpane.setContentType("text/html");
		final JScrollPane sc = new JScrollPane(); 
        sc.getViewport().add(htmlpane); 
        add(sc, "Center");
    }
	
	private HyperlinkListener createHyperLinkListener() {
		// Add the start page to history
		try{ history[pos] = new String(getClass().getResource("/help/index.html").toURI().toString());
		} catch(URISyntaxException e){ LibraryTagWindow.frame.sb.setStatus(2000); }
		return new HyperlinkListener() {
			public void back(){
				try {
					if(pos>0) pos=pos-1;
					try { htmlpane.setPage(new URI(history[pos ]).toURL()); }
					catch (URISyntaxException e) { e.printStackTrace(); }
				} catch (IOException ioe) { LibraryTagWindow.frame.sb.setStatus(2); } 
			}
			
            public void hyperlinkUpdate(HyperlinkEvent e) { 
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) { 
                    if (e instanceof HTMLFrameHyperlinkEvent) { 
                        ((HTMLDocument)htmlpane.getDocument()).processHTMLFrameHyperlinkEvent( 
                            (HTMLFrameHyperlinkEvent)e);
                    } else { 
                        try {
                        	// We're not on a browser with history, so null will be passed
                        	if(e.getURL()==null){ back(); }
                        	else if(e.getURL().toString().endsWith("history.back()")){ back(); }
                        	else {
                        		//If Home on index.html we do nothing
                        		if(pos==0) if(e.getURL().toString().equals(history[0])) return;
                        		// We're on a linked page
                           		if(e.getURL().toString().equals(history[0])) pos=0;//home
                        		else{ pos=pos+1; if(pos>=history.length) return; }                        		
                        		history[pos] = new String(e.getURL().toString());
                            	htmlpane.setPage(e.getURL());
                        	}
                        	//System.out.println(pos+" "+new URL(history[pos]));

                        } catch (IOException ioe) { LibraryTagWindow.frame.sb.setStatus(2000); } 
                    } 
                } 
            }
        }; 
    }
	
	private void home() {
	    try {
	        pos = 0;
	        htmlpane.setPage(getClass().getResource("/help/index.html"));
	    } catch (IOException e) {
	        LibraryTagWindow.frame.sb.setStatus(2000);
	    }
	}
	
	private void back() {
	    if (pos > 0) {
	        pos--;
	        try { htmlpane.setPage(new URI(history[pos]).toURL()); }
	        catch (Exception ex) { LibraryTagWindow.frame.sb.setStatus(2000); }
	    }
	}
	
	private void properties() {
	    try {
	        URL url = getClass().getResource("/help/properties.html");
	        if (url == null) {
	            LibraryTagWindow.frame.sb.setStatus(2000);
	            return;
	        }

	        pos++;
	        if (pos >= history.length) {
	            // Push Array to front (Ringbuffer)
	            System.arraycopy(history, 1, history, 0, history.length - 1);
	            pos = history.length - 1;
	        }
	        history[pos] = url.toString();
	        htmlpane.setPage(url);
	    } catch (IOException e) { LibraryTagWindow.frame.sb.setStatus(2000); }
	}
	
	private void navigateTo(URL url) {
		if (url == null) { LibraryTagWindow.frame.sb.setStatus(2000); return; }

	    String urlStr = url.toString();
	    // Wenn wir schon auf dieser Seite sind → nichts tun
	    if (pos >= 0 && history[pos] != null && history[pos].equals(urlStr)) return;
	    
	    pos++;
	    if (pos >= history.length) {
	        System.arraycopy(history, 1, history, 0, history.length - 1);
	        pos = history.length - 1;
	    }
	    history[pos] = urlStr;

	    try { htmlpane.setPage(url); }
	    catch (IOException e) { LibraryTagWindow.frame.sb.setStatus(2000); }
	}
	
	// Load aspecial file
	/*private void properties() {
	    navigateTo(getClass().getResource("/help/properties.html"));
	}*/
	
	private void createBar() {
		bb.back.addActionListener(this);
		bb.home.addActionListener(this);
		bb.properties.addActionListener(this);
		bb.done.addActionListener(this);
		add (bb,"South");
	}
	
	public void windowOpened(WindowEvent e) {
		setLocation(getOwner().getWidth()/2 + getOwner().getLocation().x-getWidth()/2,
				getOwner().getHeight()/2 + getOwner().getLocation().y-getHeight()/2);
	}
	public void windowClosing(WindowEvent e) {}
	public void windowClosed(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowActivated(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
}
