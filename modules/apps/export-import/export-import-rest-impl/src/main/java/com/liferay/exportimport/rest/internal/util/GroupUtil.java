/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.internal.util;

import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.staging.StagingGroupHelper;
import com.liferay.staging.StagingGroupHelperUtil;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

/**
 * @author Daniel Raposo
 */
public class GroupUtil {

	public static Group getAssetLibraryGroup(
		long companyId, String externalReferenceCode) {

		Group group = GroupLocalServiceUtil.fetchGroupByExternalReferenceCode(
			externalReferenceCode, companyId);

		if ((group == null) || !group.isDepot()) {
			throw new NotFoundException();
		}

		return group;
	}

	public static Group getCompanyGroup(long companyId) {
		StagingGroupHelper stagingGroupHelper =
			StagingGroupHelperUtil.getStagingGroupHelper();

		Group group = stagingGroupHelper.fetchCompanyGroup(companyId);

		if (group == null) {
			throw new NotFoundException();
		}

		return group;
	}

	public static Group getLiveGroup(Group stagingGroup) {
		StagingGroupHelper stagingGroupHelper =
			StagingGroupHelperUtil.getStagingGroupHelper();

		Group liveGroup = stagingGroupHelper.fetchLocalLiveGroup(stagingGroup);

		if (liveGroup == null) {
			throw new NotFoundException();
		}

		return liveGroup;
	}

	public static Group getSiteGroup(
		long companyId, String externalReferenceCode) {

		Group group = GroupLocalServiceUtil.fetchGroupByExternalReferenceCode(
			externalReferenceCode, companyId);

		if ((group == null) || (!group.isCMS() && !group.isSite())) {
			throw new NotFoundException();
		}

		return group;
	}

	public static Group getStagingGroup(Group group) {
		StagingGroupHelper stagingGroupHelper =
			StagingGroupHelperUtil.getStagingGroupHelper();

		if (stagingGroupHelper.isLocalStagingGroup(group)) {
			return group;
		}

		Group stagingGroup = stagingGroupHelper.fetchLocalStagingGroup(group);

		if (stagingGroup == null) {
			throw new BadRequestException(
				"Local staging is not enabled for site \"" +
					group.getExternalReferenceCode() + "\"");
		}

		return stagingGroup;
	}

}