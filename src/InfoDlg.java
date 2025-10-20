/* LibraryTag
 * © 2025 Frank Ambacher. All Rights Reserved.
 * For wishes, questions mail to anakor@gmx.net.
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import javax.swing.Box;
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

public class InfoDlg extends JDialog implements WindowListener{
	/* Class Variables */
	private static final long serialVersionUID = 1L;
	/* Instance Variables */
	private JEditorPane htmlpane = new JEditorPane();
	private ButtonBar bb = new ButtonBar();
	
	/* Inner Classes */
	@SuppressWarnings("serial")
	class ButtonBar extends JPanel {
		/*private Box box = Box.createHorizontalBox();
		private final JButton b1 = new JButton(new ImageIcon(MFTools.loadIcon("images" + File.separatorChar + "eclipse.png")
									.getScaledInstance(32,32,Image.SCALE_DEFAULT )));
		private final JButton b2 = new JButton(new ImageIcon(MFTools.loadIcon("images" + File.separatorChar + "icofx.png")
				 					.getScaledInstance(32,32,Image.SCALE_DEFAULT )));
		private final JButton b3 = new JButton(new ImageIcon(MFTools.loadIcon("images" + File.separatorChar + "mysql.png")
				 					.getScaledInstance(32,32,Image.SCALE_DEFAULT )));
		private final JButton b4 = new JButton(new ImageIcon(MFTools.loadIcon("images" + File.separatorChar + "7zip.png")
				 					.getScaledInstance(32,32,Image.SCALE_DEFAULT )));
		private final JButton b5 = new JButton(new ImageIcon(MFTools.loadIcon("images" + File.separatorChar + "notepadpp.png")
				 					.getScaledInstance(32,32,Image.SCALE_DEFAULT )));*/
		
		public ButtonBar() {
			setLayout(new FlowLayout());
			Dimension dim = new Dimension(32,32);
			/*b1.setPreferredSize(dim);
			b2.setPreferredSize(dim);
			b3.setPreferredSize(dim);
			b4.setPreferredSize(dim);
			b5.setPreferredSize(dim);
			box.add(b1); box.add(Box.createHorizontalStrut(5));
			box.add(b2); box.add(Box.createHorizontalStrut(5));
			box.add(b3); box.add(Box.createHorizontalStrut(5));
			box.add(b4); box.add(Box.createHorizontalStrut(5));
			box.add(b5); box.add(Box.createHorizontalStrut(5));
			add(box);*/
		}
	}
			
	/* Constructor */
	public InfoDlg(){
		super(LibraryTagWindow.frame, "Egobox", true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setMinimumSize(new java.awt.Dimension(332,267));
		setPreferredSize(new java.awt.Dimension(332,267));
		setIconImage(MFTools.loadIcon("images/LibraryTag.png"));
		setResizable(false);
		htmlSetup();
		add(bb, BorderLayout.SOUTH);
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
	
	private void htmlSetup(){
		htmlpane.setBackground(new Color(0xA3B8CB));
		htmlpane.setEditable(false);
		htmlpane.setCaret(null);
		htmlpane.setContentType("text/html");
		htmlpane.setText(
			"<html><head></head>"
			+ "<BODY style=\"background-image:url(../images/mwl.png)\" bgcolor=#A3B8CB>"
			+ "<center><h2 class=hilight>" + "LibraryTag"
			+ "</h2><h4>Version: 0.02</h4>"
			+ "<h5>(c) 2025 Frank Ambacher</h5>"
			+ "<h5>Included Libraries:<br>"
			+ "MusicBrainz-API-Client by Isac Wertwein, JAudioTagger by Paul Taylor</h5></center></body></html>");
		final JScrollPane sc = new JScrollPane(); 
        sc.getViewport().add(htmlpane); 
        add(sc, BorderLayout.CENTER);
    }
	
	@Override
	public void windowOpened(WindowEvent e) {
		setLocation(getOwner().getWidth()/2 + getOwner().getLocation().x-getWidth()/2,
				getOwner().getHeight()/2 + getOwner().getLocation().y-getHeight()/2);
	}
	@Override
	public void windowClosing(WindowEvent e) {}
	@Override
	public void windowClosed(WindowEvent e) {}
	@Override
	public void windowIconified(WindowEvent e) {}
	@Override
	public void windowDeiconified(WindowEvent e) {}
	@Override
	public void windowActivated(WindowEvent e) {}
	@Override
	public void windowDeactivated(WindowEvent e) {}
}
