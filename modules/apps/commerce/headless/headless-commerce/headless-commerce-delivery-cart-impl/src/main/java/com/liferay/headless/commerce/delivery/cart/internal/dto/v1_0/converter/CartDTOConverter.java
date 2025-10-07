/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.cart.internal.dto.v1_0.converter;

import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.constants.CommerceOrderPaymentConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.model.CommerceMoney;
import com.liferay.commerce.currency.util.CommercePriceFormatter;
import com.liferay.commerce.frontend.helper.CommerceOrderStepTrackerHelper;
import com.liferay.commerce.frontend.model.StepModel;
import com.liferay.commerce.model.CommerceAddress;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.model.CommerceShippingEngine;
import com.liferay.commerce.model.CommerceShippingMethod;
import com.liferay.commerce.payment.method.CommercePaymentMethod;
import com.liferay.commerce.payment.method.CommercePaymentMethodRegistry;
import com.liferay.commerce.payment.model.CommercePaymentMethodGroupRel;
import com.liferay.commerce.payment.service.CommercePaymentMethodGroupRelLocalService;
import com.liferay.commerce.pricing.constants.CommercePricingConstants;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceOrderItemService;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.commerce.service.CommerceOrderTypeService;
import com.liferay.commerce.term.model.CommerceTermEntry;
import com.liferay.commerce.term.service.CommerceTermEntryLocalService;
import com.liferay.commerce.util.CommerceShippingEngineRegistry;
import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.friendly.url.provider.FriendlyURLSeparatorProvider;
import com.liferay.headless.commerce.delivery.cart.dto.v1_0.Address;
import com.liferay.headless.commerce.delivery.cart.dto.v1_0.Attachment;
import com.liferay.headless.commerce.delivery.cart.dto.v1_0.Cart;
import com.liferay.headless.commerce.delivery.cart.dto.v1_0.Status;
import com.liferay.headless.commerce.delivery.cart.dto.v1_0.Step;
import com.liferay.headless.commerce.delivery.cart.dto.v1_0.Summary;
import com.liferay.headless.commerce.delivery.cart.internal.dto.v1_0.converter.constants.DTOConverterConstants;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.language.LanguageResources;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Sbarra
 */
@Component(
	property = "dto.class.name=com.liferay.headless.commerce.delivery.cart.dto.v1_0.Cart",
	service = DTOConverter.class
)
public class CartDTOConverter implements DTOConverter<CommerceOrder, Cart> {

	@Override
	public String getContentType() {
		return Cart.class.getSimpleName();
	}

	@Override
	public Cart toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		CommerceOrder commerceOrder = _commerceOrderService.getCommerceOrder(
			(Long)dtoConverterContext.getId());

		CommerceShippingMethod commerceShippingMethod =
			commerceOrder.getCommerceShippingMethod();

		Locale locale = dtoConverterContext.getLocale();

		ResourceBundle resourceBundle = LanguageResources.getResourceBundle(
			locale);

		return new Cart() {
			{
				setAccount(commerceOrder::getCommerceAccountName);
				setAccountId(commerceOrder::getCommerceAccountId);
				setAttachments(() -> _getAttachments(commerceOrder));
				setAuthor(commerceOrder::getUserName);
				setBillingAddress(
					() -> _toAddress(
						commerceOrder.getBillingAddress(), locale));
				setBillingAddressExternalReferenceCode(
					() -> {
						CommerceAddress billingCommerceAddress =
							commerceOrder.getBillingAddress();

						if (billingCommerceAddress == null) {
							return null;
						}

						return billingCommerceAddress.
							getExternalReferenceCode();
					});
				setBillingAddressId(commerceOrder::getBillingAddressId);
				setCouponCode(commerceOrder::getCouponCode);
				setCreateDate(commerceOrder::getCreateDate);
				setCustomFields(
					() -> {
						ExpandoBridge expandoBridge =
							commerceOrder.getExpandoBridge();

						return expandoBridge.getAttributes();
					});
				setDeliveryTermId(
					commerceOrder::getDeliveryCommerceTermEntryId);
				setDeliveryTermLabel(
					() -> {
						if (commerceOrder.getDeliveryCommerceTermEntryId() <=
								0) {

							return null;
						}

						CommerceTermEntry commerceTermEntry =
							_commerceTermEntryLocalService.getCommerceTermEntry(
								commerceOrder.getDeliveryCommerceTermEntryId());

						return commerceTermEntry.getLabel(
							_language.getLanguageId(locale));
					});
				setExternalReferenceCode(
					commerceOrder::getExternalReferenceCode);
				setFriendlyURLSeparator(
					() -> {
						if (!FeatureFlagManagerUtil.isEnabled("LPD-20379")) {
							return null;
						}

						FriendlyURLSeparatorProvider
							friendlyURLSeparatorProvider =
								_friendlyURLSeparatorProviderSnapshot.get();

						if (friendlyURLSeparatorProvider == null) {
							return null;
						}

						return friendlyURLSeparatorProvider.
							getFriendlyURLSeparator(
								commerceOrder.getCompanyId(),
								CommerceOrder.class.getName());
					});
				setId(commerceOrder::getCommerceOrderId);
				setLastPriceUpdateDate(commerceOrder::getLastPriceUpdateDate);
				setModifiedDate(commerceOrder::getModifiedDate);
				setName(commerceOrder::getName);
				setOrderStatusInfo(
					() -> {
						String commerceOrderStatusLabel =
							CommerceOrderConstants.getOrderStatusLabel(
								commerceOrder.getOrderStatus());

						String commerceOrderStatusLabelI18n = _language.get(
							resourceBundle,
							CommerceOrderConstants.getOrderStatusLabel(
								commerceOrder.getOrderStatus()));

						return _getOrderStatusInfo(
							commerceOrder.getOrderStatus(),
							commerceOrderStatusLabel,
							commerceOrderStatusLabelI18n);
					});
				setOrderType(
					() -> {
						CommerceOrderType commerceOrderType =
							_commerceOrderTypeService.fetchCommerceOrderType(
								commerceOrder.getCommerceOrderTypeId());

						if (commerceOrderType == null) {
							return null;
						}

						return commerceOrderType.getName(locale);
					});
				setOrderTypeExternalReferenceCode(
					() -> _getOrderTypeExternalReferenceCode(
						commerceOrder.getCommerceOrderTypeId()));
				setOrderTypeId(commerceOrder::getCommerceOrderTypeId);
				setOrderUUID(commerceOrder::getUuid);
				setPaymentMethod(commerceOrder::getCommercePaymentMethodKey);
				setPaymentMethodLabel(
					() -> {
						String paymentMethodKey =
							commerceOrder.getCommercePaymentMethodKey();

						if (Validator.isNull(paymentMethodKey)) {
							return null;
						}

						CommercePaymentMethodGroupRel
							commercePaymentMethodGroupRel =
								_commercePaymentMethodGroupRelLocalService.
									getCommercePaymentMethodGroupRel(
										commerceOrder.getGroupId(),
										paymentMethodKey);

						return commercePaymentMethodGroupRel.getName(locale);
					});
				setPaymentMethodType(
					() -> {
						String paymentMethodKey =
							commerceOrder.getCommercePaymentMethodKey();

						if (Validator.isNull(paymentMethodKey)) {
							return null;
						}

						CommercePaymentMethod commercePaymentMethod =
							_commercePaymentMethodRegistry.
								getCommercePaymentMethod(paymentMethodKey);

						if (commercePaymentMethod == null) {
							return null;
						}

						return commercePaymentMethod.getPaymentType();
					});
				setPaymentStatus(commerceOrder::getPaymentStatus);
				setPaymentStatusInfo(
					() -> {
						String commerceOrderPaymentStatusLabelI18n =
							_language.get(
								resourceBundle,
								CommerceOrderPaymentConstants.
									getOrderPaymentStatusLabel(
										commerceOrder.getPaymentStatus()));

						return _getPaymentStatusInfo(
							commerceOrder.getPaymentStatus(),
							getPaymentStatusLabel(),
							commerceOrderPaymentStatusLabelI18n);
					});
				setPaymentStatusLabel(
					() ->
						CommerceOrderPaymentConstants.
							getOrderPaymentStatusLabel(
								commerceOrder.getPaymentStatus()));
				setPaymentTermId(commerceOrder::getPaymentCommerceTermEntryId);
				setPaymentTermLabel(
					() -> {
						if (commerceOrder.getPaymentCommerceTermEntryId() <=
								0) {

							return null;
						}

						CommerceTermEntry commerceTermEntry =
							_commerceTermEntryLocalService.getCommerceTermEntry(
								commerceOrder.getPaymentCommerceTermEntryId());

						return commerceTermEntry.getLabel(
							_language.getLanguageId(locale));
					});
				setPrintedNote(commerceOrder::getPrintedNote);
				setPurchaseOrderNumber(commerceOrder::getPurchaseOrderNumber);
				setRequestedDeliveryDate(
					commerceOrder::getRequestedDeliveryDate);
				setShippingAddress(
					() -> _toAddress(
						commerceOrder.getShippingAddress(), locale));
				setShippingAddressExternalReferenceCode(
					() -> {
						CommerceAddress shippingCommerceAddress =
							commerceOrder.getShippingAddress();

						if (shippingCommerceAddress == null) {
							return null;
						}

						return shippingCommerceAddress.
							getExternalReferenceCode();
					});
				setShippingAddressId(commerceOrder::getShippingAddressId);
				setShippingMethod(
					() -> {
						if (commerceShippingMethod == null) {
							return null;
						}

						return commerceShippingMethod.getName(locale);
					});
				setShippingOption(
					() -> {
						if (commerceShippingMethod == null) {
							return null;
						}

						CommerceShippingEngine commerceShippingEngine =
							_commerceShippingEngineRegistry.
								getCommerceShippingEngine(
									commerceShippingMethod.getEngineKey());

						return commerceShippingEngine.
							getCommerceShippingOptionLabel(
								commerceOrder.getShippingOptionName(), locale);
					});
				setStatus(
					() -> WorkflowConstants.getStatusLabel(
						commerceOrder.getStatus()));
				setSteps(
					() -> TransformUtil.transformToArray(
						_commerceOrderStepTrackerHelper.getCommerceOrderSteps(
							false, commerceOrder, locale),
						stepModel -> _toStep(stepModel), Step.class));
				setSummary(() -> _getSummary(commerceOrder, locale));
				setWorkflowStatusInfo(
					() -> {
						String commerceOrderWorkflowStatusLabelI18n =
							_language.get(
								resourceBundle,
								WorkflowConstants.getStatusLabel(
									commerceOrder.getStatus()));

						return _toStatus(
							commerceOrder.getStatus(), getStatus(),
							commerceOrderWorkflowStatusLabelI18n);
					});
			}
		};
	}

	private String _formatPrice(
			BigDecimal price, CommerceCurrency commerceCurrency, Locale locale)
		throws Exception {

		if (price == null) {
			price = BigDecimal.ZERO;
		}

		return _commercePriceFormatter.format(commerceCurrency, price, locale);
	}

	private Attachment[] _getAttachments(CommerceOrder commerceOrder)
		throws PortalException {

		return TransformUtil.transformToArray(
			commerceOrder.getAttachmentFileEntries(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS),
			_attachmentDTOConverter::toDTO, Attachment.class);
	}

	private String[] _getFormattedDiscountPercentages(
			BigDecimal[] discountPercentages, Locale locale)
		throws Exception {

		List<String> formattedDiscountPercentages = new ArrayList<>();

		for (BigDecimal percentage : discountPercentages) {
			formattedDiscountPercentages.add(
				_commercePriceFormatter.format(percentage, locale));
		}

		return formattedDiscountPercentages.toArray(new String[0]);
	}

	private Status _getOrderStatusInfo(
		int orderStatus, String commerceOrderStatusLabel,
		String commerceOrderStatusLabelI18n) {

		return new Status() {
			{
				setCode(() -> orderStatus);
				setLabel(() -> commerceOrderStatusLabel);
				setLabel_i18n(() -> commerceOrderStatusLabelI18n);
			}
		};
	}

	private String _getOrderTypeExternalReferenceCode(long commerceOrderTypeId)
		throws Exception {

		CommerceOrderType commerceOrderType =
			_commerceOrderTypeService.fetchCommerceOrderType(
				commerceOrderTypeId);

		if (commerceOrderType == null) {
			return null;
		}

		return commerceOrderType.getExternalReferenceCode();
	}

	private Status _getPaymentStatusInfo(
		int paymentStatus, String commerceOrderPaymentStatusLabel,
		String commerceOrderPaymentStatusLabelI18n) {

		return new Status() {
			{
				setCode(() -> paymentStatus);
				setLabel(() -> commerceOrderPaymentStatusLabel);
				setLabel_i18n(() -> commerceOrderPaymentStatusLabelI18n);
			}
		};
	}

	private Summary _getSummary(CommerceOrder commerceOrder, Locale locale)
		throws Exception {

		CommerceCurrency commerceCurrency = commerceOrder.getCommerceCurrency();

		CommerceMoney commerceOrderPriceShippingValueCommerceMoney =
			commerceOrder.getShippingMoney();

		BigDecimal commerceOrderPriceShippingValuePrice =
			commerceOrderPriceShippingValueCommerceMoney.getPrice();

		CommerceMoney commerceOrderShippingValueWithTaxAmountCommerceMoney =
			commerceOrder.getShippingWithTaxAmountMoney();

		BigDecimal commerceOrderPriceShippingValueWithTaxAmountPrice =
			commerceOrderPriceShippingValuePrice;

		if (!commerceOrderShippingValueWithTaxAmountCommerceMoney.isEmpty()) {
			commerceOrderPriceShippingValueWithTaxAmountPrice =
				commerceOrderShippingValueWithTaxAmountCommerceMoney.getPrice();
		}

		CommerceMoney commerceOrderPriceSubtotalCommerceMoney =
			commerceOrder.getSubtotalMoney();

		BigDecimal orderPriceSubtotalPrice =
			commerceOrderPriceSubtotalCommerceMoney.getPrice();

		CommerceMoney commerceOrderPriceTotalCommerceMoney =
			commerceOrder.getTotalMoney();

		BigDecimal orderPriceTotalPrice =
			commerceOrderPriceTotalCommerceMoney.getPrice();

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannelByOrderGroupId(
				commerceOrder.getGroupId());

		if (Objects.equals(
				commerceChannel.getPriceDisplayType(),
				CommercePricingConstants.TAX_INCLUDED_IN_PRICE)) {

			orderPriceSubtotalPrice = commerceOrder.getSubtotalWithTaxAmount();
			orderPriceTotalPrice = commerceOrder.getTotalWithTaxAmount();
		}

		BigDecimal finalOrderPriceShippingValueWithTaxAmountPrice =
			commerceOrderPriceShippingValueWithTaxAmountPrice;
		BigDecimal finalOrderPriceSubtotalPrice = orderPriceSubtotalPrice;
		BigDecimal finalOrderPriceTotalPrice = orderPriceTotalPrice;

		Summary summary = new Summary() {
			{
				setCurrency(() -> commerceCurrency.getName(locale));
				setItemsCount(
					() ->
						_commerceOrderItemService.
							getParentCommerceOrderItemsCount(
								commerceOrder.getCommerceOrderId(), 0));
				setItemsQuantity(
					() -> BigDecimalUtil.stripTrailingZeros(
						_commerceOrderItemService.getCommerceOrderItemsQuantity(
							commerceOrder.getCommerceOrderId())));
				setShippingValue(
					commerceOrderPriceShippingValuePrice::doubleValue);
				setShippingValueFormatted(
					() -> commerceOrderPriceShippingValueCommerceMoney.format(
						locale));
				setShippingValueWithTaxAmount(
					finalOrderPriceShippingValueWithTaxAmountPrice::
						doubleValue);
				setShippingValueWithTaxAmountFormatted(
					() ->
						commerceOrderShippingValueWithTaxAmountCommerceMoney.
							format(locale));
				setSubtotal(finalOrderPriceSubtotalPrice::doubleValue);
				setSubtotalFormatted(
					() -> commerceOrderPriceSubtotalCommerceMoney.format(
						locale));
				setTotal(finalOrderPriceTotalPrice::doubleValue);
				setTotalFormatted(
					() -> commerceOrderPriceTotalCommerceMoney.format(locale));
			}
		};

		BigDecimal taxAmount = commerceOrder.getTaxAmount();

		if (taxAmount != null) {
			summary.setTaxValue(taxAmount::doubleValue);
			summary.setTaxValueFormatted(
				() -> _formatPrice(taxAmount, commerceCurrency, locale));
		}

		_setShippingDiscountOnSummary(
			commerceOrder, commerceCurrency, locale,
			commerceChannel.getPriceDisplayType(), summary);

		_setSubtotalDiscountOnSummary(
			commerceOrder, commerceCurrency, locale,
			commerceChannel.getPriceDisplayType(), summary);

		_setTotalDiscountOnSummary(
			commerceOrder, commerceCurrency, locale,
			commerceChannel.getPriceDisplayType(), summary);

		return summary;
	}

	private void _setShippingDiscountOnSummary(
			CommerceOrder commerceOrder, CommerceCurrency commerceCurrency,
			Locale locale, String priceDisplayType, Summary summary)
		throws Exception {

		BigDecimal shippingDiscountAmount =
			commerceOrder.getShippingDiscountAmount();

		if (shippingDiscountAmount == null) {
			return;
		}

		BigDecimal shippingDiscountPercentageLevel1 =
			commerceOrder.getShippingDiscountPercentageLevel1();
		BigDecimal shippingDiscountPercentageLevel2 =
			commerceOrder.getShippingDiscountPercentageLevel2();
		BigDecimal shippingDiscountPercentageLevel3 =
			commerceOrder.getShippingDiscountPercentageLevel3();
		BigDecimal shippingDiscountPercentageLevel4 =
			commerceOrder.getShippingDiscountPercentageLevel4();

		if (Objects.equals(
				priceDisplayType,
				CommercePricingConstants.TAX_INCLUDED_IN_PRICE)) {

			shippingDiscountAmount =
				commerceOrder.getShippingDiscountWithTaxAmount();

			shippingDiscountPercentageLevel1 =
				commerceOrder.
					getShippingDiscountPercentageLevel1WithTaxAmount();
			shippingDiscountPercentageLevel2 =
				commerceOrder.
					getShippingDiscountPercentageLevel2WithTaxAmount();
			shippingDiscountPercentageLevel3 =
				commerceOrder.
					getShippingDiscountPercentageLevel3WithTaxAmount();
			shippingDiscountPercentageLevel4 =
				commerceOrder.
					getShippingDiscountPercentageLevel4WithTaxAmount();
		}

		BigDecimal finalShippingDiscountPercentageLevel1 =
			shippingDiscountPercentageLevel1;
		BigDecimal finalShippingDiscountPercentageLevel2 =
			shippingDiscountPercentageLevel2;
		BigDecimal finalShippingDiscountPercentageLevel3 =
			shippingDiscountPercentageLevel3;
		BigDecimal finalShippingDiscountPercentageLevel4 =
			shippingDiscountPercentageLevel4;

		summary.setShippingDiscountPercentages(
			() -> _getFormattedDiscountPercentages(
				new BigDecimal[] {
					finalShippingDiscountPercentageLevel1,
					finalShippingDiscountPercentageLevel2,
					finalShippingDiscountPercentageLevel3,
					finalShippingDiscountPercentageLevel4
				},
				locale));

		summary.setShippingDiscountValue(shippingDiscountAmount::doubleValue);

		BigDecimal finalShippingDiscountAmount = shippingDiscountAmount;

		summary.setShippingDiscountValueFormatted(
			() -> _formatPrice(
				finalShippingDiscountAmount, commerceCurrency, locale));
	}

	private void _setSubtotalDiscountOnSummary(
			CommerceOrder commerceOrder, CommerceCurrency commerceCurrency,
			Locale locale, String priceDisplayType, Summary summary)
		throws Exception {

		BigDecimal subtotalDiscountAmount =
			commerceOrder.getSubtotalDiscountAmount();

		if (subtotalDiscountAmount == null) {
			return;
		}

		BigDecimal subtotalDiscountPercentageLevel1 =
			commerceOrder.getSubtotalDiscountPercentageLevel1();
		BigDecimal subtotalDiscountPercentageLevel2 =
			commerceOrder.getSubtotalDiscountPercentageLevel2();
		BigDecimal subtotalDiscountPercentageLevel3 =
			commerceOrder.getSubtotalDiscountPercentageLevel3();
		BigDecimal subtotalDiscountPercentageLevel4 =
			commerceOrder.getSubtotalDiscountPercentageLevel4();

		if (Objects.equals(
				priceDisplayType,
				CommercePricingConstants.TAX_INCLUDED_IN_PRICE)) {

			subtotalDiscountAmount =
				commerceOrder.getSubtotalDiscountWithTaxAmount();

			subtotalDiscountPercentageLevel1 =
				commerceOrder.
					getSubtotalDiscountPercentageLevel1WithTaxAmount();
			subtotalDiscountPercentageLevel2 =
				commerceOrder.
					getSubtotalDiscountPercentageLevel2WithTaxAmount();
			subtotalDiscountPercentageLevel3 =
				commerceOrder.
					getSubtotalDiscountPercentageLevel3WithTaxAmount();
			subtotalDiscountPercentageLevel4 =
				commerceOrder.
					getSubtotalDiscountPercentageLevel4WithTaxAmount();
		}

		BigDecimal finalSubtotalDiscountPercentageLevel1 =
			subtotalDiscountPercentageLevel1;
		BigDecimal finalSubtotalDiscountPercentageLevel2 =
			subtotalDiscountPercentageLevel2;
		BigDecimal finalSubtotalDiscountPercentageLevel3 =
			subtotalDiscountPercentageLevel3;
		BigDecimal finalSubtotalDiscountPercentageLevel4 =
			subtotalDiscountPercentageLevel4;

		summary.setSubtotalDiscountPercentages(
			() -> _getFormattedDiscountPercentages(
				new BigDecimal[] {
					finalSubtotalDiscountPercentageLevel1,
					finalSubtotalDiscountPercentageLevel2,
					finalSubtotalDiscountPercentageLevel3,
					finalSubtotalDiscountPercentageLevel4
				},
				locale));

		summary.setSubtotalDiscountValue(subtotalDiscountAmount::doubleValue);

		BigDecimal finalSubtotalDiscountAmount = subtotalDiscountAmount;

		summary.setSubtotalDiscountValueFormatted(
			() -> _formatPrice(
				finalSubtotalDiscountAmount, commerceCurrency, locale));
	}

	private void _setTotalDiscountOnSummary(
			CommerceOrder commerceOrder, CommerceCurrency commerceCurrency,
			Locale locale, String priceDisplayType, Summary summary)
		throws Exception {

		BigDecimal totalDiscountAmount = commerceOrder.getTotalDiscountAmount();

		if (totalDiscountAmount == null) {
			return;
		}

		BigDecimal totalDiscountPercentageLevel1 =
			commerceOrder.getTotalDiscountPercentageLevel1();
		BigDecimal totalDiscountPercentageLevel2 =
			commerceOrder.getTotalDiscountPercentageLevel2();
		BigDecimal totalDiscountPercentageLevel3 =
			commerceOrder.getTotalDiscountPercentageLevel3();
		BigDecimal totalDiscountPercentageLevel4 =
			commerceOrder.getTotalDiscountPercentageLevel4();

		if (Objects.equals(
				priceDisplayType,
				CommercePricingConstants.TAX_INCLUDED_IN_PRICE)) {

			totalDiscountAmount = commerceOrder.getTotalDiscountWithTaxAmount();

			totalDiscountPercentageLevel1 =
				commerceOrder.getTotalDiscountPercentageLevel1WithTaxAmount();
			totalDiscountPercentageLevel2 =
				commerceOrder.getTotalDiscountPercentageLevel2WithTaxAmount();
			totalDiscountPercentageLevel3 =
				commerceOrder.getTotalDiscountPercentageLevel3WithTaxAmount();
			totalDiscountPercentageLevel4 =
				commerceOrder.getTotalDiscountPercentageLevel4WithTaxAmount();
		}

		BigDecimal finalTotalDiscountPercentageLevel1 =
			totalDiscountPercentageLevel1;
		BigDecimal finalTotalDiscountPercentageLevel2 =
			totalDiscountPercentageLevel2;
		BigDecimal finalTotalDiscountPercentageLevel3 =
			totalDiscountPercentageLevel3;
		BigDecimal finalTotalDiscountPercentageLevel4 =
			totalDiscountPercentageLevel4;

		summary.setTotalDiscountPercentages(
			() -> _getFormattedDiscountPercentages(
				new BigDecimal[] {
					finalTotalDiscountPercentageLevel1,
					finalTotalDiscountPercentageLevel2,
					finalTotalDiscountPercentageLevel3,
					finalTotalDiscountPercentageLevel4
				},
				locale));

		summary.setTotalDiscountValue(totalDiscountAmount::doubleValue);

		BigDecimal finalTotalDiscountAmount = totalDiscountAmount;

		summary.setTotalDiscountValueFormatted(
			() -> _formatPrice(
				finalTotalDiscountAmount, commerceCurrency, locale));
	}

	private Address _toAddress(CommerceAddress commerceAddress, Locale locale) {
		if (commerceAddress == null) {
			return null;
		}

		return new Address() {
			{
				setCity(commerceAddress::getCity);
				setCountry(
					() -> {
						Country commerceAddressCountry =
							commerceAddress.getCountry();

						return commerceAddressCountry.getName(locale);
					});
				setCountryISOCode(
					() -> {
						Country commerceAddressCountry =
							commerceAddress.getCountry();

						return commerceAddressCountry.getA2();
					});
				setDescription(commerceAddress::getDescription);
				setExternalReferenceCode(
					commerceAddress::getExternalReferenceCode);
				setId(commerceAddress::getCommerceAddressId);
				setLatitude(commerceAddress::getLatitude);
				setLongitude(commerceAddress::getLongitude);
				setName(commerceAddress::getName);
				setPhoneNumber(commerceAddress::getPhoneNumber);
				setRegion(
					() -> {
						Region commerceAddressRegion =
							commerceAddress.getRegion();

						if (commerceAddressRegion == null) {
							return null;
						}

						return commerceAddressRegion.getTitle(
							_language.getLanguageId(locale));
					});
				setRegionISOCode(
					() -> {
						Region commerceAddressRegion =
							commerceAddress.getRegion();

						if (commerceAddressRegion == null) {
							return null;
						}

						return commerceAddressRegion.getRegionCode();
					});
				setStreet1(commerceAddress::getStreet1);
				setStreet2(commerceAddress::getStreet2);
				setStreet3(commerceAddress::getStreet3);
				setSubtype(commerceAddress::getSubtype);
				setZip(commerceAddress::getZip);
			}
		};
	}

	private Status _toStatus(
		int orderStatus, String commerceOrderWorkflowStatusLabel,
		String commerceOrderWorkflowStatusLabelI18n) {

		return new Status() {
			{
				setCode(() -> orderStatus);
				setLabel(() -> commerceOrderWorkflowStatusLabel);
				setLabel_i18n(() -> commerceOrderWorkflowStatusLabelI18n);
			}
		};
	}

	private Step _toStep(StepModel stepModel) {
		return new Step() {
			{
				setId(stepModel::getId);
				setLabel(stepModel::getLabel);
				setState(stepModel::getState);
			}
		};
	}

	private static final Snapshot<FriendlyURLSeparatorProvider>
		_friendlyURLSeparatorProviderSnapshot = new Snapshot<>(
			CartDTOConverter.class, FriendlyURLSeparatorProvider.class);

	@Reference(target = DTOConverterConstants.ATTACHMENT_DTO_CONVERTER)
	private DTOConverter<FileEntry, Attachment> _attachmentDTOConverter;

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceOrderItemService _commerceOrderItemService;

	@Reference
	private CommerceOrderService _commerceOrderService;

	@Reference
	private CommerceOrderStepTrackerHelper _commerceOrderStepTrackerHelper;

	@Reference
	private CommerceOrderTypeService _commerceOrderTypeService;

	@Reference
	private CommercePaymentMethodGroupRelLocalService
		_commercePaymentMethodGroupRelLocalService;

	@Reference
	private CommercePaymentMethodRegistry _commercePaymentMethodRegistry;

	@Reference
	private CommercePriceFormatter _commercePriceFormatter;

	@Reference
	private CommerceShippingEngineRegistry _commerceShippingEngineRegistry;

	@Reference
	private CommerceTermEntryLocalService _commerceTermEntryLocalService;

	@Reference
	private Language _language;

}