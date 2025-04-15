/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.asah.rest.internal.jaxrs.exception.mapper;

import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;
import com.liferay.segments.exception.SegmentsExperimentRelSplitException;

import jakarta.ws.rs.ext.ExceptionMapper;

import org.osgi.service.component.annotations.Component;

/**
 * Converts any {@code SegmentsExperimentRelSplitException} to a {@code 400}
 * error.
 *
 * @author Sarai Díaz
 * @review
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.Segments.Asah.REST)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.Segments.Asah.REST.ExperimentVariantTrafficSplitExceptionMapper"
	},
	service = ExceptionMapper.class
)
public class ExperimentVariantTrafficSplitExceptionMapper
	extends BaseExceptionMapper<SegmentsExperimentRelSplitException> {

	@Override
	protected Problem getProblem(
		SegmentsExperimentRelSplitException
			segmentsExperimentRelSplitException) {

		return new Problem(segmentsExperimentRelSplitException);
	}

}