/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend.runtime;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

/**
 * @author Igor Beslic
 */
public class LiferayRequestContentAggregatorSink extends LiferaySink {

	@Override
	public JsonObject doPatchRequest(String resourceURL, JsonValue jsonValue) {
		return _processRequest(resourceURL, jsonValue);
	}

	@Override
	public JsonObject doPostRequest(String resourceURL, JsonValue jsonValue) {
		return _processRequest(resourceURL, jsonValue);
	}

	public JsonValue getOutputJsonValue() {
		return _outputJsonValue;
	}

	public String getOutputResourceURL() {
		return _outputResourceURL;
	}

	private JsonObject _processRequest(
		String resourceURL, JsonValue jsonValue) {

		_outputResourceURL = resourceURL;
		_outputJsonValue = jsonValue;

		JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();

		if (jsonValue instanceof JsonObject) {
			jsonObjectBuilder = Json.createObjectBuilder(
				jsonValue.asJsonObject());
		}
		else {
			JsonArray jsonArray = jsonValue.asJsonArray();

			JsonValue jsonArrayJsonValue = jsonArray.get(0);

			jsonObjectBuilder = Json.createObjectBuilder(
				jsonArrayJsonValue.asJsonObject());

			jsonObjectBuilder.add("iterable", jsonValue);
		}

		jsonObjectBuilder.add("success", "true");

		return jsonObjectBuilder.build();
	}

	private JsonValue _outputJsonValue;
	private String _outputResourceURL;

}