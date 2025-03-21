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
import com.liferay.segments.model.SegmentsExperience;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Date;
import java.util.Locale;

/**
 * @author Eudaldo Alonso
 */
public class SegmentsExperienceItemDescriptor
	implements ItemSelectorViewDescriptor.ItemDescriptor {

	public SegmentsExperienceItemDescriptor(
		HttpServletRequest httpServletRequest,
		SegmentsExperience segmentsExperience) {

		_httpServletRequest = httpServletRequest;
		_segmentsExperience = segmentsExperience;
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
	public Date getModifiedDate() {
		return _segmentsExperience.getModifiedDate();
	}

	@Override
	public String getPayload() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return JSONUtil.put(
			"name", _segmentsExperience.getName(themeDisplay.getLocale())
		).put(
			"segmentsExperienceId",
			_segmentsExperience.getSegmentsExperienceId()
		).toString();
	}

	@Override
	public String getSubtitle(Locale locale) {
		return StringPool.BLANK;
	}

	@Override
	public String getTitle(Locale locale) {
		return _segmentsExperience.getName(locale);
	}

	@Override
	public long getUserId() {
		return _segmentsExperience.getUserId();
	}

	@Override
	public String getUserName() {
		return _segmentsExperience.getUserName();
	}

	private final HttpServletRequest _httpServletRequest;
	private final SegmentsExperience _segmentsExperience;

}