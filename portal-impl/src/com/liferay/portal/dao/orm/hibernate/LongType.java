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
public class LongType implements Serializable, UserType<Long> {

	public static final Long DEFAULT_VALUE = Long.valueOf(0);

	@Override
	public Long assemble(Serializable cached, Object owner) {
		return (Long)cached;
	}

	@Override
	public Long deepCopy(Long object) {
		return object;
	}

	@Override
	public Serializable disassemble(Long value) {
		return (Serializable)value;
	}

	@Override
	public boolean equals(Long x, Long y) {
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
		return Types.BIGINT;
	}

	@Override
	public int hashCode(Long x) {
		return x.hashCode();
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public Long nullSafeGet(
			ResultSet resultSet, int position, WrapperOptions wrapperOptions)
		throws SQLException {

		Long value = resultSet.getLong(position);

		if (value == null) {
			return DEFAULT_VALUE;
		}

		return value;
	}

	@Override
	public void nullSafeSet(
			PreparedStatement preparedStatement, Long target, int position,
			WrapperOptions wrapperOptions)
		throws SQLException {

		if (target == null) {
			target = DEFAULT_VALUE;
		}

		preparedStatement.setLong(position, target);
	}

	@Override
	public Long replace(Long original, Long target, Object owner) {
		return original;
	}

	@Override
	public Class<Long> returnedClass() {
		return Long.class;
	}

}