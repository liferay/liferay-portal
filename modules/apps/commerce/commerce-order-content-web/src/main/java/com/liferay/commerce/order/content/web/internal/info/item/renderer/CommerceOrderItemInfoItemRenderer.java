/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.content.web.internal.info.item.renderer;

import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.order.content.web.internal.util.CommerceOrderItemUtil;
import com.liferay.commerce.price.CommerceOrderItemPrice;
import com.liferay.commerce.price.CommerceOrderPriceCalculation;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.service.CPInstanceService;
import com.liferay.commerce.product.util.CPDefinitionHelper;
import com.liferay.commerce.product.util.CPInstanceHelper;
import com.liferay.commerce.util.CommerceUtil;
import com.liferay.info.item.renderer.InfoItemRenderer;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(service = InfoItemRenderer.class)
public class CommerceOrderItemInfoItemRenderer
	implements InfoItemRenderer<CommerceOrderItem> {

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "order-item");
	}

	@Override
	public void render(
		CommerceOrderItem commerceOrderItem,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		if (commerceOrderItem == null) {
			return;
		}

		try {
			RequestDispatcher requestDispatcher =
				_servletContext.getRequestDispatcher(
					"/info/item/renderer/commerce_order_item/page.jsp");

			httpServletRequest.setAttribute(
				CommerceWebKeys.COMMERCE_ORDER_ITEM, commerceOrderItem);

			httpServletRequest.setAttribute(
				CommerceWebKeys.COMMERCE_ORDER_ITEM_CONTEXT_MAP,
				_getCommerceOrderItemContextMap(
					commerceOrderItem, httpServletRequest));

			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	private Map<String, String> _getCommerceOrderItemContextMap(
			CommerceOrderItem commerceOrderItem,
			HttpServletRequest httpServletRequest)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Locale locale = themeDisplay.getLocale();

		CommerceOrder commerceOrder = commerceOrderItem.getCommerceOrder();

		CommerceOrderItemPrice commerceOrderItemPrice =
			_commerceOrderPriceCalculation.getCommerceOrderItemPrice(
				commerceOrder.getCommerceCurrency(), commerceOrderItem);

		CPInstance cpInstance = _cpInstanceService.fetchCPInstance(
			commerceOrderItem.getCPInstanceId());

		return HashMapBuilder.put(
			"discountAmount",
			CommerceOrderItemUtil.formatDiscountAmount(
				commerceOrderItemPrice, locale)
		).put(
			"options",
			CommerceOrderItemUtil.getOptions(
				commerceOrderItem, _cpInstanceHelper, locale)
		).put(
			"promoPrice",
			CommerceOrderItemUtil.formatPromoPrice(
				commerceOrderItemPrice, locale)
		).put(
			"thumbnailURL",
			CommerceOrderItemUtil.getThumbnailURL(
				CommerceUtil.getCommerceAccountId(
					(CommerceContext)httpServletRequest.getAttribute(
						CommerceWebKeys.COMMERCE_CONTEXT)),
				cpInstance, _cpInstanceHelper)
		).put(
			"totalPrice",
			CommerceOrderItemUtil.formatTotalPrice(
				commerceOrderItemPrice, locale)
		).put(
			"unitPrice",
			CommerceOrderItemUtil.formatUnitPrice(
				commerceOrderItemPrice, _language, locale)
		).put(
			"URL",
			CommerceOrderItemUtil.getURL(
				_cpDefinitionHelper, cpInstance, themeDisplay)
		).build();
	}

	@Reference
	private CommerceOrderPriceCalculation _commerceOrderPriceCalculation;

	@Reference
	private CPDefinitionHelper _cpDefinitionHelper;

	@Reference
	private CPInstanceHelper _cpInstanceHelper;

	@Reference
	private CPInstanceService _cpInstanceService;

	@Reference
	private Language _language;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.commerce.order.content.web)"
	)
	private ServletContext _servletContext;

}