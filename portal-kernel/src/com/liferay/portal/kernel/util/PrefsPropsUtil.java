/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import jakarta.portlet.PortletPreferences;

import java.util.Properties;

/**
 * @author Brian Wing Shun Chan
 */
public class PrefsPropsUtil {

	public static boolean getBoolean(long companyId, String name) {
		return _prefsProps.getBoolean(companyId, name);
	}

	public static boolean getBoolean(
		long companyId, String name, boolean defaultValue) {

		return _prefsProps.getBoolean(companyId, name, defaultValue);
	}

	public static boolean getBoolean(
		PortletPreferences portletPreferences, String name) {

		return _prefsProps.getBoolean(portletPreferences, name);
	}

	public static boolean getBoolean(
		PortletPreferences portletPreferences, String name,
		boolean defaultValue) {

		return _prefsProps.getBoolean(portletPreferences, name, defaultValue);
	}

	public static boolean getBoolean(String name) {
		return _prefsProps.getBoolean(name);
	}

	public static boolean getBoolean(String name, boolean defaultValue) {
		return _prefsProps.getBoolean(name, defaultValue);
	}

	public static String getContent(long companyId, String name) {
		return _prefsProps.getContent(companyId, name);
	}

	public static String getContent(
		PortletPreferences portletPreferences, String name) {

		return _prefsProps.getContent(portletPreferences, name);
	}

	public static String getContent(String name) {
		return _prefsProps.getContent(name);
	}

	public static double getDouble(long companyId, String name) {
		return _prefsProps.getDouble(companyId, name);
	}

	public static double getDouble(
		long companyId, String name, double defaultValue) {

		return _prefsProps.getDouble(companyId, name, defaultValue);
	}

	public static double getDouble(
		PortletPreferences portletPreferences, String name) {

		return _prefsProps.getDouble(portletPreferences, name);
	}

	public static double getDouble(
		PortletPreferences portletPreferences, String name,
		double defaultValue) {

		return _prefsProps.getDouble(portletPreferences, name, defaultValue);
	}

	public static double getDouble(String name) {
		return _prefsProps.getDouble(name);
	}

	public static double getDouble(String name, double defaultValue) {
		return _prefsProps.getDouble(name, defaultValue);
	}

	public static int getInteger(long companyId, String name) {
		return _prefsProps.getInteger(companyId, name);
	}

	public static int getInteger(
		long companyId, String name, int defaultValue) {

		return _prefsProps.getInteger(companyId, name, defaultValue);
	}

	public static int getInteger(
		PortletPreferences portletPreferences, String name) {

		return _prefsProps.getInteger(portletPreferences, name);
	}

	public static int getInteger(
		PortletPreferences portletPreferences, String name, int defaultValue) {

		return _prefsProps.getInteger(portletPreferences, name, defaultValue);
	}

	public static int getInteger(String name) {
		return _prefsProps.getInteger(name);
	}

	public static int getInteger(String name, int defaultValue) {
		return _prefsProps.getInteger(name, defaultValue);
	}

	public static long getLong(long companyId, String name) {
		return _prefsProps.getLong(companyId, name);
	}

	public static long getLong(long companyId, String name, long defaultValue) {
		return _prefsProps.getLong(companyId, name, defaultValue);
	}

	public static long getLong(
		PortletPreferences portletPreferences, String name) {

		return _prefsProps.getLong(portletPreferences, name);
	}

	public static long getLong(
		PortletPreferences portletPreferences, String name, long defaultValue) {

		return _prefsProps.getLong(portletPreferences, name, defaultValue);
	}

	public static long getLong(String name) {
		return _prefsProps.getLong(name);
	}

	public static long getLong(String name, long defaultValue) {
		return _prefsProps.getLong(name, defaultValue);
	}

	public static PortletPreferences getPreferences() {
		return _prefsProps.getPreferences();
	}

	public static PortletPreferences getPreferences(long companyId) {
		return _prefsProps.getPreferences(companyId);
	}

	public static PrefsProps getPrefsProps() {
		return _prefsProps;
	}

	public static Properties getProperties(
		PortletPreferences portletPreferences, String prefix,
		boolean removePrefix) {

		return _prefsProps.getProperties(
			portletPreferences, prefix, removePrefix);
	}

	public static Properties getProperties(
		String prefix, boolean removePrefix) {

		return _prefsProps.getProperties(prefix, removePrefix);
	}

	public static short getShort(long companyId, String name) {
		return _prefsProps.getShort(companyId, name);
	}

	public static short getShort(
		long companyId, String name, short defaultValue) {

		return _prefsProps.getShort(companyId, name, defaultValue);
	}

	public static short getShort(
		PortletPreferences portletPreferences, String name) {

		return _prefsProps.getShort(portletPreferences, name);
	}

	public static short getShort(
		PortletPreferences portletPreferences, String name,
		short defaultValue) {

		return _prefsProps.getShort(portletPreferences, name, defaultValue);
	}

	public static short getShort(String name) {
		return _prefsProps.getShort(name);
	}

	public static short getShort(String name, short defaultValue) {
		return _prefsProps.getShort(name, defaultValue);
	}

	public static String getString(long companyId, String name) {
		return _prefsProps.getString(companyId, name);
	}

	public static String getString(
		long companyId, String name, String defaultValue) {

		return _prefsProps.getString(companyId, name, defaultValue);
	}

	public static String getString(
		PortletPreferences portletPreferences, String name) {

		return _prefsProps.getString(portletPreferences, name);
	}

	public static String getString(
		PortletPreferences portletPreferences, String name,
		boolean defaultValue) {

		return _prefsProps.getString(portletPreferences, name, defaultValue);
	}

	public static String getString(
		PortletPreferences portletPreferences, String name,
		double defaultValue) {

		return _prefsProps.getString(portletPreferences, name, defaultValue);
	}

	public static String getString(
		PortletPreferences portletPreferences, String name, int defaultValue) {

		return _prefsProps.getString(portletPreferences, name, defaultValue);
	}

	public static String getString(
		PortletPreferences portletPreferences, String name, long defaultValue) {

		return _prefsProps.getString(portletPreferences, name, defaultValue);
	}

	public static String getString(
		PortletPreferences portletPreferences, String name,
		short defaultValue) {

		return _prefsProps.getString(portletPreferences, name, defaultValue);
	}

	public static String getString(
		PortletPreferences portletPreferences, String name,
		String defaultValue) {

		return _prefsProps.getString(portletPreferences, name, defaultValue);
	}

	public static String getString(String name) {
		return _prefsProps.getString(name);
	}

	public static String getString(String name, String defaultValue) {
		return _prefsProps.getString(name, defaultValue);
	}

	public static String[] getStringArray(
		long companyId, String name, String delimiter) {

		return _prefsProps.getStringArray(companyId, name, delimiter);
	}

	public static String[] getStringArray(
		long companyId, String name, String delimiter, String[] defaultValue) {

		return _prefsProps.getStringArray(
			companyId, name, delimiter, defaultValue);
	}

	public static String[] getStringArray(
		PortletPreferences portletPreferences, String name, String delimiter) {

		return _prefsProps.getStringArray(portletPreferences, name, delimiter);
	}

	public static String[] getStringArray(
		PortletPreferences portletPreferences, String name, String delimiter,
		String[] defaultValue) {

		return _prefsProps.getStringArray(
			portletPreferences, name, delimiter, defaultValue);
	}

	public static String[] getStringArray(String name, String delimiter) {
		return _prefsProps.getStringArray(name, delimiter);
	}

	public static String[] getStringArray(
		String name, String delimiter, String[] defaultValue) {

		return _prefsProps.getStringArray(name, delimiter, defaultValue);
	}

	public static String getStringFromNames(long companyId, String... names) {
		return _prefsProps.getStringFromNames(companyId, names);
	}

	public void setPrefsProps(PrefsProps prefsProps) {
		_prefsProps = prefsProps;
	}

	private static PrefsProps _prefsProps;

}