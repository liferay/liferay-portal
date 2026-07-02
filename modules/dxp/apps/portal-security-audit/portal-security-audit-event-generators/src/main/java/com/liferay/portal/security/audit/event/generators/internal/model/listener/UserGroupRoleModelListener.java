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
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.audit.event.generators.util.AuditMessageBuilder;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mika Koivisto
 * @author Brian Wing Shun Chan
 */
@Component(service = ModelListener.class)
public class UserGroupRoleModelListener
	extends BaseModelListener<UserGroupRole> {

	public void onBeforeCreate(UserGroupRole userGroupRole)
		throws ModelListenerException {

		auditOnCreateOrRemove(EventTypes.ASSIGN, userGroupRole);
	}

	public void onBeforeRemove(UserGroupRole userGroupRole)
		throws ModelListenerException {

		auditOnCreateOrRemove(EventTypes.UNASSIGN, userGroupRole);
	}

	protected void auditOnCreateOrRemove(
			String eventType, UserGroupRole userGroupRole)
		throws ModelListenerException {

		try {
			AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
				User.class.getName(), userGroupRole.getUserId(), eventType,
				null);

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			additionalInfoJSONObject.put("roleId", userGroupRole.getRoleId());

			Role role = userGroupRole.getRole();

			additionalInfoJSONObject.put("roleName", role.getName());

			Group group = userGroupRole.getGroup();

			additionalInfoJSONObject.put(
				"scopeClassName", group.getClassName()
			).put(
				"scopeClassPK", group.getClassPK()
			);

			auditMessage.setCompanyId(role.getCompanyId());

			_auditRouter.route(auditMessage);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	@Reference
	private AuditRouter _auditRouter;

}