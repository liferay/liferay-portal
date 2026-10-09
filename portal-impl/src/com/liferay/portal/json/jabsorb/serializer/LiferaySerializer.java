/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.json.jabsorb.serializer;

import com.liferay.petra.lang.ClassLoaderPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.Serializable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.jabsorb.JSONSerializer;
import org.jabsorb.serializer.AbstractSerializer;
import org.jabsorb.serializer.MarshallException;
import org.jabsorb.serializer.ObjectMatch;
import org.jabsorb.serializer.SerializerState;
import org.jabsorb.serializer.UnmarshallException;

import org.json.JSONObject;

/**
 * @author Raymond Augé
 */
public class LiferaySerializer extends AbstractSerializer {

	@Override
	public boolean canSerialize(
		@SuppressWarnings("rawtypes") Class clazz,
		@SuppressWarnings("rawtypes") Class jsonClass) {

		Constructor<?> constructor = null;

		try {
			constructor = clazz.getConstructor();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		if (Serializable.class.isAssignableFrom(clazz) &&
			((jsonClass == null) || (jsonClass == JSONObject.class)) &&
			(constructor != null)) {

			return true;
		}

		return false;
	}

	@Override
	public Class<?>[] getJSONClasses() {
		return _JSON_CLASSES;
	}

	@Override
	public Class<?>[] getSerializableClasses() {
		return _SERIALIZABLE_CLASSES;
	}

	@Override
	public Object marshall(
			SerializerState serializerState, Object parentObject, Object object)
		throws MarshallException {

		JSONObject jsonObject = new JSONObject();

		Class<?> javaClass = object.getClass();

		if (ser.getMarshallClassHints()) {
			try {
				jsonObject.put("javaClass", javaClass.getName());

				String contextName = ClassLoaderPool.getContextName(
					javaClass.getClassLoader());

				if (Validator.isNotNull(contextName)) {
					jsonObject.put("contextName", contextName);
				}
			}
			catch (Exception exception) {
				throw new MarshallException(
					"Unable to put javaClass", exception);
			}
		}

		JSONObject serializableJSONObject = new JSONObject();

		try {
			jsonObject.put("serializable", serializableJSONObject);

			serializerState.push(
				object, serializableJSONObject, "serializable");
		}
		catch (Exception exception) {
			throw new MarshallException(
				"Unable to put serializable", exception);
		}

		String fieldName = null;

		try {
			Set<String> processedFieldNames = new HashSet<>();

			while (javaClass != null) {
				Field[] declaredFields = javaClass.getDeclaredFields();

				for (Field field : declaredFields) {
					fieldName = field.getName();

					// Avoid processing overridden fields of super classes

					if (processedFieldNames.contains(fieldName)) {
						continue;
					}

					processedFieldNames.add(fieldName);

					int modifiers = field.getModifiers();

					// Only marshall fields that are not static or transient

					if (((modifiers & Modifier.STATIC) == Modifier.STATIC) ||
						((modifiers & Modifier.TRANSIENT) ==
							Modifier.TRANSIENT)) {

						continue;
					}

					field.setAccessible(true);

					if (fieldName.startsWith("_")) {
						fieldName = fieldName.substring(1);
					}

					Object fieldObject = ser.marshall(
						serializerState, serializableJSONObject,
						field.get(object), fieldName);

					// Omit the object entirely if it is a circular reference or
					// duplicate. It will be regenerated in the fixups phase.

					if (JSONSerializer.CIRC_REF_OR_DUPLICATE != fieldObject) {
						serializableJSONObject.put(fieldName, fieldObject);
					}
					else if (!serializableJSONObject.has(fieldName)) {
						serializableJSONObject.put(
							fieldName, field.get(object));
					}
				}

				javaClass = javaClass.getSuperclass();
			}
		}
		catch (Exception exception) {
			throw new MarshallException(
				"Unable to match field " + fieldName, exception);
		}
		finally {
			serializerState.pop();
		}

		return jsonObject;
	}

	@Override
	public ObjectMatch tryUnmarshall(
			SerializerState serializerState,
			@SuppressWarnings("rawtypes") Class clazz, Object object)
		throws UnmarshallException {

		JSONObject jsonObject = (JSONObject)object;

		String javaClassName = null;

		try {
			javaClassName = jsonObject.getString("javaClass");
		}
		catch (Exception exception) {
			throw new UnmarshallException("Unable to get javaClass", exception);
		}

		if (javaClassName == null) {
			throw new UnmarshallException("javaClass is undefined");
		}

		try {
			boolean loadedClassFromContext = false;

			if (jsonObject.has("contextName")) {
				String contextName = jsonObject.getString("contextName");

				ClassLoader classLoader = ClassLoaderPool.getClassLoader(
					contextName);

				if (classLoader != null) {
					Class.forName(javaClassName, true, classLoader);

					loadedClassFromContext = true;
				}
				else if (_log.isWarnEnabled()) {
					_log.warn(
						StringBundler.concat(
							"Unable to get class loader for class ",
							javaClassName, " in context ", contextName));
				}
			}

			if (!loadedClassFromContext) {
				Class.forName(javaClassName);
			}
		}
		catch (Exception exception) {
			throw new UnmarshallException(
				"Unable to get class " + javaClassName, exception);
		}

		JSONObject serializableJSONObject = null;

		try {
			serializableJSONObject = jsonObject.getJSONObject("serializable");
		}
		catch (Exception exception) {
			throw new UnmarshallException(
				"Unable to get serializable", exception);
		}

		if (serializableJSONObject == null) {
			throw new UnmarshallException("serializable is undefined");
		}

		ObjectMatch objectMatch = new ObjectMatch(-1);

		serializerState.setSerialized(object, objectMatch);

		String fieldName = null;

		try {
			Iterator<?> iterator = serializableJSONObject.keys();

			while (iterator.hasNext()) {
				fieldName = (String)iterator.next();

				ObjectMatch fieldObjectMatch = ser.tryUnmarshall(
					serializerState, null,
					serializableJSONObject.get(fieldName));

				ObjectMatch maxFieldObjectMatch = fieldObjectMatch.max(
					objectMatch);

				objectMatch.setMismatch(maxFieldObjectMatch.getMismatch());
			}
		}
		catch (Exception exception) {
			throw new UnmarshallException(
				"Unable to match field " + fieldName, exception);
		}

		return objectMatch;
	}

	@Override
	public Object unmarshall(
			SerializerState serializerState,
			@SuppressWarnings("rawtypes") Class clazz, Object object)
		throws UnmarshallException {

		JSONObject jsonObject = (JSONObject)object;

		String javaClassName = null;

		try {
			javaClassName = jsonObject.getString("javaClass");
		}
		catch (Exception exception) {
			throw new UnmarshallException("Unable to get javaClass", exception);
		}

		if (javaClassName == null) {
			throw new UnmarshallException("javaClass is undefined");
		}

		Class<?> javaClass = null;

		Object javaClassInstance = null;

		try {
			if (jsonObject.has("contextName")) {
				String contextName = jsonObject.getString("contextName");

				ClassLoader classLoader = ClassLoaderPool.getClassLoader(
					contextName);

				if (classLoader != null) {
					try {
						javaClass = Class.forName(
							javaClassName, true, classLoader);
					}
					catch (ClassNotFoundException classNotFoundException) {
						if (_log.isWarnEnabled()) {
							_log.warn(
								StringBundler.concat(
									"Unable to load class ", javaClassName,
									" in context ", contextName),
								classNotFoundException);
						}
					}
				}
				else if (_log.isWarnEnabled()) {
					_log.warn(
						StringBundler.concat(
							"Unable to get class loader for class ",
							javaClassName, " in context ", contextName));
				}
			}

			if (javaClass == null) {
				javaClass = Class.forName(javaClassName);
			}

			javaClassInstance = javaClass.newInstance();
		}
		catch (Exception exception) {
			throw new UnmarshallException(
				"Unable to get class " + javaClassName, exception);
		}

		JSONObject serializableJSONObject = null;

		try {
			serializableJSONObject = jsonObject.getJSONObject("serializable");
		}
		catch (Exception exception) {
			throw new UnmarshallException(
				"Unable to get serializable", exception);
		}

		if (serializableJSONObject == null) {
			throw new UnmarshallException("serializable is undefined");
		}

		serializerState.setSerialized(object, javaClassInstance);

		String fieldName = null;

		try {
			Set<String> processedFieldNames = new HashSet<>();

			while (javaClass != null) {
				Field[] fields = javaClass.getDeclaredFields();

				for (Field field : fields) {
					fieldName = field.getName();

					// Avoid processing overridden fields of super classes

					if (processedFieldNames.contains(fieldName)) {
						continue;
					}

					processedFieldNames.add(fieldName);

					int modifiers = field.getModifiers();

					// Only unmarshall fields that are not static or transient

					if (((modifiers & Modifier.STATIC) == Modifier.STATIC) ||
						((modifiers & Modifier.TRANSIENT) ==
							Modifier.TRANSIENT)) {

						continue;
					}

					field.setAccessible(true);

					if (fieldName.startsWith("_")) {
						fieldName = fieldName.substring(1);
					}

					if (!serializableJSONObject.has(fieldName)) {
						continue;
					}

					Object value = null;

					try {
						value = ser.unmarshall(
							serializerState, field.getType(),
							_getSafe(serializableJSONObject, fieldName));
					}
					catch (Exception exception) {
						if (_log.isDebugEnabled()) {
							_log.debug(exception);
						}
					}

					if (value == null) {
						continue;
					}

					try {
						field.set(javaClassInstance, value);
					}
					catch (Exception exception) {
						_log.error(exception);
					}
				}

				javaClass = javaClass.getSuperclass();
			}
		}
		catch (Exception exception) {
			throw new UnmarshallException(
				"Unable to match field " + fieldName, exception);
		}

		return javaClassInstance;
	}

	private Object _getSafe(JSONObject jsonObject, String name) {
		Object object = jsonObject.get(name);

		if (object instanceof Integer) {
			Integer jsonInteger = (Integer)object;

			Integer cachedInteger = Integer.valueOf(jsonInteger.intValue());

			if (jsonInteger == cachedInteger) {
				return new Integer(jsonInteger.intValue());
			}
		}
		else if (object instanceof Long) {
			Long jsonLong = (Long)object;

			Long cachedLong = Long.valueOf(jsonLong.longValue());

			if (jsonLong == cachedLong) {
				return new Long(jsonLong.intValue());
			}
		}

		return object;
	}

	private static final Class<?>[] _JSON_CLASSES = {JSONObject.class};

	private static final Class<?>[] _SERIALIZABLE_CLASSES = {
		Serializable.class
	};

	private static final Log _log = LogFactoryUtil.getLog(
		LiferaySerializer.class);

}