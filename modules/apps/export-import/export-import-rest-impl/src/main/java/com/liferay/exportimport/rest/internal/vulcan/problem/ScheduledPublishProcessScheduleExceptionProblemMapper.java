/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.internal.vulcan.problem;

import com.liferay.exportimport.rest.internal.resource.exception.ScheduledPublishProcessScheduleException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.vulcan.problem.Problem;
import com.liferay.portal.vulcan.problem.ProblemMapper;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Magdalena Jedraszak
 */
@Component(service = ProblemMapper.class)
public class ScheduledPublishProcessScheduleExceptionProblemMapper
	implements ProblemMapper<ScheduledPublishProcessScheduleException> {

	@Override
	public Problem getProblem(
		ScheduledPublishProcessScheduleException
			scheduledPublishProcessScheduleException) {

		if (scheduledPublishProcessScheduleException instanceof
				ScheduledPublishProcessScheduleException.MustRunAtLeastOnce) {

			return _getProblem(
				"the-scheduled-process-will-never-run",
				scheduledPublishProcessScheduleException);
		}

		return _getProblem(
			"please-enter-a-valid-cron-expression",
			scheduledPublishProcessScheduleException);
	}

	private Problem _getProblem(
		String key,
		ScheduledPublishProcessScheduleException
			scheduledPublishProcessScheduleException) {

		return new Problem() {

			@Override
			public String getDetail(Locale locale) {
				return _language.get(locale, key);
			}

			@Override
			public Status getStatus() {
				return Status.BAD_REQUEST;
			}

			@Override
			public String getTitle(Locale locale) {
				return _language.get(locale, key);
			}

			@Override
			public String getType() {
				Class<?> clazz =
					scheduledPublishProcessScheduleException.getClass();

				return clazz.getName();
			}

		};
	}

	@Reference
	private Language _language;

}