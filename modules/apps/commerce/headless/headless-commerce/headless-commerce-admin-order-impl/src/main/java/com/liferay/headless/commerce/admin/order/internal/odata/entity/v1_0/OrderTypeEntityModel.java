/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.order.internal.odata.entity.v1_0;

import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.odata.entity.CollectionEntityField;
import com.liferay.portal.odata.entity.DateTimeEntityField;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.odata.entity.IntegerEntityField;

import java.util.Map;

/**
 * @author Alessio Antonio Rendina
 */
public class OrderTypeEntityModel implements EntityModel {

	public OrderTypeEntityModel() {
		_entityFieldsMap = EntityModel.toEntityFieldsMap(
			new CollectionEntityField(
				new IntegerEntityField("name", locale -> "name")),
			new DateTimeEntityField(
				"displayDate",
				locale -> Field.getSortableFieldName("displayDate"),
				locale -> "displayDate"),
			new DateTimeEntityField(
				"expirationDate",
				locale -> Field.getSortableFieldName("expirationDate"),
				locale -> "expirationDate"));
	}

	@Override
	public Map<String, EntityField> getEntityFieldsMap() {
		return _entityFieldsMap;
	}

	private final Map<String, EntityField> _entityFieldsMap;

}