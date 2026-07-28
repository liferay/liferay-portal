/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.orm.hibernate;

import java.io.Serializable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Date;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.usertype.UserType;

/**
 * @author Tina Tian
 */
public class TimestampType implements Serializable, UserType {

	@Override
	public Object assemble(Serializable cached, Object owner) {
		return deepCopy(cached);
	}

	@Override
	public Object deepCopy(Object object) {
		if (object == null) {
			return null;
		}

		Date date = (Date)object;

		return new Date(date.getTime());
	}

	@Override
	public Serializable disassemble(Object value) {
		return (Serializable)deepCopy(value);
	}

	@Override
	public boolean equals(Object x, Object y) {
		if (x == y) {
			return true;
		}
		else if ((x == null) || (y == null)) {
			return false;
		}

		Date dateX = (Date)x;
		Date dateY = (Date)y;

		if (dateX.getTime() == dateY.getTime()) {
			return true;
		}

		return false;
	}

	@Override
	public int hashCode(Object x) {
		return x.hashCode();
	}

	@Override
	public boolean isMutable() {
		return true;
	}

	@Override
	public Object nullSafeGet(
			ResultSet resultSet, String[] names,
			SharedSessionContractImplementor sharedSessionContractImplementor,
			Object owner)
		throws SQLException {

		Object value = StandardBasicTypes.TIMESTAMP.nullSafeGet(
			resultSet, names[0], sharedSessionContractImplementor);

		return deepCopy(value);
	}

	@Override
	public void nullSafeSet(
			PreparedStatement preparedStatement, Object target, int index,
			SharedSessionContractImplementor sharedSessionContractImplementor)
		throws SQLException {

		StandardBasicTypes.TIMESTAMP.nullSafeSet(
			preparedStatement, target, index, sharedSessionContractImplementor);
	}

	@Override
	public Object replace(Object original, Object target, Object owner) {
		return deepCopy(original);
	}

	@Override
	public Class<Date> returnedClass() {
		return Date.class;
	}

	@Override
	public int[] sqlTypes() {
		return new int[] {StandardBasicTypes.TIMESTAMP.sqlType()};
	}

}