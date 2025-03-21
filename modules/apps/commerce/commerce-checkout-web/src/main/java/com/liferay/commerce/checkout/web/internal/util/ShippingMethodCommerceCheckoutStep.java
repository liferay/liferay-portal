/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.checkout.web.internal.util;

import com.liferay.commerce.checkout.helper.CommerceCheckoutStepHttpHelper;
import com.liferay.commerce.checkout.web.internal.display.context.ShippingMethodCheckoutStepDisplayContext;
import com.liferay.commerce.constants.CommerceCheckoutWebKeys;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.currency.util.CommercePriceFormatter;
import com.liferay.commerce.exception.CommerceOrderShippingMethodException;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceShippingEngine;
import com.liferay.commerce.model.CommerceShippingMethod;
import com.liferay.commerce.model.CommerceShippingOption;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.service.CommerceShippingMethodLocalService;
import com.liferay.commerce.shipping.engine.fixed.service.CommerceShippingFixedOptionLocalService;
import com.liferay.commerce.util.BaseCommerceCheckoutStep;
import com.liferay.commerce.util.CommerceCheckoutStep;
import com.liferay.commerce.util.CommerceShippingEngineRegistry;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.math.BigDecimal;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Di Giorgi
 * @author Alessio Antonio Rendina
 * @author Luca Pellizzon
 */
@Component(
	property = {
		"commerce.checkout.step.name=" + ShippingMethodCommerceCheckoutStep.NAME,
		"commerce.checkout.step.order:Integer=20"
	},
	service = CommerceCheckoutStep.class
)
public class ShippingMethodCommerceCheckoutStep
	extends BaseCommerceCheckoutStep {

	public static final char COMMERCE_SHIPPING_OPTION_KEY_SEPARATOR =
		CharPool.POUND;

	public static final String NAME = "shipping-method";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public boolean isActive(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		CommerceOrder commerceOrder =
			(CommerceOrder)httpServletRequest.getAttribute(
				CommerceCheckoutWebKeys.COMMERCE_ORDER);

		if (_commerceCheckoutStepHttpHelper.
				isActiveShippingMethodCommerceCheckoutStep(
					commerceOrder, httpServletRequest) &&
			commerceOrder.isShippable()) {

			return true;
		}

		return false;
	}

	@Override
	public void processAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try {
			_updateCommerceOrderShippingMethod(actionRequest);
		}
		catch (Exception exception) {
			if (exception instanceof CommerceOrderShippingMethodException) {
				SessionErrors.add(actionRequest, exception.getClass());

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

		ShippingMethodCheckoutStepDisplayContext
			shippingMethodCheckoutStepDisplayContext =
				new ShippingMethodCheckoutStepDisplayContext(
					_commercePriceFormatter, _commerceShippingEngineRegistry,
					_commerceShippingMethodLocalService,
					_commerceShippingFixedOptionLocalService,
					_configurationProvider, httpServletRequest);

		CommerceOrder commerceOrder =
			shippingMethodCheckoutStepDisplayContext.getCommerceOrder();

		if (!commerceOrder.isOpen()) {
			httpServletRequest.setAttribute(
				CommerceCheckoutWebKeys.COMMERCE_CHECKOUT_STEP_ORDER_DETAIL_URL,
				_commerceCheckoutStepHttpHelper.getOrderDetailURL(
					httpServletRequest, commerceOrder));

			_jspRenderer.renderJSP(
				httpServletRequest, httpServletResponse, "/error.jsp");
		}
		else {
			httpServletRequest.setAttribute(
				CommerceCheckoutWebKeys.COMMERCE_CHECKOUT_STEP_DISPLAY_CONTEXT,
				shippingMethodCheckoutStepDisplayContext);

			_jspRenderer.renderJSP(
				httpServletRequest, httpServletResponse,
				"/checkout_step/shipping_method.jsp");
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

		return super.showControls(httpServletRequest, httpServletResponse);
	}

	protected BigDecimal getShippingAmount(
			CommerceContext commerceContext, CommerceOrder commerceOrder,
			long commerceShippingMethodId, String shippingOptionName,
			Locale locale)
		throws PortalException {

		CommerceShippingMethod commerceShippingMethod =
			_commerceShippingMethodLocalService.getCommerceShippingMethod(
				commerceShippingMethodId);

		if (!commerceShippingMethod.isActive()) {
			throw new CommerceOrderShippingMethodException(
				"Shipping method " +
					commerceShippingMethod.getCommerceShippingMethodId() +
						" is not active");
		}

		CommerceShippingEngine commerceShippingEngine =
			_commerceShippingEngineRegistry.getCommerceShippingEngine(
				commerceShippingMethod.getEngineKey());

		List<CommerceShippingOption> commerceShippingOptions =
			commerceShippingEngine.getCommerceShippingOptions(
				commerceContext, commerceOrder, locale);

		for (CommerceShippingOption commerceShippingOption :
				commerceShippingOptions) {

			if (shippingOptionName.equals(commerceShippingOption.getKey())) {
				BigDecimal shippingAmount = commerceShippingOption.getAmount();

				if (CommerceOrderUtil.isCommerceOrderMultishipping(
						commerceOrder)) {

					return shippingAmount.multiply(
						BigDecimal.valueOf(
							CommerceOrderUtil.
								getCommerceOrderDeliveryGroupNamesCount(
									commerceOrder)));
				}

				return shippingAmount;
			}
		}

		throw new CommerceOrderShippingMethodException(
			StringBundler.concat(
				"Unable to get amount of option \"", shippingOptionName,
				"\" for shipping method ", commerceShippingMethodId));
	}

	private void _updateCommerceOrderShippingMethod(ActionRequest actionRequest)
		throws Exception {

		String commerceShippingOptionKey = ParamUtil.getString(
			actionRequest, "commerceShippingOptionKey");

		if (Validator.isNull(commerceShippingOptionKey)) {
			throw new CommerceOrderShippingMethodException();
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(themeDisplay.getUser());

		CommerceOrder commerceOrder = (CommerceOrder)actionRequest.getAttribute(
			CommerceCheckoutWebKeys.COMMERCE_ORDER);

		if (!_commerceOrderModelResourcePermission.contains(
				permissionChecker, commerceOrder, ActionKeys.UPDATE)) {

			return;
		}

		CommerceContext commerceContext =
			(CommerceContext)actionRequest.getAttribute(
				CommerceWebKeys.COMMERCE_CONTEXT);

		int pos = commerceShippingOptionKey.indexOf(
			COMMERCE_SHIPPING_OPTION_KEY_SEPARATOR);

		long commerceShippingMethodId = GetterUtil.getLong(
			commerceShippingOptionKey.substring(0, pos));
		String shippingOptionName = commerceShippingOptionKey.substring(
			pos + 1);

		BigDecimal shippingAmount = getShippingAmount(
			commerceContext, commerceOrder, commerceShippingMethodId,
			shippingOptionName, themeDisplay.getLocale());

		try {
			if ((commerceOrder.getCommerceShippingMethodId() !=
					commerceShippingMethodId) ||
				!StringUtil.equals(
					commerceOrder.getShippingOptionName(),
					shippingOptionName) ||
				!Objects.equals(
					commerceOrder.getShippingAmount(), shippingAmount)) {

				CommerceOrder updateCommerceOrder =
					TransactionInvokerUtil.invoke(
						_transactionConfig,
						() -> _commerceOrderLocalService.updateCommerceOrder(
							commerceOrder.getUserId(),
							commerceOrder.getExternalReferenceCode(),
							commerceOrder.getCommerceOrderId(),
							commerceOrder.getBillingAddressId(),
							commerceOrder.getCommerceAccountId(),
							commerceOrder.getCommerceCurrencyCode(),
							commerceOrder.getCommerceOrderTypeId(),
							commerceShippingMethodId,
							commerceOrder.getDeliveryCommerceTermEntryId(),
							commerceOrder.getPaymentCommerceTermEntryId(),
							commerceOrder.getShippingAddressId(),
							commerceOrder.getAdvanceStatus(),
							commerceOrder.getCommercePaymentMethodKey(),
							commerceOrder.getCouponCode(),
							commerceOrder.
								getDeliveryCommerceTermEntryDescription(),
							commerceOrder.getDeliveryCommerceTermEntryName(),
							commerceOrder.getLastPriceUpdateDate(),
							commerceOrder.isManuallyAdjusted(), null,
							commerceOrder.getOrderDate(),
							commerceOrder.getOrderStatus(),
							commerceOrder.
								getPaymentCommerceTermEntryDescription(),
							commerceOrder.getPaymentCommerceTermEntryName(),
							commerceOrder.getPaymentStatus(),
							commerceOrder.getPrintedNote(),
							commerceOrder.getPurchaseOrderNumber(),
							commerceOrder.getRequestedDeliveryDate(),
							commerceOrder.isShippable(), shippingAmount,
							commerceOrder.getShippingDiscountAmount(),
							commerceOrder.getShippingDiscountPercentageLevel1(),
							commerceOrder.getShippingDiscountPercentageLevel2(),
							commerceOrder.getShippingDiscountPercentageLevel3(),
							commerceOrder.getShippingDiscountPercentageLevel4(),
							commerceOrder.
								getShippingDiscountPercentageLevel1WithTaxAmount(),
							commerceOrder.
								getShippingDiscountPercentageLevel2WithTaxAmount(),
							commerceOrder.
								getShippingDiscountPercentageLevel3WithTaxAmount(),
							commerceOrder.
								getShippingDiscountPercentageLevel4WithTaxAmount(),
							commerceOrder.getShippingDiscountWithTaxAmount(),
							shippingOptionName,
							commerceOrder.getShippingWithTaxAmount(),
							commerceOrder.getSubtotal(),
							commerceOrder.getSubtotalDiscountAmount(),
							commerceOrder.getSubtotalDiscountPercentageLevel1(),
							commerceOrder.getSubtotalDiscountPercentageLevel2(),
							commerceOrder.getSubtotalDiscountPercentageLevel3(),
							commerceOrder.getSubtotalDiscountPercentageLevel4(),
							commerceOrder.
								getSubtotalDiscountPercentageLevel1WithTaxAmount(),
							commerceOrder.
								getSubtotalDiscountPercentageLevel2WithTaxAmount(),
							commerceOrder.
								getSubtotalDiscountPercentageLevel3WithTaxAmount(),
							commerceOrder.
								getSubtotalDiscountPercentageLevel4WithTaxAmount(),
							commerceOrder.getSubtotalDiscountWithTaxAmount(),
							commerceOrder.getSubtotalWithTaxAmount(),
							commerceOrder.getTaxAmount(),
							commerceOrder.getTotal(),
							commerceOrder.getTotalDiscountAmount(),
							commerceOrder.getTotalDiscountPercentageLevel1(),
							commerceOrder.getTotalDiscountPercentageLevel2(),
							commerceOrder.getTotalDiscountPercentageLevel3(),
							commerceOrder.getTotalDiscountPercentageLevel4(),
							commerceOrder.
								getTotalDiscountPercentageLevel1WithTaxAmount(),
							commerceOrder.
								getTotalDiscountPercentageLevel2WithTaxAmount(),
							commerceOrder.
								getTotalDiscountPercentageLevel3WithTaxAmount(),
							commerceOrder.
								getTotalDiscountPercentageLevel4WithTaxAmount(),
							commerceOrder.getTotalDiscountWithTaxAmount(),
							commerceOrder.getTotalWithTaxAmount(),
							commerceOrder.getTransactionId(),
							commerceOrder.getStatus(),
							commerceOrder.getStatusByUserId(),
							commerceOrder.getStatusByUserName(),
							commerceOrder.getStatusDate(), true,
							commerceContext));

				_commerceOrderLocalService.resetTermsAndConditions(
					commerceOrder.getCommerceOrderId(), true, false);

				actionRequest.setAttribute(
					CommerceCheckoutWebKeys.COMMERCE_ORDER,
					updateCommerceOrder);
			}
		}
		catch (Throwable throwable) {
			throw new PortalException(throwable);
		}
	}

	private static final TransactionConfig _transactionConfig =
		TransactionConfig.Factory.create(
			Propagation.REQUIRED, new Class<?>[] {Exception.class});

	@Reference
	private CommerceCheckoutStepHttpHelper _commerceCheckoutStepHttpHelper;

	@Reference
	private CommerceOrderLocalService _commerceOrderLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.commerce.model.CommerceOrder)"
	)
	private ModelResourcePermission<CommerceOrder>
		_commerceOrderModelResourcePermission;

	@Reference
	private CommercePriceFormatter _commercePriceFormatter;

	@Reference
	private CommerceShippingEngineRegistry _commerceShippingEngineRegistry;

	@Reference
	private CommerceShippingFixedOptionLocalService
		_commerceShippingFixedOptionLocalService;

	@Reference
	private CommerceShippingMethodLocalService
		_commerceShippingMethodLocalService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private JSPRenderer _jspRenderer;

}