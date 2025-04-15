/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.portlet.action;

import com.liferay.depot.service.DepotEntryGroupRelService;
import com.liferay.depot.web.internal.constants.DepotPortletKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DepotPortletKeys.DEPOT_ADMIN,
		"jakarta.portlet.name=" + DepotPortletKeys.DEPOT_SETTINGS,
		"mvc.command.name=/depot/update_ddm_structures_available"
	},
	service = MVCActionCommand.class
)
public class UpdateDDMStructuresAvailableMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws PortletException {

		try {
			_depotEntryGroupRelService.updateDDMStructuresAvailable(
				ParamUtil.getLong(actionRequest, "depotEntryGroupRelId"),
				ParamUtil.getBoolean(actionRequest, "ddmStructuresAvailable"));
		}
		catch (PortalException portalException) {
			throw new PortletException(portalException);
		}
	}

	@Reference
	private DepotEntryGroupRelService _depotEntryGroupRelService;

}