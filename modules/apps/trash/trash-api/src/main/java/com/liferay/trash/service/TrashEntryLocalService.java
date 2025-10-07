/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.trash.service;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.trash.model.TrashEntry;

import java.io.Serializable;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for TrashEntry. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see TrashEntryLocalServiceUtil
 * @generated
 */
@CTAware
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface TrashEntryLocalService
	extends BaseLocalService, CTService<TrashEntry>,
			PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.trash.service.impl.TrashEntryLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the trash entry local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link TrashEntryLocalServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Moves an entry to trash.
	 *
	 * @param userId the primary key of the user removing the entity
	 * @param groupId the primary key of the entry's group
	 * @param className the class name of the entity
	 * @param classPK the primary key of the entity
	 * @param classUuid the UUID of the entity's class
	 * @param referrerClassName the referrer class name used to add a deletion
	 {@link SystemEvent}
	 * @param status the status of the entity prior to being moved to trash
	 * @param statusOVPs the primary keys and statuses of any of the entry's
	 versions (e.g., {@link
	 com.liferay.portlet.documentlibrary.model.DLFileVersion})
	 * @param typeSettingsUnicodeProperties the type settings properties
	 * @return the trashEntry
	 */
	public TrashEntry addTrashEntry(
			long userId, long groupId, String className, long classPK,
			String classUuid, String referrerClassName, int status,
			List<ObjectValuePair<Long, Integer>> statusOVPs,
			UnicodeProperties typeSettingsUnicodeProperties)
		throws PortalException;

	/**
	 * Adds the trash entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect TrashEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param trashEntry the trash entry
	 * @return the trash entry that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public TrashEntry addTrashEntry(TrashEntry trashEntry);

	public void checkEntries() throws PortalException;

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Creates a new trash entry with the primary key. Does not add the trash entry to the database.
	 *
	 * @param entryId the primary key for the new trash entry
	 * @return the new trash entry
	 */
	@Transactional(enabled = false)
	public TrashEntry createTrashEntry(long entryId);

	public void deleteEntries(long groupId);

	public void deleteEntries(long groupId, boolean deleteTrashedModels);

	/**
	 * Deletes the trash entry with the primary key.
	 *
	 * @param entryId the primary key of the trash entry
	 * @return the trash entry with the primary key
	 */
	public TrashEntry deleteEntry(long entryId);

	/**
	 * Deletes the trash entry with the entity class name and primary key.
	 *
	 * @param className the class name of entity
	 * @param classPK the primary key of the entry
	 * @return the trash entry with the entity class name and primary key
	 */
	public TrashEntry deleteEntry(String className, long classPK);

	@Indexable(type = IndexableType.DELETE)
	public TrashEntry deleteEntry(TrashEntry trashEntry);

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException;

	public void deleteTrashEntries(long companyId, String className);

	/**
	 * Deletes the trash entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect TrashEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param entryId the primary key of the trash entry
	 * @return the trash entry that was removed
	 * @throws PortalException if a trash entry with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public TrashEntry deleteTrashEntry(long entryId) throws PortalException;

	/**
	 * Deletes the trash entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect TrashEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param trashEntry the trash entry
	 * @return the trash entry that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	public TrashEntry deleteTrashEntry(TrashEntry trashEntry);

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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.trash.model.impl.TrashEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.trash.model.impl.TrashEntryModelImpl</code>.
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

	/**
	 * Returns the trash entry with the primary key.
	 *
	 * @param entryId the primary key of the entry
	 * @return the trash entry with the primary key
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public TrashEntry fetchEntry(long entryId);

	/**
	 * Returns the trash entry with the entity class name and primary key.
	 *
	 * @param className the class name of the entity
	 * @param classPK the primary key of the entity
	 * @return the trash entry with the entity class name and primary key
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public TrashEntry fetchEntry(String className, long classPK);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public TrashEntry fetchTrashEntry(long entryId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	/**
	 * Returns the trash entries with the matching group ID.
	 *
	 * @param groupId the primary key of the group
	 * @return the trash entries with the group ID
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<TrashEntry> getEntries(long groupId);

	/**
	 * Returns a range of all the trash entries matching the group ID.
	 *
	 * @param groupId the primary key of the group
	 * @param start the lower bound of the range of trash entries to return
	 * @param end the upper bound of the range of trash entries to return (not
	 inclusive)
	 * @return the range of matching trash entries
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<TrashEntry> getEntries(long groupId, int start, int end);

	/**
	 * Returns a range of all the trash entries matching the group ID.
	 *
	 * @param groupId the primary key of the group
	 * @param start the lower bound of the range of trash entries to return
	 * @param end the upper bound of the range of trash entries to return (not
	 inclusive)
	 * @param orderByComparator the comparator to order the trash entries
	 (optionally <code>null</code>)
	 * @return the range of matching trash entries ordered by comparator
	 <code>orderByComparator</code>
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<TrashEntry> getEntries(
		long groupId, int start, int end,
		OrderByComparator<TrashEntry> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<TrashEntry> getEntries(long groupId, String className);

	/**
	 * Returns the number of trash entries with the group ID.
	 *
	 * @param groupId the primary key of the group
	 * @return the number of matching trash entries
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getEntriesCount(long groupId);

	/**
	 * Returns the trash entry with the primary key.
	 *
	 * @param entryId the primary key of the trash entry
	 * @return the trash entry with the primary key
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public TrashEntry getEntry(long entryId) throws PortalException;

	/**
	 * Returns the entry with the entity class name and primary key.
	 *
	 * @param className the class name of the entity
	 * @param classPK the primary key of the entity
	 * @return the trash entry with the entity class name and primary key
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public TrashEntry getEntry(String className, long classPK)
		throws PortalException;

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
	 * Returns a range of all the trash entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.trash.model.impl.TrashEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of trash entries
	 * @param end the upper bound of the range of trash entries (not inclusive)
	 * @return the range of trash entries
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<TrashEntry> getTrashEntries(int start, int end);

	/**
	 * Returns the number of trash entries.
	 *
	 * @return the number of trash entries
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getTrashEntriesCount();

	/**
	 * Returns the trash entry with the primary key.
	 *
	 * @param entryId the primary key of the trash entry
	 * @return the trash entry
	 * @throws PortalException if a trash entry with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public TrashEntry getTrashEntry(long entryId) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Hits search(
		long companyId, long groupId, long userId, String keywords, int start,
		int end, Sort sort);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BaseModelSearchResult<TrashEntry> searchTrashEntries(
		long companyId, long groupId, long userId, String keywords, int start,
		int end, Sort sort);

	/**
	 * Updates the trash entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect TrashEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param trashEntry the trash entry
	 * @return the trash entry that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public TrashEntry updateTrashEntry(TrashEntry trashEntry);

	@Override
	@Transactional(enabled = false)
	public CTPersistence<TrashEntry> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<TrashEntry> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<TrashEntry>, R, E>
				updateUnsafeFunction)
		throws E;

}