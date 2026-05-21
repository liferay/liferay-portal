/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.persistence.impl;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.Objects;
import java.util.function.Function;

/**
 * @author Shuyang Zhou
 */
public class ArrayableFinderColumn<T extends BaseModel<T>>
	extends FinderColumn<T> {

	public ArrayableFinderColumn(
		String entityAlias, String columnName, String dbColumnName, Type type,
		String comparator, boolean andOperator, boolean caseSensitive,
		boolean convertNull, Function<T, Object> valueExtractor) {

		super(
			entityAlias, columnName, dbColumnName, type, comparator,
			caseSensitive, convertNull, valueExtractor);

		_andOperator = andOperator;

		_hqlInPrefix = StringBundler.concat(
			"(", entityAlias, columnName, andOperator ? " NOT IN (" : " IN (");

		if (Objects.equals(columnName, dbColumnName)) {
			_sqlInPrefix = _hqlInPrefix;
		}
		else {
			_sqlInPrefix = StringUtil.replace(
				_hqlInPrefix, columnName, dbColumnName);
		}
	}

	public ArrayableFinderColumn(
		String entityAlias, String columnName, Type type, String comparator,
		boolean andOperator, boolean caseSensitive, boolean convertNull,
		Function<T, Object> valueExtractor) {

		this(
			entityAlias, columnName, columnName, type, comparator, andOperator,
			caseSensitive, convertNull, valueExtractor);
	}

	@Override
	public void bindValue(QueryPos queryPos, Object normalizedValue) {
		if (type == Type.STRING) {
			for (String stringValue : (String[])normalizedValue) {
				if ((stringValue != null) && !stringValue.isEmpty()) {
					queryPos.add(stringValue);
				}
			}

			return;
		}

		for (Object value : (Object[])normalizedValue) {
			queryPos.add(value);
		}
	}

	@Override
	public String getSqlFragment(Object normalizedValue, boolean sqlQuery) {
		Object[] array = (Object[])normalizedValue;

		if (array.length == 0) {
			return "";
		}

		if (type == Type.STRING) {
			return _buildStringSqlFragment((String[])normalizedValue, sqlQuery);
		}

		StringBundler sb = new StringBundler(array.length + 1);

		sb.append(sqlQuery ? _sqlInPrefix : _hqlInPrefix);

		for (int i = 0; i < array.length; i++) {
			sb.append("?,");
		}

		sb.setStringAt("?))", sb.index() - 1);

		return sb.toString();
	}

	public boolean isAndOperator() {
		return _andOperator;
	}

	@Override
	public boolean matches(T entity, Object normalizedValue) {
		Object entityValue = valueExtractor.apply(entity);

		if ((type == Type.STRING) && !caseSensitive) {
			entityValue = StringUtil.toLowerCase((String)entityValue);
		}

		return ArrayUtil.contains((Object[])normalizedValue, entityValue);
	}

	@Override
	public Object normalizeValue(Object value) {
		if (type == Type.STRING) {
			return _normalizeStringArray((String[])value);
		}

		return _toObjectArray(value);
	}

	@Override
	public Object toFinderArg(Object normalizedValue) {
		if (type == Type.STRING) {
			String[] strings = (String[])normalizedValue;

			if (strings.length == 1) {
				return strings[0];
			}

			return StringUtil.merge(strings);
		}

		Object[] array = (Object[])normalizedValue;

		if (array.length == 1) {
			return super.toFinderArg(array[0]);
		}

		return StringUtil.merge(array);
	}

	private String _buildStringSqlFragment(String[] strings, boolean sqlQuery) {
		String closeWithJoiner = _andOperator ? ") AND " : ") OR ";

		StringBundler sb = new StringBundler((strings.length * 3) + 1);

		sb.append("(");

		for (String stringValue : strings) {
			sb.append("(");

			if (stringValue == null) {
				sb.append(sqlQuery ? sqlIsNull : hqlIsNull);
			}
			else if (stringValue.isEmpty()) {
				sb.append(sqlQuery ? sqlNull : hqlNull);
			}
			else {
				sb.append(sqlQuery ? sqlBind : hqlBind);
			}

			sb.append(closeWithJoiner);
		}

		sb.setStringAt("))", sb.index() - 1);

		return sb.toString();
	}

	private Object _normalizeStringArray(String[] array) {
		if (array == null) {
			return new String[0];
		}

		if (!convertNull && caseSensitive) {
			return array;
		}

		if (convertNull) {
			for (int i = 0; i < array.length; i++) {
				array[i] = Objects.toString(array[i], "");
			}
		}

		if (!caseSensitive) {
			for (int i = 0; i < array.length; i++) {
				if (array[i] != null) {
					array[i] = StringUtil.toLowerCase(array[i]);
				}
			}
		}

		if (array.length > 1) {
			return ArrayUtil.sortedUnique(array);
		}

		return array;
	}

	private Object[] _toObjectArray(Object value) {
		if (value == null) {
			return new Object[0];
		}

		if (value instanceof long[] longs) {
			return ArrayUtil.toArray(longs);
		}

		if (value instanceof int[] ints) {
			return ArrayUtil.toArray(ints);
		}

		if (value instanceof short[] shorts) {
			return ArrayUtil.toArray(shorts);
		}

		if (value instanceof boolean[] booleans) {
			return ArrayUtil.toArray(booleans);
		}

		if (value instanceof double[] doubles) {
			return ArrayUtil.toArray(doubles);
		}

		if (value instanceof float[] floats) {
			return ArrayUtil.toArray(floats);
		}

		throw new IllegalStateException(
			"Unsupported arrayable value: " + value);
	}

	private final boolean _andOperator;
	private final String _hqlInPrefix;
	private final String _sqlInPrefix;

}