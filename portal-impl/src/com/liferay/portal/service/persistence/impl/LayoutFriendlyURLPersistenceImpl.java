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
import com.liferay.portal.kernel.exception.NoSuchLayoutFriendlyURLException;
import com.liferay.portal.kernel.model.LayoutFriendlyURL;
import com.liferay.portal.kernel.model.LayoutFriendlyURLTable;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.LayoutFriendlyURLPersistence;
import com.liferay.portal.kernel.service.persistence.LayoutFriendlyURLUtil;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelperUtil;
import com.liferay.portal.kernel.service.persistence.impl.ArrayableFinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.model.impl.LayoutFriendlyURLImpl;
import com.liferay.portal.model.impl.LayoutFriendlyURLModelImpl;

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
 * The persistence implementation for the layout friendly url service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class LayoutFriendlyURLPersistenceImpl
	extends BasePersistenceImpl
		<LayoutFriendlyURL, NoSuchLayoutFriendlyURLException>
	implements LayoutFriendlyURLPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>LayoutFriendlyURLUtil</code> to access the layout friendly url persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		LayoutFriendlyURLImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<LayoutFriendlyURL, NoSuchLayoutFriendlyURLException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the layout friendly urls where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutFriendlyURLModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout friendly urls
	 * @param end the upper bound of the range of layout friendly urls (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout friendly urls
	 */
	@Override
	public List<LayoutFriendlyURL> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<LayoutFriendlyURL> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout friendly url in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout friendly url
	 * @throws NoSuchLayoutFriendlyURLException if a matching layout friendly url could not be found
	 */
	@Override
	public LayoutFriendlyURL findByUuid_First(
			String uuid, OrderByComparator<LayoutFriendlyURL> orderByComparator)
		throws NoSuchLayoutFriendlyURLException {

		return _collectionPersistenceFinderByUuid.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid},
			orderByComparator);
	}

	/**
	 * Returns the first layout friendly url in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout friendly url, or <code>null</code> if a matching layout friendly url could not be found
	 */
	@Override
	public LayoutFriendlyURL fetchByUuid_First(
		String uuid, OrderByComparator<LayoutFriendlyURL> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid},
			orderByComparator);
	}

	/**
	 * Removes all the layout friendly urls where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	/**
	 * Returns the number of layout friendly urls where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching layout friendly urls
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	private UniquePersistenceFinder
		<LayoutFriendlyURL, NoSuchLayoutFriendlyURLException>
			_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the layout friendly url where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchLayoutFriendlyURLException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching layout friendly url
	 * @throws NoSuchLayoutFriendlyURLException if a matching layout friendly url could not be found
	 */
	@Override
	public LayoutFriendlyURL findByUUID_G(String uuid, long groupId)
		throws NoSuchLayoutFriendlyURLException {

		return _uniquePersistenceFinderByUUID_G.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, groupId});
	}

	/**
	 * Returns the layout friendly url where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout friendly url, or <code>null</code> if a matching layout friendly url could not be found
	 */
	@Override
	public LayoutFriendlyURL fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, groupId},
			useFinderCache);
	}

	/**
	 * Removes the layout friendly url where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the layout friendly url that was removed
	 */
	@Override
	public LayoutFriendlyURL removeByUUID_G(String uuid, long groupId)
		throws NoSuchLayoutFriendlyURLException {

		LayoutFriendlyURL layoutFriendlyURL = findByUUID_G(uuid, groupId);

		return remove(layoutFriendlyURL);
	}

	/**
	 * Returns the number of layout friendly urls where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching layout friendly urls
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder
		<LayoutFriendlyURL, NoSuchLayoutFriendlyURLException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the layout friendly urls where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutFriendlyURLModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout friendly urls
	 * @param end the upper bound of the range of layout friendly urls (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout friendly urls
	 */
	@Override
	public List<LayoutFriendlyURL> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<LayoutFriendlyURL> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout friendly url in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout friendly url
	 * @throws NoSuchLayoutFriendlyURLException if a matching layout friendly url could not be found
	 */
	@Override
	public LayoutFriendlyURL findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<LayoutFriendlyURL> orderByComparator)
		throws NoSuchLayoutFriendlyURLException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			orderByComparator);
	}

	/**
	 * Returns the first layout friendly url in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout friendly url, or <code>null</code> if a matching layout friendly url could not be found
	 */
	@Override
	public LayoutFriendlyURL fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<LayoutFriendlyURL> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			orderByComparator);
	}

	/**
	 * Removes all the layout friendly urls where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of layout friendly urls where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching layout friendly urls
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<LayoutFriendlyURL, NoSuchLayoutFriendlyURLException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the layout friendly urls where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutFriendlyURLModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layout friendly urls
	 * @param end the upper bound of the range of layout friendly urls (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout friendly urls
	 */
	@Override
	public List<LayoutFriendlyURL> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<LayoutFriendlyURL> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout friendly url in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout friendly url
	 * @throws NoSuchLayoutFriendlyURLException if a matching layout friendly url could not be found
	 */
	@Override
	public LayoutFriendlyURL findByGroupId_First(
			long groupId,
			OrderByComparator<LayoutFriendlyURL> orderByComparator)
		throws NoSuchLayoutFriendlyURLException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId},
			orderByComparator);
	}

	/**
	 * Returns the first layout friendly url in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout friendly url, or <code>null</code> if a matching layout friendly url could not be found
	 */
	@Override
	public LayoutFriendlyURL fetchByGroupId_First(
		long groupId, OrderByComparator<LayoutFriendlyURL> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId},
			orderByComparator);
	}

	/**
	 * Removes all the layout friendly urls where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId});
	}

	/**
	 * Returns the number of layout friendly urls where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching layout friendly urls
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId});
	}

	private CollectionPersistenceFinder
		<LayoutFriendlyURL, NoSuchLayoutFriendlyURLException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the layout friendly urls where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutFriendlyURLModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout friendly urls
	 * @param end the upper bound of the range of layout friendly urls (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout friendly urls
	 */
	@Override
	public List<LayoutFriendlyURL> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<LayoutFriendlyURL> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout friendly url in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout friendly url
	 * @throws NoSuchLayoutFriendlyURLException if a matching layout friendly url could not be found
	 */
	@Override
	public LayoutFriendlyURL findByCompanyId_First(
			long companyId,
			OrderByComparator<LayoutFriendlyURL> orderByComparator)
		throws NoSuchLayoutFriendlyURLException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			orderByComparator);
	}

	/**
	 * Returns the first layout friendly url in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout friendly url, or <code>null</code> if a matching layout friendly url could not be found
	 */
	@Override
	public LayoutFriendlyURL fetchByCompanyId_First(
		long companyId,
		OrderByComparator<LayoutFriendlyURL> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			orderByComparator);
	}

	/**
	 * Removes all the layout friendly urls where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId});
	}

	/**
	 * Returns the number of layout friendly urls where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching layout friendly urls
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId});
	}

	private CollectionPersistenceFinder
		<LayoutFriendlyURL, NoSuchLayoutFriendlyURLException>
			_collectionPersistenceFinderByPlid;

	/**
	 * Returns an ordered range of all the layout friendly urls where plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutFriendlyURLModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param start the lower bound of the range of layout friendly urls
	 * @param end the upper bound of the range of layout friendly urls (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout friendly urls
	 */
	@Override
	public List<LayoutFriendlyURL> findByPlid(
		long plid, int start, int end,
		OrderByComparator<LayoutFriendlyURL> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByPlid.find(
			FinderCacheUtil.getFinderCache(), new Object[] {plid}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout friendly url in the ordered set where plid = &#63;.
	 *
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout friendly url
	 * @throws NoSuchLayoutFriendlyURLException if a matching layout friendly url could not be found
	 */
	@Override
	public LayoutFriendlyURL findByPlid_First(
			long plid, OrderByComparator<LayoutFriendlyURL> orderByComparator)
		throws NoSuchLayoutFriendlyURLException {

		return _collectionPersistenceFinderByPlid.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {plid},
			orderByComparator);
	}

	/**
	 * Returns the first layout friendly url in the ordered set where plid = &#63;.
	 *
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout friendly url, or <code>null</code> if a matching layout friendly url could not be found
	 */
	@Override
	public LayoutFriendlyURL fetchByPlid_First(
		long plid, OrderByComparator<LayoutFriendlyURL> orderByComparator) {

		return _collectionPersistenceFinderByPlid.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {plid},
			orderByComparator);
	}

	/**
	 * Removes all the layout friendly urls where plid = &#63; from the database.
	 *
	 * @param plid the plid
	 */
	@Override
	public void removeByPlid(long plid) {
		_collectionPersistenceFinderByPlid.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {plid});
	}

	/**
	 * Returns the number of layout friendly urls where plid = &#63;.
	 *
	 * @param plid the plid
	 * @return the number of matching layout friendly urls
	 */
	@Override
	public int countByPlid(long plid) {
		return _collectionPersistenceFinderByPlid.count(
			FinderCacheUtil.getFinderCache(), new Object[] {plid});
	}

	private CollectionPersistenceFinder
		<LayoutFriendlyURL, NoSuchLayoutFriendlyURLException>
			_collectionPersistenceFinderByC_F;

	/**
	 * Returns an ordered range of all the layout friendly urls where companyId = &#63; and friendlyURL = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutFriendlyURLModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param friendlyURL the friendly url
	 * @param start the lower bound of the range of layout friendly urls
	 * @param end the upper bound of the range of layout friendly urls (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout friendly urls
	 */
	@Override
	public List<LayoutFriendlyURL> findByC_F(
		long companyId, String friendlyURL, int start, int end,
		OrderByComparator<LayoutFriendlyURL> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_F.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, friendlyURL}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout friendly url in the ordered set where companyId = &#63; and friendlyURL = &#63;.
	 *
	 * @param companyId the company ID
	 * @param friendlyURL the friendly url
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout friendly url
	 * @throws NoSuchLayoutFriendlyURLException if a matching layout friendly url could not be found
	 */
	@Override
	public LayoutFriendlyURL findByC_F_First(
			long companyId, String friendlyURL,
			OrderByComparator<LayoutFriendlyURL> orderByComparator)
		throws NoSuchLayoutFriendlyURLException {

		return _collectionPersistenceFinderByC_F.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, friendlyURL}, orderByComparator);
	}

	/**
	 * Returns the first layout friendly url in the ordered set where companyId = &#63; and friendlyURL = &#63;.
	 *
	 * @param companyId the company ID
	 * @param friendlyURL the friendly url
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout friendly url, or <code>null</code> if a matching layout friendly url could not be found
	 */
	@Override
	public LayoutFriendlyURL fetchByC_F_First(
		long companyId, String friendlyURL,
		OrderByComparator<LayoutFriendlyURL> orderByComparator) {

		return _collectionPersistenceFinderByC_F.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, friendlyURL}, orderByComparator);
	}

	/**
	 * Removes all the layout friendly urls where companyId = &#63; and friendlyURL = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param friendlyURL the friendly url
	 */
	@Override
	public void removeByC_F(long companyId, String friendlyURL) {
		_collectionPersistenceFinderByC_F.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, friendlyURL});
	}

	/**
	 * Returns the number of layout friendly urls where companyId = &#63; and friendlyURL = &#63;.
	 *
	 * @param companyId the company ID
	 * @param friendlyURL the friendly url
	 * @return the number of matching layout friendly urls
	 */
	@Override
	public int countByC_F(long companyId, String friendlyURL) {
		return _collectionPersistenceFinderByC_F.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, friendlyURL});
	}

	private CollectionPersistenceFinder
		<LayoutFriendlyURL, NoSuchLayoutFriendlyURLException>
			_collectionPersistenceFinderByP_F;

	/**
	 * Returns an ordered range of all the layout friendly urls where plid = &#63; and friendlyURL = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutFriendlyURLModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param friendlyURL the friendly url
	 * @param start the lower bound of the range of layout friendly urls
	 * @param end the upper bound of the range of layout friendly urls (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout friendly urls
	 */
	@Override
	public List<LayoutFriendlyURL> findByP_F(
		long plid, String friendlyURL, int start, int end,
		OrderByComparator<LayoutFriendlyURL> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByP_F.find(
			FinderCacheUtil.getFinderCache(), new Object[] {plid, friendlyURL},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout friendly url in the ordered set where plid = &#63; and friendlyURL = &#63;.
	 *
	 * @param plid the plid
	 * @param friendlyURL the friendly url
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout friendly url
	 * @throws NoSuchLayoutFriendlyURLException if a matching layout friendly url could not be found
	 */
	@Override
	public LayoutFriendlyURL findByP_F_First(
			long plid, String friendlyURL,
			OrderByComparator<LayoutFriendlyURL> orderByComparator)
		throws NoSuchLayoutFriendlyURLException {

		return _collectionPersistenceFinderByP_F.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {plid, friendlyURL},
			orderByComparator);
	}

	/**
	 * Returns the first layout friendly url in the ordered set where plid = &#63; and friendlyURL = &#63;.
	 *
	 * @param plid the plid
	 * @param friendlyURL the friendly url
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout friendly url, or <code>null</code> if a matching layout friendly url could not be found
	 */
	@Override
	public LayoutFriendlyURL fetchByP_F_First(
		long plid, String friendlyURL,
		OrderByComparator<LayoutFriendlyURL> orderByComparator) {

		return _collectionPersistenceFinderByP_F.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {plid, friendlyURL},
			orderByComparator);
	}

	/**
	 * Removes all the layout friendly urls where plid = &#63; and friendlyURL = &#63; from the database.
	 *
	 * @param plid the plid
	 * @param friendlyURL the friendly url
	 */
	@Override
	public void removeByP_F(long plid, String friendlyURL) {
		_collectionPersistenceFinderByP_F.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {plid, friendlyURL});
	}

	/**
	 * Returns the number of layout friendly urls where plid = &#63; and friendlyURL = &#63;.
	 *
	 * @param plid the plid
	 * @param friendlyURL the friendly url
	 * @return the number of matching layout friendly urls
	 */
	@Override
	public int countByP_F(long plid, String friendlyURL) {
		return _collectionPersistenceFinderByP_F.count(
			FinderCacheUtil.getFinderCache(), new Object[] {plid, friendlyURL});
	}

	private CollectionPersistenceFinder
		<LayoutFriendlyURL, NoSuchLayoutFriendlyURLException>
			_collectionPersistenceFinderByP_L;
	private UniquePersistenceFinder
		<LayoutFriendlyURL, NoSuchLayoutFriendlyURLException>
			_uniquePersistenceFinderByP_L;

	/**
	 * Returns an ordered range of all the layout friendly urls where plid = &#63; and languageId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutFriendlyURLModelImpl</code>.
	 * </p>
	 *
	 * @param plids the plids
	 * @param languageId the language ID
	 * @param start the lower bound of the range of layout friendly urls
	 * @param end the upper bound of the range of layout friendly urls (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout friendly urls
	 */
	@Override
	public List<LayoutFriendlyURL> findByP_L(
		long[] plids, String languageId, int start, int end,
		OrderByComparator<LayoutFriendlyURL> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByP_L.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {ArrayUtil.sortedUnique(plids), languageId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the layout friendly url where plid = &#63; and languageId = &#63; or throws a <code>NoSuchLayoutFriendlyURLException</code> if it could not be found.
	 *
	 * @param plid the plid
	 * @param languageId the language ID
	 * @return the matching layout friendly url
	 * @throws NoSuchLayoutFriendlyURLException if a matching layout friendly url could not be found
	 */
	@Override
	public LayoutFriendlyURL findByP_L(long plid, String languageId)
		throws NoSuchLayoutFriendlyURLException {

		return _uniquePersistenceFinderByP_L.find(
			FinderCacheUtil.getFinderCache(), new Object[] {plid, languageId});
	}

	/**
	 * Returns the layout friendly url where plid = &#63; and languageId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param plid the plid
	 * @param languageId the language ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout friendly url, or <code>null</code> if a matching layout friendly url could not be found
	 */
	@Override
	public LayoutFriendlyURL fetchByP_L(
		long plid, String languageId, boolean useFinderCache) {

		return _uniquePersistenceFinderByP_L.fetch(
			FinderCacheUtil.getFinderCache(), new Object[] {plid, languageId},
			useFinderCache);
	}

	/**
	 * Removes the layout friendly url where plid = &#63; and languageId = &#63; from the database.
	 *
	 * @param plid the plid
	 * @param languageId the language ID
	 * @return the layout friendly url that was removed
	 */
	@Override
	public LayoutFriendlyURL removeByP_L(long plid, String languageId)
		throws NoSuchLayoutFriendlyURLException {

		LayoutFriendlyURL layoutFriendlyURL = findByP_L(plid, languageId);

		return remove(layoutFriendlyURL);
	}

	/**
	 * Returns the number of layout friendly urls where plid = &#63; and languageId = &#63;.
	 *
	 * @param plid the plid
	 * @param languageId the language ID
	 * @return the number of matching layout friendly urls
	 */
	@Override
	public int countByP_L(long plid, String languageId) {
		return _collectionPersistenceFinderByP_L.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {new long[] {plid}, languageId});
	}

	/**
	 * Returns the number of layout friendly urls where plid = any &#63; and languageId = &#63;.
	 *
	 * @param plids the plids
	 * @param languageId the language ID
	 * @return the number of matching layout friendly urls
	 */
	@Override
	public int countByP_L(long[] plids, String languageId) {
		return _collectionPersistenceFinderByP_L.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {ArrayUtil.sortedUnique(plids), languageId});
	}

	private CollectionPersistenceFinder
		<LayoutFriendlyURL, NoSuchLayoutFriendlyURLException>
			_collectionPersistenceFinderByG_P_F;

	/**
	 * Returns an ordered range of all the layout friendly urls where groupId = &#63; and privateLayout = &#63; and friendlyURL = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutFriendlyURLModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param friendlyURL the friendly url
	 * @param start the lower bound of the range of layout friendly urls
	 * @param end the upper bound of the range of layout friendly urls (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout friendly urls
	 */
	@Override
	public List<LayoutFriendlyURL> findByG_P_F(
		long groupId, boolean privateLayout, String friendlyURL, int start,
		int end, OrderByComparator<LayoutFriendlyURL> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_P_F.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout, friendlyURL}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout friendly url in the ordered set where groupId = &#63; and privateLayout = &#63; and friendlyURL = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param friendlyURL the friendly url
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout friendly url
	 * @throws NoSuchLayoutFriendlyURLException if a matching layout friendly url could not be found
	 */
	@Override
	public LayoutFriendlyURL findByG_P_F_First(
			long groupId, boolean privateLayout, String friendlyURL,
			OrderByComparator<LayoutFriendlyURL> orderByComparator)
		throws NoSuchLayoutFriendlyURLException {

		return _collectionPersistenceFinderByG_P_F.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout, friendlyURL},
			orderByComparator);
	}

	/**
	 * Returns the first layout friendly url in the ordered set where groupId = &#63; and privateLayout = &#63; and friendlyURL = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param friendlyURL the friendly url
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout friendly url, or <code>null</code> if a matching layout friendly url could not be found
	 */
	@Override
	public LayoutFriendlyURL fetchByG_P_F_First(
		long groupId, boolean privateLayout, String friendlyURL,
		OrderByComparator<LayoutFriendlyURL> orderByComparator) {

		return _collectionPersistenceFinderByG_P_F.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout, friendlyURL},
			orderByComparator);
	}

	/**
	 * Removes all the layout friendly urls where groupId = &#63; and privateLayout = &#63; and friendlyURL = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param friendlyURL the friendly url
	 */
	@Override
	public void removeByG_P_F(
		long groupId, boolean privateLayout, String friendlyURL) {

		_collectionPersistenceFinderByG_P_F.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout, friendlyURL});
	}

	/**
	 * Returns the number of layout friendly urls where groupId = &#63; and privateLayout = &#63; and friendlyURL = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param friendlyURL the friendly url
	 * @return the number of matching layout friendly urls
	 */
	@Override
	public int countByG_P_F(
		long groupId, boolean privateLayout, String friendlyURL) {

		return _collectionPersistenceFinderByG_P_F.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout, friendlyURL});
	}

	private UniquePersistenceFinder
		<LayoutFriendlyURL, NoSuchLayoutFriendlyURLException>
			_uniquePersistenceFinderByG_P_F_L;

	/**
	 * Returns the layout friendly url where groupId = &#63; and privateLayout = &#63; and friendlyURL = &#63; and languageId = &#63; or throws a <code>NoSuchLayoutFriendlyURLException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param friendlyURL the friendly url
	 * @param languageId the language ID
	 * @return the matching layout friendly url
	 * @throws NoSuchLayoutFriendlyURLException if a matching layout friendly url could not be found
	 */
	@Override
	public LayoutFriendlyURL findByG_P_F_L(
			long groupId, boolean privateLayout, String friendlyURL,
			String languageId)
		throws NoSuchLayoutFriendlyURLException {

		return _uniquePersistenceFinderByG_P_F_L.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout, friendlyURL, languageId});
	}

	/**
	 * Returns the layout friendly url where groupId = &#63; and privateLayout = &#63; and friendlyURL = &#63; and languageId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param friendlyURL the friendly url
	 * @param languageId the language ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout friendly url, or <code>null</code> if a matching layout friendly url could not be found
	 */
	@Override
	public LayoutFriendlyURL fetchByG_P_F_L(
		long groupId, boolean privateLayout, String friendlyURL,
		String languageId, boolean useFinderCache) {

		return _uniquePersistenceFinderByG_P_F_L.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout, friendlyURL, languageId},
			useFinderCache);
	}

	/**
	 * Removes the layout friendly url where groupId = &#63; and privateLayout = &#63; and friendlyURL = &#63; and languageId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param friendlyURL the friendly url
	 * @param languageId the language ID
	 * @return the layout friendly url that was removed
	 */
	@Override
	public LayoutFriendlyURL removeByG_P_F_L(
			long groupId, boolean privateLayout, String friendlyURL,
			String languageId)
		throws NoSuchLayoutFriendlyURLException {

		LayoutFriendlyURL layoutFriendlyURL = findByG_P_F_L(
			groupId, privateLayout, friendlyURL, languageId);

		return remove(layoutFriendlyURL);
	}

	/**
	 * Returns the number of layout friendly urls where groupId = &#63; and privateLayout = &#63; and friendlyURL = &#63; and languageId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param friendlyURL the friendly url
	 * @param languageId the language ID
	 * @return the number of matching layout friendly urls
	 */
	@Override
	public int countByG_P_F_L(
		long groupId, boolean privateLayout, String friendlyURL,
		String languageId) {

		return _uniquePersistenceFinderByG_P_F_L.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout, friendlyURL, languageId});
	}

	public LayoutFriendlyURLPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(LayoutFriendlyURL.class);

		setModelImplClass(LayoutFriendlyURLImpl.class);
		setModelPKClass(long.class);

		setTable(LayoutFriendlyURLTable.INSTANCE);
	}

	/**
	 * Creates a new layout friendly url with the primary key. Does not add the layout friendly url to the database.
	 *
	 * @param layoutFriendlyURLId the primary key for the new layout friendly url
	 * @return the new layout friendly url
	 */
	@Override
	public LayoutFriendlyURL create(long layoutFriendlyURLId) {
		LayoutFriendlyURL layoutFriendlyURL = new LayoutFriendlyURLImpl();

		layoutFriendlyURL.setNew(true);
		layoutFriendlyURL.setPrimaryKey(layoutFriendlyURLId);

		String uuid = PortalUUIDUtil.generate();

		layoutFriendlyURL.setUuid(uuid);

		layoutFriendlyURL.setCompanyId(CompanyThreadLocal.getCompanyId());

		return layoutFriendlyURL;
	}

	/**
	 * Removes the layout friendly url with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param layoutFriendlyURLId the primary key of the layout friendly url
	 * @return the layout friendly url that was removed
	 * @throws NoSuchLayoutFriendlyURLException if a layout friendly url with the primary key could not be found
	 */
	@Override
	public LayoutFriendlyURL remove(long layoutFriendlyURLId)
		throws NoSuchLayoutFriendlyURLException {

		return remove((Serializable)layoutFriendlyURLId);
	}

	@Override
	protected LayoutFriendlyURL removeImpl(
		LayoutFriendlyURL layoutFriendlyURL) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(layoutFriendlyURL)) {
				layoutFriendlyURL = (LayoutFriendlyURL)session.get(
					LayoutFriendlyURLImpl.class,
					layoutFriendlyURL.getPrimaryKeyObj());
			}

			if ((layoutFriendlyURL != null) &&
				CTPersistenceHelperUtil.isRemove(layoutFriendlyURL)) {

				session.delete(layoutFriendlyURL);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (layoutFriendlyURL != null) {
			clearCache(layoutFriendlyURL);
		}

		return layoutFriendlyURL;
	}

	@Override
	public LayoutFriendlyURL updateImpl(LayoutFriendlyURL layoutFriendlyURL) {
		boolean isNew = layoutFriendlyURL.isNew();

		if (!(layoutFriendlyURL instanceof LayoutFriendlyURLModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(layoutFriendlyURL.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					layoutFriendlyURL);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in layoutFriendlyURL proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom LayoutFriendlyURL implementation " +
					layoutFriendlyURL.getClass());
		}

		LayoutFriendlyURLModelImpl layoutFriendlyURLModelImpl =
			(LayoutFriendlyURLModelImpl)layoutFriendlyURL;

		if (Validator.isNull(layoutFriendlyURL.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			layoutFriendlyURL.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (layoutFriendlyURL.getCreateDate() == null)) {
			if (serviceContext == null) {
				layoutFriendlyURL.setCreateDate(date);
			}
			else {
				layoutFriendlyURL.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!layoutFriendlyURLModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				layoutFriendlyURL.setModifiedDate(date);
			}
			else {
				layoutFriendlyURL.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (CTPersistenceHelperUtil.isInsert(layoutFriendlyURL)) {
				if (!isNew) {
					session.evict(
						LayoutFriendlyURLImpl.class,
						layoutFriendlyURL.getPrimaryKeyObj());
				}

				session.save(layoutFriendlyURL);
			}
			else {
				layoutFriendlyURL = (LayoutFriendlyURL)session.merge(
					layoutFriendlyURL);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(layoutFriendlyURL, false);

		if (isNew) {
			layoutFriendlyURL.setNew(false);
		}

		layoutFriendlyURL.resetOriginalValues();

		return layoutFriendlyURL;
	}

	/**
	 * Returns the layout friendly url with the primary key or throws a <code>NoSuchLayoutFriendlyURLException</code> if it could not be found.
	 *
	 * @param layoutFriendlyURLId the primary key of the layout friendly url
	 * @return the layout friendly url
	 * @throws NoSuchLayoutFriendlyURLException if a layout friendly url with the primary key could not be found
	 */
	@Override
	public LayoutFriendlyURL findByPrimaryKey(long layoutFriendlyURLId)
		throws NoSuchLayoutFriendlyURLException {

		return findByPrimaryKey((Serializable)layoutFriendlyURLId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return CTPersistenceHelperUtil.getCTPersistenceHelper();
	}

	/**
	 * Returns the layout friendly url with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param layoutFriendlyURLId the primary key of the layout friendly url
	 * @return the layout friendly url, or <code>null</code> if a layout friendly url with the primary key could not be found
	 */
	@Override
	public LayoutFriendlyURL fetchByPrimaryKey(long layoutFriendlyURLId) {
		return fetchByPrimaryKey((Serializable)layoutFriendlyURLId);
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
		return "layoutFriendlyURLId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_LAYOUTFRIENDLYURL;
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
		return LayoutFriendlyURLModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "LayoutFriendlyURL";
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
		ctStrictColumnNames.add("uuid_");
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("plid");
		ctMergeColumnNames.add("privateLayout");
		ctMergeColumnNames.add("friendlyURL");
		ctMergeColumnNames.add("languageId");
		ctMergeColumnNames.add("lastPublishDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("layoutFriendlyURLId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(new String[] {"plid", "languageId"});

		_uniqueIndexColumnNames.add(
			new String[] {
				"groupId", "privateLayout", "friendlyURL", "languageId"
			});
	}

	/**
	 * Initializes the layout friendly url persistence.
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
			_SQL_SELECT_LAYOUTFRIENDLYURL_WHERE,
			_SQL_COUNT_LAYOUTFRIENDLYURL_WHERE,
			LayoutFriendlyURLModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"", null,
			new FinderColumn<>(
				"layoutFriendlyURL.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, LayoutFriendlyURL::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(LayoutFriendlyURL::getUuid),
				LayoutFriendlyURL::getGroupId),
			_SQL_SELECT_LAYOUTFRIENDLYURL_WHERE, "",
			new FinderColumn<>(
				"layoutFriendlyURL.", "uuid", "uuid_", FinderColumn.Type.STRING,
				"=", true, true, LayoutFriendlyURL::getUuid),
			new FinderColumn<>(
				"layoutFriendlyURL.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, LayoutFriendlyURL::getGroupId));

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
				_SQL_SELECT_LAYOUTFRIENDLYURL_WHERE,
				_SQL_COUNT_LAYOUTFRIENDLYURL_WHERE,
				LayoutFriendlyURLModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"layoutFriendlyURL.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					LayoutFriendlyURL::getUuid),
				new FinderColumn<>(
					"layoutFriendlyURL.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, LayoutFriendlyURL::getCompanyId));

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
				_SQL_SELECT_LAYOUTFRIENDLYURL_WHERE,
				_SQL_COUNT_LAYOUTFRIENDLYURL_WHERE,
				LayoutFriendlyURLModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"layoutFriendlyURL.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, LayoutFriendlyURL::getGroupId));

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
				_SQL_SELECT_LAYOUTFRIENDLYURL_WHERE,
				_SQL_COUNT_LAYOUTFRIENDLYURL_WHERE,
				LayoutFriendlyURLModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"layoutFriendlyURL.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, LayoutFriendlyURL::getCompanyId));

		_collectionPersistenceFinderByPlid = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByPlid",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"plid"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByPlid",
				new String[] {Long.class.getName()}, new String[] {"plid"},
				true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByPlid",
				new String[] {Long.class.getName()}, new String[] {"plid"},
				false),
			_SQL_SELECT_LAYOUTFRIENDLYURL_WHERE,
			_SQL_COUNT_LAYOUTFRIENDLYURL_WHERE,
			LayoutFriendlyURLModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"", null,
			new FinderColumn<>(
				"layoutFriendlyURL.", "plid", FinderColumn.Type.LONG, "=", true,
				true, LayoutFriendlyURL::getPlid));

		_collectionPersistenceFinderByC_F = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_F",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"companyId", "friendlyURL"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_F",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "friendlyURL"}, 0, 2, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_F",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "friendlyURL"}, 0, 2, false, null),
			_SQL_SELECT_LAYOUTFRIENDLYURL_WHERE,
			_SQL_COUNT_LAYOUTFRIENDLYURL_WHERE,
			LayoutFriendlyURLModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"", null,
			new FinderColumn<>(
				"layoutFriendlyURL.", "companyId", FinderColumn.Type.LONG, "=",
				true, true, LayoutFriendlyURL::getCompanyId),
			new FinderColumn<>(
				"layoutFriendlyURL.", "friendlyURL", FinderColumn.Type.STRING,
				"=", true, true, LayoutFriendlyURL::getFriendlyURL));

		_collectionPersistenceFinderByP_F = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByP_F",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"plid", "friendlyURL"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByP_F",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"plid", "friendlyURL"}, 0, 2, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByP_F",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"plid", "friendlyURL"}, 0, 2, false, null),
			_SQL_SELECT_LAYOUTFRIENDLYURL_WHERE,
			_SQL_COUNT_LAYOUTFRIENDLYURL_WHERE,
			LayoutFriendlyURLModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"", null,
			new FinderColumn<>(
				"layoutFriendlyURL.", "plid", FinderColumn.Type.LONG, "=", true,
				true, LayoutFriendlyURL::getPlid),
			new FinderColumn<>(
				"layoutFriendlyURL.", "friendlyURL", FinderColumn.Type.STRING,
				"=", true, true, LayoutFriendlyURL::getFriendlyURL));

		_uniquePersistenceFinderByP_L = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByP_L",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"plid", "languageId"}, 0, 2, false,
				LayoutFriendlyURL::getPlid,
				convertNullFunction(LayoutFriendlyURL::getLanguageId)),
			_SQL_SELECT_LAYOUTFRIENDLYURL_WHERE, "",
			new FinderColumn<>(
				"layoutFriendlyURL.", "plid", FinderColumn.Type.LONG, "=", true,
				true, LayoutFriendlyURL::getPlid),
			new FinderColumn<>(
				"layoutFriendlyURL.", "languageId", FinderColumn.Type.STRING,
				"=", true, true, LayoutFriendlyURL::getLanguageId));

		_collectionPersistenceFinderByP_L = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByP_L",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"plid", "languageId"}, true),
			null,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByP_L",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"plid", "languageId"}, 0, 2, false, null),
			_SQL_SELECT_LAYOUTFRIENDLYURL_WHERE,
			_SQL_COUNT_LAYOUTFRIENDLYURL_WHERE,
			LayoutFriendlyURLModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"", _uniquePersistenceFinderByP_L,
			new ArrayableFinderColumn<>(
				"layoutFriendlyURL.", "plid", FinderColumn.Type.LONG, "=",
				false, true, true, LayoutFriendlyURL::getPlid),
			new FinderColumn<>(
				"layoutFriendlyURL.", "languageId", FinderColumn.Type.STRING,
				"=", true, true, LayoutFriendlyURL::getLanguageId));

		_collectionPersistenceFinderByG_P_F = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_P_F",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					String.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"groupId", "privateLayout", "friendlyURL"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_P_F",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					String.class.getName()
				},
				new String[] {"groupId", "privateLayout", "friendlyURL"}, 0, 4,
				true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_P_F",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					String.class.getName()
				},
				new String[] {"groupId", "privateLayout", "friendlyURL"}, 0, 4,
				false, null),
			_SQL_SELECT_LAYOUTFRIENDLYURL_WHERE,
			_SQL_COUNT_LAYOUTFRIENDLYURL_WHERE,
			LayoutFriendlyURLModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"", null,
			new FinderColumn<>(
				"layoutFriendlyURL.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, LayoutFriendlyURL::getGroupId),
			new FinderColumn<>(
				"layoutFriendlyURL.", "privateLayout",
				FinderColumn.Type.BOOLEAN, "=", true, true,
				LayoutFriendlyURL::isPrivateLayout),
			new FinderColumn<>(
				"layoutFriendlyURL.", "friendlyURL", FinderColumn.Type.STRING,
				"=", true, true, LayoutFriendlyURL::getFriendlyURL));

		_uniquePersistenceFinderByG_P_F_L = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_P_F_L",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					String.class.getName(), String.class.getName()
				},
				new String[] {
					"groupId", "privateLayout", "friendlyURL", "languageId"
				},
				0, 12, false, LayoutFriendlyURL::getGroupId,
				LayoutFriendlyURL::isPrivateLayout,
				convertNullFunction(LayoutFriendlyURL::getFriendlyURL),
				convertNullFunction(LayoutFriendlyURL::getLanguageId)),
			_SQL_SELECT_LAYOUTFRIENDLYURL_WHERE, "",
			new FinderColumn<>(
				"layoutFriendlyURL.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, LayoutFriendlyURL::getGroupId),
			new FinderColumn<>(
				"layoutFriendlyURL.", "privateLayout",
				FinderColumn.Type.BOOLEAN, "=", true, true,
				LayoutFriendlyURL::isPrivateLayout),
			new FinderColumn<>(
				"layoutFriendlyURL.", "friendlyURL", FinderColumn.Type.STRING,
				"=", true, true, LayoutFriendlyURL::getFriendlyURL),
			new FinderColumn<>(
				"layoutFriendlyURL.", "languageId", FinderColumn.Type.STRING,
				"=", true, true, LayoutFriendlyURL::getLanguageId));

		LayoutFriendlyURLUtil.setPersistence(this);
	}

	public void destroy() {
		LayoutFriendlyURLUtil.setPersistence(null);

		EntityCacheUtil.removeCache(LayoutFriendlyURLImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		LayoutFriendlyURLModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_LAYOUTFRIENDLYURL =
		"SELECT layoutFriendlyURL FROM LayoutFriendlyURL layoutFriendlyURL";

	private static final String _SQL_SELECT_LAYOUTFRIENDLYURL_WHERE =
		"SELECT layoutFriendlyURL FROM LayoutFriendlyURL layoutFriendlyURL WHERE ";

	private static final String _SQL_COUNT_LAYOUTFRIENDLYURL_WHERE =
		"SELECT COUNT(layoutFriendlyURL) FROM LayoutFriendlyURL layoutFriendlyURL WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-674763126