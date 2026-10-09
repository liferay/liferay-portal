/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.model;

import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeSupplier;

import java.util.ArrayList;

/**
 * @author Luca Pellizzon
 */
public class FDSSortItemList extends ArrayList<FDSSortItem> {

	public static FDSSortItemList of(FDSSortItem... fdsSortItems) {
		FDSSortItemList fdsSortItemList = new FDSSortItemList();

		for (FDSSortItem fdsSortItem : fdsSortItems) {
			if (fdsSortItem == null) {
				continue;
			}

			fdsSortItemList.add(fdsSortItem);
		}

		return fdsSortItemList;
	}

	public static FDSSortItemList of(
		UnsafeSupplier<FDSSortItem, Exception>... unsafeSuppliers) {

		FDSSortItemList fdsSortItemList = new FDSSortItemList();

		for (UnsafeSupplier<FDSSortItem, Exception> unsafeSupplier :
				unsafeSuppliers) {

			try {
				FDSSortItem fdsSortItem = unsafeSupplier.get();

				if (fdsSortItem != null) {
					fdsSortItemList.add(fdsSortItem);
				}
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		return fdsSortItemList;
	}

	public void add(UnsafeConsumer<FDSSortItem, Exception> unsafeConsumer) {
		FDSSortItem fdsSortItem = new FDSSortItem();

		try {
			unsafeConsumer.accept(fdsSortItem);
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}

		add(fdsSortItem);
	}

}