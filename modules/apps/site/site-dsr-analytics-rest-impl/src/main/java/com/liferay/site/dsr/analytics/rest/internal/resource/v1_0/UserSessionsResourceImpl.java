/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.analytics.rest.internal.resource.v1_0;

import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.analytics.settings.rest.resource.v1_0.ChannelResource;
import com.liferay.portal.kernel.util.Http;
import com.liferay.site.dsr.analytics.rest.dto.v1_0.UserSessions;
import com.liferay.site.dsr.analytics.rest.internal.client.AnalyticsCloudClient;
import com.liferay.site.dsr.analytics.rest.resource.v1_0.UserSessionsResource;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Gianmarco Brunialti
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/user-sessions.properties",
	scope = ServiceScope.PROTOTYPE, service = UserSessionsResource.class
)
public class UserSessionsResourceImpl extends BaseUserSessionsResourceImpl {

	@Override
	public UserSessions getUserSessions(
			String entityId, String entityType, String[] groupIds,
			String keywords, Integer page, String rangeEnd, Integer rangeKey,
			String rangeStart, Integer size)
		throws Exception {

		AnalyticsCloudClient analyticsCloudClient = new AnalyticsCloudClient(
			_channelResourceFactory, _http, contextUser);

		return analyticsCloudClient.getUserSessions(
			_analyticsSettingsManager.getAnalyticsConfiguration(
				contextCompany.getCompanyId()),
			entityId, entityType, groupIds, keywords, page, rangeEnd, rangeKey,
			rangeStart, size);
	}

	@Reference
	private AnalyticsSettingsManager _analyticsSettingsManager;

	@Reference
	private volatile ChannelResource.Factory _channelResourceFactory;

	@Reference
	private Http _http;

}

// LIFERAY-REST-BUILDER-HASH:270762275