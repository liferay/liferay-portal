/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.internal.vulcan.problem;

import com.liferay.fragment.exception.FragmentEntryPreviewFileEntryIdException;
import com.liferay.portal.vulcan.problem.Problem;
import com.liferay.portal.vulcan.problem.ProblemMapper;

import org.osgi.service.component.annotations.Component;

/**
 * @author Georgel Pop
 */
@Component(service = ProblemMapper.class)
public class FragmentEntryPreviewFileEntryIdExceptionProblemMapper
	implements ProblemMapper<FragmentEntryPreviewFileEntryIdException> {

	@Override
	public Problem getProblem(
		FragmentEntryPreviewFileEntryIdException
			fragmentEntryPreviewFileEntryIdException) {

		return ProblemUtil.getProblem(
			Problem.Status.BAD_REQUEST,
			fragmentEntryPreviewFileEntryIdException,
			"fragment-thumbnail-is-invalid");
	}

}