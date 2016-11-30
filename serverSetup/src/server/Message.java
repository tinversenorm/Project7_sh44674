/* CHATROOM Message.java
 * EE422C Project 7 submission by
 * Pranav Harathi
 * sh44674
 * 16460
 * Slip days used: 1
 * Fall 2016
 */

package server;

/**
 * Abstract superclass for all message objects.  Sets up message type and routes getObject calls.
 */
public abstract class Message {
    private String messageType;

    public String getType() {
        return messageType;
    }

    public Message(String mtype) {
        messageType = mtype;
    }

    public String toString() {
        return String.format("{\"%s\":\"%s\"}", "messageType", messageType);
    }

    public static Message getObject(String json) {
        if(!json.contains("|")) return null;
        String[] data = json.trim().split("\\|");
        String messageType = data[0].substring(1, data[0].length() - 1).split(":")[1].replace("\"", "");
        String msginfojson = "";
        for(int i = 1; i < data.length; i++) msginfojson += data[i] + "|";
        msginfojson = msginfojson.substring(0, msginfojson.lastIndexOf("|"));
        switch(messageType) {
            case "chat-message" : return ChatMessage.getObject(msginfojson);
            case "name-create-message" : return NameCreateMessage.getObject(msginfojson);
            case "assign-client-id-message" : return AssignClientIDMessage.getObject(msginfojson);
            case "user-list-message" : return UserListMessage.getObject(msginfojson);
            case "group-create-message-request" : return GroupCreateMessage.Request.getObject(msginfojson);
            case "group-create-message" : return GroupCreateMessage.getObject(msginfojson);
            default: return null;
        }
    }
}
