/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.spring.hibernate.exception;

import java.sql.SQLException;
import org.hibernate.JDBCException;
import org.springframework.dao.UncategorizedDataAccessException;
import org.springframework.lang.Nullable;

/**
 * @author Tina Tian
 */
public class HibernateJdbcException extends UncategorizedDataAccessException {

	public HibernateJdbcException(JDBCException jdbcException) {
		super("JDBC exception on Hibernate data access: SQLException for SQL [" + jdbcException.getSQL() + "]; SQL state [" + jdbcException.getSQLState() + "]; error code [" + jdbcException.getErrorCode() + "]; " + jdbcException.getMessage(), jdbcException);
	}

	public SQLException getSQLException() {
		return ((JDBCException)this.getCause()).getSQLException();
	}

	@Nullable
	public String getSql() {
		return ((JDBCException)this.getCause()).getSQL();
	}
}
