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
import com.liferay.portal.kernel.exception.NoSuchReleaseException;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.model.ReleaseTable;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.ReleasePersistence;
import com.liferay.portal.kernel.service.persistence.ReleaseUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.model.impl.ReleaseImpl;
import com.liferay.portal.model.impl.ReleaseModelImpl;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * The persistence implementation for the release service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class ReleasePersistenceImpl
	extends BasePersistenceImpl<Release, NoSuchReleaseException>
	implements ReleasePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ReleaseUtil</code> to access the release persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ReleaseImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private UniquePersistenceFinder<Release, NoSuchReleaseException>
		_uniquePersistenceFinderByServletContextName;

	/**
	 * Returns the release where servletContextName = &#63; or throws a <code>NoSuchReleaseException</code> if it could not be found.
	 *
	 * @param servletContextName the servlet context name
	 * @return the matching release
	 * @throws NoSuchReleaseException if a matching release could not be found
	 */
	@Override
	public Release findByServletContextName(String servletContextName)
		throws NoSuchReleaseException {

		return _uniquePersistenceFinderByServletContextName.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {servletContextName});
	}

	/**
	 * Returns the release where servletContextName = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param servletContextName the servlet context name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching release, or <code>null</code> if a matching release could not be found
	 */
	@Override
	public Release fetchByServletContextName(
		String servletContextName, boolean useFinderCache) {

		return _uniquePersistenceFinderByServletContextName.fetch(
			FinderCacheUtil.getFinderCache(), new Object[] {servletContextName},
			useFinderCache);
	}

	/**
	 * Removes the release where servletContextName = &#63; from the database.
	 *
	 * @param servletContextName the servlet context name
	 * @return the release that was removed
	 */
	@Override
	public Release removeByServletContextName(String servletContextName)
		throws NoSuchReleaseException {

		Release release = findByServletContextName(servletContextName);

		return remove(release);
	}

	/**
	 * Returns the number of releases where servletContextName = &#63;.
	 *
	 * @param servletContextName the servlet context name
	 * @return the number of matching releases
	 */
	@Override
	public int countByServletContextName(String servletContextName) {
		return _uniquePersistenceFinderByServletContextName.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {servletContextName});
	}

	public ReleasePersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("state", "state_");

		setDBColumnNames(dbColumnNames);

		setModelClass(Release.class);

		setModelImplClass(ReleaseImpl.class);
		setModelPKClass(long.class);

		setTable(ReleaseTable.INSTANCE);
	}

	/**
	 * Creates a new release with the primary key. Does not add the release to the database.
	 *
	 * @param releaseId the primary key for the new release
	 * @return the new release
	 */
	@Override
	public Release create(long releaseId) {
		Release release = new ReleaseImpl();

		release.setNew(true);
		release.setPrimaryKey(releaseId);

		return release;
	}

	/**
	 * Removes the release with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param releaseId the primary key of the release
	 * @return the release that was removed
	 * @throws NoSuchReleaseException if a release with the primary key could not be found
	 */
	@Override
	public Release remove(long releaseId) throws NoSuchReleaseException {
		return remove((Serializable)releaseId);
	}

	@Override
	protected Release removeImpl(Release release) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(release)) {
				release = (Release)session.get(
					ReleaseImpl.class, release.getPrimaryKeyObj());
			}

			if (release != null) {
				session.delete(release);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (release != null) {
			clearCache(release);
		}

		return release;
	}

	@Override
	public Release updateImpl(Release release) {
		boolean isNew = release.isNew();

		if (!(release instanceof ReleaseModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(release.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(release);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in release proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom Release implementation " +
					release.getClass());
		}

		ReleaseModelImpl releaseModelImpl = (ReleaseModelImpl)release;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (release.getCreateDate() == null)) {
			if (serviceContext == null) {
				release.setCreateDate(date);
			}
			else {
				release.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!releaseModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				release.setModifiedDate(date);
			}
			else {
				release.setModifiedDate(serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(release);
			}
			else {
				release = (Release)session.merge(release);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(release, false);

		if (isNew) {
			release.setNew(false);
		}

		release.resetOriginalValues();

		return release;
	}

	/**
	 * Returns the release with the primary key or throws a <code>NoSuchReleaseException</code> if it could not be found.
	 *
	 * @param releaseId the primary key of the release
	 * @return the release
	 * @throws NoSuchReleaseException if a release with the primary key could not be found
	 */
	@Override
	public Release findByPrimaryKey(long releaseId)
		throws NoSuchReleaseException {

		return findByPrimaryKey((Serializable)releaseId);
	}

	/**
	 * Returns the release with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param releaseId the primary key of the release
	 * @return the release, or <code>null</code> if a release with the primary key could not be found
	 */
	@Override
	public Release fetchByPrimaryKey(long releaseId) {
		return fetchByPrimaryKey((Serializable)releaseId);
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
		return "releaseId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_RELEASE_;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return ReleaseModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the release persistence.
	 */
	public void afterPropertiesSet() {
		_uniquePersistenceFinderByServletContextName =
			new UniquePersistenceFinder<>(
				this,
				createUniqueFinderPath(
					FINDER_CLASS_NAME_ENTITY, "fetchByServletContextName",
					new String[] {String.class.getName()},
					new String[] {"servletContextName"}, 1, 1, true,
					convertCaseFunction(Release::getServletContextName)),
				_SQL_SELECT_RELEASE__WHERE, "",
				new FinderColumn<>(
					"release_.", "servletContextName", FinderColumn.Type.STRING,
					"=", false, true, Release::getServletContextName));

		ReleaseUtil.setPersistence(this);
	}

	public void destroy() {
		ReleaseUtil.setPersistence(null);

		EntityCacheUtil.removeCache(ReleaseImpl.class.getName());
	}

	private static final String _SQL_SELECT_RELEASE_ =
		"SELECT release_ FROM Release release_";

	private static final String _SQL_SELECT_RELEASE__WHERE =
		"SELECT release_ FROM Release release_ WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"state"});

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:842092672