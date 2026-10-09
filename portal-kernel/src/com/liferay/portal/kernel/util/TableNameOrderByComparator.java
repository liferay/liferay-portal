/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;

/**
 * @author José Manuel Navarro
 */
public class TableNameOrderByComparator<T> extends OrderByComparator<T> {

	public TableNameOrderByComparator(
		OrderByComparator<T> orderByComparator, String tableName) {

		_orderByComparator = orderByComparator;

		setTableName(tableName);
	}

	@Override
	public int compare(T obj1, T obj2) {
		return _orderByComparator.compare(obj1, obj2);
	}

	@Override
	public String getOrderBy() {
		String orderBy = _orderByComparator.getOrderBy();

		if (_tableName == null) {
			return orderBy;
		}

		String[] columnNames = StringUtil.split(orderBy);

		StringBundler sb = new StringBundler((3 * columnNames.length) - 1);

		for (int i = 0; i < columnNames.length; ++i) {
			String columnName = columnNames[i];

			if (columnName.indexOf(CharPool.PERIOD) != -1) {
				columnName = StringUtil.split(columnName, CharPool.PERIOD)[1];
			}

			sb.append(_tableName);
			sb.append(StringUtil.trim(columnName));

			if (i >= (columnNames.length - 1)) {
				continue;
			}

			sb.append(StringPool.COMMA_AND_SPACE);
		}

		return sb.toString();
	}

	@Override
	public String[] getOrderByConditionFields() {
		return _orderByComparator.getOrderByConditionFields();
	}

	@Override
	public Object[] getOrderByConditionValues(Object object) {
		return _orderByComparator.getOrderByConditionValues(object);
	}

	@Override
	public String[] getOrderByFields() {
		return _orderByComparator.getOrderByFields();
	}

	public OrderByComparator<T> getWrappedOrderByComparator() {
		return _orderByComparator;
	}

	@Override
	public boolean isAscending() {
		return _orderByComparator.isAscending();
	}

	@Override
	public boolean isAscending(String field) {
		return _orderByComparator.isAscending(field);
	}

	public void setTableName(String tableName) {
		if (Validator.isNotNull(tableName)) {
			if (tableName.endsWith(StringPool.PERIOD)) {
				_tableName = tableName;
			}
			else {
				_tableName = tableName + CharPool.PERIOD;
			}
		}
		else {
			_tableName = null;
		}
	}

	@Override
	public String toString() {
		return _orderByComparator.toString();
	}

	private final OrderByComparator<T> _orderByComparator;
	private String _tableName;

}