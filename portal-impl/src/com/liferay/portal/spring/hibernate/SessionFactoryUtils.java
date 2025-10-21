/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.spring.hibernate;

import com.liferay.petra.string.StringBundler;

import org.hibernate.HibernateException;
import org.hibernate.JDBCException;
import org.hibernate.NonUniqueObjectException;
import org.hibernate.NonUniqueResultException;
import org.hibernate.ObjectDeletedException;
import org.hibernate.PersistentObjectException;
import org.hibernate.PessimisticLockException;
import org.hibernate.PropertyValueException;
import org.hibernate.QueryException;
import org.hibernate.QueryTimeoutException;
import org.hibernate.StaleObjectStateException;
import org.hibernate.StaleStateException;
import org.hibernate.TransientObjectException;
import org.hibernate.UnresolvableObjectException;
import org.hibernate.WrongClassException;
import org.hibernate.dialect.lock.OptimisticEntityLockException;
import org.hibernate.dialect.lock.PessimisticEntityLockException;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.DataException;
import org.hibernate.exception.JDBCConnectionException;
import org.hibernate.exception.LockAcquisitionException;
import org.hibernate.exception.SQLGrammarException;

import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.dao.UncategorizedDataAccessException;

/**
 * @author Tina Tian
 */
public class SessionFactoryUtils {

	public static DataAccessException convertHibernateAccessException(
		HibernateException hibernateException) {

		if (hibernateException instanceof JDBCException) {
			JDBCException jdbcException = (JDBCException)hibernateException;

			String message = _getJDBCExceptionMessage(jdbcException);

			if (hibernateException instanceof JDBCConnectionException) {
				return new DataAccessResourceFailureException(
					message, hibernateException);
			}
			else if (hibernateException instanceof SQLGrammarException) {
				return new InvalidDataAccessResourceUsageException(
					message, hibernateException);
			}
			else if (hibernateException instanceof QueryTimeoutException) {
				return new org.springframework.dao.QueryTimeoutException(
					message, hibernateException);
			}
			else if (hibernateException instanceof LockAcquisitionException) {
				return new CannotAcquireLockException(
					message, hibernateException);
			}
			else if (hibernateException instanceof PessimisticLockException) {
				return new PessimisticLockingFailureException(
					message, hibernateException);
			}
			else if (hibernateException instanceof
						ConstraintViolationException) {

				ConstraintViolationException constraintViolationException =
					(ConstraintViolationException)hibernateException;

				return new DataIntegrityViolationException(
					StringBundler.concat(
						hibernateException.getMessage(), "; SQL [",
						constraintViolationException.getSQL(),
						"]; constraint [",
						constraintViolationException.getConstraintName(), "]"),
					hibernateException);
			}
			else if (hibernateException instanceof DataException) {
				return new DataIntegrityViolationException(
					message, hibernateException);
			}
			else if (hibernateException instanceof JDBCException) {
				return new UncategorizedDataAccessException(
					StringBundler.concat(
						"JDBC exception on Hibernate data access: ",
						"SQLException for SQL [", jdbcException.getSQL(),
						"]; SQL state [", jdbcException.getSQLState(),
						"]; error code [", jdbcException.getErrorCode(), "]; ",
						jdbcException.getMessage()),
					jdbcException) {
				};
			}
		}
		else if (hibernateException instanceof QueryException) {
			return new InvalidDataAccessResourceUsageException(
				hibernateException.getMessage(), hibernateException);
		}
		else if (hibernateException instanceof NonUniqueResultException) {
			return new IncorrectResultSizeDataAccessException(
				hibernateException.getMessage(), 1, hibernateException);
		}
		else if (hibernateException instanceof NonUniqueObjectException) {
			return new DuplicateKeyException(
				hibernateException.getMessage(), hibernateException);
		}
		else if (hibernateException instanceof PropertyValueException) {
			return new DataIntegrityViolationException(
				hibernateException.getMessage(), hibernateException);
		}
		else if (hibernateException instanceof PersistentObjectException) {
			return new InvalidDataAccessApiUsageException(
				hibernateException.getMessage(), hibernateException);
		}
		else if (hibernateException instanceof TransientObjectException) {
			return new InvalidDataAccessApiUsageException(
				hibernateException.getMessage(), hibernateException);
		}
		else if (hibernateException instanceof ObjectDeletedException) {
			return new InvalidDataAccessApiUsageException(
				hibernateException.getMessage(), hibernateException);
		}
		else if (hibernateException instanceof UnresolvableObjectException) {
			return new DataRetrievalFailureException(
				hibernateException.getMessage(), hibernateException);
		}
		else if (hibernateException instanceof WrongClassException) {
			return new DataRetrievalFailureException(
				hibernateException.getMessage(), hibernateException);
		}
		else if (hibernateException instanceof StaleObjectStateException) {
			return new OptimisticLockingFailureException(
				hibernateException.getMessage(), hibernateException);
		}
		else if (hibernateException instanceof StaleStateException) {
			return new OptimisticLockingFailureException(
				hibernateException.getMessage(), hibernateException);
		}
		else if (hibernateException instanceof OptimisticEntityLockException) {
			return new OptimisticLockingFailureException(
				hibernateException.getMessage(), hibernateException);
		}
		else if (hibernateException instanceof PessimisticEntityLockException) {
			Throwable throwable = hibernateException.getCause();

			if (throwable instanceof LockAcquisitionException) {
				return new CannotAcquireLockException(
					hibernateException.getMessage(),
					hibernateException.getCause());
			}

			return new PessimisticLockingFailureException(
				hibernateException.getMessage(), hibernateException);
		}

		return new UncategorizedDataAccessException(
			hibernateException.getMessage(), hibernateException) {
		};
	}

	private static String _getJDBCExceptionMessage(
		JDBCException jdbcException) {

		return StringBundler.concat(
			jdbcException.getMessage(), "; SQL [", jdbcException.getSQL(), "]");
	}

}