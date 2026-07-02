/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.event.generators.internal.model.listener;

import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
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
public class RoleModelListener extends BaseModelListener<Role> {

	@Override
	public void onBeforeAddAssociation(
			Object classPK, String associationClassName,
			Object associationClassPK)
		throws ModelListenerException {

		_auditOnAddOrRemoveAssociation(
			EventTypes.ASSIGN, classPK, associationClassName,
			associationClassPK);
	}

	public void onBeforeCreate(Role role) throws ModelListenerException {
		auditOnCreateOrRemove(EventTypes.ADD, role);
	}

	public void onBeforeRemove(Role role) throws ModelListenerException {
		auditOnCreateOrRemove(EventTypes.DELETE, role);
	}

	@Override
	public void onBeforeRemoveAssociation(
			Object classPK, String associationClassName,
			Object associationClassPK)
		throws ModelListenerException {

		_auditOnAddOrRemoveAssociation(
			EventTypes.UNASSIGN, classPK, associationClassName,
			associationClassPK);
	}

	public void onBeforeUpdate(Role originalRole, Role role)
		throws ModelListenerException {

		try {
			List<Attribute> attributes = _getModifiedAttributes(
				originalRole, role);

			if (!attributes.isEmpty()) {
				AuditMessage auditMessage =
					AuditMessageBuilder.buildAuditMessage(
						Role.class.getName(), role.getRoleId(),
						EventTypes.UPDATE, attributes);

				auditMessage.setCompanyId(role.getCompanyId());

				_auditRouter.route(auditMessage);
			}
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	protected void auditOnCreateOrRemove(String eventType, Role role)
		throws ModelListenerException {

		try {
			AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
				Role.class.getName(), role.getRoleId(), eventType, null);

			auditMessage.setCompanyId(role.getCompanyId());

			_auditRouter.route(auditMessage);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	private void _auditOnAddOrRemoveAssociation(
			String eventType, Object classPK, String associationClassName,
			Object associationClassPK)
		throws ModelListenerException {

		if (!associationClassName.equals(Group.class.getName()) &&
			!associationClassName.equals(Organization.class.getName()) &&
			!associationClassName.equals(User.class.getName())) {

			return;
		}

		try {
			AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
				associationClassName, (Long)associationClassPK, eventType,
				null);

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			long roleId = (Long)classPK;

			additionalInfoJSONObject.put("roleId", roleId);

			Role role = _roleLocalService.getRole(roleId);

			additionalInfoJSONObject.put("roleName", role.getName());

			if (associationClassName.equals(Group.class.getName())) {
				long groupId = (Long)associationClassPK;

				Group group = _groupLocalService.getGroup(groupId);

				if (group.getClassNameId() == _classNameService.getClassNameId(
						Organization.class.getName())) {

					Organization organization =
						_organizationLocalService.getOrganization(
							group.getClassPK());

					additionalInfoJSONObject.put(
						"organizationId", organization.getOrganizationId()
					).put(
						"organizationName", organization.getName()
					);
				}
				else if (group.getClassNameId() ==
							_classNameService.getClassNameId(
								UserGroup.class.getName())) {

					UserGroup userGroup = _userGroupLocalService.getUserGroup(
						group.getClassPK());

					additionalInfoJSONObject.put(
						"userGroupId", userGroup.getUserGroupId()
					).put(
						"userGroupName", userGroup.getName()
					);
				}
				else {
					additionalInfoJSONObject.put(
						"groupId", groupId
					).put(
						"groupName", group.getNameCurrentValue()
					);
				}
			}
			else if (associationClassName.equals(User.class.getName())) {
				long userId = (Long)associationClassPK;

				User user = _userLocalService.getUser(userId);

				additionalInfoJSONObject.put(
					"userEmailAddress", user.getEmailAddress()
				).put(
					"userId", userId
				);
			}

			auditMessage.setCompanyId(role.getCompanyId());

			_auditRouter.route(auditMessage);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	private List<Attribute> _getModifiedAttributes(
		Role originalRole, Role role) {

		AttributesBuilder attributesBuilder = new AttributesBuilder(
			role, originalRole);

		attributesBuilder.add("description");
		attributesBuilder.add("name");
		attributesBuilder.add("subtype");
		attributesBuilder.add("type");

		return attributesBuilder.getAttributes();
	}

	@Reference
	private AuditRouter _auditRouter;

	@Reference
	private ClassNameLocalService _classNameService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private OrganizationLocalService _organizationLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private UserGroupLocalService _userGroupLocalService;

	@Reference
	private UserLocalService _userLocalService;

}