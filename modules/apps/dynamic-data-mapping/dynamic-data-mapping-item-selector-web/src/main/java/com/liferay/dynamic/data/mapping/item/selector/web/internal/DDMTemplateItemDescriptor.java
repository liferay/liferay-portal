/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.item.selector.web.internal;

import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Date;
import java.util.Locale;

/**
 * @author Eudaldo Alonso
 */
public class DDMTemplateItemDescriptor
	implements ItemSelectorViewDescriptor.ItemDescriptor {

	public DDMTemplateItemDescriptor(
		DDMTemplate ddmTemplate, HttpServletRequest httpServletRequest) {

		_ddmTemplate = ddmTemplate;
		_httpServletRequest = httpServletRequest;
	}

	@Override
	public String getIcon() {
		return "page-template";
	}

	@Override
	public String getImageURL() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return HtmlUtil.escapeAttribute(
			_ddmTemplate.getTemplateImageURL(themeDisplay));
	}

	@Override
	public Date getModifiedDate() {
		return _ddmTemplate.getModifiedDate();
	}

	@Override
	public String getPayload() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return JSONUtil.put(
			"ddmtemplateid", _ddmTemplate.getTemplateId()
		).put(
			"ddmtemplatekey", _ddmTemplate.getTemplateKey()
		).put(
			"description", _ddmTemplate.getDescription(themeDisplay.getLocale())
		).put(
			"imageurl", _ddmTemplate.getTemplateImageURL(themeDisplay)
		).put(
			"name", _ddmTemplate.getName(themeDisplay.getLocale())
		).toString();
	}

	@Override
	public String getSubtitle(Locale locale) {
		return _ddmTemplate.getDescription(locale);
	}

	@Override
	public String getTitle(Locale locale) {
		return _ddmTemplate.getName(locale);
	}

	@Override
	public long getUserId() {
		return _ddmTemplate.getUserId();
	}

	@Override
	public String getUserName() {
		return _ddmTemplate.getUserName();
	}

	private final DDMTemplate _ddmTemplate;
	private final HttpServletRequest _httpServletRequest;

}