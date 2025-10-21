/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.spring.hibernate;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import jakarta.persistence.PersistenceException;

import java.util.Objects;

import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.resource.jdbc.spi.LogicalConnectionImplementor;

import org.springframework.dao.DataAccessException;
import org.springframework.lang.Nullable;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * @author Tina Tian
 */
public class SpringSessionSynchronization
	implements TransactionSynchronization {

	public SpringSessionSynchronization(
		SessionHolder sessionHolder, SessionFactory sessionFactory,
		boolean newSession) {

		_sessionHolder = sessionHolder;
		_sessionFactory = sessionFactory;
		_newSession = newSession;

		_holderActive = true;
	}

	@Override
	public void afterCommit() {
	}

	@Override
	public void afterCompletion(int status) {
		try {
			if (status != 0) {
				Session session = _sessionHolder.getSession();

				session.clear();
			}
		}
		finally {
			_sessionHolder.setSynchronizedWithTransaction(false);

			if (_newSession) {
				_closeSession(_sessionHolder.getSession());
			}
		}
	}

	@Override
	public void beforeCommit(boolean readOnly) throws DataAccessException {
		if (!readOnly) {
			Session session = _sessionHolder.getSession();

			if (!Objects.equals(
					FlushMode.MANUAL, session.getHibernateFlushMode())) {

				_flush(session, true);
			}
		}
	}

	@Override
	public void beforeCompletion() {
		try {
			Session session = _sessionHolder.getSession();

			if (_sessionHolder.getPreviousFlushMode() != null) {
				session.setHibernateFlushMode(
					_sessionHolder.getPreviousFlushMode());
			}

			if (session instanceof SessionImplementor sessionImplementor) {
				JdbcCoordinator jdbcCoordinator =
					sessionImplementor.getJdbcCoordinator();

				LogicalConnectionImplementor logicalConnectionImplementor =
					jdbcCoordinator.getLogicalConnection();

				logicalConnectionImplementor.manualDisconnect();
			}
		}
		finally {
			if (_newSession) {
				TransactionSynchronizationManager.unbindResource(
					_sessionFactory);

				_holderActive = false;
			}
		}
	}

	@Override
	public void flush() {
		_flush(_sessionHolder.getSession(), false);
	}

	@Override
	public int getOrder() {
		return 900;
	}

	@Override
	public void resume() {
		if (_holderActive) {
			TransactionSynchronizationManager.bindResource(
				_sessionFactory, _sessionHolder);
		}
	}

	@Override
	public void suspend() {
		if (_holderActive) {
			TransactionSynchronizationManager.unbindResource(_sessionFactory);
		}
	}

	private void _closeSession(@Nullable Session session) {
		if (session != null) {
			try {
				if (session.isOpen()) {
					session.close();
				}
			}
			catch (Throwable throwable) {
				_log.error("Failed to release Hibernate Session", throwable);
			}
		}
	}

	private void _flush(Session session, boolean synch)
		throws DataAccessException {

		if (synch) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Flushing Hibernate Session on transaction " +
						"synchronization");
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
			throw SessionFactoryUtils.convertHibernateAccessException(
				hibernateException);
		}
		catch (PersistenceException persistenceException) {
			Throwable throwable = persistenceException.getCause();

			if (throwable instanceof HibernateException hibernateException) {
				throw SessionFactoryUtils.convertHibernateAccessException(
					hibernateException);
			}

			throw persistenceException;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SpringSessionSynchronization.class);

	private boolean _holderActive;
	private final boolean _newSession;
	private final SessionFactory _sessionFactory;
	private final SessionHolder _sessionHolder;

}