/* CHATROOM AssignClientIDMessage.java
 * EE422C Project 7 submission by
 * Pranav Harathi
 * sh44674
 * 16460
 * Slip days used: 1
 * Fall 2016
 */

package server;

/**
 * Message that assigns a client ID to a client socket.
 */
public class AssignClientIDMessage extends Message {
    private String clientId;

    public String getClientId() {
        return clientId;
    }

    public AssignClientIDMessage(String clientId) {
        super("assign-client-id-message");
        this.clientId = clientId;
    }

    public String toString() {
        return String.format(super.toString() + "|{\"clientId\":\"%s\"}", clientId);
    }

    public static AssignClientIDMessage getObject(String json) {
        return new AssignClientIDMessage(
                json.substring(1, json.length() - 1).split(":")[1].replace("\"", "")
        );
    }
}
