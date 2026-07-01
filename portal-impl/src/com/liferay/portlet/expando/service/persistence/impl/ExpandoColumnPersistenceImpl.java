/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.expando.service.persistence.impl;

import com.liferay.expando.kernel.exception.NoSuchColumnException;
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoColumnTable;
import com.liferay.expando.kernel.service.persistence.ExpandoColumnPersistence;
import com.liferay.expando.kernel.service.persistence.ExpandoColumnUtil;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelperUtil;
import com.liferay.portal.kernel.service.persistence.impl.ArrayableFinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.FilterCollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portlet.expando.model.impl.ExpandoColumnImpl;
import com.liferay.portlet.expando.model.impl.ExpandoColumnModelImpl;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence implementation for the expando column service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class ExpandoColumnPersistenceImpl
	extends BasePersistenceImpl<ExpandoColumn, NoSuchColumnException>
	implements ExpandoColumnPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ExpandoColumnUtil</code> to access the expando column persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ExpandoColumnImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FilterCollectionPersistenceFinder
		<ExpandoColumn, NoSuchColumnException>
			_collectionPersistenceFinderByTableId;

	/**
	 * Returns an ordered range of all the expando columns where tableId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ExpandoColumnModelImpl</code>.
	 * </p>
	 *
	 * @param tableId the table ID
	 * @param start the lower bound of the range of expando columns
	 * @param end the upper bound of the range of expando columns (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching expando columns
	 */
	@Override
	public List<ExpandoColumn> findByTableId(
		long tableId, int start, int end,
		OrderByComparator<ExpandoColumn> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByTableId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {tableId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first expando column in the ordered set where tableId = &#63;.
	 *
	 * @param tableId the table ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching expando column
	 * @throws NoSuchColumnException if a matching expando column could not be found
	 */
	@Override
	public ExpandoColumn findByTableId_First(
			long tableId, OrderByComparator<ExpandoColumn> orderByComparator)
		throws NoSuchColumnException {

		return _collectionPersistenceFinderByTableId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {tableId},
			orderByComparator);
	}

	/**
	 * Returns the first expando column in the ordered set where tableId = &#63;.
	 *
	 * @param tableId the table ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching expando column, or <code>null</code> if a matching expando column could not be found
	 */
	@Override
	public ExpandoColumn fetchByTableId_First(
		long tableId, OrderByComparator<ExpandoColumn> orderByComparator) {

		return _collectionPersistenceFinderByTableId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {tableId},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the expando columns that the user has permissions to view where tableId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ExpandoColumnModelImpl</code>.
	 * </p>
	 *
	 * @param tableId the table ID
	 * @param start the lower bound of the range of expando columns
	 * @param end the upper bound of the range of expando columns (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching expando columns that the user has permission to view
	 */
	@Override
	public List<ExpandoColumn> filterFindByTableId(
		long tableId, int start, int end,
		OrderByComparator<ExpandoColumn> orderByComparator) {

		return _collectionPersistenceFinderByTableId.filterFind(
			FinderCacheUtil.getFinderCache(), new Object[] {tableId}, start,
			end, orderByComparator);
	}

	/**
	 * Removes all the expando columns where tableId = &#63; from the database.
	 *
	 * @param tableId the table ID
	 */
	@Override
	public void removeByTableId(long tableId) {
		_collectionPersistenceFinderByTableId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {tableId});
	}

	/**
	 * Returns the number of expando columns where tableId = &#63;.
	 *
	 * @param tableId the table ID
	 * @return the number of matching expando columns
	 */
	@Override
	public int countByTableId(long tableId) {
		return _collectionPersistenceFinderByTableId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {tableId});
	}

	/**
	 * Returns the number of expando columns that the user has permission to view where tableId = &#63;.
	 *
	 * @param tableId the table ID
	 * @return the number of matching expando columns that the user has permission to view
	 */
	@Override
	public int filterCountByTableId(long tableId) {
		return _collectionPersistenceFinderByTableId.filterCount(
			FinderCacheUtil.getFinderCache(), new Object[] {tableId});
	}

	private FilterCollectionPersistenceFinder
		<ExpandoColumn, NoSuchColumnException>
			_collectionPersistenceFinderByT_N;
	private UniquePersistenceFinder<ExpandoColumn, NoSuchColumnException>
		_uniquePersistenceFinderByT_N;

	/**
	 * Returns an ordered range of all the expando columns where tableId = &#63; and name = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ExpandoColumnModelImpl</code>.
	 * </p>
	 *
	 * @param tableId the table ID
	 * @param names the names
	 * @param start the lower bound of the range of expando columns
	 * @param end the upper bound of the range of expando columns (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching expando columns
	 */
	@Override
	public List<ExpandoColumn> findByT_N(
		long tableId, String[] names, int start, int end,
		OrderByComparator<ExpandoColumn> orderByComparator,
		boolean useFinderCache) {

		names = ArrayUtil.sortedUnique(names);

		if (names.length == 1) {
			ExpandoColumn expandoColumn = fetchByT_N(
				tableId, names[0], useFinderCache);

			if (expandoColumn == null) {
				return Collections.emptyList();
			}
			else {
				List<ExpandoColumn> list = new ArrayList<ExpandoColumn>(1);

				list.add(expandoColumn);

				return list;
			}
		}

		return _collectionPersistenceFinderByT_N.find(
			FinderCacheUtil.getFinderCache(), new Object[] {tableId, names},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the expando column where tableId = &#63; and name = &#63; or throws a <code>NoSuchColumnException</code> if it could not be found.
	 *
	 * @param tableId the table ID
	 * @param name the name
	 * @return the matching expando column
	 * @throws NoSuchColumnException if a matching expando column could not be found
	 */
	@Override
	public ExpandoColumn findByT_N(long tableId, String name)
		throws NoSuchColumnException {

		return _uniquePersistenceFinderByT_N.find(
			FinderCacheUtil.getFinderCache(), new Object[] {tableId, name});
	}

	/**
	 * Returns the expando column where tableId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param tableId the table ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching expando column, or <code>null</code> if a matching expando column could not be found
	 */
	@Override
	public ExpandoColumn fetchByT_N(
		long tableId, String name, boolean useFinderCache) {

		return _uniquePersistenceFinderByT_N.fetch(
			FinderCacheUtil.getFinderCache(), new Object[] {tableId, name},
			useFinderCache);
	}

	/**
	 * Removes the expando column where tableId = &#63; and name = &#63; from the database.
	 *
	 * @param tableId the table ID
	 * @param name the name
	 * @return the expando column that was removed
	 */
	@Override
	public ExpandoColumn removeByT_N(long tableId, String name)
		throws NoSuchColumnException {

		ExpandoColumn expandoColumn = findByT_N(tableId, name);

		return remove(expandoColumn);
	}

	/**
	 * Returns the number of expando columns where tableId = &#63; and name = &#63;.
	 *
	 * @param tableId the table ID
	 * @param name the name
	 * @return the number of matching expando columns
	 */
	@Override
	public int countByT_N(long tableId, String name) {
		return _collectionPersistenceFinderByT_N.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {tableId, new String[] {name}});
	}

	/**
	 * Returns the number of expando columns where tableId = &#63; and name = any &#63;.
	 *
	 * @param tableId the table ID
	 * @param names the names
	 * @return the number of matching expando columns
	 */
	@Override
	public int countByT_N(long tableId, String[] names) {
		return _collectionPersistenceFinderByT_N.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {tableId, ArrayUtil.sortedUnique(names)});
	}

	/**
	 * Returns the number of expando columns that the user has permission to view where tableId = &#63; and name = &#63;.
	 *
	 * @param tableId the table ID
	 * @param name the name
	 * @return the number of matching expando columns that the user has permission to view
	 */
	@Override
	public int filterCountByT_N(long tableId, String name) {
		return _collectionPersistenceFinderByT_N.filterCount(
			FinderCacheUtil.getFinderCache(),
			new Object[] {tableId, new String[] {name}});
	}

	/**
	 * Returns the number of expando columns that the user has permission to view where tableId = &#63; and name = any &#63;.
	 *
	 * @param tableId the table ID
	 * @param names the names
	 * @return the number of matching expando columns that the user has permission to view
	 */
	@Override
	public int filterCountByT_N(long tableId, String[] names) {
		return _collectionPersistenceFinderByT_N.filterCount(
			FinderCacheUtil.getFinderCache(),
			new Object[] {tableId, ArrayUtil.sortedUnique(names)});
	}

	public ExpandoColumnPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(ExpandoColumn.class);

		setModelImplClass(ExpandoColumnImpl.class);
		setModelPKClass(long.class);

		setTable(ExpandoColumnTable.INSTANCE);
	}

	/**
	 * Creates a new expando column with the primary key. Does not add the expando column to the database.
	 *
	 * @param columnId the primary key for the new expando column
	 * @return the new expando column
	 */
	@Override
	public ExpandoColumn create(long columnId) {
		ExpandoColumn expandoColumn = new ExpandoColumnImpl();

		expandoColumn.setNew(true);
		expandoColumn.setPrimaryKey(columnId);

		expandoColumn.setCompanyId(CompanyThreadLocal.getCompanyId());

		return expandoColumn;
	}

	/**
	 * Removes the expando column with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param columnId the primary key of the expando column
	 * @return the expando column that was removed
	 * @throws NoSuchColumnException if a expando column with the primary key could not be found
	 */
	@Override
	public ExpandoColumn remove(long columnId) throws NoSuchColumnException {
		return remove((Serializable)columnId);
	}

	@Override
	protected ExpandoColumn removeImpl(ExpandoColumn expandoColumn) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(expandoColumn)) {
				expandoColumn = (ExpandoColumn)session.get(
					ExpandoColumnImpl.class, expandoColumn.getPrimaryKeyObj());
			}

			if ((expandoColumn != null) &&
				CTPersistenceHelperUtil.isRemove(expandoColumn)) {

				session.delete(expandoColumn);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (expandoColumn != null) {
			clearCache(expandoColumn);
		}

		return expandoColumn;
	}

	@Override
	public ExpandoColumn updateImpl(ExpandoColumn expandoColumn) {
		boolean isNew = expandoColumn.isNew();

		if (!(expandoColumn instanceof ExpandoColumnModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(expandoColumn.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					expandoColumn);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in expandoColumn proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ExpandoColumn implementation " +
					expandoColumn.getClass());
		}

		ExpandoColumnModelImpl expandoColumnModelImpl =
			(ExpandoColumnModelImpl)expandoColumn;

		if (!expandoColumnModelImpl.hasSetModifiedDate()) {
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			Date date = new Date();

			if (serviceContext == null) {
				expandoColumn.setModifiedDate(date);
			}
			else {
				expandoColumn.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (CTPersistenceHelperUtil.isInsert(expandoColumn)) {
				if (!isNew) {
					session.evict(
						ExpandoColumnImpl.class,
						expandoColumn.getPrimaryKeyObj());
				}

				session.save(expandoColumn);
			}
			else {
				expandoColumn = (ExpandoColumn)session.merge(expandoColumn);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(expandoColumn, false);

		if (isNew) {
			expandoColumn.setNew(false);
		}

		expandoColumn.resetOriginalValues();

		return expandoColumn;
	}

	/**
	 * Returns the expando column with the primary key or throws a <code>NoSuchColumnException</code> if it could not be found.
	 *
	 * @param columnId the primary key of the expando column
	 * @return the expando column
	 * @throws NoSuchColumnException if a expando column with the primary key could not be found
	 */
	@Override
	public ExpandoColumn findByPrimaryKey(long columnId)
		throws NoSuchColumnException {

		return findByPrimaryKey((Serializable)columnId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return CTPersistenceHelperUtil.getCTPersistenceHelper();
	}

	/**
	 * Returns the expando column with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param columnId the primary key of the expando column
	 * @return the expando column, or <code>null</code> if a expando column with the primary key could not be found
	 */
	@Override
	public ExpandoColumn fetchByPrimaryKey(long columnId) {
		return fetchByPrimaryKey((Serializable)columnId);
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
		return "columnId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_EXPANDOCOLUMN;
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
		return ExpandoColumnModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "ExpandoColumn";
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
		Set<String> ctIgnoreColumnNames = new HashSet<String>();
		Set<String> ctMergeColumnNames = new HashSet<String>();
		Set<String> ctStrictColumnNames = new HashSet<String>();

		ctControlColumnNames.add("mvccVersion");
		ctControlColumnNames.add("ctCollectionId");
		ctStrictColumnNames.add("companyId");
		ctIgnoreColumnNames.add("modifiedDate");
		ctStrictColumnNames.add("tableId");
		ctStrictColumnNames.add("name");
		ctMergeColumnNames.add("type_");
		ctMergeColumnNames.add("defaultData");
		ctMergeColumnNames.add("typeSettings");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("columnId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"tableId", "name"});
	}

	/**
	 * Initializes the expando column persistence.
	 */
	public void afterPropertiesSet() {
		_collectionPersistenceFinderByTableId =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByTableId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"tableId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByTableId",
					new String[] {Long.class.getName()},
					new String[] {"tableId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByTableId",
					new String[] {Long.class.getName()},
					new String[] {"tableId"}, false),
				_SQL_SELECT_EXPANDOCOLUMN_WHERE, _SQL_COUNT_EXPANDOCOLUMN_WHERE,
				ExpandoColumnModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"expandoColumn.", "tableId", FinderColumn.Type.LONG, "=",
					true, true, ExpandoColumn::getTableId));

		_collectionPersistenceFinderByT_N =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByT_N",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"tableId", "name"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByT_N",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"tableId", "name"}, 0, 2, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByT_N",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"tableId", "name"}, 0, 2, false, null),
				_SQL_SELECT_EXPANDOCOLUMN_WHERE, _SQL_COUNT_EXPANDOCOLUMN_WHERE,
				ExpandoColumnModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"expandoColumn.", "tableId", FinderColumn.Type.LONG, "=",
					true, true, ExpandoColumn::getTableId),
				new ArrayableFinderColumn<>(
					"expandoColumn.", "name", FinderColumn.Type.STRING, "=",
					false, true, true, ExpandoColumn::getName));

		_uniquePersistenceFinderByT_N = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByT_N",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"tableId", "name"}, 0, 2, false,
				ExpandoColumn::getTableId,
				convertNullFunction(ExpandoColumn::getName)),
			_SQL_SELECT_EXPANDOCOLUMN_WHERE, "",
			new FinderColumn<>(
				"expandoColumn.", "tableId", FinderColumn.Type.LONG, "=", true,
				true, ExpandoColumn::getTableId),
			new FinderColumn<>(
				"expandoColumn.", "name", FinderColumn.Type.STRING, "=", true,
				true, ExpandoColumn::getName));

		ExpandoColumnUtil.setPersistence(this);
	}

	public void destroy() {
		ExpandoColumnUtil.setPersistence(null);

		EntityCacheUtil.removeCache(ExpandoColumnImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		ExpandoColumnModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_EXPANDOCOLUMN =
		"SELECT expandoColumn FROM ExpandoColumn expandoColumn";

	private static final String _SQL_SELECT_EXPANDOCOLUMN_WHERE =
		"SELECT expandoColumn FROM ExpandoColumn expandoColumn WHERE ";

	private static final String _SQL_COUNT_EXPANDOCOLUMN_WHERE =
		"SELECT COUNT(expandoColumn) FROM ExpandoColumn expandoColumn WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No ExpandoColumn exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		ExpandoColumnPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"type"});

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1599522004