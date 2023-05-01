package com.cimss.project.database;

import java.util.List;

import com.cimss.project.database.entity.Group;
import com.cimss.project.database.entity.Member;
import com.cimss.project.database.entity.User;

public interface MemberService {

	String join(String instantMessagingSoftware,String instantMessagingSoftwareUserId,String groupId);

	String joinWithProperty(String instantMessagingSoftware,String instantMessagingSoftwareUserId,String groupId);

	String leave(String instantMessagingSoftware,String instantMessagingSoftwareUserId,String groupId);

	String grantPermission(String instantMessagingSoftware,String instantMessagingSoftwareUserId,String groupId);

	String revokePermission(String instantMessagingSoftware,String instantMessagingSoftwareUserId,String groupId);
	
	List<User> getUsers(String groupId);
	
	List<Group> getGroups(String instantMessagingSoftware,String instantMessagingSoftwareUserId);

	List<Member> getAllMembers();

	void deleteAllMembers();

	boolean isMember(String instantMessagingSoftware,String instantMessagingSoftwareUserId,String groupId);
	boolean isManager(String instantMessagingSoftware,String instantMessagingSoftwareUserId,String groupId);
}
