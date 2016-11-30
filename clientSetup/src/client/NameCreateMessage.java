/* CHATROOM NameCreateMessage.java
 * EE422C Project 7 submission by
 * Pranav Harathi
 * sh44674
 * 16460
 * Slip days used: 1
 * Fall 2016
 */

package client;

/**
 * Sent by client to inform server of client name.
 */
public class NameCreateMessage extends Message {
    private String clientName;
    private String clientId;

    public String getName() {
        return clientName;
    }

    public String getId() {
        return clientId;
    }

    public NameCreateMessage(String name, String id) {
        super("name-create-message");
        clientName = name;
        clientId = id;
    }

    public String toString() {
        return String.format(super.toString() + "|{\"clientName\":\"%s\"" +
                ",\"clientId\":\"%s\"}", clientName, clientId);
    }

    public static NameCreateMessage getObject(String json) {
        String[] info = json.substring(1, json.length() - 1).split(",");
        String clientName = info[0].split(":")[1].replace("\"", "");
        String clientId = info[1].split(":")[1].replace("\"","");
        return new NameCreateMessage(clientName, clientId);
    }
}
