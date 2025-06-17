/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.openid.connect.internal;

import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.model.ExpandoTableConstants;
import com.liferay.expando.kernel.model.ExpandoValue;
import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.expando.kernel.service.ExpandoTableLocalService;
import com.liferay.expando.kernel.service.ExpandoValueLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.UserEmailAddressException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.ListType;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.AddressLocalService;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.CountryLocalService;
import com.liferay.portal.kernel.service.ListTypeLocalService;
import com.liferay.portal.kernel.service.PhoneLocalService;
import com.liferay.portal.kernel.service.RegionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.sso.openid.connect.OpenIdConnectServiceException;
import com.liferay.portal.security.sso.openid.connect.internal.exception.StrangersNotAllowedException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Arthur Chan
 */
@Component(service = OIDCUserInfoProcessor.class)
public class OIDCUserInfoProcessor {

	public long processUserInfo(
			long companyId, String issuer, ServiceContext serviceContext,
			String userInfoJSON, String userInfoMapperJSON)
		throws Exception {

		User user = _addOrUpdateUser(
			companyId, issuer, serviceContext, userInfoJSON,
			userInfoMapperJSON);

		try {
			_addAddress(serviceContext, user, userInfoJSON, userInfoMapperJSON);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}

		try {
			_addPhone(serviceContext, user, userInfoJSON, userInfoMapperJSON);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}

		return user.getUserId();
	}

	private void _addAddress(
			ServiceContext serviceContext, User user, String userInfoJSON,
			String userInfoMapperJSON)
		throws Exception {

		JSONObject userInfoMapperJSONObject = _jsonFactory.createJSONObject(
			userInfoMapperJSON);

		JSONObject addressMapperJSONObject =
			userInfoMapperJSONObject.getJSONObject("address");

		if (addressMapperJSONObject == null) {
			return;
		}

		JSONObject userInfoJSONObject = _jsonFactory.createJSONObject(
			userInfoJSON);

		String streetClaimString = _getClaimString(
			"street", addressMapperJSONObject, userInfoJSONObject);

		if (Validator.isNull(streetClaimString)) {
			return;
		}

		String[] streetClaimStringParts = streetClaimString.split("\n");

		Region region = null;
		Country country = null;

		String countryClaimString = _getClaimString(
			"country", addressMapperJSONObject, userInfoJSONObject);

		if (Validator.isNotNull(countryClaimString)) {
			if ((countryClaimString.charAt(0) >= '0') &&
				(countryClaimString.charAt(0) <= '9')) {

				country = _countryLocalService.getCountryByNumber(
					user.getCompanyId(), countryClaimString);
			}
			else if (countryClaimString.length() == 2) {
				country = _countryLocalService.fetchCountryByA2(
					user.getCompanyId(),
					StringUtil.toUpperCase(countryClaimString));
			}
			else if (countryClaimString.length() == 3) {
				country = _countryLocalService.fetchCountryByA3(
					user.getCompanyId(),
					StringUtil.toUpperCase(countryClaimString));
			}
			else {
				country = _countryLocalService.fetchCountryByName(
					user.getCompanyId(),
					StringUtil.toLowerCase(countryClaimString));
			}

			String regionCode = _getClaimString(
				"region", addressMapperJSONObject, userInfoJSONObject);

			if ((country != null) && Validator.isNotNull(regionCode)) {
				region = _regionLocalService.fetchRegion(
					country.getCountryId(), StringUtil.toUpperCase(regionCode));
			}
		}

		ListType listType = _listTypeLocalService.getListType(
			user.getCompanyId(),
			_getClaimString(
				"addressType", addressMapperJSONObject, userInfoJSONObject),
			Contact.class.getName() + ".address");

		if (listType == null) {
			List<ListType> listTypes = _listTypeLocalService.getListTypes(
				user.getCompanyId(), Contact.class.getName() + ".address");

			listType = listTypes.get(0);
		}

		_addressLocalService.addAddress(
			null, user.getUserId(), Contact.class.getName(),
			user.getContactId(), (country == null) ? 0 : country.getCountryId(),
			listType.getListTypeId(),
			(region == null) ? 0 : region.getRegionId(),
			_getClaimString(
				"city", addressMapperJSONObject, userInfoJSONObject),
			null, false, null, false,
			(streetClaimStringParts.length > 0) ? streetClaimStringParts[0] :
				null,
			(streetClaimStringParts.length > 1) ? streetClaimStringParts[1] :
				null,
			(streetClaimStringParts.length > 2) ? streetClaimStringParts[2] :
				null,
			null,
			_getClaimString("zip", addressMapperJSONObject, userInfoJSONObject),
			null, serviceContext);
	}

	private User _addOrUpdateUser(
			long companyId, String issuer, ServiceContext serviceContext,
			String userInfoJSON, String userInfoMapperJSON)
		throws Exception {

		JSONObject userInfoMapperJSONObject = _jsonFactory.createJSONObject(
			userInfoMapperJSON);

		JSONObject userMapperJSONObject =
			userInfoMapperJSONObject.getJSONObject("user");

		JSONObject userInfoJSONObject = _jsonFactory.createJSONObject(
			userInfoJSON);

		String emailAddress = _getClaimString(
			"emailAddress", userMapperJSONObject, userInfoJSONObject);
		String firstName = _getClaimString(
			"firstName", userMapperJSONObject, userInfoJSONObject);
		String lastName = _getClaimString(
			"lastName", userMapperJSONObject, userInfoJSONObject);
		String screenName = _getClaimString(
			"screenName", userMapperJSONObject, userInfoJSONObject);

		User user = _userLocalService.fetchUserByEmailAddress(
			companyId, emailAddress);

		_validate(companyId, emailAddress, firstName, lastName, user);

		JSONObject contactMapperJSONObject =
			userInfoMapperJSONObject.getJSONObject("contact");

		int[] birthday = _getBirthday(
			contactMapperJSONObject, userInfoJSONObject);

		long[] roleIds = _getRoleIds(
			companyId, userInfoJSONObject,
			userInfoMapperJSONObject.getJSONObject("users_roles"));

		if (ArrayUtil.isEmpty(roleIds)) {
			roleIds = _getRoleIds(companyId, issuer);
		}

		Long oAuthClientEntryId = (Long)serviceContext.getAttribute(
			"oAuthClientEntryId");

		List<Long> userGroupIds = _getUserGroupIds(
			companyId, oAuthClientEntryId, userInfoJSONObject,
			userInfoMapperJSONObject.getJSONObject("users_groups"));

		if (user == null) {
			user = _userLocalService.addUser(
				0, companyId, true, null, null, Validator.isNull(screenName),
				screenName, emailAddress,
				_getLocale(companyId, userInfoJSONObject, userMapperJSONObject),
				firstName,
				_getClaimString(
					"middleName", userMapperJSONObject, userInfoJSONObject),
				lastName, 0, 0,
				_isMale(contactMapperJSONObject, userInfoJSONObject),
				birthday[1], birthday[2], birthday[0],
				_getClaimString(
					"jobTitle", userMapperJSONObject, userInfoJSONObject),
				UserConstants.TYPE_REGULAR, null, null, roleIds,
				(userGroupIds != null) ? ArrayUtil.toLongArray(userGroupIds) :
					null,
				false, serviceContext);

			ExpandoColumn expandoColumn = _getOrAddExpandoColumn(
				User.class.getName(), companyId);

			_expandoValueLocalService.addValue(
				_classNameLocalService.getClassNameId(User.class.getName()),
				expandoColumn.getTableId(), expandoColumn.getColumnId(),
				user.getUserId(), String.valueOf(oAuthClientEntryId));

			return _userLocalService.updatePasswordReset(
				user.getUserId(), false);
		}

		Contact contact = user.getContact();

		serviceContext.setUuid(user.getUuid());

		return _userLocalService.updateUser(
			user.getUserId(), StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, false, user.getReminderQueryQuestion(),
			user.getReminderQueryAnswer(),
			Validator.isNotNull(screenName) ? screenName : user.getScreenName(),
			Validator.isNotNull(emailAddress) ? emailAddress :
				user.getEmailAddress(),
			true, null, user.getLanguageId(), user.getTimeZoneId(),
			user.getGreeting(), user.getComments(),
			Validator.isNotNull(firstName) ? firstName : user.getFirstName(),
			_getClaimString(
				"middleName", userMapperJSONObject, userInfoJSONObject),
			Validator.isNotNull(lastName) ? lastName : user.getLastName(),
			contact.getPrefixListTypeId(), contact.getSuffixListTypeId(),
			_isMale(contactMapperJSONObject, userInfoJSONObject), birthday[1],
			birthday[2], birthday[0], contact.getSmsSn(),
			contact.getFacebookSn(), contact.getJabberSn(),
			contact.getSkypeSn(), contact.getTwitterSn(),
			_getClaimString(
				"jobTitle", userMapperJSONObject, userInfoJSONObject),
			user.getGroupIds(), user.getOrganizationIds(), user.getRoleIds(),
			user.getUserGroupRoles(),
			_getUserGroupIds(companyId, oAuthClientEntryId, user, userGroupIds),
			serviceContext);
	}

	private void _addPhone(
			ServiceContext serviceContext, User user, String userInfoJSON,
			String userInfoMapperJSON)
		throws Exception {

		JSONObject userInfoMapperJSONObject = _jsonFactory.createJSONObject(
			userInfoMapperJSON);

		JSONObject phoneMapperJSONObject =
			userInfoMapperJSONObject.getJSONObject("phone");

		if (phoneMapperJSONObject == null) {
			return;
		}

		JSONObject userInfoJSONObject = _jsonFactory.createJSONObject(
			userInfoJSON);

		String phoneClaimString = _getClaimString(
			"phone", phoneMapperJSONObject, userInfoJSONObject);

		if (Validator.isNull(phoneClaimString)) {
			return;
		}

		ListType listType = _listTypeLocalService.getListType(
			user.getCompanyId(),
			_getClaimString(
				"phoneType", phoneMapperJSONObject, userInfoJSONObject),
			Contact.class.getName() + ".phone");

		if (listType == null) {
			List<ListType> listTypes = _listTypeLocalService.getListTypes(
				user.getCompanyId(), Contact.class.getName() + ".phone");

			listType = listTypes.get(0);
		}

		_phoneLocalService.addPhone(
			null, user.getUserId(), Contact.class.getName(),
			user.getContactId(), phoneClaimString, null,
			listType.getListTypeId(), false, serviceContext);
	}

	private int[] _getBirthday(
		JSONObject contactMapperJSONObject, JSONObject userInfoJSONObject) {

		int[] birthday = new int[3];

		birthday[0] = 1970;
		birthday[1] = Calendar.JANUARY;
		birthday[2] = 1;

		String birthdateClaimString = _getClaimString(
			"birthdate", contactMapperJSONObject, userInfoJSONObject);

		if (Validator.isNull(birthdateClaimString)) {
			return birthday;
		}

		String[] birthdateClaimStringParts = birthdateClaimString.split("-");

		if (!birthdateClaimStringParts[0].equals("0000")) {
			birthday[0] = GetterUtil.getInteger(birthdateClaimStringParts[0]);
		}

		if (birthdateClaimStringParts.length == 3) {
			birthday[1] =
				GetterUtil.getInteger(birthdateClaimStringParts[1]) - 1;
			birthday[2] = GetterUtil.getInteger(birthdateClaimStringParts[2]);
		}

		return birthday;
	}

	private JSONArray _getClaimJSONArray(
		String key, JSONObject mapperJSONObject,
		JSONObject userInfoJSONObject) {

		Object claimObject = _getClaimObject(
			key, mapperJSONObject, userInfoJSONObject);

		if ((claimObject == null) || (claimObject instanceof JSONArray)) {
			return (JSONArray)claimObject;
		}

		return null;
	}

	private Object _getClaimObject(
		String key, JSONObject mapperJSONObject,
		JSONObject userInfoJSONObject) {

		String value = mapperJSONObject.getString(key);

		if (Validator.isNull(value)) {
			return null;
		}

		String[] valueParts = value.split("->");

		Object claimObject = userInfoJSONObject.get(valueParts[0]);

		for (int i = 1; i < valueParts.length; ++i) {
			JSONObject claimJSONObject = (JSONObject)claimObject;

			if (claimJSONObject != null) {
				claimObject = claimJSONObject.get(valueParts[i]);
			}
		}

		return claimObject;
	}

	private String _getClaimString(
		String key, JSONObject mapperJSONObject,
		JSONObject userInfoJSONObject) {

		Object claimObject = _getClaimObject(
			key, mapperJSONObject, userInfoJSONObject);

		if ((claimObject != null) && !(claimObject instanceof String)) {
			throw new IllegalArgumentException("Claim is not a string");
		}

		return (String)claimObject;
	}

	private Locale _getLocale(
			long companyId, JSONObject userInfoJSONObject,
			JSONObject userMapperJSONObject)
		throws Exception {

		String languageId = _getClaimString(
			"languageId", userMapperJSONObject, userInfoJSONObject);

		if (Validator.isNotNull(languageId)) {
			return new Locale(languageId);
		}

		Company company = _companyLocalService.getCompany(companyId);

		return company.getLocale();
	}

	private ExpandoColumn _getOrAddExpandoColumn(
			String className, long companyId)
		throws Exception {

		ExpandoTable expandoTable = _expandoTableLocalService.fetchTable(
			companyId, _classNameLocalService.getClassNameId(className),
			ExpandoTableConstants.DEFAULT_TABLE_NAME);

		if (expandoTable == null) {
			expandoTable = _expandoTableLocalService.addTable(
				companyId, className, ExpandoTableConstants.DEFAULT_TABLE_NAME);
		}

		ExpandoColumn expandoColumn = _expandoColumnLocalService.fetchColumn(
			expandoTable.getTableId(), "idpId");

		if (expandoColumn != null) {
			return expandoColumn;
		}

		expandoColumn = _expandoColumnLocalService.addColumn(
			expandoTable.getTableId(), "idpId", ExpandoColumnConstants.LONG);

		UnicodeProperties unicodeProperties =
			expandoColumn.getTypeSettingsProperties();

		unicodeProperties.setProperty(
			ExpandoColumnConstants.INDEX_TYPE,
			String.valueOf(ExpandoColumnConstants.INDEX_TYPE_KEYWORD));
		unicodeProperties.setProperty(
			ExpandoColumnConstants.PROPERTY_HIDDEN, Boolean.TRUE.toString());

		expandoColumn.setTypeSettingsProperties(unicodeProperties);

		return _expandoColumnLocalService.updateExpandoColumn(expandoColumn);
	}

	private long[] _getRoleIds(
		long companyId, JSONObject userInfoJSONObject,
		JSONObject usersRolesMapperJSONObject) {

		if ((usersRolesMapperJSONObject == null) ||
			(usersRolesMapperJSONObject.length() < 1)) {

			return null;
		}

		JSONArray rolesJSONArray = _getClaimJSONArray(
			"roles", usersRolesMapperJSONObject, userInfoJSONObject);

		if (rolesJSONArray == null) {
			return null;
		}

		List<Long> roleIds = new ArrayList<>();

		for (int i = 0; i < rolesJSONArray.length(); ++i) {
			Role role = _roleLocalService.fetchRole(
				companyId, rolesJSONArray.getString(i));

			if (role == null) {
				if (_log.isWarnEnabled()) {
					_log.warn("No role name " + rolesJSONArray.getString(i));
				}

				continue;
			}

			roleIds.add(role.getRoleId());
		}

		if (roleIds.isEmpty()) {
			return null;
		}

		return ArrayUtil.toLongArray(roleIds);
	}

	private long[] _getRoleIds(long companyId, String issuer) {
		if (Validator.isNull(issuer) ||
			!Objects.equals(
				issuer,
				_props.get(
					"open.id.connect.user.info.processor.impl.issuer"))) {

			return null;
		}

		String roleName = _props.get(
			"open.id.connect.user.info.processor.impl.regular.role");

		if (Validator.isNull(roleName)) {
			return null;
		}

		Role role = _roleLocalService.fetchRole(companyId, roleName);

		if (role == null) {
			return null;
		}

		if (role.getType() == RoleConstants.TYPE_REGULAR) {
			return new long[] {role.getRoleId()};
		}

		if (_log.isInfoEnabled()) {
			_log.info("Role " + roleName + " is not a regular role");
		}

		return null;
	}

	private List<Long> _getUserGroupIds(
			long companyId, long oAuthClientEntryId,
			JSONObject userInfoJSONObject,
			JSONObject usersGroupsMapperJSONObject)
		throws Exception {

		if ((usersGroupsMapperJSONObject == null) ||
			(usersGroupsMapperJSONObject.length() < 1)) {

			return null;
		}

		JSONArray userGroupsJSONArray = _getClaimJSONArray(
			"groups", usersGroupsMapperJSONObject, userInfoJSONObject);

		if (userGroupsJSONArray == null) {
			return Collections.emptyList();
		}

		List<Long> userGroupIds = new ArrayList<>();

		ExpandoColumn expandoColumn = _getOrAddExpandoColumn(
			UserGroup.class.getName(), companyId);

		for (int i = 0; i < userGroupsJSONArray.length(); ++i) {
			UserGroup userGroup = _userGroupLocalService.fetchUserGroup(
				companyId, userGroupsJSONArray.getString(i));

			if (userGroup == null) {
				try {
					userGroup = _userGroupLocalService.addUserGroup(
						StringPool.BLANK,
						_userLocalService.getGuestUserId(companyId), companyId,
						userGroupsJSONArray.getString(i), StringPool.BLANK,
						null);

					_expandoValueLocalService.addValue(
						_classNameLocalService.getClassNameId(UserGroup.class),
						expandoColumn.getTableId(), expandoColumn.getColumnId(),
						userGroup.getUserGroupId(),
						String.valueOf(oAuthClientEntryId));
				}
				catch (PortalException portalException) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Unable to create User Group", portalException);
					}

					continue;
				}
			}

			userGroupIds.add(userGroup.getUserGroupId());
		}

		return userGroupIds;
	}

	private long[] _getUserGroupIds(
			long companyId, long oAuthClientEntryId, User user,
			List<Long> userGroupIds)
		throws Exception {

		ExpandoColumn expandoColumn = _getOrAddExpandoColumn(
			UserGroup.class.getName(), companyId);

		for (UserGroup userGroup :
				_userGroupLocalService.getUserUserGroups(user.getUserId())) {

			if (userGroupIds.contains(userGroup.getUserGroupId())) {
				continue;
			}

			ExpandoValue expandoValue = _expandoValueLocalService.getValue(
				expandoColumn.getTableId(), expandoColumn.getColumnId(),
				userGroup.getUserGroupId());

			if ((expandoValue != null) &&
				(expandoValue.getLong() == oAuthClientEntryId)) {

				continue;
			}

			userGroupIds.add(userGroup.getUserGroupId());
		}

		return ArrayUtil.toLongArray(userGroupIds);
	}

	private boolean _isMale(
		JSONObject contactMapperJSONObject, JSONObject userInfoJSONObject) {

		String gender = _getClaimString(
			"gender", contactMapperJSONObject, userInfoJSONObject);

		if (Validator.isNull(gender) || gender.equals("male")) {
			return true;
		}

		return false;
	}

	private void _validate(
			long companyId, String emailAddress, String firstName,
			String lastName, User user)
		throws Exception {

		if (Validator.isNull(emailAddress)) {
			throw new OpenIdConnectServiceException.UserMappingException(
				"Email address is null");
		}

		if (Validator.isNull(firstName) && (user == null)) {
			throw new OpenIdConnectServiceException.UserMappingException(
				"First name is null");
		}

		if (Validator.isNull(lastName) && (user == null)) {
			throw new OpenIdConnectServiceException.UserMappingException(
				"Last name is null");
		}

		Company company = _companyLocalService.getCompany(companyId);

		if (!company.isStrangers()) {
			throw new StrangersNotAllowedException(companyId);
		}

		if (!company.isStrangersWithMx() &&
			company.hasCompanyMx(emailAddress)) {

			throw new UserEmailAddressException.MustNotUseCompanyMx(
				emailAddress);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		OIDCUserInfoProcessor.class);

	@Reference
	private AddressLocalService _addressLocalService;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private CountryLocalService _countryLocalService;

	@Reference
	private ExpandoColumnLocalService _expandoColumnLocalService;

	@Reference
	private ExpandoTableLocalService _expandoTableLocalService;

	@Reference
	private ExpandoValueLocalService _expandoValueLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private ListTypeLocalService _listTypeLocalService;

	@Reference
	private PhoneLocalService _phoneLocalService;

	@Reference
	private Props _props;

	@Reference
	private RegionLocalService _regionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private UserGroupLocalService _userGroupLocalService;

	@Reference
	private UserLocalService _userLocalService;

}