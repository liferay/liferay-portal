/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.util;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectEntryLocalServiceUtil;
import com.liferay.object.service.ObjectRelationshipLocalServiceUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.HtmlParserUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.site.pim.site.initializer.constants.PIMObjectDefinitionConstants;

import java.io.Serializable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Balazs Breier
 */
public class PIMConnectorFieldMappingRulesUtil {

	public static String applyRules(ObjectEntry objectEntry, String value) {
		if ((objectEntry == null) || Validator.isNull(value)) {
			return value;
		}

		Map<String, Serializable> values = objectEntry.getValues();

		if (MapUtil.getBoolean(values, "stripHTMLTags")) {
			value = HtmlParserUtil.extractText(value);
		}

		String textCase = MapUtil.getString(values, "textCase");

		if (Objects.equals(textCase, "lower")) {
			value = StringUtil.toLowerCase(value);
		}
		else if (Objects.equals(textCase, "upper")) {
			value = StringUtil.toUpperCase(value);
		}

		if (MapUtil.getBoolean(values, "trimWhitespace")) {
			value = StringUtil.trim(value);
		}

		int truncateLength = MapUtil.getInteger(values, "truncateLength");

		if (MapUtil.getBoolean(values, "urlEncode")) {
			return _encodeURL(truncateLength, value);
		}

		if (truncateLength > 0) {
			return StringUtil.shorten(value, truncateLength);
		}

		return value;
	}

	public static String getAPIURL(long companyId) {
		ObjectDefinition objectDefinition =
			ObjectDefinitionLocalServiceUtil.
				fetchObjectDefinitionByExternalReferenceCode(
					PIMObjectDefinitionConstants.
						EXTERNAL_REFERENCE_CODE_CONNECTOR_FIELD_MAPPING_RULE,
					companyId);

		if (objectDefinition == null) {
			return StringPool.BLANK;
		}

		return "/o" + objectDefinition.getRESTContextPath();
	}

	public static Map<String, ObjectEntry> getObjectEntriesMap(
			ObjectEntry objectEntry)
		throws PortalException {

		Map<String, ObjectEntry> objectEntriesMap = new HashMap<>();

		for (ObjectEntry curObjectEntry : _getObjectEntries(objectEntry)) {
			objectEntriesMap.put(
				MapUtil.getString(
					curObjectEntry.getValues(), "channelFieldName"),
				curObjectEntry);
		}

		return objectEntriesMap;
	}

	private static String _encodeURL(int length, String value) {
		String encodedValue = URLCodec.encodeURL(value, true);

		if ((length == 0) || (encodedValue.length() <= length)) {
			return encodedValue;
		}

		StringBundler sb = new StringBundler();

		int encodedLength = StringPool.TRIPLE_PERIOD.length();
		int index = 0;

		while (index < value.length()) {
			int nextIndex = value.offsetByCodePoints(index, 1);

			String encodedCodePoint = URLCodec.encodeURL(
				value.substring(index, nextIndex), true);

			encodedLength += encodedCodePoint.length();

			if (encodedLength > length) {
				break;
			}

			sb.append(encodedCodePoint);

			index = nextIndex;
		}

		sb.append(StringPool.TRIPLE_PERIOD);

		return sb.toString();
	}

	private static List<ObjectEntry> _getObjectEntries(ObjectEntry objectEntry)
		throws PortalException {

		ObjectRelationship objectRelationship =
			ObjectRelationshipLocalServiceUtil.
				fetchObjectRelationshipByExternalReferenceCode(
					"L_PIM_CONNECTOR_TO_PIM_CONNECTOR_FIELD_MAPPING_RULES",
					objectEntry.getObjectDefinitionId());

		if (objectRelationship == null) {
			return Collections.emptyList();
		}

		return ObjectEntryLocalServiceUtil.getOneToManyObjectEntries(
			objectEntry.getGroupId(),
			objectRelationship.getObjectRelationshipId(), null, false,
			objectEntry.getObjectEntryId(), true, null, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

}