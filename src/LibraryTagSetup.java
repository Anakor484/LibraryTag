/* LibraryTag
 * © 2025 Frank Ambacher. All Rights Reserved.
 * For wishes, questions mail to anakor@gmx.net.
 */

import java.awt.Dimension;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class LibraryTagSetup extends Properties {
	/* Class-Variables */
	private static final long serialVersionUID = 2L;
	
	// getter
	public String getLibraryTagLF(){
		String lf = getProperty("LibraryTag.LF","none");
		if( lf.equals("none") ) { lf = setLibraryTagLF(); } // LookAndFeel not set, so do it
		return lf;
	}
	
	public int getLibraryTagHeight() { return Integer.valueOf(getProperty("LibraryTag.Height","300")).intValue(); }
	public int getLibraryTagWidth() { return Integer.valueOf(getProperty("LibraryTag.Width","600")).intValue(); }
	public Dimension getLibraryTagSize() { return new Dimension(getLibraryTagWidth(),getLibraryTagHeight()); }
	public int getLibraryTagX() { return Integer.valueOf(getProperty("LibraryTag.X","0")).intValue(); }
	public int getLibraryTagY() { return Integer.valueOf(getProperty("LibraryTag.Y","0")).intValue(); }
	public Point getLibraryTagLocation() { return new Point(getLibraryTagX(),getLibraryTagY()); }
	public int getLibraryTagLimit() { return Integer.valueOf(getProperty("MB.Limit","100")).intValue(); }
	public boolean getLibraryTagUpdate() { return Boolean.valueOf(getProperty("LibraryTag.Update","false")).booleanValue(); }
	public String getLibraryTagBasedir() { return String.valueOf( getProperty("LibraryTag.Basedir","/") ); }
	public String getLibraryTagRW() { return String.valueOf( getProperty("LibraryTag.RW","ro") ); }
	
	// setter
	public void setLibraryTagHeight(int h){ setProperty("LibraryTag.Height", String.valueOf(h).toString()); }
	public void setLibraryTagWidth(int w){ setProperty("LibraryTag.Width", String.valueOf(w).toString()); }
	public void setLibraryTagSize(Dimension dim){ setLibraryTagWidth(dim.width); setLibraryTagHeight(dim.height); }
	public void setLibraryTagX(int x){ setProperty("LibraryTag.X", String.valueOf(x).toString()); }
	public void setLibraryTagY(int y){ setProperty("LibraryTag.Y", String.valueOf(y).toString()); }
	public void setLibraryTagLocation(Point p){ setLibraryTagX(p.x);setLibraryTagY(p.y);}
	public void setLibraryTagLimit(int limit){ setProperty("MB.Limit", String.valueOf(limit).toString()); }
	public void setLibraryTagUpdate(boolean update){ setProperty("LibraryTag.Update", String.valueOf(update)); }
	public void setLibraryTagBasedir(String basedir){ setProperty("LibraryTag.Basedir", String.valueOf(basedir)); }
	public void setLibraryTagRW(String rw){ setProperty("LibraryTag.RW", String.valueOf(rw)); }
	
	// This Method is only executed, if L&F is set to 'none' in properties file
	private String setLibraryTagLF(){
		String lf;
		String os = System.getenv("OS");
		if(os==null) os = System.getProperty("os.name");
		else if(os.equals("")) os = System.getProperty("os.name");
		if(os.equals("Windows_NT")) lf = "Windows"; 
	    else if(os.equals("Windows")) lf = "Windows Classic";
	    else if(os.toLowerCase().indexOf("inux") >= 0) lf = "Nimbus";
	    else if(os.toLowerCase().indexOf("mac") >= 0) lf = "Nimbus";
	    else if(os.toLowerCase().indexOf("unos") >= 0) lf = "CDE/Motif";
	    else if(os.toLowerCase().indexOf("nix") >= 0) lf = "CDE/Motif";
	    else if(os.toLowerCase().indexOf("aix") >= 0) lf = "CDE/Motif";
	    else lf = "Metal";
	    setProperty("LibraryTag.LF", lf);
		return lf;
	}
	
	public void saveSetup(){
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("LibraryTag.prp", false));
			store(bw,"LibraryTag - Properties File");
			bw.close();
		} catch(IOException ex){ LibraryTagWindow.frame.sb.setStatus(2); }
	}
	
	public void loadSetup(){
		try {
			BufferedReader br = new BufferedReader(new FileReader("LibraryTag.prp"));
			load(br);
			br.close();
		} catch(IOException ex){ LibraryTagWindow.frame.sb.setStatus(2); }
	}
}
