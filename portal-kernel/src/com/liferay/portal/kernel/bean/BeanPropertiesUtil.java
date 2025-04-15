/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.bean;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Brian Wing Shun Chan
 */
public class BeanPropertiesUtil {

	public static void copyProperties(Object source, Object target) {
		_beanProperties.copyProperties(source, target);
	}

	public static <T> T deepCopyProperties(Object source) throws Exception {
		return _beanProperties.deepCopyProperties(source);
	}

	public static BeanProperties getBeanProperties() {
		return _beanProperties;
	}

	public static boolean getBoolean(Object bean, String param) {
		return _beanProperties.getBoolean(bean, param);
	}

	public static boolean getBoolean(
		Object bean, String param, boolean defaultValue) {

		return _beanProperties.getBoolean(bean, param, defaultValue);
	}

	public static boolean getBooleanSilent(Object bean, String param) {
		return _beanProperties.getBooleanSilent(bean, param);
	}

	public static boolean getBooleanSilent(
		Object bean, String param, boolean defaultValue) {

		return _beanProperties.getBooleanSilent(bean, param, defaultValue);
	}

	public static byte getByte(Object bean, String param) {
		return _beanProperties.getByte(bean, param);
	}

	public static byte getByte(Object bean, String param, byte defaultValue) {
		return _beanProperties.getByte(bean, param, defaultValue);
	}

	public static byte getByteSilent(Object bean, String param) {
		return _beanProperties.getByteSilent(bean, param);
	}

	public static byte getByteSilent(
		Object bean, String param, byte defaultValue) {

		return _beanProperties.getByteSilent(bean, param, defaultValue);
	}

	public static double getDouble(Object bean, String param) {
		return _beanProperties.getDouble(bean, param);
	}

	public static double getDouble(
		Object bean, String param, double defaultValue) {

		return _beanProperties.getDouble(bean, param, defaultValue);
	}

	public static double getDoubleSilent(Object bean, String param) {
		return _beanProperties.getDoubleSilent(bean, param);
	}

	public static double getDoubleSilent(
		Object bean, String param, double defaultValue) {

		return _beanProperties.getDoubleSilent(bean, param, defaultValue);
	}

	public static float getFloat(Object bean, String param) {
		return _beanProperties.getFloat(bean, param);
	}

	public static float getFloat(
		Object bean, String param, float defaultValue) {

		return _beanProperties.getFloat(bean, param, defaultValue);
	}

	public static float getFloatSilent(Object bean, String param) {
		return _beanProperties.getFloatSilent(bean, param);
	}

	public static float getFloatSilent(
		Object bean, String param, float defaultValue) {

		return _beanProperties.getFloatSilent(bean, param, defaultValue);
	}

	public static int getInteger(Object bean, String param) {
		return _beanProperties.getInteger(bean, param);
	}

	public static int getInteger(Object bean, String param, int defaultValue) {
		return _beanProperties.getInteger(bean, param, defaultValue);
	}

	public static int getIntegerSilent(Object bean, String param) {
		return _beanProperties.getIntegerSilent(bean, param);
	}

	public static int getIntegerSilent(
		Object bean, String param, int defaultValue) {

		return _beanProperties.getIntegerSilent(bean, param, defaultValue);
	}

	public static long getLong(Object bean, String param) {
		return _beanProperties.getLong(bean, param);
	}

	public static long getLong(Object bean, String param, long defaultValue) {
		return _beanProperties.getLong(bean, param, defaultValue);
	}

	public static long getLongSilent(Object bean, String param) {
		return _beanProperties.getLongSilent(bean, param);
	}

	public static long getLongSilent(
		Object bean, String param, long defaultValue) {

		return _beanProperties.getLongSilent(bean, param, defaultValue);
	}

	public static Object getObject(Object bean, String param) {
		return _beanProperties.getObject(bean, param);
	}

	public static Object getObject(
		Object bean, String param, Object defaultValue) {

		return _beanProperties.getObject(bean, param, defaultValue);
	}

	public static Object getObjectSilent(Object bean, String param) {
		return _beanProperties.getObjectSilent(bean, param);
	}

	public static Object getObjectSilent(
		Object bean, String param, Object defaultValue) {

		return _beanProperties.getObjectSilent(bean, param, defaultValue);
	}

	public static Class<?> getObjectType(Object bean, String param) {
		return _beanProperties.getObjectType(bean, param);
	}

	public static Class<?> getObjectType(
		Object bean, String param, Class<?> defaultValue) {

		return _beanProperties.getObjectType(bean, param, defaultValue);
	}

	public static Class<?> getObjectTypeSilent(Object bean, String param) {
		return _beanProperties.getObjectType(bean, param);
	}

	public static Class<?> getObjectTypeSilent(
		Object bean, String param, Class<?> defaultValue) {

		return _beanProperties.getObjectType(bean, param, defaultValue);
	}

	public static short getShort(Object bean, String param) {
		return _beanProperties.getShort(bean, param);
	}

	public static short getShort(
		Object bean, String param, short defaultValue) {

		return _beanProperties.getShort(bean, param, defaultValue);
	}

	public static short getShortSilent(Object bean, String param) {
		return _beanProperties.getShortSilent(bean, param);
	}

	public static short getShortSilent(
		Object bean, String param, short defaultValue) {

		return _beanProperties.getShortSilent(bean, param, defaultValue);
	}

	public static String getString(Object bean, String param) {
		return _beanProperties.getString(bean, param);
	}

	public static String getString(
		Object bean, String param, String defaultValue) {

		return _beanProperties.getString(bean, param, defaultValue);
	}

	public static String getStringSilent(Object bean, String param) {
		return _beanProperties.getStringSilent(bean, param);
	}

	public static String getStringSilent(
		Object bean, String param, String defaultValue) {

		return _beanProperties.getStringSilent(bean, param, defaultValue);
	}

	public static void setProperties(
		Object bean, HttpServletRequest httpServletRequest) {

		_beanProperties.setProperties(bean, httpServletRequest);
	}

	public static void setProperties(
		Object bean, HttpServletRequest httpServletRequest,
		String[] ignoreProperties) {

		_beanProperties.setProperties(
			bean, httpServletRequest, ignoreProperties);
	}

	public static void setProperty(Object bean, String param, Object value) {
		_beanProperties.setProperty(bean, param, value);
	}

	public static void setPropertySilent(
		Object bean, String param, Object value) {

		_beanProperties.setPropertySilent(bean, param, value);
	}

	public void setBeanProperties(BeanProperties beanProperties) {
		_beanProperties = beanProperties;
	}

	private static BeanProperties _beanProperties;

}