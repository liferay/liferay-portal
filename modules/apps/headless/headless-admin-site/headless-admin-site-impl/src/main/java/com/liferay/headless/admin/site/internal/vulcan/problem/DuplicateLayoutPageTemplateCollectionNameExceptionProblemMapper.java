/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.vulcan.problem;

import com.liferay.headless.admin.site.internal.util.LayoutPageTemplateCollectionUtil;
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
public class DuplicateLayoutPageTemplateCollectionNameExceptionProblemMapper
	implements ProblemMapper<DuplicateLayoutPageTemplateCollectionException> {

	@Override
	public Problem getProblem(
		DuplicateLayoutPageTemplateCollectionException
			duplicateLayoutPageTemplateCollectionException) {

		return ProblemUtil.getProblem(
			_getMessage(duplicateLayoutPageTemplateCollectionException),
			Problem.Status.CONFLICT,
			duplicateLayoutPageTemplateCollectionException);
	}

	private String _getMessage(
		DuplicateLayoutPageTemplateCollectionException
			duplicateLayoutPageTemplateCollectionException) {

		String name = LayoutPageTemplateCollectionUtil.getName(
			duplicateLayoutPageTemplateCollectionException.getType());

		if (Validator.isNull(
				duplicateLayoutPageTemplateCollectionException.getName())) {

			return StringBundler.concat(
				"A ", name, " with the same name already exists");
		}

		return StringBundler.concat(
			"A ", name, " with the name \"",
			duplicateLayoutPageTemplateCollectionException.getName(),
			"\" already exists");
	}

}