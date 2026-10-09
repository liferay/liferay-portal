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
public class DropdownItemList extends ArrayList<DropdownItem> {

	public static DropdownItemList of(DropdownItem... dropdownItems) {
		DropdownItemList dropdownItemList = new DropdownItemList();

		for (DropdownItem dropdownItem : dropdownItems) {
			if (dropdownItem == null) {
				continue;
			}

			dropdownItemList.add(dropdownItem);
		}

		return dropdownItemList;
	}

	public static DropdownItemList of(
		UnsafeSupplier<DropdownItem, Exception>... unsafeSuppliers) {

		DropdownItemList dropdownItemList = new DropdownItemList();

		for (UnsafeSupplier<DropdownItem, Exception> unsafeSupplier :
				unsafeSuppliers) {

			try {
				DropdownItem dropdownItem = unsafeSupplier.get();

				if (dropdownItem != null) {
					dropdownItemList.add(dropdownItem);
				}
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		return dropdownItemList;
	}

	public void add(UnsafeConsumer<DropdownItem, Exception> unsafeConsumer) {
		if (unsafeConsumer == null) {
			return;
		}

		DropdownItem dropdownItem = new DropdownItem();

		try {
			unsafeConsumer.accept(dropdownItem);
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}

		add(dropdownItem);
	}

	public void addCheckbox(
		UnsafeConsumer<DropdownCheckboxItem, Exception> unsafeConsumer) {

		DropdownCheckboxItem dropdownCheckboxItem = new DropdownCheckboxItem();

		try {
			unsafeConsumer.accept(dropdownCheckboxItem);
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}

		add(dropdownCheckboxItem);
	}

	public void addContext(
		UnsafeConsumer<DropdownContextItem, Exception> unsafeConsumer) {

		DropdownContextItem dropdownContextItem = new DropdownContextItem();

		try {
			unsafeConsumer.accept(dropdownContextItem);
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}

		add(dropdownContextItem);
	}

	public void addGroup(
		UnsafeConsumer<DropdownGroupItem, Exception> unsafeConsumer) {

		DropdownGroupItem dropdownGroupItem = new DropdownGroupItem();

		try {
			unsafeConsumer.accept(dropdownGroupItem);
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}

		add(dropdownGroupItem);
	}

	public void addRadio(
		UnsafeConsumer<DropdownRadioItem, Exception> unsafeConsumer) {

		DropdownRadioItem dropdownRadioItem = new DropdownRadioItem();

		try {
			unsafeConsumer.accept(dropdownRadioItem);
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}

		add(dropdownRadioItem);
	}

	public void addRadioGroup(
		UnsafeConsumer<DropdownRadioGroupItem, Exception> unsafeConsumer) {

		DropdownRadioGroupItem dropdownRadioGroupItem =
			new DropdownRadioGroupItem();

		try {
			unsafeConsumer.accept(dropdownRadioGroupItem);
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}

		add(dropdownRadioGroupItem);
	}

}