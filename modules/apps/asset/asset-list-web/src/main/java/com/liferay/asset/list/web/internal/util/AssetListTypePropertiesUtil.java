/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.web.internal.util;

import com.liferay.asset.kernel.service.persistence.AssetEntryQuery;
import com.liferay.list.type.service.ListTypeEntryLocalServiceUtil;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.bag.ObjectFieldBag;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.ArrayUtil;
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

			ObjectFieldBag objectFieldBag =
				objectDefinition.getObjectFieldBag();

			jsonArray.put(
				JSONUtil.put(
					"items",
					_getItemsJSONArray(
						classNameIds[i], classTypeId, locale,
						objectFieldBag.getNestedIndexedObjectFields())
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

	private static JSONObject _getCommonFieldJSONObject(
		String labelKey, Locale locale, String name, String type) {

		return JSONUtil.put(
			"label", LanguageUtil.get(locale, labelKey)
		).put(
			"name", name
		).put(
			"sortable",
			ArrayUtil.contains(AssetEntryQuery.ORDER_BY_COLUMNS, name) ||
			name.equals(Field.MODIFIED_DATE)
		).put(
			"type", type
		);
	}

	private static JSONArray _getCommonFieldsItemsJSONArray(Locale locale) {
		return JSONUtil.putAll(
			_getCommonFieldJSONObject(
				"author-name", locale, Field.USER_NAME, "text"),
			_getCommonFieldJSONObject(
				"created-date", locale, Field.CREATE_DATE, "date"),
			_getCommonFieldJSONObject(
				"display-date", locale, Field.DISPLAY_DATE, "date"),
			_getCommonFieldJSONObject(
				"expiration-date", locale, Field.EXPIRATION_DATE, "date"),
			_getCommonFieldJSONObject(
				"external-reference-code", locale, "externalReferenceCode",
				"text"),
			_getCommonFieldJSONObject(
				"modified-date", locale, Field.MODIFIED_DATE, "date"),
			_getCommonFieldJSONObject(
				"priority", locale, Field.PRIORITY, "decimal"),
			_getCommonFieldJSONObject(
				"publish-date", locale, Field.PUBLISH_DATE, "date"),
			_getCommonFieldJSONObject(
				"review-date", locale, Field.REVIEW_DATE, "date"),
			_getCommonFieldJSONObject(
				"status", locale, Field.STATUS, "integer"),
			_getCommonFieldJSONObject("title", locale, Field.TITLE, "text"),
			_getCommonFieldJSONObject(
				"view-count", locale, "viewCount", "integer"));
	}

	private static JSONArray _getItemsJSONArray(
		long classNameId, long classTypeId, Locale locale,
		List<ObjectField> objectFields) {

		return JSONUtil.toJSONArray(
			objectFields,
			objectField -> {
				String type = _toType(objectField.getBusinessType());

				if (type == null) {
					return null;
				}

				return _getPropertyJSONObject(
					classNameId, classTypeId, locale, objectField, type);
			},
			_log);
	}

	private static JSONObject _getPropertyJSONObject(
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
			"sortable", _isSortable(objectField.getBusinessType())
		).put(
			"type", type
		);
	}

	private static boolean _isSortable(String businessType) {
		if (businessType.equals(ObjectFieldConstants.BUSINESS_TYPE_LONG_TEXT) ||
			businessType.equals(
				ObjectFieldConstants.BUSINESS_TYPE_MULTISELECT_PICKLIST) ||
			businessType.equals(ObjectFieldConstants.BUSINESS_TYPE_RICH_TEXT)) {

			return false;
		}

		return true;
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