/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.expando.kernel.service;

import com.liferay.expando.kernel.model.ExpandoRow;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for ExpandoRow. This utility wraps
 * <code>com.liferay.portlet.expando.service.impl.ExpandoRowLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see ExpandoRowLocalService
 * @generated
 */
public class ExpandoRowLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portlet.expando.service.impl.ExpandoRowLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the expando row to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ExpandoRowLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param expandoRow the expando row
	 * @return the expando row that was added
	 */
	public static ExpandoRow addExpandoRow(ExpandoRow expandoRow) {
		return getService().addExpandoRow(expandoRow);
	}

	public static ExpandoRow addRow(long tableId, long classPK)
		throws PortalException {

		return getService().addRow(tableId, classPK);
	}

	/**
	 * Creates a new expando row with the primary key. Does not add the expando row to the database.
	 *
	 * @param rowId the primary key for the new expando row
	 * @return the new expando row
	 */
	public static ExpandoRow createExpandoRow(long rowId) {
		return getService().createExpandoRow(rowId);
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
	 * Deletes the expando row from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ExpandoRowLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param expandoRow the expando row
	 * @return the expando row that was removed
	 */
	public static ExpandoRow deleteExpandoRow(ExpandoRow expandoRow) {
		return getService().deleteExpandoRow(expandoRow);
	}

	/**
	 * Deletes the expando row with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ExpandoRowLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param rowId the primary key of the expando row
	 * @return the expando row that was removed
	 * @throws PortalException if a expando row with the primary key could not be found
	 */
	public static ExpandoRow deleteExpandoRow(long rowId)
		throws PortalException {

		return getService().deleteExpandoRow(rowId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	public static void deleteRow(ExpandoRow row) {
		getService().deleteRow(row);
	}

	public static void deleteRow(long rowId) throws PortalException {
		getService().deleteRow(rowId);
	}

	public static void deleteRow(long tableId, long classPK)
		throws PortalException {

		getService().deleteRow(tableId, classPK);
	}

	public static void deleteRow(
			long companyId, long classNameId, String tableName, long classPK)
		throws PortalException {

		getService().deleteRow(companyId, classNameId, tableName, classPK);
	}

	public static void deleteRow(
			long companyId, String className, String tableName, long classPK)
		throws PortalException {

		getService().deleteRow(companyId, className, tableName, classPK);
	}

	public static void deleteRows(long classPK) {
		getService().deleteRows(classPK);
	}

	public static void deleteRows(
		long companyId, long classNameId, long classPK) {

		getService().deleteRows(companyId, classNameId, classPK);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.expando.model.impl.ExpandoRowModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.expando.model.impl.ExpandoRowModelImpl</code>.
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

	public static ExpandoRow fetchExpandoRow(long rowId) {
		return getService().fetchExpandoRow(rowId);
	}

	public static ExpandoRow fetchRow(long tableId, long classPK) {
		return getService().fetchRow(tableId, classPK);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static List<ExpandoRow> getDefaultTableRows(
		long companyId, long classNameId, int start, int end) {

		return getService().getDefaultTableRows(
			companyId, classNameId, start, end);
	}

	public static List<ExpandoRow> getDefaultTableRows(
		long companyId, String className, int start, int end) {

		return getService().getDefaultTableRows(
			companyId, className, start, end);
	}

	public static int getDefaultTableRowsCount(
		long companyId, long classNameId) {

		return getService().getDefaultTableRowsCount(companyId, classNameId);
	}

	public static int getDefaultTableRowsCount(
		long companyId, String className) {

		return getService().getDefaultTableRowsCount(companyId, className);
	}

	/**
	 * Returns the expando row with the primary key.
	 *
	 * @param rowId the primary key of the expando row
	 * @return the expando row
	 * @throws PortalException if a expando row with the primary key could not be found
	 */
	public static ExpandoRow getExpandoRow(long rowId) throws PortalException {
		return getService().getExpandoRow(rowId);
	}

	/**
	 * Returns a range of all the expando rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.expando.model.impl.ExpandoRowModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of expando rows
	 * @param end the upper bound of the range of expando rows (not inclusive)
	 * @return the range of expando rows
	 */
	public static List<ExpandoRow> getExpandoRows(int start, int end) {
		return getService().getExpandoRows(start, end);
	}

	/**
	 * Returns the number of expando rows.
	 *
	 * @return the number of expando rows
	 */
	public static int getExpandoRowsCount() {
		return getService().getExpandoRowsCount();
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

	public static ExpandoRow getRow(long rowId) throws PortalException {
		return getService().getRow(rowId);
	}

	public static ExpandoRow getRow(long tableId, long classPK)
		throws PortalException {

		return getService().getRow(tableId, classPK);
	}

	public static ExpandoRow getRow(
			long companyId, long classNameId, String tableName, long classPK)
		throws PortalException {

		return getService().getRow(companyId, classNameId, tableName, classPK);
	}

	public static ExpandoRow getRow(
			long companyId, String className, String tableName, long classPK)
		throws PortalException {

		return getService().getRow(companyId, className, tableName, classPK);
	}

	public static List<ExpandoRow> getRows(long tableId, int start, int end) {
		return getService().getRows(tableId, start, end);
	}

	public static List<ExpandoRow> getRows(
		long companyId, long classNameId, String tableName, int start,
		int end) {

		return getService().getRows(
			companyId, classNameId, tableName, start, end);
	}

	public static List<ExpandoRow> getRows(
		long companyId, String className, String tableName, int start,
		int end) {

		return getService().getRows(
			companyId, className, tableName, start, end);
	}

	public static int getRowsCount(long tableId) {
		return getService().getRowsCount(tableId);
	}

	public static int getRowsCount(
		long companyId, long classNameId, String tableName) {

		return getService().getRowsCount(companyId, classNameId, tableName);
	}

	public static int getRowsCount(
		long companyId, String className, String tableName) {

		return getService().getRowsCount(companyId, className, tableName);
	}

	/**
	 * Updates the expando row in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ExpandoRowLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param expandoRow the expando row
	 * @return the expando row that was updated
	 */
	public static ExpandoRow updateExpandoRow(ExpandoRow expandoRow) {
		return getService().updateExpandoRow(expandoRow);
	}

	public static ExpandoRowLocalService getService() {
		return _service;
	}

	public static void setService(ExpandoRowLocalService service) {
		_service = service;
	}

	private static volatile ExpandoRowLocalService _service;

}
// LIFERAY-SERVICE-BUILDER-HASH:-156443079