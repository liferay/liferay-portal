/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.clay.sample.web.internal.portlet;

import com.liferay.frontend.taglib.clay.sample.web.constants.ClaySamplePortletKeys;
import com.liferay.frontend.taglib.clay.sample.web.internal.display.context.CardsDisplayContext;
import com.liferay.frontend.taglib.clay.sample.web.internal.display.context.ClaySampleDisplayContext;
import com.liferay.frontend.taglib.clay.sample.web.internal.display.context.DropdownsDisplayContext;
import com.liferay.frontend.taglib.clay.sample.web.internal.display.context.MultiselectDisplayContext;
import com.liferay.frontend.taglib.clay.sample.web.internal.display.context.NavigationBarsDisplayContext;
import com.liferay.frontend.taglib.clay.sample.web.internal.display.context.TabsDisplayContext;
import com.liferay.frontend.taglib.clay.sample.web.internal.display.context.VerticalNavDisplayContext;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;

/**
 * @author Chema Balsas
 */
@Component(
	property = {
		"com.liferay.portlet.css-class-wrapper=portlet-clay-sample",
		"com.liferay.portlet.display-category=category.sample",
		"com.liferay.portlet.instanceable=true",
		"com.liferay.portlet.layout-cacheable=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.display-name=Clay Sample",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/view.jsp",
		"jakarta.portlet.name=" + ClaySamplePortletKeys.CLAY_SAMPLE,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=power-user,user",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class ClaySamplePortlet extends MVCPortlet {

	@Override
	public void doDispatch(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		renderRequest.setAttribute(
			ClaySamplePortletKeys.CARDS_DISPLAY_CONTEXT,
			new CardsDisplayContext());
		renderRequest.setAttribute(
			ClaySamplePortletKeys.CLAY_SAMPLE_DISPLAY_CONTEXT,
			new ClaySampleDisplayContext());
		renderRequest.setAttribute(
			ClaySamplePortletKeys.DROPDOWNS_DISPLAY_CONTEXT,
			new DropdownsDisplayContext());
		renderRequest.setAttribute(
			ClaySamplePortletKeys.MULTISELECT_DISPLAY_CONTEXT,
			new MultiselectDisplayContext());
		renderRequest.setAttribute(
			ClaySamplePortletKeys.NAVIGATION_BARS_DISPLAY_CONTEXT,
			new NavigationBarsDisplayContext());
		renderRequest.setAttribute(
			ClaySamplePortletKeys.TABS_DISPLAY_CONTEXT,
			new TabsDisplayContext());
		renderRequest.setAttribute(
			ClaySamplePortletKeys.VERTICAL_NAV_DISPLAY_CONTEXT,
			new VerticalNavDisplayContext());

		super.doDispatch(renderRequest, renderResponse);
	}

}