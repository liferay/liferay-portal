/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.display.page.item.selector.web.internal.frontend.taglib.clay.servlet.taglib;

import com.liferay.frontend.taglib.clay.servlet.taglib.VerticalCard;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Date;

/**
 * @author Eudaldo Alonso
 */
public class LayoutPageTemplateEntryVerticalCard implements VerticalCard {

	public LayoutPageTemplateEntryVerticalCard(
		LayoutPageTemplateEntry layoutPageTemplateEntry,
		RenderRequest renderRequest) {

		_layoutPageTemplateEntry = layoutPageTemplateEntry;

		_httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
	}

	@Override
	public String getCssClass() {
		return "card-interactive card-interactive-secondary " +
			"layout-page-template-entry";
	}

	@Override
	public String getIcon() {
		return "page";
	}

	@Override
	public String getStickerIcon() {
		if (_layoutPageTemplateEntry.isDefaultTemplate()) {
			return "check-circle";
		}

		return null;
	}

	@Override
	public String getStickerStyle() {
		return "primary";
	}

	@Override
	public String getSubtitle() {
		Date createDate = _layoutPageTemplateEntry.getCreateDate();

		String createDateDescription = LanguageUtil.getTimeDescription(
			_httpServletRequest,
			System.currentTimeMillis() - createDate.getTime(), true);

		return LanguageUtil.format(
			_httpServletRequest, "x-ago", createDateDescription);
	}

	@Override
	public String getTitle() {
		return _layoutPageTemplateEntry.getName();
	}

	@Override
	public boolean isSelectable() {
		return false;
	}

	private final HttpServletRequest _httpServletRequest;
	private final LayoutPageTemplateEntry _layoutPageTemplateEntry;

}