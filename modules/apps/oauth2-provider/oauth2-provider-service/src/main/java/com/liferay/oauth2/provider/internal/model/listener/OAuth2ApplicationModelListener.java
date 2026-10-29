/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.internal.model.listener;

import com.liferay.oauth2.provider.model.OAuth2Application;
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
public class OAuth2ApplicationModelListener
	extends BaseModelListener<OAuth2Application> {

	@Override
	public void onAfterCreate(OAuth2Application oAuth2Application)
		throws ModelListenerException {

		_route(null, EventTypes.ADD, oAuth2Application);
	}

	@Override
	public void onBeforeRemove(OAuth2Application oAuth2Application)
		throws ModelListenerException {

		_route(null, EventTypes.DELETE, oAuth2Application);
	}

	@Override
	public void onBeforeUpdate(
			OAuth2Application originalOAuth2Application,
			OAuth2Application oAuth2Application)
		throws ModelListenerException {

		List<Attribute> attributes = _getModifiedAttributes(
			oAuth2Application, originalOAuth2Application);

		if (!attributes.isEmpty()) {
			_route(attributes, EventTypes.UPDATE, oAuth2Application);
		}
	}

	private List<Attribute> _getModifiedAttributes(
		OAuth2Application oAuth2Application,
		OAuth2Application originalOAuth2Application) {

		AttributesBuilder attributesBuilder = new AttributesBuilder(
			oAuth2Application, originalOAuth2Application);

		attributesBuilder.add("OAuth2ApplicationScopeAliasesId");
		attributesBuilder.add("allowedGrantTypes");
		attributesBuilder.add("clientAuthenticationMethod");
		attributesBuilder.add("clientCredentialUserId");
		attributesBuilder.add("clientCredentialUserName");
		attributesBuilder.add("clientId");
		attributesBuilder.add("clientProfile");
		attributesBuilder.add("description");
		attributesBuilder.add("externalReferenceCode");
		attributesBuilder.add("features");
		attributesBuilder.add("homePageURL");
		attributesBuilder.add("iconFileEntryId");
		attributesBuilder.add("name");
		attributesBuilder.add("privacyPolicyURL");
		attributesBuilder.add("redirectURIs");
		attributesBuilder.add("rememberDevice");
		attributesBuilder.add("trustedApplication");

		List<Attribute> attributes = attributesBuilder.getAttributes();

		if (!Objects.equals(
				originalOAuth2Application.getClientSecret(),
				oAuth2Application.getClientSecret())) {

			attributes.add(new Attribute("clientSecret"));
		}

		if (!Objects.equals(
				originalOAuth2Application.getJwks(),
				oAuth2Application.getJwks())) {

			attributes.add(new Attribute("jwks"));
		}

		return attributes;
	}

	private void _route(
			List<Attribute> attributes, String eventType,
			OAuth2Application oAuth2Application)
		throws ModelListenerException {

		try {
			AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
				oAuth2Application, eventType, attributes);

			auditMessage.setCompanyId(oAuth2Application.getCompanyId());

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			additionalInfoJSONObject.put(
				"clientId", oAuth2Application.getClientId()
			).put(
				"externalReferenceCode",
				oAuth2Application.getExternalReferenceCode()
			).put(
				"name", oAuth2Application.getName()
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