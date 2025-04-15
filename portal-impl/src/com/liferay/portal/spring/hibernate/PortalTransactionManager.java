/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.spring.hibernate;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import jakarta.persistence.PersistenceException;

import java.sql.Connection;

import java.util.Map;

import javax.sql.DataSource;

import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.TransactionException;
import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.resource.jdbc.spi.LogicalConnectionImplementor;
import org.hibernate.resource.transaction.spi.TransactionStatus;

import org.springframework.jdbc.datasource.ConnectionHolder;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.orm.hibernate5.SessionFactoryUtils;
import org.springframework.orm.hibernate5.SessionHolder;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;

/**
 * @author Shuyang Zhou
 */
public class PortalTransactionManager
	extends AbstractPlatformTransactionManager {

	public PortalTransactionManager(
		DataSource dataSource, SessionFactory sessionFactory) {

		_dataSource = dataSource;
		_sessionFactory = sessionFactory;
	}

	public SessionFactory getSessionFactory() {
		return _sessionFactory;
	}

	@Override
	protected void doBegin(
		Object transactionObject, TransactionDefinition transactionDefinition) {

		HibernateTransactionObject hibernateTransactionObject =
			(HibernateTransactionObject)transactionObject;

		ConnectionHolder connectionHolder =
			hibernateTransactionObject.getConnectionHolder();

		if ((connectionHolder != null) &&
			connectionHolder.isSynchronizedWithTransaction()) {

			throw new IllegalTransactionStateException(
				"Found prebound JDBC connection");
		}

		SessionImplementor sessionImplementor = null;

		try {
			SessionHolder sessionHolder =
				hibernateTransactionObject.getSessionHolder();

			if ((sessionHolder == null) ||
				sessionHolder.isSynchronizedWithTransaction()) {

				hibernateTransactionObject.setSession(
					_sessionFactory.openSession());

				sessionHolder = hibernateTransactionObject.getSessionHolder();
			}

			Session session = sessionHolder.getSession();

			sessionImplementor = session.unwrap(SessionImplementor.class);

			if ((transactionDefinition.getIsolationLevel() !=
					TransactionDefinition.ISOLATION_DEFAULT) ||
				transactionDefinition.isReadOnly()) {

				Connection connection = sessionImplementor.connection();

				hibernateTransactionObject.markConnectionModified();
				hibernateTransactionObject.setPreviousIsolationLevel(
					DataSourceUtils.prepareConnectionForTransaction(
						connection, transactionDefinition));
				hibernateTransactionObject.setReadOnly(
					transactionDefinition.isReadOnly());
			}

			if (transactionDefinition.isReadOnly()) {
				if (hibernateTransactionObject.isNewSession()) {
					sessionImplementor.setDefaultReadOnly(true);
					sessionImplementor.setHibernateFlushMode(FlushMode.MANUAL);
				}
				else {
					FlushMode flushMode =
						sessionImplementor.getHibernateFlushMode();

					if (FlushMode.MANUAL == flushMode) {
						sessionHolder.setPreviousFlushMode(flushMode);

						sessionImplementor.setHibernateFlushMode(
							FlushMode.AUTO);
					}
				}
			}

			ConnectionHolder newConnectionHolder = new ConnectionHolder(
				sessionImplementor::connection);

			Transaction transaction = null;

			int timeout = determineTimeout(transactionDefinition);

			if (timeout == TransactionDefinition.TIMEOUT_DEFAULT) {
				transaction = sessionImplementor.beginTransaction();
			}
			else {
				transaction = sessionImplementor.getTransaction();

				transaction.setTimeout(timeout);

				transaction.begin();

				newConnectionHolder.setTimeoutInSeconds(timeout);
			}

			sessionHolder.setTransaction(transaction);

			Map<Object, Object> resources =
				SpringHibernateThreadLocalUtil.getResources(true);

			SpringHibernateThreadLocalUtil.setResource(
				_dataSource, newConnectionHolder, resources);

			hibernateTransactionObject.setConnectionHolder(newConnectionHolder);

			if (hibernateTransactionObject.isNewSessionHolder()) {
				SpringHibernateThreadLocalUtil.setResource(
					_sessionFactory, sessionHolder, resources);
			}

			sessionHolder.setSynchronizedWithTransaction(true);
		}
		catch (Throwable throwable1) {
			if (hibernateTransactionObject.isNewSession()) {
				try {
					if (sessionImplementor != null) {
						Transaction transaction =
							sessionImplementor.getTransaction();

						if (transaction.getStatus() ==
								TransactionStatus.ACTIVE) {

							transaction.rollback();
						}
					}
				}
				catch (Throwable throwable2) {
					throwable1.addSuppressed(throwable2);
				}
				finally {
					if ((sessionImplementor != null) &&
						sessionImplementor.isOpen()) {

						sessionImplementor.close();
					}

					hibernateTransactionObject.setSessionHolder(null);
				}
			}

			throw new CannotCreateTransactionException(
				"Unable to open Hibernate session for transaction", throwable1);
		}
	}

	@Override
	protected void doCleanupAfterCompletion(Object transactionObject) {
		HibernateTransactionObject hibernateTransactionObject =
			(HibernateTransactionObject)transactionObject;

		Map<Object, Object> resources =
			SpringHibernateThreadLocalUtil.getResources(false);

		if (hibernateTransactionObject.isNewSessionHolder()) {
			SpringHibernateThreadLocalUtil.setResource(
				_sessionFactory, null, resources);
		}

		SpringHibernateThreadLocalUtil.setResource(
			_dataSource, null, resources);

		SessionHolder sessionHolder =
			hibernateTransactionObject.getSessionHolder();

		Session session = sessionHolder.getSession();

		SessionImplementor sessionImplementor = session.unwrap(
			SessionImplementor.class);

		JdbcCoordinator jdbcCoordinator =
			sessionImplementor.getJdbcCoordinator();

		LogicalConnectionImplementor logicalConnectionImplementor =
			jdbcCoordinator.getLogicalConnection();

		if (hibernateTransactionObject.isConnectionModified() &&
			logicalConnectionImplementor.isPhysicallyConnected()) {

			try {
				Connection connection = sessionImplementor.connection();

				DataSourceUtils.resetConnectionAfterTransaction(
					connection,
					hibernateTransactionObject.getPreviousIsolationLevel(),
					hibernateTransactionObject.isReadOnly());
			}
			catch (HibernateException hibernateException) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"Unable to get JDBC connection from Hibernate session",
						hibernateException);
				}
			}
			catch (Throwable throwable) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"Unable to reset JDBC connection after transaction",
						throwable);
				}
			}
		}

		if (hibernateTransactionObject.isNewSession()) {
			sessionImplementor.close();
		}
		else {
			FlushMode flushMode = sessionHolder.getPreviousFlushMode();

			if (flushMode != null) {
				sessionImplementor.setHibernateFlushMode(flushMode);
			}

			sessionImplementor.disconnect();
		}

		sessionHolder.clear();
	}

	@Override
	protected void doCommit(DefaultTransactionStatus defaultTransactionStatus) {
		HibernateTransactionObject hibernateTransactionObject =
			(HibernateTransactionObject)
				defaultTransactionStatus.getTransaction();

		SessionHolder sessionHolder =
			hibernateTransactionObject.getSessionHolder();

		Transaction transaction = sessionHolder.getTransaction();

		try {
			transaction.commit();
		}
		catch (TransactionException transactionException) {
			throw new TransactionSystemException(
				"Unable to commit Hibernate transaction", transactionException);
		}
		catch (HibernateException hibernateException) {
			throw SessionFactoryUtils.convertHibernateAccessException(
				hibernateException);
		}
		catch (PersistenceException persistenceException) {
			Throwable throwable = persistenceException.getCause();

			if (throwable instanceof HibernateException) {
				throw SessionFactoryUtils.convertHibernateAccessException(
					(HibernateException)throwable);
			}

			throw persistenceException;
		}
	}

	@Override
	protected Object doGetTransaction() {
		HibernateTransactionObject hibernateTransactionObject =
			new HibernateTransactionObject();

		Map<Object, Object> resources =
			SpringHibernateThreadLocalUtil.getResources(false);

		SessionHolder sessionHolder =
			SpringHibernateThreadLocalUtil.getResource(
				_sessionFactory, resources);

		if (sessionHolder != null) {
			LastSessionRecorderUtil.setLastSession(sessionHolder.getSession());

			hibernateTransactionObject.setSessionHolder(sessionHolder);
		}

		hibernateTransactionObject.setConnectionHolder(
			SpringHibernateThreadLocalUtil.getResource(_dataSource, resources));

		return hibernateTransactionObject;
	}

	@Override
	protected void doResume(
		Object transactionObject, Object suspendedResources) {

		SuspendedResourcesHolder suspendedResourcesHolder =
			(SuspendedResourcesHolder)suspendedResources;

		Map<Object, Object> resources =
			SpringHibernateThreadLocalUtil.getResources(true);

		SpringHibernateThreadLocalUtil.setResource(
			_sessionFactory, suspendedResourcesHolder._sessionHolder,
			resources);

		if (suspendedResourcesHolder._connectionHolder != null) {
			SpringHibernateThreadLocalUtil.setResource(
				_dataSource, suspendedResourcesHolder._connectionHolder,
				resources);
		}
	}

	@Override
	protected void doRollback(
		DefaultTransactionStatus defaultTransactionStatus) {

		HibernateTransactionObject hibernateTransactionObject =
			(HibernateTransactionObject)
				defaultTransactionStatus.getTransaction();

		SessionHolder sessionHolder =
			hibernateTransactionObject.getSessionHolder();

		Transaction transaction = sessionHolder.getTransaction();

		try {
			transaction.rollback();
		}
		catch (TransactionException transactionException) {
			throw new TransactionSystemException(
				"Unable to roll back Hibernate transaction",
				transactionException);
		}
		catch (HibernateException hibernateException) {
			throw SessionFactoryUtils.convertHibernateAccessException(
				hibernateException);
		}
		catch (PersistenceException persistenceException) {
			Throwable throwable = persistenceException.getCause();

			if (throwable instanceof HibernateException) {
				throw SessionFactoryUtils.convertHibernateAccessException(
					(HibernateException)throwable);
			}

			throw persistenceException;
		}
		finally {
			if (!hibernateTransactionObject.isNewSession()) {
				Session session = sessionHolder.getSession();

				session.clear();
			}
		}
	}

	@Override
	protected void doSetRollbackOnly(
		DefaultTransactionStatus defaultTransactionStatus) {

		HibernateTransactionObject hibernateTransactionObject =
			(HibernateTransactionObject)
				defaultTransactionStatus.getTransaction();

		SessionHolder sessionHolder =
			hibernateTransactionObject.getSessionHolder();

		sessionHolder.setRollbackOnly();

		ConnectionHolder connectionHolder =
			hibernateTransactionObject.getConnectionHolder();

		if (connectionHolder != null) {
			connectionHolder.setRollbackOnly();
		}
	}

	@Override
	protected Object doSuspend(Object transactionObject) {
		HibernateTransactionObject hibernateTransactionObject =
			(HibernateTransactionObject)transactionObject;

		hibernateTransactionObject.setConnectionHolder(null);
		hibernateTransactionObject.setSessionHolder(null);

		Map<Object, Object> resources =
			SpringHibernateThreadLocalUtil.getResources(false);

		return new SuspendedResourcesHolder(
			SpringHibernateThreadLocalUtil.setResource(
				_dataSource, null, resources),
			SpringHibernateThreadLocalUtil.setResource(
				_sessionFactory, null, resources));
	}

	@Override
	protected boolean isExistingTransaction(Object transactionObject) {
		HibernateTransactionObject hibernateTransactionObject =
			(HibernateTransactionObject)transactionObject;

		SessionHolder sessionHolder =
			hibernateTransactionObject.getSessionHolder();

		if ((sessionHolder != null) &&
			(sessionHolder.getTransaction() != null)) {

			return true;
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortalTransactionManager.class);

	private final DataSource _dataSource;
	private final SessionFactory _sessionFactory;

	private static final class SuspendedResourcesHolder {

		private SuspendedResourcesHolder(
			ConnectionHolder connectionHolder, SessionHolder sessionHolder) {

			_connectionHolder = connectionHolder;
			_sessionHolder = sessionHolder;
		}

		private final ConnectionHolder _connectionHolder;
		private final SessionHolder _sessionHolder;

	}

	private class HibernateTransactionObject {

		public ConnectionHolder getConnectionHolder() {
			return _connectionHolder;
		}

		public Integer getPreviousIsolationLevel() {
			return _previousIsolationLevel;
		}

		public SessionHolder getSessionHolder() {
			return _sessionHolder;
		}

		public boolean isConnectionModified() {
			return _connectionModified;
		}

		public boolean isNewSession() {
			return _newSession;
		}

		public boolean isNewSessionHolder() {
			return _newSessionHolder;
		}

		public boolean isReadOnly() {
			return _readOnly;
		}

		public void markConnectionModified() {
			_connectionModified = true;
		}

		public void setConnectionHolder(ConnectionHolder connectionHolder) {
			_connectionHolder = connectionHolder;
		}

		public void setPreviousIsolationLevel(Integer previousIsolationLevel) {
			_previousIsolationLevel = previousIsolationLevel;
		}

		public void setReadOnly(boolean readOnly) {
			_readOnly = readOnly;
		}

		public void setSession(Session session) {
			_sessionHolder = new SessionHolder(session);
			_newSessionHolder = true;
			_newSession = true;
		}

		public void setSessionHolder(SessionHolder sessionHolder) {
			_sessionHolder = sessionHolder;
			_newSessionHolder = false;
			_newSession = false;
		}

		private ConnectionHolder _connectionHolder;
		private boolean _connectionModified;
		private boolean _newSession;
		private boolean _newSessionHolder;
		private Integer _previousIsolationLevel;
		private boolean _readOnly;
		private SessionHolder _sessionHolder;

	}

}