/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.impl;

import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.NoSuchBrowserTrackerException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BrowserTracker;
import com.liferay.portal.kernel.model.BrowserTrackerTable;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.BrowserTrackerPersistence;
import com.liferay.portal.kernel.service.persistence.BrowserTrackerUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.model.impl.BrowserTrackerImpl;
import com.liferay.portal.model.impl.BrowserTrackerModelImpl;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Map;

/**
 * The persistence implementation for the browser tracker service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class BrowserTrackerPersistenceImpl
	extends BasePersistenceImpl<BrowserTracker, NoSuchBrowserTrackerException>
	implements BrowserTrackerPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>BrowserTrackerUtil</code> to access the browser tracker persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		BrowserTrackerImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private UniquePersistenceFinder
		<BrowserTracker, NoSuchBrowserTrackerException>
			_uniquePersistenceFinderByUserId;

	/**
	 * Returns the browser tracker where userId = &#63; or throws a <code>NoSuchBrowserTrackerException</code> if it could not be found.
	 *
	 * @param userId the user ID
	 * @return the matching browser tracker
	 * @throws NoSuchBrowserTrackerException if a matching browser tracker could not be found
	 */
	@Override
	public BrowserTracker findByUserId(long userId)
		throws NoSuchBrowserTrackerException {

		return _uniquePersistenceFinderByUserId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {userId});
	}

	/**
	 * Returns the browser tracker where userId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param userId the user ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching browser tracker, or <code>null</code> if a matching browser tracker could not be found
	 */
	@Override
	public BrowserTracker fetchByUserId(long userId, boolean useFinderCache) {
		return _uniquePersistenceFinderByUserId.fetch(
			FinderCacheUtil.getFinderCache(), new Object[] {userId},
			useFinderCache);
	}

	/**
	 * Removes the browser tracker where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @return the browser tracker that was removed
	 */
	@Override
	public BrowserTracker removeByUserId(long userId)
		throws NoSuchBrowserTrackerException {

		BrowserTracker browserTracker = findByUserId(userId);

		return remove(browserTracker);
	}

	/**
	 * Returns the number of browser trackers where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching browser trackers
	 */
	@Override
	public int countByUserId(long userId) {
		return _uniquePersistenceFinderByUserId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {userId});
	}

	public BrowserTrackerPersistenceImpl() {
		setModelClass(BrowserTracker.class);

		setModelImplClass(BrowserTrackerImpl.class);
		setModelPKClass(long.class);

		setTable(BrowserTrackerTable.INSTANCE);
	}

	/**
	 * Creates a new browser tracker with the primary key. Does not add the browser tracker to the database.
	 *
	 * @param browserTrackerId the primary key for the new browser tracker
	 * @return the new browser tracker
	 */
	@Override
	public BrowserTracker create(long browserTrackerId) {
		BrowserTracker browserTracker = new BrowserTrackerImpl();

		browserTracker.setNew(true);
		browserTracker.setPrimaryKey(browserTrackerId);

		browserTracker.setCompanyId(CompanyThreadLocal.getCompanyId());

		return browserTracker;
	}

	/**
	 * Removes the browser tracker with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param browserTrackerId the primary key of the browser tracker
	 * @return the browser tracker that was removed
	 * @throws NoSuchBrowserTrackerException if a browser tracker with the primary key could not be found
	 */
	@Override
	public BrowserTracker remove(long browserTrackerId)
		throws NoSuchBrowserTrackerException {

		return remove((Serializable)browserTrackerId);
	}

	@Override
	protected BrowserTracker removeImpl(BrowserTracker browserTracker) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(browserTracker)) {
				browserTracker = (BrowserTracker)session.get(
					BrowserTrackerImpl.class,
					browserTracker.getPrimaryKeyObj());
			}

			if (browserTracker != null) {
				session.delete(browserTracker);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (browserTracker != null) {
			clearCache(browserTracker);
		}

		return browserTracker;
	}

	@Override
	public BrowserTracker updateImpl(BrowserTracker browserTracker) {
		boolean isNew = browserTracker.isNew();

		if (!(browserTracker instanceof BrowserTrackerModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(browserTracker.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					browserTracker);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in browserTracker proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom BrowserTracker implementation " +
					browserTracker.getClass());
		}

		BrowserTrackerModelImpl browserTrackerModelImpl =
			(BrowserTrackerModelImpl)browserTracker;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(browserTracker);
			}
			else {
				browserTracker = (BrowserTracker)session.merge(browserTracker);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(browserTracker, false);

		if (isNew) {
			browserTracker.setNew(false);
		}

		browserTracker.resetOriginalValues();

		return browserTracker;
	}

	/**
	 * Returns the browser tracker with the primary key or throws a <code>NoSuchBrowserTrackerException</code> if it could not be found.
	 *
	 * @param browserTrackerId the primary key of the browser tracker
	 * @return the browser tracker
	 * @throws NoSuchBrowserTrackerException if a browser tracker with the primary key could not be found
	 */
	@Override
	public BrowserTracker findByPrimaryKey(long browserTrackerId)
		throws NoSuchBrowserTrackerException {

		return findByPrimaryKey((Serializable)browserTrackerId);
	}

	/**
	 * Returns the browser tracker with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param browserTrackerId the primary key of the browser tracker
	 * @return the browser tracker, or <code>null</code> if a browser tracker with the primary key could not be found
	 */
	@Override
	public BrowserTracker fetchByPrimaryKey(long browserTrackerId) {
		return fetchByPrimaryKey((Serializable)browserTrackerId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return EntityCacheUtil.getEntityCache();
	}

	@Override
	protected String getPKDBName() {
		return "browserTrackerId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_BROWSERTRACKER;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return BrowserTrackerModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the browser tracker persistence.
	 */
	public void afterPropertiesSet() {
		_uniquePersistenceFinderByUserId = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUserId",
				new String[] {Long.class.getName()}, new String[] {"userId"}, 0,
				0, false, BrowserTracker::getUserId),
			_SQL_SELECT_BROWSERTRACKER_WHERE, "",
			new FinderColumn<>(
				"browserTracker.", "userId", FinderColumn.Type.LONG, "=", true,
				true, BrowserTracker::getUserId));

		BrowserTrackerUtil.setPersistence(this);
	}

	public void destroy() {
		BrowserTrackerUtil.setPersistence(null);

		EntityCacheUtil.removeCache(BrowserTrackerImpl.class.getName());
	}

	private static final String _SQL_SELECT_BROWSERTRACKER =
		"SELECT browserTracker FROM BrowserTracker browserTracker";

	private static final String _SQL_SELECT_BROWSERTRACKER_WHERE =
		"SELECT browserTracker FROM BrowserTracker browserTracker WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No BrowserTracker exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		BrowserTrackerPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-346406725