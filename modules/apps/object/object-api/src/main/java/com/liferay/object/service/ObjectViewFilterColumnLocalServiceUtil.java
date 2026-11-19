/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service;

import com.liferay.object.model.ObjectViewFilterColumn;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for ObjectViewFilterColumn. This utility wraps
 * <code>com.liferay.object.service.impl.ObjectViewFilterColumnLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Marco Leo
 * @see ObjectViewFilterColumnLocalService
 * @generated
 */
public class ObjectViewFilterColumnLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.object.service.impl.ObjectViewFilterColumnLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the object view filter column to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectViewFilterColumnLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectViewFilterColumn the object view filter column
	 * @return the object view filter column that was added
	 */
	public static ObjectViewFilterColumn addObjectViewFilterColumn(
		ObjectViewFilterColumn objectViewFilterColumn) {

		return getService().addObjectViewFilterColumn(objectViewFilterColumn);
	}

	public static List<ObjectViewFilterColumn> addObjectViewFilterColumns(
			com.liferay.portal.kernel.model.User user,
			com.liferay.object.model.ObjectView objectView,
			List<ObjectViewFilterColumn> objectViewFilterColumns)
		throws PortalException {

		return getService().addObjectViewFilterColumns(
			user, objectView, objectViewFilterColumns);
	}

	/**
	 * Creates a new object view filter column with the primary key. Does not add the object view filter column to the database.
	 *
	 * @param objectViewFilterColumnId the primary key for the new object view filter column
	 * @return the new object view filter column
	 */
	public static ObjectViewFilterColumn createObjectViewFilterColumn(
		long objectViewFilterColumnId) {

		return getService().createObjectViewFilterColumn(
			objectViewFilterColumnId);
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
	 * Deletes the object view filter column with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectViewFilterColumnLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectViewFilterColumnId the primary key of the object view filter column
	 * @return the object view filter column that was removed
	 * @throws PortalException if a object view filter column with the primary key could not be found
	 */
	public static ObjectViewFilterColumn deleteObjectViewFilterColumn(
			long objectViewFilterColumnId)
		throws PortalException {

		return getService().deleteObjectViewFilterColumn(
			objectViewFilterColumnId);
	}

	/**
	 * Deletes the object view filter column from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectViewFilterColumnLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectViewFilterColumn the object view filter column
	 * @return the object view filter column that was removed
	 */
	public static ObjectViewFilterColumn deleteObjectViewFilterColumn(
		ObjectViewFilterColumn objectViewFilterColumn) {

		return getService().deleteObjectViewFilterColumn(
			objectViewFilterColumn);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectViewFilterColumnModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectViewFilterColumnModelImpl</code>.
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

	public static ObjectViewFilterColumn fetchObjectViewFilterColumn(
		long objectViewFilterColumnId) {

		return getService().fetchObjectViewFilterColumn(
			objectViewFilterColumnId);
	}

	/**
	 * Returns the object view filter column with the matching UUID and company.
	 *
	 * @param uuid the object view filter column's UUID
	 * @param companyId the primary key of the company
	 * @return the matching object view filter column, or <code>null</code> if a matching object view filter column could not be found
	 */
	public static ObjectViewFilterColumn
		fetchObjectViewFilterColumnByUuidAndCompanyId(
			String uuid, long companyId) {

		return getService().fetchObjectViewFilterColumnByUuidAndCompanyId(
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
	 * Returns the object view filter column with the primary key.
	 *
	 * @param objectViewFilterColumnId the primary key of the object view filter column
	 * @return the object view filter column
	 * @throws PortalException if a object view filter column with the primary key could not be found
	 */
	public static ObjectViewFilterColumn getObjectViewFilterColumn(
			long objectViewFilterColumnId)
		throws PortalException {

		return getService().getObjectViewFilterColumn(objectViewFilterColumnId);
	}

	/**
	 * Returns the object view filter column with the matching UUID and company.
	 *
	 * @param uuid the object view filter column's UUID
	 * @param companyId the primary key of the company
	 * @return the matching object view filter column
	 * @throws PortalException if a matching object view filter column could not be found
	 */
	public static ObjectViewFilterColumn
			getObjectViewFilterColumnByUuidAndCompanyId(
				String uuid, long companyId)
		throws PortalException {

		return getService().getObjectViewFilterColumnByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of all the object view filter columns.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectViewFilterColumnModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object view filter columns
	 * @param end the upper bound of the range of object view filter columns (not inclusive)
	 * @return the range of object view filter columns
	 */
	public static List<ObjectViewFilterColumn> getObjectViewFilterColumns(
		int start, int end) {

		return getService().getObjectViewFilterColumns(start, end);
	}

	/**
	 * Returns the number of object view filter columns.
	 *
	 * @return the number of object view filter columns
	 */
	public static int getObjectViewFilterColumnsCount() {
		return getService().getObjectViewFilterColumnsCount();
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
	 * Updates the object view filter column in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectViewFilterColumnLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectViewFilterColumn the object view filter column
	 * @return the object view filter column that was updated
	 */
	public static ObjectViewFilterColumn updateObjectViewFilterColumn(
		ObjectViewFilterColumn objectViewFilterColumn) {

		return getService().updateObjectViewFilterColumn(
			objectViewFilterColumn);
	}

	public static ObjectViewFilterColumnLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<ObjectViewFilterColumnLocalService>
		_serviceSnapshot = new Snapshot<>(
			ObjectViewFilterColumnLocalServiceUtil.class,
			ObjectViewFilterColumnLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-1450709922