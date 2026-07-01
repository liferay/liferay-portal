/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.impl;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.DuplicateLayoutExternalReferenceCodeException;
import com.liferay.portal.kernel.exception.NoSuchLayoutException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTable;
import com.liferay.portal.kernel.sanitizer.Sanitizer;
import com.liferay.portal.kernel.sanitizer.SanitizerException;
import com.liferay.portal.kernel.sanitizer.SanitizerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.LayoutPersistence;
import com.liferay.portal.kernel.service.persistence.LayoutUtil;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelperUtil;
import com.liferay.portal.kernel.service.persistence.impl.ArrayableFinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FilterCollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.model.impl.LayoutImpl;
import com.liferay.portal.model.impl.LayoutModelImpl;

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
import java.util.Objects;
import java.util.Set;

/**
 * The persistence implementation for the layout service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class LayoutPersistenceImpl
	extends BasePersistenceImpl<Layout, NoSuchLayoutException>
	implements LayoutPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>LayoutUtil</code> to access the layout persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		LayoutImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<Layout, NoSuchLayoutException>
		_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the layouts where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByUuid_First(
			String uuid, OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		return _collectionPersistenceFinderByUuid.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid},
			orderByComparator);
	}

	/**
	 * Returns the first layout in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByUuid_First(
		String uuid, OrderByComparator<Layout> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid},
			orderByComparator);
	}

	/**
	 * Removes all the layouts where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	/**
	 * Returns the number of layouts where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching layouts
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	private UniquePersistenceFinder<Layout, NoSuchLayoutException>
		_uniquePersistenceFinderByUUID_G_P;

	/**
	 * Returns the layout where uuid = &#63; and groupId = &#63; and privateLayout = &#63; or throws a <code>NoSuchLayoutException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @return the matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByUUID_G_P(
			String uuid, long groupId, boolean privateLayout)
		throws NoSuchLayoutException {

		return _uniquePersistenceFinderByUUID_G_P.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {uuid, groupId, privateLayout});
	}

	/**
	 * Returns the layout where uuid = &#63; and groupId = &#63; and privateLayout = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByUUID_G_P(
		String uuid, long groupId, boolean privateLayout,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G_P.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {uuid, groupId, privateLayout}, useFinderCache);
	}

	/**
	 * Removes the layout where uuid = &#63; and groupId = &#63; and privateLayout = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @return the layout that was removed
	 */
	@Override
	public Layout removeByUUID_G_P(
			String uuid, long groupId, boolean privateLayout)
		throws NoSuchLayoutException {

		Layout layout = findByUUID_G_P(uuid, groupId, privateLayout);

		return remove(layout);
	}

	/**
	 * Returns the number of layouts where uuid = &#63; and groupId = &#63; and privateLayout = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @return the number of matching layouts
	 */
	@Override
	public int countByUUID_G_P(
		String uuid, long groupId, boolean privateLayout) {

		return _uniquePersistenceFinderByUUID_G_P.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {uuid, groupId, privateLayout});
	}

	private CollectionPersistenceFinder<Layout, NoSuchLayoutException>
		_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the layouts where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			orderByComparator);
	}

	/**
	 * Returns the first layout in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<Layout> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			orderByComparator);
	}

	/**
	 * Removes all the layouts where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of layouts where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching layouts
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId});
	}

	private FilterCollectionPersistenceFinder<Layout, NoSuchLayoutException>
		_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByGroupId_First(
			long groupId, OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId},
			orderByComparator);
	}

	/**
	 * Returns the first layout in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByGroupId_First(
		long groupId, OrderByComparator<Layout> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layouts that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.filterFind(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId}, start,
			end, orderByComparator, groupId);
	}

	/**
	 * Removes all the layouts where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId});
	}

	/**
	 * Returns the number of layouts where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching layouts
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId});
	}

	/**
	 * Returns the number of layouts that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching layouts that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.filterCount(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId}, groupId);
	}

	private CollectionPersistenceFinder<Layout, NoSuchLayoutException>
		_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the layouts where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByCompanyId_First(
			long companyId, OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			orderByComparator);
	}

	/**
	 * Returns the first layout in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByCompanyId_First(
		long companyId, OrderByComparator<Layout> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			orderByComparator);
	}

	/**
	 * Removes all the layouts where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId});
	}

	/**
	 * Returns the number of layouts where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching layouts
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId});
	}

	private CollectionPersistenceFinder<Layout, NoSuchLayoutException>
		_collectionPersistenceFinderByParentPlid;

	/**
	 * Returns an ordered range of all the layouts where parentPlid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param parentPlid the parent plid
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByParentPlid(
		long parentPlid, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByParentPlid.find(
			FinderCacheUtil.getFinderCache(), new Object[] {parentPlid}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout in the ordered set where parentPlid = &#63;.
	 *
	 * @param parentPlid the parent plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByParentPlid_First(
			long parentPlid, OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		return _collectionPersistenceFinderByParentPlid.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {parentPlid},
			orderByComparator);
	}

	/**
	 * Returns the first layout in the ordered set where parentPlid = &#63;.
	 *
	 * @param parentPlid the parent plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByParentPlid_First(
		long parentPlid, OrderByComparator<Layout> orderByComparator) {

		return _collectionPersistenceFinderByParentPlid.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {parentPlid},
			orderByComparator);
	}

	/**
	 * Removes all the layouts where parentPlid = &#63; from the database.
	 *
	 * @param parentPlid the parent plid
	 */
	@Override
	public void removeByParentPlid(long parentPlid) {
		_collectionPersistenceFinderByParentPlid.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {parentPlid});
	}

	/**
	 * Returns the number of layouts where parentPlid = &#63;.
	 *
	 * @param parentPlid the parent plid
	 * @return the number of matching layouts
	 */
	@Override
	public int countByParentPlid(long parentPlid) {
		return _collectionPersistenceFinderByParentPlid.count(
			FinderCacheUtil.getFinderCache(), new Object[] {parentPlid});
	}

	private CollectionPersistenceFinder<Layout, NoSuchLayoutException>
		_collectionPersistenceFinderByIconImageId;

	/**
	 * Returns an ordered range of all the layouts where iconImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param iconImageId the icon image ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByIconImageId(
		long iconImageId, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByIconImageId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {iconImageId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout in the ordered set where iconImageId = &#63;.
	 *
	 * @param iconImageId the icon image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByIconImageId_First(
			long iconImageId, OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		return _collectionPersistenceFinderByIconImageId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {iconImageId},
			orderByComparator);
	}

	/**
	 * Returns the first layout in the ordered set where iconImageId = &#63;.
	 *
	 * @param iconImageId the icon image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByIconImageId_First(
		long iconImageId, OrderByComparator<Layout> orderByComparator) {

		return _collectionPersistenceFinderByIconImageId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {iconImageId},
			orderByComparator);
	}

	/**
	 * Removes all the layouts where iconImageId = &#63; from the database.
	 *
	 * @param iconImageId the icon image ID
	 */
	@Override
	public void removeByIconImageId(long iconImageId) {
		_collectionPersistenceFinderByIconImageId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {iconImageId});
	}

	/**
	 * Returns the number of layouts where iconImageId = &#63;.
	 *
	 * @param iconImageId the icon image ID
	 * @return the number of matching layouts
	 */
	@Override
	public int countByIconImageId(long iconImageId) {
		return _collectionPersistenceFinderByIconImageId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {iconImageId});
	}

	private CollectionPersistenceFinder<Layout, NoSuchLayoutException>
		_collectionPersistenceFinderByLayoutSetPrototypeLayoutERC;

	/**
	 * Returns an ordered range of all the layouts where layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByLayoutSetPrototypeLayoutERC(
		String layoutSetPrototypeLayoutERC, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByLayoutSetPrototypeLayoutERC.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetPrototypeLayoutERC}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout in the ordered set where layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByLayoutSetPrototypeLayoutERC_First(
			String layoutSetPrototypeLayoutERC,
			OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		return _collectionPersistenceFinderByLayoutSetPrototypeLayoutERC.
			findFirst(
				FinderCacheUtil.getFinderCache(),
				new Object[] {layoutSetPrototypeLayoutERC}, orderByComparator);
	}

	/**
	 * Returns the first layout in the ordered set where layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByLayoutSetPrototypeLayoutERC_First(
		String layoutSetPrototypeLayoutERC,
		OrderByComparator<Layout> orderByComparator) {

		return _collectionPersistenceFinderByLayoutSetPrototypeLayoutERC.
			fetchFirst(
				FinderCacheUtil.getFinderCache(),
				new Object[] {layoutSetPrototypeLayoutERC}, orderByComparator);
	}

	/**
	 * Removes all the layouts where layoutSetPrototypeLayoutERC = &#63; from the database.
	 *
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 */
	@Override
	public void removeByLayoutSetPrototypeLayoutERC(
		String layoutSetPrototypeLayoutERC) {

		_collectionPersistenceFinderByLayoutSetPrototypeLayoutERC.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetPrototypeLayoutERC});
	}

	/**
	 * Returns the number of layouts where layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @return the number of matching layouts
	 */
	@Override
	public int countByLayoutSetPrototypeLayoutERC(
		String layoutSetPrototypeLayoutERC) {

		return _collectionPersistenceFinderByLayoutSetPrototypeLayoutERC.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {layoutSetPrototypeLayoutERC});
	}

	private FilterCollectionPersistenceFinder<Layout, NoSuchLayoutException>
		_collectionPersistenceFinderByG_P;

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByG_P(
		long groupId, boolean privateLayout, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByG_P.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByG_P_First(
			long groupId, boolean privateLayout,
			OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		return _collectionPersistenceFinderByG_P.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout}, orderByComparator);
	}

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByG_P_First(
		long groupId, boolean privateLayout,
		OrderByComparator<Layout> orderByComparator) {

		return _collectionPersistenceFinderByG_P.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layouts that the user has permissions to view where groupId = &#63; and privateLayout = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_P(
		long groupId, boolean privateLayout, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return _collectionPersistenceFinderByG_P.filterFind(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Removes all the layouts where groupId = &#63; and privateLayout = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 */
	@Override
	public void removeByG_P(long groupId, boolean privateLayout) {
		_collectionPersistenceFinderByG_P.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout});
	}

	/**
	 * Returns the number of layouts where groupId = &#63; and privateLayout = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @return the number of matching layouts
	 */
	@Override
	public int countByG_P(long groupId, boolean privateLayout) {
		return _collectionPersistenceFinderByG_P.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout});
	}

	/**
	 * Returns the number of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @return the number of matching layouts that the user has permission to view
	 */
	@Override
	public int filterCountByG_P(long groupId, boolean privateLayout) {
		return _collectionPersistenceFinderByG_P.filterCount(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout}, groupId);
	}

	private FilterCollectionPersistenceFinder<Layout, NoSuchLayoutException>
		_collectionPersistenceFinderByG_T;

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByG_T(
		long groupId, String type, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByG_T.find(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, type},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByG_T_First(
			long groupId, String type,
			OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		return _collectionPersistenceFinderByG_T.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, type},
			orderByComparator);
	}

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByG_T_First(
		long groupId, String type,
		OrderByComparator<Layout> orderByComparator) {

		return _collectionPersistenceFinderByG_T.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, type},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layouts that the user has permissions to view where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_T(
		long groupId, String type, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return _collectionPersistenceFinderByG_T.filterFind(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, type},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Removes all the layouts where groupId = &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 */
	@Override
	public void removeByG_T(long groupId, String type) {
		_collectionPersistenceFinderByG_T.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, type});
	}

	/**
	 * Returns the number of layouts where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @return the number of matching layouts
	 */
	@Override
	public int countByG_T(long groupId, String type) {
		return _collectionPersistenceFinderByG_T.count(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, type});
	}

	/**
	 * Returns the number of layouts that the user has permission to view where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @return the number of matching layouts that the user has permission to view
	 */
	@Override
	public int filterCountByG_T(long groupId, String type) {
		return _collectionPersistenceFinderByG_T.filterCount(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, type},
			groupId);
	}

	private FilterCollectionPersistenceFinder<Layout, NoSuchLayoutException>
		_collectionPersistenceFinderByG_MLPTEERC;

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and masterLayoutPageTemplateEntryERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param masterLayoutPageTemplateEntryERC the master layout page template entry erc
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByG_MLPTEERC(
		long groupId, String masterLayoutPageTemplateEntryERC, int start,
		int end, OrderByComparator<Layout> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_MLPTEERC.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, masterLayoutPageTemplateEntryERC}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and masterLayoutPageTemplateEntryERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param masterLayoutPageTemplateEntryERC the master layout page template entry erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByG_MLPTEERC_First(
			long groupId, String masterLayoutPageTemplateEntryERC,
			OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		return _collectionPersistenceFinderByG_MLPTEERC.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, masterLayoutPageTemplateEntryERC},
			orderByComparator);
	}

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and masterLayoutPageTemplateEntryERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param masterLayoutPageTemplateEntryERC the master layout page template entry erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByG_MLPTEERC_First(
		long groupId, String masterLayoutPageTemplateEntryERC,
		OrderByComparator<Layout> orderByComparator) {

		return _collectionPersistenceFinderByG_MLPTEERC.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, masterLayoutPageTemplateEntryERC},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layouts that the user has permissions to view where groupId = &#63; and masterLayoutPageTemplateEntryERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param masterLayoutPageTemplateEntryERC the master layout page template entry erc
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_MLPTEERC(
		long groupId, String masterLayoutPageTemplateEntryERC, int start,
		int end, OrderByComparator<Layout> orderByComparator) {

		return _collectionPersistenceFinderByG_MLPTEERC.filterFind(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, masterLayoutPageTemplateEntryERC}, start,
			end, orderByComparator, groupId);
	}

	/**
	 * Removes all the layouts where groupId = &#63; and masterLayoutPageTemplateEntryERC = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param masterLayoutPageTemplateEntryERC the master layout page template entry erc
	 */
	@Override
	public void removeByG_MLPTEERC(
		long groupId, String masterLayoutPageTemplateEntryERC) {

		_collectionPersistenceFinderByG_MLPTEERC.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, masterLayoutPageTemplateEntryERC});
	}

	/**
	 * Returns the number of layouts where groupId = &#63; and masterLayoutPageTemplateEntryERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param masterLayoutPageTemplateEntryERC the master layout page template entry erc
	 * @return the number of matching layouts
	 */
	@Override
	public int countByG_MLPTEERC(
		long groupId, String masterLayoutPageTemplateEntryERC) {

		return _collectionPersistenceFinderByG_MLPTEERC.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, masterLayoutPageTemplateEntryERC});
	}

	/**
	 * Returns the number of layouts that the user has permission to view where groupId = &#63; and masterLayoutPageTemplateEntryERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param masterLayoutPageTemplateEntryERC the master layout page template entry erc
	 * @return the number of matching layouts that the user has permission to view
	 */
	@Override
	public int filterCountByG_MLPTEERC(
		long groupId, String masterLayoutPageTemplateEntryERC) {

		return _collectionPersistenceFinderByG_MLPTEERC.filterCount(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, masterLayoutPageTemplateEntryERC}, groupId);
	}

	private CollectionPersistenceFinder<Layout, NoSuchLayoutException>
		_collectionPersistenceFinderByP_I;

	/**
	 * Returns an ordered range of all the layouts where privateLayout = &#63; and iconImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param privateLayout the private layout
	 * @param iconImageId the icon image ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByP_I(
		boolean privateLayout, long iconImageId, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByP_I.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {privateLayout, iconImageId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout in the ordered set where privateLayout = &#63; and iconImageId = &#63;.
	 *
	 * @param privateLayout the private layout
	 * @param iconImageId the icon image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByP_I_First(
			boolean privateLayout, long iconImageId,
			OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		return _collectionPersistenceFinderByP_I.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {privateLayout, iconImageId}, orderByComparator);
	}

	/**
	 * Returns the first layout in the ordered set where privateLayout = &#63; and iconImageId = &#63;.
	 *
	 * @param privateLayout the private layout
	 * @param iconImageId the icon image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByP_I_First(
		boolean privateLayout, long iconImageId,
		OrderByComparator<Layout> orderByComparator) {

		return _collectionPersistenceFinderByP_I.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {privateLayout, iconImageId}, orderByComparator);
	}

	/**
	 * Removes all the layouts where privateLayout = &#63; and iconImageId = &#63; from the database.
	 *
	 * @param privateLayout the private layout
	 * @param iconImageId the icon image ID
	 */
	@Override
	public void removeByP_I(boolean privateLayout, long iconImageId) {
		_collectionPersistenceFinderByP_I.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {privateLayout, iconImageId});
	}

	/**
	 * Returns the number of layouts where privateLayout = &#63; and iconImageId = &#63;.
	 *
	 * @param privateLayout the private layout
	 * @param iconImageId the icon image ID
	 * @return the number of matching layouts
	 */
	@Override
	public int countByP_I(boolean privateLayout, long iconImageId) {
		return _collectionPersistenceFinderByP_I.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {privateLayout, iconImageId});
	}

	private CollectionPersistenceFinder<Layout, NoSuchLayoutException>
		_collectionPersistenceFinderByC_C;

	/**
	 * Returns an ordered range of all the layouts where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByC_C(
		long classNameId, long classPK, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, new long[] {classPK}}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByC_C_First(
			long classNameId, long classPK,
			OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		Layout layout = fetchByC_C_First(
			classNameId, classPK, orderByComparator);

		if (layout != null) {
			return layout;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("classNameId=");
		sb.append(classNameId);

		sb.append(", classPK=");
		sb.append(classPK);

		sb.append("}");

		throw new NoSuchLayoutException(sb.toString());
	}

	/**
	 * Returns the first layout in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByC_C_First(
		long classNameId, long classPK,
		OrderByComparator<Layout> orderByComparator) {

		return _collectionPersistenceFinderByC_C.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, new long[] {classPK}},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layouts where classNameId = &#63; and classPK = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPKs the class pks
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByC_C(
		long classNameId, long[] classPKs, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, ArrayUtil.sortedUnique(classPKs)}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the layouts where classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	@Override
	public void removeByC_C(long classNameId, long classPK) {
		_collectionPersistenceFinderByC_C.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, new long[] {classPK}});
	}

	/**
	 * Returns the number of layouts where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching layouts
	 */
	@Override
	public int countByC_C(long classNameId, long classPK) {
		return _collectionPersistenceFinderByC_C.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, new long[] {classPK}});
	}

	/**
	 * Returns the number of layouts where classNameId = &#63; and classPK = any &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPKs the class pks
	 * @return the number of matching layouts
	 */
	@Override
	public int countByC_C(long classNameId, long[] classPKs) {
		return _collectionPersistenceFinderByC_C.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, ArrayUtil.sortedUnique(classPKs)});
	}

	private CollectionPersistenceFinder<Layout, NoSuchLayoutException>
		_collectionPersistenceFinderByPLPTEERC_PLPTESERC;

	/**
	 * Returns an ordered range of all the layouts where portletLayoutPageTemplateEntryERC = &#63; and portletLayoutPageTemplateEntryScopeERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param portletLayoutPageTemplateEntryERC the portlet layout page template entry erc
	 * @param portletLayoutPageTemplateEntryScopeERC the portlet layout page template entry scope erc
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByPLPTEERC_PLPTESERC(
		String portletLayoutPageTemplateEntryERC,
		String portletLayoutPageTemplateEntryScopeERC, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByPLPTEERC_PLPTESERC.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				portletLayoutPageTemplateEntryERC,
				portletLayoutPageTemplateEntryScopeERC
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout in the ordered set where portletLayoutPageTemplateEntryERC = &#63; and portletLayoutPageTemplateEntryScopeERC = &#63;.
	 *
	 * @param portletLayoutPageTemplateEntryERC the portlet layout page template entry erc
	 * @param portletLayoutPageTemplateEntryScopeERC the portlet layout page template entry scope erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByPLPTEERC_PLPTESERC_First(
			String portletLayoutPageTemplateEntryERC,
			String portletLayoutPageTemplateEntryScopeERC,
			OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		return _collectionPersistenceFinderByPLPTEERC_PLPTESERC.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				portletLayoutPageTemplateEntryERC,
				portletLayoutPageTemplateEntryScopeERC
			},
			orderByComparator);
	}

	/**
	 * Returns the first layout in the ordered set where portletLayoutPageTemplateEntryERC = &#63; and portletLayoutPageTemplateEntryScopeERC = &#63;.
	 *
	 * @param portletLayoutPageTemplateEntryERC the portlet layout page template entry erc
	 * @param portletLayoutPageTemplateEntryScopeERC the portlet layout page template entry scope erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByPLPTEERC_PLPTESERC_First(
		String portletLayoutPageTemplateEntryERC,
		String portletLayoutPageTemplateEntryScopeERC,
		OrderByComparator<Layout> orderByComparator) {

		return _collectionPersistenceFinderByPLPTEERC_PLPTESERC.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				portletLayoutPageTemplateEntryERC,
				portletLayoutPageTemplateEntryScopeERC
			},
			orderByComparator);
	}

	/**
	 * Removes all the layouts where portletLayoutPageTemplateEntryERC = &#63; and portletLayoutPageTemplateEntryScopeERC = &#63; from the database.
	 *
	 * @param portletLayoutPageTemplateEntryERC the portlet layout page template entry erc
	 * @param portletLayoutPageTemplateEntryScopeERC the portlet layout page template entry scope erc
	 */
	@Override
	public void removeByPLPTEERC_PLPTESERC(
		String portletLayoutPageTemplateEntryERC,
		String portletLayoutPageTemplateEntryScopeERC) {

		_collectionPersistenceFinderByPLPTEERC_PLPTESERC.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				portletLayoutPageTemplateEntryERC,
				portletLayoutPageTemplateEntryScopeERC
			});
	}

	/**
	 * Returns the number of layouts where portletLayoutPageTemplateEntryERC = &#63; and portletLayoutPageTemplateEntryScopeERC = &#63;.
	 *
	 * @param portletLayoutPageTemplateEntryERC the portlet layout page template entry erc
	 * @param portletLayoutPageTemplateEntryScopeERC the portlet layout page template entry scope erc
	 * @return the number of matching layouts
	 */
	@Override
	public int countByPLPTEERC_PLPTESERC(
		String portletLayoutPageTemplateEntryERC,
		String portletLayoutPageTemplateEntryScopeERC) {

		return _collectionPersistenceFinderByPLPTEERC_PLPTESERC.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				portletLayoutPageTemplateEntryERC,
				portletLayoutPageTemplateEntryScopeERC
			});
	}

	private UniquePersistenceFinder<Layout, NoSuchLayoutException>
		_uniquePersistenceFinderByG_P_L;

	/**
	 * Returns the layout where groupId = &#63; and privateLayout = &#63; and layoutId = &#63; or throws a <code>NoSuchLayoutException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutId the layout ID
	 * @return the matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByG_P_L(
			long groupId, boolean privateLayout, long layoutId)
		throws NoSuchLayoutException {

		return _uniquePersistenceFinderByG_P_L.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout, layoutId});
	}

	/**
	 * Returns the layout where groupId = &#63; and privateLayout = &#63; and layoutId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutId the layout ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByG_P_L(
		long groupId, boolean privateLayout, long layoutId,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByG_P_L.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout, layoutId}, useFinderCache);
	}

	/**
	 * Removes the layout where groupId = &#63; and privateLayout = &#63; and layoutId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutId the layout ID
	 * @return the layout that was removed
	 */
	@Override
	public Layout removeByG_P_L(
			long groupId, boolean privateLayout, long layoutId)
		throws NoSuchLayoutException {

		Layout layout = findByG_P_L(groupId, privateLayout, layoutId);

		return remove(layout);
	}

	/**
	 * Returns the number of layouts where groupId = &#63; and privateLayout = &#63; and layoutId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutId the layout ID
	 * @return the number of matching layouts
	 */
	@Override
	public int countByG_P_L(
		long groupId, boolean privateLayout, long layoutId) {

		return _uniquePersistenceFinderByG_P_L.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout, layoutId});
	}

	private FinderPath _finderPathWithPaginationFindByG_P_P;
	private FinderPath _finderPathWithoutPaginationFindByG_P_P;
	private FinderPath _finderPathCountByG_P_P;
	private FinderPath _finderPathWithPaginationCountByG_P_P;

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByG_P_P(
		long groupId, boolean privateLayout, long parentLayoutId, int start,
		int end, OrderByComparator<Layout> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_P_P;
					finderArgs = new Object[] {
						groupId, privateLayout, parentLayoutId
					};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_P_P;
				finderArgs = new Object[] {
					groupId, privateLayout, parentLayoutId, start, end,
					orderByComparator
				};
			}

			List<Layout> list = null;

			if (useFinderCache) {
				list = (List<Layout>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (Layout layout : list) {
						if ((groupId != layout.getGroupId()) ||
							(privateLayout != layout.isPrivateLayout()) ||
							(parentLayoutId != layout.getParentLayoutId())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						5 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(5);
				}

				sb.append(_SQL_SELECT_LAYOUT_WHERE);

				sb.append(_FINDER_COLUMN_G_P_P_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_P_PRIVATELAYOUT_2);

				sb.append(_FINDER_COLUMN_G_P_P_PARENTLAYOUTID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ENTITY_ALIAS_PREFIX, orderByComparator);
				}
				else {
					sb.append(LayoutModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(privateLayout);

					queryPos.add(parentLayoutId);

					list = (List<Layout>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						FinderCacheUtil.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
	}

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByG_P_P_First(
			long groupId, boolean privateLayout, long parentLayoutId,
			OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		Layout layout = fetchByG_P_P_First(
			groupId, privateLayout, parentLayoutId, orderByComparator);

		if (layout != null) {
			return layout;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", privateLayout=");
		sb.append(privateLayout);

		sb.append(", parentLayoutId=");
		sb.append(parentLayoutId);

		sb.append("}");

		throw new NoSuchLayoutException(sb.toString());
	}

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByG_P_P_First(
		long groupId, boolean privateLayout, long parentLayoutId,
		OrderByComparator<Layout> orderByComparator) {

		List<Layout> list = findByG_P_P(
			groupId, privateLayout, parentLayoutId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns an ordered range of all the layouts that the user has permissions to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_P_P(
		long groupId, boolean privateLayout, long parentLayoutId, int start,
		int end, OrderByComparator<Layout> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P_P(
				groupId, privateLayout, parentLayoutId, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_P_P(
					groupId, privateLayout, parentLayoutId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator),
				groupId);
		}

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(6);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_P_GROUPID_2_SQL);

		sb.append(_FINDER_COLUMN_G_P_P_PRIVATELAYOUT_2_SQL);

		sb.append(_FINDER_COLUMN_G_P_P_PARENTLAYOUTID_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_2);
		}

		if (orderByComparator != null) {
			if (getDB().isSupportsInlineDistinct()) {
				appendOrderByComparator(
					sb, _ENTITY_ALIAS_PREFIX, orderByComparator, true);
			}
			else {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_TABLE, orderByComparator, true);
			}
		}
		else {
			if (getDB().isSupportsInlineDistinct()) {
				sb.append(LayoutModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(LayoutModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), Layout.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, LayoutImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, LayoutImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(privateLayout);

			queryPos.add(parentLayoutId);

			return (List<Layout>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	/**
	 * Returns all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @return the matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_P_P(
		long groupId, boolean privateLayout, long[] parentLayoutIds) {

		return filterFindByG_P_P(
			groupId, privateLayout, parentLayoutIds, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_P_P(
		long groupId, boolean privateLayout, long[] parentLayoutIds, int start,
		int end) {

		return filterFindByG_P_P(
			groupId, privateLayout, parentLayoutIds, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_P_P(
		long groupId, boolean privateLayout, long[] parentLayoutIds, int start,
		int end, OrderByComparator<Layout> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P_P(
				groupId, privateLayout, parentLayoutIds, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_P_P(
					groupId, privateLayout, parentLayoutIds, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator),
				groupId);
		}

		if (parentLayoutIds == null) {
			parentLayoutIds = new long[0];
		}
		else if (parentLayoutIds.length > 1) {
			parentLayoutIds = ArrayUtil.sortedUnique(parentLayoutIds);
		}

		StringBundler sb = new StringBundler();

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_P_GROUPID_2_SQL);

		sb.append(_FINDER_COLUMN_G_P_P_PRIVATELAYOUT_2_SQL);

		if (parentLayoutIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_G_P_P_PARENTLAYOUTID_7_SQL);

			sb.append(StringUtil.merge(parentLayoutIds));

			sb.append(")");

			sb.append(")");
		}

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		sb.append(" AND layout.system_ = [$FALSE$]");

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_2);
		}

		if (orderByComparator != null) {
			if (getDB().isSupportsInlineDistinct()) {
				appendOrderByComparator(
					sb, _ENTITY_ALIAS_PREFIX, orderByComparator, true);
			}
			else {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_TABLE, orderByComparator, true);
			}
		}
		else {
			if (getDB().isSupportsInlineDistinct()) {
				sb.append(LayoutModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(LayoutModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), Layout.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, LayoutImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, LayoutImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(privateLayout);

			return (List<Layout>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	/**
	 * Returns all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @return the matching layouts
	 */
	@Override
	public List<Layout> findByG_P_P(
		long groupId, boolean privateLayout, long[] parentLayoutIds) {

		return findByG_P_P(
			groupId, privateLayout, parentLayoutIds, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	@Override
	public List<Layout> findByG_P_P(
		long groupId, boolean privateLayout, long[] parentLayoutIds, int start,
		int end) {

		return findByG_P_P(
			groupId, privateLayout, parentLayoutIds, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByG_P_P(
		long groupId, boolean privateLayout, long[] parentLayoutIds, int start,
		int end, OrderByComparator<Layout> orderByComparator) {

		return findByG_P_P(
			groupId, privateLayout, parentLayoutIds, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByG_P_P(
		long groupId, boolean privateLayout, long[] parentLayoutIds, int start,
		int end, OrderByComparator<Layout> orderByComparator,
		boolean useFinderCache) {

		if (parentLayoutIds == null) {
			parentLayoutIds = new long[0];
		}
		else if (parentLayoutIds.length > 1) {
			parentLayoutIds = ArrayUtil.sortedUnique(parentLayoutIds);
		}

		if (parentLayoutIds.length == 1) {
			return findByG_P_P(
				groupId, privateLayout, parentLayoutIds[0], start, end,
				orderByComparator);
		}

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderArgs = new Object[] {
						groupId, privateLayout,
						StringUtil.merge(parentLayoutIds)
					};
				}
			}
			else if (useFinderCache) {
				finderArgs = new Object[] {
					groupId, privateLayout, StringUtil.merge(parentLayoutIds),
					start, end, orderByComparator
				};
			}

			List<Layout> list = null;

			if (useFinderCache) {
				list = (List<Layout>)FinderCacheUtil.getResult(
					_finderPathWithPaginationFindByG_P_P, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (Layout layout : list) {
						if ((groupId != layout.getGroupId()) ||
							(privateLayout != layout.isPrivateLayout()) ||
							!ArrayUtil.contains(
								parentLayoutIds, layout.getParentLayoutId())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				try {
					if ((start == QueryUtil.ALL_POS) &&
						(end == QueryUtil.ALL_POS) &&
						(databaseInMaxParameters > 0) &&
						(parentLayoutIds.length > databaseInMaxParameters)) {

						list = new ArrayList<Layout>();

						long[][] parentLayoutIdsPages =
							(long[][])ArrayUtil.split(
								parentLayoutIds, databaseInMaxParameters);

						for (long[] parentLayoutIdsPage :
								parentLayoutIdsPages) {

							list.addAll(
								_findByG_P_P(
									groupId, privateLayout, parentLayoutIdsPage,
									start, end, orderByComparator));
						}

						Collections.sort(list, orderByComparator);

						list = Collections.unmodifiableList(list);
					}
					else {
						list = _findByG_P_P(
							groupId, privateLayout, parentLayoutIds, start, end,
							orderByComparator);
					}

					cacheResult(list);

					if (useFinderCache) {
						FinderCacheUtil.putResult(
							_finderPathWithPaginationFindByG_P_P, finderArgs,
							list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
			}

			return list;
		}
	}

	private List<Layout> _findByG_P_P(
		long groupId, boolean privateLayout, long[] parentLayoutIds, int start,
		int end, OrderByComparator<Layout> orderByComparator) {

		List<Layout> list = null;

		StringBundler sb = new StringBundler();

		sb.append(_SQL_SELECT_LAYOUT_WHERE);

		sb.append(_FINDER_COLUMN_G_P_P_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_P_PRIVATELAYOUT_2);

		if (parentLayoutIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_G_P_P_PARENTLAYOUTID_7);

			sb.append(StringUtil.merge(parentLayoutIds));

			sb.append(")");

			sb.append(")");
		}

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		sb.append(" AND layout.system = [$FALSE$]");

		if (orderByComparator != null) {
			appendOrderByComparator(
				sb, _ENTITY_ALIAS_PREFIX, orderByComparator);
		}
		else {
			sb.append(LayoutModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Session session = null;

		try {
			session = openSession();

			Query query = session.createQuery(sql);

			QueryPos queryPos = QueryPos.getInstance(query);

			queryPos.add(groupId);

			queryPos.add(privateLayout);

			list = (List<Layout>)QueryUtil.list(
				query, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return list;
	}

	/**
	 * Removes all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 */
	@Override
	public void removeByG_P_P(
		long groupId, boolean privateLayout, long parentLayoutId) {

		for (Layout layout :
				findByG_P_P(
					groupId, privateLayout, parentLayoutId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(layout);
		}
	}

	/**
	 * Returns the number of layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @return the number of matching layouts
	 */
	@Override
	public int countByG_P_P(
		long groupId, boolean privateLayout, long parentLayoutId) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			FinderPath finderPath = _finderPathCountByG_P_P;

			Object[] finderArgs = new Object[] {
				groupId, privateLayout, parentLayoutId
			};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_LAYOUT_WHERE);

				sb.append(_FINDER_COLUMN_G_P_P_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_P_PRIVATELAYOUT_2);

				sb.append(_FINDER_COLUMN_G_P_P_PARENTLAYOUTID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(privateLayout);

					queryPos.add(parentLayoutId);

					count = (Long)query.uniqueResult();

					FinderCacheUtil.putResult(finderPath, finderArgs, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	/**
	 * Returns the number of layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @return the number of matching layouts
	 */
	@Override
	public int countByG_P_P(
		long groupId, boolean privateLayout, long[] parentLayoutIds) {

		if (parentLayoutIds == null) {
			parentLayoutIds = new long[0];
		}
		else if (parentLayoutIds.length > 1) {
			parentLayoutIds = ArrayUtil.sortedUnique(parentLayoutIds);
		}

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			Object[] finderArgs = new Object[] {
				groupId, privateLayout, StringUtil.merge(parentLayoutIds)
			};

			Long count = (Long)FinderCacheUtil.getResult(
				_finderPathWithPaginationCountByG_P_P, finderArgs, this);

			if (count == null) {
				try {
					if ((databaseInMaxParameters > 0) &&
						(parentLayoutIds.length > databaseInMaxParameters)) {

						count = Long.valueOf(0);

						long[][] parentLayoutIdsPages =
							(long[][])ArrayUtil.split(
								parentLayoutIds, databaseInMaxParameters);

						for (long[] parentLayoutIdsPage :
								parentLayoutIdsPages) {

							count += Long.valueOf(
								_countByG_P_P(
									groupId, privateLayout,
									parentLayoutIdsPage));
						}
					}
					else {
						count = Long.valueOf(
							_countByG_P_P(
								groupId, privateLayout, parentLayoutIds));
					}

					FinderCacheUtil.putResult(
						_finderPathWithPaginationCountByG_P_P, finderArgs,
						count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
			}

			return count.intValue();
		}
	}

	private int _countByG_P_P(
		long groupId, boolean privateLayout, long[] parentLayoutIds) {

		Long count = null;

		StringBundler sb = new StringBundler();

		sb.append(_SQL_COUNT_LAYOUT_WHERE);

		sb.append(_FINDER_COLUMN_G_P_P_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_P_PRIVATELAYOUT_2);

		if (parentLayoutIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_G_P_P_PARENTLAYOUTID_7);

			sb.append(StringUtil.merge(parentLayoutIds));

			sb.append(")");

			sb.append(")");
		}

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		sb.append(" AND layout.system = [$FALSE$]");

		String sql = sb.toString();

		Session session = null;

		try {
			session = openSession();

			Query query = session.createQuery(sql);

			QueryPos queryPos = QueryPos.getInstance(query);

			queryPos.add(groupId);

			queryPos.add(privateLayout);

			count = (Long)query.uniqueResult();
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return count.intValue();
	}

	/**
	 * Returns the number of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @return the number of matching layouts that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_P(
		long groupId, boolean privateLayout, long parentLayoutId) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_P_P(groupId, privateLayout, parentLayoutId);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<Layout> layouts = findByG_P_P(
				groupId, privateLayout, parentLayoutId);

			layouts = InlineSQLHelperUtil.filter(layouts, groupId);

			return layouts.size();
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_LAYOUT_WHERE);

		sb.append(_FINDER_COLUMN_G_P_P_GROUPID_2_SQL);

		sb.append(_FINDER_COLUMN_G_P_P_PRIVATELAYOUT_2_SQL);

		sb.append(_FINDER_COLUMN_G_P_P_PARENTLAYOUTID_2_SQL);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), Layout.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(privateLayout);

			queryPos.add(parentLayoutId);

			Long count = (Long)sqlQuery.uniqueResult();

			return count.intValue();
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	/**
	 * Returns the number of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @return the number of matching layouts that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_P(
		long groupId, boolean privateLayout, long[] parentLayoutIds) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_P_P(groupId, privateLayout, parentLayoutIds);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<Layout> layouts = InlineSQLHelperUtil.filter(
				findByG_P_P(groupId, privateLayout, parentLayoutIds), groupId);

			return layouts.size();
		}

		if (parentLayoutIds == null) {
			parentLayoutIds = new long[0];
		}
		else if (parentLayoutIds.length > 1) {
			parentLayoutIds = ArrayUtil.sortedUnique(parentLayoutIds);
		}

		StringBundler sb = new StringBundler();

		sb.append(_FILTER_SQL_COUNT_LAYOUT_WHERE);

		sb.append(_FINDER_COLUMN_G_P_P_GROUPID_2_SQL);

		sb.append(_FINDER_COLUMN_G_P_P_PRIVATELAYOUT_2_SQL);

		if (parentLayoutIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_G_P_P_PARENTLAYOUTID_7_SQL);

			sb.append(StringUtil.merge(parentLayoutIds));

			sb.append(")");

			sb.append(")");
		}

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		sb.append(" AND layout.system_ = [$FALSE$]");

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), Layout.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(privateLayout);

			Long count = (Long)sqlQuery.uniqueResult();

			return count.intValue();
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	private static final String _FINDER_COLUMN_G_P_P_GROUPID_2 =
		"layout.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_P_GROUPID_2_SQL =
		"layout.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_P_PRIVATELAYOUT_2 =
		"layout.privateLayout = ? AND ";

	private static final String _FINDER_COLUMN_G_P_P_PRIVATELAYOUT_2_SQL =
		"layout.privateLayout = ? AND ";

	private static final String _FINDER_COLUMN_G_P_P_PARENTLAYOUTID_2 =
		"layout.parentLayoutId = ? AND layout.system = [$FALSE$]";

	private static final String _FINDER_COLUMN_G_P_P_PARENTLAYOUTID_7 =
		"layout.parentLayoutId IN (";

	private static final String _FINDER_COLUMN_G_P_P_PARENTLAYOUTID_2_SQL =
		"layout.parentLayoutId = ? AND layout.system_ = [$FALSE$]";

	private static final String _FINDER_COLUMN_G_P_P_PARENTLAYOUTID_7_SQL =
		"layout.parentLayoutId IN (";

	private FilterCollectionPersistenceFinder<Layout, NoSuchLayoutException>
		_collectionPersistenceFinderByG_P_T;

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param type the type
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByG_P_T(
		long groupId, boolean privateLayout, String type, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByG_P_T.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout, new String[] {type}}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByG_P_T_First(
			long groupId, boolean privateLayout, String type,
			OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		Layout layout = fetchByG_P_T_First(
			groupId, privateLayout, type, orderByComparator);

		if (layout != null) {
			return layout;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", privateLayout=");
		sb.append(privateLayout);

		sb.append(", type=");
		sb.append(type);

		sb.append("}");

		throw new NoSuchLayoutException(sb.toString());
	}

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByG_P_T_First(
		long groupId, boolean privateLayout, String type,
		OrderByComparator<Layout> orderByComparator) {

		return _collectionPersistenceFinderByG_P_T.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout, new String[] {type}},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layouts that the user has permissions to view where groupId = &#63; and privateLayout = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param type the type
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_P_T(
		long groupId, boolean privateLayout, String type, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return _collectionPersistenceFinderByG_P_T.filterFind(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout, new String[] {type}}, start,
			end, orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param types the types
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_P_T(
		long groupId, boolean privateLayout, String[] types, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return _collectionPersistenceFinderByG_P_T.filterFind(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				groupId, privateLayout, ArrayUtil.sortedUnique(types)
			},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and type = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param types the types
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByG_P_T(
		long groupId, boolean privateLayout, String[] types, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByG_P_T.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				groupId, privateLayout, ArrayUtil.sortedUnique(types)
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the layouts where groupId = &#63; and privateLayout = &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param type the type
	 */
	@Override
	public void removeByG_P_T(
		long groupId, boolean privateLayout, String type) {

		_collectionPersistenceFinderByG_P_T.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout, new String[] {type}});
	}

	/**
	 * Returns the number of layouts where groupId = &#63; and privateLayout = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param type the type
	 * @return the number of matching layouts
	 */
	@Override
	public int countByG_P_T(long groupId, boolean privateLayout, String type) {
		return _collectionPersistenceFinderByG_P_T.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout, new String[] {type}});
	}

	/**
	 * Returns the number of layouts where groupId = &#63; and privateLayout = &#63; and type = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param types the types
	 * @return the number of matching layouts
	 */
	@Override
	public int countByG_P_T(
		long groupId, boolean privateLayout, String[] types) {

		return _collectionPersistenceFinderByG_P_T.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				groupId, privateLayout, ArrayUtil.sortedUnique(types)
			});
	}

	/**
	 * Returns the number of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param type the type
	 * @return the number of matching layouts that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_T(
		long groupId, boolean privateLayout, String type) {

		return _collectionPersistenceFinderByG_P_T.filterCount(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout, new String[] {type}},
			groupId);
	}

	/**
	 * Returns the number of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and type = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param types the types
	 * @return the number of matching layouts that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_T(
		long groupId, boolean privateLayout, String[] types) {

		return _collectionPersistenceFinderByG_P_T.filterCount(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				groupId, privateLayout, ArrayUtil.sortedUnique(types)
			},
			groupId);
	}

	private FilterCollectionPersistenceFinder<Layout, NoSuchLayoutException>
		_collectionPersistenceFinderByG_P_S;

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param system the system
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByG_P_S(
		long groupId, boolean privateLayout, boolean system, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByG_P_S.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout, system}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63; and system = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByG_P_S_First(
			long groupId, boolean privateLayout, boolean system,
			OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		return _collectionPersistenceFinderByG_P_S.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout, system}, orderByComparator);
	}

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63; and system = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByG_P_S_First(
		long groupId, boolean privateLayout, boolean system,
		OrderByComparator<Layout> orderByComparator) {

		return _collectionPersistenceFinderByG_P_S.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout, system}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layouts that the user has permissions to view where groupId = &#63; and privateLayout = &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param system the system
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_P_S(
		long groupId, boolean privateLayout, boolean system, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return _collectionPersistenceFinderByG_P_S.filterFind(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout, system}, start, end,
			orderByComparator, groupId);
	}

	/**
	 * Removes all the layouts where groupId = &#63; and privateLayout = &#63; and system = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param system the system
	 */
	@Override
	public void removeByG_P_S(
		long groupId, boolean privateLayout, boolean system) {

		_collectionPersistenceFinderByG_P_S.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout, system});
	}

	/**
	 * Returns the number of layouts where groupId = &#63; and privateLayout = &#63; and system = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param system the system
	 * @return the number of matching layouts
	 */
	@Override
	public int countByG_P_S(
		long groupId, boolean privateLayout, boolean system) {

		return _collectionPersistenceFinderByG_P_S.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout, system});
	}

	/**
	 * Returns the number of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and system = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param system the system
	 * @return the number of matching layouts that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_S(
		long groupId, boolean privateLayout, boolean system) {

		return _collectionPersistenceFinderByG_P_S.filterCount(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout, system}, groupId);
	}

	private UniquePersistenceFinder<Layout, NoSuchLayoutException>
		_uniquePersistenceFinderByG_P_F;

	/**
	 * Returns the layout where groupId = &#63; and privateLayout = &#63; and friendlyURL = &#63; or throws a <code>NoSuchLayoutException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param friendlyURL the friendly url
	 * @return the matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByG_P_F(
			long groupId, boolean privateLayout, String friendlyURL)
		throws NoSuchLayoutException {

		return _uniquePersistenceFinderByG_P_F.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout, friendlyURL});
	}

	/**
	 * Returns the layout where groupId = &#63; and privateLayout = &#63; and friendlyURL = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param friendlyURL the friendly url
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByG_P_F(
		long groupId, boolean privateLayout, String friendlyURL,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByG_P_F.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout, friendlyURL}, useFinderCache);
	}

	/**
	 * Removes the layout where groupId = &#63; and privateLayout = &#63; and friendlyURL = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param friendlyURL the friendly url
	 * @return the layout that was removed
	 */
	@Override
	public Layout removeByG_P_F(
			long groupId, boolean privateLayout, String friendlyURL)
		throws NoSuchLayoutException {

		Layout layout = findByG_P_F(groupId, privateLayout, friendlyURL);

		return remove(layout);
	}

	/**
	 * Returns the number of layouts where groupId = &#63; and privateLayout = &#63; and friendlyURL = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param friendlyURL the friendly url
	 * @return the number of matching layouts
	 */
	@Override
	public int countByG_P_F(
		long groupId, boolean privateLayout, String friendlyURL) {

		return _uniquePersistenceFinderByG_P_F.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout, friendlyURL});
	}

	private FilterCollectionPersistenceFinder<Layout, NoSuchLayoutException>
		_collectionPersistenceFinderByG_P_LSPLE;

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByG_P_LSPLE(
		long groupId, boolean privateLayout, String layoutSetPrototypeLayoutERC,
		int start, int end, OrderByComparator<Layout> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_P_LSPLE.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout, layoutSetPrototypeLayoutERC},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63; and layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByG_P_LSPLE_First(
			long groupId, boolean privateLayout,
			String layoutSetPrototypeLayoutERC,
			OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		return _collectionPersistenceFinderByG_P_LSPLE.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout, layoutSetPrototypeLayoutERC},
			orderByComparator);
	}

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63; and layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByG_P_LSPLE_First(
		long groupId, boolean privateLayout, String layoutSetPrototypeLayoutERC,
		OrderByComparator<Layout> orderByComparator) {

		return _collectionPersistenceFinderByG_P_LSPLE.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout, layoutSetPrototypeLayoutERC},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layouts that the user has permissions to view where groupId = &#63; and privateLayout = &#63; and layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_P_LSPLE(
		long groupId, boolean privateLayout, String layoutSetPrototypeLayoutERC,
		int start, int end, OrderByComparator<Layout> orderByComparator) {

		return _collectionPersistenceFinderByG_P_LSPLE.filterFind(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout, layoutSetPrototypeLayoutERC},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Removes all the layouts where groupId = &#63; and privateLayout = &#63; and layoutSetPrototypeLayoutERC = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 */
	@Override
	public void removeByG_P_LSPLE(
		long groupId, boolean privateLayout,
		String layoutSetPrototypeLayoutERC) {

		_collectionPersistenceFinderByG_P_LSPLE.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout, layoutSetPrototypeLayoutERC});
	}

	/**
	 * Returns the number of layouts where groupId = &#63; and privateLayout = &#63; and layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @return the number of matching layouts
	 */
	@Override
	public int countByG_P_LSPLE(
		long groupId, boolean privateLayout,
		String layoutSetPrototypeLayoutERC) {

		return _collectionPersistenceFinderByG_P_LSPLE.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout, layoutSetPrototypeLayoutERC});
	}

	/**
	 * Returns the number of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and layoutSetPrototypeLayoutERC = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param layoutSetPrototypeLayoutERC the layout set prototype layout erc
	 * @return the number of matching layouts that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_LSPLE(
		long groupId, boolean privateLayout,
		String layoutSetPrototypeLayoutERC) {

		return _collectionPersistenceFinderByG_P_LSPLE.filterCount(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout, layoutSetPrototypeLayoutERC},
			groupId);
	}

	private FilterCollectionPersistenceFinder<Layout, NoSuchLayoutException>
		_collectionPersistenceFinderByG_P_ST;

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param status the status
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByG_P_ST(
		long groupId, boolean privateLayout, int status, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByG_P_ST.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout, new int[] {status}}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByG_P_ST_First(
			long groupId, boolean privateLayout, int status,
			OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		Layout layout = fetchByG_P_ST_First(
			groupId, privateLayout, status, orderByComparator);

		if (layout != null) {
			return layout;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", privateLayout=");
		sb.append(privateLayout);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchLayoutException(sb.toString());
	}

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByG_P_ST_First(
		long groupId, boolean privateLayout, int status,
		OrderByComparator<Layout> orderByComparator) {

		return _collectionPersistenceFinderByG_P_ST.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout, new int[] {status}},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layouts that the user has permissions to view where groupId = &#63; and privateLayout = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param status the status
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_P_ST(
		long groupId, boolean privateLayout, int status, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return _collectionPersistenceFinderByG_P_ST.filterFind(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout, new int[] {status}}, start,
			end, orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param statuses the statuses
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_P_ST(
		long groupId, boolean privateLayout, int[] statuses, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return _collectionPersistenceFinderByG_P_ST.filterFind(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				groupId, privateLayout, ArrayUtil.sortedUnique(statuses)
			},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param statuses the statuses
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByG_P_ST(
		long groupId, boolean privateLayout, int[] statuses, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByG_P_ST.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				groupId, privateLayout, ArrayUtil.sortedUnique(statuses)
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the layouts where groupId = &#63; and privateLayout = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param status the status
	 */
	@Override
	public void removeByG_P_ST(
		long groupId, boolean privateLayout, int status) {

		_collectionPersistenceFinderByG_P_ST.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout, new int[] {status}});
	}

	/**
	 * Returns the number of layouts where groupId = &#63; and privateLayout = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param status the status
	 * @return the number of matching layouts
	 */
	@Override
	public int countByG_P_ST(long groupId, boolean privateLayout, int status) {
		return _collectionPersistenceFinderByG_P_ST.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout, new int[] {status}});
	}

	/**
	 * Returns the number of layouts where groupId = &#63; and privateLayout = &#63; and status = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param statuses the statuses
	 * @return the number of matching layouts
	 */
	@Override
	public int countByG_P_ST(
		long groupId, boolean privateLayout, int[] statuses) {

		return _collectionPersistenceFinderByG_P_ST.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				groupId, privateLayout, ArrayUtil.sortedUnique(statuses)
			});
	}

	/**
	 * Returns the number of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param status the status
	 * @return the number of matching layouts that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_ST(
		long groupId, boolean privateLayout, int status) {

		return _collectionPersistenceFinderByG_P_ST.filterCount(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout, new int[] {status}}, groupId);
	}

	/**
	 * Returns the number of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and status = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param statuses the statuses
	 * @return the number of matching layouts that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_ST(
		long groupId, boolean privateLayout, int[] statuses) {

		return _collectionPersistenceFinderByG_P_ST.filterCount(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				groupId, privateLayout, ArrayUtil.sortedUnique(statuses)
			},
			groupId);
	}

	private FinderPath _finderPathWithPaginationFindByG_P_P_H;
	private FinderPath _finderPathWithoutPaginationFindByG_P_P_H;
	private FinderPath _finderPathCountByG_P_P_H;
	private FinderPath _finderPathWithPaginationCountByG_P_P_H;

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and hidden = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param hidden the hidden
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByG_P_P_H(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean hidden, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_P_P_H;
					finderArgs = new Object[] {
						groupId, privateLayout, parentLayoutId, hidden
					};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_P_P_H;
				finderArgs = new Object[] {
					groupId, privateLayout, parentLayoutId, hidden, start, end,
					orderByComparator
				};
			}

			List<Layout> list = null;

			if (useFinderCache) {
				list = (List<Layout>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (Layout layout : list) {
						if ((groupId != layout.getGroupId()) ||
							(privateLayout != layout.isPrivateLayout()) ||
							(parentLayoutId != layout.getParentLayoutId()) ||
							(hidden != layout.isHidden())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						6 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(6);
				}

				sb.append(_SQL_SELECT_LAYOUT_WHERE);

				sb.append(_FINDER_COLUMN_G_P_P_H_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_P_H_PRIVATELAYOUT_2);

				sb.append(_FINDER_COLUMN_G_P_P_H_PARENTLAYOUTID_2);

				sb.append(_FINDER_COLUMN_G_P_P_H_HIDDEN_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ENTITY_ALIAS_PREFIX, orderByComparator);
				}
				else {
					sb.append(LayoutModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(privateLayout);

					queryPos.add(parentLayoutId);

					queryPos.add(hidden);

					list = (List<Layout>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						FinderCacheUtil.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
	}

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param hidden the hidden
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByG_P_P_H_First(
			long groupId, boolean privateLayout, long parentLayoutId,
			boolean hidden, OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		Layout layout = fetchByG_P_P_H_First(
			groupId, privateLayout, parentLayoutId, hidden, orderByComparator);

		if (layout != null) {
			return layout;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", privateLayout=");
		sb.append(privateLayout);

		sb.append(", parentLayoutId=");
		sb.append(parentLayoutId);

		sb.append(", hidden=");
		sb.append(hidden);

		sb.append("}");

		throw new NoSuchLayoutException(sb.toString());
	}

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param hidden the hidden
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByG_P_P_H_First(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean hidden, OrderByComparator<Layout> orderByComparator) {

		List<Layout> list = findByG_P_P_H(
			groupId, privateLayout, parentLayoutId, hidden, 0, 1,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns an ordered range of all the layouts that the user has permissions to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and hidden = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param hidden the hidden
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_P_P_H(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean hidden, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P_P_H(
				groupId, privateLayout, parentLayoutId, hidden, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_P_P_H(
					groupId, privateLayout, parentLayoutId, hidden,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, orderByComparator),
				groupId);
		}

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(7);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_P_H_GROUPID_2_SQL);

		sb.append(_FINDER_COLUMN_G_P_P_H_PRIVATELAYOUT_2_SQL);

		sb.append(_FINDER_COLUMN_G_P_P_H_PARENTLAYOUTID_2_SQL);

		sb.append(_FINDER_COLUMN_G_P_P_H_HIDDEN_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_2);
		}

		if (orderByComparator != null) {
			if (getDB().isSupportsInlineDistinct()) {
				appendOrderByComparator(
					sb, _ENTITY_ALIAS_PREFIX, orderByComparator, true);
			}
			else {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_TABLE, orderByComparator, true);
			}
		}
		else {
			if (getDB().isSupportsInlineDistinct()) {
				sb.append(LayoutModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(LayoutModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), Layout.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, LayoutImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, LayoutImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(privateLayout);

			queryPos.add(parentLayoutId);

			queryPos.add(hidden);

			return (List<Layout>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	/**
	 * Returns all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param hidden the hidden
	 * @return the matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_P_P_H(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean hidden) {

		return filterFindByG_P_P_H(
			groupId, privateLayout, parentLayoutIds, hidden, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63; and hidden = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param hidden the hidden
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_P_P_H(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean hidden, int start, int end) {

		return filterFindByG_P_P_H(
			groupId, privateLayout, parentLayoutIds, hidden, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63; and hidden = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param hidden the hidden
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_P_P_H(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean hidden, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P_P_H(
				groupId, privateLayout, parentLayoutIds, hidden, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_P_P_H(
					groupId, privateLayout, parentLayoutIds, hidden,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, orderByComparator),
				groupId);
		}

		if (parentLayoutIds == null) {
			parentLayoutIds = new long[0];
		}
		else if (parentLayoutIds.length > 1) {
			parentLayoutIds = ArrayUtil.sortedUnique(parentLayoutIds);
		}

		StringBundler sb = new StringBundler();

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_P_H_GROUPID_2_SQL);

		sb.append(_FINDER_COLUMN_G_P_P_H_PRIVATELAYOUT_2_SQL);

		if (parentLayoutIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_G_P_P_H_PARENTLAYOUTID_7_SQL);

			sb.append(StringUtil.merge(parentLayoutIds));

			sb.append(")");

			sb.append(")");

			sb.append(WHERE_AND);
		}

		sb.append(_FINDER_COLUMN_G_P_P_H_HIDDEN_2_SQL);

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		sb.append(" AND layout.system_ = [$FALSE$]");

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_2);
		}

		if (orderByComparator != null) {
			if (getDB().isSupportsInlineDistinct()) {
				appendOrderByComparator(
					sb, _ENTITY_ALIAS_PREFIX, orderByComparator, true);
			}
			else {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_TABLE, orderByComparator, true);
			}
		}
		else {
			if (getDB().isSupportsInlineDistinct()) {
				sb.append(LayoutModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(LayoutModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), Layout.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, LayoutImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, LayoutImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(privateLayout);

			queryPos.add(hidden);

			return (List<Layout>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	/**
	 * Returns all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63; and hidden = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param hidden the hidden
	 * @return the matching layouts
	 */
	@Override
	public List<Layout> findByG_P_P_H(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean hidden) {

		return findByG_P_P_H(
			groupId, privateLayout, parentLayoutIds, hidden, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63; and hidden = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param hidden the hidden
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	@Override
	public List<Layout> findByG_P_P_H(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean hidden, int start, int end) {

		return findByG_P_P_H(
			groupId, privateLayout, parentLayoutIds, hidden, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63; and hidden = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param hidden the hidden
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByG_P_P_H(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean hidden, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return findByG_P_P_H(
			groupId, privateLayout, parentLayoutIds, hidden, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and hidden = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param hidden the hidden
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByG_P_P_H(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean hidden, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		if (parentLayoutIds == null) {
			parentLayoutIds = new long[0];
		}
		else if (parentLayoutIds.length > 1) {
			parentLayoutIds = ArrayUtil.sortedUnique(parentLayoutIds);
		}

		if (parentLayoutIds.length == 1) {
			return findByG_P_P_H(
				groupId, privateLayout, parentLayoutIds[0], hidden, start, end,
				orderByComparator);
		}

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderArgs = new Object[] {
						groupId, privateLayout,
						StringUtil.merge(parentLayoutIds), hidden
					};
				}
			}
			else if (useFinderCache) {
				finderArgs = new Object[] {
					groupId, privateLayout, StringUtil.merge(parentLayoutIds),
					hidden, start, end, orderByComparator
				};
			}

			List<Layout> list = null;

			if (useFinderCache) {
				list = (List<Layout>)FinderCacheUtil.getResult(
					_finderPathWithPaginationFindByG_P_P_H, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (Layout layout : list) {
						if ((groupId != layout.getGroupId()) ||
							(privateLayout != layout.isPrivateLayout()) ||
							!ArrayUtil.contains(
								parentLayoutIds, layout.getParentLayoutId()) ||
							(hidden != layout.isHidden())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				try {
					if ((start == QueryUtil.ALL_POS) &&
						(end == QueryUtil.ALL_POS) &&
						(databaseInMaxParameters > 0) &&
						(parentLayoutIds.length > databaseInMaxParameters)) {

						list = new ArrayList<Layout>();

						long[][] parentLayoutIdsPages =
							(long[][])ArrayUtil.split(
								parentLayoutIds, databaseInMaxParameters);

						for (long[] parentLayoutIdsPage :
								parentLayoutIdsPages) {

							list.addAll(
								_findByG_P_P_H(
									groupId, privateLayout, parentLayoutIdsPage,
									hidden, start, end, orderByComparator));
						}

						Collections.sort(list, orderByComparator);

						list = Collections.unmodifiableList(list);
					}
					else {
						list = _findByG_P_P_H(
							groupId, privateLayout, parentLayoutIds, hidden,
							start, end, orderByComparator);
					}

					cacheResult(list);

					if (useFinderCache) {
						FinderCacheUtil.putResult(
							_finderPathWithPaginationFindByG_P_P_H, finderArgs,
							list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
			}

			return list;
		}
	}

	private List<Layout> _findByG_P_P_H(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean hidden, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		List<Layout> list = null;

		StringBundler sb = new StringBundler();

		sb.append(_SQL_SELECT_LAYOUT_WHERE);

		sb.append(_FINDER_COLUMN_G_P_P_H_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_P_H_PRIVATELAYOUT_2);

		if (parentLayoutIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_G_P_P_H_PARENTLAYOUTID_7);

			sb.append(StringUtil.merge(parentLayoutIds));

			sb.append(")");

			sb.append(")");

			sb.append(WHERE_AND);
		}

		sb.append(_FINDER_COLUMN_G_P_P_H_HIDDEN_2);

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		sb.append(" AND layout.system = [$FALSE$]");

		if (orderByComparator != null) {
			appendOrderByComparator(
				sb, _ENTITY_ALIAS_PREFIX, orderByComparator);
		}
		else {
			sb.append(LayoutModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Session session = null;

		try {
			session = openSession();

			Query query = session.createQuery(sql);

			QueryPos queryPos = QueryPos.getInstance(query);

			queryPos.add(groupId);

			queryPos.add(privateLayout);

			queryPos.add(hidden);

			list = (List<Layout>)QueryUtil.list(
				query, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return list;
	}

	/**
	 * Removes all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and hidden = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param hidden the hidden
	 */
	@Override
	public void removeByG_P_P_H(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean hidden) {

		for (Layout layout :
				findByG_P_P_H(
					groupId, privateLayout, parentLayoutId, hidden,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(layout);
		}
	}

	/**
	 * Returns the number of layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param hidden the hidden
	 * @return the number of matching layouts
	 */
	@Override
	public int countByG_P_P_H(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean hidden) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			FinderPath finderPath = _finderPathCountByG_P_P_H;

			Object[] finderArgs = new Object[] {
				groupId, privateLayout, parentLayoutId, hidden
			};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_COUNT_LAYOUT_WHERE);

				sb.append(_FINDER_COLUMN_G_P_P_H_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_P_H_PRIVATELAYOUT_2);

				sb.append(_FINDER_COLUMN_G_P_P_H_PARENTLAYOUTID_2);

				sb.append(_FINDER_COLUMN_G_P_P_H_HIDDEN_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(privateLayout);

					queryPos.add(parentLayoutId);

					queryPos.add(hidden);

					count = (Long)query.uniqueResult();

					FinderCacheUtil.putResult(finderPath, finderArgs, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	/**
	 * Returns the number of layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param hidden the hidden
	 * @return the number of matching layouts
	 */
	@Override
	public int countByG_P_P_H(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean hidden) {

		if (parentLayoutIds == null) {
			parentLayoutIds = new long[0];
		}
		else if (parentLayoutIds.length > 1) {
			parentLayoutIds = ArrayUtil.sortedUnique(parentLayoutIds);
		}

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			Object[] finderArgs = new Object[] {
				groupId, privateLayout, StringUtil.merge(parentLayoutIds),
				hidden
			};

			Long count = (Long)FinderCacheUtil.getResult(
				_finderPathWithPaginationCountByG_P_P_H, finderArgs, this);

			if (count == null) {
				try {
					if ((databaseInMaxParameters > 0) &&
						(parentLayoutIds.length > databaseInMaxParameters)) {

						count = Long.valueOf(0);

						long[][] parentLayoutIdsPages =
							(long[][])ArrayUtil.split(
								parentLayoutIds, databaseInMaxParameters);

						for (long[] parentLayoutIdsPage :
								parentLayoutIdsPages) {

							count += Long.valueOf(
								_countByG_P_P_H(
									groupId, privateLayout, parentLayoutIdsPage,
									hidden));
						}
					}
					else {
						count = Long.valueOf(
							_countByG_P_P_H(
								groupId, privateLayout, parentLayoutIds,
								hidden));
					}

					FinderCacheUtil.putResult(
						_finderPathWithPaginationCountByG_P_P_H, finderArgs,
						count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
			}

			return count.intValue();
		}
	}

	private int _countByG_P_P_H(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean hidden) {

		Long count = null;

		StringBundler sb = new StringBundler();

		sb.append(_SQL_COUNT_LAYOUT_WHERE);

		sb.append(_FINDER_COLUMN_G_P_P_H_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_P_H_PRIVATELAYOUT_2);

		if (parentLayoutIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_G_P_P_H_PARENTLAYOUTID_7);

			sb.append(StringUtil.merge(parentLayoutIds));

			sb.append(")");

			sb.append(")");

			sb.append(WHERE_AND);
		}

		sb.append(_FINDER_COLUMN_G_P_P_H_HIDDEN_2);

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		sb.append(" AND layout.system = [$FALSE$]");

		String sql = sb.toString();

		Session session = null;

		try {
			session = openSession();

			Query query = session.createQuery(sql);

			QueryPos queryPos = QueryPos.getInstance(query);

			queryPos.add(groupId);

			queryPos.add(privateLayout);

			queryPos.add(hidden);

			count = (Long)query.uniqueResult();
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return count.intValue();
	}

	/**
	 * Returns the number of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param hidden the hidden
	 * @return the number of matching layouts that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_P_H(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean hidden) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_P_P_H(
				groupId, privateLayout, parentLayoutId, hidden);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<Layout> layouts = findByG_P_P_H(
				groupId, privateLayout, parentLayoutId, hidden);

			layouts = InlineSQLHelperUtil.filter(layouts, groupId);

			return layouts.size();
		}

		StringBundler sb = new StringBundler(5);

		sb.append(_FILTER_SQL_COUNT_LAYOUT_WHERE);

		sb.append(_FINDER_COLUMN_G_P_P_H_GROUPID_2_SQL);

		sb.append(_FINDER_COLUMN_G_P_P_H_PRIVATELAYOUT_2_SQL);

		sb.append(_FINDER_COLUMN_G_P_P_H_PARENTLAYOUTID_2_SQL);

		sb.append(_FINDER_COLUMN_G_P_P_H_HIDDEN_2_SQL);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), Layout.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(privateLayout);

			queryPos.add(parentLayoutId);

			queryPos.add(hidden);

			Long count = (Long)sqlQuery.uniqueResult();

			return count.intValue();
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	/**
	 * Returns the number of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63; and hidden = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param hidden the hidden
	 * @return the number of matching layouts that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_P_H(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean hidden) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_P_P_H(
				groupId, privateLayout, parentLayoutIds, hidden);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<Layout> layouts = InlineSQLHelperUtil.filter(
				findByG_P_P_H(groupId, privateLayout, parentLayoutIds, hidden),
				groupId);

			return layouts.size();
		}

		if (parentLayoutIds == null) {
			parentLayoutIds = new long[0];
		}
		else if (parentLayoutIds.length > 1) {
			parentLayoutIds = ArrayUtil.sortedUnique(parentLayoutIds);
		}

		StringBundler sb = new StringBundler();

		sb.append(_FILTER_SQL_COUNT_LAYOUT_WHERE);

		sb.append(_FINDER_COLUMN_G_P_P_H_GROUPID_2_SQL);

		sb.append(_FINDER_COLUMN_G_P_P_H_PRIVATELAYOUT_2_SQL);

		if (parentLayoutIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_G_P_P_H_PARENTLAYOUTID_7_SQL);

			sb.append(StringUtil.merge(parentLayoutIds));

			sb.append(")");

			sb.append(")");

			sb.append(WHERE_AND);
		}

		sb.append(_FINDER_COLUMN_G_P_P_H_HIDDEN_2_SQL);

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		sb.append(" AND layout.system_ = [$FALSE$]");

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), Layout.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(privateLayout);

			queryPos.add(hidden);

			Long count = (Long)sqlQuery.uniqueResult();

			return count.intValue();
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	private static final String _FINDER_COLUMN_G_P_P_H_GROUPID_2 =
		"layout.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_P_H_GROUPID_2_SQL =
		"layout.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_P_H_PRIVATELAYOUT_2 =
		"layout.privateLayout = ? AND ";

	private static final String _FINDER_COLUMN_G_P_P_H_PRIVATELAYOUT_2_SQL =
		"layout.privateLayout = ? AND ";

	private static final String _FINDER_COLUMN_G_P_P_H_PARENTLAYOUTID_2 =
		"layout.parentLayoutId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_P_H_PARENTLAYOUTID_7 =
		"layout.parentLayoutId IN (";

	private static final String _FINDER_COLUMN_G_P_P_H_PARENTLAYOUTID_2_SQL =
		"layout.parentLayoutId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_P_H_PARENTLAYOUTID_7_SQL =
		"layout.parentLayoutId IN (";

	private static final String _FINDER_COLUMN_G_P_P_H_HIDDEN_2 =
		"layout.hidden = ? AND layout.system = [$FALSE$]";

	private static final String _FINDER_COLUMN_G_P_P_H_HIDDEN_2_SQL =
		"layout.hidden_ = ? AND layout.system_ = [$FALSE$]";

	private FinderPath _finderPathWithPaginationFindByG_P_P_S;
	private FinderPath _finderPathWithoutPaginationFindByG_P_P_S;
	private FinderPath _finderPathCountByG_P_P_S;
	private FinderPath _finderPathWithPaginationCountByG_P_P_S;

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param system the system
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByG_P_P_S(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean system, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_P_P_S;
					finderArgs = new Object[] {
						groupId, privateLayout, parentLayoutId, system
					};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_P_P_S;
				finderArgs = new Object[] {
					groupId, privateLayout, parentLayoutId, system, start, end,
					orderByComparator
				};
			}

			List<Layout> list = null;

			if (useFinderCache) {
				list = (List<Layout>)FinderCacheUtil.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (Layout layout : list) {
						if ((groupId != layout.getGroupId()) ||
							(privateLayout != layout.isPrivateLayout()) ||
							(parentLayoutId != layout.getParentLayoutId()) ||
							(system != layout.isSystem())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						6 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(6);
				}

				sb.append(_SQL_SELECT_LAYOUT_WHERE);

				sb.append(_FINDER_COLUMN_G_P_P_S_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_P_S_PRIVATELAYOUT_2);

				sb.append(_FINDER_COLUMN_G_P_P_S_PARENTLAYOUTID_2);

				sb.append(_FINDER_COLUMN_G_P_P_S_SYSTEM_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ENTITY_ALIAS_PREFIX, orderByComparator);
				}
				else {
					sb.append(LayoutModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(privateLayout);

					queryPos.add(parentLayoutId);

					queryPos.add(system);

					list = (List<Layout>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						FinderCacheUtil.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
	}

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and system = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByG_P_P_S_First(
			long groupId, boolean privateLayout, long parentLayoutId,
			boolean system, OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		Layout layout = fetchByG_P_P_S_First(
			groupId, privateLayout, parentLayoutId, system, orderByComparator);

		if (layout != null) {
			return layout;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", privateLayout=");
		sb.append(privateLayout);

		sb.append(", parentLayoutId=");
		sb.append(parentLayoutId);

		sb.append(", system=");
		sb.append(system);

		sb.append("}");

		throw new NoSuchLayoutException(sb.toString());
	}

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and system = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param system the system
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByG_P_P_S_First(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean system, OrderByComparator<Layout> orderByComparator) {

		List<Layout> list = findByG_P_P_S(
			groupId, privateLayout, parentLayoutId, system, 0, 1,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns an ordered range of all the layouts that the user has permissions to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param system the system
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_P_P_S(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean system, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P_P_S(
				groupId, privateLayout, parentLayoutId, system, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_P_P_S(
					groupId, privateLayout, parentLayoutId, system,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, orderByComparator),
				groupId);
		}

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(7);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_P_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_P_S_PRIVATELAYOUT_2);

		sb.append(_FINDER_COLUMN_G_P_P_S_PARENTLAYOUTID_2);

		sb.append(_FINDER_COLUMN_G_P_P_S_SYSTEM_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_2);
		}

		if (orderByComparator != null) {
			if (getDB().isSupportsInlineDistinct()) {
				appendOrderByComparator(
					sb, _ENTITY_ALIAS_PREFIX, orderByComparator, true);
			}
			else {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_TABLE, orderByComparator, true);
			}
		}
		else {
			if (getDB().isSupportsInlineDistinct()) {
				sb.append(LayoutModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(LayoutModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), Layout.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, LayoutImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, LayoutImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(privateLayout);

			queryPos.add(parentLayoutId);

			queryPos.add(system);

			return (List<Layout>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	/**
	 * Returns all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63; and system = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param system the system
	 * @return the matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_P_P_S(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean system) {

		return filterFindByG_P_P_S(
			groupId, privateLayout, parentLayoutIds, system, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param system the system
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_P_P_S(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean system, int start, int end) {

		return filterFindByG_P_P_S(
			groupId, privateLayout, parentLayoutIds, system, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param system the system
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_P_P_S(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean system, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_P_P_S(
				groupId, privateLayout, parentLayoutIds, system, start, end,
				orderByComparator);
		}

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			isPermissionsInMemoryFilterEnabled()) {

			return InlineSQLHelperUtil.filter(
				findByG_P_P_S(
					groupId, privateLayout, parentLayoutIds, system,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, orderByComparator),
				groupId);
		}

		if (parentLayoutIds == null) {
			parentLayoutIds = new long[0];
		}
		else if (parentLayoutIds.length > 1) {
			parentLayoutIds = ArrayUtil.sortedUnique(parentLayoutIds);
		}

		StringBundler sb = new StringBundler();

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_WHERE);
		}
		else {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_P_P_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_P_S_PRIVATELAYOUT_2);

		if (parentLayoutIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_G_P_P_S_PARENTLAYOUTID_7);

			sb.append(StringUtil.merge(parentLayoutIds));

			sb.append(")");

			sb.append(")");

			sb.append(WHERE_AND);
		}

		sb.append(_FINDER_COLUMN_G_P_P_S_SYSTEM_2_SQL);

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_2);
		}

		if (orderByComparator != null) {
			if (getDB().isSupportsInlineDistinct()) {
				appendOrderByComparator(
					sb, _ENTITY_ALIAS_PREFIX, orderByComparator, true);
			}
			else {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_TABLE, orderByComparator, true);
			}
		}
		else {
			if (getDB().isSupportsInlineDistinct()) {
				sb.append(LayoutModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(LayoutModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), Layout.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, LayoutImpl.class);
			}
			else {
				sqlQuery.addEntity(_FILTER_ENTITY_TABLE, LayoutImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(privateLayout);

			queryPos.add(system);

			return (List<Layout>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	/**
	 * Returns all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param system the system
	 * @return the matching layouts
	 */
	@Override
	public List<Layout> findByG_P_P_S(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean system) {

		return findByG_P_P_S(
			groupId, privateLayout, parentLayoutIds, system, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param system the system
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	@Override
	public List<Layout> findByG_P_P_S(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean system, int start, int end) {

		return findByG_P_P_S(
			groupId, privateLayout, parentLayoutIds, system, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63; and system = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param system the system
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByG_P_P_S(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean system, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		return findByG_P_P_S(
			groupId, privateLayout, parentLayoutIds, system, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and system = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param system the system
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByG_P_P_S(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean system, int start, int end,
		OrderByComparator<Layout> orderByComparator, boolean useFinderCache) {

		if (parentLayoutIds == null) {
			parentLayoutIds = new long[0];
		}
		else if (parentLayoutIds.length > 1) {
			parentLayoutIds = ArrayUtil.sortedUnique(parentLayoutIds);
		}

		if (parentLayoutIds.length == 1) {
			return findByG_P_P_S(
				groupId, privateLayout, parentLayoutIds[0], system, start, end,
				orderByComparator);
		}

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderArgs = new Object[] {
						groupId, privateLayout,
						StringUtil.merge(parentLayoutIds), system
					};
				}
			}
			else if (useFinderCache) {
				finderArgs = new Object[] {
					groupId, privateLayout, StringUtil.merge(parentLayoutIds),
					system, start, end, orderByComparator
				};
			}

			List<Layout> list = null;

			if (useFinderCache) {
				list = (List<Layout>)FinderCacheUtil.getResult(
					_finderPathWithPaginationFindByG_P_P_S, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (Layout layout : list) {
						if ((groupId != layout.getGroupId()) ||
							(privateLayout != layout.isPrivateLayout()) ||
							!ArrayUtil.contains(
								parentLayoutIds, layout.getParentLayoutId()) ||
							(system != layout.isSystem())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				try {
					if ((start == QueryUtil.ALL_POS) &&
						(end == QueryUtil.ALL_POS) &&
						(databaseInMaxParameters > 0) &&
						(parentLayoutIds.length > databaseInMaxParameters)) {

						list = new ArrayList<Layout>();

						long[][] parentLayoutIdsPages =
							(long[][])ArrayUtil.split(
								parentLayoutIds, databaseInMaxParameters);

						for (long[] parentLayoutIdsPage :
								parentLayoutIdsPages) {

							list.addAll(
								_findByG_P_P_S(
									groupId, privateLayout, parentLayoutIdsPage,
									system, start, end, orderByComparator));
						}

						Collections.sort(list, orderByComparator);

						list = Collections.unmodifiableList(list);
					}
					else {
						list = _findByG_P_P_S(
							groupId, privateLayout, parentLayoutIds, system,
							start, end, orderByComparator);
					}

					cacheResult(list);

					if (useFinderCache) {
						FinderCacheUtil.putResult(
							_finderPathWithPaginationFindByG_P_P_S, finderArgs,
							list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
			}

			return list;
		}
	}

	private List<Layout> _findByG_P_P_S(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean system, int start, int end,
		OrderByComparator<Layout> orderByComparator) {

		List<Layout> list = null;

		StringBundler sb = new StringBundler();

		sb.append(_SQL_SELECT_LAYOUT_WHERE);

		sb.append(_FINDER_COLUMN_G_P_P_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_P_S_PRIVATELAYOUT_2);

		if (parentLayoutIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_G_P_P_S_PARENTLAYOUTID_7);

			sb.append(StringUtil.merge(parentLayoutIds));

			sb.append(")");

			sb.append(")");

			sb.append(WHERE_AND);
		}

		sb.append(_FINDER_COLUMN_G_P_P_S_SYSTEM_2);

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		if (orderByComparator != null) {
			appendOrderByComparator(
				sb, _ENTITY_ALIAS_PREFIX, orderByComparator);
		}
		else {
			sb.append(LayoutModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Session session = null;

		try {
			session = openSession();

			Query query = session.createQuery(sql);

			QueryPos queryPos = QueryPos.getInstance(query);

			queryPos.add(groupId);

			queryPos.add(privateLayout);

			queryPos.add(system);

			list = (List<Layout>)QueryUtil.list(
				query, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return list;
	}

	/**
	 * Removes all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and system = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param system the system
	 */
	@Override
	public void removeByG_P_P_S(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean system) {

		for (Layout layout :
				findByG_P_P_S(
					groupId, privateLayout, parentLayoutId, system,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(layout);
		}
	}

	/**
	 * Returns the number of layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and system = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param system the system
	 * @return the number of matching layouts
	 */
	@Override
	public int countByG_P_P_S(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean system) {

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			FinderPath finderPath = _finderPathCountByG_P_P_S;

			Object[] finderArgs = new Object[] {
				groupId, privateLayout, parentLayoutId, system
			};

			Long count = (Long)FinderCacheUtil.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_COUNT_LAYOUT_WHERE);

				sb.append(_FINDER_COLUMN_G_P_P_S_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_P_P_S_PRIVATELAYOUT_2);

				sb.append(_FINDER_COLUMN_G_P_P_S_PARENTLAYOUTID_2);

				sb.append(_FINDER_COLUMN_G_P_P_S_SYSTEM_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(privateLayout);

					queryPos.add(parentLayoutId);

					queryPos.add(system);

					count = (Long)query.uniqueResult();

					FinderCacheUtil.putResult(finderPath, finderArgs, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	/**
	 * Returns the number of layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63; and system = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param system the system
	 * @return the number of matching layouts
	 */
	@Override
	public int countByG_P_P_S(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean system) {

		if (parentLayoutIds == null) {
			parentLayoutIds = new long[0];
		}
		else if (parentLayoutIds.length > 1) {
			parentLayoutIds = ArrayUtil.sortedUnique(parentLayoutIds);
		}

		try (SafeCloseable safeCloseable =
				CTPersistenceHelperUtil.setCTCollectionIdWithSafeCloseable(
					Layout.class)) {

			Object[] finderArgs = new Object[] {
				groupId, privateLayout, StringUtil.merge(parentLayoutIds),
				system
			};

			Long count = (Long)FinderCacheUtil.getResult(
				_finderPathWithPaginationCountByG_P_P_S, finderArgs, this);

			if (count == null) {
				try {
					if ((databaseInMaxParameters > 0) &&
						(parentLayoutIds.length > databaseInMaxParameters)) {

						count = Long.valueOf(0);

						long[][] parentLayoutIdsPages =
							(long[][])ArrayUtil.split(
								parentLayoutIds, databaseInMaxParameters);

						for (long[] parentLayoutIdsPage :
								parentLayoutIdsPages) {

							count += Long.valueOf(
								_countByG_P_P_S(
									groupId, privateLayout, parentLayoutIdsPage,
									system));
						}
					}
					else {
						count = Long.valueOf(
							_countByG_P_P_S(
								groupId, privateLayout, parentLayoutIds,
								system));
					}

					FinderCacheUtil.putResult(
						_finderPathWithPaginationCountByG_P_P_S, finderArgs,
						count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
			}

			return count.intValue();
		}
	}

	private int _countByG_P_P_S(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean system) {

		Long count = null;

		StringBundler sb = new StringBundler();

		sb.append(_SQL_COUNT_LAYOUT_WHERE);

		sb.append(_FINDER_COLUMN_G_P_P_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_P_S_PRIVATELAYOUT_2);

		if (parentLayoutIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_G_P_P_S_PARENTLAYOUTID_7);

			sb.append(StringUtil.merge(parentLayoutIds));

			sb.append(")");

			sb.append(")");

			sb.append(WHERE_AND);
		}

		sb.append(_FINDER_COLUMN_G_P_P_S_SYSTEM_2);

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		String sql = sb.toString();

		Session session = null;

		try {
			session = openSession();

			Query query = session.createQuery(sql);

			QueryPos queryPos = QueryPos.getInstance(query);

			queryPos.add(groupId);

			queryPos.add(privateLayout);

			queryPos.add(system);

			count = (Long)query.uniqueResult();
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return count.intValue();
	}

	/**
	 * Returns the number of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and system = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param system the system
	 * @return the number of matching layouts that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_P_S(
		long groupId, boolean privateLayout, long parentLayoutId,
		boolean system) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_P_P_S(
				groupId, privateLayout, parentLayoutId, system);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<Layout> layouts = findByG_P_P_S(
				groupId, privateLayout, parentLayoutId, system);

			layouts = InlineSQLHelperUtil.filter(layouts, groupId);

			return layouts.size();
		}

		StringBundler sb = new StringBundler(5);

		sb.append(_FILTER_SQL_COUNT_LAYOUT_WHERE);

		sb.append(_FINDER_COLUMN_G_P_P_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_P_S_PRIVATELAYOUT_2);

		sb.append(_FINDER_COLUMN_G_P_P_S_PARENTLAYOUTID_2);

		sb.append(_FINDER_COLUMN_G_P_P_S_SYSTEM_2_SQL);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), Layout.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(privateLayout);

			queryPos.add(parentLayoutId);

			queryPos.add(system);

			Long count = (Long)sqlQuery.uniqueResult();

			return count.intValue();
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	/**
	 * Returns the number of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = any &#63; and system = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutIds the parent layout IDs
	 * @param system the system
	 * @return the number of matching layouts that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_P_S(
		long groupId, boolean privateLayout, long[] parentLayoutIds,
		boolean system) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_P_P_S(
				groupId, privateLayout, parentLayoutIds, system);
		}

		if (isPermissionsInMemoryFilterEnabled()) {
			List<Layout> layouts = InlineSQLHelperUtil.filter(
				findByG_P_P_S(groupId, privateLayout, parentLayoutIds, system),
				groupId);

			return layouts.size();
		}

		if (parentLayoutIds == null) {
			parentLayoutIds = new long[0];
		}
		else if (parentLayoutIds.length > 1) {
			parentLayoutIds = ArrayUtil.sortedUnique(parentLayoutIds);
		}

		StringBundler sb = new StringBundler();

		sb.append(_FILTER_SQL_COUNT_LAYOUT_WHERE);

		sb.append(_FINDER_COLUMN_G_P_P_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_P_P_S_PRIVATELAYOUT_2);

		if (parentLayoutIds.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_G_P_P_S_PARENTLAYOUTID_7);

			sb.append(StringUtil.merge(parentLayoutIds));

			sb.append(")");

			sb.append(")");

			sb.append(WHERE_AND);
		}

		sb.append(_FINDER_COLUMN_G_P_P_S_SYSTEM_2_SQL);

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), Layout.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(privateLayout);

			queryPos.add(system);

			Long count = (Long)sqlQuery.uniqueResult();

			return count.intValue();
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	private static final String _FINDER_COLUMN_G_P_P_S_GROUPID_2 =
		"layout.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_P_S_PRIVATELAYOUT_2 =
		"layout.privateLayout = ? AND ";

	private static final String _FINDER_COLUMN_G_P_P_S_PARENTLAYOUTID_2 =
		"layout.parentLayoutId = ? AND ";

	private static final String _FINDER_COLUMN_G_P_P_S_PARENTLAYOUTID_7 =
		"layout.parentLayoutId IN (";

	private static final String _FINDER_COLUMN_G_P_P_S_SYSTEM_2 =
		"layout.system = ?";

	private static final String _FINDER_COLUMN_G_P_P_S_SYSTEM_2_SQL =
		"layout.system_ = ?";

	private FilterCollectionPersistenceFinder<Layout, NoSuchLayoutException>
		_collectionPersistenceFinderByG_P_P_LteP;

	/**
	 * Returns all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and priority &le; &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param priority the priority
	 * @return the matching layouts
	 */
	@Override
	public List<Layout> findByG_P_P_LteP(
		long groupId, boolean privateLayout, long parentLayoutId,
		int priority) {

		return findByG_P_P_LteP(
			groupId, privateLayout, parentLayoutId, priority, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and priority &le; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param priority the priority
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts
	 */
	@Override
	public List<Layout> findByG_P_P_LteP(
		long groupId, boolean privateLayout, long parentLayoutId, int priority,
		int start, int end) {

		return findByG_P_P_LteP(
			groupId, privateLayout, parentLayoutId, priority, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and priority &le; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param priority the priority
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByG_P_P_LteP(
		long groupId, boolean privateLayout, long parentLayoutId, int priority,
		int start, int end, OrderByComparator<Layout> orderByComparator) {

		return findByG_P_P_LteP(
			groupId, privateLayout, parentLayoutId, priority, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and priority &le; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param priority the priority
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layouts
	 */
	@Override
	public List<Layout> findByG_P_P_LteP(
		long groupId, boolean privateLayout, long parentLayoutId, int priority,
		int start, int end, OrderByComparator<Layout> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_P_P_LteP.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout, parentLayoutId, priority},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and priority &le; &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param priority the priority
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByG_P_P_LteP_First(
			long groupId, boolean privateLayout, long parentLayoutId,
			int priority, OrderByComparator<Layout> orderByComparator)
		throws NoSuchLayoutException {

		return _collectionPersistenceFinderByG_P_P_LteP.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout, parentLayoutId, priority},
			orderByComparator);
	}

	/**
	 * Returns the first layout in the ordered set where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and priority &le; &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param priority the priority
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByG_P_P_LteP_First(
		long groupId, boolean privateLayout, long parentLayoutId, int priority,
		OrderByComparator<Layout> orderByComparator) {

		return _collectionPersistenceFinderByG_P_P_LteP.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout, parentLayoutId, priority},
			orderByComparator);
	}

	/**
	 * Returns all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and priority &le; &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param priority the priority
	 * @return the matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_P_P_LteP(
		long groupId, boolean privateLayout, long parentLayoutId,
		int priority) {

		return filterFindByG_P_P_LteP(
			groupId, privateLayout, parentLayoutId, priority, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and priority &le; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param priority the priority
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @return the range of matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_P_P_LteP(
		long groupId, boolean privateLayout, long parentLayoutId, int priority,
		int start, int end) {

		return filterFindByG_P_P_LteP(
			groupId, privateLayout, parentLayoutId, priority, start, end, null);
	}

	/**
	 * Returns an ordered range of all the layouts that the user has permissions to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and priority &le; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param priority the priority
	 * @param start the lower bound of the range of layouts
	 * @param end the upper bound of the range of layouts (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layouts that the user has permission to view
	 */
	@Override
	public List<Layout> filterFindByG_P_P_LteP(
		long groupId, boolean privateLayout, long parentLayoutId, int priority,
		int start, int end, OrderByComparator<Layout> orderByComparator) {

		return _collectionPersistenceFinderByG_P_P_LteP.filterFind(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout, parentLayoutId, priority},
			start, end, orderByComparator, groupId);
	}

	/**
	 * Removes all the layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and priority &le; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param priority the priority
	 */
	@Override
	public void removeByG_P_P_LteP(
		long groupId, boolean privateLayout, long parentLayoutId,
		int priority) {

		_collectionPersistenceFinderByG_P_P_LteP.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout, parentLayoutId, priority});
	}

	/**
	 * Returns the number of layouts where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and priority &le; &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param priority the priority
	 * @return the number of matching layouts
	 */
	@Override
	public int countByG_P_P_LteP(
		long groupId, boolean privateLayout, long parentLayoutId,
		int priority) {

		return _collectionPersistenceFinderByG_P_P_LteP.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout, parentLayoutId, priority});
	}

	/**
	 * Returns the number of layouts that the user has permission to view where groupId = &#63; and privateLayout = &#63; and parentLayoutId = &#63; and priority &le; &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param parentLayoutId the parent layout ID
	 * @param priority the priority
	 * @return the number of matching layouts that the user has permission to view
	 */
	@Override
	public int filterCountByG_P_P_LteP(
		long groupId, boolean privateLayout, long parentLayoutId,
		int priority) {

		return _collectionPersistenceFinderByG_P_P_LteP.filterCount(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, privateLayout, parentLayoutId, priority},
			groupId);
	}

	private UniquePersistenceFinder<Layout, NoSuchLayoutException>
		_uniquePersistenceFinderByERC_G;

	/**
	 * Returns the layout where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchLayoutException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching layout
	 * @throws NoSuchLayoutException if a matching layout could not be found
	 */
	@Override
	public Layout findByERC_G(String externalReferenceCode, long groupId)
		throws NoSuchLayoutException {

		return _uniquePersistenceFinderByERC_G.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {externalReferenceCode, groupId});
	}

	/**
	 * Returns the layout where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout, or <code>null</code> if a matching layout could not be found
	 */
	@Override
	public Layout fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_G.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {externalReferenceCode, groupId}, useFinderCache);
	}

	/**
	 * Removes the layout where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the layout that was removed
	 */
	@Override
	public Layout removeByERC_G(String externalReferenceCode, long groupId)
		throws NoSuchLayoutException {

		Layout layout = findByERC_G(externalReferenceCode, groupId);

		return remove(layout);
	}

	/**
	 * Returns the number of layouts where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching layouts
	 */
	@Override
	public int countByERC_G(String externalReferenceCode, long groupId) {
		return _uniquePersistenceFinderByERC_G.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {externalReferenceCode, groupId});
	}

	public LayoutPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("type", "type_");
		dbColumnNames.put("hidden", "hidden_");
		dbColumnNames.put("system", "system_");
		dbColumnNames.put("masterLayoutPageTemplateEntryERC", "masterLPTEERC");
		dbColumnNames.put(
			"portletLayoutPageTemplateEntryERC", "portletLPTEERC");
		dbColumnNames.put(
			"portletLayoutPageTemplateEntryScopeERC", "portletLPTESERC");
		dbColumnNames.put(
			"portletLayoutPageTemplateEntryLinkEnabled", "portletLPTELE");

		setDBColumnNames(dbColumnNames);

		setModelClass(Layout.class);

		setModelImplClass(LayoutImpl.class);
		setModelPKClass(long.class);

		setTable(LayoutTable.INSTANCE);
	}

	/**
	 * Creates a new layout with the primary key. Does not add the layout to the database.
	 *
	 * @param plid the primary key for the new layout
	 * @return the new layout
	 */
	@Override
	public Layout create(long plid) {
		Layout layout = new LayoutImpl();

		layout.setNew(true);
		layout.setPrimaryKey(plid);

		String uuid = PortalUUIDUtil.generate();

		layout.setUuid(uuid);

		layout.setCompanyId(CompanyThreadLocal.getCompanyId());

		return layout;
	}

	/**
	 * Removes the layout with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param plid the primary key of the layout
	 * @return the layout that was removed
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	@Override
	public Layout remove(long plid) throws NoSuchLayoutException {
		return remove((Serializable)plid);
	}

	@Override
	protected Layout removeImpl(Layout layout) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(layout)) {
				layout = (Layout)session.get(
					LayoutImpl.class, layout.getPrimaryKeyObj());
			}

			if ((layout != null) && CTPersistenceHelperUtil.isRemove(layout)) {
				session.delete(layout);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (layout != null) {
			clearCache(layout);
		}

		return layout;
	}

	@Override
	public Layout updateImpl(Layout layout) {
		boolean isNew = layout.isNew();

		if (!(layout instanceof LayoutModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(layout.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(layout);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in layout proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom Layout implementation " +
					layout.getClass());
		}

		LayoutModelImpl layoutModelImpl = (LayoutModelImpl)layout;

		if (Validator.isNull(layout.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			layout.setUuid(uuid);
		}

		if (Validator.isNull(layout.getExternalReferenceCode())) {
			layout.setExternalReferenceCode(layout.getUuid());
		}
		else {
			if (!Objects.equals(
					layoutModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					layout.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = layout.getCompanyId();

					long groupId = layout.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = layout.getPrimaryKey();
					}

					try {
						layout.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								Layout.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								layout.getExternalReferenceCode(), null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			Layout ercLayout = fetchByERC_G(
				layout.getExternalReferenceCode(), layout.getGroupId());

			if (isNew) {
				if (ercLayout != null) {
					throw new DuplicateLayoutExternalReferenceCodeException(
						"Duplicate layout with external reference code " +
							layout.getExternalReferenceCode() + " and group " +
								layout.getGroupId());
				}
			}
			else {
				if ((ercLayout != null) &&
					(layout.getPlid() != ercLayout.getPlid())) {

					throw new DuplicateLayoutExternalReferenceCodeException(
						"Duplicate layout with external reference code " +
							layout.getExternalReferenceCode() + " and group " +
								layout.getGroupId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (layout.getCreateDate() == null)) {
			if (serviceContext == null) {
				layout.setCreateDate(date);
			}
			else {
				layout.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!layoutModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				layout.setModifiedDate(date);
			}
			else {
				layout.setModifiedDate(serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (CTPersistenceHelperUtil.isInsert(layout)) {
				if (!isNew) {
					session.evict(LayoutImpl.class, layout.getPrimaryKeyObj());
				}

				session.save(layout);
			}
			else {
				layout = (Layout)session.merge(layout);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(layout, false);

		if (isNew) {
			layout.setNew(false);
		}

		layout.resetOriginalValues();

		return layout;
	}

	/**
	 * Returns the layout with the primary key or throws a <code>NoSuchLayoutException</code> if it could not be found.
	 *
	 * @param plid the primary key of the layout
	 * @return the layout
	 * @throws NoSuchLayoutException if a layout with the primary key could not be found
	 */
	@Override
	public Layout findByPrimaryKey(long plid) throws NoSuchLayoutException {
		return findByPrimaryKey((Serializable)plid);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return CTPersistenceHelperUtil.getCTPersistenceHelper();
	}

	/**
	 * Returns the layout with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param plid the primary key of the layout
	 * @return the layout, or <code>null</code> if a layout with the primary key could not be found
	 */
	@Override
	public Layout fetchByPrimaryKey(long plid) {
		return fetchByPrimaryKey((Serializable)plid);
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
		return "plid";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_LAYOUT;
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
		return LayoutModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "Layout";
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
		ctStrictColumnNames.add("externalReferenceCode");
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("parentPlid");
		ctMergeColumnNames.add("privateLayout");
		ctMergeColumnNames.add("layoutId");
		ctMergeColumnNames.add("parentLayoutId");
		ctStrictColumnNames.add("classNameId");
		ctStrictColumnNames.add("classPK");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("title");
		ctMergeColumnNames.add("description");
		ctMergeColumnNames.add("keywords");
		ctMergeColumnNames.add("robots");
		ctMergeColumnNames.add("type_");
		ctMergeColumnNames.add("typeSettings");
		ctMergeColumnNames.add("hidden_");
		ctMergeColumnNames.add("system_");
		ctMergeColumnNames.add("friendlyURL");
		ctMergeColumnNames.add("iconImageId");
		ctMergeColumnNames.add("themeId");
		ctMergeColumnNames.add("colorSchemeId");
		ctMergeColumnNames.add("styleBookEntryERC");
		ctMergeColumnNames.add("styleBookEntryScopeERC");
		ctMergeColumnNames.add("css");
		ctMergeColumnNames.add("priority");
		ctMergeColumnNames.add("faviconFileEntryERC");
		ctMergeColumnNames.add("faviconFileEntryScopeERC");
		ctMergeColumnNames.add("masterLPTEERC");
		ctMergeColumnNames.add("portletLPTEERC");
		ctMergeColumnNames.add("portletLPTESERC");
		ctMergeColumnNames.add("portletLPTELE");
		ctMergeColumnNames.add("layoutSetPrototypeLayoutERC");
		ctMergeColumnNames.add("publishDate");
		ctMergeColumnNames.add("lastPublishDate");
		ctMergeColumnNames.add("status");
		ctMergeColumnNames.add("statusByUserId");
		ctMergeColumnNames.add("statusByUserName");
		ctMergeColumnNames.add("statusDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("plid"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(
			new String[] {"uuid_", "groupId", "privateLayout"});

		_uniqueIndexColumnNames.add(
			new String[] {"groupId", "privateLayout", "layoutId"});

		_uniqueIndexColumnNames.add(
			new String[] {"groupId", "privateLayout", "friendlyURL"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "groupId"});
	}

	/**
	 * Initializes the layout persistence.
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
			_SQL_SELECT_LAYOUT_WHERE, _SQL_COUNT_LAYOUT_WHERE,
			LayoutModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"layout.", "uuid", "uuid_", FinderColumn.Type.STRING, "=", true,
				true, Layout::getUuid));

		_uniquePersistenceFinderByUUID_G_P = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G_P",
				new String[] {
					String.class.getName(), Long.class.getName(),
					Boolean.class.getName()
				},
				new String[] {"uuid_", "groupId", "privateLayout"}, 0, 1, false,
				convertNullFunction(Layout::getUuid), Layout::getGroupId,
				Layout::isPrivateLayout),
			_SQL_SELECT_LAYOUT_WHERE, "",
			new FinderColumn<>(
				"layout.", "uuid", "uuid_", FinderColumn.Type.STRING, "=", true,
				true, Layout::getUuid),
			new FinderColumn<>(
				"layout.", "groupId", FinderColumn.Type.LONG, "=", true, true,
				Layout::getGroupId),
			new FinderColumn<>(
				"layout.", "privateLayout", FinderColumn.Type.BOOLEAN, "=",
				true, true, Layout::isPrivateLayout));

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
				_SQL_SELECT_LAYOUT_WHERE, _SQL_COUNT_LAYOUT_WHERE,
				LayoutModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"layout.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
					true, true, Layout::getUuid),
				new FinderColumn<>(
					"layout.", "companyId", FinderColumn.Type.LONG, "=", true,
					true, Layout::getCompanyId));

		_collectionPersistenceFinderByGroupId =
			new FilterCollectionPersistenceFinder<>(
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
				_SQL_SELECT_LAYOUT_WHERE, _SQL_COUNT_LAYOUT_WHERE,
				LayoutModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"layout.system = [$FALSE$]", "layout.system_ = [$FALSE$]",
				new FinderColumn<>(
					"layout.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, Layout::getGroupId));

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
				_SQL_SELECT_LAYOUT_WHERE, _SQL_COUNT_LAYOUT_WHERE,
				LayoutModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"layout.system = [$FALSE$]", "layout.system_ = [$FALSE$]",
				new FinderColumn<>(
					"layout.", "companyId", FinderColumn.Type.LONG, "=", true,
					true, Layout::getCompanyId));

		_collectionPersistenceFinderByParentPlid =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByParentPlid",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"parentPlid"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByParentPlid", new String[] {Long.class.getName()},
					new String[] {"parentPlid"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByParentPlid", new String[] {Long.class.getName()},
					new String[] {"parentPlid"}, false),
				_SQL_SELECT_LAYOUT_WHERE, _SQL_COUNT_LAYOUT_WHERE,
				LayoutModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"layout.system = [$FALSE$]", "layout.system_ = [$FALSE$]",
				new FinderColumn<>(
					"layout.", "parentPlid", FinderColumn.Type.LONG, "=", true,
					true, Layout::getParentPlid));

		_collectionPersistenceFinderByIconImageId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByIconImageId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"iconImageId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByIconImageId", new String[] {Long.class.getName()},
					new String[] {"iconImageId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByIconImageId", new String[] {Long.class.getName()},
					new String[] {"iconImageId"}, false),
				_SQL_SELECT_LAYOUT_WHERE, _SQL_COUNT_LAYOUT_WHERE,
				LayoutModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"layout.", "iconImageId", FinderColumn.Type.LONG, "=", true,
					true, Layout::getIconImageId));

		_collectionPersistenceFinderByLayoutSetPrototypeLayoutERC =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByLayoutSetPrototypeLayoutERC",
					new String[] {
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"layoutSetPrototypeLayoutERC"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByLayoutSetPrototypeLayoutERC",
					new String[] {String.class.getName()},
					new String[] {"layoutSetPrototypeLayoutERC"}, 0, 1, true,
					null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByLayoutSetPrototypeLayoutERC",
					new String[] {String.class.getName()},
					new String[] {"layoutSetPrototypeLayoutERC"}, 0, 1, false,
					null),
				_SQL_SELECT_LAYOUT_WHERE, _SQL_COUNT_LAYOUT_WHERE,
				LayoutModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"layout.system = [$FALSE$]", "layout.system_ = [$FALSE$]",
				new FinderColumn<>(
					"layout.", "layoutSetPrototypeLayoutERC",
					FinderColumn.Type.STRING, "=", true, true,
					Layout::getLayoutSetPrototypeLayoutERC));

		_collectionPersistenceFinderByG_P =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_P",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "privateLayout"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_P",
					new String[] {
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"groupId", "privateLayout"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_P",
					new String[] {
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"groupId", "privateLayout"}, false),
				_SQL_SELECT_LAYOUT_WHERE, _SQL_COUNT_LAYOUT_WHERE,
				LayoutModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"layout.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, Layout::getGroupId),
				new FinderColumn<>(
					"layout.", "privateLayout", FinderColumn.Type.BOOLEAN, "=",
					true, true, Layout::isPrivateLayout));

		_collectionPersistenceFinderByG_T =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_T",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "type_"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_T",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"groupId", "type_"}, 0, 2, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_T",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"groupId", "type_"}, 0, 2, false, null),
				_SQL_SELECT_LAYOUT_WHERE, _SQL_COUNT_LAYOUT_WHERE,
				LayoutModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"layout.system = [$FALSE$]", "layout.system_ = [$FALSE$]",
				new FinderColumn<>(
					"layout.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, Layout::getGroupId),
				new FinderColumn<>(
					"layout.", "type", "type_", FinderColumn.Type.STRING, "=",
					true, true, Layout::getType));

		_collectionPersistenceFinderByG_MLPTEERC =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_MLPTEERC",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "masterLPTEERC"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByG_MLPTEERC",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"groupId", "masterLPTEERC"}, 0, 2, true,
					null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByG_MLPTEERC",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"groupId", "masterLPTEERC"}, 0, 2, false,
					null),
				_SQL_SELECT_LAYOUT_WHERE, _SQL_COUNT_LAYOUT_WHERE,
				LayoutModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"layout.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, Layout::getGroupId),
				new FinderColumn<>(
					"layout.", "masterLayoutPageTemplateEntryERC",
					"masterLPTEERC", FinderColumn.Type.STRING, "=", true, true,
					Layout::getMasterLayoutPageTemplateEntryERC));

		_collectionPersistenceFinderByP_I = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByP_I",
				new String[] {
					Boolean.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"privateLayout", "iconImageId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByP_I",
				new String[] {Boolean.class.getName(), Long.class.getName()},
				new String[] {"privateLayout", "iconImageId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByP_I",
				new String[] {Boolean.class.getName(), Long.class.getName()},
				new String[] {"privateLayout", "iconImageId"}, false),
			_SQL_SELECT_LAYOUT_WHERE, _SQL_COUNT_LAYOUT_WHERE,
			LayoutModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"layout.", "privateLayout", FinderColumn.Type.BOOLEAN, "=",
				true, true, Layout::isPrivateLayout),
			new FinderColumn<>(
				"layout.", "iconImageId", FinderColumn.Type.LONG, "=", true,
				true, Layout::getIconImageId));

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
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"classNameId", "classPK"}, false),
			_SQL_SELECT_LAYOUT_WHERE, _SQL_COUNT_LAYOUT_WHERE,
			LayoutModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"layout.", "classNameId", FinderColumn.Type.LONG, "=", true,
				true, Layout::getClassNameId),
			new ArrayableFinderColumn<>(
				"layout.", "classPK", FinderColumn.Type.LONG, "=", false, true,
				true, Layout::getClassPK));

		_collectionPersistenceFinderByPLPTEERC_PLPTESERC =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByPLPTEERC_PLPTESERC",
					new String[] {
						String.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"portletLPTEERC", "portletLPTESERC"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByPLPTEERC_PLPTESERC",
					new String[] {
						String.class.getName(), String.class.getName()
					},
					new String[] {"portletLPTEERC", "portletLPTESERC"}, 0, 3,
					true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByPLPTEERC_PLPTESERC",
					new String[] {
						String.class.getName(), String.class.getName()
					},
					new String[] {"portletLPTEERC", "portletLPTESERC"}, 0, 3,
					false, null),
				_SQL_SELECT_LAYOUT_WHERE, _SQL_COUNT_LAYOUT_WHERE,
				LayoutModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"layout.system = [$FALSE$]", "layout.system_ = [$FALSE$]",
				new FinderColumn<>(
					"layout.", "portletLayoutPageTemplateEntryERC",
					"portletLPTEERC", FinderColumn.Type.STRING, "=", true, true,
					Layout::getPortletLayoutPageTemplateEntryERC),
				new FinderColumn<>(
					"layout.", "portletLayoutPageTemplateEntryScopeERC",
					"portletLPTESERC", FinderColumn.Type.STRING, "=", true,
					true, Layout::getPortletLayoutPageTemplateEntryScopeERC));

		_uniquePersistenceFinderByG_P_L = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_P_L",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					Long.class.getName()
				},
				new String[] {"groupId", "privateLayout", "layoutId"}, 0, 0,
				false, Layout::getGroupId, Layout::isPrivateLayout,
				Layout::getLayoutId),
			_SQL_SELECT_LAYOUT_WHERE, "",
			new FinderColumn<>(
				"layout.", "groupId", FinderColumn.Type.LONG, "=", true, true,
				Layout::getGroupId),
			new FinderColumn<>(
				"layout.", "privateLayout", FinderColumn.Type.BOOLEAN, "=",
				true, true, Layout::isPrivateLayout),
			new FinderColumn<>(
				"layout.", "layoutId", FinderColumn.Type.LONG, "=", true, true,
				Layout::getLayoutId));

		_finderPathWithPaginationFindByG_P_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_P_P",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId", "privateLayout", "parentLayoutId"}, true);

		_finderPathWithoutPaginationFindByG_P_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_P_P",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Long.class.getName()
			},
			new String[] {"groupId", "privateLayout", "parentLayoutId"}, true);

		_finderPathCountByG_P_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_P_P",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Long.class.getName()
			},
			new String[] {"groupId", "privateLayout", "parentLayoutId"}, false);

		_finderPathWithPaginationCountByG_P_P = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_P_P",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Long.class.getName()
			},
			new String[] {"groupId", "privateLayout", "parentLayoutId"}, false);

		_collectionPersistenceFinderByG_P_T =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_P_T",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "privateLayout", "type_"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_P_T",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						String.class.getName()
					},
					new String[] {"groupId", "privateLayout", "type_"}, 0, 4,
					true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_P_T",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						String.class.getName()
					},
					new String[] {"groupId", "privateLayout", "type_"}, 0, 4,
					false, null),
				_SQL_SELECT_LAYOUT_WHERE, _SQL_COUNT_LAYOUT_WHERE,
				LayoutModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"layout.system = [$FALSE$]", "layout.system_ = [$FALSE$]",
				new FinderColumn<>(
					"layout.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, Layout::getGroupId),
				new FinderColumn<>(
					"layout.", "privateLayout", FinderColumn.Type.BOOLEAN, "=",
					true, true, Layout::isPrivateLayout),
				new ArrayableFinderColumn<>(
					"layout.", "type", "type_", FinderColumn.Type.STRING, "=",
					false, true, true, Layout::getType));

		_collectionPersistenceFinderByG_P_S =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_P_S",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "privateLayout", "system_"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_P_S",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Boolean.class.getName()
					},
					new String[] {"groupId", "privateLayout", "system_"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_P_S",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Boolean.class.getName()
					},
					new String[] {"groupId", "privateLayout", "system_"},
					false),
				_SQL_SELECT_LAYOUT_WHERE, _SQL_COUNT_LAYOUT_WHERE,
				LayoutModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"layout.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, Layout::getGroupId),
				new FinderColumn<>(
					"layout.", "privateLayout", FinderColumn.Type.BOOLEAN, "=",
					true, true, Layout::isPrivateLayout),
				new FinderColumn<>(
					"layout.", "system", "system_", FinderColumn.Type.BOOLEAN,
					"=", true, true, Layout::isSystem));

		_uniquePersistenceFinderByG_P_F = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_P_F",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					String.class.getName()
				},
				new String[] {"groupId", "privateLayout", "friendlyURL"}, 0, 4,
				false, Layout::getGroupId, Layout::isPrivateLayout,
				convertNullFunction(Layout::getFriendlyURL)),
			_SQL_SELECT_LAYOUT_WHERE, "",
			new FinderColumn<>(
				"layout.", "groupId", FinderColumn.Type.LONG, "=", true, true,
				Layout::getGroupId),
			new FinderColumn<>(
				"layout.", "privateLayout", FinderColumn.Type.BOOLEAN, "=",
				true, true, Layout::isPrivateLayout),
			new FinderColumn<>(
				"layout.", "friendlyURL", FinderColumn.Type.STRING, "=", true,
				true, Layout::getFriendlyURL));

		_collectionPersistenceFinderByG_P_LSPLE =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_P_LSPLE",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "privateLayout",
						"layoutSetPrototypeLayoutERC"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByG_P_LSPLE",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						String.class.getName()
					},
					new String[] {
						"groupId", "privateLayout",
						"layoutSetPrototypeLayoutERC"
					},
					0, 4, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByG_P_LSPLE",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						String.class.getName()
					},
					new String[] {
						"groupId", "privateLayout",
						"layoutSetPrototypeLayoutERC"
					},
					0, 4, false, null),
				_SQL_SELECT_LAYOUT_WHERE, _SQL_COUNT_LAYOUT_WHERE,
				LayoutModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"layout.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, Layout::getGroupId),
				new FinderColumn<>(
					"layout.", "privateLayout", FinderColumn.Type.BOOLEAN, "=",
					true, true, Layout::isPrivateLayout),
				new FinderColumn<>(
					"layout.", "layoutSetPrototypeLayoutERC",
					FinderColumn.Type.STRING, "=", true, true,
					Layout::getLayoutSetPrototypeLayoutERC));

		_collectionPersistenceFinderByG_P_ST =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_P_ST",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "privateLayout", "status"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_P_ST",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "privateLayout", "status"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_P_ST",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName()
					},
					new String[] {"groupId", "privateLayout", "status"}, false),
				_SQL_SELECT_LAYOUT_WHERE, _SQL_COUNT_LAYOUT_WHERE,
				LayoutModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"layout.system = [$FALSE$]", "layout.system_ = [$FALSE$]",
				new FinderColumn<>(
					"layout.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, Layout::getGroupId),
				new FinderColumn<>(
					"layout.", "privateLayout", FinderColumn.Type.BOOLEAN, "=",
					true, true, Layout::isPrivateLayout),
				new ArrayableFinderColumn<>(
					"layout.", "status", FinderColumn.Type.INTEGER, "=", false,
					true, true, Layout::getStatus));

		_finderPathWithPaginationFindByG_P_P_H = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_P_P_H",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Long.class.getName(), Boolean.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {
				"groupId", "privateLayout", "parentLayoutId", "hidden_"
			},
			true);

		_finderPathWithoutPaginationFindByG_P_P_H = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_P_P_H",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Long.class.getName(), Boolean.class.getName()
			},
			new String[] {
				"groupId", "privateLayout", "parentLayoutId", "hidden_"
			},
			true);

		_finderPathCountByG_P_P_H = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_P_P_H",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Long.class.getName(), Boolean.class.getName()
			},
			new String[] {
				"groupId", "privateLayout", "parentLayoutId", "hidden_"
			},
			false);

		_finderPathWithPaginationCountByG_P_P_H = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_P_P_H",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Long.class.getName(), Boolean.class.getName()
			},
			new String[] {
				"groupId", "privateLayout", "parentLayoutId", "hidden_"
			},
			false);

		_finderPathWithPaginationFindByG_P_P_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_P_P_S",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Long.class.getName(), Boolean.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {
				"groupId", "privateLayout", "parentLayoutId", "system_"
			},
			true);

		_finderPathWithoutPaginationFindByG_P_P_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_P_P_S",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Long.class.getName(), Boolean.class.getName()
			},
			new String[] {
				"groupId", "privateLayout", "parentLayoutId", "system_"
			},
			true);

		_finderPathCountByG_P_P_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_P_P_S",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Long.class.getName(), Boolean.class.getName()
			},
			new String[] {
				"groupId", "privateLayout", "parentLayoutId", "system_"
			},
			false);

		_finderPathWithPaginationCountByG_P_P_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_P_P_S",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Long.class.getName(), Boolean.class.getName()
			},
			new String[] {
				"groupId", "privateLayout", "parentLayoutId", "system_"
			},
			false);

		_collectionPersistenceFinderByG_P_P_LteP =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_P_P_LteP",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "privateLayout", "parentLayoutId", "priority"
					},
					true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_P_P_LteP",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {
						"groupId", "privateLayout", "parentLayoutId", "priority"
					},
					false),
				_SQL_SELECT_LAYOUT_WHERE, _SQL_COUNT_LAYOUT_WHERE,
				LayoutModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"layout.system = [$FALSE$]", "layout.system_ = [$FALSE$]",
				new FinderColumn<>(
					"layout.", "groupId", FinderColumn.Type.LONG, "=", true,
					true, Layout::getGroupId),
				new FinderColumn<>(
					"layout.", "privateLayout", FinderColumn.Type.BOOLEAN, "=",
					true, true, Layout::isPrivateLayout),
				new FinderColumn<>(
					"layout.", "parentLayoutId", FinderColumn.Type.LONG, "=",
					true, true, Layout::getParentLayoutId),
				new FinderColumn<>(
					"layout.", "priority", FinderColumn.Type.INTEGER, "<=",
					true, true, Layout::getPriority));

		_uniquePersistenceFinderByERC_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "groupId"}, 0, 1, false,
				convertNullFunction(Layout::getExternalReferenceCode),
				Layout::getGroupId),
			_SQL_SELECT_LAYOUT_WHERE, "",
			new FinderColumn<>(
				"layout.", "externalReferenceCode", FinderColumn.Type.STRING,
				"=", true, true, Layout::getExternalReferenceCode),
			new FinderColumn<>(
				"layout.", "groupId", FinderColumn.Type.LONG, "=", true, true,
				Layout::getGroupId));

		LayoutUtil.setPersistence(this);
	}

	public void destroy() {
		LayoutUtil.setPersistence(null);

		EntityCacheUtil.removeCache(LayoutImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		LayoutModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_LAYOUT =
		"SELECT layout FROM Layout layout";

	private static final String _SQL_SELECT_LAYOUT_WHERE =
		"SELECT layout FROM Layout layout WHERE ";

	private static final String _SQL_COUNT_LAYOUT_WHERE =
		"SELECT COUNT(layout) FROM Layout layout WHERE ";

	private static final String _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN =
		"layout.plid";

	private static final String _FILTER_SQL_SELECT_LAYOUT_WHERE =
		"SELECT DISTINCT {layout.*} FROM Layout layout WHERE ";

	private static final String
		_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_1 =
			"SELECT {Layout.*} FROM (SELECT DISTINCT layout.plid FROM Layout layout WHERE ";

	private static final String
		_FILTER_SQL_SELECT_LAYOUT_NO_INLINE_DISTINCT_WHERE_2 =
			") TEMP_TABLE INNER JOIN Layout ON TEMP_TABLE.plid = Layout.plid";

	private static final String _FILTER_SQL_COUNT_LAYOUT_WHERE =
		"SELECT COUNT(DISTINCT layout.plid) AS COUNT_VALUE FROM Layout layout WHERE ";

	private static final String _FILTER_ENTITY_ALIAS = "layout";

	private static final String _FILTER_ENTITY_TABLE = "Layout";

	private static final String _ORDER_BY_ENTITY_TABLE = "Layout.";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No Layout exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {
			"uuid", "type", "hidden", "system",
			"masterLayoutPageTemplateEntryERC",
			"portletLayoutPageTemplateEntryERC",
			"portletLayoutPageTemplateEntryScopeERC",
			"portletLayoutPageTemplateEntryLinkEnabled"
		});

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-157149151