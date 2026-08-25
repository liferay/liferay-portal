/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.asset.library.internal.jaxrs.exception.mapper;

import com.liferay.portal.kernel.exception.RoleSubtypeException;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

import org.osgi.service.component.annotations.Component;

/**
 * @author Lianne Louie
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.Headless.Asset.Library)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.Headless.Asset.Library.RoleSubtypeExceptionMapper"
	},
	service = ExceptionMapper.class
)
public class RoleSubtypeExceptionMapper
	extends BaseExceptionMapper<RoleSubtypeException> {

	@Override
	protected Problem getProblem(RoleSubtypeException roleSubtypeException) {
		return new Problem(
			Response.Status.BAD_REQUEST, "The role subtype is invalid");
	}

}