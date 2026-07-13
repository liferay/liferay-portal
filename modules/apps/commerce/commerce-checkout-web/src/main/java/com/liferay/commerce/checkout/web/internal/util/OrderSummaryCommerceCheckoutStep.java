/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.checkout.web.internal.util;

import com.liferay.account.constants.AccountEntryValidatorConstants;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.validator.AccountEntryValidatorRegistry;
import com.liferay.account.validator.AccountEntryValidatorResult;
import com.liferay.account.validator.exception.AccountEntryValidatorException;
import com.liferay.commerce.checkout.helper.CommerceCheckoutStepHttpHelper;
import com.liferay.commerce.checkout.web.internal.display.context.OrderSummaryCheckoutStepDisplayContext;
import com.liferay.commerce.configuration.CommerceAccountEntryValidationConfiguration;
import com.liferay.commerce.configuration.CommerceOrderCheckoutConfiguration;
import com.liferay.commerce.constants.CommerceAccountEntryValidationConstants;
import com.liferay.commerce.constants.CommerceCheckoutWebKeys;
import com.liferay.commerce.constants.CommerceConstants;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.discount.exception.CommerceDiscountLimitationTimesException;
import com.liferay.commerce.discount.exception.NoSuchDiscountException;
import com.liferay.commerce.exception.CommerceOrderBillingAddressException;
import com.liferay.commerce.exception.CommerceOrderGuestCheckoutException;
import com.liferay.commerce.exception.CommerceOrderPaymentMethodException;
import com.liferay.commerce.exception.CommerceOrderShippingAddressException;
import com.liferay.commerce.exception.CommerceOrderShippingMethodException;
import com.liferay.commerce.exception.CommerceOrderStatusException;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.order.CommerceOrderHttpHelper;
import com.liferay.commerce.order.CommerceOrderValidatorRegistry;
import com.liferay.commerce.order.engine.CommerceOrderEngine;
import com.liferay.commerce.payment.helper.CommercePaymentHelper;
import com.liferay.commerce.payment.method.CommercePaymentMethod;
import com.liferay.commerce.payment.service.CommercePaymentMethodGroupRelLocalService;
import com.liferay.commerce.percentage.PercentageFormatter;
import com.liferay.commerce.price.CommerceOrderPriceCalculation;
import com.liferay.commerce.price.CommerceProductPriceCalculation;
import com.liferay.commerce.product.helper.CPInstanceHelper;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.option.CommerceOptionValueHelper;
import com.liferay.commerce.product.service.CPInstanceUnitOfMeasureLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceOrderItemService;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.commerce.term.service.CommerceTermEntryLocalService;
import com.liferay.commerce.util.BaseCommerceCheckoutStep;
import com.liferay.commerce.util.CommerceCheckoutStep;
import com.liferay.commerce.util.CommerceOrderItemQuantityFormatter;
import com.liferay.commerce.util.CommerceShippingEngineRegistry;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import java.math.BigDecimal;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 * @author Andrea Di Giorgi
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"commerce.checkout.step.name=" + OrderSummaryCommerceCheckoutStep.NAME,
		"commerce.checkout.step.order:Integer=" + (Integer.MAX_VALUE - 150)
	},
	service = CommerceCheckoutStep.class
)
public class OrderSummaryCommerceCheckoutStep extends BaseCommerceCheckoutStep {

	public static final String NAME = "order-summary";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public boolean isSennaDisabled() {
		return true;
	}

	@Override
	public void processAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try {
			if (FeatureFlagManagerUtil.isEnabled(
					_portal.getCompanyId(actionRequest), "LPD-89850")) {

				_validateAccountEntry(
					_portal.getHttpServletRequest(actionRequest));
			}

			_validateCommerceOrder(actionRequest);

			_checkoutCommerceOrder(actionRequest);
		}
		catch (Exception exception) {
			Throwable throwable = exception.getCause();

			if (throwable == null) {
				throwable = exception;
			}

			if (throwable instanceof AccountEntryValidatorException ||
				throwable instanceof CommerceDiscountLimitationTimesException ||
				throwable instanceof CommerceOrderBillingAddressException ||
				throwable instanceof CommerceOrderGuestCheckoutException ||
				throwable instanceof CommerceOrderPaymentMethodException ||
				throwable instanceof CommerceOrderShippingAddressException ||
				throwable instanceof CommerceOrderShippingMethodException ||
				throwable instanceof NoSuchDiscountException) {

				SessionErrors.add(actionRequest, throwable.getClass());

				return;
			}

			throw exception;
		}
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		OrderSummaryCheckoutStepDisplayContext
			orderSummaryCheckoutStepDisplayContext =
				new OrderSummaryCheckoutStepDisplayContext(
					_commerceChannelLocalService, _commerceOrderHttpHelper,
					_commerceOrderItemQuantityFormatter,
					_commerceOrderPriceCalculation,
					_commerceOrderValidatorRegistry, _commerceOptionValueHelper,
					_commercePaymentMethodGroupRelLocalService,
					_commerceProductPriceCalculation,
					_commerceShippingEngineRegistry,
					_commerceTermEntryLocalService, _cpInstanceHelper,
					_cpInstanceUnitOfMeasureLocalService, httpServletRequest,
					_jsonFactory, _percentageFormatter, _portal,
					_portletResourcePermission);

		CommerceOrder commerceOrder =
			orderSummaryCheckoutStepDisplayContext.getCommerceOrder();

		String goToConfirmation = (String)httpServletRequest.getAttribute(
			"goToConfirmation");

		if (!commerceOrder.isOpen()) {
			httpServletRequest.setAttribute(
				CommerceCheckoutWebKeys.COMMERCE_CHECKOUT_STEP_ORDER_DETAIL_URL,
				_commerceCheckoutStepHttpHelper.getOrderDetailURL(
					httpServletRequest, commerceOrder));

			_jspRenderer.renderJSP(
				httpServletRequest, httpServletResponse, "/error.jsp");
		}
		else if (Validator.isNotNull(goToConfirmation)) {
			while (httpServletResponse instanceof HttpServletResponseWrapper) {
				HttpServletResponseWrapper httpServletResponseWrapper =
					(HttpServletResponseWrapper)httpServletResponse;

				httpServletResponse =
					(HttpServletResponse)
						httpServletResponseWrapper.getResponse();
			}

			goToConfirmation = _portal.escapeRedirect(
				URLCodec.encodeURL(goToConfirmation));

			httpServletResponse.sendRedirect(goToConfirmation);
		}
		else {
			httpServletRequest.setAttribute(
				CommerceCheckoutWebKeys.COMMERCE_CHECKOUT_STEP_DISPLAY_CONTEXT,
				orderSummaryCheckoutStepDisplayContext);

			if (FeatureFlagManagerUtil.isEnabled(
					_portal.getCompanyId(httpServletRequest), "LPD-89850")) {

				_getAccountEntryValidatorResult(
					commerceOrder, httpServletRequest);
			}

			_jspRenderer.renderJSP(
				httpServletRequest, httpServletResponse,
				"/checkout_step/order_summary.jsp");
		}
	}

	@Override
	public boolean showControls(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		CommerceOrder commerceOrder =
			(CommerceOrder)httpServletRequest.getAttribute(
				CommerceCheckoutWebKeys.COMMERCE_ORDER);

		if (!commerceOrder.isOpen()) {
			return false;
		}

		try {
			if (FeatureFlagManagerUtil.isEnabled(
					_portal.getCompanyId(httpServletRequest), "LPD-89850")) {

				AccountEntryValidatorResult accountEntryValidatorResult =
					_getAccountEntryValidatorResult(
						commerceOrder, httpServletRequest);

				if (accountEntryValidatorResult != null) {
					return false;
				}
			}

			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			return _commerceOrderValidatorRegistry.isValid(
				themeDisplay.getLocale(), commerceOrder);
		}
		catch (Exception exception) {
			_log.error(exception);

			return false;
		}
	}

	private void _checkoutCommerceOrder(ActionRequest actionRequest)
		throws Exception {

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			actionRequest);

		CommerceOrder commerceOrder =
			(CommerceOrder)httpServletRequest.getAttribute(
				CommerceCheckoutWebKeys.COMMERCE_ORDER);

		if (commerceOrder.isOpen()) {
			if (_isCheckoutRequestedDeliveryDateEnabled(commerceOrder)) {
				int requestedDeliveryDateMonth = ParamUtil.getInteger(
					actionRequest, "requestedDeliveryDateMonth");
				int requestedDeliveryDateDay = ParamUtil.getInteger(
					actionRequest, "requestedDeliveryDateDay");
				int requestedDeliveryDateYear = ParamUtil.getInteger(
					actionRequest, "requestedDeliveryDateYear");

				if ((requestedDeliveryDateMonth > -1) &&
					(requestedDeliveryDateDay > 0) &&
					(requestedDeliveryDateYear > 0)) {

					ServiceContext serviceContext =
						ServiceContextFactory.getInstance(
							CommerceOrder.class.getName(), actionRequest);

					commerceOrder = _commerceOrderService.updateInfo(
						commerceOrder.getCommerceOrderId(),
						commerceOrder.getPrintedNote(),
						requestedDeliveryDateMonth, requestedDeliveryDateDay,
						requestedDeliveryDateYear, 0, 0, serviceContext);

					httpServletRequest.setAttribute(
						CommerceCheckoutWebKeys.COMMERCE_ORDER, commerceOrder);
				}
			}

			_commerceOrderEngine.checkoutCommerceOrder(
				commerceOrder, _portal.getUserId(httpServletRequest));
		}
	}

	private AccountEntryValidatorResult _getAccountEntryValidatorResult(
			CommerceOrder commerceOrder, HttpServletRequest httpServletRequest)
		throws PortalException {

		AccountEntryValidatorResult accountEntryValidatorResult =
			(AccountEntryValidatorResult)httpServletRequest.getAttribute(
				CommerceWebKeys.COMMERCE_ACCOUNT_VALIDATION_RESULTS);

		if (accountEntryValidatorResult != null) {
			return accountEntryValidatorResult;
		}

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannelByOrderGroupId(
				commerceOrder.getGroupId());

		CommerceAccountEntryValidationConfiguration
			commerceAccountEntryValidationConfiguration =
				_configurationProvider.getConfiguration(
					CommerceAccountEntryValidationConfiguration.class,
					new GroupServiceSettingsLocator(
						commerceChannel.getGroupId(),
						CommerceConstants.
							SERVICE_NAME_COMMERCE_ACCOUNT_ENTRY_VALIDATION));

		String validationMode =
			commerceAccountEntryValidationConfiguration.validationMode();

		if (validationMode.equals(
				CommerceAccountEntryValidationConstants.
					VALIDATION_MODE_DISABLED)) {

			return null;
		}

		for (AccountEntryValidatorResult curAccountEntryValidatorResult :
				_accountEntryValidatorRegistry.validate(
					_accountEntryLocalService.fetchAccountEntry(
						commerceOrder.getCommerceAccountId()),
					JSONUtil.put(
						"billingAddressId", commerceOrder.getBillingAddressId()
					).put(
						"commerceOrderId", commerceOrder.getCommerceOrderId()
					).put(
						"shippingAddressId",
						commerceOrder.getShippingAddressId()
					))) {

			if (!_isAccountValidationResultValid(
					curAccountEntryValidatorResult, validationMode)) {

				httpServletRequest.setAttribute(
					CommerceWebKeys.COMMERCE_ACCOUNT_VALIDATION_RESULTS,
					curAccountEntryValidatorResult);

				return curAccountEntryValidatorResult;
			}
		}

		return null;
	}

	private boolean _isAccountValidationResultValid(
		AccountEntryValidatorResult accountEntryValidatorResult,
		String validationMode) {

		if (validationMode.equals(
				CommerceAccountEntryValidationConstants.
					VALIDATION_MODE_ALLOW_ALL)) {

			return true;
		}

		if (!accountEntryValidatorResult.isValid() ||
			(validationMode.equals(
				CommerceAccountEntryValidationConstants.
					VALIDATION_MODE_ALLOW_SUCCESSES_ONLY) &&
			 AccountEntryValidatorConstants.RESULT_WARNING.equals(
				 accountEntryValidatorResult.getResultStatus()))) {

			return false;
		}

		return true;
	}

	private boolean _isCheckoutRequestedDeliveryDateEnabled(
			CommerceOrder commerceOrder)
		throws Exception {

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannelByOrderGroupId(
				commerceOrder.getGroupId());

		CommerceOrderCheckoutConfiguration commerceOrderCheckoutConfiguration =
			_configurationProvider.getConfiguration(
				CommerceOrderCheckoutConfiguration.class,
				new GroupServiceSettingsLocator(
					commerceChannel.getGroupId(),
					CommerceConstants.SERVICE_NAME_COMMERCE_ORDER));

		return commerceOrderCheckoutConfiguration.
			checkoutRequestedDeliveryDateEnabled();
	}

	private void _validateAccountEntry(HttpServletRequest httpServletRequest)
		throws Exception {

		CommerceOrder commerceOrder =
			(CommerceOrder)httpServletRequest.getAttribute(
				CommerceCheckoutWebKeys.COMMERCE_ORDER);

		AccountEntryValidatorResult accountEntryValidatorResult =
			_getAccountEntryValidatorResult(commerceOrder, httpServletRequest);

		if (accountEntryValidatorResult != null) {
			throw new AccountEntryValidatorException();
		}
	}

	private void _validateCommerceOrder(ActionRequest actionRequest)
		throws Exception {

		CommerceOrder commerceOrder = (CommerceOrder)actionRequest.getAttribute(
			CommerceCheckoutWebKeys.COMMERCE_ORDER);

		commerceOrder = _commerceOrderService.getCommerceOrder(
			commerceOrder.getCommerceOrderId());

		if (!commerceOrder.isOpen()) {
			throw new CommerceOrderStatusException();
		}

		if ((commerceOrder.getShippingAddressId() <= 0) &&
			commerceOrder.isShippable()) {

			throw new CommerceOrderShippingAddressException();
		}

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			actionRequest);

		if ((commerceOrder.getBillingAddressId() <= 0) &&
			_commerceCheckoutStepHttpHelper.
				isActiveBillingAddressCommerceCheckoutStep(
					httpServletRequest, commerceOrder)) {

			throw new CommerceOrderBillingAddressException();
		}

		if ((commerceOrder.getCommerceShippingMethodId() <= 0) &&
			_commerceCheckoutStepHttpHelper.
				isActiveShippingMethodCommerceCheckoutStep(
					commerceOrder, httpServletRequest)) {

			throw new CommerceOrderShippingMethodException();
		}

		String commercePaymentMethodKey =
			commerceOrder.getCommercePaymentMethodKey();

		if (commercePaymentMethodKey.isEmpty() &&
			_commerceCheckoutStepHttpHelper.
				isActivePaymentMethodCommerceCheckoutStep(
					httpServletRequest, commerceOrder)) {

			throw new CommerceOrderPaymentMethodException();
		}

		int subscriptionCommerceOrderItemsCount =
			_commerceOrderItemService.countSubscriptionCommerceOrderItems(
				commerceOrder.getCommerceOrderId());

		BigDecimal subtotal = commerceOrder.getSubtotal();

		if ((subscriptionCommerceOrderItemsCount > 0) &&
			commercePaymentMethodKey.isEmpty() &&
			(subtotal.compareTo(BigDecimal.ZERO) > 0)) {

			throw new CommerceOrderPaymentMethodException();
		}

		if (commerceOrder.isSubscriptionOrder() &&
			!commercePaymentMethodKey.isEmpty()) {

			CommercePaymentMethod commercePaymentMethod =
				_commercePaymentHelper.getCommercePaymentMethod(
					commerceOrder.getCommerceOrderId());

			if (!commercePaymentMethod.isProcessRecurringEnabled()) {
				throw new CommerceOrderPaymentMethodException();
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		OrderSummaryCommerceCheckoutStep.class);

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference
	private AccountEntryValidatorRegistry _accountEntryValidatorRegistry;

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceCheckoutStepHttpHelper _commerceCheckoutStepHttpHelper;

	@Reference
	private CommerceOptionValueHelper _commerceOptionValueHelper;

	@Reference
	private CommerceOrderEngine _commerceOrderEngine;

	@Reference
	private CommerceOrderHttpHelper _commerceOrderHttpHelper;

	@Reference
	private CommerceOrderItemQuantityFormatter
		_commerceOrderItemQuantityFormatter;

	@Reference
	private CommerceOrderItemService _commerceOrderItemService;

	@Reference
	private CommerceOrderPriceCalculation _commerceOrderPriceCalculation;

	@Reference
	private CommerceOrderService _commerceOrderService;

	@Reference
	private CommerceOrderValidatorRegistry _commerceOrderValidatorRegistry;

	@Reference
	private CommercePaymentHelper _commercePaymentHelper;

	@Reference
	private CommercePaymentMethodGroupRelLocalService
		_commercePaymentMethodGroupRelLocalService;

	@Reference
	private CommerceProductPriceCalculation _commerceProductPriceCalculation;

	@Reference
	private CommerceShippingEngineRegistry _commerceShippingEngineRegistry;

	@Reference
	private CommerceTermEntryLocalService _commerceTermEntryLocalService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private CPInstanceHelper _cpInstanceHelper;

	@Reference
	private CPInstanceUnitOfMeasureLocalService
		_cpInstanceUnitOfMeasureLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private JSPRenderer _jspRenderer;

	@Reference
	private PercentageFormatter _percentageFormatter;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(resource.name=" + CommerceOrderConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

}