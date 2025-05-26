/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.util.comparator;

import com.liferay.osb.patcher.model.PatcherFix;
import com.liferay.portal.kernel.util.OrderByComparator;

/**
 * @author Eudaldo Alonso
 */
public class PatcherFixKeyVersionComparator
	extends OrderByComparator<PatcherFix> {

	public static final String ORDER_BY_ASC = "PatcherFix.keyVersion ASC";

	public static final String ORDER_BY_DESC = "PatcherFix.keyVersion DESC";

	public static final String[] ORDER_BY_FIELDS = {"keyVersion"};

	public static PatcherFixKeyVersionComparator getInstance(
		boolean ascending) {

		if (ascending) {
			return _INSTANCE_ASCENDING;
		}

		return _INSTANCE_DESCENDING;
	}

	@Override
	public int compare(PatcherFix patcherFix1, PatcherFix patcherFix2) {
		int value = 0;

		if (patcherFix1.getKeyVersion() < patcherFix2.getKeyVersion()) {
			value = -1;
		}
		else if (patcherFix1.getKeyVersion() > patcherFix2.getKeyVersion()) {
			value = 1;
		}

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

	private PatcherFixKeyVersionComparator(boolean ascending) {
		_ascending = ascending;
	}

	private static final PatcherFixKeyVersionComparator _INSTANCE_ASCENDING =
		new PatcherFixKeyVersionComparator(true);

	private static final PatcherFixKeyVersionComparator _INSTANCE_DESCENDING =
		new PatcherFixKeyVersionComparator(false);

	private final boolean _ascending;

}