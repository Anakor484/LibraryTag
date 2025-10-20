/* MusicFrame, Accesses the MusicFrame Database.
 * Copyright (c) 2017,2025 Frank Ambacher. All Rights Reserved.
 * For wishes, questions mail to micon@gmx.net.
 */
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class AlphaNumDocument extends PlainDocument {
	private static final long serialVersionUID = 2L;
	private int limit;
	private List<Character> allow = new ArrayList<Character>();
	
	public AlphaNumDocument(int len){
		super();
		limit=len;
		// allowed special chars in strings
		allow.add('\''); allow.add('/'); allow.add('-'); allow.add(' '); 
		allow.add('(');  allow.add(')'); allow.add('#'); allow.add('&'); allow.add('?');
		allow.add(',');	 allow.add(';'); allow.add('!'); allow.add('.'); allow.add(':');
	}
	
	public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        if (str == null) return;
        if ((getLength() + str.length()) <= limit) {
        	char[] ch = str.toCharArray();
        	for (int i = 0; i < ch.length; i++) {
        		if (!allow.contains(ch[i])) {
        			if ((!Character.isAlphabetic(ch[i])) && (!Character.isDigit(ch[i]))) return;
        		}
        	}
        	super.insertString(offs, new String(ch), a);
        }	
    }
}
