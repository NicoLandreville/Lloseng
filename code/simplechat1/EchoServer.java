// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.*;
import java.util.Arrays;

import common.ChatIF;
import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */
public class EchoServer extends AbstractServer implements ChatIF {
    //Class variables *************************************************

    /**
     * The default port to listen on.
     */
    final public static int DEFAULT_PORT = 5555;
    //Instance variables *************************************************

    /**
     * The default port to listen on.
     */
    Arrays clients;
    //Constructors ****************************************************

    /**
     * Constructs an instance of the echo server.
     *
     * @param port The port number to connect on.
     */
    public EchoServer(int port) {
        super(port);
    }


    //Instance methods ************************************************

    /**
     * The void speaketh
     */
    public void serverConsole() {
        try {
            BufferedReader fromConsole =
                    new BufferedReader(new InputStreamReader(System.in));
            String message;

            while (true) {
                message = fromConsole.readLine();
                if (!message.startsWith("#")) {  //if message doesn't start with # it deals with it as a object to display
                    display(message);
                } else {
                    message = message.substring(1); //removes the # from the string
                    if (message.equals("quit")) {
                        this.close();
                        this.sendToAllClients("#quit");
                        System.exit(0);
                    } else if (message.equals("stop")) {
                        this.stopListening();
                        sendToAllClients("WARNING - Server has stopped listening for connections."); //sends message from server side to be somewhat backwards compatible
                    } else if (message.startsWith("setport ")) {
                        if (this.isListening()) {
                            System.out.println("Please close server first.");
                        } else {
                            try {
                                message = message.substring(8);
                                this.setPort(Integer.parseInt(message));
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid input. Using default port.");
                                this.setPort(DEFAULT_PORT);
                            }
                            System.out.println("port set to: : " + getPort());
                        }
                    } else if (message.equals("close")) {
                        this.stopListening();
                        sendToAllClients("SERVER SHUTTING DOWN! DISCONNECTING!");
                        sendToAllClients("Abnormal termination of connection.");
                        sendToAllClients("#quit");
                        this.close();
                    } else if (message.equals("start")) {
                        if (this.isListening()) {
                            System.out.println("Server is already listening.");
                        } else {
                            this.listen();
                        }
                    } else if (message.equals("getport")) {
                        System.out.println(getPort());
                    } else if (message.equals("help")) {
                        System.out.println("#quit: Server terminates.");
                        System.out.println("#stop: Server no longer listens for new connections.");
                        System.out.println("#start: Server will listen for connections.");
                        System.out.println("#close: Server stops listening and terminates all clients.");
                        System.out.println("#setport <port>: Set a new port address.");
                        System.out.println("#getport: Returns port address.");

                    } else {
                        System.out.println("Unrecognized command. For help type #help.");
                    }

                }

            }
        } catch (Exception ex) {
            System.out.println
                    ("Unexpected error while reading from console!");
        }
    }

    /**
     * This method overrides the method in the ChatIF interface. It
     * displays a message onto the screen and sends the same message
     * to be displayed by the clients.
     *
     * @param message
     */
    public void display(String message) {
        System.out.println("SERVER MSG > " + message);
        this.sendToAllClients("SERVER MSG > " + message);
    }

    /**
     * This method handles any messages received from the client.
     *
     * @param msg    The message received from the client.
     * @param client The connection from which the message originated.
     */
    public void handleMessageFromClient(Object msg, ConnectionToClient client) {
        String message;
        message = msg.toString();
        if (message.startsWith("#login ")) {
            if (client.getInfo("logInID") == null) {
                String logInID = message.substring(7);
                System.out.println("Message received: " + message + " from " + client.getInfo("logInID"));
                client.setInfo("logInID", logInID);
                System.out.println(client.getInfo("logInID") + " has logged on");
                sendToAllClients(client.getInfo("logInID") + " has logged on");
            } else {
                try {
                    client.sendToClient("You are already logged in.");
                } catch (Exception e) {
                }
            }
        } else {
            if (client.getInfo("logInID") == null) {
                try {
                    client.sendToClient("No log-in ID detected. Terminating client.");
                    client.close();
                } catch (Exception e) {
                }
                return;
            }
            String logInID = String.valueOf(client.getInfo("logInID"));
            //message = message.substring(4); //need to automatically detect size, not just 4
            System.out.println("Message received: " + message + " from " + client + " with log-in ID " + logInID);

            this.sendToAllClients(logInID + " > " + message);
        }
    }

    /**
     * This method is called every time a new client tries to connect
     *
     * @param client the connection connected to the client.
     */
    protected void clientConnected(ConnectionToClient client) {
        System.out.println("A new client is attempting to connect to the server.");
    }

    /**
     * This method is called every time a client disconnects,
     * also prints out the same message on all client consoles.
     *
     * @param client the connection with the client.
     */
    synchronized protected void clientDisconnected(
            ConnectionToClient client) {
        System.out.println(client.getInfo("logInID") + " has disconnected");
        this.sendToAllClients(client.getInfo("logInID") + " has disconnected");
    }

    /**
     * Calls a method in the client that reacts to an unexpected quitting from the server
     *
     * @param client the client that raised the exception.
     * @param exception
     */
    synchronized protected void clientException(
            ConnectionToClient client, Throwable exception) {
        clientDisconnected(client);
    }

    /**
     * This method overrides the one in the superclass.  Called
     * when the server starts listening for connections.
     */
    protected void serverStarted() {
        System.out.println
                ("Server listening for connections on port " + getPort());
    }

    /**
     * This method overrides the one in the superclass.  Called
     * when the server stops listening for connections.
     */
    protected void serverStopped() {
        System.out.println
                ("Server has stopped listening for connections.");
    }

    //Class methods ***************************************************

    /**
     * This method is responsible for the creation of
     * the server instance (there is no UI in this phase).
     *
     * @param args[0] The port number to listen on.  Defaults to 5555
     *                if no argument is entered.
     */
    @SuppressWarnings("JavadocReference")
    public static void main(String[] args) {
        int port = 0; //Port to listen on

        try {
            port = Integer.parseInt(args[0]); //Get port from command line
        } catch (Throwable t) {
            port = DEFAULT_PORT; //Set port to 5555
        }

        EchoServer sv = new EchoServer(port);

        try {
            sv.listen(); //Start listening for connections
        } catch (Exception ex) {
            System.out.println("ERROR - Could not listen for clients!");
        }
        sv.serverConsole();
    }
}
//End of EchoServer class
