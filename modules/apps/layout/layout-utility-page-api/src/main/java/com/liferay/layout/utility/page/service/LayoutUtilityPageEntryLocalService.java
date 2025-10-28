/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.utility.page.service;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.layout.utility.page.model.LayoutUtilityPageEntry;
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
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
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
 * Provides the local service interface for LayoutUtilityPageEntry. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see LayoutUtilityPageEntryLocalServiceUtil
 * @generated
 */
@CTAware
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface LayoutUtilityPageEntryLocalService
	extends BaseLocalService, CTService<LayoutUtilityPageEntry>,
			PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.layout.utility.page.service.impl.LayoutUtilityPageEntryLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the layout utility page entry local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link LayoutUtilityPageEntryLocalServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Adds the layout utility page entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutUtilityPageEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutUtilityPageEntry the layout utility page entry
	 * @return the layout utility page entry that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public LayoutUtilityPageEntry addLayoutUtilityPageEntry(
		LayoutUtilityPageEntry layoutUtilityPageEntry);

	@Indexable(type = IndexableType.REINDEX)
	public LayoutUtilityPageEntry addLayoutUtilityPageEntry(
			String externalReferenceCode, long userId, long groupId, long plid,
			long previewFileEntryId, boolean defaultLayoutUtilityPageEntry,
			String name, String type, long masterLayoutPlid,
			ServiceContext serviceContext)
		throws PortalException;

	public LayoutUtilityPageEntry copyLayoutUtilityPageEntry(
			long userId, long groupId, long sourceLayoutUtilityPageEntryId,
			ServiceContext serviceContext)
		throws Exception;

	/**
	 * Creates a new layout utility page entry with the primary key. Does not add the layout utility page entry to the database.
	 *
	 * @param LayoutUtilityPageEntryId the primary key for the new layout utility page entry
	 * @return the new layout utility page entry
	 */
	@Transactional(enabled = false)
	public LayoutUtilityPageEntry createLayoutUtilityPageEntry(
		long LayoutUtilityPageEntryId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Deletes the layout utility page entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutUtilityPageEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutUtilityPageEntry the layout utility page entry
	 * @return the layout utility page entry that was removed
	 * @throws PortalException
	 */
	@Indexable(type = IndexableType.DELETE)
	@SystemEvent(
		action = SystemEventConstants.ACTION_SKIP,
		type = SystemEventConstants.TYPE_DELETE
	)
	public LayoutUtilityPageEntry deleteLayoutUtilityPageEntry(
			LayoutUtilityPageEntry layoutUtilityPageEntry)
		throws PortalException;

	/**
	 * Deletes the layout utility page entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutUtilityPageEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param LayoutUtilityPageEntryId the primary key of the layout utility page entry
	 * @return the layout utility page entry that was removed
	 * @throws PortalException if a layout utility page entry with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public LayoutUtilityPageEntry deleteLayoutUtilityPageEntry(
			long LayoutUtilityPageEntryId)
		throws PortalException;

	public LayoutUtilityPageEntry deleteLayoutUtilityPageEntry(
			String externalReferenceCode, long groupId)
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.utility.page.model.impl.LayoutUtilityPageEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.utility.page.model.impl.LayoutUtilityPageEntryModelImpl</code>.
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
	public LayoutUtilityPageEntry fetchDefaultLayoutUtilityPageEntry(
		long groupId, String type);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public LayoutUtilityPageEntry fetchLayoutUtilityPageEntry(
		long LayoutUtilityPageEntryId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public LayoutUtilityPageEntry fetchLayoutUtilityPageEntry(
		long groupId, String name, String type);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public LayoutUtilityPageEntry
		fetchLayoutUtilityPageEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public LayoutUtilityPageEntry fetchLayoutUtilityPageEntryByPlid(long plid);

	/**
	 * Returns the layout utility page entry matching the UUID and group.
	 *
	 * @param uuid the layout utility page entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching layout utility page entry, or <code>null</code> if a matching layout utility page entry could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public LayoutUtilityPageEntry fetchLayoutUtilityPageEntryByUuidAndGroupId(
		String uuid, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public LayoutUtilityPageEntry getDefaultLayoutUtilityPageEntry(
			long groupId, String type)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		PortletDataContext portletDataContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	/**
	 * Returns a range of all the layout utility page entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.utility.page.model.impl.LayoutUtilityPageEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of layout utility page entries
	 * @param end the upper bound of the range of layout utility page entries (not inclusive)
	 * @return the range of layout utility page entries
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<LayoutUtilityPageEntry> getLayoutUtilityPageEntries(
		int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<LayoutUtilityPageEntry> getLayoutUtilityPageEntries(
		long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<LayoutUtilityPageEntry> getLayoutUtilityPageEntries(
		long groupId, int start, int end,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<LayoutUtilityPageEntry> getLayoutUtilityPageEntries(
		long groupId, String type, int start, int end,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<LayoutUtilityPageEntry> getLayoutUtilityPageEntries(
		long groupId, String keyword, String[] types, int start, int end,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<LayoutUtilityPageEntry> getLayoutUtilityPageEntries(
		long groupId, String[] types, int start, int end,
		OrderByComparator<LayoutUtilityPageEntry> orderByComparator);

	/**
	 * Returns all the layout utility page entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the layout utility page entries
	 * @param companyId the primary key of the company
	 * @return the matching layout utility page entries, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<LayoutUtilityPageEntry>
		getLayoutUtilityPageEntriesByUuidAndCompanyId(
			String uuid, long companyId);

	/**
	 * Returns a range of layout utility page entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the layout utility page entries
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of layout utility page entries
	 * @param end the upper bound of the range of layout utility page entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching layout utility page entries, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<LayoutUtilityPageEntry>
		getLayoutUtilityPageEntriesByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			OrderByComparator<LayoutUtilityPageEntry> orderByComparator);

	/**
	 * Returns the number of layout utility page entries.
	 *
	 * @return the number of layout utility page entries
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getLayoutUtilityPageEntriesCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getLayoutUtilityPageEntriesCount(long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getLayoutUtilityPageEntriesCount(
		long groupId, String keyword, String[] types);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getLayoutUtilityPageEntriesCount(long groupId, String[] types);

	/**
	 * Returns the layout utility page entry with the primary key.
	 *
	 * @param LayoutUtilityPageEntryId the primary key of the layout utility page entry
	 * @return the layout utility page entry
	 * @throws PortalException if a layout utility page entry with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public LayoutUtilityPageEntry getLayoutUtilityPageEntry(
			long LayoutUtilityPageEntryId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public LayoutUtilityPageEntry
			getLayoutUtilityPageEntryByExternalReferenceCode(
				String externalReferenceCode, long groupId)
		throws PortalException;

	/**
	 * Returns the layout utility page entry matching the UUID and group.
	 *
	 * @param uuid the layout utility page entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching layout utility page entry
	 * @throws PortalException if a matching layout utility page entry could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public LayoutUtilityPageEntry getLayoutUtilityPageEntryByUuidAndGroupId(
			String uuid, long groupId)
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

	public LayoutUtilityPageEntry setDefaultLayoutUtilityPageEntry(
			long layoutUtilityPageEntryId)
		throws PortalException;

	/**
	 * Updates the layout utility page entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutUtilityPageEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutUtilityPageEntry the layout utility page entry
	 * @return the layout utility page entry that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public LayoutUtilityPageEntry updateLayoutUtilityPageEntry(
		LayoutUtilityPageEntry layoutUtilityPageEntry);

	public LayoutUtilityPageEntry updateLayoutUtilityPageEntry(
		long layoutUtilityPageEntryId, long previewFileEntryId);

	@Indexable(type = IndexableType.REINDEX)
	public LayoutUtilityPageEntry updateLayoutUtilityPageEntry(
			long layoutUtilityPageEntryId, String name)
		throws PortalException;

	@Override
	@Transactional(enabled = false)
	public CTPersistence<LayoutUtilityPageEntry> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<LayoutUtilityPageEntry> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<LayoutUtilityPageEntry>, R, E>
				updateUnsafeFunction)
		throws E;

}