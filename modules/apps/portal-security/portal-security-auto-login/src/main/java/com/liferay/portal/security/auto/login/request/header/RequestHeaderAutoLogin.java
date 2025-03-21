/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.auto.login.request.header;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.security.access.control.AccessControlUtil;
import com.liferay.portal.kernel.security.auto.login.AutoLogin;
import com.liferay.portal.kernel.security.auto.login.BaseAutoLogin;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.settings.CompanyServiceSettingsLocator;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.auto.login.internal.request.header.configuration.RequestHeaderAutoLoginConfiguration;
import com.liferay.portal.security.auto.login.internal.request.header.constants.RequestHeaderAutoLoginConstants;
import com.liferay.portal.security.ldap.exportimport.LDAPUserImporter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.HashSet;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Wesley Gong
 */
@Component(
	configurationPid = "com.liferay.portal.security.auto.login.internal.request.header.configuration.RequestHeaderAutoLoginConfiguration",
	service = AutoLogin.class
)
public class RequestHeaderAutoLogin extends BaseAutoLogin {

	@Override
	protected String[] doLogin(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		long companyId = _portal.getCompanyId(httpServletRequest);

		if (!isEnabled(companyId)) {
			return null;
		}

		String remoteAddr = httpServletRequest.getRemoteAddr();

		if (isAccessAllowed(companyId, httpServletRequest)) {
			if (_log.isDebugEnabled()) {
				_log.debug("Access allowed for " + remoteAddr);
			}
		}
		else {
			if (_log.isWarnEnabled()) {
				_log.warn("Access denied for " + remoteAddr);
			}

			return null;
		}

		String screenName = httpServletRequest.getHeader(
			HttpHeaders.LIFERAY_SCREEN_NAME);

		if (Validator.isNull(screenName)) {
			return null;
		}

		User user = null;

		if (isLDAPImportEnabled(companyId)) {
			try {
				user = _ldapUserImporter.importUser(
					companyId, StringPool.BLANK, screenName);
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}
		}

		if (user == null) {
			user = _userLocalService.getUserByScreenName(companyId, screenName);
		}

		String[] credentials = new String[3];

		credentials[0] = String.valueOf(user.getUserId());
		credentials[1] = user.getPassword();
		credentials[2] = Boolean.TRUE.toString();

		return credentials;
	}

	protected boolean isAccessAllowed(
		long companyId, HttpServletRequest httpServletRequest) {

		RequestHeaderAutoLoginConfiguration
			requestHeaderAutoLoginConfiguration =
				_getRequestHeaderAutoLoginConfiguration(companyId);

		if (requestHeaderAutoLoginConfiguration == null) {
			return false;
		}

		String[] hostsAllowedArray = StringUtil.split(
			requestHeaderAutoLoginConfiguration.authHostsAllowed());

		Set<String> hostsAllowed = new HashSet<>();

		for (String hostAllowed : hostsAllowedArray) {
			hostsAllowed.add(hostAllowed);
		}

		return AccessControlUtil.isAccessAllowed(
			httpServletRequest, hostsAllowed);
	}

	protected boolean isEnabled(long companyId) {
		RequestHeaderAutoLoginConfiguration
			requestHeaderAutoLoginConfiguration =
				_getRequestHeaderAutoLoginConfiguration(companyId);

		if (requestHeaderAutoLoginConfiguration == null) {
			return false;
		}

		return requestHeaderAutoLoginConfiguration.enabled();
	}

	protected boolean isLDAPImportEnabled(long companyId) {
		RequestHeaderAutoLoginConfiguration
			requestHeaderAutoLoginConfiguration =
				_getRequestHeaderAutoLoginConfiguration(companyId);

		if (requestHeaderAutoLoginConfiguration == null) {
			return false;
		}

		return requestHeaderAutoLoginConfiguration.importFromLDAP();
	}

	private RequestHeaderAutoLoginConfiguration
		_getRequestHeaderAutoLoginConfiguration(long companyId) {

		try {
			return _configurationProvider.getConfiguration(
				RequestHeaderAutoLoginConfiguration.class,
				new CompanyServiceSettingsLocator(
					companyId, RequestHeaderAutoLoginConstants.SERVICE_NAME));
		}
		catch (ConfigurationException configurationException) {
			_log.error(
				"Unable to get request header auto login configuration",
				configurationException);
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		RequestHeaderAutoLogin.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private LDAPUserImporter _ldapUserImporter;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}