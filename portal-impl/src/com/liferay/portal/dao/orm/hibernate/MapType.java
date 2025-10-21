/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.orm.hibernate;

import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.io.Serializable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.usertype.UserType;

/**
 * @author Cristina González
 */
public class MapType implements Serializable, UserType<Object> {

	@Override
	public Object assemble(Serializable cached, Object owner) {
		return cached;
	}

	@Override
	public Object deepCopy(Object object) {
		return object;
	}

	@Override
	public Serializable disassemble(Object value) {
		return (Serializable)value;
	}

	@Override
	public boolean equals(Object x, Object y) {
		return Objects.equals(x, y);
	}

	@Override
	public int getSqlType() {
		return Types.VARCHAR;
	}

	@Override
	public int hashCode(Object x) {
		return x.hashCode();
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public Object nullSafeGet(
			ResultSet resultSet, int position, WrapperOptions wrapperOptions)
		throws SQLException {

		String json = resultSet.getString(position);

		try {
			return _jsonFactory.deserialize(json);
		}
		catch (Exception exception) {
			_log.error("Unable to process JSON " + json, exception);

			return Collections.emptyMap();
		}
	}

	@Override
	public void nullSafeSet(
			PreparedStatement preparedStatement, Object target, int position,
			WrapperOptions wrapperOptions)
		throws SQLException {

		String json = _jsonFactory.serialize(target);

		if (json.isEmpty()) {
			json = null;
		}

		if (json == null) {
			preparedStatement.setNull(position, Types.VARCHAR);
		}
		else {
			preparedStatement.setString(position, json);
		}
	}

	@Override
	public Object replace(Object original, Object target, Object owner) {
		return original;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Class<Object> returnedClass() {
		return (Class<Object>)(Class<?>)Map.class;
	}

	private static final Log _log = LogFactoryUtil.getLog(MapType.class);

	private static final JSONFactory _jsonFactory = new JSONFactoryImpl();

}