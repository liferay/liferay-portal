/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.item.selector.editor.configuration.internal;

import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.ItemSelectorCriterion;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.criteria.URLItemSelectorReturnType;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.util.GetterUtil;

import jakarta.portlet.PortletURL;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Sergio González
 */
public abstract class BaseEditorConfigContributor
	extends com.liferay.portal.kernel.editor.configuration.
				BaseEditorConfigContributor {

	public PortletURL getItemSelectorPortletURL(
		Map<String, Object> inputEditorTaglibAttributes,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory,
		ItemSelectorCriterion... itemSelectorCriteria) {

		ItemSelector itemSelector = getItemSelector();

		String name = GetterUtil.getString(
			inputEditorTaglibAttributes.get("liferay-ui:input-editor:name"));

		boolean inlineEdit = GetterUtil.getBoolean(
			inputEditorTaglibAttributes.get(
				"liferay-ui:input-editor:inlineEdit"));

		if (!inlineEdit) {
			String namespace = GetterUtil.getString(
				inputEditorTaglibAttributes.get(
					"liferay-ui:input-editor:namespace"));

			name = namespace + name;
		}

		for (ItemSelectorCriterion itemSelectorCriterion :
				itemSelectorCriteria) {

			itemSelectorCriterion.setDesiredItemSelectorReturnTypes(
				getDesiredItemSelectorReturnTypes());
		}

		return itemSelector.getItemSelectorURL(
			requestBackedPortletURLFactory, name + "selectItem",
			itemSelectorCriteria);
	}

	protected List<ItemSelectorReturnType> getDesiredItemSelectorReturnTypes() {
		return Arrays.asList(new URLItemSelectorReturnType());
	}

	protected abstract ItemSelector getItemSelector();

}