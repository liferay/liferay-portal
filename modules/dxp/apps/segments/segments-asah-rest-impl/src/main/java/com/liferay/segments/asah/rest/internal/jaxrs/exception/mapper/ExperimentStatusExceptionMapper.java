/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.asah.rest.internal.jaxrs.exception.mapper;

import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;
import com.liferay.segments.exception.SegmentsExperimentStatusException;

import jakarta.ws.rs.ext.ExceptionMapper;

import org.osgi.service.component.annotations.Component;

/**
 * Converts any {@code SegmentsExperimentStatusException} to a {@code 400}
 * error.
 *
 * @author Sarai Díaz
 * @review
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.Segments.Asah.REST)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.Segments.Asah.REST.ExperimentStatusExceptionMapper"
	},
	service = ExceptionMapper.class
)
public class ExperimentStatusExceptionMapper
	extends BaseExceptionMapper<SegmentsExperimentStatusException> {

	@Override
	protected Problem getProblem(
		SegmentsExperimentStatusException segmentsExperimentStatusException) {

		return new Problem(segmentsExperimentStatusException);
	}

}