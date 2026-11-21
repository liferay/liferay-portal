/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.vulcan.problem;

import com.liferay.headless.admin.site.internal.util.LayoutPageTemplateCollectionUtil;
import com.liferay.layout.page.template.exception.LayoutPageTemplateCollectionNameException;
import com.liferay.portal.vulcan.problem.Problem;
import com.liferay.portal.vulcan.problem.ProblemMapper;

import org.osgi.service.component.annotations.Component;

/**
 * @author Javier Moral
 */
@Component(service = ProblemMapper.class)
public class PageTemplateSetNameMustNotBeNullExceptionProblemMapper
	implements ProblemMapper
		<LayoutPageTemplateCollectionNameException.MustNotBeNull> {

	@Override
	public Problem getProblem(
		LayoutPageTemplateCollectionNameException.MustNotBeNull mustNotBeNull) {

		String typeLabel = LayoutPageTemplateCollectionUtil.getTypeLabel(
			mustNotBeNull.getType());

		return ProblemUtil.getProblem(
			"A name is required for the " + typeLabel,
			Problem.Status.BAD_REQUEST, mustNotBeNull);
	}

}