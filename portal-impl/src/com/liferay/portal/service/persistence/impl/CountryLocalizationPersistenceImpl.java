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
import com.liferay.portal.kernel.exception.NoSuchCountryLocalizationException;
import com.liferay.portal.kernel.model.CountryLocalization;
import com.liferay.portal.kernel.model.CountryLocalizationTable;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.CountryLocalizationPersistence;
import com.liferay.portal.kernel.service.persistence.CountryLocalizationUtil;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelperUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.model.impl.CountryLocalizationImpl;
import com.liferay.portal.model.impl.CountryLocalizationModelImpl;

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
 * The persistence implementation for the country localization service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class CountryLocalizationPersistenceImpl
	extends BasePersistenceImpl
		<CountryLocalization, NoSuchCountryLocalizationException>
	implements CountryLocalizationPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CountryLocalizationUtil</code> to access the country localization persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CountryLocalizationImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CountryLocalization, NoSuchCountryLocalizationException>
			_collectionPersistenceFinderByCountryId;

	/**
	 * Returns an ordered range of all the country localizations where countryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CountryLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param countryId the country ID
	 * @param start the lower bound of the range of country localizations
	 * @param end the upper bound of the range of country localizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching country localizations
	 */
	@Override
	public List<CountryLocalization> findByCountryId(
		long countryId, int start, int end,
		OrderByComparator<CountryLocalization> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCountryId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {countryId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first country localization in the ordered set where countryId = &#63;.
	 *
	 * @param countryId the country ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching country localization
	 * @throws NoSuchCountryLocalizationException if a matching country localization could not be found
	 */
	@Override
	public CountryLocalization findByCountryId_First(
			long countryId,
			OrderByComparator<CountryLocalization> orderByComparator)
		throws NoSuchCountryLocalizationException {

		return _collectionPersistenceFinderByCountryId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {countryId},
			orderByComparator);
	}

	/**
	 * Returns the first country localization in the ordered set where countryId = &#63;.
	 *
	 * @param countryId the country ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching country localization, or <code>null</code> if a matching country localization could not be found
	 */
	@Override
	public CountryLocalization fetchByCountryId_First(
		long countryId,
		OrderByComparator<CountryLocalization> orderByComparator) {

		return _collectionPersistenceFinderByCountryId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {countryId},
			orderByComparator);
	}

	/**
	 * Removes all the country localizations where countryId = &#63; from the database.
	 *
	 * @param countryId the country ID
	 */
	@Override
	public void removeByCountryId(long countryId) {
		_collectionPersistenceFinderByCountryId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {countryId});
	}

	/**
	 * Returns the number of country localizations where countryId = &#63;.
	 *
	 * @param countryId the country ID
	 * @return the number of matching country localizations
	 */
	@Override
	public int countByCountryId(long countryId) {
		return _collectionPersistenceFinderByCountryId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {countryId});
	}

	private UniquePersistenceFinder
		<CountryLocalization, NoSuchCountryLocalizationException>
			_uniquePersistenceFinderByCountryId_LanguageId;

	/**
	 * Returns the country localization where countryId = &#63; and languageId = &#63; or throws a <code>NoSuchCountryLocalizationException</code> if it could not be found.
	 *
	 * @param countryId the country ID
	 * @param languageId the language ID
	 * @return the matching country localization
	 * @throws NoSuchCountryLocalizationException if a matching country localization could not be found
	 */
	@Override
	public CountryLocalization findByCountryId_LanguageId(
			long countryId, String languageId)
		throws NoSuchCountryLocalizationException {

		return _uniquePersistenceFinderByCountryId_LanguageId.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {countryId, languageId});
	}

	/**
	 * Returns the country localization where countryId = &#63; and languageId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param countryId the country ID
	 * @param languageId the language ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching country localization, or <code>null</code> if a matching country localization could not be found
	 */
	@Override
	public CountryLocalization fetchByCountryId_LanguageId(
		long countryId, String languageId, boolean useFinderCache) {

		return _uniquePersistenceFinderByCountryId_LanguageId.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {countryId, languageId}, useFinderCache);
	}

	/**
	 * Removes the country localization where countryId = &#63; and languageId = &#63; from the database.
	 *
	 * @param countryId the country ID
	 * @param languageId the language ID
	 * @return the country localization that was removed
	 */
	@Override
	public CountryLocalization removeByCountryId_LanguageId(
			long countryId, String languageId)
		throws NoSuchCountryLocalizationException {

		CountryLocalization countryLocalization = findByCountryId_LanguageId(
			countryId, languageId);

		return remove(countryLocalization);
	}

	/**
	 * Returns the number of country localizations where countryId = &#63; and languageId = &#63;.
	 *
	 * @param countryId the country ID
	 * @param languageId the language ID
	 * @return the number of matching country localizations
	 */
	@Override
	public int countByCountryId_LanguageId(long countryId, String languageId) {
		return _uniquePersistenceFinderByCountryId_LanguageId.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {countryId, languageId});
	}

	public CountryLocalizationPersistenceImpl() {
		setModelClass(CountryLocalization.class);

		setModelImplClass(CountryLocalizationImpl.class);
		setModelPKClass(long.class);

		setTable(CountryLocalizationTable.INSTANCE);
	}

	/**
	 * Creates a new country localization with the primary key. Does not add the country localization to the database.
	 *
	 * @param countryLocalizationId the primary key for the new country localization
	 * @return the new country localization
	 */
	@Override
	public CountryLocalization create(long countryLocalizationId) {
		CountryLocalization countryLocalization = new CountryLocalizationImpl();

		countryLocalization.setNew(true);
		countryLocalization.setPrimaryKey(countryLocalizationId);

		countryLocalization.setCompanyId(CompanyThreadLocal.getCompanyId());

		return countryLocalization;
	}

	/**
	 * Removes the country localization with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param countryLocalizationId the primary key of the country localization
	 * @return the country localization that was removed
	 * @throws NoSuchCountryLocalizationException if a country localization with the primary key could not be found
	 */
	@Override
	public CountryLocalization remove(long countryLocalizationId)
		throws NoSuchCountryLocalizationException {

		return remove((Serializable)countryLocalizationId);
	}

	@Override
	protected CountryLocalization removeImpl(
		CountryLocalization countryLocalization) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(countryLocalization)) {
				countryLocalization = (CountryLocalization)session.get(
					CountryLocalizationImpl.class,
					countryLocalization.getPrimaryKeyObj());
			}

			if ((countryLocalization != null) &&
				CTPersistenceHelperUtil.isRemove(countryLocalization)) {

				session.delete(countryLocalization);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (countryLocalization != null) {
			clearCache(countryLocalization);
		}

		return countryLocalization;
	}

	@Override
	public CountryLocalization updateImpl(
		CountryLocalization countryLocalization) {

		boolean isNew = countryLocalization.isNew();

		if (!(countryLocalization instanceof CountryLocalizationModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(countryLocalization.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					countryLocalization);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in countryLocalization proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CountryLocalization implementation " +
					countryLocalization.getClass());
		}

		CountryLocalizationModelImpl countryLocalizationModelImpl =
			(CountryLocalizationModelImpl)countryLocalization;

		Session session = null;

		try {
			session = openSession();

			if (CTPersistenceHelperUtil.isInsert(countryLocalization)) {
				if (!isNew) {
					session.evict(
						CountryLocalizationImpl.class,
						countryLocalization.getPrimaryKeyObj());
				}

				session.save(countryLocalization);
			}
			else {
				countryLocalization = (CountryLocalization)session.merge(
					countryLocalization);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(countryLocalization, false);

		if (isNew) {
			countryLocalization.setNew(false);
		}

		countryLocalization.resetOriginalValues();

		return countryLocalization;
	}

	/**
	 * Returns the country localization with the primary key or throws a <code>NoSuchCountryLocalizationException</code> if it could not be found.
	 *
	 * @param countryLocalizationId the primary key of the country localization
	 * @return the country localization
	 * @throws NoSuchCountryLocalizationException if a country localization with the primary key could not be found
	 */
	@Override
	public CountryLocalization findByPrimaryKey(long countryLocalizationId)
		throws NoSuchCountryLocalizationException {

		return findByPrimaryKey((Serializable)countryLocalizationId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return CTPersistenceHelperUtil.getCTPersistenceHelper();
	}

	/**
	 * Returns the country localization with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param countryLocalizationId the primary key of the country localization
	 * @return the country localization, or <code>null</code> if a country localization with the primary key could not be found
	 */
	@Override
	public CountryLocalization fetchByPrimaryKey(long countryLocalizationId) {
		return fetchByPrimaryKey((Serializable)countryLocalizationId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return EntityCacheUtil.getEntityCache();
	}

	@Override
	protected String getPKDBName() {
		return "countryLocalizationId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COUNTRYLOCALIZATION;
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
		return CountryLocalizationModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "CountryLocalization";
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
		ctMergeColumnNames.add("countryId");
		ctMergeColumnNames.add("languageId");
		ctMergeColumnNames.add("title");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("countryLocalizationId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"countryId", "languageId"});
	}

	/**
	 * Initializes the country localization persistence.
	 */
	public void afterPropertiesSet() {
		_collectionPersistenceFinderByCountryId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCountryId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"countryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCountryId", new String[] {Long.class.getName()},
					new String[] {"countryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCountryId", new String[] {Long.class.getName()},
					new String[] {"countryId"}, false),
				_SQL_SELECT_COUNTRYLOCALIZATION_WHERE,
				_SQL_COUNT_COUNTRYLOCALIZATION_WHERE,
				CountryLocalizationModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"countryLocalization.", "countryId", FinderColumn.Type.LONG,
					"=", true, true, CountryLocalization::getCountryId));

		_uniquePersistenceFinderByCountryId_LanguageId =
			new UniquePersistenceFinder<>(
				this,
				createUniqueFinderPath(
					FINDER_CLASS_NAME_ENTITY, "fetchByCountryId_LanguageId",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"countryId", "languageId"}, 0, 2, false,
					CountryLocalization::getCountryId,
					convertNullFunction(CountryLocalization::getLanguageId)),
				_SQL_SELECT_COUNTRYLOCALIZATION_WHERE, "",
				new FinderColumn<>(
					"countryLocalization.", "countryId", FinderColumn.Type.LONG,
					"=", true, true, CountryLocalization::getCountryId),
				new FinderColumn<>(
					"countryLocalization.", "languageId",
					FinderColumn.Type.STRING, "=", true, true,
					CountryLocalization::getLanguageId));

		CountryLocalizationUtil.setPersistence(this);
	}

	public void destroy() {
		CountryLocalizationUtil.setPersistence(null);

		EntityCacheUtil.removeCache(CountryLocalizationImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		CountryLocalizationModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_COUNTRYLOCALIZATION =
		"SELECT countryLocalization FROM CountryLocalization countryLocalization";

	private static final String _SQL_SELECT_COUNTRYLOCALIZATION_WHERE =
		"SELECT countryLocalization FROM CountryLocalization countryLocalization WHERE ";

	private static final String _SQL_COUNT_COUNTRYLOCALIZATION_WHERE =
		"SELECT COUNT(countryLocalization) FROM CountryLocalization countryLocalization WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1408660356