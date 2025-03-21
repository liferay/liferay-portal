/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.redirect.web.internal.portlet.action;

import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.redirect.service.RedirectEntryService;
import com.liferay.redirect.web.internal.constants.RedirectPortletKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = {
		"jakarta.portlet.name=" + RedirectPortletKeys.REDIRECT,
		"mvc.command.name=/redirect/delete_redirect_entry"
	},
	service = MVCActionCommand.class
)
public class DeleteRedirectEntryMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long redirectEntryId = ParamUtil.getLong(
			actionRequest, "redirectEntryId");

		if (redirectEntryId > 0) {
			_redirectEntryService.deleteRedirectEntry(redirectEntryId);
		}
		else {
			long[] deleteRedirectEntryIds = ParamUtil.getLongValues(
				actionRequest, "rowIds");

			for (long deleteRedirectEntryId : deleteRedirectEntryIds) {
				_redirectEntryService.deleteRedirectEntry(
					deleteRedirectEntryId);
			}
		}
	}

	@Reference
	private RedirectEntryService _redirectEntryService;

}