/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.engine.service;

import com.liferay.data.engine.model.DEDataListView;
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
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
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
 * Provides the local service interface for DEDataListView. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see DEDataListViewLocalServiceUtil
 * @generated
 */
@CTAware
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface DEDataListViewLocalService
	extends BaseLocalService, CTService<DEDataListView>,
			PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.data.engine.service.impl.DEDataListViewLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the de data list view local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link DEDataListViewLocalServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Adds the de data list view to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DEDataListViewLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param deDataListView the de data list view
	 * @return the de data list view that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public DEDataListView addDEDataListView(DEDataListView deDataListView);

	@Indexable(type = IndexableType.REINDEX)
	public DEDataListView addDEDataListView(
			long groupId, long companyId, long userId, String appliedFilters,
			long ddmStructureId, String fieldNames, Map<Locale, String> name,
			String sortField)
		throws Exception;

	/**
	 * Creates a new de data list view with the primary key. Does not add the de data list view to the database.
	 *
	 * @param deDataListViewId the primary key for the new de data list view
	 * @return the new de data list view
	 */
	@Transactional(enabled = false)
	public DEDataListView createDEDataListView(long deDataListViewId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Deletes the de data list view from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DEDataListViewLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param deDataListView the de data list view
	 * @return the de data list view that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	public DEDataListView deleteDEDataListView(DEDataListView deDataListView);

	/**
	 * Deletes the de data list view with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DEDataListViewLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param deDataListViewId the primary key of the de data list view
	 * @return the de data list view that was removed
	 * @throws PortalException if a de data list view with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public DEDataListView deleteDEDataListView(long deDataListViewId)
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.data.engine.model.impl.DEDataListViewModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.data.engine.model.impl.DEDataListViewModelImpl</code>.
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
	public DEDataListView fetchDEDataListView(long deDataListViewId);

	/**
	 * Returns the de data list view matching the UUID and group.
	 *
	 * @param uuid the de data list view's UUID
	 * @param groupId the primary key of the group
	 * @return the matching de data list view, or <code>null</code> if a matching de data list view could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DEDataListView fetchDEDataListViewByUuidAndGroupId(
		String uuid, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	/**
	 * Returns the de data list view with the primary key.
	 *
	 * @param deDataListViewId the primary key of the de data list view
	 * @return the de data list view
	 * @throws PortalException if a de data list view with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DEDataListView getDEDataListView(long deDataListViewId)
		throws PortalException;

	/**
	 * Returns the de data list view matching the UUID and group.
	 *
	 * @param uuid the de data list view's UUID
	 * @param groupId the primary key of the group
	 * @return the matching de data list view
	 * @throws PortalException if a matching de data list view could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DEDataListView getDEDataListViewByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException;

	/**
	 * Returns a range of all the de data list views.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.data.engine.model.impl.DEDataListViewModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of de data list views
	 * @param end the upper bound of the range of de data list views (not inclusive)
	 * @return the range of de data list views
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DEDataListView> getDEDataListViews(int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DEDataListView> getDEDataListViews(long ddmStructureId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DEDataListView> getDEDataListViews(
		long groupId, long companyId, long ddmStructureId, int start, int end,
		OrderByComparator<DEDataListView> orderByComparator);

	/**
	 * Returns all the de data list views matching the UUID and company.
	 *
	 * @param uuid the UUID of the de data list views
	 * @param companyId the primary key of the company
	 * @return the matching de data list views, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DEDataListView> getDEDataListViewsByUuidAndCompanyId(
		String uuid, long companyId);

	/**
	 * Returns a range of de data list views matching the UUID and company.
	 *
	 * @param uuid the UUID of the de data list views
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of de data list views
	 * @param end the upper bound of the range of de data list views (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching de data list views, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DEDataListView> getDEDataListViewsByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<DEDataListView> orderByComparator);

	/**
	 * Returns the number of de data list views.
	 *
	 * @return the number of de data list views
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getDEDataListViewsCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getDEDataListViewsCount(
		long groupId, long companyId, long ddmStructureId);

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
	 * Updates the de data list view in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DEDataListViewLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param deDataListView the de data list view
	 * @return the de data list view that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public DEDataListView updateDEDataListView(DEDataListView deDataListView);

	@Indexable(type = IndexableType.REINDEX)
	public DEDataListView updateDEDataListView(
			long deDataListViewId, String appliedFilters, String fieldNames,
			Map<Locale, String> nameMap, String sortField)
		throws Exception;

	@Override
	@Transactional(enabled = false)
	public CTPersistence<DEDataListView> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<DEDataListView> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<DEDataListView>, R, E>
				updateUnsafeFunction)
		throws E;

}
// LIFERAY-SERVICE-BUILDER-HASH:1050933529