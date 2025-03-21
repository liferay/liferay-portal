/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.web.internal.portlet.action;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.saml.constants.SamlPortletKeys;
import com.liferay.saml.constants.SamlWebKeys;
import com.liferay.saml.persistence.service.SamlIdpSpConnectionLocalService;
import com.liferay.saml.persistence.service.SamlSpIdpConnectionLocalService;
import com.liferay.saml.runtime.certificate.CertificateTool;
import com.liferay.saml.runtime.configuration.SamlConfiguration;
import com.liferay.saml.runtime.configuration.SamlProviderConfigurationHelper;
import com.liferay.saml.runtime.metadata.LocalEntityManager;
import com.liferay.saml.web.internal.display.context.GeneralTabDefaultViewDisplayContext;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(
	configurationPid = "com.liferay.saml.runtime.configuration.SamlConfiguration",
	property = {
		"jakarta.portlet.name=" + SamlPortletKeys.SAML_ADMIN,
		"mvc.command.name=/", "mvc.command.name=/admin/view"
	},
	service = MVCRenderCommand.class
)
public class AdminViewMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		renderRequest.setAttribute(
			LocalEntityManager.class.getName(), _localEntityManager);
		renderRequest.setAttribute(
			SamlProviderConfigurationHelper.class.getName(),
			_samlProviderConfigurationHelper);

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			renderRequest);

		String tabs1 = ParamUtil.getString(
			httpServletRequest, "tabs1", "general");

		if (tabs1.equals("general")) {
			_renderGeneralTab(renderRequest);
		}
		else if (tabs1.equals("identity-provider-connections")) {
			_renderViewIdentityProviderConnections(
				httpServletRequest, renderRequest, renderResponse);
		}
		else if (tabs1.equals("service-provider-connections")) {
			_renderViewServiceProviderConnections(
				httpServletRequest, renderRequest, renderResponse);
		}

		return "/admin/view.jsp";
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_samlConfiguration = ConfigurableUtil.createConfigurable(
			SamlConfiguration.class, properties);
	}

	private void _renderGeneralTab(RenderRequest renderRequest) {
		String entityId = _localEntityManager.getLocalEntityId();

		renderRequest.setAttribute(SamlWebKeys.SAML_ENTITY_ID, entityId);

		if (renderRequest.getAttribute(SamlWebKeys.SAML_X509_CERTIFICATE) !=
				null) {

			return;
		}

		GeneralTabDefaultViewDisplayContext
			generalTabDefaultViewDisplayContext =
				new GeneralTabDefaultViewDisplayContext(
					_localEntityManager, _samlConfiguration);

		renderRequest.setAttribute(
			GeneralTabDefaultViewDisplayContext.class.getName(),
			generalTabDefaultViewDisplayContext);

		renderRequest.setAttribute(
			SamlWebKeys.SAML_CERTIFICATE_TOOL, _certificateTool);
	}

	private void _renderViewIdentityProviderConnections(
		HttpServletRequest httpServletRequest, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		SearchContainer<?> searchContainer = new SearchContainer(
			renderRequest, null, null, SearchContainer.DEFAULT_CUR_PARAM, 0,
			SearchContainer.DEFAULT_DELTA, renderResponse.createRenderURL(),
			null, null);

		renderRequest.setAttribute(
			SamlWebKeys.SAML_SP_IDP_CONNECTIONS,
			_samlSpIdpConnectionLocalService.getSamlSpIdpConnections(
				themeDisplay.getCompanyId(), searchContainer.getStart(),
				searchContainer.getEnd()));

		renderRequest.setAttribute(
			SamlWebKeys.SAML_SP_IDP_CONNECTIONS_COUNT,
			_samlSpIdpConnectionLocalService.getSamlSpIdpConnectionsCount(
				themeDisplay.getCompanyId()));
	}

	private void _renderViewServiceProviderConnections(
		HttpServletRequest httpServletRequest, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		int samlIdpSpConnectionsCount =
			_samlIdpSpConnectionLocalService.getSamlIdpSpConnectionsCount(
				themeDisplay.getCompanyId());

		PortletURL portletURL = renderResponse.createRenderURL();

		SearchContainer<?> searchContainer = new SearchContainer(
			renderRequest, null, null, SearchContainer.DEFAULT_CUR_PARAM, 0,
			SearchContainer.DEFAULT_DELTA, portletURL, null, null);

		renderRequest.setAttribute(
			SamlWebKeys.SAML_IDP_SP_CONNECTIONS,
			_samlIdpSpConnectionLocalService.getSamlIdpSpConnections(
				themeDisplay.getCompanyId(), searchContainer.getStart(),
				searchContainer.getEnd()));

		renderRequest.setAttribute(
			SamlWebKeys.SAML_IDP_SP_CONNECTIONS_COUNT,
			samlIdpSpConnectionsCount);
	}

	@Reference
	private CertificateTool _certificateTool;

	@Reference
	private LocalEntityManager _localEntityManager;

	@Reference
	private Portal _portal;

	private SamlConfiguration _samlConfiguration;

	@Reference
	private SamlIdpSpConnectionLocalService _samlIdpSpConnectionLocalService;

	@Reference
	private SamlProviderConfigurationHelper _samlProviderConfigurationHelper;

	@Reference
	private SamlSpIdpConnectionLocalService _samlSpIdpConnectionLocalService;

}