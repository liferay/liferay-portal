/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.expando.kernel.service;

import com.liferay.expando.kernel.model.ExpandoRow;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link ExpandoRowLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see ExpandoRowLocalService
 * @generated
 */
public class ExpandoRowLocalServiceWrapper
	implements ExpandoRowLocalService, ServiceWrapper<ExpandoRowLocalService> {

	public ExpandoRowLocalServiceWrapper() {
		this(null);
	}

	public ExpandoRowLocalServiceWrapper(
		ExpandoRowLocalService expandoRowLocalService) {

		_expandoRowLocalService = expandoRowLocalService;
	}

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
	@Override
	public ExpandoRow addExpandoRow(ExpandoRow expandoRow) {
		return _expandoRowLocalService.addExpandoRow(expandoRow);
	}

	@Override
	public ExpandoRow addRow(long tableId, long classPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoRowLocalService.addRow(tableId, classPK);
	}

	/**
	 * Creates a new expando row with the primary key. Does not add the expando row to the database.
	 *
	 * @param rowId the primary key for the new expando row
	 * @return the new expando row
	 */
	@Override
	public ExpandoRow createExpandoRow(long rowId) {
		return _expandoRowLocalService.createExpandoRow(rowId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoRowLocalService.createPersistedModel(primaryKeyObj);
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
	@Override
	public ExpandoRow deleteExpandoRow(ExpandoRow expandoRow) {
		return _expandoRowLocalService.deleteExpandoRow(expandoRow);
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
	@Override
	public ExpandoRow deleteExpandoRow(long rowId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoRowLocalService.deleteExpandoRow(rowId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoRowLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public void deleteRow(ExpandoRow row) {
		_expandoRowLocalService.deleteRow(row);
	}

	@Override
	public void deleteRow(long rowId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_expandoRowLocalService.deleteRow(rowId);
	}

	@Override
	public void deleteRow(long tableId, long classPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		_expandoRowLocalService.deleteRow(tableId, classPK);
	}

	@Override
	public void deleteRow(
			long companyId, long classNameId, String tableName, long classPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		_expandoRowLocalService.deleteRow(
			companyId, classNameId, tableName, classPK);
	}

	@Override
	public void deleteRow(
			long companyId, String className, String tableName, long classPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		_expandoRowLocalService.deleteRow(
			companyId, className, tableName, classPK);
	}

	@Override
	public void deleteRows(long classPK) {
		_expandoRowLocalService.deleteRows(classPK);
	}

	@Override
	public void deleteRows(long companyId, long classNameId, long classPK) {
		_expandoRowLocalService.deleteRows(companyId, classNameId, classPK);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _expandoRowLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _expandoRowLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _expandoRowLocalService.dynamicQuery();
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

		return _expandoRowLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _expandoRowLocalService.dynamicQuery(dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _expandoRowLocalService.dynamicQuery(
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

		return _expandoRowLocalService.dynamicQueryCount(dynamicQuery);
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

		return _expandoRowLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public ExpandoRow fetchExpandoRow(long rowId) {
		return _expandoRowLocalService.fetchExpandoRow(rowId);
	}

	@Override
	public ExpandoRow fetchRow(long tableId, long classPK) {
		return _expandoRowLocalService.fetchRow(tableId, classPK);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _expandoRowLocalService.getActionableDynamicQuery();
	}

	@Override
	public java.util.List<ExpandoRow> getDefaultTableRows(
		long companyId, long classNameId, int start, int end) {

		return _expandoRowLocalService.getDefaultTableRows(
			companyId, classNameId, start, end);
	}

	@Override
	public java.util.List<ExpandoRow> getDefaultTableRows(
		long companyId, String className, int start, int end) {

		return _expandoRowLocalService.getDefaultTableRows(
			companyId, className, start, end);
	}

	@Override
	public int getDefaultTableRowsCount(long companyId, long classNameId) {
		return _expandoRowLocalService.getDefaultTableRowsCount(
			companyId, classNameId);
	}

	@Override
	public int getDefaultTableRowsCount(long companyId, String className) {
		return _expandoRowLocalService.getDefaultTableRowsCount(
			companyId, className);
	}

	/**
	 * Returns the expando row with the primary key.
	 *
	 * @param rowId the primary key of the expando row
	 * @return the expando row
	 * @throws PortalException if a expando row with the primary key could not be found
	 */
	@Override
	public ExpandoRow getExpandoRow(long rowId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoRowLocalService.getExpandoRow(rowId);
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
	@Override
	public java.util.List<ExpandoRow> getExpandoRows(int start, int end) {
		return _expandoRowLocalService.getExpandoRows(start, end);
	}

	/**
	 * Returns the number of expando rows.
	 *
	 * @return the number of expando rows
	 */
	@Override
	public int getExpandoRowsCount() {
		return _expandoRowLocalService.getExpandoRowsCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _expandoRowLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _expandoRowLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoRowLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public ExpandoRow getRow(long rowId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoRowLocalService.getRow(rowId);
	}

	@Override
	public ExpandoRow getRow(long tableId, long classPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoRowLocalService.getRow(tableId, classPK);
	}

	@Override
	public ExpandoRow getRow(
			long companyId, long classNameId, String tableName, long classPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoRowLocalService.getRow(
			companyId, classNameId, tableName, classPK);
	}

	@Override
	public ExpandoRow getRow(
			long companyId, String className, String tableName, long classPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoRowLocalService.getRow(
			companyId, className, tableName, classPK);
	}

	@Override
	public java.util.List<ExpandoRow> getRows(
		long tableId, int start, int end) {

		return _expandoRowLocalService.getRows(tableId, start, end);
	}

	@Override
	public java.util.List<ExpandoRow> getRows(
		long companyId, long classNameId, String tableName, int start,
		int end) {

		return _expandoRowLocalService.getRows(
			companyId, classNameId, tableName, start, end);
	}

	@Override
	public java.util.List<ExpandoRow> getRows(
		long companyId, String className, String tableName, int start,
		int end) {

		return _expandoRowLocalService.getRows(
			companyId, className, tableName, start, end);
	}

	@Override
	public int getRowsCount(long tableId) {
		return _expandoRowLocalService.getRowsCount(tableId);
	}

	@Override
	public int getRowsCount(
		long companyId, long classNameId, String tableName) {

		return _expandoRowLocalService.getRowsCount(
			companyId, classNameId, tableName);
	}

	@Override
	public int getRowsCount(
		long companyId, String className, String tableName) {

		return _expandoRowLocalService.getRowsCount(
			companyId, className, tableName);
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
	@Override
	public ExpandoRow updateExpandoRow(ExpandoRow expandoRow) {
		return _expandoRowLocalService.updateExpandoRow(expandoRow);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _expandoRowLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<ExpandoRow> getCTPersistence() {
		return _expandoRowLocalService.getCTPersistence();
	}

	@Override
	public Class<ExpandoRow> getModelClass() {
		return _expandoRowLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<ExpandoRow>, R, E>
				updateUnsafeFunction)
		throws E {

		return _expandoRowLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public ExpandoRowLocalService getWrappedService() {
		return _expandoRowLocalService;
	}

	@Override
	public void setWrappedService(
		ExpandoRowLocalService expandoRowLocalService) {

		_expandoRowLocalService = expandoRowLocalService;
	}

	private ExpandoRowLocalService _expandoRowLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1252422941