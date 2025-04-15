/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.web.internal.blueprint.admin.portlet.action;

import com.liferay.item.selector.ItemSelector;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.search.experiences.constants.SXPPortletKeys;
import com.liferay.search.experiences.web.internal.display.context.EditSXPBlueprintDisplayContext;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Kevin Tan
 */
@Component(
	enabled = false,
	property = {
		"jakarta.portlet.name=" + SXPPortletKeys.SXP_BLUEPRINT_ADMIN,
		"mvc.command.name=/sxp_blueprint_admin/edit_sxp_blueprint"
	},
	service = MVCRenderCommand.class
)
public class EditSXPBlueprintMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		renderRequest.setAttribute(
			EditSXPBlueprintDisplayContext.class.getName(),
			new EditSXPBlueprintDisplayContext(
				_itemSelector, renderRequest, renderResponse));

		return "/sxp_blueprint_admin/edit_sxp_blueprint.jsp";
	}

	@Reference
	private ItemSelector _itemSelector;

}