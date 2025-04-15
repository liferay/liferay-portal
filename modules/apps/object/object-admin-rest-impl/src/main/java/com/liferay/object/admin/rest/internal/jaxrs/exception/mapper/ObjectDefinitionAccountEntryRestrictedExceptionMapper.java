/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.internal.jaxrs.exception.mapper;

import com.liferay.object.exception.ObjectDefinitionAccountEntryRestrictedException;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.ext.ExceptionMapper;

import org.osgi.service.component.annotations.Component;

/**
 * @author Artur Souza
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.Object.Admin.REST)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.Object.Admin.REST.objectDefinitionAccountEntryRestrictedExceptionMapper"
	},
	service = ExceptionMapper.class
)
public class ObjectDefinitionAccountEntryRestrictedExceptionMapper
	extends BaseExceptionMapper
		<ObjectDefinitionAccountEntryRestrictedException> {

	@Override
	protected Problem getProblem(
		ObjectDefinitionAccountEntryRestrictedException
			objectDefinitionAccountEntryRestrictedException) {

		return new Problem(objectDefinitionAccountEntryRestrictedException);
	}

}