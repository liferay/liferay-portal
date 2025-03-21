/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.exception.mapper;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.NotAcceptableException;
import jakarta.ws.rs.core.Response;

/**
 * Converts any {@code NotAcceptableException} to a {@code 400} error.
 *
 * @author Javier de Arcos
 */
public class NotAcceptableExceptionMapper
	extends BaseExceptionMapper<NotAcceptableException> {

	@Override
	protected Problem getProblem(
		NotAcceptableException notAcceptableException) {

		_log.error(notAcceptableException);

		return new Problem(
			Response.Status.NOT_ACCEPTABLE,
			notAcceptableException.getMessage());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		NotAcceptableExceptionMapper.class);

}