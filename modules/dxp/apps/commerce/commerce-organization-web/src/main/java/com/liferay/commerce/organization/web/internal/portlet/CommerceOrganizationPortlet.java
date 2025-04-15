/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.organization.web.internal.portlet;

import com.liferay.commerce.organization.constants.CommerceOrganizationPortletKeys;
import com.liferay.commerce.organization.web.internal.display.context.CommerceOrganizationDisplayContext;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.OrganizationService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Portal;
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
	configurationPid = "com.liferay.users.admin.configuration.UserFileUploadsConfiguration",
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=portlet-commerce-organization",
		"com.liferay.portlet.display-category=commerce",
		"com.liferay.portlet.layout-cacheable=false",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.scopeable=true",
		"jakarta.portlet.display-name=Commerce Organization Management",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.view-template=/view.jsp",
		"jakarta.portlet.name=" + CommerceOrganizationPortletKeys.COMMERCE_ORGANIZATION,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=power-user,user",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class CommerceOrganizationPortlet extends MVCPortlet {

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		try {
			CommerceOrganizationDisplayContext
				commerceOrganizationDisplayContext =
					new CommerceOrganizationDisplayContext(
						_configurationProvider,
						_portal.getHttpServletRequest(renderRequest),
						_organizationLocalService, _organizationService,
						_portal, _userLocalService);

			renderRequest.setAttribute(
				WebKeys.PORTLET_DISPLAY_CONTEXT,
				commerceOrganizationDisplayContext);
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		super.render(renderRequest, renderResponse);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceOrganizationPortlet.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private OrganizationLocalService _organizationLocalService;

	@Reference
	private OrganizationService _organizationService;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}