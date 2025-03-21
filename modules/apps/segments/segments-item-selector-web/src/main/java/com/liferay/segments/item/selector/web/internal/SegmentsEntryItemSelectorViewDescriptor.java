/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.item.selector.web.internal;

import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.item.selector.TableItemView;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.segments.item.selector.SegmentsEntryItemSelectorReturnType;
import com.liferay.segments.item.selector.web.internal.display.context.SegmentsEntryDisplayContext;
import com.liferay.segments.model.SegmentsEntry;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Stefan Tanasie
 */
public class SegmentsEntryItemSelectorViewDescriptor
	implements ItemSelectorViewDescriptor<SegmentsEntry> {

	public SegmentsEntryItemSelectorViewDescriptor(
		HttpServletRequest httpServletRequest,
		SegmentsEntryDisplayContext segmentsEntryDisplayContext) {

		_httpServletRequest = httpServletRequest;
		_segmentsEntryDisplayContext = segmentsEntryDisplayContext;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
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
	public ItemDescriptor getItemDescriptor(SegmentsEntry segmentsEntry) {
		return new SegmentsEntryItemDescriptor(
			segmentsEntry, _httpServletRequest);
	}

	@Override
	public ItemSelectorReturnType getItemSelectorReturnType() {
		return new SegmentsEntryItemSelectorReturnType();
	}

	@Override
	public String[] getOrderByKeys() {
		return new String[] {"modified-date", "name"};
	}

	@Override
	public SearchContainer<SegmentsEntry> getSearchContainer()
		throws PortalException {

		return _segmentsEntryDisplayContext.getSegmentEntrySearchContainer();
	}

	@Override
	public TableItemView getTableItemView(SegmentsEntry segmentsEntry) {
		return new SegmentsEntryTableItemView(segmentsEntry, _themeDisplay);
	}

	@Override
	public boolean isShowBreadcrumb() {
		return false;
	}

	@Override
	public boolean isShowSearch() {
		return true;
	}

	private final HttpServletRequest _httpServletRequest;
	private final SegmentsEntryDisplayContext _segmentsEntryDisplayContext;
	private final ThemeDisplay _themeDisplay;

}