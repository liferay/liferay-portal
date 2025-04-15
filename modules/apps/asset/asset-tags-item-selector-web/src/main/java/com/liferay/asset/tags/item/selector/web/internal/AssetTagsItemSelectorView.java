/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.tags.item.selector.web.internal;

import com.liferay.asset.kernel.service.AssetTagService;
import com.liferay.asset.tags.item.selector.AssetTagsItemSelectorCriterion;
import com.liferay.asset.tags.item.selector.AssetTagsItemSelectorReturnType;
import com.liferay.asset.tags.item.selector.web.internal.display.context.AssetTagsDisplayContext;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.ItemSelectorViewDescriptorRenderer;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.JavaConstants;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

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
 * @author Stefan Tanasie
 */
@Component(service = ItemSelectorView.class)
public class AssetTagsItemSelectorView
	implements ItemSelectorView<AssetTagsItemSelectorCriterion> {

	@Override
	public Class<? extends AssetTagsItemSelectorCriterion>
		getItemSelectorCriterionClass() {

		return AssetTagsItemSelectorCriterion.class;
	}

	@Override
	public List<ItemSelectorReturnType> getSupportedItemSelectorReturnTypes() {
		return _supportedItemSelectorReturnTypes;
	}

	@Override
	public String getTitle(Locale locale) {
		return _language.get(locale, "tags");
	}

	@Override
	public void renderHTML(
			ServletRequest servletRequest, ServletResponse servletResponse,
			AssetTagsItemSelectorCriterion assetTagsItemSelectorCriterion,
			PortletURL portletURL, String itemSelectedEventName, boolean search)
		throws IOException, ServletException {

		HttpServletRequest httpServletRequest =
			(HttpServletRequest)servletRequest;

		RenderRequest renderRequest =
			(RenderRequest)httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_REQUEST);
		RenderResponse renderResponse =
			(RenderResponse)httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_RESPONSE);

		AssetTagsDisplayContext assetTagsDisplayContext =
			new AssetTagsDisplayContext(
				assetTagsItemSelectorCriterion, _assetTagService,
				_groupLocalService, httpServletRequest, portletURL,
				renderRequest, renderResponse);

		_itemSelectorViewDescriptorRenderer.renderHTML(
			httpServletRequest, servletResponse, assetTagsItemSelectorCriterion,
			portletURL, itemSelectedEventName, search,
			new AssetTagsItemSelectorViewDescriptor(
				assetTagsItemSelectorCriterion, assetTagsDisplayContext));
	}

	private static final List<ItemSelectorReturnType>
		_supportedItemSelectorReturnTypes = Collections.singletonList(
			new AssetTagsItemSelectorReturnType());

	@Reference
	private AssetTagService _assetTagService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ItemSelectorViewDescriptorRenderer<AssetTagsItemSelectorCriterion>
		_itemSelectorViewDescriptorRenderer;

	@Reference
	private Language _language;

}