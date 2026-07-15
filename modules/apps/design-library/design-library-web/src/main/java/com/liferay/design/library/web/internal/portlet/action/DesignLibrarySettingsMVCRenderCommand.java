/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.design.library.web.internal.portlet.action;

import com.liferay.design.library.constants.DesignLibraryAdminPortletKeys;
import com.liferay.design.library.web.internal.constants.DesignLibraryConstants;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;

/**
 * @author Mario Leandro
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DesignLibraryAdminPortletKeys.DESIGN_LIBRARY_ADMIN,
		"mvc.command.name=/design_library/design_library_settings"
	},
	service = MVCRenderCommand.class
)
public class DesignLibrarySettingsMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		long designLibraryEntryId = ParamUtil.getLong(
			renderRequest, DesignLibraryConstants.DESIGN_LIBRARY_ENTRY_ID_KEY);

		renderRequest.setAttribute(
			DesignLibraryConstants.DESIGN_LIBRARY_ENTRY_ID_KEY,
			designLibraryEntryId);

		return "/view_settings.jsp";
	}

}