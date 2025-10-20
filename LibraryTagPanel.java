/* LibraryTag
 * © 2025 Frank Ambacher. All Rights Reserved.
 * For wishes, questions mail to anakor@gmx.net.
 */

import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

public class LibraryTagPanel extends JPanel{
	private static final long serialVersionUID = 1L;
	private JPanel currentPanel;
		
	public LibraryTagPanel() {
		setOpaque(true);
		setLayout(new FlowLayout(FlowLayout.LEFT));
	}
	
	public JPanel getPanel() {
		return currentPanel;
	}
	
	private Dimension calcSize(){
		int h = 67+JMenuBar.HEIGHT + getPreferredSize().height + LibraryTagWindow.frame.sb.getPreferredSize().height;
		int w = 10+getPreferredSize().width + LibraryTagWindow.frame.getInsets().left + LibraryTagWindow.frame.getInsets().right;
		return new Dimension(w,h);
	}
	
	public void addMusicBrainzPanel() {
		if (currentPanel!=null) remove(currentPanel);
		currentPanel = new MusicBrainzPanel(); add(currentPanel);
		((MusicBrainzPanel)currentPanel).startPanel();
		setPreferredSize(currentPanel.getPreferredSize());
		LibraryTagWindow.frame.setSize(calcSize());
		validate();
	}
}
