/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.admin.web.internal.servlet;

import com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata;
import com.liferay.oauth.client.persistence.service.OAuthClientASLocalMetadataLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.PortalRunMode;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

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
public class OAuth2WellKnownAuthorizationServerServlet extends HttpServlet {

	@Override
	protected void doGet(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		httpServletResponse.setCharacterEncoding(StandardCharsets.UTF_8.name());
		httpServletResponse.setContentType(ContentTypes.APPLICATION_JSON);
		httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);

		long companyId = CompanyThreadLocal.getCompanyId();

		if (!FeatureFlagManagerUtil.isEnabled(companyId, "LPD-63415")) {
			return;
		}

		String issuer = _getIssuer(httpServletRequest);

		if (issuer == null) {
			if (_log.isDebugEnabled()) {
				_log.debug("Issuer is null");
			}

			OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
				_oAuthClientASLocalMetadataLocalService.
					fetchOAuthClientASLocalMetadata(companyId, true, null);

			if (oAuthClientASLocalMetadata != null) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						StringBundler.concat(
							"OAuth 2 client authorization server local ",
							"metadata already exists for company ", companyId));
				}

				httpServletResponse.setStatus(HttpServletResponse.SC_OK);

				ServletResponseUtil.write(
					httpServletResponse,
					oAuthClientASLocalMetadata.getOAuthASMetadataJSON());
			}
		}
		else {
			if (_log.isDebugEnabled()) {
				_log.debug("Issuer is not null");
			}

			String scheme = Http.HTTPS_WITH_SLASH;

			if (PortalRunMode.isTestMode() && !httpServletRequest.isSecure()) {
				scheme = Http.HTTP_WITH_SLASH;
			}

			OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
				_oAuthClientASLocalMetadataLocalService.
					fetchOAuthClientASLocalMetadata(companyId, scheme + issuer);

			if ((oAuthClientASLocalMetadata != null) &&
				oAuthClientASLocalMetadata.isLocalWellKnownEnabled()) {

				if (_log.isDebugEnabled()) {
					_log.debug("OAuth 2 authorization server is enabled");
				}

				httpServletResponse.setStatus(HttpServletResponse.SC_OK);

				ServletResponseUtil.write(
					httpServletResponse,
					oAuthClientASLocalMetadata.getOAuthASMetadataJSON());
			}
		}
	}

	private String _getIssuer(HttpServletRequest httpServletRequest) {
		String requestURI = StringUtil.trimTrailing(
			httpServletRequest.getRequestURI(), StringPool.SLASH.charAt(0));

		String path =
			httpServletRequest.getContextPath() +
				"/.well-known/oauth-authorization-server";

		if (requestURI.length() <= path.length()) {
			return null;
		}

		String issuer = requestURI.substring(
			requestURI.lastIndexOf(StringPool.SLASH) + 1);

		if (Validator.isNull(issuer)) {
			return null;
		}

		return URLDecoder.decode(issuer, StandardCharsets.UTF_8);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		OAuth2WellKnownAuthorizationServerServlet.class);

	@Reference
	private OAuthClientASLocalMetadataLocalService
		_oAuthClientASLocalMetadataLocalService;

}