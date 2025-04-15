/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.web.internal.portlet.action;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.upload.FileItem;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.saml.constants.SamlPortletKeys;
import com.liferay.saml.opensaml.integration.field.expression.handler.registry.SamlSpIdpConnectionFieldExpressionHandlerRegistry;
import com.liferay.saml.opensaml.integration.processor.SamlSpIdpConnectionProcessor;
import com.liferay.saml.opensaml.integration.processor.factory.SamlSpIdpConnectionProcessorFactory;
import com.liferay.saml.persistence.model.SamlSpIdpConnection;
import com.liferay.saml.persistence.service.SamlSpIdpConnectionLocalService;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(
	property = {
		"jakarta.portlet.name=" + SamlPortletKeys.SAML_ADMIN,
		"mvc.command.name=/admin/update_identity_provider_connection"
	},
	service = MVCActionCommand.class
)
public class UpdateIdentityProviderConnectionMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		UploadPortletRequest uploadPortletRequest =
			_portal.getUploadPortletRequest(actionRequest);

		long samlSpIdpConnectionId = ParamUtil.getLong(
			uploadPortletRequest, "samlSpIdpConnectionId");

		SamlSpIdpConnection samlSpIdpConnection = null;

		if (samlSpIdpConnectionId <= 0) {
			samlSpIdpConnection =
				_samlSpIdpConnectionLocalService.createSamlSpIdpConnection(0);
		}
		else {
			samlSpIdpConnection =
				_samlSpIdpConnectionLocalService.fetchSamlSpIdpConnection(
					samlSpIdpConnectionId);
		}

		SamlSpIdpConnectionProcessor samlSpIdpConnectionProcessor =
			_samlSpIdpConnectionProcessorFactory.create(
				samlSpIdpConnection,
				_samlSpIdpConnectionFieldExpressionHandlerRegistry);

		Map<String, List<String>> regularParameterMap =
			uploadPortletRequest.getRegularParameterMap();

		for (Map.Entry<String, List<String>> entry :
				regularParameterMap.entrySet()) {

			List<String> value = entry.getValue();

			samlSpIdpConnectionProcessor.setValueArray(
				entry.getKey(), value.toArray(new String[0]));
		}

		for (String booleanFieldExpression : _BOOLEAN_FIELD_EXPRESSIONS) {
			if (!regularParameterMap.containsKey(booleanFieldExpression)) {
				samlSpIdpConnectionProcessor.setValueArray(
					booleanFieldExpression, new String[] {StringPool.BLANK});
			}
		}

		Map<String, FileItem[]> multipartParameterMap =
			uploadPortletRequest.getMultipartParameterMap();

		multipartParameterMap.forEach(
			samlSpIdpConnectionProcessor::setFileItemArray);

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			SamlSpIdpConnection.class.getName(), actionRequest);

		samlSpIdpConnectionProcessor.process(serviceContext);
	}

	private static final String[] _BOOLEAN_FIELD_EXPRESSIONS = {
		"assertionSignatureRequired", "enabled", "forceAuthn",
		"ldapImportEnabled", "unknownUsersAreStrangers", "signAuthnRequest"
	};

	@Reference
	private Portal _portal;

	@Reference
	private SamlSpIdpConnectionFieldExpressionHandlerRegistry
		_samlSpIdpConnectionFieldExpressionHandlerRegistry;

	@Reference
	private SamlSpIdpConnectionLocalService _samlSpIdpConnectionLocalService;

	@Reference
	private SamlSpIdpConnectionProcessorFactory
		_samlSpIdpConnectionProcessorFactory;

}