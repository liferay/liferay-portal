/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.spring.hibernate;

import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.Transaction;

import org.springframework.lang.Nullable;
import org.springframework.orm.jpa.EntityManagerHolder;

/**
 * @author Tina Tian
 */
public class SessionHolder extends EntityManagerHolder {

	public SessionHolder(Session session) {
		super(session);
	}

	public void clear() {
		super.clear();

		_transaction = null;
		_previousFlushMode = null;
	}

	@Nullable
	public FlushMode getPreviousFlushMode() {
		return _previousFlushMode;
	}

	public Session getSession() {
		return (Session)getEntityManager();
	}

	@Nullable
	public Transaction getTransaction() {
		return _transaction;
	}

	public void setPreviousFlushMode(@Nullable FlushMode previousFlushMode) {
		_previousFlushMode = previousFlushMode;
	}

	public void setTransaction(@Nullable Transaction transaction) {
		_transaction = transaction;

		setTransactionActive(transaction != null);
	}

	@Nullable
	private FlushMode _previousFlushMode;

	@Nullable
	private Transaction _transaction;

}