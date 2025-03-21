/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.checkout.web.internal.helper;

import com.liferay.account.model.AccountEntry;
import com.liferay.commerce.checkout.helper.CommerceCheckoutStepHttpHelper;
import com.liferay.commerce.constants.CommerceCheckoutWebKeys;
import com.liferay.commerce.constants.CommerceOrderActionKeys;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.currency.model.CommerceMoney;
import com.liferay.commerce.model.CommerceAddress;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.model.CommerceShippingEngine;
import com.liferay.commerce.model.CommerceShippingMethod;
import com.liferay.commerce.model.CommerceShippingOption;
import com.liferay.commerce.model.CommerceShippingOptionAccountEntryRel;
import com.liferay.commerce.order.CommerceOrderHttpHelper;
import com.liferay.commerce.payment.integration.CommercePaymentIntegration;
import com.liferay.commerce.payment.integration.CommercePaymentIntegrationRegistry;
import com.liferay.commerce.payment.method.CommercePaymentMethod;
import com.liferay.commerce.payment.method.CommercePaymentMethodRegistry;
import com.liferay.commerce.payment.model.CommercePaymentMethodGroupRel;
import com.liferay.commerce.payment.model.CommercePaymentMethodGroupRelQualifier;
import com.liferay.commerce.payment.service.CommercePaymentMethodGroupRelLocalService;
import com.liferay.commerce.payment.service.CommercePaymentMethodGroupRelQualifierLocalService;
import com.liferay.commerce.payment.util.comparator.CommercePaymentMethodPriorityComparator;
import com.liferay.commerce.price.CommerceOrderPrice;
import com.liferay.commerce.price.CommerceOrderPriceCalculation;
import com.liferay.commerce.product.constants.CommerceChannelAccountEntryRelConstants;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.model.CommerceChannelAccountEntryRel;
import com.liferay.commerce.product.service.CommerceChannelAccountEntryRelLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceAddressService;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.service.CommerceShippingMethodLocalService;
import com.liferay.commerce.service.CommerceShippingOptionAccountEntryRelService;
import com.liferay.commerce.shipping.engine.fixed.model.CommerceShippingFixedOption;
import com.liferay.commerce.shipping.engine.fixed.service.CommerceShippingFixedOptionLocalService;
import com.liferay.commerce.term.model.CommerceTermEntry;
import com.liferay.commerce.term.service.CommerceTermEntryLocalService;
import com.liferay.commerce.util.CommerceShippingEngineRegistry;
import com.liferay.commerce.util.CommerceShippingHelper;
import com.liferay.commerce.util.comparator.CommerceShippingMethodPriorityComparator;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 * @author Luca Pellizzon
 */
@Component(service = CommerceCheckoutStepHttpHelper.class)
public class DefaultCommerceCheckoutStepHttpHelper
	implements CommerceCheckoutStepHttpHelper {

	@Override
	public String getOrderDetailURL(
			HttpServletRequest httpServletRequest, CommerceOrder commerceOrder)
		throws PortalException {

		return _commerceOrderHttpHelper.getCommerceCartPortletURL(
			httpServletRequest, commerceOrder);
	}

	@Override
	public boolean isActiveBillingAddressCommerceCheckoutStep(
			HttpServletRequest httpServletRequest, CommerceOrder commerceOrder)
		throws PortalException {

		CommerceAddress defaultBillingCommerceAddress = null;
		CommerceAddress defaultShippingCommerceAddress = null;

		AccountEntry accountEntry = commerceOrder.getAccountEntry();

		if (accountEntry != null) {
			CommerceChannel commerceChannel =
				_commerceChannelLocalService.getCommerceChannelByOrderGroupId(
					commerceOrder.getGroupId());

			CommerceChannelAccountEntryRel
				billingAddressCommerceChannelAccountEntryRel =
					_commerceChannelAccountEntryRelLocalService.
						fetchCommerceChannelAccountEntryRel(
							accountEntry.getAccountEntryId(),
							commerceChannel.getCommerceChannelId(),
							CommerceChannelAccountEntryRelConstants.
								TYPE_BILLING_ADDRESS);

			if (billingAddressCommerceChannelAccountEntryRel != null) {
				defaultBillingCommerceAddress =
					_commerceAddressService.getCommerceAddress(
						billingAddressCommerceChannelAccountEntryRel.
							getClassPK());
			}

			CommerceChannelAccountEntryRel
				shippingAddressCommerceChannelAccountEntryRel =
					_commerceChannelAccountEntryRelLocalService.
						fetchCommerceChannelAccountEntryRel(
							accountEntry.getAccountEntryId(),
							commerceChannel.getCommerceChannelId(),
							CommerceChannelAccountEntryRelConstants.
								TYPE_SHIPPING_ADDRESS);

			if (shippingAddressCommerceChannelAccountEntryRel != null) {
				defaultShippingCommerceAddress =
					_commerceAddressService.getCommerceAddress(
						shippingAddressCommerceChannelAccountEntryRel.
							getClassPK());
			}
		}

		long defaultBillingCommerceAddressId = 0;
		long defaultShippingCommerceAddressId = 0;

		if (defaultBillingCommerceAddress != null) {
			defaultBillingCommerceAddressId =
				defaultBillingCommerceAddress.getCommerceAddressId();
		}

		if (defaultShippingCommerceAddress != null) {
			defaultShippingCommerceAddressId =
				defaultShippingCommerceAddress.getCommerceAddressId();
		}

		CommerceAddress billingCommerceAddress =
			commerceOrder.getBillingAddress();
		CommerceAddress shippingCommerceAddress =
			commerceOrder.getShippingAddress();

		if (((accountEntry != null) && (defaultBillingCommerceAddressId != 0) &&
			 (defaultShippingCommerceAddressId != 0) &&
			 (defaultBillingCommerceAddressId ==
				 defaultShippingCommerceAddressId) &&
			 (billingCommerceAddress == null) &&
			 (shippingCommerceAddress == null) &&
			 commerceOrder.isShippable()) ||
			((billingCommerceAddress != null) &&
			 (shippingCommerceAddress != null) &&
			 (billingCommerceAddress.getCommerceAddressId() ==
				 shippingCommerceAddress.getCommerceAddressId()))) {

			return false;
		}

		return true;
	}

	@Override
	public boolean isActiveDeliveryTermCommerceCheckoutStep(
			HttpServletRequest httpServletRequest, CommerceOrder commerceOrder,
			String languageId)
		throws PortalException {

		if (!commerceOrder.isOpen() ||
			(commerceOrder.getCommerceShippingMethodId() <= 0)) {

			return false;
		}

		CommerceChannelAccountEntryRel commerceChannelAccountEntryRel = null;

		CommerceContext commerceContext =
			(CommerceContext)httpServletRequest.getAttribute(
				CommerceWebKeys.COMMERCE_CONTEXT);

		AccountEntry accountEntry = commerceContext.getAccountEntry();

		if (accountEntry != null) {
			commerceChannelAccountEntryRel =
				_commerceChannelAccountEntryRelLocalService.
					fetchCommerceChannelAccountEntryRel(
						accountEntry.getAccountEntryId(),
						commerceContext.getCommerceChannelId(),
						CommerceChannelAccountEntryRelConstants.
							TYPE_DELIVERY_TERM);

			if ((commerceChannelAccountEntryRel != null) &&
				commerceChannelAccountEntryRel.isOverrideEligibility()) {

				CommerceTermEntry commerceTermEntry =
					_commerceTermEntryLocalService.getCommerceTermEntry(
						commerceChannelAccountEntryRel.getClassPK());

				commerceOrder =
					_commerceOrderLocalService.updateTermsAndConditions(
						commerceOrder.getCommerceOrderId(),
						commerceTermEntry.getCommerceTermEntryId(), 0,
						languageId);

				httpServletRequest.setAttribute(
					CommerceCheckoutWebKeys.COMMERCE_ORDER, commerceOrder);

				return false;
			}
		}

		CommerceShippingMethod commerceShippingMethod =
			_commerceShippingMethodLocalService.getCommerceShippingMethod(
				commerceOrder.getCommerceShippingMethodId());

		CommerceShippingEngine commerceShippingEngine =
			_commerceShippingEngineRegistry.getCommerceShippingEngine(
				commerceShippingMethod.getEngineKey());

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		List<CommerceShippingOption> commerceShippingOptions =
			commerceShippingEngine.getCommerceShippingOptions(
				commerceContext, commerceOrder, themeDisplay.getLocale());

		String shippingOptionName = commerceOrder.getShippingOptionName();

		List<CommerceTermEntry> deliveryCommerceTermEntries = null;

		for (CommerceShippingOption commerceShippingOption :
				commerceShippingOptions) {

			if (shippingOptionName.equals(commerceShippingOption.getKey())) {
				CommerceShippingFixedOption commerceShippingFixedOption =
					_commerceShippingFixedOptionLocalService.
						fetchCommerceShippingFixedOption(
							commerceOrder.getCompanyId(),
							commerceShippingOption.getKey());

				if (commerceShippingFixedOption != null) {
					deliveryCommerceTermEntries =
						_commerceTermEntryLocalService.
							getDeliveryCommerceTermEntries(
								commerceOrder.getCompanyId(),
								commerceOrder.getCommerceOrderTypeId(),
								commerceShippingFixedOption.
									getCommerceShippingFixedOptionId());
				}
			}
		}

		if (ListUtil.isEmpty(deliveryCommerceTermEntries)) {
			return false;
		}

		if (deliveryCommerceTermEntries.size() == 1) {
			if (commerceOrder.getDeliveryCommerceTermEntryId() > 0) {
				return false;
			}

			CommerceTermEntry commerceTermEntry =
				deliveryCommerceTermEntries.get(0);

			commerceOrder = _commerceOrderLocalService.updateTermsAndConditions(
				commerceOrder.getCommerceOrderId(),
				commerceTermEntry.getCommerceTermEntryId(), 0, languageId);

			httpServletRequest.setAttribute(
				CommerceCheckoutWebKeys.COMMERCE_ORDER, commerceOrder);

			return false;
		}

		CommerceTermEntry commerceTermEntry =
			_commerceTermEntryLocalService.fetchCommerceTermEntry(
				commerceOrder.getDeliveryCommerceTermEntryId());

		if ((commerceTermEntry == null) &&
			(commerceChannelAccountEntryRel != null)) {

			commerceTermEntry =
				_commerceTermEntryLocalService.fetchCommerceTermEntry(
					commerceChannelAccountEntryRel.getClassPK());
		}

		if ((commerceTermEntry != null) && commerceTermEntry.isActive() &&
			deliveryCommerceTermEntries.contains(commerceTermEntry)) {

			commerceOrder = _commerceOrderLocalService.updateTermsAndConditions(
				commerceOrder.getCommerceOrderId(),
				commerceTermEntry.getCommerceTermEntryId(), 0,
				_language.getLanguageId(_portal.getLocale(httpServletRequest)));

			httpServletRequest.setAttribute(
				CommerceCheckoutWebKeys.COMMERCE_ORDER, commerceOrder);
		}
		else {
			commerceTermEntry = deliveryCommerceTermEntries.get(0);

			commerceOrder = _commerceOrderLocalService.updateTermsAndConditions(
				commerceOrder.getCommerceOrderId(),
				commerceTermEntry.getCommerceTermEntryId(), 0, languageId);

			httpServletRequest.setAttribute(
				CommerceCheckoutWebKeys.COMMERCE_ORDER, commerceOrder);
		}

		return true;
	}

	@Override
	public boolean isActivePaymentMethodCommerceCheckoutStep(
			HttpServletRequest httpServletRequest, CommerceOrder commerceOrder)
		throws PortalException {

		List<CommercePaymentMethodGroupRel> commercePaymentMethodGroupRels =
			new ArrayList<>();

		CommerceAddress commerceAddress = commerceOrder.getBillingAddress();

		if (commerceAddress == null) {
			commerceAddress = commerceOrder.getShippingAddress();
		}

		if (commerceAddress != null) {
			commercePaymentMethodGroupRels.addAll(
				_commercePaymentMethodGroupRelLocalService.
					getCommercePaymentMethodGroupRels(
						commerceOrder.getGroupId(),
						commerceAddress.getCountryId(), true));
		}
		else {
			commercePaymentMethodGroupRels.addAll(
				_commercePaymentMethodGroupRelLocalService.
					getCommercePaymentMethodGroupRels(
						commerceOrder.getGroupId(), true));
		}

		commercePaymentMethodGroupRels = _filterCommercePaymentMethodGroupRels(
			commercePaymentMethodGroupRels,
			commerceOrder.getCommerceOrderTypeId(),
			commerceOrder.isSubscriptionOrder());

		if (commercePaymentMethodGroupRels.isEmpty()) {
			commerceOrder = _updateCommerceOrder(
				commerceOrder, StringPool.BLANK, httpServletRequest);

			return false;
		}

		CommerceContext commerceContext =
			(CommerceContext)httpServletRequest.getAttribute(
				CommerceWebKeys.COMMERCE_CONTEXT);

		CommerceOrderPrice commerceOrderPrice =
			_commerceOrderPriceCalculation.getCommerceOrderPrice(
				commerceOrder, commerceContext);

		CommerceMoney orderPriceTotalCommerceMoney =
			commerceOrderPrice.getTotal();

		if (BigDecimalUtil.isZero(orderPriceTotalCommerceMoney.getPrice())) {
			return false;
		}

		if (commercePaymentMethodGroupRels.size() == 1) {
			CommercePaymentMethodGroupRel commercePaymentMethodGroupRel =
				commercePaymentMethodGroupRels.get(0);

			commerceOrder = _updateCommerceOrder(
				commerceOrder,
				commercePaymentMethodGroupRel.getPaymentIntegrationKey(),
				httpServletRequest);

			return false;
		}

		AccountEntry accountEntry = commerceContext.getAccountEntry();

		if ((accountEntry != null) &&
			!commercePaymentMethodGroupRels.isEmpty()) {

			CommerceChannelAccountEntryRel commerceChannelAccountEntryRel =
				_commerceChannelAccountEntryRelLocalService.
					fetchCommerceChannelAccountEntryRel(
						accountEntry.getAccountEntryId(),
						commerceContext.getCommerceChannelId(),
						CommerceChannelAccountEntryRelConstants.TYPE_PAYMENT);

			if (commerceChannelAccountEntryRel != null) {
				CommercePaymentMethodGroupRel commercePaymentMethodGroupRel =
					_commercePaymentMethodGroupRelLocalService.
						fetchCommercePaymentMethodGroupRel(
							commerceChannelAccountEntryRel.getClassPK());

				if ((commercePaymentMethodGroupRel != null) &&
					commercePaymentMethodGroupRel.isActive() &&
					commercePaymentMethodGroupRels.contains(
						commercePaymentMethodGroupRel) &&
					Validator.isNull(
						commerceOrder.getCommercePaymentMethodKey())) {

					commerceOrder = _updateCommerceOrder(
						commerceOrder,
						commercePaymentMethodGroupRel.
							getPaymentIntegrationKey(),
						httpServletRequest);
				}
			}
		}

		if (Validator.isNull(commerceOrder.getCommercePaymentMethodKey())) {
			CommercePaymentMethodGroupRel commercePaymentMethodGroupRel =
				commercePaymentMethodGroupRels.get(0);

			commerceOrder = _updateCommerceOrder(
				commerceOrder,
				commercePaymentMethodGroupRel.getPaymentIntegrationKey(),
				httpServletRequest);
		}

		return _hasCommerceOrderPermission(
			CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_PAYMENT_METHODS,
			commerceOrder, httpServletRequest);
	}

	@Override
	public boolean isActivePaymentTermCommerceCheckoutStep(
			CommerceOrder commerceOrder, HttpServletRequest httpServletRequest,
			String languageId)
		throws PortalException {

		String commercePaymentMethodKey =
			commerceOrder.getCommercePaymentMethodKey();

		if (!commerceOrder.isOpen() ||
			Validator.isNull(commercePaymentMethodKey)) {

			return false;
		}

		CommerceChannelAccountEntryRel commerceChannelAccountEntryRel = null;

		CommerceContext commerceContext =
			(CommerceContext)httpServletRequest.getAttribute(
				CommerceWebKeys.COMMERCE_CONTEXT);

		AccountEntry accountEntry = commerceContext.getAccountEntry();

		if (accountEntry != null) {
			commerceChannelAccountEntryRel =
				_commerceChannelAccountEntryRelLocalService.
					fetchCommerceChannelAccountEntryRel(
						accountEntry.getAccountEntryId(),
						commerceContext.getCommerceChannelId(),
						CommerceChannelAccountEntryRelConstants.
							TYPE_PAYMENT_TERM);

			if ((commerceChannelAccountEntryRel != null) &&
				commerceChannelAccountEntryRel.isOverrideEligibility()) {

				CommerceTermEntry commerceTermEntry =
					_commerceTermEntryLocalService.getCommerceTermEntry(
						commerceChannelAccountEntryRel.getClassPK());

				commerceOrder =
					_commerceOrderLocalService.updateTermsAndConditions(
						commerceOrder.getCommerceOrderId(), 0,
						commerceTermEntry.getCommerceTermEntryId(), languageId);

				httpServletRequest.setAttribute(
					CommerceCheckoutWebKeys.COMMERCE_ORDER, commerceOrder);

				return false;
			}
		}

		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel =
			_commercePaymentMethodGroupRelLocalService.
				getCommercePaymentMethodGroupRel(
					commerceOrder.getGroupId(), commercePaymentMethodKey);

		List<CommerceTermEntry> paymentCommerceTermEntries =
			_commerceTermEntryLocalService.getPaymentCommerceTermEntries(
				commerceOrder.getCompanyId(),
				commerceOrder.getCommerceOrderTypeId(),
				commercePaymentMethodGroupRel.
					getCommercePaymentMethodGroupRelId());

		if (paymentCommerceTermEntries.isEmpty()) {
			return false;
		}

		if (paymentCommerceTermEntries.size() == 1) {
			if (commerceOrder.getPaymentCommerceTermEntryId() > 0) {
				return false;
			}

			CommerceTermEntry commerceTermEntry =
				paymentCommerceTermEntries.get(0);

			commerceOrder = _commerceOrderLocalService.updateTermsAndConditions(
				commerceOrder.getCommerceOrderId(), 0,
				commerceTermEntry.getCommerceTermEntryId(), languageId);

			httpServletRequest.setAttribute(
				CommerceCheckoutWebKeys.COMMERCE_ORDER, commerceOrder);

			return false;
		}

		CommerceTermEntry commerceTermEntry =
			_commerceTermEntryLocalService.fetchCommerceTermEntry(
				commerceOrder.getPaymentCommerceTermEntryId());

		if ((commerceTermEntry == null) &&
			(commerceChannelAccountEntryRel != null)) {

			commerceTermEntry =
				_commerceTermEntryLocalService.fetchCommerceTermEntry(
					commerceChannelAccountEntryRel.getClassPK());
		}

		if ((commerceTermEntry != null) && commerceTermEntry.isActive() &&
			paymentCommerceTermEntries.contains(commerceTermEntry)) {

			commerceOrder = _commerceOrderLocalService.updateTermsAndConditions(
				commerceOrder.getCommerceOrderId(), 0,
				commerceTermEntry.getCommerceTermEntryId(), languageId);
		}
		else {
			commerceTermEntry = paymentCommerceTermEntries.get(0);

			commerceOrder = _commerceOrderLocalService.updateTermsAndConditions(
				commerceOrder.getCommerceOrderId(), 0,
				commerceTermEntry.getCommerceTermEntryId(), languageId);
		}

		httpServletRequest.setAttribute(
			CommerceCheckoutWebKeys.COMMERCE_ORDER, commerceOrder);

		return true;
	}

	@Override
	public boolean isActiveShippingMethodCommerceCheckoutStep(
			CommerceOrder commerceOrder, HttpServletRequest httpServletRequest)
		throws PortalException {

		if (!commerceOrder.isOpen() || !commerceOrder.isShippable() ||
			_commerceShippingHelper.isFreeShipping(commerceOrder)) {

			return false;
		}

		CommerceContext commerceContext =
			(CommerceContext)httpServletRequest.getAttribute(
				CommerceWebKeys.COMMERCE_CONTEXT);

		List<CommerceShippingMethod> commerceShippingMethods =
			_commerceShippingMethodLocalService.getCommerceShippingMethods(
				commerceOrder.getGroupId(), true, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS,
				CommerceShippingMethodPriorityComparator.getInstance(false));

		CommerceShippingOption singleCommerceShippingOption =
			_getSingleCommerceShippingOption(
				commerceContext, commerceOrder, commerceShippingMethods,
				httpServletRequest);

		if (singleCommerceShippingOption != null) {
			_updateCommerceOrder(
				commerceContext, commerceOrder,
				singleCommerceShippingOption.getCommerceShippingMethodKey(),
				singleCommerceShippingOption.getKey(), httpServletRequest);

			return false;
		}

		if (commerceOrder.getCommerceShippingMethodId() > 0) {
			CommerceShippingMethod commerceShippingMethod =
				_commerceShippingMethodLocalService.getCommerceShippingMethod(
					commerceOrder.getCommerceShippingMethodId());

			if (commerceShippingMethod.isActive()) {
				return _hasCommerceOrderPermission(
					CommerceOrderActionKeys.
						MANAGE_COMMERCE_ORDER_SHIPPING_OPTIONS,
					commerceOrder, httpServletRequest);
			}
		}

		if (commerceShippingMethods.isEmpty()) {
			_updateCommerceOrder(
				commerceContext, commerceOrder, StringPool.BLANK,
				StringPool.BLANK, httpServletRequest);

			return false;
		}

		if (!commerceOrder.isGuestOrder()) {
			commerceOrder = _updateCommerceOrderCommerceShippingMethod(
				commerceContext, commerceOrder, commerceShippingMethods,
				httpServletRequest);

			httpServletRequest.setAttribute(
				CommerceCheckoutWebKeys.COMMERCE_ORDER, commerceOrder);
		}

		return _hasCommerceOrderPermission(
			CommerceOrderActionKeys.MANAGE_COMMERCE_ORDER_SHIPPING_OPTIONS,
			commerceOrder, httpServletRequest);
	}

	@Override
	public boolean isCommercePaymentComplete(
			HttpServletRequest httpServletRequest, CommerceOrder commerceOrder)
		throws PortalException {

		return BigDecimalUtil.isZero(commerceOrder.getTotal());
	}

	private List<CommercePaymentMethodGroupRel>
		_filterCommercePaymentMethodGroupRels(
			List<CommercePaymentMethodGroupRel> commercePaymentMethodGroupRels,
			long commerceOrderTypeId, boolean subscriptionOrder) {

		List<CommercePaymentMethodGroupRel>
			filteredCommercePaymentMethodGroupRels = new LinkedList<>();

		ListUtil.sort(
			commercePaymentMethodGroupRels,
			new CommercePaymentMethodPriorityComparator());

		for (CommercePaymentMethodGroupRel commercePaymentMethodGroupRel :
				commercePaymentMethodGroupRels) {

			List<CommercePaymentMethodGroupRelQualifier>
				commercePaymentMethodGroupRelQualifiers =
					_commercePaymentMethodGroupRelQualifierLocalService.
						getCommercePaymentMethodGroupRelQualifiers(
							CommerceOrderType.class.getName(),
							commercePaymentMethodGroupRel.
								getCommercePaymentMethodGroupRelId());

			if ((commerceOrderTypeId > 0) &&
				ListUtil.isNotEmpty(commercePaymentMethodGroupRelQualifiers) &&
				!ListUtil.exists(
					commercePaymentMethodGroupRelQualifiers,
					commercePaymentMethodGroupRelQualifier -> {
						long classPK =
							commercePaymentMethodGroupRelQualifier.getClassPK();

						return classPK == commerceOrderTypeId;
					})) {

				continue;
			}

			PermissionChecker permissionChecker =
				PermissionThreadLocal.getPermissionChecker();

			CommercePaymentMethod commercePaymentMethod =
				_commercePaymentMethodRegistry.getCommercePaymentMethod(
					commercePaymentMethodGroupRel.getPaymentIntegrationKey());

			CommercePaymentIntegration commercePaymentIntegration =
				_commercePaymentIntegrationRegistry.
					getCommercePaymentIntegration(
						commercePaymentMethodGroupRel.
							getPaymentIntegrationKey());

			if (((commercePaymentMethod == null) &&
				 (commercePaymentIntegration == null)) ||
				!permissionChecker.hasPermission(
					commercePaymentMethodGroupRel.getGroupId(),
					CommercePaymentMethodGroupRel.class.getName(),
					commercePaymentMethodGroupRel.
						getCommercePaymentMethodGroupRelId(),
					ActionKeys.VIEW) ||
				((commercePaymentMethod == null) && subscriptionOrder) ||
				((commercePaymentMethod != null) && subscriptionOrder &&
				 !commercePaymentMethod.isProcessRecurringEnabled()) ||
				((commercePaymentMethod != null) && !subscriptionOrder &&
				 !commercePaymentMethod.isProcessPaymentEnabled())) {

				continue;
			}

			filteredCommercePaymentMethodGroupRels.add(
				commercePaymentMethodGroupRel);
		}

		return filteredCommercePaymentMethodGroupRels;
	}

	private CommerceShippingOption _getSingleCommerceShippingOption(
			CommerceContext commerceContext, CommerceOrder commerceOrder,
			List<CommerceShippingMethod> commerceShippingMethods,
			HttpServletRequest httpServletRequest)
		throws PortalException {

		if (commerceShippingMethods.size() != 1) {
			return null;
		}

		CommerceShippingMethod commerceShippingMethod =
			commerceShippingMethods.get(0);

		CommerceShippingEngine commerceShippingEngine =
			_commerceShippingEngineRegistry.getCommerceShippingEngine(
				commerceShippingMethod.getEngineKey());

		List<CommerceShippingOption> commerceShippingOptions =
			commerceShippingEngine.getEnabledCommerceShippingOptions(
				commerceContext, commerceOrder,
				_portal.getLocale(httpServletRequest));

		if (commerceShippingOptions.size() == 1) {
			return commerceShippingOptions.get(0);
		}

		return null;
	}

	private boolean _hasCommerceOrderPermission(
			String actionId, CommerceOrder commerceOrder,
			HttpServletRequest httpServletRequest)
		throws PortalException {

		AccountEntry accountEntry = commerceOrder.getAccountEntry();
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (!commerceOrder.isGuestOrder() &&
			!accountEntry.isPersonalAccount() &&
			!_commerceOrderPortletResourcePermission.contains(
				themeDisplay.getPermissionChecker(),
				accountEntry.getAccountEntryGroupId(), actionId)) {

			return false;
		}

		return true;
	}

	private CommerceOrder _updateCommerceOrder(
			CommerceContext commerceContext, CommerceOrder commerceOrder,
			String commerceShippingMethodKey, String shippingOptionKey,
			HttpServletRequest httpServletRequest)
		throws PortalException {

		CommerceAddress commerceAddress = commerceOrder.getBillingAddress();

		if (commerceAddress == null) {
			commerceAddress = commerceOrder.getShippingAddress();
		}

		if (commerceAddress == null) {
			return commerceOrder;
		}

		try {
			CommerceOrder updatedCommerceOrder = TransactionInvokerUtil.invoke(
				_transactionConfig,
				() -> {
					long commerceShippingMethodId = 0;

					CommerceShippingMethod commerceShippingMethod =
						_commerceShippingMethodLocalService.
							fetchCommerceShippingMethod(
								commerceContext.getCommerceChannelGroupId(),
								commerceShippingMethodKey);

					if (commerceShippingMethod != null) {
						commerceShippingMethodId =
							commerceShippingMethod.
								getCommerceShippingMethodId();
					}

					_commerceOrderLocalService.updateCommerceShippingMethod(
						commerceOrder.getCommerceOrderId(),
						commerceShippingMethodId, shippingOptionKey,
						commerceContext, _portal.getLocale(httpServletRequest));

					return _commerceOrderLocalService.recalculatePrice(
						commerceOrder.getCommerceOrderId(), commerceContext);
				});

			httpServletRequest.setAttribute(
				CommerceCheckoutWebKeys.COMMERCE_ORDER, updatedCommerceOrder);

			return updatedCommerceOrder;
		}
		catch (Throwable throwable) {
			throw new PortalException(throwable);
		}
	}

	private CommerceOrder _updateCommerceOrder(
			CommerceOrder commerceOrder, String commercePaymentMethodKey,
			HttpServletRequest httpServletRequest)
		throws PortalException {

		if (!commerceOrder.isOpen()) {
			return commerceOrder;
		}

		CommerceAddress commerceAddress = commerceOrder.getBillingAddress();

		if (commerceAddress == null) {
			commerceAddress = commerceOrder.getShippingAddress();
		}

		if ((commerceAddress == null) ||
			commercePaymentMethodKey.equals(
				commerceOrder.getCommercePaymentMethodKey())) {

			return commerceOrder;
		}

		commerceOrder =
			_commerceOrderLocalService.updateCommercePaymentMethodKey(
				commerceOrder.getCommerceOrderId(), commercePaymentMethodKey);

		httpServletRequest.setAttribute(
			CommerceCheckoutWebKeys.COMMERCE_ORDER, commerceOrder);

		return commerceOrder;
	}

	private CommerceOrder _updateCommerceOrderCommerceShippingMethod(
			CommerceContext commerceContext, CommerceOrder commerceOrder,
			List<CommerceShippingMethod> commerceShippingMethods,
			HttpServletRequest httpServletRequest)
		throws PortalException {

		AccountEntry accountEntry = commerceOrder.getAccountEntry();

		if (accountEntry.isPersonalAccount()) {
			return commerceOrder;
		}

		CommerceShippingOption highestPriorityCommerceShippingOption = null;

		CommerceShippingOptionAccountEntryRel
			commerceShippingOptionAccountEntryRel =
				_commerceShippingOptionAccountEntryRelService.
					fetchCommerceShippingOptionAccountEntryRel(
						accountEntry.getAccountEntryId(),
						commerceContext.getCommerceChannelId());

		for (CommerceShippingMethod commerceShippingMethod :
				commerceShippingMethods) {

			CommerceShippingEngine commerceShippingEngine =
				_commerceShippingEngineRegistry.getCommerceShippingEngine(
					commerceShippingMethod.getEngineKey());

			List<CommerceShippingOption> commerceShippingOptions =
				commerceShippingEngine.getEnabledCommerceShippingOptions(
					commerceContext, commerceOrder,
					_portal.getLocale(httpServletRequest));

			if (commerceShippingOptions.isEmpty()) {
				continue;
			}

			if (commerceShippingOptionAccountEntryRel != null) {
				CommerceShippingOption defaultCommerceShippingOption = null;

				for (CommerceShippingOption commerceShippingOption :
						commerceShippingOptions) {

					String key = commerceShippingOption.getKey();

					if (key.equals(
							commerceShippingOptionAccountEntryRel.
								getCommerceShippingOptionKey())) {

						defaultCommerceShippingOption = commerceShippingOption;

						break;
					}
				}

				if (defaultCommerceShippingOption != null) {
					return _updateCommerceOrder(
						commerceContext, commerceOrder,
						commerceShippingMethod.getEngineKey(),
						defaultCommerceShippingOption.getKey(),
						httpServletRequest);
				}
			}

			if (highestPriorityCommerceShippingOption == null) {
				highestPriorityCommerceShippingOption =
					commerceShippingOptions.get(0);

				if (commerceShippingOptionAccountEntryRel == null) {
					break;
				}
			}
		}

		if (highestPriorityCommerceShippingOption != null) {
			return _updateCommerceOrder(
				commerceContext, commerceOrder,
				highestPriorityCommerceShippingOption.
					getCommerceShippingMethodKey(),
				highestPriorityCommerceShippingOption.getKey(),
				httpServletRequest);
		}

		return commerceOrder;
	}

	private static final TransactionConfig _transactionConfig =
		TransactionConfig.Factory.create(
			Propagation.REQUIRED, new Class<?>[] {Exception.class});

	@Reference
	private CommerceAddressService _commerceAddressService;

	@Reference
	private CommerceChannelAccountEntryRelLocalService
		_commerceChannelAccountEntryRelLocalService;

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceOrderHttpHelper _commerceOrderHttpHelper;

	@Reference
	private CommerceOrderLocalService _commerceOrderLocalService;

	@Reference(
		target = "(resource.name=" + CommerceOrderConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _commerceOrderPortletResourcePermission;

	@Reference
	private CommerceOrderPriceCalculation _commerceOrderPriceCalculation;

	@Reference
	private CommercePaymentIntegrationRegistry
		_commercePaymentIntegrationRegistry;

	@Reference
	private CommercePaymentMethodGroupRelLocalService
		_commercePaymentMethodGroupRelLocalService;

	@Reference
	private CommercePaymentMethodGroupRelQualifierLocalService
		_commercePaymentMethodGroupRelQualifierLocalService;

	@Reference
	private CommercePaymentMethodRegistry _commercePaymentMethodRegistry;

	@Reference
	private CommerceShippingEngineRegistry _commerceShippingEngineRegistry;

	@Reference
	private CommerceShippingFixedOptionLocalService
		_commerceShippingFixedOptionLocalService;

	@Reference
	private CommerceShippingHelper _commerceShippingHelper;

	@Reference
	private CommerceShippingMethodLocalService
		_commerceShippingMethodLocalService;

	@Reference
	private CommerceShippingOptionAccountEntryRelService
		_commerceShippingOptionAccountEntryRelService;

	@Reference
	private CommerceTermEntryLocalService _commerceTermEntryLocalService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}