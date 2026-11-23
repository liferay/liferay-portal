/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.object.entries.frontend.data.set.filter;

import com.liferay.frontend.data.set.filter.BaseDateRangeFDSFilter;
import com.liferay.frontend.data.set.filter.DateFDSFilterItem;

import java.util.Calendar;

/**
 * @author Feliphe Marinho
 */
public class ObjectFieldDateRangeFDSFilter extends BaseDateRangeFDSFilter {

	public ObjectFieldDateRangeFDSFilter(
		long groupId, String id, String label) {

		_groupId = groupId;
		_id = id;
		_label = label;
	}

	public long getGroupId() {
		return _groupId;
	}

	@Override
	public String getId() {
		return _id;
	}

	@Override
	public String getLabel() {
		return _label;
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

	private final long _groupId;
	private final String _id;
	private final String _label;

}