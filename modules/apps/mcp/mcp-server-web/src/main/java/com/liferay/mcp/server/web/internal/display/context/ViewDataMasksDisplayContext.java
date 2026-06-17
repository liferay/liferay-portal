/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.web.internal.display.context;

import com.liferay.mcp.server.web.internal.constants.MCPServerWebFDSNames;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.HashMapBuilder;

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

	private static final String _DATA_MASK_ID_TOKEN = "__DATA_MASK_ID__";

	private final LiferayPortletResponse _liferayPortletResponse;

}