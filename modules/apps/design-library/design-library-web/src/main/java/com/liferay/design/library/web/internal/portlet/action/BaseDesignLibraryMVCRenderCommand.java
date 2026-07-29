/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.design.library.web.internal.portlet.action;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryService;
import com.liferay.design.library.web.internal.constants.DesignLibraryWebKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Thiago Buarque
 */
public abstract class BaseDesignLibraryMVCRenderCommand
	implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			long designLibraryEntryId = ParamUtil.getLong(
				renderRequest, "designLibraryEntryId");

			DepotEntry depotEntry = depotEntryService.getDepotEntry(
				designLibraryEntryId);

			ThemeDisplay themeDisplay =
				(ThemeDisplay)renderRequest.getAttribute(WebKeys.THEME_DISPLAY);

			PermissionChecker permissionChecker =
				themeDisplay.getPermissionChecker();

			if ((depotEntry == null) ||
				(depotEntry.getType() != DepotConstants.TYPE_DESIGN_LIBRARY)) {

				throw new PrincipalException.MustHavePermission(
					permissionChecker, DepotEntry.class.getName(),
					designLibraryEntryId, ActionKeys.VIEW);
			}

			checkPermissions(permissionChecker, depotEntry);

			renderRequest.setAttribute(
				DesignLibraryWebKeys.DESIGN_LIBRARY_ENTRY, depotEntry);
		}
		catch (PrincipalException principalException) {
			SessionErrors.add(renderRequest, principalException.getClass());

			return "/error.jsp";
		}
		catch (PortalException portalException) {
			throw new PortletException(portalException);
		}

		return getPath();
	}

	protected void checkPermissions(
			PermissionChecker permissionChecker, DepotEntry depotEntry)
		throws PortalException {
	}

	protected abstract String getPath();

	@Reference
	protected DepotEntryService depotEntryService;

}