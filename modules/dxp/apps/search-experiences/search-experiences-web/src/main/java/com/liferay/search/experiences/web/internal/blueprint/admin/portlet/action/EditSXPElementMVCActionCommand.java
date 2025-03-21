/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.web.internal.blueprint.admin.portlet.action;

import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.search.experiences.constants.SXPPortletKeys;
import com.liferay.search.experiences.exception.SXPElementReadOnlyException;
import com.liferay.search.experiences.service.SXPElementService;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Olivia Yu
 */
@Component(
	enabled = false,
	property = {
		"jakarta.portlet.name=" + SXPPortletKeys.SXP_BLUEPRINT_ADMIN,
		"mvc.command.name=/sxp_blueprint_admin/edit_sxp_element"
	},
	service = MVCActionCommand.class
)
public class EditSXPElementMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals(Constants.DELETE)) {
				_deleteSXPElements(actionRequest);
			}
		}
		catch (Exception exception) {
			if (exception instanceof SXPElementReadOnlyException) {
				hideDefaultErrorMessage(actionRequest);
			}

			SessionErrors.add(actionRequest, exception.getClass(), exception);

			String redirect = ParamUtil.getString(actionRequest, "redirect");

			sendRedirect(actionRequest, actionResponse, redirect);
		}
	}

	private void _deleteSXPElements(ActionRequest actionRequest)
		throws Exception {

		long[] deleteSXPElementIds = StringUtil.split(
			ParamUtil.getString(actionRequest, "id"), 0L);

		for (long deleteSXPElementId : deleteSXPElementIds) {
			_sxpElementService.deleteSXPElement(deleteSXPElementId);
		}
	}

	@Reference
	private SXPElementService _sxpElementService;

}