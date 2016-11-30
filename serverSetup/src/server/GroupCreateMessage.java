/* CHATROOM GroupCreateMessage.java
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
 * Sent and Received when a group is created.
 */
public class GroupCreateMessage extends Message {
    private String clientId;
    private String groupNameId;
    private List<String> groupClientIds;

    public String getClientId() {
        return clientId;
    }

    public List<String> getGroupMembers() {
        return groupClientIds;
    }

    public String getGroupNameId() {
        return groupNameId;
    }

    public GroupCreateMessage(String groupNameId, String cId, List<String> group) {
        super("group-create-message");
        this.groupNameId = groupNameId;
        clientId = cId;
        groupClientIds = group;
    }

    public String toString() {
        StringBuilder out = new StringBuilder(super.toString() + "|{" +
                "\"groupNameId\":\"" + groupNameId + "\"," +
                "\"clientId\":\"" + clientId + "\",\"users\":");
        out.append(groupClientIds.toString().replaceAll("\\s", ""));
        out.append("}");
        return out.toString();
    }

    public static GroupCreateMessage getObject(String json) {
        String info[] = json.substring(1, json.length() - 1).split(",");
        String groupNameId = info[0].split(":")[1].replace("\"", "");
        String clientId = info[1].split(":")[1].replace("\"", "");
        if(clientId.equals("null")) throw new NullPointerException(); //debug
        String userList = "";
        List<String> users = new ArrayList<>();
        for(int i = 2; i < info.length; i++) userList += info[i] + ",";
        userList = userList.substring(0, userList.lastIndexOf(","));
        for(String s : userList.split(":")[1].replace("\"", "").split(",")) {
            users.add(s.replace("[", "").replace("]",""));
        }
        return new GroupCreateMessage(groupNameId, clientId, users);
    }

    public static class Request extends Message {
        private String clientId;
        private String groupName;
        private List<String> groupClientIds;

        public String getClientId() {
            return clientId;
        }

        public List<String> getGroupMembers() {
            return groupClientIds;
        }

        public Request(String groupName, String cId, List<String> group) {
            super("group-create-message-request");
            this.groupName = groupName;
            clientId = cId;
            groupClientIds = group;
        }

        public String toString() {
            StringBuilder out = new StringBuilder(super.toString() + "|{" +
                    "\"groupNameId\":\"" + groupName + "\"," +
                    "\"clientId\":\"" + clientId + "\",\"users\":");
            out.append(groupClientIds.toString().replaceAll("\\s", ""));
            out.append("}");
            return out.toString();
        }

        public static Request getObject(String json) {
            String info[] = json.substring(1, json.length() - 1).split(",");
            String groupName = info[0].split(":")[1].replace("\"", "");
            String clientId = info[1].split(":")[1].replace("\"", "");
            if(clientId.equals("null")) throw new NullPointerException(); //debug
            String userList = "";
            List<String> users = new ArrayList<>();
            for(int i = 2; i < info.length; i++) userList += info[i] + ",";
            userList = userList.substring(0, userList.lastIndexOf(","));
            for(String s : userList.split(":")[1].replace("\"", "").split(",")) {
                users.add(s.replace("[", "").replace("]",""));
            }
            return new Request(groupName, clientId, users);
        }
    }
}
