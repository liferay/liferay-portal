/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.clay.servlet.taglib.util;

import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeSupplier;

import java.util.ArrayList;

/**
 * @author Luca Pellizzon
 */
public class SortItemList extends ArrayList<SortItem> {

	public static SortItemList of(SortItem... sortItems) {
		SortItemList sortItemList = new SortItemList();

		for (SortItem sortItem : sortItems) {
			if (sortItem == null) {
				continue;
			}

			sortItemList.add(sortItem);
		}

		return sortItemList;
	}

	public static SortItemList of(
		UnsafeSupplier<SortItem, Exception>... unsafeSuppliers) {

		SortItemList sortItemList = new SortItemList();

		for (UnsafeSupplier<SortItem, Exception> unsafeSupplier :
				unsafeSuppliers) {

			try {
				SortItem sortItem = unsafeSupplier.get();

				if (sortItem != null) {
					sortItemList.add(sortItem);
				}
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		return sortItemList;
	}

	public void add(UnsafeConsumer<SortItem, Exception> unsafeConsumer) {
		SortItem sortItem = new SortItem();

		try {
			unsafeConsumer.accept(sortItem);
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}

		add(sortItem);
	}

}