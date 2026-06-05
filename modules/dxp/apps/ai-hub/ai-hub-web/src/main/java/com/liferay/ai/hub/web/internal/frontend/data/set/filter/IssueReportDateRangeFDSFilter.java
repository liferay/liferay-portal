/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.web.internal.frontend.data.set.filter;

import com.liferay.ai.hub.web.internal.constants.AIHubFDSNames;
import com.liferay.frontend.data.set.filter.BaseDateRangeFDSFilter;
import com.liferay.frontend.data.set.filter.DateFDSFilterItem;
import com.liferay.frontend.data.set.filter.FDSFilter;

import java.util.Calendar;

import org.osgi.service.component.annotations.Component;

/**
 * @author Davyson Melo
 */
@Component(
	property = "frontend.data.set.name=" + AIHubFDSNames.ISSUE_REPORTS,
	service = FDSFilter.class
)
public class IssueReportDateRangeFDSFilter extends BaseDateRangeFDSFilter {

	@Override
	public String getId() {
		return "dateCreated";
	}

	@Override
	public String getLabel() {
		return "date-range";
	}

	@Override
	public DateFDSFilterItem getMaxDateFDSFilterItem() {
		Calendar calendar = Calendar.getInstance();

		return new DateFDSFilterItem(
			calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH),
			calendar.get(Calendar.YEAR));
	}

	@Override
	public DateFDSFilterItem getMinDateFDSFilterItem() {
		Calendar calendar = Calendar.getInstance();

		calendar.add(Calendar.YEAR, -1);

		return new DateFDSFilterItem(
			calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH),
			calendar.get(Calendar.YEAR));
	}

}