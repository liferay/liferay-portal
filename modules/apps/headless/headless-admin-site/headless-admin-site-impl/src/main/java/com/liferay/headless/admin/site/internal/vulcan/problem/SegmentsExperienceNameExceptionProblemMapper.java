/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.vulcan.problem;

import com.liferay.portal.vulcan.problem.Problem;
import com.liferay.portal.vulcan.problem.ProblemMapper;
import com.liferay.segments.exception.SegmentsExperienceNameException;

import org.osgi.service.component.annotations.Component;

/**
 * @author Javier Moral
 */
@Component(service = ProblemMapper.class)
public class SegmentsExperienceNameExceptionProblemMapper
	implements ProblemMapper<SegmentsExperienceNameException> {

	@Override
	public Problem getProblem(
		SegmentsExperienceNameException segmentsExperienceNameException) {

		return ProblemUtil.getProblem(
			"A name in the site's default language is required for each page " +
				"experience",
			Problem.Status.BAD_REQUEST, segmentsExperienceNameException);
	}

}