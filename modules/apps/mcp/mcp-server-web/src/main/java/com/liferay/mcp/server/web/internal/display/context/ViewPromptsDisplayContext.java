/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.web.internal.display.context;

import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.data.set.model.FDSSortItemBuilder;
import com.liferay.frontend.data.set.model.FDSSortItemList;
import com.liferay.frontend.data.set.model.FDSSortItemListBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.mcp.server.web.internal.constants.MCPServerFDSNames;
import com.liferay.mcp.server.web.internal.util.MCPServerPermissionUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jorge González
 */
public class ViewPromptsDisplayContext {

	public ViewPromptsDisplayContext(
			HttpServletRequest httpServletRequest,
			LiferayPortletResponse liferayPortletResponse)
		throws PortalException {

		_httpServletRequest = httpServletRequest;
		_liferayPortletResponse = liferayPortletResponse;

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		_objectDefinition =
			ObjectDefinitionLocalServiceUtil.
				getObjectDefinitionByExternalReferenceCode(
					"L_MCP_SERVER_PROMPT", themeDisplay.getCompanyId());

		_addObjectEntryPermission =
			MCPServerPermissionUtil.hasAddObjectEntryPermission(
				httpServletRequest, _objectDefinition);
	}

	public String getAPIURL() {
		return "/o/mcp/server-prompts";
	}

	public CreationMenu getCreationMenu() {
		if (!_addObjectEntryPermission) {
			return null;
		}

		return CreationMenuBuilder.addPrimaryDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(
					PortletURLBuilder.createRenderURL(
						_liferayPortletResponse
					).setMVCRenderCommandName(
						"/mcp_server/edit_prompt"
					).buildString());
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "new-prompt"));
			}
		).build();
	}

	public List<FDSActionDropdownItem> getFDSActionDropdownItems() {
		List<FDSActionDropdownItem> fdsActionDropdownItems = new ArrayList<>();

		fdsActionDropdownItems.add(
			new FDSActionDropdownItem(
				PortletURLBuilder.createRenderURL(
					_liferayPortletResponse
				).setMVCRenderCommandName(
					"/mcp_server/edit_prompt"
				).setParameter(
					"promptId", "{id}"
				).buildString(),
				"pencil", "edit", LanguageUtil.get(_httpServletRequest, "edit"),
				"get", "update", null));

		if (_addObjectEntryPermission) {
			fdsActionDropdownItems.add(
				new FDSActionDropdownItem(
					"#", "copy", "duplicate",
					LanguageUtil.get(_httpServletRequest, "duplicate"), "get",
					null, null));
		}

		fdsActionDropdownItems.add(
			new FDSActionDropdownItem(
				"#", "trash", "delete",
				LanguageUtil.get(_httpServletRequest, "delete"), "get",
				"delete", null));
		fdsActionDropdownItems.add(
			MCPServerPermissionUtil.getPermissionsFDSActionDropdownItem(
				_httpServletRequest, _objectDefinition));

		return fdsActionDropdownItems;
	}

	public String getFDSName() {
		return MCPServerFDSNames.PROMPTS;
	}

	public FDSSortItemList getFDSSortItemList() {
		return FDSSortItemListBuilder.add(
			FDSSortItemBuilder.setDirection(
				"asc"
			).setKey(
				"name"
			).setLabel(
				LanguageUtil.get(_httpServletRequest, "name")
			).build()
		).add(
			FDSSortItemBuilder.setDirection(
				"asc"
			).setKey(
				"promptStatus"
			).setLabel(
				LanguageUtil.get(_httpServletRequest, "status")
			).build()
		).add(
			FDSSortItemBuilder.setDirection(
				"desc"
			).setKey(
				"dateModified"
			).setLabel(
				LanguageUtil.get(_httpServletRequest, "last-modified")
			).build()
		).build();
	}

	private final boolean _addObjectEntryPermission;
	private final HttpServletRequest _httpServletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private final ObjectDefinition _objectDefinition;

}