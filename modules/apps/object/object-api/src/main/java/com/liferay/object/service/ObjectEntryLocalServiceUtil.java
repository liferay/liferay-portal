/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service;

import com.liferay.object.model.ObjectEntry;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * Provides the local service utility for ObjectEntry. This utility wraps
 * <code>com.liferay.object.service.impl.ObjectEntryLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Marco Leo
 * @see ObjectEntryLocalService
 * @generated
 */
public class ObjectEntryLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.object.service.impl.ObjectEntryLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static ObjectEntry addLatestApprovedObjectEntry(
			String externalReferenceCode, long groupId, long userId,
			long headObjectEntryId,
			com.liferay.object.model.ObjectDefinition objectDefinition,
			long objectEntryFolderId, String defaultLanguageId, int version,
			Map<String, Serializable> values)
		throws PortalException {

		return getService().addLatestApprovedObjectEntry(
			externalReferenceCode, groupId, userId, headObjectEntryId,
			objectDefinition, objectEntryFolderId, defaultLanguageId, version,
			values);
	}

	public static ObjectEntry addObjectEntry(
			long groupId, long userId, long objectDefinitionId,
			long objectEntryFolderId, String defaultLanguageId,
			Map<String, Serializable> values,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addObjectEntry(
			groupId, userId, objectDefinitionId, objectEntryFolderId,
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
	public static ObjectEntry addObjectEntry(ObjectEntry objectEntry) {
		return getService().addObjectEntry(objectEntry);
	}

	public static ObjectEntry addObjectEntry(
			String externalReferenceCode, long groupId, long userId,
			com.liferay.object.model.ObjectDefinition objectDefinition,
			long objectEntryFolderId)
		throws PortalException {

		return getService().addObjectEntry(
			externalReferenceCode, groupId, userId, objectDefinition,
			objectEntryFolderId);
	}

	public static void addOrUpdateExtensionDynamicObjectDefinitionTableValues(
			long userId,
			com.liferay.object.model.ObjectDefinition objectDefinition,
			long primaryKey, Map<String, Serializable> values,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		getService().addOrUpdateExtensionDynamicObjectDefinitionTableValues(
			userId, objectDefinition, primaryKey, values, serviceContext);
	}

	public static ObjectEntry addOrUpdateObjectEntry(
			String externalReferenceCode, long groupId, long userId,
			long objectDefinitionId, long objectEntryFolderId,
			Map<String, Serializable> values,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addOrUpdateObjectEntry(
			externalReferenceCode, groupId, userId, objectDefinitionId,
			objectEntryFolderId, values, serviceContext);
	}

	public static void checkObjectEntries(long companyId)
		throws PortalException {

		getService().checkObjectEntries(companyId);
	}

	/**
	 * Creates a new object entry with the primary key. Does not add the object entry to the database.
	 *
	 * @param objectEntryId the primary key for the new object entry
	 * @return the new object entry
	 */
	public static ObjectEntry createObjectEntry(long objectEntryId) {
		return getService().createObjectEntry(objectEntryId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	public static void deleteExtensionDynamicObjectDefinitionTableValues(
			com.liferay.object.model.ObjectDefinition objectDefinition,
			long primaryKey)
		throws PortalException {

		getService().deleteExtensionDynamicObjectDefinitionTableValues(
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
	public static ObjectEntry deleteObjectEntry(long objectEntryId)
		throws PortalException {

		return getService().deleteObjectEntry(objectEntryId);
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
	public static ObjectEntry deleteObjectEntry(ObjectEntry objectEntry)
		throws PortalException {

		return getService().deleteObjectEntry(objectEntry);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	public static void deleteRelatedObjectEntries(
			long groupId, long objectDefinitionId, long primaryKey)
		throws PortalException {

		getService().deleteRelatedObjectEntries(
			groupId, objectDefinitionId, primaryKey);
	}

	public static <T> T dslQuery(DSLQuery dslQuery) {
		return getService().dslQuery(dslQuery);
	}

	public static int dslQueryCount(DSLQuery dslQuery) {
		return getService().dslQueryCount(dslQuery);
	}

	public static DynamicQuery dynamicQuery() {
		return getService().dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	public static <T> List<T> dynamicQuery(DynamicQuery dynamicQuery) {
		return getService().dynamicQuery(dynamicQuery);
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
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getService().dynamicQuery(dynamicQuery, start, end);
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
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator) {

		return getService().dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(DynamicQuery dynamicQuery) {
		return getService().dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(
		DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return getService().dynamicQueryCount(dynamicQuery, projection);
	}

	public static ObjectEntry expireObjectEntry(
			long userId, long objectEntryId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().expireObjectEntry(
			userId, objectEntryId, serviceContext);
	}

	public static ObjectEntry fetchManyToOneObjectEntry(
			long groupId, long objectRelationshipId, long primaryKey)
		throws PortalException {

		return getService().fetchManyToOneObjectEntry(
			groupId, objectRelationshipId, primaryKey);
	}

	public static ObjectEntry fetchObjectEntry(long objectEntryId) {
		return getService().fetchObjectEntry(objectEntryId);
	}

	public static ObjectEntry fetchObjectEntry(
		long groupId,
		com.liferay.object.model.ObjectDefinition objectDefinition,
		String urlTitle) {

		return getService().fetchObjectEntry(
			groupId, objectDefinition, urlTitle);
	}

	public static ObjectEntry fetchObjectEntry(
		String externalReferenceCode, long groupId, long objectDefinitionId) {

		return getService().fetchObjectEntry(
			externalReferenceCode, groupId, objectDefinitionId);
	}

	public static ObjectEntry fetchObjectEntryByHeadObjectEntryId(
		long headObjectEntryId) {

		return getService().fetchObjectEntryByHeadObjectEntryId(
			headObjectEntryId);
	}

	/**
	 * Returns the object entry matching the UUID and group.
	 *
	 * @param uuid the object entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	public static ObjectEntry fetchObjectEntryByUuidAndGroupId(
		String uuid, long groupId) {

		return getService().fetchObjectEntryByUuidAndGroupId(uuid, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static Map<Object, Long> getAggregationCounts(
			long groupId, long objectDefinitionId, String aggregationTerm,
			com.liferay.petra.sql.dsl.expression.Predicate predicate,
			boolean preferApproved, int start, int end)
		throws PortalException {

		return getService().getAggregationCounts(
			groupId, objectDefinitionId, aggregationTerm, predicate,
			preferApproved, start, end);
	}

	public static com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return getService().getExportActionableDynamicQuery(portletDataContext);
	}

	public static Map<String, Serializable>
			getExtensionDynamicObjectDefinitionTableValues(
				com.liferay.object.model.ObjectDefinition objectDefinition,
				long primaryKey)
		throws PortalException {

		return getService().getExtensionDynamicObjectDefinitionTableValues(
			objectDefinition, primaryKey);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	public static List<ObjectEntry> getManyToManyObjectEntries(
			long groupId, long objectRelationshipId, long primaryKey,
			boolean related, boolean reverse, String search, int start, int end)
		throws PortalException {

		return getService().getManyToManyObjectEntries(
			groupId, objectRelationshipId, primaryKey, related, reverse, search,
			start, end);
	}

	public static int getManyToManyObjectEntriesCount(
			long groupId, long objectRelationshipId, long primaryKey,
			boolean related, boolean reverse, String search)
		throws PortalException {

		return getService().getManyToManyObjectEntriesCount(
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
	public static List<ObjectEntry> getObjectEntries(int start, int end) {
		return getService().getObjectEntries(start, end);
	}

	public static List<ObjectEntry> getObjectEntries(
		long groupId, long objectDefinitionId, int start, int end) {

		return getService().getObjectEntries(
			groupId, objectDefinitionId, start, end);
	}

	public static List<ObjectEntry> getObjectEntries(
		long groupId, long objectDefinitionId, int status, int start, int end) {

		return getService().getObjectEntries(
			groupId, objectDefinitionId, status, start, end);
	}

	/**
	 * Returns all the object entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the object entries
	 * @param companyId the primary key of the company
	 * @return the matching object entries, or an empty list if no matches were found
	 */
	public static List<ObjectEntry> getObjectEntriesByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().getObjectEntriesByUuidAndCompanyId(uuid, companyId);
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
	public static List<ObjectEntry> getObjectEntriesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectEntry> orderByComparator) {

		return getService().getObjectEntriesByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of object entries.
	 *
	 * @return the number of object entries
	 */
	public static int getObjectEntriesCount() {
		return getService().getObjectEntriesCount();
	}

	public static int getObjectEntriesCount(long objectDefinitionId) {
		return getService().getObjectEntriesCount(objectDefinitionId);
	}

	public static long getObjectEntriesCount(
			long userId, java.util.Date createDate, long objectDefinitionId)
		throws PortalException {

		return getService().getObjectEntriesCount(
			userId, createDate, objectDefinitionId);
	}

	public static int getObjectEntriesCount(
		long groupId, long objectDefinitionId) {

		return getService().getObjectEntriesCount(groupId, objectDefinitionId);
	}

	public static long getObjectEntriesCount(
			long groupId,
			com.liferay.object.model.ObjectDefinition objectDefinition,
			com.liferay.petra.sql.dsl.expression.Predicate predicate)
		throws PortalException {

		return getService().getObjectEntriesCount(
			groupId, objectDefinition, predicate);
	}

	/**
	 * Returns the object entry with the primary key.
	 *
	 * @param objectEntryId the primary key of the object entry
	 * @return the object entry
	 * @throws PortalException if a object entry with the primary key could not be found
	 */
	public static ObjectEntry getObjectEntry(long objectEntryId)
		throws PortalException {

		return getService().getObjectEntry(objectEntryId);
	}

	public static ObjectEntry getObjectEntry(
			String externalReferenceCode, long groupId, long objectDefinitionId)
		throws PortalException {

		return getService().getObjectEntry(
			externalReferenceCode, groupId, objectDefinitionId);
	}

	/**
	 * Returns the object entry matching the UUID and group.
	 *
	 * @param uuid the object entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching object entry
	 * @throws PortalException if a matching object entry could not be found
	 */
	public static ObjectEntry getObjectEntryByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getObjectEntryByUuidAndGroupId(uuid, groupId);
	}

	public static List<ObjectEntry> getObjectEntryFolderObjectEntries(
		long groupId, long objectEntryFolderId, int start, int end) {

		return getService().getObjectEntryFolderObjectEntries(
			groupId, objectEntryFolderId, start, end);
	}

	public static int getObjectEntryFolderObjectEntriesCount(
		long groupId, long objectEntryFolderId) {

		return getService().getObjectEntryFolderObjectEntriesCount(
			groupId, objectEntryFolderId);
	}

	public static Map<Object, Long> getOneToManyAggregationCounts(
			long groupId, long objectDefinitionId, long objectEntryId,
			long objectRelationshipId, String aggregationTerm,
			com.liferay.petra.sql.dsl.expression.Predicate predicate,
			boolean related, String search, int start, int end)
		throws PortalException {

		return getService().getOneToManyAggregationCounts(
			groupId, objectDefinitionId, objectEntryId, objectRelationshipId,
			aggregationTerm, predicate, related, search, start, end);
	}

	public static List<ObjectEntry> getOneToManyObjectEntries(
			long groupId, long objectRelationshipId,
			com.liferay.petra.sql.dsl.expression.Predicate predicate,
			boolean preferApproved, long primaryKey, boolean related,
			String search, int start, int end,
			com.liferay.portal.kernel.search.Sort[] sorts)
		throws PortalException {

		return getService().getOneToManyObjectEntries(
			groupId, objectRelationshipId, predicate, preferApproved,
			primaryKey, related, search, start, end, sorts);
	}

	public static int getOneToManyObjectEntriesCount(
			long groupId, long objectRelationshipId,
			com.liferay.petra.sql.dsl.expression.Predicate predicate,
			long primaryKey, boolean related, String search)
		throws PortalException {

		return getService().getOneToManyObjectEntriesCount(
			groupId, objectRelationshipId, predicate, primaryKey, related,
			search);
	}

	public static ObjectEntry getOrAddEmptyObjectEntry(
			String externalReferenceCode, long groupId, long userId,
			long objectDefinitionId)
		throws PortalException {

		return getService().getOrAddEmptyObjectEntry(
			externalReferenceCode, groupId, userId, objectDefinitionId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return getService().getPersistedModel(primaryKeyObj);
	}

	public static List<Long> getPrimaryKeys(
			Long[] groupIds, long companyId, long userId,
			long objectDefinitionId,
			com.liferay.petra.sql.dsl.expression.Predicate predicate,
			boolean preferApproved, String search, int start, int end,
			com.liferay.portal.kernel.search.Sort[] sorts)
		throws PortalException {

		return getService().getPrimaryKeys(
			groupIds, companyId, userId, objectDefinitionId, predicate,
			preferApproved, search, start, end, sorts);
	}

	public static Map<String, Object> getSystemModelAttributes(
			com.liferay.object.model.ObjectDefinition objectDefinition,
			long primaryKey)
		throws PortalException {

		return getService().getSystemModelAttributes(
			objectDefinition, primaryKey);
	}

	public static Map<String, Serializable> getSystemValues(
			ObjectEntry objectEntry)
		throws PortalException {

		return getService().getSystemValues(objectEntry);
	}

	public static String getTitleValue(long objectDefinitionId, long primaryKey)
		throws PortalException {

		return getService().getTitleValue(objectDefinitionId, primaryKey);
	}

	public static Map<String, Serializable> getValues(long objectEntryId)
		throws PortalException {

		return getService().getValues(objectEntryId);
	}

	public static Map<String, Serializable> getValues(ObjectEntry objectEntry)
		throws PortalException {

		return getService().getValues(objectEntry);
	}

	public static List<Map<String, Serializable>> getValuesList(
			long groupId, long companyId, long userId, long objectDefinitionId,
			com.liferay.petra.sql.dsl.expression.Predicate predicate,
			String search, int start, int end,
			com.liferay.portal.kernel.search.Sort[] sorts)
		throws PortalException {

		return getService().getValuesList(
			groupId, companyId, userId, objectDefinitionId, predicate, search,
			start, end, sorts);
	}

	public static int getValuesListCount(
			Long[] groupIds, long companyId, long userId,
			long objectDefinitionId,
			com.liferay.petra.sql.dsl.expression.Predicate predicate,
			boolean preferApproved, String search)
		throws PortalException {

		return getService().getValuesListCount(
			groupIds, companyId, userId, objectDefinitionId, predicate,
			preferApproved, search);
	}

	public static void insertIntoOrUpdateExtensionTable(
			long userId, long objectDefinitionId, long primaryKey,
			Map<String, Serializable> values)
		throws PortalException {

		getService().insertIntoOrUpdateExtensionTable(
			userId, objectDefinitionId, primaryKey, values);
	}

	public static void moveObjectEntriesToTrash(
			long userId,
			com.liferay.object.model.ObjectEntryFolder objectEntryFolder,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		getService().moveObjectEntriesToTrash(
			userId, objectEntryFolder, serviceContext);
	}

	public static ObjectEntry moveObjectEntryToTrash(
			long userId, ObjectEntry objectEntry,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().moveObjectEntryToTrash(
			userId, objectEntry, serviceContext);
	}

	public static void moveRelatedObjectEntriesToTrash(
			long groupId, long objectDefinitionId, long primaryKey)
		throws PortalException {

		getService().moveRelatedObjectEntriesToTrash(
			groupId, objectDefinitionId, primaryKey);
	}

	public static ObjectEntry partialUpdateObjectEntry(
			long userId, long objectEntryId, long objectEntryFolderId,
			Map<String, Serializable> values,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().partialUpdateObjectEntry(
			userId, objectEntryId, objectEntryFolderId, values, serviceContext);
	}

	public static void restoreObjectEntriesFromTrash(
			long userId,
			com.liferay.object.model.ObjectEntryFolder objectEntryFolder,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		getService().restoreObjectEntriesFromTrash(
			userId, objectEntryFolder, serviceContext);
	}

	public static ObjectEntry restoreObjectEntryFromTrash(
			long userId, ObjectEntry objectEntry,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().restoreObjectEntryFromTrash(
			userId, objectEntry, serviceContext);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<ObjectEntry> searchObjectEntries(
				long groupId, long objectDefinitionId, String keywords, int cur,
				int delta)
			throws PortalException {

		return getService().searchObjectEntries(
			groupId, objectDefinitionId, keywords, cur, delta);
	}

	public static void subscribeObjectEntry(
			long userId, long groupId, long objectEntryId)
		throws PortalException {

		getService().subscribeObjectEntry(userId, groupId, objectEntryId);
	}

	public static void unsubscribeObjectEntry(long userId, long objectEntryId)
		throws PortalException {

		getService().unsubscribeObjectEntry(userId, objectEntryId);
	}

	public static void updateAsset(
			long userId, ObjectEntry objectEntry, long[] assetCategoryIds,
			String[] assetTagNames, long[] assetLinkEntryIds, Double priority)
		throws PortalException {

		getService().updateAsset(
			userId, objectEntry, assetCategoryIds, assetTagNames,
			assetLinkEntryIds, priority);
	}

	public static ObjectEntry updateObjectEntry(
			long userId, long objectEntryId, long objectEntryFolderId,
			Map<String, Serializable> values,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateObjectEntry(
			userId, objectEntryId, objectEntryFolderId, values, serviceContext);
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
	public static ObjectEntry updateObjectEntry(ObjectEntry objectEntry) {
		return getService().updateObjectEntry(objectEntry);
	}

	public static void updateRootObjectEntryIds(
			com.liferay.object.model.ObjectDefinition objectDefinition1,
			com.liferay.object.model.ObjectDefinition objectDefinition2,
			com.liferay.object.model.ObjectRelationship objectRelationship)
		throws PortalException {

		getService().updateRootObjectEntryIds(
			objectDefinition1, objectDefinition2, objectRelationship);
	}

	public static ObjectEntry updateStatus(
			long userId, long objectEntryId, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateStatus(
			userId, objectEntryId, status, serviceContext);
	}

	public static ObjectEntry updateStatus(
			long userId, ObjectEntry objectEntry, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateStatus(
			userId, objectEntry, status, serviceContext);
	}

	public static void validate(
			long groupId, ObjectEntry objectEntry,
			List<String> objectValidationRuleExternalReferenceCodes,
			com.liferay.portal.kernel.service.ServiceContext serviceContext,
			long userId)
		throws PortalException {

		getService().validate(
			groupId, objectEntry, objectValidationRuleExternalReferenceCodes,
			serviceContext, userId);
	}

	public static ObjectEntryLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<ObjectEntryLocalService> _serviceSnapshot =
		new Snapshot<>(
			ObjectEntryLocalServiceUtil.class, ObjectEntryLocalService.class);

}