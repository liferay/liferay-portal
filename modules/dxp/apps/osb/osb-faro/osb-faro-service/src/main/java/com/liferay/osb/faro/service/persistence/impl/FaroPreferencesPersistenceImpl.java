/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.service.persistence.impl;

import com.liferay.osb.faro.exception.NoSuchFaroPreferencesException;
import com.liferay.osb.faro.model.FaroPreferences;
import com.liferay.osb.faro.model.FaroPreferencesTable;
import com.liferay.osb.faro.model.impl.FaroPreferencesImpl;
import com.liferay.osb.faro.model.impl.FaroPreferencesModelImpl;
import com.liferay.osb.faro.service.persistence.FaroPreferencesPersistence;
import com.liferay.osb.faro.service.persistence.FaroPreferencesUtil;
import com.liferay.osb.faro.service.persistence.impl.constants.OSBFaroPersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the faro preferences service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Matthew Kong
 * @generated
 */
@Component(service = FaroPreferencesPersistence.class)
public class FaroPreferencesPersistenceImpl
	extends BasePersistenceImpl<FaroPreferences, NoSuchFaroPreferencesException>
	implements FaroPreferencesPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>FaroPreferencesUtil</code> to access the faro preferences persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		FaroPreferencesImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<FaroPreferences, NoSuchFaroPreferencesException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the faro preferenceses where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>FaroPreferencesModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of faro preferenceses
	 * @param end the upper bound of the range of faro preferenceses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching faro preferenceses
	 */
	@Override
	public List<FaroPreferences> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<FaroPreferences> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first faro preferences in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro preferences
	 * @throws NoSuchFaroPreferencesException if a matching faro preferences could not be found
	 */
	@Override
	public FaroPreferences findByGroupId_First(
			long groupId, OrderByComparator<FaroPreferences> orderByComparator)
		throws NoSuchFaroPreferencesException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns the first faro preferences in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro preferences, or <code>null</code> if a matching faro preferences could not be found
	 */
	@Override
	public FaroPreferences fetchByGroupId_First(
		long groupId, OrderByComparator<FaroPreferences> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Removes all the faro preferenceses where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of faro preferenceses where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching faro preferenceses
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {groupId});
	}

	private UniquePersistenceFinder
		<FaroPreferences, NoSuchFaroPreferencesException>
			_uniquePersistenceFinderByG_O;

	/**
	 * Returns the faro preferences where groupId = &#63; and ownerId = &#63; or throws a <code>NoSuchFaroPreferencesException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param ownerId the owner ID
	 * @return the matching faro preferences
	 * @throws NoSuchFaroPreferencesException if a matching faro preferences could not be found
	 */
	@Override
	public FaroPreferences findByG_O(long groupId, long ownerId)
		throws NoSuchFaroPreferencesException {

		return _uniquePersistenceFinderByG_O.find(
			finderCache, new Object[] {groupId, ownerId});
	}

	/**
	 * Returns the faro preferences where groupId = &#63; and ownerId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param ownerId the owner ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching faro preferences, or <code>null</code> if a matching faro preferences could not be found
	 */
	@Override
	public FaroPreferences fetchByG_O(
		long groupId, long ownerId, boolean useFinderCache) {

		return _uniquePersistenceFinderByG_O.fetch(
			finderCache, new Object[] {groupId, ownerId}, useFinderCache);
	}

	/**
	 * Removes the faro preferences where groupId = &#63; and ownerId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param ownerId the owner ID
	 * @return the faro preferences that was removed
	 */
	@Override
	public FaroPreferences removeByG_O(long groupId, long ownerId)
		throws NoSuchFaroPreferencesException {

		FaroPreferences faroPreferences = findByG_O(groupId, ownerId);

		return remove(faroPreferences);
	}

	/**
	 * Returns the number of faro preferenceses where groupId = &#63; and ownerId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param ownerId the owner ID
	 * @return the number of matching faro preferenceses
	 */
	@Override
	public int countByG_O(long groupId, long ownerId) {
		return _uniquePersistenceFinderByG_O.count(
			finderCache, new Object[] {groupId, ownerId});
	}

	public FaroPreferencesPersistenceImpl() {
		setModelClass(FaroPreferences.class);

		setModelImplClass(FaroPreferencesImpl.class);
		setModelPKClass(long.class);

		setTable(FaroPreferencesTable.INSTANCE);
	}

	/**
	 * Creates a new faro preferences with the primary key. Does not add the faro preferences to the database.
	 *
	 * @param faroPreferencesId the primary key for the new faro preferences
	 * @return the new faro preferences
	 */
	@Override
	public FaroPreferences create(long faroPreferencesId) {
		FaroPreferences faroPreferences = new FaroPreferencesImpl();

		faroPreferences.setNew(true);
		faroPreferences.setPrimaryKey(faroPreferencesId);

		faroPreferences.setCompanyId(CompanyThreadLocal.getCompanyId());

		return faroPreferences;
	}

	/**
	 * Removes the faro preferences with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param faroPreferencesId the primary key of the faro preferences
	 * @return the faro preferences that was removed
	 * @throws NoSuchFaroPreferencesException if a faro preferences with the primary key could not be found
	 */
	@Override
	public FaroPreferences remove(long faroPreferencesId)
		throws NoSuchFaroPreferencesException {

		return remove((Serializable)faroPreferencesId);
	}

	@Override
	protected FaroPreferences removeImpl(FaroPreferences faroPreferences) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(faroPreferences)) {
				faroPreferences = (FaroPreferences)session.get(
					FaroPreferencesImpl.class,
					faroPreferences.getPrimaryKeyObj());
			}

			if (faroPreferences != null) {
				session.delete(faroPreferences);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (faroPreferences != null) {
			clearCache(faroPreferences);
		}

		return faroPreferences;
	}

	@Override
	public FaroPreferences updateImpl(FaroPreferences faroPreferences) {
		boolean isNew = faroPreferences.isNew();

		if (!(faroPreferences instanceof FaroPreferencesModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(faroPreferences.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					faroPreferences);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in faroPreferences proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom FaroPreferences implementation " +
					faroPreferences.getClass());
		}

		FaroPreferencesModelImpl faroPreferencesModelImpl =
			(FaroPreferencesModelImpl)faroPreferences;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(faroPreferences);
			}
			else {
				faroPreferences = (FaroPreferences)session.merge(
					faroPreferences);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(faroPreferences, false);

		if (isNew) {
			faroPreferences.setNew(false);
		}

		faroPreferences.resetOriginalValues();

		return faroPreferences;
	}

	/**
	 * Returns the faro preferences with the primary key or throws a <code>NoSuchFaroPreferencesException</code> if it could not be found.
	 *
	 * @param faroPreferencesId the primary key of the faro preferences
	 * @return the faro preferences
	 * @throws NoSuchFaroPreferencesException if a faro preferences with the primary key could not be found
	 */
	@Override
	public FaroPreferences findByPrimaryKey(long faroPreferencesId)
		throws NoSuchFaroPreferencesException {

		return findByPrimaryKey((Serializable)faroPreferencesId);
	}

	/**
	 * Returns the faro preferences with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param faroPreferencesId the primary key of the faro preferences
	 * @return the faro preferences, or <code>null</code> if a faro preferences with the primary key could not be found
	 */
	@Override
	public FaroPreferences fetchByPrimaryKey(long faroPreferencesId) {
		return fetchByPrimaryKey((Serializable)faroPreferencesId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "faroPreferencesId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_FAROPREFERENCES;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return FaroPreferencesModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the faro preferences persistence.
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
				_SQL_SELECT_FAROPREFERENCES_WHERE,
				_SQL_COUNT_FAROPREFERENCES_WHERE,
				FaroPreferencesModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"faroPreferences.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, FaroPreferences::getGroupId));

		_uniquePersistenceFinderByG_O = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_O",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"groupId", "ownerId"}, 0, 0, false,
				FaroPreferences::getGroupId, FaroPreferences::getOwnerId),
			_SQL_SELECT_FAROPREFERENCES_WHERE, "",
			new FinderColumn<>(
				"faroPreferences.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, FaroPreferences::getGroupId),
			new FinderColumn<>(
				"faroPreferences.", "ownerId", FinderColumn.Type.LONG, "=",
				true, true, FaroPreferences::getOwnerId));

		FaroPreferencesUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		FaroPreferencesUtil.setPersistence(null);

		entityCache.removeCache(FaroPreferencesImpl.class.getName());
	}

	@Override
	@Reference(
		target = OSBFaroPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = OSBFaroPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = OSBFaroPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		FaroPreferencesModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_FAROPREFERENCES =
		"SELECT faroPreferences FROM FaroPreferences faroPreferences";

	private static final String _SQL_SELECT_FAROPREFERENCES_WHERE =
		"SELECT faroPreferences FROM FaroPreferences faroPreferences WHERE ";

	private static final String _SQL_COUNT_FAROPREFERENCES_WHERE =
		"SELECT COUNT(faroPreferences) FROM FaroPreferences faroPreferences WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-191237047