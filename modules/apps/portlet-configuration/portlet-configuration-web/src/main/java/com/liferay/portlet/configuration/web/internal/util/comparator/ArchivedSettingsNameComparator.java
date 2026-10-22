/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.configuration.web.internal.util.comparator;

import com.liferay.portal.kernel.model.PortletItem;
import com.liferay.portal.kernel.util.OrderByComparator;

/**
 * @author Eudaldo Alonso
 */
public class ArchivedSettingsNameComparator
	extends OrderByComparator<PortletItem> {

	public static ArchivedSettingsNameComparator getInstance(
		boolean ascending) {

		if (ascending) {
			return _INSTANCE_ASCENDING;
		}

		return _INSTANCE_DESCENDING;
	}

	@Override
	public int compare(PortletItem portletItem1, PortletItem portletItem2) {
		String name1 = portletItem1.getName();
		String name2 = portletItem2.getName();

		int value = name1.compareTo(name2);

		if (_ascending) {
			return value;
		}

		return -value;
	}

	private ArchivedSettingsNameComparator(boolean ascending) {
		_ascending = ascending;
	}

	private static final ArchivedSettingsNameComparator _INSTANCE_ASCENDING =
		new ArchivedSettingsNameComparator(true);

	private static final ArchivedSettingsNameComparator _INSTANCE_DESCENDING =
		new ArchivedSettingsNameComparator(false);

	private final boolean _ascending;

}