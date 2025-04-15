/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.info.list.renderer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Pavel Savinov
 */
public class DefaultInfoListRendererContext implements InfoListRendererContext {

	public DefaultInfoListRendererContext(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		_httpServletRequest = httpServletRequest;
		_httpServletResponse = httpServletResponse;
	}

	@Override
	public HttpServletRequest getHttpServletRequest() {
		return _httpServletRequest;
	}

	@Override
	public HttpServletResponse getHttpServletResponse() {
		return _httpServletResponse;
	}

	@Override
	public String getListItemRendererKey() {
		return _listItemRendererKey;
	}

	@Override
	public String getTemplateKey() {
		return _templateKey;
	}

	public void setListItemRendererKey(String listItemRendererKey) {
		_listItemRendererKey = listItemRendererKey;
	}

	public void setTemplateKey(String templateKey) {
		_templateKey = templateKey;
	}

	private final HttpServletRequest _httpServletRequest;
	private final HttpServletResponse _httpServletResponse;
	private String _listItemRendererKey;
	private String _templateKey;

}