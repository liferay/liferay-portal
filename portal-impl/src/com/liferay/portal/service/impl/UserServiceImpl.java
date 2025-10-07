/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.announcements.kernel.model.AnnouncementsDelivery;
import com.liferay.announcements.kernel.service.AnnouncementsDeliveryService;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.RequiredUserException;
import com.liferay.portal.kernel.exception.UserEmailAddressException;
import com.liferay.portal.kernel.exception.UserFieldException;
import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import com.liferay.portal.kernel.jsonwebservice.JSONWebServiceMode;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.EmailAddress;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.ListTypeConstants;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Phone;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.model.Website;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.membershippolicy.OrganizationMembershipPolicyUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.service.permission.OrganizationPermissionUtil;
import com.liferay.portal.kernel.service.permission.PortalPermissionUtil;
import com.liferay.portal.kernel.service.permission.RolePermissionUtil;
import com.liferay.portal.kernel.service.permission.TeamPermissionUtil;
import com.liferay.portal.kernel.service.permission.UserGroupRolePermissionUtil;
import com.liferay.portal.kernel.service.permission.UserPermissionUtil;
import com.liferay.portal.kernel.service.persistence.CompanyPersistence;
import com.liferay.portal.kernel.service.persistence.GroupPersistence;
import com.liferay.portal.kernel.service.persistence.OrganizationPersistence;
import com.liferay.portal.kernel.service.persistence.RolePersistence;
import com.liferay.portal.kernel.service.persistence.UserGroupPersistence;
import com.liferay.portal.kernel.service.persistence.UserGroupRolePersistence;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.comparator.UserIdComparator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowThreadLocal;
import com.liferay.portal.security.membershippolicy.RoleMembershipPolicyUtil;
import com.liferay.portal.security.membershippolicy.SiteMembershipPolicyUtil;
import com.liferay.portal.security.membershippolicy.UserGroupMembershipPolicyUtil;
import com.liferay.portal.service.base.UserServiceBaseImpl;
import com.liferay.portal.service.permission.PasswordPolicyPermissionUtil;
import com.liferay.portal.service.permission.UserGroupPermissionUtil;
import com.liferay.portlet.admin.util.OmniadminUtil;
import com.liferay.portlet.usersadmin.util.UsersAdminUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Provides the remote service for accessing, adding, authenticating, deleting,
 * and updating users. Its methods include permission checks.
 *
 * @author Brian Wing Shun Chan
 * @author Brian Myunghun Kim
 * @author Scott Lee
 * @author Jorge Ferrer
 * @author Julio Camarero
 */
public class UserServiceImpl extends UserServiceBaseImpl {

	/**
	 * Adds the users to the group.
	 *
	 * @param groupId the primary key of the group
	 * @param userIds the primary keys of the users
	 * @param serviceContext the service context to be applied (optionally
	 *        <code>null</code>)
	 */
	@Override
	public void addGroupUsers(
			long groupId, long[] userIds, ServiceContext serviceContext)
		throws PortalException {

		if (userIds.length == 0) {
			return;
		}

		PermissionChecker permissionChecker = getPermissionChecker();

		if (!GroupPermissionUtil.contains(
				permissionChecker, groupId, ActionKeys.ASSIGN_MEMBERS)) {

			// Allow any user to join open sites

			boolean hasPermission = false;

			if (userIds.length == 1) {
				User user = getUser();

				if (user.getUserId() == userIds[0]) {
					Group group = _groupPersistence.findByPrimaryKey(groupId);

					if (user.getCompanyId() == group.getCompanyId()) {
						int type = group.getType();

						if (type == GroupConstants.TYPE_SITE_OPEN) {
							hasPermission = true;
						}
					}
				}
			}

			if (!hasPermission) {
				throw new PrincipalException.MustHavePermission(
					permissionChecker, Group.class.getName(), groupId,
					ActionKeys.ASSIGN_MEMBERS);
			}
		}

		SiteMembershipPolicyUtil.checkMembership(
			userIds, new long[] {groupId}, null);

		userLocalService.addGroupUsers(groupId, userIds);

		SiteMembershipPolicyUtil.propagateMembership(
			userIds, new long[] {groupId}, null);
	}

	/**
	 * Adds the users to the organization.
	 *
	 * @param organizationId the primary key of the organization
	 * @param userIds the primary keys of the users
	 */
	@Override
	public void addOrganizationUsers(long organizationId, long[] userIds)
		throws PortalException {

		if (userIds.length == 0) {
			return;
		}

		validateUserIds(userIds);

		OrganizationPermissionUtil.check(
			getPermissionChecker(), organizationId, ActionKeys.ASSIGN_MEMBERS);

		validateOrganizationUsers(userIds);

		OrganizationMembershipPolicyUtil.checkMembership(
			userIds, new long[] {organizationId}, null);

		userLocalService.addOrganizationUsers(organizationId, userIds);

		OrganizationMembershipPolicyUtil.propagateMembership(
			userIds, new long[] {organizationId}, null);
	}

	@Override
	public User addOrUpdateUser(
			String externalReferenceCode, long creatorUserId, long companyId,
			boolean autoPassword, String password1, String password2,
			boolean autoScreenName, String screenName, String emailAddress,
			Locale locale, String firstName, String middleName, String lastName,
			long prefixListTypeId, long suffixListTypeId, boolean male,
			int birthdayMonth, int birthdayDay, int birthdayYear,
			String jobTitle, List<Address> addresses,
			List<EmailAddress> emailAddresses, List<Phone> phones,
			List<Website> websites, boolean sendEmail,
			ServiceContext serviceContext)
		throws PortalException {

		boolean workflowEnabled = WorkflowThreadLocal.isEnabled();

		try {
			WorkflowThreadLocal.setEnabled(false);

			User user = userLocalService.fetchUserByExternalReferenceCode(
				externalReferenceCode, companyId);

			if (user == null) {
				checkAddUserPermission(
					creatorUserId, companyId, emailAddress, new long[0],
					new long[0], new long[0], new long[0], serviceContext);
			}
			else {
				UserPermissionUtil.check(
					getPermissionChecker(), user.getUserId(), null,
					ActionKeys.UPDATE);

				validateUpdatePermission(
					user, screenName, emailAddress, firstName, middleName,
					lastName, prefixListTypeId, suffixListTypeId, birthdayMonth,
					birthdayDay, birthdayYear, male, jobTitle);

				long curUserId = getUserId();

				if (curUserId == user.getUserId()) {
					emailAddress = StringUtil.toLowerCase(
						StringUtil.trim(emailAddress));

					if (!StringUtil.equalsIgnoreCase(
							emailAddress, user.getEmailAddress())) {

						validateEmailAddress(user, emailAddress);
					}
				}
			}

			user = userLocalService.addOrUpdateUser(
				externalReferenceCode, creatorUserId, companyId, autoPassword,
				password1, password2, autoScreenName, screenName, emailAddress,
				locale, firstName, middleName, lastName, prefixListTypeId,
				suffixListTypeId, male, birthdayMonth, birthdayDay,
				birthdayYear, jobTitle, sendEmail, serviceContext);

			if (addresses != null) {
				UsersAdminUtil.updateAddresses(
					Contact.class.getName(), user.getContactId(), addresses,
					ListTypeConstants.CONTACT_ADDRESS);
			}

			if (emailAddresses != null) {
				UsersAdminUtil.updateEmailAddresses(
					Contact.class.getName(), user.getContactId(),
					emailAddresses);
			}

			if (phones != null) {
				UsersAdminUtil.updatePhones(
					Contact.class.getName(), user.getContactId(), phones);
			}

			if (websites != null) {
				UsersAdminUtil.updateWebsites(
					Contact.class.getName(), user.getContactId(), websites);
			}

			return user;
		}
		finally {
			WorkflowThreadLocal.setEnabled(workflowEnabled);
		}
	}

	/**
	 * Assigns the password policy to the users, removing any other currently
	 * assigned password policies.
	 *
	 * @param passwordPolicyId the primary key of the password policy
	 * @param userIds the primary keys of the users
	 */
	@Override
	public void addPasswordPolicyUsers(long passwordPolicyId, long[] userIds)
		throws PortalException {

		if (userIds.length == 0) {
			return;
		}

		PasswordPolicyPermissionUtil.check(
			getPermissionChecker(), passwordPolicyId,
			ActionKeys.ASSIGN_MEMBERS);

		userLocalService.addPasswordPolicyUsers(passwordPolicyId, userIds);
	}

	/**
	 * Adds the users to the role.
	 *
	 * @param roleId the primary key of the role
	 * @param userIds the primary keys of the users
	 */
	@Override
	public void addRoleUsers(long roleId, long[] userIds)
		throws PortalException {

		if (userIds.length == 0) {
			return;
		}

		RolePermissionUtil.check(
			getPermissionChecker(), roleId, ActionKeys.ASSIGN_MEMBERS);

		RoleMembershipPolicyUtil.checkRoles(userIds, new long[] {roleId}, null);

		userLocalService.addRoleUsers(roleId, userIds);

		RoleMembershipPolicyUtil.propagateRoles(
			userIds, new long[] {roleId}, null);
	}

	/**
	 * Adds the users to the team.
	 *
	 * @param teamId the primary key of the team
	 * @param userIds the primary keys of the users
	 */
	@Override
	public void addTeamUsers(long teamId, long[] userIds)
		throws PortalException {

		if (userIds.length == 0) {
			return;
		}

		TeamPermissionUtil.check(
			getPermissionChecker(), teamId, ActionKeys.ASSIGN_MEMBERS);

		userLocalService.addTeamUsers(teamId, userIds);
	}

	/**
	 * Adds a user.
	 *
	 * <p>
	 * This method handles the creation and bookkeeping of the user including
	 * its resources, metadata, and internal data structures. It is not
	 * necessary to make subsequent calls to any methods to setup default
	 * groups, resources, etc.
	 * </p>
	 *
	 * @param  companyId the primary key of the user's company
	 * @param  autoPassword whether a password should be automatically generated
	 *         for the user
	 * @param  password1 the user's password
	 * @param  password2 the user's password confirmation
	 * @param  autoScreenName whether a screen name should be automatically
	 *         generated for the user
	 * @param  screenName the user's screen name
	 * @param  emailAddress the user's email address
	 * @param  locale the user's locale
	 * @param  firstName the user's first name
	 * @param  middleName the user's middle name
	 * @param  lastName the user's last name
	 * @param  prefixListTypeId the user's name prefix ID
	 * @param  suffixListTypeId the user's name suffix ID
	 * @param  male whether the user is male
	 * @param  birthdayMonth the user's birthday month (0-based, meaning 0 for
	 *         January)
	 * @param  birthdayDay the user's birthday day
	 * @param  birthdayYear the user's birthday year
	 * @param  jobTitle the user's job title
	 * @param  groupIds the primary keys of the user's groups
	 * @param  organizationIds the primary keys of the user's organizations
	 * @param  roleIds the primary keys of the roles this user possesses
	 * @param  userGroupIds the primary keys of the user's user groups
	 * @param  sendEmail whether to send the user an email notification about
	 *         their new account
	 * @param  serviceContext the service context to be applied (optionally
	 *         <code>null</code>). Can set the UUID (with the <code>uuid</code>
	 *         attribute), asset category IDs, asset tag names, and expando
	 *         bridge attributes for the user.
	 * @return the new user
	 */
	@Override
	public User addUser(
			long companyId, boolean autoPassword, String password1,
			String password2, boolean autoScreenName, String screenName,
			String emailAddress, Locale locale, String firstName,
			String middleName, String lastName, long prefixListTypeId,
			long suffixListTypeId, boolean male, int birthdayMonth,
			int birthdayDay, int birthdayYear, String jobTitle, long[] groupIds,
			long[] organizationIds, long[] roleIds, long[] userGroupIds,
			boolean sendEmail, ServiceContext serviceContext)
		throws PortalException {

		boolean workflowEnabled = WorkflowThreadLocal.isEnabled();

		try {
			WorkflowThreadLocal.setEnabled(false);

			return addUserWithWorkflow(
				companyId, autoPassword, password1, password2, autoScreenName,
				screenName, emailAddress, locale, firstName, middleName,
				lastName, prefixListTypeId, suffixListTypeId, male,
				birthdayMonth, birthdayDay, birthdayYear, jobTitle, groupIds,
				organizationIds, roleIds, userGroupIds, sendEmail,
				serviceContext);
		}
		finally {
			WorkflowThreadLocal.setEnabled(workflowEnabled);
		}
	}

	/**
	 * Adds a user with additional parameters.
	 *
	 * <p>
	 * This method handles the creation and bookkeeping of the user including
	 * its resources, metadata, and internal data structures. It is not
	 * necessary to make subsequent calls to any methods to setup default
	 * groups, resources, etc.
	 * </p>
	 *
	 * @param  companyId the primary key of the user's company
	 * @param  autoPassword whether a password should be automatically generated
	 *         for the user
	 * @param  password1 the user's password
	 * @param  password2 the user's password confirmation
	 * @param  autoScreenName whether a screen name should be automatically
	 *         generated for the user
	 * @param  screenName the user's screen name
	 * @param  emailAddress the user's email address
	 * @param  locale the user's locale
	 * @param  firstName the user's first name
	 * @param  middleName the user's middle name
	 * @param  lastName the user's last name
	 * @param  prefixListTypeId the user's name prefix ID
	 * @param  suffixListTypeId the user's name suffix ID
	 * @param  male whether the user is male
	 * @param  birthdayMonth the user's birthday month (0-based, meaning 0 for
	 *         January)
	 * @param  birthdayDay the user's birthday day
	 * @param  birthdayYear the user's birthday year
	 * @param  jobTitle the user's job title
	 * @param  groupIds the primary keys of the user's groups
	 * @param  organizationIds the primary keys of the user's organizations
	 * @param  roleIds the primary keys of the roles this user possesses
	 * @param  userGroupIds the primary keys of the user's user groups
	 * @param  addresses the user's addresses
	 * @param  emailAddresses the user's email addresses
	 * @param  phones the user's phone numbers
	 * @param  websites the user's websites
	 * @param  announcementsDelivers the announcements deliveries
	 * @param  sendEmail whether to send the user an email notification about
	 *         their new account
	 * @param  serviceContext the service context to be applied (optionally
	 *         <code>null</code>). Can set the UUID (with the <code>uuid</code>
	 *         attribute), asset category IDs, asset tag names, and expando
	 *         bridge attributes for the user.
	 * @return the new user
	 */
	@Override
	public User addUser(
			long companyId, boolean autoPassword, String password1,
			String password2, boolean autoScreenName, String screenName,
			String emailAddress, Locale locale, String firstName,
			String middleName, String lastName, long prefixListTypeId,
			long suffixListTypeId, boolean male, int birthdayMonth,
			int birthdayDay, int birthdayYear, String jobTitle, long[] groupIds,
			long[] organizationIds, long[] roleIds, long[] userGroupIds,
			List<Address> addresses, List<EmailAddress> emailAddresses,
			List<Phone> phones, List<Website> websites,
			List<AnnouncementsDelivery> announcementsDelivers,
			boolean sendEmail, ServiceContext serviceContext)
		throws PortalException {

		boolean workflowEnabled = WorkflowThreadLocal.isEnabled();

		try {
			WorkflowThreadLocal.setEnabled(false);

			return addUserWithWorkflow(
				companyId, autoPassword, password1, password2, autoScreenName,
				screenName, emailAddress, locale, firstName, middleName,
				lastName, prefixListTypeId, suffixListTypeId, male,
				birthdayMonth, birthdayDay, birthdayYear, jobTitle, groupIds,
				organizationIds, roleIds, userGroupIds, addresses,
				emailAddresses, phones, websites, announcementsDelivers,
				sendEmail, serviceContext);
		}
		finally {
			WorkflowThreadLocal.setEnabled(workflowEnabled);
		}
	}

	/**
	 * Adds a user.
	 *
	 * <p>
	 * This method handles the creation and bookkeeping of the user including
	 * its resources, metadata, and internal data structures. It is not
	 * necessary to make subsequent calls to any methods to setup default
	 * groups, resources, etc.
	 * </p>
	 *
	 * @param      companyId the primary key of the user's company
	 * @param      autoPassword whether a password should be automatically
	 *             generated for the user
	 * @param      password1 the user's password
	 * @param      password2 the user's password confirmation
	 * @param      autoScreenName whether a screen name should be automatically
	 *             generated for the user
	 * @param      screenName the user's screen name
	 * @param      emailAddress the user's email address
	 * @param      facebookId the user's facebook ID
	 * @param      openId the user's OpenID
	 * @param      locale the user's locale
	 * @param      firstName the user's first name
	 * @param      middleName the user's middle name
	 * @param      lastName the user's last name
	 * @param      prefixListTypeId the user's name prefix ID
	 * @param      suffixListTypeId the user's name suffix ID
	 * @param      male whether the user is male
	 * @param      birthdayMonth the user's birthday month (0-based, meaning 0
	 *             for January)
	 * @param      birthdayDay the user's birthday day
	 * @param      birthdayYear the user's birthday year
	 * @param      jobTitle the user's job title
	 * @param      groupIds the primary keys of the user's groups
	 * @param      organizationIds the primary keys of the user's organizations
	 * @param      roleIds the primary keys of the roles this user possesses
	 * @param      userGroupIds the primary keys of the user's user groups
	 * @param      sendEmail whether to send the user an email notification
	 *             about their new account
	 * @param      serviceContext the service context to be applied (optionally
	 *             <code>null</code>). Can set the UUID (with the
	 *             <code>uuid</code> attribute), asset category IDs, asset tag
	 *             names, and expando bridge attributes for the user.
	 * @return     the new user
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link #addUser(long,
	 *             boolean, String, String, boolean, String, String, Locale,
	 *             String, String, String, long, long, boolean, int, int, int,
	 *             String, long[], long[], long[], long[], boolean,
	 *             ServiceContext)}
	 */
	@Deprecated
	@Override
	public User addUser(
			long companyId, boolean autoPassword, String password1,
			String password2, boolean autoScreenName, String screenName,
			String emailAddress, long facebookId, String openId, Locale locale,
			String firstName, String middleName, String lastName,
			long prefixListTypeId, long suffixListTypeId, boolean male,
			int birthdayMonth, int birthdayDay, int birthdayYear,
			String jobTitle, long[] groupIds, long[] organizationIds,
			long[] roleIds, long[] userGroupIds, boolean sendEmail,
			ServiceContext serviceContext)
		throws PortalException {

		boolean workflowEnabled = WorkflowThreadLocal.isEnabled();

		try {
			WorkflowThreadLocal.setEnabled(false);

			return addUserWithWorkflow(
				companyId, autoPassword, password1, password2, autoScreenName,
				screenName, emailAddress, facebookId, openId, locale, firstName,
				middleName, lastName, prefixListTypeId, suffixListTypeId, male,
				birthdayMonth, birthdayDay, birthdayYear, jobTitle, groupIds,
				organizationIds, roleIds, userGroupIds, sendEmail,
				serviceContext);
		}
		finally {
			WorkflowThreadLocal.setEnabled(workflowEnabled);
		}
	}

	/**
	 * Adds a user with additional parameters.
	 *
	 * <p>
	 * This method handles the creation and bookkeeping of the user including
	 * its resources, metadata, and internal data structures. It is not
	 * necessary to make subsequent calls to any methods to setup default
	 * groups, resources, etc.
	 * </p>
	 *
	 * @param      companyId the primary key of the user's company
	 * @param      autoPassword whether a password should be automatically
	 *             generated for the user
	 * @param      password1 the user's password
	 * @param      password2 the user's password confirmation
	 * @param      autoScreenName whether a screen name should be automatically
	 *             generated for the user
	 * @param      screenName the user's screen name
	 * @param      emailAddress the user's email address
	 * @param      facebookId the user's facebook ID
	 * @param      openId the user's OpenID
	 * @param      locale the user's locale
	 * @param      firstName the user's first name
	 * @param      middleName the user's middle name
	 * @param      lastName the user's last name
	 * @param      prefixListTypeId the user's name prefix ID
	 * @param      suffixListTypeId the user's name suffix ID
	 * @param      male whether the user is male
	 * @param      birthdayMonth the user's birthday month (0-based, meaning 0
	 *             for January)
	 * @param      birthdayDay the user's birthday day
	 * @param      birthdayYear the user's birthday year
	 * @param      jobTitle the user's job title
	 * @param      groupIds the primary keys of the user's groups
	 * @param      organizationIds the primary keys of the user's organizations
	 * @param      roleIds the primary keys of the roles this user possesses
	 * @param      userGroupIds the primary keys of the user's user groups
	 * @param      addresses the user's addresses
	 * @param      emailAddresses the user's email addresses
	 * @param      phones the user's phone numbers
	 * @param      websites the user's websites
	 * @param      announcementsDelivers the announcements deliveries
	 * @param      sendEmail whether to send the user an email notification
	 *             about their new account
	 * @param      serviceContext the service context to be applied (optionally
	 *             <code>null</code>). Can set the UUID (with the
	 *             <code>uuid</code> attribute), asset category IDs, asset tag
	 *             names, and expando bridge attributes for the user.
	 * @return     the new user
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link #addUser(long,
	 *             boolean, String, String, boolean, String, String, Locale,
	 *             String, String, String, long, long, boolean, int, int, int,
	 *             String, long[], long[], long[], long[], List, List, List,
	 *             List, List, boolean, ServiceContext)}
	 */
	@Deprecated
	@Override
	public User addUser(
			long companyId, boolean autoPassword, String password1,
			String password2, boolean autoScreenName, String screenName,
			String emailAddress, long facebookId, String openId, Locale locale,
			String firstName, String middleName, String lastName,
			long prefixListTypeId, long suffixListTypeId, boolean male,
			int birthdayMonth, int birthdayDay, int birthdayYear,
			String jobTitle, long[] groupIds, long[] organizationIds,
			long[] roleIds, long[] userGroupIds, List<Address> addresses,
			List<EmailAddress> emailAddresses, List<Phone> phones,
			List<Website> websites,
			List<AnnouncementsDelivery> announcementsDelivers,
			boolean sendEmail, ServiceContext serviceContext)
		throws PortalException {

		boolean workflowEnabled = WorkflowThreadLocal.isEnabled();

		try {
			WorkflowThreadLocal.setEnabled(false);

			return addUserWithWorkflow(
				companyId, autoPassword, password1, password2, autoScreenName,
				screenName, emailAddress, facebookId, openId, locale, firstName,
				middleName, lastName, prefixListTypeId, suffixListTypeId, male,
				birthdayMonth, birthdayDay, birthdayYear, jobTitle, groupIds,
				organizationIds, roleIds, userGroupIds, addresses,
				emailAddresses, phones, websites, announcementsDelivers,
				sendEmail, serviceContext);
		}
		finally {
			WorkflowThreadLocal.setEnabled(workflowEnabled);
		}
	}

	/**
	 * Adds the users to the user group.
	 *
	 * @param userGroupId the primary key of the user group
	 * @param userIds the primary keys of the users
	 */
	@Override
	public void addUserGroupUsers(long userGroupId, long[] userIds)
		throws PortalException {

		if (userIds.length == 0) {
			return;
		}

		UserGroupPermissionUtil.check(
			getPermissionChecker(), userGroupId, ActionKeys.ASSIGN_MEMBERS);

		UserGroupMembershipPolicyUtil.checkMembership(
			userIds, new long[] {userGroupId}, null);

		userLocalService.addUserGroupUsers(userGroupId, userIds);

		UserGroupMembershipPolicyUtil.propagateMembership(
			userIds, new long[] {userGroupId}, null);
	}

	/**
	 * Adds a user with workflow.
	 *
	 * <p>
	 * This method handles the creation and bookkeeping of the user including
	 * its resources, metadata, and internal data structures. It is not
	 * necessary to make subsequent calls to any methods to setup default
	 * groups, resources, etc.
	 * </p>
	 *
	 * @param  companyId the primary key of the user's company
	 * @param  autoPassword whether a password should be automatically generated
	 *         for the user
	 * @param  password1 the user's password
	 * @param  password2 the user's password confirmation
	 * @param  autoScreenName whether a screen name should be automatically
	 *         generated for the user
	 * @param  screenName the user's screen name
	 * @param  emailAddress the user's email address
	 * @param  locale the user's locale
	 * @param  firstName the user's first name
	 * @param  middleName the user's middle name
	 * @param  lastName the user's last name
	 * @param  prefixListTypeId the user's name prefix ID
	 * @param  suffixListTypeId the user's name suffix ID
	 * @param  male whether the user is male
	 * @param  birthdayMonth the user's birthday month (0-based, meaning 0 for
	 *         January)
	 * @param  birthdayDay the user's birthday day
	 * @param  birthdayYear the user's birthday year
	 * @param  jobTitle the user's job title
	 * @param  groupIds the primary keys of the user's groups
	 * @param  organizationIds the primary keys of the user's organizations
	 * @param  roleIds the primary keys of the roles this user possesses
	 * @param  userGroupIds the primary keys of the user's user groups
	 * @param  sendEmail whether to send the user an email notification about
	 *         their new account
	 * @param  serviceContext the service context to be applied (optionally
	 *         <code>null</code>). Can set the UUID (with the <code>uuid</code>
	 *         attribute), asset category IDs, asset tag names, and expando
	 *         bridge attributes for the user.
	 * @return the new user
	 */
	@Override
	public User addUserWithWorkflow(
			long companyId, boolean autoPassword, String password1,
			String password2, boolean autoScreenName, String screenName,
			String emailAddress, Locale locale, String firstName,
			String middleName, String lastName, long prefixListTypeId,
			long suffixListTypeId, boolean male, int birthdayMonth,
			int birthdayDay, int birthdayYear, String jobTitle, long[] groupIds,
			long[] organizationIds, long[] roleIds, long[] userGroupIds,
			boolean sendEmail, ServiceContext serviceContext)
		throws PortalException {

		long creatorUserId = 0;

		try {
			creatorUserId = getGuestOrUserId();
		}
		catch (PrincipalException principalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to get guest or current user ID",
					principalException);
			}
		}

		checkAddUserPermission(
			creatorUserId, companyId, emailAddress, groupIds, organizationIds,
			roleIds, userGroupIds, serviceContext);

		User user = userLocalService.addUserWithWorkflow(
			creatorUserId, companyId, autoPassword, password1, password2,
			autoScreenName, screenName, emailAddress, locale, firstName,
			middleName, lastName, prefixListTypeId, suffixListTypeId, male,
			birthdayMonth, birthdayDay, birthdayYear, jobTitle,
			UserConstants.TYPE_REGULAR, groupIds, organizationIds, roleIds,
			userGroupIds, sendEmail, serviceContext);

		checkMembership(
			new long[] {user.getUserId()}, groupIds, organizationIds, roleIds,
			userGroupIds);

		propagateMembership(
			new long[] {user.getUserId()}, groupIds, organizationIds, roleIds,
			userGroupIds);

		return user;
	}

	/**
	 * Adds a user with workflow and additional parameters.
	 *
	 * <p>
	 * This method handles the creation and bookkeeping of the user including
	 * its resources, metadata, and internal data structures. It is not
	 * necessary to make subsequent calls to any methods to setup default
	 * groups, resources, etc.
	 * </p>
	 *
	 * @param  companyId the primary key of the user's company
	 * @param  autoPassword whether a password should be automatically generated
	 *         for the user
	 * @param  password1 the user's password
	 * @param  password2 the user's password confirmation
	 * @param  autoScreenName whether a screen name should be automatically
	 *         generated for the user
	 * @param  screenName the user's screen name
	 * @param  emailAddress the user's email address
	 * @param  locale the user's locale
	 * @param  firstName the user's first name
	 * @param  middleName the user's middle name
	 * @param  lastName the user's last name
	 * @param  prefixListTypeId the user's name prefix ID
	 * @param  suffixListTypeId the user's name suffix ID
	 * @param  male whether the user is male
	 * @param  birthdayMonth the user's birthday month (0-based, meaning 0 for
	 *         January)
	 * @param  birthdayDay the user's birthday day
	 * @param  birthdayYear the user's birthday year
	 * @param  jobTitle the user's job title
	 * @param  groupIds the primary keys of the user's groups
	 * @param  organizationIds the primary keys of the user's organizations
	 * @param  roleIds the primary keys of the roles this user possesses
	 * @param  userGroupIds the primary keys of the user's user groups
	 * @param  addresses the user's addresses
	 * @param  emailAddresses the user's email addresses
	 * @param  phones the user's phone numbers
	 * @param  websites the user's websites
	 * @param  announcementsDelivers the announcements deliveries
	 * @param  sendEmail whether to send the user an email notification about
	 *         their new account
	 * @param  serviceContext the service context to be applied (optionally
	 *         <code>null</code>). Can set the UUID (with the <code>uuid</code>
	 *         attribute), asset category IDs, asset tag names, and expando
	 *         bridge attributes for the user.
	 * @return the new user
	 */
	@Override
	public User addUserWithWorkflow(
			long companyId, boolean autoPassword, String password1,
			String password2, boolean autoScreenName, String screenName,
			String emailAddress, Locale locale, String firstName,
			String middleName, String lastName, long prefixListTypeId,
			long suffixListTypeId, boolean male, int birthdayMonth,
			int birthdayDay, int birthdayYear, String jobTitle, long[] groupIds,
			long[] organizationIds, long[] roleIds, long[] userGroupIds,
			List<Address> addresses, List<EmailAddress> emailAddresses,
			List<Phone> phones, List<Website> websites,
			List<AnnouncementsDelivery> announcementsDelivers,
			boolean sendEmail, ServiceContext serviceContext)
		throws PortalException {

		boolean indexingEnabled = true;

		if (serviceContext != null) {
			indexingEnabled = serviceContext.isIndexingEnabled();

			serviceContext.setIndexingEnabled(false);
		}

		try {
			User user = addUserWithWorkflow(
				companyId, autoPassword, password1, password2, autoScreenName,
				screenName, emailAddress, locale, firstName, middleName,
				lastName, prefixListTypeId, suffixListTypeId, male,
				birthdayMonth, birthdayDay, birthdayYear, jobTitle, groupIds,
				organizationIds, roleIds, userGroupIds, sendEmail,
				serviceContext);

			UsersAdminUtil.updateAddresses(
				Contact.class.getName(), user.getContactId(), addresses,
				ListTypeConstants.CONTACT_ADDRESS);

			UsersAdminUtil.updateEmailAddresses(
				Contact.class.getName(), user.getContactId(), emailAddresses);

			UsersAdminUtil.updatePhones(
				Contact.class.getName(), user.getContactId(), phones);

			UsersAdminUtil.updateWebsites(
				Contact.class.getName(), user.getContactId(), websites);

			updateAnnouncementsDeliveries(
				user.getUserId(), announcementsDelivers);

			if (indexingEnabled) {
				Indexer<User> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
					User.class);

				indexer.reindex(user);
			}

			return user;
		}
		finally {
			if (serviceContext != null) {
				serviceContext.setIndexingEnabled(indexingEnabled);
			}
		}
	}

	/**
	 * Adds a user with workflow.
	 *
	 * <p>
	 * This method handles the creation and bookkeeping of the user including
	 * its resources, metadata, and internal data structures. It is not
	 * necessary to make subsequent calls to any methods to setup default
	 * groups, resources, etc.
	 * </p>
	 *
	 * @param      companyId the primary key of the user's company
	 * @param      autoPassword whether a password should be automatically
	 *             generated for the user
	 * @param      password1 the user's password
	 * @param      password2 the user's password confirmation
	 * @param      autoScreenName whether a screen name should be automatically
	 *             generated for the user
	 * @param      screenName the user's screen name
	 * @param      emailAddress the user's email address
	 * @param      facebookId the user's facebook ID
	 * @param      openId the user's OpenID
	 * @param      locale the user's locale
	 * @param      firstName the user's first name
	 * @param      middleName the user's middle name
	 * @param      lastName the user's last name
	 * @param      prefixListTypeId the user's name prefix ID
	 * @param      suffixListTypeId the user's name suffix ID
	 * @param      male whether the user is male
	 * @param      birthdayMonth the user's birthday month (0-based, meaning 0
	 *             for January)
	 * @param      birthdayDay the user's birthday day
	 * @param      birthdayYear the user's birthday year
	 * @param      jobTitle the user's job title
	 * @param      groupIds the primary keys of the user's groups
	 * @param      organizationIds the primary keys of the user's organizations
	 * @param      roleIds the primary keys of the roles this user possesses
	 * @param      userGroupIds the primary keys of the user's user groups
	 * @param      sendEmail whether to send the user an email notification
	 *             about their new account
	 * @param      serviceContext the service context to be applied (optionally
	 *             <code>null</code>). Can set the UUID (with the
	 *             <code>uuid</code> attribute), asset category IDs, asset tag
	 *             names, and expando bridge attributes for the user.
	 * @return     the new user
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 *             #addUserWithWorkflow(long, boolean, String, String, boolean,
	 *             String, String, Locale, String, String, String, long, long,
	 *             boolean, int, int, int, String, long[], long[], long[],
	 *             long[], boolean, ServiceContext)}
	 */
	@Deprecated
	@Override
	public User addUserWithWorkflow(
			long companyId, boolean autoPassword, String password1,
			String password2, boolean autoScreenName, String screenName,
			String emailAddress, long facebookId, String openId, Locale locale,
			String firstName, String middleName, String lastName,
			long prefixListTypeId, long suffixListTypeId, boolean male,
			int birthdayMonth, int birthdayDay, int birthdayYear,
			String jobTitle, long[] groupIds, long[] organizationIds,
			long[] roleIds, long[] userGroupIds, boolean sendEmail,
			ServiceContext serviceContext)
		throws PortalException {

		long creatorUserId = 0;

		try {
			creatorUserId = getGuestOrUserId();
		}
		catch (PrincipalException principalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to get guest or current user ID",
					principalException);
			}
		}

		checkAddUserPermission(
			creatorUserId, companyId, emailAddress, groupIds, organizationIds,
			roleIds, userGroupIds, serviceContext);

		User user = userLocalService.addUserWithWorkflow(
			creatorUserId, companyId, autoPassword, password1, password2,
			autoScreenName, screenName, emailAddress, locale, firstName,
			middleName, lastName, prefixListTypeId, suffixListTypeId, male,
			birthdayMonth, birthdayDay, birthdayYear, jobTitle,
			UserConstants.TYPE_REGULAR, groupIds, organizationIds, roleIds,
			userGroupIds, sendEmail, serviceContext);

		checkMembership(
			new long[] {user.getUserId()}, groupIds, organizationIds, roleIds,
			userGroupIds);

		propagateMembership(
			new long[] {user.getUserId()}, groupIds, organizationIds, roleIds,
			userGroupIds);

		return user;
	}

	/**
	 * Adds a user with workflow and additional parameters.
	 *
	 * <p>
	 * This method handles the creation and bookkeeping of the user including
	 * its resources, metadata, and internal data structures. It is not
	 * necessary to make subsequent calls to any methods to setup default
	 * groups, resources, etc.
	 * </p>
	 *
	 * @param      companyId the primary key of the user's company
	 * @param      autoPassword whether a password should be automatically
	 *             generated for the user
	 * @param      password1 the user's password
	 * @param      password2 the user's password confirmation
	 * @param      autoScreenName whether a screen name should be automatically
	 *             generated for the user
	 * @param      screenName the user's screen name
	 * @param      emailAddress the user's email address
	 * @param      facebookId the user's facebook ID
	 * @param      openId the user's OpenID
	 * @param      locale the user's locale
	 * @param      firstName the user's first name
	 * @param      middleName the user's middle name
	 * @param      lastName the user's last name
	 * @param      prefixListTypeId the user's name prefix ID
	 * @param      suffixListTypeId the user's name suffix ID
	 * @param      male whether the user is male
	 * @param      birthdayMonth the user's birthday month (0-based, meaning 0
	 *             for January)
	 * @param      birthdayDay the user's birthday day
	 * @param      birthdayYear the user's birthday year
	 * @param      jobTitle the user's job title
	 * @param      groupIds the primary keys of the user's groups
	 * @param      organizationIds the primary keys of the user's organizations
	 * @param      roleIds the primary keys of the roles this user possesses
	 * @param      userGroupIds the primary keys of the user's user groups
	 * @param      addresses the user's addresses
	 * @param      emailAddresses the user's email addresses
	 * @param      phones the user's phone numbers
	 * @param      websites the user's websites
	 * @param      announcementsDelivers the announcements deliveries
	 * @param      sendEmail whether to send the user an email notification
	 *             about their new account
	 * @param      serviceContext the service context to be applied (optionally
	 *             <code>null</code>). Can set the UUID (with the
	 *             <code>uuid</code> attribute), asset category IDs, asset tag
	 *             names, and expando bridge attributes for the user.
	 * @return     the new user
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 *             #addUserWithWorkflow(long, boolean, String, String, boolean,
	 *             String, String, Locale, String, String, String, long, long,
	 *             boolean, int, int, int, String, long[], long[], long[],
	 *             long[], List, List, List, List, List, boolean, ServiceContext
	 *             )}
	 */
	@Deprecated
	@Override
	public User addUserWithWorkflow(
			long companyId, boolean autoPassword, String password1,
			String password2, boolean autoScreenName, String screenName,
			String emailAddress, long facebookId, String openId, Locale locale,
			String firstName, String middleName, String lastName,
			long prefixListTypeId, long suffixListTypeId, boolean male,
			int birthdayMonth, int birthdayDay, int birthdayYear,
			String jobTitle, long[] groupIds, long[] organizationIds,
			long[] roleIds, long[] userGroupIds, List<Address> addresses,
			List<EmailAddress> emailAddresses, List<Phone> phones,
			List<Website> websites,
			List<AnnouncementsDelivery> announcementsDelivers,
			boolean sendEmail, ServiceContext serviceContext)
		throws PortalException {

		boolean indexingEnabled = true;

		if (serviceContext != null) {
			indexingEnabled = serviceContext.isIndexingEnabled();

			serviceContext.setIndexingEnabled(false);
		}

		try {
			User user = addUserWithWorkflow(
				companyId, autoPassword, password1, password2, autoScreenName,
				screenName, emailAddress, facebookId, openId, locale, firstName,
				middleName, lastName, prefixListTypeId, suffixListTypeId, male,
				birthdayMonth, birthdayDay, birthdayYear, jobTitle, groupIds,
				organizationIds, roleIds, userGroupIds, sendEmail,
				serviceContext);

			UsersAdminUtil.updateAddresses(
				Contact.class.getName(), user.getContactId(), addresses,
				ListTypeConstants.CONTACT_ADDRESS);

			UsersAdminUtil.updateEmailAddresses(
				Contact.class.getName(), user.getContactId(), emailAddresses);

			UsersAdminUtil.updatePhones(
				Contact.class.getName(), user.getContactId(), phones);

			UsersAdminUtil.updateWebsites(
				Contact.class.getName(), user.getContactId(), websites);

			updateAnnouncementsDeliveries(
				user.getUserId(), announcementsDelivers);

			if (indexingEnabled) {
				Indexer<User> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
					User.class);

				indexer.reindex(user);
			}

			return user;
		}
		finally {
			if (serviceContext != null) {
				serviceContext.setIndexingEnabled(indexingEnabled);
			}
		}
	}

	/**
	 * Deletes the user's portrait image.
	 *
	 * @param userId the primary key of the user
	 */
	@Override
	public void deletePortrait(long userId) throws PortalException {
		UserPermissionUtil.check(
			getPermissionChecker(), userId, ActionKeys.UPDATE);

		userLocalService.deletePortrait(userId);
	}

	/**
	 * Removes the user from the role.
	 *
	 * @param roleId the primary key of the role
	 * @param userId the primary key of the user
	 */
	@Override
	public void deleteRoleUser(long roleId, long userId)
		throws PortalException {

		RolePermissionUtil.check(
			getPermissionChecker(), roleId, ActionKeys.ASSIGN_MEMBERS);

		userLocalService.deleteRoleUser(roleId, userId);
	}

	/**
	 * Deletes the user.
	 *
	 * @param userId the primary key of the user
	 */
	@Override
	public void deleteUser(long userId) throws PortalException {
		if (getUserId() == userId) {
			throw new RequiredUserException();
		}

		UserPermissionUtil.check(
			getPermissionChecker(), userId, ActionKeys.DELETE);

		userLocalService.deleteUser(userId);
	}

	@Override
	public User fetchUserByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		User user = userLocalService.fetchUserByExternalReferenceCode(
			externalReferenceCode, companyId);

		if (user != null) {
			UserPermissionUtil.check(
				getPermissionChecker(), user.getUserId(), ActionKeys.VIEW);
		}

		return user;
	}

	@Override
	public List<User> getCompanyUsers(long companyId, int start, int end)
		throws PortalException {

		PermissionChecker permissionChecker = getPermissionChecker();

		if (!permissionChecker.hasPermission(
				null, User.class.getName(), companyId, ActionKeys.VIEW)) {

			throw new PrincipalException.MustHavePermission(
				permissionChecker, User.class.getName(), 0, ActionKeys.VIEW);
		}

		return userPersistence.findByCompanyId(companyId, start, end);
	}

	@Override
	public int getCompanyUsersCount(long companyId) throws PortalException {
		PermissionChecker permissionChecker = getPermissionChecker();

		if (!permissionChecker.hasPermission(
				null, User.class.getName(), companyId, ActionKeys.VIEW)) {

			throw new PrincipalException.MustHavePermission(
				permissionChecker, User.class.getName(), 0, ActionKeys.VIEW);
		}

		return userPersistence.countByCompanyId(companyId);
	}

	@Override
	public User getCurrentUser() throws PortalException {
		return getUser();
	}

	/**
	 * Returns the primary keys of all the users belonging to the group.
	 *
	 * @param  groupId the primary key of the group
	 * @return the primary keys of the users belonging to the group
	 */
	@Override
	public long[] getGroupUserIds(long groupId) throws PortalException {
		GroupPermissionUtil.check(
			getPermissionChecker(), groupId, ActionKeys.VIEW_MEMBERS);

		return userLocalService.getGroupUserIds(groupId);
	}

	/**
	 * Returns all the users belonging to the group.
	 *
	 * @param  groupId the primary key of the group
	 * @return the users belonging to the group
	 */
	@Override
	public List<User> getGroupUsers(long groupId) throws PortalException {
		GroupPermissionUtil.check(
			getPermissionChecker(), groupId, ActionKeys.VIEW_MEMBERS);

		return userLocalService.getGroupUsers(groupId);
	}

	/**
	 * Returns the users belonging to a group.
	 *
	 * @param  groupId the primary key of the group
	 * @param  status the workflow status
	 * @param  start the lower bound of the range of users
	 * @param  end the upper bound of the range of users (not inclusive)
	 * @param  orderByComparator the comparator to order the users by
	 *         (optionally <code>null</code>)
	 * @return the matching users
	 */
	@Override
	public List<User> getGroupUsers(
			long groupId, int status, int start, int end,
			OrderByComparator<User> orderByComparator)
		throws PortalException {

		GroupPermissionUtil.check(
			getPermissionChecker(), groupId, ActionKeys.VIEW_MEMBERS);

		return userLocalService.getGroupUsers(
			groupId, status, start, end, orderByComparator);
	}

	/**
	 * Returns the users belonging to a group.
	 *
	 * @param  groupId the primary key of the group
	 * @param  status the workflow status
	 * @param  orderByComparator the comparator to order the users by
	 *         (optionally <code>null</code>)
	 * @return the matching users
	 */
	@Override
	public List<User> getGroupUsers(
			long groupId, int status, OrderByComparator<User> orderByComparator)
		throws PortalException {

		return getGroupUsers(
			groupId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			orderByComparator);
	}

	/**
	 * Returns the number of users with the status belonging to the group.
	 *
	 * @param  groupId the primary key of the group
	 * @param  status the workflow status
	 * @return the number of users with the status belonging to the group
	 */
	@Override
	public int getGroupUsersCount(long groupId, int status)
		throws PortalException {

		GroupPermissionUtil.check(
			getPermissionChecker(), groupId, ActionKeys.VIEW);

		return userLocalService.getGroupUsersCount(groupId, status);
	}

	@Override
	public List<User> getGtCompanyUsers(long gtUserId, long companyId, int size)
		throws PortalException {

		PermissionChecker permissionChecker = getPermissionChecker();

		if (!permissionChecker.hasPermission(
				null, User.class.getName(), companyId, ActionKeys.VIEW)) {

			throw new PrincipalException.MustHavePermission(
				permissionChecker, User.class.getName(), 0, ActionKeys.VIEW);
		}

		return userPersistence.findByGtU_C(
			gtUserId, companyId, 0, size, UserIdComparator.getInstance(true));
	}

	@Override
	public List<User> getGtOrganizationUsers(
			long gtUserId, long organizationId, int size)
		throws PortalException {

		Organization organization = _organizationPersistence.findByPrimaryKey(
			organizationId);

		PermissionChecker permissionChecker = getPermissionChecker();

		if (!permissionChecker.hasPermission(
				null, Organization.class.getName(), organization.getCompanyId(),
				ActionKeys.VIEW_MEMBERS)) {

			throw new PrincipalException.MustHavePermission(
				permissionChecker, Organization.class.getName(), 0,
				ActionKeys.VIEW_MEMBERS);
		}

		return userFinder.findByUsersOrgsGtUserId(
			organization.getCompanyId(), organizationId, gtUserId, size);
	}

	@Override
	public List<User> getGtUserGroupUsers(
			long gtUserId, long userGroupId, int size)
		throws PortalException {

		UserGroup userGroup = _userGroupPersistence.findByPrimaryKey(
			userGroupId);

		PermissionChecker permissionChecker = getPermissionChecker();

		if (!permissionChecker.hasPermission(
				null, UserGroup.class.getName(), userGroup.getCompanyId(),
				ActionKeys.VIEW_MEMBERS)) {

			throw new PrincipalException.MustHavePermission(
				permissionChecker, UserGroup.class.getName(), 0,
				ActionKeys.VIEW_MEMBERS);
		}

		return userFinder.findByUsersUserGroupsGtUserId(
			userGroup.getCompanyId(), userGroupId, gtUserId, size);
	}

	@Override
	public int getOrganizationsAndUserGroupsUsersCount(
			long[] organizationIds, long[] userGroupIds)
		throws PrincipalException {

		PermissionChecker permissionChecker = getPermissionChecker();

		if (!permissionChecker.hasPermission(
				null, Organization.class.getName(),
				CompanyThreadLocal.getCompanyId(), ActionKeys.VIEW_MEMBERS)) {

			throw new PrincipalException.MustHavePermission(
				permissionChecker, Organization.class.getName(), 0,
				ActionKeys.VIEW_MEMBERS);
		}

		if (!permissionChecker.hasPermission(
				null, UserGroup.class.getName(),
				CompanyThreadLocal.getCompanyId(), ActionKeys.VIEW_MEMBERS)) {

			throw new PrincipalException.MustHavePermission(
				permissionChecker, UserGroup.class.getName(), 0,
				ActionKeys.VIEW_MEMBERS);
		}

		return userLocalService.getOrganizationsAndUserGroupsUsersCount(
			organizationIds, userGroupIds);
	}

	/**
	 * Returns the primary keys of all the users belonging to the organization.
	 *
	 * @param  organizationId the primary key of the organization
	 * @return the primary keys of the users belonging to the organization
	 */
	@Override
	public long[] getOrganizationUserIds(long organizationId)
		throws PortalException {

		OrganizationPermissionUtil.check(
			getPermissionChecker(), organizationId, ActionKeys.VIEW_MEMBERS);

		return userLocalService.getOrganizationUserIds(organizationId);
	}

	/**
	 * Returns all the users belonging to the organization.
	 *
	 * @param  organizationId the primary key of the organization
	 * @return users belonging to the organization
	 */
	@Override
	public List<User> getOrganizationUsers(long organizationId)
		throws PortalException {

		OrganizationPermissionUtil.check(
			getPermissionChecker(), organizationId, ActionKeys.VIEW_MEMBERS);

		return userLocalService.getOrganizationUsers(organizationId);
	}

	/**
	 * Returns the users belonging to the organization with the status.
	 *
	 * @param  organizationId the primary key of the organization
	 * @param  status the workflow status
	 * @param  start the lower bound of the range of users
	 * @param  end the upper bound of the range of users (not inclusive)
	 * @param  orderByComparator the comparator to order the users by
	 *         (optionally <code>null</code>)
	 * @return the matching users
	 */
	@Override
	public List<User> getOrganizationUsers(
			long organizationId, int status, int start, int end,
			OrderByComparator<User> orderByComparator)
		throws PortalException {

		OrganizationPermissionUtil.check(
			getPermissionChecker(), organizationId, ActionKeys.VIEW_MEMBERS);

		return userLocalService.getOrganizationUsers(
			organizationId, status, start, end, orderByComparator);
	}

	/**
	 * Returns the users belonging to the organization with the status.
	 *
	 * @param  organizationId the primary key of the organization
	 * @param  status the workflow status
	 * @param  orderByComparator the comparator to order the users by
	 *         (optionally <code>null</code>)
	 * @return the matching users
	 */
	@Override
	public List<User> getOrganizationUsers(
			long organizationId, int status,
			OrderByComparator<User> orderByComparator)
		throws PortalException {

		OrganizationPermissionUtil.check(
			getPermissionChecker(), organizationId, ActionKeys.VIEW_MEMBERS);

		return userLocalService.getOrganizationUsers(
			organizationId, status, orderByComparator);
	}

	/**
	 * Returns the number of users with the status belonging to the
	 * organization.
	 *
	 * @param  organizationId the primary key of the organization
	 * @param  status the workflow status
	 * @return the number of users with the status belonging to the organization
	 */
	@Override
	public int getOrganizationUsersCount(long organizationId, int status)
		throws PortalException {

		OrganizationPermissionUtil.check(
			getPermissionChecker(), organizationId, ActionKeys.VIEW);

		return userLocalService.getOrganizationUsersCount(
			organizationId, status);
	}

	/**
	 * Returns the primary keys of all the users belonging to the role.
	 *
	 * @param  roleId the primary key of the role
	 * @return the primary keys of the users belonging to the role
	 */
	@Override
	public long[] getRoleUserIds(long roleId) throws PortalException {
		RolePermissionUtil.check(
			getPermissionChecker(), roleId, ActionKeys.ASSIGN_MEMBERS);

		return userLocalService.getRoleUserIds(roleId);
	}

	/**
	 * Returns the user with the email address.
	 *
	 * @param  companyId the primary key of the user's company
	 * @param  emailAddress the user's email address
	 * @return the user with the email address
	 */
	@Override
	public User getUserByEmailAddress(long companyId, String emailAddress)
		throws PortalException {

		User user = userLocalService.getUserByEmailAddress(
			companyId, emailAddress);

		UserPermissionUtil.check(
			getPermissionChecker(), user.getUserId(), ActionKeys.VIEW);

		return user;
	}

	@Override
	public User getUserByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		User user = userLocalService.getUserByExternalReferenceCode(
			externalReferenceCode, companyId);

		UserPermissionUtil.check(
			getPermissionChecker(), user.getUserId(), ActionKeys.VIEW);

		return user;
	}

	/**
	 * Returns the user with the primary key.
	 *
	 * @param  userId the primary key of the user
	 * @return the user with the primary key
	 */
	@Override
	public User getUserById(long userId) throws PortalException {
		User user = userPersistence.findByPrimaryKey(userId);

		UserPermissionUtil.check(
			getPermissionChecker(), user.getUserId(), ActionKeys.VIEW);

		return user;
	}

	/**
	 * Returns the user with the screen name.
	 *
	 * @param  companyId the primary key of the user's company
	 * @param  screenName the user's screen name
	 * @return the user with the screen name
	 */
	@Override
	public User getUserByScreenName(long companyId, String screenName)
		throws PortalException {

		User user = userLocalService.getUserByScreenName(companyId, screenName);

		UserPermissionUtil.check(
			getPermissionChecker(), user.getUserId(), ActionKeys.VIEW);

		return user;
	}

	@Override
	public List<User> getUserGroupUsers(long userGroupId)
		throws PortalException {

		UserGroupPermissionUtil.check(
			getPermissionChecker(), userGroupId, ActionKeys.VIEW_MEMBERS);

		return _userGroupPersistence.getUsers(userGroupId);
	}

	@Override
	public List<User> getUserGroupUsers(long userGroupId, int start, int end)
		throws PortalException {

		UserGroupPermissionUtil.check(
			getPermissionChecker(), userGroupId, ActionKeys.VIEW_MEMBERS);

		return userLocalService.getUserGroupUsers(userGroupId, start, end);
	}

	/**
	 * Returns the primary key of the user with the email address.
	 *
	 * @param  companyId the primary key of the user's company
	 * @param  emailAddress the user's email address
	 * @return the primary key of the user with the email address
	 */
	@Override
	public long getUserIdByEmailAddress(long companyId, String emailAddress)
		throws PortalException {

		User user = getUserByEmailAddress(companyId, emailAddress);

		UserPermissionUtil.check(
			getPermissionChecker(), user.getUserId(), ActionKeys.VIEW);

		return user.getUserId();
	}

	/**
	 * Returns the primary key of the user with the screen name.
	 *
	 * @param  companyId the primary key of the user's company
	 * @param  screenName the user's screen name
	 * @return the primary key of the user with the screen name
	 */
	@Override
	public long getUserIdByScreenName(long companyId, String screenName)
		throws PortalException {

		User user = getUserByScreenName(companyId, screenName);

		UserPermissionUtil.check(
			getPermissionChecker(), user.getUserId(), ActionKeys.VIEW);

		return user.getUserId();
	}

	/**
	 * Returns <code>true</code> if the user is a member of the group.
	 *
	 * @param  groupId the primary key of the group
	 * @param  userId the primary key of the user
	 * @return <code>true</code> if the user is a member of the group;
	 *         <code>false</code> otherwise
	 */
	@Override
	public boolean hasGroupUser(long groupId, long userId)
		throws PortalException {

		if (!UserPermissionUtil.contains(
				getPermissionChecker(), userId, ActionKeys.VIEW)) {

			GroupPermissionUtil.check(
				getPermissionChecker(), groupId, ActionKeys.VIEW_MEMBERS);
		}

		return userLocalService.hasGroupUser(groupId, userId);
	}

	/**
	 * Returns <code>true</code> if the user is a member of the role.
	 *
	 * @param  roleId the primary key of the role
	 * @param  userId the primary key of the user
	 * @return <code>true</code> if the user is a member of the role;
	 *         <code>false</code> otherwise
	 */
	@Override
	public boolean hasRoleUser(long roleId, long userId)
		throws PortalException {

		if (!UserPermissionUtil.contains(
				getPermissionChecker(), userId, ActionKeys.VIEW)) {

			RolePermissionUtil.check(
				getPermissionChecker(), roleId, ActionKeys.VIEW_MEMBERS);
		}

		return userLocalService.hasRoleUser(roleId, userId);
	}

	/**
	 * Returns <code>true</code> if the user has the role with the name,
	 * optionally through inheritance.
	 *
	 * @param  companyId the primary key of the role's company
	 * @param  name the name of the role (must be a regular role, not an
	 *         organization, site or provider role)
	 * @param  userId the primary key of the user
	 * @param  inherited whether to include roles inherited from organizations,
	 *         sites, etc.
	 * @return <code>true</code> if the user has the role; <code>false</code>
	 *         otherwise
	 */
	@Override
	public boolean hasRoleUser(
			long companyId, String name, long userId, boolean inherited)
		throws PortalException {

		if (!UserPermissionUtil.contains(
				getPermissionChecker(), userId, ActionKeys.VIEW)) {

			Role role = _roleLocalService.getRole(companyId, name);

			RolePermissionUtil.check(
				getPermissionChecker(), role.getRoleId(),
				ActionKeys.VIEW_MEMBERS);
		}

		return userLocalService.hasRoleUser(companyId, name, userId, inherited);
	}

	/**
	 * Sends a password notification email to the user matching the email
	 * address. The portal's settings determine whether a password is sent
	 * explicitly or whether a link for resetting the user's password is sent.
	 * The method sends the email asynchronously and returns before the email is
	 * sent.
	 *
	 * <p>
	 * The content of the notification email is specified with the
	 * <code>admin.email.password</code> portal property keys. They can be
	 * overridden via a <code>portal-ext.properties</code> file or modified
	 * through the Portal Settings UI.
	 * </p>
	 *
	 * @param  companyId the primary key of the user's company
	 * @param  emailAddress the user's email address
	 * @return <code>true</code> if the notification email includes a new
	 *         password; <code>false</code> if the notification email only
	 *         contains a reset link
	 */
	@JSONWebService(mode = JSONWebServiceMode.IGNORE)
	@Override
	public boolean sendPasswordByEmailAddress(
			long companyId, String emailAddress)
		throws PortalException {

		return userLocalService.sendPasswordByEmailAddress(
			companyId, emailAddress);
	}

	/**
	 * Sends a password notification email to the user matching the screen name.
	 * The portal's settings determine whether a password is sent explicitly or
	 * whether a link for resetting the user's password is sent. The method
	 * sends the email asynchronously and returns before the email is sent.
	 *
	 * <p>
	 * The content of the notification email is specified with the
	 * <code>admin.email.password</code> portal property keys. They can be
	 * overridden via a <code>portal-ext.properties</code> file or modified
	 * through the Portal Settings UI.
	 * </p>
	 *
	 * @param  companyId the primary key of the user's company
	 * @param  screenName the user's screen name
	 * @return <code>true</code> if the notification email includes a new
	 *         password; <code>false</code> if the notification email only
	 *         contains a reset link
	 */
	@JSONWebService(mode = JSONWebServiceMode.IGNORE)
	@Override
	public boolean sendPasswordByScreenName(long companyId, String screenName)
		throws PortalException {

		return userLocalService.sendPasswordByScreenName(companyId, screenName);
	}

	/**
	 * Sends a password notification email to the user matching the ID. The
	 * portal's settings determine whether a password is sent explicitly or
	 * whether a link for resetting the user's password is sent. The method
	 * sends the email asynchronously and returns before the email is sent.
	 *
	 * <p>
	 * The content of the notification email is specified with the
	 * <code>admin.email.password</code> portal property keys. They can be
	 * overridden via a <code>portal-ext.properties</code> file or modified
	 * through the Portal Settings UI.
	 * </p>
	 *
	 * @param  userId the user's primary key
	 * @return <code>true</code> if the notification email includes a new
	 *         password; <code>false</code> if the notification email only
	 *         contains a reset link
	 */
	@JSONWebService(mode = JSONWebServiceMode.IGNORE)
	@Override
	public boolean sendPasswordByUserId(long userId) throws PortalException {
		return userLocalService.sendPasswordByUserId(userId);
	}

	/**
	 * Sets the users in the role, removing and adding users to the role as
	 * necessary.
	 *
	 * @param roleId the primary key of the role
	 * @param userIds the primary keys of the users
	 */
	@Override
	public void setRoleUsers(long roleId, long[] userIds)
		throws PortalException {

		RolePermissionUtil.check(
			getPermissionChecker(), roleId, ActionKeys.ASSIGN_MEMBERS);

		Set<Long> unsetUserIds = SetUtil.fromArray(
			_rolePersistence.getUserPrimaryKeys(roleId));

		unsetUserIds.removeAll(SetUtil.fromArray(userIds));

		if (!unsetUserIds.isEmpty()) {
			RoleMembershipPolicyUtil.checkRoles(
				ArrayUtil.toLongArray(unsetUserIds), null, new long[] {roleId});
		}

		if (userIds.length > 0) {
			RoleMembershipPolicyUtil.checkRoles(
				userIds, new long[] {roleId}, null);
		}

		userLocalService.setRoleUsers(roleId, userIds);

		if (!unsetUserIds.isEmpty()) {
			RoleMembershipPolicyUtil.propagateRoles(
				ArrayUtil.toLongArray(unsetUserIds), null, new long[] {roleId});
		}

		if (userIds.length > 0) {
			RoleMembershipPolicyUtil.propagateRoles(
				userIds, new long[] {roleId}, null);
		}
	}

	/**
	 * Sets the users in the user group, removing and adding users to the user
	 * group as necessary.
	 *
	 * @param userGroupId the primary key of the user group
	 * @param userIds the primary keys of the users
	 */
	@Override
	public void setUserGroupUsers(long userGroupId, long[] userIds)
		throws PortalException {

		UserGroupPermissionUtil.check(
			getPermissionChecker(), userGroupId, ActionKeys.ASSIGN_MEMBERS);

		Set<Long> unsetUserIds = SetUtil.fromArray(
			_userGroupPersistence.getUserPrimaryKeys(userGroupId));

		unsetUserIds.removeAll(SetUtil.fromArray(userIds));

		if (!unsetUserIds.isEmpty()) {
			UserGroupMembershipPolicyUtil.checkMembership(
				ArrayUtil.toLongArray(unsetUserIds), null,
				new long[] {userGroupId});
		}

		if (userIds.length > 0) {
			UserGroupMembershipPolicyUtil.checkMembership(
				userIds, new long[] {userGroupId}, null);
		}

		userLocalService.setUserGroupUsers(userGroupId, userIds);

		if (!unsetUserIds.isEmpty()) {
			UserGroupMembershipPolicyUtil.propagateMembership(
				ArrayUtil.toLongArray(unsetUserIds), null,
				new long[] {userGroupId});
		}

		if (userIds.length > 0) {
			UserGroupMembershipPolicyUtil.propagateMembership(
				userIds, new long[] {userGroupId}, null);
		}
	}

	/**
	 * Removes the users from the teams of a group.
	 *
	 * @param groupId the primary key of the group
	 * @param userIds the primary keys of the users
	 */
	@Override
	public void unsetGroupTeamsUsers(long groupId, long[] userIds)
		throws PortalException {

		if (userIds.length == 0) {
			return;
		}

		UserGroupPermissionUtil.check(
			getPermissionChecker(), groupId, ActionKeys.ASSIGN_MEMBERS);

		userLocalService.unsetGroupTeamsUsers(groupId, userIds);
	}

	/**
	 * Removes the users from the group.
	 *
	 * @param groupId the primary key of the group
	 * @param userIds the primary keys of the users
	 * @param serviceContext the service context to be applied (optionally
	 *        <code>null</code>)
	 */
	@Override
	public void unsetGroupUsers(
			long groupId, long[] userIds, ServiceContext serviceContext)
		throws PortalException {

		userIds = UsersAdminUtil.filterUnsetGroupUserIds(
			getPermissionChecker(), groupId, userIds);

		if (userIds.length == 0) {
			return;
		}

		PermissionChecker permissionChecker = getPermissionChecker();

		if (!GroupPermissionUtil.contains(
				permissionChecker, groupId, ActionKeys.ASSIGN_MEMBERS)) {

			// Allow any user to leave open and restricted sites

			boolean hasPermission = false;

			if (userIds.length == 1) {
				User user = getUser();

				if (user.getUserId() == userIds[0]) {
					Group group = _groupPersistence.findByPrimaryKey(groupId);

					if (user.getCompanyId() == group.getCompanyId()) {
						int type = group.getType();

						if ((type == GroupConstants.TYPE_SITE_OPEN) ||
							(type == GroupConstants.TYPE_SITE_RESTRICTED)) {

							hasPermission = true;
						}
					}
				}
			}

			if (!hasPermission) {
				throw new PrincipalException.MustHavePermission(
					permissionChecker, Group.class.getName(), groupId,
					ActionKeys.ASSIGN_MEMBERS);
			}
		}

		SiteMembershipPolicyUtil.checkMembership(
			userIds, null, new long[] {groupId});

		userLocalService.unsetGroupUsers(groupId, userIds, serviceContext);

		SiteMembershipPolicyUtil.propagateMembership(
			userIds, null, new long[] {groupId});
	}

	/**
	 * Removes the users from the organization.
	 *
	 * @param organizationId the primary key of the organization
	 * @param userIds the primary keys of the users
	 */
	@Override
	public void unsetOrganizationUsers(long organizationId, long[] userIds)
		throws PortalException {

		userIds = UsersAdminUtil.filterUnsetOrganizationUserIds(
			getPermissionChecker(), organizationId, userIds);

		if (userIds.length == 0) {
			return;
		}

		OrganizationPermissionUtil.check(
			getPermissionChecker(), organizationId, ActionKeys.ASSIGN_MEMBERS);

		OrganizationMembershipPolicyUtil.checkMembership(
			userIds, null, new long[] {organizationId});

		userLocalService.unsetOrganizationUsers(organizationId, userIds);

		OrganizationMembershipPolicyUtil.propagateMembership(
			userIds, null, new long[] {organizationId});
	}

	/**
	 * Removes the users from the password policy.
	 *
	 * @param passwordPolicyId the primary key of the password policy
	 * @param userIds the primary keys of the users
	 */
	@Override
	public void unsetPasswordPolicyUsers(long passwordPolicyId, long[] userIds)
		throws PortalException {

		if (userIds.length == 0) {
			return;
		}

		PasswordPolicyPermissionUtil.check(
			getPermissionChecker(), passwordPolicyId,
			ActionKeys.ASSIGN_MEMBERS);

		userLocalService.unsetPasswordPolicyUsers(passwordPolicyId, userIds);
	}

	/**
	 * Removes the users from the role.
	 *
	 * @param roleId the primary key of the role
	 * @param userIds the primary keys of the users
	 */
	@Override
	public void unsetRoleUsers(long roleId, long[] userIds)
		throws PortalException {

		if (userIds.length == 0) {
			return;
		}

		RolePermissionUtil.check(
			getPermissionChecker(), roleId, ActionKeys.ASSIGN_MEMBERS);

		RoleMembershipPolicyUtil.checkRoles(userIds, null, new long[] {roleId});

		userLocalService.unsetRoleUsers(roleId, userIds);

		RoleMembershipPolicyUtil.propagateRoles(
			userIds, null, new long[] {roleId});
	}

	/**
	 * Removes the users from the team.
	 *
	 * @param teamId the primary key of the team
	 * @param userIds the primary keys of the users
	 */
	@Override
	public void unsetTeamUsers(long teamId, long[] userIds)
		throws PortalException {

		if (userIds.length == 0) {
			return;
		}

		TeamPermissionUtil.check(
			getPermissionChecker(), teamId, ActionKeys.ASSIGN_MEMBERS);

		userLocalService.unsetTeamUsers(teamId, userIds);
	}

	/**
	 * Removes the users from the user group.
	 *
	 * @param userGroupId the primary key of the user group
	 * @param userIds the primary keys of the users
	 */
	@Override
	public void unsetUserGroupUsers(long userGroupId, long[] userIds)
		throws PortalException {

		if (userIds.length == 0) {
			return;
		}

		UserGroupPermissionUtil.check(
			getPermissionChecker(), userGroupId, ActionKeys.ASSIGN_MEMBERS);

		UserGroupMembershipPolicyUtil.checkMembership(
			userIds, null, new long[] {userGroupId});

		userLocalService.unsetUserGroupUsers(userGroupId, userIds);

		UserGroupMembershipPolicyUtil.propagateMembership(
			userIds, null, new long[] {userGroupId});
	}

	/**
	 * Updates the user's response to the terms of use agreement.
	 *
	 * @param  userId the primary key of the user
	 * @param  agreedToTermsOfUse whether the user has agree to the terms of use
	 * @return the user
	 */
	@CTAware(onProduction = true)
	@Override
	public User updateAgreedToTermsOfUse(
			long userId, boolean agreedToTermsOfUse)
		throws PortalException {

		UserPermissionUtil.check(
			getPermissionChecker(), userId, ActionKeys.UPDATE);

		return userLocalService.updateAgreedToTermsOfUse(
			userId, agreedToTermsOfUse);
	}

	/**
	 * Updates the user's email address.
	 *
	 * @param  userId the primary key of the user
	 * @param  password the user's password
	 * @param  emailAddress1 the user's new email address
	 * @param  emailAddress2 the user's new email address confirmation
	 * @param  serviceContext the service context to be applied. Must set the
	 *         portal URL, main path, primary key of the layout, remote address,
	 *         remote host, and agent for the user.
	 * @return the user
	 */
	@Override
	public User updateEmailAddress(
			long userId, String password, String emailAddress1,
			String emailAddress2, ServiceContext serviceContext)
		throws PortalException {

		UserPermissionUtil.check(
			getPermissionChecker(), userId, ActionKeys.UPDATE);

		User user = userPersistence.findByPrimaryKey(userId);

		validateEmailAddress(user, emailAddress2);

		return userLocalService.updateEmailAddress(
			userId, password, emailAddress1, emailAddress2, serviceContext);
	}

	@Override
	public User updateExternalReferenceCode(
			long userId, String externalReferenceCode)
		throws PortalException {

		UserPermissionUtil.check(
			getPermissionChecker(), userId, ActionKeys.UPDATE);

		return userLocalService.updateExternalReferenceCode(
			userId, externalReferenceCode);
	}

	@Override
	public User updateExternalReferenceCode(
			User user, String externalReferenceCode)
		throws PortalException {

		UserPermissionUtil.check(
			getPermissionChecker(), user.getUserId(), ActionKeys.UPDATE);

		return userLocalService.updateExternalReferenceCode(
			user, externalReferenceCode);
	}

	/**
	 * Updates a user account that was automatically created when a guest user
	 * participated in an action (e.g. posting a comment) and only provided his
	 * name and email address.
	 *
	 * @param  companyId the primary key of the user's company
	 * @param  autoPassword whether a password should be automatically generated
	 *         for the user
	 * @param  password1 the user's password
	 * @param  password2 the user's password confirmation
	 * @param  autoScreenName whether a screen name should be automatically
	 *         generated for the user
	 * @param  screenName the user's screen name
	 * @param  emailAddress the user's email address
	 * @param  locale the user's locale
	 * @param  firstName the user's first name
	 * @param  middleName the user's middle name
	 * @param  lastName the user's last name
	 * @param  prefixListTypeId the user's name prefix ID
	 * @param  suffixListTypeId the user's name suffix ID
	 * @param  male whether the user is male
	 * @param  birthdayMonth the user's birthday month (0-based, meaning 0 for
	 *         January)
	 * @param  birthdayDay the user's birthday day
	 * @param  birthdayYear the user's birthday year
	 * @param  jobTitle the user's job title
	 * @param  updateUserInformation whether to update the user's information
	 * @param  sendEmail whether to send the user an email notification about
	 *         their new account
	 * @param  serviceContext the service context to be applied (optionally
	 *         <code>null</code>). Can set the expando bridge attributes for the
	 *         user.
	 * @return the user
	 */
	@Override
	public User updateIncompleteUser(
			long companyId, boolean autoPassword, String password1,
			String password2, boolean autoScreenName, String screenName,
			String emailAddress, Locale locale, String firstName,
			String middleName, String lastName, long prefixListTypeId,
			long suffixListTypeId, boolean male, int birthdayMonth,
			int birthdayDay, int birthdayYear, String jobTitle,
			boolean updateUserInformation, boolean sendEmail,
			ServiceContext serviceContext)
		throws PortalException {

		long creatorUserId = 0;

		try {
			creatorUserId = getGuestOrUserId();
		}
		catch (PrincipalException principalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to get guest or current user ID",
					principalException);
			}
		}

		checkAddUserPermission(
			creatorUserId, companyId, emailAddress, null, null, null, null,
			serviceContext);

		return userLocalService.updateIncompleteUser(
			creatorUserId, companyId, autoPassword, password1, password2,
			autoScreenName, screenName, emailAddress, locale, firstName,
			middleName, lastName, prefixListTypeId, suffixListTypeId, male,
			birthdayMonth, birthdayDay, birthdayYear, jobTitle,
			updateUserInformation, sendEmail, serviceContext);
	}

	/**
	 * Updates a user account that was automatically created when a guest user
	 * participated in an action (e.g. posting a comment) and only provided his
	 * name and email address.
	 *
	 * @param      companyId the primary key of the user's company
	 * @param      autoPassword whether a password should be automatically
	 *             generated for the user
	 * @param      password1 the user's password
	 * @param      password2 the user's password confirmation
	 * @param      autoScreenName whether a screen name should be automatically
	 *             generated for the user
	 * @param      screenName the user's screen name
	 * @param      emailAddress the user's email address
	 * @param      facebookId the user's facebook ID
	 * @param      openId the user's OpenID
	 * @param      locale the user's locale
	 * @param      firstName the user's first name
	 * @param      middleName the user's middle name
	 * @param      lastName the user's last name
	 * @param      prefixListTypeId the user's name prefix ID
	 * @param      suffixListTypeId the user's name suffix ID
	 * @param      male whether the user is male
	 * @param      birthdayMonth the user's birthday month (0-based, meaning 0
	 *             for January)
	 * @param      birthdayDay the user's birthday day
	 * @param      birthdayYear the user's birthday year
	 * @param      jobTitle the user's job title
	 * @param      updateUserInformation whether to update the user's
	 *             information
	 * @param      sendEmail whether to send the user an email notification
	 *             about their new account
	 * @param      serviceContext the service context to be applied (optionally
	 *             <code>null</code>). Can set the expando bridge attributes for
	 *             the user.
	 * @return     the user
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 *             #updateIncompleteUser(long, long, boolean, String, String,
	 *             boolean, String, String, Locale, String, String, String,
	 *             long, long, boolean, int, int, int, String, boolean, boolean,
	 *             ServiceContext)}
	 */
	@Deprecated
	@Override
	public User updateIncompleteUser(
			long companyId, boolean autoPassword, String password1,
			String password2, boolean autoScreenName, String screenName,
			String emailAddress, long facebookId, String openId, Locale locale,
			String firstName, String middleName, String lastName,
			long prefixListTypeId, long suffixListTypeId, boolean male,
			int birthdayMonth, int birthdayDay, int birthdayYear,
			String jobTitle, boolean updateUserInformation, boolean sendEmail,
			ServiceContext serviceContext)
		throws PortalException {

		long creatorUserId = 0;

		try {
			creatorUserId = getGuestOrUserId();
		}
		catch (PrincipalException principalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to get guest or current user ID",
					principalException);
			}
		}

		checkAddUserPermission(
			creatorUserId, companyId, emailAddress, null, null, null, null,
			serviceContext);

		return userLocalService.updateIncompleteUser(
			creatorUserId, companyId, autoPassword, password1, password2,
			autoScreenName, screenName, emailAddress, locale, firstName,
			middleName, lastName, prefixListTypeId, suffixListTypeId, male,
			birthdayMonth, birthdayDay, birthdayYear, jobTitle,
			updateUserInformation, sendEmail, serviceContext);
	}

	@Override
	public User updateLanguageId(long userId, String languageId)
		throws PortalException {

		UserPermissionUtil.check(
			getPermissionChecker(), userId, ActionKeys.UPDATE);

		return userLocalService.updateLanguageId(userId, languageId);
	}

	/**
	 * Updates whether the user is locked out from logging in.
	 *
	 * @param  userId the primary key of the user
	 * @param  lockout whether the user is locked out
	 * @return the user
	 */
	@Override
	public User updateLockoutById(long userId, boolean lockout)
		throws PortalException {

		UserPermissionUtil.check(
			getPermissionChecker(), userId, ActionKeys.DELETE);

		return userLocalService.updateLockoutById(userId, lockout);
	}

	/**
	 * Sets the organizations that the user is in, removing and adding
	 * organizations as necessary.
	 *
	 * @param userId the primary key of the user
	 * @param organizationIds the primary keys of the organizations
	 * @param serviceContext the service context to be applied. Must set whether
	 *        user indexing is enabled.
	 */
	@Override
	public void updateOrganizations(
			long userId, long[] organizationIds, ServiceContext serviceContext)
		throws PortalException {

		UserPermissionUtil.check(
			getPermissionChecker(), userId, ActionKeys.UPDATE);

		checkOrganizations(userId, organizationIds);

		userLocalService.updateOrganizations(
			userId, organizationIds, serviceContext);
	}

	/**
	 * Updates the user's password without tracking or validation of the change.
	 *
	 * @param  userId the primary key of the user
	 * @param  password1 the user's new password
	 * @param  password2 the user's new password confirmation
	 * @param  passwordReset whether the user should be asked to reset their
	 *         password the next time they log in
	 * @return the user
	 */
	@CTAware(onProduction = true)
	@Override
	public User updatePassword(
			long userId, String password1, String password2,
			boolean passwordReset)
		throws PortalException {

		UserPermissionUtil.check(
			getPermissionChecker(), userId, ActionKeys.UPDATE);

		return userLocalService.updatePassword(
			userId, password1, password2, passwordReset);
	}

	/**
	 * Updates the user's portrait image.
	 *
	 * @param  userId the primary key of the user
	 * @param  bytes the new portrait image data
	 * @return the user
	 */
	@Override
	public User updatePortrait(long userId, byte[] bytes)
		throws PortalException {

		UserPermissionUtil.check(
			getPermissionChecker(), userId, ActionKeys.UPDATE);

		return userLocalService.updatePortrait(userId, bytes);
	}

	/**
	 * Updates the user's password reset question and answer.
	 *
	 * @param  userId the primary key of the user
	 * @param  question the user's new password reset question
	 * @param  answer the user's new password reset answer
	 * @return the user
	 */
	@CTAware(onProduction = true)
	@Override
	public User updateReminderQuery(long userId, String question, String answer)
		throws PortalException {

		UserPermissionUtil.check(
			getPermissionChecker(), userId, ActionKeys.UPDATE);

		return userLocalService.updateReminderQuery(userId, question, answer);
	}

	/**
	 * Updates the user's screen name.
	 *
	 * @param  userId the primary key of the user
	 * @param  screenName the user's new screen name
	 * @return the user
	 */
	@Override
	public User updateScreenName(long userId, String screenName)
		throws PortalException {

		UserPermissionUtil.check(
			getPermissionChecker(), userId, ActionKeys.UPDATE);

		return userLocalService.updateScreenName(userId, screenName);
	}

	/**
	 * Updates the user's workflow status.
	 *
	 * @param  userId the primary key of the user
	 * @param  status the user's new workflow status
	 * @param  serviceContext the service context to be applied. You can specify
	 *         an unencrypted custom password (used by an LDAP listener) for the
	 *         user via attribute <code>passwordUnencrypted</code>.
	 * @return the user
	 */
	@Override
	public User updateStatus(
			long userId, int status, ServiceContext serviceContext)
		throws PortalException {

		return updateStatus(
			userPersistence.findByPrimaryKey(userId), status, serviceContext);
	}

	@Override
	public User updateStatus(
			User user, int status, ServiceContext serviceContext)
		throws PortalException {

		long userId = user.getUserId();

		if ((getUserId() == userId) &&
			(status != WorkflowConstants.STATUS_APPROVED)) {

			throw new RequiredUserException();
		}

		if ((status == WorkflowConstants.STATUS_APPROVED) &&
			!UserPermissionUtil.contains(
				getPermissionChecker(), userId, ActionKeys.ACTIVATE) &&
			!UserPermissionUtil.contains(
				getPermissionChecker(), userId, ActionKeys.DELETE)) {

			throw new PrincipalException.MustHavePermission(
				getPermissionChecker(), User.class.getName(), userId,
				ActionKeys.ACTIVATE, ActionKeys.DELETE);
		}

		if ((status == WorkflowConstants.STATUS_INACTIVE) &&
			!UserPermissionUtil.contains(
				getPermissionChecker(), userId, ActionKeys.DEACTIVATE) &&
			!UserPermissionUtil.contains(
				getPermissionChecker(), userId, ActionKeys.DELETE)) {

			throw new PrincipalException.MustHavePermission(
				getPermissionChecker(), User.class.getName(), userId,
				ActionKeys.DEACTIVATE, ActionKeys.DELETE);
		}

		if ((status != WorkflowConstants.STATUS_APPROVED) &&
			(status != WorkflowConstants.STATUS_INACTIVE)) {

			UserPermissionUtil.check(
				getPermissionChecker(), userId, ActionKeys.DELETE);
		}

		return userLocalService.updateStatus(user, status, serviceContext);
	}

	/**
	 * Updates the user with additional parameters.
	 *
	 * @param  userId the primary key of the user
	 * @param  oldPassword the user's old password
	 * @param  newPassword1 the user's new password (optionally
	 *         <code>null</code>)
	 * @param  newPassword2 the user's new password confirmation (optionally
	 *         <code>null</code>)
	 * @param  passwordReset whether the user should be asked to reset their
	 *         password the next time they login
	 * @param  reminderQueryQuestion the user's new password reset question
	 * @param  reminderQueryAnswer the user's new password reset answer
	 * @param  screenName the user's new screen name
	 * @param  emailAddress the user's new email address
	 * @param  hasPortrait if the user has a custom portrait image
	 * @param  portraitBytes the new portrait image data
	 * @param  languageId the user's new language ID
	 * @param  timeZoneId the user's new time zone ID
	 * @param  greeting the user's new greeting
	 * @param  comments the user's new comments
	 * @param  firstName the user's new first name
	 * @param  middleName the user's new middle name
	 * @param  lastName the user's new last name
	 * @param  prefixListTypeId the user's new name prefix ID
	 * @param  suffixListTypeId the user's new name suffix ID
	 * @param  male whether user is male
	 * @param  birthdayMonth the user's new birthday month (0-based, meaning 0
	 *         for January)
	 * @param  birthdayDay the user's new birthday day
	 * @param  birthdayYear the user's birthday year
	 * @param  smsSn the user's new SMS screen name
	 * @param  facebookSn the user's new Facebook screen name
	 * @param  jabberSn the user's new Jabber screen name
	 * @param  skypeSn the user's new Skype screen name
	 * @param  twitterSn the user's new Twitter screen name
	 * @param  jobTitle the user's new job title
	 * @param  groupIds the primary keys of the user's groups
	 * @param  organizationIds the primary keys of the user's organizations
	 * @param  roleIds the primary keys of the user's roles
	 * @param  userGroupRoles the user user's group roles
	 * @param  userGroupIds the primary keys of the user's user groups
	 * @param  addresses the user's addresses
	 * @param  emailAddresses the user's email addresses
	 * @param  phones the user's phone numbers
	 * @param  websites the user's websites
	 * @param  announcementsDelivers the announcements deliveries
	 * @param  serviceContext the service context to be applied (optionally
	 *         <code>null</code>). Can set the UUID (with the <code>uuid</code>
	 *         attribute), asset category IDs, asset tag names, and expando
	 *         bridge attributes for the user.
	 * @return the user
	 */
	@Override
	public User updateUser(
			long userId, String oldPassword, String newPassword1,
			String newPassword2, boolean passwordReset,
			String reminderQueryQuestion, String reminderQueryAnswer,
			String screenName, String emailAddress, boolean hasPortrait,
			byte[] portraitBytes, String languageId, String timeZoneId,
			String greeting, String comments, String firstName,
			String middleName, String lastName, long prefixListTypeId,
			long suffixListTypeId, boolean male, int birthdayMonth,
			int birthdayDay, int birthdayYear, String smsSn, String facebookSn,
			String jabberSn, String skypeSn, String twitterSn, String jobTitle,
			long[] groupIds, long[] organizationIds, long[] roleIds,
			List<UserGroupRole> userGroupRoles, long[] userGroupIds,
			List<Address> addresses, List<EmailAddress> emailAddresses,
			List<Phone> phones, List<Website> websites,
			List<AnnouncementsDelivery> announcementsDelivers,
			ServiceContext serviceContext)
		throws PortalException {

		UserPermissionUtil.check(
			getPermissionChecker(), userId, organizationIds, ActionKeys.UPDATE);

		User user = userPersistence.findByPrimaryKey(userId);

		if (addresses != null) {
			UsersAdminUtil.updateAddresses(
				Contact.class.getName(), user.getContactId(), addresses,
				ListTypeConstants.CONTACT_ADDRESS);
		}

		if (emailAddresses != null) {
			UsersAdminUtil.updateEmailAddresses(
				Contact.class.getName(), user.getContactId(), emailAddresses);
		}

		if (phones != null) {
			UsersAdminUtil.updatePhones(
				Contact.class.getName(), user.getContactId(), phones);
		}

		if (websites != null) {
			UsersAdminUtil.updateWebsites(
				Contact.class.getName(), user.getContactId(), websites);
		}

		if (announcementsDelivers != null) {
			updateAnnouncementsDeliveries(
				user.getUserId(), announcementsDelivers);
		}

		long curUserId = getUserId();

		if (curUserId == userId) {
			emailAddress = StringUtil.toLowerCase(
				StringUtil.trim(emailAddress));

			if (!StringUtil.equalsIgnoreCase(
					emailAddress, user.getEmailAddress())) {

				validateEmailAddress(user, emailAddress);
			}
		}

		validateUpdatePermission(
			user, screenName, emailAddress, firstName, middleName, lastName,
			prefixListTypeId, suffixListTypeId, birthdayMonth, birthdayDay,
			birthdayYear, male, jobTitle);

		// Group membership policy

		List<Long> addGroupIds = new ArrayList<>();
		List<Long> removeGroupIds = Collections.emptyList();

		if (groupIds != null) {
			long[] oldGroupIds = user.getGroupIds();

			removeGroupIds = ListUtil.fromArray(oldGroupIds);

			groupIds = checkGroups(userId, groupIds);

			for (long groupId : groupIds) {
				if (ArrayUtil.contains(oldGroupIds, groupId)) {
					removeGroupIds.remove(groupId);
				}
				else {
					addGroupIds.add(groupId);
				}
			}

			if (!addGroupIds.isEmpty() || !removeGroupIds.isEmpty()) {
				SiteMembershipPolicyUtil.checkMembership(
					new long[] {userId}, ArrayUtil.toLongArray(addGroupIds),
					ArrayUtil.toLongArray(removeGroupIds));
			}
		}

		// Organization membership policy

		List<Long> addOrganizationIds = new ArrayList<>();
		List<Long> removeOrganizationIds = Collections.emptyList();

		if (organizationIds != null) {
			long[] oldOrganizationIds = user.getOrganizationIds();

			removeOrganizationIds = ListUtil.fromArray(oldOrganizationIds);

			organizationIds = checkOrganizations(userId, organizationIds);

			for (long organizationId : organizationIds) {
				if (ArrayUtil.contains(oldOrganizationIds, organizationId)) {
					removeOrganizationIds.remove(organizationId);
				}
				else {
					addOrganizationIds.add(organizationId);
				}
			}

			if (!addOrganizationIds.isEmpty() ||
				!removeOrganizationIds.isEmpty()) {

				OrganizationMembershipPolicyUtil.checkMembership(
					new long[] {userId},
					ArrayUtil.toLongArray(addOrganizationIds),
					ArrayUtil.toLongArray(removeOrganizationIds));
			}
		}

		// Role membership policy

		List<Long> addRoleIds = new ArrayList<>();
		List<Long> removeRoleIds = Collections.emptyList();

		if (roleIds != null) {
			long[] oldRoleIds = user.getRoleIds();

			removeRoleIds = ListUtil.fromArray(oldRoleIds);

			roleIds = checkRoles(userId, roleIds);

			for (long roleId : roleIds) {
				if (ArrayUtil.contains(oldRoleIds, roleId)) {
					removeRoleIds.remove(roleId);
				}
				else {
					addRoleIds.add(roleId);
				}
			}

			if (!addRoleIds.isEmpty() || !removeRoleIds.isEmpty()) {
				RoleMembershipPolicyUtil.checkRoles(
					new long[] {userId}, ArrayUtil.toLongArray(addRoleIds),
					ArrayUtil.toLongArray(removeRoleIds));
			}
		}

		List<UserGroupRole> oldOrganizationUserGroupRoles = new ArrayList<>();
		List<UserGroupRole> oldSiteUserGroupRoles = new ArrayList<>();

		List<UserGroupRole> oldUserGroupRoles =
			_userGroupRolePersistence.findByUserId(userId);

		for (UserGroupRole oldUserGroupRole : oldUserGroupRoles) {
			Role role = oldUserGroupRole.getRole();

			if (role.getType() == RoleConstants.TYPE_ORGANIZATION) {
				oldOrganizationUserGroupRoles.add(oldUserGroupRole);
			}
			else if (role.getType() == RoleConstants.TYPE_SITE) {
				oldSiteUserGroupRoles.add(oldUserGroupRole);
			}
		}

		List<UserGroupRole> addOrganizationUserGroupRoles = new ArrayList<>();
		List<UserGroupRole> removeOrganizationUserGroupRoles =
			Collections.emptyList();
		List<UserGroupRole> addSiteUserGroupRoles = new ArrayList<>();
		List<UserGroupRole> removeSiteUserGroupRoles = Collections.emptyList();

		if (userGroupRoles != null) {
			userGroupRoles = checkUserGroupRoles(userId, userGroupRoles);

			removeOrganizationUserGroupRoles = ListUtil.copy(
				oldOrganizationUserGroupRoles);
			removeSiteUserGroupRoles = ListUtil.copy(oldSiteUserGroupRoles);

			for (UserGroupRole userGroupRole : userGroupRoles) {
				Role role = userGroupRole.getRole();

				if (role.getType() == RoleConstants.TYPE_ORGANIZATION) {
					if (oldOrganizationUserGroupRoles.contains(userGroupRole)) {
						removeOrganizationUserGroupRoles.remove(userGroupRole);
					}
					else {
						addOrganizationUserGroupRoles.add(userGroupRole);
					}
				}
				else if (role.getType() == RoleConstants.TYPE_SITE) {
					if (oldSiteUserGroupRoles.contains(userGroupRole)) {
						removeSiteUserGroupRoles.remove(userGroupRole);
					}
					else {
						addSiteUserGroupRoles.add(userGroupRole);
					}
				}
			}

			if (!addOrganizationUserGroupRoles.isEmpty() ||
				!removeOrganizationUserGroupRoles.isEmpty()) {

				OrganizationMembershipPolicyUtil.checkRoles(
					addOrganizationUserGroupRoles,
					removeOrganizationUserGroupRoles);
			}

			if (!addSiteUserGroupRoles.isEmpty() ||
				!removeSiteUserGroupRoles.isEmpty()) {

				SiteMembershipPolicyUtil.checkRoles(
					addSiteUserGroupRoles, removeSiteUserGroupRoles);
			}
		}

		// User group membership policy

		List<Long> addUserGroupIds = new ArrayList<>();
		List<Long> removeUserGroupIds = Collections.emptyList();

		if (userGroupIds != null) {
			long[] oldUserGroupIds = user.getUserGroupIds();

			removeUserGroupIds = ListUtil.fromArray(oldUserGroupIds);

			userGroupIds = checkUserGroupIds(userId, userGroupIds);

			for (long userGroupId : userGroupIds) {
				if (ArrayUtil.contains(oldUserGroupIds, userGroupId)) {
					removeUserGroupIds.remove(userGroupId);
				}
				else {
					addUserGroupIds.add(userGroupId);
				}
			}

			if (!addUserGroupIds.isEmpty() || !removeUserGroupIds.isEmpty()) {
				UserGroupMembershipPolicyUtil.checkMembership(
					new long[] {userId}, ArrayUtil.toLongArray(addUserGroupIds),
					ArrayUtil.toLongArray(removeUserGroupIds));
			}
		}

		user = userLocalService.updateUser(
			userId, oldPassword, newPassword1, newPassword2, passwordReset,
			reminderQueryQuestion, reminderQueryAnswer, screenName,
			emailAddress, hasPortrait, portraitBytes, languageId, timeZoneId,
			greeting, comments, firstName, middleName, lastName,
			prefixListTypeId, suffixListTypeId, male, birthdayMonth,
			birthdayDay, birthdayYear, smsSn, facebookSn, jabberSn, skypeSn,
			twitterSn, jobTitle, groupIds, organizationIds, roleIds,
			userGroupRoles, userGroupIds, serviceContext);

		if (!addGroupIds.isEmpty() || !removeGroupIds.isEmpty()) {
			SiteMembershipPolicyUtil.propagateMembership(
				new long[] {user.getUserId()},
				ArrayUtil.toLongArray(addGroupIds),
				ArrayUtil.toLongArray(removeGroupIds));
		}

		if (!addOrganizationIds.isEmpty() || !removeOrganizationIds.isEmpty()) {
			OrganizationMembershipPolicyUtil.propagateMembership(
				new long[] {user.getUserId()},
				ArrayUtil.toLongArray(addOrganizationIds),
				ArrayUtil.toLongArray(removeOrganizationIds));
		}

		if (!addRoleIds.isEmpty() || !removeRoleIds.isEmpty()) {
			RoleMembershipPolicyUtil.propagateRoles(
				new long[] {user.getUserId()},
				ArrayUtil.toLongArray(addRoleIds),
				ArrayUtil.toLongArray(removeRoleIds));
		}

		if (!addSiteUserGroupRoles.isEmpty() ||
			!removeSiteUserGroupRoles.isEmpty()) {

			SiteMembershipPolicyUtil.propagateRoles(
				addSiteUserGroupRoles, removeSiteUserGroupRoles);
		}

		if (!addOrganizationUserGroupRoles.isEmpty() ||
			!removeOrganizationUserGroupRoles.isEmpty()) {

			OrganizationMembershipPolicyUtil.propagateRoles(
				addOrganizationUserGroupRoles,
				removeOrganizationUserGroupRoles);
		}

		if (!addUserGroupIds.isEmpty() || !removeUserGroupIds.isEmpty()) {
			UserGroupMembershipPolicyUtil.propagateMembership(
				new long[] {user.getUserId()},
				ArrayUtil.toLongArray(addUserGroupIds),
				ArrayUtil.toLongArray(removeUserGroupIds));
		}

		return user;
	}

	/**
	 * Updates the user with additional parameters.
	 *
	 * @param      userId the primary key of the user
	 * @param      oldPassword the user's old password
	 * @param      newPassword1 the user's new password (optionally
	 *             <code>null</code>)
	 * @param      newPassword2 the user's new password confirmation (optionally
	 *             <code>null</code>)
	 * @param      passwordReset whether the user should be asked to reset their
	 *             password the next time they login
	 * @param      reminderQueryQuestion the user's new password reset question
	 * @param      reminderQueryAnswer the user's new password reset answer
	 * @param      screenName the user's new screen name
	 * @param      emailAddress the user's new email address
	 * @param      hasPortrait if the user has a custom portrait image
	 * @param      portraitBytes the new portrait image data
	 * @param      languageId the user's new language ID
	 * @param      timeZoneId the user's new time zone ID
	 * @param      greeting the user's new greeting
	 * @param      comments the user's new comments
	 * @param      firstName the user's new first name
	 * @param      middleName the user's new middle name
	 * @param      lastName the user's new last name
	 * @param      prefixListTypeId the user's new name prefix ID
	 * @param      suffixListTypeId the user's new name suffix ID
	 * @param      male whether user is male
	 * @param      birthdayMonth the user's new birthday month (0-based, meaning
	 *             0 for January)
	 * @param      birthdayDay the user's new birthday day
	 * @param      birthdayYear the user's birthday year
	 * @param      smsSn the user's new SMS screen name
	 * @param      facebookSn the user's new Facebook screen name
	 * @param      jabberSn the user's new Jabber screen name
	 * @param      skypeSn the user's new Skype screen name
	 * @param      twitterSn the user's new Twitter screen name
	 * @param      jobTitle the user's new job title
	 * @param      groupIds the primary keys of the user's groups
	 * @param      organizationIds the primary keys of the user's organizations
	 * @param      roleIds the primary keys of the user's roles
	 * @param      userGroupRoles the user user's group roles
	 * @param      userGroupIds the primary keys of the user's user groups
	 * @param      addresses the user's addresses
	 * @param      emailAddresses the user's email addresses
	 * @param      phones the user's phone numbers
	 * @param      websites the user's websites
	 * @param      announcementsDelivers the announcements deliveries
	 * @param      serviceContext the service context to be applied (optionally
	 *             <code>null</code>). Can set the UUID (with the
	 *             <code>uuid</code> attribute), asset category IDs, asset tag
	 *             names, and expando bridge attributes for the user.
	 * @return     the user
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 *             #updateUser(long, String, String, String, boolean, String,
	 *             String, String, String, boolean, byte[], String, String,
	 *             String, String, String, String, String, long, long, boolean,
	 *             int, int, int, String, String, String, String, String,
	 *             String, long[], long[], long[], List, long[], List, List,
	 *             List, List, List, ServiceContext)}
	 */
	@Deprecated
	@Override
	public User updateUser(
			long userId, String oldPassword, String newPassword1,
			String newPassword2, boolean passwordReset,
			String reminderQueryQuestion, String reminderQueryAnswer,
			String screenName, String emailAddress, long facebookId,
			String openId, boolean hasPortrait, byte[] portraitBytes,
			String languageId, String timeZoneId, String greeting,
			String comments, String firstName, String middleName,
			String lastName, long prefixListTypeId, long suffixListTypeId,
			boolean male, int birthdayMonth, int birthdayDay, int birthdayYear,
			String smsSn, String facebookSn, String jabberSn, String skypeSn,
			String twitterSn, String jobTitle, long[] groupIds,
			long[] organizationIds, long[] roleIds,
			List<UserGroupRole> userGroupRoles, long[] userGroupIds,
			List<Address> addresses, List<EmailAddress> emailAddresses,
			List<Phone> phones, List<Website> websites,
			List<AnnouncementsDelivery> announcementsDelivers,
			ServiceContext serviceContext)
		throws PortalException {

		UserPermissionUtil.check(
			getPermissionChecker(), userId, organizationIds, ActionKeys.UPDATE);

		User user = userPersistence.findByPrimaryKey(userId);

		if (addresses != null) {
			UsersAdminUtil.updateAddresses(
				Contact.class.getName(), user.getContactId(), addresses,
				ListTypeConstants.CONTACT_ADDRESS);
		}

		if (emailAddresses != null) {
			UsersAdminUtil.updateEmailAddresses(
				Contact.class.getName(), user.getContactId(), emailAddresses);
		}

		if (phones != null) {
			UsersAdminUtil.updatePhones(
				Contact.class.getName(), user.getContactId(), phones);
		}

		if (websites != null) {
			UsersAdminUtil.updateWebsites(
				Contact.class.getName(), user.getContactId(), websites);
		}

		if (announcementsDelivers != null) {
			updateAnnouncementsDeliveries(
				user.getUserId(), announcementsDelivers);
		}

		long curUserId = getUserId();

		if (curUserId == userId) {
			emailAddress = StringUtil.toLowerCase(
				StringUtil.trim(emailAddress));

			if (!StringUtil.equalsIgnoreCase(
					emailAddress, user.getEmailAddress())) {

				validateEmailAddress(user, emailAddress);
			}
		}

		validateUpdatePermission(
			user, screenName, emailAddress, firstName, middleName, lastName,
			prefixListTypeId, suffixListTypeId, birthdayMonth, birthdayDay,
			birthdayYear, male, jobTitle);

		// Group membership policy

		List<Long> addGroupIds = new ArrayList<>();
		List<Long> removeGroupIds = Collections.emptyList();

		if (groupIds != null) {
			long[] oldGroupIds = user.getGroupIds();

			removeGroupIds = ListUtil.fromArray(oldGroupIds);

			groupIds = checkGroups(userId, groupIds);

			for (long groupId : groupIds) {
				if (ArrayUtil.contains(oldGroupIds, groupId)) {
					removeGroupIds.remove(groupId);
				}
				else {
					addGroupIds.add(groupId);
				}
			}

			if (!addGroupIds.isEmpty() || !removeGroupIds.isEmpty()) {
				SiteMembershipPolicyUtil.checkMembership(
					new long[] {userId}, ArrayUtil.toLongArray(addGroupIds),
					ArrayUtil.toLongArray(removeGroupIds));
			}
		}

		// Organization membership policy

		List<Long> addOrganizationIds = new ArrayList<>();
		List<Long> removeOrganizationIds = Collections.emptyList();

		if (organizationIds != null) {
			long[] oldOrganizationIds = user.getOrganizationIds();

			removeOrganizationIds = ListUtil.fromArray(oldOrganizationIds);

			organizationIds = checkOrganizations(userId, organizationIds);

			for (long organizationId : organizationIds) {
				if (ArrayUtil.contains(oldOrganizationIds, organizationId)) {
					removeOrganizationIds.remove(organizationId);
				}
				else {
					addOrganizationIds.add(organizationId);
				}
			}

			if (!addOrganizationIds.isEmpty() ||
				!removeOrganizationIds.isEmpty()) {

				OrganizationMembershipPolicyUtil.checkMembership(
					new long[] {userId},
					ArrayUtil.toLongArray(addOrganizationIds),
					ArrayUtil.toLongArray(removeOrganizationIds));
			}
		}

		// Role membership policy

		List<Long> addRoleIds = new ArrayList<>();
		List<Long> removeRoleIds = Collections.emptyList();

		if (roleIds != null) {
			long[] oldRoleIds = user.getRoleIds();

			removeRoleIds = ListUtil.fromArray(oldRoleIds);

			roleIds = checkRoles(userId, roleIds);

			for (long roleId : roleIds) {
				if (ArrayUtil.contains(oldRoleIds, roleId)) {
					removeRoleIds.remove(roleId);
				}
				else {
					addRoleIds.add(roleId);
				}
			}

			if (!addRoleIds.isEmpty() || !removeRoleIds.isEmpty()) {
				RoleMembershipPolicyUtil.checkRoles(
					new long[] {userId}, ArrayUtil.toLongArray(addRoleIds),
					ArrayUtil.toLongArray(removeRoleIds));
			}
		}

		List<UserGroupRole> oldOrganizationUserGroupRoles = new ArrayList<>();
		List<UserGroupRole> oldSiteUserGroupRoles = new ArrayList<>();

		List<UserGroupRole> oldUserGroupRoles =
			_userGroupRolePersistence.findByUserId(userId);

		for (UserGroupRole oldUserGroupRole : oldUserGroupRoles) {
			Role role = oldUserGroupRole.getRole();

			if (role.getType() == RoleConstants.TYPE_ORGANIZATION) {
				oldOrganizationUserGroupRoles.add(oldUserGroupRole);
			}
			else if (role.getType() == RoleConstants.TYPE_SITE) {
				oldSiteUserGroupRoles.add(oldUserGroupRole);
			}
		}

		List<UserGroupRole> addOrganizationUserGroupRoles = new ArrayList<>();
		List<UserGroupRole> removeOrganizationUserGroupRoles =
			Collections.emptyList();
		List<UserGroupRole> addSiteUserGroupRoles = new ArrayList<>();
		List<UserGroupRole> removeSiteUserGroupRoles = Collections.emptyList();

		if (userGroupRoles != null) {
			userGroupRoles = checkUserGroupRoles(userId, userGroupRoles);

			removeOrganizationUserGroupRoles = ListUtil.copy(
				oldOrganizationUserGroupRoles);
			removeSiteUserGroupRoles = ListUtil.copy(oldSiteUserGroupRoles);

			for (UserGroupRole userGroupRole : userGroupRoles) {
				Role role = userGroupRole.getRole();

				if (role.getType() == RoleConstants.TYPE_ORGANIZATION) {
					if (oldOrganizationUserGroupRoles.contains(userGroupRole)) {
						removeOrganizationUserGroupRoles.remove(userGroupRole);
					}
					else {
						addOrganizationUserGroupRoles.add(userGroupRole);
					}
				}
				else if (role.getType() == RoleConstants.TYPE_SITE) {
					if (oldSiteUserGroupRoles.contains(userGroupRole)) {
						removeSiteUserGroupRoles.remove(userGroupRole);
					}
					else {
						addSiteUserGroupRoles.add(userGroupRole);
					}
				}
			}

			if (!addOrganizationUserGroupRoles.isEmpty() ||
				!removeOrganizationUserGroupRoles.isEmpty()) {

				OrganizationMembershipPolicyUtil.checkRoles(
					addOrganizationUserGroupRoles,
					removeOrganizationUserGroupRoles);
			}

			if (!addSiteUserGroupRoles.isEmpty() ||
				!removeSiteUserGroupRoles.isEmpty()) {

				SiteMembershipPolicyUtil.checkRoles(
					addSiteUserGroupRoles, removeSiteUserGroupRoles);
			}
		}

		// User group membership policy

		List<Long> addUserGroupIds = new ArrayList<>();
		List<Long> removeUserGroupIds = Collections.emptyList();

		if (userGroupIds != null) {
			long[] oldUserGroupIds = user.getUserGroupIds();

			removeUserGroupIds = ListUtil.fromArray(oldUserGroupIds);

			userGroupIds = checkUserGroupIds(userId, userGroupIds);

			for (long userGroupId : userGroupIds) {
				if (ArrayUtil.contains(oldUserGroupIds, userGroupId)) {
					removeUserGroupIds.remove(userGroupId);
				}
				else {
					addUserGroupIds.add(userGroupId);
				}
			}

			if (!addUserGroupIds.isEmpty() || !removeUserGroupIds.isEmpty()) {
				UserGroupMembershipPolicyUtil.checkMembership(
					new long[] {userId}, ArrayUtil.toLongArray(addUserGroupIds),
					ArrayUtil.toLongArray(removeUserGroupIds));
			}
		}

		user = userLocalService.updateUser(
			userId, oldPassword, newPassword1, newPassword2, passwordReset,
			reminderQueryQuestion, reminderQueryAnswer, screenName,
			emailAddress, hasPortrait, portraitBytes, languageId, timeZoneId,
			greeting, comments, firstName, middleName, lastName,
			prefixListTypeId, suffixListTypeId, male, birthdayMonth,
			birthdayDay, birthdayYear, smsSn, facebookSn, jabberSn, skypeSn,
			twitterSn, jobTitle, groupIds, organizationIds, roleIds,
			userGroupRoles, userGroupIds, serviceContext);

		if (!addGroupIds.isEmpty() || !removeGroupIds.isEmpty()) {
			SiteMembershipPolicyUtil.propagateMembership(
				new long[] {user.getUserId()},
				ArrayUtil.toLongArray(addGroupIds),
				ArrayUtil.toLongArray(removeGroupIds));
		}

		if (!addOrganizationIds.isEmpty() || !removeOrganizationIds.isEmpty()) {
			OrganizationMembershipPolicyUtil.propagateMembership(
				new long[] {user.getUserId()},
				ArrayUtil.toLongArray(addOrganizationIds),
				ArrayUtil.toLongArray(removeOrganizationIds));
		}

		if (!addRoleIds.isEmpty() || !removeRoleIds.isEmpty()) {
			RoleMembershipPolicyUtil.propagateRoles(
				new long[] {user.getUserId()},
				ArrayUtil.toLongArray(addRoleIds),
				ArrayUtil.toLongArray(removeRoleIds));
		}

		if (!addSiteUserGroupRoles.isEmpty() ||
			!removeSiteUserGroupRoles.isEmpty()) {

			SiteMembershipPolicyUtil.propagateRoles(
				addSiteUserGroupRoles, removeSiteUserGroupRoles);
		}

		if (!addOrganizationUserGroupRoles.isEmpty() ||
			!removeOrganizationUserGroupRoles.isEmpty()) {

			OrganizationMembershipPolicyUtil.propagateRoles(
				addOrganizationUserGroupRoles,
				removeOrganizationUserGroupRoles);
		}

		if (!addUserGroupIds.isEmpty() || !removeUserGroupIds.isEmpty()) {
			UserGroupMembershipPolicyUtil.propagateMembership(
				new long[] {user.getUserId()},
				ArrayUtil.toLongArray(addUserGroupIds),
				ArrayUtil.toLongArray(removeUserGroupIds));
		}

		return user;
	}

	/**
	 * Updates the user.
	 *
	 * @param      userId the primary key of the user
	 * @param      oldPassword the user's old password
	 * @param      newPassword1 the user's new password (optionally
	 *             <code>null</code>)
	 * @param      newPassword2 the user's new password confirmation (optionally
	 *             <code>null</code>)
	 * @param      passwordReset whether the user should be asked to reset their
	 *             password the next time they login
	 * @param      reminderQueryQuestion the user's new password reset question
	 * @param      reminderQueryAnswer the user's new password reset answer
	 * @param      screenName the user's new screen name
	 * @param      emailAddress the user's new email address
	 * @param      languageId the user's new language ID
	 * @param      timeZoneId the user's new time zone ID
	 * @param      greeting the user's new greeting
	 * @param      comments the user's new comments
	 * @param      firstName the user's new first name
	 * @param      middleName the user's new middle name
	 * @param      lastName the user's new last name
	 * @param      prefixListTypeId the user's new name prefix ID
	 * @param      suffixListTypeId the user's new name suffix ID
	 * @param      male whether user is male
	 * @param      birthdayMonth the user's new birthday month (0-based, meaning
	 *             0 for January)
	 * @param      birthdayDay the user's new birthday day
	 * @param      birthdayYear the user's birthday year
	 * @param      smsSn the user's new SMS screen name
	 * @param      facebookSn the user's new Facebook screen name
	 * @param      jabberSn the user's new Jabber screen name
	 * @param      skypeSn the user's new Skype screen name
	 * @param      twitterSn the user's new Twitter screen name
	 * @param      jobTitle the user's new job title
	 * @param      groupIds the primary keys of the user's groups
	 * @param      organizationIds the primary keys of the user's organizations
	 * @param      roleIds the primary keys of the user's roles
	 * @param      userGroupRoles the user user's group roles
	 * @param      userGroupIds the primary keys of the user's user groups
	 * @param      serviceContext the service context to be applied (optionally
	 *             <code>null</code>). Can set the UUID (with the
	 *             <code>uuid</code> attribute), asset category IDs, asset tag
	 *             names, and expando bridge attributes for the user.
	 * @return     the user
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 *             #updateUser(long, String, String, String, boolean, String,
	 *             String, String, String, String, String, String, String,
	 *             String, String, String, long, long, boolean, int, int, int,
	 *             String, String, String, String, String, String, long[],
	 *             long[], long[], List, long[], ServiceContext)}
	 */
	@Deprecated
	@Override
	public User updateUser(
			long userId, String oldPassword, String newPassword1,
			String newPassword2, boolean passwordReset,
			String reminderQueryQuestion, String reminderQueryAnswer,
			String screenName, String emailAddress, long facebookId,
			String openId, String languageId, String timeZoneId,
			String greeting, String comments, String firstName,
			String middleName, String lastName, long prefixListTypeId,
			long suffixListTypeId, boolean male, int birthdayMonth,
			int birthdayDay, int birthdayYear, String smsSn, String facebookSn,
			String jabberSn, String skypeSn, String twitterSn, String jobTitle,
			long[] groupIds, long[] organizationIds, long[] roleIds,
			List<UserGroupRole> userGroupRoles, long[] userGroupIds,
			ServiceContext serviceContext)
		throws PortalException {

		return updateUser(
			userId, oldPassword, newPassword1, newPassword2, passwordReset,
			reminderQueryQuestion, reminderQueryAnswer, screenName,
			emailAddress, facebookId, openId, true, null, languageId,
			timeZoneId, greeting, comments, firstName, middleName, lastName,
			prefixListTypeId, suffixListTypeId, male, birthdayMonth,
			birthdayDay, birthdayYear, smsSn, facebookSn, jabberSn, skypeSn,
			twitterSn, jobTitle, groupIds, organizationIds, roleIds,
			userGroupRoles, userGroupIds, null, null, null, null, null,
			serviceContext);
	}

	/**
	 * Updates the user.
	 *
	 * @param  userId the primary key of the user
	 * @param  oldPassword the user's old password
	 * @param  newPassword1 the user's new password (optionally
	 *         <code>null</code>)
	 * @param  newPassword2 the user's new password confirmation (optionally
	 *         <code>null</code>)
	 * @param  passwordReset whether the user should be asked to reset their
	 *         password the next time they login
	 * @param  reminderQueryQuestion the user's new password reset question
	 * @param  reminderQueryAnswer the user's new password reset answer
	 * @param  screenName the user's new screen name
	 * @param  emailAddress the user's new email address
	 * @param  languageId the user's new language ID
	 * @param  timeZoneId the user's new time zone ID
	 * @param  greeting the user's new greeting
	 * @param  comments the user's new comments
	 * @param  firstName the user's new first name
	 * @param  middleName the user's new middle name
	 * @param  lastName the user's new last name
	 * @param  prefixListTypeId the user's new name prefix ID
	 * @param  suffixListTypeId the user's new name suffix ID
	 * @param  male whether user is male
	 * @param  birthdayMonth the user's new birthday month (0-based, meaning 0
	 *         for January)
	 * @param  birthdayDay the user's new birthday day
	 * @param  birthdayYear the user's birthday year
	 * @param  smsSn the user's new SMS screen name
	 * @param  facebookSn the user's new Facebook screen name
	 * @param  jabberSn the user's new Jabber screen name
	 * @param  skypeSn the user's new Skype screen name
	 * @param  twitterSn the user's new Twitter screen name
	 * @param  jobTitle the user's new job title
	 * @param  groupIds the primary keys of the user's groups
	 * @param  organizationIds the primary keys of the user's organizations
	 * @param  roleIds the primary keys of the user's roles
	 * @param  userGroupRoles the user user's group roles
	 * @param  userGroupIds the primary keys of the user's user groups
	 * @param  serviceContext the service context to be applied (optionally
	 *         <code>null</code>). Can set the UUID (with the <code>uuid</code>
	 *         attribute), asset category IDs, asset tag names, and expando
	 *         bridge attributes for the user.
	 * @return the user
	 */
	@Override
	public User updateUser(
			long userId, String oldPassword, String newPassword1,
			String newPassword2, boolean passwordReset,
			String reminderQueryQuestion, String reminderQueryAnswer,
			String screenName, String emailAddress, String languageId,
			String timeZoneId, String greeting, String comments,
			String firstName, String middleName, String lastName,
			long prefixListTypeId, long suffixListTypeId, boolean male,
			int birthdayMonth, int birthdayDay, int birthdayYear, String smsSn,
			String facebookSn, String jabberSn, String skypeSn,
			String twitterSn, String jobTitle, long[] groupIds,
			long[] organizationIds, long[] roleIds,
			List<UserGroupRole> userGroupRoles, long[] userGroupIds,
			ServiceContext serviceContext)
		throws PortalException {

		return updateUser(
			userId, oldPassword, newPassword1, newPassword2, passwordReset,
			reminderQueryQuestion, reminderQueryAnswer, screenName,
			emailAddress, true, null, languageId, timeZoneId, greeting,
			comments, firstName, middleName, lastName, prefixListTypeId,
			suffixListTypeId, male, birthdayMonth, birthdayDay, birthdayYear,
			smsSn, facebookSn, jabberSn, skypeSn, twitterSn, jobTitle, groupIds,
			organizationIds, roleIds, userGroupRoles, userGroupIds, null, null,
			null, null, null, serviceContext);
	}

	protected void checkAddUserPermission(
			long creatorUserId, long companyId, String emailAddress,
			long[] groupIds, long[] organizationIds, long[] roleIds,
			long[] userGroupIds, ServiceContext serviceContext)
		throws PortalException {

		Company company = _companyPersistence.findByPrimaryKey(companyId);

		if (groupIds != null) {
			checkGroups(0, groupIds);
		}

		if (organizationIds != null) {
			checkOrganizations(0, organizationIds);
		}

		if (roleIds != null) {
			checkRoles(0, roleIds);
		}

		if (userGroupIds != null) {
			checkUserGroupIds(0, userGroupIds);
		}

		boolean anonymousUser = ParamUtil.getBoolean(
			serviceContext, "anonymousUser");

		long guestUserId = userLocalService.getGuestUserId(companyId);

		if (((creatorUserId != 0) && (creatorUserId != guestUserId)) ||
			(!company.isStrangers() && !anonymousUser)) {

			PermissionChecker permissionChecker = getPermissionChecker();

			if (!PortalPermissionUtil.contains(
					permissionChecker, ActionKeys.ADD_USER) &&
				!OrganizationPermissionUtil.contains(
					getPermissionChecker(), organizationIds,
					ActionKeys.ASSIGN_MEMBERS)) {

				throw new PrincipalException.MustHavePermission(
					permissionChecker, Organization.class.getName(), 0,
					ActionKeys.ADD_USER, ActionKeys.ASSIGN_MEMBERS);
			}
		}

		if (((creatorUserId == 0) || (creatorUserId == guestUserId)) &&
			!company.isStrangersWithMx() &&
			company.hasCompanyMx(emailAddress)) {

			throw new UserEmailAddressException.MustNotUseCompanyMx(
				emailAddress);
		}

		User creatorUser = getUserById(creatorUserId);

		if (!OmniadminUtil.isOmniadmin(creatorUserId) &&
			(creatorUser.getCompanyId() != companyId)) {

			throw new PrincipalException(
				"Only the omniadmin can add users to another company");
		}
	}

	protected long[] checkGroups(long userId, long[] groupIds)
		throws PortalException {

		long[] oldGroupIds = null;

		PermissionChecker permissionChecker = getPermissionChecker();

		if (userId != CompanyConstants.SYSTEM) {

			// Add back any mandatory groups or groups that the administrator
			// does not have the rights to remove and check that he has the
			// permission to add a new group

			User user = userPersistence.findByPrimaryKey(userId);

			List<Group> oldGroups = _groupLocalService.getUserGroups(userId);

			oldGroupIds = new long[oldGroups.size()];

			for (int i = 0; i < oldGroups.size(); i++) {
				Group group = oldGroups.get(i);

				if (!ArrayUtil.contains(groupIds, group.getGroupId()) &&
					(!GroupPermissionUtil.contains(
						permissionChecker, group, ActionKeys.ASSIGN_MEMBERS) ||
					 SiteMembershipPolicyUtil.isMembershipProtected(
						 permissionChecker, user.getUserId(),
						 group.getGroupId()) ||
					 SiteMembershipPolicyUtil.isMembershipRequired(
						 userId, group.getGroupId()))) {

					groupIds = ArrayUtil.append(groupIds, group.getGroupId());
				}

				oldGroupIds[i] = group.getGroupId();
			}
		}

		// Check that the administrator has the permission to add a new group
		// and that the group membership is allowed

		for (long groupId : groupIds) {
			if ((oldGroupIds != null) &&
				ArrayUtil.contains(oldGroupIds, groupId)) {

				continue;
			}

			Group group = _groupPersistence.findByPrimaryKey(groupId);

			GroupPermissionUtil.check(
				permissionChecker, group, ActionKeys.ASSIGN_MEMBERS);
		}

		return groupIds;
	}

	protected void checkMembership(
			long[] userIds, long[] groupIds, long[] organizationIds,
			long[] roleIds, long[] userGroupIds)
		throws PortalException {

		if (groupIds != null) {
			SiteMembershipPolicyUtil.checkMembership(userIds, groupIds, null);
		}

		if (organizationIds != null) {
			OrganizationMembershipPolicyUtil.checkMembership(
				userIds, organizationIds, null);
		}

		if (roleIds != null) {
			RoleMembershipPolicyUtil.checkRoles(userIds, roleIds, null);
		}

		if (userGroupIds != null) {
			UserGroupMembershipPolicyUtil.checkMembership(
				userIds, userGroupIds, null);
		}
	}

	protected long[] checkOrganizations(long userId, long[] organizationIds)
		throws PortalException {

		long[] oldOrganizationIds = null;

		PermissionChecker permissionChecker = getPermissionChecker();

		boolean strictAssignment = false;

		if (PropsValues.ORGANIZATIONS_ASSIGNMENT_STRICT ||
			!permissionChecker.isCompanyAdmin()) {

			strictAssignment = true;
		}

		if (userId != CompanyConstants.SYSTEM) {

			// Add back any mandatory organizations or organizations that the
			// administrator does not have the rights to remove and check that
			// he has the permission to add a new organization

			List<Organization> oldOrganizations =
				_organizationLocalService.getUserOrganizations(userId);

			oldOrganizationIds = new long[oldOrganizations.size()];

			for (int i = 0; i < oldOrganizations.size(); i++) {
				Organization organization = oldOrganizations.get(i);

				if (!ArrayUtil.contains(
						organizationIds, organization.getOrganizationId()) &&
					(!OrganizationPermissionUtil.contains(
						permissionChecker, organization,
						ActionKeys.ASSIGN_MEMBERS) ||
					 (strictAssignment &&
					  !OrganizationPermissionUtil.contains(
						  permissionChecker, organization,
						  ActionKeys.MANAGE_USERS)) ||
					 OrganizationMembershipPolicyUtil.isMembershipProtected(
						 permissionChecker, userId,
						 organization.getOrganizationId()) ||
					 OrganizationMembershipPolicyUtil.isMembershipRequired(
						 userId, organization.getOrganizationId()))) {

					organizationIds = ArrayUtil.append(
						organizationIds, organization.getOrganizationId());
				}

				oldOrganizationIds[i] = organization.getOrganizationId();
			}
		}

		// Check that the administrator has the permission to add a new
		// organization and that the organization membership is allowed

		for (long organizationId : organizationIds) {
			if ((oldOrganizationIds != null) &&
				ArrayUtil.contains(oldOrganizationIds, organizationId)) {

				continue;
			}

			Organization organization =
				_organizationPersistence.findByPrimaryKey(organizationId);

			OrganizationPermissionUtil.check(
				permissionChecker, organization, ActionKeys.ASSIGN_MEMBERS);

			if (strictAssignment) {
				OrganizationPermissionUtil.check(
					permissionChecker, organization, ActionKeys.MANAGE_USERS);
			}
		}

		return organizationIds;
	}

	protected long[] checkRoles(long userId, long[] roleIds)
		throws PortalException {

		long[] oldRoleIds = null;

		PermissionChecker permissionChecker = getPermissionChecker();

		if (userId != CompanyConstants.SYSTEM) {

			// Add back any mandatory roles or roles that the administrator does
			// not have the rights to remove and check that he has the
			// permission to add a new role

			List<Role> oldRoles = _roleLocalService.getUserRoles(userId);

			oldRoleIds = new long[oldRoles.size()];

			for (int i = 0; i < oldRoles.size(); i++) {
				Role role = oldRoles.get(i);

				if (!ArrayUtil.contains(roleIds, role.getRoleId()) &&
					(!RolePermissionUtil.contains(
						permissionChecker, role.getRoleId(),
						ActionKeys.ASSIGN_MEMBERS) ||
					 RoleMembershipPolicyUtil.isRoleRequired(
						 userId, role.getRoleId()))) {

					roleIds = ArrayUtil.append(roleIds, role.getRoleId());
				}

				oldRoleIds[i] = role.getRoleId();
			}
		}

		// Check that the administrator has the permission to add a new role and
		// that the role membership is allowed

		for (long roleId : roleIds) {
			if ((oldRoleIds != null) &&
				ArrayUtil.contains(oldRoleIds, roleId)) {

				continue;
			}

			RolePermissionUtil.check(
				permissionChecker, roleId, ActionKeys.ASSIGN_MEMBERS);
		}

		if (userId != CompanyConstants.SYSTEM) {
			return UsersAdminUtil.addRequiredRoles(userId, roleIds);
		}

		return roleIds;
	}

	protected long[] checkUserGroupIds(long userId, long[] userGroupIds)
		throws PortalException {

		long[] oldUserGroupIds = null;

		PermissionChecker permissionChecker = getPermissionChecker();

		if (userId != CompanyConstants.SYSTEM) {

			// Add back any user groups that the administrator does not have the
			// rights to remove or that have a mandatory membership

			List<UserGroup> oldUserGroups =
				_userGroupLocalService.getUserUserGroups(userId);

			oldUserGroupIds = new long[oldUserGroups.size()];

			for (int i = 0; i < oldUserGroups.size(); i++) {
				UserGroup userGroup = oldUserGroups.get(i);

				if (!ArrayUtil.contains(
						userGroupIds, userGroup.getUserGroupId()) &&
					(!UserGroupPermissionUtil.contains(
						permissionChecker, userGroup.getUserGroupId(),
						ActionKeys.ASSIGN_MEMBERS) ||
					 UserGroupMembershipPolicyUtil.isMembershipRequired(
						 userId, userGroup.getUserGroupId()))) {

					userGroupIds = ArrayUtil.append(
						userGroupIds, userGroup.getUserGroupId());
				}

				oldUserGroupIds[i] = userGroup.getUserGroupId();
			}
		}

		// Check that the administrator has the permission to add a new user
		// group and that the user group membership is allowed

		for (long userGroupId : userGroupIds) {
			if ((oldUserGroupIds == null) ||
				!ArrayUtil.contains(oldUserGroupIds, userGroupId)) {

				UserGroupPermissionUtil.check(
					permissionChecker, userGroupId, ActionKeys.ASSIGN_MEMBERS);
			}
		}

		return userGroupIds;
	}

	protected List<UserGroupRole> checkUserGroupRoles(
			long userId, List<UserGroupRole> userGroupRoles)
		throws PortalException {

		List<UserGroupRole> oldUserGroupRoles = null;

		PermissionChecker permissionChecker = getPermissionChecker();

		if (userId != CompanyConstants.SYSTEM) {

			// Add back any user group roles that the administrator does not
			// have the rights to remove or that have a mandatory membership

			oldUserGroupRoles = _userGroupRoleLocalService.getUserGroupRoles(
				userId);

			for (UserGroupRole oldUserGroupRole : oldUserGroupRoles) {
				Role role = oldUserGroupRole.getRole();
				Group group = oldUserGroupRole.getGroup();

				if (userGroupRoles.contains(oldUserGroupRole)) {
					continue;
				}

				if (role.getType() == RoleConstants.TYPE_ORGANIZATION) {
					Organization organization =
						_organizationPersistence.findByPrimaryKey(
							group.getOrganizationId());

					if (!UserGroupRolePermissionUtil.contains(
							permissionChecker, group, role) ||
						OrganizationMembershipPolicyUtil.isRoleProtected(
							getPermissionChecker(), userId,
							organization.getOrganizationId(),
							role.getRoleId()) ||
						OrganizationMembershipPolicyUtil.isRoleRequired(
							userId, organization.getOrganizationId(),
							role.getRoleId())) {

						userGroupRoles.add(oldUserGroupRole);
					}
				}
				else if (role.getType() == RoleConstants.TYPE_SITE) {
					if (!userGroupRoles.contains(oldUserGroupRole) &&
						(!UserGroupRolePermissionUtil.contains(
							permissionChecker, group, role) ||
						 SiteMembershipPolicyUtil.isRoleProtected(
							 getPermissionChecker(), userId, group.getGroupId(),
							 role.getRoleId()) ||
						 SiteMembershipPolicyUtil.isRoleRequired(
							 userId, group.getGroupId(), role.getRoleId()))) {

						userGroupRoles.add(oldUserGroupRole);
					}
				}
			}
		}

		// Check that the administrator has the permission to add a new user
		// group role and that the user group role membership is allowed

		for (UserGroupRole userGroupRole : userGroupRoles) {
			if ((oldUserGroupRoles == null) ||
				!oldUserGroupRoles.contains(userGroupRole)) {

				UserGroupRolePermissionUtil.check(
					permissionChecker, userGroupRole.getGroupId(),
					userGroupRole.getRoleId());
			}
		}

		return userGroupRoles;
	}

	protected void propagateMembership(
			long[] userIds, long[] groupIds, long[] organizationIds,
			long[] roleIds, long[] userGroupIds)
		throws PortalException {

		if (groupIds != null) {
			SiteMembershipPolicyUtil.propagateMembership(
				userIds, groupIds, null);
		}

		if (organizationIds != null) {
			OrganizationMembershipPolicyUtil.propagateMembership(
				userIds, organizationIds, null);
		}

		if (roleIds != null) {
			RoleMembershipPolicyUtil.propagateRoles(userIds, roleIds, null);
		}

		if (userGroupIds != null) {
			UserGroupMembershipPolicyUtil.propagateMembership(
				userIds, userGroupIds, null);
		}
	}

	protected void updateAnnouncementsDeliveries(
			long userId, List<AnnouncementsDelivery> announcementsDeliveries)
		throws PortalException {

		for (AnnouncementsDelivery announcementsDelivery :
				announcementsDeliveries) {

			_announcementsDeliveryService.updateDelivery(
				userId, announcementsDelivery.getType(),
				announcementsDelivery.isEmail(), announcementsDelivery.isSms());
		}
	}

	protected void validateEmailAddress(User user, String emailAddress)
		throws PortalException {

		if (!user.hasCompanyMx() && user.hasCompanyMx(emailAddress)) {
			Company company = _companyPersistence.findByPrimaryKey(
				user.getCompanyId());

			if (company.isStrangers() && !company.isStrangersWithMx()) {
				throw new UserEmailAddressException.MustNotUseCompanyMx(
					emailAddress);
			}
		}
	}

	protected void validateOrganizationUsers(long[] userIds)
		throws PortalException {

		PermissionChecker permissionChecker = getPermissionChecker();

		if (!PropsValues.ORGANIZATIONS_ASSIGNMENT_STRICT ||
			permissionChecker.isCompanyAdmin()) {

			return;
		}

		for (long userId : userIds) {
			boolean allowed = false;

			List<Organization> organizations =
				_organizationLocalService.getUserOrganizations(userId);

			for (Organization organization : organizations) {
				if (OrganizationPermissionUtil.contains(
						permissionChecker, organization,
						ActionKeys.MANAGE_USERS)) {

					allowed = true;

					break;
				}
			}

			if (!allowed) {
				throw new PrincipalException.MustHavePermission(
					permissionChecker, Organization.class.getName(), 0,
					ActionKeys.MANAGE_USERS);
			}
		}
	}

	protected void validateUpdatePermission(
			User user, String screenName, String emailAddress, String firstName,
			String middleName, String lastName, long prefixListTypeId,
			long suffixListTypeId, int birthdayMonth, int birthdayDay,
			int birthdayYear, boolean male, String jobTitle)
		throws PortalException {

		List<String> fields = new ArrayList<>();

		Contact contact = user.getContact();

		Calendar birthday = CalendarFactoryUtil.getCalendar();

		birthday.setTime(contact.getBirthday());

		if ((birthdayMonth != birthday.get(Calendar.MONTH)) ||
			(birthdayDay != birthday.get(Calendar.DAY_OF_MONTH)) ||
			(birthdayYear != birthday.get(Calendar.YEAR))) {

			fields.add("birthday");
		}

		if (!StringUtil.equalsIgnoreCase(
				emailAddress, user.getEmailAddress())) {

			fields.add("emailAddress");
		}

		if (!StringUtil.equalsIgnoreCase(firstName, user.getFirstName())) {
			fields.add("firstName");
		}

		if (male != contact.isMale()) {
			fields.add("gender");
		}

		if (!StringUtil.equalsIgnoreCase(jobTitle, user.getJobTitle())) {
			fields.add("jobTitle");
		}

		if (!StringUtil.equalsIgnoreCase(lastName, user.getLastName())) {
			fields.add("lastName");
		}

		if (!StringUtil.equalsIgnoreCase(middleName, user.getMiddleName())) {
			fields.add("middleName");
		}

		if (prefixListTypeId != contact.getPrefixListTypeId()) {
			fields.add("prefix");
		}

		if (!StringUtil.equalsIgnoreCase(screenName, user.getScreenName())) {
			fields.add("screenName");
		}

		if (suffixListTypeId != contact.getSuffixListTypeId()) {
			fields.add("suffix");
		}

		UserFieldException userFieldException = new UserFieldException();

		for (String field : fields) {
			if (!UsersAdminUtil.hasUpdateFieldPermission(
					getPermissionChecker(), getUser(), user, field)) {

				userFieldException.addField(field);
			}
		}

		if (userFieldException.hasFields()) {
			throw userFieldException;
		}
	}

	protected void validateUserIds(long[] userIds) throws PortalException {
		for (long userId : userIds) {
			getUserById(userId);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UserServiceImpl.class);

	@BeanReference(type = AnnouncementsDeliveryService.class)
	private AnnouncementsDeliveryService _announcementsDeliveryService;

	@BeanReference(type = CompanyPersistence.class)
	private CompanyPersistence _companyPersistence;

	@BeanReference(type = GroupLocalService.class)
	private GroupLocalService _groupLocalService;

	@BeanReference(type = GroupPersistence.class)
	private GroupPersistence _groupPersistence;

	@BeanReference(type = OrganizationLocalService.class)
	private OrganizationLocalService _organizationLocalService;

	@BeanReference(type = OrganizationPersistence.class)
	private OrganizationPersistence _organizationPersistence;

	@BeanReference(type = RoleLocalService.class)
	private RoleLocalService _roleLocalService;

	@BeanReference(type = RolePersistence.class)
	private RolePersistence _rolePersistence;

	@BeanReference(type = UserGroupLocalService.class)
	private UserGroupLocalService _userGroupLocalService;

	@BeanReference(type = UserGroupPersistence.class)
	private UserGroupPersistence _userGroupPersistence;

	@BeanReference(type = UserGroupRoleLocalService.class)
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@BeanReference(type = UserGroupRolePersistence.class)
	private UserGroupRolePersistence _userGroupRolePersistence;

}