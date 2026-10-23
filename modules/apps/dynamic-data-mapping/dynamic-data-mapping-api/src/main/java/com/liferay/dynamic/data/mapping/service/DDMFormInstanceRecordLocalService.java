/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service;

import com.liferay.dynamic.data.mapping.exception.StorageException;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
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
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Sort;
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

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for DDMFormInstanceRecord. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see DDMFormInstanceRecordLocalServiceUtil
 * @generated
 */
@CTAware
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface DDMFormInstanceRecordLocalService
	extends BaseLocalService, CTService<DDMFormInstanceRecord>,
			PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.dynamic.data.mapping.service.impl.DDMFormInstanceRecordLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the ddm form instance record local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link DDMFormInstanceRecordLocalServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Adds the ddm form instance record to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMFormInstanceRecordLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddmFormInstanceRecord the ddm form instance record
	 * @return the ddm form instance record that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public DDMFormInstanceRecord addDDMFormInstanceRecord(
		DDMFormInstanceRecord ddmFormInstanceRecord);

	@Indexable(type = IndexableType.REINDEX)
	public DDMFormInstanceRecord addFormInstanceRecord(
			long userId, long groupId, long ddmFormInstanceId,
			DDMFormValues ddmFormValues, ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Creates a new ddm form instance record with the primary key. Does not add the ddm form instance record to the database.
	 *
	 * @param formInstanceRecordId the primary key for the new ddm form instance record
	 * @return the new ddm form instance record
	 */
	@Transactional(enabled = false)
	public DDMFormInstanceRecord createDDMFormInstanceRecord(
		long formInstanceRecordId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Deletes the ddm form instance record from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMFormInstanceRecordLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddmFormInstanceRecord the ddm form instance record
	 * @return the ddm form instance record that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	public DDMFormInstanceRecord deleteDDMFormInstanceRecord(
		DDMFormInstanceRecord ddmFormInstanceRecord);

	/**
	 * Deletes the ddm form instance record with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMFormInstanceRecordLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param formInstanceRecordId the primary key of the ddm form instance record
	 * @return the ddm form instance record that was removed
	 * @throws PortalException if a ddm form instance record with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public DDMFormInstanceRecord deleteDDMFormInstanceRecord(
			long formInstanceRecordId)
		throws PortalException;

	@Indexable(type = IndexableType.DELETE)
	@SystemEvent(
		action = SystemEventConstants.ACTION_SKIP,
		type = SystemEventConstants.TYPE_DELETE
	)
	public DDMFormInstanceRecord deleteFormInstanceRecord(
			DDMFormInstanceRecord ddmFormInstanceRecord)
		throws PortalException;

	@Indexable(type = IndexableType.DELETE)
	public DDMFormInstanceRecord deleteFormInstanceRecord(
			long ddmFormInstanceRecordId)
		throws PortalException;

	public void deleteFormInstanceRecords(long ddmFormInstanceId)
		throws PortalException;

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordModelImpl</code>.
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
	public DDMFormInstanceRecord fetchDDMFormInstanceRecord(
		long formInstanceRecordId);

	/**
	 * Returns the ddm form instance record matching the UUID and group.
	 *
	 * @param uuid the ddm form instance record's UUID
	 * @param groupId the primary key of the group
	 * @return the matching ddm form instance record, or <code>null</code> if a matching ddm form instance record could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DDMFormInstanceRecord fetchDDMFormInstanceRecordByUuidAndGroupId(
		String uuid, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DDMFormInstanceRecord fetchFormInstanceRecord(
		long ddmFormInstanceRecordId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	/**
	 * Returns the ddm form instance record with the primary key.
	 *
	 * @param formInstanceRecordId the primary key of the ddm form instance record
	 * @return the ddm form instance record
	 * @throws PortalException if a ddm form instance record with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DDMFormInstanceRecord getDDMFormInstanceRecord(
			long formInstanceRecordId)
		throws PortalException;

	/**
	 * Returns the ddm form instance record matching the UUID and group.
	 *
	 * @param uuid the ddm form instance record's UUID
	 * @param groupId the primary key of the group
	 * @return the matching ddm form instance record
	 * @throws PortalException if a matching ddm form instance record could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DDMFormInstanceRecord getDDMFormInstanceRecordByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException;

	/**
	 * Returns a range of all the ddm form instance records.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceRecordModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ddm form instance records
	 * @param end the upper bound of the range of ddm form instance records (not inclusive)
	 * @return the range of ddm form instance records
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DDMFormInstanceRecord> getDDMFormInstanceRecords(
		int start, int end);

	/**
	 * Returns all the ddm form instance records matching the UUID and company.
	 *
	 * @param uuid the UUID of the ddm form instance records
	 * @param companyId the primary key of the company
	 * @return the matching ddm form instance records, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DDMFormInstanceRecord>
		getDDMFormInstanceRecordsByUuidAndCompanyId(
			String uuid, long companyId);

	/**
	 * Returns a range of ddm form instance records matching the UUID and company.
	 *
	 * @param uuid the UUID of the ddm form instance records
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of ddm form instance records
	 * @param end the upper bound of the range of ddm form instance records (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching ddm form instance records, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DDMFormInstanceRecord>
		getDDMFormInstanceRecordsByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			OrderByComparator<DDMFormInstanceRecord> orderByComparator);

	/**
	 * Returns the number of ddm form instance records.
	 *
	 * @return the number of ddm form instance records
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getDDMFormInstanceRecordsCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DDMFormValues getDDMFormValues(
			DDMForm ddmForm, long storageId, String storageType)
		throws StorageException;

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 #getDDMFormValues(DDMForm, long, String)}
	 */
	@Deprecated
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DDMFormValues getDDMFormValues(long storageId, DDMForm ddmForm)
		throws StorageException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		PortletDataContext portletDataContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DDMFormInstanceRecord getFormInstanceRecord(
			long formInstanceRecordId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DDMFormInstanceRecord> getFormInstanceRecords(
		long ddmFormInstanceId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DDMFormInstanceRecord> getFormInstanceRecords(
		long ddmFormInstanceId, int status, int start, int end,
		OrderByComparator<DDMFormInstanceRecord> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DDMFormInstanceRecord> getFormInstanceRecords(
		long ddmFormInstanceId, long userId, int start, int end,
		OrderByComparator<DDMFormInstanceRecord> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFormInstanceRecordsCount(long ddmFormInstanceId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFormInstanceRecordsCount(long ddmFormInstanceId, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFormInstanceRecordsCount(long ddmFormInstanceId, long userId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFormInstanceRecordsCount(
		long ddmFormInstanceId, String ipAddress);

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

	public void revertFormInstanceRecord(
			long userId, long ddmFormInstanceRecordId, String version,
			ServiceContext serviceContext)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BaseModelSearchResult<DDMFormInstanceRecord>
			searchFormInstanceRecords(
				long formInstanceId, String[] notEmptyFields, int status,
				int start, int end, Sort sort)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BaseModelSearchResult<DDMFormInstanceRecord>
		searchFormInstanceRecords(SearchContext searchContext);

	/**
	 * Updates the ddm form instance record in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMFormInstanceRecordLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddmFormInstanceRecord the ddm form instance record
	 * @return the ddm form instance record that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public DDMFormInstanceRecord updateDDMFormInstanceRecord(
		DDMFormInstanceRecord ddmFormInstanceRecord);

	@Indexable(type = IndexableType.REINDEX)
	public DDMFormInstanceRecord updateFormInstanceRecord(
			long userId, long ddmFormInstanceRecordId, boolean majorVersion,
			DDMFormValues ddmFormValues, ServiceContext serviceContext)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public DDMFormInstanceRecord updateStatus(
			long userId, long recordVersionId, int status,
			ServiceContext serviceContext)
		throws PortalException;

	@Override
	@Transactional(enabled = false)
	public CTPersistence<DDMFormInstanceRecord> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<DDMFormInstanceRecord> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<DDMFormInstanceRecord>, R, E>
				updateUnsafeFunction)
		throws E;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1018099160