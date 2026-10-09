/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.clay.servlet.taglib.util;

import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeSupplier;

import java.util.ArrayList;

/**
 * @author Eduardo Allegrini
 */
public class VerticalNavItemList extends ArrayList<VerticalNavItem> {

	public static VerticalNavItemList of(
		UnsafeSupplier<VerticalNavItem, Exception>... unsafeSuppliers) {

		VerticalNavItemList verticalNavItemList = new VerticalNavItemList();

		for (UnsafeSupplier<VerticalNavItem, Exception> unsafeSupplier :
				unsafeSuppliers) {

			try {
				VerticalNavItem verticalNavItem = unsafeSupplier.get();

				if (verticalNavItem != null) {
					verticalNavItemList.add(verticalNavItem);
				}
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		return verticalNavItemList;
	}

	public static VerticalNavItemList of(VerticalNavItem... verticalNavItems) {
		VerticalNavItemList verticalNavItemList = new VerticalNavItemList();

		for (VerticalNavItem verticalNavItem : verticalNavItems) {
			if (verticalNavItem == null) {
				continue;
			}

			verticalNavItemList.add(verticalNavItem);
		}

		return verticalNavItemList;
	}

	public void add(UnsafeConsumer<VerticalNavItem, Exception> unsafeConsumer) {
		VerticalNavItem verticalNavItem = new VerticalNavItem();

		try {
			unsafeConsumer.accept(verticalNavItem);
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}

		add(verticalNavItem);
	}

}