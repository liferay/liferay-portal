/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.event.generators.user.management.internal.model.listener;

import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.audit.event.generators.util.Attribute;
import com.liferay.portal.security.audit.event.generators.util.AttributesBuilder;
import com.liferay.portal.security.audit.event.generators.util.AuditMessageBuilder;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Amos Fong
 */
@Component(service = ModelListener.class)
public class OrganizationModelListener extends BaseModelListener<Organization> {

	@Override
	public void onBeforeAddAssociation(
			Object classPK, String associationClassName,
			Object associationClassPK)
		throws ModelListenerException {

		auditOnAddorRemoveAssociation(
			EventTypes.ASSIGN, classPK, associationClassName,
			associationClassPK);
	}

	public void onBeforeCreate(Organization organization)
		throws ModelListenerException {

		auditOnCreateOrRemove(EventTypes.ADD, organization);
	}

	public void onBeforeRemove(Organization organization)
		throws ModelListenerException {

		auditOnCreateOrRemove(EventTypes.DELETE, organization);
	}

	@Override
	public void onBeforeRemoveAssociation(
			Object classPK, String associationClassName,
			Object associationClassPK)
		throws ModelListenerException {

		auditOnAddorRemoveAssociation(
			EventTypes.UNASSIGN, classPK, associationClassName,
			associationClassPK);
	}

	public void onBeforeUpdate(
			Organization originalOrganization, Organization organization)
		throws ModelListenerException {

		try {
			List<Attribute> attributes = getModifiedAttributes(
				originalOrganization, organization);

			if (!attributes.isEmpty()) {
				AuditMessage auditMessage =
					AuditMessageBuilder.buildAuditMessage(
						Organization.class.getName(),
						organization.getOrganizationId(), EventTypes.UPDATE,
						attributes);

				auditMessage.setCompanyId(organization.getCompanyId());

				_auditRouter.route(auditMessage);
			}
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	protected void auditOnAddorRemoveAssociation(
			String eventType, Object classPK, String associationClassName,
			Object associationClassPK)
		throws ModelListenerException {

		if (!associationClassName.equals(User.class.getName())) {
			return;
		}

		try {
			AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
				associationClassName, (Long)associationClassPK, eventType,
				null);

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			long organizationId = (Long)classPK;

			additionalInfoJSONObject.put("organizationId", organizationId);

			Organization organization =
				_organizationLocalService.getOrganization(organizationId);

			additionalInfoJSONObject.put(
				"organizationName", organization.getName());

			auditMessage.setCompanyId(organization.getCompanyId());

			_auditRouter.route(auditMessage);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	protected void auditOnCreateOrRemove(
			String eventType, Organization organization)
		throws ModelListenerException {

		try {
			AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
				Organization.class.getName(), organization.getOrganizationId(),
				eventType, null);

			auditMessage.setCompanyId(organization.getCompanyId());

			_auditRouter.route(auditMessage);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	protected List<Attribute> getModifiedAttributes(
		Organization originalOrganization, Organization organization) {

		AttributesBuilder attributesBuilder = new AttributesBuilder(
			organization, originalOrganization);

		attributesBuilder.add("comments");
		attributesBuilder.add("countryId");
		attributesBuilder.add("name");
		attributesBuilder.add("parentOrganizationId");
		attributesBuilder.add("regionId");

		return attributesBuilder.getAttributes();
	}

	@Reference
	private AuditRouter _auditRouter;

	@Reference
	private OrganizationLocalService _organizationLocalService;

}