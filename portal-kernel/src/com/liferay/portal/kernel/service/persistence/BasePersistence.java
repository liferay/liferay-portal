/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.persistence;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.orm.Dialect;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.ORMException;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.NoSuchModelException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import javax.sql.DataSource;

/**
 * The base interface for all ServiceBuilder persistence classes. This interface
 * should never need to be used directly.
 *
 * <p>
 * Caching information and settings can be found in
 * <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see    com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl
 */
public interface BasePersistence<T extends BaseModel<T>> {

	/**
	 * Clears the cache for all instances of this model.
	 *
	 * <p>
	 * The {@link com.liferay.portal.kernel.dao.orm.EntityCache} and {@link
	 * com.liferay.portal.kernel.dao.orm.FinderCache} are both cleared by this
	 * method.
	 * </p>
	 */
	public void clearCache();

	/**
	 * Clears the cache for a List instances of this model.
	 *
	 * <p>
	 * The {@link com.liferay.portal.kernel.dao.orm.EntityCache} and {@link
	 * com.liferay.portal.kernel.dao.orm.FinderCache} are both cleared by this
	 * method.
	 * </p>
	 *
	 * @param modelList the List instances of this model to clear the cache for
	 */
	public void clearCache(List<T> modelList);

	public default void clearCache(Set<Serializable> primaryKeys) {
		clearCache();
	}

	/**
	 * Clears the cache for one instance of this model.
	 *
	 * <p>
	 * The {@link com.liferay.portal.kernel.dao.orm.EntityCache} and {@link
	 * com.liferay.portal.kernel.dao.orm.FinderCache} are both cleared by this
	 * method.
	 * </p>
	 *
	 * @param model the instance of this model to clear the cache for
	 */
	public void clearCache(T model);

	public void closeSession(Session session);

	/**
	 * Returns the number of rows that match the dynamic query.
	 *
	 * @param  dynamicQuery the dynamic query
	 * @return the number of rows that match the dynamic query
	 */
	public long countWithDynamicQuery(DynamicQuery dynamicQuery);

	/**
	 * Returns the number of rows that match the dynamic query.
	 *
	 * @param  dynamicQuery the dynamic query
	 * @param  projection the projection to apply to the query
	 * @return the number of rows that match the dynamic query
	 */
	public long countWithDynamicQuery(
		DynamicQuery dynamicQuery, Projection projection);

	public <R> R dslQuery(DSLQuery dslQuery);

	public <R> R dslQuery(DSLQuery dslQuery, boolean useFinderCache);

	public default int dslQueryCount(DSLQuery dslQuery) {
		Long count = dslQuery(dslQuery);

		return count.intValue();
	}

	public default int dslQueryCount(
		DSLQuery dslQuery, boolean useFinderCache) {

		Long count = dslQuery(dslQuery, useFinderCache);

		return count.intValue();
	}

	/**
	 * Returns the model instance with the primary key or returns
	 * <code>null</code> if it could not be found.
	 *
	 * @param  primaryKey the primary key of the model instance
	 * @return the model instance, or <code>null</code> if an instance of this
	 *         model with the primary key could not be found
	 */
	public T fetchByPrimaryKey(Serializable primaryKey);

	public Map<Serializable, T> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys);

	/**
	 * Returns the model instance with the primary key or throws a {@link
	 * NoSuchModelException} if it could not be found.
	 *
	 * @param  primaryKey the primary key of the model instance
	 * @return the model instance
	 */
	public T findByPrimaryKey(Serializable primaryKey)
		throws NoSuchModelException;

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param  dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	public <V> List<V> findWithDynamicQuery(DynamicQuery dynamicQuery);

	/**
	 * Performs a dynamic query on the database and returns a range of the
	 * matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link
	 * com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param  dynamicQuery the dynamic query
	 * @param  start the lower bound of the range of matching rows
	 * @param  end the upper bound of the range of matching rows (not inclusive)
	 * @return the range of matching rows
	 * @see    com.liferay.portal.kernel.dao.orm.QueryUtil#list(
	 *         com.liferay.portal.kernel.dao.orm.Query,
	 *         Dialect, int, int)
	 */
	public <V> List<V> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end);

	/**
	 * Performs a dynamic query on the database and returns an ordered range of
	 * the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link
	 * com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param  dynamicQuery the dynamic query
	 * @param  start the lower bound of the range of matching rows
	 * @param  end the upper bound of the range of matching rows (not inclusive)
	 * @param  orderByComparator the comparator to order the results by
	 *         (optionally <code>null</code>)
	 * @return the ordered range of matching rows
	 */
	public <V> List<V> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<V> orderByComparator);

	public void flush();

	public Set<String> getBadColumnNames();

	public Session getCurrentSession() throws ORMException;

	/**
	 * Returns the data source for this model.
	 *
	 * @return the data source for this model
	 * @see    #setDataSource(DataSource)
	 */
	public DataSource getDataSource();

	public DB getDB();

	public Dialect getDialect();

	/**
	 * Returns the listeners registered for this model.
	 *
	 * @return the listeners registered for this model
	 * @see    #registerListener(ModelListener)
	 */
	public ModelListener<T>[] getListeners();

	public Class<T> getModelClass();

	public Session openSession() throws ORMException;

	public SystemException processException(Exception exception);

	/**
	 * Registers a new listener for this model.
	 *
	 * <p>
	 * A model listener is notified whenever a change is made to an instance of
	 * this model, such as when one is added, updated, or removed.
	 * </p>
	 *
	 * @param listener the model listener to register
	 */
	public void registerListener(ModelListener<T> modelListener);

	/**
	 * Removes the model instance with the primary key from the database. Also
	 * notifies the appropriate model listeners.
	 *
	 * @param  primaryKey the primary key of the model instance to remove
	 * @return the model instance that was removed
	 */
	public T remove(Serializable primaryKey) throws NoSuchModelException;

	/**
	 * Removes the model instance from the database. Also notifies the
	 * appropriate model listeners.
	 *
	 * @param  model the model instance to remove
	 * @return the model instance that was removed
	 */
	public T remove(T model);

	public T removeByFunction(T model, Function<T, T> function);

	/**
	 * Sets the data source for this model.
	 *
	 * @param dataSource the data source to use for this model
	 */
	public void setDataSource(DataSource dataSource);

	/**
	 * Unregisters the model listener.
	 *
	 * @param listener the model listener to unregister
	 * @see   #registerListener(ModelListener)
	 */
	public void unregisterListener(ModelListener<T> modelListener);

	/**
	 * Updates the model instance in the database or adds it if it does not yet
	 * exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * Typically not called directly, use local service update model methods
	 * instead. For example, {@link
	 * com.liferay.portal.kernel.service.UserLocalServiceUtil#updateUser(
	 * com.liferay.portal.kernel.model.User)}.
	 * </p>
	 *
	 * @param  model the model instance to update
	 * @return the model instance that was updated
	 */
	public T update(T model);

	/**
	 * Updates the model instance in the database or adds it if it does not yet
	 * exist, within a different service context. Also notifies the appropriate
	 * model listeners.
	 *
	 * @param  model the model instance to update
	 * @param  serviceContext the service context to be applied
	 * @return the model instance that was updated
	 */
	public T update(T model, ServiceContext serviceContext);

}