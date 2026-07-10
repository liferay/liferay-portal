/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.checkout.web.internal.display.context;

import com.liferay.account.model.AccountEntry;
import com.liferay.commerce.configuration.CommerceOrderCheckoutConfiguration;
import com.liferay.commerce.constants.CommerceCheckoutWebKeys;
import com.liferay.commerce.constants.CommerceConstants;
import com.liferay.commerce.constants.CommerceOrderActionKeys;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.model.CommerceMoney;
import com.liferay.commerce.discount.CommerceDiscountValue;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.model.CommerceShippingEngine;
import com.liferay.commerce.model.CommerceShippingMethod;
import com.liferay.commerce.order.CommerceOrderHttpHelper;
import com.liferay.commerce.order.CommerceOrderValidatorRegistry;
import com.liferay.commerce.order.CommerceOrderValidatorResult;
import com.liferay.commerce.payment.model.CommercePaymentMethodGroupRel;
import com.liferay.commerce.payment.service.CommercePaymentMethodGroupRelLocalService;
import com.liferay.commerce.percentage.PercentageFormatter;
import com.liferay.commerce.price.CommerceOrderPrice;
import com.liferay.commerce.price.CommerceOrderPriceCalculation;
import com.liferay.commerce.price.CommerceProductPrice;
import com.liferay.commerce.price.CommerceProductPriceCalculation;
import com.liferay.commerce.price.CommerceProductPriceImpl;
import com.liferay.commerce.price.CommerceProductPriceRequest;
import com.liferay.commerce.product.constants.CPConstants;
import com.liferay.commerce.product.helper.CPInstanceHelper;
import com.liferay.commerce.product.model.CPInstanceUnitOfMeasure;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.option.CommerceOptionValue;
import com.liferay.commerce.product.option.CommerceOptionValueHelper;
import com.liferay.commerce.product.service.CPInstanceUnitOfMeasureLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.term.model.CommerceTermEntry;
import com.liferay.commerce.term.service.CommerceTermEntryLocalService;
import com.liferay.commerce.util.CommerceOrderItemQuantityFormatter;
import com.liferay.commerce.util.CommerceShippingEngineRegistry;
import com.liferay.commerce.util.CommerceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.settings.GroupServiceSettingsLocator;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.KeyValuePair;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * @author Marco Leo
 */
public class OrderSummaryCheckoutStepDisplayContext {

	public OrderSummaryCheckoutStepDisplayContext(
		CommerceChannelLocalService commerceChannelLocalService,
		CommerceOrderHttpHelper commerceOrderHttpHelper,
		CommerceOrderItemQuantityFormatter commerceOrderItemQuantityFormatter,
		CommerceOrderPriceCalculation commerceOrderPriceCalculation,
		CommerceOrderValidatorRegistry commerceOrderValidatorRegistry,
		CommerceOptionValueHelper commerceOptionValueHelper,
		CommercePaymentMethodGroupRelLocalService
			commercePaymentMethodGroupRelLocalService,
		CommerceProductPriceCalculation commerceProductPriceCalculation,
		CommerceShippingEngineRegistry commerceShippingEngineRegistry,
		CommerceTermEntryLocalService commerceTermEntryLocalService,
		CPInstanceHelper cpInstanceHelper,
		CPInstanceUnitOfMeasureLocalService cpInstanceUnitOfMeasureLocalService,
		HttpServletRequest httpServletRequest, JSONFactory jsonFactory,
		PercentageFormatter percentageFormatter, Portal portal,
		PortletResourcePermission portletResourcePermission) {

		_commerceChannelLocalService = commerceChannelLocalService;
		_commerceOrderHttpHelper = commerceOrderHttpHelper;
		_commerceOrderItemQuantityFormatter =
			commerceOrderItemQuantityFormatter;
		_commerceOrderPriceCalculation = commerceOrderPriceCalculation;
		_commerceOrderValidatorRegistry = commerceOrderValidatorRegistry;
		_commerceOptionValueHelper = commerceOptionValueHelper;
		_commercePaymentMethodGroupRelLocalService =
			commercePaymentMethodGroupRelLocalService;
		_commerceProductPriceCalculation = commerceProductPriceCalculation;
		_commerceShippingEngineRegistry = commerceShippingEngineRegistry;
		_commerceTermEntryLocalService = commerceTermEntryLocalService;
		_cpInstanceHelper = cpInstanceHelper;
		_cpInstanceUnitOfMeasureLocalService =
			cpInstanceUnitOfMeasureLocalService;
		_httpServletRequest = httpServletRequest;
		_jsonFactory = jsonFactory;
		_percentageFormatter = percentageFormatter;
		_portal = portal;
		_portletResourcePermission = portletResourcePermission;

		_commerceContext = (CommerceContext)httpServletRequest.getAttribute(
			CommerceWebKeys.COMMERCE_CONTEXT);
		_commerceOrder = (CommerceOrder)httpServletRequest.getAttribute(
			CommerceCheckoutWebKeys.COMMERCE_ORDER);
	}

	public CommerceOrder getCommerceOrder() {
		if (_commerceOrder != null) {
			return _commerceOrder;
		}

		return (CommerceOrder)_httpServletRequest.getAttribute(
			CommerceCheckoutWebKeys.COMMERCE_ORDER);
	}

	public String getCommerceOrderItemFormattedQuantity(
			CommerceOrderItem commerceOrderItem)
		throws PortalException {

		CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure = null;

		String unitOfMeasureKey = commerceOrderItem.getUnitOfMeasureKey();

		if (Validator.isNotNull(unitOfMeasureKey)) {
			cpInstanceUnitOfMeasure =
				_cpInstanceUnitOfMeasureLocalService.
					fetchCPInstanceUnitOfMeasure(
						commerceOrderItem.getCPInstanceId(), unitOfMeasureKey);
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return _commerceOrderItemQuantityFormatter.format(
			commerceOrderItem, cpInstanceUnitOfMeasure,
			themeDisplay.getLocale());
	}

	public int getCommerceOrderItemsQuantity() throws PortalException {
		BigDecimal quantity =
			_commerceOrderHttpHelper.getCommerceOrderItemsQuantity(
				_httpServletRequest);

		return quantity.intValue();
	}

	public CommerceOrderPrice getCommerceOrderPrice() throws PortalException {
		CommerceOrderPrice commerceOrderPrice =
			_commerceOrderPriceCalculation.getCommerceOrderPrice(
				getCommerceOrder(), _commerceContext);

		if (commerceOrderPrice != null) {
			return commerceOrderPrice;
		}

		throw new PortalException(
			"There is no price for this order, or the current user does not " +
				"have permission to view it");
	}

	public Map<Long, List<CommerceOrderValidatorResult>>
			getCommerceOrderValidatorResultsMap()
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return _commerceOrderValidatorRegistry.getCommerceOrderValidatorResults(
			themeDisplay.getLocale(), _commerceOrder);
	}

	public String getCommercePriceDisplayType() throws PortalException {
		CommerceOrder commerceOrder = getCommerceOrder();

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannelByOrderGroupId(
				commerceOrder.getGroupId());

		return commerceChannel.getPriceDisplayType();
	}

	public CommerceProductPrice getCommerceProductPrice(
			CommerceOrderItem commerceOrderItem)
		throws PortalException {

		if (commerceOrderItem.isManuallyAdjusted()) {
			return _getCommerceProductPriceFromOrderItem(commerceOrderItem);
		}

		return _getCommerceProductPrice(commerceOrderItem, _commerceContext);
	}

	public String getCPInstanceCDNURL(CommerceOrderItem commerceOrderItem)
		throws Exception {

		return _cpInstanceHelper.getCPInstanceCDNURL(
			CommerceUtil.getCommerceAccountId(_commerceContext),
			commerceOrderItem.getCPInstanceId());
	}

	public FileVersion getCPInstanceImageFileVersion(
			CommerceOrderItem commerceOrderItem)
		throws Exception {

		return _cpInstanceHelper.getCPInstanceImageFileVersion(
			CommerceUtil.getCommerceAccountId(_commerceContext),
			_portal.getCompanyId(_httpServletRequest),
			commerceOrderItem.getCPInstanceId());
	}

	public String getDeliveryTermEntryName(Locale locale) {
		CommerceTermEntry commerceTermEntry =
			_commerceTermEntryLocalService.fetchCommerceTermEntry(
				_commerceOrder.getDeliveryCommerceTermEntryId());

		if (commerceTermEntry == null) {
			return StringPool.BLANK;
		}

		return commerceTermEntry.getLabel(LanguageUtil.getLanguageId(locale));
	}

	public String getJSONOptionValue(String json, String key) {
		try {
			JSONObject jsonObject = _jsonFactory.createJSONObject(json);

			return jsonObject.getString(key);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return StringPool.BLANK;
	}

	public List<KeyValuePair> getKeyValuePairs(
			long cpDefinitionId, String json, Locale locale)
		throws PortalException {

		return _cpInstanceHelper.getKeyValuePairs(cpDefinitionId, json, locale);
	}

	public String getLocalizedPercentage(BigDecimal percentage, Locale locale)
		throws PortalException {

		CommerceOrder commerceOrder = getCommerceOrder();

		CommerceCurrency commerceCurrency = commerceOrder.getCommerceCurrency();

		return _percentageFormatter.getLocalizedPercentage(
			locale, commerceCurrency.getMaxFractionDigits(),
			commerceCurrency.getMinFractionDigits(), percentage);
	}

	public String getPaymentMethodName(String paymentMethodKey, Locale locale)
		throws PortalException {

		if (paymentMethodKey.isEmpty() || (locale == null)) {
			return StringPool.BLANK;
		}

		CommerceOrder commerceOrder = getCommerceOrder();

		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel =
			_commercePaymentMethodGroupRelLocalService.
				getCommercePaymentMethodGroupRel(
					commerceOrder.getGroupId(), paymentMethodKey);

		return commercePaymentMethodGroupRel.getName(locale);
	}

	public String getPaymentTermEntryName(Locale locale) {
		CommerceTermEntry commerceTermEntry =
			_commerceTermEntryLocalService.fetchCommerceTermEntry(
				_commerceOrder.getPaymentCommerceTermEntryId());

		if (commerceTermEntry == null) {
			return StringPool.BLANK;
		}

		return commerceTermEntry.getLabel(LanguageUtil.getLanguageId(locale));
	}

	public String getShippingOptionName(Locale locale) throws PortalException {
		CommerceOrder commerceOrder = getCommerceOrder();

		if ((commerceOrder == null) ||
			Validator.isNull(commerceOrder.getShippingOptionName())) {

			return StringPool.BLANK;
		}

		CommerceShippingMethod commerceShippingMethod =
			commerceOrder.getCommerceShippingMethod();

		CommerceShippingEngine commerceShippingEngine =
			_commerceShippingEngineRegistry.getCommerceShippingEngine(
				commerceShippingMethod.getEngineKey());

		return commerceShippingEngine.getCommerceShippingOptionLabel(
			commerceOrder.getShippingOptionName(), locale);
	}

	public boolean hasViewBillingAddressPermission(
			PermissionChecker permissionChecker, AccountEntry accountEntry)
		throws PortalException {

		if (accountEntry.isGuestAccount() || accountEntry.isPersonalAccount() ||
			_portletResourcePermission.contains(
				permissionChecker, accountEntry.getAccountEntryGroup(),
				CommerceOrderActionKeys.VIEW_BILLING_ADDRESS)) {

			return true;
		}

		return false;
	}

	public boolean isCheckoutRequestedDeliveryDateEnabled()
		throws PortalException {

		CommerceOrder commerceOrder = getCommerceOrder();

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannelByOrderGroupId(
				commerceOrder.getGroupId());

		CommerceOrderCheckoutConfiguration commerceOrderCheckoutConfiguration =
			ConfigurationProviderUtil.getConfiguration(
				CommerceOrderCheckoutConfiguration.class,
				new GroupServiceSettingsLocator(
					commerceChannel.getGroupId(),
					CommerceConstants.SERVICE_NAME_COMMERCE_ORDER));

		return commerceOrderCheckoutConfiguration.
			checkoutRequestedDeliveryDateEnabled();
	}

	private CommerceProductPrice _getCommerceProductPrice(
			CommerceOrderItem commerceOrderItem,
			CommerceContext commerceContext)
		throws PortalException {

		List<CommerceOptionValue> cpDefinitionCommerceOptionValues =
			_commerceOptionValueHelper.getCPDefinitionCommerceOptionValues(
				commerceOrderItem.getCPDefinitionId(),
				commerceOrderItem.getJson());

		if (ListUtil.isEmpty(cpDefinitionCommerceOptionValues) &&
			!Objects.equals(commerceOrderItem.getJson(), "[]")) {

			List<CommerceOptionValue> commerceOptionValues =
				_commerceOptionValueHelper.toCommerceOptionValues(
					commerceOrderItem.getJson());

			for (CommerceOptionValue commerceOptionValue :
					commerceOptionValues) {

				if (Objects.equals(
						commerceOptionValue.getPriceType(),
						CPConstants.PRODUCT_OPTION_PRICE_TYPE_STATIC)) {

					return _getCommerceProductPriceFromOrderItem(
						commerceOrderItem);
				}
			}
		}

		CommerceProductPriceRequest commerceProductPriceRequest =
			new CommerceProductPriceRequest();

		commerceProductPriceRequest.setCalculateTax(true);
		commerceProductPriceRequest.setCommerceContext(commerceContext);
		commerceProductPriceRequest.setCommerceOptionValues(
			cpDefinitionCommerceOptionValues);
		commerceProductPriceRequest.setCpInstanceId(
			commerceOrderItem.getCPInstanceId());
		commerceProductPriceRequest.setQuantity(
			commerceOrderItem.getQuantity());
		commerceProductPriceRequest.setSecure(false);
		commerceProductPriceRequest.setUnitOfMeasureKey(
			commerceOrderItem.getUnitOfMeasureKey());

		return _commerceProductPriceCalculation.getCommerceProductPrice(
			commerceProductPriceRequest);
	}

	private CommerceProductPrice _getCommerceProductPriceFromOrderItem(
			CommerceOrderItem commerceOrderItem)
		throws PortalException {

		CommerceProductPriceImpl commerceProductPriceImpl =
			new CommerceProductPriceImpl();

		BigDecimal activePrice = commerceOrderItem.getUnitPrice();
		BigDecimal activePriceWithTaxAmount =
			commerceOrderItem.getUnitPriceWithTaxAmount();

		BigDecimal promoPrice = commerceOrderItem.getPromoPrice();

		if ((promoPrice != null) &&
			BigDecimalUtil.gt(promoPrice, BigDecimal.ZERO) &&
			BigDecimalUtil.lte(promoPrice, activePrice)) {

			activePrice = promoPrice;
			activePriceWithTaxAmount =
				commerceOrderItem.getPromoPriceWithTaxAmount();
		}

		BigDecimal quantity = commerceOrderItem.getQuantity();

		BigDecimal unitOfMeasureIncrementalOrderQuantity =
			commerceOrderItem.getUnitOfMeasureIncrementalOrderQuantity();

		if (unitOfMeasureIncrementalOrderQuantity == null) {
			unitOfMeasureIncrementalOrderQuantity = BigDecimal.ONE;
		}

		commerceProductPriceImpl.setFinalPrice(
			commerceOrderItem.getFinalPriceMoney());
		commerceProductPriceImpl.setFinalPriceWithTaxAmount(
			commerceOrderItem.getFinalPriceWithTaxAmountMoney());
		commerceProductPriceImpl.setQuantity(quantity);
		commerceProductPriceImpl.setUnitOfMeasureIncrementalOrderQuantity(
			unitOfMeasureIncrementalOrderQuantity);
		commerceProductPriceImpl.setUnitOfMeasureKey(
			commerceOrderItem.getUnitOfMeasureKey());
		commerceProductPriceImpl.setUnitPrice(
			commerceOrderItem.getUnitPriceMoney());
		commerceProductPriceImpl.setUnitPriceWithTaxAmount(
			commerceOrderItem.getUnitPriceWithTaxAmountMoney());
		commerceProductPriceImpl.setUnitPromoPrice(
			commerceOrderItem.getPromoPriceMoney());
		commerceProductPriceImpl.setUnitPromoPriceWithTaxAmount(
			commerceOrderItem.getPromoPriceWithTaxAmountMoney());

		BigDecimal discountAmount = commerceOrderItem.getDiscountAmount();

		if ((discountAmount == null) || BigDecimalUtil.isZero(discountAmount)) {
			return commerceProductPriceImpl;
		}

		BigDecimal baseQuantity = quantity.divide(
			unitOfMeasureIncrementalOrderQuantity,
			unitOfMeasureIncrementalOrderQuantity.scale(),
			RoundingMode.HALF_UP);

		activePrice = activePrice.multiply(baseQuantity);

		BigDecimal discountedAmount = activePrice.subtract(discountAmount);

		CommerceMoney discountAmountCommerceMoney =
			commerceOrderItem.getDiscountAmountMoney();

		CommerceCurrency commerceCurrency =
			discountAmountCommerceMoney.getCommerceCurrency();

		BigDecimal[] values = {
			commerceOrderItem.getDiscountPercentageLevel1(),
			commerceOrderItem.getDiscountPercentageLevel2(),
			commerceOrderItem.getDiscountPercentageLevel3(),
			commerceOrderItem.getDiscountPercentageLevel4()
		};

		CommerceDiscountValue commerceDiscountValue = new CommerceDiscountValue(
			0, discountAmountCommerceMoney,
			_getDiscountPercentage(
				discountedAmount, activePrice,
				RoundingMode.valueOf(commerceCurrency.getRoundingMode())),
			values);

		commerceProductPriceImpl.setCommerceDiscountValue(
			commerceDiscountValue);

		activePriceWithTaxAmount = activePriceWithTaxAmount.multiply(
			baseQuantity);

		CommerceMoney discountWithTaxAmountCommerceMoney =
			commerceOrderItem.getDiscountWithTaxAmountMoney();

		BigDecimal discountedAmountWithTaxAmount =
			activePriceWithTaxAmount.subtract(
				commerceOrderItem.getDiscountWithTaxAmount());

		BigDecimal[] valuesWithTaxAmount = {
			commerceOrderItem.getDiscountPercentageLevel1WithTaxAmount(),
			commerceOrderItem.getDiscountPercentageLevel2WithTaxAmount(),
			commerceOrderItem.getDiscountPercentageLevel3WithTaxAmount(),
			commerceOrderItem.getDiscountPercentageLevel4WithTaxAmount()
		};

		CommerceDiscountValue commerceDiscountValueWithTaxAmount =
			new CommerceDiscountValue(
				0, discountWithTaxAmountCommerceMoney,
				_getDiscountPercentage(
					discountedAmountWithTaxAmount, activePriceWithTaxAmount,
					RoundingMode.valueOf(commerceCurrency.getRoundingMode())),
				valuesWithTaxAmount);

		commerceProductPriceImpl.setCommerceDiscountValueWithTaxAmount(
			commerceDiscountValueWithTaxAmount);

		return commerceProductPriceImpl;
	}

	private BigDecimal _getDiscountPercentage(
		BigDecimal discountedAmount, BigDecimal amount,
		RoundingMode roundingMode) {

		double actualPrice = discountedAmount.doubleValue();
		double originalPrice = amount.doubleValue();

		double percentage = actualPrice / originalPrice;

		BigDecimal discountPercentage = new BigDecimal(percentage);

		discountPercentage = discountPercentage.multiply(_ONE_HUNDRED);

		MathContext mathContext = new MathContext(
			discountPercentage.precision(), roundingMode);

		return _ONE_HUNDRED.subtract(discountPercentage, mathContext);
	}

	private static final BigDecimal _ONE_HUNDRED = BigDecimal.valueOf(100);

	private static final Log _log = LogFactoryUtil.getLog(
		OrderSummaryCheckoutStepDisplayContext.class);

	private final CommerceChannelLocalService _commerceChannelLocalService;
	private final CommerceContext _commerceContext;
	private final CommerceOptionValueHelper _commerceOptionValueHelper;
	private final CommerceOrder _commerceOrder;
	private final CommerceOrderHttpHelper _commerceOrderHttpHelper;
	private final CommerceOrderItemQuantityFormatter
		_commerceOrderItemQuantityFormatter;
	private final CommerceOrderPriceCalculation _commerceOrderPriceCalculation;
	private final CommerceOrderValidatorRegistry
		_commerceOrderValidatorRegistry;
	private final CommercePaymentMethodGroupRelLocalService
		_commercePaymentMethodGroupRelLocalService;
	private final CommerceProductPriceCalculation
		_commerceProductPriceCalculation;
	private final CommerceShippingEngineRegistry
		_commerceShippingEngineRegistry;
	private final CommerceTermEntryLocalService _commerceTermEntryLocalService;
	private final CPInstanceHelper _cpInstanceHelper;
	private final CPInstanceUnitOfMeasureLocalService
		_cpInstanceUnitOfMeasureLocalService;
	private final HttpServletRequest _httpServletRequest;
	private final JSONFactory _jsonFactory;
	private final PercentageFormatter _percentageFormatter;
	private final Portal _portal;
	private final PortletResourcePermission _portletResourcePermission;

}