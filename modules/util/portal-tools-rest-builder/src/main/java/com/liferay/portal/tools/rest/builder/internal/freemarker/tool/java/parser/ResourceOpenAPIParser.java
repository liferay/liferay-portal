/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.internal.freemarker.tool.java.parser;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.util.CamelCaseUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TextFormatter;
import com.liferay.portal.kernel.util.TreeMapBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.tools.rest.builder.internal.freemarker.tool.java.JavaMethodParameter;
import com.liferay.portal.tools.rest.builder.internal.freemarker.tool.java.JavaMethodSignature;
import com.liferay.portal.tools.rest.builder.internal.freemarker.tool.java.parser.util.OpenAPIParserUtil;
import com.liferay.portal.tools.rest.builder.internal.freemarker.util.ConfigUtil;
import com.liferay.portal.tools.rest.builder.internal.freemarker.util.OpenAPIUtil;
import com.liferay.portal.tools.rest.builder.internal.yaml.config.ConfigYAML;
import com.liferay.portal.tools.rest.builder.internal.yaml.openapi.Content;
import com.liferay.portal.tools.rest.builder.internal.yaml.openapi.Delete;
import com.liferay.portal.tools.rest.builder.internal.yaml.openapi.Get;
import com.liferay.portal.tools.rest.builder.internal.yaml.openapi.OpenAPIYAML;
import com.liferay.portal.tools.rest.builder.internal.yaml.openapi.Operation;
import com.liferay.portal.tools.rest.builder.internal.yaml.openapi.Parameter;
import com.liferay.portal.tools.rest.builder.internal.yaml.openapi.PathItem;
import com.liferay.portal.tools.rest.builder.internal.yaml.openapi.Post;
import com.liferay.portal.tools.rest.builder.internal.yaml.openapi.Put;
import com.liferay.portal.tools.rest.builder.internal.yaml.openapi.RequestBody;
import com.liferay.portal.tools.rest.builder.internal.yaml.openapi.Response;
import com.liferay.portal.tools.rest.builder.internal.yaml.openapi.ResponseCode;
import com.liferay.portal.tools.rest.builder.internal.yaml.openapi.Schema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;

/**
 * @author Peter Shin
 */
public class ResourceOpenAPIParser {

	public static List<JavaMethodSignature> getJavaMethodSignatures(
		ConfigYAML configYAML, OpenAPIYAML openAPIYAML, String schemaName) {

		Map<String, PathItem> pathItems = openAPIYAML.getPathItems();

		if (pathItems == null) {
			return Collections.emptyList();
		}

		Map<String, String> javaDataTypeMap =
			OpenAPIParserUtil.getJavaDataTypeMap(configYAML, openAPIYAML);
		List<JavaMethodSignature> javaMethodSignatures = new ArrayList<>();

		for (Map.Entry<String, PathItem> entry : pathItems.entrySet()) {
			String path = entry.getKey();
			PathItem pathItem = entry.getValue();

			_visitOperations(
				pathItem,
				operation -> {
					String returnType = _getReturnType(
						configYAML, javaDataTypeMap, operation, path);

					if (!_isSchemaMethod(
							javaDataTypeMap, returnType, schemaName,
							operation.getTags())) {

						return;
					}

					_visitRequestBodyMediaTypes(
						operation.getRequestBody(),
						requestBodyMediaTypes -> {
							String operationId = _getOperationId(
								configYAML, operation, path, returnType,
								schemaName,
								configYAML.isForcePredictableOperationId());

							operation.setOperationId(operationId);

							List<JavaMethodParameter> javaMethodParameters =
								_getJavaMethodParameters(
									javaDataTypeMap, operation,
									requestBodyMediaTypes);

							JavaMethodSignature javaMethodSignature =
								new JavaMethodSignature(
									path, pathItem, operation,
									requestBodyMediaTypes, schemaName,
									javaMethodParameters,
									_getMethodName(
										configYAML, javaMethodParameters,
										operationId),
									returnType,
									_getParentSchema(
										path, pathItems, schemaName));

							javaMethodSignatures.add(javaMethodSignature);

							if (configYAML.isGenerateBatch()) {
								_addBatchJavaMethodSignature(
									configYAML, javaMethodSignature,
									javaMethodSignatures);
							}
						});
				});
		}

		javaMethodSignatures.sort(
			Comparator.comparing(JavaMethodSignature::getMethodName));

		return javaMethodSignatures;
	}

	public static String getMethodAnnotations(
		ConfigYAML configYAML, JavaMethodSignature javaMethodSignature) {

		String path = javaMethodSignature.getPath();
		Operation operation = javaMethodSignature.getOperation();

		Set<String> methodAnnotations = new TreeSet<>();

		if ((operation.getDescription() != null) || operation.isDeprecated()) {
			StringBundler sb = new StringBundler(
				"@io.swagger.v3.oas.annotations.Operation(");

			if (operation.isDeprecated()) {
				methodAnnotations.add("@Deprecated");
				sb.append("deprecated=true");
			}

			if (operation.getDescription() != null) {
				if (operation.isDeprecated()) {
					sb.append(", ");
				}

				sb.append("description=\"");
				sb.append(operation.getDescription());
				sb.append("\"");

				if (!StringUtil.equals(
						javaMethodSignature.getMethodName(),
						operation.getOperationId())) {

					sb.append(", operationId=\"");
					sb.append(operation.getOperationId());
					sb.append("\"");
				}

				if (getMultipartBodySchemas(javaMethodSignature) != null) {
					sb.append(", requestBody = ");
					sb.append("@io.swagger.v3.oas.annotations.parameters.");
					sb.append("RequestBody(content = ");
					sb.append(_getRequestBodyContent(javaMethodSignature));
					sb.append(")");
				}
			}

			sb.append(")");

			methodAnnotations.add(sb.toString());
		}
		else if (getMultipartBodySchemas(javaMethodSignature) != null) {
			StringBundler sb = new StringBundler(
				"@io.swagger.v3.oas.annotations.Operation(");

			sb.append("requestBody = ");
			sb.append("@io.swagger.v3.oas.annotations.parameters.");
			sb.append("RequestBody(content = ");
			sb.append(_getRequestBodyContent(javaMethodSignature));
			sb.append("))");

			methodAnnotations.add(sb.toString());
		}

		if (operation.getTags() != null) {
			StringBundler sb = new StringBundler("");

			for (String tag : operation.getTags()) {
				sb.append("@io.swagger.v3.oas.annotations.tags.Tag(name=\"");
				sb.append(tag);
				sb.append("\"),");
			}

			methodAnnotations.add(
				"@io.swagger.v3.oas.annotations.tags.Tags(value={" +
					sb.toString() + "})");
		}

		StringBundler sb = new StringBundler("");

		for (Parameter parameter : operation.getParameters()) {
			if (StringUtil.equals(parameter.getIn(), "header")) {
				continue;
			}

			sb.append(
				_addParameter(_findParameter(operation, parameter.getName())));
		}

		if (sb.length() > 0) {
			methodAnnotations.add(
				"@io.swagger.v3.oas.annotations.Parameters(value={" + sb +
					"})");
		}

		for (JavaMethodParameter pathJavaMethodParameter :
				javaMethodSignature.getPathJavaMethodParameters()) {

			String parameterName = pathJavaMethodParameter.getParameterName();

			if (parameterName.endsWith("Path") &&
				Objects.equals(
					pathJavaMethodParameter.getParameterType(),
					"java.lang.String")) {

				path = StringUtil.replace(
					path, "{" + parameterName + "}",
					"{" + parameterName + ": .+}");
			}
		}

		methodAnnotations.add(
			StringBundler.concat(
				"@", configYAML.getJavaEEPackage(), ".ws.rs.Path(\"", path,
				"\")"));

		String annotationString = StringUtil.toUpperCase(
			OpenAPIParserUtil.getHTTPMethod(operation));

		methodAnnotations.add(
			StringBundler.concat(
				"@", configYAML.getJavaEEPackage(), ".ws.rs.",
				annotationString));

		String methodAnnotation = _getMethodAnnotationConsumes(
			configYAML, javaMethodSignature.getRequestBodyMediaTypes());

		if (Validator.isNotNull(methodAnnotation)) {
			methodAnnotations.add(methodAnnotation);
		}

		methodAnnotation = _getMethodAnnotationProduces(configYAML, operation);

		if (Validator.isNotNull(methodAnnotation)) {
			methodAnnotations.add(methodAnnotation);
		}

		return StringUtil.merge(methodAnnotations, "\n");
	}

	public static Map<String, Schema> getMultipartBodySchemas(
		JavaMethodSignature javaMethodSignature) {

		Operation operation = javaMethodSignature.getOperation();

		RequestBody requestBody = operation.getRequestBody();

		if (requestBody == null) {
			return null;
		}

		Map<String, Content> contentMap = requestBody.getContent();

		Content content = contentMap.get("multipart/form-data");

		if (content == null) {
			return null;
		}

		Schema schema = content.getSchema();

		return schema.getPropertySchemas();
	}

	public static String getParameters(
		ConfigYAML configYAML, List<JavaMethodParameter> javaMethodParameters,
		Operation operation, Map<String, Schema> schemas, boolean annotation) {

		StringBuilder sb = new StringBuilder();

		for (JavaMethodParameter javaMethodParameter : javaMethodParameters) {
			String parameterAnnotation = null;

			if (annotation) {
				parameterAnnotation = _getParameterAnnotation(
					configYAML, javaMethodParameter, operation, schemas);
			}

			sb.append(
				OpenAPIParserUtil.getParameter(
					javaMethodParameter, parameterAnnotation));
			sb.append(',');
		}

		if (sb.length() > 0) {
			sb.setLength(sb.length() - 1);
		}

		return sb.toString();
	}

	public static List<JavaMethodSignature>
		getResourceGetPageJavaMethodSignatures(
			List<JavaMethodSignature> javaMethodSignatures) {

		return TransformUtil.transform(
			javaMethodSignatures,
			javaMethodSignature -> {
				if (StringUtil.startsWith(
						javaMethodSignature.getReturnType(),
						"com.liferay.portal.vulcan.pagination.Page<")) {

					return javaMethodSignature;
				}

				return null;
			});
	}

	public static String getResourceMethodName(
		List<JavaMethodSignature> javaMethodSignatures, String propertyName) {

		for (JavaMethodSignature javaMethodSignature : javaMethodSignatures) {
			String methodName = javaMethodSignature.getMethodName();
			String schemaName = javaMethodSignature.getSchemaName();

			if (StringUtil.equals(propertyName, "delete")) {
				if (StringUtil.equals(methodName, "delete" + schemaName)) {
					return methodName;
				}
			}
			else if (StringUtil.equals(propertyName, "get")) {
				if (StringUtil.equals(methodName, "get" + schemaName)) {
					return methodName;
				}
			}
			else if (StringUtil.equals(propertyName, "update")) {
				if (StringUtil.equals(methodName, "patch" + schemaName)) {
					return methodName;
				}
			}
			else if (StringUtil.equals(propertyName, "replace")) {
				if (StringUtil.equals(methodName, "put" + schemaName)) {
					return methodName;
				}
			}
		}

		return null;
	}

	public static Set<String> getVulcanBatchImplementationCreateStrategies(
		List<JavaMethodSignature> javaMethodSignatures,
		Map<String, String> properties) {

		Set<String> createStrategies = new TreeSet<>();

		Set<String> propertyNames = properties.keySet();

		for (JavaMethodSignature javaMethodSignature : javaMethodSignatures) {
			String methodName = javaMethodSignature.getMethodName();
			String parentSchemaName = javaMethodSignature.getParentSchemaName();
			String schemaName = javaMethodSignature.getSchemaName();

			if (parentSchemaName == null) {
				parentSchemaName = "";
			}

			if (methodName.equals("post" + parentSchemaName + schemaName) ||
				isExternalReferenceCodeMethod("post", javaMethodSignature)) {

				createStrategies.add("INSERT");
			}
			else if (propertyNames.contains("externalReferenceCode") &&
					 isExternalReferenceCodeMethod(
						 "put", javaMethodSignature)) {

				createStrategies.add("UPSERT");
			}
		}

		return createStrategies;
	}

	public static Set<String> getVulcanBatchImplementationUpdateStrategies(
		List<JavaMethodSignature> javaMethodSignatures) {

		Set<String> updateStrategies = new TreeSet<>();

		for (JavaMethodSignature javaMethodSignature : javaMethodSignatures) {
			String methodName = javaMethodSignature.getMethodName();

			String schemaName = javaMethodSignature.getSchemaName();

			if (hasPathParameter(
					javaMethodSignature,
					OpenAPIParserUtil.getSchemaVarName(schemaName) +
						"ExternalReferenceCode")) {

				continue;
			}

			if (methodName.equals("patch" + schemaName)) {
				updateStrategies.add("PARTIAL_UPDATE");
			}
			else if (methodName.equals("put" + schemaName)) {
				updateStrategies.add("UPDATE");
			}
		}

		return updateStrategies;
	}

	public static boolean hasPathParameter(
		JavaMethodSignature javaMethodSignature, String parameterName) {

		List<JavaMethodParameter> javaMethodParameters =
			javaMethodSignature.getPathJavaMethodParameters();

		for (JavaMethodParameter javaMethodParameter : javaMethodParameters) {
			if (parameterName.equals(javaMethodParameter.getParameterName())) {
				return true;
			}
		}

		return false;
	}

	public static boolean hasReadVulcanBatchImplementation(
		List<JavaMethodSignature> javaMethodSignatures) {

		for (JavaMethodSignature javaMethodSignature : javaMethodSignatures) {
			String methodName = javaMethodSignature.getMethodName();
			String parentSchemaName = javaMethodSignature.getParentSchemaName();
			String schemaName = javaMethodSignature.getSchemaName();

			if (parentSchemaName == null) {
				parentSchemaName = "";
			}

			if (methodName.equals(
					StringBundler.concat(
						"get", parentSchemaName,
						TextFormatter.formatPlural(schemaName), "Page"))) {

				return true;
			}
		}

		return false;
	}

	public static boolean hasResourceBatchJavaMethodSignatures(
		List<JavaMethodSignature> javaMethodSignatures) {

		for (JavaMethodSignature javaMethodSignature : javaMethodSignatures) {
			String methodName = javaMethodSignature.getMethodName();

			if (methodName.endsWith("Batch")) {
				return true;
			}
		}

		return false;
	}

	public static boolean hasResourceGetPageJavaMethodSignature(
		String javaDataType, List<JavaMethodSignature> javaMethodSignatures) {

		String pageJavaDataType = StringBundler.concat(
			"com.liferay.portal.vulcan.pagination.Page<", javaDataType, ">");

		for (JavaMethodSignature javaMethodSignature :
				getResourceGetPageJavaMethodSignatures(javaMethodSignatures)) {

			if (StringUtil.equals(
					pageJavaDataType, javaMethodSignature.getReturnType())) {

				return true;
			}
		}

		return false;
	}

	public static boolean isExternalReferenceCodeMethod(
		String httpMethod, JavaMethodSignature javaMethodSignature) {

		Set<String> validMethodNames = new HashSet<>();

		validMethodNames.add(httpMethod + "ByExternalReferenceCode");

		String parentSchemaName = GetterUtil.getString(
			javaMethodSignature.getParentSchemaName());
		String schemaName = GetterUtil.getString(
			javaMethodSignature.getSchemaName());

		validMethodNames.add(
			StringBundler.concat(
				httpMethod, parentSchemaName, "ByExternalReferenceCode",
				schemaName));
		validMethodNames.add(
			StringBundler.concat(
				httpMethod, parentSchemaName, schemaName,
				"ByExternalReferenceCode"));

		if (hasPathParameter(
				javaMethodSignature,
				OpenAPIParserUtil.getSchemaVarName(schemaName) +
					"ExternalReferenceCode")) {

			validMethodNames.add(httpMethod + schemaName);

			if (hasPathParameter(
					javaMethodSignature,
					OpenAPIParserUtil.getSchemaVarName(parentSchemaName) +
						"ExternalReferenceCode")) {

				validMethodNames.add(
					StringBundler.concat(
						httpMethod, parentSchemaName, schemaName));
			}
		}

		return validMethodNames.contains(javaMethodSignature.getMethodName());
	}

	private static void _addBatchJavaMethodSignature(
		ConfigYAML configYAML, JavaMethodSignature javaMethodSignature,
		List<JavaMethodSignature> javaMethodSignatures) {

		BatchOperationType batchOperationType = null;

		String methodName = javaMethodSignature.getMethodName();
		String parentSchemaName = GetterUtil.getString(
			javaMethodSignature.getParentSchemaName());
		String schemaName = javaMethodSignature.getSchemaName();

		if (ConfigUtil.isVersionCompatible(configYAML, 2) &&
			methodName.equals(
				StringBundler.concat(
					"get", parentSchemaName,
					TextFormatter.formatPlural(schemaName), "Page"))) {

			batchOperationType = BatchOperationType.EXPORT;
		}
		else if (methodName.equals("delete" + schemaName) ||
				 methodName.equals("post" + parentSchemaName + schemaName) ||
				 methodName.equals(
					 StringBundler.concat(
						 "post", parentSchemaName, "Id", schemaName)) ||
				 methodName.equals("put" + schemaName)) {

			batchOperationType = BatchOperationType.IMPORT;
		}
		else {
			return;
		}

		Operation batchOperation = _getBatchOperation(
			batchOperationType, configYAML, javaMethodSignature, methodName,
			schemaName);

		String batchPath = _getBatchPath(
			batchOperationType, configYAML, javaMethodSignature.getPath(),
			schemaName);

		for (JavaMethodSignature existingJavaMethodSignature :
				javaMethodSignatures) {

			String httpMethod = OpenAPIParserUtil.getHTTPMethod(
				existingJavaMethodSignature.getOperation());

			if (Objects.equals(
					existingJavaMethodSignature.getPath(), batchPath) &&
				httpMethod.equals(
					OpenAPIParserUtil.getHTTPMethod(batchOperation))) {

				return;
			}
		}

		List<JavaMethodParameter> javaMethodParameters = new ArrayList<>();

		for (JavaMethodParameter javaMethodParameter :
				javaMethodSignature.getJavaMethodParameters()) {

			if (_isValidParameter(
					configYAML, javaMethodParameter.getParameterName(),
					schemaName)) {

				javaMethodParameters.add(javaMethodParameter);
			}
		}

		javaMethodParameters.add(
			new JavaMethodParameter("callbackURL", "String"));

		if (batchOperationType == BatchOperationType.EXPORT) {
			javaMethodParameters.add(
				new JavaMethodParameter("contentType", "String"));
			javaMethodParameters.add(
				new JavaMethodParameter("fieldNames", "String"));
		}
		else if (batchOperationType == BatchOperationType.IMPORT) {
			javaMethodParameters.add(
				new JavaMethodParameter("object", "Object"));
		}

		javaMethodSignatures.add(
			new JavaMethodSignature(
				batchPath, javaMethodSignature.getPathItem(), batchOperation,
				Collections.singleton(ContentTypes.APPLICATION_JSON),
				schemaName, javaMethodParameters,
				_getBatchMethodName(batchOperationType, methodName),
				configYAML.getJavaEEPackage() + ".ws.rs.core.Response",
				parentSchemaName));
	}

	private static String _addParameter(Parameter parameter) {
		if ((parameter == null) ||
			Objects.equals(parameter.getIn(), "context")) {

			return "";
		}

		StringBundler sb = new StringBundler(5);

		sb.append(
			StringBundler.concat(
				"@io.swagger.v3.oas.annotations.Parameter(in = ",
				"io.swagger.v3.oas.annotations.enums.ParameterIn.",
				StringUtil.toUpperCase(parameter.getIn()), ", name = \"",
				parameter.getName(), "\""));

		if (parameter.isDeprecated()) {
			sb.append(
				String.format(", deprecated = %s", parameter.isDeprecated()));
		}

		if (parameter.getDescription() != null) {
			sb.append(
				String.format(
					", description = \"%s\"",
					_escapeAnnotationValue(parameter.getDescription())));
		}

		if (parameter.getExample() != null) {
			sb.append(
				String.format(", example = \"%s\"", parameter.getExample()));
		}

		sb.append("),");

		return sb.toString();
	}

	private static String _escapeAnnotationValue(String value) {
		if (value == null) {
			return null;
		}

		String escaped = StringUtil.replace(value, "\\", "\\\\");

		escaped = StringUtil.replace(escaped, "\"", "\\\"");
		escaped = StringUtil.replace(escaped, "\n", "\\n");
		escaped = StringUtil.replace(escaped, "\r", "\\r");

		return escaped;
	}

	private static Parameter _findParameter(
		Operation operation, String parameterName) {

		for (Parameter parameter : operation.getParameters()) {
			if (parameterName.equals(parameter.getName())) {
				return parameter;
			}
		}

		return null;
	}

	private static String _getBatchMethodName(
		BatchOperationType batchOperationType, String methodName) {

		if (batchOperationType == BatchOperationType.EXPORT) {
			return StringUtil.replaceFirst(methodName, "get", "post") +
				"ExportBatch";
		}
		else if (batchOperationType == BatchOperationType.IMPORT) {
			return methodName + "Batch";
		}

		throw new IllegalStateException(
			"Unsupported batch operation type: " + batchOperationType);
	}

	private static Operation _getBatchOperation(
		BatchOperationType batchOperationType, ConfigYAML configYAML,
		JavaMethodSignature javaMethodSignature, String methodName,
		String schemaName) {

		Operation batchOperation = null;

		if (methodName.startsWith("delete")) {
			batchOperation = new Delete();
		}
		else if (methodName.startsWith("get")) {
			batchOperation = new Post();
		}
		else if (methodName.startsWith("post")) {
			batchOperation = new Post();
		}
		else {
			batchOperation = new Put();
		}

		Operation operation = javaMethodSignature.getOperation();

		if (batchOperation.getOperationId() != null) {
			batchOperation.setOperationId(operation.getOperationId() + "Batch");
		}

		if (operation.isDeprecated()) {
			batchOperation.setDeprecated(true);
		}

		batchOperation.setParameters(
			_getBatchParameters(
				batchOperationType, configYAML, operation, schemaName));
		batchOperation.setTags(operation.getTags());

		Response response = new Response();

		Content content = new Content();

		content.setSchema(new Schema());

		response.setContent(
			Collections.singletonMap("application/json", content));

		batchOperation.setResponses(
			HashMapBuilder.put(
				new ResponseCode("200"), response
			).build());

		return batchOperation;
	}

	private static List<Parameter> _getBatchParameters(
		BatchOperationType batchOperationType, ConfigYAML configYAML,
		Operation operation, String schemaName) {

		List<Parameter> parameters = new ArrayList<>();

		for (Parameter parameter : operation.getParameters()) {
			if (_isValidParameter(
					configYAML, parameter.getName(), schemaName)) {

				parameters.add(parameter);
			}
		}

		parameters.add(_getQueryParameter(null, "callbackURL"));

		if (batchOperationType == BatchOperationType.EXPORT) {
			parameters.add(_getQueryParameter("JSON", "contentType"));
			parameters.add(_getQueryParameter(null, "fieldNames"));
		}

		return parameters;
	}

	private static String _getBatchPath(
		BatchOperationType batchOperationType, ConfigYAML configYAML,
		String path, String schemaName) {

		if (batchOperationType == BatchOperationType.EXPORT) {
			return path + "/export-batch";
		}
		else if (batchOperationType == BatchOperationType.IMPORT) {
			String schemaVarName = StringUtil.lowerCaseFirstLetter(schemaName);

			if (ConfigUtil.isVersionCompatible(configYAML, 8)) {
				schemaVarName = OpenAPIParserUtil.getSchemaVarName(schemaName);
			}

			String batchPath = StringUtil.removeSubstrings(
				path, "/{" + schemaVarName + "Id}", "/{id}");

			if (ConfigUtil.isVersionCompatible(configYAML, 13)) {
				batchPath = StringUtil.removeSubstring(
					batchPath, "/{" + schemaVarName + "ExternalReferenceCode}");
			}

			return batchPath + "/batch";
		}

		throw new IllegalStateException(
			"Unsupported batch operation type: " + batchOperationType);
	}

	private static String _getDefaultValue(
		ConfigYAML configYAML, Schema schema, Map<String, Schema> schemas) {

		if (schema.getDefault() != null) {
			return schema.getDefault();
		}
		else if (schema.getReference() != null) {
			String referenceName = OpenAPIParserUtil.getReferenceName(
				schema.getReference());

			Schema referenceSchema = schemas.get(referenceName);

			if (referenceSchema == null) {
				Map<String, Schema> enumSchemas = OpenAPIUtil.getEnumSchemas(
					configYAML, schemas);

				referenceSchema = enumSchemas.get(referenceName);
			}

			return referenceSchema.getDefault();
		}

		return null;
	}

	private static List<JavaMethodParameter> _getJavaMethodParameters(
		Map<String, String> javaDataTypeMap, Operation operation,
		Set<String> requestBodyMediaTypes) {

		if ((operation == null) || (operation.getParameters() == null)) {
			return Collections.emptyList();
		}

		List<JavaMethodParameter> javaMethodParameters = new ArrayList<>();

		List<Parameter> parameters = operation.getParameters();

		Set<String> parameterNames = new HashSet<>();

		for (Parameter parameter : parameters) {
			parameterNames.add(parameter.getName());
		}

		for (Parameter parameter : parameters) {
			String parameterName = parameter.getName();

			if (StringUtil.equals(parameterName, "Accept-Language") ||
				StringUtil.equals(parameterName, "aggregationTerms") ||
				StringUtil.equals(parameterName, "fields") ||
				StringUtil.equals(parameterName, "filter") ||
				StringUtil.equals(parameterName, "nestedFields") ||
				StringUtil.equals(parameterName, "restrictFields") ||
				StringUtil.equals(parameterName, "sort")) {

				continue;
			}

			if ((StringUtil.equals(parameterName, "page") ||
				 StringUtil.equals(parameterName, "pageSize")) &&
				parameterNames.contains("page") &&
				parameterNames.contains("pageSize")) {

				continue;
			}

			javaMethodParameters.add(
				new JavaMethodParameter(
					CamelCaseUtil.toCamelCase(parameterName),
					OpenAPIParserUtil.getJavaDataType(
						javaDataTypeMap, parameter.getSchema())));
		}

		String operationId = operation.getOperationId();

		Schema schema = _getOperationSchema(operation, requestBodyMediaTypes);

		if ((operationId != null) && operationId.endsWith("PermissionsPage") &&
			operationId.startsWith("put") && (schema == null)) {

			javaMethodParameters.add(
				new JavaMethodParameter(
					"permissions",
					"[Lcom.liferay.portal.vulcan.permission.Permission;"));
		}

		if (parameterNames.contains("aggregationTerms")) {
			JavaMethodParameter javaMethodParameter = new JavaMethodParameter(
				"aggregation",
				"com.liferay.portal.vulcan.aggregation.Aggregation");

			javaMethodParameters.add(javaMethodParameter);
		}

		if (parameterNames.contains("filter")) {
			JavaMethodParameter javaMethodParameter = new JavaMethodParameter(
				"filter", Filter.class.getName());

			javaMethodParameters.add(javaMethodParameter);
		}

		if (parameterNames.contains("page") &&
			parameterNames.contains("pageSize")) {

			JavaMethodParameter javaMethodParameter = new JavaMethodParameter(
				"pagination",
				"com.liferay.portal.vulcan.pagination.Pagination");

			javaMethodParameters.add(javaMethodParameter);
		}

		if (parameterNames.contains("sort")) {
			JavaMethodParameter javaMethodParameter = new JavaMethodParameter(
				"sorts", Sort[].class.getName());

			javaMethodParameters.add(javaMethodParameter);
		}

		if (!requestBodyMediaTypes.isEmpty()) {
			if (requestBodyMediaTypes.contains(
					"application/x-www-form-urlencoded")) {

				throw new RuntimeException(
					"application/x-www-form-urlencoded is not supported");
			}
			else if (requestBodyMediaTypes.contains("multipart/form-data")) {
				javaMethodParameters.add(
					new JavaMethodParameter(
						"multipartBody",
						"com.liferay.portal.vulcan.multipart.MultipartBody"));
			}
			else {
				if (schema == null) {
					return javaMethodParameters;
				}

				String parameterType = OpenAPIParserUtil.getJavaDataType(
					javaDataTypeMap, schema);

				String simpleClassName = parameterType.substring(
					parameterType.lastIndexOf(".") + 1);

				String parameterName = TextFormatter.format(
					simpleClassName, TextFormatter.I);

				if (parameterType.startsWith("[")) {
					String elementClassName =
						OpenAPIParserUtil.getElementClassName(parameterType);

					simpleClassName = elementClassName.substring(
						elementClassName.lastIndexOf(".") + 1);

					parameterName = TextFormatter.formatPlural(
						TextFormatter.format(simpleClassName, TextFormatter.I));
				}

				javaMethodParameters.add(
					new JavaMethodParameter(parameterName, parameterType));
			}
		}

		return javaMethodParameters;
	}

	private static String _getMethodAnnotationConsumes(
		ConfigYAML configYAML, Set<String> requestBodyMediaTypes) {

		if (requestBodyMediaTypes.isEmpty()) {
			return null;
		}

		StringBuilder sb = new StringBuilder();

		for (String requestBodyMediaType : requestBodyMediaTypes) {
			sb.append(StringUtil.quote(requestBodyMediaType, "\""));
			sb.append(',');
		}

		if (sb.length() > 0) {
			sb.setLength(sb.length() - 1);
		}

		if (requestBodyMediaTypes.size() > 1) {
			return StringBundler.concat(
				"@", configYAML.getJavaEEPackage(), ".ws.rs.Consumes({", sb,
				"})");
		}

		return StringBundler.concat(
			"@", configYAML.getJavaEEPackage(), ".ws.rs.Consumes(", sb, ")");
	}

	private static String _getMethodAnnotationProduces(
		ConfigYAML configYAML, Operation operation) {

		Map<ResponseCode, Response> responses = operation.getResponses();

		if ((responses == null) || responses.isEmpty()) {
			return null;
		}

		Set<String> mediaTypes = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

		for (Response response : responses.values()) {
			if (response == null) {
				continue;
			}

			Map<String, Content> contents = response.getContent();

			if ((contents == null) || contents.isEmpty()) {
				continue;
			}

			mediaTypes.addAll(new ArrayList<>(contents.keySet()));
		}

		if (mediaTypes.isEmpty()) {
			return null;
		}

		StringBuilder sb = new StringBuilder();

		for (String mediaType : mediaTypes) {
			sb.append(StringUtil.quote(mediaType, "\""));

			sb.append(", ");
		}

		sb.setLength(sb.length() - 2);

		if (mediaTypes.size() > 1) {
			return StringBundler.concat(
				"@", configYAML.getJavaEEPackage(), ".ws.rs.Produces({", sb,
				"})");
		}

		return StringBundler.concat(
			"@", configYAML.getJavaEEPackage(), ".ws.rs.Produces(", sb, ")");
	}

	private static String _getMethodName(
		ConfigYAML configYAML, List<JavaMethodParameter> javaMethodParameters,
		String operationId) {

		if (!Boolean.TRUE.equals(configYAML.getForceObjectMethodNameSuffix())) {
			return operationId;
		}

		for (JavaMethodParameter javaMethodParameter : javaMethodParameters) {
			if (Objects.equals(
					javaMethodParameter.getParameterName(), "object") &&
				Objects.equals(
					javaMethodParameter.getParameterType(),
					"java.lang.Object")) {

				return operationId + "Object";
			}
		}

		return operationId;
	}

	private static String _getOperationId(
		ConfigYAML configYAML, Operation operation, String path,
		String returnType, String schemaName,
		boolean forcePredictableOperationId) {

		if (!forcePredictableOperationId &&
			(operation.getOperationId() != null)) {

			return operation.getOperationId();
		}

		boolean collection = StringUtil.startsWith(
			returnType, "com.liferay.portal.vulcan.pagination.Page<");

		List<String> operationIdSegments = new ArrayList<>();

		operationIdSegments.add(OpenAPIParserUtil.getHTTPMethod(operation));

		String[] pathSegments = path.split("/");
		String pluralSchemaName = TextFormatter.formatPlural(schemaName);

		for (int i = 0; i < pathSegments.length; i++) {
			String pathSegment = pathSegments[i];

			if (pathSegment.isEmpty()) {
				if (pathSegments.length != 1) {
					continue;
				}

				if (collection) {
					pathSegment = pluralSchemaName;
				}
				else {
					pathSegment = schemaName;
				}
			}

			String pathName = CamelCaseUtil.toCamelCase(
				pathSegment.replaceAll("\\{|-id|}|Id}", ""));

			if (StringUtil.equalsIgnoreCase(pathName, schemaName)) {
				pathName = schemaName;
			}
			else if (StringUtil.equalsIgnoreCase(pathName, pluralSchemaName)) {
				pathName = pluralSchemaName;
			}
			else {
				pathName = StringUtil.upperCaseFirstLetter(pathName);
			}

			if ((i == (pathSegments.length - 1)) && collection) {
				String previousMethodNameSegment = operationIdSegments.get(
					operationIdSegments.size() - 1);

				String pageClassName =
					"com.liferay.portal.vulcan.pagination.Page";

				String elementClassName = returnType.substring(
					pageClassName.length() + 1, returnType.length() - 1);

				String elementSimpleClassName = elementClassName.substring(
					elementClassName.lastIndexOf(".") + 1);

				if (Objects.equals(elementSimpleClassName, schemaName) &&
					!pathName.endsWith(pluralSchemaName) &&
					previousMethodNameSegment.endsWith(schemaName)) {

					String string = StringUtil.replaceLast(
						previousMethodNameSegment, schemaName,
						pluralSchemaName);

					operationIdSegments.set(
						operationIdSegments.size() - 1, string);
				}

				operationIdSegments.add(pathName + "Page");
			}
			else if (pathSegment.contains("{")) {
				String previousMethodNameSegment = operationIdSegments.get(
					operationIdSegments.size() - 1);

				if (pathName.endsWith("ExternalReferenceCode")) {
					if (!(Objects.equals(
							previousMethodNameSegment, "AssetLibrary") ||
						  Objects.equals(previousMethodNameSegment, "Site") ||
						  previousMethodNameSegment.endsWith(pathName) ||
						  StringUtil.equalsIgnoreCase(
							  previousMethodNameSegment,
							  pathName.replaceFirst(
								  "ExternalReferenceCode$", "")))) {

						operationIdSegments.add(pathName);
					}
				}
				else if (!previousMethodNameSegment.endsWith(pathName) &&
						 !previousMethodNameSegment.endsWith(schemaName)) {

					operationIdSegments.add(pathName);
				}
			}
			else if (Objects.equals(pathName, schemaName)) {
				operationIdSegments.add(pathName);
			}
			else if ((i != (pathSegments.length - 1)) ||
					 !Objects.equals(returnType, String.class.getName())) {

				String segment = OpenAPIUtil.formatSingular(
					configYAML, pathName);

				String s = StringUtil.toLowerCase(segment);

				if (s.endsWith(StringUtil.toLowerCase(schemaName))) {
					char c = segment.charAt(
						segment.length() - schemaName.length());

					if (Character.isUpperCase(c)) {
						String substring = segment.substring(
							0, segment.length() - schemaName.length());

						segment = substring + schemaName;
					}
				}

				operationIdSegments.add(segment);
			}
			else {
				operationIdSegments.add(pathName);
			}
		}

		return StringUtil.merge(operationIdSegments, "");
	}

	private static Schema _getOperationSchema(
		Operation operation, Set<String> requestBodyMediaTypes) {

		if (requestBodyMediaTypes.isEmpty()) {
			return null;
		}

		RequestBody requestBody = operation.getRequestBody();

		Map<String, Content> contents = requestBody.getContent();

		Iterator<String> iterator = requestBodyMediaTypes.iterator();

		Content content = contents.get(iterator.next());

		return content.getSchema();
	}

	private static String _getPageClassName(String returnType) {
		return StringBundler.concat(
			"com.liferay.portal.vulcan.pagination.Page<",
			OpenAPIParserUtil.getElementClassName(returnType), ">");
	}

	private static String _getParameterAnnotation(
		ConfigYAML configYAML, JavaMethodParameter javaMethodParameter,
		Operation operation, Map<String, Schema> schemas) {

		List<Parameter> parameters = operation.getParameters();

		Set<String> parameterNames = new HashSet<>();

		for (Parameter parameter : parameters) {
			parameterNames.add(parameter.getName());
		}

		String parameterType = javaMethodParameter.getParameterType();

		if (Objects.equals(
				parameterType,
				"com.liferay.portal.vulcan.aggregation.Aggregation") &&
			parameterNames.contains("aggregationTerms")) {

			return "@" + configYAML.getJavaEEPackage() + ".ws.rs.core.Context";
		}

		if (Objects.equals(parameterType, Filter.class.getName()) &&
			parameterNames.contains("filter")) {

			return "@" + configYAML.getJavaEEPackage() + ".ws.rs.core.Context";
		}

		if (Objects.equals(
				parameterType,
				"com.liferay.portal.vulcan.pagination.Pagination") &&
			parameterNames.contains("page") &&
			parameterNames.contains("pageSize")) {

			return "@" + configYAML.getJavaEEPackage() + ".ws.rs.core.Context";
		}

		if (Objects.equals(parameterType, Sort[].class.getName()) &&
			parameterNames.contains("sort")) {

			return "@" + configYAML.getJavaEEPackage() + ".ws.rs.core.Context";
		}

		if (Objects.equals(parameterType, "jakarta.ws.rs.sse.SseEventSink") &&
			parameterNames.contains("sseEventSink")) {

			return "@" + configYAML.getJavaEEPackage() + ".ws.rs.core.Context";
		}

		for (Parameter parameter : operation.getParameters()) {
			String parameterName = CamelCaseUtil.toCamelCase(
				parameter.getName());

			if (!Objects.equals(
					parameterName, javaMethodParameter.getParameterName())) {

				continue;
			}

			StringBundler sb = new StringBundler(10);

			String defaultValue = _getDefaultValue(
				configYAML, parameter.getSchema(), schemas);

			if (defaultValue != null) {
				sb.append(
					"@"
				).append(
					configYAML.getJavaEEPackage()
				).append(
					".ws.rs.DefaultValue(\""
				);

				sb.append(defaultValue);
				sb.append("\")");
			}

			if (parameter.isDeprecated()) {
				sb.append("@Deprecated");
			}

			if (parameter.isRequired()) {
				sb.append(
					"@"
				).append(
					configYAML.getJavaEEPackage()
				).append(
					".validation.constraints.NotNull"
				);
			}

			sb.append(
				"@io.swagger.v3.oas.annotations.Parameter(hidden=true)"
			).append(
				"@"
			).append(
				configYAML.getJavaEEPackage()
			).append(
				".ws.rs."
			);

			sb.append(StringUtil.upperCaseFirstLetter(parameter.getIn()));
			sb.append("Param(\"");
			sb.append(parameter.getName());
			sb.append("\")");

			return sb.toString();
		}

		return "";
	}

	private static String _getParentSchema(
		String path, Map<String, PathItem> pathItems, String schemaName) {

		String basePath = path;

		if (basePath.endsWith(
				"/{" + OpenAPIParserUtil.getSchemaVarName(schemaName) +
					"ExternalReferenceCode}")) {

			basePath = StringUtil.removeLast(
				path,
				"/{" + OpenAPIParserUtil.getSchemaVarName(schemaName) +
					"ExternalReferenceCode}");
		}
		else if (basePath.endsWith(
					"/by-external-reference-code/{externalReferenceCode}")) {

			basePath = StringUtil.removeLast(
				path, "/by-external-reference-code/{externalReferenceCode}");
		}

		if (basePath.endsWith("/permissions")) {
			basePath = StringUtil.removeLast(path, "/permissions");
		}

		int lastIndexOfSlash = basePath.lastIndexOf("/");

		if (lastIndexOfSlash < 1) {
			return null;
		}

		basePath = basePath.substring(0, lastIndexOfSlash);

		if (basePath.startsWith(
				"/asset-libraries/{assetLibraryExternalReferenceCode}") ||
			basePath.startsWith("/asset-libraries/{assetLibraryId}")) {

			return "AssetLibrary";
		}
		else if (basePath.startsWith("/sites/{siteExternalReferenceCode}") ||
				 basePath.startsWith("/sites/{siteId}")) {

			return "Site";
		}

		basePath = basePath.replaceAll("\\{parent([^}]*)\\}", "{$1}");
		basePath = basePath.replaceAll(
			"\\{[^}]*ExternalReferenceCode\\}", "{externalReferenceCode}");

		for (Map.Entry<String, PathItem> entry : pathItems.entrySet()) {
			PathItem pathItem = entry.getValue();

			Get get = pathItem.getGet();

			if ((get != null) &&
				StringUtil.equalsIgnoreCase(basePath, entry.getKey())) {

				List<String> tags = get.getTags();

				if (!tags.isEmpty()) {
					String tag = tags.get(0);

					if (!tag.equals(schemaName)) {
						return tag;
					}
				}
			}
		}

		return null;
	}

	private static Parameter _getQueryParameter(
		String defaultValue, String parameterName) {

		Parameter parameter = new Parameter();

		parameter.setIn("query");
		parameter.setName(parameterName);

		Schema schema = new Schema();

		schema.setDefault(defaultValue);
		schema.setType("String");

		parameter.setSchema(schema);

		return parameter;
	}

	private static String _getRequestBodyContent(
		JavaMethodSignature javaMethodSignature) {

		StringBundler sb = new StringBundler();

		Operation operation = javaMethodSignature.getOperation();

		RequestBody requestBody = operation.getRequestBody();

		Map<String, Content> contents = requestBody.getContent();

		List<Map.Entry<String, Content>> entries = new ArrayList<>(
			contents.entrySet());

		if (entries.size() > 1) {
			sb.append("{");
		}

		for (Map.Entry<String, Content> entry : entries) {
			if (Objects.equals(entry.getKey(), "multipart/form-data")) {
				sb.append("@io.swagger.v3.oas.annotations.media.Content(");
				sb.append("mediaType = \"multipart/form-data\", schema = ");
				sb.append("@io.swagger.v3.oas.annotations.media.Schema(");
				sb.append("implementation = ");
				sb.append(
					StringUtil.upperCaseFirstLetter(
						operation.getOperationId()));
				sb.append("RequestBody.class))");
			}
			else {
				sb.append("@io.swagger.v3.oas.annotations.media.Content(");
				sb.append("mediaType = \"");
				sb.append(entry.getKey());
				sb.append("\", schema = @io.swagger.v3.oas.annotations.media.");
				sb.append("Schema(implementation = ");
				sb.append(javaMethodSignature.getReturnType());
				sb.append(".class))");
			}

			if (entry != entries.get(entries.size() - 1)) {
				sb.append(",");
			}
		}

		if (entries.size() > 1) {
			sb.append("}");
		}

		return sb.toString();
	}

	private static String _getReturnType(
		ConfigYAML configYAML, Map<String, String> javaDataTypeMap,
		Operation operation, String path) {

		Map<ResponseCode, Response> responses = operation.getResponses();

		if ((responses == null) || responses.isEmpty()) {
			return void.class.getName();
		}

		Integer httpStatusCode = null;
		Response response = null;

		Set<Map.Entry<ResponseCode, Response>> responseEntrySet =
			responses.entrySet();

		for (Map.Entry<ResponseCode, Response> responseEntry :
				responseEntrySet) {

			ResponseCode responseCode = responseEntry.getKey();

			if (!responseCode.isDefaultResponse()) {
				continue;
			}

			response = responseEntry.getValue();

			break;
		}

		for (Map.Entry<ResponseCode, Response> entry : responses.entrySet()) {
			ResponseCode responseCode = entry.getKey();

			Integer curHttpStatusCode = responseCode.getHttpCode();

			if (responseCode.isDefaultResponse() ||
				((curHttpStatusCode / 100) != 2)) {

				continue;
			}

			if ((httpStatusCode == null) ||
				(httpStatusCode > curHttpStatusCode)) {

				httpStatusCode = curHttpStatusCode;
				response = entry.getValue();
			}
		}

		String returnType = String.class.getName();

		if ((response != null) && (response.getContent() != null)) {
			Map<String, Content> sortedContents =
				TreeMapBuilder.<String, Content>putAll(
					response.getContent()
				).build();

			if (sortedContents.isEmpty()) {
				return void.class.getName();
			}

			if ((operation instanceof Get || operation instanceof Put) &&
				path.endsWith("/permissions")) {

				return _getPageClassName(
					"[Lcom.liferay.portal.vulcan.permission.Permission;");
			}

			for (Content content : sortedContents.values()) {
				Schema schema = content.getSchema();

				if (schema == null) {
					return void.class.getName();
				}

				String format = schema.getFormat();

				if ((format != null) && format.equals("binary")) {
					return configYAML.getJavaEEPackage() +
						".ws.rs.core.Response";
				}

				returnType = OpenAPIParserUtil.getJavaDataType(
					javaDataTypeMap, schema);

				if (returnType.startsWith("[")) {
					return _getPageClassName(returnType);
				}

				String schemaReference = schema.getReference();

				if ((schemaReference == null) ||
					!schemaReference.contains("#")) {

					continue;
				}

				return returnType;
			}
		}

		if (operation instanceof Get) {
			return returnType;
		}

		return configYAML.getJavaEEPackage() + ".ws.rs.core.Response";
	}

	private static boolean _isSchemaMethod(
		Map<String, String> javaDataTypeMap, String returnType,
		String schemaName, List<String> tags) {

		if (!tags.isEmpty()) {
			return tags.contains(schemaName);
		}

		if (returnType.equals(javaDataTypeMap.get(schemaName))) {
			return true;
		}

		if (returnType.startsWith(
				"com.liferay.portal.vulcan.pagination.Page<") &&
			returnType.endsWith(">")) {

			String pageClassName = "com.liferay.portal.vulcan.pagination.Page";

			String className = returnType.substring(
				pageClassName.length() + 1, returnType.length() - 1);

			if (className.equals(javaDataTypeMap.get(schemaName))) {
				return true;
			}
		}

		return false;
	}

	private static boolean _isValidParameter(
		ConfigYAML configYAML, String name, String schemaName) {

		String schemaVarName = StringUtil.lowerCaseFirstLetter(schemaName);

		if (ConfigUtil.isVersionCompatible(configYAML, 8)) {
			schemaVarName = OpenAPIParserUtil.getSchemaVarName(schemaName);
		}

		if (StringUtil.equals(name, "aggregation") ||
			StringUtil.equals(name, "aggregationTerms") ||
			StringUtil.equals(name, "fields") ||
			StringUtil.equals(name, "flatten") ||
			StringUtil.equals(name, "id") ||
			StringUtil.equals(name, "nestedFields") ||
			StringUtil.equals(name, "page") ||
			StringUtil.equals(name, "pageSize") ||
			StringUtil.equals(name, "pagination") ||
			StringUtil.equals(name, "restrictFields") ||
			StringUtil.equals(name, schemaVarName) ||
			StringUtil.equals(name, schemaVarName + "ExternalReferenceCode") ||
			StringUtil.equals(name, schemaVarName + "Id")) {

			return false;
		}

		return true;
	}

	private static void _visitOperations(
		PathItem pathItem, Consumer<Operation> consumer) {

		if (pathItem.getDelete() != null) {
			consumer.accept(pathItem.getDelete());
		}

		if (pathItem.getGet() != null) {
			consumer.accept(pathItem.getGet());
		}

		if (pathItem.getHead() != null) {
			consumer.accept(pathItem.getHead());
		}

		if (pathItem.getOptions() != null) {
			consumer.accept(pathItem.getOptions());
		}

		if (pathItem.getPatch() != null) {
			consumer.accept(pathItem.getPatch());
		}

		if (pathItem.getPost() != null) {
			consumer.accept(pathItem.getPost());
		}

		if (pathItem.getPut() != null) {
			consumer.accept(pathItem.getPut());
		}
	}

	private static void _visitRequestBodyMediaTypes(
		RequestBody requestBody, Consumer<Set<String>> consumer) {

		if (requestBody != null) {
			boolean multipartFormData = false;
			Set<String> requestBodyMediaTypes = new TreeSet<>();

			Map<String, Content> contents = requestBody.getContent();

			for (String requestBodyMediaType : contents.keySet()) {
				if (Objects.equals(
						requestBodyMediaType, "multipart/form-data")) {

					multipartFormData = true;
				}
				else {
					requestBodyMediaTypes.add(requestBodyMediaType);
				}
			}

			if (!requestBodyMediaTypes.isEmpty()) {
				consumer.accept(requestBodyMediaTypes);
			}

			if (multipartFormData) {
				consumer.accept(Collections.singleton("multipart/form-data"));
			}
		}
		else {
			consumer.accept(Collections.emptySet());
		}
	}

	private enum BatchOperationType {

		EXPORT, IMPORT

	}

}