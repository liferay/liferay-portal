/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.vulcan.problem;

import com.liferay.headless.admin.site.internal.util.LayoutPageTemplateCollectionUtil;
import com.liferay.layout.page.template.exception.LayoutPageTemplateCollectionLayoutPageTemplateCollectionKeyException;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.vulcan.problem.Problem;
import com.liferay.portal.vulcan.problem.ProblemMapper;

import org.osgi.service.component.annotations.Component;

/**
 * @author Lourdes Fernández Besada
 */
@Component(service = ProblemMapper.class)
public class
	PageTemplateSetKeyMustNotContainInvalidCharactersExceptionProblemMapper
		implements ProblemMapper
			<LayoutPageTemplateCollectionLayoutPageTemplateCollectionKeyException.MustNotContainInvalidCharacters> {

	@Override
	public Problem getProblem(
		LayoutPageTemplateCollectionLayoutPageTemplateCollectionKeyException.
			MustNotContainInvalidCharacters mustNotContainInvalidCharacters) {

		return ProblemUtil.getProblem(
			StringBundler.concat(
				"The key \"",
				mustNotContainInvalidCharacters.
					getLayoutPageTemplateCollectionKey(),
				"\" of the ",
				LayoutPageTemplateCollectionUtil.getTypeLabel(
					mustNotContainInvalidCharacters.getType()),
				" must contain only alphanumeric characters, dashes, and ",
				"underscores"),
			Problem.Status.BAD_REQUEST, mustNotContainInvalidCharacters);
	}

}