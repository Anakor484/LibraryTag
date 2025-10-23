/* LibraryTag
 * © 2025 Frank Ambacher. All Rights Reserved.
 * For wishes, questions mail to anakor@gmx.net.
 */

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import io.aesy.musicbrainz.client.MusicBrainzClient;
import io.aesy.musicbrainz.client.MusicBrainzJerseyClient;
import io.aesy.musicbrainz.entity.Artist;
import io.aesy.musicbrainz.entity.ReleaseGroup;

public class OutSpinner extends SwingWorker<String, Character> {
	private final JTextArea outArea;
	private final char[] spinnerChars = new char[]{'|', '/', '-', '\\'};
	public OutSpinner(JTextArea outArea) {
		this.outArea=outArea;
		//((MusicBrainzPanel)LibraryTagWindow.frame.panel.currentPanel).outArea;
	}

    @Override
    protected String doInBackground() throws Exception {
        int i = 0;
        for (int k = 0; k < 50; k++) {
        	if (isCancelled()) return "Abbruch!";
            publish(spinnerChars[i++ % spinnerChars.length]); 
            Thread.sleep(100);
        }
        return "Unknown";
    }

    @Override
    protected void process(List<Character> chunks) {
        for (Character spinnerChar : chunks) {
            int currentLength = outArea.getDocument().getLength();
                        
            if (currentLength > 0) {
                char lastChar = outArea.getText().charAt(currentLength - 1);
                boolean isSpinnerChar = false;
                for (char sc : spinnerChars) {
                    if (lastChar == sc) {
                        isSpinnerChar = true;
                        break;
                    }
                }
                if (isSpinnerChar) outArea.replaceRange("", currentLength - 1, currentLength);
            }
            outArea.append(String.valueOf(spinnerChar));
        }
    }

    @Override
    protected void done() {
        try {
        	int currentLength = outArea.getDocument().getLength();
            if (currentLength > 0) {
                char lastChar = outArea.getText().charAt(currentLength - 1);
                for (char sc : spinnerChars) {
                    if (lastChar == sc) {
                        outArea.replaceRange("", currentLength - 1, currentLength); break;
                    }
                }
            }
            
            String result = get();
            if (isCancelled()) outArea.append("Canceled! \n");
        }
        catch (CancellationException e) {
        	int currentLength = outArea.getDocument().getLength();
        	outArea.replaceRange("", currentLength-1, currentLength);
        }
        catch (InterruptedException | ExecutionException e) {
        	outArea.append("Fehler: " + e.getMessage());
        }
    }
}
