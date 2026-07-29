/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.design.library.web.internal.portlet.action;

import com.liferay.design.library.constants.DesignLibraryAdminPortletKeys;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;

import org.osgi.service.component.annotations.Component;

/**
 * @author Gabriel Prates
 * @author Thiago Buarque
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DesignLibraryAdminPortletKeys.DESIGN_LIBRARY_ADMIN,
		"mvc.command.name=/design_library/view_resources_design_library"
	},
	service = MVCRenderCommand.class
)
public class ViewResourcesDesignLibraryMVCRenderCommand
	extends BaseDesignLibraryMVCRenderCommand {

	@Override
	protected String getPath() {
		return "/view_resources.jsp";
	}

}