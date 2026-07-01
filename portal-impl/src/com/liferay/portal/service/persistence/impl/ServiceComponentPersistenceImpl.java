/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.impl;

import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.NoSuchServiceComponentException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ServiceComponent;
import com.liferay.portal.kernel.model.ServiceComponentTable;
import com.liferay.portal.kernel.service.persistence.ServiceComponentPersistence;
import com.liferay.portal.kernel.service.persistence.ServiceComponentUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.model.impl.ServiceComponentImpl;
import com.liferay.portal.model.impl.ServiceComponentModelImpl;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence implementation for the service component service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class ServiceComponentPersistenceImpl
	extends BasePersistenceImpl
		<ServiceComponent, NoSuchServiceComponentException>
	implements ServiceComponentPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ServiceComponentUtil</code> to access the service component persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ServiceComponentImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<ServiceComponent, NoSuchServiceComponentException>
			_collectionPersistenceFinderByBuildNamespace;

	/**
	 * Returns an ordered range of all the service components where buildNamespace = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ServiceComponentModelImpl</code>.
	 * </p>
	 *
	 * @param buildNamespace the build namespace
	 * @param start the lower bound of the range of service components
	 * @param end the upper bound of the range of service components (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching service components
	 */
	@Override
	public List<ServiceComponent> findByBuildNamespace(
		String buildNamespace, int start, int end,
		OrderByComparator<ServiceComponent> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByBuildNamespace.find(
			FinderCacheUtil.getFinderCache(), new Object[] {buildNamespace},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first service component in the ordered set where buildNamespace = &#63;.
	 *
	 * @param buildNamespace the build namespace
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching service component
	 * @throws NoSuchServiceComponentException if a matching service component could not be found
	 */
	@Override
	public ServiceComponent findByBuildNamespace_First(
			String buildNamespace,
			OrderByComparator<ServiceComponent> orderByComparator)
		throws NoSuchServiceComponentException {

		return _collectionPersistenceFinderByBuildNamespace.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {buildNamespace},
			orderByComparator);
	}

	/**
	 * Returns the first service component in the ordered set where buildNamespace = &#63;.
	 *
	 * @param buildNamespace the build namespace
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching service component, or <code>null</code> if a matching service component could not be found
	 */
	@Override
	public ServiceComponent fetchByBuildNamespace_First(
		String buildNamespace,
		OrderByComparator<ServiceComponent> orderByComparator) {

		return _collectionPersistenceFinderByBuildNamespace.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {buildNamespace},
			orderByComparator);
	}

	/**
	 * Removes all the service components where buildNamespace = &#63; from the database.
	 *
	 * @param buildNamespace the build namespace
	 */
	@Override
	public void removeByBuildNamespace(String buildNamespace) {
		_collectionPersistenceFinderByBuildNamespace.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {buildNamespace});
	}

	/**
	 * Returns the number of service components where buildNamespace = &#63;.
	 *
	 * @param buildNamespace the build namespace
	 * @return the number of matching service components
	 */
	@Override
	public int countByBuildNamespace(String buildNamespace) {
		return _collectionPersistenceFinderByBuildNamespace.count(
			FinderCacheUtil.getFinderCache(), new Object[] {buildNamespace});
	}

	private UniquePersistenceFinder
		<ServiceComponent, NoSuchServiceComponentException>
			_uniquePersistenceFinderByBNS_BNU;

	/**
	 * Returns the service component where buildNamespace = &#63; and buildNumber = &#63; or throws a <code>NoSuchServiceComponentException</code> if it could not be found.
	 *
	 * @param buildNamespace the build namespace
	 * @param buildNumber the build number
	 * @return the matching service component
	 * @throws NoSuchServiceComponentException if a matching service component could not be found
	 */
	@Override
	public ServiceComponent findByBNS_BNU(
			String buildNamespace, long buildNumber)
		throws NoSuchServiceComponentException {

		return _uniquePersistenceFinderByBNS_BNU.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {buildNamespace, buildNumber});
	}

	/**
	 * Returns the service component where buildNamespace = &#63; and buildNumber = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param buildNamespace the build namespace
	 * @param buildNumber the build number
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching service component, or <code>null</code> if a matching service component could not be found
	 */
	@Override
	public ServiceComponent fetchByBNS_BNU(
		String buildNamespace, long buildNumber, boolean useFinderCache) {

		return _uniquePersistenceFinderByBNS_BNU.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {buildNamespace, buildNumber}, useFinderCache);
	}

	/**
	 * Removes the service component where buildNamespace = &#63; and buildNumber = &#63; from the database.
	 *
	 * @param buildNamespace the build namespace
	 * @param buildNumber the build number
	 * @return the service component that was removed
	 */
	@Override
	public ServiceComponent removeByBNS_BNU(
			String buildNamespace, long buildNumber)
		throws NoSuchServiceComponentException {

		ServiceComponent serviceComponent = findByBNS_BNU(
			buildNamespace, buildNumber);

		return remove(serviceComponent);
	}

	/**
	 * Returns the number of service components where buildNamespace = &#63; and buildNumber = &#63;.
	 *
	 * @param buildNamespace the build namespace
	 * @param buildNumber the build number
	 * @return the number of matching service components
	 */
	@Override
	public int countByBNS_BNU(String buildNamespace, long buildNumber) {
		return _uniquePersistenceFinderByBNS_BNU.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {buildNamespace, buildNumber});
	}

	public ServiceComponentPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("data", "data_");

		setDBColumnNames(dbColumnNames);

		setModelClass(ServiceComponent.class);

		setModelImplClass(ServiceComponentImpl.class);
		setModelPKClass(long.class);

		setTable(ServiceComponentTable.INSTANCE);
	}

	/**
	 * Creates a new service component with the primary key. Does not add the service component to the database.
	 *
	 * @param serviceComponentId the primary key for the new service component
	 * @return the new service component
	 */
	@Override
	public ServiceComponent create(long serviceComponentId) {
		ServiceComponent serviceComponent = new ServiceComponentImpl();

		serviceComponent.setNew(true);
		serviceComponent.setPrimaryKey(serviceComponentId);

		return serviceComponent;
	}

	/**
	 * Removes the service component with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param serviceComponentId the primary key of the service component
	 * @return the service component that was removed
	 * @throws NoSuchServiceComponentException if a service component with the primary key could not be found
	 */
	@Override
	public ServiceComponent remove(long serviceComponentId)
		throws NoSuchServiceComponentException {

		return remove((Serializable)serviceComponentId);
	}

	@Override
	protected ServiceComponent removeImpl(ServiceComponent serviceComponent) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(serviceComponent)) {
				serviceComponent = (ServiceComponent)session.get(
					ServiceComponentImpl.class,
					serviceComponent.getPrimaryKeyObj());
			}

			if (serviceComponent != null) {
				session.delete(serviceComponent);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (serviceComponent != null) {
			clearCache(serviceComponent);
		}

		return serviceComponent;
	}

	@Override
	public ServiceComponent updateImpl(ServiceComponent serviceComponent) {
		boolean isNew = serviceComponent.isNew();

		if (!(serviceComponent instanceof ServiceComponentModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(serviceComponent.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					serviceComponent);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in serviceComponent proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ServiceComponent implementation " +
					serviceComponent.getClass());
		}

		ServiceComponentModelImpl serviceComponentModelImpl =
			(ServiceComponentModelImpl)serviceComponent;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(serviceComponent);
			}
			else {
				serviceComponent = (ServiceComponent)session.merge(
					serviceComponent);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(serviceComponent, false);

		if (isNew) {
			serviceComponent.setNew(false);
		}

		serviceComponent.resetOriginalValues();

		return serviceComponent;
	}

	/**
	 * Returns the service component with the primary key or throws a <code>NoSuchServiceComponentException</code> if it could not be found.
	 *
	 * @param serviceComponentId the primary key of the service component
	 * @return the service component
	 * @throws NoSuchServiceComponentException if a service component with the primary key could not be found
	 */
	@Override
	public ServiceComponent findByPrimaryKey(long serviceComponentId)
		throws NoSuchServiceComponentException {

		return findByPrimaryKey((Serializable)serviceComponentId);
	}

	/**
	 * Returns the service component with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param serviceComponentId the primary key of the service component
	 * @return the service component, or <code>null</code> if a service component with the primary key could not be found
	 */
	@Override
	public ServiceComponent fetchByPrimaryKey(long serviceComponentId) {
		return fetchByPrimaryKey((Serializable)serviceComponentId);
	}

	@Override
	public Set<String> getBadColumnNames() {
		return _badColumnNames;
	}

	@Override
	protected EntityCache getEntityCache() {
		return EntityCacheUtil.getEntityCache();
	}

	@Override
	protected String getPKDBName() {
		return "serviceComponentId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_SERVICECOMPONENT;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return ServiceComponentModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the service component persistence.
	 */
	public void afterPropertiesSet() {
		_collectionPersistenceFinderByBuildNamespace =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByBuildNamespace",
					new String[] {
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"buildNamespace"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByBuildNamespace",
					new String[] {String.class.getName()},
					new String[] {"buildNamespace"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByBuildNamespace",
					new String[] {String.class.getName()},
					new String[] {"buildNamespace"}, 0, 1, false, null),
				_SQL_SELECT_SERVICECOMPONENT_WHERE,
				_SQL_COUNT_SERVICECOMPONENT_WHERE,
				ServiceComponentModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"serviceComponent.", "buildNamespace",
					FinderColumn.Type.STRING, "=", true, true,
					ServiceComponent::getBuildNamespace));

		_uniquePersistenceFinderByBNS_BNU = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByBNS_BNU",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"buildNamespace", "buildNumber"}, 0, 1, false,
				convertNullFunction(ServiceComponent::getBuildNamespace),
				ServiceComponent::getBuildNumber),
			_SQL_SELECT_SERVICECOMPONENT_WHERE, "",
			new FinderColumn<>(
				"serviceComponent.", "buildNamespace", FinderColumn.Type.STRING,
				"=", true, true, ServiceComponent::getBuildNamespace),
			new FinderColumn<>(
				"serviceComponent.", "buildNumber", FinderColumn.Type.LONG, "=",
				true, true, ServiceComponent::getBuildNumber));

		ServiceComponentUtil.setPersistence(this);
	}

	public void destroy() {
		ServiceComponentUtil.setPersistence(null);

		EntityCacheUtil.removeCache(ServiceComponentImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		ServiceComponentModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_SERVICECOMPONENT =
		"SELECT serviceComponent FROM ServiceComponent serviceComponent";

	private static final String _SQL_SELECT_SERVICECOMPONENT_WHERE =
		"SELECT serviceComponent FROM ServiceComponent serviceComponent WHERE ";

	private static final String _SQL_COUNT_SERVICECOMPONENT_WHERE =
		"SELECT COUNT(serviceComponent) FROM ServiceComponent serviceComponent WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No ServiceComponent exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		ServiceComponentPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"data"});

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1635077016