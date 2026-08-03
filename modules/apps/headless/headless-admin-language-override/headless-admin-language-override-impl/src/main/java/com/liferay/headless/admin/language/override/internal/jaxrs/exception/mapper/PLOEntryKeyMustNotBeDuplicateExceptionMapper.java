/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.language.override.internal.jaxrs.exception.mapper;

import com.liferay.portal.language.override.exception.PLOEntryKeyException;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

import org.osgi.service.component.annotations.Component;

/**
 * @author Thiago Buarque
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.Headless.Admin.Language.Override)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.Headless.Admin.Language.Override.PLOEntryKeyMustNotBeDuplicateExceptionMapper"
	},
	service = ExceptionMapper.class
)
public class PLOEntryKeyMustNotBeDuplicateExceptionMapper
	extends BaseExceptionMapper<PLOEntryKeyException.MustNotBeDuplicate> {

	@Override
	protected Problem getProblem(
		PLOEntryKeyException.MustNotBeDuplicate mustNotBeDuplicate) {

		return new Problem(
			mustNotBeDuplicate.getMessage(), Response.Status.CONFLICT, null,
			PLOEntryKeyException.MustNotBeDuplicate.class.getName());
	}

}