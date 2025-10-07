/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service;

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link ObjectEntryService}.
 *
 * @author Marco Leo
 * @see ObjectEntryService
 * @generated
 */
public class ObjectEntryServiceWrapper
	implements ObjectEntryService, ServiceWrapper<ObjectEntryService> {

	public ObjectEntryServiceWrapper() {
		this(null);
	}

	public ObjectEntryServiceWrapper(ObjectEntryService objectEntryService) {
		_objectEntryService = objectEntryService;
	}

	@Override
	public com.liferay.object.model.ObjectEntry addObjectEntry(
			long groupId, long objectDefinitionId, long objectEntryFolderId,
			String defaultLanguageId,
			java.util.Map<String, java.io.Serializable> values,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryService.addObjectEntry(
			groupId, objectDefinitionId, objectEntryFolderId, defaultLanguageId,
			values, serviceContext);
	}

	@Override
	public com.liferay.object.model.ObjectEntry addOrUpdateObjectEntry(
			String externalReferenceCode, long groupId, long objectDefinitionId,
			long objectEntryFolderId,
			java.util.Map<String, java.io.Serializable> values,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryService.addOrUpdateObjectEntry(
			externalReferenceCode, groupId, objectDefinitionId,
			objectEntryFolderId, values, serviceContext);
	}

	@Override
	public void checkModelResourcePermission(
			long objectDefinitionId, long objectEntryId, String actionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_objectEntryService.checkModelResourcePermission(
			objectDefinitionId, objectEntryId, actionId);
	}

	@Override
	public com.liferay.object.model.ObjectEntry deleteObjectEntry(
			long objectEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryService.deleteObjectEntry(objectEntryId);
	}

	@Override
	public com.liferay.object.model.ObjectEntry expireObjectEntry(
			long objectEntryId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryService.expireObjectEntry(
			objectEntryId, serviceContext);
	}

	@Override
	public com.liferay.object.model.ObjectEntry fetchManyToOneObjectEntry(
			long groupId, long objectRelationshipId, long primaryKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryService.fetchManyToOneObjectEntry(
			groupId, objectRelationshipId, primaryKey);
	}

	@Override
	public com.liferay.object.model.ObjectEntry fetchObjectEntry(
			long objectEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryService.fetchObjectEntry(objectEntryId);
	}

	@Override
	public com.liferay.object.model.ObjectEntry fetchObjectEntry(
			String externalReferenceCode, long groupId, long objectDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryService.fetchObjectEntry(
			externalReferenceCode, groupId, objectDefinitionId);
	}

	@Override
	public java.util.List<com.liferay.object.model.ObjectEntry>
			getManyToManyObjectEntries(
				long groupId, long objectRelationshipId, long primaryKey,
				boolean related, boolean reverse, String search, int start,
				int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryService.getManyToManyObjectEntries(
			groupId, objectRelationshipId, primaryKey, related, reverse, search,
			start, end);
	}

	@Override
	public int getManyToManyObjectEntriesCount(
			long groupId, long objectRelationshipId, long primaryKey,
			boolean related, boolean reverse, String search)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryService.getManyToManyObjectEntriesCount(
			groupId, objectRelationshipId, primaryKey, related, reverse,
			search);
	}

	@Override
	public com.liferay.portal.kernel.security.permission.resource.
		ModelResourcePermission<com.liferay.object.model.ObjectEntry>
				getModelResourcePermission(long objectDefinitionId)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryService.getModelResourcePermission(
			objectDefinitionId);
	}

	@Override
	public com.liferay.object.model.ObjectEntry getObjectEntry(
			long objectEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryService.getObjectEntry(objectEntryId);
	}

	@Override
	public com.liferay.object.model.ObjectEntry getObjectEntry(
			String externalReferenceCode, long groupId, long objectDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryService.getObjectEntry(
			externalReferenceCode, groupId, objectDefinitionId);
	}

	@Override
	public java.util.List<com.liferay.object.model.ObjectEntry>
			getOneToManyObjectEntries(
				long groupId, long objectRelationshipId,
				com.liferay.petra.sql.dsl.expression.Predicate predicate,
				boolean preferApproved, long primaryKey, boolean related,
				String search, int start, int end,
				com.liferay.portal.kernel.search.Sort[] sorts)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryService.getOneToManyObjectEntries(
			groupId, objectRelationshipId, predicate, preferApproved,
			primaryKey, related, search, start, end, sorts);
	}

	@Override
	public int getOneToManyObjectEntriesCount(
			long groupId, long objectRelationshipId,
			com.liferay.petra.sql.dsl.expression.Predicate predicate,
			long primaryKey, boolean related, String search)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryService.getOneToManyObjectEntriesCount(
			groupId, objectRelationshipId, predicate, primaryKey, related,
			search);
	}

	@Override
	public com.liferay.object.model.ObjectEntry getOrAddEmptyObjectEntry(
			String externalReferenceCode, long groupId, long objectDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryService.getOrAddEmptyObjectEntry(
			externalReferenceCode, groupId, objectDefinitionId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _objectEntryService.getOSGiServiceIdentifier();
	}

	@Override
	public boolean hasModelResourcePermission(
			long objectDefinitionId, long objectEntryId, String actionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryService.hasModelResourcePermission(
			objectDefinitionId, objectEntryId, actionId);
	}

	@Override
	public boolean hasModelResourcePermission(
			com.liferay.object.model.ObjectEntry objectEntry, String actionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryService.hasModelResourcePermission(
			objectEntry, actionId);
	}

	@Override
	public boolean hasModelResourcePermission(
			com.liferay.portal.kernel.model.User user, long objectEntryId,
			String actionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryService.hasModelResourcePermission(
			user, objectEntryId, actionId);
	}

	@Override
	public boolean hasPortletResourcePermission(
			long groupId, long objectDefinitionId, String actionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryService.hasPortletResourcePermission(
			groupId, objectDefinitionId, actionId);
	}

	@Override
	public com.liferay.object.model.ObjectEntry moveObjectEntryToTrash(
			com.liferay.object.model.ObjectEntry objectEntry,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryService.moveObjectEntryToTrash(
			objectEntry, serviceContext);
	}

	@Override
	public com.liferay.object.model.ObjectEntry partialUpdateObjectEntry(
			long objectEntryId, long objectEntryFolderId,
			java.util.Map<String, java.io.Serializable> values,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryService.partialUpdateObjectEntry(
			objectEntryId, objectEntryFolderId, values, serviceContext);
	}

	@Override
	public com.liferay.object.model.ObjectEntry restoreObjectEntryFromTrash(
			com.liferay.object.model.ObjectEntry objectEntry,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryService.restoreObjectEntryFromTrash(
			objectEntry, serviceContext);
	}

	@Override
	public void subscribeObjectEntry(long groupId, long objectEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_objectEntryService.subscribeObjectEntry(groupId, objectEntryId);
	}

	@Override
	public void unsubscribeObjectEntry(long objectEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_objectEntryService.unsubscribeObjectEntry(objectEntryId);
	}

	@Override
	public com.liferay.object.model.ObjectEntry updateObjectEntry(
			long objectEntryId, long objectEntryFolderId,
			java.util.Map<String, java.io.Serializable> values,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryService.updateObjectEntry(
			objectEntryId, objectEntryFolderId, values, serviceContext);
	}

	@Override
	public void validate(
			long groupId, com.liferay.object.model.ObjectEntry objectEntry,
			java.util.List<String> objectValidationRuleExternalReferenceCodes,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		_objectEntryService.validate(
			groupId, objectEntry, objectValidationRuleExternalReferenceCodes,
			serviceContext);
	}

	@Override
	public ObjectEntryService getWrappedService() {
		return _objectEntryService;
	}

	@Override
	public void setWrappedService(ObjectEntryService objectEntryService) {
		_objectEntryService = objectEntryService;
	}

	private ObjectEntryService _objectEntryService;

}