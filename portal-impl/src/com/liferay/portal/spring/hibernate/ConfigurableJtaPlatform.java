package com.liferay.portal.spring.hibernate;

import jakarta.transaction.Synchronization;
import jakarta.transaction.SystemException;
import jakarta.transaction.Transaction;
import jakarta.transaction.TransactionManager;
import jakarta.transaction.TransactionSynchronizationRegistry;
import jakarta.transaction.UserTransaction;
import org.hibernate.TransactionException;
import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform;
import org.springframework.lang.Nullable;
import org.springframework.transaction.jta.UserTransactionAdapter;
import org.springframework.util.Assert;

class ConfigurableJtaPlatform implements JtaPlatform {
	private final TransactionManager transactionManager;
	private final UserTransaction userTransaction;
	@Nullable
	private final TransactionSynchronizationRegistry transactionSynchronizationRegistry;

	public ConfigurableJtaPlatform(TransactionManager tm, @Nullable UserTransaction ut, @Nullable TransactionSynchronizationRegistry tsr) {
		Assert.notNull(tm, "TransactionManager reference must not be null");
		this.transactionManager = tm;
		this.userTransaction = (UserTransaction)(ut != null ? ut : new UserTransactionAdapter(tm));
		this.transactionSynchronizationRegistry = tsr;
	}

	public TransactionManager retrieveTransactionManager() {
		return this.transactionManager;
	}

	public UserTransaction retrieveUserTransaction() {
		return this.userTransaction;
	}

	public Object getTransactionIdentifier(Transaction transaction) {
		return transaction;
	}

	public boolean canRegisterSynchronization() {
		try {
			return this.transactionManager.getStatus() == 0;
		} catch (SystemException var2) {
			throw new TransactionException("Could not determine JTA transaction status", var2);
		}
	}

	public void registerSynchronization(Synchronization synchronization) {
		if (this.transactionSynchronizationRegistry != null) {
			this.transactionSynchronizationRegistry.registerInterposedSynchronization(synchronization);
		} else {
			try {
				this.transactionManager.getTransaction().registerSynchronization(synchronization);
			} catch (Exception var3) {
				throw new TransactionException("Could not access JTA Transaction to register synchronization", var3);
			}
		}

	}

	public int getCurrentStatus() throws SystemException {
		return this.transactionManager.getStatus();
	}
}

