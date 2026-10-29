/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.internal.model.listener;

import com.liferay.oauth.client.persistence.model.OAuthClientEntry;
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
 * @author Lucas Miranda
 */
@Component(service = ModelListener.class)
public class OAuthClientEntryModelListener
	extends BaseModelListener<OAuthClientEntry> {

	@Override
	public void onAfterCreate(OAuthClientEntry oAuthClientEntry)
		throws ModelListenerException {

		_route(null, EventTypes.ADD, oAuthClientEntry);
	}

	@Override
	public void onBeforeRemove(OAuthClientEntry oAuthClientEntry)
		throws ModelListenerException {

		_route(null, EventTypes.DELETE, oAuthClientEntry);
	}

	@Override
	public void onBeforeUpdate(
			OAuthClientEntry originalOAuthClientEntry,
			OAuthClientEntry oAuthClientEntry)
		throws ModelListenerException {

		List<Attribute> attributes = _getModifiedAttributes(
			oAuthClientEntry, originalOAuthClientEntry);

		if (!attributes.isEmpty()) {
			_route(attributes, EventTypes.UPDATE, oAuthClientEntry);
		}
	}

	private List<Attribute> _getModifiedAttributes(
		OAuthClientEntry oAuthClientEntry,
		OAuthClientEntry originalOAuthClientEntry) {

		AttributesBuilder attributesBuilder = new AttributesBuilder(
			oAuthClientEntry, originalOAuthClientEntry);

		attributesBuilder.add("OIDCUserInfoMapperJSON");
		attributesBuilder.add("authServerWellKnownURI");
		attributesBuilder.add("clientId");
		attributesBuilder.add("customClaimsJSON");
		attributesBuilder.add("externalReferenceCode");
		attributesBuilder.add("matcherField");
		attributesBuilder.add("metadataCacheTime");
		attributesBuilder.add("tokenConnectionTimeout");

		List<Attribute> attributes = attributesBuilder.getAttributes();

		if (!Objects.equals(
				originalOAuthClientEntry.getAuthRequestParametersJSON(),
				oAuthClientEntry.getAuthRequestParametersJSON())) {

			attributes.add(new Attribute("authRequestParametersJSON"));
		}

		if (!Objects.equals(
				originalOAuthClientEntry.getInfoJSON(),
				oAuthClientEntry.getInfoJSON())) {

			attributes.add(new Attribute("infoJSON"));
		}

		if (!Objects.equals(
				originalOAuthClientEntry.getTokenRequestParametersJSON(),
				oAuthClientEntry.getTokenRequestParametersJSON())) {

			attributes.add(new Attribute("tokenRequestParametersJSON"));
		}

		return attributes;
	}

	private void _route(
			List<Attribute> attributes, String eventType,
			OAuthClientEntry oAuthClientEntry)
		throws ModelListenerException {

		try {
			AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
				oAuthClientEntry, eventType, attributes);

			auditMessage.setCompanyId(oAuthClientEntry.getCompanyId());

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			additionalInfoJSONObject.put(
				"authServerWellKnownURI",
				oAuthClientEntry.getAuthServerWellKnownURI()
			).put(
				"clientId", oAuthClientEntry.getClientId()
			).put(
				"externalReferenceCode",
				oAuthClientEntry.getExternalReferenceCode()
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