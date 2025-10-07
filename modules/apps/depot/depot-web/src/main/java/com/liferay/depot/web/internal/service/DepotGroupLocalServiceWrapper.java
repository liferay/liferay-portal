/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.service;

import com.liferay.depot.constants.DepotPortletKeys;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.auth.GuestOrUserUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.GroupServiceWrapper;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.Portal;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(service = ServiceWrapper.class)
public class DepotGroupLocalServiceWrapper extends GroupServiceWrapper {

	@Override
	public String getGroupDisplayURL(
			long groupId, boolean privateLayout, boolean secureConnection)
		throws PortalException {

		Group group = _groupLocalService.getGroup(groupId);

		if (group.isDepot()) {
			GroupPermissionUtil.check(
				GuestOrUserUtil.getPermissionChecker(), group,
				ActionKeys.UPDATE);

			String controlPanelFullURL = _portal.getControlPanelFullURL(
				groupId, DepotPortletKeys.DEPOT_ADMIN, null);

			DepotEntry depotEntry = _depotEntryLocalService.getGroupDepotEntry(
				group.getGroupId());

			String namespace = _portal.getPortletNamespace(
				DepotPortletKeys.DEPOT_ADMIN);

			controlPanelFullURL = HttpComponentsUtil.addParameter(
				controlPanelFullURL, namespace + "mvcRenderCommandName",
				"/depot/view_depot_dashboard");
			controlPanelFullURL = HttpComponentsUtil.addParameter(
				controlPanelFullURL, namespace + "depotEntryId",
				String.valueOf(depotEntry.getDepotEntryId()));

			return controlPanelFullURL;
		}

		return super.getGroupDisplayURL(
			groupId, privateLayout, secureConnection);
	}

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

}