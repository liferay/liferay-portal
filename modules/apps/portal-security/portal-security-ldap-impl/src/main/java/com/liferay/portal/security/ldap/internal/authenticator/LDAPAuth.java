/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.ldap.internal.authenticator;

import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PasswordExpiredException;
import com.liferay.portal.kernel.exception.UserLockoutException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.AuthException;
import com.liferay.portal.kernel.security.auth.Authenticator;
import com.liferay.portal.kernel.security.auth.PasswordModificationThreadLocal;
import com.liferay.portal.kernel.security.ldap.LDAPSettings;
import com.liferay.portal.kernel.security.pwd.PasswordEncryptor;
import com.liferay.portal.kernel.security.pwd.PasswordEncryptorUtil;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.ldap.SafeLdapContext;
import com.liferay.portal.security.ldap.SafeLdapFilterTemplate;
import com.liferay.portal.security.ldap.SafeLdapNameFactory;
import com.liferay.portal.security.ldap.SafePortalLDAP;
import com.liferay.portal.security.ldap.authenticator.configuration.LDAPAuthConfiguration;
import com.liferay.portal.security.ldap.configuration.ConfigurationProvider;
import com.liferay.portal.security.ldap.configuration.LDAPServerConfiguration;
import com.liferay.portal.security.ldap.configuration.SystemLDAPConfiguration;
import com.liferay.portal.security.ldap.constants.LDAPConstants;
import com.liferay.portal.security.ldap.exportimport.LDAPUserImporter;
import com.liferay.portal.security.ldap.exportimport.configuration.LDAPImportConfiguration;
import com.liferay.portal.security.ldap.internal.util.SafeLDAPReferralUtil;
import com.liferay.portal.security.ldap.util.LDAPUtil;
import com.liferay.portal.security.ldap.validator.LDAPFilterValidator;
import com.liferay.portlet.admin.util.OmniadminUtil;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Brian Wing Shun Chan
 * @author Scott Lee
 * @author Josef Sustacek
 */
@Component(property = "key=auth.pipeline.pre", service = Authenticator.class)
public class LDAPAuth implements Authenticator {

	public static final String RESULT_PASSWORD_EXP_WARNING =
		"2.16.840.1.113730.3.4.5";

	public static final String RESULT_PASSWORD_RESET =
		"2.16.840.1.113730.3.4.4";

	@Override
	public int authenticateByEmailAddress(
			long companyId, String emailAddress, String password,
			Map<String, String[]> headerMap, Map<String, String[]> parameterMap)
		throws AuthException {

		try {
			return _authenticate(
				companyId, emailAddress, StringPool.BLANK, 0, password);
		}
		catch (Exception exception) {
			_log.error(exception);

			throw new AuthException(exception);
		}
	}

	@Override
	public int authenticateByScreenName(
			long companyId, String screenName, String password,
			Map<String, String[]> headerMap, Map<String, String[]> parameterMap)
		throws AuthException {

		try {
			return _authenticate(
				companyId, StringPool.BLANK, screenName, 0, password);
		}
		catch (Exception exception) {
			_log.error(exception);

			throw new AuthException(exception);
		}
	}

	@Override
	public int authenticateByUserId(
			long companyId, long userId, String password,
			Map<String, String[]> headerMap, Map<String, String[]> parameterMap)
		throws AuthException {

		try {
			return _authenticate(
				companyId, StringPool.BLANK, StringPool.BLANK, userId,
				password);
		}
		catch (Exception exception) {
			_log.error(exception);

			throw new AuthException(exception);
		}
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_authPipelineEnableLiferayCheck = GetterUtil.getBoolean(
			PropsUtil.get(PropsKeys.AUTH_PIPELINE_ENABLE_LIFERAY_CHECK));
	}

	private LDAPAuthResult _authenticate(
			LdapContext ctx, long companyId, Attributes attributes,
			String userDN, String password)
		throws Exception {

		LDAPAuthResult ldapAuthResult = null;

		// Check passwords by either doing a comparison between the passwords or
		// by binding to the LDAP server. If using LDAP password policies, bind
		// auth method must be used in order to get the result control codes.

		LDAPAuthConfiguration ldapAuthConfiguration =
			_ldapAuthConfigurationProvider.getConfiguration(companyId);

		String authMethod = ldapAuthConfiguration.method();

		if (authMethod.equals(LDAPConstants.AUTH_METHOD_BIND)) {
			Hashtable<String, Object> env =
				(Hashtable<String, Object>)ctx.getEnvironment();

			SystemLDAPConfiguration systemLDAPConfiguration =
				_systemLDAPConfigurationProvider.getConfiguration(companyId);

			SafeLDAPReferralUtil.setProperties(
				env, systemLDAPConfiguration.referral());

			env.put(Context.SECURITY_CREDENTIALS, password);
			env.put(Context.SECURITY_PRINCIPAL, userDN);

			// Do not use pooling because principal changes

			env.put("com.sun.jndi.ldap.connect.pool", "false");

			ldapAuthResult = _getFailedLDAPAuthResult(env);

			if (ldapAuthResult != null) {
				return ldapAuthResult;
			}

			ldapAuthResult = new LDAPAuthResult();

			InitialLdapContext initialLdapContext = null;

			try {
				initialLdapContext = new InitialLdapContext(env, null);

				// Get LDAP bind results

				ldapAuthResult.setAuthenticated(true);
				ldapAuthResult.setResponseControl(
					initialLdapContext.getResponseControls());
			}
			catch (Exception exception) {
				boolean authenticationException = false;

				if (exception instanceof AuthenticationException) {
					authenticationException = true;
				}

				if (_log.isDebugEnabled()) {
					if (authenticationException) {
						_log.debug(
							StringBundler.concat(
								"Failed to bind to the LDAP server, wrong ",
								"password provided for userDN ", userDN),
							exception);
					}
					else {
						_log.debug(
							"Failed to bind to the LDAP server with userDN " +
								userDN,
							exception);
					}
				}
				else if (_log.isWarnEnabled() && !authenticationException) {
					_log.warn(
						StringBundler.concat(
							"Failed to bind to the LDAP server with userDN ",
							userDN, " :", exception.getMessage()));
				}

				ldapAuthResult.setAuthenticated(false);
				ldapAuthResult.setErrorMessage(exception.getMessage());

				_setFailedLDAPAuthResult(env, ldapAuthResult);
			}
			finally {
				if (initialLdapContext != null) {
					initialLdapContext.close();
				}
			}
		}
		else if (authMethod.equals(
					LDAPConstants.AUTH_METHOD_PASSWORD_COMPARE)) {

			ldapAuthResult = new LDAPAuthResult();

			Attribute userPassword = attributes.get("userPassword");

			if (userPassword != null) {
				String encryptedPassword = password;
				String ldapPassword = new String((byte[])userPassword.get());

				if (Validator.isNotNull(
						ldapAuthConfiguration.passwordEncryptionAlgorithm()) &&
					!Objects.equals(
						ldapAuthConfiguration.passwordEncryptionAlgorithm(),
						PasswordEncryptor.TYPE_NONE)) {

					ldapPassword = _removeEncryptionAlgorithm(ldapPassword);

					encryptedPassword = PasswordEncryptorUtil.encrypt(
						ldapAuthConfiguration.passwordEncryptionAlgorithm(),
						password, ldapPassword);
				}

				if (ldapPassword.equals(encryptedPassword)) {
					ldapAuthResult.setAuthenticated(true);
				}
				else {
					ldapAuthResult.setAuthenticated(false);

					if (_log.isDebugEnabled()) {
						_log.debug(
							"Passwords do not match for userDN " + userDN);
					}
				}
			}
		}

		return ldapAuthResult;
	}

	private int _authenticate(
			long ldapServerId, long companyId, String emailAddress,
			String screenName, long userId, String password)
		throws Exception {

		SafeLdapContext safeLdapContext = _portalLDAP.getSafeLdapContext(
			ldapServerId, companyId);

		if (safeLdapContext == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"No LDAP server configuration available for LDAP ",
						"server ", ldapServerId, " and company ", companyId));
			}

			return FAILURE;
		}

		NamingEnumeration<SearchResult> enumeration = null;

		try {
			LDAPServerConfiguration ldapServerConfiguration =
				_ldapServerConfigurationProvider.getConfiguration(
					companyId, ldapServerId);

			if (ldapServerConfiguration.ldapServerId() != ldapServerId) {
				return DNE;
			}

			//  Process LDAP auth search filter

			SafeLdapFilterTemplate authSearchSafeLdapFilterTemplate =
				LDAPUtil.getAuthSearchSafeLdapFilterTemplate(
					ldapServerConfiguration, _ldapFilterValidator);

			if (authSearchSafeLdapFilterTemplate == null) {
				_log.error("Missing authSearchFilter");

				return FAILURE;
			}

			authSearchSafeLdapFilterTemplate =
				authSearchSafeLdapFilterTemplate.replace(
					new String[] {
						"@company_id@", "@email_address@", "@screen_name@",
						"@user_id@"
					},
					new String[] {
						String.valueOf(companyId), emailAddress, screenName,
						String.valueOf(userId)
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
				if (_log.isDebugEnabled()) {
					_log.debug(
						"No results found with search filter: " +
							authSearchSafeLdapFilterTemplate);
				}

				return DNE;
			}

			if (_log.isDebugEnabled()) {
				_log.debug(
					"Found results with search filter: " +
						authSearchSafeLdapFilterTemplate);
			}

			SearchResult searchResult = enumeration.nextElement();

			Attributes attributes = _portalLDAP.getUserAttributes(
				ldapServerId, companyId, safeLdapContext,
				SafeLdapNameFactory.from(searchResult));

			// Authenticate

			String fullUserDN = searchResult.getNameInNamespace();

			LDAPAuthResult ldapAuthResult = _authenticate(
				safeLdapContext, companyId, attributes, fullUserDN, password);

			LDAPImportConfiguration ldapImportConfiguration =
				_ldapImportConfigurationProvider.getConfiguration(companyId);

			if (!ldapAuthResult.isAuthenticated() ||
				ldapImportConfiguration.importUserPasswordAutogenerated()) {

				password = null;
			}

			User user = _ldapUserImporter.importUser(
				ldapServerId, companyId, safeLdapContext, attributes, password);

			if (!ldapAuthResult.isAuthenticated()) {
				PasswordModificationThreadLocal.setPasswordModified(false);
			}

			// Process LDAP failure codes

			String errorMessage = ldapAuthResult.getErrorMessage();

			if (errorMessage != null) {
				SystemLDAPConfiguration systemLDAPConfiguration =
					_systemLDAPConfigurationProvider.getConfiguration(
						companyId);

				for (String errorUserLockoutKeyword :
						systemLDAPConfiguration.errorUserLockoutKeywords()) {

					if (errorMessage.contains(errorUserLockoutKeyword)) {
						throw new UserLockoutException.LDAPLockout(
							fullUserDN, errorMessage);
					}
				}

				for (String errorPasswordExpiredKeyword :
						systemLDAPConfiguration.
							errorPasswordExpiredKeywords()) {

					if (errorMessage.contains(errorPasswordExpiredKeyword)) {
						throw new PasswordExpiredException();
					}
				}
			}

			if (!ldapAuthResult.isAuthenticated()) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						StringBundler.concat(
							"Unable to authenticate with ", fullUserDN,
							" on LDAP server ", ldapServerId, ", company ",
							companyId, ", and LDAP context ", safeLdapContext,
							": ", errorMessage));
				}

				return FAILURE;
			}

			if (user == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						StringBundler.concat(
							"Rejecting authenticated user ", fullUserDN,
							" because of failed import from LDAP server ",
							ldapServerId, ", company ", companyId,
							", and LDAP context ", safeLdapContext));
				}

				return FAILURE;
			}

			// Process LDAP success codes

			String resultCode = ldapAuthResult.getResponseControl();

			if (resultCode.equals(LDAPAuth.RESULT_PASSWORD_RESET)) {
				_userLocalService.updatePasswordReset(user.getUserId(), true);
			}
		}
		catch (Exception exception) {
			if (exception instanceof PasswordExpiredException ||
				exception instanceof UserLockoutException) {

				throw exception;
			}

			_log.error("Problem accessing LDAP server", exception);

			return FAILURE;
		}
		finally {
			if (enumeration != null) {
				enumeration.close();
			}

			safeLdapContext.close();
		}

		return SUCCESS;
	}

	private int _authenticate(
			long companyId, String emailAddress, String screenName, long userId,
			String password)
		throws Exception {

		LDAPAuthConfiguration ldapAuthConfiguration =
			_ldapAuthConfigurationProvider.getConfiguration(companyId);

		if (!ldapAuthConfiguration.enabled()) {
			if (_log.isDebugEnabled()) {
				_log.debug("Authenticator is not enabled");
			}

			return SUCCESS;
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Authenticator is enabled");
		}

		long preferredLDAPServerId = _getPreferredLDAPServer(
			companyId, emailAddress, screenName, userId);

		int preferredLDAPServerResult = _authenticateAgainstPreferredLDAPServer(
			companyId, preferredLDAPServerId, emailAddress, screenName, userId,
			password);

		LDAPImportConfiguration ldapImportConfiguration =
			_ldapImportConfigurationProvider.getConfiguration(companyId);

		if (preferredLDAPServerResult == SUCCESS) {
			if (_log.isDebugEnabled()) {
				_log.debug("Found preferred LDAP server");
			}

			if (ldapImportConfiguration.importUserPasswordEnabled()) {
				if (_log.isDebugEnabled()) {
					_log.debug("Import user password enabled");
				}

				return preferredLDAPServerResult;
			}

			if (_log.isDebugEnabled()) {
				_log.debug("Import user password disabled");
			}

			return Authenticator.SKIP_LIFERAY_CHECK;
		}

		List<LDAPServerConfiguration> ldapServerConfigurations =
			_ldapServerConfigurationProvider.getConfigurations(companyId);

		for (LDAPServerConfiguration ldapServerConfiguration :
				ldapServerConfigurations) {

			if (preferredLDAPServerId ==
					ldapServerConfiguration.ldapServerId()) {

				if (_log.isDebugEnabled()) {
					_log.debug("Bypassing preferred LDAP server");
				}

				continue;
			}

			int result = _authenticate(
				ldapServerConfiguration.ldapServerId(), companyId, emailAddress,
				screenName, userId, password);

			if (result == SUCCESS) {
				if (ldapImportConfiguration.importUserPasswordEnabled()) {
					return result;
				}

				return Authenticator.SKIP_LIFERAY_CHECK;
			}
		}

		return _authenticateRequired(
			companyId, userId, emailAddress, screenName, true, FAILURE);
	}

	private int _authenticateAgainstPreferredLDAPServer(
			long companyId, long ldapServerId, String emailAddress,
			String screenName, long userId, String password)
		throws Exception {

		if (ldapServerId < 0) {
			if (_log.isDebugEnabled()) {
				_log.debug("No preferred LDAP server ID provided");
			}

			return DNE;
		}

		LDAPServerConfiguration ldapServerConfiguration =
			_ldapServerConfigurationProvider.getConfiguration(
				companyId, ldapServerId);

		if (Validator.isNull(ldapServerConfiguration.baseProviderURL())) {
			if (_log.isDebugEnabled()) {
				_log.debug("Base provider URL is not set");
			}

			return DNE;
		}

		if (ldapServerConfiguration.ldapServerId() != ldapServerId) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"LDAP server ", ldapServerId,
						" is invalid because company ", companyId, " uses ",
						ldapServerConfiguration.ldapServerId()));
			}

			return DNE;
		}

		return _authenticate(
			ldapServerId, companyId, emailAddress, screenName, userId,
			password);
	}

	private int _authenticateOmniadmin(
			long companyId, String emailAddress, String screenName, long userId)
		throws Exception {

		// Only allow omniadmin if Liferay password checking is enabled

		if (!_authPipelineEnableLiferayCheck) {
			return FAILURE;
		}

		if (userId > 0) {
			if (OmniadminUtil.isOmniadmin(userId)) {
				return SUCCESS;
			}
		}
		else if (Validator.isNotNull(emailAddress)) {
			User user = _userLocalService.fetchUserByEmailAddress(
				companyId, emailAddress);

			if ((user != null) && OmniadminUtil.isOmniadmin(user)) {
				return SUCCESS;
			}
		}
		else if (Validator.isNotNull(screenName)) {
			User user = _userLocalService.fetchUserByScreenName(
				companyId, screenName);

			if ((user != null) && OmniadminUtil.isOmniadmin(user)) {
				return SUCCESS;
			}
		}

		return FAILURE;
	}

	private int _authenticateRequired(
			long companyId, long userId, String emailAddress, String screenName,
			boolean allowOmniadmin, int failureCode)
		throws Exception {

		// Make exceptions for omniadmins so that if they break the LDAP
		// configuration, they can still login to fix the problem

		if (allowOmniadmin) {
			int code = _authenticateOmniadmin(
				companyId, emailAddress, screenName, userId);

			if (code == SUCCESS) {
				return SUCCESS;
			}
		}

		LDAPAuthConfiguration ldapAuthConfiguration =
			_ldapAuthConfigurationProvider.getConfiguration(companyId);

		if (ldapAuthConfiguration.required()) {
			return failureCode;
		}

		return SUCCESS;
	}

	private LDAPAuthResult _getFailedLDAPAuthResult(Map<String, Object> env) {
		Map<String, LDAPAuthResult> failedLDAPAuthResults =
			_failedLDAPAuthResults.get();

		String cacheKey = _getKey(env);

		return failedLDAPAuthResults.get(cacheKey);
	}

	private String _getKey(Map<String, Object> env) {
		return StringBundler.concat(
			MapUtil.getString(env, Context.PROVIDER_URL), StringPool.POUND,
			MapUtil.getString(env, Context.SECURITY_PRINCIPAL),
			StringPool.POUND,
			MapUtil.getString(env, Context.SECURITY_CREDENTIALS));
	}

	private long _getPreferredLDAPServer(
			long companyId, String emailAddress, String screenName, long userId)
		throws Exception {

		User user = null;

		if (userId > 0) {
			user = _userLocalService.fetchUserById(userId);
		}
		else if (Validator.isNotNull(emailAddress)) {
			user = _userLocalService.fetchUserByEmailAddress(
				companyId, emailAddress);
		}
		else if (Validator.isNotNull(screenName)) {
			user = _userLocalService.fetchUserByScreenName(
				companyId, screenName);
		}
		else {
			if (_log.isDebugEnabled()) {
				_log.debug("Unable to get preferred LDAP server");
			}

			return -1;
		}

		if (user == null) {
			if (_log.isDebugEnabled()) {
				_log.debug("Unable to get user " + userId);
			}

			return -1;
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"Using LDAP server ", user.getLdapServerId(),
					" to authenticate user ", userId));
		}

		return user.getLdapServerId();
	}

	private String _removeEncryptionAlgorithm(String ldapPassword) {
		if (_log.isDebugEnabled()) {
			_log.debug("Removing encryption algorithm");
		}

		int x = ldapPassword.indexOf(StringPool.OPEN_CURLY_BRACE);

		if (x == -1) {
			return ldapPassword;
		}

		int y = ldapPassword.indexOf(StringPool.CLOSE_CURLY_BRACE, x);

		if (y == -1) {
			return ldapPassword;
		}

		return ldapPassword.substring(y + 1);
	}

	private void _setFailedLDAPAuthResult(
		Map<String, Object> env, LDAPAuthResult ldapAuthResult) {

		Map<String, LDAPAuthResult> failedLDAPAuthResults =
			_failedLDAPAuthResults.get();

		String cacheKey = _getKey(env);

		if (failedLDAPAuthResults.containsKey(cacheKey)) {
			return;
		}

		failedLDAPAuthResults.put(cacheKey, ldapAuthResult);
	}

	private static final Log _log = LogFactoryUtil.getLog(LDAPAuth.class);

	private boolean _authPipelineEnableLiferayCheck;
	private final ThreadLocal<Map<String, LDAPAuthResult>>
		_failedLDAPAuthResults = new CentralizedThreadLocal<>(
			LDAPAuth.class + "._failedLDAPAuthResultCache", HashMap::new);

	@Reference(
		target = "(factoryPid=com.liferay.portal.security.ldap.authenticator.configuration.LDAPAuthConfiguration)"
	)
	private ConfigurationProvider<LDAPAuthConfiguration>
		_ldapAuthConfigurationProvider;

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	private volatile LDAPFilterValidator _ldapFilterValidator;

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
	private volatile LDAPUserImporter _ldapUserImporter;

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	private volatile SafePortalLDAP _portalLDAP;

	@Reference(
		target = "(factoryPid=com.liferay.portal.security.ldap.configuration.SystemLDAPConfiguration)"
	)
	private ConfigurationProvider<SystemLDAPConfiguration>
		_systemLDAPConfigurationProvider;

	@Reference
	private UserLocalService _userLocalService;

}