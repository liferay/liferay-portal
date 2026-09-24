/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.vulcan.problem;

import com.liferay.portal.kernel.exception.SitemapPagePriorityException;
import com.liferay.portal.vulcan.problem.Problem;
import com.liferay.portal.vulcan.problem.ProblemMapper;

import org.osgi.service.component.annotations.Component;

/**
 * @author Javier Moral
 */
@Component(service = ProblemMapper.class)
public class SitemapPagePriorityExceptionProblemMapper
	implements ProblemMapper<SitemapPagePriorityException> {

	@Override
	public Problem getProblem(
		SitemapPagePriorityException sitemapPagePriorityException) {

		return ProblemUtil.getProblem(
			"The \"sitemap-priority\" setting must be a number between 0 and 1",
			Problem.Status.BAD_REQUEST, sitemapPagePriorityException);
	}

}