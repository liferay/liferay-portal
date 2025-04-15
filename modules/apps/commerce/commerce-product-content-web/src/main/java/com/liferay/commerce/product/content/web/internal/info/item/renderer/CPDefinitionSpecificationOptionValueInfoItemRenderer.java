/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.content.web.internal.info.item.renderer;

import com.liferay.commerce.product.constants.CPWebKeys;
import com.liferay.commerce.product.model.CPDefinitionSpecificationOptionValue;
import com.liferay.info.item.renderer.InfoItemRenderer;
import com.liferay.portal.kernel.language.Language;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(service = InfoItemRenderer.class)
public class CPDefinitionSpecificationOptionValueInfoItemRenderer
	implements InfoItemRenderer<CPDefinitionSpecificationOptionValue> {

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "product-specification");
	}

	@Override
	public void render(
		CPDefinitionSpecificationOptionValue
			cpDefinitionSpecificationOptionValue,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		if (cpDefinitionSpecificationOptionValue == null) {
			return;
		}

		try {
			RequestDispatcher requestDispatcher =
				_servletContext.getRequestDispatcher(
					"/info/item/renderer" +
						"/cp_definition_specification_option_value/page.jsp");

			httpServletRequest.setAttribute(
				CPWebKeys.CP_DEFINITION_SPECIFICATION_OPTION_VALUE,
				cpDefinitionSpecificationOptionValue);

			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	@Reference
	private Language _language;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.commerce.product.content.web)"
	)
	private ServletContext _servletContext;

}