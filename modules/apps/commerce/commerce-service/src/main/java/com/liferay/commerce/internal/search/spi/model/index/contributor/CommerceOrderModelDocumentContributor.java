/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.search.spi.model.index.contributor;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.commerce.currency.model.CommerceMoney;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceOrderTypeLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.service.AddressLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Danny Situ
 */
@Component(
	property = "indexer.class.name=com.liferay.commerce.model.CommerceOrder",
	service = ModelDocumentContributor.class
)
public class CommerceOrderModelDocumentContributor
	implements ModelDocumentContributor<CommerceOrder> {

	@Override
	public void contribute(Document document, CommerceOrder commerceOrder) {
		try {
			document.setSortableTextFields(
				ArrayUtil.append(
					PropsUtil.getArray(PropsKeys.INDEX_SORTABLE_TEXT_FIELDS),
					new String[] {
						Field.NAME, Field.USER_NAME, "accountName",
						"commerceOrderTypeExternalReferenceCode",
						"externalReferenceCode", "purchaseOrderNumber"
					}));

			document.addNumberSortable(
				Field.ENTRY_CLASS_PK, commerceOrder.getCommerceOrderId());
			document.addKeyword(Field.NAME, commerceOrder.getName(), true);
			document.addKeyword(Field.STATUS, commerceOrder.getStatus());

			User user = _userLocalService.fetchUser(commerceOrder.getUserId());

			if (user != null) {
				document.addKeyword(Field.USER_NAME, user.getFullName(), true);
			}

			AccountEntry accountEntry =
				_accountEntryLocalService.fetchAccountEntry(
					commerceOrder.getCommerceAccountId());

			if (accountEntry != null) {
				document.addKeyword(
					"accountName", accountEntry.getName(), true);
			}

			Address address = null;

			if (!commerceOrder.isOpen()) {
				address = _addressLocalService.fetchAddress(
					commerceOrder.getShippingAddressId());
			}

			if (address != null) {
				document.addKeyword("addressName", address.getName());
			}

			document.addKeyword(
				"advanceStatus", commerceOrder.getAdvanceStatus());

			if (address != null) {
				document.addKeyword("city", address.getCity());
			}

			document.addKeyword(
				"commerceAccountId", commerceOrder.getCommerceAccountId());
			document.addNumberSortable(
				"commerceAccountId", commerceOrder.getCommerceAccountId());

			CommerceChannel commerceChannel =
				_commerceChannelLocalService.getCommerceChannelByOrderGroupId(
					commerceOrder.getGroupId());

			document.addKeyword(
				"commerceChannelId", commerceChannel.getCommerceChannelId());

			CommerceOrderType commerceOrderType =
				_commerceOrderTypeLocalService.fetchCommerceOrderType(
					commerceOrder.getCommerceOrderTypeId());

			if (commerceOrderType != null) {
				document.addKeyword(
					"commerceOrderTypeExternalReferenceCode",
					commerceOrderType.getExternalReferenceCode(), true);
			}

			document.addKeyword(
				"commerceOrderTypeId", commerceOrder.getCommerceOrderTypeId());

			if (commerceOrderType != null) {
				document.addLocalizedKeyword(
					"commerceOrderTypeName", commerceOrderType.getNameMap(),
					false, true);
			}

			if (address != null) {
				Country country = address.getCountry();

				document.addKeyword("countryIsoCode", country.getA2());
				document.addKeyword("countryName", country.getName());
			}

			document.addKeyword(
				"externalReferenceCode",
				commerceOrder.getExternalReferenceCode(), true);
			document.addNumber(
				"itemsQuantity", _getItemsQuantity(commerceOrder));

			if (user != null) {
				document.addKeyword(
					"orderCreatorEmailAddress", user.getEmailAddress());
				document.addKeyword("orderCreatorUserId", user.getUserId());
			}

			document.addDate("orderDate", commerceOrder.getOrderDate());
			document.addDateSortable("orderDate", commerceOrder.getOrderDate());
			document.addKeyword(
				"orderItemNames", _getCommerceOrderItemNames(commerceOrder));
			document.addKeyword("orderStatus", commerceOrder.getOrderStatus());
			document.addNumber(
				"paymentStatus", commerceOrder.getPaymentStatus());
			document.addKeyword(
				"purchaseOrderNumber", commerceOrder.getPurchaseOrderNumber());

			if (address != null) {
				Region region = address.getRegion();

				document.addKeyword("regionName", region.getName());
			}

			Date requestedDeliveryDate =
				commerceOrder.getRequestedDeliveryDate();

			if (requestedDeliveryDate != null) {
				document.addDate(
					"requestedDeliveryDate", requestedDeliveryDate);
				document.addDateSortable(
					"requestedDeliveryDate", requestedDeliveryDate);
			}

			if (address != null) {
				document.addKeyword(
					"shippingAddressExternalReference",
					address.getExternalReferenceCode());
			}

			document.addKeyword(
				"sku", _getCommerceOrderItemSKUs(commerceOrder));

			if (address != null) {
				document.addKeyword("street1", address.getStreet1());
				document.addKeyword("street2", address.getStreet2());
				document.addKeyword("street3", address.getStreet3());
			}

			document.addNumber("total", commerceOrder.getTotal());
			document.addNumber("totalAmount", _getTotalAmount(commerceOrder));

			if (address != null) {
				document.addKeyword("zip", address.getZip());
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to index commerce order " +
						commerceOrder.getCommerceOrderId(),
					exception);
			}
		}
	}

	private String[] _getCommerceOrderItemNames(CommerceOrder commerceOrder) {
		List<String> commerceOrderItemNamesList = new ArrayList<>();

		for (Locale locale :
				_language.getAvailableLocales(commerceOrder.getGroupId())) {

			for (CommerceOrderItem commerceOrderItem :
					commerceOrder.getCommerceOrderItems()) {

				String commerceOrderItemName = commerceOrderItem.getName(
					locale);

				if (Validator.isNull(commerceOrderItemName)) {
					commerceOrderItemName = _localization.getDefaultLanguageId(
						commerceOrderItem.getName());
				}

				commerceOrderItemNamesList.add(commerceOrderItemName);
			}
		}

		return commerceOrderItemNamesList.toArray(new String[0]);
	}

	private String[] _getCommerceOrderItemSKUs(CommerceOrder commerceOrder) {
		List<String> commerceOrderItemSKUsList = new ArrayList<>();

		List<CommerceOrderItem> commerceOrderItems =
			commerceOrder.getCommerceOrderItems();

		for (CommerceOrderItem commerceOrderItem : commerceOrderItems) {
			commerceOrderItemSKUsList.add(commerceOrderItem.getSku());
		}

		return commerceOrderItemSKUsList.toArray(new String[0]);
	}

	private BigDecimal _getItemsQuantity(CommerceOrder commerceOrder) {
		BigDecimal count = BigDecimal.ZERO;

		for (CommerceOrderItem commerceOrderItem :
				commerceOrder.getCommerceOrderItems()) {

			count = count.add(commerceOrderItem.getQuantity());
		}

		return count;
	}

	private double _getTotalAmount(CommerceOrder commerceOrder)
		throws PortalException {

		CommerceMoney commerceOrderTotalCommerceMoney =
			commerceOrder.getTotalMoney();

		BigDecimal commerceOrderTotalValue =
			commerceOrderTotalCommerceMoney.getPrice();

		return commerceOrderTotalValue.doubleValue();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceOrderModelDocumentContributor.class);

	@Reference
	private AccountEntryLocalService _accountEntryLocalService;

	@Reference
	private AddressLocalService _addressLocalService;

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceOrderTypeLocalService _commerceOrderTypeLocalService;

	@Reference
	private Language _language;

	@Reference
	private Localization _localization;

	@Reference
	private UserLocalService _userLocalService;

}