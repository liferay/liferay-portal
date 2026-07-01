/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.impl;

import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.spring.extender.service.ServiceReference;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchLVEntryLocalizationException;
import com.liferay.portal.tools.service.builder.test.model.LVEntryLocalization;
import com.liferay.portal.tools.service.builder.test.model.LVEntryLocalizationTable;
import com.liferay.portal.tools.service.builder.test.model.impl.LVEntryLocalizationImpl;
import com.liferay.portal.tools.service.builder.test.model.impl.LVEntryLocalizationModelImpl;
import com.liferay.portal.tools.service.builder.test.service.persistence.LVEntryLocalizationPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.LVEntryLocalizationUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.List;
import java.util.Map;

/**
 * The persistence implementation for the lv entry localization service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class LVEntryLocalizationPersistenceImpl
	extends BasePersistenceImpl
		<LVEntryLocalization, NoSuchLVEntryLocalizationException>
	implements LVEntryLocalizationPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>LVEntryLocalizationUtil</code> to access the lv entry localization persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		LVEntryLocalizationImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<LVEntryLocalization, NoSuchLVEntryLocalizationException>
			_collectionPersistenceFinderByLvEntryId;

	/**
	 * Returns an ordered range of all the lv entry localizations where lvEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LVEntryLocalizationModelImpl</code>.
	 * </p>
	 *
	 * @param lvEntryId the lv entry ID
	 * @param start the lower bound of the range of lv entry localizations
	 * @param end the upper bound of the range of lv entry localizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching lv entry localizations
	 */
	@Override
	public List<LVEntryLocalization> findByLvEntryId(
		long lvEntryId, int start, int end,
		OrderByComparator<LVEntryLocalization> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByLvEntryId.find(
			finderCache, new Object[] {lvEntryId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first lv entry localization in the ordered set where lvEntryId = &#63;.
	 *
	 * @param lvEntryId the lv entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lv entry localization
	 * @throws NoSuchLVEntryLocalizationException if a matching lv entry localization could not be found
	 */
	@Override
	public LVEntryLocalization findByLvEntryId_First(
			long lvEntryId,
			OrderByComparator<LVEntryLocalization> orderByComparator)
		throws NoSuchLVEntryLocalizationException {

		return _collectionPersistenceFinderByLvEntryId.findFirst(
			finderCache, new Object[] {lvEntryId}, orderByComparator);
	}

	/**
	 * Returns the first lv entry localization in the ordered set where lvEntryId = &#63;.
	 *
	 * @param lvEntryId the lv entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lv entry localization, or <code>null</code> if a matching lv entry localization could not be found
	 */
	@Override
	public LVEntryLocalization fetchByLvEntryId_First(
		long lvEntryId,
		OrderByComparator<LVEntryLocalization> orderByComparator) {

		return _collectionPersistenceFinderByLvEntryId.fetchFirst(
			finderCache, new Object[] {lvEntryId}, orderByComparator);
	}

	/**
	 * Removes all the lv entry localizations where lvEntryId = &#63; from the database.
	 *
	 * @param lvEntryId the lv entry ID
	 */
	@Override
	public void removeByLvEntryId(long lvEntryId) {
		_collectionPersistenceFinderByLvEntryId.remove(
			finderCache, new Object[] {lvEntryId});
	}

	/**
	 * Returns the number of lv entry localizations where lvEntryId = &#63;.
	 *
	 * @param lvEntryId the lv entry ID
	 * @return the number of matching lv entry localizations
	 */
	@Override
	public int countByLvEntryId(long lvEntryId) {
		return _collectionPersistenceFinderByLvEntryId.count(
			finderCache, new Object[] {lvEntryId});
	}

	private UniquePersistenceFinder
		<LVEntryLocalization, NoSuchLVEntryLocalizationException>
			_uniquePersistenceFinderByLvEntryId_LanguageId;

	/**
	 * Returns the lv entry localization where lvEntryId = &#63; and languageId = &#63; or throws a <code>NoSuchLVEntryLocalizationException</code> if it could not be found.
	 *
	 * @param lvEntryId the lv entry ID
	 * @param languageId the language ID
	 * @return the matching lv entry localization
	 * @throws NoSuchLVEntryLocalizationException if a matching lv entry localization could not be found
	 */
	@Override
	public LVEntryLocalization findByLvEntryId_LanguageId(
			long lvEntryId, String languageId)
		throws NoSuchLVEntryLocalizationException {

		return _uniquePersistenceFinderByLvEntryId_LanguageId.find(
			finderCache, new Object[] {lvEntryId, languageId});
	}

	/**
	 * Returns the lv entry localization where lvEntryId = &#63; and languageId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param lvEntryId the lv entry ID
	 * @param languageId the language ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching lv entry localization, or <code>null</code> if a matching lv entry localization could not be found
	 */
	@Override
	public LVEntryLocalization fetchByLvEntryId_LanguageId(
		long lvEntryId, String languageId, boolean useFinderCache) {

		return _uniquePersistenceFinderByLvEntryId_LanguageId.fetch(
			finderCache, new Object[] {lvEntryId, languageId}, useFinderCache);
	}

	/**
	 * Removes the lv entry localization where lvEntryId = &#63; and languageId = &#63; from the database.
	 *
	 * @param lvEntryId the lv entry ID
	 * @param languageId the language ID
	 * @return the lv entry localization that was removed
	 */
	@Override
	public LVEntryLocalization removeByLvEntryId_LanguageId(
			long lvEntryId, String languageId)
		throws NoSuchLVEntryLocalizationException {

		LVEntryLocalization lvEntryLocalization = findByLvEntryId_LanguageId(
			lvEntryId, languageId);

		return remove(lvEntryLocalization);
	}

	/**
	 * Returns the number of lv entry localizations where lvEntryId = &#63; and languageId = &#63;.
	 *
	 * @param lvEntryId the lv entry ID
	 * @param languageId the language ID
	 * @return the number of matching lv entry localizations
	 */
	@Override
	public int countByLvEntryId_LanguageId(long lvEntryId, String languageId) {
		return _uniquePersistenceFinderByLvEntryId_LanguageId.count(
			finderCache, new Object[] {lvEntryId, languageId});
	}

	private UniquePersistenceFinder
		<LVEntryLocalization, NoSuchLVEntryLocalizationException>
			_uniquePersistenceFinderByHeadId;

	/**
	 * Returns the lv entry localization where headId = &#63; or throws a <code>NoSuchLVEntryLocalizationException</code> if it could not be found.
	 *
	 * @param headId the head ID
	 * @return the matching lv entry localization
	 * @throws NoSuchLVEntryLocalizationException if a matching lv entry localization could not be found
	 */
	@Override
	public LVEntryLocalization findByHeadId(long headId)
		throws NoSuchLVEntryLocalizationException {

		return _uniquePersistenceFinderByHeadId.find(
			finderCache, new Object[] {headId});
	}

	/**
	 * Returns the lv entry localization where headId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param headId the head ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching lv entry localization, or <code>null</code> if a matching lv entry localization could not be found
	 */
	@Override
	public LVEntryLocalization fetchByHeadId(
		long headId, boolean useFinderCache) {

		return _uniquePersistenceFinderByHeadId.fetch(
			finderCache, new Object[] {headId}, useFinderCache);
	}

	/**
	 * Removes the lv entry localization where headId = &#63; from the database.
	 *
	 * @param headId the head ID
	 * @return the lv entry localization that was removed
	 */
	@Override
	public LVEntryLocalization removeByHeadId(long headId)
		throws NoSuchLVEntryLocalizationException {

		LVEntryLocalization lvEntryLocalization = findByHeadId(headId);

		return remove(lvEntryLocalization);
	}

	/**
	 * Returns the number of lv entry localizations where headId = &#63;.
	 *
	 * @param headId the head ID
	 * @return the number of matching lv entry localizations
	 */
	@Override
	public int countByHeadId(long headId) {
		return _uniquePersistenceFinderByHeadId.count(
			finderCache, new Object[] {headId});
	}

	public LVEntryLocalizationPersistenceImpl() {
		setModelClass(LVEntryLocalization.class);

		setModelImplClass(LVEntryLocalizationImpl.class);
		setModelPKClass(long.class);

		setTable(LVEntryLocalizationTable.INSTANCE);
	}

	/**
	 * Creates a new lv entry localization with the primary key. Does not add the lv entry localization to the database.
	 *
	 * @param lvEntryLocalizationId the primary key for the new lv entry localization
	 * @return the new lv entry localization
	 */
	@Override
	public LVEntryLocalization create(long lvEntryLocalizationId) {
		LVEntryLocalization lvEntryLocalization = new LVEntryLocalizationImpl();

		lvEntryLocalization.setNew(true);
		lvEntryLocalization.setPrimaryKey(lvEntryLocalizationId);

		lvEntryLocalization.setCompanyId(CompanyThreadLocal.getCompanyId());

		return lvEntryLocalization;
	}

	/**
	 * Removes the lv entry localization with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param lvEntryLocalizationId the primary key of the lv entry localization
	 * @return the lv entry localization that was removed
	 * @throws NoSuchLVEntryLocalizationException if a lv entry localization with the primary key could not be found
	 */
	@Override
	public LVEntryLocalization remove(long lvEntryLocalizationId)
		throws NoSuchLVEntryLocalizationException {

		return remove((Serializable)lvEntryLocalizationId);
	}

	@Override
	protected LVEntryLocalization removeImpl(
		LVEntryLocalization lvEntryLocalization) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(lvEntryLocalization)) {
				lvEntryLocalization = (LVEntryLocalization)session.get(
					LVEntryLocalizationImpl.class,
					lvEntryLocalization.getPrimaryKeyObj());
			}

			if (lvEntryLocalization != null) {
				session.delete(lvEntryLocalization);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (lvEntryLocalization != null) {
			clearCache(lvEntryLocalization);
		}

		return lvEntryLocalization;
	}

	@Override
	public LVEntryLocalization updateImpl(
		LVEntryLocalization lvEntryLocalization) {

		boolean isNew = lvEntryLocalization.isNew();

		if (!(lvEntryLocalization instanceof LVEntryLocalizationModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(lvEntryLocalization.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					lvEntryLocalization);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in lvEntryLocalization proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom LVEntryLocalization implementation " +
					lvEntryLocalization.getClass());
		}

		LVEntryLocalizationModelImpl lvEntryLocalizationModelImpl =
			(LVEntryLocalizationModelImpl)lvEntryLocalization;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(lvEntryLocalization);
			}
			else {
				lvEntryLocalization = (LVEntryLocalization)session.merge(
					lvEntryLocalization);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(lvEntryLocalization, false);

		if (isNew) {
			lvEntryLocalization.setNew(false);
		}

		lvEntryLocalization.resetOriginalValues();

		return lvEntryLocalization;
	}

	/**
	 * Returns the lv entry localization with the primary key or throws a <code>NoSuchLVEntryLocalizationException</code> if it could not be found.
	 *
	 * @param lvEntryLocalizationId the primary key of the lv entry localization
	 * @return the lv entry localization
	 * @throws NoSuchLVEntryLocalizationException if a lv entry localization with the primary key could not be found
	 */
	@Override
	public LVEntryLocalization findByPrimaryKey(long lvEntryLocalizationId)
		throws NoSuchLVEntryLocalizationException {

		return findByPrimaryKey((Serializable)lvEntryLocalizationId);
	}

	/**
	 * Returns the lv entry localization with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param lvEntryLocalizationId the primary key of the lv entry localization
	 * @return the lv entry localization, or <code>null</code> if a lv entry localization with the primary key could not be found
	 */
	@Override
	public LVEntryLocalization fetchByPrimaryKey(long lvEntryLocalizationId) {
		return fetchByPrimaryKey((Serializable)lvEntryLocalizationId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "lvEntryLocalizationId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_LVENTRYLOCALIZATION;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return LVEntryLocalizationModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the lv entry localization persistence.
	 */
	public void afterPropertiesSet() {
		_collectionPersistenceFinderByLvEntryId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByLvEntryId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"lvEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByLvEntryId", new String[] {Long.class.getName()},
					new String[] {"lvEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByLvEntryId", new String[] {Long.class.getName()},
					new String[] {"lvEntryId"}, false),
				_SQL_SELECT_LVENTRYLOCALIZATION_WHERE,
				_SQL_COUNT_LVENTRYLOCALIZATION_WHERE,
				LVEntryLocalizationModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"lvEntryLocalization.", "lvEntryId", FinderColumn.Type.LONG,
					"=", true, true, LVEntryLocalization::getLvEntryId));

		_uniquePersistenceFinderByLvEntryId_LanguageId =
			new UniquePersistenceFinder<>(
				this,
				createUniqueFinderPath(
					FINDER_CLASS_NAME_ENTITY, "fetchByLvEntryId_LanguageId",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"lvEntryId", "languageId"}, 0, 2, false,
					LVEntryLocalization::getLvEntryId,
					convertNullFunction(LVEntryLocalization::getLanguageId)),
				_SQL_SELECT_LVENTRYLOCALIZATION_WHERE, "",
				new FinderColumn<>(
					"lvEntryLocalization.", "lvEntryId", FinderColumn.Type.LONG,
					"=", true, true, LVEntryLocalization::getLvEntryId),
				new FinderColumn<>(
					"lvEntryLocalization.", "languageId",
					FinderColumn.Type.STRING, "=", true, true,
					LVEntryLocalization::getLanguageId));

		_uniquePersistenceFinderByHeadId = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByHeadId",
				new String[] {Long.class.getName()}, new String[] {"headId"}, 0,
				0, false, LVEntryLocalization::getHeadId),
			_SQL_SELECT_LVENTRYLOCALIZATION_WHERE, "",
			new FinderColumn<>(
				"lvEntryLocalization.", "headId", FinderColumn.Type.LONG, "=",
				true, true, LVEntryLocalization::getHeadId));

		LVEntryLocalizationUtil.setPersistence(this);
	}

	public void destroy() {
		LVEntryLocalizationUtil.setPersistence(null);

		entityCache.removeCache(LVEntryLocalizationImpl.class.getName());
	}

	@ServiceReference(type = EntityCache.class)
	protected EntityCache entityCache;

	@ServiceReference(type = FinderCache.class)
	protected FinderCache finderCache;

	private static final String _ENTITY_ALIAS_PREFIX =
		LVEntryLocalizationModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_LVENTRYLOCALIZATION =
		"SELECT lvEntryLocalization FROM LVEntryLocalization lvEntryLocalization";

	private static final String _SQL_SELECT_LVENTRYLOCALIZATION_WHERE =
		"SELECT lvEntryLocalization FROM LVEntryLocalization lvEntryLocalization WHERE ";

	private static final String _SQL_COUNT_LVENTRYLOCALIZATION_WHERE =
		"SELECT COUNT(lvEntryLocalization) FROM LVEntryLocalization lvEntryLocalization WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1608208881