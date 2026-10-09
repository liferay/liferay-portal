/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.json;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.lang.reflect.Array;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author Brian Wing Shun Chan
 * @author Rachael Koestartyo
 * @author Hugo Huijser
 */
public class JSONUtil {

	public static void addToStringCollection(
		Collection<String> collection, JSONArray jsonArray) {

		if (jsonArray == null) {
			return;
		}

		for (int i = 0; i < jsonArray.length(); i++) {
			collection.add(jsonArray.getString(i));
		}
	}

	public static void addToStringCollection(
		Collection<String> collection, JSONArray jsonArray,
		String jsonObjectKey) {

		if (jsonArray == null) {
			return;
		}

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			Object value = jsonObject.opt(jsonObjectKey);

			if (value == null) {
				continue;
			}

			collection.add((String)value);
		}
	}

	public static JSONArray concat(JSONArray... jsonArrays) {
		JSONArray newJSONArray = _createJSONArray();

		for (JSONArray jsonArray : jsonArrays) {
			for (int i = 0; i < jsonArray.length(); i++) {
				newJSONArray.put(jsonArray.get(i));
			}
		}

		return newJSONArray;
	}

	public static boolean equals(JSONArray jsonArray1, JSONArray jsonArray2) {
		return Objects.equals(jsonArray1.toString(), jsonArray2.toString());
	}

	public static boolean equals(
		JSONObject jsonObject1, JSONObject jsonObject2) {

		return Objects.equals(jsonObject1.toString(), jsonObject2.toString());
	}

	public static Object getValue(Object object, String... paths) {
		Object value = null;

		String[] parts = paths[0].split("/");

		String type = parts[0];
		String key = parts[1];

		if (type.equals("JSONArray")) {
			if (object instanceof JSONArray) {
				JSONArray jsonArray = (JSONArray)object;

				value = jsonArray.getJSONArray(GetterUtil.getInteger(key));
			}
			else if (object instanceof JSONObject) {
				JSONObject jsonObject = (JSONObject)object;

				value = jsonObject.getJSONArray(key);
			}
		}
		else if (type.equals("JSONObject")) {
			if (object instanceof JSONArray) {
				JSONArray jsonArray = (JSONArray)object;

				value = jsonArray.getJSONObject(GetterUtil.getInteger(key));
			}
			else if (object instanceof JSONObject) {
				JSONObject jsonObject = (JSONObject)object;

				value = jsonObject.getJSONObject(key);
			}
		}
		else if (type.equals("Object")) {
			if (object instanceof JSONArray) {
				JSONArray jsonArray = (JSONArray)object;

				value = jsonArray.get(GetterUtil.getInteger(key));
			}
			else if (object instanceof JSONObject) {
				JSONObject jsonObject = (JSONObject)object;

				value = jsonObject.get(key);
			}
		}

		if (paths.length == 1) {
			return value;
		}

		return getValue(value, Arrays.copyOfRange(paths, 1, paths.length));
	}

	public static boolean getValueAsBoolean(Object object, String... paths) {
		return GetterUtil.getBoolean(getValue(object, paths));
	}

	public static double getValueAsDouble(Object object, String... paths) {
		return GetterUtil.getDouble(getValue(object, paths));
	}

	public static int getValueAsInt(Object object, String... paths) {
		return GetterUtil.getInteger(getValue(object, paths));
	}

	public static JSONArray getValueAsJSONArray(
		Object object, String... paths) {

		return (JSONArray)getValue(object, paths);
	}

	public static JSONObject getValueAsJSONObject(
		Object object, String... paths) {

		return (JSONObject)getValue(object, paths);
	}

	public static long getValueAsLong(Object object, String... paths) {
		return GetterUtil.getLong(getValue(object, paths));
	}

	public static String getValueAsString(Object object, String... paths) {
		return String.valueOf(getValue(object, paths));
	}

	public static boolean hasValue(JSONArray jsonArray, Object value) {
		for (int i = 0; i < jsonArray.length(); i++) {
			if (Objects.equals(value, jsonArray.get(i))) {
				return true;
			}
		}

		return false;
	}

	public static boolean isEmpty(JSONArray jsonArray) {
		if ((jsonArray == null) || (jsonArray.length() == 0)) {
			return true;
		}

		return false;
	}

	public static boolean isEmpty(JSONObject jsonObject) {
		if ((jsonObject == null) || (jsonObject.length() == 0)) {
			return true;
		}

		return false;
	}

	public static boolean isJSONArray(String json) {
		try {
			_createJSONArray(json);

			return true;
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug(jsonException);
			}

			return false;
		}
	}

	public static boolean isJSONObject(String json) {
		if (json == null) {
			return false;
		}

		json = json.trim();

		if ((json.length() < 2) ||
			(json.charAt(0) != CharPool.OPEN_CURLY_BRACE) ||
			(json.charAt(json.length() - 1) != CharPool.CLOSE_CURLY_BRACE)) {

			return false;
		}

		try {
			_createJSONObject(json);

			return true;
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug(jsonException);
			}

			return false;
		}
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             #isJSONObject(String)}
	 */
	@Deprecated
	public static boolean isValid(String json) {
		return isJSONObject(json);
	}

	public static JSONObject merge(
			JSONObject jsonObject1, JSONObject jsonObject2)
		throws JSONException {

		if (jsonObject1 == null) {
			return _createJSONObject(jsonObject2.toString());
		}

		if (jsonObject2 == null) {
			return _createJSONObject(jsonObject1.toString());
		}

		JSONObject jsonObject3 = _createJSONObject(jsonObject1.toString());

		Iterator<String> iterator = jsonObject2.keys();

		while (iterator.hasNext()) {
			String key = iterator.next();

			jsonObject3.put(key, jsonObject2.get(key));
		}

		return jsonObject3;
	}

	public static JSONArray put(Object value) {
		JSONArray jsonArray = _createJSONArray();

		jsonArray.put(value);

		return jsonArray;
	}

	public static JSONObject put(String key, Object value) {
		JSONObject jsonObject = _createJSONObject();

		return jsonObject.put(key, value);
	}

	public static JSONObject put(
		String key, UnsafeSupplier<Object, Exception> valueUnsafeSupplier) {

		JSONObject jsonObject = _createJSONObject();

		try {
			Object value = valueUnsafeSupplier.get();

			if (value != null) {
				jsonObject.put(key, value);
			}
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}

		return jsonObject;
	}

	public static JSONArray put(
		UnsafeSupplier<Object, Exception> valueUnsafeSupplier) {

		JSONArray jsonArray = _createJSONArray();

		try {
			Object value = valueUnsafeSupplier.get();

			if (value != null) {
				jsonArray.put(value);
			}
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}

		return jsonArray;
	}

	public static JSONArray putAll(Object... values) {
		JSONArray jsonArray = _createJSONArray();

		for (Object value : values) {
			jsonArray.put(value);
		}

		return jsonArray;
	}

	public static JSONArray replace(
		JSONArray jsonArray, String jsonObjectKey, JSONObject newJSONObject) {

		if (jsonArray == null) {
			return null;
		}

		JSONArray newJSONArray = _createJSONArray();

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			if (Objects.equals(
					jsonObject.getString(jsonObjectKey),
					newJSONObject.getString(jsonObjectKey))) {

				newJSONArray.put(newJSONObject);
			}
			else {
				newJSONArray.put(jsonObject);
			}
		}

		return newJSONArray;
	}

	public static <T> T[] toArray(
			JSONArray jsonArray,
			UnsafeFunction<JSONObject, T, Exception> unsafeFunction,
			Class<?> clazz)
		throws Exception {

		List<T> list = toList(jsonArray, unsafeFunction);

		return list.toArray((T[])Array.newInstance(clazz, 0));
	}

	public static <T> T[] toArray(
		JSONArray jsonArray,
		UnsafeFunction<JSONObject, T, Exception> unsafeFunction,
		Consumer<Exception> exceptionConsumer, Class<?> clazz) {

		List<T> list = toList(jsonArray, unsafeFunction, exceptionConsumer);

		return list.toArray((T[])Array.newInstance(clazz, 0));
	}

	public static <T> T[] toArray(
		JSONArray jsonArray,
		UnsafeFunction<JSONObject, T, Exception> unsafeFunction, Log log,
		Class<?> clazz) {

		List<T> list = toList(jsonArray, unsafeFunction, log);

		return list.toArray((T[])Array.newInstance(clazz, 0));
	}

	public static double[] toDoubleArray(JSONArray jsonArray) {
		if (jsonArray == null) {
			return new double[0];
		}

		double[] values = new double[jsonArray.length()];

		for (int i = 0; i < jsonArray.length(); i++) {
			values[i] = jsonArray.getDouble(i);
		}

		return values;
	}

	public static double[] toDoubleArray(
		JSONArray jsonArray, String jsonObjectKey) {

		if (jsonArray == null) {
			return new double[0];
		}

		List<Double> values = toDoubleList(jsonArray, jsonObjectKey);

		return ArrayUtil.toArray(values.toArray(new Double[0]));
	}

	public static List<Double> toDoubleList(JSONArray jsonArray) {
		if (jsonArray == null) {
			return new ArrayList<>();
		}

		List<Double> values = new ArrayList<>(jsonArray.length());

		for (int i = 0; i < jsonArray.length(); i++) {
			values.add(jsonArray.getDouble(i));
		}

		return values;
	}

	public static List<Double> toDoubleList(
		JSONArray jsonArray, String jsonObjectKey) {

		if (jsonArray == null) {
			return new ArrayList<>();
		}

		List<Double> values = new ArrayList<>(jsonArray.length());

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			Object value = jsonObject.opt(jsonObjectKey);

			if (value == null) {
				continue;
			}

			values.add(jsonObject.getDouble(jsonObjectKey));
		}

		return values;
	}

	public static Set<Double> toDoubleSet(JSONArray jsonArray) {
		if (jsonArray == null) {
			return new HashSet<>();
		}

		Set<Double> values = new HashSet<>(jsonArray.length());

		for (int i = 0; i < jsonArray.length(); i++) {
			values.add(jsonArray.getDouble(i));
		}

		return values;
	}

	public static Set<Double> toDoubleSet(
		JSONArray jsonArray, String jsonObjectKey) {

		if (jsonArray == null) {
			return new HashSet<>();
		}

		Set<Double> values = new HashSet<>(jsonArray.length());

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			Object value = jsonObject.opt(jsonObjectKey);

			if (value == null) {
				continue;
			}

			values.add(jsonObject.getDouble(jsonObjectKey));
		}

		return values;
	}

	public static float[] toFloatArray(JSONArray jsonArray) {
		if (jsonArray == null) {
			return new float[0];
		}

		float[] values = new float[jsonArray.length()];

		for (int i = 0; i < jsonArray.length(); i++) {
			values[i] = GetterUtil.getFloat(jsonArray.get(i));
		}

		return values;
	}

	public static float[] toFloatArray(
		JSONArray jsonArray, String jsonObjectKey) {

		if (jsonArray == null) {
			return new float[0];
		}

		List<Float> values = toFloatList(jsonArray, jsonObjectKey);

		return ArrayUtil.toArray(values.toArray(new Float[0]));
	}

	public static List<Float> toFloatList(JSONArray jsonArray) {
		if (jsonArray == null) {
			return new ArrayList<>();
		}

		List<Float> values = new ArrayList<>(jsonArray.length());

		for (int i = 0; i < jsonArray.length(); i++) {
			values.add(GetterUtil.getFloat(jsonArray.get(i)));
		}

		return values;
	}

	public static List<Float> toFloatList(
		JSONArray jsonArray, String jsonObjectKey) {

		if (jsonArray == null) {
			return new ArrayList<>();
		}

		List<Float> values = new ArrayList<>(jsonArray.length());

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			Object value = jsonObject.opt(jsonObjectKey);

			if (value == null) {
				continue;
			}

			values.add(GetterUtil.getFloat(jsonObject.get(jsonObjectKey)));
		}

		return values;
	}

	public static Set<Float> toFloatSet(JSONArray jsonArray) {
		if (jsonArray == null) {
			return new HashSet<>();
		}

		Set<Float> values = new HashSet<>(jsonArray.length());

		for (int i = 0; i < jsonArray.length(); i++) {
			values.add(GetterUtil.getFloat(jsonArray.get(i)));
		}

		return values;
	}

	public static Set<Float> toFloatSet(
		JSONArray jsonArray, String jsonObjectKey) {

		if (jsonArray == null) {
			return new HashSet<>();
		}

		Set<Float> values = new HashSet<>(jsonArray.length());

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			Object value = jsonObject.opt(jsonObjectKey);

			if (value == null) {
				continue;
			}

			values.add(GetterUtil.getFloat(jsonObject.get(jsonObjectKey)));
		}

		return values;
	}

	public static int[] toIntegerArray(JSONArray jsonArray) {
		if (jsonArray == null) {
			return new int[0];
		}

		int[] values = new int[jsonArray.length()];

		for (int i = 0; i < jsonArray.length(); i++) {
			values[i] = jsonArray.getInt(i);
		}

		return values;
	}

	public static int[] toIntegerArray(
		JSONArray jsonArray, String jsonObjectKey) {

		if (jsonArray == null) {
			return new int[0];
		}

		List<Integer> values = toIntegerList(jsonArray, jsonObjectKey);

		return ArrayUtil.toArray(values.toArray(new Integer[0]));
	}

	public static List<Integer> toIntegerList(JSONArray jsonArray) {
		if (jsonArray == null) {
			return new ArrayList<>();
		}

		List<Integer> values = new ArrayList<>(jsonArray.length());

		for (int i = 0; i < jsonArray.length(); i++) {
			values.add(jsonArray.getInt(i));
		}

		return values;
	}

	public static List<Integer> toIntegerList(
		JSONArray jsonArray, String jsonObjectKey) {

		if (jsonArray == null) {
			return new ArrayList<>();
		}

		List<Integer> values = new ArrayList<>(jsonArray.length());

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			Object value = jsonObject.opt(jsonObjectKey);

			if (value == null) {
				continue;
			}

			values.add(jsonObject.getInt(jsonObjectKey));
		}

		return values;
	}

	public static Set<Integer> toIntegerSet(JSONArray jsonArray) {
		if (jsonArray == null) {
			return new HashSet<>();
		}

		Set<Integer> values = new HashSet<>(jsonArray.length());

		for (int i = 0; i < jsonArray.length(); i++) {
			values.add(jsonArray.getInt(i));
		}

		return values;
	}

	public static Set<Integer> toIntegerSet(
		JSONArray jsonArray, String jsonObjectKey) {

		if (jsonArray == null) {
			return new HashSet<>();
		}

		Set<Integer> values = new HashSet<>(jsonArray.length());

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			Object value = jsonObject.opt(jsonObjectKey);

			if (value == null) {
				continue;
			}

			values.add(jsonObject.getInt(jsonObjectKey));
		}

		return values;
	}

	public static <T> JSONArray toJSONArray(
			Collection<T> collection,
			UnsafeFunction<T, Object, Exception> unsafeFunction)
		throws Exception {

		JSONArray jsonArray = _createJSONArray();

		if (collection == null) {
			return jsonArray;
		}

		Iterator<T> iterator = collection.iterator();

		while (iterator.hasNext()) {
			Object item = unsafeFunction.apply(iterator.next());

			if (item == null) {
				continue;
			}

			jsonArray.put(item);
		}

		return jsonArray;
	}

	public static <T> JSONArray toJSONArray(
		Collection<T> collection,
		UnsafeFunction<T, Object, Exception> unsafeFunction,
		Consumer<Exception> exceptionConsumer) {

		JSONArray jsonArray = _createJSONArray();

		if (collection == null) {
			return jsonArray;
		}

		Iterator<T> iterator = collection.iterator();

		while (iterator.hasNext()) {
			try {
				Object item = unsafeFunction.apply(iterator.next());

				if (item != null) {
					jsonArray.put(item);
				}
			}
			catch (Exception exception) {
				exceptionConsumer.accept(exception);
			}
		}

		return jsonArray;
	}

	public static <T> JSONArray toJSONArray(
		Collection<T> collection,
		UnsafeFunction<T, Object, Exception> unsafeFunction, Log log) {

		return toJSONArray(
			collection, unsafeFunction,
			exception -> {
				if (log.isWarnEnabled()) {
					log.warn(exception, exception);
				}
			});
	}

	public static <T> JSONArray toJSONArray(
			T[] array, UnsafeFunction<T, Object, Exception> unsafeFunction)
		throws Exception {

		JSONArray jsonArray = _createJSONArray();

		if (array == null) {
			return jsonArray;
		}

		for (T t : array) {
			Object item = unsafeFunction.apply(t);

			if (item == null) {
				continue;
			}

			jsonArray.put(item);
		}

		return jsonArray;
	}

	public static <T> JSONArray toJSONArray(
		T[] array, UnsafeFunction<T, Object, Exception> unsafeFunction,
		Consumer<Exception> exceptionConsumer) {

		JSONArray jsonArray = _createJSONArray();

		if (array == null) {
			return jsonArray;
		}

		for (T t : array) {
			try {
				Object item = unsafeFunction.apply(t);

				if (item != null) {
					jsonArray.put(item);
				}
			}
			catch (Exception exception) {
				exceptionConsumer.accept(exception);
			}
		}

		return jsonArray;
	}

	public static <T> JSONArray toJSONArray(
		T[] array, UnsafeFunction<T, Object, Exception> unsafeFunction,
		Log log) {

		return toJSONArray(
			array, unsafeFunction,
			exception -> {
				if (log.isWarnEnabled()) {
					log.warn(exception, exception);
				}
			});
	}

	public static Map<String, JSONObject> toJSONObjectMap(
		JSONArray jsonArray, String jsonObjectKey) {

		Map<String, JSONObject> values = new HashMap<>();

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			values.put(jsonObject.getString(jsonObjectKey), jsonObject);
		}

		return values;
	}

	public static <T> List<T> toList(
			JSONArray jsonArray,
			UnsafeFunction<JSONObject, T, Exception> unsafeFunction)
		throws Exception {

		if (jsonArray == null) {
			return new ArrayList<>();
		}

		List<T> values = new ArrayList<>(jsonArray.length());

		for (int i = 0; i < jsonArray.length(); i++) {
			T item = unsafeFunction.apply(jsonArray.getJSONObject(i));

			if (item == null) {
				continue;
			}

			values.add(item);
		}

		return values;
	}

	public static <T> List<T> toList(
		JSONArray jsonArray,
		UnsafeFunction<JSONObject, T, Exception> unsafeFunction,
		Consumer<Exception> exceptionConsumer) {

		if (jsonArray == null) {
			return new ArrayList<>();
		}

		List<T> values = new ArrayList<>(jsonArray.length());

		for (int i = 0; i < jsonArray.length(); i++) {
			try {
				T item = unsafeFunction.apply(jsonArray.getJSONObject(i));

				if (item != null) {
					values.add(item);
				}
			}
			catch (Exception exception) {
				exceptionConsumer.accept(exception);
			}
		}

		return values;
	}

	public static <T> List<T> toList(
		JSONArray jsonArray,
		UnsafeFunction<JSONObject, T, Exception> unsafeFunction, Log log) {

		return toList(
			jsonArray, unsafeFunction,
			exception -> {
				if (log.isWarnEnabled()) {
					log.warn(exception, exception);
				}
			});
	}

	public static long[] toLongArray(JSONArray jsonArray) {
		if (jsonArray == null) {
			return new long[0];
		}

		long[] values = new long[jsonArray.length()];

		for (int i = 0; i < jsonArray.length(); i++) {
			values[i] = jsonArray.getLong(i);
		}

		return values;
	}

	public static long[] toLongArray(
		JSONArray jsonArray, String jsonObjectKey) {

		if (jsonArray == null) {
			return new long[0];
		}

		List<Long> values = toLongList(jsonArray, jsonObjectKey);

		return ArrayUtil.toArray(values.toArray(new Long[0]));
	}

	public static List<Long> toLongList(JSONArray jsonArray) {
		if (jsonArray == null) {
			return new ArrayList<>();
		}

		List<Long> values = new ArrayList<>(jsonArray.length());

		for (int i = 0; i < jsonArray.length(); i++) {
			values.add(jsonArray.getLong(i));
		}

		return values;
	}

	public static List<Long> toLongList(
		JSONArray jsonArray, String jsonObjectKey) {

		if (jsonArray == null) {
			return new ArrayList<>();
		}

		List<Long> values = new ArrayList<>(jsonArray.length());

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			Object value = jsonObject.opt(jsonObjectKey);

			if (value == null) {
				continue;
			}

			values.add(jsonObject.getLong(jsonObjectKey));
		}

		return values;
	}

	public static Set<Long> toLongSet(JSONArray jsonArray) {
		if (jsonArray == null) {
			return new HashSet<>();
		}

		Set<Long> values = new HashSet<>(jsonArray.length());

		for (int i = 0; i < jsonArray.length(); i++) {
			values.add(jsonArray.getLong(i));
		}

		return values;
	}

	public static Set<Long> toLongSet(
		JSONArray jsonArray, String jsonObjectKey) {

		if (jsonArray == null) {
			return new HashSet<>();
		}

		Set<Long> values = new HashSet<>(jsonArray.length());

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			Object value = jsonObject.opt(jsonObjectKey);

			if (value == null) {
				continue;
			}

			values.add(jsonObject.getLong(jsonObjectKey));
		}

		return values;
	}

	public static Object[] toObjectArray(JSONArray jsonArray) {
		if (jsonArray == null) {
			return new Object[0];
		}

		Object[] values = new Object[jsonArray.length()];

		for (int i = 0; i < jsonArray.length(); i++) {
			values[i] = jsonArray.get(i);
		}

		return values;
	}

	public static Object[] toObjectArray(
		JSONArray jsonArray, String jsonObjectKey) {

		if (jsonArray == null) {
			return new Object[0];
		}

		List<Object> values = toObjectList(jsonArray, jsonObjectKey);

		return values.toArray(new Object[0]);
	}

	public static List<Object> toObjectList(JSONArray jsonArray) {
		if (jsonArray == null) {
			return new ArrayList<>();
		}

		List<Object> values = new ArrayList<>(jsonArray.length());

		for (int i = 0; i < jsonArray.length(); i++) {
			values.add(jsonArray.get(i));
		}

		return values;
	}

	public static List<Object> toObjectList(
		JSONArray jsonArray, String jsonObjectKey) {

		if (jsonArray == null) {
			return new ArrayList<>();
		}

		List<Object> values = new ArrayList<>(jsonArray.length());

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			Object value = jsonObject.opt(jsonObjectKey);

			if (value == null) {
				continue;
			}

			values.add(value);
		}

		return values;
	}

	public static Set<Object> toObjectSet(JSONArray jsonArray) {
		if (jsonArray == null) {
			return new HashSet<>();
		}

		Set<Object> values = new HashSet<>(jsonArray.length());

		for (int i = 0; i < jsonArray.length(); i++) {
			values.add(jsonArray.get(i));
		}

		return values;
	}

	public static Set<Object> toObjectSet(
		JSONArray jsonArray, String jsonObjectKey) {

		if (jsonArray == null) {
			return new HashSet<>();
		}

		Set<Object> values = new HashSet<>(jsonArray.length());

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			Object value = jsonObject.opt(jsonObjectKey);

			if (value == null) {
				continue;
			}

			values.add(value);
		}

		return values;
	}

	public static String toString(JSONArray jsonArray) {
		return _toString(jsonArray, StringPool.TAB, 0);
	}

	public static String toString(JSONObject jsonObject) {
		return _toString(jsonObject, StringPool.TAB, 0);
	}

	public static String[] toStringArray(JSONArray jsonArray) {
		if (jsonArray == null) {
			return new String[0];
		}

		String[] values = new String[jsonArray.length()];

		for (int i = 0; i < jsonArray.length(); i++) {
			values[i] = jsonArray.getString(i);
		}

		return values;
	}

	public static String[] toStringArray(
		JSONArray jsonArray, String jsonObjectKey) {

		if (jsonArray == null) {
			return new String[0];
		}

		List<String> values = toStringList(jsonArray, jsonObjectKey);

		return values.toArray(new String[0]);
	}

	public static List<String> toStringList(JSONArray jsonArray) {
		if (jsonArray == null) {
			return new ArrayList<>();
		}

		List<String> values = new ArrayList<>(jsonArray.length());

		for (int i = 0; i < jsonArray.length(); i++) {
			values.add(jsonArray.getString(i));
		}

		return values;
	}

	public static List<String> toStringList(
		JSONArray jsonArray, String jsonObjectKey) {

		if (jsonArray == null) {
			return new ArrayList<>();
		}

		List<String> values = new ArrayList<>(jsonArray.length());

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			Object value = jsonObject.opt(jsonObjectKey);

			if (value == null) {
				continue;
			}

			values.add((String)value);
		}

		return values;
	}

	public static Map<String, String> toStringMap(JSONObject jsonObject) {
		Map<String, String> values = new HashMap<>();

		for (String key : jsonObject.keySet()) {
			values.put(key, jsonObject.getString(key));
		}

		return values;
	}

	public static Set<String> toStringSet(JSONArray jsonArray) {
		if (jsonArray == null) {
			return new HashSet<>();
		}

		Set<String> values = new HashSet<>(jsonArray.length());

		for (int i = 0; i < jsonArray.length(); i++) {
			values.add(jsonArray.getString(i));
		}

		return values;
	}

	public static Set<String> toStringSet(
		JSONArray jsonArray, String jsonObjectKey) {

		if (jsonArray == null) {
			return new HashSet<>();
		}

		Set<String> values = new HashSet<>(jsonArray.length());

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			Object value = jsonObject.opt(jsonObjectKey);

			if (value == null) {
				continue;
			}

			values.add((String)value);
		}

		return values;
	}

	private static JSONArray _createJSONArray() {
		return JSONFactoryUtil.createJSONArray();
	}

	private static JSONArray _createJSONArray(String json)
		throws JSONException {

		return JSONFactoryUtil.createJSONArray(json);
	}

	private static JSONObject _createJSONObject() {
		return JSONFactoryUtil.createJSONObject();
	}

	private static JSONObject _createJSONObject(String json)
		throws JSONException {

		return JSONFactoryUtil.createJSONObject(json);
	}

	private static String _getIndent(String indent, int level) {
		StringBundler sb = new StringBundler(level);

		for (int i = 0; i < level; i++) {
			sb.append(indent);
		}

		return sb.toString();
	}

	private static String _getString(Object value, String indent, int level) {
		if (value instanceof JSONArray) {
			return _toString((JSONArray)value, indent, level);
		}

		if (value instanceof JSONObject) {
			return _toString((JSONObject)value, indent, level);
		}

		if (value instanceof String) {
			return StringBundler.concat(
				StringPool.QUOTE,
				StringUtil.replace(
					(String)value, new String[] {"\\", "\"", "\n", "\r", "\t"},
					new String[] {"\\\\", "\\\"", "\\n", "\\r", "\\t"}),
				StringPool.QUOTE);
		}

		return value.toString();
	}

	private static String _toString(
		JSONArray jsonArray, String indent, int level) {

		if (jsonArray.length() == 0) {
			return StringBundler.concat(
				"[\n", _getIndent(indent, level), StringPool.CLOSE_BRACKET);
		}

		StringBundler sb = new StringBundler();

		sb.append(StringPool.OPEN_BRACKET);
		sb.append("\n");

		for (Object value : toObjectList(jsonArray)) {
			sb.append(_getIndent(indent, level + 1));
			sb.append(_getString(value, indent, level + 1));
			sb.append(StringPool.COMMA);
			sb.append("\n");
		}

		sb.setIndex(sb.index() - 2);

		sb.append("\n");

		sb.append(_getIndent(indent, level));

		sb.append(StringPool.CLOSE_BRACKET);

		return sb.toString();
	}

	private static String _toString(
		JSONObject jsonObject, String indent, int level) {

		if (jsonObject.length() == 0) {
			return StringBundler.concat(
				"{\n", _getIndent(indent, level), StringPool.CLOSE_CURLY_BRACE);
		}

		StringBundler sb = new StringBundler();

		sb.append(StringPool.OPEN_CURLY_BRACE);
		sb.append("\n");

		List<String> keys = new ArrayList<>(jsonObject.keySet());

		Collections.sort(keys);

		for (String key : keys) {
			sb.append(_getIndent(indent, level + 1));
			sb.append(_getString(key, indent, level + 1));
			sb.append(": ");
			sb.append(_getString(jsonObject.get(key), indent, level + 1));
			sb.append(StringPool.COMMA);
			sb.append("\n");
		}

		sb.setIndex(sb.index() - 2);

		sb.append("\n");

		sb.append(_getIndent(indent, level));

		sb.append(StringPool.CLOSE_CURLY_BRACE);

		return sb.toString();
	}

	private static final Log _log = LogFactoryUtil.getLog(JSONUtil.class);

}