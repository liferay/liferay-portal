/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.search.index.util;

import com.liferay.mcp.server.rest.internal.search.index.MCPTool;
import com.liferay.mcp.server.rest.internal.search.index.constants.MCPToolConstants;
import com.liferay.mcp.server.rest.internal.util.OpenAPIUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Petteri Karttunen
 */
public class MCPToolFactoryUtil {

	public static List<MCPTool> getMCPTools(
		String basePath, JSONObject openAPIJSONObject, String toolSetName) {

		List<MCPTool> mcpTools = new ArrayList<>();

		JSONObject pathsJSONObject = openAPIJSONObject.getJSONObject("paths");

		if (pathsJSONObject == null) {
			return mcpTools;
		}

		for (String path : pathsJSONObject.keySet()) {
			JSONObject pathItemJSONObject = pathsJSONObject.getJSONObject(path);

			mcpTools.addAll(
				TransformUtil.transformToList(
					OpenAPIUtil.METHODS,
					method -> _getMCPTool(
						basePath, method, openAPIJSONObject, path,
						pathItemJSONObject, toolSetName)));
		}

		return mcpTools;
	}

	private static String _getCollection(
		boolean collection, String entityName, String method,
		String operationVariant, String path) {

		if (!collection || Validator.isNotNull(operationVariant) ||
			!Objects.equals(method, "get")) {

			return null;
		}

		String[] pathParts = StringUtil.split(path, CharPool.SLASH);

		String pathPart = pathParts[pathParts.length - 1];

		if (OpenAPIUtil.isPathParameter(pathPart) ||
			!_isEntityPathPart(entityName, pathPart)) {

			return null;
		}

		return WordUtil.normalize(pathPart);
	}

	private static String _getDescription(JSONObject operationJSONObject) {
		String description = StringUtil.trim(
			operationJSONObject.getString("description"));
		String summary = StringUtil.trim(
			operationJSONObject.getString("summary"));

		if (Validator.isBlank(summary)) {
			return description;
		}

		if (Validator.isBlank(description)) {
			return summary;
		}

		if (!StringUtil.endsWith(summary, CharPool.PERIOD)) {
			summary += StringPool.PERIOD;
		}

		return summary + StringPool.SPACE + description;
	}

	private static String _getEntityName(JSONObject operationJSONObject) {
		JSONArray tagsJSONArray = operationJSONObject.getJSONArray("tags");

		if (JSONUtil.isEmpty(tagsJSONArray)) {
			return StringPool.BLANK;
		}

		return tagsJSONArray.getString(0);
	}

	private static String[] _getExpansions(
		boolean collection, String entityPhrase, String intent,
		String operationVariant, String path, String toolAction,
		String toolName) {

		if (Validator.isNull(entityPhrase) || Validator.isNull(intent)) {
			return new String[0];
		}

		boolean batch = Objects.equals(
			operationVariant, MCPToolConstants.OPERATION_VARIANT_BATCH);

		String toolNameStem = _getToolNameStem(
			collection, operationVariant, toolAction, toolName);

		String parentEntityName = _getParentEntityName(
			entityPhrase, path, toolNameStem);

		if (parentEntityName != null) {
			toolNameStem = toolNameStem.substring(parentEntityName.length());
		}

		String lastPathParameterPhrase = _getLastPathParameterPhrase(path);

		Target target = _getTarget(
			entityPhrase, lastPathParameterPhrase,
			_isPluralTarget(batch, collection, path, toolAction), toolNameStem);

		String parentEntityPhrase = _getParentEntityPhrase(parentEntityName);

		String parameterPhrase = _getParameterPhrase(
			lastPathParameterPhrase, parentEntityPhrase, target);

		return TransformUtil.transformToArray(
			IntentUtil.getPrimaryVerbs(intent),
			verb -> _toExpansion(
				batch, parameterPhrase, parentEntityPhrase, target, verb),
			String.class);
	}

	private static String _getLastPathParameterPhrase(String path) {
		String lastPathParameter = OpenAPIUtil.getLastPathParameter(path);

		if (lastPathParameter == null) {
			return StringPool.BLANK;
		}

		return WordUtil.toPhrase(lastPathParameter);
	}

	private static MCPTool _getMCPTool(
		String basePath, String method, JSONObject openAPIJSONObject,
		String path, JSONObject pathItemJSONObject, String toolSetName) {

		JSONObject operationJSONObject = pathItemJSONObject.getJSONObject(
			method);

		if (operationJSONObject == null) {
			return null;
		}

		String toolName = operationJSONObject.getString("operationId");

		if (Validator.isBlank(toolName)) {
			return null;
		}

		String toolAction = _getToolAction(method, path, toolName);

		String entityName = _getEntityName(operationJSONObject);

		String operationVariant = _getOperationVariant(entityName, path);

		if (Objects.equals(
				operationVariant,
				MCPToolConstants.OPERATION_VARIANT_TRAVERSAL)) {

			entityName = _getTraversalEntityName(entityName, path, toolName);
		}

		JSONObject responseSchemaJSONObject =
			OpenAPIUtil.getResponseSchemaJSONObject(operationJSONObject);

		boolean collection = OpenAPIUtil.isCollectionSchema(
			openAPIJSONObject, responseSchemaJSONObject);

		String intent = IntentUtil.getToolIntent(
			collection, method, toolAction);

		JSONObject requestBodySchemaJSONObject =
			OpenAPIUtil.getRequestBodySchemaJSONObject(
				openAPIJSONObject, operationJSONObject);

		return new MCPTool(
			_getCollection(
				collection, entityName, method, operationVariant, path),
			operationJSONObject.getBoolean("deprecated"),
			_getDescription(operationJSONObject), entityName,
			_getExpansions(
				collection, WordUtil.toPhrase(entityName), intent,
				operationVariant, path, toolAction, toolName),
			intent, method, operationVariant,
			_getParameterNames(operationJSONObject, pathItemJSONObject),
			Portal.PATH_MODULE + basePath + path,
			_getRequiredPropertyCollections(
				openAPIJSONObject, path, requestBodySchemaJSONObject),
			_getSchemaPropertyNames(
				openAPIJSONObject, requestBodySchemaJSONObject,
				responseSchemaJSONObject),
			toolName, toolSetName);
	}

	private static String _getOperationVariant(String entityName, String path) {
		String operationVariant = _getPathPartOperationVariant(path);

		if (Validator.isNotNull(operationVariant)) {
			return operationVariant;
		}

		if (_hasNestedExternalReferenceCodes(path)) {
			return MCPToolConstants.OPERATION_VARIANT_NESTED;
		}

		if (_isTraversal(entityName, path)) {
			return MCPToolConstants.OPERATION_VARIANT_TRAVERSAL;
		}

		return StringPool.BLANK;
	}

	private static String[] _getParameterNames(
		JSONObject operationJSONObject, JSONObject pathItemJSONObject) {

		Set<String> parameterNames = new LinkedHashSet<>();

		parameterNames.addAll(
			JSONUtil.toStringList(
				operationJSONObject.getJSONArray("parameters"), "name"));
		parameterNames.addAll(
			JSONUtil.toStringList(
				pathItemJSONObject.getJSONArray("parameters"), "name"));

		return parameterNames.toArray(new String[0]);
	}

	private static String _getParameterPhrase(
		String lastPathParameterPhrase, String parentEntityPhrase,
		Target target) {

		if (Validator.isNull(lastPathParameterPhrase) ||
			StringUtil.contains(
				target.toString(), lastPathParameterPhrase, StringPool.SPACE)) {

			return StringPool.BLANK;
		}

		for (String word :
				StringUtil.split(lastPathParameterPhrase, CharPool.SPACE)) {

			if (StringUtil.contains(
					parentEntityPhrase, word, StringPool.SPACE)) {

				return StringPool.BLANK;
			}
		}

		return "by " + lastPathParameterPhrase;
	}

	private static String _getParentCollection(String path) {
		String[] pathParts = StringUtil.split(path, CharPool.SLASH);

		for (int i = 1; i < (pathParts.length - 1); i++) {
			if (!OpenAPIUtil.isPathParameter(pathParts[i])) {
				continue;
			}

			String pathPart = pathParts[i - 1];

			if ((i >= 2) && StringUtil.startsWith(pathPart, "by-")) {
				pathPart = pathParts[i - 2];
			}

			if (Validator.isNull(pathPart) ||
				StringUtil.startsWith(pathPart, "by-")) {

				return null;
			}

			return pathPart;
		}

		return null;
	}

	private static String _getParentEntityName(
		String entityPhrase, String path, String toolNameStem) {

		String parentCollection = _getParentCollection(path);

		if (parentCollection == null) {
			return null;
		}

		String parentEntityName = _toEntityName(parentCollection);

		if (!toolNameStem.startsWith(parentEntityName)) {
			return null;
		}

		int length = parentEntityName.length();

		if ((toolNameStem.length() <= length) ||
			!Character.isUpperCase(toolNameStem.charAt(length)) ||
			StringUtil.contains(
				entityPhrase, WordUtil.toPhrase(parentEntityName),
				StringPool.SPACE)) {

			return null;
		}

		return parentEntityName;
	}

	private static String _getParentEntityPhrase(String parentEntityName) {
		if (parentEntityName == null) {
			return StringPool.BLANK;
		}

		String parentEntityPhrase = WordUtil.toPhrase(parentEntityName);

		if ("aeio".indexOf(parentEntityPhrase.charAt(0)) >= 0) {
			return "in an " + parentEntityPhrase;
		}

		return "in a " + parentEntityPhrase;
	}

	private static String _getPathPartOperationVariant(String path) {
		String[] pathParts = StringUtil.split(path, CharPool.SLASH);

		for (int i = pathParts.length - 1; i >= 0; i--) {
			String pathPart = pathParts[i];

			if (Validator.isNull(pathPart) ||
				OpenAPIUtil.isPathParameter(pathPart)) {

				continue;
			}

			String operationVariant = _pathPartOperationVariants.get(
				_toPathPartOperationVariantKey(pathPart));

			if (operationVariant != null) {
				return operationVariant;
			}
		}

		return StringPool.BLANK;
	}

	private static String _getPropertyCollection(String propertyName) {
		String referenceEntityName = null;

		for (String suffix : _REFERENCE_PROPERTY_NAME_SUFFIXES) {
			if (propertyName.endsWith(suffix) &&
				(propertyName.length() > suffix.length())) {

				referenceEntityName = propertyName.substring(
					0, propertyName.length() - suffix.length());

				break;
			}
		}

		if (referenceEntityName == null) {
			return null;
		}

		return WordUtil.normalize(
			WordUtil.toPlural(WordUtil.toPhrase(referenceEntityName)));
	}

	private static Set<String> _getPropertyNames(
		JSONObject openAPIJSONObject, JSONObject schemaJSONObject) {

		Set<String> propertyNames = new LinkedHashSet<>();

		Map<String, JSONObject> propertiesJSONObjects =
			OpenAPIUtil.getPropertiesJSONObjects(
				openAPIJSONObject, schemaJSONObject);

		for (Map.Entry<String, JSONObject> entry :
				propertiesJSONObjects.entrySet()) {

			String propertyName = entry.getKey();

			if (propertyName.startsWith("x-") ||
				OpenAPIUtil.isNestedSchema(entry.getValue())) {

				continue;
			}

			propertyNames.add(propertyName);
		}

		return propertyNames;
	}

	private static String[] _getRequiredPropertyCollections(
		JSONObject openAPIJSONObject, String path,
		JSONObject requestBodySchemaJSONObject) {

		Set<String> propertyNames = _getPropertyNames(
			openAPIJSONObject, requestBodySchemaJSONObject);

		return TransformUtil.transformToArray(
			OpenAPIUtil.getRequiredPropertyNames(
				openAPIJSONObject, requestBodySchemaJSONObject),
			propertyName -> {
				if (path.contains("{" + propertyName + "}") ||
					!propertyNames.contains(propertyName)) {

					return null;
				}

				String propertyCollection = _getPropertyCollection(
					propertyName);

				if (propertyCollection == null) {
					return null;
				}

				return propertyName + StringPool.POUND + propertyCollection;
			},
			String.class);
	}

	private static String[] _getSchemaPropertyNames(
		JSONObject openAPIJSONObject, JSONObject requestBodySchemaJSONObject,
		JSONObject responseSchemaJSONObject) {

		Set<String> propertyNames = _getPropertyNames(
			openAPIJSONObject, requestBodySchemaJSONObject);

		propertyNames.addAll(
			_getPropertyNames(openAPIJSONObject, responseSchemaJSONObject));

		return propertyNames.toArray(new String[0]);
	}

	private static Target _getTarget(
		String entityPhrase, String lastPathParameterPhrase, boolean plural,
		String toolNameStem) {

		Target target = _getTarget(
			entityPhrase, lastPathParameterPhrase, toolNameStem);

		if (!plural) {
			return target;
		}

		return new Target(
			WordUtil.toPlural(target._noun), target._wordsAfter,
			target._wordsBefore);
	}

	private static Target _getTarget(
		String entityPhrase, String lastPathParameterPhrase,
		String toolNameStem) {

		String toolNamePhrase = _removePrefix(
			WordUtil.toPhrase(toolNameStem),
			lastPathParameterPhrase + StringPool.SPACE);

		String[] toolNameWords = StringUtil.split(
			toolNamePhrase, CharPool.SPACE);

		Set<String> entityWords = new HashSet<>();

		for (String word : StringUtil.split(entityPhrase, CharPool.SPACE)) {
			entityWords.add(WordUtil.toSingular(word));
		}

		int firstEntityWordIndex = toolNameWords.length;
		int lastEntityWordIndex = toolNameWords.length;

		for (int i = 0; i < toolNameWords.length; i++) {
			if (!entityWords.contains(WordUtil.toSingular(toolNameWords[i]))) {
				continue;
			}

			if (firstEntityWordIndex == toolNameWords.length) {
				firstEntityWordIndex = i;
			}

			lastEntityWordIndex = i;
		}

		if (firstEntityWordIndex == toolNameWords.length) {
			if (StringUtil.startsWith(toolNamePhrase, "by ")) {
				return new Target(
					entityPhrase, toolNamePhrase, StringPool.BLANK);
			}

			return _getToolNameTarget(ListUtil.fromArray(toolNameWords));
		}

		List<String> wordsAfterEntity = new ArrayList<>();
		List<String> wordsBeforeEntity = new ArrayList<>();
		List<String> wordsBetweenEntityWords = new ArrayList<>();

		for (int i = 0; i < toolNameWords.length; i++) {
			if (i < firstEntityWordIndex) {
				wordsBeforeEntity.add(toolNameWords[i]);
			}
			else if (i > lastEntityWordIndex) {
				wordsAfterEntity.add(toolNameWords[i]);
			}
			else if (!entityWords.contains(
						WordUtil.toSingular(toolNameWords[i]))) {

				wordsBetweenEntityWords.add(toolNameWords[i]);
			}
		}

		String wordsBefore = StringUtil.merge(
			wordsBeforeEntity, StringPool.SPACE);

		if (StringUtil.startsWith(wordsBefore, "by ")) {
			wordsBefore = StringPool.BLANK;
		}

		String wordsAfter = StringUtil.merge(
			wordsAfterEntity, StringPool.SPACE);

		String wordsBetween = StringUtil.merge(
			wordsBetweenEntityWords, StringPool.SPACE);

		if (Validator.isNull(wordsAfter) &&
			StringUtil.startsWith(wordsBetween, "by ")) {

			wordsAfter = wordsBetween;
		}

		if (Validator.isNull(wordsAfter)) {
			return new Target(entityPhrase, StringPool.BLANK, wordsBefore);
		}

		if (_isTrailingCollection(wordsAfter)) {
			return new Target(
				wordsAfter, StringPool.BLANK,
				StringUtil.trim(wordsBefore + StringPool.SPACE + entityPhrase));
		}

		return new Target(entityPhrase, wordsAfter, wordsBefore);
	}

	private static String _getToolAction(
		String method, String path, String toolName) {

		if (_readOnlyMethods.contains(method)) {
			return null;
		}

		for (String toolAction : IntentUtil.getToolActions()) {
			if (toolName.endsWith(toolAction) &&
				_hasToolActionPathPart(path, toolAction)) {

				return toolAction;
			}
		}

		return null;
	}

	private static String _getToolNameStem(
		boolean collection, String operationVariant, String toolAction,
		String toolName) {

		Matcher matcher = _methodPrefixPattern.matcher(toolName);

		String toolNameStem = matcher.replaceFirst(StringPool.BLANK);

		if (toolAction != null) {
			toolNameStem = _removeSuffix(toolNameStem, toolAction);
		}

		if (collection ||
			Objects.equals(
				operationVariant, MCPToolConstants.OPERATION_VARIANT_EXPORT)) {

			toolNameStem = _removeSuffix(toolNameStem, "Page");
		}

		if (Objects.equals(
				operationVariant, MCPToolConstants.OPERATION_VARIANT_BATCH)) {

			toolNameStem = _removeSuffix(toolNameStem, "Batch");
		}

		return toolNameStem;
	}

	private static Target _getToolNameTarget(List<String> words) {
		int index = words.indexOf("by");

		if (index < 0) {
			return new Target(
				StringUtil.merge(words, StringPool.SPACE), StringPool.BLANK,
				StringPool.BLANK);
		}

		return new Target(
			StringUtil.merge(words.subList(0, index), StringPool.SPACE),
			StringUtil.merge(
				words.subList(index, words.size()), StringPool.SPACE),
			StringPool.BLANK);
	}

	private static String _getTraversalEntityName(
		String entityName, String path, String toolName) {

		if (!StringUtil.contains(
				_toSingularPhrase(toolName), _toSingularPhrase(entityName),
				StringPool.SPACE)) {

			return entityName;
		}

		String[] pathParts = StringUtil.split(path, CharPool.SLASH);

		for (int i = pathParts.length - 1; i >= 0; i--) {
			String pathPart = pathParts[i];

			if (OpenAPIUtil.isPathParameter(pathPart) ||
				StringUtil.startsWith(pathPart, "by-") ||
				!_isCollectionPathPart(pathPart)) {

				continue;
			}

			return _toEntityName(pathPart);
		}

		return entityName;
	}

	private static boolean _hasNestedExternalReferenceCodes(String path) {
		int count = 0;

		for (String pathPart : StringUtil.split(path, CharPool.SLASH)) {
			if (!StringUtil.startsWith(
					StringUtil.toLowerCase(pathPart), "by-external")) {

				continue;
			}

			count++;
		}

		if (count > 1) {
			return true;
		}

		return false;
	}

	private static boolean _hasToolActionPathPart(
		String path, String toolAction) {

		String normalizedToolAction = WordUtil.normalize(toolAction);

		for (String pathPart : StringUtil.split(path, CharPool.SLASH)) {
			if (Validator.isNull(pathPart) ||
				OpenAPIUtil.isPathParameter(pathPart)) {

				continue;
			}

			String normalizedPathPart = WordUtil.normalize(pathPart);

			if (normalizedPathPart.endsWith(normalizedToolAction) ||
				normalizedToolAction.startsWith(
					WordUtil.toSingular(normalizedPathPart))) {

				return true;
			}
		}

		return false;
	}

	private static boolean _isCollectionPathPart(String pathPart) {
		if (StringUtil.startsWith(pathPart, "has-") ||
			StringUtil.startsWith(pathPart, "is-")) {

			return false;
		}

		return WordUtil.isPlural(pathPart);
	}

	private static boolean _isCollectionToolAction(String path) {
		String[] pathParts = StringUtil.split(path, CharPool.SLASH);

		if (pathParts.length < 2) {
			return false;
		}

		return _isCollectionPathPart(pathParts[pathParts.length - 2]);
	}

	private static boolean _isEntityPathPart(
		String entityName, String pathPart) {

		String normalizedPathPart = WordUtil.normalize(
			_toSingularPhrase(_toEntityName(pathPart)));

		if (Validator.isNull(normalizedPathPart)) {
			return false;
		}

		String normalizedEntityName = WordUtil.normalize(
			_toSingularPhrase(entityName));

		return normalizedEntityName.endsWith(normalizedPathPart);
	}

	private static boolean _isPluralTarget(
		boolean batch, boolean collection, String path, String toolAction) {

		if (toolAction != null) {
			return _isCollectionToolAction(path);
		}

		if (batch || collection) {
			return true;
		}

		return false;
	}

	private static boolean _isTrailingCollection(String value) {
		if (!StringUtil.startsWith(value, "by ") && WordUtil.isPlural(value)) {
			return true;
		}

		return false;
	}

	private static boolean _isTraversal(String entityName, String path) {
		if (Validator.isNull(entityName)) {
			return false;
		}

		List<String> pathPartsAfterFirstParameter = new ArrayList<>();

		boolean parameterSeen = false;

		for (String pathPart : StringUtil.split(path, CharPool.SLASH)) {
			if (Validator.isNull(pathPart)) {
				continue;
			}

			if (OpenAPIUtil.isPathParameter(pathPart)) {
				parameterSeen = true;

				continue;
			}

			if (!parameterSeen) {
				continue;
			}

			pathPartsAfterFirstParameter.add(pathPart);
		}

		for (int i = pathPartsAfterFirstParameter.size() - 1; i >= 0; i--) {
			String pathPart = pathPartsAfterFirstParameter.get(i);

			if (_isEntityPathPart(entityName, pathPart)) {
				return false;
			}

			if (_isCollectionPathPart(pathPart)) {
				return true;
			}
		}

		return false;
	}

	private static String _removePrefix(String value, String prefix) {
		if (value.startsWith(prefix)) {
			return value.substring(prefix.length());
		}

		return value;
	}

	private static String _removeSuffix(String value, String suffix) {
		if (value.endsWith(suffix)) {
			return value.substring(0, value.length() - suffix.length());
		}

		return value;
	}

	private static String _toEntityName(String value) {
		StringBundler sb = new StringBundler();

		for (String word :
				StringUtil.split(WordUtil.toSingular(value), CharPool.DASH)) {

			sb.append(StringUtil.upperCaseFirstLetter(word));
		}

		return sb.toString();
	}

	private static String _toExpansion(
		boolean batch, String parameterPhrase, String parentEntityPhrase,
		Target target, String verb) {

		StringBundler sb = new StringBundler(12);

		if (batch) {
			sb.append("batch ");
		}

		sb.append(verb);
		sb.append(StringPool.SPACE);

		if (Validator.isNotNull(target._wordsBefore)) {
			sb.append(target._wordsBefore);
			sb.append(StringPool.SPACE);
		}

		sb.append(target._noun);

		if (Validator.isNotNull(parentEntityPhrase)) {
			sb.append(StringPool.SPACE);
			sb.append(parentEntityPhrase);
		}

		if (Validator.isNotNull(target._wordsAfter)) {
			sb.append(StringPool.SPACE);
			sb.append(target._wordsAfter);
		}

		if (Validator.isNotNull(parameterPhrase)) {
			sb.append(StringPool.SPACE);
			sb.append(parameterPhrase);
		}

		return sb.toString();
	}

	private static String _toPathPartOperationVariantKey(String pathPart) {
		pathPart = StringUtil.toLowerCase(pathPart);

		int index = pathPart.indexOf(CharPool.PERIOD);

		if (index > 0) {
			pathPart = pathPart.substring(0, index);
		}

		return _removeSuffix(pathPart, "-replace");
	}

	private static String _toSingularPhrase(String value) {
		StringBundler sb = new StringBundler();

		for (String word :
				StringUtil.split(WordUtil.toPhrase(value), CharPool.SPACE)) {

			sb.append(WordUtil.toSingular(word));
			sb.append(StringPool.SPACE);
		}

		return StringUtil.trim(sb.toString());
	}

	private static final String[] _REFERENCE_PROPERTY_NAME_SUFFIXES = {
		"ExternalReferenceCode", "ExternalReferenceCodes", "Id", "Ids", "Key",
		"Keys"
	};

	private static final Pattern _methodPrefixPattern = Pattern.compile(
		StringBundler.concat(
			"^(", StringUtil.merge(OpenAPIUtil.METHODS, StringPool.PIPE), ")"));
	private static final Map<String, String> _pathPartOperationVariants =
		HashMapBuilder.put(
			"approved", MCPToolConstants.OPERATION_VARIANT_APPROVED
		).put(
			"batch", MCPToolConstants.OPERATION_VARIANT_BATCH
		).put(
			"by-key", MCPToolConstants.OPERATION_VARIANT_KEYED
		).put(
			"by-uuid", MCPToolConstants.OPERATION_VARIANT_KEYED
		).put(
			"copy", MCPToolConstants.OPERATION_VARIANT_COPY
		).put(
			"expire", MCPToolConstants.OPERATION_VARIANT_EXPIRE
		).put(
			"export-batch", MCPToolConstants.OPERATION_VARIANT_EXPORT
		).put(
			"export-preview", MCPToolConstants.OPERATION_VARIANT_PREVIEW
		).put(
			"friendly-url-history", MCPToolConstants.OPERATION_VARIANT_HISTORY
		).put(
			"import-preview", MCPToolConstants.OPERATION_VARIANT_PREVIEW
		).put(
			"move", MCPToolConstants.OPERATION_VARIANT_MOVE
		).put(
			"my-rating", MCPToolConstants.OPERATION_VARIANT_RATING
		).put(
			"openapi", MCPToolConstants.OPERATION_VARIANT_OPENAPI
		).put(
			"permissions", MCPToolConstants.OPERATION_VARIANT_PERMISSIONS
		).put(
			"preview", MCPToolConstants.OPERATION_VARIANT_PREVIEW
		).put(
			"rated-by-me", MCPToolConstants.OPERATION_VARIANT_RATING
		).put(
			"restore", MCPToolConstants.OPERATION_VARIANT_RESTORE
		).put(
			"subscribe", MCPToolConstants.OPERATION_VARIANT_SUBSCRIPTION
		).put(
			"translation", MCPToolConstants.OPERATION_VARIANT_TRANSLATION
		).put(
			"translations", MCPToolConstants.OPERATION_VARIANT_TRANSLATION
		).put(
			"unsubscribe", MCPToolConstants.OPERATION_VARIANT_SUBSCRIPTION
		).put(
			"validate", MCPToolConstants.OPERATION_VARIANT_VALIDATE
		).build();
	private static final Set<String> _readOnlyMethods = SetUtil.fromArray(
		"get", "head", "options");

	private static class Target {

		@Override
		public String toString() {
			return StringUtil.trim(
				StringBundler.concat(
					_wordsBefore, StringPool.SPACE, _noun, StringPool.SPACE,
					_wordsAfter));
		}

		private Target(String noun, String wordsAfter, String wordsBefore) {
			_noun = noun;
			_wordsAfter = wordsAfter;
			_wordsBefore = wordsBefore;
		}

		private final String _noun;
		private final String _wordsAfter;
		private final String _wordsBefore;

	}

}