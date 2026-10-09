/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.vulcan.problem;

import com.liferay.layout.utility.page.exception.DefaultLayoutUtilityPageEntryException;
import com.liferay.portal.vulcan.problem.Problem;
import com.liferay.portal.vulcan.problem.ProblemMapper;

import org.osgi.service.component.annotations.Component;

/**
 * @author Lourdes Fernández Besada
 */
@Component(service = ProblemMapper.class)
public class UtilityPageMarkAsDefaultExceptionProblemMapper
	implements ProblemMapper<DefaultLayoutUtilityPageEntryException> {

	@Override
	public Problem getProblem(
		DefaultLayoutUtilityPageEntryException
			defaultLayoutUtilityPageEntryException) {

		return ProblemUtil.getProblem(
			"A utility page must be published before it is marked as the " +
				"default utility page",
			Problem.Status.BAD_REQUEST, defaultLayoutUtilityPageEntryException);
	}

}