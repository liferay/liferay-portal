/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.resource.v1_0;

import com.liferay.mcp.server.rest.dto.v1_0.ToolSearchResult;
import com.liferay.mcp.server.rest.resource.v1_0.ToolSearchResultResource;
import com.liferay.portal.vulcan.pagination.Page;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Petteri Karttunen
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/tool-search-result.properties",
	scope = ServiceScope.PROTOTYPE, service = ToolSearchResultResource.class
)
public class ToolSearchResultResourceImpl
	extends BaseToolSearchResultResourceImpl {

	@Override
	public Page<ToolSearchResult> getToolSearchToolSearchResultsPage(
			Boolean includeRequiredInputSchema, String search)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

}