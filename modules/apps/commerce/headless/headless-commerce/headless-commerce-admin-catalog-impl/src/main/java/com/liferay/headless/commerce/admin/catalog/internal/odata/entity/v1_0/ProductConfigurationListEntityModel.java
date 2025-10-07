/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.odata.entity.v1_0;

import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.odata.entity.CollectionEntityField;
import com.liferay.portal.odata.entity.DateTimeEntityField;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.odata.entity.IntegerEntityField;
import com.liferay.portal.odata.entity.StringEntityField;

import java.util.Map;

/**
 * @author Andrea Sbarra
 */
public class ProductConfigurationListEntityModel implements EntityModel {

	public ProductConfigurationListEntityModel() {
		_entityFieldsMap = EntityModel.toEntityFieldsMap(
			new CollectionEntityField(
				new IntegerEntityField(
					"accountGroupId", locale -> "commerceAccountGroupIds")),
			new CollectionEntityField(
				new IntegerEntityField(
					"accountId", locale -> "commerceAccountId")),
			new CollectionEntityField(
				new IntegerEntityField("catalogId", locale -> "catalogId")),
			new CollectionEntityField(
				new IntegerEntityField(
					"channelId", locale -> "commerceChannelId")),
			new CollectionEntityField(
				new IntegerEntityField(
					"orderTypeId", locale -> "commerceOrderTypeId")),
			new DateTimeEntityField(
				"createDate",
				locale -> Field.getSortableFieldName(Field.CREATE_DATE),
				locale -> Field.CREATE_DATE),
			new StringEntityField(
				"name", locale -> Field.getSortableFieldName("name")));
	}

	@Override
	public Map<String, EntityField> getEntityFieldsMap() {
		return _entityFieldsMap;
	}

	private final Map<String, EntityField> _entityFieldsMap;

}