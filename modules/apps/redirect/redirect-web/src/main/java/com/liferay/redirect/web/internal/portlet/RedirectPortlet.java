/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.redirect.web.internal.portlet;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.redirect.configuration.RedirectConfiguration;
import com.liferay.redirect.configuration.RedirectPatternConfigurationProvider;
import com.liferay.redirect.constants.RedirectConstants;
import com.liferay.redirect.model.RedirectEntry;
import com.liferay.redirect.service.RedirectEntryLocalService;
import com.liferay.redirect.service.RedirectEntryService;
import com.liferay.redirect.service.RedirectNotFoundEntryLocalService;
import com.liferay.redirect.web.internal.constants.RedirectPortletKeys;
import com.liferay.redirect.web.internal.display.context.RedirectDisplayContext;
import com.liferay.redirect.web.internal.display.context.RedirectEntriesDisplayContext;
import com.liferay.redirect.web.internal.display.context.RedirectEntryInfoPanelDisplayContext;
import com.liferay.redirect.web.internal.display.context.RedirectNotFoundEntriesDisplayContext;
import com.liferay.redirect.web.internal.display.context.RedirectPatternConfigurationDisplayContext;
import com.liferay.staging.StagingGroupHelper;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

import java.util.Collections;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = {
		"com.liferay.portlet.css-class-wrapper=portlet-redirect",
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.instanceable=false",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.display-name=Redirect",
		"jakarta.portlet.init-param.always-send-redirect=true",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/view.jsp",
		"jakarta.portlet.name=" + RedirectPortletKeys.REDIRECT,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=administrator",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class RedirectPortlet extends MVCPortlet {

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		RedirectDisplayContext redirectDisplayContext =
			new RedirectDisplayContext(
				_portal.getHttpServletRequest(renderRequest),
				_redirectConfiguration, renderResponse);

		renderRequest.setAttribute(
			RedirectDisplayContext.class.getName(), redirectDisplayContext);

		if (redirectDisplayContext.isShowRedirectNotFoundEntries()) {
			renderRequest.setAttribute(
				RedirectNotFoundEntriesDisplayContext.class.getName(),
				new RedirectNotFoundEntriesDisplayContext(
					_portal.getHttpServletRequest(renderRequest),
					_portal.getLiferayPortletRequest(renderRequest),
					_portal.getLiferayPortletResponse(renderResponse),
					_portletResourcePermission,
					_redirectNotFoundEntryLocalService));
		}
		else if (redirectDisplayContext.isShowRedirectEntries()) {
			renderRequest.setAttribute(
				RedirectEntriesDisplayContext.class.getName(),
				new RedirectEntriesDisplayContext(
					_portal.getHttpServletRequest(renderRequest),
					_portal.getLiferayPortletRequest(renderRequest),
					_portal.getLiferayPortletResponse(renderResponse),
					_modelResourcePermission, _portletResourcePermission,
					_redirectEntryLocalService, _redirectEntryService,
					_stagingGroupHelper));
			renderRequest.setAttribute(
				RedirectEntryInfoPanelDisplayContext.class.getName(),
				new RedirectEntryInfoPanelDisplayContext(
					_portal.getLiferayPortletRequest(renderRequest),
					Collections.emptyList()));
		}
		else {
			renderRequest.setAttribute(
				RedirectPatternConfigurationDisplayContext.class.getName(),
				new RedirectPatternConfigurationDisplayContext(
					_portal.getHttpServletRequest(renderRequest),
					_portal.getLiferayPortletResponse(renderResponse),
					_redirectPatternConfigurationProvider,
					_stagingGroupHelper));
		}

		super.render(renderRequest, renderResponse);
	}

	@Reference(
		target = "(model.class.name=com.liferay.redirect.model.RedirectEntry)"
	)
	private ModelResourcePermission<RedirectEntry> _modelResourcePermission;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(resource.name=" + RedirectConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

	@Reference
	private RedirectConfiguration _redirectConfiguration;

	@Reference
	private RedirectEntryLocalService _redirectEntryLocalService;

	@Reference
	private RedirectEntryService _redirectEntryService;

	@Reference
	private RedirectNotFoundEntryLocalService
		_redirectNotFoundEntryLocalService;

	@Reference
	private RedirectPatternConfigurationProvider
		_redirectPatternConfigurationProvider;

	@Reference
	private StagingGroupHelper _stagingGroupHelper;

}