/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.testray.rest.internal.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author José Abelenda
 */
public class TestrayUtil {

	public static List<Map<String, Object>> executeQuery(
			String sql, List<Object> params)
		throws SQLException {

		List<Map<String, Object>> list = new ArrayList<>();

		try (Connection connection = DataAccess.getConnection()) {
			PreparedStatement preparedStatement = connection.prepareStatement(
				sql);

			if (ListUtil.isNotEmpty(params)) {
				for (int i = 0; i < params.size(); i++) {
					preparedStatement.setObject(i + 1, params.get(i));
				}
			}

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

				List<String> columnLabels = new ArrayList<>();

				for (int j = 1; j <= resultSetMetaData.getColumnCount(); j++) {
					columnLabels.add(resultSetMetaData.getColumnLabel(j));
				}

				while (resultSet.next()) {
					Map<String, Object> map = new HashMap<>();

					for (String columnLabel : columnLabels) {
						Object object = resultSet.getObject(columnLabel);

						map.put(columnLabel, object);
					}

					list.add(map);
				}
			}
		}

		return list;
	}

	public static int executeUpdate(String sql, List<Object> params)
		throws SQLException {

		try (Connection connection = DataAccess.getConnection()) {
			PreparedStatement preparedStatement = connection.prepareStatement(
				sql);

			if (ListUtil.isNotEmpty(params)) {
				for (int i = 0; i < params.size(); i++) {
					preparedStatement.setObject(i + 1, params.get(i));
				}
			}

			return preparedStatement.executeUpdate();
		}
	}

	public static long getTotalCount(String sql, List<Object> params)
		throws SQLException {

		StringBundler sb = new StringBundler(3);

		sb.append("select count(*) as count from ( ");
		sb.append(sql);
		sb.append(") count");

		List<Map<String, Object>> values = executeQuery(sb.toString(), params);

		return GetterUtil.getLong(
			values.get(
				0
			).get(
				"count"
			));
	}

	public static String interpolateParams(
		List<Object> params, Object[] values) {

		StringBundler sb = new StringBundler();

		for (Object value : values) {
			sb.append("? ");
			sb.append(", ");

			if (StringUtil.equals(
					GetterUtil.getString(value),
					StringUtil.extractDigits(GetterUtil.getString(value)))) {

				params.add(GetterUtil.getLong(value));

				continue;
			}

			params.add(GetterUtil.getString(value));
		}

		sb.setIndex(sb.index() - 1);

		return sb.toString();
	}

	public static String interpolateParams(List<Object> params, String values) {
		Object[] valuesObjectArray = null;

		if (Validator.isNotNull(StringUtil.extractDigits(values))) {
			valuesObjectArray = ArrayUtil.toLongArray(
				StringUtil.split(values, ",", 0L));
		}
		else {
			valuesObjectArray = StringUtil.split(values);
		}

		return interpolateParams(params, valuesObjectArray);
	}

}