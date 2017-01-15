//Matthew Miller
//January 2017
//Parses the output of nirsoft Skype chat-log export (http://www.nirsoft.net/utils/skype_log_view.html) to separate chat files
//NOTE: export should be in UTF-8 format.

package skype_nirsoft_parser;

import java.util.Date;
import java.util.ArrayList;

public class ChatThread
{
	private ArrayList<SkypeRecord> messages = new ArrayList<SkypeRecord>();
	private ArrayList<String> participants = new ArrayList<String>();
	private Date dateTime = null;
	
	public void addMessage(SkypeRecord record)
	{
		messages.add(record);
		if(!participants.contains(record.getUserName()))
		{
			participants.add(record.getUserName());
		}
		if(dateTime == null || dateTime.compareTo(record.getActionTime()) > 0)
		{
			dateTime = record.getActionTime();
		}
	}
	
	public void addMessages(ArrayList<SkypeRecord> messages)
	{
		this.messages.addAll(messages);
	}
	
	public String getParticipantsString()
	{
		String s = "";
		for(int x=0; x < participants.size(); x++)
		{
			if(s.isEmpty())
				s=participants.get(x);
			else
				s+=" - " + participants.get(x);
		}
		return s;
	}
	
	/**
	 * @return the messages
	 */
	public ArrayList<SkypeRecord> getMessages() {
		return messages;
	}
	/**
	 * @return the participants
	 */
	public ArrayList<String> getParticipants() {
		return participants;
	}
	
	/**
	 * @return the dateTime
	 */
	public Date getDateTime() {
		return dateTime;
	}
	/**
	 * @param messages the messages to set
	 */
	public void setMessages(ArrayList<SkypeRecord> messages) {
		this.messages = messages;
	}
	
	/**
	 * @param participants the participants to set
	 */
	public void setParticipants(ArrayList<String> participants) {
		this.participants = participants;
	}
	
	/**
	 * @param dateTime the dateTime to set
	 */
	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}
	
	public int getMessageCount()
	{
		return messages.size();
	}
}
