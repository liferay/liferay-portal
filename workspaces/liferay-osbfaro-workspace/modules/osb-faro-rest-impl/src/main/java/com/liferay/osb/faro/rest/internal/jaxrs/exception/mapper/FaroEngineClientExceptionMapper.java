/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.jaxrs.exception.mapper;

import com.liferay.osb.faro.engine.client.exception.FaroEngineClientException;
import com.liferay.osb.faro.engine.client.exception.InvalidFilterException;
import com.liferay.osb.faro.engine.client.exception.NoSuchEntryException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

import org.osgi.service.component.annotations.Component;

/**
 * @author Nilton Vieira
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.Faro.Rest)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.Faro.Rest.FaroEngineClientExceptionMapper"
	},
	service = ExceptionMapper.class
)
public class FaroEngineClientExceptionMapper
	extends BaseExceptionMapper<FaroEngineClientException> {

	@Override
	protected Problem getProblem(
		FaroEngineClientException faroEngineClientException) {

		if (faroEngineClientException instanceof NoSuchEntryException) {
			return new Problem(
				Response.Status.NOT_FOUND,
				Response.Status.NOT_FOUND.getReasonPhrase());
		}

		int status = faroEngineClientException.getStatus();

		if ((faroEngineClientException instanceof InvalidFilterException) ||
			((status >= HttpServletResponse.SC_BAD_REQUEST) &&
			 (status < HttpServletResponse.SC_INTERNAL_SERVER_ERROR))) {

			return new Problem(
				Response.Status.BAD_REQUEST,
				"Check the parameter values against the tool description");
		}

		_log.error(faroEngineClientException);

		return new Problem(
			Response.Status.INTERNAL_SERVER_ERROR,
			Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FaroEngineClientExceptionMapper.class);

}