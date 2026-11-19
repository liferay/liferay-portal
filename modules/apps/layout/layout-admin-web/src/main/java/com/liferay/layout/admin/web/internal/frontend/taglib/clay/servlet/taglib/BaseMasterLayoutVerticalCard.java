/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.frontend.taglib.clay.servlet.taglib;

import com.liferay.frontend.taglib.clay.servlet.taglib.VerticalCard;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Georgel Pop
 */
public abstract class BaseMasterLayoutVerticalCard implements VerticalCard {

	public BaseMasterLayoutVerticalCard(
		LayoutPageTemplateEntry layoutPageTemplateEntry,
		RenderRequest renderRequest, RenderResponse renderResponse) {

		this.layoutPageTemplateEntry = layoutPageTemplateEntry;
		this.renderResponse = renderResponse;

		httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
		_themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	@Override
	public String getCssClass() {
		return "add-layout-action-option card-interactive " +
			"card-interactive-primary";
	}

	@Override
	public Map<String, String> getDynamicAttributes() {
		Map<String, String> dynamicAttributes = new HashMap<>();

		try {
			dynamicAttributes.put("data-add-layout-url", getAddLayoutURL());
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		dynamicAttributes.put("role", "button");
		dynamicAttributes.put("tabIndex", "0");

		return dynamicAttributes;
	}

	@Override
	public String getIcon() {
		return "page";
	}

	@Override
	public String getImageAlt() {
		return StringPool.BLANK;
	}

	@Override
	public String getImageSrc() {
		return layoutPageTemplateEntry.getImagePreviewURL(_themeDisplay);
	}

	@Override
	public String getTitle() {
		return layoutPageTemplateEntry.getName();
	}

	@Override
	public boolean isSelectable() {
		return false;
	}

	protected abstract String getAddLayoutURL();

	protected final HttpServletRequest httpServletRequest;
	protected final LayoutPageTemplateEntry layoutPageTemplateEntry;
	protected final RenderResponse renderResponse;

	private static final Log _log = LogFactoryUtil.getLog(
		BaseMasterLayoutVerticalCard.class);

	private final ThemeDisplay _themeDisplay;

}