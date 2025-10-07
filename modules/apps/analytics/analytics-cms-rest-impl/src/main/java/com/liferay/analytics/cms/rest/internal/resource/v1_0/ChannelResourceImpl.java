/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.internal.resource.v1_0;

import com.liferay.analytics.cms.rest.dto.v1_0.Channel;
import com.liferay.analytics.cms.rest.resource.v1_0.ChannelResource;
import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.pagination.Page;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Rachael Koestartyo
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/channel.properties",
	scope = ServiceScope.PROTOTYPE, service = ChannelResource.class
)
public class ChannelResourceImpl extends BaseChannelResourceImpl {

	public Page<Channel> getChannelsPage(String keywords) throws Exception {
		AnalyticsConfiguration analyticsConfiguration =
			_analyticsSettingsManager.getAnalyticsConfiguration(
				contextUser.getCompanyId());

		return Page.of(
			transformToList(
				analyticsConfiguration.syncedGroupIds(),
				syncedGroupId -> {
					long groupId = GetterUtil.getLong(syncedGroupId);

					if (!ArrayUtil.contains(
							contextUser.getGroupIds(),
							GetterUtil.getLong(groupId))) {

						return null;
					}

					Group group = groupLocalService.fetchGroup(groupId);

					if (group == null) {
						return null;
					}

					String name = group.getName(contextUser.getLocale());

					if (Validator.isNotNull(keywords) &&
						!name.contains(keywords)) {

						return null;
					}

					Channel channel = new Channel();

					channel.setGroupId(() -> groupId);
					channel.setName(() -> name);

					return channel;
				}));
	}

	@Reference
	private AnalyticsSettingsManager _analyticsSettingsManager;

}