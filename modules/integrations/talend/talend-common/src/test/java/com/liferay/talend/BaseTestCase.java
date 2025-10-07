/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend;

import com.liferay.talend.common.schema.SchemaBuilder;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.apache.avro.Schema;

/**
 * @author Igor Beslic
 */
public abstract class BaseTestCase {

	public JsonObject readObject(String fileName) {
		Class<BaseTestCase> baseTestClass = BaseTestCase.class;

		JsonReader jsonReader = Json.createReader(
			baseTestClass.getResourceAsStream(fileName));

		return jsonReader.readObject();
	}

	protected Schema getSchema(
		String endpoint, String operation, JsonObject oasJsonObject) {

		return _schemaBuilder.inferSchema(endpoint, operation, oasJsonObject);
	}

	private final SchemaBuilder _schemaBuilder = new SchemaBuilder();

}