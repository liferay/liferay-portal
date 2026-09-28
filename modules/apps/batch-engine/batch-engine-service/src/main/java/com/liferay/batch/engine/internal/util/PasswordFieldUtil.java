/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.internal.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.jackson.databind.ObjectMapperProviderUtil;

import io.swagger.v3.oas.annotations.media.Schema;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Adolfo Pérez
 */
public class PasswordFieldUtil {

	public static Set<String> getPasswordFieldNames(Class<?> clazz) {
		Set<String> passwordFieldNames = new HashSet<>();

		_populatePasswordFieldNames(
			clazz, new HashSet<>(), StringPool.BLANK, passwordFieldNames);

		return passwordFieldNames;
	}

	public static void removePasswordFields(
		JsonNode jsonNode, Set<String> passwordFieldNames) {

		if (jsonNode.isArray()) {
			for (JsonNode elementJsonNode : jsonNode) {
				removePasswordFields(elementJsonNode, passwordFieldNames);
			}

			return;
		}

		if (!jsonNode.isObject()) {
			return;
		}

		ObjectNode objectNode = (ObjectNode)jsonNode;

		for (String passwordFieldName : passwordFieldNames) {
			int index = passwordFieldName.indexOf(CharPool.PERIOD);

			if (index == -1) {
				objectNode.remove(passwordFieldName);

				continue;
			}

			JsonNode childJsonNode = objectNode.get(
				passwordFieldName.substring(0, index));

			if (childJsonNode != null) {
				removePasswordFields(
					childJsonNode,
					Set.of(passwordFieldName.substring(index + 1)));
			}
		}
	}

	public static String toString(Object object) {
		String string = object.toString();

		Set<String> passwordFieldNames = getPasswordFieldNames(
			object.getClass());

		if (passwordFieldNames.isEmpty()) {
			return string;
		}

		try {
			ObjectMapper objectMapper =
				ObjectMapperProviderUtil.getBatchEngineObjectMapper();

			JsonNode jsonNode = objectMapper.readTree(string);

			removePasswordFields(jsonNode, passwordFieldNames);

			return objectMapper.writeValueAsString(jsonNode);
		}
		catch (JsonProcessingException jsonProcessingException) {

			// The exception message may contain sensitive data.

			Class<?> clazz = object.getClass();

			_log.error(
				StringBundler.concat(
					"Unable to sanitize fields from object ", clazz.getName(),
					" at ", jsonProcessingException.getLocation()));

			return null;
		}
	}

	private static String _getFieldName(Field field) {
		JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);

		if ((jsonProperty != null) &&
			Validator.isNotNull(jsonProperty.value())) {

			return jsonProperty.value();
		}

		return field.getName();
	}

	private static boolean _isPasswordField(Class<?> clazz, Field field) {
		if (_isPasswordSchema(field.getAnnotation(Schema.class))) {
			return true;
		}

		try {
			Method method = clazz.getMethod(
				"get" + StringUtil.upperCaseFirstLetter(field.getName()));

			return _isPasswordSchema(method.getAnnotation(Schema.class));
		}
		catch (NoSuchMethodException noSuchMethodException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchMethodException);
			}

			return false;
		}
	}

	private static boolean _isPasswordSchema(Schema schema) {
		if ((schema != null) &&
			StringUtil.equals(schema.format(), "password")) {

			return true;
		}

		return false;
	}

	private static void _populatePasswordFieldNames(
		Class<?> clazz, Set<Class<?>> classes, String prefix,
		Set<String> passwordFieldNames) {

		if (!clazz.isAnnotationPresent(Schema.class) || !classes.add(clazz)) {
			return;
		}

		Class<?> currentClass = clazz;

		while (currentClass != null) {
			for (Field field : currentClass.getDeclaredFields()) {
				if (Modifier.isStatic(field.getModifiers()) ||
					field.isAnnotationPresent(JsonIgnore.class)) {

					continue;
				}

				String fieldName = prefix + _getFieldName(field);

				if (_isPasswordField(clazz, field)) {
					passwordFieldNames.add(fieldName);

					continue;
				}

				Class<?> fieldClass = field.getType();

				if (fieldClass.isArray()) {
					fieldClass = fieldClass.getComponentType();
				}

				_populatePasswordFieldNames(
					fieldClass, classes, fieldName + StringPool.PERIOD,
					passwordFieldNames);
			}

			currentClass = currentClass.getSuperclass();
		}

		classes.remove(clazz);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PasswordFieldUtil.class);

}