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
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portlet.social.model.impl.SocialActivitySetImpl;
import com.liferay.portlet.social.model.impl.SocialActivitySetModelImpl;
import com.liferay.social.kernel.exception.NoSuchActivitySetException;
import com.liferay.social.kernel.model.SocialActivitySet;
import com.liferay.social.kernel.model.SocialActivitySetTable;
import com.liferay.social.kernel.service.persistence.SocialActivitySetPersistence;
import com.liferay.social.kernel.service.persistence.SocialActivitySetUtil;

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
 * The persistence implementation for the social activity set service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class SocialActivitySetPersistenceImpl
	extends BasePersistenceImpl<SocialActivitySet, NoSuchActivitySetException>
	implements SocialActivitySetPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>SocialActivitySetUtil</code> to access the social activity set persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		SocialActivitySetImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<SocialActivitySet, NoSuchActivitySetException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the social activity sets where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SocialActivitySetModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of social activity sets
	 * @param end the upper bound of the range of social activity sets (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social activity sets
	 */
	@Override
	public List<SocialActivitySet> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<SocialActivitySet> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first social activity set in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity set
	 * @throws NoSuchActivitySetException if a matching social activity set could not be found
	 */
	@Override
	public SocialActivitySet findByGroupId_First(
			long groupId,
			OrderByComparator<SocialActivitySet> orderByComparator)
		throws NoSuchActivitySetException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId},
			orderByComparator);
	}

	/**
	 * Returns the first social activity set in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity set, or <code>null</code> if a matching social activity set could not be found
	 */
	@Override
	public SocialActivitySet fetchByGroupId_First(
		long groupId, OrderByComparator<SocialActivitySet> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId},
			orderByComparator);
	}

	/**
	 * Removes all the social activity sets where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId});
	}

	/**
	 * Returns the number of social activity sets where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching social activity sets
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId});
	}

	private CollectionPersistenceFinder
		<SocialActivitySet, NoSuchActivitySetException>
			_collectionPersistenceFinderByUserId;

	/**
	 * Returns an ordered range of all the social activity sets where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SocialActivitySetModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of social activity sets
	 * @param end the upper bound of the range of social activity sets (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social activity sets
	 */
	@Override
	public List<SocialActivitySet> findByUserId(
		long userId, int start, int end,
		OrderByComparator<SocialActivitySet> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUserId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {userId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first social activity set in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity set
	 * @throws NoSuchActivitySetException if a matching social activity set could not be found
	 */
	@Override
	public SocialActivitySet findByUserId_First(
			long userId, OrderByComparator<SocialActivitySet> orderByComparator)
		throws NoSuchActivitySetException {

		return _collectionPersistenceFinderByUserId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {userId},
			orderByComparator);
	}

	/**
	 * Returns the first social activity set in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity set, or <code>null</code> if a matching social activity set could not be found
	 */
	@Override
	public SocialActivitySet fetchByUserId_First(
		long userId, OrderByComparator<SocialActivitySet> orderByComparator) {

		return _collectionPersistenceFinderByUserId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {userId},
			orderByComparator);
	}

	/**
	 * Removes all the social activity sets where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	@Override
	public void removeByUserId(long userId) {
		_collectionPersistenceFinderByUserId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {userId});
	}

	/**
	 * Returns the number of social activity sets where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching social activity sets
	 */
	@Override
	public int countByUserId(long userId) {
		return _collectionPersistenceFinderByUserId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {userId});
	}

	private CollectionPersistenceFinder
		<SocialActivitySet, NoSuchActivitySetException>
			_collectionPersistenceFinderByG_U_T;

	/**
	 * Returns an ordered range of all the social activity sets where groupId = &#63; and userId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SocialActivitySetModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param type the type
	 * @param start the lower bound of the range of social activity sets
	 * @param end the upper bound of the range of social activity sets (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social activity sets
	 */
	@Override
	public List<SocialActivitySet> findByG_U_T(
		long groupId, long userId, int type, int start, int end,
		OrderByComparator<SocialActivitySet> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_U_T.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, userId, type}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first social activity set in the ordered set where groupId = &#63; and userId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity set
	 * @throws NoSuchActivitySetException if a matching social activity set could not be found
	 */
	@Override
	public SocialActivitySet findByG_U_T_First(
			long groupId, long userId, int type,
			OrderByComparator<SocialActivitySet> orderByComparator)
		throws NoSuchActivitySetException {

		return _collectionPersistenceFinderByG_U_T.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, userId, type}, orderByComparator);
	}

	/**
	 * Returns the first social activity set in the ordered set where groupId = &#63; and userId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity set, or <code>null</code> if a matching social activity set could not be found
	 */
	@Override
	public SocialActivitySet fetchByG_U_T_First(
		long groupId, long userId, int type,
		OrderByComparator<SocialActivitySet> orderByComparator) {

		return _collectionPersistenceFinderByG_U_T.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, userId, type}, orderByComparator);
	}

	/**
	 * Removes all the social activity sets where groupId = &#63; and userId = &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param type the type
	 */
	@Override
	public void removeByG_U_T(long groupId, long userId, int type) {
		_collectionPersistenceFinderByG_U_T.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, userId, type});
	}

	/**
	 * Returns the number of social activity sets where groupId = &#63; and userId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param type the type
	 * @return the number of matching social activity sets
	 */
	@Override
	public int countByG_U_T(long groupId, long userId, int type) {
		return _collectionPersistenceFinderByG_U_T.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, userId, type});
	}

	private CollectionPersistenceFinder
		<SocialActivitySet, NoSuchActivitySetException>
			_collectionPersistenceFinderByC_C_T;

	/**
	 * Returns an ordered range of all the social activity sets where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SocialActivitySetModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param start the lower bound of the range of social activity sets
	 * @param end the upper bound of the range of social activity sets (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social activity sets
	 */
	@Override
	public List<SocialActivitySet> findByC_C_T(
		long classNameId, long classPK, int type, int start, int end,
		OrderByComparator<SocialActivitySet> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C_T.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK, type}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first social activity set in the ordered set where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity set
	 * @throws NoSuchActivitySetException if a matching social activity set could not be found
	 */
	@Override
	public SocialActivitySet findByC_C_T_First(
			long classNameId, long classPK, int type,
			OrderByComparator<SocialActivitySet> orderByComparator)
		throws NoSuchActivitySetException {

		return _collectionPersistenceFinderByC_C_T.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK, type}, orderByComparator);
	}

	/**
	 * Returns the first social activity set in the ordered set where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity set, or <code>null</code> if a matching social activity set could not be found
	 */
	@Override
	public SocialActivitySet fetchByC_C_T_First(
		long classNameId, long classPK, int type,
		OrderByComparator<SocialActivitySet> orderByComparator) {

		return _collectionPersistenceFinderByC_C_T.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK, type}, orderByComparator);
	}

	/**
	 * Removes all the social activity sets where classNameId = &#63; and classPK = &#63; and type = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 */
	@Override
	public void removeByC_C_T(long classNameId, long classPK, int type) {
		_collectionPersistenceFinderByC_C_T.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK, type});
	}

	/**
	 * Returns the number of social activity sets where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @return the number of matching social activity sets
	 */
	@Override
	public int countByC_C_T(long classNameId, long classPK, int type) {
		return _collectionPersistenceFinderByC_C_T.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK, type});
	}

	private CollectionPersistenceFinder
		<SocialActivitySet, NoSuchActivitySetException>
			_collectionPersistenceFinderByG_U_C_T;

	/**
	 * Returns an ordered range of all the social activity sets where groupId = &#63; and userId = &#63; and classNameId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SocialActivitySetModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param type the type
	 * @param start the lower bound of the range of social activity sets
	 * @param end the upper bound of the range of social activity sets (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social activity sets
	 */
	@Override
	public List<SocialActivitySet> findByG_U_C_T(
		long groupId, long userId, long classNameId, int type, int start,
		int end, OrderByComparator<SocialActivitySet> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_U_C_T.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, userId, classNameId, type}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first social activity set in the ordered set where groupId = &#63; and userId = &#63; and classNameId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity set
	 * @throws NoSuchActivitySetException if a matching social activity set could not be found
	 */
	@Override
	public SocialActivitySet findByG_U_C_T_First(
			long groupId, long userId, long classNameId, int type,
			OrderByComparator<SocialActivitySet> orderByComparator)
		throws NoSuchActivitySetException {

		return _collectionPersistenceFinderByG_U_C_T.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, userId, classNameId, type},
			orderByComparator);
	}

	/**
	 * Returns the first social activity set in the ordered set where groupId = &#63; and userId = &#63; and classNameId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity set, or <code>null</code> if a matching social activity set could not be found
	 */
	@Override
	public SocialActivitySet fetchByG_U_C_T_First(
		long groupId, long userId, long classNameId, int type,
		OrderByComparator<SocialActivitySet> orderByComparator) {

		return _collectionPersistenceFinderByG_U_C_T.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, userId, classNameId, type},
			orderByComparator);
	}

	/**
	 * Removes all the social activity sets where groupId = &#63; and userId = &#63; and classNameId = &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param type the type
	 */
	@Override
	public void removeByG_U_C_T(
		long groupId, long userId, long classNameId, int type) {

		_collectionPersistenceFinderByG_U_C_T.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, userId, classNameId, type});
	}

	/**
	 * Returns the number of social activity sets where groupId = &#63; and userId = &#63; and classNameId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param type the type
	 * @return the number of matching social activity sets
	 */
	@Override
	public int countByG_U_C_T(
		long groupId, long userId, long classNameId, int type) {

		return _collectionPersistenceFinderByG_U_C_T.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, userId, classNameId, type});
	}

	private CollectionPersistenceFinder
		<SocialActivitySet, NoSuchActivitySetException>
			_collectionPersistenceFinderByU_C_C_T;

	/**
	 * Returns an ordered range of all the social activity sets where userId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SocialActivitySetModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param start the lower bound of the range of social activity sets
	 * @param end the upper bound of the range of social activity sets (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social activity sets
	 */
	@Override
	public List<SocialActivitySet> findByU_C_C_T(
		long userId, long classNameId, long classPK, int type, int start,
		int end, OrderByComparator<SocialActivitySet> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByU_C_C_T.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, classNameId, classPK, type}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first social activity set in the ordered set where userId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity set
	 * @throws NoSuchActivitySetException if a matching social activity set could not be found
	 */
	@Override
	public SocialActivitySet findByU_C_C_T_First(
			long userId, long classNameId, long classPK, int type,
			OrderByComparator<SocialActivitySet> orderByComparator)
		throws NoSuchActivitySetException {

		return _collectionPersistenceFinderByU_C_C_T.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, classNameId, classPK, type},
			orderByComparator);
	}

	/**
	 * Returns the first social activity set in the ordered set where userId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity set, or <code>null</code> if a matching social activity set could not be found
	 */
	@Override
	public SocialActivitySet fetchByU_C_C_T_First(
		long userId, long classNameId, long classPK, int type,
		OrderByComparator<SocialActivitySet> orderByComparator) {

		return _collectionPersistenceFinderByU_C_C_T.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, classNameId, classPK, type},
			orderByComparator);
	}

	/**
	 * Removes all the social activity sets where userId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 */
	@Override
	public void removeByU_C_C_T(
		long userId, long classNameId, long classPK, int type) {

		_collectionPersistenceFinderByU_C_C_T.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, classNameId, classPK, type});
	}

	/**
	 * Returns the number of social activity sets where userId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @return the number of matching social activity sets
	 */
	@Override
	public int countByU_C_C_T(
		long userId, long classNameId, long classPK, int type) {

		return _collectionPersistenceFinderByU_C_C_T.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, classNameId, classPK, type});
	}

	public SocialActivitySetPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(SocialActivitySet.class);

		setModelImplClass(SocialActivitySetImpl.class);
		setModelPKClass(long.class);

		setTable(SocialActivitySetTable.INSTANCE);
	}

	/**
	 * Creates a new social activity set with the primary key. Does not add the social activity set to the database.
	 *
	 * @param activitySetId the primary key for the new social activity set
	 * @return the new social activity set
	 */
	@Override
	public SocialActivitySet create(long activitySetId) {
		SocialActivitySet socialActivitySet = new SocialActivitySetImpl();

		socialActivitySet.setNew(true);
		socialActivitySet.setPrimaryKey(activitySetId);

		socialActivitySet.setCompanyId(CompanyThreadLocal.getCompanyId());

		return socialActivitySet;
	}

	/**
	 * Removes the social activity set with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param activitySetId the primary key of the social activity set
	 * @return the social activity set that was removed
	 * @throws NoSuchActivitySetException if a social activity set with the primary key could not be found
	 */
	@Override
	public SocialActivitySet remove(long activitySetId)
		throws NoSuchActivitySetException {

		return remove((Serializable)activitySetId);
	}

	@Override
	protected SocialActivitySet removeImpl(
		SocialActivitySet socialActivitySet) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(socialActivitySet)) {
				socialActivitySet = (SocialActivitySet)session.get(
					SocialActivitySetImpl.class,
					socialActivitySet.getPrimaryKeyObj());
			}

			if ((socialActivitySet != null) &&
				CTPersistenceHelperUtil.isRemove(socialActivitySet)) {

				session.delete(socialActivitySet);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (socialActivitySet != null) {
			clearCache(socialActivitySet);
		}

		return socialActivitySet;
	}

	@Override
	public SocialActivitySet updateImpl(SocialActivitySet socialActivitySet) {
		boolean isNew = socialActivitySet.isNew();

		if (!(socialActivitySet instanceof SocialActivitySetModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(socialActivitySet.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					socialActivitySet);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in socialActivitySet proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom SocialActivitySet implementation " +
					socialActivitySet.getClass());
		}

		SocialActivitySetModelImpl socialActivitySetModelImpl =
			(SocialActivitySetModelImpl)socialActivitySet;

		Session session = null;

		try {
			session = openSession();

			if (CTPersistenceHelperUtil.isInsert(socialActivitySet)) {
				if (!isNew) {
					session.evict(
						SocialActivitySetImpl.class,
						socialActivitySet.getPrimaryKeyObj());
				}

				session.save(socialActivitySet);
			}
			else {
				socialActivitySet = (SocialActivitySet)session.merge(
					socialActivitySet);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(socialActivitySet, false);

		if (isNew) {
			socialActivitySet.setNew(false);
		}

		socialActivitySet.resetOriginalValues();

		return socialActivitySet;
	}

	/**
	 * Returns the social activity set with the primary key or throws a <code>NoSuchActivitySetException</code> if it could not be found.
	 *
	 * @param activitySetId the primary key of the social activity set
	 * @return the social activity set
	 * @throws NoSuchActivitySetException if a social activity set with the primary key could not be found
	 */
	@Override
	public SocialActivitySet findByPrimaryKey(long activitySetId)
		throws NoSuchActivitySetException {

		return findByPrimaryKey((Serializable)activitySetId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return CTPersistenceHelperUtil.getCTPersistenceHelper();
	}

	/**
	 * Returns the social activity set with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param activitySetId the primary key of the social activity set
	 * @return the social activity set, or <code>null</code> if a social activity set with the primary key could not be found
	 */
	@Override
	public SocialActivitySet fetchByPrimaryKey(long activitySetId) {
		return fetchByPrimaryKey((Serializable)activitySetId);
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
		return "activitySetId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_SOCIALACTIVITYSET;
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
		return SocialActivitySetModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "SocialActivitySet";
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
		ctStrictColumnNames.add("createDate");
		ctMergeColumnNames.add("modifiedDate");
		ctStrictColumnNames.add("classNameId");
		ctStrictColumnNames.add("classPK");
		ctMergeColumnNames.add("type_");
		ctMergeColumnNames.add("extraData");
		ctMergeColumnNames.add("activityCount");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("activitySetId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);
	}

	/**
	 * Initializes the social activity set persistence.
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
				_SQL_SELECT_SOCIALACTIVITYSET_WHERE,
				_SQL_COUNT_SOCIALACTIVITYSET_WHERE,
				SocialActivitySetModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"socialActivitySet.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, SocialActivitySet::getGroupId));

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
				_SQL_SELECT_SOCIALACTIVITYSET_WHERE,
				_SQL_COUNT_SOCIALACTIVITYSET_WHERE,
				SocialActivitySetModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"socialActivitySet.", "userId", FinderColumn.Type.LONG, "=",
					true, true, SocialActivitySet::getUserId));

		_collectionPersistenceFinderByG_U_T = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_U_T",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"groupId", "userId", "type_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_U_T",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName()
				},
				new String[] {"groupId", "userId", "type_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_U_T",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName()
				},
				new String[] {"groupId", "userId", "type_"}, false),
			_SQL_SELECT_SOCIALACTIVITYSET_WHERE,
			_SQL_COUNT_SOCIALACTIVITYSET_WHERE,
			SocialActivitySetModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"socialActivitySet.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, SocialActivitySet::getGroupId),
			new FinderColumn<>(
				"socialActivitySet.", "userId", FinderColumn.Type.LONG, "=",
				true, true, SocialActivitySet::getUserId),
			new FinderColumn<>(
				"socialActivitySet.", "type", "type_",
				FinderColumn.Type.INTEGER, "=", true, true,
				SocialActivitySet::getType));

		_collectionPersistenceFinderByC_C_T = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C_T",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"classNameId", "classPK", "type_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C_T",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName()
				},
				new String[] {"classNameId", "classPK", "type_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C_T",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName()
				},
				new String[] {"classNameId", "classPK", "type_"}, false),
			_SQL_SELECT_SOCIALACTIVITYSET_WHERE,
			_SQL_COUNT_SOCIALACTIVITYSET_WHERE,
			SocialActivitySetModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"",
			new FinderColumn<>(
				"socialActivitySet.", "classNameId", FinderColumn.Type.LONG,
				"=", true, true, SocialActivitySet::getClassNameId),
			new FinderColumn<>(
				"socialActivitySet.", "classPK", FinderColumn.Type.LONG, "=",
				true, true, SocialActivitySet::getClassPK),
			new FinderColumn<>(
				"socialActivitySet.", "type", "type_",
				FinderColumn.Type.INTEGER, "=", true, true,
				SocialActivitySet::getType));

		_collectionPersistenceFinderByG_U_C_T =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_U_C_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "userId", "classNameId", "type_"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_U_C_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {"groupId", "userId", "classNameId", "type_"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_U_C_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {"groupId", "userId", "classNameId", "type_"},
					false),
				_SQL_SELECT_SOCIALACTIVITYSET_WHERE,
				_SQL_COUNT_SOCIALACTIVITYSET_WHERE,
				SocialActivitySetModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"socialActivitySet.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, SocialActivitySet::getGroupId),
				new FinderColumn<>(
					"socialActivitySet.", "userId", FinderColumn.Type.LONG, "=",
					true, true, SocialActivitySet::getUserId),
				new FinderColumn<>(
					"socialActivitySet.", "classNameId", FinderColumn.Type.LONG,
					"=", true, true, SocialActivitySet::getClassNameId),
				new FinderColumn<>(
					"socialActivitySet.", "type", "type_",
					FinderColumn.Type.INTEGER, "=", true, true,
					SocialActivitySet::getType));

		_collectionPersistenceFinderByU_C_C_T =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByU_C_C_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"userId", "classNameId", "classPK", "type_"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByU_C_C_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {"userId", "classNameId", "classPK", "type_"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByU_C_C_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {"userId", "classNameId", "classPK", "type_"},
					false),
				_SQL_SELECT_SOCIALACTIVITYSET_WHERE,
				_SQL_COUNT_SOCIALACTIVITYSET_WHERE,
				SocialActivitySetModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"socialActivitySet.", "userId", FinderColumn.Type.LONG, "=",
					true, true, SocialActivitySet::getUserId),
				new FinderColumn<>(
					"socialActivitySet.", "classNameId", FinderColumn.Type.LONG,
					"=", true, true, SocialActivitySet::getClassNameId),
				new FinderColumn<>(
					"socialActivitySet.", "classPK", FinderColumn.Type.LONG,
					"=", true, true, SocialActivitySet::getClassPK),
				new FinderColumn<>(
					"socialActivitySet.", "type", "type_",
					FinderColumn.Type.INTEGER, "=", true, true,
					SocialActivitySet::getType));

		SocialActivitySetUtil.setPersistence(this);
	}

	public void destroy() {
		SocialActivitySetUtil.setPersistence(null);

		EntityCacheUtil.removeCache(SocialActivitySetImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		SocialActivitySetModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_SOCIALACTIVITYSET =
		"SELECT socialActivitySet FROM SocialActivitySet socialActivitySet";

	private static final String _SQL_SELECT_SOCIALACTIVITYSET_WHERE =
		"SELECT socialActivitySet FROM SocialActivitySet socialActivitySet WHERE ";

	private static final String _SQL_COUNT_SOCIALACTIVITYSET_WHERE =
		"SELECT COUNT(socialActivitySet) FROM SocialActivitySet socialActivitySet WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No SocialActivitySet exists with the key {";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"type"});

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:688192428