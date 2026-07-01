/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.tax.service.persistence.impl;

import com.liferay.commerce.tax.exception.NoSuchTaxMethodException;
import com.liferay.commerce.tax.model.CommerceTaxMethod;
import com.liferay.commerce.tax.model.CommerceTaxMethodTable;
import com.liferay.commerce.tax.model.impl.CommerceTaxMethodImpl;
import com.liferay.commerce.tax.model.impl.CommerceTaxMethodModelImpl;
import com.liferay.commerce.tax.service.persistence.CommerceTaxMethodPersistence;
import com.liferay.commerce.tax.service.persistence.CommerceTaxMethodUtil;
import com.liferay.commerce.tax.service.persistence.impl.constants.CommercePersistenceConstants;
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
 * The persistence implementation for the commerce tax method service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = CommerceTaxMethodPersistence.class)
public class CommerceTaxMethodPersistenceImpl
	extends BasePersistenceImpl<CommerceTaxMethod, NoSuchTaxMethodException>
	implements CommerceTaxMethodPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CommerceTaxMethodUtil</code> to access the commerce tax method persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CommerceTaxMethodImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CommerceTaxMethod, NoSuchTaxMethodException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the commerce tax methods where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTaxMethodModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of commerce tax methods
	 * @param end the upper bound of the range of commerce tax methods (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce tax methods
	 */
	@Override
	public List<CommerceTaxMethod> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<CommerceTaxMethod> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first commerce tax method in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce tax method
	 * @throws NoSuchTaxMethodException if a matching commerce tax method could not be found
	 */
	@Override
	public CommerceTaxMethod findByGroupId_First(
			long groupId,
			OrderByComparator<CommerceTaxMethod> orderByComparator)
		throws NoSuchTaxMethodException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns the first commerce tax method in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce tax method, or <code>null</code> if a matching commerce tax method could not be found
	 */
	@Override
	public CommerceTaxMethod fetchByGroupId_First(
		long groupId, OrderByComparator<CommerceTaxMethod> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Removes all the commerce tax methods where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of commerce tax methods where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching commerce tax methods
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {groupId});
	}

	private UniquePersistenceFinder<CommerceTaxMethod, NoSuchTaxMethodException>
		_uniquePersistenceFinderByG_E;

	/**
	 * Returns the commerce tax method where groupId = &#63; and engineKey = &#63; or throws a <code>NoSuchTaxMethodException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param engineKey the engine key
	 * @return the matching commerce tax method
	 * @throws NoSuchTaxMethodException if a matching commerce tax method could not be found
	 */
	@Override
	public CommerceTaxMethod findByG_E(long groupId, String engineKey)
		throws NoSuchTaxMethodException {

		return _uniquePersistenceFinderByG_E.find(
			finderCache, new Object[] {groupId, engineKey});
	}

	/**
	 * Returns the commerce tax method where groupId = &#63; and engineKey = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param engineKey the engine key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce tax method, or <code>null</code> if a matching commerce tax method could not be found
	 */
	@Override
	public CommerceTaxMethod fetchByG_E(
		long groupId, String engineKey, boolean useFinderCache) {

		return _uniquePersistenceFinderByG_E.fetch(
			finderCache, new Object[] {groupId, engineKey}, useFinderCache);
	}

	/**
	 * Removes the commerce tax method where groupId = &#63; and engineKey = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param engineKey the engine key
	 * @return the commerce tax method that was removed
	 */
	@Override
	public CommerceTaxMethod removeByG_E(long groupId, String engineKey)
		throws NoSuchTaxMethodException {

		CommerceTaxMethod commerceTaxMethod = findByG_E(groupId, engineKey);

		return remove(commerceTaxMethod);
	}

	/**
	 * Returns the number of commerce tax methods where groupId = &#63; and engineKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param engineKey the engine key
	 * @return the number of matching commerce tax methods
	 */
	@Override
	public int countByG_E(long groupId, String engineKey) {
		return _uniquePersistenceFinderByG_E.count(
			finderCache, new Object[] {groupId, engineKey});
	}

	private CollectionPersistenceFinder
		<CommerceTaxMethod, NoSuchTaxMethodException>
			_collectionPersistenceFinderByG_A;

	/**
	 * Returns an ordered range of all the commerce tax methods where groupId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceTaxMethodModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @param start the lower bound of the range of commerce tax methods
	 * @param end the upper bound of the range of commerce tax methods (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce tax methods
	 */
	@Override
	public List<CommerceTaxMethod> findByG_A(
		long groupId, boolean active, int start, int end,
		OrderByComparator<CommerceTaxMethod> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_A.find(
			finderCache, new Object[] {groupId, active}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce tax method in the ordered set where groupId = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce tax method
	 * @throws NoSuchTaxMethodException if a matching commerce tax method could not be found
	 */
	@Override
	public CommerceTaxMethod findByG_A_First(
			long groupId, boolean active,
			OrderByComparator<CommerceTaxMethod> orderByComparator)
		throws NoSuchTaxMethodException {

		return _collectionPersistenceFinderByG_A.findFirst(
			finderCache, new Object[] {groupId, active}, orderByComparator);
	}

	/**
	 * Returns the first commerce tax method in the ordered set where groupId = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce tax method, or <code>null</code> if a matching commerce tax method could not be found
	 */
	@Override
	public CommerceTaxMethod fetchByG_A_First(
		long groupId, boolean active,
		OrderByComparator<CommerceTaxMethod> orderByComparator) {

		return _collectionPersistenceFinderByG_A.fetchFirst(
			finderCache, new Object[] {groupId, active}, orderByComparator);
	}

	/**
	 * Removes all the commerce tax methods where groupId = &#63; and active = &#63; from the database.
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
	 * Returns the number of commerce tax methods where groupId = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @return the number of matching commerce tax methods
	 */
	@Override
	public int countByG_A(long groupId, boolean active) {
		return _collectionPersistenceFinderByG_A.count(
			finderCache, new Object[] {groupId, active});
	}

	public CommerceTaxMethodPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("active", "active_");

		setDBColumnNames(dbColumnNames);

		setModelClass(CommerceTaxMethod.class);

		setModelImplClass(CommerceTaxMethodImpl.class);
		setModelPKClass(long.class);

		setTable(CommerceTaxMethodTable.INSTANCE);
	}

	/**
	 * Creates a new commerce tax method with the primary key. Does not add the commerce tax method to the database.
	 *
	 * @param commerceTaxMethodId the primary key for the new commerce tax method
	 * @return the new commerce tax method
	 */
	@Override
	public CommerceTaxMethod create(long commerceTaxMethodId) {
		CommerceTaxMethod commerceTaxMethod = new CommerceTaxMethodImpl();

		commerceTaxMethod.setNew(true);
		commerceTaxMethod.setPrimaryKey(commerceTaxMethodId);

		commerceTaxMethod.setCompanyId(CompanyThreadLocal.getCompanyId());

		return commerceTaxMethod;
	}

	/**
	 * Removes the commerce tax method with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceTaxMethodId the primary key of the commerce tax method
	 * @return the commerce tax method that was removed
	 * @throws NoSuchTaxMethodException if a commerce tax method with the primary key could not be found
	 */
	@Override
	public CommerceTaxMethod remove(long commerceTaxMethodId)
		throws NoSuchTaxMethodException {

		return remove((Serializable)commerceTaxMethodId);
	}

	@Override
	protected CommerceTaxMethod removeImpl(
		CommerceTaxMethod commerceTaxMethod) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(commerceTaxMethod)) {
				commerceTaxMethod = (CommerceTaxMethod)session.get(
					CommerceTaxMethodImpl.class,
					commerceTaxMethod.getPrimaryKeyObj());
			}

			if (commerceTaxMethod != null) {
				session.delete(commerceTaxMethod);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (commerceTaxMethod != null) {
			clearCache(commerceTaxMethod);
		}

		return commerceTaxMethod;
	}

	@Override
	public CommerceTaxMethod updateImpl(CommerceTaxMethod commerceTaxMethod) {
		boolean isNew = commerceTaxMethod.isNew();

		if (!(commerceTaxMethod instanceof CommerceTaxMethodModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(commerceTaxMethod.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					commerceTaxMethod);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in commerceTaxMethod proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CommerceTaxMethod implementation " +
					commerceTaxMethod.getClass());
		}

		CommerceTaxMethodModelImpl commerceTaxMethodModelImpl =
			(CommerceTaxMethodModelImpl)commerceTaxMethod;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (commerceTaxMethod.getCreateDate() == null)) {
			if (serviceContext == null) {
				commerceTaxMethod.setCreateDate(date);
			}
			else {
				commerceTaxMethod.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!commerceTaxMethodModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				commerceTaxMethod.setModifiedDate(date);
			}
			else {
				commerceTaxMethod.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(commerceTaxMethod);
			}
			else {
				commerceTaxMethod = (CommerceTaxMethod)session.merge(
					commerceTaxMethod);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(commerceTaxMethod, false);

		if (isNew) {
			commerceTaxMethod.setNew(false);
		}

		commerceTaxMethod.resetOriginalValues();

		return commerceTaxMethod;
	}

	/**
	 * Returns the commerce tax method with the primary key or throws a <code>NoSuchTaxMethodException</code> if it could not be found.
	 *
	 * @param commerceTaxMethodId the primary key of the commerce tax method
	 * @return the commerce tax method
	 * @throws NoSuchTaxMethodException if a commerce tax method with the primary key could not be found
	 */
	@Override
	public CommerceTaxMethod findByPrimaryKey(long commerceTaxMethodId)
		throws NoSuchTaxMethodException {

		return findByPrimaryKey((Serializable)commerceTaxMethodId);
	}

	/**
	 * Returns the commerce tax method with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceTaxMethodId the primary key of the commerce tax method
	 * @return the commerce tax method, or <code>null</code> if a commerce tax method with the primary key could not be found
	 */
	@Override
	public CommerceTaxMethod fetchByPrimaryKey(long commerceTaxMethodId) {
		return fetchByPrimaryKey((Serializable)commerceTaxMethodId);
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
		return "commerceTaxMethodId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMMERCETAXMETHOD;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CommerceTaxMethodModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the commerce tax method persistence.
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
				_SQL_SELECT_COMMERCETAXMETHOD_WHERE,
				_SQL_COUNT_COMMERCETAXMETHOD_WHERE,
				CommerceTaxMethodModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"commerceTaxMethod.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, CommerceTaxMethod::getGroupId));

		_uniquePersistenceFinderByG_E = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_E",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"groupId", "engineKey"}, 0, 2, false,
				CommerceTaxMethod::getGroupId,
				convertNullFunction(CommerceTaxMethod::getEngineKey)),
			_SQL_SELECT_COMMERCETAXMETHOD_WHERE, "",
			new FinderColumn<>(
				"commerceTaxMethod.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, CommerceTaxMethod::getGroupId),
			new FinderColumn<>(
				"commerceTaxMethod.", "engineKey", FinderColumn.Type.STRING,
				"=", true, true, CommerceTaxMethod::getEngineKey));

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
			_SQL_SELECT_COMMERCETAXMETHOD_WHERE,
			_SQL_COUNT_COMMERCETAXMETHOD_WHERE,
			CommerceTaxMethodModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"commerceTaxMethod.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, CommerceTaxMethod::getGroupId),
			new FinderColumn<>(
				"commerceTaxMethod.", "active", "active_",
				FinderColumn.Type.BOOLEAN, "=", true, true,
				CommerceTaxMethod::isActive));

		CommerceTaxMethodUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CommerceTaxMethodUtil.setPersistence(null);

		entityCache.removeCache(CommerceTaxMethodImpl.class.getName());
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
		CommerceTaxMethodModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_COMMERCETAXMETHOD =
		"SELECT commerceTaxMethod FROM CommerceTaxMethod commerceTaxMethod";

	private static final String _SQL_SELECT_COMMERCETAXMETHOD_WHERE =
		"SELECT commerceTaxMethod FROM CommerceTaxMethod commerceTaxMethod WHERE ";

	private static final String _SQL_COUNT_COMMERCETAXMETHOD_WHERE =
		"SELECT COUNT(commerceTaxMethod) FROM CommerceTaxMethod commerceTaxMethod WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CommerceTaxMethod exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceTaxMethodPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"active"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1364945369