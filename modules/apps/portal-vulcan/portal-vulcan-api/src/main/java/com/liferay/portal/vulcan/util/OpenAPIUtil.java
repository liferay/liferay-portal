/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.batch.engine.Field;
import com.liferay.portal.vulcan.yaml.openapi.Components;
import com.liferay.portal.vulcan.yaml.openapi.Content;
import com.liferay.portal.vulcan.yaml.openapi.Get;
import com.liferay.portal.vulcan.yaml.openapi.OpenAPIYAML;
import com.liferay.portal.vulcan.yaml.openapi.Operation;
import com.liferay.portal.vulcan.yaml.openapi.Parameter;
import com.liferay.portal.vulcan.yaml.openapi.PathItem;
import com.liferay.portal.vulcan.yaml.openapi.Post;
import com.liferay.portal.vulcan.yaml.openapi.Response;
import com.liferay.portal.vulcan.yaml.openapi.ResponseCode;
import com.liferay.portal.vulcan.yaml.openapi.Schema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Javier de Arcos
 */
public class OpenAPIUtil {

	public static String[] getBatchUnsupportedFormats(
		Map<String, Object> extensions) {

		if (MapUtil.isEmpty(extensions) ||
			!extensions.containsKey("x-batch-unsupported-formats")) {

			return null;
		}

		return StringUtil.split(
			GetterUtil.getString(
				extensions.get("x-batch-unsupported-formats")));
	}

	public static List<String> getCreateEntityScopes(
		String entityName, OpenAPIYAML openAPIYAML) {

		List<String> scopes = new ArrayList<>();

		Map<String, PathItem> pathItemsMap = openAPIYAML.getPathItems();

		for (PathItem pathItem : pathItemsMap.values()) {
			Post post = pathItem.getPost();

			if ((post == null) ||
				!_hasOKResponseContentSchemaReferenceLike(entityName, post)) {

				continue;
			}

			String operationScope = _getOperationScope(post);

			if (Validator.isNull(operationScope)) {
				continue;
			}

			scopes.add(operationScope);
		}

		return scopes;
	}

	public static Map<String, Field> getDTOEntityFields(
		String entityName, OpenAPIYAML openAPIYAML) {

		Components components = openAPIYAML.getComponents();

		Map<String, Schema> schemas = components.getSchemas();

		Schema schema = schemas.get(entityName);

		if (schema == null) {
			return Collections.emptyMap();
		}

		Map<String, Field> fields = new HashMap<>();

		List<String> requiredPropertySchemaNames =
			_getRequiredPropertySchemaNames(schema);

		Map<String, Schema> propertySchemas = schema.getPropertySchemas();

		for (Map.Entry<String, Schema> schemaEntry :
				propertySchemas.entrySet()) {

			String propertyName = schemaEntry.getKey();
			Schema propertySchema = schemaEntry.getValue();

			fields.put(
				propertyName,
				Field.of(
					null, propertySchema.getDescription(), propertyName,
					propertySchema.isReadOnly(), null,
					requiredPropertySchemaNames.contains(propertyName),
					propertySchema.getType(),
					getBatchUnsupportedFormats(Collections.emptyMap()),
					propertySchema.isWriteOnly()));
		}

		return fields;
	}

	public static List<String> getReadEntityScopes(
		String entityName, OpenAPIYAML openAPIYAML) {

		List<String> scopes = new ArrayList<>();

		Map<String, PathItem> pathItemsMap = openAPIYAML.getPathItems();

		for (PathItem pathItem : pathItemsMap.values()) {
			Get get = pathItem.getGet();

			if ((get == null) ||
				!_hasOKResponseContentSchemaReferenceLike(
					"Page" + entityName, get)) {

				continue;
			}

			String operationScope = _getOperationScope(get);

			if (Validator.isNull(operationScope)) {
				continue;
			}

			scopes.add(operationScope);
		}

		return scopes;
	}

	private static String _getOperationScope(Operation operation) {
		List<Parameter> parameters = operation.getParameters();

		if (parameters.isEmpty()) {
			return null;
		}

		StringBundler sb = new StringBundler(parameters.size() * 2);

		for (Parameter parameter : parameters) {
			if (!StringUtil.equals(parameter.getIn(), "path")) {
				continue;
			}

			String name = parameter.getName();

			if (name.endsWith("Id")) {
				name = StringUtil.removeLast(name, "Id");
			}

			sb.append(name);
			sb.append(",");
		}

		if (sb.index() > 0) {
			sb.setIndex(sb.index() - 1);
		}

		return sb.toString();
	}

	private static List<String> _getRequiredPropertySchemaNames(Schema schema) {
		List<String> requiredPropertySchemaNames =
			schema.getRequiredPropertySchemaNames();

		if (requiredPropertySchemaNames == null) {
			requiredPropertySchemaNames = Collections.emptyList();
		}

		return requiredPropertySchemaNames;
	}

	private static boolean _hasOKResponseContentSchemaReferenceLike(
		String name, Operation operation) {

		Map<ResponseCode, Response> responses = operation.getResponses();

		if (responses == null) {
			return false;
		}

		for (Map.Entry<ResponseCode, Response> entry : responses.entrySet()) {
			if (!_isOKResponseCode(entry.getKey())) {
				continue;
			}

			Response response = entry.getValue();

			Map<String, Content> contentMap = response.getContent();

			for (Map.Entry<String, Content> contentEntry :
					contentMap.entrySet()) {

				Content content = contentEntry.getValue();

				Schema schema = content.getSchema();

				if (schema == null) {
					continue;
				}

				String reference = schema.getReference();

				if (reference == null) {
					continue;
				}

				if (StringUtil.equals(
						name,
						reference.substring(reference.lastIndexOf('/') + 1))) {

					return true;
				}
			}
		}

		return false;
	}

	private static boolean _isOKResponseCode(ResponseCode responseCode) {
		if (responseCode.isDefaultResponse() ||
			((responseCode.getHttpCode() / 100) == 2)) {

			return true;
		}

		return false;
	}

}