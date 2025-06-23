/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.util.comparator;

import com.liferay.osb.patcher.model.PatcherProjectVersion;
import com.liferay.portal.kernel.util.OrderByComparator;

/**
 * @author Eudaldo Alonso
 */
public class PatcherProjectVersionNameComparator
	extends OrderByComparator<PatcherProjectVersion> {

	public static final String ORDER_BY_ASC = "PatcherProjectVersion.name ASC";

	public static final String ORDER_BY_DESC =
		"PatcherProjectVersion.name DESC";

	public static final String[] ORDER_BY_FIELDS = {"name"};

	public static PatcherProjectVersionNameComparator getInstance(
		boolean ascending) {

		if (ascending) {
			return _INSTANCE_ASCENDING;
		}

		return _INSTANCE_DESCENDING;
	}

	@Override
	public int compare(
		PatcherProjectVersion patcherProjectVersion1,
		PatcherProjectVersion patcherProjectVersion2) {

		String name1 = patcherProjectVersion1.getName();
		String name2 = patcherProjectVersion2.getName();

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

	private PatcherProjectVersionNameComparator(boolean ascending) {
		_ascending = ascending;
	}

	private static final PatcherProjectVersionNameComparator
		_INSTANCE_ASCENDING = new PatcherProjectVersionNameComparator(true);

	private static final PatcherProjectVersionNameComparator
		_INSTANCE_DESCENDING = new PatcherProjectVersionNameComparator(false);

	private final boolean _ascending;

}