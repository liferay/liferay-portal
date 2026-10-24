/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.lists.service;

import com.liferay.dynamic.data.lists.model.DDLRecord;
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
 * Provides the local service utility for DDLRecord. This utility wraps
 * <code>com.liferay.dynamic.data.lists.service.impl.DDLRecordLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see DDLRecordLocalService
 * @generated
 */
public class DDLRecordLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.dynamic.data.lists.service.impl.DDLRecordLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
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
	public static DDLRecord addDDLRecord(DDLRecord ddlRecord) {
		return getService().addDDLRecord(ddlRecord);
	}

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
	public static DDLRecord addRecord(
			long userId, long groupId, long recordSetId, int displayIndex,
			com.liferay.dynamic.data.mapping.storage.DDMFormValues
				ddmFormValues,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addRecord(
			userId, groupId, recordSetId, displayIndex, ddmFormValues,
			serviceContext);
	}

	public static DDLRecord addRecord(
			long userId, long groupId, long recordSetId, int displayIndex,
			Map<String, Serializable> fieldsMap,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addRecord(
			userId, groupId, recordSetId, displayIndex, fieldsMap,
			serviceContext);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	public static DDLRecord addRecord(
			long userId, long groupId, long ddmStorageId, long ddlRecordSetId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addRecord(
			userId, groupId, ddmStorageId, ddlRecordSetId, serviceContext);
	}

	public static DDLRecord addRecord(
			long userId, long groupId, long ddmStorageId, long ddlRecordSetId,
			String className, long classPK,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addRecord(
			userId, groupId, ddmStorageId, ddlRecordSetId, className, classPK,
			serviceContext);
	}

	/**
	 * Creates a new ddl record with the primary key. Does not add the ddl record to the database.
	 *
	 * @param recordId the primary key for the new ddl record
	 * @return the new ddl record
	 */
	public static DDLRecord createDDLRecord(long recordId) {
		return getService().createDDLRecord(recordId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

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
	public static DDLRecord deleteDDLRecord(DDLRecord ddlRecord) {
		return getService().deleteDDLRecord(ddlRecord);
	}

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
	public static DDLRecord deleteDDLRecord(long recordId)
		throws PortalException {

		return getService().deleteDDLRecord(recordId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	/**
	 * Deletes the record and its resources.
	 *
	 * @param record the record to be deleted
	 * @return the record
	 * @throws PortalException if a portal exception occurred
	 */
	public static DDLRecord deleteRecord(DDLRecord record)
		throws PortalException {

		return getService().deleteRecord(record);
	}

	/**
	 * Deletes the record and its resources.
	 *
	 * @param recordId the primary key of the record to be deleted
	 * @throws PortalException if a portal exception occurred
	 */
	public static void deleteRecord(long recordId) throws PortalException {
		getService().deleteRecord(recordId);
	}

	/**
	 * Deletes all the record set's records.
	 *
	 * @param recordSetId the primary key of the record set from which to
	 delete records
	 * @throws PortalException if a portal exception occurred
	 */
	public static void deleteRecords(long recordSetId) throws PortalException {
		getService().deleteRecords(recordSetId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.lists.model.impl.DDLRecordModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.lists.model.impl.DDLRecordModelImpl</code>.
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

	public static DDLRecord fetchDDLRecord(long recordId) {
		return getService().fetchDDLRecord(recordId);
	}

	/**
	 * Returns the ddl record matching the UUID and group.
	 *
	 * @param uuid the ddl record's UUID
	 * @param groupId the primary key of the group
	 * @return the matching ddl record, or <code>null</code> if a matching ddl record could not be found
	 */
	public static DDLRecord fetchDDLRecordByUuidAndGroupId(
		String uuid, long groupId) {

		return getService().fetchDDLRecordByUuidAndGroupId(uuid, groupId);
	}

	/**
	 * Returns the record with the ID.
	 *
	 * @param recordId the primary key of the record
	 * @return the record with the ID, or <code>null</code> if a matching record
	 could not be found
	 */
	public static DDLRecord fetchRecord(long recordId) {
		return getService().fetchRecord(recordId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the ddl record with the primary key.
	 *
	 * @param recordId the primary key of the ddl record
	 * @return the ddl record
	 * @throws PortalException if a ddl record with the primary key could not be found
	 */
	public static DDLRecord getDDLRecord(long recordId) throws PortalException {
		return getService().getDDLRecord(recordId);
	}

	/**
	 * Returns the ddl record matching the UUID and group.
	 *
	 * @param uuid the ddl record's UUID
	 * @param groupId the primary key of the group
	 * @return the matching ddl record
	 * @throws PortalException if a matching ddl record could not be found
	 */
	public static DDLRecord getDDLRecordByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getDDLRecordByUuidAndGroupId(uuid, groupId);
	}

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
	public static List<DDLRecord> getDDLRecords(int start, int end) {
		return getService().getDDLRecords(start, end);
	}

	/**
	 * Returns all the ddl records matching the UUID and company.
	 *
	 * @param uuid the UUID of the ddl records
	 * @param companyId the primary key of the company
	 * @return the matching ddl records, or an empty list if no matches were found
	 */
	public static List<DDLRecord> getDDLRecordsByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().getDDLRecordsByUuidAndCompanyId(uuid, companyId);
	}

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
	public static List<DDLRecord> getDDLRecordsByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<DDLRecord> orderByComparator) {

		return getService().getDDLRecordsByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of ddl records.
	 *
	 * @return the number of ddl records
	 */
	public static int getDDLRecordsCount() {
		return getService().getDDLRecordsCount();
	}

	/**
	 * Returns the DDM form values object associated with the record storage ID
	 * See <code>DDLRecord#getDDMFormValues</code> in the
	 * <code>com.liferay.dynamic.data.lists.api</code> module.
	 *
	 * @param ddmStorageId the storage ID associated with the record
	 * @return the DDM form values
	 */
	public static com.liferay.dynamic.data.mapping.storage.DDMFormValues
			getDDMFormValues(long ddmStorageId)
		throws com.liferay.dynamic.data.mapping.exception.StorageException {

		return getService().getDDMFormValues(ddmStorageId);
	}

	public static com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return getService().getExportActionableDynamicQuery(portletDataContext);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
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

	/**
	 * Returns the record with the ID.
	 *
	 * @param recordId the primary key of the record
	 * @return the record with the ID
	 * @throws PortalException if a portal exception occurred
	 */
	public static DDLRecord getRecord(long recordId) throws PortalException {
		return getService().getRecord(recordId);
	}

	/**
	 * Returns all the records matching the record set ID
	 *
	 * @param recordSetId the record's record set ID
	 * @return the matching records
	 */
	public static List<DDLRecord> getRecords(long recordSetId) {
		return getService().getRecords(recordSetId);
	}

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
	public static List<DDLRecord> getRecords(
		long recordSetId, int status, int start, int end,
		OrderByComparator<DDLRecord> orderByComparator) {

		return getService().getRecords(
			recordSetId, status, start, end, orderByComparator);
	}

	public static List<DDLRecord> getRecords(
		long recordSetId, int start, int end,
		OrderByComparator<DDLRecord> orderByComparator) {

		return getService().getRecords(
			recordSetId, start, end, orderByComparator);
	}

	/**
	 * Returns all the records matching the record set ID and user ID.
	 *
	 * @param recordSetId the record's record set ID
	 * @param userId the user ID the records belong to
	 * @return the list of matching records ordered by the comparator
	 */
	public static List<DDLRecord> getRecords(long recordSetId, long userId) {
		return getService().getRecords(recordSetId, userId);
	}

	public static List<DDLRecord> getRecords(
		long recordSetId, long userId, int start, int end,
		OrderByComparator<DDLRecord> orderByComparator) {

		return getService().getRecords(
			recordSetId, userId, start, end, orderByComparator);
	}

	public static int getRecordsCount(long recordSetId) {
		return getService().getRecordsCount(recordSetId);
	}

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
	public static int getRecordsCount(long recordSetId, int status) {
		return getService().getRecordsCount(recordSetId, status);
	}

	public static int getRecordsCount(long recordSetId, long userId) {
		return getService().getRecordsCount(recordSetId, userId);
	}

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
	public static void revertRecord(
			long userId, long recordId, String version,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		getService().revertRecord(userId, recordId, version, serviceContext);
	}

	/**
	 * Returns hits to all the records indexed by the search engine matching the
	 * search context.
	 *
	 * @param searchContext the search context to be applied for searching
	 records. For more information, see <code>SearchContext</code> in
	 the <code>portal-kernel</code> module.
	 * @return the hits of the records that matched the search criteria.
	 */
	public static com.liferay.portal.kernel.search.Hits search(
		com.liferay.portal.kernel.search.SearchContext searchContext) {

		return getService().search(searchContext);
	}

	/**
	 * Searches for records documents indexed by the search engine.
	 *
	 * @param searchContext the search context to be applied for searching
	 documents. For more information, see <code>SearchContext</code>
	 in the <code>portal-kernel</code> module.
	 * @return BaseModelSearchResult containing the list of records that matched
	 the search criteria
	 */
	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<DDLRecord> searchDDLRecords(
			com.liferay.portal.kernel.search.SearchContext searchContext) {

		return getService().searchDDLRecords(searchContext);
	}

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
	public static void updateAsset(
			long userId, DDLRecord record,
			com.liferay.dynamic.data.lists.model.DDLRecordVersion recordVersion,
			long[] assetCategoryIds, String[] assetTagNames,
			java.util.Locale locale, Double priority)
		throws PortalException {

		getService().updateAsset(
			userId, record, recordVersion, assetCategoryIds, assetTagNames,
			locale, priority);
	}

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
	public static DDLRecord updateDDLRecord(DDLRecord ddlRecord) {
		return getService().updateDDLRecord(ddlRecord);
	}

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
	public static DDLRecord updateRecord(
			long userId, long recordId, boolean majorVersion, int displayIndex,
			com.liferay.dynamic.data.mapping.storage.DDMFormValues
				ddmFormValues,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateRecord(
			userId, recordId, majorVersion, displayIndex, ddmFormValues,
			serviceContext);
	}

	public static DDLRecord updateRecord(
			long userId, long recordId, int displayIndex,
			Map<String, Serializable> fieldsMap, boolean mergeFields,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateRecord(
			userId, recordId, displayIndex, fieldsMap, mergeFields,
			serviceContext);
	}

	public static DDLRecord updateRecord(
			long userId, long recordId, long ddmStorageId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateRecord(
			userId, recordId, ddmStorageId, serviceContext);
	}

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
	public static DDLRecord updateStatus(
			long userId, long recordVersionId, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateStatus(
			userId, recordVersionId, status, serviceContext);
	}

	public static DDLRecordLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<DDLRecordLocalService> _serviceSnapshot =
		new Snapshot<>(
			DDLRecordLocalServiceUtil.class, DDLRecordLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:1779975162