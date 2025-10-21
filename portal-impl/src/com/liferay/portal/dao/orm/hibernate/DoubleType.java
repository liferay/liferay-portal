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
public class DoubleType implements Serializable, UserType<Double> {

	public static final Double DEFAULT_VALUE = Double.valueOf(0);

	@Override
	public Double assemble(Serializable cached, Object owner) {
		return (Double)cached;
	}

	@Override
	public Double deepCopy(Double object) {
		return object;
	}

	@Override
	public Serializable disassemble(Double value) {
		return value;
	}

	@Override
	public boolean equals(Double x, Double y) {
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
		return Types.DOUBLE;
	}

	@Override
	public int hashCode(Double x) {
		return x.hashCode();
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public Double nullSafeGet(
			ResultSet resultSet, int position, WrapperOptions wrapperOptions)
		throws SQLException {

		Double value = resultSet.getDouble(position);

		if (value == null) {
			return DEFAULT_VALUE;
		}

		return value;
	}

	@Override
	public void nullSafeSet(
			PreparedStatement preparedStatement, Double target, int position,
			WrapperOptions wrapperOptions)
		throws SQLException {

		if (target == null) {
			target = DEFAULT_VALUE;
		}

		preparedStatement.setDouble(position, target);
	}

	@Override
	public Double replace(Double original, Double target, Object owner) {
		return original;
	}

	@Override
	public Class<Double> returnedClass() {
		return Double.class;
	}

}