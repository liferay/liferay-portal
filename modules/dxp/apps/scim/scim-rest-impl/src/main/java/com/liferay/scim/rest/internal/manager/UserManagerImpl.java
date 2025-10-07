/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.rest.internal.manager;

import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.model.ExpandoTableConstants;
import com.liferay.expando.kernel.model.ExpandoValue;
import com.liferay.expando.kernel.model.ExpandoValueTable;
import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.expando.kernel.service.ExpandoTableLocalService;
import com.liferay.expando.kernel.service.ExpandoValueLocalService;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.sql.dsl.DSLFunctionFactoryUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.base.BaseTable;
import com.liferay.petra.sql.dsl.expression.Expression;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.WebsiteURLException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.UserGroupTable;
import com.liferay.portal.kernel.model.UserTable;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.AddressLocalService;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ContactLocalService;
import com.liferay.portal.kernel.service.CountryLocalService;
import com.liferay.portal.kernel.service.EmailAddressLocalService;
import com.liferay.portal.kernel.service.ListTypeLocalService;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.service.PhoneLocalService;
import com.liferay.portal.kernel.service.RegionLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserGroupService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserService;
import com.liferay.portal.kernel.service.WebsiteLocalService;
import com.liferay.portal.kernel.service.permission.UserPermissionUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.service.permission.UserGroupPermissionUtil;
import com.liferay.scim.rest.internal.configuration.ScimClientOAuth2ApplicationConfiguration;
import com.liferay.scim.rest.internal.model.ScimUser;
import com.liferay.scim.rest.internal.util.ScimUtil;
import com.liferay.scim.rest.util.ScimClientUtil;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

import org.osgi.service.cm.ConfigurationAdmin;

import org.wso2.charon3.core.exceptions.AbstractCharonException;
import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.ConflictException;
import org.wso2.charon3.core.exceptions.NotFoundException;
import org.wso2.charon3.core.exceptions.NotImplementedException;
import org.wso2.charon3.core.extensions.UserManager;
import org.wso2.charon3.core.objects.Group;
import org.wso2.charon3.core.objects.User;
import org.wso2.charon3.core.objects.plainobjects.GroupsGetResponse;
import org.wso2.charon3.core.objects.plainobjects.MultiValuedComplexType;
import org.wso2.charon3.core.objects.plainobjects.ScimAddress;
import org.wso2.charon3.core.objects.plainobjects.UsersGetResponse;
import org.wso2.charon3.core.utils.codeutils.ExpressionNode;
import org.wso2.charon3.core.utils.codeutils.Node;
import org.wso2.charon3.core.utils.codeutils.SearchRequest;

/**
 * @author Rafael Praxedes
 * @author Olivér Kecskeméty
 */
public class UserManagerImpl implements UserManager {

	public UserManagerImpl(
		AddressLocalService addressLocalService,
		ClassNameLocalService classNameLocalService,
		CompanyLocalService companyLocalService,
		ConfigurationAdmin configurationAdmin,
		ContactLocalService contactLocalService,
		CounterLocalService counterLocalService,
		CountryLocalService countryLocalService,
		EmailAddressLocalService emailAddressLocalService,
		ExpandoColumnLocalService expandoColumnLocalService,
		ExpandoTableLocalService expandoTableLocalService,
		ExpandoValueLocalService expandoValueLocalService,
		ListTypeLocalService listTypeLocalService,
		PhoneLocalService phoneLocalService,
		RegionLocalService regionLocalService,
		UserGroupLocalService userGroupLocalService,
		UserGroupService userGroupService, UserLocalService userLocalService,
		UserService userService, WebsiteLocalService websiteLocalService) {

		_addressLocalService = addressLocalService;
		_classNameLocalService = classNameLocalService;
		_companyLocalService = companyLocalService;
		_configurationAdmin = configurationAdmin;
		_contactLocalService = contactLocalService;
		_counterLocalService = counterLocalService;
		_countryLocalService = countryLocalService;
		_emailAddressLocalService = emailAddressLocalService;
		_expandoColumnLocalService = expandoColumnLocalService;
		_expandoTableLocalService = expandoTableLocalService;
		_expandoValueLocalService = expandoValueLocalService;
		_listTypeLocalService = listTypeLocalService;
		_phoneLocalService = phoneLocalService;
		_regionLocalService = regionLocalService;
		_userGroupLocalService = userGroupLocalService;
		_userGroupService = userGroupService;
		_userLocalService = userLocalService;
		_userService = userService;
		_websiteLocalService = websiteLocalService;
	}

	@Override
	public Group createGroup(
			Group group, Map<String, Boolean> requiredAttributes)
		throws CharonException {

		return _addOrUpdateGroup(group);
	}

	@Override
	public User createMe(User user, Map<String, Boolean> requiredAttributes)
		throws NotImplementedException {

		throw new NotImplementedException();
	}

	@Override
	public User createUser(User user, Map<String, Boolean> requiredAttributes)
		throws CharonException {

		return _addOrUpdateUser(user);
	}

	@Override
	public void deleteGroup(String groupId)
		throws CharonException, NotFoundException {

		try {
			getGroup(groupId, Collections.emptyMap());

			TransactionInvokerUtil.invoke(
				_transactionConfig,
				() -> {
					_userService.setUserGroupUsers(
						GetterUtil.getLong(groupId), new long[0]);

					_userGroupService.deleteUserGroup(
						GetterUtil.getLong(groupId));

					return null;
				});
		}
		catch (AbstractCharonException abstractCharonException) {
			ReflectionUtil.throwException(abstractCharonException);
		}
		catch (PrincipalException principalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(principalException);
			}

			throw new NotFoundException();
		}
		catch (Throwable throwable) {
			throw new CharonException(
				"Unable to delete group with group ID " + groupId, throwable);
		}
	}

	@Override
	public void deleteMe(String userName) throws NotImplementedException {
		throw new NotImplementedException();
	}

	@Override
	public void deleteUser(String userId) throws CharonException {
		try {
			_getScimUser(
				CompanyThreadLocal.getCompanyId(), GetterUtil.getLong(userId));

			_userService.updateStatus(
				GetterUtil.getLong(userId), WorkflowConstants.STATUS_INACTIVE,
				new ServiceContext());
		}
		catch (AbstractCharonException abstractCharonException) {
			ReflectionUtil.throwException(abstractCharonException);
		}
		catch (Exception exception) {
			throw new CharonException(
				"Unable to delete user with user ID " + userId, exception);
		}
	}

	@Override
	public Group getGroup(
		String groupId, Map<String, Boolean> requiredAttributes) {

		try {
			UserGroup userGroup = _getUserGroup(
				CompanyThreadLocal.getCompanyId(), GetterUtil.getLong(groupId));

			return ScimUtil.toGroup(
				_getScimUsers(
					CompanyThreadLocal.getCompanyId(),
					userGroup.getUserGroupId()),
				userGroup);
		}
		catch (Exception exception) {
			return ReflectionUtil.throwException(exception);
		}
	}

	@Override
	public User getMe(String userName, Map<String, Boolean> requiredAttributes)
		throws NotImplementedException {

		throw new NotImplementedException();
	}

	@Override
	public User getUser(
		String userId, Map<String, Boolean> requiredAttributes) {

		try {
			ScimUser scimUser = _getScimUser(
				CompanyThreadLocal.getCompanyId(), GetterUtil.getLong(userId));

			if (!scimUser.isActive()) {
				throw new NotFoundException(
					"No user found with user ID " + userId);
			}

			return ScimUtil.toUser(
				_getGroups(
					CompanyThreadLocal.getCompanyId(),
					GetterUtil.getLong(scimUser.getId())),
				scimUser);
		}
		catch (Exception exception) {
			return ReflectionUtil.throwException(exception);
		}
	}

	@Override
	public GroupsGetResponse listGroupsWithGET(
			Node node, Integer startIndex, Integer count, String sortBy,
			String sortOrder, String domainName,
			Map<String, Boolean> requiredAttributes)
		throws BadRequestException {

		GroupsGetResponse groupsGetResponse = new GroupsGetResponse(
			0, Collections.emptyList());

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(
				_userLocalService.fetchUser(PrincipalThreadLocal.getUserId()));

		_buildGetResponse(
			UserGroupTable.INSTANCE,
			(totalGroups, groups) -> {
				groupsGetResponse.setGroups(groups);
				groupsGetResponse.setTotalGroups(totalGroups);
			},
			UserGroup.class, count, UserGroupTable.INSTANCE.userGroupId,
			new String[] {"displayName"}, node, _userGroupLocalService,
			startIndex,
			userGroup -> {
				if (!UserGroupPermissionUtil.contains(
						permissionChecker, userGroup.getUserGroupId(),
						ActionKeys.VIEW)) {

					return null;
				}

				return ScimUtil.toGroup(
					_getScimUsers(
						userGroup.getCompanyId(), userGroup.getUserGroupId()),
					userGroup);
			});

		return groupsGetResponse;
	}

	@Override
	public GroupsGetResponse listGroupsWithPost(
			SearchRequest searchRequest,
			Map<String, Boolean> requiredAttributes)
		throws NotImplementedException {

		throw new NotImplementedException();
	}

	@Override
	public UsersGetResponse listUsersWithGET(
			Node node, Integer startIndex, Integer count, String sortBy,
			String sortOrder, String domainName,
			Map<String, Boolean> requiredAttributes)
		throws BadRequestException {

		UsersGetResponse usersGetResponse = new UsersGetResponse(
			0, Collections.emptyList());

		com.liferay.portal.kernel.model.User portalUser =
			_userLocalService.fetchUser(PrincipalThreadLocal.getUserId());

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(portalUser);

		_buildGetResponse(
			UserTable.INSTANCE,
			(totalUsers, users) -> {
				usersGetResponse.setTotalUsers(totalUsers);
				usersGetResponse.setUsers(users);
			},
			com.liferay.portal.kernel.model.User.class, count,
			UserTable.INSTANCE.userId, new String[] {"externalId", "userName"},
			node, _userLocalService, startIndex,
			user -> {
				if (!UserPermissionUtil.contains(
						permissionChecker, user.getUserId(), ActionKeys.VIEW)) {

					return null;
				}

				return ScimUtil.toUser(
					_getGroups(user.getCompanyId(), user.getUserId()),
					ScimUtil.toScimUser(
						_userService.getUserById(user.getUserId())));
			});

		return usersGetResponse;
	}

	@Override
	public UsersGetResponse listUsersWithPost(
			SearchRequest searchRequest,
			Map<String, Boolean> requiredAttributes)
		throws NotImplementedException {

		throw new NotImplementedException();
	}

	@Override
	public void updateGroup(Group oldGroup, Group newGroup)
		throws CharonException {

		_addOrUpdateGroup(newGroup);
	}

	@Override
	public Group updateGroup(
			Group oldGroup, Group newGroup,
			Map<String, Boolean> requiredAttributes)
		throws CharonException {

		return _addOrUpdateGroup(newGroup);
	}

	@Override
	public User updateMe(
			User updatedUser, Map<String, Boolean> requiredAttributes)
		throws NotImplementedException {

		throw new NotImplementedException();
	}

	@Override
	public User updateUser(User user, Map<String, Boolean> requiredAttributes)
		throws CharonException {

		return _addOrUpdateUser(user);
	}

	private void _addOrUpdateExpandoValue(
			String name, com.liferay.portal.kernel.model.User portalUser,
			boolean textBox, String value)
		throws Exception {

		long classNameId = ClassNameLocalServiceUtil.getClassNameId(
			com.liferay.portal.kernel.model.User.class.getName());

		ExpandoColumn expandoColumn = _getOrAddExpandoColumn(
			classNameId, portalUser.getCompanyId(), name, textBox, false);

		_expandoValueLocalService.addValue(
			classNameId, expandoColumn.getTableId(),
			expandoColumn.getColumnId(), portalUser.getUserId(), value);
	}

	private Group _addOrUpdateGroup(Group group) throws CharonException {
		try {
			Company company = _companyLocalService.fetchCompany(
				CompanyThreadLocal.getCompanyId());

			return TransactionInvokerUtil.invoke(
				_transactionConfig,
				() -> {
					UserGroup userGroup = _addOrUpdateUserGroup(company, group);

					return ScimUtil.toGroup(
						_getScimUsers(
							userGroup.getCompanyId(),
							userGroup.getUserGroupId()),
						userGroup);
				});
		}
		catch (AbstractCharonException abstractCharonException) {
			return ReflectionUtil.throwException(abstractCharonException);
		}
		catch (Throwable throwable) {
			throw new CharonException(
				"Unable to provision a portal group for " +
					group.getDisplayName(),
				throwable);
		}
	}

	private ScimUser _addOrUpdateScimUser(ScimUser scimUser) throws Exception {
		Company company = _companyLocalService.getCompany(
			scimUser.getCompanyId());

		ScimClientOAuth2ApplicationConfiguration
			scimClientOAuth2ApplicationConfiguration =
				ScimUtil.getScimClientOAuth2ApplicationConfiguration(
					company.getCompanyId(), _configurationAdmin);

		com.liferay.portal.kernel.model.User portalUser = _fetchPortalUser(
			scimClientOAuth2ApplicationConfiguration, scimUser);

		Calendar birthdayCalendar = CalendarFactoryUtil.getCalendar();

		birthdayCalendar.setTime(scimUser.getBirthday());

		int birthdayMonth = birthdayCalendar.get(Calendar.MONTH);
		int birthdayDay = birthdayCalendar.get(Calendar.DAY_OF_MONTH);
		int birthdayYear = birthdayCalendar.get(Calendar.YEAR);

		if (portalUser == null) {
			portalUser = _addPortalUser(
				birthdayMonth, birthdayDay, birthdayYear,
				scimClientOAuth2ApplicationConfiguration, scimUser);
		}
		else {
			portalUser = _updatePortalUser(
				birthdayMonth, birthdayDay, birthdayYear, portalUser, scimUser,
				scimClientOAuth2ApplicationConfiguration);
		}

		if (!FeatureFlagManagerUtil.isEnabled("LPD-56434")) {
			return ScimUtil.toScimUser(portalUser);
		}

		_addOrUpdateExpandoValue(
			"scimDisplayName", portalUser, false, scimUser.getDisplayName());
		_addOrUpdateExpandoValue(
			"scimEntitlements", portalUser, true,
			ArrayUtil.toString(
				scimUser.getEntitlements(), StringPool.BLANK,
				StringPool.NEW_LINE));
		_addOrUpdateExpandoValue(
			"scimNickName", portalUser, false, scimUser.getNickName());
		_addOrUpdateExpandoValue(
			"scimPhotos", portalUser, true,
			ArrayUtil.toString(
				scimUser.getPhotos(), StringPool.BLANK, StringPool.NEW_LINE));
		_addOrUpdateExpandoValue(
			"scimPreferredLanguage", portalUser, false,
			scimUser.getPreferredLanguage());
		_addOrUpdateExpandoValue(
			"scimUserType", portalUser, false, scimUser.getUserType());
		_addOrUpdateExpandoValue(
			"scimX509Certificates", portalUser, true,
			ArrayUtil.toString(
				scimUser.getX509Certificates(), StringPool.BLANK,
				StringPool.NEW_LINE));

		_addressLocalService.deleteAddresses(
			portalUser.getCompanyId(), Contact.class.getName(),
			portalUser.getContactId());

		for (ScimAddress scimAddress : scimUser.getAddresses()) {
			Address address = _addressLocalService.createAddress(
				_counterLocalService.increment());

			address.setUserId(portalUser.getUserId());
			address.setUserName(portalUser.getFullName());
			address.setClassName(Contact.class.getName());
			address.setClassPK(portalUser.getContactId());

			Country country = _countryLocalService.getCountryByA2(
				portalUser.getCompanyId(), scimAddress.getCountry());

			address.setCountryId(country.getCountryId());

			if (Validator.isNull(scimAddress.getType())) {
				scimAddress.setType("other");
			}

			address.setListTypeId(
				_listTypeLocalService.getListTypeId(
					portalUser.getCompanyId(), scimAddress.getType(),
					Contact.class.getName() + ".address"));

			for (Region region :
					_regionLocalService.getRegions(
						country.getCountryId(), true)) {

				if (Objects.equals(region.getName(), scimAddress.getRegion())) {
					address.setRegionId(region.getRegionId());

					break;
				}
			}

			address.setCity(scimAddress.getLocality());
			address.setPrimary(scimAddress.isPrimary());

			String[] streetAddressParts = StringUtil.split(
				scimAddress.getStreetAddress(), "\n");

			address.setStreet1(streetAddressParts[0]);

			if (streetAddressParts.length > 1) {
				address.setStreet2(streetAddressParts[1]);
			}

			if (streetAddressParts.length > 2) {
				address.setStreet3(streetAddressParts[2]);
			}

			address.setZip(scimAddress.getPostalCode());

			_addressLocalService.addAddress(address);
		}

		Contact contact = portalUser.getContact();

		Map<String, String> ims = scimUser.getIMs();

		String jabberSn = ims.get("Jabber");

		if (Validator.isNotNull(jabberSn)) {
			contact.setJabberSn(jabberSn);
		}

		String skypeSn = ims.get("Skype");

		if (Validator.isNotNull(skypeSn)) {
			contact.setSkypeSn(skypeSn);
		}

		contact = _contactLocalService.updateContact(contact);

		portalUser.setContact(contact);

		_emailAddressLocalService.deleteEmailAddresses(
			portalUser.getCompanyId(), Contact.class.getName(),
			portalUser.getContactId());

		long listTypeId = _listTypeLocalService.getListTypeId(
			portalUser.getCompanyId(), "email-address",
			Contact.class.getName() + ".emailAddress");

		boolean primary = true;

		for (String emailAddress : scimUser.getEmailAddresses()) {
			ServiceContext serviceContext = new ServiceContext();

			_emailAddressLocalService.addEmailAddress(
				serviceContext.getUuidWithoutReset(), portalUser.getUserId(),
				Contact.class.getName(), portalUser.getContactId(),
				emailAddress, listTypeId, primary, serviceContext);

			primary = false;
		}

		_phoneLocalService.deletePhones(
			portalUser.getCompanyId(), Contact.class.getName(),
			portalUser.getContactId());

		for (MultiValuedComplexType multiValuedComplexType :
				scimUser.getPhoneNumberMultiValuedComplexTypes()) {

			listTypeId = _listTypeLocalService.getListTypeId(
				portalUser.getCompanyId(),
				StringUtil.toLowerCase(multiValuedComplexType.getType()),
				Contact.class.getName() + ".phone");

			_phoneLocalService.addPhone(
				null, portalUser.getUserId(), Contact.class.getName(),
				portalUser.getContactId(), multiValuedComplexType.getValue(),
				null, listTypeId, multiValuedComplexType.isPrimary(),
				new ServiceContext());
		}

		_websiteLocalService.deleteWebsites(
			portalUser.getCompanyId(), Contact.class.getName(),
			portalUser.getContactId());

		if (Validator.isNotNull(scimUser.getProfileUrl())) {
			listTypeId = _listTypeLocalService.getListTypeId(
				portalUser.getCompanyId(), "personal",
				Contact.class.getName() + ".website");

			try {
				_websiteLocalService.addWebsite(
					null, portalUser.getUserId(), Contact.class.getName(),
					portalUser.getContactId(), scimUser.getProfileUrl(),
					listTypeId, true, new ServiceContext());
			}
			catch (WebsiteURLException websiteURLException) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"Unable to create website URL", websiteURLException);
				}
			}
		}

		return ScimUtil.toScimUser(portalUser);
	}

	private User _addOrUpdateUser(User user) throws CharonException {
		try {
			Company company = _companyLocalService.fetchCompany(
				CompanyThreadLocal.getCompanyId());

			return TransactionInvokerUtil.invoke(
				_transactionConfig,
				() -> {
					ScimUser scimUser = _addOrUpdateScimUser(
						ScimUtil.toScimUser(
							company.getCompanyId(), company.getLocale(), user));

					return ScimUtil.toUser(
						_getGroups(
							company.getCompanyId(),
							GetterUtil.getLong(scimUser.getId())),
						scimUser);
				});
		}
		catch (AbstractCharonException abstractCharonException) {
			return ReflectionUtil.throwException(abstractCharonException);
		}
		catch (Throwable throwable) {
			throw new CharonException(
				"Unable to provision a portal user for " +
					user.getDisplayName(),
				throwable);
		}
	}

	private UserGroup _addOrUpdateUserGroup(Company company, Group group)
		throws Exception {

		ScimClientOAuth2ApplicationConfiguration
			scimClientOAuth2ApplicationConfiguration =
				ScimUtil.getScimClientOAuth2ApplicationConfiguration(
					company.getCompanyId(), _configurationAdmin);

		UserGroup userGroup = _fetchUserGroup(
			company.getCompanyId(), group.getExternalId(),
			GetterUtil.getLong(group.getId()));

		if (userGroup == null) {
			userGroup = _userGroupService.addUserGroup(
				group.getExternalId(), group.getDisplayName(), null,
				new ServiceContext());

			_saveScimClientId(
				UserGroup.class.getName(), userGroup.getPrimaryKey(),
				userGroup.getCompanyId(),
				ScimClientUtil.generateScimClientId(
					scimClientOAuth2ApplicationConfiguration.
						oAuth2ApplicationName()));
		}
		else {
			String scimClientId = ScimClientUtil.generateScimClientId(
				scimClientOAuth2ApplicationConfiguration.
					oAuth2ApplicationName());
			String userGroupScimClientId = _getScimClientId(
				UserGroup.class.getName(), userGroup.getPrimaryKey(),
				userGroup.getCompanyId());

			if (Validator.isNotNull(userGroupScimClientId) &&
				!Objects.equals(scimClientId, userGroupScimClientId)) {

				throw new ConflictException(
					"Group was provisioned by another SCIM client");
			}

			userGroup = _userGroupService.updateUserGroup(
				group.getExternalId(), userGroup.getPrimaryKey(),
				group.getDisplayName(), userGroup.getDescription(),
				new ServiceContext());

			if (Validator.isNull(userGroupScimClientId)) {
				_saveScimClientId(
					UserGroup.class.getName(), userGroup.getPrimaryKey(),
					userGroup.getCompanyId(), scimClientId);
			}
		}

		_updateUserGroupUsers(
			userGroup.getCompanyId(), group, userGroup.getUserGroupId());

		return userGroup;
	}

	private com.liferay.portal.kernel.model.User _addPortalUser(
			int birthdayMonth, int birthdayDay, int birthdayYear,
			ScimClientOAuth2ApplicationConfiguration
				scimClientOAuth2ApplicationConfiguration,
			ScimUser scimUser)
		throws Exception {

		com.liferay.portal.kernel.model.User portalUser = _userService.addUser(
			scimUser.getCompanyId(), scimUser.isAutoPassword(),
			scimUser.getPassword(), scimUser.getPassword(),
			scimUser.isAutoScreenName(), scimUser.getScreenName(),
			scimUser.getEmailAddresses()[0], scimUser.getLocale(),
			scimUser.getFirstName(), scimUser.getMiddleName(),
			scimUser.getLastName(), scimUser.getPrefix(), scimUser.getSuffix(),
			scimUser.isMale(), birthdayMonth, birthdayDay, birthdayYear,
			scimUser.getJobTitle(), scimUser.getGroupIds(),
			scimUser.getOrganizationIds(), scimUser.getRoleIds(),
			scimUser.getUserGroupIds(), scimUser.isSendEmail(),
			new ServiceContext());

		portalUser.setExternalReferenceCode(
			scimUser.getExternalReferenceCode());

		if (FeatureFlagManagerUtil.isEnabled("LPD-56434") &&
			Validator.isNotNull(scimUser.getTimeZoneId())) {

			portalUser.setTimeZoneId(scimUser.getTimeZoneId());
		}

		portalUser = _userLocalService.updateUser(portalUser);

		portalUser = _userLocalService.updateEmailAddressVerified(
			portalUser.getUserId(), true);

		_saveScimClientId(
			com.liferay.portal.kernel.model.User.class.getName(),
			portalUser.getUserId(), portalUser.getCompanyId(),
			ScimClientUtil.generateScimClientId(
				scimClientOAuth2ApplicationConfiguration.
					oAuth2ApplicationName()));

		return portalUser;
	}

	private <T extends BaseTable<T>, U, E extends Throwable, R> void
			_buildGetResponse(
				BaseTable<T> baseTable, BiConsumer<Integer, List<R>> biConsumer,
				Class<U> clazz, Integer pageSize, Expression<Long> expression,
				String[] fieldNames, Node node,
				PersistedModelLocalService persistedModelLocalService,
				Integer startIndex, UnsafeFunction<U, R, E> unsafeFunction)
		throws BadRequestException {

		if ((pageSize != null) && (pageSize < 0)) {
			pageSize = 0;
		}

		startIndex--;

		_validate(node, fieldNames);

		Predicate predicate = _buildWherePredicate(
			baseTable, clazz.getName(), node,
			ServiceContextThreadLocal.getServiceContext());

		int count = persistedModelLocalService.dslQueryCount(
			DSLQueryFactoryUtil.count(
			).from(
				baseTable
			).innerJoinON(
				ExpandoValueTable.INSTANCE,
				ExpandoValueTable.INSTANCE.classPK.eq(expression)
			).where(
				predicate
			));

		if (pageSize == null) {
			pageSize = count;
		}

		biConsumer.accept(
			count,
			TransformUtil.transform(
				(List<U>)persistedModelLocalService.dslQuery(
					DSLQueryFactoryUtil.select(
						baseTable
					).from(
						baseTable
					).innerJoinON(
						ExpandoValueTable.INSTANCE,
						ExpandoValueTable.INSTANCE.classPK.eq(expression)
					).where(
						predicate
					).limit(
						startIndex, startIndex + pageSize
					)),
				model -> unsafeFunction.apply(model)));
	}

	private <T extends BaseTable<T>> Predicate _buildWherePredicate(
		BaseTable<T> baseTable, String className, Node node,
		ServiceContext serviceContext) {

		ExpandoColumn expandoColumn = null;

		try {
			expandoColumn = _getOrAddExpandoColumn(
				_classNameLocalService.getClassNameId(className),
				serviceContext.getCompanyId(), "scimClientId", false, true);
		}
		catch (Exception exception) {
			ReflectionUtil.throwException(exception);
		}

		Predicate predicate = ExpandoValueTable.INSTANCE.columnId.eq(
			expandoColumn.getColumnId()
		).and(
			DSLFunctionFactoryUtil.castClobText(
				ExpandoValueTable.INSTANCE.data
			).eq(
				ScimClientUtil.generateScimClientId(
					ScimUtil.getScimClientOAuth2ApplicationConfiguration(
						serviceContext.getCompanyId(), _configurationAdmin
					).oAuth2ApplicationName())
			)
		);

		ExpressionNode expressionNode = (ExpressionNode)node;

		if (expressionNode == null) {
			return predicate;
		}

		for (Map.Entry<String, String> entry : _columnNames.entrySet()) {
			if (StringUtil.contains(
					expressionNode.getAttributeValue(), entry.getKey(),
					StringPool.COLON)) {

				Expression<String> expression =
					(Expression<String>)baseTable.getColumn(entry.getValue());

				predicate = predicate.and(
					expression.eq(expressionNode.getValue()));
			}
		}

		return predicate;
	}

	private com.liferay.portal.kernel.model.User _fetchPortalUser(
		ScimClientOAuth2ApplicationConfiguration
			scimClientOAuth2ApplicationConfiguration,
		ScimUser scimUser) {

		com.liferay.portal.kernel.model.User portalUser =
			_userLocalService.fetchUserByExternalReferenceCode(
				scimUser.getExternalReferenceCode(), scimUser.getCompanyId());

		if (portalUser != null) {
			return portalUser;
		}

		if (Objects.equals(
				scimClientOAuth2ApplicationConfiguration.matcherField(),
				"email")) {

			return _userLocalService.fetchUserByEmailAddress(
				scimUser.getCompanyId(), scimUser.getEmailAddresses()[0]);
		}
		else if (Objects.equals(
					scimClientOAuth2ApplicationConfiguration.matcherField(),
					"userName")) {

			return _userLocalService.fetchUserByScreenName(
				scimUser.getCompanyId(), scimUser.getScreenName());
		}

		return null;
	}

	private UserGroup _fetchUserGroup(
		long companyId, String externalReferenceCode, long userGroupId) {

		UserGroup userGroup =
			_userGroupLocalService.fetchUserGroupByExternalReferenceCode(
				externalReferenceCode, companyId);

		if (userGroup != null) {
			return userGroup;
		}

		return _userGroupLocalService.fetchUserGroup(userGroupId);
	}

	private List<Group> _getGroups(long companyId, long userId) {
		String userScimClientId = _getScimClientId(
			com.liferay.portal.kernel.model.User.class.getName(), userId,
			companyId);

		return TransformUtil.transform(
			_userGroupLocalService.getUserUserGroups(userId),
			userGroup -> {
				String userGroupScimClientId = _getScimClientId(
					UserGroup.class.getName(), userGroup.getUserGroupId(),
					userGroup.getCompanyId());

				if (!Objects.equals(userGroupScimClientId, userScimClientId)) {
					return null;
				}

				return ScimUtil.toGroup(Collections.emptyList(), userGroup);
			});
	}

	private ExpandoColumn _getOrAddExpandoColumn(
			long classNameId, long companyId, String name, boolean textBox,
			boolean hidden)
		throws Exception {

		ExpandoTable expandoTable = _expandoTableLocalService.fetchTable(
			companyId, classNameId, ExpandoTableConstants.DEFAULT_TABLE_NAME);

		if (expandoTable == null) {
			expandoTable = _expandoTableLocalService.addTable(
				companyId, classNameId,
				ExpandoTableConstants.DEFAULT_TABLE_NAME);
		}

		ExpandoColumn expandoColumn = _expandoColumnLocalService.fetchColumn(
			expandoTable.getTableId(), name);

		if (expandoColumn != null) {
			return expandoColumn;
		}

		expandoColumn = _expandoColumnLocalService.addColumn(
			expandoTable.getTableId(), name, ExpandoColumnConstants.STRING);

		UnicodeProperties unicodeProperties =
			expandoColumn.getTypeSettingsProperties();

		unicodeProperties.setProperty(
			ExpandoColumnConstants.INDEX_TYPE,
			String.valueOf(ExpandoColumnConstants.INDEX_TYPE_KEYWORD));

		if (textBox) {
			unicodeProperties.setProperty(
				ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE,
				ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE_TEXT_BOX);
			unicodeProperties.setProperty(
				ExpandoColumnConstants.PROPERTY_HEIGHT, "150");
		}
		else {
			unicodeProperties.setProperty(
				ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE,
				ExpandoColumnConstants.PROPERTY_DISPLAY_TYPE_INPUT_FIELD);
		}

		if (hidden) {
			unicodeProperties.setProperty(
				ExpandoColumnConstants.PROPERTY_HIDDEN,
				Boolean.TRUE.toString());
		}

		unicodeProperties.setProperty(
			ExpandoColumnConstants.PROPERTY_WIDTH, "400");

		expandoColumn.setTypeSettingsProperties(unicodeProperties);

		return _expandoColumnLocalService.updateExpandoColumn(expandoColumn);
	}

	private String _getScimClientId(
		String className, long classPK, long companyId) {

		ExpandoTable expandoTable = _expandoTableLocalService.fetchTable(
			companyId, _classNameLocalService.getClassNameId(className),
			ExpandoTableConstants.DEFAULT_TABLE_NAME);

		if (expandoTable == null) {
			return StringPool.BLANK;
		}

		ExpandoColumn expandoColumn = _expandoColumnLocalService.fetchColumn(
			expandoTable.getTableId(), "scimClientId");

		if (expandoColumn == null) {
			return StringPool.BLANK;
		}

		ExpandoValue expandoValue = _expandoValueLocalService.getValue(
			expandoTable.getTableId(), expandoColumn.getColumnId(), classPK);

		if (expandoValue == null) {
			return StringPool.BLANK;
		}

		return expandoValue.getData();
	}

	private ScimUser _getScimUser(long companyId, long userId)
		throws Exception {

		com.liferay.portal.kernel.model.User portalUser = null;

		try {
			portalUser = _userService.getUserById(userId);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			throw new NotFoundException();
		}

		String userScimClientId = _getScimClientId(
			com.liferay.portal.kernel.model.User.class.getName(),
			portalUser.getUserId(), portalUser.getCompanyId());

		if (Validator.isNull(userScimClientId)) {
			throw new NotFoundException(
				"User " + userId + " is not a SCIM user");
		}

		ScimClientOAuth2ApplicationConfiguration
			scimClientOAuth2ApplicationConfiguration =
				ScimUtil.getScimClientOAuth2ApplicationConfiguration(
					companyId, _configurationAdmin);

		String scimClientId = ScimClientUtil.generateScimClientId(
			scimClientOAuth2ApplicationConfiguration.oAuth2ApplicationName());

		if (!Objects.equals(userScimClientId, scimClientId)) {
			throw new ConflictException(
				"User was provisioned by another SCIM client");
		}

		return ScimUtil.toScimUser(portalUser);
	}

	private List<ScimUser> _getScimUsers(long companyId, long userGroupId) {
		String userGroupScimClientId = _getScimClientId(
			UserGroup.class.getName(), userGroupId, companyId);

		return TransformUtil.transform(
			_userLocalService.getUserGroupUsers(userGroupId),
			user -> {
				String userScimClientId = _getScimClientId(
					com.liferay.portal.kernel.model.User.class.getName(),
					user.getUserId(), user.getCompanyId());

				if (!Objects.equals(userGroupScimClientId, userScimClientId)) {
					return null;
				}

				return ScimUtil.toScimUser(user);
			});
	}

	private UserGroup _getUserGroup(long companyId, long userGroupId)
		throws AbstractCharonException {

		UserGroup userGroup = null;

		try {
			userGroup = _userGroupService.fetchUserGroup(userGroupId);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			throw new NotFoundException();
		}

		if (userGroup == null) {
			throw new NotFoundException(
				"No group found with group ID " + userGroupId);
		}

		String groupScimClientId = _getScimClientId(
			UserGroup.class.getName(), userGroup.getPrimaryKey(),
			userGroup.getCompanyId());

		if (Validator.isNull(groupScimClientId)) {
			throw new NotFoundException(
				"No group found with group ID " + userGroupId);
		}

		ScimClientOAuth2ApplicationConfiguration
			scimClientOAuth2ApplicationConfiguration =
				ScimUtil.getScimClientOAuth2ApplicationConfiguration(
					companyId, _configurationAdmin);

		String scimClientId = ScimClientUtil.generateScimClientId(
			scimClientOAuth2ApplicationConfiguration.oAuth2ApplicationName());

		if (!Objects.equals(groupScimClientId, scimClientId)) {
			throw new ConflictException(
				"Group was provisioned by another SCIM client");
		}

		return userGroup;
	}

	private void _saveScimClientId(
			String className, long classPK, long companyId, String scimClientId)
		throws Exception {

		ExpandoColumn expandoColumn = _getOrAddExpandoColumn(
			ClassNameLocalServiceUtil.getClassNameId(className), companyId,
			"scimClientId", false, true);

		_expandoValueLocalService.addValue(
			companyId, className, ExpandoTableConstants.DEFAULT_TABLE_NAME,
			expandoColumn.getName(), classPK, scimClientId);
	}

	private com.liferay.portal.kernel.model.User _updatePortalUser(
			int birthdayMonth, int birthdayDay, int birthdayYear,
			com.liferay.portal.kernel.model.User portalUser, ScimUser scimUser,
			ScimClientOAuth2ApplicationConfiguration
				scimClientOAuth2ApplicationConfiguration)
		throws Exception {

		String scimClientId = ScimClientUtil.generateScimClientId(
			scimClientOAuth2ApplicationConfiguration.oAuth2ApplicationName());
		String portalUserScimClientId = _getScimClientId(
			com.liferay.portal.kernel.model.User.class.getName(),
			portalUser.getUserId(), portalUser.getCompanyId());

		if (Validator.isNotNull(portalUserScimClientId) &&
			!Objects.equals(scimClientId, portalUserScimClientId)) {

			throw new ConflictException(
				"User was provisioned by another SCIM client");
		}

		Contact contact = portalUser.getContact();

		portalUser = _userService.updateUser(
			portalUser.getUserId(), scimUser.getPassword(), StringPool.BLANK,
			StringPool.BLANK, false, portalUser.getReminderQueryQuestion(),
			portalUser.getReminderQueryAnswer(), portalUser.getScreenName(),
			scimUser.getEmailAddresses()[0], false, null,
			portalUser.getLanguageId(), scimUser.getTimeZoneId(),
			portalUser.getGreeting(), portalUser.getComments(),
			scimUser.getFirstName(), scimUser.getMiddleName(),
			scimUser.getLastName(), scimUser.getPrefix(), scimUser.getSuffix(),
			scimUser.isMale(), birthdayMonth, birthdayDay, birthdayYear,
			contact.getSmsSn(), contact.getFacebookSn(), contact.getJabberSn(),
			contact.getSkypeSn(), contact.getTwitterSn(),
			scimUser.getJobTitle(), portalUser.getGroupIds(),
			portalUser.getOrganizationIds(), scimUser.getRoleIds(), null,
			portalUser.getUserGroupIds(), portalUser.getAddresses(),
			portalUser.getEmailAddresses(), portalUser.getPhones(),
			portalUser.getWebsites(), null, new ServiceContext());

		if (!Objects.equals(
				portalUser.getExternalReferenceCode(),
				scimUser.getExternalReferenceCode())) {

			portalUser.setExternalReferenceCode(
				scimUser.getExternalReferenceCode());

			portalUser = _userLocalService.updateUser(portalUser);
		}

		if (portalUser.isActive() != scimUser.isActive()) {
			portalUser = _userLocalService.updateStatus(
				portalUser.getUserId(),
				scimUser.isActive() ? WorkflowConstants.STATUS_APPROVED :
					WorkflowConstants.STATUS_INACTIVE,
				new ServiceContext());
		}

		if (Validator.isNull(portalUserScimClientId)) {
			_saveScimClientId(
				com.liferay.portal.kernel.model.User.class.getName(),
				portalUser.getUserId(), portalUser.getCompanyId(),
				scimClientId);
		}

		return portalUser;
	}

	private void _updateUserGroupUsers(
			long companyId, Group group, long userGroupId)
		throws Exception {

		String userGroupScimClientId = _getScimClientId(
			UserGroup.class.getName(), userGroupId, companyId);

		_userLocalService.setUserGroupUsers(
			userGroupId,
			TransformUtil.transformToLongArray(
				group.getMembers(),
				userId -> {
					String userScimClientId = _getScimClientId(
						com.liferay.portal.kernel.model.User.class.getName(),
						GetterUtil.getLong(userId), companyId);

					if (!Objects.equals(
							userGroupScimClientId, userScimClientId)) {

						return null;
					}

					return GetterUtil.getLong(userId);
				}));
	}

	private void _validate(Node node, String... fieldNames)
		throws BadRequestException {

		if (node == null) {
			return;
		}

		ExpressionNode expressionNode = (ExpressionNode)node;

		for (String fieldName : fieldNames) {
			if (StringUtil.contains(
					expressionNode.getAttributeValue(), fieldName,
					StringPool.COLON)) {

				return;
			}
		}

		throw new BadRequestException();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UserManagerImpl.class);

	private static final Map<String, String> _columnNames = Map.of(
		"displayName", "name", "externalId", "externalReferenceCode",
		"userName", "screenName");
	private static final TransactionConfig _transactionConfig =
		TransactionConfig.Factory.create(
			Propagation.REQUIRED, new Class<?>[] {Exception.class});

	private final AddressLocalService _addressLocalService;
	private final ClassNameLocalService _classNameLocalService;
	private final CompanyLocalService _companyLocalService;
	private final ConfigurationAdmin _configurationAdmin;
	private final ContactLocalService _contactLocalService;
	private final CounterLocalService _counterLocalService;
	private final CountryLocalService _countryLocalService;
	private final EmailAddressLocalService _emailAddressLocalService;
	private final ExpandoColumnLocalService _expandoColumnLocalService;
	private final ExpandoTableLocalService _expandoTableLocalService;
	private final ExpandoValueLocalService _expandoValueLocalService;
	private final ListTypeLocalService _listTypeLocalService;
	private final PhoneLocalService _phoneLocalService;
	private final RegionLocalService _regionLocalService;
	private final UserGroupLocalService _userGroupLocalService;
	private final UserGroupService _userGroupService;
	private final UserLocalService _userLocalService;
	private final UserService _userService;
	private final WebsiteLocalService _websiteLocalService;

}