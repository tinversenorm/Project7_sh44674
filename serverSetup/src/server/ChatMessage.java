/* CHATROOM ChatMessage.java
 * EE422C Project 7 submission by
 * Pranav Harathi
 * sh44674
 * 16460
 * Slip days used: 1
 * Fall 2016
 */

package server;

/**
 * Sent and received whenever a message is sent in a chat group.
 */
public class ChatMessage extends Message {
    //private static final long serialVersionUID = -2141489888176900128L;
    private String clientID;
    private String groupID;
    private String content;

    public String getClientID() {
        return clientID;
    }

    public String getGroupID() {
        return groupID;
    }

    public String getContent() {
        return content;
    }

    public ChatMessage(String cID, String gID, String content) {
        super("chat-message");
        clientID = cID;
        groupID = gID;
        this.content = content;
    }

    public String toString() {
        return String.format(super.toString() + "|{\"clientID\":\"%s\",\"groupID\":\"%s\",\"content\":\"%s\"}", clientID, groupID, content);
    }

    public static ChatMessage getObject(String json) {
        String[] fields = json.substring(1, json.length() - 1).split(",");
        String cID = fields[0].split(":")[1].replace("\"", "");
        String gID = fields[1].split(":")[1].replace("\"", "");
        String message = "";
        for(int i = 2; i < fields.length; i++) message += fields[i] + ",";
        message = message.substring(0, message.lastIndexOf(","));
        message = message.split(":")[1].replace("\"", "");
        return new ChatMessage(cID, gID, message);
    }
}
