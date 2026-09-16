/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.cache;

import com.liferay.mcp.server.rest.internal.servlet.MCPServerServlet;
import com.liferay.mcp.server.rest.internal.util.ToolSetUtil;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.cluster.Clusterable;
import com.liferay.portal.kernel.module.framework.service.IdentifiableOSGiService;

import jakarta.servlet.Servlet;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Petteri Karttunen
 */
@Component(service = AopService.class)
public class MCPServerCacheManagerImpl
	implements AopService, IdentifiableOSGiService, MCPServerCacheManager {

	@Clusterable
	@Override
	public void clearOpenAPIJSONObjectCache(long companyId) {
		ToolSetUtil.clearOpenAPIJSONObjectCache(companyId);
	}

	@Clusterable
	@Override
	public void clearServletCache(long companyId) {
		MCPServerServlet mcpServerServlet = (MCPServerServlet)_servlet;

		mcpServerServlet.invalidateAll(companyId);
	}

	@Clusterable
	@Override
	public void clearServletCache(long companyId, String mcpServerProfileName) {
		MCPServerServlet mcpServerServlet = (MCPServerServlet)_servlet;

		mcpServerServlet.invalidate(companyId, mcpServerProfileName);
	}

	@Override
	public String getOSGiServiceIdentifier() {
		return MCPServerCacheManager.class.getName();
	}

	@Reference(
		target = "(osgi.http.whiteboard.servlet.name=com.liferay.mcp.server.rest.internal.servlet.MCPServerServlet)"
	)
	private Servlet _servlet;

}