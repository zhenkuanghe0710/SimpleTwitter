import edu.rit.ds.Lease;
import edu.rit.ds.RemoteEventListener;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;

/**
 * Interface MicroblogRef specifies the Java RMI remote interface for a distributed
 * microblog object in the P2Patter system.
 */
public interface MicroblogRef 
	extends Remote
	{
	/**
	 * Add the message with the given message text. This method is called by the
	 * AddMessage client program to add the given message to specify microblog.
	 *
	 * @param	text  Message text.
	 *
	 * @return	The complete message content includes: 
         *		the first line consists of 80 hyphen characters;
	 *          	the second line consists of the name of microblog owner, 
	 *	    	the number of message, the date/time of that message;
	 *	    	the third line consists of the message <TT>text</TT>.
	 *
	 * @exception  	RemoteException
	 *     	       	Thrown if a remote error occurred.
	 */
	public String AddMessage
		(String text)
		throws RemoteException;
	
	/**
	 * Remove the message with the given message text. This method is called by 
	 * the RemoveMessage client program to remove the given message from specify 		 
	 * microblog.
	 *
	 * @param	num  Message serial number.
	 *
	 * @return	String  The complete message content includes: 
         *          		the first line consists of 80 hyphen characters;
	 *          		the second line consists of the name of microblog owner, 
	 *	    		the <TT>num</TT> of message, the date/time of that message;
	 *	    		the third line consists of the message text.
	 *
	 * @exception  	RemoteException
	 *     	    	Thrown if a remote error occurred.
	 */
	public String RemoveMessage
		(int num)
		throws RemoteException;
	
	/**
	 * Add the given remote event listener to this microblog. Whenever a message
	 * is added to this microblog, this microblog will report a MicroblogEvent to 
	 * the given listener.
	 *
	 * @param  	listener  Remote event listener.
	 *
	 * @exception  	RemoteException
	 *             	Thrown if a remote error occurred.
	 */
	public Lease addListener
		(RemoteEventListener<MicroblogEvent> listener)
		throws RemoteException;
	
	/**
	 * Get the date/time of 2 most recently messages from this microblog's message 
	 * list.
	 *
	 * @return	Date[]  Array of the date/time (length = 2).
	 *
	 * @exception  	RemoteException
	 *             	Thrown if a remote error occurred.
	 */
	public Date[] getRecentTime()
		throws RemoteException;

	/**
	 * Get the serial number of 2 most recently messages from this microblog's 
	 * message list.
	 *
	 * @return  	int[]  Array of the serial number (length = 2).
	 *
	 * @exception  	RemoteException
	 *             	Thrown if a remote error occurred.
	 */
	public int[] getRecentNum()
		throws RemoteException;

	/**
	 * Get the name of 2 most recently messages from this microblog's message 
	 * list.
	 *
	 * @return  	String  Sender name of those messages.
	 *
	 * @exception	RemoteException
	 *             	Thrown if a remote error occurred.
	 */
	public String getRecentName()
		throws RemoteException;

	/**
	 * Get the conplete message content of 2 most recently messages from this 
	 * microblog's message list.
	 *
	 * @return	String[]  Array of the conplete message content (length = 2).
	 *
	 * @exception  	RemoteException
	 *             	Thrown if a remote error occurred.
	 */
	public String[] getRecentCont()
		throws RemoteException;
	}
