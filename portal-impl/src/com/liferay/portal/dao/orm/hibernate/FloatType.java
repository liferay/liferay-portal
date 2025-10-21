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
public class FloatType implements Serializable, UserType<Float> {

	public static final Float DEFAULT_VALUE = Float.valueOf(0);

	@Override
	public Float assemble(Serializable cached, Object owner) {
		return (Float)cached;
	}

	@Override
	public Float deepCopy(Float object) {
		return object;
	}

	@Override
	public Serializable disassemble(Float value) {
		return value;
	}

	@Override
	public boolean equals(Float x, Float y) {
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
		return Types.FLOAT;
	}

	@Override
	public int hashCode(Float x) {
		return x.hashCode();
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public Float nullSafeGet(
			ResultSet resultSet, int position, WrapperOptions wrapperOptions)
		throws SQLException {

		Float value = resultSet.getFloat(position);

		if (value == null) {
			return DEFAULT_VALUE;
		}

		return value;
	}

	@Override
	public void nullSafeSet(
			PreparedStatement preparedStatement, Float target, int position,
			WrapperOptions wrapperOptions)
		throws SQLException {

		if (target == null) {
			target = DEFAULT_VALUE;
		}

		preparedStatement.setFloat(position, target);
	}

	@Override
	public Float replace(Float original, Float target, Object owner) {
		return original;
	}

	@Override
	public Class<Float> returnedClass() {
		return Float.class;
	}

}