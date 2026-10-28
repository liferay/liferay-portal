/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.internal.jaxrs.exception.mapper;

import com.liferay.exportimport.kernel.exception.ExportImportConfigurationNameException;
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
 * @author Adolfo Pérez
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Export.Import.REST)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Export.Import.REST.ExportProcessNameMustNotExceedMaximumLengthExceptionMapper"
	},
	service = ExceptionMapper.class
)
public class ExportProcessNameMustNotExceedMaximumLengthExceptionMapper
	extends BaseExceptionMapper
		<ExportImportConfigurationNameException.MustNotExceedMaximumLength> {

	@Override
	protected Problem getProblem(
		ExportImportConfigurationNameException.MustNotExceedMaximumLength
			mustNotExceedMaximumLength) {

		return new Problem(
			Response.Status.BAD_REQUEST,
			_language.format(
				_acceptLanguage.getPreferredLocale(),
				"process-name-cannot-exceed-x-characters",
				mustNotExceedMaximumLength.nameMaxLength));
	}

	@Context
	private AcceptLanguage _acceptLanguage;

	@Reference
	private Language _language;

}