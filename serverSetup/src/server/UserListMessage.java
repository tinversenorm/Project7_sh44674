/* CHATROOM UserListMessage.java
 * EE422C Project 7 submission by
 * Pranav Harathi
 * sh44674
 * 16460
 * Slip days used: 1
 * Fall 2016
 */

package server;

import java.util.ArrayList;
import java.util.List;

/**
 * Sent when list of users changes.
 */
public class UserListMessage extends Message {
    private List<String> users;
    private String clientId;

    public List<String> getUsers() {
        return users;
    }

    public UserListMessage(List<String> users) {
        super("user-list-message");
        this.users = users;
    }

    public UserListMessage(String clientId, List<String> users) {
        super("user-list-message");
        this.users = users;
        this.clientId = clientId;
    }

    public String toString() {
        StringBuilder out = new StringBuilder(super.toString() + "|{" +
                "\"clientId\":\"" + clientId + "\",\"users\":");
        out.append(users.toString().replaceAll("\\s", ""));
        out.append("}");
        return out.toString();
    }

    // Ex. {"clientId":"12","users":"[me,you,him]"}
    public static UserListMessage getObject(String json) {
        String info[] = json.substring(1, json.length() - 1).split(",");
        String clientId = info[0].split(":")[1].replace("\"", "");
        if(clientId.equals("null")) clientId = null;
        String userList = "";
        List<String> users = new ArrayList<>();
        for(int i = 1; i < info.length; i++) userList += info[i] + ",";
        userList = userList.substring(0, userList.lastIndexOf(","));
        for(String s : userList.split(":")[1].replace("\"", "").split(",")) {
            users.add(s.replace("[", "").replace("]",""));
        }
        return clientId == null ? new UserListMessage(users) : new UserListMessage(clientId, users);
    }

}
