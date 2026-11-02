/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.message.storage.service.impl;

import com.liferay.analytics.message.storage.model.AnalyticsMessage;
import com.liferay.analytics.message.storage.service.base.AnalyticsMessageLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;

import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Rachael Koestartyo
 */
@Component(
	property = "model.class.name=com.liferay.analytics.message.storage.model.AnalyticsMessage",
	service = AopService.class
)
public class AnalyticsMessageLocalServiceImpl
	extends AnalyticsMessageLocalServiceBaseImpl {

	@Override
	public void deleteAnalyticsMessages(
		List<AnalyticsMessage> analyticsMessages) {

		for (AnalyticsMessage analyticsMessage : analyticsMessages) {
			analyticsMessagePersistence.remove(analyticsMessage);
		}
	}

	@Override
	public void deleteAnalyticsMessages(long companyId) {
		analyticsMessagePersistence.removeByCompanyId(companyId);
	}

}