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
public class LayoutPageTemplateCollectionKeyExceptionProblemMapper
	implements ProblemMapper
		<LayoutPageTemplateCollectionLayoutPageTemplateCollectionKeyException> {

	@Override
	public Problem getProblem(
		LayoutPageTemplateCollectionLayoutPageTemplateCollectionKeyException
			layoutPageTemplateCollectionLayoutPageTemplateCollectionKeyException) {

		Problem.Status status = Problem.Status.BAD_REQUEST;

		if (
				layoutPageTemplateCollectionLayoutPageTemplateCollectionKeyException instanceof
					LayoutPageTemplateCollectionLayoutPageTemplateCollectionKeyException.MustNotBeDuplicate) {

			status = Problem.Status.CONFLICT;
		}

		return ProblemUtil.getProblem(
			_getMessage(
				layoutPageTemplateCollectionLayoutPageTemplateCollectionKeyException),
			status,
			layoutPageTemplateCollectionLayoutPageTemplateCollectionKeyException);
	}

	private String _getMessage(
		LayoutPageTemplateCollectionLayoutPageTemplateCollectionKeyException
			layoutPageTemplateCollectionLayoutPageTemplateCollectionKeyException) {

		String key =
			layoutPageTemplateCollectionLayoutPageTemplateCollectionKeyException.
				getLayoutPageTemplateCollectionKey();
		String name = LayoutPageTemplateCollectionUtil.getName(
			layoutPageTemplateCollectionLayoutPageTemplateCollectionKeyException.
				getType());

		if (
				layoutPageTemplateCollectionLayoutPageTemplateCollectionKeyException instanceof
					LayoutPageTemplateCollectionLayoutPageTemplateCollectionKeyException.MustNotBeDuplicate) {

			return StringBundler.concat(
				"A ", name, " with the key \"", key, "\" already exists");
		}

		if (
				layoutPageTemplateCollectionLayoutPageTemplateCollectionKeyException instanceof
					LayoutPageTemplateCollectionLayoutPageTemplateCollectionKeyException.MustNotContainInvalidCharacters) {

			return StringBundler.concat(
				"The key \"", key, "\" of the ", name,
				" must contain only alphanumeric characters, dashes, and ",
				"underscores");
		}

		if (
				layoutPageTemplateCollectionLayoutPageTemplateCollectionKeyException instanceof
					LayoutPageTemplateCollectionLayoutPageTemplateCollectionKeyException.MustNotExceedMaximumSize
						mustNotExceedMaximumSize) {

			return StringBundler.concat(
				"The key \"", key, "\" of the ", name, " must not exceed ",
				mustNotExceedMaximumSize.
					getLayoutPageTemplateCollectionKeyMaxSize(),
				" characters");
		}

		return StringBundler.concat(
			"The key \"", key, "\" of the ", name, " is invalid");
	}

}