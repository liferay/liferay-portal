/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.web.internal.portlet.action;

import com.liferay.document.library.util.DLURLHelper;
import com.liferay.oauth2.provider.configuration.OAuth2ProviderConfiguration;
import com.liferay.oauth2.provider.scope.liferay.ScopeLocator;
import com.liferay.oauth2.provider.scope.liferay.spi.ApplicationDescriptorLocator;
import com.liferay.oauth2.provider.scope.liferay.spi.ScopeDescriptorLocator;
import com.liferay.oauth2.provider.scope.spi.scope.matcher.ScopeMatcherFactory;
import com.liferay.oauth2.provider.service.OAuth2ApplicationScopeAliasesLocalService;
import com.liferay.oauth2.provider.service.OAuth2ApplicationService;
import com.liferay.oauth2.provider.service.OAuth2ScopeGrantLocalService;
import com.liferay.oauth2.provider.web.internal.constants.OAuth2ProviderPortletKeys;
import com.liferay.oauth2.provider.web.internal.constants.OAuth2ProviderWebKeys;
import com.liferay.oauth2.provider.web.internal.display.context.AssignScopesDisplayContext;
import com.liferay.oauth2.provider.web.internal.display.context.AssignScopesTreeDisplayContext;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;

/**
 * @author Tomas Polesovsky
 * @author Stian Sigvartsen
 */
@Component(
	configurationPid = "com.liferay.oauth2.provider.configuration.OAuth2ProviderConfiguration",
	property = {
		"jakarta.portlet.name=" + OAuth2ProviderPortletKeys.OAUTH2_ADMIN,
		"mvc.command.name=/oauth2_provider/assign_scopes"
	},
	service = MVCRenderCommand.class
)
public class AssignScopesMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		ThemeDisplay themeDisplay = getThemeDisplay(renderRequest);

		try {
			renderRequest.setAttribute(
				OAuth2ProviderWebKeys.OAUTH2_ADMIN_PORTLET_DISPLAY_CONTEXT,
				new AssignScopesDisplayContext(
					_applicationDescriptorLocator, _dlURLHelper,
					_oAuth2ApplicationScopeAliasesLocalService,
					_oAuth2ApplicationService, _oAuth2ProviderConfiguration,
					_oAuth2ScopeGrantLocalService, renderRequest,
					_scopeDescriptorLocator, _scopeLocator, themeDisplay));

			PermissionChecker permissionChecker =
				themeDisplay.getPermissionChecker();

			if (!permissionChecker.isOmniadmin()) {
				renderRequest.setAttribute(
					OAuth2ProviderWebKeys.ASSIGN_SCOPES_TREE_DISPLAY_CONTEXT,
					new AssignScopesTreeDisplayContext(
						_dlURLHelper,
						_oAuth2ApplicationScopeAliasesLocalService,
						_oAuth2ApplicationService, _oAuth2ProviderConfiguration,
						_oAuth2ScopeGrantLocalService, renderRequest,
						_scopeDescriptorLocator, _scopeLocator,
						_getScopeMatcherFactory(themeDisplay.getCompanyId()),
						themeDisplay));
			}
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return "/admin/edit_application.jsp";
	}

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		_oAuth2ProviderConfiguration = ConfigurableUtil.createConfigurable(
			OAuth2ProviderConfiguration.class, properties);
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, ScopeMatcherFactory.class, "company.id");
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	protected ThemeDisplay getThemeDisplay(PortletRequest portletRequest) {
		return (ThemeDisplay)portletRequest.getAttribute(WebKeys.THEME_DISPLAY);
	}

	private ScopeMatcherFactory _getScopeMatcherFactory(long companyId) {
		ScopeMatcherFactory scopeMatcherFactory = _serviceTrackerMap.getService(
			String.valueOf(companyId));

		if (scopeMatcherFactory == null) {
			return _defaultScopeMatcherFactory;
		}

		return scopeMatcherFactory;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AssignScopesMVCRenderCommand.class);

	@Reference
	private ApplicationDescriptorLocator _applicationDescriptorLocator;

	@Reference(name = "default", policy = ReferencePolicy.DYNAMIC)
	private volatile ScopeMatcherFactory _defaultScopeMatcherFactory;

	@Reference
	private DLURLHelper _dlURLHelper;

	@Reference
	private OAuth2ApplicationScopeAliasesLocalService
		_oAuth2ApplicationScopeAliasesLocalService;

	@Reference
	private OAuth2ApplicationService _oAuth2ApplicationService;

	private OAuth2ProviderConfiguration _oAuth2ProviderConfiguration;

	@Reference
	private OAuth2ScopeGrantLocalService _oAuth2ScopeGrantLocalService;

	@Reference
	private ScopeDescriptorLocator _scopeDescriptorLocator;

	@Reference
	private ScopeLocator _scopeLocator;

	private ServiceTrackerMap<String, ScopeMatcherFactory> _serviceTrackerMap;

}