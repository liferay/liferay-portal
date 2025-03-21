/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.web.internal.portlet.action;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.saml.constants.SamlPortletKeys;
import com.liferay.saml.constants.SamlWebKeys;
import com.liferay.saml.opensaml.integration.field.expression.handler.registry.UserFieldExpressionHandlerRegistry;
import com.liferay.saml.opensaml.integration.field.expression.resolver.registry.UserFieldExpressionResolverRegistry;
import com.liferay.saml.persistence.model.SamlSpIdpConnection;
import com.liferay.saml.persistence.service.SamlSpIdpConnectionLocalService;
import com.liferay.saml.runtime.configuration.SamlProviderConfiguration;
import com.liferay.saml.runtime.configuration.SamlProviderConfigurationHelper;
import com.liferay.saml.web.internal.display.context.AttributeMappingDisplayContext;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stian Sigvartsen
 */
@Component(
	property = {
		"jakarta.portlet.name=" + SamlPortletKeys.SAML_ADMIN,
		"mvc.command.name=/admin/edit_identity_provider_connection"
	},
	service = MVCRenderCommand.class
)
public class EditIdentityProviderConnectionMVCRenderCommand
	implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			return _render(renderRequest);
		}
		catch (Exception exception) {
			throw new PortletException(exception);
		}
	}

	private long _getClockSkew(
		RenderRequest renderRequest, SamlSpIdpConnection samlSpIdpConnection) {

		if (samlSpIdpConnection != null) {
			return ParamUtil.getLong(
				renderRequest, "clockSkew", samlSpIdpConnection.getClockSkew());
		}

		SamlProviderConfiguration samlProviderConfiguration =
			_samlProviderConfigurationHelper.getSamlProviderConfiguration();

		return ParamUtil.getLong(
			renderRequest, "clockSkew", samlProviderConfiguration.clockSkew());
	}

	private String _render(RenderRequest renderRequest) throws Exception {
		long samlSpIdpConnectionId = ParamUtil.getLong(
			renderRequest, "samlSpIdpConnectionId");

		SamlSpIdpConnection samlSpIdpConnection = null;

		if (samlSpIdpConnectionId > 0) {
			samlSpIdpConnection =
				_samlSpIdpConnectionLocalService.getSamlSpIdpConnection(
					samlSpIdpConnectionId);
		}

		renderRequest.setAttribute(
			AttributeMappingDisplayContext.class.getName(),
			new AttributeMappingDisplayContext(
				renderRequest, samlSpIdpConnection,
				(ThemeDisplay)renderRequest.getAttribute(WebKeys.THEME_DISPLAY),
				_userFieldExpressionHandlerRegistry));
		renderRequest.setAttribute(
			SamlProviderConfigurationHelper.class.getName(),
			_samlProviderConfigurationHelper);
		renderRequest.setAttribute(
			SamlWebKeys.SAML_CLOCK_SKEW,
			_getClockSkew(renderRequest, samlSpIdpConnection));
		renderRequest.setAttribute(
			SamlWebKeys.SAML_SP_IDP_CONNECTION, samlSpIdpConnection);
		renderRequest.setAttribute(
			UserFieldExpressionResolverRegistry.class.getName(),
			_userFieldExpressionResolverRegistry);

		return "/admin/edit_identity_provider_connection.jsp";
	}

	@Reference
	private SamlProviderConfigurationHelper _samlProviderConfigurationHelper;

	@Reference
	private SamlSpIdpConnectionLocalService _samlSpIdpConnectionLocalService;

	@Reference
	private UserFieldExpressionHandlerRegistry
		_userFieldExpressionHandlerRegistry;

	@Reference
	private UserFieldExpressionResolverRegistry
		_userFieldExpressionResolverRegistry;

}