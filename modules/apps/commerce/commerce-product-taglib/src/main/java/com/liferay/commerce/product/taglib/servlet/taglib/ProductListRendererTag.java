/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.taglib.servlet.taglib;

import com.liferay.commerce.product.content.helper.CPContentHelper;
import com.liferay.commerce.product.content.render.list.CPContentListRenderer;
import com.liferay.commerce.product.content.render.list.CPContentListRendererRegistry;
import com.liferay.commerce.product.data.source.CPDataSourceResult;
import com.liferay.commerce.product.taglib.servlet.taglib.internal.servlet.ServletContextUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;

import java.util.Map;

/**
 * @author Alessio Antonio Rendina
 */
public class ProductListRendererTag extends IncludeTag {

	@Override
	public int doStartTag() throws JspException {
		if (Validator.isNull(_key)) {
			return SKIP_BODY;
		}

		_cpContentListRenderer =
			cpContentListRendererRegistry.getCPContentListRenderer(_key);

		return super.doStartTag();
	}

	public CPDataSourceResult getCPDataSourceResult() {
		return _cpDataSourceResult;
	}

	public Map<String, String> getEntryKeys() {
		return _entryKeys;
	}

	public String getKey() {
		return _key;
	}

	public void setCPDataSourceResult(CPDataSourceResult cpDataSourceResult) {
		_cpDataSourceResult = cpDataSourceResult;
	}

	public void setEntryKeys(Map<String, String> entryKeys) {
		_entryKeys = entryKeys;
	}

	public void setKey(String key) {
		_key = key;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());

		cpContentHelper = ServletContextUtil.getCPContentHelper();
		cpContentListRendererRegistry =
			ServletContextUtil.getCPContentListRendererRegistry();
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_cpContentListRenderer = null;
		_cpDataSourceResult = null;
		_entryKeys = null;
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
			"liferay-commerce-product:product-list-renderer:cpContentHelper",
			cpContentHelper);
		httpServletRequest.setAttribute(
			"liferay-commerce-product:product-list-renderer:" +
				"cpContentListRenderer",
			_cpContentListRenderer);
		httpServletRequest.setAttribute(
			"liferay-commerce-product:product-list-renderer:cpDataSourceResult",
			_cpDataSourceResult);
		httpServletRequest.setAttribute(
			"liferay-commerce-product:product-list-renderer:entryKeys",
			_entryKeys);
	}

	protected CPContentHelper cpContentHelper;
	protected CPContentListRendererRegistry cpContentListRendererRegistry;

	private static final String _PAGE = "/product_list_renderer/page.jsp";

	private CPContentListRenderer _cpContentListRenderer;
	private CPDataSourceResult _cpDataSourceResult;
	private Map<String, String> _entryKeys;
	private String _key;

}