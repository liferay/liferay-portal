/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.servlet;

import com.liferay.mcp.server.rest.internal.constants.MCPServerConstants;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.Portal;

import jakarta.servlet.Servlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jorge García Jiménez
 */
@Component(
	property = {
		"osgi.http.whiteboard.servlet.name=com.liferay.mcp.server.rest.internal.servlet.MCPServerProtectedResourceMetadataServlet",
		"osgi.http.whiteboard.servlet.pattern=/.well-known/oauth-protected-resource/mcp"
	},
	service = Servlet.class
)
public class MCPServerProtectedResourceMetadataServlet extends HttpServlet {

	@Override
	protected void service(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		if (!FeatureFlagManagerUtil.isEnabled(
				_portal.getCompanyId(httpServletRequest), "LPD-63415")) {

			httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);

			return;
		}

		httpServletResponse.setHeader(
			"Access-Control-Allow-Headers",
			"Authorization, Content-Type, MCP-Protocol-Version");
		httpServletResponse.setHeader(
			"Access-Control-Allow-Methods", "GET, HEAD, OPTIONS");
		httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
		httpServletResponse.setHeader("Access-Control-Max-Age", "300");

		String method = httpServletRequest.getMethod();

		if (Objects.equals(method, "OPTIONS")) {
			httpServletResponse.setStatus(HttpServletResponse.SC_NO_CONTENT);

			return;
		}

		if (!Objects.equals(method, "GET") && !Objects.equals(method, "HEAD")) {
			httpServletResponse.setHeader("Allow", "GET, HEAD, OPTIONS");

			httpServletResponse.sendError(
				HttpServletResponse.SC_METHOD_NOT_ALLOWED);

			return;
		}

		httpServletResponse.setCharacterEncoding(StringPool.UTF8);
		httpServletResponse.setContentType(ContentTypes.APPLICATION_JSON);
		httpServletResponse.setHeader(
			HttpHeaders.CACHE_CONTROL, "public, max-age=300");
		httpServletResponse.setStatus(HttpServletResponse.SC_OK);

		if (Objects.equals(method, "GET")) {
			String portalURL =
				_portal.getPortalURL(httpServletRequest) +
					_portal.getPathContext();

			ServletResponseUtil.write(
				httpServletResponse,
				JSONUtil.put(
					"authorization_servers", JSONUtil.putAll(portalURL)
				).put(
					"bearer_methods_supported", JSONUtil.putAll("header")
				).put(
					"resource",
					portalURL + Portal.PATH_MODULE + MCPServerConstants.PATH_MCP
				).put(
					"resource_name", "Liferay MCP Server"
				).toString());
		}

		httpServletResponse.flushBuffer();
	}

	@Reference
	private Portal _portal;

}