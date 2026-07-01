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
import com.liferay.portal.kernel.exception.NoSuchUserTrackerException;
import com.liferay.portal.kernel.model.UserTracker;
import com.liferay.portal.kernel.model.UserTrackerTable;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.UserTrackerPersistence;
import com.liferay.portal.kernel.service.persistence.UserTrackerUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.model.impl.UserTrackerImpl;
import com.liferay.portal.model.impl.UserTrackerModelImpl;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * The persistence implementation for the user tracker service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class UserTrackerPersistenceImpl
	extends BasePersistenceImpl<UserTracker, NoSuchUserTrackerException>
	implements UserTrackerPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>UserTrackerUtil</code> to access the user tracker persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		UserTrackerImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<UserTracker, NoSuchUserTrackerException>
		_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the user trackers where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserTrackerModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of user trackers
	 * @param end the upper bound of the range of user trackers (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching user trackers
	 */
	@Override
	public List<UserTracker> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<UserTracker> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first user tracker in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user tracker
	 * @throws NoSuchUserTrackerException if a matching user tracker could not be found
	 */
	@Override
	public UserTracker findByCompanyId_First(
			long companyId, OrderByComparator<UserTracker> orderByComparator)
		throws NoSuchUserTrackerException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			orderByComparator);
	}

	/**
	 * Returns the first user tracker in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user tracker, or <code>null</code> if a matching user tracker could not be found
	 */
	@Override
	public UserTracker fetchByCompanyId_First(
		long companyId, OrderByComparator<UserTracker> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			orderByComparator);
	}

	/**
	 * Removes all the user trackers where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId});
	}

	/**
	 * Returns the number of user trackers where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching user trackers
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId});
	}

	private CollectionPersistenceFinder<UserTracker, NoSuchUserTrackerException>
		_collectionPersistenceFinderByUserId;

	/**
	 * Returns an ordered range of all the user trackers where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserTrackerModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of user trackers
	 * @param end the upper bound of the range of user trackers (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching user trackers
	 */
	@Override
	public List<UserTracker> findByUserId(
		long userId, int start, int end,
		OrderByComparator<UserTracker> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUserId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {userId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first user tracker in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user tracker
	 * @throws NoSuchUserTrackerException if a matching user tracker could not be found
	 */
	@Override
	public UserTracker findByUserId_First(
			long userId, OrderByComparator<UserTracker> orderByComparator)
		throws NoSuchUserTrackerException {

		return _collectionPersistenceFinderByUserId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {userId},
			orderByComparator);
	}

	/**
	 * Returns the first user tracker in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user tracker, or <code>null</code> if a matching user tracker could not be found
	 */
	@Override
	public UserTracker fetchByUserId_First(
		long userId, OrderByComparator<UserTracker> orderByComparator) {

		return _collectionPersistenceFinderByUserId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {userId},
			orderByComparator);
	}

	/**
	 * Removes all the user trackers where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	@Override
	public void removeByUserId(long userId) {
		_collectionPersistenceFinderByUserId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {userId});
	}

	/**
	 * Returns the number of user trackers where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching user trackers
	 */
	@Override
	public int countByUserId(long userId) {
		return _collectionPersistenceFinderByUserId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {userId});
	}

	private CollectionPersistenceFinder<UserTracker, NoSuchUserTrackerException>
		_collectionPersistenceFinderBySessionId;

	/**
	 * Returns an ordered range of all the user trackers where sessionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserTrackerModelImpl</code>.
	 * </p>
	 *
	 * @param sessionId the session ID
	 * @param start the lower bound of the range of user trackers
	 * @param end the upper bound of the range of user trackers (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching user trackers
	 */
	@Override
	public List<UserTracker> findBySessionId(
		String sessionId, int start, int end,
		OrderByComparator<UserTracker> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderBySessionId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {sessionId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first user tracker in the ordered set where sessionId = &#63;.
	 *
	 * @param sessionId the session ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user tracker
	 * @throws NoSuchUserTrackerException if a matching user tracker could not be found
	 */
	@Override
	public UserTracker findBySessionId_First(
			String sessionId, OrderByComparator<UserTracker> orderByComparator)
		throws NoSuchUserTrackerException {

		return _collectionPersistenceFinderBySessionId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {sessionId},
			orderByComparator);
	}

	/**
	 * Returns the first user tracker in the ordered set where sessionId = &#63;.
	 *
	 * @param sessionId the session ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user tracker, or <code>null</code> if a matching user tracker could not be found
	 */
	@Override
	public UserTracker fetchBySessionId_First(
		String sessionId, OrderByComparator<UserTracker> orderByComparator) {

		return _collectionPersistenceFinderBySessionId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {sessionId},
			orderByComparator);
	}

	/**
	 * Removes all the user trackers where sessionId = &#63; from the database.
	 *
	 * @param sessionId the session ID
	 */
	@Override
	public void removeBySessionId(String sessionId) {
		_collectionPersistenceFinderBySessionId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {sessionId});
	}

	/**
	 * Returns the number of user trackers where sessionId = &#63;.
	 *
	 * @param sessionId the session ID
	 * @return the number of matching user trackers
	 */
	@Override
	public int countBySessionId(String sessionId) {
		return _collectionPersistenceFinderBySessionId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {sessionId});
	}

	public UserTrackerPersistenceImpl() {
		setModelClass(UserTracker.class);

		setModelImplClass(UserTrackerImpl.class);
		setModelPKClass(long.class);

		setTable(UserTrackerTable.INSTANCE);
	}

	/**
	 * Creates a new user tracker with the primary key. Does not add the user tracker to the database.
	 *
	 * @param userTrackerId the primary key for the new user tracker
	 * @return the new user tracker
	 */
	@Override
	public UserTracker create(long userTrackerId) {
		UserTracker userTracker = new UserTrackerImpl();

		userTracker.setNew(true);
		userTracker.setPrimaryKey(userTrackerId);

		userTracker.setCompanyId(CompanyThreadLocal.getCompanyId());

		return userTracker;
	}

	/**
	 * Removes the user tracker with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param userTrackerId the primary key of the user tracker
	 * @return the user tracker that was removed
	 * @throws NoSuchUserTrackerException if a user tracker with the primary key could not be found
	 */
	@Override
	public UserTracker remove(long userTrackerId)
		throws NoSuchUserTrackerException {

		return remove((Serializable)userTrackerId);
	}

	@Override
	protected UserTracker removeImpl(UserTracker userTracker) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(userTracker)) {
				userTracker = (UserTracker)session.get(
					UserTrackerImpl.class, userTracker.getPrimaryKeyObj());
			}

			if (userTracker != null) {
				session.delete(userTracker);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (userTracker != null) {
			clearCache(userTracker);
		}

		return userTracker;
	}

	@Override
	public UserTracker updateImpl(UserTracker userTracker) {
		boolean isNew = userTracker.isNew();

		if (!(userTracker instanceof UserTrackerModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(userTracker.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(userTracker);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in userTracker proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom UserTracker implementation " +
					userTracker.getClass());
		}

		UserTrackerModelImpl userTrackerModelImpl =
			(UserTrackerModelImpl)userTracker;

		if (!userTrackerModelImpl.hasSetModifiedDate()) {
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			Date date = new Date();

			if (serviceContext == null) {
				userTracker.setModifiedDate(date);
			}
			else {
				userTracker.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(userTracker);
			}
			else {
				userTracker = (UserTracker)session.merge(userTracker);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(userTracker, false);

		if (isNew) {
			userTracker.setNew(false);
		}

		userTracker.resetOriginalValues();

		return userTracker;
	}

	/**
	 * Returns the user tracker with the primary key or throws a <code>NoSuchUserTrackerException</code> if it could not be found.
	 *
	 * @param userTrackerId the primary key of the user tracker
	 * @return the user tracker
	 * @throws NoSuchUserTrackerException if a user tracker with the primary key could not be found
	 */
	@Override
	public UserTracker findByPrimaryKey(long userTrackerId)
		throws NoSuchUserTrackerException {

		return findByPrimaryKey((Serializable)userTrackerId);
	}

	/**
	 * Returns the user tracker with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param userTrackerId the primary key of the user tracker
	 * @return the user tracker, or <code>null</code> if a user tracker with the primary key could not be found
	 */
	@Override
	public UserTracker fetchByPrimaryKey(long userTrackerId) {
		return fetchByPrimaryKey((Serializable)userTrackerId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return EntityCacheUtil.getEntityCache();
	}

	@Override
	protected String getPKDBName() {
		return "userTrackerId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_USERTRACKER;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return UserTrackerModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the user tracker persistence.
	 */
	public void afterPropertiesSet() {
		_collectionPersistenceFinderByCompanyId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCompanyId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCompanyId", new String[] {Long.class.getName()},
					new String[] {"companyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCompanyId", new String[] {Long.class.getName()},
					new String[] {"companyId"}, false),
				_SQL_SELECT_USERTRACKER_WHERE, _SQL_COUNT_USERTRACKER_WHERE,
				UserTrackerModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"userTracker.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, UserTracker::getCompanyId));

		_collectionPersistenceFinderByUserId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUserId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"userId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUserId",
					new String[] {Long.class.getName()},
					new String[] {"userId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUserId",
					new String[] {Long.class.getName()},
					new String[] {"userId"}, false),
				_SQL_SELECT_USERTRACKER_WHERE, _SQL_COUNT_USERTRACKER_WHERE,
				UserTrackerModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"userTracker.", "userId", FinderColumn.Type.LONG, "=", true,
					true, UserTracker::getUserId));

		_collectionPersistenceFinderBySessionId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findBySessionId",
					new String[] {
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"sessionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findBySessionId", new String[] {String.class.getName()},
					new String[] {"sessionId"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countBySessionId", new String[] {String.class.getName()},
					new String[] {"sessionId"}, 0, 1, false, null),
				_SQL_SELECT_USERTRACKER_WHERE, _SQL_COUNT_USERTRACKER_WHERE,
				UserTrackerModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"userTracker.", "sessionId", FinderColumn.Type.STRING, "=",
					true, true, UserTracker::getSessionId));

		UserTrackerUtil.setPersistence(this);
	}

	public void destroy() {
		UserTrackerUtil.setPersistence(null);

		EntityCacheUtil.removeCache(UserTrackerImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		UserTrackerModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_USERTRACKER =
		"SELECT userTracker FROM UserTracker userTracker";

	private static final String _SQL_SELECT_USERTRACKER_WHERE =
		"SELECT userTracker FROM UserTracker userTracker WHERE ";

	private static final String _SQL_COUNT_USERTRACKER_WHERE =
		"SELECT COUNT(userTracker) FROM UserTracker userTracker WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No UserTracker exists with the key {";

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-682367608