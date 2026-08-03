/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service;

import com.liferay.object.model.ObjectView;
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
 * Provides the local service utility for ObjectView. This utility wraps
 * <code>com.liferay.object.service.impl.ObjectViewLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Marco Leo
 * @see ObjectViewLocalService
 * @generated
 */
public class ObjectViewLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.object.service.impl.ObjectViewLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the object view to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectViewLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectView the object view
	 * @return the object view that was added
	 */
	public static ObjectView addObjectView(ObjectView objectView) {
		return getService().addObjectView(objectView);
	}

	public static ObjectView addObjectView(
			String externalReferenceCode, long userId, long objectDefinitionId,
			boolean defaultObjectView, Map<java.util.Locale, String> nameMap,
			List<com.liferay.object.model.ObjectViewColumn> objectViewColumns,
			List<com.liferay.object.model.ObjectViewFilterColumn>
				objectViewFilterColumns,
			List<com.liferay.object.model.ObjectViewSortColumn>
				objectViewSortColumns)
		throws PortalException {

		return getService().addObjectView(
			externalReferenceCode, userId, objectDefinitionId,
			defaultObjectView, nameMap, objectViewColumns,
			objectViewFilterColumns, objectViewSortColumns);
	}

	/**
	 * Creates a new object view with the primary key. Does not add the object view to the database.
	 *
	 * @param objectViewId the primary key for the new object view
	 * @return the new object view
	 */
	public static ObjectView createObjectView(long objectViewId) {
		return getService().createObjectView(objectViewId);
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
	 * Deletes the object view with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectViewLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectViewId the primary key of the object view
	 * @return the object view that was removed
	 * @throws PortalException if a object view with the primary key could not be found
	 */
	public static ObjectView deleteObjectView(long objectViewId)
		throws PortalException {

		return getService().deleteObjectView(objectViewId);
	}

	/**
	 * Deletes the object view from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectViewLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectView the object view
	 * @return the object view that was removed
	 */
	public static ObjectView deleteObjectView(ObjectView objectView) {
		return getService().deleteObjectView(objectView);
	}

	public static void deleteObjectViews(long objectDefinitionId)
		throws PortalException {

		getService().deleteObjectViews(objectDefinitionId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectViewModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectViewModelImpl</code>.
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

	public static ObjectView fetchDefaultObjectView(long objectDefinitionId) {
		return getService().fetchDefaultObjectView(objectDefinitionId);
	}

	public static ObjectView fetchObjectView(long objectViewId) {
		return getService().fetchObjectView(objectViewId);
	}

	public static ObjectView fetchObjectView(
		String externalReferenceCode, long objectDefinitionId) {

		return getService().fetchObjectView(
			externalReferenceCode, objectDefinitionId);
	}

	/**
	 * Returns the object view with the matching UUID and company.
	 *
	 * @param uuid the object view's UUID
	 * @param companyId the primary key of the company
	 * @return the matching object view, or <code>null</code> if a matching object view could not be found
	 */
	public static ObjectView fetchObjectViewByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().fetchObjectViewByUuidAndCompanyId(uuid, companyId);
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
	 * Returns the object view with the primary key.
	 *
	 * @param objectViewId the primary key of the object view
	 * @return the object view
	 * @throws PortalException if a object view with the primary key could not be found
	 */
	public static ObjectView getObjectView(long objectViewId)
		throws PortalException {

		return getService().getObjectView(objectViewId);
	}

	/**
	 * Returns the object view with the matching UUID and company.
	 *
	 * @param uuid the object view's UUID
	 * @param companyId the primary key of the company
	 * @return the matching object view
	 * @throws PortalException if a matching object view could not be found
	 */
	public static ObjectView getObjectViewByUuidAndCompanyId(
			String uuid, long companyId)
		throws PortalException {

		return getService().getObjectViewByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of all the object views.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectViewModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object views
	 * @param end the upper bound of the range of object views (not inclusive)
	 * @return the range of object views
	 */
	public static List<ObjectView> getObjectViews(int start, int end) {
		return getService().getObjectViews(start, end);
	}

	public static List<ObjectView> getObjectViews(long objectDefinitionId) {
		return getService().getObjectViews(objectDefinitionId);
	}

	/**
	 * Returns the number of object views.
	 *
	 * @return the number of object views
	 */
	public static int getObjectViewsCount() {
		return getService().getObjectViewsCount();
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

	public static void unassociateObjectField(
		com.liferay.object.model.ObjectField objectField) {

		getService().unassociateObjectField(objectField);
	}

	/**
	 * Updates the object view in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectViewLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectView the object view
	 * @return the object view that was updated
	 */
	public static ObjectView updateObjectView(ObjectView objectView) {
		return getService().updateObjectView(objectView);
	}

	public static ObjectView updateObjectView(
			String externalReferenceCode, long objectViewId,
			boolean defaultObjectView, Map<java.util.Locale, String> nameMap,
			List<com.liferay.object.model.ObjectViewColumn> objectViewColumns,
			List<com.liferay.object.model.ObjectViewFilterColumn>
				objectViewFilterColumns,
			List<com.liferay.object.model.ObjectViewSortColumn>
				objectViewSortColumns)
		throws PortalException {

		return getService().updateObjectView(
			externalReferenceCode, objectViewId, defaultObjectView, nameMap,
			objectViewColumns, objectViewFilterColumns, objectViewSortColumns);
	}

	public static ObjectViewLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<ObjectViewLocalService> _serviceSnapshot =
		new Snapshot<>(
			ObjectViewLocalServiceUtil.class, ObjectViewLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-893323279