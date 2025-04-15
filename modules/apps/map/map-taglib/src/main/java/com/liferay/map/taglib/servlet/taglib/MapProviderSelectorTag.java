/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.map.taglib.servlet.taglib;

import com.liferay.map.taglib.internal.servlet.ServletContextUtil;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

/**
 * @author Julio Camarero
 */
public class MapProviderSelectorTag extends IncludeTag {

	public String getConfigurationPrefix() {
		return _configurationPrefix;
	}

	public String getMapProviderKey() {
		return _mapProviderKey;
	}

	public String getName() {
		return _name;
	}

	public void setConfigurationPrefix(String configurationPrefix) {
		_configurationPrefix = configurationPrefix;
	}

	public void setMapProviderKey(String mapProviderKey) {
		_mapProviderKey = mapProviderKey;
	}

	public void setName(String name) {
		_name = name;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_configurationPrefix = null;
		_mapProviderKey = null;
		_name = null;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute(
			"liferay-map:map-provider-selector:configurationPrefix",
			_configurationPrefix);
		httpServletRequest.setAttribute(
			"liferay-map:map-provider-selector:mapProviderKey",
			_mapProviderKey);
		httpServletRequest.setAttribute(
			"liferay-map:map-provider-selector:mapProviders",
			ServletContextUtil.getMapProviders());
		httpServletRequest.setAttribute(
			"liferay-map:map-provider-selector:name", _name);
	}

	private static final String _PAGE = "/map_provider_selector/page.jsp";

	private String _configurationPrefix;
	private String _mapProviderKey;
	private String _name;

}