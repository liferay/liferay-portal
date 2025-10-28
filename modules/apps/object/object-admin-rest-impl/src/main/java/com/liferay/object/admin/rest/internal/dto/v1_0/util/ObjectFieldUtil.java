/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.internal.dto.v1_0.util;

import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.ListTypeDefinitionLocalService;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.object.admin.rest.dto.v1_0.ObjectField;
import com.liferay.object.admin.rest.dto.v1_0.ObjectFieldSetting;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFieldSettingLocalService;
import com.liferay.object.service.ObjectFilterLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Gabriel Albuquerque
 */
public class ObjectFieldUtil {

	public static long addListTypeDefinition(
			long companyId,
			ListTypeDefinitionLocalService listTypeDefinitionLocalService,
			ListTypeEntryLocalService listTypeEntryLocalService,
			ObjectField objectField, long userId)
		throws Exception {

		if (!(StringUtil.equals(
				objectField.getBusinessTypeAsString(),
				ObjectFieldConstants.BUSINESS_TYPE_MULTISELECT_PICKLIST) ||
			  StringUtil.equals(
				  objectField.getBusinessTypeAsString(),
				  ObjectFieldConstants.BUSINESS_TYPE_PICKLIST))) {

			return 0;
		}

		ListTypeDefinition listTypeDefinition =
			listTypeDefinitionLocalService.
				fetchListTypeDefinitionByExternalReferenceCode(
					objectField.getListTypeDefinitionExternalReferenceCode(),
					companyId);

		if (listTypeDefinition == null) {
			listTypeDefinition =
				listTypeDefinitionLocalService.fetchListTypeDefinition(
					GetterUtil.getLong(objectField.getListTypeDefinitionId()));
		}

		if (listTypeDefinition != null) {
			return listTypeDefinition.getListTypeDefinitionId();
		}

		listTypeDefinition =
			listTypeDefinitionLocalService.addListTypeDefinition(
				objectField.getListTypeDefinitionExternalReferenceCode(),
				userId, GetterUtil.getBoolean(objectField.getSystem()));

		Map<String, ListTypeEntry> listTypeEntries = new HashMap<>();

		ListUtil.isNotEmptyForEach(
			listTypeEntryLocalService.getListTypeEntries(
				listTypeDefinition.getListTypeDefinitionId()),
			listTypeEntry -> listTypeEntries.put(
				listTypeEntry.getKey(), listTypeEntry));

		ObjectFieldSetting[] stateFlowObjectFieldSettings = ArrayUtil.filter(
			objectField.getObjectFieldSettings(),
			objectFieldSetting -> StringUtil.equals(
				objectFieldSetting.getName(),
				ObjectFieldSettingConstants.NAME_STATE_FLOW));

		if (ArrayUtil.isNotEmpty(stateFlowObjectFieldSettings)) {
			JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
				JSONFactoryUtil.looseSerializeDeep(
					stateFlowObjectFieldSettings[0].getValue()));

			JSONArray objectStatesJSONArray = jsonObject.getJSONArray(
				"objectStates");

			for (int i = 0; i < objectStatesJSONArray.length(); i++) {
				JSONObject objectStateJSONObject =
					objectStatesJSONArray.getJSONObject(i);

				String key = objectStateJSONObject.getString("key");

				if (listTypeEntries.containsKey(key)) {
					listTypeEntries.remove(key);

					continue;
				}

				listTypeEntryLocalService.addListTypeEntry(
					null, userId, listTypeDefinition.getListTypeDefinitionId(),
					key, Collections.singletonMap(LocaleUtil.getDefault(), key),
					listTypeDefinition.isSystem());
			}
		}

		ObjectFieldSetting[] defaultObjectFieldSettings = null;

		if (!GetterUtil.getBoolean(objectField.getState())) {
			defaultObjectFieldSettings = ArrayUtil.filter(
				objectField.getObjectFieldSettings(),
				objectFieldSetting -> StringUtil.equals(
					objectFieldSetting.getName(),
					ObjectFieldSettingConstants.NAME_DEFAULT_VALUE));
		}

		if (ArrayUtil.isEmpty(defaultObjectFieldSettings)) {
			return listTypeDefinition.getListTypeDefinitionId();
		}

		String defaultObjectFieldSettingValue = GetterUtil.getString(
			defaultObjectFieldSettings[0].getValue());

		if (listTypeEntries.containsKey(defaultObjectFieldSettingValue)) {
			listTypeEntries.remove(defaultObjectFieldSettingValue);
		}

		listTypeEntryLocalService.addListTypeEntry(
			null, userId, listTypeDefinition.getListTypeDefinitionId(),
			defaultObjectFieldSettingValue,
			Collections.singletonMap(
				LocaleUtil.getDefault(), defaultObjectFieldSettingValue),
			listTypeDefinition.isSystem());

		for (ListTypeEntry listTypeEntry : listTypeEntries.values()) {
			listTypeEntryLocalService.deleteListTypeEntry(listTypeEntry);
		}

		return listTypeDefinition.getListTypeDefinitionId();
	}

	public static String getDBType(String dbType, String type) {
		if (Validator.isNull(dbType) && Validator.isNotNull(type)) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"The type property is deprecated. Use the DBType " +
						"property instead.");
			}

			return type;
		}

		return dbType;
	}

	public static long getListTypeDefinitionId(
		long companyId,
		ListTypeDefinitionLocalService listTypeDefinitionLocalService,
		ObjectField objectField) {

		if (!StringUtil.equals(
				objectField.getBusinessTypeAsString(),
				ObjectFieldConstants.BUSINESS_TYPE_MULTISELECT_PICKLIST) &&
			!StringUtil.equals(
				objectField.getBusinessTypeAsString(),
				ObjectFieldConstants.BUSINESS_TYPE_PICKLIST)) {

			return 0;
		}

		long listTypeDefinitionId = GetterUtil.getLong(
			objectField.getListTypeDefinitionId());

		if ((listTypeDefinitionId != 0) ||
			Validator.isNull(
				objectField.getListTypeDefinitionExternalReferenceCode())) {

			return listTypeDefinitionId;
		}

		ListTypeDefinition listTypeDefinition =
			listTypeDefinitionLocalService.
				fetchListTypeDefinitionByExternalReferenceCode(
					objectField.getListTypeDefinitionExternalReferenceCode(),
					companyId);

		if (listTypeDefinition == null) {
			return 0;
		}

		return listTypeDefinition.getListTypeDefinitionId();
	}

	public static com.liferay.object.model.ObjectField toObjectField(
		String defaultLanguageId,
		ListTypeDefinitionLocalService listTypeDefinitionLocalService,
		ObjectField objectField,
		ObjectFieldLocalService objectFieldLocalService,
		ObjectFieldSettingLocalService objectFieldSettingLocalService,
		ObjectFilterLocalService objectFilterLocalService) {

		if (objectField == null) {
			return null;
		}

		com.liferay.object.model.ObjectField serviceBuilderObjectField =
			objectFieldLocalService.createObjectField(0L);

		serviceBuilderObjectField.setExternalReferenceCode(
			objectField.getExternalReferenceCode());

		long listTypeDefinitionId = getListTypeDefinitionId(
			serviceBuilderObjectField.getCompanyId(),
			listTypeDefinitionLocalService, objectField);

		serviceBuilderObjectField.setListTypeDefinitionId(listTypeDefinitionId);

		serviceBuilderObjectField.setBusinessType(
			objectField.getBusinessTypeAsString());
		serviceBuilderObjectField.setDBType(
			getDBType(
				objectField.getDBTypeAsString(),
				objectField.getTypeAsString()));
		serviceBuilderObjectField.setIndexed(
			GetterUtil.getBoolean(objectField.getIndexed()));
		serviceBuilderObjectField.setIndexedAsKeyword(
			GetterUtil.getBoolean(objectField.getIndexedAsKeyword()));
		serviceBuilderObjectField.setIndexedLanguageId(
			objectField.getIndexedLanguageId());

		Map<Locale, String> localizedLabelMap =
			LocalizedMapUtil.populateLocalizedMap(
				defaultLanguageId, objectField.getLabel(),
				objectField.getName());

		if (GetterUtil.getBoolean(objectField.getSystem())) {
			Locale siteDefaultLocale = LocaleUtil.getSiteDefault();

			localizedLabelMap.put(
				siteDefaultLocale,
				LanguageUtil.get(
					siteDefaultLocale,
					_systemObjectFieldLabelKeys.get(objectField.getName()),
					localizedLabelMap.get(siteDefaultLocale)));
		}

		serviceBuilderObjectField.setLabelMap(localizedLabelMap);

		serviceBuilderObjectField.setLocalized(
			GetterUtil.getBoolean(objectField.getLocalized()));
		serviceBuilderObjectField.setName(objectField.getName());
		serviceBuilderObjectField.setObjectFieldSettings(
			ObjectFieldSettingUtil.toObjectFieldSettings(
				listTypeDefinitionId, objectField,
				objectFieldSettingLocalService, objectFilterLocalService));
		serviceBuilderObjectField.setReadOnly(
			objectField.getReadOnlyAsString());
		serviceBuilderObjectField.setReadOnlyConditionExpression(
			objectField.getReadOnlyConditionExpression());
		serviceBuilderObjectField.setRequired(
			GetterUtil.getBoolean(objectField.getRequired()));

		if (Validator.isNotNull(objectField.getState())) {
			serviceBuilderObjectField.setState(objectField.getState());
		}

		serviceBuilderObjectField.setSystem(
			GetterUtil.getBoolean(objectField.getSystem()));

		return serviceBuilderObjectField;
	}

	public static List<com.liferay.object.model.ObjectField> toObjectFields(
		String defaultLanguageId,
		ListTypeDefinitionLocalService listTypeDefinitionLocalService,
		ObjectFieldLocalService objectFieldLocalService,
		ObjectField[] objectFields,
		ObjectFieldSettingLocalService objectFieldSettingLocalService,
		ObjectFilterLocalService objectFilterLocalService) {

		if (objectFields == null) {
			return new ArrayList<>();
		}

		return TransformUtil.transformToList(
			objectFields,
			objectField -> {
				com.liferay.object.model.ObjectField serviceBuilderObjectField =
					toObjectField(
						defaultLanguageId, listTypeDefinitionLocalService,
						objectField, objectFieldLocalService,
						objectFieldSettingLocalService,
						objectFilterLocalService);

				serviceBuilderObjectField.setObjectFieldId(
					GetterUtil.getLong(objectField.getId()));

				return serviceBuilderObjectField;
			});
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectFieldUtil.class);

	private static final Map<String, String> _systemObjectFieldLabelKeys =
		HashMapBuilder.put(
			"createDate", "create-date"
		).put(
			"creator", "author"
		).put(
			"displayDate", "display-date"
		).put(
			"expirationDate", "expiration-date"
		).put(
			"externalReferenceCode", "external-reference-code"
		).put(
			"id", "id"
		).put(
			"modifiedDate", "modified-date"
		).put(
			"reviewDate", "review-date"
		).put(
			"status", "status"
		).build();

}