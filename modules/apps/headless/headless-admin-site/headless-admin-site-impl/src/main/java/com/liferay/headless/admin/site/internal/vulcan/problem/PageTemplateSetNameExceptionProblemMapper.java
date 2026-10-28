/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.vulcan.problem;

import com.liferay.layout.page.template.exception.LayoutPageTemplateCollectionNameException;
import com.liferay.portal.vulcan.problem.Problem;
import com.liferay.portal.vulcan.problem.ProblemMapper;

import org.osgi.service.component.annotations.Component;

/**
 * @author Javier Moral
 */
@Component(service = ProblemMapper.class)
public class PageTemplateSetNameExceptionProblemMapper
	implements ProblemMapper<LayoutPageTemplateCollectionNameException> {

	@Override
	public Problem getProblem(
		LayoutPageTemplateCollectionNameException
			layoutPageTemplateCollectionNameException) {

		return ProblemUtil.getProblem(
			_getMessage(layoutPageTemplateCollectionNameException),
			Problem.Status.BAD_REQUEST,
			layoutPageTemplateCollectionNameException);
	}

	private String _getMessage(
		LayoutPageTemplateCollectionNameException
			layoutPageTemplateCollectionNameException) {

		if (layoutPageTemplateCollectionNameException instanceof
				LayoutPageTemplateCollectionNameException.MustNotBeNull) {

			return "A name is required for each template folder or set";
		}

		if (layoutPageTemplateCollectionNameException instanceof
				LayoutPageTemplateCollectionNameException.
					MustNotExceedMaximumSize) {

			return "The name for the template folder or set is too long";
		}

		return "The name for the template folder or set is invalid";
	}

}