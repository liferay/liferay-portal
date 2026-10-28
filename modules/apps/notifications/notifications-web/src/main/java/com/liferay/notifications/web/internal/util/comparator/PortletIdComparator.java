/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notifications.web.internal.util.comparator;

import com.liferay.portal.kernel.util.CollatorUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import java.text.Collator;

import java.util.Comparator;
import java.util.Locale;

/**
 * @author Matthew Kong
 */
public class PortletIdComparator implements Comparator<String> {

	public PortletIdComparator(Locale locale) {
		_locale = locale;

		_collator = CollatorUtil.getInstance(locale);
	}

	@Override
	public int compare(String portletId1, String portletId2) {
		String portletTitle1 = PortalUtil.getPortletTitle(portletId1, _locale);
		String portletTitle2 = PortalUtil.getPortletTitle(portletId2, _locale);

		int value = _collator.compare(portletTitle1, portletTitle2);

		if (value != 0) {
			return value;
		}

		return _collator.compare(portletId1, portletId2);
	}

	private final Collator _collator;
	private final Locale _locale;

}