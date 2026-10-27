/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.frontend.data.set.filter;

import com.liferay.frontend.data.set.constants.FDSEntityFieldTypes;
import com.liferay.frontend.data.set.filter.BaseDateRangeFDSFilter;
import com.liferay.frontend.data.set.filter.DateFDSFilterItem;
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
public class TaskDueDateRangeFDSFilter extends BaseDateRangeFDSFilter {

	@Override
	public String getEntityFieldType() {
		return FDSEntityFieldTypes.DATE_TIME;
	}

	@Override
	public String getId() {
		return "dueDate";
	}

	@Override
	public String getLabel() {
		return "due-date";
	}

	@Override
	public DateFDSFilterItem getMaxDateFDSFilterItem() {
		return new DateFDSFilterItem(0, 0, 0);
	}

	@Override
	public DateFDSFilterItem getMinDateFDSFilterItem() {
		return new DateFDSFilterItem(0, 0, 0);
	}

}