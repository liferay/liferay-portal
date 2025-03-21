/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.frontend.taglib.servlet.taglib;

import com.liferay.commerce.frontend.taglib.internal.servlet.ServletContextUtil;
import com.liferay.commerce.product.catalog.CPCatalogEntry;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.service.CPDefinitionLocalServiceUtil;
import com.liferay.info.item.renderer.InfoItemRenderer;
import com.liferay.info.item.renderer.InfoItemRendererRegistry;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.taglib.servlet.PipingServletResponseFactory;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;

/**
 * @author Gianmarco Brunialti Masera
 * @author Ivica Cardic
 * @author Alec Sloan
 */
public class AvailabilityLabelTag extends IncludeTag {

	@Override
	public int doStartTag() throws JspException {
		try {
			InfoItemRenderer<CPDefinition> infoItemRenderer =
				(InfoItemRenderer<CPDefinition>)
					_infoItemRendererRegistry.getInfoItemRenderer(
						"cpDefinition-availability-label");

			infoItemRenderer.render(
				CPDefinitionLocalServiceUtil.getCPDefinition(
					_cpCatalogEntry.getCPDefinitionId()),
				(HttpServletRequest)pageContext.getRequest(),
				PipingServletResponseFactory.createPipingServletResponse(
					pageContext));
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return SKIP_BODY;
		}

		return SKIP_BODY;
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
			"liferay-commerce:availability-label:namespace", _namespace);
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

		_infoItemRendererRegistry =
			ServletContextUtil.getInfoItemRendererRegistry();
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_cpCatalogEntry = null;
		_infoItemRendererRegistry = null;
		_namespace = StringPool.BLANK;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AvailabilityLabelTag.class);

	private CPCatalogEntry _cpCatalogEntry;
	private InfoItemRendererRegistry _infoItemRendererRegistry;
	private String _namespace = StringPool.BLANK;

}