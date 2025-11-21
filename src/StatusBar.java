/* LibraryTag
 * Copyright (c) 2017 Frank Ambacher. All Rights Reserved.
 * For wishes, questions mail to anakor@gmx.net.
 */

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.plaf.ColorUIResource;

public class StatusBar extends JLabel implements ActionListener {
	//Msg
    //   0 StatusBar default behaviour
	//  99 Clear StatusBar
	//1000-1999 Messages
	String MSG1000= "This is a Message"; 
	//1001-1099 Application Messages
	String MSG1001 = "Program can't be executed, we need to have Java 6 as minimum requirement.";
	String MSG1002 = "Please update your Java Runtime Environment(JRE).";
	String MSG1003 = "String not found.";
	String MSG1004 = "String too short.";
	String MSG1005 = "String not valid.";
	String MSG1006 = "records found";
	String MSG1007 = "Ready.";
	//1101-1199 File I/O Messages
	String MSG1101 = "File opened";
	String MSG1102 = "File closed";
	//1200-1299 SQL/DB-Messages
	//String MSG1201 = MusicFrame.bundle.getString("ST_DBOpened");//"Connection opened";
	//String MSG1202 = MusicFrame.bundle.getString("ST_DBClosed");//"Connection closed";
	String MSG1203 = "Data submited";
	String MSG1204 = "Duplicate Data";
	String MSG1205 = "Record not found";
	//1800-1899 OS-Messages
	//1900-1999 Hardware Messages
	//2000-2999 Errors
	String ERR2000 = "Error occured";
	//2001-2099 Application Errors
	String ERR2001 = "Application Error";
	String ERR2002 = "Unkown Datatype";
	String ERR2003 = "No valid Link";
	String ERR2004 = "Plausibility-Check failed";
	String ERR2005 = "Forms-1.3.0.jar is not installed!";
	String ERR2006 = "Java version mismatch";
	//2100-2199 File I/O Errors
	String ERR2100 = "File-I/O Error";
	String ERR2101 = "File Not Found";
	//2200-2299 SQL/DB-Errors
	String ERR2200= "SQL/DB Error";
	String ERR2201= "Can't connect to this database";
	String ERR2202= "Can't find the driver classes";
	//2800-2899 OS-Errors
	//2900-2999 Hardware Errors
	//3000-3999 Warnings
	String WRN3000 = "Warning occured";
	//3001-3099 Application Warning
	String WRN3001 = "Whithout tracks the medium possibly won't shown in views.";
	//3101-3199 File I/O Errors
	//3200-3299 SQL/DB-Errors
	//3800-3899 OS-Errors
	//3900-3999 Hardware Errors
		
	private static final long serialVersionUID = 1L;
	private Timer timer = new Timer(3000, this);
	
	/* Constructor */
	public StatusBar(JFrame f){
		setOpaque(true);
		setForeground(new ColorUIResource(Color.RED));
		setBorder(new BevelBorder(BevelBorder.LOWERED));
		setStatus(99);
	}
	
	/* Methods */
	private void setStatus(String text) {
		setText(text);
		if(timer.isRunning()) timer.restart(); else timer.start();
	}
	
	protected void setStatus(String text, int st) {
		switch(st){
			case 1006: setStatus(text + " " + MSG1006); break;
		}
	}
	
	protected void setStatus(int st) {
		switch(st){
			/*case 0:
				if (frame.table.getSelectedRow()>-1)
					setStatus(MusicFrame.bundle.getString("Row")+ ": " +  new Integer(frame.table.getSelectedRow())
						+ " " + MusicFrame.bundle.getString("Column")+ ": " + new Integer(frame.table.getSelectedColumn()));
			break;*/
			
			case 99: // clears the statusbar
				setStatus(" ");
			break;
			case 1003: setStatus(MSG1003); break;
			case 1004: setStatus(MSG1004); break;
			case 1005: setStatus(MSG1005); break;
			case 1007: setStatus(MSG1007); break;
			//case 1201: setStatus(MSG1201); break;
			//case 1202: setStatus(MSG1202); break;
			case 1203: setStatus(MSG1203); break;
			case 1204: setStatus(MSG1204); break;
			case 1205: setStatus(MSG1205); break;
			case 2000: setStatus(ERR2000); break;
			case 2004: setStatus(ERR2004); break;
			case 2201: setStatus(ERR2201); break;
			case 2202: setStatus(ERR2202); break;
			case 3001: setStatus(WRN3001); break;
			default: System.out.println("Unknown Error");
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(timer)) { timer.stop(); setText(" ");}	
	}
}
