/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.spring.hibernate.exception;

import org.hibernate.StaleObjectStateException;
import org.hibernate.StaleStateException;
import org.hibernate.dialect.lock.OptimisticEntityLockException;

/**
 * @author Tina Tian
 */
public class HibernateOptimisticLockingFailureException
	extends ObjectOptimisticLockingFailureException {

	public HibernateOptimisticLockingFailureException(
		OptimisticEntityLockException ex) {

		super(ex.getMessage(), ex);
	}

	public HibernateOptimisticLockingFailureException(
		StaleObjectStateException ex) {

		super(
			ex.getEntityName(),
			HibernateObjectRetrievalFailureException.getIdentifier(ex),
			ex.getMessage(), ex);
	}

	public HibernateOptimisticLockingFailureException(StaleStateException ex) {
		super(ex.getMessage(), ex);
	}

}