/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.content.web.internal.info.item.renderer;

import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.util.CPDefinitionHelper;
import com.liferay.commerce.util.CommerceUtil;
import com.liferay.info.item.renderer.InfoItemRenderer;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

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
public class PriceInfoItemRenderer implements InfoItemRenderer<CPDefinition> {

	@Override
	public String getKey() {
		return "cpDefinition-price";
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "price");
	}

	@Override
	public void render(
		CPDefinition cpDefinition, HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		if (cpDefinition == null) {
			return;
		}

		try {
			RequestDispatcher requestDispatcher =
				_servletContext.getRequestDispatcher(
					"/info/item/renderer/price/page.jsp");

			CommerceContext commerceContext =
				(CommerceContext)httpServletRequest.getAttribute(
					CommerceWebKeys.COMMERCE_CONTEXT);

			httpServletRequest.setAttribute(
				"liferay-commerce:price:cpCatalogEntry",
				_cpDefinitionHelper.getCPCatalogEntry(
					CommerceUtil.getCommerceAccountId(commerceContext),
					commerceContext.getCommerceChannelGroupId(),
					cpDefinition.getCPDefinitionId(),
					_portal.getLocale(httpServletRequest)));

			String namespace = (String)httpServletRequest.getAttribute(
				"liferay-commerce:price:namespace");

			if (Validator.isNull(namespace)) {
				ThemeDisplay themeDisplay =
					(ThemeDisplay)httpServletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				PortletDisplay portletDisplay =
					themeDisplay.getPortletDisplay();

				namespace = portletDisplay.getNamespace();
			}

			httpServletRequest.setAttribute(
				"liferay-commerce:price:namespace", namespace);

			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	@Reference
	private CPDefinitionHelper _cpDefinitionHelper;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.commerce.product.content.web)"
	)
	private ServletContext _servletContext;

}