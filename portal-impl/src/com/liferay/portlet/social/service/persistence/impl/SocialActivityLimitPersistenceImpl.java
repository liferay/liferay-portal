/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.social.service.persistence.impl;

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
import com.liferay.portlet.social.model.impl.SocialActivityLimitImpl;
import com.liferay.portlet.social.model.impl.SocialActivityLimitModelImpl;
import com.liferay.social.kernel.exception.NoSuchActivityLimitException;
import com.liferay.social.kernel.model.SocialActivityLimit;
import com.liferay.social.kernel.model.SocialActivityLimitTable;
import com.liferay.social.kernel.service.persistence.SocialActivityLimitPersistence;
import com.liferay.social.kernel.service.persistence.SocialActivityLimitUtil;

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
 * The persistence implementation for the social activity limit service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class SocialActivityLimitPersistenceImpl
	extends BasePersistenceImpl
		<SocialActivityLimit, NoSuchActivityLimitException>
	implements SocialActivityLimitPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>SocialActivityLimitUtil</code> to access the social activity limit persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		SocialActivityLimitImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<SocialActivityLimit, NoSuchActivityLimitException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the social activity limits where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SocialActivityLimitModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of social activity limits
	 * @param end the upper bound of the range of social activity limits (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social activity limits
	 */
	@Override
	public List<SocialActivityLimit> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<SocialActivityLimit> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first social activity limit in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity limit
	 * @throws NoSuchActivityLimitException if a matching social activity limit could not be found
	 */
	@Override
	public SocialActivityLimit findByGroupId_First(
			long groupId,
			OrderByComparator<SocialActivityLimit> orderByComparator)
		throws NoSuchActivityLimitException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId},
			orderByComparator);
	}

	/**
	 * Returns the first social activity limit in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity limit, or <code>null</code> if a matching social activity limit could not be found
	 */
	@Override
	public SocialActivityLimit fetchByGroupId_First(
		long groupId,
		OrderByComparator<SocialActivityLimit> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId},
			orderByComparator);
	}

	/**
	 * Removes all the social activity limits where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId});
	}

	/**
	 * Returns the number of social activity limits where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching social activity limits
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId});
	}

	private CollectionPersistenceFinder
		<SocialActivityLimit, NoSuchActivityLimitException>
			_collectionPersistenceFinderByUserId;

	/**
	 * Returns an ordered range of all the social activity limits where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SocialActivityLimitModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of social activity limits
	 * @param end the upper bound of the range of social activity limits (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social activity limits
	 */
	@Override
	public List<SocialActivityLimit> findByUserId(
		long userId, int start, int end,
		OrderByComparator<SocialActivityLimit> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUserId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {userId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first social activity limit in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity limit
	 * @throws NoSuchActivityLimitException if a matching social activity limit could not be found
	 */
	@Override
	public SocialActivityLimit findByUserId_First(
			long userId,
			OrderByComparator<SocialActivityLimit> orderByComparator)
		throws NoSuchActivityLimitException {

		return _collectionPersistenceFinderByUserId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {userId},
			orderByComparator);
	}

	/**
	 * Returns the first social activity limit in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity limit, or <code>null</code> if a matching social activity limit could not be found
	 */
	@Override
	public SocialActivityLimit fetchByUserId_First(
		long userId, OrderByComparator<SocialActivityLimit> orderByComparator) {

		return _collectionPersistenceFinderByUserId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {userId},
			orderByComparator);
	}

	/**
	 * Removes all the social activity limits where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	@Override
	public void removeByUserId(long userId) {
		_collectionPersistenceFinderByUserId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {userId});
	}

	/**
	 * Returns the number of social activity limits where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching social activity limits
	 */
	@Override
	public int countByUserId(long userId) {
		return _collectionPersistenceFinderByUserId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {userId});
	}

	private CollectionPersistenceFinder
		<SocialActivityLimit, NoSuchActivityLimitException>
			_collectionPersistenceFinderByC_C;

	/**
	 * Returns an ordered range of all the social activity limits where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SocialActivityLimitModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of social activity limits
	 * @param end the upper bound of the range of social activity limits (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social activity limits
	 */
	@Override
	public List<SocialActivityLimit> findByC_C(
		long classNameId, long classPK, int start, int end,
		OrderByComparator<SocialActivityLimit> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first social activity limit in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity limit
	 * @throws NoSuchActivityLimitException if a matching social activity limit could not be found
	 */
	@Override
	public SocialActivityLimit findByC_C_First(
			long classNameId, long classPK,
			OrderByComparator<SocialActivityLimit> orderByComparator)
		throws NoSuchActivityLimitException {

		return _collectionPersistenceFinderByC_C.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK}, orderByComparator);
	}

	/**
	 * Returns the first social activity limit in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity limit, or <code>null</code> if a matching social activity limit could not be found
	 */
	@Override
	public SocialActivityLimit fetchByC_C_First(
		long classNameId, long classPK,
		OrderByComparator<SocialActivityLimit> orderByComparator) {

		return _collectionPersistenceFinderByC_C.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK}, orderByComparator);
	}

	/**
	 * Removes all the social activity limits where classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	@Override
	public void removeByC_C(long classNameId, long classPK) {
		_collectionPersistenceFinderByC_C.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK});
	}

	/**
	 * Returns the number of social activity limits where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching social activity limits
	 */
	@Override
	public int countByC_C(long classNameId, long classPK) {
		return _collectionPersistenceFinderByC_C.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK});
	}

	private UniquePersistenceFinder
		<SocialActivityLimit, NoSuchActivityLimitException>
			_uniquePersistenceFinderByG_U_C_C_A_A;

	/**
	 * Returns the social activity limit where groupId = &#63; and userId = &#63; and classNameId = &#63; and classPK = &#63; and activityType = &#63; and activityCounterName = &#63; or throws a <code>NoSuchActivityLimitException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param activityType the activity type
	 * @param activityCounterName the activity counter name
	 * @return the matching social activity limit
	 * @throws NoSuchActivityLimitException if a matching social activity limit could not be found
	 */
	@Override
	public SocialActivityLimit findByG_U_C_C_A_A(
			long groupId, long userId, long classNameId, long classPK,
			int activityType, String activityCounterName)
		throws NoSuchActivityLimitException {

		return _uniquePersistenceFinderByG_U_C_C_A_A.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				groupId, userId, classNameId, classPK, activityType,
				activityCounterName
			});
	}

	/**
	 * Returns the social activity limit where groupId = &#63; and userId = &#63; and classNameId = &#63; and classPK = &#63; and activityType = &#63; and activityCounterName = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param activityType the activity type
	 * @param activityCounterName the activity counter name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching social activity limit, or <code>null</code> if a matching social activity limit could not be found
	 */
	@Override
	public SocialActivityLimit fetchByG_U_C_C_A_A(
		long groupId, long userId, long classNameId, long classPK,
		int activityType, String activityCounterName, boolean useFinderCache) {

		return _uniquePersistenceFinderByG_U_C_C_A_A.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				groupId, userId, classNameId, classPK, activityType,
				activityCounterName
			},
			useFinderCache);
	}

	/**
	 * Removes the social activity limit where groupId = &#63; and userId = &#63; and classNameId = &#63; and classPK = &#63; and activityType = &#63; and activityCounterName = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param activityType the activity type
	 * @param activityCounterName the activity counter name
	 * @return the social activity limit that was removed
	 */
	@Override
	public SocialActivityLimit removeByG_U_C_C_A_A(
			long groupId, long userId, long classNameId, long classPK,
			int activityType, String activityCounterName)
		throws NoSuchActivityLimitException {

		SocialActivityLimit socialActivityLimit = findByG_U_C_C_A_A(
			groupId, userId, classNameId, classPK, activityType,
			activityCounterName);

		return remove(socialActivityLimit);
	}

	/**
	 * Returns the number of social activity limits where groupId = &#63; and userId = &#63; and classNameId = &#63; and classPK = &#63; and activityType = &#63; and activityCounterName = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param activityType the activity type
	 * @param activityCounterName the activity counter name
	 * @return the number of matching social activity limits
	 */
	@Override
	public int countByG_U_C_C_A_A(
		long groupId, long userId, long classNameId, long classPK,
		int activityType, String activityCounterName) {

		return _uniquePersistenceFinderByG_U_C_C_A_A.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				groupId, userId, classNameId, classPK, activityType,
				activityCounterName
			});
	}

	public SocialActivityLimitPersistenceImpl() {
		setModelClass(SocialActivityLimit.class);

		setModelImplClass(SocialActivityLimitImpl.class);
		setModelPKClass(long.class);

		setTable(SocialActivityLimitTable.INSTANCE);
	}

	/**
	 * Creates a new social activity limit with the primary key. Does not add the social activity limit to the database.
	 *
	 * @param activityLimitId the primary key for the new social activity limit
	 * @return the new social activity limit
	 */
	@Override
	public SocialActivityLimit create(long activityLimitId) {
		SocialActivityLimit socialActivityLimit = new SocialActivityLimitImpl();

		socialActivityLimit.setNew(true);
		socialActivityLimit.setPrimaryKey(activityLimitId);

		socialActivityLimit.setCompanyId(CompanyThreadLocal.getCompanyId());

		return socialActivityLimit;
	}

	/**
	 * Removes the social activity limit with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param activityLimitId the primary key of the social activity limit
	 * @return the social activity limit that was removed
	 * @throws NoSuchActivityLimitException if a social activity limit with the primary key could not be found
	 */
	@Override
	public SocialActivityLimit remove(long activityLimitId)
		throws NoSuchActivityLimitException {

		return remove((Serializable)activityLimitId);
	}

	@Override
	protected SocialActivityLimit removeImpl(
		SocialActivityLimit socialActivityLimit) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(socialActivityLimit)) {
				socialActivityLimit = (SocialActivityLimit)session.get(
					SocialActivityLimitImpl.class,
					socialActivityLimit.getPrimaryKeyObj());
			}

			if ((socialActivityLimit != null) &&
				CTPersistenceHelperUtil.isRemove(socialActivityLimit)) {

				session.delete(socialActivityLimit);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (socialActivityLimit != null) {
			clearCache(socialActivityLimit);
		}

		return socialActivityLimit;
	}

	@Override
	public SocialActivityLimit updateImpl(
		SocialActivityLimit socialActivityLimit) {

		boolean isNew = socialActivityLimit.isNew();

		if (!(socialActivityLimit instanceof SocialActivityLimitModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(socialActivityLimit.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					socialActivityLimit);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in socialActivityLimit proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom SocialActivityLimit implementation " +
					socialActivityLimit.getClass());
		}

		SocialActivityLimitModelImpl socialActivityLimitModelImpl =
			(SocialActivityLimitModelImpl)socialActivityLimit;

		Session session = null;

		try {
			session = openSession();

			if (CTPersistenceHelperUtil.isInsert(socialActivityLimit)) {
				if (!isNew) {
					session.evict(
						SocialActivityLimitImpl.class,
						socialActivityLimit.getPrimaryKeyObj());
				}

				session.save(socialActivityLimit);
			}
			else {
				socialActivityLimit = (SocialActivityLimit)session.merge(
					socialActivityLimit);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(socialActivityLimit, false);

		if (isNew) {
			socialActivityLimit.setNew(false);
		}

		socialActivityLimit.resetOriginalValues();

		return socialActivityLimit;
	}

	/**
	 * Returns the social activity limit with the primary key or throws a <code>NoSuchActivityLimitException</code> if it could not be found.
	 *
	 * @param activityLimitId the primary key of the social activity limit
	 * @return the social activity limit
	 * @throws NoSuchActivityLimitException if a social activity limit with the primary key could not be found
	 */
	@Override
	public SocialActivityLimit findByPrimaryKey(long activityLimitId)
		throws NoSuchActivityLimitException {

		return findByPrimaryKey((Serializable)activityLimitId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return CTPersistenceHelperUtil.getCTPersistenceHelper();
	}

	/**
	 * Returns the social activity limit with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param activityLimitId the primary key of the social activity limit
	 * @return the social activity limit, or <code>null</code> if a social activity limit with the primary key could not be found
	 */
	@Override
	public SocialActivityLimit fetchByPrimaryKey(long activityLimitId) {
		return fetchByPrimaryKey((Serializable)activityLimitId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return EntityCacheUtil.getEntityCache();
	}

	@Override
	protected String getPKDBName() {
		return "activityLimitId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_SOCIALACTIVITYLIMIT;
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
		return SocialActivityLimitModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "SocialActivityLimit";
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
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("classNameId");
		ctStrictColumnNames.add("classPK");
		ctMergeColumnNames.add("activityType");
		ctMergeColumnNames.add("activityCounterName");
		ctMergeColumnNames.add("value");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("activityLimitId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(
			new String[] {
				"groupId", "userId", "classNameId", "classPK", "activityType",
				"activityCounterName"
			});
	}

	/**
	 * Initializes the social activity limit persistence.
	 */
	public void afterPropertiesSet() {
		_collectionPersistenceFinderByGroupId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByGroupId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByGroupId",
					new String[] {Long.class.getName()},
					new String[] {"groupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByGroupId",
					new String[] {Long.class.getName()},
					new String[] {"groupId"}, false),
				_SQL_SELECT_SOCIALACTIVITYLIMIT_WHERE,
				_SQL_COUNT_SOCIALACTIVITYLIMIT_WHERE,
				SocialActivityLimitModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"socialActivityLimit.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, SocialActivityLimit::getGroupId));

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
				_SQL_SELECT_SOCIALACTIVITYLIMIT_WHERE,
				_SQL_COUNT_SOCIALACTIVITYLIMIT_WHERE,
				SocialActivityLimitModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"socialActivityLimit.", "userId", FinderColumn.Type.LONG,
					"=", true, true, SocialActivityLimit::getUserId));

		_collectionPersistenceFinderByC_C = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"classNameId", "classPK"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"classNameId", "classPK"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"classNameId", "classPK"}, false),
			_SQL_SELECT_SOCIALACTIVITYLIMIT_WHERE,
			_SQL_COUNT_SOCIALACTIVITYLIMIT_WHERE,
			SocialActivityLimitModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "",
			new FinderColumn<>(
				"socialActivityLimit.", "classNameId", FinderColumn.Type.LONG,
				"=", true, true, SocialActivityLimit::getClassNameId),
			new FinderColumn<>(
				"socialActivityLimit.", "classPK", FinderColumn.Type.LONG, "=",
				true, true, SocialActivityLimit::getClassPK));

		_uniquePersistenceFinderByG_U_C_C_A_A = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_U_C_C_A_A",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), String.class.getName()
				},
				new String[] {
					"groupId", "userId", "classNameId", "classPK",
					"activityType", "activityCounterName"
				},
				0, 32, false, SocialActivityLimit::getGroupId,
				SocialActivityLimit::getUserId,
				SocialActivityLimit::getClassNameId,
				SocialActivityLimit::getClassPK,
				SocialActivityLimit::getActivityType,
				convertNullFunction(
					SocialActivityLimit::getActivityCounterName)),
			_SQL_SELECT_SOCIALACTIVITYLIMIT_WHERE, "",
			new FinderColumn<>(
				"socialActivityLimit.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, SocialActivityLimit::getGroupId),
			new FinderColumn<>(
				"socialActivityLimit.", "userId", FinderColumn.Type.LONG, "=",
				true, true, SocialActivityLimit::getUserId),
			new FinderColumn<>(
				"socialActivityLimit.", "classNameId", FinderColumn.Type.LONG,
				"=", true, true, SocialActivityLimit::getClassNameId),
			new FinderColumn<>(
				"socialActivityLimit.", "classPK", FinderColumn.Type.LONG, "=",
				true, true, SocialActivityLimit::getClassPK),
			new FinderColumn<>(
				"socialActivityLimit.", "activityType",
				FinderColumn.Type.INTEGER, "=", true, true,
				SocialActivityLimit::getActivityType),
			new FinderColumn<>(
				"socialActivityLimit.", "activityCounterName",
				FinderColumn.Type.STRING, "=", true, true,
				SocialActivityLimit::getActivityCounterName));

		SocialActivityLimitUtil.setPersistence(this);
	}

	public void destroy() {
		SocialActivityLimitUtil.setPersistence(null);

		EntityCacheUtil.removeCache(SocialActivityLimitImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		SocialActivityLimitModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_SOCIALACTIVITYLIMIT =
		"SELECT socialActivityLimit FROM SocialActivityLimit socialActivityLimit";

	private static final String _SQL_SELECT_SOCIALACTIVITYLIMIT_WHERE =
		"SELECT socialActivityLimit FROM SocialActivityLimit socialActivityLimit WHERE ";

	private static final String _SQL_COUNT_SOCIALACTIVITYLIMIT_WHERE =
		"SELECT COUNT(socialActivityLimit) FROM SocialActivityLimit socialActivityLimit WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No SocialActivityLimit exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		SocialActivityLimitPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1554192687