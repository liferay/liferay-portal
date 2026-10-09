/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.video.internal.util;

import com.liferay.document.library.display.context.DLUIItemKeys;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.SetUtil;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * @author Iván Zaera
 * @author Alejandro Tardín
 */
public class DLVideoExternalShortcutUIItemsUtil {

	public static void processDropdownItems(List<DropdownItem> dropdownItems) {
		_removeUIItems(
			dropdownItems, dropdownItem -> (String)dropdownItem.get("key"),
			SetUtil.fromArray(
				DLUIItemKeys.CANCEL_CHECKOUT, DLUIItemKeys.CHECKIN,
				DLUIItemKeys.CHECKOUT, DLUIItemKeys.DOWNLOAD,
				DLUIItemKeys.OPEN_IN_MS_OFFICE));
	}

	private static <T> void _removeUIItems(
		List<T> items, Function<T, String> function, Set<String> keys) {

		if (ListUtil.isEmpty(items)) {
			return;
		}

		Iterator<T> iterator = items.iterator();

		while (iterator.hasNext()) {
			T item = iterator.next();

			if (!keys.contains(function.apply(item))) {
				continue;
			}

			iterator.remove();
		}
	}

}