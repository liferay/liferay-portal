/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.object.contributor;

import com.liferay.commerce.constants.CommerceReturnConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.util.CommerceReturnThreadLocal;
import com.liferay.object.entry.ObjectEntryContext;
import com.liferay.object.entry.contributor.ObjectEntryValuesContributor;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Crescenzo Rega
 */
@Component(service = ObjectEntryValuesContributor.class)
public class CommerceReturnObjectEntryValuesContributor
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

		if (!StringUtil.equals(objectDefinition.getName(), "CommerceReturn")) {
			return;
		}

		Map<String, Serializable> values = objectEntryContext.getValues();

		CommerceOrder commerceOrder =
			_commerceOrderLocalService.getCommerceOrder(
				GetterUtil.getLong(
					values.get(
						"r_commerceOrderToCommerceReturns_commerceOrderId")));

		values.put("channelGroupId", commerceOrder.getGroupId());

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.getCommerceChannelByOrderGroupId(
				commerceOrder.getGroupId());

		values.put("channelId", commerceChannel.getCommerceChannelId());
		values.put("channelName", commerceChannel.getName());

		CommerceCurrency commerceCurrency = commerceOrder.getCommerceCurrency();

		values.put("currencyCode", commerceCurrency.getCode());
		values.put("currencySymbol", commerceCurrency.getSymbol());

		if (!values.containsKey("l_commerceReturnId") &&
			!values.containsKey("externalReferenceCode")) {

			return;
		}

		ObjectEntry originalObjectEntry =
			_objectEntryLocalService.fetchObjectEntry(
				GetterUtil.getLong(values.get("l_commerceReturnId")));

		if (originalObjectEntry == null) {
			originalObjectEntry = _objectEntryLocalService.fetchObjectEntry(
				GetterUtil.getString(values.get("externalReferenceCode")),
				objectDefinition.getObjectDefinitionId());
		}

		Map<String, Serializable> originalValues =
			originalObjectEntry.getValues();

		String currentReturnStatus = GetterUtil.getString(
			originalValues.get("returnStatus"));

		String newReturnStatus = GetterUtil.getString(
			values.get("returnStatus"));

		if (StringUtil.equalsIgnoreCase(
				currentReturnStatus,
				CommerceReturnConstants.RETURN_STATUS_DRAFT) &&
			StringUtil.equalsIgnoreCase(
				newReturnStatus,
				CommerceReturnConstants.RETURN_STATUS_PENDING)) {

			return;
		}

		List<ObjectEntry> objectEntries =
			_objectEntryLocalService.getOneToManyObjectEntries(
				originalObjectEntry.getGroupId(),
				_objectRelationshipLocalService.getObjectRelationship(
					originalObjectEntry.getObjectDefinitionId(),
					"commerceReturnToCommerceReturnItems"
				).getObjectRelationshipId(),
				originalObjectEntry.getObjectEntryId(), true, null,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Map<String, List<ObjectEntry>> returnItemStatusObjectEntriesMap =
			_toReturnItemStatusObjectEntriesMap(objectEntries);

		if (CommerceReturnThreadLocal.isMarkAsCompleted()) {
			CommerceReturnThreadLocal.setMarkAsCompleted(false);

			for (ObjectEntry objectEntry :
					returnItemStatusObjectEntriesMap.getOrDefault(
						"toBeProcessedReturnItems", Collections.emptyList())) {

				Map<String, Serializable> objectEntryValues =
					objectEntry.getValues();

				objectEntryValues.put(
					"returnItemStatus",
					CommerceReturnConstants.RETURN_ITEM_STATUS_COMPLETED);

				CommerceReturnThreadLocal.setSkipCommerceReturnItemContributor(
					true);

				_objectEntryLocalService.updateObjectEntry(
					objectEntry.getUserId(), objectEntry.getObjectEntryId(),
					objectEntryValues, new ServiceContext());
			}

			values.put(
				"returnStatus",
				CommerceReturnConstants.RETURN_STATUS_COMPLETED);

			return;
		}

		if (CommerceReturnThreadLocal.isMarkAsProcessed()) {
			CommerceReturnThreadLocal.setMarkAsProcessed(false);

			for (ObjectEntry objectEntry :
					returnItemStatusObjectEntriesMap.getOrDefault(
						"toBeProcessedReturnItems", Collections.emptyList())) {

				Map<String, Serializable> objectEntryValues =
					objectEntry.getValues();

				objectEntryValues.put(
					"returnItemStatus",
					CommerceReturnConstants.RETURN_ITEM_STATUS_PROCESSED);

				CommerceReturnThreadLocal.setSkipCommerceReturnItemContributor(
					true);

				_objectEntryLocalService.updateObjectEntry(
					objectEntry.getUserId(), objectEntry.getObjectEntryId(),
					objectEntryValues, new ServiceContext());
			}

			return;
		}

		String nextReturnStatus = _getNextReturnStatus(
			objectEntries.size(), currentReturnStatus,
			returnItemStatusObjectEntriesMap);

		if (StringUtil.equals(currentReturnStatus, nextReturnStatus)) {
			return;
		}

		if (StringUtil.equals(
				nextReturnStatus,
				CommerceReturnConstants.RETURN_STATUS_AUTHORIZED)) {

			for (ObjectEntry objectEntry :
					returnItemStatusObjectEntriesMap.getOrDefault(
						"authorizedReturnItems", Collections.emptyList())) {

				Map<String, Serializable> objectEntryValues =
					objectEntry.getValues();

				objectEntryValues.put(
					"returnItemStatus",
					CommerceReturnConstants.
						RETURN_ITEM_STATUS_AWAITING_RECEIPT);

				CommerceReturnThreadLocal.setSkipCommerceReturnItemContributor(
					true);

				_objectEntryLocalService.updateObjectEntry(
					objectEntry.getUserId(), objectEntry.getObjectEntryId(),
					objectEntryValues, new ServiceContext());
			}
		}

		values.put("returnStatus", nextReturnStatus);
	}

	private String _getNextReturnStatus(
		int commerceReturnItemsSize, String currentReturnStatus,
		Map<String, List<ObjectEntry>> returnItemStatusObjectEntriesMap) {

		List<ObjectEntry> notAuthorizedReturnItemObjectEntries =
			returnItemStatusObjectEntriesMap.getOrDefault(
				"notAuthorizedReturnItems", Collections.emptyList());
		List<ObjectEntry> receiptRejectedReturnItemObjectEntries =
			returnItemStatusObjectEntriesMap.getOrDefault(
				"receiptRejectedReturnItems", Collections.emptyList());
		List<ObjectEntry> receivedReturnItemObjectEntries =
			returnItemStatusObjectEntriesMap.getOrDefault(
				"receivedReturnItems", Collections.emptyList());
		List<ObjectEntry> toBeProcessedReturnItemObjectEntries =
			returnItemStatusObjectEntriesMap.getOrDefault(
				"toBeProcessedReturnItems", Collections.emptyList());

		if (StringUtil.equalsIgnoreCase(
				currentReturnStatus,
				CommerceReturnConstants.RETURN_STATUS_AUTHORIZED)) {

			if (commerceReturnItemsSize ==
					(notAuthorizedReturnItemObjectEntries.size() +
						receiptRejectedReturnItemObjectEntries.size())) {

				return CommerceReturnConstants.RETURN_STATUS_REJECTED;
			}

			int count = ListUtil.count(
				receivedReturnItemObjectEntries,
				receivedReturnItemObjectEntry -> {
					Map<String, Serializable> values =
						receivedReturnItemObjectEntry.getValues();

					return Validator.isNotNull(
						values.get("returnResolutionMethod"));
				});

			if ((commerceReturnItemsSize == count) ||
				ListUtil.isNotEmpty(toBeProcessedReturnItemObjectEntries)) {

				return CommerceReturnConstants.RETURN_STATUS_PROCESSING;
			}
		}

		if (StringUtil.equalsIgnoreCase(
				currentReturnStatus,
				CommerceReturnConstants.RETURN_STATUS_PENDING)) {

			if (!toBeProcessedReturnItemObjectEntries.isEmpty() &&
				(commerceReturnItemsSize ==
					(notAuthorizedReturnItemObjectEntries.size() +
						toBeProcessedReturnItemObjectEntries.size()))) {

				return CommerceReturnConstants.RETURN_STATUS_PROCESSING;
			}

			if (commerceReturnItemsSize ==
					notAuthorizedReturnItemObjectEntries.size()) {

				return CommerceReturnConstants.RETURN_STATUS_REJECTED;
			}

			List<ObjectEntry> authorizedReturnItemObjectEntries =
				returnItemStatusObjectEntriesMap.getOrDefault(
					"authorizedReturnItems", Collections.emptyList());

			if (commerceReturnItemsSize ==
					(authorizedReturnItemObjectEntries.size() +
						notAuthorizedReturnItemObjectEntries.size() +
							receivedReturnItemObjectEntries.size() +
								toBeProcessedReturnItemObjectEntries.size())) {

				return CommerceReturnConstants.RETURN_STATUS_AUTHORIZED;
			}
		}

		if (StringUtil.equalsIgnoreCase(
				currentReturnStatus,
				CommerceReturnConstants.RETURN_STATUS_PROCESSING) &&
			(commerceReturnItemsSize ==
				(notAuthorizedReturnItemObjectEntries.size() +
					receiptRejectedReturnItemObjectEntries.size()))) {

			return CommerceReturnConstants.RETURN_STATUS_REJECTED;
		}

		return currentReturnStatus;
	}

	private Map<String, List<ObjectEntry>> _toReturnItemStatusObjectEntriesMap(
		List<ObjectEntry> objectEntries) {

		Map<String, List<ObjectEntry>> returnItemStatusObjectEntriesMap =
			new HashMap<>();

		for (ObjectEntry objectEntry : objectEntries) {
			String key = null;

			Map<String, Serializable> values = objectEntry.getValues();

			String returnItemStatus = GetterUtil.getString(
				values.get("returnItemStatus"));

			if (ArrayUtil.contains(
					CommerceReturnConstants.RETURN_ITEM_STATUSES_AUTHORIZED,
					returnItemStatus)) {

				key = "authorizedReturnItems";
			}
			else if (StringUtil.equals(
						returnItemStatus,
						CommerceReturnConstants.
							RETURN_ITEM_STATUS_NOT_AUTHORIZED)) {

				key = "notAuthorizedReturnItems";
			}
			else if (StringUtil.equals(
						returnItemStatus,
						CommerceReturnConstants.RETURN_ITEM_STATUS_PROCESSED)) {

				key = "processedReturnItems";
			}
			else if (StringUtil.equals(
						returnItemStatus,
						CommerceReturnConstants.
							RETURN_ITEM_STATUS_RECEIPT_REJECTED)) {

				key = "receiptRejectedReturnItems";
			}
			else if (ArrayUtil.contains(
						CommerceReturnConstants.RETURN_ITEM_STATUSES_RECEIVED,
						returnItemStatus)) {

				key = "receivedReturnItems";
			}
			else if (StringUtil.equals(
						returnItemStatus,
						CommerceReturnConstants.
							RETURN_ITEM_STATUS_TO_BE_PROCESSED)) {

				key = "toBeProcessedReturnItems";
			}

			if (Validator.isNull(key)) {
				continue;
			}

			List<ObjectEntry> returnItemStatusObjectEntries =
				returnItemStatusObjectEntriesMap.get(key);

			if (returnItemStatusObjectEntries == null) {
				returnItemStatusObjectEntries = new ArrayList<>();
			}

			returnItemStatusObjectEntries.add(objectEntry);

			returnItemStatusObjectEntriesMap.put(
				key, returnItemStatusObjectEntries);
		}

		return returnItemStatusObjectEntriesMap;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceReturnObjectEntryValuesContributor.class);

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceOrderLocalService _commerceOrderLocalService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

}