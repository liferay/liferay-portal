/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.frontend.taglib.clay.servlet.taglib;

import com.liferay.frontend.taglib.clay.servlet.taglib.NavigationCard;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * @author Eudaldo Alonso
 */
public class SelectBasicTemplatesNavigationCard implements NavigationCard {

	public SelectBasicTemplatesNavigationCard(
		String type, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		_type = type;
		_renderResponse = renderResponse;

		_httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
	}

	@Override
	public String getCssClass() {
		return "add-layout-action-option";
	}

	@Override
	public Map<String, String> getDynamicAttributes() {
		return HashMapBuilder.put(
			"data-add-layout-url",
			PortletURLBuilder.createRenderURL(
				_renderResponse
			).setMVCRenderCommandName(
				"/layout_admin/add_layout"
			).setBackURL(
				ParamUtil.getString(_httpServletRequest, "redirect")
			).setParameter(
				"privateLayout",
				ParamUtil.getBoolean(_httpServletRequest, "privateLayout")
			).setParameter(
				"selPlid", ParamUtil.getLong(_httpServletRequest, "selPlid")
			).setParameter(
				"type", _type
			).setWindowState(
				LiferayWindowState.POP_UP
			).buildString()
		).put(
			"role", "button"
		).put(
			"tabIndex", "0"
		).build();
	}

	@Override
	public String getIcon() {
		return "page";
	}

	@Override
	public String getTitle() {
		return LanguageUtil.get(_httpServletRequest, "layout.types." + _type);
	}

	@Override
	public Boolean isSmall() {
		return true;
	}

	private final HttpServletRequest _httpServletRequest;
	private final RenderResponse _renderResponse;
	private final String _type;

}