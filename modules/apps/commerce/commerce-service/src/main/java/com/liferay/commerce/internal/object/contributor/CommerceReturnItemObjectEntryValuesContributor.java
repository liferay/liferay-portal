/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.object.contributor;

import com.liferay.commerce.constants.CommerceReturnConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.model.CommerceMoney;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.price.CommerceOrderItemPrice;
import com.liferay.commerce.price.CommerceOrderPriceCalculation;
import com.liferay.commerce.service.CommerceOrderItemLocalService;
import com.liferay.commerce.util.CommerceReturnThreadLocal;
import com.liferay.object.entry.ObjectEntryContext;
import com.liferay.object.entry.contributor.ObjectEntryValuesContributor;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.Serializable;

import java.math.BigDecimal;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Crescenzo Rega
 */
@Component(service = ObjectEntryValuesContributor.class)
public class CommerceReturnItemObjectEntryValuesContributor
	implements ObjectEntryValuesContributor {

	@Override
	public void contribute(ObjectEntryContext objectEntryContext) {
		try {
			_contribute(objectEntryContext);
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	private void _contribute(ObjectEntryContext objectEntryContext)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				objectEntryContext.getObjectDefinitionId());

		if (!StringUtil.equals(
				objectDefinition.getName(), "CommerceReturnItem")) {

			return;
		}

		Map<String, Serializable> values = objectEntryContext.getValues();

		CommerceOrderItem commerceOrderItem =
			_commerceOrderItemService.getCommerceOrderItem(
				GetterUtil.getLong(
					values.get(
						"r_commerceOrderItemToCommerceReturnItems_" +
							"commerceOrderItemId")));

		CommerceOrder commerceOrder = commerceOrderItem.getCommerceOrder();

		CommerceCurrency commerceCurrency = commerceOrder.getCommerceCurrency();

		values.put("currencyCode", commerceCurrency.getCode());
		values.put("currencySymbol", commerceCurrency.getSymbol());

		CommerceOrderItemPrice commerceOrderItemPrice =
			_commerceOrderPriceCalculation.getCommerceOrderItemPricePerUnit(
				commerceOrder.getCommerceCurrency(), commerceOrderItem);

		CommerceMoney commerceMoney = commerceOrderItemPrice.getFinalPrice();

		values.put(
			"amount",
			BigDecimalUtil.multiply(
				new BigDecimal(String.valueOf(values.get("quantity"))),
				commerceMoney.getPrice()));

		if (CommerceReturnThreadLocal.isSkipCommerceReturnItemContributor()) {
			CommerceReturnThreadLocal.setSkipCommerceReturnItemContributor(
				false);

			return;
		}

		ObjectEntry originalObjectEntry =
			_objectEntryLocalService.fetchObjectEntry(
				GetterUtil.getLong(
					values.get(
						"r_commerceReturnToCommerceReturnItems_l_" +
							"commerceReturnId")));

		if (originalObjectEntry == null) {
			originalObjectEntry = _objectEntryLocalService.fetchObjectEntry(
				GetterUtil.getString(
					values.get(
						"r_commerceReturnToCommerceReturnItems_l_" +
							"commerceReturnERC")),
				objectDefinition.getObjectDefinitionId());

			if (originalObjectEntry == null) {
				return;
			}
		}

		Map<String, Serializable> originalValues =
			originalObjectEntry.getValues();

		String returnStatus = GetterUtil.getString(
			originalValues.get("returnStatus"));

		if (StringUtil.equals(
				returnStatus, CommerceReturnConstants.RETURN_STATUS_DRAFT)) {

			return;
		}

		values.put(
			"returnItemStatus",
			_getNextReturnItemStatus(
				objectEntryContext.getObjectDefinitionId(), returnStatus,
				values));
	}

	private String _getNextReturnItemStatus(
			long objectDefinitionId, String returnStatus,
			Map<String, Serializable> values)
		throws Exception {

		BigDecimal authorized = BigDecimal.valueOf(
			GetterUtil.getDouble(values.get("authorized")));

		if (BigDecimalUtil.isZero(authorized)) {
			return CommerceReturnConstants.RETURN_ITEM_STATUS_NOT_AUTHORIZED;
		}

		if (GetterUtil.getBoolean(
				values.get("authorizeReturnWithoutReturningProducts"))) {

			if (Validator.isNotNull(
					String.valueOf(values.get("returnResolutionMethod")))) {

				return CommerceReturnConstants.
					RETURN_ITEM_STATUS_TO_BE_PROCESSED;
			}

			return CommerceReturnConstants.RETURN_ITEM_STATUS_RECEIVED;
		}

		BigDecimal received = BigDecimal.valueOf(
			GetterUtil.getDouble(values.get("received")));

		if (BigDecimalUtil.isZero(received)) {
			if (StringUtil.equals(
					returnStatus,
					CommerceReturnConstants.RETURN_STATUS_PENDING)) {

				BigDecimal quantity = BigDecimal.valueOf(
					GetterUtil.getLong(values.get("quantity")));

				if (BigDecimalUtil.lt(authorized, quantity)) {
					return CommerceReturnConstants.
						RETURN_ITEM_STATUS_PARTIALLY_AUTHORIZED;
				}

				if (BigDecimalUtil.eq(authorized, quantity)) {
					return CommerceReturnConstants.
						RETURN_ITEM_STATUS_AUTHORIZED;
				}
			}

			if (StringUtil.equals(
					returnStatus,
					CommerceReturnConstants.RETURN_STATUS_AUTHORIZED) &&
				BigDecimalUtil.gt(authorized, BigDecimal.ZERO)) {

				ObjectEntry originalObjectEntry =
					_objectEntryLocalService.getObjectEntry(
						GetterUtil.getString(
							values.get("externalReferenceCode")),
						objectDefinitionId);

				Map<String, Serializable> originalValues =
					originalObjectEntry.getValues();

				if (!BigDecimalUtil.eq(
						authorized,
						BigDecimal.valueOf(
							GetterUtil.getDouble(
								originalValues.get("authorized"))))) {

					return CommerceReturnConstants.
						RETURN_ITEM_STATUS_AWAITING_RECEIPT;
				}

				return CommerceReturnConstants.
					RETURN_ITEM_STATUS_RECEIPT_REJECTED;
			}
		}

		if (Validator.isNotNull(
				String.valueOf(values.get("returnResolutionMethod")))) {

			return CommerceReturnConstants.RETURN_ITEM_STATUS_TO_BE_PROCESSED;
		}

		if (BigDecimalUtil.lt(received, authorized)) {
			return CommerceReturnConstants.
				RETURN_ITEM_STATUS_PARTIALLY_RECEIVED;
		}

		return CommerceReturnConstants.RETURN_ITEM_STATUS_RECEIVED;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceReturnItemObjectEntryValuesContributor.class);

	@Reference
	private CommerceOrderItemLocalService _commerceOrderItemService;

	@Reference
	private CommerceOrderPriceCalculation _commerceOrderPriceCalculation;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

}