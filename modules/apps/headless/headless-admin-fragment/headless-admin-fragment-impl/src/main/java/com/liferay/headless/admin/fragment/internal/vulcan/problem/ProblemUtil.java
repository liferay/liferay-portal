/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.internal.vulcan.problem;

import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.vulcan.problem.Problem;

import java.util.Locale;
import java.util.function.Function;

/**
 * @author Lourdes Fernández Besada
 */
public class ProblemUtil {

	public static Problem getProblem(
		Function<Locale, String> detailFunction, Problem.Status status,
		Throwable throwable, Function<Locale, String> titleFunction) {

		return new Problem() {

			@Override
			public String getDetail(Locale locale) {
				return detailFunction.apply(locale);
			}

			@Override
			public Status getStatus() {
				return status;
			}

			@Override
			public String getTitle(Locale locale) {
				return titleFunction.apply(locale);
			}

			@Override
			public String getType() {
				Class<? extends Throwable> throwableClass =
					throwable.getClass();

				return throwableClass.getName();
			}

		};
	}

	public static Problem getProblem(
		Problem.Status status, Throwable throwable,
		Function<Locale, String> titleFunction) {

		return getProblem(titleFunction, status, throwable, titleFunction);
	}

	public static Problem getProblem(
		Problem.Status status, Throwable throwable, String titleKey) {

		return getProblem(
			status, throwable, locale -> LanguageUtil.get(locale, titleKey));
	}

}