/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.frontend.data.set.filter;

import com.liferay.frontend.data.set.filter.BaseDateTimeRangeFDSFilter;
import com.liferay.frontend.data.set.filter.DateTimeFDSFilterItem;
import com.liferay.frontend.data.set.filter.FDSFilter;
import com.liferay.site.cms.site.initializer.internal.constants.CMSSiteInitializerFDSNames;

import org.osgi.service.component.annotations.Component;

/**
 * @author Jan Brychta
 */
@Component(
	property = {
		"frontend.data.set.name=" + CMSSiteInitializerFDSNames.TASKS_SECTION,
		"service.ranking:Integer=90"
	},
	service = FDSFilter.class
)
public class TaskDueDateTimeRangeFDSFilter extends BaseDateTimeRangeFDSFilter {

	@Override
	public String getId() {
		return "dueDate";
	}

	@Override
	public String getLabel() {
		return "due-date";
	}

	@Override
	public DateTimeFDSFilterItem getMaxDateTimeFDSFilterItem() {
		return new DateTimeFDSFilterItem(0, 0, 0, 0, 0);
	}

	@Override
	public DateTimeFDSFilterItem getMinDateTimeFDSFilterItem() {
		return new DateTimeFDSFilterItem(0, 0, 0, 0, 0);
	}

}