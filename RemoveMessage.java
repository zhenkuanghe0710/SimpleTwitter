import edu.rit.ds.registry.NotBoundException;
import edu.rit.ds.registry.RegistryProxy;
import java.rmi.RemoteException;

/**
 * Class RemoveMessage encapsulates a function of removing the message from specify  
 * microblog in the P2Patter system. An object of removing message is uniquely  
 * identified by the given microblog owner name and the message serial number,
 * <P>
 * Class AddMessage also has the main program for removing the message from specify
 * microblog in the P2Pedia system.
 * <P>
 * Usage: java AddMessage <I>host</I> <I>port</I> <I>name</I> <I>number</I>
 * <BR><I>host</I> = The name of the host computer where the Registry Server is running
 * <BR><I>port</I> = The port number to which the Registry Server is listening
 * <BR><I>name</I> = The microblog owner's name
 * <BR><I>number</I> = The serial number of the message to be removed
 */
public class RemoveMessage 
	{
	/**
	 * This microblog's owner name.
	 */
	public final String name;

	/**
	 * This message's serial number.
	 */
	public final int num;
	
	/**
	 * Remove the message with the given serial <TT>num</TT> from the specify microblog 
	 * with the owner <TT>name</TT>. Do not call this constructor directly. Call the 
	 * <TT>deleteMessage()</TT> method in class MessageFactory.
	 *
	 * @param	name    Microblog owner name.
	 * @param	num  	Serial number.
	 */
	public RemoveMessage
		(String name, 
		 int num)
		{
		this.name = name;
		this.num = num;
		}
	
	/**
	 * RemoveMessage main program.
	 */
	public static void main
		(String[] args)
		throws Exception
		{
		// Parse command line arguments.
		if (args.length !=4) 
			{
			throw new IllegalArgumentException
			("\nUsage: java RemoveMessage <host> <port> <name> <number>\n" +
			 "<host> = The name of the host computer where the Registry Server is running\n" +
			 "<port> = The port number to which the Registry Server is listening\n" +
		     	 "<name> = The microblog owner's name\n" +
	     	 	 "<number> = The serial number of the message to be removed");
			}
		
		String host = args[0];
		int port = parseInt(args[1], "port");
		String name = args[2];
		int num = parseInt(args[3], "number");
	
		// Look up microblog owner name in the Registry Server and remove the specify message. 
		// Then print that complete message content on console.
		try
			{
			RegistryProxy registry = new RegistryProxy(host, port);
			MicroblogRef microblog = (MicroblogRef) registry.lookup(name);
			String message = microblog.RemoveMessage(num);
			if (message == null)
				{
				throw new NullPointerException
					("\nRemoveMessage(): The given owner name does not contain a message with the given serial number");
				}
			System.out.println(message);
			}
		catch (NotBoundException nbe)
			{
			throw new NotBoundException
				("\nRemoveMessage(): No microblog object for the given owner name");
			}
		catch (RemoteException re)
			{
			throw new RemoteException
				("\nRemoveMessage(): No Registry Server running at the given host and port");
			}
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
		catch (NumberFormatException e)
			{
			throw new IllegalArgumentException
				("\nRemoveMessage(): Invalid <"+object+">: \""+arg+"\"");
			}
		}		
	}
