/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.spring.hibernate.exception;

import org.hibernate.QueryException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.lang.Nullable;

/**
 * @author Tina Tian
 */
public class HibernateQueryException
	extends InvalidDataAccessResourceUsageException {

	public HibernateQueryException(QueryException ex) {
		super(ex.getMessage(), ex);
	}

	@Nullable
	public String getQueryString() {
		QueryException cause = (QueryException)this.getCause();
		return cause != null ? cause.getQueryString() : null;
	}
}
