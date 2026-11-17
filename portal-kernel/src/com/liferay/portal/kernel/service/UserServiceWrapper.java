/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.portal.kernel.model.User;

/**
 * Provides a wrapper for {@link UserService}.
 *
 * @author Brian Wing Shun Chan
 * @see UserService
 * @generated
 */
public class UserServiceWrapper
	implements ServiceWrapper<UserService>, UserService {

	public UserServiceWrapper() {
		this(null);
	}

	public UserServiceWrapper(UserService userService) {
		_userService = userService;
	}

	/**
	 * Adds the users to the group.
	 *
	 * @param groupId the primary key of the group
	 * @param userIds the primary keys of the users
	 * @param serviceContext the service context to be applied (optionally
	 <code>null</code>)
	 */
	@Override
	public void addGroupUsers(
			long groupId, long[] userIds, ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		_userService.addGroupUsers(groupId, userIds, serviceContext);
	}

	/**
	 * Adds the users to the organization.
	 *
	 * @param organizationId the primary key of the organization
	 * @param userIds the primary keys of the users
	 */
	@Override
	public void addOrganizationUsers(long organizationId, long[] userIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		_userService.addOrganizationUsers(organizationId, userIds);
	}

	@Override
	public User addOrUpdateUser(
			String externalReferenceCode, long creatorUserId, long companyId,
			boolean autoPassword, String password1, String password2,
			boolean autoScreenName, String screenName, String emailAddress,
			java.util.Locale locale, String firstName, String middleName,
			String lastName, long prefixListTypeId, long suffixListTypeId,
			boolean male, int birthdayMonth, int birthdayDay, int birthdayYear,
			String jobTitle,
			java.util.List<com.liferay.portal.kernel.model.Address> addresses,
			java.util.List<com.liferay.portal.kernel.model.EmailAddress>
				emailAddresses,
			java.util.List<com.liferay.portal.kernel.model.Phone> phones,
			java.util.List<com.liferay.portal.kernel.model.Website> websites,
			boolean sendEmail, ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userService.addOrUpdateUser(
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
	@Override
	public void addPasswordPolicyUsers(long passwordPolicyId, long[] userIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		_userService.addPasswordPolicyUsers(passwordPolicyId, userIds);
	}

	/**
	 * Adds the users to the role.
	 *
	 * @param roleId the primary key of the role
	 * @param userIds the primary keys of the users
	 */
	@Override
	public void addRoleUsers(long roleId, long[] userIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		_userService.addRoleUsers(roleId, userIds);
	}

	/**
	 * Adds the users to the team.
	 *
	 * @param teamId the primary key of the team
	 * @param userIds the primary keys of the users
	 */
	@Override
	public void addTeamUsers(long teamId, long[] userIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		_userService.addTeamUsers(teamId, userIds);
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
	@Override
	public User addUser(
			long companyId, boolean autoPassword, String password1,
			String password2, boolean autoScreenName, String screenName,
			String emailAddress, java.util.Locale locale, String firstName,
			String middleName, String lastName, long prefixListTypeId,
			long suffixListTypeId, boolean male, int birthdayMonth,
			int birthdayDay, int birthdayYear, String jobTitle, long[] groupIds,
			long[] organizationIds, long[] roleIds, long[] userGroupIds,
			boolean sendEmail, ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userService.addUser(
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
	@Override
	public User addUser(
			long companyId, boolean autoPassword, String password1,
			String password2, boolean autoScreenName, String screenName,
			String emailAddress, java.util.Locale locale, String firstName,
			String middleName, String lastName, long prefixListTypeId,
			long suffixListTypeId, boolean male, int birthdayMonth,
			int birthdayDay, int birthdayYear, String jobTitle, long[] groupIds,
			long[] organizationIds, long[] roleIds, long[] userGroupIds,
			java.util.List<com.liferay.portal.kernel.model.Address> addresses,
			java.util.List<com.liferay.portal.kernel.model.EmailAddress>
				emailAddresses,
			java.util.List<com.liferay.portal.kernel.model.Phone> phones,
			java.util.List<com.liferay.portal.kernel.model.Website> websites,
			java.util.List
				<com.liferay.announcements.kernel.model.AnnouncementsDelivery>
					announcementsDelivers,
			boolean sendEmail, ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userService.addUser(
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
	@Override
	public User addUser(
			long companyId, boolean autoPassword, String password1,
			String password2, boolean autoScreenName, String screenName,
			String emailAddress, long facebookId, String openId,
			java.util.Locale locale, String firstName, String middleName,
			String lastName, long prefixListTypeId, long suffixListTypeId,
			boolean male, int birthdayMonth, int birthdayDay, int birthdayYear,
			String jobTitle, long[] groupIds, long[] organizationIds,
			long[] roleIds, long[] userGroupIds, boolean sendEmail,
			ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userService.addUser(
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
	@Override
	public User addUser(
			long companyId, boolean autoPassword, String password1,
			String password2, boolean autoScreenName, String screenName,
			String emailAddress, long facebookId, String openId,
			java.util.Locale locale, String firstName, String middleName,
			String lastName, long prefixListTypeId, long suffixListTypeId,
			boolean male, int birthdayMonth, int birthdayDay, int birthdayYear,
			String jobTitle, long[] groupIds, long[] organizationIds,
			long[] roleIds, long[] userGroupIds,
			java.util.List<com.liferay.portal.kernel.model.Address> addresses,
			java.util.List<com.liferay.portal.kernel.model.EmailAddress>
				emailAddresses,
			java.util.List<com.liferay.portal.kernel.model.Phone> phones,
			java.util.List<com.liferay.portal.kernel.model.Website> websites,
			java.util.List
				<com.liferay.announcements.kernel.model.AnnouncementsDelivery>
					announcementsDelivers,
			boolean sendEmail, ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userService.addUser(
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
	@Override
	public void addUserGroupUsers(long userGroupId, long[] userIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		_userService.addUserGroupUsers(userGroupId, userIds);
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
	@Override
	public User addUserWithWorkflow(
			long companyId, boolean autoPassword, String password1,
			String password2, boolean autoScreenName, String screenName,
			String emailAddress, java.util.Locale locale, String firstName,
			String middleName, String lastName, long prefixListTypeId,
			long suffixListTypeId, boolean male, int birthdayMonth,
			int birthdayDay, int birthdayYear, String jobTitle, long[] groupIds,
			long[] organizationIds, long[] roleIds, long[] userGroupIds,
			boolean sendEmail, ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userService.addUserWithWorkflow(
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
	@Override
	public User addUserWithWorkflow(
			long companyId, boolean autoPassword, String password1,
			String password2, boolean autoScreenName, String screenName,
			String emailAddress, java.util.Locale locale, String firstName,
			String middleName, String lastName, long prefixListTypeId,
			long suffixListTypeId, boolean male, int birthdayMonth,
			int birthdayDay, int birthdayYear, String jobTitle, long[] groupIds,
			long[] organizationIds, long[] roleIds, long[] userGroupIds,
			java.util.List<com.liferay.portal.kernel.model.Address> addresses,
			java.util.List<com.liferay.portal.kernel.model.EmailAddress>
				emailAddresses,
			java.util.List<com.liferay.portal.kernel.model.Phone> phones,
			java.util.List<com.liferay.portal.kernel.model.Website> websites,
			java.util.List
				<com.liferay.announcements.kernel.model.AnnouncementsDelivery>
					announcementsDelivers,
			boolean sendEmail, ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userService.addUserWithWorkflow(
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
	@Override
	public User addUserWithWorkflow(
			long companyId, boolean autoPassword, String password1,
			String password2, boolean autoScreenName, String screenName,
			String emailAddress, long facebookId, String openId,
			java.util.Locale locale, String firstName, String middleName,
			String lastName, long prefixListTypeId, long suffixListTypeId,
			boolean male, int birthdayMonth, int birthdayDay, int birthdayYear,
			String jobTitle, long[] groupIds, long[] organizationIds,
			long[] roleIds, long[] userGroupIds, boolean sendEmail,
			ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userService.addUserWithWorkflow(
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
	@Override
	public User addUserWithWorkflow(
			long companyId, boolean autoPassword, String password1,
			String password2, boolean autoScreenName, String screenName,
			String emailAddress, long facebookId, String openId,
			java.util.Locale locale, String firstName, String middleName,
			String lastName, long prefixListTypeId, long suffixListTypeId,
			boolean male, int birthdayMonth, int birthdayDay, int birthdayYear,
			String jobTitle, long[] groupIds, long[] organizationIds,
			long[] roleIds, long[] userGroupIds,
			java.util.List<com.liferay.portal.kernel.model.Address> addresses,
			java.util.List<com.liferay.portal.kernel.model.EmailAddress>
				emailAddresses,
			java.util.List<com.liferay.portal.kernel.model.Phone> phones,
			java.util.List<com.liferay.portal.kernel.model.Website> websites,
			java.util.List
				<com.liferay.announcements.kernel.model.AnnouncementsDelivery>
					announcementsDelivers,
			boolean sendEmail, ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userService.addUserWithWorkflow(
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
	@Override
	public void deletePortrait(long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_userService.deletePortrait(userId);
	}

	/**
	 * Removes the user from the role.
	 *
	 * @param roleId the primary key of the role
	 * @param userId the primary key of the user
	 */
	@Override
	public void deleteRoleUser(long roleId, long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_userService.deleteRoleUser(roleId, userId);
	}

	/**
	 * Deletes the user.
	 *
	 * @param userId the primary key of the user
	 */
	@Override
	public void deleteUser(long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_userService.deleteUser(userId);
	}

	@Override
	public User fetchUserByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userService.fetchUserByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	@Override
	public java.util.List<User> getCompanyUsers(
			long companyId, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userService.getCompanyUsers(companyId, start, end);
	}

	@Override
	public int getCompanyUsersCount(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userService.getCompanyUsersCount(companyId);
	}

	@Override
	public User getCurrentUser()
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userService.getCurrentUser();
	}

	/**
	 * Returns the primary keys of all the users belonging to the group.
	 *
	 * @param groupId the primary key of the group
	 * @return the primary keys of the users belonging to the group
	 */
	@Override
	public long[] getGroupUserIds(long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userService.getGroupUserIds(groupId);
	}

	/**
	 * Returns all the users belonging to the group.
	 *
	 * @param groupId the primary key of the group
	 * @return the users belonging to the group
	 */
	@Override
	public java.util.List<User> getGroupUsers(long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userService.getGroupUsers(groupId);
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
	@Override
	public java.util.List<User> getGroupUsers(
			long groupId, int status, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<User>
				orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userService.getGroupUsers(
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
	@Override
	public java.util.List<User> getGroupUsers(
			long groupId, int status,
			com.liferay.portal.kernel.util.OrderByComparator<User>
				orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userService.getGroupUsers(groupId, status, orderByComparator);
	}

	/**
	 * Returns the number of users with the status belonging to the group.
	 *
	 * @param groupId the primary key of the group
	 * @param status the workflow status
	 * @return the number of users with the status belonging to the group
	 */
	@Override
	public int getGroupUsersCount(long groupId, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userService.getGroupUsersCount(groupId, status);
	}

	@Override
	public java.util.List<User> getGtCompanyUsers(
			long gtUserId, long companyId, int size)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userService.getGtCompanyUsers(gtUserId, companyId, size);
	}

	@Override
	public java.util.List<User> getGtOrganizationUsers(
			long gtUserId, long organizationId, int size)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userService.getGtOrganizationUsers(
			gtUserId, organizationId, size);
	}

	@Override
	public java.util.List<User> getGtUserGroupUsers(
			long gtUserId, long userGroupId, int size)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userService.getGtUserGroupUsers(gtUserId, userGroupId, size);
	}

	@Override
	public int getOrganizationsAndUserGroupsUsersCount(
			long[] organizationIds, long[] userGroupIds)
		throws com.liferay.portal.kernel.security.auth.PrincipalException {

		return _userService.getOrganizationsAndUserGroupsUsersCount(
			organizationIds, userGroupIds);
	}

	/**
	 * Returns the primary keys of all the users belonging to the organization.
	 *
	 * @param organizationId the primary key of the organization
	 * @return the primary keys of the users belonging to the organization
	 */
	@Override
	public long[] getOrganizationUserIds(long organizationId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userService.getOrganizationUserIds(organizationId);
	}

	/**
	 * Returns all the users belonging to the organization.
	 *
	 * @param organizationId the primary key of the organization
	 * @return users belonging to the organization
	 */
	@Override
	public java.util.List<User> getOrganizationUsers(long organizationId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userService.getOrganizationUsers(organizationId);
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
	@Override
	public java.util.List<User> getOrganizationUsers(
			long organizationId, int status, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<User>
				orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userService.getOrganizationUsers(
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
	@Override
	public java.util.List<User> getOrganizationUsers(
			long organizationId, int status,
			com.liferay.portal.kernel.util.OrderByComparator<User>
				orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userService.getOrganizationUsers(
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
	@Override
	public int getOrganizationUsersCount(long organizationId, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userService.getOrganizationUsersCount(organizationId, status);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _userService.getOSGiServiceIdentifier();
	}

	/**
	 * Returns the primary keys of all the users belonging to the role.
	 *
	 * @param roleId the primary key of the role
	 * @return the primary keys of the users belonging to the role
	 */
	@Override
	public long[] getRoleUserIds(long roleId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userService.getRoleUserIds(roleId);
	}

	/**
	 * Returns the user with the email address.
	 *
	 * @param companyId the primary key of the user's company
	 * @param emailAddress the user's email address
	 * @return the user with the email address
	 */
	@Override
	public User getUserByEmailAddress(long companyId, String emailAddress)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userService.getUserByEmailAddress(companyId, emailAddress);
	}

	@Override
	public User getUserByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userService.getUserByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the user with the primary key.
	 *
	 * @param userId the primary key of the user
	 * @return the user with the primary key
	 */
	@Override
	public User getUserById(long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userService.getUserById(userId);
	}

	/**
	 * Returns the user with the screen name.
	 *
	 * @param companyId the primary key of the user's company
	 * @param screenName the user's screen name
	 * @return the user with the screen name
	 */
	@Override
	public User getUserByScreenName(long companyId, String screenName)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userService.getUserByScreenName(companyId, screenName);
	}

	@Override
	public java.util.List<User> getUserGroupUsers(long userGroupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userService.getUserGroupUsers(userGroupId);
	}

	@Override
	public java.util.List<User> getUserGroupUsers(
			long userGroupId, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userService.getUserGroupUsers(userGroupId, start, end);
	}

	/**
	 * Returns the primary key of the user with the email address.
	 *
	 * @param companyId the primary key of the user's company
	 * @param emailAddress the user's email address
	 * @return the primary key of the user with the email address
	 */
	@Override
	public long getUserIdByEmailAddress(long companyId, String emailAddress)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userService.getUserIdByEmailAddress(companyId, emailAddress);
	}

	/**
	 * Returns the primary key of the user with the screen name.
	 *
	 * @param companyId the primary key of the user's company
	 * @param screenName the user's screen name
	 * @return the primary key of the user with the screen name
	 */
	@Override
	public long getUserIdByScreenName(long companyId, String screenName)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userService.getUserIdByScreenName(companyId, screenName);
	}

	/**
	 * Returns <code>true</code> if the user is a member of the group.
	 *
	 * @param groupId the primary key of the group
	 * @param userId the primary key of the user
	 * @return <code>true</code> if the user is a member of the group;
	 <code>false</code> otherwise
	 */
	@Override
	public boolean hasGroupUser(long groupId, long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userService.hasGroupUser(groupId, userId);
	}

	/**
	 * Returns <code>true</code> if the user is a member of the role.
	 *
	 * @param roleId the primary key of the role
	 * @param userId the primary key of the user
	 * @return <code>true</code> if the user is a member of the role;
	 <code>false</code> otherwise
	 */
	@Override
	public boolean hasRoleUser(long roleId, long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userService.hasRoleUser(roleId, userId);
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
	@Override
	public boolean hasRoleUser(
			long companyId, String name, long userId, boolean inherited)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userService.hasRoleUser(companyId, name, userId, inherited);
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
	@Override
	public boolean sendPasswordByEmailAddress(
			long companyId, String emailAddress)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userService.sendPasswordByEmailAddress(companyId, emailAddress);
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
	@Override
	public boolean sendPasswordByScreenName(long companyId, String screenName)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userService.sendPasswordByScreenName(companyId, screenName);
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
	@Override
	public boolean sendPasswordByUserId(long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userService.sendPasswordByUserId(userId);
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
		throws com.liferay.portal.kernel.exception.PortalException {

		_userService.setRoleUsers(roleId, userIds);
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
		throws com.liferay.portal.kernel.exception.PortalException {

		_userService.setUserGroupUsers(userGroupId, userIds);
	}

	/**
	 * Removes the users from the teams of a group.
	 *
	 * @param groupId the primary key of the group
	 * @param userIds the primary keys of the users
	 */
	@Override
	public void unsetGroupTeamsUsers(long groupId, long[] userIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		_userService.unsetGroupTeamsUsers(groupId, userIds);
	}

	/**
	 * Removes the users from the group.
	 *
	 * @param groupId the primary key of the group
	 * @param userIds the primary keys of the users
	 * @param serviceContext the service context to be applied (optionally
	 <code>null</code>)
	 */
	@Override
	public void unsetGroupUsers(
			long groupId, long[] userIds, ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		_userService.unsetGroupUsers(groupId, userIds, serviceContext);
	}

	/**
	 * Removes the users from the organization.
	 *
	 * @param organizationId the primary key of the organization
	 * @param userIds the primary keys of the users
	 */
	@Override
	public void unsetOrganizationUsers(long organizationId, long[] userIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		_userService.unsetOrganizationUsers(organizationId, userIds);
	}

	/**
	 * Removes the users from the password policy.
	 *
	 * @param passwordPolicyId the primary key of the password policy
	 * @param userIds the primary keys of the users
	 */
	@Override
	public void unsetPasswordPolicyUsers(long passwordPolicyId, long[] userIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		_userService.unsetPasswordPolicyUsers(passwordPolicyId, userIds);
	}

	/**
	 * Removes the users from the role.
	 *
	 * @param roleId the primary key of the role
	 * @param userIds the primary keys of the users
	 */
	@Override
	public void unsetRoleUsers(long roleId, long[] userIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		_userService.unsetRoleUsers(roleId, userIds);
	}

	/**
	 * Removes the users from the team.
	 *
	 * @param teamId the primary key of the team
	 * @param userIds the primary keys of the users
	 */
	@Override
	public void unsetTeamUsers(long teamId, long[] userIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		_userService.unsetTeamUsers(teamId, userIds);
	}

	/**
	 * Removes the users from the user group.
	 *
	 * @param userGroupId the primary key of the user group
	 * @param userIds the primary keys of the users
	 */
	@Override
	public void unsetUserGroupUsers(long userGroupId, long[] userIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		_userService.unsetUserGroupUsers(userGroupId, userIds);
	}

	/**
	 * Updates the user's response to the terms of use agreement.
	 *
	 * @param userId the primary key of the user
	 * @param agreedToTermsOfUse whether the user has agree to the terms of use
	 * @return the user
	 */
	@Override
	public User updateAgreedToTermsOfUse(
			long userId, boolean agreedToTermsOfUse)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userService.updateAgreedToTermsOfUse(
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
	@Override
	public User updateEmailAddress(
			long userId, String password, String emailAddress1,
			String emailAddress2, ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userService.updateEmailAddress(
			userId, password, emailAddress1, emailAddress2, serviceContext);
	}

	@Override
	public User updateExternalReferenceCode(
			long userId, String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userService.updateExternalReferenceCode(
			userId, externalReferenceCode);
	}

	@Override
	public User updateExternalReferenceCode(
			User user, String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userService.updateExternalReferenceCode(
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
	@Override
	public User updateIncompleteUser(
			long companyId, boolean autoPassword, String password1,
			String password2, boolean autoScreenName, String screenName,
			String emailAddress, java.util.Locale locale, String firstName,
			String middleName, String lastName, long prefixListTypeId,
			long suffixListTypeId, boolean male, int birthdayMonth,
			int birthdayDay, int birthdayYear, String jobTitle,
			boolean updateUserInformation, boolean sendEmail,
			ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userService.updateIncompleteUser(
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
	@Override
	public User updateIncompleteUser(
			long companyId, boolean autoPassword, String password1,
			String password2, boolean autoScreenName, String screenName,
			String emailAddress, long facebookId, String openId,
			java.util.Locale locale, String firstName, String middleName,
			String lastName, long prefixListTypeId, long suffixListTypeId,
			boolean male, int birthdayMonth, int birthdayDay, int birthdayYear,
			String jobTitle, boolean updateUserInformation, boolean sendEmail,
			ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userService.updateIncompleteUser(
			companyId, autoPassword, password1, password2, autoScreenName,
			screenName, emailAddress, facebookId, openId, locale, firstName,
			middleName, lastName, prefixListTypeId, suffixListTypeId, male,
			birthdayMonth, birthdayDay, birthdayYear, jobTitle,
			updateUserInformation, sendEmail, serviceContext);
	}

	@Override
	public User updateLanguageId(long userId, String languageId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userService.updateLanguageId(userId, languageId);
	}

	/**
	 * Updates whether the user is locked out from logging in.
	 *
	 * @param userId the primary key of the user
	 * @param lockout whether the user is locked out
	 * @return the user
	 */
	@Override
	public User updateLockoutById(long userId, boolean lockout)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userService.updateLockoutById(userId, lockout);
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
	@Override
	public void updateOrganizations(
			long userId, long[] organizationIds, ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		_userService.updateOrganizations(
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
	@Override
	public User updatePassword(
			long userId, String password1, String password2,
			boolean passwordReset)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userService.updatePassword(
			userId, password1, password2, passwordReset);
	}

	/**
	 * Updates the user's portrait image.
	 *
	 * @param userId the primary key of the user
	 * @param bytes the new portrait image data
	 * @return the user
	 */
	@Override
	public User updatePortrait(long userId, byte[] bytes)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userService.updatePortrait(userId, bytes);
	}

	/**
	 * Updates the user's password reset question and answer.
	 *
	 * @param userId the primary key of the user
	 * @param question the user's new password reset question
	 * @param answer the user's new password reset answer
	 * @return the user
	 */
	@Override
	public User updateReminderQuery(long userId, String question, String answer)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userService.updateReminderQuery(userId, question, answer);
	}

	/**
	 * Updates the user's screen name.
	 *
	 * @param userId the primary key of the user
	 * @param screenName the user's new screen name
	 * @return the user
	 */
	@Override
	public User updateScreenName(long userId, String screenName)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userService.updateScreenName(userId, screenName);
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
	@Override
	public User updateStatus(
			long userId, int status, ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userService.updateStatus(userId, status, serviceContext);
	}

	@Override
	public User updateStatus(
			User user, int status, ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userService.updateStatus(user, status, serviceContext);
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
			java.util.List<com.liferay.portal.kernel.model.UserGroupRole>
				userGroupRoles,
			long[] userGroupIds,
			java.util.List<com.liferay.portal.kernel.model.Address> addresses,
			java.util.List<com.liferay.portal.kernel.model.EmailAddress>
				emailAddresses,
			java.util.List<com.liferay.portal.kernel.model.Phone> phones,
			java.util.List<com.liferay.portal.kernel.model.Website> websites,
			java.util.List
				<com.liferay.announcements.kernel.model.AnnouncementsDelivery>
					announcementsDelivers,
			ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userService.updateUser(
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
			java.util.List<com.liferay.portal.kernel.model.UserGroupRole>
				userGroupRoles,
			long[] userGroupIds, ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userService.updateUser(
			userId, oldPassword, newPassword1, newPassword2, passwordReset,
			reminderQueryQuestion, reminderQueryAnswer, screenName,
			emailAddress, languageId, timeZoneId, greeting, comments, firstName,
			middleName, lastName, prefixListTypeId, suffixListTypeId, male,
			birthdayMonth, birthdayDay, birthdayYear, smsSn, facebookSn,
			jabberSn, skypeSn, twitterSn, jobTitle, groupIds, organizationIds,
			roleIds, userGroupRoles, userGroupIds, serviceContext);
	}

	@Override
	public UserService getWrappedService() {
		return _userService;
	}

	@Override
	public void setWrappedService(UserService userService) {
		_userService = userService;
	}

	private UserService _userService;

}
// LIFERAY-SERVICE-BUILDER-HASH:105785649