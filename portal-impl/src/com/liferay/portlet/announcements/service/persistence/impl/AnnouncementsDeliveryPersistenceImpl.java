/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.announcements.service.persistence.impl;

import com.liferay.announcements.kernel.exception.NoSuchDeliveryException;
import com.liferay.announcements.kernel.model.AnnouncementsDelivery;
import com.liferay.announcements.kernel.model.AnnouncementsDeliveryTable;
import com.liferay.announcements.kernel.service.persistence.AnnouncementsDeliveryPersistence;
import com.liferay.announcements.kernel.service.persistence.AnnouncementsDeliveryUtil;
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
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelperUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portlet.announcements.model.impl.AnnouncementsDeliveryImpl;
import com.liferay.portlet.announcements.model.impl.AnnouncementsDeliveryModelImpl;

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

/**
 * The persistence implementation for the announcements delivery service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class AnnouncementsDeliveryPersistenceImpl
	extends BasePersistenceImpl<AnnouncementsDelivery, NoSuchDeliveryException>
	implements AnnouncementsDeliveryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>AnnouncementsDeliveryUtil</code> to access the announcements delivery persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		AnnouncementsDeliveryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<AnnouncementsDelivery, NoSuchDeliveryException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the announcements deliveries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AnnouncementsDeliveryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of announcements deliveries
	 * @param end the upper bound of the range of announcements deliveries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching announcements deliveries
	 */
	@Override
	public List<AnnouncementsDelivery> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<AnnouncementsDelivery> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first announcements delivery in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching announcements delivery
	 * @throws NoSuchDeliveryException if a matching announcements delivery could not be found
	 */
	@Override
	public AnnouncementsDelivery findByCompanyId_First(
			long companyId,
			OrderByComparator<AnnouncementsDelivery> orderByComparator)
		throws NoSuchDeliveryException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			orderByComparator);
	}

	/**
	 * Returns the first announcements delivery in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching announcements delivery, or <code>null</code> if a matching announcements delivery could not be found
	 */
	@Override
	public AnnouncementsDelivery fetchByCompanyId_First(
		long companyId,
		OrderByComparator<AnnouncementsDelivery> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			orderByComparator);
	}

	/**
	 * Removes all the announcements deliveries where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId});
	}

	/**
	 * Returns the number of announcements deliveries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching announcements deliveries
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId});
	}

	private CollectionPersistenceFinder
		<AnnouncementsDelivery, NoSuchDeliveryException>
			_collectionPersistenceFinderByUserId;

	/**
	 * Returns an ordered range of all the announcements deliveries where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AnnouncementsDeliveryModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of announcements deliveries
	 * @param end the upper bound of the range of announcements deliveries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching announcements deliveries
	 */
	@Override
	public List<AnnouncementsDelivery> findByUserId(
		long userId, int start, int end,
		OrderByComparator<AnnouncementsDelivery> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUserId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {userId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first announcements delivery in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching announcements delivery
	 * @throws NoSuchDeliveryException if a matching announcements delivery could not be found
	 */
	@Override
	public AnnouncementsDelivery findByUserId_First(
			long userId,
			OrderByComparator<AnnouncementsDelivery> orderByComparator)
		throws NoSuchDeliveryException {

		return _collectionPersistenceFinderByUserId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {userId},
			orderByComparator);
	}

	/**
	 * Returns the first announcements delivery in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching announcements delivery, or <code>null</code> if a matching announcements delivery could not be found
	 */
	@Override
	public AnnouncementsDelivery fetchByUserId_First(
		long userId,
		OrderByComparator<AnnouncementsDelivery> orderByComparator) {

		return _collectionPersistenceFinderByUserId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {userId},
			orderByComparator);
	}

	/**
	 * Removes all the announcements deliveries where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	@Override
	public void removeByUserId(long userId) {
		_collectionPersistenceFinderByUserId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {userId});
	}

	/**
	 * Returns the number of announcements deliveries where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching announcements deliveries
	 */
	@Override
	public int countByUserId(long userId) {
		return _collectionPersistenceFinderByUserId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {userId});
	}

	private UniquePersistenceFinder
		<AnnouncementsDelivery, NoSuchDeliveryException>
			_uniquePersistenceFinderByU_T;

	/**
	 * Returns the announcements delivery where userId = &#63; and type = &#63; or throws a <code>NoSuchDeliveryException</code> if it could not be found.
	 *
	 * @param userId the user ID
	 * @param type the type
	 * @return the matching announcements delivery
	 * @throws NoSuchDeliveryException if a matching announcements delivery could not be found
	 */
	@Override
	public AnnouncementsDelivery findByU_T(long userId, String type)
		throws NoSuchDeliveryException {

		return _uniquePersistenceFinderByU_T.find(
			FinderCacheUtil.getFinderCache(), new Object[] {userId, type});
	}

	/**
	 * Returns the announcements delivery where userId = &#63; and type = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param userId the user ID
	 * @param type the type
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching announcements delivery, or <code>null</code> if a matching announcements delivery could not be found
	 */
	@Override
	public AnnouncementsDelivery fetchByU_T(
		long userId, String type, boolean useFinderCache) {

		return _uniquePersistenceFinderByU_T.fetch(
			FinderCacheUtil.getFinderCache(), new Object[] {userId, type},
			useFinderCache);
	}

	/**
	 * Removes the announcements delivery where userId = &#63; and type = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param type the type
	 * @return the announcements delivery that was removed
	 */
	@Override
	public AnnouncementsDelivery removeByU_T(long userId, String type)
		throws NoSuchDeliveryException {

		AnnouncementsDelivery announcementsDelivery = findByU_T(userId, type);

		return remove(announcementsDelivery);
	}

	/**
	 * Returns the number of announcements deliveries where userId = &#63; and type = &#63;.
	 *
	 * @param userId the user ID
	 * @param type the type
	 * @return the number of matching announcements deliveries
	 */
	@Override
	public int countByU_T(long userId, String type) {
		return _uniquePersistenceFinderByU_T.count(
			FinderCacheUtil.getFinderCache(), new Object[] {userId, type});
	}

	public AnnouncementsDeliveryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(AnnouncementsDelivery.class);

		setModelImplClass(AnnouncementsDeliveryImpl.class);
		setModelPKClass(long.class);

		setTable(AnnouncementsDeliveryTable.INSTANCE);
	}

	/**
	 * Creates a new announcements delivery with the primary key. Does not add the announcements delivery to the database.
	 *
	 * @param deliveryId the primary key for the new announcements delivery
	 * @return the new announcements delivery
	 */
	@Override
	public AnnouncementsDelivery create(long deliveryId) {
		AnnouncementsDelivery announcementsDelivery =
			new AnnouncementsDeliveryImpl();

		announcementsDelivery.setNew(true);
		announcementsDelivery.setPrimaryKey(deliveryId);

		announcementsDelivery.setCompanyId(CompanyThreadLocal.getCompanyId());

		return announcementsDelivery;
	}

	/**
	 * Removes the announcements delivery with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param deliveryId the primary key of the announcements delivery
	 * @return the announcements delivery that was removed
	 * @throws NoSuchDeliveryException if a announcements delivery with the primary key could not be found
	 */
	@Override
	public AnnouncementsDelivery remove(long deliveryId)
		throws NoSuchDeliveryException {

		return remove((Serializable)deliveryId);
	}

	@Override
	protected AnnouncementsDelivery removeImpl(
		AnnouncementsDelivery announcementsDelivery) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(announcementsDelivery)) {
				announcementsDelivery = (AnnouncementsDelivery)session.get(
					AnnouncementsDeliveryImpl.class,
					announcementsDelivery.getPrimaryKeyObj());
			}

			if ((announcementsDelivery != null) &&
				CTPersistenceHelperUtil.isRemove(announcementsDelivery)) {

				session.delete(announcementsDelivery);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (announcementsDelivery != null) {
			clearCache(announcementsDelivery);
		}

		return announcementsDelivery;
	}

	@Override
	public AnnouncementsDelivery updateImpl(
		AnnouncementsDelivery announcementsDelivery) {

		boolean isNew = announcementsDelivery.isNew();

		if (!(announcementsDelivery instanceof
				AnnouncementsDeliveryModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(announcementsDelivery.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					announcementsDelivery);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in announcementsDelivery proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom AnnouncementsDelivery implementation " +
					announcementsDelivery.getClass());
		}

		AnnouncementsDeliveryModelImpl announcementsDeliveryModelImpl =
			(AnnouncementsDeliveryModelImpl)announcementsDelivery;

		Session session = null;

		try {
			session = openSession();

			if (CTPersistenceHelperUtil.isInsert(announcementsDelivery)) {
				if (!isNew) {
					session.evict(
						AnnouncementsDeliveryImpl.class,
						announcementsDelivery.getPrimaryKeyObj());
				}

				session.save(announcementsDelivery);
			}
			else {
				announcementsDelivery = (AnnouncementsDelivery)session.merge(
					announcementsDelivery);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(announcementsDelivery, false);

		if (isNew) {
			announcementsDelivery.setNew(false);
		}

		announcementsDelivery.resetOriginalValues();

		return announcementsDelivery;
	}

	/**
	 * Returns the announcements delivery with the primary key or throws a <code>NoSuchDeliveryException</code> if it could not be found.
	 *
	 * @param deliveryId the primary key of the announcements delivery
	 * @return the announcements delivery
	 * @throws NoSuchDeliveryException if a announcements delivery with the primary key could not be found
	 */
	@Override
	public AnnouncementsDelivery findByPrimaryKey(long deliveryId)
		throws NoSuchDeliveryException {

		return findByPrimaryKey((Serializable)deliveryId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return CTPersistenceHelperUtil.getCTPersistenceHelper();
	}

	/**
	 * Returns the announcements delivery with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param deliveryId the primary key of the announcements delivery
	 * @return the announcements delivery, or <code>null</code> if a announcements delivery with the primary key could not be found
	 */
	@Override
	public AnnouncementsDelivery fetchByPrimaryKey(long deliveryId) {
		return fetchByPrimaryKey((Serializable)deliveryId);
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
		return "deliveryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_ANNOUNCEMENTSDELIVERY;
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
		return AnnouncementsDeliveryModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "AnnouncementsDelivery";
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
		ctStrictColumnNames.add("userId");
		ctMergeColumnNames.add("type_");
		ctMergeColumnNames.add("email");
		ctMergeColumnNames.add("sms");
		ctMergeColumnNames.add("website");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("deliveryId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"userId", "type_"});
	}

	/**
	 * Initializes the announcements delivery persistence.
	 */
	public void afterPropertiesSet() {
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
				_SQL_SELECT_ANNOUNCEMENTSDELIVERY_WHERE,
				_SQL_COUNT_ANNOUNCEMENTSDELIVERY_WHERE,
				AnnouncementsDeliveryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"announcementsDelivery.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					AnnouncementsDelivery::getCompanyId));

		_collectionPersistenceFinderByUserId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUserId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"userId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUserId",
					new String[] {Long.class.getName()},
					new String[] {"userId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUserId",
					new String[] {Long.class.getName()},
					new String[] {"userId"}, false),
				_SQL_SELECT_ANNOUNCEMENTSDELIVERY_WHERE,
				_SQL_COUNT_ANNOUNCEMENTSDELIVERY_WHERE,
				AnnouncementsDeliveryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"announcementsDelivery.", "userId", FinderColumn.Type.LONG,
					"=", true, true, AnnouncementsDelivery::getUserId));

		_uniquePersistenceFinderByU_T = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByU_T",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"userId", "type_"}, 0, 2, false,
				AnnouncementsDelivery::getUserId,
				convertNullFunction(AnnouncementsDelivery::getType)),
			_SQL_SELECT_ANNOUNCEMENTSDELIVERY_WHERE, "",
			new FinderColumn<>(
				"announcementsDelivery.", "userId", FinderColumn.Type.LONG, "=",
				true, true, AnnouncementsDelivery::getUserId),
			new FinderColumn<>(
				"announcementsDelivery.", "type", "type_",
				FinderColumn.Type.STRING, "=", true, true,
				AnnouncementsDelivery::getType));

		AnnouncementsDeliveryUtil.setPersistence(this);
	}

	public void destroy() {
		AnnouncementsDeliveryUtil.setPersistence(null);

		EntityCacheUtil.removeCache(AnnouncementsDeliveryImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		AnnouncementsDeliveryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_ANNOUNCEMENTSDELIVERY =
		"SELECT announcementsDelivery FROM AnnouncementsDelivery announcementsDelivery";

	private static final String _SQL_SELECT_ANNOUNCEMENTSDELIVERY_WHERE =
		"SELECT announcementsDelivery FROM AnnouncementsDelivery announcementsDelivery WHERE ";

	private static final String _SQL_COUNT_ANNOUNCEMENTSDELIVERY_WHERE =
		"SELECT COUNT(announcementsDelivery) FROM AnnouncementsDelivery announcementsDelivery WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No AnnouncementsDelivery exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		AnnouncementsDeliveryPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"type"});

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1242801855