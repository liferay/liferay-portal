/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.item.selector.web.internal;

import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.item.selector.TableItemView;
import com.liferay.object.item.selector.ObjectDefinitionItemSelectorReturnType;
import com.liferay.object.item.selector.web.internal.display.context.ObjectDefinitionDisplayContext;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Jonathan McCann
 */
public class ObjectDefinitionItemSelectorViewDescriptor
	implements ItemSelectorViewDescriptor<ObjectDefinition> {

	public ObjectDefinitionItemSelectorViewDescriptor(
		HttpServletRequest httpServletRequest,
		ObjectDefinitionDisplayContext objectDefinitionDisplayContext) {

		_httpServletRequest = httpServletRequest;
		_objectDefinitionDisplayContext = objectDefinitionDisplayContext;
	}

	@Override
	public String getDefaultDisplayStyle() {
		return "list";
	}

	@Override
	public String[] getDisplayViews() {
		return new String[0];
	}

	@Override
	public ItemDescriptor getItemDescriptor(ObjectDefinition objectDefinition) {
		return new ObjectDefinitionItemDescriptor(
			_httpServletRequest, objectDefinition);
	}

	@Override
	public ItemSelectorReturnType getItemSelectorReturnType() {
		return new ObjectDefinitionItemSelectorReturnType();
	}

	@Override
	public String[] getOrderByKeys() {
		return new String[] {"label", "modified-date"};
	}

	@Override
	public SearchContainer<ObjectDefinition> getSearchContainer()
		throws PortalException {

		return _objectDefinitionDisplayContext.
			getObjectDefinitionSearchContainer();
	}

	@Override
	public TableItemView getTableItemView(ObjectDefinition objectDefinition) {
		return new ObjectDefinitionTableItemView(objectDefinition);
	}

	@Override
	public boolean isShowBreadcrumb() {
		return false;
	}

	@Override
	public boolean isShowSearch() {
		return false;
	}

	private final HttpServletRequest _httpServletRequest;
	private final ObjectDefinitionDisplayContext
		_objectDefinitionDisplayContext;

}