/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.theme.item.selector.web.internal.frontend.taglib.clay.servlet.taglib;

import com.liferay.frontend.taglib.clay.servlet.taglib.VerticalCard;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.plugin.PluginPackage;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * @author Eudaldo Alonso
 */
public class SelectThemeVerticalCard implements VerticalCard {

	public SelectThemeVerticalCard(Theme theme, RenderRequest renderRequest) {
		_theme = theme;

		_httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
	}

	@Override
	public String getCssClass() {
		return "card-interactive card-interactive-secondary selector-button";
	}

	@Override
	public Map<String, String> getDynamicAttributes() {
		return HashMapBuilder.put(
			"data-themeid", _theme.getThemeId()
		).put(
			"role", "button"
		).put(
			"tabIndex", "0"
		).build();
	}

	@Override
	public String getImageSrc() {
		return _theme.getStaticResourcePath() + _theme.getImagesPath() +
			"/thumbnail.png";
	}

	@Override
	public String getSubtitle() {
		String author = StringPool.DASH;

		PluginPackage selPluginPackage = _theme.getPluginPackage();

		if ((selPluginPackage != null) &&
			Validator.isNotNull(selPluginPackage.getAuthor())) {

			author = LanguageUtil.format(
				_httpServletRequest, "by-x", selPluginPackage.getAuthor());
		}

		return author;
	}

	@Override
	public String getTitle() {
		return _theme.getName();
	}

	@Override
	public boolean isSelectable() {
		return false;
	}

	private final HttpServletRequest _httpServletRequest;
	private final Theme _theme;

}