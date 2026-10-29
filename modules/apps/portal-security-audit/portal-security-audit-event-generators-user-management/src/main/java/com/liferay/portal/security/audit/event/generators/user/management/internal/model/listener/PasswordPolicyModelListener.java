/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.event.generators.user.management.internal.model.listener;

import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.PasswordPolicy;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.audit.event.generators.util.Attribute;
import com.liferay.portal.security.audit.event.generators.util.AttributesBuilder;
import com.liferay.portal.security.audit.event.generators.util.AuditMessageBuilder;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lucas Miranda
 */
@Component(service = ModelListener.class)
public class PasswordPolicyModelListener
	extends BaseModelListener<PasswordPolicy> {

	@Override
	public void onBeforeCreate(PasswordPolicy passwordPolicy)
		throws ModelListenerException {

		_route(null, EventTypes.ADD, passwordPolicy);
	}

	@Override
	public void onBeforeRemove(PasswordPolicy passwordPolicy)
		throws ModelListenerException {

		_route(null, EventTypes.DELETE, passwordPolicy);
	}

	@Override
	public void onBeforeUpdate(
			PasswordPolicy originalPasswordPolicy,
			PasswordPolicy passwordPolicy)
		throws ModelListenerException {

		List<Attribute> attributes = _getModifiedAttributes(
			originalPasswordPolicy, passwordPolicy);

		if (!attributes.isEmpty()) {
			_route(attributes, EventTypes.UPDATE, passwordPolicy);
		}
	}

	private List<Attribute> _getModifiedAttributes(
		PasswordPolicy originalPasswordPolicy, PasswordPolicy passwordPolicy) {

		AttributesBuilder attributesBuilder = new AttributesBuilder(
			passwordPolicy, originalPasswordPolicy);

		attributesBuilder.add("allowDictionaryWords");
		attributesBuilder.add("changeRequired");
		attributesBuilder.add("changeable");
		attributesBuilder.add("checkSyntax");
		attributesBuilder.add("defaultPolicy");
		attributesBuilder.add("description");
		attributesBuilder.add("expireable");
		attributesBuilder.add("graceLimit");
		attributesBuilder.add("history");
		attributesBuilder.add("historyCount");
		attributesBuilder.add("lockout");
		attributesBuilder.add("lockoutDuration");
		attributesBuilder.add("maxAge");
		attributesBuilder.add("maxFailure");
		attributesBuilder.add("minAge");
		attributesBuilder.add("minAlphanumeric");
		attributesBuilder.add("minLength");
		attributesBuilder.add("minLowerCase");
		attributesBuilder.add("minNumbers");
		attributesBuilder.add("minSymbols");
		attributesBuilder.add("minUpperCase");
		attributesBuilder.add("name");
		attributesBuilder.add("regex");
		attributesBuilder.add("requireUnlock");
		attributesBuilder.add("resetFailureCount");
		attributesBuilder.add("resetTicketMaxAge");
		attributesBuilder.add("warningTime");

		return attributesBuilder.getAttributes();
	}

	private void _route(
			List<Attribute> attributes, String eventType,
			PasswordPolicy passwordPolicy)
		throws ModelListenerException {

		try {
			AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
				passwordPolicy, eventType, attributes);

			auditMessage.setCompanyId(passwordPolicy.getCompanyId());

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			additionalInfoJSONObject.put("name", passwordPolicy.getName());

			_auditRouter.route(auditMessage);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	@Reference
	private AuditRouter _auditRouter;

}