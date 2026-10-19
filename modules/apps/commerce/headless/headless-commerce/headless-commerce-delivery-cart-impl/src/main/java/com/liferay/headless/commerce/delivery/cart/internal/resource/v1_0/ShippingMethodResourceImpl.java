/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.cart.internal.resource.v1_0;

import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.context.CommerceContextFactory;
import com.liferay.commerce.currency.util.CommercePriceFormatter;
import com.liferay.commerce.exception.NoSuchOrderException;
import com.liferay.commerce.model.CommerceAddress;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceShippingEngine;
import com.liferay.commerce.model.CommerceShippingMethod;
import com.liferay.commerce.model.CommerceShippingOption;
import com.liferay.commerce.order.CommerceOrderHttpHelper;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.commerce.service.CommerceShippingMethodLocalService;
import com.liferay.commerce.util.CommerceShippingEngineRegistry;
import com.liferay.commerce.util.comparator.CommerceShippingOptionPriorityComparator;
import com.liferay.headless.commerce.delivery.cart.dto.v1_0.ShippingMethod;
import com.liferay.headless.commerce.delivery.cart.dto.v1_0.ShippingOption;
import com.liferay.headless.commerce.delivery.cart.internal.util.v1_0.GuestCommerceOrderUtil;
import com.liferay.headless.commerce.delivery.cart.resource.v1_0.ShippingMethodResource;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.vulcan.pagination.Page;

import java.math.BigDecimal;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Andrea Sbarra
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/shipping-method.properties",
	scope = ServiceScope.PROTOTYPE, service = ShippingMethodResource.class
)
public class ShippingMethodResourceImpl extends BaseShippingMethodResourceImpl {

	@Override
	public Page<ShippingMethod>
			getCartByExternalReferenceCodeShippingMethodsPage(
				String externalReferenceCode)
		throws Exception {

		CommerceOrder commerceOrder =
			_commerceOrderService.fetchCommerceOrderByExternalReferenceCode(
				externalReferenceCode, contextCompany.getCompanyId());

		if (commerceOrder == null) {
			throw new NoSuchOrderException(
				"Unable to find order with external reference code " +
					externalReferenceCode);
		}

		GuestCommerceOrderUtil.checkGuestCommerceOrder(
			commerceOrder, _commerceOrderHttpHelper, contextHttpServletRequest);

		CommerceAddress shippingCommerceAddress =
			commerceOrder.getShippingAddress();

		if (shippingCommerceAddress == null) {
			return super.getCartByExternalReferenceCodeShippingMethodsPage(
				commerceOrder.getExternalReferenceCode());
		}

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannelByOrderGroupId(
				commerceOrder.getGroupId());

		return Page.of(
			transform(
				_commerceShippingMethodLocalService.getCommerceShippingMethods(
					commerceChannel.getGroupId(),
					shippingCommerceAddress.getCountryId(), true),
				shippingMethod -> _toShippingMethod(
					shippingMethod, commerceChannel, commerceOrder)));
	}

	@Override
	public Page<ShippingMethod> getCartShippingMethodsPage(Long cartId)
		throws Exception {

		CommerceOrder commerceOrder = _commerceOrderService.getCommerceOrder(
			cartId);

		GuestCommerceOrderUtil.checkGuestCommerceOrder(
			commerceOrder, _commerceOrderHttpHelper, contextHttpServletRequest);

		CommerceAddress shippingCommerceAddress =
			commerceOrder.getShippingAddress();

		if (shippingCommerceAddress == null) {
			return super.getCartShippingMethodsPage(cartId);
		}

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannelByOrderGroupId(
				commerceOrder.getGroupId());

		return Page.of(
			transform(
				_commerceShippingMethodLocalService.getCommerceShippingMethods(
					commerceChannel.getGroupId(),
					shippingCommerceAddress.getCountryId(), true),
				shippingMethod -> _toShippingMethod(
					shippingMethod, commerceChannel, commerceOrder)));
	}

	private ShippingOption[] _getShippingOptions(
			CommerceShippingMethod commerceShippingMethod,
			CommerceChannel commerceChannel, CommerceOrder commerceOrder)
		throws PortalException {

		CommerceContext commerceContext = _commerceContextFactory.create(
			commerceOrder.getCommerceAccountId(), commerceChannel.getGroupId(),
			null, commerceOrder.getCommerceOrderId(),
			contextCompany.getCompanyId());

		CommerceShippingEngine commerceShippingEngine =
			_commerceShippingEngineRegistry.getCommerceShippingEngine(
				commerceShippingMethod.getEngineKey());

		return transformToArray(
			ListUtil.sort(
				commerceShippingEngine.getCommerceShippingOptions(
					commerceContext, commerceOrder,
					contextAcceptLanguage.getPreferredLocale()),
				new CommerceShippingOptionPriorityComparator()),
			shippingOption -> _toShippingOption(
				shippingOption, commerceContext),
			ShippingOption.class);
	}

	private ShippingMethod _toShippingMethod(
			CommerceShippingMethod commerceShippingMethod,
			CommerceChannel commerceChannel, CommerceOrder commerceOrder)
		throws PortalException {

		return new ShippingMethod() {
			{
				setDescription(
					() -> commerceShippingMethod.getDescription(
						contextAcceptLanguage.getPreferredLocale()));
				setEngineKey(commerceShippingMethod::getEngineKey);
				setId(commerceShippingMethod::getCommerceShippingMethodId);
				setName(
					() -> commerceShippingMethod.getName(
						contextAcceptLanguage.getPreferredLocale()));
				setShippingOptions(
					() -> _getShippingOptions(
						commerceShippingMethod, commerceChannel,
						commerceOrder));
			}
		};
	}

	private ShippingOption _toShippingOption(
			CommerceShippingOption commerceShippingOption,
			CommerceContext commerceContext)
		throws PortalException {

		BigDecimal commerceShippingOptionAmount =
			commerceShippingOption.getAmount();

		return new ShippingOption() {
			{
				setAmount(commerceShippingOptionAmount::doubleValue);
				setAmountFormatted(
					() -> _commercePriceFormatter.format(
						commerceContext.getCommerceCurrency(), true,
						contextAcceptLanguage.getPreferredLocale(),
						commerceShippingOption.getAmount()));
				setLabel(commerceShippingOption::getName);
				setName(commerceShippingOption::getKey);
			}
		};
	}

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceContextFactory _commerceContextFactory;

	@Reference
	private CommerceOrderHttpHelper _commerceOrderHttpHelper;

	@Reference
	private CommerceOrderService _commerceOrderService;

	@Reference
	private CommercePriceFormatter _commercePriceFormatter;

	@Reference
	private CommerceShippingEngineRegistry _commerceShippingEngineRegistry;

	@Reference
	private CommerceShippingMethodLocalService
		_commerceShippingMethodLocalService;

}