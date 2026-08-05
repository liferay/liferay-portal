/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemListBuilder;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;
import java.util.Objects;

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
		).put(
			"tab", getTab()
		).build();
	}

	public List<NavigationItem> getNavigationItems() {
		String tab = getTab();

		return NavigationItemListBuilder.add(
			navigationItem -> {
				navigationItem.setActive(Objects.equals(tab, "profile-info"));
				navigationItem.setHref(_getTabURL("profile-info"));
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "profile-info"));
			}
		).add(
			navigationItem -> {
				navigationItem.setActive(Objects.equals(tab, "data-masks"));
				navigationItem.setDisabled(_getProfileId() == 0);
				navigationItem.setHref(_getTabURL("data-masks"));
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "data-masks"));
			}
		).build();
	}

	public String getTab() {
		if (_getProfileId() == 0) {
			return "profile-info";
		}

		return ParamUtil.getString(_renderRequest, "tab", "profile-info");
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

	private String _getTabURL(String tab) {
		return PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).setMVCRenderCommandName(
			"/mcp_server/edit_profile"
		).setParameter(
			"profileId", _getProfileId()
		).setParameter(
			"tab", tab
		).buildString();
	}

	private final HttpServletRequest _httpServletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private final RenderRequest _renderRequest;

}