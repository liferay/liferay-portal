/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.vulcan.problem;

import com.liferay.portal.vulcan.problem.Problem;
import com.liferay.portal.vulcan.problem.ProblemMapper;
import com.liferay.segments.exception.SegmentsExperienceLayoutException;

import org.osgi.service.component.annotations.Component;

/**
 * @author Alberto Chaparro
 */
@Component(service = ProblemMapper.class)
public class SegmentsExperienceLayoutExceptionProblemMapper
	implements ProblemMapper<SegmentsExperienceLayoutException> {

	@Override
	public Problem getProblem(
		SegmentsExperienceLayoutException segmentsExperienceLayoutException) {

		return ProblemUtil.getProblem(
			"Only site pages can define additional page experiences",
			Problem.Status.BAD_REQUEST, segmentsExperienceLayoutException);
	}

}