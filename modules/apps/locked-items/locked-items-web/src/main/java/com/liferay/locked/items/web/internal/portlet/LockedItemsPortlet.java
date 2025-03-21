/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.locked.items.web.internal.portlet;

import com.liferay.locked.items.constants.LockedItemsPortletKeys;
import com.liferay.locked.items.renderer.LockedItemsRendererRegistry;
import com.liferay.locked.items.web.internal.display.context.LockedItemsDisplayContext;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Galluzzi
 */
@Component(
	property = {
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.instanceable=false",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.display-name=Locked Items",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/view.jsp",
		"jakarta.portlet.name=" + LockedItemsPortletKeys.LOCKED_ITEMS,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=administrator",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class LockedItemsPortlet extends MVCPortlet {

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		renderRequest.setAttribute(
			LockedItemsDisplayContext.class.getName(),
			new LockedItemsDisplayContext(
				_portal.getHttpServletRequest(renderRequest),
				_lockedItemsRendererRegistry, renderResponse));

		super.render(renderRequest, renderResponse);
	}

	@Reference
	private LockedItemsRendererRegistry _lockedItemsRendererRegistry;

	@Reference
	private Portal _portal;

}