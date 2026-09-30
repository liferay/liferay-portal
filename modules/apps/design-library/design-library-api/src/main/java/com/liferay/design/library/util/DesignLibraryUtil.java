/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.design.library.util;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.model.DepotEntryGroupRel;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.design.library.constants.DesignLibraryAdminPortletKeys;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Lourdes Fernández Besada
 * @author Georgel Pop
 * @author Javier Moral
 */
public class DesignLibraryUtil {

	public static long[] fetchConnectedDesignLibraryGroupIds(long groupId) {
		try {
			return getConnectedDesignLibraryGroupIds(groupId);
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portalException);
			}

			return new long[0];
		}
	}

	public static long[] getConnectedDesignLibraryGroupIds(long groupId)
		throws PortalException {

		Group group = GroupLocalServiceUtil.getGroup(groupId);

		if (!FeatureFlagManagerUtil.isEnabled(
				group.getCompanyId(), "LPD-57283")) {

			return new long[0];
		}

		DepotEntryLocalService depotEntryLocalService =
			_depotEntryLocalServiceSnapshot.get();

		if (depotEntryLocalService == null) {
			return new long[0];
		}

		return ListUtil.toLongArray(
			depotEntryLocalService.getGroupConnectedDepotEntries(
				groupId, DepotConstants.TYPE_DESIGN_LIBRARY, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS),
			DepotEntry::getGroupId);
	}

	public static String getDesignLibraryResourcesURL(
		Group depotGroup, HttpServletRequest httpServletRequest) {

		DepotEntry depotEntry = _fetchGroupDepotEntry(depotGroup.getGroupId());

		if (depotEntry == null) {
			return null;
		}

		return PortletURLBuilder.create(
			PortalUtil.getControlPanelPortletURL(
				httpServletRequest, depotGroup,
				DesignLibraryAdminPortletKeys.DESIGN_LIBRARY_ADMIN, 0, 0,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/design_library/view_resources_design_library"
		).setParameter(
			"designLibraryEntryId", depotEntry.getDepotEntryId()
		).buildString();
	}

	public static boolean isConnectedDesignLibraryGroupId(
		long designLibraryGroupId, long groupId) {

		if (!isDesignLibraryScope(designLibraryGroupId)) {
			return false;
		}

		DepotEntryGroupRelLocalService depotEntryGroupRelLocalService =
			_depotEntryGroupRelLocalServiceSnapshot.get();

		if (depotEntryGroupRelLocalService == null) {
			return false;
		}

		DepotEntry depotEntry = _fetchGroupDepotEntry(designLibraryGroupId);

		DepotEntryGroupRel depotEntryGroupRel =
			depotEntryGroupRelLocalService.
				fetchDepotEntryGroupRelByDepotEntryIdToGroupId(
					depotEntry.getDepotEntryId(), groupId);

		if (depotEntryGroupRel != null) {
			return true;
		}

		return false;
	}

	public static boolean isDesignLibraryScope(Group group) {
		if ((group == null) || !group.isDepot()) {
			return false;
		}

		return isDesignLibraryScope(group.getGroupId());
	}

	public static boolean isDesignLibraryScope(long groupId) {
		DepotEntry depotEntry = _fetchGroupDepotEntry(groupId);

		if ((depotEntry == null) ||
			(depotEntry.getType() != DepotConstants.TYPE_DESIGN_LIBRARY)) {

			return false;
		}

		return FeatureFlagManagerUtil.isEnabled(
			depotEntry.getCompanyId(), "LPD-57283");
	}

	private static DepotEntry _fetchGroupDepotEntry(long groupId) {
		DepotEntryLocalService depotEntryLocalService =
			_depotEntryLocalServiceSnapshot.get();

		if (depotEntryLocalService == null) {
			return null;
		}

		return depotEntryLocalService.fetchGroupDepotEntry(groupId);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DesignLibraryUtil.class);

	private static final Snapshot<DepotEntryGroupRelLocalService>
		_depotEntryGroupRelLocalServiceSnapshot = new Snapshot<>(
			DesignLibraryUtil.class, DepotEntryGroupRelLocalService.class);
	private static final Snapshot<DepotEntryLocalService>
		_depotEntryLocalServiceSnapshot = new Snapshot<>(
			DesignLibraryUtil.class, DepotEntryLocalService.class);

}