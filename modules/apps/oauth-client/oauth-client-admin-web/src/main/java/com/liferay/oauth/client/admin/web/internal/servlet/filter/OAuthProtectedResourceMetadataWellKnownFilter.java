/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.admin.web.internal.servlet.filter;

import com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata;
import com.liferay.oauth.client.persistence.service.OAuthClientPRLocalMetadataLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.servlet.BaseFilter;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.Portal;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Serves OAuth 2.0 Protected Resource Metadata (RFC 9728) at the spec-mandated
 * host-root location. The path suffix after
 * <code>/.well-known/oauth-protected-resource</code> is appended to the request
 * authority to reconstruct the protected resource URL, which is then looked up
 * in the persisted metadata. With no suffix, the first enabled record for the
 * company is returned.
 *
 * @author Jorge García Jiménez
 */
@Component(
	property = {
		"dispatcher=REQUEST", "servlet-context-name=",
		"servlet-filter-name=OAuth Protected Resource Metadata Well-Known Filter",
		"url-pattern=/.well-known/oauth-protected-resource",
		"url-pattern=/.well-known/oauth-protected-resource/*"
	},
	service = Filter.class
)
public class OAuthProtectedResourceMetadataWellKnownFilter extends BaseFilter {

	@Override
	protected Log getLog() {
		return _log;
	}

	@Override
	protected void processFilter(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FilterChain filterChain)
		throws Exception {

		long companyId = CompanyThreadLocal.getCompanyId();

		if (!FeatureFlagManagerUtil.isEnabled(companyId, "LPD-63415")) {
			httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);

			return;
		}

		if (!Objects.equals(httpServletRequest.getMethod(), "GET")) {
			httpServletResponse.setHeader("Allow", "GET");
			httpServletResponse.sendError(
				HttpServletResponse.SC_METHOD_NOT_ALLOWED);

			return;
		}

		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata = _resolve(
			companyId, httpServletRequest);

		if ((oAuthClientPRLocalMetadata == null) ||
			!oAuthClientPRLocalMetadata.isLocalWellKnownEnabled()) {

			httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);

			return;
		}

		httpServletResponse.setCharacterEncoding(StringPool.UTF8);
		httpServletResponse.setContentType(ContentTypes.APPLICATION_JSON);
		httpServletResponse.setHeader(
			HttpHeaders.CACHE_CONTROL, "public, max-age=300");
		httpServletResponse.setStatus(HttpServletResponse.SC_OK);

		ServletResponseUtil.write(
			httpServletResponse, oAuthClientPRLocalMetadata.getMetadataJSON());

		httpServletResponse.flushBuffer();
	}

	private OAuthClientPRLocalMetadata _resolve(
		long companyId, HttpServletRequest httpServletRequest) {

		String requestURI = httpServletRequest.getRequestURI();

		int index = requestURI.indexOf(_WELL_KNOWN_PATH);

		if (index < 0) {
			return _oAuthClientPRLocalMetadataLocalService.
				fetchOAuthClientPRLocalMetadata(companyId, true, null);
		}

		String resourcePath = requestURI.substring(
			index + _WELL_KNOWN_PATH.length());

		if (resourcePath.isEmpty()) {
			return _oAuthClientPRLocalMetadataLocalService.
				fetchOAuthClientPRLocalMetadata(companyId, true, null);
		}

		String resource =
			_portal.getPortalURL(httpServletRequest) + resourcePath;

		return _oAuthClientPRLocalMetadataLocalService.
			fetchOAuthClientPRLocalMetadata(companyId, resource);
	}

	private static final String _WELL_KNOWN_PATH =
		"/.well-known/oauth-protected-resource";

	private static final Log _log = LogFactoryUtil.getLog(
		OAuthProtectedResourceMetadataWellKnownFilter.class);

	@Reference
	private OAuthClientPRLocalMetadataLocalService
		_oAuthClientPRLocalMetadataLocalService;

	@Reference
	private Portal _portal;

}