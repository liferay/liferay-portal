/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.internal.util;

import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.staging.StagingGroupHelper;
import com.liferay.staging.StagingGroupHelperUtil;

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

	public static Group getSiteGroup(
		long companyId, String externalReferenceCode) {

		Group group = GroupLocalServiceUtil.fetchGroupByExternalReferenceCode(
			externalReferenceCode, companyId);

		if ((group == null) || (!group.isCMS() && !group.isSite())) {
			throw new NotFoundException();
		}

		return group;
	}

}