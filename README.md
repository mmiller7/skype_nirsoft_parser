# skype_nirsoft_parser

This takes an input file being UTF-8 encoded textfile from the nirsoft Skype log export and parses it out into the separate readable chat threads grouped by the chat participants and sorted chronologically for easy readable archiving.  Data that can not be corrolated to a specific chat is written into a separate file.



Sample input-data generated from the Nirsoft Skype export tool:

==================================================
Record Number     : 12345
Action Type       : Chat Message
Action Time       : 12/5/2011 10:19:08 PM
End Time          : 
User Name         : username
Display Name      : Some Person
Duration          : 
Chat Message      : hello
ChatID            : #username/$otheruser;123457890abcdef
Filename          : 
==================================================



Sample translated output chat log generated by this Java application:

Some Person (username)  12/5/2011 10:19:08 PM: hello
