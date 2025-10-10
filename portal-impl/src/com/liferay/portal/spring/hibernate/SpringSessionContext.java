package com.liferay.portal.spring.hibernate;

import jakarta.transaction.SystemException;
import jakarta.transaction.TransactionManager;
import org.apache.commons.logging.LogFactory;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.context.spi.CurrentSessionContext;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform;
import org.springframework.lang.Nullable;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class SpringSessionContext implements CurrentSessionContext {
	private final SessionFactoryImplementor sessionFactory;
	@Nullable
	private TransactionManager transactionManager;
	@Nullable
	private CurrentSessionContext jtaSessionContext;

	public SpringSessionContext(SessionFactoryImplementor sessionFactory) {
		this.sessionFactory = sessionFactory;

		try {
			JtaPlatform jtaPlatform = (JtaPlatform)sessionFactory.getServiceRegistry().getService(JtaPlatform.class);
			this.transactionManager = jtaPlatform.retrieveTransactionManager();
			if (this.transactionManager != null) {
				this.jtaSessionContext = new SpringJtaSessionContext(sessionFactory);
			}
		} catch (Exception var3) {
			LogFactory.getLog(
				SpringSessionContext.class).warn("Could not introspect Hibernate JtaPlatform for SpringJtaSessionContext", var3);
		}

	}

	public Session currentSession() throws HibernateException {
		Object value = TransactionSynchronizationManager.getResource(this.sessionFactory);
		if (value instanceof Session session) {
			return session;
		} else if (value instanceof SessionHolder sessionHolder) {
			Session session = sessionHolder.getSession();
			if (!sessionHolder.isSynchronizedWithTransaction() && TransactionSynchronizationManager.isSynchronizationActive()) {
				TransactionSynchronizationManager.registerSynchronization(new SpringSessionSynchronization(sessionHolder, this.sessionFactory, false));
				sessionHolder.setSynchronizedWithTransaction(true);
				FlushMode flushMode = session.getHibernateFlushMode();
				if (flushMode.equals(FlushMode.MANUAL) && !TransactionSynchronizationManager.isCurrentTransactionReadOnly()) {
					session.setHibernateFlushMode(FlushMode.AUTO);
					sessionHolder.setPreviousFlushMode(flushMode);
				}
			}

			return session;
		} else if (value instanceof EntityManagerHolder entityManagerHolder) {
			return (Session)entityManagerHolder.getEntityManager().unwrap(Session.class);
		} else {
			if (this.transactionManager != null && this.jtaSessionContext != null) {
				try {
					if (this.transactionManager.getStatus() == 0) {
						Session session = this.jtaSessionContext.currentSession();
						if (TransactionSynchronizationManager.isSynchronizationActive()) {
							TransactionSynchronizationManager.registerSynchronization(new SpringFlushSynchronization(session));
						}

						return session;
					}
				} catch (SystemException var7) {
					throw new HibernateException("JTA TransactionManager found but status check failed", var7);
				}
			}

			if (TransactionSynchronizationManager.isSynchronizationActive()) {
				Session session = this.sessionFactory.openSession();
				if (TransactionSynchronizationManager.isCurrentTransactionReadOnly()) {
					session.setHibernateFlushMode(FlushMode.MANUAL);
				}

				SessionHolder sessionHolder = new SessionHolder(session);
				TransactionSynchronizationManager.registerSynchronization(new SpringSessionSynchronization(sessionHolder, this.sessionFactory, true));
				TransactionSynchronizationManager.bindResource(this.sessionFactory, sessionHolder);
				sessionHolder.setSynchronizedWithTransaction(true);
				return session;
			} else {
				throw new HibernateException("Could not obtain transaction-synchronized Session for current thread");
			}
		}
	}
}

