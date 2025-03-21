/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.rest.internal.jaxrs.exception.mapper;

import com.liferay.change.tracking.exception.CTCollectionStatusException;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.ext.ExceptionMapper;

import org.osgi.service.component.annotations.Component;

/**
 * @author Pei-Jung Lan
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.Change.Tracking.REST)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.Change.Tracking.REST.CTCollectionStatusExceptionMapper"
	},
	service = ExceptionMapper.class
)
public class CTCollectionStatusExceptionMapper
	extends BaseExceptionMapper<CTCollectionStatusException> {

	@Override
	protected Problem getProblem(
		CTCollectionStatusException ctCollectionStatusException) {

		return new Problem(ctCollectionStatusException);
	}

}