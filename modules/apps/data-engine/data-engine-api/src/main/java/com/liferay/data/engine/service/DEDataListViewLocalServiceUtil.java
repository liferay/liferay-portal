/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.engine.service;

import com.liferay.data.engine.model.DEDataListView;
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
 * Provides the local service utility for DEDataListView. This utility wraps
 * <code>com.liferay.data.engine.service.impl.DEDataListViewLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see DEDataListViewLocalService
 * @generated
 */
public class DEDataListViewLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.data.engine.service.impl.DEDataListViewLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
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
	public static DEDataListView addDEDataListView(
		DEDataListView deDataListView) {

		return getService().addDEDataListView(deDataListView);
	}

	public static DEDataListView addDEDataListView(
			long groupId, long companyId, long userId, String appliedFilters,
			long ddmStructureId, String fieldNames,
			Map<java.util.Locale, String> name, String sortField)
		throws Exception {

		return getService().addDEDataListView(
			groupId, companyId, userId, appliedFilters, ddmStructureId,
			fieldNames, name, sortField);
	}

	/**
	 * Creates a new de data list view with the primary key. Does not add the de data list view to the database.
	 *
	 * @param deDataListViewId the primary key for the new de data list view
	 * @return the new de data list view
	 */
	public static DEDataListView createDEDataListView(long deDataListViewId) {
		return getService().createDEDataListView(deDataListViewId);
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
	 * Deletes the de data list view from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DEDataListViewLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param deDataListView the de data list view
	 * @return the de data list view that was removed
	 */
	public static DEDataListView deleteDEDataListView(
		DEDataListView deDataListView) {

		return getService().deleteDEDataListView(deDataListView);
	}

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
	public static DEDataListView deleteDEDataListView(long deDataListViewId)
		throws PortalException {

		return getService().deleteDEDataListView(deDataListViewId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.data.engine.model.impl.DEDataListViewModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.data.engine.model.impl.DEDataListViewModelImpl</code>.
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

	public static DEDataListView fetchDEDataListView(long deDataListViewId) {
		return getService().fetchDEDataListView(deDataListViewId);
	}

	/**
	 * Returns the de data list view matching the UUID and group.
	 *
	 * @param uuid the de data list view's UUID
	 * @param groupId the primary key of the group
	 * @return the matching de data list view, or <code>null</code> if a matching de data list view could not be found
	 */
	public static DEDataListView fetchDEDataListViewByUuidAndGroupId(
		String uuid, long groupId) {

		return getService().fetchDEDataListViewByUuidAndGroupId(uuid, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the de data list view with the primary key.
	 *
	 * @param deDataListViewId the primary key of the de data list view
	 * @return the de data list view
	 * @throws PortalException if a de data list view with the primary key could not be found
	 */
	public static DEDataListView getDEDataListView(long deDataListViewId)
		throws PortalException {

		return getService().getDEDataListView(deDataListViewId);
	}

	/**
	 * Returns the de data list view matching the UUID and group.
	 *
	 * @param uuid the de data list view's UUID
	 * @param groupId the primary key of the group
	 * @return the matching de data list view
	 * @throws PortalException if a matching de data list view could not be found
	 */
	public static DEDataListView getDEDataListViewByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getDEDataListViewByUuidAndGroupId(uuid, groupId);
	}

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
	public static List<DEDataListView> getDEDataListViews(int start, int end) {
		return getService().getDEDataListViews(start, end);
	}

	public static List<DEDataListView> getDEDataListViews(long ddmStructureId) {
		return getService().getDEDataListViews(ddmStructureId);
	}

	public static List<DEDataListView> getDEDataListViews(
		long groupId, long companyId, long ddmStructureId, int start, int end,
		OrderByComparator<DEDataListView> orderByComparator) {

		return getService().getDEDataListViews(
			groupId, companyId, ddmStructureId, start, end, orderByComparator);
	}

	/**
	 * Returns all the de data list views matching the UUID and company.
	 *
	 * @param uuid the UUID of the de data list views
	 * @param companyId the primary key of the company
	 * @return the matching de data list views, or an empty list if no matches were found
	 */
	public static List<DEDataListView> getDEDataListViewsByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().getDEDataListViewsByUuidAndCompanyId(
			uuid, companyId);
	}

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
	public static List<DEDataListView> getDEDataListViewsByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<DEDataListView> orderByComparator) {

		return getService().getDEDataListViewsByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of de data list views.
	 *
	 * @return the number of de data list views
	 */
	public static int getDEDataListViewsCount() {
		return getService().getDEDataListViewsCount();
	}

	public static int getDEDataListViewsCount(
		long groupId, long companyId, long ddmStructureId) {

		return getService().getDEDataListViewsCount(
			groupId, companyId, ddmStructureId);
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
	 * Updates the de data list view in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DEDataListViewLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param deDataListView the de data list view
	 * @return the de data list view that was updated
	 */
	public static DEDataListView updateDEDataListView(
		DEDataListView deDataListView) {

		return getService().updateDEDataListView(deDataListView);
	}

	public static DEDataListView updateDEDataListView(
			long deDataListViewId, String appliedFilters, String fieldNames,
			Map<java.util.Locale, String> nameMap, String sortField)
		throws Exception {

		return getService().updateDEDataListView(
			deDataListViewId, appliedFilters, fieldNames, nameMap, sortField);
	}

	public static DEDataListViewLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<DEDataListViewLocalService> _serviceSnapshot =
		new Snapshot<>(
			DEDataListViewLocalServiceUtil.class,
			DEDataListViewLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-1141928109