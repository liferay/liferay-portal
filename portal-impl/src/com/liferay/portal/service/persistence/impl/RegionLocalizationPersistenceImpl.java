/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.impl;

import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.NoSuchRegionLocalizationException;
import com.liferay.portal.kernel.model.RegionLocalization;
import com.liferay.portal.kernel.model.RegionLocalizationTable;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.RegionLocalizationPersistence;
import com.liferay.portal.kernel.service.persistence.RegionLocalizationUtil;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelperUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.model.impl.RegionLocalizationImpl;
import com.liferay.portal.model.impl.RegionLocalizationModelImpl;

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
 * The persistence implementation for the region localization service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class RegionLocalizationPersistenceImpl
	extends BasePersistenceImpl
		<RegionLocalization, NoSuchRegionLocalizationException>
	implements RegionLocalizationPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>RegionLocalizationUtil</code> to access the region localization persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		RegionLocalizationImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<RegionLocalization, NoSuchRegionLocalizationException>
			_collectionPersistenceFinderByRegionId;

	/**
	 * Returns an ordered range of all the region localizations where regionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RegionLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param regionId the region ID
	 * @param start the lower bound of the range of region localizations
	 * @param end the upper bound of the range of region localizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching region localizations
	 */
	@Override
	public List<RegionLocalization> findByRegionId(
		long regionId, int start, int end,
		OrderByComparator<RegionLocalization> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByRegionId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {regionId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first region localization in the ordered set where regionId = &#63;.
	 *
	 * @param regionId the region ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching region localization
	 * @throws NoSuchRegionLocalizationException if a matching region localization could not be found
	 */
	@Override
	public RegionLocalization findByRegionId_First(
			long regionId,
			OrderByComparator<RegionLocalization> orderByComparator)
		throws NoSuchRegionLocalizationException {

		return _collectionPersistenceFinderByRegionId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {regionId},
			orderByComparator);
	}

	/**
	 * Returns the first region localization in the ordered set where regionId = &#63;.
	 *
	 * @param regionId the region ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching region localization, or <code>null</code> if a matching region localization could not be found
	 */
	@Override
	public RegionLocalization fetchByRegionId_First(
		long regionId,
		OrderByComparator<RegionLocalization> orderByComparator) {

		return _collectionPersistenceFinderByRegionId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {regionId},
			orderByComparator);
	}

	/**
	 * Removes all the region localizations where regionId = &#63; from the database.
	 *
	 * @param regionId the region ID
	 */
	@Override
	public void removeByRegionId(long regionId) {
		_collectionPersistenceFinderByRegionId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {regionId});
	}

	/**
	 * Returns the number of region localizations where regionId = &#63;.
	 *
	 * @param regionId the region ID
	 * @return the number of matching region localizations
	 */
	@Override
	public int countByRegionId(long regionId) {
		return _collectionPersistenceFinderByRegionId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {regionId});
	}

	private UniquePersistenceFinder
		<RegionLocalization, NoSuchRegionLocalizationException>
			_uniquePersistenceFinderByRegionId_LanguageId;

	/**
	 * Returns the region localization where regionId = &#63; and languageId = &#63; or throws a <code>NoSuchRegionLocalizationException</code> if it could not be found.
	 *
	 * @param regionId the region ID
	 * @param languageId the language ID
	 * @return the matching region localization
	 * @throws NoSuchRegionLocalizationException if a matching region localization could not be found
	 */
	@Override
	public RegionLocalization findByRegionId_LanguageId(
			long regionId, String languageId)
		throws NoSuchRegionLocalizationException {

		return _uniquePersistenceFinderByRegionId_LanguageId.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {regionId, languageId});
	}

	/**
	 * Returns the region localization where regionId = &#63; and languageId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param regionId the region ID
	 * @param languageId the language ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching region localization, or <code>null</code> if a matching region localization could not be found
	 */
	@Override
	public RegionLocalization fetchByRegionId_LanguageId(
		long regionId, String languageId, boolean useFinderCache) {

		return _uniquePersistenceFinderByRegionId_LanguageId.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {regionId, languageId}, useFinderCache);
	}

	/**
	 * Removes the region localization where regionId = &#63; and languageId = &#63; from the database.
	 *
	 * @param regionId the region ID
	 * @param languageId the language ID
	 * @return the region localization that was removed
	 */
	@Override
	public RegionLocalization removeByRegionId_LanguageId(
			long regionId, String languageId)
		throws NoSuchRegionLocalizationException {

		RegionLocalization regionLocalization = findByRegionId_LanguageId(
			regionId, languageId);

		return remove(regionLocalization);
	}

	/**
	 * Returns the number of region localizations where regionId = &#63; and languageId = &#63;.
	 *
	 * @param regionId the region ID
	 * @param languageId the language ID
	 * @return the number of matching region localizations
	 */
	@Override
	public int countByRegionId_LanguageId(long regionId, String languageId) {
		return _uniquePersistenceFinderByRegionId_LanguageId.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {regionId, languageId});
	}

	public RegionLocalizationPersistenceImpl() {
		setModelClass(RegionLocalization.class);

		setModelImplClass(RegionLocalizationImpl.class);
		setModelPKClass(long.class);

		setTable(RegionLocalizationTable.INSTANCE);
	}

	/**
	 * Creates a new region localization with the primary key. Does not add the region localization to the database.
	 *
	 * @param regionLocalizationId the primary key for the new region localization
	 * @return the new region localization
	 */
	@Override
	public RegionLocalization create(long regionLocalizationId) {
		RegionLocalization regionLocalization = new RegionLocalizationImpl();

		regionLocalization.setNew(true);
		regionLocalization.setPrimaryKey(regionLocalizationId);

		regionLocalization.setCompanyId(CompanyThreadLocal.getCompanyId());

		return regionLocalization;
	}

	/**
	 * Removes the region localization with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param regionLocalizationId the primary key of the region localization
	 * @return the region localization that was removed
	 * @throws NoSuchRegionLocalizationException if a region localization with the primary key could not be found
	 */
	@Override
	public RegionLocalization remove(long regionLocalizationId)
		throws NoSuchRegionLocalizationException {

		return remove((Serializable)regionLocalizationId);
	}

	@Override
	protected RegionLocalization removeImpl(
		RegionLocalization regionLocalization) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(regionLocalization)) {
				regionLocalization = (RegionLocalization)session.get(
					RegionLocalizationImpl.class,
					regionLocalization.getPrimaryKeyObj());
			}

			if ((regionLocalization != null) &&
				CTPersistenceHelperUtil.isRemove(regionLocalization)) {

				session.delete(regionLocalization);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (regionLocalization != null) {
			clearCache(regionLocalization);
		}

		return regionLocalization;
	}

	@Override
	public RegionLocalization updateImpl(
		RegionLocalization regionLocalization) {

		boolean isNew = regionLocalization.isNew();

		if (!(regionLocalization instanceof RegionLocalizationModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(regionLocalization.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					regionLocalization);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in regionLocalization proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom RegionLocalization implementation " +
					regionLocalization.getClass());
		}

		RegionLocalizationModelImpl regionLocalizationModelImpl =
			(RegionLocalizationModelImpl)regionLocalization;

		Session session = null;

		try {
			session = openSession();

			if (CTPersistenceHelperUtil.isInsert(regionLocalization)) {
				if (!isNew) {
					session.evict(
						RegionLocalizationImpl.class,
						regionLocalization.getPrimaryKeyObj());
				}

				session.save(regionLocalization);
			}
			else {
				regionLocalization = (RegionLocalization)session.merge(
					regionLocalization);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(regionLocalization, false);

		if (isNew) {
			regionLocalization.setNew(false);
		}

		regionLocalization.resetOriginalValues();

		return regionLocalization;
	}

	/**
	 * Returns the region localization with the primary key or throws a <code>NoSuchRegionLocalizationException</code> if it could not be found.
	 *
	 * @param regionLocalizationId the primary key of the region localization
	 * @return the region localization
	 * @throws NoSuchRegionLocalizationException if a region localization with the primary key could not be found
	 */
	@Override
	public RegionLocalization findByPrimaryKey(long regionLocalizationId)
		throws NoSuchRegionLocalizationException {

		return findByPrimaryKey((Serializable)regionLocalizationId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return CTPersistenceHelperUtil.getCTPersistenceHelper();
	}

	/**
	 * Returns the region localization with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param regionLocalizationId the primary key of the region localization
	 * @return the region localization, or <code>null</code> if a region localization with the primary key could not be found
	 */
	@Override
	public RegionLocalization fetchByPrimaryKey(long regionLocalizationId) {
		return fetchByPrimaryKey((Serializable)regionLocalizationId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return EntityCacheUtil.getEntityCache();
	}

	@Override
	protected String getPKDBName() {
		return "regionLocalizationId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_REGIONLOCALIZATION;
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
		return RegionLocalizationModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "RegionLocalization";
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
		ctMergeColumnNames.add("regionId");
		ctMergeColumnNames.add("languageId");
		ctMergeColumnNames.add("title");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("regionLocalizationId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"regionId", "languageId"});
	}

	/**
	 * Initializes the region localization persistence.
	 */
	public void afterPropertiesSet() {
		_collectionPersistenceFinderByRegionId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByRegionId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"regionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByRegionId",
					new String[] {Long.class.getName()},
					new String[] {"regionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByRegionId", new String[] {Long.class.getName()},
					new String[] {"regionId"}, false),
				_SQL_SELECT_REGIONLOCALIZATION_WHERE,
				_SQL_COUNT_REGIONLOCALIZATION_WHERE,
				RegionLocalizationModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"regionLocalization.", "regionId", FinderColumn.Type.LONG,
					"=", true, true, RegionLocalization::getRegionId));

		_uniquePersistenceFinderByRegionId_LanguageId =
			new UniquePersistenceFinder<>(
				this,
				createUniqueFinderPath(
					FINDER_CLASS_NAME_ENTITY, "fetchByRegionId_LanguageId",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"regionId", "languageId"}, 0, 2, false,
					RegionLocalization::getRegionId,
					convertNullFunction(RegionLocalization::getLanguageId)),
				_SQL_SELECT_REGIONLOCALIZATION_WHERE, "",
				new FinderColumn<>(
					"regionLocalization.", "regionId", FinderColumn.Type.LONG,
					"=", true, true, RegionLocalization::getRegionId),
				new FinderColumn<>(
					"regionLocalization.", "languageId",
					FinderColumn.Type.STRING, "=", true, true,
					RegionLocalization::getLanguageId));

		RegionLocalizationUtil.setPersistence(this);
	}

	public void destroy() {
		RegionLocalizationUtil.setPersistence(null);

		EntityCacheUtil.removeCache(RegionLocalizationImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		RegionLocalizationModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_REGIONLOCALIZATION =
		"SELECT regionLocalization FROM RegionLocalization regionLocalization";

	private static final String _SQL_SELECT_REGIONLOCALIZATION_WHERE =
		"SELECT regionLocalization FROM RegionLocalization regionLocalization WHERE ";

	private static final String _SQL_COUNT_REGIONLOCALIZATION_WHERE =
		"SELECT COUNT(regionLocalization) FROM RegionLocalization regionLocalization WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-591249980