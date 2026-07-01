/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.announcements.service.persistence.impl;

import com.liferay.announcements.kernel.exception.NoSuchFlagException;
import com.liferay.announcements.kernel.model.AnnouncementsFlag;
import com.liferay.announcements.kernel.model.AnnouncementsFlagTable;
import com.liferay.announcements.kernel.service.persistence.AnnouncementsFlagPersistence;
import com.liferay.announcements.kernel.service.persistence.AnnouncementsFlagUtil;
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
import com.liferay.portlet.announcements.model.impl.AnnouncementsFlagImpl;
import com.liferay.portlet.announcements.model.impl.AnnouncementsFlagModelImpl;

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

/**
 * The persistence implementation for the announcements flag service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class AnnouncementsFlagPersistenceImpl
	extends BasePersistenceImpl<AnnouncementsFlag, NoSuchFlagException>
	implements AnnouncementsFlagPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>AnnouncementsFlagUtil</code> to access the announcements flag persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		AnnouncementsFlagImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<AnnouncementsFlag, NoSuchFlagException>
		_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the announcements flags where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AnnouncementsFlagModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of announcements flags
	 * @param end the upper bound of the range of announcements flags (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching announcements flags
	 */
	@Override
	public List<AnnouncementsFlag> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<AnnouncementsFlag> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first announcements flag in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching announcements flag
	 * @throws NoSuchFlagException if a matching announcements flag could not be found
	 */
	@Override
	public AnnouncementsFlag findByCompanyId_First(
			long companyId,
			OrderByComparator<AnnouncementsFlag> orderByComparator)
		throws NoSuchFlagException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			orderByComparator);
	}

	/**
	 * Returns the first announcements flag in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching announcements flag, or <code>null</code> if a matching announcements flag could not be found
	 */
	@Override
	public AnnouncementsFlag fetchByCompanyId_First(
		long companyId,
		OrderByComparator<AnnouncementsFlag> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			orderByComparator);
	}

	/**
	 * Removes all the announcements flags where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId});
	}

	/**
	 * Returns the number of announcements flags where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching announcements flags
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId});
	}

	private CollectionPersistenceFinder<AnnouncementsFlag, NoSuchFlagException>
		_collectionPersistenceFinderByEntryId;

	/**
	 * Returns an ordered range of all the announcements flags where entryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AnnouncementsFlagModelImpl</code>.
	 * </p>
	 *
	 * @param entryId the entry ID
	 * @param start the lower bound of the range of announcements flags
	 * @param end the upper bound of the range of announcements flags (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching announcements flags
	 */
	@Override
	public List<AnnouncementsFlag> findByEntryId(
		long entryId, int start, int end,
		OrderByComparator<AnnouncementsFlag> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByEntryId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {entryId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first announcements flag in the ordered set where entryId = &#63;.
	 *
	 * @param entryId the entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching announcements flag
	 * @throws NoSuchFlagException if a matching announcements flag could not be found
	 */
	@Override
	public AnnouncementsFlag findByEntryId_First(
			long entryId,
			OrderByComparator<AnnouncementsFlag> orderByComparator)
		throws NoSuchFlagException {

		return _collectionPersistenceFinderByEntryId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {entryId},
			orderByComparator);
	}

	/**
	 * Returns the first announcements flag in the ordered set where entryId = &#63;.
	 *
	 * @param entryId the entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching announcements flag, or <code>null</code> if a matching announcements flag could not be found
	 */
	@Override
	public AnnouncementsFlag fetchByEntryId_First(
		long entryId, OrderByComparator<AnnouncementsFlag> orderByComparator) {

		return _collectionPersistenceFinderByEntryId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {entryId},
			orderByComparator);
	}

	/**
	 * Removes all the announcements flags where entryId = &#63; from the database.
	 *
	 * @param entryId the entry ID
	 */
	@Override
	public void removeByEntryId(long entryId) {
		_collectionPersistenceFinderByEntryId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {entryId});
	}

	/**
	 * Returns the number of announcements flags where entryId = &#63;.
	 *
	 * @param entryId the entry ID
	 * @return the number of matching announcements flags
	 */
	@Override
	public int countByEntryId(long entryId) {
		return _collectionPersistenceFinderByEntryId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {entryId});
	}

	private UniquePersistenceFinder<AnnouncementsFlag, NoSuchFlagException>
		_uniquePersistenceFinderByU_E_V;

	/**
	 * Returns the announcements flag where userId = &#63; and entryId = &#63; and value = &#63; or throws a <code>NoSuchFlagException</code> if it could not be found.
	 *
	 * @param userId the user ID
	 * @param entryId the entry ID
	 * @param value the value
	 * @return the matching announcements flag
	 * @throws NoSuchFlagException if a matching announcements flag could not be found
	 */
	@Override
	public AnnouncementsFlag findByU_E_V(long userId, long entryId, int value)
		throws NoSuchFlagException {

		return _uniquePersistenceFinderByU_E_V.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, entryId, value});
	}

	/**
	 * Returns the announcements flag where userId = &#63; and entryId = &#63; and value = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param userId the user ID
	 * @param entryId the entry ID
	 * @param value the value
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching announcements flag, or <code>null</code> if a matching announcements flag could not be found
	 */
	@Override
	public AnnouncementsFlag fetchByU_E_V(
		long userId, long entryId, int value, boolean useFinderCache) {

		return _uniquePersistenceFinderByU_E_V.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, entryId, value}, useFinderCache);
	}

	/**
	 * Removes the announcements flag where userId = &#63; and entryId = &#63; and value = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param entryId the entry ID
	 * @param value the value
	 * @return the announcements flag that was removed
	 */
	@Override
	public AnnouncementsFlag removeByU_E_V(long userId, long entryId, int value)
		throws NoSuchFlagException {

		AnnouncementsFlag announcementsFlag = findByU_E_V(
			userId, entryId, value);

		return remove(announcementsFlag);
	}

	/**
	 * Returns the number of announcements flags where userId = &#63; and entryId = &#63; and value = &#63;.
	 *
	 * @param userId the user ID
	 * @param entryId the entry ID
	 * @param value the value
	 * @return the number of matching announcements flags
	 */
	@Override
	public int countByU_E_V(long userId, long entryId, int value) {
		return _uniquePersistenceFinderByU_E_V.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, entryId, value});
	}

	public AnnouncementsFlagPersistenceImpl() {
		setModelClass(AnnouncementsFlag.class);

		setModelImplClass(AnnouncementsFlagImpl.class);
		setModelPKClass(long.class);

		setTable(AnnouncementsFlagTable.INSTANCE);
	}

	/**
	 * Creates a new announcements flag with the primary key. Does not add the announcements flag to the database.
	 *
	 * @param flagId the primary key for the new announcements flag
	 * @return the new announcements flag
	 */
	@Override
	public AnnouncementsFlag create(long flagId) {
		AnnouncementsFlag announcementsFlag = new AnnouncementsFlagImpl();

		announcementsFlag.setNew(true);
		announcementsFlag.setPrimaryKey(flagId);

		announcementsFlag.setCompanyId(CompanyThreadLocal.getCompanyId());

		return announcementsFlag;
	}

	/**
	 * Removes the announcements flag with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param flagId the primary key of the announcements flag
	 * @return the announcements flag that was removed
	 * @throws NoSuchFlagException if a announcements flag with the primary key could not be found
	 */
	@Override
	public AnnouncementsFlag remove(long flagId) throws NoSuchFlagException {
		return remove((Serializable)flagId);
	}

	@Override
	protected AnnouncementsFlag removeImpl(
		AnnouncementsFlag announcementsFlag) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(announcementsFlag)) {
				announcementsFlag = (AnnouncementsFlag)session.get(
					AnnouncementsFlagImpl.class,
					announcementsFlag.getPrimaryKeyObj());
			}

			if ((announcementsFlag != null) &&
				CTPersistenceHelperUtil.isRemove(announcementsFlag)) {

				session.delete(announcementsFlag);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (announcementsFlag != null) {
			clearCache(announcementsFlag);
		}

		return announcementsFlag;
	}

	@Override
	public AnnouncementsFlag updateImpl(AnnouncementsFlag announcementsFlag) {
		boolean isNew = announcementsFlag.isNew();

		if (!(announcementsFlag instanceof AnnouncementsFlagModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(announcementsFlag.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					announcementsFlag);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in announcementsFlag proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom AnnouncementsFlag implementation " +
					announcementsFlag.getClass());
		}

		AnnouncementsFlagModelImpl announcementsFlagModelImpl =
			(AnnouncementsFlagModelImpl)announcementsFlag;

		if (isNew && (announcementsFlag.getCreateDate() == null)) {
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			Date date = new Date();

			if (serviceContext == null) {
				announcementsFlag.setCreateDate(date);
			}
			else {
				announcementsFlag.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (CTPersistenceHelperUtil.isInsert(announcementsFlag)) {
				if (!isNew) {
					session.evict(
						AnnouncementsFlagImpl.class,
						announcementsFlag.getPrimaryKeyObj());
				}

				session.save(announcementsFlag);
			}
			else {
				announcementsFlag = (AnnouncementsFlag)session.merge(
					announcementsFlag);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(announcementsFlag, false);

		if (isNew) {
			announcementsFlag.setNew(false);
		}

		announcementsFlag.resetOriginalValues();

		return announcementsFlag;
	}

	/**
	 * Returns the announcements flag with the primary key or throws a <code>NoSuchFlagException</code> if it could not be found.
	 *
	 * @param flagId the primary key of the announcements flag
	 * @return the announcements flag
	 * @throws NoSuchFlagException if a announcements flag with the primary key could not be found
	 */
	@Override
	public AnnouncementsFlag findByPrimaryKey(long flagId)
		throws NoSuchFlagException {

		return findByPrimaryKey((Serializable)flagId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return CTPersistenceHelperUtil.getCTPersistenceHelper();
	}

	/**
	 * Returns the announcements flag with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param flagId the primary key of the announcements flag
	 * @return the announcements flag, or <code>null</code> if a announcements flag with the primary key could not be found
	 */
	@Override
	public AnnouncementsFlag fetchByPrimaryKey(long flagId) {
		return fetchByPrimaryKey((Serializable)flagId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return EntityCacheUtil.getEntityCache();
	}

	@Override
	protected String getPKDBName() {
		return "flagId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_ANNOUNCEMENTSFLAG;
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
		return AnnouncementsFlagModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "AnnouncementsFlag";
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
		ctStrictColumnNames.add("createDate");
		ctMergeColumnNames.add("entryId");
		ctMergeColumnNames.add("value");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("flagId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);
	}

	/**
	 * Initializes the announcements flag persistence.
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
				_SQL_SELECT_ANNOUNCEMENTSFLAG_WHERE,
				_SQL_COUNT_ANNOUNCEMENTSFLAG_WHERE,
				AnnouncementsFlagModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"announcementsFlag.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, AnnouncementsFlag::getCompanyId));

		_collectionPersistenceFinderByEntryId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByEntryId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"entryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByEntryId",
					new String[] {Long.class.getName()},
					new String[] {"entryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByEntryId",
					new String[] {Long.class.getName()},
					new String[] {"entryId"}, false),
				_SQL_SELECT_ANNOUNCEMENTSFLAG_WHERE,
				_SQL_COUNT_ANNOUNCEMENTSFLAG_WHERE,
				AnnouncementsFlagModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"announcementsFlag.", "entryId", FinderColumn.Type.LONG,
					"=", true, true, AnnouncementsFlag::getEntryId));

		_uniquePersistenceFinderByU_E_V = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByU_E_V",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName()
				},
				new String[] {"userId", "entryId", "value"}, 0, 0, false,
				AnnouncementsFlag::getUserId, AnnouncementsFlag::getEntryId,
				AnnouncementsFlag::getValue),
			_SQL_SELECT_ANNOUNCEMENTSFLAG_WHERE, "",
			new FinderColumn<>(
				"announcementsFlag.", "userId", FinderColumn.Type.LONG, "=",
				true, true, AnnouncementsFlag::getUserId),
			new FinderColumn<>(
				"announcementsFlag.", "entryId", FinderColumn.Type.LONG, "=",
				true, true, AnnouncementsFlag::getEntryId),
			new FinderColumn<>(
				"announcementsFlag.", "value", FinderColumn.Type.INTEGER, "=",
				true, true, AnnouncementsFlag::getValue));

		AnnouncementsFlagUtil.setPersistence(this);
	}

	public void destroy() {
		AnnouncementsFlagUtil.setPersistence(null);

		EntityCacheUtil.removeCache(AnnouncementsFlagImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		AnnouncementsFlagModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_ANNOUNCEMENTSFLAG =
		"SELECT announcementsFlag FROM AnnouncementsFlag announcementsFlag";

	private static final String _SQL_SELECT_ANNOUNCEMENTSFLAG_WHERE =
		"SELECT announcementsFlag FROM AnnouncementsFlag announcementsFlag WHERE ";

	private static final String _SQL_COUNT_ANNOUNCEMENTSFLAG_WHERE =
		"SELECT COUNT(announcementsFlag) FROM AnnouncementsFlag announcementsFlag WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No AnnouncementsFlag exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		AnnouncementsFlagPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-993122887