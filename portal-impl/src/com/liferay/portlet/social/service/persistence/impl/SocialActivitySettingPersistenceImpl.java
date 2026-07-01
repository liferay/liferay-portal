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
import com.liferay.portlet.social.model.impl.SocialActivitySettingImpl;
import com.liferay.portlet.social.model.impl.SocialActivitySettingModelImpl;
import com.liferay.social.kernel.exception.NoSuchActivitySettingException;
import com.liferay.social.kernel.model.SocialActivitySetting;
import com.liferay.social.kernel.model.SocialActivitySettingTable;
import com.liferay.social.kernel.service.persistence.SocialActivitySettingPersistence;
import com.liferay.social.kernel.service.persistence.SocialActivitySettingUtil;

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
 * The persistence implementation for the social activity setting service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class SocialActivitySettingPersistenceImpl
	extends BasePersistenceImpl
		<SocialActivitySetting, NoSuchActivitySettingException>
	implements SocialActivitySettingPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>SocialActivitySettingUtil</code> to access the social activity setting persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		SocialActivitySettingImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<SocialActivitySetting, NoSuchActivitySettingException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the social activity settings where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SocialActivitySettingModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of social activity settings
	 * @param end the upper bound of the range of social activity settings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social activity settings
	 */
	@Override
	public List<SocialActivitySetting> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<SocialActivitySetting> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first social activity setting in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity setting
	 * @throws NoSuchActivitySettingException if a matching social activity setting could not be found
	 */
	@Override
	public SocialActivitySetting findByGroupId_First(
			long groupId,
			OrderByComparator<SocialActivitySetting> orderByComparator)
		throws NoSuchActivitySettingException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId},
			orderByComparator);
	}

	/**
	 * Returns the first social activity setting in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity setting, or <code>null</code> if a matching social activity setting could not be found
	 */
	@Override
	public SocialActivitySetting fetchByGroupId_First(
		long groupId,
		OrderByComparator<SocialActivitySetting> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId},
			orderByComparator);
	}

	/**
	 * Removes all the social activity settings where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId});
	}

	/**
	 * Returns the number of social activity settings where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching social activity settings
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId});
	}

	private CollectionPersistenceFinder
		<SocialActivitySetting, NoSuchActivitySettingException>
			_collectionPersistenceFinderByG_C;

	/**
	 * Returns an ordered range of all the social activity settings where groupId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SocialActivitySettingModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of social activity settings
	 * @param end the upper bound of the range of social activity settings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social activity settings
	 */
	@Override
	public List<SocialActivitySetting> findByG_C(
		long groupId, long classNameId, int start, int end,
		OrderByComparator<SocialActivitySetting> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, classNameId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first social activity setting in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity setting
	 * @throws NoSuchActivitySettingException if a matching social activity setting could not be found
	 */
	@Override
	public SocialActivitySetting findByG_C_First(
			long groupId, long classNameId,
			OrderByComparator<SocialActivitySetting> orderByComparator)
		throws NoSuchActivitySettingException {

		return _collectionPersistenceFinderByG_C.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, classNameId}, orderByComparator);
	}

	/**
	 * Returns the first social activity setting in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity setting, or <code>null</code> if a matching social activity setting could not be found
	 */
	@Override
	public SocialActivitySetting fetchByG_C_First(
		long groupId, long classNameId,
		OrderByComparator<SocialActivitySetting> orderByComparator) {

		return _collectionPersistenceFinderByG_C.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, classNameId}, orderByComparator);
	}

	/**
	 * Removes all the social activity settings where groupId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 */
	@Override
	public void removeByG_C(long groupId, long classNameId) {
		_collectionPersistenceFinderByG_C.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, classNameId});
	}

	/**
	 * Returns the number of social activity settings where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @return the number of matching social activity settings
	 */
	@Override
	public int countByG_C(long groupId, long classNameId) {
		return _collectionPersistenceFinderByG_C.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, classNameId});
	}

	private CollectionPersistenceFinder
		<SocialActivitySetting, NoSuchActivitySettingException>
			_collectionPersistenceFinderByG_A;

	/**
	 * Returns an ordered range of all the social activity settings where groupId = &#63; and activityType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SocialActivitySettingModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param activityType the activity type
	 * @param start the lower bound of the range of social activity settings
	 * @param end the upper bound of the range of social activity settings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social activity settings
	 */
	@Override
	public List<SocialActivitySetting> findByG_A(
		long groupId, int activityType, int start, int end,
		OrderByComparator<SocialActivitySetting> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_A.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, activityType}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first social activity setting in the ordered set where groupId = &#63; and activityType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param activityType the activity type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity setting
	 * @throws NoSuchActivitySettingException if a matching social activity setting could not be found
	 */
	@Override
	public SocialActivitySetting findByG_A_First(
			long groupId, int activityType,
			OrderByComparator<SocialActivitySetting> orderByComparator)
		throws NoSuchActivitySettingException {

		return _collectionPersistenceFinderByG_A.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, activityType}, orderByComparator);
	}

	/**
	 * Returns the first social activity setting in the ordered set where groupId = &#63; and activityType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param activityType the activity type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity setting, or <code>null</code> if a matching social activity setting could not be found
	 */
	@Override
	public SocialActivitySetting fetchByG_A_First(
		long groupId, int activityType,
		OrderByComparator<SocialActivitySetting> orderByComparator) {

		return _collectionPersistenceFinderByG_A.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, activityType}, orderByComparator);
	}

	/**
	 * Removes all the social activity settings where groupId = &#63; and activityType = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param activityType the activity type
	 */
	@Override
	public void removeByG_A(long groupId, int activityType) {
		_collectionPersistenceFinderByG_A.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, activityType});
	}

	/**
	 * Returns the number of social activity settings where groupId = &#63; and activityType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param activityType the activity type
	 * @return the number of matching social activity settings
	 */
	@Override
	public int countByG_A(long groupId, int activityType) {
		return _collectionPersistenceFinderByG_A.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, activityType});
	}

	private CollectionPersistenceFinder
		<SocialActivitySetting, NoSuchActivitySettingException>
			_collectionPersistenceFinderByG_C_A;

	/**
	 * Returns an ordered range of all the social activity settings where groupId = &#63; and classNameId = &#63; and activityType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SocialActivitySettingModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param activityType the activity type
	 * @param start the lower bound of the range of social activity settings
	 * @param end the upper bound of the range of social activity settings (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social activity settings
	 */
	@Override
	public List<SocialActivitySetting> findByG_C_A(
		long groupId, long classNameId, int activityType, int start, int end,
		OrderByComparator<SocialActivitySetting> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_C_A.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, classNameId, activityType}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first social activity setting in the ordered set where groupId = &#63; and classNameId = &#63; and activityType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param activityType the activity type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity setting
	 * @throws NoSuchActivitySettingException if a matching social activity setting could not be found
	 */
	@Override
	public SocialActivitySetting findByG_C_A_First(
			long groupId, long classNameId, int activityType,
			OrderByComparator<SocialActivitySetting> orderByComparator)
		throws NoSuchActivitySettingException {

		return _collectionPersistenceFinderByG_C_A.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, classNameId, activityType},
			orderByComparator);
	}

	/**
	 * Returns the first social activity setting in the ordered set where groupId = &#63; and classNameId = &#63; and activityType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param activityType the activity type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity setting, or <code>null</code> if a matching social activity setting could not be found
	 */
	@Override
	public SocialActivitySetting fetchByG_C_A_First(
		long groupId, long classNameId, int activityType,
		OrderByComparator<SocialActivitySetting> orderByComparator) {

		return _collectionPersistenceFinderByG_C_A.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, classNameId, activityType},
			orderByComparator);
	}

	/**
	 * Removes all the social activity settings where groupId = &#63; and classNameId = &#63; and activityType = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param activityType the activity type
	 */
	@Override
	public void removeByG_C_A(
		long groupId, long classNameId, int activityType) {

		_collectionPersistenceFinderByG_C_A.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, classNameId, activityType});
	}

	/**
	 * Returns the number of social activity settings where groupId = &#63; and classNameId = &#63; and activityType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param activityType the activity type
	 * @return the number of matching social activity settings
	 */
	@Override
	public int countByG_C_A(long groupId, long classNameId, int activityType) {
		return _collectionPersistenceFinderByG_C_A.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, classNameId, activityType});
	}

	private UniquePersistenceFinder
		<SocialActivitySetting, NoSuchActivitySettingException>
			_uniquePersistenceFinderByG_C_A_N;

	/**
	 * Returns the social activity setting where groupId = &#63; and classNameId = &#63; and activityType = &#63; and name = &#63; or throws a <code>NoSuchActivitySettingException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param activityType the activity type
	 * @param name the name
	 * @return the matching social activity setting
	 * @throws NoSuchActivitySettingException if a matching social activity setting could not be found
	 */
	@Override
	public SocialActivitySetting findByG_C_A_N(
			long groupId, long classNameId, int activityType, String name)
		throws NoSuchActivitySettingException {

		return _uniquePersistenceFinderByG_C_A_N.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, classNameId, activityType, name});
	}

	/**
	 * Returns the social activity setting where groupId = &#63; and classNameId = &#63; and activityType = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param activityType the activity type
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching social activity setting, or <code>null</code> if a matching social activity setting could not be found
	 */
	@Override
	public SocialActivitySetting fetchByG_C_A_N(
		long groupId, long classNameId, int activityType, String name,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByG_C_A_N.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, classNameId, activityType, name},
			useFinderCache);
	}

	/**
	 * Removes the social activity setting where groupId = &#63; and classNameId = &#63; and activityType = &#63; and name = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param activityType the activity type
	 * @param name the name
	 * @return the social activity setting that was removed
	 */
	@Override
	public SocialActivitySetting removeByG_C_A_N(
			long groupId, long classNameId, int activityType, String name)
		throws NoSuchActivitySettingException {

		SocialActivitySetting socialActivitySetting = findByG_C_A_N(
			groupId, classNameId, activityType, name);

		return remove(socialActivitySetting);
	}

	/**
	 * Returns the number of social activity settings where groupId = &#63; and classNameId = &#63; and activityType = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param activityType the activity type
	 * @param name the name
	 * @return the number of matching social activity settings
	 */
	@Override
	public int countByG_C_A_N(
		long groupId, long classNameId, int activityType, String name) {

		return _uniquePersistenceFinderByG_C_A_N.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, classNameId, activityType, name});
	}

	public SocialActivitySettingPersistenceImpl() {
		setModelClass(SocialActivitySetting.class);

		setModelImplClass(SocialActivitySettingImpl.class);
		setModelPKClass(long.class);

		setTable(SocialActivitySettingTable.INSTANCE);
	}

	/**
	 * Creates a new social activity setting with the primary key. Does not add the social activity setting to the database.
	 *
	 * @param activitySettingId the primary key for the new social activity setting
	 * @return the new social activity setting
	 */
	@Override
	public SocialActivitySetting create(long activitySettingId) {
		SocialActivitySetting socialActivitySetting =
			new SocialActivitySettingImpl();

		socialActivitySetting.setNew(true);
		socialActivitySetting.setPrimaryKey(activitySettingId);

		socialActivitySetting.setCompanyId(CompanyThreadLocal.getCompanyId());

		return socialActivitySetting;
	}

	/**
	 * Removes the social activity setting with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param activitySettingId the primary key of the social activity setting
	 * @return the social activity setting that was removed
	 * @throws NoSuchActivitySettingException if a social activity setting with the primary key could not be found
	 */
	@Override
	public SocialActivitySetting remove(long activitySettingId)
		throws NoSuchActivitySettingException {

		return remove((Serializable)activitySettingId);
	}

	@Override
	protected SocialActivitySetting removeImpl(
		SocialActivitySetting socialActivitySetting) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(socialActivitySetting)) {
				socialActivitySetting = (SocialActivitySetting)session.get(
					SocialActivitySettingImpl.class,
					socialActivitySetting.getPrimaryKeyObj());
			}

			if ((socialActivitySetting != null) &&
				CTPersistenceHelperUtil.isRemove(socialActivitySetting)) {

				session.delete(socialActivitySetting);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (socialActivitySetting != null) {
			clearCache(socialActivitySetting);
		}

		return socialActivitySetting;
	}

	@Override
	public SocialActivitySetting updateImpl(
		SocialActivitySetting socialActivitySetting) {

		boolean isNew = socialActivitySetting.isNew();

		if (!(socialActivitySetting instanceof
				SocialActivitySettingModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(socialActivitySetting.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					socialActivitySetting);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in socialActivitySetting proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom SocialActivitySetting implementation " +
					socialActivitySetting.getClass());
		}

		SocialActivitySettingModelImpl socialActivitySettingModelImpl =
			(SocialActivitySettingModelImpl)socialActivitySetting;

		Session session = null;

		try {
			session = openSession();

			if (CTPersistenceHelperUtil.isInsert(socialActivitySetting)) {
				if (!isNew) {
					session.evict(
						SocialActivitySettingImpl.class,
						socialActivitySetting.getPrimaryKeyObj());
				}

				session.save(socialActivitySetting);
			}
			else {
				socialActivitySetting = (SocialActivitySetting)session.merge(
					socialActivitySetting);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(socialActivitySetting, false);

		if (isNew) {
			socialActivitySetting.setNew(false);
		}

		socialActivitySetting.resetOriginalValues();

		return socialActivitySetting;
	}

	/**
	 * Returns the social activity setting with the primary key or throws a <code>NoSuchActivitySettingException</code> if it could not be found.
	 *
	 * @param activitySettingId the primary key of the social activity setting
	 * @return the social activity setting
	 * @throws NoSuchActivitySettingException if a social activity setting with the primary key could not be found
	 */
	@Override
	public SocialActivitySetting findByPrimaryKey(long activitySettingId)
		throws NoSuchActivitySettingException {

		return findByPrimaryKey((Serializable)activitySettingId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return CTPersistenceHelperUtil.getCTPersistenceHelper();
	}

	/**
	 * Returns the social activity setting with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param activitySettingId the primary key of the social activity setting
	 * @return the social activity setting, or <code>null</code> if a social activity setting with the primary key could not be found
	 */
	@Override
	public SocialActivitySetting fetchByPrimaryKey(long activitySettingId) {
		return fetchByPrimaryKey((Serializable)activitySettingId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return EntityCacheUtil.getEntityCache();
	}

	@Override
	protected String getPKDBName() {
		return "activitySettingId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_SOCIALACTIVITYSETTING;
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
		return SocialActivitySettingModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "SocialActivitySetting";
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
		ctMergeColumnNames.add("activityType");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("value");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("activitySettingId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(
			new String[] {"groupId", "classNameId", "activityType", "name"});
	}

	/**
	 * Initializes the social activity setting persistence.
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
				_SQL_SELECT_SOCIALACTIVITYSETTING_WHERE,
				_SQL_COUNT_SOCIALACTIVITYSETTING_WHERE,
				SocialActivitySettingModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"socialActivitySetting.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, SocialActivitySetting::getGroupId));

		_collectionPersistenceFinderByG_C = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"groupId", "classNameId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"groupId", "classNameId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"groupId", "classNameId"}, false),
			_SQL_SELECT_SOCIALACTIVITYSETTING_WHERE,
			_SQL_COUNT_SOCIALACTIVITYSETTING_WHERE,
			SocialActivitySettingModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "",
			new FinderColumn<>(
				"socialActivitySetting.", "groupId", FinderColumn.Type.LONG,
				"=", true, true, SocialActivitySetting::getGroupId),
			new FinderColumn<>(
				"socialActivitySetting.", "classNameId", FinderColumn.Type.LONG,
				"=", true, true, SocialActivitySetting::getClassNameId));

		_collectionPersistenceFinderByG_A = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_A",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"groupId", "activityType"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_A",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"groupId", "activityType"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_A",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"groupId", "activityType"}, false),
			_SQL_SELECT_SOCIALACTIVITYSETTING_WHERE,
			_SQL_COUNT_SOCIALACTIVITYSETTING_WHERE,
			SocialActivitySettingModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "",
			new FinderColumn<>(
				"socialActivitySetting.", "groupId", FinderColumn.Type.LONG,
				"=", true, true, SocialActivitySetting::getGroupId),
			new FinderColumn<>(
				"socialActivitySetting.", "activityType",
				FinderColumn.Type.INTEGER, "=", true, true,
				SocialActivitySetting::getActivityType));

		_collectionPersistenceFinderByG_C_A = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_C_A",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"groupId", "classNameId", "activityType"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_C_A",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName()
				},
				new String[] {"groupId", "classNameId", "activityType"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_C_A",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName()
				},
				new String[] {"groupId", "classNameId", "activityType"}, false),
			_SQL_SELECT_SOCIALACTIVITYSETTING_WHERE,
			_SQL_COUNT_SOCIALACTIVITYSETTING_WHERE,
			SocialActivitySettingModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "",
			new FinderColumn<>(
				"socialActivitySetting.", "groupId", FinderColumn.Type.LONG,
				"=", true, true, SocialActivitySetting::getGroupId),
			new FinderColumn<>(
				"socialActivitySetting.", "classNameId", FinderColumn.Type.LONG,
				"=", true, true, SocialActivitySetting::getClassNameId),
			new FinderColumn<>(
				"socialActivitySetting.", "activityType",
				FinderColumn.Type.INTEGER, "=", true, true,
				SocialActivitySetting::getActivityType));

		_uniquePersistenceFinderByG_C_A_N = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_C_A_N",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), String.class.getName()
				},
				new String[] {"groupId", "classNameId", "activityType", "name"},
				0, 8, false, SocialActivitySetting::getGroupId,
				SocialActivitySetting::getClassNameId,
				SocialActivitySetting::getActivityType,
				convertNullFunction(SocialActivitySetting::getName)),
			_SQL_SELECT_SOCIALACTIVITYSETTING_WHERE, "",
			new FinderColumn<>(
				"socialActivitySetting.", "groupId", FinderColumn.Type.LONG,
				"=", true, true, SocialActivitySetting::getGroupId),
			new FinderColumn<>(
				"socialActivitySetting.", "classNameId", FinderColumn.Type.LONG,
				"=", true, true, SocialActivitySetting::getClassNameId),
			new FinderColumn<>(
				"socialActivitySetting.", "activityType",
				FinderColumn.Type.INTEGER, "=", true, true,
				SocialActivitySetting::getActivityType),
			new FinderColumn<>(
				"socialActivitySetting.", "name", FinderColumn.Type.STRING, "=",
				true, true, SocialActivitySetting::getName));

		SocialActivitySettingUtil.setPersistence(this);
	}

	public void destroy() {
		SocialActivitySettingUtil.setPersistence(null);

		EntityCacheUtil.removeCache(SocialActivitySettingImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		SocialActivitySettingModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_SOCIALACTIVITYSETTING =
		"SELECT socialActivitySetting FROM SocialActivitySetting socialActivitySetting";

	private static final String _SQL_SELECT_SOCIALACTIVITYSETTING_WHERE =
		"SELECT socialActivitySetting FROM SocialActivitySetting socialActivitySetting WHERE ";

	private static final String _SQL_COUNT_SOCIALACTIVITYSETTING_WHERE =
		"SELECT COUNT(socialActivitySetting) FROM SocialActivitySetting socialActivitySetting WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No SocialActivitySetting exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		SocialActivitySettingPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:466012175