/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.web.internal.display.context;

import com.liferay.frontend.data.set.model.FDSSortItemBuilder;
import com.liferay.frontend.data.set.model.FDSSortItemList;
import com.liferay.frontend.data.set.model.FDSSortItemListBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemListBuilder;
import com.liferay.mcp.server.web.internal.constants.MCPServerFDSNames;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

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

	public Map<String, Object> getAdditionalProps() {
		return HashMapBuilder.<String, Object>put(
			"profileERC", _getProfileERC()
		).build();
	}

	public String getAPIURL() {
		String apiURL = HttpComponentsUtil.addParameter(
			"/o/mcp/server-profile-tools", "fields",
			"externalReferenceCode,toolName,toolSetName");

		return HttpComponentsUtil.addParameter(
			apiURL, "filter",
			StringBundler.concat(
				"r_mcpServerProfileToTools_l_mcpServerProfileERC eq '",
				StringUtil.replace(_getProfileERC(), '\'', "''"), "'"));
	}

	public String getBackURL() {
		return PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).setMVCRenderCommandName(
			"/mcp_server/view_profiles"
		).buildString();
	}

	public CreationMenu getCreationMenu() {
		return CreationMenuBuilder.addPrimaryDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref("#");
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "add-tools"));
			}
		).build();
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
			"profileERC", _getProfileERC()
		).build();
	}

	public String getFDSName() {
		return MCPServerFDSNames.PROFILE_TOOLS;
	}

	public FDSSortItemList getFDSSortItemList() {
		return FDSSortItemListBuilder.add(
			FDSSortItemBuilder.setDirection(
				"asc"
			).setKey(
				"toolName"
			).setLabel(
				LanguageUtil.get(_httpServletRequest, "title")
			).build()
		).add(
			FDSSortItemBuilder.setDirection(
				"asc"
			).setKey(
				"toolSetName"
			).setLabel(
				LanguageUtil.get(_httpServletRequest, "tool-set")
			).build()
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
				navigationItem.setActive(Objects.equals(tab, "tools"));
				navigationItem.setDisabled(Validator.isNull(_getProfileERC()));
				navigationItem.setHref(_getTabURL("tools"));
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "tools"));
			}
		).add(
			navigationItem -> {
				navigationItem.setActive(Objects.equals(tab, "data-masks"));
				navigationItem.setDisabled(Validator.isNull(_getProfileERC()));
				navigationItem.setHref(_getTabURL("data-masks"));
				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "data-masks"));
			}
		).build();
	}

	public Map<String, Object> getProfileDataMasksProps() {
		return HashMapBuilder.<String, Object>put(
			"profileERC", _getProfileERC()
		).build();
	}

	public String getTab() {
		if (Validator.isNull(_getProfileERC())) {
			return "profile-info";
		}

		String tab = ParamUtil.getString(_renderRequest, "tab");

		if (Objects.equals(tab, "data-masks") || Objects.equals(tab, "tools")) {
			return tab;
		}

		return "profile-info";
	}

	public String getTitle() {
		if (Validator.isNotNull(_getProfileERC())) {
			return LanguageUtil.get(_httpServletRequest, "edit-profile");
		}

		return LanguageUtil.get(_httpServletRequest, "new-profile");
	}

	private String _getProfileERC() {
		return ParamUtil.getString(_renderRequest, "profileERC");
	}

	private String _getTabURL(String tab) {
		return PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).setMVCRenderCommandName(
			"/mcp_server/edit_profile"
		).setParameter(
			"profileERC", _getProfileERC()
		).setParameter(
			"tab", tab
		).buildString();
	}

	private final HttpServletRequest _httpServletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private final RenderRequest _renderRequest;

}