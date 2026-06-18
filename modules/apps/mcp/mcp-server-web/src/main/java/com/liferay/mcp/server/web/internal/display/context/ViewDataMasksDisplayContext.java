/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.web.internal.display.context;

import com.liferay.frontend.data.set.model.FDSSortItemBuilder;
import com.liferay.frontend.data.set.model.FDSSortItemList;
import com.liferay.frontend.data.set.model.FDSSortItemListBuilder;
import com.liferay.mcp.server.web.internal.constants.MCPServerWebFDSNames;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.HashMapBuilder;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * @author Jose Luis Navarro
 */
public class ViewDataMasksDisplayContext {

	public ViewDataMasksDisplayContext(
		LiferayPortletResponse liferayPortletResponse) {

		_liferayPortletResponse = liferayPortletResponse;
	}

	public String getAPIURL() {
		return "/o/mcp/server-data-masks";
	}

	public Map<String, Object> getFDSAdditionalProps() {
		return HashMapBuilder.<String, Object>put(
			"createURL",
			PortletURLBuilder.createRenderURL(
				_liferayPortletResponse
			).setMVCRenderCommandName(
				"/mcp_server/edit_data_mask"
			).buildString()
		).put(
			"editURL",
			PortletURLBuilder.createRenderURL(
				_liferayPortletResponse
			).setMVCRenderCommandName(
				"/mcp_server/edit_data_mask"
			).setParameter(
				"dataMaskId", _DATA_MASK_ID_TOKEN
			).buildString()
		).put(
			"viewURL",
			PortletURLBuilder.createRenderURL(
				_liferayPortletResponse
			).setMVCRenderCommandName(
				"/mcp_server/edit_data_mask"
			).setParameter(
				"dataMaskId", _DATA_MASK_ID_TOKEN
			).setParameter(
				"readOnly", true
			).buildString()
		).build();
	}

	public String getFDSName() {
		return MCPServerWebFDSNames.DATA_MASKS;
	}

	public FDSSortItemList getFDSSortItemList(
		HttpServletRequest httpServletRequest) {

		return FDSSortItemListBuilder.add(
			FDSSortItemBuilder.setDirection(
				"asc"
			).setKey(
				"name"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "title")
			).build()
		).add(
			FDSSortItemBuilder.setDirection(
				"desc"
			).setKey(
				"dateModified"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "last-modified")
			).build()
		).build();
	}

	private static final String _DATA_MASK_ID_TOKEN = "__DATA_MASK_ID__";

	private final LiferayPortletResponse _liferayPortletResponse;

}