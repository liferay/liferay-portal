/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.object.validation.rule;

import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.service.CommerceOrderItemLocalService;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.validation.rule.ObjectValidationRuleEngine;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.GetterUtil;

import java.io.Serializable;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Crescenzo Rega
 */
@Component(service = ObjectValidationRuleEngine.class)
public class CommerceReturnItemCommerceOrderItemIdObjectValidationRuleEngineImpl
	extends BaseObjectValidationRuleEngineImpl {

	@Override
	protected String getObjectDefinitionName() {
		return "CommerceReturnItem";
	}

	@Override
	protected String getObjectFieldName() {
		return "commerceOrderItemId";
	}

	@Override
	protected boolean hasValidationCriteriaMet(
		Map<String, Object> inputObjects) {

		Map<String, Object> entryDTO = (Map<String, Object>)inputObjects.get(
			"entryDTO");

		Map<String, Object> properties = (Map<String, Object>)entryDTO.get(
			"properties");

		CommerceOrderItem commerceOrderItem =
			_commerceOrderItemLocalService.fetchCommerceOrderItem(
				GetterUtil.getLong(
					properties.get(
						"r_commerceOrderItemToCommerceReturnItems_" +
							"commerceOrderItemId")));

		if (commerceOrderItem == null) {
			return false;
		}

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_COMMERCE_RETURN", CompanyThreadLocal.getCompanyId());

		if (objectDefinition == null) {
			return false;
		}

		ObjectEntry objectEntry = _objectEntryLocalService.fetchObjectEntry(
			GetterUtil.getString(
				properties.get(
					"r_commerceReturnToCommerceReturnItems_l_" +
						"commerceReturnERC")),
			ObjectDefinitionConstants.GROUP_ID_DEFAULT,
			objectDefinition.getObjectDefinitionId());

		if (objectEntry == null) {
			Map<String, Object> originalEntryDTO =
				(Map<String, Object>)inputObjects.get("originalEntryDTO");

			Map<String, Object> originalProperties =
				(Map<String, Object>)originalEntryDTO.get("properties");

			objectEntry = _objectEntryLocalService.fetchObjectEntry(
				GetterUtil.getString(
					originalProperties.get(
						"r_commerceReturnToCommerceReturnItems_l_" +
							"commerceReturnERC")),
				ObjectDefinitionConstants.GROUP_ID_DEFAULT,
				objectDefinition.getObjectDefinitionId());

			if (objectEntry == null) {
				return false;
			}
		}

		Map<String, Serializable> values = objectEntry.getValues();

		if (commerceOrderItem.getCommerceOrderId() == GetterUtil.getLong(
				values.get(
					"r_commerceOrderToCommerceReturns_commerceOrderId"))) {

			return true;
		}

		return false;
	}

	@Reference
	private CommerceOrderItemLocalService _commerceOrderItemLocalService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

}