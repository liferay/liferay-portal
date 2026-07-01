/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.sample.service.persistence.impl;

import com.liferay.change.tracking.sample.exception.NoSuchCTSChildException;
import com.liferay.change.tracking.sample.model.CTSChild;
import com.liferay.change.tracking.sample.model.CTSChildTable;
import com.liferay.change.tracking.sample.model.impl.CTSChildImpl;
import com.liferay.change.tracking.sample.model.impl.CTSChildModelImpl;
import com.liferay.change.tracking.sample.service.persistence.CTSChildPersistence;
import com.liferay.change.tracking.sample.service.persistence.CTSChildUtil;
import com.liferay.change.tracking.sample.service.persistence.impl.constants.CTSPersistenceConstants;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the cts child service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = CTSChildPersistence.class)
public class CTSChildPersistenceImpl
	extends BasePersistenceImpl<CTSChild, NoSuchCTSChildException>
	implements CTSChildPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CTSChildUtil</code> to access the cts child persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CTSChildImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<CTSChild, NoSuchCTSChildException>
		_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the cts childs where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSChildModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cts childs
	 * @param end the upper bound of the range of cts childs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cts childs
	 */
	@Override
	public List<CTSChild> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CTSChild> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			finderCache, new Object[] {companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cts child in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cts child
	 * @throws NoSuchCTSChildException if a matching cts child could not be found
	 */
	@Override
	public CTSChild findByCompanyId_First(
			long companyId, OrderByComparator<CTSChild> orderByComparator)
		throws NoSuchCTSChildException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Returns the first cts child in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cts child, or <code>null</code> if a matching cts child could not be found
	 */
	@Override
	public CTSChild fetchByCompanyId_First(
		long companyId, OrderByComparator<CTSChild> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			finderCache, new Object[] {companyId}, orderByComparator);
	}

	/**
	 * Removes all the cts childs where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			finderCache, new Object[] {companyId});
	}

	/**
	 * Returns the number of cts childs where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching cts childs
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			finderCache, new Object[] {companyId});
	}

	private CollectionPersistenceFinder<CTSChild, NoSuchCTSChildException>
		_collectionPersistenceFinderByC_C;

	/**
	 * Returns an ordered range of all the cts childs where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSChildModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @param start the lower bound of the range of cts childs
	 * @param end the upper bound of the range of cts childs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cts childs
	 */
	@Override
	public List<CTSChild> findByC_C(
		long companyId, long ctsGrandParentId, int start, int end,
		OrderByComparator<CTSChild> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C.find(
			finderCache, new Object[] {companyId, ctsGrandParentId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cts child in the ordered set where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cts child
	 * @throws NoSuchCTSChildException if a matching cts child could not be found
	 */
	@Override
	public CTSChild findByC_C_First(
			long companyId, long ctsGrandParentId,
			OrderByComparator<CTSChild> orderByComparator)
		throws NoSuchCTSChildException {

		return _collectionPersistenceFinderByC_C.findFirst(
			finderCache, new Object[] {companyId, ctsGrandParentId},
			orderByComparator);
	}

	/**
	 * Returns the first cts child in the ordered set where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cts child, or <code>null</code> if a matching cts child could not be found
	 */
	@Override
	public CTSChild fetchByC_C_First(
		long companyId, long ctsGrandParentId,
		OrderByComparator<CTSChild> orderByComparator) {

		return _collectionPersistenceFinderByC_C.fetchFirst(
			finderCache, new Object[] {companyId, ctsGrandParentId},
			orderByComparator);
	}

	/**
	 * Removes all the cts childs where companyId = &#63; and ctsGrandParentId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 */
	@Override
	public void removeByC_C(long companyId, long ctsGrandParentId) {
		_collectionPersistenceFinderByC_C.remove(
			finderCache, new Object[] {companyId, ctsGrandParentId});
	}

	/**
	 * Returns the number of cts childs where companyId = &#63; and ctsGrandParentId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param ctsGrandParentId the cts grand parent ID
	 * @return the number of matching cts childs
	 */
	@Override
	public int countByC_C(long companyId, long ctsGrandParentId) {
		return _collectionPersistenceFinderByC_C.count(
			finderCache, new Object[] {companyId, ctsGrandParentId});
	}

	private CollectionPersistenceFinder<CTSChild, NoSuchCTSChildException>
		_collectionPersistenceFinderByC_P;

	/**
	 * Returns an ordered range of all the cts childs where companyId = &#63; and parentCTSChildId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CTSChildModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentCTSChildId the parent cts child ID
	 * @param start the lower bound of the range of cts childs
	 * @param end the upper bound of the range of cts childs (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cts childs
	 */
	@Override
	public List<CTSChild> findByC_P(
		long companyId, long parentCTSChildId, int start, int end,
		OrderByComparator<CTSChild> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByC_P.find(
			finderCache, new Object[] {companyId, parentCTSChildId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cts child in the ordered set where companyId = &#63; and parentCTSChildId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentCTSChildId the parent cts child ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cts child
	 * @throws NoSuchCTSChildException if a matching cts child could not be found
	 */
	@Override
	public CTSChild findByC_P_First(
			long companyId, long parentCTSChildId,
			OrderByComparator<CTSChild> orderByComparator)
		throws NoSuchCTSChildException {

		return _collectionPersistenceFinderByC_P.findFirst(
			finderCache, new Object[] {companyId, parentCTSChildId},
			orderByComparator);
	}

	/**
	 * Returns the first cts child in the ordered set where companyId = &#63; and parentCTSChildId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentCTSChildId the parent cts child ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cts child, or <code>null</code> if a matching cts child could not be found
	 */
	@Override
	public CTSChild fetchByC_P_First(
		long companyId, long parentCTSChildId,
		OrderByComparator<CTSChild> orderByComparator) {

		return _collectionPersistenceFinderByC_P.fetchFirst(
			finderCache, new Object[] {companyId, parentCTSChildId},
			orderByComparator);
	}

	/**
	 * Removes all the cts childs where companyId = &#63; and parentCTSChildId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param parentCTSChildId the parent cts child ID
	 */
	@Override
	public void removeByC_P(long companyId, long parentCTSChildId) {
		_collectionPersistenceFinderByC_P.remove(
			finderCache, new Object[] {companyId, parentCTSChildId});
	}

	/**
	 * Returns the number of cts childs where companyId = &#63; and parentCTSChildId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentCTSChildId the parent cts child ID
	 * @return the number of matching cts childs
	 */
	@Override
	public int countByC_P(long companyId, long parentCTSChildId) {
		return _collectionPersistenceFinderByC_P.count(
			finderCache, new Object[] {companyId, parentCTSChildId});
	}

	public CTSChildPersistenceImpl() {
		setModelClass(CTSChild.class);

		setModelImplClass(CTSChildImpl.class);
		setModelPKClass(long.class);

		setTable(CTSChildTable.INSTANCE);
	}

	/**
	 * Creates a new cts child with the primary key. Does not add the cts child to the database.
	 *
	 * @param ctsChildId the primary key for the new cts child
	 * @return the new cts child
	 */
	@Override
	public CTSChild create(long ctsChildId) {
		CTSChild ctsChild = new CTSChildImpl();

		ctsChild.setNew(true);
		ctsChild.setPrimaryKey(ctsChildId);

		ctsChild.setCompanyId(CompanyThreadLocal.getCompanyId());

		return ctsChild;
	}

	/**
	 * Removes the cts child with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param ctsChildId the primary key of the cts child
	 * @return the cts child that was removed
	 * @throws NoSuchCTSChildException if a cts child with the primary key could not be found
	 */
	@Override
	public CTSChild remove(long ctsChildId) throws NoSuchCTSChildException {
		return remove((Serializable)ctsChildId);
	}

	@Override
	protected CTSChild removeImpl(CTSChild ctsChild) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(ctsChild)) {
				ctsChild = (CTSChild)session.get(
					CTSChildImpl.class, ctsChild.getPrimaryKeyObj());
			}

			if ((ctsChild != null) && ctPersistenceHelper.isRemove(ctsChild)) {
				session.delete(ctsChild);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (ctsChild != null) {
			clearCache(ctsChild);
		}

		return ctsChild;
	}

	@Override
	public CTSChild updateImpl(CTSChild ctsChild) {
		boolean isNew = ctsChild.isNew();

		if (!(ctsChild instanceof CTSChildModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(ctsChild.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(ctsChild);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in ctsChild proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CTSChild implementation " +
					ctsChild.getClass());
		}

		CTSChildModelImpl ctsChildModelImpl = (CTSChildModelImpl)ctsChild;

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(ctsChild)) {
				if (!isNew) {
					session.evict(
						CTSChildImpl.class, ctsChild.getPrimaryKeyObj());
				}

				session.save(ctsChild);
			}
			else {
				ctsChild = (CTSChild)session.merge(ctsChild);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(ctsChild, false);

		if (isNew) {
			ctsChild.setNew(false);
		}

		ctsChild.resetOriginalValues();

		return ctsChild;
	}

	/**
	 * Returns the cts child with the primary key or throws a <code>NoSuchCTSChildException</code> if it could not be found.
	 *
	 * @param ctsChildId the primary key of the cts child
	 * @return the cts child
	 * @throws NoSuchCTSChildException if a cts child with the primary key could not be found
	 */
	@Override
	public CTSChild findByPrimaryKey(long ctsChildId)
		throws NoSuchCTSChildException {

		return findByPrimaryKey((Serializable)ctsChildId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the cts child with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param ctsChildId the primary key of the cts child
	 * @return the cts child, or <code>null</code> if a cts child with the primary key could not be found
	 */
	@Override
	public CTSChild fetchByPrimaryKey(long ctsChildId) {
		return fetchByPrimaryKey((Serializable)ctsChildId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "ctsChildId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CTSCHILD;
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
		return CTSChildModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "CTSChild";
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
		Set<String> ctMergeColumnNames = new HashSet<String>();
		Set<String> ctStrictColumnNames = new HashSet<String>();

		ctControlColumnNames.add("mvccVersion");
		ctControlColumnNames.add("ctCollectionId");
		ctStrictColumnNames.add("companyId");
		ctMergeColumnNames.add("ctsGrandParentId");
		ctMergeColumnNames.add("parentCTSChildId");
		ctMergeColumnNames.add("ctsParentName");
		ctMergeColumnNames.add("name");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("ctsChildId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);
	}

	/**
	 * Initializes the cts child persistence.
	 */
	@Activate
	public void activate() {
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
				_SQL_SELECT_CTSCHILD_WHERE, _SQL_COUNT_CTSCHILD_WHERE,
				CTSChildModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"ctsChild.", "companyId", FinderColumn.Type.LONG, "=", true,
					true, CTSChild::getCompanyId));

		_collectionPersistenceFinderByC_C = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"companyId", "ctsGrandParentId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"companyId", "ctsGrandParentId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"companyId", "ctsGrandParentId"}, false),
			_SQL_SELECT_CTSCHILD_WHERE, _SQL_COUNT_CTSCHILD_WHERE,
			CTSChildModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"ctsChild.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, CTSChild::getCompanyId),
			new FinderColumn<>(
				"ctsChild.", "ctsGrandParentId", FinderColumn.Type.LONG, "=",
				true, true, CTSChild::getCtsGrandParentId));

		_collectionPersistenceFinderByC_P = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_P",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"companyId", "parentCTSChildId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_P",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"companyId", "parentCTSChildId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_P",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"companyId", "parentCTSChildId"}, false),
			_SQL_SELECT_CTSCHILD_WHERE, _SQL_COUNT_CTSCHILD_WHERE,
			CTSChildModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"ctsChild.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, CTSChild::getCompanyId),
			new FinderColumn<>(
				"ctsChild.", "parentCTSChildId", FinderColumn.Type.LONG, "=",
				true, true, CTSChild::getParentCTSChildId));

		CTSChildUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CTSChildUtil.setPersistence(null);

		entityCache.removeCache(CTSChildImpl.class.getName());
	}

	@Override
	@Reference(
		target = CTSPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = CTSPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = CTSPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected CTPersistenceHelper ctPersistenceHelper;

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _ENTITY_ALIAS_PREFIX =
		CTSChildModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_CTSCHILD =
		"SELECT ctsChild FROM CTSChild ctsChild";

	private static final String _SQL_SELECT_CTSCHILD_WHERE =
		"SELECT ctsChild FROM CTSChild ctsChild WHERE ";

	private static final String _SQL_COUNT_CTSCHILD_WHERE =
		"SELECT COUNT(ctsChild) FROM CTSChild ctsChild WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CTSChild exists with the key {";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-292165120