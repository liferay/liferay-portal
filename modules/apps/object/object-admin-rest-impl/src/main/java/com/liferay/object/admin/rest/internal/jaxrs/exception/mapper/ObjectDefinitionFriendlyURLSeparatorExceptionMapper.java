/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.internal.jaxrs.exception.mapper;

import com.liferay.object.exception.ObjectDefinitionFriendlyURLSeparatorException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

import org.osgi.service.component.annotations.Component;

/**
 * @author Carolina Barbosa
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.Object.Admin.REST)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.Object.Admin.REST.ObjectDefinitionFriendlyURLSeparatorExceptionMapper"
	},
	service = ExceptionMapper.class
)
public class ObjectDefinitionFriendlyURLSeparatorExceptionMapper
	extends BaseExceptionMapper<ObjectDefinitionFriendlyURLSeparatorException> {

	@Override
	protected Problem getProblem(
		ObjectDefinitionFriendlyURLSeparatorException
			objectDefinitionFriendlyURLSeparatorException) {

		return new Problem(
			JSONUtil.putAll(
				JSONUtil.put(
					"fieldName", "friendlyURLSeparator"
				).put(
					"message",
					objectDefinitionFriendlyURLSeparatorException.getMessage()
				)
			).toString(),
			Response.Status.BAD_REQUEST, null,
			ObjectDefinitionFriendlyURLSeparatorException.class.getName());
	}

	@Context
	private AcceptLanguage _acceptLanguage;

}