/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.vulcan.problem;

import com.liferay.portal.vulcan.problem.Problem;
import com.liferay.portal.vulcan.problem.ProblemMapper;
import com.liferay.segments.exception.LockedSegmentsExperimentException;

import org.osgi.service.component.annotations.Component;

/**
 * @author Javier Moral
 */
@Component(service = ProblemMapper.class)
public class LockedSegmentsExperimentExceptionProblemMapper
	implements ProblemMapper<LockedSegmentsExperimentException> {

	@Override
	public Problem getProblem(
		LockedSegmentsExperimentException lockedSegmentsExperimentException) {

		return ProblemUtil.getProblem(
			"The page experience cannot be modified while it is used by an " +
				"active A/B test",
			Problem.Status.CONFLICT, lockedSegmentsExperimentException);
	}

}