/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.spring.hibernate;

import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.Transaction;

import org.springframework.transaction.support.ResourceHolderSupport;

/**
 * @author Tina Tian
 */
public class SessionHolder extends ResourceHolderSupport {

	public SessionHolder(Session session) {
		_session = session;
	}

	public void clear() {
		super.clear();

		_transaction = null;
		_previousFlushMode = null;
	}

	public FlushMode getPreviousFlushMode() {
		return _previousFlushMode;
	}

	public Session getSession() {
		return _session;
	}

	public Transaction getTransaction() {
		return _transaction;
	}

	public void setPreviousFlushMode(FlushMode previousFlushMode) {
		_previousFlushMode = previousFlushMode;
	}

	public void setTransaction(Transaction transaction) {
		_transaction = transaction;
	}

	private FlushMode _previousFlushMode;
	private final Session _session;
	private Transaction _transaction;

}