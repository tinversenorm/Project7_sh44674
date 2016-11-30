/* CHATROOM UserGroup.java
 * EE422C Project 7 submission by
 * Pranav Harathi
 * sh44674
 * 16460
 * Slip days used: 1
 * Fall 2016
 */

package client;

import java.util.List;

/**
 * Stores info about a user group
 */
public class UserGroup {
    private String groupId;
    private List<String> memberIds;

    public String getGroupId() {
        return groupId;
    }

    public List<String> getMemberIds() {
        return memberIds;
    }

    public UserGroup(String gId, List<String> members) {
        groupId = gId;
        memberIds = members;
    }

    public boolean inGroup(String id) {
        return memberIds.contains(id);
    }
}
