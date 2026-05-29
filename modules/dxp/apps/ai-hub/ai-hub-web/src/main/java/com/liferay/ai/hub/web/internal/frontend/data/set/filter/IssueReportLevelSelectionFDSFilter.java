/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.web.internal.frontend.data.set.filter;

import com.liferay.ai.hub.web.internal.constants.AIHubFDSNames;
import com.liferay.frontend.data.set.filter.BaseSelectionFDSFilter;
import com.liferay.frontend.data.set.filter.FDSFilter;
import com.liferay.frontend.data.set.filter.SelectionFDSFilterItem;

import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;

/**
 * @author Davyson Melo
 */
@Component(
	property = "frontend.data.set.name=" + AIHubFDSNames.ISSUE_REPORTS,
	service = FDSFilter.class
)
public class IssueReportLevelSelectionFDSFilter extends BaseSelectionFDSFilter {

	@Override
	public String getId() {
		return "level";
	}

	@Override
	public String getLabel() {
		return "level";
	}

	@Override
	public List<SelectionFDSFilterItem> getSelectionFDSFilterItems(
		Locale locale) {

		return List.of(
			new SelectionFDSFilterItem("CRITICAL", "critical"),
			new SelectionFDSFilterItem("HIGH", "high"),
			new SelectionFDSFilterItem("MEDIUM", "medium"),
			new SelectionFDSFilterItem("LOW", "low"));
	}

}