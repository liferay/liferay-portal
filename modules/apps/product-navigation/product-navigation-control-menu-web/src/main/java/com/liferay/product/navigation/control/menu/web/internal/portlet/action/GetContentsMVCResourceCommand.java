/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.control.menu.web.internal.portlet.action;

import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.product.navigation.control.menu.constants.ProductNavigationControlMenuPortletKeys;
import com.liferay.product.navigation.control.menu.web.internal.display.context.AddContentPanelDisplayContext;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ProductNavigationControlMenuPortletKeys.PRODUCT_NAVIGATION_CONTROL_MENU,
		"mvc.command.name=/product_navigation_control_menu/get_contents"
	},
	service = MVCResourceCommand.class
)
public class GetContentsMVCResourceCommand extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		AddContentPanelDisplayContext addContentPanelDisplayContext =
			new AddContentPanelDisplayContext(
				_portal.getHttpServletRequest(resourceRequest),
				_portal.getLiferayPortletRequest(resourceRequest),
				_portal.getLiferayPortletResponse(resourceResponse));

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse,
			JSONUtil.put(
				"contents", addContentPanelDisplayContext.getContents()));
	}

	@Reference
	private Portal _portal;

}