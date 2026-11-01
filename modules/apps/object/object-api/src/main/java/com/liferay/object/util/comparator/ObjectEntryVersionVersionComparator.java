/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.util.comparator;

import com.liferay.object.model.ObjectEntryVersion;
import com.liferay.portal.kernel.util.OrderByComparator;

/**
 * @author Feliphe Marinho
 */
public class ObjectEntryVersionVersionComparator
	extends OrderByComparator<ObjectEntryVersion> {

	public static final String ORDER_BY_ASC = "ObjectEntryVersion.version ASC";

	public static final String ORDER_BY_DESC =
		"ObjectEntryVersion.version DESC";

	public static final String[] ORDER_BY_FIELDS = {"version"};

	public static ObjectEntryVersionVersionComparator getInstance(
		boolean ascending) {

		if (ascending) {
			return _INSTANCE_ASCENDING;
		}

		return _INSTANCE_DESCENDING;
	}

	@Override
	public int compare(
		ObjectEntryVersion objectEntryVersion1,
		ObjectEntryVersion objectEntryVersion2) {

		int value = Integer.compare(
			objectEntryVersion1.getVersion(), objectEntryVersion2.getVersion());

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

	private ObjectEntryVersionVersionComparator(boolean ascending) {
		_ascending = ascending;
	}

	private static final ObjectEntryVersionVersionComparator
		_INSTANCE_ASCENDING = new ObjectEntryVersionVersionComparator(true);

	private static final ObjectEntryVersionVersionComparator
		_INSTANCE_DESCENDING = new ObjectEntryVersionVersionComparator(false);

	private final boolean _ascending;

}