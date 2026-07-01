/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.impl;

import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.spring.extender.service.ServiceReference;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchCacheMissEntryException;
import com.liferay.portal.tools.service.builder.test.model.CacheMissEntry;
import com.liferay.portal.tools.service.builder.test.model.CacheMissEntryTable;
import com.liferay.portal.tools.service.builder.test.model.impl.CacheMissEntryImpl;
import com.liferay.portal.tools.service.builder.test.model.impl.CacheMissEntryModelImpl;
import com.liferay.portal.tools.service.builder.test.service.persistence.CacheMissEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.CacheMissEntryUtil;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence implementation for the cache miss entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class CacheMissEntryPersistenceImpl
	extends BasePersistenceImpl<CacheMissEntry, NoSuchCacheMissEntryException>
	implements CacheMissEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CacheMissEntryUtil</code> to access the cache miss entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CacheMissEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	public CacheMissEntryPersistenceImpl() {
		setModelClass(CacheMissEntry.class);

		setModelImplClass(CacheMissEntryImpl.class);
		setModelPKClass(long.class);

		setTable(CacheMissEntryTable.INSTANCE);
	}

	/**
	 * Creates a new cache miss entry with the primary key. Does not add the cache miss entry to the database.
	 *
	 * @param cacheMissEntryId the primary key for the new cache miss entry
	 * @return the new cache miss entry
	 */
	@Override
	public CacheMissEntry create(long cacheMissEntryId) {
		CacheMissEntry cacheMissEntry = new CacheMissEntryImpl();

		cacheMissEntry.setNew(true);
		cacheMissEntry.setPrimaryKey(cacheMissEntryId);

		return cacheMissEntry;
	}

	/**
	 * Removes the cache miss entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param cacheMissEntryId the primary key of the cache miss entry
	 * @return the cache miss entry that was removed
	 * @throws NoSuchCacheMissEntryException if a cache miss entry with the primary key could not be found
	 */
	@Override
	public CacheMissEntry remove(long cacheMissEntryId)
		throws NoSuchCacheMissEntryException {

		return remove((Serializable)cacheMissEntryId);
	}

	@Override
	protected CacheMissEntry removeImpl(CacheMissEntry cacheMissEntry) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(cacheMissEntry)) {
				cacheMissEntry = (CacheMissEntry)session.get(
					CacheMissEntryImpl.class,
					cacheMissEntry.getPrimaryKeyObj());
			}

			if ((cacheMissEntry != null) &&
				ctPersistenceHelper.isRemove(cacheMissEntry)) {

				session.delete(cacheMissEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (cacheMissEntry != null) {
			clearCache(cacheMissEntry);
		}

		return cacheMissEntry;
	}

	@Override
	public CacheMissEntry updateImpl(CacheMissEntry cacheMissEntry) {
		boolean isNew = cacheMissEntry.isNew();

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(cacheMissEntry)) {
				if (!isNew) {
					session.evict(
						CacheMissEntryImpl.class,
						cacheMissEntry.getPrimaryKeyObj());
				}

				session.save(cacheMissEntry);
			}
			else {
				cacheMissEntry = (CacheMissEntry)session.merge(cacheMissEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(cacheMissEntry, false);

		if (isNew) {
			cacheMissEntry.setNew(false);
		}

		cacheMissEntry.resetOriginalValues();

		return cacheMissEntry;
	}

	/**
	 * Returns the cache miss entry with the primary key or throws a <code>NoSuchCacheMissEntryException</code> if it could not be found.
	 *
	 * @param cacheMissEntryId the primary key of the cache miss entry
	 * @return the cache miss entry
	 * @throws NoSuchCacheMissEntryException if a cache miss entry with the primary key could not be found
	 */
	@Override
	public CacheMissEntry findByPrimaryKey(long cacheMissEntryId)
		throws NoSuchCacheMissEntryException {

		return findByPrimaryKey((Serializable)cacheMissEntryId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the cache miss entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param cacheMissEntryId the primary key of the cache miss entry
	 * @return the cache miss entry, or <code>null</code> if a cache miss entry with the primary key could not be found
	 */
	@Override
	public CacheMissEntry fetchByPrimaryKey(long cacheMissEntryId) {
		return fetchByPrimaryKey((Serializable)cacheMissEntryId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return dummyEntityCache;
	}

	@Override
	protected String getPKDBName() {
		return "cacheMissEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CACHEMISSENTRY;
	}

	@Override
	public Set<String> getCTColumnNames(
		CTColumnResolutionType ctColumnResolutionType) {

		return _ctColumnNamesMap.getOrDefault(
			ctColumnResolutionType, Collections.emptySet());
	}

	@Override
	public List<String> getMappingTableNames() {
		return _mappingTableNames;
	}

	@Override
	public Map<String, Integer> getTableColumnsMap() {
		return CacheMissEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "CacheMissEntry";
	}

	@Override
	public List<String[]> getUniqueIndexColumnNames() {
		return _uniqueIndexColumnNames;
	}

	private static final Map<CTColumnResolutionType, Set<String>>
		_ctColumnNamesMap = new EnumMap<CTColumnResolutionType, Set<String>>(
			CTColumnResolutionType.class);
	private static final List<String> _mappingTableNames =
		new ArrayList<String>();
	private static final List<String[]> _uniqueIndexColumnNames =
		new ArrayList<String[]>();

	static {
		Set<String> ctControlColumnNames = new HashSet<String>();

		ctControlColumnNames.add("mvccVersion");
		ctControlColumnNames.add("ctCollectionId");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("cacheMissEntryId"));
	}

	/**
	 * Initializes the cache miss entry persistence.
	 */
	public void afterPropertiesSet() {
		CacheMissEntryUtil.setPersistence(this);
	}

	public void destroy() {
		CacheMissEntryUtil.setPersistence(null);

		dummyEntityCache.removeCache(CacheMissEntryImpl.class.getName());
	}

	@ServiceReference(type = CTPersistenceHelper.class)
	protected CTPersistenceHelper ctPersistenceHelper;

	private static final String _SQL_SELECT_CACHEMISSENTRY =
		"SELECT cacheMissEntry FROM CacheMissEntry cacheMissEntry";

	@Override
	protected FinderCache getFinderCache() {
		return dummyFinderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-405011163