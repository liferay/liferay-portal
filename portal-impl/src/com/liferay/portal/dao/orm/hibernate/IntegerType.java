/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.orm.hibernate;

import java.io.Serializable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.usertype.UserType;

/**
 * @author Brian Wing Shun Chan
 * @author Bruno Farache
 */
public class IntegerType implements Serializable, UserType<Integer> {

	public static final Integer DEFAULT_VALUE = Integer.valueOf(0);

	@Override
	public Integer assemble(Serializable cached, Object owner) {
		return (Integer)cached;
	}

	@Override
	public Integer deepCopy(Integer object) {
		return object;
	}

	@Override
	public Serializable disassemble(Integer value) {
		return value;
	}

	@Override
	public boolean equals(Integer x, Integer y) {
		if (x == y) {
			return true;
		}
		else if ((x == null) || (y == null)) {
			return false;
		}

		return x.equals(y);
	}

	@Override
	public int getSqlType() {
		return Types.INTEGER;
	}

	@Override
	public int hashCode(Integer x) {
		return x.hashCode();
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public Integer nullSafeGet(
			ResultSet resultSet, int position, WrapperOptions wrapperOptions)
		throws SQLException {

		Integer value = resultSet.getInt(position);

		if (value == null) {
			return DEFAULT_VALUE;
		}

		return value;
	}

	@Override
	public void nullSafeSet(
			PreparedStatement preparedStatement, Integer target, int position,
			WrapperOptions wrapperOptions)
		throws SQLException {

		if (target == null) {
			target = DEFAULT_VALUE;
		}

		preparedStatement.setInt(position, target);
	}

	@Override
	public Integer replace(Integer original, Integer target, Object owner) {
		return original;
	}

	@Override
	public Class<Integer> returnedClass() {
		return Integer.class;
	}

}