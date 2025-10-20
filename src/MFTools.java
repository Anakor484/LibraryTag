/* MusicFrame, Accesses the MusicFrame Database.
 * Copyright (c) 2017,2025 Frank Ambacher. All Rights Reserved.
 * For wishes, questions mail to anakor@gmx.net.
 */

import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

public class MFTools {
	//enum myenum{Yes,No};
	//System.out.println(myenum.Yes.ordinal());
	/** Suppresses constructor. */
	private MFTools() {}
	
	// ToDo Works for DB access, but a hack
	public static boolean isNumber( Object str ){
		boolean isnumber = true;
		if( str == null ) return false;
		if( str.equals("") ) return false;
		try { Integer.parseInt((String)str); }
		catch(NumberFormatException e){ isnumber = false; }
		catch(ClassCastException e){ isnumber = true; }
		return isnumber;
	}
	
	/** Out mask c from str. All occurances will be masked.
	 * 
	 * @param str A string which contains a number e.g "345". An empty
	 *        string will be returned as null.
	 * @return The Number as an object or null on error.
	 * @Sample Object obj = numberstringToObject( "555" );
	 */
	public static Object numberstringToObject(String str){
		Object obj;
		try {
			obj=Integer.valueOf(str);
		} catch(NumberFormatException ex){obj=null;}
		return obj;
	}
	
	public static Object stringOrNull(String str){
		if (str.equals("")) str=null;
		return str;
	}
	
	/** Out mask c from str. All occurances will be masked.
	 * 
	 * @param str The string which will be altered.
	 * @param c   The char which will be masked.
	 * @return The altered string
	 * @Sample newdir = MwlTools.maskChar( "home\name", '\' );
	 */
	public static String maskChar( String str, char c ) {
		StringBuffer buf = new StringBuffer(str);
		String ch = String.valueOf(c);
		for ( int start = 0; start >= 0 ; start=start+2) {
			start = buf.indexOf(ch, start);
			if (start < 0) break;
			buf.insert(start, '\\');
		}
		return buf.toString();
	}
	
	/** Count occurances of c in str.
	 * 
	 * @param str The string which will be processed.
	 * @param c   The char which will be searched for.
	 * @return Count Occurances
	 * @Sample i = MwlTools.chCount( "www.mysite.net", '.' );
	 */
	public static int chCount(String str, char c) {
		int cnt=0;
		for(int i=0; i<str.length(); i++) {
			if(str.charAt(i)==c) cnt++;
		}
		return cnt;
	}
	
	/** Loads an Icon in a manner that it will be shown in Eclipse IDE and
	 * at execute time from a Jar Archive.
	 * @param file String representation if an image
	 * @return the loaded icon
	 * @Sample f.setIconImage( MwlTools.loadIcon("images/myicon.png") );
	 */
	 
	 public static Image loadIcon(String file) {
		 Image img = null;
		 try (InputStream is = Image.class.getResourceAsStream("/" + file)) {
			 if (is != null) img = ImageIO.read(is);
		 } catch (IOException e) {
			 e.printStackTrace();
		 }
		 // Fallback:
		 if (img == null) img = Toolkit.getDefaultToolkit().getImage(file);
		 return img;
	 }

	public static Image loadIconSimple(String file) {
		Image img=null;
		try{ img = ImageIO.read( Image.class.getResource( "/" + file )); }
		catch ( IOException ignored ) { }
		catch(IllegalArgumentException ignored){
			img=Toolkit.getDefaultToolkit().getImage(file);
		}
		return img;
	}
	
	public static int getJavaShortVersion(){
		String property   = System.getProperty("java.version");
		String version = null;
		
		if(property.startsWith("1.")) version = property.substring(2, 3);
		else {
		        int dot = property.indexOf(".");
		        if(dot != -1) { version = property.substring(0, dot); }
		        else version = property;
		}
		
		return Integer.parseInt(version);
	}
	
	public static void registerLF(String name, String c){
		//"Synthetica","de.javasoft.plaf.synthetica.SyntheticaLookAndFeel"
		try { UIManager.installLookAndFeel(name,c); }
		catch (Exception ignored) { }
	}
	
	public static String getNameFromURL(URL url) {
		return new File(url.getPath()).getName();
	}
	
	public static void setLF(String lf){
		boolean found=false;
		try {
			
			//UIManager.setLookAndFeel("de.javasoft.plaf.synthetica.SyntheticaLookAndFeel");
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				//System.out.println(info.getName());
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
}
