/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.expando.kernel.service;

import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link ExpandoTableLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see ExpandoTableLocalService
 * @generated
 */
public class ExpandoTableLocalServiceWrapper
	implements ExpandoTableLocalService,
			   ServiceWrapper<ExpandoTableLocalService> {

	public ExpandoTableLocalServiceWrapper() {
		this(null);
	}

	public ExpandoTableLocalServiceWrapper(
		ExpandoTableLocalService expandoTableLocalService) {

		_expandoTableLocalService = expandoTableLocalService;
	}

	@Override
	public ExpandoTable addDefaultTable(long companyId, long classNameId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoTableLocalService.addDefaultTable(
			companyId, classNameId);
	}

	@Override
	public ExpandoTable addDefaultTable(long companyId, String className)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoTableLocalService.addDefaultTable(companyId, className);
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
	@Override
	public ExpandoTable addExpandoTable(ExpandoTable expandoTable) {
		return _expandoTableLocalService.addExpandoTable(expandoTable);
	}

	@Override
	public ExpandoTable addTable(long companyId, long classNameId, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoTableLocalService.addTable(companyId, classNameId, name);
	}

	@Override
	public ExpandoTable addTable(long companyId, String className, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoTableLocalService.addTable(companyId, className, name);
	}

	/**
	 * Creates a new expando table with the primary key. Does not add the expando table to the database.
	 *
	 * @param tableId the primary key for the new expando table
	 * @return the new expando table
	 */
	@Override
	public ExpandoTable createExpandoTable(long tableId) {
		return _expandoTableLocalService.createExpandoTable(tableId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoTableLocalService.createPersistedModel(primaryKeyObj);
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
	@Override
	public ExpandoTable deleteExpandoTable(ExpandoTable expandoTable)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoTableLocalService.deleteExpandoTable(expandoTable);
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
	@Override
	public ExpandoTable deleteExpandoTable(long tableId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoTableLocalService.deleteExpandoTable(tableId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoTableLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public void deleteTable(ExpandoTable table)
		throws com.liferay.portal.kernel.exception.PortalException {

		_expandoTableLocalService.deleteTable(table);
	}

	@Override
	public void deleteTable(long tableId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_expandoTableLocalService.deleteTable(tableId);
	}

	@Override
	public void deleteTable(long companyId, long classNameId, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		_expandoTableLocalService.deleteTable(companyId, classNameId, name);
	}

	@Override
	public void deleteTable(long companyId, String className, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		_expandoTableLocalService.deleteTable(companyId, className, name);
	}

	@Override
	public void deleteTables(long companyId, long classNameId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_expandoTableLocalService.deleteTables(companyId, classNameId);
	}

	@Override
	public void deleteTables(long companyId, String className)
		throws com.liferay.portal.kernel.exception.PortalException {

		_expandoTableLocalService.deleteTables(companyId, className);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _expandoTableLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _expandoTableLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _expandoTableLocalService.dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _expandoTableLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _expandoTableLocalService.dynamicQuery(dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _expandoTableLocalService.dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _expandoTableLocalService.dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return _expandoTableLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public ExpandoTable fetchDefaultTable(long companyId, long classNameId) {
		return _expandoTableLocalService.fetchDefaultTable(
			companyId, classNameId);
	}

	@Override
	public ExpandoTable fetchDefaultTable(long companyId, String className) {
		return _expandoTableLocalService.fetchDefaultTable(
			companyId, className);
	}

	@Override
	public ExpandoTable fetchExpandoTable(long tableId) {
		return _expandoTableLocalService.fetchExpandoTable(tableId);
	}

	@Override
	public ExpandoTable fetchTable(
		long companyId, long classNameId, String name) {

		return _expandoTableLocalService.fetchTable(
			companyId, classNameId, name);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _expandoTableLocalService.getActionableDynamicQuery();
	}

	@Override
	public ExpandoTable getDefaultTable(long companyId, long classNameId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoTableLocalService.getDefaultTable(
			companyId, classNameId);
	}

	@Override
	public ExpandoTable getDefaultTable(long companyId, String className)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoTableLocalService.getDefaultTable(companyId, className);
	}

	/**
	 * Returns the expando table with the primary key.
	 *
	 * @param tableId the primary key of the expando table
	 * @return the expando table
	 * @throws PortalException if a expando table with the primary key could not be found
	 */
	@Override
	public ExpandoTable getExpandoTable(long tableId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoTableLocalService.getExpandoTable(tableId);
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
	@Override
	public java.util.List<ExpandoTable> getExpandoTables(int start, int end) {
		return _expandoTableLocalService.getExpandoTables(start, end);
	}

	/**
	 * Returns the number of expando tables.
	 *
	 * @return the number of expando tables
	 */
	@Override
	public int getExpandoTablesCount() {
		return _expandoTableLocalService.getExpandoTablesCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _expandoTableLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _expandoTableLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoTableLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public ExpandoTable getTable(long tableId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoTableLocalService.getTable(tableId);
	}

	@Override
	public ExpandoTable getTable(long companyId, long classNameId, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoTableLocalService.getTable(companyId, classNameId, name);
	}

	@Override
	public ExpandoTable getTable(long companyId, String className, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoTableLocalService.getTable(companyId, className, name);
	}

	@Override
	public java.util.List<ExpandoTable> getTables(
		long companyId, long classNameId) {

		return _expandoTableLocalService.getTables(companyId, classNameId);
	}

	@Override
	public java.util.List<ExpandoTable> getTables(
		long companyId, String className) {

		return _expandoTableLocalService.getTables(companyId, className);
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
	@Override
	public ExpandoTable updateExpandoTable(ExpandoTable expandoTable) {
		return _expandoTableLocalService.updateExpandoTable(expandoTable);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _expandoTableLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<ExpandoTable> getCTPersistence() {
		return _expandoTableLocalService.getCTPersistence();
	}

	@Override
	public Class<ExpandoTable> getModelClass() {
		return _expandoTableLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<ExpandoTable>, R, E>
				updateUnsafeFunction)
		throws E {

		return _expandoTableLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public ExpandoTableLocalService getWrappedService() {
		return _expandoTableLocalService;
	}

	@Override
	public void setWrappedService(
		ExpandoTableLocalService expandoTableLocalService) {

		_expandoTableLocalService = expandoTableLocalService;
	}

	private ExpandoTableLocalService _expandoTableLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:137269797