/* LibraryTag
 * © 2025 Frank Ambacher. All Rights Reserved.
 * For wishes, questions mail to anakor@gmx.net.
 */

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.swing.JOptionPane;

public class LibraryTag {
	static ResourceBundle bundle;
		
	public static void main(String[] args) {
		try {
			bundle = ResourceBundle.getBundle( "locale/LibraryTag", Locale.getDefault(), Class.forName("LibraryTag").getClassLoader());
		} catch( MissingResourceException e ) {
			System.err.println( e );
		} catch( ClassNotFoundException e) {
			System.err.println( e );
		}
		
		int argi = 0;
        while (argi < args.length) {
            String arg = args[argi];
            if (!arg.startsWith("-")) break;
            for (int i=1; i<arg.length(); i++) {
                char c = arg.charAt(i);
                switch (c) {
                    case 'h' : /*usage();*/ break;
                    case 'i' : /*install();*/ break;
                    default : /*usage();*/
                }
            }
            argi++;
        }
        if(getJavaShortVersion()>=8) new LibraryTagWindow();
        else JOptionPane.showMessageDialog(null,
        		LibraryTag.bundle.getString("JVM_1") + "\n" + LibraryTag.bundle.getString("JVM_2"),
        		LibraryTag.bundle.getString("JVM_Title"), JOptionPane.WARNING_MESSAGE); 
    }
	
	private static int getJavaShortVersion(){
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
}
