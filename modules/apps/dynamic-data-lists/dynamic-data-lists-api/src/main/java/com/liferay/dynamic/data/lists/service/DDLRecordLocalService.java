/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.lists.service;

import com.liferay.dynamic.data.lists.model.DDLRecord;
import com.liferay.dynamic.data.lists.model.DDLRecordVersion;
import com.liferay.dynamic.data.mapping.exception.StorageException;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.change.tracking.CTAware;
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
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for DDLRecord. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see DDLRecordLocalServiceUtil
 * @generated
 */
@CTAware
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface DDLRecordLocalService
	extends BaseLocalService, CTService<DDLRecord>, PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.dynamic.data.lists.service.impl.DDLRecordLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the ddl record local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link DDLRecordLocalServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Adds the ddl record to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDLRecordLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddlRecord the ddl record
	 * @return the ddl record that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public DDLRecord addDDLRecord(DDLRecord ddlRecord);

	/**
	 * Adds a record referencing the record set.
	 *
	 * @param userId the primary key of the record's creator/owner
	 * @param groupId the primary key of the record's group
	 * @param recordSetId the primary key of the record set
	 * @param displayIndex the index position in which the record is displayed
	 in the spreadsheet view
	 * @param ddmFormValues the record values. See <code>DDMFormValues</code>
	 in the <code>dynamic.data.mapping.api</code> module.
	 * @param serviceContext the service context to be applied. This can set
	 the UUID, guest permissions, and group permissions for the
	 record.
	 * @return the record
	 * @throws PortalException if a portal exception occurred
	 */
	@Indexable(type = IndexableType.REINDEX)
	public DDLRecord addRecord(
			long userId, long groupId, long recordSetId, int displayIndex,
			DDMFormValues ddmFormValues, ServiceContext serviceContext)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public DDLRecord addRecord(
			long userId, long groupId, long recordSetId, int displayIndex,
			Map<String, Serializable> fieldsMap, ServiceContext serviceContext)
		throws PortalException;

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	@Indexable(type = IndexableType.REINDEX)
	public DDLRecord addRecord(
			long userId, long groupId, long ddmStorageId, long ddlRecordSetId,
			ServiceContext serviceContext)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public DDLRecord addRecord(
			long userId, long groupId, long ddmStorageId, long ddlRecordSetId,
			String className, long classPK, ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Creates a new ddl record with the primary key. Does not add the ddl record to the database.
	 *
	 * @param recordId the primary key for the new ddl record
	 * @return the new ddl record
	 */
	@Transactional(enabled = false)
	public DDLRecord createDDLRecord(long recordId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Deletes the ddl record from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDLRecordLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddlRecord the ddl record
	 * @return the ddl record that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	public DDLRecord deleteDDLRecord(DDLRecord ddlRecord);

	/**
	 * Deletes the ddl record with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDLRecordLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param recordId the primary key of the ddl record
	 * @return the ddl record that was removed
	 * @throws PortalException if a ddl record with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public DDLRecord deleteDDLRecord(long recordId) throws PortalException;

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException;

	/**
	 * Deletes the record and its resources.
	 *
	 * @param record the record to be deleted
	 * @return the record
	 * @throws PortalException if a portal exception occurred
	 */
	@Indexable(type = IndexableType.DELETE)
	@SystemEvent(
		action = SystemEventConstants.ACTION_SKIP,
		type = SystemEventConstants.TYPE_DELETE
	)
	public DDLRecord deleteRecord(DDLRecord record) throws PortalException;

	/**
	 * Deletes the record and its resources.
	 *
	 * @param recordId the primary key of the record to be deleted
	 * @throws PortalException if a portal exception occurred
	 */
	public void deleteRecord(long recordId) throws PortalException;

	/**
	 * Deletes all the record set's records.
	 *
	 * @param recordSetId the primary key of the record set from which to
	 delete records
	 * @throws PortalException if a portal exception occurred
	 */
	public void deleteRecords(long recordSetId) throws PortalException;

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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.lists.model.impl.DDLRecordModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.lists.model.impl.DDLRecordModelImpl</code>.
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

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DDLRecord fetchDDLRecord(long recordId);

	/**
	 * Returns the ddl record matching the UUID and group.
	 *
	 * @param uuid the ddl record's UUID
	 * @param groupId the primary key of the group
	 * @return the matching ddl record, or <code>null</code> if a matching ddl record could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DDLRecord fetchDDLRecordByUuidAndGroupId(String uuid, long groupId);

	/**
	 * Returns the record with the ID.
	 *
	 * @param recordId the primary key of the record
	 * @return the record with the ID, or <code>null</code> if a matching record
	 could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DDLRecord fetchRecord(long recordId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	/**
	 * Returns the ddl record with the primary key.
	 *
	 * @param recordId the primary key of the ddl record
	 * @return the ddl record
	 * @throws PortalException if a ddl record with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DDLRecord getDDLRecord(long recordId) throws PortalException;

	/**
	 * Returns the ddl record matching the UUID and group.
	 *
	 * @param uuid the ddl record's UUID
	 * @param groupId the primary key of the group
	 * @return the matching ddl record
	 * @throws PortalException if a matching ddl record could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DDLRecord getDDLRecordByUuidAndGroupId(String uuid, long groupId)
		throws PortalException;

	/**
	 * Returns a range of all the ddl records.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.lists.model.impl.DDLRecordModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ddl records
	 * @param end the upper bound of the range of ddl records (not inclusive)
	 * @return the range of ddl records
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DDLRecord> getDDLRecords(int start, int end);

	/**
	 * Returns all the ddl records matching the UUID and company.
	 *
	 * @param uuid the UUID of the ddl records
	 * @param companyId the primary key of the company
	 * @return the matching ddl records, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DDLRecord> getDDLRecordsByUuidAndCompanyId(
		String uuid, long companyId);

	/**
	 * Returns a range of ddl records matching the UUID and company.
	 *
	 * @param uuid the UUID of the ddl records
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of ddl records
	 * @param end the upper bound of the range of ddl records (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching ddl records, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DDLRecord> getDDLRecordsByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<DDLRecord> orderByComparator);

	/**
	 * Returns the number of ddl records.
	 *
	 * @return the number of ddl records
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getDDLRecordsCount();

	/**
	 * Returns the DDM form values object associated with the record storage ID
	 * See <code>DDLRecord#getDDMFormValues</code> in the
	 * <code>com.liferay.dynamic.data.lists.api</code> module.
	 *
	 * @param ddmStorageId the storage ID associated with the record
	 * @return the DDM form values
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DDMFormValues getDDMFormValues(long ddmStorageId)
		throws StorageException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		PortletDataContext portletDataContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

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

	/**
	 * Returns the record with the ID.
	 *
	 * @param recordId the primary key of the record
	 * @return the record with the ID
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DDLRecord getRecord(long recordId) throws PortalException;

	/**
	 * Returns all the records matching the record set ID
	 *
	 * @param recordSetId the record's record set ID
	 * @return the matching records
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DDLRecord> getRecords(long recordSetId);

	/**
	 * Returns an ordered range of all the records matching the record set ID
	 * and workflow status.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to <code>QueryUtil.ALL_POS</code> will return the
	 * full result set.
	 * </p>
	 *
	 * @param recordSetId the record's record set ID
	 * @param status the record's workflow status. For more information search
	 the portal kernel's WorkflowConstants class for constants
	 starting with the "STATUS_" prefix.
	 * @param start the lower bound of the range of records to return
	 * @param end the upper bound of the range of records to return (not
	 inclusive)
	 * @param orderByComparator the comparator to order the records
	 * @return the range of matching records ordered by the comparator
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DDLRecord> getRecords(
		long recordSetId, int status, int start, int end,
		OrderByComparator<DDLRecord> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DDLRecord> getRecords(
		long recordSetId, int start, int end,
		OrderByComparator<DDLRecord> orderByComparator);

	/**
	 * Returns all the records matching the record set ID and user ID.
	 *
	 * @param recordSetId the record's record set ID
	 * @param userId the user ID the records belong to
	 * @return the list of matching records ordered by the comparator
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DDLRecord> getRecords(long recordSetId, long userId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DDLRecord> getRecords(
		long recordSetId, long userId, int start, int end,
		OrderByComparator<DDLRecord> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getRecordsCount(long recordSetId);

	/**
	 * Returns the number of records matching the record set ID and workflow
	 * status.
	 *
	 * @param recordSetId the record's record set ID
	 * @param status the record's workflow status. For more information search
	 the portal kernel's WorkflowConstants class for constants
	 starting with the "STATUS_" prefix.
	 * @return the number of matching records
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getRecordsCount(long recordSetId, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getRecordsCount(long recordSetId, long userId);

	/**
	 * Reverts the record to the given version.
	 *
	 * @param userId the primary key of the user who is reverting the record
	 * @param recordId the primary key of the record
	 * @param version the version to revert to
	 * @param serviceContext the service context to be applied. This can set
	 the record modified date.
	 * @throws PortalException if a portal exception occurred
	 */
	public void revertRecord(
			long userId, long recordId, String version,
			ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Returns hits to all the records indexed by the search engine matching the
	 * search context.
	 *
	 * @param searchContext the search context to be applied for searching
	 records. For more information, see <code>SearchContext</code> in
	 the <code>portal-kernel</code> module.
	 * @return the hits of the records that matched the search criteria.
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Hits search(SearchContext searchContext);

	/**
	 * Searches for records documents indexed by the search engine.
	 *
	 * @param searchContext the search context to be applied for searching
	 documents. For more information, see <code>SearchContext</code>
	 in the <code>portal-kernel</code> module.
	 * @return BaseModelSearchResult containing the list of records that matched
	 the search criteria
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BaseModelSearchResult<DDLRecord> searchDDLRecords(
		SearchContext searchContext);

	/**
	 * Updates the record's asset with new asset categories, tag names, and link
	 * entries, removing and adding them as necessary.
	 *
	 * @param userId the primary key of the user updating the record's asset
	 * @param record the record
	 * @param recordVersion the record version
	 * @param assetCategoryIds the primary keys of the new asset categories
	 * @param assetTagNames the new asset tag names
	 * @param locale the locale to apply to the asset
	 * @param priority the new priority
	 * @throws PortalException if a portal exception occurred
	 */
	public void updateAsset(
			long userId, DDLRecord record, DDLRecordVersion recordVersion,
			long[] assetCategoryIds, String[] assetTagNames, Locale locale,
			Double priority)
		throws PortalException;

	/**
	 * Updates the ddl record in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDLRecordLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddlRecord the ddl record
	 * @return the ddl record that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public DDLRecord updateDDLRecord(DDLRecord ddlRecord);

	/**
	 * Updates the record, replacing its display index and values.
	 *
	 * @param userId the primary key of the user updating the record
	 * @param recordId the primary key of the record
	 * @param majorVersion whether this update is a major change. A major
	 change increments the record's major version number.
	 * @param displayIndex the index position in which the record is displayed
	 in the spreadsheet view
	 * @param ddmFormValues the record values. See <code>DDMFormValues</code>
	 in the <code>dynamic.data.mapping.api</code> module.
	 * @param serviceContext the service context to be applied. This can set
	 the record modified date.
	 * @return the record
	 * @throws PortalException if a portal exception occurred
	 */
	@Indexable(type = IndexableType.REINDEX)
	public DDLRecord updateRecord(
			long userId, long recordId, boolean majorVersion, int displayIndex,
			DDMFormValues ddmFormValues, ServiceContext serviceContext)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public DDLRecord updateRecord(
			long userId, long recordId, int displayIndex,
			Map<String, Serializable> fieldsMap, boolean mergeFields,
			ServiceContext serviceContext)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public DDLRecord updateRecord(
			long userId, long recordId, long ddmStorageId,
			ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Updates the workflow status of the record version.
	 *
	 * @param userId the primary key of the user updating the record's workflow
	 status
	 * @param recordVersionId the primary key of the record version
	 * @param status
	 * @param serviceContext the service context to be applied. This can set
	 the modification date and status date.
	 * @return the record
	 * @throws PortalException if a portal exception occurred
	 */
	@Indexable(type = IndexableType.REINDEX)
	public DDLRecord updateStatus(
			long userId, long recordVersionId, int status,
			ServiceContext serviceContext)
		throws PortalException;

	@Override
	@Transactional(enabled = false)
	public CTPersistence<DDLRecord> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<DDLRecord> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<DDLRecord>, R, E> updateUnsafeFunction)
		throws E;

}
// LIFERAY-SERVICE-BUILDER-HASH:1155561891