/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend.avro;

import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.IndexedRecord;

/**
 * @author Igor Beslic
 */
public class OASDictionaryConverter {

	public OASDictionaryConverter(Schema schema) {
		_schema = schema;
	}

	public IndexedRecord toIndexedRecord(JsonObject contentJsonObject) {
		IndexedRecord record = new GenericData.Record(_schema);

		contentJsonObject.forEach(
			(entryKey, entryValue) -> {
				record.put(0, entryKey);
				record.put(1, _asText(entryValue));
			});

		return record;
	}

	private String _asText(JsonValue jsonValue) {
		JsonString jsonString = (JsonString)jsonValue;

		return jsonString.getString();
	}

	private final Schema _schema;

}