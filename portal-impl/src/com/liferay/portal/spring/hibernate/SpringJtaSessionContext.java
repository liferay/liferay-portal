package com.liferay.portal.spring.hibernate;

import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.context.internal.JTASessionContext;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class SpringJtaSessionContext extends JTASessionContext {
	public SpringJtaSessionContext(SessionFactoryImplementor factory) {
		super(factory);
	}

	protected Session buildOrObtainSession() {
		Session session = super.buildOrObtainSession();
		if (TransactionSynchronizationManager.isCurrentTransactionReadOnly()) {
			session.setHibernateFlushMode(FlushMode.MANUAL);
		}

		return session;
	}
}
