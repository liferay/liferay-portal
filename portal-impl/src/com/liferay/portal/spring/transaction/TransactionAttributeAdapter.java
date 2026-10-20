/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.spring.transaction;

import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;

import java.util.Collection;

import org.springframework.transaction.interceptor.TransactionAttribute;

/**
 * @author Shuyang Zhou
 */
public class TransactionAttributeAdapter
	implements com.liferay.portal.kernel.transaction.TransactionAttribute,
			   TransactionAttribute {

	public TransactionAttributeAdapter(
		TransactionAttribute transactionAttribute) {

		this(transactionAttribute, false);
	}

	public TransactionAttributeAdapter(
		TransactionAttribute transactionAttribute, boolean strictReadOnly) {

		_transactionAttribute = transactionAttribute;
		_strictReadOnly = strictReadOnly;

		_isolation = Isolation.getIsolation(
			transactionAttribute.getIsolationLevel());
		_propagation = Propagation.getPropagation(
			transactionAttribute.getPropagationBehavior());
	}

	@Override
	public Isolation getIsolation() {
		return _isolation;
	}

	@Override
	public int getIsolationLevel() {
		return _transactionAttribute.getIsolationLevel();
	}

	@Override
	public Collection<String> getLabels() {
		return _transactionAttribute.getLabels();
	}

	@Override
	public String getName() {
		return _transactionAttribute.getName();
	}

	@Override
	public Propagation getPropagation() {
		return _propagation;
	}

	@Override
	public int getPropagationBehavior() {
		return _transactionAttribute.getPropagationBehavior();
	}

	@Override
	public String getQualifier() {
		return _transactionAttribute.getQualifier();
	}

	@Override
	public int getTimeout() {
		return _transactionAttribute.getTimeout();
	}

	@Override
	public boolean isReadOnly() {
		return _transactionAttribute.isReadOnly();
	}

	@Override
	public boolean isStrictReadOnly() {
		return _strictReadOnly;
	}

	@Override
	public boolean rollbackOn(Throwable throwable) {
		return _transactionAttribute.rollbackOn(throwable);
	}

	private final Isolation _isolation;
	private final Propagation _propagation;
	private final boolean _strictReadOnly;
	private final TransactionAttribute _transactionAttribute;

}