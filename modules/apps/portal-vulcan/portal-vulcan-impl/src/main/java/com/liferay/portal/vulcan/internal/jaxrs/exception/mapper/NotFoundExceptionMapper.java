/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.exception.mapper;

import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;

/**
 * @author Alberto Javier Moreno Lage
 */
public class NotFoundExceptionMapper
	extends BaseExceptionMapper<NotFoundException> {

	@Override
	protected Problem getProblem(NotFoundException notFoundException) {
		return new Problem(Response.Status.NOT_FOUND, null);
	}

}