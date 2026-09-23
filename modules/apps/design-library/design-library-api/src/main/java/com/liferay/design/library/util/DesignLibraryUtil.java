/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.design.library.util;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalServiceUtil;
import com.liferay.design.library.constants.DesignLibraryAdminPortletKeys;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Lourdes Fernández Besada
 * @author Georgel Pop
 */
public class DesignLibraryUtil {

	public static long[] getConnectedDesignLibraryGroupIds(
			long companyId, long groupId)
		throws PortalException {

		if (!FeatureFlagManagerUtil.isEnabled(companyId, "LPD-57283")) {
			return new long[0];
		}

		return ListUtil.toLongArray(
			DepotEntryLocalServiceUtil.getGroupConnectedDepotEntries(
				groupId, DepotConstants.TYPE_DESIGN_LIBRARY, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS),
			DepotEntry::getGroupId);
	}

	public static String getDesignLibraryResourcesURL(
		Group depotGroup, HttpServletRequest httpServletRequest) {

		DepotEntry depotEntry = DepotEntryLocalServiceUtil.fetchGroupDepotEntry(
			depotGroup.getGroupId());

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
			long companyId, long designLibraryGroupId, long groupId)
		throws PortalException {

		return ArrayUtil.contains(
			getConnectedDesignLibraryGroupIds(companyId, groupId),
			designLibraryGroupId);
	}

	public static boolean isDesignLibraryScope(Group group) {
		if ((group == null) || !group.isDepot()) {
			return false;
		}

		return isDesignLibraryScope(group.getGroupId());
	}

	public static boolean isDesignLibraryScope(long groupId) {
		DepotEntry depotEntry = DepotEntryLocalServiceUtil.fetchGroupDepotEntry(
			groupId);

		if ((depotEntry == null) ||
			(depotEntry.getType() != DepotConstants.TYPE_DESIGN_LIBRARY)) {

			return false;
		}

		return true;
	}

}