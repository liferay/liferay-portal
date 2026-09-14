/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.impl;

import com.liferay.object.constants.ObjectActionKeys;
import com.liferay.object.constants.ObjectConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFieldSetting;
import com.liferay.object.service.base.ObjectFieldServiceBaseImpl;
import com.liferay.object.service.persistence.ObjectDefinitionPersistence;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;

import java.util.List;
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
		"json.web.service.context.path=ObjectField"
	},
	service = AopService.class
)
public class ObjectFieldServiceImpl extends ObjectFieldServiceBaseImpl {

	@Override
	public ObjectField addCustomObjectField(
			String externalReferenceCode, long listTypeDefinitionId,
			long objectDefinitionId, String businessType, String dbType,
			Map<Locale, String> descriptionMap, boolean indexed,
			boolean indexedAsKeyword, String indexedLanguageId,
			Map<Locale, String> labelMap, boolean localized, String name,
			String readOnly, String readOnlyConditionExpression,
			boolean required, boolean state,
			List<ObjectFieldSetting> objectFieldSettings)
		throws PortalException {

		ObjectDefinition objectDefinition =
			_objectDefinitionPersistence.findByPrimaryKey(objectDefinitionId);

		if (objectDefinition.isUnmodifiableSystemObject()) {
			_portletResourcePermission.check(
				getPermissionChecker(), null,
				ObjectActionKeys.EXTEND_SYSTEM_OBJECT_DEFINITION);
		}
		else {
			_objectDefinitionModelResourcePermission.check(
				getPermissionChecker(),
				objectDefinition.getObjectDefinitionId(), ActionKeys.UPDATE);
		}

		return objectFieldLocalService.addCustomObjectField(
			externalReferenceCode, getUserId(), listTypeDefinitionId,
			objectDefinitionId, businessType, dbType, descriptionMap, indexed,
			indexedAsKeyword, indexedLanguageId, labelMap, localized, name,
			readOnly, readOnlyConditionExpression, required, state,
			objectFieldSettings);
	}

	@Override
	public ObjectField deleteObjectField(long objectFieldId) throws Exception {
		ObjectField objectField = objectFieldPersistence.findByPrimaryKey(
			objectFieldId);

		_objectDefinitionModelResourcePermission.check(
			getPermissionChecker(), objectField.getObjectDefinitionId(),
			ActionKeys.UPDATE);

		return objectFieldLocalService.deleteObjectField(objectFieldId);
	}

	@Override
	public ObjectField getObjectField(long objectFieldId)
		throws PortalException {

		ObjectField objectField = objectFieldPersistence.findByPrimaryKey(
			objectFieldId);

		_objectDefinitionModelResourcePermission.check(
			getPermissionChecker(), objectField.getObjectDefinitionId(),
			ActionKeys.VIEW);

		return objectFieldPersistence.findByPrimaryKey(objectFieldId);
	}

	@Override
	public ObjectField updateObjectField(
			String externalReferenceCode, long objectFieldId,
			long listTypeDefinitionId, String businessType, String dbType,
			Map<Locale, String> descriptionMap, boolean indexed,
			boolean indexedAsKeyword, String indexedLanguageId,
			Map<Locale, String> labelMap, boolean localized, String name,
			String readOnly, String readOnlyConditionExpression,
			boolean required, boolean state,
			List<ObjectFieldSetting> objectFieldSettings)
		throws PortalException {

		ObjectField objectField = objectFieldPersistence.findByPrimaryKey(
			objectFieldId);

		_objectDefinitionModelResourcePermission.check(
			getPermissionChecker(), objectField.getObjectDefinitionId(),
			ActionKeys.UPDATE);

		return objectFieldLocalService.updateObjectField(
			externalReferenceCode, objectFieldId, getUserId(),
			listTypeDefinitionId, objectField.getObjectDefinitionId(),
			businessType, objectField.getDBColumnName(),
			objectField.getDBTableName(), dbType, descriptionMap, indexed,
			indexedAsKeyword, indexedLanguageId, labelMap, localized, name,
			readOnly, readOnlyConditionExpression, required, state,
			objectField.isSystem(), objectFieldSettings);
	}

	@Reference(
		target = "(model.class.name=com.liferay.object.model.ObjectDefinition)"
	)
	private ModelResourcePermission<ObjectDefinition>
		_objectDefinitionModelResourcePermission;

	@Reference
	private ObjectDefinitionPersistence _objectDefinitionPersistence;

	@Reference(target = "(resource.name=" + ObjectConstants.RESOURCE_NAME + ")")
	private PortletResourcePermission _portletResourcePermission;

}