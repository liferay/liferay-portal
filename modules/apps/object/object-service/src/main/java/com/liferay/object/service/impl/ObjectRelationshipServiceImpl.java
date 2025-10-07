/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.impl;

import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.object.service.base.ObjectRelationshipServiceBaseImpl;
import com.liferay.object.service.persistence.ObjectDefinitionPersistence;
import com.liferay.object.system.SystemObjectDefinitionManager;
import com.liferay.object.system.SystemObjectDefinitionManagerRegistry;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 * @author Brian Wing Shun Chan
 */
@Component(
	property = {
		"json.web.service.context.name=object",
		"json.web.service.context.path=ObjectRelationship"
	},
	service = AopService.class
)
public class ObjectRelationshipServiceImpl
	extends ObjectRelationshipServiceBaseImpl {

	@Override
	public ObjectRelationship addObjectRelationship(
			String externalReferenceCode, long objectDefinitionId1,
			long objectDefinitionId2, long parameterObjectFieldId,
			String deletionType, boolean edge, Map<Locale, String> labelMap,
			String name, boolean system, String type, ObjectField objectField)
		throws PortalException {

		ObjectDefinition objectDefinition =
			_objectDefinitionPersistence.findByPrimaryKey(objectDefinitionId1);

		_objectDefinitionModelResourcePermission.check(
			getPermissionChecker(), objectDefinition.getObjectDefinitionId(),
			ActionKeys.UPDATE);

		return objectRelationshipLocalService.addObjectRelationship(
			externalReferenceCode, getUserId(), objectDefinitionId1,
			objectDefinitionId2, parameterObjectFieldId, deletionType, edge,
			labelMap, name, system, type, objectField);
	}

	@Override
	public void addObjectRelationshipMappingTableValues(
			long objectRelationshipId, long primaryKey1, long primaryKey2,
			ServiceContext serviceContext)
		throws PortalException {

		ObjectRelationship objectRelationship =
			objectRelationshipPersistence.findByPrimaryKey(
				objectRelationshipId);

		_checkModelResourcePermission(
			objectRelationship.getObjectDefinitionId2(), primaryKey2,
			ActionKeys.UPDATE);

		if (Objects.equals(
				objectRelationship.getType(),
				ObjectRelationshipConstants.TYPE_MANY_TO_MANY)) {

			_checkModelResourcePermission(
				objectRelationship.getObjectDefinitionId1(), primaryKey1,
				ActionKeys.UPDATE);
		}

		objectRelationshipLocalService.addObjectRelationshipMappingTableValues(
			getUserId(), objectRelationshipId, primaryKey1, primaryKey2,
			serviceContext);
	}

	@Override
	public ObjectRelationship deleteObjectRelationship(
			long objectRelationshipId)
		throws PortalException {

		ObjectRelationship objectRelationship =
			objectRelationshipPersistence.findByPrimaryKey(
				objectRelationshipId);

		_objectDefinitionModelResourcePermission.check(
			getPermissionChecker(), objectRelationship.getObjectDefinitionId1(),
			ActionKeys.UPDATE);

		return objectRelationshipLocalService.deleteObjectRelationship(
			objectRelationshipId);
	}

	@Override
	public ObjectRelationship fetchObjectRelationshipByExternalReferenceCode(
			String externalReferenceCode, long companyId,
			long objectDefinitionId1)
		throws PortalException {

		ObjectRelationship objectRelationship =
			objectRelationshipLocalService.
				fetchObjectRelationshipByExternalReferenceCode(
					externalReferenceCode, companyId, objectDefinitionId1);

		if (objectRelationship != null) {
			_objectDefinitionModelResourcePermission.check(
				getPermissionChecker(), objectDefinitionId1, ActionKeys.VIEW);
		}

		return objectRelationship;
	}

	@Override
	public ObjectRelationship getObjectRelationship(long objectRelationshipId)
		throws PortalException {

		ObjectRelationship objectRelationship =
			objectRelationshipPersistence.findByPrimaryKey(
				objectRelationshipId);

		_objectDefinitionModelResourcePermission.check(
			getPermissionChecker(), objectRelationship.getObjectDefinitionId1(),
			ActionKeys.VIEW);

		return objectRelationshipLocalService.getObjectRelationship(
			objectRelationshipId);
	}

	@Override
	public ObjectRelationship getObjectRelationship(
			long objectDefinitionId1, String name)
		throws PortalException {

		_objectDefinitionModelResourcePermission.check(
			getPermissionChecker(), objectDefinitionId1, ActionKeys.VIEW);

		return objectRelationshipLocalService.getObjectRelationship(
			objectDefinitionId1, name);
	}

	@Override
	public List<ObjectRelationship> getObjectRelationships(
			long objectDefinitionId1, int start, int end)
		throws PortalException {

		ObjectDefinition objectDefinition =
			_objectDefinitionPersistence.findByPrimaryKey(objectDefinitionId1);

		_objectDefinitionModelResourcePermission.check(
			getPermissionChecker(), objectDefinition.getObjectDefinitionId(),
			ActionKeys.VIEW);

		return objectRelationshipLocalService.getObjectRelationships(
			objectDefinitionId1, start, end);
	}

	@Override
	public ObjectRelationship updateObjectRelationship(
			String externalReferenceCode, long objectRelationshipId,
			long parameterObjectFieldId, String deletionType, boolean edge,
			Map<Locale, String> labelMap, ObjectField objectField)
		throws PortalException {

		ObjectRelationship objectRelationship =
			objectRelationshipPersistence.findByPrimaryKey(
				objectRelationshipId);

		_objectDefinitionModelResourcePermission.check(
			getPermissionChecker(), objectRelationship.getObjectDefinitionId1(),
			ActionKeys.UPDATE);

		return objectRelationshipLocalService.updateObjectRelationship(
			externalReferenceCode, objectRelationshipId, parameterObjectFieldId,
			deletionType, edge, labelMap, objectField);
	}

	private void _checkModelResourcePermission(
			long objectDefinitionId, long objectEntryId, String actionId)
		throws PortalException {

		ObjectDefinition objectDefinition =
			_objectDefinitionPersistence.findByPrimaryKey(objectDefinitionId);

		if (objectDefinition.isUnmodifiableSystemObject()) {
			SystemObjectDefinitionManager systemObjectDefinitionManager =
				_systemObjectDefinitionManagerRegistry.
					getSystemObjectDefinitionManager(
						objectDefinition.getName());

			systemObjectDefinitionManager.checkModelResourcePermission(
				objectDefinition.getPrimaryKey(), getPermissionChecker(),
				objectEntryId, actionId);
		}
		else {
			ModelResourcePermission<ObjectEntry> modelResourcePermission =
				_objectEntryService.getModelResourcePermission(
					objectDefinitionId);

			modelResourcePermission.check(
				getPermissionChecker(), objectEntryId, actionId);
		}
	}

	@Reference(
		target = "(model.class.name=com.liferay.object.model.ObjectDefinition)"
	)
	private ModelResourcePermission<ObjectDefinition>
		_objectDefinitionModelResourcePermission;

	@Reference
	private ObjectDefinitionPersistence _objectDefinitionPersistence;

	@Reference
	private ObjectEntryService _objectEntryService;

	@Reference
	private SystemObjectDefinitionManagerRegistry
		_systemObjectDefinitionManagerRegistry;

}