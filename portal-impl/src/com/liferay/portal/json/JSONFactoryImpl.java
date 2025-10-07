/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.json;

import com.liferay.portal.json.jabsorb.serializer.EnumSerializer;
import com.liferay.portal.json.jabsorb.serializer.LiferayJSONDeserializationWhitelist;
import com.liferay.portal.json.jabsorb.serializer.LiferayJSONSerializer;
import com.liferay.portal.json.jabsorb.serializer.LiferaySerializer;
import com.liferay.portal.json.jabsorb.serializer.LocaleSerializer;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONDeserializer;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONSerializer;
import com.liferay.portal.kernel.json.JSONTransformer;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ClassUtil;
import com.liferay.portal.kernel.util.Validator;

import java.lang.reflect.InvocationTargetException;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.jabsorb.serializer.MarshallException;

import org.json.JSONML;

/**
 * @author Brian Wing Shun Chan
 */
public class JSONFactoryImpl implements JSONFactory {

	public JSONFactoryImpl() {
		JSONInit.init();

		_jsonSerializer = new LiferayJSONSerializer(
			_liferayJSONDeserializationWhitelist);

		try {
			_jsonSerializer.registerDefaultSerializers();

			_jsonSerializer.registerSerializer(new EnumSerializer());
			_jsonSerializer.registerSerializer(new LiferaySerializer());
			_jsonSerializer.registerSerializer(new LocaleSerializer());
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	@Override
	public String convertJSONMLArrayToXML(String jsonml) {
		try {
			org.json.JSONArray jsonArray = new org.json.JSONArray(jsonml);

			return JSONML.toString(jsonArray);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}

			throw new IllegalStateException(
				"Unable to convert to XML", exception);
		}
	}

	@Override
	public String convertJSONMLObjectToXML(String jsonml) {
		try {
			org.json.JSONObject jsonObject = new org.json.JSONObject(jsonml);

			return JSONML.toString(jsonObject);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}

			throw new IllegalStateException(
				"Unable to convert to XML", exception);
		}
	}

	@Override
	public String convertXMLtoJSONMLArray(String xml) {
		try {
			org.json.JSONArray jsonArray = JSONML.toJSONArray(xml);

			return jsonArray.toString();
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}

			throw new IllegalStateException(
				"Unable to convert to JSONML", exception);
		}
	}

	@Override
	public String convertXMLtoJSONMLObject(String xml) {
		try {
			org.json.JSONObject jsonObject = JSONML.toJSONObject(xml);

			return jsonObject.toString();
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}

			throw new IllegalStateException(
				"Unable to convert to JSONML", exception);
		}
	}

	@Override
	public JSONTransformer createJavaScriptNormalizerJSONTransformer(
		List<String> javaScriptAttributes) {

		throw new UnsupportedOperationException();
	}

	@Override
	public JSONArray createJSONArray() {
		return new JSONArrayImpl();
	}

	@Override
	public JSONArray createJSONArray(Collection<?> collection) {
		return new JSONArrayImpl(collection);
	}

	@Override
	public JSONArray createJSONArray(String json) throws JSONException {
		return new JSONArrayImpl(json);
	}

	@Override
	public <T> JSONArray createJSONArray(T[] array) {
		return new JSONArrayImpl(Arrays.asList(array));
	}

	@Override
	public <T> JSONDeserializer<T> createJSONDeserializer() {
		return new JSONDeserializerImpl<>();
	}

	@Override
	public JSONObject createJSONObject() {
		return new JSONObjectImpl();
	}

	@Override
	public JSONObject createJSONObject(Map<?, ?> map) {
		return new JSONObjectImpl(map);
	}

	@Override
	public JSONObject createJSONObject(String json) throws JSONException {
		return new JSONObjectImpl(json);
	}

	@Override
	public JSONSerializer createJSONSerializer() {
		return new JSONSerializerImpl();
	}

	@Override
	public Object deserialize(JSONObject jsonObject) {
		return deserialize(jsonObject.toString());
	}

	@Override
	public Object deserialize(String json) {
		try {
			return _jsonSerializer.fromJSON(json);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}

			throw new IllegalStateException(
				"Unable to deserialize object", exception);
		}
	}

	public LiferayJSONDeserializationWhitelist
		getLiferayJSONDeserializationWhitelist() {

		return _liferayJSONDeserializationWhitelist;
	}

	@Override
	public String getNullJSON() {
		return _NULL_JSON;
	}

	@Override
	public JSONObject getUnmodifiableJSONObject() {
		return _unmodifiableJSONObject;
	}

	@Override
	public Object looseDeserialize(String json) {
		try {
			JSONDeserializer<?> jsonDeserializer = createJSONDeserializer();

			return jsonDeserializer.deserialize(json);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}

			throw new IllegalStateException(
				"Unable to deserialize object", exception);
		}
	}

	@Override
	public <T> T looseDeserialize(String json, Class<T> clazz) {
		JSONDeserializer<?> jsonDeserializer = createJSONDeserializer();

		jsonDeserializer.use(null, clazz);

		return (T)jsonDeserializer.deserialize(json);
	}

	@Override
	public String looseSerialize(Object object) {
		JSONSerializer jsonSerializer = createJSONSerializer();

		return jsonSerializer.serialize(object);
	}

	@Override
	public String looseSerialize(
		Object object, JSONTransformer jsonTransformer, Class<?> clazz) {

		JSONSerializer jsonSerializer = createJSONSerializer();

		jsonSerializer.transform(jsonTransformer, clazz);

		return jsonSerializer.serialize(object);
	}

	@Override
	public String looseSerialize(Object object, String... includes) {
		JSONSerializer jsonSerializer = createJSONSerializer();

		jsonSerializer.include(includes);

		return jsonSerializer.serialize(object);
	}

	@Override
	public String looseSerializeDeep(Object object) {
		JSONSerializer jsonSerializer = createJSONSerializer();

		return jsonSerializer.serializeDeep(object);
	}

	@Override
	public String looseSerializeDeep(
		Object object, JSONTransformer jsonTransformer, Class<?> clazz) {

		JSONSerializer jsonSerializer = createJSONSerializer();

		jsonSerializer.transform(jsonTransformer, clazz);

		return jsonSerializer.serializeDeep(object);
	}

	@Override
	public JSONObject safeCreateJSONObject(String json) {
		return safeCreateJSONObject(json, false);
	}

	@Override
	public JSONObject safeCreateJSONObject(String json, boolean strict) {
		if (strict && Validator.isNull(json)) {
			return null;
		}

		try {
			return createJSONObject(json);
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug(jsonException);
			}

			return null;
		}
	}

	@Override
	public String serialize(Object object) {
		try {
			return _jsonSerializer.toJSON(object);
		}
		catch (MarshallException marshallException) {
			if (_log.isWarnEnabled()) {
				_log.warn(marshallException);
			}

			throw new IllegalStateException(
				"Unable to serialize object", marshallException);
		}
	}

	@Override
	public String serializeThrowable(Throwable throwable) {
		JSONObject jsonObject = createJSONObject();

		if (throwable instanceof InvocationTargetException) {
			throwable = throwable.getCause();
		}

		String throwableMessage = throwable.getMessage();

		if (Validator.isNull(throwableMessage)) {
			throwableMessage = throwable.toString();
		}

		JSONObject errorJSONObject = createJSONObject();

		errorJSONObject.put(
			"message", throwableMessage
		).put(
			"type", ClassUtil.getClassName(throwable)
		);

		jsonObject.put(
			"error", errorJSONObject
		).put(
			"exception", throwableMessage
		).put(
			"throwable", throwable.toString()
		);

		if (throwable.getCause() == null) {
			return jsonObject.toString();
		}

		Throwable rootCauseThrowable = throwable;

		while (rootCauseThrowable.getCause() != null) {
			rootCauseThrowable = rootCauseThrowable.getCause();
		}

		JSONObject rootCauseJSONObject = createJSONObject();

		throwableMessage = rootCauseThrowable.getMessage();

		if (Validator.isNull(throwableMessage)) {
			throwableMessage = rootCauseThrowable.toString();
		}

		rootCauseJSONObject.put(
			"message", throwableMessage
		).put(
			"type", ClassUtil.getClassName(rootCauseThrowable)
		);

		jsonObject.put("rootCause", rootCauseJSONObject);

		return jsonObject.toString();
	}

	@Override
	public String toString(JSONObject jsonObject) {
		if (jsonObject == null) {
			return null;
		}

		return jsonObject.toString();
	}

	private static final String _NULL_JSON = "{}";

	private static final Log _log = LogFactoryUtil.getLog(
		JSONFactoryImpl.class);

	private final org.jabsorb.JSONSerializer _jsonSerializer;
	private final LiferayJSONDeserializationWhitelist
		_liferayJSONDeserializationWhitelist =
			new LiferayJSONDeserializationWhitelist();
	private final JSONObject _unmodifiableJSONObject =
		new UnmodifiableJSONObjectImpl();

}