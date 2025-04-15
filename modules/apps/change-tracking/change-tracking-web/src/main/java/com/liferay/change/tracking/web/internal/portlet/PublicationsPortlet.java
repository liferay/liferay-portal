/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.portlet;

import com.liferay.change.tracking.constants.CTPortletKeys;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTPreferencesLocalService;
import com.liferay.change.tracking.service.CTRemoteLocalService;
import com.liferay.change.tracking.spi.display.CTDisplayRendererRegistry;
import com.liferay.change.tracking.web.internal.configuration.helper.CTSettingsConfigurationHelper;
import com.liferay.change.tracking.web.internal.constants.CTWebKeys;
import com.liferay.change.tracking.web.internal.display.context.PublicationsDisplayContext;
import com.liferay.change.tracking.web.internal.helper.PublicationHelper;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Máté Thurzó
 */
@Component(
	property = {
		"com.liferay.portlet.add-default-resource=false",
		"com.liferay.portlet.css-class-wrapper=portlet-publications",
		"com.liferay.portlet.header-portlet-css=/publications/css/main.css",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.show-portlet-access-denied=false",
		"com.liferay.portlet.show-portlet-inactive=false",
		"com.liferay.portlet.system=true",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.display-name=Overview",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/publications/view_publications.jsp",
		"jakarta.portlet.name=" + CTPortletKeys.PUBLICATIONS,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=administrator",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class PublicationsPortlet extends MVCPortlet {

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		try {
			checkPermissions(renderRequest);
		}
		catch (Exception exception) {
			SessionErrors.add(renderRequest, exception.getClass());

			include("/publications/error.jsp", renderRequest, renderResponse);

			return;
		}

		PublicationsDisplayContext publicationsDisplayContext =
			new PublicationsDisplayContext(
				_ctCollectionLocalService, _ctDisplayRendererRegistry,
				_ctPreferencesLocalService, _ctRemoteLocalService,
				_portal.getHttpServletRequest(renderRequest), _language,
				_publicationHelper, renderRequest, renderResponse);

		renderRequest.setAttribute(
			CTWebKeys.PUBLICATIONS_DISPLAY_CONTEXT, publicationsDisplayContext);

		super.render(renderRequest, renderResponse);
	}

	@Override
	protected void checkPermissions(PortletRequest portletRequest)
		throws Exception {

		if (!_ctSettingsConfigurationHelper.isEnabled(
				_portal.getCompanyId(portletRequest))) {

			String actionName = ParamUtil.getString(
				portletRequest, ActionRequest.ACTION_NAME);
			String mvcRenderCommandName = ParamUtil.getString(
				portletRequest, "mvcRenderCommandName");

			if (!actionName.equals(
					"/change_tracking" +
						"/update_global_publications_configuration") &&
				!mvcRenderCommandName.equals(
					"/change_tracking/view_settings")) {

				throw new PrincipalException("Publications are not enabled");
			}
		}

		PortletPermissionUtil.check(
			PermissionThreadLocal.getPermissionChecker(),
			CTPortletKeys.PUBLICATIONS, ActionKeys.VIEW);
	}

	@Reference
	private CTCollectionLocalService _ctCollectionLocalService;

	@Reference
	private CTDisplayRendererRegistry _ctDisplayRendererRegistry;

	@Reference
	private CTPreferencesLocalService _ctPreferencesLocalService;

	@Reference
	private CTRemoteLocalService _ctRemoteLocalService;

	@Reference
	private CTSettingsConfigurationHelper _ctSettingsConfigurationHelper;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private PublicationHelper _publicationHelper;

	@Reference(
		target = "(&(release.bundle.symbolic.name=com.liferay.change.tracking.web)(&(release.schema.version>=1.0.2)(!(release.schema.version>=2.0.0))))"
	)
	private Release _release;

}