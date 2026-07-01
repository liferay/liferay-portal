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
import com.liferay.portlet.social.model.impl.SocialActivityImpl;
import com.liferay.portlet.social.model.impl.SocialActivityModelImpl;
import com.liferay.social.kernel.exception.NoSuchActivityException;
import com.liferay.social.kernel.model.SocialActivity;
import com.liferay.social.kernel.model.SocialActivityTable;
import com.liferay.social.kernel.service.persistence.SocialActivityPersistence;
import com.liferay.social.kernel.service.persistence.SocialActivityUtil;

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
 * The persistence implementation for the social activity service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class SocialActivityPersistenceImpl
	extends BasePersistenceImpl<SocialActivity, NoSuchActivityException>
	implements SocialActivityPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>SocialActivityUtil</code> to access the social activity persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		SocialActivityImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<SocialActivity, NoSuchActivityException>
		_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the social activities where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social activities
	 */
	@Override
	public List<SocialActivity> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<SocialActivity> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first social activity in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity
	 * @throws NoSuchActivityException if a matching social activity could not be found
	 */
	@Override
	public SocialActivity findByGroupId_First(
			long groupId, OrderByComparator<SocialActivity> orderByComparator)
		throws NoSuchActivityException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId},
			orderByComparator);
	}

	/**
	 * Returns the first social activity in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity, or <code>null</code> if a matching social activity could not be found
	 */
	@Override
	public SocialActivity fetchByGroupId_First(
		long groupId, OrderByComparator<SocialActivity> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId},
			orderByComparator);
	}

	/**
	 * Removes all the social activities where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId});
	}

	/**
	 * Returns the number of social activities where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching social activities
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId});
	}

	private CollectionPersistenceFinder<SocialActivity, NoSuchActivityException>
		_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the social activities where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social activities
	 */
	@Override
	public List<SocialActivity> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<SocialActivity> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first social activity in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity
	 * @throws NoSuchActivityException if a matching social activity could not be found
	 */
	@Override
	public SocialActivity findByCompanyId_First(
			long companyId, OrderByComparator<SocialActivity> orderByComparator)
		throws NoSuchActivityException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			orderByComparator);
	}

	/**
	 * Returns the first social activity in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity, or <code>null</code> if a matching social activity could not be found
	 */
	@Override
	public SocialActivity fetchByCompanyId_First(
		long companyId, OrderByComparator<SocialActivity> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			orderByComparator);
	}

	/**
	 * Removes all the social activities where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId});
	}

	/**
	 * Returns the number of social activities where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching social activities
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId});
	}

	private CollectionPersistenceFinder<SocialActivity, NoSuchActivityException>
		_collectionPersistenceFinderByUserId;

	/**
	 * Returns an ordered range of all the social activities where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social activities
	 */
	@Override
	public List<SocialActivity> findByUserId(
		long userId, int start, int end,
		OrderByComparator<SocialActivity> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUserId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {userId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first social activity in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity
	 * @throws NoSuchActivityException if a matching social activity could not be found
	 */
	@Override
	public SocialActivity findByUserId_First(
			long userId, OrderByComparator<SocialActivity> orderByComparator)
		throws NoSuchActivityException {

		return _collectionPersistenceFinderByUserId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {userId},
			orderByComparator);
	}

	/**
	 * Returns the first social activity in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity, or <code>null</code> if a matching social activity could not be found
	 */
	@Override
	public SocialActivity fetchByUserId_First(
		long userId, OrderByComparator<SocialActivity> orderByComparator) {

		return _collectionPersistenceFinderByUserId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {userId},
			orderByComparator);
	}

	/**
	 * Removes all the social activities where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	@Override
	public void removeByUserId(long userId) {
		_collectionPersistenceFinderByUserId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {userId});
	}

	/**
	 * Returns the number of social activities where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching social activities
	 */
	@Override
	public int countByUserId(long userId) {
		return _collectionPersistenceFinderByUserId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {userId});
	}

	private CollectionPersistenceFinder<SocialActivity, NoSuchActivityException>
		_collectionPersistenceFinderByActivitySetId;

	/**
	 * Returns an ordered range of all the social activities where activitySetId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param activitySetId the activity set ID
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social activities
	 */
	@Override
	public List<SocialActivity> findByActivitySetId(
		long activitySetId, int start, int end,
		OrderByComparator<SocialActivity> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByActivitySetId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {activitySetId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first social activity in the ordered set where activitySetId = &#63;.
	 *
	 * @param activitySetId the activity set ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity
	 * @throws NoSuchActivityException if a matching social activity could not be found
	 */
	@Override
	public SocialActivity findByActivitySetId_First(
			long activitySetId,
			OrderByComparator<SocialActivity> orderByComparator)
		throws NoSuchActivityException {

		return _collectionPersistenceFinderByActivitySetId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {activitySetId},
			orderByComparator);
	}

	/**
	 * Returns the first social activity in the ordered set where activitySetId = &#63;.
	 *
	 * @param activitySetId the activity set ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity, or <code>null</code> if a matching social activity could not be found
	 */
	@Override
	public SocialActivity fetchByActivitySetId_First(
		long activitySetId,
		OrderByComparator<SocialActivity> orderByComparator) {

		return _collectionPersistenceFinderByActivitySetId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {activitySetId},
			orderByComparator);
	}

	/**
	 * Removes all the social activities where activitySetId = &#63; from the database.
	 *
	 * @param activitySetId the activity set ID
	 */
	@Override
	public void removeByActivitySetId(long activitySetId) {
		_collectionPersistenceFinderByActivitySetId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {activitySetId});
	}

	/**
	 * Returns the number of social activities where activitySetId = &#63;.
	 *
	 * @param activitySetId the activity set ID
	 * @return the number of matching social activities
	 */
	@Override
	public int countByActivitySetId(long activitySetId) {
		return _collectionPersistenceFinderByActivitySetId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {activitySetId});
	}

	private UniquePersistenceFinder<SocialActivity, NoSuchActivityException>
		_uniquePersistenceFinderByMirrorActivityId;

	/**
	 * Returns the social activity where mirrorActivityId = &#63; or throws a <code>NoSuchActivityException</code> if it could not be found.
	 *
	 * @param mirrorActivityId the mirror activity ID
	 * @return the matching social activity
	 * @throws NoSuchActivityException if a matching social activity could not be found
	 */
	@Override
	public SocialActivity findByMirrorActivityId(long mirrorActivityId)
		throws NoSuchActivityException {

		return _uniquePersistenceFinderByMirrorActivityId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {mirrorActivityId});
	}

	/**
	 * Returns the social activity where mirrorActivityId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param mirrorActivityId the mirror activity ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching social activity, or <code>null</code> if a matching social activity could not be found
	 */
	@Override
	public SocialActivity fetchByMirrorActivityId(
		long mirrorActivityId, boolean useFinderCache) {

		return _uniquePersistenceFinderByMirrorActivityId.fetch(
			FinderCacheUtil.getFinderCache(), new Object[] {mirrorActivityId},
			useFinderCache);
	}

	/**
	 * Removes the social activity where mirrorActivityId = &#63; from the database.
	 *
	 * @param mirrorActivityId the mirror activity ID
	 * @return the social activity that was removed
	 */
	@Override
	public SocialActivity removeByMirrorActivityId(long mirrorActivityId)
		throws NoSuchActivityException {

		SocialActivity socialActivity = findByMirrorActivityId(
			mirrorActivityId);

		return remove(socialActivity);
	}

	/**
	 * Returns the number of social activities where mirrorActivityId = &#63;.
	 *
	 * @param mirrorActivityId the mirror activity ID
	 * @return the number of matching social activities
	 */
	@Override
	public int countByMirrorActivityId(long mirrorActivityId) {
		return _uniquePersistenceFinderByMirrorActivityId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {mirrorActivityId});
	}

	private CollectionPersistenceFinder<SocialActivity, NoSuchActivityException>
		_collectionPersistenceFinderByReceiverUserId;

	/**
	 * Returns an ordered range of all the social activities where receiverUserId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param receiverUserId the receiver user ID
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social activities
	 */
	@Override
	public List<SocialActivity> findByReceiverUserId(
		long receiverUserId, int start, int end,
		OrderByComparator<SocialActivity> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByReceiverUserId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {receiverUserId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first social activity in the ordered set where receiverUserId = &#63;.
	 *
	 * @param receiverUserId the receiver user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity
	 * @throws NoSuchActivityException if a matching social activity could not be found
	 */
	@Override
	public SocialActivity findByReceiverUserId_First(
			long receiverUserId,
			OrderByComparator<SocialActivity> orderByComparator)
		throws NoSuchActivityException {

		return _collectionPersistenceFinderByReceiverUserId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {receiverUserId},
			orderByComparator);
	}

	/**
	 * Returns the first social activity in the ordered set where receiverUserId = &#63;.
	 *
	 * @param receiverUserId the receiver user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity, or <code>null</code> if a matching social activity could not be found
	 */
	@Override
	public SocialActivity fetchByReceiverUserId_First(
		long receiverUserId,
		OrderByComparator<SocialActivity> orderByComparator) {

		return _collectionPersistenceFinderByReceiverUserId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {receiverUserId},
			orderByComparator);
	}

	/**
	 * Removes all the social activities where receiverUserId = &#63; from the database.
	 *
	 * @param receiverUserId the receiver user ID
	 */
	@Override
	public void removeByReceiverUserId(long receiverUserId) {
		_collectionPersistenceFinderByReceiverUserId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {receiverUserId});
	}

	/**
	 * Returns the number of social activities where receiverUserId = &#63;.
	 *
	 * @param receiverUserId the receiver user ID
	 * @return the number of matching social activities
	 */
	@Override
	public int countByReceiverUserId(long receiverUserId) {
		return _collectionPersistenceFinderByReceiverUserId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {receiverUserId});
	}

	private CollectionPersistenceFinder<SocialActivity, NoSuchActivityException>
		_collectionPersistenceFinderByC_CN;

	/**
	 * Returns an ordered range of all the social activities where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social activities
	 */
	@Override
	public List<SocialActivity> findByC_CN(
		long companyId, long classNameId, int start, int end,
		OrderByComparator<SocialActivity> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_CN.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first social activity in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity
	 * @throws NoSuchActivityException if a matching social activity could not be found
	 */
	@Override
	public SocialActivity findByC_CN_First(
			long companyId, long classNameId,
			OrderByComparator<SocialActivity> orderByComparator)
		throws NoSuchActivityException {

		return _collectionPersistenceFinderByC_CN.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId}, orderByComparator);
	}

	/**
	 * Returns the first social activity in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity, or <code>null</code> if a matching social activity could not be found
	 */
	@Override
	public SocialActivity fetchByC_CN_First(
		long companyId, long classNameId,
		OrderByComparator<SocialActivity> orderByComparator) {

		return _collectionPersistenceFinderByC_CN.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId}, orderByComparator);
	}

	/**
	 * Removes all the social activities where companyId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 */
	@Override
	public void removeByC_CN(long companyId, long classNameId) {
		_collectionPersistenceFinderByC_CN.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId});
	}

	/**
	 * Returns the number of social activities where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the number of matching social activities
	 */
	@Override
	public int countByC_CN(long companyId, long classNameId) {
		return _collectionPersistenceFinderByC_CN.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId});
	}

	private CollectionPersistenceFinder<SocialActivity, NoSuchActivityException>
		_collectionPersistenceFinderByC_C;

	/**
	 * Returns an ordered range of all the social activities where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social activities
	 */
	@Override
	public List<SocialActivity> findByC_C(
		long classNameId, long classPK, int start, int end,
		OrderByComparator<SocialActivity> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first social activity in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity
	 * @throws NoSuchActivityException if a matching social activity could not be found
	 */
	@Override
	public SocialActivity findByC_C_First(
			long classNameId, long classPK,
			OrderByComparator<SocialActivity> orderByComparator)
		throws NoSuchActivityException {

		return _collectionPersistenceFinderByC_C.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK}, orderByComparator);
	}

	/**
	 * Returns the first social activity in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity, or <code>null</code> if a matching social activity could not be found
	 */
	@Override
	public SocialActivity fetchByC_C_First(
		long classNameId, long classPK,
		OrderByComparator<SocialActivity> orderByComparator) {

		return _collectionPersistenceFinderByC_C.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK}, orderByComparator);
	}

	/**
	 * Removes all the social activities where classNameId = &#63; and classPK = &#63; from the database.
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
	 * Returns the number of social activities where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching social activities
	 */
	@Override
	public int countByC_C(long classNameId, long classPK) {
		return _collectionPersistenceFinderByC_C.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK});
	}

	private CollectionPersistenceFinder<SocialActivity, NoSuchActivityException>
		_collectionPersistenceFinderByM_C_C;

	/**
	 * Returns an ordered range of all the social activities where mirrorActivityId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param mirrorActivityId the mirror activity ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social activities
	 */
	@Override
	public List<SocialActivity> findByM_C_C(
		long mirrorActivityId, long classNameId, long classPK, int start,
		int end, OrderByComparator<SocialActivity> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByM_C_C.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {mirrorActivityId, classNameId, classPK}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first social activity in the ordered set where mirrorActivityId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param mirrorActivityId the mirror activity ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity
	 * @throws NoSuchActivityException if a matching social activity could not be found
	 */
	@Override
	public SocialActivity findByM_C_C_First(
			long mirrorActivityId, long classNameId, long classPK,
			OrderByComparator<SocialActivity> orderByComparator)
		throws NoSuchActivityException {

		return _collectionPersistenceFinderByM_C_C.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {mirrorActivityId, classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Returns the first social activity in the ordered set where mirrorActivityId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param mirrorActivityId the mirror activity ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity, or <code>null</code> if a matching social activity could not be found
	 */
	@Override
	public SocialActivity fetchByM_C_C_First(
		long mirrorActivityId, long classNameId, long classPK,
		OrderByComparator<SocialActivity> orderByComparator) {

		return _collectionPersistenceFinderByM_C_C.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {mirrorActivityId, classNameId, classPK},
			orderByComparator);
	}

	/**
	 * Removes all the social activities where mirrorActivityId = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param mirrorActivityId the mirror activity ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	@Override
	public void removeByM_C_C(
		long mirrorActivityId, long classNameId, long classPK) {

		_collectionPersistenceFinderByM_C_C.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {mirrorActivityId, classNameId, classPK});
	}

	/**
	 * Returns the number of social activities where mirrorActivityId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param mirrorActivityId the mirror activity ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching social activities
	 */
	@Override
	public int countByM_C_C(
		long mirrorActivityId, long classNameId, long classPK) {

		return _collectionPersistenceFinderByM_C_C.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {mirrorActivityId, classNameId, classPK});
	}

	private CollectionPersistenceFinder<SocialActivity, NoSuchActivityException>
		_collectionPersistenceFinderByC_C_T;

	/**
	 * Returns an ordered range of all the social activities where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social activities
	 */
	@Override
	public List<SocialActivity> findByC_C_T(
		long classNameId, long classPK, int type, int start, int end,
		OrderByComparator<SocialActivity> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C_T.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK, type}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first social activity in the ordered set where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity
	 * @throws NoSuchActivityException if a matching social activity could not be found
	 */
	@Override
	public SocialActivity findByC_C_T_First(
			long classNameId, long classPK, int type,
			OrderByComparator<SocialActivity> orderByComparator)
		throws NoSuchActivityException {

		return _collectionPersistenceFinderByC_C_T.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK, type}, orderByComparator);
	}

	/**
	 * Returns the first social activity in the ordered set where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity, or <code>null</code> if a matching social activity could not be found
	 */
	@Override
	public SocialActivity fetchByC_C_T_First(
		long classNameId, long classPK, int type,
		OrderByComparator<SocialActivity> orderByComparator) {

		return _collectionPersistenceFinderByC_C_T.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK, type}, orderByComparator);
	}

	/**
	 * Removes all the social activities where classNameId = &#63; and classPK = &#63; and type = &#63; from the database.
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
	 * Returns the number of social activities where classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @return the number of matching social activities
	 */
	@Override
	public int countByC_C_T(long classNameId, long classPK, int type) {
		return _collectionPersistenceFinderByC_C_T.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK, type});
	}

	private CollectionPersistenceFinder<SocialActivity, NoSuchActivityException>
		_collectionPersistenceFinderByG_U_C_C_T_R;

	/**
	 * Returns an ordered range of all the social activities where groupId = &#63; and userId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and receiverUserId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SocialActivityModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param receiverUserId the receiver user ID
	 * @param start the lower bound of the range of social activities
	 * @param end the upper bound of the range of social activities (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social activities
	 */
	@Override
	public List<SocialActivity> findByG_U_C_C_T_R(
		long groupId, long userId, long classNameId, long classPK, int type,
		long receiverUserId, int start, int end,
		OrderByComparator<SocialActivity> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_U_C_C_T_R.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				groupId, userId, classNameId, classPK, type, receiverUserId
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first social activity in the ordered set where groupId = &#63; and userId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and receiverUserId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param receiverUserId the receiver user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity
	 * @throws NoSuchActivityException if a matching social activity could not be found
	 */
	@Override
	public SocialActivity findByG_U_C_C_T_R_First(
			long groupId, long userId, long classNameId, long classPK, int type,
			long receiverUserId,
			OrderByComparator<SocialActivity> orderByComparator)
		throws NoSuchActivityException {

		return _collectionPersistenceFinderByG_U_C_C_T_R.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				groupId, userId, classNameId, classPK, type, receiverUserId
			},
			orderByComparator);
	}

	/**
	 * Returns the first social activity in the ordered set where groupId = &#63; and userId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and receiverUserId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param receiverUserId the receiver user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity, or <code>null</code> if a matching social activity could not be found
	 */
	@Override
	public SocialActivity fetchByG_U_C_C_T_R_First(
		long groupId, long userId, long classNameId, long classPK, int type,
		long receiverUserId,
		OrderByComparator<SocialActivity> orderByComparator) {

		return _collectionPersistenceFinderByG_U_C_C_T_R.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				groupId, userId, classNameId, classPK, type, receiverUserId
			},
			orderByComparator);
	}

	/**
	 * Removes all the social activities where groupId = &#63; and userId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and receiverUserId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param receiverUserId the receiver user ID
	 */
	@Override
	public void removeByG_U_C_C_T_R(
		long groupId, long userId, long classNameId, long classPK, int type,
		long receiverUserId) {

		_collectionPersistenceFinderByG_U_C_C_T_R.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				groupId, userId, classNameId, classPK, type, receiverUserId
			});
	}

	/**
	 * Returns the number of social activities where groupId = &#63; and userId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and receiverUserId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param receiverUserId the receiver user ID
	 * @return the number of matching social activities
	 */
	@Override
	public int countByG_U_C_C_T_R(
		long groupId, long userId, long classNameId, long classPK, int type,
		long receiverUserId) {

		return _collectionPersistenceFinderByG_U_C_C_T_R.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				groupId, userId, classNameId, classPK, type, receiverUserId
			});
	}

	private UniquePersistenceFinder<SocialActivity, NoSuchActivityException>
		_uniquePersistenceFinderByG_U_CD_C_C_T_R;

	/**
	 * Returns the social activity where groupId = &#63; and userId = &#63; and createDate = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and receiverUserId = &#63; or throws a <code>NoSuchActivityException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param receiverUserId the receiver user ID
	 * @return the matching social activity
	 * @throws NoSuchActivityException if a matching social activity could not be found
	 */
	@Override
	public SocialActivity findByG_U_CD_C_C_T_R(
			long groupId, long userId, long createDate, long classNameId,
			long classPK, int type, long receiverUserId)
		throws NoSuchActivityException {

		return _uniquePersistenceFinderByG_U_CD_C_C_T_R.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				groupId, userId, createDate, classNameId, classPK, type,
				receiverUserId
			});
	}

	/**
	 * Returns the social activity where groupId = &#63; and userId = &#63; and createDate = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and receiverUserId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param receiverUserId the receiver user ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching social activity, or <code>null</code> if a matching social activity could not be found
	 */
	@Override
	public SocialActivity fetchByG_U_CD_C_C_T_R(
		long groupId, long userId, long createDate, long classNameId,
		long classPK, int type, long receiverUserId, boolean useFinderCache) {

		return _uniquePersistenceFinderByG_U_CD_C_C_T_R.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				groupId, userId, createDate, classNameId, classPK, type,
				receiverUserId
			},
			useFinderCache);
	}

	/**
	 * Removes the social activity where groupId = &#63; and userId = &#63; and createDate = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and receiverUserId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param receiverUserId the receiver user ID
	 * @return the social activity that was removed
	 */
	@Override
	public SocialActivity removeByG_U_CD_C_C_T_R(
			long groupId, long userId, long createDate, long classNameId,
			long classPK, int type, long receiverUserId)
		throws NoSuchActivityException {

		SocialActivity socialActivity = findByG_U_CD_C_C_T_R(
			groupId, userId, createDate, classNameId, classPK, type,
			receiverUserId);

		return remove(socialActivity);
	}

	/**
	 * Returns the number of social activities where groupId = &#63; and userId = &#63; and createDate = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and receiverUserId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param receiverUserId the receiver user ID
	 * @return the number of matching social activities
	 */
	@Override
	public int countByG_U_CD_C_C_T_R(
		long groupId, long userId, long createDate, long classNameId,
		long classPK, int type, long receiverUserId) {

		return _uniquePersistenceFinderByG_U_CD_C_C_T_R.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				groupId, userId, createDate, classNameId, classPK, type,
				receiverUserId
			});
	}

	public SocialActivityPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(SocialActivity.class);

		setModelImplClass(SocialActivityImpl.class);
		setModelPKClass(long.class);

		setTable(SocialActivityTable.INSTANCE);
	}

	/**
	 * Creates a new social activity with the primary key. Does not add the social activity to the database.
	 *
	 * @param activityId the primary key for the new social activity
	 * @return the new social activity
	 */
	@Override
	public SocialActivity create(long activityId) {
		SocialActivity socialActivity = new SocialActivityImpl();

		socialActivity.setNew(true);
		socialActivity.setPrimaryKey(activityId);

		socialActivity.setCompanyId(CompanyThreadLocal.getCompanyId());

		return socialActivity;
	}

	/**
	 * Removes the social activity with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param activityId the primary key of the social activity
	 * @return the social activity that was removed
	 * @throws NoSuchActivityException if a social activity with the primary key could not be found
	 */
	@Override
	public SocialActivity remove(long activityId)
		throws NoSuchActivityException {

		return remove((Serializable)activityId);
	}

	@Override
	protected SocialActivity removeImpl(SocialActivity socialActivity) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(socialActivity)) {
				socialActivity = (SocialActivity)session.get(
					SocialActivityImpl.class,
					socialActivity.getPrimaryKeyObj());
			}

			if ((socialActivity != null) &&
				CTPersistenceHelperUtil.isRemove(socialActivity)) {

				session.delete(socialActivity);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (socialActivity != null) {
			clearCache(socialActivity);
		}

		return socialActivity;
	}

	@Override
	public SocialActivity updateImpl(SocialActivity socialActivity) {
		boolean isNew = socialActivity.isNew();

		if (!(socialActivity instanceof SocialActivityModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(socialActivity.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					socialActivity);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in socialActivity proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom SocialActivity implementation " +
					socialActivity.getClass());
		}

		SocialActivityModelImpl socialActivityModelImpl =
			(SocialActivityModelImpl)socialActivity;

		Session session = null;

		try {
			session = openSession();

			if (CTPersistenceHelperUtil.isInsert(socialActivity)) {
				if (!isNew) {
					session.evict(
						SocialActivityImpl.class,
						socialActivity.getPrimaryKeyObj());
				}

				session.save(socialActivity);
			}
			else {
				socialActivity = (SocialActivity)session.merge(socialActivity);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(socialActivity, false);

		if (isNew) {
			socialActivity.setNew(false);
		}

		socialActivity.resetOriginalValues();

		return socialActivity;
	}

	/**
	 * Returns the social activity with the primary key or throws a <code>NoSuchActivityException</code> if it could not be found.
	 *
	 * @param activityId the primary key of the social activity
	 * @return the social activity
	 * @throws NoSuchActivityException if a social activity with the primary key could not be found
	 */
	@Override
	public SocialActivity findByPrimaryKey(long activityId)
		throws NoSuchActivityException {

		return findByPrimaryKey((Serializable)activityId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return CTPersistenceHelperUtil.getCTPersistenceHelper();
	}

	/**
	 * Returns the social activity with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param activityId the primary key of the social activity
	 * @return the social activity, or <code>null</code> if a social activity with the primary key could not be found
	 */
	@Override
	public SocialActivity fetchByPrimaryKey(long activityId) {
		return fetchByPrimaryKey((Serializable)activityId);
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
		return "activityId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_SOCIALACTIVITY;
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
		return SocialActivityModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "SocialActivity";
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
		ctMergeColumnNames.add("activitySetId");
		ctMergeColumnNames.add("mirrorActivityId");
		ctStrictColumnNames.add("classNameId");
		ctStrictColumnNames.add("classPK");
		ctMergeColumnNames.add("parentClassNameId");
		ctMergeColumnNames.add("parentClassPK");
		ctMergeColumnNames.add("type_");
		ctMergeColumnNames.add("extraData");
		ctMergeColumnNames.add("receiverUserId");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("activityId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(
			new String[] {
				"groupId", "userId", "createDate", "classNameId", "classPK",
				"type_", "receiverUserId"
			});
	}

	/**
	 * Initializes the social activity persistence.
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
				_SQL_SELECT_SOCIALACTIVITY_WHERE,
				_SQL_COUNT_SOCIALACTIVITY_WHERE,
				SocialActivityModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"socialActivity.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, SocialActivity::getGroupId));

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
				_SQL_SELECT_SOCIALACTIVITY_WHERE,
				_SQL_COUNT_SOCIALACTIVITY_WHERE,
				SocialActivityModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"socialActivity.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, SocialActivity::getCompanyId));

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
				_SQL_SELECT_SOCIALACTIVITY_WHERE,
				_SQL_COUNT_SOCIALACTIVITY_WHERE,
				SocialActivityModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"socialActivity.", "userId", FinderColumn.Type.LONG, "=",
					true, true, SocialActivity::getUserId));

		_collectionPersistenceFinderByActivitySetId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByActivitySetId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"activitySetId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByActivitySetId", new String[] {Long.class.getName()},
					new String[] {"activitySetId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByActivitySetId", new String[] {Long.class.getName()},
					new String[] {"activitySetId"}, false),
				_SQL_SELECT_SOCIALACTIVITY_WHERE,
				_SQL_COUNT_SOCIALACTIVITY_WHERE,
				SocialActivityModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"socialActivity.", "activitySetId", FinderColumn.Type.LONG,
					"=", true, true, SocialActivity::getActivitySetId));

		_uniquePersistenceFinderByMirrorActivityId =
			new UniquePersistenceFinder<>(
				this,
				createUniqueFinderPath(
					FINDER_CLASS_NAME_ENTITY, "fetchByMirrorActivityId",
					new String[] {Long.class.getName()},
					new String[] {"mirrorActivityId"}, 0, 0, false,
					SocialActivity::getMirrorActivityId),
				_SQL_SELECT_SOCIALACTIVITY_WHERE, "",
				new FinderColumn<>(
					"socialActivity.", "mirrorActivityId",
					FinderColumn.Type.LONG, "=", true, true,
					SocialActivity::getMirrorActivityId));

		_collectionPersistenceFinderByReceiverUserId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByReceiverUserId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"receiverUserId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByReceiverUserId", new String[] {Long.class.getName()},
					new String[] {"receiverUserId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByReceiverUserId",
					new String[] {Long.class.getName()},
					new String[] {"receiverUserId"}, false),
				_SQL_SELECT_SOCIALACTIVITY_WHERE,
				_SQL_COUNT_SOCIALACTIVITY_WHERE,
				SocialActivityModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"socialActivity.", "receiverUserId", FinderColumn.Type.LONG,
					"=", true, true, SocialActivity::getReceiverUserId));

		_collectionPersistenceFinderByC_CN = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_CN",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"companyId", "classNameId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_CN",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"companyId", "classNameId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_CN",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"companyId", "classNameId"}, false),
			_SQL_SELECT_SOCIALACTIVITY_WHERE, _SQL_COUNT_SOCIALACTIVITY_WHERE,
			SocialActivityModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"socialActivity.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, SocialActivity::getCompanyId),
			new FinderColumn<>(
				"socialActivity.", "classNameId", FinderColumn.Type.LONG, "=",
				true, true, SocialActivity::getClassNameId));

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
			_SQL_SELECT_SOCIALACTIVITY_WHERE, _SQL_COUNT_SOCIALACTIVITY_WHERE,
			SocialActivityModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"socialActivity.", "classNameId", FinderColumn.Type.LONG, "=",
				true, true, SocialActivity::getClassNameId),
			new FinderColumn<>(
				"socialActivity.", "classPK", FinderColumn.Type.LONG, "=", true,
				true, SocialActivity::getClassPK));

		_collectionPersistenceFinderByM_C_C = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByM_C_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"mirrorActivityId", "classNameId", "classPK"},
				true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByM_C_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {"mirrorActivityId", "classNameId", "classPK"},
				true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByM_C_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {"mirrorActivityId", "classNameId", "classPK"},
				false),
			_SQL_SELECT_SOCIALACTIVITY_WHERE, _SQL_COUNT_SOCIALACTIVITY_WHERE,
			SocialActivityModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"socialActivity.", "mirrorActivityId", FinderColumn.Type.LONG,
				"=", true, true, SocialActivity::getMirrorActivityId),
			new FinderColumn<>(
				"socialActivity.", "classNameId", FinderColumn.Type.LONG, "=",
				true, true, SocialActivity::getClassNameId),
			new FinderColumn<>(
				"socialActivity.", "classPK", FinderColumn.Type.LONG, "=", true,
				true, SocialActivity::getClassPK));

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
			_SQL_SELECT_SOCIALACTIVITY_WHERE, _SQL_COUNT_SOCIALACTIVITY_WHERE,
			SocialActivityModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"socialActivity.", "classNameId", FinderColumn.Type.LONG, "=",
				true, true, SocialActivity::getClassNameId),
			new FinderColumn<>(
				"socialActivity.", "classPK", FinderColumn.Type.LONG, "=", true,
				true, SocialActivity::getClassPK),
			new FinderColumn<>(
				"socialActivity.", "type", "type_", FinderColumn.Type.INTEGER,
				"=", true, true, SocialActivity::getType));

		_collectionPersistenceFinderByG_U_C_C_T_R =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_U_C_C_T_R",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "userId", "classNameId", "classPK", "type_",
						"receiverUserId"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByG_U_C_C_T_R",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Long.class.getName()
					},
					new String[] {
						"groupId", "userId", "classNameId", "classPK", "type_",
						"receiverUserId"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByG_U_C_C_T_R",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Long.class.getName()
					},
					new String[] {
						"groupId", "userId", "classNameId", "classPK", "type_",
						"receiverUserId"
					},
					false),
				_SQL_SELECT_SOCIALACTIVITY_WHERE,
				_SQL_COUNT_SOCIALACTIVITY_WHERE,
				SocialActivityModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"socialActivity.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, SocialActivity::getGroupId),
				new FinderColumn<>(
					"socialActivity.", "userId", FinderColumn.Type.LONG, "=",
					true, true, SocialActivity::getUserId),
				new FinderColumn<>(
					"socialActivity.", "classNameId", FinderColumn.Type.LONG,
					"=", true, true, SocialActivity::getClassNameId),
				new FinderColumn<>(
					"socialActivity.", "classPK", FinderColumn.Type.LONG, "=",
					true, true, SocialActivity::getClassPK),
				new FinderColumn<>(
					"socialActivity.", "type", "type_",
					FinderColumn.Type.INTEGER, "=", true, true,
					SocialActivity::getType),
				new FinderColumn<>(
					"socialActivity.", "receiverUserId", FinderColumn.Type.LONG,
					"=", true, true, SocialActivity::getReceiverUserId));

		_uniquePersistenceFinderByG_U_CD_C_C_T_R =
			new UniquePersistenceFinder<>(
				this,
				createUniqueFinderPath(
					FINDER_CLASS_NAME_ENTITY, "fetchByG_U_CD_C_C_T_R",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName(),
						Long.class.getName()
					},
					new String[] {
						"groupId", "userId", "createDate", "classNameId",
						"classPK", "type_", "receiverUserId"
					},
					0, 0, false, SocialActivity::getGroupId,
					SocialActivity::getUserId, SocialActivity::getCreateDate,
					SocialActivity::getClassNameId, SocialActivity::getClassPK,
					SocialActivity::getType, SocialActivity::getReceiverUserId),
				_SQL_SELECT_SOCIALACTIVITY_WHERE, "",
				new FinderColumn<>(
					"socialActivity.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, SocialActivity::getGroupId),
				new FinderColumn<>(
					"socialActivity.", "userId", FinderColumn.Type.LONG, "=",
					true, true, SocialActivity::getUserId),
				new FinderColumn<>(
					"socialActivity.", "createDate", FinderColumn.Type.LONG,
					"=", true, true, SocialActivity::getCreateDate),
				new FinderColumn<>(
					"socialActivity.", "classNameId", FinderColumn.Type.LONG,
					"=", true, true, SocialActivity::getClassNameId),
				new FinderColumn<>(
					"socialActivity.", "classPK", FinderColumn.Type.LONG, "=",
					true, true, SocialActivity::getClassPK),
				new FinderColumn<>(
					"socialActivity.", "type", "type_",
					FinderColumn.Type.INTEGER, "=", true, true,
					SocialActivity::getType),
				new FinderColumn<>(
					"socialActivity.", "receiverUserId", FinderColumn.Type.LONG,
					"=", true, true, SocialActivity::getReceiverUserId));

		SocialActivityUtil.setPersistence(this);
	}

	public void destroy() {
		SocialActivityUtil.setPersistence(null);

		EntityCacheUtil.removeCache(SocialActivityImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		SocialActivityModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_SOCIALACTIVITY =
		"SELECT socialActivity FROM SocialActivity socialActivity";

	private static final String _SQL_SELECT_SOCIALACTIVITY_WHERE =
		"SELECT socialActivity FROM SocialActivity socialActivity WHERE ";

	private static final String _SQL_COUNT_SOCIALACTIVITY_WHERE =
		"SELECT COUNT(socialActivity) FROM SocialActivity socialActivity WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"type"});

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1633961054