/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.util;

import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
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

		Group group = groupLocalService.fetchGroup(
			Long.valueOf(assetLibraryKey));

		if (group == null) {
			group = groupLocalService.fetchGroup(companyId, assetLibraryKey);

			if (group == null) {
				try {
					DepotEntry depotEntry =
						depotEntryLocalService.fetchDepotEntry(
							GetterUtil.getLong(assetLibraryKey));

					if (depotEntry == null) {
						return null;
					}

					group = depotEntry.getGroup();
				}
				catch (PortalException portalException) {
					if (_log.isDebugEnabled()) {
						_log.debug(portalException);
					}

					return null;
				}
			}
		}

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
		if (_isDepotOrSite(group) ||
			((group != null) && _isDepotOrSite(group.getLiveGroup()))) {

			return true;
		}

		return false;
	}

	private static boolean _isDepotOrSite(Group group) {
		if ((group != null) && (group.isDepot() || group.isSite())) {
			return true;
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(GroupUtil.class);

}