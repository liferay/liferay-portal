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
import com.liferay.mcp.server.web.internal.constants.MCPServerWebFDSNames;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Jose Luis Navarro
 */
public class ViewDataMasksDisplayContext {

	public ViewDataMasksDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		_httpServletRequest = httpServletRequest;
		_liferayPortletResponse = liferayPortletResponse;
	}

	public String getAPIURL() {
		return "/o/data-masks";
	}

	public CreationMenu getCreationMenu() {
		return CreationMenuBuilder.addPrimaryDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(
					PortletURLBuilder.createRenderURL(
						_liferayPortletResponse
					).setMVCRenderCommandName(
						"/mcp_server/edit_data_mask"
					).buildString());
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "new-data-mask"));
			}
		).build();
	}

	public List<FDSActionDropdownItem> getFDSActionDropdownItems() {
		return List.of(
			new FDSActionDropdownItem(
				PortletURLBuilder.createRenderURL(
					_liferayPortletResponse
				).setMVCRenderCommandName(
					"/mcp_server/edit_data_mask"
				).setParameter(
					"dataMaskId", "{id}"
				).setParameter(
					"readOnly", true
				).buildString(),
				"view", "view", LanguageUtil.get(_httpServletRequest, "view"),
				"get", null, null),
			new FDSActionDropdownItem(
				PortletURLBuilder.createRenderURL(
					_liferayPortletResponse
				).setMVCRenderCommandName(
					"/mcp_server/edit_data_mask"
				).setParameter(
					"dataMaskId", "{id}"
				).buildString(),
				"pencil", "edit", LanguageUtil.get(_httpServletRequest, "edit"),
				"get", null, null),
			new FDSActionDropdownItem(
				"#", "copy", "duplicate",
				LanguageUtil.get(_httpServletRequest, "duplicate"), "get", null,
				null),
			new FDSActionDropdownItem(
				"#", "trash", "delete",
				LanguageUtil.get(_httpServletRequest, "delete"), "get", null,
				null));
	}

	public String getFDSName() {
		return MCPServerWebFDSNames.DATA_MASKS;
	}

	public FDSSortItemList getFDSSortItemList() {
		return FDSSortItemListBuilder.add(
			FDSSortItemBuilder.setDirection(
				"asc"
			).setKey(
				"name"
			).setLabel(
				LanguageUtil.get(_httpServletRequest, "title")
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

	private final HttpServletRequest _httpServletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;

}