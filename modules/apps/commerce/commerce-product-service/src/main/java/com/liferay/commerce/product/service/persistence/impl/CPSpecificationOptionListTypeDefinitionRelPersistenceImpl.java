/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.persistence.impl;

import com.liferay.commerce.product.exception.NoSuchCPSpecificationOptionListTypeDefinitionRelException;
import com.liferay.commerce.product.model.CPSpecificationOptionListTypeDefinitionRel;
import com.liferay.commerce.product.model.CPSpecificationOptionListTypeDefinitionRelTable;
import com.liferay.commerce.product.model.impl.CPSpecificationOptionListTypeDefinitionRelImpl;
import com.liferay.commerce.product.model.impl.CPSpecificationOptionListTypeDefinitionRelModelImpl;
import com.liferay.commerce.product.service.persistence.CPSpecificationOptionListTypeDefinitionRelPersistence;
import com.liferay.commerce.product.service.persistence.CPSpecificationOptionListTypeDefinitionRelUtil;
import com.liferay.commerce.product.service.persistence.impl.constants.CommercePersistenceConstants;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
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
 * The persistence implementation for the cp specification option list type definition rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @generated
 */
@Component(
	service = CPSpecificationOptionListTypeDefinitionRelPersistence.class
)
public class CPSpecificationOptionListTypeDefinitionRelPersistenceImpl
	extends BasePersistenceImpl
		<CPSpecificationOptionListTypeDefinitionRel,
		 NoSuchCPSpecificationOptionListTypeDefinitionRelException>
	implements CPSpecificationOptionListTypeDefinitionRelPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CPSpecificationOptionListTypeDefinitionRelUtil</code> to access the cp specification option list type definition rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CPSpecificationOptionListTypeDefinitionRelImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CPSpecificationOptionListTypeDefinitionRel,
		 NoSuchCPSpecificationOptionListTypeDefinitionRelException>
			_collectionPersistenceFinderByCPSpecificationOptionId;

	/**
	 * Returns an ordered range of all the cp specification option list type definition rels where CPSpecificationOptionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPSpecificationOptionListTypeDefinitionRelModelImpl</code>.
	 * </p>
	 *
	 * @param CPSpecificationOptionId the cp specification option ID
	 * @param start the lower bound of the range of cp specification option list type definition rels
	 * @param end the upper bound of the range of cp specification option list type definition rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp specification option list type definition rels
	 */
	@Override
	public List<CPSpecificationOptionListTypeDefinitionRel>
		findByCPSpecificationOptionId(
			long CPSpecificationOptionId, int start, int end,
			OrderByComparator<CPSpecificationOptionListTypeDefinitionRel>
				orderByComparator,
			boolean useFinderCache) {

		return _collectionPersistenceFinderByCPSpecificationOptionId.find(
			finderCache, new Object[] {CPSpecificationOptionId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp specification option list type definition rel in the ordered set where CPSpecificationOptionId = &#63;.
	 *
	 * @param CPSpecificationOptionId the cp specification option ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp specification option list type definition rel
	 * @throws NoSuchCPSpecificationOptionListTypeDefinitionRelException if a matching cp specification option list type definition rel could not be found
	 */
	@Override
	public CPSpecificationOptionListTypeDefinitionRel
			findByCPSpecificationOptionId_First(
				long CPSpecificationOptionId,
				OrderByComparator<CPSpecificationOptionListTypeDefinitionRel>
					orderByComparator)
		throws NoSuchCPSpecificationOptionListTypeDefinitionRelException {

		return _collectionPersistenceFinderByCPSpecificationOptionId.findFirst(
			finderCache, new Object[] {CPSpecificationOptionId},
			orderByComparator);
	}

	/**
	 * Returns the first cp specification option list type definition rel in the ordered set where CPSpecificationOptionId = &#63;.
	 *
	 * @param CPSpecificationOptionId the cp specification option ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp specification option list type definition rel, or <code>null</code> if a matching cp specification option list type definition rel could not be found
	 */
	@Override
	public CPSpecificationOptionListTypeDefinitionRel
		fetchByCPSpecificationOptionId_First(
			long CPSpecificationOptionId,
			OrderByComparator<CPSpecificationOptionListTypeDefinitionRel>
				orderByComparator) {

		return _collectionPersistenceFinderByCPSpecificationOptionId.fetchFirst(
			finderCache, new Object[] {CPSpecificationOptionId},
			orderByComparator);
	}

	/**
	 * Removes all the cp specification option list type definition rels where CPSpecificationOptionId = &#63; from the database.
	 *
	 * @param CPSpecificationOptionId the cp specification option ID
	 */
	@Override
	public void removeByCPSpecificationOptionId(long CPSpecificationOptionId) {
		_collectionPersistenceFinderByCPSpecificationOptionId.remove(
			finderCache, new Object[] {CPSpecificationOptionId});
	}

	/**
	 * Returns the number of cp specification option list type definition rels where CPSpecificationOptionId = &#63;.
	 *
	 * @param CPSpecificationOptionId the cp specification option ID
	 * @return the number of matching cp specification option list type definition rels
	 */
	@Override
	public int countByCPSpecificationOptionId(long CPSpecificationOptionId) {
		return _collectionPersistenceFinderByCPSpecificationOptionId.count(
			finderCache, new Object[] {CPSpecificationOptionId});
	}

	private CollectionPersistenceFinder
		<CPSpecificationOptionListTypeDefinitionRel,
		 NoSuchCPSpecificationOptionListTypeDefinitionRelException>
			_collectionPersistenceFinderByListTypeDefinitionId;

	/**
	 * Returns an ordered range of all the cp specification option list type definition rels where listTypeDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPSpecificationOptionListTypeDefinitionRelModelImpl</code>.
	 * </p>
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param start the lower bound of the range of cp specification option list type definition rels
	 * @param end the upper bound of the range of cp specification option list type definition rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp specification option list type definition rels
	 */
	@Override
	public List<CPSpecificationOptionListTypeDefinitionRel>
		findByListTypeDefinitionId(
			long listTypeDefinitionId, int start, int end,
			OrderByComparator<CPSpecificationOptionListTypeDefinitionRel>
				orderByComparator,
			boolean useFinderCache) {

		return _collectionPersistenceFinderByListTypeDefinitionId.find(
			finderCache, new Object[] {listTypeDefinitionId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp specification option list type definition rel in the ordered set where listTypeDefinitionId = &#63;.
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp specification option list type definition rel
	 * @throws NoSuchCPSpecificationOptionListTypeDefinitionRelException if a matching cp specification option list type definition rel could not be found
	 */
	@Override
	public CPSpecificationOptionListTypeDefinitionRel
			findByListTypeDefinitionId_First(
				long listTypeDefinitionId,
				OrderByComparator<CPSpecificationOptionListTypeDefinitionRel>
					orderByComparator)
		throws NoSuchCPSpecificationOptionListTypeDefinitionRelException {

		return _collectionPersistenceFinderByListTypeDefinitionId.findFirst(
			finderCache, new Object[] {listTypeDefinitionId},
			orderByComparator);
	}

	/**
	 * Returns the first cp specification option list type definition rel in the ordered set where listTypeDefinitionId = &#63;.
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp specification option list type definition rel, or <code>null</code> if a matching cp specification option list type definition rel could not be found
	 */
	@Override
	public CPSpecificationOptionListTypeDefinitionRel
		fetchByListTypeDefinitionId_First(
			long listTypeDefinitionId,
			OrderByComparator<CPSpecificationOptionListTypeDefinitionRel>
				orderByComparator) {

		return _collectionPersistenceFinderByListTypeDefinitionId.fetchFirst(
			finderCache, new Object[] {listTypeDefinitionId},
			orderByComparator);
	}

	/**
	 * Removes all the cp specification option list type definition rels where listTypeDefinitionId = &#63; from the database.
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 */
	@Override
	public void removeByListTypeDefinitionId(long listTypeDefinitionId) {
		_collectionPersistenceFinderByListTypeDefinitionId.remove(
			finderCache, new Object[] {listTypeDefinitionId});
	}

	/**
	 * Returns the number of cp specification option list type definition rels where listTypeDefinitionId = &#63;.
	 *
	 * @param listTypeDefinitionId the list type definition ID
	 * @return the number of matching cp specification option list type definition rels
	 */
	@Override
	public int countByListTypeDefinitionId(long listTypeDefinitionId) {
		return _collectionPersistenceFinderByListTypeDefinitionId.count(
			finderCache, new Object[] {listTypeDefinitionId});
	}

	private UniquePersistenceFinder
		<CPSpecificationOptionListTypeDefinitionRel,
		 NoSuchCPSpecificationOptionListTypeDefinitionRelException>
			_uniquePersistenceFinderByC_L;

	/**
	 * Returns the cp specification option list type definition rel where CPSpecificationOptionId = &#63; and listTypeDefinitionId = &#63; or throws a <code>NoSuchCPSpecificationOptionListTypeDefinitionRelException</code> if it could not be found.
	 *
	 * @param CPSpecificationOptionId the cp specification option ID
	 * @param listTypeDefinitionId the list type definition ID
	 * @return the matching cp specification option list type definition rel
	 * @throws NoSuchCPSpecificationOptionListTypeDefinitionRelException if a matching cp specification option list type definition rel could not be found
	 */
	@Override
	public CPSpecificationOptionListTypeDefinitionRel findByC_L(
			long CPSpecificationOptionId, long listTypeDefinitionId)
		throws NoSuchCPSpecificationOptionListTypeDefinitionRelException {

		return _uniquePersistenceFinderByC_L.find(
			finderCache,
			new Object[] {CPSpecificationOptionId, listTypeDefinitionId});
	}

	/**
	 * Returns the cp specification option list type definition rel where CPSpecificationOptionId = &#63; and listTypeDefinitionId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param CPSpecificationOptionId the cp specification option ID
	 * @param listTypeDefinitionId the list type definition ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp specification option list type definition rel, or <code>null</code> if a matching cp specification option list type definition rel could not be found
	 */
	@Override
	public CPSpecificationOptionListTypeDefinitionRel fetchByC_L(
		long CPSpecificationOptionId, long listTypeDefinitionId,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByC_L.fetch(
			finderCache,
			new Object[] {CPSpecificationOptionId, listTypeDefinitionId},
			useFinderCache);
	}

	/**
	 * Removes the cp specification option list type definition rel where CPSpecificationOptionId = &#63; and listTypeDefinitionId = &#63; from the database.
	 *
	 * @param CPSpecificationOptionId the cp specification option ID
	 * @param listTypeDefinitionId the list type definition ID
	 * @return the cp specification option list type definition rel that was removed
	 */
	@Override
	public CPSpecificationOptionListTypeDefinitionRel removeByC_L(
			long CPSpecificationOptionId, long listTypeDefinitionId)
		throws NoSuchCPSpecificationOptionListTypeDefinitionRelException {

		CPSpecificationOptionListTypeDefinitionRel
			cpSpecificationOptionListTypeDefinitionRel = findByC_L(
				CPSpecificationOptionId, listTypeDefinitionId);

		return remove(cpSpecificationOptionListTypeDefinitionRel);
	}

	/**
	 * Returns the number of cp specification option list type definition rels where CPSpecificationOptionId = &#63; and listTypeDefinitionId = &#63;.
	 *
	 * @param CPSpecificationOptionId the cp specification option ID
	 * @param listTypeDefinitionId the list type definition ID
	 * @return the number of matching cp specification option list type definition rels
	 */
	@Override
	public int countByC_L(
		long CPSpecificationOptionId, long listTypeDefinitionId) {

		return _uniquePersistenceFinderByC_L.count(
			finderCache,
			new Object[] {CPSpecificationOptionId, listTypeDefinitionId});
	}

	public CPSpecificationOptionListTypeDefinitionRelPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put(
			"CPSpecificationOptionListTypeDefinitionRelId",
			"CPSOListTypeDefinitionRelId");

		setDBColumnNames(dbColumnNames);

		setModelClass(CPSpecificationOptionListTypeDefinitionRel.class);

		setModelImplClass(CPSpecificationOptionListTypeDefinitionRelImpl.class);
		setModelPKClass(long.class);

		setTable(CPSpecificationOptionListTypeDefinitionRelTable.INSTANCE);
	}

	/**
	 * Creates a new cp specification option list type definition rel with the primary key. Does not add the cp specification option list type definition rel to the database.
	 *
	 * @param CPSpecificationOptionListTypeDefinitionRelId the primary key for the new cp specification option list type definition rel
	 * @return the new cp specification option list type definition rel
	 */
	@Override
	public CPSpecificationOptionListTypeDefinitionRel create(
		long CPSpecificationOptionListTypeDefinitionRelId) {

		CPSpecificationOptionListTypeDefinitionRel
			cpSpecificationOptionListTypeDefinitionRel =
				new CPSpecificationOptionListTypeDefinitionRelImpl();

		cpSpecificationOptionListTypeDefinitionRel.setNew(true);
		cpSpecificationOptionListTypeDefinitionRel.setPrimaryKey(
			CPSpecificationOptionListTypeDefinitionRelId);

		cpSpecificationOptionListTypeDefinitionRel.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return cpSpecificationOptionListTypeDefinitionRel;
	}

	/**
	 * Removes the cp specification option list type definition rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param CPSpecificationOptionListTypeDefinitionRelId the primary key of the cp specification option list type definition rel
	 * @return the cp specification option list type definition rel that was removed
	 * @throws NoSuchCPSpecificationOptionListTypeDefinitionRelException if a cp specification option list type definition rel with the primary key could not be found
	 */
	@Override
	public CPSpecificationOptionListTypeDefinitionRel remove(
			long CPSpecificationOptionListTypeDefinitionRelId)
		throws NoSuchCPSpecificationOptionListTypeDefinitionRelException {

		return remove(
			(Serializable)CPSpecificationOptionListTypeDefinitionRelId);
	}

	@Override
	protected CPSpecificationOptionListTypeDefinitionRel removeImpl(
		CPSpecificationOptionListTypeDefinitionRel
			cpSpecificationOptionListTypeDefinitionRel) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(cpSpecificationOptionListTypeDefinitionRel)) {
				cpSpecificationOptionListTypeDefinitionRel =
					(CPSpecificationOptionListTypeDefinitionRel)session.get(
						CPSpecificationOptionListTypeDefinitionRelImpl.class,
						cpSpecificationOptionListTypeDefinitionRel.
							getPrimaryKeyObj());
			}

			if ((cpSpecificationOptionListTypeDefinitionRel != null) &&
				ctPersistenceHelper.isRemove(
					cpSpecificationOptionListTypeDefinitionRel)) {

				session.delete(cpSpecificationOptionListTypeDefinitionRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (cpSpecificationOptionListTypeDefinitionRel != null) {
			clearCache(cpSpecificationOptionListTypeDefinitionRel);
		}

		return cpSpecificationOptionListTypeDefinitionRel;
	}

	@Override
	public CPSpecificationOptionListTypeDefinitionRel updateImpl(
		CPSpecificationOptionListTypeDefinitionRel
			cpSpecificationOptionListTypeDefinitionRel) {

		boolean isNew = cpSpecificationOptionListTypeDefinitionRel.isNew();

		if (!(cpSpecificationOptionListTypeDefinitionRel instanceof
				CPSpecificationOptionListTypeDefinitionRelModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(
					cpSpecificationOptionListTypeDefinitionRel.getClass())) {

				invocationHandler = ProxyUtil.getInvocationHandler(
					cpSpecificationOptionListTypeDefinitionRel);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in cpSpecificationOptionListTypeDefinitionRel proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CPSpecificationOptionListTypeDefinitionRel implementation " +
					cpSpecificationOptionListTypeDefinitionRel.getClass());
		}

		CPSpecificationOptionListTypeDefinitionRelModelImpl
			cpSpecificationOptionListTypeDefinitionRelModelImpl =
				(CPSpecificationOptionListTypeDefinitionRelModelImpl)
					cpSpecificationOptionListTypeDefinitionRel;

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(
					cpSpecificationOptionListTypeDefinitionRel)) {

				if (!isNew) {
					session.evict(
						CPSpecificationOptionListTypeDefinitionRelImpl.class,
						cpSpecificationOptionListTypeDefinitionRel.
							getPrimaryKeyObj());
				}

				session.save(cpSpecificationOptionListTypeDefinitionRel);
			}
			else {
				cpSpecificationOptionListTypeDefinitionRel =
					(CPSpecificationOptionListTypeDefinitionRel)session.merge(
						cpSpecificationOptionListTypeDefinitionRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(
			cpSpecificationOptionListTypeDefinitionRel, false);

		if (isNew) {
			cpSpecificationOptionListTypeDefinitionRel.setNew(false);
		}

		cpSpecificationOptionListTypeDefinitionRel.resetOriginalValues();

		return cpSpecificationOptionListTypeDefinitionRel;
	}

	/**
	 * Returns the cp specification option list type definition rel with the primary key or throws a <code>NoSuchCPSpecificationOptionListTypeDefinitionRelException</code> if it could not be found.
	 *
	 * @param CPSpecificationOptionListTypeDefinitionRelId the primary key of the cp specification option list type definition rel
	 * @return the cp specification option list type definition rel
	 * @throws NoSuchCPSpecificationOptionListTypeDefinitionRelException if a cp specification option list type definition rel with the primary key could not be found
	 */
	@Override
	public CPSpecificationOptionListTypeDefinitionRel findByPrimaryKey(
			long CPSpecificationOptionListTypeDefinitionRelId)
		throws NoSuchCPSpecificationOptionListTypeDefinitionRelException {

		return findByPrimaryKey(
			(Serializable)CPSpecificationOptionListTypeDefinitionRelId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the cp specification option list type definition rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param CPSpecificationOptionListTypeDefinitionRelId the primary key of the cp specification option list type definition rel
	 * @return the cp specification option list type definition rel, or <code>null</code> if a cp specification option list type definition rel with the primary key could not be found
	 */
	@Override
	public CPSpecificationOptionListTypeDefinitionRel fetchByPrimaryKey(
		long CPSpecificationOptionListTypeDefinitionRelId) {

		return fetchByPrimaryKey(
			(Serializable)CPSpecificationOptionListTypeDefinitionRelId);
	}

	@Override
	public Set<String> getBadColumnNames() {
		return _badColumnNames;
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "CPSOListTypeDefinitionRelId";
	}

	@Override
	protected String getPKFieldName() {
		return "CPSpecificationOptionListTypeDefinitionRelId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_CPSPECIFICATIONOPTIONLISTTYPEDEFINITIONREL;
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
		return CPSpecificationOptionListTypeDefinitionRelModelImpl.
			TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "CPSOListTypeDefinitionRel";
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
		ctMergeColumnNames.add("CPSpecificationOptionId");
		ctMergeColumnNames.add("listTypeDefinitionId");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("CPSOListTypeDefinitionRelId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(
			new String[] {"CPSpecificationOptionId", "listTypeDefinitionId"});
	}

	/**
	 * Initializes the cp specification option list type definition rel persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByCPSpecificationOptionId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByCPSpecificationOptionId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"CPSpecificationOptionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCPSpecificationOptionId",
					new String[] {Long.class.getName()},
					new String[] {"CPSpecificationOptionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCPSpecificationOptionId",
					new String[] {Long.class.getName()},
					new String[] {"CPSpecificationOptionId"}, false),
				_SQL_SELECT_CPSPECIFICATIONOPTIONLISTTYPEDEFINITIONREL_WHERE,
				_SQL_COUNT_CPSPECIFICATIONOPTIONLISTTYPEDEFINITIONREL_WHERE,
				CPSpecificationOptionListTypeDefinitionRelModelImpl.
					ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"cpSpecificationOptionListTypeDefinitionRel.",
					"CPSpecificationOptionId", FinderColumn.Type.LONG, "=",
					true, true,
					CPSpecificationOptionListTypeDefinitionRel::
						getCPSpecificationOptionId));

		_collectionPersistenceFinderByListTypeDefinitionId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByListTypeDefinitionId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"listTypeDefinitionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByListTypeDefinitionId",
					new String[] {Long.class.getName()},
					new String[] {"listTypeDefinitionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByListTypeDefinitionId",
					new String[] {Long.class.getName()},
					new String[] {"listTypeDefinitionId"}, false),
				_SQL_SELECT_CPSPECIFICATIONOPTIONLISTTYPEDEFINITIONREL_WHERE,
				_SQL_COUNT_CPSPECIFICATIONOPTIONLISTTYPEDEFINITIONREL_WHERE,
				CPSpecificationOptionListTypeDefinitionRelModelImpl.
					ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"cpSpecificationOptionListTypeDefinitionRel.",
					"listTypeDefinitionId", FinderColumn.Type.LONG, "=", true,
					true,
					CPSpecificationOptionListTypeDefinitionRel::
						getListTypeDefinitionId));

		_uniquePersistenceFinderByC_L = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_L",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {
					"CPSpecificationOptionId", "listTypeDefinitionId"
				},
				0, 0, false,
				CPSpecificationOptionListTypeDefinitionRel::
					getCPSpecificationOptionId,
				CPSpecificationOptionListTypeDefinitionRel::
					getListTypeDefinitionId),
			_SQL_SELECT_CPSPECIFICATIONOPTIONLISTTYPEDEFINITIONREL_WHERE, "",
			new FinderColumn<>(
				"cpSpecificationOptionListTypeDefinitionRel.",
				"CPSpecificationOptionId", FinderColumn.Type.LONG, "=", true,
				true,
				CPSpecificationOptionListTypeDefinitionRel::
					getCPSpecificationOptionId),
			new FinderColumn<>(
				"cpSpecificationOptionListTypeDefinitionRel.",
				"listTypeDefinitionId", FinderColumn.Type.LONG, "=", true, true,
				CPSpecificationOptionListTypeDefinitionRel::
					getListTypeDefinitionId));

		CPSpecificationOptionListTypeDefinitionRelUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CPSpecificationOptionListTypeDefinitionRelUtil.setPersistence(null);

		entityCache.removeCache(
			CPSpecificationOptionListTypeDefinitionRelImpl.class.getName());
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
		CPSpecificationOptionListTypeDefinitionRelModelImpl.ENTITY_ALIAS + ".";

	private static final String
		_SQL_SELECT_CPSPECIFICATIONOPTIONLISTTYPEDEFINITIONREL =
			"SELECT cpSpecificationOptionListTypeDefinitionRel FROM CPSpecificationOptionListTypeDefinitionRel cpSpecificationOptionListTypeDefinitionRel";

	private static final String
		_SQL_SELECT_CPSPECIFICATIONOPTIONLISTTYPEDEFINITIONREL_WHERE =
			"SELECT cpSpecificationOptionListTypeDefinitionRel FROM CPSpecificationOptionListTypeDefinitionRel cpSpecificationOptionListTypeDefinitionRel WHERE ";

	private static final String
		_SQL_COUNT_CPSPECIFICATIONOPTIONLISTTYPEDEFINITIONREL_WHERE =
			"SELECT COUNT(cpSpecificationOptionListTypeDefinitionRel) FROM CPSpecificationOptionListTypeDefinitionRel cpSpecificationOptionListTypeDefinitionRel WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No CPSpecificationOptionListTypeDefinitionRel exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CPSpecificationOptionListTypeDefinitionRelPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"CPSpecificationOptionListTypeDefinitionRelId"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:404403908