/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mule.internal.oas.constants;

/**
 * @author Igor Beslic
 * @author Matija Petanjek
 */
public class OASConstants {

	public static final String ADDITIONAL_PROPERTIES = "additionalProperties";

	public static final String ARRAY = "array";

	public static final String FORMAT = "format";

	public static final String ITEMS = "items";

	public static final String OBJECT = "object";

	public static final String OPERATION_DELETE = "delete";

	public static final String OPERATION_GET = "get";

	public static final String OPERATION_PATCH = "patch";

	public static final String OPERATION_POST = "post";

	public static final String PATH_COMPONENTS_SCHEMAS = "components>schemas";

	public static final String PATH_COMPONENTS_SCHEMAS_PATTERN =
		"components>schemas>SCHEMA_TPL";

	public static final String PATH_ITEMS_ITEMS_REF = "items>items>$ref";

	public static final String PATH_ITEMS_REF = "items>$ref";

	public static final String PATH_PROPERTIES_X_CLASS_NAME_DEFAULT =
		"properties>x-class-name>default";

	public static final String
		PATH_REQUEST_BODY_CONTENT_APPLICATION_JSON_SCHEMA_PATTERN =
			"paths>ENDPOINT_TPL>OPERATION_TPL>requestBody>content>" +
				"application/json>schema>$ref";

	public static final String
		PATH_RESPONSES_DEFAULT_CONTENT_APPLICATION_JSON_SCHEMA_PATTERN =
			"paths>ENDPOINT_TPL>OPERATION_TPL>responses>default>content>" +
				"application/json>schema>$ref";

	public static final String PATH_SCHEMA_REFERENCE = "#/components/schemas/";

	public static final String PATHS = "paths";

	public static final String PROPERTIES = "properties";

	public static final String REF = "$ref";

	public static final String REQUIRED = "required";

	public static final String TYPE = "type";

	private OASConstants() {
	}

}