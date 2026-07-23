/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.dao.orm;

import com.liferay.portal.kernel.util.CalendarUtil;

import java.math.BigDecimal;

import java.sql.Timestamp;

import java.util.Date;

/**
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 */
public class QueryPos {

	public static QueryPos getInstance(Query query) {
		return new QueryPos(query);
	}

	public void add(BigDecimal value) {
		_query.setBigDecimal(_pos++, value);
	}

	public void add(BigDecimal[] values) {
		add(values, _DEFAULT_ARRAY_COUNT);
	}

	public void add(BigDecimal[] values, int count) {
		for (BigDecimal value : values) {
			for (int j = 0; j < count; j++) {
				add(value);
			}
		}
	}

	public void add(boolean value) {
		_query.setBoolean(_pos++, value);
	}

	public void add(Boolean value) {
		if (value != null) {
			_query.setBoolean(_pos++, value.booleanValue());
		}
		else {
			_addNull();
		}
	}

	public void add(boolean[] values) {
		add(values, _DEFAULT_ARRAY_COUNT);
	}

	public void add(boolean[] values, int count) {
		for (boolean value : values) {
			for (int j = 0; j < count; j++) {
				add(value);
			}
		}
	}

	public void add(Boolean[] values) {
		add(values, _DEFAULT_ARRAY_COUNT);
	}

	public void add(Boolean[] values, int count) {
		for (Boolean value : values) {
			for (int j = 0; j < count; j++) {
				add(value);
			}
		}
	}

	public void add(Date value) {
		add(CalendarUtil.getTimestamp(value));
	}

	public void add(Date[] values) {
		add(values, _DEFAULT_ARRAY_COUNT);
	}

	public void add(Date[] values, int count) {
		for (Date value : values) {
			for (int j = 0; j < count; j++) {
				add(CalendarUtil.getTimestamp(value));
			}
		}
	}

	public void add(double value) {
		_query.setDouble(_pos++, value);
	}

	public void add(Double value) {
		if (value != null) {
			_query.setDouble(_pos++, value.doubleValue());
		}
		else {
			_addNull();
		}
	}

	public void add(double[] values) {
		add(values, _DEFAULT_ARRAY_COUNT);
	}

	public void add(double[] values, int count) {
		for (double value : values) {
			for (int j = 0; j < count; j++) {
				add(value);
			}
		}
	}

	public void add(Double[] values) {
		add(values, _DEFAULT_ARRAY_COUNT);
	}

	public void add(Double[] values, int count) {
		for (Double value : values) {
			for (int j = 0; j < count; j++) {
				add(value);
			}
		}
	}

	public void add(float value) {
		_query.setFloat(_pos++, value);
	}

	public void add(Float value) {
		if (value != null) {
			_query.setFloat(_pos++, value.intValue());
		}
		else {
			_addNull();
		}
	}

	public void add(float[] values) {
		add(values, _DEFAULT_ARRAY_COUNT);
	}

	public void add(float[] values, int count) {
		for (float value : values) {
			for (int j = 0; j < count; j++) {
				add(value);
			}
		}
	}

	public void add(Float[] values) {
		add(values, _DEFAULT_ARRAY_COUNT);
	}

	public void add(Float[] values, int count) {
		for (Float value : values) {
			for (int j = 0; j < count; j++) {
				add(value);
			}
		}
	}

	public void add(int value) {
		_query.setInteger(_pos++, value);
	}

	public void add(int[] values) {
		add(values, _DEFAULT_ARRAY_COUNT);
	}

	public void add(int[] values, int count) {
		for (int value : values) {
			for (int j = 0; j < count; j++) {
				add(value);
			}
		}
	}

	public void add(Integer value) {
		if (value != null) {
			_query.setInteger(_pos++, value.intValue());
		}
		else {
			_addNull();
		}
	}

	public void add(Integer[] values) {
		add(values, _DEFAULT_ARRAY_COUNT);
	}

	public void add(Integer[] values, int count) {
		for (Integer value : values) {
			for (int j = 0; j < count; j++) {
				add(value);
			}
		}
	}

	public void add(long value) {
		_query.setLong(_pos++, value);
	}

	public void add(Long value) {
		if (value != null) {
			_query.setLong(_pos++, value.longValue());
		}
		else {
			_addNull();
		}
	}

	public void add(long[] values) {
		add(values, _DEFAULT_ARRAY_COUNT);
	}

	public void add(long[] values, int count) {
		for (long value : values) {
			for (int j = 0; j < count; j++) {
				add(value);
			}
		}
	}

	public void add(Long[] values) {
		add(values, _DEFAULT_ARRAY_COUNT);
	}

	public void add(Long[] values, int count) {
		for (Long value : values) {
			for (int j = 0; j < count; j++) {
				add(value);
			}
		}
	}

	public void add(Object object) {
		if (object == null) {
			_addNull();

			return;
		}

		Class<?> clazz = object.getClass();

		if (clazz == BigDecimal.class) {
			add((BigDecimal)object);
		}
		else if (clazz == Boolean.class) {
			Boolean booleanObj = (Boolean)object;

			add(booleanObj.booleanValue());
		}
		else if (clazz == Date.class) {
			add(CalendarUtil.getTimestamp((Date)object));
		}
		else if (clazz == Double.class) {
			Double doubleObj = (Double)object;

			add(doubleObj.doubleValue());
		}
		else if (clazz == Float.class) {
			Float floatObj = (Float)object;

			add(floatObj.floatValue());
		}
		else if (clazz == Integer.class) {
			Integer integerObj = (Integer)object;

			add(integerObj.intValue());
		}
		else if (clazz == Long.class) {
			Long longObj = (Long)object;

			add(longObj.longValue());
		}
		else if (clazz == Short.class) {
			Short shortObj = (Short)object;

			add(shortObj.shortValue());
		}
		else if (clazz == String.class) {
			add((String)object);
		}
		else if (clazz == Timestamp.class) {
			add((Timestamp)object);
		}
		else {
			throw new RuntimeException("Unsupport type " + clazz.getName());
		}
	}

	public void add(short value) {
		_query.setShort(_pos++, value);
	}

	public void add(Short value) {
		if (value != null) {
			_query.setShort(_pos++, value.shortValue());
		}
		else {
			_addNull();
		}
	}

	public void add(short[] values) {
		add(values, _DEFAULT_ARRAY_COUNT);
	}

	public void add(short[] values, int count) {
		for (short value : values) {
			for (int j = 0; j < count; j++) {
				add(value);
			}
		}
	}

	public void add(Short[] values) {
		add(values, _DEFAULT_ARRAY_COUNT);
	}

	public void add(Short[] values, int count) {
		for (Short value : values) {
			for (int j = 0; j < count; j++) {
				add(value);
			}
		}
	}

	public void add(String value) {
		_query.setString(_pos++, value);
	}

	public void add(String[] values) {
		add(values, _DEFAULT_ARRAY_COUNT);
	}

	public void add(String[] values, int count) {
		for (String value : values) {
			for (int j = 0; j < count; j++) {
				add(value);
			}
		}
	}

	public void add(Timestamp value) {
		_query.setTimestamp(_pos++, value);
	}

	public void add(Timestamp[] values) {
		add(values, _DEFAULT_ARRAY_COUNT);
	}

	public void add(Timestamp[] values, int count) {
		for (Timestamp value : values) {
			for (int j = 0; j < count; j++) {
				add(value);
			}
		}
	}

	public int getPos() {
		return _pos;
	}

	private QueryPos(Query query) {
		_query = query;
	}

	private void _addNull() {
		_query.setSerializable(_pos++, null);
	}

	private static final int _DEFAULT_ARRAY_COUNT = 1;

	private int _pos = 1;
	private final Query _query;

}