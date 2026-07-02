/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.event.generators.user.management.internal.model.listener;

import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.change.tracking.CTTransactionException;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.audit.event.generators.util.Attribute;
import com.liferay.portal.security.audit.event.generators.util.AttributesBuilder;
import com.liferay.portal.security.audit.event.generators.util.AuditMessageBuilder;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mika Koivisto
 * @author Brian Wing Shun Chan
 */
@Component(service = ModelListener.class)
public class UserGroupModelListener extends BaseModelListener<UserGroup> {

	@Override
	public void onBeforeAddAssociation(
			Object classPK, String associationClassName,
			Object associationClassPK)
		throws ModelListenerException {

		auditOnAddorRemoveAssociation(
			EventTypes.ASSIGN, classPK, associationClassName,
			associationClassPK);
	}

	public void onBeforeCreate(UserGroup userGroup)
		throws ModelListenerException {

		auditOnCreateOrRemove(EventTypes.ADD, userGroup);
	}

	public void onBeforeRemove(UserGroup userGroup)
		throws ModelListenerException {

		auditOnCreateOrRemove(EventTypes.DELETE, userGroup);
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

	public void onBeforeUpdate(UserGroup originalUserGroup, UserGroup userGroup)
		throws ModelListenerException {

		try {
			List<Attribute> attributes = getModifiedAttributes(
				originalUserGroup, userGroup);

			if (!attributes.isEmpty()) {
				AuditMessage auditMessage =
					AuditMessageBuilder.buildAuditMessage(
						UserGroup.class.getName(), userGroup.getUserGroupId(),
						EventTypes.UPDATE, attributes);

				auditMessage.setCompanyId(userGroup.getCompanyId());

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

		if (!associationClassName.equals(Group.class.getName()) &&
			!associationClassName.equals(User.class.getName())) {

			return;
		}

		try {
			AuditMessage auditMessage = null;

			if (associationClassName.equals(Group.class.getName())) {
				long groupId = (Long)associationClassPK;

				Group group = _groupLocalService.getGroup(groupId);

				auditMessage = AuditMessageBuilder.buildAuditMessage(
					group.getClassName(), group.getClassPK(), eventType, null);
			}
			else {
				auditMessage = AuditMessageBuilder.buildAuditMessage(
					associationClassName, (Long)associationClassPK, eventType,
					null);
			}

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			long userGroupId = (Long)classPK;

			additionalInfoJSONObject.put("userGroupId", userGroupId);

			UserGroup userGroup = _userGroupLocalService.getUserGroup(
				userGroupId);

			additionalInfoJSONObject.put("userGroupName", userGroup.getName());

			auditMessage.setCompanyId(userGroup.getCompanyId());

			_auditRouter.route(auditMessage);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	protected void auditOnCreateOrRemove(String eventType, UserGroup userGroup)
		throws ModelListenerException {

		try {
			AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
				UserGroup.class.getName(), userGroup.getUserGroupId(),
				eventType, null);

			auditMessage.setCompanyId(userGroup.getCompanyId());

			_auditRouter.route(auditMessage);
		}
		catch (CTTransactionException ctTransactionException) {
			throw ctTransactionException;
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	protected List<Attribute> getModifiedAttributes(
		UserGroup originalUserGroup, UserGroup userGroup) {

		AttributesBuilder attributesBuilder = new AttributesBuilder(
			userGroup, originalUserGroup);

		attributesBuilder.add("description");
		attributesBuilder.add("name");

		return attributesBuilder.getAttributes();
	}

	@Reference
	private AuditRouter _auditRouter;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private UserGroupLocalService _userGroupLocalService;

}