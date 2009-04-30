import transport.Network;
import transport.Receiver;
import transport.TimeoutAction;

/**
 * This class provides a basic Go back N Receiver
 * @author Nicos Giuliani
 * @version 1.0
 */
public class GBNReceiver implements Receiver, TimeoutAction {

	private final static int RECEIVER_TIMEOUT = 8000;
	private int expSeqNum;

	public GBNReceiver() {}

	public final void unreliableReceive(final byte[] buffer, final int offset, final int length) {
	
		
		if(SeqNum.getSeqNum(buffer) == expSeqNum) {
			
			System.out.println("Received valid packet n. " + expSeqNum);

			Network.reliableReceive(buffer, offset + (int) buffer[0] + 1, length - ((int) buffer[0] + 1));
			expSeqNum++;
			
			// Send ACK
			Network.unreliableSend(SeqNum.toByte(expSeqNum), 0, SeqNum.toByte(expSeqNum).length);			
			Network.setTimeout(RECEIVER_TIMEOUT, this);
			
			
		} else {
			System.out.println("Received invalid packet, n. " + SeqNum.getSeqNum(buffer)+" instead of " + expSeqNum);
			Network.unreliableSend(SeqNum.toByte(expSeqNum), 0, SeqNum.toByte(expSeqNum).length);
		}

	}
	
	public final void timeoutExpired() {
		
		System.out.println("Receiver timeout! Disconnection...");
		
		try {
		
			Network.cancelTimeout(this);
			Network.disconnect();
			Network.allowClose();
		
		} catch (InterruptedException e) {
		
			Network.setTimeout(RECEIVER_TIMEOUT, this);
			e.printStackTrace();
		
		}
	}	
}