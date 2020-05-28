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
    public ClientConsole(String host, int port) {
        try {
            client = new ChatClient(host, port, this);
        } catch (IOException exception) {
            System.out.println("Error: Can't setup connection!"
                    + " Terminating client.");
            System.exit(1);
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
                if (!message.startsWith("#")) {
                    client.handleMessageFromClientUI(message);
                } else {
                    message = message.substring(1);
                    if (message.equals("quit")) {
                        client.quit();
                    } else if (message.equals("logoff")) {
                        client.logOff();
                    } else if (message.startsWith("setport ")) {
                        if (client.isConnected()) {
                            System.out.println("Please Log-off first.");
                        } else {
                            try {
                                message = message.substring(8);
                                System.out.println(message);
                                client.setPort(Integer.parseInt(message));
                                System.out.println(client.getPort());
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid input. Using default port");
                                client.setPort(DEFAULT_PORT);
                            }
                        }
                    } else if (message.startsWith("sethost ")) {
                        if (client.isConnected()) {
                            System.out.println("Please Log-off first.");
                        } else {
                            client.setHost(message.substring(7));
                        }
                    } else if (message.equals("login")) {
                        if (client.isConnected()) {
                            System.out.println("Client already connected.");
                        } else {
                            try {
                                client.openConnection();
                            } catch (Exception e) {
                                System.out.println("Unable to connect to port " + client.getPort() + " and host" + client.getHost() +". Connecting to default port " + DEFAULT_PORT + " and default host localhost");
                                client.setPort(DEFAULT_PORT);
                                client.setHost("localhost");
                                client.openConnection();
                            }
                        }
                    } else if (message.equals("gethost")) {
                        System.out.println("The current host name is: " + client.getHost());
                    } else if (message.equals("getport")) {
                        System.out.println("The current port is: " + client.getPort());
                    }
                }

            }
        } catch (Exception ex) {
            System.out.println
                    ("Unexpected error while reading from console!");
        }
    }

    /**
     * This method overrides the method in the ChatIF interface.  It
     * displays a message onto the screen.
     *
     * @param message The string to be displayed.
     */
    public void display(String message) {
        System.out.println("> " + message);
    }


    //Class methods ***************************************************

    /**
     * This method is responsible for the creation of the Client UI.
     *
     * @param args[0] The host to connect to.
     */
    public static void main(String[] args) {
        String host = "";
        int port = 0;  //The port number
        Scanner myPort = new Scanner(System.in);
        System.out.println("Enter Port Address: ");
        String validPort = myPort.nextLine();
        if (!validPort.isEmpty()) {

            try {
                port = Integer.parseInt(validPort);

            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Using default port");
            }
        }
        if (port == 0) {
            port = DEFAULT_PORT;
        }
        System.out.println("Connecting to port: " + port);
        try {
            host = args[0];
        } catch (ArrayIndexOutOfBoundsException e) {
            host = "localhost";
        }
        ClientConsole chat = new ClientConsole(host, port);
        chat.accept();  //Wait for console data

    }
}
//End of ConsoleChat class
