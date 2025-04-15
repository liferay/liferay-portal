/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.taglib.servlet.taglib;

import com.liferay.commerce.product.catalog.CPCatalogEntry;
import com.liferay.commerce.product.content.constants.CPContentWebKeys;
import com.liferay.commerce.product.content.render.list.entry.CPContentListEntryRenderer;
import com.liferay.commerce.product.content.render.list.entry.CPContentListEntryRendererRegistry;
import com.liferay.commerce.product.taglib.servlet.taglib.internal.servlet.ServletContextUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;

import java.util.Map;

/**
 * @author Alessio Antonio Rendina
 */
public class ProductListEntryRendererTag extends IncludeTag {

	@Override
	public int doStartTag() throws JspException {
		if (_cpCatalogEntry == null) {
			return SKIP_BODY;
		}

		HttpServletRequest httpServletRequest = getRequest();

		if (Validator.isNull(_key)) {
			Map<String, String> entryKeys =
				(Map<String, String>)httpServletRequest.getAttribute(
					CPContentWebKeys.CP_CONTENT_LIST_ENTRY_RENDERER_KEYS);

			_key = entryKeys.get(_cpCatalogEntry.getProductTypeName());
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		_cpContentListEntryRenderer =
			cpContentListEntryRendererRegistry.getCPContentListEntryRenderer(
				_key, portletDisplay.getPortletName(),
				_cpCatalogEntry.getProductTypeName());

		if (_cpContentListEntryRenderer == null) {
			_cpContentListEntryRenderer =
				cpContentListEntryRendererRegistry.
					getCPContentListEntryRenderer(
						"list-entry-default", portletDisplay.getPortletName(),
						_cpCatalogEntry.getProductTypeName());
		}

		return super.doStartTag();
	}

	public CPCatalogEntry getCPCatalogEntry() {
		return _cpCatalogEntry;
	}

	public String getKey() {
		return _key;
	}

	public void setCPCatalogEntry(CPCatalogEntry cpCatalogEntry) {
		_cpCatalogEntry = cpCatalogEntry;
	}

	public void setKey(String key) {
		_key = key;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		cpContentListEntryRendererRegistry =
			ServletContextUtil.getCPContentListEntryRendererRegistry();
		setServletContext(ServletContextUtil.getServletContext());
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_cpCatalogEntry = null;
		_cpContentListEntryRenderer = null;
		_key = null;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest = getRequest();

		httpServletRequest.setAttribute(
			"liferay-commerce-product:product-list-entry-renderer:" +
				"cpCatalogEntry",
			_cpCatalogEntry);
		httpServletRequest.setAttribute(
			"liferay-commerce-product:product-list-entry-renderer:" +
				"cpContentListEntryRenderer",
			_cpContentListEntryRenderer);
	}

	protected CPContentListEntryRendererRegistry
		cpContentListEntryRendererRegistry;

	private static final String _PAGE = "/product_list_entry_renderer/page.jsp";

	private CPCatalogEntry _cpCatalogEntry;
	private CPContentListEntryRenderer _cpContentListEntryRenderer;
	private String _key;

}