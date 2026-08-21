/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service;

import com.liferay.object.model.ObjectLayoutTab;
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
 * Provides the local service utility for ObjectLayoutTab. This utility wraps
 * <code>com.liferay.object.service.impl.ObjectLayoutTabLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Marco Leo
 * @see ObjectLayoutTabLocalService
 * @generated
 */
public class ObjectLayoutTabLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.object.service.impl.ObjectLayoutTabLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static ObjectLayoutTab addObjectLayoutTab(
			long userId, long objectLayoutId, long objectRelationshipId,
			Map<java.util.Locale, String> nameMap, int priority)
		throws PortalException {

		return getService().addObjectLayoutTab(
			userId, objectLayoutId, objectRelationshipId, nameMap, priority);
	}

	/**
	 * Adds the object layout tab to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectLayoutTabLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectLayoutTab the object layout tab
	 * @return the object layout tab that was added
	 */
	public static ObjectLayoutTab addObjectLayoutTab(
		ObjectLayoutTab objectLayoutTab) {

		return getService().addObjectLayoutTab(objectLayoutTab);
	}

	/**
	 * Creates a new object layout tab with the primary key. Does not add the object layout tab to the database.
	 *
	 * @param objectLayoutTabId the primary key for the new object layout tab
	 * @return the new object layout tab
	 */
	public static ObjectLayoutTab createObjectLayoutTab(
		long objectLayoutTabId) {

		return getService().createObjectLayoutTab(objectLayoutTabId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	public static void deleteObjectLayoutObjectLayoutTabs(long objectLayoutId)
		throws PortalException {

		getService().deleteObjectLayoutObjectLayoutTabs(objectLayoutId);
	}

	/**
	 * Deletes the object layout tab with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectLayoutTabLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectLayoutTabId the primary key of the object layout tab
	 * @return the object layout tab that was removed
	 * @throws PortalException if a object layout tab with the primary key could not be found
	 */
	public static ObjectLayoutTab deleteObjectLayoutTab(long objectLayoutTabId)
		throws PortalException {

		return getService().deleteObjectLayoutTab(objectLayoutTabId);
	}

	/**
	 * Deletes the object layout tab from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectLayoutTabLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectLayoutTab the object layout tab
	 * @return the object layout tab that was removed
	 */
	public static ObjectLayoutTab deleteObjectLayoutTab(
		ObjectLayoutTab objectLayoutTab) {

		return getService().deleteObjectLayoutTab(objectLayoutTab);
	}

	public static void deleteObjectRelationshipObjectLayoutTabs(
			long objectRelationshipId)
		throws PortalException {

		getService().deleteObjectRelationshipObjectLayoutTabs(
			objectRelationshipId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectLayoutTabModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectLayoutTabModelImpl</code>.
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

	public static ObjectLayoutTab fetchObjectLayoutTab(long objectLayoutTabId) {
		return getService().fetchObjectLayoutTab(objectLayoutTabId);
	}

	/**
	 * Returns the object layout tab with the matching UUID and company.
	 *
	 * @param uuid the object layout tab's UUID
	 * @param companyId the primary key of the company
	 * @return the matching object layout tab, or <code>null</code> if a matching object layout tab could not be found
	 */
	public static ObjectLayoutTab fetchObjectLayoutTabByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().fetchObjectLayoutTabByUuidAndCompanyId(
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

	public static List<ObjectLayoutTab> getObjectLayoutObjectLayoutTabs(
		long objectLayoutId) {

		return getService().getObjectLayoutObjectLayoutTabs(objectLayoutId);
	}

	/**
	 * Returns the object layout tab with the primary key.
	 *
	 * @param objectLayoutTabId the primary key of the object layout tab
	 * @return the object layout tab
	 * @throws PortalException if a object layout tab with the primary key could not be found
	 */
	public static ObjectLayoutTab getObjectLayoutTab(long objectLayoutTabId)
		throws PortalException {

		return getService().getObjectLayoutTab(objectLayoutTabId);
	}

	/**
	 * Returns the object layout tab with the matching UUID and company.
	 *
	 * @param uuid the object layout tab's UUID
	 * @param companyId the primary key of the company
	 * @return the matching object layout tab
	 * @throws PortalException if a matching object layout tab could not be found
	 */
	public static ObjectLayoutTab getObjectLayoutTabByUuidAndCompanyId(
			String uuid, long companyId)
		throws PortalException {

		return getService().getObjectLayoutTabByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of all the object layout tabs.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectLayoutTabModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object layout tabs
	 * @param end the upper bound of the range of object layout tabs (not inclusive)
	 * @return the range of object layout tabs
	 */
	public static List<ObjectLayoutTab> getObjectLayoutTabs(
		int start, int end) {

		return getService().getObjectLayoutTabs(start, end);
	}

	/**
	 * Returns the number of object layout tabs.
	 *
	 * @return the number of object layout tabs
	 */
	public static int getObjectLayoutTabsCount() {
		return getService().getObjectLayoutTabsCount();
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

	public static void registerObjectLayoutTabScreenNavigationCategories(
		com.liferay.object.model.ObjectDefinition objectDefinition,
		List<ObjectLayoutTab> objectLayoutTabs) {

		getService().registerObjectLayoutTabScreenNavigationCategories(
			objectDefinition, objectLayoutTabs);
	}

	public static void unregisterObjectLayoutTabScreenNavigationCategories(
		com.liferay.object.model.ObjectDefinition objectDefinition) {

		getService().unregisterObjectLayoutTabScreenNavigationCategories(
			objectDefinition);
	}

	/**
	 * Updates the object layout tab in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectLayoutTabLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectLayoutTab the object layout tab
	 * @return the object layout tab that was updated
	 */
	public static ObjectLayoutTab updateObjectLayoutTab(
		ObjectLayoutTab objectLayoutTab) {

		return getService().updateObjectLayoutTab(objectLayoutTab);
	}

	public static ObjectLayoutTabLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<ObjectLayoutTabLocalService>
		_serviceSnapshot = new Snapshot<>(
			ObjectLayoutTabLocalServiceUtil.class,
			ObjectLayoutTabLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-1733747140