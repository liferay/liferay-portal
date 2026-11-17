/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

/**
 * Provides the remote service utility for User. This utility wraps
 * <code>com.liferay.portal.service.impl.UserServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see UserService
 * @generated
 */
public class UserServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.service.impl.UserServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the users to the group.
	 *
	 * @param groupId the primary key of the group
	 * @param userIds the primary keys of the users
	 * @param serviceContext the service context to be applied (optionally
	 <code>null</code>)
	 */
	public static void addGroupUsers(
			long groupId, long[] userIds, ServiceContext serviceContext)
		throws PortalException {

		getService().addGroupUsers(groupId, userIds, serviceContext);
	}

	/**
	 * Adds the users to the organization.
	 *
	 * @param organizationId the primary key of the organization
	 * @param userIds the primary keys of the users
	 */
	public static void addOrganizationUsers(long organizationId, long[] userIds)
		throws PortalException {

		getService().addOrganizationUsers(organizationId, userIds);
	}

	public static User addOrUpdateUser(
			String externalReferenceCode, long creatorUserId, long companyId,
			boolean autoPassword, String password1, String password2,
			boolean autoScreenName, String screenName, String emailAddress,
			java.util.Locale locale, String firstName, String middleName,
			String lastName, long prefixListTypeId, long suffixListTypeId,
			boolean male, int birthdayMonth, int birthdayDay, int birthdayYear,
			String jobTitle,
			List<com.liferay.portal.kernel.model.Address> addresses,
			List<com.liferay.portal.kernel.model.EmailAddress> emailAddresses,
			List<com.liferay.portal.kernel.model.Phone> phones,
			List<com.liferay.portal.kernel.model.Website> websites,
			boolean sendEmail, ServiceContext serviceContext)
		throws PortalException {

		return getService().addOrUpdateUser(
			externalReferenceCode, creatorUserId, companyId, autoPassword,
			password1, password2, autoScreenName, screenName, emailAddress,
			locale, firstName, middleName, lastName, prefixListTypeId,
			suffixListTypeId, male, birthdayMonth, birthdayDay, birthdayYear,
			jobTitle, addresses, emailAddresses, phones, websites, sendEmail,
			serviceContext);
	}

	/**
	 * Assigns the password policy to the users, removing any other currently
	 * assigned password policies.
	 *
	 * @param passwordPolicyId the primary key of the password policy
	 * @param userIds the primary keys of the users
	 */
	public static void addPasswordPolicyUsers(
			long passwordPolicyId, long[] userIds)
		throws PortalException {

		getService().addPasswordPolicyUsers(passwordPolicyId, userIds);
	}

	/**
	 * Adds the users to the role.
	 *
	 * @param roleId the primary key of the role
	 * @param userIds the primary keys of the users
	 */
	public static void addRoleUsers(long roleId, long[] userIds)
		throws PortalException {

		getService().addRoleUsers(roleId, userIds);
	}

	/**
	 * Adds the users to the team.
	 *
	 * @param teamId the primary key of the team
	 * @param userIds the primary keys of the users
	 */
	public static void addTeamUsers(long teamId, long[] userIds)
		throws PortalException {

		getService().addTeamUsers(teamId, userIds);
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
	 * @param companyId the primary key of the user's company
	 * @param autoPassword whether a password should be automatically generated
	 for the user
	 * @param password1 the user's password
	 * @param password2 the user's password confirmation
	 * @param autoScreenName whether a screen name should be automatically
	 generated for the user
	 * @param screenName the user's screen name
	 * @param emailAddress the user's email address
	 * @param locale the user's locale
	 * @param firstName the user's first name
	 * @param middleName the user's middle name
	 * @param lastName the user's last name
	 * @param prefixListTypeId the user's name prefix ID
	 * @param suffixListTypeId the user's name suffix ID
	 * @param male whether the user is male
	 * @param birthdayMonth the user's birthday month (0-based, meaning 0 for
	 January)
	 * @param birthdayDay the user's birthday day
	 * @param birthdayYear the user's birthday year
	 * @param jobTitle the user's job title
	 * @param groupIds the primary keys of the user's groups
	 * @param organizationIds the primary keys of the user's organizations
	 * @param roleIds the primary keys of the roles this user possesses
	 * @param userGroupIds the primary keys of the user's user groups
	 * @param sendEmail whether to send the user an email notification about
	 their new account
	 * @param serviceContext the service context to be applied (optionally
	 <code>null</code>). Can set the UUID (with the <code>uuid</code>
	 attribute), asset category IDs, asset tag names, and expando
	 bridge attributes for the user.
	 * @return the new user
	 */
	public static User addUser(
			long companyId, boolean autoPassword, String password1,
			String password2, boolean autoScreenName, String screenName,
			String emailAddress, java.util.Locale locale, String firstName,
			String middleName, String lastName, long prefixListTypeId,
			long suffixListTypeId, boolean male, int birthdayMonth,
			int birthdayDay, int birthdayYear, String jobTitle, long[] groupIds,
			long[] organizationIds, long[] roleIds, long[] userGroupIds,
			boolean sendEmail, ServiceContext serviceContext)
		throws PortalException {

		return getService().addUser(
			companyId, autoPassword, password1, password2, autoScreenName,
			screenName, emailAddress, locale, firstName, middleName, lastName,
			prefixListTypeId, suffixListTypeId, male, birthdayMonth,
			birthdayDay, birthdayYear, jobTitle, groupIds, organizationIds,
			roleIds, userGroupIds, sendEmail, serviceContext);
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
	 * @param companyId the primary key of the user's company
	 * @param autoPassword whether a password should be automatically generated
	 for the user
	 * @param password1 the user's password
	 * @param password2 the user's password confirmation
	 * @param autoScreenName whether a screen name should be automatically
	 generated for the user
	 * @param screenName the user's screen name
	 * @param emailAddress the user's email address
	 * @param locale the user's locale
	 * @param firstName the user's first name
	 * @param middleName the user's middle name
	 * @param lastName the user's last name
	 * @param prefixListTypeId the user's name prefix ID
	 * @param suffixListTypeId the user's name suffix ID
	 * @param male whether the user is male
	 * @param birthdayMonth the user's birthday month (0-based, meaning 0 for
	 January)
	 * @param birthdayDay the user's birthday day
	 * @param birthdayYear the user's birthday year
	 * @param jobTitle the user's job title
	 * @param groupIds the primary keys of the user's groups
	 * @param organizationIds the primary keys of the user's organizations
	 * @param roleIds the primary keys of the roles this user possesses
	 * @param userGroupIds the primary keys of the user's user groups
	 * @param addresses the user's addresses
	 * @param emailAddresses the user's email addresses
	 * @param phones the user's phone numbers
	 * @param websites the user's websites
	 * @param announcementsDelivers the announcements deliveries
	 * @param sendEmail whether to send the user an email notification about
	 their new account
	 * @param serviceContext the service context to be applied (optionally
	 <code>null</code>). Can set the UUID (with the <code>uuid</code>
	 attribute), asset category IDs, asset tag names, and expando
	 bridge attributes for the user.
	 * @return the new user
	 */
	public static User addUser(
			long companyId, boolean autoPassword, String password1,
			String password2, boolean autoScreenName, String screenName,
			String emailAddress, java.util.Locale locale, String firstName,
			String middleName, String lastName, long prefixListTypeId,
			long suffixListTypeId, boolean male, int birthdayMonth,
			int birthdayDay, int birthdayYear, String jobTitle, long[] groupIds,
			long[] organizationIds, long[] roleIds, long[] userGroupIds,
			List<com.liferay.portal.kernel.model.Address> addresses,
			List<com.liferay.portal.kernel.model.EmailAddress> emailAddresses,
			List<com.liferay.portal.kernel.model.Phone> phones,
			List<com.liferay.portal.kernel.model.Website> websites,
			List<com.liferay.announcements.kernel.model.AnnouncementsDelivery>
				announcementsDelivers,
			boolean sendEmail, ServiceContext serviceContext)
		throws PortalException {

		return getService().addUser(
			companyId, autoPassword, password1, password2, autoScreenName,
			screenName, emailAddress, locale, firstName, middleName, lastName,
			prefixListTypeId, suffixListTypeId, male, birthdayMonth,
			birthdayDay, birthdayYear, jobTitle, groupIds, organizationIds,
			roleIds, userGroupIds, addresses, emailAddresses, phones, websites,
			announcementsDelivers, sendEmail, serviceContext);
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
	 * @param companyId the primary key of the user's company
	 * @param autoPassword whether a password should be automatically
	 generated for the user
	 * @param password1 the user's password
	 * @param password2 the user's password confirmation
	 * @param autoScreenName whether a screen name should be automatically
	 generated for the user
	 * @param screenName the user's screen name
	 * @param emailAddress the user's email address
	 * @param facebookId the user's facebook ID
	 * @param openId the user's OpenID
	 * @param locale the user's locale
	 * @param firstName the user's first name
	 * @param middleName the user's middle name
	 * @param lastName the user's last name
	 * @param prefixListTypeId the user's name prefix ID
	 * @param suffixListTypeId the user's name suffix ID
	 * @param male whether the user is male
	 * @param birthdayMonth the user's birthday month (0-based, meaning 0
	 for January)
	 * @param birthdayDay the user's birthday day
	 * @param birthdayYear the user's birthday year
	 * @param jobTitle the user's job title
	 * @param groupIds the primary keys of the user's groups
	 * @param organizationIds the primary keys of the user's organizations
	 * @param roleIds the primary keys of the roles this user possesses
	 * @param userGroupIds the primary keys of the user's user groups
	 * @param sendEmail whether to send the user an email notification
	 about their new account
	 * @param serviceContext the service context to be applied (optionally
	 <code>null</code>). Can set the UUID (with the
	 <code>uuid</code> attribute), asset category IDs, asset tag
	 names, and expando bridge attributes for the user.
	 * @return the new user
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link #addUser(long,
	 boolean, String, String, boolean, String, String, Locale,
	 String, String, String, long, long, boolean, int, int, int,
	 String, long[], long[], long[], long[], boolean,
	 ServiceContext)}
	 */
	@Deprecated
	public static User addUser(
			long companyId, boolean autoPassword, String password1,
			String password2, boolean autoScreenName, String screenName,
			String emailAddress, long facebookId, String openId,
			java.util.Locale locale, String firstName, String middleName,
			String lastName, long prefixListTypeId, long suffixListTypeId,
			boolean male, int birthdayMonth, int birthdayDay, int birthdayYear,
			String jobTitle, long[] groupIds, long[] organizationIds,
			long[] roleIds, long[] userGroupIds, boolean sendEmail,
			ServiceContext serviceContext)
		throws PortalException {

		return getService().addUser(
			companyId, autoPassword, password1, password2, autoScreenName,
			screenName, emailAddress, facebookId, openId, locale, firstName,
			middleName, lastName, prefixListTypeId, suffixListTypeId, male,
			birthdayMonth, birthdayDay, birthdayYear, jobTitle, groupIds,
			organizationIds, roleIds, userGroupIds, sendEmail, serviceContext);
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
	 * @param companyId the primary key of the user's company
	 * @param autoPassword whether a password should be automatically
	 generated for the user
	 * @param password1 the user's password
	 * @param password2 the user's password confirmation
	 * @param autoScreenName whether a screen name should be automatically
	 generated for the user
	 * @param screenName the user's screen name
	 * @param emailAddress the user's email address
	 * @param facebookId the user's facebook ID
	 * @param openId the user's OpenID
	 * @param locale the user's locale
	 * @param firstName the user's first name
	 * @param middleName the user's middle name
	 * @param lastName the user's last name
	 * @param prefixListTypeId the user's name prefix ID
	 * @param suffixListTypeId the user's name suffix ID
	 * @param male whether the user is male
	 * @param birthdayMonth the user's birthday month (0-based, meaning 0
	 for January)
	 * @param birthdayDay the user's birthday day
	 * @param birthdayYear the user's birthday year
	 * @param jobTitle the user's job title
	 * @param groupIds the primary keys of the user's groups
	 * @param organizationIds the primary keys of the user's organizations
	 * @param roleIds the primary keys of the roles this user possesses
	 * @param userGroupIds the primary keys of the user's user groups
	 * @param addresses the user's addresses
	 * @param emailAddresses the user's email addresses
	 * @param phones the user's phone numbers
	 * @param websites the user's websites
	 * @param announcementsDelivers the announcements deliveries
	 * @param sendEmail whether to send the user an email notification
	 about their new account
	 * @param serviceContext the service context to be applied (optionally
	 <code>null</code>). Can set the UUID (with the
	 <code>uuid</code> attribute), asset category IDs, asset tag
	 names, and expando bridge attributes for the user.
	 * @return the new user
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link #addUser(long,
	 boolean, String, String, boolean, String, String, Locale,
	 String, String, String, long, long, boolean, int, int, int,
	 String, long[], long[], long[], long[], List, List, List,
	 List, List, boolean, ServiceContext)}
	 */
	@Deprecated
	public static User addUser(
			long companyId, boolean autoPassword, String password1,
			String password2, boolean autoScreenName, String screenName,
			String emailAddress, long facebookId, String openId,
			java.util.Locale locale, String firstName, String middleName,
			String lastName, long prefixListTypeId, long suffixListTypeId,
			boolean male, int birthdayMonth, int birthdayDay, int birthdayYear,
			String jobTitle, long[] groupIds, long[] organizationIds,
			long[] roleIds, long[] userGroupIds,
			List<com.liferay.portal.kernel.model.Address> addresses,
			List<com.liferay.portal.kernel.model.EmailAddress> emailAddresses,
			List<com.liferay.portal.kernel.model.Phone> phones,
			List<com.liferay.portal.kernel.model.Website> websites,
			List<com.liferay.announcements.kernel.model.AnnouncementsDelivery>
				announcementsDelivers,
			boolean sendEmail, ServiceContext serviceContext)
		throws PortalException {

		return getService().addUser(
			companyId, autoPassword, password1, password2, autoScreenName,
			screenName, emailAddress, facebookId, openId, locale, firstName,
			middleName, lastName, prefixListTypeId, suffixListTypeId, male,
			birthdayMonth, birthdayDay, birthdayYear, jobTitle, groupIds,
			organizationIds, roleIds, userGroupIds, addresses, emailAddresses,
			phones, websites, announcementsDelivers, sendEmail, serviceContext);
	}

	/**
	 * Adds the users to the user group.
	 *
	 * @param userGroupId the primary key of the user group
	 * @param userIds the primary keys of the users
	 */
	public static void addUserGroupUsers(long userGroupId, long[] userIds)
		throws PortalException {

		getService().addUserGroupUsers(userGroupId, userIds);
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
	 * @param companyId the primary key of the user's company
	 * @param autoPassword whether a password should be automatically generated
	 for the user
	 * @param password1 the user's password
	 * @param password2 the user's password confirmation
	 * @param autoScreenName whether a screen name should be automatically
	 generated for the user
	 * @param screenName the user's screen name
	 * @param emailAddress the user's email address
	 * @param locale the user's locale
	 * @param firstName the user's first name
	 * @param middleName the user's middle name
	 * @param lastName the user's last name
	 * @param prefixListTypeId the user's name prefix ID
	 * @param suffixListTypeId the user's name suffix ID
	 * @param male whether the user is male
	 * @param birthdayMonth the user's birthday month (0-based, meaning 0 for
	 January)
	 * @param birthdayDay the user's birthday day
	 * @param birthdayYear the user's birthday year
	 * @param jobTitle the user's job title
	 * @param groupIds the primary keys of the user's groups
	 * @param organizationIds the primary keys of the user's organizations
	 * @param roleIds the primary keys of the roles this user possesses
	 * @param userGroupIds the primary keys of the user's user groups
	 * @param sendEmail whether to send the user an email notification about
	 their new account
	 * @param serviceContext the service context to be applied (optionally
	 <code>null</code>). Can set the UUID (with the <code>uuid</code>
	 attribute), asset category IDs, asset tag names, and expando
	 bridge attributes for the user.
	 * @return the new user
	 */
	public static User addUserWithWorkflow(
			long companyId, boolean autoPassword, String password1,
			String password2, boolean autoScreenName, String screenName,
			String emailAddress, java.util.Locale locale, String firstName,
			String middleName, String lastName, long prefixListTypeId,
			long suffixListTypeId, boolean male, int birthdayMonth,
			int birthdayDay, int birthdayYear, String jobTitle, long[] groupIds,
			long[] organizationIds, long[] roleIds, long[] userGroupIds,
			boolean sendEmail, ServiceContext serviceContext)
		throws PortalException {

		return getService().addUserWithWorkflow(
			companyId, autoPassword, password1, password2, autoScreenName,
			screenName, emailAddress, locale, firstName, middleName, lastName,
			prefixListTypeId, suffixListTypeId, male, birthdayMonth,
			birthdayDay, birthdayYear, jobTitle, groupIds, organizationIds,
			roleIds, userGroupIds, sendEmail, serviceContext);
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
	 * @param companyId the primary key of the user's company
	 * @param autoPassword whether a password should be automatically generated
	 for the user
	 * @param password1 the user's password
	 * @param password2 the user's password confirmation
	 * @param autoScreenName whether a screen name should be automatically
	 generated for the user
	 * @param screenName the user's screen name
	 * @param emailAddress the user's email address
	 * @param locale the user's locale
	 * @param firstName the user's first name
	 * @param middleName the user's middle name
	 * @param lastName the user's last name
	 * @param prefixListTypeId the user's name prefix ID
	 * @param suffixListTypeId the user's name suffix ID
	 * @param male whether the user is male
	 * @param birthdayMonth the user's birthday month (0-based, meaning 0 for
	 January)
	 * @param birthdayDay the user's birthday day
	 * @param birthdayYear the user's birthday year
	 * @param jobTitle the user's job title
	 * @param groupIds the primary keys of the user's groups
	 * @param organizationIds the primary keys of the user's organizations
	 * @param roleIds the primary keys of the roles this user possesses
	 * @param userGroupIds the primary keys of the user's user groups
	 * @param addresses the user's addresses
	 * @param emailAddresses the user's email addresses
	 * @param phones the user's phone numbers
	 * @param websites the user's websites
	 * @param announcementsDelivers the announcements deliveries
	 * @param sendEmail whether to send the user an email notification about
	 their new account
	 * @param serviceContext the service context to be applied (optionally
	 <code>null</code>). Can set the UUID (with the <code>uuid</code>
	 attribute), asset category IDs, asset tag names, and expando
	 bridge attributes for the user.
	 * @return the new user
	 */
	public static User addUserWithWorkflow(
			long companyId, boolean autoPassword, String password1,
			String password2, boolean autoScreenName, String screenName,
			String emailAddress, java.util.Locale locale, String firstName,
			String middleName, String lastName, long prefixListTypeId,
			long suffixListTypeId, boolean male, int birthdayMonth,
			int birthdayDay, int birthdayYear, String jobTitle, long[] groupIds,
			long[] organizationIds, long[] roleIds, long[] userGroupIds,
			List<com.liferay.portal.kernel.model.Address> addresses,
			List<com.liferay.portal.kernel.model.EmailAddress> emailAddresses,
			List<com.liferay.portal.kernel.model.Phone> phones,
			List<com.liferay.portal.kernel.model.Website> websites,
			List<com.liferay.announcements.kernel.model.AnnouncementsDelivery>
				announcementsDelivers,
			boolean sendEmail, ServiceContext serviceContext)
		throws PortalException {

		return getService().addUserWithWorkflow(
			companyId, autoPassword, password1, password2, autoScreenName,
			screenName, emailAddress, locale, firstName, middleName, lastName,
			prefixListTypeId, suffixListTypeId, male, birthdayMonth,
			birthdayDay, birthdayYear, jobTitle, groupIds, organizationIds,
			roleIds, userGroupIds, addresses, emailAddresses, phones, websites,
			announcementsDelivers, sendEmail, serviceContext);
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
	 * @param companyId the primary key of the user's company
	 * @param autoPassword whether a password should be automatically
	 generated for the user
	 * @param password1 the user's password
	 * @param password2 the user's password confirmation
	 * @param autoScreenName whether a screen name should be automatically
	 generated for the user
	 * @param screenName the user's screen name
	 * @param emailAddress the user's email address
	 * @param facebookId the user's facebook ID
	 * @param openId the user's OpenID
	 * @param locale the user's locale
	 * @param firstName the user's first name
	 * @param middleName the user's middle name
	 * @param lastName the user's last name
	 * @param prefixListTypeId the user's name prefix ID
	 * @param suffixListTypeId the user's name suffix ID
	 * @param male whether the user is male
	 * @param birthdayMonth the user's birthday month (0-based, meaning 0
	 for January)
	 * @param birthdayDay the user's birthday day
	 * @param birthdayYear the user's birthday year
	 * @param jobTitle the user's job title
	 * @param groupIds the primary keys of the user's groups
	 * @param organizationIds the primary keys of the user's organizations
	 * @param roleIds the primary keys of the roles this user possesses
	 * @param userGroupIds the primary keys of the user's user groups
	 * @param sendEmail whether to send the user an email notification
	 about their new account
	 * @param serviceContext the service context to be applied (optionally
	 <code>null</code>). Can set the UUID (with the
	 <code>uuid</code> attribute), asset category IDs, asset tag
	 names, and expando bridge attributes for the user.
	 * @return the new user
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 #addUserWithWorkflow(long, boolean, String, String, boolean,
	 String, String, Locale, String, String, String, long, long,
	 boolean, int, int, int, String, long[], long[], long[],
	 long[], boolean, ServiceContext)}
	 */
	@Deprecated
	public static User addUserWithWorkflow(
			long companyId, boolean autoPassword, String password1,
			String password2, boolean autoScreenName, String screenName,
			String emailAddress, long facebookId, String openId,
			java.util.Locale locale, String firstName, String middleName,
			String lastName, long prefixListTypeId, long suffixListTypeId,
			boolean male, int birthdayMonth, int birthdayDay, int birthdayYear,
			String jobTitle, long[] groupIds, long[] organizationIds,
			long[] roleIds, long[] userGroupIds, boolean sendEmail,
			ServiceContext serviceContext)
		throws PortalException {

		return getService().addUserWithWorkflow(
			companyId, autoPassword, password1, password2, autoScreenName,
			screenName, emailAddress, facebookId, openId, locale, firstName,
			middleName, lastName, prefixListTypeId, suffixListTypeId, male,
			birthdayMonth, birthdayDay, birthdayYear, jobTitle, groupIds,
			organizationIds, roleIds, userGroupIds, sendEmail, serviceContext);
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
	 * @param companyId the primary key of the user's company
	 * @param autoPassword whether a password should be automatically
	 generated for the user
	 * @param password1 the user's password
	 * @param password2 the user's password confirmation
	 * @param autoScreenName whether a screen name should be automatically
	 generated for the user
	 * @param screenName the user's screen name
	 * @param emailAddress the user's email address
	 * @param facebookId the user's facebook ID
	 * @param openId the user's OpenID
	 * @param locale the user's locale
	 * @param firstName the user's first name
	 * @param middleName the user's middle name
	 * @param lastName the user's last name
	 * @param prefixListTypeId the user's name prefix ID
	 * @param suffixListTypeId the user's name suffix ID
	 * @param male whether the user is male
	 * @param birthdayMonth the user's birthday month (0-based, meaning 0
	 for January)
	 * @param birthdayDay the user's birthday day
	 * @param birthdayYear the user's birthday year
	 * @param jobTitle the user's job title
	 * @param groupIds the primary keys of the user's groups
	 * @param organizationIds the primary keys of the user's organizations
	 * @param roleIds the primary keys of the roles this user possesses
	 * @param userGroupIds the primary keys of the user's user groups
	 * @param addresses the user's addresses
	 * @param emailAddresses the user's email addresses
	 * @param phones the user's phone numbers
	 * @param websites the user's websites
	 * @param announcementsDelivers the announcements deliveries
	 * @param sendEmail whether to send the user an email notification
	 about their new account
	 * @param serviceContext the service context to be applied (optionally
	 <code>null</code>). Can set the UUID (with the
	 <code>uuid</code> attribute), asset category IDs, asset tag
	 names, and expando bridge attributes for the user.
	 * @return the new user
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 #addUserWithWorkflow(long, boolean, String, String, boolean,
	 String, String, Locale, String, String, String, long, long,
	 boolean, int, int, int, String, long[], long[], long[],
	 long[], List, List, List, List, List, boolean, ServiceContext
	 )}
	 */
	@Deprecated
	public static User addUserWithWorkflow(
			long companyId, boolean autoPassword, String password1,
			String password2, boolean autoScreenName, String screenName,
			String emailAddress, long facebookId, String openId,
			java.util.Locale locale, String firstName, String middleName,
			String lastName, long prefixListTypeId, long suffixListTypeId,
			boolean male, int birthdayMonth, int birthdayDay, int birthdayYear,
			String jobTitle, long[] groupIds, long[] organizationIds,
			long[] roleIds, long[] userGroupIds,
			List<com.liferay.portal.kernel.model.Address> addresses,
			List<com.liferay.portal.kernel.model.EmailAddress> emailAddresses,
			List<com.liferay.portal.kernel.model.Phone> phones,
			List<com.liferay.portal.kernel.model.Website> websites,
			List<com.liferay.announcements.kernel.model.AnnouncementsDelivery>
				announcementsDelivers,
			boolean sendEmail, ServiceContext serviceContext)
		throws PortalException {

		return getService().addUserWithWorkflow(
			companyId, autoPassword, password1, password2, autoScreenName,
			screenName, emailAddress, facebookId, openId, locale, firstName,
			middleName, lastName, prefixListTypeId, suffixListTypeId, male,
			birthdayMonth, birthdayDay, birthdayYear, jobTitle, groupIds,
			organizationIds, roleIds, userGroupIds, addresses, emailAddresses,
			phones, websites, announcementsDelivers, sendEmail, serviceContext);
	}

	/**
	 * Deletes the user's portrait image.
	 *
	 * @param userId the primary key of the user
	 */
	public static void deletePortrait(long userId) throws PortalException {
		getService().deletePortrait(userId);
	}

	/**
	 * Removes the user from the role.
	 *
	 * @param roleId the primary key of the role
	 * @param userId the primary key of the user
	 */
	public static void deleteRoleUser(long roleId, long userId)
		throws PortalException {

		getService().deleteRoleUser(roleId, userId);
	}

	/**
	 * Deletes the user.
	 *
	 * @param userId the primary key of the user
	 */
	public static void deleteUser(long userId) throws PortalException {
		getService().deleteUser(userId);
	}

	public static User fetchUserByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().fetchUserByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	public static List<User> getCompanyUsers(long companyId, int start, int end)
		throws PortalException {

		return getService().getCompanyUsers(companyId, start, end);
	}

	public static int getCompanyUsersCount(long companyId)
		throws PortalException {

		return getService().getCompanyUsersCount(companyId);
	}

	public static User getCurrentUser() throws PortalException {
		return getService().getCurrentUser();
	}

	/**
	 * Returns the primary keys of all the users belonging to the group.
	 *
	 * @param groupId the primary key of the group
	 * @return the primary keys of the users belonging to the group
	 */
	public static long[] getGroupUserIds(long groupId) throws PortalException {
		return getService().getGroupUserIds(groupId);
	}

	/**
	 * Returns all the users belonging to the group.
	 *
	 * @param groupId the primary key of the group
	 * @return the users belonging to the group
	 */
	public static List<User> getGroupUsers(long groupId)
		throws PortalException {

		return getService().getGroupUsers(groupId);
	}

	/**
	 * Returns the users belonging to a group.
	 *
	 * @param groupId the primary key of the group
	 * @param status the workflow status
	 * @param start the lower bound of the range of users
	 * @param end the upper bound of the range of users (not inclusive)
	 * @param orderByComparator the comparator to order the users by
	 (optionally <code>null</code>)
	 * @return the matching users
	 */
	public static List<User> getGroupUsers(
			long groupId, int status, int start, int end,
			OrderByComparator<User> orderByComparator)
		throws PortalException {

		return getService().getGroupUsers(
			groupId, status, start, end, orderByComparator);
	}

	/**
	 * Returns the users belonging to a group.
	 *
	 * @param groupId the primary key of the group
	 * @param status the workflow status
	 * @param orderByComparator the comparator to order the users by
	 (optionally <code>null</code>)
	 * @return the matching users
	 */
	public static List<User> getGroupUsers(
			long groupId, int status, OrderByComparator<User> orderByComparator)
		throws PortalException {

		return getService().getGroupUsers(groupId, status, orderByComparator);
	}

	/**
	 * Returns the number of users with the status belonging to the group.
	 *
	 * @param groupId the primary key of the group
	 * @param status the workflow status
	 * @return the number of users with the status belonging to the group
	 */
	public static int getGroupUsersCount(long groupId, int status)
		throws PortalException {

		return getService().getGroupUsersCount(groupId, status);
	}

	public static List<User> getGtCompanyUsers(
			long gtUserId, long companyId, int size)
		throws PortalException {

		return getService().getGtCompanyUsers(gtUserId, companyId, size);
	}

	public static List<User> getGtOrganizationUsers(
			long gtUserId, long organizationId, int size)
		throws PortalException {

		return getService().getGtOrganizationUsers(
			gtUserId, organizationId, size);
	}

	public static List<User> getGtUserGroupUsers(
			long gtUserId, long userGroupId, int size)
		throws PortalException {

		return getService().getGtUserGroupUsers(gtUserId, userGroupId, size);
	}

	public static int getOrganizationsAndUserGroupsUsersCount(
			long[] organizationIds, long[] userGroupIds)
		throws com.liferay.portal.kernel.security.auth.PrincipalException {

		return getService().getOrganizationsAndUserGroupsUsersCount(
			organizationIds, userGroupIds);
	}

	/**
	 * Returns the primary keys of all the users belonging to the organization.
	 *
	 * @param organizationId the primary key of the organization
	 * @return the primary keys of the users belonging to the organization
	 */
	public static long[] getOrganizationUserIds(long organizationId)
		throws PortalException {

		return getService().getOrganizationUserIds(organizationId);
	}

	/**
	 * Returns all the users belonging to the organization.
	 *
	 * @param organizationId the primary key of the organization
	 * @return users belonging to the organization
	 */
	public static List<User> getOrganizationUsers(long organizationId)
		throws PortalException {

		return getService().getOrganizationUsers(organizationId);
	}

	/**
	 * Returns the users belonging to the organization with the status.
	 *
	 * @param organizationId the primary key of the organization
	 * @param status the workflow status
	 * @param start the lower bound of the range of users
	 * @param end the upper bound of the range of users (not inclusive)
	 * @param orderByComparator the comparator to order the users by
	 (optionally <code>null</code>)
	 * @return the matching users
	 */
	public static List<User> getOrganizationUsers(
			long organizationId, int status, int start, int end,
			OrderByComparator<User> orderByComparator)
		throws PortalException {

		return getService().getOrganizationUsers(
			organizationId, status, start, end, orderByComparator);
	}

	/**
	 * Returns the users belonging to the organization with the status.
	 *
	 * @param organizationId the primary key of the organization
	 * @param status the workflow status
	 * @param orderByComparator the comparator to order the users by
	 (optionally <code>null</code>)
	 * @return the matching users
	 */
	public static List<User> getOrganizationUsers(
			long organizationId, int status,
			OrderByComparator<User> orderByComparator)
		throws PortalException {

		return getService().getOrganizationUsers(
			organizationId, status, orderByComparator);
	}

	/**
	 * Returns the number of users with the status belonging to the
	 * organization.
	 *
	 * @param organizationId the primary key of the organization
	 * @param status the workflow status
	 * @return the number of users with the status belonging to the organization
	 */
	public static int getOrganizationUsersCount(long organizationId, int status)
		throws PortalException {

		return getService().getOrganizationUsersCount(organizationId, status);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	/**
	 * Returns the primary keys of all the users belonging to the role.
	 *
	 * @param roleId the primary key of the role
	 * @return the primary keys of the users belonging to the role
	 */
	public static long[] getRoleUserIds(long roleId) throws PortalException {
		return getService().getRoleUserIds(roleId);
	}

	/**
	 * Returns the user with the email address.
	 *
	 * @param companyId the primary key of the user's company
	 * @param emailAddress the user's email address
	 * @return the user with the email address
	 */
	public static User getUserByEmailAddress(
			long companyId, String emailAddress)
		throws PortalException {

		return getService().getUserByEmailAddress(companyId, emailAddress);
	}

	public static User getUserByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().getUserByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the user with the primary key.
	 *
	 * @param userId the primary key of the user
	 * @return the user with the primary key
	 */
	public static User getUserById(long userId) throws PortalException {
		return getService().getUserById(userId);
	}

	/**
	 * Returns the user with the screen name.
	 *
	 * @param companyId the primary key of the user's company
	 * @param screenName the user's screen name
	 * @return the user with the screen name
	 */
	public static User getUserByScreenName(long companyId, String screenName)
		throws PortalException {

		return getService().getUserByScreenName(companyId, screenName);
	}

	public static List<User> getUserGroupUsers(long userGroupId)
		throws PortalException {

		return getService().getUserGroupUsers(userGroupId);
	}

	public static List<User> getUserGroupUsers(
			long userGroupId, int start, int end)
		throws PortalException {

		return getService().getUserGroupUsers(userGroupId, start, end);
	}

	/**
	 * Returns the primary key of the user with the email address.
	 *
	 * @param companyId the primary key of the user's company
	 * @param emailAddress the user's email address
	 * @return the primary key of the user with the email address
	 */
	public static long getUserIdByEmailAddress(
			long companyId, String emailAddress)
		throws PortalException {

		return getService().getUserIdByEmailAddress(companyId, emailAddress);
	}

	/**
	 * Returns the primary key of the user with the screen name.
	 *
	 * @param companyId the primary key of the user's company
	 * @param screenName the user's screen name
	 * @return the primary key of the user with the screen name
	 */
	public static long getUserIdByScreenName(long companyId, String screenName)
		throws PortalException {

		return getService().getUserIdByScreenName(companyId, screenName);
	}

	/**
	 * Returns <code>true</code> if the user is a member of the group.
	 *
	 * @param groupId the primary key of the group
	 * @param userId the primary key of the user
	 * @return <code>true</code> if the user is a member of the group;
	 <code>false</code> otherwise
	 */
	public static boolean hasGroupUser(long groupId, long userId)
		throws PortalException {

		return getService().hasGroupUser(groupId, userId);
	}

	/**
	 * Returns <code>true</code> if the user is a member of the role.
	 *
	 * @param roleId the primary key of the role
	 * @param userId the primary key of the user
	 * @return <code>true</code> if the user is a member of the role;
	 <code>false</code> otherwise
	 */
	public static boolean hasRoleUser(long roleId, long userId)
		throws PortalException {

		return getService().hasRoleUser(roleId, userId);
	}

	/**
	 * Returns <code>true</code> if the user has the role with the name,
	 * optionally through inheritance.
	 *
	 * @param companyId the primary key of the role's company
	 * @param name the name of the role (must be a regular role, not an
	 organization, site or provider role)
	 * @param userId the primary key of the user
	 * @param inherited whether to include roles inherited from organizations,
	 sites, etc.
	 * @return <code>true</code> if the user has the role; <code>false</code>
	 otherwise
	 */
	public static boolean hasRoleUser(
			long companyId, String name, long userId, boolean inherited)
		throws PortalException {

		return getService().hasRoleUser(companyId, name, userId, inherited);
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
	 * @param companyId the primary key of the user's company
	 * @param emailAddress the user's email address
	 * @return <code>true</code> if the notification email includes a new
	 password; <code>false</code> if the notification email only
	 contains a reset link
	 */
	public static boolean sendPasswordByEmailAddress(
			long companyId, String emailAddress)
		throws PortalException {

		return getService().sendPasswordByEmailAddress(companyId, emailAddress);
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
	 * @param companyId the primary key of the user's company
	 * @param screenName the user's screen name
	 * @return <code>true</code> if the notification email includes a new
	 password; <code>false</code> if the notification email only
	 contains a reset link
	 */
	public static boolean sendPasswordByScreenName(
			long companyId, String screenName)
		throws PortalException {

		return getService().sendPasswordByScreenName(companyId, screenName);
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
	 * @param userId the user's primary key
	 * @return <code>true</code> if the notification email includes a new
	 password; <code>false</code> if the notification email only
	 contains a reset link
	 */
	public static boolean sendPasswordByUserId(long userId)
		throws PortalException {

		return getService().sendPasswordByUserId(userId);
	}

	/**
	 * Sets the users in the role, removing and adding users to the role as
	 * necessary.
	 *
	 * @param roleId the primary key of the role
	 * @param userIds the primary keys of the users
	 */
	public static void setRoleUsers(long roleId, long[] userIds)
		throws PortalException {

		getService().setRoleUsers(roleId, userIds);
	}

	/**
	 * Sets the users in the user group, removing and adding users to the user
	 * group as necessary.
	 *
	 * @param userGroupId the primary key of the user group
	 * @param userIds the primary keys of the users
	 */
	public static void setUserGroupUsers(long userGroupId, long[] userIds)
		throws PortalException {

		getService().setUserGroupUsers(userGroupId, userIds);
	}

	/**
	 * Removes the users from the teams of a group.
	 *
	 * @param groupId the primary key of the group
	 * @param userIds the primary keys of the users
	 */
	public static void unsetGroupTeamsUsers(long groupId, long[] userIds)
		throws PortalException {

		getService().unsetGroupTeamsUsers(groupId, userIds);
	}

	/**
	 * Removes the users from the group.
	 *
	 * @param groupId the primary key of the group
	 * @param userIds the primary keys of the users
	 * @param serviceContext the service context to be applied (optionally
	 <code>null</code>)
	 */
	public static void unsetGroupUsers(
			long groupId, long[] userIds, ServiceContext serviceContext)
		throws PortalException {

		getService().unsetGroupUsers(groupId, userIds, serviceContext);
	}

	/**
	 * Removes the users from the organization.
	 *
	 * @param organizationId the primary key of the organization
	 * @param userIds the primary keys of the users
	 */
	public static void unsetOrganizationUsers(
			long organizationId, long[] userIds)
		throws PortalException {

		getService().unsetOrganizationUsers(organizationId, userIds);
	}

	/**
	 * Removes the users from the password policy.
	 *
	 * @param passwordPolicyId the primary key of the password policy
	 * @param userIds the primary keys of the users
	 */
	public static void unsetPasswordPolicyUsers(
			long passwordPolicyId, long[] userIds)
		throws PortalException {

		getService().unsetPasswordPolicyUsers(passwordPolicyId, userIds);
	}

	/**
	 * Removes the users from the role.
	 *
	 * @param roleId the primary key of the role
	 * @param userIds the primary keys of the users
	 */
	public static void unsetRoleUsers(long roleId, long[] userIds)
		throws PortalException {

		getService().unsetRoleUsers(roleId, userIds);
	}

	/**
	 * Removes the users from the team.
	 *
	 * @param teamId the primary key of the team
	 * @param userIds the primary keys of the users
	 */
	public static void unsetTeamUsers(long teamId, long[] userIds)
		throws PortalException {

		getService().unsetTeamUsers(teamId, userIds);
	}

	/**
	 * Removes the users from the user group.
	 *
	 * @param userGroupId the primary key of the user group
	 * @param userIds the primary keys of the users
	 */
	public static void unsetUserGroupUsers(long userGroupId, long[] userIds)
		throws PortalException {

		getService().unsetUserGroupUsers(userGroupId, userIds);
	}

	/**
	 * Updates the user's response to the terms of use agreement.
	 *
	 * @param userId the primary key of the user
	 * @param agreedToTermsOfUse whether the user has agree to the terms of use
	 * @return the user
	 */
	public static User updateAgreedToTermsOfUse(
			long userId, boolean agreedToTermsOfUse)
		throws PortalException {

		return getService().updateAgreedToTermsOfUse(
			userId, agreedToTermsOfUse);
	}

	/**
	 * Updates the user's email address.
	 *
	 * @param userId the primary key of the user
	 * @param password the user's password
	 * @param emailAddress1 the user's new email address
	 * @param emailAddress2 the user's new email address confirmation
	 * @param serviceContext the service context to be applied. Must set the
	 portal URL, main path, primary key of the layout, remote address,
	 remote host, and agent for the user.
	 * @return the user
	 */
	public static User updateEmailAddress(
			long userId, String password, String emailAddress1,
			String emailAddress2, ServiceContext serviceContext)
		throws PortalException {

		return getService().updateEmailAddress(
			userId, password, emailAddress1, emailAddress2, serviceContext);
	}

	public static User updateExternalReferenceCode(
			long userId, String externalReferenceCode)
		throws PortalException {

		return getService().updateExternalReferenceCode(
			userId, externalReferenceCode);
	}

	public static User updateExternalReferenceCode(
			User user, String externalReferenceCode)
		throws PortalException {

		return getService().updateExternalReferenceCode(
			user, externalReferenceCode);
	}

	/**
	 * Updates a user account that was automatically created when a guest user
	 * participated in an action (e.g. posting a comment) and only provided his
	 * name and email address.
	 *
	 * @param companyId the primary key of the user's company
	 * @param autoPassword whether a password should be automatically generated
	 for the user
	 * @param password1 the user's password
	 * @param password2 the user's password confirmation
	 * @param autoScreenName whether a screen name should be automatically
	 generated for the user
	 * @param screenName the user's screen name
	 * @param emailAddress the user's email address
	 * @param locale the user's locale
	 * @param firstName the user's first name
	 * @param middleName the user's middle name
	 * @param lastName the user's last name
	 * @param prefixListTypeId the user's name prefix ID
	 * @param suffixListTypeId the user's name suffix ID
	 * @param male whether the user is male
	 * @param birthdayMonth the user's birthday month (0-based, meaning 0 for
	 January)
	 * @param birthdayDay the user's birthday day
	 * @param birthdayYear the user's birthday year
	 * @param jobTitle the user's job title
	 * @param updateUserInformation whether to update the user's information
	 * @param sendEmail whether to send the user an email notification about
	 their new account
	 * @param serviceContext the service context to be applied (optionally
	 <code>null</code>). Can set the expando bridge attributes for the
	 user.
	 * @return the user
	 */
	public static User updateIncompleteUser(
			long companyId, boolean autoPassword, String password1,
			String password2, boolean autoScreenName, String screenName,
			String emailAddress, java.util.Locale locale, String firstName,
			String middleName, String lastName, long prefixListTypeId,
			long suffixListTypeId, boolean male, int birthdayMonth,
			int birthdayDay, int birthdayYear, String jobTitle,
			boolean updateUserInformation, boolean sendEmail,
			ServiceContext serviceContext)
		throws PortalException {

		return getService().updateIncompleteUser(
			companyId, autoPassword, password1, password2, autoScreenName,
			screenName, emailAddress, locale, firstName, middleName, lastName,
			prefixListTypeId, suffixListTypeId, male, birthdayMonth,
			birthdayDay, birthdayYear, jobTitle, updateUserInformation,
			sendEmail, serviceContext);
	}

	/**
	 * Updates a user account that was automatically created when a guest user
	 * participated in an action (e.g. posting a comment) and only provided his
	 * name and email address.
	 *
	 * @param companyId the primary key of the user's company
	 * @param autoPassword whether a password should be automatically
	 generated for the user
	 * @param password1 the user's password
	 * @param password2 the user's password confirmation
	 * @param autoScreenName whether a screen name should be automatically
	 generated for the user
	 * @param screenName the user's screen name
	 * @param emailAddress the user's email address
	 * @param facebookId the user's facebook ID
	 * @param openId the user's OpenID
	 * @param locale the user's locale
	 * @param firstName the user's first name
	 * @param middleName the user's middle name
	 * @param lastName the user's last name
	 * @param prefixListTypeId the user's name prefix ID
	 * @param suffixListTypeId the user's name suffix ID
	 * @param male whether the user is male
	 * @param birthdayMonth the user's birthday month (0-based, meaning 0
	 for January)
	 * @param birthdayDay the user's birthday day
	 * @param birthdayYear the user's birthday year
	 * @param jobTitle the user's job title
	 * @param updateUserInformation whether to update the user's
	 information
	 * @param sendEmail whether to send the user an email notification
	 about their new account
	 * @param serviceContext the service context to be applied (optionally
	 <code>null</code>). Can set the expando bridge attributes for
	 the user.
	 * @return the user
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 #updateIncompleteUser(long, long, boolean, String, String,
	 boolean, String, String, Locale, String, String, String,
	 long, long, boolean, int, int, int, String, boolean, boolean,
	 ServiceContext)}
	 */
	@Deprecated
	public static User updateIncompleteUser(
			long companyId, boolean autoPassword, String password1,
			String password2, boolean autoScreenName, String screenName,
			String emailAddress, long facebookId, String openId,
			java.util.Locale locale, String firstName, String middleName,
			String lastName, long prefixListTypeId, long suffixListTypeId,
			boolean male, int birthdayMonth, int birthdayDay, int birthdayYear,
			String jobTitle, boolean updateUserInformation, boolean sendEmail,
			ServiceContext serviceContext)
		throws PortalException {

		return getService().updateIncompleteUser(
			companyId, autoPassword, password1, password2, autoScreenName,
			screenName, emailAddress, facebookId, openId, locale, firstName,
			middleName, lastName, prefixListTypeId, suffixListTypeId, male,
			birthdayMonth, birthdayDay, birthdayYear, jobTitle,
			updateUserInformation, sendEmail, serviceContext);
	}

	public static User updateLanguageId(long userId, String languageId)
		throws PortalException {

		return getService().updateLanguageId(userId, languageId);
	}

	/**
	 * Updates whether the user is locked out from logging in.
	 *
	 * @param userId the primary key of the user
	 * @param lockout whether the user is locked out
	 * @return the user
	 */
	public static User updateLockoutById(long userId, boolean lockout)
		throws PortalException {

		return getService().updateLockoutById(userId, lockout);
	}

	/**
	 * Sets the organizations that the user is in, removing and adding
	 * organizations as necessary.
	 *
	 * @param userId the primary key of the user
	 * @param organizationIds the primary keys of the organizations
	 * @param serviceContext the service context to be applied. Must set whether
	 user indexing is enabled.
	 */
	public static void updateOrganizations(
			long userId, long[] organizationIds, ServiceContext serviceContext)
		throws PortalException {

		getService().updateOrganizations(
			userId, organizationIds, serviceContext);
	}

	/**
	 * Updates the user's password without tracking or validation of the change.
	 *
	 * @param userId the primary key of the user
	 * @param password1 the user's new password
	 * @param password2 the user's new password confirmation
	 * @param passwordReset whether the user should be asked to reset their
	 password the next time they log in
	 * @return the user
	 */
	public static User updatePassword(
			long userId, String password1, String password2,
			boolean passwordReset)
		throws PortalException {

		return getService().updatePassword(
			userId, password1, password2, passwordReset);
	}

	/**
	 * Updates the user's portrait image.
	 *
	 * @param userId the primary key of the user
	 * @param bytes the new portrait image data
	 * @return the user
	 */
	public static User updatePortrait(long userId, byte[] bytes)
		throws PortalException {

		return getService().updatePortrait(userId, bytes);
	}

	/**
	 * Updates the user's password reset question and answer.
	 *
	 * @param userId the primary key of the user
	 * @param question the user's new password reset question
	 * @param answer the user's new password reset answer
	 * @return the user
	 */
	public static User updateReminderQuery(
			long userId, String question, String answer)
		throws PortalException {

		return getService().updateReminderQuery(userId, question, answer);
	}

	/**
	 * Updates the user's screen name.
	 *
	 * @param userId the primary key of the user
	 * @param screenName the user's new screen name
	 * @return the user
	 */
	public static User updateScreenName(long userId, String screenName)
		throws PortalException {

		return getService().updateScreenName(userId, screenName);
	}

	/**
	 * Updates the user's workflow status.
	 *
	 * @param userId the primary key of the user
	 * @param status the user's new workflow status
	 * @param serviceContext the service context to be applied. You can specify
	 an unencrypted custom password (used by an LDAP listener) for the
	 user via attribute <code>passwordUnencrypted</code>.
	 * @return the user
	 */
	public static User updateStatus(
			long userId, int status, ServiceContext serviceContext)
		throws PortalException {

		return getService().updateStatus(userId, status, serviceContext);
	}

	public static User updateStatus(
			User user, int status, ServiceContext serviceContext)
		throws PortalException {

		return getService().updateStatus(user, status, serviceContext);
	}

	/**
	 * Updates the user with additional parameters.
	 *
	 * @param userId the primary key of the user
	 * @param oldPassword the user's old password
	 * @param newPassword1 the user's new password (optionally
	 <code>null</code>)
	 * @param newPassword2 the user's new password confirmation (optionally
	 <code>null</code>)
	 * @param passwordReset whether the user should be asked to reset their
	 password the next time they login
	 * @param reminderQueryQuestion the user's new password reset question
	 * @param reminderQueryAnswer the user's new password reset answer
	 * @param screenName the user's new screen name
	 * @param emailAddress the user's new email address
	 * @param hasPortrait if the user has a custom portrait image
	 * @param portraitBytes the new portrait image data
	 * @param languageId the user's new language ID
	 * @param timeZoneId the user's new time zone ID
	 * @param greeting the user's new greeting
	 * @param comments the user's new comments
	 * @param firstName the user's new first name
	 * @param middleName the user's new middle name
	 * @param lastName the user's new last name
	 * @param prefixListTypeId the user's new name prefix ID
	 * @param suffixListTypeId the user's new name suffix ID
	 * @param male whether user is male
	 * @param birthdayMonth the user's new birthday month (0-based, meaning 0
	 for January)
	 * @param birthdayDay the user's new birthday day
	 * @param birthdayYear the user's birthday year
	 * @param smsSn the user's new SMS screen name
	 * @param facebookSn the user's new Facebook screen name
	 * @param jabberSn the user's new Jabber screen name
	 * @param skypeSn the user's new Skype screen name
	 * @param twitterSn the user's new Twitter screen name
	 * @param jobTitle the user's new job title
	 * @param groupIds the primary keys of the user's groups
	 * @param organizationIds the primary keys of the user's organizations
	 * @param roleIds the primary keys of the user's roles
	 * @param userGroupRoles the user user's group roles
	 * @param userGroupIds the primary keys of the user's user groups
	 * @param addresses the user's addresses
	 * @param emailAddresses the user's email addresses
	 * @param phones the user's phone numbers
	 * @param websites the user's websites
	 * @param announcementsDelivers the announcements deliveries
	 * @param serviceContext the service context to be applied (optionally
	 <code>null</code>). Can set the UUID (with the <code>uuid</code>
	 attribute), asset category IDs, asset tag names, and expando
	 bridge attributes for the user.
	 * @return the user
	 */
	public static User updateUser(
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
			List<com.liferay.portal.kernel.model.UserGroupRole> userGroupRoles,
			long[] userGroupIds,
			List<com.liferay.portal.kernel.model.Address> addresses,
			List<com.liferay.portal.kernel.model.EmailAddress> emailAddresses,
			List<com.liferay.portal.kernel.model.Phone> phones,
			List<com.liferay.portal.kernel.model.Website> websites,
			List<com.liferay.announcements.kernel.model.AnnouncementsDelivery>
				announcementsDelivers,
			ServiceContext serviceContext)
		throws PortalException {

		return getService().updateUser(
			userId, oldPassword, newPassword1, newPassword2, passwordReset,
			reminderQueryQuestion, reminderQueryAnswer, screenName,
			emailAddress, hasPortrait, portraitBytes, languageId, timeZoneId,
			greeting, comments, firstName, middleName, lastName,
			prefixListTypeId, suffixListTypeId, male, birthdayMonth,
			birthdayDay, birthdayYear, smsSn, facebookSn, jabberSn, skypeSn,
			twitterSn, jobTitle, groupIds, organizationIds, roleIds,
			userGroupRoles, userGroupIds, addresses, emailAddresses, phones,
			websites, announcementsDelivers, serviceContext);
	}

	/**
	 * Updates the user.
	 *
	 * @param userId the primary key of the user
	 * @param oldPassword the user's old password
	 * @param newPassword1 the user's new password (optionally
	 <code>null</code>)
	 * @param newPassword2 the user's new password confirmation (optionally
	 <code>null</code>)
	 * @param passwordReset whether the user should be asked to reset their
	 password the next time they login
	 * @param reminderQueryQuestion the user's new password reset question
	 * @param reminderQueryAnswer the user's new password reset answer
	 * @param screenName the user's new screen name
	 * @param emailAddress the user's new email address
	 * @param languageId the user's new language ID
	 * @param timeZoneId the user's new time zone ID
	 * @param greeting the user's new greeting
	 * @param comments the user's new comments
	 * @param firstName the user's new first name
	 * @param middleName the user's new middle name
	 * @param lastName the user's new last name
	 * @param prefixListTypeId the user's new name prefix ID
	 * @param suffixListTypeId the user's new name suffix ID
	 * @param male whether user is male
	 * @param birthdayMonth the user's new birthday month (0-based, meaning 0
	 for January)
	 * @param birthdayDay the user's new birthday day
	 * @param birthdayYear the user's birthday year
	 * @param smsSn the user's new SMS screen name
	 * @param facebookSn the user's new Facebook screen name
	 * @param jabberSn the user's new Jabber screen name
	 * @param skypeSn the user's new Skype screen name
	 * @param twitterSn the user's new Twitter screen name
	 * @param jobTitle the user's new job title
	 * @param groupIds the primary keys of the user's groups
	 * @param organizationIds the primary keys of the user's organizations
	 * @param roleIds the primary keys of the user's roles
	 * @param userGroupRoles the user user's group roles
	 * @param userGroupIds the primary keys of the user's user groups
	 * @param serviceContext the service context to be applied (optionally
	 <code>null</code>). Can set the UUID (with the <code>uuid</code>
	 attribute), asset category IDs, asset tag names, and expando
	 bridge attributes for the user.
	 * @return the user
	 */
	public static User updateUser(
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
			List<com.liferay.portal.kernel.model.UserGroupRole> userGroupRoles,
			long[] userGroupIds, ServiceContext serviceContext)
		throws PortalException {

		return getService().updateUser(
			userId, oldPassword, newPassword1, newPassword2, passwordReset,
			reminderQueryQuestion, reminderQueryAnswer, screenName,
			emailAddress, languageId, timeZoneId, greeting, comments, firstName,
			middleName, lastName, prefixListTypeId, suffixListTypeId, male,
			birthdayMonth, birthdayDay, birthdayYear, smsSn, facebookSn,
			jabberSn, skypeSn, twitterSn, jobTitle, groupIds, organizationIds,
			roleIds, userGroupRoles, userGroupIds, serviceContext);
	}

	public static UserService getService() {
		return _service;
	}

	public static void setService(UserService service) {
		_service = service;
	}

	private static volatile UserService _service;

}
// LIFERAY-SERVICE-BUILDER-HASH:-494646698