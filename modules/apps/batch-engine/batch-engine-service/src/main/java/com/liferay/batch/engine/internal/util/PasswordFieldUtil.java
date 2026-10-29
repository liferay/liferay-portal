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

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.encryptor.EncryptorException;
import com.liferay.portal.kernel.encryptor.EncryptorUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.jackson.databind.ObjectMapperProviderUtil;

import io.swagger.v3.oas.annotations.media.Schema;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import java.security.Key;

import java.util.HashSet;
import java.util.Set;

import javax.crypto.spec.SecretKeySpec;

/**
 * @author Adolfo Pérez
 */
public class PasswordFieldUtil {

	public static String decrypt(String value) throws EncryptorException {
		if (value.isEmpty()) {
			return value;
		}

		return EncryptorUtil.decryptAuthenticated(_getKey(), value);
	}

	public static String encrypt(String value) throws EncryptorException {
		if (value.isEmpty()) {
			return value;
		}

		return EncryptorUtil.encryptAuthenticated(_getKey(), value);
	}

	public static Set<String> getPasswordFieldNames(Class<?> clazz) {
		Set<String> passwordFieldNames = new HashSet<>();

		_populatePasswordFieldNames(
			clazz, new HashSet<>(), StringPool.BLANK, passwordFieldNames);

		return passwordFieldNames;
	}

	public static <E extends Exception> void replacePasswordField(
			ObjectNode objectNode, String name,
			UnsafeFunction<String, String, E> unsafeFunction)
		throws E {

		JsonNode jsonNode = objectNode.get(name);

		if ((jsonNode == null) || jsonNode.isNull()) {
			return;
		}

		String value = unsafeFunction.apply(jsonNode.asText());

		if (value == null) {
			objectNode.remove(name);
		}
		else {
			objectNode.put(name, value);
		}
	}

	public static <E extends Exception> void replacePasswordFields(
			JsonNode jsonNode, Set<String> passwordFieldNames,
			UnsafeFunction<String, String, E> unsafeFunction)
		throws E {

		if (jsonNode.isArray()) {
			for (JsonNode elementJsonNode : jsonNode) {
				replacePasswordFields(
					elementJsonNode, passwordFieldNames, unsafeFunction);
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
				replacePasswordField(
					objectNode, passwordFieldName, unsafeFunction);

				continue;
			}

			JsonNode childJsonNode = objectNode.get(
				passwordFieldName.substring(0, index));

			if (childJsonNode == null) {
				continue;
			}

			replacePasswordFields(
				childJsonNode, Set.of(passwordFieldName.substring(index + 1)),
				unsafeFunction);
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

			replacePasswordFields(jsonNode, passwordFieldNames, value -> null);

			return objectMapper.writeValueAsString(jsonNode);
		}
		catch (JsonProcessingException jsonProcessingException) {
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

	private static Key _getKey() {
		return new SecretKeySpec(
			Base64.decode(PropsValues.BATCH_ENGINE_ENCRYPTION_KEY), "AES");
	}

	private static boolean _hasSchemaAnnotation(Class<?> clazz) {
		if (clazz.isAnnotationPresent(Schema.class)) {
			return true;
		}

		for (Method method : clazz.getMethods()) {
			if (method.isAnnotationPresent(Schema.class)) {
				return true;
			}
		}

		return false;
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

		if (!_hasSchemaAnnotation(clazz) || !classes.add(clazz)) {
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