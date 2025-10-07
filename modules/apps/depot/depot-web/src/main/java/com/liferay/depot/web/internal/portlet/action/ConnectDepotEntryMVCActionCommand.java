/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.portlet.action;

import com.liferay.depot.constants.DepotPortletKeys;
import com.liferay.depot.exception.DepotEntryGroupException;
import com.liferay.depot.exception.DepotEntryGroupRelToGroupException;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryGroupRelService;
import com.liferay.depot.service.DepotEntryService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.GroupService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DepotPortletKeys.DEPOT_ADMIN,
		"jakarta.portlet.name=" + DepotPortletKeys.DEPOT_SETTINGS,
		"mvc.command.name=/depot/connect_depot_entry"
	},
	service = MVCActionCommand.class
)
public class ConnectDepotEntryMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws IOException, PortalException {

		long depotEntryId = ParamUtil.getLong(actionRequest, "depotEntryId");
		long toGroupId = ParamUtil.getLong(actionRequest, "toGroupId");

		try {
			DepotEntry depotEntry = _depotEntryService.getDepotEntry(
				depotEntryId);

			Group depotGroup = depotEntry.getGroup();

			Group toGroup = _groupService.getGroup(toGroupId);

			if (!depotGroup.isStaged() && toGroup.isStaged()) {
				throw new DepotEntryGroupRelToGroupException.MustNotBeStaged();
			}
			else if (depotGroup.isStaged()) {
				if (!toGroup.isStaged()) {
					throw new DepotEntryGroupRelToGroupException.MustBeStaged();
				}

				if (depotGroup.isStagedRemotely() &&
					!toGroup.isStagedRemotely()) {

					throw new DepotEntryGroupRelToGroupException.
						MustBeRemotelyStaged();
				}

				if (!depotGroup.isStagedRemotely() &&
					toGroup.isStagedRemotely()) {

					throw new DepotEntryGroupRelToGroupException.
						MustBeLocallyStaged();
				}
			}

			if (toGroup.isStaged()) {
				Group stagingToGroup = toGroup.getStagingGroup();

				if (stagingToGroup != null) {
					toGroupId = stagingToGroup.getGroupId();
				}
			}

			_depotEntryGroupRelService.addDepotEntryGroupRel(
				depotEntryId, toGroupId);
		}
		catch (DepotEntryGroupException depotEntryGroupException) {
			SessionErrors.add(
				actionRequest, depotEntryGroupException.getClass(),
				depotEntryGroupException);

			hideDefaultErrorMessage(actionRequest);

			actionResponse.sendRedirect(
				ParamUtil.getString(actionRequest, "redirect"));
		}
	}

	@Reference
	private DepotEntryGroupRelService _depotEntryGroupRelService;

	@Reference
	private DepotEntryService _depotEntryService;

	@Reference
	private GroupService _groupService;

}