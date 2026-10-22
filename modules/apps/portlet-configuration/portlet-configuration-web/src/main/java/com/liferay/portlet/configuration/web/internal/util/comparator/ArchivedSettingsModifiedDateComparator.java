/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.configuration.web.internal.util.comparator;

import com.liferay.portal.kernel.model.PortletItem;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.OrderByComparator;

/**
 * @author Eudaldo Alonso
 */
public class ArchivedSettingsModifiedDateComparator
	extends OrderByComparator<PortletItem> {

	public static ArchivedSettingsModifiedDateComparator getInstance(
		boolean ascending) {

		if (ascending) {
			return _INSTANCE_ASCENDING;
		}

		return _INSTANCE_DESCENDING;
	}

	@Override
	public int compare(PortletItem portletItem1, PortletItem portletItem2) {
		int value = DateUtil.compareTo(
			portletItem1.getModifiedDate(), portletItem2.getModifiedDate());

		if (_ascending) {
			return value;
		}

		return -value;
	}

	private ArchivedSettingsModifiedDateComparator(boolean ascending) {
		_ascending = ascending;
	}

	private static final ArchivedSettingsModifiedDateComparator
		_INSTANCE_ASCENDING = new ArchivedSettingsModifiedDateComparator(true);

	private static final ArchivedSettingsModifiedDateComparator
		_INSTANCE_DESCENDING = new ArchivedSettingsModifiedDateComparator(
			false);

	private final boolean _ascending;

}