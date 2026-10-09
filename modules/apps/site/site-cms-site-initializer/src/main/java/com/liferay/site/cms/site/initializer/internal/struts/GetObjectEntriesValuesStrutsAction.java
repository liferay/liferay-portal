/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.struts;

import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.Serializable;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Víctor Galán Grande
 */
@Component(
	property = "path=/cms/get_object_entries_values",
	service = StrutsAction.class
)
public class GetObjectEntriesValuesStrutsAction implements StrutsAction {

	@Override
	public String execute(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		String objectEntriesString = ParamUtil.getString(
			httpServletRequest, "objectEntries");

		if (Validator.isNull(objectEntriesString)) {
			return null;
		}

		JSONArray resultJSONArray = _jsonFactory.createJSONArray();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		JSONArray objectEntriesJSONArray = _jsonFactory.createJSONArray(
			objectEntriesString);

		for (int i = 0; i < objectEntriesJSONArray.length(); i++) {
			JSONObject objectEntryJSONObject =
				objectEntriesJSONArray.getJSONObject(i);

			String className = objectEntryJSONObject.getString("className");

			ObjectDefinition objectDefinition =
				_objectDefinitionLocalService.fetchObjectDefinitionByClassName(
					themeDisplay.getCompanyId(), className);

			if (objectDefinition == null) {
				continue;
			}

			long objectEntryId = objectEntryJSONObject.getLong("objectEntryId");

			ObjectEntry objectEntry = _getObjectEntry(objectEntryId);

			if (objectEntry == null) {
				continue;
			}

			JSONObject resultJSONObject = _getObjectEntryValuesJSONObject(
				themeDisplay.getLocale(), objectDefinition, objectEntry,
				new HashSet<>());

			if (resultJSONObject == null) {
				continue;
			}

			resultJSONArray.put(resultJSONObject);
		}

		ServletResponseUtil.write(
			httpServletResponse, resultJSONArray.toString());

		return null;
	}

	private ObjectEntry _getObjectEntry(long objectEntryId) {
		try {
			return _objectEntryService.fetchObjectEntry(objectEntryId);
		}
		catch (Exception exception) {
			_log.error(exception);

			return null;
		}
	}

	private JSONObject _getObjectEntryValuesJSONObject(
			Locale locale, ObjectDefinition objectDefinition,
			ObjectEntry objectEntry, Set<Long> objectEntryIds)
		throws Exception {

		if (objectEntryIds.contains(objectEntry.getObjectEntryId())) {
			return null;
		}

		objectEntryIds.add(objectEntry.getObjectEntryId());

		JSONObject valuesJSONObject = JSONUtil.put(
			"externalReferenceCode", objectEntry.getExternalReferenceCode()
		).put(
			"id", String.valueOf(objectEntry.getObjectEntryId())
		);

		JSONArray fieldsJSONArray = _jsonFactory.createJSONArray();

		List<ObjectField> objectFields =
			_objectFieldLocalService.getObjectFields(
				objectDefinition.getObjectDefinitionId());

		Map<String, Serializable> values = objectEntry.getValues();

		for (ObjectField objectField : objectFields) {
			if ((!objectField.compareBusinessType(
					ObjectFieldConstants.BUSINESS_TYPE_LONG_TEXT) &&
				 !objectField.compareBusinessType(
					 ObjectFieldConstants.BUSINESS_TYPE_RICH_TEXT) &&
				 !objectField.compareBusinessType(
					 ObjectFieldConstants.BUSINESS_TYPE_TEXT)) ||
				objectField.isMetadata()) {

				continue;
			}

			JSONObject fieldJSONObject = JSONUtil.put(
				"label", objectField.getLabel(locale)
			).put(
				"name", objectField.getName()
			);

			if (objectField.isLocalized()) {
				Serializable value = values.get(
					objectField.getI18nObjectFieldName());

				if (!(value instanceof Map)) {
					continue;
				}

				JSONObject valueI18nJSONObject =
					_jsonFactory.createJSONObject();

				Map<String, Serializable> localizedValues =
					(Map<String, Serializable>)value;

				if (MapUtil.isEmpty(localizedValues)) {
					continue;
				}

				for (Map.Entry<String, Serializable> entry :
						localizedValues.entrySet()) {

					valueI18nJSONObject.put(entry.getKey(), entry.getValue());
				}

				fieldJSONObject.put("value_i18n", valueI18nJSONObject);
			}
			else {
				Serializable value = values.get(objectField.getName());

				if (value == null) {
					continue;
				}

				fieldJSONObject.put("value", String.valueOf(value));
			}

			fieldsJSONArray.put(fieldJSONObject);
		}

		valuesJSONObject.put("fields", fieldsJSONArray);

		JSONArray relatedJSONArray = _jsonFactory.createJSONArray();

		List<ObjectRelationship> objectRelationships =
			_objectRelationshipLocalService.getObjectRelationships(
				objectDefinition.getObjectDefinitionId());

		for (ObjectRelationship objectRelationship : objectRelationships) {
			if (!objectRelationship.compareType(
					ObjectRelationshipConstants.TYPE_ONE_TO_MANY) ||
				!Objects.equals(
					objectRelationship.getDeletionType(),
					ObjectRelationshipConstants.DELETION_TYPE_CASCADE)) {

				continue;
			}

			ObjectDefinition relatedObjectDefinition =
				_objectDefinitionLocalService.getObjectDefinition(
					objectRelationship.getObjectDefinitionId2());

			List<ObjectEntry> relatedObjectEntries =
				_objectEntryService.getOneToManyObjectEntries(
					objectEntry.getGroupId(),
					objectRelationship.getObjectRelationshipId(), null, false,
					objectEntry.getObjectEntryId(), true, null, -1, -1, null);

			for (ObjectEntry relatedObjectEntry : relatedObjectEntries) {
				JSONObject jsonObject = _getObjectEntryValuesJSONObject(
					locale, relatedObjectDefinition, relatedObjectEntry,
					objectEntryIds);

				if (jsonObject == null) {
					continue;
				}

				relatedJSONArray.put(
					jsonObject.put(
						"label", objectRelationship.getLabelMap()
					).put(
						"name", objectRelationship.getName()
					));
			}
		}

		valuesJSONObject.put("related", relatedJSONArray);

		return valuesJSONObject;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GetObjectEntriesValuesStrutsAction.class);

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryService _objectEntryService;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

}