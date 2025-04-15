/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.taglib.servlet.taglib;

import com.liferay.asset.taglib.internal.servlet.ServletContextUtil;
import com.liferay.taglib.util.IncludeTag;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

/**
 * @author Julio Camarero
 */
public class CategorizationFilterTag extends IncludeTag {

	public String getAssetType() {
		return _assetType;
	}

	public long[] getGroupIds() {
		return _groupIds;
	}

	public PortletURL getPortletURL() {
		return _portletURL;
	}

	public void setAssetType(String assetType) {
		_assetType = assetType;
	}

	public void setGroupIds(long[] groupIds) {
		_groupIds = groupIds;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	public void setPortletURL(PortletURL portletURL) {
		_portletURL = portletURL;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_assetType = null;
		_groupIds = null;
		_portletURL = null;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute(
			"liferay-asset:categorization-filter:assetType", _assetType);
		httpServletRequest.setAttribute(
			"liferay-asset:categorization-filter:groupIds", _groupIds);
		httpServletRequest.setAttribute(
			"liferay-asset:categorization-filter:portletURL", _portletURL);
	}

	private static final String _PAGE = "/categorization_filter/page.jsp";

	private String _assetType;
	private long[] _groupIds;
	private PortletURL _portletURL;

}