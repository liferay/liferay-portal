/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.display.page.item.selector.web.internal.item.selector;

import com.liferay.asset.display.page.item.selector.web.internal.frontend.taglib.clay.servlet.taglib.LayoutPageTemplateEntryVerticalCard;
import com.liferay.frontend.taglib.clay.servlet.taglib.VerticalCard;
import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.json.JSONUtil;

import jakarta.portlet.RenderRequest;

import java.util.Locale;

/**
 * @author Eudaldo Alonso
 */
public class AssetDisplayPageItemDescriptor
	implements ItemSelectorViewDescriptor.ItemDescriptor {

	public AssetDisplayPageItemDescriptor(
		LayoutPageTemplateEntry layoutPageTemplateEntry) {

		_layoutPageTemplateEntry = layoutPageTemplateEntry;
	}

	@Override
	public String getIcon() {
		return null;
	}

	@Override
	public String getImageURL() {
		return null;
	}

	@Override
	public String getPayload() {
		return JSONUtil.put(
			"id", _layoutPageTemplateEntry.getLayoutPageTemplateEntryId()
		).put(
			"name", _layoutPageTemplateEntry.getName()
		).put(
			"plid", _layoutPageTemplateEntry.getPlid()
		).put(
			"type", "asset-display-page"
		).put(
			"uuid", _layoutPageTemplateEntry.getUuid()
		).toString();
	}

	@Override
	public String getSubtitle(Locale locale) {
		return StringPool.BLANK;
	}

	@Override
	public String getTitle(Locale locale) {
		return StringPool.BLANK;
	}

	@Override
	public VerticalCard getVerticalCard(
		RenderRequest renderRequest, RowChecker rowChecker) {

		return new LayoutPageTemplateEntryVerticalCard(
			_layoutPageTemplateEntry, renderRequest);
	}

	private final LayoutPageTemplateEntry _layoutPageTemplateEntry;

}