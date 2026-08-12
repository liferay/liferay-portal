/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.web.internal.frontend.data.set.filter;

import com.liferay.commerce.payment.web.internal.constants.CommercePaymentsFDSNames;
import com.liferay.frontend.data.set.constants.FDSEntityFieldTypes;
import com.liferay.frontend.data.set.filter.BaseDateRangeFDSFilter;
import com.liferay.frontend.data.set.filter.DateFDSFilterItem;
import com.liferay.frontend.data.set.filter.FDSFilter;

import java.util.Calendar;

import org.osgi.service.component.annotations.Component;

/**
 * @author Crescenzo Rega
 */
@Component(
	property = "frontend.data.set.name=" + CommercePaymentsFDSNames.REFUNDS,
	service = FDSFilter.class
)
public class CommerceRefundCreateDateRangeFDSFilter
	extends BaseDateRangeFDSFilter {

	@Override
	public String getEntityFieldType() {
		return FDSEntityFieldTypes.DATE_TIME;
	}

	@Override
	public String getId() {
		return "createDate";
	}

	@Override
	public String getLabel() {
		return "refund-date";
	}

	@Override
	public DateFDSFilterItem getMaxDateFDSFilterItem() {
		Calendar calendar = Calendar.getInstance();

		return new DateFDSFilterItem(
			calendar.get(Calendar.DAY_OF_MONTH),
			calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
	}

	@Override
	public DateFDSFilterItem getMinDateFDSFilterItem() {
		return new DateFDSFilterItem(0, 0, 0);
	}

}