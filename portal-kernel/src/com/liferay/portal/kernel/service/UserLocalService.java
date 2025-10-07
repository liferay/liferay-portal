/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.portal.kernel.spring.osgi.OSGiBeanProperties;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for User. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see UserLocalServiceUtil
 * @generated
 */
@CTAware
@OSGiBeanProperties(
	property = {"model.class.name=com.liferay.portal.kernel.model.User"}
)
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface UserLocalService
	extends BaseLocalService, CTService<User>, PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.portal.service.impl.UserLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the user local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link UserLocalServiceUtil} if injection and service tracking are not available.
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
	public User addDefaultAdminUser(
			long companyId, String password, String screenName,
			String emailAddress, Locale locale, String firstName,
			String middleName, String lastName)
		throws PortalException;

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
	public boolean addDefaultGroups(long userId) throws PortalException;

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
	public boolean addDefaultRoles(long userId) throws PortalException;

	public User addDefaultServiceAccountUser(long companyId)
		throws PortalException;

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
	public boolean addDefaultUserGroups(long userId) throws PortalException;

	public boolean addGroupUser(long groupId, long userId);

	public boolean addGroupUser(long groupId, User user);

	/**
	 * @throws PortalException
	 */
	public boolean addGroupUsers(long groupId, List<User> users)
		throws PortalException;

	/**
	 * @throws PortalException
	 */
	public boolean addGroupUsers(long groupId, long[] userIds)
		throws PortalException;

	public boolean addOrganizationUser(long organizationId, long userId);

	public boolean addOrganizationUser(long organizationId, User user);

	/**
	 * @throws PortalException
	 */
	public boolean addOrganizationUsers(long organizationId, List<User> users)
		throws PortalException;

	/**
	 * @throws PortalException
	 */
	public boolean addOrganizationUsers(long organizationId, long[] userIds)
		throws PortalException;

	public User addOrUpdateUser(
			String externalReferenceCode, long creatorUserId, long companyId,
			boolean autoPassword, String password1, String password2,
			boolean autoScreenName, String screenName, String emailAddress,
			Locale locale, String firstName, String middleName, String lastName,
			long prefixListTypeId, long suffixListTypeId, boolean male,
			int birthdayMonth, int birthdayDay, int birthdayYear,
			String jobTitle, boolean sendEmail, ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Assigns the password policy to the users, removing any other currently
	 * assigned password policies.
	 *
	 * @param passwordPolicyId the primary key of the password policy
	 * @param userIds the primary keys of the users
	 */
	public void addPasswordPolicyUsers(long passwordPolicyId, long[] userIds);

	public boolean addRoleUser(long roleId, long userId);

	public boolean addRoleUser(long roleId, User user);

	/**
	 * @throws PortalException
	 */
	public boolean addRoleUsers(long roleId, List<User> users)
		throws PortalException;

	/**
	 * @throws PortalException
	 */
	public boolean addRoleUsers(long roleId, long[] userIds)
		throws PortalException;

	public boolean addTeamUser(long teamId, long userId);

	public boolean addTeamUser(long teamId, User user);

	/**
	 * @throws PortalException
	 */
	public boolean addTeamUsers(long teamId, List<User> users)
		throws PortalException;

	/**
	 * @throws PortalException
	 */
	public boolean addTeamUsers(long teamId, long[] userIds)
		throws PortalException;

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
	public User addUser(
			long creatorUserId, long companyId, boolean autoPassword,
			String password1, String password2, boolean autoScreenName,
			String screenName, String emailAddress, Locale locale,
			String firstName, String middleName, String lastName,
			long prefixListTypeId, long suffixListTypeId, boolean male,
			int birthdayMonth, int birthdayDay, int birthdayYear,
			String jobTitle, int type, long[] groupIds, long[] organizationIds,
			long[] roleIds, long[] userGroupIds, boolean sendEmail,
			ServiceContext serviceContext)
		throws PortalException;

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
	@Indexable(type = IndexableType.REINDEX)
	public User addUser(User user);

	public boolean addUserGroupUser(long userGroupId, long userId);

	public boolean addUserGroupUser(long userGroupId, User user);

	/**
	 * @throws PortalException
	 */
	public boolean addUserGroupUsers(long userGroupId, List<User> users)
		throws PortalException;

	/**
	 * @throws PortalException
	 */
	public boolean addUserGroupUsers(long userGroupId, long[] userIds)
		throws PortalException;

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
	public User addUserWithWorkflow(
			long creatorUserId, long companyId, boolean autoPassword,
			String password1, String password2, boolean autoScreenName,
			String screenName, String emailAddress, Locale locale,
			String firstName, String middleName, String lastName,
			long prefixListTypeId, long suffixListTypeId, boolean male,
			int birthdayMonth, int birthdayDay, int birthdayYear,
			String jobTitle, int type, long[] groupIds, long[] organizationIds,
			long[] roleIds, long[] userGroupIds, boolean sendEmail,
			ServiceContext serviceContext)
		throws PortalException;

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
	@Transactional(propagation = Propagation.REQUIRED)
	public int authenticateByEmailAddress(
			long companyId, String emailAddress, String password,
			Map<String, String[]> headerMap, Map<String, String[]> parameterMap,
			Map<String, Object> resultsMap)
		throws PortalException;

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
	@Transactional(propagation = Propagation.REQUIRED)
	public int authenticateByScreenName(
			long companyId, String screenName, String password,
			Map<String, String[]> headerMap, Map<String, String[]> parameterMap,
			Map<String, Object> resultsMap)
		throws PortalException;

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
	@Transactional(propagation = Propagation.REQUIRED)
	public int authenticateByUserId(
			long companyId, long userId, String password,
			Map<String, String[]> headerMap, Map<String, String[]> parameterMap,
			Map<String, Object> resultsMap)
		throws PortalException;

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
	@Transactional(propagation = Propagation.SUPPORTS)
	public long authenticateForBasic(
			long companyId, String authType, String login, String password)
		throws PortalException;

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
	@Transactional(propagation = Propagation.SUPPORTS)
	public long authenticateForDigest(
			long companyId, String userName, String realm, String nonce,
			String method, String uri, String response)
		throws PortalException;

	/**
	 * Checks if the user is currently locked out based on the password policy,
	 * and performs maintenance on the user's lockout and failed login data.
	 *
	 * @param user the user
	 */
	public void checkLockout(User user) throws PortalException;

	/**
	 * Adds a failed login attempt to the user and updates the user's last
	 * failed login date.
	 *
	 * @param user the user
	 */
	public void checkLoginFailure(User user);

	/**
	 * Adds a failed login attempt to the user with the email address and
	 * updates the user's last failed login date.
	 *
	 * @param companyId the primary key of the user's company
	 * @param emailAddress the user's email address
	 */
	public void checkLoginFailureByEmailAddress(
			long companyId, String emailAddress)
		throws PortalException;

	/**
	 * Adds a failed login attempt to the user and updates the user's last
	 * failed login date.
	 *
	 * @param userId the primary key of the user
	 */
	public void checkLoginFailureById(long userId) throws PortalException;

	/**
	 * Adds a failed login attempt to the user with the screen name and updates
	 * the user's last failed login date.
	 *
	 * @param companyId the primary key of the user's company
	 * @param screenName the user's screen name
	 */
	public void checkLoginFailureByScreenName(long companyId, String screenName)
		throws PortalException;

	/**
	 * Checks if the user's password is expired based on the password policy,
	 * and performs maintenance on the user's grace login and password reset
	 * data.
	 *
	 * @param user the user
	 */
	public void checkPasswordExpired(User user) throws PortalException;

	public void clearGroupUsers(long groupId);

	public void clearOrganizationUsers(long organizationId);

	public void clearRoleUsers(long roleId);

	public void clearTeamUsers(long teamId);

	public void clearUserGroupUsers(long userGroupId);

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
	public void completeUserRegistration(
			User user, ServiceContext serviceContext)
		throws PortalException;

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Creates a new user with the primary key. Does not add the user to the database.
	 *
	 * @param userId the primary key for the new user
	 * @return the new user
	 */
	@Transactional(enabled = false)
	public User createUser(long userId);

	public void deleteGroupUser(long groupId, long userId);

	public void deleteGroupUser(long groupId, User user);

	public void deleteGroupUsers(long groupId, List<User> users);

	public void deleteGroupUsers(long groupId, long[] userIds);

	public void deleteOrganizationUser(long organizationId, long userId);

	public void deleteOrganizationUser(long organizationId, User user);

	public void deleteOrganizationUsers(long organizationId, List<User> users);

	public void deleteOrganizationUsers(long organizationId, long[] userIds);

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException;

	/**
	 * Deletes the user's portrait image.
	 *
	 * @param userId the primary key of the user
	 */
	public void deletePortrait(long userId) throws PortalException;

	/**
	 * @throws PortalException
	 */
	public void deleteRoleUser(long roleId, long userId) throws PortalException;

	/**
	 * @throws PortalException
	 */
	public void deleteRoleUser(long roleId, User user) throws PortalException;

	public void deleteRoleUsers(long roleId, List<User> users);

	public void deleteRoleUsers(long roleId, long[] userIds);

	public void deleteTeamUser(long teamId, long userId);

	public void deleteTeamUser(long teamId, User user);

	public void deleteTeamUsers(long teamId, List<User> users);

	public void deleteTeamUsers(long teamId, long[] userIds);

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
	@Indexable(type = IndexableType.DELETE)
	public User deleteUser(long userId) throws PortalException;

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
	@Indexable(type = IndexableType.DELETE)
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public User deleteUser(User user) throws PortalException;

	/**
	 * @throws PortalException
	 */
	public void deleteUserGroupUser(long userGroupId, long userId)
		throws PortalException;

	/**
	 * @throws PortalException
	 */
	public void deleteUserGroupUser(long userGroupId, User user)
		throws PortalException;

	public void deleteUserGroupUsers(long userGroupId, List<User> users);

	public void deleteUserGroupUsers(long userGroupId, long[] userIds);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> T dslQuery(DSLQuery dslQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int dslQueryCount(DSLQuery dslQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DynamicQuery dynamicQuery();

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(DynamicQuery dynamicQuery);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(DynamicQuery dynamicQuery);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(
		DynamicQuery dynamicQuery, Projection projection);

	/**
	 * Encrypts the primary key of the user. Used when encrypting the user's
	 * credentials for storage in an automatic login cookie.
	 *
	 * @param name the primary key of the user
	 * @return the user's encrypted primary key
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public String encryptUserId(String name) throws PortalException;

	/**
	 * Returns the guest user for the company.
	 *
	 * @param companyId the primary key of the company
	 * @return the guest user for the company, or <code>null</code> if a user
	 with the company key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public User fetchGuestUser(long companyId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public User fetchUser(long userId);

	/**
	 * Returns the user with the contact ID.
	 *
	 * @param contactId the user's contact ID
	 * @return the user with the contact ID, or <code>null</code> if a user with
	 the contact ID could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public User fetchUserByContactId(long contactId);

	/**
	 * Returns the user with the email address.
	 *
	 * @param companyId the primary key of the user's company
	 * @param emailAddress the user's email address
	 * @return the user with the email address, or <code>null</code> if a user
	 with the email address could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public User fetchUserByEmailAddress(long companyId, String emailAddress);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public User fetchUserByExternalReferenceCode(
		String externalReferenceCode, long companyId);

	/**
	 * Returns the user with the Facebook ID.
	 *
	 * @param companyId the primary key of the user's company
	 * @param facebookId the user's Facebook ID
	 * @return the user with the Facebook ID, or <code>null</code> if a user
	 with the Facebook ID could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public User fetchUserByFacebookId(long companyId, long facebookId);

	/**
	 * Returns the user with the primary key.
	 *
	 * @param userId the primary key of the user
	 * @return the user with the primary key, or <code>null</code> if a user
	 with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public User fetchUserById(long userId);

	/**
	 * Returns the user with the portrait ID.
	 *
	 * @param portraitId the user's portrait ID
	 * @return the user with the portrait ID, or <code>null</code> if a user
	 with the portrait ID could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public User fetchUserByPortraitId(long portraitId);

	/**
	 * Returns the user with the screen name.
	 *
	 * @param companyId the primary key of the user's company
	 * @param screenName the user's screen name
	 * @return the user with the screen name, or <code>null</code> if a user
	 with the screen name could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public User fetchUserByScreenName(long companyId, String screenName);

	/**
	 * Returns the user with the matching UUID and company.
	 *
	 * @param uuid the user's UUID
	 * @param companyId the primary key of the company
	 * @return the matching user, or <code>null</code> if a matching user could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public User fetchUserByUuidAndCompanyId(String uuid, long companyId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<User> getCompanyUsers(long companyId, int start, int end);

	/**
	 * Returns the number of users belonging to the company.
	 *
	 * @param companyId the primary key of the company
	 * @return the number of users belonging to the company
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCompanyUsersCount(long companyId);

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 #getGuestUser(long)}
	 */
	@Deprecated
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public User getDefaultUser(long companyId) throws PortalException;

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 #getGuestUserId(long)}
	 */
	@Deprecated
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long getDefaultUserId(long companyId) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		PortletDataContext portletDataContext);

	/**
	 * Returns the groupIds of the groups associated with the user.
	 *
	 * @param userId the userId of the user
	 * @return long[] the groupIds of groups associated with the user
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long[] getGroupPrimaryKeys(long userId);

	/**
	 * Returns the primary keys of all the users belonging to the group.
	 *
	 * @param groupId the primary key of the group
	 * @return the primary keys of the users belonging to the group
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long[] getGroupUserIds(long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<User> getGroupUsers(long groupId);

	/**
	 * @throws PortalException
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<User> getGroupUsers(long groupId, int start, int end)
		throws PortalException;

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<User> getGroupUsers(
			long groupId, int status, int start, int end,
			OrderByComparator<User> orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<User> getGroupUsers(
		long groupId, int start, int end,
		OrderByComparator<User> orderByComparator);

	/**
	 * Returns the users belonging to a group.
	 *
	 * @param groupId the primary key of the group
	 * @param status the workflow status
	 * @param orderByComparator the comparator to order the users by
	 (optionally <code>null</code>)
	 * @return the matching users
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<User> getGroupUsers(
			long groupId, int status, OrderByComparator<User> orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getGroupUsersCount(long groupId);

	/**
	 * Returns the number of users with the status belonging to the group.
	 *
	 * @param groupId the primary key of the group
	 * @param status the workflow status
	 * @return the number of users with the status belonging to the group
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getGroupUsersCount(long groupId, int status)
		throws PortalException;

	/**
	 * Returns the guest user for the company.
	 *
	 * @param companyId the primary key of the company
	 * @return the guest user for the company
	 */
	@Transactional(enabled = false)
	public User getGuestUser(long companyId) throws PortalException;

	/**
	 * Returns the primary key of the guest user for the company.
	 *
	 * @param companyId the primary key of the company
	 * @return the primary key of the guest user for the company
	 */
	@Transactional(enabled = false)
	public long getGuestUserId(long companyId) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<User> getInheritedRoleUsers(
			long roleId, int start, int end,
			OrderByComparator<User> orderByComparator)
		throws PortalException;

	/**
	 * Returns all the users who have not had any announcements of the type
	 * delivered, excluding the default user.
	 *
	 * @param type the type of announcement
	 * @return the users who have not had any annoucements of the type delivered
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<User> getNoAnnouncementsDeliveries(String type);

	/**
	 * Returns all the users who do not belong to any groups, excluding the
	 * default user.
	 *
	 * @return the users who do not belong to any groups
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<User> getNoGroups();

	/**
	 * Returns the organizationIds of the organizations associated with the user.
	 *
	 * @param userId the userId of the user
	 * @return long[] the organizationIds of organizations associated with the user
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long[] getOrganizationPrimaryKeys(long userId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getOrganizationsAndUserGroupsUsersCount(
		long[] organizationIds, long[] userGroupIds);

	/**
	 * Returns the primary keys of all the users belonging to the organization.
	 *
	 * @param organizationId the primary key of the organization
	 * @return the primary keys of the users belonging to the organization
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long[] getOrganizationUserIds(long organizationId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<User> getOrganizationUsers(long organizationId);

	/**
	 * @throws PortalException
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<User> getOrganizationUsers(
			long organizationId, int start, int end)
		throws PortalException;

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<User> getOrganizationUsers(
			long organizationId, int status, int start, int end,
			OrderByComparator<User> orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<User> getOrganizationUsers(
		long organizationId, int start, int end,
		OrderByComparator<User> orderByComparator);

	/**
	 * Returns the users belonging to the organization with the status.
	 *
	 * @param organizationId the primary key of the organization
	 * @param status the workflow status
	 * @param orderByComparator the comparator to order the users by
	 (optionally <code>null</code>)
	 * @return the matching users
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<User> getOrganizationUsers(
			long organizationId, int status,
			OrderByComparator<User> orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getOrganizationUsersCount(long organizationId);

	/**
	 * Returns the number of users with the status belonging to the
	 * organization.
	 *
	 * @param organizationId the primary key of the organization
	 * @param status the workflow status
	 * @return the number of users with the status belonging to the organization
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getOrganizationUsersCount(long organizationId, int status)
		throws PortalException;

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	/**
	 * @throws PortalException
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Returns the roleIds of the roles associated with the user.
	 *
	 * @param userId the userId of the user
	 * @return long[] the roleIds of roles associated with the user
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long[] getRolePrimaryKeys(long userId);

	/**
	 * Returns the primary keys of all the users belonging to the role.
	 *
	 * @param roleId the primary key of the role
	 * @return the primary keys of the users belonging to the role
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long[] getRoleUserIds(long roleId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long[] getRoleUserIds(long roleId, long type);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<User> getRoleUsers(long roleId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<User> getRoleUsers(long roleId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<User> getRoleUsers(
		long roleId, int start, int end,
		OrderByComparator<User> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getRoleUsersCount(long roleId);

	/**
	 * Returns the number of users with the status belonging to the role.
	 *
	 * @param roleId the primary key of the role
	 * @param status the workflow status
	 * @return the number of users with the status belonging to the role
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getRoleUsersCount(long roleId, int status)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<User> getSocialUsers(
			long userId, int socialRelationType,
			String socialRelationTypeComparator, int start, int end,
			OrderByComparator<User> orderByComparator)
		throws PortalException;

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<User> getSocialUsers(
			long userId1, long userId2, int socialRelationType, int start,
			int end, OrderByComparator<User> orderByComparator)
		throws PortalException;

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<User> getSocialUsers(
			long userId1, long userId2, int start, int end,
			OrderByComparator<User> orderByComparator)
		throws PortalException;

	/**
	 * Returns the number of users with a social relation with the user.
	 *
	 * @param userId the primary key of the user
	 * @param socialRelationType the type of social relation. The possible
	 types can be found in {@link SocialRelationConstants}.
	 * @return the number of users with a social relation with the user
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getSocialUsersCount(
			long userId, int socialRelationType,
			String socialRelationTypeComparator)
		throws PortalException;

	/**
	 * Returns the number of users with a mutual social relation with both of
	 * the given users.
	 *
	 * @param userId1 the primary key of the first user
	 * @param userId2 the primary key of the second user
	 * @return the number of users with a mutual social relation with the user
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getSocialUsersCount(long userId1, long userId2)
		throws PortalException;

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getSocialUsersCount(
			long userId1, long userId2, int socialRelationType)
		throws PortalException;

	/**
	 * Returns the teamIds of the teams associated with the user.
	 *
	 * @param userId the userId of the user
	 * @return long[] the teamIds of teams associated with the user
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long[] getTeamPrimaryKeys(long userId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<User> getTeamUsers(long teamId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<User> getTeamUsers(long teamId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<User> getTeamUsers(
		long teamId, int start, int end,
		OrderByComparator<User> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getTeamUsersCount(long teamId);

	/**
	 * Returns the user with the primary key.
	 *
	 * @param userId the primary key of the user
	 * @return the user
	 * @throws PortalException if a user with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public User getUser(long userId) throws PortalException;

	/**
	 * Returns the user with the contact ID.
	 *
	 * @param contactId the user's contact ID
	 * @return the user with the contact ID
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public User getUserByContactId(long contactId) throws PortalException;

	/**
	 * Returns the user with the email address.
	 *
	 * @param companyId the primary key of the user's company
	 * @param emailAddress the user's email address
	 * @return the user with the email address
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public User getUserByEmailAddress(long companyId, String emailAddress)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public User getUserByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException;

	/**
	 * Returns the user with the primary key.
	 *
	 * @param userId the primary key of the user
	 * @return the user with the primary key
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public User getUserById(long userId) throws PortalException;

	/**
	 * Returns the user with the primary key from the company.
	 *
	 * @param companyId the primary key of the user's company
	 * @param userId the primary key of the user
	 * @return the user with the primary key
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public User getUserById(long companyId, long userId) throws PortalException;

	/**
	 * Returns the user with the screen name.
	 *
	 * @param companyId the primary key of the user's company
	 * @param screenName the user's screen name
	 * @return the user with the screen name
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public User getUserByScreenName(long companyId, String screenName)
		throws PortalException;

	/**
	 * Returns the user with the matching UUID and company.
	 *
	 * @param uuid the user's UUID
	 * @param companyId the primary key of the company
	 * @return the matching user
	 * @throws PortalException if a matching user could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public User getUserByUuidAndCompanyId(String uuid, long companyId)
		throws PortalException;

	/**
	 * Returns the userGroupIds of the user groups associated with the user.
	 *
	 * @param userId the userId of the user
	 * @return long[] the userGroupIds of user groups associated with the user
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long[] getUserGroupPrimaryKeys(long userId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<User> getUserGroupUsers(long userGroupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<User> getUserGroupUsers(long userGroupId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<User> getUserGroupUsers(
		long userGroupId, int start, int end,
		OrderByComparator<User> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getUserGroupUsersCount(long userGroupId);

	/**
	 * Returns the number of users with the status belonging to the user group.
	 *
	 * @param userGroupId the primary key of the user group
	 * @param status the workflow status
	 * @return the number of users with the status belonging to the user group
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getUserGroupUsersCount(long userGroupId, int status)
		throws PortalException;

	/**
	 * Returns the primary key of the user with the email address.
	 *
	 * @param companyId the primary key of the user's company
	 * @param emailAddress the user's email address
	 * @return the primary key of the user with the email address
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long getUserIdByEmailAddress(long companyId, String emailAddress)
		throws PortalException;

	/**
	 * Returns the primary key of the user with the screen name.
	 *
	 * @param companyId the primary key of the user's company
	 * @param screenName the user's screen name
	 * @return the primary key of the user with the screen name
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long getUserIdByScreenName(long companyId, String screenName)
		throws PortalException;

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<User> getUsers(int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<User> getUsers(
		long companyId, int status, int start, int end,
		OrderByComparator<User> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<User> getUsersByRoleId(long roleId, int start, int end)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<User> getUsersByRoleName(
			long companyId, String roleName, int start, int end)
		throws PortalException;

	/**
	 * Returns the number of users.
	 *
	 * @return the number of users
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getUsersCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getUsersCount(long companyId, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasGroupUser(long groupId, long userId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasGroupUsers(long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasOrganizationUser(long organizationId, long userId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasOrganizationUsers(long organizationId);

	/**
	 * Returns <code>true</code> if the password policy has been assigned to the
	 * user.
	 *
	 * @param passwordPolicyId the primary key of the password policy
	 * @param userId the primary key of the user
	 * @return <code>true</code> if the password policy is assigned to the user;
	 <code>false</code> otherwise
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasPasswordPolicyUser(long passwordPolicyId, long userId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasRoleUser(long roleId, long userId);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasRoleUser(
			long companyId, String name, long userId, boolean inherited)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasRoleUsers(long roleId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasTeamUser(long teamId, long userId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasTeamUsers(long teamId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasUserGroupUser(long userGroupId, long userId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasUserGroupUsers(long userGroupId);

	/**
	 * Returns <code>true</code> if the user's password is expired.
	 *
	 * @param user the user
	 * @return <code>true</code> if the user's password is expired;
	 <code>false</code> otherwise
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean isPasswordExpired(User user) throws PortalException;

	/**
	 * Returns the guest user for the company.
	 *
	 * @param companyId the primary key of the company
	 * @return the guest user for the company
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public User loadGetGuestUser(long companyId) throws PortalException;

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<User> search(
		long companyId, String keywords, int status,
		LinkedHashMap<String, Object> params, int start, int end,
		OrderByComparator<User> orderByComparator);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Hits search(
		long companyId, String keywords, int status,
		LinkedHashMap<String, Object> params, int start, int end, Sort sort);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Hits search(
		long companyId, String keywords, int status,
		LinkedHashMap<String, Object> params, int start, int end, Sort[] sorts);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<User> search(
		long companyId, String firstName, String middleName, String lastName,
		String screenName, String emailAddress, int status,
		LinkedHashMap<String, Object> params, boolean andSearch, int start,
		int end, OrderByComparator<User> orderByComparator);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Hits search(
		long companyId, String firstName, String middleName, String lastName,
		String screenName, String emailAddress, int status,
		LinkedHashMap<String, Object> params, boolean andSearch, int start,
		int end, Sort sort);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Hits search(
		long companyId, String firstName, String middleName, String lastName,
		String screenName, String emailAddress, int status,
		LinkedHashMap<String, Object> params, boolean andSearch, int start,
		int end, Sort[] sorts);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<User> searchBySocial(
			long userId, int[] socialRelationTypes, String keywords, int start,
			int end)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<User> searchBySocial(
		long companyId, long[] groupIds, long[] userGroupIds, String keywords,
		int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<User> searchBySocial(
		long companyId, long[] groupIds, long[] userGroupIds, String keywords,
		int start, int end, OrderByComparator<User> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<User> searchBySocial(
			long[] groupIds, long userId, int[] socialRelationTypes,
			String keywords, int start, int end)
		throws PortalException;

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int searchCount(
		long companyId, String keywords, int status,
		LinkedHashMap<String, Object> params);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int searchCount(
		long companyId, String firstName, String middleName, String lastName,
		String screenName, String emailAddress, int status,
		LinkedHashMap<String, Object> params, boolean andSearch);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int searchCountBySocial(
		long companyId, long[] groupIds, long[] userGroupIds, String keywords);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Map<Long, Integer> searchCounts(
		long companyId, int status, long[] groupIds);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BaseModelSearchResult<User> searchUsers(
			long companyId, String keywords, int status,
			LinkedHashMap<String, Object> params, int start, int end, Sort sort)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BaseModelSearchResult<User> searchUsers(
			long companyId, String keywords, int status,
			LinkedHashMap<String, Object> params, int start, int end,
			Sort[] sorts)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BaseModelSearchResult<User> searchUsers(
			long companyId, String firstName, String middleName,
			String lastName, String screenName, String emailAddress, int status,
			LinkedHashMap<String, Object> params, boolean andSearch, int start,
			int end, Sort sort)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BaseModelSearchResult<User> searchUsers(
			long companyId, String firstName, String middleName,
			String lastName, String screenName, String emailAddress, int status,
			LinkedHashMap<String, Object> params, boolean andSearch, int start,
			int end, Sort[] sorts)
		throws PortalException;

	/**
	 * Sends an email address verification to the user.
	 *
	 * @param user the verification email recipient
	 * @param emailAddress the recipient's email address
	 * @param serviceContext the service context to be applied. Must set the
	 portal URL, main path, primary key of the layout, remote address,
	 remote host, and agent for the user.
	 */
	public void sendEmailAddressVerification(
			User user, String emailAddress, ServiceContext serviceContext)
		throws PortalException;

	public boolean sendEmailUserCreationAttempt(
			long companyId, String emailAddress, String fromName,
			String fromAddress, String subject, String body,
			ServiceContext serviceContext)
		throws PortalException;

	public boolean sendPassword(
			long companyId, String emailAddress, String fromName,
			String fromAddress, String subject, String body,
			ServiceContext serviceContext)
		throws PortalException;

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
	public boolean sendPasswordByEmailAddress(
			long companyId, String emailAddress)
		throws PortalException;

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
	public boolean sendPasswordByScreenName(long companyId, String screenName)
		throws PortalException;

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
	public boolean sendPasswordByUserId(long userId) throws PortalException;

	public boolean sendPasswordLockout(
			long companyId, String emailAddress, String fromName,
			String fromAddress, String subject, String body,
			ServiceContext serviceContext)
		throws PortalException;

	public void setGroupUsers(long groupId, long[] userIds);

	public void setOrganizationUsers(long organizationId, long[] userIds);

	/**
	 * @throws PortalException
	 */
	public void setRoleUsers(long roleId, long[] userIds)
		throws PortalException;

	public void setTeamUsers(long teamId, long[] userIds);

	/**
	 * @throws PortalException
	 */
	public void setUserGroupUsers(long userGroupId, long[] userIds)
		throws PortalException;

	/**
	 * Removes the users from the teams of a group.
	 *
	 * @param groupId the primary key of the group
	 * @param userIds the primary keys of the users
	 */
	public void unsetGroupTeamsUsers(long groupId, long[] userIds)
		throws PortalException;

	/**
	 * Removes the users from the group.
	 *
	 * @param groupId the primary key of the group
	 * @param userIds the primary keys of the users
	 * @param serviceContext the service context to be applied (optionally
	 <code>null</code>)
	 */
	public void unsetGroupUsers(
			long groupId, long[] userIds, ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Removes the users from the organization.
	 *
	 * @param organizationId the primary key of the organization
	 * @param userIds the primary keys of the users
	 */
	public void unsetOrganizationUsers(long organizationId, long[] userIds)
		throws PortalException;

	/**
	 * Removes the users from the password policy.
	 *
	 * @param passwordPolicyId the primary key of the password policy
	 * @param userIds the primary keys of the users
	 */
	public void unsetPasswordPolicyUsers(long passwordPolicyId, long[] userIds);

	/**
	 * Removes the users from the role.
	 *
	 * @param roleId the primary key of the role
	 * @param users the users
	 */
	public void unsetRoleUsers(long roleId, List<User> users)
		throws PortalException;

	/**
	 * Removes the users from the role.
	 *
	 * @param roleId the primary key of the role
	 * @param userIds the primary keys of the users
	 */
	public void unsetRoleUsers(long roleId, long[] userIds)
		throws PortalException;

	/**
	 * Removes the users from the team.
	 *
	 * @param teamId the primary key of the team
	 * @param userIds the primary keys of the users
	 */
	public void unsetTeamUsers(long teamId, long[] userIds)
		throws PortalException;

	/**
	 * Removes the users from the user group.
	 *
	 * @param userGroupId the primary key of the user group
	 * @param userIds the primary keys of the users
	 */
	public void unsetUserGroupUsers(long userGroupId, long[] userIds)
		throws PortalException;

	/**
	 * Updates whether the user has agreed to the terms of use.
	 *
	 * @param userId the primary key of the user
	 * @param agreedToTermsOfUse whether the user has agreet to the terms of
	 use
	 * @return the user
	 */
	@CTAware(onProduction = true)
	public User updateAgreedToTermsOfUse(
			long userId, boolean agreedToTermsOfUse)
		throws PortalException;

	/**
	 * Updates the user's asset with the new asset categories and tag names,
	 * removing and adding asset categories and tag names as necessary.
	 *
	 * @param userId the primary key of the user
	 * @param user ID the primary key of the user
	 * @param assetCategoryIds the primary key's of the new asset categories
	 * @param assetTagNames the new asset tag names
	 */
	public void updateAsset(
			long userId, User user, long[] assetCategoryIds,
			String[] assetTagNames)
		throws PortalException;

	/**
	 * Updates the user's creation date.
	 *
	 * @param userId the primary key of the user
	 * @param createDate the new creation date
	 * @return the user
	 */
	public User updateCreateDate(long userId, Date createDate)
		throws PortalException;

	/**
	 * Updates the user's email address.
	 *
	 * @param userId the primary key of the user
	 * @param password the user's password
	 * @param emailAddress1 the user's new email address
	 * @param emailAddress2 the user's new email address confirmation
	 * @return the user
	 */
	@Indexable(type = IndexableType.REINDEX)
	public User updateEmailAddress(
			long userId, String password, String emailAddress1,
			String emailAddress2)
		throws PortalException;

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
	@Indexable(type = IndexableType.REINDEX)
	public User updateEmailAddress(
			long userId, String password, String emailAddress1,
			String emailAddress2, ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Updates whether the user has verified email address.
	 *
	 * @param userId the primary key of the user
	 * @param emailAddressVerified whether the user has verified email address
	 * @return the user
	 */
	@CTAware(onProduction = true)
	public User updateEmailAddressVerified(
			long userId, boolean emailAddressVerified)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public User updateExternalReferenceCode(
			long userId, String externalReferenceCode)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public User updateExternalReferenceCode(
			User user, String externalReferenceCode)
		throws PortalException;

	/**
	 * Sets the groups the user is in, removing and adding groups as necessary.
	 *
	 * @param userId the primary key of the user
	 * @param newGroupIds the primary keys of the groups
	 * @param serviceContext the service context to be applied (optionally
	 <code>null</code>)
	 */
	public void updateGroups(
			long userId, long[] newGroupIds, ServiceContext serviceContext)
		throws PortalException;

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
	public User updateIncompleteUser(
			long creatorUserId, long companyId, boolean autoPassword,
			String password1, String password2, boolean autoScreenName,
			String screenName, String emailAddress, Locale locale,
			String firstName, String middleName, String lastName,
			long prefixListTypeId, long suffixListTypeId, boolean male,
			int birthdayMonth, int birthdayDay, int birthdayYear,
			String jobTitle, boolean updateUserInformation, boolean sendEmail,
			ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Updates the user's job title.
	 *
	 * @param userId the primary key of the user
	 * @param jobTitle the user's job title
	 * @return the user
	 */
	@Indexable(type = IndexableType.REINDEX)
	public User updateJobTitle(long userId, String jobTitle)
		throws PortalException;

	@CTAware(onProduction = true)
	public User updateLanguageId(long userId, String languageId)
		throws PortalException;

	/**
	 * Updates the user's last login with the current time and the IP address.
	 *
	 * @param userId the primary key of the user
	 * @param loginIP the IP address the user logged in from
	 * @return the user
	 */
	@Indexable(
		callbackKey = "com.liferay.portal.kernel.model.User#lastLoginDate",
		type = IndexableType.REINDEX
	)
	@Transactional(enabled = false)
	public User updateLastLogin(long userId, String loginIP)
		throws PortalException;

	@Indexable(
		callbackKey = "com.liferay.portal.kernel.model.User#lastLoginDate",
		type = IndexableType.REINDEX
	)
	@Transactional(enabled = false)
	public User updateLastLogin(User user, String loginIP)
		throws PortalException;

	/**
	 * Updates whether the user is locked out from logging in.
	 *
	 * @param user the user
	 * @param lockout whether the user is locked out
	 * @return the user
	 */
	public User updateLockout(User user, boolean lockout)
		throws PortalException;

	/**
	 * Updates whether the user is locked out from logging in.
	 *
	 * @param companyId the primary key of the user's company
	 * @param emailAddress the user's email address
	 * @param lockout whether the user is locked out
	 * @return the user
	 */
	public User updateLockoutByEmailAddress(
			long companyId, String emailAddress, boolean lockout)
		throws PortalException;

	/**
	 * Updates whether the user is locked out from logging in.
	 *
	 * @param userId the primary key of the user
	 * @param lockout whether the user is locked out
	 * @return the user
	 */
	public User updateLockoutById(long userId, boolean lockout)
		throws PortalException;

	/**
	 * Updates whether the user is locked out from logging in.
	 *
	 * @param companyId the primary key of the user's company
	 * @param screenName the user's screen name
	 * @param lockout whether the user is locked out
	 * @return the user
	 */
	public User updateLockoutByScreenName(
			long companyId, String screenName, boolean lockout)
		throws PortalException;

	/**
	 * Updates the user's modified date.
	 *
	 * @param userId the primary key of the user
	 * @param modifiedDate the new modified date
	 * @return the user
	 */
	@Indexable(type = IndexableType.REINDEX)
	public User updateModifiedDate(long userId, Date modifiedDate)
		throws PortalException;

	/**
	 * Sets the organizations that the user is in, removing and adding
	 * organizations as necessary.
	 *
	 * @param userId the primary key of the user
	 * @param newOrganizationIds the primary keys of the organizations
	 * @param serviceContext the service context to be applied. Must set whether
	 user indexing is enabled.
	 */
	public void updateOrganizations(
			long userId, long[] newOrganizationIds,
			ServiceContext serviceContext)
		throws PortalException;

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
	@CTAware(onProduction = true)
	public User updatePassword(
			long userId, String password1, String password2,
			boolean passwordReset)
		throws PortalException;

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
	@CTAware(onProduction = true)
	public User updatePassword(
			long userId, String password1, String password2,
			boolean passwordReset, boolean silentUpdate)
		throws PortalException;

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
	public User updatePasswordManually(
			long userId, String password, boolean passwordEncrypted,
			boolean passwordReset, Date passwordModifiedDate)
		throws PortalException;

	/**
	 * Updates whether the user should be asked to reset their password the next
	 * time they login.
	 *
	 * @param userId the primary key of the user
	 * @param passwordReset whether the user should be asked to reset their
	 password the next time they login
	 * @return the user
	 */
	@CTAware(onProduction = true)
	public User updatePasswordReset(long userId, boolean passwordReset)
		throws PortalException;

	/**
	 * Updates the user's portrait image.
	 *
	 * @param userId the primary key of the user
	 * @param bytes the new portrait image data
	 * @return the user
	 */
	public User updatePortrait(long userId, byte[] bytes)
		throws PortalException;

	/**
	 * Updates the user's password reset question and answer.
	 *
	 * @param userId the primary key of the user
	 * @param question the user's new password reset question
	 * @param answer the user's new password reset answer
	 * @return the user
	 */
	@CTAware(onProduction = true)
	public User updateReminderQuery(long userId, String question, String answer)
		throws PortalException;

	/**
	 * Updates the user's screen name.
	 *
	 * @param userId the primary key of the user
	 * @param screenName the user's new screen name
	 * @return the user
	 */
	@Indexable(type = IndexableType.REINDEX)
	public User updateScreenName(long userId, String screenName)
		throws PortalException;

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
	public User updateStatus(
			long userId, int status, ServiceContext serviceContext)
		throws PortalException;

	public User updateStatus(
			User user, int status, ServiceContext serviceContext)
		throws PortalException;

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
			ServiceContext serviceContext)
		throws PortalException;

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
	@Indexable(type = IndexableType.REINDEX)
	public User updateUser(User user);

	public void validateMaxUsers(long companyId) throws PortalException;

	/**
	 * Verifies the email address of the ticket.
	 *
	 * @param ticketKey the ticket key
	 */
	public void verifyEmailAddress(String ticketKey) throws PortalException;

	@Override
	@Transactional(enabled = false)
	public CTPersistence<User> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<User> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<User>, R, E> updateUnsafeFunction)
		throws E;

}