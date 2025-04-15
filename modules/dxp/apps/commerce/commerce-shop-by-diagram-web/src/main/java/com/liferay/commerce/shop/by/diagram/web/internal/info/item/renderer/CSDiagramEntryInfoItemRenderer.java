/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shop.by.diagram.web.internal.info.item.renderer;

import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.product.constants.CPWebKeys;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.permission.CommerceProductViewPermission;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.shop.by.diagram.model.CSDiagramEntry;
import com.liferay.commerce.util.CommerceUtil;
import com.liferay.info.item.renderer.InfoItemRenderer;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mahmoud Azzam
 * @author Alessio Antonio Rendina
 */
@Component(service = InfoItemRenderer.class)
public class CSDiagramEntryInfoItemRenderer
	implements InfoItemRenderer<CSDiagramEntry> {

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "related-diagram");
	}

	@Override
	public void render(
		CSDiagramEntry csDiagramEntry, HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		if (csDiagramEntry == null) {
			return;
		}

		try {
			CPDefinition cpDefinition =
				_cpDefinitionLocalService.fetchCPDefinition(
					csDiagramEntry.getCPDefinitionId());

			if (cpDefinition == null) {
				return;
			}

			CommerceContext commerceContext =
				(CommerceContext)httpServletRequest.getAttribute(
					CommerceWebKeys.COMMERCE_CONTEXT);

			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			if (!_commerceProductViewPermission.contains(
					themeDisplay.getPermissionChecker(),
					CommerceUtil.getCommerceAccountId(commerceContext),
					commerceContext.getCommerceChannelGroupId(),
					cpDefinition.getCPDefinitionId())) {

				return;
			}

			RequestDispatcher requestDispatcher =
				_servletContext.getRequestDispatcher(
					"/info/item/renderer/cs_diagram_entry/page.jsp");

			httpServletRequest.setAttribute(
				CPWebKeys.CP_DEFINITION, cpDefinition);

			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	@Reference
	private CommerceProductViewPermission _commerceProductViewPermission;

	@Reference
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Reference
	private Language _language;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.commerce.shop.by.diagram.web)"
	)
	private ServletContext _servletContext;

}