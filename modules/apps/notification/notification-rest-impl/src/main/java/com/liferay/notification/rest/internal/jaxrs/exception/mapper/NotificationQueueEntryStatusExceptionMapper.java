/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.rest.internal.jaxrs.exception.mapper;

import com.liferay.notification.exception.NotificationQueueEntryStatusException;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.ext.ExceptionMapper;

import org.osgi.service.component.annotations.Component;

/**
 * @author Pedro Tavares
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.Notification.REST)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.Notification.REST.NotificationQueueEntryStatusExceptionMapper"
	},
	service = ExceptionMapper.class
)
public class NotificationQueueEntryStatusExceptionMapper
	extends BaseExceptionMapper<NotificationQueueEntryStatusException> {

	@Override
	protected Problem getProblem(
		NotificationQueueEntryStatusException
			notificationQueueEntryStatusException) {

		return new Problem(notificationQueueEntryStatusException);
	}

}