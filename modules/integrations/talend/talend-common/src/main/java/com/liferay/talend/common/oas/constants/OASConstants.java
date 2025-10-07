/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend.common.oas.constants;

/**
 * @author Zoltán Takács
 * @author Igor Beslic
 */
public class OASConstants {

	public static final String ADDITIONAL_PROPERTIES = "additionalProperties";

	public static final String FORMAT = "format";

	public static final String INFO = "info";

	public static final String ITEMS = "items";

	public static final String LOCATOR_COMPONENTS_SCHEMAS =
		"components>schemas";

	public static final String LOCATOR_COMPONENTS_SCHEMAS_CLASS_NAME_PATTERN =
		LOCATOR_COMPONENTS_SCHEMAS +
			">SCHEMA_TPL>properties>x-class-name>default";

	public static final String LOCATOR_COMPONENTS_SCHEMAS_PATTERN =
		LOCATOR_COMPONENTS_SCHEMAS + ">SCHEMA_TPL";

	public static final String LOCATOR_ENDPOINT_OPERATION_PARAMETERS_PATTERN =
		"paths>ENDPOINT_TPL>OPERATION_TPL>parameters";

	public static final String LOCATOR_PATHS_PATTERN = "paths>ENDPOINT_TPL";

	public static final String LOCATOR_PROPERTIES_ITEMS_ITEMS =
		"properties>items>items";

	public static final String
		LOCATOR_REQUEST_BODY_CONTENT_APPLICATION_JSON_SCHEMA_PATTERN =
			"paths>ENDPOINT_TPL>OPERATION_TPL>requestBody>content>" +
				"application/json>schema";

	public static final String LOCATOR_RESPONSE_SCHEMA_ITEMS_REFERENCE =
		"responses>default>content>application/json>schema>items>$ref";

	public static final String LOCATOR_RESPONSE_SCHEMA_REFERENCE =
		"responses>default>content>application/json>schema>$ref";

	public static final String
		LOCATOR_RESPONSES_CONTENT_APPLICATION_JSON_SCHEMA_PATTERN =
			"paths>ENDPOINT_TPL>OPERATION_TPL>responses>default>content>" +
				"application/json>schema";

	public static final String OPERATION_DELETE = "delete";

	public static final String OPERATION_GET = "get";

	public static final String OPERATION_PATCH = "patch";

	public static final String OPERATION_POST = "post";

	public static final String OPERATION_PUT = "put";

	public static final String PATH_SCHEMA_REFERENCE = "#/components/schemas/";

	public static final String PATHS = "paths";

	public static final String PROPERTIES = "properties";

	public static final String REF = "$ref";

	public static final String REQUIRED = "required";

	public static final String TYPE = "type";

	public static final String VERSION = "version";

	private OASConstants() {
	}

}