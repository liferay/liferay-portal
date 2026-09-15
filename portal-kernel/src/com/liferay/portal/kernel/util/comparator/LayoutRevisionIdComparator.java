/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util.comparator;

import com.liferay.portal.kernel.model.LayoutRevision;
import com.liferay.portal.kernel.util.OrderByComparator;

/**
 * @author Raymond Augé
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 */
@Deprecated
public class LayoutRevisionIdComparator
	extends OrderByComparator<LayoutRevision> {

	public static final String ORDER_BY_ASC =
		"LayoutRevision.layoutRevisionId ASC";

	public static final String ORDER_BY_DESC =
		"LayoutRevision.layoutRevisionId DESC";

	public static final String[] ORDER_BY_FIELDS = {"layoutRevisionId"};

	public static LayoutRevisionIdComparator getInstance(boolean ascending) {
		if (ascending) {
			return _INSTANCE_ASCENDING;
		}

		return _INSTANCE_DESCENDING;
	}

	@Override
	public int compare(
		LayoutRevision layoutRevision1, LayoutRevision layoutRevision2) {

		int value = 0;

		if (layoutRevision1.getLayoutRevisionId() >
				layoutRevision2.getLayoutRevisionId()) {

			value = 1;
		}
		else if (layoutRevision1.getLayoutRevisionId() <
					layoutRevision2.getLayoutRevisionId()) {

			value = -1;
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

	@Override
	public String[] getOrderByFields() {
		return ORDER_BY_FIELDS;
	}

	@Override
	public boolean isAscending() {
		return _ascending;
	}

	private LayoutRevisionIdComparator(boolean ascending) {
		_ascending = ascending;
	}

	private static final LayoutRevisionIdComparator _INSTANCE_ASCENDING =
		new LayoutRevisionIdComparator(true);

	private static final LayoutRevisionIdComparator _INSTANCE_DESCENDING =
		new LayoutRevisionIdComparator(false);

	private final boolean _ascending;

}