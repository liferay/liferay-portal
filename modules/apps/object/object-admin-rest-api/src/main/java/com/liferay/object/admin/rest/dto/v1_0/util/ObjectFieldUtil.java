/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.dto.v1_0.util;

import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.service.ListTypeDefinitionService;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.model.ObjectField;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.StringUtil;

/**
 * @author Feliphe Marinho
 */
public class ObjectFieldUtil {

	public static JSONObject toJSONObject(
		ListTypeDefinitionService listTypeDefinitionService,
		ObjectField objectField) {

		return JSONUtil.put(
			"businessType", objectField.getBusinessType()
		).put(
			"DBType", objectField.getDBType()
		).put(
			"defaultValue",
			String.valueOf(
				com.liferay.object.field.setting.util.ObjectFieldSettingUtil.
					getDefaultValue(null, objectField, null))
		).put(
			"description", objectField.getDescriptionMap()
		).put(
			"externalReferenceCode", objectField.getExternalReferenceCode()
		).put(
			"id", Long.valueOf(objectField.getObjectFieldId())
		).put(
			"indexed", objectField.isIndexed()
		).put(
			"indexedAsKeyword", objectField.isIndexedAsKeyword()
		).put(
			"indexedLanguageId", objectField.getIndexedLanguageId()
		).put(
			"label", objectField.getLabelMap()
		).put(
			"listTypeDefinitionExternalReferenceCode",
			() -> {
				if (!StringUtil.equals(
						objectField.getBusinessType(),
						ObjectFieldConstants.BUSINESS_TYPE_PICKLIST)) {

					return null;
				}

				ListTypeDefinition listTypeDefinition =
					listTypeDefinitionService.getListTypeDefinition(
						objectField.getListTypeDefinitionId());

				return listTypeDefinition.getExternalReferenceCode();
			}
		).put(
			"listTypeDefinitionId",
			Long.valueOf(objectField.getListTypeDefinitionId())
		).put(
			"name", objectField.getName()
		).put(
			"objectFieldSettings",
			ObjectFieldSettingUtil.toJSONObject(objectField)
		).put(
			"relationshipType", objectField.getRelationshipType()
		).put(
			"required", objectField.isRequired()
		).put(
			"state", objectField.isState()
		).put(
			"system", objectField.isSystem()
		);
	}

}