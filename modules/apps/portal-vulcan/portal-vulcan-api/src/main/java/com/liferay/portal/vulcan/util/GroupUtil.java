/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.util;

import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.GetterUtil;

/**
 * @author Javier Gamarra
 */
public class GroupUtil {

	public static String getAssetLibraryKey(Group group) {
		if (group.isDepot()) {
			return group.getGroupKey();
		}

		return null;
	}

	public static Long getDepotGroupId(
		String assetLibraryKey, long companyId,
		DepotEntryLocalService depotEntryLocalService,
		GroupLocalService groupLocalService) {

		Group group = _getGroup(
			assetLibraryKey, companyId, depotEntryLocalService,
			groupLocalService);

		if (_checkGroup(group)) {
			return group.getGroupId();
		}

		return null;
	}

	public static Long getGroupId(
		long companyId, String siteKey, GroupLocalService groupLocalService) {

		Group group = groupLocalService.fetchGroup(companyId, siteKey);

		if (group == null) {
			group = groupLocalService.fetchGroup(GetterUtil.getLong(siteKey));
		}

		if (group == null) {
			group = groupLocalService.fetchGroupByExternalReferenceCode(
				siteKey, companyId);
		}

		if (_checkGroup(group)) {
			return group.getGroupId();
		}

		return null;
	}

	public static String getSiteExternalReferenceCode(Group group) {
		if (group.isDepot()) {
			return null;
		}

		return group.getExternalReferenceCode();
	}

	public static Long getSiteId(Group group) {
		if (group.isDepot()) {
			return null;
		}

		return group.getGroupId();
	}

	private static boolean _checkGroup(Group group) {
		if ((group != null) &&
			(_isDepotOrSite(group) || _isDepotOrSite(group.getLiveGroup()) ||
			 group.isUserGroup())) {

			return true;
		}

		return false;
	}

	private static Group _getGroup(
		String assetLibraryKey, long companyId,
		DepotEntryLocalService depotEntryLocalService,
		GroupLocalService groupLocalService) {

		Group group = groupLocalService.fetchGroup(companyId, assetLibraryKey);

		if (group != null) {
			return group;
		}

		long assetLibraryId = GetterUtil.getLong(assetLibraryKey);

		DepotEntry depotEntry = depotEntryLocalService.fetchDepotEntry(
			assetLibraryId);

		if (depotEntry != null) {
			Group depotEntryGroup = groupLocalService.fetchGroup(
				depotEntry.getGroupId());

			if (depotEntryGroup != null) {
				return depotEntryGroup;
			}
		}

		if (!FeatureFlagManagerUtil.isEnabled(companyId, "LPD-17564")) {
			return null;
		}

		return groupLocalService.fetchGroup(assetLibraryId);
	}

	private static boolean _isDepotOrSite(Group group) {
		if ((group != null) && (group.isDepot() || group.isSite())) {
			return true;
		}

		return false;
	}

}