/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.frontend.taglib.servlet.taglib;

import com.liferay.commerce.frontend.taglib.internal.servlet.ServletContextUtil;
import com.liferay.commerce.product.catalog.CPCatalogEntry;
import com.liferay.commerce.product.catalog.CPSku;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;

import java.util.List;

/**
 * @author Alessio Antonio Rendina
 */
public class DiscontinuedLabelTag extends IncludeTag {

	@Override
	public int doStartTag() throws JspException {
		try {
			List<CPSku> cpSkus = _cpCatalogEntry.getCPSkus();

			if (cpSkus.size() == 1) {
				CPSku cpSku = cpSkus.get(0);

				_discontinued = cpSku.isDiscontinued();
			}
			else {
				_discontinued = false;
			}
		}
		catch (Exception exception) {
			_log.error(exception);

			return SKIP_BODY;
		}

		return super.doStartTag();
	}

	public CPCatalogEntry getCPCatalogEntry() {
		return _cpCatalogEntry;
	}

	public String getNamespace() {
		return _namespace;
	}

	@Override
	public void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute(
			"liferay-commerce:discontinued-label:discontinued", _discontinued);
		httpServletRequest.setAttribute(
			"liferay-commerce:discontinued-label:namespace", _namespace);
	}

	public void setCPCatalogEntry(CPCatalogEntry cpCatalogEntry) {
		_cpCatalogEntry = cpCatalogEntry;
	}

	public void setNamespace(String namespace) {
		_namespace = namespace;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_cpCatalogEntry = null;
		_discontinued = false;
		_namespace = StringPool.BLANK;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	private static final String _PAGE = "/discontinued_label/page.jsp";

	private static final Log _log = LogFactoryUtil.getLog(
		DiscontinuedLabelTag.class);

	private CPCatalogEntry _cpCatalogEntry;
	private boolean _discontinued;
	private String _namespace = StringPool.BLANK;

}