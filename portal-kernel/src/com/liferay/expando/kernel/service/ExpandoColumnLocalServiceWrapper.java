/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.expando.kernel.service;

import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link ExpandoColumnLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see ExpandoColumnLocalService
 * @generated
 */
public class ExpandoColumnLocalServiceWrapper
	implements ExpandoColumnLocalService,
			   ServiceWrapper<ExpandoColumnLocalService> {

	public ExpandoColumnLocalServiceWrapper() {
		this(null);
	}

	public ExpandoColumnLocalServiceWrapper(
		ExpandoColumnLocalService expandoColumnLocalService) {

		_expandoColumnLocalService = expandoColumnLocalService;
	}

	@Override
	public ExpandoColumn addColumn(long tableId, String name, int type)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoColumnLocalService.addColumn(tableId, name, type);
	}

	@Override
	public ExpandoColumn addColumn(
			long tableId, String name, int type, Object defaultData)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoColumnLocalService.addColumn(
			tableId, name, type, defaultData);
	}

	/**
	 * Adds the expando column to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ExpandoColumnLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param expandoColumn the expando column
	 * @return the expando column that was added
	 */
	@Override
	public ExpandoColumn addExpandoColumn(ExpandoColumn expandoColumn) {
		return _expandoColumnLocalService.addExpandoColumn(expandoColumn);
	}

	/**
	 * Creates a new expando column with the primary key. Does not add the expando column to the database.
	 *
	 * @param columnId the primary key for the new expando column
	 * @return the new expando column
	 */
	@Override
	public ExpandoColumn createExpandoColumn(long columnId) {
		return _expandoColumnLocalService.createExpandoColumn(columnId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoColumnLocalService.createPersistedModel(primaryKeyObj);
	}

	@Override
	public void deleteColumn(ExpandoColumn column)
		throws com.liferay.portal.kernel.exception.PortalException {

		_expandoColumnLocalService.deleteColumn(column);
	}

	@Override
	public void deleteColumn(long columnId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_expandoColumnLocalService.deleteColumn(columnId);
	}

	@Override
	public void deleteColumn(
			long companyId, long classNameId, String tableName, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		_expandoColumnLocalService.deleteColumn(
			companyId, classNameId, tableName, name);
	}

	@Override
	public void deleteColumn(long tableId, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		_expandoColumnLocalService.deleteColumn(tableId, name);
	}

	@Override
	public void deleteColumn(
			long companyId, String className, String tableName, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		_expandoColumnLocalService.deleteColumn(
			companyId, className, tableName, name);
	}

	@Override
	public void deleteColumns(long tableId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_expandoColumnLocalService.deleteColumns(tableId);
	}

	/**
	 * Deletes the expando column from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ExpandoColumnLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param expandoColumn the expando column
	 * @return the expando column that was removed
	 */
	@Override
	public ExpandoColumn deleteExpandoColumn(ExpandoColumn expandoColumn) {
		return _expandoColumnLocalService.deleteExpandoColumn(expandoColumn);
	}

	/**
	 * Deletes the expando column with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ExpandoColumnLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param columnId the primary key of the expando column
	 * @return the expando column that was removed
	 * @throws PortalException if a expando column with the primary key could not be found
	 */
	@Override
	public ExpandoColumn deleteExpandoColumn(long columnId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoColumnLocalService.deleteExpandoColumn(columnId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoColumnLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _expandoColumnLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _expandoColumnLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _expandoColumnLocalService.dynamicQuery();
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

		return _expandoColumnLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.expando.model.impl.ExpandoColumnModelImpl</code>.
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

		return _expandoColumnLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.expando.model.impl.ExpandoColumnModelImpl</code>.
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

		return _expandoColumnLocalService.dynamicQuery(
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

		return _expandoColumnLocalService.dynamicQueryCount(dynamicQuery);
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

		return _expandoColumnLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public ExpandoColumn fetchColumn(
		long companyId, long classNameId, String tableName, String name) {

		return _expandoColumnLocalService.fetchColumn(
			companyId, classNameId, tableName, name);
	}

	@Override
	public ExpandoColumn fetchColumn(long tableId, String name) {
		return _expandoColumnLocalService.fetchColumn(tableId, name);
	}

	@Override
	public ExpandoColumn fetchExpandoColumn(long columnId) {
		return _expandoColumnLocalService.fetchExpandoColumn(columnId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _expandoColumnLocalService.getActionableDynamicQuery();
	}

	@Override
	public ExpandoColumn getColumn(long columnId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoColumnLocalService.getColumn(columnId);
	}

	@Override
	public ExpandoColumn getColumn(
			long companyId, long classNameId, String tableName, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoColumnLocalService.getColumn(
			companyId, classNameId, tableName, name);
	}

	@Override
	public ExpandoColumn getColumn(long tableId, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoColumnLocalService.getColumn(tableId, name);
	}

	@Override
	public ExpandoColumn getColumn(
		long companyId, String className, String tableName, String name) {

		return _expandoColumnLocalService.getColumn(
			companyId, className, tableName, name);
	}

	@Override
	public java.util.List<ExpandoColumn> getColumns(long tableId) {
		return _expandoColumnLocalService.getColumns(tableId);
	}

	@Override
	public java.util.List<ExpandoColumn> getColumns(
		long companyId, long classNameId, String tableName) {

		return _expandoColumnLocalService.getColumns(
			companyId, classNameId, tableName);
	}

	@Override
	public java.util.List<ExpandoColumn> getColumns(
		long companyId, String className, String tableName) {

		return _expandoColumnLocalService.getColumns(
			companyId, className, tableName);
	}

	@Override
	public int getColumnsCount(long tableId) {
		return _expandoColumnLocalService.getColumnsCount(tableId);
	}

	@Override
	public ExpandoColumn getDefaultTableColumn(
		long companyId, long classNameId, String name) {

		return _expandoColumnLocalService.getDefaultTableColumn(
			companyId, classNameId, name);
	}

	@Override
	public ExpandoColumn getDefaultTableColumn(
		long companyId, String className, String name) {

		return _expandoColumnLocalService.getDefaultTableColumn(
			companyId, className, name);
	}

	@Override
	public java.util.List<ExpandoColumn> getDefaultTableColumns(
		long companyId, long classNameId) {

		return _expandoColumnLocalService.getDefaultTableColumns(
			companyId, classNameId);
	}

	@Override
	public java.util.List<ExpandoColumn> getDefaultTableColumns(
		long companyId, String className) {

		return _expandoColumnLocalService.getDefaultTableColumns(
			companyId, className);
	}

	/**
	 * Returns the expando column with the primary key.
	 *
	 * @param columnId the primary key of the expando column
	 * @return the expando column
	 * @throws PortalException if a expando column with the primary key could not be found
	 */
	@Override
	public ExpandoColumn getExpandoColumn(long columnId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoColumnLocalService.getExpandoColumn(columnId);
	}

	/**
	 * Returns a range of all the expando columns.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.expando.model.impl.ExpandoColumnModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of expando columns
	 * @param end the upper bound of the range of expando columns (not inclusive)
	 * @return the range of expando columns
	 */
	@Override
	public java.util.List<ExpandoColumn> getExpandoColumns(int start, int end) {
		return _expandoColumnLocalService.getExpandoColumns(start, end);
	}

	/**
	 * Returns the number of expando columns.
	 *
	 * @return the number of expando columns
	 */
	@Override
	public int getExpandoColumnsCount() {
		return _expandoColumnLocalService.getExpandoColumnsCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _expandoColumnLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _expandoColumnLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoColumnLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public ExpandoColumn updateColumn(long columnId, String name, int type)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoColumnLocalService.updateColumn(columnId, name, type);
	}

	@Override
	public ExpandoColumn updateColumn(
			long columnId, String name, int type, Object defaultData)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoColumnLocalService.updateColumn(
			columnId, name, type, defaultData);
	}

	/**
	 * Updates the expando column in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ExpandoColumnLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param expandoColumn the expando column
	 * @return the expando column that was updated
	 */
	@Override
	public ExpandoColumn updateExpandoColumn(ExpandoColumn expandoColumn) {
		return _expandoColumnLocalService.updateExpandoColumn(expandoColumn);
	}

	@Override
	public ExpandoColumn updateTypeSettings(long columnId, String typeSettings)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoColumnLocalService.updateTypeSettings(
			columnId, typeSettings);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _expandoColumnLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<ExpandoColumn> getCTPersistence() {
		return _expandoColumnLocalService.getCTPersistence();
	}

	@Override
	public Class<ExpandoColumn> getModelClass() {
		return _expandoColumnLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<ExpandoColumn>, R, E>
				updateUnsafeFunction)
		throws E {

		return _expandoColumnLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public ExpandoColumnLocalService getWrappedService() {
		return _expandoColumnLocalService;
	}

	@Override
	public void setWrappedService(
		ExpandoColumnLocalService expandoColumnLocalService) {

		_expandoColumnLocalService = expandoColumnLocalService;
	}

	private ExpandoColumnLocalService _expandoColumnLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1107485510