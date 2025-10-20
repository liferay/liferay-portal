/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.item.selector.web.internal;

import com.liferay.frontend.taglib.clay.servlet.taglib.VerticalCard;
import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.style.book.item.selector.web.internal.frontend.taglib.clay.servlet.taglib.SelectStylebookLayoutVerticalCard;
import com.liferay.style.book.model.StyleBookEntry;

import jakarta.portlet.RenderRequest;

import java.util.Locale;

/**
 * @author Eudaldo Alonso
 */
public class StyleBookEntryItemDescriptor
	implements ItemSelectorViewDescriptor.ItemDescriptor {

	public StyleBookEntryItemDescriptor(
		Layout selLayout, StyleBookEntry styleBookEntry) {

		_selLayout = selLayout;
		_styleBookEntry = styleBookEntry;
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
			"externalReferenceCode", _styleBookEntry.getExternalReferenceCode()
		).put(
			"name", _styleBookEntry.getName()
		).put(
			"styleBookEntryId", _styleBookEntry.getStyleBookEntryId()
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

		return new SelectStylebookLayoutVerticalCard(
			renderRequest, _selLayout, _styleBookEntry);
	}

	private final Layout _selLayout;
	private final StyleBookEntry _styleBookEntry;

}