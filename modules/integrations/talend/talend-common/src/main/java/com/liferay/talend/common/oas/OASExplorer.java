/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend.common.oas;

import com.liferay.talend.common.json.JsonFinder;
import com.liferay.talend.common.oas.constants.OASConstants;
import com.liferay.talend.common.util.StringUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;

/**
 * @author Igor Beslic
 */
public class OASExplorer {

	public String getEntityClassName(String name, JsonObject oasJsonObject) {
		String jsonFinderPath = StringUtil.replace(
			OASConstants.LOCATOR_COMPONENTS_SCHEMAS_CLASS_NAME_PATTERN,
			"SCHEMA_TPL", name);

		if (!_jsonFinder.hasJsonObject(jsonFinderPath, oasJsonObject)) {
			return null;
		}

		JsonValue classNameJsonValue = _jsonFinder.getDescendantJsonValue(
			jsonFinderPath, oasJsonObject);

		if (classNameJsonValue.getValueType() != JsonValue.ValueType.STRING) {
			return null;
		}

		JsonString classNameJsonString = (JsonString)classNameJsonValue;

		return classNameJsonString.getString();
	}

	public Set<String> getEntitySchemaNames(JsonObject oasJsonObject) {
		Set<String> entitySchemaNames = new HashSet<>();

		JsonObject componentSchemaJsonObject =
			_jsonFinder.getDescendantJsonObject(
				OASConstants.LOCATOR_COMPONENTS_SCHEMAS, oasJsonObject);

		componentSchemaJsonObject.forEach(
			(entityName, entityDefinition) -> {
				if (entityName.startsWith("Page")) {
					return;
				}

				entitySchemaNames.add(entityName);
			});

		return entitySchemaNames;
	}

	public Set<String> getOperationPaths(
		JsonObject oasJsonObject, String... operations) {

		if (operations.length == 0) {
			return _extractPaths(oasJsonObject, null);
		}

		Set<String> paths = new HashSet<>();

		for (String operation : operations) {
			paths.addAll(_extractPaths(oasJsonObject, operation));
		}

		return paths;
	}

	public List<OASParameter> getPathOperationOASParameters(
		String path, String operation, JsonObject oasJsonObject) {

		List<OASParameter> oasParameters = new ArrayList<>();

		String jsonFinderPath = StringUtil.replace(
			OASConstants.LOCATOR_ENDPOINT_OPERATION_PARAMETERS_PATTERN,
			"ENDPOINT_TPL", path, "OPERATION_TPL", operation);

		JsonArray parametersJsonArray = _jsonFinder.getDescendantJsonArray(
			jsonFinderPath, oasJsonObject);

		for (int i = 0; i < parametersJsonArray.size(); i++) {
			oasParameters.add(
				_toParameter(parametersJsonArray.getJsonObject(i)));
		}

		return oasParameters;
	}

	public Set<String> getPathOperations(
		String path, JsonObject oasJsonObject) {

		String locatorExpression = StringUtil.replace(
			OASConstants.LOCATOR_PATHS_PATTERN, "ENDPOINT_TPL", path);

		JsonObject pathMethodsJsonObject = _jsonFinder.getDescendantJsonObject(
			locatorExpression, oasJsonObject);

		return pathMethodsJsonObject.keySet();
	}

	public String getVersion(JsonObject oasJsonObject) {
		JsonObject infoVersionJsonObject = _jsonFinder.getDescendantJsonObject(
			OASConstants.INFO, oasJsonObject);

		return infoVersionJsonObject.getString(OASConstants.VERSION);
	}

	private Set<String> _extractPaths(
		JsonObject oasJsonObject, String httpMethod) {

		Set<String> paths = new TreeSet<>();

		JsonObject pathsJsonObject = oasJsonObject.getJsonObject(
			OASConstants.PATHS);

		pathsJsonObject.forEach(
			(path, pathOperationsJsonValue) -> {
				JsonObject pathOperationsJsonObject =
					pathOperationsJsonValue.asJsonObject();

				pathOperationsJsonObject.forEach(
					(operation, operationJsonValue) -> {
						if (httpMethod != null) {
							if (!Objects.equals(httpMethod, operation)) {
								return;
							}

							if (!Objects.equals(
									httpMethod, OASConstants.OPERATION_GET)) {

								paths.add(path);

								return;
							}
						}

						if (_jsonFinder.hasJsonObject(
								OASConstants.LOCATOR_RESPONSE_SCHEMA_REFERENCE,
								operationJsonValue.asJsonObject()) ||
							_jsonFinder.hasJsonObject(
								OASConstants.
									LOCATOR_RESPONSE_SCHEMA_ITEMS_REFERENCE,
								operationJsonValue.asJsonObject())) {

							paths.add(path);
						}
					});
			});

		return paths;
	}

	private OASParameter _toParameter(JsonObject jsonObject) {
		OASParameter oasParameter = new OASParameter(
			jsonObject.getString("name"), jsonObject.getString("in"));

		if (jsonObject.containsKey("required")) {
			oasParameter.setRequired(jsonObject.getBoolean("required"));
		}

		return oasParameter;
	}

	private final JsonFinder _jsonFinder = new JsonFinder();

}