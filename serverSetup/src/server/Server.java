/* CHATROOM AssignClientIDMessage.java
 * EE422C Project 7 submission by
 * Pranav Harathi
 * sh44674
 * 16460
 * Slip days used: 1
 * Fall 2016
 */

package server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * Server class sets up connection with clients and uses Observer design pattern
 * to send and receive messages.
 */
public class Server extends Observable {

    int port = 8007;
    ServerSocket server;
    int idCounter = 0;
    int groupIdCtr = 0;
    List<String> clients = new ArrayList<>(); // list of all clients with client name + id
    List<UserGroup> groups = new ArrayList<>(); // list of all groups

    // need to add "You" group
    void addClient(String clientidName) {
        clients.add(clientidName);
        setChanged();
        notifyObservers(new UserListMessage(clients));
    }

    void removeClient(String clientidName) {
        System.out.println("bef remove: " + clients);
        clients.remove(clientidName);
        System.out.println("after remove: " + clients);
        setChanged();
        notifyObservers(new UserListMessage(clients));
    }

    void addGroup(String clientId, List<String> members) {
        // verify group
        boolean valid = true;
        for(String s : members) {
            if(!clients.contains(s)) {
                valid = false;
                break;
            }
        }
        ++groupIdCtr;
        UserGroup g = new UserGroup("g" + groupIdCtr, members);
        GroupCreateMessage gMessage;
        if(valid) {
            groups.add(g);
            gMessage = new GroupCreateMessage("Group" + g.getGroupId(), clientId, g.getMemberIds());
        } else {
            gMessage = new GroupCreateMessage("NULL", clientId, null);
        }
        setChanged();
        notifyObservers(gMessage);
    }

    void runserver() {
        try {
            // Create a server socket, and define in and out streams for it
            server = new ServerSocket(port);

            while(true) {
                Socket clientSocket = server.accept();
                ClientObserver writer = new ClientObserver(
                        clientSocket.getOutputStream(), Integer.toString(++idCounter), this
                );
                Thread t = new Thread(new ClientHandler(clientSocket));
                t.start();
                this.addObserver(writer);
                System.out.println("got a connection");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class ClientHandler implements Runnable {
        private BufferedReader reader;

        public ClientHandler(Socket clientSocket) {
            Socket sock = clientSocket;
            try {
                reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /*
         * Receives message and notifies observer
         */
        public void run() {
            String message;
            try {
                while((message = reader.readLine()) != null) {
                    System.out.println("server read " + message);
                    if(message.split(" ")[0].equals("delete")){
                        removeClient(message.split(" ")[1]);
                        continue;
                    }
                    setChanged();
                    notifyObservers(Message.getObject(message));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
