/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.vulcan.problem;

import com.liferay.portal.kernel.exception.SitemapIncludeException;
import com.liferay.portal.vulcan.problem.Problem;
import com.liferay.portal.vulcan.problem.ProblemMapper;

import org.osgi.service.component.annotations.Component;

/**
 * @author Javier Moral
 */
@Component(service = ProblemMapper.class)
public class SitemapIncludeExceptionProblemMapper
	implements ProblemMapper<SitemapIncludeException> {

	@Override
	public Problem getProblem(SitemapIncludeException sitemapIncludeException) {
		return ProblemUtil.getProblem(
			"The \"sitemap-include\" setting must be 0 or 1",
			Problem.Status.BAD_REQUEST, sitemapIncludeException);
	}

}