/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.staging;

import com.liferay.portal.kernel.model.Group;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Akos Thurzo
 */
@ProviderType
public interface StagingGroupHelper {

	public Group fetchCompanyGroup(long companyId);

	public Group fetchLiveGroup(Group group);

	public Group fetchLiveGroup(long groupId);

	public Group fetchLocalLiveGroup(Group group);

	public Group fetchLocalLiveGroup(long groupId);

	public Group fetchLocalStagingGroup(Group group);

	public Group fetchLocalStagingGroup(long groupId);

	public Group fetchRemoteLiveGroup(Group group);

	public Group fetchRemoteLiveGroup(long groupId);

	public Group getStagedPortletGroup(Group group, String portletId);

	public long getStagedPortletGroupId(long groupId, String portletId);

	public boolean isCompanyGroup(Group group);

	public boolean isCompanyGroup(long companyId, long groupId);

	public boolean isCompanyGroupFriendlyURL(String friendlyURL);

	public boolean isDepotGroup(long groupId);

	public boolean isLiveGroup(Group group);

	public boolean isLiveGroup(long groupId);

	public boolean isLocalLiveGroup(Group group);

	public boolean isLocalLiveGroup(long groupId);

	public boolean isLocalStagingGroup(Group group);

	public boolean isLocalStagingGroup(long groupId);

	public boolean isLocalStagingOrLocalLiveGroup(Group group);

	public boolean isLocalStagingOrLocalLiveGroup(long groupId);

	public boolean isRemoteLiveGroup(Group group);

	public boolean isRemoteLiveGroup(long groupId);

	public boolean isRemoteStagingGroup(Group group);

	public boolean isRemoteStagingGroup(long groupId);

	public boolean isRemoteStagingOrRemoteLiveGroup(Group group);

	public boolean isRemoteStagingOrRemoteLiveGroup(long groupId);

	public boolean isStagedPortlet(Group group, String portletId);

	public boolean isStagedPortlet(long groupId, String portletId);

	public boolean isStagedPortletData(long groupId, String className);

	public boolean isStagingGroup(Group group);

	public boolean isStagingGroup(long groupId);

	public boolean isStagingOrLiveGroup(Group group);

	public boolean isStagingOrLiveGroup(long groupId);

}