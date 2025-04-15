/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.portlet.action;

import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.layout.admin.web.internal.handler.LayoutUtilityPageEntryPortalExceptionRequestHandlerUtil;
import com.liferay.layout.utility.page.service.LayoutUtilityPageEntryService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bárbara
 */
@Component(
	property = {
		"jakarta.portlet.name=" + LayoutAdminPortletKeys.GROUP_PAGES,
		"mvc.command.name=/layout_admin/update_layout_utility_page_entry"
	},
	service = MVCActionCommand.class
)
public class UpdateLayoutUtilityPageEntryMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long layoutUtilityPageEntryId = ParamUtil.getLong(
			actionRequest, "layoutUtilityPageEntryId");

		String name = ParamUtil.getString(actionRequest, "name");

		try {
			_layoutUtilityPageEntryService.updateLayoutUtilityPageEntry(
				layoutUtilityPageEntryId, name);

			if (SessionErrors.contains(
					actionRequest, "layoutUtilityPageEntryNameInvalid")) {

				addSuccessMessage(actionRequest, actionResponse);
			}

			JSONPortletResponseUtil.writeJSON(
				actionRequest, actionResponse,
				JSONUtil.put(
					"redirectURL",
					ParamUtil.getString(actionRequest, "redirect")));
		}
		catch (PortalException portalException) {
			SessionErrors.add(
				actionRequest, "layoutUtilityPageEntryNameInvalid");

			hideDefaultErrorMessage(actionRequest);

			LayoutUtilityPageEntryPortalExceptionRequestHandlerUtil.
				handlePortalException(
					actionRequest, actionResponse, portalException);
		}
	}

	@Reference
	private LayoutUtilityPageEntryService _layoutUtilityPageEntryService;

}