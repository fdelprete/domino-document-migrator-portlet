domino-document-migrator-portlet
================================

A control panel Liferay portlet to import Notes document attachments from a remote Notes database.

This portlet use NCSO.jar. You caon obtian the library from the data/domino/html/java directory in you server Domino.

You can choose a Notes database from a remote Domino server. 
(You connect to the remote Domino server using DIIOP so that task must be running on the Domino server)

Then you can choose a Notes view selecting all the Notes document cotaining the attachments you want to import.

The attachments will be imported in a folder (you can choose it) af the Documents And Media Library.

If in your Notes document you have a field storing tags you can also import the Notes tags into Liferay document tags.