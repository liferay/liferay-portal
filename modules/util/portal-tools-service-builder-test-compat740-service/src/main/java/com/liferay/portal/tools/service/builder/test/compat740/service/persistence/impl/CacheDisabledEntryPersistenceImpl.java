/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.compat740.service.persistence.impl;

import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.tools.service.builder.test.compat740.exception.NoSuchCacheDisabledEntryException;
import com.liferay.portal.tools.service.builder.test.compat740.model.CacheDisabledEntry;
import com.liferay.portal.tools.service.builder.test.compat740.model.CacheDisabledEntryTable;
import com.liferay.portal.tools.service.builder.test.compat740.model.impl.CacheDisabledEntryImpl;
import com.liferay.portal.tools.service.builder.test.compat740.model.impl.CacheDisabledEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.compat740.service.persistence.CacheDisabledEntryPersistence;
import com.liferay.portal.tools.service.builder.test.compat740.service.persistence.CacheDisabledEntryUtil;
import com.liferay.portal.tools.service.builder.test.compat740.service.persistence.impl.constants.SBCompat740PersistenceConstants;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Map;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the cache disabled entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = CacheDisabledEntryPersistence.class)
public class CacheDisabledEntryPersistenceImpl
	extends BasePersistenceImpl
		<CacheDisabledEntry, NoSuchCacheDisabledEntryException>
	implements CacheDisabledEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CacheDisabledEntryUtil</code> to access the cache disabled entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CacheDisabledEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private UniquePersistenceFinder
		<CacheDisabledEntry, NoSuchCacheDisabledEntryException>
			_uniquePersistenceFinderByName;

	/**
	 * Returns the cache disabled entry where name = &#63; or throws a <code>NoSuchCacheDisabledEntryException</code> if it could not be found.
	 *
	 * @param name the name
	 * @return the matching cache disabled entry
	 * @throws NoSuchCacheDisabledEntryException if a matching cache disabled entry could not be found
	 */
	@Override
	public CacheDisabledEntry findByName(String name)
		throws NoSuchCacheDisabledEntryException {

		return _uniquePersistenceFinderByName.find(
			dummyFinderCache, new Object[] {name});
	}

	/**
	 * Returns the cache disabled entry where name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cache disabled entry, or <code>null</code> if a matching cache disabled entry could not be found
	 */
	@Override
	public CacheDisabledEntry fetchByName(String name, boolean useFinderCache) {
		return _uniquePersistenceFinderByName.fetch(
			dummyFinderCache, new Object[] {name}, useFinderCache);
	}

	/**
	 * Removes the cache disabled entry where name = &#63; from the database.
	 *
	 * @param name the name
	 * @return the cache disabled entry that was removed
	 */
	@Override
	public CacheDisabledEntry removeByName(String name)
		throws NoSuchCacheDisabledEntryException {

		CacheDisabledEntry cacheDisabledEntry = findByName(name);

		return remove(cacheDisabledEntry);
	}

	/**
	 * Returns the number of cache disabled entries where name = &#63;.
	 *
	 * @param name the name
	 * @return the number of matching cache disabled entries
	 */
	@Override
	public int countByName(String name) {
		return _uniquePersistenceFinderByName.count(
			dummyFinderCache, new Object[] {name});
	}

	public CacheDisabledEntryPersistenceImpl() {
		setModelClass(CacheDisabledEntry.class);

		setModelImplClass(CacheDisabledEntryImpl.class);
		setModelPKClass(long.class);

		setTable(CacheDisabledEntryTable.INSTANCE);
	}

	/**
	 * Creates a new cache disabled entry with the primary key. Does not add the cache disabled entry to the database.
	 *
	 * @param cacheDisabledEntryId the primary key for the new cache disabled entry
	 * @return the new cache disabled entry
	 */
	@Override
	public CacheDisabledEntry create(long cacheDisabledEntryId) {
		CacheDisabledEntry cacheDisabledEntry = new CacheDisabledEntryImpl();

		cacheDisabledEntry.setNew(true);
		cacheDisabledEntry.setPrimaryKey(cacheDisabledEntryId);

		return cacheDisabledEntry;
	}

	/**
	 * Removes the cache disabled entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param cacheDisabledEntryId the primary key of the cache disabled entry
	 * @return the cache disabled entry that was removed
	 * @throws NoSuchCacheDisabledEntryException if a cache disabled entry with the primary key could not be found
	 */
	@Override
	public CacheDisabledEntry remove(long cacheDisabledEntryId)
		throws NoSuchCacheDisabledEntryException {

		return remove((Serializable)cacheDisabledEntryId);
	}

	@Override
	protected CacheDisabledEntry removeImpl(
		CacheDisabledEntry cacheDisabledEntry) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(cacheDisabledEntry)) {
				cacheDisabledEntry = (CacheDisabledEntry)session.get(
					CacheDisabledEntryImpl.class,
					cacheDisabledEntry.getPrimaryKeyObj());
			}

			if (cacheDisabledEntry != null) {
				session.delete(cacheDisabledEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (cacheDisabledEntry != null) {
			clearCache(cacheDisabledEntry);
		}

		return cacheDisabledEntry;
	}

	@Override
	public CacheDisabledEntry updateImpl(
		CacheDisabledEntry cacheDisabledEntry) {

		boolean isNew = cacheDisabledEntry.isNew();

		if (!(cacheDisabledEntry instanceof CacheDisabledEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(cacheDisabledEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					cacheDisabledEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in cacheDisabledEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CacheDisabledEntry implementation " +
					cacheDisabledEntry.getClass());
		}

		CacheDisabledEntryModelImpl cacheDisabledEntryModelImpl =
			(CacheDisabledEntryModelImpl)cacheDisabledEntry;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(cacheDisabledEntry);
			}
			else {
				cacheDisabledEntry = (CacheDisabledEntry)session.merge(
					cacheDisabledEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(cacheDisabledEntry, false);

		if (isNew) {
			cacheDisabledEntry.setNew(false);
		}

		cacheDisabledEntry.resetOriginalValues();

		return cacheDisabledEntry;
	}

	/**
	 * Returns the cache disabled entry with the primary key or throws a <code>NoSuchCacheDisabledEntryException</code> if it could not be found.
	 *
	 * @param cacheDisabledEntryId the primary key of the cache disabled entry
	 * @return the cache disabled entry
	 * @throws NoSuchCacheDisabledEntryException if a cache disabled entry with the primary key could not be found
	 */
	@Override
	public CacheDisabledEntry findByPrimaryKey(long cacheDisabledEntryId)
		throws NoSuchCacheDisabledEntryException {

		return findByPrimaryKey((Serializable)cacheDisabledEntryId);
	}

	/**
	 * Returns the cache disabled entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param cacheDisabledEntryId the primary key of the cache disabled entry
	 * @return the cache disabled entry, or <code>null</code> if a cache disabled entry with the primary key could not be found
	 */
	@Override
	public CacheDisabledEntry fetchByPrimaryKey(long cacheDisabledEntryId) {
		return fetchByPrimaryKey((Serializable)cacheDisabledEntryId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return dummyEntityCache;
	}

	@Override
	protected String getPKDBName() {
		return "cacheDisabledEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CACHEDISABLEDENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CacheDisabledEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the cache disabled entry persistence.
	 */
	@Activate
	public void activate() {
		_uniquePersistenceFinderByName = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByName",
				new String[] {String.class.getName()}, new String[] {"name"}, 0,
				1, false, convertNullFunction(CacheDisabledEntry::getName)),
			_SQL_SELECT_CACHEDISABLEDENTRY_WHERE, "",
			new FinderColumn<>(
				"cacheDisabledEntry.", "name", FinderColumn.Type.STRING, "=",
				true, true, CacheDisabledEntry::getName));

		CacheDisabledEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CacheDisabledEntryUtil.setPersistence(null);

		dummyEntityCache.removeCache(CacheDisabledEntryImpl.class.getName());
	}

	@Override
	@Reference(
		target = SBCompat740PersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = SBCompat740PersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = SBCompat740PersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	private static final String _SQL_SELECT_CACHEDISABLEDENTRY =
		"SELECT cacheDisabledEntry FROM CacheDisabledEntry cacheDisabledEntry";

	private static final String _SQL_SELECT_CACHEDISABLEDENTRY_WHERE =
		"SELECT cacheDisabledEntry FROM CacheDisabledEntry cacheDisabledEntry WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CacheDisabledEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CacheDisabledEntryPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return dummyFinderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:2094945149