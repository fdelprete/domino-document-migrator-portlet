package com.fmdp.domino_migrator.util;


import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import lotus.domino.NotesError;
import lotus.domino.NotesException;
import lotus.domino.NotesFactory;
/**
 * Manages connections with a Lotus Domino server via DIIOP.
 * The DIIOP task on the Domino server must be started and listening on port 63148 
 *
 * @author Filippo Maria Del Prete, FDP Sas
 * @version 1.0.0, 28/12/2014
 */
public class DominoProxy {

	public lotus.domino.Session dominoSession;
		

	/**
     * Open the DIIOP connection with the Domino server.  The session is opened with a username and password.
     *
     * @param dominoServer The host name and port address for connecting to the Domino server with DIIOP.
     *                     Example value: domino.acme.com:63148 
     *                     If the port is the standard port 63148 you can omit the port, so the value
     *                     is: domino.ame.com
     * @param username     Username
     * @param password     Password
     */
    public void openDominoSession(String dominoServer, String username, String password) {

        try {
            //Open a session with the Domino server.  
        	//The Domino server must be running the DIIOP task,
        	//and must accept connections for the user.
            dominoSession = NotesFactory.createSession(dominoServer, username, password);

        } catch (NotesException e) {

            switch (e.id) {
                case NotesError.NOTES_ERR_INVALID_USERNAME_PASSWD:
                    //Invalid password.
                	_log.info("Invalid notes password");
                	break;
                case NotesError.NOTES_ERR_INVALID_USERNAME:
                    //Invalid username.
                	_log.info("Invalid notes username");
                    break;
                case NotesError.NOTES_ERR_SERVER_ACCESS_DENIED:
                    //Access denied.
                    _log.info("This user, " + username + ", is not authorized to open DIIOP connections with the Domino server.  Check your DIIOP configuration.  NotesException - " + e.id + " " + e.getLocalizedMessage() + ".");
                    break;
                case NotesError.NOTES_ERR_GETIOR_FAILED:
                    //Could not get IOR from Domino Server.
                    _log.info("Unable to open a DIIOP connection with " + dominoServer + ".  Make sure the DIIOP and HTTP tasks are running on the Domino server, and that ports are open.  NotesException - " + e.id + " " + e.getLocalizedMessage() + ".");
                    break;
                default:
                    //Unexpected error.
                    _log.error("NotesException - " + e.id + " " + e.getLocalizedMessage() + ".");
                    e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Close the DIIOP session with the Domino server.
     */
    public void closeDominoSession() {
        try {
            dominoSession.recycle();
        } catch (NotesException e) {
        }
    }


    /**
     * Determine if a session is available with the Domino server.
     *
     * @return True if the session is available, otherwise False.
     */
    public boolean isDominoSessionAvailable() {

        if (dominoSession == null) {
            return false;
        } else {
            if (dominoSession.isValid()) {
                return true;
            } else {
                return false;
            }
        }

    }

		
	/** Singleton instance. */
	private static DominoProxy INSTANCE = new DominoProxy();

	  
	private DominoProxy() {
		init();
	}

	public static DominoProxy getInstance() {
		return INSTANCE;
	}

	private void init() {
	}
	
	private static final Log _log = LogFactoryUtil.getLog(DominoProxy.class);

}
