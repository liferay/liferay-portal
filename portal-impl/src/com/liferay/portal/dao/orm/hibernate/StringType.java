/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.orm.hibernate;

import com.liferay.petra.string.StringPool;

import java.io.Serializable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import java.util.Objects;

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.usertype.UserType;

/**
 * @author Brian Wing Shun Chan
 */
public class StringType implements Serializable, UserType<String> {

	@Override
	public String assemble(Serializable cached, Object owner) {
		return (String)cached;
	}

	@Override
	public String deepCopy(String object) {
		return object;
	}

	@Override
	public Serializable disassemble(String value) {
		return value;
	}

	@Override
	public boolean equals(String x, String y) {
		if (Objects.equals(x, y)) {
			return true;
		}
		else if (((x == null) || x.equals(StringPool.BLANK)) &&
				 ((y == null) || y.equals(StringPool.BLANK))) {

			return true;
		}

		return false;
	}

	@Override
	public int getSqlType() {
		return Types.VARCHAR;
	}

	@Override
	public int hashCode(String x) {
		return x.hashCode();
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public String nullSafeGet(
			ResultSet resultSet, int position, WrapperOptions wrapperOptions)
		throws SQLException {

		return resultSet.getString(position);
	}

	@Override
	public void nullSafeSet(
			PreparedStatement preparedStatement, String target, int position,
			WrapperOptions wrapperOptions)
		throws SQLException {

		if (target.isEmpty()) {
			target = null;
		}

		if (target == null) {
			preparedStatement.setNull(position, Types.VARCHAR);
		}
		else {
			preparedStatement.setString(position, target);
		}
	}

	@Override
	public String replace(String original, String target, Object owner) {
		return original;
	}

	@Override
	public Class<String> returnedClass() {
		return String.class;
	}

}