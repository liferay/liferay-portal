/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.order.internal.odata.entity.v1_0;

import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.odata.entity.CollectionEntityField;
import com.liferay.portal.odata.entity.ComplexEntityField;
import com.liferay.portal.odata.entity.DateTimeEntityField;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.odata.entity.IntegerEntityField;
import com.liferay.portal.odata.entity.StringEntityField;

import java.util.Collections;
import java.util.Map;

/**
 * @author Alessio Antonio Rendina
 */
public class PlacedOrderEntityModel implements EntityModel {

	public PlacedOrderEntityModel() {
		_entityFieldsMap = EntityModel.toEntityFieldsMap(
			new CollectionEntityField(
				new IntegerEntityField(
					"accountId", locale -> "commerceAccountId")),
			new CollectionEntityField(
				new IntegerEntityField("orderStatus", locale -> "orderStatus")),
			new ComplexEntityField(
				"orderStatusInfo",
				Collections.singletonList(
					new IntegerEntityField("code", locale -> "orderStatus"))),
			new DateTimeEntityField(
				"createDate",
				locale -> Field.getSortableFieldName(Field.CREATE_DATE),
				locale -> Field.CREATE_DATE),
			new DateTimeEntityField(
				"modifiedDate",
				locale -> Field.getSortableFieldName(Field.MODIFIED_DATE),
				locale -> Field.MODIFIED_DATE),
			new DateTimeEntityField(
				"orderDate", locale -> Field.getSortableFieldName("orderDate"),
				locale -> "orderDate"),
			new DateTimeEntityField(
				"requestedDeliveryDate",
				locale -> Field.getSortableFieldName("requestedDeliveryDate"),
				locale -> "requestedDeliveryDate"),
			new IntegerEntityField("authorId", locale -> "orderCreatorUserId"),
			new IntegerEntityField("id", locale -> Field.ENTRY_CLASS_PK),
			new StringEntityField(
				"account", locale -> Field.getSortableFieldName("accountName")),
			new StringEntityField(
				"author",
				locale -> Field.getSortableFieldName(Field.USER_NAME)),
			new StringEntityField(
				"externalReferenceCode",
				locale -> Field.getSortableFieldName("externalReferenceCode")),
			new StringEntityField(
				"name", locale -> Field.getSortableFieldName(Field.NAME)),
			new StringEntityField(
				"orderType",
				locale -> Field.getSortableFieldName("commerceOrderTypeName"),
				locale -> "commerceOrderTypeName"),
			new StringEntityField(
				"orderTypeExternalReferenceCode",
				locale -> Field.getSortableFieldName(
					"commerceOrderTypeExternalReferenceCode")),
			new StringEntityField(
				"purchaseOrderNumber",
				locale -> Field.getSortableFieldName("purchaseOrderNumber")));
	}

	@Override
	public Map<String, EntityField> getEntityFieldsMap() {
		return _entityFieldsMap;
	}

	private final Map<String, EntityField> _entityFieldsMap;

}