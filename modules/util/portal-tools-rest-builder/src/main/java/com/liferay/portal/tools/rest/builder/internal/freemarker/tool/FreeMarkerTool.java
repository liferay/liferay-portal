/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.internal.freemarker.tool;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.CamelCaseUtil;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TextFormatter;
import com.liferay.portal.kernel.util.TimeZoneUtil;
import com.liferay.portal.tools.rest.builder.internal.freemarker.tool.java.JavaMethodParameter;
import com.liferay.portal.tools.rest.builder.internal.freemarker.tool.java.JavaMethodSignature;
import com.liferay.portal.tools.rest.builder.internal.freemarker.tool.java.parser.DTOOpenAPIParser;
import com.liferay.portal.tools.rest.builder.internal.freemarker.tool.java.parser.GraphQLOpenAPIParser;
import com.liferay.portal.tools.rest.builder.internal.freemarker.tool.java.parser.ResourceOpenAPIParser;
import com.liferay.portal.tools.rest.builder.internal.freemarker.tool.java.parser.ResourceTestCaseOpenAPIParser;
import com.liferay.portal.tools.rest.builder.internal.freemarker.tool.java.parser.util.OpenAPIParserUtil;
import com.liferay.portal.tools.rest.builder.internal.freemarker.util.ConfigUtil;
import com.liferay.portal.tools.rest.builder.internal.freemarker.util.OpenAPIUtil;
import com.liferay.portal.tools.rest.builder.internal.util.GraphQLNamingUtil;
import com.liferay.portal.tools.rest.builder.internal.yaml.config.Application;
import com.liferay.portal.tools.rest.builder.internal.yaml.config.ConfigYAML;
import com.liferay.portal.tools.rest.builder.internal.yaml.openapi.Components;
import com.liferay.portal.tools.rest.builder.internal.yaml.openapi.Content;
import com.liferay.portal.tools.rest.builder.internal.yaml.openapi.Get;
import com.liferay.portal.tools.rest.builder.internal.yaml.openapi.Info;
import com.liferay.portal.tools.rest.builder.internal.yaml.openapi.OpenAPIYAML;
import com.liferay.portal.tools.rest.builder.internal.yaml.openapi.Operation;
import com.liferay.portal.tools.rest.builder.internal.yaml.openapi.Parameter;
import com.liferay.portal.tools.rest.builder.internal.yaml.openapi.PathItem;
import com.liferay.portal.tools.rest.builder.internal.yaml.openapi.RequestBody;
import com.liferay.portal.tools.rest.builder.internal.yaml.openapi.Schema;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * @author Peter Shin
 */
public class FreeMarkerTool {

	public static FreeMarkerTool getInstance() {
		return _freeMarkerTool;
	}

	public static String getPropertyType(
		ConfigYAML configYAML, OpenAPIYAML openAPIYAML, Schema propertySchema,
		String propertySchemaName) {

		Map<String, String> javaDataTypeMap =
			OpenAPIParserUtil.getJavaDataTypeMap(configYAML, openAPIYAML);

		return DTOOpenAPIParser.getPropertyType(
			configYAML, javaDataTypeMap, openAPIYAML, propertySchema,
			propertySchemaName);
	}

	public boolean containsJavaMethodSignature(
		List<JavaMethodSignature> javaMethodSignatures, String text) {

		for (JavaMethodSignature javaMethodSignature : javaMethodSignatures) {
			String javaMethodSignatureMethodName =
				javaMethodSignature.getMethodName();

			if (javaMethodSignatureMethodName.contains(text)) {
				return true;
			}
		}

		return false;
	}

	public boolean containsParameterType(
		List<JavaMethodSignature> javaMethodSignatures, String parameterType) {

		for (JavaMethodSignature javaMethodSignature : javaMethodSignatures) {
			for (JavaMethodParameter javaMethodParameter :
					javaMethodSignature.getJavaMethodParameters()) {

				if (StringUtil.equals(
						javaMethodParameter.getParameterType(),
						parameterType)) {

					return true;
				}
			}
		}

		return false;
	}

	public String[] distinct(String[] array) {
		return ArrayUtil.distinct(array);
	}

	public boolean generateBatch(
		ConfigYAML configYAML, String javaDataType,
		List<JavaMethodSignature> javaMethodSignatures, String schemaName) {

		if (!configYAML.isGenerateBatch() || (javaDataType == null) ||
			javaDataType.isEmpty()) {

			return false;
		}

		if (ResourceOpenAPIParser.hasResourceBatchJavaMethodSignatures(
				javaMethodSignatures) ||
			ResourceOpenAPIParser.hasResourceGetPageJavaMethodSignature(
				javaDataType, javaMethodSignatures)) {

			return true;
		}

		return false;
	}

	public boolean generateCRUD(
		ConfigYAML configYAML, List<JavaMethodSignature> javaMethodSignatures,
		String schemaName) {

		if (!configYAML.isGenerateCRUD() ||
			!isVersionCompatible(configYAML, 7)) {

			return false;
		}

		JavaMethodSignature javaMethodSignature = getJavaMethodSignature(
			javaMethodSignatures, "get" + schemaName);

		if (javaMethodSignature == null) {
			return false;
		}

		for (JavaMethodParameter javaMethodParameter :
				javaMethodSignature.getPathJavaMethodParameters()) {

			if (isIdParameter(javaMethodParameter, schemaName) &&
				StringUtil.equals(
					javaMethodParameter.getParameterType(), "java.lang.Long")) {

				return true;
			}
		}

		return false;
	}

	public String getActionName(String propertyName) {
		if (StringUtil.equals(propertyName, "delete")) {
			return ActionKeys.DELETE;
		}
		else if (StringUtil.equals(propertyName, "get")) {
			return ActionKeys.VIEW;
		}
		else if (StringUtil.equals(propertyName, "update") ||
				 StringUtil.equals(propertyName, "replace")) {

			return ActionKeys.UPDATE;
		}

		return null;
	}

	public Map<String, Schema> getAllSchemas(
		Map<String, Schema> allExternalSchemas, OpenAPIYAML openAPIYAML,
		Map<String, Schema> schemas) {

		Map<String, PathItem> pathItems = openAPIYAML.getPathItems();

		if (pathItems == null) {
			return Collections.emptyMap();
		}

		Set<Map.Entry<String, PathItem>> entries = pathItems.entrySet();

		for (Map.Entry<String, PathItem> entry : entries) {
			List<Operation> operations = OpenAPIParserUtil.getOperations(
				entry.getValue());

			for (Operation operation : operations) {
				List<String> tags = operation.getTags();

				for (String tag : tags) {
					if (!schemas.containsKey(tag)) {
						if ((allExternalSchemas != null) &&
							allExternalSchemas.containsKey(tag)) {

							schemas.put(tag, allExternalSchemas.get(tag));
						}
						else {
							schemas.put(tag, new Schema());
						}
					}
				}
			}
		}

		return schemas;
	}

	public List<JavaMethodParameter> getBodyJavaMethodParameters(
		JavaMethodSignature javaMethodSignature) {

		List<JavaMethodParameter> javaMethodParameters = new ArrayList<>();

		for (JavaMethodParameter javaMethodParameter :
				javaMethodSignature.getJavaMethodParameters()) {

			boolean exists = false;

			for (JavaMethodParameter pathJavaMethodParameter :
					javaMethodSignature.getPathJavaMethodParameters()) {

				if (Objects.equals(
						pathJavaMethodParameter.getParameterName(),
						javaMethodParameter.getParameterName()) &&
					Objects.equals(
						pathJavaMethodParameter.getParameterType(),
						javaMethodParameter.getParameterType())) {

					exists = true;

					break;
				}
			}

			if (!exists &&
				!isQueryParameter(
					javaMethodParameter, javaMethodSignature.getOperation())) {

				javaMethodParameters.add(javaMethodParameter);
			}
		}

		return javaMethodParameters;
	}

	public String getClientParameters(
		List<JavaMethodParameter> javaMethodParameters, String schemaName,
		String schemaVarName) {

		StringBuilder sb = new StringBuilder();

		for (JavaMethodParameter javaMethodParameter : javaMethodParameters) {
			String parameterAnnotation = null;

			String parameter = OpenAPIParserUtil.getParameter(
				javaMethodParameter, parameterAnnotation);

			if (parameter.contains("dto")) {
				sb.append(parameter.substring(parameter.lastIndexOf(".") + 1));
			}
			else {
				sb.append(parameter);
			}

			sb.append(',');
		}

		if (sb.length() > 0) {
			sb.setLength(sb.length() - 1);
		}

		String parameter = sb.toString();

		parameter = StringUtil.replace(
			parameter, ".constant.", ".client.constant.");
		parameter = StringUtil.replace(
			parameter, "com.liferay.portal.kernel.search.filter.Filter filter",
			"String filterString");
		parameter = StringUtil.replace(
			parameter, "com.liferay.portal.kernel.search.Sort[] sorts",
			"String sortString");
		parameter = StringUtil.replace(
			parameter,
			"com.liferay.portal.vulcan.aggregation.Aggregation aggregation",
			"List<String> aggregations");
		parameter = StringUtil.replace(
			parameter,
			"com.liferay.portal.vulcan.multipart.MultipartBody multipartBody",
			StringBundler.concat(
				schemaName, " ", schemaVarName,
				", Map<String, File> multipartFiles"));
		parameter = StringUtil.removeSubstring(
			parameter, "com.liferay.portal.vulcan.pagination.");
		parameter = StringUtil.removeSubstring(
			parameter, "com.liferay.portal.vulcan.permission.");

		return parameter;
	}

	public Map<String, Schema> getDTOEnumSchemas(
		ConfigYAML configYAML, OpenAPIYAML openAPIYAML, Schema schema) {

		return DTOOpenAPIParser.getEnumSchemas(configYAML, openAPIYAML, schema);
	}

	public String getDTOParentClassName(
		OpenAPIYAML openAPIYAML, String schemaName) {

		Map<String, Schema> schemas = getSchemas(openAPIYAML);

		Schema schema = schemas.get(schemaName);

		if (schema == null) {
			return null;
		}

		List<Schema> allOfSchemas = schema.getAllOfSchemas();

		if (allOfSchemas == null) {
			return null;
		}

		for (Schema allOfSchema : allOfSchemas) {
			if (allOfSchema.getReference() == null) {
				continue;
			}

			String referenceName = getReferenceName(allOfSchema.getReference());

			allOfSchema = schemas.get(referenceName);

			if (allOfSchema.getDiscriminator() != null) {
				return referenceName;
			}
		}

		return null;
	}

	public Map<String, String> getDTOProperties(
		ConfigYAML configYAML, OpenAPIYAML openAPIYAML, Schema schema,
		Map<String, Schema> schemas) {

		return DTOOpenAPIParser.getProperties(
			configYAML, false, openAPIYAML, schema, schemas);
	}

	public Map<String, String> getDTOProperties(
		ConfigYAML configYAML, OpenAPIYAML openAPIYAML, String schemaName,
		Map<String, Schema> schemas) {

		return DTOOpenAPIParser.getProperties(
			configYAML, openAPIYAML, schemaName, schemas);
	}

	public Schema getDTOPropertySchema(
		ConfigYAML configYAML, String propertyName, Schema schema,
		Map<String, Schema> schemas) {

		return DTOOpenAPIParser.getPropertySchema(
			configYAML, propertyName, schema, schemas);
	}

	public String getEnumFieldName(String value) {
		String fieldName = TextFormatter.format(value, TextFormatter.H);

		fieldName = fieldName.replaceFirst("^([0-9])", "positive_$1");
		fieldName = fieldName.replaceFirst("^\\-([0-9])", "negative_$1");

		fieldName = fieldName.replaceAll("\\.", "_point_");

		fieldName = fieldName.replaceAll("[ \\-\\/]", "_");
		fieldName = fieldName.replaceAll("[^a-zA-Z0-9_]", "");

		fieldName = fieldName.replaceAll("_+", "_");

		return StringUtil.toUpperCase(fieldName);
	}

	public String getExternalReferenceCodeParameterName(
		JavaMethodSignature javaMethodSignature, String schemaName) {

		for (JavaMethodParameter javaMethodParameter :
				javaMethodSignature.getJavaMethodParameters()) {

			String parameterName = javaMethodParameter.getParameterName();

			if (isExternalReferenceCodeParameterName(
					parameterName, schemaName)) {

				return parameterName;
			}
		}

		return null;
	}

	public String getGraphQLArguments(
		List<JavaMethodParameter> javaMethodParameters, String schemaVarName) {

		Map<String, String> substitutions = HashMapBuilder.put(
			"aggregation",
			"_aggregationBiFunction.apply(" + schemaVarName +
				"Resource, aggregations)"
		).put(
			"assetLibraryId", "Long.valueOf(assetLibraryId)"
		).put(
			"filter",
			"_filterBiFunction.apply(" + schemaVarName +
				"Resource, filterString)"
		).put(
			"siteId", "Long.valueOf(siteKey)"
		).put(
			"sorts",
			"_sortsBiFunction.apply(" + schemaVarName + "Resource, sortsString)"
		).build();

		StringBuilder sb = new StringBuilder();

		for (JavaMethodParameter javaMethodParameter : javaMethodParameters) {
			sb.append(
				_replaceGraphQLParameter(
					javaMethodParameter.getParameterName(), substitutions));
			sb.append(',');
		}

		if (sb.length() > 0) {
			sb.setLength(sb.length() - 1);
		}

		return StringUtil.replace(
			sb.toString(), "pageSize,page", "Pagination.of(page, pageSize)");
	}

	public List<JavaMethodSignature> getGraphQLJavaMethodSignatures(
			ConfigYAML configYAML, String graphQLType, OpenAPIYAML openAPIYAML)
		throws Exception {

		return GraphQLOpenAPIParser.getJavaMethodSignatures(
			configYAML, openAPIYAML,
			operation -> {
				String requiredType = "mutation";

				if (operation instanceof Get) {
					requiredType = "query";
				}

				return requiredType.equals(graphQLType);
			});
	}

	public String getGraphQLJavaParameterName(
		ConfigYAML configYAML, OpenAPIYAML openAPIYAML, String schemaName,
		Map<String, Schema> schemas, JavaMethodParameter javaMethodParameter) {

		Map<String, String> properties = getDTOProperties(
			configYAML, openAPIYAML, schemaName, schemas);

		return _getParentProperty(
			schemaName, javaMethodParameter, properties.keySet());
	}

	public String getGraphQLMethodAnnotations(
		JavaMethodSignature javaMethodSignature) {

		return GraphQLOpenAPIParser.getMethodAnnotations(javaMethodSignature);
	}

	public String getGraphQLMethodJavadoc(
		JavaMethodSignature javaMethodSignature,
		List<JavaMethodSignature> javaMethodSignatures,
		OpenAPIYAML openAPIYAML) {

		return StringBundler.concat(
			"Invoke this method with the command line:\n*\n* curl -H ",
			"'Content-Type: text/plain; charset=utf-8' -X 'POST' ",
			"'http://localhost:8080/o/graphql' -d $'",
			_getGraphQLBody(
				javaMethodSignature, javaMethodSignatures, openAPIYAML),
			"' -u 'test@liferay.com:test'");
	}

	public String getGraphQLMutationName(String methodName) {
		return GraphQLNamingUtil.getGraphQLMutationName(methodName);
	}

	public String getGraphQLNamespace(
		ConfigYAML configYAML, OpenAPIYAML openAPIYAML) {

		Application application = configYAML.getApplication();

		String baseURI = application.getBaseURI();

		if (baseURI.startsWith("/")) {
			baseURI = baseURI.substring(1);
		}

		int index = baseURI.indexOf("-rest");

		if (index != -1) {
			baseURI = baseURI.substring(0, index);
		}

		return CamelCaseUtil.toCamelCase(
			baseURI + "_" + OpenAPIUtil.escapeVersion(openAPIYAML));
	}

	public String getGraphQLParameters(
		List<JavaMethodParameter> javaMethodParameters, Operation operation,
		boolean annotation) {

		String parameters = GraphQLOpenAPIParser.getParameters(
			javaMethodParameters, operation, annotation);

		parameters = StringUtil.replace(
			parameters,
			"@GraphQLName(\"assetLibraryExternalReferenceCode\") " +
				"java.lang.String assetLibraryExternalReferenceCode",
			"@GraphQLName(\"assetLibraryExternalReferenceCode\") @NotEmpty " +
				"String assetLibraryExternalReferenceCode");
		parameters = StringUtil.replace(
			parameters,
			"@GraphQLName(\"assetLibraryId\") java.lang.Long assetLibraryId",
			"@GraphQLName(\"assetLibraryId\") @NotEmpty String assetLibraryId");
		parameters = StringUtil.replace(
			parameters,
			"@GraphQLName(\"siteExternalReferenceCode\") java.lang.String " +
				"siteExternalReferenceCode",
			"@GraphQLName(\"siteExternalReferenceCode\") @NotEmpty String " +
				"siteExternalReferenceCode");
		parameters = StringUtil.replace(
			parameters, "@GraphQLName(\"siteId\") java.lang.Long siteId",
			"@GraphQLName(\"siteKey\") @NotEmpty String siteKey");
		parameters = StringUtil.replace(
			parameters, "com.liferay.portal.kernel.search.filter.Filter filter",
			"String filterString");
		parameters = StringUtil.replace(
			parameters, "com.liferay.portal.kernel.search.Sort[] sorts",
			"String sortsString");
		parameters = StringUtil.replace(
			parameters,
			"com.liferay.portal.vulcan.aggregation.Aggregation aggregation",
			"List<String> aggregations");

		return parameters;
	}

	public String getGraphQLPropertyName(
		JavaMethodSignature javaMethodSignature,
		List<JavaMethodSignature> javaMethodSignatures) {

		return GraphQLNamingUtil.getGraphQLPropertyName(
			javaMethodSignature.getMethodName(),
			javaMethodSignature.getReturnType(),
			ListUtil.toList(
				javaMethodSignatures, JavaMethodSignature::getMethodName));
	}

	public List<JavaMethodSignature> getGraphQLRelationJavaMethodSignatures(
			ConfigYAML configYAML, String graphQLType, OpenAPIYAML openAPIYAML)
		throws Exception {

		List<JavaMethodSignature> javaMethodSignatures =
			_getSortedJavaMethodSignatures(
				configYAML, graphQLType, openAPIYAML);

		Map<String, Schema> schemas = getSchemas(openAPIYAML);

		Map<String, JavaMethodSignature> javaMethodSignatureMap =
			new HashMap<>();

		for (JavaMethodSignature javaMethodSignature : javaMethodSignatures) {
			List<JavaMethodParameter> javaMethodParameters =
				javaMethodSignature.getJavaMethodParameters();

			if (javaMethodParameters.isEmpty()) {
				continue;
			}

			JavaMethodParameter javaMethodParameter = javaMethodParameters.get(
				0);

			String parameterName = javaMethodParameter.getParameterName();

			String methodName = javaMethodSignature.getMethodName();

			JavaMethodSignature relationJavaMethodSignature =
				_getGraphQLPathRelation(
					javaMethodSignature, javaMethodSignatures, parameterName,
					schemas);

			if (relationJavaMethodSignature != null) {
				javaMethodSignatureMap.put(
					methodName, relationJavaMethodSignature);
			}

			for (Map.Entry<String, Schema> entry : schemas.entrySet()) {
				Schema schema = entry.getValue();

				Map<String, Schema> propertySchemas =
					schema.getPropertySchemas();

				if (propertySchemas != null) {
					for (String propertyName : propertySchemas.keySet()) {
						if (_isGraphQLPropertyRelation(
								javaMethodSignature, parameterName,
								propertyName, entry.getKey())) {

							javaMethodSignatureMap.put(
								methodName,
								_getJavaMethodSignature(
									javaMethodSignature, entry.getKey()));
						}
					}
				}
			}
		}

		return new ArrayList<>(javaMethodSignatureMap.values());
	}

	public String getGraphQLRelationName(
		JavaMethodSignature javaMethodSignature,
		List<JavaMethodSignature> javaMethodSignatures) {

		String methodName = getGraphQLPropertyName(
			javaMethodSignature, javaMethodSignatures);

		return StringUtil.lowerCaseFirstLetter(
			methodName.replaceFirst(
				StringUtil.lowerCaseFirstLetter(
					javaMethodSignature.getParentSchemaName()),
				""));
	}

	public Set<String> getGraphQLSchemaNames(
		List<JavaMethodSignature> javaMethodSignatures) {

		return OpenAPIParserUtil.getSchemaNames(javaMethodSignatures);
	}

	public String getHTTPMethod(Operation operation) {
		return OpenAPIParserUtil.getHTTPMethod(operation);
	}

	public String getJavaDataType(
		ConfigYAML configYAML, OpenAPIYAML openAPIYAML, Schema schema) {

		return OpenAPIParserUtil.getJavaDataType(
			OpenAPIParserUtil.getJavaDataTypeMap(configYAML, openAPIYAML),
			schema);
	}

	public String getJavaDataType(
		ConfigYAML configYAML, OpenAPIYAML openAPIYAML, String schemaName) {

		Map<String, String> javaDataTypeMap =
			OpenAPIParserUtil.getJavaDataTypeMap(configYAML, openAPIYAML);

		return javaDataTypeMap.get(schemaName);
	}

	public JavaMethodSignature getJavaMethodSignature(
		List<JavaMethodSignature> javaMethodSignatures, String methodName) {

		for (JavaMethodSignature javaMethodSignature : javaMethodSignatures) {
			if (!methodName.equals(javaMethodSignature.getMethodName())) {
				continue;
			}

			return javaMethodSignature;
		}

		return null;
	}

	public Map<String, Schema> getMultipartBodySchemas(
		JavaMethodSignature javaMethodSignature) {

		return ResourceOpenAPIParser.getMultipartBodySchemas(
			javaMethodSignature);
	}

	public String getObjectFieldStringValue(String type, Object value) {
		if (value instanceof Date) {
			if (type.equals("Date")) {
				return _dateFormat.format(value);
			}

			return _dateTimeDateFormat.format(value);
		}

		return value.toString();
	}

	public List<JavaMethodSignature>
			getParentGraphQLRelationJavaMethodSignatures(
				ConfigYAML configYAML, String graphQLType,
				OpenAPIYAML openAPIYAML)
		throws Exception {

		Map<String, Schema> schemas = getSchemas(openAPIYAML);

		List<JavaMethodSignature> javaMethodSignatures =
			_getSortedJavaMethodSignatures(
				configYAML, graphQLType, openAPIYAML);

		List<JavaMethodSignature> parentJavaMethodSignatures =
			new ArrayList<>();

		for (Map.Entry<String, Schema> entry : schemas.entrySet()) {
			Schema schema = entry.getValue();

			Map<String, Schema> propertySchemas = schema.getPropertySchemas();

			if (propertySchemas == null) {
				continue;
			}

			Set<String> propertyNames = propertySchemas.keySet();

			for (String propertyName : propertyNames) {
				if (!propertyName.startsWith("parent") ||
					propertyNames.contains(
						StringUtil.removeSubstring(propertyName, "Id"))) {

					continue;
				}

				propertyName = StringUtil.lowerCaseFirstLetter(
					StringUtil.removeSubstring(propertyName, "parent"));

				for (JavaMethodSignature javaMethodSignature :
						javaMethodSignatures) {

					List<JavaMethodParameter> javaMethodParameters =
						javaMethodSignature.getJavaMethodParameters();

					if (javaMethodParameters.isEmpty()) {
						continue;
					}

					JavaMethodParameter javaMethodParameter =
						javaMethodParameters.get(0);

					if (!propertyName.equals(
							javaMethodParameter.getParameterName())) {

						continue;
					}

					parentJavaMethodSignatures.add(
						_getJavaMethodSignature(
							javaMethodSignature, entry.getKey()));

					break;
				}
			}
		}

		return parentJavaMethodSignatures;
	}

	public JavaMethodSignature getParentPermissionsPageJavaMethodSignature(
		String httpMethod, List<JavaMethodSignature> javaMethodSignatures,
		String parentSchemaName, String schemaName) {

		if ((parentSchemaName == null) ||
			!(Objects.equals(parentSchemaName, "AssetLibrary") ||
			  Objects.equals(parentSchemaName, "Site"))) {

			return null;
		}

		for (JavaMethodSignature javaMethodSignature : javaMethodSignatures) {
			if (Objects.equals(
					javaMethodSignature.getMethodName(),
					StringBundler.concat(
						httpMethod, parentSchemaName, schemaName,
						"PermissionsPage")) &&
				hasPathParameter(
					javaMethodSignature,
					TextFormatter.format(parentSchemaName, TextFormatter.I) +
						"ExternalReferenceCode") &&
				hasPathParameter(
					javaMethodSignature,
					TextFormatter.format(schemaName, TextFormatter.I) +
						"ExternalReferenceCode")) {

				return javaMethodSignature;
			}
		}

		return null;
	}

	public JavaMethodSignature getPostSchemaJavaMethodSignature(
		List<JavaMethodSignature> javaMethodSignatures, String parameterName,
		String schemaName) {

		String parentSchemaName = parameterName;

		if (parentSchemaName.startsWith("parent")) {
			parentSchemaName = parentSchemaName.substring(6);
		}

		if (parentSchemaName.endsWith("ExternalReferenceCode")) {
			parentSchemaName = parentSchemaName.substring(
				0, parentSchemaName.length() - 21);
		}

		if (parentSchemaName.endsWith("Id")) {
			parentSchemaName = parentSchemaName.substring(
				0, parentSchemaName.length() - 2);
		}

		for (JavaMethodSignature javaMethodSignature : javaMethodSignatures) {
			Operation operation = javaMethodSignature.getOperation();

			if (!Objects.equals(getHTTPMethod(operation), "post") ||
				!hasParameter(javaMethodSignature, parameterName)) {

				continue;
			}

			StringBundler sb = new StringBundler(3);

			sb.append(getHTTPMethod(operation));

			sb.append(StringUtil.upperCaseFirstLetter(parentSchemaName));

			sb.append(StringUtil.upperCaseFirstLetter(schemaName));

			if (!Objects.equals(
					javaMethodSignature.getMethodName(), sb.toString())) {

				continue;
			}

			List<JavaMethodParameter> javaMethodParameters =
				javaMethodSignature.getJavaMethodParameters();

			if ((javaMethodParameters.size() != 2) ||
				SetUtil.isEmpty(
					javaMethodSignature.getRequestBodyMediaTypes())) {

				continue;
			}

			return javaMethodSignature;
		}

		return null;
	}

	public String getReferenceName(String reference) {
		return OpenAPIParserUtil.getReferenceName(reference);
	}

	public String getResourceArguments(
		List<JavaMethodParameter> javaMethodParameters) {

		return OpenAPIParserUtil.getArguments(javaMethodParameters);
	}

	public List<JavaMethodSignature> getResourceJavaMethodSignatures(
		ConfigYAML configYAML, OpenAPIYAML openAPIYAML, String schemaName) {

		return ResourceOpenAPIParser.getJavaMethodSignatures(
			configYAML, openAPIYAML, schemaName);
	}

	public String getResourceMethodAnnotations(
		ConfigYAML configYAML, JavaMethodSignature javaMethodSignature) {

		return ResourceOpenAPIParser.getMethodAnnotations(
			configYAML, javaMethodSignature);
	}

	public String getResourceMethodName(
		List<JavaMethodSignature> javaMethodSignatures, String propertyName) {

		return ResourceOpenAPIParser.getResourceMethodName(
			javaMethodSignatures, propertyName);
	}

	public String getResourceParameters(
		ConfigYAML configYAML, List<JavaMethodParameter> javaMethodParameters,
		Operation operation, Map<String, Schema> schemas, boolean annotation) {

		return ResourceOpenAPIParser.getParameters(
			configYAML, javaMethodParameters, operation, schemas, annotation);
	}

	public String getResourceTestCaseArguments(
		List<JavaMethodParameter> javaMethodParameters) {

		return OpenAPIParserUtil.getArguments(javaMethodParameters);
	}

	public List<JavaMethodSignature> getResourceTestCaseJavaMethodSignatures(
		ConfigYAML configYAML, OpenAPIYAML openAPIYAML, String schemaName) {

		return ResourceTestCaseOpenAPIParser.getJavaMethodSignatures(
			configYAML, openAPIYAML, schemaName);
	}

	public String getResourceTestCaseParameters(
		ConfigYAML configYAML, List<JavaMethodParameter> javaMethodParameters,
		Operation operation, Map<String, Schema> schemas, boolean annotation) {

		return ResourceTestCaseOpenAPIParser.getParameters(
			configYAML, javaMethodParameters, operation, schemas, annotation);
	}

	public String getRESTMethodJavadoc(
		ConfigYAML configYAML, JavaMethodSignature javaMethodSignature,
		OpenAPIYAML openAPIYAML) {

		StringBundler sb = new StringBundler(10);

		sb.append("Invoke this method with the command line:\n*\n* curl -X '");
		sb.append(
			StringUtil.toUpperCase(
				OpenAPIParserUtil.getHTTPMethod(
					javaMethodSignature.getOperation())));
		sb.append("' 'http://localhost:8080/o");

		Application application = configYAML.getApplication();

		sb.append(application.getBaseURI());

		sb.append("/");

		Info info = openAPIYAML.getInfo();

		sb.append(info.getVersion());

		sb.append(javaMethodSignature.getPath());
		sb.append("' ");
		sb.append(_getRESTBody(javaMethodSignature, openAPIYAML));
		sb.append(" -u 'test@liferay.com:test'");

		return sb.toString();
	}

	public Map<String, Schema> getSchemas(OpenAPIYAML openAPIYAML) {
		Components components = openAPIYAML.getComponents();

		if (components == null) {
			return new HashMap<>();
		}

		return new TreeMap<>(components.getSchemas());
	}

	public String getSchemaVarName(String schemaName) {
		return OpenAPIParserUtil.getSchemaVarName(schemaName);
	}

	public String getVersion(OpenAPIYAML openAPIYAML) {
		return OpenAPIParserUtil.getVersion(openAPIYAML);
	}

	public Set<String> getVulcanBatchImplementationCreateStrategies(
		List<JavaMethodSignature> javaMethodSignatures,
		Map<String, String> properties) {

		return ResourceOpenAPIParser.
			getVulcanBatchImplementationCreateStrategies(
				javaMethodSignatures, properties);
	}

	public Set<String> getVulcanBatchImplementationUpdateStrategies(
		List<JavaMethodSignature> javaMethodSignatures) {

		return ResourceOpenAPIParser.
			getVulcanBatchImplementationUpdateStrategies(javaMethodSignatures);
	}

	public Map<String, String> getWritableDTOProperties(
		ConfigYAML configYAML, OpenAPIYAML openAPIYAML, Schema schema,
		Map<String, Schema> schemas) {

		return DTOOpenAPIParser.getProperties(
			configYAML, true, openAPIYAML, schema, schemas);
	}

	public boolean hasHTTPMethod(
		JavaMethodSignature javaMethodSignature, String... httpMethods) {

		return OpenAPIParserUtil.hasHTTPMethod(
			javaMethodSignature, httpMethods);
	}

	public boolean hasJavaMethodSignature(
		List<JavaMethodSignature> javaMethodSignatures, String methodName) {

		for (JavaMethodSignature javaMethodSignature : javaMethodSignatures) {
			String javaMethodSignatureMethodName =
				javaMethodSignature.getMethodName();

			if (javaMethodSignatureMethodName.equals(methodName)) {
				return true;
			}
		}

		return false;
	}

	public boolean hasParameter(
		JavaMethodSignature javaMethodSignature, String parameterName) {

		List<JavaMethodParameter> javaMethodParameters =
			javaMethodSignature.getJavaMethodParameters();

		for (JavaMethodParameter javaMethodParameter : javaMethodParameters) {
			if (parameterName.equals(javaMethodParameter.getParameterName())) {
				return true;
			}
		}

		return false;
	}

	public boolean hasPath(
		List<JavaMethodSignature> javaMethodSignatures, String path) {

		for (JavaMethodSignature javaMethodSignature : javaMethodSignatures) {
			String javaMethodSignaturePath = javaMethodSignature.getPath();

			if (javaMethodSignaturePath.equals(path)) {
				return true;
			}
		}

		return false;
	}

	public boolean hasPathParameter(JavaMethodSignature javaMethodSignature) {
		List<JavaMethodParameter> javaMethodParameters =
			javaMethodSignature.getJavaMethodParameters();
		Operation operation = javaMethodSignature.getOperation();

		for (JavaMethodParameter javaMethodParameter : javaMethodParameters) {
			if (isPathParameter(javaMethodParameter, operation)) {
				return true;
			}
		}

		return false;
	}

	public boolean hasPathParameter(
		JavaMethodSignature javaMethodSignature, String parameterName) {

		return ResourceOpenAPIParser.hasPathParameter(
			javaMethodSignature, parameterName);
	}

	public boolean hasPostSchemaJavaMethodSignature(
		List<JavaMethodSignature> javaMethodSignatures, String parameterName,
		String schemaName) {

		JavaMethodSignature javaMethodSignature =
			getPostSchemaJavaMethodSignature(
				javaMethodSignatures, parameterName, schemaName);

		if (javaMethodSignature != null) {
			return true;
		}

		return false;
	}

	public boolean hasQueryParameter(JavaMethodSignature javaMethodSignature) {
		List<JavaMethodParameter> javaMethodParameters =
			javaMethodSignature.getJavaMethodParameters();
		Operation operation = javaMethodSignature.getOperation();

		for (JavaMethodParameter javaMethodParameter : javaMethodParameters) {
			if (isQueryParameter(javaMethodParameter, operation)) {
				return true;
			}
		}

		return false;
	}

	public boolean hasReadVulcanBatchImplementation(
		List<JavaMethodSignature> javaMethodSignatures) {

		return ResourceOpenAPIParser.hasReadVulcanBatchImplementation(
			javaMethodSignatures);
	}

	public boolean hasRequestBodyMediaType(
		JavaMethodSignature javaMethodSignature, String mediaType) {

		Operation operation = javaMethodSignature.getOperation();

		if (operation.getRequestBody() == null) {
			return false;
		}

		RequestBody requestBody = operation.getRequestBody();

		if (requestBody.getContent() == null) {
			return false;
		}

		Map<String, Content> contents = requestBody.getContent();

		Set<String> mediaTypes = contents.keySet();

		return mediaTypes.contains(mediaType);
	}

	public boolean isCollection(
		JavaMethodSignature javaMethodSignaturePathItem,
		List<JavaMethodSignature> javaMethodSignatures, String schemaNames) {

		PathItem pathItem = javaMethodSignaturePathItem.getPathItem();

		Operation getOperation = pathItem.getGet();

		if (getOperation != null) {
			return StringUtil.endsWith(getOperation.getOperationId(), "Page");
		}

		for (JavaMethodSignature javaMethodSignature : javaMethodSignatures) {
			if (StringUtil.equals(
					javaMethodSignature.getMethodName(),
					"get" + schemaNames + "Page")) {

				return true;
			}
		}

		return false;
	}

	public boolean isDTOSchemaProperty(
		ConfigYAML configYAML, String propertyName, Schema schema,
		Map<String, Schema> schemas) {

		return DTOOpenAPIParser.isSchemaProperty(
			configYAML, propertyName, schema, schemas);
	}

	public boolean isExternalReferenceCodeMethod(
		String httpMethod, JavaMethodSignature javaMethodSignature) {

		return ResourceOpenAPIParser.isExternalReferenceCodeMethod(
			httpMethod, javaMethodSignature);
	}

	public boolean isExternalReferenceCodeParameter(
		JavaMethodParameter javaMethodParameter, String schemaName) {

		return isExternalReferenceCodeParameterName(
			javaMethodParameter.getParameterName(), schemaName);
	}

	public boolean isExternalReferenceCodeParameterName(
		String parameterName, String schemaName) {

		if (StringUtil.equals(parameterName, "externalReferenceCode") ||
			StringUtil.equals(
				parameterName,
				TextFormatter.format(schemaName, TextFormatter.I) +
					"ExternalReferenceCode")) {

			return true;
		}

		return false;
	}

	public boolean isGeneratePermissions(
		ConfigYAML configYAML, JavaMethodSignature javaMethodSignature,
		List<JavaMethodSignature> javaMethodSignatures, Schema schema,
		String schemaName) {

		if (!configYAML.isGeneratePermissions()) {
			return false;
		}

		Map<String, Schema> propertySchemas = schema.getPropertySchemas();

		if (MapUtil.isEmpty(propertySchemas) ||
			!propertySchemas.containsKey("permissions")) {

			return false;
		}

		String methodName = javaMethodSignature.getMethodName();
		String parentSchemaName = GetterUtil.getString(
			javaMethodSignature.getParentSchemaName());
		String pluralSchemaName = TextFormatter.formatPlural(schemaName);

		if (!(methodName.equals(
				StringBundler.concat(
					"get", parentSchemaName, pluralSchemaName, "Page")) ||
			  methodName.equals("get" + parentSchemaName + schemaName) ||
			  methodName.equals(
				  StringBundler.concat(
					  "get", parentSchemaName, schemaName,
					  "ByExternalReferenceCode")) ||
			  methodName.equals("post" + parentSchemaName + schemaName) ||
			  methodName.equals("put" + parentSchemaName + schemaName) ||
			  methodName.equals(
				  StringBundler.concat(
					  "put", parentSchemaName, schemaName,
					  "ByExternalReferenceCode")))) {

			return false;
		}

		Schema permissionsSchema = propertySchemas.get("permissions");

		if (permissionsSchema.isReadOnly() || permissionsSchema.isWriteOnly()) {
			throw new IllegalStateException(
				StringBundler.concat(
					"The attribute \"", schemaName,
					".permissions\" cannot be \"",
					permissionsSchema.isReadOnly() ? "readOnly" : "writeOnly",
					"\""));
		}

		return true;
	}

	public boolean isIdParameter(
		JavaMethodParameter javaMethodParameter, String schemaName) {

		return isIdParameterName(
			javaMethodParameter.getParameterName(), schemaName);
	}

	public boolean isIdParameterName(String parameterName, String schemaName) {
		if (StringUtil.equals(parameterName, "id") ||
			StringUtil.equals(
				parameterName,
				TextFormatter.format(schemaName, TextFormatter.I) + "Id")) {

			return true;
		}

		return false;
	}

	public boolean isParameter(
		JavaMethodParameter javaMethodParameter, Operation operation,
		String type) {

		String name = javaMethodParameter.getParameterName();

		for (Parameter parameter : operation.getParameters()) {
			if (Objects.equals(parameter.getName(), name) &&
				Objects.equals(parameter.getIn(), type)) {

				return true;
			}
		}

		return false;
	}

	public boolean isParameterNameSchemaRelated(
		String parameterName, String path, String schemaName) {

		String schemaVarName = TextFormatter.format(
			schemaName, TextFormatter.I);

		if (StringUtil.equals(
				parameterName, schemaVarName + "ExternalReferenceCode") ||
			StringUtil.equals(parameterName, schemaVarName + "Id")) {

			return true;
		}

		String parameterNameSubpath = "/{" + parameterName + "}";

		if (StringUtil.endsWith(path, parameterNameSubpath) &&
			(StringUtil.equals(parameterName, "externalReferenceCode") ||
			 StringUtil.equals(parameterName, "id"))) {

			return true;
		}

		int parameterIndex = path.indexOf(parameterNameSubpath);

		if (parameterIndex == -1) {
			return false;
		}

		String[] pathSegments = path.substring(
			0, parameterIndex
		).split(
			"/"
		);

		String parameterSchemaName = null;

		for (int i = pathSegments.length - 1; i >= 0; i--) {
			String segment = pathSegments[i];

			if (StringUtil.startsWith(segment, "by-") ||
				(StringUtil.startsWith(segment, "{") &&
				 StringUtil.endsWith(segment, "}"))) {

				continue;
			}

			parameterSchemaName = segment;

			break;
		}

		if (parameterSchemaName == null) {
			return false;
		}

		String formattedParameterSchemaName = TextFormatter.format(
			parameterSchemaName, TextFormatter.K);
		String formattedSchemaNamePlural = TextFormatter.format(
			TextFormatter.formatPlural(schemaName), TextFormatter.K);
		String formattedSchemaNameSingular = TextFormatter.format(
			schemaName, TextFormatter.K);

		if (StringUtil.equals(
				formattedParameterSchemaName, formattedSchemaNamePlural) ||
			StringUtil.equals(
				formattedParameterSchemaName, formattedSchemaNameSingular)) {

			return true;
		}

		return false;
	}

	public boolean isParameterNameScopeRelated(String parameterName) {
		if (StringUtil.equals(
				parameterName, "assetLibraryExternalReferenceCode") ||
			StringUtil.equals(parameterName, "assetLibraryId") ||
			StringUtil.equals(parameterName, "siteExternalReferenceCode") ||
			StringUtil.equals(parameterName, "siteId")) {

			return true;
		}

		return false;
	}

	public boolean isPathParameter(
		JavaMethodParameter javaMethodParameter, Operation operation) {

		return isParameter(javaMethodParameter, operation, "path");
	}

	public boolean isQueryParameter(
		JavaMethodParameter javaMethodParameter, Operation operation) {

		if (isParameter(javaMethodParameter, operation, "query") ||
			(Objects.equals(
				javaMethodParameter.getParameterName(), "pagination") &&
			 Objects.equals(
				 javaMethodParameter.getParameterType(),
				 "com.liferay.portal.vulcan.pagination.Pagination")) ||
			(Objects.equals(javaMethodParameter.getParameterName(), "sorts") &&
			 Objects.equals(
				 javaMethodParameter.getParameterType(),
				 Sort[].class.getName()))) {

			return true;
		}

		return false;
	}

	public boolean isReturnTypeRelatedSchema(
		JavaMethodSignature javaMethodSignature,
		List<String> relatedSchemaNames) {

		String returnType = javaMethodSignature.getReturnType();

		String[] returnTypeParts = returnType.split("\\.");

		if (returnTypeParts.length > 0) {
			String string = returnTypeParts[returnTypeParts.length - 1];

			return relatedSchemaNames.contains(string);
		}

		return false;
	}

	public boolean isSchemaPropertyRequired(
		OpenAPIYAML openAPIYAML, String schemaName, String propertyName) {

		Map<String, Schema> schemas = getSchemas(openAPIYAML);

		Schema schema = schemas.get(schemaName);

		if (schema == null) {
			return false;
		}

		List<String> requiredPropertyNames =
			schema.getRequiredPropertySchemaNames();

		if ((requiredPropertyNames != null) &&
			requiredPropertyNames.contains(propertyName)) {

			return true;
		}

		return false;
	}

	public boolean isVersionCompatible(ConfigYAML configYAML, int version) {
		return ConfigUtil.isVersionCompatible(configYAML, version);
	}

	private static DateFormat _getDateFormat(String pattern) {
		DateFormat dateFormat = new SimpleDateFormat(pattern);

		dateFormat.setTimeZone(TimeZoneUtil.GMT);

		return dateFormat;
	}

	private FreeMarkerTool() {
	}

	private String _getGraphQLBody(
		JavaMethodSignature javaMethodSignature,
		List<JavaMethodSignature> javaMethodSignatures,
		OpenAPIYAML openAPIYAML) {

		StringBundler sb = new StringBundler("{\"query\": \"query {");

		sb.append(
			getGraphQLPropertyName(javaMethodSignature, javaMethodSignatures));

		Set<String> javaMethodParameterNames = new TreeSet<>();

		for (JavaMethodParameter javaMethodParameter :
				javaMethodSignature.getJavaMethodParameters()) {

			javaMethodParameterNames.add(
				javaMethodParameter.getParameterName());
		}

		if (!javaMethodParameterNames.isEmpty()) {
			sb.append("(");

			Iterator<String> iterator = javaMethodParameterNames.iterator();

			while (iterator.hasNext()) {
				String javaMethodParameterName = iterator.next();

				javaMethodParameterName = StringUtil.replace(
					javaMethodParameterName, "siteId", "siteKey");

				sb.append(javaMethodParameterName);

				sb.append(": ___");

				if (iterator.hasNext()) {
					sb.append(", ");
				}
			}

			sb.append(")");
		}

		sb.append("{");

		String returnType = javaMethodSignature.getReturnType();

		if (returnType.startsWith("java.util.Collection<")) {
			sb.append("items {__}, page, pageSize, totalCount");
		}
		else {
			Map<String, Schema> schemas = getSchemas(openAPIYAML);

			String returnSchema = returnType.substring(
				returnType.lastIndexOf(".") + 1);

			if (schemas.containsKey(returnSchema)) {
				Schema schema = schemas.get(returnSchema);

				Map<String, Schema> propertySchemas =
					schema.getPropertySchemas();

				Set<String> strings = propertySchemas.keySet();

				Iterator<String> iterator = strings.iterator();

				while (iterator.hasNext()) {
					String key = iterator.next();

					sb.append(key);

					if (iterator.hasNext()) {
						sb.append(", ");
					}
				}
			}
		}

		sb.append("}}\"}");

		return sb.toString();
	}

	private JavaMethodSignature _getGraphQLPathRelation(
		JavaMethodSignature parentJavaMethodSignature,
		List<JavaMethodSignature> javaMethodSignatures, String parameterName,
		Map<String, Schema> schemas) {

		for (JavaMethodSignature javaMethodSignature : javaMethodSignatures) {
			if (parentJavaMethodSignature == javaMethodSignature) {
				continue;
			}

			List<JavaMethodParameter> javaMethodParameters =
				javaMethodSignature.getJavaMethodParameters();

			if (javaMethodParameters.size() != 1) {
				continue;
			}

			JavaMethodParameter javaMethodParameter = javaMethodParameters.get(
				0);

			String propertyName = StringUtil.upperCaseFirstLetter(
				javaMethodParameter.getParameterName());

			String returnType = javaMethodSignature.getReturnType();

			String schemaName = javaMethodSignature.getSchemaName();

			if ((returnType.endsWith(schemaName) &&
				 !parameterName.equals("id") &&
				 parameterName.equals(
					 javaMethodParameter.getParameterName())) ||
				parameterName.equals("parent" + propertyName)) {

				JavaMethodSignature relationJavaMethodSignature =
					_getJavaMethodSignature(
						parentJavaMethodSignature, schemaName);

				Schema schema = schemas.get(schemaName);

				Map<String, Schema> propertySchemas =
					schema.getPropertySchemas();

				if (propertySchemas.containsKey(
						getGraphQLRelationName(
							relationJavaMethodSignature,
							javaMethodSignatures))) {

					return null;
				}

				if ((relationJavaMethodSignature.getParentSchemaName() !=
						null) &&
					!returnType.contains("Collection<")) {

					Schema parentSchema = schemas.get(
						relationJavaMethodSignature.getParentSchemaName());

					Map<String, Schema> parentPropertySchemas =
						parentSchema.getPropertySchemas();

					String parentProperty = _getParentProperty(
						relationJavaMethodSignature.getParentSchemaName(),
						javaMethodParameter, parentPropertySchemas.keySet());

					if (parentProperty == null) {
						continue;
					}
				}

				return relationJavaMethodSignature;
			}
		}

		return null;
	}

	private JavaMethodSignature _getJavaMethodSignature(
		JavaMethodSignature javaMethodSignature, String parentSchemaName) {

		return new JavaMethodSignature(
			javaMethodSignature.getPath(), javaMethodSignature.getPathItem(),
			javaMethodSignature.getOperation(),
			javaMethodSignature.getRequestBodyMediaTypes(),
			javaMethodSignature.getSchemaName(),
			javaMethodSignature.getJavaMethodParameters(),
			javaMethodSignature.getMethodName(),
			javaMethodSignature.getReturnType(), parentSchemaName);
	}

	private String _getParentProperty(
		String schemaName, JavaMethodParameter javaMethodParameter,
		Set<String> properties) {

		String parameterName = StringUtil.toLowerCase(
			javaMethodParameter.getParameterName());

		schemaName = StringUtil.toLowerCase(schemaName);

		String shortParameterName = StringUtil.removeSubstring(
			parameterName, schemaName);

		shortParameterName = StringUtil.removeSubstring(
			shortParameterName, "parent");

		for (String propertyKey : properties) {
			if (StringUtil.equalsIgnoreCase(parameterName, propertyKey) ||
				StringUtil.equalsIgnoreCase(shortParameterName, propertyKey)) {

				return StringUtil.upperCaseFirstLetter(propertyKey);
			}
		}

		return null;
	}

	private String _getRESTBody(
		JavaMethodSignature javaMethodSignature, OpenAPIYAML openAPIYAML) {

		StringBundler sb = new StringBundler();

		Set<String> properties = new TreeSet<>();

		Map<String, Schema> schemas = getSchemas(openAPIYAML);

		List<JavaMethodParameter> javaMethodParameters =
			javaMethodSignature.getJavaMethodParameters();

		for (JavaMethodParameter javaMethodParameter : javaMethodParameters) {
			String parameterType = javaMethodParameter.getParameterType();

			String schemaName = parameterType.substring(
				parameterType.lastIndexOf(".") + 1);

			if (schemas.containsKey(schemaName)) {
				Schema schema = schemas.get(schemaName);

				Map<String, Schema> propertySchemas =
					schema.getPropertySchemas();

				if (propertySchemas == null) {
					continue;
				}

				for (Map.Entry<String, Schema> entry :
						propertySchemas.entrySet()) {

					Schema propertySchema = entry.getValue();

					if (!propertySchema.isReadOnly()) {
						properties.add(entry.getKey());
					}
				}
			}
		}

		if (!properties.isEmpty()) {
			sb.append("-d $'{");

			Iterator<String> iterator = properties.iterator();

			while (iterator.hasNext()) {
				sb.append("\"");
				sb.append(iterator.next());
				sb.append("\": ___");

				if (iterator.hasNext()) {
					sb.append(", ");
				}
			}

			sb.append("}' --header 'Content-Type: application/json'");
		}

		return sb.toString();
	}

	private List<JavaMethodSignature> _getSortedJavaMethodSignatures(
			ConfigYAML configYAML, String graphQLType, OpenAPIYAML openAPIYAML)
		throws Exception {

		List<JavaMethodSignature> javaMethodSignatures =
			getGraphQLJavaMethodSignatures(
				configYAML, graphQLType, openAPIYAML);

		javaMethodSignatures.sort(
			Comparator.comparingInt(
				javaMethodSignature -> StringUtil.count(
					javaMethodSignature.getPath(), "/")));

		return javaMethodSignatures;
	}

	private boolean _isGraphQLPropertyRelation(
		JavaMethodSignature javaMethodSignature, String parameterName,
		String propertyName, String schemaName) {

		String returnType = StringUtil.toLowerCase(
			javaMethodSignature.getReturnType());
		String parameterSchemaName = StringUtil.removeSubstring(
			parameterName, "Id");

		if (!StringUtil.equalsIgnoreCase(schemaName, parameterSchemaName) &&
			StringUtil.equals(propertyName, parameterName) &&
			returnType.endsWith(StringUtil.toLowerCase(parameterSchemaName))) {

			return true;
		}

		return false;
	}

	private String _replaceGraphQLParameter(
		String parameterName, Map<String, String> substitutions) {

		for (Map.Entry<String, String> entry : substitutions.entrySet()) {
			if (parameterName.equals(entry.getKey())) {
				return entry.getValue();
			}
		}

		return parameterName;
	}

	private static final DateFormat _dateFormat = _getDateFormat("yyyy-MM-dd");
	private static final DateFormat _dateTimeDateFormat = _getDateFormat(
		DateUtil.ISO_8601_PATTERN);
	private static final FreeMarkerTool _freeMarkerTool = new FreeMarkerTool();

}