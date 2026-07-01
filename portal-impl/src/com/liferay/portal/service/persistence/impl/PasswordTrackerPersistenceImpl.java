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
import com.liferay.portal.kernel.exception.NoSuchPasswordTrackerException;
import com.liferay.portal.kernel.model.PasswordTracker;
import com.liferay.portal.kernel.model.PasswordTrackerTable;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.PasswordTrackerPersistence;
import com.liferay.portal.kernel.service.persistence.PasswordTrackerUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.model.impl.PasswordTrackerImpl;
import com.liferay.portal.model.impl.PasswordTrackerModelImpl;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence implementation for the password tracker service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class PasswordTrackerPersistenceImpl
	extends BasePersistenceImpl<PasswordTracker, NoSuchPasswordTrackerException>
	implements PasswordTrackerPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>PasswordTrackerUtil</code> to access the password tracker persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		PasswordTrackerImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<PasswordTracker, NoSuchPasswordTrackerException>
			_collectionPersistenceFinderByUserId;

	/**
	 * Returns an ordered range of all the password trackers where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PasswordTrackerModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of password trackers
	 * @param end the upper bound of the range of password trackers (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching password trackers
	 */
	@Override
	public List<PasswordTracker> findByUserId(
		long userId, int start, int end,
		OrderByComparator<PasswordTracker> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUserId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {userId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first password tracker in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching password tracker
	 * @throws NoSuchPasswordTrackerException if a matching password tracker could not be found
	 */
	@Override
	public PasswordTracker findByUserId_First(
			long userId, OrderByComparator<PasswordTracker> orderByComparator)
		throws NoSuchPasswordTrackerException {

		return _collectionPersistenceFinderByUserId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {userId},
			orderByComparator);
	}

	/**
	 * Returns the first password tracker in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching password tracker, or <code>null</code> if a matching password tracker could not be found
	 */
	@Override
	public PasswordTracker fetchByUserId_First(
		long userId, OrderByComparator<PasswordTracker> orderByComparator) {

		return _collectionPersistenceFinderByUserId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {userId},
			orderByComparator);
	}

	/**
	 * Removes all the password trackers where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	@Override
	public void removeByUserId(long userId) {
		_collectionPersistenceFinderByUserId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {userId});
	}

	/**
	 * Returns the number of password trackers where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching password trackers
	 */
	@Override
	public int countByUserId(long userId) {
		return _collectionPersistenceFinderByUserId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {userId});
	}

	public PasswordTrackerPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("password", "password_");

		setDBColumnNames(dbColumnNames);

		setModelClass(PasswordTracker.class);

		setModelImplClass(PasswordTrackerImpl.class);
		setModelPKClass(long.class);

		setTable(PasswordTrackerTable.INSTANCE);
	}

	/**
	 * Creates a new password tracker with the primary key. Does not add the password tracker to the database.
	 *
	 * @param passwordTrackerId the primary key for the new password tracker
	 * @return the new password tracker
	 */
	@Override
	public PasswordTracker create(long passwordTrackerId) {
		PasswordTracker passwordTracker = new PasswordTrackerImpl();

		passwordTracker.setNew(true);
		passwordTracker.setPrimaryKey(passwordTrackerId);

		passwordTracker.setCompanyId(CompanyThreadLocal.getCompanyId());

		return passwordTracker;
	}

	/**
	 * Removes the password tracker with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param passwordTrackerId the primary key of the password tracker
	 * @return the password tracker that was removed
	 * @throws NoSuchPasswordTrackerException if a password tracker with the primary key could not be found
	 */
	@Override
	public PasswordTracker remove(long passwordTrackerId)
		throws NoSuchPasswordTrackerException {

		return remove((Serializable)passwordTrackerId);
	}

	@Override
	protected PasswordTracker removeImpl(PasswordTracker passwordTracker) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(passwordTracker)) {
				passwordTracker = (PasswordTracker)session.get(
					PasswordTrackerImpl.class,
					passwordTracker.getPrimaryKeyObj());
			}

			if (passwordTracker != null) {
				session.delete(passwordTracker);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (passwordTracker != null) {
			clearCache(passwordTracker);
		}

		return passwordTracker;
	}

	@Override
	public PasswordTracker updateImpl(PasswordTracker passwordTracker) {
		boolean isNew = passwordTracker.isNew();

		if (!(passwordTracker instanceof PasswordTrackerModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(passwordTracker.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					passwordTracker);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in passwordTracker proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom PasswordTracker implementation " +
					passwordTracker.getClass());
		}

		PasswordTrackerModelImpl passwordTrackerModelImpl =
			(PasswordTrackerModelImpl)passwordTracker;

		if (isNew && (passwordTracker.getCreateDate() == null)) {
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			Date date = new Date();

			if (serviceContext == null) {
				passwordTracker.setCreateDate(date);
			}
			else {
				passwordTracker.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(passwordTracker);
			}
			else {
				passwordTracker = (PasswordTracker)session.merge(
					passwordTracker);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(passwordTracker, false);

		if (isNew) {
			passwordTracker.setNew(false);
		}

		passwordTracker.resetOriginalValues();

		return passwordTracker;
	}

	/**
	 * Returns the password tracker with the primary key or throws a <code>NoSuchPasswordTrackerException</code> if it could not be found.
	 *
	 * @param passwordTrackerId the primary key of the password tracker
	 * @return the password tracker
	 * @throws NoSuchPasswordTrackerException if a password tracker with the primary key could not be found
	 */
	@Override
	public PasswordTracker findByPrimaryKey(long passwordTrackerId)
		throws NoSuchPasswordTrackerException {

		return findByPrimaryKey((Serializable)passwordTrackerId);
	}

	/**
	 * Returns the password tracker with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param passwordTrackerId the primary key of the password tracker
	 * @return the password tracker, or <code>null</code> if a password tracker with the primary key could not be found
	 */
	@Override
	public PasswordTracker fetchByPrimaryKey(long passwordTrackerId) {
		return fetchByPrimaryKey((Serializable)passwordTrackerId);
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
		return "passwordTrackerId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_PASSWORDTRACKER;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return PasswordTrackerModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the password tracker persistence.
	 */
	public void afterPropertiesSet() {
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
				_SQL_SELECT_PASSWORDTRACKER_WHERE,
				_SQL_COUNT_PASSWORDTRACKER_WHERE,
				PasswordTrackerModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"passwordTracker.", "userId", FinderColumn.Type.LONG, "=",
					true, true, PasswordTracker::getUserId));

		PasswordTrackerUtil.setPersistence(this);
	}

	public void destroy() {
		PasswordTrackerUtil.setPersistence(null);

		EntityCacheUtil.removeCache(PasswordTrackerImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		PasswordTrackerModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_PASSWORDTRACKER =
		"SELECT passwordTracker FROM PasswordTracker passwordTracker";

	private static final String _SQL_SELECT_PASSWORDTRACKER_WHERE =
		"SELECT passwordTracker FROM PasswordTracker passwordTracker WHERE ";

	private static final String _SQL_COUNT_PASSWORDTRACKER_WHERE =
		"SELECT COUNT(passwordTracker) FROM PasswordTracker passwordTracker WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"password"});

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1022216538