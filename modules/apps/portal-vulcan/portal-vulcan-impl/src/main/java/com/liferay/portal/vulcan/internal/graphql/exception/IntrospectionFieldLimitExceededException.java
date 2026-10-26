/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.graphql.exception;

import graphql.execution.AbortExecutionException;

/**
 * @author Magdalena Jedraszak
 */
public class IntrospectionFieldLimitExceededException
	extends AbortExecutionException {

	public IntrospectionFieldLimitExceededException(String message) {
		super(message);
	}

}