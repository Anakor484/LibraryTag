/* LibraryTag
 * © 2025 Frank Ambacher. All Rights Reserved.
 * For wishes, questions mail to anakor@gmx.net.
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

public class LibraryTagWindow extends JFrame implements ActionListener, MenuListener {
	/* Class variable */
	private static final long serialVersionUID = 1L;
	public static LibraryTagWindow frame;
	/* Instance Variable */
	protected LibraryTagSetup setup = new LibraryTagSetup();
	protected StatusBar sb = new StatusBar(this);
	protected LibraryTagPanel panel = new LibraryTagPanel();
	private JMenu     m_file, m_help;
	private JMenuItem mi_MusicBrainz, mi_about, mi_license;
		
	/* Inner Classes */
		
	class LibraryTagWindowAdapter extends WindowAdapter {
		@Override public void windowClosing(WindowEvent e) {
			setup.setLibraryTagSize(getSize());
			setup.setLibraryTagLocation(getLocation());
			setup.setLibraryTagLimit( ((MusicBrainzPanel)panel.getPanel()).mblimit);
			setup.setLibraryTagUpdate( ((MusicBrainzPanel)panel.getPanel()).update);
			setup.setLibraryTagBasedir( ((MusicBrainzPanel)panel.getPanel()).basedir);
			setup.setLibraryTagRW( ((MusicBrainzPanel)panel.getPanel()).rw);
			setup.saveSetup();
		}
		@Override public void windowClosed(WindowEvent e) { System.exit(0); }
		@Override public void windowOpened(WindowEvent e) {
			// Load the setup
			setup.loadSetup();
			setSize(setup.getLibraryTagSize());
			setLocation(setup.getLibraryTagLocation());
			MFTools.setLF(setup.getLibraryTagLF());
			panel.addMusicBrainzPanel();
		}
	}
	
	/*class LocalKeyAdapter extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
			if (e.getSource().equals(m_file)){
				if (e.getModifiers()!=0) return; // Ctrl,Alt etc
				switch(e.getKeyCode()) {
					case KeyEvent.VK_RIGHT:
						if (m_file.isPopupMenuVisible()) m_file.setPopupMenuVisible(false);
						m_file.setSelected(false); m_edit.setSelected(true); break;
					case KeyEvent.VK_LEFT:
						if (m_file.isPopupMenuVisible()) m_file.setPopupMenuVisible(false);
						m_file.setSelected(false); m_help.setSelected(true);
						break;
				}
			}
			else if (e.getSource().equals(m_edit)) {
				if (e.getModifiers()!=0) return; // Ctrl,Alt etc
				switch(e.getKeyCode()) {
					case KeyEvent.VK_RIGHT:
						if (m_edit.isPopupMenuVisible()) m_edit.setPopupMenuVisible(false);
						m_edit.setSelected(false); m_view.setSelected(true);
					break;
					case KeyEvent.VK_LEFT:
						if (m_edit.isPopupMenuVisible()) m_edit.setPopupMenuVisible(false);
						m_edit.setSelected(false); m_file.setSelected(true);
					break;
				}
			}
			else if (e.getSource().equals(m_view)) {
				if (e.getModifiers()!=0) return; // Ctrl,Alt etc
				switch(e.getKeyCode()) {
					case KeyEvent.VK_RIGHT: m_view.setSelected(false); m_help.setSelected(true); break;
					case KeyEvent.VK_LEFT: m_view.setSelected(false); m_edit.setSelected(true); break;
				}
			}
			else if (e.getSource().equals(m_help)) {
				if (e.getModifiers()!=0) return; // Ctrl,Alt etc
				switch(e.getKeyCode()) {
					case KeyEvent.VK_RIGHT: m_help.setSelected(false); m_file.setSelected(true); break;
					case KeyEvent.VK_LEFT: m_help.setSelected(false); m_view.setSelected(true); break;
				}
			}
		}
	}
	public LocalKeyAdapter keyListener = new LocalKeyAdapter();*/
	
	public LibraryTagWindow() {
		super("LibraryTag");
		frame = this;
		// Init the Frame
		setIconImage(MFTools.loadIcon("images/LibraryTag.png"));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationByPlatform(true);
		setJMenuBar(createMenu());
		addWindowListener(new LibraryTagWindowAdapter());
		
		// Children
		add(sb, "South");
		add(panel, "Center");
		pack();
		setResizable(false);
		setVisible(true);
	}

	private JMenuBar createMenu() {
		final JMenuBar mb = new JMenuBar();
		final Vector<String> m_names = new Vector<String>();
		final Vector<String> mi_names = new Vector<String>();
		final int[] s = { 0,1 }; // start/menu
		final int[] c = { 1,3 };  // count/menu
		m_names.add(LibraryTag.bundle.getString("M_File"));
		m_names.add(LibraryTag.bundle.getString("M_Help"));
		mi_names.add("MusicBrainz");
		mi_names.add(LibraryTag.bundle.getString("MI_About"));
		mi_names.add(LibraryTag.bundle.getString("MI_License"));
		//mi_names.add(MusicFrame.bundle.getString("MI_Help"));
			
		for(int m=0; m<m_names.size(); m++) {
			JMenu menu;
			switch (m) {
				case 0:  m_file = mb.add(menu=new JMenu(m_names.get(m),true)); break;
				case 1:  m_help = mb.add(menu=new JMenu(m_names.get(m))); break;
				default: menu = null;
			}
		
			for(int mi=s[m]; mi<mi_names.size(); mi++ ) {
				switch(mi) {
					case 0: mi_MusicBrainz = menu.add(new JMenuItem(mi_names.get(mi), LibraryTag.bundle.getString("SC_OpenDB" ).charAt(0))); break;
					case 1: mi_about       = menu.add(new JMenuItem(mi_names.get(mi), LibraryTag.bundle.getString("SC_About"     ).charAt(0))); break;
					case 2: mi_license     = menu.add(new JMenuItem(mi_names.get(mi), LibraryTag.bundle.getString("SC_License"   ).charAt(0))); break;
					//case 12: mi_help    = menu.add(new JMenuItem(mi_names.get(mi), MusicFrame.bundle.getString("SC_Help"      ).charAt(0))); break;
				} /* switch */
				if(mi>=s[m]+c[m]-1) break;
			} /* for */ 
		} /* for */
	
		// EventListeners
		m_file.addMenuListener(this);
		m_help.addMenuListener(this);
		mi_MusicBrainz.addActionListener(this);
		mi_about.addActionListener(this);
		mi_license.addActionListener(this);
		//mi_help.addActionListener(this);
		
		/*
		// Tooltips
		m_file.setToolTipText(MusicFrame.bundle.getString("TT_File"));
		m_help.setToolTipText(MusicFrame.bundle.getString("TT_Help"));
		mi_open.setToolTipText(MusicFrame.bundle.getString("TT_OpenDB"));
		mi_about.setToolTipText(MusicFrame.bundle.getString("TT_About"));
		mi_sysinfo.setToolTipText(MusicFrame.bundle.getString("TT_SysInfo"));
		mi_help.setToolTipText(MusicFrame.bundle.getString("TT_Help"));
	
		// Mnemonics
		m_file.setMnemonic(MusicFrame.bundle.getString("SC_File").charAt(0));
		m_help.setMnemonic(MusicFrame.bundle.getString("SC_Help").charAt(0));
		// Key-Accelerators overrides mnemonic
		//setAccelerator( KeyStroke.getKeyStroke(KeyEvent.VK_F1, InputEvent.CTRL_MASK));
		mi_help.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
		mi_open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2,0));
		mi_close.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3,0));
		mi_tedit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4,0));*/
		return(mb);
	} /* createMenu */

	/* ***************
	 * Event handling 
	 *****************/

	@Override
	public void menuSelected(MenuEvent e) {
		((JMenu)e.getSource()).requestFocusInWindow();
		//((JMenu)e.getSource()).addKeyListener(keyListener);
	}

	@Override
	public void menuDeselected(MenuEvent e) {
		//((JMenu)e.getSource()).removeKeyListener(keyListener);
		getMostRecentFocusOwner().requestFocusInWindow();
	}

	@Override
	public void menuCanceled(MenuEvent e) {
		//((JMenu)e.getSource()).removeKeyListener(keyListener);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (!e.paramString().contains("Button1")) return;
		if (e.getSource()==mi_MusicBrainz) {
			//System.out.println("MusicBrainz pressed");
			String c = panel.getPanel().getClass().getName();
			if(c==null) return;
			else if(c=="MusicBrainzPanel") ((MusicBrainzPanel)panel.getPanel()).startPanel();
		}
		else if (e.getSource()==mi_about) new InfoDlg();
		else if (e.getSource()==mi_license) new LicenseDlg();
		//else if (e.getSource()==mi_help) new HelpDlg();
		else System.out.println(e.getSource());
	}

/*
	/*@Override
		public void keyPressed(KeyEvent e) {
			if (e.getSource().equals(m_file)) {
				if (e.getModifiersEx()!=0) return; // Ctrl,Alt etc
				switch(e.getKeyCode()) {
					case KeyEvent.VK_RIGHT: changeMenu(m_file,m_edit); break;
					case KeyEvent.VK_LEFT:  changeMenu(m_file,m_help); break;
					case KeyEvent.VK_DOWN:  changeMenuItem(m_file); break;
					case KeyEvent.VK_UP:    changeMenuItem(m_file); break;
				}
			}
			else if (e.getSource().equals(m_edit)) {
				if (e.getModifiersEx()!=0) return; // Ctrl,Alt etc
				switch(e.getKeyCode()) {
					case KeyEvent.VK_RIGHT: changeMenu(m_edit,m_view); break;
					case KeyEvent.VK_LEFT: changeMenu(m_edit,m_file); break;
				}
			}
			else if (e.getSource().equals(m_view)) {
				if (e.getModifiersEx()!=0) return; // Ctrl,Alt etc
				switch(e.getKeyCode()) {
					case KeyEvent.VK_RIGHT: changeMenu(m_view,m_help); break;
					case KeyEvent.VK_LEFT: changeMenu(m_view,m_edit); break;
				}
			}
			else if (e.getSource().equals(m_help)) {
				if (e.getModifiersEx()!=0) return; // Ctrl,Alt etc
				switch(e.getKeyCode()) {
					case KeyEvent.VK_RIGHT: changeMenu(m_help,m_file); break;
					case KeyEvent.VK_LEFT: changeMenu(m_help,m_view); break;
				}
			}
		}
	}
	public LocalKeyAdapter keyListener = new LocalKeyAdapter();*/
}
