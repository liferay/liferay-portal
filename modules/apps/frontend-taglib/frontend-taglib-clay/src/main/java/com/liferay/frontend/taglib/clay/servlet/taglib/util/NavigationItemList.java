/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.clay.servlet.taglib.util;

import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeSupplier;

import java.util.ArrayList;

/**
 * @author Brian Wing Shun Chan
 */
public class NavigationItemList extends ArrayList<NavigationItem> {

	public static NavigationItemList of(NavigationItem... navigationItems) {
		NavigationItemList navigationItemList = new NavigationItemList();

		for (NavigationItem navigationItem : navigationItems) {
			if (navigationItem == null) {
				continue;
			}

			navigationItemList.add(navigationItem);
		}

		return navigationItemList;
	}

	public static NavigationItemList of(
		UnsafeSupplier<NavigationItem, Exception>... unsafeSuppliers) {

		NavigationItemList navigationItemList = new NavigationItemList();

		for (UnsafeSupplier<NavigationItem, Exception> unsafeSupplier :
				unsafeSuppliers) {

			try {
				NavigationItem navigationItem = unsafeSupplier.get();

				if (navigationItem != null) {
					navigationItemList.add(navigationItem);
				}
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		return navigationItemList;
	}

	public void add(UnsafeConsumer<NavigationItem, Exception> unsafeConsumer) {
		NavigationItem navigationItem = new NavigationItem();

		try {
			unsafeConsumer.accept(navigationItem);
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}

		add(navigationItem);
	}

}