/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.orm.hibernate;

import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;

import java.io.Serializable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.usertype.EnhancedUserType;

/**
 * @author Brian Wing Shun Chan
 */
public class BooleanType implements EnhancedUserType, Serializable {

	public static final Boolean DEFAULT_VALUE = Boolean.FALSE;

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
		if (x == y) {
			return true;
		}
		else if ((x == null) || (y == null)) {
			return false;
		}

		return x.equals(y);
	}

	@Override
	public Object fromXMLString(String xmlValue) {
		return Boolean.valueOf(xmlValue);
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
			ResultSet resultSet, String[] names,
			SharedSessionContractImplementor sharedSessionContractImplementor,
			Object owner)
		throws SQLException {

		Boolean value = StandardBasicTypes.BOOLEAN.nullSafeGet(
			resultSet, names[0], sharedSessionContractImplementor);

		if (value == null) {
			return DEFAULT_VALUE;
		}

		return value;
	}

	@Override
	public void nullSafeSet(
			PreparedStatement preparedStatement, Object target, int index,
			SharedSessionContractImplementor sharedSessionContractImplementor)
		throws SQLException {

		if (target == null) {
			target = DEFAULT_VALUE;
		}

		preparedStatement.setBoolean(index, (Boolean)target);
	}

	@Override
	public String objectToSQLString(Object value) {
		DB db = DBManagerUtil.getDB();

		if (Boolean.TRUE.equals(value)) {
			return db.getTemplateTrue();
		}

		return db.getTemplateFalse();
	}

	@Override
	public Object replace(Object original, Object target, Object owner) {
		return original;
	}

	@Override
	public Class<Boolean> returnedClass() {
		return Boolean.class;
	}

	@Override
	public int[] sqlTypes() {
		return new int[] {StandardBasicTypes.BOOLEAN.sqlType()};
	}

	@Override
	public String toXMLString(Object value) {
		return value.toString();
	}

}