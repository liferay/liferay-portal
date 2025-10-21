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
 */
public class ShortType implements Serializable, UserType<Short> {

	public static final Short DEFAULT_VALUE = Short.valueOf((short)0);

	@Override
	public Short assemble(Serializable cached, Object owner) {
		return (Short)cached;
	}

	@Override
	public Short deepCopy(Short object) {
		return object;
	}

	@Override
	public Serializable disassemble(Short value) {
		return value;
	}

	@Override
	public boolean equals(Short x, Short y) {
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
		return Types.SMALLINT;
	}

	@Override
	public int hashCode(Short x) {
		return x.hashCode();
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public Short nullSafeGet(
			ResultSet resultSet, int position, WrapperOptions wrapperOptions)
		throws SQLException {

		Short value = resultSet.getShort(position);

		if (value == null) {
			return DEFAULT_VALUE;
		}

		return value;
	}

	@Override
	public void nullSafeSet(
			PreparedStatement preparedStatement, Short target, int position,
			WrapperOptions wrapperOptions)
		throws SQLException {

		if (target == null) {
			target = DEFAULT_VALUE;
		}

		preparedStatement.setShort(position, target);
	}

	@Override
	public Short replace(Short original, Short target, Object owner) {
		return original;
	}

	@Override
	public Class<Short> returnedClass() {
		return Short.class;
	}

}