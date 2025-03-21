/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch.monitoring.web.internal.servlet;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.GroupThreadLocal;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.search.elasticsearch.monitoring.web.internal.configuration.MonitoringConfiguration;
import com.liferay.portal.search.elasticsearch.monitoring.web.internal.constants.MonitoringPortletKeys;
import com.liferay.portal.search.elasticsearch.monitoring.web.internal.constants.MonitoringProxyServletWebKeys;
import com.liferay.portal.search.elasticsearch.monitoring.web.internal.constants.MonitoringWebConstants;
import com.liferay.portal.search.elasticsearch.monitoring.web.internal.servlet.display.context.ErrorDisplayContext;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.net.ConnectException;

import java.util.Map;

import org.apache.http.HttpRequest;

import org.mitre.dsmiley.httpproxy.ProxyServlet;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 * @author Miguel Angelo Caldas Gallindo
 * @author Artur Aquino
 * @author André de Oliveira
 */
@Component(
	configurationPid = "com.liferay.portal.search.elasticsearch.monitoring.web.internal.configuration.MonitoringConfiguration",
	enabled = false,
	property = {
		"osgi.http.whiteboard.context.select=portal-search-elasticsearch-monitoring",
		"osgi.http.whiteboard.servlet.name=com.liferay.portal.search.elasticsearch.monitoring.web.internal.servlet.MonitoringProxyServlet",
		"osgi.http.whiteboard.servlet.pattern=/" + MonitoringWebConstants.SERVLET_PATH + "/*"
	},
	service = Servlet.class
)
public class MonitoringProxyServlet extends ProxyServlet {

	@Override
	public String getServletInfo() {
		return "Liferay Portal Search Elasticsearch Monitoring Proxy Servlet";
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_replaceConfiguration(properties);
	}

	@Override
	protected void copyRequestHeaders(
		HttpServletRequest httpServletRequest, HttpRequest proxyHttpRequest) {

		super.copyRequestHeaders(httpServletRequest, proxyHttpRequest);

		proxyHttpRequest.addHeader(
			HttpHeaders.AUTHORIZATION, _getShieldAuthorization());

		proxyHttpRequest.removeHeaders(HttpHeaders.ACCEPT_ENCODING);
	}

	@Override
	protected String getConfigParam(String key) {
		if (key.equals(ProxyServlet.P_TARGET_URI)) {
			return GetterUtil.getString(_monitoringConfiguration.kibanaURL());
		}

		if (key.equals(ProxyServlet.P_LOG)) {
			return String.valueOf(
				_monitoringConfiguration.proxyServletLogEnable());
		}

		return super.getConfigParam(key);
	}

	@Modified
	protected void modified(Map<String, Object> properties) throws Exception {
		_replaceConfiguration(properties);

		try {
			init();
		}
		catch (NullPointerException nullPointerException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to initialize monitoring proxy servlet",
					nullPointerException);
			}
		}
	}

	@Override
	protected void service(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		try {
			_checkPermission(httpServletRequest);

			super.service(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			_sendError(exception, httpServletRequest, httpServletResponse);
		}
	}

	@Reference
	protected PermissionCheckerFactory permissionCheckerFactory;

	@Reference
	protected Portal portal;

	private ErrorDisplayContext _buildErrorDisplayContext(Exception exception) {
		ErrorDisplayContext errorDisplayContext = new ErrorDisplayContext();

		errorDisplayContext.setException(exception);

		boolean error = true;

		if (exception instanceof ConnectException) {
			errorDisplayContext.setConnectExceptionAddress(
				_monitoringConfiguration.kibanaURL());
		}
		else if (exception instanceof PrincipalException.MustBeAuthenticated) {
			error = false;
		}
		else if (exception instanceof PrincipalException.MustHavePermission) {
			error = false;
		}

		if (error) {
			_log.error(exception);
		}
		else {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return errorDisplayContext;
	}

	private void _checkPermission(HttpServletRequest httpServletRequest)
		throws Exception {

		User user = portal.getUser(httpServletRequest);

		if (user == null) {
			throw new PrincipalException.MustBeAuthenticated(StringPool.BLANK);
		}

		PermissionChecker permissionChecker = permissionCheckerFactory.create(
			user);

		boolean hasPermission = permissionChecker.hasPermission(
			GroupThreadLocal.getGroupId(), MonitoringPortletKeys.MONITORING,
			MonitoringPortletKeys.MONITORING, ActionKeys.VIEW);

		if (!hasPermission) {
			throw new PrincipalException.MustHavePermission(
				permissionChecker, MonitoringPortletKeys.MONITORING, 0,
				ActionKeys.VIEW);
		}
	}

	private String _getShieldAuthorization() {
		String userName = GetterUtil.getString(
			_monitoringConfiguration.kibanaUserName());
		String password = GetterUtil.getString(
			_monitoringConfiguration.kibanaPassword());

		String authorization = userName + ":" + password;

		return "Basic " + Base64.encode(authorization.getBytes());
	}

	private void _replaceConfiguration(Map<String, Object> properties) {
		_monitoringConfiguration = ConfigurableUtil.createConfigurable(
			MonitoringConfiguration.class, properties);
	}

	private void _sendError(
			Exception exception, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		httpServletRequest.setAttribute(
			MonitoringProxyServletWebKeys.ERROR_DISPLAY_CONTEXT,
			_buildErrorDisplayContext(exception));

		RequestDispatcher requestDispatcher =
			httpServletRequest.getRequestDispatcher(
				httpServletRequest.getContextPath() + "/servlet/error.jsp");

		requestDispatcher.include(httpServletRequest, httpServletResponse);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MonitoringProxyServlet.class);

	private volatile MonitoringConfiguration _monitoringConfiguration;

}