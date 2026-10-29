/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.persistence.internal.model.listener;

import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.audit.event.generators.util.Attribute;
import com.liferay.portal.security.audit.event.generators.util.AttributesBuilder;
import com.liferay.portal.security.audit.event.generators.util.AuditMessageBuilder;
import com.liferay.saml.persistence.model.SamlIdpSpConnection;

import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lucas Miranda
 */
@Component(service = ModelListener.class)
public class SamlIdpSpConnectionModelListener
	extends BaseModelListener<SamlIdpSpConnection> {

	@Override
	public void onAfterCreate(SamlIdpSpConnection samlIdpSpConnection)
		throws ModelListenerException {

		_route(null, EventTypes.ADD, samlIdpSpConnection);
	}

	@Override
	public void onBeforeRemove(SamlIdpSpConnection samlIdpSpConnection)
		throws ModelListenerException {

		_route(null, EventTypes.DELETE, samlIdpSpConnection);
	}

	@Override
	public void onBeforeUpdate(
			SamlIdpSpConnection originalSamlIdpSpConnection,
			SamlIdpSpConnection samlIdpSpConnection)
		throws ModelListenerException {

		List<Attribute> attributes = _getModifiedAttributes(
			originalSamlIdpSpConnection, samlIdpSpConnection);

		if (!attributes.isEmpty()) {
			_route(attributes, EventTypes.UPDATE, samlIdpSpConnection);
		}
	}

	private List<Attribute> _getModifiedAttributes(
		SamlIdpSpConnection originalSamlIdpSpConnection,
		SamlIdpSpConnection samlIdpSpConnection) {

		AttributesBuilder attributesBuilder = new AttributesBuilder(
			samlIdpSpConnection, originalSamlIdpSpConnection);

		attributesBuilder.add("assertionLifetime");
		attributesBuilder.add("attributeNames");
		attributesBuilder.add("attributesEnabled");
		attributesBuilder.add("attributesNamespaceEnabled");
		attributesBuilder.add("enabled");
		attributesBuilder.add("encryptionForced");
		attributesBuilder.add("metadataUrl");
		attributesBuilder.add("name");
		attributesBuilder.add("nameIdAttribute");
		attributesBuilder.add("nameIdFormat");
		attributesBuilder.add("samlSpEntityId");

		List<Attribute> attributes = attributesBuilder.getAttributes();

		if (!Objects.equals(
				originalSamlIdpSpConnection.getMetadataXml(),
				samlIdpSpConnection.getMetadataXml())) {

			attributes.add(new Attribute("metadataXml"));
		}

		return attributes;
	}

	private void _route(
			List<Attribute> attributes, String eventType,
			SamlIdpSpConnection samlIdpSpConnection)
		throws ModelListenerException {

		try {
			AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
				samlIdpSpConnection, eventType, attributes);

			auditMessage.setCompanyId(samlIdpSpConnection.getCompanyId());

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			additionalInfoJSONObject.put(
				"name", samlIdpSpConnection.getName()
			).put(
				"samlSpEntityId", samlIdpSpConnection.getSamlSpEntityId()
			);

			_auditRouter.route(auditMessage);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	@Reference
	private AuditRouter _auditRouter;

}