/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for ObjectEntry. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Marco Leo
 * @see ObjectEntryLocalServiceUtil
 * @generated
 */
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface ObjectEntryLocalService
	extends BaseLocalService, PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.object.service.impl.ObjectEntryLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the object entry local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link ObjectEntryLocalServiceUtil} if injection and service tracking are not available.
	 */
	public ObjectEntry addLatestApprovedObjectEntry(
			String externalReferenceCode, long groupId, long userId,
			long headObjectEntryId, ObjectDefinition objectDefinition,
			long objectEntryFolderId, String defaultLanguageId, int version,
			Map<String, Serializable> values)
		throws PortalException;

	public ObjectEntry addObjectEntry(
			long groupId, long userId, long objectDefinitionId,
			long objectEntryFolderId, String defaultLanguageId,
			Map<String, Serializable> values, ServiceContext serviceContext)
		throws PortalException;

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
	@Indexable(type = IndexableType.REINDEX)
	public ObjectEntry addObjectEntry(ObjectEntry objectEntry);

	public ObjectEntry addObjectEntry(
			String externalReferenceCode, long groupId, long userId,
			ObjectDefinition objectDefinition, long objectEntryFolderId)
		throws PortalException;

	public void addOrUpdateExtensionDynamicObjectDefinitionTableValues(
			long userId, ObjectDefinition objectDefinition, long primaryKey,
			Map<String, Serializable> values, ServiceContext serviceContext)
		throws PortalException;

	public ObjectEntry addOrUpdateObjectEntry(
			String externalReferenceCode, long groupId, long userId,
			long objectDefinitionId, long objectEntryFolderId,
			Map<String, Serializable> values, ServiceContext serviceContext)
		throws PortalException;

	public void checkObjectEntries(long companyId) throws PortalException;

	/**
	 * Creates a new object entry with the primary key. Does not add the object entry to the database.
	 *
	 * @param objectEntryId the primary key for the new object entry
	 * @return the new object entry
	 */
	@Transactional(enabled = false)
	public ObjectEntry createObjectEntry(long objectEntryId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	public void deleteExtensionDynamicObjectDefinitionTableValues(
			ObjectDefinition objectDefinition, long primaryKey)
		throws PortalException;

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
	@Indexable(type = IndexableType.DELETE)
	public ObjectEntry deleteObjectEntry(long objectEntryId)
		throws PortalException;

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
	@Indexable(type = IndexableType.DELETE)
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public ObjectEntry deleteObjectEntry(ObjectEntry objectEntry)
		throws PortalException;

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException;

	public void deleteRelatedObjectEntries(
			long groupId, long objectDefinitionId, long primaryKey)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> T dslQuery(DSLQuery dslQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int dslQueryCount(DSLQuery dslQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DynamicQuery dynamicQuery();

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(DynamicQuery dynamicQuery);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(DynamicQuery dynamicQuery);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(
		DynamicQuery dynamicQuery, Projection projection);

	public ObjectEntry expireObjectEntry(
			long userId, long objectEntryId, ServiceContext serviceContext)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ObjectEntry fetchManyToOneObjectEntry(
			long groupId, long objectRelationshipId, long primaryKey)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ObjectEntry fetchObjectEntry(long objectEntryId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ObjectEntry fetchObjectEntry(
		long groupId, ObjectDefinition objectDefinition, String urlTitle);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ObjectEntry fetchObjectEntry(
		String externalReferenceCode, long groupId, long objectDefinitionId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ObjectEntry fetchObjectEntryByHeadObjectEntryId(
		long headObjectEntryId);

	/**
	 * Returns the object entry matching the UUID and group.
	 *
	 * @param uuid the object entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ObjectEntry fetchObjectEntryByUuidAndGroupId(
		String uuid, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Map<Object, Long> getAggregationCounts(
			long groupId, long objectDefinitionId, String aggregationTerm,
			Predicate predicate, boolean preferApproved, int start, int end)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		PortletDataContext portletDataContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Map<String, Serializable>
			getExtensionDynamicObjectDefinitionTableValues(
				ObjectDefinition objectDefinition, long primaryKey)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<ObjectEntry> getManyToManyObjectEntries(
			long groupId, long objectRelationshipId, long primaryKey,
			boolean related, boolean reverse, String search, int start, int end)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getManyToManyObjectEntriesCount(
			long groupId, long objectRelationshipId, long primaryKey,
			boolean related, boolean reverse, String search)
		throws PortalException;

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<ObjectEntry> getObjectEntries(int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<ObjectEntry> getObjectEntries(
		long groupId, long objectDefinitionId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<ObjectEntry> getObjectEntries(
		long groupId, long objectDefinitionId, int status, int start, int end);

	/**
	 * Returns all the object entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the object entries
	 * @param companyId the primary key of the company
	 * @return the matching object entries, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<ObjectEntry> getObjectEntriesByUuidAndCompanyId(
		String uuid, long companyId);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<ObjectEntry> getObjectEntriesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<ObjectEntry> orderByComparator);

	/**
	 * Returns the number of object entries.
	 *
	 * @return the number of object entries
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getObjectEntriesCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getObjectEntriesCount(long objectDefinitionId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long getObjectEntriesCount(
			long userId, Date createDate, long objectDefinitionId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getObjectEntriesCount(long groupId, long objectDefinitionId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long getObjectEntriesCount(
			long groupId, ObjectDefinition objectDefinition,
			Predicate predicate)
		throws PortalException;

	/**
	 * Returns the object entry with the primary key.
	 *
	 * @param objectEntryId the primary key of the object entry
	 * @return the object entry
	 * @throws PortalException if a object entry with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ObjectEntry getObjectEntry(long objectEntryId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ObjectEntry getObjectEntry(
			String externalReferenceCode, long groupId, long objectDefinitionId)
		throws PortalException;

	/**
	 * Returns the object entry matching the UUID and group.
	 *
	 * @param uuid the object entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching object entry
	 * @throws PortalException if a matching object entry could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ObjectEntry getObjectEntryByUuidAndGroupId(String uuid, long groupId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<ObjectEntry> getObjectEntryFolderObjectEntries(
		long groupId, long objectEntryFolderId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getObjectEntryFolderObjectEntriesCount(
		long groupId, long objectEntryFolderId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Map<Object, Long> getOneToManyAggregationCounts(
			long groupId, long objectDefinitionId, long objectEntryId,
			long objectRelationshipId, String aggregationTerm,
			Predicate predicate, boolean related, String search, int start,
			int end)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<ObjectEntry> getOneToManyObjectEntries(
			long groupId, long objectRelationshipId, Predicate predicate,
			boolean preferApproved, long primaryKey, boolean related,
			String search, int start, int end, Sort[] sorts)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getOneToManyObjectEntriesCount(
			long groupId, long objectRelationshipId, Predicate predicate,
			long primaryKey, boolean related, String search)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	@Transactional(propagation = Propagation.REQUIRED)
	public ObjectEntry getOrAddEmptyObjectEntry(
			String externalReferenceCode, long groupId, long userId,
			long objectDefinitionId)
		throws PortalException;

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	/**
	 * @throws PortalException
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Long> getPrimaryKeys(
			Long[] groupIds, long companyId, long userId,
			long objectDefinitionId, Predicate predicate,
			boolean preferApproved, String search, int start, int end,
			Sort[] sorts)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Map<String, Object> getSystemModelAttributes(
			ObjectDefinition objectDefinition, long primaryKey)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Map<String, Serializable> getSystemValues(ObjectEntry objectEntry)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public String getTitleValue(long objectDefinitionId, long primaryKey)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Map<String, Serializable> getValues(long objectEntryId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Map<String, Serializable> getValues(ObjectEntry objectEntry)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Map<String, Serializable>> getValuesList(
			long groupId, long companyId, long userId, long objectDefinitionId,
			Predicate predicate, String search, int start, int end,
			Sort[] sorts)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getValuesListCount(
			Long[] groupIds, long companyId, long userId,
			long objectDefinitionId, Predicate predicate,
			boolean preferApproved, String search)
		throws PortalException;

	public void insertIntoOrUpdateExtensionTable(
			long userId, long objectDefinitionId, long primaryKey,
			Map<String, Serializable> values)
		throws PortalException;

	public void moveObjectEntriesToTrash(
			long userId, ObjectEntryFolder objectEntryFolder,
			ServiceContext serviceContext)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public ObjectEntry moveObjectEntryToTrash(
			long userId, ObjectEntry objectEntry, ServiceContext serviceContext)
		throws PortalException;

	public void moveRelatedObjectEntriesToTrash(
			long groupId, long objectDefinitionId, long primaryKey)
		throws PortalException;

	public ObjectEntry partialUpdateObjectEntry(
			long userId, long objectEntryId, long objectEntryFolderId,
			Map<String, Serializable> values, ServiceContext serviceContext)
		throws PortalException;

	public void restoreObjectEntriesFromTrash(
			long userId, ObjectEntryFolder objectEntryFolder,
			ServiceContext serviceContext)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public ObjectEntry restoreObjectEntryFromTrash(
			long userId, ObjectEntry objectEntry, ServiceContext serviceContext)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BaseModelSearchResult<ObjectEntry> searchObjectEntries(
			long groupId, long objectDefinitionId, String keywords, int cur,
			int delta)
		throws PortalException;

	public void subscribeObjectEntry(
			long userId, long groupId, long objectEntryId)
		throws PortalException;

	public void unsubscribeObjectEntry(long userId, long objectEntryId)
		throws PortalException;

	public void updateAsset(
			long userId, ObjectEntry objectEntry, long[] assetCategoryIds,
			String[] assetTagNames, long[] assetLinkEntryIds, Double priority)
		throws PortalException;

	public ObjectEntry updateObjectEntry(
			long userId, long objectEntryId, long objectEntryFolderId,
			Map<String, Serializable> values, ServiceContext serviceContext)
		throws PortalException;

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
	@Indexable(type = IndexableType.REINDEX)
	public ObjectEntry updateObjectEntry(ObjectEntry objectEntry);

	public void updateRootObjectEntryIds(
			ObjectDefinition objectDefinition1,
			ObjectDefinition objectDefinition2,
			ObjectRelationship objectRelationship)
		throws PortalException;

	public ObjectEntry updateStatus(
			long userId, long objectEntryId, int status,
			ServiceContext serviceContext)
		throws PortalException;

	public ObjectEntry updateStatus(
			long userId, ObjectEntry objectEntry, int status,
			ServiceContext serviceContext)
		throws PortalException;

	public void validate(
			long groupId, ObjectEntry objectEntry,
			List<String> objectValidationRuleExternalReferenceCodes,
			ServiceContext serviceContext, long userId)
		throws PortalException;

}