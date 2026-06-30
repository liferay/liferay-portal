/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.http;

import com.liferay.petra.io.unsync.UnsyncStringWriter;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.security.access.control.AccessControlUtil;
import com.liferay.portal.kernel.security.auth.AccessControlContext;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.servlet.PipingServletResponse;
import com.liferay.portal.kernel.servlet.ServletContextPool;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.vulcan.http.VulcanRequestForwarder;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;

/**
 * @author Alejandro Tardín
 */
@Component(service = VulcanRequestForwarder.class)
public class VulcanRequestForwarderImpl implements VulcanRequestForwarder {

	@Override
	public Response forward(
			HttpServletRequest httpServletRequest, Request request)
		throws Exception {

		ServletContext servletContext = _getServletContext();

		RequestDispatcher requestDispatcher =
			servletContext.getRequestDispatcher(
				Portal.PATH_MODULE + request.getPath());

		VulcanRequestForwarderHttpServletResponse
			vulcanRequestForwarderHttpServletResponse =
				new VulcanRequestForwarderHttpServletResponse();

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		AccessControlContext accessControlContext =
			AccessControlUtil.getAccessControlContext();
		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			AccessControlUtil.setAccessControlContext(null);

			requestDispatcher.forward(
				new VulcanRequestForwarderHttpServletRequestWrapper(
					request.getBody(), request.getContentType(),
					request.getHeaders(), httpServletRequest,
					request.getMethod(), request.getPath(), request.getUser()),
				new PipingServletResponse(
					vulcanRequestForwarderHttpServletResponse,
					unsyncStringWriter));
		}
		finally {
			AccessControlUtil.setAccessControlContext(accessControlContext);
			PermissionThreadLocal.setPermissionChecker(permissionChecker);
		}

		return new Response() {

			@Override
			public String getContent() {
				return unsyncStringWriter.toString();
			}

			@Override
			public String getContentType() {
				return vulcanRequestForwarderHttpServletResponse.
					getContentType();
			}

			@Override
			public int getStatusCode() {
				return vulcanRequestForwarderHttpServletResponse.getStatus();
			}

		};
	}

	private ServletContext _getServletContext() {
		ServletContext servletContext = _servletContext;

		if (servletContext != null) {
			return servletContext;
		}

		servletContext = ServletContextPool.get(StringPool.BLANK);

		_servletContext = servletContext;

		return servletContext;
	}

	private volatile ServletContext _servletContext;

}