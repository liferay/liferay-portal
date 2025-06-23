/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.util.comparator;

import com.liferay.osb.patcher.model.PatcherFixPack;
import com.liferay.portal.kernel.util.OrderByComparator;

/**
 * @author Eudaldo Alonso
 */
public class PatcherFixPackVersionComparator
	extends OrderByComparator<PatcherFixPack> {

	public static final String ORDER_BY_ASC = "PatcherFixPack.version ASC";

	public static final String ORDER_BY_DESC = "PatcherFixPack.version DESC";

	public static final String[] ORDER_BY_FIELDS = {"version"};

	public static PatcherFixPackVersionComparator getInstance(
		boolean ascending) {

		if (ascending) {
			return _INSTANCE_ASCENDING;
		}

		return _INSTANCE_DESCENDING;
	}

	@Override
	public int compare(
		PatcherFixPack patcherFixPack1, PatcherFixPack patcherFixPack2) {

		int version1 = patcherFixPack1.getVersion();
		int version2 = patcherFixPack2.getVersion();

		int value = Integer.compare(version1, version2);

		if (_ascending) {
			return value;
		}

		return Math.negateExact(value);
	}

	@Override
	public String getOrderBy() {
		if (_ascending) {
			return ORDER_BY_ASC;
		}

		return ORDER_BY_DESC;
	}

	private PatcherFixPackVersionComparator(boolean ascending) {
		_ascending = ascending;
	}

	private static final PatcherFixPackVersionComparator _INSTANCE_ASCENDING =
		new PatcherFixPackVersionComparator(true);

	private static final PatcherFixPackVersionComparator _INSTANCE_DESCENDING =
		new PatcherFixPackVersionComparator(false);

	private final boolean _ascending;

}