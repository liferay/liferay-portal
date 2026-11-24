/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.util;

import com.liferay.depot.model.DepotEntry;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectEntryFolderLocalServiceUtil;
import com.liferay.object.service.ObjectEntryLocalServiceUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Stefano Motta
 */
public class CMSDefaultValueUtil {

	public static ObjectEntry addOrUpdateObjectEntry(
			long userId, JSONArray defaultValuesJSONArray,
			DepotEntry depotEntry, FilterFactory<Predicate> filterFactory)
		throws PortalException {

		Group group = depotEntry.getGroup();

		return _addOrUpdateObjectEntry(
			group.getGroupId(), userId, group.getExternalReferenceCode(),
			DepotEntry.class.getName(), defaultValuesJSONArray, filterFactory,
			StringPool.BLANK);
	}

	public static ObjectEntry addOrUpdateObjectEntry(
			long userId, JSONArray defaultValuesJSONArray,
			FilterFactory<Predicate> filterFactory,
			ObjectEntryFolder objectEntryFolder)
		throws PortalException {

		return _addOrUpdateObjectEntry(
			objectEntryFolder.getGroupId(), userId,
			objectEntryFolder.getExternalReferenceCode(),
			ObjectEntryFolder.class.getName(), defaultValuesJSONArray,
			filterFactory, objectEntryFolder.getTreePath());
	}

	public static ObjectDefinition fetchObjectDefinition(long companyId) {
		return ObjectDefinitionLocalServiceUtil.
			fetchObjectDefinitionByExternalReferenceCode(
				"L_CMS_DEFAULT_VALUE", companyId);
	}

	public static ObjectEntry fetchObjectEntry(
			long groupId, String classExternalReferenceCode, String className,
			FilterFactory<Predicate> filterFactory)
		throws PortalException {

		Group group = GroupLocalServiceUtil.getGroup(groupId);

		ObjectDefinition objectDefinition = fetchObjectDefinition(
			group.getCompanyId());

		if (objectDefinition == null) {
			return null;
		}

		List<Long> primaryKeys = ObjectEntryLocalServiceUtil.getPrimaryKeys(
			new Long[] {groupId}, group.getCompanyId(), 0,
			objectDefinition.getObjectDefinitionId(),
			filterFactory.create(
				_getFilterString(
					List.of(classExternalReferenceCode), className),
				objectDefinition),
			false, null, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		if (ListUtil.isEmpty(primaryKeys)) {
			return null;
		}

		return ObjectEntryLocalServiceUtil.fetchObjectEntry(primaryKeys.get(0));
	}

	public static JSONArray getDefaultValuesJSONArray(
			long groupId, String classExternalReferenceCode, String className,
			FilterFactory<Predicate> filterFactory)
		throws PortalException {

		ObjectEntry objectEntry = fetchObjectEntry(
			groupId, classExternalReferenceCode, className, filterFactory);

		if (objectEntry == null) {
			return JSONFactoryUtil.createJSONArray();
		}

		return JSONFactoryUtil.createJSONArray(
			MapUtil.getString(objectEntry.getValues(), "defaultValues", "[]"));
	}

	public static Map<String, JSONObject> getInheritedDefaultValues(
			long groupId, FilterFactory<Predicate> filterFactory,
			long objectEntryFolderId)
		throws PortalException {

		Map<String, JSONObject> defaultValues = new LinkedHashMap<>();

		if (groupId == 0) {
			return defaultValues;
		}

		Group group = GroupLocalServiceUtil.getGroup(groupId);

		ObjectDefinition objectDefinition = fetchObjectDefinition(
			group.getCompanyId());

		if (objectDefinition == null) {
			return defaultValues;
		}

		String filterString = _getFilterString(
			List.of(group.getExternalReferenceCode()),
			DepotEntry.class.getName());

		List<String> objectEntryFolderExternalReferenceCodes =
			new ArrayList<>();

		while (objectEntryFolderId !=
					ObjectEntryFolderConstants.
						PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT) {

			ObjectEntryFolder objectEntryFolder =
				ObjectEntryFolderLocalServiceUtil.getObjectEntryFolder(
					objectEntryFolderId);

			objectEntryFolderExternalReferenceCodes.add(
				objectEntryFolder.getExternalReferenceCode());

			objectEntryFolderId =
				objectEntryFolder.getParentObjectEntryFolderId();
		}

		if (!objectEntryFolderExternalReferenceCodes.isEmpty()) {
			filterString = StringBundler.concat(
				"(", filterString, ") or (",
				_getFilterString(
					objectEntryFolderExternalReferenceCodes,
					ObjectEntryFolder.class.getName()),
				")");
		}

		List<String> classKeys = ListUtil.toList(
			objectEntryFolderExternalReferenceCodes,
			objectEntryFolderExternalReferenceCode -> _getClassKey(
				objectEntryFolderExternalReferenceCode,
				ObjectEntryFolder.class.getName()));

		classKeys.add(
			_getClassKey(
				group.getExternalReferenceCode(), DepotEntry.class.getName()));

		for (Map<String, Serializable> values :
				ListUtil.sort(
					ObjectEntryLocalServiceUtil.getValuesList(
						groupId, group.getCompanyId(), 0,
						objectDefinition.getObjectDefinitionId(),
						filterFactory.create(filterString, objectDefinition),
						null, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null),
					Comparator.comparingInt(
						values -> classKeys.indexOf(
							_getClassKey(
								MapUtil.getString(
									values, "classExternalReferenceCode"),
								MapUtil.getString(values, "className")))))) {

			JSONArray defaultValuesJSONArray = JSONFactoryUtil.createJSONArray(
				MapUtil.getString(values, "defaultValues", "[]"));

			for (int i = 0; i < defaultValuesJSONArray.length(); i++) {
				JSONObject jsonObject = defaultValuesJSONArray.getJSONObject(i);

				String key = getObjectFieldKey(
					jsonObject.getString("listTypeDefinitionERC"),
					jsonObject.getString("name"), jsonObject.getString("type"));

				if (defaultValues.containsKey(key) ||
					!_hasValue(jsonObject, "value")) {

					continue;
				}

				Object value = jsonObject.get("value");

				if (value instanceof JSONObject) {
					JSONObject valueJSONObject = (JSONObject)value;

					for (String valueKey :
							new ArrayList<>(valueJSONObject.keySet())) {

						if (_hasValue(valueJSONObject, valueKey)) {
							continue;
						}

						valueJSONObject.remove(valueKey);
					}
				}

				defaultValues.put(
					key,
					jsonObject.put(
						"source",
						JSONUtil.put(
							"className", MapUtil.getString(values, "className")
						).put(
							"externalReferenceCode",
							MapUtil.getString(
								values, "classExternalReferenceCode")
						)));
			}
		}

		return defaultValues;
	}

	public static String getObjectFieldKey(
		String listTypeDefinitionExternalReferenceCode, String name,
		String type) {

		return StringBundler.concat(
			GetterUtil.getString(listTypeDefinitionExternalReferenceCode),
			StringPool.POUND, name, StringPool.POUND, type);
	}

	private static ObjectEntry _addOrUpdateObjectEntry(
			long groupId, long userId, String classExternalReferenceCode,
			String className, JSONArray defaultValuesJSONArray,
			FilterFactory<Predicate> filterFactory, String treePath)
		throws PortalException {

		Map<String, Serializable> values =
			HashMapBuilder.<String, Serializable>put(
				"classExternalReferenceCode", classExternalReferenceCode
			).put(
				"className", className
			).put(
				"defaultValues", defaultValuesJSONArray.toString()
			).put(
				"treePath", treePath
			).build();

		ObjectEntry objectEntry = fetchObjectEntry(
			groupId, classExternalReferenceCode, className, filterFactory);

		if (objectEntry != null) {
			return ObjectEntryLocalServiceUtil.updateObjectEntry(
				userId, objectEntry.getObjectEntryId(),
				objectEntry.getObjectEntryFolderId(), values,
				new ServiceContext());
		}

		Group group = GroupLocalServiceUtil.getGroup(groupId);

		ObjectDefinition objectDefinition =
			ObjectDefinitionLocalServiceUtil.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMS_DEFAULT_VALUE", group.getCompanyId());

		return ObjectEntryLocalServiceUtil.addObjectEntry(
			groupId, userId, objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null, values, new ServiceContext());
	}

	private static String _getClassKey(
		String classExternalReferenceCode, String className) {

		return classExternalReferenceCode + StringPool.POUND + className;
	}

	private static String _getFilterString(
		List<String> classExternalReferenceCodes, String className) {

		return StringBundler.concat(
			"(className eq '",
			StringUtil.replace(
				className, CharPool.APOSTROPHE, StringPool.DOUBLE_APOSTROPHE),
			"') and (classExternalReferenceCode in (",
			StringUtil.merge(
				ListUtil.toList(
					classExternalReferenceCodes,
					classExternalReferenceCode -> StringUtil.quote(
						StringUtil.replace(
							classExternalReferenceCode, CharPool.APOSTROPHE,
							StringPool.DOUBLE_APOSTROPHE),
						StringPool.APOSTROPHE)),
				StringPool.COMMA),
			"))");
	}

	private static boolean _hasValue(JSONObject jsonObject, String key) {
		if (jsonObject.isNull(key)) {
			return false;
		}

		Object value = jsonObject.get(key);

		if (value instanceof String) {
			return Validator.isNotNull((String)value);
		}

		if (value instanceof JSONArray) {
			JSONArray jsonArray = (JSONArray)value;

			if (jsonArray.length() > 0) {
				return true;
			}

			return false;
		}

		if (value instanceof JSONObject) {
			JSONObject valueJSONObject = (JSONObject)value;

			for (String valueKey : valueJSONObject.keySet()) {
				if (_hasValue(valueJSONObject, valueKey)) {
					return true;
				}
			}

			return false;
		}

		return true;
	}

}