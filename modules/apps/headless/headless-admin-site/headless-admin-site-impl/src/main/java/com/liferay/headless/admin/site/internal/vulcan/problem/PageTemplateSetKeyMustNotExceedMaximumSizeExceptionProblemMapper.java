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
public class PageTemplateSetKeyMustNotExceedMaximumSizeExceptionProblemMapper
	implements ProblemMapper
		<LayoutPageTemplateCollectionLayoutPageTemplateCollectionKeyException.
			MustNotExceedMaximumSize> {

	@Override
	public Problem getProblem(
		LayoutPageTemplateCollectionLayoutPageTemplateCollectionKeyException.
			MustNotExceedMaximumSize mustNotExceedMaximumSize) {

		return ProblemUtil.getProblem(
			StringBundler.concat(
				"The key \"",
				mustNotExceedMaximumSize.getLayoutPageTemplateCollectionKey(),
				"\" of the ",
				LayoutPageTemplateCollectionUtil.getTypeLabel(
					mustNotExceedMaximumSize.getType()),
				" must not exceed ",
				mustNotExceedMaximumSize.
					getLayoutPageTemplateCollectionKeyMaxSize(),
				" characters"),
			Problem.Status.BAD_REQUEST, mustNotExceedMaximumSize);
	}

}