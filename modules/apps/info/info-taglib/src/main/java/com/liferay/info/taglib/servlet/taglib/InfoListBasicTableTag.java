/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.info.taglib.servlet.taglib;

import com.liferay.info.item.renderer.InfoItemRenderer;
import com.liferay.info.item.renderer.InfoItemRendererRegistry;
import com.liferay.info.taglib.internal.servlet.ServletContextUtil;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

import java.util.List;

/**
 * @author Guilherme Camacho
 */
public class InfoListBasicTableTag extends IncludeTag {

	public List<String> getInfoListObjectColumnNames() {
		return _infoListObjectColumnNames;
	}

	public List<?> getInfoListObjects() {
		return _infoListObjects;
	}

	public String getItemRendererKey() {
		return _itemRendererKey;
	}

	public void setInfoListObjectColumnNames(
		List<String> infoListObjectColumnNames) {

		_infoListObjectColumnNames = infoListObjectColumnNames;
	}

	public void setInfoListObjects(List<?> infoListObjects) {
		_infoListObjects = infoListObjects;
	}

	public void setItemRendererKey(String itemRendererKey) {
		_itemRendererKey = itemRendererKey;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_infoListObjectColumnNames = null;
		_infoListObjects = null;
		_itemRendererKey = null;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected ServletContext getServletContext() {
		return ServletContextUtil.getServletContext();
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		super.setAttributes(httpServletRequest);

		httpServletRequest.setAttribute(
			"liferay-info:info-list-basic-table:infoItemRenderer",
			_getInfoItemRenderer());
		httpServletRequest.setAttribute(
			"liferay-info:info-list-basic-table:infoListObjectColumnNames",
			getInfoListObjectColumnNames());
		httpServletRequest.setAttribute(
			"liferay-info:info-list-basic-table:infoListObjects",
			getInfoListObjects());
	}

	private InfoItemRenderer<?> _getInfoItemRenderer() {
		InfoItemRendererRegistry infoItemRendererRegistry =
			ServletContextUtil.getInfoItemRendererRegistry();

		return infoItemRendererRegistry.getInfoItemRenderer(
			getItemRendererKey());
	}

	private static final String _PAGE = "/info_list_basic_table/page.jsp";

	private List<String> _infoListObjectColumnNames;
	private List<?> _infoListObjects;
	private String _itemRendererKey;

}