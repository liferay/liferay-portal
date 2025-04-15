/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shipment.web.internal.frontend.data.set.provider;

import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.constants.CommerceShipmentFDSNames;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.frontend.model.Icon;
import com.liferay.commerce.frontend.model.OrderItem;
import com.liferay.commerce.inventory.engine.CommerceInventoryEngine;
import com.liferay.commerce.model.CommerceAddress;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceShipment;
import com.liferay.commerce.model.CommerceShipmentItem;
import com.liferay.commerce.model.CommerceShippingEngine;
import com.liferay.commerce.model.CommerceShippingMethod;
import com.liferay.commerce.model.CommerceShippingOption;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.service.CommerceAddressLocalService;
import com.liferay.commerce.service.CommerceOrderItemLocalService;
import com.liferay.commerce.service.CommerceShipmentItemService;
import com.liferay.commerce.service.CommerceShipmentService;
import com.liferay.commerce.util.CommerceShippingEngineRegistry;
import com.liferay.frontend.data.set.provider.FDSDataProvider;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alec Sloan
 */
@Component(
	property = "fds.data.provider.key=" + CommerceShipmentFDSNames.SHIPPABLE_ORDER_ITEMS,
	service = FDSDataProvider.class
)
public class CommerceShippableOrderItemsFDSDataProvider
	implements FDSDataProvider<OrderItem> {

	@Override
	public List<OrderItem> getItems(
			FDSKeywords fdsKeywords, FDSPagination fdsPagination,
			HttpServletRequest httpServletRequest, Sort sort)
		throws PortalException {

		long commerceShipmentId = ParamUtil.getLong(
			httpServletRequest, "commerceShipmentId");

		CommerceShipment commerceShipment =
			_commerceShipmentService.getCommerceShipment(commerceShipmentId);

		return TransformUtil.transform(
			_commerceOrderItemLocalService.getCommerceOrderItems(
				commerceShipment.getGroupId(),
				commerceShipment.getCommerceAccountId(), orderStatuses,
				fdsPagination.getStartPosition(),
				fdsPagination.getEndPosition()),
			commerceOrderItem -> {
				if (!commerceOrderItem.isShippable()) {
					return null;
				}

				CommerceOrder commerceOrder =
					commerceOrderItem.getCommerceOrder();

				String iconName = _getAddressMatchIcon(
					commerceShipment, commerceOrder);

				Icon icon = null;

				if (iconName != null) {
					icon = new Icon(iconName);
				}

				CommerceShipmentItem commerceShipmentItem =
					_commerceShipmentItemService.fetchCommerceShipmentItem(
						commerceShipmentId,
						commerceOrderItem.getCommerceOrderItemId(), 0);

				if (commerceShipmentItem != null) {
					return null;
				}

				long commerceCatalogGroupId = 0;

				CPDefinition cpDefinition = commerceOrderItem.getCPDefinition();

				if (cpDefinition != null) {
					commerceCatalogGroupId = cpDefinition.getGroupId();
				}

				BigDecimal quantity = commerceOrderItem.getQuantity();

				return new OrderItem(
					_commerceInventoryEngine.getStockQuantity(
						commerceOrderItem.getCompanyId(),
						commerceOrder.getCommerceAccountId(),
						commerceCatalogGroupId, commerceOrderItem.getGroupId(),
						commerceOrderItem.getSku(),
						commerceOrderItem.getUnitOfMeasureKey()),
					icon, commerceOrderItem.getCommerceOrderId(),
					commerceOrderItem.getCommerceOrderItemId(),
					quantity.subtract(commerceOrderItem.getShippedQuantity()),
					_getShippingMethodAndOptionName(
						commerceOrder, httpServletRequest),
					commerceOrderItem.getSku(),
					commerceOrderItem.getUnitOfMeasureKey());
			});
	}

	@Override
	public int getItemsCount(
			FDSKeywords fdsKeywords, HttpServletRequest httpServletRequest)
		throws PortalException {

		long commerceShipmentId = ParamUtil.getLong(
			httpServletRequest, "commerceShipmentId");

		CommerceShipment commerceShipment =
			_commerceShipmentService.getCommerceShipment(commerceShipmentId);

		return _commerceOrderItemLocalService.getCommerceOrderItemsCount(
			commerceShipment.getGroupId(),
			commerceShipment.getCommerceAccountId(), orderStatuses);
	}

	protected int[] orderStatuses = {
		CommerceOrderConstants.ORDER_STATUS_PROCESSING,
		CommerceOrderConstants.ORDER_STATUS_PARTIALLY_SHIPPED
	};

	private String _getAddressMatchIcon(
			CommerceShipment commerceShipment, CommerceOrder commerceOrder)
		throws PortalException {

		CommerceAddress commerceOrderShippingCommerceAddress =
			_commerceAddressLocalService.fetchCommerceAddress(
				commerceOrder.getShippingAddressId());
		CommerceAddress commerceShipmentCommerceAddress =
			_commerceAddressLocalService.fetchCommerceAddress(
				commerceShipment.getCommerceAddressId());

		if ((commerceOrderShippingCommerceAddress != null) &&
			(commerceShipmentCommerceAddress != null) &&
			commerceShipmentCommerceAddress.isSameAddress(
				commerceOrderShippingCommerceAddress)) {

			return "check";
		}

		return StringPool.BLANK;
	}

	private String _getShippingMethodAndOptionName(
			CommerceOrder commerceOrder, HttpServletRequest httpServletRequest)
		throws PortalException {

		CommerceShippingMethod commerceShippingMethod =
			commerceOrder.getCommerceShippingMethod();

		if (commerceShippingMethod == null) {
			return StringPool.BLANK;
		}

		CommerceShippingEngine commerceShippingEngine =
			_commerceShippingEngineRegistry.getCommerceShippingEngine(
				commerceShippingMethod.getEngineKey());

		if (commerceShippingEngine == null) {
			return StringPool.BLANK;
		}

		CommerceContext commerceContext =
			(CommerceContext)httpServletRequest.getAttribute(
				CommerceWebKeys.COMMERCE_CONTEXT);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		List<CommerceShippingOption> commerceShippingOptions =
			commerceShippingEngine.getCommerceShippingOptions(
				commerceContext, commerceOrder, themeDisplay.getLocale());

		for (CommerceShippingOption commerceShippingOption :
				commerceShippingOptions) {

			String commerceShippingOptionKey = commerceShippingOption.getKey();

			if (commerceShippingOptionKey.equals(
					commerceOrder.getShippingOptionName())) {

				return StringBundler.concat(
					commerceShippingMethod.getName(themeDisplay.getLocale()),
					" - ", commerceShippingOption.getName());
			}
		}

		return StringPool.BLANK;
	}

	@Reference
	private CommerceAddressLocalService _commerceAddressLocalService;

	@Reference
	private CommerceInventoryEngine _commerceInventoryEngine;

	@Reference
	private CommerceOrderItemLocalService _commerceOrderItemLocalService;

	@Reference
	private CommerceShipmentItemService _commerceShipmentItemService;

	@Reference
	private CommerceShipmentService _commerceShipmentService;

	@Reference
	private CommerceShippingEngineRegistry _commerceShippingEngineRegistry;

}