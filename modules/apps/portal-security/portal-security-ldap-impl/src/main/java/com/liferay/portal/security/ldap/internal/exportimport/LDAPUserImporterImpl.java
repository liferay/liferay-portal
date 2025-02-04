/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.ldap.internal.exportimport;

import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.model.ExpandoTableConstants;
import com.liferay.expando.kernel.model.ExpandoValue;
import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.expando.kernel.service.ExpandoTableLocalService;
import com.liferay.expando.kernel.service.ExpandoValueLocalService;
import com.liferay.expando.util.ExpandoConverterUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.bean.BeanProperties;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.SingleVMPool;
import com.liferay.portal.kernel.exception.GroupFriendlyURLException;
import com.liferay.portal.kernel.exception.NoSuchRoleException;
import com.liferay.portal.kernel.exception.NoSuchUserGroupException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.lock.DuplicateLockException;
import com.liferay.portal.kernel.lock.Lock;
import com.liferay.portal.kernel.lock.LockManager;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.exportimport.UserGroupImportTransactionThreadLocal;
import com.liferay.portal.kernel.security.ldap.AttributesTransformer;
import com.liferay.portal.kernel.security.ldap.LDAPSettings;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PwdGenerator;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.exportimport.UserImporter;
import com.liferay.portal.security.ldap.ContactConverterKeys;
import com.liferay.portal.security.ldap.SafeLdapContext;
import com.liferay.portal.security.ldap.SafeLdapFilter;
import com.liferay.portal.security.ldap.SafeLdapFilterConstraints;
import com.liferay.portal.security.ldap.SafeLdapFilterFactory;
import com.liferay.portal.security.ldap.SafeLdapFilterTemplate;
import com.liferay.portal.security.ldap.SafeLdapName;
import com.liferay.portal.security.ldap.SafeLdapNameFactory;
import com.liferay.portal.security.ldap.SafePortalLDAP;
import com.liferay.portal.security.ldap.UserConverterKeys;
import com.liferay.portal.security.ldap.configuration.ConfigurationProvider;
import com.liferay.portal.security.ldap.configuration.LDAPServerConfiguration;
import com.liferay.portal.security.ldap.exportimport.LDAPGroup;
import com.liferay.portal.security.ldap.exportimport.LDAPToPortalConverter;
import com.liferay.portal.security.ldap.exportimport.LDAPUser;
import com.liferay.portal.security.ldap.exportimport.LDAPUserImporter;
import com.liferay.portal.security.ldap.exportimport.configuration.LDAPImportConfiguration;
import com.liferay.portal.security.ldap.internal.UserImportTransactionThreadLocal;
import com.liferay.portal.security.ldap.util.LDAPUtil;
import com.liferay.portal.security.ldap.validator.LDAPFilterException;
import com.liferay.portal.security.ldap.validator.LDAPFilterValidator;

import java.io.Serializable;

import java.text.ParseException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

import javax.naming.Binding;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.commons.lang.time.StopWatch;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Michael C. Han
 * @author Brian Wing Shun Chan
 * @author Wesley Gong
 * @author Hugo Huijser
 * @author Edward C. Han
 */
@Component(service = LDAPUserImporter.class)
public class LDAPUserImporterImpl implements LDAPUserImporter {

	@Override
	public long getLastImportTime() {
		return _lastImportTime;
	}

	@Override
	public User importUser(
			long ldapServerId, long companyId, SafeLdapContext safeLdapContext,
			Attributes attributes, String password)
		throws Exception {

		LDAPServerConfiguration ldapServerConfiguration =
			_ldapServerConfigurationProvider.getConfiguration(
				companyId, ldapServerId);

		if (ldapServerConfiguration.ldapServerId() != ldapServerId) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"LDAP server ", ldapServerId,
						" is invalid because company ", companyId, " uses ",
						ldapServerConfiguration.ldapServerId()));
			}

			return null;
		}

		LDAPImportContext ldapImportContext = _getLDAPImportContext(
			companyId,
			_ldapSettings.getContactExpandoMappings(ldapServerId, companyId),
			_ldapSettings.getContactMappings(ldapServerId, companyId),
			_ldapSettings.getGroupMappings(ldapServerId, companyId),
			safeLdapContext, ldapServerId,
			new HashSet<>(
				Arrays.asList(ldapServerConfiguration.userIgnoreAttributes())),
			_ldapSettings.getUserExpandoMappings(ldapServerId, companyId),
			_ldapSettings.getUserMappings(ldapServerId, companyId));

		Attributes userLdapAttributes = _attributesTransformer.transformUser(
			attributes);

		LDAPUser ldapUser = _ldapToPortalConverter.importLDAPUser(
			ldapImportContext.getCompanyId(), userLdapAttributes,
			ldapImportContext.getUserMappings(),
			ldapImportContext.getUserExpandoMappings(),
			ldapImportContext.getContactMappings(),
			ldapImportContext.getContactExpandoMappings(), password);

		if (!ldapServerConfiguration.ignoreUserSearchFilterForAuth() &&
			!_safePortalLDAP.hasUser(
				ldapServerId, companyId, ldapUser.getScreenName(),
				ldapUser.getEmailAddress())) {

			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"User with screen name ", ldapUser.getScreenName(),
						" does not belong to LDAP server ", ldapServerId));
			}

			return null;
		}

		String fullUserDN = StringPool.BLANK;

		User user = _importUser(
			ldapImportContext, fullUserDN, userLdapAttributes, password,
			ldapUser);

		if ((user != null) &&
			ldapImportContext.containsImportedUser(fullUserDN)) {

			_importGroups(ldapImportContext, attributes, user);
		}

		return user;
	}

	@Override
	public User importUser(
			long ldapServerId, long companyId, String emailAddress,
			String screenName)
		throws Exception {

		SafeLdapContext safeLdapContext = null;

		NamingEnumeration<SearchResult> enumeration = null;

		try {
			LDAPServerConfiguration ldapServerConfiguration =
				_ldapServerConfigurationProvider.getConfiguration(
					companyId, ldapServerId);

			safeLdapContext = _safePortalLDAP.getSafeLdapContext(
				ldapServerId, companyId);

			if (safeLdapContext == null) {
				_log.error("Unable to bind to the LDAP server");

				return null;
			}

			if (ldapServerConfiguration.ldapServerId() != ldapServerId) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						StringBundler.concat(
							"LDAP server ID ", ldapServerId,
							" is no longer valid, company ", companyId,
							" now uses ",
							ldapServerConfiguration.ldapServerId()));
				}

				return null;
			}

			SafeLdapFilterTemplate authSearchSafeLdapFilterTemplate =
				LDAPUtil.getAuthSearchSafeLdapFilterTemplate(
					ldapServerConfiguration, _ldapFilterValidator);

			if (authSearchSafeLdapFilterTemplate == null) {
				_log.error("Missing authSearchFilter");

				return null;
			}

			authSearchSafeLdapFilterTemplate =
				authSearchSafeLdapFilterTemplate.replace(
					new String[] {
						"@company_id@", "@email_address@", "@screen_name@"
					},
					new String[] {
						String.valueOf(companyId), emailAddress, screenName
					});

			Properties userMappings = _ldapSettings.getUserMappings(
				ldapServerId, companyId);

			String userMappingsScreenName = GetterUtil.getString(
				userMappings.getProperty("screenName"));

			userMappingsScreenName = StringUtil.toLowerCase(
				userMappingsScreenName);

			SearchControls searchControls = new SearchControls(
				SearchControls.SUBTREE_SCOPE, 1, 0,
				new String[] {userMappingsScreenName}, false, false);

			enumeration = safeLdapContext.search(
				LDAPUtil.getBaseDNSafeLdapName(ldapServerConfiguration),
				authSearchSafeLdapFilterTemplate, searchControls);

			if (!enumeration.hasMoreElements()) {
				return null;
			}

			if (_log.isDebugEnabled()) {
				_log.debug("Search filter returned at least one result");
			}

			Binding binding = enumeration.nextElement();

			Attributes attributes = _safePortalLDAP.getUserAttributes(
				ldapServerId, companyId, safeLdapContext,
				SafeLdapNameFactory.from(binding));

			return importUser(
				ldapServerId, companyId, safeLdapContext, attributes, null);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Problem accessing LDAP server " + exception.getMessage());
			}

			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			throw new SystemException(
				"Problem accessing LDAP server " + exception.getMessage());
		}
		finally {
			if (enumeration != null) {
				enumeration.close();
			}

			if (safeLdapContext != null) {
				safeLdapContext.close();
			}
		}
	}

	@Override
	public User importUser(
			long companyId, String emailAddress, String screenName)
		throws Exception {

		Collection<LDAPServerConfiguration> ldapServerConfigurations =
			_ldapServerConfigurationProvider.getConfigurations(companyId);

		for (LDAPServerConfiguration ldapServerConfiguration :
				ldapServerConfigurations) {

			if (Validator.isNull(ldapServerConfiguration.baseProviderURL())) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"No provider URL defined in " +
							ldapServerConfiguration);
				}

				continue;
			}

			User user = importUser(
				ldapServerConfiguration.ldapServerId(), companyId, emailAddress,
				screenName);

			if (user != null) {
				return user;
			}
		}

		if (_log.isDebugEnabled()) {
			if (Validator.isNotNull(emailAddress)) {
				_log.debug(
					"User with the email address " + emailAddress +
						" was not found in any LDAP servers");
			}
			else {
				_log.debug(
					"User with the screen name " + screenName +
						" was not found in any LDAP servers");
			}
		}

		return null;
	}

	@Override
	public User importUserByScreenName(long companyId, String screenName)
		throws Exception {

		long ldapServerId = _safePortalLDAP.getLdapServerId(
			companyId, screenName, StringPool.BLANK);

		SearchResult result = (SearchResult)_safePortalLDAP.getUser(
			ldapServerId, companyId, screenName, StringPool.BLANK);

		if (result == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"No user was found in LDAP with screenName " + screenName);
			}

			return null;
		}

		SafeLdapContext safeLdapContext = _safePortalLDAP.getSafeLdapContext(
			ldapServerId, companyId);

		SafeLdapName fullUserDNSafeLdapName = SafeLdapNameFactory.from(result);

		Attributes attributes = _safePortalLDAP.getUserAttributes(
			ldapServerId, companyId, safeLdapContext, fullUserDNSafeLdapName);

		User user = importUser(
			ldapServerId, companyId, safeLdapContext, attributes, null);

		safeLdapContext.close();

		return user;
	}

	@Override
	public User importUserByUuid(long ldapServerId, long companyId, String uuid)
		throws Exception {

		Properties userMappings = _ldapSettings.getUserMappings(
			ldapServerId, companyId);

		String attributeName = GetterUtil.getString(
			userMappings.getProperty("uuid"));

		if (Validator.isBlank(attributeName)) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"User field UUID is not mapped for LDAP server " +
						ldapServerId);
			}

			return null;
		}

		return _importUserByLdapAttribute(
			ldapServerId, companyId, attributeName, uuid);
	}

	@Override
	public User importUserByUuid(long companyId, String uuid) throws Exception {
		Collection<LDAPServerConfiguration> ldapServerConfigurations =
			_ldapServerConfigurationProvider.getConfigurations(companyId);

		for (LDAPServerConfiguration ldapServerConfiguration :
				ldapServerConfigurations) {

			if (Validator.isNull(ldapServerConfiguration.baseProviderURL())) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"No provider URL defined in " +
							ldapServerConfiguration);
				}

				continue;
			}

			User user = importUserByUuid(
				ldapServerConfiguration.ldapServerId(), companyId, uuid);

			if (user != null) {
				return user;
			}
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				"User with UUID " + uuid +
					" was not found in any LDAP servers");
		}

		return null;
	}

	@Override
	public void importUsers() throws Exception {
		_companyLocalService.forEachCompanyId(
			companyId -> importUsers(companyId));
	}

	@Override
	public void importUsers(long companyId) throws Exception {
		if (!_ldapSettings.isImportEnabled(companyId)) {
			return;
		}

		LDAPImportConfiguration ldapImportConfiguration =
			_ldapImportConfigurationProvider.getConfiguration(companyId);

		try {
			long userId = _userLocalService.getGuestUserId(companyId);

			Lock lock = _lockManager.lock(
				userId, UserImporter.class.getName(), companyId,
				LDAPUserImporterImpl.class.getName(), false,
				ldapImportConfiguration.importLockExpirationTime(), false);

			if (!lock.isNew()) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						StringBundler.concat(
							"Skipping LDAP import for company ", companyId,
							" because another LDAP import is in process by ",
							"the same user ", userId));
				}

				return;
			}
		}
		catch (DuplicateLockException duplicateLockException) {
			if (_log.isDebugEnabled()) {
				Lock lock = duplicateLockException.getLock();

				_log.debug(
					StringBundler.concat(
						"Skipping LDAP import for company ", companyId,
						" because another LDAP import is in process by ",
						"another user ", lock.getUserId()));
			}

			return;
		}
		catch (Throwable throwable) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Skipping LDAP import for company " + companyId +
						" because unable to lock the lock",
					throwable);
			}

			return;
		}

		try {
			Collection<LDAPServerConfiguration> ldapServerConfigurations =
				_ldapServerConfigurationProvider.getConfigurations(companyId);

			for (LDAPServerConfiguration ldapServerConfiguration :
					ldapServerConfigurations) {

				importUsers(ldapServerConfiguration.ldapServerId(), companyId);
			}
		}
		finally {
			_lockManager.unlock(UserImporter.class.getName(), companyId);
		}
	}

	@Override
	public void importUsers(long ldapServerId, long companyId)
		throws Exception {

		if (!_ldapSettings.isImportEnabled(companyId)) {
			return;
		}

		SafeLdapContext safeLdapContext = _safePortalLDAP.getSafeLdapContext(
			ldapServerId, companyId);

		if (safeLdapContext == null) {
			return;
		}

		LDAPImportConfiguration ldapImportConfiguration =
			_ldapImportConfigurationProvider.getConfiguration(companyId);

		LDAPServerConfiguration ldapServerConfiguration =
			_ldapServerConfigurationProvider.getConfiguration(
				companyId, ldapServerId);

		if (ldapServerConfiguration.ldapServerId() != ldapServerId) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"LDAP server ID ", ldapServerId,
						" is no longer valid, company ", companyId,
						" now uses ", ldapServerConfiguration.ldapServerId()));
			}

			_lastImportTime = System.currentTimeMillis();

			return;
		}

		String[] userIgnoreAttributes =
			ldapServerConfiguration.userIgnoreAttributes();

		Set<String> ldapUserIgnoreAttributes = new HashSet<>(
			Arrays.asList(userIgnoreAttributes));

		try {
			Properties userMappings = _ldapSettings.getUserMappings(
				ldapServerId, companyId);
			Properties userExpandoMappings =
				_ldapSettings.getUserExpandoMappings(ldapServerId, companyId);
			Properties contactMappings = _ldapSettings.getContactMappings(
				ldapServerId, companyId);
			Properties contactExpandoMappings =
				_ldapSettings.getContactExpandoMappings(
					ldapServerId, companyId);
			Properties groupMappings = _ldapSettings.getGroupMappings(
				ldapServerId, companyId);

			String importMethod = ldapImportConfiguration.importMethod();

			LDAPImportContext ldapImportContext = _getLDAPImportContext(
				companyId, contactExpandoMappings, contactMappings,
				groupMappings, safeLdapContext, ldapServerId,
				ldapUserIgnoreAttributes, userExpandoMappings, userMappings);

			if (importMethod.equals(_IMPORT_BY_GROUP)) {
				_importFromLDAPByGroup(ldapImportContext);
			}
			else if (importMethod.equals(_IMPORT_BY_USER)) {
				_importFromLDAPByUser(ldapImportContext);
			}
		}
		catch (Exception exception) {
			_log.error("Unable to import LDAP users and groups", exception);
		}
		finally {
			_lastImportTime = System.currentTimeMillis();

			safeLdapContext.close();
		}
	}

	@Activate
	protected void activate() {
		_companySecurityAuthType = GetterUtil.getString(
			_props.get(PropsKeys.COMPANY_SECURITY_AUTH_TYPE));
		_portalCache = (PortalCache<String, Long>)_singleVMPool.getPortalCache(
			UserImporter.class.getName());
	}

	protected User addUser(long companyId, LDAPUser ldapUser, String password)
		throws Exception {

		StopWatch stopWatch = new StopWatch();

		if (_log.isDebugEnabled()) {
			stopWatch.start();

			_log.debug(
				StringBundler.concat(
					"Adding LDAP user ", ldapUser, " to company ", companyId));
		}

		boolean autoPassword = ldapUser.isAutoPassword();

		LDAPImportConfiguration ldapImportConfiguration =
			_ldapImportConfigurationProvider.getConfiguration(companyId);

		if (!ldapImportConfiguration.importUserPasswordEnabled()) {
			autoPassword =
				ldapImportConfiguration.importUserPasswordAutogenerated();

			if (!autoPassword) {
				String defaultPassword =
					ldapImportConfiguration.importUserPasswordDefault();

				if (StringUtil.equalsIgnoreCase(
						defaultPassword, _USER_PASSWORD_SCREEN_NAME)) {

					defaultPassword = ldapUser.getScreenName();
				}

				password = defaultPassword;
			}
		}

		Calendar birthdayCal = CalendarFactoryUtil.getCalendar();

		birthdayCal.setTime(ldapUser.getBirthday());

		int birthdayMonth = birthdayCal.get(Calendar.MONTH);
		int birthdayDay = birthdayCal.get(Calendar.DAY_OF_MONTH);
		int birthdayYear = birthdayCal.get(Calendar.YEAR);

		User user = _userLocalService.addUser(
			ldapUser.getCreatorUserId(), companyId, autoPassword, password,
			password, ldapUser.isAutoScreenName(), ldapUser.getScreenName(),
			ldapUser.getEmailAddress(), ldapUser.getLocale(),
			ldapUser.getFirstName(), ldapUser.getMiddleName(),
			ldapUser.getLastName(), 0, 0, ldapUser.isMale(), birthdayMonth,
			birthdayDay, birthdayYear, StringPool.BLANK,
			UserConstants.TYPE_REGULAR, ldapUser.getGroupIds(),
			ldapUser.getOrganizationIds(), ldapUser.getRoleIds(),
			ldapUser.getUserGroupIds(), ldapUser.isSendEmail(),
			ldapUser.getServiceContext());

		_userLocalService.updateEmailAddressVerified(user.getUserId(), true);

		if (ldapUser.isUpdatePortrait()) {
			byte[] portraitBytes = ldapUser.getPortraitBytes();

			if (ArrayUtil.isNotEmpty(portraitBytes)) {
				user = _userLocalService.updatePortrait(
					user.getUserId(), portraitBytes);
			}
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"Finished adding LDAP user ", ldapUser, " as user ", user,
					" in ", stopWatch.getTime(), "ms"));
		}

		return user;
	}

	protected String escapeLDAPName(String ldapName) {
		return StringUtil.replace(ldapName, '\\', "\\\\");
	}

	protected User getUser(long companyId, LDAPUser ldapUser) throws Exception {
		LDAPImportConfiguration ldapImportConfiguration =
			_ldapImportConfigurationProvider.getConfiguration(companyId);

		if (Objects.equals(
				ldapImportConfiguration.importUserSyncStrategy(),
				_USER_SYNC_STRATEGY_UUID)) {

			ServiceContext serviceContext = ldapUser.getServiceContext();

			return _userLocalService.fetchUserByUuidAndCompanyId(
				serviceContext.getUuidWithoutReset(), companyId);
		}

		String authType = PrefsPropsUtil.getString(
			companyId, PropsKeys.COMPANY_SECURITY_AUTH_TYPE,
			_companySecurityAuthType);

		if (authType.equals(CompanyConstants.AUTH_TYPE_SN) &&
			!ldapUser.isAutoScreenName()) {

			return _userLocalService.fetchUserByScreenName(
				companyId, ldapUser.getScreenName());
		}

		return _userLocalService.fetchUserByEmailAddress(
			companyId, ldapUser.getEmailAddress());
	}

	protected User importUser(
			LDAPImportContext ldapImportContext, String fullUserDN,
			Attributes userLdapAttributes, String password)
		throws Exception {

		userLdapAttributes = _attributesTransformer.transformUser(
			userLdapAttributes);

		LDAPUser ldapUser = _ldapToPortalConverter.importLDAPUser(
			ldapImportContext.getCompanyId(), userLdapAttributes,
			ldapImportContext.getUserMappings(),
			ldapImportContext.getUserExpandoMappings(),
			ldapImportContext.getContactMappings(),
			ldapImportContext.getContactExpandoMappings(), password);

		return _importUser(
			ldapImportContext, fullUserDN, userLdapAttributes, password,
			ldapUser);
	}

	protected void importUsers(
			LDAPImportContext ldapImportContext, long userGroupId,
			Attribute usersLdapAttribute)
		throws Exception {

		int size = 0;

		if (usersLdapAttribute != null) {
			size = usersLdapAttribute.size();
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"Importing ", size, " users from LDAP server ",
					ldapImportContext.getLdapServerId(), " to company ",
					ldapImportContext.getCompanyId()));
		}

		Set<Long> newUserIds = new LinkedHashSet<>();

		for (int i = 0; i < size; i++) {
			String fullUserDN = (String)usersLdapAttribute.get(i);

			Long userId = ldapImportContext.getImportedUserId(fullUserDN);

			if (userId != null) {
				newUserIds.add(userId);
			}
			else {
				Attributes userAttributes = null;

				try {
					userAttributes = _safePortalLDAP.getUserAttributes(
						ldapImportContext.getLdapServerId(),
						ldapImportContext.getCompanyId(),
						ldapImportContext.getSafeLdapContext(),
						SafeLdapNameFactory.from(usersLdapAttribute, i));
				}
				catch (NameNotFoundException nameNotFoundException) {
					_log.error(
						"LDAP user not found with fullUserDN " + fullUserDN,
						nameNotFoundException);

					continue;
				}

				try {
					User user = importUser(
						ldapImportContext, fullUserDN, userAttributes, null);

					if (user != null) {
						if (_log.isDebugEnabled()) {
							_log.debug(
								StringBundler.concat(
									"Adding user ", user, " to user group ",
									userGroupId));
						}

						newUserIds.add(user.getUserId());
					}
				}
				catch (GroupFriendlyURLException groupFriendlyURLException) {
					int type = groupFriendlyURLException.getType();

					if (type == GroupFriendlyURLException.DUPLICATE) {
						_log.error(
							"Unable to import user " + userAttributes +
								" because of a duplicate group friendly URL",
							groupFriendlyURLException);
					}
					else {
						_log.error(
							"Unable to import user " + userAttributes,
							groupFriendlyURLException);
					}
				}
				catch (Exception exception) {
					_log.error(
						"Unable to load user " + userAttributes, exception);
				}
			}
		}

		Set<Long> deletedUserIds = new LinkedHashSet<>();

		List<User> userGroupUsers = _userLocalService.getUserGroupUsers(
			userGroupId);

		for (User user : userGroupUsers) {
			if ((ldapImportContext.getLdapServerId() ==
					user.getLdapServerId()) &&
				!newUserIds.contains(user.getUserId())) {

				if (_log.isDebugEnabled()) {
					_log.debug(
						StringBundler.concat(
							"Removing user ", user, " from user group ",
							userGroupId));
				}

				deletedUserIds.add(user.getUserId());
			}
		}

		_userLocalService.addUserGroupUsers(
			userGroupId, ArrayUtil.toLongArray(newUserIds));

		_userLocalService.deleteUserGroupUsers(
			userGroupId, ArrayUtil.toLongArray(deletedUserIds));
	}

	private void _addRole(
			long companyId, LDAPGroup ldapGroup, UserGroup userGroup)
		throws Exception {

		Company company = _companyLocalService.getCompany(companyId);

		LDAPImportConfiguration ldapImportConfiguration =
			_ldapImportConfigurationProvider.getConfiguration(companyId);

		if (!ldapImportConfiguration.importCreateRolePerGroup()) {
			return;
		}

		Role role = null;

		try {
			role = _roleLocalService.getRole(
				companyId, ldapGroup.getGroupName());
		}
		catch (NoSuchRoleException noSuchRoleException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(noSuchRoleException);
			}

			User guestUser = _userLocalService.getGuestUser(companyId);

			role = _roleLocalService.addRole(
				null, guestUser.getUserId(), null, 0, ldapGroup.getGroupName(),
				null,
				HashMapBuilder.put(
					company.getLocale(), "Autogenerated role from LDAP import"
				).build(),
				RoleConstants.TYPE_REGULAR, null, null);

			if (_log.isDebugEnabled()) {
				_log.debug(
					"Imported autogenerated role from LDAP import: " + role);
			}
		}

		Group group = userGroup.getGroup();

		if (_groupLocalService.hasRoleGroup(
				role.getRoleId(), group.getGroupId())) {

			return;
		}

		_groupLocalService.addRoleGroups(
			role.getRoleId(), new long[] {group.getGroupId()});
	}

	private long _getExpandoColumnId() throws Exception {
		if (_expandoColumn == null) {
			_expandoColumn = _expandoColumnLocalService.fetchColumn(
				_expandoTable.getTableId(), "ldapServerId");

			if (_expandoColumn == null) {
				_expandoColumn = _expandoColumnLocalService.addColumn(
					_expandoTable.getTableId(), "ldapServerId",
					ExpandoColumnConstants.LONG);

				UnicodeProperties unicodeProperties =
					_expandoColumn.getTypeSettingsProperties();

				unicodeProperties.setProperty(
					ExpandoColumnConstants.INDEX_TYPE,
					String.valueOf(ExpandoColumnConstants.INDEX_TYPE_KEYWORD));
				unicodeProperties.setProperty(
					ExpandoColumnConstants.PROPERTY_HIDDEN,
					Boolean.TRUE.toString());
			}
		}

		return _expandoColumn.getColumnId();
	}

	private long _getExpandoTableId(long companyId) throws Exception {
		if (_expandoTable == null) {
			_expandoTable = _expandoTableLocalService.getTable(
				companyId, UserGroup.class.getName(), "LDAP");

			if (_expandoTable == null) {
				_expandoTable = _expandoTableLocalService.addTable(
					companyId, UserGroup.class.getName(), "LDAP");
			}
		}

		return _expandoTable.getTableId();
	}

	private LDAPImportContext _getLDAPImportContext(
		long companyId, Properties contactExpandoMappings,
		Properties contactMappings, Properties groupMappings,
		SafeLdapContext safeLdapContext, long ldapServerId,
		Set<String> ldapUserIgnoreAttributes, Properties userExpandoMappings,
		Properties userMappings) {

		return new LDAPImportContext(
			companyId, contactExpandoMappings, contactMappings, groupMappings,
			safeLdapContext, ldapServerId, ldapUserIgnoreAttributes,
			userExpandoMappings, userMappings);
	}

	private Attribute _getUsers(
			LDAPImportContext ldapImportContext, Attributes groupAttributes,
			UserGroup userGroup)
		throws Exception {

		Properties groupMappings = ldapImportContext.getGroupMappings();

		Attribute attribute = groupAttributes.get(
			groupMappings.getProperty("user"));

		if (attribute == null) {
			return null;
		}

		LDAPServerConfiguration ldapServerConfiguration =
			_ldapServerConfigurationProvider.getConfiguration(
				ldapImportContext.getCompanyId(),
				ldapImportContext.getLdapServerId());

		if (ldapServerConfiguration.ldapServerId() !=
				ldapImportContext.getLdapServerId()) {

			return null;
		}

		SafeLdapFilter safeLdapFilter = SafeLdapFilterConstraints.eq(
			groupMappings.getProperty("groupName"), userGroup.getName());

		SafeLdapFilter groupSearchSafeLdapFilter =
			LDAPUtil.getGroupSearchSafeLdapFilter(
				ldapServerConfiguration, _ldapFilterValidator);

		if (groupSearchSafeLdapFilter != null) {
			safeLdapFilter = safeLdapFilter.and(groupSearchSafeLdapFilter);
		}

		return _safePortalLDAP.getMultivaluedAttribute(
			ldapImportContext.getCompanyId(),
			ldapImportContext.getSafeLdapContext(),
			LDAPUtil.getBaseDNSafeLdapName(ldapServerConfiguration),
			safeLdapFilter, attribute);
	}

	private void _importFromLDAPByGroup(LDAPImportContext ldapImportContext)
		throws Exception {

		byte[] cookie = new byte[0];

		while (cookie != null) {
			List<SearchResult> searchResults = new ArrayList<>();

			Properties groupMappings = ldapImportContext.getGroupMappings();

			String groupMappingsGroupName = GetterUtil.getString(
				groupMappings.getProperty("groupName"));

			groupMappingsGroupName = StringUtil.toLowerCase(
				groupMappingsGroupName);

			cookie = _safePortalLDAP.getGroups(
				ldapImportContext.getLdapServerId(),
				ldapImportContext.getCompanyId(),
				ldapImportContext.getSafeLdapContext(), cookie, 0,
				new String[] {groupMappingsGroupName}, searchResults);

			for (SearchResult searchResult : searchResults) {
				try {
					Attributes groupAttributes =
						_safePortalLDAP.getGroupAttributes(
							ldapImportContext.getLdapServerId(),
							ldapImportContext.getCompanyId(),
							ldapImportContext.getSafeLdapContext(),
							SafeLdapNameFactory.from(searchResult), true);

					UserGroup userGroup = _importUserGroup(
						ldapImportContext.getCompanyId(), groupAttributes,
						groupMappings);

					Attribute usersAttribute = _getUsers(
						ldapImportContext, groupAttributes, userGroup);

					if (usersAttribute == null) {
						if (_log.isInfoEnabled()) {
							_log.info(
								"No users found in " + userGroup.getName());
						}
					}

					importUsers(
						ldapImportContext, userGroup.getUserGroupId(),
						usersAttribute);
				}
				catch (Exception exception) {
					_log.error(
						"Unable to import group " + searchResult, exception);
				}
			}
		}
	}

	private void _importFromLDAPByUser(LDAPImportContext ldapImportContext)
		throws Exception {

		SafeLdapContext safeLdapContext = _safePortalLDAP.getSafeLdapContext(
			ldapImportContext.getLdapServerId(),
			ldapImportContext.getCompanyId());

		try {
			byte[] cookie = new byte[0];

			while (cookie != null) {
				List<SearchResult> searchResults = new ArrayList<>();

				Properties userMappings = ldapImportContext.getUserMappings();

				String userMappingsScreenName = GetterUtil.getString(
					userMappings.getProperty("screenName"));

				userMappingsScreenName = StringUtil.toLowerCase(
					userMappingsScreenName);

				cookie = _safePortalLDAP.getUsers(
					ldapImportContext.getLdapServerId(),
					ldapImportContext.getCompanyId(), safeLdapContext, cookie,
					0, new String[] {userMappingsScreenName}, searchResults);

				for (SearchResult searchResult : searchResults) {
					String fullUserDN = searchResult.getNameInNamespace();

					if (ldapImportContext.containsImportedUser(fullUserDN)) {
						continue;
					}

					try {
						Attributes userAttributes =
							_safePortalLDAP.getUserAttributes(
								ldapImportContext.getLdapServerId(),
								ldapImportContext.getCompanyId(),
								ldapImportContext.getSafeLdapContext(),
								SafeLdapNameFactory.from(searchResult));

						User user = importUser(
							ldapImportContext, fullUserDN, userAttributes,
							null);

						_importGroups(ldapImportContext, userAttributes, user);
					}
					catch (GroupFriendlyURLException
								groupFriendlyURLException) {

						int type = groupFriendlyURLException.getType();

						if (type == GroupFriendlyURLException.DUPLICATE) {
							_log.error(
								StringBundler.concat(
									"Unable to import user ", fullUserDN,
									" because of a duplicate group friendly ",
									"URL"),
								groupFriendlyURLException);
						}
						else {
							_log.error(
								"Unable to import user " + fullUserDN,
								groupFriendlyURLException);
						}
					}
					catch (Exception exception) {
						_log.error(
							"Unable to import user " + fullUserDN, exception);
					}
				}
			}
		}
		finally {
			if (safeLdapContext != null) {
				safeLdapContext.close();
			}
		}
	}

	private Set<Long> _importGroup(
			LDAPImportContext ldapImportContext,
			SafeLdapName userGroupDNSafeLdapName, User user,
			Set<Long> newUserGroupIds)
		throws Exception {

		String userGroupIdKey = null;

		Long userGroupId = null;

		LDAPImportConfiguration ldapImportConfiguration =
			_ldapImportConfigurationProvider.getConfiguration(
				ldapImportContext.getCompanyId());

		if (ldapImportConfiguration.importGroupCacheEnabled()) {
			userGroupIdKey = StringBundler.concat(
				ldapImportContext.getLdapServerId(), StringPool.UNDERLINE,
				ldapImportContext.getCompanyId(), StringPool.UNDERLINE,
				userGroupDNSafeLdapName);

			userGroupId = _portalCache.get(userGroupIdKey);
		}

		if (userGroupId != null) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Skipping reimport of full group DN " +
						userGroupDNSafeLdapName);
			}
		}
		else {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Importing full group DN " + userGroupDNSafeLdapName);
			}

			Attributes groupAttributes = null;

			try {
				groupAttributes = _safePortalLDAP.getGroupAttributes(
					ldapImportContext.getLdapServerId(),
					ldapImportContext.getCompanyId(),
					ldapImportContext.getSafeLdapContext(),
					userGroupDNSafeLdapName);
			}
			catch (NameNotFoundException nameNotFoundException) {
				_log.error(
					"LDAP group not found with full group DN " +
						userGroupDNSafeLdapName,
					nameNotFoundException);
			}

			UserGroup userGroup = _importUserGroup(
				ldapImportContext.getCompanyId(), groupAttributes,
				ldapImportContext.getGroupMappings());

			if (userGroup == null) {
				return newUserGroupIds;
			}

			userGroupId = userGroup.getUserGroupId();

			if (ldapImportConfiguration.importGroupCacheEnabled()) {
				_portalCache.put(userGroupIdKey, userGroupId);
			}
		}

		_expandoValueLocalService.addValue(
			_classNameLocalService.getClassNameId(UserGroup.class),
			_getExpandoTableId(ldapImportContext.getCompanyId()),
			_getExpandoColumnId(), userGroupId,
			String.valueOf(ldapImportContext.getLdapServerId()));

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"Adding user ", user, " to user group ", userGroupId));
		}

		newUserGroupIds.add(userGroupId);

		return newUserGroupIds;
	}

	private void _importGroups(
			LDAPImportContext ldapImportContext, Attributes userAttributes,
			User user)
		throws Exception {

		LDAPServerConfiguration ldapServerConfiguration =
			_ldapServerConfigurationProvider.getConfiguration(
				ldapImportContext.getCompanyId(),
				ldapImportContext.getLdapServerId());

		if (ldapServerConfiguration.ldapServerId() !=
				ldapImportContext.getLdapServerId()) {

			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"LDAP server ID ", ldapImportContext.getLdapServerId(),
						" is no longer valid, company ",
						ldapImportContext.getCompanyId(), " now uses ",
						ldapServerConfiguration.ldapServerId()));
			}

			return;
		}

		Properties groupMappings = ldapImportContext.getGroupMappings();

		String groupMappingsUser = groupMappings.getProperty("user");

		Set<Long> newUserGroupIds = new LinkedHashSet<>();

		if (Validator.isNotNull(groupMappingsUser) &&
			ldapServerConfiguration.groupSearchFilterEnabled()) {

			Binding userBinding = _safePortalLDAP.getUser(
				ldapImportContext.getLdapServerId(),
				ldapImportContext.getCompanyId(), user.getScreenName(),
				user.getEmailAddress());

			String fullUserDN = userBinding.getNameInNamespace();

			SafeLdapFilter safeLdapFilter = SafeLdapFilterConstraints.eq(
				groupMappingsUser, fullUserDN);

			SafeLdapFilter groupSearchSafeLdapFilter =
				LDAPUtil.getGroupSearchSafeLdapFilter(
					ldapServerConfiguration, _ldapFilterValidator);

			if (groupSearchSafeLdapFilter != null) {
				safeLdapFilter = safeLdapFilter.and(groupSearchSafeLdapFilter);
			}

			byte[] cookie = new byte[0];

			while (cookie != null) {
				List<SearchResult> searchResults = new ArrayList<>();

				String groupMappingsGroupName = GetterUtil.getString(
					groupMappings.getProperty("groupName"));

				groupMappingsGroupName = StringUtil.toLowerCase(
					groupMappingsGroupName);

				cookie = _safePortalLDAP.searchLDAP(
					ldapImportContext.getCompanyId(),
					ldapImportContext.getSafeLdapContext(), cookie, 0,
					LDAPUtil.getBaseDNSafeLdapName(ldapServerConfiguration),
					safeLdapFilter, new String[] {groupMappingsGroupName},
					searchResults);

				for (SearchResult searchResult : searchResults) {
					SafeLdapName userGroupSafeLdapName =
						SafeLdapNameFactory.from(searchResult);

					newUserGroupIds = _importGroup(
						ldapImportContext, userGroupSafeLdapName, user,
						newUserGroupIds);
				}
			}
		}
		else {
			Properties userMappings = ldapImportContext.getUserMappings();

			String userMappingsGroup = userMappings.getProperty("group");

			if (Validator.isNull(userMappingsGroup)) {
				if (_log.isInfoEnabled()) {
					_log.info(
						"Skipping group import because no mappings for LDAP " +
							"groups were specified in user mappings " +
								userMappings);
				}

				return;
			}

			Attribute userGroupAttribute = userAttributes.get(
				userMappingsGroup);

			if (userGroupAttribute == null) {
				return;
			}

			for (int i = 0; i < userGroupAttribute.size(); i++) {
				SafeLdapName groupSafeLdapName = SafeLdapNameFactory.from(
					userGroupAttribute, i);

				newUserGroupIds = _importGroup(
					ldapImportContext, groupSafeLdapName, user,
					newUserGroupIds);
			}
		}

		_updateUserUserGroups(
			ldapImportContext.getLdapServerId(), user.getUserId(),
			newUserGroupIds);
	}

	private User _importUser(
			LDAPImportContext ldapImportContext, String fullUserDN,
			Attributes userLdapAttributes, String password, LDAPUser ldapUser)
		throws Exception {

		UserImportTransactionThreadLocal.setOriginatesFromImport(true);

		try {
			User user = getUser(ldapImportContext.getCompanyId(), ldapUser);

			if ((user != null) && user.isGuestUser()) {
				return user;
			}

			ServiceContext serviceContext = ldapUser.getServiceContext();

			serviceContext.setAttribute(
				"ldapServerId", ldapImportContext.getLdapServerId());

			String modifyTimestamp = LDAPUtil.getAttributeString(
				userLdapAttributes, "modifyTimestamp");

			Date modifiedDate = null;

			try {
				modifiedDate = LDAPUtil.parseDate(modifyTimestamp);
			}
			catch (ParseException parseException) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"Unable to parse LDAP modify timestamp " +
							modifyTimestamp,
						parseException);
				}
			}

			if (modifiedDate != null) {
				LDAPServerConfiguration ldapServerConfiguration =
					_ldapServerConfigurationProvider.getConfiguration(
						ldapImportContext.getCompanyId(),
						ldapImportContext.getLdapServerId());

				Date expireDate = new Date(
					System.currentTimeMillis() +
						ldapServerConfiguration.clockSkew());

				if (modifiedDate.compareTo(expireDate) > 0) {
					_log.error(
						StringBundler.concat(
							"User import failed because modified date is in ",
							"the future. Increase clock skew value in LDAP ",
							"server configuration to synchronize mismatched ",
							"server times. Current date: ", new Date(),
							" Modified date: ", modifiedDate));

					return user;
				}
			}

			boolean isNew = false;

			if (user == null) {
				user = addUser(
					ldapImportContext.getCompanyId(), ldapUser, password);

				isNew = true;
			}

			try {
				user = _updateUser(
					ldapImportContext, ldapUser, user, password,
					modifyTimestamp, isNew);

				ldapImportContext.addImportedUserId(
					fullUserDN, user.getUserId());
			}
			catch (GroupFriendlyURLException groupFriendlyURLException) {
				int type = groupFriendlyURLException.getType();

				if (type == GroupFriendlyURLException.DUPLICATE) {
					_log.error(
						"Unable to import user " + user.getUserId() +
							" because of a duplicate group friendly URL",
						groupFriendlyURLException);
				}
				else {
					_log.error(
						"Unable to import user " + user.getUserId(),
						groupFriendlyURLException);
				}
			}
			catch (Exception exception) {
				_log.error(
					"Unable to import user " + user.getUserId(), exception);
			}

			return user;
		}
		finally {
			UserImportTransactionThreadLocal.setOriginatesFromImport(false);
		}
	}

	private User _importUserByLdapAttribute(
			long ldapServerId, long companyId, String attributeName,
			String attributeValue)
		throws Exception {

		SafeLdapContext safeLdapContext = null;

		NamingEnumeration<SearchResult> enumeration = null;

		try {
			LDAPServerConfiguration ldapServerConfiguration =
				_ldapServerConfigurationProvider.getConfiguration(
					companyId, ldapServerId);

			safeLdapContext = _safePortalLDAP.getSafeLdapContext(
				ldapServerId, companyId);

			if (safeLdapContext == null) {
				_log.error("Unable to bind to the LDAP server");

				return null;
			}

			if (ldapServerConfiguration.ldapServerId() != ldapServerId) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						StringBundler.concat(
							"LDAP server ID ", ldapServerId,
							" is no longer valid, company ", companyId,
							" now uses ",
							ldapServerConfiguration.ldapServerId()));
				}

				return null;
			}

			SafeLdapFilter safeLdapFilter = null;

			try {
				safeLdapFilter = SafeLdapFilterFactory.fromUnsafeFilter(
					ldapServerConfiguration.userSearchFilter(),
					_ldapFilterValidator);
			}
			catch (LDAPFilterException ldapFilterException) {
				throw new LDAPFilterException(
					"Invalid user search filter: ", ldapFilterException);
			}

			safeLdapFilter = safeLdapFilter.and(
				SafeLdapFilterConstraints.eq(attributeName, attributeValue));

			Properties userMappings = _ldapSettings.getUserMappings(
				ldapServerId, companyId);

			String userMappingsScreenName = GetterUtil.getString(
				userMappings.getProperty("screenName"));

			userMappingsScreenName = StringUtil.toLowerCase(
				userMappingsScreenName);

			SearchControls searchControls = new SearchControls(
				SearchControls.SUBTREE_SCOPE, 1, 0,
				new String[] {userMappingsScreenName}, false, false);

			enumeration = safeLdapContext.search(
				LDAPUtil.getBaseDNSafeLdapName(ldapServerConfiguration),
				safeLdapFilter, searchControls);

			if (!enumeration.hasMoreElements()) {
				return null;
			}

			if (_log.isDebugEnabled()) {
				_log.debug("Search filter returned at least one result");
			}

			Binding binding = enumeration.nextElement();

			Attributes attributes = _safePortalLDAP.getUserAttributes(
				ldapServerId, companyId, safeLdapContext,
				SafeLdapNameFactory.from(binding));

			return importUser(
				ldapServerId, companyId, safeLdapContext, attributes, null);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Problem accessing LDAP server " + exception.getMessage());
			}

			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			throw new SystemException(
				"Problem accessing LDAP server " + exception.getMessage());
		}
		finally {
			if (enumeration != null) {
				enumeration.close();
			}

			if (safeLdapContext != null) {
				safeLdapContext.close();
			}
		}
	}

	private UserGroup _importUserGroup(
			long companyId, Attributes groupAttributes,
			Properties groupMappings)
		throws Exception {

		groupAttributes = _attributesTransformer.transformGroup(
			groupAttributes);

		LDAPGroup ldapGroup = _ldapToPortalConverter.importLDAPGroup(
			companyId, groupAttributes, groupMappings);

		UserGroup userGroup = null;

		try {
			userGroup = _userGroupLocalService.getUserGroup(
				companyId, ldapGroup.getGroupName());

			if (!Objects.equals(
					userGroup.getDescription(), ldapGroup.getDescription())) {

				_userGroupLocalService.updateUserGroup(
					userGroup.getExternalReferenceCode(), companyId,
					userGroup.getUserGroupId(), ldapGroup.getGroupName(),
					ldapGroup.getDescription(), null);
			}
		}
		catch (NoSuchUserGroupException noSuchUserGroupException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(noSuchUserGroupException);
			}

			StopWatch stopWatch = new StopWatch();

			if (_log.isDebugEnabled()) {
				stopWatch.start();

				_log.debug("Adding LDAP group " + ldapGroup);
			}

			long guestUserId = _userLocalService.getGuestUserId(companyId);

			UserGroupImportTransactionThreadLocal.setOriginatesFromImport(true);

			try {
				userGroup = _userGroupLocalService.addUserGroup(
					StringPool.BLANK, guestUserId, companyId,
					ldapGroup.getGroupName(), ldapGroup.getDescription(), null);

				if (_log.isDebugEnabled()) {
					_log.debug(
						StringBundler.concat(
							"Finished adding LDAP group ", ldapGroup,
							" as user group ", userGroup, " in ",
							stopWatch.getTime(), "ms"));
				}
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Unable to create user group " +
							ldapGroup.getGroupName());
				}

				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}
			finally {
				UserGroupImportTransactionThreadLocal.setOriginatesFromImport(
					false);
			}
		}

		_addRole(companyId, ldapGroup, userGroup);

		return userGroup;
	}

	private void _populateExpandoAttributes(
		ExpandoBridge expandoBridge, Map<String, String[]> expandoAttributes,
		Properties expandoMappings, Set<String> ldapUserIgnoreAttributes) {

		Map<String, Serializable> serializedExpandoAttributes = new HashMap<>();

		for (Map.Entry<String, String[]> expandoAttribute :
				expandoAttributes.entrySet()) {

			String name = expandoAttribute.getKey();

			if (!expandoBridge.hasAttribute(name)) {
				continue;
			}

			if (expandoMappings.containsKey(name) &&
				!ldapUserIgnoreAttributes.contains(name)) {

				int type = expandoBridge.getAttributeType(name);

				Serializable value =
					ExpandoConverterUtil.getAttributeFromStringArray(
						type, expandoAttribute.getValue());

				if ((type == ExpandoColumnConstants.STRING_LOCALIZED) &&
					(value != null)) {

					ExpandoValue existingValue =
						_expandoValueLocalService.getValue(
							expandoBridge.getCompanyId(),
							expandoBridge.getClassName(),
							ExpandoTableConstants.DEFAULT_TABLE_NAME, name,
							expandoBridge.getClassPK());

					if (existingValue != null) {
						Map<Locale, String> existingValuesMap =
							_localization.getLocalizationMap(
								existingValue.getData());

						existingValuesMap.putAll((Map<Locale, String>)value);

						value = (Serializable)existingValuesMap;
					}
				}

				serializedExpandoAttributes.put(name, value);
			}
		}

		if (serializedExpandoAttributes.isEmpty()) {
			return;
		}

		try {
			_expandoValueLocalService.addValues(
				expandoBridge.getCompanyId(), expandoBridge.getClassName(),
				ExpandoTableConstants.DEFAULT_TABLE_NAME,
				expandoBridge.getClassPK(), serializedExpandoAttributes);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to populate expando attributes");
			}

			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}
	}

	private void _setProperty(Object bean1, Object bean2, String propertyName) {
		Object value = _beanProperties.getObject(bean2, propertyName);

		_beanProperties.setProperty(bean1, propertyName, value);
	}

	private void _updateLDAPUser(
			User ldapUser, Contact ldapContact, User user,
			Properties userMappings, Properties contactMappings,
			Set<String> ldapUserIgnoreAttributes)
		throws Exception {

		Contact contact = user.getContact();

		for (String propertyName : _CONTACT_PROPERTY_NAMES) {
			String mappingPropertyName = propertyName;

			if (propertyName.equals("male")) {
				mappingPropertyName = ContactConverterKeys.GENDER;
			}
			else if (propertyName.equals("prefixListTypeId")) {
				mappingPropertyName = ContactConverterKeys.PREFIX;
			}
			else if (propertyName.equals("suffixListTypeId")) {
				mappingPropertyName = ContactConverterKeys.SUFFIX;
			}

			if (!contactMappings.containsKey(mappingPropertyName) ||
				ldapUserIgnoreAttributes.contains(propertyName)) {

				_setProperty(ldapContact, contact, propertyName);
			}
		}

		for (String propertyName : _USER_PROPERTY_NAMES) {
			String mappingPropertyName = propertyName;

			if (propertyName.equals("portraitId")) {
				mappingPropertyName = UserConverterKeys.PORTRAIT;
			}

			if (!userMappings.containsKey(mappingPropertyName) ||
				ldapUserIgnoreAttributes.contains(propertyName)) {

				_setProperty(ldapUser, user, propertyName);
			}
		}
	}

	private User _updateUser(
			LDAPImportContext ldapImportContext, LDAPUser ldapUser, User user,
			String password, String modifyTimestamp, boolean isNew)
		throws Exception {

		StopWatch stopWatch = new StopWatch();

		long companyId = ldapImportContext.getCompanyId();
		long ldapServerId = ldapImportContext.getLdapServerId();

		if (_log.isDebugEnabled()) {
			stopWatch.start();

			if (isNew) {
				_log.debug(
					StringBundler.concat(
						"Updating new user ", user, " from LDAP server ",
						ldapServerId, " to company ", companyId));
			}
			else {
				_log.debug(
					StringBundler.concat(
						"Updating existing user ", user, " from LDAP server ",
						ldapServerId, " to company ", companyId));
			}
		}

		LDAPImportConfiguration ldapImportConfiguration =
			_ldapImportConfigurationProvider.getConfiguration(companyId);
		LDAPServerConfiguration ldapServerConfiguration =
			_ldapServerConfigurationProvider.getConfiguration(
				companyId, ldapServerId);

		Date modifiedDate = null;

		try {
			modifiedDate = LDAPUtil.parseDate(modifyTimestamp);
		}
		catch (ParseException parseException) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to parse LDAP modify timestamp " + modifyTimestamp,
					parseException);
			}
		}

		if ((modifiedDate != null) &&
			(modifiedDate.compareTo(new Date()) > 0)) {

			modifiedDate = new Date();
		}

		boolean passwordReset = ldapUser.isPasswordReset();

		if (_ldapSettings.isExportEnabled(companyId)) {
			passwordReset = user.isPasswordReset();
		}

		ServiceContext serviceContext = ldapUser.getServiceContext();
		boolean updatedCustomMappings = false;

		if (Validator.isNotNull(ldapServerConfiguration.modifiedDate())) {
			Date serverConfigurationModifiedDate = DateUtil.parseDate(
				"EEE MMM d HH:mm:ss zzz yyyy",
				ldapServerConfiguration.modifiedDate(),
				serviceContext.getLocale());

			updatedCustomMappings = serverConfigurationModifiedDate.after(
				new Date(_lastImportTime));
		}

		if ((modifiedDate != null) &&
			modifiedDate.equals(user.getModifiedDate()) &&
			!updatedCustomMappings) {

			if ((ldapUser.isUpdatePassword() ||
				 !ldapImportConfiguration.importUserPasswordEnabled()) &&
				!modifiedDate.equals(user.getPasswordModifiedDate())) {

				_updateUserPassword(
					ldapImportConfiguration, user.getUserId(),
					user.getScreenName(), password, passwordReset,
					modifiedDate);

				if (_log.isDebugEnabled()) {
					_log.debug(
						StringBundler.concat(
							"Synchronizing password for ",
							user.getEmailAddress(),
							" because it might be out of date"));
				}
			}

			return user;
		}
		else if ((modifiedDate == null) && !isNew) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Skipping user " + user.getEmailAddress() +
						" because the LDAP entry was never modified");
			}

			return user;
		}

		if (ldapServerConfiguration.ldapServerId() != ldapServerId) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"LDAP server ID ", ldapServerId,
						" is no longer valid, company ", companyId,
						" now uses ", ldapServerConfiguration.ldapServerId()));
			}

			return user;
		}

		String[] userIgnoreAttributes =
			ldapServerConfiguration.userIgnoreAttributes();

		Set<String> ldapUserIgnoreAttributes = new HashSet<>(
			Arrays.asList(userIgnoreAttributes));

		if (Validator.isNull(ldapUser.getScreenName()) ||
			ldapUser.isAutoScreenName()) {

			ldapUser.setScreenName(user.getScreenName());
		}

		if (ldapUser.isUpdatePassword() ||
			!ldapImportConfiguration.importUserPasswordEnabled()) {

			password = _updateUserPassword(
				ldapImportConfiguration, user.getUserId(),
				ldapUser.getScreenName(), password, passwordReset,
				modifiedDate);
		}

		Contact ldapContact = ldapUser.getContact();

		ExpandoBridge userExpandoBridge = user.getExpandoBridge();

		_populateExpandoAttributes(
			userExpandoBridge, ldapUser.getUserExpandoAttributes(),
			ldapImportContext.getUserExpandoMappings(),
			ldapImportContext.getLdapUserIgnoreAttributes());

		Contact contact = user.getContact();

		ExpandoBridge contactExpandoBridge = contact.getExpandoBridge();

		_populateExpandoAttributes(
			contactExpandoBridge, ldapUser.getContactExpandoAttributes(),
			ldapImportContext.getContactExpandoMappings(),
			ldapImportContext.getLdapUserIgnoreAttributes());

		_updateLDAPUser(
			ldapUser.getUser(), ldapContact, user,
			ldapImportContext.getUserMappings(),
			ldapImportContext.getContactMappings(), ldapUserIgnoreAttributes);

		Calendar birthdayCal = CalendarFactoryUtil.getCalendar();

		birthdayCal.setTime(ldapContact.getBirthday());

		int birthdayMonth = birthdayCal.get(Calendar.MONTH);
		int birthdayDay = birthdayCal.get(Calendar.DAY_OF_MONTH);
		int birthdayYear = birthdayCal.get(Calendar.YEAR);

		if (modifiedDate != null) {
			serviceContext.setModifiedDate(modifiedDate);
		}

		if (isNew) {
			_userLocalService.updateEmailAddressVerified(
				user.getUserId(), true);
		}

		user = _userLocalService.updateUser(
			user.getUserId(), password, StringPool.BLANK, StringPool.BLANK,
			passwordReset, ldapUser.getReminderQueryQuestion(),
			ldapUser.getReminderQueryAnswer(), ldapUser.getScreenName(),
			ldapUser.getEmailAddress(), ldapUser.isUpdatePortrait(),
			ldapUser.getPortraitBytes(), ldapUser.getLanguageId(),
			ldapUser.getTimeZoneId(), ldapUser.getGreeting(),
			ldapUser.getComments(), ldapUser.getFirstName(),
			ldapUser.getMiddleName(), ldapUser.getLastName(),
			ldapUser.getPrefixListTypeId(), ldapUser.getSuffixListTypeId(),
			ldapUser.isMale(), birthdayMonth, birthdayDay, birthdayYear,
			ldapUser.getSmsSn(), ldapUser.getFacebookSn(),
			ldapUser.getJabberSn(), ldapUser.getSkypeSn(),
			ldapUser.getTwitterSn(), ldapUser.getJobTitle(),
			ldapUser.getGroupIds(), ldapUser.getOrganizationIds(),
			ldapUser.getRoleIds(), ldapUser.getUserGroupRoles(),
			ldapUser.getUserGroupIds(), serviceContext);

		if (user.getStatus() != ldapUser.getStatus()) {
			user = _userLocalService.updateStatus(
				user, ldapUser.getStatus(), serviceContext);
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"Finished update for user ", user, " in ",
					stopWatch.getTime(), "ms"));
		}

		return user;
	}

	private String _updateUserPassword(
			LDAPImportConfiguration ldapImportConfiguration, long userId,
			String screenName, String password, boolean passwordReset,
			Date modifiedDate)
		throws Exception {

		boolean passwordGenerated = false;

		if (!ldapImportConfiguration.importUserPasswordEnabled()) {
			passwordGenerated = true;

			if (ldapImportConfiguration.importUserPasswordAutogenerated()) {
				password = PwdGenerator.getPassword();
			}
			else {
				password = ldapImportConfiguration.importUserPasswordDefault();

				if (StringUtil.equalsIgnoreCase(
						password, _USER_PASSWORD_SCREEN_NAME)) {

					password = screenName;
				}
			}
		}

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setModifiedDate(modifiedDate);

		try {
			ServiceContextThreadLocal.pushServiceContext(serviceContext);

			User user = _userLocalService.updatePassword(
				userId, password, password, passwordReset, true);

			if (passwordGenerated) {
				user.setDigest(StringPool.BLANK);

				_userLocalService.updateUser(user);
			}
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}

		return password;
	}

	private void _updateUserUserGroups(
			long ldapServerId, long userId, Set<Long> userGroupIds)
		throws Exception {

		List<Long> deleteUserGroupIds = new ArrayList<>();

		for (UserGroup userGroup :
				_userGroupLocalService.getUserUserGroups(userId)) {

			if (userGroup.isAddedByLDAPImport()) {
				long userGroupId = userGroup.getUserGroupId();

				if (userGroupIds.contains(userGroupId)) {
					userGroupIds.remove(userGroupId);
				}
				else {
					ExpandoValue expandoValue =
						_expandoValueLocalService.getValue(
							_getExpandoTableId(userGroup.getCompanyId()),
							_getExpandoColumnId(), userGroupId);

					if ((expandoValue != null) &&
						(expandoValue.getLong() == ldapServerId)) {

						deleteUserGroupIds.add(userGroupId);
					}
				}
			}
		}

		if (!deleteUserGroupIds.isEmpty()) {
			_userGroupLocalService.deleteUserUserGroups(
				userId, ArrayUtil.toLongArray(deleteUserGroupIds));
		}

		if (!userGroupIds.isEmpty()) {
			_userGroupLocalService.addUserUserGroups(
				userId, ArrayUtil.toLongArray(userGroupIds));
		}
	}

	private static final String[] _CONTACT_PROPERTY_NAMES = {
		"birthday", "employeeNumber", "facebookSn", "jabberSn", "male",
		"prefixListTypeId", "skypeSn", "smsSn", "suffixListTypeId", "twitterSn"
	};

	private static final String _IMPORT_BY_GROUP = "group";

	private static final String _IMPORT_BY_USER = "user";

	private static final String _USER_PASSWORD_SCREEN_NAME = "screenName";

	private static final String[] _USER_PROPERTY_NAMES = {
		"comments", "emailAddress", "firstName", "greeting", "jobTitle",
		"languageId", "lastName", "middleName", "openId", "portraitId",
		"timeZoneId", "status"
	};

	private static final String _USER_SYNC_STRATEGY_UUID = "uuid";

	private static final Log _log = LogFactoryUtil.getLog(
		LDAPUserImporterImpl.class);

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	private volatile AttributesTransformer _attributesTransformer;

	@Reference
	private BeanProperties _beanProperties;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CompanyLocalService _companyLocalService;

	private String _companySecurityAuthType;
	private ExpandoColumn _expandoColumn;

	@Reference
	private ExpandoColumnLocalService _expandoColumnLocalService;

	private ExpandoTable _expandoTable;

	@Reference
	private ExpandoTableLocalService _expandoTableLocalService;

	@Reference
	private ExpandoValueLocalService _expandoValueLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	private long _lastImportTime;

	@Reference
	private LDAPFilterValidator _ldapFilterValidator;

	@Reference(
		target = "(factoryPid=com.liferay.portal.security.ldap.exportimport.configuration.LDAPImportConfiguration)"
	)
	private ConfigurationProvider<LDAPImportConfiguration>
		_ldapImportConfigurationProvider;

	@Reference(
		target = "(factoryPid=com.liferay.portal.security.ldap.configuration.LDAPServerConfiguration)"
	)
	private ConfigurationProvider<LDAPServerConfiguration>
		_ldapServerConfigurationProvider;

	@Reference
	private LDAPSettings _ldapSettings;

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	private volatile LDAPToPortalConverter _ldapToPortalConverter;

	@Reference
	private Localization _localization;

	@Reference
	private LockManager _lockManager;

	private PortalCache<String, Long> _portalCache;

	@Reference
	private Props _props;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	private volatile SafePortalLDAP _safePortalLDAP;

	@Reference
	private SingleVMPool _singleVMPool;

	@Reference
	private UserGroupLocalService _userGroupLocalService;

	@Reference
	private UserLocalService _userLocalService;

}