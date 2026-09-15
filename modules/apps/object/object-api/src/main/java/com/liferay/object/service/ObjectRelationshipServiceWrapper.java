/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service;

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link ObjectRelationshipService}.
 *
 * @author Marco Leo
 * @see ObjectRelationshipService
 * @generated
 */
public class ObjectRelationshipServiceWrapper
	implements ObjectRelationshipService,
			   ServiceWrapper<ObjectRelationshipService> {

	public ObjectRelationshipServiceWrapper() {
		this(null);
	}

	public ObjectRelationshipServiceWrapper(
		ObjectRelationshipService objectRelationshipService) {

		_objectRelationshipService = objectRelationshipService;
	}

	@Override
	public com.liferay.object.model.ObjectRelationship addObjectRelationship(
			String externalReferenceCode, long objectDefinitionId1,
			long objectDefinitionId2, long parameterObjectFieldId,
			String deletionType,
			java.util.Map<java.util.Locale, String> descriptionMap,
			boolean edge, java.util.Map<java.util.Locale, String> labelMap,
			String name, boolean system, String type,
			com.liferay.object.model.ObjectField objectField)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectRelationshipService.addObjectRelationship(
			externalReferenceCode, objectDefinitionId1, objectDefinitionId2,
			parameterObjectFieldId, deletionType, descriptionMap, edge,
			labelMap, name, system, type, objectField);
	}

	@Override
	public void addObjectRelationshipMappingTableValues(
			long objectRelationshipId, long primaryKey1, long primaryKey2,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		_objectRelationshipService.addObjectRelationshipMappingTableValues(
			objectRelationshipId, primaryKey1, primaryKey2, serviceContext);
	}

	@Override
	public com.liferay.object.model.ObjectRelationship deleteObjectRelationship(
			long objectRelationshipId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectRelationshipService.deleteObjectRelationship(
			objectRelationshipId);
	}

	@Override
	public com.liferay.object.model.ObjectRelationship
			fetchObjectRelationshipByExternalReferenceCode(
				String externalReferenceCode, long companyId,
				long objectDefinitionId1)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectRelationshipService.
			fetchObjectRelationshipByExternalReferenceCode(
				externalReferenceCode, companyId, objectDefinitionId1);
	}

	@Override
	public com.liferay.object.model.ObjectRelationship getObjectRelationship(
			long objectRelationshipId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectRelationshipService.getObjectRelationship(
			objectRelationshipId);
	}

	@Override
	public com.liferay.object.model.ObjectRelationship getObjectRelationship(
			long objectDefinitionId1, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectRelationshipService.getObjectRelationship(
			objectDefinitionId1, name);
	}

	@Override
	public java.util.List<com.liferay.object.model.ObjectRelationship>
			getObjectRelationships(long objectDefinitionId1, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectRelationshipService.getObjectRelationships(
			objectDefinitionId1, start, end);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _objectRelationshipService.getOSGiServiceIdentifier();
	}

	@Override
	public com.liferay.object.model.ObjectRelationship updateObjectRelationship(
			String externalReferenceCode, long objectRelationshipId,
			long parameterObjectFieldId, String deletionType,
			java.util.Map<java.util.Locale, String> descriptionMap,
			boolean edge, java.util.Map<java.util.Locale, String> labelMap,
			com.liferay.object.model.ObjectField objectField)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectRelationshipService.updateObjectRelationship(
			externalReferenceCode, objectRelationshipId, parameterObjectFieldId,
			deletionType, descriptionMap, edge, labelMap, objectField);
	}

	@Override
	public ObjectRelationshipService getWrappedService() {
		return _objectRelationshipService;
	}

	@Override
	public void setWrappedService(
		ObjectRelationshipService objectRelationshipService) {

		_objectRelationshipService = objectRelationshipService;
	}

	private ObjectRelationshipService _objectRelationshipService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-282406461