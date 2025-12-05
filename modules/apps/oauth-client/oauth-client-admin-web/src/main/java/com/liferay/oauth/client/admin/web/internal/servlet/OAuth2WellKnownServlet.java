/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.admin.web.internal.servlet;

import com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata;
import com.liferay.oauth.client.persistence.service.OAuthClientASLocalMetadataService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.Servlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.net.URLDecoder;

import java.nio.charset.StandardCharsets;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alvaro Saugar
 */
@Component(
	property = {
		"osgi.http.whiteboard.context.path=/",
		"osgi.http.whiteboard.servlet.pattern=/.well-known/oauth-authorization-server",
		"osgi.http.whiteboard.servlet.pattern=/.well-known/oauth-authorization-server/*",
		"servlet.init.httpMethods=GET"
	},
	service = Servlet.class
)
public class OAuth2WellKnownServlet extends HttpServlet {

	@Override
	protected void doGet(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		httpServletResponse.setContentType("application/json");
		httpServletResponse.setCharacterEncoding("UTF-8");

		String issuer = _extractIssuerFromRequest(httpServletRequest);

		long companyId = GetterUtil.getLong(
			httpServletRequest.getAttribute(WebKeys.COMPANY_ID));

		if (issuer == null) {

			// Default value

		}
		else {
			try {
				OAuthClientASLocalMetadata meta =
					_oAuthClientASLocalMetadataService.
						getIssuerAuthClientASLocalMetadata(
							companyId, "https://" + issuer);

				if (meta.isLocalWellKnownEnabled()) {
					httpServletResponse.setContentType(
						ContentTypes.APPLICATION_JSON);
					httpServletResponse.setStatus(HttpServletResponse.SC_OK);

					ServletResponseUtil.write(
						httpServletResponse, meta.getMetadataJSONOAS());
				}
				else {
					httpServletResponse.setStatus(
						HttpServletResponse.SC_NOT_FOUND);
				}
			}
			catch (PortalException portalException) {
				_log.error(portalException);

				httpServletResponse.setStatus(
					HttpServletResponse.SC_BAD_REQUEST);
			}
		}
	}

	private String _extractIssuerFromRequest(
		HttpServletRequest httpServletRequest) {

		String requestURI = httpServletRequest.getRequestURI();

		String contextPath = httpServletRequest.getContextPath();

		String basePath =
			contextPath + "/.well-known/oauth-authorization-server";

		if (requestURI.length() == basePath.length()) {
			return null;
		}

		String extra = requestURI.substring(basePath.length());

		if (extra.startsWith("/")) {
			extra = extra.substring(1);
		}

		if (extra.isEmpty()) {
			return null;
		}

		return URLDecoder.decode(extra, StandardCharsets.UTF_8);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		OAuth2WellKnownServlet.class);

	@Reference
	private OAuthClientASLocalMetadataService
		_oAuthClientASLocalMetadataService;

}