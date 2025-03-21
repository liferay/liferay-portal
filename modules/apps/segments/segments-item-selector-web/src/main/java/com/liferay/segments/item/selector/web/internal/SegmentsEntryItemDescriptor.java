/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.item.selector.web.internal;

import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.segments.model.SegmentsEntry;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

/**
 * @author Stefan Tanasie
 */
public class SegmentsEntryItemDescriptor
	implements ItemSelectorViewDescriptor.ItemDescriptor {

	public SegmentsEntryItemDescriptor(
		SegmentsEntry segmentsEntry, HttpServletRequest httpServletRequest) {

		_segmentsEntry = segmentsEntry;
		_httpServletRequest = httpServletRequest;
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
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return JSONUtil.put(
			"segmentsEntryId",
			String.valueOf(_segmentsEntry.getSegmentsEntryId())
		).put(
			"segmentsEntryName",
			_segmentsEntry.getName(themeDisplay.getLocale())
		).toString();
	}

	@Override
	public String getSubtitle(Locale locale) {
		return StringPool.BLANK;
	}

	@Override
	public String getTitle(Locale locale) {
		return _segmentsEntry.getName(locale);
	}

	private final HttpServletRequest _httpServletRequest;
	private final SegmentsEntry _segmentsEntry;

}