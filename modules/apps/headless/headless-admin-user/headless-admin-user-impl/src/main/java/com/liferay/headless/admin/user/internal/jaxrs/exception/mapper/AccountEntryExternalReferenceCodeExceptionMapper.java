/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.internal.jaxrs.exception.mapper;

import com.liferay.account.exception.AccountEntryExternalReferenceCodeException;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

import org.osgi.service.component.annotations.Component;

/**
 * @author Tancredi Covioli
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.Headless.Admin.User)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.Headless.Admin.User.AccountEntryExternalReferenceCodeExceptionMapper"
	},
	service = ExceptionMapper.class
)
public class AccountEntryExternalReferenceCodeExceptionMapper
	extends BaseExceptionMapper<AccountEntryExternalReferenceCodeException> {

	@Override
	protected Problem getProblem(
		AccountEntryExternalReferenceCodeException
			accountEntryExternalReferenceCodeException) {

		return new Problem(
			Response.Status.BAD_REQUEST,
			"The account external reference code is invalid");
	}

}