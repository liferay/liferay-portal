/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shop.by.diagram.web.internal.info.item.renderer;

import com.liferay.account.model.AccountEntry;
import com.liferay.commerce.product.constants.CPWebKeys;
import com.liferay.commerce.product.content.constants.CPContentWebKeys;
import com.liferay.commerce.product.content.helper.CPContentHelper;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.product.util.CPDefinitionHelper;
import com.liferay.commerce.shop.by.diagram.constants.CSDiagramWebKeys;
import com.liferay.commerce.shop.by.diagram.model.CSDiagramEntry;
import com.liferay.commerce.shop.by.diagram.util.CSDiagramCPTypeHelper;
import com.liferay.commerce.util.CommerceAccountHelper;
import com.liferay.info.item.renderer.InfoItemRenderer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.Portal;

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
public class DiagramCardInfoItemRenderer
	implements InfoItemRenderer<CSDiagramEntry> {

	@Override
	public String getKey() {
		return "csDiagramEntry-diagram-card";
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "diagram-card");
	}

	@Override
	public void render(
		CSDiagramEntry csDiagramEntry, HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		if (csDiagramEntry == null) {
			return;
		}

		try {
			long groupId = _portal.getScopeGroupId(httpServletRequest);

			httpServletRequest.setAttribute(
				CPWebKeys.CP_CATALOG_ENTRY,
				_cpDefinitionHelper.getCPCatalogEntry(
					_getAccountEntryId(groupId, httpServletRequest), groupId,
					csDiagramEntry.getCPDefinitionId(),
					_portal.getLocale(httpServletRequest)));

			httpServletRequest.setAttribute(
				CPContentWebKeys.CP_CONTENT_HELPER, _cpContentHelper);
			httpServletRequest.setAttribute(
				CSDiagramWebKeys.CS_DIAGRAM_CP_TYPE_HELPER,
				_csDiagramCPTypeHelper);
			httpServletRequest.setAttribute(
				"liferay-commerce:product-card:showAddToCartButton",
				Boolean.TRUE);
			httpServletRequest.setAttribute(
				"liferay-commerce:product-card:showImage", Boolean.TRUE);
			httpServletRequest.setAttribute(
				"liferay-commerce:product-card:showName", Boolean.TRUE);
			httpServletRequest.setAttribute(
				"liferay-commerce:product-card:showPrice", Boolean.TRUE);

			RequestDispatcher requestDispatcher =
				_servletContext.getRequestDispatcher(
					"/info/item/renderer/diagram_card/page.jsp");

			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	private long _getAccountEntryId(
			long groupId, HttpServletRequest httpServletRequest)
		throws PortalException {

		AccountEntry accountEntry =
			_commerceAccountHelper.getCurrentAccountEntry(
				_commerceChannelLocalService.
					getCommerceChannelGroupIdBySiteGroupId(groupId),
				httpServletRequest);

		if (accountEntry != null) {
			return accountEntry.getAccountEntryId();
		}

		return 0;
	}

	@Reference
	private CommerceAccountHelper _commerceAccountHelper;

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CPContentHelper _cpContentHelper;

	@Reference
	private CPDefinitionHelper _cpDefinitionHelper;

	@Reference
	private CSDiagramCPTypeHelper _csDiagramCPTypeHelper;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.commerce.shop.by.diagram.web)"
	)
	private ServletContext _servletContext;

}