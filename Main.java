//Matthew Miller
//January 2017
//Parses the output of nirsoft Skype chat-log export (http://www.nirsoft.net/utils/skype_log_view.html) to separate chat files
//NOTE: export should be in UTF-8 format.

package skype_nirsoft_parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

public class Main {
	
	static String nirsoftFile = "Z:\\skype_recovered_UTF8.txt";
	static String outPath = "Z:\\";
	
	//Filesystem "slash" (for Windows "\\" vs Mac and Linux "/")
	static String slash = "\\";
	
	//filesystem-safe date-time format
	static SimpleDateFormat filenameWriterDateTimeFormat = new SimpleDateFormat("M-d-yyyy  h_mm_ss a");
	
	// Format is "1/6/2011 8:53:52 PM"
	static SimpleDateFormat parseDateTimeFormat = new SimpleDateFormat("M/d/yyyy h:m:s a");

	// Format is "8:53:52"
	static SimpleDateFormat parseTimeDurationFormat = new SimpleDateFormat("h:m:s");

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		HashMap<String,ChatThread> chatThreads = new HashMap<String,ChatThread>();
		HashMap<String,ChatThread> chatGroups = new HashMap<String,ChatThread>();
		ArrayList<SkypeRecord> otherRecords = new ArrayList<SkypeRecord>();
		
		//For stats
		ArrayList<String> recordTypes = new ArrayList<String>();
		
		
		/**
		 * **************
		 * Read data in *
		 * **************
		 */
		System.out.println();
		System.out.println("Reading input file..............................................");
		String line = null;
		String lastField = "";
		try
		{
			//open file
	    	BufferedReader br = new BufferedReader(new FileReader(nirsoftFile));
	        StringBuilder sb = new StringBuilder();
	        line = br.readLine();
	        //line = br.readLine(); //skip first line
	
	        //parse file
	        boolean inRecord=false;
	        SkypeRecord record = null;
	        while (line != null) {
	        	if(!line.isEmpty())
	        	{
	        		if(line.contains("==========") && !inRecord)
	        		{
	        			//start of record
	        			inRecord = true;
	        			record = new SkypeRecord();
	        		}
	        		else if(line.contains("==========") && inRecord)
	        		{
	        			//end of record
	        			if(!recordTypes.contains(record.getActionType()))
	        			{
	        				recordTypes.add(record.getActionType());
	        			}
	        			
	        			
	        			//determine type of record
	        			if(record.getActionType().equals("Chat Message"))
	        			{
	        				//file to chats
	        				if(record.getChatID() != null && !record.getChatID().isEmpty())
	        				{
	        					String chatID = record.getChatID();
	        					ChatThread thread = chatThreads.get(chatID);
	        					if(thread == null)
	        					{
	        						thread = new ChatThread();
	        						chatThreads.put(chatID, thread);
	        					}
	        					thread.addMessage(record);
	        				}
	        				else
	        				{
	        					System.out.println("Unexpected Chat Message without ChatID");
	        				}
	        			}
	        			else
	        			{
	        				//file to other
	        				otherRecords.add(record);
	        				
	        			}
	        			
	        			inRecord = false;
	        		}
	        		else if(inRecord)
	        		{
	        			//This handles multi-line messages
	        			if(lastField.equals("Chat Message") && !line.contains("ChatID"))
	        			{
	        				record.setMessage(record.getMessage() + " \n" + line);
	        			}
	        			//This handles other fields
	        			else if(line.contains(": "))
	        			{
	        				String fieldData[] = line.split(": ",2);
	        				String field = fieldData[0].trim();
	        				String value = fieldData[1];
	        				lastField = field;
	        				
	        				switch(field)
	        				{
	        					case "Record Number":
	        						record.setRecordNumber(Integer.parseInt(value));
	        						break;
	        					
	        					case "Action Type":
	        						record.setActionType(value);
	        						break;
	        					
	        					case "Action Time":
	        						record.setActionTime(parseDateTime(value));
	        						break;
	        					
	        					case "End Time":
	        						record.setEndTime(parseDateTime(value));
	        						break;
	        					
	        					case "User Name":
	        						record.setUserName(value);
	        						break;
	        					
	        					case "Display Name":
	        						record.setDisplayName(value);
	        						break;
	        					
	        					case "Duration":
	        						record.setDuration(parseTime(value));
	        						break;
	        					
	        					case "Chat Message":
	        						record.setMessage(value);
	        						break;
	        					
	        					case "ChatID":
	        						record.setChatID(value);
	        						break;
	        						
	        					case "Filename":
	        						record.setFilename(value);
	        						break;
	        					
	        					default:
	        						System.out.println("Unknown field data: "+line);
	        					
	        				}
	        			}
	        			else
	        			{
	        				System.out.println("Unknown line: "+line);
	        			}
	        		}
	        	}
				line = br.readLine();
	        }
		}
		catch(Exception e)
		{
			System.err.println("Error parsing line: "+line);
			e.printStackTrace();
		}
		
		/**
		 * **************
		 * Process data *
		 * **************
		 */
		
		//print unknown data
		System.out.println();
		System.out.println("Listing unmatched records (no ChatID to match).......................");
		for(int x=0; x < otherRecords.size(); x++)
		{
			System.out.println(otherRecords.get(x));
		}
		
		
		//print initial stats
		System.out.println();
		System.out.println("Initial data statistics..............................................");
		System.out.println("[ Parsed "+chatThreads.size()+" threads by unique ID ]");
		for (String key : chatThreads.keySet())
		{
			ChatThread chatThread = chatThreads.get(key);
		    System.out.println(chatThread.getParticipantsString() +" ("+chatThread.getMessageCount()+" messages)");
		}
		
		System.out.println();
		System.out.println("Consolidating chats by usernames......................................");
		for (String key : chatThreads.keySet())
		{
			//Get the chat thread and participant group from the hashmaps
			ChatThread chatThread = chatThreads.get(key);
			Collections.sort(chatThread.getParticipants());
			String groupName = chatThread.getParticipantsString();
			ChatThread group = chatGroups.get(groupName);		
			if(group == null)
			{
				//Initialize chat thread for group
				group = new ChatThread();
				group.setParticipants(chatThread.getParticipants());
				group.setDateTime(chatThread.getDateTime());
				Collections.sort(group.getParticipants());
				chatGroups.put(groupName,group);
			}
			//Merge the messages
			group.addMessages(chatThread.getMessages());
			//Update start time if needed
			if(group.getDateTime().compareTo(chatThread.getDateTime()) > 0)
				group.setDateTime(chatThread.getDateTime());
		}
		
		//print initial stats
		System.out.println();
		System.out.println("Merged data statistics..............................................");
		System.out.println("[ Parsed "+chatGroups.size()+" threads by participants ]");
		for (String key : chatGroups.keySet())
		{
			ChatThread chatGroup = chatGroups.get(key);
		    System.out.println(chatGroup.getParticipantsString() +" ("+chatGroup.getMessageCount()+" messages)");
		}
		
		//print initial stats
		System.out.println();
		System.out.println("Sorting messages chronologically.....................................");
		for (String key : chatGroups.keySet())
		{
			ChatThread chatGroup = chatGroups.get(key);
			Collections.sort(chatGroup.getMessages());
		}
		
		/**
		 * ************
		 * Write data *
		 * ************
		 */
		System.out.println();
		System.out.println("Writing out chats by group...........................................");
		int chatCounter=0; //just to add a unique value we will count
		for (String key : chatGroups.keySet())
		{
			chatCounter++;
			ChatThread chatGroup = chatGroups.get(key);

			String participantString = chatGroup.getParticipantsString();
			if(participantString.length() > 100)
			{
				System.out.print("Truncating long list of usernames: ");
				System.out.print(participantString + " --> ");
				participantString = participantString.substring(0,80) + " [and more]";
				System.out.println(participantString);
			}
			String filename = "[" + chatCounter + "] " + participantString +" ("+filenameWriterDateTimeFormat.format(chatGroup.getDateTime())+")";
			
			System.out.println("Writing to file \""+filename+"\"");
			
			try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
		              new FileOutputStream(outPath + slash + filename + ".txt"), "utf-8")))
		    {
				//Write list of participants at top of file
				ArrayList<String> participants = chatGroup.getParticipants();
				writer.write("Chat Participants:");
				writer.newLine();
				for(int x=0; x < participants.size(); x++)
				{
					writer.write(participants.get(x).toString());
					writer.newLine();
				}
				writer.write("------------------------------------------------------------");
				writer.newLine();
				writer.newLine();
				
				//Write chat log
				ArrayList<SkypeRecord> data = chatGroup.getMessages();
				for(int x=0; x < data.size(); x++)
				{
					writer.write(convertHtml(data.get(x).toString()));
					writer.newLine();
				}
			
			}
			catch(Exception e)
			{
				System.err.println("Failed writing file: "+filename);
				e.printStackTrace();
			}
		}
		
		
		System.out.println();
		System.out.println("Writing out other data...........................................");
		
		String filename = "[0] orphined other data records";
		
		System.out.println("Writing to file \""+filename+"\"");
		
		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
	              new FileOutputStream(outPath + slash + filename + ".txt"), "utf-8")))
	    {

			for(int x=0; x < otherRecords.size(); x++)
			{
				writer.write(convertHtml(otherRecords.get(x).toString()));
				writer.newLine();
			}
		
		}
		catch(Exception e)
		{
			System.err.println("Failed writing file: "+filename);
			e.printStackTrace();
		}
		
		System.out.println("Done.");
		
	}
	
	private static Date parseDateTime(String dateTime) throws ParseException
	{
		if(dateTime.trim().isEmpty())
			return null;
			
		Date date = parseDateTimeFormat.parse(dateTime);
		return date;
	}
	
	private static Date parseTime(String time) throws ParseException
	{
		if(time.trim().isEmpty())
			return null;
			
		Date date = parseTimeDurationFormat.parse(time);
		return date;
	}
	
	private static String convertHtml(String data)
	{
		data = data.replaceAll("&amp;", "&");
		data = data.replaceAll("&apos;", "'");
		data = data.replaceAll("&gt;", ">");
		data = data.replaceAll("&lt;", "<");
		data = data.replaceAll("&quot;", "\"");
		return data;
	}

}
