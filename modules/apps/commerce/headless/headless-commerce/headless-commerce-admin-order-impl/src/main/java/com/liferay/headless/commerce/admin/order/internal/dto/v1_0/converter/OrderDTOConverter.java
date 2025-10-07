/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.order.internal.dto.v1_0.converter;

import com.liferay.account.model.AccountEntry;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.constants.CommerceOrderPaymentConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.model.CommerceMoney;
import com.liferay.commerce.currency.util.CommercePriceFormatter;
import com.liferay.commerce.model.CommerceAddress;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.model.CommerceShippingMethod;
import com.liferay.commerce.order.status.CommerceOrderStatus;
import com.liferay.commerce.order.status.CommerceOrderStatusRegistry;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.service.CommerceOrderService;
import com.liferay.commerce.service.CommerceOrderTypeService;
import com.liferay.commerce.term.model.CommerceTermEntry;
import com.liferay.commerce.term.service.CommerceTermEntryLocalService;
import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.headless.commerce.admin.order.dto.v1_0.Order;
import com.liferay.headless.commerce.admin.order.dto.v1_0.Status;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.language.LanguageResources;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import java.math.BigDecimal;

import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"application.name=Liferay.Headless.Commerce.Admin.Order",
		"dto.class.name=com.liferay.commerce.model.CommerceOrder",
		"version=v1.0"
	},
	service = DTOConverter.class
)
public class OrderDTOConverter implements DTOConverter<CommerceOrder, Order> {

	@Override
	public String getContentType() {
		return Order.class.getSimpleName();
	}

	@Override
	public Order toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		CommerceOrder commerceOrder = _getCommerceOrder(dtoConverterContext);

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannelByOrderGroupId(
				commerceOrder.getGroupId());
		CommerceCurrency commerceCurrency = commerceOrder.getCommerceCurrency();

		Locale locale = dtoConverterContext.getLocale();

		ResourceBundle resourceBundle = LanguageResources.getResourceBundle(
			locale);

		return new Order() {
			{
				setAccountExternalReferenceCode(
					() -> {
						AccountEntry accountEntry =
							commerceOrder.getAccountEntry();

						return accountEntry.getExternalReferenceCode();
					});
				setAccountId(commerceOrder::getCommerceAccountId);
				setActions(dtoConverterContext::getActions);
				setAdvanceStatus(commerceOrder::getAdvanceStatus);
				setAuthor(commerceOrder::getUserName);
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
				setChannelExternalReferenceCode(
					commerceChannel::getExternalReferenceCode);
				setChannelId(commerceChannel::getCommerceChannelId);
				setCouponCode(commerceOrder::getCouponCode);
				setCreateDate(commerceOrder::getCreateDate);
				setCreatorEmailAddress(
					() -> {
						User user = _userLocalService.fetchUser(
							commerceOrder.getUserId());

						if (user == null) {
							return StringPool.BLANK;
						}

						return user.getEmailAddress();
					});
				setCurrencyCode(commerceCurrency::getCode);
				setCurrencyExternalReferenceCode(
					commerceCurrency::getExternalReferenceCode);
				setCurrencyId(commerceCurrency::getCommerceCurrencyId);
				setCustomFields(
					() -> {
						ExpandoBridge expandoBridge =
							commerceOrder.getExpandoBridge();

						return expandoBridge.getAttributes();
					});
				setDeliveryTermDescription(
					commerceOrder::getDeliveryCommerceTermEntryDescription);
				setDeliveryTermExternalReferenceCode(
					() -> {
						CommerceTermEntry commerceTermEntry =
							_commerceTermEntryLocalService.
								fetchCommerceTermEntry(
									commerceOrder.
										getDeliveryCommerceTermEntryId());

						if (commerceTermEntry == null) {
							return null;
						}

						return commerceTermEntry.getExternalReferenceCode();
					});
				setDeliveryTermId(
					commerceOrder::getDeliveryCommerceTermEntryId);
				setDeliveryTermName(
					commerceOrder::getDeliveryCommerceTermEntryName);
				setExternalReferenceCode(
					commerceOrder::getExternalReferenceCode);
				setId(commerceOrder::getCommerceOrderId);
				setLastPriceUpdateDate(commerceOrder::getLastPriceUpdateDate);
				setModifiedDate(commerceOrder::getModifiedDate);
				setName(commerceOrder::getName);
				setOrderDate(commerceOrder::getOrderDate);
				setOrderStatus(commerceOrder::getOrderStatus);
				setOrderStatusInfo(
					() -> _getOrderStatusInfo(
						commerceOrder.getOrderStatus(),
						_getCommerceOrderStatusLabel(
							commerceOrder.getOrderStatus(), locale),
						_getCommerceOrderStatusLabelI18n(
							commerceOrder.getOrderStatus(), locale)));
				setOrderTypeExternalReferenceCode(
					() -> _getOrderTypeExternalReferenceCode(
						commerceOrder.getCommerceOrderTypeId()));
				setOrderTypeId(commerceOrder::getCommerceOrderTypeId);
				setPaymentMethod(commerceOrder::getCommercePaymentMethodKey);
				setPaymentStatus(commerceOrder::getPaymentStatus);
				setPaymentStatusInfo(
					() -> _getPaymentStatusInfo(
						commerceOrder.getPaymentStatus(),
						CommerceOrderPaymentConstants.
							getOrderPaymentStatusLabel(
								commerceOrder.getPaymentStatus()),
						_language.get(
							resourceBundle,
							CommerceOrderPaymentConstants.
								getOrderPaymentStatusLabel(
									commerceOrder.getPaymentStatus()))));
				setPaymentTermDescription(
					commerceOrder::getPaymentCommerceTermEntryDescription);
				setPaymentTermExternalReferenceCode(
					() -> {
						CommerceTermEntry commerceTermEntry =
							_commerceTermEntryLocalService.
								fetchCommerceTermEntry(
									commerceOrder.
										getPaymentCommerceTermEntryId());

						if (commerceTermEntry == null) {
							return null;
						}

						return commerceTermEntry.getExternalReferenceCode();
					});
				setPaymentTermId(commerceOrder::getPaymentCommerceTermEntryId);
				setPaymentTermName(
					commerceOrder::getPaymentCommerceTermEntryName);
				setPrintedNote(commerceOrder::getPrintedNote);
				setPurchaseOrderNumber(commerceOrder::getPurchaseOrderNumber);
				setRequestedDeliveryDate(
					commerceOrder::getRequestedDeliveryDate);
				setShippable(commerceOrder::isShippable);
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

				setShippingAmount(commerceOrder::getShippingAmount);
				setShippingAmountFormatted(
					() -> {
						CommerceMoney commerceOrderShippingAmountCommerceMoney =
							commerceOrder.getShippingMoney();

						return commerceOrderShippingAmountCommerceMoney.format(
							locale);
					});
				setShippingAmountValue(
					() -> {
						CommerceMoney commerceOrderShippingAmountCommerceMoney =
							commerceOrder.getShippingMoney();

						BigDecimal commerceOrderShippingValue =
							commerceOrderShippingAmountCommerceMoney.getPrice();

						if (commerceOrderShippingValue == null) {
							return null;
						}

						return commerceOrderShippingValue.doubleValue();
					});
				setShippingDiscountAmount(
					commerceOrder::getShippingDiscountAmount);
				setShippingDiscountAmountFormatted(
					() -> {
						BigDecimal shippingDiscountAmount =
							getShippingDiscountAmount();

						if (shippingDiscountAmount == null) {
							return null;
						}

						return _formatPrice(
							shippingDiscountAmount, commerceCurrency, locale);
					});
				setShippingDiscountPercentageLevel1(
					() -> {
						BigDecimal shippingDiscountAmount =
							getShippingDiscountAmount();

						if (shippingDiscountAmount == null) {
							return null;
						}

						return commerceOrder.
							getShippingDiscountPercentageLevel1();
					});
				setShippingDiscountPercentageLevel1WithTaxAmount(
					() -> {
						BigDecimal shippingDiscountWithTaxAmount =
							getShippingDiscountWithTaxAmount();

						if (shippingDiscountWithTaxAmount == null) {
							return null;
						}

						return commerceOrder.
							getShippingDiscountPercentageLevel1WithTaxAmount();
					});
				setShippingDiscountPercentageLevel2(
					() -> {
						BigDecimal shippingDiscountAmount =
							getShippingDiscountAmount();

						if (shippingDiscountAmount == null) {
							return null;
						}

						return commerceOrder.
							getShippingDiscountPercentageLevel2();
					});
				setShippingDiscountPercentageLevel2WithTaxAmount(
					() -> {
						BigDecimal shippingDiscountWithTaxAmount =
							getShippingDiscountWithTaxAmount();

						if (shippingDiscountWithTaxAmount == null) {
							return null;
						}

						return commerceOrder.
							getShippingDiscountPercentageLevel2WithTaxAmount();
					});
				setShippingDiscountPercentageLevel3(
					() -> {
						BigDecimal shippingDiscountAmount =
							getShippingDiscountAmount();

						if (shippingDiscountAmount == null) {
							return null;
						}

						return commerceOrder.
							getShippingDiscountPercentageLevel3();
					});
				setShippingDiscountPercentageLevel3WithTaxAmount(
					() -> {
						BigDecimal shippingDiscountWithTaxAmount =
							getShippingDiscountWithTaxAmount();

						if (shippingDiscountWithTaxAmount == null) {
							return null;
						}

						return commerceOrder.
							getShippingDiscountPercentageLevel3WithTaxAmount();
					});
				setShippingDiscountPercentageLevel4(
					() -> {
						BigDecimal shippingDiscountAmount =
							getShippingDiscountAmount();

						if (shippingDiscountAmount == null) {
							return null;
						}

						return commerceOrder.
							getShippingDiscountPercentageLevel4();
					});
				setShippingDiscountPercentageLevel4WithTaxAmount(
					() -> {
						BigDecimal shippingDiscountWithTaxAmount =
							getShippingDiscountWithTaxAmount();

						if (shippingDiscountWithTaxAmount == null) {
							return null;
						}

						return commerceOrder.
							getShippingDiscountPercentageLevel4WithTaxAmount();
					});
				setShippingDiscountWithTaxAmount(
					commerceOrder::getShippingDiscountWithTaxAmount);
				setShippingDiscountWithTaxAmountFormatted(
					() -> {
						BigDecimal shippingDiscountWithTaxAmount =
							getShippingDiscountWithTaxAmount();

						if (shippingDiscountWithTaxAmount == null) {
							return null;
						}

						return _formatPrice(
							shippingDiscountWithTaxAmount, commerceCurrency,
							locale);
					});
				setShippingMethod(
					() -> _getShippingMethodEngineKey(
						commerceOrder.getCommerceShippingMethod()));
				setShippingOption(commerceOrder::getShippingOptionName);
				setShippingWithTaxAmountFormatted(
					() -> {
						CommerceMoney
							commerceOrderShippingWithTaxAmountCommerceMoney =
								commerceOrder.getShippingWithTaxAmountMoney();

						if (commerceOrderShippingWithTaxAmountCommerceMoney ==
								null) {

							return null;
						}

						return commerceOrderShippingWithTaxAmountCommerceMoney.
							format(locale);
					});
				setShippingWithTaxAmountValue(
					() -> {
						CommerceMoney
							commerceOrderShippingWithTaxAmountCommerceMoney =
								commerceOrder.getShippingWithTaxAmountMoney();

						if (commerceOrderShippingWithTaxAmountCommerceMoney ==
								null) {

							return null;
						}

						BigDecimal commerceOrderShippingWithTaxAmountValue =
							commerceOrderShippingWithTaxAmountCommerceMoney.
								getPrice();

						if (commerceOrderShippingWithTaxAmountValue == null) {
							return null;
						}

						return commerceOrderShippingWithTaxAmountValue.
							doubleValue();
					});
				setSubtotalAmount(
					() -> {
						CommerceMoney commerceOrderSubtotalCommerceMoney =
							commerceOrder.getSubtotalMoney();

						if (commerceOrderSubtotalCommerceMoney == null) {
							return null;
						}

						BigDecimal commerceOrderSubtotalValue =
							commerceOrderSubtotalCommerceMoney.getPrice();

						if (commerceOrderSubtotalValue == null) {
							return null;
						}

						return commerceOrderSubtotalValue.doubleValue();
					});
				setSubtotalDiscountAmount(
					commerceOrder::getSubtotalDiscountAmount);
				setSubtotalDiscountAmountFormatted(
					() -> {
						BigDecimal subtotalDiscountAmount =
							getSubtotalDiscountAmount();

						if (subtotalDiscountAmount == null) {
							return null;
						}

						return _formatPrice(
							subtotalDiscountAmount, commerceCurrency, locale);
					});
				setSubtotalDiscountPercentageLevel1(
					() -> {
						BigDecimal subtotalDiscountAmount =
							getSubtotalDiscountAmount();

						if (subtotalDiscountAmount == null) {
							return null;
						}

						return commerceOrder.
							getSubtotalDiscountPercentageLevel1();
					});
				setSubtotalDiscountPercentageLevel1WithTaxAmount(
					() -> {
						BigDecimal subtotalDiscountWithTaxAmount =
							getSubtotalDiscountWithTaxAmount();

						if (subtotalDiscountWithTaxAmount == null) {
							return null;
						}

						return commerceOrder.
							getSubtotalDiscountPercentageLevel1WithTaxAmount();
					});
				setSubtotalDiscountPercentageLevel2(
					() -> {
						BigDecimal subtotalDiscountAmount =
							getSubtotalDiscountAmount();

						if (subtotalDiscountAmount == null) {
							return null;
						}

						return commerceOrder.
							getSubtotalDiscountPercentageLevel2();
					});
				setSubtotalDiscountPercentageLevel2WithTaxAmount(
					() -> {
						BigDecimal subtotalDiscountWithTaxAmount =
							getSubtotalDiscountWithTaxAmount();

						if (subtotalDiscountWithTaxAmount == null) {
							return null;
						}

						return commerceOrder.
							getSubtotalDiscountPercentageLevel2WithTaxAmount();
					});
				setSubtotalDiscountPercentageLevel3(
					() -> {
						BigDecimal subtotalDiscountAmount =
							getSubtotalDiscountAmount();

						if (subtotalDiscountAmount == null) {
							return null;
						}

						return commerceOrder.
							getSubtotalDiscountPercentageLevel3();
					});
				setSubtotalDiscountPercentageLevel3WithTaxAmount(
					() -> {
						BigDecimal subtotalDiscountWithTaxAmount =
							getSubtotalDiscountWithTaxAmount();

						if (subtotalDiscountWithTaxAmount == null) {
							return null;
						}

						return commerceOrder.
							getSubtotalDiscountPercentageLevel3WithTaxAmount();
					});
				setSubtotalDiscountPercentageLevel4(
					() -> {
						BigDecimal subtotalDiscountAmount =
							getSubtotalDiscountAmount();

						if (subtotalDiscountAmount == null) {
							return null;
						}

						return commerceOrder.
							getSubtotalDiscountPercentageLevel4();
					});
				setSubtotalDiscountPercentageLevel4WithTaxAmount(
					() -> {
						BigDecimal subtotalDiscountWithTaxAmount =
							getSubtotalDiscountWithTaxAmount();

						if (subtotalDiscountWithTaxAmount == null) {
							return null;
						}

						return commerceOrder.
							getSubtotalDiscountPercentageLevel4WithTaxAmount();
					});
				setSubtotalDiscountWithTaxAmount(
					commerceOrder::getSubtotalDiscountWithTaxAmount);
				setSubtotalDiscountWithTaxAmountFormatted(
					() -> {
						BigDecimal subtotalDiscountWithTaxAmount =
							getSubtotalDiscountWithTaxAmount();

						if (subtotalDiscountWithTaxAmount == null) {
							return null;
						}

						return _formatPrice(
							subtotalDiscountWithTaxAmount, commerceCurrency,
							locale);
					});
				setSubtotalFormatted(
					() -> {
						CommerceMoney commerceOrderSubtotalCommerceMoney =
							commerceOrder.getSubtotalMoney();

						if (commerceOrderSubtotalCommerceMoney == null) {
							return null;
						}

						return commerceOrderSubtotalCommerceMoney.format(
							locale);
					});
				setSubtotalWithTaxAmountFormatted(
					() -> {
						CommerceMoney
							commerceOrderSubtotalWithTaxAmountCommerceMoney =
								commerceOrder.getSubtotalWithTaxAmountMoney();

						if (commerceOrderSubtotalWithTaxAmountCommerceMoney ==
								null) {

							return null;
						}

						return commerceOrderSubtotalWithTaxAmountCommerceMoney.
							format(locale);
					});
				setSubtotalWithTaxAmountValue(
					() -> {
						CommerceMoney
							commerceOrderSubtotalWithTaxAmountCommerceMoney =
								commerceOrder.getSubtotalWithTaxAmountMoney();

						if (commerceOrderSubtotalWithTaxAmountCommerceMoney ==
								null) {

							return null;
						}

						BigDecimal commerceOrderSubtotalWithTaxAmountValue =
							commerceOrderSubtotalWithTaxAmountCommerceMoney.
								getPrice();

						if (commerceOrderSubtotalWithTaxAmountValue == null) {
							return null;
						}

						return commerceOrderSubtotalWithTaxAmountValue.
							doubleValue();
					});
				setTaxAmount(commerceOrder::getTaxAmount);
				setTaxAmountFormatted(
					() -> {
						BigDecimal taxAmount = getTaxAmount();

						if (taxAmount == null) {
							return null;
						}

						return _formatPrice(
							taxAmount, commerceCurrency, locale);
					});
				setTaxAmountValue(
					() -> {
						BigDecimal taxAmount = getTaxAmount();

						if (taxAmount == null) {
							return null;
						}

						return taxAmount.doubleValue();
					});
				setTotal(commerceOrder::getTotal);
				setTotalAmount(
					() -> {
						CommerceMoney commerceOrderTotalCommerceMoney =
							commerceOrder.getTotalMoney();

						if (commerceOrderTotalCommerceMoney == null) {
							return null;
						}

						BigDecimal commerceOrderTotalValue =
							commerceOrderTotalCommerceMoney.getPrice();

						if (commerceOrderTotalValue == null) {
							return null;
						}

						return commerceOrderTotalValue.doubleValue();
					});
				setTotalDiscountAmount(commerceOrder::getTotalDiscountAmount);
				setTotalDiscountAmountFormatted(
					() -> {
						BigDecimal totalDiscountAmount =
							getTotalDiscountAmount();

						if (totalDiscountAmount == null) {
							return null;
						}

						return _formatPrice(
							totalDiscountAmount, commerceCurrency, locale);
					});
				setTotalDiscountPercentageLevel1(
					() -> {
						BigDecimal totalDiscountAmount =
							getTotalDiscountAmount();

						if (totalDiscountAmount == null) {
							return null;
						}

						return commerceOrder.getTotalDiscountPercentageLevel1();
					});
				setTotalDiscountPercentageLevel1WithTaxAmount(
					() -> {
						BigDecimal totalDiscountWithTaxAmount =
							getTotalDiscountWithTaxAmount();

						if (totalDiscountWithTaxAmount == null) {
							return null;
						}

						return commerceOrder.
							getTotalDiscountPercentageLevel1WithTaxAmount();
					});
				setTotalDiscountPercentageLevel2(
					() -> {
						BigDecimal totalDiscountAmount =
							getTotalDiscountAmount();

						if (totalDiscountAmount == null) {
							return null;
						}

						return commerceOrder.getTotalDiscountPercentageLevel2();
					});
				setTotalDiscountPercentageLevel2WithTaxAmount(
					() -> {
						BigDecimal totalDiscountWithTaxAmount =
							getTotalDiscountWithTaxAmount();

						if (totalDiscountWithTaxAmount == null) {
							return null;
						}

						return commerceOrder.
							getTotalDiscountPercentageLevel2WithTaxAmount();
					});
				setTotalDiscountPercentageLevel3(
					() -> {
						BigDecimal totalDiscountAmount =
							getTotalDiscountAmount();

						if (totalDiscountAmount == null) {
							return null;
						}

						return commerceOrder.getTotalDiscountPercentageLevel3();
					});
				setTotalDiscountPercentageLevel3WithTaxAmount(
					() -> {
						BigDecimal totalDiscountWithTaxAmount =
							getTotalDiscountWithTaxAmount();

						if (totalDiscountWithTaxAmount == null) {
							return null;
						}

						return commerceOrder.
							getTotalDiscountPercentageLevel3WithTaxAmount();
					});
				setTotalDiscountPercentageLevel4(
					() -> {
						BigDecimal totalDiscountAmount =
							getTotalDiscountAmount();

						if (totalDiscountAmount == null) {
							return null;
						}

						return commerceOrder.getTotalDiscountPercentageLevel4();
					});
				setTotalDiscountPercentageLevel4WithTaxAmount(
					() -> {
						BigDecimal totalDiscountWithTaxAmount =
							getTotalDiscountWithTaxAmount();

						if (totalDiscountWithTaxAmount == null) {
							return null;
						}

						return commerceOrder.
							getTotalDiscountPercentageLevel4WithTaxAmount();
					});
				setTotalDiscountWithTaxAmount(
					commerceOrder::getTotalDiscountWithTaxAmount);
				setTotalDiscountWithTaxAmountFormatted(
					() -> {
						BigDecimal totalDiscountWithTaxAmount =
							getTotalDiscountWithTaxAmount();

						if (totalDiscountWithTaxAmount == null) {
							return null;
						}

						return _formatPrice(
							totalDiscountWithTaxAmount, commerceCurrency,
							locale);
					});
				setTotalFormatted(
					() -> {
						CommerceMoney commerceOrderTotalCommerceMoney =
							commerceOrder.getTotalMoney();

						if (commerceOrderTotalCommerceMoney == null) {
							return null;
						}

						return commerceOrderTotalCommerceMoney.format(locale);
					});
				setTotalWithTaxAmountFormatted(
					() -> {
						CommerceMoney commerceOrderTotalWithTaxAmountMoney =
							commerceOrder.getTotalWithTaxAmountMoney();

						if (commerceOrderTotalWithTaxAmountMoney == null) {
							return null;
						}

						return commerceOrderTotalWithTaxAmountMoney.format(
							locale);
					});
				setTotalWithTaxAmountValue(
					() -> {
						CommerceMoney commerceOrderTotalWithTaxAmountMoney =
							commerceOrder.getTotalWithTaxAmountMoney();

						if (commerceOrderTotalWithTaxAmountMoney == null) {
							return null;
						}

						BigDecimal commerceOrderTotalWithTaxAmountValue =
							commerceOrderTotalWithTaxAmountMoney.getPrice();

						if (commerceOrderTotalWithTaxAmountValue == null) {
							return null;
						}

						return commerceOrderTotalWithTaxAmountValue.
							doubleValue();
					});
				setTransactionId(commerceOrder::getTransactionId);
				setWorkflowStatusInfo(
					() -> _toStatus(
						commerceOrder.getStatus(),
						WorkflowConstants.getStatusLabel(
							commerceOrder.getStatus()),
						_language.get(
							resourceBundle,
							WorkflowConstants.getStatusLabel(
								commerceOrder.getStatus()))));
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

	private CommerceOrder _getCommerceOrder(
			DTOConverterContext dtoConverterContext)
		throws Exception {

		CommerceOrder commerceOrder = null;

		boolean secure = GetterUtil.getBoolean(
			dtoConverterContext.getAttribute("secure"), true);

		if (secure) {
			commerceOrder = _commerceOrderService.getCommerceOrder(
				(Long)dtoConverterContext.getId());
		}
		else {
			commerceOrder = _commerceOrderLocalService.getCommerceOrder(
				(Long)dtoConverterContext.getId());
		}

		return commerceOrder;
	}

	private String _getCommerceOrderStatusLabel(
		int orderStatus, Locale locale) {

		String commerceOrderStatusLabel =
			CommerceOrderConstants.getOrderStatusLabel(orderStatus);

		if (!Validator.isBlank(commerceOrderStatusLabel)) {
			return commerceOrderStatusLabel;
		}

		CommerceOrderStatus commerceOrderStatus =
			_commerceOrderStatusRegistry.getCommerceOrderStatus(orderStatus);

		if (commerceOrderStatus != null) {
			return commerceOrderStatus.getLabel(locale);
		}

		return commerceOrderStatusLabel;
	}

	private String _getCommerceOrderStatusLabelI18n(
		int orderStatus, Locale locale) {

		String commerceOrderStatusLabelI18n = _language.get(
			locale, CommerceOrderConstants.getOrderStatusLabel(orderStatus));

		if (!Validator.isBlank(commerceOrderStatusLabelI18n)) {
			return commerceOrderStatusLabelI18n;
		}

		return _getCommerceOrderStatusLabel(orderStatus, locale);
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

	private String _getShippingMethodEngineKey(
		CommerceShippingMethod commerceShippingMethod) {

		if (commerceShippingMethod == null) {
			return null;
		}

		return commerceShippingMethod.getEngineKey();
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

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceOrderLocalService _commerceOrderLocalService;

	@Reference
	private CommerceOrderService _commerceOrderService;

	@Reference
	private CommerceOrderStatusRegistry _commerceOrderStatusRegistry;

	@Reference
	private CommerceOrderTypeService _commerceOrderTypeService;

	@Reference
	private CommercePriceFormatter _commercePriceFormatter;

	@Reference
	private CommerceTermEntryLocalService _commerceTermEntryLocalService;

	@Reference
	private Language _language;

	@Reference
	private UserLocalService _userLocalService;

}