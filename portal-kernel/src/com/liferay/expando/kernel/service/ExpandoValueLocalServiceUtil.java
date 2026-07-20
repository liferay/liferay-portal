/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.expando.kernel.service;

import com.liferay.expando.kernel.model.ExpandoValue;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * Provides the local service utility for ExpandoValue. This utility wraps
 * <code>com.liferay.portlet.expando.service.impl.ExpandoValueLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see ExpandoValueLocalService
 * @generated
 */
public class ExpandoValueLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portlet.expando.service.impl.ExpandoValueLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

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
	public static ExpandoValue addExpandoValue(ExpandoValue expandoValue) {
		return getService().addExpandoValue(expandoValue);
	}

	public static ExpandoValue addValue(
			long classNameId, long tableId, long columnId, long classPK,
			String data)
		throws PortalException {

		return getService().addValue(
			classNameId, tableId, columnId, classPK, data);
	}

	public static ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, boolean data)
		throws PortalException {

		return getService().addValue(
			companyId, className, tableName, columnName, classPK, data);
	}

	public static ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, boolean[] data)
		throws PortalException {

		return getService().addValue(
			companyId, className, tableName, columnName, classPK, data);
	}

	public static ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, java.util.Date data)
		throws PortalException {

		return getService().addValue(
			companyId, className, tableName, columnName, classPK, data);
	}

	public static ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, java.util.Date[] data)
		throws PortalException {

		return getService().addValue(
			companyId, className, tableName, columnName, classPK, data);
	}

	public static ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, double data)
		throws PortalException {

		return getService().addValue(
			companyId, className, tableName, columnName, classPK, data);
	}

	public static ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, double[] data)
		throws PortalException {

		return getService().addValue(
			companyId, className, tableName, columnName, classPK, data);
	}

	public static ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, float data)
		throws PortalException {

		return getService().addValue(
			companyId, className, tableName, columnName, classPK, data);
	}

	public static ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, float[] data)
		throws PortalException {

		return getService().addValue(
			companyId, className, tableName, columnName, classPK, data);
	}

	public static ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, int data)
		throws PortalException {

		return getService().addValue(
			companyId, className, tableName, columnName, classPK, data);
	}

	public static ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, int[] data)
		throws PortalException {

		return getService().addValue(
			companyId, className, tableName, columnName, classPK, data);
	}

	public static ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK,
			com.liferay.portal.kernel.json.JSONObject dataJSONObject)
		throws PortalException {

		return getService().addValue(
			companyId, className, tableName, columnName, classPK,
			dataJSONObject);
	}

	public static ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, long data)
		throws PortalException {

		return getService().addValue(
			companyId, className, tableName, columnName, classPK, data);
	}

	public static ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, long[] data)
		throws PortalException {

		return getService().addValue(
			companyId, className, tableName, columnName, classPK, data);
	}

	public static ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, Map<java.util.Locale, ?> dataMap,
			java.util.Locale defaultLocale)
		throws PortalException {

		return getService().addValue(
			companyId, className, tableName, columnName, classPK, dataMap,
			defaultLocale);
	}

	public static ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, Number data)
		throws PortalException {

		return getService().addValue(
			companyId, className, tableName, columnName, classPK, data);
	}

	public static ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, Number[] data)
		throws PortalException {

		return getService().addValue(
			companyId, className, tableName, columnName, classPK, data);
	}

	public static ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, Object data)
		throws PortalException {

		return getService().addValue(
			companyId, className, tableName, columnName, classPK, data);
	}

	public static ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, short data)
		throws PortalException {

		return getService().addValue(
			companyId, className, tableName, columnName, classPK, data);
	}

	public static ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, short[] data)
		throws PortalException {

		return getService().addValue(
			companyId, className, tableName, columnName, classPK, data);
	}

	public static ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, String data)
		throws PortalException {

		return getService().addValue(
			companyId, className, tableName, columnName, classPK, data);
	}

	public static ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, String[] data)
		throws PortalException {

		return getService().addValue(
			companyId, className, tableName, columnName, classPK, data);
	}

	public static void addValues(
			long classNameId, long tableId,
			List<com.liferay.expando.kernel.model.ExpandoColumn> columns,
			long classPK, Map<String, String> data)
		throws PortalException {

		getService().addValues(classNameId, tableId, columns, classPK, data);
	}

	public static void addValues(
			long companyId, long classNameId, String tableName, long classPK,
			Map<String, Serializable> attributes)
		throws PortalException {

		getService().addValues(
			companyId, classNameId, tableName, classPK, attributes);
	}

	public static void addValues(
			long companyId, String className, String tableName, long classPK,
			Map<String, Serializable> attributes)
		throws PortalException {

		getService().addValues(
			companyId, className, tableName, classPK, attributes);
	}

	/**
	 * Creates a new expando value with the primary key. Does not add the expando value to the database.
	 *
	 * @param valueId the primary key for the new expando value
	 * @return the new expando value
	 */
	public static ExpandoValue createExpandoValue(long valueId) {
		return getService().createExpandoValue(valueId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	public static void deleteColumnValues(long columnId) {
		getService().deleteColumnValues(columnId);
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
	public static ExpandoValue deleteExpandoValue(ExpandoValue expandoValue) {
		return getService().deleteExpandoValue(expandoValue);
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
	public static ExpandoValue deleteExpandoValue(long valueId)
		throws PortalException {

		return getService().deleteExpandoValue(valueId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	public static void deleteRowValues(long rowId) {
		getService().deleteRowValues(rowId);
	}

	public static void deleteTableValues(long tableId) {
		getService().deleteTableValues(tableId);
	}

	public static void deleteValue(ExpandoValue value) {
		getService().deleteValue(value);
	}

	public static void deleteValue(long valueId) throws PortalException {
		getService().deleteValue(valueId);
	}

	public static void deleteValue(long columnId, long rowId)
		throws PortalException {

		getService().deleteValue(columnId, rowId);
	}

	public static void deleteValue(
			long companyId, long classNameId, String tableName,
			String columnName, long classPK)
		throws PortalException {

		getService().deleteValue(
			companyId, classNameId, tableName, columnName, classPK);
	}

	public static void deleteValue(
			long companyId, String className, String tableName,
			String columnName, long classPK)
		throws PortalException {

		getService().deleteValue(
			companyId, className, tableName, columnName, classPK);
	}

	public static void deleteValues(long classNameId, long classPK) {
		getService().deleteValues(classNameId, classPK);
	}

	public static void deleteValues(String className, long classPK) {
		getService().deleteValues(className, classPK);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.expando.model.impl.ExpandoValueModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.expando.model.impl.ExpandoValueModelImpl</code>.
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

	public static ExpandoValue fetchExpandoValue(long valueId) {
		return getService().fetchExpandoValue(valueId);
	}

	public static ExpandoValue fetchValue(
		long tableId, long columnId, long classPK) {

		return getService().fetchValue(tableId, columnId, classPK);
	}

	public static ExpandoValue fetchValue(
		long companyId, long classNameId, String tableName, String columnName,
		long classPK) {

		return getService().fetchValue(
			companyId, classNameId, tableName, columnName, classPK);
	}

	public static ExpandoValue fetchValue(
		long companyId, String className, String tableName, String columnName,
		long classPK) {

		return getService().fetchValue(
			companyId, className, tableName, columnName, classPK);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static List<ExpandoValue> getColumnValues(
		long columnId, int start, int end) {

		return getService().getColumnValues(columnId, start, end);
	}

	public static List<ExpandoValue> getColumnValues(
		long companyId, long classNameId, String tableName, String columnName,
		int start, int end) {

		return getService().getColumnValues(
			companyId, classNameId, tableName, columnName, start, end);
	}

	public static List<ExpandoValue> getColumnValues(
		long companyId, long classNameId, String tableName, String columnName,
		String data, int start, int end) {

		return getService().getColumnValues(
			companyId, classNameId, tableName, columnName, data, start, end);
	}

	public static List<ExpandoValue> getColumnValues(
		long companyId, String className, String tableName, String columnName,
		int start, int end) {

		return getService().getColumnValues(
			companyId, className, tableName, columnName, start, end);
	}

	public static List<ExpandoValue> getColumnValues(
		long companyId, String className, String tableName, String columnName,
		String data, int start, int end) {

		return getService().getColumnValues(
			companyId, className, tableName, columnName, data, start, end);
	}

	public static int getColumnValuesCount(long columnId) {
		return getService().getColumnValuesCount(columnId);
	}

	public static int getColumnValuesCount(
		long companyId, long classNameId, String tableName, String columnName) {

		return getService().getColumnValuesCount(
			companyId, classNameId, tableName, columnName);
	}

	public static int getColumnValuesCount(
		long companyId, long classNameId, String tableName, String columnName,
		String data) {

		return getService().getColumnValuesCount(
			companyId, classNameId, tableName, columnName, data);
	}

	public static int getColumnValuesCount(
		long companyId, String className, String tableName, String columnName) {

		return getService().getColumnValuesCount(
			companyId, className, tableName, columnName);
	}

	public static int getColumnValuesCount(
		long companyId, String className, String tableName, String columnName,
		String data) {

		return getService().getColumnValuesCount(
			companyId, className, tableName, columnName, data);
	}

	public static Map<String, Serializable> getData(
			long companyId, String className, String tableName,
			java.util.Collection<String> columnNames, long classPK)
		throws PortalException {

		return getService().getData(
			companyId, className, tableName, columnNames, classPK);
	}

	public static Serializable getData(
			long companyId, String className, String tableName,
			String columnName, long classPK)
		throws PortalException {

		return getService().getData(
			companyId, className, tableName, columnName, classPK);
	}

	public static boolean getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, boolean defaultData)
		throws PortalException {

		return getService().getData(
			companyId, className, tableName, columnName, classPK, defaultData);
	}

	public static boolean[] getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, boolean[] defaultData)
		throws PortalException {

		return getService().getData(
			companyId, className, tableName, columnName, classPK, defaultData);
	}

	public static java.util.Date getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, java.util.Date defaultData)
		throws PortalException {

		return getService().getData(
			companyId, className, tableName, columnName, classPK, defaultData);
	}

	public static java.util.Date[] getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, java.util.Date[] defaultData)
		throws PortalException {

		return getService().getData(
			companyId, className, tableName, columnName, classPK, defaultData);
	}

	public static double getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, double defaultData)
		throws PortalException {

		return getService().getData(
			companyId, className, tableName, columnName, classPK, defaultData);
	}

	public static double[] getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, double[] defaultData)
		throws PortalException {

		return getService().getData(
			companyId, className, tableName, columnName, classPK, defaultData);
	}

	public static float getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, float defaultData)
		throws PortalException {

		return getService().getData(
			companyId, className, tableName, columnName, classPK, defaultData);
	}

	public static float[] getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, float[] defaultData)
		throws PortalException {

		return getService().getData(
			companyId, className, tableName, columnName, classPK, defaultData);
	}

	public static int getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, int defaultData)
		throws PortalException {

		return getService().getData(
			companyId, className, tableName, columnName, classPK, defaultData);
	}

	public static int[] getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, int[] defaultData)
		throws PortalException {

		return getService().getData(
			companyId, className, tableName, columnName, classPK, defaultData);
	}

	public static com.liferay.portal.kernel.json.JSONObject getData(
			long companyId, String className, String tableName,
			String columnName, long classPK,
			com.liferay.portal.kernel.json.JSONObject defaultDataJSONObject)
		throws PortalException {

		return getService().getData(
			companyId, className, tableName, columnName, classPK,
			defaultDataJSONObject);
	}

	public static long getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, long defaultData)
		throws PortalException {

		return getService().getData(
			companyId, className, tableName, columnName, classPK, defaultData);
	}

	public static long[] getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, long[] defaultData)
		throws PortalException {

		return getService().getData(
			companyId, className, tableName, columnName, classPK, defaultData);
	}

	public static Map<?, ?> getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, Map<?, ?> defaultData)
		throws PortalException {

		return getService().getData(
			companyId, className, tableName, columnName, classPK, defaultData);
	}

	public static Number getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, Number defaultData)
		throws PortalException {

		return getService().getData(
			companyId, className, tableName, columnName, classPK, defaultData);
	}

	public static Number[] getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, Number[] defaultData)
		throws PortalException {

		return getService().getData(
			companyId, className, tableName, columnName, classPK, defaultData);
	}

	public static short getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, short defaultData)
		throws PortalException {

		return getService().getData(
			companyId, className, tableName, columnName, classPK, defaultData);
	}

	public static short[] getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, short[] defaultData)
		throws PortalException {

		return getService().getData(
			companyId, className, tableName, columnName, classPK, defaultData);
	}

	public static String getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, String defaultData)
		throws PortalException {

		return getService().getData(
			companyId, className, tableName, columnName, classPK, defaultData);
	}

	public static String[] getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, String[] defaultData)
		throws PortalException {

		return getService().getData(
			companyId, className, tableName, columnName, classPK, defaultData);
	}

	public static List<ExpandoValue> getDefaultTableColumnValues(
		long companyId, long classNameId, String columnName, int start,
		int end) {

		return getService().getDefaultTableColumnValues(
			companyId, classNameId, columnName, start, end);
	}

	public static List<ExpandoValue> getDefaultTableColumnValues(
		long companyId, String className, String columnName, int start,
		int end) {

		return getService().getDefaultTableColumnValues(
			companyId, className, columnName, start, end);
	}

	public static int getDefaultTableColumnValuesCount(
		long companyId, long classNameId, String columnName) {

		return getService().getDefaultTableColumnValuesCount(
			companyId, classNameId, columnName);
	}

	public static int getDefaultTableColumnValuesCount(
		long companyId, String className, String columnName) {

		return getService().getDefaultTableColumnValuesCount(
			companyId, className, columnName);
	}

	/**
	 * Returns the expando value with the primary key.
	 *
	 * @param valueId the primary key of the expando value
	 * @return the expando value
	 * @throws PortalException if a expando value with the primary key could not be found
	 */
	public static ExpandoValue getExpandoValue(long valueId)
		throws PortalException {

		return getService().getExpandoValue(valueId);
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
	public static List<ExpandoValue> getExpandoValues(int start, int end) {
		return getService().getExpandoValues(start, end);
	}

	/**
	 * Returns the number of expando values.
	 *
	 * @return the number of expando values
	 */
	public static int getExpandoValuesCount() {
		return getService().getExpandoValuesCount();
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

	public static List<ExpandoValue> getRowValues(long rowId) {
		return getService().getRowValues(rowId);
	}

	public static List<ExpandoValue> getRowValues(
		long rowId, int start, int end) {

		return getService().getRowValues(rowId, start, end);
	}

	public static List<ExpandoValue> getRowValues(
		long companyId, long classNameId, String tableName, long classPK,
		int start, int end) {

		return getService().getRowValues(
			companyId, classNameId, tableName, classPK, start, end);
	}

	public static List<ExpandoValue> getRowValues(
		long companyId, String className, String tableName, long classPK,
		int start, int end) {

		return getService().getRowValues(
			companyId, className, tableName, classPK, start, end);
	}

	public static int getRowValuesCount(long rowId) {
		return getService().getRowValuesCount(rowId);
	}

	public static int getRowValuesCount(
		long companyId, long classNameId, String tableName, long classPK) {

		return getService().getRowValuesCount(
			companyId, classNameId, tableName, classPK);
	}

	public static int getRowValuesCount(
		long companyId, String className, String tableName, long classPK) {

		return getService().getRowValuesCount(
			companyId, className, tableName, classPK);
	}

	public static ExpandoValue getValue(long valueId) throws PortalException {
		return getService().getValue(valueId);
	}

	public static ExpandoValue getValue(long columnId, long rowId)
		throws PortalException {

		return getService().getValue(columnId, rowId);
	}

	public static ExpandoValue getValue(
			long tableId, long columnId, long classPK)
		throws PortalException {

		return getService().getValue(tableId, columnId, classPK);
	}

	public static ExpandoValue getValue(
			long companyId, long classNameId, String tableName,
			String columnName, long classPK)
		throws PortalException {

		return getService().getValue(
			companyId, classNameId, tableName, columnName, classPK);
	}

	public static ExpandoValue getValue(
			long companyId, String className, String tableName,
			String columnName, long classPK)
		throws PortalException {

		return getService().getValue(
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
	public static ExpandoValue updateExpandoValue(ExpandoValue expandoValue) {
		return getService().updateExpandoValue(expandoValue);
	}

	public static ExpandoValueLocalService getService() {
		return _service;
	}

	public static void setService(ExpandoValueLocalService service) {
		_service = service;
	}

	private static volatile ExpandoValueLocalService _service;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1643081554