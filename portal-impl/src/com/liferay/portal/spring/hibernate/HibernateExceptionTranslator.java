/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.spring.hibernate;

import jakarta.persistence.PersistenceException;
import org.hibernate.HibernateException;
import org.hibernate.JDBCException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.lang.Nullable;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;

public class HibernateExceptionTranslator implements PersistenceExceptionTranslator {
	@Nullable
	private SQLExceptionTranslator jdbcExceptionTranslator;

	public HibernateExceptionTranslator() {
	}

	public void setJdbcExceptionTranslator(SQLExceptionTranslator jdbcExceptionTranslator) {
		this.jdbcExceptionTranslator = jdbcExceptionTranslator;
	}

	@Nullable
	public DataAccessException translateExceptionIfPossible(RuntimeException ex) {
		if (ex instanceof HibernateException hibernateEx) {
			return this.convertHibernateAccessException(hibernateEx);
		} else if (ex instanceof PersistenceException) {
			Throwable var3 = ex.getCause();
			if (var3 instanceof HibernateException) {
				HibernateException hibernateEx = (HibernateException)var3;
				return this.convertHibernateAccessException(hibernateEx);
			} else {
				return EntityManagerFactoryUtils.convertJpaAccessExceptionIfPossible(ex);
			}
		} else {
			return null;
		}
	}

	protected DataAccessException convertHibernateAccessException(HibernateException ex) {
		if (this.jdbcExceptionTranslator != null && ex instanceof JDBCException jdbcEx) {
			DataAccessException dae = this.jdbcExceptionTranslator.translate("Hibernate operation: " + jdbcEx.getMessage(), jdbcEx.getSQL(), jdbcEx.getSQLException());
			if (dae != null) {
				return dae;
			}
		}

		return SessionFactoryUtils.convertHibernateAccessException(ex);
	}
}
