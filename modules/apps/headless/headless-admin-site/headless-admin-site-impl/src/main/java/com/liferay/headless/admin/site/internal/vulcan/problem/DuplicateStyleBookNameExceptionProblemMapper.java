/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.vulcan.problem;

import com.liferay.portal.vulcan.problem.Problem;
import com.liferay.portal.vulcan.problem.ProblemMapper;
import com.liferay.style.book.exception.DuplicateStyleBookEntryNameException;

import org.osgi.service.component.annotations.Component;

/**
 * @author Thiago Buarque
 */
@Component(service = ProblemMapper.class)
public class DuplicateStyleBookNameExceptionProblemMapper
	implements ProblemMapper<DuplicateStyleBookEntryNameException> {

	@Override
	public Problem getProblem(
		DuplicateStyleBookEntryNameException
			duplicateStyleBookEntryNameException) {

		return ProblemUtil.getProblem(
			"A style book with the same name already exists",
			Problem.Status.CONFLICT, duplicateStyleBookEntryNameException);
	}

}