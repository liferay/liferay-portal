/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.util.comparator;

import com.liferay.osb.patcher.model.PatcherProductVersion;
import com.liferay.portal.kernel.util.OrderByComparator;

/**
 * @author Eudaldo Alonso
 */
public class PatcherProductVersionNameComparator
	extends OrderByComparator<PatcherProductVersion> {

	public static final String ORDER_BY_ASC = "PatcherProductVersion.name ASC";

	public static final String ORDER_BY_DESC =
		"PatcherProductVersion.name DESC";

	public static final String[] ORDER_BY_FIELDS = {"name"};

	public static PatcherProductVersionNameComparator getInstance(
		boolean ascending) {

		if (ascending) {
			return _INSTANCE_ASCENDING;
		}

		return _INSTANCE_DESCENDING;
	}

	@Override
	public int compare(
		PatcherProductVersion patcherProductVersion1,
		PatcherProductVersion patcherProductVersion2) {

		String name1 = patcherProductVersion1.getName();
		String name2 = patcherProductVersion2.getName();

		int value = name1.compareToIgnoreCase(name2);

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

	private PatcherProductVersionNameComparator(boolean ascending) {
		_ascending = ascending;
	}

	private static final PatcherProductVersionNameComparator
		_INSTANCE_ASCENDING = new PatcherProductVersionNameComparator(true);

	private static final PatcherProductVersionNameComparator
		_INSTANCE_DESCENDING = new PatcherProductVersionNameComparator(false);

	private final boolean _ascending;

}