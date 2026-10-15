/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.internal.vulcan.problem;

import com.liferay.fragment.exception.RequiredFragmentEntryVersionException;
import com.liferay.portal.vulcan.problem.Problem;
import com.liferay.portal.vulcan.problem.ProblemMapper;

import org.osgi.service.component.annotations.Component;

/**
 * @author Rubén Pulido
 */
@Component(service = ProblemMapper.class)
public class RequiredFragmentVersionExceptionProblemMapper
	implements ProblemMapper<RequiredFragmentEntryVersionException> {

	@Override
	public Problem getProblem(
		RequiredFragmentEntryVersionException
			requiredFragmentEntryVersionException) {

		return ProblemUtil.getProblem(
			Problem.Status.BAD_REQUEST, requiredFragmentEntryVersionException,
			"at-least-one-fragment-entry-version-is-required");
	}

}