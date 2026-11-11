/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.vulcan.problem;

import com.liferay.layout.page.template.exception.DuplicateLayoutPageTemplateCollectionException;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.problem.Problem;
import com.liferay.portal.vulcan.problem.ProblemMapper;

import org.osgi.service.component.annotations.Component;

/**
 * @author Javier Moral
 */
@Component(service = ProblemMapper.class)
public class PageTemplateSetExceptionProblemMapper
	implements ProblemMapper<DuplicateLayoutPageTemplateCollectionException> {

	@Override
	public Problem getProblem(
		DuplicateLayoutPageTemplateCollectionException
			duplicateLayoutPageTemplateCollectionException) {

		String message =
			"A page template set with the same name already exists";
		String name =
			duplicateLayoutPageTemplateCollectionException.getMessage();

		if (Validator.isNotNull(name)) {
			message = StringBundler.concat(
				"A page template set with name ", name, " already exists");
		}

		return ProblemUtil.getProblem(
			message, Problem.Status.CONFLICT,
			duplicateLayoutPageTemplateCollectionException);
	}

}