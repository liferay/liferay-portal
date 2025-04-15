/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.servlet.filters.authverifier;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.access.control.AccessControlUtil;
import com.liferay.portal.kernel.security.auth.AccessControlContext;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifierConfiguration;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifierResult;
import com.liferay.portal.kernel.servlet.ServletContextPool;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.auth.AuthVerifierPipeline;
import com.liferay.portal.servlet.AuthVerifierServletRequest;
import com.liferay.portal.servlet.filters.BasePortalFilter;
import com.liferay.portal.util.PropsUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * <p>
 * See https://issues.liferay.com/browse/LPS-27888.
 * </p>
 *
 * @author Tomas Polesovsky
 * @author Raymond Augé
 */
public class AuthVerifierFilter extends BasePortalFilter {

	@Override
	public void init(FilterConfig filterConfig) {
		super.init(filterConfig);

		Enumeration<String> enumeration = filterConfig.getInitParameterNames();

		while (enumeration.hasMoreElements()) {
			String name = enumeration.nextElement();

			String value = filterConfig.getInitParameter(name);

			_initParametersMap.put(name, value);
		}

		String portalPropertyPrefix = GetterUtil.getString(
			_initParametersMap.get("portal_property_prefix"));

		if (Validator.isNotNull(portalPropertyPrefix)) {
			Properties properties = PropsUtil.getProperties(
				portalPropertyPrefix, true);

			for (Map.Entry<Object, Object> entry : properties.entrySet()) {
				_initParametersMap.put(
					(String)entry.getKey(), entry.getValue());
			}
		}

		if (_initParametersMap.containsKey("guest.allowed")) {
			_guestAllowed = GetterUtil.getBoolean(
				_initParametersMap.get("guest.allowed"), true);

			_initParametersMap.remove("guest.allowed");
		}

		if (_initParametersMap.containsKey("hosts.allowed")) {
			String hostsAllowedString = (String)_initParametersMap.get(
				"hosts.allowed");

			String[] hostsAllowed = StringUtil.split(hostsAllowedString);

			for (String hostAllowed : hostsAllowed) {
				_hostsAllowed.add(hostAllowed);
			}

			_initParametersMap.remove("hosts.allowed");
		}

		if (_initParametersMap.containsKey("https.required")) {
			_httpsRequired = GetterUtil.getBoolean(
				_initParametersMap.get("https.required"));

			_initParametersMap.remove("https.required");
		}

		if (_initParametersMap.containsKey("use_permission_checker")) {
			_initParametersMap.remove("use_permission_checker");

			if (_log.isWarnEnabled()) {
				_log.warn("use_permission_checker is deprecated");
			}
		}

		ServletContext servletContext = filterConfig.getServletContext();

		if (!servletContext.equals(
				ServletContextPool.get(PortalUtil.getServletContextName()))) {

			_initParametersMap.put(
				AuthVerifierPipeline.class.getName(),
				new AuthVerifierPipeline(
					_buildAuthVerifierConfigurations(_initParametersMap),
					servletContext.getContextPath()));
		}
	}

	@Override
	protected void processFilter(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FilterChain filterChain)
		throws Exception {

		if (!_isAccessAllowed(httpServletRequest, httpServletResponse) ||
			_isApplySSL(httpServletRequest, httpServletResponse)) {

			return;
		}

		if (_isCORSPreflightRequest(httpServletRequest)) {
			Class<?> clazz = getClass();

			processFilter(
				clazz.getName(), httpServletRequest, httpServletResponse,
				filterChain);

			return;
		}

		AccessControlUtil.initAccessControlContext(
			httpServletRequest, httpServletResponse, _initParametersMap);

		AuthVerifierResult.State state = AccessControlUtil.verifyRequest();

		AccessControlContext accessControlContext =
			AccessControlUtil.getAccessControlContext();

		AuthVerifierResult authVerifierResult =
			accessControlContext.getAuthVerifierResult();

		if (_log.isDebugEnabled()) {
			_log.debug("Auth verifier result " + authVerifierResult);
		}

		if (state == AuthVerifierResult.State.INVALID_CREDENTIALS) {
			if (_log.isDebugEnabled()) {
				_log.debug("Result state does not allow us to continue");
			}
		}
		else if (state == AuthVerifierResult.State.NOT_APPLICABLE) {
			_log.error("Invalid state " + state);
		}
		else if (!_guestAllowed &&
				 (state == AuthVerifierResult.State.UNSUCCESSFUL)) {

			if (_log.isDebugEnabled()) {
				_log.debug(
					"Guest is not allowed to access " +
						httpServletRequest.getRequestURI());
			}

			httpServletResponse.sendError(
				HttpServletResponse.SC_FORBIDDEN, "Authorization required");
		}
		else if (_guestAllowed || (state == AuthVerifierResult.State.SUCCESS)) {
			long userId = authVerifierResult.getUserId();

			AccessControlUtil.initContextUser(userId);

			String authType = MapUtil.getString(
				accessControlContext.getSettings(),
				AuthVerifierPipeline.AUTH_TYPE);

			AuthVerifierServletRequest authVerifierServletRequest =
				new AuthVerifierServletRequest(
					httpServletRequest, userId, authType);

			accessControlContext.setRequest(authVerifierServletRequest);

			Class<?> clazz = getClass();

			processFilter(
				clazz.getName(), authVerifierServletRequest,
				httpServletResponse, filterChain);
		}
		else {
			_log.error("Unimplemented state " + state);
		}
	}

	private List<AuthVerifierConfiguration> _buildAuthVerifierConfigurations(
		Map<String, Object> initParametersMap) {

		Map<String, Integer> authVerifierConfigurationIndexs = new HashMap<>();

		ArrayList<AuthVerifierConfiguration> authVerifierConfigurations =
			new ArrayList<>();

		for (Map.Entry<String, Object> entry : initParametersMap.entrySet()) {
			String propertyName = entry.getKey();

			if (!propertyName.startsWith(PropsKeys.AUTH_VERIFIER)) {
				continue;
			}

			String authVerifierPropertyName = propertyName.substring(
				PropsKeys.AUTH_VERIFIER.length());

			int index = authVerifierPropertyName.indexOf('.');

			String authVerifierClassName = authVerifierPropertyName.substring(
				0, index);

			Integer authVerifierConfigurationIndex =
				authVerifierConfigurationIndexs.get(authVerifierClassName);

			if (authVerifierConfigurationIndex == null) {
				authVerifierConfigurations.add(new AuthVerifierConfiguration());

				authVerifierConfigurationIndex =
					authVerifierConfigurations.size() - 1;

				authVerifierConfigurationIndexs.put(
					authVerifierClassName, authVerifierConfigurationIndex);
			}

			AuthVerifierConfiguration authVerifierConfiguration =
				authVerifierConfigurations.get(authVerifierConfigurationIndex);

			if (authVerifierConfiguration.getAuthVerifierClassName() == null) {
				authVerifierConfiguration.setAuthVerifierClassName(
					authVerifierClassName);
			}

			if (authVerifierConfiguration.getProperties() == null) {
				authVerifierConfiguration.setProperties(new Properties());
			}

			Properties properties = authVerifierConfiguration.getProperties();

			properties.put(
				authVerifierPropertyName.substring(index + 1),
				entry.getValue());
		}

		return authVerifierConfigurations;
	}

	private boolean _isAccessAllowed(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		String remoteAddr = httpServletRequest.getRemoteAddr();

		if (AccessControlUtil.isAccessAllowed(
				httpServletRequest, _hostsAllowed)) {

			if (_log.isDebugEnabled()) {
				_log.debug("Access allowed for " + remoteAddr);
			}

			return true;
		}

		if (_log.isWarnEnabled()) {
			_log.warn("Access denied for " + remoteAddr);
		}

		httpServletResponse.sendError(
			HttpServletResponse.SC_FORBIDDEN,
			"Access denied for " + remoteAddr);

		return false;
	}

	private boolean _isApplySSL(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		if (!_httpsRequired || PortalUtil.isSecure(httpServletRequest)) {
			return false;
		}

		if (_log.isDebugEnabled()) {
			String completeURL = HttpComponentsUtil.getCompleteURL(
				httpServletRequest);

			_log.debug("Securing " + completeURL);
		}

		StringBundler sb = new StringBundler(5);

		sb.append(PortalUtil.getPortalURL(httpServletRequest, true));
		sb.append(PortalUtil.getPathContext(httpServletRequest));
		sb.append(httpServletRequest.getRequestURI());

		if (Validator.isNotNull(httpServletRequest.getQueryString())) {
			sb.append(StringPool.QUESTION);
			sb.append(httpServletRequest.getQueryString());
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Redirect to " + sb.toString());
		}

		httpServletResponse.sendRedirect(sb.toString());

		return true;
	}

	private boolean _isCORSPreflightRequest(
		HttpServletRequest httpServletRequest) {

		if (StringUtil.equals(httpServletRequest.getMethod(), "OPTIONS") &&
			Validator.isNotNull(httpServletRequest.getHeader("Origin"))) {

			return true;
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AuthVerifierFilter.class.getName());

	private boolean _guestAllowed = true;
	private final Set<String> _hostsAllowed = new HashSet<>();
	private boolean _httpsRequired;
	private final Map<String, Object> _initParametersMap = new HashMap<>();

}