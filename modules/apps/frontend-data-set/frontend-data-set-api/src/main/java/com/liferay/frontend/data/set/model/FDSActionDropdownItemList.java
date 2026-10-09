/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.model;

import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeSupplier;

import java.util.ArrayList;

/**
 * @author Daniel Sanz
 */
public class FDSActionDropdownItemList
	extends ArrayList<FDSActionDropdownItem> {

	public static FDSActionDropdownItemList of(
		FDSActionDropdownItem... fdsActionDropdownItems) {

		FDSActionDropdownItemList fdsActionDropdownItemList =
			new FDSActionDropdownItemList();

		for (FDSActionDropdownItem fdsActionDropdownItem :
				fdsActionDropdownItems) {

			if (fdsActionDropdownItem == null) {
				continue;
			}

			fdsActionDropdownItemList.add(fdsActionDropdownItem);
		}

		return fdsActionDropdownItemList;
	}

	public static FDSActionDropdownItemList of(
		UnsafeSupplier<FDSActionDropdownItem, Exception>... unsafeSuppliers) {

		FDSActionDropdownItemList fdsActionDropdownItemList =
			new FDSActionDropdownItemList();

		for (UnsafeSupplier<FDSActionDropdownItem, Exception> unsafeSupplier :
				unsafeSuppliers) {

			try {
				FDSActionDropdownItem fdsActionDropdownItem =
					unsafeSupplier.get();

				if (fdsActionDropdownItem != null) {
					fdsActionDropdownItemList.add(fdsActionDropdownItem);
				}
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		return fdsActionDropdownItemList;
	}

	public void add(
		UnsafeConsumer<FDSActionDropdownItem, Exception> unsafeConsumer) {

		if (unsafeConsumer == null) {
			return;
		}

		FDSActionDropdownItem fdsActionDropdownItem =
			new FDSActionDropdownItem();

		try {
			unsafeConsumer.accept(fdsActionDropdownItem);
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}

		add(fdsActionDropdownItem);
	}

}