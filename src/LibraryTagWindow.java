/* LibraryTag
 * © 2025 Frank Ambacher. All Rights Reserved.
 * For wishes, questions mail to anakor@gmx.net.
 */

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;

public class LibraryTagWindow extends JFrame implements ActionListener {
	/* Class Variables */
	private static final long serialVersionUID = 1L;
	public static LibraryTagWindow frame;
	
	/* Instance Variables */
	protected LibraryTagSetup setup = new LibraryTagSetup();
	protected StatusBar sb = new StatusBar(this);
	protected LibraryTagPanel panel = new LibraryTagPanel();
	private JMenu     m_file, m_help;
	private JMenuItem mi_MusicBrainz, mi_about, mi_license, mi_help;
		
	/* Inner Classes */
	private class LibraryTagTheme extends DefaultMetalTheme {
		@Override public String getName() { return "LibraryTagTheme"; }
	    private final ColorUIResource primary1   = new ColorUIResource(new Color(0x1e3d59));
	    private final ColorUIResource primary2   = new ColorUIResource(new Color(0x4a7bb7)); // Menu background
	    private final ColorUIResource primary3   = new ColorUIResource(new Color(0xa3b8cb)); // ScrollBar border, StatusBar back
	    private final ColorUIResource secondary1 = new ColorUIResource(new Color(0x2c3e50)); // Component border
	    private final ColorUIResource secondary2 = new ColorUIResource(new Color(0x2c3e50)); // Panel border
	    private final ColorUIResource secondary3 = new ColorUIResource(new Color(0xa3b8cb));
	    
	    @Override protected ColorUIResource getPrimary1()   { return primary1; }
	    @Override protected ColorUIResource getPrimary2()   { return primary2; }
	    @Override protected ColorUIResource getPrimary3()   { return primary3; }
	    @Override protected ColorUIResource getSecondary1() { return secondary1; }
	    @Override protected ColorUIResource getSecondary2() { return secondary2; }
	    @Override protected ColorUIResource getSecondary3() { return secondary3; }
	    @Override public FontUIResource getControlTextFont() {
	        return new FontUIResource("Arial", Font.PLAIN, 13);
	    }
	    
	    public LibraryTagTheme() {
	    	super();
	    	sb.setBackground(primary3);
	    	UIManager.put("ProgressBar.background", Color.WHITE);
	    	UIManager.put("TextArea.background", Color.BLACK);
	    	UIManager.put("TextArea.foreground", Color.WHITE);
	    	UIManager.put("TextArea.font", new Font("Consolas", Font.PLAIN, 13));
	    	UIManager.put("TextArea.selectionBackground", Color.WHITE);
	    	UIManager.put("TextArea.selectionForeground", Color.BLACK);
	    	UIManager.put("TextArea.margin", new Insets(2, 4, 2, 4));
	    	UIManager.put("Button.background", Color.WHITE);
	    	UIManager.put("EditorPane.background", primary3);
	        UIManager.put("EditorPane.border", BorderFactory.createEmptyBorder());
	        UIManager.put("ScrollPane.background", primary3);
	        UIManager.put("ScrollPane.border", BorderFactory.createEmptyBorder());
	        
	        // Update all Windows
	        for (Window w : Window.getWindows()) {
	            SwingUtilities.updateComponentTreeUI(w);
	            w.pack();
	        }
	    }
	}
		
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
		@Override public void windowOpened(WindowEvent e) { panel.addMusicBrainzPanel(); }
	}
	
	/* ***************
	 * Event handling 
	 *****************/
	
	private void globalHotKeys() {
		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F1"), "help");
		getRootPane().getActionMap().put("help", new AbstractAction() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        if ((e.getModifiers() & (ActionEvent.CTRL_MASK
		        						 | ActionEvent.ALT_MASK
		        						 | ActionEvent.SHIFT_MASK)) == 0) {
		            new HelpDlg();
		        }
		    }
		});
	}	
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (!e.paramString().contains("Button1")) return;
		if (!(panel.currentPanel instanceof MusicBrainzPanel)) return;
		
		if (e.getSource()==mi_MusicBrainz) {
			// Which Panel is loaded?
			String c = panel.getPanel().getClass().getName();
			if(c==null) return;
			else if(c!="MusicBrainzPanel") panel.addMusicBrainzPanel();
		}
		else if (e.getSource()==mi_about) {
			new InfoDlg();
			if (panel.currentPanel instanceof MusicBrainzPanel) {
		        MusicBrainzPanel mbPanel = (MusicBrainzPanel) panel.currentPanel;
		        mbPanel.searchField.requestFocusInWindow();
		        mbPanel.searchField.selectAll();
		    }
		}
		else if (e.getSource()==mi_license) {
			new LicenseDlg();
			if (panel.currentPanel instanceof MusicBrainzPanel) {
		        MusicBrainzPanel mbPanel = (MusicBrainzPanel) panel.currentPanel;
		        mbPanel.searchField.requestFocusInWindow();
		        mbPanel.searchField.selectAll();
		    }
		}
		else if (e.getSource()==mi_help) {
			new HelpDlg();
			if (panel.currentPanel instanceof MusicBrainzPanel) {
		        MusicBrainzPanel mbPanel = (MusicBrainzPanel) panel.currentPanel;
		        mbPanel.searchField.requestFocusInWindow();
		        mbPanel.searchField.selectAll();
		    }
		}
		else System.out.println(e.getSource());
	}
	
	
	/* Constructor */
	
	public LibraryTagWindow() {
		super("LibraryTag");
		frame = this;
		
		// Load the setup
		setup.loadSetup();
		setLF(setup.getLibraryTagLF());
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationByPlatform(true);
		setSize(setup.getLibraryTagSize());
		setLocation(setup.getLibraryTagLocation());
		setJMenuBar( createMenu() );
		setIconImage( new IconLoader().loadIcon("LibraryTag.png") );
		addWindowListener(new LibraryTagWindowAdapter());
		
		// Children
		add(sb, "South");
		add(panel, "Center");
		pack();
		globalHotKeys();
		setVisible(true);
	}
	
	private void setLF(String lf){
		boolean found=false;
				
		if (lf.equals("Metal")) { MetalLookAndFeel.setCurrentTheme(new LibraryTagTheme()); }
        try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if (lf.equals(info.getName())) {
					found=true; UIManager.setLookAndFeel(info.getClassName()); break;
                }
			}
			
			if(!found) {
				if (lf.equals("Synthetica")) {
            		UIManager.setLookAndFeel("de.javasoft.plaf.synthetica.SyntheticaLookAndFeel");
            	}
				else if (lf.equals("SyntheticaSimple2D")) {
            		UIManager.setLookAndFeel("de.javasoft.plaf.synthetica.SyntheticaSimple2DLookAndFeel");
            	}
				else if (lf.equals("Synth")) {
            		UIManager.setLookAndFeel("javax.swing.plaf.synth.SynthLookAndFeel");
            	}
			}
        } catch (Exception ex) { System.err.println(ex); }
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
		mi_names.add(LibraryTag.bundle.getString("MI_Help"));
			
		for(int m=0; m<m_names.size(); m++) {
			JMenu menu;
			switch (m) {
				case 0:  m_file = mb.add(menu=new JMenu(m_names.get(m),true)); break;
				case 1:  m_help = mb.add(menu=new JMenu(m_names.get(m))); break;
				default: menu = null;
			}
		
			for(int mi=s[m]; mi<mi_names.size(); mi++ ) {
				switch(mi) {
					case 0:
						mi_MusicBrainz = menu.add(new JMenuItem(mi_names.get(mi), LibraryTag.bundle.getString("SC_OpenDB").charAt(0)));
						mi_MusicBrainz.setIcon(new ImageIcon(
							new IconLoader().loadIcon("mb.png").getScaledInstance(16,16,Image.SCALE_DEFAULT )));
					break;
					case 1:
						mi_about = menu.add(new JMenuItem(mi_names.get(mi), LibraryTag.bundle.getString("SC_About").charAt(0)));
						mi_about.setIcon(new ImageIcon(
							new IconLoader().loadIcon("about.png").getScaledInstance(16,16,Image.SCALE_DEFAULT )));
					break;
					case 2:
						mi_license = menu.add(new JMenuItem(mi_names.get(mi), LibraryTag.bundle.getString("SC_License").charAt(0)));
						mi_license.setIcon(new ImageIcon(
							new IconLoader().loadIcon("doc.png").getScaledInstance(16,16,Image.SCALE_DEFAULT )));
					break;
					case 3:
						mi_help	= menu.add(new JMenuItem(mi_names.get(mi), LibraryTag.bundle.getString("SC_Help").charAt(0)));
						mi_help.setIcon(new ImageIcon(
							new IconLoader().loadIcon("help.png").getScaledInstance(16,16,Image.SCALE_DEFAULT )));
					break;
				} /* switch */
				if(mi>=s[m]+c[m]-1) break;
			} /* for */ 
		} /* for */
	
		// EventListeners
		mi_MusicBrainz.addActionListener(this);
		mi_about.addActionListener(this);
		mi_license.addActionListener(this);
		mi_help.addActionListener(this);
		
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
}
