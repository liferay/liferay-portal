/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.web.internal.portlet.action;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.saml.constants.SamlPortletKeys;
import com.liferay.saml.constants.SamlWebKeys;
import com.liferay.saml.persistence.model.SamlIdpSpConnection;
import com.liferay.saml.persistence.service.SamlIdpSpConnectionLocalService;
import com.liferay.saml.runtime.configuration.SamlProviderConfiguration;
import com.liferay.saml.runtime.configuration.SamlProviderConfigurationHelper;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(
	property = {
		"jakarta.portlet.name=" + SamlPortletKeys.SAML_ADMIN,
		"mvc.command.name=/admin/edit_service_provider_connection"
	},
	service = MVCRenderCommand.class
)
public class EditServiceProviderConnectionMVCRenderCommand
	implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		long samlIdpSpConnectionId = ParamUtil.getLong(
			renderRequest, "samlIdpSpConnectionId");

		renderRequest.setAttribute(
			SamlProviderConfigurationHelper.class.getName(),
			_samlProviderConfigurationHelper);

		SamlProviderConfiguration samlProviderConfiguration =
			_samlProviderConfigurationHelper.getSamlProviderConfiguration();

		int assertionLifetime = ParamUtil.getInteger(
			renderRequest, "assertionLifetime",
			samlProviderConfiguration.defaultAssertionLifetime());

		if (samlIdpSpConnectionId > 0) {
			SamlIdpSpConnection samlIdpSpConnection =
				_samlIdpSpConnectionLocalService.fetchSamlIdpSpConnection(
					samlIdpSpConnectionId);

			if (samlIdpSpConnection != null) {
				assertionLifetime = ParamUtil.getInteger(
					renderRequest, "assertionLifetime",
					samlIdpSpConnection.getAssertionLifetime());

				renderRequest.setAttribute(
					SamlWebKeys.SAML_IDP_SP_CONNECTION, samlIdpSpConnection);
			}
		}

		renderRequest.setAttribute(
			SamlWebKeys.SAML_ASSERTION_LIFETIME, assertionLifetime);

		return "/admin/edit_service_provider_connection.jsp";
	}

	@Reference
	private SamlIdpSpConnectionLocalService _samlIdpSpConnectionLocalService;

	@Reference
	private SamlProviderConfigurationHelper _samlProviderConfigurationHelper;

}