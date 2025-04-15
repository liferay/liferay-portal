/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.display.page.item.selector.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.SearchContainerManagementToolbarDisplayContext;
import com.liferay.layout.page.template.constants.LayoutPageTemplateConstants;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Yurena Cabrera
 */
public class
	AssetDisplayPagesItemSelectorCustomViewManagementToolbarDisplayContext
		extends SearchContainerManagementToolbarDisplayContext {

	public AssetDisplayPagesItemSelectorCustomViewManagementToolbarDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		AssetDisplayPagesItemSelectorCustomViewDisplayContext
			assetDisplayPagesItemSelectorCustomViewDisplayContext) {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			assetDisplayPagesItemSelectorCustomViewDisplayContext.
				getAssetDisplayPageSearchContainer());

		_assetDisplayPagesItemSelectorCustomViewDisplayContext =
			assetDisplayPagesItemSelectorCustomViewDisplayContext;
	}

	@Override
	public String getClearResultsURL() {
		return PortletURLBuilder.create(
			getPortletURL()
		).setKeywords(
			StringPool.BLANK
		).setParameter(
			"layoutPageTemplateCollectionId",
			LayoutPageTemplateConstants.
				PARENT_LAYOUT_PAGE_TEMPLATE_COLLECTION_ID_DEFAULT
		).buildString();
	}

	@Override
	public String getSearchActionURL() {
		return PortletURLBuilder.create(
			getPortletURL()
		).setParameter(
			"layoutPageTemplateCollectionId", -1
		).buildString();
	}

	@Override
	public String getSearchContainerId() {
		long layoutPageTemplateCollectionId =
			_assetDisplayPagesItemSelectorCustomViewDisplayContext.
				getLayoutPageTemplateCollectionId();

		return "displayPages" + layoutPageTemplateCollectionId;
	}

	@Override
	public Boolean isSelectable() {
		return false;
	}

	@Override
	protected String[] getOrderByKeys() {
		return new String[] {"create-date", "modified-date", "name"};
	}

	private final AssetDisplayPagesItemSelectorCustomViewDisplayContext
		_assetDisplayPagesItemSelectorCustomViewDisplayContext;

}