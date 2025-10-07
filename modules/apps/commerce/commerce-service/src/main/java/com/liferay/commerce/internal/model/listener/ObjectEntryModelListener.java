/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.model.listener;

import com.liferay.commerce.notification.util.CommerceNotificationHelper;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.comment.CommentManager;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.Serializable;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 */
@Component(service = ModelListener.class)
public class ObjectEntryModelListener extends BaseModelListener<ObjectEntry> {

	@Override
	public void onAfterCreate(ObjectEntry objectEntry)
		throws ModelListenerException {

		super.onAfterCreate(objectEntry);

		_sendNotifications("create", objectEntry);

		_updateCommerceReturnItemResourcePermissions(objectEntry);
	}

	@Override
	public void onAfterRemove(ObjectEntry objectEntry)
		throws ModelListenerException {

		super.onAfterRemove(objectEntry);

		_sendNotifications("delete", objectEntry);

		_removeCommerceReturnItemDiscussion(objectEntry);

		_updateCommerceReturn(objectEntry);
	}

	@Override
	public void onAfterUpdate(
			ObjectEntry originalObjectEntry, ObjectEntry objectEntry)
		throws ModelListenerException {

		super.onAfterUpdate(originalObjectEntry, objectEntry);

		_sendNotifications("update", objectEntry);

		_updateCommerceReturn(originalObjectEntry);
	}

	private void _removeCommerceReturnItemDiscussion(ObjectEntry objectEntry) {
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				objectEntry.getObjectDefinitionId());

		if (!StringUtil.equals(
				objectDefinition.getName(), "CommerceReturnItem")) {

			return;
		}

		try {
			_commentManager.deleteDiscussion(
				objectDefinition.getClassName(),
				objectEntry.getObjectEntryId());
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portalException);
			}
		}
	}

	private void _sendNotifications(String action, ObjectEntry objectEntry) {
		try {
			CommerceChannel commerceChannel =
				_commerceChannelLocalService.fetchCommerceChannelBySiteGroupId(
					objectEntry.getGroupId());

			if (commerceChannel == null) {
				return;
			}

			ObjectDefinition objectDefinition =
				_objectDefinitionLocalService.getObjectDefinition(
					objectEntry.getObjectDefinitionId());

			_commerceNotificationHelper.sendNotifications(
				commerceChannel.getGroupId(), objectEntry.getUserId(),
				objectDefinition.getClassName() + "#" + action, objectEntry);
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portalException);
			}
		}
	}

	private void _updateCommerceReturn(ObjectEntry originalObjectEntry) {
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				originalObjectEntry.getObjectDefinitionId());

		if (!StringUtil.equals(
				objectDefinition.getName(), "CommerceReturnItem")) {

			return;
		}

		Map<String, Serializable> values = originalObjectEntry.getValues();

		ObjectEntry objectEntry = _objectEntryLocalService.fetchObjectEntry(
			GetterUtil.getLong(
				values.get(
					"r_commerceReturnToCommerceReturnItems_l_" +
						"commerceReturnId")));

		if (objectEntry == null) {
			return;
		}

		try {
			_objectEntryLocalService.updateObjectEntry(
				objectEntry.getUserId(), objectEntry.getObjectEntryId(),
				objectEntry.getObjectEntryFolderId(), objectEntry.getValues(),
				new ServiceContext());
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portalException);
			}
		}
	}

	private void _updateCommerceReturnItemResourcePermissions(
		ObjectEntry objectEntry) {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				objectEntry.getObjectDefinitionId());

		if (!StringUtil.equals(
				objectDefinition.getName(), "CommerceReturnItem")) {

			return;
		}

		Role role = _roleLocalService.fetchRole(
			objectEntry.getCompanyId(), RoleConstants.OWNER);

		if (role == null) {
			return;
		}

		try {
			_resourcePermissionLocalService.removeResourcePermission(
				objectEntry.getCompanyId(), objectDefinition.getClassName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(objectEntry.getObjectEntryId()),
				role.getRoleId(), ActionKeys.DELETE_DISCUSSION);
			_resourcePermissionLocalService.removeResourcePermission(
				objectEntry.getCompanyId(), objectDefinition.getClassName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(objectEntry.getObjectEntryId()),
				role.getRoleId(), ActionKeys.UPDATE_DISCUSSION);
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portalException);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ObjectEntryModelListener.class);

	@Reference
	private CommentManager _commentManager;

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CommerceNotificationHelper _commerceNotificationHelper;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

}