/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.util.comparator;

import com.liferay.osb.patcher.model.PatcherBuild;
import com.liferay.portal.kernel.util.OrderByComparator;

/**
 * @author Eudaldo Alonso
 */
public class PatcherBuildKeyVersionComparator
	extends OrderByComparator<PatcherBuild> {

	public static final String ORDER_BY_ASC = "PatcherBuild.keyVersion ASC";

	public static final String ORDER_BY_DESC = "PatcherBuild.keyVersion DESC";

	public static final String[] ORDER_BY_FIELDS = {"keyVersion"};

	public static PatcherBuildKeyVersionComparator getInstance(
		boolean ascending) {

		if (ascending) {
			return _INSTANCE_ASCENDING;
		}

		return _INSTANCE_DESCENDING;
	}

	@Override
	public int compare(PatcherBuild patcherBuild1, PatcherBuild patcherBuild2) {
		int value = 0;

		if (patcherBuild1.getKeyVersion() < patcherBuild2.getKeyVersion()) {
			value = -1;
		}
		else if (patcherBuild1.getKeyVersion() >
					patcherBuild2.getKeyVersion()) {

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

	private PatcherBuildKeyVersionComparator(boolean ascending) {
		_ascending = ascending;
	}

	private static final PatcherBuildKeyVersionComparator _INSTANCE_ASCENDING =
		new PatcherBuildKeyVersionComparator(true);

	private static final PatcherBuildKeyVersionComparator _INSTANCE_DESCENDING =
		new PatcherBuildKeyVersionComparator(false);

	private final boolean _ascending;

}