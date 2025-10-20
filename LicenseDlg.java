/* ISRCGetter
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

public class LicenseDlg extends JDialog implements WindowListener{
	/* Class Variables */
	private static final long serialVersionUID = 1L;
	/* Instance Variables */
	private JEditorPane htmlpane = new JEditorPane();
		
	/* Inner Classes */
				
	/* Constructor */
	public LicenseDlg(){
		super(LibraryTagWindow.frame, "License", true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setMinimumSize(new java.awt.Dimension(520,300));
		setPreferredSize(new java.awt.Dimension(520,300));
		setIconImage(MFTools.loadIcon("images/LibraryTag.png"));
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
	
	private void htmlSetup(){
		htmlpane.setBackground(new Color(0xA3B8CB));
		htmlpane.setEditable(false);
		htmlpane.setCaret(null);
		htmlpane.setContentType("text/html");
		htmlpane.setText(
			"<html><head></head>"
			+ "<BODY style=\"background-image:url(../images/musicframe.png)\" bgcolor=#A3B8CB>"
			+ "<left>"
			+ "<h2 class=hilight> MIT License</h2>"
			+ "<h4>Copyright © 2025</h4>"
			+ "<h4>Permission is hereby granted, free of charge, to any person obtaining a copy<br>"
			+ "of this software and associated documentation files (the \"Software\"), to deal<br>"
			+ "in the Software without restriction, including without limitation the rights<br>"
			+ "to use, copy, modify, merge, publish, distribute, sublicense, and/or sell<br>"
			+ "copies of the Software, and to permit persons to whom the Software is<br>"
			+ "furnished to do so, subject to the following conditions:<br></h4>"
			+ "<h5>The above copyright notice and this permission notice shall be included in all<br>"
			+ "copies or substantial portions of the Software.<br>"
			+ "THE SOFTWARE IS PROVIDED 'AS IS', WITHOUT WARRANTY OF ANY KIND, EXPRESS OR<br>"
			+ "IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,<br>"
			+ "FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE<br>"
			+ "AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER<br>"
			+ "LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,<br>"
			+ "OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE "
			+ "SOFTWARE.<br></h5></left></body></html>");
		final JScrollPane sc = new JScrollPane(); 
        sc.getViewport().add(htmlpane); 
        add(sc, BorderLayout.CENTER);
    }
	
	/*
	private Component createButtons(ButtonBarBuilder2 builder) {
		final JButton b1 = new JButton(new ImageIcon(MFTools.loadIcon("images/eclipse.png")));
		final JButton b2 = new JButton(new ImageIcon(MFTools.loadIcon("images/icofx.png")));
		final JButton b3 = new JButton(new ImageIcon(MFTools.loadIcon("images/mysql.png")));
		final JButton b4 = new JButton(new ImageIcon(MFTools.loadIcon("images/7zip.png")));
		final JButton b5 = new JButton(new ImageIcon(MFTools.loadIcon("images/notepadpp.png")));
		final JLabel txt = new JLabel(ISRCGetter.bundle.getString("WeUseTheseTools"));
		txt.setFont(new Font(txt.getFont().getName(),txt.getFont().getStyle(),10));
		// The Images are at Size 32x32 so the buttons should too.
		Dimension dim = new Dimension(32,32);
		b1.setPreferredSize(dim);
		b2.setPreferredSize(dim);
		b3.setPreferredSize(dim);
		b4.setPreferredSize(dim);
		b5.setPreferredSize(dim);
		// ToolTips
		b1.setToolTipText("Eclipse IDE Luna");
		b2.setToolTipText("IcoFX IconEditor by Attila Kovrig");
		b3.setToolTipText("MySQL Community Database");
		b4.setToolTipText("7-Zip Archiver by Igor Pavlov");
		b5.setToolTipText("Notepad++ Editor");
		builder.addGlue();
		builder.addFixed( txt );
		builder.addRelatedGap();
		builder.addFixed( b1 );
		builder.addRelatedGap();
		builder.addFixed( b2 );
		builder.addRelatedGap();
		builder.addFixed( b3 );
		builder.addRelatedGap();
		builder.addFixed( b4 );
		builder.addRelatedGap();
		builder.addFixed( b5 );
		return builder.getPanel();
	}
	*/
	
	/*private void createBar() {
		FormLayout layout = new FormLayout("pref");
		DefaultFormBuilder rowBuilder = new DefaultFormBuilder(layout);
		rowBuilder.setDefaultDialogBorder();
		rowBuilder.append(createButtons(ButtonBarBuilder2.createLeftToRightBuilder()));
		add(rowBuilder.getPanel(), "South");
	}*/
	
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
