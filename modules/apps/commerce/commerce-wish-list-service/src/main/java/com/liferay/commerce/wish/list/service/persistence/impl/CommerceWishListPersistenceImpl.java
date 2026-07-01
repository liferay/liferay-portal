/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.wish.list.service.persistence.impl;

import com.liferay.commerce.wish.list.exception.NoSuchWishListException;
import com.liferay.commerce.wish.list.model.CommerceWishList;
import com.liferay.commerce.wish.list.model.CommerceWishListTable;
import com.liferay.commerce.wish.list.model.impl.CommerceWishListImpl;
import com.liferay.commerce.wish.list.model.impl.CommerceWishListModelImpl;
import com.liferay.commerce.wish.list.service.persistence.CommerceWishListPersistence;
import com.liferay.commerce.wish.list.service.persistence.CommerceWishListUtil;
import com.liferay.commerce.wish.list.service.persistence.impl.constants.CommercePersistenceConstants;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the commerce wish list service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Andrea Di Giorgi
 * @generated
 */
@Component(service = CommerceWishListPersistence.class)
public class CommerceWishListPersistenceImpl
	extends BasePersistenceImpl<CommerceWishList, NoSuchWishListException>
	implements CommerceWishListPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CommerceWishListUtil</code> to access the commerce wish list persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CommerceWishListImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<CommerceWishList, NoSuchWishListException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the commerce wish lists where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceWishListModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce wish lists
	 * @param end the upper bound of the range of commerce wish lists (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce wish lists
	 */
	@Override
	public List<CommerceWishList> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CommerceWishList> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first commerce wish list in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce wish list
	 * @throws NoSuchWishListException if a matching commerce wish list could not be found
	 */
	@Override
	public CommerceWishList findByUuid_First(
			String uuid, OrderByComparator<CommerceWishList> orderByComparator)
		throws NoSuchWishListException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first commerce wish list in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce wish list, or <code>null</code> if a matching commerce wish list could not be found
	 */
	@Override
	public CommerceWishList fetchByUuid_First(
		String uuid, OrderByComparator<CommerceWishList> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the commerce wish lists where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of commerce wish lists where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching commerce wish lists
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private UniquePersistenceFinder<CommerceWishList, NoSuchWishListException>
		_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the commerce wish list where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchWishListException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching commerce wish list
	 * @throws NoSuchWishListException if a matching commerce wish list could not be found
	 */
	@Override
	public CommerceWishList findByUUID_G(String uuid, long groupId)
		throws NoSuchWishListException {

		return _uniquePersistenceFinderByUUID_G.find(
			finderCache, new Object[] {uuid, groupId});
	}

	/**
	 * Returns the commerce wish list where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce wish list, or <code>null</code> if a matching commerce wish list could not be found
	 */
	@Override
	public CommerceWishList fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			finderCache, new Object[] {uuid, groupId}, useFinderCache);
	}

	/**
	 * Removes the commerce wish list where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the commerce wish list that was removed
	 */
	@Override
	public CommerceWishList removeByUUID_G(String uuid, long groupId)
		throws NoSuchWishListException {

		CommerceWishList commerceWishList = findByUUID_G(uuid, groupId);

		return remove(commerceWishList);
	}

	/**
	 * Returns the number of commerce wish lists where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching commerce wish lists
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			finderCache, new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<CommerceWishList, NoSuchWishListException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the commerce wish lists where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceWishListModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce wish lists
	 * @param end the upper bound of the range of commerce wish lists (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce wish lists
	 */
	@Override
	public List<CommerceWishList> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CommerceWishList> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce wish list in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce wish list
	 * @throws NoSuchWishListException if a matching commerce wish list could not be found
	 */
	@Override
	public CommerceWishList findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CommerceWishList> orderByComparator)
		throws NoSuchWishListException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first commerce wish list in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce wish list, or <code>null</code> if a matching commerce wish list could not be found
	 */
	@Override
	public CommerceWishList fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CommerceWishList> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the commerce wish lists where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		_collectionPersistenceFinderByUuid_C.remove(
			finderCache, new Object[] {uuid, companyId});
	}

	/**
	 * Returns the number of commerce wish lists where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching commerce wish lists
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<CommerceWishList, NoSuchWishListException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the commerce wish lists where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceWishListModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of commerce wish lists
	 * @param end the upper bound of the range of commerce wish lists (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce wish lists
	 */
	@Override
	public List<CommerceWishList> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<CommerceWishList> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			finderCache, new Object[] {groupId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first commerce wish list in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce wish list
	 * @throws NoSuchWishListException if a matching commerce wish list could not be found
	 */
	@Override
	public CommerceWishList findByGroupId_First(
			long groupId, OrderByComparator<CommerceWishList> orderByComparator)
		throws NoSuchWishListException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Returns the first commerce wish list in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce wish list, or <code>null</code> if a matching commerce wish list could not be found
	 */
	@Override
	public CommerceWishList fetchByGroupId_First(
		long groupId, OrderByComparator<CommerceWishList> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			finderCache, new Object[] {groupId}, orderByComparator);
	}

	/**
	 * Removes all the commerce wish lists where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			finderCache, new Object[] {groupId});
	}

	/**
	 * Returns the number of commerce wish lists where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching commerce wish lists
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			finderCache, new Object[] {groupId});
	}

	private CollectionPersistenceFinder
		<CommerceWishList, NoSuchWishListException>
			_collectionPersistenceFinderByUserId;

	/**
	 * Returns an ordered range of all the commerce wish lists where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceWishListModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of commerce wish lists
	 * @param end the upper bound of the range of commerce wish lists (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce wish lists
	 */
	@Override
	public List<CommerceWishList> findByUserId(
		long userId, int start, int end,
		OrderByComparator<CommerceWishList> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUserId.find(
			finderCache, new Object[] {userId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first commerce wish list in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce wish list
	 * @throws NoSuchWishListException if a matching commerce wish list could not be found
	 */
	@Override
	public CommerceWishList findByUserId_First(
			long userId, OrderByComparator<CommerceWishList> orderByComparator)
		throws NoSuchWishListException {

		return _collectionPersistenceFinderByUserId.findFirst(
			finderCache, new Object[] {userId}, orderByComparator);
	}

	/**
	 * Returns the first commerce wish list in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce wish list, or <code>null</code> if a matching commerce wish list could not be found
	 */
	@Override
	public CommerceWishList fetchByUserId_First(
		long userId, OrderByComparator<CommerceWishList> orderByComparator) {

		return _collectionPersistenceFinderByUserId.fetchFirst(
			finderCache, new Object[] {userId}, orderByComparator);
	}

	/**
	 * Removes all the commerce wish lists where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	@Override
	public void removeByUserId(long userId) {
		_collectionPersistenceFinderByUserId.remove(
			finderCache, new Object[] {userId});
	}

	/**
	 * Returns the number of commerce wish lists where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching commerce wish lists
	 */
	@Override
	public int countByUserId(long userId) {
		return _collectionPersistenceFinderByUserId.count(
			finderCache, new Object[] {userId});
	}

	private CollectionPersistenceFinder
		<CommerceWishList, NoSuchWishListException>
			_collectionPersistenceFinderByG_U;

	/**
	 * Returns an ordered range of all the commerce wish lists where groupId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceWishListModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of commerce wish lists
	 * @param end the upper bound of the range of commerce wish lists (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce wish lists
	 */
	@Override
	public List<CommerceWishList> findByG_U(
		long groupId, long userId, int start, int end,
		OrderByComparator<CommerceWishList> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_U.find(
			finderCache, new Object[] {groupId, userId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce wish list in the ordered set where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce wish list
	 * @throws NoSuchWishListException if a matching commerce wish list could not be found
	 */
	@Override
	public CommerceWishList findByG_U_First(
			long groupId, long userId,
			OrderByComparator<CommerceWishList> orderByComparator)
		throws NoSuchWishListException {

		return _collectionPersistenceFinderByG_U.findFirst(
			finderCache, new Object[] {groupId, userId}, orderByComparator);
	}

	/**
	 * Returns the first commerce wish list in the ordered set where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce wish list, or <code>null</code> if a matching commerce wish list could not be found
	 */
	@Override
	public CommerceWishList fetchByG_U_First(
		long groupId, long userId,
		OrderByComparator<CommerceWishList> orderByComparator) {

		return _collectionPersistenceFinderByG_U.fetchFirst(
			finderCache, new Object[] {groupId, userId}, orderByComparator);
	}

	/**
	 * Removes all the commerce wish lists where groupId = &#63; and userId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 */
	@Override
	public void removeByG_U(long groupId, long userId) {
		_collectionPersistenceFinderByG_U.remove(
			finderCache, new Object[] {groupId, userId});
	}

	/**
	 * Returns the number of commerce wish lists where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @return the number of matching commerce wish lists
	 */
	@Override
	public int countByG_U(long groupId, long userId) {
		return _collectionPersistenceFinderByG_U.count(
			finderCache, new Object[] {groupId, userId});
	}

	private CollectionPersistenceFinder
		<CommerceWishList, NoSuchWishListException>
			_collectionPersistenceFinderByU_LtC;

	/**
	 * Returns all the commerce wish lists where userId = &#63; and createDate &lt; &#63;.
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 * @return the matching commerce wish lists
	 */
	@Override
	public List<CommerceWishList> findByU_LtC(long userId, Date createDate) {
		return findByU_LtC(
			userId, createDate, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the commerce wish lists where userId = &#63; and createDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceWishListModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param start the lower bound of the range of commerce wish lists
	 * @param end the upper bound of the range of commerce wish lists (not inclusive)
	 * @return the range of matching commerce wish lists
	 */
	@Override
	public List<CommerceWishList> findByU_LtC(
		long userId, Date createDate, int start, int end) {

		return findByU_LtC(userId, createDate, start, end, null);
	}

	/**
	 * Returns an ordered range of all the commerce wish lists where userId = &#63; and createDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceWishListModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param start the lower bound of the range of commerce wish lists
	 * @param end the upper bound of the range of commerce wish lists (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce wish lists
	 */
	@Override
	public List<CommerceWishList> findByU_LtC(
		long userId, Date createDate, int start, int end,
		OrderByComparator<CommerceWishList> orderByComparator) {

		return findByU_LtC(
			userId, createDate, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the commerce wish lists where userId = &#63; and createDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceWishListModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param start the lower bound of the range of commerce wish lists
	 * @param end the upper bound of the range of commerce wish lists (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce wish lists
	 */
	@Override
	public List<CommerceWishList> findByU_LtC(
		long userId, Date createDate, int start, int end,
		OrderByComparator<CommerceWishList> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByU_LtC.find(
			finderCache, new Object[] {userId, createDate}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce wish list in the ordered set where userId = &#63; and createDate &lt; &#63;.
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce wish list
	 * @throws NoSuchWishListException if a matching commerce wish list could not be found
	 */
	@Override
	public CommerceWishList findByU_LtC_First(
			long userId, Date createDate,
			OrderByComparator<CommerceWishList> orderByComparator)
		throws NoSuchWishListException {

		return _collectionPersistenceFinderByU_LtC.findFirst(
			finderCache, new Object[] {userId, createDate}, orderByComparator);
	}

	/**
	 * Returns the first commerce wish list in the ordered set where userId = &#63; and createDate &lt; &#63;.
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce wish list, or <code>null</code> if a matching commerce wish list could not be found
	 */
	@Override
	public CommerceWishList fetchByU_LtC_First(
		long userId, Date createDate,
		OrderByComparator<CommerceWishList> orderByComparator) {

		return _collectionPersistenceFinderByU_LtC.fetchFirst(
			finderCache, new Object[] {userId, createDate}, orderByComparator);
	}

	/**
	 * Removes all the commerce wish lists where userId = &#63; and createDate &lt; &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 */
	@Override
	public void removeByU_LtC(long userId, Date createDate) {
		_collectionPersistenceFinderByU_LtC.remove(
			finderCache, new Object[] {userId, createDate});
	}

	/**
	 * Returns the number of commerce wish lists where userId = &#63; and createDate &lt; &#63;.
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 * @return the number of matching commerce wish lists
	 */
	@Override
	public int countByU_LtC(long userId, Date createDate) {
		return _collectionPersistenceFinderByU_LtC.count(
			finderCache, new Object[] {userId, createDate});
	}

	private CollectionPersistenceFinder
		<CommerceWishList, NoSuchWishListException>
			_collectionPersistenceFinderByG_U_D;

	/**
	 * Returns an ordered range of all the commerce wish lists where groupId = &#63; and userId = &#63; and defaultWishList = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CommerceWishListModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param defaultWishList the default wish list
	 * @param start the lower bound of the range of commerce wish lists
	 * @param end the upper bound of the range of commerce wish lists (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce wish lists
	 */
	@Override
	public List<CommerceWishList> findByG_U_D(
		long groupId, long userId, boolean defaultWishList, int start, int end,
		OrderByComparator<CommerceWishList> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_U_D.find(
			finderCache, new Object[] {groupId, userId, defaultWishList}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce wish list in the ordered set where groupId = &#63; and userId = &#63; and defaultWishList = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param defaultWishList the default wish list
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce wish list
	 * @throws NoSuchWishListException if a matching commerce wish list could not be found
	 */
	@Override
	public CommerceWishList findByG_U_D_First(
			long groupId, long userId, boolean defaultWishList,
			OrderByComparator<CommerceWishList> orderByComparator)
		throws NoSuchWishListException {

		return _collectionPersistenceFinderByG_U_D.findFirst(
			finderCache, new Object[] {groupId, userId, defaultWishList},
			orderByComparator);
	}

	/**
	 * Returns the first commerce wish list in the ordered set where groupId = &#63; and userId = &#63; and defaultWishList = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param defaultWishList the default wish list
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce wish list, or <code>null</code> if a matching commerce wish list could not be found
	 */
	@Override
	public CommerceWishList fetchByG_U_D_First(
		long groupId, long userId, boolean defaultWishList,
		OrderByComparator<CommerceWishList> orderByComparator) {

		return _collectionPersistenceFinderByG_U_D.fetchFirst(
			finderCache, new Object[] {groupId, userId, defaultWishList},
			orderByComparator);
	}

	/**
	 * Removes all the commerce wish lists where groupId = &#63; and userId = &#63; and defaultWishList = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param defaultWishList the default wish list
	 */
	@Override
	public void removeByG_U_D(
		long groupId, long userId, boolean defaultWishList) {

		_collectionPersistenceFinderByG_U_D.remove(
			finderCache, new Object[] {groupId, userId, defaultWishList});
	}

	/**
	 * Returns the number of commerce wish lists where groupId = &#63; and userId = &#63; and defaultWishList = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param defaultWishList the default wish list
	 * @return the number of matching commerce wish lists
	 */
	@Override
	public int countByG_U_D(
		long groupId, long userId, boolean defaultWishList) {

		return _collectionPersistenceFinderByG_U_D.count(
			finderCache, new Object[] {groupId, userId, defaultWishList});
	}

	public CommerceWishListPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(CommerceWishList.class);

		setModelImplClass(CommerceWishListImpl.class);
		setModelPKClass(long.class);

		setTable(CommerceWishListTable.INSTANCE);
	}

	/**
	 * Creates a new commerce wish list with the primary key. Does not add the commerce wish list to the database.
	 *
	 * @param commerceWishListId the primary key for the new commerce wish list
	 * @return the new commerce wish list
	 */
	@Override
	public CommerceWishList create(long commerceWishListId) {
		CommerceWishList commerceWishList = new CommerceWishListImpl();

		commerceWishList.setNew(true);
		commerceWishList.setPrimaryKey(commerceWishListId);

		String uuid = PortalUUIDUtil.generate();

		commerceWishList.setUuid(uuid);

		commerceWishList.setCompanyId(CompanyThreadLocal.getCompanyId());

		return commerceWishList;
	}

	/**
	 * Removes the commerce wish list with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceWishListId the primary key of the commerce wish list
	 * @return the commerce wish list that was removed
	 * @throws NoSuchWishListException if a commerce wish list with the primary key could not be found
	 */
	@Override
	public CommerceWishList remove(long commerceWishListId)
		throws NoSuchWishListException {

		return remove((Serializable)commerceWishListId);
	}

	@Override
	protected CommerceWishList removeImpl(CommerceWishList commerceWishList) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(commerceWishList)) {
				commerceWishList = (CommerceWishList)session.get(
					CommerceWishListImpl.class,
					commerceWishList.getPrimaryKeyObj());
			}

			if (commerceWishList != null) {
				session.delete(commerceWishList);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (commerceWishList != null) {
			clearCache(commerceWishList);
		}

		return commerceWishList;
	}

	@Override
	public CommerceWishList updateImpl(CommerceWishList commerceWishList) {
		boolean isNew = commerceWishList.isNew();

		if (!(commerceWishList instanceof CommerceWishListModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(commerceWishList.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					commerceWishList);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in commerceWishList proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom CommerceWishList implementation " +
					commerceWishList.getClass());
		}

		CommerceWishListModelImpl commerceWishListModelImpl =
			(CommerceWishListModelImpl)commerceWishList;

		if (Validator.isNull(commerceWishList.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			commerceWishList.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (commerceWishList.getCreateDate() == null)) {
			if (serviceContext == null) {
				commerceWishList.setCreateDate(date);
			}
			else {
				commerceWishList.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!commerceWishListModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				commerceWishList.setModifiedDate(date);
			}
			else {
				commerceWishList.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(commerceWishList);
			}
			else {
				commerceWishList = (CommerceWishList)session.merge(
					commerceWishList);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(commerceWishList, false);

		if (isNew) {
			commerceWishList.setNew(false);
		}

		commerceWishList.resetOriginalValues();

		return commerceWishList;
	}

	/**
	 * Returns the commerce wish list with the primary key or throws a <code>NoSuchWishListException</code> if it could not be found.
	 *
	 * @param commerceWishListId the primary key of the commerce wish list
	 * @return the commerce wish list
	 * @throws NoSuchWishListException if a commerce wish list with the primary key could not be found
	 */
	@Override
	public CommerceWishList findByPrimaryKey(long commerceWishListId)
		throws NoSuchWishListException {

		return findByPrimaryKey((Serializable)commerceWishListId);
	}

	/**
	 * Returns the commerce wish list with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceWishListId the primary key of the commerce wish list
	 * @return the commerce wish list, or <code>null</code> if a commerce wish list with the primary key could not be found
	 */
	@Override
	public CommerceWishList fetchByPrimaryKey(long commerceWishListId) {
		return fetchByPrimaryKey((Serializable)commerceWishListId);
	}

	@Override
	public Set<String> getBadColumnNames() {
		return _badColumnNames;
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "commerceWishListId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COMMERCEWISHLIST;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return CommerceWishListModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the commerce wish list persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByUuid = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid",
				new String[] {
					String.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"uuid_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid",
				new String[] {String.class.getName()}, new String[] {"uuid_"},
				0, 1, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid",
				new String[] {String.class.getName()}, new String[] {"uuid_"},
				0, 1, false, null),
			_SQL_SELECT_COMMERCEWISHLIST_WHERE,
			_SQL_COUNT_COMMERCEWISHLIST_WHERE,
			CommerceWishListModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"", null,
			new FinderColumn<>(
				"commerceWishList.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, CommerceWishList::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(CommerceWishList::getUuid),
				CommerceWishList::getGroupId),
			_SQL_SELECT_COMMERCEWISHLIST_WHERE, "",
			new FinderColumn<>(
				"commerceWishList.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, CommerceWishList::getUuid),
			new FinderColumn<>(
				"commerceWishList.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, CommerceWishList::getGroupId));

		_collectionPersistenceFinderByUuid_C =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid_C",
					new String[] {
						String.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"uuid_", "companyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid_C",
					new String[] {String.class.getName(), Long.class.getName()},
					new String[] {"uuid_", "companyId"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid_C",
					new String[] {String.class.getName(), Long.class.getName()},
					new String[] {"uuid_", "companyId"}, 0, 1, false, null),
				_SQL_SELECT_COMMERCEWISHLIST_WHERE,
				_SQL_COUNT_COMMERCEWISHLIST_WHERE,
				CommerceWishListModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"commerceWishList.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					CommerceWishList::getUuid),
				new FinderColumn<>(
					"commerceWishList.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, CommerceWishList::getCompanyId));

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
				_SQL_SELECT_COMMERCEWISHLIST_WHERE,
				_SQL_COUNT_COMMERCEWISHLIST_WHERE,
				CommerceWishListModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"commerceWishList.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, CommerceWishList::getGroupId));

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
				_SQL_SELECT_COMMERCEWISHLIST_WHERE,
				_SQL_COUNT_COMMERCEWISHLIST_WHERE,
				CommerceWishListModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"commerceWishList.", "userId", FinderColumn.Type.LONG, "=",
					true, true, CommerceWishList::getUserId));

		_collectionPersistenceFinderByG_U = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_U",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"groupId", "userId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_U",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"groupId", "userId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_U",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"groupId", "userId"}, false),
			_SQL_SELECT_COMMERCEWISHLIST_WHERE,
			_SQL_COUNT_COMMERCEWISHLIST_WHERE,
			CommerceWishListModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"", null,
			new FinderColumn<>(
				"commerceWishList.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, CommerceWishList::getGroupId),
			new FinderColumn<>(
				"commerceWishList.", "userId", FinderColumn.Type.LONG, "=",
				true, true, CommerceWishList::getUserId));

		_collectionPersistenceFinderByU_LtC = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByU_LtC",
				new String[] {
					Long.class.getName(), Date.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"userId", "createDate"}, true),
			null,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByU_LtC",
				new String[] {Long.class.getName(), Date.class.getName()},
				new String[] {"userId", "createDate"}, false),
			_SQL_SELECT_COMMERCEWISHLIST_WHERE,
			_SQL_COUNT_COMMERCEWISHLIST_WHERE,
			CommerceWishListModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"", null,
			new FinderColumn<>(
				"commerceWishList.", "userId", FinderColumn.Type.LONG, "=",
				true, true, CommerceWishList::getUserId),
			new FinderColumn<>(
				"commerceWishList.", "createDate", FinderColumn.Type.DATE, "<",
				true, true, CommerceWishList::getCreateDate));

		_collectionPersistenceFinderByG_U_D = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_U_D",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Boolean.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"groupId", "userId", "defaultWishList"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_U_D",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Boolean.class.getName()
				},
				new String[] {"groupId", "userId", "defaultWishList"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_U_D",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Boolean.class.getName()
				},
				new String[] {"groupId", "userId", "defaultWishList"}, false),
			_SQL_SELECT_COMMERCEWISHLIST_WHERE,
			_SQL_COUNT_COMMERCEWISHLIST_WHERE,
			CommerceWishListModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"", null,
			new FinderColumn<>(
				"commerceWishList.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, CommerceWishList::getGroupId),
			new FinderColumn<>(
				"commerceWishList.", "userId", FinderColumn.Type.LONG, "=",
				true, true, CommerceWishList::getUserId),
			new FinderColumn<>(
				"commerceWishList.", "defaultWishList",
				FinderColumn.Type.BOOLEAN, "=", true, true,
				CommerceWishList::isDefaultWishList));

		CommerceWishListUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		CommerceWishListUtil.setPersistence(null);

		entityCache.removeCache(CommerceWishListImpl.class.getName());
	}

	@Override
	@Reference(
		target = CommercePersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = CommercePersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = CommercePersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _ENTITY_ALIAS_PREFIX =
		CommerceWishListModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_COMMERCEWISHLIST =
		"SELECT commerceWishList FROM CommerceWishList commerceWishList";

	private static final String _SQL_SELECT_COMMERCEWISHLIST_WHERE =
		"SELECT commerceWishList FROM CommerceWishList commerceWishList WHERE ";

	private static final String _SQL_COUNT_COMMERCEWISHLIST_WHERE =
		"SELECT COUNT(commerceWishList) FROM CommerceWishList commerceWishList WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1783963250