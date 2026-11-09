/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.event.generators.user.management.internal.model.listener;

import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.audit.event.generators.util.AuditMessageBuilder;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lucas Miranda
 */
@Component(service = ModelListener.class)
public class ResourcePermissionModelListener
	extends BaseModelListener<ResourcePermission> {

	@Override
	public void onBeforeCreate(ResourcePermission resourcePermission)
		throws ModelListenerException {

		_route(
			_getActionIdsJSONArray(null, resourcePermission), EventTypes.ADD,
			JSONUtil.putAll(), resourcePermission);
	}

	@Override
	public void onBeforeRemove(ResourcePermission resourcePermission)
		throws ModelListenerException {

		_route(
			JSONUtil.putAll(), EventTypes.DELETE,
			_getActionIdsJSONArray(null, resourcePermission),
			resourcePermission);
	}

	@Override
	public void onBeforeUpdate(
			ResourcePermission originalResourcePermission,
			ResourcePermission resourcePermission)
		throws ModelListenerException {

		if (originalResourcePermission.getRoleId() !=
				resourcePermission.getRoleId()) {

			onBeforeRemove(originalResourcePermission);
			onBeforeCreate(resourcePermission);
		}
		else if (originalResourcePermission.getActionIds() !=
					resourcePermission.getActionIds()) {

			_route(
				_getActionIdsJSONArray(
					originalResourcePermission, resourcePermission),
				EventTypes.UPDATE,
				_getActionIdsJSONArray(
					resourcePermission, originalResourcePermission),
				resourcePermission);
		}
	}

	private JSONArray _getActionIdsJSONArray(
		ResourcePermission excludedResourcePermission,
		ResourcePermission resourcePermission) {

		return JSONUtil.toJSONArray(
			ListUtil.filter(
				_resourceActionLocalService.getResourceActions(
					resourcePermission.getName()),
				resourceAction ->
					resourcePermission.hasAction(resourceAction) &&
					((excludedResourcePermission == null) ||
					 !excludedResourcePermission.hasAction(resourceAction))),
			ResourceAction::getActionId,
			exception -> {
				throw new ModelListenerException(exception);
			});
	}

	private void _route(
			JSONArray addedActionIdsJSONArray, String eventType,
			JSONArray removedActionIdsJSONArray,
			ResourcePermission resourcePermission)
		throws ModelListenerException {

		try {
			AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
				resourcePermission, eventType, null);

			auditMessage.setCompanyId(resourcePermission.getCompanyId());

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			additionalInfoJSONObject.put(
				"addedActionIds", addedActionIdsJSONArray
			).put(
				"name", resourcePermission.getName()
			).put(
				"primKey", resourcePermission.getPrimKey()
			).put(
				"removedActionIds", removedActionIdsJSONArray
			).put(
				"roleId", resourcePermission.getRoleId()
			).put(
				"scope", resourcePermission.getScope()
			);

			Role role = _roleLocalService.fetchRole(
				resourcePermission.getRoleId());

			if (role != null) {
				additionalInfoJSONObject.put("roleName", role.getName());
			}

			_auditRouter.route(auditMessage);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	@Reference
	private AuditRouter _auditRouter;

	@Reference
	private ResourceActionLocalService _resourceActionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

}