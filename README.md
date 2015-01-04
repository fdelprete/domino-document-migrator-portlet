domino-document-migrator-portlet
================================

A control panel Liferay portlet to import Notes document attachments from a remote Notes database.

This portlet use NCSO.jar. You can obtain the library from the /domino/java directory in your server Domino data folder.

You can choose a Notes database from a remote Domino server. 
(You connect to the remote Domino server using DIIOP so that task must be running on the Domino server)

Then you can choose a Notes view selecting all the Notes document cotaining the attachments you want to import.

The attachments will be imported in a folder (you can choose it) af the Documents And Media Library.

If in your Notes document you have a field storing tags you can also import the Notes tags into Liferay document tags.

If in your Notes document you have a field storing categories in the "classic" Notes mode (cat1\cat\2\...\catX) you can also import the Notes category into Liferay document vocabulary.


The import process (could be time consuming) is started as liferay BackgroundTask based on the robust Liferay ESB.

TO DO
- Managing BackgroundTastStatus (done 2014-12-31)
- Remove or decide to move server connection parameters in the configuration page. (done 2015-01-01)
- Mapping of Notes document category to liferay asset category (done 2015-01-04).
- Mapping of Roles in Notes readers and authors field to liferay site roles.
