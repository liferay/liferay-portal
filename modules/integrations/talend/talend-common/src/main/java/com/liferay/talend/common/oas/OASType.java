/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend.common.oas;

/**
 * @author Igor Beslic
 */
public enum OASType {

	ARRAY("array"), BOOLEAN("boolean"), INTEGER("integer"), NUMBER("number"),
	OBJECT("object"), STRING("string");

	public static OASType fromDefinition(String oasTypeDefinition) {
		for (OASType oasType : values()) {
			if (oasTypeDefinition.equals(oasType._oasTypeDefinition)) {
				return oasType;
			}
		}

		throw new OASException(
			"Unknown OpenAPI specification type " + oasTypeDefinition);
	}

	private OASType(String oasTypeDefinition) {
		_oasTypeDefinition = oasTypeDefinition;
	}

	private final String _oasTypeDefinition;

}