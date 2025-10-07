/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.json;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 */
public class JSONFactoryUtil {

	public static String convertJSONMLArrayToXML(String jsonml) {
		return _jsonFactory.convertJSONMLArrayToXML(jsonml);
	}

	public static String convertJSONMLObjectToXML(String jsonml) {
		return _jsonFactory.convertJSONMLObjectToXML(jsonml);
	}

	public static String convertXMLtoJSONMLArray(String xml) {
		return _jsonFactory.convertXMLtoJSONMLArray(xml);
	}

	public static String convertXMLtoJSONMLObject(String xml) {
		return _jsonFactory.convertXMLtoJSONMLObject(xml);
	}

	public static JSONTransformer createJavaScriptNormalizerJSONTransformer(
		List<String> javaScriptAttributes) {

		return _jsonFactory.createJavaScriptNormalizerJSONTransformer(
			javaScriptAttributes);
	}

	public static JSONArray createJSONArray() {
		return _jsonFactory.createJSONArray();
	}

	public static JSONArray createJSONArray(Collection<?> collection) {
		return _jsonFactory.createJSONArray(collection);
	}

	public static JSONArray createJSONArray(String json) throws JSONException {
		return _jsonFactory.createJSONArray(json);
	}

	public static <T> JSONArray createJSONArray(T[] array) {
		return _jsonFactory.createJSONArray(array);
	}

	public static <T> JSONDeserializer<T> createJSONDeserializer() {
		return _jsonFactory.createJSONDeserializer();
	}

	public static JSONObject createJSONObject() {
		return _jsonFactory.createJSONObject();
	}

	public static JSONObject createJSONObject(Map<?, ?> map) {
		return _jsonFactory.createJSONObject(map);
	}

	public static JSONObject createJSONObject(String json)
		throws JSONException {

		return _jsonFactory.createJSONObject(json);
	}

	public static JSONSerializer createJSONSerializer() {
		return _jsonFactory.createJSONSerializer();
	}

	public static Object deserialize(JSONObject jsonObject) {
		return _jsonFactory.deserialize(jsonObject);
	}

	public static Object deserialize(String json) {
		return _jsonFactory.deserialize(json);
	}

	public static JSONFactory getJSONFactory() {
		return _jsonFactory;
	}

	public static String getNullJSON() {
		return _jsonFactory.getNullJSON();
	}

	public static JSONObject getUnmodifiableJSONObject() {
		return _jsonFactory.getUnmodifiableJSONObject();
	}

	public static Object looseDeserialize(String json) {
		return _jsonFactory.looseDeserialize(json);
	}

	public static <T> T looseDeserialize(String json, Class<T> clazz) {
		return _jsonFactory.looseDeserialize(json, clazz);
	}

	public static String looseSerialize(Object object) {
		return _jsonFactory.looseSerialize(object);
	}

	public static String looseSerialize(
		Object object, JSONTransformer jsonTransformer, Class<?> clazz) {

		return _jsonFactory.looseSerialize(object, jsonTransformer, clazz);
	}

	public static String looseSerialize(Object object, String... includes) {
		return _jsonFactory.looseSerialize(object, includes);
	}

	public static String looseSerializeDeep(Object object) {
		return _jsonFactory.looseSerializeDeep(object);
	}

	public static String looseSerializeDeep(
		Object object, JSONTransformer jsonTransformer, Class<?> clazz) {

		return _jsonFactory.looseSerializeDeep(object, jsonTransformer, clazz);
	}

	public static JSONObject safeCreateJSONObject(String json) {
		return _jsonFactory.safeCreateJSONObject(json);
	}

	public static JSONObject safeCreateJSONObject(String json, boolean strict) {
		return _jsonFactory.safeCreateJSONObject(json, strict);
	}

	public static String serialize(Object object) {
		return _jsonFactory.serialize(object);
	}

	public static String serializeThrowable(Throwable throwable) {
		return _jsonFactory.serializeThrowable(throwable);
	}

	public static String toString(JSONObject jsonObject) {
		return _jsonFactory.toString(jsonObject);
	}

	public void setJSONFactory(JSONFactory jsonFactory) {
		_jsonFactory = jsonFactory;
	}

	private static JSONFactory _jsonFactory;

}