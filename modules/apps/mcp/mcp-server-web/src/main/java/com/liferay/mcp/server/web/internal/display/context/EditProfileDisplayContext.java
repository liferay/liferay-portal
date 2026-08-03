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
public class EditProfileDisplayContext {

	public EditProfileDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletResponse liferayPortletResponse,
		RenderRequest renderRequest) {

		_httpServletRequest = httpServletRequest;
		_liferayPortletResponse = liferayPortletResponse;
		_renderRequest = renderRequest;
	}

	public String getBackURL() {
		return PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).setMVCRenderCommandName(
			"/mcp_server/view_profiles"
		).buildString();
	}

	public Map<String, Object> getEditProfileProps() {
		return HashMapBuilder.<String, Object>put(
			"backURL", getBackURL()
		).put(
			"editProfileURL",
			PortletURLBuilder.createRenderURL(
				_liferayPortletResponse
			).setMVCRenderCommandName(
				"/mcp_server/edit_profile"
			).buildString()
		).put(
			"portletNamespace", _liferayPortletResponse.getNamespace()
		).put(
			"profileId", _getProfileId()
		).build();
	}

	public String getTitle() {
		if (_getProfileId() > 0) {
			return LanguageUtil.get(_httpServletRequest, "edit-profile");
		}

		return LanguageUtil.get(_httpServletRequest, "new-profile");
	}

	private long _getProfileId() {
		return ParamUtil.getLong(_renderRequest, "profileId");
	}

	private final HttpServletRequest _httpServletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private final RenderRequest _renderRequest;

}
