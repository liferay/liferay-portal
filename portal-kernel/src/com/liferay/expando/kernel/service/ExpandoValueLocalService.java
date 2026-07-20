/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.expando.kernel.service;

import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoValue;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.portal.kernel.spring.osgi.OSGiBeanProperties;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for ExpandoValue. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see ExpandoValueLocalServiceUtil
 * @generated
 */
@CTAware
@OSGiBeanProperties(
	property = {
		"model.class.name=com.liferay.expando.kernel.model.ExpandoValue"
	}
)
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface ExpandoValueLocalService
	extends BaseLocalService, CTService<ExpandoValue>,
			PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.portlet.expando.service.impl.ExpandoValueLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the expando value local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link ExpandoValueLocalServiceUtil} if injection and service tracking are not available.
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
	@Indexable(type = IndexableType.REINDEX)
	public ExpandoValue addExpandoValue(ExpandoValue expandoValue);

	public ExpandoValue addValue(
			long classNameId, long tableId, long columnId, long classPK,
			String data)
		throws PortalException;

	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, boolean data)
		throws PortalException;

	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, boolean[] data)
		throws PortalException;

	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, Date data)
		throws PortalException;

	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, Date[] data)
		throws PortalException;

	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, double data)
		throws PortalException;

	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, double[] data)
		throws PortalException;

	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, float data)
		throws PortalException;

	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, float[] data)
		throws PortalException;

	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, int data)
		throws PortalException;

	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, int[] data)
		throws PortalException;

	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, JSONObject dataJSONObject)
		throws PortalException;

	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, long data)
		throws PortalException;

	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, long[] data)
		throws PortalException;

	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, Map<Locale, ?> dataMap,
			Locale defaultLocale)
		throws PortalException;

	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, Number data)
		throws PortalException;

	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, Number[] data)
		throws PortalException;

	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, Object data)
		throws PortalException;

	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, short data)
		throws PortalException;

	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, short[] data)
		throws PortalException;

	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, String data)
		throws PortalException;

	public ExpandoValue addValue(
			long companyId, String className, String tableName,
			String columnName, long classPK, String[] data)
		throws PortalException;

	public void addValues(
			long classNameId, long tableId, List<ExpandoColumn> columns,
			long classPK, Map<String, String> data)
		throws PortalException;

	public void addValues(
			long companyId, long classNameId, String tableName, long classPK,
			Map<String, Serializable> attributes)
		throws PortalException;

	public void addValues(
			long companyId, String className, String tableName, long classPK,
			Map<String, Serializable> attributes)
		throws PortalException;

	/**
	 * Creates a new expando value with the primary key. Does not add the expando value to the database.
	 *
	 * @param valueId the primary key for the new expando value
	 * @return the new expando value
	 */
	@Transactional(enabled = false)
	public ExpandoValue createExpandoValue(long valueId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	public void deleteColumnValues(long columnId);

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
	@Indexable(type = IndexableType.DELETE)
	public ExpandoValue deleteExpandoValue(ExpandoValue expandoValue);

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
	@Indexable(type = IndexableType.DELETE)
	public ExpandoValue deleteExpandoValue(long valueId) throws PortalException;

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException;

	public void deleteRowValues(long rowId);

	public void deleteTableValues(long tableId);

	public void deleteValue(ExpandoValue value);

	public void deleteValue(long valueId) throws PortalException;

	public void deleteValue(long columnId, long rowId) throws PortalException;

	public void deleteValue(
			long companyId, long classNameId, String tableName,
			String columnName, long classPK)
		throws PortalException;

	public void deleteValue(
			long companyId, String className, String tableName,
			String columnName, long classPK)
		throws PortalException;

	public void deleteValues(long classNameId, long classPK);

	public void deleteValues(String className, long classPK);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> T dslQuery(DSLQuery dslQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int dslQueryCount(DSLQuery dslQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DynamicQuery dynamicQuery();

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(DynamicQuery dynamicQuery);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(DynamicQuery dynamicQuery);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(
		DynamicQuery dynamicQuery, Projection projection);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExpandoValue fetchExpandoValue(long valueId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExpandoValue fetchValue(long tableId, long columnId, long classPK);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExpandoValue fetchValue(
		long companyId, long classNameId, String tableName, String columnName,
		long classPK);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExpandoValue fetchValue(
		long companyId, String className, String tableName, String columnName,
		long classPK);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<ExpandoValue> getColumnValues(
		long columnId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<ExpandoValue> getColumnValues(
		long companyId, long classNameId, String tableName, String columnName,
		int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<ExpandoValue> getColumnValues(
		long companyId, long classNameId, String tableName, String columnName,
		String data, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<ExpandoValue> getColumnValues(
		long companyId, String className, String tableName, String columnName,
		int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<ExpandoValue> getColumnValues(
		long companyId, String className, String tableName, String columnName,
		String data, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getColumnValuesCount(long columnId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getColumnValuesCount(
		long companyId, long classNameId, String tableName, String columnName);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getColumnValuesCount(
		long companyId, long classNameId, String tableName, String columnName,
		String data);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getColumnValuesCount(
		long companyId, String className, String tableName, String columnName);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getColumnValuesCount(
		long companyId, String className, String tableName, String columnName,
		String data);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Map<String, Serializable> getData(
			long companyId, String className, String tableName,
			Collection<String> columnNames, long classPK)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Serializable getData(
			long companyId, String className, String tableName,
			String columnName, long classPK)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, boolean defaultData)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean[] getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, boolean[] defaultData)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Date getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, Date defaultData)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Date[] getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, Date[] defaultData)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public double getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, double defaultData)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public double[] getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, double[] defaultData)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public float getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, float defaultData)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public float[] getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, float[] defaultData)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, int defaultData)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int[] getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, int[] defaultData)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public JSONObject getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, JSONObject defaultDataJSONObject)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, long defaultData)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long[] getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, long[] defaultData)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Map<?, ?> getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, Map<?, ?> defaultData)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Number getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, Number defaultData)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Number[] getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, Number[] defaultData)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public short getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, short defaultData)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public short[] getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, short[] defaultData)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public String getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, String defaultData)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public String[] getData(
			long companyId, String className, String tableName,
			String columnName, long classPK, String[] defaultData)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<ExpandoValue> getDefaultTableColumnValues(
		long companyId, long classNameId, String columnName, int start,
		int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<ExpandoValue> getDefaultTableColumnValues(
		long companyId, String className, String columnName, int start,
		int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getDefaultTableColumnValuesCount(
		long companyId, long classNameId, String columnName);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getDefaultTableColumnValuesCount(
		long companyId, String className, String columnName);

	/**
	 * Returns the expando value with the primary key.
	 *
	 * @param valueId the primary key of the expando value
	 * @return the expando value
	 * @throws PortalException if a expando value with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExpandoValue getExpandoValue(long valueId) throws PortalException;

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<ExpandoValue> getExpandoValues(int start, int end);

	/**
	 * Returns the number of expando values.
	 *
	 * @return the number of expando values
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getExpandoValuesCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	/**
	 * @throws PortalException
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<ExpandoValue> getRowValues(long rowId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<ExpandoValue> getRowValues(long rowId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<ExpandoValue> getRowValues(
		long companyId, long classNameId, String tableName, long classPK,
		int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<ExpandoValue> getRowValues(
		long companyId, String className, String tableName, long classPK,
		int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getRowValuesCount(long rowId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getRowValuesCount(
		long companyId, long classNameId, String tableName, long classPK);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getRowValuesCount(
		long companyId, String className, String tableName, long classPK);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExpandoValue getValue(long valueId) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExpandoValue getValue(long columnId, long rowId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExpandoValue getValue(long tableId, long columnId, long classPK)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExpandoValue getValue(
			long companyId, long classNameId, String tableName,
			String columnName, long classPK)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExpandoValue getValue(
			long companyId, String className, String tableName,
			String columnName, long classPK)
		throws PortalException;

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
	@Indexable(type = IndexableType.REINDEX)
	public ExpandoValue updateExpandoValue(ExpandoValue expandoValue);

	@Override
	@Transactional(enabled = false)
	public CTPersistence<ExpandoValue> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<ExpandoValue> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<ExpandoValue>, R, E>
				updateUnsafeFunction)
		throws E;

}
// LIFERAY-SERVICE-BUILDER-HASH:1195626595