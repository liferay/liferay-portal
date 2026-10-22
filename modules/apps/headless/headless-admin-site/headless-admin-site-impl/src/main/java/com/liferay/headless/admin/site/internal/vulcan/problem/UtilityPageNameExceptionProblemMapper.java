/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.vulcan.problem;

import com.liferay.layout.utility.page.exception.LayoutUtilityPageEntryNameException;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.vulcan.problem.Problem;
import com.liferay.portal.vulcan.problem.ProblemMapper;

import org.osgi.service.component.annotations.Component;

/**
 * @author Javier Moral
 */
@Component(service = ProblemMapper.class)
public class UtilityPageNameExceptionProblemMapper
	implements ProblemMapper<LayoutUtilityPageEntryNameException> {

	@Override
	public Problem getProblem(
		LayoutUtilityPageEntryNameException
			layoutUtilityPageEntryNameException) {

		return ProblemUtil.getProblem(
			_getMessage(layoutUtilityPageEntryNameException),
			Problem.Status.BAD_REQUEST, layoutUtilityPageEntryNameException);
	}

	private String _getMessage(
		LayoutUtilityPageEntryNameException
			layoutUtilityPageEntryNameException) {

		if (layoutUtilityPageEntryNameException instanceof
				LayoutUtilityPageEntryNameException.MustNotBeNull) {

			return "A name is required for the utility page";
		}

		if (layoutUtilityPageEntryNameException instanceof
				LayoutUtilityPageEntryNameException.
					MustNotContainInvalidCharacters
						mustNotContainInvalidCharacters) {

			return StringBundler.concat(
				"The name \"", mustNotContainInvalidCharacters.name,
				"\" of the utility page contains an invalid character: \"",
				mustNotContainInvalidCharacters.character, "\"");
		}

		if (layoutUtilityPageEntryNameException instanceof
				LayoutUtilityPageEntryNameException.MustNotExceedMaximumSize
					mustNotExceedMaximumSize) {

			return StringBundler.concat(
				"The name \"", mustNotExceedMaximumSize.name,
				"\" of the utility page must not exceed ",
				mustNotExceedMaximumSize.maxLength, " characters");
		}

		return "The name of the utility page is invalid";
	}

}