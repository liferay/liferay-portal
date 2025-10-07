/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.util.comparator;

import com.liferay.osb.patcher.model.PatcherAccount;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.OrderByComparator;

/**
 * @author Eudaldo Alonso
 */
public class PatcherAccountModifiedDateComparator
	extends OrderByComparator<PatcherAccount> {

	public static final String ORDER_BY_ASC = "PatcherAccount.modifiedDate ASC";

	public static final String ORDER_BY_DESC =
		"PatcherAccount.modifiedDate DESC";

	public static final String[] ORDER_BY_FIELDS = {"modifiedDate"};

	public static PatcherAccountModifiedDateComparator getInstance(
		boolean ascending) {

		if (ascending) {
			return _INSTANCE_ASCENDING;
		}

		return _INSTANCE_DESCENDING;
	}

	@Override
	public int compare(
		PatcherAccount patcherAccount1, PatcherAccount patcherAccount2) {

		int value = DateUtil.compareTo(
			patcherAccount1.getModifiedDate(),
			patcherAccount2.getModifiedDate());

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

	private PatcherAccountModifiedDateComparator(boolean ascending) {
		_ascending = ascending;
	}

	private static final PatcherAccountModifiedDateComparator
		_INSTANCE_ASCENDING = new PatcherAccountModifiedDateComparator(true);

	private static final PatcherAccountModifiedDateComparator
		_INSTANCE_DESCENDING = new PatcherAccountModifiedDateComparator(false);

	private final boolean _ascending;

}