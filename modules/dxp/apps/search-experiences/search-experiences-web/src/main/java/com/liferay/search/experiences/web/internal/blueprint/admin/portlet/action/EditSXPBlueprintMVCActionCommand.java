/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.web.internal.blueprint.admin.portlet.action;

import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.search.experiences.constants.SXPPortletKeys;
import com.liferay.search.experiences.service.SXPBlueprintService;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Olivia Yu
 */
@Component(
	enabled = false,
	property = {
		"jakarta.portlet.name=" + SXPPortletKeys.SXP_BLUEPRINT_ADMIN,
		"mvc.command.name=/sxp_blueprint_admin/edit_sxp_blueprint"
	},
	service = MVCActionCommand.class
)
public class EditSXPBlueprintMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals(Constants.DELETE)) {
				_deleteSXPBlueprints(actionRequest);
			}
		}
		catch (Exception exception) {
			throw new PortletException(exception);
		}
	}

	private void _deleteSXPBlueprints(ActionRequest actionRequest)
		throws Exception {

		long[] deleteSXPBlueprintIds = StringUtil.split(
			ParamUtil.getString(actionRequest, "id"), 0L);

		for (long deleteSXPBlueprintId : deleteSXPBlueprintIds) {
			_sxpBlueprintService.deleteSXPBlueprint(deleteSXPBlueprintId);
		}
	}

	@Reference
	private SXPBlueprintService _sxpBlueprintService;

}