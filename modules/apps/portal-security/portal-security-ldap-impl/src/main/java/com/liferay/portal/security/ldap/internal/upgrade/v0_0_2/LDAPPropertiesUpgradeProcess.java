/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.ldap.internal.upgrade.v0_0_2;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.ldap.LDAPSettings;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.PrefsProps;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.security.ldap.authenticator.configuration.LDAPAuthConfiguration;
import com.liferay.portal.security.ldap.configuration.ConfigurationProvider;
import com.liferay.portal.security.ldap.configuration.LDAPServerConfiguration;
import com.liferay.portal.security.ldap.configuration.SystemLDAPConfiguration;
import com.liferay.portal.security.ldap.constants.LDAPConstants;
import com.liferay.portal.security.ldap.constants.LegacyLDAPPropsKeys;
import com.liferay.portal.security.ldap.exportimport.configuration.LDAPExportConfiguration;
import com.liferay.portal.security.ldap.exportimport.configuration.LDAPImportConfiguration;

import java.util.Collections;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Michael C. Han
 */
public class LDAPPropertiesUpgradeProcess extends UpgradeProcess {

	public LDAPPropertiesUpgradeProcess(
		CompanyLocalService companyLocalService,
		ConfigurationProvider<LDAPAuthConfiguration>
			ldapAuthConfigurationProvider,
		ConfigurationProvider<LDAPExportConfiguration>
			ldapExportConfigurationProvider,
		ConfigurationProvider<LDAPImportConfiguration>
			ldapImportConfigurationProvider,
		ConfigurationProvider<LDAPServerConfiguration>
			ldapServerConfigurationProvider,
		LDAPSettings ldapSettings, PrefsProps prefsProps,
		ConfigurationProvider<SystemLDAPConfiguration>
			systemLDAPConfigurationProvider) {

		_companyLocalService = companyLocalService;
		_ldapAuthConfigurationProvider = ldapAuthConfigurationProvider;
		_ldapExportConfigurationProvider = ldapExportConfigurationProvider;
		_ldapImportConfigurationProvider = ldapImportConfigurationProvider;
		_ldapServerConfigurationProvider = ldapServerConfigurationProvider;
		_ldapSettings = ldapSettings;
		_prefsProps = prefsProps;
		_systemLDAPConfigurationProvider = systemLDAPConfigurationProvider;
	}

	@Override
	protected void doUpgrade() throws Exception {
		_upgradeLDAPProperties();
	}

	private void _upgradeLDAPAuthProperties(long companyId) {
		Dictionary<String, Object> dictionary =
			HashMapDictionaryBuilder.<String, Object>put(
				LDAPConstants.AUTH_ENABLED,
				_prefsProps.getBoolean(
					companyId, LegacyLDAPPropsKeys.LDAP_AUTH_ENABLED, false)
			).put(
				LDAPConstants.AUTH_METHOD,
				_prefsProps.getString(
					companyId, LegacyLDAPPropsKeys.LDAP_AUTH_METHOD, "bind")
			).put(
				LDAPConstants.AUTH_REQUIRED,
				_prefsProps.getBoolean(
					companyId, LegacyLDAPPropsKeys.LDAP_AUTH_REQUIRED, false)
			).put(
				LDAPConstants.PASSWORD_ENCRYPTION_ALGORITHM,
				_prefsProps.getString(
					companyId,
					LegacyLDAPPropsKeys.LDAP_AUTH_PASSWORD_ENCRYPTION_ALGORITHM,
					"NONE")
			).put(
				LDAPConstants.PASSWORD_POLICY_ENABLED,
				_prefsProps.getBoolean(
					companyId, LegacyLDAPPropsKeys.LDAP_PASSWORD_POLICY_ENABLED,
					false)
			).build();

		if (_log.isInfoEnabled()) {
			_log.info(
				StringBundler.concat(
					"Adding LDAP auth configuration for company ", companyId,
					" with properties: ", dictionary));
		}

		_ldapAuthConfigurationProvider.updateProperties(companyId, dictionary);
	}

	private void _upgradeLDAPExportProperties(long companyId) {
		Dictionary<String, Object> dictionary =
			HashMapDictionaryBuilder.<String, Object>put(
				LDAPConstants.EXPORT_ENABLED,
				_prefsProps.getBoolean(
					companyId, LegacyLDAPPropsKeys.LDAP_EXPORT_ENABLED, false)
			).put(
				LDAPConstants.EXPORT_GROUP_ENABLED,
				_prefsProps.getBoolean(
					companyId, LegacyLDAPPropsKeys.LDAP_EXPORT_GROUP_ENABLED,
					false)
			).build();

		if (_log.isInfoEnabled()) {
			_log.info(
				StringBundler.concat(
					"Adding LDAP export configuration for company ", companyId,
					" with properties: ", dictionary));
		}

		_ldapExportConfigurationProvider.updateProperties(
			companyId, dictionary);
	}

	private void _upgradeLDAPImportProperties(long companyId) {
		Dictionary<String, Object> dictionary =
			HashMapDictionaryBuilder.<String, Object>put(
				LDAPConstants.IMPORT_CREATE_ROLE_PER_GROUP,
				_prefsProps.getBoolean(
					companyId,
					LegacyLDAPPropsKeys.LDAP_IMPORT_CREATE_ROLE_PER_GROUP,
					false)
			).put(
				LDAPConstants.IMPORT_ENABLED,
				_prefsProps.getBoolean(
					companyId, LegacyLDAPPropsKeys.LDAP_IMPORT_ENABLED, false)
			).put(
				LDAPConstants.IMPORT_GROUP_CACHE_ENABLED,
				_prefsProps.getBoolean(
					companyId,
					LegacyLDAPPropsKeys.LDAP_IMPORT_GROUP_CACHE_ENABLED, false)
			).put(
				LDAPConstants.IMPORT_INTERVAL,
				_prefsProps.getInteger(
					companyId, LegacyLDAPPropsKeys.LDAP_IMPORT_INTERVAL, 10)
			).put(
				LDAPConstants.IMPORT_LOCK_EXPIRATION_TIME,
				_prefsProps.getLong(
					companyId,
					LegacyLDAPPropsKeys.LDAP_IMPORT_LOCK_EXPIRATION_TIME,
					86400000)
			).put(
				LDAPConstants.IMPORT_METHOD,
				_prefsProps.getString(
					companyId, LegacyLDAPPropsKeys.LDAP_IMPORT_METHOD, "user")
			).put(
				LDAPConstants.IMPORT_ON_STARTUP,
				_prefsProps.getBoolean(
					companyId, LegacyLDAPPropsKeys.LDAP_IMPORT_ON_STARTUP,
					false)
			).put(
				LDAPConstants.IMPORT_USER_PASSWORD_AUTOGENERATED,
				_prefsProps.getBoolean(
					companyId,
					LegacyLDAPPropsKeys.LDAP_IMPORT_USER_PASSWORD_AUTOGENERATED,
					false)
			).put(
				LDAPConstants.IMPORT_USER_PASSWORD_DEFAULT,
				_prefsProps.getString(
					companyId,
					LegacyLDAPPropsKeys.LDAP_IMPORT_USER_PASSWORD_DEFAULT,
					"test")
			).put(
				LDAPConstants.IMPORT_USER_PASSWORD_ENABLED,
				_prefsProps.getBoolean(
					companyId,
					LegacyLDAPPropsKeys.LDAP_IMPORT_USER_PASSWORD_ENABLED, true)
			).put(
				LDAPConstants.IMPORT_USER_SYNC_STRATEGY,
				_prefsProps.getString(
					companyId,
					LegacyLDAPPropsKeys.LDAP_IMPORT_USER_SYNC_STRATEGY,
					"auth-type")
			).build();

		if (_log.isInfoEnabled()) {
			_log.info(
				StringBundler.concat(
					"Adding LDAP import configuration for company ", companyId,
					" with properties: ", dictionary));
		}

		_ldapImportConfigurationProvider.updateProperties(
			companyId, dictionary);
	}

	private void _upgradeLDAPProperties() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			_companyLocalService.forEachCompanyId(
				companyId -> {
					long[] ldapServerIds = StringUtil.split(
						_prefsProps.getString(companyId, "ldap.server.ids"),
						0L);

					if (ArrayUtil.isEmpty(ldapServerIds)) {
						return;
					}

					_upgradeLDAPAuthProperties(companyId);
					_upgradeLDAPExportProperties(companyId);
					_upgradeLDAPImportProperties(companyId);
					_upgradeSystemLDAPConfiguration(companyId);

					Set<String> keys = new HashSet<>();

					Collections.addAll(
						keys, LegacyLDAPPropsKeys.LDAP_KEYS_NONPOSTFIXED);

					for (long ldapServerId : ldapServerIds) {
						String postfix = _ldapSettings.getPropertyPostfix(
							ldapServerId);

						_upgradeLDAPServerConfiguration(
							companyId, ldapServerId, postfix);

						for (int i = 0;
							 i < LegacyLDAPPropsKeys.LDAP_KEYS_POSTFIXED.length;
							 i++) {

							keys.add(
								LegacyLDAPPropsKeys.LDAP_KEYS_POSTFIXED[i] +
									postfix);
						}
					}

					if (_log.isInfoEnabled()) {
						_log.info(
							StringBundler.concat(
								"Removing preference keys ", keys, " for ",
								"company ", companyId));
					}

					_companyLocalService.removePreferences(
						companyId, keys.toArray(new String[0]));

					if (_log.isInfoEnabled()) {
						_log.info(
							StringBundler.concat(
								"Removing LDAP server IDs ",
								ListUtil.fromArray(ldapServerIds), " for ",
								"company ", companyId));
					}

					_companyLocalService.updatePreferences(
						companyId,
						UnicodePropertiesBuilder.put(
							"ldap.server.ids", StringPool.BLANK
						).build());
				});
		}
	}

	private void _upgradeLDAPServerConfiguration(
		long companyId, long ldapServerId, String postfix) {

		Dictionary<String, Object> dictionary =
			HashMapDictionaryBuilder.<String, Object>put(
				LDAPConstants.AUTH_SEARCH_FILTER,
				_prefsProps.getString(
					companyId,
					LegacyLDAPPropsKeys.LDAP_AUTH_SEARCH_FILTER + postfix,
					"(mail=@email_address@)")
			).put(
				LDAPConstants.BASE_DN,
				_prefsProps.getString(
					companyId, LegacyLDAPPropsKeys.LDAP_BASE_DN + postfix,
					"dc=example,dc=com")
			).put(
				LDAPConstants.BASE_PROVIDER_URL,
				_prefsProps.getString(
					companyId,
					LegacyLDAPPropsKeys.LDAP_BASE_PROVIDER_URL + postfix,
					"ldap://localhost:10389")
			).put(
				LDAPConstants.CONTACT_CUSTOM_MAPPINGS,
				_prefsProps.getStringArray(
					companyId,
					LegacyLDAPPropsKeys.LDAP_CONTACT_CUSTOM_MAPPINGS + postfix,
					StringPool.NEW_LINE)
			).put(
				LDAPConstants.CONTACT_MAPPINGS,
				_prefsProps.getStringArray(
					companyId,
					LegacyLDAPPropsKeys.LDAP_CONTACT_MAPPINGS + postfix,
					StringPool.NEW_LINE)
			).put(
				LDAPConstants.GROUP_DEFAULT_OBJECT_CLASSES,
				_prefsProps.getStringArray(
					companyId,
					LegacyLDAPPropsKeys.LDAP_GROUP_DEFAULT_OBJECT_CLASSES +
						postfix,
					StringPool.COMMA)
			).put(
				LDAPConstants.GROUP_MAPPINGS,
				_prefsProps.getStringArray(
					companyId,
					LegacyLDAPPropsKeys.LDAP_GROUP_MAPPINGS + postfix,
					StringPool.NEW_LINE)
			).put(
				LDAPConstants.GROUP_SEARCH_FILTER,
				_prefsProps.getString(
					companyId,
					LegacyLDAPPropsKeys.LDAP_IMPORT_GROUP_SEARCH_FILTER +
						postfix,
					"(objectClass=groupOfUniqueNames)")
			).put(
				LDAPConstants.GROUP_SEARCH_FILTER_ENABLED,
				_prefsProps.getBoolean(
					companyId,
					LegacyLDAPPropsKeys.
						LDAP_IMPORT_GROUP_SEARCH_FILTER_ENABLED + postfix,
					true)
			).put(
				LDAPConstants.GROUPS_DN,
				_prefsProps.getString(
					companyId, LegacyLDAPPropsKeys.LDAP_GROUPS_DN + postfix,
					"ou=groups,dc=example,dc=com")
			).put(
				LDAPConstants.SECURITY_CREDENTIAL,
				_prefsProps.getString(
					companyId,
					LegacyLDAPPropsKeys.LDAP_SECURITY_CREDENTIALS + postfix,
					"secret")
			).put(
				LDAPConstants.SECURITY_PRINCIPAL,
				_prefsProps.getString(
					companyId,
					LegacyLDAPPropsKeys.LDAP_SECURITY_PRINCIPAL + postfix,
					"uid=admin,ou=system")
			).put(
				LDAPConstants.SERVER_NAME,
				_prefsProps.getString(
					companyId, LegacyLDAPPropsKeys.LDAP_SERVER_NAME + postfix)
			).put(
				LDAPConstants.USER_CUSTOM_MAPPINGS,
				_prefsProps.getStringArray(
					companyId,
					LegacyLDAPPropsKeys.LDAP_USER_CUSTOM_MAPPINGS + postfix,
					StringPool.NEW_LINE)
			).put(
				LDAPConstants.USER_DEFAULT_OBJECT_CLASSES,
				_prefsProps.getStringArray(
					companyId,
					LegacyLDAPPropsKeys.LDAP_USER_DEFAULT_OBJECT_CLASSES +
						postfix,
					StringPool.COMMA)
			).put(
				LDAPConstants.USER_IGNORE_ATTRIBUTES,
				_prefsProps.getStringArray(
					companyId,
					LegacyLDAPPropsKeys.LDAP_USER_IGNORE_ATTRIBUTES + postfix,
					StringPool.COMMA)
			).put(
				LDAPConstants.USER_MAPPINGS,
				_prefsProps.getStringArray(
					companyId, LegacyLDAPPropsKeys.LDAP_USER_MAPPINGS + postfix,
					StringPool.NEW_LINE)
			).put(
				LDAPConstants.USER_SEARCH_FILTER,
				_prefsProps.getString(
					companyId,
					LegacyLDAPPropsKeys.LDAP_IMPORT_USER_SEARCH_FILTER +
						postfix,
					"(objectClass=inetOrgPerson)")
			).put(
				LDAPConstants.USERS_DN,
				_prefsProps.getString(
					companyId, LegacyLDAPPropsKeys.LDAP_USERS_DN + postfix,
					"users,dc=example,dc=com")
			).build();

		if (_log.isInfoEnabled()) {
			_log.info(
				StringBundler.concat(
					"Adding LDAP servier configuration for company ", companyId,
					" and LDAP server ID ", ldapServerId, " with properties: ",
					dictionary));
		}

		_ldapServerConfigurationProvider.updateProperties(
			companyId, ldapServerId, dictionary);
	}

	private void _upgradeSystemLDAPConfiguration(long companyId) {
		Dictionary<String, Object> dictionary =
			HashMapDictionaryBuilder.<String, Object>put(
				LDAPConstants.ERROR_PASSWORD_AGE_KEYWORDS,
				new String[] {
					_prefsProps.getString(
						companyId, LegacyLDAPPropsKeys.LDAP_ERROR_PASSWORD_AGE,
						"age")
				}
			).put(
				LDAPConstants.ERROR_PASSWORD_EXPIRED_KEYWORDS,
				new String[] {
					_prefsProps.getString(
						companyId,
						LegacyLDAPPropsKeys.LDAP_ERROR_PASSWORD_EXPIRED,
						"expired")
				}
			).put(
				LDAPConstants.ERROR_PASSWORD_HISTORY_KEYWORDS,
				new String[] {
					_prefsProps.getString(
						companyId,
						LegacyLDAPPropsKeys.LDAP_ERROR_PASSWORD_HISTORY,
						"history")
				}
			).put(
				LDAPConstants.ERROR_PASSWORD_NOT_CHANGEABLE_KEYWORDS,
				new String[] {
					_prefsProps.getString(
						companyId,
						LegacyLDAPPropsKeys.LDAP_ERROR_PASSWORD_NOT_CHANGEABLE,
						"not allowed to change")
				}
			).put(
				LDAPConstants.ERROR_PASSWORD_SYNTAX_KEYWORDS,
				new String[] {
					_prefsProps.getString(
						companyId,
						LegacyLDAPPropsKeys.LDAP_ERROR_PASSWORD_SYNTAX,
						"syntax")
				}
			).put(
				LDAPConstants.ERROR_PASSWORD_TRIVIAL_KEYWORDS,
				new String[] {
					_prefsProps.getString(
						companyId,
						LegacyLDAPPropsKeys.LDAP_ERROR_PASSWORD_TRIVIAL,
						"trivial")
				}
			).put(
				LDAPConstants.ERROR_USER_LOCKOUT_KEYWORDS,
				new String[] {
					_prefsProps.getString(
						companyId, LegacyLDAPPropsKeys.LDAP_ERROR_USER_LOCKOUT,
						"retry limit")
				}
			).put(
				LDAPConstants.FACTORY_INITIAL,
				_prefsProps.getString(
					companyId, LegacyLDAPPropsKeys.LDAP_FACTORY_INITIAL,
					"com.sun.jndi.ldap.LdapCtxFactory")
			).put(
				LDAPConstants.PAGE_SIZE,
				_prefsProps.getInteger(
					companyId, LegacyLDAPPropsKeys.LDAP_PAGE_SIZE, 1000)
			).put(
				LDAPConstants.RANGE_SIZE,
				_prefsProps.getInteger(
					companyId, LegacyLDAPPropsKeys.LDAP_RANGE_SIZE, 1000)
			).put(
				LDAPConstants.REFERRAL,
				_prefsProps.getString(
					companyId, LegacyLDAPPropsKeys.LDAP_REFERRAL,
					LDAPConstants.REFERRAL_FOLLOW)
			).build();

		if (_log.isInfoEnabled()) {
			_log.info(
				StringBundler.concat(
					"Adding system LDAP configurations for company ", companyId,
					" with properties: ", dictionary));
		}

		_systemLDAPConfigurationProvider.updateProperties(
			companyId, dictionary);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LDAPPropertiesUpgradeProcess.class);

	private final CompanyLocalService _companyLocalService;
	private final ConfigurationProvider<LDAPAuthConfiguration>
		_ldapAuthConfigurationProvider;
	private final ConfigurationProvider<LDAPExportConfiguration>
		_ldapExportConfigurationProvider;
	private final ConfigurationProvider<LDAPImportConfiguration>
		_ldapImportConfigurationProvider;
	private final ConfigurationProvider<LDAPServerConfiguration>
		_ldapServerConfigurationProvider;
	private final LDAPSettings _ldapSettings;
	private final PrefsProps _prefsProps;
	private final ConfigurationProvider<SystemLDAPConfiguration>
		_systemLDAPConfigurationProvider;

}