/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.spring.hibernate.exception;

import org.hibernate.HibernateException;

import org.springframework.dao.UncategorizedDataAccessException;
import org.springframework.lang.Nullable;

/**
 * @author Tina Tian
 */
public class HibernateSystemException extends UncategorizedDataAccessException {

	public HibernateSystemException(@Nullable HibernateException cause) {
		super(cause != null ? cause.getMessage() : null, cause);
	}

}