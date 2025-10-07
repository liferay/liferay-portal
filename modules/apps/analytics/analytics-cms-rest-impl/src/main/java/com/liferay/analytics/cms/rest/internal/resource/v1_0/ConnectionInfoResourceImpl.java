/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.internal.resource.v1_0;

import com.liferay.analytics.cms.rest.dto.v1_0.ConnectionInfo;
import com.liferay.analytics.cms.rest.resource.v1_0.ConnectionInfoResource;
import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.depot.model.DepotEntryGroupRelModel;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Rachael Koestartyo
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/connection-info.properties",
	scope = ServiceScope.PROTOTYPE, service = ConnectionInfoResource.class
)
public class ConnectionInfoResourceImpl extends BaseConnectionInfoResourceImpl {

	@Override
	public ConnectionInfo getConnectionInfo(Long depotEntryGroupId)
		throws Exception {

		AnalyticsConfiguration analyticsConfiguration =
			_analyticsSettingsManager.getAnalyticsConfiguration(
				contextUser.getCompanyId());

		List<Long> groupIds = transform(
			_depotEntryGroupRelLocalService.getDepotEntryGroupRels(
				_depotEntryLocalService.getGroupDepotEntry(depotEntryGroupId)),
			DepotEntryGroupRelModel::getToGroupId);

		return _toConnectionInfo(
			roleLocalService.hasUserRole(
				contextUser.getUserId(), contextUser.getCompanyId(),
				RoleConstants.ADMINISTRATOR, true),
			!Validator.isBlank(analyticsConfiguration.token()),
			!groupIds.isEmpty(),
			_hasSitesSyncedToAnalyticsCloud(
				analyticsConfiguration.syncedGroupIds(), groupIds));
	}

	private boolean _hasSitesSyncedToAnalyticsCloud(
		String[] analyticsCloudSyncedGroupIds, List<Long> groupIds) {

		for (long groupId : groupIds) {
			if (ArrayUtil.contains(
					analyticsCloudSyncedGroupIds, String.valueOf(groupId))) {

				return true;
			}
		}

		return false;
	}

	private ConnectionInfo _toConnectionInfo(
		boolean admin, boolean connectedToAnalyticsCloud,
		boolean connectedToSpace, boolean siteSyncedToAnalyticsCloud) {

		ConnectionInfo connectionInfo = new ConnectionInfo();

		connectionInfo.setAdmin(() -> admin);
		connectionInfo.setConnectedToAnalyticsCloud(
			() -> connectedToAnalyticsCloud);
		connectionInfo.setConnectedToSpace(() -> connectedToSpace);
		connectionInfo.setSiteSyncedToAnalyticsCloud(
			() -> siteSyncedToAnalyticsCloud);

		return connectionInfo;
	}

	@Reference
	private AnalyticsSettingsManager _analyticsSettingsManager;

	@Reference
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

}