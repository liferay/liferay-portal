package com.liferay.portal.spring.hibernate;

import org.hibernate.Session;
import org.springframework.lang.Nullable;
import org.springframework.transaction.support.TransactionSynchronization;

public class SpringFlushSynchronization implements TransactionSynchronization {
	private final Session session;

	public SpringFlushSynchronization(Session session) {
		this.session = session;
	}

	public void flush() {
		SessionFactoryUtils.flush(this.session, false);
	}

	public boolean equals(@Nullable Object other) {
		boolean var10000;
		if (this != other) {
			label26: {
				if (other instanceof SpringFlushSynchronization) {
					SpringFlushSynchronization
						that = (SpringFlushSynchronization)other;
					if (this.session == that.session) {
						break label26;
					}
				}

				var10000 = false;
				return var10000;
			}
		}

		var10000 = true;
		return var10000;
	}

	public int hashCode() {
		return this.session.hashCode();
	}
}

