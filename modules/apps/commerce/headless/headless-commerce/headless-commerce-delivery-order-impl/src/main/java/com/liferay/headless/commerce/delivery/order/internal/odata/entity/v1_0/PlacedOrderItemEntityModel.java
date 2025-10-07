/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.order.internal.odata.entity.v1_0;

import com.liferay.commerce.product.constants.CPField;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.odata.entity.IntegerEntityField;
import com.liferay.portal.odata.entity.StringEntityField;

import java.util.Map;

/**
 * @author Alessio Antonio Rendina
 */
public class PlacedOrderItemEntityModel implements EntityModel {

	public PlacedOrderItemEntityModel() {
		_entityFieldsMap = EntityModel.toEntityFieldsMap(
			new IntegerEntityField(
				"quantity", locale -> Field.getSortableFieldName("quantity")),
			new StringEntityField(
				"name", locale -> Field.getSortableFieldName(Field.NAME),
				locale -> Field.NAME),
			new StringEntityField(
				"sku", locale -> Field.getSortableFieldName(CPField.SKU),
				locale -> CPField.SKU),
			new StringEntityField(
				"unitOfMeasure",
				locale -> Field.getSortableFieldName("cpInstanceUnitOfMeasure"),
				locale -> "cpInstanceUnitOfMeasure"));
	}

	@Override
	public Map<String, EntityField> getEntityFieldsMap() {
		return _entityFieldsMap;
	}

	private final Map<String, EntityField> _entityFieldsMap;

}