/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.token.internal.auto.login;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auto.login.AutoLogin;
import com.liferay.portal.kernel.security.auto.login.BaseAutoLogin;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.settings.CompanyServiceSettingsLocator;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.ldap.exportimport.LDAPUserImporter;
import com.liferay.portal.security.sso.token.configuration.TokenConfiguration;
import com.liferay.portal.security.sso.token.constants.TokenConstants;
import com.liferay.portal.security.sso.token.security.auth.TokenRetriever;
import com.liferay.portal.util.PropsValues;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * Participates in every unauthenticated HTTP request to Liferay Portal.
 *
 * <p>
 * If this class finds an authentication token in the HTTP request and
 * successfully identifies a Liferay Portal user using it, then this user is
 * logged in without any further challenge.
 * </p>
 *
 * @author Michael C. Han
 */
@Component(
	configurationPid = "com.liferay.portal.security.sso.token.configuration.TokenConfiguration",
	service = AutoLogin.class
)
public class TokenAutoLogin extends BaseAutoLogin {

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, TokenRetriever.class, "token.location");
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	@Override
	protected String[] doLogin(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		long companyId = _portal.getCompanyId(httpServletRequest);

		TokenConfiguration tokenConfiguration =
			_configurationProvider.getConfiguration(
				TokenConfiguration.class,
				new CompanyServiceSettingsLocator(
					companyId, TokenConstants.SERVICE_NAME));

		if (!tokenConfiguration.enabled()) {
			return null;
		}

		String userTokenName = tokenConfiguration.userTokenName();

		String tokenLocation = tokenConfiguration.tokenLocation();

		TokenRetriever tokenRetriever = _serviceTrackerMap.getService(
			tokenLocation);

		if (tokenRetriever == null) {
			if (_log.isWarnEnabled()) {
				_log.warn("No token retriever found for " + tokenLocation);
			}

			return null;
		}

		String login = tokenRetriever.getLoginToken(
			httpServletRequest, userTokenName);

		if (Validator.isNull(login)) {
			if (_log.isInfoEnabled()) {
				_log.info("No login found for " + tokenLocation);
			}

			return null;
		}

		User user = _getUser(companyId, login, tokenConfiguration);

		addRedirect(httpServletRequest);

		String[] credentials = new String[3];

		credentials[0] = String.valueOf(user.getUserId());
		credentials[1] = user.getPassword();
		credentials[2] = Boolean.TRUE.toString();

		return credentials;
	}

	private User _getUser(
			long companyId, String login, TokenConfiguration tokenConfiguration)
		throws Exception {

		User user = null;

		String authType = PrefsPropsUtil.getString(
			companyId, PropsKeys.COMPANY_SECURITY_AUTH_TYPE,
			PropsValues.COMPANY_SECURITY_AUTH_TYPE);

		if (tokenConfiguration.importFromLDAP()) {
			try {
				if (authType.equals(CompanyConstants.AUTH_TYPE_SN)) {
					user = _ldapUserImporter.importUser(
						companyId, StringPool.BLANK, login);
				}
				else if (authType.equals(CompanyConstants.AUTH_TYPE_EA)) {
					user = _ldapUserImporter.importUser(
						companyId, login, StringPool.BLANK);
				}
				else {
					if (_log.isWarnEnabled()) {
						_log.warn(
							StringBundler.concat(
								"The property \"",
								PropsKeys.COMPANY_SECURITY_AUTH_TYPE,
								"\" must be set to either \"",
								CompanyConstants.AUTH_TYPE_EA, "\" or \"",
								CompanyConstants.AUTH_TYPE_SN, "\""));
					}
				}
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn("Unable to import from LDAP", exception);
				}
			}
		}

		if (user != null) {
			return user;
		}

		if (authType.equals(CompanyConstants.AUTH_TYPE_SN)) {
			user = _userLocalService.getUserByScreenName(companyId, login);
		}
		else if (authType.equals(CompanyConstants.AUTH_TYPE_EA)) {
			user = _userLocalService.getUserByEmailAddress(companyId, login);
		}
		else {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Incompatible setting for: ",
						PropsKeys.COMPANY_SECURITY_AUTH_TYPE,
						". Please configure to either: ",
						CompanyConstants.AUTH_TYPE_EA, " or ",
						CompanyConstants.AUTH_TYPE_SN));
			}
		}

		return user;
	}

	private static final Log _log = LogFactoryUtil.getLog(TokenAutoLogin.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private LDAPUserImporter _ldapUserImporter;

	@Reference
	private Portal _portal;

	private ServiceTrackerMap<String, TokenRetriever> _serviceTrackerMap;

	@Reference
	private UserLocalService _userLocalService;

}