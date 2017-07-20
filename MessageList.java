import java.util.Date;
import java.util.HashMap;

/**
 * Class MessageList encapsulates a list of message of every microblog in the P2Patter
 * system.
 */
public class MessageList 
	{
	/**
	 * Map from message serial number to specify message object.
	 */
	private HashMap<Integer, Message> messages = new HashMap<Integer, Message>();
	private String name;
	private Message mess;
	
	/**
	 * Construct a new message list with given microblog owner <TT>name</TT>.
	 *
	 * @param	name  Microblog owner name.
	 */
	public MessageList
		(String name)
		{
		this.name = name;
		messages.put(0, null);
		}
	
	/**
	 * Add a message with given serial <TT>num</tt> and object <TT>mess</TT> 
	 * to this message list. 
	 *
	 * @param	num   Serial number.
	 * @param	mess  Message object.
	 */
	public void add
		(int num,
		 Message mess)
		{		
		messages.put(num, mess);
		}

	/**
	 * Remove the message with given serial <TT>num</tt> to this message list.
	 * The way to remove the message is to set the object of certain serial 
	 * <TT>num</TT> to be null.
	 *
	 * @param	num   Serial number.
	 */
	public void remove
		(int num)
		{
		messages.put(num, null);
		}
	
	/**
	 * Query a message with given serial <TT>num</tt> from this message list.
	 *
	 * @param	num   Serial number.
	 */
	public Message query
		(int num)
		{
		mess = messages.get(num);
		return mess;
		}
	
	/**
	 * Query the recent 2 message dates/time from this message list.
	 */
	public Date[] queryRecentTime()
		{
		// If the message with specify number exist, return that date/time;
		// if not, return null.
		Date last1 = messages.get(this.queryRecentNum()[0]) == null ?
						null : messages.get(this.queryRecentNum()[0]).getDate();
		Date last2 = messages.get(this.queryRecentNum()[1]) == null ?
						null : messages.get(this.queryRecentNum()[1]).getDate();
		
		Date[] last = {last1, last2};
		return last;
		}
	
	/**
	 * Query the recent 2 message serial numbers from this message list. 
	 */
	public int[] queryRecentNum()
		{
		// Get the last (most recently) message serial number		
		int last1 = messages.size()-1;
		int temp = last1;
		int last2 = 0;
		
		// Get the second message serial number from last (if any)
		while (last2 ==0 && temp != 0)
			{
			if (messages.get(--temp) != null)
				last2 = temp;
			}
		
		int[] last = {last1, last2};
		
		return last;
		}
	
	/**
	 * Query the microblog owner name from this message list. 
	 */
	public String queryRecentName()
		{		
		return name;
		}
	
	/**
	 * Query the recent 2 complete message contents from this message list. 
	 */
	public String[] queryRecentCont()
		{	
		// If the message with specify number exist, return that content;
		// if not, return null.	
		String last1 = messages.get(this.queryRecentNum()[0]) == null ?
						  null : messages.get(this.queryRecentNum()[0]).getCont();
		String last2 = messages.get(this.queryRecentNum()[1]) == null ?
				  		  null : messages.get(this.queryRecentNum()[1]).getCont();
		
		String[] last = {last1, last2};
		return last;
		}
	}
