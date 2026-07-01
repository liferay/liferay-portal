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
import com.liferay.portal.kernel.exception.NoSuchUserTrackerPathException;
import com.liferay.portal.kernel.model.UserTrackerPath;
import com.liferay.portal.kernel.model.UserTrackerPathTable;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.UserTrackerPathPersistence;
import com.liferay.portal.kernel.service.persistence.UserTrackerPathUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.model.impl.UserTrackerPathImpl;
import com.liferay.portal.model.impl.UserTrackerPathModelImpl;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence implementation for the user tracker path service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class UserTrackerPathPersistenceImpl
	extends BasePersistenceImpl<UserTrackerPath, NoSuchUserTrackerPathException>
	implements UserTrackerPathPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>UserTrackerPathUtil</code> to access the user tracker path persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		UserTrackerPathImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<UserTrackerPath, NoSuchUserTrackerPathException>
			_collectionPersistenceFinderByUserTrackerId;

	/**
	 * Returns an ordered range of all the user tracker paths where userTrackerId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserTrackerPathModelImpl</code>.
	 * </p>
	 *
	 * @param userTrackerId the user tracker ID
	 * @param start the lower bound of the range of user tracker paths
	 * @param end the upper bound of the range of user tracker paths (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching user tracker paths
	 */
	@Override
	public List<UserTrackerPath> findByUserTrackerId(
		long userTrackerId, int start, int end,
		OrderByComparator<UserTrackerPath> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUserTrackerId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {userTrackerId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first user tracker path in the ordered set where userTrackerId = &#63;.
	 *
	 * @param userTrackerId the user tracker ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user tracker path
	 * @throws NoSuchUserTrackerPathException if a matching user tracker path could not be found
	 */
	@Override
	public UserTrackerPath findByUserTrackerId_First(
			long userTrackerId,
			OrderByComparator<UserTrackerPath> orderByComparator)
		throws NoSuchUserTrackerPathException {

		return _collectionPersistenceFinderByUserTrackerId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {userTrackerId},
			orderByComparator);
	}

	/**
	 * Returns the first user tracker path in the ordered set where userTrackerId = &#63;.
	 *
	 * @param userTrackerId the user tracker ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user tracker path, or <code>null</code> if a matching user tracker path could not be found
	 */
	@Override
	public UserTrackerPath fetchByUserTrackerId_First(
		long userTrackerId,
		OrderByComparator<UserTrackerPath> orderByComparator) {

		return _collectionPersistenceFinderByUserTrackerId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {userTrackerId},
			orderByComparator);
	}

	/**
	 * Removes all the user tracker paths where userTrackerId = &#63; from the database.
	 *
	 * @param userTrackerId the user tracker ID
	 */
	@Override
	public void removeByUserTrackerId(long userTrackerId) {
		_collectionPersistenceFinderByUserTrackerId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {userTrackerId});
	}

	/**
	 * Returns the number of user tracker paths where userTrackerId = &#63;.
	 *
	 * @param userTrackerId the user tracker ID
	 * @return the number of matching user tracker paths
	 */
	@Override
	public int countByUserTrackerId(long userTrackerId) {
		return _collectionPersistenceFinderByUserTrackerId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {userTrackerId});
	}

	public UserTrackerPathPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("path", "path_");

		setDBColumnNames(dbColumnNames);

		setModelClass(UserTrackerPath.class);

		setModelImplClass(UserTrackerPathImpl.class);
		setModelPKClass(long.class);

		setTable(UserTrackerPathTable.INSTANCE);
	}

	/**
	 * Creates a new user tracker path with the primary key. Does not add the user tracker path to the database.
	 *
	 * @param userTrackerPathId the primary key for the new user tracker path
	 * @return the new user tracker path
	 */
	@Override
	public UserTrackerPath create(long userTrackerPathId) {
		UserTrackerPath userTrackerPath = new UserTrackerPathImpl();

		userTrackerPath.setNew(true);
		userTrackerPath.setPrimaryKey(userTrackerPathId);

		userTrackerPath.setCompanyId(CompanyThreadLocal.getCompanyId());

		return userTrackerPath;
	}

	/**
	 * Removes the user tracker path with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param userTrackerPathId the primary key of the user tracker path
	 * @return the user tracker path that was removed
	 * @throws NoSuchUserTrackerPathException if a user tracker path with the primary key could not be found
	 */
	@Override
	public UserTrackerPath remove(long userTrackerPathId)
		throws NoSuchUserTrackerPathException {

		return remove((Serializable)userTrackerPathId);
	}

	@Override
	protected UserTrackerPath removeImpl(UserTrackerPath userTrackerPath) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(userTrackerPath)) {
				userTrackerPath = (UserTrackerPath)session.get(
					UserTrackerPathImpl.class,
					userTrackerPath.getPrimaryKeyObj());
			}

			if (userTrackerPath != null) {
				session.delete(userTrackerPath);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (userTrackerPath != null) {
			clearCache(userTrackerPath);
		}

		return userTrackerPath;
	}

	@Override
	public UserTrackerPath updateImpl(UserTrackerPath userTrackerPath) {
		boolean isNew = userTrackerPath.isNew();

		if (!(userTrackerPath instanceof UserTrackerPathModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(userTrackerPath.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					userTrackerPath);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in userTrackerPath proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom UserTrackerPath implementation " +
					userTrackerPath.getClass());
		}

		UserTrackerPathModelImpl userTrackerPathModelImpl =
			(UserTrackerPathModelImpl)userTrackerPath;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(userTrackerPath);
			}
			else {
				userTrackerPath = (UserTrackerPath)session.merge(
					userTrackerPath);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(userTrackerPath, false);

		if (isNew) {
			userTrackerPath.setNew(false);
		}

		userTrackerPath.resetOriginalValues();

		return userTrackerPath;
	}

	/**
	 * Returns the user tracker path with the primary key or throws a <code>NoSuchUserTrackerPathException</code> if it could not be found.
	 *
	 * @param userTrackerPathId the primary key of the user tracker path
	 * @return the user tracker path
	 * @throws NoSuchUserTrackerPathException if a user tracker path with the primary key could not be found
	 */
	@Override
	public UserTrackerPath findByPrimaryKey(long userTrackerPathId)
		throws NoSuchUserTrackerPathException {

		return findByPrimaryKey((Serializable)userTrackerPathId);
	}

	/**
	 * Returns the user tracker path with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param userTrackerPathId the primary key of the user tracker path
	 * @return the user tracker path, or <code>null</code> if a user tracker path with the primary key could not be found
	 */
	@Override
	public UserTrackerPath fetchByPrimaryKey(long userTrackerPathId) {
		return fetchByPrimaryKey((Serializable)userTrackerPathId);
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
		return "userTrackerPathId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_USERTRACKERPATH;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return UserTrackerPathModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the user tracker path persistence.
	 */
	public void afterPropertiesSet() {
		_collectionPersistenceFinderByUserTrackerId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByUserTrackerId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"userTrackerId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByUserTrackerId", new String[] {Long.class.getName()},
					new String[] {"userTrackerId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByUserTrackerId", new String[] {Long.class.getName()},
					new String[] {"userTrackerId"}, false),
				_SQL_SELECT_USERTRACKERPATH_WHERE,
				_SQL_COUNT_USERTRACKERPATH_WHERE,
				UserTrackerPathModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"userTrackerPath.", "userTrackerId", FinderColumn.Type.LONG,
					"=", true, true, UserTrackerPath::getUserTrackerId));

		UserTrackerPathUtil.setPersistence(this);
	}

	public void destroy() {
		UserTrackerPathUtil.setPersistence(null);

		EntityCacheUtil.removeCache(UserTrackerPathImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		UserTrackerPathModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_USERTRACKERPATH =
		"SELECT userTrackerPath FROM UserTrackerPath userTrackerPath";

	private static final String _SQL_SELECT_USERTRACKERPATH_WHERE =
		"SELECT userTrackerPath FROM UserTrackerPath userTrackerPath WHERE ";

	private static final String _SQL_COUNT_USERTRACKERPATH_WHERE =
		"SELECT COUNT(userTrackerPath) FROM UserTrackerPath userTrackerPath WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No UserTrackerPath exists with the key {";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"path"});

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:310839619