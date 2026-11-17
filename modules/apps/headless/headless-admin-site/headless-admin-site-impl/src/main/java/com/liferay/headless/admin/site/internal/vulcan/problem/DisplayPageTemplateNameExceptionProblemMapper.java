/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.vulcan.problem;

import com.liferay.layout.page.template.exception.LayoutPageTemplateEntryNameException;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.vulcan.problem.Problem;
import com.liferay.portal.vulcan.problem.ProblemMapper;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Javier Moral
 */
@Component(service = ProblemMapper.class)
public class DisplayPageTemplateNameExceptionProblemMapper
	implements ProblemMapper<LayoutPageTemplateEntryNameException> {

	@Override
	public Problem getProblem(
		LayoutPageTemplateEntryNameException
			layoutPageTemplateEntryNameException) {

		return ProblemUtil.getProblem(
			_getMessage(
				layoutPageTemplateEntryNameException,
				LocaleUtil.getMostRelevantLocale()),
			_getStatus(layoutPageTemplateEntryNameException),
			layoutPageTemplateEntryNameException);
	}

	private String _getMessage(
		LayoutPageTemplateEntryNameException
			layoutPageTemplateEntryNameException,
		Locale locale) {

		if (layoutPageTemplateEntryNameException instanceof
				LayoutPageTemplateEntryNameException.MustNotBeDuplicate) {

			return _language.get(
				locale, "a-page-template-entry-with-that-name-already-exists");
		}

		if (layoutPageTemplateEntryNameException instanceof
				LayoutPageTemplateEntryNameException.MustNotBeNull) {

			return _language.get(locale, "name-must-not-be-empty");
		}

		if (layoutPageTemplateEntryNameException instanceof
				LayoutPageTemplateEntryNameException.
					MustNotContainInvalidCharacters
						mustNotContainInvalidCharacters) {

			return _language.format(
				locale, "name-cannot-contain-the-following-invalid-character-x",
				mustNotContainInvalidCharacters.character);
		}

		if (layoutPageTemplateEntryNameException instanceof
				LayoutPageTemplateEntryNameException.MustNotExceedMaximumSize) {

			return _language.format(
				locale, "please-enter-a-name-with-fewer-than-x-characters",
				ModelHintsUtil.getMaxLength(
					LayoutPageTemplateEntry.class.getName(), "name"));
		}

		return layoutPageTemplateEntryNameException.getMessage();
	}

	private Problem.Status _getStatus(
		LayoutPageTemplateEntryNameException
			layoutPageTemplateEntryNameException) {

		if (layoutPageTemplateEntryNameException instanceof
				LayoutPageTemplateEntryNameException.MustNotBeDuplicate) {

			return Problem.Status.CONFLICT;
		}

		return Problem.Status.BAD_REQUEST;
	}

	@Reference
	private Language _language;

}