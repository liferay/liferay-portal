/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.util;

import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.ListTypeEntryLocalServiceUtil;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectEntryLocalServiceUtil;
import com.liferay.object.service.ObjectFieldLocalServiceUtil;
import com.liferay.object.service.ObjectRelationshipLocalServiceUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.site.pim.site.initializer.connector.PIMConnector;
import com.liferay.site.pim.site.initializer.connector.PIMConnectorChannelField;
import com.liferay.site.pim.site.initializer.constants.PIMObjectDefinitionConstants;

import java.io.Serializable;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Stefano Motta
 */
public class PIMConnectorFieldMappingsUtil {

	public static final String TYPE_FIXED_VALUE = "fixedValue";

	public static ObjectRelationship fetchObjectRelationship(
		long objectDefinitionId) {

		return ObjectRelationshipLocalServiceUtil.
			fetchObjectRelationshipByExternalReferenceCode(
				"L_PIM_CONNECTOR_TO_PIM_CONNECTOR_FIELD_MAPPINGS",
				objectDefinitionId);
	}

	public static List<ObjectEntry> filterObjectEntries(
		ObjectDefinition objectDefinition,
		List<ObjectEntry> pimFieldMappingObjectEntries) {

		if (ListUtil.isEmpty(pimFieldMappingObjectEntries)) {
			return Collections.emptyList();
		}

		List<ObjectEntry> objectEntries = ListUtil.filter(
			pimFieldMappingObjectEntries,
			objectEntry -> Objects.equals(
				objectDefinition.getClassName(),
				MapUtil.getString(objectEntry.getValues(), "sourceClassName")));

		if (!objectEntries.isEmpty()) {
			return objectEntries;
		}

		return ListUtil.filter(
			pimFieldMappingObjectEntries,
			objectEntry -> Validator.isNull(
				MapUtil.getString(objectEntry.getValues(), "sourceClassName")));
	}

	public static String getAPIURL(long companyId) {
		ObjectDefinition objectDefinition =
			ObjectDefinitionLocalServiceUtil.
				fetchObjectDefinitionByExternalReferenceCode(
					PIMObjectDefinitionConstants.
						EXTERNAL_REFERENCE_CODE_CONNECTOR_FIELD_MAPPING,
					companyId);

		if (objectDefinition == null) {
			return StringPool.BLANK;
		}

		return "/o" + objectDefinition.getRESTContextPath();
	}

	public static Object getChannelFieldValue(
		ObjectDefinition objectDefinition,
		Map<String, List<ObjectEntry>> objectEntriesMap,
		PIMConnectorChannelField pimConnectorChannelField,
		Map<String, Serializable> values) {

		List<String> channelFieldValues = TransformUtil.transform(
			filterObjectEntries(
				objectDefinition,
				objectEntriesMap.get(pimConnectorChannelField.getName())),
			objectEntry -> {
				String value = getValue(objectDefinition, objectEntry, values);

				if (Validator.isNull(value)) {
					return null;
				}

				return value;
			});

		if (channelFieldValues.isEmpty()) {
			return null;
		}

		String type = pimConnectorChannelField.getType();

		if (pimConnectorChannelField.isMultiple()) {
			return JSONUtil.putAll(
				TransformUtil.transformToArray(
					channelFieldValues,
					channelFieldValue -> _toObject(
						pimConnectorChannelField, channelFieldValue),
					Object.class));
		}

		if (Objects.equals(type, ObjectFieldConstants.BUSINESS_TYPE_TEXT)) {
			return StringUtil.merge(channelFieldValues, StringPool.SPACE);
		}

		return _toObject(pimConnectorChannelField, channelFieldValues.get(0));
	}

	public static String getLabel(
		ObjectDefinition objectDefinition, ObjectEntry objectEntry) {

		ObjectField objectField = ObjectFieldLocalServiceUtil.fetchObjectField(
			objectDefinition.getObjectDefinitionId(),
			MapUtil.getString(objectEntry.getValues(), "sourceFieldName"));

		if (objectField == null) {
			return StringPool.BLANK;
		}

		return objectField.getLabel(LocaleUtil.US);
	}

	public static List<ObjectEntry> getObjectEntries(ObjectEntry objectEntry)
		throws PortalException {

		ObjectRelationship objectRelationship = fetchObjectRelationship(
			objectEntry.getObjectDefinitionId());

		if (objectRelationship == null) {
			return Collections.emptyList();
		}

		return ListUtil.sort(
			ObjectEntryLocalServiceUtil.getOneToManyObjectEntries(
				objectEntry.getGroupId(),
				objectRelationship.getObjectRelationshipId(), null, false,
				objectEntry.getObjectEntryId(), true, null, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null),
			Comparator.comparingInt(
				curObjectEntry -> MapUtil.getInteger(
					curObjectEntry.getValues(), "priority")));
	}

	public static String getValue(
		ObjectDefinition objectDefinition, ObjectEntry objectEntry,
		Map<String, Serializable> values) {

		if (isFixedValue(objectEntry)) {
			return MapUtil.getString(objectEntry.getValues(), "value");
		}

		String sourceFieldName = MapUtil.getString(
			objectEntry.getValues(), "sourceFieldName");

		String value = MapUtil.getString(values, sourceFieldName);

		if (Validator.isNull(value)) {
			return value;
		}

		ObjectField objectField = ObjectFieldLocalServiceUtil.fetchObjectField(
			objectDefinition.getObjectDefinitionId(), sourceFieldName);

		if ((objectField == null) ||
			!objectField.compareBusinessType(
				ObjectFieldConstants.BUSINESS_TYPE_PICKLIST)) {

			return value;
		}

		ListTypeEntry listTypeEntry =
			ListTypeEntryLocalServiceUtil.fetchListTypeEntry(
				objectField.getListTypeDefinitionId(), value);

		if (listTypeEntry == null) {
			return value;
		}

		return listTypeEntry.getName(LocaleUtil.US);
	}

	public static boolean isFixedValue(ObjectEntry objectEntry) {
		return Objects.equals(
			MapUtil.getString(objectEntry.getValues(), "type"),
			TYPE_FIXED_VALUE);
	}

	public static boolean isMapped(ObjectEntry objectEntry) {
		if (isFixedValue(objectEntry)) {
			return Validator.isNotNull(
				MapUtil.getString(objectEntry.getValues(), "value"));
		}

		return Validator.isNotNull(
			MapUtil.getString(objectEntry.getValues(), "sourceFieldName"));
	}

	public static boolean isMappingComplete(
		List<ObjectEntry> objectEntries, PIMConnector pimConnector) {

		if (pimConnector == null) {
			return false;
		}

		Set<String> mappedChannelFieldNames = new HashSet<>();

		for (ObjectEntry objectEntry : objectEntries) {
			if (!isMapped(objectEntry)) {
				continue;
			}

			mappedChannelFieldNames.add(
				MapUtil.getString(objectEntry.getValues(), "channelFieldName"));
		}

		for (PIMConnectorChannelField pimConnectorChannelField :
				pimConnector.getPIMConnectorChannelFields()) {

			if (pimConnectorChannelField.isRequired() &&
				!mappedChannelFieldNames.contains(
					pimConnectorChannelField.getName())) {

				return false;
			}
		}

		return true;
	}

	public static boolean isMappingComplete(
			ObjectEntry objectEntry, PIMConnector pimConnector)
		throws PortalException {

		if (pimConnector == null) {
			return false;
		}

		if (!ListUtil.exists(
				pimConnector.getPIMConnectorChannelFields(),
				PIMConnectorChannelField::isRequired)) {

			return true;
		}

		return isMappingComplete(getObjectEntries(objectEntry), pimConnector);
	}

	private static Object _toObject(
		PIMConnectorChannelField pimConnectorChannelField, String value) {

		String type = pimConnectorChannelField.getType();

		if (Objects.equals(type, ObjectFieldConstants.BUSINESS_TYPE_BOOLEAN)) {
			return Boolean.valueOf(value);
		}

		try {
			if (Objects.equals(
					type, ObjectFieldConstants.BUSINESS_TYPE_DECIMAL)) {

				return Double.valueOf(value);
			}

			if (Objects.equals(
					type, ObjectFieldConstants.BUSINESS_TYPE_INTEGER) ||
				Objects.equals(
					type, ObjectFieldConstants.BUSINESS_TYPE_LONG_INTEGER)) {

				return Long.valueOf(value);
			}
		}
		catch (NumberFormatException numberFormatException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Unable to convert ",
						pimConnectorChannelField.getName(), " to ", type),
					numberFormatException);
			}
		}

		return value;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PIMConnectorFieldMappingsUtil.class);

}