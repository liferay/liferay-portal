/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.internal.util;

import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectFieldLocalServiceUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Locale;

/**
 * @author Joshua Cords
 * @author Olivia Yu
 */
public class AssetListObjectFieldUtil {

	public static ObjectField fetchObjectField(
		long classNameId, long companyId, String name) {

		if (classNameId <= 0) {
			return null;
		}

		ObjectDefinition objectDefinition =
			ObjectDefinitionLocalServiceUtil.fetchObjectDefinitionByClassName(
				companyId, PortalUtil.getClassName(classNameId));

		if (objectDefinition == null) {
			return null;
		}

		return ObjectFieldLocalServiceUtil.fetchObjectField(
			objectDefinition.getObjectDefinitionId(), name);
	}

	public static String getFilterSubfield(
		Locale locale, ObjectField objectField) {

		String subfieldSuffix = _getTypedSubfieldSuffix(objectField);

		if (subfieldSuffix != null) {
			return "nestedFieldArray." + subfieldSuffix;
		}

		if (objectField.isLocalized()) {
			return "nestedFieldArray." +
				Field.getLocalizedName(locale, "value");
		}

		String indexedLanguageId = objectField.getIndexedLanguageId();

		if (Validator.isNotNull(indexedLanguageId)) {
			return "nestedFieldArray.value_" + indexedLanguageId;
		}

		return "nestedFieldArray.value_text";
	}

	public static String getSortSubfield(ObjectField objectField) {
		String subfieldSuffix = _getTypedSubfieldSuffix(objectField);

		if (subfieldSuffix == null) {
			subfieldSuffix = "value_keyword_lowercase";
		}

		return StringBundler.concat(
			"nestedFieldArray.", objectField.getName(), StringPool.PERIOD,
			subfieldSuffix);
	}

	private static String _getTypedSubfieldSuffix(ObjectField objectField) {
		if (objectField.isIndexedAsKeyword()) {
			return "value_keyword";
		}

		String dbType = objectField.getDBType();

		if (ObjectFieldConstants.DB_TYPE_BIG_DECIMAL.equals(dbType) ||
			ObjectFieldConstants.DB_TYPE_DOUBLE.equals(dbType)) {

			return "value_double";
		}

		if (ObjectFieldConstants.DB_TYPE_BOOLEAN.equals(dbType)) {
			return "value_boolean";
		}

		if (ObjectFieldConstants.DB_TYPE_DATE.equals(dbType) ||
			ObjectFieldConstants.DB_TYPE_DATE_TIME.equals(dbType)) {

			return "value_date";
		}

		if (ObjectFieldConstants.DB_TYPE_INTEGER.equals(dbType)) {
			return "value_integer";
		}

		if (ObjectFieldConstants.DB_TYPE_LONG.equals(dbType)) {
			return "value_long";
		}

		return null;
	}

}