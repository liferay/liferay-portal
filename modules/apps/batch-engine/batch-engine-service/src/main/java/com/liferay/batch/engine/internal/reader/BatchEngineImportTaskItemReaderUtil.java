/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.internal.reader;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;

import com.liferay.batch.engine.BatchEngineTaskContentType;
import com.liferay.batch.engine.action.ItemReaderPostAction;
import com.liferay.batch.engine.model.BatchEngineImportTask;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.jackson.databind.ObjectMapperProviderUtil;

import java.io.IOException;
import java.io.Serializable;

import java.lang.reflect.Field;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ivica Cardic
 */
public class BatchEngineImportTaskItemReaderUtil {

	public static <T> T convertValue(
			BatchEngineImportTask batchEngineImportTask, Class<T> itemClass,
			Map<String, Object> fieldNameValueMap,
			List<ItemReaderPostAction> itemReaderPostActions)
		throws ReflectiveOperationException {

		Map<String, Serializable> extendedProperties = new HashMap<>();

		JsonTypeInfo jsonTypeInfo = itemClass.getAnnotation(JsonTypeInfo.class);

		Class<? extends T> resolvedClass = itemClass;

		if (jsonTypeInfo != null) {
			String property = jsonTypeInfo.property();

			ObjectMapper objectMapper =
				ObjectMapperProviderUtil.getBatchEngineObjectMapper();

			T value = objectMapper.convertValue(
				HashMapBuilder.put(
					property, fieldNameValueMap.get(property)
				).build(),
				itemClass);

			resolvedClass = (Class<? extends T>)value.getClass();
		}

		T item = resolvedClass.getDeclaredConstructor(
		).newInstance();

		Set<String> batchRestrictFields = _getBatchRestrictFields(
			batchEngineImportTask);

		for (Map.Entry<String, Object> entry : fieldNameValueMap.entrySet()) {
			String name = entry.getKey();

			if (batchRestrictFields.contains(name)) {
				continue;
			}

			Field field = null;

			Field[] declaredFields = itemClass.getDeclaredFields();

			if (jsonTypeInfo != null) {
				declaredFields = ArrayUtil.append(
					declaredFields, resolvedClass.getDeclaredFields());
			}

			for (Field declaredField : declaredFields) {
				if (name.equals(declaredField.getName()) ||
					Objects.equals(
						StringPool.UNDERLINE + name, declaredField.getName())) {

					field = declaredField;

					break;
				}
			}

			if (field != null) {
				field.setAccessible(true);

				Object value = entry.getValue();

				ObjectMapper objectMapper = _getObjectMapper(
					batchEngineImportTask, field, value);

				field.set(
					item, objectMapper.convertValue(value, field.getType()));

				continue;
			}

			for (Field declaredField : declaredFields) {
				JsonAnySetter[] jsonAnySetters =
					declaredField.getAnnotationsByType(JsonAnySetter.class);

				if (jsonAnySetters.length > 0) {
					field = declaredField;

					break;
				}
			}

			if (field == null) {
				extendedProperties.put(
					entry.getKey(), (Serializable)entry.getValue());
			}
			else {
				field.setAccessible(true);

				Map<String, Object> map = (Map)field.get(item);

				map.put(entry.getKey(), entry.getValue());
			}
		}

		for (ItemReaderPostAction itemReaderPostAction :
				itemReaderPostActions) {

			itemReaderPostAction.run(
				batchEngineImportTask, extendedProperties, item);
		}

		return item;
	}

	public static Map<String, Object> mapFieldNames(
		Map<String, ? extends Serializable> fieldNameMappingMap,
		Map<String, Object> fieldNameValueMap) {

		if ((fieldNameMappingMap == null) || fieldNameMappingMap.isEmpty()) {
			return fieldNameValueMap;
		}

		Map<String, Object> targetFieldNameValueMap = new HashMap<>();

		for (Map.Entry<String, Object> entry : fieldNameValueMap.entrySet()) {
			String targetFieldName = (String)fieldNameMappingMap.get(
				entry.getKey());

			if (Validator.isNotNull(targetFieldName)) {
				Object object = targetFieldNameValueMap.get(targetFieldName);

				if ((object != null) && (object instanceof Map)) {
					Map<?, ?> map = (Map)object;

					map.putAll((Map)entry.getValue());
				}
				else {
					targetFieldNameValueMap.put(
						targetFieldName, entry.getValue());
				}

				continue;
			}

			String[] fieldNameParts = StringUtil.split(
				entry.getKey(), StringPool.PERIOD);

			targetFieldName = (String)fieldNameMappingMap.get(
				fieldNameParts[0]);

			if (Validator.isNull(targetFieldName)) {
				continue;
			}

			Matcher multiselectPicklistMatcher =
				_multiselectPicklistPattern.matcher(fieldNameParts[1]);

			if (multiselectPicklistMatcher.matches()) {
				if (fieldNameParts[1].startsWith("name_")) {
					continue;
				}

				List<Object> list =
					(List<Object>)targetFieldNameValueMap.computeIfAbsent(
						targetFieldName, key -> new ArrayList<>());

				list.add(entry.getValue());

				continue;
			}

			Map<String, Object> map =
				(Map<String, Object>)targetFieldNameValueMap.computeIfAbsent(
					targetFieldName, key -> new HashMap<>());

			for (int i = 1; i < fieldNameParts.length; i++) {
				if ((fieldNameParts.length - 1) > i) {
					map = (Map<String, Object>)map.computeIfAbsent(
						fieldNameParts[i], key -> new HashMap<>());

					continue;
				}

				map.put(fieldNameParts[i], entry.getValue());
			}
		}

		return targetFieldNameValueMap;
	}

	public abstract static class CreatorMixin {

		@JsonProperty(access = JsonProperty.Access.READ_WRITE)
		public String externalReferenceCode;

		@JsonProperty(access = JsonProperty.Access.READ_WRITE)
		public Long id;

	}

	private static Set<String> _getBatchRestrictFields(
		BatchEngineImportTask batchEngineImportTask) {

		Map<String, Serializable> parameters =
			batchEngineImportTask.getParameters();

		if (parameters == null) {
			return new HashSet<>();
		}

		String batchRestrictFields = MapUtil.getString(
			parameters, "batchRestrictFields");

		if (Validator.isBlank(batchRestrictFields)) {
			return new HashSet<>();
		}

		return SetUtil.fromArray(StringUtil.split(batchRestrictFields));
	}

	private static ObjectMapper _getObjectMapper(
			BatchEngineImportTask batchEngineImportTask, Field field,
			Object value)
		throws IllegalAccessException, InstantiationException {

		if (StringUtil.equals(
				batchEngineImportTask.getParameterValue(
					"importCreatorStrategy"),
				"KEEP_CREATOR") &&
			StringUtil.equals(field.getName(), "creator")) {

			return new ObjectMapper() {
				{
					addMixIn(field.getType(), CreatorMixin.class);
				}
			};
		}

		JsonDeserialize[] jsonDeserializes = field.getAnnotationsByType(
			JsonDeserialize.class);

		if (ArrayUtil.isEmpty(jsonDeserializes)) {
			if (Objects.equals(
					batchEngineImportTask.getContentType(),
					BatchEngineTaskContentType.CSV.getFileExtension()) &&
				_isCSVMapColumn(field.getType(), value)) {

				return _csvMapObjectMapper;
			}

			return _objectMapper;
		}

		JsonDeserialize jsonDeserialize = jsonDeserializes[0];

		return new ObjectMapper() {
			{
				SimpleModule simpleModule = new SimpleModule();

				simpleModule.addDeserializer(
					field.getType(),
					jsonDeserialize.using(
					).newInstance());

				registerModule(simpleModule);
			}
		};
	}

	private static boolean _isCSVMapColumn(Class<?> clazz, Object value) {
		if (!Map.class.isAssignableFrom(clazz)) {
			return false;
		}

		String string = value.toString();

		if ((string == null) || string.isEmpty()) {
			return false;
		}

		for (String line : string.split(StringPool.RETURN_NEW_LINE)) {
			if (!line.contains(StringPool.COLON)) {
				return false;
			}
		}

		return true;
	}

	private static final ObjectMapper _csvMapObjectMapper = new ObjectMapper() {
		{
			SimpleModule simpleModule = new SimpleModule();

			simpleModule.addDeserializer(
				Map.class, new MapStdCSVDeserializer());

			registerModule(simpleModule);
		}
	};

	private static final Pattern _multiselectPicklistPattern = Pattern.compile(
		"key_\\d+|name_\\d+");

	private static final ObjectMapper _objectMapper = new ObjectMapper() {
		{
			SimpleModule simpleModule = new SimpleModule();

			registerModule(simpleModule);
		}
	};

	private static class MapStdCSVDeserializer
		extends StdDeserializer<Map<String, Object>> {

		public MapStdCSVDeserializer() {
			this(Map.class);
		}

		@Override
		public Map<String, Object> deserialize(
				JsonParser jsonParser,
				DeserializationContext deserializationContext)
			throws IOException {

			Map<String, Object> map = new LinkedHashMap<>();

			String string = jsonParser.getValueAsString();

			for (String line : string.split(StringPool.RETURN_NEW_LINE)) {
				String[] lineParts = line.split(StringPool.COLON);

				map.put(lineParts[0], lineParts[1]);
			}

			return map;
		}

		protected MapStdCSVDeserializer(Class<?> clazz) {
			super(clazz);
		}

	}

}