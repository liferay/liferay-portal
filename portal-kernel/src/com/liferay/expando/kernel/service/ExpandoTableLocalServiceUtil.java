/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.expando.kernel.service;

import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for ExpandoTable. This utility wraps
 * <code>com.liferay.portlet.expando.service.impl.ExpandoTableLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see ExpandoTableLocalService
 * @generated
 */
public class ExpandoTableLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portlet.expando.service.impl.ExpandoTableLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static ExpandoTable addDefaultTable(long companyId, long classNameId)
		throws PortalException {

		return getService().addDefaultTable(companyId, classNameId);
	}

	public static ExpandoTable addDefaultTable(long companyId, String className)
		throws PortalException {

		return getService().addDefaultTable(companyId, className);
	}

	/**
	 * Adds the expando table to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ExpandoTableLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param expandoTable the expando table
	 * @return the expando table that was added
	 */
	public static ExpandoTable addExpandoTable(ExpandoTable expandoTable) {
		return getService().addExpandoTable(expandoTable);
	}

	public static ExpandoTable addTable(
			long companyId, long classNameId, String name)
		throws PortalException {

		return getService().addTable(companyId, classNameId, name);
	}

	public static ExpandoTable addTable(
			long companyId, String className, String name)
		throws PortalException {

		return getService().addTable(companyId, className, name);
	}

	/**
	 * Creates a new expando table with the primary key. Does not add the expando table to the database.
	 *
	 * @param tableId the primary key for the new expando table
	 * @return the new expando table
	 */
	public static ExpandoTable createExpandoTable(long tableId) {
		return getService().createExpandoTable(tableId);
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
	 * Deletes the expando table from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ExpandoTableLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param expandoTable the expando table
	 * @return the expando table that was removed
	 * @throws PortalException
	 */
	public static ExpandoTable deleteExpandoTable(ExpandoTable expandoTable)
		throws PortalException {

		return getService().deleteExpandoTable(expandoTable);
	}

	/**
	 * Deletes the expando table with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ExpandoTableLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param tableId the primary key of the expando table
	 * @return the expando table that was removed
	 * @throws PortalException if a expando table with the primary key could not be found
	 */
	public static ExpandoTable deleteExpandoTable(long tableId)
		throws PortalException {

		return getService().deleteExpandoTable(tableId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	public static void deleteTable(ExpandoTable table) throws PortalException {
		getService().deleteTable(table);
	}

	public static void deleteTable(long tableId) throws PortalException {
		getService().deleteTable(tableId);
	}

	public static void deleteTable(
			long companyId, long classNameId, String name)
		throws PortalException {

		getService().deleteTable(companyId, classNameId, name);
	}

	public static void deleteTable(
			long companyId, String className, String name)
		throws PortalException {

		getService().deleteTable(companyId, className, name);
	}

	public static void deleteTables(long companyId, long classNameId)
		throws PortalException {

		getService().deleteTables(companyId, classNameId);
	}

	public static void deleteTables(long companyId, String className)
		throws PortalException {

		getService().deleteTables(companyId, className);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.expando.model.impl.ExpandoTableModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.expando.model.impl.ExpandoTableModelImpl</code>.
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

	public static ExpandoTable fetchDefaultTable(
		long companyId, long classNameId) {

		return getService().fetchDefaultTable(companyId, classNameId);
	}

	public static ExpandoTable fetchDefaultTable(
		long companyId, String className) {

		return getService().fetchDefaultTable(companyId, className);
	}

	public static ExpandoTable fetchExpandoTable(long tableId) {
		return getService().fetchExpandoTable(tableId);
	}

	public static ExpandoTable fetchTable(
		long companyId, long classNameId, String name) {

		return getService().fetchTable(companyId, classNameId, name);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static ExpandoTable getDefaultTable(long companyId, long classNameId)
		throws PortalException {

		return getService().getDefaultTable(companyId, classNameId);
	}

	public static ExpandoTable getDefaultTable(long companyId, String className)
		throws PortalException {

		return getService().getDefaultTable(companyId, className);
	}

	/**
	 * Returns the expando table with the primary key.
	 *
	 * @param tableId the primary key of the expando table
	 * @return the expando table
	 * @throws PortalException if a expando table with the primary key could not be found
	 */
	public static ExpandoTable getExpandoTable(long tableId)
		throws PortalException {

		return getService().getExpandoTable(tableId);
	}

	/**
	 * Returns a range of all the expando tables.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.expando.model.impl.ExpandoTableModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of expando tables
	 * @param end the upper bound of the range of expando tables (not inclusive)
	 * @return the range of expando tables
	 */
	public static List<ExpandoTable> getExpandoTables(int start, int end) {
		return getService().getExpandoTables(start, end);
	}

	/**
	 * Returns the number of expando tables.
	 *
	 * @return the number of expando tables
	 */
	public static int getExpandoTablesCount() {
		return getService().getExpandoTablesCount();
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

	public static ExpandoTable getTable(long tableId) throws PortalException {
		return getService().getTable(tableId);
	}

	public static ExpandoTable getTable(
			long companyId, long classNameId, String name)
		throws PortalException {

		return getService().getTable(companyId, classNameId, name);
	}

	public static ExpandoTable getTable(
			long companyId, String className, String name)
		throws PortalException {

		return getService().getTable(companyId, className, name);
	}

	public static List<ExpandoTable> getTables(
		long companyId, long classNameId) {

		return getService().getTables(companyId, classNameId);
	}

	public static List<ExpandoTable> getTables(
		long companyId, String className) {

		return getService().getTables(companyId, className);
	}

	/**
	 * Updates the expando table in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ExpandoTableLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param expandoTable the expando table
	 * @return the expando table that was updated
	 */
	public static ExpandoTable updateExpandoTable(ExpandoTable expandoTable) {
		return getService().updateExpandoTable(expandoTable);
	}

	public static ExpandoTableLocalService getService() {
		return _service;
	}

	public static void setService(ExpandoTableLocalService service) {
		_service = service;
	}

	private static volatile ExpandoTableLocalService _service;

}
// LIFERAY-SERVICE-BUILDER-HASH:1664331316