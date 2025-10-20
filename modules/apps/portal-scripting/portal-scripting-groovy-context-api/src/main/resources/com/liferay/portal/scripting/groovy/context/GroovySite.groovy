/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.scripting.groovy.context;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.NoSuchTeamException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Team;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.TeamLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;

/**
 * @author Michael C. Han
 */
class GroovySite {

	static GroovySite openSite(String name, String description) {
		GroovySite groovySite = new GroovySite();

		groovySite.name = name;
		groovySite.description = description;
		groovySite.type = GroupConstants.TYPE_SITE_OPEN;

		return groovySite;
	}

	static GroovySite privateSite(String name, String description) {
		GroovySite groovySite = new GroovySite();

		groovySite.name = name;
		groovySite.description = description;
		groovySite.type = GroupConstants.TYPE_SITE_PRIVATE;

		return groovySite;
	}

	static GroovySite restrictedSite(String name, String description) {
		GroovySite groovySite = new GroovySite();

		groovySite.name = name;
		groovySite.description = description;
		groovySite.type = GroupConstants.TYPE_SITE_RESTRICTED;

		return groovySite;
	}

	void addOrganizations(
		GroovyScriptingContext groovyScriptingContext,
		String... organizationNames) {

		for (String organizationName : organizationNames) {
			Organization organization = GroovyOrganization.fetchOrganization(
				groovyScriptingContext, organizationName);

			if (organization != null) {
				GroupLocalServiceUtil.addOrganizationGroup(
					organization.getOrganizationId(), group);
			}
		}
	}

	void addTeamUserGroupMembers(
		GroovyScriptingContext groovyScriptingContext, String teamName,
		String... userGroupNames) {

		Team team = null;

		try {
			team = TeamLocalServiceUtil.getTeam(group.getGroupId(), teamName);
		}
		catch (NoSuchTeamException nste) {
			team = TeamLocalServiceUtil.addTeam(
				groovyScriptingContext.guestUserId, group.getGroupId(),
				teamName, null);
		}

		for (String userGroupName : userGroupNames) {
			UserGroup userGroup = GroovyUserGroup.fetchUserGroup(
				groovyScriptingContext, userGroupName);

			if (userGroup != null) {
				TeamLocalServiceUtil.addUserGroupTeam(
					userGroup.getUserGroupId(), team);
			}
		}
	}

	void addTeamUserMembers(
		GroovyScriptingContext groovyScriptingContext, String teamName,
		String... screenNames) {

		Team team = null;

		try {
			team = TeamLocalServiceUtil.getTeam(group.getGroupId(), teamName);
		}
		catch (NoSuchTeamException nste) {
			team = TeamLocalServiceUtil.addTeam(
				groovyScriptingContext.guestUserId, group.getGroupId(),
				teamName, null);
		}

		for (String screenName : screenNames) {
			User user = UserLocalServiceUtil.fetchUserByScreenName(
				groovyScriptingContext.companyId, screenName);

			if (user != null) {
				TeamLocalServiceUtil.addUserTeam(user.getUserId(), team);
			}
		}
	}

	void create(GroovyScriptingContext groovyScriptingContext) {
		group = GroupLocalServiceUtil.fetchGroup(
			groovyScriptingContext.companyId, name);

		if (group != null) {
			return;
		}

		group = GroupLocalServiceUtil.addGroup(
			StringPool.BLANK, groovyScriptingContext.guestUserId,
			GroupConstants.DEFAULT_PARENT_GROUP_ID, null, 0, 0, name,
			description, type, null, true,
			GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION, null, true, false,
			true, groovyScriptingContext.serviceContext);
	}

	String description;
	Group group;
	String name;
	int type;

}