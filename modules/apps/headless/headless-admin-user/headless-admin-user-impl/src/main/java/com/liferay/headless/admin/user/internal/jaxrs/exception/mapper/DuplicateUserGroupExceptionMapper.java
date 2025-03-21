/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.internal.jaxrs.exception.mapper;

import com.liferay.portal.kernel.exception.DuplicateUserGroupException;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

import org.osgi.service.component.annotations.Component;

/**
 * @author Erick Monteiro
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.Headless.Admin.User)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.Headless.Admin.User.DuplicateUserGroupExceptionMapper"
	},
	service = ExceptionMapper.class
)
public class DuplicateUserGroupExceptionMapper
	extends BaseExceptionMapper<DuplicateUserGroupException> {

	@Override
	protected Problem getProblem(
		DuplicateUserGroupException duplicateUserGroupException) {

		return new Problem(
			Response.Status.CONFLICT,
			"A user group already exists with the same name");
	}

}