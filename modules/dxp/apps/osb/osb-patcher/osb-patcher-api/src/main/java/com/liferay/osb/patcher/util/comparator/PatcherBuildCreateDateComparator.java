/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.util.comparator;

import com.liferay.osb.patcher.model.PatcherBuild;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.OrderByComparator;

/**
 * @author Eudaldo Alonso
 */
public class PatcherBuildCreateDateComparator
	extends OrderByComparator<PatcherBuild> {

	public static final String ORDER_BY_ASC = "PatcherBuild.createDate ASC";

	public static final String ORDER_BY_DESC = "PatcherBuild.createDate DESC";

	public static final String[] ORDER_BY_FIELDS = {"createDate"};

	public static PatcherBuildCreateDateComparator getInstance(
		boolean ascending) {

		if (ascending) {
			return _INSTANCE_ASCENDING;
		}

		return _INSTANCE_DESCENDING;
	}

	@Override
	public int compare(PatcherBuild patcherBuild1, PatcherBuild patcherBuild2) {
		int value = DateUtil.compareTo(
			patcherBuild1.getCreateDate(), patcherBuild2.getCreateDate());

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

	private PatcherBuildCreateDateComparator(boolean ascending) {
		_ascending = ascending;
	}

	private static final PatcherBuildCreateDateComparator _INSTANCE_ASCENDING =
		new PatcherBuildCreateDateComparator(true);

	private static final PatcherBuildCreateDateComparator _INSTANCE_DESCENDING =
		new PatcherBuildCreateDateComparator(false);

	private final boolean _ascending;

}