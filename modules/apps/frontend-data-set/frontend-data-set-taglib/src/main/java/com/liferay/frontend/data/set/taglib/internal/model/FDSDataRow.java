/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.taglib.internal.model;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marco Leo
 */
public class FDSDataRow {

	public FDSDataRow(Object item) {
		_item = item;
	}

	public void addActionDropdownItems(List<DropdownItem> actionDropdownItems) {
		_actionDropdownItems.addAll(actionDropdownItems);
	}

	public List<DropdownItem> getActionDropdownItems() {
		return _actionDropdownItems;
	}

	@JsonUnwrapped
	public Object getItem() {
		return _item;
	}

	private final List<DropdownItem> _actionDropdownItems = new ArrayList<>();
	private final Object _item;

}