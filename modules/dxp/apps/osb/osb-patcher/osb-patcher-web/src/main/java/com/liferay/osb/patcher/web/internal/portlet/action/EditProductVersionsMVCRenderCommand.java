/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.web.internal.portlet.action;

import com.liferay.osb.patcher.constants.PatcherActionKeys;
import com.liferay.osb.patcher.constants.PatcherPortletKeys;
import com.liferay.osb.patcher.exception.NoSuchPatcherProductVersionException;
import com.liferay.osb.patcher.model.PatcherProductVersion;
import com.liferay.osb.patcher.permission.resource.PatcherPermission;
import com.liferay.osb.patcher.service.PatcherProductVersionLocalService;
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
		"mvc.command.name=/patcher/edit_product_versions"
	},
	service = MVCRenderCommand.class
)
public class EditProductVersionsMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long patcherProductVersionId = ParamUtil.getLong(
			renderRequest, "patcherProductVersionId");

		try {
			PatcherProductVersion patcherProductVersion =
				_patcherProductVersionLocalService.getPatcherProductVersion(
					patcherProductVersionId);

			if (!PatcherPermission.contains(
					themeDisplay.getPermissionChecker(), patcherProductVersion,
					PatcherActionKeys.EDIT,
					patcherProductVersion.getUserId())) {

				throw new PrincipalException.MustHavePermission(
					themeDisplay.getUserId());
			}
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchPatcherProductVersionException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(renderRequest, exception.getClass());

				return "/osb_patcher/views/error.jsp";
			}

			throw new PortletException(exception);
		}

		return "/osb_patcher/views/product_versions/edit.jsp";
	}

	@Reference
	private PatcherProductVersionLocalService
		_patcherProductVersionLocalService;

}