/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.auto.tagger.service.persistence.impl;

import com.liferay.asset.auto.tagger.exception.NoSuchEntryException;
import com.liferay.asset.auto.tagger.model.AssetAutoTaggerEntry;
import com.liferay.asset.auto.tagger.model.AssetAutoTaggerEntryTable;
import com.liferay.asset.auto.tagger.model.impl.AssetAutoTaggerEntryImpl;
import com.liferay.asset.auto.tagger.model.impl.AssetAutoTaggerEntryModelImpl;
import com.liferay.asset.auto.tagger.service.persistence.AssetAutoTaggerEntryPersistence;
import com.liferay.asset.auto.tagger.service.persistence.AssetAutoTaggerEntryUtil;
import com.liferay.asset.auto.tagger.service.persistence.impl.constants.AssetAutoTaggerPersistenceConstants;
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
 * The persistence implementation for the asset auto tagger entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = AssetAutoTaggerEntryPersistence.class)
public class AssetAutoTaggerEntryPersistenceImpl
	extends BasePersistenceImpl<AssetAutoTaggerEntry, NoSuchEntryException>
	implements AssetAutoTaggerEntryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>AssetAutoTaggerEntryUtil</code> to access the asset auto tagger entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		AssetAutoTaggerEntryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<AssetAutoTaggerEntry, NoSuchEntryException>
			_collectionPersistenceFinderByAssetEntryId;

	/**
	 * Returns an ordered range of all the asset auto tagger entries where assetEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetAutoTaggerEntryModelImpl</code>.
	 * </p>
	 *
	 * @param assetEntryId the asset entry ID
	 * @param start the lower bound of the range of asset auto tagger entries
	 * @param end the upper bound of the range of asset auto tagger entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset auto tagger entries
	 */
	@Override
	public List<AssetAutoTaggerEntry> findByAssetEntryId(
		long assetEntryId, int start, int end,
		OrderByComparator<AssetAutoTaggerEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByAssetEntryId.find(
			finderCache, new Object[] {assetEntryId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset auto tagger entry in the ordered set where assetEntryId = &#63;.
	 *
	 * @param assetEntryId the asset entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset auto tagger entry
	 * @throws NoSuchEntryException if a matching asset auto tagger entry could not be found
	 */
	@Override
	public AssetAutoTaggerEntry findByAssetEntryId_First(
			long assetEntryId,
			OrderByComparator<AssetAutoTaggerEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByAssetEntryId.findFirst(
			finderCache, new Object[] {assetEntryId}, orderByComparator);
	}

	/**
	 * Returns the first asset auto tagger entry in the ordered set where assetEntryId = &#63;.
	 *
	 * @param assetEntryId the asset entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset auto tagger entry, or <code>null</code> if a matching asset auto tagger entry could not be found
	 */
	@Override
	public AssetAutoTaggerEntry fetchByAssetEntryId_First(
		long assetEntryId,
		OrderByComparator<AssetAutoTaggerEntry> orderByComparator) {

		return _collectionPersistenceFinderByAssetEntryId.fetchFirst(
			finderCache, new Object[] {assetEntryId}, orderByComparator);
	}

	/**
	 * Removes all the asset auto tagger entries where assetEntryId = &#63; from the database.
	 *
	 * @param assetEntryId the asset entry ID
	 */
	@Override
	public void removeByAssetEntryId(long assetEntryId) {
		_collectionPersistenceFinderByAssetEntryId.remove(
			finderCache, new Object[] {assetEntryId});
	}

	/**
	 * Returns the number of asset auto tagger entries where assetEntryId = &#63;.
	 *
	 * @param assetEntryId the asset entry ID
	 * @return the number of matching asset auto tagger entries
	 */
	@Override
	public int countByAssetEntryId(long assetEntryId) {
		return _collectionPersistenceFinderByAssetEntryId.count(
			finderCache, new Object[] {assetEntryId});
	}

	private CollectionPersistenceFinder
		<AssetAutoTaggerEntry, NoSuchEntryException>
			_collectionPersistenceFinderByAssetTagId;

	/**
	 * Returns an ordered range of all the asset auto tagger entries where assetTagId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AssetAutoTaggerEntryModelImpl</code>.
	 * </p>
	 *
	 * @param assetTagId the asset tag ID
	 * @param start the lower bound of the range of asset auto tagger entries
	 * @param end the upper bound of the range of asset auto tagger entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset auto tagger entries
	 */
	@Override
	public List<AssetAutoTaggerEntry> findByAssetTagId(
		long assetTagId, int start, int end,
		OrderByComparator<AssetAutoTaggerEntry> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByAssetTagId.find(
			finderCache, new Object[] {assetTagId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first asset auto tagger entry in the ordered set where assetTagId = &#63;.
	 *
	 * @param assetTagId the asset tag ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset auto tagger entry
	 * @throws NoSuchEntryException if a matching asset auto tagger entry could not be found
	 */
	@Override
	public AssetAutoTaggerEntry findByAssetTagId_First(
			long assetTagId,
			OrderByComparator<AssetAutoTaggerEntry> orderByComparator)
		throws NoSuchEntryException {

		return _collectionPersistenceFinderByAssetTagId.findFirst(
			finderCache, new Object[] {assetTagId}, orderByComparator);
	}

	/**
	 * Returns the first asset auto tagger entry in the ordered set where assetTagId = &#63;.
	 *
	 * @param assetTagId the asset tag ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset auto tagger entry, or <code>null</code> if a matching asset auto tagger entry could not be found
	 */
	@Override
	public AssetAutoTaggerEntry fetchByAssetTagId_First(
		long assetTagId,
		OrderByComparator<AssetAutoTaggerEntry> orderByComparator) {

		return _collectionPersistenceFinderByAssetTagId.fetchFirst(
			finderCache, new Object[] {assetTagId}, orderByComparator);
	}

	/**
	 * Removes all the asset auto tagger entries where assetTagId = &#63; from the database.
	 *
	 * @param assetTagId the asset tag ID
	 */
	@Override
	public void removeByAssetTagId(long assetTagId) {
		_collectionPersistenceFinderByAssetTagId.remove(
			finderCache, new Object[] {assetTagId});
	}

	/**
	 * Returns the number of asset auto tagger entries where assetTagId = &#63;.
	 *
	 * @param assetTagId the asset tag ID
	 * @return the number of matching asset auto tagger entries
	 */
	@Override
	public int countByAssetTagId(long assetTagId) {
		return _collectionPersistenceFinderByAssetTagId.count(
			finderCache, new Object[] {assetTagId});
	}

	private UniquePersistenceFinder<AssetAutoTaggerEntry, NoSuchEntryException>
		_uniquePersistenceFinderByA_A;

	/**
	 * Returns the asset auto tagger entry where assetEntryId = &#63; and assetTagId = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param assetEntryId the asset entry ID
	 * @param assetTagId the asset tag ID
	 * @return the matching asset auto tagger entry
	 * @throws NoSuchEntryException if a matching asset auto tagger entry could not be found
	 */
	@Override
	public AssetAutoTaggerEntry findByA_A(long assetEntryId, long assetTagId)
		throws NoSuchEntryException {

		return _uniquePersistenceFinderByA_A.find(
			finderCache, new Object[] {assetEntryId, assetTagId});
	}

	/**
	 * Returns the asset auto tagger entry where assetEntryId = &#63; and assetTagId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param assetEntryId the asset entry ID
	 * @param assetTagId the asset tag ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset auto tagger entry, or <code>null</code> if a matching asset auto tagger entry could not be found
	 */
	@Override
	public AssetAutoTaggerEntry fetchByA_A(
		long assetEntryId, long assetTagId, boolean useFinderCache) {

		return _uniquePersistenceFinderByA_A.fetch(
			finderCache, new Object[] {assetEntryId, assetTagId},
			useFinderCache);
	}

	/**
	 * Removes the asset auto tagger entry where assetEntryId = &#63; and assetTagId = &#63; from the database.
	 *
	 * @param assetEntryId the asset entry ID
	 * @param assetTagId the asset tag ID
	 * @return the asset auto tagger entry that was removed
	 */
	@Override
	public AssetAutoTaggerEntry removeByA_A(long assetEntryId, long assetTagId)
		throws NoSuchEntryException {

		AssetAutoTaggerEntry assetAutoTaggerEntry = findByA_A(
			assetEntryId, assetTagId);

		return remove(assetAutoTaggerEntry);
	}

	/**
	 * Returns the number of asset auto tagger entries where assetEntryId = &#63; and assetTagId = &#63;.
	 *
	 * @param assetEntryId the asset entry ID
	 * @param assetTagId the asset tag ID
	 * @return the number of matching asset auto tagger entries
	 */
	@Override
	public int countByA_A(long assetEntryId, long assetTagId) {
		return _uniquePersistenceFinderByA_A.count(
			finderCache, new Object[] {assetEntryId, assetTagId});
	}

	public AssetAutoTaggerEntryPersistenceImpl() {
		setModelClass(AssetAutoTaggerEntry.class);

		setModelImplClass(AssetAutoTaggerEntryImpl.class);
		setModelPKClass(long.class);

		setTable(AssetAutoTaggerEntryTable.INSTANCE);
	}

	/**
	 * Creates a new asset auto tagger entry with the primary key. Does not add the asset auto tagger entry to the database.
	 *
	 * @param assetAutoTaggerEntryId the primary key for the new asset auto tagger entry
	 * @return the new asset auto tagger entry
	 */
	@Override
	public AssetAutoTaggerEntry create(long assetAutoTaggerEntryId) {
		AssetAutoTaggerEntry assetAutoTaggerEntry =
			new AssetAutoTaggerEntryImpl();

		assetAutoTaggerEntry.setNew(true);
		assetAutoTaggerEntry.setPrimaryKey(assetAutoTaggerEntryId);

		assetAutoTaggerEntry.setCompanyId(CompanyThreadLocal.getCompanyId());

		return assetAutoTaggerEntry;
	}

	/**
	 * Removes the asset auto tagger entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param assetAutoTaggerEntryId the primary key of the asset auto tagger entry
	 * @return the asset auto tagger entry that was removed
	 * @throws NoSuchEntryException if a asset auto tagger entry with the primary key could not be found
	 */
	@Override
	public AssetAutoTaggerEntry remove(long assetAutoTaggerEntryId)
		throws NoSuchEntryException {

		return remove((Serializable)assetAutoTaggerEntryId);
	}

	@Override
	protected AssetAutoTaggerEntry removeImpl(
		AssetAutoTaggerEntry assetAutoTaggerEntry) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(assetAutoTaggerEntry)) {
				assetAutoTaggerEntry = (AssetAutoTaggerEntry)session.get(
					AssetAutoTaggerEntryImpl.class,
					assetAutoTaggerEntry.getPrimaryKeyObj());
			}

			if ((assetAutoTaggerEntry != null) &&
				ctPersistenceHelper.isRemove(assetAutoTaggerEntry)) {

				session.delete(assetAutoTaggerEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (assetAutoTaggerEntry != null) {
			clearCache(assetAutoTaggerEntry);
		}

		return assetAutoTaggerEntry;
	}

	@Override
	public AssetAutoTaggerEntry updateImpl(
		AssetAutoTaggerEntry assetAutoTaggerEntry) {

		boolean isNew = assetAutoTaggerEntry.isNew();

		if (!(assetAutoTaggerEntry instanceof AssetAutoTaggerEntryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(assetAutoTaggerEntry.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					assetAutoTaggerEntry);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in assetAutoTaggerEntry proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom AssetAutoTaggerEntry implementation " +
					assetAutoTaggerEntry.getClass());
		}

		AssetAutoTaggerEntryModelImpl assetAutoTaggerEntryModelImpl =
			(AssetAutoTaggerEntryModelImpl)assetAutoTaggerEntry;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (assetAutoTaggerEntry.getCreateDate() == null)) {
			if (serviceContext == null) {
				assetAutoTaggerEntry.setCreateDate(date);
			}
			else {
				assetAutoTaggerEntry.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!assetAutoTaggerEntryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				assetAutoTaggerEntry.setModifiedDate(date);
			}
			else {
				assetAutoTaggerEntry.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(assetAutoTaggerEntry)) {
				if (!isNew) {
					session.evict(
						AssetAutoTaggerEntryImpl.class,
						assetAutoTaggerEntry.getPrimaryKeyObj());
				}

				session.save(assetAutoTaggerEntry);
			}
			else {
				assetAutoTaggerEntry = (AssetAutoTaggerEntry)session.merge(
					assetAutoTaggerEntry);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(assetAutoTaggerEntry, false);

		if (isNew) {
			assetAutoTaggerEntry.setNew(false);
		}

		assetAutoTaggerEntry.resetOriginalValues();

		return assetAutoTaggerEntry;
	}

	/**
	 * Returns the asset auto tagger entry with the primary key or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param assetAutoTaggerEntryId the primary key of the asset auto tagger entry
	 * @return the asset auto tagger entry
	 * @throws NoSuchEntryException if a asset auto tagger entry with the primary key could not be found
	 */
	@Override
	public AssetAutoTaggerEntry findByPrimaryKey(long assetAutoTaggerEntryId)
		throws NoSuchEntryException {

		return findByPrimaryKey((Serializable)assetAutoTaggerEntryId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the asset auto tagger entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param assetAutoTaggerEntryId the primary key of the asset auto tagger entry
	 * @return the asset auto tagger entry, or <code>null</code> if a asset auto tagger entry with the primary key could not be found
	 */
	@Override
	public AssetAutoTaggerEntry fetchByPrimaryKey(long assetAutoTaggerEntryId) {
		return fetchByPrimaryKey((Serializable)assetAutoTaggerEntryId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "assetAutoTaggerEntryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_ASSETAUTOTAGGERENTRY;
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
		return AssetAutoTaggerEntryModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "AssetAutoTaggerEntry";
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
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("assetEntryId");
		ctMergeColumnNames.add("assetTagId");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("assetAutoTaggerEntryId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(
			new String[] {"assetEntryId", "assetTagId"});
	}

	/**
	 * Initializes the asset auto tagger entry persistence.
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
				_SQL_SELECT_ASSETAUTOTAGGERENTRY_WHERE,
				_SQL_COUNT_ASSETAUTOTAGGERENTRY_WHERE,
				AssetAutoTaggerEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"assetAutoTaggerEntry.", "assetEntryId",
					FinderColumn.Type.LONG, "=", true, true,
					AssetAutoTaggerEntry::getAssetEntryId));

		_collectionPersistenceFinderByAssetTagId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByAssetTagId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"assetTagId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByAssetTagId", new String[] {Long.class.getName()},
					new String[] {"assetTagId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByAssetTagId", new String[] {Long.class.getName()},
					new String[] {"assetTagId"}, false),
				_SQL_SELECT_ASSETAUTOTAGGERENTRY_WHERE,
				_SQL_COUNT_ASSETAUTOTAGGERENTRY_WHERE,
				AssetAutoTaggerEntryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"assetAutoTaggerEntry.", "assetTagId",
					FinderColumn.Type.LONG, "=", true, true,
					AssetAutoTaggerEntry::getAssetTagId));

		_uniquePersistenceFinderByA_A = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByA_A",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"assetEntryId", "assetTagId"}, 0, 0, false,
				AssetAutoTaggerEntry::getAssetEntryId,
				AssetAutoTaggerEntry::getAssetTagId),
			_SQL_SELECT_ASSETAUTOTAGGERENTRY_WHERE, "",
			new FinderColumn<>(
				"assetAutoTaggerEntry.", "assetEntryId", FinderColumn.Type.LONG,
				"=", true, true, AssetAutoTaggerEntry::getAssetEntryId),
			new FinderColumn<>(
				"assetAutoTaggerEntry.", "assetTagId", FinderColumn.Type.LONG,
				"=", true, true, AssetAutoTaggerEntry::getAssetTagId));

		AssetAutoTaggerEntryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		AssetAutoTaggerEntryUtil.setPersistence(null);

		entityCache.removeCache(AssetAutoTaggerEntryImpl.class.getName());
	}

	@Override
	@Reference(
		target = AssetAutoTaggerPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = AssetAutoTaggerPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = AssetAutoTaggerPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
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
		AssetAutoTaggerEntryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_ASSETAUTOTAGGERENTRY =
		"SELECT assetAutoTaggerEntry FROM AssetAutoTaggerEntry assetAutoTaggerEntry";

	private static final String _SQL_SELECT_ASSETAUTOTAGGERENTRY_WHERE =
		"SELECT assetAutoTaggerEntry FROM AssetAutoTaggerEntry assetAutoTaggerEntry WHERE ";

	private static final String _SQL_COUNT_ASSETAUTOTAGGERENTRY_WHERE =
		"SELECT COUNT(assetAutoTaggerEntry) FROM AssetAutoTaggerEntry assetAutoTaggerEntry WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:181971987