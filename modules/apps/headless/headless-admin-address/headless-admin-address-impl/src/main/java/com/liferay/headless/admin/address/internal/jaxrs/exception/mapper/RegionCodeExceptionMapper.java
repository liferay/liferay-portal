/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.address.internal.jaxrs.exception.mapper;

import com.liferay.portal.kernel.exception.RegionCodeException;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.ext.ExceptionMapper;

import org.osgi.service.component.annotations.Component;

/**
 * @author Pei-Jung Lan
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.Headless.Admin.Address)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.Headless.Admin.Address.RegionCodeExceptionMapper"
	},
	service = ExceptionMapper.class
)
public class RegionCodeExceptionMapper
	extends BaseExceptionMapper<RegionCodeException> {

	@Override
	protected Problem getProblem(RegionCodeException regionCodeException) {
		return new Problem(regionCodeException);
	}

}