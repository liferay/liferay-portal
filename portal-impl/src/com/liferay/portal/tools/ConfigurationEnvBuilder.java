/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.lang.reflect.Field;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Matthew Tambara
 */
public class ConfigurationEnvBuilder {

	public static void main(String[] args) throws IOException {
		Map<String, String> arguments = ArgumentsUtil.parseArguments(args);

		Path languagePropertiesPath = Paths.get(
			arguments.get("language.properties.file"));

		File languagePropertiesFile = languagePropertiesPath.toFile();

		if (languagePropertiesFile.exists()) {
			_languageProperties.load(new FileReader(languagePropertiesFile));
		}

		Path basePath = Paths.get(".");

		List<ObjectDef> objectDefs = getObjectDefs(
			basePath.toRealPath(),
			StringUtil.split(arguments.get("configuration.java.files"), '\n'));

		String propertiesOutputFile = arguments.get("properties.output.file");

		if (Validator.isNotNull(propertiesOutputFile)) {
			Path propertiesOutputPath = Paths.get(propertiesOutputFile);

			String content = new String(
				Files.readAllBytes(propertiesOutputPath));

			int index = content.indexOf("##\n## OSGi Configuration Overrides");

			content = content.substring(0, index);

			content = content.concat(buildContent(objectDefs));

			Files.write(propertiesOutputPath, content.getBytes());
		}

		String jsonSchemaOutputFile = arguments.get("json.schema.output.file");

		if (Validator.isNotNull(jsonSchemaOutputFile)) {
			String json = _generateJSON(objectDefs);

			Files.write(Paths.get(jsonSchemaOutputFile), json.getBytes());
		}
	}

	protected static String buildContent(List<ObjectDef> objectDefs) {
		StringBundler sb = new StringBundler();

		sb.append("##\n## OSGi Configuration Overrides\n##\n");

		for (ObjectDef objectDef : objectDefs) {
			String fullyQualifiedName = objectDef.pid;

			for (AttributeDef attributeDef : objectDef.attributeDefs) {
				String configurationKey = StringBundler.concat(
					"configuration.override.", fullyQualifiedName,
					StringPool.UNDERLINE, attributeDef.name);

				sb.append("\n");
				sb.append("    #\n");
				sb.append("    # Env: ");
				sb.append(
					ToolsUtil.encodeEnvironmentProperty(configurationKey));
				sb.append("\n");
				sb.append("    #\n");
				sb.append("    #");
				sb.append(configurationKey);
				sb.append(StringPool.EQUAL);
			}
		}

		return sb.toString();
	}

	protected static List<ObjectDef> getObjectDefs(
			Path basePath, String[] configurationFilePaths)
		throws IOException {

		List<ObjectDef> objectDefs = new ArrayList<>();

		Path realPath = basePath.toRealPath();

		for (String configurationFilePath : configurationFilePaths) {
			ObjectDef objectDef = _createObjectDef(
				realPath, configurationFilePath);

			if (objectDef != null) {
				objectDefs.add(objectDef);
			}
		}

		return _postProcessObjectDefs(objectDefs);
	}

	protected static class AttributeDef implements Comparable<AttributeDef> {

		@Override
		public int compareTo(AttributeDef attributeDef) {
			return name.compareTo(attributeDef.name);
		}

		@Override
		public boolean equals(Object object) {
			if (!Objects.equals(AttributeDef.class, object.getClass())) {
				return false;
			}

			AttributeDef attributeDef = (AttributeDef)object;

			return Objects.equals(name, attributeDef.name);
		}

		@Override
		public int hashCode() {
			String string = toJSONObject().toString();

			return string.hashCode();
		}

		protected boolean isArray() {
			return Objects.equals(type, "array");
		}

		protected boolean isBoolean() {
			return Objects.equals(type, "boolean");
		}

		protected boolean isNumber() {
			return Objects.equals(type, "number");
		}

		protected boolean isObject() {
			return Objects.equals(type, "object");
		}

		protected boolean isString() {
			return Objects.equals(type, "string");
		}

		protected JSONObject toJSONObject() {
			JSONObject jsonObject = _toJSONObject(
				"default", () -> defaultValue
			).put(
				"deprecated", () -> deprecated
			).put(
				"description", () -> description
			).put(
				"title", () -> title
			).put(
				"type", () -> type
			);

			if (isArray()) {
				jsonObject.put("items", _toJSONObject("type", "string"));
			}

			if (isObject()) {
				jsonObject.put("properties", _jsonFactory.createJSONObject());
			}

			if (isNumber()) {
				jsonObject.put(
					"max", () -> max
				).put(
					"min", () -> min
				);
			}

			if (isString()) {
				jsonObject.put(
					"maxLength", () -> max
				).put(
					"minLength", () -> min
				);
			}

			if (ArrayUtil.isNotEmpty(optionValues)) {
				JSONArray optionValuesJSONArray = _toJSONArray(optionValues);

				if (isArray()) {
					jsonObject.getJSONObject(
						"items"
					).put(
						"enum", optionValuesJSONArray
					);
				}
				else {
					jsonObject.put("enum", optionValuesJSONArray);
				}
			}

			return jsonObject;
		}

		protected Object defaultValue;
		protected Boolean deprecated;
		protected String description;
		protected boolean hasMetaAnnotation;
		protected Number max;
		protected Number min;
		protected String name;
		protected String[] optionLabels;
		protected Object[] optionValues;
		protected boolean required = true;
		protected boolean requiredInput;
		protected String title;
		protected String type;

	}

	protected static class ObjectDef implements Comparable<ObjectDef> {

		@Override
		public int compareTo(ObjectDef objectDef) {
			return pid.compareTo(objectDef.pid);
		}

		protected boolean extendsObjectDef(ObjectDef superObjectDef) {
			return Objects.equals(
				extendsInterfaceName, superObjectDef.interfaceName);
		}

		protected JSONObject toJSONObject() {
			JSONObject jsonObject = _toJSONObject(
				"description", () -> description
			).put(
				"properties",
				_toJSONObject(
					"pid",
					_toJSONObject(
						"const", pid
					).put(
						"description", () -> description
					).put(
						"title", () -> title
					))
			).put(
				"required", _toJSONArray("pid")
			).put(
				"title", () -> title
			);

			for (AttributeDef attributeDef : attributeDefs) {
				if (attributeDef.required) {
					jsonObject.getJSONArray(
						"required"
					).put(
						attributeDef.name
					);
				}

				jsonObject.getJSONObject(
					"properties"
				).put(
					attributeDef.name, attributeDef.toJSONObject()
				);
			}

			return jsonObject;
		}

		protected List<AttributeDef> attributeDefs = new ArrayList<>();
		protected String category;
		protected String description;
		protected String extendsInterfaceName;
		protected boolean hasMetaAnnotation;
		protected String interfaceName;
		protected String pid;
		protected String scope = "system";
		protected String title;

	}

	private static ObjectDef _createObjectDef(
		Path basePath, String configurationFilePath) {

		List<String> lines = null;

		try {
			String realString = basePath.toString();

			lines = Files.readAllLines(
				Paths.get(realString, configurationFilePath));
		}
		catch (IOException ioException) {
			_log.error(
				String.format(
					"Could not read configuration file %s%n",
					configurationFilePath),
				ioException);

			return null;
		}

		ObjectDef objectDef = new ObjectDef();

		AttributeDef attributeDef = new AttributeDef();

		for (String line : lines) {
			if (objectDef.interfaceName == null) {
				_withMatcher(line, objectDef, _objectDefCategoryPattern);
				_withMatcher(
					line, objectDef, _objectDefDescriptionPattern,
					(ObjectDef curObjectDef) ->
						curObjectDef.description = _lang(
							curObjectDef.description));
				_withMatcher(
					line, objectDef, _objectDefMetaAnnotationPattern,
					(ObjectDef curObjectDef) ->
						curObjectDef.hasMetaAnnotation = true);
				_withMatcher(line, objectDef, _objectDefPidPattern);
				_withMatcher(
					line, objectDef, _objectDefScopePattern,
					(ObjectDef curObjectDef) ->
						curObjectDef.scope = StringUtil.lowerCase(
							curObjectDef.scope));
				_withMatcher(
					line, objectDef, _objectDefTitlePattern,
					(ObjectDef curObjectDef) ->
						curObjectDef.title = _lang(curObjectDef.title));

				_withMatcher(line, objectDef, _objectDefInterfaceNamePattern);

				continue;
			}

			_withMatcher(
				line, objectDef, _objectDefExtendsInterfaceNamePattern);

			if (!StringUtil.startsWith(objectDef.pid, "com.liferay")) {
				String fullyQualifiedName = configurationFilePath.substring(
					configurationFilePath.indexOf(
						StringBundler.concat("com", File.separator, "liferay")),
					configurationFilePath.indexOf(".java"));

				fullyQualifiedName = StringUtil.replace(
					fullyQualifiedName, File.separator, StringPool.PERIOD);

				objectDef.pid = fullyQualifiedName;
			}

			_withMatcher(line, attributeDef, _attributeDefaultValuePattern);
			_withMatcher(
				line, attributeDef, _attributeDeprecatedPattern,
				(AttributeDef curAttributeDef) ->
					curAttributeDef.deprecated = true);
			_withMatcher(
				line, attributeDef, _attributeDescriptionPattern,
				(AttributeDef curAttributeDef) ->
					curAttributeDef.description = _lang(
						curAttributeDef.description));
			_withMatcher(line, attributeDef, _attributeMaxPattern);
			_withMatcher(
				line, attributeDef, _attributeDefMetaAnnotationPattern,
				(AttributeDef curAttributeDef) ->
					curAttributeDef.hasMetaAnnotation = true);
			_withMatcher(line, attributeDef, _attributeMinPattern);
			_withMatcher(line, attributeDef, _attributeOptionLabelsPattern);
			_withMatcher(line, attributeDef, _attributeOptionValuesPattern);
			_withMatcher(line, attributeDef, _attributeRequiredInputPattern);
			_withMatcher(line, attributeDef, _attributeRequiredPattern);
			_withMatcher(
				line, attributeDef, _attributeTitlePattern,
				(AttributeDef curAttributeDef) ->
					curAttributeDef.title = _lang(curAttributeDef.title));

			_withMatcher(line, attributeDef, _attributeTypeNamePattern);

			if (attributeDef.name == null) {
				continue;
			}

			if (attributeDef.requiredInput) {
				attributeDef.required = true;
			}

			if (attributeDef.defaultValue != null) {
				if (attributeDef.isBoolean()) {
					attributeDef.defaultValue = _toBoolean(
						attributeDef.defaultValue);
				}

				if (attributeDef.isNumber()) {
					attributeDef.defaultValue = _toNumber(
						String.valueOf(attributeDef.defaultValue));
				}

				if (StringUtil.startsWith(
						String.valueOf(attributeDef.defaultValue), "${")) {

					attributeDef.defaultValue = null;
				}
			}

			if (ArrayUtil.isNotEmpty(attributeDef.optionValues) &&
				attributeDef.isNumber()) {

				Number[] optionValues = {};

				for (Object optionValue : attributeDef.optionValues) {
					optionValues = ArrayUtil.append(
						optionValues, _toNumber(String.valueOf(optionValue)));
				}

				attributeDef.optionValues = optionValues;
			}

			if (objectDef.hasMetaAnnotation || attributeDef.hasMetaAnnotation) {
				objectDef.attributeDefs.add(attributeDef);
			}

			attributeDef = new AttributeDef();
		}

		if (ListUtil.isEmpty(objectDef.attributeDefs)) {
			return null;
		}

		return objectDef;
	}

	private static String _generateJSON(List<ObjectDef> objectDefs) {
		JSONObject schemaJSONObject = _toJSONObject(
			jsonObject -> jsonObject.put(
				"oneOf", _toJSONArray()
			).put(
				"properties",
				_toJSONObject("pid", _toJSONObject("enum", _toJSONArray()))
			));

		for (ObjectDef objectDef : objectDefs) {
			if (!Objects.equals(objectDef.scope, "company")) {
				if (_log.isInfoEnabled()) {
					_log.info(
						String.format(
							"Scope for %s is %s, SKIPPING %n", objectDef.pid,
							objectDef.scope));
				}

				continue;
			}

			schemaJSONObject.getJSONArray(
				"oneOf"
			).put(
				objectDef.toJSONObject()
			);

			schemaJSONObject.getJSONObject(
				"properties"
			).getJSONObject(
				"pid"
			).getJSONArray(
				"enum"
			).put(
				objectDef.pid
			);
		}

		return JSONUtil.toString(schemaJSONObject);
	}

	private static String _lang(String key) {
		return _languageProperties.getProperty(key, key);
	}

	private static List<ObjectDef> _postProcessObjectDefs(
		List<ObjectDef> objectDefs) {

		List<ObjectDef> newObjectDefs = new ArrayList<>();

		for (ObjectDef objectDef : objectDefs) {
			if (!objectDef.hasMetaAnnotation) {
				continue;
			}

			if (!Validator.isBlank(objectDef.extendsInterfaceName)) {
				objectDefs.stream(
				).filter(
					objectDef::extendsObjectDef
				).limit(
					1
				).flatMap(
					objectDef1 -> objectDef1.attributeDefs.stream()
				).filter(
					attributeDef -> !objectDef.attributeDefs.contains(
						attributeDef)
				).forEach(
					objectDef.attributeDefs::add
				);
			}

			Collections.sort(objectDef.attributeDefs);

			newObjectDefs.add(objectDef);
		}

		Collections.sort(newObjectDefs);

		return newObjectDefs;
	}

	private static void _setFieldValue(
		Field field, Object object, Object value) {

		try {
			field.set(object, value);
		}
		catch (IllegalAccessException illegalAccessException) {
			throw new RuntimeException(illegalAccessException);
		}
	}

	private static boolean _toBoolean(Object object) {
		return Objects.equals(
			String.valueOf(object), String.valueOf(Boolean.TRUE));
	}

	private static JSONArray _toJSONArray(Object... items) {
		return _jsonFactory.createJSONArray(items);
	}

	private static JSONObject _toJSONObject(Consumer<JSONObject> consumer) {
		JSONObject jsonObject = _jsonFactory.createJSONObject();

		consumer.accept(jsonObject);

		return jsonObject;
	}

	private static JSONObject _toJSONObject(String key, Object value) {
		return _toJSONObject(jsonObject -> jsonObject.put(key, value));
	}

	private static JSONObject _toJSONObject(
		String key, UnsafeSupplier<Object, Exception> valueUnsafeSupplier) {

		return _toJSONObject(
			jsonObject -> jsonObject.put(key, valueUnsafeSupplier));
	}

	private static Number _toNumber(Object object) {
		String s = String.valueOf(object);

		if (Validator.isBlank(s)) {
			return 0;
		}

		if (s.contains(".")) {
			return GetterUtil.getFloat(s);
		}

		return GetterUtil.getInteger(s);
	}

	private static void _withMatcher(String s, Object object, Pattern pattern) {
		_withMatcher(s, object, pattern, null);
	}

	private static <T> void _withMatcher(
		String s, T target, Pattern pattern, Consumer<T> consumer) {

		Matcher matcher = pattern.matcher(s);

		if (!matcher.find()) {
			return;
		}

		Class<?> clazz = target.getClass();

		for (Field field : clazz.getDeclaredFields()) {
			String value;

			try {
				value = matcher.group(field.getName());
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}

				continue;
			}

			Class<?> typeClass = field.getType();

			if (ArrayUtil.contains(
					new Class<?>[] {Object[].class, String[].class},
					typeClass)) {

				_setFieldValue(
					field, target,
					value.replaceAll(
						"[ \"]", ""
					).split(
						","
					));
			}
			else if (Objects.equals(typeClass, Number.class)) {
				_setFieldValue(field, target, _toNumber(value));
			}
			else if (Objects.equals(typeClass, boolean.class)) {
				_setFieldValue(field, target, _toBoolean(value));
			}
			else if (Objects.equals(field.getName(), "type")) {
				_setFieldValue(field, target, _schemaDataTypes.get(value));
			}
			else {
				_setFieldValue(field, target, value);
			}
		}

		if (consumer != null) {
			consumer.accept(target);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ConfigurationEnvBuilder.class);

	private static final Pattern _attributeDefMetaAnnotationPattern =
		Pattern.compile("@Meta.AD\\b");
	private static final Pattern _attributeDefaultValuePattern =
		Pattern.compile("\\bdeflt = \"(?<defaultValue>[^\"]*)\"");
	private static final Pattern _attributeDeprecatedPattern = Pattern.compile(
		"\\b(?<deprecated>@Deprecated)");
	private static final Pattern _attributeDescriptionPattern = Pattern.compile(
		"\\bdescription = \"(?<description>[^\"]*)\"");
	private static final Pattern _attributeMaxPattern = Pattern.compile(
		"\\bmax = \"(?<max>[^\"]+)\"");
	private static final Pattern _attributeMinPattern = Pattern.compile(
		"\\bmin = \"(?<min>[^\"]+)\"");
	private static final Pattern _attributeOptionLabelsPattern =
		Pattern.compile("\\boptionLabels = \\{(?<optionLabels>[^{}]*)}");
	private static final Pattern _attributeOptionValuesPattern =
		Pattern.compile("\\boptionValues = \\{(?<optionValues>[^{}]*)}");
	private static final Pattern _attributeRequiredInputPattern =
		Pattern.compile("\\brequiredInput = (?<requiredInput>true|false)");
	private static final Pattern _attributeRequiredPattern = Pattern.compile(
		"\\brequired = (?<required>true|false)");
	private static final Pattern _attributeTitlePattern = Pattern.compile(
		"\\bname = \"(?<title>[^\"]*)\"");
	private static final Pattern _attributeTypeNamePattern = Pattern.compile(
		"\\s+public(default)? (?<type>\\w+|\\S+) (?<name>\\w+)\\(\\)");
	private static final JSONFactory _jsonFactory = new JSONFactoryImpl();
	private static final Properties _languageProperties = new Properties();
	private static final Pattern _objectDefCategoryPattern = Pattern.compile(
		"\\bcategory = \"(?<category>[^\"]*)\"");
	private static final Pattern _objectDefDescriptionPattern = Pattern.compile(
		"\\bdescription = \"(?<description>[^\"]*)\"");
	private static final Pattern _objectDefExtendsInterfaceNamePattern =
		Pattern.compile(
			"\\bextends (?<extendsInterfaceName>[A-Z][A-Za-z\\d]+)\\b");
	private static final Pattern _objectDefInterfaceNamePattern =
		Pattern.compile(" @?interface (?<interfaceName>[A-Z][A-Za-z\\d]+)\\b");
	private static final Pattern _objectDefMetaAnnotationPattern =
		Pattern.compile("@Meta.OCD\\b");
	private static final Pattern _objectDefPidPattern = Pattern.compile(
		"\\bid = \"?(?<pid>[\\w.]+)\"?");
	private static final Pattern _objectDefScopePattern = Pattern.compile(
		"\\bscope = ExtendedObjectClassDefinition\\.Scope\\." +
			"(?<scope>SYSTEM|COMPANY|GROUP|PORTLET_INSTANCE)\\b");
	private static final Pattern _objectDefTitlePattern = Pattern.compile(
		"\\bname = \"(?<title>[^\"]*)\"");
	private static final Map<String, String> _schemaDataTypes =
		HashMapBuilder.put(
			"boolean", "boolean"
		).put(
			"float", "number"
		).put(
			"int", "number"
		).put(
			"LocalizedValuesMap", "object"
		).put(
			"long", "number"
		).put(
			"String", "string"
		).put(
			"String[]", "array"
		).build();

}