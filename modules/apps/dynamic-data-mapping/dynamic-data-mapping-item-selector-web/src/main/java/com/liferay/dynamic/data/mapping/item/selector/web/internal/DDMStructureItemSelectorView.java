/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.item.selector.web.internal;

import com.liferay.dynamic.data.mapping.item.selector.DDMStructureItemSelectorCriterion;
import com.liferay.dynamic.data.mapping.item.selector.DDMStructureItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.ItemSelectorViewDescriptorRenderer;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.service.GroupLocalService;

import jakarta.portlet.PortletURL;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = ItemSelectorView.class)
public class DDMStructureItemSelectorView
	implements ItemSelectorView<DDMStructureItemSelectorCriterion> {

	@Override
	public Class<DDMStructureItemSelectorCriterion>
		getItemSelectorCriterionClass() {

		return DDMStructureItemSelectorCriterion.class;
	}

	@Override
	public List<ItemSelectorReturnType> getSupportedItemSelectorReturnTypes() {
		return _supportedItemSelectorReturnTypes;
	}

	@Override
	public String getTitle(Locale locale) {
		return _language.get(locale, "structures");
	}

	@Override
	public void renderHTML(
			ServletRequest servletRequest, ServletResponse servletResponse,
			DDMStructureItemSelectorCriterion ddmStructureItemSelectorCriterion,
			PortletURL portletURL, String itemSelectedEventName, boolean search)
		throws IOException, ServletException {

		_itemSelectorViewDescriptorRenderer.renderHTML(
			servletRequest, servletResponse, ddmStructureItemSelectorCriterion,
			portletURL, itemSelectedEventName, search,
			new DDMStructureItemSelectorViewDescriptor(
				ddmStructureItemSelectorCriterion, _groupLocalService,
				(HttpServletRequest)servletRequest, portletURL));
	}

	private static final List<ItemSelectorReturnType>
		_supportedItemSelectorReturnTypes = Collections.singletonList(
			new DDMStructureItemSelectorReturnType());

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ItemSelectorViewDescriptorRenderer
		<DDMStructureItemSelectorCriterion> _itemSelectorViewDescriptorRenderer;

	@Reference
	private Language _language;

}