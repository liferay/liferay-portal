/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.clay.servlet.taglib.util;

import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeSupplier;

import java.util.ArrayList;

/**
 * @author Carlos Lancha
 */
public class TabsItemList extends ArrayList<TabsItem> {

	public static TabsItemList of(TabsItem... tabsItems) {
		TabsItemList tabsItemList = new TabsItemList();

		for (TabsItem tabsItem : tabsItems) {
			if (tabsItem == null) {
				continue;
			}

			tabsItemList.add(tabsItem);
		}

		return tabsItemList;
	}

	public static TabsItemList of(
		UnsafeSupplier<TabsItem, Exception>... unsafeSuppliers) {

		TabsItemList tabsItemList = new TabsItemList();

		for (UnsafeSupplier<TabsItem, Exception> unsafeSupplier :
				unsafeSuppliers) {

			try {
				TabsItem tabsItem = unsafeSupplier.get();

				if (tabsItem != null) {
					tabsItemList.add(tabsItem);
				}
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		return tabsItemList;
	}

	public void add(UnsafeConsumer<TabsItem, Exception> unsafeConsumer) {
		TabsItem tabsItem = new TabsItem();

		try {
			unsafeConsumer.accept(tabsItem);
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}

		add(tabsItem);
	}

}