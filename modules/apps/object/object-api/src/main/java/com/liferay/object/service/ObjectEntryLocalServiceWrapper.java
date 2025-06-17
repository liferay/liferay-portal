/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link ObjectEntryLocalService}.
 *
 * @author Marco Leo
 * @see ObjectEntryLocalService
 * @generated
 */
public class ObjectEntryLocalServiceWrapper
	implements ObjectEntryLocalService,
			   ServiceWrapper<ObjectEntryLocalService> {

	public ObjectEntryLocalServiceWrapper() {
		this(null);
	}

	public ObjectEntryLocalServiceWrapper(
		ObjectEntryLocalService objectEntryLocalService) {

		_objectEntryLocalService = objectEntryLocalService;
	}

	@Override
	public com.liferay.object.model.ObjectEntry addObjectEntry(
			long userId, long groupId, long objectDefinitionId,
			long objectEntryFolderId, String defaultLanguageId,
			java.util.Map<String, java.io.Serializable> values,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryLocalService.addObjectEntry(
			userId, groupId, objectDefinitionId, objectEntryFolderId,
			defaultLanguageId, values, serviceContext);
	}

	/**
	 * Adds the object entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectEntry the object entry
	 * @return the object entry that was added
	 */
	@Override
	public com.liferay.object.model.ObjectEntry addObjectEntry(
		com.liferay.object.model.ObjectEntry objectEntry) {

		return _objectEntryLocalService.addObjectEntry(objectEntry);
	}

	@Override
	public com.liferay.object.model.ObjectEntry addObjectEntry(
			String externalReferenceCode, long userId,
			com.liferay.object.model.ObjectDefinition objectDefinition,
			long objectEntryFolderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryLocalService.addObjectEntry(
			externalReferenceCode, userId, objectDefinition,
			objectEntryFolderId);
	}

	@Override
	public void addOrUpdateExtensionDynamicObjectDefinitionTableValues(
			long userId,
			com.liferay.object.model.ObjectDefinition objectDefinition,
			long primaryKey, java.util.Map<String, java.io.Serializable> values,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		_objectEntryLocalService.
			addOrUpdateExtensionDynamicObjectDefinitionTableValues(
				userId, objectDefinition, primaryKey, values, serviceContext);
	}

	@Override
	public com.liferay.object.model.ObjectEntry addOrUpdateObjectEntry(
			String externalReferenceCode, long userId, long groupId,
			long objectDefinitionId, long objectEntryFolderId,
			java.util.Map<String, java.io.Serializable> values,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryLocalService.addOrUpdateObjectEntry(
			externalReferenceCode, userId, groupId, objectDefinitionId,
			objectEntryFolderId, values, serviceContext);
	}

	@Override
	public void checkObjectEntries(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_objectEntryLocalService.checkObjectEntries(companyId);
	}

	/**
	 * Creates a new object entry with the primary key. Does not add the object entry to the database.
	 *
	 * @param objectEntryId the primary key for the new object entry
	 * @return the new object entry
	 */
	@Override
	public com.liferay.object.model.ObjectEntry createObjectEntry(
		long objectEntryId) {

		return _objectEntryLocalService.createObjectEntry(objectEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryLocalService.createPersistedModel(primaryKeyObj);
	}

	@Override
	public void deleteExtensionDynamicObjectDefinitionTableValues(
			com.liferay.object.model.ObjectDefinition objectDefinition,
			long primaryKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		_objectEntryLocalService.
			deleteExtensionDynamicObjectDefinitionTableValues(
				objectDefinition, primaryKey);
	}

	/**
	 * Deletes the object entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectEntryId the primary key of the object entry
	 * @return the object entry that was removed
	 * @throws PortalException if a object entry with the primary key could not be found
	 */
	@Override
	public com.liferay.object.model.ObjectEntry deleteObjectEntry(
			long objectEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryLocalService.deleteObjectEntry(objectEntryId);
	}

	/**
	 * Deletes the object entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectEntry the object entry
	 * @return the object entry that was removed
	 * @throws PortalException
	 */
	@Override
	public com.liferay.object.model.ObjectEntry deleteObjectEntry(
			com.liferay.object.model.ObjectEntry objectEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryLocalService.deleteObjectEntry(objectEntry);
	}

	@Override
	public com.liferay.object.model.ObjectEntry deleteObjectEntry(
			String externalReferenceCode, long companyId, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryLocalService.deleteObjectEntry(
			externalReferenceCode, companyId, groupId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public void deleteRelatedObjectEntries(
			long groupId, long objectDefinitionId, long primaryKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		_objectEntryLocalService.deleteRelatedObjectEntries(
			groupId, objectDefinitionId, primaryKey);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _objectEntryLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _objectEntryLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _objectEntryLocalService.dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _objectEntryLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @return the range of matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _objectEntryLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _objectEntryLocalService.dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _objectEntryLocalService.dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return _objectEntryLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.object.model.ObjectEntry expireObjectEntry(
			long userId, long objectEntryId, int version,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryLocalService.expireObjectEntry(
			userId, objectEntryId, version, serviceContext);
	}

	@Override
	public com.liferay.object.model.ObjectEntry fetchManyToOneObjectEntry(
			long groupId, long objectRelationshipId, long primaryKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryLocalService.fetchManyToOneObjectEntry(
			groupId, objectRelationshipId, primaryKey);
	}

	@Override
	public com.liferay.object.model.ObjectEntry fetchObjectEntry(
		long objectEntryId) {

		return _objectEntryLocalService.fetchObjectEntry(objectEntryId);
	}

	@Override
	public com.liferay.object.model.ObjectEntry fetchObjectEntry(
		long groupId,
		com.liferay.object.model.ObjectDefinition objectDefinition,
		String urlTitle) {

		return _objectEntryLocalService.fetchObjectEntry(
			groupId, objectDefinition, urlTitle);
	}

	@Override
	public com.liferay.object.model.ObjectEntry fetchObjectEntry(
		String externalReferenceCode, long objectDefinitionId) {

		return _objectEntryLocalService.fetchObjectEntry(
			externalReferenceCode, objectDefinitionId);
	}

	/**
	 * Returns the object entry matching the UUID and group.
	 *
	 * @param uuid the object entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	@Override
	public com.liferay.object.model.ObjectEntry
		fetchObjectEntryByUuidAndGroupId(String uuid, long groupId) {

		return _objectEntryLocalService.fetchObjectEntryByUuidAndGroupId(
			uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _objectEntryLocalService.getActionableDynamicQuery();
	}

	@Override
	public java.util.Map<Object, Long> getAggregationCounts(
			long groupId, long objectDefinitionId, String aggregationTerm,
			com.liferay.petra.sql.dsl.expression.Predicate predicate, int start,
			int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryLocalService.getAggregationCounts(
			groupId, objectDefinitionId, aggregationTerm, predicate, start,
			end);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _objectEntryLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public java.util.Map<String, java.io.Serializable>
			getExtensionDynamicObjectDefinitionTableValues(
				com.liferay.object.model.ObjectDefinition objectDefinition,
				long primaryKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryLocalService.
			getExtensionDynamicObjectDefinitionTableValues(
				objectDefinition, primaryKey);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _objectEntryLocalService.getIndexableActionableDynamicQuery();
	}

	@Override
	public java.util.List<com.liferay.object.model.ObjectEntry>
			getManyToManyObjectEntries(
				long groupId, long objectRelationshipId, long primaryKey,
				boolean related, boolean reverse, String search, int start,
				int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryLocalService.getManyToManyObjectEntries(
			groupId, objectRelationshipId, primaryKey, related, reverse, search,
			start, end);
	}

	@Override
	public int getManyToManyObjectEntriesCount(
			long groupId, long objectRelationshipId, long primaryKey,
			boolean related, boolean reverse, String search)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryLocalService.getManyToManyObjectEntriesCount(
			groupId, objectRelationshipId, primaryKey, related, reverse,
			search);
	}

	/**
	 * Returns a range of all the object entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @return the range of object entries
	 */
	@Override
	public java.util.List<com.liferay.object.model.ObjectEntry>
		getObjectEntries(int start, int end) {

		return _objectEntryLocalService.getObjectEntries(start, end);
	}

	@Override
	public java.util.List<com.liferay.object.model.ObjectEntry>
		getObjectEntries(
			long groupId, long objectDefinitionId, int start, int end) {

		return _objectEntryLocalService.getObjectEntries(
			groupId, objectDefinitionId, start, end);
	}

	@Override
	public java.util.List<com.liferay.object.model.ObjectEntry>
		getObjectEntries(
			long groupId, long objectDefinitionId, int status, int start,
			int end) {

		return _objectEntryLocalService.getObjectEntries(
			groupId, objectDefinitionId, status, start, end);
	}

	/**
	 * Returns all the object entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the object entries
	 * @param companyId the primary key of the company
	 * @return the matching object entries, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<com.liferay.object.model.ObjectEntry>
		getObjectEntriesByUuidAndCompanyId(String uuid, long companyId) {

		return _objectEntryLocalService.getObjectEntriesByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of object entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the object entries
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching object entries, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<com.liferay.object.model.ObjectEntry>
		getObjectEntriesByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.object.model.ObjectEntry> orderByComparator) {

		return _objectEntryLocalService.getObjectEntriesByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of object entries.
	 *
	 * @return the number of object entries
	 */
	@Override
	public int getObjectEntriesCount() {
		return _objectEntryLocalService.getObjectEntriesCount();
	}

	@Override
	public int getObjectEntriesCount(long objectDefinitionId) {
		return _objectEntryLocalService.getObjectEntriesCount(
			objectDefinitionId);
	}

	@Override
	public long getObjectEntriesCount(
			long userId, java.util.Date createDate, long objectDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryLocalService.getObjectEntriesCount(
			userId, createDate, objectDefinitionId);
	}

	@Override
	public int getObjectEntriesCount(long groupId, long objectDefinitionId) {
		return _objectEntryLocalService.getObjectEntriesCount(
			groupId, objectDefinitionId);
	}

	@Override
	public long getObjectEntriesCount(
			long groupId,
			com.liferay.object.model.ObjectDefinition objectDefinition,
			com.liferay.petra.sql.dsl.expression.Predicate predicate)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryLocalService.getObjectEntriesCount(
			groupId, objectDefinition, predicate);
	}

	/**
	 * Returns the object entry with the primary key.
	 *
	 * @param objectEntryId the primary key of the object entry
	 * @return the object entry
	 * @throws PortalException if a object entry with the primary key could not be found
	 */
	@Override
	public com.liferay.object.model.ObjectEntry getObjectEntry(
			long objectEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryLocalService.getObjectEntry(objectEntryId);
	}

	@Override
	public com.liferay.object.model.ObjectEntry getObjectEntry(
			String externalReferenceCode, long objectDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryLocalService.getObjectEntry(
			externalReferenceCode, objectDefinitionId);
	}

	@Override
	public com.liferay.object.model.ObjectEntry getObjectEntry(
			String externalReferenceCode, long companyId, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryLocalService.getObjectEntry(
			externalReferenceCode, companyId, groupId);
	}

	/**
	 * Returns the object entry matching the UUID and group.
	 *
	 * @param uuid the object entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching object entry
	 * @throws PortalException if a matching object entry could not be found
	 */
	@Override
	public com.liferay.object.model.ObjectEntry getObjectEntryByUuidAndGroupId(
			String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryLocalService.getObjectEntryByUuidAndGroupId(
			uuid, groupId);
	}

	@Override
	public java.util.List<com.liferay.object.model.ObjectEntry>
		getObjectEntryFolderObjectEntries(
			long groupId, long objectEntryFolderId, int start, int end) {

		return _objectEntryLocalService.getObjectEntryFolderObjectEntries(
			groupId, objectEntryFolderId, start, end);
	}

	@Override
	public int getObjectEntryFolderObjectEntriesCount(
		long groupId, long objectEntryFolderId) {

		return _objectEntryLocalService.getObjectEntryFolderObjectEntriesCount(
			groupId, objectEntryFolderId);
	}

	@Override
	public java.util.List<com.liferay.object.model.ObjectEntry>
			getOneToManyObjectEntries(
				long groupId, long objectRelationshipId, long primaryKey,
				boolean related, String search, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryLocalService.getOneToManyObjectEntries(
			groupId, objectRelationshipId, primaryKey, related, search, start,
			end);
	}

	@Override
	public int getOneToManyObjectEntriesCount(
			long groupId, long objectRelationshipId, long primaryKey,
			boolean related, String search)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryLocalService.getOneToManyObjectEntriesCount(
			groupId, objectRelationshipId, primaryKey, related, search);
	}

	@Override
	public com.liferay.object.model.ObjectEntry getOrAddIncompleteObjectEntry(
			String externalReferenceCode, long userId, long objectDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryLocalService.getOrAddIncompleteObjectEntry(
			externalReferenceCode, userId, objectDefinitionId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _objectEntryLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public java.util.List<Long> getPrimaryKeys(
			long groupId, long companyId, long userId, long objectDefinitionId,
			com.liferay.petra.sql.dsl.expression.Predicate predicate,
			String search, int start, int end,
			com.liferay.portal.kernel.search.Sort[] sorts)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryLocalService.getPrimaryKeys(
			groupId, companyId, userId, objectDefinitionId, predicate, search,
			start, end, sorts);
	}

	@Override
	public java.util.Map<String, Object> getSystemModelAttributes(
			com.liferay.object.model.ObjectDefinition objectDefinition,
			long primaryKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryLocalService.getSystemModelAttributes(
			objectDefinition, primaryKey);
	}

	@Override
	public java.util.Map<String, java.io.Serializable> getSystemValues(
			com.liferay.object.model.ObjectEntry objectEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryLocalService.getSystemValues(objectEntry);
	}

	@Override
	public String getTitleValue(long objectDefinitionId, long primaryKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryLocalService.getTitleValue(
			objectDefinitionId, primaryKey);
	}

	@Override
	public java.util.Map<String, java.io.Serializable> getValues(
			long objectEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryLocalService.getValues(objectEntryId);
	}

	@Override
	public java.util.Map<String, java.io.Serializable> getValues(
			com.liferay.object.model.ObjectEntry objectEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryLocalService.getValues(objectEntry);
	}

	@Override
	public java.util.List<java.util.Map<String, java.io.Serializable>>
			getValuesList(
				long groupId, long companyId, long userId,
				long objectDefinitionId,
				com.liferay.petra.sql.dsl.expression.Predicate predicate,
				String search, int start, int end,
				com.liferay.portal.kernel.search.Sort[] sorts)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryLocalService.getValuesList(
			groupId, companyId, userId, objectDefinitionId, predicate, search,
			start, end, sorts);
	}

	@Override
	public int getValuesListCount(
			long groupId, long companyId, long userId, long objectDefinitionId,
			com.liferay.petra.sql.dsl.expression.Predicate predicate,
			String search)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryLocalService.getValuesListCount(
			groupId, companyId, userId, objectDefinitionId, predicate, search);
	}

	@Override
	public void insertIntoOrUpdateExtensionTable(
			long userId, long objectDefinitionId, long primaryKey,
			java.util.Map<String, java.io.Serializable> values)
		throws com.liferay.portal.kernel.exception.PortalException {

		_objectEntryLocalService.insertIntoOrUpdateExtensionTable(
			userId, objectDefinitionId, primaryKey, values);
	}

	@Override
	public com.liferay.object.model.ObjectEntry partialUpdateObjectEntry(
			long userId, long objectEntryId,
			java.util.Map<String, java.io.Serializable> values,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryLocalService.partialUpdateObjectEntry(
			userId, objectEntryId, values, serviceContext);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult
		<com.liferay.object.model.ObjectEntry> searchObjectEntries(
				long groupId, long objectDefinitionId, String keywords, int cur,
				int delta)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryLocalService.searchObjectEntries(
			groupId, objectDefinitionId, keywords, cur, delta);
	}

	@Override
	public void updateAsset(
			long userId, com.liferay.object.model.ObjectEntry objectEntry,
			long[] assetCategoryIds, String[] assetTagNames,
			long[] assetLinkEntryIds, Double priority)
		throws com.liferay.portal.kernel.exception.PortalException {

		_objectEntryLocalService.updateAsset(
			userId, objectEntry, assetCategoryIds, assetTagNames,
			assetLinkEntryIds, priority);
	}

	@Override
	public com.liferay.object.model.ObjectEntry updateObjectEntry(
			long userId, long objectEntryId,
			java.util.Map<String, java.io.Serializable> values,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryLocalService.updateObjectEntry(
			userId, objectEntryId, values, serviceContext);
	}

	/**
	 * Updates the object entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectEntry the object entry
	 * @return the object entry that was updated
	 */
	@Override
	public com.liferay.object.model.ObjectEntry updateObjectEntry(
		com.liferay.object.model.ObjectEntry objectEntry) {

		return _objectEntryLocalService.updateObjectEntry(objectEntry);
	}

	@Override
	public void updateRootObjectEntryIds(
			com.liferay.object.model.ObjectDefinition objectDefinition1,
			com.liferay.object.model.ObjectDefinition objectDefinition2,
			com.liferay.object.model.ObjectRelationship objectRelationship)
		throws com.liferay.portal.kernel.exception.PortalException {

		_objectEntryLocalService.updateRootObjectEntryIds(
			objectDefinition1, objectDefinition2, objectRelationship);
	}

	@Override
	public com.liferay.object.model.ObjectEntry updateStatus(
			long userId, long objectEntryId, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryLocalService.updateStatus(
			userId, objectEntryId, status, serviceContext);
	}

	@Override
	public com.liferay.object.model.ObjectEntry updateStatus(
			long userId, com.liferay.object.model.ObjectEntry objectEntry,
			int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryLocalService.updateStatus(
			userId, objectEntry, status, serviceContext);
	}

	@Override
	public void validate(
			long groupId, com.liferay.object.model.ObjectEntry objectEntry,
			java.util.List<String> objectValidationRuleExternalReferenceCodes,
			com.liferay.portal.kernel.service.ServiceContext serviceContext,
			long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_objectEntryLocalService.validate(
			groupId, objectEntry, objectValidationRuleExternalReferenceCodes,
			serviceContext, userId);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _objectEntryLocalService.getBasePersistence();
	}

	@Override
	public ObjectEntryLocalService getWrappedService() {
		return _objectEntryLocalService;
	}

	@Override
	public void setWrappedService(
		ObjectEntryLocalService objectEntryLocalService) {

		_objectEntryLocalService = objectEntryLocalService;
	}

	private ObjectEntryLocalService _objectEntryLocalService;

}