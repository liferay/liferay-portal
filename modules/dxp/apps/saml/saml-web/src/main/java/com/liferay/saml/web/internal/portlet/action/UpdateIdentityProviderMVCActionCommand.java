/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.web.internal.portlet.action;

import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PropertiesParamUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.saml.constants.SamlPortletKeys;
import com.liferay.saml.runtime.configuration.SamlProviderConfigurationHelper;
import com.liferay.saml.util.PortletPropsKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(
	property = {
		"jakarta.portlet.name=" + SamlPortletKeys.SAML_ADMIN,
		"mvc.command.name=/admin/update_identity_provider"
	},
	service = MVCActionCommand.class
)
public class UpdateIdentityProviderMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		UnicodeProperties unicodeProperties = PropertiesParamUtil.getProperties(
			actionRequest, "settings--");

		String nameIdentifierAttributeType = ParamUtil.getString(
			actionRequest, "nameIdentifierAttributeType");

		if (Validator.isNotNull(nameIdentifierAttributeType)) {
			String nameIdentifierAttribute = unicodeProperties.getProperty(
				PortletPropsKeys.SAML_IDP_METADATA_NAME_ID_ATTRIBUTE);

			nameIdentifierAttribute =
				nameIdentifierAttributeType + ":" + nameIdentifierAttribute;

			unicodeProperties.setProperty(
				PortletPropsKeys.SAML_IDP_METADATA_NAME_ID_ATTRIBUTE,
				nameIdentifierAttribute);
		}

		_samlProviderConfigurationHelper.updateProperties(unicodeProperties);

		actionResponse.setRenderParameter(
			"mvcRenderCommandName", "/admin/view");
		actionResponse.setRenderParameter("tabs1", "identity-provider");
	}

	@Reference
	private SamlProviderConfigurationHelper _samlProviderConfigurationHelper;

}