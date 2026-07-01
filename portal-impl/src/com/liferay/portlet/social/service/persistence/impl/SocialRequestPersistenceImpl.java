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
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portlet.social.model.impl.SocialRequestImpl;
import com.liferay.portlet.social.model.impl.SocialRequestModelImpl;
import com.liferay.social.kernel.exception.NoSuchRequestException;
import com.liferay.social.kernel.model.SocialRequest;
import com.liferay.social.kernel.model.SocialRequestTable;
import com.liferay.social.kernel.service.persistence.SocialRequestPersistence;
import com.liferay.social.kernel.service.persistence.SocialRequestUtil;

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
 * The persistence implementation for the social request service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class SocialRequestPersistenceImpl
	extends BasePersistenceImpl<SocialRequest, NoSuchRequestException>
	implements SocialRequestPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>SocialRequestUtil</code> to access the social request persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		SocialRequestImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<SocialRequest, NoSuchRequestException>
		_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the social requests where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SocialRequestModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of social requests
	 * @param end the upper bound of the range of social requests (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social requests
	 */
	@Override
	public List<SocialRequest> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<SocialRequest> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first social request in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social request
	 * @throws NoSuchRequestException if a matching social request could not be found
	 */
	@Override
	public SocialRequest findByUuid_First(
			String uuid, OrderByComparator<SocialRequest> orderByComparator)
		throws NoSuchRequestException {

		return _collectionPersistenceFinderByUuid.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid},
			orderByComparator);
	}

	/**
	 * Returns the first social request in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social request, or <code>null</code> if a matching social request could not be found
	 */
	@Override
	public SocialRequest fetchByUuid_First(
		String uuid, OrderByComparator<SocialRequest> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid},
			orderByComparator);
	}

	/**
	 * Removes all the social requests where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	/**
	 * Returns the number of social requests where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching social requests
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	private UniquePersistenceFinder<SocialRequest, NoSuchRequestException>
		_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the social request where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchRequestException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching social request
	 * @throws NoSuchRequestException if a matching social request could not be found
	 */
	@Override
	public SocialRequest findByUUID_G(String uuid, long groupId)
		throws NoSuchRequestException {

		return _uniquePersistenceFinderByUUID_G.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, groupId});
	}

	/**
	 * Returns the social request where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching social request, or <code>null</code> if a matching social request could not be found
	 */
	@Override
	public SocialRequest fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, groupId},
			useFinderCache);
	}

	/**
	 * Removes the social request where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the social request that was removed
	 */
	@Override
	public SocialRequest removeByUUID_G(String uuid, long groupId)
		throws NoSuchRequestException {

		SocialRequest socialRequest = findByUUID_G(uuid, groupId);

		return remove(socialRequest);
	}

	/**
	 * Returns the number of social requests where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching social requests
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder<SocialRequest, NoSuchRequestException>
		_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the social requests where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SocialRequestModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of social requests
	 * @param end the upper bound of the range of social requests (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social requests
	 */
	@Override
	public List<SocialRequest> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<SocialRequest> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first social request in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social request
	 * @throws NoSuchRequestException if a matching social request could not be found
	 */
	@Override
	public SocialRequest findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<SocialRequest> orderByComparator)
		throws NoSuchRequestException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			orderByComparator);
	}

	/**
	 * Returns the first social request in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social request, or <code>null</code> if a matching social request could not be found
	 */
	@Override
	public SocialRequest fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<SocialRequest> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			orderByComparator);
	}

	/**
	 * Removes all the social requests where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		_collectionPersistenceFinderByUuid_C.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId});
	}

	/**
	 * Returns the number of social requests where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching social requests
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder<SocialRequest, NoSuchRequestException>
		_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the social requests where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SocialRequestModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of social requests
	 * @param end the upper bound of the range of social requests (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social requests
	 */
	@Override
	public List<SocialRequest> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<SocialRequest> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first social request in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social request
	 * @throws NoSuchRequestException if a matching social request could not be found
	 */
	@Override
	public SocialRequest findByCompanyId_First(
			long companyId, OrderByComparator<SocialRequest> orderByComparator)
		throws NoSuchRequestException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			orderByComparator);
	}

	/**
	 * Returns the first social request in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social request, or <code>null</code> if a matching social request could not be found
	 */
	@Override
	public SocialRequest fetchByCompanyId_First(
		long companyId, OrderByComparator<SocialRequest> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			orderByComparator);
	}

	/**
	 * Removes all the social requests where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId});
	}

	/**
	 * Returns the number of social requests where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching social requests
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId});
	}

	private CollectionPersistenceFinder<SocialRequest, NoSuchRequestException>
		_collectionPersistenceFinderByUserId;

	/**
	 * Returns an ordered range of all the social requests where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SocialRequestModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of social requests
	 * @param end the upper bound of the range of social requests (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social requests
	 */
	@Override
	public List<SocialRequest> findByUserId(
		long userId, int start, int end,
		OrderByComparator<SocialRequest> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUserId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {userId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first social request in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social request
	 * @throws NoSuchRequestException if a matching social request could not be found
	 */
	@Override
	public SocialRequest findByUserId_First(
			long userId, OrderByComparator<SocialRequest> orderByComparator)
		throws NoSuchRequestException {

		return _collectionPersistenceFinderByUserId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {userId},
			orderByComparator);
	}

	/**
	 * Returns the first social request in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social request, or <code>null</code> if a matching social request could not be found
	 */
	@Override
	public SocialRequest fetchByUserId_First(
		long userId, OrderByComparator<SocialRequest> orderByComparator) {

		return _collectionPersistenceFinderByUserId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {userId},
			orderByComparator);
	}

	/**
	 * Removes all the social requests where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	@Override
	public void removeByUserId(long userId) {
		_collectionPersistenceFinderByUserId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {userId});
	}

	/**
	 * Returns the number of social requests where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching social requests
	 */
	@Override
	public int countByUserId(long userId) {
		return _collectionPersistenceFinderByUserId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {userId});
	}

	private CollectionPersistenceFinder<SocialRequest, NoSuchRequestException>
		_collectionPersistenceFinderByReceiverUserId;

	/**
	 * Returns an ordered range of all the social requests where receiverUserId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SocialRequestModelImpl</code>.
	 * </p>
	 *
	 * @param receiverUserId the receiver user ID
	 * @param start the lower bound of the range of social requests
	 * @param end the upper bound of the range of social requests (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social requests
	 */
	@Override
	public List<SocialRequest> findByReceiverUserId(
		long receiverUserId, int start, int end,
		OrderByComparator<SocialRequest> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByReceiverUserId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {receiverUserId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first social request in the ordered set where receiverUserId = &#63;.
	 *
	 * @param receiverUserId the receiver user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social request
	 * @throws NoSuchRequestException if a matching social request could not be found
	 */
	@Override
	public SocialRequest findByReceiverUserId_First(
			long receiverUserId,
			OrderByComparator<SocialRequest> orderByComparator)
		throws NoSuchRequestException {

		return _collectionPersistenceFinderByReceiverUserId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {receiverUserId},
			orderByComparator);
	}

	/**
	 * Returns the first social request in the ordered set where receiverUserId = &#63;.
	 *
	 * @param receiverUserId the receiver user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social request, or <code>null</code> if a matching social request could not be found
	 */
	@Override
	public SocialRequest fetchByReceiverUserId_First(
		long receiverUserId,
		OrderByComparator<SocialRequest> orderByComparator) {

		return _collectionPersistenceFinderByReceiverUserId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {receiverUserId},
			orderByComparator);
	}

	/**
	 * Removes all the social requests where receiverUserId = &#63; from the database.
	 *
	 * @param receiverUserId the receiver user ID
	 */
	@Override
	public void removeByReceiverUserId(long receiverUserId) {
		_collectionPersistenceFinderByReceiverUserId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {receiverUserId});
	}

	/**
	 * Returns the number of social requests where receiverUserId = &#63;.
	 *
	 * @param receiverUserId the receiver user ID
	 * @return the number of matching social requests
	 */
	@Override
	public int countByReceiverUserId(long receiverUserId) {
		return _collectionPersistenceFinderByReceiverUserId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {receiverUserId});
	}

	private CollectionPersistenceFinder<SocialRequest, NoSuchRequestException>
		_collectionPersistenceFinderByU_S;

	/**
	 * Returns an ordered range of all the social requests where userId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SocialRequestModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of social requests
	 * @param end the upper bound of the range of social requests (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social requests
	 */
	@Override
	public List<SocialRequest> findByU_S(
		long userId, int status, int start, int end,
		OrderByComparator<SocialRequest> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByU_S.find(
			FinderCacheUtil.getFinderCache(), new Object[] {userId, status},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first social request in the ordered set where userId = &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social request
	 * @throws NoSuchRequestException if a matching social request could not be found
	 */
	@Override
	public SocialRequest findByU_S_First(
			long userId, int status,
			OrderByComparator<SocialRequest> orderByComparator)
		throws NoSuchRequestException {

		return _collectionPersistenceFinderByU_S.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {userId, status},
			orderByComparator);
	}

	/**
	 * Returns the first social request in the ordered set where userId = &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social request, or <code>null</code> if a matching social request could not be found
	 */
	@Override
	public SocialRequest fetchByU_S_First(
		long userId, int status,
		OrderByComparator<SocialRequest> orderByComparator) {

		return _collectionPersistenceFinderByU_S.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {userId, status},
			orderByComparator);
	}

	/**
	 * Removes all the social requests where userId = &#63; and status = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param status the status
	 */
	@Override
	public void removeByU_S(long userId, int status) {
		_collectionPersistenceFinderByU_S.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {userId, status});
	}

	/**
	 * Returns the number of social requests where userId = &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param status the status
	 * @return the number of matching social requests
	 */
	@Override
	public int countByU_S(long userId, int status) {
		return _collectionPersistenceFinderByU_S.count(
			FinderCacheUtil.getFinderCache(), new Object[] {userId, status});
	}

	private CollectionPersistenceFinder<SocialRequest, NoSuchRequestException>
		_collectionPersistenceFinderByC_C;

	/**
	 * Returns an ordered range of all the social requests where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SocialRequestModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of social requests
	 * @param end the upper bound of the range of social requests (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social requests
	 */
	@Override
	public List<SocialRequest> findByC_C(
		long classNameId, long classPK, int start, int end,
		OrderByComparator<SocialRequest> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first social request in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social request
	 * @throws NoSuchRequestException if a matching social request could not be found
	 */
	@Override
	public SocialRequest findByC_C_First(
			long classNameId, long classPK,
			OrderByComparator<SocialRequest> orderByComparator)
		throws NoSuchRequestException {

		return _collectionPersistenceFinderByC_C.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK}, orderByComparator);
	}

	/**
	 * Returns the first social request in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social request, or <code>null</code> if a matching social request could not be found
	 */
	@Override
	public SocialRequest fetchByC_C_First(
		long classNameId, long classPK,
		OrderByComparator<SocialRequest> orderByComparator) {

		return _collectionPersistenceFinderByC_C.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK}, orderByComparator);
	}

	/**
	 * Removes all the social requests where classNameId = &#63; and classPK = &#63; from the database.
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
	 * Returns the number of social requests where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching social requests
	 */
	@Override
	public int countByC_C(long classNameId, long classPK) {
		return _collectionPersistenceFinderByC_C.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK});
	}

	private CollectionPersistenceFinder<SocialRequest, NoSuchRequestException>
		_collectionPersistenceFinderByR_S;

	/**
	 * Returns an ordered range of all the social requests where receiverUserId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SocialRequestModelImpl</code>.
	 * </p>
	 *
	 * @param receiverUserId the receiver user ID
	 * @param status the status
	 * @param start the lower bound of the range of social requests
	 * @param end the upper bound of the range of social requests (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social requests
	 */
	@Override
	public List<SocialRequest> findByR_S(
		long receiverUserId, int status, int start, int end,
		OrderByComparator<SocialRequest> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByR_S.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {receiverUserId, status}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first social request in the ordered set where receiverUserId = &#63; and status = &#63;.
	 *
	 * @param receiverUserId the receiver user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social request
	 * @throws NoSuchRequestException if a matching social request could not be found
	 */
	@Override
	public SocialRequest findByR_S_First(
			long receiverUserId, int status,
			OrderByComparator<SocialRequest> orderByComparator)
		throws NoSuchRequestException {

		return _collectionPersistenceFinderByR_S.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {receiverUserId, status}, orderByComparator);
	}

	/**
	 * Returns the first social request in the ordered set where receiverUserId = &#63; and status = &#63;.
	 *
	 * @param receiverUserId the receiver user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social request, or <code>null</code> if a matching social request could not be found
	 */
	@Override
	public SocialRequest fetchByR_S_First(
		long receiverUserId, int status,
		OrderByComparator<SocialRequest> orderByComparator) {

		return _collectionPersistenceFinderByR_S.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {receiverUserId, status}, orderByComparator);
	}

	/**
	 * Removes all the social requests where receiverUserId = &#63; and status = &#63; from the database.
	 *
	 * @param receiverUserId the receiver user ID
	 * @param status the status
	 */
	@Override
	public void removeByR_S(long receiverUserId, int status) {
		_collectionPersistenceFinderByR_S.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {receiverUserId, status});
	}

	/**
	 * Returns the number of social requests where receiverUserId = &#63; and status = &#63;.
	 *
	 * @param receiverUserId the receiver user ID
	 * @param status the status
	 * @return the number of matching social requests
	 */
	@Override
	public int countByR_S(long receiverUserId, int status) {
		return _collectionPersistenceFinderByR_S.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {receiverUserId, status});
	}

	private UniquePersistenceFinder<SocialRequest, NoSuchRequestException>
		_uniquePersistenceFinderByU_C_C_T_R;

	/**
	 * Returns the social request where userId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and receiverUserId = &#63; or throws a <code>NoSuchRequestException</code> if it could not be found.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param receiverUserId the receiver user ID
	 * @return the matching social request
	 * @throws NoSuchRequestException if a matching social request could not be found
	 */
	@Override
	public SocialRequest findByU_C_C_T_R(
			long userId, long classNameId, long classPK, int type,
			long receiverUserId)
		throws NoSuchRequestException {

		return _uniquePersistenceFinderByU_C_C_T_R.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, classNameId, classPK, type, receiverUserId});
	}

	/**
	 * Returns the social request where userId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and receiverUserId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param receiverUserId the receiver user ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching social request, or <code>null</code> if a matching social request could not be found
	 */
	@Override
	public SocialRequest fetchByU_C_C_T_R(
		long userId, long classNameId, long classPK, int type,
		long receiverUserId, boolean useFinderCache) {

		return _uniquePersistenceFinderByU_C_C_T_R.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, classNameId, classPK, type, receiverUserId},
			useFinderCache);
	}

	/**
	 * Removes the social request where userId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and receiverUserId = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param receiverUserId the receiver user ID
	 * @return the social request that was removed
	 */
	@Override
	public SocialRequest removeByU_C_C_T_R(
			long userId, long classNameId, long classPK, int type,
			long receiverUserId)
		throws NoSuchRequestException {

		SocialRequest socialRequest = findByU_C_C_T_R(
			userId, classNameId, classPK, type, receiverUserId);

		return remove(socialRequest);
	}

	/**
	 * Returns the number of social requests where userId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and receiverUserId = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param receiverUserId the receiver user ID
	 * @return the number of matching social requests
	 */
	@Override
	public int countByU_C_C_T_R(
		long userId, long classNameId, long classPK, int type,
		long receiverUserId) {

		return _uniquePersistenceFinderByU_C_C_T_R.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, classNameId, classPK, type, receiverUserId});
	}

	private CollectionPersistenceFinder<SocialRequest, NoSuchRequestException>
		_collectionPersistenceFinderByU_C_C_T_S;

	/**
	 * Returns an ordered range of all the social requests where userId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SocialRequestModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of social requests
	 * @param end the upper bound of the range of social requests (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social requests
	 */
	@Override
	public List<SocialRequest> findByU_C_C_T_S(
		long userId, long classNameId, long classPK, int type, int status,
		int start, int end, OrderByComparator<SocialRequest> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByU_C_C_T_S.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, classNameId, classPK, type, status}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first social request in the ordered set where userId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social request
	 * @throws NoSuchRequestException if a matching social request could not be found
	 */
	@Override
	public SocialRequest findByU_C_C_T_S_First(
			long userId, long classNameId, long classPK, int type, int status,
			OrderByComparator<SocialRequest> orderByComparator)
		throws NoSuchRequestException {

		return _collectionPersistenceFinderByU_C_C_T_S.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, classNameId, classPK, type, status},
			orderByComparator);
	}

	/**
	 * Returns the first social request in the ordered set where userId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social request, or <code>null</code> if a matching social request could not be found
	 */
	@Override
	public SocialRequest fetchByU_C_C_T_S_First(
		long userId, long classNameId, long classPK, int type, int status,
		OrderByComparator<SocialRequest> orderByComparator) {

		return _collectionPersistenceFinderByU_C_C_T_S.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, classNameId, classPK, type, status},
			orderByComparator);
	}

	/**
	 * Removes all the social requests where userId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and status = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param status the status
	 */
	@Override
	public void removeByU_C_C_T_S(
		long userId, long classNameId, long classPK, int type, int status) {

		_collectionPersistenceFinderByU_C_C_T_S.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, classNameId, classPK, type, status});
	}

	/**
	 * Returns the number of social requests where userId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param status the status
	 * @return the number of matching social requests
	 */
	@Override
	public int countByU_C_C_T_S(
		long userId, long classNameId, long classPK, int type, int status) {

		return _collectionPersistenceFinderByU_C_C_T_S.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, classNameId, classPK, type, status});
	}

	private CollectionPersistenceFinder<SocialRequest, NoSuchRequestException>
		_collectionPersistenceFinderByC_C_T_R_S;

	/**
	 * Returns an ordered range of all the social requests where classNameId = &#63; and classPK = &#63; and type = &#63; and receiverUserId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SocialRequestModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param receiverUserId the receiver user ID
	 * @param status the status
	 * @param start the lower bound of the range of social requests
	 * @param end the upper bound of the range of social requests (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social requests
	 */
	@Override
	public List<SocialRequest> findByC_C_T_R_S(
		long classNameId, long classPK, int type, long receiverUserId,
		int status, int start, int end,
		OrderByComparator<SocialRequest> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C_T_R_S.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK, type, receiverUserId, status},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first social request in the ordered set where classNameId = &#63; and classPK = &#63; and type = &#63; and receiverUserId = &#63; and status = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param receiverUserId the receiver user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social request
	 * @throws NoSuchRequestException if a matching social request could not be found
	 */
	@Override
	public SocialRequest findByC_C_T_R_S_First(
			long classNameId, long classPK, int type, long receiverUserId,
			int status, OrderByComparator<SocialRequest> orderByComparator)
		throws NoSuchRequestException {

		return _collectionPersistenceFinderByC_C_T_R_S.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK, type, receiverUserId, status},
			orderByComparator);
	}

	/**
	 * Returns the first social request in the ordered set where classNameId = &#63; and classPK = &#63; and type = &#63; and receiverUserId = &#63; and status = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param receiverUserId the receiver user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social request, or <code>null</code> if a matching social request could not be found
	 */
	@Override
	public SocialRequest fetchByC_C_T_R_S_First(
		long classNameId, long classPK, int type, long receiverUserId,
		int status, OrderByComparator<SocialRequest> orderByComparator) {

		return _collectionPersistenceFinderByC_C_T_R_S.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK, type, receiverUserId, status},
			orderByComparator);
	}

	/**
	 * Removes all the social requests where classNameId = &#63; and classPK = &#63; and type = &#63; and receiverUserId = &#63; and status = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param receiverUserId the receiver user ID
	 * @param status the status
	 */
	@Override
	public void removeByC_C_T_R_S(
		long classNameId, long classPK, int type, long receiverUserId,
		int status) {

		_collectionPersistenceFinderByC_C_T_R_S.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK, type, receiverUserId, status});
	}

	/**
	 * Returns the number of social requests where classNameId = &#63; and classPK = &#63; and type = &#63; and receiverUserId = &#63; and status = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param receiverUserId the receiver user ID
	 * @param status the status
	 * @return the number of matching social requests
	 */
	@Override
	public int countByC_C_T_R_S(
		long classNameId, long classPK, int type, long receiverUserId,
		int status) {

		return _collectionPersistenceFinderByC_C_T_R_S.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK, type, receiverUserId, status});
	}

	public SocialRequestPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(SocialRequest.class);

		setModelImplClass(SocialRequestImpl.class);
		setModelPKClass(long.class);

		setTable(SocialRequestTable.INSTANCE);
	}

	/**
	 * Creates a new social request with the primary key. Does not add the social request to the database.
	 *
	 * @param requestId the primary key for the new social request
	 * @return the new social request
	 */
	@Override
	public SocialRequest create(long requestId) {
		SocialRequest socialRequest = new SocialRequestImpl();

		socialRequest.setNew(true);
		socialRequest.setPrimaryKey(requestId);

		String uuid = PortalUUIDUtil.generate();

		socialRequest.setUuid(uuid);

		socialRequest.setCompanyId(CompanyThreadLocal.getCompanyId());

		return socialRequest;
	}

	/**
	 * Removes the social request with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param requestId the primary key of the social request
	 * @return the social request that was removed
	 * @throws NoSuchRequestException if a social request with the primary key could not be found
	 */
	@Override
	public SocialRequest remove(long requestId) throws NoSuchRequestException {
		return remove((Serializable)requestId);
	}

	@Override
	protected SocialRequest removeImpl(SocialRequest socialRequest) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(socialRequest)) {
				socialRequest = (SocialRequest)session.get(
					SocialRequestImpl.class, socialRequest.getPrimaryKeyObj());
			}

			if ((socialRequest != null) &&
				CTPersistenceHelperUtil.isRemove(socialRequest)) {

				session.delete(socialRequest);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (socialRequest != null) {
			clearCache(socialRequest);
		}

		return socialRequest;
	}

	@Override
	public SocialRequest updateImpl(SocialRequest socialRequest) {
		boolean isNew = socialRequest.isNew();

		if (!(socialRequest instanceof SocialRequestModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(socialRequest.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					socialRequest);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in socialRequest proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom SocialRequest implementation " +
					socialRequest.getClass());
		}

		SocialRequestModelImpl socialRequestModelImpl =
			(SocialRequestModelImpl)socialRequest;

		if (Validator.isNull(socialRequest.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			socialRequest.setUuid(uuid);
		}

		Session session = null;

		try {
			session = openSession();

			if (CTPersistenceHelperUtil.isInsert(socialRequest)) {
				if (!isNew) {
					session.evict(
						SocialRequestImpl.class,
						socialRequest.getPrimaryKeyObj());
				}

				session.save(socialRequest);
			}
			else {
				socialRequest = (SocialRequest)session.merge(socialRequest);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(socialRequest, false);

		if (isNew) {
			socialRequest.setNew(false);
		}

		socialRequest.resetOriginalValues();

		return socialRequest;
	}

	/**
	 * Returns the social request with the primary key or throws a <code>NoSuchRequestException</code> if it could not be found.
	 *
	 * @param requestId the primary key of the social request
	 * @return the social request
	 * @throws NoSuchRequestException if a social request with the primary key could not be found
	 */
	@Override
	public SocialRequest findByPrimaryKey(long requestId)
		throws NoSuchRequestException {

		return findByPrimaryKey((Serializable)requestId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return CTPersistenceHelperUtil.getCTPersistenceHelper();
	}

	/**
	 * Returns the social request with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param requestId the primary key of the social request
	 * @return the social request, or <code>null</code> if a social request with the primary key could not be found
	 */
	@Override
	public SocialRequest fetchByPrimaryKey(long requestId) {
		return fetchByPrimaryKey((Serializable)requestId);
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
		return "requestId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_SOCIALREQUEST;
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
		return SocialRequestModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "SocialRequest";
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
		ctStrictColumnNames.add("uuid_");
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("createDate");
		ctMergeColumnNames.add("modifiedDate");
		ctStrictColumnNames.add("classNameId");
		ctStrictColumnNames.add("classPK");
		ctMergeColumnNames.add("type_");
		ctMergeColumnNames.add("extraData");
		ctMergeColumnNames.add("receiverUserId");
		ctMergeColumnNames.add("status");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("requestId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {
				"userId", "classNameId", "classPK", "type_", "receiverUserId"
			});
	}

	/**
	 * Initializes the social request persistence.
	 */
	public void afterPropertiesSet() {
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
			_SQL_SELECT_SOCIALREQUEST_WHERE, _SQL_COUNT_SOCIALREQUEST_WHERE,
			SocialRequestModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"socialRequest.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, SocialRequest::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(SocialRequest::getUuid),
				SocialRequest::getGroupId),
			_SQL_SELECT_SOCIALREQUEST_WHERE, "",
			new FinderColumn<>(
				"socialRequest.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, SocialRequest::getUuid),
			new FinderColumn<>(
				"socialRequest.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, SocialRequest::getGroupId));

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
				_SQL_SELECT_SOCIALREQUEST_WHERE, _SQL_COUNT_SOCIALREQUEST_WHERE,
				SocialRequestModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"socialRequest.", "uuid", "uuid_", FinderColumn.Type.STRING,
					"=", true, true, SocialRequest::getUuid),
				new FinderColumn<>(
					"socialRequest.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, SocialRequest::getCompanyId));

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
				_SQL_SELECT_SOCIALREQUEST_WHERE, _SQL_COUNT_SOCIALREQUEST_WHERE,
				SocialRequestModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"socialRequest.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, SocialRequest::getCompanyId));

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
				_SQL_SELECT_SOCIALREQUEST_WHERE, _SQL_COUNT_SOCIALREQUEST_WHERE,
				SocialRequestModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"socialRequest.", "userId", FinderColumn.Type.LONG, "=",
					true, true, SocialRequest::getUserId));

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
				_SQL_SELECT_SOCIALREQUEST_WHERE, _SQL_COUNT_SOCIALREQUEST_WHERE,
				SocialRequestModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"socialRequest.", "receiverUserId", FinderColumn.Type.LONG,
					"=", true, true, SocialRequest::getReceiverUserId));

		_collectionPersistenceFinderByU_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByU_S",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"userId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByU_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"userId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByU_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"userId", "status"}, false),
			_SQL_SELECT_SOCIALREQUEST_WHERE, _SQL_COUNT_SOCIALREQUEST_WHERE,
			SocialRequestModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"socialRequest.", "userId", FinderColumn.Type.LONG, "=", true,
				true, SocialRequest::getUserId),
			new FinderColumn<>(
				"socialRequest.", "status", FinderColumn.Type.INTEGER, "=",
				true, true, SocialRequest::getStatus));

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
			_SQL_SELECT_SOCIALREQUEST_WHERE, _SQL_COUNT_SOCIALREQUEST_WHERE,
			SocialRequestModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"socialRequest.", "classNameId", FinderColumn.Type.LONG, "=",
				true, true, SocialRequest::getClassNameId),
			new FinderColumn<>(
				"socialRequest.", "classPK", FinderColumn.Type.LONG, "=", true,
				true, SocialRequest::getClassPK));

		_collectionPersistenceFinderByR_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByR_S",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"receiverUserId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByR_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"receiverUserId", "status"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByR_S",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"receiverUserId", "status"}, false),
			_SQL_SELECT_SOCIALREQUEST_WHERE, _SQL_COUNT_SOCIALREQUEST_WHERE,
			SocialRequestModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			null,
			new FinderColumn<>(
				"socialRequest.", "receiverUserId", FinderColumn.Type.LONG, "=",
				true, true, SocialRequest::getReceiverUserId),
			new FinderColumn<>(
				"socialRequest.", "status", FinderColumn.Type.INTEGER, "=",
				true, true, SocialRequest::getStatus));

		_uniquePersistenceFinderByU_C_C_T_R = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByU_C_C_T_R",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName(), Integer.class.getName(),
					Long.class.getName()
				},
				new String[] {
					"userId", "classNameId", "classPK", "type_",
					"receiverUserId"
				},
				0, 0, false, SocialRequest::getUserId,
				SocialRequest::getClassNameId, SocialRequest::getClassPK,
				SocialRequest::getType, SocialRequest::getReceiverUserId),
			_SQL_SELECT_SOCIALREQUEST_WHERE, "",
			new FinderColumn<>(
				"socialRequest.", "userId", FinderColumn.Type.LONG, "=", true,
				true, SocialRequest::getUserId),
			new FinderColumn<>(
				"socialRequest.", "classNameId", FinderColumn.Type.LONG, "=",
				true, true, SocialRequest::getClassNameId),
			new FinderColumn<>(
				"socialRequest.", "classPK", FinderColumn.Type.LONG, "=", true,
				true, SocialRequest::getClassPK),
			new FinderColumn<>(
				"socialRequest.", "type", "type_", FinderColumn.Type.INTEGER,
				"=", true, true, SocialRequest::getType),
			new FinderColumn<>(
				"socialRequest.", "receiverUserId", FinderColumn.Type.LONG, "=",
				true, true, SocialRequest::getReceiverUserId));

		_collectionPersistenceFinderByU_C_C_T_S =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByU_C_C_T_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"userId", "classNameId", "classPK", "type_", "status"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByU_C_C_T_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName()
					},
					new String[] {
						"userId", "classNameId", "classPK", "type_", "status"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByU_C_C_T_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName()
					},
					new String[] {
						"userId", "classNameId", "classPK", "type_", "status"
					},
					false),
				_SQL_SELECT_SOCIALREQUEST_WHERE, _SQL_COUNT_SOCIALREQUEST_WHERE,
				SocialRequestModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"socialRequest.", "userId", FinderColumn.Type.LONG, "=",
					true, true, SocialRequest::getUserId),
				new FinderColumn<>(
					"socialRequest.", "classNameId", FinderColumn.Type.LONG,
					"=", true, true, SocialRequest::getClassNameId),
				new FinderColumn<>(
					"socialRequest.", "classPK", FinderColumn.Type.LONG, "=",
					true, true, SocialRequest::getClassPK),
				new FinderColumn<>(
					"socialRequest.", "type", "type_",
					FinderColumn.Type.INTEGER, "=", true, true,
					SocialRequest::getType),
				new FinderColumn<>(
					"socialRequest.", "status", FinderColumn.Type.INTEGER, "=",
					true, true, SocialRequest::getStatus));

		_collectionPersistenceFinderByC_C_T_R_S =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C_T_R_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"classNameId", "classPK", "type_", "receiverUserId",
						"status"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByC_C_T_R_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Long.class.getName(),
						Integer.class.getName()
					},
					new String[] {
						"classNameId", "classPK", "type_", "receiverUserId",
						"status"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByC_C_T_R_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Long.class.getName(),
						Integer.class.getName()
					},
					new String[] {
						"classNameId", "classPK", "type_", "receiverUserId",
						"status"
					},
					false),
				_SQL_SELECT_SOCIALREQUEST_WHERE, _SQL_COUNT_SOCIALREQUEST_WHERE,
				SocialRequestModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"", null,
				new FinderColumn<>(
					"socialRequest.", "classNameId", FinderColumn.Type.LONG,
					"=", true, true, SocialRequest::getClassNameId),
				new FinderColumn<>(
					"socialRequest.", "classPK", FinderColumn.Type.LONG, "=",
					true, true, SocialRequest::getClassPK),
				new FinderColumn<>(
					"socialRequest.", "type", "type_",
					FinderColumn.Type.INTEGER, "=", true, true,
					SocialRequest::getType),
				new FinderColumn<>(
					"socialRequest.", "receiverUserId", FinderColumn.Type.LONG,
					"=", true, true, SocialRequest::getReceiverUserId),
				new FinderColumn<>(
					"socialRequest.", "status", FinderColumn.Type.INTEGER, "=",
					true, true, SocialRequest::getStatus));

		SocialRequestUtil.setPersistence(this);
	}

	public void destroy() {
		SocialRequestUtil.setPersistence(null);

		EntityCacheUtil.removeCache(SocialRequestImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		SocialRequestModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_SOCIALREQUEST =
		"SELECT socialRequest FROM SocialRequest socialRequest";

	private static final String _SQL_SELECT_SOCIALREQUEST_WHERE =
		"SELECT socialRequest FROM SocialRequest socialRequest WHERE ";

	private static final String _SQL_COUNT_SOCIALREQUEST_WHERE =
		"SELECT COUNT(socialRequest) FROM SocialRequest socialRequest WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "type"});

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-621330472