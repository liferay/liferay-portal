/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.spring.hibernate;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.spring.hibernate.exception.HibernateJdbcException;
import com.liferay.portal.spring.hibernate.exception.HibernateObjectRetrievalFailureException;
import com.liferay.portal.spring.hibernate.exception.HibernateOptimisticLockingFailureException;
import com.liferay.portal.spring.hibernate.exception.HibernateQueryException;
import com.liferay.portal.spring.hibernate.exception.HibernateSystemException;

import jakarta.persistence.PersistenceException;

import java.lang.reflect.Method;

import java.util.Map;

import javax.sql.DataSource;

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
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StaleObjectStateException;
import org.hibernate.StaleStateException;
import org.hibernate.TransientObjectException;
import org.hibernate.UnresolvableObjectException;
import org.hibernate.WrongClassException;
import org.hibernate.dialect.lock.OptimisticEntityLockException;
import org.hibernate.dialect.lock.PessimisticEntityLockException;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.DataException;
import org.hibernate.exception.JDBCConnectionException;
import org.hibernate.exception.LockAcquisitionException;
import org.hibernate.exception.SQLGrammarException;
import org.hibernate.service.UnknownServiceException;

import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

public class SessionFactoryUtils {

	public static void closeSession(@Nullable Session session) {
		if (session != null) {
			try {
				if (session.isOpen()) {
					session.close();
				}
			}
			catch (Throwable var2) {
				_log.error("Failed to release Hibernate Session", var2);
			}
		}
	}

	public static DataAccessException convertHibernateAccessException(
		HibernateException ex) {

		if (ex instanceof JDBCConnectionException) {
			return new DataAccessResourceFailureException(ex.getMessage(), ex);
		}
		else if (ex instanceof SQLGrammarException) {
			SQLGrammarException hibJdbcEx = (SQLGrammarException)ex;

			return new InvalidDataAccessResourceUsageException(
				ex.getMessage() + "; SQL [" + hibJdbcEx.getSQL() + "]", ex);
		}
		else if (ex instanceof QueryTimeoutException) {
			QueryTimeoutException hibJdbcEx = (QueryTimeoutException)ex;

			return new org.springframework.dao.QueryTimeoutException(
				ex.getMessage() + "; SQL [" + hibJdbcEx.getSQL() + "]", ex);
		}
		else if (ex instanceof LockAcquisitionException) {
			LockAcquisitionException hibJdbcEx = (LockAcquisitionException)ex;

			return new CannotAcquireLockException(
				ex.getMessage() + "; SQL [" + hibJdbcEx.getSQL() + "]", ex);
		}
		else if (ex instanceof PessimisticLockException) {
			PessimisticLockException hibJdbcEx = (PessimisticLockException)ex;

			return new PessimisticLockingFailureException(
				ex.getMessage() + "; SQL [" + hibJdbcEx.getSQL() + "]", ex);
		}
		else if (ex instanceof ConstraintViolationException) {
			ConstraintViolationException hibJdbcEx =
				(ConstraintViolationException)ex;
			String var10002 = ex.getMessage();

			return new DataIntegrityViolationException(
				var10002 + "; SQL [" + hibJdbcEx.getSQL() + "]; constraint [" +
					hibJdbcEx.getConstraintName() + "]",
				ex);
		}
		else if (ex instanceof DataException) {
			DataException hibJdbcEx = (DataException)ex;

			return new DataIntegrityViolationException(
				ex.getMessage() + "; SQL [" + hibJdbcEx.getSQL() + "]", ex);
		}
		else if (ex instanceof JDBCException) {
			JDBCException hibJdbcEx = (JDBCException)ex;

			return new HibernateJdbcException(hibJdbcEx);
		}
		else if (ex instanceof QueryException) {
			QueryException queryException = (QueryException)ex;

			return new HibernateQueryException(queryException);
		}
		else if (ex instanceof NonUniqueResultException) {
			return new IncorrectResultSizeDataAccessException(
				ex.getMessage(), 1, ex);
		}
		else if (ex instanceof NonUniqueObjectException) {
			return new DuplicateKeyException(ex.getMessage(), ex);
		}
		else if (ex instanceof PropertyValueException) {
			return new DataIntegrityViolationException(ex.getMessage(), ex);
		}
		else if (ex instanceof PersistentObjectException) {
			return new InvalidDataAccessApiUsageException(ex.getMessage(), ex);
		}
		else if (ex instanceof TransientObjectException) {
			return new InvalidDataAccessApiUsageException(ex.getMessage(), ex);
		}
		else if (ex instanceof ObjectDeletedException) {
			return new InvalidDataAccessApiUsageException(ex.getMessage(), ex);
		}
		else if (ex instanceof UnresolvableObjectException) {
			UnresolvableObjectException unresolvableObjectException =
				(UnresolvableObjectException)ex;

			return new HibernateObjectRetrievalFailureException(
				unresolvableObjectException);
		}
		else if (ex instanceof WrongClassException) {
			WrongClassException wrongClassException = (WrongClassException)ex;

			return new HibernateObjectRetrievalFailureException(
				wrongClassException);
		}
		else if (ex instanceof StaleObjectStateException) {
			StaleObjectStateException staleObjectStateException =
				(StaleObjectStateException)ex;

			return new HibernateOptimisticLockingFailureException(
				staleObjectStateException);
		}
		else if (ex instanceof StaleStateException) {
			StaleStateException staleStateException = (StaleStateException)ex;

			return new HibernateOptimisticLockingFailureException(
				staleStateException);
		}
		else if (ex instanceof OptimisticEntityLockException) {
			OptimisticEntityLockException optimisticEntityLockException =
				(OptimisticEntityLockException)ex;

			return new HibernateOptimisticLockingFailureException(
				optimisticEntityLockException);
		}
		else if (ex instanceof PessimisticEntityLockException) {
			return (DataAccessException)
				(ex.getCause() instanceof LockAcquisitionException ?
					new CannotAcquireLockException(
						ex.getMessage(), ex.getCause()) :
							new PessimisticLockingFailureException(
								ex.getMessage(), ex));
		}

		return new HibernateSystemException(ex);
	}

	public static void flush(Session session, boolean synch)
		throws DataAccessException {

		if (synch) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Flushing Hibernate Session on transaction synchronization");
			}
		}
		else {
			if (_log.isDebugEnabled()) {
				_log.debug("Flushing Hibernate Session on explicit request");
			}
		}

		try {
			session.flush();
		}
		catch (HibernateException hibernateException) {
			throw convertHibernateAccessException(hibernateException);
		}
		catch (PersistenceException persistenceException) {
			Throwable throwable = persistenceException.getCause();

			if (throwable instanceof HibernateException hibernateException) {
				throw convertHibernateAccessException(hibernateException);
			}

			throw persistenceException;
		}
	}

	@Nullable
	public static DataSource getDataSource(SessionFactory sessionFactory) {
		Method getProperties = ClassUtils.getMethodIfAvailable(
			sessionFactory.getClass(), "getProperties", new Class<?>[0]);

		if (getProperties != null) {
			Map<?, ?> props = (Map)ReflectionUtils.invokeMethod(
				getProperties, sessionFactory);

			if (props != null) {
				Object dataSourceValue = props.get(
					"hibernate.connection.datasource");

				if (dataSourceValue instanceof DataSource) {
					DataSource dataSource = (DataSource)dataSourceValue;

					return dataSource;
				}
			}
		}

		if (sessionFactory instanceof SessionFactoryImplementor sfi) {
			try {
				ConnectionProvider cp =
					(ConnectionProvider)sfi.getServiceRegistry(
					).getService(
						ConnectionProvider.class
					);

				if (cp != null) {
					return (DataSource)cp.unwrap(DataSource.class);
				}
			}
			catch (UnknownServiceException var5) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"No ConnectionProvider found - cannot determine DataSource for SessionFactory: " +
							String.valueOf(var5));
				}
			}
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SessionFactoryUtils.class);

}