/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.rest.internal.jaxrs.exception.mapper;

import com.liferay.portal.search.rest.internal.resource.exception.IllegalScopeParameterException;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import org.osgi.service.component.annotations.Component;

/**
 * @author Petteri Karttunen
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.Portal.Search.REST)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.Portal.Search.REST.IllegalScopeParameterExceptionMapper"
	},
	service = ExceptionMapper.class
)
@Provider
public class IllegalScopeParameterExceptionMapper
	extends BaseExceptionMapper<IllegalScopeParameterException> {

	@Override
	protected Problem getProblem(
		IllegalScopeParameterException illegalScopeParameterException) {

		return new Problem(illegalScopeParameterException);
	}

}