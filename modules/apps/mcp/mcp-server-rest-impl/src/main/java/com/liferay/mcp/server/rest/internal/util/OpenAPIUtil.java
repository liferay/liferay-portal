/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.util;

import com.liferay.mcp.server.rest.dto.v1_0.Tool;
import com.liferay.mcp.server.rest.dto.v1_0.ToolSummary;
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
import com.liferay.portal.vulcan.http.VulcanRequestForwarder;

import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.util.EntityUtils;

/**
 * @author Alejandro Tardín
 */
public class OpenAPIUtil {

	public static VulcanRequestForwarder.Request getRequest(
			String basePath, Map<String, String> headers,
			JSONObject inputJSONObject, JSONObject openAPIJSONObject,
			String toolName, User user)
		throws Exception {

		byte[] body;
		String contentType;

		Operation operation = _getOperation(openAPIJSONObject, toolName);

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
					inputJSONObject, operation);

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

	public static Tool getTool(
		boolean injectVulcanParameters, JSONObject openAPIJSONObject,
		String toolName) {

		Operation operation = _getOperation(openAPIJSONObject, toolName);

		return new Tool() {
			{
				setDescription(
					() -> _getDescription(
						operation._method, operation._operationJSONObject,
						operation._path));
				setInputSchema(
					() -> _getInputSchema(
						injectVulcanParameters, operation._method,
						openAPIJSONObject, operation._operationJSONObject,
						operation._pathParametersJSONArray));

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

			for (String method : _METHODS) {
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

	private static void _addMultipartParts(
		JSONObject openAPIJSONObject, JSONObject operationJSONObject,
		Map<String, Object> properties, List<String> requiredPropertyNames) {

		Map<String, Object> bodySchemaMap =
			(Map<String, Object>)_getSchemaObject(
				openAPIJSONObject,
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
		JSONObject parameterJSONObject, Map<String, Object> properties,
		List<String> requiredPropertyNames,
		Collection<String> responseFieldNames,
		Set<String> visitedParameterNames) {

		String name = parameterJSONObject.getString("name");

		if (!visitedParameterNames.add(name)) {
			return;
		}

		Map<String, Object> parameterSchemaMap;

		if (Objects.equals(name, "fields")) {
			parameterSchemaMap = _getParameterSchemaMap(
				_DESCRIPTION_FIELDS, responseFieldNames);
		}
		else {
			parameterSchemaMap = new LinkedHashMap<>();

			JSONObject parameterSchemaJSONObject =
				parameterJSONObject.getJSONObject("schema");

			for (String key : parameterSchemaJSONObject.keySet()) {
				parameterSchemaMap.put(key, parameterSchemaJSONObject.get(key));
			}

			if (parameterJSONObject.has("description")) {
				parameterSchemaMap.put(
					"description",
					parameterJSONObject.getString("description"));
			}
		}

		properties.put(name, parameterSchemaMap);

		if (Objects.equals(parameterJSONObject.getString("in"), "path")) {
			requiredPropertyNames.add(name);
		}
	}

	private static void _addParameters(
		JSONArray parametersJSONArray, Map<String, Object> properties,
		List<String> requiredPropertyNames,
		Collection<String> responseFieldNames,
		Set<String> visitedParameterNames) {

		if (parametersJSONArray == null) {
			return;
		}

		for (int i = 0; i < parametersJSONArray.length(); i++) {
			_addParameter(
				parametersJSONArray.getJSONObject(i), properties,
				requiredPropertyNames, responseFieldNames,
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

	private static void _filterReadOnlyProperties(
		Map<String, Object> schemaMap) {

		Object propertiesObject = schemaMap.get("properties");

		if (!(propertiesObject instanceof Map)) {
			return;
		}

		Map<String, Object> properties = (Map<String, Object>)propertiesObject;

		Set<String> readOnlyPropertyNames = new HashSet<>();

		for (Map.Entry<String, Object> entry : properties.entrySet()) {
			Object value = entry.getValue();

			if (!(value instanceof Map)) {
				continue;
			}

			Map<?, ?> propertyMap = (Map<?, ?>)value;

			if (GetterUtil.getBoolean(propertyMap.get("readOnly"))) {
				readOnlyPropertyNames.add(entry.getKey());
			}
		}

		if (readOnlyPropertyNames.isEmpty()) {
			return;
		}

		readOnlyPropertyNames.forEach(properties::remove);

		List<Object> required = (List<Object>)schemaMap.get("required");

		if (required == null) {
			return;
		}

		schemaMap.put(
			"required",
			ListUtil.filter(
				TransformUtil.transform(required, String::valueOf),
				Predicate.not(readOnlyPropertyNames::contains)));
	}

	private static Map<String, Object> _getAllOfSchemaMap(
		JSONObject jsonObject, JSONObject openAPIJSONObject,
		Set<String> visitedRefs) {

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
					openAPIJSONObject, jsonObject.get(key), visitedRefs));
		}

		JSONArray allOfJSONArray = jsonObject.getJSONArray("allOf");

		for (int i = 0; i < allOfJSONArray.length(); i++) {
			Object schemaObject = _getSchemaObject(
				openAPIJSONObject, allOfJSONArray.getJSONObject(i),
				visitedRefs);

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

		_filterReadOnlyProperties(allOfSchemaMap);

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

		JSONObject mediaTypeJSONObject = contentJSONObject.getJSONObject(
			"application/json");

		if (mediaTypeJSONObject == null) {
			for (String mediaType : contentJSONObject.keySet()) {
				mediaTypeJSONObject = contentJSONObject.getJSONObject(
					mediaType);

				if (mediaTypeJSONObject != null) {
					break;
				}
			}
		}

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

	private static Map<String, Object> _getInputSchema(
		boolean injectVulcanParameters, String method,
		JSONObject openAPIJSONObject, JSONObject operationJSONObject,
		JSONArray pathParametersJSONArray) {

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
						openAPIJSONObject,
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

		Set<String> visitedParameterNames = new HashSet<>();

		_addParameters(
			operationJSONObject.getJSONArray("parameters"), properties,
			requiredPropertyNames, responseFieldNames, visitedParameterNames);
		_addParameters(
			pathParametersJSONArray, properties, requiredPropertyNames,
			responseFieldNames, visitedParameterNames);

		if (injectVulcanParameters && Objects.equals(method, "get") &&
			visitedParameterNames.add("fields")) {

			properties.put(
				"fields",
				_getParameterSchemaMap(
					_DESCRIPTION_FIELDS, responseFieldNames));
		}

		return LinkedHashMapBuilder.<String, Object>put(
			"properties", properties
		).put(
			"required", requiredPropertyNames
		).put(
			"type", "object"
		).build();
	}

	private static HttpEntity _getMultipartHttpEntity(
		JSONObject inputJSONObject, JSONObject openAPIJSONObject,
		Operation operation) {

		Map<String, Object> bodySchemaMap =
			(Map<String, Object>)_getSchemaObject(
				openAPIJSONObject,
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

	private static Map<String, Object> _getOneOfSchemaMap(
		JSONObject jsonObject, JSONObject openAPIJSONObject, String ref,
		Set<String> visitedRefs) {

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
						openAPIJSONObject, schemaJSONObject,
						subtypeVisitedRefs));
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
						openAPIJSONObject, jsonObject.get(key), visitedRefs));
			}

			_filterReadOnlyProperties(schemaMap);

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

			for (String method : _METHODS) {
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

	private static String _getQueryString(
		JSONObject inputJSONObject, Operation operation) {

		StringBundler sb = new StringBundler();

		Map<String, Object> parameterSchemaObjects = _getParameterSchemaObjects(
			"query", inputJSONObject, operation);

		Set<String> visitedParameterNames = new HashSet<>(
			Arrays.asList("fields", "restrictFields"));

		for (Map.Entry<String, Object> entry :
				parameterSchemaObjects.entrySet()) {

			String name = entry.getKey();

			if (visitedParameterNames.add(name)) {
				_appendQueryParameter(name, sb, entry.getValue());
			}
		}

		Set<String> fieldNames = _getFieldNames(inputJSONObject.opt("fields"));

		if (!fieldNames.isEmpty()) {
			_appendQueryParameter("fields", sb, StringUtil.merge(fieldNames));
		}

		if (Objects.equals(operation._method, "get")) {
			_appendQueryParameter("restrictFields", sb, "actions");
		}

		return sb.toString();
	}

	private static JSONObject _getRefJSONObject(
		String ref, JSONObject openAPIJSONObject) {

		JSONObject refJSONObject = openAPIJSONObject;

		for (String part : StringUtil.split(ref.substring(2), CharPool.SLASH)) {
			refJSONObject = refJSONObject.getJSONObject(part);
		}

		return refJSONObject;
	}

	private static Collection<String> _getResponseFieldNames(
		JSONObject openAPIJSONObject, JSONObject operationJSONObject) {

		JSONObject responsesJSONObject = operationJSONObject.getJSONObject(
			"responses");

		JSONObject responseJSONObject = null;

		if (responsesJSONObject != null) {
			for (String code : responsesJSONObject.keySet()) {
				if (code.startsWith("2")) {
					responseJSONObject = responsesJSONObject.getJSONObject(
						code);

					break;
				}
			}

			if (responseJSONObject == null) {
				responseJSONObject = responsesJSONObject.getJSONObject(
					"default");
			}
		}

		JSONObject responseSchemaJSONObject = null;

		if (responseJSONObject != null) {
			JSONObject contentJSONObject = responseJSONObject.getJSONObject(
				"content");

			if (contentJSONObject != null) {
				JSONObject mediaTypeJSONObject =
					contentJSONObject.getJSONObject("application/json");

				if (mediaTypeJSONObject != null) {
					responseSchemaJSONObject =
						mediaTypeJSONObject.getJSONObject("schema");
				}
			}
		}

		return _getResponseFieldNames(
			openAPIJSONObject, responseSchemaJSONObject, new HashSet<>());
	}

	private static Set<String> _getResponseFieldNames(
		JSONObject openAPIJSONObject, JSONObject schemaJSONObject,
		Set<String> visitedRefs) {

		Set<String> responseFieldNames = new TreeSet<>();

		if (schemaJSONObject == null) {
			return responseFieldNames;
		}

		if (schemaJSONObject.has("$ref")) {
			String ref = schemaJSONObject.getString("$ref");

			if (!visitedRefs.add(ref)) {
				return responseFieldNames;
			}

			return _getResponseFieldNames(
				openAPIJSONObject, _getRefJSONObject(ref, openAPIJSONObject),
				visitedRefs);
		}

		if (Objects.equals(schemaJSONObject.getString("type"), "array")) {
			return _getResponseFieldNames(
				openAPIJSONObject, schemaJSONObject.getJSONObject("items"),
				visitedRefs);
		}

		JSONObject propertiesJSONObject = schemaJSONObject.getJSONObject(
			"properties");

		if (propertiesJSONObject != null) {
			JSONObject itemsPropertyJSONObject =
				propertiesJSONObject.getJSONObject("items");

			if ((itemsPropertyJSONObject != null) &&
				Objects.equals(
					itemsPropertyJSONObject.getString("type"), "array")) {

				return _getResponseFieldNames(
					openAPIJSONObject,
					itemsPropertyJSONObject.getJSONObject("items"),
					visitedRefs);
			}

			responseFieldNames.addAll(propertiesJSONObject.keySet());
		}

		JSONArray allOfJSONArray = schemaJSONObject.getJSONArray("allOf");

		if (allOfJSONArray != null) {
			for (int i = 0; i < allOfJSONArray.length(); i++) {
				responseFieldNames.addAll(
					_getResponseFieldNames(
						openAPIJSONObject, allOfJSONArray.getJSONObject(i),
						visitedRefs));
			}
		}

		return responseFieldNames;
	}

	private static Object _getSchemaObject(
		JSONObject openAPIJSONObject, Object value, Set<String> visitedRefs) {

		if (value instanceof JSONObject) {
			JSONObject jsonObject = (JSONObject)value;

			if (jsonObject.has("$ref")) {
				String ref = jsonObject.getString("$ref");

				if (visitedRefs.contains(ref) || (visitedRefs.size() >= 3)) {
					return HashMapBuilder.<String, Object>put(
						"type", "object"
					).build();
				}

				JSONObject refJSONObject = _getRefJSONObject(
					ref, openAPIJSONObject);

				Set<String> currentVisitedRefs = new HashSet<>(visitedRefs);

				currentVisitedRefs.add(ref);

				if (refJSONObject.has("discriminator")) {
					return _getOneOfSchemaMap(
						refJSONObject, openAPIJSONObject, ref,
						currentVisitedRefs);
				}

				return _getSchemaObject(
					openAPIJSONObject, refJSONObject, currentVisitedRefs);
			}

			if (jsonObject.has("allOf")) {
				return _getAllOfSchemaMap(
					jsonObject, openAPIJSONObject, visitedRefs);
			}

			Map<String, Object> schemaMap = new LinkedHashMap<>();

			for (String key : jsonObject.keySet()) {
				if (_excludedSchemaKeys.contains(key) || key.startsWith("x-")) {
					continue;
				}

				schemaMap.put(
					key,
					_getSchemaObject(
						openAPIJSONObject, jsonObject.get(key), visitedRefs));
			}

			_filterReadOnlyProperties(schemaMap);

			return schemaMap;
		}

		if (value instanceof JSONArray jsonArray) {
			return TransformUtil.transform(
				JSONUtil.toObjectList(jsonArray),
				object -> _getSchemaObject(
					openAPIJSONObject, object, visitedRefs));
		}

		return value;
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

	private static final String _DESCRIPTION_FIELDS =
		"Fields to include in the response. Pass only the fields the user " +
			"actually needs.";

	private static final String[] _METHODS = {
		"delete", "get", "head", "options", "patch", "post", "put"
	};

	private static final Set<String> _excludedSchemaKeys = Set.of(
		"actions", "example", "exclusiveMaximum", "exclusiveMinimum", "xml");

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