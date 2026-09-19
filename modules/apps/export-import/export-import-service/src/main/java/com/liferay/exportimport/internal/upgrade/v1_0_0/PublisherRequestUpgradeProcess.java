/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.upgrade.v1_0_0;

import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactoryUtil;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.lar.ExportImportHelperUtil;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalService;
import com.liferay.exportimport.kernel.staging.StagingUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.scheduler.SchedulerEngineHelper;
import com.liferay.portal.kernel.scheduler.StorageType;
import com.liferay.portal.kernel.scheduler.messaging.SchedulerResponse;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.LoggingTimer;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * @author Daniel Kocsis
 */
public class PublisherRequestUpgradeProcess extends UpgradeProcess {

	public PublisherRequestUpgradeProcess(
		ExportImportConfigurationLocalService
			exportImportConfigurationLocalService,
		GroupLocalService groupLocalService,
		SchedulerEngineHelper schedulerEngineHelper,
		UserLocalService userLocalService) {

		_exportImportConfigurationLocalService =
			exportImportConfigurationLocalService;
		_groupLocalService = groupLocalService;
		_schedulerEngineHelper = schedulerEngineHelper;
		_userLocalService = userLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		List<Group> groups = _groupLocalService.getStagedSites();

		for (Group group : groups) {
			_updateScheduledPublications(group);
		}
	}

	private String _getSchedulerGroupName(long groupId, boolean localStaging)
		throws PortalException {

		String destinationName = DestinationNames.LAYOUTS_LOCAL_PUBLISHER;

		if (!localStaging) {
			destinationName = DestinationNames.LAYOUTS_REMOTE_PUBLISHER;
		}

		return StagingUtil.getSchedulerGroupName(destinationName, groupId);
	}

	private void _updateScheduleRemotePublication(
			SchedulerResponse schedulerResponse)
		throws PortalException {

		Message message = schedulerResponse.getMessage();

		LayoutsRemotePublisherRequest publisherRequest =
			(LayoutsRemotePublisherRequest)message.getPayload();

		User user = _userLocalService.getUser(publisherRequest.getUserId());

		Map<String, Serializable> publishLayoutRemoteSettingsMap =
			ExportImportConfigurationSettingsMapFactoryUtil.
				buildPublishLayoutRemoteSettingsMap(
					user, publisherRequest.getSourceGroupId(),
					publisherRequest.isPrivateLayout(),
					publisherRequest.getLayoutIdMap(),
					publisherRequest.getParameterMap(),
					publisherRequest.getRemoteAddress(),
					publisherRequest.getRemotePort(),
					publisherRequest.getRemotePathContext(),
					publisherRequest.isSecureConnection(),
					publisherRequest.getRemoteGroupId(),
					publisherRequest.isRemotePrivateLayout());

		ExportImportConfiguration exportImportConfiguration =
			_exportImportConfigurationLocalService.
				addDraftExportImportConfiguration(
					user.getUserId(), schedulerResponse.getDescription(),
					ExportImportConfigurationConstants.
						TYPE_SCHEDULED_PUBLISH_LAYOUT_REMOTE,
					publishLayoutRemoteSettingsMap);

		_schedulerEngineHelper.schedule(
			schedulerResponse.getTrigger(), StorageType.PERSISTED,
			schedulerResponse.getDescription(),
			DestinationNames.LAYOUTS_REMOTE_PUBLISHER,
			exportImportConfiguration.getExportImportConfigurationId());
	}

	private void _updateScheduledLocalPublication(
			SchedulerResponse schedulerResponse)
		throws PortalException {

		Message message = schedulerResponse.getMessage();

		LayoutsLocalPublisherRequest publisherRequest =
			(LayoutsLocalPublisherRequest)message.getPayload();

		User user = _userLocalService.getUser(publisherRequest.getUserId());

		Map<String, Serializable> publishLayoutLocalSettingsMap =
			ExportImportConfigurationSettingsMapFactoryUtil.
				buildPublishLayoutLocalSettingsMap(
					user, publisherRequest.getSourceGroupId(),
					publisherRequest.getTargetGroupId(),
					publisherRequest.isPrivateLayout(),
					ExportImportHelperUtil.getLayoutIds(
						publisherRequest.getLayoutIdMap()),
					publisherRequest.getParameterMap());

		ExportImportConfiguration exportImportConfiguration =
			_exportImportConfigurationLocalService.
				addDraftExportImportConfiguration(
					user.getUserId(), schedulerResponse.getDescription(),
					ExportImportConfigurationConstants.
						TYPE_SCHEDULED_PUBLISH_LAYOUT_LOCAL,
					publishLayoutLocalSettingsMap);

		_schedulerEngineHelper.schedule(
			schedulerResponse.getTrigger(), StorageType.PERSISTED,
			schedulerResponse.getDescription(),
			DestinationNames.LAYOUTS_LOCAL_PUBLISHER,
			exportImportConfiguration.getExportImportConfigurationId());
	}

	private void _updateScheduledPublications(Group group) throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer(
				String.valueOf(group.getGroupId()))) {

			boolean localStaging = true;

			if (group.isStagedRemotely() || group.hasRemoteStagingGroup()) {
				localStaging = false;
			}

			List<SchedulerResponse> schedulerResponses =
				_schedulerEngineHelper.getScheduledJobs(
					_getSchedulerGroupName(group.getGroupId(), localStaging),
					StorageType.PERSISTED);

			for (SchedulerResponse schedulerResponse : schedulerResponses) {
				if (localStaging) {
					_updateScheduledLocalPublication(schedulerResponse);
				}
				else {
					_updateScheduleRemotePublication(schedulerResponse);
				}
			}
		}
	}

	private final ExportImportConfigurationLocalService
		_exportImportConfigurationLocalService;
	private final GroupLocalService _groupLocalService;
	private final SchedulerEngineHelper _schedulerEngineHelper;
	private final UserLocalService _userLocalService;

}