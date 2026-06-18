/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.web.internal.frontend.data.set.filter;

import com.liferay.frontend.data.set.filter.BaseSelectionFDSFilter;
import com.liferay.frontend.data.set.filter.FDSFilter;
import com.liferay.frontend.data.set.filter.SelectionFDSFilterItem;
import com.liferay.mcp.server.web.internal.constants.MCPServerWebFDSNames;

import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;

/**
 * @author Jose Luis Navarro
 */
@Component(
	property = "frontend.data.set.name=" + MCPServerWebFDSNames.DATA_MASKS,
	service = FDSFilter.class
)
public class MaskTypeSelectionFDSFilter extends BaseSelectionFDSFilter {

	@Override
	public String getId() {
		return "maskType";
	}

	@Override
	public String getLabel() {
		return "type";
	}

	@Override
	public List<SelectionFDSFilterItem> getSelectionFDSFilterItems(
		Locale locale) {

		return List.of(
			new SelectionFDSFilterItem("system", "system"),
			new SelectionFDSFilterItem("custom", "custom"));
	}

	@Override
	public boolean isMultiple() {
		return true;
	}

}