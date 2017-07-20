import java.util.Date;

/**
 * Class Message encapsulates a object node that save every message information,
 * which includes the owner name of microblog that the message belongs to,
 * the message serial number, the message post date/time, and the complete
 * message content. Each message has a class Message node to contain that
 * information.
 */
public class Message
	{
	private String name;
	private int num;
	private Date date;
	private String cont;
	
	/**
	 * Default Constructor
	 */
	public Message()
		{
		}
	
	/**
	 * Create a new message. It consists of the owner <TT>name</TT> of 
	 * microblog that the message belongs to, the message serial <TT>number</TT>, 
	 * the message post <TT>date</TT>, and the complete message <TT>cont</TT>. 
	 *
	 * @param	name    Microblog owner name.
	 * @param	num  	Serial number.
	 * @param	date	Message date/time.
	 * @param	cont	Complete message content.
	 */
	public Message
		(String name,
		 int num,
		 Date date,
		 String cont)
		{
		this.name = name;
		this.num = num;
		this.date = date;
		this.cont = cont;
		}

	/**
	 * Get the microblog owner name of that message.
	 * 
	 * @return	String  Microblog owner name.
	 */
	public String getName() 
		{
		return name;
		}

	/**
	 * Get the serial number of that message.
	 * 
	 * @return	int  Serial number.
	 */
	public int getNum() 
		{
		return num;
		}

	/**
	 * Get the date/time of that message.
	 * 
	 * @return	Date  Message date/time.
	 */
	public Date getDate() 
		{
		return date;
		}

	/**
	 * Get the complete content of that message.
	 * 
	 * @return	String  Complete message content.
	 */
	public String getCont() 
		{
		return cont;
		}

	/**
	 * Returns the complete message content of the message.
	 *
	 * @return	String	Complete message content.
	 */	
	public String toString()
		{
		return cont;
		}
	}
