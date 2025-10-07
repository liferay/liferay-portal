/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * Provides the local service utility for User. This utility wraps
 * <code>com.liferay.portal.service.impl.UserLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see UserLocalService
 * @generated
 */
public class UserLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.service.impl.UserLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds a default admin user for the company.
	 *
	 * @param companyId the primary key of the user's company
	 * @param password the password of the user
	 * @param screenName the user's screen name
	 * @param emailAddress the user's email address
	 * @param locale the user's locale
	 * @param firstName the user's first name
	 * @param middleName the user's middle name
	 * @param lastName the user's last name
	 * @return the new default admin user
	 */
	public static User addDefaultAdminUser(
			long companyId, String password, String screenName,
			String emailAddress, java.util.Locale locale, String firstName,
			String middleName, String lastName)
		throws PortalException {

		return getService().addDefaultAdminUser(
			companyId, password, screenName, emailAddress, locale, firstName,
			middleName, lastName);
	}

	/**
	 * Adds the user to the default groups, unless the user is already in these
	 * groups. The default groups can be specified in
	 * <code>portal.properties</code> with the key
	 * <code>admin.default.group.names</code>.
	 *
	 * @param userId the primary key of the user
	 * @return <code>true</code> if user was added to default groups;
	 <code>false</code> if user was already a member
	 */
	public static boolean addDefaultGroups(long userId) throws PortalException {
		return getService().addDefaultGroups(userId);
	}

	/**
	 * Adds the user to the default regular roles, unless the user already has
	 * these regular roles. The default regular roles can be specified in
	 * <code>portal.properties</code> with the key
	 * <code>admin.default.role.names</code>.
	 *
	 * @param userId the primary key of the user
	 * @return <code>true</code> if user was given default roles;
	 <code>false</code> if user already has default roles
	 */
	public static boolean addDefaultRoles(long userId) throws PortalException {
		return getService().addDefaultRoles(userId);
	}

	public static User addDefaultServiceAccountUser(long companyId)
		throws PortalException {

		return getService().addDefaultServiceAccountUser(companyId);
	}

	/**
	 * Adds the user to the default user groups, unless the user is already in
	 * these user groups. The default user groups can be specified in
	 * <code>portal.properties</code> with the property
	 * <code>admin.default.user.group.names</code>.
	 *
	 * @param userId the primary key of the user
	 * @return <code>true</code> if user was added to default user groups;
	 <code>false</code> if user is already a user group member
	 */
	public static boolean addDefaultUserGroups(long userId)
		throws PortalException {

		return getService().addDefaultUserGroups(userId);
	}

	public static boolean addGroupUser(long groupId, long userId) {
		return getService().addGroupUser(groupId, userId);
	}

	public static boolean addGroupUser(long groupId, User user) {
		return getService().addGroupUser(groupId, user);
	}

	/**
	 * @throws PortalException
	 */
	public static boolean addGroupUsers(long groupId, List<User> users)
		throws PortalException {

		return getService().addGroupUsers(groupId, users);
	}

	/**
	 * @throws PortalException
	 */
	public static boolean addGroupUsers(long groupId, long[] userIds)
		throws PortalException {

		return getService().addGroupUsers(groupId, userIds);
	}

	public static boolean addOrganizationUser(
		long organizationId, long userId) {

		return getService().addOrganizationUser(organizationId, userId);
	}

	public static boolean addOrganizationUser(long organizationId, User user) {
		return getService().addOrganizationUser(organizationId, user);
	}

	/**
	 * @throws PortalException
	 */
	public static boolean addOrganizationUsers(
			long organizationId, List<User> users)
		throws PortalException {

		return getService().addOrganizationUsers(organizationId, users);
	}

	/**
	 * @throws PortalException
	 */
	public static boolean addOrganizationUsers(
			long organizationId, long[] userIds)
		throws PortalException {

		return getService().addOrganizationUsers(organizationId, userIds);
	}

	public static User addOrUpdateUser(
			String externalReferenceCode, long creatorUserId, long companyId,
			boolean autoPassword, String password1, String password2,
			boolean autoScreenName, String screenName, String emailAddress,
			java.util.Locale locale, String firstName, String middleName,
			String lastName, long prefixListTypeId, long suffixListTypeId,
			boolean male, int birthdayMonth, int birthdayDay, int birthdayYear,
			String jobTitle, boolean sendEmail, ServiceContext serviceContext)
		throws PortalException {

		return getService().addOrUpdateUser(
			externalReferenceCode, creatorUserId, companyId, autoPassword,
			password1, password2, autoScreenName, screenName, emailAddress,
			locale, firstName, middleName, lastName, prefixListTypeId,
			suffixListTypeId, male, birthdayMonth, birthdayDay, birthdayYear,
			jobTitle, sendEmail, serviceContext);
	}

	/**
	 * Assigns the password policy to the users, removing any other currently
	 * assigned password policies.
	 *
	 * @param passwordPolicyId the primary key of the password policy
	 * @param userIds the primary keys of the users
	 */
	public static void addPasswordPolicyUsers(
		long passwordPolicyId, long[] userIds) {

		getService().addPasswordPolicyUsers(passwordPolicyId, userIds);
	}

	public static boolean addRoleUser(long roleId, long userId) {
		return getService().addRoleUser(roleId, userId);
	}

	public static boolean addRoleUser(long roleId, User user) {
		return getService().addRoleUser(roleId, user);
	}

	/**
	 * @throws PortalException
	 */
	public static boolean addRoleUsers(long roleId, List<User> users)
		throws PortalException {

		return getService().addRoleUsers(roleId, users);
	}

	/**
	 * @throws PortalException
	 */
	public static boolean addRoleUsers(long roleId, long[] userIds)
		throws PortalException {

		return getService().addRoleUsers(roleId, userIds);
	}

	public static boolean addTeamUser(long teamId, long userId) {
		return getService().addTeamUser(teamId, userId);
	}

	public static boolean addTeamUser(long teamId, User user) {
		return getService().addTeamUser(teamId, user);
	}

	/**
	 * @throws PortalException
	 */
	public static boolean addTeamUsers(long teamId, List<User> users)
		throws PortalException {

		return getService().addTeamUsers(teamId, users);
	}

	/**
	 * @throws PortalException
	 */
	public static boolean addTeamUsers(long teamId, long[] userIds)
		throws PortalException {

		return getService().addTeamUsers(teamId, userIds);
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
	 * @param creatorUserId the primary key of the creator
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
	 * @param type the user's type
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
			long creatorUserId, long companyId, boolean autoPassword,
			String password1, String password2, boolean autoScreenName,
			String screenName, String emailAddress, java.util.Locale locale,
			String firstName, String middleName, String lastName,
			long prefixListTypeId, long suffixListTypeId, boolean male,
			int birthdayMonth, int birthdayDay, int birthdayYear,
			String jobTitle, int type, long[] groupIds, long[] organizationIds,
			long[] roleIds, long[] userGroupIds, boolean sendEmail,
			ServiceContext serviceContext)
		throws PortalException {

		return getService().addUser(
			creatorUserId, companyId, autoPassword, password1, password2,
			autoScreenName, screenName, emailAddress, locale, firstName,
			middleName, lastName, prefixListTypeId, suffixListTypeId, male,
			birthdayMonth, birthdayDay, birthdayYear, jobTitle, type, groupIds,
			organizationIds, roleIds, userGroupIds, sendEmail, serviceContext);
	}

	/**
	 * Adds the user to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect UserLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param user the user
	 * @return the user that was added
	 */
	public static User addUser(User user) {
		return getService().addUser(user);
	}

	public static boolean addUserGroupUser(long userGroupId, long userId) {
		return getService().addUserGroupUser(userGroupId, userId);
	}

	public static boolean addUserGroupUser(long userGroupId, User user) {
		return getService().addUserGroupUser(userGroupId, user);
	}

	/**
	 * @throws PortalException
	 */
	public static boolean addUserGroupUsers(long userGroupId, List<User> users)
		throws PortalException {

		return getService().addUserGroupUsers(userGroupId, users);
	}

	/**
	 * @throws PortalException
	 */
	public static boolean addUserGroupUsers(long userGroupId, long[] userIds)
		throws PortalException {

		return getService().addUserGroupUsers(userGroupId, userIds);
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
	 * @param creatorUserId the primary key of the creator
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
	 * @param type the user's type
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
			long creatorUserId, long companyId, boolean autoPassword,
			String password1, String password2, boolean autoScreenName,
			String screenName, String emailAddress, java.util.Locale locale,
			String firstName, String middleName, String lastName,
			long prefixListTypeId, long suffixListTypeId, boolean male,
			int birthdayMonth, int birthdayDay, int birthdayYear,
			String jobTitle, int type, long[] groupIds, long[] organizationIds,
			long[] roleIds, long[] userGroupIds, boolean sendEmail,
			ServiceContext serviceContext)
		throws PortalException {

		return getService().addUserWithWorkflow(
			creatorUserId, companyId, autoPassword, password1, password2,
			autoScreenName, screenName, emailAddress, locale, firstName,
			middleName, lastName, prefixListTypeId, suffixListTypeId, male,
			birthdayMonth, birthdayDay, birthdayYear, jobTitle, type, groupIds,
			organizationIds, roleIds, userGroupIds, sendEmail, serviceContext);
	}

	/**
	 * Attempts to authenticate the user by their email address and password,
	 * while using the AuthPipeline.
	 *
	 * @param companyId the primary key of the user's company
	 * @param emailAddress the user's email address
	 * @param password the user's password
	 * @param headerMap the header map from the authentication request
	 * @param parameterMap the parameter map from the authentication request
	 * @param resultsMap the map of authentication results (may be nil). After
	 a successful authentication the user's primary key will be placed
	 under the key <code>userId</code>.
	 * @return the authentication status. This can be {@link
	 Authenticator#FAILURE} indicating that the user's credentials are
	 invalid, {@link Authenticator#SUCCESS} indicating a successful
	 login, or {@link Authenticator#DNE} indicating that a user with
	 that login does not exist.
	 * @see AuthPipeline
	 */
	public static int authenticateByEmailAddress(
			long companyId, String emailAddress, String password,
			Map<String, String[]> headerMap, Map<String, String[]> parameterMap,
			Map<String, Object> resultsMap)
		throws PortalException {

		return getService().authenticateByEmailAddress(
			companyId, emailAddress, password, headerMap, parameterMap,
			resultsMap);
	}

	/**
	 * Attempts to authenticate the user by their screen name and password,
	 * while using the AuthPipeline.
	 *
	 * @param companyId the primary key of the user's company
	 * @param screenName the user's screen name
	 * @param password the user's password
	 * @param headerMap the header map from the authentication request
	 * @param parameterMap the parameter map from the authentication request
	 * @param resultsMap the map of authentication results (may be nil). After
	 a successful authentication the user's primary key will be placed
	 under the key <code>userId</code>.
	 * @return the authentication status. This can be {@link
	 Authenticator#FAILURE} indicating that the user's credentials are
	 invalid, {@link Authenticator#SUCCESS} indicating a successful
	 login, or {@link Authenticator#DNE} indicating that a user with
	 that login does not exist.
	 * @see AuthPipeline
	 */
	public static int authenticateByScreenName(
			long companyId, String screenName, String password,
			Map<String, String[]> headerMap, Map<String, String[]> parameterMap,
			Map<String, Object> resultsMap)
		throws PortalException {

		return getService().authenticateByScreenName(
			companyId, screenName, password, headerMap, parameterMap,
			resultsMap);
	}

	/**
	 * Attempts to authenticate the user by their primary key and password,
	 * while using the AuthPipeline.
	 *
	 * @param companyId the primary key of the user's company
	 * @param userId the user's primary key
	 * @param password the user's password
	 * @param headerMap the header map from the authentication request
	 * @param parameterMap the parameter map from the authentication request
	 * @param resultsMap the map of authentication results (may be nil). After
	 a successful authentication the user's primary key will be placed
	 under the key <code>userId</code>.
	 * @return the authentication status. This can be {@link
	 Authenticator#FAILURE} indicating that the user's credentials are
	 invalid, {@link Authenticator#SUCCESS} indicating a successful
	 login, or {@link Authenticator#DNE} indicating that a user with
	 that login does not exist.
	 * @see AuthPipeline
	 */
	public static int authenticateByUserId(
			long companyId, long userId, String password,
			Map<String, String[]> headerMap, Map<String, String[]> parameterMap,
			Map<String, Object> resultsMap)
		throws PortalException {

		return getService().authenticateByUserId(
			companyId, userId, password, headerMap, parameterMap, resultsMap);
	}

	/**
	 * Attempts to authenticate the user using HTTP basic access authentication,
	 * without using the AuthPipeline. Primarily used for authenticating users
	 * of <code>tunnel-web</code>.
	 *
	 * <p>
	 * Authentication type specifies what <code>login</code> contains.The valid
	 * values are:
	 * </p>
	 *
	 * <ul>
	 * <li>
	 * <code>CompanyConstants.AUTH_TYPE_EA</code> - <code>login</code> is the
	 * user's email address
	 * </li>
	 * <li>
	 * <code>CompanyConstants.AUTH_TYPE_SN</code> - <code>login</code> is the
	 * user's screen name
	 * </li>
	 * <li>
	 * <code>CompanyConstants.AUTH_TYPE_ID</code> - <code>login</code> is the
	 * user's primary key
	 * </li>
	 * </ul>
	 *
	 * @param companyId the primary key of the user's company
	 * @param authType the type of authentication to perform
	 * @param login either the user's email address, screen name, or primary
	 key depending on the value of <code>authType</code>
	 * @param password the user's password
	 * @return the user's primary key if authentication is successful;
	 <code>0</code> otherwise
	 */
	public static long authenticateForBasic(
			long companyId, String authType, String login, String password)
		throws PortalException {

		return getService().authenticateForBasic(
			companyId, authType, login, password);
	}

	/**
	 * Attempts to authenticate the user using HTTP digest access
	 * authentication, without using the AuthPipeline. Primarily used for
	 * authenticating users of <code>tunnel-web</code>.
	 *
	 * @param companyId the primary key of the user's company
	 * @param realm unused
	 * @param nonce the number used once
	 * @param method the request method
	 * @param uri the request URI
	 * @param response the authentication response hash
	 * @return the user's primary key if authentication is successful;
	 <code>0</code> otherwise
	 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
	 */
	@Deprecated
	public static long authenticateForDigest(
			long companyId, String userName, String realm, String nonce,
			String method, String uri, String response)
		throws PortalException {

		return getService().authenticateForDigest(
			companyId, userName, realm, nonce, method, uri, response);
	}

	/**
	 * Checks if the user is currently locked out based on the password policy,
	 * and performs maintenance on the user's lockout and failed login data.
	 *
	 * @param user the user
	 */
	public static void checkLockout(User user) throws PortalException {
		getService().checkLockout(user);
	}

	/**
	 * Adds a failed login attempt to the user and updates the user's last
	 * failed login date.
	 *
	 * @param user the user
	 */
	public static void checkLoginFailure(User user) {
		getService().checkLoginFailure(user);
	}

	/**
	 * Adds a failed login attempt to the user with the email address and
	 * updates the user's last failed login date.
	 *
	 * @param companyId the primary key of the user's company
	 * @param emailAddress the user's email address
	 */
	public static void checkLoginFailureByEmailAddress(
			long companyId, String emailAddress)
		throws PortalException {

		getService().checkLoginFailureByEmailAddress(companyId, emailAddress);
	}

	/**
	 * Adds a failed login attempt to the user and updates the user's last
	 * failed login date.
	 *
	 * @param userId the primary key of the user
	 */
	public static void checkLoginFailureById(long userId)
		throws PortalException {

		getService().checkLoginFailureById(userId);
	}

	/**
	 * Adds a failed login attempt to the user with the screen name and updates
	 * the user's last failed login date.
	 *
	 * @param companyId the primary key of the user's company
	 * @param screenName the user's screen name
	 */
	public static void checkLoginFailureByScreenName(
			long companyId, String screenName)
		throws PortalException {

		getService().checkLoginFailureByScreenName(companyId, screenName);
	}

	/**
	 * Checks if the user's password is expired based on the password policy,
	 * and performs maintenance on the user's grace login and password reset
	 * data.
	 *
	 * @param user the user
	 */
	public static void checkPasswordExpired(User user) throws PortalException {
		getService().checkPasswordExpired(user);
	}

	public static void clearGroupUsers(long groupId) {
		getService().clearGroupUsers(groupId);
	}

	public static void clearOrganizationUsers(long organizationId) {
		getService().clearOrganizationUsers(organizationId);
	}

	public static void clearRoleUsers(long roleId) {
		getService().clearRoleUsers(roleId);
	}

	public static void clearTeamUsers(long teamId) {
		getService().clearTeamUsers(teamId);
	}

	public static void clearUserGroupUsers(long userGroupId) {
		getService().clearUserGroupUsers(userGroupId);
	}

	/**
	 * Completes the user's registration by generating a password and sending
	 * the confirmation email.
	 *
	 * @param user the user
	 * @param serviceContext the service context to be applied. You
	 automatically generate a password for the user by setting attribute
	 <code>autoPassword</code> to <code>true</code>. You can send a
	 confirmation email to the user by setting attribute
	 <code>sendEmail</code> to <code>true</code>.
	 */
	public static void completeUserRegistration(
			User user, ServiceContext serviceContext)
		throws PortalException {

		getService().completeUserRegistration(user, serviceContext);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	/**
	 * Creates a new user with the primary key. Does not add the user to the database.
	 *
	 * @param userId the primary key for the new user
	 * @return the new user
	 */
	public static User createUser(long userId) {
		return getService().createUser(userId);
	}

	public static void deleteGroupUser(long groupId, long userId) {
		getService().deleteGroupUser(groupId, userId);
	}

	public static void deleteGroupUser(long groupId, User user) {
		getService().deleteGroupUser(groupId, user);
	}

	public static void deleteGroupUsers(long groupId, List<User> users) {
		getService().deleteGroupUsers(groupId, users);
	}

	public static void deleteGroupUsers(long groupId, long[] userIds) {
		getService().deleteGroupUsers(groupId, userIds);
	}

	public static void deleteOrganizationUser(
		long organizationId, long userId) {

		getService().deleteOrganizationUser(organizationId, userId);
	}

	public static void deleteOrganizationUser(long organizationId, User user) {
		getService().deleteOrganizationUser(organizationId, user);
	}

	public static void deleteOrganizationUsers(
		long organizationId, List<User> users) {

		getService().deleteOrganizationUsers(organizationId, users);
	}

	public static void deleteOrganizationUsers(
		long organizationId, long[] userIds) {

		getService().deleteOrganizationUsers(organizationId, userIds);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
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
	 * @throws PortalException
	 */
	public static void deleteRoleUser(long roleId, long userId)
		throws PortalException {

		getService().deleteRoleUser(roleId, userId);
	}

	/**
	 * @throws PortalException
	 */
	public static void deleteRoleUser(long roleId, User user)
		throws PortalException {

		getService().deleteRoleUser(roleId, user);
	}

	public static void deleteRoleUsers(long roleId, List<User> users) {
		getService().deleteRoleUsers(roleId, users);
	}

	public static void deleteRoleUsers(long roleId, long[] userIds) {
		getService().deleteRoleUsers(roleId, userIds);
	}

	public static void deleteTeamUser(long teamId, long userId) {
		getService().deleteTeamUser(teamId, userId);
	}

	public static void deleteTeamUser(long teamId, User user) {
		getService().deleteTeamUser(teamId, user);
	}

	public static void deleteTeamUsers(long teamId, List<User> users) {
		getService().deleteTeamUsers(teamId, users);
	}

	public static void deleteTeamUsers(long teamId, long[] userIds) {
		getService().deleteTeamUsers(teamId, userIds);
	}

	/**
	 * Deletes the user with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect UserLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param userId the primary key of the user
	 * @return the user that was removed
	 * @throws PortalException if a user with the primary key could not be found
	 */
	public static User deleteUser(long userId) throws PortalException {
		return getService().deleteUser(userId);
	}

	/**
	 * Deletes the user from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect UserLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param user the user
	 * @return the user that was removed
	 * @throws PortalException
	 */
	public static User deleteUser(User user) throws PortalException {
		return getService().deleteUser(user);
	}

	/**
	 * @throws PortalException
	 */
	public static void deleteUserGroupUser(long userGroupId, long userId)
		throws PortalException {

		getService().deleteUserGroupUser(userGroupId, userId);
	}

	/**
	 * @throws PortalException
	 */
	public static void deleteUserGroupUser(long userGroupId, User user)
		throws PortalException {

		getService().deleteUserGroupUser(userGroupId, user);
	}

	public static void deleteUserGroupUsers(
		long userGroupId, List<User> users) {

		getService().deleteUserGroupUsers(userGroupId, users);
	}

	public static void deleteUserGroupUsers(long userGroupId, long[] userIds) {
		getService().deleteUserGroupUsers(userGroupId, userIds);
	}

	public static <T> T dslQuery(DSLQuery dslQuery) {
		return getService().dslQuery(dslQuery);
	}

	public static int dslQueryCount(DSLQuery dslQuery) {
		return getService().dslQueryCount(dslQuery);
	}

	public static DynamicQuery dynamicQuery() {
		return getService().dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	public static <T> List<T> dynamicQuery(DynamicQuery dynamicQuery) {
		return getService().dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @return the range of matching rows
	 */
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getService().dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching rows
	 */
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator) {

		return getService().dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(DynamicQuery dynamicQuery) {
		return getService().dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(
		DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return getService().dynamicQueryCount(dynamicQuery, projection);
	}

	/**
	 * Encrypts the primary key of the user. Used when encrypting the user's
	 * credentials for storage in an automatic login cookie.
	 *
	 * @param name the primary key of the user
	 * @return the user's encrypted primary key
	 */
	public static String encryptUserId(String name) throws PortalException {
		return getService().encryptUserId(name);
	}

	/**
	 * Returns the guest user for the company.
	 *
	 * @param companyId the primary key of the company
	 * @return the guest user for the company, or <code>null</code> if a user
	 with the company key could not be found
	 */
	public static User fetchGuestUser(long companyId) {
		return getService().fetchGuestUser(companyId);
	}

	public static User fetchUser(long userId) {
		return getService().fetchUser(userId);
	}

	/**
	 * Returns the user with the contact ID.
	 *
	 * @param contactId the user's contact ID
	 * @return the user with the contact ID, or <code>null</code> if a user with
	 the contact ID could not be found
	 */
	public static User fetchUserByContactId(long contactId) {
		return getService().fetchUserByContactId(contactId);
	}

	/**
	 * Returns the user with the email address.
	 *
	 * @param companyId the primary key of the user's company
	 * @param emailAddress the user's email address
	 * @return the user with the email address, or <code>null</code> if a user
	 with the email address could not be found
	 */
	public static User fetchUserByEmailAddress(
		long companyId, String emailAddress) {

		return getService().fetchUserByEmailAddress(companyId, emailAddress);
	}

	public static User fetchUserByExternalReferenceCode(
		String externalReferenceCode, long companyId) {

		return getService().fetchUserByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the user with the Facebook ID.
	 *
	 * @param companyId the primary key of the user's company
	 * @param facebookId the user's Facebook ID
	 * @return the user with the Facebook ID, or <code>null</code> if a user
	 with the Facebook ID could not be found
	 */
	public static User fetchUserByFacebookId(long companyId, long facebookId) {
		return getService().fetchUserByFacebookId(companyId, facebookId);
	}

	/**
	 * Returns the user with the primary key.
	 *
	 * @param userId the primary key of the user
	 * @return the user with the primary key, or <code>null</code> if a user
	 with the primary key could not be found
	 */
	public static User fetchUserById(long userId) {
		return getService().fetchUserById(userId);
	}

	/**
	 * Returns the user with the portrait ID.
	 *
	 * @param portraitId the user's portrait ID
	 * @return the user with the portrait ID, or <code>null</code> if a user
	 with the portrait ID could not be found
	 */
	public static User fetchUserByPortraitId(long portraitId) {
		return getService().fetchUserByPortraitId(portraitId);
	}

	/**
	 * Returns the user with the screen name.
	 *
	 * @param companyId the primary key of the user's company
	 * @param screenName the user's screen name
	 * @return the user with the screen name, or <code>null</code> if a user
	 with the screen name could not be found
	 */
	public static User fetchUserByScreenName(
		long companyId, String screenName) {

		return getService().fetchUserByScreenName(companyId, screenName);
	}

	/**
	 * Returns the user with the matching UUID and company.
	 *
	 * @param uuid the user's UUID
	 * @param companyId the primary key of the company
	 * @return the matching user, or <code>null</code> if a matching user could not be found
	 */
	public static User fetchUserByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().fetchUserByUuidAndCompanyId(uuid, companyId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns a range of all the users belonging to the company.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of users
	 * @param end the upper bound of the range of users (not inclusive)
	 * @return the range of users belonging to the company
	 */
	public static List<User> getCompanyUsers(
		long companyId, int start, int end) {

		return getService().getCompanyUsers(companyId, start, end);
	}

	/**
	 * Returns the number of users belonging to the company.
	 *
	 * @param companyId the primary key of the company
	 * @return the number of users belonging to the company
	 */
	public static int getCompanyUsersCount(long companyId) {
		return getService().getCompanyUsersCount(companyId);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 #getGuestUser(long)}
	 */
	@Deprecated
	public static User getDefaultUser(long companyId) throws PortalException {
		return getService().getDefaultUser(companyId);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 #getGuestUserId(long)}
	 */
	@Deprecated
	public static long getDefaultUserId(long companyId) throws PortalException {
		return getService().getDefaultUserId(companyId);
	}

	public static com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return getService().getExportActionableDynamicQuery(portletDataContext);
	}

	/**
	 * Returns the groupIds of the groups associated with the user.
	 *
	 * @param userId the userId of the user
	 * @return long[] the groupIds of groups associated with the user
	 */
	public static long[] getGroupPrimaryKeys(long userId) {
		return getService().getGroupPrimaryKeys(userId);
	}

	/**
	 * Returns the primary keys of all the users belonging to the group.
	 *
	 * @param groupId the primary key of the group
	 * @return the primary keys of the users belonging to the group
	 */
	public static long[] getGroupUserIds(long groupId) {
		return getService().getGroupUserIds(groupId);
	}

	public static List<User> getGroupUsers(long groupId) {
		return getService().getGroupUsers(groupId);
	}

	/**
	 * @throws PortalException
	 */
	public static List<User> getGroupUsers(long groupId, int start, int end)
		throws PortalException {

		return getService().getGroupUsers(groupId, start, end);
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

	public static List<User> getGroupUsers(
		long groupId, int start, int end,
		OrderByComparator<User> orderByComparator) {

		return getService().getGroupUsers(
			groupId, start, end, orderByComparator);
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

	public static int getGroupUsersCount(long groupId) {
		return getService().getGroupUsersCount(groupId);
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

	/**
	 * Returns the guest user for the company.
	 *
	 * @param companyId the primary key of the company
	 * @return the guest user for the company
	 */
	public static User getGuestUser(long companyId) throws PortalException {
		return getService().getGuestUser(companyId);
	}

	/**
	 * Returns the primary key of the guest user for the company.
	 *
	 * @param companyId the primary key of the company
	 * @return the primary key of the guest user for the company
	 */
	public static long getGuestUserId(long companyId) throws PortalException {
		return getService().getGuestUserId(companyId);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	public static List<User> getInheritedRoleUsers(
			long roleId, int start, int end,
			OrderByComparator<User> orderByComparator)
		throws PortalException {

		return getService().getInheritedRoleUsers(
			roleId, start, end, orderByComparator);
	}

	/**
	 * Returns all the users who have not had any announcements of the type
	 * delivered, excluding the default user.
	 *
	 * @param type the type of announcement
	 * @return the users who have not had any annoucements of the type delivered
	 */
	public static List<User> getNoAnnouncementsDeliveries(String type) {
		return getService().getNoAnnouncementsDeliveries(type);
	}

	/**
	 * Returns all the users who do not belong to any groups, excluding the
	 * default user.
	 *
	 * @return the users who do not belong to any groups
	 */
	public static List<User> getNoGroups() {
		return getService().getNoGroups();
	}

	/**
	 * Returns the organizationIds of the organizations associated with the user.
	 *
	 * @param userId the userId of the user
	 * @return long[] the organizationIds of organizations associated with the user
	 */
	public static long[] getOrganizationPrimaryKeys(long userId) {
		return getService().getOrganizationPrimaryKeys(userId);
	}

	public static int getOrganizationsAndUserGroupsUsersCount(
		long[] organizationIds, long[] userGroupIds) {

		return getService().getOrganizationsAndUserGroupsUsersCount(
			organizationIds, userGroupIds);
	}

	/**
	 * Returns the primary keys of all the users belonging to the organization.
	 *
	 * @param organizationId the primary key of the organization
	 * @return the primary keys of the users belonging to the organization
	 */
	public static long[] getOrganizationUserIds(long organizationId) {
		return getService().getOrganizationUserIds(organizationId);
	}

	public static List<User> getOrganizationUsers(long organizationId) {
		return getService().getOrganizationUsers(organizationId);
	}

	/**
	 * @throws PortalException
	 */
	public static List<User> getOrganizationUsers(
			long organizationId, int start, int end)
		throws PortalException {

		return getService().getOrganizationUsers(organizationId, start, end);
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

	public static List<User> getOrganizationUsers(
		long organizationId, int start, int end,
		OrderByComparator<User> orderByComparator) {

		return getService().getOrganizationUsers(
			organizationId, start, end, orderByComparator);
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

	public static int getOrganizationUsersCount(long organizationId) {
		return getService().getOrganizationUsersCount(organizationId);
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
	 * @throws PortalException
	 */
	public static PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return getService().getPersistedModel(primaryKeyObj);
	}

	/**
	 * Returns the roleIds of the roles associated with the user.
	 *
	 * @param userId the userId of the user
	 * @return long[] the roleIds of roles associated with the user
	 */
	public static long[] getRolePrimaryKeys(long userId) {
		return getService().getRolePrimaryKeys(userId);
	}

	/**
	 * Returns the primary keys of all the users belonging to the role.
	 *
	 * @param roleId the primary key of the role
	 * @return the primary keys of the users belonging to the role
	 */
	public static long[] getRoleUserIds(long roleId) {
		return getService().getRoleUserIds(roleId);
	}

	public static long[] getRoleUserIds(long roleId, long type) {
		return getService().getRoleUserIds(roleId, type);
	}

	public static List<User> getRoleUsers(long roleId) {
		return getService().getRoleUsers(roleId);
	}

	public static List<User> getRoleUsers(long roleId, int start, int end) {
		return getService().getRoleUsers(roleId, start, end);
	}

	public static List<User> getRoleUsers(
		long roleId, int start, int end,
		OrderByComparator<User> orderByComparator) {

		return getService().getRoleUsers(roleId, start, end, orderByComparator);
	}

	public static int getRoleUsersCount(long roleId) {
		return getService().getRoleUsersCount(roleId);
	}

	/**
	 * Returns the number of users with the status belonging to the role.
	 *
	 * @param roleId the primary key of the role
	 * @param status the workflow status
	 * @return the number of users with the status belonging to the role
	 */
	public static int getRoleUsersCount(long roleId, int status)
		throws PortalException {

		return getService().getRoleUsersCount(roleId, status);
	}

	public static List<User> getSocialUsers(
			long userId, int socialRelationType,
			String socialRelationTypeComparator, int start, int end,
			OrderByComparator<User> orderByComparator)
		throws PortalException {

		return getService().getSocialUsers(
			userId, socialRelationType, socialRelationTypeComparator, start,
			end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the users with a mutual social relation
	 * of the type with both of the given users.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param userId1 the primary key of the first user
	 * @param userId2 the primary key of the second user
	 * @param socialRelationType the type of social relation. The possible
	 types can be found in {@link SocialRelationConstants}.
	 * @param start the lower bound of the range of users
	 * @param end the upper bound of the range of users (not inclusive)
	 * @param orderByComparator the comparator to order the users by
	 (optionally <code>null</code>)
	 * @return the ordered range of users with a mutual social relation of the
	 type with the user
	 */
	public static List<User> getSocialUsers(
			long userId1, long userId2, int socialRelationType, int start,
			int end, OrderByComparator<User> orderByComparator)
		throws PortalException {

		return getService().getSocialUsers(
			userId1, userId2, socialRelationType, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the users with a mutual social relation
	 * with both of the given users.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param userId1 the primary key of the first user
	 * @param userId2 the primary key of the second user
	 * @param start the lower bound of the range of users
	 * @param end the upper bound of the range of users (not inclusive)
	 * @param orderByComparator the comparator to order the users by
	 (optionally <code>null</code>)
	 * @return the ordered range of users with a mutual social relation with the
	 user
	 */
	public static List<User> getSocialUsers(
			long userId1, long userId2, int start, int end,
			OrderByComparator<User> orderByComparator)
		throws PortalException {

		return getService().getSocialUsers(
			userId1, userId2, start, end, orderByComparator);
	}

	/**
	 * Returns the number of users with a social relation with the user.
	 *
	 * @param userId the primary key of the user
	 * @param socialRelationType the type of social relation. The possible
	 types can be found in {@link SocialRelationConstants}.
	 * @return the number of users with a social relation with the user
	 */
	public static int getSocialUsersCount(
			long userId, int socialRelationType,
			String socialRelationTypeComparator)
		throws PortalException {

		return getService().getSocialUsersCount(
			userId, socialRelationType, socialRelationTypeComparator);
	}

	/**
	 * Returns the number of users with a mutual social relation with both of
	 * the given users.
	 *
	 * @param userId1 the primary key of the first user
	 * @param userId2 the primary key of the second user
	 * @return the number of users with a mutual social relation with the user
	 */
	public static int getSocialUsersCount(long userId1, long userId2)
		throws PortalException {

		return getService().getSocialUsersCount(userId1, userId2);
	}

	/**
	 * Returns the number of users with a mutual social relation of the type
	 * with both of the given users.
	 *
	 * @param userId1 the primary key of the first user
	 * @param userId2 the primary key of the second user
	 * @param socialRelationType the type of social relation. The possible
	 types can be found in {@link SocialRelationConstants}.
	 * @return the number of users with a mutual social relation of the type
	 with the user
	 */
	public static int getSocialUsersCount(
			long userId1, long userId2, int socialRelationType)
		throws PortalException {

		return getService().getSocialUsersCount(
			userId1, userId2, socialRelationType);
	}

	/**
	 * Returns the teamIds of the teams associated with the user.
	 *
	 * @param userId the userId of the user
	 * @return long[] the teamIds of teams associated with the user
	 */
	public static long[] getTeamPrimaryKeys(long userId) {
		return getService().getTeamPrimaryKeys(userId);
	}

	public static List<User> getTeamUsers(long teamId) {
		return getService().getTeamUsers(teamId);
	}

	public static List<User> getTeamUsers(long teamId, int start, int end) {
		return getService().getTeamUsers(teamId, start, end);
	}

	public static List<User> getTeamUsers(
		long teamId, int start, int end,
		OrderByComparator<User> orderByComparator) {

		return getService().getTeamUsers(teamId, start, end, orderByComparator);
	}

	public static int getTeamUsersCount(long teamId) {
		return getService().getTeamUsersCount(teamId);
	}

	/**
	 * Returns the user with the primary key.
	 *
	 * @param userId the primary key of the user
	 * @return the user
	 * @throws PortalException if a user with the primary key could not be found
	 */
	public static User getUser(long userId) throws PortalException {
		return getService().getUser(userId);
	}

	/**
	 * Returns the user with the contact ID.
	 *
	 * @param contactId the user's contact ID
	 * @return the user with the contact ID
	 */
	public static User getUserByContactId(long contactId)
		throws PortalException {

		return getService().getUserByContactId(contactId);
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
	 * Returns the user with the primary key from the company.
	 *
	 * @param companyId the primary key of the user's company
	 * @param userId the primary key of the user
	 * @return the user with the primary key
	 */
	public static User getUserById(long companyId, long userId)
		throws PortalException {

		return getService().getUserById(companyId, userId);
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

	/**
	 * Returns the user with the matching UUID and company.
	 *
	 * @param uuid the user's UUID
	 * @param companyId the primary key of the company
	 * @return the matching user
	 * @throws PortalException if a matching user could not be found
	 */
	public static User getUserByUuidAndCompanyId(String uuid, long companyId)
		throws PortalException {

		return getService().getUserByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns the userGroupIds of the user groups associated with the user.
	 *
	 * @param userId the userId of the user
	 * @return long[] the userGroupIds of user groups associated with the user
	 */
	public static long[] getUserGroupPrimaryKeys(long userId) {
		return getService().getUserGroupPrimaryKeys(userId);
	}

	public static List<User> getUserGroupUsers(long userGroupId) {
		return getService().getUserGroupUsers(userGroupId);
	}

	public static List<User> getUserGroupUsers(
		long userGroupId, int start, int end) {

		return getService().getUserGroupUsers(userGroupId, start, end);
	}

	public static List<User> getUserGroupUsers(
		long userGroupId, int start, int end,
		OrderByComparator<User> orderByComparator) {

		return getService().getUserGroupUsers(
			userGroupId, start, end, orderByComparator);
	}

	public static int getUserGroupUsersCount(long userGroupId) {
		return getService().getUserGroupUsersCount(userGroupId);
	}

	/**
	 * Returns the number of users with the status belonging to the user group.
	 *
	 * @param userGroupId the primary key of the user group
	 * @param status the workflow status
	 * @return the number of users with the status belonging to the user group
	 */
	public static int getUserGroupUsersCount(long userGroupId, int status)
		throws PortalException {

		return getService().getUserGroupUsersCount(userGroupId, status);
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
	 * Returns a range of all the users.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of users
	 * @param end the upper bound of the range of users (not inclusive)
	 * @return the range of users
	 */
	public static List<User> getUsers(int start, int end) {
		return getService().getUsers(start, end);
	}

	public static List<User> getUsers(
		long companyId, int status, int start, int end,
		OrderByComparator<User> orderByComparator) {

		return getService().getUsers(
			companyId, status, start, end, orderByComparator);
	}

	public static List<User> getUsersByRoleId(long roleId, int start, int end)
		throws PortalException {

		return getService().getUsersByRoleId(roleId, start, end);
	}

	public static List<User> getUsersByRoleName(
			long companyId, String roleName, int start, int end)
		throws PortalException {

		return getService().getUsersByRoleName(companyId, roleName, start, end);
	}

	/**
	 * Returns the number of users.
	 *
	 * @return the number of users
	 */
	public static int getUsersCount() {
		return getService().getUsersCount();
	}

	public static int getUsersCount(long companyId, int status) {
		return getService().getUsersCount(companyId, status);
	}

	public static boolean hasGroupUser(long groupId, long userId) {
		return getService().hasGroupUser(groupId, userId);
	}

	public static boolean hasGroupUsers(long groupId) {
		return getService().hasGroupUsers(groupId);
	}

	public static boolean hasOrganizationUser(
		long organizationId, long userId) {

		return getService().hasOrganizationUser(organizationId, userId);
	}

	public static boolean hasOrganizationUsers(long organizationId) {
		return getService().hasOrganizationUsers(organizationId);
	}

	/**
	 * Returns <code>true</code> if the password policy has been assigned to the
	 * user.
	 *
	 * @param passwordPolicyId the primary key of the password policy
	 * @param userId the primary key of the user
	 * @return <code>true</code> if the password policy is assigned to the user;
	 <code>false</code> otherwise
	 */
	public static boolean hasPasswordPolicyUser(
		long passwordPolicyId, long userId) {

		return getService().hasPasswordPolicyUser(passwordPolicyId, userId);
	}

	public static boolean hasRoleUser(long roleId, long userId) {
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

	public static boolean hasRoleUsers(long roleId) {
		return getService().hasRoleUsers(roleId);
	}

	public static boolean hasTeamUser(long teamId, long userId) {
		return getService().hasTeamUser(teamId, userId);
	}

	public static boolean hasTeamUsers(long teamId) {
		return getService().hasTeamUsers(teamId);
	}

	public static boolean hasUserGroupUser(long userGroupId, long userId) {
		return getService().hasUserGroupUser(userGroupId, userId);
	}

	public static boolean hasUserGroupUsers(long userGroupId) {
		return getService().hasUserGroupUsers(userGroupId);
	}

	/**
	 * Returns <code>true</code> if the user's password is expired.
	 *
	 * @param user the user
	 * @return <code>true</code> if the user's password is expired;
	 <code>false</code> otherwise
	 */
	public static boolean isPasswordExpired(User user) throws PortalException {
		return getService().isPasswordExpired(user);
	}

	/**
	 * Returns the guest user for the company.
	 *
	 * @param companyId the primary key of the company
	 * @return the guest user for the company
	 */
	public static User loadGetGuestUser(long companyId) throws PortalException {
		return getService().loadGetGuestUser(companyId);
	}

	/**
	 * Returns an ordered range of all the users who match the keywords and
	 * status, without using the indexer. It is preferable to use the indexed
	 * version {@link #search(long, String, int, LinkedHashMap, int, int, Sort)}
	 * instead of this method wherever possible for performance reasons.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param companyId the primary key of the user's company
	 * @param keywords the keywords (space separated), which may occur in the
	 user's first name, middle name, last name, screen name, or email
	 address
	 * @param status the workflow status
	 * @param params the finder parameters (optionally <code>null</code>). For
	 more information see {@link
	 com.liferay.portal.kernel.service.persistence.UserFinder}.
	 * @param start the lower bound of the range of users
	 * @param end the upper bound of the range of users (not inclusive)
	 * @param orderByComparator the comparator to order the users by
	 (optionally <code>null</code>)
	 * @return the matching users
	 * @see com.liferay.portal.kernel.service.persistence.UserFinder
	 */
	public static List<User> search(
		long companyId, String keywords, int status,
		java.util.LinkedHashMap<String, Object> params, int start, int end,
		OrderByComparator<User> orderByComparator) {

		return getService().search(
			companyId, keywords, status, params, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the users who match the keywords and
	 * status, using the indexer. It is preferable to use this method instead of
	 * the non-indexed version whenever possible for performance reasons.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param companyId the primary key of the user's company
	 * @param keywords the keywords (space separated), which may occur in the
	 user's first name, middle name, last name, screen name, or email
	 address
	 * @param status the workflow status
	 * @param params the indexer parameters (optionally <code>null</code>).
	 * @param start the lower bound of the range of users
	 * @param end the upper bound of the range of users (not inclusive)
	 * @param sort the field and direction to sort by (optionally
	 <code>null</code>)
	 * @return the matching users
	 */
	public static com.liferay.portal.kernel.search.Hits search(
		long companyId, String keywords, int status,
		java.util.LinkedHashMap<String, Object> params, int start, int end,
		com.liferay.portal.kernel.search.Sort sort) {

		return getService().search(
			companyId, keywords, status, params, start, end, sort);
	}

	public static com.liferay.portal.kernel.search.Hits search(
		long companyId, String keywords, int status,
		java.util.LinkedHashMap<String, Object> params, int start, int end,
		com.liferay.portal.kernel.search.Sort[] sorts) {

		return getService().search(
			companyId, keywords, status, params, start, end, sorts);
	}

	/**
	 * Returns an ordered range of all the users with the status, and whose
	 * first name, middle name, last name, screen name, and email address match
	 * the keywords specified for them, without using the indexer. It is
	 * preferable to use the indexed version {@link #search(long, String,
	 * String, String, String, String, int, LinkedHashMap, boolean, int, int,
	 * Sort)} instead of this method wherever possible for performance reasons.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param companyId the primary key of the user's company
	 * @param firstName the first name keywords (space separated)
	 * @param middleName the middle name keywords
	 * @param lastName the last name keywords
	 * @param screenName the screen name keywords
	 * @param emailAddress the email address keywords
	 * @param status the workflow status
	 * @param params the finder parameters (optionally <code>null</code>). For
	 more information see {@link
	 com.liferay.portal.kernel.service.persistence.UserFinder}.
	 * @param andSearch whether every field must match its keywords, or just
	 one field. For example, &quot;users with the first name 'bob' and
	 last name 'smith'&quot; vs &quot;users with the first name 'bob'
	 or the last name 'smith'&quot;.
	 * @param start the lower bound of the range of users
	 * @param end the upper bound of the range of users (not inclusive)
	 * @param orderByComparator the comparator to order the users by
	 (optionally <code>null</code>)
	 * @return the matching users
	 * @see com.liferay.portal.kernel.service.persistence.UserFinder
	 */
	public static List<User> search(
		long companyId, String firstName, String middleName, String lastName,
		String screenName, String emailAddress, int status,
		java.util.LinkedHashMap<String, Object> params, boolean andSearch,
		int start, int end, OrderByComparator<User> orderByComparator) {

		return getService().search(
			companyId, firstName, middleName, lastName, screenName,
			emailAddress, status, params, andSearch, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the users with the status, and whose
	 * first name, middle name, last name, screen name, and email address match
	 * the keywords specified for them, using the indexer. It is preferable to
	 * use this method instead of the non-indexed version whenever possible for
	 * performance reasons.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param companyId the primary key of the user's company
	 * @param firstName the first name keywords (space separated)
	 * @param middleName the middle name keywords
	 * @param lastName the last name keywords
	 * @param screenName the screen name keywords
	 * @param emailAddress the email address keywords
	 * @param status the workflow status
	 * @param params the indexer parameters (optionally <code>null</code>).
	 * @param andSearch whether every field must match its keywords, or just
	 one field. For example, &quot;users with the first name 'bob' and
	 last name 'smith'&quot; vs &quot;users with the first name 'bob'
	 or the last name 'smith'&quot;.
	 * @param start the lower bound of the range of users
	 * @param end the upper bound of the range of users (not inclusive)
	 * @param sort the field and direction to sort by (optionally
	 <code>null</code>)
	 * @return the matching users
	 */
	public static com.liferay.portal.kernel.search.Hits search(
		long companyId, String firstName, String middleName, String lastName,
		String screenName, String emailAddress, int status,
		java.util.LinkedHashMap<String, Object> params, boolean andSearch,
		int start, int end, com.liferay.portal.kernel.search.Sort sort) {

		return getService().search(
			companyId, firstName, middleName, lastName, screenName,
			emailAddress, status, params, andSearch, start, end, sort);
	}

	public static com.liferay.portal.kernel.search.Hits search(
		long companyId, String firstName, String middleName, String lastName,
		String screenName, String emailAddress, int status,
		java.util.LinkedHashMap<String, Object> params, boolean andSearch,
		int start, int end, com.liferay.portal.kernel.search.Sort[] sorts) {

		return getService().search(
			companyId, firstName, middleName, lastName, screenName,
			emailAddress, status, params, andSearch, start, end, sorts);
	}

	public static List<User> searchBySocial(
			long userId, int[] socialRelationTypes, String keywords, int start,
			int end)
		throws PortalException {

		return getService().searchBySocial(
			userId, socialRelationTypes, keywords, start, end);
	}

	public static List<User> searchBySocial(
		long companyId, long[] groupIds, long[] userGroupIds, String keywords,
		int start, int end) {

		return getService().searchBySocial(
			companyId, groupIds, userGroupIds, keywords, start, end);
	}

	public static List<User> searchBySocial(
		long companyId, long[] groupIds, long[] userGroupIds, String keywords,
		int start, int end, OrderByComparator<User> orderByComparator) {

		return getService().searchBySocial(
			companyId, groupIds, userGroupIds, keywords, start, end,
			orderByComparator);
	}

	public static List<User> searchBySocial(
			long[] groupIds, long userId, int[] socialRelationTypes,
			String keywords, int start, int end)
		throws PortalException {

		return getService().searchBySocial(
			groupIds, userId, socialRelationTypes, keywords, start, end);
	}

	/**
	 * Returns the number of users who match the keywords and status.
	 *
	 * @param companyId the primary key of the user's company
	 * @param keywords the keywords (space separated), which may occur in the
	 user's first name, middle name, last name, screen name, or email
	 address
	 * @param status the workflow status
	 * @param params the finder parameters (optionally <code>null</code>). For
	 more information see {@link
	 com.liferay.portal.kernel.service.persistence.UserFinder}.
	 * @return the number matching users
	 */
	public static int searchCount(
		long companyId, String keywords, int status,
		java.util.LinkedHashMap<String, Object> params) {

		return getService().searchCount(companyId, keywords, status, params);
	}

	/**
	 * Returns the number of users with the status, and whose first name, middle
	 * name, last name, screen name, and email address match the keywords
	 * specified for them.
	 *
	 * @param companyId the primary key of the user's company
	 * @param firstName the first name keywords (space separated)
	 * @param middleName the middle name keywords
	 * @param lastName the last name keywords
	 * @param screenName the screen name keywords
	 * @param emailAddress the email address keywords
	 * @param status the workflow status
	 * @param params the finder parameters (optionally <code>null</code>). For
	 more information see {@link
	 com.liferay.portal.kernel.service.persistence.UserFinder}.
	 * @param andSearch whether every field must match its keywords, or just
	 one field. For example, &quot;users with the first name 'bob' and
	 last name 'smith'&quot; vs &quot;users with the first name 'bob'
	 or the last name 'smith'&quot;.
	 * @return the number of matching users
	 */
	public static int searchCount(
		long companyId, String firstName, String middleName, String lastName,
		String screenName, String emailAddress, int status,
		java.util.LinkedHashMap<String, Object> params, boolean andSearch) {

		return getService().searchCount(
			companyId, firstName, middleName, lastName, screenName,
			emailAddress, status, params, andSearch);
	}

	public static int searchCountBySocial(
		long companyId, long[] groupIds, long[] userGroupIds, String keywords) {

		return getService().searchCountBySocial(
			companyId, groupIds, userGroupIds, keywords);
	}

	public static Map<Long, Integer> searchCounts(
		long companyId, int status, long[] groupIds) {

		return getService().searchCounts(companyId, status, groupIds);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult<User>
			searchUsers(
				long companyId, String keywords, int status,
				java.util.LinkedHashMap<String, Object> params, int start,
				int end, com.liferay.portal.kernel.search.Sort sort)
		throws PortalException {

		return getService().searchUsers(
			companyId, keywords, status, params, start, end, sort);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult<User>
			searchUsers(
				long companyId, String keywords, int status,
				java.util.LinkedHashMap<String, Object> params, int start,
				int end, com.liferay.portal.kernel.search.Sort[] sorts)
		throws PortalException {

		return getService().searchUsers(
			companyId, keywords, status, params, start, end, sorts);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult<User>
			searchUsers(
				long companyId, String firstName, String middleName,
				String lastName, String screenName, String emailAddress,
				int status, java.util.LinkedHashMap<String, Object> params,
				boolean andSearch, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
		throws PortalException {

		return getService().searchUsers(
			companyId, firstName, middleName, lastName, screenName,
			emailAddress, status, params, andSearch, start, end, sort);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult<User>
			searchUsers(
				long companyId, String firstName, String middleName,
				String lastName, String screenName, String emailAddress,
				int status, java.util.LinkedHashMap<String, Object> params,
				boolean andSearch, int start, int end,
				com.liferay.portal.kernel.search.Sort[] sorts)
		throws PortalException {

		return getService().searchUsers(
			companyId, firstName, middleName, lastName, screenName,
			emailAddress, status, params, andSearch, start, end, sorts);
	}

	/**
	 * Sends an email address verification to the user.
	 *
	 * @param user the verification email recipient
	 * @param emailAddress the recipient's email address
	 * @param serviceContext the service context to be applied. Must set the
	 portal URL, main path, primary key of the layout, remote address,
	 remote host, and agent for the user.
	 */
	public static void sendEmailAddressVerification(
			User user, String emailAddress, ServiceContext serviceContext)
		throws PortalException {

		getService().sendEmailAddressVerification(
			user, emailAddress, serviceContext);
	}

	public static boolean sendEmailUserCreationAttempt(
			long companyId, String emailAddress, String fromName,
			String fromAddress, String subject, String body,
			ServiceContext serviceContext)
		throws PortalException {

		return getService().sendEmailUserCreationAttempt(
			companyId, emailAddress, fromName, fromAddress, subject, body,
			serviceContext);
	}

	public static boolean sendPassword(
			long companyId, String emailAddress, String fromName,
			String fromAddress, String subject, String body,
			ServiceContext serviceContext)
		throws PortalException {

		return getService().sendPassword(
			companyId, emailAddress, fromName, fromAddress, subject, body,
			serviceContext);
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

	public static boolean sendPasswordLockout(
			long companyId, String emailAddress, String fromName,
			String fromAddress, String subject, String body,
			ServiceContext serviceContext)
		throws PortalException {

		return getService().sendPasswordLockout(
			companyId, emailAddress, fromName, fromAddress, subject, body,
			serviceContext);
	}

	public static void setGroupUsers(long groupId, long[] userIds) {
		getService().setGroupUsers(groupId, userIds);
	}

	public static void setOrganizationUsers(
		long organizationId, long[] userIds) {

		getService().setOrganizationUsers(organizationId, userIds);
	}

	/**
	 * @throws PortalException
	 */
	public static void setRoleUsers(long roleId, long[] userIds)
		throws PortalException {

		getService().setRoleUsers(roleId, userIds);
	}

	public static void setTeamUsers(long teamId, long[] userIds) {
		getService().setTeamUsers(teamId, userIds);
	}

	/**
	 * @throws PortalException
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
		long passwordPolicyId, long[] userIds) {

		getService().unsetPasswordPolicyUsers(passwordPolicyId, userIds);
	}

	/**
	 * Removes the users from the role.
	 *
	 * @param roleId the primary key of the role
	 * @param users the users
	 */
	public static void unsetRoleUsers(long roleId, List<User> users)
		throws PortalException {

		getService().unsetRoleUsers(roleId, users);
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
	 * Updates whether the user has agreed to the terms of use.
	 *
	 * @param userId the primary key of the user
	 * @param agreedToTermsOfUse whether the user has agreet to the terms of
	 use
	 * @return the user
	 */
	public static User updateAgreedToTermsOfUse(
			long userId, boolean agreedToTermsOfUse)
		throws PortalException {

		return getService().updateAgreedToTermsOfUse(
			userId, agreedToTermsOfUse);
	}

	/**
	 * Updates the user's asset with the new asset categories and tag names,
	 * removing and adding asset categories and tag names as necessary.
	 *
	 * @param userId the primary key of the user
	 * @param user ID the primary key of the user
	 * @param assetCategoryIds the primary key's of the new asset categories
	 * @param assetTagNames the new asset tag names
	 */
	public static void updateAsset(
			long userId, User user, long[] assetCategoryIds,
			String[] assetTagNames)
		throws PortalException {

		getService().updateAsset(userId, user, assetCategoryIds, assetTagNames);
	}

	/**
	 * Updates the user's creation date.
	 *
	 * @param userId the primary key of the user
	 * @param createDate the new creation date
	 * @return the user
	 */
	public static User updateCreateDate(long userId, java.util.Date createDate)
		throws PortalException {

		return getService().updateCreateDate(userId, createDate);
	}

	/**
	 * Updates the user's email address.
	 *
	 * @param userId the primary key of the user
	 * @param password the user's password
	 * @param emailAddress1 the user's new email address
	 * @param emailAddress2 the user's new email address confirmation
	 * @return the user
	 */
	public static User updateEmailAddress(
			long userId, String password, String emailAddress1,
			String emailAddress2)
		throws PortalException {

		return getService().updateEmailAddress(
			userId, password, emailAddress1, emailAddress2);
	}

	/**
	 * Updates the user's email address or sends verification email.
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

	/**
	 * Updates whether the user has verified email address.
	 *
	 * @param userId the primary key of the user
	 * @param emailAddressVerified whether the user has verified email address
	 * @return the user
	 */
	public static User updateEmailAddressVerified(
			long userId, boolean emailAddressVerified)
		throws PortalException {

		return getService().updateEmailAddressVerified(
			userId, emailAddressVerified);
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
	 * Sets the groups the user is in, removing and adding groups as necessary.
	 *
	 * @param userId the primary key of the user
	 * @param newGroupIds the primary keys of the groups
	 * @param serviceContext the service context to be applied (optionally
	 <code>null</code>)
	 */
	public static void updateGroups(
			long userId, long[] newGroupIds, ServiceContext serviceContext)
		throws PortalException {

		getService().updateGroups(userId, newGroupIds, serviceContext);
	}

	/**
	 * Updates a user account that was automatically created when a guest user
	 * participated in an action (e.g. posting a comment) and only provided his
	 * name and email address.
	 *
	 * @param creatorUserId the primary key of the creator
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
	 <code>null</code>). Can set expando bridge attributes for the
	 user.
	 * @return the user
	 */
	public static User updateIncompleteUser(
			long creatorUserId, long companyId, boolean autoPassword,
			String password1, String password2, boolean autoScreenName,
			String screenName, String emailAddress, java.util.Locale locale,
			String firstName, String middleName, String lastName,
			long prefixListTypeId, long suffixListTypeId, boolean male,
			int birthdayMonth, int birthdayDay, int birthdayYear,
			String jobTitle, boolean updateUserInformation, boolean sendEmail,
			ServiceContext serviceContext)
		throws PortalException {

		return getService().updateIncompleteUser(
			creatorUserId, companyId, autoPassword, password1, password2,
			autoScreenName, screenName, emailAddress, locale, firstName,
			middleName, lastName, prefixListTypeId, suffixListTypeId, male,
			birthdayMonth, birthdayDay, birthdayYear, jobTitle,
			updateUserInformation, sendEmail, serviceContext);
	}

	/**
	 * Updates the user's job title.
	 *
	 * @param userId the primary key of the user
	 * @param jobTitle the user's job title
	 * @return the user
	 */
	public static User updateJobTitle(long userId, String jobTitle)
		throws PortalException {

		return getService().updateJobTitle(userId, jobTitle);
	}

	public static User updateLanguageId(long userId, String languageId)
		throws PortalException {

		return getService().updateLanguageId(userId, languageId);
	}

	/**
	 * Updates the user's last login with the current time and the IP address.
	 *
	 * @param userId the primary key of the user
	 * @param loginIP the IP address the user logged in from
	 * @return the user
	 */
	public static User updateLastLogin(long userId, String loginIP)
		throws PortalException {

		return getService().updateLastLogin(userId, loginIP);
	}

	public static User updateLastLogin(User user, String loginIP)
		throws PortalException {

		return getService().updateLastLogin(user, loginIP);
	}

	/**
	 * Updates whether the user is locked out from logging in.
	 *
	 * @param user the user
	 * @param lockout whether the user is locked out
	 * @return the user
	 */
	public static User updateLockout(User user, boolean lockout)
		throws PortalException {

		return getService().updateLockout(user, lockout);
	}

	/**
	 * Updates whether the user is locked out from logging in.
	 *
	 * @param companyId the primary key of the user's company
	 * @param emailAddress the user's email address
	 * @param lockout whether the user is locked out
	 * @return the user
	 */
	public static User updateLockoutByEmailAddress(
			long companyId, String emailAddress, boolean lockout)
		throws PortalException {

		return getService().updateLockoutByEmailAddress(
			companyId, emailAddress, lockout);
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
	 * Updates whether the user is locked out from logging in.
	 *
	 * @param companyId the primary key of the user's company
	 * @param screenName the user's screen name
	 * @param lockout whether the user is locked out
	 * @return the user
	 */
	public static User updateLockoutByScreenName(
			long companyId, String screenName, boolean lockout)
		throws PortalException {

		return getService().updateLockoutByScreenName(
			companyId, screenName, lockout);
	}

	/**
	 * Updates the user's modified date.
	 *
	 * @param userId the primary key of the user
	 * @param modifiedDate the new modified date
	 * @return the user
	 */
	public static User updateModifiedDate(
			long userId, java.util.Date modifiedDate)
		throws PortalException {

		return getService().updateModifiedDate(userId, modifiedDate);
	}

	/**
	 * Sets the organizations that the user is in, removing and adding
	 * organizations as necessary.
	 *
	 * @param userId the primary key of the user
	 * @param newOrganizationIds the primary keys of the organizations
	 * @param serviceContext the service context to be applied. Must set whether
	 user indexing is enabled.
	 */
	public static void updateOrganizations(
			long userId, long[] newOrganizationIds,
			ServiceContext serviceContext)
		throws PortalException {

		getService().updateOrganizations(
			userId, newOrganizationIds, serviceContext);
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
	 * Updates the user's password, optionally with tracking and validation of
	 * the change.
	 *
	 * @param userId the primary key of the user
	 * @param password1 the user's new password
	 * @param password2 the user's new password confirmation
	 * @param passwordReset whether the user should be asked to reset their
	 password the next time they login
	 * @param silentUpdate whether the password should be updated without being
	 tracked, or validated. Primarily used for password imports.
	 * @return the user
	 */
	public static User updatePassword(
			long userId, String password1, String password2,
			boolean passwordReset, boolean silentUpdate)
		throws PortalException {

		return getService().updatePassword(
			userId, password1, password2, passwordReset, silentUpdate);
	}

	/**
	 * Updates the user's password with manually input information. This method
	 * should only be used when performing maintenance.
	 *
	 * @param userId the primary key of the user
	 * @param password the user's new password
	 * @param passwordEncrypted the user's new encrypted password
	 * @param passwordReset whether the user should be asked to reset their
	 password the next time they login
	 * @param passwordModifiedDate the new password modified date
	 * @return the user
	 */
	public static User updatePasswordManually(
			long userId, String password, boolean passwordEncrypted,
			boolean passwordReset, java.util.Date passwordModifiedDate)
		throws PortalException {

		return getService().updatePasswordManually(
			userId, password, passwordEncrypted, passwordReset,
			passwordModifiedDate);
	}

	/**
	 * Updates whether the user should be asked to reset their password the next
	 * time they login.
	 *
	 * @param userId the primary key of the user
	 * @param passwordReset whether the user should be asked to reset their
	 password the next time they login
	 * @return the user
	 */
	public static User updatePasswordReset(long userId, boolean passwordReset)
		throws PortalException {

		return getService().updatePasswordReset(userId, passwordReset);
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
			long[] userGroupIds, ServiceContext serviceContext)
		throws PortalException {

		return getService().updateUser(
			userId, oldPassword, newPassword1, newPassword2, passwordReset,
			reminderQueryQuestion, reminderQueryAnswer, screenName,
			emailAddress, hasPortrait, portraitBytes, languageId, timeZoneId,
			greeting, comments, firstName, middleName, lastName,
			prefixListTypeId, suffixListTypeId, male, birthdayMonth,
			birthdayDay, birthdayYear, smsSn, facebookSn, jabberSn, skypeSn,
			twitterSn, jobTitle, groupIds, organizationIds, roleIds,
			userGroupRoles, userGroupIds, serviceContext);
	}

	/**
	 * Updates the user in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect UserLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param user the user
	 * @return the user that was updated
	 */
	public static User updateUser(User user) {
		return getService().updateUser(user);
	}

	public static void validateMaxUsers(long companyId) throws PortalException {
		getService().validateMaxUsers(companyId);
	}

	/**
	 * Verifies the email address of the ticket.
	 *
	 * @param ticketKey the ticket key
	 */
	public static void verifyEmailAddress(String ticketKey)
		throws PortalException {

		getService().verifyEmailAddress(ticketKey);
	}

	public static UserLocalService getService() {
		return _service;
	}

	public static void setService(UserLocalService service) {
		_service = service;
	}

	private static volatile UserLocalService _service;

}