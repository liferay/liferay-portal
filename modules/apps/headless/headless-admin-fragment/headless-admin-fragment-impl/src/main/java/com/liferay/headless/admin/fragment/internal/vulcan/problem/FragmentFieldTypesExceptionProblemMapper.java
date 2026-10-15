/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.internal.vulcan.problem;

import com.liferay.fragment.exception.FragmentEntryFieldTypesException;
import com.liferay.portal.vulcan.problem.Problem;
import com.liferay.portal.vulcan.problem.ProblemMapper;

import org.osgi.service.component.annotations.Component;

/**
 * @author Rubén Pulido
 */
@Component(service = ProblemMapper.class)
public class FragmentFieldTypesExceptionProblemMapper
	implements ProblemMapper<FragmentEntryFieldTypesException> {

	@Override
	public Problem getProblem(
		FragmentEntryFieldTypesException fragmentEntryFieldTypesException) {

		return ProblemUtil.getProblem(
			Problem.Status.BAD_REQUEST, fragmentEntryFieldTypesException,
			"the-form-fragment-field-types-are-invalid");
	}

}