package server;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;


public class ClientObserver extends PrintWriter implements Observer {
    public String clientId;
    private String clientName;
    private HashSet<String> groups = new HashSet<String>();
    private Server server;

    public ClientObserver(OutputStream out, String id, Server s) {
        super(out);
        clientId = id;
        server = s;
        groups.add(clientId);
        this.println(new AssignClientIDMessage(clientId));
        this.flush();
    }

    // Object arg is a message or null
    @Override
    public void update(Observable o, Object arg) {
        if(arg instanceof Message) {
            if(arg instanceof NameCreateMessage) {
                NameCreateMessage m = (NameCreateMessage) arg;
                if(m.getId().equals(clientId)) {
                    this.clientName = ((NameCreateMessage) arg).getName();
                    server.addClient(clientName + clientId);
                    server.groups.add(new UserGroup(clientId, Collections.singletonList(clientName+clientId)));
                    System.out.println("Name set");
                    System.out.println(server.clients);
                }
            } else if(arg instanceof UserListMessage) {
                this.println(arg);
                this.flush();
            } else if(arg instanceof GroupCreateMessage.Request) {
                GroupCreateMessage.Request m = (GroupCreateMessage.Request) arg;
                if(m.getClientId().equals(clientId)) {
                    server.addGroup(m.getClientId(), m.getGroupMembers());
                    System.out.println("Group create request received: " + m.getGroupMembers());
                }
            } else if(arg instanceof GroupCreateMessage) {
                GroupCreateMessage m = (GroupCreateMessage) arg;
                /*if(m.getGroupMembers() == null && clientId.equals(m.getClientId())) {
                    this.println(m);
                    this.flush();
                }*/
                System.out.println("Group members: " + m.getGroupMembers());
                if (clientId.equals(m.getClientId()) || m.getGroupMembers().contains(clientName + clientId)) {
                    this.println(m);
                    this.flush();
                    System.out.println("Outgoing: " + m);
                }
            } else if(arg instanceof ChatMessage) {
                ChatMessage m = (ChatMessage) arg;
                String gId = m.getGroupID();
                for(UserGroup g : server.groups) {
                    if(g.getGroupId().equals(gId.replace("Group",""))) {
                        if(g.inGroup(clientName + clientId)){
                            this.println(m);
                            this.flush();
                            break;
                        }
                    }
                }
            } else {
                this.println(arg);
                this.flush();
            }
        }
    }
}
