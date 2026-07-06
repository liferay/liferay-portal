/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.order.engine;

import com.liferay.account.model.AccountEntry;
import com.liferay.commerce.configuration.CommerceOrderCheckoutConfiguration;
import com.liferay.commerce.constants.CommerceConstants;
import com.liferay.commerce.constants.CommerceOrderActionKeys;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.constants.CommerceOrderPaymentConstants;
import com.liferay.commerce.constants.CommercePaymentMethodConstants;
import com.liferay.commerce.constants.CommerceShipmentConstants;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.context.CommerceContextFactory;
import com.liferay.commerce.discount.exception.CommerceDiscountLimitationTimesException;
import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.service.CommerceDiscountLocalService;
import com.liferay.commerce.discount.service.CommerceDiscountUsageEntryLocalService;
import com.liferay.commerce.exception.CommerceOrderBillingAddressException;
import com.liferay.commerce.exception.CommerceOrderGuestCheckoutException;
import com.liferay.commerce.exception.CommerceOrderPriceException;
import com.liferay.commerce.exception.CommerceOrderShippingAddressException;
import com.liferay.commerce.exception.CommerceOrderShippingMethodException;
import com.liferay.commerce.exception.CommerceOrderStatusException;
import com.liferay.commerce.exception.CommerceOrderValidatorException;
import com.liferay.commerce.helper.CommerceShippingHelper;
import com.liferay.commerce.internal.order.status.CompletedCommerceOrderStatusImpl;
import com.liferay.commerce.internal.order.status.PartiallyShippedCommerceOrderStatusImpl;
import com.liferay.commerce.internal.order.status.ShippedCommerceOrderStatusImpl;
import com.liferay.commerce.inventory.model.CommerceInventoryBookedQuantity;
import com.liferay.commerce.inventory.service.CommerceInventoryBookedQuantityLocalService;
import com.liferay.commerce.inventory.type.constants.CommerceInventoryAuditTypeConstants;
import com.liferay.commerce.model.CommerceAddress;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.model.CommerceOrderItemModel;
import com.liferay.commerce.model.CommerceShippingMethod;
import com.liferay.commerce.model.attributes.provider.CommerceModelAttributesProvider;
import com.liferay.commerce.notification.CommerceNotificationSender;
import com.liferay.commerce.order.CommerceOrderValidatorRegistry;
import com.liferay.commerce.order.engine.CommerceOrderEngine;
import com.liferay.commerce.order.status.CommerceOrderStatus;
import com.liferay.commerce.order.status.CommerceOrderStatusRegistry;
import com.liferay.commerce.payment.method.CommercePaymentMethod;
import com.liferay.commerce.payment.method.CommercePaymentMethodRegistry;
import com.liferay.commerce.service.CommerceAddressLocalService;
import com.liferay.commerce.service.CommerceOrderItemLocalService;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.service.CommerceShipmentLocalService;
import com.liferay.commerce.service.CommerceShippingMethodLocalService;
import com.liferay.commerce.subscription.CommerceSubscriptionEntryHelperUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBusUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionCallbackUtil;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.extension.EntityExtensionHandler;
import com.liferay.portal.vulcan.extension.ExtensionProviderRegistry;
import com.liferay.portal.vulcan.extension.util.ExtensionUtil;

import java.io.Serializable;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alec Sloan
 */
@Component(service = CommerceOrderEngine.class)
public class CommerceOrderEngineImpl implements CommerceOrderEngine {

	@Override
	public CommerceOrder checkCommerceOrderShipmentStatus(
			CommerceOrder commerceOrder, boolean secure)
		throws PortalException {

		return _executeInTransaction(
			new Callable<CommerceOrder>() {

				@Override
				public CommerceOrder call() throws Exception {
					return _checkCommerceOrderShipmentStatus(
						commerceOrder, secure);
				}

			});
	}

	@Override
	public CommerceOrder checkoutCommerceOrder(
			CommerceOrder commerceOrder, long userId)
		throws PortalException {

		return _executeInTransaction(
			new Callable<CommerceOrder>() {

				@Override
				public CommerceOrder call() throws Exception {
					return _checkoutCommerceOrder(commerceOrder, userId);
				}

			});
	}

	@Override
	public CommerceOrderStatus getCurrentCommerceOrderStatus(
		CommerceOrder commerceOrder) {

		return _commerceOrderStatusRegistry.getCommerceOrderStatus(
			commerceOrder.getOrderStatus());
	}

	@Override
	public List<CommerceOrderStatus> getNextCommerceOrderStatuses(
			CommerceOrder commerceOrder)
		throws PortalException {

		List<CommerceOrderStatus> nextCommerceOrderStatuses = new ArrayList<>();

		CommerceOrderStatus currentCommerceOrderStatus =
			_commerceOrderStatusRegistry.getCommerceOrderStatus(
				commerceOrder.getOrderStatus());

		if (currentCommerceOrderStatus == null) {
			return nextCommerceOrderStatuses;
		}

		if (currentCommerceOrderStatus.getKey() ==
				CommerceOrderConstants.ORDER_STATUS_ON_HOLD) {

			nextCommerceOrderStatuses.add(
				_commerceOrderStatusRegistry.getCommerceOrderStatus(
					CommerceOrderConstants.ORDER_STATUS_ON_HOLD));

			return nextCommerceOrderStatuses;
		}

		List<CommerceOrderStatus> commerceOrderStatuses =
			_commerceOrderStatusRegistry.getCommerceOrderStatuses(
				commerceOrder);

		int currentOrderStatusIndex = commerceOrderStatuses.indexOf(
			currentCommerceOrderStatus);

		if (currentOrderStatusIndex == (commerceOrderStatuses.size() - 1)) {
			return nextCommerceOrderStatuses;
		}

		CommerceOrderStatus nextCommerceOrderStatus = null;

		for (int i = currentOrderStatusIndex + 1;
			 i < commerceOrderStatuses.size(); i++) {

			if ((nextCommerceOrderStatus != null) &&
				(nextCommerceOrderStatus.getPriority() >
					currentCommerceOrderStatus.getPriority())) {

				break;
			}

			nextCommerceOrderStatus = commerceOrderStatuses.get(i);
		}

		for (CommerceOrderStatus commerceOrderStatus : commerceOrderStatuses) {
			if ((!_commerceShippingHelper.isShippable(commerceOrder) &&
				 commerceOrderStatus.isValidForOrder(commerceOrder) &&
				 (commerceOrderStatus.getPriority() >
					 currentCommerceOrderStatus.getPriority())) ||
				((((commerceOrderStatus.getPriority() ==
					CommerceOrderConstants.ORDER_STATUS_ANY) &&
				   (currentCommerceOrderStatus.getKey() !=
					   CommerceOrderConstants.ORDER_STATUS_OPEN)) ||
				  (commerceOrderStatus.getPriority() ==
					  nextCommerceOrderStatus.getPriority())) &&
				 commerceOrderStatus.isTransitionCriteriaMet(commerceOrder))) {

				nextCommerceOrderStatuses.add(commerceOrderStatus);
			}
		}

		return nextCommerceOrderStatuses;
	}

	@Override
	public CommerceOrder transitionCommerceOrder(
			CommerceOrder commerceOrder, int orderStatus, long userId,
			boolean secure)
		throws PortalException {

		return _executeInTransaction(
			new Callable<CommerceOrder>() {

				@Override
				public CommerceOrder call() throws Exception {
					return _transitionCommerceOrder(
						commerceOrder, orderStatus, userId, secure);
				}

			});
	}

	@Override
	public CommerceOrder updateCommerceOrder(
			String externalReferenceCode, long commerceOrderId,
			long billingAddressId, long commerceShippingMethodId,
			long shippingAddressId, String advanceStatus,
			String commercePaymentMethodKey, String name,
			String purchaseOrderNumber, BigDecimal shippingAmount,
			String shippingOptionName, BigDecimal shippingWithTaxAmount,
			BigDecimal subtotal, BigDecimal subtotalWithTaxAmount,
			BigDecimal taxAmount, BigDecimal total,
			BigDecimal totalDiscountAmount, BigDecimal totalWithTaxAmount,
			CommerceContext commerceContext, boolean recalculatePrice)
		throws PortalException {

		try {
			return _executeInTransaction(
				() -> {
					CommerceOrder updatedCommerceOrder =
						_commerceOrderLocalService.updateCommerceOrder(
							externalReferenceCode, commerceOrderId,
							billingAddressId, commerceShippingMethodId,
							shippingAddressId, advanceStatus,
							commercePaymentMethodKey, name, purchaseOrderNumber,
							shippingAmount, shippingOptionName,
							shippingWithTaxAmount, subtotal,
							subtotalWithTaxAmount, taxAmount, total,
							totalDiscountAmount, totalWithTaxAmount);

					if (recalculatePrice) {
						updatedCommerceOrder =
							_commerceOrderLocalService.recalculatePrice(
								updatedCommerceOrder.getCommerceOrderId(),
								commerceContext);
					}

					return updatedCommerceOrder;
				});
		}
		catch (Throwable throwable) {
			throw new PortalException(throwable);
		}
	}

	private void _bookQuantities(long commerceOrderId) throws Exception {
		CommerceOrder commerceOrder =
			_commerceOrderLocalService.getCommerceOrder(commerceOrderId);

		List<CommerceOrderItem> commerceOrderItems =
			commerceOrder.getCommerceOrderItems();

		AccountEntry accountEntry = commerceOrder.getAccountEntry();

		for (CommerceOrderItem commerceOrderItem : commerceOrderItems) {
			CommerceInventoryBookedQuantity commerceInventoryBookedQuantity =
				_commerceInventoryBookedQuantityLocalService.
					addCommerceInventoryBookedQuantity(
						commerceOrderItem.getUserId(), null,
						commerceOrderItem.getQuantity(),
						commerceOrderItem.getSku(),
						commerceOrderItem.getUnitOfMeasureKey(),
						HashMapBuilder.put(
							CommerceInventoryAuditTypeConstants.ACCOUNT_NAME,
							accountEntry.getName()
						).put(
							CommerceInventoryAuditTypeConstants.ORDER_ID,
							String.valueOf(
								commerceOrderItem.getCommerceOrderId())
						).put(
							CommerceInventoryAuditTypeConstants.ORDER_ITEM_ID,
							String.valueOf(
								commerceOrderItem.getCommerceOrderItemId())
						).build());

			_commerceOrderItemLocalService.updateCommerceOrderItem(
				commerceOrderItem.getCommerceOrderItemId(),
				commerceInventoryBookedQuantity.
					getCommerceInventoryBookedQuantityId());
		}
	}

	private CommerceOrder _checkCommerceOrderShipmentStatus(
			CommerceOrder commerceOrder, boolean secure)
		throws Exception {

		CommerceOrderStatus partiallyShippedCommerceOrderStatus =
			_commerceOrderStatusRegistry.getCommerceOrderStatus(
				PartiallyShippedCommerceOrderStatusImpl.KEY);

		CommerceOrderStatus shippedCommerceOrderStatus =
			_commerceOrderStatusRegistry.getCommerceOrderStatus(
				ShippedCommerceOrderStatusImpl.KEY);

		CommerceOrderStatus completedCommerceOrderStatus =
			_commerceOrderStatusRegistry.getCommerceOrderStatus(
				CompletedCommerceOrderStatusImpl.KEY);

		int[] commerceShipmentStatuses =
			_commerceShipmentLocalService.
				getCommerceShipmentStatusesByCommerceOrderId(
					commerceOrder.getCommerceOrderId());

		if (completedCommerceOrderStatus.isTransitionCriteriaMet(
				commerceOrder) &&
			(commerceShipmentStatuses.length == 1) &&
			(commerceShipmentStatuses[0] ==
				CommerceShipmentConstants.SHIPMENT_STATUS_DELIVERED)) {

			commerceOrder = transitionCommerceOrder(
				commerceOrder, CommerceOrderConstants.ORDER_STATUS_COMPLETED, 0,
				secure);
		}
		else if (shippedCommerceOrderStatus.isTransitionCriteriaMet(
					commerceOrder)) {

			commerceOrder = transitionCommerceOrder(
				commerceOrder, CommerceOrderConstants.ORDER_STATUS_SHIPPED, 0,
				secure);
		}
		else if (partiallyShippedCommerceOrderStatus.isTransitionCriteriaMet(
					commerceOrder)) {

			commerceOrder = transitionCommerceOrder(
				commerceOrder,
				CommerceOrderConstants.ORDER_STATUS_PARTIALLY_SHIPPED, 0,
				secure);
		}

		return commerceOrder;
	}

	private CommerceOrder _checkoutCommerceOrder(
			CommerceOrder commerceOrder, long userId)
		throws Exception {

		if (commerceOrder.isGuestOrder() &&
			!_isGuestCheckoutEnabled(commerceOrder.getGroupId())) {

			throw new CommerceOrderGuestCheckoutException();
		}

		_commerceOrderModelResourcePermission.check(
			PermissionThreadLocal.getPermissionChecker(), commerceOrder,
			CommerceOrderActionKeys.CHECKOUT_COMMERCE_ORDER);

		CommerceOrderStatus currentCommerceOrderStatus =
			_commerceOrderStatusRegistry.getCommerceOrderStatus(
				commerceOrder.getOrderStatus());

		if ((currentCommerceOrderStatus == null) ||
			(currentCommerceOrderStatus.getKey() !=
				CommerceOrderConstants.ORDER_STATUS_OPEN)) {

			throw new CommerceOrderStatusException();
		}

		_validateCheckout(commerceOrder);

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setScopeGroupId(commerceOrder.getGroupId());

		if (userId == 0) {
			User guestUser = _userLocalService.getGuestUser(
				commerceOrder.getCompanyId());

			userId = guestUser.getUserId();
		}

		serviceContext.setUserId(userId);

		long commerceOrderId = commerceOrder.getCommerceOrderId();

		CommerceContext commerceContext = _commerceContextFactory.create(
			commerceOrder.getCommerceAccountId(), commerceOrder.getGroupId(),
			commerceOrder.getCommerceCurrencyCode(), commerceOrderId,
			commerceOrder.getCompanyId());

		TransactionCallbackUtil.registerCommitCallback(
			() -> {
				_bookQuantities(commerceOrderId);

				return null;
			});

		commerceOrder = _commerceOrderLocalService.recalculatePrice(
			commerceOrderId, commerceContext);

		commerceOrder.setOrderDate(new Date());

		_updateCommerceDiscountUsageEntry(
			commerceOrder.getCompanyId(), commerceOrder.getCommerceAccountId(),
			commerceOrderId, commerceOrder.getCouponCode(), serviceContext);

		// Commerce addresses

		if (commerceOrder.getBillingAddressId() > 0) {
			CommerceAddress commerceAddress =
				_commerceAddressLocalService.copyCommerceAddress(
					commerceOrder.getBillingAddressId(),
					commerceOrder.getModelClassName(), commerceOrderId,
					serviceContext);

			commerceOrder.setBillingAddressId(
				commerceAddress.getCommerceAddressId());
		}

		if (commerceOrder.getShippingAddressId() > 0) {
			CommerceAddress commerceAddress =
				_commerceAddressLocalService.copyCommerceAddress(
					commerceOrder.getShippingAddressId(),
					commerceOrder.getModelClassName(), commerceOrderId,
					serviceContext);

			commerceOrder.setShippingAddressId(
				commerceAddress.getCommerceAddressId());
		}

		if (BigDecimalUtil.eq(commerceOrder.getTotal(), BigDecimal.ZERO)) {
			commerceOrder = _commerceOrderLocalService.updatePaymentStatus(
				userId, commerceOrder.getCommerceOrderId(),
				CommerceOrderPaymentConstants.STATUS_NOT_REQUIRED);

			return transitionCommerceOrder(
				commerceOrder, CommerceOrderConstants.ORDER_STATUS_PENDING,
				userId, true);
		}

		CommercePaymentMethod commercePaymentMethod =
			_commercePaymentMethodRegistry.getCommercePaymentMethod(
				commerceOrder.getCommercePaymentMethodKey());

		if ((commerceOrder.getPaymentStatus() ==
				CommerceOrderPaymentConstants.STATUS_COMPLETED) ||
			(commercePaymentMethod == null) ||
			((commercePaymentMethod != null) &&
			 (commercePaymentMethod.getPaymentType() ==
				 CommercePaymentMethodConstants.TYPE_OFFLINE) &&
			 (commerceOrder.getPaymentStatus() ==
				 CommerceOrderPaymentConstants.STATUS_PENDING))) {

			return transitionCommerceOrder(
				commerceOrder, CommerceOrderConstants.ORDER_STATUS_PENDING,
				userId, true);
		}

		return transitionCommerceOrder(
			commerceOrder, CommerceOrderConstants.ORDER_STATUS_IN_PROGRESS,
			userId, true);
	}

	private CommerceOrder _executeInTransaction(
			Callable<CommerceOrder> callable)
		throws PortalException {

		try {
			return TransactionInvokerUtil.invoke(_transactionConfig, callable);
		}
		catch (Throwable throwable) {
			throw new PortalException(throwable);
		}
	}

	private JSONObject _getCommerceOrderJSONObject(
			CommerceOrder commerceOrder,
			DTOConverter<?, ?> commerceOrderDTOConverter)
		throws Exception {

		DefaultDTOConverterContext dtoConverterContext =
			new DefaultDTOConverterContext(
				_dtoConverterRegistry, commerceOrder.getCommerceOrderId(),
				LocaleUtil.getSiteDefault(), null, null);

		dtoConverterContext.setAttribute("secure", Boolean.FALSE);

		JSONObject commerceOrderJSONObject = _jsonFactory.createJSONObject(
			_jsonFactory.looseSerializeDeep(
				commerceOrderDTOConverter.toDTO(dtoConverterContext)));

		JSONArray commerceOrderItemsJSONArray = _jsonFactory.createJSONArray();

		DTOConverter<?, ?> commerceOrderItemDTOConverter =
			_dtoConverterRegistry.getDTOConverter(
				"Liferay.Headless.Commerce.Admin.Order",
				CommerceOrderItem.class.getName(), "v1.0");

		List<CommerceOrderItem> commerceOrderItems =
			commerceOrder.getCommerceOrderItems();

		for (CommerceOrderItem commerceOrderItem : commerceOrderItems) {
			dtoConverterContext = new DefaultDTOConverterContext(
				_dtoConverterRegistry,
				commerceOrderItem.getCommerceOrderItemId(),
				LocaleUtil.getSiteDefault(), null, null);

			dtoConverterContext.setAttribute("secure", Boolean.FALSE);

			JSONObject commerceOrderItemJSONObject =
				_jsonFactory.createJSONObject(
					_jsonFactory.looseSerializeDeep(
						commerceOrderItemDTOConverter.toDTO(
							dtoConverterContext)));

			commerceOrderItemsJSONArray.put(commerceOrderItemJSONObject);
		}

		commerceOrderJSONObject.put("orderItems", commerceOrderItemsJSONArray);

		return commerceOrderJSONObject;
	}

	private boolean _isGuestCheckoutEnabled(long groupId) throws Exception {
		CommerceOrderCheckoutConfiguration commerceOrderCheckoutConfiguration =
			_configurationProvider.getConfiguration(
				CommerceOrderCheckoutConfiguration.class,
				new GroupServiceSettingsLocator(
					groupId, CommerceConstants.SERVICE_NAME_COMMERCE_ORDER));

		return commerceOrderCheckoutConfiguration.guestCheckoutEnabled();
	}

	private void _sendOrderStatusMessage(
		CommerceOrder commerceOrder, int orderStatus, long userId) {

		CommerceOrder originalCommerceOrder =
			commerceOrder.cloneWithOriginalValues();

		TransactionCallbackUtil.registerCommitCallback(
			() -> {
				if ((orderStatus ==
						CommerceOrderConstants.ORDER_STATUS_PENDING) &&
					(commerceOrder.getPaymentStatus() ==
						CommerceOrderPaymentConstants.STATUS_COMPLETED)) {

					CommerceSubscriptionEntryHelperUtil.
						checkCommerceSubscriptions(commerceOrder);
				}

				_commerceNotificationSender.sendNotifications(
					commerceOrder.getGroupId(), commerceOrder.getUserId(),
					CommerceOrderConstants.getNotificationKey(orderStatus),
					commerceOrder);

				Message message = new Message();

				DTOConverter<?, ?> commerceOrderDTOConverter =
					_dtoConverterRegistry.getDTOConverter(
						"Liferay.Headless.Commerce.Admin.Order",
						CommerceOrder.class.getName(), "v1.0");

				EntityExtensionHandler entityExtensionHandler =
					ExtensionUtil.getEntityExtensionHandler(
						commerceOrderDTOConverter.getExternalDTOClassName(),
						commerceOrder.getCompanyId(),
						_extensionProviderRegistry);

				Map<String, Serializable> extendedProperties = null;

				if (entityExtensionHandler != null) {
					extendedProperties =
						entityExtensionHandler.getExtendedProperties(
							commerceOrder.getCompanyId(), userId,
							HashMapBuilder.<String, Object>put(
								"id", commerceOrder.getCommerceOrderId()
							).build());
				}

				message.setPayload(
					JSONUtil.put(
						"classPK", commerceOrder.getCommerceOrderId()
					).put(
						"commerceOrder",
						_getCommerceOrderJSONObject(
							commerceOrder, commerceOrderDTOConverter)
					).put(
						"commerceOrderId", commerceOrder.getCommerceOrderId()
					).put(
						"extendedProperties", extendedProperties
					).put(
						"model" + CommerceOrder.class.getSimpleName(),
						commerceOrder.getModelAttributes()
					).put(
						"modelDTO" + commerceOrderDTOConverter.getContentType(),
						_commerceModelAttributesProvider.getModelAttributes(
							commerceOrder, commerceOrderDTOConverter,
							commerceOrder.getUserId())
					).put(
						"orderStatus", commerceOrder.getOrderStatus()
					).put(
						"originalCommerceOrder",
						originalCommerceOrder.getModelAttributes()
					));

				MessageBusUtil.sendMessage(
					DestinationNames.COMMERCE_ORDER_STATUS, message);

				return null;
			});
	}

	private CommerceOrder _transitionCommerceOrder(
			CommerceOrder commerceOrder, int orderStatus, long userId,
			boolean secure)
		throws Exception {

		CommerceOrderStatus commerceOrderStatus =
			_commerceOrderStatusRegistry.getCommerceOrderStatus(orderStatus);

		if (commerceOrderStatus == null) {
			throw new CommerceOrderStatusException();
		}

		if ((commerceOrderStatus.getKey() ==
				CommerceOrderConstants.ORDER_STATUS_CANCELLED) &&
			commerceOrderStatus.isTransitionCriteriaMet(commerceOrder)) {

			_sendOrderStatusMessage(
				commerceOrder, commerceOrderStatus.getKey(), userId);

			return commerceOrderStatus.doTransition(
				commerceOrder, userId, secure);
		}

		CommerceOrderStatus currentCommerceOrderStatus =
			_commerceOrderStatusRegistry.getCommerceOrderStatus(
				commerceOrder.getOrderStatus());

		if (!currentCommerceOrderStatus.isComplete(commerceOrder) ||
			!commerceOrderStatus.isTransitionCriteriaMet(commerceOrder) ||
			((currentCommerceOrderStatus.getKey() ==
				CommerceOrderConstants.ORDER_STATUS_ON_HOLD) &&
			 (commerceOrderStatus.getKey() !=
				 CommerceOrderConstants.ORDER_STATUS_ON_HOLD) &&
			 (commerceOrderStatus.getKey() !=
				 CommerceOrderConstants.ORDER_STATUS_PROCESSING))) {

			throw new CommerceOrderStatusException();
		}

		_sendOrderStatusMessage(
			commerceOrder, commerceOrderStatus.getKey(), userId);

		return commerceOrderStatus.doTransition(commerceOrder, userId, secure);
	}

	private void _updateCommerceDiscountUsageEntry(
			long companyId, long commerceAccountId, long commerceOrderId,
			String couponCode, ServiceContext serviceContext)
		throws Exception {

		if (!Validator.isBlank(couponCode)) {
			CommerceDiscount commerceDiscount =
				_commerceDiscountLocalService.getActiveCommerceDiscount(
					companyId, couponCode, true);

			if (!_commerceDiscountUsageEntryLocalService.
					validateDiscountLimitationUsage(
						commerceAccountId,
						commerceDiscount.getCommerceDiscountId())) {

				throw new CommerceDiscountLimitationTimesException();
			}

			_commerceDiscountUsageEntryLocalService.
				addCommerceDiscountUsageEntry(
					commerceAccountId, commerceOrderId,
					commerceDiscount.getCommerceDiscountId(), serviceContext);
		}
	}

	private void _validateCheckout(CommerceOrder commerceOrder)
		throws Exception {

		if (ListUtil.exists(
				commerceOrder.getCommerceOrderItems(),
				CommerceOrderItemModel::isPriceOnApplication)) {

			throw new CommerceOrderPriceException();
		}

		if (!_commerceOrderValidatorRegistry.isValid(
				LocaleUtil.getSiteDefault(), commerceOrder)) {

			throw new CommerceOrderValidatorException();
		}

		if (commerceOrder.isB2B() &&
			(commerceOrder.getBillingAddressId() <= 0)) {

			throw new CommerceOrderBillingAddressException();
		}

		CommerceShippingMethod commerceShippingMethod = null;

		long commerceShippingMethodId =
			commerceOrder.getCommerceShippingMethodId();

		if (commerceShippingMethodId > 0) {
			commerceShippingMethod =
				_commerceShippingMethodLocalService.getCommerceShippingMethod(
					commerceShippingMethodId);

			if (!commerceShippingMethod.isActive()) {
				commerceShippingMethod = null;
			}
			else if (commerceOrder.getShippingAddressId() <= 0) {
				throw new CommerceOrderShippingAddressException();
			}
		}

		int count =
			_commerceShippingMethodLocalService.getCommerceShippingMethodsCount(
				commerceOrder.getGroupId(), true);

		if ((commerceShippingMethod == null) && (count > 0) &&
			_commerceShippingHelper.isShippable(commerceOrder) &&
			!_commerceShippingHelper.isFreeShipping(commerceOrder)) {

			throw new CommerceOrderShippingMethodException();
		}
	}

	private static final TransactionConfig _transactionConfig =
		TransactionConfig.Factory.create(
			Propagation.REQUIRED, new Class<?>[] {Exception.class});

	@Reference
	private CommerceAddressLocalService _commerceAddressLocalService;

	@Reference
	private CommerceContextFactory _commerceContextFactory;

	@Reference
	private CommerceDiscountLocalService _commerceDiscountLocalService;

	@Reference
	private CommerceDiscountUsageEntryLocalService
		_commerceDiscountUsageEntryLocalService;

	@Reference
	private CommerceInventoryBookedQuantityLocalService
		_commerceInventoryBookedQuantityLocalService;

	@Reference
	private CommerceModelAttributesProvider _commerceModelAttributesProvider;

	@Reference
	private CommerceNotificationSender _commerceNotificationSender;

	@Reference
	private CommerceOrderItemLocalService _commerceOrderItemLocalService;

	@Reference
	private CommerceOrderLocalService _commerceOrderLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.model.CommerceOrder)"
	)
	private ModelResourcePermission<CommerceOrder>
		_commerceOrderModelResourcePermission;

	@Reference
	private CommerceOrderStatusRegistry _commerceOrderStatusRegistry;

	@Reference
	private CommerceOrderValidatorRegistry _commerceOrderValidatorRegistry;

	@Reference
	private CommercePaymentMethodRegistry _commercePaymentMethodRegistry;

	@Reference
	private CommerceShipmentLocalService _commerceShipmentLocalService;

	@Reference
	private CommerceShippingHelper _commerceShippingHelper;

	@Reference
	private CommerceShippingMethodLocalService
		_commerceShippingMethodLocalService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private ExtensionProviderRegistry _extensionProviderRegistry;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private UserLocalService _userLocalService;

}