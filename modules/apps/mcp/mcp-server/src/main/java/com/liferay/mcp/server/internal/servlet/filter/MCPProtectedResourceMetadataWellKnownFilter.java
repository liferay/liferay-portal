/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.internal.servlet.filter;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
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
 * Publishes the OAuth 2.0 Protected Resource Metadata document (RFC 9728) for
 * the MCP server at the spec-mandated host-root location. Implemented as a
 * portal filter so the URL is not prefixed with the portal's <code>/o</code>
 * module context.
 *
 * @author Jorge García Jiménez
 */
@Component(
	property = {
		"dispatcher=REQUEST", "servlet-context-name=",
		"servlet-filter-name=MCP Protected Resource Metadata Well-Known Filter",
		"url-pattern=/.well-known/oauth-protected-resource"
	},
	service = Filter.class
)
public class MCPProtectedResourceMetadataWellKnownFilter extends BaseFilter {

	@Override
	protected Log getLog() {
		return _log;
	}

	@Override
	protected void processFilter(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FilterChain filterChain)
		throws Exception {

		if (!Objects.equals(httpServletRequest.getMethod(), "GET")) {
			httpServletResponse.setHeader("Allow", "GET");
			httpServletResponse.sendError(
				HttpServletResponse.SC_METHOD_NOT_ALLOWED);

			return;
		}

		String portalURL =
			_portal.getPortalURL(httpServletRequest) + _portal.getPathContext();

		JSONObject metadataJSONObject = JSONUtil.put(
			"authorization_servers", JSONUtil.putAll(portalURL)
		).put(
			"bearer_methods_supported", JSONUtil.putAll("header")
		).put(
			"resource", portalURL + _MCP_PATH
		).put(
			"resource_name", "Liferay MCP Server"
		);

		httpServletResponse.setCharacterEncoding(StringPool.UTF8);
		httpServletResponse.setContentType(ContentTypes.APPLICATION_JSON);
		httpServletResponse.setHeader(
			HttpHeaders.CACHE_CONTROL, "public, max-age=300");
		httpServletResponse.setStatus(HttpServletResponse.SC_OK);

		ServletResponseUtil.write(
			httpServletResponse, metadataJSONObject.toString());

		httpServletResponse.flushBuffer();
	}

	private static final String _MCP_PATH = "/o/mcp";

	private static final Log _log = LogFactoryUtil.getLog(
		MCPProtectedResourceMetadataWellKnownFilter.class);

	@Reference
	private Portal _portal;

}