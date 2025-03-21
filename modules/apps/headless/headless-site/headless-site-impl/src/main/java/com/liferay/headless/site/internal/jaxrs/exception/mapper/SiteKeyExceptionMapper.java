/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.site.internal.jaxrs.exception.mapper;

import com.liferay.portal.kernel.exception.GroupKeyException;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

import org.osgi.service.component.annotations.Component;

/**
 * Converts any {@code GroupKeyException} to a {@code 400} error.
 *
 * @author Rubén Pulido
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.Headless.Site)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.Headless.Site.SiteKeyExceptionMapper"
	},
	service = ExceptionMapper.class
)
public class SiteKeyExceptionMapper
	extends BaseExceptionMapper<GroupKeyException> {

	@Override
	protected Problem getProblem(GroupKeyException groupKeyException) {
		return new Problem(Response.Status.BAD_REQUEST, "Site key is invalid");
	}

}