/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.exception.mapper;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

/**
 * Converts any {@code WebApplicationException} to a {@code 500} error.
 *
 * @author Alejandro Hernández
 * @review
 */
public class WebApplicationExceptionMapper
	extends BaseExceptionMapper<WebApplicationException> {

	@Override
	protected Problem getProblem(
		WebApplicationException webApplicationException) {

		_log.error(webApplicationException);

		Response response = webApplicationException.getResponse();

		return new Problem(
			Response.Status.fromStatusCode(response.getStatus()),
			webApplicationException.getMessage());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		WebApplicationExceptionMapper.class);

}