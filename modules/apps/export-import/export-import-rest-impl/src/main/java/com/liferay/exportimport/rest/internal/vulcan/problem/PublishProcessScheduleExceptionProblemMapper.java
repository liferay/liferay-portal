/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.internal.vulcan.problem;

import com.liferay.exportimport.rest.internal.resource.exception.PublishProcessScheduleException;
import com.liferay.portal.vulcan.problem.Problem;
import com.liferay.portal.vulcan.problem.ProblemMapper;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;

/**
 * @author Magdalena Jedraszak
 */
@Component(service = ProblemMapper.class)
public class PublishProcessScheduleExceptionProblemMapper
	implements ProblemMapper<PublishProcessScheduleException> {

	@Override
	public Problem getProblem(
		PublishProcessScheduleException publishProcessScheduleException) {

		return new Problem() {

			@Override
			public String getDetail(Locale locale) {
				return publishProcessScheduleException.getMessage();
			}

			@Override
			public Status getStatus() {
				return Status.BAD_REQUEST;
			}

			@Override
			public String getTitle(Locale locale) {
				return publishProcessScheduleException.getMessage();
			}

			@Override
			public String getType() {
				Class<?> clazz = publishProcessScheduleException.getClass();

				return clazz.getName();
			}

		};
	}

}