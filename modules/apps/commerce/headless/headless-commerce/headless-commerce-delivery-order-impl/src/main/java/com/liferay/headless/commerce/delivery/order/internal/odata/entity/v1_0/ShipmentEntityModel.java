/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.order.internal.odata.entity.v1_0;

import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.odata.entity.CollectionEntityField;
import com.liferay.portal.odata.entity.DateTimeEntityField;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.odata.entity.IntegerEntityField;
import com.liferay.portal.odata.entity.StringEntityField;

import java.util.Map;

/**
 * @author Alessio Antonio Rendina
 */
public class ShipmentEntityModel implements EntityModel {

	public ShipmentEntityModel() {
		_entityFieldsMap = EntityModel.toEntityFieldsMap(
			new CollectionEntityField(
				new IntegerEntityField("status", locale -> Field.STATUS)),
			new DateTimeEntityField(
				"createDate",
				locale -> Field.getSortableFieldName(Field.CREATE_DATE),
				locale -> Field.CREATE_DATE),
			new DateTimeEntityField(
				"expectedDate",
				locale -> Field.getSortableFieldName("expectedDate"),
				locale -> "expectedDate"),
			new IntegerEntityField(
				"id",
				locale -> Field.getSortableFieldName(Field.ENTRY_CLASS_PK)),
			new StringEntityField(
				"carrier", locale -> Field.getSortableFieldName("carrier"),
				locale -> "carrier"),
			new StringEntityField("tracking", locale -> "trackingNumber"));
	}

	@Override
	public Map<String, EntityField> getEntityFieldsMap() {
		return _entityFieldsMap;
	}

	private final Map<String, EntityField> _entityFieldsMap;

}