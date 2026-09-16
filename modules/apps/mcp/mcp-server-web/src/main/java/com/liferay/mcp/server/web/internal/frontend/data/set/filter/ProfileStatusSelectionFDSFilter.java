/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.web.internal.frontend.data.set.filter;

import com.liferay.frontend.data.set.filter.BaseSelectionFDSFilter;
import com.liferay.frontend.data.set.filter.FDSFilter;
import com.liferay.frontend.data.set.filter.SelectionFDSFilterItem;
import com.liferay.mcp.server.web.internal.constants.MCPServerFDSNames;

import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;

/**
 * @author Jose Luis Navarro
 */
@Component(
	property = "frontend.data.set.name=" + MCPServerFDSNames.PROFILES,
	service = FDSFilter.class
)
public class ProfileStatusSelectionFDSFilter extends BaseSelectionFDSFilter {

	@Override
	public String getId() {
		return "profileStatus";
	}

	@Override
	public String getLabel() {
		return "status";
	}

	@Override
	public List<SelectionFDSFilterItem> getSelectionFDSFilterItems(
		Locale locale) {

		return List.of(
			new SelectionFDSFilterItem("active", "active"),
			new SelectionFDSFilterItem("inactive", "inactive"));
	}

}