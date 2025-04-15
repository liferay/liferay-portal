/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.portal.instances.internal.jaxrs.exception.mapper;

import com.liferay.portal.kernel.exception.RequiredCompanyException;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

import org.osgi.service.component.annotations.Component;

/**
 * Converts any {@code RequiredCompanyException} to a {@code 400} error.
 *
 * @author Alberto Chaparro
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.Headless.Portal.Instances)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.Headless.Portal.Instances.RequiredPortalInstanceExceptionMapper"
	},
	service = ExceptionMapper.class
)
public class RequiredPortalInstanceExceptionMapper
	extends BaseExceptionMapper<RequiredCompanyException> {

	@Override
	protected Problem getProblem(
		RequiredCompanyException requiredCompanyException) {

		return new Problem(
			Response.Status.BAD_REQUEST,
			StringUtil.replaceFirst(
				requiredCompanyException.getMessage(), "company",
				"portal instance"));
	}

}