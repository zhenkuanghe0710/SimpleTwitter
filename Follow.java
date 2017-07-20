import edu.rit.ds.RemoteEventListener;
import edu.rit.ds.registry.NotBoundException;
import edu.rit.ds.registry.RegistryEventFilter;
import edu.rit.ds.registry.RegistryEventListener;
import edu.rit.ds.registry.RegistryProxy;
import edu.rit.ds.registry.RegistryEvent;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Class Follow provides the follow program in the P2Patter system.
 */
public class Follow 
	{	
	private static RegistryProxy registry;
	private static RegistryEventListener rBoundListener;
	private static RegistryEventListener rUnboundListener;
	private static RegistryEventFilter rBoundFilter;
	private static RegistryEventFilter rUnboundFilter;
	private static RemoteEventListener<MicroblogEvent> mbListener;
	
	private static String rname;
	private static int[] rnum;
	private static String[] rcont;
	private static Date[] rdate;
	
	private static ArrayList<String> boundName = new ArrayList<String>();
	private static ArrayList<String> unboundName = new ArrayList<String>();
	private static Message mess[] = new Message[2];
	private static LinkedList<Message> initial = new LinkedList<Message>();
	
	/**
	 * Follow main program.
	 */
	public static void main
		(String[] args)
		throws Exception
		{
		// Parse command line arguments.
		if (args.length <3) 
			{
			throw new IllegalArgumentException
			("\nUsage: java Follow <host> <port> <name> ...\n" +
			 "<host> = The name of the host computer where the Registry Server is running\n" +
		     	 "<port> = The port number to which the Registry Server is listening\n" +
	         	 "<name> = The microblog owner's name; there may be one or more names");
			}

		String host = args[0];
		int port = parseInt (args[1], "port");
		
		// Get proxy for the Registry Server.
		try
			{
			registry = new RegistryProxy(host, port);
			}
		catch (RemoteException re)
			{
			throw new RemoteException
				("\nFollow(): No Registry Server running at the given host and port");
			}
		
		// If the microblog owner name(s) already exist(s) in registry list, 
		// add it(them) into boundName list;
		// if the microblog owner name(s) do(es) not exist in registry list, 
		// add it(them) into unboundName list.
		for (int i=2; i<args.length; i++)
			{
			for (String objectName : registry.list("Microblog"))
				{
				if (args[i].equals(objectName))
					{
					boundName.add(args[i]);
					args[i] = null;
					break;
					}
				}
			}
		
		for (int i=2; i<args.length; i++)
			{
			if (args[i] != null)
				unboundName.add(args[i]);
			}

		// Export a remote event listener object for receiving "object bound" notifications
		// from the Registry Server.
		rBoundListener = new RegistryEventListener()
			{
			public void report(long seq, RegistryEvent rEvent)
				{
				for (Iterator<String> it = unboundName.iterator(); it.hasNext(); )
					{
					// Check if the new bound object is followed by the client.
					if (rEvent.objectName().equals(it.next()))
						{
						try 
							{
							listenToMicroblog(rEvent.objectName());
							// Update the name lists.
							boundName.add(rEvent.objectName());
							unboundName.remove(rEvent.objectName());
							} 
						catch (Exception e) 
							{
							e.printStackTrace();
							}
						}
					}
				}
			};	
		UnicastRemoteObject.exportObject(rBoundListener, 0);
		
		// Export a remote event listener object for receiving "object unbound" notifications
		// from the Registry Server.
		rUnboundListener = new RegistryEventListener()
			{
			public void report(long seq, RegistryEvent rEvent)
				{
				for (Iterator<String> it = boundName.iterator(); it.hasNext();)
					{
					// Check if the new bound object is followed by the client.
					if (rEvent.objectName().equals(it.next()))
						{
						System.out.println("--------------------------------------------------------------------------------" +
								    rEvent.objectName() + " -- Failed");
						// Update the name lists.
						boundName.remove(rEvent.objectName());
						unboundName.add(rEvent.objectName());
						}
					}
				}
			};
		UnicastRemoteObject.exportObject(rUnboundListener, 0);
		
		// Export a remote event listener object for receiving notifications
		// from Microblog objects.
		mbListener = new RemoteEventListener<MicroblogEvent>()
			{
			public void report(long seq, MicroblogEvent mbEvent)
				{
				// Print the new message from microblog owner 
				// who is followed by client on the console.
				System.out.println(mbEvent.message);
				}
			};
		UnicastRemoteObject.exportObject(mbListener, 0);
		
		// Tell the Registry Server to notify us when a new Node object is bound.
		rBoundFilter = new RegistryEventFilter().reportType("Microblog").reportBound();
		registry.addEventListener(rBoundListener, rBoundFilter);
		
		// Tell the Registry Server to notify us when a Node object is unbound.
		rUnboundFilter = new RegistryEventFilter().reportType("Microblog").reportUnbound();
		registry.addEventListener(rUnboundListener, rUnboundFilter);
		
		// Do the initial follow action to the microblog of given name.
		for (Iterator<String> it = boundName.iterator(); it.hasNext(); )
			{
			try
				{
				listenToInitialMicroblog(it.next());
				}
			catch (Exception e)
				{
				e.printStackTrace();
				}
			}
		
		// Print the 0, 1, or 2 most recently added messages from 
		// the given owners' microblogs on console.
		for (Iterator<Message> it = initial.iterator(); it.hasNext(); )
			{
			System.out.println(it.next());
			}

		}

	/**
	 * Tell the microblog object with given <TT>name</TT> to notify us of adding message.
	 *
	 * @param	name  Microblog owner name.
	 *
	 * @exception  	RemoteException
	 *     		Thrown if a remote error occurred.
	 */
	private static void listenToMicroblog
		(String name)
		throws Exception
		{
		try
			{
			MicroblogRef microblog = (MicroblogRef) registry.lookup(name);
			microblog.addListener(mbListener);
			}
		catch (RemoteException re)
			{
			}
		}
	
	/**
	 * Tell the microblog object with given <TT>name</TT> to notify us of adding message.
	 * This method is for the first time of following and use the distributed method to get
	 * the 2 recent message of a microblog.
	 *
	 * @param	name  Microblog owner name.
	 *
	 * @exception  	RemoteException
	 *     		Thrown if a remote error occurred.
	 */
	private static void listenToInitialMicroblog
		(String name)
		throws Exception
		{
		try
			{
			MicroblogRef microblog = (MicroblogRef) registry.lookup(name);
			microblog.addListener(mbListener);
			
			// Get the data of 2 recent message with distributed method
			rdate = microblog.getRecentTime();
			rname = microblog.getRecentName();
			rnum = microblog.getRecentNum();
			rcont = microblog.getRecentCont();
				
			mess[0] = new Message(rname, rnum[0], rdate[0], rcont[0]);
			mess[1] = new Message(rname, rnum[1], rdate[1], rcont[1]);
			
			// Do twice loop to put the message into right order of initial list.
			for (int i=0; i<=1; i++)
				{
				// Check if the message is null.
				if (rdate[i] != null)
					{
					if (initial.isEmpty())
						{
						initial.add(mess[i]);
						}
					else
						{
						Message tm = initial.getFirst();
						// Compare the new message to every message of 
						// the initial list. 
						while (tm != null)
							{
							// Set the message with ascending order of date/time
							if (mess[i].getDate().compareTo(tm.getDate()) < 0)
								{
								initial.add(initial.indexOf(tm), mess[i]);
								break;
								}
							else if (mess[i].getDate().compareTo(tm.getDate()) == 0)
								{
								// If multiple messages have the same date/time, 
								// set the messages with ascending order of 
								// the users' names;
								if (mess[i].getName().compareTo(tm.getName()) < 0)
									{
									initial.add(initial.indexOf(tm), mess[i]);
									break;
									}
								else if (mess[i].getName().compareTo(tm.getName()) == 0)
									{
									// If multiple messages have the same date/time
									// and the same user name, set the message with 
									// ascending order of serial number.
									if (mess[i].getNum() < tm.getNum())
										{
										initial.add(initial.indexOf(tm), mess[i]);
										break;
										}
									else
										{
										initial.add(initial.indexOf(tm)+1, mess[i]);
										break;
										}
									}
								else
									{
									// Check if that message is the last one of the list
									if (initial.indexOf(tm)+1 == initial.size())
										{
										initial.add(initial.indexOf(tm)+1, mess[i]);
										break;
										}
									else
										{
										tm = initial.get(initial.indexOf(tm)+1);
										continue;
										}
									}
								}
							else
								{
								// Check if that message is the last one of the list
								if (initial.indexOf(tm)+1 == initial.size())
									{
									initial.add(initial.indexOf(tm)+1, mess[i]);
									break;
									}
								else
									{
									tm = initial.get(initial.indexOf(tm)+1);
									continue;
									}
								}
							}	
						}
					}
				}		
			}
		catch (RemoteException re)
			{
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
