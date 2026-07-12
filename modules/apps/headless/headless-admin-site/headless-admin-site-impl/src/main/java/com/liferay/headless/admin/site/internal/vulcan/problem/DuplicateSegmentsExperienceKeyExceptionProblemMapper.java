/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.vulcan.problem;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.problem.Problem;
import com.liferay.portal.vulcan.problem.ProblemMapper;
import com.liferay.segments.exception.DuplicateSegmentsExperienceKeyException;

import org.osgi.service.component.annotations.Component;

/**
 * @author Javier Moral
 */
@Component(service = ProblemMapper.class)
public class DuplicateSegmentsExperienceKeyExceptionProblemMapper
	implements ProblemMapper<DuplicateSegmentsExperienceKeyException> {

	@Override
	public Problem getProblem(
		DuplicateSegmentsExperienceKeyException
			duplicateSegmentsExperienceKeyException) {

		String segmentsExperienceKey =
			duplicateSegmentsExperienceKeyException.getSegmentsExperienceKey();

		String message = "A page experience with the same key already exists";

		if (Validator.isNotNull(segmentsExperienceKey)) {
			message = StringBundler.concat(
				"A page experience with key ", segmentsExperienceKey,
				" already exists");
		}

		return ProblemUtil.getProblem(
			message, Problem.Status.CONFLICT,
			duplicateSegmentsExperienceKeyException);
	}

}