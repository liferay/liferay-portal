/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util.comparator;

import com.liferay.portal.kernel.model.LayoutRevision;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.OrderByComparator;

/**
 * @author Julio Camarero
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 */
@Deprecated
public class LayoutRevisionCreateDateComparator
	extends OrderByComparator<LayoutRevision> {

	public static final String ORDER_BY_ASC = "LayoutRevision.createDate ASC";

	public static final String ORDER_BY_DESC = "LayoutRevision.createDate DESC";

	public static final String[] ORDER_BY_FIELDS = {"createDate"};

	public static LayoutRevisionCreateDateComparator getInstance(
		boolean ascending) {

		if (ascending) {
			return _INSTANCE_ASCENDING;
		}

		return _INSTANCE_DESCENDING;
	}

	@Override
	public int compare(
		LayoutRevision layoutRevision1, LayoutRevision layoutRevision2) {

		int value = DateUtil.compareTo(
			layoutRevision1.getCreateDate(), layoutRevision2.getCreateDate());

		if (_ascending) {
			return value;
		}

		return -value;
	}

	@Override
	public String getOrderBy() {
		if (_ascending) {
			return ORDER_BY_ASC;
		}

		return ORDER_BY_DESC;
	}

	@Override
	public String[] getOrderByFields() {
		return ORDER_BY_FIELDS;
	}

	@Override
	public boolean isAscending() {
		return _ascending;
	}

	private LayoutRevisionCreateDateComparator(boolean ascending) {
		_ascending = ascending;
	}

	private static final LayoutRevisionCreateDateComparator
		_INSTANCE_ASCENDING = new LayoutRevisionCreateDateComparator(true);

	private static final LayoutRevisionCreateDateComparator
		_INSTANCE_DESCENDING = new LayoutRevisionCreateDateComparator(false);

	private final boolean _ascending;

}