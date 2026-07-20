/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.expando.kernel.service;

import com.liferay.expando.kernel.model.ExpandoValue;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link ExpandoValueLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see ExpandoValueLocalService
 * @generated
 */
public class ExpandoValueLocalServiceWrapper
	implements ExpandoValueLocalService,
			   ServiceWrapper<ExpandoValueLocalService> {

	public ExpandoValueLocalServiceWrapper() {
		this(null);
	}

	public ExpandoValueLocalServiceWrapper(
		ExpandoValueLocalService expandoValueLocalService) {

		_expandoValueLocalService = expandoValueLocalService;
	}

	/**
	 * Adds the expando value to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ExpandoValueLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param expandoValue the expando value
	 * @return the expando value that was added
	 */
	@Override
	public ExpandoValue addExpandoValue(ExpandoValue expandoValue) {
		return _expandoValueLocalService.addExpandoValue(expandoValue);
	}

	@Override
	public ExpandoValue addValue(
			long classNameId, long tableId, long columnId, long classPK,
			String data)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoValueLocalService.addValue(
			classNameId, tableId, columnId, classPK, data);
	}

	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, boolean data)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoValueLocalService.addValue(
			companyId, className, tableName, columnName, classPK, data);
	}

	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, boolean[] data)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoValueLocalService.addValue(
			companyId, className, tableName, columnName, classPK, data);
	}

	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, java.util.Date data)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoValueLocalService.addValue(
			companyId, className, tableName, columnName, classPK, data);
	}

	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, java.util.Date[] data)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoValueLocalService.addValue(
			companyId, className, tableName, columnName, classPK, data);
	}

	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, double data)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoValueLocalService.addValue(
			companyId, className, tableName, columnName, classPK, data);
	}

	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, double[] data)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoValueLocalService.addValue(
			companyId, className, tableName, columnName, classPK, data);
	}

	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, float data)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoValueLocalService.addValue(
			companyId, className, tableName, columnName, classPK, data);
	}

	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, float[] data)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoValueLocalService.addValue(
			companyId, className, tableName, columnName, classPK, data);
	}

	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, int data)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoValueLocalService.addValue(
			companyId, className, tableName, columnName, classPK, data);
	}

	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, int[] data)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoValueLocalService.addValue(
			companyId, className, tableName, columnName, classPK, data);
	}

	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK,
			com.liferay.portal.kernel.json.JSONObject dataJSONObject)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoValueLocalService.addValue(
			companyId, className, tableName, columnName, classPK,
			dataJSONObject);
	}

	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, long data)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoValueLocalService.addValue(
			companyId, className, tableName, columnName, classPK, data);
	}

	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, long[] data)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoValueLocalService.addValue(
			companyId, className, tableName, columnName, classPK, data);
	}

	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK,
			java.util.Map<java.util.Locale, ?> dataMap,
			java.util.Locale defaultLocale)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoValueLocalService.addValue(
			companyId, className, tableName, columnName, classPK, dataMap,
			defaultLocale);
	}

	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, Number data)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoValueLocalService.addValue(
			companyId, className, tableName, columnName, classPK, data);
	}

	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, Number[] data)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoValueLocalService.addValue(
			companyId, className, tableName, columnName, classPK, data);
	}

	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, Object data)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoValueLocalService.addValue(
			companyId, className, tableName, columnName, classPK, data);
	}

	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, short data)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoValueLocalService.addValue(
			companyId, className, tableName, columnName, classPK, data);
	}

	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, short[] data)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoValueLocalService.addValue(
			companyId, className, tableName, columnName, classPK, data);
	}

	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, String data)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoValueLocalService.addValue(
			companyId, className, tableName, columnName, classPK, data);
	}

	@Override
	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, String[] data)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoValueLocalService.addValue(
			companyId, className, tableName, columnName, classPK, data);
	}

	@Override
	public void addValues(
			long classNameId, long tableId,
			java.util.List<com.liferay.expando.kernel.model.ExpandoColumn>
				columns,
			long classPK, java.util.Map<String, String> data)
		throws com.liferay.portal.kernel.exception.PortalException {

		_expandoValueLocalService.addValues(
			classNameId, tableId, columns, classPK, data);
	}

	@Override
	public void addValues(
			long companyId, long classNameId, String tableName, long classPK,
			java.util.Map<String, java.io.Serializable> attributes)
		throws com.liferay.portal.kernel.exception.PortalException {

		_expandoValueLocalService.addValues(
			companyId, classNameId, tableName, classPK, attributes);
	}

	@Override
	public void addValues(
			long companyId, String className, String tableName, long classPK,
			java.util.Map<String, java.io.Serializable> attributes)
		throws com.liferay.portal.kernel.exception.PortalException {

		_expandoValueLocalService.addValues(
			companyId, className, tableName, classPK, attributes);
	}

	/**
	 * Creates a new expando value with the primary key. Does not add the expando value to the database.
	 *
	 * @param valueId the primary key for the new expando value
	 * @return the new expando value
	 */
	@Override
	public ExpandoValue createExpandoValue(long valueId) {
		return _expandoValueLocalService.createExpandoValue(valueId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoValueLocalService.createPersistedModel(primaryKeyObj);
	}

	@Override
	public void deleteColumnValues(long columnId) {
		_expandoValueLocalService.deleteColumnValues(columnId);
	}

	/**
	 * Deletes the expando value from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ExpandoValueLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param expandoValue the expando value
	 * @return the expando value that was removed
	 */
	@Override
	public ExpandoValue deleteExpandoValue(ExpandoValue expandoValue) {
		return _expandoValueLocalService.deleteExpandoValue(expandoValue);
	}

	/**
	 * Deletes the expando value with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ExpandoValueLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param valueId the primary key of the expando value
	 * @return the expando value that was removed
	 * @throws PortalException if a expando value with the primary key could not be found
	 */
	@Override
	public ExpandoValue deleteExpandoValue(long valueId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoValueLocalService.deleteExpandoValue(valueId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoValueLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public void deleteRowValues(long rowId) {
		_expandoValueLocalService.deleteRowValues(rowId);
	}

	@Override
	public void deleteTableValues(long tableId) {
		_expandoValueLocalService.deleteTableValues(tableId);
	}

	@Override
	public void deleteValue(ExpandoValue value) {
		_expandoValueLocalService.deleteValue(value);
	}

	@Override
	public void deleteValue(long valueId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_expandoValueLocalService.deleteValue(valueId);
	}

	@Override
	public void deleteValue(long columnId, long rowId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_expandoValueLocalService.deleteValue(columnId, rowId);
	}

	@Override
	public void deleteValue(
			long companyId, long classNameId, String tableName,
			String columnName, long classPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		_expandoValueLocalService.deleteValue(
			companyId, classNameId, tableName, columnName, classPK);
	}

	@Override
	public void deleteValue(
			long companyId, String className, String tableName,
			String columnName, long classPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		_expandoValueLocalService.deleteValue(
			companyId, className, tableName, columnName, classPK);
	}

	@Override
	public void deleteValues(long classNameId, long classPK) {
		_expandoValueLocalService.deleteValues(classNameId, classPK);
	}

	@Override
	public void deleteValues(String className, long classPK) {
		_expandoValueLocalService.deleteValues(className, classPK);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _expandoValueLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _expandoValueLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _expandoValueLocalService.dynamicQuery();
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

		return _expandoValueLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.expando.model.impl.ExpandoValueModelImpl</code>.
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

		return _expandoValueLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.expando.model.impl.ExpandoValueModelImpl</code>.
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

		return _expandoValueLocalService.dynamicQuery(
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

		return _expandoValueLocalService.dynamicQueryCount(dynamicQuery);
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

		return _expandoValueLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public ExpandoValue fetchExpandoValue(long valueId) {
		return _expandoValueLocalService.fetchExpandoValue(valueId);
	}

	@Override
	public ExpandoValue fetchValue(long tableId, long columnId, long classPK) {
		return _expandoValueLocalService.fetchValue(tableId, columnId, classPK);
	}

	@Override
	public ExpandoValue fetchValue(
		long companyId, long classNameId, String tableName, String columnName,
		long classPK) {

		return _expandoValueLocalService.fetchValue(
			companyId, classNameId, tableName, columnName, classPK);
	}

	@Override
	public ExpandoValue fetchValue(
		long companyId, String className, String tableName, String columnName,
		long classPK) {

		return _expandoValueLocalService.fetchValue(
			companyId, className, tableName, columnName, classPK);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _expandoValueLocalService.getActionableDynamicQuery();
	}

	@Override
	public java.util.List<ExpandoValue> getColumnValues(
		long columnId, int start, int end) {

		return _expandoValueLocalService.getColumnValues(columnId, start, end);
	}

	@Override
	public java.util.List<ExpandoValue> getColumnValues(
		long companyId, long classNameId, String tableName, String columnName,
		int start, int end) {

		return _expandoValueLocalService.getColumnValues(
			companyId, classNameId, tableName, columnName, start, end);
	}

	@Override
	public java.util.List<ExpandoValue> getColumnValues(
		long companyId, long classNameId, String tableName, String columnName,
		String data, int start, int end) {

		return _expandoValueLocalService.getColumnValues(
			companyId, classNameId, tableName, columnName, data, start, end);
	}

	@Override
	public java.util.List<ExpandoValue> getColumnValues(
		long companyId, String className, String tableName, String columnName,
		int start, int end) {

		return _expandoValueLocalService.getColumnValues(
			companyId, className, tableName, columnName, start, end);
	}

	@Override
	public java.util.List<ExpandoValue> getColumnValues(
		long companyId, String className, String tableName, String columnName,
		String data, int start, int end) {

		return _expandoValueLocalService.getColumnValues(
			companyId, className, tableName, columnName, data, start, end);
	}

	@Override
	public int getColumnValuesCount(long columnId) {
		return _expandoValueLocalService.getColumnValuesCount(columnId);
	}

	@Override
	public int getColumnValuesCount(
		long companyId, long classNameId, String tableName, String columnName) {

		return _expandoValueLocalService.getColumnValuesCount(
			companyId, classNameId, tableName, columnName);
	}

	@Override
	public int getColumnValuesCount(
		long companyId, long classNameId, String tableName, String columnName,
		String data) {

		return _expandoValueLocalService.getColumnValuesCount(
			companyId, classNameId, tableName, columnName, data);
	}

	@Override
	public int getColumnValuesCount(
		long companyId, String className, String tableName, String columnName) {

		return _expandoValueLocalService.getColumnValuesCount(
			companyId, className, tableName, columnName);
	}

	@Override
	public int getColumnValuesCount(
		long companyId, String className, String tableName, String columnName,
		String data) {

		return _expandoValueLocalService.getColumnValuesCount(
			companyId, className, tableName, columnName, data);
	}

	@Override
	public java.util.Map<String, java.io.Serializable> getData(
			long companyId, String className, String tableName,
			java.util.Collection<String> columnNames, long classPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoValueLocalService.getData(
			companyId, className, tableName, columnNames, classPK);
	}

	@Override
	public java.io.Serializable getData(
			long companyId, String className, String tableName,
			String columnName, long classPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoValueLocalService.getData(
			companyId, className, tableName, columnName, classPK);
	}

	@Override
	public boolean getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, boolean defaultData)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoValueLocalService.getData(
			companyId, className, tableName, columnName, classPK, defaultData);
	}

	@Override
	public boolean[] getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, boolean[] defaultData)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoValueLocalService.getData(
			companyId, className, tableName, columnName, classPK, defaultData);
	}

	@Override
	public java.util.Date getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, java.util.Date defaultData)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoValueLocalService.getData(
			companyId, className, tableName, columnName, classPK, defaultData);
	}

	@Override
	public java.util.Date[] getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, java.util.Date[] defaultData)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoValueLocalService.getData(
			companyId, className, tableName, columnName, classPK, defaultData);
	}

	@Override
	public double getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, double defaultData)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoValueLocalService.getData(
			companyId, className, tableName, columnName, classPK, defaultData);
	}

	@Override
	public double[] getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, double[] defaultData)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoValueLocalService.getData(
			companyId, className, tableName, columnName, classPK, defaultData);
	}

	@Override
	public float getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, float defaultData)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoValueLocalService.getData(
			companyId, className, tableName, columnName, classPK, defaultData);
	}

	@Override
	public float[] getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, float[] defaultData)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoValueLocalService.getData(
			companyId, className, tableName, columnName, classPK, defaultData);
	}

	@Override
	public int getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, int defaultData)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoValueLocalService.getData(
			companyId, className, tableName, columnName, classPK, defaultData);
	}

	@Override
	public int[] getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, int[] defaultData)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoValueLocalService.getData(
			companyId, className, tableName, columnName, classPK, defaultData);
	}

	@Override
	public com.liferay.portal.kernel.json.JSONObject getData(
			long companyId, String className, String tableName,
			String columnName, long classPK,
			com.liferay.portal.kernel.json.JSONObject defaultDataJSONObject)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoValueLocalService.getData(
			companyId, className, tableName, columnName, classPK,
			defaultDataJSONObject);
	}

	@Override
	public long getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, long defaultData)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoValueLocalService.getData(
			companyId, className, tableName, columnName, classPK, defaultData);
	}

	@Override
	public long[] getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, long[] defaultData)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoValueLocalService.getData(
			companyId, className, tableName, columnName, classPK, defaultData);
	}

	@Override
	public java.util.Map<?, ?> getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, java.util.Map<?, ?> defaultData)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoValueLocalService.getData(
			companyId, className, tableName, columnName, classPK, defaultData);
	}

	@Override
	public Number getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, Number defaultData)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoValueLocalService.getData(
			companyId, className, tableName, columnName, classPK, defaultData);
	}

	@Override
	public Number[] getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, Number[] defaultData)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoValueLocalService.getData(
			companyId, className, tableName, columnName, classPK, defaultData);
	}

	@Override
	public short getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, short defaultData)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoValueLocalService.getData(
			companyId, className, tableName, columnName, classPK, defaultData);
	}

	@Override
	public short[] getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, short[] defaultData)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoValueLocalService.getData(
			companyId, className, tableName, columnName, classPK, defaultData);
	}

	@Override
	public String getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, String defaultData)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoValueLocalService.getData(
			companyId, className, tableName, columnName, classPK, defaultData);
	}

	@Override
	public String[] getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, String[] defaultData)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoValueLocalService.getData(
			companyId, className, tableName, columnName, classPK, defaultData);
	}

	@Override
	public java.util.List<ExpandoValue> getDefaultTableColumnValues(
		long companyId, long classNameId, String columnName, int start,
		int end) {

		return _expandoValueLocalService.getDefaultTableColumnValues(
			companyId, classNameId, columnName, start, end);
	}

	@Override
	public java.util.List<ExpandoValue> getDefaultTableColumnValues(
		long companyId, String className, String columnName, int start,
		int end) {

		return _expandoValueLocalService.getDefaultTableColumnValues(
			companyId, className, columnName, start, end);
	}

	@Override
	public int getDefaultTableColumnValuesCount(
		long companyId, long classNameId, String columnName) {

		return _expandoValueLocalService.getDefaultTableColumnValuesCount(
			companyId, classNameId, columnName);
	}

	@Override
	public int getDefaultTableColumnValuesCount(
		long companyId, String className, String columnName) {

		return _expandoValueLocalService.getDefaultTableColumnValuesCount(
			companyId, className, columnName);
	}

	/**
	 * Returns the expando value with the primary key.
	 *
	 * @param valueId the primary key of the expando value
	 * @return the expando value
	 * @throws PortalException if a expando value with the primary key could not be found
	 */
	@Override
	public ExpandoValue getExpandoValue(long valueId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoValueLocalService.getExpandoValue(valueId);
	}

	/**
	 * Returns a range of all the expando values.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.expando.model.impl.ExpandoValueModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of expando values
	 * @param end the upper bound of the range of expando values (not inclusive)
	 * @return the range of expando values
	 */
	@Override
	public java.util.List<ExpandoValue> getExpandoValues(int start, int end) {
		return _expandoValueLocalService.getExpandoValues(start, end);
	}

	/**
	 * Returns the number of expando values.
	 *
	 * @return the number of expando values
	 */
	@Override
	public int getExpandoValuesCount() {
		return _expandoValueLocalService.getExpandoValuesCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _expandoValueLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _expandoValueLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoValueLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public java.util.List<ExpandoValue> getRowValues(long rowId) {
		return _expandoValueLocalService.getRowValues(rowId);
	}

	@Override
	public java.util.List<ExpandoValue> getRowValues(
		long rowId, int start, int end) {

		return _expandoValueLocalService.getRowValues(rowId, start, end);
	}

	@Override
	public java.util.List<ExpandoValue> getRowValues(
		long companyId, long classNameId, String tableName, long classPK,
		int start, int end) {

		return _expandoValueLocalService.getRowValues(
			companyId, classNameId, tableName, classPK, start, end);
	}

	@Override
	public java.util.List<ExpandoValue> getRowValues(
		long companyId, String className, String tableName, long classPK,
		int start, int end) {

		return _expandoValueLocalService.getRowValues(
			companyId, className, tableName, classPK, start, end);
	}

	@Override
	public int getRowValuesCount(long rowId) {
		return _expandoValueLocalService.getRowValuesCount(rowId);
	}

	@Override
	public int getRowValuesCount(
		long companyId, long classNameId, String tableName, long classPK) {

		return _expandoValueLocalService.getRowValuesCount(
			companyId, classNameId, tableName, classPK);
	}

	@Override
	public int getRowValuesCount(
		long companyId, String className, String tableName, long classPK) {

		return _expandoValueLocalService.getRowValuesCount(
			companyId, className, tableName, classPK);
	}

	@Override
	public ExpandoValue getValue(long valueId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoValueLocalService.getValue(valueId);
	}

	@Override
	public ExpandoValue getValue(long columnId, long rowId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoValueLocalService.getValue(columnId, rowId);
	}

	@Override
	public ExpandoValue getValue(long tableId, long columnId, long classPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoValueLocalService.getValue(tableId, columnId, classPK);
	}

	@Override
	public ExpandoValue getValue(
			long companyId, long classNameId, String tableName,
			String columnName, long classPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoValueLocalService.getValue(
			companyId, classNameId, tableName, columnName, classPK);
	}

	@Override
	public ExpandoValue getValue(
			long companyId, String className, String tableName,
			String columnName, long classPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _expandoValueLocalService.getValue(
			companyId, className, tableName, columnName, classPK);
	}

	/**
	 * Updates the expando value in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ExpandoValueLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param expandoValue the expando value
	 * @return the expando value that was updated
	 */
	@Override
	public ExpandoValue updateExpandoValue(ExpandoValue expandoValue) {
		return _expandoValueLocalService.updateExpandoValue(expandoValue);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _expandoValueLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<ExpandoValue> getCTPersistence() {
		return _expandoValueLocalService.getCTPersistence();
	}

	@Override
	public Class<ExpandoValue> getModelClass() {
		return _expandoValueLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<ExpandoValue>, R, E>
				updateUnsafeFunction)
		throws E {

		return _expandoValueLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public ExpandoValueLocalService getWrappedService() {
		return _expandoValueLocalService;
	}

	@Override
	public void setWrappedService(
		ExpandoValueLocalService expandoValueLocalService) {

		_expandoValueLocalService = expandoValueLocalService;
	}

	private ExpandoValueLocalService _expandoValueLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1705224228