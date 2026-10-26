/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.vulcan.problem;

import com.liferay.layout.page.template.constants.LayoutPageTemplateCollectionTypeConstants;
import com.liferay.layout.page.template.exception.LayoutPageTemplateCollectionLayoutPageTemplateCollectionKeyException;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.problem.Problem;
import com.liferay.portal.vulcan.problem.ProblemMapper;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;

/**
 * @author Lourdes Fernández Besada
 */
@Component(service = ProblemMapper.class)
public class DisplayPageTemplateFolderKeyExceptionProblemMapper
	implements ProblemMapper
		<LayoutPageTemplateCollectionLayoutPageTemplateCollectionKeyException> {

	@Override
	public Problem getProblem(
		LayoutPageTemplateCollectionLayoutPageTemplateCollectionKeyException
			layoutPageTemplateCollectionLayoutPageTemplateCollectionKeyException) {

		String name = "page template set";

		if (Objects.equals(
				layoutPageTemplateCollectionLayoutPageTemplateCollectionKeyException.
					getType(),
				LayoutPageTemplateCollectionTypeConstants.DISPLAY_PAGE)) {

			name = "display page template folder";
		}

		Problem.Status status = Problem.Status.BAD_REQUEST;

		if (
				layoutPageTemplateCollectionLayoutPageTemplateCollectionKeyException instanceof
					LayoutPageTemplateCollectionLayoutPageTemplateCollectionKeyException.MustNotBeDuplicate) {

			status = Problem.Status.CONFLICT;
		}

		return ProblemUtil.getProblem(
			StringUtil.replace(
				layoutPageTemplateCollectionLayoutPageTemplateCollectionKeyException.
					getMessage(),
				new String[] {
					"Layout page template collection key",
					"layout page template collection key",
					"layout page template collection"
				},
				new String[] {"Key", "key", name}),
			status,
			layoutPageTemplateCollectionLayoutPageTemplateCollectionKeyException);
	}

}