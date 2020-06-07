import common.ChatIF;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class serverConsole extends EchoServer implements ChatIF {


    /**
     * Constructs an instance of the echo server.
     *
     * @param port The port number to connect on.
     */
    public serverConsole(int port) {
        super(port);
    }

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
                            System.out.println("port set to: " + getPort());
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
}
