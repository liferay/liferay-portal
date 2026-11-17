/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.test.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserGroupRoleLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.service.UserServiceUtil;
import com.liferay.portal.kernel.test.randomizerbumpers.NumericStringRandomizerBumper;
import com.liferay.portal.kernel.test.randomizerbumpers.UniqueStringRandomizerBumper;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * @author Alberto Chaparro
 * @author Manuel de la Peña
 * @author Sampsa Sohlman
 */
public class UserTestUtil {

	public static User addCompanyAdminUser(Company company) throws Exception {
		return addCompanyUser(company, RoleConstants.ADMINISTRATOR);
	}

	public static User addCompanyUser(Company company, String roleName)
		throws Exception {

		User user = addUser(company);

		Role role = RoleLocalServiceUtil.getRole(
			company.getCompanyId(), roleName);

		UserLocalServiceUtil.addRoleUser(role.getRoleId(), user);

		return user;
	}

	public static User addGroupAdminUser(Group group) throws Exception {
		return addGroupUser(group, RoleConstants.SITE_ADMINISTRATOR);
	}

	public static User addGroupOwnerUser(Group group) throws Exception {
		return addGroupUser(group, RoleConstants.SITE_OWNER);
	}

	public static User addGroupUser(Group group, String roleName)
		throws Exception {

		User groupUser = addUser(
			group.getCompanyId(), TestPropsValues.getUserId(),
			RandomTestUtil.randomString(
				NumericStringRandomizerBumper.INSTANCE,
				UniqueStringRandomizerBumper.INSTANCE),
			LocaleUtil.getDefault(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), new long[] {group.getGroupId()},
			ServiceContextTestUtil.getServiceContext());

		Role role = RoleLocalServiceUtil.getRole(
			group.getCompanyId(), roleName);

		UserGroupRoleLocalServiceUtil.addUserGroupRoles(
			new long[] {groupUser.getUserId()}, group.getGroupId(),
			role.getRoleId());

		return groupUser;
	}

	public static User addOmniadminUser() throws Exception {
		Company company = CompanyLocalServiceUtil.getCompanyByWebId(
			PropsUtil.get(PropsKeys.COMPANY_DEFAULT_WEB_ID));

		return addCompanyAdminUser(company);
	}

	public static User addOrganizationAdminUser(Organization organization)
		throws Exception {

		return addOrganizationUser(
			organization, RoleConstants.ORGANIZATION_ADMINISTRATOR);
	}

	public static User addOrganizationOwnerUser(Organization organization)
		throws Exception {

		return addOrganizationUser(
			organization, RoleConstants.ORGANIZATION_OWNER);
	}

	public static User addOrganizationUser(
			Organization organization, String roleName)
		throws Exception {

		User organizationUser = addUser(organization.getGroupId());

		UserLocalServiceUtil.addOrganizationUser(
			organization.getOrganizationId(), organizationUser.getUserId());

		addUserGroupRole(
			organizationUser.getUserId(), organization.getGroupId(), roleName);

		return organizationUser;
	}

	public static User addUser() throws Exception {
		return addUser(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			RandomTestUtil.randomString(
				NumericStringRandomizerBumper.INSTANCE,
				UniqueStringRandomizerBumper.INSTANCE),
			LocaleUtil.getDefault(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			new long[] {TestPropsValues.getGroupId()},
			ServiceContextTestUtil.getServiceContext());
	}

	public static User addUser(boolean secure) throws Exception {
		boolean autoPassword = true;
		String password1 = StringPool.BLANK;
		String password2 = StringPool.BLANK;
		boolean autoScreenName = true;
		String screenName = StringPool.BLANK;

		Locale locale = LocaleUtil.getDefault();
		String firstName = "UserServiceTest";
		String middleName = StringPool.BLANK;
		String lastName = "UserServiceTest";
		long prefixListTypeId = 0;
		long suffixListTypeId = 0;
		boolean male = true;
		int birthdayMonth = Calendar.JANUARY;
		int birthdayDay = 1;
		int birthdayYear = 1970;
		String jobTitle = StringPool.BLANK;
		long[] groupIds = null;
		long[] organizationIds = null;
		long[] roleIds = null;
		long[] userGroupIds = null;
		boolean sendMail = false;

		ServiceContext serviceContext = new ServiceContext();

		if (secure) {
			String emailAddress =
				"UserServiceTest." + RandomTestUtil.nextLong() + "@liferay.com";

			return UserServiceUtil.addUser(
				TestPropsValues.getCompanyId(), autoPassword, password1,
				password2, autoScreenName, screenName, emailAddress, locale,
				firstName, middleName, lastName, prefixListTypeId,
				suffixListTypeId, male, birthdayMonth, birthdayDay,
				birthdayYear, jobTitle, groupIds, organizationIds, roleIds,
				userGroupIds, sendMail, serviceContext);
		}

		String emailAddress =
			"UserServiceTest." + RandomTestUtil.nextLong() + "@test.com";

		return UserLocalServiceUtil.addUser(
			TestPropsValues.getUserId(), TestPropsValues.getCompanyId(),
			autoPassword, password1, password2, autoScreenName, screenName,
			emailAddress, locale, firstName, middleName, lastName,
			prefixListTypeId, suffixListTypeId, male, birthdayMonth,
			birthdayDay, birthdayYear, jobTitle, UserConstants.TYPE_REGULAR,
			groupIds, organizationIds, roleIds, userGroupIds, sendMail,
			serviceContext);
	}

	public static User addUser(Company company) throws Exception {
		User user = getAdminUser(company.getCompanyId());

		Group group = GroupLocalServiceUtil.getGroup(
			company.getCompanyId(), GroupConstants.GUEST);

		return addUser(
			company.getCompanyId(), user.getUserId(),
			RandomTestUtil.randomString(
				NumericStringRandomizerBumper.INSTANCE,
				UniqueStringRandomizerBumper.INSTANCE),
			LocaleUtil.getDefault(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), new long[] {group.getGroupId()},
			ServiceContextTestUtil.getServiceContext());
	}

	public static User addUser(Company company, String password)
		throws Exception {

		User user = getAdminUser(company.getCompanyId());

		Group group = GroupLocalServiceUtil.getGroup(
			company.getCompanyId(), GroupConstants.GUEST);

		String emailAddress =
			RandomTestUtil.randomString() + RandomTestUtil.nextLong() +
				"@liferay.com";

		return addUser(
			company.getCompanyId(), user.getUserId(), password, emailAddress,
			RandomTestUtil.randomString(
				NumericStringRandomizerBumper.INSTANCE,
				UniqueStringRandomizerBumper.INSTANCE),
			LocaleUtil.getDefault(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), new long[] {group.getGroupId()},
			ServiceContextTestUtil.getServiceContext());
	}

	public static User addUser(long... groupIds) throws Exception {
		return addUser(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			RandomTestUtil.randomString(
				NumericStringRandomizerBumper.INSTANCE,
				UniqueStringRandomizerBumper.INSTANCE),
			LocaleUtil.getDefault(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), groupIds,
			ServiceContextTestUtil.getServiceContext());
	}

	public static User addUser(long groupId, Locale locale) throws Exception {
		return addUser(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			RandomTestUtil.randomString(
				NumericStringRandomizerBumper.INSTANCE,
				UniqueStringRandomizerBumper.INSTANCE),
			locale, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), new long[] {groupId},
			ServiceContextTestUtil.getServiceContext());
	}

	public static User addUser(
			long companyId, long userId, String screenName, Locale locale,
			String firstName, String lastName, long[] groupIds,
			ServiceContext serviceContext)
		throws Exception {

		String emailAddress =
			RandomTestUtil.randomString() + RandomTestUtil.nextLong() +
				"@liferay.com";

		return addUser(
			companyId, userId, StringPool.BLANK, emailAddress, screenName,
			locale, firstName, lastName, groupIds, serviceContext);
	}

	public static User addUser(
			long companyId, long userId, String password, String emailAddress,
			String screenName, Locale locale, String firstName, String lastName,
			long[] groupIds, ServiceContext serviceContext)
		throws Exception {

		User user = UserLocalServiceUtil.fetchUserByScreenName(
			companyId, screenName);

		if (user != null) {
			return user;
		}

		boolean autoPassword = false;

		if ((password == null) || password.equals(StringPool.BLANK)) {
			autoPassword = true;
		}

		String password1 = password;
		String password2 = password;
		String middleName = StringPool.BLANK;
		long prefixListTypeId = 0;
		long suffixListTypeId = 0;
		boolean male = true;
		int birthdayMonth = Calendar.JANUARY;
		int birthdayDay = 1;
		int birthdayYear = 1970;
		String jobTitle = StringPool.BLANK;
		long[] organizationIds = null;
		long[] roleIds = null;
		long[] userGroupIds = null;
		boolean sendMail = false;

		return UserLocalServiceUtil.addUser(
			userId, companyId, autoPassword, password1, password2,
			Validator.isNull(screenName), screenName, emailAddress, locale,
			firstName, middleName, lastName, prefixListTypeId, suffixListTypeId,
			male, birthdayMonth, birthdayDay, birthdayYear, jobTitle,
			UserConstants.TYPE_REGULAR, groupIds, organizationIds, roleIds,
			userGroupIds, sendMail, serviceContext);
	}

	public static User addUser(
			String screenName, Locale locale, String firstName, String lastName,
			long[] groupIds)
		throws Exception {

		return addUser(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			screenName, locale, firstName, lastName, groupIds,
			ServiceContextTestUtil.getServiceContext());
	}

	public static User addUser(String screenName, long... groupIds)
		throws Exception {

		return addUser(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			screenName, LocaleUtil.getDefault(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), groupIds,
			ServiceContextTestUtil.getServiceContext());
	}

	public static void addUserGroupRole(
			long userId, long groupId, String roleName)
		throws Exception {

		Role role = RoleLocalServiceUtil.getRole(
			TestPropsValues.getCompanyId(), roleName);

		UserGroupRoleLocalServiceUtil.addUserGroupRoles(
			new long[] {userId}, groupId, role.getRoleId());
	}

	public static User getAdminUser(long companyId) throws PortalException {
		Role role = RoleLocalServiceUtil.getRole(
			companyId, RoleConstants.ADMINISTRATOR);

		List<User> users = UserLocalServiceUtil.getRoleUsers(
			role.getRoleId(), 0, 1);

		if (!users.isEmpty()) {
			return users.get(0);
		}

		return null;
	}

	public static void setUser(User user) {
		if (user == null) {
			return;
		}

		PrincipalThreadLocal.setName(user.getUserId());

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));
	}

	public static User updateUser(User user) throws Exception {
		return updateUser(user, new ServiceContext());
	}

	public static User updateUser(User user, ServiceContext serviceContext)
		throws Exception {

		String oldPassword = StringPool.BLANK;
		String newPassword1 = StringPool.BLANK;
		String newPassword2 = StringPool.BLANK;
		Boolean passwordReset = false;
		String reminderQueryQuestion = StringPool.BLANK;
		String reminderQueryAnswer = StringPool.BLANK;
		String screenName = "TestUser" + RandomTestUtil.nextLong();
		String emailAddress =
			"UserServiceTest." + RandomTestUtil.nextLong() + "@liferay.com";
		String languageId = StringPool.BLANK;
		String timeZoneId = StringPool.BLANK;
		String greeting = StringPool.BLANK;
		String comments = StringPool.BLANK;
		String firstName = "UserServiceTest";
		String middleName = StringPool.BLANK;
		String lastName = "UserServiceTest";
		long prefixListTypeId = 0;
		long suffixListTypeId = 0;
		boolean male = true;
		int birthdayMonth = Calendar.JANUARY;
		int birthdayDay = 1;
		int birthdayYear = 1970;
		String smsSn = StringPool.BLANK;
		String facebookSn = StringPool.BLANK;
		String jabberSn = StringPool.BLANK;
		String skypeSn = StringPool.BLANK;
		String twitterSn = StringPool.BLANK;
		String jobTitle = StringPool.BLANK;
		long[] groupIds = null;
		long[] organizationIds = null;
		long[] roleIds = null;
		List<UserGroupRole> userGroupRoles = null;
		long[] userGroupIds = null;

		return UserServiceUtil.updateUser(
			user.getUserId(), oldPassword, newPassword1, newPassword2,
			passwordReset, reminderQueryQuestion, reminderQueryAnswer,
			screenName, emailAddress, languageId, timeZoneId, greeting,
			comments, firstName, middleName, lastName, prefixListTypeId,
			suffixListTypeId, male, birthdayMonth, birthdayDay, birthdayYear,
			smsSn, facebookSn, jabberSn, skypeSn, twitterSn, jobTitle, groupIds,
			organizationIds, roleIds, userGroupRoles, userGroupIds,
			serviceContext);
	}

}