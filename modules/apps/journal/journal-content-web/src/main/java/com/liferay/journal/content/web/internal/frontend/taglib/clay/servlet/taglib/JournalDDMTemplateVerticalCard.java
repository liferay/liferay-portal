/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.content.web.internal.frontend.taglib.clay.servlet.taglib;

import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.frontend.taglib.clay.servlet.taglib.VerticalCard;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Date;

/**
 * @author Eudaldo Alonso
 */
public class JournalDDMTemplateVerticalCard implements VerticalCard {

	public JournalDDMTemplateVerticalCard(
		DDMTemplate ddmTemplate, HttpServletRequest httpServletRequest) {

		_ddmTemplate = ddmTemplate;
		_httpServletRequest = httpServletRequest;
	}

	@Override
	public String getIcon() {
		return "page-template";
	}

	@Override
	public String getImageSrc() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return HtmlUtil.escapeAttribute(
			_ddmTemplate.getTemplateImageURL(themeDisplay));
	}

	@Override
	public String getSubtitle() {
		Date createDate = _ddmTemplate.getModifiedDate();

		String modifiedDateDescription = LanguageUtil.getTimeDescription(
			_httpServletRequest,
			System.currentTimeMillis() - createDate.getTime(), true);

		return LanguageUtil.format(
			_httpServletRequest, "modified-x-ago", modifiedDateDescription);
	}

	@Override
	public String getTitle() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return _ddmTemplate.getName(themeDisplay.getLocale());
	}

	@Override
	public boolean isSelectable() {
		return false;
	}

	private final DDMTemplate _ddmTemplate;
	private final HttpServletRequest _httpServletRequest;

}