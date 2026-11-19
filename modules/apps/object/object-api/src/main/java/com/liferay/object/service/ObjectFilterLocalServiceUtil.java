/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service;

import com.liferay.object.model.ObjectFilter;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for ObjectFilter. This utility wraps
 * <code>com.liferay.object.service.impl.ObjectFilterLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Marco Leo
 * @see ObjectFilterLocalService
 * @generated
 */
public class ObjectFilterLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.object.service.impl.ObjectFilterLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static ObjectFilter addObjectFilter(
			long userId, long objectFieldId, String filterBy, String filterType,
			String json)
		throws PortalException {

		return getService().addObjectFilter(
			userId, objectFieldId, filterBy, filterType, json);
	}

	/**
	 * Adds the object filter to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectFilterLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectFilter the object filter
	 * @return the object filter that was added
	 */
	public static ObjectFilter addObjectFilter(ObjectFilter objectFilter) {
		return getService().addObjectFilter(objectFilter);
	}

	/**
	 * Creates a new object filter with the primary key. Does not add the object filter to the database.
	 *
	 * @param objectFilterId the primary key for the new object filter
	 * @return the new object filter
	 */
	public static ObjectFilter createObjectFilter(long objectFilterId) {
		return getService().createObjectFilter(objectFilterId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	public static void deleteObjectFieldObjectFilter(long objectFieldId) {
		getService().deleteObjectFieldObjectFilter(objectFieldId);
	}

	/**
	 * Deletes the object filter with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectFilterLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectFilterId the primary key of the object filter
	 * @return the object filter that was removed
	 * @throws PortalException if a object filter with the primary key could not be found
	 */
	public static ObjectFilter deleteObjectFilter(long objectFilterId)
		throws PortalException {

		return getService().deleteObjectFilter(objectFilterId);
	}

	/**
	 * Deletes the object filter from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectFilterLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectFilter the object filter
	 * @return the object filter that was removed
	 */
	public static ObjectFilter deleteObjectFilter(ObjectFilter objectFilter) {
		return getService().deleteObjectFilter(objectFilter);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectFilterModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectFilterModelImpl</code>.
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

	public static ObjectFilter fetchObjectFilter(long objectFilterId) {
		return getService().fetchObjectFilter(objectFilterId);
	}

	/**
	 * Returns the object filter with the matching UUID and company.
	 *
	 * @param uuid the object filter's UUID
	 * @param companyId the primary key of the company
	 * @return the matching object filter, or <code>null</code> if a matching object filter could not be found
	 */
	public static ObjectFilter fetchObjectFilterByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().fetchObjectFilterByUuidAndCompanyId(
			uuid, companyId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
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
	 * Returns the object filter with the primary key.
	 *
	 * @param objectFilterId the primary key of the object filter
	 * @return the object filter
	 * @throws PortalException if a object filter with the primary key could not be found
	 */
	public static ObjectFilter getObjectFilter(long objectFilterId)
		throws PortalException {

		return getService().getObjectFilter(objectFilterId);
	}

	/**
	 * Returns the object filter with the matching UUID and company.
	 *
	 * @param uuid the object filter's UUID
	 * @param companyId the primary key of the company
	 * @return the matching object filter
	 * @throws PortalException if a matching object filter could not be found
	 */
	public static ObjectFilter getObjectFilterByUuidAndCompanyId(
			String uuid, long companyId)
		throws PortalException {

		return getService().getObjectFilterByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of all the object filters.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectFilterModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object filters
	 * @param end the upper bound of the range of object filters (not inclusive)
	 * @return the range of object filters
	 */
	public static List<ObjectFilter> getObjectFilters(int start, int end) {
		return getService().getObjectFilters(start, end);
	}

	/**
	 * Returns the number of object filters.
	 *
	 * @return the number of object filters
	 */
	public static int getObjectFiltersCount() {
		return getService().getObjectFiltersCount();
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
	 * Updates the object filter in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectFilterLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectFilter the object filter
	 * @return the object filter that was updated
	 */
	public static ObjectFilter updateObjectFilter(ObjectFilter objectFilter) {
		return getService().updateObjectFilter(objectFilter);
	}

	public static ObjectFilterLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<ObjectFilterLocalService> _serviceSnapshot =
		new Snapshot<>(
			ObjectFilterLocalServiceUtil.class, ObjectFilterLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-861988776