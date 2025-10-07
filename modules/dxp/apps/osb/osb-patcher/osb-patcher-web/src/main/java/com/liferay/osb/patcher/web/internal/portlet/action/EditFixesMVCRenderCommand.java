/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.web.internal.portlet.action;

import com.liferay.osb.patcher.constants.PatcherActionKeys;
import com.liferay.osb.patcher.constants.PatcherPortletKeys;
import com.liferay.osb.patcher.exception.NoSuchPatcherFixPackException;
import com.liferay.osb.patcher.model.PatcherFix;
import com.liferay.osb.patcher.permission.resource.PatcherPermission;
import com.liferay.osb.patcher.service.PatcherFixLocalService;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + PatcherPortletKeys.PATCHER,
		"mvc.command.name=/patcher/edit_fixes"
	},
	service = MVCRenderCommand.class
)
public class EditFixesMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long patcherFixId = ParamUtil.getLong(renderRequest, "patcherFixId");

		try {
			PatcherFix patcherFix = _patcherFixLocalService.getPatcherFix(
				patcherFixId);

			if (!PatcherPermission.contains(
					themeDisplay.getPermissionChecker(), patcherFix,
					PatcherActionKeys.EDIT, patcherFix.getUserId())) {

				throw new PrincipalException.MustHavePermission(
					themeDisplay.getUserId());
			}
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchPatcherFixPackException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(renderRequest, exception.getClass());

				return "/osb_patcher/views/error.jsp";
			}

			throw new PortletException(exception);
		}

		return "/osb_patcher/views/fixes/edit.jsp";
	}

	@Reference
	private PatcherFixLocalService _patcherFixLocalService;

}