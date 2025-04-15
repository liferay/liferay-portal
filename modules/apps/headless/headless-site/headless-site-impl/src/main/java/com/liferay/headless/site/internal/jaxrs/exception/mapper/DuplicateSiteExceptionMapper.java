/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.site.internal.jaxrs.exception.mapper;

import com.liferay.portal.kernel.exception.DuplicateGroupException;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

import org.osgi.service.component.annotations.Component;

/**
 * Converts any {@code DuplicateGroupException} to a {@code 409} error.
 *
 * @author Rubén Pulido
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.Headless.Site)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.Headless.Site.DuplicateSiteExceptionMapper"
	},
	service = ExceptionMapper.class
)
public class DuplicateSiteExceptionMapper
	extends BaseExceptionMapper<DuplicateGroupException> {

	@Override
	protected Problem getProblem(
		DuplicateGroupException duplicateGroupException) {

		return new Problem(
			Response.Status.CONFLICT,
			"A site with the same key already exists");
	}

}