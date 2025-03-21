/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.web.internal.portlet.action;

import com.liferay.document.library.util.DLURLHelper;
import com.liferay.oauth2.provider.model.OAuth2Authorization;
import com.liferay.oauth2.provider.model.OAuth2ScopeGrant;
import com.liferay.oauth2.provider.scope.liferay.ScopeLocator;
import com.liferay.oauth2.provider.scope.liferay.spi.ApplicationDescriptorLocator;
import com.liferay.oauth2.provider.scope.liferay.spi.ScopeDescriptorLocator;
import com.liferay.oauth2.provider.service.OAuth2ApplicationService;
import com.liferay.oauth2.provider.service.OAuth2AuthorizationService;
import com.liferay.oauth2.provider.service.OAuth2ScopeGrantLocalService;
import com.liferay.oauth2.provider.web.internal.AssignableScopes;
import com.liferay.oauth2.provider.web.internal.constants.OAuth2ProviderPortletKeys;
import com.liferay.oauth2.provider.web.internal.constants.OAuth2ProviderWebKeys;
import com.liferay.oauth2.provider.web.internal.display.context.OAuth2ConnectedApplicationsPortletDisplayContext;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tomas Polesovsky
 * @author Stian Sigvartsen
 */
@Component(
	property = {
		"jakarta.portlet.name=" + OAuth2ProviderPortletKeys.OAUTH2_CONNECTED_APPLICATIONS,
		"mvc.command.name=/",
		"mvc.command.name=/oauth2_provider/view_connected_applications"
	},
	service = MVCRenderCommand.class
)
public class ViewConnectedApplicationsMVCRenderCommand
	implements MVCRenderCommand {

	@Override
	public String render(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		List<OAuth2Authorization> userOAuth2Authorizations =
			Collections.emptyList();

		try {
			userOAuth2Authorizations =
				_oAuth2AuthorizationService.getUserOAuth2Authorizations(
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
		}
		catch (PortalException portalException) {
			_log.error(
				"Unable to load user OAuth 2 authorizations", portalException);
		}

		long oAuth2AuthorizationId = ParamUtil.getLong(
			renderRequest, "oAuth2AuthorizationId");

		if (!ListUtil.exists(
				userOAuth2Authorizations,
				userOAuth2Authorization ->
					userOAuth2Authorization.getOAuth2AuthorizationId() ==
						oAuth2AuthorizationId)) {

			OAuth2ConnectedApplicationsPortletDisplayContext
				oAuth2ConnectedApplicationsPortletDisplayContext =
					new OAuth2ConnectedApplicationsPortletDisplayContext(
						_dlURLHelper, _oAuth2ApplicationService, renderRequest);

			renderRequest.setAttribute(
				OAuth2ProviderWebKeys.
					OAUTH2_CONNECTED_APPLICATIONS_PORTLET_DISPLAY_CONTEXT,
				oAuth2ConnectedApplicationsPortletDisplayContext);

			return "/connected_applications/view.jsp";
		}

		OAuth2Authorization oAuth2Authorization = null;

		for (OAuth2Authorization userOAuth2Authorization :
				userOAuth2Authorizations) {

			if (userOAuth2Authorization.getOAuth2AuthorizationId() ==
					oAuth2AuthorizationId) {

				oAuth2Authorization = userOAuth2Authorization;

				break;
			}
		}

		AssignableScopes assignableScopes = new AssignableScopes(
			_applicationDescriptorLocator, themeDisplay.getLocale(),
			_scopeDescriptorLocator);

		for (OAuth2ScopeGrant oAuth2ScopeGrant :
				_oAuth2ScopeGrantLocalService.getOAuth2ScopeGrants(
					oAuth2Authorization.getOAuth2ApplicationScopeAliasesId(),
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			assignableScopes.addLiferayOAuth2Scope(
				_scopeLocator.getLiferayOAuth2Scope(
					oAuth2ScopeGrant.getCompanyId(),
					oAuth2ScopeGrant.getApplicationName(),
					oAuth2ScopeGrant.getScope()));
		}

		OAuth2ConnectedApplicationsPortletDisplayContext
			oAuth2ConnectedApplicationsPortletDisplayContext =
				new OAuth2ConnectedApplicationsPortletDisplayContext(
					assignableScopes, _dlURLHelper, _oAuth2ApplicationService,
					oAuth2Authorization, renderRequest);

		renderRequest.setAttribute(
			OAuth2ProviderWebKeys.
				OAUTH2_CONNECTED_APPLICATIONS_PORTLET_DISPLAY_CONTEXT,
			oAuth2ConnectedApplicationsPortletDisplayContext);

		return "/connected_applications/view_application.jsp";
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ViewConnectedApplicationsMVCRenderCommand.class);

	@Reference
	private ApplicationDescriptorLocator _applicationDescriptorLocator;

	@Reference
	private DLURLHelper _dlURLHelper;

	@Reference
	private OAuth2ApplicationService _oAuth2ApplicationService;

	@Reference
	private OAuth2AuthorizationService _oAuth2AuthorizationService;

	@Reference
	private OAuth2ScopeGrantLocalService _oAuth2ScopeGrantLocalService;

	@Reference
	private ScopeDescriptorLocator _scopeDescriptorLocator;

	@Reference
	private ScopeLocator _scopeLocator;

}