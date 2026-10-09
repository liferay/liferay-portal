/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.internal.writer;

import com.liferay.petra.concurrent.ConcurrentReferenceKeyHashMap;
import com.liferay.petra.concurrent.ConcurrentReferenceValueHashMap;
import com.liferay.petra.memory.FinalizeManager;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.StringUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.math.BigDecimal;
import java.math.BigInteger;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.function.Supplier;

/**
 * @author Shuyang Zhou
 * @author Igor Beslic
 */
public class ItemClassIndexUtil {

	public static Map<String, ObjectValuePair<Field, Method>> index(
		Class<?> itemClass) {

		Queue<Class<?>> queue = new LinkedList<>();

		Map<String, ObjectValuePair<Field, Method>> fieldNameObjectValuePairs =
			_fieldNameObjectValuePairs.computeIfAbsent(
				itemClass, clazz -> _index(clazz, queue));

		while ((itemClass = queue.poll()) != null) {
			_fieldNameObjectValuePairs.computeIfAbsent(
				itemClass, clazz -> _index(clazz, queue));
		}

		return fieldNameObjectValuePairs;
	}

	public static boolean isDate(Class<?> clazz) {
		return Objects.equals(clazz, Date.class);
	}

	public static boolean isIterable(Class<?> valueClass) {
		if (valueClass.isArray() ||
			Collection.class.isAssignableFrom(valueClass)) {

			return true;
		}

		return false;
	}

	public static boolean isMap(Class<?> clazz) {
		return Objects.equals(clazz, Map.class);
	}

	public static boolean isMultidimensionalArray(Class<?> clazz) {
		if (!clazz.isArray()) {
			return false;
		}

		Class<?> componentTypeClass = clazz.getComponentType();

		return componentTypeClass.isArray();
	}

	public static boolean isSingleColumnAdoptableArray(Class<?> clazz) {
		if (!clazz.isArray()) {
			return false;
		}

		return isSingleColumnAdoptableValue(clazz.getComponentType());
	}

	public static boolean isSingleColumnAdoptableValue(Class<?> clazz) {
		if (!clazz.isPrimitive() && !_objectTypes.contains(clazz) &&
			!Enum.class.isAssignableFrom(clazz)) {

			return false;
		}

		return true;
	}

	private static Method _getGetterMethod(
		Class<?> clazz, Field field, String name) {

		Class<?> fieldClass = field.getType();

		String methodName = null;

		if (fieldClass.isEnum()) {
			methodName = "get" + fieldClass.getSimpleName();
		}
		else {
			methodName = "get" + StringUtil.upperCaseFirstLetter(name);
		}

		for (Method method : clazz.getMethods()) {
			if (StringUtil.equals(method.getName(), methodName) &&
				(method.getParameterCount() == 0) &&
				Objects.equals(fieldClass, method.getReturnType())) {

				return method;
			}
		}

		return null;
	}

	private static Map<String, ObjectValuePair<Field, Method>> _index(
		Class<?> clazz, Queue<Class<?>> queue) {

		Map<String, ObjectValuePair<Field, Method>> fieldNameObjectValuePairs =
			new HashMap<>();

		while (clazz != Object.class) {
			for (Field field : clazz.getDeclaredFields()) {
				if (isMultidimensionalArray(field.getType()) ||
					Objects.equals(field.getType(), Supplier.class)) {

					continue;
				}

				field.setAccessible(true);

				String name = field.getName();

				if (name.charAt(0) == CharPool.UNDERLINE) {
					name = name.substring(1);
				}

				if (field.isSynthetic()) {
					continue;
				}

				fieldNameObjectValuePairs.put(
					name,
					new ObjectValuePair<>(
						field, _getGetterMethod(clazz, field, name)));

				Class<?> fieldClass = field.getType();

				if (isIterable(fieldClass) || isMap(fieldClass) ||
					isSingleColumnAdoptableArray(fieldClass) ||
					isSingleColumnAdoptableValue(fieldClass) ||
					Objects.equals(clazz, fieldClass)) {

					continue;
				}

				queue.add(clazz);
			}

			if (Objects.equals(
					clazz.getSuperclass(), clazz.getDeclaringClass())) {

				break;
			}

			clazz = clazz.getSuperclass();
		}

		return fieldNameObjectValuePairs;
	}

	private static final Map
		<Class<?>, Map<String, ObjectValuePair<Field, Method>>>
			_fieldNameObjectValuePairs = new ConcurrentReferenceKeyHashMap<>(
				new ConcurrentReferenceValueHashMap<>(
					FinalizeManager.WEAK_REFERENCE_FACTORY),
				FinalizeManager.WEAK_REFERENCE_FACTORY);
	private static final List<Class<?>> _objectTypes = Arrays.asList(
		Boolean.class, BigDecimal.class, BigInteger.class, Byte.class,
		Date.class, Double.class, Float.class, Integer.class, Long.class,
		String.class);

}