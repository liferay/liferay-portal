/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.spring.hibernate;

import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionImplementor;

import org.springframework.core.Ordered;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class SpringSessionSynchronization
	implements Ordered, TransactionSynchronization {

	public SpringSessionSynchronization(
		SessionHolder sessionHolder, SessionFactory sessionFactory,
		boolean newSession) {

		this.sessionHolder = sessionHolder;
		this.sessionFactory = sessionFactory;
		this.newSession = newSession;

		this.holderActive = true;
	}

	public void afterCommit() {
	}

	public void afterCompletion(int status) {
		try {
			if (status != 0) {
				this.sessionHolder.getSession(
				).clear();
			}
		}
		finally {
			this.sessionHolder.setSynchronizedWithTransaction(false);

			if (this.newSession) {
				SessionFactoryUtils.closeSession(
					this.sessionHolder.getSession());
			}
		}
	}

	public void beforeCommit(boolean readOnly) throws DataAccessException {
		if (!readOnly) {
			Session session = this.getCurrentSession();

			if (!FlushMode.MANUAL.equals(session.getHibernateFlushMode())) {
				SessionFactoryUtils.flush(this.getCurrentSession(), true);
			}
		}
	}

	public void beforeCompletion() {
		try {
			Session session = this.sessionHolder.getSession();

			if (this.sessionHolder.getPreviousFlushMode() != null) {
				session.setHibernateFlushMode(
					this.sessionHolder.getPreviousFlushMode());
			}

			if (session instanceof SessionImplementor sessionImpl) {
				sessionImpl.getJdbcCoordinator(
				).getLogicalConnection(
				).manualDisconnect();
			}
		}
		finally {
			if (this.newSession) {
				TransactionSynchronizationManager.unbindResource(
					this.sessionFactory);
				this.holderActive = false;
			}
		}
	}

	public void flush() {
		SessionFactoryUtils.flush(this.getCurrentSession(), false);
	}

	public int getOrder() {
		return 900;
	}

	public void resume() {
		if (this.holderActive) {
			TransactionSynchronizationManager.bindResource(
				this.sessionFactory, this.sessionHolder);
		}
	}

	public void suspend() {
		if (this.holderActive) {
			TransactionSynchronizationManager.unbindResource(
				this.sessionFactory);
		}
	}

	private Session getCurrentSession() {
		return this.sessionHolder.getSession();
	}

	private boolean holderActive;
	private final boolean newSession;
	private final SessionFactory sessionFactory;
	private final SessionHolder sessionHolder;

}