/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.internal.freemarker.tool.java.parser.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TextFormatter;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.tools.rest.builder.internal.freemarker.tool.java.JavaMethodParameter;
import com.liferay.portal.tools.rest.builder.internal.freemarker.tool.java.JavaMethodSignature;
import com.liferay.portal.tools.rest.builder.internal.freemarker.util.ConfigUtil;
import com.liferay.portal.tools.rest.builder.internal.freemarker.util.OpenAPIUtil;
import com.liferay.portal.tools.rest.builder.internal.yaml.YAMLUtil;
import com.liferay.portal.tools.rest.builder.internal.yaml.config.ConfigYAML;
import com.liferay.portal.tools.rest.builder.internal.yaml.openapi.Components;
import com.liferay.portal.tools.rest.builder.internal.yaml.openapi.Content;
import com.liferay.portal.tools.rest.builder.internal.yaml.openapi.Info;
import com.liferay.portal.tools.rest.builder.internal.yaml.openapi.Items;
import com.liferay.portal.tools.rest.builder.internal.yaml.openapi.OpenAPIYAML;
import com.liferay.portal.tools.rest.builder.internal.yaml.openapi.Operation;
import com.liferay.portal.tools.rest.builder.internal.yaml.openapi.Parameter;
import com.liferay.portal.tools.rest.builder.internal.yaml.openapi.PathItem;
import com.liferay.portal.tools.rest.builder.internal.yaml.openapi.RequestBody;
import com.liferay.portal.tools.rest.builder.internal.yaml.openapi.Response;
import com.liferay.portal.tools.rest.builder.internal.yaml.openapi.ResponseCode;
import com.liferay.portal.tools.rest.builder.internal.yaml.openapi.Schema;

import java.io.File;
import java.io.IOException;

import java.math.BigDecimal;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Peter Shin
 */
public class OpenAPIParserUtil {

	public static Map<String, Schema> getAllOfPropertySchemas(
		ConfigYAML configYAML, Schema schema, Map<String, Schema> schemas) {

		List<Schema> allOfSchemas = schema.getAllOfSchemas();

		if (allOfSchemas.size() == 1) {
			return schema.getPropertySchemas();
		}

		Map<String, Schema> propertySchemas = new LinkedHashMap<>();

		for (Schema allOfSchema : allOfSchemas) {
			if (allOfSchema.getReference() != null) {
				Schema referenceSchema = schemas.get(
					getReferenceName(allOfSchema.getReference()));

				if (referenceSchema.getDiscriminator() != null) {
					continue;
				}

				if (allOfSchema.isMergeProperties() &&
					ConfigUtil.isVersionCompatible(configYAML, 4)) {

					propertySchemas.putAll(
						referenceSchema.getPropertySchemas());
				}
				else {
					Schema itemSchema = new Schema();

					String reference = allOfSchema.getReference();

					itemSchema.setReference(reference);

					propertySchemas.put(
						StringUtil.lowerCaseFirstLetter(
							getReferenceName(reference)),
						itemSchema);
				}
			}
			else {
				propertySchemas.putAll(allOfSchema.getPropertySchemas());
			}
		}

		return propertySchemas;
	}

	public static String getArguments(
		List<JavaMethodParameter> javaMethodParameters) {

		StringBuilder sb = new StringBuilder();

		for (JavaMethodParameter javaMethodParameter : javaMethodParameters) {
			sb.append(javaMethodParameter.getParameterName());
			sb.append(',');
		}

		if (sb.length() > 0) {
			sb.setLength(sb.length() - 1);
		}

		return sb.toString();
	}

	public static String getArrayClassName(String name) {
		if (name.equals(boolean.class.getName())) {
			return "[Z";
		}
		else if (name.equals(double.class.getName())) {
			return "[D";
		}
		else if (name.equals(float.class.getName())) {
			return "[F";
		}
		else if (name.equals(int.class.getName())) {
			return "[I";
		}
		else if (name.equals(long.class.getName())) {
			return "[J";
		}

		return "[L" + name + ";";
	}

	public static String getElementClassName(String name) {
		if (name.equals("[Z")) {
			return boolean.class.getName();
		}
		else if (name.equals("[D")) {
			return double.class.getName();
		}
		else if (name.equals("[F")) {
			return float.class.getName();
		}
		else if (name.equals("[I")) {
			return int.class.getName();
		}
		else if (name.equals("[J")) {
			return long.class.getName();
		}
		else if (name.startsWith("[L") && name.endsWith(";")) {
			return name.substring(2, name.length() - 1);
		}
		else if (name.startsWith("[[")) {
			return getElementClassName(name.substring(1)) + "[]";
		}

		return name;
	}

	public static List<String> getExternalReferences(OpenAPIYAML openAPIYAML) {
		return _externalReferencesMap.computeIfAbsent(
			openAPIYAML,
			keyOpenAPIYAML -> {
				Set<String> externalReferences = new LinkedHashSet<>();

				Map<String, PathItem> pathItems = keyOpenAPIYAML.getPathItems();

				Map<String, Schema> schemas = new LinkedHashMap<>();

				Components components = keyOpenAPIYAML.getComponents();

				if ((components != null) && (components.getSchemas() != null)) {
					schemas = components.getSchemas();
				}

				if (pathItems != null) {
					for (PathItem pathItem : pathItems.values()) {
						List<Operation> operations = getOperations(pathItem);

						for (Operation operation : operations) {
							RequestBody requestBody =
								operation.getRequestBody();

							if (requestBody != null) {
								_addExternalReferences(
									requestBody.getContent(),
									externalReferences, schemas);
							}

							Map<ResponseCode, Response> responses =
								operation.getResponses();

							for (Response response : responses.values()) {
								if (response == null) {
									continue;
								}

								_addExternalReferences(
									response.getContent(), externalReferences,
									schemas);
							}
						}
					}
				}

				for (Schema schema : schemas.values()) {
					Map<String, Schema> propertySchemas =
						schema.getPropertySchemas();

					if (propertySchemas == null) {
						continue;
					}

					for (Schema propertySchema : propertySchemas.values()) {
						_addExternalReferences(
							externalReferences, propertySchema, schemas);
					}
				}

				return new ArrayList<>(externalReferences);
			});
	}

	public static Map<String, Schema> getExternalSchemas(
			ConfigYAML configYAML, OpenAPIYAML openAPIYAML)
		throws Exception {

		Map<String, Schema> externalReferencesMap = new TreeMap<>();

		String baseDir = configYAML.getBaseDir();
		String externalReference = null;
		Set<String> visitedPaths = new HashSet<>();

		Queue<String> queue = new LinkedList<>(
			getExternalReferences(openAPIYAML));

		while ((externalReference = queue.poll()) != null) {
			File externalFile = _resolveExternalFile(
				baseDir, externalReference);

			if (!visitedPaths.add(externalFile.getPath())) {
				continue;
			}

			openAPIYAML = YAMLUtil.loadOpenAPIYAML(externalFile);

			externalReferencesMap.putAll(
				OpenAPIUtil.getAllSchemas(configYAML, openAPIYAML));

			String parentPath = externalFile.getParent() + "/";

			for (String curExternalReference :
					getExternalReferences(openAPIYAML)) {

				queue.add(parentPath + curExternalReference);
			}
		}

		return externalReferencesMap;
	}

	public static String getHTTPMethod(Operation operation) {
		Class<? extends Operation> clazz = operation.getClass();

		return StringUtil.lowerCase(clazz.getSimpleName());
	}

	public static String getJavaDataType(
		Map<String, String> javaDataTypeMap, Schema schema) {

		if (schema.getAllOfSchemas() != null) {
			for (Schema allOfSchema : schema.getAllOfSchemas()) {
				if (Validator.isNotNull(allOfSchema.getReference())) {
					return javaDataTypeMap.get(
						getReferenceName(allOfSchema.getReference()));
				}
			}
		}

		if ((schema.getAnyOfSchemas() != null) ||
			(schema.getOneOfSchemas() != null)) {

			return Object.class.getName();
		}

		if (schema.getItems() != null) {
			return _getItemsDataType(javaDataTypeMap, schema.getItems());
		}

		if (Objects.equals(schema.getType(), "object")) {
			return _getMapType(
				javaDataTypeMap, schema.getAdditionalPropertySchema());
		}

		if (schema.getReference() != null) {
			return javaDataTypeMap.get(getReferenceName(schema.getReference()));
		}

		String type = _openAPIDataTypeMap.get(
			new AbstractMap.SimpleImmutableEntry<>(
				schema.getType(), schema.getFormat()));

		if (type == null) {
			throw new RuntimeException(
				StringBundler.concat(
					"Unsupported combination of type/format: ",
					schema.getType(), "/", schema.getFormat()));
		}

		return type;
	}

	public static Map<String, String> getJavaDataTypeMap(
		ConfigYAML configYAML, OpenAPIYAML openAPIYAML) {

		Map<String, String> javaDataTypeMap = new TreeMap<>();

		String baseDir = configYAML.getBaseDir();
		Set<String> visitedPaths = new HashSet<>();

		try {
			for (String externalReference :
					getExternalReferences(openAPIYAML)) {

				File externalFile = _resolveExternalFile(
					baseDir, externalReference);

				if (!visitedPaths.add(externalFile.getPath())) {
					continue;
				}

				File configFile = new File(
					externalFile.getParent(), "rest-config.yaml");

				ConfigYAML externalConfigYAML = YAMLUtil.loadConfigYAML(
					externalFile.getParent(), configFile);

				OpenAPIYAML externalOpenAPIYAML = YAMLUtil.loadOpenAPIYAML(
					externalFile);

				if ((externalConfigYAML == null) ||
					(externalOpenAPIYAML == null)) {

					continue;
				}

				Map<String, String> externalJavaDataTypeMap =
					getJavaDataTypeMap(externalConfigYAML, externalOpenAPIYAML);

				javaDataTypeMap.putAll(externalJavaDataTypeMap);
			}
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		Map<String, Schema> allSchemas = OpenAPIUtil.getAllSchemas(
			configYAML, openAPIYAML);

		for (String schemaName : allSchemas.keySet()) {
			StringBuilder sb = new StringBuilder();

			sb.append(configYAML.getApiPackagePath());
			sb.append(".dto.");
			sb.append(OpenAPIUtil.escapeVersion(openAPIYAML));
			sb.append('.');
			sb.append(schemaName);

			javaDataTypeMap.put(schemaName, sb.toString());

			sb.setLength(0);

			sb.append(configYAML.getApiPackagePath());
			sb.append(".resource.");
			sb.append(OpenAPIUtil.escapeVersion(openAPIYAML));
			sb.append('.');
			sb.append(schemaName);
			sb.append("Resource");

			javaDataTypeMap.put(schemaName + "Resource", sb.toString());

			sb.setLength(0);

			sb.append(configYAML.getApiPackagePath());
			sb.append(".internal.resource.");
			sb.append(OpenAPIUtil.escapeVersion(openAPIYAML));
			sb.append('.');
			sb.append(schemaName);
			sb.append("ResourceImpl");

			javaDataTypeMap.put(schemaName + "ResourceImpl", sb.toString());
		}

		Map<String, Schema> globalEnumSchemas =
			OpenAPIUtil.getGlobalEnumSchemas(configYAML, openAPIYAML);

		for (String schemaName : globalEnumSchemas.keySet()) {
			javaDataTypeMap.put(
				schemaName,
				StringBundler.concat(
					configYAML.getApiPackagePath(), ".constant.",
					OpenAPIUtil.escapeVersion(openAPIYAML), '.', schemaName));
		}

		return javaDataTypeMap;
	}

	public static List<Operation> getOperations(PathItem pathItem) {
		List<Operation> operations = new ArrayList<>();

		if (pathItem.getDelete() != null) {
			operations.add(pathItem.getDelete());
		}

		if (pathItem.getGet() != null) {
			operations.add(pathItem.getGet());
		}

		if (pathItem.getHead() != null) {
			operations.add(pathItem.getHead());
		}

		if (pathItem.getOptions() != null) {
			operations.add(pathItem.getOptions());
		}

		if (pathItem.getPatch() != null) {
			operations.add(pathItem.getPatch());
		}

		if (pathItem.getPost() != null) {
			operations.add(pathItem.getPost());
		}

		if (pathItem.getPut() != null) {
			operations.add(pathItem.getPut());
		}

		return operations;
	}

	public static String getParameter(
		JavaMethodParameter javaMethodParameter, String parameterAnnotation) {

		StringBundler sb = new StringBundler(6);

		if (Validator.isNotNull(parameterAnnotation)) {
			sb.append(parameterAnnotation);
			sb.append(' ');
		}

		String parameterType = javaMethodParameter.getParameterType();

		if (parameterType.startsWith("[")) {
			sb.append(getElementClassName(parameterType));
			sb.append("[]");
		}
		else {
			sb.append(parameterType);
		}

		sb.append(' ');
		sb.append(javaMethodParameter.getParameterName());

		return sb.toString();
	}

	public static String getReferenceName(String reference) {
		if (!reference.contains("#/components/parameters") &&
			!reference.contains("#/components/schemas")) {

			return reference.substring(reference.lastIndexOf("#") + 1);
		}

		int index = reference.lastIndexOf('/');

		if (index == -1) {
			return reference;
		}

		return reference.substring(index + 1);
	}

	public static Set<String> getSchemaNames(
		List<JavaMethodSignature> javaMethodSignatures) {

		Set<String> schemaNames = new TreeSet<>();

		for (JavaMethodSignature javaMethodSignature : javaMethodSignatures) {
			schemaNames.add(javaMethodSignature.getSchemaName());
		}

		return schemaNames;
	}

	public static String getSchemaVarName(String schemaName) {
		return TextFormatter.format(schemaName, TextFormatter.I);
	}

	public static String getVersion(OpenAPIYAML openAPIYAML) {
		Info info = openAPIYAML.getInfo();

		return info.getVersion();
	}

	public static boolean hasHTTPMethod(
		JavaMethodSignature javaMethodSignature, String... httpMethods) {

		Operation operation = javaMethodSignature.getOperation();

		for (String httpMethod : httpMethods) {
			if (Objects.equals(httpMethod, getHTTPMethod(operation))) {
				return true;
			}
		}

		return false;
	}

	public static OpenAPIYAML loadOpenAPIYAML(String yamlString) {
		OpenAPIYAML openAPIYAML = YAMLUtil.loadOpenAPIYAML(yamlString);

		return _openAPIYAMLs.computeIfAbsent(
			openAPIYAML,
			keyOpenAPIYAML -> {
				Map<String, PathItem> pathItems = keyOpenAPIYAML.getPathItems();

				if (pathItems == null) {
					return keyOpenAPIYAML;
				}

				Components components = keyOpenAPIYAML.getComponents();

				if (components == null) {
					return keyOpenAPIYAML;
				}

				Map<String, Parameter> parameterMap =
					components.getParameters();

				for (Map.Entry<String, PathItem> entry : pathItems.entrySet()) {
					PathItem pathItem = entry.getValue();

					List<Operation> operations = new ArrayList<>();

					if (pathItem.getDelete() != null) {
						operations.add(pathItem.getDelete());
					}

					if (pathItem.getGet() != null) {
						operations.add(pathItem.getGet());
					}

					if (pathItem.getHead() != null) {
						operations.add(pathItem.getHead());
					}

					if (pathItem.getOptions() != null) {
						operations.add(pathItem.getOptions());
					}

					if (pathItem.getPatch() != null) {
						operations.add(pathItem.getPatch());
					}

					if (pathItem.getPost() != null) {
						operations.add(pathItem.getPost());
					}

					if (pathItem.getPut() != null) {
						operations.add(pathItem.getPut());
					}

					for (Operation operation : operations) {
						List<Parameter> parameters = operation.getParameters();

						for (int i = 0; i < parameters.size(); i++) {
							Parameter parameter = parameters.get(i);

							if (Validator.isNull(parameter.getReference())) {
								continue;
							}

							String key = getReferenceName(
								parameter.getReference());

							Parameter curParameter = parameterMap.get(key);

							if (curParameter != null) {
								parameters.set(i, curParameter);
							}
						}
					}
				}

				return keyOpenAPIYAML;
			});
	}

	private static void _addExternalReferences(
		Map<String, Content> contents, Set<String> externalReferences,
		Map<String, Schema> schemas) {

		if (contents == null) {
			return;
		}

		for (Content content : contents.values()) {
			_addExternalReferences(
				externalReferences, content.getSchema(), schemas);
		}
	}

	private static void _addExternalReferences(
		Set<String> externalReferences, Schema schema,
		Map<String, Schema> schemas) {

		if (schema == null) {
			return;
		}

		Queue<Map<String, Schema>> queue = new LinkedList<>();

		queue.add(Collections.singletonMap("content", schema));

		Map<String, Schema> map = null;
		Set<String> visited = new HashSet<>();

		while ((map = queue.poll()) != null) {
			for (Map.Entry<String, Schema> entry : map.entrySet()) {
				if (visited.contains(entry.getKey())) {
					continue;
				}

				Schema currentSchema = entry.getValue();

				String reference = _getReference(currentSchema);

				if (reference != null) {
					if (reference.contains("#/components/schemas/")) {
						String referenceName = getReferenceName(reference);

						Schema referenceSchema = schemas.get(referenceName);

						if (referenceSchema != null) {
							queue.add(
								Collections.singletonMap(
									referenceName, referenceSchema));
							visited.add(entry.getKey());
						}
					}
					else {
						externalReferences.add(reference);
					}
				}
				else if (currentSchema.getAllOfSchemas() != null) {
					List<Schema> allOfSchemas = currentSchema.getAllOfSchemas();

					queue.add(
						Collections.singletonMap(
							"allOf" + entry.getKey(), allOfSchemas.get(0)));

					visited.add(entry.getKey());
				}
				else if (currentSchema.getPropertySchemas() != null) {
					queue.add(currentSchema.getPropertySchemas());
					visited.add(entry.getKey());
				}
			}
		}
	}

	private static String _getItemsDataType(
		Map<String, String> javaDataTypeMap, Items items) {

		String type = items.getType();

		if (StringUtil.equals(type, "array")) {
			Items childItems = items.getItems();

			if (childItems != null) {
				return "[" + _getItemsDataType(javaDataTypeMap, childItems);
			}
		}

		String javaDataType = _openAPIDataTypeMap.get(
			new AbstractMap.SimpleImmutableEntry<>(type, items.getFormat()));

		if (items.getAdditionalPropertySchema() != null) {
			javaDataType = Map.class.getName();
		}

		if (items.getReference() != null) {
			javaDataType = javaDataTypeMap.get(
				getReferenceName(items.getReference()));
		}

		return getArrayClassName(javaDataType);
	}

	private static String _getMapType(
		Map<String, String> javaDataTypeMap, Schema schema) {

		if (schema != null) {
			if (schema.getReference() != null) {
				String referenceType = javaDataTypeMap.get(
					getReferenceName(schema.getReference()));

				return "Map<String, " + referenceType + ">";
			}

			AbstractMap.SimpleImmutableEntry<String, String> key =
				new AbstractMap.SimpleImmutableEntry<>(
					schema.getType(), schema.getFormat());

			if (_openAPIDataTypeMap.containsKey(key)) {
				String additionalJavaDataType = getJavaDataType(
					javaDataTypeMap, schema);

				return "Map<String, " + additionalJavaDataType + ">";
			}
		}

		return Object.class.getName();
	}

	private static String _getReference(Schema schema) {
		if (schema.getReference() != null) {
			return schema.getReference();
		}

		Items items = schema.getItems();

		if (items != null) {
			return items.getReference();
		}

		return null;
	}

	private static File _resolveExternalFile(
			String baseDir, String externalReference)
		throws IOException {

		String path = externalReference.substring(
			0, externalReference.indexOf("#"));

		File externalFile = new File(path);

		if (!externalFile.isAbsolute()) {
			externalFile = new File(baseDir, path);
		}

		return externalFile.getCanonicalFile();
	}

	private static final Map<OpenAPIYAML, List<String>> _externalReferencesMap =
		new ConcurrentHashMap<>();

	private static final Map<Map.Entry<String, String>, String>
		_openAPIDataTypeMap = new HashMap<Map.Entry<String, String>, String>() {
			{
				put(
					new AbstractMap.SimpleImmutableEntry<>("boolean", null),
					Boolean.class.getName());
				put(
					new AbstractMap.SimpleImmutableEntry<>("customField", null),
					"com.liferay.portal.vulcan.custom.field.CustomField");
				put(
					new AbstractMap.SimpleImmutableEntry<>("integer", "int32"),
					Integer.class.getName());
				put(
					new AbstractMap.SimpleImmutableEntry<>("integer", "int64"),
					Long.class.getName());
				put(
					new AbstractMap.SimpleImmutableEntry<>("number", "float"),
					Float.class.getName());
				put(
					new AbstractMap.SimpleImmutableEntry<>("number", "double"),
					Double.class.getName());
				put(
					new AbstractMap.SimpleImmutableEntry<>("object", null),
					Object.class.getName());
				put(
					new AbstractMap.SimpleImmutableEntry<>("permission", null),
					"com.liferay.portal.vulcan.permission.Permission");
				put(
					new AbstractMap.SimpleImmutableEntry<>("scope", null),
					"com.liferay.portal.vulcan.scope.Scope");
				put(
					new AbstractMap.SimpleImmutableEntry<>(
						"sseEventSink", null),
					"jakarta.ws.rs.sse.SseEventSink");
				put(
					new AbstractMap.SimpleImmutableEntry<>("string", null),
					String.class.getName());
				put(
					new AbstractMap.SimpleImmutableEntry<>("string", "byte"),
					String.class.getName());
				put(
					new AbstractMap.SimpleImmutableEntry<>("string", "binary"),
					String.class.getName());
				put(
					new AbstractMap.SimpleImmutableEntry<>("string", "date"),
					Date.class.getName());
				put(
					new AbstractMap.SimpleImmutableEntry<>(
						"string", "date-time"),
					Date.class.getName());
				put(
					new AbstractMap.SimpleImmutableEntry<>(
						"string", "password"),
					String.class.getName());

				// Liferay

				put(new AbstractMap.SimpleImmutableEntry<>("?", null), "?");
				put(
					new AbstractMap.SimpleImmutableEntry<>("integer", null),
					Integer.class.getName());
				put(
					new AbstractMap.SimpleImmutableEntry<>("number", null),
					Number.class.getName());
				put(
					new AbstractMap.SimpleImmutableEntry<>(
						"number", "bigdecimal"),
					BigDecimal.class.getName());
				put(
					new AbstractMap.SimpleImmutableEntry<>("string", "email"),
					String.class.getName());
				put(
					new AbstractMap.SimpleImmutableEntry<>("string", "uri"),
					String.class.getName());
			}
		};

	private static final Map<OpenAPIYAML, OpenAPIYAML> _openAPIYAMLs =
		new ConcurrentHashMap<>();

}