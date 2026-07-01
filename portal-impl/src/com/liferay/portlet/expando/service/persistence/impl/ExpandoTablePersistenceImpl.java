/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.expando.service.persistence.impl;

import com.liferay.expando.kernel.exception.NoSuchTableException;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.model.ExpandoTableTable;
import com.liferay.expando.kernel.service.persistence.ExpandoTablePersistence;
import com.liferay.expando.kernel.service.persistence.ExpandoTableUtil;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelperUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portlet.expando.model.impl.ExpandoTableImpl;
import com.liferay.portlet.expando.model.impl.ExpandoTableModelImpl;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence implementation for the expando table service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class ExpandoTablePersistenceImpl
	extends BasePersistenceImpl<ExpandoTable, NoSuchTableException>
	implements ExpandoTablePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ExpandoTableUtil</code> to access the expando table persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ExpandoTableImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<ExpandoTable, NoSuchTableException>
		_collectionPersistenceFinderByC_C;

	/**
	 * Returns an ordered range of all the expando tables where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ExpandoTableModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of expando tables
	 * @param end the upper bound of the range of expando tables (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching expando tables
	 */
	@Override
	public List<ExpandoTable> findByC_C(
		long companyId, long classNameId, int start, int end,
		OrderByComparator<ExpandoTable> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first expando table in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching expando table
	 * @throws NoSuchTableException if a matching expando table could not be found
	 */
	@Override
	public ExpandoTable findByC_C_First(
			long companyId, long classNameId,
			OrderByComparator<ExpandoTable> orderByComparator)
		throws NoSuchTableException {

		return _collectionPersistenceFinderByC_C.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId}, orderByComparator);
	}

	/**
	 * Returns the first expando table in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching expando table, or <code>null</code> if a matching expando table could not be found
	 */
	@Override
	public ExpandoTable fetchByC_C_First(
		long companyId, long classNameId,
		OrderByComparator<ExpandoTable> orderByComparator) {

		return _collectionPersistenceFinderByC_C.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId}, orderByComparator);
	}

	/**
	 * Removes all the expando tables where companyId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 */
	@Override
	public void removeByC_C(long companyId, long classNameId) {
		_collectionPersistenceFinderByC_C.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId});
	}

	/**
	 * Returns the number of expando tables where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the number of matching expando tables
	 */
	@Override
	public int countByC_C(long companyId, long classNameId) {
		return _collectionPersistenceFinderByC_C.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId});
	}

	private UniquePersistenceFinder<ExpandoTable, NoSuchTableException>
		_uniquePersistenceFinderByC_C_N;

	/**
	 * Returns the expando table where companyId = &#63; and classNameId = &#63; and name = &#63; or throws a <code>NoSuchTableException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param name the name
	 * @return the matching expando table
	 * @throws NoSuchTableException if a matching expando table could not be found
	 */
	@Override
	public ExpandoTable findByC_C_N(
			long companyId, long classNameId, String name)
		throws NoSuchTableException {

		return _uniquePersistenceFinderByC_C_N.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId, name});
	}

	/**
	 * Returns the expando table where companyId = &#63; and classNameId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching expando table, or <code>null</code> if a matching expando table could not be found
	 */
	@Override
	public ExpandoTable fetchByC_C_N(
		long companyId, long classNameId, String name, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_C_N.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId, name}, useFinderCache);
	}

	/**
	 * Removes the expando table where companyId = &#63; and classNameId = &#63; and name = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param name the name
	 * @return the expando table that was removed
	 */
	@Override
	public ExpandoTable removeByC_C_N(
			long companyId, long classNameId, String name)
		throws NoSuchTableException {

		ExpandoTable expandoTable = findByC_C_N(companyId, classNameId, name);

		return remove(expandoTable);
	}

	/**
	 * Returns the number of expando tables where companyId = &#63; and classNameId = &#63; and name = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param name the name
	 * @return the number of matching expando tables
	 */
	@Override
	public int countByC_C_N(long companyId, long classNameId, String name) {
		return _uniquePersistenceFinderByC_C_N.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId, name});
	}

	public ExpandoTablePersistenceImpl() {
		setModelClass(ExpandoTable.class);

		setModelImplClass(ExpandoTableImpl.class);
		setModelPKClass(long.class);

		setTable(ExpandoTableTable.INSTANCE);
	}

	/**
	 * Creates a new expando table with the primary key. Does not add the expando table to the database.
	 *
	 * @param tableId the primary key for the new expando table
	 * @return the new expando table
	 */
	@Override
	public ExpandoTable create(long tableId) {
		ExpandoTable expandoTable = new ExpandoTableImpl();

		expandoTable.setNew(true);
		expandoTable.setPrimaryKey(tableId);

		expandoTable.setCompanyId(CompanyThreadLocal.getCompanyId());

		return expandoTable;
	}

	/**
	 * Removes the expando table with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param tableId the primary key of the expando table
	 * @return the expando table that was removed
	 * @throws NoSuchTableException if a expando table with the primary key could not be found
	 */
	@Override
	public ExpandoTable remove(long tableId) throws NoSuchTableException {
		return remove((Serializable)tableId);
	}

	@Override
	protected ExpandoTable removeImpl(ExpandoTable expandoTable) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(expandoTable)) {
				expandoTable = (ExpandoTable)session.get(
					ExpandoTableImpl.class, expandoTable.getPrimaryKeyObj());
			}

			if ((expandoTable != null) &&
				CTPersistenceHelperUtil.isRemove(expandoTable)) {

				session.delete(expandoTable);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (expandoTable != null) {
			clearCache(expandoTable);
		}

		return expandoTable;
	}

	@Override
	public ExpandoTable updateImpl(ExpandoTable expandoTable) {
		boolean isNew = expandoTable.isNew();

		if (!(expandoTable instanceof ExpandoTableModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(expandoTable.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					expandoTable);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in expandoTable proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ExpandoTable implementation " +
					expandoTable.getClass());
		}

		ExpandoTableModelImpl expandoTableModelImpl =
			(ExpandoTableModelImpl)expandoTable;

		Session session = null;

		try {
			session = openSession();

			if (CTPersistenceHelperUtil.isInsert(expandoTable)) {
				if (!isNew) {
					session.evict(
						ExpandoTableImpl.class,
						expandoTable.getPrimaryKeyObj());
				}

				session.save(expandoTable);
			}
			else {
				expandoTable = (ExpandoTable)session.merge(expandoTable);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(expandoTable, false);

		if (isNew) {
			expandoTable.setNew(false);
		}

		expandoTable.resetOriginalValues();

		return expandoTable;
	}

	/**
	 * Returns the expando table with the primary key or throws a <code>NoSuchTableException</code> if it could not be found.
	 *
	 * @param tableId the primary key of the expando table
	 * @return the expando table
	 * @throws NoSuchTableException if a expando table with the primary key could not be found
	 */
	@Override
	public ExpandoTable findByPrimaryKey(long tableId)
		throws NoSuchTableException {

		return findByPrimaryKey((Serializable)tableId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return CTPersistenceHelperUtil.getCTPersistenceHelper();
	}

	/**
	 * Returns the expando table with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param tableId the primary key of the expando table
	 * @return the expando table, or <code>null</code> if a expando table with the primary key could not be found
	 */
	@Override
	public ExpandoTable fetchByPrimaryKey(long tableId) {
		return fetchByPrimaryKey((Serializable)tableId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return EntityCacheUtil.getEntityCache();
	}

	@Override
	protected String getPKDBName() {
		return "tableId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_EXPANDOTABLE;
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
		return ExpandoTableModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "ExpandoTable";
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
		Set<String> ctStrictColumnNames = new HashSet<String>();

		ctControlColumnNames.add("mvccVersion");
		ctControlColumnNames.add("ctCollectionId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("classNameId");
		ctStrictColumnNames.add("name");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("tableId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(
			new String[] {"companyId", "classNameId", "name"});
	}

	/**
	 * Initializes the expando table persistence.
	 */
	public void afterPropertiesSet() {
		_collectionPersistenceFinderByC_C = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"companyId", "classNameId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"companyId", "classNameId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"companyId", "classNameId"}, false),
			_SQL_SELECT_EXPANDOTABLE_WHERE, _SQL_COUNT_EXPANDOTABLE_WHERE,
			ExpandoTableModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"expandoTable.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, ExpandoTable::getCompanyId),
			new FinderColumn<>(
				"expandoTable.", "classNameId", FinderColumn.Type.LONG, "=",
				true, true, ExpandoTable::getClassNameId));

		_uniquePersistenceFinderByC_C_N = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_C_N",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					String.class.getName()
				},
				new String[] {"companyId", "classNameId", "name"}, 0, 4, false,
				ExpandoTable::getCompanyId, ExpandoTable::getClassNameId,
				convertNullFunction(ExpandoTable::getName)),
			_SQL_SELECT_EXPANDOTABLE_WHERE, "",
			new FinderColumn<>(
				"expandoTable.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, ExpandoTable::getCompanyId),
			new FinderColumn<>(
				"expandoTable.", "classNameId", FinderColumn.Type.LONG, "=",
				true, true, ExpandoTable::getClassNameId),
			new FinderColumn<>(
				"expandoTable.", "name", FinderColumn.Type.STRING, "=", true,
				true, ExpandoTable::getName));

		ExpandoTableUtil.setPersistence(this);
	}

	public void destroy() {
		ExpandoTableUtil.setPersistence(null);

		EntityCacheUtil.removeCache(ExpandoTableImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		ExpandoTableModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_EXPANDOTABLE =
		"SELECT expandoTable FROM ExpandoTable expandoTable";

	private static final String _SQL_SELECT_EXPANDOTABLE_WHERE =
		"SELECT expandoTable FROM ExpandoTable expandoTable WHERE ";

	private static final String _SQL_COUNT_EXPANDOTABLE_WHERE =
		"SELECT COUNT(expandoTable) FROM ExpandoTable expandoTable WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1325747031