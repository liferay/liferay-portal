/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.impl;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectView;
import com.liferay.object.model.ObjectViewColumn;
import com.liferay.object.model.ObjectViewFilterColumn;
import com.liferay.object.model.ObjectViewSortColumn;
import com.liferay.object.service.base.ObjectViewServiceBaseImpl;
import com.liferay.object.service.persistence.ObjectDefinitionPersistence;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Gabriel Albuquerque
 */
@Component(
	property = {
		"json.web.service.context.name=object",
		"json.web.service.context.path=ObjectView"
	},
	service = AopService.class
)
public class ObjectViewServiceImpl extends ObjectViewServiceBaseImpl {

	@Override
	public ObjectView addObjectView(
			String externalReferenceCode, long objectDefinitionId,
			boolean defaultObjectView, Map<Locale, String> nameMap,
			List<ObjectViewColumn> objectViewColumns,
			List<ObjectViewFilterColumn> objectViewFilterColumns,
			List<ObjectViewSortColumn> objectViewSortColumns)
		throws PortalException {

		ObjectDefinition objectDefinition =
			_objectDefinitionPersistence.findByPrimaryKey(objectDefinitionId);

		_objectDefinitionModelResourcePermission.check(
			getPermissionChecker(), objectDefinition.getObjectDefinitionId(),
			ActionKeys.UPDATE);

		return objectViewLocalService.addObjectView(
			externalReferenceCode, getUserId(), objectDefinitionId,
			defaultObjectView, nameMap, objectViewColumns,
			objectViewFilterColumns, objectViewSortColumns);
	}

	@Override
	public ObjectView deleteObjectView(long objectViewId)
		throws PortalException {

		ObjectView objectView = objectViewPersistence.findByPrimaryKey(
			objectViewId);

		_objectDefinitionModelResourcePermission.check(
			getPermissionChecker(), objectView.getObjectDefinitionId(),
			ActionKeys.DELETE);

		return objectViewLocalService.deleteObjectView(objectViewId);
	}

	@Override
	public ObjectView getObjectView(long objectViewId) throws PortalException {
		ObjectView objectView = objectViewPersistence.findByPrimaryKey(
			objectViewId);

		_objectDefinitionModelResourcePermission.check(
			getPermissionChecker(), objectView.getObjectDefinitionId(),
			ActionKeys.VIEW);

		return objectViewLocalService.getObjectView(objectViewId);
	}

	@Override
	public ObjectView updateObjectView(
			String externalReferenceCode, long objectViewId,
			boolean defaultObjectView, Map<Locale, String> nameMap,
			List<ObjectViewColumn> objectViewColumns,
			List<ObjectViewFilterColumn> objectViewFilterColumns,
			List<ObjectViewSortColumn> objectViewSortColumns)
		throws PortalException {

		ObjectView objectView = objectViewPersistence.findByPrimaryKey(
			objectViewId);

		_objectDefinitionModelResourcePermission.check(
			getPermissionChecker(), objectView.getObjectDefinitionId(),
			ActionKeys.UPDATE);

		return objectViewLocalService.updateObjectView(
			externalReferenceCode, objectViewId, defaultObjectView, nameMap,
			objectViewColumns, objectViewFilterColumns, objectViewSortColumns);
	}

	@Reference(
		target = "(model.class.name=com.liferay.object.model.ObjectDefinition)"
	)
	private ModelResourcePermission<ObjectDefinition>
		_objectDefinitionModelResourcePermission;

	@Reference
	private ObjectDefinitionPersistence _objectDefinitionPersistence;

}