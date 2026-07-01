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
import com.liferay.portal.kernel.exception.NoSuchLayoutSetException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutSetTable;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.LayoutSetPersistence;
import com.liferay.portal.kernel.service.persistence.LayoutSetUtil;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelperUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.model.impl.LayoutSetImpl;
import com.liferay.portal.model.impl.LayoutSetModelImpl;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence implementation for the layout set service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class LayoutSetPersistenceImpl
	extends BasePersistenceImpl<LayoutSet, NoSuchLayoutSetException>
	implements LayoutSetPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>LayoutSetUtil</code> to access the layout set persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		LayoutSetImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<LayoutSet, NoSuchLayoutSetException>
		_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the layout sets where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutSetModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layout sets
	 * @param end the upper bound of the range of layout sets (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout sets
	 */
	@Override
	public List<LayoutSet> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<LayoutSet> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout set in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout set
	 * @throws NoSuchLayoutSetException if a matching layout set could not be found
	 */
	@Override
	public LayoutSet findByGroupId_First(
			long groupId, OrderByComparator<LayoutSet> orderByComparator)
		throws NoSuchLayoutSetException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId},
			orderByComparator);
	}

	/**
	 * Returns the first layout set in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout set, or <code>null</code> if a matching layout set could not be found
	 */
	@Override
	public LayoutSet fetchByGroupId_First(
		long groupId, OrderByComparator<LayoutSet> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId},
			orderByComparator);
	}

	/**
	 * Removes all the layout sets where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId});
	}

	/**
	 * Returns the number of layout sets where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching layout sets
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId});
	}

	private CollectionPersistenceFinder<LayoutSet, NoSuchLayoutSetException>
		_collectionPersistenceFinderByLayoutSetPrototypeUuid;

	/**
	 * Returns an ordered range of all the layout sets where layoutSetPrototypeUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutSetModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetPrototypeUuid the layout set prototype uuid
	 * @param start the lower bound of the range of layout sets
	 * @param end the upper bound of the range of layout sets (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout sets
	 */
	@Override
	public List<LayoutSet> findByLayoutSetPrototypeUuid(
		String layoutSetPrototypeUuid, int start, int end,
		OrderByComparator<LayoutSet> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByLayoutSetPrototypeUuid.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetPrototypeUuid}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout set in the ordered set where layoutSetPrototypeUuid = &#63;.
	 *
	 * @param layoutSetPrototypeUuid the layout set prototype uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout set
	 * @throws NoSuchLayoutSetException if a matching layout set could not be found
	 */
	@Override
	public LayoutSet findByLayoutSetPrototypeUuid_First(
			String layoutSetPrototypeUuid,
			OrderByComparator<LayoutSet> orderByComparator)
		throws NoSuchLayoutSetException {

		return _collectionPersistenceFinderByLayoutSetPrototypeUuid.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetPrototypeUuid}, orderByComparator);
	}

	/**
	 * Returns the first layout set in the ordered set where layoutSetPrototypeUuid = &#63;.
	 *
	 * @param layoutSetPrototypeUuid the layout set prototype uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout set, or <code>null</code> if a matching layout set could not be found
	 */
	@Override
	public LayoutSet fetchByLayoutSetPrototypeUuid_First(
		String layoutSetPrototypeUuid,
		OrderByComparator<LayoutSet> orderByComparator) {

		return _collectionPersistenceFinderByLayoutSetPrototypeUuid.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetPrototypeUuid}, orderByComparator);
	}

	/**
	 * Removes all the layout sets where layoutSetPrototypeUuid = &#63; from the database.
	 *
	 * @param layoutSetPrototypeUuid the layout set prototype uuid
	 */
	@Override
	public void removeByLayoutSetPrototypeUuid(String layoutSetPrototypeUuid) {
		_collectionPersistenceFinderByLayoutSetPrototypeUuid.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetPrototypeUuid});
	}

	/**
	 * Returns the number of layout sets where layoutSetPrototypeUuid = &#63;.
	 *
	 * @param layoutSetPrototypeUuid the layout set prototype uuid
	 * @return the number of matching layout sets
	 */
	@Override
	public int countByLayoutSetPrototypeUuid(String layoutSetPrototypeUuid) {
		return _collectionPersistenceFinderByLayoutSetPrototypeUuid.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetPrototypeUuid});
	}

	private UniquePersistenceFinder<LayoutSet, NoSuchLayoutSetException>
		_uniquePersistenceFinderByG_P;

	/**
	 * Returns the layout set where groupId = &#63; and privateLayout = &#63; or throws a <code>NoSuchLayoutSetException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @return the matching layout set
	 * @throws NoSuchLayoutSetException if a matching layout set could not be found
	 */
	@Override
	public LayoutSet findByG_P(long groupId, boolean privateLayout)
		throws NoSuchLayoutSetException {

		return _uniquePersistenceFinderByG_P.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout});
	}

	/**
	 * Returns the layout set where groupId = &#63; and privateLayout = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout set, or <code>null</code> if a matching layout set could not be found
	 */
	@Override
	public LayoutSet fetchByG_P(
		long groupId, boolean privateLayout, boolean useFinderCache) {

		return _uniquePersistenceFinderByG_P.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout}, useFinderCache);
	}

	/**
	 * Removes the layout set where groupId = &#63; and privateLayout = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @return the layout set that was removed
	 */
	@Override
	public LayoutSet removeByG_P(long groupId, boolean privateLayout)
		throws NoSuchLayoutSetException {

		LayoutSet layoutSet = findByG_P(groupId, privateLayout);

		return remove(layoutSet);
	}

	/**
	 * Returns the number of layout sets where groupId = &#63; and privateLayout = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @return the number of matching layout sets
	 */
	@Override
	public int countByG_P(long groupId, boolean privateLayout) {
		return _uniquePersistenceFinderByG_P.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout});
	}

	private CollectionPersistenceFinder<LayoutSet, NoSuchLayoutSetException>
		_collectionPersistenceFinderByC_L;

	/**
	 * Returns an ordered range of all the layout sets where companyId = &#63; and layoutSetPrototypeUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutSetModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param layoutSetPrototypeUuid the layout set prototype uuid
	 * @param start the lower bound of the range of layout sets
	 * @param end the upper bound of the range of layout sets (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout sets
	 */
	@Override
	public List<LayoutSet> findByC_L(
		long companyId, String layoutSetPrototypeUuid, int start, int end,
		OrderByComparator<LayoutSet> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_L.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, layoutSetPrototypeUuid}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout set in the ordered set where companyId = &#63; and layoutSetPrototypeUuid = &#63;.
	 *
	 * @param companyId the company ID
	 * @param layoutSetPrototypeUuid the layout set prototype uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout set
	 * @throws NoSuchLayoutSetException if a matching layout set could not be found
	 */
	@Override
	public LayoutSet findByC_L_First(
			long companyId, String layoutSetPrototypeUuid,
			OrderByComparator<LayoutSet> orderByComparator)
		throws NoSuchLayoutSetException {

		return _collectionPersistenceFinderByC_L.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, layoutSetPrototypeUuid},
			orderByComparator);
	}

	/**
	 * Returns the first layout set in the ordered set where companyId = &#63; and layoutSetPrototypeUuid = &#63;.
	 *
	 * @param companyId the company ID
	 * @param layoutSetPrototypeUuid the layout set prototype uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout set, or <code>null</code> if a matching layout set could not be found
	 */
	@Override
	public LayoutSet fetchByC_L_First(
		long companyId, String layoutSetPrototypeUuid,
		OrderByComparator<LayoutSet> orderByComparator) {

		return _collectionPersistenceFinderByC_L.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, layoutSetPrototypeUuid},
			orderByComparator);
	}

	/**
	 * Removes all the layout sets where companyId = &#63; and layoutSetPrototypeUuid = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param layoutSetPrototypeUuid the layout set prototype uuid
	 */
	@Override
	public void removeByC_L(long companyId, String layoutSetPrototypeUuid) {
		_collectionPersistenceFinderByC_L.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, layoutSetPrototypeUuid});
	}

	/**
	 * Returns the number of layout sets where companyId = &#63; and layoutSetPrototypeUuid = &#63;.
	 *
	 * @param companyId the company ID
	 * @param layoutSetPrototypeUuid the layout set prototype uuid
	 * @return the number of matching layout sets
	 */
	@Override
	public int countByC_L(long companyId, String layoutSetPrototypeUuid) {
		return _collectionPersistenceFinderByC_L.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, layoutSetPrototypeUuid});
	}

	private CollectionPersistenceFinder<LayoutSet, NoSuchLayoutSetException>
		_collectionPersistenceFinderByP_L;

	/**
	 * Returns an ordered range of all the layout sets where privateLayout = &#63; and logoId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutSetModelImpl</code>.
	 * </p>
	 *
	 * @param privateLayout the private layout
	 * @param logoId the logo ID
	 * @param start the lower bound of the range of layout sets
	 * @param end the upper bound of the range of layout sets (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout sets
	 */
	@Override
	public List<LayoutSet> findByP_L(
		boolean privateLayout, long logoId, int start, int end,
		OrderByComparator<LayoutSet> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByP_L.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {privateLayout, logoId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first layout set in the ordered set where privateLayout = &#63; and logoId = &#63;.
	 *
	 * @param privateLayout the private layout
	 * @param logoId the logo ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout set
	 * @throws NoSuchLayoutSetException if a matching layout set could not be found
	 */
	@Override
	public LayoutSet findByP_L_First(
			boolean privateLayout, long logoId,
			OrderByComparator<LayoutSet> orderByComparator)
		throws NoSuchLayoutSetException {

		return _collectionPersistenceFinderByP_L.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {privateLayout, logoId}, orderByComparator);
	}

	/**
	 * Returns the first layout set in the ordered set where privateLayout = &#63; and logoId = &#63;.
	 *
	 * @param privateLayout the private layout
	 * @param logoId the logo ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout set, or <code>null</code> if a matching layout set could not be found
	 */
	@Override
	public LayoutSet fetchByP_L_First(
		boolean privateLayout, long logoId,
		OrderByComparator<LayoutSet> orderByComparator) {

		return _collectionPersistenceFinderByP_L.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {privateLayout, logoId}, orderByComparator);
	}

	/**
	 * Removes all the layout sets where privateLayout = &#63; and logoId = &#63; from the database.
	 *
	 * @param privateLayout the private layout
	 * @param logoId the logo ID
	 */
	@Override
	public void removeByP_L(boolean privateLayout, long logoId) {
		_collectionPersistenceFinderByP_L.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {privateLayout, logoId});
	}

	/**
	 * Returns the number of layout sets where privateLayout = &#63; and logoId = &#63;.
	 *
	 * @param privateLayout the private layout
	 * @param logoId the logo ID
	 * @return the number of matching layout sets
	 */
	@Override
	public int countByP_L(boolean privateLayout, long logoId) {
		return _collectionPersistenceFinderByP_L.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {privateLayout, logoId});
	}

	public LayoutSetPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("settings", "settings_");

		setDBColumnNames(dbColumnNames);

		setModelClass(LayoutSet.class);

		setModelImplClass(LayoutSetImpl.class);
		setModelPKClass(long.class);

		setTable(LayoutSetTable.INSTANCE);
	}

	/**
	 * Creates a new layout set with the primary key. Does not add the layout set to the database.
	 *
	 * @param layoutSetId the primary key for the new layout set
	 * @return the new layout set
	 */
	@Override
	public LayoutSet create(long layoutSetId) {
		LayoutSet layoutSet = new LayoutSetImpl();

		layoutSet.setNew(true);
		layoutSet.setPrimaryKey(layoutSetId);

		layoutSet.setCompanyId(CompanyThreadLocal.getCompanyId());

		return layoutSet;
	}

	/**
	 * Removes the layout set with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param layoutSetId the primary key of the layout set
	 * @return the layout set that was removed
	 * @throws NoSuchLayoutSetException if a layout set with the primary key could not be found
	 */
	@Override
	public LayoutSet remove(long layoutSetId) throws NoSuchLayoutSetException {
		return remove((Serializable)layoutSetId);
	}

	@Override
	protected LayoutSet removeImpl(LayoutSet layoutSet) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(layoutSet)) {
				layoutSet = (LayoutSet)session.get(
					LayoutSetImpl.class, layoutSet.getPrimaryKeyObj());
			}

			if ((layoutSet != null) &&
				CTPersistenceHelperUtil.isRemove(layoutSet)) {

				session.delete(layoutSet);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (layoutSet != null) {
			clearCache(layoutSet);
		}

		return layoutSet;
	}

	@Override
	public LayoutSet updateImpl(LayoutSet layoutSet) {
		boolean isNew = layoutSet.isNew();

		if (!(layoutSet instanceof LayoutSetModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(layoutSet.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(layoutSet);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in layoutSet proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom LayoutSet implementation " +
					layoutSet.getClass());
		}

		LayoutSetModelImpl layoutSetModelImpl = (LayoutSetModelImpl)layoutSet;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (layoutSet.getCreateDate() == null)) {
			if (serviceContext == null) {
				layoutSet.setCreateDate(date);
			}
			else {
				layoutSet.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!layoutSetModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				layoutSet.setModifiedDate(date);
			}
			else {
				layoutSet.setModifiedDate(serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (CTPersistenceHelperUtil.isInsert(layoutSet)) {
				if (!isNew) {
					session.evict(
						LayoutSetImpl.class, layoutSet.getPrimaryKeyObj());
				}

				session.save(layoutSet);
			}
			else {
				layoutSet = (LayoutSet)session.merge(layoutSet);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(layoutSet, false);

		if (isNew) {
			layoutSet.setNew(false);
		}

		layoutSet.resetOriginalValues();

		return layoutSet;
	}

	/**
	 * Returns the layout set with the primary key or throws a <code>NoSuchLayoutSetException</code> if it could not be found.
	 *
	 * @param layoutSetId the primary key of the layout set
	 * @return the layout set
	 * @throws NoSuchLayoutSetException if a layout set with the primary key could not be found
	 */
	@Override
	public LayoutSet findByPrimaryKey(long layoutSetId)
		throws NoSuchLayoutSetException {

		return findByPrimaryKey((Serializable)layoutSetId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return CTPersistenceHelperUtil.getCTPersistenceHelper();
	}

	/**
	 * Returns the layout set with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param layoutSetId the primary key of the layout set
	 * @return the layout set, or <code>null</code> if a layout set with the primary key could not be found
	 */
	@Override
	public LayoutSet fetchByPrimaryKey(long layoutSetId) {
		return fetchByPrimaryKey((Serializable)layoutSetId);
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
		return "layoutSetId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_LAYOUTSET;
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
		return LayoutSetModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "LayoutSet";
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
		Set<String> ctIgnoreColumnNames = new HashSet<String>();
		Set<String> ctMergeColumnNames = new HashSet<String>();
		Set<String> ctStrictColumnNames = new HashSet<String>();

		ctControlColumnNames.add("mvccVersion");
		ctControlColumnNames.add("ctCollectionId");
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("privateLayout");
		ctMergeColumnNames.add("logoId");
		ctMergeColumnNames.add("themeId");
		ctMergeColumnNames.add("colorSchemeId");
		ctMergeColumnNames.add("faviconFileEntryId");
		ctMergeColumnNames.add("css");
		ctMergeColumnNames.add("settings_");
		ctMergeColumnNames.add("layoutSetPrototypeUuid");
		ctMergeColumnNames.add("layoutSetPrototypeLinkEnabled");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("layoutSetId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"groupId", "privateLayout"});
	}

	/**
	 * Initializes the layout set persistence.
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
				_SQL_SELECT_LAYOUTSET_WHERE, _SQL_COUNT_LAYOUTSET_WHERE,
				LayoutSetModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"layoutSet.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, LayoutSet::getGroupId));

		_collectionPersistenceFinderByLayoutSetPrototypeUuid =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByLayoutSetPrototypeUuid",
					new String[] {
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"layoutSetPrototypeUuid"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByLayoutSetPrototypeUuid",
					new String[] {String.class.getName()},
					new String[] {"layoutSetPrototypeUuid"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByLayoutSetPrototypeUuid",
					new String[] {String.class.getName()},
					new String[] {"layoutSetPrototypeUuid"}, 0, 1, false, null),
				_SQL_SELECT_LAYOUTSET_WHERE, _SQL_COUNT_LAYOUTSET_WHERE,
				LayoutSetModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"layoutSet.", "layoutSetPrototypeUuid",
					FinderColumn.Type.STRING, "=", true, true,
					LayoutSet::getLayoutSetPrototypeUuid));

		_uniquePersistenceFinderByG_P = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_P",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"groupId", "privateLayout"}, 0, 0, false,
				LayoutSet::getGroupId, LayoutSet::isPrivateLayout),
			_SQL_SELECT_LAYOUTSET_WHERE, "",
			new FinderColumn<>(
				"layoutSet.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, LayoutSet::getGroupId),
			new FinderColumn<>(
				"layoutSet.", "privateLayout", FinderColumn.Type.BOOLEAN, "=",
				true, true, LayoutSet::isPrivateLayout));

		_collectionPersistenceFinderByC_L = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_L",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"companyId", "layoutSetPrototypeUuid"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_L",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "layoutSetPrototypeUuid"}, 0, 2,
				true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_L",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "layoutSetPrototypeUuid"}, 0, 2,
				false, null),
			_SQL_SELECT_LAYOUTSET_WHERE, _SQL_COUNT_LAYOUTSET_WHERE,
			LayoutSetModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"layoutSet.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, LayoutSet::getCompanyId),
			new FinderColumn<>(
				"layoutSet.", "layoutSetPrototypeUuid",
				FinderColumn.Type.STRING, "=", true, true,
				LayoutSet::getLayoutSetPrototypeUuid));

		_collectionPersistenceFinderByP_L = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByP_L",
				new String[] {
					Boolean.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"privateLayout", "logoId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByP_L",
				new String[] {Boolean.class.getName(), Long.class.getName()},
				new String[] {"privateLayout", "logoId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByP_L",
				new String[] {Boolean.class.getName(), Long.class.getName()},
				new String[] {"privateLayout", "logoId"}, false),
			_SQL_SELECT_LAYOUTSET_WHERE, _SQL_COUNT_LAYOUTSET_WHERE,
			LayoutSetModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"layoutSet.", "privateLayout", FinderColumn.Type.BOOLEAN, "=",
				true, true, LayoutSet::isPrivateLayout),
			new FinderColumn<>(
				"layoutSet.", "logoId", FinderColumn.Type.LONG, "=", true, true,
				LayoutSet::getLogoId));

		LayoutSetUtil.setPersistence(this);
	}

	public void destroy() {
		LayoutSetUtil.setPersistence(null);

		EntityCacheUtil.removeCache(LayoutSetImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		LayoutSetModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_LAYOUTSET =
		"SELECT layoutSet FROM LayoutSet layoutSet";

	private static final String _SQL_SELECT_LAYOUTSET_WHERE =
		"SELECT layoutSet FROM LayoutSet layoutSet WHERE ";

	private static final String _SQL_COUNT_LAYOUTSET_WHERE =
		"SELECT COUNT(layoutSet) FROM LayoutSet layoutSet WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No LayoutSet exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutSetPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"settings"});

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:934925156