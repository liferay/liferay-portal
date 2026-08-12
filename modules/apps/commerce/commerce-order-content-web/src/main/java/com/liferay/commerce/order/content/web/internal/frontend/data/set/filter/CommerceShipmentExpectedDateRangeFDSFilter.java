/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.content.web.internal.frontend.data.set.filter;

import com.liferay.commerce.order.content.web.internal.constants.CommerceOrderFragmentFDSNames;
import com.liferay.frontend.data.set.constants.FDSEntityFieldTypes;
import com.liferay.frontend.data.set.filter.BaseDateRangeFDSFilter;
import com.liferay.frontend.data.set.filter.DateFDSFilterItem;
import com.liferay.frontend.data.set.filter.FDSFilter;

import java.util.Calendar;

import org.osgi.service.component.annotations.Component;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "frontend.data.set.name=" + CommerceOrderFragmentFDSNames.PLACED_ORDER_SHIPMENTS,
	service = FDSFilter.class
)
public class CommerceShipmentExpectedDateRangeFDSFilter
	extends BaseDateRangeFDSFilter {

	@Override
	public String getEntityFieldType() {
		return FDSEntityFieldTypes.DATE_TIME;
	}

	@Override
	public String getId() {
		return "expectedDate";
	}

	@Override
	public String getLabel() {
		return "delivery-date-range";
	}

	@Override
	public DateFDSFilterItem getMaxDateFDSFilterItem() {
		Calendar calendar = Calendar.getInstance();

		return new DateFDSFilterItem(
			calendar.get(Calendar.DAY_OF_MONTH),
			calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR) + 1);
	}

	@Override
	public DateFDSFilterItem getMinDateFDSFilterItem() {
		return new DateFDSFilterItem(0, 0, 0);
	}

}