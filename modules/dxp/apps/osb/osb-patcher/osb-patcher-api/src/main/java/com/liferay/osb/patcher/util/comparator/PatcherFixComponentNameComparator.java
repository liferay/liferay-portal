/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.util.comparator;

import com.liferay.osb.patcher.model.PatcherFixComponent;
import com.liferay.portal.kernel.util.OrderByComparator;

/**
 * @author Eudaldo Alonso
 */
public class PatcherFixComponentNameComparator
	extends OrderByComparator<PatcherFixComponent> {

	public static final String ORDER_BY_ASC = "PatcherFixComponent.name ASC";

	public static final String ORDER_BY_DESC = "PatcherFixComponent.name DESC";

	public static final String[] ORDER_BY_FIELDS = {"name"};

	public static PatcherFixComponentNameComparator getInstance(
		boolean ascending) {

		if (ascending) {
			return _INSTANCE_ASCENDING;
		}

		return _INSTANCE_DESCENDING;
	}

	@Override
	public int compare(
		PatcherFixComponent patcherFixComponent1,
		PatcherFixComponent patcherFixComponent2) {

		String name1 = patcherFixComponent1.getName();
		String name2 = patcherFixComponent2.getName();

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

	private PatcherFixComponentNameComparator(boolean ascending) {
		_ascending = ascending;
	}

	private static final PatcherFixComponentNameComparator _INSTANCE_ASCENDING =
		new PatcherFixComponentNameComparator(true);

	private static final PatcherFixComponentNameComparator
		_INSTANCE_DESCENDING = new PatcherFixComponentNameComparator(false);

	private final boolean _ascending;

}