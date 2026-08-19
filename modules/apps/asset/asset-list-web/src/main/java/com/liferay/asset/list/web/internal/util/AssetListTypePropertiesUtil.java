/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.web.internal.util;

import com.liferay.list.type.service.ListTypeEntryLocalServiceUtil;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectFieldLocalServiceUtil;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.PortalUtil;

import java.util.List;
import java.util.Locale;

/**
 * @author Joshua Cords
 */
public class AssetListTypePropertiesUtil {

	public static JSONArray getTypePropertiesJSONArray(
		long[] classNameIds, long[] classTypeIds, long companyId,
		Locale locale) {

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		if (!FeatureFlagManagerUtil.isEnabled(companyId, "LPD-74731")) {
			return jsonArray;
		}

		jsonArray.put(
			JSONUtil.put(
				"items", _getCommonFieldsItemsJSONArray(locale)
			).put(
				"label", LanguageUtil.get(locale, "common-fields")
			));

		for (int i = 0; i < classNameIds.length; i++) {
			long classTypeId = 0;

			if (i < classTypeIds.length) {
				classTypeId = classTypeIds[i];
			}

			ObjectDefinition objectDefinition = _fetchObjectDefinition(
				classNameIds[i], companyId);

			if (objectDefinition == null) {
				continue;
			}

			jsonArray.put(
				JSONUtil.put(
					"items",
					_getItemsJSONArray(
						classNameIds[i], classTypeId, locale,
						ObjectFieldLocalServiceUtil.getObjectFields(
							objectDefinition.getObjectDefinitionId()))
				).put(
					"label", objectDefinition.getLabel(locale, true)
				));
		}

		return jsonArray;
	}

	private static ObjectDefinition _fetchObjectDefinition(
		long classNameId, long companyId) {

		if (classNameId <= 0) {
			return null;
		}

		return ObjectDefinitionLocalServiceUtil.
			fetchObjectDefinitionByClassName(
				companyId, PortalUtil.getClassName(classNameId));
	}

	private static JSONArray _getCommonFieldsItemsJSONArray(Locale locale) {
		return JSONUtil.putAll(
			JSONUtil.put(
				"label", LanguageUtil.get(locale, "author-name")
			).put(
				"name", Field.USER_NAME
			).put(
				"type", "text"
			),
			JSONUtil.put(
				"label", LanguageUtil.get(locale, "created-date")
			).put(
				"name", Field.CREATE_DATE
			).put(
				"type", "date"
			),
			JSONUtil.put(
				"label", LanguageUtil.get(locale, "display-date")
			).put(
				"name", Field.DISPLAY_DATE
			).put(
				"type", "date"
			),
			JSONUtil.put(
				"label", LanguageUtil.get(locale, "expiration-date")
			).put(
				"name", Field.EXPIRATION_DATE
			).put(
				"type", "date"
			),
			JSONUtil.put(
				"label", LanguageUtil.get(locale, "external-reference-code")
			).put(
				"name", "externalReferenceCode"
			).put(
				"type", "text"
			),
			JSONUtil.put(
				"label", LanguageUtil.get(locale, "modified-date")
			).put(
				"name", Field.MODIFIED_DATE
			).put(
				"type", "date"
			),
			JSONUtil.put(
				"label", LanguageUtil.get(locale, "priority")
			).put(
				"name", Field.PRIORITY
			).put(
				"type", "decimal"
			),
			JSONUtil.put(
				"label", LanguageUtil.get(locale, "publish-date")
			).put(
				"name", Field.PUBLISH_DATE
			).put(
				"type", "date"
			),
			JSONUtil.put(
				"label", LanguageUtil.get(locale, "review-date")
			).put(
				"name", Field.REVIEW_DATE
			).put(
				"type", "date"
			),
			JSONUtil.put(
				"label", LanguageUtil.get(locale, "status")
			).put(
				"name", Field.STATUS
			).put(
				"type", "integer"
			),
			JSONUtil.put(
				"label", LanguageUtil.get(locale, "title")
			).put(
				"name", Field.TITLE
			).put(
				"type", "text"
			),
			JSONUtil.put(
				"label", LanguageUtil.get(locale, "view-count")
			).put(
				"name", "viewCount"
			).put(
				"type", "integer"
			));
	}

	private static JSONArray _getItemsJSONArray(
		long classNameId, long classTypeId, Locale locale,
		List<ObjectField> objectFields) {

		return JSONUtil.toJSONArray(
			objectFields,
			objectField -> {
				if (objectField.isMetadata()) {
					return null;
				}

				String type = _toType(objectField.getBusinessType());

				if (type == null) {
					return null;
				}

				return _toPropertyJSONObject(
					classNameId, classTypeId, locale, objectField, type);
			},
			_log);
	}

	private static JSONObject _toPropertyJSONObject(
		long classNameId, long classTypeId, Locale locale,
		ObjectField objectField, String type) {

		return JSONUtil.put(
			"classNameId", classNameId
		).put(
			"classTypeId", classTypeId
		).put(
			"label", objectField.getLabel(locale, true)
		).put(
			"name", objectField.getName()
		).put(
			"options",
			() -> {
				if (!type.equals("picklist") ||
					(objectField.getListTypeDefinitionId() <= 0)) {

					return null;
				}

				return JSONUtil.toJSONArray(
					ListTypeEntryLocalServiceUtil.getListTypeEntries(
						objectField.getListTypeDefinitionId()),
					listTypeEntry -> JSONUtil.put(
						"label", listTypeEntry.getName(locale, true)
					).put(
						"value", listTypeEntry.getKey()
					),
					_log);
			}
		).put(
			"type", type
		);
	}

	private static String _toType(String businessType) {
		if (businessType == null) {
			return null;
		}

		if (businessType.equals(ObjectFieldConstants.BUSINESS_TYPE_BOOLEAN)) {
			return "boolean";
		}

		if (businessType.equals(ObjectFieldConstants.BUSINESS_TYPE_DATE)) {
			return "date";
		}

		if (businessType.equals(ObjectFieldConstants.BUSINESS_TYPE_DATE_TIME)) {
			return "date-time";
		}

		if (businessType.equals(ObjectFieldConstants.BUSINESS_TYPE_DECIMAL) ||
			businessType.equals(
				ObjectFieldConstants.BUSINESS_TYPE_PRECISION_DECIMAL)) {

			return "decimal";
		}

		if (businessType.equals(ObjectFieldConstants.BUSINESS_TYPE_INTEGER) ||
			businessType.equals(
				ObjectFieldConstants.BUSINESS_TYPE_LONG_INTEGER)) {

			return "integer";
		}

		if (businessType.equals(ObjectFieldConstants.BUSINESS_TYPE_LONG_TEXT) ||
			businessType.equals(
				ObjectFieldConstants.BUSINESS_TYPE_PHONE_NUMBER) ||
			businessType.equals(ObjectFieldConstants.BUSINESS_TYPE_RICH_TEXT) ||
			businessType.equals(ObjectFieldConstants.BUSINESS_TYPE_TEXT)) {

			return "text";
		}

		if (businessType.equals(
				ObjectFieldConstants.BUSINESS_TYPE_MULTISELECT_PICKLIST) ||
			businessType.equals(ObjectFieldConstants.BUSINESS_TYPE_PICKLIST)) {

			return "picklist";
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AssetListTypePropertiesUtil.class);

}