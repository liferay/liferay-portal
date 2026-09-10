/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.exception.mapper;

import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

/**
 * Converts any {@code IllegalArgumentException} to a {@code 400} error.
 *
 * @author Rubén Pulido
 * @review
 */
public class IllegalArgumentExceptionMapper
	extends BaseExceptionMapper<IllegalArgumentException> {

	@Override
	protected Problem getProblem(
		IllegalArgumentException illegalArgumentException) {

		return new Problem(illegalArgumentException);
	}

}