/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.list.type.internal.jaxrs.exception.mapper;

import com.liferay.list.type.exception.ListTypeDefinitionSystemException;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.ext.ExceptionMapper;

import org.osgi.service.component.annotations.Component;

/**
 * @author Pedro Leite
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.Headless.Admin.List.Type)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.Headless.Admin.List.Type.ListTypeDefinitionSystemExceptionMapper"
	},
	service = ExceptionMapper.class
)
public class ListTypeDefinitionSystemExceptionMapper
	extends BaseExceptionMapper<ListTypeDefinitionSystemException> {

	@Override
	protected Problem getProblem(
		ListTypeDefinitionSystemException listTypeDefinitionSystemException) {

		return new Problem(listTypeDefinitionSystemException);
	}

}