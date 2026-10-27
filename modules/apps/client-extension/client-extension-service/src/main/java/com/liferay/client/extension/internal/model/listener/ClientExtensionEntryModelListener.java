/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.internal.model.listener;

import com.liferay.client.extension.model.ClientExtensionEntry;
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

import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Christopher Kian
 */
@Component(service = ModelListener.class)
public class ClientExtensionEntryModelListener
	extends BaseModelListener<ClientExtensionEntry> {

	@Override
	public void onBeforeCreate(ClientExtensionEntry clientExtensionEntry)
		throws ModelListenerException {

		_route(clientExtensionEntry, EventTypes.ADD);
	}

	@Override
	public void onBeforeRemove(ClientExtensionEntry clientExtensionEntry)
		throws ModelListenerException {

		_route(clientExtensionEntry, EventTypes.DELETE);
	}

	@Override
	public void onBeforeUpdate(
			ClientExtensionEntry originalClientExtensionEntry,
			ClientExtensionEntry clientExtensionEntry)
		throws ModelListenerException {

		List<Attribute> attributes = _getModifiedAttributes(
			clientExtensionEntry, originalClientExtensionEntry);

		if (attributes.isEmpty()) {
			return;
		}

		try {
			_auditRouter.route(
				AuditMessageBuilder.buildAuditMessage(
					ClientExtensionEntry.class.getName(),
					clientExtensionEntry.getClientExtensionEntryId(),
					EventTypes.UPDATE, attributes));
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	private List<Attribute> _getModifiedAttributes(
		ClientExtensionEntry clientExtensionEntry,
		ClientExtensionEntry originalClientExtensionEntry) {

		AttributesBuilder attributesBuilder = new AttributesBuilder(
			clientExtensionEntry, originalClientExtensionEntry);

		attributesBuilder.add("description");
		attributesBuilder.add("nameMap");
		attributesBuilder.add("sourceCodeURL");

		List<Attribute> attributes = attributesBuilder.getAttributes();

		if (!Objects.equals(
				originalClientExtensionEntry.getProperties(),
				clientExtensionEntry.getProperties())) {

			attributes.add(new Attribute("properties"));
		}

		if (!Objects.equals(
				originalClientExtensionEntry.getTypeSettings(),
				clientExtensionEntry.getTypeSettings())) {

			attributes.add(new Attribute("typeSettings"));
		}

		return attributes;
	}

	private void _route(
			ClientExtensionEntry clientExtensionEntry, String eventType)
		throws ModelListenerException {

		try {
			AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
				ClientExtensionEntry.class.getName(),
				clientExtensionEntry.getClientExtensionEntryId(), eventType,
				null);

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			additionalInfoJSONObject.put(
				"externalReferenceCode",
				clientExtensionEntry.getExternalReferenceCode()
			).put(
				"nameMap", clientExtensionEntry.getNameMap()
			).put(
				"type", clientExtensionEntry.getType()
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