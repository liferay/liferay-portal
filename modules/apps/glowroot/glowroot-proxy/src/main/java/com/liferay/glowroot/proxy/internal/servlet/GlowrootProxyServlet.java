/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.glowroot.proxy.internal.servlet;

import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.Servlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;

import java.io.Serializable;

import java.nio.charset.StandardCharsets;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.mitre.dsmiley.httpproxy.ProxyServlet;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Fabian Bouché
 */
@Component(
	enabled = false,
	property = {
		"osgi.http.whiteboard.context.path=/",
		"osgi.http.whiteboard.servlet.pattern=/glowroot/*",
		"servlet.init.targetUri=" + GlowrootProxyServlet.URL_GLOWROOT
	},
	service = Servlet.class
)
public class GlowrootProxyServlet extends ProxyServlet implements Serializable {

	public static final String URL_GLOWROOT =
		"http://localhost:4000/o/glowroot";

	@Override
	protected String getConfigParam(String key) {
		String value = super.getConfigParam(key);

		if (P_TARGET_URI.equals(key) && URL_GLOWROOT.equals(value)) {
			String contextPath = _portal.getPathContext();

			if (Validator.isNotNull(contextPath)) {
				value = "http://localhost:4000" + contextPath + "/o/glowroot";

				_updateGlowrootContextPath();
			}
		}

		return value;
	}

	@Override
	protected void service(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		try {
			PermissionChecker permissionChecker = _getPermissionChecker(
				httpServletRequest);

			if (!permissionChecker.isOmniadmin()) {
				throw new PrincipalException.MustBeCompanyAdmin(
					permissionChecker.getUserId());
			}

			super.service(
				new GzipEncodingRequestWrapper(httpServletRequest),
				httpServletResponse);
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	private PermissionChecker _getPermissionChecker(
			HttpServletRequest httpServletRequest)
		throws Exception {

		User user = _portal.getUser(httpServletRequest);

		if (user == null) {
			throw new PrincipalException.MustBeAuthenticated(0);
		}

		return _permissionCheckerFactory.create(user);
	}

	private JSONObject _loadBackendAdminWebJSON() {
		try {
			return _jsonFactory.createJSONObject(
				_http.URLtoString(_URL_BACKEND_ADMIN_WEB));
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to load " + _URL_BACKEND_ADMIN_WEB, exception);
			}

			return null;
		}
	}

	private void _updateGlowrootContextPath() {
		JSONObject jsonObject = _loadBackendAdminWebJSON();

		if (jsonObject == null) {
			return;
		}

		try {
			Http.Options options = new Http.Options();

			options.addHeader(
				HttpHeaders.CONTENT_TYPE, ContentTypes.APPLICATION_JSON);

			JSONObject configJSONObject = jsonObject.getJSONObject("config");

			String contextPath = configJSONObject.getString("contextPath");

			if (Objects.equals(contextPath, "/o/glowroot")) {
				configJSONObject.put(
					"contextPath", _portal.getPathContext() + "/o/glowroot");
			}

			options.setBody(
				configJSONObject.toString(), ContentTypes.APPLICATION_JSON,
				StandardCharsets.UTF_8.name());

			options.setLocation(_URL_BACKEND_ADMIN_WEB);
			options.setMethod(Http.Method.POST);

			_http.URLtoString(options);
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	private static final String _URL_BACKEND_ADMIN_WEB =
		URL_GLOWROOT + "/backend/admin/web";

	private static final Log _log = LogFactoryUtil.getLog(
		GlowrootProxyServlet.class);

	private static final long serialVersionUID = 1L;

	@Reference
	private Http _http;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private PermissionCheckerFactory _permissionCheckerFactory;

	@Reference
	private Portal _portal;

	private static class GzipEncodingRequestWrapper
		extends HttpServletRequestWrapper {

		@Override
		public String getHeader(String name) {
			if (StringUtil.equalsIgnoreCase("Accept-Encoding", name)) {
				return null;
			}

			return super.getHeader(name);
		}

		@Override
		public Enumeration<String> getHeaderNames() {
			if (_headerNameSet == null) {
				_headerNameSet = new HashSet<>();

				Enumeration<String> enumeration = super.getHeaderNames();

				while (enumeration.hasMoreElements()) {
					String headerName = enumeration.nextElement();

					if (!StringUtil.equalsIgnoreCase(
							"Accept-Encoding", headerName)) {

						_headerNameSet.add(headerName);
					}
				}
			}

			return Collections.enumeration(_headerNameSet);
		}

		@Override
		public Enumeration<String> getHeaders(String name) {
			if (StringUtil.equalsIgnoreCase("Accept-Encoding", name)) {
				return Collections.emptyEnumeration();
			}

			return super.getHeaders(name);
		}

		private GzipEncodingRequestWrapper(
			HttpServletRequest httpServletRequest) {

			super(httpServletRequest);
		}

		private Set<String> _headerNameSet;

	}

}