/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.impl;

import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.DuplicateGroupExternalReferenceCodeException;
import com.liferay.portal.kernel.exception.NoSuchGroupException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupTable;
import com.liferay.portal.kernel.sanitizer.Sanitizer;
import com.liferay.portal.kernel.sanitizer.SanitizerException;
import com.liferay.portal.kernel.sanitizer.SanitizerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.GroupPersistence;
import com.liferay.portal.kernel.service.persistence.GroupUtil;
import com.liferay.portal.kernel.service.persistence.OrganizationPersistence;
import com.liferay.portal.kernel.service.persistence.RolePersistence;
import com.liferay.portal.kernel.service.persistence.UserGroupPersistence;
import com.liferay.portal.kernel.service.persistence.UserPersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelperUtil;
import com.liferay.portal.kernel.service.persistence.impl.ArrayableFinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.TableMapper;
import com.liferay.portal.kernel.service.persistence.impl.TableMapperFactory;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.model.impl.GroupImpl;
import com.liferay.portal.model.impl.GroupModelImpl;

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
 * The persistence implementation for the group service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class GroupPersistenceImpl
	extends BasePersistenceImpl<Group, NoSuchGroupException>
	implements GroupPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>GroupUtil</code> to access the group persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		GroupImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<Group, NoSuchGroupException>
		_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the groups where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<Group> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first group in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByUuid_First(
			String uuid, OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		return _collectionPersistenceFinderByUuid.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid},
			orderByComparator);
	}

	/**
	 * Returns the first group in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByUuid_First(
		String uuid, OrderByComparator<Group> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid},
			orderByComparator);
	}

	/**
	 * Removes all the groups where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	/**
	 * Returns the number of groups where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching groups
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	private UniquePersistenceFinder<Group, NoSuchGroupException>
		_uniquePersistenceFinderByUUID_G;

	/**
	 * Returns the group where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchGroupException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByUUID_G(String uuid, long groupId)
		throws NoSuchGroupException {

		return _uniquePersistenceFinderByUUID_G.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, groupId});
	}

	/**
	 * Returns the group where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return _uniquePersistenceFinderByUUID_G.fetch(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, groupId},
			useFinderCache);
	}

	/**
	 * Removes the group where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the group that was removed
	 */
	@Override
	public Group removeByUUID_G(String uuid, long groupId)
		throws NoSuchGroupException {

		Group group = findByUUID_G(uuid, groupId);

		return remove(group);
	}

	/**
	 * Returns the number of groups where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching groups
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		return _uniquePersistenceFinderByUUID_G.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, groupId});
	}

	private CollectionPersistenceFinder<Group, NoSuchGroupException>
		_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the groups where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<Group> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first group in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			orderByComparator);
	}

	/**
	 * Returns the first group in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<Group> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			orderByComparator);
	}

	/**
	 * Removes all the groups where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of groups where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching groups
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder<Group, NoSuchGroupException>
		_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the groups where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<Group> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByCompanyId_First(
			long companyId, OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			orderByComparator);
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByCompanyId_First(
		long companyId, OrderByComparator<Group> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			orderByComparator);
	}

	/**
	 * Removes all the groups where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId});
	}

	/**
	 * Returns the number of groups where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching groups
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId});
	}

	private CollectionPersistenceFinder<Group, NoSuchGroupException>
		_collectionPersistenceFinderByLiveGroupId;

	/**
	 * Returns an ordered range of all the groups where liveGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param liveGroupId the live group ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByLiveGroupId(
		long liveGroupId, int start, int end,
		OrderByComparator<Group> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByLiveGroupId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {liveGroupId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first group in the ordered set where liveGroupId = &#63;.
	 *
	 * @param liveGroupId the live group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByLiveGroupId_First(
			long liveGroupId, OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		return _collectionPersistenceFinderByLiveGroupId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {liveGroupId},
			orderByComparator);
	}

	/**
	 * Returns the first group in the ordered set where liveGroupId = &#63;.
	 *
	 * @param liveGroupId the live group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByLiveGroupId_First(
		long liveGroupId, OrderByComparator<Group> orderByComparator) {

		return _collectionPersistenceFinderByLiveGroupId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {liveGroupId},
			orderByComparator);
	}

	/**
	 * Removes all the groups where liveGroupId = &#63; from the database.
	 *
	 * @param liveGroupId the live group ID
	 */
	@Override
	public void removeByLiveGroupId(long liveGroupId) {
		_collectionPersistenceFinderByLiveGroupId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {liveGroupId});
	}

	/**
	 * Returns the number of groups where liveGroupId = &#63;.
	 *
	 * @param liveGroupId the live group ID
	 * @return the number of matching groups
	 */
	@Override
	public int countByLiveGroupId(long liveGroupId) {
		return _collectionPersistenceFinderByLiveGroupId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {liveGroupId});
	}

	private CollectionPersistenceFinder<Group, NoSuchGroupException>
		_collectionPersistenceFinderByC_C;

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByC_C(
		long companyId, long classNameId, int start, int end,
		OrderByComparator<Group> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByC_C_First(
			long companyId, long classNameId,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		return _collectionPersistenceFinderByC_C.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId}, orderByComparator);
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByC_C_First(
		long companyId, long classNameId,
		OrderByComparator<Group> orderByComparator) {

		return _collectionPersistenceFinderByC_C.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId}, orderByComparator);
	}

	/**
	 * Removes all the groups where companyId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 */
	@Override
	public void removeByC_C(long companyId, long classNameId) {
		_collectionPersistenceFinderByC_C.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId});
	}

	/**
	 * Returns the number of groups where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the number of matching groups
	 */
	@Override
	public int countByC_C(long companyId, long classNameId) {
		return _collectionPersistenceFinderByC_C.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId});
	}

	private CollectionPersistenceFinder<Group, NoSuchGroupException>
		_collectionPersistenceFinderByC_P;

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and parentGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByC_P(
		long companyId, long parentGroupId, int start, int end,
		OrderByComparator<Group> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByC_P.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, parentGroupId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and parentGroupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByC_P_First(
			long companyId, long parentGroupId,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		return _collectionPersistenceFinderByC_P.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, parentGroupId}, orderByComparator);
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and parentGroupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByC_P_First(
		long companyId, long parentGroupId,
		OrderByComparator<Group> orderByComparator) {

		return _collectionPersistenceFinderByC_P.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, parentGroupId}, orderByComparator);
	}

	/**
	 * Removes all the groups where companyId = &#63; and parentGroupId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 */
	@Override
	public void removeByC_P(long companyId, long parentGroupId) {
		_collectionPersistenceFinderByC_P.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, parentGroupId});
	}

	/**
	 * Returns the number of groups where companyId = &#63; and parentGroupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @return the number of matching groups
	 */
	@Override
	public int countByC_P(long companyId, long parentGroupId) {
		return _collectionPersistenceFinderByC_P.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, parentGroupId});
	}

	private CollectionPersistenceFinder<Group, NoSuchGroupException>
		_collectionPersistenceFinderByC_GK;
	private UniquePersistenceFinder<Group, NoSuchGroupException>
		_uniquePersistenceFinderByC_GK;

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and groupKey = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param groupKeys the group keys
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByC_GK(
		long companyId, String[] groupKeys, int start, int end,
		OrderByComparator<Group> orderByComparator, boolean useFinderCache) {

		groupKeys = ArrayUtil.sortedUnique(groupKeys);

		if (groupKeys.length == 1) {
			Group group = fetchByC_GK(companyId, groupKeys[0], useFinderCache);

			if (group == null) {
				return Collections.emptyList();
			}
			else {
				List<Group> list = new ArrayList<Group>(1);

				list.add(group);

				return list;
			}
		}

		return _collectionPersistenceFinderByC_GK.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, groupKeys}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the group where companyId = &#63; and groupKey = &#63; or throws a <code>NoSuchGroupException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param groupKey the group key
	 * @return the matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByC_GK(long companyId, String groupKey)
		throws NoSuchGroupException {

		return _uniquePersistenceFinderByC_GK.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, groupKey});
	}

	/**
	 * Returns the group where companyId = &#63; and groupKey = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param groupKey the group key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByC_GK(
		long companyId, String groupKey, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_GK.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, groupKey}, useFinderCache);
	}

	/**
	 * Removes the group where companyId = &#63; and groupKey = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param groupKey the group key
	 * @return the group that was removed
	 */
	@Override
	public Group removeByC_GK(long companyId, String groupKey)
		throws NoSuchGroupException {

		Group group = findByC_GK(companyId, groupKey);

		return remove(group);
	}

	/**
	 * Returns the number of groups where companyId = &#63; and groupKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param groupKey the group key
	 * @return the number of matching groups
	 */
	@Override
	public int countByC_GK(long companyId, String groupKey) {
		return _collectionPersistenceFinderByC_GK.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, new String[] {groupKey}});
	}

	/**
	 * Returns the number of groups where companyId = &#63; and groupKey = any &#63;.
	 *
	 * @param companyId the company ID
	 * @param groupKeys the group keys
	 * @return the number of matching groups
	 */
	@Override
	public int countByC_GK(long companyId, String[] groupKeys) {
		return _collectionPersistenceFinderByC_GK.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, ArrayUtil.sortedUnique(groupKeys)});
	}

	private UniquePersistenceFinder<Group, NoSuchGroupException>
		_uniquePersistenceFinderByC_F;

	/**
	 * Returns the group where companyId = &#63; and friendlyURL = &#63; or throws a <code>NoSuchGroupException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param friendlyURL the friendly url
	 * @return the matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByC_F(long companyId, String friendlyURL)
		throws NoSuchGroupException {

		return _uniquePersistenceFinderByC_F.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, friendlyURL});
	}

	/**
	 * Returns the group where companyId = &#63; and friendlyURL = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param friendlyURL the friendly url
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByC_F(
		long companyId, String friendlyURL, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_F.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, friendlyURL}, useFinderCache);
	}

	/**
	 * Removes the group where companyId = &#63; and friendlyURL = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param friendlyURL the friendly url
	 * @return the group that was removed
	 */
	@Override
	public Group removeByC_F(long companyId, String friendlyURL)
		throws NoSuchGroupException {

		Group group = findByC_F(companyId, friendlyURL);

		return remove(group);
	}

	/**
	 * Returns the number of groups where companyId = &#63; and friendlyURL = &#63;.
	 *
	 * @param companyId the company ID
	 * @param friendlyURL the friendly url
	 * @return the number of matching groups
	 */
	@Override
	public int countByC_F(long companyId, String friendlyURL) {
		return _uniquePersistenceFinderByC_F.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, friendlyURL});
	}

	private CollectionPersistenceFinder<Group, NoSuchGroupException>
		_collectionPersistenceFinderByC_S;

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByC_S(
		long companyId, boolean site, int start, int end,
		OrderByComparator<Group> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByC_S.find(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, site},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByC_S_First(
			long companyId, boolean site,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		return _collectionPersistenceFinderByC_S.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, site},
			orderByComparator);
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByC_S_First(
		long companyId, boolean site,
		OrderByComparator<Group> orderByComparator) {

		return _collectionPersistenceFinderByC_S.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, site},
			orderByComparator);
	}

	/**
	 * Removes all the groups where companyId = &#63; and site = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param site the site
	 */
	@Override
	public void removeByC_S(long companyId, boolean site) {
		_collectionPersistenceFinderByC_S.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, site});
	}

	/**
	 * Returns the number of groups where companyId = &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param site the site
	 * @return the number of matching groups
	 */
	@Override
	public int countByC_S(long companyId, boolean site) {
		return _collectionPersistenceFinderByC_S.count(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, site});
	}

	private CollectionPersistenceFinder<Group, NoSuchGroupException>
		_collectionPersistenceFinderByC_A;

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByC_A(
		long companyId, boolean active, int start, int end,
		OrderByComparator<Group> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByC_A.find(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, active},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByC_A_First(
			long companyId, boolean active,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		return _collectionPersistenceFinderByC_A.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, active},
			orderByComparator);
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByC_A_First(
		long companyId, boolean active,
		OrderByComparator<Group> orderByComparator) {

		return _collectionPersistenceFinderByC_A.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, active},
			orderByComparator);
	}

	/**
	 * Removes all the groups where companyId = &#63; and active = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 */
	@Override
	public void removeByC_A(long companyId, boolean active) {
		_collectionPersistenceFinderByC_A.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, active});
	}

	/**
	 * Returns the number of groups where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @return the number of matching groups
	 */
	@Override
	public int countByC_A(long companyId, boolean active) {
		return _collectionPersistenceFinderByC_A.count(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, active});
	}

	private CollectionPersistenceFinder<Group, NoSuchGroupException>
		_collectionPersistenceFinderByC_CPK;

	/**
	 * Returns an ordered range of all the groups where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByC_CPK(
		long classNameId, long classPK, int start, int end,
		OrderByComparator<Group> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByC_CPK.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first group in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByC_CPK_First(
			long classNameId, long classPK,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		return _collectionPersistenceFinderByC_CPK.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK}, orderByComparator);
	}

	/**
	 * Returns the first group in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByC_CPK_First(
		long classNameId, long classPK,
		OrderByComparator<Group> orderByComparator) {

		return _collectionPersistenceFinderByC_CPK.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK}, orderByComparator);
	}

	/**
	 * Removes all the groups where classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	@Override
	public void removeByC_CPK(long classNameId, long classPK) {
		_collectionPersistenceFinderByC_CPK.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK});
	}

	/**
	 * Returns the number of groups where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching groups
	 */
	@Override
	public int countByC_CPK(long classNameId, long classPK) {
		return _collectionPersistenceFinderByC_CPK.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK});
	}

	private CollectionPersistenceFinder<Group, NoSuchGroupException>
		_collectionPersistenceFinderByT_A;

	/**
	 * Returns an ordered range of all the groups where type = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param active the active
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByT_A(
		int type, boolean active, int start, int end,
		OrderByComparator<Group> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByT_A.find(
			FinderCacheUtil.getFinderCache(), new Object[] {type, active},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first group in the ordered set where type = &#63; and active = &#63;.
	 *
	 * @param type the type
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByT_A_First(
			int type, boolean active,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		return _collectionPersistenceFinderByT_A.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {type, active},
			orderByComparator);
	}

	/**
	 * Returns the first group in the ordered set where type = &#63; and active = &#63;.
	 *
	 * @param type the type
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByT_A_First(
		int type, boolean active, OrderByComparator<Group> orderByComparator) {

		return _collectionPersistenceFinderByT_A.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {type, active},
			orderByComparator);
	}

	/**
	 * Removes all the groups where type = &#63; and active = &#63; from the database.
	 *
	 * @param type the type
	 * @param active the active
	 */
	@Override
	public void removeByT_A(int type, boolean active) {
		_collectionPersistenceFinderByT_A.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {type, active});
	}

	/**
	 * Returns the number of groups where type = &#63; and active = &#63;.
	 *
	 * @param type the type
	 * @param active the active
	 * @return the number of matching groups
	 */
	@Override
	public int countByT_A(int type, boolean active) {
		return _collectionPersistenceFinderByT_A.count(
			FinderCacheUtil.getFinderCache(), new Object[] {type, active});
	}

	private CollectionPersistenceFinder<Group, NoSuchGroupException>
		_collectionPersistenceFinderByGtG_C_P;

	/**
	 * Returns all the groups where groupId &gt; &#63; and companyId = &#63; and parentGroupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @return the matching groups
	 */
	@Override
	public List<Group> findByGtG_C_P(
		long groupId, long companyId, long parentGroupId) {

		return findByGtG_C_P(
			groupId, companyId, parentGroupId, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the groups where groupId &gt; &#63; and companyId = &#63; and parentGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	@Override
	public List<Group> findByGtG_C_P(
		long groupId, long companyId, long parentGroupId, int start, int end) {

		return findByGtG_C_P(
			groupId, companyId, parentGroupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the groups where groupId &gt; &#63; and companyId = &#63; and parentGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByGtG_C_P(
		long groupId, long companyId, long parentGroupId, int start, int end,
		OrderByComparator<Group> orderByComparator) {

		return findByGtG_C_P(
			groupId, companyId, parentGroupId, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the groups where groupId &gt; &#63; and companyId = &#63; and parentGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByGtG_C_P(
		long groupId, long companyId, long parentGroupId, int start, int end,
		OrderByComparator<Group> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByGtG_C_P.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, companyId, parentGroupId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first group in the ordered set where groupId &gt; &#63; and companyId = &#63; and parentGroupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByGtG_C_P_First(
			long groupId, long companyId, long parentGroupId,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		return _collectionPersistenceFinderByGtG_C_P.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, companyId, parentGroupId},
			orderByComparator);
	}

	/**
	 * Returns the first group in the ordered set where groupId &gt; &#63; and companyId = &#63; and parentGroupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByGtG_C_P_First(
		long groupId, long companyId, long parentGroupId,
		OrderByComparator<Group> orderByComparator) {

		return _collectionPersistenceFinderByGtG_C_P.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, companyId, parentGroupId},
			orderByComparator);
	}

	/**
	 * Removes all the groups where groupId &gt; &#63; and companyId = &#63; and parentGroupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 */
	@Override
	public void removeByGtG_C_P(
		long groupId, long companyId, long parentGroupId) {

		_collectionPersistenceFinderByGtG_C_P.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, companyId, parentGroupId});
	}

	/**
	 * Returns the number of groups where groupId &gt; &#63; and companyId = &#63; and parentGroupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @return the number of matching groups
	 */
	@Override
	public int countByGtG_C_P(
		long groupId, long companyId, long parentGroupId) {

		return _collectionPersistenceFinderByGtG_C_P.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, companyId, parentGroupId});
	}

	private UniquePersistenceFinder<Group, NoSuchGroupException>
		_uniquePersistenceFinderByC_C_C;

	/**
	 * Returns the group where companyId = &#63; and classNameId = &#63; and classPK = &#63; or throws a <code>NoSuchGroupException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByC_C_C(long companyId, long classNameId, long classPK)
		throws NoSuchGroupException {

		return _uniquePersistenceFinderByC_C_C.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId, classPK});
	}

	/**
	 * Returns the group where companyId = &#63; and classNameId = &#63; and classPK = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByC_C_C(
		long companyId, long classNameId, long classPK,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByC_C_C.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId, classPK}, useFinderCache);
	}

	/**
	 * Removes the group where companyId = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the group that was removed
	 */
	@Override
	public Group removeByC_C_C(long companyId, long classNameId, long classPK)
		throws NoSuchGroupException {

		Group group = findByC_C_C(companyId, classNameId, classPK);

		return remove(group);
	}

	/**
	 * Returns the number of groups where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching groups
	 */
	@Override
	public int countByC_C_C(long companyId, long classNameId, long classPK) {
		return _uniquePersistenceFinderByC_C_C.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId, classPK});
	}

	private CollectionPersistenceFinder<Group, NoSuchGroupException>
		_collectionPersistenceFinderByC_C_P;

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and classNameId = &#63; and parentGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByC_C_P(
		long companyId, long classNameId, long parentGroupId, int start,
		int end, OrderByComparator<Group> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C_P.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId, parentGroupId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and classNameId = &#63; and parentGroupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByC_C_P_First(
			long companyId, long classNameId, long parentGroupId,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		return _collectionPersistenceFinderByC_C_P.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId, parentGroupId},
			orderByComparator);
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and classNameId = &#63; and parentGroupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByC_C_P_First(
		long companyId, long classNameId, long parentGroupId,
		OrderByComparator<Group> orderByComparator) {

		return _collectionPersistenceFinderByC_C_P.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId, parentGroupId},
			orderByComparator);
	}

	/**
	 * Removes all the groups where companyId = &#63; and classNameId = &#63; and parentGroupId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 */
	@Override
	public void removeByC_C_P(
		long companyId, long classNameId, long parentGroupId) {

		_collectionPersistenceFinderByC_C_P.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId, parentGroupId});
	}

	/**
	 * Returns the number of groups where companyId = &#63; and classNameId = &#63; and parentGroupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 * @return the number of matching groups
	 */
	@Override
	public int countByC_C_P(
		long companyId, long classNameId, long parentGroupId) {

		return _collectionPersistenceFinderByC_C_P.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId, parentGroupId});
	}

	private CollectionPersistenceFinder<Group, NoSuchGroupException>
		_collectionPersistenceFinderByC_C_S;

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and classNameId = &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByC_C_S(
		long companyId, long classNameId, boolean site, int start, int end,
		OrderByComparator<Group> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C_S.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId, site}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and classNameId = &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByC_C_S_First(
			long companyId, long classNameId, boolean site,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		return _collectionPersistenceFinderByC_C_S.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId, site}, orderByComparator);
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and classNameId = &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByC_C_S_First(
		long companyId, long classNameId, boolean site,
		OrderByComparator<Group> orderByComparator) {

		return _collectionPersistenceFinderByC_C_S.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId, site}, orderByComparator);
	}

	/**
	 * Removes all the groups where companyId = &#63; and classNameId = &#63; and site = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param site the site
	 */
	@Override
	public void removeByC_C_S(long companyId, long classNameId, boolean site) {
		_collectionPersistenceFinderByC_C_S.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId, site});
	}

	/**
	 * Returns the number of groups where companyId = &#63; and classNameId = &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param site the site
	 * @return the number of matching groups
	 */
	@Override
	public int countByC_C_S(long companyId, long classNameId, boolean site) {
		return _collectionPersistenceFinderByC_C_S.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId, site});
	}

	private CollectionPersistenceFinder<Group, NoSuchGroupException>
		_collectionPersistenceFinderByC_P_S;

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and parentGroupId = &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByC_P_S(
		long companyId, long parentGroupId, boolean site, int start, int end,
		OrderByComparator<Group> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByC_P_S.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, parentGroupId, site}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and parentGroupId = &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByC_P_S_First(
			long companyId, long parentGroupId, boolean site,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		return _collectionPersistenceFinderByC_P_S.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, parentGroupId, site}, orderByComparator);
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and parentGroupId = &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByC_P_S_First(
		long companyId, long parentGroupId, boolean site,
		OrderByComparator<Group> orderByComparator) {

		return _collectionPersistenceFinderByC_P_S.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, parentGroupId, site}, orderByComparator);
	}

	/**
	 * Removes all the groups where companyId = &#63; and parentGroupId = &#63; and site = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 */
	@Override
	public void removeByC_P_S(
		long companyId, long parentGroupId, boolean site) {

		_collectionPersistenceFinderByC_P_S.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, parentGroupId, site});
	}

	/**
	 * Returns the number of groups where companyId = &#63; and parentGroupId = &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @return the number of matching groups
	 */
	@Override
	public int countByC_P_S(long companyId, long parentGroupId, boolean site) {
		return _collectionPersistenceFinderByC_P_S.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, parentGroupId, site});
	}

	private UniquePersistenceFinder<Group, NoSuchGroupException>
		_uniquePersistenceFinderByC_L_GK;

	/**
	 * Returns the group where companyId = &#63; and liveGroupId = &#63; and groupKey = &#63; or throws a <code>NoSuchGroupException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param liveGroupId the live group ID
	 * @param groupKey the group key
	 * @return the matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByC_L_GK(long companyId, long liveGroupId, String groupKey)
		throws NoSuchGroupException {

		return _uniquePersistenceFinderByC_L_GK.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, liveGroupId, groupKey});
	}

	/**
	 * Returns the group where companyId = &#63; and liveGroupId = &#63; and groupKey = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param liveGroupId the live group ID
	 * @param groupKey the group key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByC_L_GK(
		long companyId, long liveGroupId, String groupKey,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByC_L_GK.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, liveGroupId, groupKey}, useFinderCache);
	}

	/**
	 * Removes the group where companyId = &#63; and liveGroupId = &#63; and groupKey = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param liveGroupId the live group ID
	 * @param groupKey the group key
	 * @return the group that was removed
	 */
	@Override
	public Group removeByC_L_GK(
			long companyId, long liveGroupId, String groupKey)
		throws NoSuchGroupException {

		Group group = findByC_L_GK(companyId, liveGroupId, groupKey);

		return remove(group);
	}

	/**
	 * Returns the number of groups where companyId = &#63; and liveGroupId = &#63; and groupKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param liveGroupId the live group ID
	 * @param groupKey the group key
	 * @return the number of matching groups
	 */
	@Override
	public int countByC_L_GK(
		long companyId, long liveGroupId, String groupKey) {

		return _uniquePersistenceFinderByC_L_GK.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, liveGroupId, groupKey});
	}

	private CollectionPersistenceFinder<Group, NoSuchGroupException>
		_collectionPersistenceFinderByC_LikeT_S;

	/**
	 * Returns all the groups where companyId = &#63; and treePath LIKE &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @param site the site
	 * @return the matching groups
	 */
	@Override
	public List<Group> findByC_LikeT_S(
		long companyId, String treePath, boolean site) {

		return findByC_LikeT_S(
			companyId, treePath, site, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the groups where companyId = &#63; and treePath LIKE &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	@Override
	public List<Group> findByC_LikeT_S(
		long companyId, String treePath, boolean site, int start, int end) {

		return findByC_LikeT_S(companyId, treePath, site, start, end, null);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and treePath LIKE &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByC_LikeT_S(
		long companyId, String treePath, boolean site, int start, int end,
		OrderByComparator<Group> orderByComparator) {

		return findByC_LikeT_S(
			companyId, treePath, site, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and treePath LIKE &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByC_LikeT_S(
		long companyId, String treePath, boolean site, int start, int end,
		OrderByComparator<Group> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByC_LikeT_S.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, treePath, site}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and treePath LIKE &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByC_LikeT_S_First(
			long companyId, String treePath, boolean site,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		return _collectionPersistenceFinderByC_LikeT_S.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, treePath, site}, orderByComparator);
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and treePath LIKE &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByC_LikeT_S_First(
		long companyId, String treePath, boolean site,
		OrderByComparator<Group> orderByComparator) {

		return _collectionPersistenceFinderByC_LikeT_S.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, treePath, site}, orderByComparator);
	}

	/**
	 * Removes all the groups where companyId = &#63; and treePath LIKE &#63; and site = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @param site the site
	 */
	@Override
	public void removeByC_LikeT_S(
		long companyId, String treePath, boolean site) {

		_collectionPersistenceFinderByC_LikeT_S.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, treePath, site});
	}

	/**
	 * Returns the number of groups where companyId = &#63; and treePath LIKE &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param treePath the tree path
	 * @param site the site
	 * @return the number of matching groups
	 */
	@Override
	public int countByC_LikeT_S(long companyId, String treePath, boolean site) {
		return _collectionPersistenceFinderByC_LikeT_S.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, treePath, site});
	}

	private CollectionPersistenceFinder<Group, NoSuchGroupException>
		_collectionPersistenceFinderByC_LikeN_S;

	/**
	 * Returns all the groups where companyId = &#63; and name LIKE &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param site the site
	 * @return the matching groups
	 */
	@Override
	public List<Group> findByC_LikeN_S(
		long companyId, String name, boolean site) {

		return findByC_LikeN_S(
			companyId, name, site, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the groups where companyId = &#63; and name LIKE &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	@Override
	public List<Group> findByC_LikeN_S(
		long companyId, String name, boolean site, int start, int end) {

		return findByC_LikeN_S(companyId, name, site, start, end, null);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and name LIKE &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByC_LikeN_S(
		long companyId, String name, boolean site, int start, int end,
		OrderByComparator<Group> orderByComparator) {

		return findByC_LikeN_S(
			companyId, name, site, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and name LIKE &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByC_LikeN_S(
		long companyId, String name, boolean site, int start, int end,
		OrderByComparator<Group> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByC_LikeN_S.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, name, site}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and name LIKE &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByC_LikeN_S_First(
			long companyId, String name, boolean site,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		return _collectionPersistenceFinderByC_LikeN_S.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, name, site}, orderByComparator);
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and name LIKE &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByC_LikeN_S_First(
		long companyId, String name, boolean site,
		OrderByComparator<Group> orderByComparator) {

		return _collectionPersistenceFinderByC_LikeN_S.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, name, site}, orderByComparator);
	}

	/**
	 * Removes all the groups where companyId = &#63; and name LIKE &#63; and site = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param site the site
	 */
	@Override
	public void removeByC_LikeN_S(long companyId, String name, boolean site) {
		_collectionPersistenceFinderByC_LikeN_S.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, name, site});
	}

	/**
	 * Returns the number of groups where companyId = &#63; and name LIKE &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param site the site
	 * @return the number of matching groups
	 */
	@Override
	public int countByC_LikeN_S(long companyId, String name, boolean site) {
		return _collectionPersistenceFinderByC_LikeN_S.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, name, site});
	}

	private CollectionPersistenceFinder<Group, NoSuchGroupException>
		_collectionPersistenceFinderByC_S_A;

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and site = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param site the site
	 * @param active the active
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByC_S_A(
		long companyId, boolean site, boolean active, int start, int end,
		OrderByComparator<Group> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByC_S_A.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, site, active}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and site = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param site the site
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByC_S_A_First(
			long companyId, boolean site, boolean active,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		return _collectionPersistenceFinderByC_S_A.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, site, active}, orderByComparator);
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and site = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param site the site
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByC_S_A_First(
		long companyId, boolean site, boolean active,
		OrderByComparator<Group> orderByComparator) {

		return _collectionPersistenceFinderByC_S_A.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, site, active}, orderByComparator);
	}

	/**
	 * Removes all the groups where companyId = &#63; and site = &#63; and active = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param site the site
	 * @param active the active
	 */
	@Override
	public void removeByC_S_A(long companyId, boolean site, boolean active) {
		_collectionPersistenceFinderByC_S_A.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, site, active});
	}

	/**
	 * Returns the number of groups where companyId = &#63; and site = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param site the site
	 * @param active the active
	 * @return the number of matching groups
	 */
	@Override
	public int countByC_S_A(long companyId, boolean site, boolean active) {
		return _collectionPersistenceFinderByC_S_A.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, site, active});
	}

	private CollectionPersistenceFinder<Group, NoSuchGroupException>
		_collectionPersistenceFinderByGtG_C_C_P;

	/**
	 * Returns all the groups where groupId &gt; &#63; and companyId = &#63; and classNameId = &#63; and parentGroupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 * @return the matching groups
	 */
	@Override
	public List<Group> findByGtG_C_C_P(
		long groupId, long companyId, long classNameId, long parentGroupId) {

		return findByGtG_C_C_P(
			groupId, companyId, classNameId, parentGroupId, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the groups where groupId &gt; &#63; and companyId = &#63; and classNameId = &#63; and parentGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	@Override
	public List<Group> findByGtG_C_C_P(
		long groupId, long companyId, long classNameId, long parentGroupId,
		int start, int end) {

		return findByGtG_C_C_P(
			groupId, companyId, classNameId, parentGroupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the groups where groupId &gt; &#63; and companyId = &#63; and classNameId = &#63; and parentGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByGtG_C_C_P(
		long groupId, long companyId, long classNameId, long parentGroupId,
		int start, int end, OrderByComparator<Group> orderByComparator) {

		return findByGtG_C_C_P(
			groupId, companyId, classNameId, parentGroupId, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the groups where groupId &gt; &#63; and companyId = &#63; and classNameId = &#63; and parentGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByGtG_C_C_P(
		long groupId, long companyId, long classNameId, long parentGroupId,
		int start, int end, OrderByComparator<Group> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGtG_C_C_P.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, companyId, classNameId, parentGroupId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first group in the ordered set where groupId &gt; &#63; and companyId = &#63; and classNameId = &#63; and parentGroupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByGtG_C_C_P_First(
			long groupId, long companyId, long classNameId, long parentGroupId,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		return _collectionPersistenceFinderByGtG_C_C_P.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, companyId, classNameId, parentGroupId},
			orderByComparator);
	}

	/**
	 * Returns the first group in the ordered set where groupId &gt; &#63; and companyId = &#63; and classNameId = &#63; and parentGroupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByGtG_C_C_P_First(
		long groupId, long companyId, long classNameId, long parentGroupId,
		OrderByComparator<Group> orderByComparator) {

		return _collectionPersistenceFinderByGtG_C_C_P.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, companyId, classNameId, parentGroupId},
			orderByComparator);
	}

	/**
	 * Removes all the groups where groupId &gt; &#63; and companyId = &#63; and classNameId = &#63; and parentGroupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 */
	@Override
	public void removeByGtG_C_C_P(
		long groupId, long companyId, long classNameId, long parentGroupId) {

		_collectionPersistenceFinderByGtG_C_C_P.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, companyId, classNameId, parentGroupId});
	}

	/**
	 * Returns the number of groups where groupId &gt; &#63; and companyId = &#63; and classNameId = &#63; and parentGroupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param parentGroupId the parent group ID
	 * @return the number of matching groups
	 */
	@Override
	public int countByGtG_C_C_P(
		long groupId, long companyId, long classNameId, long parentGroupId) {

		return _collectionPersistenceFinderByGtG_C_C_P.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, companyId, classNameId, parentGroupId});
	}

	private CollectionPersistenceFinder<Group, NoSuchGroupException>
		_collectionPersistenceFinderByGtG_C_P_S;

	/**
	 * Returns all the groups where groupId &gt; &#63; and companyId = &#63; and parentGroupId = &#63; and site = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @return the matching groups
	 */
	@Override
	public List<Group> findByGtG_C_P_S(
		long groupId, long companyId, long parentGroupId, boolean site) {

		return findByGtG_C_P_S(
			groupId, companyId, parentGroupId, site, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the groups where groupId &gt; &#63; and companyId = &#63; and parentGroupId = &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	@Override
	public List<Group> findByGtG_C_P_S(
		long groupId, long companyId, long parentGroupId, boolean site,
		int start, int end) {

		return findByGtG_C_P_S(
			groupId, companyId, parentGroupId, site, start, end, null);
	}

	/**
	 * Returns an ordered range of all the groups where groupId &gt; &#63; and companyId = &#63; and parentGroupId = &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByGtG_C_P_S(
		long groupId, long companyId, long parentGroupId, boolean site,
		int start, int end, OrderByComparator<Group> orderByComparator) {

		return findByGtG_C_P_S(
			groupId, companyId, parentGroupId, site, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the groups where groupId &gt; &#63; and companyId = &#63; and parentGroupId = &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByGtG_C_P_S(
		long groupId, long companyId, long parentGroupId, boolean site,
		int start, int end, OrderByComparator<Group> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGtG_C_P_S.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, companyId, parentGroupId, site}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first group in the ordered set where groupId &gt; &#63; and companyId = &#63; and parentGroupId = &#63; and site = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByGtG_C_P_S_First(
			long groupId, long companyId, long parentGroupId, boolean site,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		return _collectionPersistenceFinderByGtG_C_P_S.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, companyId, parentGroupId, site},
			orderByComparator);
	}

	/**
	 * Returns the first group in the ordered set where groupId &gt; &#63; and companyId = &#63; and parentGroupId = &#63; and site = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByGtG_C_P_S_First(
		long groupId, long companyId, long parentGroupId, boolean site,
		OrderByComparator<Group> orderByComparator) {

		return _collectionPersistenceFinderByGtG_C_P_S.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, companyId, parentGroupId, site},
			orderByComparator);
	}

	/**
	 * Removes all the groups where groupId &gt; &#63; and companyId = &#63; and parentGroupId = &#63; and site = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 */
	@Override
	public void removeByGtG_C_P_S(
		long groupId, long companyId, long parentGroupId, boolean site) {

		_collectionPersistenceFinderByGtG_C_P_S.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, companyId, parentGroupId, site});
	}

	/**
	 * Returns the number of groups where groupId &gt; &#63; and companyId = &#63; and parentGroupId = &#63; and site = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @return the number of matching groups
	 */
	@Override
	public int countByGtG_C_P_S(
		long groupId, long companyId, long parentGroupId, boolean site) {

		return _collectionPersistenceFinderByGtG_C_P_S.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, companyId, parentGroupId, site});
	}

	private UniquePersistenceFinder<Group, NoSuchGroupException>
		_uniquePersistenceFinderByC_C_L_GK;

	/**
	 * Returns the group where companyId = &#63; and classNameId = &#63; and liveGroupId = &#63; and groupKey = &#63; or throws a <code>NoSuchGroupException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param liveGroupId the live group ID
	 * @param groupKey the group key
	 * @return the matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByC_C_L_GK(
			long companyId, long classNameId, long liveGroupId, String groupKey)
		throws NoSuchGroupException {

		return _uniquePersistenceFinderByC_C_L_GK.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId, liveGroupId, groupKey});
	}

	/**
	 * Returns the group where companyId = &#63; and classNameId = &#63; and liveGroupId = &#63; and groupKey = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param liveGroupId the live group ID
	 * @param groupKey the group key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByC_C_L_GK(
		long companyId, long classNameId, long liveGroupId, String groupKey,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByC_C_L_GK.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId, liveGroupId, groupKey},
			useFinderCache);
	}

	/**
	 * Removes the group where companyId = &#63; and classNameId = &#63; and liveGroupId = &#63; and groupKey = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param liveGroupId the live group ID
	 * @param groupKey the group key
	 * @return the group that was removed
	 */
	@Override
	public Group removeByC_C_L_GK(
			long companyId, long classNameId, long liveGroupId, String groupKey)
		throws NoSuchGroupException {

		Group group = findByC_C_L_GK(
			companyId, classNameId, liveGroupId, groupKey);

		return remove(group);
	}

	/**
	 * Returns the number of groups where companyId = &#63; and classNameId = &#63; and liveGroupId = &#63; and groupKey = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param liveGroupId the live group ID
	 * @param groupKey the group key
	 * @return the number of matching groups
	 */
	@Override
	public int countByC_C_L_GK(
		long companyId, long classNameId, long liveGroupId, String groupKey) {

		return _uniquePersistenceFinderByC_C_L_GK.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId, liveGroupId, groupKey});
	}

	private CollectionPersistenceFinder<Group, NoSuchGroupException>
		_collectionPersistenceFinderByC_P_LikeN_S;

	/**
	 * Returns all the groups where companyId = &#63; and parentGroupId = &#63; and name LIKE &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param name the name
	 * @param site the site
	 * @return the matching groups
	 */
	@Override
	public List<Group> findByC_P_LikeN_S(
		long companyId, long parentGroupId, String name, boolean site) {

		return findByC_P_LikeN_S(
			companyId, parentGroupId, name, site, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the groups where companyId = &#63; and parentGroupId = &#63; and name LIKE &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param name the name
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of matching groups
	 */
	@Override
	public List<Group> findByC_P_LikeN_S(
		long companyId, long parentGroupId, String name, boolean site,
		int start, int end) {

		return findByC_P_LikeN_S(
			companyId, parentGroupId, name, site, start, end, null);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and parentGroupId = &#63; and name LIKE &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param name the name
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByC_P_LikeN_S(
		long companyId, long parentGroupId, String name, boolean site,
		int start, int end, OrderByComparator<Group> orderByComparator) {

		return findByC_P_LikeN_S(
			companyId, parentGroupId, name, site, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and parentGroupId = &#63; and name LIKE &#63; and site = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param name the name
	 * @param site the site
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByC_P_LikeN_S(
		long companyId, long parentGroupId, String name, boolean site,
		int start, int end, OrderByComparator<Group> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_P_LikeN_S.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, parentGroupId, name, site}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and parentGroupId = &#63; and name LIKE &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param name the name
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByC_P_LikeN_S_First(
			long companyId, long parentGroupId, String name, boolean site,
			OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		return _collectionPersistenceFinderByC_P_LikeN_S.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, parentGroupId, name, site},
			orderByComparator);
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and parentGroupId = &#63; and name LIKE &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param name the name
	 * @param site the site
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByC_P_LikeN_S_First(
		long companyId, long parentGroupId, String name, boolean site,
		OrderByComparator<Group> orderByComparator) {

		return _collectionPersistenceFinderByC_P_LikeN_S.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, parentGroupId, name, site},
			orderByComparator);
	}

	/**
	 * Removes all the groups where companyId = &#63; and parentGroupId = &#63; and name LIKE &#63; and site = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param name the name
	 * @param site the site
	 */
	@Override
	public void removeByC_P_LikeN_S(
		long companyId, long parentGroupId, String name, boolean site) {

		_collectionPersistenceFinderByC_P_LikeN_S.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, parentGroupId, name, site});
	}

	/**
	 * Returns the number of groups where companyId = &#63; and parentGroupId = &#63; and name LIKE &#63; and site = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param name the name
	 * @param site the site
	 * @return the number of matching groups
	 */
	@Override
	public int countByC_P_LikeN_S(
		long companyId, long parentGroupId, String name, boolean site) {

		return _collectionPersistenceFinderByC_P_LikeN_S.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, parentGroupId, name, site});
	}

	private CollectionPersistenceFinder<Group, NoSuchGroupException>
		_collectionPersistenceFinderByC_P_S_I;

	/**
	 * Returns an ordered range of all the groups where companyId = &#63; and parentGroupId = &#63; and site = &#63; and inheritContent = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param inheritContent the inherit content
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching groups
	 */
	@Override
	public List<Group> findByC_P_S_I(
		long companyId, long parentGroupId, boolean site,
		boolean inheritContent, int start, int end,
		OrderByComparator<Group> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByC_P_S_I.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, parentGroupId, site, inheritContent},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and parentGroupId = &#63; and site = &#63; and inheritContent = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param inheritContent the inherit content
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByC_P_S_I_First(
			long companyId, long parentGroupId, boolean site,
			boolean inheritContent, OrderByComparator<Group> orderByComparator)
		throws NoSuchGroupException {

		return _collectionPersistenceFinderByC_P_S_I.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, parentGroupId, site, inheritContent},
			orderByComparator);
	}

	/**
	 * Returns the first group in the ordered set where companyId = &#63; and parentGroupId = &#63; and site = &#63; and inheritContent = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param inheritContent the inherit content
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByC_P_S_I_First(
		long companyId, long parentGroupId, boolean site,
		boolean inheritContent, OrderByComparator<Group> orderByComparator) {

		return _collectionPersistenceFinderByC_P_S_I.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, parentGroupId, site, inheritContent},
			orderByComparator);
	}

	/**
	 * Removes all the groups where companyId = &#63; and parentGroupId = &#63; and site = &#63; and inheritContent = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param inheritContent the inherit content
	 */
	@Override
	public void removeByC_P_S_I(
		long companyId, long parentGroupId, boolean site,
		boolean inheritContent) {

		_collectionPersistenceFinderByC_P_S_I.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, parentGroupId, site, inheritContent});
	}

	/**
	 * Returns the number of groups where companyId = &#63; and parentGroupId = &#63; and site = &#63; and inheritContent = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentGroupId the parent group ID
	 * @param site the site
	 * @param inheritContent the inherit content
	 * @return the number of matching groups
	 */
	@Override
	public int countByC_P_S_I(
		long companyId, long parentGroupId, boolean site,
		boolean inheritContent) {

		return _collectionPersistenceFinderByC_P_S_I.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, parentGroupId, site, inheritContent});
	}

	private UniquePersistenceFinder<Group, NoSuchGroupException>
		_uniquePersistenceFinderByERC_C;

	/**
	 * Returns the group where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchGroupException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching group
	 * @throws NoSuchGroupException if a matching group could not be found
	 */
	@Override
	public Group findByERC_C(String externalReferenceCode, long companyId)
		throws NoSuchGroupException {

		return _uniquePersistenceFinderByERC_C.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {externalReferenceCode, companyId});
	}

	/**
	 * Returns the group where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching group, or <code>null</code> if a matching group could not be found
	 */
	@Override
	public Group fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_C.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {externalReferenceCode, companyId}, useFinderCache);
	}

	/**
	 * Removes the group where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the group that was removed
	 */
	@Override
	public Group removeByERC_C(String externalReferenceCode, long companyId)
		throws NoSuchGroupException {

		Group group = findByERC_C(externalReferenceCode, companyId);

		return remove(group);
	}

	/**
	 * Returns the number of groups where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching groups
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		return _uniquePersistenceFinderByERC_C.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {externalReferenceCode, companyId});
	}

	public GroupPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("type", "type_");
		dbColumnNames.put("active", "active_");

		setDBColumnNames(dbColumnNames);

		setModelClass(Group.class);

		setModelImplClass(GroupImpl.class);
		setModelPKClass(long.class);

		setTable(GroupTable.INSTANCE);
	}

	/**
	 * Creates a new group with the primary key. Does not add the group to the database.
	 *
	 * @param groupId the primary key for the new group
	 * @return the new group
	 */
	@Override
	public Group create(long groupId) {
		Group group = new GroupImpl();

		group.setNew(true);
		group.setPrimaryKey(groupId);

		String uuid = PortalUUIDUtil.generate();

		group.setUuid(uuid);

		group.setCompanyId(CompanyThreadLocal.getCompanyId());

		return group;
	}

	/**
	 * Removes the group with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param groupId the primary key of the group
	 * @return the group that was removed
	 * @throws NoSuchGroupException if a group with the primary key could not be found
	 */
	@Override
	public Group remove(long groupId) throws NoSuchGroupException {
		return remove((Serializable)groupId);
	}

	@Override
	protected Group removeImpl(Group group) {
		groupToOrganizationTableMapper.deleteLeftPrimaryKeyTableMappings(
			group.getPrimaryKey());

		groupToRoleTableMapper.deleteLeftPrimaryKeyTableMappings(
			group.getPrimaryKey());

		groupToUserGroupTableMapper.deleteLeftPrimaryKeyTableMappings(
			group.getPrimaryKey());

		groupToUserTableMapper.deleteLeftPrimaryKeyTableMappings(
			group.getPrimaryKey());

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(group)) {
				group = (Group)session.get(
					GroupImpl.class, group.getPrimaryKeyObj());
			}

			if ((group != null) && CTPersistenceHelperUtil.isRemove(group)) {
				session.delete(group);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (group != null) {
			clearCache(group);
		}

		return group;
	}

	@Override
	public Group updateImpl(Group group) {
		boolean isNew = group.isNew();

		if (!(group instanceof GroupModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(group.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(group);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in group proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom Group implementation " +
					group.getClass());
		}

		GroupModelImpl groupModelImpl = (GroupModelImpl)group;

		if (Validator.isNull(group.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			group.setUuid(uuid);
		}

		if (Validator.isNull(group.getExternalReferenceCode())) {
			group.setExternalReferenceCode(group.getUuid());
		}
		else {
			if (!Objects.equals(
					groupModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					group.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = group.getCompanyId();

					long groupId = group.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = group.getPrimaryKey();
					}

					try {
						group.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								Group.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								group.getExternalReferenceCode(), null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			Group ercGroup = fetchByERC_C(
				group.getExternalReferenceCode(), group.getCompanyId());

			if (isNew) {
				if (ercGroup != null) {
					throw new DuplicateGroupExternalReferenceCodeException(
						"Duplicate group with external reference code " +
							group.getExternalReferenceCode() + " and company " +
								group.getCompanyId());
				}
			}
			else {
				if ((ercGroup != null) &&
					(group.getGroupId() != ercGroup.getGroupId())) {

					throw new DuplicateGroupExternalReferenceCodeException(
						"Duplicate group with external reference code " +
							group.getExternalReferenceCode() + " and company " +
								group.getCompanyId());
				}
			}
		}

		if (!groupModelImpl.hasSetModifiedDate()) {
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			Date date = new Date();

			if (serviceContext == null) {
				group.setModifiedDate(date);
			}
			else {
				group.setModifiedDate(serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (CTPersistenceHelperUtil.isInsert(group)) {
				if (!isNew) {
					session.evict(GroupImpl.class, group.getPrimaryKeyObj());
				}

				session.save(group);
			}
			else {
				group = (Group)session.merge(group);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(group, false);

		if (isNew) {
			group.setNew(false);
		}

		group.resetOriginalValues();

		return group;
	}

	/**
	 * Returns the group with the primary key or throws a <code>NoSuchGroupException</code> if it could not be found.
	 *
	 * @param groupId the primary key of the group
	 * @return the group
	 * @throws NoSuchGroupException if a group with the primary key could not be found
	 */
	@Override
	public Group findByPrimaryKey(long groupId) throws NoSuchGroupException {
		return findByPrimaryKey((Serializable)groupId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return CTPersistenceHelperUtil.getCTPersistenceHelper();
	}

	/**
	 * Returns the group with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param groupId the primary key of the group
	 * @return the group, or <code>null</code> if a group with the primary key could not be found
	 */
	@Override
	public Group fetchByPrimaryKey(long groupId) {
		return fetchByPrimaryKey((Serializable)groupId);
	}

	/**
	 * Returns the primaryKeys of organizations associated with the group.
	 *
	 * @param pk the primary key of the group
	 * @return long[] of the primaryKeys of organizations associated with the group
	 */
	@Override
	public long[] getOrganizationPrimaryKeys(long pk) {
		long[] pks = groupToOrganizationTableMapper.getRightPrimaryKeys(pk);

		return pks.clone();
	}

	/**
	 * Returns all the organizations associated with the group.
	 *
	 * @param pk the primary key of the group
	 * @return the organizations associated with the group
	 */
	@Override
	public List<com.liferay.portal.kernel.model.Organization> getOrganizations(
		long pk) {

		return getOrganizations(pk, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	/**
	 * Returns a range of all the organizations associated with the group.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the group
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of organizations associated with the group
	 */
	@Override
	public List<com.liferay.portal.kernel.model.Organization> getOrganizations(
		long pk, int start, int end) {

		return getOrganizations(pk, start, end, null);
	}

	/**
	 * Returns an ordered range of all the organizations associated with the group.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the group
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of organizations associated with the group
	 */
	@Override
	public List<com.liferay.portal.kernel.model.Organization> getOrganizations(
		long pk, int start, int end,
		OrderByComparator<com.liferay.portal.kernel.model.Organization>
			orderByComparator) {

		return groupToOrganizationTableMapper.getRightBaseModels(
			pk, start, end, orderByComparator);
	}

	/**
	 * Returns the number of organizations associated with the group.
	 *
	 * @param pk the primary key of the group
	 * @return the number of organizations associated with the group
	 */
	@Override
	public int getOrganizationsSize(long pk) {
		long[] pks = groupToOrganizationTableMapper.getRightPrimaryKeys(pk);

		return pks.length;
	}

	/**
	 * Returns <code>true</code> if the organization is associated with the group.
	 *
	 * @param pk the primary key of the group
	 * @param organizationPK the primary key of the organization
	 * @return <code>true</code> if the organization is associated with the group; <code>false</code> otherwise
	 */
	@Override
	public boolean containsOrganization(long pk, long organizationPK) {
		return groupToOrganizationTableMapper.containsTableMapping(
			pk, organizationPK);
	}

	/**
	 * Returns <code>true</code> if the group has any organizations associated with it.
	 *
	 * @param pk the primary key of the group to check for associations with organizations
	 * @return <code>true</code> if the group has any organizations associated with it; <code>false</code> otherwise
	 */
	@Override
	public boolean containsOrganizations(long pk) {
		if (getOrganizationsSize(pk) > 0) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Adds an association between the group and the organization. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param organizationPK the primary key of the organization
	 * @return <code>true</code> if an association between the group and the organization was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addOrganization(long pk, long organizationPK) {
		Group group = fetchByPrimaryKey(pk);

		if (group == null) {
			return groupToOrganizationTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk, organizationPK);
		}
		else {
			return groupToOrganizationTableMapper.addTableMapping(
				group.getCompanyId(), pk, organizationPK);
		}
	}

	/**
	 * Adds an association between the group and the organization. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param organization the organization
	 * @return <code>true</code> if an association between the group and the organization was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addOrganization(
		long pk, com.liferay.portal.kernel.model.Organization organization) {

		Group group = fetchByPrimaryKey(pk);

		if (group == null) {
			return groupToOrganizationTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk,
				organization.getPrimaryKey());
		}
		else {
			return groupToOrganizationTableMapper.addTableMapping(
				group.getCompanyId(), pk, organization.getPrimaryKey());
		}
	}

	/**
	 * Adds an association between the group and the organizations. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param organizationPKs the primary keys of the organizations
	 * @return <code>true</code> if at least one association between the group and the organizations was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addOrganizations(long pk, long[] organizationPKs) {
		long companyId = 0;

		Group group = fetchByPrimaryKey(pk);

		if (group == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = group.getCompanyId();
		}

		long[] addedKeys = groupToOrganizationTableMapper.addTableMappings(
			companyId, pk, organizationPKs);

		if (addedKeys.length > 0) {
			return true;
		}

		return false;
	}

	/**
	 * Adds an association between the group and the organizations. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param organizations the organizations
	 * @return <code>true</code> if at least one association between the group and the organizations was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addOrganizations(
		long pk,
		List<com.liferay.portal.kernel.model.Organization> organizations) {

		return addOrganizations(
			pk,
			ListUtil.toLongArray(
				organizations,
				com.liferay.portal.kernel.model.Organization.
					ORGANIZATION_ID_ACCESSOR));
	}

	/**
	 * Clears all associations between the group and its organizations. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group to clear the associated organizations from
	 */
	@Override
	public void clearOrganizations(long pk) {
		groupToOrganizationTableMapper.deleteLeftPrimaryKeyTableMappings(pk);
	}

	/**
	 * Removes the association between the group and the organization. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param organizationPK the primary key of the organization
	 */
	@Override
	public void removeOrganization(long pk, long organizationPK) {
		groupToOrganizationTableMapper.deleteTableMapping(pk, organizationPK);
	}

	/**
	 * Removes the association between the group and the organization. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param organization the organization
	 */
	@Override
	public void removeOrganization(
		long pk, com.liferay.portal.kernel.model.Organization organization) {

		groupToOrganizationTableMapper.deleteTableMapping(
			pk, organization.getPrimaryKey());
	}

	/**
	 * Removes the association between the group and the organizations. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param organizationPKs the primary keys of the organizations
	 */
	@Override
	public void removeOrganizations(long pk, long[] organizationPKs) {
		groupToOrganizationTableMapper.deleteTableMappings(pk, organizationPKs);
	}

	/**
	 * Removes the association between the group and the organizations. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param organizations the organizations
	 */
	@Override
	public void removeOrganizations(
		long pk,
		List<com.liferay.portal.kernel.model.Organization> organizations) {

		removeOrganizations(
			pk,
			ListUtil.toLongArray(
				organizations,
				com.liferay.portal.kernel.model.Organization.
					ORGANIZATION_ID_ACCESSOR));
	}

	/**
	 * Sets the organizations associated with the group, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param organizationPKs the primary keys of the organizations to be associated with the group
	 */
	@Override
	public void setOrganizations(long pk, long[] organizationPKs) {
		Set<Long> newOrganizationPKsSet = SetUtil.fromArray(organizationPKs);
		Set<Long> oldOrganizationPKsSet = SetUtil.fromArray(
			groupToOrganizationTableMapper.getRightPrimaryKeys(pk));

		Set<Long> removeOrganizationPKsSet = new HashSet<Long>(
			oldOrganizationPKsSet);

		removeOrganizationPKsSet.removeAll(newOrganizationPKsSet);

		groupToOrganizationTableMapper.deleteTableMappings(
			pk, ArrayUtil.toLongArray(removeOrganizationPKsSet));

		newOrganizationPKsSet.removeAll(oldOrganizationPKsSet);

		long companyId = 0;

		Group group = fetchByPrimaryKey(pk);

		if (group == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = group.getCompanyId();
		}

		groupToOrganizationTableMapper.addTableMappings(
			companyId, pk, ArrayUtil.toLongArray(newOrganizationPKsSet));
	}

	/**
	 * Sets the organizations associated with the group, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param organizations the organizations to be associated with the group
	 */
	@Override
	public void setOrganizations(
		long pk,
		List<com.liferay.portal.kernel.model.Organization> organizations) {

		try {
			long[] organizationPKs = new long[organizations.size()];

			for (int i = 0; i < organizations.size(); i++) {
				com.liferay.portal.kernel.model.Organization organization =
					organizations.get(i);

				organizationPKs[i] = organization.getPrimaryKey();
			}

			setOrganizations(pk, organizationPKs);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
	}

	/**
	 * Returns the primaryKeys of roles associated with the group.
	 *
	 * @param pk the primary key of the group
	 * @return long[] of the primaryKeys of roles associated with the group
	 */
	@Override
	public long[] getRolePrimaryKeys(long pk) {
		long[] pks = groupToRoleTableMapper.getRightPrimaryKeys(pk);

		return pks.clone();
	}

	/**
	 * Returns all the roles associated with the group.
	 *
	 * @param pk the primary key of the group
	 * @return the roles associated with the group
	 */
	@Override
	public List<com.liferay.portal.kernel.model.Role> getRoles(long pk) {
		return getRoles(pk, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	/**
	 * Returns a range of all the roles associated with the group.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the group
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of roles associated with the group
	 */
	@Override
	public List<com.liferay.portal.kernel.model.Role> getRoles(
		long pk, int start, int end) {

		return getRoles(pk, start, end, null);
	}

	/**
	 * Returns an ordered range of all the roles associated with the group.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the group
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of roles associated with the group
	 */
	@Override
	public List<com.liferay.portal.kernel.model.Role> getRoles(
		long pk, int start, int end,
		OrderByComparator<com.liferay.portal.kernel.model.Role>
			orderByComparator) {

		return groupToRoleTableMapper.getRightBaseModels(
			pk, start, end, orderByComparator);
	}

	/**
	 * Returns the number of roles associated with the group.
	 *
	 * @param pk the primary key of the group
	 * @return the number of roles associated with the group
	 */
	@Override
	public int getRolesSize(long pk) {
		long[] pks = groupToRoleTableMapper.getRightPrimaryKeys(pk);

		return pks.length;
	}

	/**
	 * Returns <code>true</code> if the role is associated with the group.
	 *
	 * @param pk the primary key of the group
	 * @param rolePK the primary key of the role
	 * @return <code>true</code> if the role is associated with the group; <code>false</code> otherwise
	 */
	@Override
	public boolean containsRole(long pk, long rolePK) {
		return groupToRoleTableMapper.containsTableMapping(pk, rolePK);
	}

	/**
	 * Returns <code>true</code> if the group has any roles associated with it.
	 *
	 * @param pk the primary key of the group to check for associations with roles
	 * @return <code>true</code> if the group has any roles associated with it; <code>false</code> otherwise
	 */
	@Override
	public boolean containsRoles(long pk) {
		if (getRolesSize(pk) > 0) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Adds an association between the group and the role. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param rolePK the primary key of the role
	 * @return <code>true</code> if an association between the group and the role was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addRole(long pk, long rolePK) {
		Group group = fetchByPrimaryKey(pk);

		if (group == null) {
			return groupToRoleTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk, rolePK);
		}
		else {
			return groupToRoleTableMapper.addTableMapping(
				group.getCompanyId(), pk, rolePK);
		}
	}

	/**
	 * Adds an association between the group and the role. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param role the role
	 * @return <code>true</code> if an association between the group and the role was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addRole(long pk, com.liferay.portal.kernel.model.Role role) {
		Group group = fetchByPrimaryKey(pk);

		if (group == null) {
			return groupToRoleTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk, role.getPrimaryKey());
		}
		else {
			return groupToRoleTableMapper.addTableMapping(
				group.getCompanyId(), pk, role.getPrimaryKey());
		}
	}

	/**
	 * Adds an association between the group and the roles. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param rolePKs the primary keys of the roles
	 * @return <code>true</code> if at least one association between the group and the roles was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addRoles(long pk, long[] rolePKs) {
		long companyId = 0;

		Group group = fetchByPrimaryKey(pk);

		if (group == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = group.getCompanyId();
		}

		long[] addedKeys = groupToRoleTableMapper.addTableMappings(
			companyId, pk, rolePKs);

		if (addedKeys.length > 0) {
			return true;
		}

		return false;
	}

	/**
	 * Adds an association between the group and the roles. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param roles the roles
	 * @return <code>true</code> if at least one association between the group and the roles was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addRoles(
		long pk, List<com.liferay.portal.kernel.model.Role> roles) {

		return addRoles(
			pk,
			ListUtil.toLongArray(
				roles, com.liferay.portal.kernel.model.Role.ROLE_ID_ACCESSOR));
	}

	/**
	 * Clears all associations between the group and its roles. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group to clear the associated roles from
	 */
	@Override
	public void clearRoles(long pk) {
		groupToRoleTableMapper.deleteLeftPrimaryKeyTableMappings(pk);
	}

	/**
	 * Removes the association between the group and the role. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param rolePK the primary key of the role
	 */
	@Override
	public void removeRole(long pk, long rolePK) {
		groupToRoleTableMapper.deleteTableMapping(pk, rolePK);
	}

	/**
	 * Removes the association between the group and the role. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param role the role
	 */
	@Override
	public void removeRole(long pk, com.liferay.portal.kernel.model.Role role) {
		groupToRoleTableMapper.deleteTableMapping(pk, role.getPrimaryKey());
	}

	/**
	 * Removes the association between the group and the roles. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param rolePKs the primary keys of the roles
	 */
	@Override
	public void removeRoles(long pk, long[] rolePKs) {
		groupToRoleTableMapper.deleteTableMappings(pk, rolePKs);
	}

	/**
	 * Removes the association between the group and the roles. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param roles the roles
	 */
	@Override
	public void removeRoles(
		long pk, List<com.liferay.portal.kernel.model.Role> roles) {

		removeRoles(
			pk,
			ListUtil.toLongArray(
				roles, com.liferay.portal.kernel.model.Role.ROLE_ID_ACCESSOR));
	}

	/**
	 * Sets the roles associated with the group, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param rolePKs the primary keys of the roles to be associated with the group
	 */
	@Override
	public void setRoles(long pk, long[] rolePKs) {
		Set<Long> newRolePKsSet = SetUtil.fromArray(rolePKs);
		Set<Long> oldRolePKsSet = SetUtil.fromArray(
			groupToRoleTableMapper.getRightPrimaryKeys(pk));

		Set<Long> removeRolePKsSet = new HashSet<Long>(oldRolePKsSet);

		removeRolePKsSet.removeAll(newRolePKsSet);

		groupToRoleTableMapper.deleteTableMappings(
			pk, ArrayUtil.toLongArray(removeRolePKsSet));

		newRolePKsSet.removeAll(oldRolePKsSet);

		long companyId = 0;

		Group group = fetchByPrimaryKey(pk);

		if (group == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = group.getCompanyId();
		}

		groupToRoleTableMapper.addTableMappings(
			companyId, pk, ArrayUtil.toLongArray(newRolePKsSet));
	}

	/**
	 * Sets the roles associated with the group, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param roles the roles to be associated with the group
	 */
	@Override
	public void setRoles(
		long pk, List<com.liferay.portal.kernel.model.Role> roles) {

		try {
			long[] rolePKs = new long[roles.size()];

			for (int i = 0; i < roles.size(); i++) {
				com.liferay.portal.kernel.model.Role role = roles.get(i);

				rolePKs[i] = role.getPrimaryKey();
			}

			setRoles(pk, rolePKs);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
	}

	/**
	 * Returns the primaryKeys of user groups associated with the group.
	 *
	 * @param pk the primary key of the group
	 * @return long[] of the primaryKeys of user groups associated with the group
	 */
	@Override
	public long[] getUserGroupPrimaryKeys(long pk) {
		long[] pks = groupToUserGroupTableMapper.getRightPrimaryKeys(pk);

		return pks.clone();
	}

	/**
	 * Returns all the user groups associated with the group.
	 *
	 * @param pk the primary key of the group
	 * @return the user groups associated with the group
	 */
	@Override
	public List<com.liferay.portal.kernel.model.UserGroup> getUserGroups(
		long pk) {

		return getUserGroups(pk, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	/**
	 * Returns a range of all the user groups associated with the group.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the group
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of user groups associated with the group
	 */
	@Override
	public List<com.liferay.portal.kernel.model.UserGroup> getUserGroups(
		long pk, int start, int end) {

		return getUserGroups(pk, start, end, null);
	}

	/**
	 * Returns an ordered range of all the user groups associated with the group.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the group
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of user groups associated with the group
	 */
	@Override
	public List<com.liferay.portal.kernel.model.UserGroup> getUserGroups(
		long pk, int start, int end,
		OrderByComparator<com.liferay.portal.kernel.model.UserGroup>
			orderByComparator) {

		return groupToUserGroupTableMapper.getRightBaseModels(
			pk, start, end, orderByComparator);
	}

	/**
	 * Returns the number of user groups associated with the group.
	 *
	 * @param pk the primary key of the group
	 * @return the number of user groups associated with the group
	 */
	@Override
	public int getUserGroupsSize(long pk) {
		long[] pks = groupToUserGroupTableMapper.getRightPrimaryKeys(pk);

		return pks.length;
	}

	/**
	 * Returns <code>true</code> if the user group is associated with the group.
	 *
	 * @param pk the primary key of the group
	 * @param userGroupPK the primary key of the user group
	 * @return <code>true</code> if the user group is associated with the group; <code>false</code> otherwise
	 */
	@Override
	public boolean containsUserGroup(long pk, long userGroupPK) {
		return groupToUserGroupTableMapper.containsTableMapping(
			pk, userGroupPK);
	}

	/**
	 * Returns <code>true</code> if the group has any user groups associated with it.
	 *
	 * @param pk the primary key of the group to check for associations with user groups
	 * @return <code>true</code> if the group has any user groups associated with it; <code>false</code> otherwise
	 */
	@Override
	public boolean containsUserGroups(long pk) {
		if (getUserGroupsSize(pk) > 0) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Adds an association between the group and the user group. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param userGroupPK the primary key of the user group
	 * @return <code>true</code> if an association between the group and the user group was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addUserGroup(long pk, long userGroupPK) {
		Group group = fetchByPrimaryKey(pk);

		if (group == null) {
			return groupToUserGroupTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk, userGroupPK);
		}
		else {
			return groupToUserGroupTableMapper.addTableMapping(
				group.getCompanyId(), pk, userGroupPK);
		}
	}

	/**
	 * Adds an association between the group and the user group. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param userGroup the user group
	 * @return <code>true</code> if an association between the group and the user group was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addUserGroup(
		long pk, com.liferay.portal.kernel.model.UserGroup userGroup) {

		Group group = fetchByPrimaryKey(pk);

		if (group == null) {
			return groupToUserGroupTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk,
				userGroup.getPrimaryKey());
		}
		else {
			return groupToUserGroupTableMapper.addTableMapping(
				group.getCompanyId(), pk, userGroup.getPrimaryKey());
		}
	}

	/**
	 * Adds an association between the group and the user groups. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param userGroupPKs the primary keys of the user groups
	 * @return <code>true</code> if at least one association between the group and the user groups was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addUserGroups(long pk, long[] userGroupPKs) {
		long companyId = 0;

		Group group = fetchByPrimaryKey(pk);

		if (group == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = group.getCompanyId();
		}

		long[] addedKeys = groupToUserGroupTableMapper.addTableMappings(
			companyId, pk, userGroupPKs);

		if (addedKeys.length > 0) {
			return true;
		}

		return false;
	}

	/**
	 * Adds an association between the group and the user groups. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param userGroups the user groups
	 * @return <code>true</code> if at least one association between the group and the user groups was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addUserGroups(
		long pk, List<com.liferay.portal.kernel.model.UserGroup> userGroups) {

		return addUserGroups(
			pk,
			ListUtil.toLongArray(
				userGroups,
				com.liferay.portal.kernel.model.UserGroup.
					USER_GROUP_ID_ACCESSOR));
	}

	/**
	 * Clears all associations between the group and its user groups. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group to clear the associated user groups from
	 */
	@Override
	public void clearUserGroups(long pk) {
		groupToUserGroupTableMapper.deleteLeftPrimaryKeyTableMappings(pk);
	}

	/**
	 * Removes the association between the group and the user group. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param userGroupPK the primary key of the user group
	 */
	@Override
	public void removeUserGroup(long pk, long userGroupPK) {
		groupToUserGroupTableMapper.deleteTableMapping(pk, userGroupPK);
	}

	/**
	 * Removes the association between the group and the user group. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param userGroup the user group
	 */
	@Override
	public void removeUserGroup(
		long pk, com.liferay.portal.kernel.model.UserGroup userGroup) {

		groupToUserGroupTableMapper.deleteTableMapping(
			pk, userGroup.getPrimaryKey());
	}

	/**
	 * Removes the association between the group and the user groups. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param userGroupPKs the primary keys of the user groups
	 */
	@Override
	public void removeUserGroups(long pk, long[] userGroupPKs) {
		groupToUserGroupTableMapper.deleteTableMappings(pk, userGroupPKs);
	}

	/**
	 * Removes the association between the group and the user groups. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param userGroups the user groups
	 */
	@Override
	public void removeUserGroups(
		long pk, List<com.liferay.portal.kernel.model.UserGroup> userGroups) {

		removeUserGroups(
			pk,
			ListUtil.toLongArray(
				userGroups,
				com.liferay.portal.kernel.model.UserGroup.
					USER_GROUP_ID_ACCESSOR));
	}

	/**
	 * Sets the user groups associated with the group, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param userGroupPKs the primary keys of the user groups to be associated with the group
	 */
	@Override
	public void setUserGroups(long pk, long[] userGroupPKs) {
		Set<Long> newUserGroupPKsSet = SetUtil.fromArray(userGroupPKs);
		Set<Long> oldUserGroupPKsSet = SetUtil.fromArray(
			groupToUserGroupTableMapper.getRightPrimaryKeys(pk));

		Set<Long> removeUserGroupPKsSet = new HashSet<Long>(oldUserGroupPKsSet);

		removeUserGroupPKsSet.removeAll(newUserGroupPKsSet);

		groupToUserGroupTableMapper.deleteTableMappings(
			pk, ArrayUtil.toLongArray(removeUserGroupPKsSet));

		newUserGroupPKsSet.removeAll(oldUserGroupPKsSet);

		long companyId = 0;

		Group group = fetchByPrimaryKey(pk);

		if (group == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = group.getCompanyId();
		}

		groupToUserGroupTableMapper.addTableMappings(
			companyId, pk, ArrayUtil.toLongArray(newUserGroupPKsSet));
	}

	/**
	 * Sets the user groups associated with the group, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param userGroups the user groups to be associated with the group
	 */
	@Override
	public void setUserGroups(
		long pk, List<com.liferay.portal.kernel.model.UserGroup> userGroups) {

		try {
			long[] userGroupPKs = new long[userGroups.size()];

			for (int i = 0; i < userGroups.size(); i++) {
				com.liferay.portal.kernel.model.UserGroup userGroup =
					userGroups.get(i);

				userGroupPKs[i] = userGroup.getPrimaryKey();
			}

			setUserGroups(pk, userGroupPKs);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
	}

	/**
	 * Returns the primaryKeys of users associated with the group.
	 *
	 * @param pk the primary key of the group
	 * @return long[] of the primaryKeys of users associated with the group
	 */
	@Override
	public long[] getUserPrimaryKeys(long pk) {
		long[] pks = groupToUserTableMapper.getRightPrimaryKeys(pk);

		return pks.clone();
	}

	/**
	 * Returns all the users associated with the group.
	 *
	 * @param pk the primary key of the group
	 * @return the users associated with the group
	 */
	@Override
	public List<com.liferay.portal.kernel.model.User> getUsers(long pk) {
		return getUsers(pk, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	/**
	 * Returns a range of all the users associated with the group.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the group
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @return the range of users associated with the group
	 */
	@Override
	public List<com.liferay.portal.kernel.model.User> getUsers(
		long pk, int start, int end) {

		return getUsers(pk, start, end, null);
	}

	/**
	 * Returns an ordered range of all the users associated with the group.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>GroupModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the group
	 * @param start the lower bound of the range of groups
	 * @param end the upper bound of the range of groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of users associated with the group
	 */
	@Override
	public List<com.liferay.portal.kernel.model.User> getUsers(
		long pk, int start, int end,
		OrderByComparator<com.liferay.portal.kernel.model.User>
			orderByComparator) {

		return groupToUserTableMapper.getRightBaseModels(
			pk, start, end, orderByComparator);
	}

	/**
	 * Returns the number of users associated with the group.
	 *
	 * @param pk the primary key of the group
	 * @return the number of users associated with the group
	 */
	@Override
	public int getUsersSize(long pk) {
		long[] pks = groupToUserTableMapper.getRightPrimaryKeys(pk);

		return pks.length;
	}

	/**
	 * Returns <code>true</code> if the user is associated with the group.
	 *
	 * @param pk the primary key of the group
	 * @param userPK the primary key of the user
	 * @return <code>true</code> if the user is associated with the group; <code>false</code> otherwise
	 */
	@Override
	public boolean containsUser(long pk, long userPK) {
		return groupToUserTableMapper.containsTableMapping(pk, userPK);
	}

	/**
	 * Returns <code>true</code> if the group has any users associated with it.
	 *
	 * @param pk the primary key of the group to check for associations with users
	 * @return <code>true</code> if the group has any users associated with it; <code>false</code> otherwise
	 */
	@Override
	public boolean containsUsers(long pk) {
		if (getUsersSize(pk) > 0) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Adds an association between the group and the user. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param userPK the primary key of the user
	 * @return <code>true</code> if an association between the group and the user was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addUser(long pk, long userPK) {
		Group group = fetchByPrimaryKey(pk);

		if (group == null) {
			return groupToUserTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk, userPK);
		}
		else {
			return groupToUserTableMapper.addTableMapping(
				group.getCompanyId(), pk, userPK);
		}
	}

	/**
	 * Adds an association between the group and the user. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param user the user
	 * @return <code>true</code> if an association between the group and the user was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addUser(long pk, com.liferay.portal.kernel.model.User user) {
		Group group = fetchByPrimaryKey(pk);

		if (group == null) {
			return groupToUserTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk, user.getPrimaryKey());
		}
		else {
			return groupToUserTableMapper.addTableMapping(
				group.getCompanyId(), pk, user.getPrimaryKey());
		}
	}

	/**
	 * Adds an association between the group and the users. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param userPKs the primary keys of the users
	 * @return <code>true</code> if at least one association between the group and the users was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addUsers(long pk, long[] userPKs) {
		long companyId = 0;

		Group group = fetchByPrimaryKey(pk);

		if (group == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = group.getCompanyId();
		}

		long[] addedKeys = groupToUserTableMapper.addTableMappings(
			companyId, pk, userPKs);

		if (addedKeys.length > 0) {
			return true;
		}

		return false;
	}

	/**
	 * Adds an association between the group and the users. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param users the users
	 * @return <code>true</code> if at least one association between the group and the users was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addUsers(
		long pk, List<com.liferay.portal.kernel.model.User> users) {

		return addUsers(
			pk,
			ListUtil.toLongArray(
				users, com.liferay.portal.kernel.model.User.USER_ID_ACCESSOR));
	}

	/**
	 * Clears all associations between the group and its users. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group to clear the associated users from
	 */
	@Override
	public void clearUsers(long pk) {
		groupToUserTableMapper.deleteLeftPrimaryKeyTableMappings(pk);
	}

	/**
	 * Removes the association between the group and the user. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param userPK the primary key of the user
	 */
	@Override
	public void removeUser(long pk, long userPK) {
		groupToUserTableMapper.deleteTableMapping(pk, userPK);
	}

	/**
	 * Removes the association between the group and the user. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param user the user
	 */
	@Override
	public void removeUser(long pk, com.liferay.portal.kernel.model.User user) {
		groupToUserTableMapper.deleteTableMapping(pk, user.getPrimaryKey());
	}

	/**
	 * Removes the association between the group and the users. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param userPKs the primary keys of the users
	 */
	@Override
	public void removeUsers(long pk, long[] userPKs) {
		groupToUserTableMapper.deleteTableMappings(pk, userPKs);
	}

	/**
	 * Removes the association between the group and the users. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param users the users
	 */
	@Override
	public void removeUsers(
		long pk, List<com.liferay.portal.kernel.model.User> users) {

		removeUsers(
			pk,
			ListUtil.toLongArray(
				users, com.liferay.portal.kernel.model.User.USER_ID_ACCESSOR));
	}

	/**
	 * Sets the users associated with the group, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param userPKs the primary keys of the users to be associated with the group
	 */
	@Override
	public void setUsers(long pk, long[] userPKs) {
		Set<Long> newUserPKsSet = SetUtil.fromArray(userPKs);
		Set<Long> oldUserPKsSet = SetUtil.fromArray(
			groupToUserTableMapper.getRightPrimaryKeys(pk));

		Set<Long> removeUserPKsSet = new HashSet<Long>(oldUserPKsSet);

		removeUserPKsSet.removeAll(newUserPKsSet);

		groupToUserTableMapper.deleteTableMappings(
			pk, ArrayUtil.toLongArray(removeUserPKsSet));

		newUserPKsSet.removeAll(oldUserPKsSet);

		long companyId = 0;

		Group group = fetchByPrimaryKey(pk);

		if (group == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = group.getCompanyId();
		}

		groupToUserTableMapper.addTableMappings(
			companyId, pk, ArrayUtil.toLongArray(newUserPKsSet));
	}

	/**
	 * Sets the users associated with the group, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the group
	 * @param users the users to be associated with the group
	 */
	@Override
	public void setUsers(
		long pk, List<com.liferay.portal.kernel.model.User> users) {

		try {
			long[] userPKs = new long[users.size()];

			for (int i = 0; i < users.size(); i++) {
				com.liferay.portal.kernel.model.User user = users.get(i);

				userPKs[i] = user.getPrimaryKey();
			}

			setUsers(pk, userPKs);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
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
		return "groupId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_GROUP_;
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
		return GroupModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "Group_";
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
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("creatorUserId");
		ctIgnoreColumnNames.add("modifiedDate");
		ctStrictColumnNames.add("classNameId");
		ctStrictColumnNames.add("classPK");
		ctMergeColumnNames.add("parentGroupId");
		ctMergeColumnNames.add("liveGroupId");
		ctMergeColumnNames.add("treePath");
		ctMergeColumnNames.add("groupKey");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("description");
		ctMergeColumnNames.add("type_");
		ctMergeColumnNames.add("typeSettings");
		ctMergeColumnNames.add("manualMembership");
		ctMergeColumnNames.add("membershipRestriction");
		ctMergeColumnNames.add("friendlyURL");
		ctMergeColumnNames.add("site");
		ctMergeColumnNames.add("remoteStagingGroupCount");
		ctMergeColumnNames.add("inheritContent");
		ctMergeColumnNames.add("active_");
		ctMergeColumnNames.add("orgs");
		ctMergeColumnNames.add("roles");
		ctMergeColumnNames.add("userGroups");
		ctMergeColumnNames.add("users");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("groupId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_mappingTableNames.add("Groups_Orgs");
		_mappingTableNames.add("Groups_Roles");
		_mappingTableNames.add("Groups_UserGroups");
		_mappingTableNames.add("Users_Groups");

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(new String[] {"companyId", "groupKey"});

		_uniqueIndexColumnNames.add(new String[] {"companyId", "friendlyURL"});

		_uniqueIndexColumnNames.add(
			new String[] {"companyId", "classNameId", "classPK"});

		_uniqueIndexColumnNames.add(
			new String[] {"companyId", "liveGroupId", "groupKey"});

		_uniqueIndexColumnNames.add(
			new String[] {
				"companyId", "classNameId", "liveGroupId", "groupKey"
			});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "companyId"});
	}

	/**
	 * Initializes the group persistence.
	 */
	public void afterPropertiesSet() {
		groupToOrganizationTableMapper = TableMapperFactory.getTableMapper(
			"Groups_Orgs", "companyId", "groupId", "organizationId", this,
			organizationPersistence);

		groupToRoleTableMapper = TableMapperFactory.getTableMapper(
			"Groups_Roles", "companyId", "groupId", "roleId", this,
			rolePersistence);

		groupToUserGroupTableMapper = TableMapperFactory.getTableMapper(
			"Groups_UserGroups", "companyId", "groupId", "userGroupId", this,
			userGroupPersistence);

		groupToUserTableMapper = TableMapperFactory.getTableMapper(
			"Users_Groups", "companyId", "groupId", "userId", this,
			userPersistence);

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
			_SQL_SELECT_GROUP__WHERE, _SQL_COUNT_GROUP__WHERE,
			GroupModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"group_.", "uuid", "uuid_", FinderColumn.Type.STRING, "=", true,
				true, Group::getUuid));

		_uniquePersistenceFinderByUUID_G = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"uuid_", "groupId"}, 0, 1, false,
				convertNullFunction(Group::getUuid), Group::getGroupId),
			_SQL_SELECT_GROUP__WHERE, "",
			new FinderColumn<>(
				"group_.", "uuid", "uuid_", FinderColumn.Type.STRING, "=", true,
				true, Group::getUuid),
			new FinderColumn<>(
				"group_.", "groupId", FinderColumn.Type.LONG, "=", true, true,
				Group::getGroupId));

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
				_SQL_SELECT_GROUP__WHERE, _SQL_COUNT_GROUP__WHERE,
				GroupModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"group_.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
					true, true, Group::getUuid),
				new FinderColumn<>(
					"group_.", "companyId", FinderColumn.Type.LONG, "=", true,
					true, Group::getCompanyId));

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
				_SQL_SELECT_GROUP__WHERE, _SQL_COUNT_GROUP__WHERE,
				GroupModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"group_.", "companyId", FinderColumn.Type.LONG, "=", true,
					true, Group::getCompanyId));

		_collectionPersistenceFinderByLiveGroupId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByLiveGroupId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"liveGroupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByLiveGroupId", new String[] {Long.class.getName()},
					new String[] {"liveGroupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByLiveGroupId", new String[] {Long.class.getName()},
					new String[] {"liveGroupId"}, false),
				_SQL_SELECT_GROUP__WHERE, _SQL_COUNT_GROUP__WHERE,
				GroupModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"group_.", "liveGroupId", FinderColumn.Type.LONG, "=", true,
					true, Group::getLiveGroupId));

		_collectionPersistenceFinderByC_C = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"companyId", "classNameId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"companyId", "classNameId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"companyId", "classNameId"}, false),
			_SQL_SELECT_GROUP__WHERE, _SQL_COUNT_GROUP__WHERE,
			GroupModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"group_.", "companyId", FinderColumn.Type.LONG, "=", true, true,
				Group::getCompanyId),
			new FinderColumn<>(
				"group_.", "classNameId", FinderColumn.Type.LONG, "=", true,
				true, Group::getClassNameId));

		_collectionPersistenceFinderByC_P = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_P",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"companyId", "parentGroupId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_P",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"companyId", "parentGroupId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_P",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"companyId", "parentGroupId"}, false),
			_SQL_SELECT_GROUP__WHERE, _SQL_COUNT_GROUP__WHERE,
			GroupModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"group_.", "companyId", FinderColumn.Type.LONG, "=", true, true,
				Group::getCompanyId),
			new FinderColumn<>(
				"group_.", "parentGroupId", FinderColumn.Type.LONG, "=", true,
				true, Group::getParentGroupId));

		_collectionPersistenceFinderByC_GK = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_GK",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"companyId", "groupKey"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_GK",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "groupKey"}, 0, 2, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_GK",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "groupKey"}, 0, 2, false, null),
			_SQL_SELECT_GROUP__WHERE, _SQL_COUNT_GROUP__WHERE,
			GroupModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"group_.", "companyId", FinderColumn.Type.LONG, "=", true, true,
				Group::getCompanyId),
			new ArrayableFinderColumn<>(
				"group_.", "groupKey", FinderColumn.Type.STRING, "=", false,
				true, true, Group::getGroupKey));

		_uniquePersistenceFinderByC_GK = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_GK",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "groupKey"}, 0, 2, false,
				Group::getCompanyId, convertNullFunction(Group::getGroupKey)),
			_SQL_SELECT_GROUP__WHERE, "",
			new FinderColumn<>(
				"group_.", "companyId", FinderColumn.Type.LONG, "=", true, true,
				Group::getCompanyId),
			new FinderColumn<>(
				"group_.", "groupKey", FinderColumn.Type.STRING, "=", true,
				true, Group::getGroupKey));

		_uniquePersistenceFinderByC_F = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_F",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "friendlyURL"}, 0, 2, false,
				Group::getCompanyId,
				convertNullFunction(Group::getFriendlyURL)),
			_SQL_SELECT_GROUP__WHERE, "",
			new FinderColumn<>(
				"group_.", "companyId", FinderColumn.Type.LONG, "=", true, true,
				Group::getCompanyId),
			new FinderColumn<>(
				"group_.", "friendlyURL", FinderColumn.Type.STRING, "=", true,
				true, Group::getFriendlyURL));

		_collectionPersistenceFinderByC_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_S",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"companyId", "site"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_S",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"companyId", "site"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_S",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"companyId", "site"}, false),
			_SQL_SELECT_GROUP__WHERE, _SQL_COUNT_GROUP__WHERE,
			GroupModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"group_.", "companyId", FinderColumn.Type.LONG, "=", true, true,
				Group::getCompanyId),
			new FinderColumn<>(
				"group_.", "site", FinderColumn.Type.BOOLEAN, "=", true, true,
				Group::isSite));

		_collectionPersistenceFinderByC_A = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_A",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"companyId", "active_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_A",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"companyId", "active_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_A",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"companyId", "active_"}, false),
			_SQL_SELECT_GROUP__WHERE, _SQL_COUNT_GROUP__WHERE,
			GroupModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"group_.", "companyId", FinderColumn.Type.LONG, "=", true, true,
				Group::getCompanyId),
			new FinderColumn<>(
				"group_.", "active", "active_", FinderColumn.Type.BOOLEAN, "=",
				true, true, Group::isActive));

		_collectionPersistenceFinderByC_CPK = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_CPK",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"classNameId", "classPK"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_CPK",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"classNameId", "classPK"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_CPK",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"classNameId", "classPK"}, false),
			_SQL_SELECT_GROUP__WHERE, _SQL_COUNT_GROUP__WHERE,
			GroupModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"group_.", "classNameId", FinderColumn.Type.LONG, "=", true,
				true, Group::getClassNameId),
			new FinderColumn<>(
				"group_.", "classPK", FinderColumn.Type.LONG, "=", true, true,
				Group::getClassPK));

		_collectionPersistenceFinderByT_A = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByT_A",
				new String[] {
					Integer.class.getName(), Boolean.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"type_", "active_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByT_A",
				new String[] {Integer.class.getName(), Boolean.class.getName()},
				new String[] {"type_", "active_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByT_A",
				new String[] {Integer.class.getName(), Boolean.class.getName()},
				new String[] {"type_", "active_"}, false),
			_SQL_SELECT_GROUP__WHERE, _SQL_COUNT_GROUP__WHERE,
			GroupModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"group_.", "type", "type_", FinderColumn.Type.INTEGER, "=",
				true, true, Group::getType),
			new FinderColumn<>(
				"group_.", "active", "active_", FinderColumn.Type.BOOLEAN, "=",
				true, true, Group::isActive));

		_collectionPersistenceFinderByGtG_C_P =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByGtG_C_P",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId", "companyId", "parentGroupId"},
					true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByGtG_C_P",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName()
					},
					new String[] {"groupId", "companyId", "parentGroupId"},
					false),
				_SQL_SELECT_GROUP__WHERE, _SQL_COUNT_GROUP__WHERE,
				GroupModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"group_.", "groupId", FinderColumn.Type.LONG, ">", true,
					true, Group::getGroupId),
				new FinderColumn<>(
					"group_.", "companyId", FinderColumn.Type.LONG, "=", true,
					true, Group::getCompanyId),
				new FinderColumn<>(
					"group_.", "parentGroupId", FinderColumn.Type.LONG, "=",
					true, true, Group::getParentGroupId));

		_uniquePersistenceFinderByC_C_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_C_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {"companyId", "classNameId", "classPK"}, 0, 0,
				false, Group::getCompanyId, Group::getClassNameId,
				Group::getClassPK),
			_SQL_SELECT_GROUP__WHERE, "",
			new FinderColumn<>(
				"group_.", "companyId", FinderColumn.Type.LONG, "=", true, true,
				Group::getCompanyId),
			new FinderColumn<>(
				"group_.", "classNameId", FinderColumn.Type.LONG, "=", true,
				true, Group::getClassNameId),
			new FinderColumn<>(
				"group_.", "classPK", FinderColumn.Type.LONG, "=", true, true,
				Group::getClassPK));

		_collectionPersistenceFinderByC_C_P = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C_P",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"companyId", "classNameId", "parentGroupId"},
				true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C_P",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {"companyId", "classNameId", "parentGroupId"},
				true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C_P",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {"companyId", "classNameId", "parentGroupId"},
				false),
			_SQL_SELECT_GROUP__WHERE, _SQL_COUNT_GROUP__WHERE,
			GroupModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"group_.", "companyId", FinderColumn.Type.LONG, "=", true, true,
				Group::getCompanyId),
			new FinderColumn<>(
				"group_.", "classNameId", FinderColumn.Type.LONG, "=", true,
				true, Group::getClassNameId),
			new FinderColumn<>(
				"group_.", "parentGroupId", FinderColumn.Type.LONG, "=", true,
				true, Group::getParentGroupId));

		_collectionPersistenceFinderByC_C_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C_S",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Boolean.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"companyId", "classNameId", "site"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C_S",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Boolean.class.getName()
				},
				new String[] {"companyId", "classNameId", "site"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C_S",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Boolean.class.getName()
				},
				new String[] {"companyId", "classNameId", "site"}, false),
			_SQL_SELECT_GROUP__WHERE, _SQL_COUNT_GROUP__WHERE,
			GroupModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"group_.", "companyId", FinderColumn.Type.LONG, "=", true, true,
				Group::getCompanyId),
			new FinderColumn<>(
				"group_.", "classNameId", FinderColumn.Type.LONG, "=", true,
				true, Group::getClassNameId),
			new FinderColumn<>(
				"group_.", "site", FinderColumn.Type.BOOLEAN, "=", true, true,
				Group::isSite));

		_collectionPersistenceFinderByC_P_S = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_P_S",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Boolean.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"companyId", "parentGroupId", "site"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_P_S",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Boolean.class.getName()
				},
				new String[] {"companyId", "parentGroupId", "site"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_P_S",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Boolean.class.getName()
				},
				new String[] {"companyId", "parentGroupId", "site"}, false),
			_SQL_SELECT_GROUP__WHERE, _SQL_COUNT_GROUP__WHERE,
			GroupModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"group_.", "companyId", FinderColumn.Type.LONG, "=", true, true,
				Group::getCompanyId),
			new FinderColumn<>(
				"group_.", "parentGroupId", FinderColumn.Type.LONG, "=", true,
				true, Group::getParentGroupId),
			new FinderColumn<>(
				"group_.", "site", FinderColumn.Type.BOOLEAN, "=", true, true,
				Group::isSite));

		_uniquePersistenceFinderByC_L_GK = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_L_GK",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					String.class.getName()
				},
				new String[] {"companyId", "liveGroupId", "groupKey"}, 0, 4,
				false, Group::getCompanyId, Group::getLiveGroupId,
				convertNullFunction(Group::getGroupKey)),
			_SQL_SELECT_GROUP__WHERE, "",
			new FinderColumn<>(
				"group_.", "companyId", FinderColumn.Type.LONG, "=", true, true,
				Group::getCompanyId),
			new FinderColumn<>(
				"group_.", "liveGroupId", FinderColumn.Type.LONG, "=", true,
				true, Group::getLiveGroupId),
			new FinderColumn<>(
				"group_.", "groupKey", FinderColumn.Type.STRING, "=", true,
				true, Group::getGroupKey));

		_collectionPersistenceFinderByC_LikeT_S =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_LikeT_S",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId", "treePath", "site"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_LikeT_S",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Boolean.class.getName()
					},
					new String[] {"companyId", "treePath", "site"}, false),
				_SQL_SELECT_GROUP__WHERE, _SQL_COUNT_GROUP__WHERE,
				GroupModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"group_.", "companyId", FinderColumn.Type.LONG, "=", true,
					true, Group::getCompanyId),
				new FinderColumn<>(
					"group_.", "treePath", FinderColumn.Type.STRING, "LIKE",
					true, true, Group::getTreePath),
				new FinderColumn<>(
					"group_.", "site", FinderColumn.Type.BOOLEAN, "=", true,
					true, Group::isSite));

		_collectionPersistenceFinderByC_LikeN_S =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_LikeN_S",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId", "name", "site"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_LikeN_S",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Boolean.class.getName()
					},
					new String[] {"companyId", "name", "site"}, false),
				_SQL_SELECT_GROUP__WHERE, _SQL_COUNT_GROUP__WHERE,
				GroupModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"group_.", "companyId", FinderColumn.Type.LONG, "=", true,
					true, Group::getCompanyId),
				new FinderColumn<>(
					"group_.", "name", FinderColumn.Type.STRING, "LIKE", false,
					true, Group::getName),
				new FinderColumn<>(
					"group_.", "site", FinderColumn.Type.BOOLEAN, "=", true,
					true, Group::isSite));

		_collectionPersistenceFinderByC_S_A = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_S_A",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					Boolean.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"companyId", "site", "active_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_S_A",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					Boolean.class.getName()
				},
				new String[] {"companyId", "site", "active_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_S_A",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					Boolean.class.getName()
				},
				new String[] {"companyId", "site", "active_"}, false),
			_SQL_SELECT_GROUP__WHERE, _SQL_COUNT_GROUP__WHERE,
			GroupModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"group_.", "companyId", FinderColumn.Type.LONG, "=", true, true,
				Group::getCompanyId),
			new FinderColumn<>(
				"group_.", "site", FinderColumn.Type.BOOLEAN, "=", true, true,
				Group::isSite),
			new FinderColumn<>(
				"group_.", "active", "active_", FinderColumn.Type.BOOLEAN, "=",
				true, true, Group::isActive));

		_collectionPersistenceFinderByGtG_C_C_P =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByGtG_C_C_P",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "companyId", "classNameId", "parentGroupId"
					},
					true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByGtG_C_C_P",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Long.class.getName()
					},
					new String[] {
						"groupId", "companyId", "classNameId", "parentGroupId"
					},
					false),
				_SQL_SELECT_GROUP__WHERE, _SQL_COUNT_GROUP__WHERE,
				GroupModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"group_.", "groupId", FinderColumn.Type.LONG, ">", true,
					true, Group::getGroupId),
				new FinderColumn<>(
					"group_.", "companyId", FinderColumn.Type.LONG, "=", true,
					true, Group::getCompanyId),
				new FinderColumn<>(
					"group_.", "classNameId", FinderColumn.Type.LONG, "=", true,
					true, Group::getClassNameId),
				new FinderColumn<>(
					"group_.", "parentGroupId", FinderColumn.Type.LONG, "=",
					true, true, Group::getParentGroupId));

		_collectionPersistenceFinderByGtG_C_P_S =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByGtG_C_P_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"groupId", "companyId", "parentGroupId", "site"
					},
					true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByGtG_C_P_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {
						"groupId", "companyId", "parentGroupId", "site"
					},
					false),
				_SQL_SELECT_GROUP__WHERE, _SQL_COUNT_GROUP__WHERE,
				GroupModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"group_.", "groupId", FinderColumn.Type.LONG, ">", true,
					true, Group::getGroupId),
				new FinderColumn<>(
					"group_.", "companyId", FinderColumn.Type.LONG, "=", true,
					true, Group::getCompanyId),
				new FinderColumn<>(
					"group_.", "parentGroupId", FinderColumn.Type.LONG, "=",
					true, true, Group::getParentGroupId),
				new FinderColumn<>(
					"group_.", "site", FinderColumn.Type.BOOLEAN, "=", true,
					true, Group::isSite));

		_uniquePersistenceFinderByC_C_L_GK = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_C_L_GK",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName(), String.class.getName()
				},
				new String[] {
					"companyId", "classNameId", "liveGroupId", "groupKey"
				},
				0, 8, false, Group::getCompanyId, Group::getClassNameId,
				Group::getLiveGroupId, convertNullFunction(Group::getGroupKey)),
			_SQL_SELECT_GROUP__WHERE, "",
			new FinderColumn<>(
				"group_.", "companyId", FinderColumn.Type.LONG, "=", true, true,
				Group::getCompanyId),
			new FinderColumn<>(
				"group_.", "classNameId", FinderColumn.Type.LONG, "=", true,
				true, Group::getClassNameId),
			new FinderColumn<>(
				"group_.", "liveGroupId", FinderColumn.Type.LONG, "=", true,
				true, Group::getLiveGroupId),
			new FinderColumn<>(
				"group_.", "groupKey", FinderColumn.Type.STRING, "=", true,
				true, Group::getGroupKey));

		_collectionPersistenceFinderByC_P_LikeN_S =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_P_LikeN_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId", "parentGroupId", "name", "site"},
					true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"countByC_P_LikeN_S",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						String.class.getName(), Boolean.class.getName()
					},
					new String[] {"companyId", "parentGroupId", "name", "site"},
					false),
				_SQL_SELECT_GROUP__WHERE, _SQL_COUNT_GROUP__WHERE,
				GroupModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"group_.", "companyId", FinderColumn.Type.LONG, "=", true,
					true, Group::getCompanyId),
				new FinderColumn<>(
					"group_.", "parentGroupId", FinderColumn.Type.LONG, "=",
					true, true, Group::getParentGroupId),
				new FinderColumn<>(
					"group_.", "name", FinderColumn.Type.STRING, "LIKE", false,
					true, Group::getName),
				new FinderColumn<>(
					"group_.", "site", FinderColumn.Type.BOOLEAN, "=", true,
					true, Group::isSite));

		_collectionPersistenceFinderByC_P_S_I =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_P_S_I",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"companyId", "parentGroupId", "site", "inheritContent"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_P_S_I",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName(), Boolean.class.getName()
					},
					new String[] {
						"companyId", "parentGroupId", "site", "inheritContent"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_P_S_I",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Boolean.class.getName(), Boolean.class.getName()
					},
					new String[] {
						"companyId", "parentGroupId", "site", "inheritContent"
					},
					false),
				_SQL_SELECT_GROUP__WHERE, _SQL_COUNT_GROUP__WHERE,
				GroupModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"group_.", "companyId", FinderColumn.Type.LONG, "=", true,
					true, Group::getCompanyId),
				new FinderColumn<>(
					"group_.", "parentGroupId", FinderColumn.Type.LONG, "=",
					true, true, Group::getParentGroupId),
				new FinderColumn<>(
					"group_.", "site", FinderColumn.Type.BOOLEAN, "=", true,
					true, Group::isSite),
				new FinderColumn<>(
					"group_.", "inheritContent", FinderColumn.Type.BOOLEAN, "=",
					true, true, Group::isInheritContent));

		_uniquePersistenceFinderByERC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "companyId"}, 0, 1,
				false, convertNullFunction(Group::getExternalReferenceCode),
				Group::getCompanyId),
			_SQL_SELECT_GROUP__WHERE, "",
			new FinderColumn<>(
				"group_.", "externalReferenceCode", FinderColumn.Type.STRING,
				"=", true, true, Group::getExternalReferenceCode),
			new FinderColumn<>(
				"group_.", "companyId", FinderColumn.Type.LONG, "=", true, true,
				Group::getCompanyId));

		GroupUtil.setPersistence(this);
	}

	public void destroy() {
		GroupUtil.setPersistence(null);

		EntityCacheUtil.removeCache(GroupImpl.class.getName());

		TableMapperFactory.removeTableMapper("Groups_Orgs");
		TableMapperFactory.removeTableMapper("Groups_Roles");
		TableMapperFactory.removeTableMapper("Groups_UserGroups");
		TableMapperFactory.removeTableMapper("Users_Groups");
	}

	@BeanReference(type = OrganizationPersistence.class)
	protected OrganizationPersistence organizationPersistence;

	protected TableMapper<Group, com.liferay.portal.kernel.model.Organization>
		groupToOrganizationTableMapper;

	@BeanReference(type = RolePersistence.class)
	protected RolePersistence rolePersistence;

	protected TableMapper<Group, com.liferay.portal.kernel.model.Role>
		groupToRoleTableMapper;

	@BeanReference(type = UserGroupPersistence.class)
	protected UserGroupPersistence userGroupPersistence;

	protected TableMapper<Group, com.liferay.portal.kernel.model.UserGroup>
		groupToUserGroupTableMapper;

	@BeanReference(type = UserPersistence.class)
	protected UserPersistence userPersistence;

	protected TableMapper<Group, com.liferay.portal.kernel.model.User>
		groupToUserTableMapper;

	private static final String _ENTITY_ALIAS_PREFIX =
		GroupModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_GROUP_ =
		"SELECT group_ FROM Group group_";

	private static final String _SQL_SELECT_GROUP__WHERE =
		"SELECT group_ FROM Group group_ WHERE ";

	private static final String _SQL_COUNT_GROUP__WHERE =
		"SELECT COUNT(group_) FROM Group group_ WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No Group exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		GroupPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "type", "active"});

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1215036844