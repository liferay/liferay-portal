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
import com.liferay.portal.kernel.exception.NoSuchPreferencesException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.PortalPreferences;
import com.liferay.portal.kernel.model.PortalPreferencesTable;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.PortalPreferencesPersistence;
import com.liferay.portal.kernel.service.persistence.PortalPreferencesUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.model.impl.PortalPreferencesImpl;
import com.liferay.portal.model.impl.PortalPreferencesModelImpl;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.List;
import java.util.Map;

/**
 * The persistence implementation for the portal preferences service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class PortalPreferencesPersistenceImpl
	extends BasePersistenceImpl<PortalPreferences, NoSuchPreferencesException>
	implements PortalPreferencesPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>PortalPreferencesUtil</code> to access the portal preferences persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		PortalPreferencesImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<PortalPreferences, NoSuchPreferencesException>
			_collectionPersistenceFinderByOwnerType;

	/**
	 * Returns an ordered range of all the portal preferenceses where ownerType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PortalPreferencesModelImpl</code>.
	 * </p>
	 *
	 * @param ownerType the owner type
	 * @param start the lower bound of the range of portal preferenceses
	 * @param end the upper bound of the range of portal preferenceses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching portal preferenceses
	 */
	@Override
	public List<PortalPreferences> findByOwnerType(
		int ownerType, int start, int end,
		OrderByComparator<PortalPreferences> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByOwnerType.find(
			FinderCacheUtil.getFinderCache(), new Object[] {ownerType}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first portal preferences in the ordered set where ownerType = &#63;.
	 *
	 * @param ownerType the owner type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portal preferences
	 * @throws NoSuchPreferencesException if a matching portal preferences could not be found
	 */
	@Override
	public PortalPreferences findByOwnerType_First(
			int ownerType,
			OrderByComparator<PortalPreferences> orderByComparator)
		throws NoSuchPreferencesException {

		return _collectionPersistenceFinderByOwnerType.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {ownerType},
			orderByComparator);
	}

	/**
	 * Returns the first portal preferences in the ordered set where ownerType = &#63;.
	 *
	 * @param ownerType the owner type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching portal preferences, or <code>null</code> if a matching portal preferences could not be found
	 */
	@Override
	public PortalPreferences fetchByOwnerType_First(
		int ownerType, OrderByComparator<PortalPreferences> orderByComparator) {

		return _collectionPersistenceFinderByOwnerType.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {ownerType},
			orderByComparator);
	}

	/**
	 * Removes all the portal preferenceses where ownerType = &#63; from the database.
	 *
	 * @param ownerType the owner type
	 */
	@Override
	public void removeByOwnerType(int ownerType) {
		_collectionPersistenceFinderByOwnerType.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {ownerType});
	}

	/**
	 * Returns the number of portal preferenceses where ownerType = &#63;.
	 *
	 * @param ownerType the owner type
	 * @return the number of matching portal preferenceses
	 */
	@Override
	public int countByOwnerType(int ownerType) {
		return _collectionPersistenceFinderByOwnerType.count(
			FinderCacheUtil.getFinderCache(), new Object[] {ownerType});
	}

	private UniquePersistenceFinder
		<PortalPreferences, NoSuchPreferencesException>
			_uniquePersistenceFinderByO_O;

	/**
	 * Returns the portal preferences where ownerId = &#63; and ownerType = &#63; or throws a <code>NoSuchPreferencesException</code> if it could not be found.
	 *
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @return the matching portal preferences
	 * @throws NoSuchPreferencesException if a matching portal preferences could not be found
	 */
	@Override
	public PortalPreferences findByO_O(long ownerId, int ownerType)
		throws NoSuchPreferencesException {

		return _uniquePersistenceFinderByO_O.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {ownerId, ownerType});
	}

	/**
	 * Returns the portal preferences where ownerId = &#63; and ownerType = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching portal preferences, or <code>null</code> if a matching portal preferences could not be found
	 */
	@Override
	public PortalPreferences fetchByO_O(
		long ownerId, int ownerType, boolean useFinderCache) {

		return _uniquePersistenceFinderByO_O.fetch(
			FinderCacheUtil.getFinderCache(), new Object[] {ownerId, ownerType},
			useFinderCache);
	}

	/**
	 * Removes the portal preferences where ownerId = &#63; and ownerType = &#63; from the database.
	 *
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @return the portal preferences that was removed
	 */
	@Override
	public PortalPreferences removeByO_O(long ownerId, int ownerType)
		throws NoSuchPreferencesException {

		PortalPreferences portalPreferences = findByO_O(ownerId, ownerType);

		return remove(portalPreferences);
	}

	/**
	 * Returns the number of portal preferenceses where ownerId = &#63; and ownerType = &#63;.
	 *
	 * @param ownerId the owner ID
	 * @param ownerType the owner type
	 * @return the number of matching portal preferenceses
	 */
	@Override
	public int countByO_O(long ownerId, int ownerType) {
		return _uniquePersistenceFinderByO_O.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {ownerId, ownerType});
	}

	public PortalPreferencesPersistenceImpl() {
		setModelClass(PortalPreferences.class);

		setModelImplClass(PortalPreferencesImpl.class);
		setModelPKClass(long.class);

		setTable(PortalPreferencesTable.INSTANCE);
	}

	/**
	 * Creates a new portal preferences with the primary key. Does not add the portal preferences to the database.
	 *
	 * @param portalPreferencesId the primary key for the new portal preferences
	 * @return the new portal preferences
	 */
	@Override
	public PortalPreferences create(long portalPreferencesId) {
		PortalPreferences portalPreferences = new PortalPreferencesImpl();

		portalPreferences.setNew(true);
		portalPreferences.setPrimaryKey(portalPreferencesId);

		portalPreferences.setCompanyId(CompanyThreadLocal.getCompanyId());

		return portalPreferences;
	}

	/**
	 * Removes the portal preferences with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param portalPreferencesId the primary key of the portal preferences
	 * @return the portal preferences that was removed
	 * @throws NoSuchPreferencesException if a portal preferences with the primary key could not be found
	 */
	@Override
	public PortalPreferences remove(long portalPreferencesId)
		throws NoSuchPreferencesException {

		return remove((Serializable)portalPreferencesId);
	}

	@Override
	protected PortalPreferences removeImpl(
		PortalPreferences portalPreferences) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(portalPreferences)) {
				portalPreferences = (PortalPreferences)session.get(
					PortalPreferencesImpl.class,
					portalPreferences.getPrimaryKeyObj());
			}

			if (portalPreferences != null) {
				session.delete(portalPreferences);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (portalPreferences != null) {
			clearCache(portalPreferences);
		}

		return portalPreferences;
	}

	@Override
	public PortalPreferences updateImpl(PortalPreferences portalPreferences) {
		boolean isNew = portalPreferences.isNew();

		if (!(portalPreferences instanceof PortalPreferencesModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(portalPreferences.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					portalPreferences);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in portalPreferences proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom PortalPreferences implementation " +
					portalPreferences.getClass());
		}

		PortalPreferencesModelImpl portalPreferencesModelImpl =
			(PortalPreferencesModelImpl)portalPreferences;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(portalPreferences);
			}
			else {
				portalPreferences = (PortalPreferences)session.merge(
					portalPreferences);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(portalPreferences, false);

		if (isNew) {
			portalPreferences.setNew(false);
		}

		portalPreferences.resetOriginalValues();

		return portalPreferences;
	}

	/**
	 * Returns the portal preferences with the primary key or throws a <code>NoSuchPreferencesException</code> if it could not be found.
	 *
	 * @param portalPreferencesId the primary key of the portal preferences
	 * @return the portal preferences
	 * @throws NoSuchPreferencesException if a portal preferences with the primary key could not be found
	 */
	@Override
	public PortalPreferences findByPrimaryKey(long portalPreferencesId)
		throws NoSuchPreferencesException {

		return findByPrimaryKey((Serializable)portalPreferencesId);
	}

	/**
	 * Returns the portal preferences with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param portalPreferencesId the primary key of the portal preferences
	 * @return the portal preferences, or <code>null</code> if a portal preferences with the primary key could not be found
	 */
	@Override
	public PortalPreferences fetchByPrimaryKey(long portalPreferencesId) {
		return fetchByPrimaryKey((Serializable)portalPreferencesId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return EntityCacheUtil.getEntityCache();
	}

	@Override
	protected String getPKDBName() {
		return "portalPreferencesId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_PORTALPREFERENCES;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return PortalPreferencesModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the portal preferences persistence.
	 */
	public void afterPropertiesSet() {
		_collectionPersistenceFinderByOwnerType =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByOwnerType",
					new String[] {
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"ownerType"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByOwnerType", new String[] {Integer.class.getName()},
					new String[] {"ownerType"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByOwnerType", new String[] {Integer.class.getName()},
					new String[] {"ownerType"}, false),
				_SQL_SELECT_PORTALPREFERENCES_WHERE,
				_SQL_COUNT_PORTALPREFERENCES_WHERE,
				PortalPreferencesModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"portalPreferences.", "ownerType",
					FinderColumn.Type.INTEGER, "=", true, true,
					PortalPreferences::getOwnerType));

		_uniquePersistenceFinderByO_O = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByO_O",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"ownerId", "ownerType"}, 0, 0, false,
				PortalPreferences::getOwnerId, PortalPreferences::getOwnerType),
			_SQL_SELECT_PORTALPREFERENCES_WHERE, "",
			new FinderColumn<>(
				"portalPreferences.", "ownerId", FinderColumn.Type.LONG, "=",
				true, true, PortalPreferences::getOwnerId),
			new FinderColumn<>(
				"portalPreferences.", "ownerType", FinderColumn.Type.INTEGER,
				"=", true, true, PortalPreferences::getOwnerType));

		PortalPreferencesUtil.setPersistence(this);
	}

	public void destroy() {
		PortalPreferencesUtil.setPersistence(null);

		EntityCacheUtil.removeCache(PortalPreferencesImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		PortalPreferencesModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_PORTALPREFERENCES =
		"SELECT portalPreferences FROM PortalPreferences portalPreferences";

	private static final String _SQL_SELECT_PORTALPREFERENCES_WHERE =
		"SELECT portalPreferences FROM PortalPreferences portalPreferences WHERE ";

	private static final String _SQL_COUNT_PORTALPREFERENCES_WHERE =
		"SELECT COUNT(portalPreferences) FROM PortalPreferences portalPreferences WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No PortalPreferences exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		PortalPreferencesPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1996021193