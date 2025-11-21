/* LibraryTag
 * © 2025 Frank Ambacher. All Rights Reserved.
 * For wishes, questions mail to anakor@gmx.net.
 */

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class LibraryTagPanel extends JPanel {
	/* Class Variables */
	private static final long serialVersionUID = 1L;
	
	/* Instance Variables */
	protected JPanel currentPanel;
	
	/* Constructors */
	public LibraryTagPanel() {
		setLayout(new BorderLayout());
	}
	
	/* Methods */
	public JPanel getPanel() { return currentPanel; }
	
	public void addMusicBrainzPanel() {
		if (currentPanel!=null) remove(currentPanel);
		
		currentPanel = new MusicBrainzPanel();
		add(currentPanel); //((MusicBrainzPanel)currentPanel).startPanel();
		LibraryTagWindow.frame.setSize(LibraryTagWindow.frame.getPreferredSize());
		
	}
}
