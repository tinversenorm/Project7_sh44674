package server;

import java.io.*;
import java.net.*;

/**
 * Created by pranavh on 11/28/16.
 */
public class Server {

    int port;
    DataInputStream in;
    DataOutputStream out;
    ServerSocket server;
    Socket socket;

    void runserver() {
        try {
            // Create a server socket, and define in and out streams for it
            server = new ServerSocket(port);
            socket = server.accept();
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            // loop keeps reading from client, squares it, and sends the result
            // back to the client.
            while (true) {
                Double msg = in.readDouble();
                System.out.println("Server received " + msg);
                out.writeDouble(msg*msg);
                out.flush();
                System.out.println("Server: I wrote " + msg*msg + " to client.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
