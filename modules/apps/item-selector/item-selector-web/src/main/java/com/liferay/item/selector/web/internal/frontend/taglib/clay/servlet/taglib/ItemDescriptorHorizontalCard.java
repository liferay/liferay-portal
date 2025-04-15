/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.item.selector.web.internal.frontend.taglib.clay.servlet.taglib;

import com.liferay.frontend.taglib.clay.servlet.taglib.BaseHorizontalCard;
import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.model.BaseModel;

import jakarta.portlet.RenderRequest;

/**
 * @author Alejandro Tardín
 */
public class ItemDescriptorHorizontalCard extends BaseHorizontalCard {

	public ItemDescriptorHorizontalCard(
		BaseModel<?> baseModel,
		ItemSelectorViewDescriptor.ItemDescriptor itemDescriptor,
		RenderRequest renderRequest, RowChecker rowChecker) {

		super(baseModel, renderRequest, rowChecker);

		_itemDescriptor = itemDescriptor;
	}

	@Override
	public String getElementClasses() {
		return "card-interactive card-interactive-secondary";
	}

	@Override
	public String getIcon() {
		return _itemDescriptor.getIcon();
	}

	@Override
	public String getInputValue() {
		return null;
	}

	@Override
	public String getTitle() {
		return _itemDescriptor.getTitle(themeDisplay.getLocale());
	}

	private final ItemSelectorViewDescriptor.ItemDescriptor _itemDescriptor;

}