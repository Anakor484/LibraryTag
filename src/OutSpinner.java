/* LibraryTag
 * © 2025 Frank Ambacher. All Rights Reserved.
 * For wishes, questions mail to anakor@gmx.net.
 */

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import io.aesy.musicbrainz.client.MusicBrainzClient;
import io.aesy.musicbrainz.client.MusicBrainzJerseyClient;
import io.aesy.musicbrainz.entity.Artist;
import io.aesy.musicbrainz.entity.ReleaseGroup;

public class OutSpinner extends SwingWorker<String, Character> {
	private final JTextArea outArea;
	private final char[] spinnerChars = new char[]{'|', '/', '-', '\\'};
	
	public OutSpinner(JTextArea outArea) {
		this.outArea = outArea;
	}

    @Override
    protected String doInBackground() throws Exception {
        int i = 0;
        while (!isCancelled()) {
            publish(spinnerChars[i++ % spinnerChars.length]);
            Thread.sleep(100);
        }
        return null;
    }

    @Override
    protected void process(List<Character> chunks) {
    	//synchronized (outArea) {
    	//SwingUtilities.invokeLater(() -> {
    		int currentLength = outArea.getDocument().getLength();
    		if (currentLength > 0) {
                String text = outArea.getText();
                while (currentLength > 0) {
                    char lastChar = text.charAt(currentLength - 1);
                    boolean isSpinnerChar = false;
                    for (char sc : spinnerChars) {
                        if (lastChar == sc) {
                            isSpinnerChar = true;
                            break;
                        }
                    }
                    if (!isSpinnerChar) break;
                    outArea.replaceRange("", currentLength - 1, currentLength);
                    currentLength--;
                    text = outArea.getText();
                }
            }
            outArea.append(String.valueOf(chunks.get(chunks.size() - 1)));
        //});
    	//}
    }

    @Override
    protected void done() {
    	synchronized (outArea) {
    	SwingUtilities.invokeLater(() -> {
    		int currentLength = outArea.getDocument().getLength();
    		if (currentLength > 0) {
    			String text = outArea.getText();
                while (currentLength > 0) {
                    char lastChar = text.charAt(currentLength - 1);
                    boolean isSpinnerChar = false;
                    for (char sc : spinnerChars) {
                        if (lastChar == sc) {
                            isSpinnerChar = true;
                            break;
                        }
                    }
                    if (!isSpinnerChar) break;
                    outArea.replaceRange("", currentLength - 1, currentLength);
                    currentLength--;
                    text = outArea.getText();
                }
            }
    		if (isCancelled()) {
    			outArea.append("Canceled!\n");
    		}    
    	});
    	}
    }
}
