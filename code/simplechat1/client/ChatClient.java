// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package client;

import ocsf.client.*;
import common.*;

import java.io.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient {
    //Instance variables **********************************************

    /**
     * The interface type variable.  It allows the implementation of
     * the display method in the client.
     */
    ChatIF clientUI;
    String logInID;

    //Constructors ****************************************************

    /**
     * Constructs an instance of the chat client.
     *
     * @param host     The server to connect to.
     * @param port     The port number to connect on.
     * @param clientUI The interface type variable.
     */

    public ChatClient(String logInID, String host, int port, ChatIF clientUI)
            throws IOException {
        super(host, port); //Call the superclass constructor
        this.clientUI = clientUI;
        this.logInID = logInID;
    }


    //Instance methods ************************************************

    /**
     * This method handles all data that comes in from the server.
     *
     * @param msg The message from the server.
     */
    public void handleMessageFromServer(Object msg) {
        clientUI.display(msg.toString());
    }

    /**
     * This method handles all data coming from the UI
     * *
     *
     * @param message The message from the UI.
     */
    public void handleMessageFromClientUI(String message) {
        try {
            sendToServer(message);
        } catch (IOException e) {
            clientUI.display
                    ("Could not send message to server.");
            quit();
        }
    }

    /**
     * This method terminates the client.
     */
    public void quit() {
        try {
            closeConnection();
        } catch (IOException e) {
        }
        System.exit(0);
    }

    /**
     * This method disconnects the client.
     */
    public void logOff() {
        try {
            closeConnection();
        } catch (IOException e) {
        }
    }

    /**
     * This method sets a new login ID
     * @param logInID
     */
    public void setLogInID(String logInID) {
        this.logInID = logInID;
    }

    /**
     * this method returns the login ID
     * @return
     */
    public String getLogInID() {
        return logInID;
    }

    /**
     * This method displays a message when the connection closes between a client and a server
     */
    public void connectionClosed() {
        clientUI.display("Connection closed.");
    }

    /**
     * this method displays a  message when the server shuts down unexpectedly and terminates the client
     * @param e
     */
    public void connectionException(Exception e) {
        clientUI.display("Server has shut down.");
        quit(); //can be modified to disconnect instead of quiting by switching quit() with logOff()
    }

    /**
     * this method open's the connection to the server. This was separated
     * from the constructor to allow the creation of a client without
     * it being connected to the server.
     */
    public void connectClient() {
        try {
            openConnection();
            this.sendToServer("#login " + logInID);
        }catch (Exception e){
            clientUI.display("Cannot open connection.  Awaiting command.");
        }
    }
}
//End of ChatClient class
