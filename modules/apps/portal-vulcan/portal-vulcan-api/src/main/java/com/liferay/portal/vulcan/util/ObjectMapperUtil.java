/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * @author Leonardo Barros
 */
public class ObjectMapperUtil {

	public static <T> T readValue(Class<?> clazz, Object object) {
		try {
			return readValue(clazz, _objectMapper.writeValueAsString(object));
		}
		catch (JsonProcessingException jsonProcessingException) {
			if (_log.isWarnEnabled()) {
				_log.warn(jsonProcessingException);
			}

			return null;
		}
	}

	public static <T> T readValue(Class<?> clazz, String json) {
		try {
			return (T)_objectMapper.readValue(json, clazz);
		}
		catch (JsonProcessingException jsonProcessingException) {
			if (_log.isWarnEnabled()) {
				_log.warn(jsonProcessingException);
			}

			return null;
		}
	}

	public static <T> T unsafeReadValue(Class<?> clazz, String json) {
		try {
			return (T)_objectMapper.readValue(json, clazz);
		}
		catch (JsonProcessingException jsonProcessingException) {
			throw new RuntimeException(jsonProcessingException);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectMapperUtil.class);

	private static final ObjectMapper _objectMapper;

	static {
		_objectMapper = new ObjectMapper() {
			{
				configure(
					DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
				configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
				configure(
					SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
				enable(SerializationFeature.INDENT_OUTPUT);
				setDateFormat(new ISO8601DateFormat());
				setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
				setSerializationInclusion(JsonInclude.Include.NON_NULL);
				setVisibility(
					PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
				setVisibility(
					PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
			}
		};
	}

}