/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.impl;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectFolderItem;
import com.liferay.object.model.ObjectFolderItemTable;
import com.liferay.object.relationship.util.ObjectRelationshipUtil;
import com.liferay.object.service.base.ObjectFolderItemLocalServiceBaseImpl;
import com.liferay.object.service.persistence.ObjectDefinitionPersistence;
import com.liferay.object.service.persistence.ObjectFolderPersistence;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.InlineSQLHelper;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Murilo Stodolni
 */
@Component(
	property = "model.class.name=com.liferay.object.model.ObjectFolderItem",
	service = AopService.class
)
public class ObjectFolderItemLocalServiceImpl
	extends ObjectFolderItemLocalServiceBaseImpl {

	@Override
	public ObjectFolderItem addObjectFolderItem(
			long userId, long objectDefinitionId, long objectFolderId,
			int positionX, int positionY)
		throws PortalException {

		_objectDefinitionPersistence.findByPrimaryKey(objectDefinitionId);
		_objectFolderPersistence.findByPrimaryKey(objectFolderId);

		ObjectFolderItem objectFolderItem = objectFolderItemPersistence.create(
			counterLocalService.increment());

		User user = _userLocalService.getUser(userId);

		objectFolderItem.setCompanyId(user.getCompanyId());
		objectFolderItem.setUserId(user.getUserId());
		objectFolderItem.setUserName(user.getFullName());

		objectFolderItem.setObjectDefinitionId(objectDefinitionId);
		objectFolderItem.setObjectFolderId(objectFolderId);
		objectFolderItem.setPositionX(positionX);
		objectFolderItem.setPositionY(positionY);

		return objectFolderItemPersistence.update(objectFolderItem);
	}

	@Override
	public ObjectFolderItem deleteObjectFolderItem(
			long objectDefinitionId, long objectFolderId)
		throws PortalException {

		ObjectFolderItem objectFolderItem =
			objectFolderItemPersistence.findByODI_OFI(
				objectDefinitionId, objectFolderId);

		return objectFolderItemLocalService.deleteObjectFolderItem(
			objectFolderItem);
	}

	@Override
	public ObjectFolderItem deleteObjectFolderItem(
			ObjectFolderItem objectFolderItem)
		throws PortalException {

		ObjectDefinition objectDefinition =
			_objectDefinitionPersistence.findByPrimaryKey(
				objectFolderItem.getObjectDefinitionId());

		if (!objectDefinition.isLinkedToObjectFolder(
				objectFolderItem.getObjectFolderId())) {

			return objectFolderItem;
		}

		for (ObjectDefinition relatedObjectDefinition :
				ObjectRelationshipUtil.getRelatedObjectDefinitions(
					objectDefinition)) {

			if (!relatedObjectDefinition.isLinkedToObjectFolder(
					objectFolderItem.getObjectFolderId())) {

				return objectFolderItem;
			}
		}

		return objectFolderItemPersistence.remove(objectFolderItem);
	}

	@Override
	public void deleteObjectFolderItemByObjectDefinitionId(
		long objectDefinitionId) {

		List<ObjectFolderItem> objectFolderItems =
			objectFolderItemPersistence.findByObjectDefinitionId(
				objectDefinitionId);

		for (ObjectFolderItem objectFolderItem : objectFolderItems) {
			objectFolderItemPersistence.remove(objectFolderItem);
		}
	}

	@Override
	public void deleteObjectFolderItemByObjectFolderId(long objectFolderId) {
		List<ObjectFolderItem> objectFolderItems =
			objectFolderItemPersistence.findByObjectFolderId(objectFolderId);

		for (ObjectFolderItem objectFolderItem : objectFolderItems) {
			objectFolderItemPersistence.remove(objectFolderItem);
		}
	}

	@Override
	public List<ObjectFolderItem> getObjectFolderItemsByObjectFolderId(
		long objectFolderId) {

		DSLQuery dslQuery = DSLQueryFactoryUtil.select(
			ObjectFolderItemTable.INSTANCE
		).from(
			ObjectFolderItemTable.INSTANCE
		).where(
			() -> {
				Predicate predicate =
					ObjectFolderItemTable.INSTANCE.objectFolderId.eq(
						objectFolderId);

				if ((PermissionThreadLocal.getPermissionChecker() == null) ||
					!_inlineSQLHelper.isEnabled()) {

					return predicate;
				}

				Predicate permissionWherePredicate =
					_inlineSQLHelper.getPermissionWherePredicate(
						ObjectDefinition.class.getName(),
						ObjectFolderItemTable.INSTANCE.objectDefinitionId);

				if (permissionWherePredicate != null) {
					predicate = predicate.and(permissionWherePredicate);
				}

				return predicate;
			}
		);

		return dslQuery(dslQuery);
	}

	@Override
	public ObjectFolderItem updateObjectFolderItem(
			long objectDefinitionId, long objectFolderId, int positionX,
			int positionY)
		throws PortalException {

		ObjectFolderItem objectFolderItem =
			objectFolderItemPersistence.findByODI_OFI(
				objectDefinitionId, objectFolderId);

		objectFolderItem.setPositionX(positionX);
		objectFolderItem.setPositionY(positionY);

		return objectFolderItemPersistence.update(objectFolderItem);
	}

	@Override
	public void updateObjectFolderObjectFolderItem(
			long objectDefinitionId, long newObjectFolderId,
			long oldObjectFolderId)
		throws PortalException {

		if (newObjectFolderId == oldObjectFolderId) {
			return;
		}

		ObjectDefinition objectDefinition =
			_objectDefinitionPersistence.findByPrimaryKey(objectDefinitionId);

		_updateObjectFolderObjectFolderItem(
			objectDefinitionId, newObjectFolderId, oldObjectFolderId,
			objectDefinition.getUserId());

		for (ObjectDefinition relatedObjectDefinition :
				ObjectRelationshipUtil.getRelatedObjectDefinitions(
					objectDefinition)) {

			_updateObjectFolderObjectFolderItem(
				relatedObjectDefinition.getObjectDefinitionId(),
				newObjectFolderId, oldObjectFolderId,
				objectDefinition.getUserId());
		}
	}

	private void _updateObjectFolderObjectFolderItem(
			long objectDefinitionId, long newObjectFolderId,
			long oldObjectFolderId, long userId)
		throws PortalException {

		objectFolderItemLocalService.deleteObjectFolderItem(
			objectDefinitionId, oldObjectFolderId);

		ObjectFolderItem objectFolderItem =
			objectFolderItemPersistence.fetchByODI_OFI(
				objectDefinitionId, newObjectFolderId);

		if (objectFolderItem != null) {
			return;
		}

		objectFolderItemLocalService.addObjectFolderItem(
			userId, objectDefinitionId, newObjectFolderId, 0, 0);
	}

	@Reference
	private InlineSQLHelper _inlineSQLHelper;

	@Reference
	private ObjectDefinitionPersistence _objectDefinitionPersistence;

	@Reference
	private ObjectFolderPersistence _objectFolderPersistence;

	@Reference
	private UserLocalService _userLocalService;

}