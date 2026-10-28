/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.search.index;

import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.search.capabilities.SearchCapabilities;
import com.liferay.portal.search.spi.reindexer.IndexReindexer;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Petteri Karttunen
 */
@Component(service = IndexReindexer.class)
public class MCPToolIndexReindexer implements IndexReindexer {

	@Override
	public void reindex(long companyId, ExecutionMode executionMode)
		throws Exception {

		if (!_searchCapabilities.isMCPToolSearchSupported() ||
			(companyId == CompanyConstants.SYSTEM)) {

			return;
		}

		_mcpToolIndexWriter.deleteIndex(companyId);

		if (FeatureFlagManagerUtil.isEnabled(companyId, "LPD-63311")) {
			_mcpToolIndexInvalidator.invalidate(companyId);
		}
	}

	@Reference
	private MCPToolIndexInvalidator _mcpToolIndexInvalidator;

	@Reference
	private MCPToolIndexWriter _mcpToolIndexWriter;

	@Reference
	private SearchCapabilities _searchCapabilities;

}