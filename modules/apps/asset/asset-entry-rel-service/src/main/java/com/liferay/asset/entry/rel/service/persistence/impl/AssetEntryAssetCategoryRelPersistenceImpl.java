/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.entry.rel.service.persistence.impl;

import com.liferay.asset.entry.rel.exception.NoSuchEntryAssetCategoryRelException;
import com.liferay.asset.entry.rel.model.AssetEntryAssetCategoryRel;
import com.liferay.asset.entry.rel.model.AssetEntryAssetCategoryRelTable;
import com.liferay.asset.entry.rel.model.impl.AssetEntryAssetCategoryRelImpl;
import com.liferay.asset.entry.rel.model.impl.AssetEntryAssetCategoryRelModelImpl;
import com.liferay.asset.entry.rel.service.persistence.AssetEntryAssetCategoryRelPersistence;
import com.liferay.asset.entry.rel.service.persistence.AssetEntryAssetCategoryRelUtil;
import com.liferay.asset.entry.rel.service.persistence.impl.constants.AssetPersistenceConstants;
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
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
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
 * The persistence implementation for the asset entry asset category rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = AssetEntryAssetCategoryRelPersistence.class)
public class AssetEntryAssetCategoryRelPersistenceImpl
	extends BasePersistenceImpl
		<AssetEntryAssetCategoryRel, NoSuchEntryAssetCategoryRelException>
	implements AssetEntryAssetCategoryRelPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>AssetEntryAssetCategoryRelUtil</code> to access the asset entry asset category rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		AssetEntryAssetCategoryRelImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<AssetEntryAssetCategoryRel, NoSuchEntryAssetCategoryRelException>
			_collectionPersistenceFinderByAssetEntryId;

	/**
	 * Returns an ordered range of all the asset entry asset category rels where assetEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetEntryAssetCategoryRelModelImpl</code>.
	 * </p>
	 *
	 * @param assetEntryId the asset entry ID
	 * @param start the lower bound of the range of asset entry asset category rels
	 * @param end the upper bound of the range of asset entry asset category rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset entry asset category rels
	 */
	@Override
	public List<AssetEntryAssetCategoryRel> findByAssetEntryId(
		long assetEntryId, int start, int end,
		OrderByComparator<AssetEntryAssetCategoryRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByAssetEntryId.find(
			finderCache, new Object[] {assetEntryId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset entry asset category rel in the ordered set where assetEntryId = &#63;.
	 *
	 * @param assetEntryId the asset entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset entry asset category rel
	 * @throws NoSuchEntryAssetCategoryRelException if a matching asset entry asset category rel could not be found
	 */
	@Override
	public AssetEntryAssetCategoryRel findByAssetEntryId_First(
			long assetEntryId,
			OrderByComparator<AssetEntryAssetCategoryRel> orderByComparator)
		throws NoSuchEntryAssetCategoryRelException {

		return _collectionPersistenceFinderByAssetEntryId.findFirst(
			finderCache, new Object[] {assetEntryId}, orderByComparator);
	}

	/**
	 * Returns the first asset entry asset category rel in the ordered set where assetEntryId = &#63;.
	 *
	 * @param assetEntryId the asset entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset entry asset category rel, or <code>null</code> if a matching asset entry asset category rel could not be found
	 */
	@Override
	public AssetEntryAssetCategoryRel fetchByAssetEntryId_First(
		long assetEntryId,
		OrderByComparator<AssetEntryAssetCategoryRel> orderByComparator) {

		return _collectionPersistenceFinderByAssetEntryId.fetchFirst(
			finderCache, new Object[] {assetEntryId}, orderByComparator);
	}

	/**
	 * Removes all the asset entry asset category rels where assetEntryId = &#63; from the database.
	 *
	 * @param assetEntryId the asset entry ID
	 */
	@Override
	public void removeByAssetEntryId(long assetEntryId) {
		_collectionPersistenceFinderByAssetEntryId.remove(
			finderCache, new Object[] {assetEntryId});
	}

	/**
	 * Returns the number of asset entry asset category rels where assetEntryId = &#63;.
	 *
	 * @param assetEntryId the asset entry ID
	 * @return the number of matching asset entry asset category rels
	 */
	@Override
	public int countByAssetEntryId(long assetEntryId) {
		return _collectionPersistenceFinderByAssetEntryId.count(
			finderCache, new Object[] {assetEntryId});
	}

	private CollectionPersistenceFinder
		<AssetEntryAssetCategoryRel, NoSuchEntryAssetCategoryRelException>
			_collectionPersistenceFinderByAssetCategoryId;

	/**
	 * Returns an ordered range of all the asset entry asset category rels where assetCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetEntryAssetCategoryRelModelImpl</code>.
	 * </p>
	 *
	 * @param assetCategoryId the asset category ID
	 * @param start the lower bound of the range of asset entry asset category rels
	 * @param end the upper bound of the range of asset entry asset category rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset entry asset category rels
	 */
	@Override
	public List<AssetEntryAssetCategoryRel> findByAssetCategoryId(
		long assetCategoryId, int start, int end,
		OrderByComparator<AssetEntryAssetCategoryRel> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByAssetCategoryId.find(
			finderCache, new Object[] {assetCategoryId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset entry asset category rel in the ordered set where assetCategoryId = &#63;.
	 *
	 * @param assetCategoryId the asset category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset entry asset category rel
	 * @throws NoSuchEntryAssetCategoryRelException if a matching asset entry asset category rel could not be found
	 */
	@Override
	public AssetEntryAssetCategoryRel findByAssetCategoryId_First(
			long assetCategoryId,
			OrderByComparator<AssetEntryAssetCategoryRel> orderByComparator)
		throws NoSuchEntryAssetCategoryRelException {

		return _collectionPersistenceFinderByAssetCategoryId.findFirst(
			finderCache, new Object[] {assetCategoryId}, orderByComparator);
	}

	/**
	 * Returns the first asset entry asset category rel in the ordered set where assetCategoryId = &#63;.
	 *
	 * @param assetCategoryId the asset category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset entry asset category rel, or <code>null</code> if a matching asset entry asset category rel could not be found
	 */
	@Override
	public AssetEntryAssetCategoryRel fetchByAssetCategoryId_First(
		long assetCategoryId,
		OrderByComparator<AssetEntryAssetCategoryRel> orderByComparator) {

		return _collectionPersistenceFinderByAssetCategoryId.fetchFirst(
			finderCache, new Object[] {assetCategoryId}, orderByComparator);
	}

	/**
	 * Removes all the asset entry asset category rels where assetCategoryId = &#63; from the database.
	 *
	 * @param assetCategoryId the asset category ID
	 */
	@Override
	public void removeByAssetCategoryId(long assetCategoryId) {
		_collectionPersistenceFinderByAssetCategoryId.remove(
			finderCache, new Object[] {assetCategoryId});
	}

	/**
	 * Returns the number of asset entry asset category rels where assetCategoryId = &#63;.
	 *
	 * @param assetCategoryId the asset category ID
	 * @return the number of matching asset entry asset category rels
	 */
	@Override
	public int countByAssetCategoryId(long assetCategoryId) {
		return _collectionPersistenceFinderByAssetCategoryId.count(
			finderCache, new Object[] {assetCategoryId});
	}

	private UniquePersistenceFinder
		<AssetEntryAssetCategoryRel, NoSuchEntryAssetCategoryRelException>
			_uniquePersistenceFinderByA_A;

	/**
	 * Returns the asset entry asset category rel where assetEntryId = &#63; and assetCategoryId = &#63; or throws a <code>NoSuchEntryAssetCategoryRelException</code> if it could not be found.
	 *
	 * @param assetEntryId the asset entry ID
	 * @param assetCategoryId the asset category ID
	 * @return the matching asset entry asset category rel
	 * @throws NoSuchEntryAssetCategoryRelException if a matching asset entry asset category rel could not be found
	 */
	@Override
	public AssetEntryAssetCategoryRel findByA_A(
			long assetEntryId, long assetCategoryId)
		throws NoSuchEntryAssetCategoryRelException {

		return _uniquePersistenceFinderByA_A.find(
			finderCache, new Object[] {assetEntryId, assetCategoryId});
	}

	/**
	 * Returns the asset entry asset category rel where assetEntryId = &#63; and assetCategoryId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param assetEntryId the asset entry ID
	 * @param assetCategoryId the asset category ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset entry asset category rel, or <code>null</code> if a matching asset entry asset category rel could not be found
	 */
	@Override
	public AssetEntryAssetCategoryRel fetchByA_A(
		long assetEntryId, long assetCategoryId, boolean useFinderCache) {

		return _uniquePersistenceFinderByA_A.fetch(
			finderCache, new Object[] {assetEntryId, assetCategoryId},
			useFinderCache);
	}

	/**
	 * Removes the asset entry asset category rel where assetEntryId = &#63; and assetCategoryId = &#63; from the database.
	 *
	 * @param assetEntryId the asset entry ID
	 * @param assetCategoryId the asset category ID
	 * @return the asset entry asset category rel that was removed
	 */
	@Override
	public AssetEntryAssetCategoryRel removeByA_A(
			long assetEntryId, long assetCategoryId)
		throws NoSuchEntryAssetCategoryRelException {

		AssetEntryAssetCategoryRel assetEntryAssetCategoryRel = findByA_A(
			assetEntryId, assetCategoryId);

		return remove(assetEntryAssetCategoryRel);
	}

	/**
	 * Returns the number of asset entry asset category rels where assetEntryId = &#63; and assetCategoryId = &#63;.
	 *
	 * @param assetEntryId the asset entry ID
	 * @param assetCategoryId the asset category ID
	 * @return the number of matching asset entry asset category rels
	 */
	@Override
	public int countByA_A(long assetEntryId, long assetCategoryId) {
		return _uniquePersistenceFinderByA_A.count(
			finderCache, new Object[] {assetEntryId, assetCategoryId});
	}

	public AssetEntryAssetCategoryRelPersistenceImpl() {
		setModelClass(AssetEntryAssetCategoryRel.class);

		setModelImplClass(AssetEntryAssetCategoryRelImpl.class);
		setModelPKClass(long.class);

		setTable(AssetEntryAssetCategoryRelTable.INSTANCE);
	}

	/**
	 * Creates a new asset entry asset category rel with the primary key. Does not add the asset entry asset category rel to the database.
	 *
	 * @param assetEntryAssetCategoryRelId the primary key for the new asset entry asset category rel
	 * @return the new asset entry asset category rel
	 */
	@Override
	public AssetEntryAssetCategoryRel create(
		long assetEntryAssetCategoryRelId) {

		AssetEntryAssetCategoryRel assetEntryAssetCategoryRel =
			new AssetEntryAssetCategoryRelImpl();

		assetEntryAssetCategoryRel.setNew(true);
		assetEntryAssetCategoryRel.setPrimaryKey(assetEntryAssetCategoryRelId);

		assetEntryAssetCategoryRel.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return assetEntryAssetCategoryRel;
	}

	/**
	 * Removes the asset entry asset category rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param assetEntryAssetCategoryRelId the primary key of the asset entry asset category rel
	 * @return the asset entry asset category rel that was removed
	 * @throws NoSuchEntryAssetCategoryRelException if a asset entry asset category rel with the primary key could not be found
	 */
	@Override
	public AssetEntryAssetCategoryRel remove(long assetEntryAssetCategoryRelId)
		throws NoSuchEntryAssetCategoryRelException {

		return remove((Serializable)assetEntryAssetCategoryRelId);
	}

	@Override
	protected AssetEntryAssetCategoryRel removeImpl(
		AssetEntryAssetCategoryRel assetEntryAssetCategoryRel) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(assetEntryAssetCategoryRel)) {
				assetEntryAssetCategoryRel =
					(AssetEntryAssetCategoryRel)session.get(
						AssetEntryAssetCategoryRelImpl.class,
						assetEntryAssetCategoryRel.getPrimaryKeyObj());
			}

			if ((assetEntryAssetCategoryRel != null) &&
				ctPersistenceHelper.isRemove(assetEntryAssetCategoryRel)) {

				session.delete(assetEntryAssetCategoryRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (assetEntryAssetCategoryRel != null) {
			clearCache(assetEntryAssetCategoryRel);
		}

		return assetEntryAssetCategoryRel;
	}

	@Override
	public AssetEntryAssetCategoryRel updateImpl(
		AssetEntryAssetCategoryRel assetEntryAssetCategoryRel) {

		boolean isNew = assetEntryAssetCategoryRel.isNew();

		if (!(assetEntryAssetCategoryRel instanceof
				AssetEntryAssetCategoryRelModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(assetEntryAssetCategoryRel.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					assetEntryAssetCategoryRel);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in assetEntryAssetCategoryRel proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom AssetEntryAssetCategoryRel implementation " +
					assetEntryAssetCategoryRel.getClass());
		}

		AssetEntryAssetCategoryRelModelImpl
			assetEntryAssetCategoryRelModelImpl =
				(AssetEntryAssetCategoryRelModelImpl)assetEntryAssetCategoryRel;

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(assetEntryAssetCategoryRel)) {
				if (!isNew) {
					session.evict(
						AssetEntryAssetCategoryRelImpl.class,
						assetEntryAssetCategoryRel.getPrimaryKeyObj());
				}

				session.save(assetEntryAssetCategoryRel);
			}
			else {
				assetEntryAssetCategoryRel =
					(AssetEntryAssetCategoryRel)session.merge(
						assetEntryAssetCategoryRel);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(assetEntryAssetCategoryRel, false);

		if (isNew) {
			assetEntryAssetCategoryRel.setNew(false);
		}

		assetEntryAssetCategoryRel.resetOriginalValues();

		return assetEntryAssetCategoryRel;
	}

	/**
	 * Returns the asset entry asset category rel with the primary key or throws a <code>NoSuchEntryAssetCategoryRelException</code> if it could not be found.
	 *
	 * @param assetEntryAssetCategoryRelId the primary key of the asset entry asset category rel
	 * @return the asset entry asset category rel
	 * @throws NoSuchEntryAssetCategoryRelException if a asset entry asset category rel with the primary key could not be found
	 */
	@Override
	public AssetEntryAssetCategoryRel findByPrimaryKey(
			long assetEntryAssetCategoryRelId)
		throws NoSuchEntryAssetCategoryRelException {

		return findByPrimaryKey((Serializable)assetEntryAssetCategoryRelId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the asset entry asset category rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param assetEntryAssetCategoryRelId the primary key of the asset entry asset category rel
	 * @return the asset entry asset category rel, or <code>null</code> if a asset entry asset category rel with the primary key could not be found
	 */
	@Override
	public AssetEntryAssetCategoryRel fetchByPrimaryKey(
		long assetEntryAssetCategoryRelId) {

		return fetchByPrimaryKey((Serializable)assetEntryAssetCategoryRelId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "assetEntryAssetCategoryRelId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_ASSETENTRYASSETCATEGORYREL;
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
		return AssetEntryAssetCategoryRelModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "AssetEntryAssetCategoryRel";
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
		ctMergeColumnNames.add("assetEntryId");
		ctMergeColumnNames.add("assetCategoryId");
		ctMergeColumnNames.add("priority");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("assetEntryAssetCategoryRelId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(
			new String[] {"assetEntryId", "assetCategoryId"});
	}

	/**
	 * Initializes the asset entry asset category rel persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByAssetEntryId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByAssetEntryId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"assetEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByAssetEntryId", new String[] {Long.class.getName()},
					new String[] {"assetEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByAssetEntryId", new String[] {Long.class.getName()},
					new String[] {"assetEntryId"}, false),
				_SQL_SELECT_ASSETENTRYASSETCATEGORYREL_WHERE,
				_SQL_COUNT_ASSETENTRYASSETCATEGORYREL_WHERE,
				AssetEntryAssetCategoryRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"assetEntryAssetCategoryRel.", "assetEntryId",
					FinderColumn.Type.LONG, "=", true, true,
					AssetEntryAssetCategoryRel::getAssetEntryId));

		_collectionPersistenceFinderByAssetCategoryId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByAssetCategoryId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"assetCategoryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByAssetCategoryId",
					new String[] {Long.class.getName()},
					new String[] {"assetCategoryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByAssetCategoryId",
					new String[] {Long.class.getName()},
					new String[] {"assetCategoryId"}, false),
				_SQL_SELECT_ASSETENTRYASSETCATEGORYREL_WHERE,
				_SQL_COUNT_ASSETENTRYASSETCATEGORYREL_WHERE,
				AssetEntryAssetCategoryRelModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"assetEntryAssetCategoryRel.", "assetCategoryId",
					FinderColumn.Type.LONG, "=", true, true,
					AssetEntryAssetCategoryRel::getAssetCategoryId));

		_uniquePersistenceFinderByA_A = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByA_A",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"assetEntryId", "assetCategoryId"}, 0, 0, false,
				AssetEntryAssetCategoryRel::getAssetEntryId,
				AssetEntryAssetCategoryRel::getAssetCategoryId),
			_SQL_SELECT_ASSETENTRYASSETCATEGORYREL_WHERE, "",
			new FinderColumn<>(
				"assetEntryAssetCategoryRel.", "assetEntryId",
				FinderColumn.Type.LONG, "=", true, true,
				AssetEntryAssetCategoryRel::getAssetEntryId),
			new FinderColumn<>(
				"assetEntryAssetCategoryRel.", "assetCategoryId",
				FinderColumn.Type.LONG, "=", true, true,
				AssetEntryAssetCategoryRel::getAssetCategoryId));

		AssetEntryAssetCategoryRelUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		AssetEntryAssetCategoryRelUtil.setPersistence(null);

		entityCache.removeCache(AssetEntryAssetCategoryRelImpl.class.getName());
	}

	@Override
	@Reference(
		target = AssetPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = AssetPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = AssetPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		AssetEntryAssetCategoryRelModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_ASSETENTRYASSETCATEGORYREL =
		"SELECT assetEntryAssetCategoryRel FROM AssetEntryAssetCategoryRel assetEntryAssetCategoryRel";

	private static final String _SQL_SELECT_ASSETENTRYASSETCATEGORYREL_WHERE =
		"SELECT assetEntryAssetCategoryRel FROM AssetEntryAssetCategoryRel assetEntryAssetCategoryRel WHERE ";

	private static final String _SQL_COUNT_ASSETENTRYASSETCATEGORYREL_WHERE =
		"SELECT COUNT(assetEntryAssetCategoryRel) FROM AssetEntryAssetCategoryRel assetEntryAssetCategoryRel WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1259875840