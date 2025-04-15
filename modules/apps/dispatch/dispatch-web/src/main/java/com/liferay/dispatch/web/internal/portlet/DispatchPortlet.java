/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.web.internal.portlet;

import com.liferay.dispatch.constants.DispatchPortletKeys;
import com.liferay.dispatch.constants.DispatchWebKeys;
import com.liferay.dispatch.executor.DispatchTaskExecutorRegistry;
import com.liferay.dispatch.metadata.DispatchTriggerMetadataProvider;
import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.dispatch.service.DispatchTriggerLocalService;
import com.liferay.dispatch.web.internal.display.context.DispatchTriggerDisplayContext;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=portlet-dispatch",
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.layout-cacheable=true",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.preferences-unique-per-layout=false",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.scopeable=true",
		"jakarta.portlet.display-name=Dispatch",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.view-template=/view_dispatch_trigger.jsp",
		"jakarta.portlet.name=" + DispatchPortletKeys.DISPATCH,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=administrator",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class DispatchPortlet extends MVCPortlet {

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		long dispatchTriggerId = ParamUtil.getLong(
			renderRequest, "dispatchTriggerId");

		if (dispatchTriggerId > 0) {
			DispatchTrigger dispatchTrigger =
				_dispatchTriggerLocalService.fetchDispatchTrigger(
					dispatchTriggerId);

			if (dispatchTrigger != null) {
				renderRequest.setAttribute(
					DispatchWebKeys.DISPATCH_TRIGGER, dispatchTrigger);
			}
		}

		DispatchTriggerDisplayContext dispatchTriggerDisplayContext =
			new DispatchTriggerDisplayContext(
				_dispatchTaskExecutorRegistry, _dispatchTriggerLocalService,
				_dispatchTriggerMetadataProvider, renderRequest);

		renderRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT, dispatchTriggerDisplayContext);

		super.render(renderRequest, renderResponse);
	}

	@Reference
	private DispatchTaskExecutorRegistry _dispatchTaskExecutorRegistry;

	@Reference
	private DispatchTriggerLocalService _dispatchTriggerLocalService;

	@Reference
	private DispatchTriggerMetadataProvider _dispatchTriggerMetadataProvider;

}