/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.web.internal.opensaml.integration.field.expression.handler;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.upload.FileItem;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.saml.opensaml.integration.field.expression.handler.SamlSpIdpConnectionFieldExpressionHandler;
import com.liferay.saml.opensaml.integration.processor.context.SamlSpIdpConnectionProcessorContext;
import com.liferay.saml.persistence.model.SamlSpIdpConnection;
import com.liferay.saml.persistence.service.SamlSpIdpConnectionLocalService;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.nio.charset.StandardCharsets;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stian Sigvartsen
 */
@Component(
	property = {"prefix=", "processing.index:Integer=" + Integer.MAX_VALUE},
	service = SamlSpIdpConnectionFieldExpressionHandler.class
)
public class DefaultSamlSpIdpConnectionFieldExpressionHandler
	implements SamlSpIdpConnectionFieldExpressionHandler {

	@Override
	public void bindProcessorContext(
		SamlSpIdpConnectionProcessorContext
			samlSpIdpConnectionProcessorContext) {

		SamlSpIdpConnectionProcessorContext.SamlSpIdpConnectionBind
			<SamlSpIdpConnection> samlSpIdpConnectionBind =
				samlSpIdpConnectionProcessorContext.bind(
					Integer.MIN_VALUE,
					(currentSamlSpIdpConnection, newSamlSpIdpConnection,
					 serviceContext) -> newSamlSpIdpConnection);

		samlSpIdpConnectionBind.mapBoolean(
			"assertionSignatureRequired",
			SamlSpIdpConnection::setAssertionSignatureRequired);
		samlSpIdpConnectionBind.mapLong(
			"clockSkew", SamlSpIdpConnection::setClockSkew);
		samlSpIdpConnectionBind.mapBoolean(
			"enabled", SamlSpIdpConnection::setEnabled);
		samlSpIdpConnectionBind.mapBoolean(
			"forceAuthn", SamlSpIdpConnection::setForceAuthn);
		samlSpIdpConnectionBind.mapBoolean(
			"ldapImportEnabled", SamlSpIdpConnection::setLdapImportEnabled);
		samlSpIdpConnectionBind.mapBoolean(
			"unknownUsersAreStrangers",
			SamlSpIdpConnection::setUnknownUsersAreStrangers);

		samlSpIdpConnectionBind.mapString(
			"metadataDelivery",
			(samlSpIdpConnection, metadataDelivery) -> {
				if (metadataDelivery.equals("metadataXml")) {
					samlSpIdpConnection.setMetadataUrl(null);

					samlSpIdpConnectionBind.handleFileItemArray(
						"metadataXml", this::_setMetadataXml);
				}
				else {
					samlSpIdpConnection.setMetadataXml(null);

					samlSpIdpConnectionBind.mapString(
						"metadataUrl", SamlSpIdpConnection::setMetadataUrl);
				}
			});

		samlSpIdpConnectionBind.mapString("name", SamlSpIdpConnection::setName);
		samlSpIdpConnectionBind.mapString(
			"nameIdFormat", SamlSpIdpConnection::setNameIdFormat);
		samlSpIdpConnectionBind.mapString(
			"samlIdpEntityId", SamlSpIdpConnection::setSamlIdpEntityId);
		samlSpIdpConnectionBind.mapBoolean(
			"signAuthnRequest", SamlSpIdpConnection::setSignAuthnRequest);
		samlSpIdpConnectionBind.mapString(
			"userIdentifierExpression",
			SamlSpIdpConnection::setUserIdentifierExpression);

		samlSpIdpConnectionProcessorContext.bind(
			_processingIndex, this::_persist);
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_processingIndex = GetterUtil.getInteger(
			properties.get("processing.index"));
	}

	private SamlSpIdpConnection _persist(
			SamlSpIdpConnection currentSamlSpIdpConnection,
			SamlSpIdpConnection newSamlSpIdpConnection,
			ServiceContext serviceContext)
		throws PortalException {

		String metadataXml = newSamlSpIdpConnection.getMetadataXml();
		InputStream metadataXmlInputStream = null;

		if (Validator.isNotNull(metadataXml)) {
			metadataXmlInputStream = new ByteArrayInputStream(
				metadataXml.getBytes(StandardCharsets.UTF_8));
		}

		if (newSamlSpIdpConnection.getSamlSpIdpConnectionId() <= 0) {
			_samlSpIdpConnectionLocalService.addSamlSpIdpConnection(
				newSamlSpIdpConnection.getSamlIdpEntityId(),
				newSamlSpIdpConnection.isAssertionSignatureRequired(),
				newSamlSpIdpConnection.getClockSkew(),
				newSamlSpIdpConnection.isEnabled(),
				newSamlSpIdpConnection.isForceAuthn(),
				newSamlSpIdpConnection.isLdapImportEnabled(),
				newSamlSpIdpConnection.getMetadataUrl(), metadataXmlInputStream,
				newSamlSpIdpConnection.getName(),
				newSamlSpIdpConnection.getNameIdFormat(),
				newSamlSpIdpConnection.isSignAuthnRequest(),
				newSamlSpIdpConnection.isUnknownUsersAreStrangers(),
				newSamlSpIdpConnection.getUserAttributeMappings(),
				newSamlSpIdpConnection.getUserIdentifierExpression(),
				serviceContext);
		}
		else {
			_samlSpIdpConnectionLocalService.updateSamlSpIdpConnection(
				newSamlSpIdpConnection.getSamlSpIdpConnectionId(),
				newSamlSpIdpConnection.getSamlIdpEntityId(),
				newSamlSpIdpConnection.isAssertionSignatureRequired(),
				newSamlSpIdpConnection.getClockSkew(),
				newSamlSpIdpConnection.isEnabled(),
				newSamlSpIdpConnection.isForceAuthn(),
				newSamlSpIdpConnection.isLdapImportEnabled(),
				newSamlSpIdpConnection.getMetadataUrl(), metadataXmlInputStream,
				newSamlSpIdpConnection.getName(),
				newSamlSpIdpConnection.getNameIdFormat(),
				newSamlSpIdpConnection.isSignAuthnRequest(),
				newSamlSpIdpConnection.isUnknownUsersAreStrangers(),
				newSamlSpIdpConnection.getUserAttributeMappings(),
				newSamlSpIdpConnection.getUserIdentifierExpression(),
				serviceContext);
		}

		return null;
	}

	private void _setMetadataXml(
		SamlSpIdpConnection samlSpIdpConnection, FileItem[] fileItems) {

		if (ArrayUtil.isEmpty(fileItems)) {

			// Dereference metadata XML

			samlSpIdpConnection.setMetadataXml(null);

			return;
		}

		FileItem fileItem = fileItems[0];

		samlSpIdpConnection.setMetadataXml(fileItem.getString());
	}

	private int _processingIndex;

	@Reference
	private SamlSpIdpConnectionLocalService _samlSpIdpConnectionLocalService;

}