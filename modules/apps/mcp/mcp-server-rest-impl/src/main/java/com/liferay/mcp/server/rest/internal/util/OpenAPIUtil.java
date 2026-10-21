/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.util;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;

import com.liferay.mcp.server.rest.dto.v1_0.Tool;
import com.liferay.mcp.server.rest.dto.v1_0.ToolSummary;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.odata.filter.InvalidFilterException;
import com.liferay.portal.odata.sort.InvalidSortException;
import com.liferay.portal.vulcan.http.VulcanRequestForwarder;
import com.liferay.portal.vulcan.jackson.databind.ObjectMapperProviderUtil;

import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.util.EntityUtils;

/**
 * @author Alejandro Tardín
 */
public class OpenAPIUtil {

	public static final String[] METHODS = {
		"delete", "get", "head", "options", "patch", "post", "put"
	};

	public static String getLastPathParameter(String path) {
		String[] pathParts = StringUtil.split(path, CharPool.SLASH);

		for (int i = pathParts.length - 1; i >= 0; i--) {
			String pathParameter = getPathParameter(pathParts[i]);

			if (pathParameter != null) {
				return pathParameter;
			}
		}

		return null;
	}

	public static Map<String, ?> getOutputSchema(
		JSONObject openAPIJSONObject, String toolName) {

		Operation operation = _getOperation(openAPIJSONObject, toolName);

		JSONObject responseSchemaJSONObject = getResponseSchemaJSONObject(
			operation._operationJSONObject);

		if (responseSchemaJSONObject == null) {
			return null;
		}

		return (Map<String, Object>)_getSchemaObject(
			"writeOnly", openAPIJSONObject, responseSchemaJSONObject,
			new HashSet<>());
	}

	public static String getPathParameter(String pathPart) {
		if (Validator.isNull(pathPart)) {
			return null;
		}

		if ((pathPart.length() > 2) &&
			(pathPart.charAt(0) == CharPool.OPEN_CURLY_BRACE) &&
			(pathPart.charAt(pathPart.length() - 1) ==
				CharPool.CLOSE_CURLY_BRACE)) {

			return pathPart.substring(1, pathPart.length() - 1);
		}

		return null;
	}

	public static Map<String, JSONObject> getPropertiesJSONObjects(
		JSONObject openAPIJSONObject, JSONObject schemaJSONObject) {

		return _getPropertiesJSONObjects(
			false, openAPIJSONObject, false, schemaJSONObject, new HashSet<>());
	}

	public static VulcanRequestForwarder.Request getRequest(
			String basePath, Map<String, String> headers,
			JSONObject inputJSONObject, JSONObject openAPIJSONObject,
			String restrictFields, String toolName, User user)
		throws Exception {

		byte[] body;
		String contentType;

		Operation operation = _getOperation(openAPIJSONObject, toolName);

		_validateQueryParameters(inputJSONObject, operation, restrictFields);

		if (_isMultipartRequest(operation._operationJSONObject)) {
			HttpEntity httpEntity = _getMultipartHttpEntity(
				inputJSONObject, openAPIJSONObject, operation);

			body = EntityUtils.toByteArray(httpEntity);

			Header contentTypeHeader = httpEntity.getContentType();

			contentType = contentTypeHeader.getValue();
		}
		else if (operation._operationJSONObject.has("requestBody")) {
			if (!inputJSONObject.has("body")) {
				throw new IllegalArgumentException(
					StringBundler.concat(
						"The \"", toolName,
						"\" tool requires the request payload nested under a ",
						"\"body\" property. Pass any path or query parameters ",
						"as siblings of \"body\" rather than flattening the ",
						"payload into the input map."));
			}

			String bodyString = StringPool.BLANK;

			Object bodyObject = inputJSONObject.get("body");

			if (bodyObject instanceof JSONObject) {
				bodyString = bodyObject.toString();
			}
			else if (bodyObject != null) {
				bodyString = String.valueOf(bodyObject);
			}

			body = bodyString.getBytes(StandardCharsets.UTF_8);

			contentType = ContentTypes.APPLICATION_JSON;
		}
		else {
			body = null;
			contentType = null;
		}

		return new VulcanRequestForwarder.Request() {

			@Override
			public byte[] getBody() {
				return body;
			}

			@Override
			public String getContentType() {
				return contentType;
			}

			@Override
			public Map<String, String> getHeaders() {
				return headers;
			}

			@Override
			public String getMethod() {
				return StringUtil.toUpperCase(operation._method);
			}

			@Override
			public String getPath() {
				String path = basePath + _getPath(inputJSONObject, operation);

				String queryString = _getQueryString(
					inputJSONObject, openAPIJSONObject, operation,
					restrictFields);

				if (queryString.isEmpty()) {
					return path;
				}

				return path + StringPool.QUESTION + queryString;
			}

			@Override
			public User getUser() {
				return user;
			}

		};
	}

	public static JSONObject getRequestBodySchemaJSONObject(
		JSONObject openAPIJSONObject, JSONObject operationJSONObject) {

		JSONObject contentJSONObject = JSONUtil.getValueAsJSONObject(
			operationJSONObject, "JSONObject/requestBody",
			"JSONObject/content");

		if (contentJSONObject == null) {
			return null;
		}

		JSONObject mediaTypeJSONObject = _getMediaTypeJSONObject(
			contentJSONObject);

		if (mediaTypeJSONObject == null) {
			return null;
		}

		JSONObject schemaJSONObject = mediaTypeJSONObject.getJSONObject(
			"schema");

		if ((schemaJSONObject == null) || !schemaJSONObject.has("$ref")) {
			return schemaJSONObject;
		}

		return _getRefJSONObject(
			openAPIJSONObject, schemaJSONObject.getString("$ref"));
	}

	public static Set<String> getRequiredPropertyNames(
		JSONObject openAPIJSONObject, JSONObject schemaJSONObject) {

		Map<String, JSONObject> propertiesJSONObjects =
			_getPropertiesJSONObjects(
				false, openAPIJSONObject, true, schemaJSONObject,
				new HashSet<>());

		return propertiesJSONObjects.keySet();
	}

	public static JSONObject getResponseSchemaJSONObject(
		JSONObject operationJSONObject) {

		JSONObject responsesJSONObject = operationJSONObject.getJSONObject(
			"responses");

		if (responsesJSONObject == null) {
			return null;
		}

		JSONObject responseJSONObject = null;

		for (String code : responsesJSONObject.keySet()) {
			if (code.startsWith("2")) {
				responseJSONObject = responsesJSONObject.getJSONObject(code);

				break;
			}
		}

		if (responseJSONObject == null) {
			responseJSONObject = responsesJSONObject.getJSONObject("default");
		}

		if (responseJSONObject == null) {
			return null;
		}

		JSONObject contentJSONObject = responseJSONObject.getJSONObject(
			"content");

		if (contentJSONObject == null) {
			return null;
		}

		JSONObject mediaTypeJSONObject = contentJSONObject.getJSONObject(
			"application/json");

		if (mediaTypeJSONObject == null) {
			return null;
		}

		return mediaTypeJSONObject.getJSONObject("schema");
	}

	public static Tool getTool(
		boolean injectVulcanParameters, JSONObject openAPIJSONObject,
		boolean requiredInputSchemaOnly, String restrictFields,
		String toolName) {

		Operation operation = _getOperation(openAPIJSONObject, toolName);

		return new Tool() {
			{
				setDescription(
					() -> _getDescription(
						operation._method, operation._operationJSONObject,
						operation._path));
				setInputSchema(
					() -> {
						Map<String, Object> inputSchema = _getInputSchema(
							injectVulcanParameters, operation._method,
							openAPIJSONObject, operation._operationJSONObject,
							operation._pathParametersJSONArray, restrictFields);

						if (!requiredInputSchemaOnly) {
							return inputSchema;
						}

						return _toRequiredInputSchema(
							inputSchema,
							_isObjectEntrySchema(
								getRequestBodySchemaJSONObject(
									openAPIJSONObject,
									operation._operationJSONObject)));
					});

				setName(() -> toolName);
			}
		};
	}

	public static List<ToolSummary> getToolSummaries(
		JSONObject openAPIJSONObject) {

		JSONObject pathsJSONObject = openAPIJSONObject.getJSONObject("paths");

		if (pathsJSONObject == null) {
			throw new IllegalArgumentException(
				"OpenAPI document has no \"paths\" object");
		}

		List<ToolSummary> toolSummaries = new ArrayList<>();

		for (String path : pathsJSONObject.keySet()) {
			JSONObject pathItemJSONObject = pathsJSONObject.getJSONObject(path);

			for (String method : METHODS) {
				JSONObject operationJSONObject =
					pathItemJSONObject.getJSONObject(method);

				if (operationJSONObject == null) {
					continue;
				}

				toolSummaries.add(
					new ToolSummary() {
						{
							setDescription(
								() -> _getDescription(
									method, operationJSONObject, path));
							setName(
								() -> operationJSONObject.getString(
									"operationId"));
						}
					});
			}
		}

		return toolSummaries;
	}

	public static boolean isCollectionSchema(
		JSONObject openAPIJSONObject, JSONObject schemaJSONObject) {

		if (schemaJSONObject == null) {
			return false;
		}

		if (schemaJSONObject.has("$ref")) {
			return isCollectionSchema(
				openAPIJSONObject,
				_getRefJSONObject(
					openAPIJSONObject, schemaJSONObject.getString("$ref")));
		}

		if (Objects.equals(schemaJSONObject.getString("type"), "array") ||
			(_getItemsArrayJSONObject(schemaJSONObject) != null)) {

			return true;
		}

		return false;
	}

	public static boolean isNestedSchema(JSONObject propertyJSONObject) {
		if (propertyJSONObject == null) {
			return false;
		}

		if (Validator.isNotNull(propertyJSONObject.getString("$ref"))) {
			return true;
		}

		JSONObject additionalPropertiesJSONObject =
			propertyJSONObject.getJSONObject("additionalProperties");

		if ((additionalPropertiesJSONObject != null) &&
			Validator.isNotNull(
				additionalPropertiesJSONObject.getString("$ref"))) {

			return true;
		}

		JSONObject itemsJSONObject = propertyJSONObject.getJSONObject("items");

		if ((itemsJSONObject != null) &&
			Validator.isNotNull(itemsJSONObject.getString("$ref"))) {

			return true;
		}

		return false;
	}

	public static boolean isPathParameter(String value) {
		if (getPathParameter(value) != null) {
			return true;
		}

		return false;
	}

	private static String _addLocalizedRestrictFields(
		JSONObject openAPIJSONObject, JSONObject operationJSONObject,
		String restrictFields) {

		if (Validator.isNull(restrictFields)) {
			return restrictFields;
		}

		Set<String> restrictFieldNames = new LinkedHashSet<>();

		Object schemaObject = _getSchemaObject(
			"writeOnly", openAPIJSONObject,
			getResponseSchemaJSONObject(operationJSONObject), new HashSet<>());

		for (String restrictFieldName : StringUtil.split(restrictFields)) {
			restrictFieldNames.add(restrictFieldName);

			if (restrictFieldName.endsWith("_i18n")) {
				continue;
			}

			String localizedRestrictFieldName = restrictFieldName + "_i18n";

			if (!_hasSchemaProperty(localizedRestrictFieldName, schemaObject)) {
				continue;
			}

			restrictFieldNames.add(localizedRestrictFieldName);
		}

		return StringUtil.merge(restrictFieldNames);
	}

	private static void _addMultipartParts(
		JSONObject openAPIJSONObject, JSONObject operationJSONObject,
		Map<String, Object> properties, List<String> requiredPropertyNames) {

		Map<String, Object> bodySchemaMap =
			(Map<String, Object>)_getSchemaObject(
				"readOnly", openAPIJSONObject,
				_getBodySchemaJSONObject(operationJSONObject), new HashSet<>());

		Map<String, Object> bodyProperties =
			(Map<String, Object>)bodySchemaMap.get("properties");

		if (bodyProperties == null) {
			return;
		}

		for (Map.Entry<String, Object> entry : bodyProperties.entrySet()) {
			Map<String, Object> propertySchemaMap =
				(Map<String, Object>)entry.getValue();

			properties.put(
				entry.getKey(),
				_isBinary(propertySchemaMap) ?
					_getBinarySchemaMap(propertySchemaMap) : propertySchemaMap);
		}

		requiredPropertyNames.addAll(
			TransformUtil.transform(
				(List<Object>)bodySchemaMap.get("required"), String::valueOf));
	}

	private static void _addParameter(
		JSONObject openAPIJSONObject, JSONObject parameterJSONObject,
		Map<String, Object> properties, List<String> requiredPropertyNames,
		Collection<String> responseFieldNames,
		Set<String> visitedParameterNames) {

		String name = parameterJSONObject.getString("name");

		if (!visitedParameterNames.add(name)) {
			return;
		}

		Map<String, Object> parameterSchemaMap;

		if (Objects.equals(name, "fields")) {
			parameterSchemaMap = _getParameterSchemaMap(
				_DESCRIPTION, responseFieldNames);
		}
		else {
			parameterSchemaMap = (Map<String, Object>)_getSchemaObject(
				"readOnly", openAPIJSONObject,
				parameterJSONObject.getJSONObject("schema"), new HashSet<>());

			if (parameterJSONObject.has("description")) {
				parameterSchemaMap.put(
					"description",
					parameterJSONObject.getString("description"));
			}
		}

		properties.put(name, parameterSchemaMap);

		if (Objects.equals(parameterJSONObject.getString("in"), "path") ||
			parameterJSONObject.getBoolean("required")) {

			requiredPropertyNames.add(name);
		}
	}

	private static void _addParameters(
		JSONObject openAPIJSONObject, JSONArray parametersJSONArray,
		Map<String, Object> properties, List<String> requiredPropertyNames,
		Collection<String> responseFieldNames,
		Set<String> visitedParameterNames) {

		if (parametersJSONArray == null) {
			return;
		}

		for (int i = 0; i < parametersJSONArray.length(); i++) {
			_addParameter(
				openAPIJSONObject, parametersJSONArray.getJSONObject(i),
				properties, requiredPropertyNames, responseFieldNames,
				visitedParameterNames);
		}
	}

	private static void _appendQueryParameter(
		String name, StringBundler sb, Object value) {

		if (value == null) {
			return;
		}

		String stringValue = String.valueOf(value);

		if (stringValue.isEmpty()) {
			return;
		}

		if (sb.length() > 0) {
			sb.append(CharPool.AMPERSAND);
		}

		sb.append(URLCodec.encodeURL(name));
		sb.append(CharPool.EQUAL);
		sb.append(URLCodec.encodeURL(stringValue));
	}

	private static Map<String, Object> _collapseNestedProperties(
		Map<String, Object> schema) {

		Map<String, Object> properties = (Map<String, Object>)schema.get(
			"properties");

		if (properties == null) {
			return schema;
		}

		Map<String, Object> collapsedProperties = new LinkedHashMap<>();

		for (Map.Entry<String, Object> entry : properties.entrySet()) {
			Object value = entry.getValue();

			if ((value instanceof Map<?, ?> valueMap) &&
				(valueMap.containsKey("items") ||
				 valueMap.containsKey("properties"))) {

				collapsedProperties.put(
					entry.getKey(),
					HashMapBuilder.<String, Object>put(
						"type", valueMap.get("type")
					).build());
			}
			else {
				collapsedProperties.put(entry.getKey(), value);
			}
		}

		return HashMapBuilder.<String, Object>putAll(
			schema
		).put(
			"properties", collapsedProperties
		).build();
	}

	private static void _filterProperties(
		String excludedPropertyAttributeName, Map<String, Object> schemaMap) {

		Object propertiesObject = schemaMap.get("properties");

		if (!(propertiesObject instanceof Map)) {
			return;
		}

		Map<String, Object> properties = (Map<String, Object>)propertiesObject;

		Set<String> excludedPropertyNames = new HashSet<>();

		for (Map.Entry<String, Object> entry : properties.entrySet()) {
			Object value = entry.getValue();

			if (!(value instanceof Map)) {
				continue;
			}

			Map<?, ?> propertyMap = (Map<?, ?>)value;

			if (!GetterUtil.getBoolean(
					propertyMap.get(excludedPropertyAttributeName))) {

				continue;
			}

			excludedPropertyNames.add(entry.getKey());
		}

		if (excludedPropertyNames.isEmpty()) {
			return;
		}

		excludedPropertyNames.forEach(properties::remove);

		List<Object> required = (List<Object>)schemaMap.get("required");

		if (required == null) {
			return;
		}

		schemaMap.put(
			"required",
			ListUtil.filter(
				TransformUtil.transform(required, String::valueOf),
				Predicate.not(excludedPropertyNames::contains)));
	}

	private static Map<String, Object> _getAllOfSchemaMap(
		String excludedPropertyAttributeName, JSONObject jsonObject,
		JSONObject openAPIJSONObject, Set<String> visitedRefs) {

		Map<String, Object> allOfSchemaMap = new LinkedHashMap<>();

		Map<String, Object> properties = new LinkedHashMap<>();
		Set<String> requiredPropertyNames = new HashSet<>();

		for (String key : jsonObject.keySet()) {
			if (key.equals("allOf") || _excludedSchemaKeys.contains(key) ||
				key.startsWith("x-")) {

				continue;
			}

			allOfSchemaMap.put(
				key,
				_getSchemaObject(
					excludedPropertyAttributeName, openAPIJSONObject,
					jsonObject.get(key), visitedRefs));
		}

		JSONArray allOfJSONArray = jsonObject.getJSONArray("allOf");

		for (int i = 0; i < allOfJSONArray.length(); i++) {
			Object schemaObject = _getSchemaObject(
				excludedPropertyAttributeName, openAPIJSONObject,
				allOfJSONArray.getJSONObject(i), visitedRefs);

			if (!(schemaObject instanceof Map)) {
				continue;
			}

			Map<String, Object> schemaMap = (Map<String, Object>)schemaObject;

			Map<String, Object> schemaProperties =
				(Map<String, Object>)schemaMap.get("properties");

			if (schemaProperties != null) {
				properties.putAll(schemaProperties);
			}

			requiredPropertyNames.addAll(
				TransformUtil.transform(
					(List<Object>)schemaMap.get("required"), String::valueOf));

			for (Map.Entry<String, Object> entry : schemaMap.entrySet()) {
				String key = entry.getKey();

				if (allOfSchemaMap.containsKey(key) ||
					key.equals("properties") || key.equals("required")) {

					continue;
				}

				allOfSchemaMap.put(key, entry.getValue());
			}
		}

		if (!properties.isEmpty()) {
			allOfSchemaMap.put("properties", properties);
		}

		if (!requiredPropertyNames.isEmpty()) {
			allOfSchemaMap.put("required", List.copyOf(requiredPropertyNames));
		}

		allOfSchemaMap.putIfAbsent("type", "object");

		_filterProperties(excludedPropertyAttributeName, allOfSchemaMap);

		return allOfSchemaMap;
	}

	private static Map<String, Object> _getBinarySchemaMap(
		Map<String, Object> schemaMap) {

		return HashMapBuilder.<String, Object>put(
			"description",
			() -> {
				String extraDescription =
					"Provide as an object with `data` (base64-encoded bytes)" +
						", and optional `filename` and `contentType`.";

				String description = (String)schemaMap.get("description");

				if (Validator.isNotNull(description)) {
					return description + ". " + extraDescription;
				}

				return extraDescription;
			}
		).put(
			"properties",
			HashMapBuilder.<String, Object>put(
				"contentType",
				HashMapBuilder.<String, Object>put(
					"description", "MIME type of the file content."
				).put(
					"type", "string"
				).build()
			).put(
				"data",
				HashMapBuilder.<String, Object>put(
					"description", "Base64-encoded file bytes."
				).put(
					"type", "string"
				).build()
			).put(
				"filename",
				HashMapBuilder.<String, Object>put(
					"description", "Original file name."
				).put(
					"type", "string"
				).build()
			).build()
		).put(
			"required", Arrays.asList("data")
		).put(
			"type", "object"
		).build();
	}

	private static JSONObject _getBodySchemaJSONObject(
		JSONObject operationJSONObject) {

		JSONObject requestBodyJSONObject = operationJSONObject.getJSONObject(
			"requestBody");

		JSONObject contentJSONObject = requestBodyJSONObject.getJSONObject(
			"content");

		if (contentJSONObject == null) {
			throw new IllegalArgumentException(
				"Request body has no \"content\"");
		}

		JSONObject mediaTypeJSONObject = _getMediaTypeJSONObject(
			contentJSONObject);

		if (mediaTypeJSONObject == null) {
			throw new IllegalArgumentException("Request body has no content");
		}

		JSONObject bodySchemaJSONObject = mediaTypeJSONObject.getJSONObject(
			"schema");

		if (bodySchemaJSONObject == null) {
			throw new IllegalArgumentException(
				"Request body content has no \"schema\"");
		}

		return bodySchemaJSONObject;
	}

	private static String _getDescription(
		String method, JSONObject operationJSONObject, String path) {

		boolean hasDescription = operationJSONObject.has("description");
		boolean hasSummary = operationJSONObject.has("summary");

		if (hasDescription && hasSummary) {
			return operationJSONObject.getString("summary") + ". " +
				operationJSONObject.getString("description");
		}

		if (hasDescription) {
			return operationJSONObject.getString("description");
		}

		if (hasSummary) {
			return operationJSONObject.getString("summary");
		}

		return StringUtil.toUpperCase(method) + StringPool.SPACE + path;
	}

	private static Set<String> _getFieldNames(Object value) {
		if (value instanceof JSONArray jsonArray) {
			return new LinkedHashSet<>(JSONUtil.toStringList(jsonArray));
		}

		return new LinkedHashSet<>(
			Arrays.asList(StringUtil.split((String)value)));
	}

	private static Set<String> _getFilterFieldPaths(String filterString) {
		if (Validator.isNull(filterString)) {
			return Collections.emptySet();
		}

		Set<String> fieldPaths = new LinkedHashSet<>();

		Matcher matcher = _fieldPathPattern.matcher(
			_getMaskedFilterString(filterString));

		while (matcher.find()) {
			fieldPaths.add(matcher.group());
		}

		return fieldPaths;
	}

	private static Map<String, Object> _getInputSchema(
		boolean injectVulcanParameters, String method,
		JSONObject openAPIJSONObject, JSONObject operationJSONObject,
		JSONArray pathParametersJSONArray, String restrictFields) {

		Map<String, Object> properties = new LinkedHashMap<>();
		List<String> requiredPropertyNames = new ArrayList<>();

		if (operationJSONObject.has("requestBody")) {
			if (_isMultipartRequest(operationJSONObject)) {
				_addMultipartParts(
					openAPIJSONObject, operationJSONObject, properties,
					requiredPropertyNames);
			}
			else {
				JSONObject requestBodyJSONObject =
					operationJSONObject.getJSONObject("requestBody");

				Map<String, Object> bodySchemaMap = null;

				if (requestBodyJSONObject.getJSONObject("content") != null) {
					Object bodySchemaObject = _getSchemaObject(
						"readOnly", openAPIJSONObject,
						_getBodySchemaJSONObject(operationJSONObject),
						new HashSet<>());

					if (bodySchemaObject instanceof Map) {
						bodySchemaMap = (Map<String, Object>)bodySchemaObject;
					}
				}

				if (bodySchemaMap == null) {
					bodySchemaMap = LinkedHashMapBuilder.<String, Object>put(
						"type", "object"
					).build();
				}

				String requestBodyDescription = requestBodyJSONObject.getString(
					"description");

				if (Validator.isNotNull(requestBodyDescription)) {
					Object bodyDescription = bodySchemaMap.get("description");

					if (Validator.isNull(bodyDescription)) {
						bodySchemaMap.put(
							"description", requestBodyDescription);
					}
					else if (!Objects.equals(
								bodyDescription, requestBodyDescription)) {

						bodySchemaMap.put(
							"description",
							StringBundler.concat(
								requestBodyDescription, StringPool.SPACE,
								bodyDescription));
					}
				}

				properties.put("body", bodySchemaMap);

				requiredPropertyNames.add("body");
			}
		}

		Collection<String> responseFieldNames = _getResponseFieldNames(
			openAPIJSONObject, operationJSONObject);

		restrictFields = _addLocalizedRestrictFields(
			openAPIJSONObject, operationJSONObject, restrictFields);

		if (Validator.isNotNull(restrictFields)) {
			responseFieldNames.removeAll(
				Arrays.asList(StringUtil.split(restrictFields)));
		}

		Set<String> visitedParameterNames = new HashSet<>();

		_addParameters(
			openAPIJSONObject, operationJSONObject.getJSONArray("parameters"),
			properties, requiredPropertyNames, responseFieldNames,
			visitedParameterNames);
		_addParameters(
			openAPIJSONObject, pathParametersJSONArray, properties,
			requiredPropertyNames, responseFieldNames, visitedParameterNames);

		if (injectVulcanParameters && Objects.equals(method, "get") &&
			visitedParameterNames.add("fields")) {

			properties.put(
				"fields",
				_getParameterSchemaMap(_DESCRIPTION, responseFieldNames));
		}

		return LinkedHashMapBuilder.<String, Object>put(
			"properties", properties
		).put(
			"required", requiredPropertyNames
		).put(
			"type", "object"
		).build();
	}

	private static JSONObject _getItemsArrayJSONObject(
		JSONObject schemaJSONObject) {

		JSONObject itemsJSONObject = JSONUtil.getValueAsJSONObject(
			schemaJSONObject, "JSONObject/properties", "JSONObject/items");

		if ((itemsJSONObject == null) ||
			!Objects.equals(itemsJSONObject.getString("type"), "array")) {

			return null;
		}

		return itemsJSONObject;
	}

	private static String _getMaskedFilterString(String filterString) {
		char[] chars = filterString.toCharArray();

		boolean quoted = false;

		for (int i = 0; i < chars.length; i++) {
			if (chars[i] == CharPool.APOSTROPHE) {
				chars[i] = CharPool.SPACE;

				quoted = !quoted;
			}
			else if (quoted) {
				chars[i] = CharPool.SPACE;
			}
		}

		return new String(chars);
	}

	private static JSONObject _getMediaTypeJSONObject(
		JSONObject contentJSONObject) {

		JSONObject mediaTypeJSONObject = contentJSONObject.getJSONObject(
			"application/json");

		if (mediaTypeJSONObject != null) {
			return mediaTypeJSONObject;
		}

		for (String mediaType : contentJSONObject.keySet()) {
			mediaTypeJSONObject = contentJSONObject.getJSONObject(mediaType);

			if (mediaTypeJSONObject != null) {
				return mediaTypeJSONObject;
			}
		}

		return null;
	}

	private static HttpEntity _getMultipartHttpEntity(
		JSONObject inputJSONObject, JSONObject openAPIJSONObject,
		Operation operation) {

		Map<String, Object> bodySchemaMap =
			(Map<String, Object>)_getSchemaObject(
				"readOnly", openAPIJSONObject,
				_getBodySchemaJSONObject(operation._operationJSONObject),
				new HashSet<>());

		MultipartEntityBuilder multipartEntityBuilder =
			MultipartEntityBuilder.create();

		Map<String, Object> properties = (Map<String, Object>)bodySchemaMap.get(
			"properties");

		if (properties == null) {
			return multipartEntityBuilder.build();
		}

		for (Map.Entry<String, Object> entry : properties.entrySet()) {
			String partName = entry.getKey();

			Object value = inputJSONObject.get(partName);

			if (value == null) {
				continue;
			}

			Map<String, Object> partSchema =
				(Map<String, Object>)entry.getValue();

			if (Objects.equals(partSchema.get("format"), "binary")) {
				JSONObject jsonObject = null;
				String fileName = partName;
				String contentType = ContentTypes.APPLICATION_OCTET_STREAM;
				String data;

				if (value instanceof JSONObject) {
					jsonObject = (JSONObject)value;
				}
				else if (value instanceof Map) {
					jsonObject = JSONFactoryUtil.createJSONObject(
						(Map<String, ?>)value);
				}

				if (jsonObject != null) {
					if (jsonObject.has("filename")) {
						fileName = jsonObject.getString("filename");
					}

					if (jsonObject.has("contentType")) {
						contentType = jsonObject.getString("contentType");
					}

					data = jsonObject.getString("data");
				}
				else {
					data = String.valueOf(value);
				}

				if (Validator.isNull(data)) {
					throw new IllegalArgumentException(
						"Multipart part \"" + partName + "\" has no \"data\"");
				}

				Base64.Decoder decoder = Base64.getDecoder();

				multipartEntityBuilder.addBinaryBody(
					partName, decoder.decode(data),
					ContentType.create(contentType), fileName);
			}
			else {
				multipartEntityBuilder.addTextBody(
					partName, _getPart(value),
					ContentType.create(
						ContentTypes.TEXT_PLAIN, StandardCharsets.UTF_8));
			}
		}

		return multipartEntityBuilder.build();
	}

	private static Set<String> _getObjectEntrySystemPropertyNames() {
		if (_objectEntrySystemPropertyNames != null) {
			return _objectEntrySystemPropertyNames;
		}

		Set<String> objectEntrySystemPropertyNames = new HashSet<>();

		ObjectMapper objectMapper = ObjectMapperProviderUtil.getObjectMapper();

		SerializationConfig serializationConfig =
			objectMapper.getSerializationConfig();

		BeanDescription beanDescription = serializationConfig.introspect(
			serializationConfig.constructType(ObjectEntry.class));

		for (BeanPropertyDefinition beanPropertyDefinition :
				beanDescription.findProperties()) {

			objectEntrySystemPropertyNames.add(
				beanPropertyDefinition.getName());
		}

		_objectEntrySystemPropertyNames = Collections.unmodifiableSet(
			objectEntrySystemPropertyNames);

		return _objectEntrySystemPropertyNames;
	}

	private static Map<String, Object> _getOneOfSchemaMap(
		String excludedPropertyAttributeName, JSONObject jsonObject,
		JSONObject openAPIJSONObject, String ref, Set<String> visitedRefs) {

		List<Object> oneOfSchemaObjects = new ArrayList<>();

		JSONObject schemasJSONObject = JSONUtil.getValueAsJSONObject(
			openAPIJSONObject, "JSONObject/components", "JSONObject/schemas");

		if (schemasJSONObject != null) {
			for (String schemaName : schemasJSONObject.keySet()) {
				JSONObject schemaJSONObject = schemasJSONObject.getJSONObject(
					schemaName);

				JSONArray allOfJSONArray = schemaJSONObject.getJSONArray(
					"allOf");

				if (allOfJSONArray == null) {
					continue;
				}

				boolean extendsBase = false;

				for (int i = 0; i < allOfJSONArray.length(); i++) {
					JSONObject memberJSONObject = allOfJSONArray.getJSONObject(
						i);

					if (memberJSONObject == null) {
						continue;
					}

					if (ref.equals(memberJSONObject.getString("$ref"))) {
						extendsBase = true;

						break;
					}
				}

				if (!extendsBase) {
					continue;
				}

				String subtypeRef = "#/components/schemas/" + schemaName;

				if (visitedRefs.contains(subtypeRef)) {
					continue;
				}

				Set<String> subtypeVisitedRefs = new HashSet<>(visitedRefs);

				subtypeVisitedRefs.add(subtypeRef);

				oneOfSchemaObjects.add(
					_getSchemaObject(
						excludedPropertyAttributeName, openAPIJSONObject,
						schemaJSONObject, subtypeVisitedRefs));
			}
		}

		if (oneOfSchemaObjects.isEmpty()) {
			Map<String, Object> schemaMap = new LinkedHashMap<>();

			for (String key : jsonObject.keySet()) {
				if (_excludedSchemaKeys.contains(key) || key.startsWith("x-")) {
					continue;
				}

				schemaMap.put(
					key,
					_getSchemaObject(
						excludedPropertyAttributeName, openAPIJSONObject,
						jsonObject.get(key), visitedRefs));
			}

			_filterProperties(excludedPropertyAttributeName, schemaMap);

			return schemaMap;
		}

		return LinkedHashMapBuilder.<String, Object>put(
			"oneOf", oneOfSchemaObjects
		).build();
	}

	private static Operation _getOperation(
		JSONObject openAPIJSONObject, String toolName) {

		JSONObject pathsJSONObject = openAPIJSONObject.getJSONObject("paths");

		if (pathsJSONObject == null) {
			throw new IllegalArgumentException(
				"OpenAPI document has no \"paths\" object");
		}

		for (String path : pathsJSONObject.keySet()) {
			JSONObject pathJSONObject = pathsJSONObject.getJSONObject(path);

			for (String method : METHODS) {
				JSONObject operationJSONObject = pathJSONObject.getJSONObject(
					method);

				if ((operationJSONObject == null) ||
					!toolName.equals(
						operationJSONObject.getString("operationId"))) {

					continue;
				}

				return new Operation(
					method, operationJSONObject, path,
					pathJSONObject.getJSONArray("parameters"));
			}
		}

		throw new IllegalArgumentException(
			"OpenAPI document has no tool with name \"" + toolName + "\"");
	}

	private static Map<String, Object> _getParameterSchemaMap(
		String description, Collection<String> enumFieldNames) {

		return LinkedHashMapBuilder.<String, Object>put(
			"description", description
		).put(
			"items",
			LinkedHashMapBuilder.<String, Object>put(
				"type", "string"
			).put(
				"enum",
				() -> {
					if ((enumFieldNames == null) || enumFieldNames.isEmpty()) {
						return null;
					}

					return new ArrayList<>(enumFieldNames);
				}
			).build()
		).put(
			"type", "array"
		).build();
	}

	private static Map<String, Object> _getParameterSchemaObjects(
		String in, JSONObject inputJSONObject, Operation operation) {

		Map<String, Object> parameterSchemaObjects = new LinkedHashMap<>();

		for (JSONArray parametersJSONArray :
				Arrays.asList(
					operation._operationJSONObject.getJSONArray("parameters"),
					operation._pathParametersJSONArray)) {

			if (parametersJSONArray == null) {
				continue;
			}

			for (int i = 0; i < parametersJSONArray.length(); i++) {
				JSONObject parameterJSONObject =
					parametersJSONArray.getJSONObject(i);

				if (!Objects.equals(parameterJSONObject.getString("in"), in)) {
					continue;
				}

				String name = parameterJSONObject.getString("name");

				if (parameterSchemaObjects.containsKey(name) ||
					!inputJSONObject.has(name)) {

					continue;
				}

				Object value = inputJSONObject.get(name);

				if (value == null) {
					continue;
				}

				parameterSchemaObjects.put(name, value);
			}
		}

		return parameterSchemaObjects;
	}

	private static String _getPart(Object value) {
		if (value instanceof JSONArray || value instanceof JSONObject) {
			return value.toString();
		}

		if (value instanceof Map) {
			return JSONFactoryUtil.createJSONObject(
				(Map<String, ?>)value
			).toString();
		}

		if (value instanceof List) {
			return JSONFactoryUtil.createJSONArray(
				(List<?>)value
			).toString();
		}

		return String.valueOf(value);
	}

	private static String _getPath(
		JSONObject inputJSONObject, Operation operation) {

		Map<String, Object> parameterSchemaObjects = _getParameterSchemaObjects(
			"path", inputJSONObject, operation);

		String path = operation._path;

		for (Map.Entry<String, Object> entry :
				parameterSchemaObjects.entrySet()) {

			path = StringUtil.replace(
				path, "{" + entry.getKey() + "}",
				URLCodec.encodeURL(String.valueOf(entry.getValue())));
		}

		return path;
	}

	private static Map<String, JSONObject> _getPropertiesJSONObjects(
		boolean excludeWriteOnlyProperties, JSONObject openAPIJSONObject,
		boolean requiredOnly, JSONObject schemaJSONObject,
		Set<String> visitedRefs) {

		Map<String, JSONObject> propertiesJSONObjects = new LinkedHashMap<>();

		if (schemaJSONObject == null) {
			return propertiesJSONObjects;
		}

		if (schemaJSONObject.has("$ref")) {
			String ref = schemaJSONObject.getString("$ref");

			if (!visitedRefs.add(ref)) {
				return propertiesJSONObjects;
			}

			return _getPropertiesJSONObjects(
				excludeWriteOnlyProperties, openAPIJSONObject, requiredOnly,
				_getRefJSONObject(openAPIJSONObject, ref), visitedRefs);
		}

		if (Objects.equals(schemaJSONObject.getString("type"), "array")) {
			return _getPropertiesJSONObjects(
				excludeWriteOnlyProperties, openAPIJSONObject, requiredOnly,
				schemaJSONObject.getJSONObject("items"), visitedRefs);
		}

		JSONObject itemsArrayJSONObject = _getItemsArrayJSONObject(
			schemaJSONObject);

		if (itemsArrayJSONObject != null) {
			return _getPropertiesJSONObjects(
				excludeWriteOnlyProperties, openAPIJSONObject, requiredOnly,
				itemsArrayJSONObject.getJSONObject("items"), visitedRefs);
		}

		JSONObject propertiesJSONObject = schemaJSONObject.getJSONObject(
			"properties");

		if (requiredOnly) {
			for (String requiredPropertyName :
					JSONUtil.toStringArray(
						schemaJSONObject.getJSONArray("required"))) {

				JSONObject requiredPropertyJSONObject = null;

				if (propertiesJSONObject != null) {
					requiredPropertyJSONObject =
						propertiesJSONObject.getJSONObject(
							requiredPropertyName);
				}

				if (excludeWriteOnlyProperties &&
					_isWriteOnlyProperty(requiredPropertyJSONObject)) {

					continue;
				}

				propertiesJSONObjects.put(
					requiredPropertyName, requiredPropertyJSONObject);
			}
		}
		else if (propertiesJSONObject != null) {
			for (String propertyName : propertiesJSONObject.keySet()) {
				JSONObject propertyJSONObject =
					propertiesJSONObject.getJSONObject(propertyName);

				if (excludeWriteOnlyProperties &&
					_isWriteOnlyProperty(propertyJSONObject)) {

					continue;
				}

				propertiesJSONObjects.put(propertyName, propertyJSONObject);
			}
		}

		JSONArray allOfJSONArray = schemaJSONObject.getJSONArray("allOf");

		if (allOfJSONArray != null) {
			for (int i = 0; i < allOfJSONArray.length(); i++) {
				propertiesJSONObjects.putAll(
					_getPropertiesJSONObjects(
						excludeWriteOnlyProperties, openAPIJSONObject,
						requiredOnly, allOfJSONArray.getJSONObject(i),
						visitedRefs));
			}
		}

		return propertiesJSONObjects;
	}

	private static String _getQueryString(
		JSONObject inputJSONObject, JSONObject openAPIJSONObject,
		Operation operation, String restrictFields) {

		StringBundler sb = new StringBundler();

		Map<String, Object> parameterSchemaObjects = _getParameterSchemaObjects(
			"query", inputJSONObject, operation);

		Set<String> visitedParameterNames = new HashSet<>(
			Arrays.asList("fields", "restrictFields"));

		for (Map.Entry<String, Object> entry :
				parameterSchemaObjects.entrySet()) {

			String name = entry.getKey();

			if (!visitedParameterNames.add(name)) {
				continue;
			}

			_appendQueryParameter(name, sb, entry.getValue());
		}

		Set<String> fieldNames = _getFieldNames(inputJSONObject.opt("fields"));

		if (!fieldNames.isEmpty()) {
			_appendQueryParameter("fields", sb, StringUtil.merge(fieldNames));
		}

		restrictFields = _addLocalizedRestrictFields(
			openAPIJSONObject, operation._operationJSONObject, restrictFields);

		if (Objects.equals(operation._method, "get")) {
			if (Validator.isNull(restrictFields)) {
				restrictFields = "actions";
			}
			else {
				restrictFields = "actions," + restrictFields;
			}
		}

		if (Validator.isNotNull(restrictFields)) {
			_appendQueryParameter("restrictFields", sb, restrictFields);
		}

		return sb.toString();
	}

	private static JSONObject _getRefJSONObject(
		JSONObject openAPIJSONObject, String ref) {

		JSONObject refJSONObject = openAPIJSONObject;

		for (String part : StringUtil.split(ref.substring(2), CharPool.SLASH)) {
			refJSONObject = refJSONObject.getJSONObject(part);
		}

		return refJSONObject;
	}

	private static Collection<String> _getResponseFieldNames(
		JSONObject openAPIJSONObject, JSONObject operationJSONObject) {

		Map<String, JSONObject> propertiesJSONObjects =
			_getPropertiesJSONObjects(
				true, openAPIJSONObject, false,
				getResponseSchemaJSONObject(operationJSONObject),
				new HashSet<>());

		return new TreeSet<>(propertiesJSONObjects.keySet());
	}

	private static int _getSchemaMaxDepth(
		String excludedPropertyAttributeName) {

		if (Objects.equals(excludedPropertyAttributeName, "writeOnly")) {
			return 5;
		}

		return 3;
	}

	private static Object _getSchemaObject(
		String excludedPropertyAttributeName, JSONObject openAPIJSONObject,
		Object value, Set<String> visitedRefs) {

		if (value instanceof JSONObject) {
			JSONObject jsonObject = (JSONObject)value;

			if (jsonObject.has("$ref")) {
				String ref = jsonObject.getString("$ref");

				int schemaMaxDepth = _getSchemaMaxDepth(
					excludedPropertyAttributeName);

				if (visitedRefs.contains(ref) ||
					(visitedRefs.size() >= schemaMaxDepth)) {

					return HashMapBuilder.<String, Object>put(
						"type", "object"
					).build();
				}

				JSONObject refJSONObject = _getRefJSONObject(
					openAPIJSONObject, ref);

				Set<String> currentVisitedRefs = new HashSet<>(visitedRefs);

				currentVisitedRefs.add(ref);

				if (refJSONObject.has("discriminator")) {
					return _getOneOfSchemaMap(
						excludedPropertyAttributeName, refJSONObject,
						openAPIJSONObject, ref, currentVisitedRefs);
				}

				return _getSchemaObject(
					excludedPropertyAttributeName, openAPIJSONObject,
					refJSONObject, currentVisitedRefs);
			}

			if (jsonObject.has("allOf")) {
				return _getAllOfSchemaMap(
					excludedPropertyAttributeName, jsonObject,
					openAPIJSONObject, visitedRefs);
			}

			Map<String, Object> schemaMap = new LinkedHashMap<>();

			for (String key : jsonObject.keySet()) {
				if (_excludedSchemaKeys.contains(key) || key.startsWith("x-")) {
					continue;
				}

				schemaMap.put(
					key,
					_getSchemaObject(
						excludedPropertyAttributeName, openAPIJSONObject,
						jsonObject.get(key), visitedRefs));
			}

			_filterProperties(excludedPropertyAttributeName, schemaMap);

			return schemaMap;
		}

		if (value instanceof JSONArray jsonArray) {
			return TransformUtil.transform(
				JSONUtil.toObjectList(jsonArray),
				object -> _getSchemaObject(
					excludedPropertyAttributeName, openAPIJSONObject, object,
					visitedRefs));
		}

		return value;
	}

	private static Map<String, Object> _getSchemaProperties(
		Object schemaObject) {

		if (!(schemaObject instanceof Map)) {
			return null;
		}

		Map<String, Object> schemaMap = (Map<String, Object>)schemaObject;

		if (Objects.equals(schemaMap.get("type"), "array")) {
			return _getSchemaProperties(schemaMap.get("items"));
		}

		Object propertiesObject = schemaMap.get("properties");

		if (!(propertiesObject instanceof Map)) {
			return null;
		}

		Map<String, Object> propertiesMap =
			(Map<String, Object>)propertiesObject;

		Object itemsObject = propertiesMap.get("items");

		if (!(itemsObject instanceof Map)) {
			return propertiesMap;
		}

		Map<String, Object> itemsMap = (Map<String, Object>)itemsObject;

		if (!Objects.equals(itemsMap.get("type"), "array")) {
			return propertiesMap;
		}

		return _getSchemaProperties(itemsMap.get("items"));
	}

	private static Set<String> _getSortFieldPaths(String sortString) {
		if (Validator.isNull(sortString)) {
			return Collections.emptySet();
		}

		Set<String> fieldPaths = new LinkedHashSet<>();

		for (String string : StringUtil.split(sortString)) {
			int index = string.indexOf(CharPool.COLON);

			if (index >= 0) {
				string = string.substring(0, index);
			}

			string = string.trim();

			if (string.isEmpty()) {
				continue;
			}

			fieldPaths.add(string);
		}

		return fieldPaths;
	}

	private static boolean _hasSchemaProperty(
		String fieldName, Object schemaObject) {

		for (String name : StringUtil.split(fieldName, CharPool.PERIOD)) {
			Map<String, Object> propertiesMap = _getSchemaProperties(
				schemaObject);

			if (propertiesMap == null) {
				return false;
			}

			schemaObject = propertiesMap.get(name);
		}

		if (schemaObject != null) {
			return true;
		}

		return false;
	}

	private static boolean _isBinary(Map<String, Object> schemaMap) {
		if (schemaMap == null) {
			return false;
		}

		return Objects.equals(schemaMap.get("format"), "binary");
	}

	private static boolean _isMultipartRequest(JSONObject operationJSONObject) {
		JSONObject contentJSONObject = JSONUtil.getValueAsJSONObject(
			operationJSONObject, "JSONObject/requestBody",
			"JSONObject/content");

		if ((contentJSONObject == null) ||
			contentJSONObject.has("application/json")) {

			return false;
		}

		return contentJSONObject.has("multipart/form-data");
	}

	private static boolean _isObjectEntrySchema(JSONObject schemaJSONObject) {
		if (schemaJSONObject == null) {
			return false;
		}

		return Objects.equals(
			JSONUtil.getValueAsString(
				schemaJSONObject, "JSONObject/properties",
				"JSONObject/x-class-name", "Object/default"),
			ObjectEntry.class.getName());
	}

	private static boolean _isRestrictedFieldPath(
		String fieldPath, String[] restrictFieldNames) {

		String normalizedFieldPath = StringUtil.replace(
			fieldPath, CharPool.SLASH, CharPool.PERIOD);

		for (String restrictFieldName : restrictFieldNames) {
			if (normalizedFieldPath.equals(restrictFieldName) ||
				normalizedFieldPath.startsWith(
					restrictFieldName + StringPool.PERIOD)) {

				return true;
			}
		}

		return false;
	}

	private static boolean _isWriteOnlyProperty(JSONObject propertyJSONObject) {
		if (propertyJSONObject == null) {
			return false;
		}

		return GetterUtil.getBoolean(propertyJSONObject.get("writeOnly"));
	}

	private static Map<String, Object> _removeObjectEntrySystemProperties(
		Map<String, Object> schema) {

		Map<String, Object> properties = (Map<String, Object>)schema.get(
			"properties");

		Map<String, Object> customProperties = new LinkedHashMap<>(properties);

		Set<String> keys = customProperties.keySet();

		keys.removeAll(_getObjectEntrySystemPropertyNames());

		return HashMapBuilder.<String, Object>putAll(
			schema
		).put(
			"properties", customProperties
		).build();
	}

	private static Map<String, Object> _toRequiredInputSchema(
		Map<String, ?> inputSchema, boolean objectToolSet) {

		Map<String, Object> requiredInputSchema = _toRequiredSchema(
			0, objectToolSet, inputSchema);

		if (requiredInputSchema == null) {
			requiredInputSchema = HashMapBuilder.<String, Object>put(
				"properties", new HashMap<String, Object>()
			).put(
				"type", "object"
			).build();
		}

		Map<String, ?> properties = (Map<String, ?>)inputSchema.get(
			"properties");

		if (properties.containsKey("fields")) {
			Map<String, Object> requiredProperties =
				(Map<String, Object>)requiredInputSchema.get("properties");

			requiredProperties.put("fields", properties.get("fields"));
		}

		return requiredInputSchema;
	}

	private static Map<String, Object> _toRequiredPropertySchema(
		int depth, boolean objectToolSet, Map<String, Object> propertySchema) {

		Map<String, Object> requiredSchema = _toRequiredSchema(
			depth + 1, objectToolSet, propertySchema);

		if (requiredSchema != null) {
			Object description = propertySchema.get("description");

			if (description != null) {
				requiredSchema.put("description", description);
			}

			return requiredSchema;
		}

		if (!propertySchema.containsKey("properties")) {
			return propertySchema;
		}

		if (objectToolSet && (depth == 0)) {
			propertySchema = _removeObjectEntrySystemProperties(propertySchema);
		}

		return _collapseNestedProperties(propertySchema);
	}

	private static Map<String, Object> _toRequiredSchema(
		int depth, boolean objectToolSet, Map<String, ?> schema) {

		if ((schema == null) || (depth > 4)) {
			return null;
		}

		Map<String, Object> properties = (Map<String, Object>)schema.get(
			"properties");
		List<String> requiredPropertyNames = (List<String>)schema.get(
			"required");

		if ((properties == null) || ListUtil.isEmpty(requiredPropertyNames)) {
			return null;
		}

		Map<String, Object> requiredPropertySchemas = new LinkedHashMap<>();

		for (String requiredPropertyName : requiredPropertyNames) {
			if (!(properties.get(requiredPropertyName) instanceof
					Map<?, ?> propertySchema)) {

				continue;
			}

			requiredPropertySchemas.put(
				requiredPropertyName,
				_toRequiredPropertySchema(
					depth, objectToolSet, (Map<String, Object>)propertySchema));
		}

		if (requiredPropertySchemas.isEmpty()) {
			return null;
		}

		return HashMapBuilder.<String, Object>put(
			"properties", requiredPropertySchemas
		).put(
			"required", new ArrayList<>(requiredPropertySchemas.keySet())
		).put(
			"type", schema.get("type")
		).build();
	}

	private static void _validateQueryParameters(
		JSONObject inputJSONObject, Operation operation,
		String restrictFields) {

		if (Validator.isNull(restrictFields)) {
			return;
		}

		String[] restrictFieldNames = StringUtil.split(restrictFields);

		Map<String, Object> parameterSchemaObjects = _getParameterSchemaObjects(
			"query", inputJSONObject, operation);

		for (Map.Entry<String, Object> entry :
				parameterSchemaObjects.entrySet()) {

			Set<String> fieldPaths = null;

			String name = entry.getKey();

			if (Objects.equals(name, "filter")) {
				fieldPaths = _getFilterFieldPaths(
					String.valueOf(entry.getValue()));
			}
			else if (Objects.equals(name, "sort")) {
				fieldPaths = _getSortFieldPaths(
					String.valueOf(entry.getValue()));
			}
			else {
				continue;
			}

			for (String fieldPath : fieldPaths) {
				if (!_isRestrictedFieldPath(fieldPath, restrictFieldNames)) {
					continue;
				}

				String message = StringBundler.concat(
					"Parameter \"", name, "\" references a restricted field");

				if (Objects.equals(name, "filter")) {
					throw new InvalidFilterException(message);
				}

				throw new InvalidSortException(message);
			}
		}
	}

	private static final String _DESCRIPTION =
		"Fields to include in the response. Pass only the fields the user " +
			"actually needs.";

	private static final Set<String> _excludedSchemaKeys = Set.of(
		"actions", "example", "exclusiveMaximum", "exclusiveMinimum", "xml");
	private static final Pattern _fieldPathPattern = Pattern.compile(
		"[A-Za-z_][A-Za-z0-9_]*(?:[./][A-Za-z_][A-Za-z0-9_]*)*");
	private static volatile Set<String> _objectEntrySystemPropertyNames;

	private static class Operation {

		private Operation(
			String method, JSONObject operationJSONObject, String path,
			JSONArray pathParametersJSONArray) {

			_method = method;
			_operationJSONObject = operationJSONObject;
			_path = path;
			_pathParametersJSONArray = pathParametersJSONArray;
		}

		private final String _method;
		private final JSONObject _operationJSONObject;
		private final String _path;
		private final JSONArray _pathParametersJSONArray;

	}

}