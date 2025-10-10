/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.spring.hibernate.exception;

import org.hibernate.HibernateException;
import org.hibernate.UnresolvableObjectException;
import org.hibernate.WrongClassException;

import org.springframework.lang.Nullable;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.util.ReflectionUtils;

/**
 * @author Tina Tian
 */
public class HibernateObjectRetrievalFailureException
	extends ObjectRetrievalFailureException {

	public HibernateObjectRetrievalFailureException(
		UnresolvableObjectException ex) {

		super(ex.getEntityName(), getIdentifier(ex), ex.getMessage(), ex);
	}

	public HibernateObjectRetrievalFailureException(WrongClassException ex) {
		super(ex.getEntityName(), getIdentifier(ex), ex.getMessage(), ex);
	}

	@Nullable
	static Object getIdentifier(HibernateException hibEx) {
		try {
			return ReflectionUtils.invokeMethod(
				hibEx.getClass(
				).getMethod(
					"getIdentifier"
				),
				hibEx);
		}
		catch (NoSuchMethodException var2) {
			return null;
		}
	}

}