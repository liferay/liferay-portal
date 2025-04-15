/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.internal.jaxrs.exception.mapper;

import com.liferay.object.exception.ObjectActionNameException;
import com.liferay.object.jaxrs.exception.mapper.util.ObjectExceptionMapperUtil;
import com.liferay.portal.kernel.json.JSONUtil;
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
 * @author Javier Gamarra
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.Object.Admin.REST)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.Object.Admin.REST.ObjectActionNameExceptionMapper"
	},
	service = ExceptionMapper.class
)
public class ObjectActionNameExceptionMapper
	extends BaseExceptionMapper<ObjectActionNameException> {

	@Override
	protected Problem getProblem(
		ObjectActionNameException objectActionNameException) {

		return new Problem(
			JSONUtil.putAll(
				JSONUtil.put(
					"fieldName", "name"
				).put(
					"message",
					ObjectExceptionMapperUtil.getTitle(
						_acceptLanguage,
						objectActionNameException.getArguments(), _language,
						objectActionNameException.getMessage(),
						objectActionNameException.getMessageKey())
				)
			).toString(),
			Response.Status.BAD_REQUEST, null,
			ObjectActionNameException.class.getName());
	}

	@Context
	private AcceptLanguage _acceptLanguage;

	@Reference
	private Language _language;

}