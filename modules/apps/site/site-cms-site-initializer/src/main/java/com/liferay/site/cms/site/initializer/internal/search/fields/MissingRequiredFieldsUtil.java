/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.search.fields;

import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.model.ObjectField;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.List;
import java.util.Objects;

/**
 * @author Mikel Lorza
 */
public class MissingRequiredFieldsUtil {

	public static int getMissingRequiredFieldsCount(
		String defaultLanguageId, List<ObjectField> objectFields,
		JSONObject propertiesJSONObject) {

		int missingRequiredFieldsCount = 0;

		for (ObjectField objectField : objectFields) {
			if (!objectField.isRequired() ||
				!_isMissing(
					defaultLanguageId, objectField, propertiesJSONObject)) {

				continue;
			}

			missingRequiredFieldsCount++;
		}

		return missingRequiredFieldsCount;
	}

	private static boolean _isEmpty(
		JSONObject jsonObject, String key, ObjectField objectField) {

		if (!jsonObject.has(key) || jsonObject.isNull(key)) {
			return true;
		}

		if (objectField.compareBusinessType(
				ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT)) {

			JSONObject attachmentJSONObject = jsonObject.getJSONObject(key);

			if ((attachmentJSONObject == null) ||
				(attachmentJSONObject.getLong("id") <= 0)) {

				return true;
			}

			return false;
		}

		if (objectField.compareBusinessType(
				ObjectFieldConstants.BUSINESS_TYPE_BOOLEAN)) {

			return !jsonObject.getBoolean(key);
		}

		if (objectField.compareBusinessType(
				ObjectFieldConstants.BUSINESS_TYPE_MULTISELECT_PICKLIST)) {

			return JSONUtil.isEmpty(jsonObject.getJSONArray(key));
		}

		if (objectField.compareBusinessType(
				ObjectFieldConstants.BUSINESS_TYPE_PICKLIST)) {

			JSONObject listEntryJSONObject = jsonObject.getJSONObject(key);

			if ((listEntryJSONObject == null) ||
				Validator.isNull(listEntryJSONObject.getString("key"))) {

				return true;
			}

			return false;
		}

		if (objectField.hasMultipleDBColumns()) {
			return JSONUtil.isEmpty(jsonObject.getJSONObject(key));
		}

		if (Objects.equals(
				objectField.getDBType(), ObjectFieldConstants.DB_TYPE_LONG)) {

			if (jsonObject.getLong(key) == 0) {
				return true;
			}

			return false;
		}

		return Validator.isNull(jsonObject.getString(key));
	}

	private static boolean _isMissing(
		String defaultLanguageId, ObjectField objectField,
		JSONObject propertiesJSONObject) {

		if (propertiesJSONObject == null) {
			return true;
		}

		if (!objectField.isLocalized()) {
			return _isEmpty(
				propertiesJSONObject, objectField.getName(), objectField);
		}

		JSONObject localizedJSONObject = propertiesJSONObject.getJSONObject(
			objectField.getI18nObjectFieldName());

		if (localizedJSONObject == null) {
			return true;
		}

		return _isEmpty(localizedJSONObject, defaultLanguageId, objectField);
	}

}