/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.sales.room.web.internal.background.task;

import com.liferay.analytics.settings.rest.dto.v1_0.Channel;
import com.liferay.analytics.settings.rest.dto.v1_0.DataSource;
import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.analytics.settings.rest.resource.v1_0.ChannelResource;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskExecutor;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskResult;
import com.liferay.portal.kernel.backgroundtask.BaseBackgroundTaskExecutor;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.backgroundtask.display.BackgroundTaskDisplay;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.GroupModel;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Andrea Sbarra
 */
@Component(
	property = "background.task.executor.class.name=com.liferay.digital.sales.room.web.internal.background.task.DSRAnalyticsChannelBackgroundTaskExecutor",
	service = BackgroundTaskExecutor.class
)
public class DSRAnalyticsChannelBackgroundTaskExecutor
	extends BaseBackgroundTaskExecutor {

	public DSRAnalyticsChannelBackgroundTaskExecutor() {
		setIsolationLevel(BackgroundTaskConstants.ISOLATION_LEVEL_COMPANY);
	}

	@Override
	public BackgroundTaskExecutor clone() {
		return this;
	}

	@Override
	public BackgroundTaskResult execute(BackgroundTask backgroundTask)
		throws Exception {

		if (!_analyticsSettingsManager.isAnalyticsEnabled(
				backgroundTask.getCompanyId())) {

			return BackgroundTaskResult.SUCCESS;
		}

		Channel channel = new Channel();

		ChannelResource channelResource = _channelResourceFactory.create(
		).checkPermissions(
			false
		).user(
			_userLocalService.getUser(backgroundTask.getUserId())
		).build();

		Channel analyticsChannel = _getOrAddAnalyticsChannel(channelResource);

		channel.setChannelId(analyticsChannel::getChannelId);

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				MapUtil.getLong(
					backgroundTask.getTaskContextMap(), "objectDefinitionId"));

		DataSource dataSource = new DataSource();

		dataSource.setSiteIds(
			() -> TransformUtil.transformToArray(
				_groupLocalService.getGroups(
					backgroundTask.getCompanyId(),
					objectDefinition.getClassName(),
					GroupConstants.DEFAULT_PARENT_GROUP_ID),
				GroupModel::getGroupId, Long.class));

		channel.setDataSources(() -> new DataSource[] {dataSource});

		channelResource.patchChannel(channel);

		return BackgroundTaskResult.SUCCESS;
	}

	@Override
	public BackgroundTaskDisplay getBackgroundTaskDisplay(
		BackgroundTask backgroundTask) {

		return null;
	}

	private Channel _getOrAddAnalyticsChannel(ChannelResource channelResource)
		throws Exception {

		Page<Channel> channelsPage = channelResource.getChannelsPage(
			_DSR_CHANNEL_NAME, Pagination.of(1, 1), null);

		List<Channel> channels = ListUtil.fromCollection(
			channelsPage.getItems());

		if (!channels.isEmpty()) {
			return channels.get(0);
		}

		Channel channel = new Channel();

		channel.setName(() -> _DSR_CHANNEL_NAME);

		return channelResource.postChannel(channel);
	}

	private static final String _DSR_CHANNEL_NAME = "DSR";

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	private volatile AnalyticsSettingsManager _analyticsSettingsManager;

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	private volatile ChannelResource.Factory _channelResourceFactory;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private UserLocalService _userLocalService;

}