/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.orm.hibernate;

import com.liferay.petra.string.StringPool;

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import java.util.Objects;

import org.hibernate.HibernateException;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.usertype.UserType;

/**
 * @author Shuyang Zhou
 */
public class StringClobType implements Serializable, UserType<String> {

	@Override
	public String assemble(Serializable cached, Object owner)
		throws HibernateException {

		return (String)cached;
	}

	@Override
	public String deepCopy(String value) throws HibernateException {
		return value;
	}

	@Override
	public Serializable disassemble(String value) throws HibernateException {
		return value;
	}

	@Override
	public boolean equals(String object1, String object2) {
		if (Objects.equals(object1, object2)) {
			return true;
		}
		else if (((object1 == null) || object1.equals(StringPool.BLANK)) &&
				 ((object2 == null) || object2.equals(StringPool.BLANK))) {

			return true;
		}

		return false;
	}

	@Override
	public int getSqlType() {
		return Types.BLOB;
	}

	@Override
	public int hashCode(String object) throws HibernateException {
		return object.hashCode();
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public String nullSafeGet(
			ResultSet resultSet, int position, WrapperOptions wrapperOptions)
		throws SQLException {

		Reader reader = resultSet.getCharacterStream(position);

		if (reader == null) {
			return null;
		}

		StringBuilder stringBuilder = new StringBuilder(4096);

		try {
			char[] chars = new char[4096];

			for (int i = reader.read(chars); i > 0; i = reader.read(chars)) {
				stringBuilder.append(chars, 0, i);
			}
		}
		catch (IOException ioException) {
			throw new SQLException(ioException.getMessage());
		}

		return stringBuilder.toString();
	}

	@Override
	public void nullSafeSet(
			PreparedStatement preparedStatement, String target, int position,
			WrapperOptions wrapperOptions)
		throws SQLException {

		if (target != null) {
			StringReader stringReader = new StringReader(target);

			preparedStatement.setCharacterStream(
				position, stringReader, target.length());
		}
		else {
			preparedStatement.setNull(position, Types.BLOB);
		}
	}

	@Override
	public String replace(String original, String target, Object owner)
		throws HibernateException {

		return original;
	}

	@Override
	public Class<String> returnedClass() {
		return String.class;
	}

}