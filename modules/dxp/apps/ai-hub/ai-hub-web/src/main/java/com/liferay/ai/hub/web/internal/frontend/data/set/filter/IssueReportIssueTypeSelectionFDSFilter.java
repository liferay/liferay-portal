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
public class IssueReportIssueTypeSelectionFDSFilter
	extends BaseSelectionFDSFilter {

	@Override
	public String getId() {
		return "issueType";
	}

	@Override
	public String getLabel() {
		return "issue-type";
	}

	@Override
	public List<SelectionFDSFilterItem> getSelectionFDSFilterItems(
		Locale locale) {

		return List.of(
			new SelectionFDSFilterItem(
				"AGENT_ERROR_OR_MALFUNCTION", "agent-error-or-malfunction"),
			new SelectionFDSFilterItem(
				"INAPPROPRIATE_OR_HARMFUL_CONTENT",
				"inappropriate-or-harmful-content"),
			new SelectionFDSFilterItem(
				"INCORRECT_OR_INACCURATE_RESPONSE",
				"incorrect-or-inaccurate-response"),
			new SelectionFDSFilterItem("OTHER", "other"),
			new SelectionFDSFilterItem("PII_EXPOSURE", "pii-exposure"));
	}

}