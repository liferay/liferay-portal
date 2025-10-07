/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

/**
 * @author Brian Wing Shun Chan
 */
public class GetterUtil_IW {
	public static GetterUtil_IW getInstance() {
		return _instance;
	}

	public boolean get(java.lang.Object value, boolean defaultValue) {
		return GetterUtil.get(value, defaultValue);
	}

	public java.util.Date get(java.lang.Object value,
		java.text.DateFormat dateFormat, java.util.Date defaultValue) {
		return GetterUtil.get(value, dateFormat, defaultValue);
	}

	public double get(java.lang.Object value, double defaultValue) {
		return GetterUtil.get(value, defaultValue);
	}

	public float get(java.lang.Object value, float defaultValue) {
		return GetterUtil.get(value, defaultValue);
	}

	public int get(java.lang.Object value, int defaultValue) {
		return GetterUtil.get(value, defaultValue);
	}

	public long get(java.lang.Object value, long defaultValue) {
		return GetterUtil.get(value, defaultValue);
	}

	public java.lang.Number get(java.lang.Object value,
		java.lang.Number defaultValue) {
		return GetterUtil.get(value, defaultValue);
	}

	public short get(java.lang.Object value, short defaultValue) {
		return GetterUtil.get(value, defaultValue);
	}

	public java.lang.String get(java.lang.Object value,
		java.lang.String defaultValue) {
		return GetterUtil.get(value, defaultValue);
	}

	public boolean get(java.lang.String value, boolean defaultValue) {
		return GetterUtil.get(value, defaultValue);
	}

	public java.util.Date get(java.lang.String value,
		java.text.DateFormat dateFormat, java.util.Date defaultValue) {
		return GetterUtil.get(value, dateFormat, defaultValue);
	}

	public double get(java.lang.String value, double defaultValue) {
		return GetterUtil.get(value, defaultValue);
	}

	public double get(java.lang.String value, double defaultValue,
		java.util.Locale locale) {
		return GetterUtil.get(value, defaultValue, locale);
	}

	public float get(java.lang.String value, float defaultValue) {
		return GetterUtil.get(value, defaultValue);
	}

	public int get(java.lang.String value, int defaultValue) {
		return GetterUtil.get(value, defaultValue);
	}

	public long get(java.lang.String value, long defaultValue) {
		return GetterUtil.get(value, defaultValue);
	}

	public short get(java.lang.String value, short defaultValue) {
		return GetterUtil.get(value, defaultValue);
	}

	public java.lang.String get(java.lang.String value,
		java.lang.String defaultValue) {
		return GetterUtil.get(value, defaultValue);
	}

	public boolean getBoolean(java.lang.Object value) {
		return GetterUtil.getBoolean(value);
	}

	public boolean getBoolean(java.lang.Object value, boolean defaultValue) {
		return GetterUtil.getBoolean(value, defaultValue);
	}

	public boolean getBoolean(java.lang.String value) {
		return GetterUtil.getBoolean(value);
	}

	public boolean getBoolean(java.lang.String value, boolean defaultValue) {
		return GetterUtil.getBoolean(value, defaultValue);
	}

	public boolean[] getBooleanValues(java.lang.Object value) {
		return GetterUtil.getBooleanValues(value);
	}

	public boolean[] getBooleanValues(java.lang.Object value,
		boolean[] defaultValue) {
		return GetterUtil.getBooleanValues(value, defaultValue);
	}

	public boolean[] getBooleanValues(java.lang.String[] values) {
		return GetterUtil.getBooleanValues(values);
	}

	public boolean[] getBooleanValues(java.lang.String[] values,
		boolean[] defaultValue) {
		return GetterUtil.getBooleanValues(values, defaultValue);
	}

	public java.util.Date getDate(java.lang.Object value,
		java.text.DateFormat dateFormat) {
		return GetterUtil.getDate(value, dateFormat);
	}

	public java.util.Date getDate(java.lang.Object value,
		java.text.DateFormat dateFormat, java.util.Date defaultValue) {
		return GetterUtil.getDate(value, dateFormat, defaultValue);
	}

	public java.util.Date getDate(java.lang.String value,
		java.text.DateFormat dateFormat) {
		return GetterUtil.getDate(value, dateFormat);
	}

	public java.util.Date getDate(java.lang.String value,
		java.text.DateFormat dateFormat, java.util.Date defaultValue) {
		return GetterUtil.getDate(value, dateFormat, defaultValue);
	}

	public java.util.Date[] getDateValues(java.lang.Object value,
		java.text.DateFormat dateFormat) {
		return GetterUtil.getDateValues(value, dateFormat);
	}

	public java.util.Date[] getDateValues(java.lang.Object value,
		java.text.DateFormat dateFormat, java.util.Date[] defaultValue) {
		return GetterUtil.getDateValues(value, dateFormat, defaultValue);
	}

	public java.util.Date[] getDateValues(java.lang.String[] values,
		java.text.DateFormat dateFormat) {
		return GetterUtil.getDateValues(values, dateFormat);
	}

	public java.util.Date[] getDateValues(java.lang.String[] values,
		java.text.DateFormat dateFormat, java.util.Date[] defaultValue) {
		return GetterUtil.getDateValues(values, dateFormat, defaultValue);
	}

	public double getDouble(java.lang.Object value) {
		return GetterUtil.getDouble(value);
	}

	public double getDouble(java.lang.Object value, double defaultValue) {
		return GetterUtil.getDouble(value, defaultValue);
	}

	public double getDouble(java.lang.String value) {
		return GetterUtil.getDouble(value);
	}

	public double getDouble(java.lang.String value, double defaultValue) {
		return GetterUtil.getDouble(value, defaultValue);
	}

	public double getDouble(java.lang.String value, java.util.Locale locale) {
		return GetterUtil.getDouble(value, locale);
	}

	public double[] getDoubleValues(java.lang.Object value) {
		return GetterUtil.getDoubleValues(value);
	}

	public double[] getDoubleValues(java.lang.Object value,
		double[] defaultValue) {
		return GetterUtil.getDoubleValues(value, defaultValue);
	}

	public double[] getDoubleValues(java.lang.String[] values) {
		return GetterUtil.getDoubleValues(values);
	}

	public double[] getDoubleValues(java.lang.String[] values,
		double[] defaultValue) {
		return GetterUtil.getDoubleValues(values, defaultValue);
	}

	public float getFloat(java.lang.Object value) {
		return GetterUtil.getFloat(value);
	}

	public float getFloat(java.lang.Object value, float defaultValue) {
		return GetterUtil.getFloat(value, defaultValue);
	}

	public float getFloat(java.lang.String value) {
		return GetterUtil.getFloat(value);
	}

	public float getFloat(java.lang.String value, float defaultValue) {
		return GetterUtil.getFloat(value, defaultValue);
	}

	public float[] getFloatValues(java.lang.Object value) {
		return GetterUtil.getFloatValues(value);
	}

	public float[] getFloatValues(java.lang.Object value, float[] defaultValue) {
		return GetterUtil.getFloatValues(value, defaultValue);
	}

	public float[] getFloatValues(java.lang.String[] values) {
		return GetterUtil.getFloatValues(values);
	}

	public float[] getFloatValues(java.lang.String[] values,
		float[] defaultValue) {
		return GetterUtil.getFloatValues(values, defaultValue);
	}

	public int getInteger(java.lang.Object value) {
		return GetterUtil.getInteger(value);
	}

	public int getInteger(java.lang.Object value, int defaultValue) {
		return GetterUtil.getInteger(value, defaultValue);
	}

	public int getInteger(java.lang.String value) {
		return GetterUtil.getInteger(value);
	}

	public int getInteger(java.lang.String value, int defaultValue) {
		return GetterUtil.getInteger(value, defaultValue);
	}

	public int getIntegerStrict(java.lang.String value) {
		return GetterUtil.getIntegerStrict(value);
	}

	public int[] getIntegerValues(java.lang.Object value) {
		return GetterUtil.getIntegerValues(value);
	}

	public int[] getIntegerValues(java.lang.Object value, int[] defaultValue) {
		return GetterUtil.getIntegerValues(value, defaultValue);
	}

	public int[] getIntegerValues(java.lang.String[] values) {
		return GetterUtil.getIntegerValues(values);
	}

	public int[] getIntegerValues(java.lang.String[] values, int[] defaultValue) {
		return GetterUtil.getIntegerValues(values, defaultValue);
	}

	public long getLong(java.lang.Object value) {
		return GetterUtil.getLong(value);
	}

	public long getLong(java.lang.Object value, long defaultValue) {
		return GetterUtil.getLong(value, defaultValue);
	}

	public long getLong(java.lang.String value) {
		return GetterUtil.getLong(value);
	}

	public long getLong(java.lang.String value, long defaultValue) {
		return GetterUtil.getLong(value, defaultValue);
	}

	public long getLongStrict(java.lang.String value) {
		return GetterUtil.getLongStrict(value);
	}

	public long[] getLongValues(java.lang.Object value) {
		return GetterUtil.getLongValues(value);
	}

	public long[] getLongValues(java.lang.Object value, long[] defaultValue) {
		return GetterUtil.getLongValues(value, defaultValue);
	}

	public long[] getLongValues(java.lang.String[] values) {
		return GetterUtil.getLongValues(values);
	}

	public long[] getLongValues(java.lang.String[] values, long[] defaultValue) {
		return GetterUtil.getLongValues(values, defaultValue);
	}

	public java.lang.Number getNumber(java.lang.Object value) {
		return GetterUtil.getNumber(value);
	}

	public java.lang.Number getNumber(java.lang.Object value,
		java.lang.Number defaultValue) {
		return GetterUtil.getNumber(value, defaultValue);
	}

	public java.lang.Number getNumber(java.lang.String value) {
		return GetterUtil.getNumber(value);
	}

	public java.lang.Number getNumber(java.lang.String value,
		java.lang.Number defaultValue) {
		return GetterUtil.getNumber(value, defaultValue);
	}

	public java.lang.Number[] getNumberValues(java.lang.Object value) {
		return GetterUtil.getNumberValues(value);
	}

	public java.lang.Number[] getNumberValues(java.lang.Object value,
		java.lang.Number[] defaultValue) {
		return GetterUtil.getNumberValues(value, defaultValue);
	}

	public java.lang.Number[] getNumberValues(java.lang.String[] values) {
		return GetterUtil.getNumberValues(values);
	}

	public java.lang.Number[] getNumberValues(java.lang.String[] values,
		java.lang.Number[] defaultValue) {
		return GetterUtil.getNumberValues(values, defaultValue);
	}

	public java.lang.Object getObject(java.lang.Object value) {
		return GetterUtil.getObject(value);
	}

	public java.lang.Object getObject(java.lang.Object value,
		java.lang.Object defaultValue) {
		return GetterUtil.getObject(value, defaultValue);
	}

	public <T, E extends java.lang.Throwable> T getObject(T value,
		com.liferay.petra.function.UnsafeSupplier<T, E> defaultValueUnsafeSupplier)
		throws E {
		return GetterUtil.getObject(value, defaultValueUnsafeSupplier);
	}

	public short getShort(java.lang.Object value) {
		return GetterUtil.getShort(value);
	}

	public short getShort(java.lang.Object value, short defaultValue) {
		return GetterUtil.getShort(value, defaultValue);
	}

	public short getShort(java.lang.String value) {
		return GetterUtil.getShort(value);
	}

	public short getShort(java.lang.String value, short defaultValue) {
		return GetterUtil.getShort(value, defaultValue);
	}

	public short getShortStrict(java.lang.String value) {
		return GetterUtil.getShortStrict(value);
	}

	public short[] getShortValues(java.lang.Object value) {
		return GetterUtil.getShortValues(value);
	}

	public short[] getShortValues(java.lang.Object value, short[] defaultValue) {
		return GetterUtil.getShortValues(value, defaultValue);
	}

	public short[] getShortValues(java.lang.String[] values) {
		return GetterUtil.getShortValues(values);
	}

	public short[] getShortValues(java.lang.String[] values,
		short[] defaultValue) {
		return GetterUtil.getShortValues(values, defaultValue);
	}

	public java.lang.String getString(java.lang.Object value) {
		return GetterUtil.getString(value);
	}

	public java.lang.String getString(java.lang.Object value,
		java.lang.String defaultValue) {
		return GetterUtil.getString(value, defaultValue);
	}

	public java.lang.String getString(java.lang.String value) {
		return GetterUtil.getString(value);
	}

	public java.lang.String getString(java.lang.String value,
		java.lang.String defaultValue) {
		return GetterUtil.getString(value, defaultValue);
	}

	public java.lang.String[] getStringValues(java.lang.Object value) {
		return GetterUtil.getStringValues(value);
	}

	public java.lang.String[] getStringValues(java.lang.Object value,
		java.lang.String[] defaultValue) {
		return GetterUtil.getStringValues(value, defaultValue);
	}

	public java.lang.String[] getStringValues(java.lang.Object value,
		java.util.function.Supplier<java.lang.String[]> defaultValueSupplier) {
		return GetterUtil.getStringValues(value, defaultValueSupplier);
	}

	public java.lang.String[] getStringValues(java.lang.Object[] values,
		java.lang.String[] defaultValue) {
		return GetterUtil.getStringValues(values, defaultValue);
	}

	public java.lang.String[] getStringValues(java.lang.Object[] values,
		java.util.function.Supplier<java.lang.String[]> defaultValueSupplier) {
		return GetterUtil.getStringValues(values, defaultValueSupplier);
	}

	public java.lang.String[] getStringValues(java.lang.String[] values) {
		return GetterUtil.getStringValues(values);
	}

	private GetterUtil_IW() {
	}

	private static GetterUtil_IW _instance = new GetterUtil_IW();
}