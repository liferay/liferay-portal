/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend.common.oas;

/**
 * @author Igor Beslic
 */
public enum OASFormat {

	BIGDECIMAL("bigdecimal", OASType.NUMBER, true),
	BINARY("binary", OASType.STRING, false),
	BOOLEAN("boolean", OASType.BOOLEAN, true),
	BYTE("byte", OASType.STRING, false), DATE("date", OASType.STRING, false),
	DATE_TIME("date-time", OASType.STRING, false),
	DICTIONARY("string", OASType.OBJECT, true),
	DOUBLE("double", OASType.NUMBER, false),
	FLOAT("float", OASType.NUMBER, true), INT32("int32", OASType.INTEGER, true),
	INT64("int64", OASType.INTEGER, false), STRING(null, OASType.STRING, true);

	public static OASFormat fromOpenAPITypeAndFormat(
		OASType oasType, String openAPIFormatDefinition) {

		OASFormat defaultOASFormat = null;

		for (OASFormat oasFormat : values()) {
			if (oasType != oasFormat._oasType) {
				continue;
			}

			if ((openAPIFormatDefinition == null) && oasFormat._defaultFormat) {
				return oasFormat;
			}

			if ((openAPIFormatDefinition != null) &&
				openAPIFormatDefinition.equals(
					oasFormat._openAPIFormatDefinition)) {

				return oasFormat;
			}

			if (oasFormat._defaultFormat) {
				defaultOASFormat = oasFormat;
			}
		}

		return defaultOASFormat;
	}

	private OASFormat(
		String openAPIFormatDefinition, OASType oasType,
		boolean defaultFormat) {

		_openAPIFormatDefinition = openAPIFormatDefinition;
		_oasType = oasType;
		_defaultFormat = defaultFormat;
	}

	private final boolean _defaultFormat;
	private final OASType _oasType;
	private final String _openAPIFormatDefinition;

}