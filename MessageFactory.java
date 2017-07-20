/**
 * Class MessageFactory provides an object for creating addMessage and
 * removeMessage objects in the P2Patter system.
 */
public class MessageFactory 
	{
	/**
	 * Specify microblog owner name that a message need to add in
	 * or remove from.
	 */
	private String name;

	/**
	 * The serial number of message
	 */
	private int num;
	
	/**
	 * Create a new message factory. The given microblog owner <TT>name</TT> 
	 * will be filled into every newly created message (add or remove).
	 *
	 * @param	name  Microblog owner name.
	 */
	public MessageFactory
		(String name)
		{
		this.name = name;
		}

	/**
	 * Create a message with given message <TT>text</TT> and <TT>date</TT>.
	 *
	 * @param	text  Message text.
 	 * @param	date  Messate date/time.
	 */
	public AddMessage createMessage
		(String text,
		 String date)
		{
		return new AddMessage(name, ++ this.num, date, text);
		}
	
	/**
	 * Delete the message with given serial <TT>num</TT>.
	 *
	 * @param	num  Serial number.
	 */
	public RemoveMessage deleteMessage
		(int num)
		{
		return new RemoveMessage(name, num);
		}
	}
