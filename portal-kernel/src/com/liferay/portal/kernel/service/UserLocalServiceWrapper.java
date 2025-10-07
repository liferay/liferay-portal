/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link UserLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see UserLocalService
 * @generated
 */
public class UserLocalServiceWrapper
	implements ServiceWrapper<UserLocalService>, UserLocalService {

	public UserLocalServiceWrapper() {
		this(null);
	}

	public UserLocalServiceWrapper(UserLocalService userLocalService) {
		_userLocalService = userLocalService;
	}

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
	@Override
	public User addDefaultAdminUser(
			long companyId, String password, String screenName,
			String emailAddress, java.util.Locale locale, String firstName,
			String middleName, String lastName)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.addDefaultAdminUser(
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
	@Override
	public boolean addDefaultGroups(long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.addDefaultGroups(userId);
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
	@Override
	public boolean addDefaultRoles(long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.addDefaultRoles(userId);
	}

	@Override
	public User addDefaultServiceAccountUser(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.addDefaultServiceAccountUser(companyId);
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
	@Override
	public boolean addDefaultUserGroups(long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.addDefaultUserGroups(userId);
	}

	@Override
	public boolean addGroupUser(long groupId, long userId) {
		return _userLocalService.addGroupUser(groupId, userId);
	}

	@Override
	public boolean addGroupUser(long groupId, User user) {
		return _userLocalService.addGroupUser(groupId, user);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public boolean addGroupUsers(long groupId, java.util.List<User> users)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.addGroupUsers(groupId, users);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public boolean addGroupUsers(long groupId, long[] userIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.addGroupUsers(groupId, userIds);
	}

	@Override
	public boolean addOrganizationUser(long organizationId, long userId) {
		return _userLocalService.addOrganizationUser(organizationId, userId);
	}

	@Override
	public boolean addOrganizationUser(long organizationId, User user) {
		return _userLocalService.addOrganizationUser(organizationId, user);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public boolean addOrganizationUsers(
			long organizationId, java.util.List<User> users)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.addOrganizationUsers(organizationId, users);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public boolean addOrganizationUsers(long organizationId, long[] userIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.addOrganizationUsers(organizationId, userIds);
	}

	@Override
	public User addOrUpdateUser(
			String externalReferenceCode, long creatorUserId, long companyId,
			boolean autoPassword, String password1, String password2,
			boolean autoScreenName, String screenName, String emailAddress,
			java.util.Locale locale, String firstName, String middleName,
			String lastName, long prefixListTypeId, long suffixListTypeId,
			boolean male, int birthdayMonth, int birthdayDay, int birthdayYear,
			String jobTitle, boolean sendEmail, ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.addOrUpdateUser(
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
	@Override
	public void addPasswordPolicyUsers(long passwordPolicyId, long[] userIds) {
		_userLocalService.addPasswordPolicyUsers(passwordPolicyId, userIds);
	}

	@Override
	public boolean addRoleUser(long roleId, long userId) {
		return _userLocalService.addRoleUser(roleId, userId);
	}

	@Override
	public boolean addRoleUser(long roleId, User user) {
		return _userLocalService.addRoleUser(roleId, user);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public boolean addRoleUsers(long roleId, java.util.List<User> users)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.addRoleUsers(roleId, users);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public boolean addRoleUsers(long roleId, long[] userIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.addRoleUsers(roleId, userIds);
	}

	@Override
	public boolean addTeamUser(long teamId, long userId) {
		return _userLocalService.addTeamUser(teamId, userId);
	}

	@Override
	public boolean addTeamUser(long teamId, User user) {
		return _userLocalService.addTeamUser(teamId, user);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public boolean addTeamUsers(long teamId, java.util.List<User> users)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.addTeamUsers(teamId, users);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public boolean addTeamUsers(long teamId, long[] userIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.addTeamUsers(teamId, userIds);
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
	@Override
	public User addUser(
			long creatorUserId, long companyId, boolean autoPassword,
			String password1, String password2, boolean autoScreenName,
			String screenName, String emailAddress, java.util.Locale locale,
			String firstName, String middleName, String lastName,
			long prefixListTypeId, long suffixListTypeId, boolean male,
			int birthdayMonth, int birthdayDay, int birthdayYear,
			String jobTitle, int type, long[] groupIds, long[] organizationIds,
			long[] roleIds, long[] userGroupIds, boolean sendEmail,
			ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.addUser(
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
	@Override
	public User addUser(User user) {
		return _userLocalService.addUser(user);
	}

	@Override
	public boolean addUserGroupUser(long userGroupId, long userId) {
		return _userLocalService.addUserGroupUser(userGroupId, userId);
	}

	@Override
	public boolean addUserGroupUser(long userGroupId, User user) {
		return _userLocalService.addUserGroupUser(userGroupId, user);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public boolean addUserGroupUsers(
			long userGroupId, java.util.List<User> users)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.addUserGroupUsers(userGroupId, users);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public boolean addUserGroupUsers(long userGroupId, long[] userIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.addUserGroupUsers(userGroupId, userIds);
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
	@Override
	public User addUserWithWorkflow(
			long creatorUserId, long companyId, boolean autoPassword,
			String password1, String password2, boolean autoScreenName,
			String screenName, String emailAddress, java.util.Locale locale,
			String firstName, String middleName, String lastName,
			long prefixListTypeId, long suffixListTypeId, boolean male,
			int birthdayMonth, int birthdayDay, int birthdayYear,
			String jobTitle, int type, long[] groupIds, long[] organizationIds,
			long[] roleIds, long[] userGroupIds, boolean sendEmail,
			ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.addUserWithWorkflow(
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
	@Override
	public int authenticateByEmailAddress(
			long companyId, String emailAddress, String password,
			java.util.Map<String, String[]> headerMap,
			java.util.Map<String, String[]> parameterMap,
			java.util.Map<String, Object> resultsMap)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.authenticateByEmailAddress(
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
	@Override
	public int authenticateByScreenName(
			long companyId, String screenName, String password,
			java.util.Map<String, String[]> headerMap,
			java.util.Map<String, String[]> parameterMap,
			java.util.Map<String, Object> resultsMap)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.authenticateByScreenName(
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
	@Override
	public int authenticateByUserId(
			long companyId, long userId, String password,
			java.util.Map<String, String[]> headerMap,
			java.util.Map<String, String[]> parameterMap,
			java.util.Map<String, Object> resultsMap)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.authenticateByUserId(
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
	@Override
	public long authenticateForBasic(
			long companyId, String authType, String login, String password)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.authenticateForBasic(
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
	@Override
	public long authenticateForDigest(
			long companyId, String userName, String realm, String nonce,
			String method, String uri, String response)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.authenticateForDigest(
			companyId, userName, realm, nonce, method, uri, response);
	}

	/**
	 * Checks if the user is currently locked out based on the password policy,
	 * and performs maintenance on the user's lockout and failed login data.
	 *
	 * @param user the user
	 */
	@Override
	public void checkLockout(User user)
		throws com.liferay.portal.kernel.exception.PortalException {

		_userLocalService.checkLockout(user);
	}

	/**
	 * Adds a failed login attempt to the user and updates the user's last
	 * failed login date.
	 *
	 * @param user the user
	 */
	@Override
	public void checkLoginFailure(User user) {
		_userLocalService.checkLoginFailure(user);
	}

	/**
	 * Adds a failed login attempt to the user with the email address and
	 * updates the user's last failed login date.
	 *
	 * @param companyId the primary key of the user's company
	 * @param emailAddress the user's email address
	 */
	@Override
	public void checkLoginFailureByEmailAddress(
			long companyId, String emailAddress)
		throws com.liferay.portal.kernel.exception.PortalException {

		_userLocalService.checkLoginFailureByEmailAddress(
			companyId, emailAddress);
	}

	/**
	 * Adds a failed login attempt to the user and updates the user's last
	 * failed login date.
	 *
	 * @param userId the primary key of the user
	 */
	@Override
	public void checkLoginFailureById(long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_userLocalService.checkLoginFailureById(userId);
	}

	/**
	 * Adds a failed login attempt to the user with the screen name and updates
	 * the user's last failed login date.
	 *
	 * @param companyId the primary key of the user's company
	 * @param screenName the user's screen name
	 */
	@Override
	public void checkLoginFailureByScreenName(long companyId, String screenName)
		throws com.liferay.portal.kernel.exception.PortalException {

		_userLocalService.checkLoginFailureByScreenName(companyId, screenName);
	}

	/**
	 * Checks if the user's password is expired based on the password policy,
	 * and performs maintenance on the user's grace login and password reset
	 * data.
	 *
	 * @param user the user
	 */
	@Override
	public void checkPasswordExpired(User user)
		throws com.liferay.portal.kernel.exception.PortalException {

		_userLocalService.checkPasswordExpired(user);
	}

	@Override
	public void clearGroupUsers(long groupId) {
		_userLocalService.clearGroupUsers(groupId);
	}

	@Override
	public void clearOrganizationUsers(long organizationId) {
		_userLocalService.clearOrganizationUsers(organizationId);
	}

	@Override
	public void clearRoleUsers(long roleId) {
		_userLocalService.clearRoleUsers(roleId);
	}

	@Override
	public void clearTeamUsers(long teamId) {
		_userLocalService.clearTeamUsers(teamId);
	}

	@Override
	public void clearUserGroupUsers(long userGroupId) {
		_userLocalService.clearUserGroupUsers(userGroupId);
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
	@Override
	public void completeUserRegistration(
			User user, ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		_userLocalService.completeUserRegistration(user, serviceContext);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Creates a new user with the primary key. Does not add the user to the database.
	 *
	 * @param userId the primary key for the new user
	 * @return the new user
	 */
	@Override
	public User createUser(long userId) {
		return _userLocalService.createUser(userId);
	}

	@Override
	public void deleteGroupUser(long groupId, long userId) {
		_userLocalService.deleteGroupUser(groupId, userId);
	}

	@Override
	public void deleteGroupUser(long groupId, User user) {
		_userLocalService.deleteGroupUser(groupId, user);
	}

	@Override
	public void deleteGroupUsers(long groupId, java.util.List<User> users) {
		_userLocalService.deleteGroupUsers(groupId, users);
	}

	@Override
	public void deleteGroupUsers(long groupId, long[] userIds) {
		_userLocalService.deleteGroupUsers(groupId, userIds);
	}

	@Override
	public void deleteOrganizationUser(long organizationId, long userId) {
		_userLocalService.deleteOrganizationUser(organizationId, userId);
	}

	@Override
	public void deleteOrganizationUser(long organizationId, User user) {
		_userLocalService.deleteOrganizationUser(organizationId, user);
	}

	@Override
	public void deleteOrganizationUsers(
		long organizationId, java.util.List<User> users) {

		_userLocalService.deleteOrganizationUsers(organizationId, users);
	}

	@Override
	public void deleteOrganizationUsers(long organizationId, long[] userIds) {
		_userLocalService.deleteOrganizationUsers(organizationId, userIds);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.deletePersistedModel(persistedModel);
	}

	/**
	 * Deletes the user's portrait image.
	 *
	 * @param userId the primary key of the user
	 */
	@Override
	public void deletePortrait(long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_userLocalService.deletePortrait(userId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public void deleteRoleUser(long roleId, long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_userLocalService.deleteRoleUser(roleId, userId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public void deleteRoleUser(long roleId, User user)
		throws com.liferay.portal.kernel.exception.PortalException {

		_userLocalService.deleteRoleUser(roleId, user);
	}

	@Override
	public void deleteRoleUsers(long roleId, java.util.List<User> users) {
		_userLocalService.deleteRoleUsers(roleId, users);
	}

	@Override
	public void deleteRoleUsers(long roleId, long[] userIds) {
		_userLocalService.deleteRoleUsers(roleId, userIds);
	}

	@Override
	public void deleteTeamUser(long teamId, long userId) {
		_userLocalService.deleteTeamUser(teamId, userId);
	}

	@Override
	public void deleteTeamUser(long teamId, User user) {
		_userLocalService.deleteTeamUser(teamId, user);
	}

	@Override
	public void deleteTeamUsers(long teamId, java.util.List<User> users) {
		_userLocalService.deleteTeamUsers(teamId, users);
	}

	@Override
	public void deleteTeamUsers(long teamId, long[] userIds) {
		_userLocalService.deleteTeamUsers(teamId, userIds);
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
	@Override
	public User deleteUser(long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.deleteUser(userId);
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
	@Override
	public User deleteUser(User user)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.deleteUser(user);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public void deleteUserGroupUser(long userGroupId, long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_userLocalService.deleteUserGroupUser(userGroupId, userId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public void deleteUserGroupUser(long userGroupId, User user)
		throws com.liferay.portal.kernel.exception.PortalException {

		_userLocalService.deleteUserGroupUser(userGroupId, user);
	}

	@Override
	public void deleteUserGroupUsers(
		long userGroupId, java.util.List<User> users) {

		_userLocalService.deleteUserGroupUsers(userGroupId, users);
	}

	@Override
	public void deleteUserGroupUsers(long userGroupId, long[] userIds) {
		_userLocalService.deleteUserGroupUsers(userGroupId, userIds);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _userLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _userLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _userLocalService.dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _userLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _userLocalService.dynamicQuery(dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _userLocalService.dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _userLocalService.dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return _userLocalService.dynamicQueryCount(dynamicQuery, projection);
	}

	/**
	 * Encrypts the primary key of the user. Used when encrypting the user's
	 * credentials for storage in an automatic login cookie.
	 *
	 * @param name the primary key of the user
	 * @return the user's encrypted primary key
	 */
	@Override
	public String encryptUserId(String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.encryptUserId(name);
	}

	/**
	 * Returns the guest user for the company.
	 *
	 * @param companyId the primary key of the company
	 * @return the guest user for the company, or <code>null</code> if a user
	 with the company key could not be found
	 */
	@Override
	public User fetchGuestUser(long companyId) {
		return _userLocalService.fetchGuestUser(companyId);
	}

	@Override
	public User fetchUser(long userId) {
		return _userLocalService.fetchUser(userId);
	}

	/**
	 * Returns the user with the contact ID.
	 *
	 * @param contactId the user's contact ID
	 * @return the user with the contact ID, or <code>null</code> if a user with
	 the contact ID could not be found
	 */
	@Override
	public User fetchUserByContactId(long contactId) {
		return _userLocalService.fetchUserByContactId(contactId);
	}

	/**
	 * Returns the user with the email address.
	 *
	 * @param companyId the primary key of the user's company
	 * @param emailAddress the user's email address
	 * @return the user with the email address, or <code>null</code> if a user
	 with the email address could not be found
	 */
	@Override
	public User fetchUserByEmailAddress(long companyId, String emailAddress) {
		return _userLocalService.fetchUserByEmailAddress(
			companyId, emailAddress);
	}

	@Override
	public User fetchUserByExternalReferenceCode(
		String externalReferenceCode, long companyId) {

		return _userLocalService.fetchUserByExternalReferenceCode(
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
	@Override
	public User fetchUserByFacebookId(long companyId, long facebookId) {
		return _userLocalService.fetchUserByFacebookId(companyId, facebookId);
	}

	/**
	 * Returns the user with the primary key.
	 *
	 * @param userId the primary key of the user
	 * @return the user with the primary key, or <code>null</code> if a user
	 with the primary key could not be found
	 */
	@Override
	public User fetchUserById(long userId) {
		return _userLocalService.fetchUserById(userId);
	}

	/**
	 * Returns the user with the portrait ID.
	 *
	 * @param portraitId the user's portrait ID
	 * @return the user with the portrait ID, or <code>null</code> if a user
	 with the portrait ID could not be found
	 */
	@Override
	public User fetchUserByPortraitId(long portraitId) {
		return _userLocalService.fetchUserByPortraitId(portraitId);
	}

	/**
	 * Returns the user with the screen name.
	 *
	 * @param companyId the primary key of the user's company
	 * @param screenName the user's screen name
	 * @return the user with the screen name, or <code>null</code> if a user
	 with the screen name could not be found
	 */
	@Override
	public User fetchUserByScreenName(long companyId, String screenName) {
		return _userLocalService.fetchUserByScreenName(companyId, screenName);
	}

	/**
	 * Returns the user with the matching UUID and company.
	 *
	 * @param uuid the user's UUID
	 * @param companyId the primary key of the company
	 * @return the matching user, or <code>null</code> if a matching user could not be found
	 */
	@Override
	public User fetchUserByUuidAndCompanyId(String uuid, long companyId) {
		return _userLocalService.fetchUserByUuidAndCompanyId(uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _userLocalService.getActionableDynamicQuery();
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
	@Override
	public java.util.List<User> getCompanyUsers(
		long companyId, int start, int end) {

		return _userLocalService.getCompanyUsers(companyId, start, end);
	}

	/**
	 * Returns the number of users belonging to the company.
	 *
	 * @param companyId the primary key of the company
	 * @return the number of users belonging to the company
	 */
	@Override
	public int getCompanyUsersCount(long companyId) {
		return _userLocalService.getCompanyUsersCount(companyId);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 #getGuestUser(long)}
	 */
	@Deprecated
	@Override
	public User getDefaultUser(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.getDefaultUser(companyId);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 #getGuestUserId(long)}
	 */
	@Deprecated
	@Override
	public long getDefaultUserId(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.getDefaultUserId(companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _userLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	/**
	 * Returns the groupIds of the groups associated with the user.
	 *
	 * @param userId the userId of the user
	 * @return long[] the groupIds of groups associated with the user
	 */
	@Override
	public long[] getGroupPrimaryKeys(long userId) {
		return _userLocalService.getGroupPrimaryKeys(userId);
	}

	/**
	 * Returns the primary keys of all the users belonging to the group.
	 *
	 * @param groupId the primary key of the group
	 * @return the primary keys of the users belonging to the group
	 */
	@Override
	public long[] getGroupUserIds(long groupId) {
		return _userLocalService.getGroupUserIds(groupId);
	}

	@Override
	public java.util.List<User> getGroupUsers(long groupId) {
		return _userLocalService.getGroupUsers(groupId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public java.util.List<User> getGroupUsers(long groupId, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.getGroupUsers(groupId, start, end);
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

		return _userLocalService.getGroupUsers(
			groupId, status, start, end, orderByComparator);
	}

	@Override
	public java.util.List<User> getGroupUsers(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<User>
			orderByComparator) {

		return _userLocalService.getGroupUsers(
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
	@Override
	public java.util.List<User> getGroupUsers(
			long groupId, int status,
			com.liferay.portal.kernel.util.OrderByComparator<User>
				orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.getGroupUsers(
			groupId, status, orderByComparator);
	}

	@Override
	public int getGroupUsersCount(long groupId) {
		return _userLocalService.getGroupUsersCount(groupId);
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

		return _userLocalService.getGroupUsersCount(groupId, status);
	}

	/**
	 * Returns the guest user for the company.
	 *
	 * @param companyId the primary key of the company
	 * @return the guest user for the company
	 */
	@Override
	public User getGuestUser(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.getGuestUser(companyId);
	}

	/**
	 * Returns the primary key of the guest user for the company.
	 *
	 * @param companyId the primary key of the company
	 * @return the primary key of the guest user for the company
	 */
	@Override
	public long getGuestUserId(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.getGuestUserId(companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _userLocalService.getIndexableActionableDynamicQuery();
	}

	@Override
	public java.util.List<User> getInheritedRoleUsers(
			long roleId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<User>
				orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.getInheritedRoleUsers(
			roleId, start, end, orderByComparator);
	}

	/**
	 * Returns all the users who have not had any announcements of the type
	 * delivered, excluding the default user.
	 *
	 * @param type the type of announcement
	 * @return the users who have not had any annoucements of the type delivered
	 */
	@Override
	public java.util.List<User> getNoAnnouncementsDeliveries(String type) {
		return _userLocalService.getNoAnnouncementsDeliveries(type);
	}

	/**
	 * Returns all the users who do not belong to any groups, excluding the
	 * default user.
	 *
	 * @return the users who do not belong to any groups
	 */
	@Override
	public java.util.List<User> getNoGroups() {
		return _userLocalService.getNoGroups();
	}

	/**
	 * Returns the organizationIds of the organizations associated with the user.
	 *
	 * @param userId the userId of the user
	 * @return long[] the organizationIds of organizations associated with the user
	 */
	@Override
	public long[] getOrganizationPrimaryKeys(long userId) {
		return _userLocalService.getOrganizationPrimaryKeys(userId);
	}

	@Override
	public int getOrganizationsAndUserGroupsUsersCount(
		long[] organizationIds, long[] userGroupIds) {

		return _userLocalService.getOrganizationsAndUserGroupsUsersCount(
			organizationIds, userGroupIds);
	}

	/**
	 * Returns the primary keys of all the users belonging to the organization.
	 *
	 * @param organizationId the primary key of the organization
	 * @return the primary keys of the users belonging to the organization
	 */
	@Override
	public long[] getOrganizationUserIds(long organizationId) {
		return _userLocalService.getOrganizationUserIds(organizationId);
	}

	@Override
	public java.util.List<User> getOrganizationUsers(long organizationId) {
		return _userLocalService.getOrganizationUsers(organizationId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public java.util.List<User> getOrganizationUsers(
			long organizationId, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.getOrganizationUsers(
			organizationId, start, end);
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

		return _userLocalService.getOrganizationUsers(
			organizationId, status, start, end, orderByComparator);
	}

	@Override
	public java.util.List<User> getOrganizationUsers(
		long organizationId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<User>
			orderByComparator) {

		return _userLocalService.getOrganizationUsers(
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
	@Override
	public java.util.List<User> getOrganizationUsers(
			long organizationId, int status,
			com.liferay.portal.kernel.util.OrderByComparator<User>
				orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.getOrganizationUsers(
			organizationId, status, orderByComparator);
	}

	@Override
	public int getOrganizationUsersCount(long organizationId) {
		return _userLocalService.getOrganizationUsersCount(organizationId);
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

		return _userLocalService.getOrganizationUsersCount(
			organizationId, status);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _userLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Returns the roleIds of the roles associated with the user.
	 *
	 * @param userId the userId of the user
	 * @return long[] the roleIds of roles associated with the user
	 */
	@Override
	public long[] getRolePrimaryKeys(long userId) {
		return _userLocalService.getRolePrimaryKeys(userId);
	}

	/**
	 * Returns the primary keys of all the users belonging to the role.
	 *
	 * @param roleId the primary key of the role
	 * @return the primary keys of the users belonging to the role
	 */
	@Override
	public long[] getRoleUserIds(long roleId) {
		return _userLocalService.getRoleUserIds(roleId);
	}

	@Override
	public long[] getRoleUserIds(long roleId, long type) {
		return _userLocalService.getRoleUserIds(roleId, type);
	}

	@Override
	public java.util.List<User> getRoleUsers(long roleId) {
		return _userLocalService.getRoleUsers(roleId);
	}

	@Override
	public java.util.List<User> getRoleUsers(long roleId, int start, int end) {
		return _userLocalService.getRoleUsers(roleId, start, end);
	}

	@Override
	public java.util.List<User> getRoleUsers(
		long roleId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<User>
			orderByComparator) {

		return _userLocalService.getRoleUsers(
			roleId, start, end, orderByComparator);
	}

	@Override
	public int getRoleUsersCount(long roleId) {
		return _userLocalService.getRoleUsersCount(roleId);
	}

	/**
	 * Returns the number of users with the status belonging to the role.
	 *
	 * @param roleId the primary key of the role
	 * @param status the workflow status
	 * @return the number of users with the status belonging to the role
	 */
	@Override
	public int getRoleUsersCount(long roleId, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.getRoleUsersCount(roleId, status);
	}

	@Override
	public java.util.List<User> getSocialUsers(
			long userId, int socialRelationType,
			String socialRelationTypeComparator, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<User>
				orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.getSocialUsers(
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
	@Override
	public java.util.List<User> getSocialUsers(
			long userId1, long userId2, int socialRelationType, int start,
			int end,
			com.liferay.portal.kernel.util.OrderByComparator<User>
				orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.getSocialUsers(
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
	@Override
	public java.util.List<User> getSocialUsers(
			long userId1, long userId2, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<User>
				orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.getSocialUsers(
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
	@Override
	public int getSocialUsersCount(
			long userId, int socialRelationType,
			String socialRelationTypeComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.getSocialUsersCount(
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
	@Override
	public int getSocialUsersCount(long userId1, long userId2)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.getSocialUsersCount(userId1, userId2);
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
	@Override
	public int getSocialUsersCount(
			long userId1, long userId2, int socialRelationType)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.getSocialUsersCount(
			userId1, userId2, socialRelationType);
	}

	/**
	 * Returns the teamIds of the teams associated with the user.
	 *
	 * @param userId the userId of the user
	 * @return long[] the teamIds of teams associated with the user
	 */
	@Override
	public long[] getTeamPrimaryKeys(long userId) {
		return _userLocalService.getTeamPrimaryKeys(userId);
	}

	@Override
	public java.util.List<User> getTeamUsers(long teamId) {
		return _userLocalService.getTeamUsers(teamId);
	}

	@Override
	public java.util.List<User> getTeamUsers(long teamId, int start, int end) {
		return _userLocalService.getTeamUsers(teamId, start, end);
	}

	@Override
	public java.util.List<User> getTeamUsers(
		long teamId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<User>
			orderByComparator) {

		return _userLocalService.getTeamUsers(
			teamId, start, end, orderByComparator);
	}

	@Override
	public int getTeamUsersCount(long teamId) {
		return _userLocalService.getTeamUsersCount(teamId);
	}

	/**
	 * Returns the user with the primary key.
	 *
	 * @param userId the primary key of the user
	 * @return the user
	 * @throws PortalException if a user with the primary key could not be found
	 */
	@Override
	public User getUser(long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.getUser(userId);
	}

	/**
	 * Returns the user with the contact ID.
	 *
	 * @param contactId the user's contact ID
	 * @return the user with the contact ID
	 */
	@Override
	public User getUserByContactId(long contactId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.getUserByContactId(contactId);
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

		return _userLocalService.getUserByEmailAddress(companyId, emailAddress);
	}

	@Override
	public User getUserByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.getUserByExternalReferenceCode(
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

		return _userLocalService.getUserById(userId);
	}

	/**
	 * Returns the user with the primary key from the company.
	 *
	 * @param companyId the primary key of the user's company
	 * @param userId the primary key of the user
	 * @return the user with the primary key
	 */
	@Override
	public User getUserById(long companyId, long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.getUserById(companyId, userId);
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

		return _userLocalService.getUserByScreenName(companyId, screenName);
	}

	/**
	 * Returns the user with the matching UUID and company.
	 *
	 * @param uuid the user's UUID
	 * @param companyId the primary key of the company
	 * @return the matching user
	 * @throws PortalException if a matching user could not be found
	 */
	@Override
	public User getUserByUuidAndCompanyId(String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.getUserByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns the userGroupIds of the user groups associated with the user.
	 *
	 * @param userId the userId of the user
	 * @return long[] the userGroupIds of user groups associated with the user
	 */
	@Override
	public long[] getUserGroupPrimaryKeys(long userId) {
		return _userLocalService.getUserGroupPrimaryKeys(userId);
	}

	@Override
	public java.util.List<User> getUserGroupUsers(long userGroupId) {
		return _userLocalService.getUserGroupUsers(userGroupId);
	}

	@Override
	public java.util.List<User> getUserGroupUsers(
		long userGroupId, int start, int end) {

		return _userLocalService.getUserGroupUsers(userGroupId, start, end);
	}

	@Override
	public java.util.List<User> getUserGroupUsers(
		long userGroupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<User>
			orderByComparator) {

		return _userLocalService.getUserGroupUsers(
			userGroupId, start, end, orderByComparator);
	}

	@Override
	public int getUserGroupUsersCount(long userGroupId) {
		return _userLocalService.getUserGroupUsersCount(userGroupId);
	}

	/**
	 * Returns the number of users with the status belonging to the user group.
	 *
	 * @param userGroupId the primary key of the user group
	 * @param status the workflow status
	 * @return the number of users with the status belonging to the user group
	 */
	@Override
	public int getUserGroupUsersCount(long userGroupId, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.getUserGroupUsersCount(userGroupId, status);
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

		return _userLocalService.getUserIdByEmailAddress(
			companyId, emailAddress);
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

		return _userLocalService.getUserIdByScreenName(companyId, screenName);
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
	@Override
	public java.util.List<User> getUsers(int start, int end) {
		return _userLocalService.getUsers(start, end);
	}

	@Override
	public java.util.List<User> getUsers(
		long companyId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<User>
			orderByComparator) {

		return _userLocalService.getUsers(
			companyId, status, start, end, orderByComparator);
	}

	@Override
	public java.util.List<User> getUsersByRoleId(
			long roleId, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.getUsersByRoleId(roleId, start, end);
	}

	@Override
	public java.util.List<User> getUsersByRoleName(
			long companyId, String roleName, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.getUsersByRoleName(
			companyId, roleName, start, end);
	}

	/**
	 * Returns the number of users.
	 *
	 * @return the number of users
	 */
	@Override
	public int getUsersCount() {
		return _userLocalService.getUsersCount();
	}

	@Override
	public int getUsersCount(long companyId, int status) {
		return _userLocalService.getUsersCount(companyId, status);
	}

	@Override
	public boolean hasGroupUser(long groupId, long userId) {
		return _userLocalService.hasGroupUser(groupId, userId);
	}

	@Override
	public boolean hasGroupUsers(long groupId) {
		return _userLocalService.hasGroupUsers(groupId);
	}

	@Override
	public boolean hasOrganizationUser(long organizationId, long userId) {
		return _userLocalService.hasOrganizationUser(organizationId, userId);
	}

	@Override
	public boolean hasOrganizationUsers(long organizationId) {
		return _userLocalService.hasOrganizationUsers(organizationId);
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
	@Override
	public boolean hasPasswordPolicyUser(long passwordPolicyId, long userId) {
		return _userLocalService.hasPasswordPolicyUser(
			passwordPolicyId, userId);
	}

	@Override
	public boolean hasRoleUser(long roleId, long userId) {
		return _userLocalService.hasRoleUser(roleId, userId);
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

		return _userLocalService.hasRoleUser(
			companyId, name, userId, inherited);
	}

	@Override
	public boolean hasRoleUsers(long roleId) {
		return _userLocalService.hasRoleUsers(roleId);
	}

	@Override
	public boolean hasTeamUser(long teamId, long userId) {
		return _userLocalService.hasTeamUser(teamId, userId);
	}

	@Override
	public boolean hasTeamUsers(long teamId) {
		return _userLocalService.hasTeamUsers(teamId);
	}

	@Override
	public boolean hasUserGroupUser(long userGroupId, long userId) {
		return _userLocalService.hasUserGroupUser(userGroupId, userId);
	}

	@Override
	public boolean hasUserGroupUsers(long userGroupId) {
		return _userLocalService.hasUserGroupUsers(userGroupId);
	}

	/**
	 * Returns <code>true</code> if the user's password is expired.
	 *
	 * @param user the user
	 * @return <code>true</code> if the user's password is expired;
	 <code>false</code> otherwise
	 */
	@Override
	public boolean isPasswordExpired(User user)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.isPasswordExpired(user);
	}

	/**
	 * Returns the guest user for the company.
	 *
	 * @param companyId the primary key of the company
	 * @return the guest user for the company
	 */
	@Override
	public User loadGetGuestUser(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.loadGetGuestUser(companyId);
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
	@Override
	public java.util.List<User> search(
		long companyId, String keywords, int status,
		java.util.LinkedHashMap<String, Object> params, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<User>
			orderByComparator) {

		return _userLocalService.search(
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
	@Override
	public com.liferay.portal.kernel.search.Hits search(
		long companyId, String keywords, int status,
		java.util.LinkedHashMap<String, Object> params, int start, int end,
		com.liferay.portal.kernel.search.Sort sort) {

		return _userLocalService.search(
			companyId, keywords, status, params, start, end, sort);
	}

	@Override
	public com.liferay.portal.kernel.search.Hits search(
		long companyId, String keywords, int status,
		java.util.LinkedHashMap<String, Object> params, int start, int end,
		com.liferay.portal.kernel.search.Sort[] sorts) {

		return _userLocalService.search(
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
	@Override
	public java.util.List<User> search(
		long companyId, String firstName, String middleName, String lastName,
		String screenName, String emailAddress, int status,
		java.util.LinkedHashMap<String, Object> params, boolean andSearch,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<User>
			orderByComparator) {

		return _userLocalService.search(
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
	@Override
	public com.liferay.portal.kernel.search.Hits search(
		long companyId, String firstName, String middleName, String lastName,
		String screenName, String emailAddress, int status,
		java.util.LinkedHashMap<String, Object> params, boolean andSearch,
		int start, int end, com.liferay.portal.kernel.search.Sort sort) {

		return _userLocalService.search(
			companyId, firstName, middleName, lastName, screenName,
			emailAddress, status, params, andSearch, start, end, sort);
	}

	@Override
	public com.liferay.portal.kernel.search.Hits search(
		long companyId, String firstName, String middleName, String lastName,
		String screenName, String emailAddress, int status,
		java.util.LinkedHashMap<String, Object> params, boolean andSearch,
		int start, int end, com.liferay.portal.kernel.search.Sort[] sorts) {

		return _userLocalService.search(
			companyId, firstName, middleName, lastName, screenName,
			emailAddress, status, params, andSearch, start, end, sorts);
	}

	@Override
	public java.util.List<User> searchBySocial(
			long userId, int[] socialRelationTypes, String keywords, int start,
			int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.searchBySocial(
			userId, socialRelationTypes, keywords, start, end);
	}

	@Override
	public java.util.List<User> searchBySocial(
		long companyId, long[] groupIds, long[] userGroupIds, String keywords,
		int start, int end) {

		return _userLocalService.searchBySocial(
			companyId, groupIds, userGroupIds, keywords, start, end);
	}

	@Override
	public java.util.List<User> searchBySocial(
		long companyId, long[] groupIds, long[] userGroupIds, String keywords,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<User>
			orderByComparator) {

		return _userLocalService.searchBySocial(
			companyId, groupIds, userGroupIds, keywords, start, end,
			orderByComparator);
	}

	@Override
	public java.util.List<User> searchBySocial(
			long[] groupIds, long userId, int[] socialRelationTypes,
			String keywords, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.searchBySocial(
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
	@Override
	public int searchCount(
		long companyId, String keywords, int status,
		java.util.LinkedHashMap<String, Object> params) {

		return _userLocalService.searchCount(
			companyId, keywords, status, params);
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
	@Override
	public int searchCount(
		long companyId, String firstName, String middleName, String lastName,
		String screenName, String emailAddress, int status,
		java.util.LinkedHashMap<String, Object> params, boolean andSearch) {

		return _userLocalService.searchCount(
			companyId, firstName, middleName, lastName, screenName,
			emailAddress, status, params, andSearch);
	}

	@Override
	public int searchCountBySocial(
		long companyId, long[] groupIds, long[] userGroupIds, String keywords) {

		return _userLocalService.searchCountBySocial(
			companyId, groupIds, userGroupIds, keywords);
	}

	@Override
	public java.util.Map<Long, Integer> searchCounts(
		long companyId, int status, long[] groupIds) {

		return _userLocalService.searchCounts(companyId, status, groupIds);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult<User>
			searchUsers(
				long companyId, String keywords, int status,
				java.util.LinkedHashMap<String, Object> params, int start,
				int end, com.liferay.portal.kernel.search.Sort sort)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.searchUsers(
			companyId, keywords, status, params, start, end, sort);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult<User>
			searchUsers(
				long companyId, String keywords, int status,
				java.util.LinkedHashMap<String, Object> params, int start,
				int end, com.liferay.portal.kernel.search.Sort[] sorts)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.searchUsers(
			companyId, keywords, status, params, start, end, sorts);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult<User>
			searchUsers(
				long companyId, String firstName, String middleName,
				String lastName, String screenName, String emailAddress,
				int status, java.util.LinkedHashMap<String, Object> params,
				boolean andSearch, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.searchUsers(
			companyId, firstName, middleName, lastName, screenName,
			emailAddress, status, params, andSearch, start, end, sort);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult<User>
			searchUsers(
				long companyId, String firstName, String middleName,
				String lastName, String screenName, String emailAddress,
				int status, java.util.LinkedHashMap<String, Object> params,
				boolean andSearch, int start, int end,
				com.liferay.portal.kernel.search.Sort[] sorts)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.searchUsers(
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
	@Override
	public void sendEmailAddressVerification(
			User user, String emailAddress, ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		_userLocalService.sendEmailAddressVerification(
			user, emailAddress, serviceContext);
	}

	@Override
	public boolean sendEmailUserCreationAttempt(
			long companyId, String emailAddress, String fromName,
			String fromAddress, String subject, String body,
			ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.sendEmailUserCreationAttempt(
			companyId, emailAddress, fromName, fromAddress, subject, body,
			serviceContext);
	}

	@Override
	public boolean sendPassword(
			long companyId, String emailAddress, String fromName,
			String fromAddress, String subject, String body,
			ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.sendPassword(
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
	@Override
	public boolean sendPasswordByEmailAddress(
			long companyId, String emailAddress)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.sendPasswordByEmailAddress(
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
	 * @param companyId the primary key of the user's company
	 * @param screenName the user's screen name
	 * @return <code>true</code> if the notification email includes a new
	 password; <code>false</code> if the notification email only
	 contains a reset link
	 */
	@Override
	public boolean sendPasswordByScreenName(long companyId, String screenName)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.sendPasswordByScreenName(
			companyId, screenName);
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

		return _userLocalService.sendPasswordByUserId(userId);
	}

	@Override
	public boolean sendPasswordLockout(
			long companyId, String emailAddress, String fromName,
			String fromAddress, String subject, String body,
			ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.sendPasswordLockout(
			companyId, emailAddress, fromName, fromAddress, subject, body,
			serviceContext);
	}

	@Override
	public void setGroupUsers(long groupId, long[] userIds) {
		_userLocalService.setGroupUsers(groupId, userIds);
	}

	@Override
	public void setOrganizationUsers(long organizationId, long[] userIds) {
		_userLocalService.setOrganizationUsers(organizationId, userIds);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public void setRoleUsers(long roleId, long[] userIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		_userLocalService.setRoleUsers(roleId, userIds);
	}

	@Override
	public void setTeamUsers(long teamId, long[] userIds) {
		_userLocalService.setTeamUsers(teamId, userIds);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public void setUserGroupUsers(long userGroupId, long[] userIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		_userLocalService.setUserGroupUsers(userGroupId, userIds);
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

		_userLocalService.unsetGroupTeamsUsers(groupId, userIds);
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

		_userLocalService.unsetGroupUsers(groupId, userIds, serviceContext);
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

		_userLocalService.unsetOrganizationUsers(organizationId, userIds);
	}

	/**
	 * Removes the users from the password policy.
	 *
	 * @param passwordPolicyId the primary key of the password policy
	 * @param userIds the primary keys of the users
	 */
	@Override
	public void unsetPasswordPolicyUsers(
		long passwordPolicyId, long[] userIds) {

		_userLocalService.unsetPasswordPolicyUsers(passwordPolicyId, userIds);
	}

	/**
	 * Removes the users from the role.
	 *
	 * @param roleId the primary key of the role
	 * @param users the users
	 */
	@Override
	public void unsetRoleUsers(long roleId, java.util.List<User> users)
		throws com.liferay.portal.kernel.exception.PortalException {

		_userLocalService.unsetRoleUsers(roleId, users);
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

		_userLocalService.unsetRoleUsers(roleId, userIds);
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

		_userLocalService.unsetTeamUsers(teamId, userIds);
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

		_userLocalService.unsetUserGroupUsers(userGroupId, userIds);
	}

	/**
	 * Updates whether the user has agreed to the terms of use.
	 *
	 * @param userId the primary key of the user
	 * @param agreedToTermsOfUse whether the user has agreet to the terms of
	 use
	 * @return the user
	 */
	@Override
	public User updateAgreedToTermsOfUse(
			long userId, boolean agreedToTermsOfUse)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.updateAgreedToTermsOfUse(
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
	@Override
	public void updateAsset(
			long userId, User user, long[] assetCategoryIds,
			String[] assetTagNames)
		throws com.liferay.portal.kernel.exception.PortalException {

		_userLocalService.updateAsset(
			userId, user, assetCategoryIds, assetTagNames);
	}

	/**
	 * Updates the user's creation date.
	 *
	 * @param userId the primary key of the user
	 * @param createDate the new creation date
	 * @return the user
	 */
	@Override
	public User updateCreateDate(long userId, java.util.Date createDate)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.updateCreateDate(userId, createDate);
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
	@Override
	public User updateEmailAddress(
			long userId, String password, String emailAddress1,
			String emailAddress2)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.updateEmailAddress(
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
	@Override
	public User updateEmailAddress(
			long userId, String password, String emailAddress1,
			String emailAddress2, ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.updateEmailAddress(
			userId, password, emailAddress1, emailAddress2, serviceContext);
	}

	/**
	 * Updates whether the user has verified email address.
	 *
	 * @param userId the primary key of the user
	 * @param emailAddressVerified whether the user has verified email address
	 * @return the user
	 */
	@Override
	public User updateEmailAddressVerified(
			long userId, boolean emailAddressVerified)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.updateEmailAddressVerified(
			userId, emailAddressVerified);
	}

	@Override
	public User updateExternalReferenceCode(
			long userId, String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.updateExternalReferenceCode(
			userId, externalReferenceCode);
	}

	@Override
	public User updateExternalReferenceCode(
			User user, String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.updateExternalReferenceCode(
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
	@Override
	public void updateGroups(
			long userId, long[] newGroupIds, ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		_userLocalService.updateGroups(userId, newGroupIds, serviceContext);
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
	@Override
	public User updateIncompleteUser(
			long creatorUserId, long companyId, boolean autoPassword,
			String password1, String password2, boolean autoScreenName,
			String screenName, String emailAddress, java.util.Locale locale,
			String firstName, String middleName, String lastName,
			long prefixListTypeId, long suffixListTypeId, boolean male,
			int birthdayMonth, int birthdayDay, int birthdayYear,
			String jobTitle, boolean updateUserInformation, boolean sendEmail,
			ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.updateIncompleteUser(
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
	@Override
	public User updateJobTitle(long userId, String jobTitle)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.updateJobTitle(userId, jobTitle);
	}

	@Override
	public User updateLanguageId(long userId, String languageId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.updateLanguageId(userId, languageId);
	}

	/**
	 * Updates the user's last login with the current time and the IP address.
	 *
	 * @param userId the primary key of the user
	 * @param loginIP the IP address the user logged in from
	 * @return the user
	 */
	@Override
	public User updateLastLogin(long userId, String loginIP)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.updateLastLogin(userId, loginIP);
	}

	@Override
	public User updateLastLogin(User user, String loginIP)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.updateLastLogin(user, loginIP);
	}

	/**
	 * Updates whether the user is locked out from logging in.
	 *
	 * @param user the user
	 * @param lockout whether the user is locked out
	 * @return the user
	 */
	@Override
	public User updateLockout(User user, boolean lockout)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.updateLockout(user, lockout);
	}

	/**
	 * Updates whether the user is locked out from logging in.
	 *
	 * @param companyId the primary key of the user's company
	 * @param emailAddress the user's email address
	 * @param lockout whether the user is locked out
	 * @return the user
	 */
	@Override
	public User updateLockoutByEmailAddress(
			long companyId, String emailAddress, boolean lockout)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.updateLockoutByEmailAddress(
			companyId, emailAddress, lockout);
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

		return _userLocalService.updateLockoutById(userId, lockout);
	}

	/**
	 * Updates whether the user is locked out from logging in.
	 *
	 * @param companyId the primary key of the user's company
	 * @param screenName the user's screen name
	 * @param lockout whether the user is locked out
	 * @return the user
	 */
	@Override
	public User updateLockoutByScreenName(
			long companyId, String screenName, boolean lockout)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.updateLockoutByScreenName(
			companyId, screenName, lockout);
	}

	/**
	 * Updates the user's modified date.
	 *
	 * @param userId the primary key of the user
	 * @param modifiedDate the new modified date
	 * @return the user
	 */
	@Override
	public User updateModifiedDate(long userId, java.util.Date modifiedDate)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.updateModifiedDate(userId, modifiedDate);
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
	@Override
	public void updateOrganizations(
			long userId, long[] newOrganizationIds,
			ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		_userLocalService.updateOrganizations(
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
	@Override
	public User updatePassword(
			long userId, String password1, String password2,
			boolean passwordReset)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.updatePassword(
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
	@Override
	public User updatePassword(
			long userId, String password1, String password2,
			boolean passwordReset, boolean silentUpdate)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.updatePassword(
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
	@Override
	public User updatePasswordManually(
			long userId, String password, boolean passwordEncrypted,
			boolean passwordReset, java.util.Date passwordModifiedDate)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.updatePasswordManually(
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
	@Override
	public User updatePasswordReset(long userId, boolean passwordReset)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.updatePasswordReset(userId, passwordReset);
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

		return _userLocalService.updatePortrait(userId, bytes);
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

		return _userLocalService.updateReminderQuery(userId, question, answer);
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

		return _userLocalService.updateScreenName(userId, screenName);
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

		return _userLocalService.updateStatus(userId, status, serviceContext);
	}

	@Override
	public User updateStatus(
			User user, int status, ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.updateStatus(user, status, serviceContext);
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
			long[] userGroupIds, ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userLocalService.updateUser(
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
	@Override
	public User updateUser(User user) {
		return _userLocalService.updateUser(user);
	}

	@Override
	public void validateMaxUsers(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_userLocalService.validateMaxUsers(companyId);
	}

	/**
	 * Verifies the email address of the ticket.
	 *
	 * @param ticketKey the ticket key
	 */
	@Override
	public void verifyEmailAddress(String ticketKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		_userLocalService.verifyEmailAddress(ticketKey);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _userLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<User> getCTPersistence() {
		return _userLocalService.getCTPersistence();
	}

	@Override
	public Class<User> getModelClass() {
		return _userLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<User>, R, E> updateUnsafeFunction)
		throws E {

		return _userLocalService.updateWithUnsafeFunction(updateUnsafeFunction);
	}

	@Override
	public UserLocalService getWrappedService() {
		return _userLocalService;
	}

	@Override
	public void setWrappedService(UserLocalService userLocalService) {
		_userLocalService = userLocalService;
	}

	private UserLocalService _userLocalService;

}