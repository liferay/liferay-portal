//Adds i users with i roles
//Example: 1user user1 with Role 1 role

import com.liferay.portal.kernel.dao.orm.*
import com.liferay.portal.kernel.service.ServiceContext
import com.liferay.portal.kernel.util.*
import com.liferay.portal.kernel.workflow.*
import com.liferay.portal.kernel.model.*
import java.util.*

companyId = com.liferay.portal.kernel.util.PortalUtil.getCompanyId(actionRequest)
userId = com.liferay.portal.kernel.util.PortalUtil.getUserId(actionRequest)
group = com.liferay.portal.kernel.service.GroupLocalServiceUtil.getGroup(companyId, "Guest");
numberOfUsers = 0
groupId = group.getGroupId();
serviceContext = new com.liferay.portal.kernel.service.ServiceContext();
serviceContext.setCompanyId(companyId)
serviceContext.setScopeGroupId(group.getGroupId())
serviceContext.setUserId(userId)

for (i =1; i <= numberOfUsers; i++) {
boolean autoPassword = false;
String password = "123";
String screenName = i + "user";
String emailAddress = i + "user@liferay.com";
String firstName = i + "user";
String middleName = "";
String lastName = "user" + i;
long prefixListTypeId = 0;
long suffixListTypeId = 0;
boolean male = true;
int birthdayMonth = 1;
int birthdayDay = 1;
int birthdayYear = 1970;
String jobTitle = "";
long[] organizationIds = null;
long[] roleIds = null;
long[] userGroupIds = null;
boolean sendMail = false;
long[] groudIds = [groupId];

//create a user
groupUser = com.liferay.portal.kernel.service.UserLocalServiceUtil.addUser(
	0L, companyId, autoPassword, password, password,
	false, screenName, emailAddress, java.util.Locale.US, firstName,
	middleName, lastName, prefixListTypeId, suffixListTypeId, male,
	birthdayMonth, birthdayDay, birthdayYear, jobTitle,
	com.liferay.portal.kernel.model.UserConstants.TYPE_REGULAR, groudIds,
	organizationIds, roleIds, userGroupIds, sendMail, serviceContext);

role = com.liferay.portal.kernel.service.RoleLocalServiceUtil.getRole(companyId, "Administrator");

//assign admin role to new user
long [] userIds = [groupUser.getUserId()];
com.liferay.portal.kernel.service.UserLocalServiceUtil.addRoleUser(role.getRoleId(), groupUser);
com.liferay.portal.kernel.service.UserGroupRoleLocalServiceUtil.addUserGroupRoles(userIds, groupId, role.getRoleId());

//create a new Role and assign it to created user
java.util.Map<java.util.Locale, String> titleMap = new java.util.HashMap<java.util.Locale, String>();
titleMap.put(java.util.Locale.US, i + "Role");
com.liferay.portal.kernel.model.Role newRole = com.liferay.portal.kernel.service.RoleLocalServiceUtil.addRole(null, userId, null, 0, i + "Role", titleMap, titleMap, 1, null, null);
com.liferay.portal.kernel.service.UserLocalServiceUtil.addRoleUser(newRole.getRoleId(), groupUser);
}