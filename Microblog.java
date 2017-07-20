import edu.rit.ds.Lease;
import edu.rit.ds.RemoteEventGenerator;
import edu.rit.ds.RemoteEventListener;
import edu.rit.ds.registry.AlreadyBoundException;
import edu.rit.ds.registry.RegistryProxy;
import java.io.IOException;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Class Microblog provides a Java RMI distributed microblog object in the P2Patter system.
 * <P>
 * Usage: java Start Microblog <I>host</I> <I>port</I> <I>name</I>
 * <BR><I>host</I> = The name of the host computer where the Registry Server is running
 * <BR><I>port</I> = The port number to which the Registry Server is listening
 * <BR><I>name</I> = The microblog owner's name
 */
public class Microblog 
	implements MicroblogRef
	{
	private String host;
	private int port;
	private String name;
	
	private RegistryProxy registry;
	private MessageFactory factory;
	private MessageList list;
	private RemoteEventGenerator<MicroblogEvent> eventGenerator;

	private AddMessage message;

	/**
	 * Construct a new microblog object.
	 * <P>
	 * The command line arguments are:
	 * <BR><TT>args[0]</TT> = The name of the host computer where the Registry Server is running
	 * <BR><TT>args[1]</TT> = The port number to which the Registry Server is listening
	 *
	 * @param	args  Command line arguments.
	 *
	 * @exception  	IllegalArgumentException
	 *		(unchecked exception) Thrown if there was a problem 
	 *		with the command line arguments.
	 *
	 * @exception  	IOException
	 *      	Thrown if an I/O error or a remote error occurred.
	 */
	public Microblog
		(String[] args)
		throws IOException
		{
		// Parse command line arguments.
		if (args.length !=3) 
			{
			throw new IllegalArgumentException
				("\nUsage: java Start Microblog <host> <port> <name>\n" +
			     	 "<host> = The name of the host computer where the Registry Server is running\n" +
				 "<port> = The port number to which the Registry Server is listening\n" +
				 "<name> = The microblog owner's name");
			}
		host = args[0];
		port = parseInt(args[1], "port");
		name = args[2];
		
		// Generate a message list for the microblog.
		list = new MessageList(name);
		
		// Prepare to add/remove the message.
		factory = new MessageFactory(name);
		
		// Prepare to generate remote events.
		eventGenerator = new RemoteEventGenerator<MicroblogEvent>();
		
		// Get a proxy for the Registry Server. 
		try
			{
			registry = new RegistryProxy(host, port);
			}
		catch (RemoteException re)
			{
			throw new RemoteException
				("\nMicroblog(): No Registry Server running at the given host and port");
			}
		
		// Export this microblog.
		UnicastRemoteObject.exportObject(this, 0);
		
		// Bind this microblog into the Registry Server.
		// Set the lease interval time to 5 seconds.
		try
			{
			registry.bind (name, this, 5000);
			}
		catch (AlreadyBoundException albe)
			{
			try
				{
				UnicastRemoteObject.unexportObject(this, true);
				}	
			catch (NoSuchObjectException nsoe)
				{
				}

			throw new IllegalArgumentException
				("\nMicroblog(): <name> = \"" + name + "\" already exists");
			}
		catch (RemoteException re)
			{
			try
				{
				UnicastRemoteObject.unexportObject(this, true);
				}
			catch (NoSuchObjectException nsoe)
				{
				}
			throw re;
			}
		}

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
		throws RemoteException 
		{
		// Generate the date/time with specify format.
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MILLISECOND, 0);
		Date toDay = cal.getTime();
		String date = format.format(toDay);
		
		// Create a new message
		message = factory.createMessage(text, date);
		int num = message.num;
		String cont = "--------------------------------------------------------------------------------\n" +
			       name + " -- Message " + num + " -- " + date + "\n" + 
			       text;
		Message mess = new Message(name, num, toDay, cont);
		list.add(num, mess);
		
		// Report a MicroblogEvent to any remote event listeners.
		eventGenerator.reportEvent(new MicroblogEvent(name, message));
		
		return cont;
		}
	
	/**
	 * Remove the message with the given message text. This method is called by 
	 * the RemoveMessage client program to remove the given message from specify 		 
	 * microblog.
	 *
	 * @param	num  Message serial number.
	 *
	 * @return  	The complete message content includes: 
         *          	the first line consists of 80 hyphen characters;
	 *          	the second line consists of the name of microblog owner, 
	 *	    	the <TT>num</TT> of message, the date/time of that message;
	 *	    	the third line consists of the message text.
	 *
	 * @exception  	RemoteException
	 *     	    	Thrown if a remote error occurred.
	 */
	public String RemoveMessage
		(int num) 
		throws RemoteException 
		{
		// Delete the message
		factory.deleteMessage(num);
		String cont = list.query(num).getCont();
		list.remove(num);

		return cont;
		}

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
		throws RemoteException 
		{
		// Return the lease and set the lease interval time to 5 seconds.
		return eventGenerator.addListener(listener, 5000);
		}
	
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
		throws RemoteException
		{		
		return list.queryRecentTime();
		}

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
		throws RemoteException
		{		
		return list.queryRecentNum();
		}

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
		throws RemoteException
		{		
		return list.queryRecentName();
		}

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
		throws RemoteException
		{		
		return list.queryRecentCont();
		}

	/**
	 * Parse an integer command line argument.
	 *
	 * @param	arg	Command line argument.
	 * @param	object  Invalid object name.
	 *
	 * @return	Integer value of <TT>arg</TT>.
	 *
	 * @exception	IllegalArgumentException
	 *     		(unchecked exception) Thrown if <TT>arg</TT> cannot be parsed as an
	 *     		integer.
	 */
	private static int parseInt
		(String arg,
		 String object)
		{
		try
			{
			return Integer.parseInt(arg);
			}
		catch (NumberFormatException nfe)
			{
			throw new IllegalArgumentException
			("\nMicroblog(): Invalid <"+object+">: \""+arg+"\"");
			}
		}
}
