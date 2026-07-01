/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.persistence.impl;

import com.liferay.commerce.exception.NoSuchShippingMethodException;
import com.liferay.commerce.model.CommerceShippingMethod;
import com.liferay.commerce.model.CommerceShippingMethodTable;
import com.liferay.commerce.model.impl.CommerceShippingMethodImpl;
import com.liferay.commerce.model.impl.CommerceShippingMethodModelImpl;
import com.liferay.commerce.service.persistence.CommerceShippingMethodPersistence;
import com.liferay.commerce.service.persistence.CommerceShippingMethodUtil;
import com.liferay.commerce.service.persistence.impl.constants.CommercePersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the commerce shipping method service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Alessio Antonio Rendina
 * @generated
 */
@Component(service = CommerceShippingMethodPersistence.class)
public class CommerceShippingMethodPersistenceImpl
	extends BasePersistenceImpl
		<CommerceShippingMethod, NoSuchShippingMethodException>
	implements CommerceShippingMethodPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CommerceShippingMethodUtil</code> to access the commerce shipping method persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CommerceShippingMethodImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CommerceShippingMethod, NoSuchShippingMethodException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the commerce shipping methods where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShippingMethodModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of commerce shipping methods
	 * @param end the upper bound of the range of commerce shipping methods (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce shipping methods
	 */
	@Override
	public List<CommerceShippingMethod> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<CommerceShippingMethod> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first commerce shipping method in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipping method
	 * @throws NoSuchShippingMethodException if a matching commerce shipping method could not be found
	 */
	@Override
	public CommerceShippingMethod findByGroupId_First(
			long groupId,
			OrderByComparator<CommerceShippingMethod> orderByComparator)
		throws NoSuchShippingMethodException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns the first commerce shipping method in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipping method, or <code>null</code> if a matching commerce shipping method could not be found
	 */
	@Override
	public CommerceShippingMethod fetchByGroupId_First(
		long groupId,
		OrderByComparator<CommerceShippingMethod> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Removes all the commerce shipping methods where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of commerce shipping methods where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching commerce shipping methods
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {groupId});
	}

	private CollectionPersistenceFinder
		<CommerceShippingMethod, NoSuchShippingMethodException>
			_collectionPersistenceFinderByG_A;

	/**
	 * Returns an ordered range of all the commerce shipping methods where groupId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceShippingMethodModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @param start the lower bound of the range of commerce shipping methods
	 * @param end the upper bound of the range of commerce shipping methods (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce shipping methods
	 */
	@Override
	public List<CommerceShippingMethod> findByG_A(
		long groupId, boolean active, int start, int end,
		OrderByComparator<CommerceShippingMethod> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_A.find(
			finderCache, new Object[] {groupId, active}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce shipping method in the ordered set where groupId = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipping method
	 * @throws NoSuchShippingMethodException if a matching commerce shipping method could not be found
	 */
	@Override
	public CommerceShippingMethod findByG_A_First(
			long groupId, boolean active,
			OrderByComparator<CommerceShippingMethod> orderByComparator)
		throws NoSuchShippingMethodException {

		return _collectionPersistenceFinderByG_A.findFirst(
			finderCache, new Object[] {groupId, active}, orderByComparator);
	}

	/**
	 * Returns the first commerce shipping method in the ordered set where groupId = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce shipping method, or <code>null</code> if a matching commerce shipping method could not be found
	 */
	@Override
	public CommerceShippingMethod fetchByG_A_First(
		long groupId, boolean active,
		OrderByComparator<CommerceShippingMethod> orderByComparator) {

		return _collectionPersistenceFinderByG_A.fetchFirst(
			finderCache, new Object[] {groupId, active}, orderByComparator);
	}

	/**
	 * Removes all the commerce shipping methods where groupId = &#63; and active = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param active the active
	 */
	@Override
	public void removeByG_A(long groupId, boolean active) {
		_collectionPersistenceFinderByG_A.remove(
			finderCache, new Object[] {groupId, active});
	}

	/**
	 * Returns the number of commerce shipping methods where groupId = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @return the number of matching commerce shipping methods
	 */
	@Override
	public int countByG_A(long groupId, boolean active) {
		return _collectionPersistenceFinderByG_A.count(
			finderCache, new Object[] {groupId, active});
	}

	private UniquePersistenceFinder
		<CommerceShippingMethod, NoSuchShippingMethodException>
			_uniquePersistenceFinderByG_E;

	/**
	 * Returns the commerce shipping method where groupId = &#63; and engineKey = &#63; or throws a <code>NoSuchShippingMethodException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param engineKey the engine key
	 * @return the matching commerce shipping method
	 * @throws NoSuchShippingMethodException if a matching commerce shipping method could not be found
	 */
	@Override
	public CommerceShippingMethod findByG_E(long groupId, String engineKey)
		throws NoSuchShippingMethodException {

		return _uniquePersistenceFinderByG_E.find(
			finderCache, new Object[] {groupId, engineKey});
	}

	/**
	 * Returns the commerce shipping method where groupId = &#63; and engineKey = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param engineKey the engine key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce shipping method, or <code>null</code> if a matching commerce shipping method could not be found
	 */
	@Override
	public CommerceShippingMethod fetchByG_E(
		long groupId, String engineKey, boolean useFinderCache) {

		return _uniquePersistenceFinderByG_E.fetch(
			finderCache, new Object[] {groupId, engineKey}, useFinderCache);
	}

	/**
	 * Removes the commerce shipping method where groupId = &#63; and engineKey = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param engineKey the engine key
	 * @return the commerce shipping method that was removed
	 */
	@Override
	public CommerceShippingMethod removeByG_E(long groupId, String engineKey)
		throws NoSuchShippingMethodException {

		CommerceShippingMethod commerceShippingMethod = findByG_E(
			groupId, engineKey);

		return remove(commerceShippingMethod);
	}

	/**
	 * Returns the number of commerce shipping methods where groupId = &#63; and engineKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param engineKey the engine key
	 * @return the number of matching commerce shipping methods
	 */
	@Override
	public int countByG_E(long groupId, String engineKey) {
		return _uniquePersistenceFinderByG_E.count(
			finderCache, new Object[] {groupId, engineKey});
	}

	public CommerceShippingMethodPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("active", "active_");

		setDBColumnNames(dbColumnNames);

		setModelClass(CommerceShippingMethod.class);

		setModelImplClass(CommerceShippingMethodImpl.class);
		setModelPKClass(long.class);

		setTable(CommerceShippingMethodTable.INSTANCE);
	}

	/**
	 * Creates a new commerce shipping method with the primary key. Does not add the commerce shipping method to the database.
	 *
	 * @param commerceShippingMethodId the primary key for the new commerce shipping method
	 * @return the new commerce shipping method
	 */
	@Override
	public CommerceShippingMethod create(long commerceShippingMethodId) {
		CommerceShippingMethod commerceShippingMethod =
			new CommerceShippingMethodImpl();

		commerceShippingMethod.setNew(true);
		commerceShippingMethod.setPrimaryKey(commerceShippingMethodId);

		commerceShippingMethod.setCompanyId(CompanyThreadLocal.getCompanyId());

		return commerceShippingMethod;
	}

	/**
	 * Removes the commerce shipping method with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceShippingMethodId the primary key of the commerce shipping method
	 * @return the commerce shipping method that was removed
	 * @throws NoSuchShippingMethodException if a commerce shipping method with the primary key could not be found
	 */
	@Override
	public CommerceShippingMethod remove(long commerceShippingMethodId)
		throws NoSuchShippingMethodException {

		return remove((Serializable)commerceShippingMethodId);
	}

	@Override
	protected CommerceShippingMethod removeImpl(
		CommerceShippingMethod commerceShippingMethod) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(commerceShippingMethod)) {
				commerceShippingMethod = (CommerceShippingMethod)session.get(
					CommerceShippingMethodImpl.class,
					commerceShippingMethod.getPrimaryKeyObj());
			}

			if (commerceShippingMethod != null) {
				session.delete(commerceShippingMethod);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (commerceShippingMethod != null) {
			clearCache(commerceShippingMethod);
		}

		return commerceShippingMethod;
	}

	@Override
	public CommerceShippingMethod updateImpl(
		CommerceShippingMethod commerceShippingMethod) {

		boolean isNew = commerceShippingMethod.isNew();

		if (!(commerceShippingMethod instanceof
				CommerceShippingMethodModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(commerceShippingMethod.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					commerceShippingMethod);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in commerceShippingMethod proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CommerceShippingMethod implementation " +
					commerceShippingMethod.getClass());
		}

		CommerceShippingMethodModelImpl commerceShippingMethodModelImpl =
			(CommerceShippingMethodModelImpl)commerceShippingMethod;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (commerceShippingMethod.getCreateDate() == null)) {
			if (serviceContext == null) {
				commerceShippingMethod.setCreateDate(date);
			}
			else {
				commerceShippingMethod.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!commerceShippingMethodModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				commerceShippingMethod.setModifiedDate(date);
			}
			else {
				commerceShippingMethod.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(commerceShippingMethod);
			}
			else {
				commerceShippingMethod = (CommerceShippingMethod)session.merge(
					commerceShippingMethod);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(commerceShippingMethod, false);

		if (isNew) {
			commerceShippingMethod.setNew(false);
		}

		commerceShippingMethod.resetOriginalValues();

		return commerceShippingMethod;
	}

	/**
	 * Returns the commerce shipping method with the primary key or throws a <code>NoSuchShippingMethodException</code> if it could not be found.
	 *
	 * @param commerceShippingMethodId the primary key of the commerce shipping method
	 * @return the commerce shipping method
	 * @throws NoSuchShippingMethodException if a commerce shipping method with the primary key could not be found
	 */
	@Override
	public CommerceShippingMethod findByPrimaryKey(
			long commerceShippingMethodId)
		throws NoSuchShippingMethodException {

		return findByPrimaryKey((Serializable)commerceShippingMethodId);
	}

	/**
	 * Returns the commerce shipping method with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceShippingMethodId the primary key of the commerce shipping method
	 * @return the commerce shipping method, or <code>null</code> if a commerce shipping method with the primary key could not be found
	 */
	@Override
	public CommerceShippingMethod fetchByPrimaryKey(
		long commerceShippingMethodId) {

		return fetchByPrimaryKey((Serializable)commerceShippingMethodId);
	}

	@Override
	public Set<String> getBadColumnNames() {
		return _badColumnNames;
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "commerceShippingMethodId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMMERCESHIPPINGMETHOD;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CommerceShippingMethodModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the commerce shipping method persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByGroupId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByGroupId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByGroupId",
					new String[] {Long.class.getName()},
					new String[] {"groupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByGroupId",
					new String[] {Long.class.getName()},
					new String[] {"groupId"}, false),
				_SQL_SELECT_COMMERCESHIPPINGMETHOD_WHERE,
				_SQL_COUNT_COMMERCESHIPPINGMETHOD_WHERE,
				CommerceShippingMethodModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"commerceShippingMethod.", "groupId",
					FinderColumn.Type.LONG, "=", true, true,
					CommerceShippingMethod::getGroupId));

		_collectionPersistenceFinderByG_A = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_A",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"groupId", "active_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_A",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"groupId", "active_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_A",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"groupId", "active_"}, false),
			_SQL_SELECT_COMMERCESHIPPINGMETHOD_WHERE,
			_SQL_COUNT_COMMERCESHIPPINGMETHOD_WHERE,
			CommerceShippingMethodModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "",
			new FinderColumn<>(
				"commerceShippingMethod.", "groupId", FinderColumn.Type.LONG,
				"=", true, true, CommerceShippingMethod::getGroupId),
			new FinderColumn<>(
				"commerceShippingMethod.", "active", "active_",
				FinderColumn.Type.BOOLEAN, "=", true, true,
				CommerceShippingMethod::isActive));

		_uniquePersistenceFinderByG_E = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_E",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"groupId", "engineKey"}, 0, 2, false,
				CommerceShippingMethod::getGroupId,
				convertNullFunction(CommerceShippingMethod::getEngineKey)),
			_SQL_SELECT_COMMERCESHIPPINGMETHOD_WHERE, "",
			new FinderColumn<>(
				"commerceShippingMethod.", "groupId", FinderColumn.Type.LONG,
				"=", true, true, CommerceShippingMethod::getGroupId),
			new FinderColumn<>(
				"commerceShippingMethod.", "engineKey",
				FinderColumn.Type.STRING, "=", true, true,
				CommerceShippingMethod::getEngineKey));

		CommerceShippingMethodUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CommerceShippingMethodUtil.setPersistence(null);

		entityCache.removeCache(CommerceShippingMethodImpl.class.getName());
	}

	@Override
	@Reference(
		target = CommercePersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = CommercePersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = CommercePersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _ENTITY_ALIAS_PREFIX =
		CommerceShippingMethodModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_COMMERCESHIPPINGMETHOD =
		"SELECT commerceShippingMethod FROM CommerceShippingMethod commerceShippingMethod";

	private static final String _SQL_SELECT_COMMERCESHIPPINGMETHOD_WHERE =
		"SELECT commerceShippingMethod FROM CommerceShippingMethod commerceShippingMethod WHERE ";

	private static final String _SQL_COUNT_COMMERCESHIPPINGMETHOD_WHERE =
		"SELECT COUNT(commerceShippingMethod) FROM CommerceShippingMethod commerceShippingMethod WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CommerceShippingMethod exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceShippingMethodPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"active"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1025055997