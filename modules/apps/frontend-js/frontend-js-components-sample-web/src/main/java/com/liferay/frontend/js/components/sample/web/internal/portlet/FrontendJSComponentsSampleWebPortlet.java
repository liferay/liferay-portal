/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.components.sample.web.internal.portlet;

import com.liferay.frontend.js.components.sample.web.internal.constants.FrontendJSComponentsSampleWebKeys;
import com.liferay.frontend.js.components.sample.web.internal.constants.FrontendJSComponentsSampleWebPortletKeys;
import com.liferay.frontend.js.components.sample.web.internal.display.context.TranslationManagerDisplayContext;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;

/**
 * @author Eduardo Allegrini
 */
@Component(
	property = {
		"com.liferay.portlet.display-category=category.sample",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"jakarta.portlet.display-name=JS Components Sample",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/view.jsp",
		"jakarta.portlet.name=" + FrontendJSComponentsSampleWebPortletKeys.JS_COMPONENTS_SAMPLE,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=power-user,user",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class FrontendJSComponentsSampleWebPortlet extends MVCPortlet {

	@Override
	public void doDispatch(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		renderRequest.setAttribute(
			FrontendJSComponentsSampleWebKeys.
				TRANSLATION_MANAGER_DISPLAY_CONTEXT,
			new TranslationManagerDisplayContext());

		super.doDispatch(renderRequest, renderResponse);
	}

}