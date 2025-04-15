/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.rest.internal.jaxrs.exception.mapper;

import com.liferay.notification.exception.NotificationRecipientSettingNameException;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.ws.rs.ext.ExceptionMapper;

import org.osgi.service.component.annotations.Component;

/**
 * @author Leilany Ulisses
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.Notification.REST)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.Notification.REST.NotificationRecipientSettingNameExceptionMapper"
	},
	service = ExceptionMapper.class
)
public class NotificationRecipientSettingNameExceptionMapper
	extends BaseExceptionMapper<NotificationRecipientSettingNameException> {

	@Override
	protected Problem getProblem(
		NotificationRecipientSettingNameException
			notificationRecipientSettingNameException) {

		return new Problem(notificationRecipientSettingNameException);
	}

}