/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.odata.entity.v1_0;

import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.odata.entity.BooleanEntityField;
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
public class ProductConfigurationEntityModel implements EntityModel {

	public ProductConfigurationEntityModel() {
		_entityFieldsMap = EntityModel.toEntityFieldsMap(
			new BooleanEntityField("purchasable", locale -> "purchasable"),
			new BooleanEntityField("shippable", locale -> "shippable"),
			new CollectionEntityField(
				new StringEntityField(
					"categoryIds", locale -> "assetCategoryIds")),
			new CollectionEntityField(
				new StringEntityField(
					"categoryNames", locale -> "assetCategoryNames")),
			new DateTimeEntityField(
				"createDate",
				locale -> Field.getSortableFieldName(Field.CREATE_DATE),
				locale -> Field.CREATE_DATE),
			new DateTimeEntityField(
				"modifiedDate",
				locale -> Field.getSortableFieldName(Field.MODIFIED_DATE),
				locale -> Field.MODIFIED_DATE),
			new IntegerEntityField(
				"maxOrderQuantity",
				locale -> Field.getSortableFieldName("maximumOrderQuantity")),
			new IntegerEntityField(
				"minOrderQuantity",
				locale -> Field.getSortableFieldName("minimumOrderQuantity")),
			new IntegerEntityField(
				"multipleOrderQuantity",
				locale -> Field.getSortableFieldName("multipleOrderQuantity")),
			new StringEntityField(
				"entityName", locale -> Field.getSortableFieldName("name")),
			new StringEntityField("productType", locale -> "productTypeName"));
	}

	@Override
	public Map<String, EntityField> getEntityFieldsMap() {
		return _entityFieldsMap;
	}

	private final Map<String, EntityField> _entityFieldsMap;

}