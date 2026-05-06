/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.internal.servlet;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.Portal;

import jakarta.servlet.Servlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Publishes the OAuth 2.0 Protected Resource Metadata document (RFC 9728) for
 * the MCP server. Mounted at <code>/.well-known/oauth-protected-resource</code>
 * and <code>/.well-known/oauth-protected-resource/*</code> so clients reach the
 * doc whether they construct the path-suffix variant from the
 * <code>https://&lt;host&gt;/mcp</code> resource URI or hit the bare well-known
 * URL.
 *
 * @author Jorge García Jiménez
 */
@Component(
	property = {
		"osgi.http.whiteboard.servlet.name=com.liferay.mcp.server.internal.servlet.MCPProtectedResourceMetadataServlet",
		"osgi.http.whiteboard.servlet.pattern=/.well-known/oauth-protected-resource",
		"osgi.http.whiteboard.servlet.pattern=/.well-known/oauth-protected-resource/*"
	},
	service = Servlet.class
)
public class MCPProtectedResourceMetadataServlet extends HttpServlet {

	@Override
	protected void doGet(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		String portalURL = _portal.getPortalURL(httpServletRequest);

		String resource = portalURL + _MCP_PATH;

		JSONObject metadataJSONObject = JSONUtil.put(
			"authorization_servers", JSONUtil.putAll(portalURL)
		).put(
			"bearer_methods_supported", JSONUtil.putAll("header")
		).put(
			"resource", resource
		).put(
			"resource_name", "Liferay MCP Server"
		);

		httpServletResponse.setContentType(ContentTypes.APPLICATION_JSON);
		httpServletResponse.setHeader(
			HttpHeaders.CACHE_CONTROL, "public, max-age=300");

		try (PrintWriter printWriter = httpServletResponse.getWriter()) {
			printWriter.write(metadataJSONObject.toString());
		}
	}

	private static final String _MCP_PATH = "/mcp";

	@Reference
	private Portal _portal;

}