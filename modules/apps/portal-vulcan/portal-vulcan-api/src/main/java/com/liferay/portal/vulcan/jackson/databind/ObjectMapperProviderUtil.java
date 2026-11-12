/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.jackson.databind;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonFactoryBuilder;
import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.vulcan.jaxrs.serializer.JSONArrayStdSerializer;
import com.liferay.portal.vulcan.jaxrs.serializer.JSONObjectStdSerializer;
import com.liferay.portal.vulcan.jaxrs.serializer.UnsafeSupplierJsonSerializer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.TimeZone;

/**
 * @author Alejandro Tardín
 */
public class ObjectMapperProviderUtil {

	public static ObjectMapper getBatchEngineObjectMapper() {
		if ((_batchEngineObjectMapper != null) &&
			(_batchEngineJSONStringMaxLength != null) &&
			_batchEngineJSONStringMaxLength.equals(
				PropsValues.JSON_STRING_MAX_LENGTH)) {

			return _batchEngineObjectMapper;
		}

		_batchEngineJSONStringMaxLength = PropsValues.JSON_STRING_MAX_LENGTH;

		_batchEngineObjectMapper = new ObjectMapper(
			_getJsonFactory(_batchEngineJSONStringMaxLength)) {

			{
				disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
				enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);
				registerModule(
					new SimpleModule() {
						{
							addSerializer(
								(Class<UnsafeSupplier<Object, Exception>>)
									(Class<?>)UnsafeSupplier.class,
								new UnsafeSupplierJsonSerializer());
						}
					});

				DateFormat dateFormat = new SimpleDateFormat(
					"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

				dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

				setDateFormat(dateFormat);

				setSerializationInclusion(JsonInclude.Include.NON_NULL);
			}
		};

		return _batchEngineObjectMapper;
	}

	public static ObjectMapper getObjectMapper() {
		if ((_objectMapper != null) && (_jsonStringMaxLength != null) &&
			_jsonStringMaxLength.equals(PropsValues.JSON_STRING_MAX_LENGTH)) {

			return _objectMapper;
		}

		_jsonStringMaxLength = PropsValues.JSON_STRING_MAX_LENGTH;

		_objectMapper = new ObjectMapper(
			_getJsonFactory(_jsonStringMaxLength)) {

			{
				configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
				enable(SerializationFeature.INDENT_OUTPUT);
				registerModule(
					new SimpleModule() {
						{
							addSerializer(
								JSONArray.class,
								new JSONArrayStdSerializer(JSONArray.class));
							addSerializer(
								JSONObject.class,
								new JSONObjectStdSerializer(JSONObject.class));
							addSerializer(
								(Class<UnsafeSupplier<Object, Exception>>)
									(Class<?>)UnsafeSupplier.class,
								new UnsafeSupplierJsonSerializer());
						}
					});
				setDateFormat(new ISO8601DateFormat());
				setFilterProvider(
					new SimpleFilterProvider() {
						{
							addFilter(
								"Liferay.Vulcan",
								SimpleBeanPropertyFilter.serializeAll());
						}
					});
				setSerializationInclusion(JsonInclude.Include.NON_ABSENT);
			}
		};

		return _objectMapper;
	}

	private static JsonFactory _getJsonFactory(int jsonStringMaxLength) {
		return new JsonFactoryBuilder(
		).streamReadConstraints(
			StreamReadConstraints.builder(
			).maxStringLength(
				jsonStringMaxLength
			).build()
		).build();
	}

	private static Integer _batchEngineJSONStringMaxLength;
	private static volatile ObjectMapper _batchEngineObjectMapper;
	private static Integer _jsonStringMaxLength;
	private static volatile ObjectMapper _objectMapper;

}