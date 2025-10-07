/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.web.internal.portlet.action;

import com.liferay.osb.patcher.constants.PatcherPortletKeys;
import com.liferay.osb.patcher.exception.NoSuchPatcherFixPackException;
import com.liferay.osb.patcher.model.PatcherFixPack;
import com.liferay.osb.patcher.permission.resource.PatcherPermission;
import com.liferay.osb.patcher.service.PatcherFixPackLocalService;
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
		"mvc.command.name=/patcher/view_fix_packs"
	},
	service = MVCRenderCommand.class
)
public class ViewFixPacksMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long patcherFixPackId = ParamUtil.getLong(
			renderRequest, "patcherFixPackId");

		try {
			PatcherFixPack patcherFixPack =
				_patcherFixPackLocalService.getPatcherFixPack(patcherFixPackId);

			if (!PatcherPermission.contains(
					themeDisplay.getPermissionChecker(), patcherFixPack, "VIEW",
					patcherFixPack.getUserId())) {

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

		return "/osb_patcher/views/fix_packs/view.jsp";
	}

	@Reference
	private PatcherFixPackLocalService _patcherFixPackLocalService;

}