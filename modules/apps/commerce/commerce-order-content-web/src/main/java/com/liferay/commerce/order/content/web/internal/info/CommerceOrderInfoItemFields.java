/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.content.web.internal.info;

import com.liferay.commerce.model.CommerceOrder;
import com.liferay.info.field.InfoField;
import com.liferay.info.field.type.BooleanInfoFieldType;
import com.liferay.info.field.type.DateInfoFieldType;
import com.liferay.info.field.type.NumberInfoFieldType;
import com.liferay.info.field.type.TextInfoFieldType;
import com.liferay.info.localized.InfoLocalizedValue;

/**
 * @author Danny Situ
 */
public class CommerceOrderInfoItemFields {

	public static final InfoField<NumberInfoFieldType> accountIdInfoField =
		BuilderHolder._builder.infoFieldType(
			NumberInfoFieldType.INSTANCE
		).name(
			"accountId"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "account-id")
		).build();
	public static final InfoField<TextInfoFieldType> accountNameInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"accountName"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "account-name")
		).build();
	public static final InfoField<TextInfoFieldType> advanceStatusInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"advanceStatus"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "advance-status")
		).build();
	public static final InfoField<BooleanInfoFieldType> approvedInfoField =
		BuilderHolder._builder.infoFieldType(
			BooleanInfoFieldType.INSTANCE
		).name(
			"approved"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "approved")
		).build();
	public static final InfoField<BooleanInfoFieldType>
		awaitingPickupOrderStatusInfoField =
			BuilderHolder._builder.infoFieldType(
				BooleanInfoFieldType.INSTANCE
			).name(
				"awaitingPickupOrderStatus"
			).labelInfoLocalizedValue(
				InfoLocalizedValue.localize(
					CommerceOrderInfoItemFields.class, "awaiting-pickup")
			).build();
	public static final InfoField<TextInfoFieldType> b2bInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"b2b"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "b2b")
		).build();
	public static final InfoField<TextInfoFieldType>
		billingAddressCityInfoField = BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"billingAddressCity"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "billing-address-city")
		).build();
	public static final InfoField<TextInfoFieldType>
		billingAddressCountryInfoField = BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"billingAddressCountry"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "billing-address-country")
		).build();
	public static final InfoField<NumberInfoFieldType>
		billingAddressIdInfoField = BuilderHolder._builder.infoFieldType(
			NumberInfoFieldType.INSTANCE
		).name(
			"billingAddressId"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "billing-address-id")
		).build();
	public static final InfoField<TextInfoFieldType>
		billingAddressPhoneNumberInfoField =
			BuilderHolder._builder.infoFieldType(
				TextInfoFieldType.INSTANCE
			).name(
				"billingAddressPhoneNumber"
			).labelInfoLocalizedValue(
				InfoLocalizedValue.localize(
					CommerceOrderInfoItemFields.class,
					"billing-address-phone-number")
			).build();
	public static final InfoField<TextInfoFieldType>
		billingAddressRegionInfoField = BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"billingAddressRegion"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "billing-address-region")
		).build();
	public static final InfoField<TextInfoFieldType>
		billingAddressStreet1InfoField = BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"billingAddressStreet1"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "billing-address-street1")
		).build();
	public static final InfoField<TextInfoFieldType>
		billingAddressStreet2InfoField = BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"billingAddressStreet2"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "billing-address-street2")
		).build();
	public static final InfoField<TextInfoFieldType>
		billingAddressStreet3InfoField = BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"billingAddressStreet3"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "billing-address-street3")
		).build();
	public static final InfoField<TextInfoFieldType>
		billingAddressZipInfoField = BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"billingAddressZip"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "billing-address-zip")
		).build();
	public static final InfoField<BooleanInfoFieldType>
		cancelledOrderStatusInfoField = BuilderHolder._builder.infoFieldType(
			BooleanInfoFieldType.INSTANCE
		).name(
			"cancelledOrderStatus"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "cancelled")
		).build();
	public static final InfoField<NumberInfoFieldType> companyIdInfoField =
		BuilderHolder._builder.infoFieldType(
			NumberInfoFieldType.INSTANCE
		).name(
			"companyId"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "company-id")
		).build();
	public static final InfoField<BooleanInfoFieldType>
		completedOrderStatusInfoField = BuilderHolder._builder.infoFieldType(
			BooleanInfoFieldType.INSTANCE
		).name(
			"completedOrderStatus"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "completed")
		).build();
	public static final InfoField<TextInfoFieldType> couponCodeInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"couponCode"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "coupon-code")
		).build();
	public static final InfoField<DateInfoFieldType> createDateInfoField =
		BuilderHolder._builder.infoFieldType(
			DateInfoFieldType.INSTANCE
		).name(
			"createDate"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "create-date")
		).build();
	public static final InfoField<NumberInfoFieldType> currencyIdInfoField =
		BuilderHolder._builder.infoFieldType(
			NumberInfoFieldType.INSTANCE
		).name(
			"currencyId"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "currency-id")
		).build();
	public static final InfoField<BooleanInfoFieldType>
		declinedOrderStatusInfoField = BuilderHolder._builder.infoFieldType(
			BooleanInfoFieldType.INSTANCE
		).name(
			"declinedOrderStatus"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "declined")
		).build();
	public static final InfoField<TextInfoFieldType>
		defaultLanguageIdInfoField = BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"defaultLanguageId"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "default-languageId")
		).build();
	public static final InfoField<TextInfoFieldType>
		deliveryCommerceTermEntryDescriptionInfoField =
			BuilderHolder._builder.infoFieldType(
				TextInfoFieldType.INSTANCE
			).name(
				"deliveryCommerceTermEntryDescription"
			).labelInfoLocalizedValue(
				InfoLocalizedValue.localize(
					CommerceOrderInfoItemFields.class,
					"delivery-term-description")
			).build();
	public static final InfoField<NumberInfoFieldType>
		deliveryCommerceTermEntryIdInfoField =
			BuilderHolder._builder.infoFieldType(
				NumberInfoFieldType.INSTANCE
			).name(
				"deliveryCommerceTermEntryId"
			).labelInfoLocalizedValue(
				InfoLocalizedValue.localize(
					CommerceOrderInfoItemFields.class, "delivery-term-entry-id")
			).build();
	public static final InfoField<TextInfoFieldType>
		deliveryCommerceTermEntryNameInfoField =
			BuilderHolder._builder.infoFieldType(
				TextInfoFieldType.INSTANCE
			).name(
				"deliveryCommerceTermEntryName"
			).labelInfoLocalizedValue(
				InfoLocalizedValue.localize(
					CommerceOrderInfoItemFields.class, "delivery-term-name")
			).build();
	public static final InfoField<BooleanInfoFieldType> deniedInfoField =
		BuilderHolder._builder.infoFieldType(
			BooleanInfoFieldType.INSTANCE
		).name(
			"denied"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "denied")
		).build();
	public static final InfoField<BooleanInfoFieldType>
		disputedOrderStatusInfoField = BuilderHolder._builder.infoFieldType(
			BooleanInfoFieldType.INSTANCE
		).name(
			"disputedOrderStatus"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "disputed")
		).build();
	public static final InfoField<BooleanInfoFieldType> draftInfoField =
		BuilderHolder._builder.infoFieldType(
			BooleanInfoFieldType.INSTANCE
		).name(
			"draft"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "draft")
		).build();
	public static final InfoField<BooleanInfoFieldType> emptyInfoField =
		BuilderHolder._builder.infoFieldType(
			BooleanInfoFieldType.INSTANCE
		).name(
			"empty"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "empty")
		).build();
	public static final InfoField<BooleanInfoFieldType> expiredInfoField =
		BuilderHolder._builder.infoFieldType(
			BooleanInfoFieldType.INSTANCE
		).name(
			"expired"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "expired")
		).build();
	public static final InfoField<TextInfoFieldType>
		formattedDiscountAmountInfoField = BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"formattedDiscountAmount"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "formatted-discount-amount")
		).build();
	public static final InfoField<TextInfoFieldType>
		formattedDiscountWithTaxAmountInfoField =
			BuilderHolder._builder.infoFieldType(
				TextInfoFieldType.INSTANCE
			).name(
				"formattedDiscountWithTaxAmount"
			).labelInfoLocalizedValue(
				InfoLocalizedValue.localize(
					CommerceOrderInfoItemFields.class,
					"formatted-discount-with-tax-amount")
			).build();
	public static final InfoField<TextInfoFieldType>
		formattedShippingAmountInfoField = BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"formattedShippingAmount"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "formatted-shipping-amount")
		).build();
	public static final InfoField<TextInfoFieldType>
		formattedShippingWithTaxAmountInfoField =
			BuilderHolder._builder.infoFieldType(
				TextInfoFieldType.INSTANCE
			).name(
				"formattedShippingWithTaxAmount"
			).labelInfoLocalizedValue(
				InfoLocalizedValue.localize(
					CommerceOrderInfoItemFields.class,
					"formatted-shipping-with-tax-amount")
			).build();
	public static final InfoField<TextInfoFieldType>
		formattedSubtotalAmountInfoField = BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"formattedSubtotalAmount"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "formatted-subtotal-amount")
		).build();
	public static final InfoField<TextInfoFieldType>
		formattedSubtotalWithTaxAmountInfoField =
			BuilderHolder._builder.infoFieldType(
				TextInfoFieldType.INSTANCE
			).name(
				"formattedSubtotalWithTaxAmount"
			).labelInfoLocalizedValue(
				InfoLocalizedValue.localize(
					CommerceOrderInfoItemFields.class,
					"formatted-subtotal-with-tax-amount")
			).build();
	public static final InfoField<TextInfoFieldType>
		formattedTotalAmountInfoField = BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"formattedTotalAmount"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "formatted-total-amount")
		).build();
	public static final InfoField<TextInfoFieldType>
		formattedTotalWithTaxAmountInfoField =
			BuilderHolder._builder.infoFieldType(
				TextInfoFieldType.INSTANCE
			).name(
				"formattedTotalWithTaxAmount"
			).labelInfoLocalizedValue(
				InfoLocalizedValue.localize(
					CommerceOrderInfoItemFields.class,
					"formatted-total-with-tax-amount")
			).build();
	public static final InfoField<NumberInfoFieldType> groupIdInfoField =
		BuilderHolder._builder.infoFieldType(
			NumberInfoFieldType.INSTANCE
		).name(
			"groupId"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "group-id")
		).build();
	public static final InfoField<BooleanInfoFieldType> guestOrderInfoField =
		BuilderHolder._builder.infoFieldType(
			BooleanInfoFieldType.INSTANCE
		).name(
			"guestOrder"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "guest-order")
		).build();
	public static final InfoField<BooleanInfoFieldType>
		inProgressOrderStatusInfoField = BuilderHolder._builder.infoFieldType(
			BooleanInfoFieldType.INSTANCE
		).name(
			"inProgressOrderStatus"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "in-progress")
		).build();
	public static final InfoField<BooleanInfoFieldType> inactiveInfoField =
		BuilderHolder._builder.infoFieldType(
			BooleanInfoFieldType.INSTANCE
		).name(
			"inactive"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "inactive")
		).build();
	public static final InfoField<BooleanInfoFieldType> incompleteInfoField =
		BuilderHolder._builder.infoFieldType(
			BooleanInfoFieldType.INSTANCE
		).name(
			"incomplete"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "incomplete")
		).build();
	public static final InfoField<DateInfoFieldType>
		lastPriceUpdateDateInfoField = BuilderHolder._builder.infoFieldType(
			DateInfoFieldType.INSTANCE
		).name(
			"lastPriceUpdateDate"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "last-price-update-date")
		).build();
	public static final InfoField<BooleanInfoFieldType>
		manuallyAdjustedInfoField = BuilderHolder._builder.infoFieldType(
			BooleanInfoFieldType.INSTANCE
		).name(
			"manuallyAdjusted"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "manually-adjusted")
		).build();
	public static final InfoField<DateInfoFieldType> modifiedDateInfoField =
		BuilderHolder._builder.infoFieldType(
			DateInfoFieldType.INSTANCE
		).name(
			"modifiedDate"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "modified-date")
		).build();
	public static final InfoField<TextInfoFieldType> nameInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"name"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "name")
		).build();
	public static final InfoField<BooleanInfoFieldType>
		onHoldOrderStatusInfoField = BuilderHolder._builder.infoFieldType(
			BooleanInfoFieldType.INSTANCE
		).name(
			"onHoldOrderStatus"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "on-hold")
		).build();
	public static final InfoField<BooleanInfoFieldType>
		openOrderStatusInfoField = BuilderHolder._builder.infoFieldType(
			BooleanInfoFieldType.INSTANCE
		).name(
			"openOrderStatus"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "open")
		).build();
	public static final InfoField<DateInfoFieldType> orderDateInfoField =
		BuilderHolder._builder.infoFieldType(
			DateInfoFieldType.INSTANCE
		).name(
			"orderDate"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "order-date")
		).build();
	public static final InfoField<NumberInfoFieldType> orderIdInfoField =
		BuilderHolder._builder.infoFieldType(
			NumberInfoFieldType.INSTANCE
		).name(
			"orderId"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "order-id")
		).build();
	public static final InfoField<TextInfoFieldType> orderStatusInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"orderStatus"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "order-status")
		).build();
	public static final InfoField<NumberInfoFieldType> orderTypeIdInfoField =
		BuilderHolder._builder.infoFieldType(
			NumberInfoFieldType.INSTANCE
		).name(
			"orderTypeId"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "order-type-id")
		).build();
	public static final InfoField<TextInfoFieldType> orderTypeNameInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"orderTypeName"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "order-type-name")
		).build();
	public static final InfoField<BooleanInfoFieldType>
		partiallyRefundedOrderStatusInfoField =
			BuilderHolder._builder.infoFieldType(
				BooleanInfoFieldType.INSTANCE
			).name(
				"partiallyRefundedOrderStatus"
			).labelInfoLocalizedValue(
				InfoLocalizedValue.localize(
					CommerceOrderInfoItemFields.class, "partially-refunded")
			).build();
	public static final InfoField<BooleanInfoFieldType>
		partiallyShippedOrderStatusInfoField =
			BuilderHolder._builder.infoFieldType(
				BooleanInfoFieldType.INSTANCE
			).name(
				"partiallyShippedOrderStatus"
			).labelInfoLocalizedValue(
				InfoLocalizedValue.localize(
					CommerceOrderInfoItemFields.class, "partially-shipped")
			).build();
	public static final InfoField<TextInfoFieldType> paymenMethodKeyInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"paymenMethodKey"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "payment-method-key")
		).build();
	public static final InfoField<TextInfoFieldType>
		paymentCommerceTermEntryDescriptionInfoField =
			BuilderHolder._builder.infoFieldType(
				TextInfoFieldType.INSTANCE
			).name(
				"paymentCommerceTermEntryDescription"
			).labelInfoLocalizedValue(
				InfoLocalizedValue.localize(
					CommerceOrderInfoItemFields.class,
					"payment-term-entry-description")
			).build();
	public static final InfoField<NumberInfoFieldType>
		paymentCommerceTermEntryIdInfoField =
			BuilderHolder._builder.infoFieldType(
				NumberInfoFieldType.INSTANCE
			).name(
				"paymentCommerceTermEntryId"
			).labelInfoLocalizedValue(
				InfoLocalizedValue.localize(
					CommerceOrderInfoItemFields.class, "payment-term-entry-id")
			).build();
	public static final InfoField<TextInfoFieldType>
		paymentCommerceTermEntryNameInfoField =
			BuilderHolder._builder.infoFieldType(
				TextInfoFieldType.INSTANCE
			).name(
				"paymentCommerceTermEntryName"
			).labelInfoLocalizedValue(
				InfoLocalizedValue.localize(
					CommerceOrderInfoItemFields.class,
					"payment-term-entry-name")
			).build();
	public static final InfoField<TextInfoFieldType> paymentStatusInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"paymentStatus"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "payment-status")
		).build();
	public static final InfoField<BooleanInfoFieldType> pendingInfoField =
		BuilderHolder._builder.infoFieldType(
			BooleanInfoFieldType.INSTANCE
		).name(
			"pending"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "pending")
		).build();
	public static final InfoField<BooleanInfoFieldType>
		pendingOrderStatusInfoField = BuilderHolder._builder.infoFieldType(
			BooleanInfoFieldType.INSTANCE
		).name(
			"pendingOrderStatus"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "pending")
		).build();
	public static final InfoField<BooleanInfoFieldType>
		processingOrderStatusInfoField = BuilderHolder._builder.infoFieldType(
			BooleanInfoFieldType.INSTANCE
		).name(
			"processingOrderStatus"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "processing")
		).build();
	public static final InfoField<NumberInfoFieldType>
		purchaseOrderNumberInfoField = BuilderHolder._builder.infoFieldType(
			NumberInfoFieldType.INSTANCE
		).name(
			"purchaseOrderNumber"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "purchase-order-number")
		).build();
	public static final InfoField<BooleanInfoFieldType>
		refundedOrderStatusInfoField = BuilderHolder._builder.infoFieldType(
			BooleanInfoFieldType.INSTANCE
		).name(
			"refundedOrderStatus"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "refunded")
		).build();
	public static final InfoField<DateInfoFieldType>
		requestedDeliveryDateInfoField = BuilderHolder._builder.infoFieldType(
			DateInfoFieldType.INSTANCE
		).name(
			"requestedDeliveryDate"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "requested-delivery-date")
		).build();
	public static final InfoField<BooleanInfoFieldType> scheduledInfoField =
		BuilderHolder._builder.infoFieldType(
			BooleanInfoFieldType.INSTANCE
		).name(
			"scheduled"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "scheduled")
		).build();
	public static final InfoField<NumberInfoFieldType> scopeGroupIdInfoField =
		BuilderHolder._builder.infoFieldType(
			NumberInfoFieldType.INSTANCE
		).name(
			"scopeGroupId"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "scope-group-id")
		).build();
	public static final InfoField<BooleanInfoFieldType>
		shippedOrderStatusInfoField = BuilderHolder._builder.infoFieldType(
			BooleanInfoFieldType.INSTANCE
		).name(
			"shippedOrderStatus"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "shipped")
		).build();
	public static final InfoField<TextInfoFieldType>
		shippingAddressCityInfoField = BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"shippingAddressCity"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "shipping-address-city")
		).build();
	public static final InfoField<TextInfoFieldType>
		shippingAddressCountryInfoField = BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"shippingAddressCountry"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "shipping-address-country")
		).build();
	public static final InfoField<NumberInfoFieldType>
		shippingAddressIdInfoField = BuilderHolder._builder.infoFieldType(
			NumberInfoFieldType.INSTANCE
		).name(
			"shippingAddressId"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "shipping-address-id")
		).build();
	public static final InfoField<TextInfoFieldType>
		shippingAddressPhoneNumberInfoField =
			BuilderHolder._builder.infoFieldType(
				TextInfoFieldType.INSTANCE
			).name(
				"shippingAddressPhoneNumber"
			).labelInfoLocalizedValue(
				InfoLocalizedValue.localize(
					CommerceOrderInfoItemFields.class,
					"shipping-address-phone-number")
			).build();
	public static final InfoField<TextInfoFieldType>
		shippingAddressRegionInfoField = BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"shippingAddressRegion"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "shipping-address-region")
		).build();
	public static final InfoField<TextInfoFieldType>
		shippingAddressStreet1InfoField = BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"shippingAddressStreet1"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "shipping-address-street1")
		).build();
	public static final InfoField<TextInfoFieldType>
		shippingAddressStreet2InfoField = BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"shippingAddressStreet2"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "shipping-address-street2")
		).build();
	public static final InfoField<TextInfoFieldType>
		shippingAddressStreet3InfoField = BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"shippingAddressStreet3"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "shipping-address-street3")
		).build();
	public static final InfoField<TextInfoFieldType>
		shippingAddressZipInfoField = BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"shippingAddressZip"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "shipping-address-zip")
		).build();
	public static final InfoField<NumberInfoFieldType> shippingAmountInfoField =
		BuilderHolder._builder.infoFieldType(
			NumberInfoFieldType.INSTANCE
		).name(
			"shippingAmount"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "shipping-amount")
		).build();
	public static final InfoField<NumberInfoFieldType>
		shippingDiscountAmountInfoField = BuilderHolder._builder.infoFieldType(
			NumberInfoFieldType.INSTANCE
		).name(
			"shippingDiscountAmount"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "shipping-discount-amount")
		).build();
	public static final InfoField<NumberInfoFieldType>
		shippingDiscountPercentageLevel1InfoField =
			BuilderHolder._builder.infoFieldType(
				NumberInfoFieldType.INSTANCE
			).name(
				"shippingDiscountPercentageLevel1"
			).labelInfoLocalizedValue(
				InfoLocalizedValue.localize(
					CommerceOrderInfoItemFields.class,
					"shipping-discount-percentage-level-1")
			).build();
	public static final InfoField<NumberInfoFieldType>
		shippingDiscountPercentageLevel1WithTaxAmountInfoField =
			BuilderHolder._builder.infoFieldType(
				NumberInfoFieldType.INSTANCE
			).name(
				"shippingDiscountPercentageLevel1WithTaxAmount"
			).labelInfoLocalizedValue(
				InfoLocalizedValue.localize(
					CommerceOrderInfoItemFields.class,
					"shipping-discount-percentage-level-1-with-tax-amount")
			).build();
	public static final InfoField<NumberInfoFieldType>
		shippingDiscountPercentageLevel2InfoField =
			BuilderHolder._builder.infoFieldType(
				NumberInfoFieldType.INSTANCE
			).name(
				"shippingDiscountPercentageLevel2"
			).labelInfoLocalizedValue(
				InfoLocalizedValue.localize(
					CommerceOrderInfoItemFields.class,
					"shipping-discount-percentage-level-2")
			).build();
	public static final InfoField<NumberInfoFieldType>
		shippingDiscountPercentageLevel2WithTaxAmountInfoField =
			BuilderHolder._builder.infoFieldType(
				NumberInfoFieldType.INSTANCE
			).name(
				"shippingDiscountPercentageLevel2WithTaxAmount"
			).labelInfoLocalizedValue(
				InfoLocalizedValue.localize(
					CommerceOrderInfoItemFields.class,
					"shipping-discount-percentage-level-2-with-tax-amount")
			).build();
	public static final InfoField<NumberInfoFieldType>
		shippingDiscountPercentageLevel3InfoField =
			BuilderHolder._builder.infoFieldType(
				NumberInfoFieldType.INSTANCE
			).name(
				"shippingDiscountPercentageLevel3"
			).labelInfoLocalizedValue(
				InfoLocalizedValue.localize(
					CommerceOrderInfoItemFields.class,
					"shipping-discount-percentage-level-3")
			).build();
	public static final InfoField<NumberInfoFieldType>
		shippingDiscountPercentageLevel3WithTaxAmountInfoField =
			BuilderHolder._builder.infoFieldType(
				NumberInfoFieldType.INSTANCE
			).name(
				"shippingDiscountPercentageLevel3WithTaxAmount"
			).labelInfoLocalizedValue(
				InfoLocalizedValue.localize(
					CommerceOrderInfoItemFields.class,
					"shipping-discount-percentage-level-3-with-tax-amount")
			).build();
	public static final InfoField<NumberInfoFieldType>
		shippingDiscountPercentageLevel4InfoField =
			BuilderHolder._builder.infoFieldType(
				NumberInfoFieldType.INSTANCE
			).name(
				"shippingDiscountPercentageLevel4"
			).labelInfoLocalizedValue(
				InfoLocalizedValue.localize(
					CommerceOrderInfoItemFields.class,
					"shipping-discount-percentage-level-4")
			).build();
	public static final InfoField<NumberInfoFieldType>
		shippingDiscountPercentageLevel4WithTaxAmountInfoField =
			BuilderHolder._builder.infoFieldType(
				NumberInfoFieldType.INSTANCE
			).name(
				"shippingDiscountPercentageLevel4WithTaxAmount"
			).labelInfoLocalizedValue(
				InfoLocalizedValue.localize(
					CommerceOrderInfoItemFields.class,
					"shipping-discount-percentage-level-4-with-tax-amount")
			).build();
	public static final InfoField<NumberInfoFieldType>
		shippingMethodIdInfoField = BuilderHolder._builder.infoFieldType(
			NumberInfoFieldType.INSTANCE
		).name(
			"shippingMethodId"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "shipping-method-id")
		).build();
	public static final InfoField<TextInfoFieldType>
		shippingOptionNameInfoField = BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"shippingOptionName"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "shipping-option-name")
		).build();
	public static final InfoField<NumberInfoFieldType>
		shippingWithTaxAmountInfoField = BuilderHolder._builder.infoFieldType(
			NumberInfoFieldType.INSTANCE
		).name(
			"shippingWithTaxAmount"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "shipping-with-tax-amount")
		).build();
	public static final InfoField<TextInfoFieldType> stagedModelTypeInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"stagedModelType"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "staged-model-type")
		).build();
	public static final InfoField<TextInfoFieldType> statusByUserIdInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"statusByUserId"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "status-by-userId")
		).build();
	public static final InfoField<TextInfoFieldType> statusByUserNameInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"statusByUserName"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "status-by-userName")
		).build();
	public static final InfoField<TextInfoFieldType> statusByUserUuidInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"statusByUserUuid"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "status-by-userUuid")
		).build();
	public static final InfoField<DateInfoFieldType> statusDateInfoField =
		BuilderHolder._builder.infoFieldType(
			DateInfoFieldType.INSTANCE
		).name(
			"statusDate"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "status-date")
		).build();
	public static final InfoField<TextInfoFieldType> statusInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"status"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "status")
		).build();
	public static final InfoField<BooleanInfoFieldType>
		subscriptionOrderInfoField = BuilderHolder._builder.infoFieldType(
			BooleanInfoFieldType.INSTANCE
		).name(
			"subscriptionOrder"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "subscription-order")
		).build();
	public static final InfoField<BooleanInfoFieldType>
		subscriptionOrderStatusInfoField = BuilderHolder._builder.infoFieldType(
			BooleanInfoFieldType.INSTANCE
		).name(
			"subscriptionOrderStatus"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "subscription")
		).build();
	public static final InfoField<NumberInfoFieldType>
		subtotalDiscountAmountInfoField = BuilderHolder._builder.infoFieldType(
			NumberInfoFieldType.INSTANCE
		).name(
			"subtotalDiscountAmount"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "subtotal-discount-amount")
		).build();
	public static final InfoField<NumberInfoFieldType>
		subtotalDiscountPercentageLevel1InfoField =
			BuilderHolder._builder.infoFieldType(
				NumberInfoFieldType.INSTANCE
			).name(
				"subtotalDiscountPercentageLevel1"
			).labelInfoLocalizedValue(
				InfoLocalizedValue.localize(
					CommerceOrderInfoItemFields.class,
					"subtotal-discount-percentage-level-1")
			).build();
	public static final InfoField<NumberInfoFieldType>
		subtotalDiscountPercentageLevel1WithTaxAmountInfoField =
			BuilderHolder._builder.infoFieldType(
				NumberInfoFieldType.INSTANCE
			).name(
				"subtotalDiscountPercentageLevel1WithTaxAmount"
			).labelInfoLocalizedValue(
				InfoLocalizedValue.localize(
					CommerceOrderInfoItemFields.class,
					"subtotal-discount-percentage-level-1-with-tax-amount")
			).build();
	public static final InfoField<NumberInfoFieldType>
		subtotalDiscountPercentageLevel2InfoField =
			BuilderHolder._builder.infoFieldType(
				NumberInfoFieldType.INSTANCE
			).name(
				"subtotalDiscountPercentageLevel2"
			).labelInfoLocalizedValue(
				InfoLocalizedValue.localize(
					CommerceOrderInfoItemFields.class,
					"subtotal-discount-percentage-level-2")
			).build();
	public static final InfoField<NumberInfoFieldType>
		subtotalDiscountPercentageLevel2WithTaxAmountInfoField =
			BuilderHolder._builder.infoFieldType(
				NumberInfoFieldType.INSTANCE
			).name(
				"subtotalDiscountPercentageLevel2WithTaxAmount"
			).labelInfoLocalizedValue(
				InfoLocalizedValue.localize(
					CommerceOrderInfoItemFields.class,
					"subtotal-discount-percentage-level-2-with-tax-amount")
			).build();
	public static final InfoField<NumberInfoFieldType>
		subtotalDiscountPercentageLevel3InfoField =
			BuilderHolder._builder.infoFieldType(
				NumberInfoFieldType.INSTANCE
			).name(
				"subtotalDiscountPercentageLevel3"
			).labelInfoLocalizedValue(
				InfoLocalizedValue.localize(
					CommerceOrderInfoItemFields.class,
					"subtotal-discount-percentage-level-3")
			).build();
	public static final InfoField<NumberInfoFieldType>
		subtotalDiscountPercentageLevel3WithTaxAmountInfoField =
			BuilderHolder._builder.infoFieldType(
				NumberInfoFieldType.INSTANCE
			).name(
				"subtotalDiscountPercentageLevel3WithTaxAmount"
			).labelInfoLocalizedValue(
				InfoLocalizedValue.localize(
					CommerceOrderInfoItemFields.class,
					"subtotal-discount-percentage-level-3-with-tax-amount")
			).build();
	public static final InfoField<NumberInfoFieldType>
		subtotalDiscountPercentageLevel4InfoField =
			BuilderHolder._builder.infoFieldType(
				NumberInfoFieldType.INSTANCE
			).name(
				"subtotalDiscountPercentageLevel4"
			).labelInfoLocalizedValue(
				InfoLocalizedValue.localize(
					CommerceOrderInfoItemFields.class,
					"subtotal-discount-percentage-level-4")
			).build();
	public static final InfoField<NumberInfoFieldType>
		subtotalDiscountPercentageLevel4WithTaxAmountInfoField =
			BuilderHolder._builder.infoFieldType(
				NumberInfoFieldType.INSTANCE
			).name(
				"subtotalDiscountPercentageLevel4WithTaxAmount"
			).labelInfoLocalizedValue(
				InfoLocalizedValue.localize(
					CommerceOrderInfoItemFields.class,
					"subtotal-discount-percentage-level-4-with-tax-amount")
			).build();
	public static final InfoField<NumberInfoFieldType>
		subtotalDiscountWithTaxAmountInfoField =
			BuilderHolder._builder.infoFieldType(
				NumberInfoFieldType.INSTANCE
			).name(
				"subtotalDiscounrWithTaxAmount"
			).labelInfoLocalizedValue(
				InfoLocalizedValue.localize(
					CommerceOrderInfoItemFields.class,
					"subtotal-discount-with-tax-amount")
			).build();
	public static final InfoField<NumberInfoFieldType> subtotalInfoField =
		BuilderHolder._builder.infoFieldType(
			NumberInfoFieldType.INSTANCE
		).name(
			"subtotal"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "subtotal")
		).build();
	public static final InfoField<NumberInfoFieldType>
		subtotalWithTaxAmountInfoField = BuilderHolder._builder.infoFieldType(
			NumberInfoFieldType.INSTANCE
		).name(
			"subtotalWithTaxAmount"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "subtotal-with-tax-amount")
		).build();
	public static final InfoField<NumberInfoFieldType> taxAmountInfoField =
		BuilderHolder._builder.infoFieldType(
			NumberInfoFieldType.INSTANCE
		).name(
			"taxAmount"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "tax-amount")
		).build();
	public static final InfoField<NumberInfoFieldType>
		totalDiscountAmountInfoField = BuilderHolder._builder.infoFieldType(
			NumberInfoFieldType.INSTANCE
		).name(
			"totalDiscountAmount"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "total-discount-amount")
		).build();
	public static final InfoField<NumberInfoFieldType>
		totalDiscountPercentageLevel1InfoField =
			BuilderHolder._builder.infoFieldType(
				NumberInfoFieldType.INSTANCE
			).name(
				"totalDiscountPercentageLevel1"
			).labelInfoLocalizedValue(
				InfoLocalizedValue.localize(
					CommerceOrderInfoItemFields.class,
					"total-discount-percentage-level-1")
			).build();
	public static final InfoField<NumberInfoFieldType>
		totalDiscountPercentageLevel1WithTaxAmountInfoField =
			BuilderHolder._builder.infoFieldType(
				NumberInfoFieldType.INSTANCE
			).name(
				"totalDiscountPercentageLevel1WithTaxAmount"
			).labelInfoLocalizedValue(
				InfoLocalizedValue.localize(
					CommerceOrderInfoItemFields.class,
					"total-discount-percentage-level-1-with-tax-amount")
			).build();
	public static final InfoField<NumberInfoFieldType>
		totalDiscountPercentageLevel2InfoField =
			BuilderHolder._builder.infoFieldType(
				NumberInfoFieldType.INSTANCE
			).name(
				"totalDiscountPercentageLevel2"
			).labelInfoLocalizedValue(
				InfoLocalizedValue.localize(
					CommerceOrderInfoItemFields.class,
					"total-discount-percentage-level-2")
			).build();
	public static final InfoField<NumberInfoFieldType>
		totalDiscountPercentageLevel2WithTaxAmountInfoField =
			BuilderHolder._builder.infoFieldType(
				NumberInfoFieldType.INSTANCE
			).name(
				"totalDiscountPercentageLevel2WithTaxAmount"
			).labelInfoLocalizedValue(
				InfoLocalizedValue.localize(
					CommerceOrderInfoItemFields.class,
					"total-discount-percentage-level-2-with-tax-amount")
			).build();
	public static final InfoField<NumberInfoFieldType>
		totalDiscountPercentageLevel3InfoField =
			BuilderHolder._builder.infoFieldType(
				NumberInfoFieldType.INSTANCE
			).name(
				"totalDiscountPercentageLevel3"
			).labelInfoLocalizedValue(
				InfoLocalizedValue.localize(
					CommerceOrderInfoItemFields.class,
					"total-discount-percentage-level-3")
			).build();
	public static final InfoField<NumberInfoFieldType>
		totalDiscountPercentageLevel3WithTaxAmountInfoField =
			BuilderHolder._builder.infoFieldType(
				NumberInfoFieldType.INSTANCE
			).name(
				"totalDiscountPercentageLevel3WithTaxAmount"
			).labelInfoLocalizedValue(
				InfoLocalizedValue.localize(
					CommerceOrderInfoItemFields.class,
					"total-discount-percentage-level-3-with-tax-amount")
			).build();
	public static final InfoField<NumberInfoFieldType>
		totalDiscountPercentageLevel4InfoField =
			BuilderHolder._builder.infoFieldType(
				NumberInfoFieldType.INSTANCE
			).name(
				"totalDiscountPercentageLevel4"
			).labelInfoLocalizedValue(
				InfoLocalizedValue.localize(
					CommerceOrderInfoItemFields.class,
					"total-discount-percentage-level-4")
			).build();
	public static final InfoField<NumberInfoFieldType>
		totalDiscountPercentageLevel4WithTaxAmountInfoField =
			BuilderHolder._builder.infoFieldType(
				NumberInfoFieldType.INSTANCE
			).name(
				"totalDiscountPercentageLevel4WithTaxAmount"
			).labelInfoLocalizedValue(
				InfoLocalizedValue.localize(
					CommerceOrderInfoItemFields.class,
					"total-discount-percentage-level-4-with-tax-amount")
			).build();
	public static final InfoField<NumberInfoFieldType>
		totalDiscountWithTaxAmountInfoField =
			BuilderHolder._builder.infoFieldType(
				NumberInfoFieldType.INSTANCE
			).name(
				"totalDiscountWithTaxAmount"
			).labelInfoLocalizedValue(
				InfoLocalizedValue.localize(
					CommerceOrderInfoItemFields.class,
					"total-discount-with-tax-amount")
			).build();
	public static final InfoField<NumberInfoFieldType> totalInfoField =
		BuilderHolder._builder.infoFieldType(
			NumberInfoFieldType.INSTANCE
		).name(
			"total"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "total")
		).build();
	public static final InfoField<NumberInfoFieldType>
		totalWithTaxAmountInfoField = BuilderHolder._builder.infoFieldType(
			NumberInfoFieldType.INSTANCE
		).name(
			"totalWithTaxAmount"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "total-with-tax-amount")
		).build();
	public static final InfoField<NumberInfoFieldType> transactionIdInfoField =
		BuilderHolder._builder.infoFieldType(
			NumberInfoFieldType.INSTANCE
		).name(
			"transactionId"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "transaction-id")
		).build();
	public static final InfoField<NumberInfoFieldType> userIdInfoField =
		BuilderHolder._builder.infoFieldType(
			NumberInfoFieldType.INSTANCE
		).name(
			"userId"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "userId")
		).build();
	public static final InfoField<TextInfoFieldType> userNameInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"userName"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "userName")
		).build();
	public static final InfoField<TextInfoFieldType> userUuidInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"userUuid"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "userUuid")
		).build();
	public static final InfoField<TextInfoFieldType> uuidInfoField =
		BuilderHolder._builder.infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"uuid"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(
				CommerceOrderInfoItemFields.class, "uuid")
		).build();

	private static class BuilderHolder {

		private static final InfoField.NamespacedBuilder _builder =
			InfoField.builder(CommerceOrder.class.getSimpleName());

	}

}