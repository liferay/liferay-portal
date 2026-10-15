/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.frontend.taglib.clay.servlet.taglib;

import com.liferay.frontend.taglib.clay.servlet.taglib.VerticalCard;
import com.liferay.layout.admin.web.internal.util.MasterLayoutUtil;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jürgen Kappler
 */
public class SelectLayoutMasterLayoutVerticalCard implements VerticalCard {

	public SelectLayoutMasterLayoutVerticalCard(
		LayoutPageTemplateEntry layoutPageTemplateEntry,
		RenderRequest renderRequest, RenderResponse renderResponse) {

		_layoutPageTemplateEntry = layoutPageTemplateEntry;
		_renderResponse = renderResponse;

		_httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);

		_themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_designLibraryMasterLayout = Validator.isNotNull(
			MasterLayoutUtil.getMasterLayoutPageTemplateEntryScopeERC(
				_themeDisplay.getScopeGroupId(), layoutPageTemplateEntry));
	}

	@Override
	public String getCssClass() {
		String cssClass =
			"add-layout-action-option card-interactive " +
				"card-interactive-primary";

		if (_designLibraryMasterLayout) {
			cssClass += " design-library-master-layout";
		}

		return cssClass;
	}

	@Override
	public Map<String, String> getDynamicAttributes() {
		Map<String, String> data = new HashMap<>();

		try {
			data.put(
				"data-add-layout-url",
				PortletURLBuilder.createRenderURL(
					_renderResponse
				).setMVCRenderCommandName(
					"/layout_admin/add_layout"
				).setRedirect(
					ParamUtil.getString(_httpServletRequest, "redirect")
				).setParameter(
					"groupId", ParamUtil.getLong(_httpServletRequest, "groupId")
				).setParameter(
					"masterLayoutPlid", _layoutPageTemplateEntry.getPlid()
				).setParameter(
					"privateLayout",
					ParamUtil.getBoolean(_httpServletRequest, "privateLayout")
				).setParameter(
					"selPlid", ParamUtil.getLong(_httpServletRequest, "selPlid")
				).setWindowState(
					LiferayWindowState.POP_UP
				).buildString());
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		data.put("role", "button");
		data.put("tabIndex", "0");

		return data;
	}

	@Override
	public String getIcon() {
		return "page";
	}

	@Override
	public String getImageSrc() {
		return _layoutPageTemplateEntry.getImagePreviewURL(_themeDisplay);
	}

	@Override
	public String getStickerIcon() {
		if (_designLibraryMasterLayout) {
			return "books-brush";
		}

		return null;
	}

	@Override
	public String getStickerTitle() {
		if (_designLibraryMasterLayout) {
			return MasterLayoutUtil.getMasterLayoutName(
				_themeDisplay.getScopeGroupId(), _layoutPageTemplateEntry,
				_themeDisplay.getLocale());
		}

		return null;
	}

	@Override
	public String getTitle() {
		return _layoutPageTemplateEntry.getName();
	}

	@Override
	public boolean isSelectable() {
		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SelectLayoutMasterLayoutVerticalCard.class);

	private final boolean _designLibraryMasterLayout;
	private final HttpServletRequest _httpServletRequest;
	private final LayoutPageTemplateEntry _layoutPageTemplateEntry;
	private final RenderResponse _renderResponse;
	private final ThemeDisplay _themeDisplay;

}