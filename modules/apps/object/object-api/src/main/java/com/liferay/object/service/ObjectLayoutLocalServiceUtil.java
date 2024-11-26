/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service;

import com.liferay.object.model.ObjectLayout;
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
 * Provides the local service utility for ObjectLayout. This utility wraps
 * <code>com.liferay.object.service.impl.ObjectLayoutLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Marco Leo
 * @see ObjectLayoutLocalService
 * @generated
 */
public class ObjectLayoutLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.object.service.impl.ObjectLayoutLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static ObjectLayout addObjectLayout(
			long userId, long objectDefinitionId, boolean defaultObjectLayout,
			Map<java.util.Locale, String> nameMap,
			List<com.liferay.object.model.ObjectLayoutTab> objectLayoutTabs)
		throws PortalException {

		return getService().addObjectLayout(
			userId, objectDefinitionId, defaultObjectLayout, nameMap,
			objectLayoutTabs);
	}

	/**
	 * Adds the object layout to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectLayoutLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectLayout the object layout
	 * @return the object layout that was added
	 */
	public static ObjectLayout addObjectLayout(ObjectLayout objectLayout) {
		return getService().addObjectLayout(objectLayout);
	}

	/**
	 * Creates a new object layout with the primary key. Does not add the object layout to the database.
	 *
	 * @param objectLayoutId the primary key for the new object layout
	 * @return the new object layout
	 */
	public static ObjectLayout createObjectLayout(long objectLayoutId) {
		return getService().createObjectLayout(objectLayoutId);
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
	 * Deletes the object layout with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectLayoutLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectLayoutId the primary key of the object layout
	 * @return the object layout that was removed
	 * @throws PortalException if a object layout with the primary key could not be found
	 */
	public static ObjectLayout deleteObjectLayout(long objectLayoutId)
		throws PortalException {

		return getService().deleteObjectLayout(objectLayoutId);
	}

	/**
	 * Deletes the object layout from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectLayoutLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectLayout the object layout
	 * @return the object layout that was removed
	 * @throws PortalException
	 */
	public static ObjectLayout deleteObjectLayout(ObjectLayout objectLayout)
		throws PortalException {

		return getService().deleteObjectLayout(objectLayout);
	}

	public static void deleteObjectLayouts(long objectDefinitionId)
		throws PortalException {

		getService().deleteObjectLayouts(objectDefinitionId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectLayoutModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectLayoutModelImpl</code>.
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

	public static ObjectLayout fetchDefaultObjectLayout(
		long objectDefinitionId) {

		return getService().fetchDefaultObjectLayout(objectDefinitionId);
	}

	public static ObjectLayout fetchObjectLayout(long objectLayoutId) {
		return getService().fetchObjectLayout(objectLayoutId);
	}

	/**
	 * Returns the object layout with the matching UUID and company.
	 *
	 * @param uuid the object layout's UUID
	 * @param companyId the primary key of the company
	 * @return the matching object layout, or <code>null</code> if a matching object layout could not be found
	 */
	public static ObjectLayout fetchObjectLayoutByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().fetchObjectLayoutByUuidAndCompanyId(
			uuid, companyId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static ObjectLayout getDefaultObjectLayout(long objectDefinitionId)
		throws PortalException {

		return getService().getDefaultObjectLayout(objectDefinitionId);
	}

	public static List<ObjectLayout> getDefaultObjectLayouts(long companyId) {
		return getService().getDefaultObjectLayouts(companyId);
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
	 * Returns the object layout with the primary key.
	 *
	 * @param objectLayoutId the primary key of the object layout
	 * @return the object layout
	 * @throws PortalException if a object layout with the primary key could not be found
	 */
	public static ObjectLayout getObjectLayout(long objectLayoutId)
		throws PortalException {

		return getService().getObjectLayout(objectLayoutId);
	}

	/**
	 * Returns the object layout with the matching UUID and company.
	 *
	 * @param uuid the object layout's UUID
	 * @param companyId the primary key of the company
	 * @return the matching object layout
	 * @throws PortalException if a matching object layout could not be found
	 */
	public static ObjectLayout getObjectLayoutByUuidAndCompanyId(
			String uuid, long companyId)
		throws PortalException {

		return getService().getObjectLayoutByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of all the object layouts.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object layouts
	 * @param end the upper bound of the range of object layouts (not inclusive)
	 * @return the range of object layouts
	 */
	public static List<ObjectLayout> getObjectLayouts(int start, int end) {
		return getService().getObjectLayouts(start, end);
	}

	public static List<ObjectLayout> getObjectLayouts(long objectDefinitionId) {
		return getService().getObjectLayouts(objectDefinitionId);
	}

	public static List<ObjectLayout> getObjectLayouts(
		long objectDefinitionId, int start, int end) {

		return getService().getObjectLayouts(objectDefinitionId, start, end);
	}

	/**
	 * Returns the number of object layouts.
	 *
	 * @return the number of object layouts
	 */
	public static int getObjectLayoutsCount() {
		return getService().getObjectLayoutsCount();
	}

	public static int getObjectLayoutsCount(long objectDefinitionId) {
		return getService().getObjectLayoutsCount(objectDefinitionId);
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

	public static ObjectLayout updateObjectLayout(
			long objectLayoutId, boolean defaultObjectLayout,
			Map<java.util.Locale, String> nameMap,
			List<com.liferay.object.model.ObjectLayoutTab> objectLayoutTabs)
		throws PortalException {

		return getService().updateObjectLayout(
			objectLayoutId, defaultObjectLayout, nameMap, objectLayoutTabs);
	}

	/**
	 * Updates the object layout in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectLayoutLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectLayout the object layout
	 * @return the object layout that was updated
	 */
	public static ObjectLayout updateObjectLayout(ObjectLayout objectLayout) {
		return getService().updateObjectLayout(objectLayout);
	}

	public static ObjectLayoutLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<ObjectLayoutLocalService> _serviceSnapshot =
		new Snapshot<>(
			ObjectLayoutLocalServiceUtil.class, ObjectLayoutLocalService.class);

}