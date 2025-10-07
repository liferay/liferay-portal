/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.exception.mapper;

import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;

import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

/**
 * @author Alberto Javier Moreno Lage
 */
public class InvalidTypeIdExceptionMapper
	extends BaseExceptionMapper<InvalidTypeIdException> {

	@Override
	protected Problem getProblem(
		InvalidTypeIdException invalidTypeIdException) {

		return new Problem(invalidTypeIdException);
	}

}