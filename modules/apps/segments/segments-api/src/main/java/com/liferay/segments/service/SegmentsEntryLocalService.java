/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.service;

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
import com.liferay.segments.model.SegmentsEntry;

import java.io.Serializable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for SegmentsEntry. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Eduardo Garcia
 * @see SegmentsEntryLocalServiceUtil
 * @generated
 */
@CTAware
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface SegmentsEntryLocalService
	extends BaseLocalService, CTService<SegmentsEntry>,
			PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.segments.service.impl.SegmentsEntryLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the segments entry local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link SegmentsEntryLocalServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Adds the segments entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SegmentsEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param segmentsEntry the segments entry
	 * @return the segments entry that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public SegmentsEntry addSegmentsEntry(SegmentsEntry segmentsEntry);

	@Indexable(type = IndexableType.REINDEX)
	public SegmentsEntry addSegmentsEntry(
			String externalReferenceCode, String segmentsEntryKey,
			Map<Locale, String> nameMap, Map<Locale, String> descriptionMap,
			boolean active, String criteria, String source,
			ServiceContext serviceContext)
		throws PortalException;

	public void addSegmentsEntryClassPKs(
			long segmentsEntryId, long[] classPKs,
			ServiceContext serviceContext)
		throws PortalException;

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Creates a new segments entry with the primary key. Does not add the segments entry to the database.
	 *
	 * @param segmentsEntryId the primary key for the new segments entry
	 * @return the new segments entry
	 */
	@Transactional(enabled = false)
	public SegmentsEntry createSegmentsEntry(long segmentsEntryId);

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException;

	public void deleteSegmentsEntries(long groupId) throws PortalException;

	public void deleteSegmentsEntries(String source) throws PortalException;

	/**
	 * Deletes the segments entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SegmentsEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param segmentsEntryId the primary key of the segments entry
	 * @return the segments entry that was removed
	 * @throws PortalException if a segments entry with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public SegmentsEntry deleteSegmentsEntry(long segmentsEntryId)
		throws PortalException;

	/**
	 * Deletes the segments entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SegmentsEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param segmentsEntry the segments entry
	 * @return the segments entry that was removed
	 * @throws PortalException
	 */
	@Indexable(type = IndexableType.DELETE)
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public SegmentsEntry deleteSegmentsEntry(SegmentsEntry segmentsEntry)
		throws PortalException;

	public void deleteSegmentsEntryClassPKs(
			long segmentsEntryId, long[] classPKs)
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsEntryModelImpl</code>.
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
	public SegmentsEntry fetchSegmentsEntry(long segmentsEntryId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public SegmentsEntry fetchSegmentsEntry(
		long groupId, String segmentsEntryKey);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public SegmentsEntry fetchSegmentsEntryByExternalReferenceCode(
		String externalReferenceCode, long groupId);

	/**
	 * Returns the segments entry matching the UUID and group.
	 *
	 * @param uuid the segments entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching segments entry, or <code>null</code> if a matching segments entry could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public SegmentsEntry fetchSegmentsEntryByUuidAndGroupId(
		String uuid, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

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
	 * Returns a range of all the segments entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.segments.model.impl.SegmentsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of segments entries
	 * @param end the upper bound of the range of segments entries (not inclusive)
	 * @return the range of segments entries
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SegmentsEntry> getSegmentsEntries(int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SegmentsEntry> getSegmentsEntries(
		long groupId, int start, int end,
		OrderByComparator<SegmentsEntry> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SegmentsEntry> getSegmentsEntries(
		long groupId, String[] sources, int start, int end,
		OrderByComparator<SegmentsEntry> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SegmentsEntry> getSegmentsEntries(
		long[] groupIds, boolean active, String[] sources);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SegmentsEntry> getSegmentsEntries(
		long[] segmentsEntryIds, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SegmentsEntry> getSegmentsEntriesBySource(
		long companyId, String source, int start, int end,
		OrderByComparator<SegmentsEntry> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SegmentsEntry> getSegmentsEntriesBySource(
		String source, int start, int end,
		OrderByComparator<SegmentsEntry> orderByComparator);

	/**
	 * Returns all the segments entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the segments entries
	 * @param companyId the primary key of the company
	 * @return the matching segments entries, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SegmentsEntry> getSegmentsEntriesByUuidAndCompanyId(
		String uuid, long companyId);

	/**
	 * Returns a range of segments entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the segments entries
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of segments entries
	 * @param end the upper bound of the range of segments entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching segments entries, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SegmentsEntry> getSegmentsEntriesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<SegmentsEntry> orderByComparator);

	/**
	 * Returns the number of segments entries.
	 *
	 * @return the number of segments entries
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getSegmentsEntriesCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getSegmentsEntriesCount(long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getSegmentsEntriesCount(long groupId, String[] sources);

	/**
	 * Returns the segments entry with the primary key.
	 *
	 * @param segmentsEntryId the primary key of the segments entry
	 * @return the segments entry
	 * @throws PortalException if a segments entry with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public SegmentsEntry getSegmentsEntry(long segmentsEntryId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public SegmentsEntry getSegmentsEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException;

	/**
	 * Returns the segments entry matching the UUID and group.
	 *
	 * @param uuid the segments entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching segments entry
	 * @throws PortalException if a matching segments entry could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public SegmentsEntry getSegmentsEntryByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BaseModelSearchResult<SegmentsEntry> searchSegmentsEntries(
			long companyId, long groupId, String keywords,
			LinkedHashMap<String, Object> params, int start, int end, Sort sort)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BaseModelSearchResult<SegmentsEntry> searchSegmentsEntries(
			SearchContext searchContext)
		throws PortalException;

	/**
	 * Updates the segments entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SegmentsEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param segmentsEntry the segments entry
	 * @return the segments entry that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public SegmentsEntry updateSegmentsEntry(SegmentsEntry segmentsEntry);

	@Indexable(type = IndexableType.REINDEX)
	public SegmentsEntry updateSegmentsEntry(
			String externalReferenceCode, long segmentsEntryId,
			String segmentsEntryKey, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap, boolean active, String criteria,
			ServiceContext serviceContext)
		throws PortalException;

	@Override
	@Transactional(enabled = false)
	public CTPersistence<SegmentsEntry> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<SegmentsEntry> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<SegmentsEntry>, R, E>
				updateUnsafeFunction)
		throws E;

}
// LIFERAY-SERVICE-BUILDER-HASH:-15771358