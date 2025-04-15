/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.portlet.action;

import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryTypeException;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Balázs Sáfrány-Kovalik
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DLPortletKeys.DOCUMENT_LIBRARY_ADMIN,
		"mvc.command.name=/document_library/subscribe_file_entry"
	},
	service = MVCActionCommand.class
)
public class SubscribeFileEntryMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long fileEntryId = ParamUtil.getLong(actionRequest, "fileEntryId");

		try {
			_dlAppService.subscribeFileEntry(
				themeDisplay.getScopeGroupId(), fileEntryId);

			if (SessionErrors.isEmpty(actionRequest)) {
				SessionMessages.add(
					actionRequest,
					_portal.getPortletId(actionRequest) +
						SessionMessages.KEY_SUFFIX_REFRESH_PORTLET,
					DLPortletKeys.DOCUMENT_LIBRARY);

				String redirect = _portal.escapeRedirect(
					ParamUtil.getString(actionRequest, "redirect"));

				if (Validator.isNotNull(redirect)) {
					sendRedirect(actionRequest, actionResponse, redirect);
				}
			}
		}
		catch (NoSuchFileEntryTypeException | PrincipalException exception) {
			SessionErrors.add(actionRequest, exception.getClass());

			actionResponse.setRenderParameter(
				"mvcPath", "/document_library/error.jsp");
		}
	}

	@Reference
	private DLAppService _dlAppService;

	@Reference
	private Portal _portal;

}