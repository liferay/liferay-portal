/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.impl;

import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchNullConvertibleEntryException;
import com.liferay.portal.tools.service.builder.test.model.NullConvertibleEntry;
import com.liferay.portal.tools.service.builder.test.model.NullConvertibleEntryTable;
import com.liferay.portal.tools.service.builder.test.model.impl.NullConvertibleEntryImpl;
import com.liferay.portal.tools.service.builder.test.model.impl.NullConvertibleEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.service.persistence.NullConvertibleEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.NullConvertibleEntryUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Map;

/**
 * The persistence implementation for the null convertible entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class NullConvertibleEntryPersistenceImpl
	extends BasePersistenceImpl
		<NullConvertibleEntry, NoSuchNullConvertibleEntryException>
	implements NullConvertibleEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>NullConvertibleEntryUtil</code> to access the null convertible entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		NullConvertibleEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private UniquePersistenceFinder
		<NullConvertibleEntry, NoSuchNullConvertibleEntryException>
			_uniquePersistenceFinderByName;

	/**
	 * Returns the null convertible entry where name = &#63; or throws a <code>NoSuchNullConvertibleEntryException</code> if it could not be found.
	 *
	 * @param name the name
	 * @return the matching null convertible entry
	 * @throws NoSuchNullConvertibleEntryException if a matching null convertible entry could not be found
	 */
	@Override
	public NullConvertibleEntry findByName(String name)
		throws NoSuchNullConvertibleEntryException {

		return _uniquePersistenceFinderByName.find(
			dummyFinderCache, new Object[] {name});
	}

	/**
	 * Returns the null convertible entry where name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching null convertible entry, or <code>null</code> if a matching null convertible entry could not be found
	 */
	@Override
	public NullConvertibleEntry fetchByName(
		String name, boolean useFinderCache) {

		return _uniquePersistenceFinderByName.fetch(
			dummyFinderCache, new Object[] {name}, useFinderCache);
	}

	/**
	 * Removes the null convertible entry where name = &#63; from the database.
	 *
	 * @param name the name
	 * @return the null convertible entry that was removed
	 */
	@Override
	public NullConvertibleEntry removeByName(String name)
		throws NoSuchNullConvertibleEntryException {

		NullConvertibleEntry nullConvertibleEntry = findByName(name);

		return remove(nullConvertibleEntry);
	}

	/**
	 * Returns the number of null convertible entries where name = &#63;.
	 *
	 * @param name the name
	 * @return the number of matching null convertible entries
	 */
	@Override
	public int countByName(String name) {
		return _uniquePersistenceFinderByName.count(
			dummyFinderCache, new Object[] {name});
	}

	public NullConvertibleEntryPersistenceImpl() {
		setModelClass(NullConvertibleEntry.class);

		setModelImplClass(NullConvertibleEntryImpl.class);
		setModelPKClass(long.class);

		setTable(NullConvertibleEntryTable.INSTANCE);
	}

	/**
	 * Creates a new null convertible entry with the primary key. Does not add the null convertible entry to the database.
	 *
	 * @param nullConvertibleEntryId the primary key for the new null convertible entry
	 * @return the new null convertible entry
	 */
	@Override
	public NullConvertibleEntry create(long nullConvertibleEntryId) {
		NullConvertibleEntry nullConvertibleEntry =
			new NullConvertibleEntryImpl();

		nullConvertibleEntry.setNew(true);
		nullConvertibleEntry.setPrimaryKey(nullConvertibleEntryId);

		return nullConvertibleEntry;
	}

	/**
	 * Removes the null convertible entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param nullConvertibleEntryId the primary key of the null convertible entry
	 * @return the null convertible entry that was removed
	 * @throws NoSuchNullConvertibleEntryException if a null convertible entry with the primary key could not be found
	 */
	@Override
	public NullConvertibleEntry remove(long nullConvertibleEntryId)
		throws NoSuchNullConvertibleEntryException {

		return remove((Serializable)nullConvertibleEntryId);
	}

	@Override
	protected NullConvertibleEntry removeImpl(
		NullConvertibleEntry nullConvertibleEntry) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(nullConvertibleEntry)) {
				nullConvertibleEntry = (NullConvertibleEntry)session.get(
					NullConvertibleEntryImpl.class,
					nullConvertibleEntry.getPrimaryKeyObj());
			}

			if (nullConvertibleEntry != null) {
				session.delete(nullConvertibleEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (nullConvertibleEntry != null) {
			clearCache(nullConvertibleEntry);
		}

		return nullConvertibleEntry;
	}

	@Override
	public NullConvertibleEntry updateImpl(
		NullConvertibleEntry nullConvertibleEntry) {

		boolean isNew = nullConvertibleEntry.isNew();

		if (!(nullConvertibleEntry instanceof NullConvertibleEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(nullConvertibleEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					nullConvertibleEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in nullConvertibleEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom NullConvertibleEntry implementation " +
					nullConvertibleEntry.getClass());
		}

		NullConvertibleEntryModelImpl nullConvertibleEntryModelImpl =
			(NullConvertibleEntryModelImpl)nullConvertibleEntry;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(nullConvertibleEntry);
			}
			else {
				nullConvertibleEntry = (NullConvertibleEntry)session.merge(
					nullConvertibleEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(nullConvertibleEntry, false);

		if (isNew) {
			nullConvertibleEntry.setNew(false);
		}

		nullConvertibleEntry.resetOriginalValues();

		return nullConvertibleEntry;
	}

	/**
	 * Returns the null convertible entry with the primary key or throws a <code>NoSuchNullConvertibleEntryException</code> if it could not be found.
	 *
	 * @param nullConvertibleEntryId the primary key of the null convertible entry
	 * @return the null convertible entry
	 * @throws NoSuchNullConvertibleEntryException if a null convertible entry with the primary key could not be found
	 */
	@Override
	public NullConvertibleEntry findByPrimaryKey(long nullConvertibleEntryId)
		throws NoSuchNullConvertibleEntryException {

		return findByPrimaryKey((Serializable)nullConvertibleEntryId);
	}

	/**
	 * Returns the null convertible entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param nullConvertibleEntryId the primary key of the null convertible entry
	 * @return the null convertible entry, or <code>null</code> if a null convertible entry with the primary key could not be found
	 */
	@Override
	public NullConvertibleEntry fetchByPrimaryKey(long nullConvertibleEntryId) {
		return fetchByPrimaryKey((Serializable)nullConvertibleEntryId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return dummyEntityCache;
	}

	@Override
	protected String getPKDBName() {
		return "nullConvertibleEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_NULLCONVERTIBLEENTRY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return NullConvertibleEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the null convertible entry persistence.
	 */
	public void afterPropertiesSet() {
		_uniquePersistenceFinderByName = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByName",
				new String[] {String.class.getName()}, new String[] {"name"}, 0,
				1, false, convertNullFunction(NullConvertibleEntry::getName)),
			_SQL_SELECT_NULLCONVERTIBLEENTRY_WHERE, "",
			new FinderColumn<>(
				"nullConvertibleEntry.", "name", FinderColumn.Type.STRING, "=",
				true, true, NullConvertibleEntry::getName));

		NullConvertibleEntryUtil.setPersistence(this);
	}

	public void destroy() {
		NullConvertibleEntryUtil.setPersistence(null);

		dummyEntityCache.removeCache(NullConvertibleEntryImpl.class.getName());
	}

	private static final String _SQL_SELECT_NULLCONVERTIBLEENTRY =
		"SELECT nullConvertibleEntry FROM NullConvertibleEntry nullConvertibleEntry";

	private static final String _SQL_SELECT_NULLCONVERTIBLEENTRY_WHERE =
		"SELECT nullConvertibleEntry FROM NullConvertibleEntry nullConvertibleEntry WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No NullConvertibleEntry exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		NullConvertibleEntryPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return dummyFinderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-2008350953