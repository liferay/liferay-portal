/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.initializer.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.MappingJsonFactory;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountEntryUserRel;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryUserRelLocalService;
import com.liferay.commerce.helper.CommerceAccountHelper;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserIdMapperLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.FriendlyURLNormalizer;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.URI;
import java.net.URLEncoder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alec Sloan
 */
@Component(service = CommerceUsersImporter.class)
public class CommerceUsersImporter {

	public void importCommerceUsers(
			File commerceUsersFile, ClassLoader classLoader,
			String imageDependenciesPath, long scopeGroupId, long userId)
		throws Exception {

		MappingJsonFactory mappingJsonFactory = new MappingJsonFactory();

		JsonParser jsonFactoryParser = mappingJsonFactory.createParser(
			commerceUsersFile);

		JsonToken jsonToken = jsonFactoryParser.nextToken();

		if (jsonToken != JsonToken.START_ARRAY) {
			throw new Exception("JSON Array Expected");
		}

		ServiceContext serviceContext = getServiceContext(scopeGroupId, userId);

		int importCount = 0;

		while (jsonFactoryParser.nextToken() != JsonToken.END_ARRAY) {
			TreeNode treeNode = jsonFactoryParser.readValueAsTree();

			JSONObject jsonObject = _jsonFactory.createJSONObject(
				treeNode.toString());

			if (_log.isDebugEnabled()) {
				_log.debug(jsonObject);
			}

			_importCommerceUser(
				jsonObject, classLoader, imageDependenciesPath, serviceContext);

			importCount += 1;
		}

		if (_log.isInfoEnabled()) {
			_log.info("Imported users count: " + importCount);
		}

		jsonFactoryParser.close();
	}

	public void importCommerceUsers(
			JSONArray jsonArray, ClassLoader classLoader,
			String dependenciesPath, long scopeGroupId, long userId)
		throws IOException, PortalException {

		User user = _userLocalService.getUser(userId);

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(user.getCompanyId());
		serviceContext.setScopeGroupId(scopeGroupId);
		serviceContext.setUserId(userId);

		for (int i = 0; i < jsonArray.length(); i++) {
			_importCommerceUser(
				jsonArray.getJSONObject(i), classLoader, dependenciesPath,
				serviceContext);
		}
	}

	protected User addOrUpdateUser(
			String password, String userReminderQueryQuestion,
			String userReminderQueryAnswer, String screenName,
			String emailAddress, long facebookId, String openId,
			boolean portrait, byte[] portraitBytes, Locale locale,
			String timeZoneId, String greeting, String comments,
			String firstName, String middleName, String lastName,
			long prefixListTypeId, long suffixListTypeId, boolean male,
			int birthdayMonth, int birthdayDay, int birthdayYear, String smsSn,
			String facebookSn, String jabberSn, String skypeSn,
			String twitterSn, String jobTitle, long[] groupIds,
			long[] organizationIds, long[] roleIds, long[] userGroupIds,
			ServiceContext serviceContext)
		throws PortalException {

		User user = _userLocalService.fetchUserByScreenName(
			serviceContext.getCompanyId(), screenName);

		if (user == null) {
			user = _userLocalService.fetchUserByEmailAddress(
				serviceContext.getCompanyId(), emailAddress);
		}

		long companyId = serviceContext.getCompanyId();

		long creatorUserId = serviceContext.getUserId();

		boolean autoScreenName = Validator.isNull(screenName);

		if (user == null) {
			boolean autoPassword = Validator.isNull(password);

			user = _userLocalService.addUser(
				creatorUserId, companyId, autoPassword, password, password,
				autoScreenName, screenName, emailAddress, locale, firstName,
				middleName, lastName, prefixListTypeId, suffixListTypeId, male,
				birthdayMonth, birthdayDay, birthdayYear, jobTitle,
				UserConstants.TYPE_REGULAR, groupIds, organizationIds, roleIds,
				userGroupIds, false, serviceContext);
		}
		else {
			groupIds = ArrayUtil.append(user.getGroupIds(), groupIds);
		}

		if (Validator.isNotNull(comments) ||
			!groupIds.equals(user.getGroupIds())) {

			user = _userLocalService.updateUser(
				user.getUserId(), StringPool.BLANK, StringPool.BLANK,
				StringPool.BLANK, false, userReminderQueryQuestion,
				userReminderQueryAnswer, screenName, emailAddress, portrait,
				portraitBytes, LocaleUtil.toLanguageId(locale), timeZoneId,
				greeting, comments, firstName, middleName, lastName,
				prefixListTypeId, suffixListTypeId, male, birthdayMonth,
				birthdayDay, birthdayYear, smsSn, facebookSn, jabberSn, skypeSn,
				twitterSn, jobTitle, groupIds, organizationIds, roleIds, null,
				userGroupIds, serviceContext);
		}
		else if (portrait) {
			_userLocalService.updatePortrait(user.getUserId(), portraitBytes);
		}

		return user;
	}

	protected ServiceContext getServiceContext(long scopeGroupId, long userId)
		throws PortalException {

		ServiceContext serviceContext = new ServiceContext();

		User user = _userLocalService.getUser(userId);

		serviceContext.setCompanyId(user.getCompanyId());

		serviceContext.setScopeGroupId(scopeGroupId);
		serviceContext.setUserId(userId);

		return serviceContext;
	}

	@SuppressFBWarnings("PATH_TRAVERSAL_IN")
	private void _importCommerceUser(
			JSONObject jsonObject, ClassLoader classLoader,
			String dependenciesPath, ServiceContext serviceContext)
		throws IOException, PortalException {

		String password = jsonObject.getString("password");

		String userReminderQueryQuestion = jsonObject.getString(
			"userReminderQueryQuestion");
		String userReminderQueryAnswer = jsonObject.getString(
			"userReminderQueryAnswer");
		String screenName = jsonObject.getString("screenName");
		long facebookId = jsonObject.getLong("facebookId");
		String openId = jsonObject.getString("openId");

		String emailAddress = jsonObject.getString("emailAddress");

		boolean hasPortrait = false;
		byte[] portraitBytes = null;

		String portrait = jsonObject.getString("portrait");

		if (!Validator.isBlank(portrait)) {
			InputStream inputStream = null;

			try {
				String uriString =
					dependenciesPath + URLEncoder.encode(portrait, "UTF-8");

				URI uri = new URI(uriString);

				if (StringUtil.equalsIgnoreCase(uri.getScheme(), "file")) {
					inputStream = new FileInputStream(uri.getPath());
				}
				else {
					inputStream = classLoader.getResourceAsStream(
						dependenciesPath + portrait);
				}

				portraitBytes = _file.getBytes(inputStream);

				hasPortrait = true;
			}
			catch (Exception exception) {
				_log.error(exception);
			}
			finally {
				if (inputStream != null) {
					inputStream.close();
				}
			}
		}

		User user = _userLocalService.getUser(serviceContext.getUserId());

		Locale locale = user.getLocale();

		String importedLanguageCode = jsonObject.getString("languageCode");

		if (!Validator.isBlank(importedLanguageCode)) {
			locale = _language.getLocale(importedLanguageCode);
		}

		TimeZone timeZone = user.getTimeZone();

		String timeZoneId = jsonObject.getString(
			"timeZoneId", timeZone.getID());

		String greeting = jsonObject.getString("greeting");
		String comments = jsonObject.getString("comments");
		String firstName = jsonObject.getString("firstName");
		String middleName = jsonObject.getString("middleName");
		String lastName = jsonObject.getString("lastName");
		long prefixListTypeId = jsonObject.getLong("prefixListTypeId");
		long suffixListTypeId = jsonObject.getLong("suffixListTypeId");
		boolean male = jsonObject.getBoolean("male");

		String gender = jsonObject.getString("gender");

		if (Validator.isNotNull(gender)) {
			male = gender.equals("male");
		}

		Calendar calendar = CalendarFactoryUtil.getCalendar(timeZone);

		int birthdayMonth = calendar.get(
			jsonObject.getInt("birthdayMonth", Calendar.MONTH));
		int birthdayDay = calendar.get(
			jsonObject.getInt("birthdayDay", Calendar.DAY_OF_MONTH));
		int birthdayYear = calendar.get(
			jsonObject.getInt("birthdayYear", Calendar.YEAR));

		String birthday = jsonObject.getString("birthday");

		if (Validator.isNotNull(birthday)) {
			String[] birthdayTokens = birthday.split("-");

			birthdayMonth = GetterUtil.getInteger(birthdayTokens[1]) - 1;

			birthdayDay = GetterUtil.getInteger(birthdayTokens[2]);

			birthdayYear = GetterUtil.getInteger(birthdayTokens[0]);
		}

		String smsSn = jsonObject.getString("smsSn");
		String facebookSn = jsonObject.getString("facebookSn");
		String jabberSn = jsonObject.getString("jabberSn");
		String skypeSn = jsonObject.getString("skypeSn");
		String twitterSn = jsonObject.getString("twitterSn");
		String jobTitle = jsonObject.getString("jobTitle");

		// Collect Organization Role Ids

		JSONArray organizationsJSONArray = jsonObject.getJSONArray(
			"organizations");

		List<Long> organizationIds = new ArrayList<>();

		if (organizationsJSONArray != null) {
			for (int i = 0; i < organizationsJSONArray.length(); i++) {
				Organization organization =
					_organizationLocalService.getOrganization(
						serviceContext.getCompanyId(),
						organizationsJSONArray.getString(i));

				if (organization == null) {
					continue;
				}

				organizationIds.add(organization.getOrganizationId());
			}
		}

		// Collect Role Ids

		JSONArray rolesJSONArray = jsonObject.getJSONArray("roles");

		List<Long> roleIds = new ArrayList<>();

		if (rolesJSONArray != null) {
			for (int i = 0; i < rolesJSONArray.length(); i++) {
				Role role = _roleLocalService.getRole(
					serviceContext.getCompanyId(), rolesJSONArray.getString(i));

				if (role == null) {
					continue;
				}

				roleIds.add(role.getRoleId());
			}
		}

		long[] userGroupIds = null;

		user = addOrUpdateUser(
			password, userReminderQueryQuestion, userReminderQueryAnswer,
			screenName, emailAddress, facebookId, openId, hasPortrait,
			portraitBytes, locale, timeZoneId, greeting, comments, firstName,
			middleName, lastName, prefixListTypeId, suffixListTypeId, male,
			birthdayMonth, birthdayDay, birthdayYear, smsSn, facebookSn,
			jabberSn, skypeSn, twitterSn, jobTitle,
			new long[] {serviceContext.getScopeGroupId()},
			ArrayUtil.toLongArray(organizationIds),
			ArrayUtil.toLongArray(roleIds), userGroupIds, serviceContext);

		// Add User Group Roles

		JSONArray groupRolesJSONArray = jsonObject.getJSONArray("groupRoles");

		if (groupRolesJSONArray != null) {
			for (int i = 0; i < groupRolesJSONArray.length(); i++) {
				Role role = _roleLocalService.getRole(
					serviceContext.getCompanyId(),
					groupRolesJSONArray.getString(i));

				_userGroupRoleLocalService.addUserGroupRoles(
					user.getUserId(), serviceContext.getScopeGroupId(),
					new long[] {role.getRoleId()});
			}
		}

		// Add External ID mapping

		String externalUserId = jsonObject.getString("externalUserId");

		String externalSystemType = jsonObject.getString("externalSystemType");

		if (Validator.isNotNull(externalUserId) &&
			Validator.isNotNull(externalSystemType)) {

			String externalSystemDescription = jsonObject.getString(
				"externalSystemDescription", "");

			_userIdMapperLocalService.updateUserIdMapper(
				user.getUserId(), externalSystemType, externalSystemDescription,
				externalUserId);
		}

		// Add User to Commerce Accounts and Account Roles

		JSONArray accountsJSONArray = jsonObject.getJSONArray("accounts");

		if (accountsJSONArray != null) {
			for (int i = 0; i < accountsJSONArray.length(); i++) {
				JSONObject accountJSONObject = accountsJSONArray.getJSONObject(
					i);

				JSONArray accountRolesJSONArray =
					accountJSONObject.getJSONArray("roles");

				if (accountRolesJSONArray == null) {
					continue;
				}

				AccountEntry accountEntry =
					_accountEntryLocalService.
						fetchAccountEntryByExternalReferenceCode(
							_friendlyURLNormalizer.normalize(
								accountJSONObject.getString("name")),
							serviceContext.getCompanyId());

				AccountEntryUserRel accountEntryUserRel =
					_accountEntryUserRelLocalService.fetchAccountEntryUserRel(
						accountEntry.getAccountEntryId(), user.getUserId());

				if (accountEntryUserRel != null) {
					continue;
				}

				List<Long> accountRoleIds = new ArrayList<>();

				for (int j = 0; j < accountRolesJSONArray.length(); j++) {
					Role role = _roleLocalService.fetchRole(
						serviceContext.getCompanyId(),
						accountRolesJSONArray.getString(j));

					if (role == null) {
						continue;
					}

					accountRoleIds.add(role.getRoleId());
				}

				long[] userIds = {user.getUserId()};

				_commerceAccountHelper.addAccountEntryUserRels(
					accountEntry.getAccountEntryId(), userIds, null,
					ArrayUtil.toLongArray(accountRoleIds), serviceContext);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceUsersImporter.class);

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference
	private AccountEntryUserRelLocalService _accountEntryUserRelLocalService;

	@Reference
	private CommerceAccountHelper _commerceAccountHelper;

	@Reference
	private com.liferay.portal.kernel.util.File _file;

	@Reference
	private FriendlyURLNormalizer _friendlyURLNormalizer;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private OrganizationLocalService _organizationLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@Reference
	private UserIdMapperLocalService _userIdMapperLocalService;

	@Reference
	private UserLocalService _userLocalService;

}