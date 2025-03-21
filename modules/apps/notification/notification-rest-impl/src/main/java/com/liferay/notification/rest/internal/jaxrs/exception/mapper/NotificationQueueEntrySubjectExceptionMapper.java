/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.rest.internal.jaxrs.exception.mapper;

import com.liferay.notification.exception.NotificationQueueEntrySubjectException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Murilo Stodolni
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.Notification.REST)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.Notification.REST.NotificationQueueEntrySubjectExceptionMapper"
	},
	service = ExceptionMapper.class
)
public class NotificationQueueEntrySubjectExceptionMapper
	extends BaseExceptionMapper<NotificationQueueEntrySubjectException> {

	@Override
	protected Problem getProblem(
		NotificationQueueEntrySubjectException
			notificationQueueEntrySubjectException) {

		return new Problem(
			Response.Status.BAD_REQUEST,
			_language.get(
				_acceptLanguage.getPreferredLocale(), "subject-is-required"));
	}

	@Context
	private AcceptLanguage _acceptLanguage;

	@Reference
	private Language _language;

}