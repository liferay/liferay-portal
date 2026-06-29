/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.expando.service.persistence.impl;

import com.liferay.expando.kernel.exception.NoSuchRowException;
import com.liferay.expando.kernel.model.ExpandoRow;
import com.liferay.expando.kernel.model.ExpandoRowTable;
import com.liferay.expando.kernel.service.persistence.ExpandoRowPersistence;
import com.liferay.expando.kernel.service.persistence.ExpandoRowUtil;
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
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portlet.expando.model.impl.ExpandoRowImpl;
import com.liferay.portlet.expando.model.impl.ExpandoRowModelImpl;

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
 * The persistence implementation for the expando row service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class ExpandoRowPersistenceImpl
	extends BasePersistenceImpl<ExpandoRow, NoSuchRowException>
	implements ExpandoRowPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>ExpandoRowUtil</code> to access the expando row persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		ExpandoRowImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<ExpandoRow, NoSuchRowException>
		_collectionPersistenceFinderByTableId;

	/**
	 * Returns an ordered range of all the expando rows where tableId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ExpandoRowModelImpl</code>.
	 * </p>
	 *
	 * @param tableId the table ID
	 * @param start the lower bound of the range of expando rows
	 * @param end the upper bound of the range of expando rows (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching expando rows
	 */
	@Override
	public List<ExpandoRow> findByTableId(
		long tableId, int start, int end,
		OrderByComparator<ExpandoRow> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByTableId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {tableId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first expando row in the ordered set where tableId = &#63;.
	 *
	 * @param tableId the table ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching expando row
	 * @throws NoSuchRowException if a matching expando row could not be found
	 */
	@Override
	public ExpandoRow findByTableId_First(
			long tableId, OrderByComparator<ExpandoRow> orderByComparator)
		throws NoSuchRowException {

		return _collectionPersistenceFinderByTableId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {tableId},
			orderByComparator);
	}

	/**
	 * Returns the first expando row in the ordered set where tableId = &#63;.
	 *
	 * @param tableId the table ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching expando row, or <code>null</code> if a matching expando row could not be found
	 */
	@Override
	public ExpandoRow fetchByTableId_First(
		long tableId, OrderByComparator<ExpandoRow> orderByComparator) {

		return _collectionPersistenceFinderByTableId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {tableId},
			orderByComparator);
	}

	/**
	 * Removes all the expando rows where tableId = &#63; from the database.
	 *
	 * @param tableId the table ID
	 */
	@Override
	public void removeByTableId(long tableId) {
		_collectionPersistenceFinderByTableId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {tableId});
	}

	/**
	 * Returns the number of expando rows where tableId = &#63;.
	 *
	 * @param tableId the table ID
	 * @return the number of matching expando rows
	 */
	@Override
	public int countByTableId(long tableId) {
		return _collectionPersistenceFinderByTableId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {tableId});
	}

	private CollectionPersistenceFinder<ExpandoRow, NoSuchRowException>
		_collectionPersistenceFinderByClassPK;

	/**
	 * Returns an ordered range of all the expando rows where classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ExpandoRowModelImpl</code>.
	 * </p>
	 *
	 * @param classPK the class pk
	 * @param start the lower bound of the range of expando rows
	 * @param end the upper bound of the range of expando rows (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching expando rows
	 */
	@Override
	public List<ExpandoRow> findByClassPK(
		long classPK, int start, int end,
		OrderByComparator<ExpandoRow> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByClassPK.find(
			FinderCacheUtil.getFinderCache(), new Object[] {classPK}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first expando row in the ordered set where classPK = &#63;.
	 *
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching expando row
	 * @throws NoSuchRowException if a matching expando row could not be found
	 */
	@Override
	public ExpandoRow findByClassPK_First(
			long classPK, OrderByComparator<ExpandoRow> orderByComparator)
		throws NoSuchRowException {

		return _collectionPersistenceFinderByClassPK.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {classPK},
			orderByComparator);
	}

	/**
	 * Returns the first expando row in the ordered set where classPK = &#63;.
	 *
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching expando row, or <code>null</code> if a matching expando row could not be found
	 */
	@Override
	public ExpandoRow fetchByClassPK_First(
		long classPK, OrderByComparator<ExpandoRow> orderByComparator) {

		return _collectionPersistenceFinderByClassPK.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {classPK},
			orderByComparator);
	}

	/**
	 * Removes all the expando rows where classPK = &#63; from the database.
	 *
	 * @param classPK the class pk
	 */
	@Override
	public void removeByClassPK(long classPK) {
		_collectionPersistenceFinderByClassPK.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {classPK});
	}

	/**
	 * Returns the number of expando rows where classPK = &#63;.
	 *
	 * @param classPK the class pk
	 * @return the number of matching expando rows
	 */
	@Override
	public int countByClassPK(long classPK) {
		return _collectionPersistenceFinderByClassPK.count(
			FinderCacheUtil.getFinderCache(), new Object[] {classPK});
	}

	private UniquePersistenceFinder<ExpandoRow, NoSuchRowException>
		_uniquePersistenceFinderByT_C;

	/**
	 * Returns the expando row where tableId = &#63; and classPK = &#63; or throws a <code>NoSuchRowException</code> if it could not be found.
	 *
	 * @param tableId the table ID
	 * @param classPK the class pk
	 * @return the matching expando row
	 * @throws NoSuchRowException if a matching expando row could not be found
	 */
	@Override
	public ExpandoRow findByT_C(long tableId, long classPK)
		throws NoSuchRowException {

		return _uniquePersistenceFinderByT_C.find(
			FinderCacheUtil.getFinderCache(), new Object[] {tableId, classPK});
	}

	/**
	 * Returns the expando row where tableId = &#63; and classPK = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param tableId the table ID
	 * @param classPK the class pk
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching expando row, or <code>null</code> if a matching expando row could not be found
	 */
	@Override
	public ExpandoRow fetchByT_C(
		long tableId, long classPK, boolean useFinderCache) {

		return _uniquePersistenceFinderByT_C.fetch(
			FinderCacheUtil.getFinderCache(), new Object[] {tableId, classPK},
			useFinderCache);
	}

	/**
	 * Removes the expando row where tableId = &#63; and classPK = &#63; from the database.
	 *
	 * @param tableId the table ID
	 * @param classPK the class pk
	 * @return the expando row that was removed
	 */
	@Override
	public ExpandoRow removeByT_C(long tableId, long classPK)
		throws NoSuchRowException {

		ExpandoRow expandoRow = findByT_C(tableId, classPK);

		return remove(expandoRow);
	}

	/**
	 * Returns the number of expando rows where tableId = &#63; and classPK = &#63;.
	 *
	 * @param tableId the table ID
	 * @param classPK the class pk
	 * @return the number of matching expando rows
	 */
	@Override
	public int countByT_C(long tableId, long classPK) {
		return _uniquePersistenceFinderByT_C.count(
			FinderCacheUtil.getFinderCache(), new Object[] {tableId, classPK});
	}

	public ExpandoRowPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("rowId", "rowId_");

		setDBColumnNames(dbColumnNames);

		setModelClass(ExpandoRow.class);

		setModelImplClass(ExpandoRowImpl.class);
		setModelPKClass(long.class);

		setTable(ExpandoRowTable.INSTANCE);
	}

	/**
	 * Creates a new expando row with the primary key. Does not add the expando row to the database.
	 *
	 * @param rowId the primary key for the new expando row
	 * @return the new expando row
	 */
	@Override
	public ExpandoRow create(long rowId) {
		ExpandoRow expandoRow = new ExpandoRowImpl();

		expandoRow.setNew(true);
		expandoRow.setPrimaryKey(rowId);

		expandoRow.setCompanyId(CompanyThreadLocal.getCompanyId());

		return expandoRow;
	}

	/**
	 * Removes the expando row with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param rowId the primary key of the expando row
	 * @return the expando row that was removed
	 * @throws NoSuchRowException if a expando row with the primary key could not be found
	 */
	@Override
	public ExpandoRow remove(long rowId) throws NoSuchRowException {
		return remove((Serializable)rowId);
	}

	@Override
	protected ExpandoRow removeImpl(ExpandoRow expandoRow) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(expandoRow)) {
				expandoRow = (ExpandoRow)session.get(
					ExpandoRowImpl.class, expandoRow.getPrimaryKeyObj());
			}

			if ((expandoRow != null) &&
				CTPersistenceHelperUtil.isRemove(expandoRow)) {

				session.delete(expandoRow);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (expandoRow != null) {
			clearCache(expandoRow);
		}

		return expandoRow;
	}

	@Override
	public ExpandoRow updateImpl(ExpandoRow expandoRow) {
		boolean isNew = expandoRow.isNew();

		if (!(expandoRow instanceof ExpandoRowModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(expandoRow.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(expandoRow);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in expandoRow proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom ExpandoRow implementation " +
					expandoRow.getClass());
		}

		ExpandoRowModelImpl expandoRowModelImpl =
			(ExpandoRowModelImpl)expandoRow;

		if (!expandoRowModelImpl.hasSetModifiedDate()) {
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			Date date = new Date();

			if (serviceContext == null) {
				expandoRow.setModifiedDate(date);
			}
			else {
				expandoRow.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (CTPersistenceHelperUtil.isInsert(expandoRow)) {
				if (!isNew) {
					session.evict(
						ExpandoRowImpl.class, expandoRow.getPrimaryKeyObj());
				}

				session.save(expandoRow);
			}
			else {
				expandoRow = (ExpandoRow)session.merge(expandoRow);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(expandoRow, false);

		if (isNew) {
			expandoRow.setNew(false);
		}

		expandoRow.resetOriginalValues();

		return expandoRow;
	}

	/**
	 * Returns the expando row with the primary key or throws a <code>NoSuchRowException</code> if it could not be found.
	 *
	 * @param rowId the primary key of the expando row
	 * @return the expando row
	 * @throws NoSuchRowException if a expando row with the primary key could not be found
	 */
	@Override
	public ExpandoRow findByPrimaryKey(long rowId) throws NoSuchRowException {
		return findByPrimaryKey((Serializable)rowId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return CTPersistenceHelperUtil.getCTPersistenceHelper();
	}

	/**
	 * Returns the expando row with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param rowId the primary key of the expando row
	 * @return the expando row, or <code>null</code> if a expando row with the primary key could not be found
	 */
	@Override
	public ExpandoRow fetchByPrimaryKey(long rowId) {
		return fetchByPrimaryKey((Serializable)rowId);
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
		return "rowId_";
	}

	@Override
	protected String getPKFieldName() {
		return "rowId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_EXPANDOROW;
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
		return ExpandoRowModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "ExpandoRow";
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
		Set<String> ctStrictColumnNames = new HashSet<String>();

		ctControlColumnNames.add("mvccVersion");
		ctControlColumnNames.add("ctCollectionId");
		ctStrictColumnNames.add("companyId");
		ctIgnoreColumnNames.add("modifiedDate");
		ctStrictColumnNames.add("tableId");
		ctStrictColumnNames.add("classPK");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("rowId_"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"tableId", "classPK"});
	}

	/**
	 * Initializes the expando row persistence.
	 */
	public void afterPropertiesSet() {
		_collectionPersistenceFinderByTableId =
			new CollectionPersistenceFinder<>(
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
				_SQL_SELECT_EXPANDOROW_WHERE, _SQL_COUNT_EXPANDOROW_WHERE,
				ExpandoRowModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"expandoRow.", "tableId", FinderColumn.Type.LONG, "=", true,
					true, ExpandoRow::getTableId));

		_collectionPersistenceFinderByClassPK =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByClassPK",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"classPK"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByClassPK",
					new String[] {Long.class.getName()},
					new String[] {"classPK"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByClassPK",
					new String[] {Long.class.getName()},
					new String[] {"classPK"}, false),
				_SQL_SELECT_EXPANDOROW_WHERE, _SQL_COUNT_EXPANDOROW_WHERE,
				ExpandoRowModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"expandoRow.", "classPK", FinderColumn.Type.LONG, "=", true,
					true, ExpandoRow::getClassPK));

		_uniquePersistenceFinderByT_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByT_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"tableId", "classPK"}, 0, 0, false,
				ExpandoRow::getTableId, ExpandoRow::getClassPK),
			_SQL_SELECT_EXPANDOROW_WHERE, "",
			new FinderColumn<>(
				"expandoRow.", "tableId", FinderColumn.Type.LONG, "=", true,
				true, ExpandoRow::getTableId),
			new FinderColumn<>(
				"expandoRow.", "classPK", FinderColumn.Type.LONG, "=", true,
				true, ExpandoRow::getClassPK));

		ExpandoRowUtil.setPersistence(this);
	}

	public void destroy() {
		ExpandoRowUtil.setPersistence(null);

		EntityCacheUtil.removeCache(ExpandoRowImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		ExpandoRowModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_EXPANDOROW =
		"SELECT expandoRow FROM ExpandoRow expandoRow";

	private static final String _SQL_SELECT_EXPANDOROW_WHERE =
		"SELECT expandoRow FROM ExpandoRow expandoRow WHERE ";

	private static final String _SQL_COUNT_EXPANDOROW_WHERE =
		"SELECT COUNT(expandoRow) FROM ExpandoRow expandoRow WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No ExpandoRow exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		ExpandoRowPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"rowId"});

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1493847836