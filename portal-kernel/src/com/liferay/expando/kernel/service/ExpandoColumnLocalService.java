/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.expando.kernel.service;

import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
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

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for ExpandoColumn. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see ExpandoColumnLocalServiceUtil
 * @generated
 */
@CTAware
@OSGiBeanProperties(
	property = {
		"model.class.name=com.liferay.expando.kernel.model.ExpandoColumn"
	}
)
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface ExpandoColumnLocalService
	extends BaseLocalService, CTService<ExpandoColumn>,
			PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.portlet.expando.service.impl.ExpandoColumnLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the expando column local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link ExpandoColumnLocalServiceUtil} if injection and service tracking are not available.
	 */
	public ExpandoColumn addColumn(long tableId, String name, int type)
		throws PortalException;

	public ExpandoColumn addColumn(
			long tableId, String name, int type, Object defaultData)
		throws PortalException;

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
	@Indexable(type = IndexableType.REINDEX)
	public ExpandoColumn addExpandoColumn(ExpandoColumn expandoColumn);

	/**
	 * Creates a new expando column with the primary key. Does not add the expando column to the database.
	 *
	 * @param columnId the primary key for the new expando column
	 * @return the new expando column
	 */
	@Transactional(enabled = false)
	public ExpandoColumn createExpandoColumn(long columnId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	public void deleteColumn(ExpandoColumn column) throws PortalException;

	public void deleteColumn(long columnId) throws PortalException;

	public void deleteColumn(
			long companyId, long classNameId, String tableName, String name)
		throws PortalException;

	public void deleteColumn(long tableId, String name) throws PortalException;

	public void deleteColumn(
			long companyId, String className, String tableName, String name)
		throws PortalException;

	public void deleteColumns(long tableId) throws PortalException;

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
	@Indexable(type = IndexableType.DELETE)
	public ExpandoColumn deleteExpandoColumn(ExpandoColumn expandoColumn);

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
	@Indexable(type = IndexableType.DELETE)
	public ExpandoColumn deleteExpandoColumn(long columnId)
		throws PortalException;

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException;

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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.expando.model.impl.ExpandoColumnModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.expando.model.impl.ExpandoColumnModelImpl</code>.
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
	public ExpandoColumn fetchColumn(
		long companyId, long classNameId, String tableName, String name);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExpandoColumn fetchColumn(long tableId, String name);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExpandoColumn fetchExpandoColumn(long columnId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExpandoColumn getColumn(long columnId) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExpandoColumn getColumn(
			long companyId, long classNameId, String tableName, String name)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExpandoColumn getColumn(long tableId, String name)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExpandoColumn getColumn(
		long companyId, String className, String tableName, String name);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<ExpandoColumn> getColumns(long tableId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<ExpandoColumn> getColumns(
		long companyId, long classNameId, String tableName);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<ExpandoColumn> getColumns(
		long companyId, String className, String tableName);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getColumnsCount(long tableId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExpandoColumn getDefaultTableColumn(
		long companyId, long classNameId, String name);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExpandoColumn getDefaultTableColumn(
		long companyId, String className, String name);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<ExpandoColumn> getDefaultTableColumns(
		long companyId, long classNameId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<ExpandoColumn> getDefaultTableColumns(
		long companyId, String className);

	/**
	 * Returns the expando column with the primary key.
	 *
	 * @param columnId the primary key of the expando column
	 * @return the expando column
	 * @throws PortalException if a expando column with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExpandoColumn getExpandoColumn(long columnId) throws PortalException;

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<ExpandoColumn> getExpandoColumns(int start, int end);

	/**
	 * Returns the number of expando columns.
	 *
	 * @return the number of expando columns
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getExpandoColumnsCount();

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

	public ExpandoColumn updateColumn(long columnId, String name, int type)
		throws PortalException;

	public ExpandoColumn updateColumn(
			long columnId, String name, int type, Object defaultData)
		throws PortalException;

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
	@Indexable(type = IndexableType.REINDEX)
	public ExpandoColumn updateExpandoColumn(ExpandoColumn expandoColumn);

	public ExpandoColumn updateTypeSettings(long columnId, String typeSettings)
		throws PortalException;

	@Override
	@Transactional(enabled = false)
	public CTPersistence<ExpandoColumn> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<ExpandoColumn> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<ExpandoColumn>, R, E>
				updateUnsafeFunction)
		throws E;

}
// LIFERAY-SERVICE-BUILDER-HASH:668027393