/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.collection.item.selector.web.internal;

import com.liferay.fragment.collection.item.selector.FragmentCollectionItemSelectorCriterion;
import com.liferay.fragment.collection.item.selector.FragmentCollectionItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.ItemSelectorViewDescriptorRenderer;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

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
 * @author Rubén Pulido
 */
@Component(
	property = "item.selector.view.order:Integer=200",
	service = ItemSelectorView.class
)
public class FragmentCollectionGlobalItemSelectorView
	implements ItemSelectorView<FragmentCollectionItemSelectorCriterion> {

	@Override
	public Class<FragmentCollectionItemSelectorCriterion>
		getItemSelectorCriterionClass() {

		return FragmentCollectionItemSelectorCriterion.class;
	}

	@Override
	public List<ItemSelectorReturnType> getSupportedItemSelectorReturnTypes() {
		return _supportedItemSelectorReturnTypes;
	}

	@Override
	public String getTitle(Locale locale) {
		return _language.get(locale, "global");
	}

	@Override
	public void renderHTML(
			ServletRequest servletRequest, ServletResponse servletResponse,
			FragmentCollectionItemSelectorCriterion
				fragmentCollectionItemSelectorCriterion,
			PortletURL portletURL, String itemSelectedEventName, boolean search)
		throws IOException, ServletException {

		ThemeDisplay themeDisplay = (ThemeDisplay)servletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		fragmentCollectionItemSelectorCriterion.setGroupId(
			themeDisplay.getCompanyGroupId());

		_itemSelectorViewDescriptorRenderer.renderHTML(
			servletRequest, servletResponse,
			fragmentCollectionItemSelectorCriterion, portletURL,
			itemSelectedEventName, search,
			new FragmentCollectionItemSelectorViewDescriptor(
				fragmentCollectionItemSelectorCriterion,
				themeDisplay.getCompanyGroupId(),
				(HttpServletRequest)servletRequest, portletURL));
	}

	private static final List<ItemSelectorReturnType>
		_supportedItemSelectorReturnTypes = Collections.singletonList(
			new FragmentCollectionItemSelectorReturnType());

	@Reference
	private ItemSelectorViewDescriptorRenderer
		<FragmentCollectionItemSelectorCriterion>
			_itemSelectorViewDescriptorRenderer;

	@Reference
	private Language _language;

}