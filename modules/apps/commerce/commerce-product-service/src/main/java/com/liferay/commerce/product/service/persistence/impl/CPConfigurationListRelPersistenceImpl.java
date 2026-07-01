/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.persistence.impl;

import com.liferay.commerce.product.exception.NoSuchCPConfigurationListRelException;
import com.liferay.commerce.product.model.CPConfigurationListRel;
import com.liferay.commerce.product.model.CPConfigurationListRelTable;
import com.liferay.commerce.product.model.impl.CPConfigurationListRelImpl;
import com.liferay.commerce.product.model.impl.CPConfigurationListRelModelImpl;
import com.liferay.commerce.product.service.persistence.CPConfigurationListRelPersistence;
import com.liferay.commerce.product.service.persistence.CPConfigurationListRelUtil;
import com.liferay.commerce.product.service.persistence.impl.constants.CommercePersistenceConstants;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
 * The persistence implementation for the cp configuration list rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(service = CPConfigurationListRelPersistence.class)
public class CPConfigurationListRelPersistenceImpl
	extends BasePersistenceImpl
		<CPConfigurationListRel, NoSuchCPConfigurationListRelException>
	implements CPConfigurationListRelPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CPConfigurationListRelUtil</code> to access the cp configuration list rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CPConfigurationListRelImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CPConfigurationListRel, NoSuchCPConfigurationListRelException>
			_collectionPersistenceFinderByCPConfigurationListId;

	/**
	 * Returns an ordered range of all the cp configuration list rels where CPConfigurationListId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPConfigurationListRelModelImpl</code>.
	 * </p>
	 *
	 * @param CPConfigurationListId the cp configuration list ID
	 * @param start the lower bound of the range of cp configuration list rels
	 * @param end the upper bound of the range of cp configuration list rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp configuration list rels
	 */
	@Override
	public List<CPConfigurationListRel> findByCPConfigurationListId(
		long CPConfigurationListId, int start, int end,
		OrderByComparator<CPConfigurationListRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCPConfigurationListId.find(
			finderCache, new Object[] {CPConfigurationListId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp configuration list rel in the ordered set where CPConfigurationListId = &#63;.
	 *
	 * @param CPConfigurationListId the cp configuration list ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp configuration list rel
	 * @throws NoSuchCPConfigurationListRelException if a matching cp configuration list rel could not be found
	 */
	@Override
	public CPConfigurationListRel findByCPConfigurationListId_First(
			long CPConfigurationListId,
			OrderByComparator<CPConfigurationListRel> orderByComparator)
		throws NoSuchCPConfigurationListRelException {

		return _collectionPersistenceFinderByCPConfigurationListId.findFirst(
			finderCache, new Object[] {CPConfigurationListId},
			orderByComparator);
	}

	/**
	 * Returns the first cp configuration list rel in the ordered set where CPConfigurationListId = &#63;.
	 *
	 * @param CPConfigurationListId the cp configuration list ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp configuration list rel, or <code>null</code> if a matching cp configuration list rel could not be found
	 */
	@Override
	public CPConfigurationListRel fetchByCPConfigurationListId_First(
		long CPConfigurationListId,
		OrderByComparator<CPConfigurationListRel> orderByComparator) {

		return _collectionPersistenceFinderByCPConfigurationListId.fetchFirst(
			finderCache, new Object[] {CPConfigurationListId},
			orderByComparator);
	}

	/**
	 * Removes all the cp configuration list rels where CPConfigurationListId = &#63; from the database.
	 *
	 * @param CPConfigurationListId the cp configuration list ID
	 */
	@Override
	public void removeByCPConfigurationListId(long CPConfigurationListId) {
		_collectionPersistenceFinderByCPConfigurationListId.remove(
			finderCache, new Object[] {CPConfigurationListId});
	}

	/**
	 * Returns the number of cp configuration list rels where CPConfigurationListId = &#63;.
	 *
	 * @param CPConfigurationListId the cp configuration list ID
	 * @return the number of matching cp configuration list rels
	 */
	@Override
	public int countByCPConfigurationListId(long CPConfigurationListId) {
		return _collectionPersistenceFinderByCPConfigurationListId.count(
			finderCache, new Object[] {CPConfigurationListId});
	}

	private CollectionPersistenceFinder
		<CPConfigurationListRel, NoSuchCPConfigurationListRelException>
			_collectionPersistenceFinderByC_C;

	/**
	 * Returns an ordered range of all the cp configuration list rels where classNameId = &#63; and CPConfigurationListId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPConfigurationListRelModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param CPConfigurationListId the cp configuration list ID
	 * @param start the lower bound of the range of cp configuration list rels
	 * @param end the upper bound of the range of cp configuration list rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp configuration list rels
	 */
	@Override
	public List<CPConfigurationListRel> findByC_C(
		long classNameId, long CPConfigurationListId, int start, int end,
		OrderByComparator<CPConfigurationListRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C.find(
			finderCache, new Object[] {classNameId, CPConfigurationListId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp configuration list rel in the ordered set where classNameId = &#63; and CPConfigurationListId = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param CPConfigurationListId the cp configuration list ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp configuration list rel
	 * @throws NoSuchCPConfigurationListRelException if a matching cp configuration list rel could not be found
	 */
	@Override
	public CPConfigurationListRel findByC_C_First(
			long classNameId, long CPConfigurationListId,
			OrderByComparator<CPConfigurationListRel> orderByComparator)
		throws NoSuchCPConfigurationListRelException {

		return _collectionPersistenceFinderByC_C.findFirst(
			finderCache, new Object[] {classNameId, CPConfigurationListId},
			orderByComparator);
	}

	/**
	 * Returns the first cp configuration list rel in the ordered set where classNameId = &#63; and CPConfigurationListId = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param CPConfigurationListId the cp configuration list ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp configuration list rel, or <code>null</code> if a matching cp configuration list rel could not be found
	 */
	@Override
	public CPConfigurationListRel fetchByC_C_First(
		long classNameId, long CPConfigurationListId,
		OrderByComparator<CPConfigurationListRel> orderByComparator) {

		return _collectionPersistenceFinderByC_C.fetchFirst(
			finderCache, new Object[] {classNameId, CPConfigurationListId},
			orderByComparator);
	}

	/**
	 * Removes all the cp configuration list rels where classNameId = &#63; and CPConfigurationListId = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param CPConfigurationListId the cp configuration list ID
	 */
	@Override
	public void removeByC_C(long classNameId, long CPConfigurationListId) {
		_collectionPersistenceFinderByC_C.remove(
			finderCache, new Object[] {classNameId, CPConfigurationListId});
	}

	/**
	 * Returns the number of cp configuration list rels where classNameId = &#63; and CPConfigurationListId = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param CPConfigurationListId the cp configuration list ID
	 * @return the number of matching cp configuration list rels
	 */
	@Override
	public int countByC_C(long classNameId, long CPConfigurationListId) {
		return _collectionPersistenceFinderByC_C.count(
			finderCache, new Object[] {classNameId, CPConfigurationListId});
	}

	private UniquePersistenceFinder
		<CPConfigurationListRel, NoSuchCPConfigurationListRelException>
			_uniquePersistenceFinderByC_C_C;

	/**
	 * Returns the cp configuration list rel where classNameId = &#63; and classPK = &#63; and CPConfigurationListId = &#63; or throws a <code>NoSuchCPConfigurationListRelException</code> if it could not be found.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param CPConfigurationListId the cp configuration list ID
	 * @return the matching cp configuration list rel
	 * @throws NoSuchCPConfigurationListRelException if a matching cp configuration list rel could not be found
	 */
	@Override
	public CPConfigurationListRel findByC_C_C(
			long classNameId, long classPK, long CPConfigurationListId)
		throws NoSuchCPConfigurationListRelException {

		return _uniquePersistenceFinderByC_C_C.find(
			finderCache,
			new Object[] {classNameId, classPK, CPConfigurationListId});
	}

	/**
	 * Returns the cp configuration list rel where classNameId = &#63; and classPK = &#63; and CPConfigurationListId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param CPConfigurationListId the cp configuration list ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp configuration list rel, or <code>null</code> if a matching cp configuration list rel could not be found
	 */
	@Override
	public CPConfigurationListRel fetchByC_C_C(
		long classNameId, long classPK, long CPConfigurationListId,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByC_C_C.fetch(
			finderCache,
			new Object[] {classNameId, classPK, CPConfigurationListId},
			useFinderCache);
	}

	/**
	 * Removes the cp configuration list rel where classNameId = &#63; and classPK = &#63; and CPConfigurationListId = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param CPConfigurationListId the cp configuration list ID
	 * @return the cp configuration list rel that was removed
	 */
	@Override
	public CPConfigurationListRel removeByC_C_C(
			long classNameId, long classPK, long CPConfigurationListId)
		throws NoSuchCPConfigurationListRelException {

		CPConfigurationListRel cpConfigurationListRel = findByC_C_C(
			classNameId, classPK, CPConfigurationListId);

		return remove(cpConfigurationListRel);
	}

	/**
	 * Returns the number of cp configuration list rels where classNameId = &#63; and classPK = &#63; and CPConfigurationListId = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param CPConfigurationListId the cp configuration list ID
	 * @return the number of matching cp configuration list rels
	 */
	@Override
	public int countByC_C_C(
		long classNameId, long classPK, long CPConfigurationListId) {

		return _uniquePersistenceFinderByC_C_C.count(
			finderCache,
			new Object[] {classNameId, classPK, CPConfigurationListId});
	}

	public CPConfigurationListRelPersistenceImpl() {
		setModelClass(CPConfigurationListRel.class);

		setModelImplClass(CPConfigurationListRelImpl.class);
		setModelPKClass(long.class);

		setTable(CPConfigurationListRelTable.INSTANCE);
	}

	/**
	 * Creates a new cp configuration list rel with the primary key. Does not add the cp configuration list rel to the database.
	 *
	 * @param CPConfigurationListRelId the primary key for the new cp configuration list rel
	 * @return the new cp configuration list rel
	 */
	@Override
	public CPConfigurationListRel create(long CPConfigurationListRelId) {
		CPConfigurationListRel cpConfigurationListRel =
			new CPConfigurationListRelImpl();

		cpConfigurationListRel.setNew(true);
		cpConfigurationListRel.setPrimaryKey(CPConfigurationListRelId);

		cpConfigurationListRel.setCompanyId(CompanyThreadLocal.getCompanyId());

		return cpConfigurationListRel;
	}

	/**
	 * Removes the cp configuration list rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param CPConfigurationListRelId the primary key of the cp configuration list rel
	 * @return the cp configuration list rel that was removed
	 * @throws NoSuchCPConfigurationListRelException if a cp configuration list rel with the primary key could not be found
	 */
	@Override
	public CPConfigurationListRel remove(long CPConfigurationListRelId)
		throws NoSuchCPConfigurationListRelException {

		return remove((Serializable)CPConfigurationListRelId);
	}

	@Override
	protected CPConfigurationListRel removeImpl(
		CPConfigurationListRel cpConfigurationListRel) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(cpConfigurationListRel)) {
				cpConfigurationListRel = (CPConfigurationListRel)session.get(
					CPConfigurationListRelImpl.class,
					cpConfigurationListRel.getPrimaryKeyObj());
			}

			if ((cpConfigurationListRel != null) &&
				ctPersistenceHelper.isRemove(cpConfigurationListRel)) {

				session.delete(cpConfigurationListRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (cpConfigurationListRel != null) {
			clearCache(cpConfigurationListRel);
		}

		return cpConfigurationListRel;
	}

	@Override
	public CPConfigurationListRel updateImpl(
		CPConfigurationListRel cpConfigurationListRel) {

		boolean isNew = cpConfigurationListRel.isNew();

		if (!(cpConfigurationListRel instanceof
				CPConfigurationListRelModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(cpConfigurationListRel.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					cpConfigurationListRel);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in cpConfigurationListRel proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CPConfigurationListRel implementation " +
					cpConfigurationListRel.getClass());
		}

		CPConfigurationListRelModelImpl cpConfigurationListRelModelImpl =
			(CPConfigurationListRelModelImpl)cpConfigurationListRel;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (cpConfigurationListRel.getCreateDate() == null)) {
			if (serviceContext == null) {
				cpConfigurationListRel.setCreateDate(date);
			}
			else {
				cpConfigurationListRel.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!cpConfigurationListRelModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				cpConfigurationListRel.setModifiedDate(date);
			}
			else {
				cpConfigurationListRel.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(cpConfigurationListRel)) {
				if (!isNew) {
					session.evict(
						CPConfigurationListRelImpl.class,
						cpConfigurationListRel.getPrimaryKeyObj());
				}

				session.save(cpConfigurationListRel);
			}
			else {
				cpConfigurationListRel = (CPConfigurationListRel)session.merge(
					cpConfigurationListRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(cpConfigurationListRel, false);

		if (isNew) {
			cpConfigurationListRel.setNew(false);
		}

		cpConfigurationListRel.resetOriginalValues();

		return cpConfigurationListRel;
	}

	/**
	 * Returns the cp configuration list rel with the primary key or throws a <code>NoSuchCPConfigurationListRelException</code> if it could not be found.
	 *
	 * @param CPConfigurationListRelId the primary key of the cp configuration list rel
	 * @return the cp configuration list rel
	 * @throws NoSuchCPConfigurationListRelException if a cp configuration list rel with the primary key could not be found
	 */
	@Override
	public CPConfigurationListRel findByPrimaryKey(
			long CPConfigurationListRelId)
		throws NoSuchCPConfigurationListRelException {

		return findByPrimaryKey((Serializable)CPConfigurationListRelId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the cp configuration list rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param CPConfigurationListRelId the primary key of the cp configuration list rel
	 * @return the cp configuration list rel, or <code>null</code> if a cp configuration list rel with the primary key could not be found
	 */
	@Override
	public CPConfigurationListRel fetchByPrimaryKey(
		long CPConfigurationListRelId) {

		return fetchByPrimaryKey((Serializable)CPConfigurationListRelId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "CPConfigurationListRelId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CPCONFIGURATIONLISTREL;
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
		return CPConfigurationListRelModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "CPConfigurationListRel";
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
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctStrictColumnNames.add("classNameId");
		ctStrictColumnNames.add("classPK");
		ctMergeColumnNames.add("CPConfigurationListId");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("CPConfigurationListRelId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(
			new String[] {"classNameId", "classPK", "CPConfigurationListId"});
	}

	/**
	 * Initializes the cp configuration list rel persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByCPConfigurationListId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCPConfigurationListId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"CPConfigurationListId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCPConfigurationListId",
					new String[] {Long.class.getName()},
					new String[] {"CPConfigurationListId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCPConfigurationListId",
					new String[] {Long.class.getName()},
					new String[] {"CPConfigurationListId"}, false),
				_SQL_SELECT_CPCONFIGURATIONLISTREL_WHERE,
				_SQL_COUNT_CPCONFIGURATIONLISTREL_WHERE,
				CPConfigurationListRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"cpConfigurationListRel.", "CPConfigurationListId",
					FinderColumn.Type.LONG, "=", true, true,
					CPConfigurationListRel::getCPConfigurationListId));

		_collectionPersistenceFinderByC_C = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"classNameId", "CPConfigurationListId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"classNameId", "CPConfigurationListId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"classNameId", "CPConfigurationListId"}, false),
			_SQL_SELECT_CPCONFIGURATIONLISTREL_WHERE,
			_SQL_COUNT_CPCONFIGURATIONLISTREL_WHERE,
			CPConfigurationListRelModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "", null,
			new FinderColumn<>(
				"cpConfigurationListRel.", "classNameId",
				FinderColumn.Type.LONG, "=", true, true,
				CPConfigurationListRel::getClassNameId),
			new FinderColumn<>(
				"cpConfigurationListRel.", "CPConfigurationListId",
				FinderColumn.Type.LONG, "=", true, true,
				CPConfigurationListRel::getCPConfigurationListId));

		_uniquePersistenceFinderByC_C_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_C_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {
					"classNameId", "classPK", "CPConfigurationListId"
				},
				0, 0, false, CPConfigurationListRel::getClassNameId,
				CPConfigurationListRel::getClassPK,
				CPConfigurationListRel::getCPConfigurationListId),
			_SQL_SELECT_CPCONFIGURATIONLISTREL_WHERE, "",
			new FinderColumn<>(
				"cpConfigurationListRel.", "classNameId",
				FinderColumn.Type.LONG, "=", true, true,
				CPConfigurationListRel::getClassNameId),
			new FinderColumn<>(
				"cpConfigurationListRel.", "classPK", FinderColumn.Type.LONG,
				"=", true, true, CPConfigurationListRel::getClassPK),
			new FinderColumn<>(
				"cpConfigurationListRel.", "CPConfigurationListId",
				FinderColumn.Type.LONG, "=", true, true,
				CPConfigurationListRel::getCPConfigurationListId));

		CPConfigurationListRelUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CPConfigurationListRelUtil.setPersistence(null);

		entityCache.removeCache(CPConfigurationListRelImpl.class.getName());
	}

	@Override
	@Reference(
		target = CommercePersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = CommercePersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = CommercePersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		CPConfigurationListRelModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_CPCONFIGURATIONLISTREL =
		"SELECT cpConfigurationListRel FROM CPConfigurationListRel cpConfigurationListRel";

	private static final String _SQL_SELECT_CPCONFIGURATIONLISTREL_WHERE =
		"SELECT cpConfigurationListRel FROM CPConfigurationListRel cpConfigurationListRel WHERE ";

	private static final String _SQL_COUNT_CPCONFIGURATIONLISTREL_WHERE =
		"SELECT COUNT(cpConfigurationListRel) FROM CPConfigurationListRel cpConfigurationListRel WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-2078257486