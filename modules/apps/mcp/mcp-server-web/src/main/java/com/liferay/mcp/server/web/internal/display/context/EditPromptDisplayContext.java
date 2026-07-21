/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.web.internal.display.context;

import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * @author Jorge González
 */
public class EditPromptDisplayContext {

	public EditPromptDisplayContext(
		LiferayPortletResponse liferayPortletResponse,
		RenderRequest renderRequest) {

		_liferayPortletResponse = liferayPortletResponse;
		_renderRequest = renderRequest;
	}

	public String getBackURL() {
		return PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).setMVCRenderCommandName(
			"/mcp_server/view_prompts"
		).buildString();
	}

	public Map<String, Object> getEditPromptProps() {
		return HashMapBuilder.<String, Object>put(
			"backURL", getBackURL()
		).put(
			"promptId", ParamUtil.getLong(_renderRequest, "promptId")
		).build();
	}

	public String getTitle(HttpServletRequest httpServletRequest) {
		if (ParamUtil.getLong(_renderRequest, "promptId") > 0) {
			return LanguageUtil.get(httpServletRequest, "edit-prompt");
		}

		return LanguageUtil.get(httpServletRequest, "new-prompt");
	}

	private final LiferayPortletResponse _liferayPortletResponse;
	private final RenderRequest _renderRequest;

}