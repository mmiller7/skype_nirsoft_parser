//Matthew Miller
//January 2017
//Parses the output of nirsoft Skype chat-log export (http://www.nirsoft.net/utils/skype_log_view.html) to separate chat files
//NOTE: export should be in UTF-8 format.

package skype_nirsoft_parser;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SkypeRecord implements Comparable<Object>
{
	private int recordNumber;
	private String actionType;
	private Date actionTime;
	private Date endTime;
	private String userName;
	private String displayName;
	private Date duration;
	private String message;
	private String chatID;
	private String filename;
	
	private static final SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm:ss");
	private static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("M/d/yyyy h:mm:ss a");
	
	
	
	/**
	 * @return the recordNumber
	 */
	public int getRecordNumber() {
		return recordNumber;
	}



	/**
	 * @return the actionType
	 */
	public String getActionType() {
		return actionType;
	}



	/**
	 * @return the actionTime
	 */
	public Date getActionTime() {
		return actionTime;
	}



	/**
	 * @return the endTime
	 */
	public Date getEndTime() {
		return endTime;
	}



	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}



	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}



	/**
	 * @return the duration
	 */
	public Date getDuration() {
		return duration;
	}



	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}



	/**
	 * @return the chatID
	 */
	public String getChatID() {
		return chatID;
	}



	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}



	/**
	 * @param recordNumber the recordNumber to set
	 */
	public void setRecordNumber(int recordNumber) {
		this.recordNumber = recordNumber;
	}



	/**
	 * @param actionType the actionType to set
	 */
	public void setActionType(String actionType) {
		this.actionType = actionType;
	}



	/**
	 * @param actionTime the actionTime to set
	 */
	public void setActionTime(Date actionTime) {
		this.actionTime = actionTime;
	}



	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}



	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}



	/**
	 * @param displayName the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}



	/**
	 * @param duration the duration to set
	 */
	public void setDuration(Date duration) {
		this.duration = duration;
	}



	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}



	/**
	 * @param chatID the chatID to set
	 */
	public void setChatID(String chatID) {
		this.chatID = chatID;
	}



	/**
	 * @param filename the filename to set
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}



	public int compareTo(Object o)
	{
		return actionTime.compareTo(
				((SkypeRecord)o).getActionTime()
				);
	}
	
	public String toString()
	{
		String s = "";
		if(displayName != null && !displayName.isEmpty())
			s+=displayName+" ";
		if(userName != null && !userName.isEmpty())
			s+="("+userName+") " ;
		if(actionType != null && !actionType.isEmpty() && !actionType.equals("Chat Message"))
			s+="- "+actionType;
		s+=" " + dateTimeFormat.format(actionTime) + ": ";
		if(filename != null && !filename.isEmpty())
			s+=filename+" ";
		if(message != null && !message.isEmpty())
			s+=message;
		if(duration != null)
			s+=" (" + timeFormat.format(duration) + " duration)";
		
		return s.trim();
	}

}
