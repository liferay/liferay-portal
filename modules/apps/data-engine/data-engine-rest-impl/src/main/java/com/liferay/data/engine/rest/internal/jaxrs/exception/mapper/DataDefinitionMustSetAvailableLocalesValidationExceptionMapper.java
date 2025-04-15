/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.engine.rest.internal.jaxrs.exception.mapper;

import com.liferay.data.engine.rest.resource.exception.DataDefinitionValidationException;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.ext.ExceptionMapper;

import org.osgi.service.component.annotations.Component;

/**
 * @author Leonardo Barros
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.Data.Engine.REST)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.Data.Engine.REST.DataDefinitionMustSetAvailableLocalesValidationExceptionMapper"
	},
	service = ExceptionMapper.class
)
public class DataDefinitionMustSetAvailableLocalesValidationExceptionMapper
	extends BaseExceptionMapper
		<DataDefinitionValidationException.MustSetAvailableLocales> {

	@Override
	protected Problem getProblem(
		DataDefinitionValidationException.MustSetAvailableLocales
			mustSetAvailableLocales) {

		return new Problem(mustSetAvailableLocales);
	}

}