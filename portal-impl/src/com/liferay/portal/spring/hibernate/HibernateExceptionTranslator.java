/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.spring.hibernate;

import com.liferay.portal.spring.hibernate.exception.JpaObjectRetrievalFailureException;
import com.liferay.portal.spring.hibernate.exception.JpaOptimisticLockingFailureException;
import com.liferay.portal.spring.hibernate.exception.JpaSystemException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.LockTimeoutException;
import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.PessimisticLockException;
import jakarta.persistence.QueryTimeoutException;
import jakarta.persistence.TransactionRequiredException;
import org.hibernate.HibernateException;
import org.hibernate.JDBCException;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.lang.Nullable;

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
				return convertJpaAccessExceptionIfPossible(ex);
			}
		} else {
			return null;
		}
	}

	@Nullable
	public static DataAccessException convertJpaAccessExceptionIfPossible(RuntimeException ex) {
		if (ex instanceof IllegalStateException) {
			return new InvalidDataAccessApiUsageException(ex.getMessage(), ex);
		} else if (ex instanceof IllegalArgumentException) {
			return new InvalidDataAccessApiUsageException(ex.getMessage(), ex);
		} else if (ex instanceof EntityNotFoundException) {
			EntityNotFoundException entityNotFoundException = (EntityNotFoundException)ex;
			return new JpaObjectRetrievalFailureException(entityNotFoundException);
		} else if (ex instanceof NoResultException) {
			return new EmptyResultDataAccessException(ex.getMessage(), 1, ex);
		} else if (ex instanceof NonUniqueResultException) {
			return new IncorrectResultSizeDataAccessException(ex.getMessage(), 1, ex);
		} else if (ex instanceof QueryTimeoutException) {
			return new org.springframework.dao.QueryTimeoutException(ex.getMessage(), ex);
		} else if (ex instanceof LockTimeoutException) {
			return new CannotAcquireLockException(ex.getMessage(), ex);
		} else if (ex instanceof PessimisticLockException) {
			return new PessimisticLockingFailureException(ex.getMessage(), ex);
		} else if (ex instanceof OptimisticLockException) {
			OptimisticLockException optimisticLockException = (OptimisticLockException)ex;
			return new JpaOptimisticLockingFailureException(optimisticLockException);
		} else if (ex instanceof EntityExistsException) {
			return new DataIntegrityViolationException(ex.getMessage(), ex);
		} else if (ex instanceof TransactionRequiredException) {
			return new InvalidDataAccessApiUsageException(ex.getMessage(), ex);
		} else {
			return ex instanceof PersistenceException ? new JpaSystemException(ex) : null;
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
