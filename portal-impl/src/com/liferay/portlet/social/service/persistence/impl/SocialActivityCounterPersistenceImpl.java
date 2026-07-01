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
import com.liferay.portlet.social.model.impl.SocialActivityCounterImpl;
import com.liferay.portlet.social.model.impl.SocialActivityCounterModelImpl;
import com.liferay.social.kernel.exception.NoSuchActivityCounterException;
import com.liferay.social.kernel.model.SocialActivityCounter;
import com.liferay.social.kernel.model.SocialActivityCounterTable;
import com.liferay.social.kernel.service.persistence.SocialActivityCounterPersistence;
import com.liferay.social.kernel.service.persistence.SocialActivityCounterUtil;

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
 * The persistence implementation for the social activity counter service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class SocialActivityCounterPersistenceImpl
	extends BasePersistenceImpl
		<SocialActivityCounter, NoSuchActivityCounterException>
	implements SocialActivityCounterPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>SocialActivityCounterUtil</code> to access the social activity counter persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		SocialActivityCounterImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<SocialActivityCounter, NoSuchActivityCounterException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the social activity counters where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SocialActivityCounterModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of social activity counters
	 * @param end the upper bound of the range of social activity counters (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social activity counters
	 */
	@Override
	public List<SocialActivityCounter> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<SocialActivityCounter> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first social activity counter in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity counter
	 * @throws NoSuchActivityCounterException if a matching social activity counter could not be found
	 */
	@Override
	public SocialActivityCounter findByGroupId_First(
			long groupId,
			OrderByComparator<SocialActivityCounter> orderByComparator)
		throws NoSuchActivityCounterException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId},
			orderByComparator);
	}

	/**
	 * Returns the first social activity counter in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity counter, or <code>null</code> if a matching social activity counter could not be found
	 */
	@Override
	public SocialActivityCounter fetchByGroupId_First(
		long groupId,
		OrderByComparator<SocialActivityCounter> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId},
			orderByComparator);
	}

	/**
	 * Removes all the social activity counters where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId});
	}

	/**
	 * Returns the number of social activity counters where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching social activity counters
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId});
	}

	private CollectionPersistenceFinder
		<SocialActivityCounter, NoSuchActivityCounterException>
			_collectionPersistenceFinderByC_C;

	/**
	 * Returns an ordered range of all the social activity counters where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SocialActivityCounterModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of social activity counters
	 * @param end the upper bound of the range of social activity counters (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social activity counters
	 */
	@Override
	public List<SocialActivityCounter> findByC_C(
		long classNameId, long classPK, int start, int end,
		OrderByComparator<SocialActivityCounter> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first social activity counter in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity counter
	 * @throws NoSuchActivityCounterException if a matching social activity counter could not be found
	 */
	@Override
	public SocialActivityCounter findByC_C_First(
			long classNameId, long classPK,
			OrderByComparator<SocialActivityCounter> orderByComparator)
		throws NoSuchActivityCounterException {

		return _collectionPersistenceFinderByC_C.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK}, orderByComparator);
	}

	/**
	 * Returns the first social activity counter in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity counter, or <code>null</code> if a matching social activity counter could not be found
	 */
	@Override
	public SocialActivityCounter fetchByC_C_First(
		long classNameId, long classPK,
		OrderByComparator<SocialActivityCounter> orderByComparator) {

		return _collectionPersistenceFinderByC_C.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK}, orderByComparator);
	}

	/**
	 * Removes all the social activity counters where classNameId = &#63; and classPK = &#63; from the database.
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
	 * Returns the number of social activity counters where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching social activity counters
	 */
	@Override
	public int countByC_C(long classNameId, long classPK) {
		return _collectionPersistenceFinderByC_C.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK});
	}

	private CollectionPersistenceFinder
		<SocialActivityCounter, NoSuchActivityCounterException>
			_collectionPersistenceFinderByG_C_C_O;

	/**
	 * Returns an ordered range of all the social activity counters where groupId = &#63; and classNameId = &#63; and classPK = &#63; and ownerType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SocialActivityCounterModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param ownerType the owner type
	 * @param start the lower bound of the range of social activity counters
	 * @param end the upper bound of the range of social activity counters (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social activity counters
	 */
	@Override
	public List<SocialActivityCounter> findByG_C_C_O(
		long groupId, long classNameId, long classPK, int ownerType, int start,
		int end, OrderByComparator<SocialActivityCounter> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C_C_O.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, classNameId, classPK, ownerType}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first social activity counter in the ordered set where groupId = &#63; and classNameId = &#63; and classPK = &#63; and ownerType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param ownerType the owner type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity counter
	 * @throws NoSuchActivityCounterException if a matching social activity counter could not be found
	 */
	@Override
	public SocialActivityCounter findByG_C_C_O_First(
			long groupId, long classNameId, long classPK, int ownerType,
			OrderByComparator<SocialActivityCounter> orderByComparator)
		throws NoSuchActivityCounterException {

		return _collectionPersistenceFinderByG_C_C_O.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, classNameId, classPK, ownerType},
			orderByComparator);
	}

	/**
	 * Returns the first social activity counter in the ordered set where groupId = &#63; and classNameId = &#63; and classPK = &#63; and ownerType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param ownerType the owner type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity counter, or <code>null</code> if a matching social activity counter could not be found
	 */
	@Override
	public SocialActivityCounter fetchByG_C_C_O_First(
		long groupId, long classNameId, long classPK, int ownerType,
		OrderByComparator<SocialActivityCounter> orderByComparator) {

		return _collectionPersistenceFinderByG_C_C_O.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, classNameId, classPK, ownerType},
			orderByComparator);
	}

	/**
	 * Removes all the social activity counters where groupId = &#63; and classNameId = &#63; and classPK = &#63; and ownerType = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param ownerType the owner type
	 */
	@Override
	public void removeByG_C_C_O(
		long groupId, long classNameId, long classPK, int ownerType) {

		_collectionPersistenceFinderByG_C_C_O.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, classNameId, classPK, ownerType});
	}

	/**
	 * Returns the number of social activity counters where groupId = &#63; and classNameId = &#63; and classPK = &#63; and ownerType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param ownerType the owner type
	 * @return the number of matching social activity counters
	 */
	@Override
	public int countByG_C_C_O(
		long groupId, long classNameId, long classPK, int ownerType) {

		return _collectionPersistenceFinderByG_C_C_O.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, classNameId, classPK, ownerType});
	}

	private UniquePersistenceFinder
		<SocialActivityCounter, NoSuchActivityCounterException>
			_uniquePersistenceFinderByG_C_C_N_O_S;

	/**
	 * Returns the social activity counter where groupId = &#63; and classNameId = &#63; and classPK = &#63; and name = &#63; and ownerType = &#63; and startPeriod = &#63; or throws a <code>NoSuchActivityCounterException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param name the name
	 * @param ownerType the owner type
	 * @param startPeriod the start period
	 * @return the matching social activity counter
	 * @throws NoSuchActivityCounterException if a matching social activity counter could not be found
	 */
	@Override
	public SocialActivityCounter findByG_C_C_N_O_S(
			long groupId, long classNameId, long classPK, String name,
			int ownerType, int startPeriod)
		throws NoSuchActivityCounterException {

		return _uniquePersistenceFinderByG_C_C_N_O_S.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				groupId, classNameId, classPK, name, ownerType, startPeriod
			});
	}

	/**
	 * Returns the social activity counter where groupId = &#63; and classNameId = &#63; and classPK = &#63; and name = &#63; and ownerType = &#63; and startPeriod = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param name the name
	 * @param ownerType the owner type
	 * @param startPeriod the start period
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching social activity counter, or <code>null</code> if a matching social activity counter could not be found
	 */
	@Override
	public SocialActivityCounter fetchByG_C_C_N_O_S(
		long groupId, long classNameId, long classPK, String name,
		int ownerType, int startPeriod, boolean useFinderCache) {

		return _uniquePersistenceFinderByG_C_C_N_O_S.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				groupId, classNameId, classPK, name, ownerType, startPeriod
			},
			useFinderCache);
	}

	/**
	 * Removes the social activity counter where groupId = &#63; and classNameId = &#63; and classPK = &#63; and name = &#63; and ownerType = &#63; and startPeriod = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param name the name
	 * @param ownerType the owner type
	 * @param startPeriod the start period
	 * @return the social activity counter that was removed
	 */
	@Override
	public SocialActivityCounter removeByG_C_C_N_O_S(
			long groupId, long classNameId, long classPK, String name,
			int ownerType, int startPeriod)
		throws NoSuchActivityCounterException {

		SocialActivityCounter socialActivityCounter = findByG_C_C_N_O_S(
			groupId, classNameId, classPK, name, ownerType, startPeriod);

		return remove(socialActivityCounter);
	}

	/**
	 * Returns the number of social activity counters where groupId = &#63; and classNameId = &#63; and classPK = &#63; and name = &#63; and ownerType = &#63; and startPeriod = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param name the name
	 * @param ownerType the owner type
	 * @param startPeriod the start period
	 * @return the number of matching social activity counters
	 */
	@Override
	public int countByG_C_C_N_O_S(
		long groupId, long classNameId, long classPK, String name,
		int ownerType, int startPeriod) {

		return _uniquePersistenceFinderByG_C_C_N_O_S.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				groupId, classNameId, classPK, name, ownerType, startPeriod
			});
	}

	private UniquePersistenceFinder
		<SocialActivityCounter, NoSuchActivityCounterException>
			_uniquePersistenceFinderByG_C_C_N_O_E;

	/**
	 * Returns the social activity counter where groupId = &#63; and classNameId = &#63; and classPK = &#63; and name = &#63; and ownerType = &#63; and endPeriod = &#63; or throws a <code>NoSuchActivityCounterException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param name the name
	 * @param ownerType the owner type
	 * @param endPeriod the end period
	 * @return the matching social activity counter
	 * @throws NoSuchActivityCounterException if a matching social activity counter could not be found
	 */
	@Override
	public SocialActivityCounter findByG_C_C_N_O_E(
			long groupId, long classNameId, long classPK, String name,
			int ownerType, int endPeriod)
		throws NoSuchActivityCounterException {

		return _uniquePersistenceFinderByG_C_C_N_O_E.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				groupId, classNameId, classPK, name, ownerType, endPeriod
			});
	}

	/**
	 * Returns the social activity counter where groupId = &#63; and classNameId = &#63; and classPK = &#63; and name = &#63; and ownerType = &#63; and endPeriod = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param name the name
	 * @param ownerType the owner type
	 * @param endPeriod the end period
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching social activity counter, or <code>null</code> if a matching social activity counter could not be found
	 */
	@Override
	public SocialActivityCounter fetchByG_C_C_N_O_E(
		long groupId, long classNameId, long classPK, String name,
		int ownerType, int endPeriod, boolean useFinderCache) {

		return _uniquePersistenceFinderByG_C_C_N_O_E.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				groupId, classNameId, classPK, name, ownerType, endPeriod
			},
			useFinderCache);
	}

	/**
	 * Removes the social activity counter where groupId = &#63; and classNameId = &#63; and classPK = &#63; and name = &#63; and ownerType = &#63; and endPeriod = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param name the name
	 * @param ownerType the owner type
	 * @param endPeriod the end period
	 * @return the social activity counter that was removed
	 */
	@Override
	public SocialActivityCounter removeByG_C_C_N_O_E(
			long groupId, long classNameId, long classPK, String name,
			int ownerType, int endPeriod)
		throws NoSuchActivityCounterException {

		SocialActivityCounter socialActivityCounter = findByG_C_C_N_O_E(
			groupId, classNameId, classPK, name, ownerType, endPeriod);

		return remove(socialActivityCounter);
	}

	/**
	 * Returns the number of social activity counters where groupId = &#63; and classNameId = &#63; and classPK = &#63; and name = &#63; and ownerType = &#63; and endPeriod = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param name the name
	 * @param ownerType the owner type
	 * @param endPeriod the end period
	 * @return the number of matching social activity counters
	 */
	@Override
	public int countByG_C_C_N_O_E(
		long groupId, long classNameId, long classPK, String name,
		int ownerType, int endPeriod) {

		return _uniquePersistenceFinderByG_C_C_N_O_E.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				groupId, classNameId, classPK, name, ownerType, endPeriod
			});
	}

	public SocialActivityCounterPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("active", "active_");

		setDBColumnNames(dbColumnNames);

		setModelClass(SocialActivityCounter.class);

		setModelImplClass(SocialActivityCounterImpl.class);
		setModelPKClass(long.class);

		setTable(SocialActivityCounterTable.INSTANCE);
	}

	/**
	 * Creates a new social activity counter with the primary key. Does not add the social activity counter to the database.
	 *
	 * @param activityCounterId the primary key for the new social activity counter
	 * @return the new social activity counter
	 */
	@Override
	public SocialActivityCounter create(long activityCounterId) {
		SocialActivityCounter socialActivityCounter =
			new SocialActivityCounterImpl();

		socialActivityCounter.setNew(true);
		socialActivityCounter.setPrimaryKey(activityCounterId);

		socialActivityCounter.setCompanyId(CompanyThreadLocal.getCompanyId());

		return socialActivityCounter;
	}

	/**
	 * Removes the social activity counter with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param activityCounterId the primary key of the social activity counter
	 * @return the social activity counter that was removed
	 * @throws NoSuchActivityCounterException if a social activity counter with the primary key could not be found
	 */
	@Override
	public SocialActivityCounter remove(long activityCounterId)
		throws NoSuchActivityCounterException {

		return remove((Serializable)activityCounterId);
	}

	@Override
	protected SocialActivityCounter removeImpl(
		SocialActivityCounter socialActivityCounter) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(socialActivityCounter)) {
				socialActivityCounter = (SocialActivityCounter)session.get(
					SocialActivityCounterImpl.class,
					socialActivityCounter.getPrimaryKeyObj());
			}

			if ((socialActivityCounter != null) &&
				CTPersistenceHelperUtil.isRemove(socialActivityCounter)) {

				session.delete(socialActivityCounter);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (socialActivityCounter != null) {
			clearCache(socialActivityCounter);
		}

		return socialActivityCounter;
	}

	@Override
	public SocialActivityCounter updateImpl(
		SocialActivityCounter socialActivityCounter) {

		boolean isNew = socialActivityCounter.isNew();

		if (!(socialActivityCounter instanceof
				SocialActivityCounterModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(socialActivityCounter.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					socialActivityCounter);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in socialActivityCounter proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom SocialActivityCounter implementation " +
					socialActivityCounter.getClass());
		}

		SocialActivityCounterModelImpl socialActivityCounterModelImpl =
			(SocialActivityCounterModelImpl)socialActivityCounter;

		Session session = null;

		try {
			session = openSession();

			if (CTPersistenceHelperUtil.isInsert(socialActivityCounter)) {
				if (!isNew) {
					session.evict(
						SocialActivityCounterImpl.class,
						socialActivityCounter.getPrimaryKeyObj());
				}

				session.save(socialActivityCounter);
			}
			else {
				socialActivityCounter = (SocialActivityCounter)session.merge(
					socialActivityCounter);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(socialActivityCounter, false);

		if (isNew) {
			socialActivityCounter.setNew(false);
		}

		socialActivityCounter.resetOriginalValues();

		return socialActivityCounter;
	}

	/**
	 * Returns the social activity counter with the primary key or throws a <code>NoSuchActivityCounterException</code> if it could not be found.
	 *
	 * @param activityCounterId the primary key of the social activity counter
	 * @return the social activity counter
	 * @throws NoSuchActivityCounterException if a social activity counter with the primary key could not be found
	 */
	@Override
	public SocialActivityCounter findByPrimaryKey(long activityCounterId)
		throws NoSuchActivityCounterException {

		return findByPrimaryKey((Serializable)activityCounterId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return CTPersistenceHelperUtil.getCTPersistenceHelper();
	}

	/**
	 * Returns the social activity counter with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param activityCounterId the primary key of the social activity counter
	 * @return the social activity counter, or <code>null</code> if a social activity counter with the primary key could not be found
	 */
	@Override
	public SocialActivityCounter fetchByPrimaryKey(long activityCounterId) {
		return fetchByPrimaryKey((Serializable)activityCounterId);
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
		return "activityCounterId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_SOCIALACTIVITYCOUNTER;
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
		return SocialActivityCounterModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "SocialActivityCounter";
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
		ctStrictColumnNames.add("classNameId");
		ctStrictColumnNames.add("classPK");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("ownerType");
		ctMergeColumnNames.add("currentValue");
		ctMergeColumnNames.add("totalValue");
		ctMergeColumnNames.add("graceValue");
		ctMergeColumnNames.add("startPeriod");
		ctMergeColumnNames.add("endPeriod");
		ctMergeColumnNames.add("active_");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("activityCounterId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(
			new String[] {
				"groupId", "classNameId", "classPK", "name", "ownerType",
				"startPeriod"
			});

		_uniqueIndexColumnNames.add(
			new String[] {
				"groupId", "classNameId", "classPK", "name", "ownerType",
				"endPeriod"
			});
	}

	/**
	 * Initializes the social activity counter persistence.
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
				_SQL_SELECT_SOCIALACTIVITYCOUNTER_WHERE,
				_SQL_COUNT_SOCIALACTIVITYCOUNTER_WHERE,
				SocialActivityCounterModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"socialActivityCounter.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, SocialActivityCounter::getGroupId));

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
			_SQL_SELECT_SOCIALACTIVITYCOUNTER_WHERE,
			_SQL_COUNT_SOCIALACTIVITYCOUNTER_WHERE,
			SocialActivityCounterModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "", null,
			new FinderColumn<>(
				"socialActivityCounter.", "classNameId", FinderColumn.Type.LONG,
				"=", true, true, SocialActivityCounter::getClassNameId),
			new FinderColumn<>(
				"socialActivityCounter.", "classPK", FinderColumn.Type.LONG,
				"=", true, true, SocialActivityCounter::getClassPK));

		_collectionPersistenceFinderByG_C_C_O =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C_C_O",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "classNameId", "classPK", "ownerType"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C_C_O",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {
						"groupId", "classNameId", "classPK", "ownerType"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C_C_O",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {
						"groupId", "classNameId", "classPK", "ownerType"
					},
					false),
				_SQL_SELECT_SOCIALACTIVITYCOUNTER_WHERE,
				_SQL_COUNT_SOCIALACTIVITYCOUNTER_WHERE,
				SocialActivityCounterModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "socialActivityCounter.endPeriod = -1",
				"socialActivityCounter.endPeriod = -1", null,
				new FinderColumn<>(
					"socialActivityCounter.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, SocialActivityCounter::getGroupId),
				new FinderColumn<>(
					"socialActivityCounter.", "classNameId",
					FinderColumn.Type.LONG, "=", true, true,
					SocialActivityCounter::getClassNameId),
				new FinderColumn<>(
					"socialActivityCounter.", "classPK", FinderColumn.Type.LONG,
					"=", true, true, SocialActivityCounter::getClassPK),
				new FinderColumn<>(
					"socialActivityCounter.", "ownerType",
					FinderColumn.Type.INTEGER, "=", true, true,
					SocialActivityCounter::getOwnerType));

		_uniquePersistenceFinderByG_C_C_N_O_S = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_C_C_N_O_S",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName(), String.class.getName(),
					Integer.class.getName(), Integer.class.getName()
				},
				new String[] {
					"groupId", "classNameId", "classPK", "name", "ownerType",
					"startPeriod"
				},
				0, 8, false, SocialActivityCounter::getGroupId,
				SocialActivityCounter::getClassNameId,
				SocialActivityCounter::getClassPK,
				convertNullFunction(SocialActivityCounter::getName),
				SocialActivityCounter::getOwnerType,
				SocialActivityCounter::getStartPeriod),
			_SQL_SELECT_SOCIALACTIVITYCOUNTER_WHERE, "",
			new FinderColumn<>(
				"socialActivityCounter.", "groupId", FinderColumn.Type.LONG,
				"=", true, true, SocialActivityCounter::getGroupId),
			new FinderColumn<>(
				"socialActivityCounter.", "classNameId", FinderColumn.Type.LONG,
				"=", true, true, SocialActivityCounter::getClassNameId),
			new FinderColumn<>(
				"socialActivityCounter.", "classPK", FinderColumn.Type.LONG,
				"=", true, true, SocialActivityCounter::getClassPK),
			new FinderColumn<>(
				"socialActivityCounter.", "name", FinderColumn.Type.STRING, "=",
				true, true, SocialActivityCounter::getName),
			new FinderColumn<>(
				"socialActivityCounter.", "ownerType",
				FinderColumn.Type.INTEGER, "=", true, true,
				SocialActivityCounter::getOwnerType),
			new FinderColumn<>(
				"socialActivityCounter.", "startPeriod",
				FinderColumn.Type.INTEGER, "=", true, true,
				SocialActivityCounter::getStartPeriod));

		_uniquePersistenceFinderByG_C_C_N_O_E = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_C_C_N_O_E",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName(), String.class.getName(),
					Integer.class.getName(), Integer.class.getName()
				},
				new String[] {
					"groupId", "classNameId", "classPK", "name", "ownerType",
					"endPeriod"
				},
				0, 8, false, SocialActivityCounter::getGroupId,
				SocialActivityCounter::getClassNameId,
				SocialActivityCounter::getClassPK,
				convertNullFunction(SocialActivityCounter::getName),
				SocialActivityCounter::getOwnerType,
				SocialActivityCounter::getEndPeriod),
			_SQL_SELECT_SOCIALACTIVITYCOUNTER_WHERE, "",
			new FinderColumn<>(
				"socialActivityCounter.", "groupId", FinderColumn.Type.LONG,
				"=", true, true, SocialActivityCounter::getGroupId),
			new FinderColumn<>(
				"socialActivityCounter.", "classNameId", FinderColumn.Type.LONG,
				"=", true, true, SocialActivityCounter::getClassNameId),
			new FinderColumn<>(
				"socialActivityCounter.", "classPK", FinderColumn.Type.LONG,
				"=", true, true, SocialActivityCounter::getClassPK),
			new FinderColumn<>(
				"socialActivityCounter.", "name", FinderColumn.Type.STRING, "=",
				true, true, SocialActivityCounter::getName),
			new FinderColumn<>(
				"socialActivityCounter.", "ownerType",
				FinderColumn.Type.INTEGER, "=", true, true,
				SocialActivityCounter::getOwnerType),
			new FinderColumn<>(
				"socialActivityCounter.", "endPeriod",
				FinderColumn.Type.INTEGER, "=", true, true,
				SocialActivityCounter::getEndPeriod));

		SocialActivityCounterUtil.setPersistence(this);
	}

	public void destroy() {
		SocialActivityCounterUtil.setPersistence(null);

		EntityCacheUtil.removeCache(SocialActivityCounterImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		SocialActivityCounterModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_SOCIALACTIVITYCOUNTER =
		"SELECT socialActivityCounter FROM SocialActivityCounter socialActivityCounter";

	private static final String _SQL_SELECT_SOCIALACTIVITYCOUNTER_WHERE =
		"SELECT socialActivityCounter FROM SocialActivityCounter socialActivityCounter WHERE ";

	private static final String _SQL_COUNT_SOCIALACTIVITYCOUNTER_WHERE =
		"SELECT COUNT(socialActivityCounter) FROM SocialActivityCounter socialActivityCounter WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"active"});

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-485559841