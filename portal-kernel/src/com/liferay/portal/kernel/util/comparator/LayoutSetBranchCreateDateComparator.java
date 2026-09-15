/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util.comparator;

import com.liferay.portal.kernel.model.LayoutSetBranch;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.OrderByComparator;

/**
 * @author Miguel Pastor
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 */
@Deprecated
public class LayoutSetBranchCreateDateComparator
	extends OrderByComparator<LayoutSetBranch> {

	public static final String ORDER_BY_ASC = "createDate ASC";

	public static final String ORDER_BY_DESC = "createDate DESC";

	public static final String[] ORDER_BY_FIELDS = {"createDate"};

	public static LayoutSetBranchCreateDateComparator getInstance(
		boolean ascending) {

		if (ascending) {
			return _INSTANCE_ASCENDING;
		}

		return _INSTANCE_DESCENDING;
	}

	@Override
	public int compare(
		LayoutSetBranch layoutSetBranch1, LayoutSetBranch layoutSetBranch2) {

		int value = DateUtil.compareTo(
			layoutSetBranch1.getCreateDate(), layoutSetBranch2.getCreateDate());

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

	private LayoutSetBranchCreateDateComparator(boolean ascending) {
		_ascending = ascending;
	}

	private static final LayoutSetBranchCreateDateComparator
		_INSTANCE_ASCENDING = new LayoutSetBranchCreateDateComparator(true);

	private static final LayoutSetBranchCreateDateComparator
		_INSTANCE_DESCENDING = new LayoutSetBranchCreateDateComparator(false);

	private final boolean _ascending;

}