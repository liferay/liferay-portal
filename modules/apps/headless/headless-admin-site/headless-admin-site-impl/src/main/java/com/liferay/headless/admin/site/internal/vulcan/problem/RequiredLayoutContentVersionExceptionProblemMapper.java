/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.vulcan.problem;

import com.liferay.layout.content.exception.RequiredLayoutContentVersionException;
import com.liferay.portal.vulcan.problem.Problem;
import com.liferay.portal.vulcan.problem.ProblemMapper;

import org.osgi.service.component.annotations.Component;

/**
 * @author Lourdes Fernández Besada
 */
@Component(service = ProblemMapper.class)
public class RequiredLayoutContentVersionExceptionProblemMapper
	implements ProblemMapper<RequiredLayoutContentVersionException> {

	@Override
	public Problem getProblem(
		RequiredLayoutContentVersionException
			requiredLayoutContentVersionException) {

		return ProblemUtil.getProblem(
			"The latest approved page specification version is required",
			Problem.Status.CONFLICT, requiredLayoutContentVersionException);
	}

}