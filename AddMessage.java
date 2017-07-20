import edu.rit.ds.registry.NotBoundException;
import edu.rit.ds.registry.RegistryProxy;
import java.io.Serializable;
import java.rmi.RemoteException;

/**
 * Class Addmessage encapsulates a function of adding a new message to specify  
 * microblog in the P2Patter system. An object of adding message is uniquely  
 * identified by the given microblog owner name, message serial number,
 * message date/time, and message text. Class AddMessage is serializable so it 
 * can be passed in remote method calls.
 * <P>
 * Class AddMessage also has the main program for adding a new message to specify
 * microblog in the P2Pedia system.
 * <P>
 * Usage: java AddMessage <I>host</I> <I>port</I> <I>name</I> "<I>text</I>"
 * <BR><I>host</I> = The name of the host computer where the Registry Server is running
 * <BR><I>port</I> = The port number to which the Registry Server is listening
 * <BR><I>name</I> = The microblog owner's name
 * <BR><I>text</I> = The text of the message to be added (It is enclosed in quotation marks)
 */
public class AddMessage 
	implements Serializable
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
	 * This message's text.
	 */
	public final String text;

	/**
	 * This message's date/time.
	 */
	public final String date;
	
	/**
	 * Add a new message to the specify microblog with the owner <TT>name</TT>.
	 * The message includes serial <TT>num</TT>, current <TT>date</TT>, and
	 * given <TT>text</TT>. Do not call this constructor directly. Call the 
	 * <TT>createMessage()</TT> method in class MessageFactory.
	 *
	 * @param	name    Microblog owner name.
	 * @param	num  	Serial number.
	 * @param	date	Current date/time.
	 * @param	text	Message text.
	 */
	public AddMessage
		(String name, 
		 int num,
		 String date,
		 String text)
		{
		this.name = name;
		this.num = num;
		this.date = date;
		this.text = text;
		}
	
	/**
	 * Returns the complete message content of the current adding message.
	 *
	 * @return	String	Complete message content.
	 */
	public String toString()
		{
		return "--------------------------------------------------------------------------------\n" +
			name + " -- Message " + num + " -- " + date + "\n" + 
			text;
		}
	
	/**
	 * AddMessage main program.
	 */
	public static void main
		(String[] args)
		throws Exception
		{
		// Parse command line arguments.
		if (args.length !=4) 
			{
			throw new IllegalArgumentException
				("\nUsage: java AddMessage <host> <port> <name> \"<text>\"\n" +
				 "<host> = The name of the host computer where the Registry Server is running\n" +
				 "<port> = The port number to which the Registry Server is listening\n" +
			     	 "<name> = The microblog owner's name\n" +
		     	 	 "<text> = The text of the message to be added (It is enclosed in quotation marks)");
			}

		String host = args[0];
		int port = parseInt(args[1], "port");
		String name = args[2];
		String text = args[3];
		
		// Look up microblog owner name in the Registry Server and add a new message. 
		// Then print the complete message content on console.
		try
			{
			RegistryProxy registry = new RegistryProxy(host, port);
			MicroblogRef microblog = (MicroblogRef) registry.lookup(name);
			String message = microblog.AddMessage(text);
			
			System.out.println(message);
			}
		catch (NotBoundException nbe)
			{
			throw new NotBoundException
				("\nAddMessage(): No microblog object for the given owner name");
			}
		catch (RemoteException re)
			{
			throw new RemoteException
				("\nAddmessage(): No Registry Server running at the given host and port");
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
				("\nAddmessage(): Invalid <"+object+">: \""+arg+"\"");
			}
		}		
	
	}
