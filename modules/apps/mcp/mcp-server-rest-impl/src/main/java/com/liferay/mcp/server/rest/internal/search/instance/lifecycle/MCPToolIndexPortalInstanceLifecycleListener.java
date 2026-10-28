/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.search.instance.lifecycle;

import com.liferay.mcp.server.rest.internal.search.index.MCPToolIndexWriter;
import com.liferay.portal.instance.lifecycle.BasePortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.search.capabilities.SearchCapabilities;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Petteri Karttunen
 */
@Component(service = PortalInstanceLifecycleListener.class)
public class MCPToolIndexPortalInstanceLifecycleListener
	extends BasePortalInstanceLifecycleListener {

	@Override
	public void portalInstanceUnregistered(Company company) throws Exception {
		if (!_searchCapabilities.isMCPToolSearchSupported() ||
			(company.getCompanyId() == CompanyConstants.SYSTEM)) {

			return;
		}

		_mcpToolIndexWriter.deleteIndex(company.getCompanyId());
	}

	@Reference
	private MCPToolIndexWriter _mcpToolIndexWriter;

	@Reference
	private SearchCapabilities _searchCapabilities;

}