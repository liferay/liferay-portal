/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.web.internal.display.context;

import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.RenderRequest;

import java.util.Map;

/**
 * @author Jose Luis Navarro
 */
public class EditDataMaskDisplayContext {

	public EditDataMaskDisplayContext(
		LiferayPortletResponse liferayPortletResponse,
		RenderRequest renderRequest) {

		_liferayPortletResponse = liferayPortletResponse;
		_renderRequest = renderRequest;
	}

	public String getBackURL() {
		return PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).buildString();
	}

	public Map<String, Object> getEditDataMaskProps() {
		return HashMapBuilder.<String, Object>put(
			"backURL", getBackURL()
		).put(
			"dataMaskId", ParamUtil.getLong(_renderRequest, "dataMaskId")
		).build();
	}

	private final LiferayPortletResponse _liferayPortletResponse;
	private final RenderRequest _renderRequest;

}