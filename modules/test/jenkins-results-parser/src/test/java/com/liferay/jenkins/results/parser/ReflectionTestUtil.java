/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.lang.reflect.Field;

/**
 * @author Shuyang Zhou
 */
public class ReflectionTestUtil {

	public static Field getField(Class<?> clazz, String fieldName) {
		while (clazz != null) {
			try {
				Field field = clazz.getDeclaredField(fieldName);

				field.setAccessible(true);

				return field;
			}
			catch (NoSuchFieldException noSuchFieldException) {
				clazz = clazz.getSuperclass();
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		throw new RuntimeException(
			new NoSuchFieldException("No field with name " + fieldName));
	}

	public static <T> T getFieldValue(Class<?> clazz, String fieldName) {
		Field field = getField(clazz, fieldName);

		try {
			return (T)field.get(null);
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	public static <T> T getFieldValue(Object instance, String fieldName) {
		Field field = getField(instance.getClass(), fieldName);

		try {
			return (T)field.get(instance);
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	public static void setFieldValue(
		Class<?> clazz, String fieldName, Object value) {

		Field field = getField(clazz, fieldName);

		try {
			field.set(null, value);
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	public static void setFieldValue(
		Object instance, String fieldName, Object value) {

		Field field = getField(instance.getClass(), fieldName);

		try {
			field.set(instance, value);
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

}