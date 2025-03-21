/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.template;

import com.liferay.petra.io.unsync.UnsyncStringWriter;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.access.control.AccessControlUtil;
import com.liferay.portal.kernel.security.auth.AccessControlContext;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.servlet.PipingServletResponse;
import com.liferay.portal.kernel.servlet.ServletContextPool;
import com.liferay.portal.kernel.template.TemplateContextContributor;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.vulcan.internal.template.servlet.RESTClientHttpRequestDelegate;
import com.liferay.portal.vulcan.internal.template.servlet.RESTClientHttpResponse;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = "type=" + TemplateContextContributor.TYPE_GLOBAL,
	service = TemplateContextContributor.class
)
public class RESTClientTemplateContextContributor
	implements TemplateContextContributor {

	@Override
	public void prepare(
		Map<String, Object> contextObjects,
		HttpServletRequest httpServletRequest) {

		contextObjects.put(
			"restClient", new RESTClient(contextObjects, httpServletRequest));
	}

	public class RESTClient {

		public RESTClient(
			Map<String, Object> contextObjects,
			HttpServletRequest httpServletRequest) {

			_contextObjects = contextObjects;
			_httpServletRequest = httpServletRequest;
		}

		public Object get(String path) throws Exception {
			try {
				return _get(path);
			}
			catch (Throwable throwable) {
				_log.error(throwable, throwable);

				throw throwable;
			}
		}

		private Object _get(String path) throws Exception {
			UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

			AccessControlContext accessControlContext =
				AccessControlUtil.getAccessControlContext();

			HttpServletResponse httpServletResponse = new PipingServletResponse(
				new RESTClientHttpResponse(), unsyncStringWriter);

			ServletContext servletContext = _getServletContext();

			RequestDispatcher requestDispatcher =
				servletContext.getRequestDispatcher(Portal.PATH_MODULE + path);

			PermissionChecker permissionChecker =
				PermissionThreadLocal.getPermissionChecker();

			try {
				AccessControlUtil.setAccessControlContext(null);

				requestDispatcher.forward(
					ProxyUtil.newDelegateProxyInstance(
						HttpServletRequest.class.getClassLoader(),
						HttpServletRequest.class,
						new RESTClientHttpRequestDelegate(
							_contextObjects, _httpServletRequest, path),
						_httpServletRequest),
					httpServletResponse);
			}
			finally {
				AccessControlUtil.setAccessControlContext(accessControlContext);
				PermissionThreadLocal.setPermissionChecker(permissionChecker);
			}

			String responseString = unsyncStringWriter.toString();

			if (Objects.equals(
					httpServletResponse.getContentType(),
					ContentTypes.APPLICATION_JSON)) {

				return _jsonFactory.looseDeserialize(responseString);
			}

			return responseString;
		}

		private final Map<String, Object> _contextObjects;
		private final HttpServletRequest _httpServletRequest;

	}

	private ServletContext _getServletContext() {
		if (_servletContext == null) {
			_servletContext = ServletContextPool.get(StringPool.BLANK);
		}

		return _servletContext;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		RESTClientTemplateContextContributor.class);

	@Reference
	private JSONFactory _jsonFactory;

	private ServletContext _servletContext;

}