/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.vulcan.problem;

import com.liferay.layout.utility.page.exception.LayoutUtilityPageEntryNameException;
import com.liferay.portal.vulcan.problem.Problem;
import com.liferay.portal.vulcan.problem.ProblemMapper;

import org.osgi.service.component.annotations.Component;

/**
 * @author Javier Moral
 */
@Component(service = ProblemMapper.class)
public class DuplicateLayoutUtilityPageEntryNameExceptionProblemMapper
	implements ProblemMapper
		<LayoutUtilityPageEntryNameException.MustNotBeDuplicate> {

	@Override
	public Problem getProblem(
		LayoutUtilityPageEntryNameException.MustNotBeDuplicate
			mustNotBeDuplicate) {

		return ProblemUtil.getProblem(
			"A utility page of the same type with the name \"" +
				mustNotBeDuplicate.name + "\" already exists",
			Problem.Status.CONFLICT, mustNotBeDuplicate);
	}

}