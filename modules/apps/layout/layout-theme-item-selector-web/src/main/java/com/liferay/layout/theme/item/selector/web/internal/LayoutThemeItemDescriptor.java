/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.theme.item.selector.web.internal;

import com.liferay.frontend.taglib.clay.servlet.taglib.VerticalCard;
import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.layout.theme.item.selector.web.internal.frontend.taglib.clay.servlet.taglib.SelectThemeVerticalCard;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.plugin.PluginPackage;

import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

/**
 * @author Stefan Tanasie
 */
public class LayoutThemeItemDescriptor
	implements ItemSelectorViewDescriptor.ItemDescriptor {

	public LayoutThemeItemDescriptor(
		Theme theme, HttpServletRequest httpServletRequest) {

		_theme = theme;
		_httpServletRequest = httpServletRequest;
	}

	@Override
	public String getIcon() {
		return null;
	}

	@Override
	public String getImageURL() {
		return _theme.getStaticResourcePath() + _theme.getImagesPath() +
			"/thumbnail.png";
	}

	@Override
	public String getPayload() {
		return JSONUtil.put(
			"themeId", _theme.getThemeId()
		).toString();
	}

	@Override
	public String getSubtitle(Locale locale) {
		return LanguageUtil.format(
			_httpServletRequest, "by-x", _getPluginPackage().getAuthor());
	}

	@Override
	public String getTitle(Locale locale) {
		return _theme.getName();
	}

	@Override
	public VerticalCard getVerticalCard(
		RenderRequest renderRequest, RowChecker rowChecker) {

		return new SelectThemeVerticalCard(_theme, renderRequest);
	}

	private PluginPackage _getPluginPackage() {
		return _theme.getPluginPackage();
	}

	private final HttpServletRequest _httpServletRequest;
	private final Theme _theme;

}