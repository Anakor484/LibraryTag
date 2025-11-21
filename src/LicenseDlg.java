/* LibraryTag
 * © 2025 Frank Ambacher. All Rights Reserved.
 * For wishes, questions mail to anakor@gmx.net.
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

public class LicenseDlg extends JDialog implements WindowListener{
	/* Class Variables */
	private static final long serialVersionUID = 1L;
	/* Instance Variables */
	private JEditorPane htmlpane = new JEditorPane();
	private ButtonBar bb = new ButtonBar();
		
	/* Inner Classes */
	@SuppressWarnings("serial")
	class ButtonBar extends JPanel {
		public ButtonBar() {
			setLayout(new FlowLayout());
			Dimension dim = new Dimension(32,32);
		}
	}
				
	/* Constructor */
	public LicenseDlg(){
		super(LibraryTagWindow.frame, "License", true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setMaximumSize(new java.awt.Dimension(560,420));
		setMinimumSize(new java.awt.Dimension(560,420));
		setPreferredSize(new java.awt.Dimension(560,420));
		setIconImage(new IconLoader().loadIcon("LibraryTag.png"));
		setResizable(false);
		htmlSetup();
		pack();
		addWindowListener(this);
		setVisible(true);
	}
	
	public HyperlinkListener createHyperLinkListener() { 
        return new HyperlinkListener() { 
            @Override
			public void hyperlinkUpdate(HyperlinkEvent e) { 
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) { 
                    if (e instanceof HTMLFrameHyperlinkEvent) { 
                        ((HTMLDocument)htmlpane.getDocument()).processHTMLFrameHyperlinkEvent( 
                            (HTMLFrameHyperlinkEvent)e); 
                    } else { 
                        try { 
                            htmlpane.setPage(e.getURL()); 
                        } catch (IOException ioe) { LibraryTagWindow.frame.sb.setStatus(2000); } 
                    } 
                } 
            }
        }; 
    }
	
	private void htmlSetup() {
	    htmlpane.setBackground(new Color(0xA3B8CB));
	    htmlpane.setEditable(false);
	    htmlpane.setCaret(null);
	    htmlpane.setContentType("text/html");
	    htmlpane.setText(getHtmlContent());

	    JScrollPane scrollPane = new JScrollPane(htmlpane);
	    scrollPane.setBorder(null);
	    add(scrollPane, BorderLayout.CENTER);

	    htmlpane.addComponentListener(new java.awt.event.ComponentAdapter() {
	        @Override
	        public void componentResized(java.awt.event.ComponentEvent e) {
	            setDialogSize();
	            htmlpane.removeComponentListener(this);
	        }
	    });
	}
	
	private String getHtmlContent() {
		return "<html><head><style>"
			+ "body { font-family: Arial; margin: 5px; text-align: left; }"
	        + "h1 { font-size: 14px; margin: 16px 8px; }"
	        + "h2 { font-size: 12px; margin: 14px 7px; color: #1e3d59; }"
	        + "h3 { font-size: 10px; margin: 12px 6px; color: #1e3d59; }"
	        + "h4 { font-size: 8px; margin:  10px 5px; color: #2c3e50; }"
	        + "h5 { font-size: 7px; margin:   9px 5px; color: #2c3e50; }"
	        + "h6 { font-size: 6px; margin:   8px 4px; }"
	        + "</style></head>"
	        + "<body bgcolor=#A3B8CB>"
	        + "<h2>MIT License</h2>"
			+ "<h4>Copyright © 2025"
			+ "Permission is hereby granted, free of charge, to any person obtaining a<br>"
			+ "copy of this software and associated documentation files (the \"Software\"), to<br>"
			+ "deal in the Software without restriction, including without limitation the rights<br>"
			+ "to use, copy, modify, merge, publish, distribute, sublicense, and/or sell<br>"
			+ "copies of the Software, and to permit persons to whom the Software is<br>"
			+ "furnished to do so, subject to the following conditions:</h4>"
			+ "<h5>The above copyright notice and this permission notice shall be included in all<br>"
			+ "copies or substantial portions of the Software.<br><br>"
			+ "THE SOFTWARE IS PROVIDED 'AS IS', WITHOUT WARRANTY OF ANY KIND, EXPRESS OR<br>"
			+ "IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,<br>"
			+ "FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE<br>"
			+ "AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER<br>"
			+ "LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,<br>"
			+ "OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE "
			+ "SOFTWARE.</h5></body></html>";
	}
	
	private void setDialogSize() {
	    SwingUtilities.invokeLater(() -> {
	        Dimension pref = htmlpane.getPreferredSize();
	        int newHeight = pref.height + bb.getPreferredSize().height;
	        int newWidth = Math.max(560, pref.width + 20);

	        newHeight = Math.min(newHeight, 450);
	        newWidth = Math.min(newWidth, 600);

	        setSize(newWidth, newHeight);
	        validate();
	        centerOnParent();
	    });
	}
	
	private void centerOnParent() {
	    Window parent = getOwner();
	    if (parent != null) {
	        int x = parent.getX() + (parent.getWidth() - getWidth()) / 2;
	        int y = parent.getY() + (parent.getHeight() - getHeight()) / 2;
	        setLocation(x, y);
	    }
	}
	
	@Override
	public void windowOpened(WindowEvent e) {
		System.setProperty("sun.java2d.uiScale", "0.8");
		System.setProperty("awt.useSystemAAFontSettings", "on");
	    System.setProperty("swing.aatext", "true");
		centerOnParent();
	}
	@Override
	public void windowClosing(WindowEvent e) {}
	@Override
	public void windowClosed(WindowEvent e) {
		System.setProperty("sun.java2d.uiScale", "1.0");
		System.setProperty("awt.useSystemAAFontSettings", "off");
	    System.setProperty("swing.aatext", "false");
	}
	@Override
	public void windowIconified(WindowEvent e) {}
	@Override
	public void windowDeiconified(WindowEvent e) {}
	@Override
	public void windowActivated(WindowEvent e) {}
	@Override
	public void windowDeactivated(WindowEvent e) {}
}
