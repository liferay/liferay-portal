/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.cart.internal.resource.v1_0;

import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.context.CommerceContextFactory;
import com.liferay.commerce.exception.CommerceOrderStatusException;
import com.liferay.commerce.helper.CommerceWorkflowedModelHelper;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.model.CommerceOrderItemModel;
import com.liferay.commerce.model.CommerceShippingEngine;
import com.liferay.commerce.model.CommerceShippingMethod;
import com.liferay.commerce.model.CommerceShippingOption;
import com.liferay.commerce.order.CommerceOrderHttpHelper;
import com.liferay.commerce.order.engine.CommerceOrderEngine;
import com.liferay.commerce.order.status.CommerceOrderStatus;
import com.liferay.commerce.order.status.CommerceOrderStatusRegistry;
import com.liferay.commerce.payment.model.CommercePaymentMethodGroupRel;
import com.liferay.commerce.payment.service.CommercePaymentMethodGroupRelLocalService;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceOrderNoteLocalService;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.commerce.service.CommerceShippingMethodLocalService;
import com.liferay.commerce.shipping.engine.fixed.model.CommerceShippingFixedOption;
import com.liferay.commerce.shipping.engine.fixed.service.CommerceShippingFixedOptionLocalService;
import com.liferay.commerce.term.service.CommerceTermEntryLocalService;
import com.liferay.commerce.util.CommerceShippingEngineRegistry;
import com.liferay.headless.commerce.core.helper.ServiceContextHelper;
import com.liferay.headless.commerce.delivery.cart.dto.v1_0.CartTransition;
import com.liferay.headless.commerce.delivery.cart.internal.util.v1_0.GuestCommerceOrderUtil;
import com.liferay.headless.commerce.delivery.cart.resource.v1_0.CartTransitionResource;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/cart-transition.properties",
	scope = ServiceScope.PROTOTYPE, service = CartTransitionResource.class
)
public class CartTransitionResourceImpl extends BaseCartTransitionResourceImpl {

	@Override
	public Page<CartTransition> getCartCartTransitionsPage(Long cartId)
		throws Exception {

		CommerceOrder commerceOrder = _commerceOrderService.getCommerceOrder(
			cartId);

		GuestCommerceOrderUtil.checkGuestCommerceOrder(
			commerceOrder, _commerceOrderHttpHelper, contextHttpServletRequest);

		if (!commerceOrder.isOpen()) {
			throw new CommerceOrderStatusException(
				"Unable to get order transitions of a placed order");
		}

		List<ObjectValuePair<Long, String>> transitionOVPs = new ArrayList<>();

		CommerceOrderStatus quoteRequestedCommerceOrderStatus =
			_commerceOrderStatusRegistry.getCommerceOrderStatus(
				CommerceOrderConstants.ORDER_STATUS_QUOTE_REQUESTED);

		if (quoteRequestedCommerceOrderStatus.isTransitionCriteriaMet(
				commerceOrder)) {

			transitionOVPs.add(new ObjectValuePair<>(0L, "request-quote"));
		}

		transitionOVPs.addAll(
			_commerceWorkflowedModelHelper.getWorkflowTransitions(
				contextUser.getUserId(), commerceOrder.getCompanyId(),
				commerceOrder.getModelClassName(),
				commerceOrder.getCommerceOrderId()));

		CommerceOrderStatus inProgressCommerceOrderStatus =
			_commerceOrderStatusRegistry.getCommerceOrderStatus(
				CommerceOrderConstants.ORDER_STATUS_IN_PROGRESS);

		if (inProgressCommerceOrderStatus.isTransitionCriteriaMet(
				commerceOrder)) {

			boolean priceOnApplication = ListUtil.exists(
				commerceOrder.getCommerceOrderItems(),
				CommerceOrderItemModel::isPriceOnApplication);

			if (commerceOrder.isApproved() && !priceOnApplication) {
				transitionOVPs.add(new ObjectValuePair<>(0L, "checkout"));
			}
			else if (commerceOrder.isDraft()) {
				transitionOVPs.add(new ObjectValuePair<>(0L, "submit"));
			}

			List<CommerceOrderItem> commerceOrderItems =
				commerceOrder.getCommerceOrderItems();

			if ((commerceOrder.getBillingAddressId() > 0) &&
				Validator.isNotNull(
					commerceOrder.getCommercePaymentMethodKey()) &&
				!commerceOrderItems.isEmpty() &&
				_isShippable(commerceOrder, commerceOrderItems)) {

				int deliveryCommerceTermEntriesCount =
					_commerceTermEntryLocalService.
						getDeliveryCommerceTermEntriesCount(
							contextCompany.getCompanyId(),
							commerceOrder.getCommerceOrderTypeId(),
							_getCommerceShippingFixedOptionId(commerceOrder));
				int paymentCommerceTermEntriesCount =
					_commerceTermEntryLocalService.
						getPaymentCommerceTermEntriesCount(
							contextCompany.getCompanyId(),
							commerceOrder.getCommerceOrderTypeId(),
							_getCommercePaymentMethodGroupRelId(commerceOrder));

				if (((deliveryCommerceTermEntriesCount == 0) ||
					 (commerceOrder.getDeliveryCommerceTermEntryId() > 0)) &&
					((paymentCommerceTermEntriesCount == 0) ||
					 (commerceOrder.getPaymentCommerceTermEntryId() > 0)) &&
					!priceOnApplication) {

					transitionOVPs.add(
						new ObjectValuePair<>(0L, "quick-checkout"));
				}
			}
		}

		return Page.of(
			transform(
				transitionOVPs,
				transitionOVP -> _toCartTransition(
					commerceOrder.getCommerceOrderId(), null, transitionOVP)),
			null, transitionOVPs.size());
	}

	@Override
	public CartTransition postCartCartTransition(
			Long cartId, CartTransition cartTransition)
		throws Exception {

		CommerceOrder commerceOrder = _commerceOrderService.getCommerceOrder(
			cartId);

		GuestCommerceOrderUtil.checkGuestCommerceOrder(
			commerceOrder, _commerceOrderHttpHelper, contextHttpServletRequest);

		if (!commerceOrder.isOpen()) {
			throw new CommerceOrderStatusException(
				"Unable to post order transition of a placed order");
		}

		String comment = GetterUtil.getString(cartTransition.getComment());
		String name = GetterUtil.getString(cartTransition.getName());

		long workflowTaskId = GetterUtil.getLong(
			cartTransition.getWorkflowTaskId());

		if (workflowTaskId > 0) {
			_commerceOrderService.executeWorkflowTransition(
				commerceOrder.getCommerceOrderId(), workflowTaskId, name,
				comment);
		}
		else if (name.equals("request-quote")) {
			if (Validator.isNotNull(comment)) {
				boolean restricted = GetterUtil.getBoolean(
					cartTransition.getRestricted());

				_commerceOrderNoteLocalService.addCommerceOrderNote(
					commerceOrder.getCommerceOrderId(), comment, restricted,
					_serviceContextHelper.getServiceContext(
						commerceOrder.getGroupId()));
			}

			_commerceOrderEngine.transitionCommerceOrder(
				commerceOrder,
				CommerceOrderConstants.ORDER_STATUS_QUOTE_REQUESTED,
				contextUser.getUserId(), true);
		}
		else if (name.equals("submit")) {
			_commerceOrderEngine.transitionCommerceOrder(
				commerceOrder, CommerceOrderConstants.ORDER_STATUS_IN_PROGRESS,
				contextUser.getUserId(), true);
		}

		return _toCartTransition(
			commerceOrder.getCommerceOrderId(), comment,
			new ObjectValuePair<>(workflowTaskId, name));
	}

	private long _getCommercePaymentMethodGroupRelId(
		CommerceOrder commerceOrder) {

		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel =
			_commercePaymentMethodGroupRelLocalService.
				fetchCommercePaymentMethodGroupRel(
					commerceOrder.getGroupId(),
					commerceOrder.getCommercePaymentMethodKey());

		if (commercePaymentMethodGroupRel == null) {
			return 0;
		}

		return commercePaymentMethodGroupRel.
			getCommercePaymentMethodGroupRelId();
	}

	private long _getCommerceShippingFixedOptionId(CommerceOrder commerceOrder)
		throws Exception {

		CommerceShippingMethod commerceShippingMethod =
			_commerceShippingMethodLocalService.fetchCommerceShippingMethod(
				commerceOrder.getCommerceShippingMethodId());

		if (commerceShippingMethod == null) {
			return 0;
		}

		CommerceShippingEngine commerceShippingEngine =
			_commerceShippingEngineRegistry.getCommerceShippingEngine(
				commerceShippingMethod.getEngineKey());

		if (commerceShippingEngine == null) {
			return 0;
		}

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.fetchCommerceChannelByGroupClassPK(
				commerceOrder.getGroupId());

		if (commerceChannel == null) {
			return 0;
		}

		for (CommerceShippingOption commerceShippingOption :
				commerceShippingEngine.getCommerceShippingOptions(
					_commerceContextFactory.create(
						commerceOrder.getCommerceAccountId(),
						commerceChannel.getGroupId(), null,
						commerceOrder.getCommerceOrderId(),
						contextCompany.getCompanyId()),
					commerceOrder,
					contextAcceptLanguage.getPreferredLocale())) {

			if (!StringUtil.equals(
					commerceOrder.getShippingOptionName(),
					commerceShippingOption.getKey())) {

				continue;
			}

			CommerceShippingFixedOption commerceShippingFixedOption =
				_commerceShippingFixedOptionLocalService.
					fetchCommerceShippingFixedOption(
						commerceOrder.getCompanyId(),
						commerceShippingOption.getKey());

			if (commerceShippingFixedOption != null) {
				return commerceShippingFixedOption.
					getCommerceShippingFixedOptionId();
			}
		}

		return 0;
	}

	private boolean _isShippable(
		CommerceOrder commerceOrder,
		List<CommerceOrderItem> commerceOrderItems) {

		for (CommerceOrderItem commerceOrderItem : commerceOrderItems) {
			if (commerceOrderItem.isShippable()) {
				if ((commerceOrder.getShippingAddressId() > 0) &&
					(commerceOrder.getCommerceShippingMethodId() > 0) &&
					Validator.isNotNull(
						commerceOrder.getShippingOptionName())) {

					return true;
				}

				return false;
			}
		}

		return true;
	}

	private CartTransition _toCartTransition(
			long commerceOrderId, String comment,
			ObjectValuePair<Long, String> transitionOVP)
		throws Exception {

		DefaultDTOConverterContext defaultDTOConverterContext =
			new DefaultDTOConverterContext(
				false, new HashMap<>(), _dtoConverterRegistry,
				contextHttpServletRequest, commerceOrderId,
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser);

		defaultDTOConverterContext.setAttribute("comment", comment);
		defaultDTOConverterContext.setAttribute("transitionOVP", transitionOVP);

		return _cartTransitionDTOConverter.toDTO(defaultDTOConverterContext);
	}

	@Reference(
		target = "(component.name=com.liferay.headless.commerce.delivery.cart.internal.dto.v1_0.converter.CartTransitionDTOConverter)"
	)
	private DTOConverter<CommerceOrder, CartTransition>
		_cartTransitionDTOConverter;

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceContextFactory _commerceContextFactory;

	@Reference
	private CommerceOrderEngine _commerceOrderEngine;

	@Reference
	private CommerceOrderHttpHelper _commerceOrderHttpHelper;

	@Reference
	private CommerceOrderNoteLocalService _commerceOrderNoteLocalService;

	@Reference
	private CommerceOrderService _commerceOrderService;

	@Reference
	private CommerceOrderStatusRegistry _commerceOrderStatusRegistry;

	@Reference
	private CommercePaymentMethodGroupRelLocalService
		_commercePaymentMethodGroupRelLocalService;

	@Reference
	private CommerceShippingEngineRegistry _commerceShippingEngineRegistry;

	@Reference
	private CommerceShippingFixedOptionLocalService
		_commerceShippingFixedOptionLocalService;

	@Reference
	private CommerceShippingMethodLocalService
		_commerceShippingMethodLocalService;

	@Reference
	private CommerceTermEntryLocalService _commerceTermEntryLocalService;

	@Reference
	private CommerceWorkflowedModelHelper _commerceWorkflowedModelHelper;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private ServiceContextHelper _serviceContextHelper;

}