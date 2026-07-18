/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.impl;

import com.liferay.object.model.ObjectAction;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.base.ObjectActionServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.UnicodeProperties;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 */
@Component(
	property = {
		"json.web.service.context.name=object",
		"json.web.service.context.path=ObjectAction"
	},
	service = AopService.class
)
public class ObjectActionServiceImpl extends ObjectActionServiceBaseImpl {

	@Override
	public ObjectAction addObjectAction(
			String externalReferenceCode, long objectDefinitionId,
			boolean active, String conditionExpression, String description,
			Map<Locale, String> errorMessageMap, Map<Locale, String> labelMap,
			String name, String objectActionExecutorKey,
			String objectActionTriggerKey,
			UnicodeProperties parametersUnicodeProperties, boolean system)
		throws PortalException {

		_objectDefinitionModelResourcePermission.check(
			getPermissionChecker(), objectDefinitionId, ActionKeys.UPDATE);

		return objectActionLocalService.addObjectAction(
			externalReferenceCode, getUserId(), objectDefinitionId, active,
			conditionExpression, description, errorMessageMap, labelMap, name,
			objectActionExecutorKey, objectActionTriggerKey,
			parametersUnicodeProperties, system);
	}

	@Override
	public ObjectAction deleteObjectAction(long objectActionId)
		throws PortalException {

		ObjectAction objectAction = objectActionPersistence.findByPrimaryKey(
			objectActionId);

		_objectDefinitionModelResourcePermission.check(
			getPermissionChecker(), objectAction.getObjectDefinitionId(),
			ActionKeys.UPDATE);

		return objectActionLocalService.deleteObjectAction(objectAction);
	}

	@Override
	public ObjectAction getObjectAction(long objectActionId)
		throws PortalException {

		ObjectAction objectAction = objectActionPersistence.findByPrimaryKey(
			objectActionId);

		_objectDefinitionModelResourcePermission.check(
			getPermissionChecker(), objectAction.getObjectDefinitionId(),
			ActionKeys.VIEW);

		return objectActionPersistence.findByPrimaryKey(objectActionId);
	}

	@Override
	public ObjectAction updateObjectAction(
			String externalReferenceCode, long objectActionId, boolean active,
			String conditionExpression, String description,
			Map<Locale, String> errorMessageMap, Map<Locale, String> labelMap,
			String name, String objectActionExecutorKey,
			String objectActionTriggerKey,
			UnicodeProperties parametersUnicodeProperties)
		throws PortalException {

		ObjectAction objectAction = objectActionPersistence.findByPrimaryKey(
			objectActionId);

		_objectDefinitionModelResourcePermission.check(
			getPermissionChecker(), objectAction.getObjectDefinitionId(),
			ActionKeys.UPDATE);

		return objectActionLocalService.updateObjectAction(
			externalReferenceCode, objectActionId, active, conditionExpression,
			description, errorMessageMap, labelMap, name,
			objectActionExecutorKey, objectActionTriggerKey,
			parametersUnicodeProperties);
	}

	@Reference(
		target = "(model.class.name=com.liferay.object.model.ObjectDefinition)"
	)
	private ModelResourcePermission<ObjectDefinition>
		_objectDefinitionModelResourcePermission;

}