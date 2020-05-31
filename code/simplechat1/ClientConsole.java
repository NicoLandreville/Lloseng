// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.*;
import java.util.Scanner;

import client.*;
import common.*;

/**
 * This class constructs the UI for a chat client.  It implements the
 * chat interface in order to activate the display() method.
 * Warning: Some of the code here is cloned in ServerConsole
 *
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @version July 2000
 */
public class ClientConsole implements ChatIF {
    //Class variables *************************************************

    /**
     * The default port to connect on.
     */
    final public static int DEFAULT_PORT = 5555;

    //Instance variables **********************************************

    /**
     * The instance of the client that created this ConsoleChat.
     */
    ChatClient client;

    //Constructors ****************************************************

    /**
     * Constructs an instance of the ClientConsole UI.
     *
     * @param host The host to connect to.
     * @param port The port to connect on.
     */
    public ClientConsole(String logInID, String host, int port) {
        try {
            client = new ChatClient(logInID, host, port, this);
            client.connectClient(); //separated the client connection to server so that a client can be created without a server running
        } catch (IOException exception) {
            System.out.println("Cannot open connection.  Awaiting command.");
            // message for test case 2, if connection is not possible the code will not wait for input as it would have to create a new client but the commands
            // require to create a new client require that a client already exist
            //System.out.println("Error: Can't setup connection!" + " Terminating client.");   //testcase 2002 states a new message and instead of terminating the client it now waits for commands
            //System.exit(1);
        }
    }


    //Instance methods ************************************************

    /**
     * This method waits for input from the console.  Once it is
     * received, it sends it to the client's message handler.
     */
    public void accept() {
        try {
            BufferedReader fromConsole =
                    new BufferedReader(new InputStreamReader(System.in));
            String message;
            while (true) {
                message = fromConsole.readLine();
                if (!message.startsWith("#")) { //if message doesn't start with # it deals with it as a object to display
                    client.handleMessageFromClientUI(message);
                } else {
                    message = message.substring(1); //removes the # from the string
                    if (message.equals("quit")) {
                        client.quit();
                    } else if (message.equals("logoff")) {
                        client.logOff();
                    } else if (message.startsWith("setport ")) {
                        if (client.isConnected()) {
                            System.out.println("Please Log-off first.");
                        } else {
                            try {
                                message = message.substring(8); //keeps only the value of the new port
                                client.setPort(Integer.parseInt(message));
                                System.out.println("New port: " +client.getPort());
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid input. Using default port");
                                client.setPort(DEFAULT_PORT);
                            }
                        }
                    } else if (message.startsWith("sethost ")) {
                        if (client.isConnected()) {
                            System.out.println("Please Log-off first.");
                        } else {
                            client.setHost(message.substring(8)); //keeps only the value of the new host
                            System.out.println("New host: " + client.getHost());
                        }
                    } else if (message.equals("login")) {
                        if (client.isConnected()) {
                            System.out.println("Client already connected.");

                        } else {
                            try {
                                client.openConnection();
                                System.out.println("Connection established.");
                                client.sendToServer("#login " + client.getLogInID()); //re-sends a log in attempt, this counts as a new connection
                            } catch (Exception e) { //if values given do not connect to server, the client will try to connect by using default data
                                System.out.println("Unable to connect to port " + client.getPort() + " and host " + client.getHost() + ". Connecting to default port " + DEFAULT_PORT + " and default host localhost");
                                client.setPort(DEFAULT_PORT);
                                client.setHost("localhost");
                                client.openConnection();
                                client.sendToServer("#login " + client.getLogInID());
                            }
                        }
                    } else if (message.equals("gethost")) {
                        System.out.println("The current host name is: " + client.getHost());
                    } else if (message.equals("getport")) {
                        System.out.println("The current port is: " + client.getPort());
                    } else if (message.equals("help")) {
                        System.out.println("#quit: Client terminates.");
                        System.out.println("#logoff: Connection to server terminates.");
                        System.out.println("#sethost <host>: Set a new host name.");
                        System.out.println("#setport <port>: Set a new port address.");
                        System.out.println("#login: Connect to server.");
                        System.out.println("#gethost: Returns host name.");
                        System.out.println("#getport: Returns port address.");
                    } else {
                        System.out.println("Unrecognized command. For help type #help");
                    }
                }

            }
        } catch (
                Exception ex) {
            System.out.println
                    ("Unexpected error while reading from console!");
        }

    }


    /**
     * This method overrides the method in the ChatIF interface.  It
     * displays a message onto the screen. It also deals with the disconnect
     * command from the server.
     *
     * @param message The string to be displayed.
     */
    public void display(String message) {
        if (message.equals("#quit")) {
            client.logOff();
        } else {
            System.out.println(message);
        }
    }


    //Class methods ***************************************************

    /**
     * This method is responsible for the creation of the Client UI.
     *
     * @param args[0] The login ID.
     * @param args[1] The host to connect to.
     * @param args[2] The port to connect to.
     */
    @SuppressWarnings("JavadocReference")
    public static void main(String[] args) {
        String logInID = null;
        String host = "";
        int port = 0;  //The port number
        try {
            logInID = args[0];
        } catch (Exception e) {
            System.out.println("ERROR - No login ID specified.  Connection aborted.");
            System.exit(1);
        }
        try {
            host = args[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Invalid input. Using default host");
            host = "localhost";
        }
        System.out.println("Connecting to host: " + host);
        try {
            String validPort = args[2];
            port = Integer.parseInt(validPort);

        } catch (Exception e) {
            System.out.println("Invalid input. Using default port");
            port = DEFAULT_PORT;
        }
        System.out.println("Connecting to port: " + port);

        ClientConsole chat = new ClientConsole(logInID, host, port);
        chat.accept();  //Wait for console data

    }
}
//End of ConsoleChat class
