/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.internal.jaxrs.exception.mapper;

import com.liferay.object.exception.ObjectDefinitionStatusException;
import com.liferay.object.jaxrs.exception.mapper.util.ObjectExceptionMapperUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Larissa Ribeiro
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.Object.Admin.REST)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.Object.Admin.REST.ObjectDefinitionStatusExceptionMapper"
	},
	service = ExceptionMapper.class
)
public class ObjectDefinitionStatusExceptionMapper
	extends BaseExceptionMapper<ObjectDefinitionStatusException> {

	@Override
	protected Problem getProblem(
		ObjectDefinitionStatusException objectDefinitionStatusException) {

		return new Problem(
			Response.Status.BAD_REQUEST,
			ObjectExceptionMapperUtil.getTitle(
				_acceptLanguage, null, _language,
				objectDefinitionStatusException.getMessage(),
				objectDefinitionStatusException.getMessageKey()));
	}

	@Context
	private AcceptLanguage _acceptLanguage;

	@Reference
	private Language _language;

}