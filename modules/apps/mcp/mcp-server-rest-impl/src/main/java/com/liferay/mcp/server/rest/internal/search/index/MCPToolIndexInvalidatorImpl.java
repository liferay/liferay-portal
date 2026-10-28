/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.search.index;

import com.liferay.mcp.server.rest.internal.util.ToolSetUtil;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.cluster.Clusterable;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.module.framework.service.IdentifiableOSGiService;
import com.liferay.portal.search.capabilities.SearchCapabilities;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Petteri Karttunen
 */
@Component(service = AopService.class)
public class MCPToolIndexInvalidatorImpl
	implements AopService, IdentifiableOSGiService, MCPToolIndexInvalidator {

	@Override
	public String getOSGiServiceIdentifier() {
		return MCPToolIndexInvalidator.class.getName();
	}

	@Clusterable
	@Override
	public void invalidate(long companyId) {
		if (!_isEnabled(companyId)) {
			return;
		}

		_mcpToolIndexWriter.invalidate(companyId);
	}

	@Clusterable
	@Override
	public void invalidate(long companyId, String restContextPath) {
		if (!_isEnabled(companyId)) {
			return;
		}

		String toolSetName = ToolSetUtil.getToolSetName(
			companyId, restContextPath);

		if (toolSetName == null) {
			return;
		}

		_mcpToolIndexWriter.invalidate(companyId, toolSetName);
	}

	private boolean _isEnabled(long companyId) {
		if (FeatureFlagManagerUtil.isEnabled(companyId, "LPD-63311") &&
			_searchCapabilities.isMCPToolSearchSupported()) {

			return true;
		}

		return false;
	}

	@Reference
	private MCPToolIndexWriter _mcpToolIndexWriter;

	@Reference
	private SearchCapabilities _searchCapabilities;

}