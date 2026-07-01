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
import com.liferay.portal.kernel.exception.DuplicateUserGroupExternalReferenceCodeException;
import com.liferay.portal.kernel.exception.NoSuchUserGroupException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.UserGroupTable;
import com.liferay.portal.kernel.sanitizer.Sanitizer;
import com.liferay.portal.kernel.sanitizer.SanitizerException;
import com.liferay.portal.kernel.sanitizer.SanitizerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.GroupPersistence;
import com.liferay.portal.kernel.service.persistence.TeamPersistence;
import com.liferay.portal.kernel.service.persistence.UserGroupPersistence;
import com.liferay.portal.kernel.service.persistence.UserGroupUtil;
import com.liferay.portal.kernel.service.persistence.UserPersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelperUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.FilterCollectionPersistenceFinder;
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
import com.liferay.portal.model.impl.UserGroupImpl;
import com.liferay.portal.model.impl.UserGroupModelImpl;

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
 * The persistence implementation for the user group service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class UserGroupPersistenceImpl
	extends BasePersistenceImpl<UserGroup, NoSuchUserGroupException>
	implements UserGroupPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>UserGroupUtil</code> to access the user group persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		UserGroupImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FilterCollectionPersistenceFinder
		<UserGroup, NoSuchUserGroupException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the user groups where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching user groups
	 */
	@Override
	public List<UserGroup> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<UserGroup> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first user group in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user group
	 * @throws NoSuchUserGroupException if a matching user group could not be found
	 */
	@Override
	public UserGroup findByUuid_First(
			String uuid, OrderByComparator<UserGroup> orderByComparator)
		throws NoSuchUserGroupException {

		return _collectionPersistenceFinderByUuid.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid},
			orderByComparator);
	}

	/**
	 * Returns the first user group in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user group, or <code>null</code> if a matching user group could not be found
	 */
	@Override
	public UserGroup fetchByUuid_First(
		String uuid, OrderByComparator<UserGroup> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the user groups that the user has permissions to view where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching user groups that the user has permission to view
	 */
	@Override
	public List<UserGroup> filterFindByUuid(
		String uuid, int start, int end,
		OrderByComparator<UserGroup> orderByComparator) {

		return _collectionPersistenceFinderByUuid.filterFind(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid}, start, end,
			orderByComparator);
	}

	/**
	 * Removes all the user groups where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	/**
	 * Returns the number of user groups where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching user groups
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	/**
	 * Returns the number of user groups that the user has permission to view where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching user groups that the user has permission to view
	 */
	@Override
	public int filterCountByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.filterCount(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	private FilterCollectionPersistenceFinder
		<UserGroup, NoSuchUserGroupException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the user groups where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching user groups
	 */
	@Override
	public List<UserGroup> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<UserGroup> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first user group in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user group
	 * @throws NoSuchUserGroupException if a matching user group could not be found
	 */
	@Override
	public UserGroup findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<UserGroup> orderByComparator)
		throws NoSuchUserGroupException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			orderByComparator);
	}

	/**
	 * Returns the first user group in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user group, or <code>null</code> if a matching user group could not be found
	 */
	@Override
	public UserGroup fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<UserGroup> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the user groups that the user has permissions to view where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching user groups that the user has permission to view
	 */
	@Override
	public List<UserGroup> filterFindByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<UserGroup> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.filterFind(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			start, end, orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the user groups where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of user groups where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching user groups
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId});
	}

	/**
	 * Returns the number of user groups that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching user groups that the user has permission to view
	 */
	@Override
	public int filterCountByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.filterCount(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			companyId, 0);
	}

	private FilterCollectionPersistenceFinder
		<UserGroup, NoSuchUserGroupException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the user groups where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching user groups
	 */
	@Override
	public List<UserGroup> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<UserGroup> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first user group in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user group
	 * @throws NoSuchUserGroupException if a matching user group could not be found
	 */
	@Override
	public UserGroup findByCompanyId_First(
			long companyId, OrderByComparator<UserGroup> orderByComparator)
		throws NoSuchUserGroupException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			orderByComparator);
	}

	/**
	 * Returns the first user group in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user group, or <code>null</code> if a matching user group could not be found
	 */
	@Override
	public UserGroup fetchByCompanyId_First(
		long companyId, OrderByComparator<UserGroup> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the user groups that the user has permissions to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching user groups that the user has permission to view
	 */
	@Override
	public List<UserGroup> filterFindByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<UserGroup> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.filterFind(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId}, start,
			end, orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the user groups where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId});
	}

	/**
	 * Returns the number of user groups where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching user groups
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId});
	}

	/**
	 * Returns the number of user groups that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching user groups that the user has permission to view
	 */
	@Override
	public int filterCountByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.filterCount(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			companyId, 0);
	}

	private FilterCollectionPersistenceFinder
		<UserGroup, NoSuchUserGroupException> _collectionPersistenceFinderByC_P;

	/**
	 * Returns an ordered range of all the user groups where companyId = &#63; and parentUserGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentUserGroupId the parent user group ID
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching user groups
	 */
	@Override
	public List<UserGroup> findByC_P(
		long companyId, long parentUserGroupId, int start, int end,
		OrderByComparator<UserGroup> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_P.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, parentUserGroupId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first user group in the ordered set where companyId = &#63; and parentUserGroupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentUserGroupId the parent user group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user group
	 * @throws NoSuchUserGroupException if a matching user group could not be found
	 */
	@Override
	public UserGroup findByC_P_First(
			long companyId, long parentUserGroupId,
			OrderByComparator<UserGroup> orderByComparator)
		throws NoSuchUserGroupException {

		return _collectionPersistenceFinderByC_P.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, parentUserGroupId}, orderByComparator);
	}

	/**
	 * Returns the first user group in the ordered set where companyId = &#63; and parentUserGroupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentUserGroupId the parent user group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user group, or <code>null</code> if a matching user group could not be found
	 */
	@Override
	public UserGroup fetchByC_P_First(
		long companyId, long parentUserGroupId,
		OrderByComparator<UserGroup> orderByComparator) {

		return _collectionPersistenceFinderByC_P.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, parentUserGroupId}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the user groups that the user has permissions to view where companyId = &#63; and parentUserGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param parentUserGroupId the parent user group ID
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching user groups that the user has permission to view
	 */
	@Override
	public List<UserGroup> filterFindByC_P(
		long companyId, long parentUserGroupId, int start, int end,
		OrderByComparator<UserGroup> orderByComparator) {

		return _collectionPersistenceFinderByC_P.filterFind(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, parentUserGroupId}, start, end,
			orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the user groups where companyId = &#63; and parentUserGroupId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param parentUserGroupId the parent user group ID
	 */
	@Override
	public void removeByC_P(long companyId, long parentUserGroupId) {
		_collectionPersistenceFinderByC_P.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, parentUserGroupId});
	}

	/**
	 * Returns the number of user groups where companyId = &#63; and parentUserGroupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentUserGroupId the parent user group ID
	 * @return the number of matching user groups
	 */
	@Override
	public int countByC_P(long companyId, long parentUserGroupId) {
		return _collectionPersistenceFinderByC_P.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, parentUserGroupId});
	}

	/**
	 * Returns the number of user groups that the user has permission to view where companyId = &#63; and parentUserGroupId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param parentUserGroupId the parent user group ID
	 * @return the number of matching user groups that the user has permission to view
	 */
	@Override
	public int filterCountByC_P(long companyId, long parentUserGroupId) {
		return _collectionPersistenceFinderByC_P.filterCount(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, parentUserGroupId}, companyId, 0);
	}

	private UniquePersistenceFinder<UserGroup, NoSuchUserGroupException>
		_uniquePersistenceFinderByC_N;

	/**
	 * Returns the user group where companyId = &#63; and name = &#63; or throws a <code>NoSuchUserGroupException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the matching user group
	 * @throws NoSuchUserGroupException if a matching user group could not be found
	 */
	@Override
	public UserGroup findByC_N(long companyId, String name)
		throws NoSuchUserGroupException {

		return _uniquePersistenceFinderByC_N.find(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, name});
	}

	/**
	 * Returns the user group where companyId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching user group, or <code>null</code> if a matching user group could not be found
	 */
	@Override
	public UserGroup fetchByC_N(
		long companyId, String name, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_N.fetch(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, name},
			useFinderCache);
	}

	/**
	 * Removes the user group where companyId = &#63; and name = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the user group that was removed
	 */
	@Override
	public UserGroup removeByC_N(long companyId, String name)
		throws NoSuchUserGroupException {

		UserGroup userGroup = findByC_N(companyId, name);

		return remove(userGroup);
	}

	/**
	 * Returns the number of user groups where companyId = &#63; and name = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the number of matching user groups
	 */
	@Override
	public int countByC_N(long companyId, String name) {
		return _uniquePersistenceFinderByC_N.count(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, name});
	}

	private FilterCollectionPersistenceFinder
		<UserGroup, NoSuchUserGroupException>
			_collectionPersistenceFinderByC_LikeN;

	/**
	 * Returns all the user groups where companyId = &#63; and name LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the matching user groups
	 */
	@Override
	public List<UserGroup> findByC_LikeN(long companyId, String name) {
		return findByC_LikeN(
			companyId, name, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the user groups where companyId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @return the range of matching user groups
	 */
	@Override
	public List<UserGroup> findByC_LikeN(
		long companyId, String name, int start, int end) {

		return findByC_LikeN(companyId, name, start, end, null);
	}

	/**
	 * Returns an ordered range of all the user groups where companyId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching user groups
	 */
	@Override
	public List<UserGroup> findByC_LikeN(
		long companyId, String name, int start, int end,
		OrderByComparator<UserGroup> orderByComparator) {

		return findByC_LikeN(
			companyId, name, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the user groups where companyId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching user groups
	 */
	@Override
	public List<UserGroup> findByC_LikeN(
		long companyId, String name, int start, int end,
		OrderByComparator<UserGroup> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_LikeN.find(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, name},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first user group in the ordered set where companyId = &#63; and name LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user group
	 * @throws NoSuchUserGroupException if a matching user group could not be found
	 */
	@Override
	public UserGroup findByC_LikeN_First(
			long companyId, String name,
			OrderByComparator<UserGroup> orderByComparator)
		throws NoSuchUserGroupException {

		return _collectionPersistenceFinderByC_LikeN.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, name},
			orderByComparator);
	}

	/**
	 * Returns the first user group in the ordered set where companyId = &#63; and name LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user group, or <code>null</code> if a matching user group could not be found
	 */
	@Override
	public UserGroup fetchByC_LikeN_First(
		long companyId, String name,
		OrderByComparator<UserGroup> orderByComparator) {

		return _collectionPersistenceFinderByC_LikeN.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, name},
			orderByComparator);
	}

	/**
	 * Returns all the user groups that the user has permission to view where companyId = &#63; and name LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the matching user groups that the user has permission to view
	 */
	@Override
	public List<UserGroup> filterFindByC_LikeN(long companyId, String name) {
		return filterFindByC_LikeN(
			companyId, name, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the user groups that the user has permission to view where companyId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @return the range of matching user groups that the user has permission to view
	 */
	@Override
	public List<UserGroup> filterFindByC_LikeN(
		long companyId, String name, int start, int end) {

		return filterFindByC_LikeN(companyId, name, start, end, null);
	}

	/**
	 * Returns an ordered range of all the user groups that the user has permissions to view where companyId = &#63; and name LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching user groups that the user has permission to view
	 */
	@Override
	public List<UserGroup> filterFindByC_LikeN(
		long companyId, String name, int start, int end,
		OrderByComparator<UserGroup> orderByComparator) {

		return _collectionPersistenceFinderByC_LikeN.filterFind(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, name},
			start, end, orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the user groups where companyId = &#63; and name LIKE &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 */
	@Override
	public void removeByC_LikeN(long companyId, String name) {
		_collectionPersistenceFinderByC_LikeN.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, name});
	}

	/**
	 * Returns the number of user groups where companyId = &#63; and name LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the number of matching user groups
	 */
	@Override
	public int countByC_LikeN(long companyId, String name) {
		return _collectionPersistenceFinderByC_LikeN.count(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, name});
	}

	/**
	 * Returns the number of user groups that the user has permission to view where companyId = &#63; and name LIKE &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the number of matching user groups that the user has permission to view
	 */
	@Override
	public int filterCountByC_LikeN(long companyId, String name) {
		return _collectionPersistenceFinderByC_LikeN.filterCount(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, name},
			companyId, 0);
	}

	private FilterCollectionPersistenceFinder
		<UserGroup, NoSuchUserGroupException>
			_collectionPersistenceFinderByGtU_C_P;

	/**
	 * Returns all the user groups where userGroupId &gt; &#63; and companyId = &#63; and parentUserGroupId = &#63;.
	 *
	 * @param userGroupId the user group ID
	 * @param companyId the company ID
	 * @param parentUserGroupId the parent user group ID
	 * @return the matching user groups
	 */
	@Override
	public List<UserGroup> findByGtU_C_P(
		long userGroupId, long companyId, long parentUserGroupId) {

		return findByGtU_C_P(
			userGroupId, companyId, parentUserGroupId, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the user groups where userGroupId &gt; &#63; and companyId = &#63; and parentUserGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param userGroupId the user group ID
	 * @param companyId the company ID
	 * @param parentUserGroupId the parent user group ID
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @return the range of matching user groups
	 */
	@Override
	public List<UserGroup> findByGtU_C_P(
		long userGroupId, long companyId, long parentUserGroupId, int start,
		int end) {

		return findByGtU_C_P(
			userGroupId, companyId, parentUserGroupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the user groups where userGroupId &gt; &#63; and companyId = &#63; and parentUserGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param userGroupId the user group ID
	 * @param companyId the company ID
	 * @param parentUserGroupId the parent user group ID
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching user groups
	 */
	@Override
	public List<UserGroup> findByGtU_C_P(
		long userGroupId, long companyId, long parentUserGroupId, int start,
		int end, OrderByComparator<UserGroup> orderByComparator) {

		return findByGtU_C_P(
			userGroupId, companyId, parentUserGroupId, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the user groups where userGroupId &gt; &#63; and companyId = &#63; and parentUserGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param userGroupId the user group ID
	 * @param companyId the company ID
	 * @param parentUserGroupId the parent user group ID
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching user groups
	 */
	@Override
	public List<UserGroup> findByGtU_C_P(
		long userGroupId, long companyId, long parentUserGroupId, int start,
		int end, OrderByComparator<UserGroup> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGtU_C_P.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userGroupId, companyId, parentUserGroupId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first user group in the ordered set where userGroupId &gt; &#63; and companyId = &#63; and parentUserGroupId = &#63;.
	 *
	 * @param userGroupId the user group ID
	 * @param companyId the company ID
	 * @param parentUserGroupId the parent user group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user group
	 * @throws NoSuchUserGroupException if a matching user group could not be found
	 */
	@Override
	public UserGroup findByGtU_C_P_First(
			long userGroupId, long companyId, long parentUserGroupId,
			OrderByComparator<UserGroup> orderByComparator)
		throws NoSuchUserGroupException {

		return _collectionPersistenceFinderByGtU_C_P.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userGroupId, companyId, parentUserGroupId},
			orderByComparator);
	}

	/**
	 * Returns the first user group in the ordered set where userGroupId &gt; &#63; and companyId = &#63; and parentUserGroupId = &#63;.
	 *
	 * @param userGroupId the user group ID
	 * @param companyId the company ID
	 * @param parentUserGroupId the parent user group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user group, or <code>null</code> if a matching user group could not be found
	 */
	@Override
	public UserGroup fetchByGtU_C_P_First(
		long userGroupId, long companyId, long parentUserGroupId,
		OrderByComparator<UserGroup> orderByComparator) {

		return _collectionPersistenceFinderByGtU_C_P.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userGroupId, companyId, parentUserGroupId},
			orderByComparator);
	}

	/**
	 * Returns all the user groups that the user has permission to view where userGroupId &gt; &#63; and companyId = &#63; and parentUserGroupId = &#63;.
	 *
	 * @param userGroupId the user group ID
	 * @param companyId the company ID
	 * @param parentUserGroupId the parent user group ID
	 * @return the matching user groups that the user has permission to view
	 */
	@Override
	public List<UserGroup> filterFindByGtU_C_P(
		long userGroupId, long companyId, long parentUserGroupId) {

		return filterFindByGtU_C_P(
			userGroupId, companyId, parentUserGroupId, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the user groups that the user has permission to view where userGroupId &gt; &#63; and companyId = &#63; and parentUserGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param userGroupId the user group ID
	 * @param companyId the company ID
	 * @param parentUserGroupId the parent user group ID
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @return the range of matching user groups that the user has permission to view
	 */
	@Override
	public List<UserGroup> filterFindByGtU_C_P(
		long userGroupId, long companyId, long parentUserGroupId, int start,
		int end) {

		return filterFindByGtU_C_P(
			userGroupId, companyId, parentUserGroupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the user groups that the user has permissions to view where userGroupId &gt; &#63; and companyId = &#63; and parentUserGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param userGroupId the user group ID
	 * @param companyId the company ID
	 * @param parentUserGroupId the parent user group ID
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching user groups that the user has permission to view
	 */
	@Override
	public List<UserGroup> filterFindByGtU_C_P(
		long userGroupId, long companyId, long parentUserGroupId, int start,
		int end, OrderByComparator<UserGroup> orderByComparator) {

		return _collectionPersistenceFinderByGtU_C_P.filterFind(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userGroupId, companyId, parentUserGroupId}, start,
			end, orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the user groups where userGroupId &gt; &#63; and companyId = &#63; and parentUserGroupId = &#63; from the database.
	 *
	 * @param userGroupId the user group ID
	 * @param companyId the company ID
	 * @param parentUserGroupId the parent user group ID
	 */
	@Override
	public void removeByGtU_C_P(
		long userGroupId, long companyId, long parentUserGroupId) {

		_collectionPersistenceFinderByGtU_C_P.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userGroupId, companyId, parentUserGroupId});
	}

	/**
	 * Returns the number of user groups where userGroupId &gt; &#63; and companyId = &#63; and parentUserGroupId = &#63;.
	 *
	 * @param userGroupId the user group ID
	 * @param companyId the company ID
	 * @param parentUserGroupId the parent user group ID
	 * @return the number of matching user groups
	 */
	@Override
	public int countByGtU_C_P(
		long userGroupId, long companyId, long parentUserGroupId) {

		return _collectionPersistenceFinderByGtU_C_P.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userGroupId, companyId, parentUserGroupId});
	}

	/**
	 * Returns the number of user groups that the user has permission to view where userGroupId &gt; &#63; and companyId = &#63; and parentUserGroupId = &#63;.
	 *
	 * @param userGroupId the user group ID
	 * @param companyId the company ID
	 * @param parentUserGroupId the parent user group ID
	 * @return the number of matching user groups that the user has permission to view
	 */
	@Override
	public int filterCountByGtU_C_P(
		long userGroupId, long companyId, long parentUserGroupId) {

		return _collectionPersistenceFinderByGtU_C_P.filterCount(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userGroupId, companyId, parentUserGroupId}, companyId,
			0);
	}

	private UniquePersistenceFinder<UserGroup, NoSuchUserGroupException>
		_uniquePersistenceFinderByERC_C;

	/**
	 * Returns the user group where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchUserGroupException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching user group
	 * @throws NoSuchUserGroupException if a matching user group could not be found
	 */
	@Override
	public UserGroup findByERC_C(String externalReferenceCode, long companyId)
		throws NoSuchUserGroupException {

		return _uniquePersistenceFinderByERC_C.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {externalReferenceCode, companyId});
	}

	/**
	 * Returns the user group where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching user group, or <code>null</code> if a matching user group could not be found
	 */
	@Override
	public UserGroup fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_C.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {externalReferenceCode, companyId}, useFinderCache);
	}

	/**
	 * Removes the user group where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the user group that was removed
	 */
	@Override
	public UserGroup removeByERC_C(String externalReferenceCode, long companyId)
		throws NoSuchUserGroupException {

		UserGroup userGroup = findByERC_C(externalReferenceCode, companyId);

		return remove(userGroup);
	}

	/**
	 * Returns the number of user groups where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching user groups
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		return _uniquePersistenceFinderByERC_C.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {externalReferenceCode, companyId});
	}

	public UserGroupPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("groups", "groups_");

		setDBColumnNames(dbColumnNames);

		setModelClass(UserGroup.class);

		setModelImplClass(UserGroupImpl.class);
		setModelPKClass(long.class);

		setTable(UserGroupTable.INSTANCE);
	}

	/**
	 * Creates a new user group with the primary key. Does not add the user group to the database.
	 *
	 * @param userGroupId the primary key for the new user group
	 * @return the new user group
	 */
	@Override
	public UserGroup create(long userGroupId) {
		UserGroup userGroup = new UserGroupImpl();

		userGroup.setNew(true);
		userGroup.setPrimaryKey(userGroupId);

		String uuid = PortalUUIDUtil.generate();

		userGroup.setUuid(uuid);

		userGroup.setCompanyId(CompanyThreadLocal.getCompanyId());

		return userGroup;
	}

	/**
	 * Removes the user group with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param userGroupId the primary key of the user group
	 * @return the user group that was removed
	 * @throws NoSuchUserGroupException if a user group with the primary key could not be found
	 */
	@Override
	public UserGroup remove(long userGroupId) throws NoSuchUserGroupException {
		return remove((Serializable)userGroupId);
	}

	@Override
	protected UserGroup removeImpl(UserGroup userGroup) {
		userGroupToGroupTableMapper.deleteLeftPrimaryKeyTableMappings(
			userGroup.getPrimaryKey());

		userGroupToTeamTableMapper.deleteLeftPrimaryKeyTableMappings(
			userGroup.getPrimaryKey());

		userGroupToUserTableMapper.deleteLeftPrimaryKeyTableMappings(
			userGroup.getPrimaryKey());

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(userGroup)) {
				userGroup = (UserGroup)session.get(
					UserGroupImpl.class, userGroup.getPrimaryKeyObj());
			}

			if ((userGroup != null) &&
				CTPersistenceHelperUtil.isRemove(userGroup)) {

				session.delete(userGroup);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (userGroup != null) {
			clearCache(userGroup);
		}

		return userGroup;
	}

	@Override
	public UserGroup updateImpl(UserGroup userGroup) {
		boolean isNew = userGroup.isNew();

		if (!(userGroup instanceof UserGroupModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(userGroup.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(userGroup);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in userGroup proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom UserGroup implementation " +
					userGroup.getClass());
		}

		UserGroupModelImpl userGroupModelImpl = (UserGroupModelImpl)userGroup;

		if (Validator.isNull(userGroup.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			userGroup.setUuid(uuid);
		}

		if (Validator.isNull(userGroup.getExternalReferenceCode())) {
			userGroup.setExternalReferenceCode(userGroup.getUuid());
		}
		else {
			if (!Objects.equals(
					userGroupModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					userGroup.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = userGroup.getCompanyId();

					long groupId = 0;

					long classPK = 0;

					if (!isNew) {
						classPK = userGroup.getPrimaryKey();
					}

					try {
						userGroup.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								UserGroup.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								userGroup.getExternalReferenceCode(), null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			UserGroup ercUserGroup = fetchByERC_C(
				userGroup.getExternalReferenceCode(), userGroup.getCompanyId());

			if (isNew) {
				if (ercUserGroup != null) {
					throw new DuplicateUserGroupExternalReferenceCodeException(
						"Duplicate user group with external reference code " +
							userGroup.getExternalReferenceCode() +
								" and company " + userGroup.getCompanyId());
				}
			}
			else {
				if ((ercUserGroup != null) &&
					(userGroup.getUserGroupId() !=
						ercUserGroup.getUserGroupId())) {

					throw new DuplicateUserGroupExternalReferenceCodeException(
						"Duplicate user group with external reference code " +
							userGroup.getExternalReferenceCode() +
								" and company " + userGroup.getCompanyId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (userGroup.getCreateDate() == null)) {
			if (serviceContext == null) {
				userGroup.setCreateDate(date);
			}
			else {
				userGroup.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!userGroupModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				userGroup.setModifiedDate(date);
			}
			else {
				userGroup.setModifiedDate(serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (CTPersistenceHelperUtil.isInsert(userGroup)) {
				if (!isNew) {
					session.evict(
						UserGroupImpl.class, userGroup.getPrimaryKeyObj());
				}

				session.save(userGroup);
			}
			else {
				userGroup = (UserGroup)session.merge(userGroup);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(userGroup, false);

		if (isNew) {
			userGroup.setNew(false);
		}

		userGroup.resetOriginalValues();

		return userGroup;
	}

	/**
	 * Returns the user group with the primary key or throws a <code>NoSuchUserGroupException</code> if it could not be found.
	 *
	 * @param userGroupId the primary key of the user group
	 * @return the user group
	 * @throws NoSuchUserGroupException if a user group with the primary key could not be found
	 */
	@Override
	public UserGroup findByPrimaryKey(long userGroupId)
		throws NoSuchUserGroupException {

		return findByPrimaryKey((Serializable)userGroupId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return CTPersistenceHelperUtil.getCTPersistenceHelper();
	}

	/**
	 * Returns the user group with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param userGroupId the primary key of the user group
	 * @return the user group, or <code>null</code> if a user group with the primary key could not be found
	 */
	@Override
	public UserGroup fetchByPrimaryKey(long userGroupId) {
		return fetchByPrimaryKey((Serializable)userGroupId);
	}

	/**
	 * Returns the primaryKeys of groups associated with the user group.
	 *
	 * @param pk the primary key of the user group
	 * @return long[] of the primaryKeys of groups associated with the user group
	 */
	@Override
	public long[] getGroupPrimaryKeys(long pk) {
		long[] pks = userGroupToGroupTableMapper.getRightPrimaryKeys(pk);

		return pks.clone();
	}

	/**
	 * Returns all the groups associated with the user group.
	 *
	 * @param pk the primary key of the user group
	 * @return the groups associated with the user group
	 */
	@Override
	public List<com.liferay.portal.kernel.model.Group> getGroups(long pk) {
		return getGroups(pk, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	/**
	 * Returns a range of all the groups associated with the user group.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the user group
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @return the range of groups associated with the user group
	 */
	@Override
	public List<com.liferay.portal.kernel.model.Group> getGroups(
		long pk, int start, int end) {

		return getGroups(pk, start, end, null);
	}

	/**
	 * Returns an ordered range of all the groups associated with the user group.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the user group
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of groups associated with the user group
	 */
	@Override
	public List<com.liferay.portal.kernel.model.Group> getGroups(
		long pk, int start, int end,
		OrderByComparator<com.liferay.portal.kernel.model.Group>
			orderByComparator) {

		return userGroupToGroupTableMapper.getRightBaseModels(
			pk, start, end, orderByComparator);
	}

	/**
	 * Returns the number of groups associated with the user group.
	 *
	 * @param pk the primary key of the user group
	 * @return the number of groups associated with the user group
	 */
	@Override
	public int getGroupsSize(long pk) {
		long[] pks = userGroupToGroupTableMapper.getRightPrimaryKeys(pk);

		return pks.length;
	}

	/**
	 * Returns <code>true</code> if the group is associated with the user group.
	 *
	 * @param pk the primary key of the user group
	 * @param groupPK the primary key of the group
	 * @return <code>true</code> if the group is associated with the user group; <code>false</code> otherwise
	 */
	@Override
	public boolean containsGroup(long pk, long groupPK) {
		return userGroupToGroupTableMapper.containsTableMapping(pk, groupPK);
	}

	/**
	 * Returns <code>true</code> if the user group has any groups associated with it.
	 *
	 * @param pk the primary key of the user group to check for associations with groups
	 * @return <code>true</code> if the user group has any groups associated with it; <code>false</code> otherwise
	 */
	@Override
	public boolean containsGroups(long pk) {
		if (getGroupsSize(pk) > 0) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Adds an association between the user group and the group. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group
	 * @param groupPK the primary key of the group
	 * @return <code>true</code> if an association between the user group and the group was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addGroup(long pk, long groupPK) {
		UserGroup userGroup = fetchByPrimaryKey(pk);

		if (userGroup == null) {
			return userGroupToGroupTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk, groupPK);
		}
		else {
			return userGroupToGroupTableMapper.addTableMapping(
				userGroup.getCompanyId(), pk, groupPK);
		}
	}

	/**
	 * Adds an association between the user group and the group. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group
	 * @param group the group
	 * @return <code>true</code> if an association between the user group and the group was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addGroup(
		long pk, com.liferay.portal.kernel.model.Group group) {

		UserGroup userGroup = fetchByPrimaryKey(pk);

		if (userGroup == null) {
			return userGroupToGroupTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk, group.getPrimaryKey());
		}
		else {
			return userGroupToGroupTableMapper.addTableMapping(
				userGroup.getCompanyId(), pk, group.getPrimaryKey());
		}
	}

	/**
	 * Adds an association between the user group and the groups. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group
	 * @param groupPKs the primary keys of the groups
	 * @return <code>true</code> if at least one association between the user group and the groups was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addGroups(long pk, long[] groupPKs) {
		long companyId = 0;

		UserGroup userGroup = fetchByPrimaryKey(pk);

		if (userGroup == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = userGroup.getCompanyId();
		}

		long[] addedKeys = userGroupToGroupTableMapper.addTableMappings(
			companyId, pk, groupPKs);

		if (addedKeys.length > 0) {
			return true;
		}

		return false;
	}

	/**
	 * Adds an association between the user group and the groups. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group
	 * @param groups the groups
	 * @return <code>true</code> if at least one association between the user group and the groups was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addGroups(
		long pk, List<com.liferay.portal.kernel.model.Group> groups) {

		return addGroups(
			pk,
			ListUtil.toLongArray(
				groups,
				com.liferay.portal.kernel.model.Group.GROUP_ID_ACCESSOR));
	}

	/**
	 * Clears all associations between the user group and its groups. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group to clear the associated groups from
	 */
	@Override
	public void clearGroups(long pk) {
		userGroupToGroupTableMapper.deleteLeftPrimaryKeyTableMappings(pk);
	}

	/**
	 * Removes the association between the user group and the group. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group
	 * @param groupPK the primary key of the group
	 */
	@Override
	public void removeGroup(long pk, long groupPK) {
		userGroupToGroupTableMapper.deleteTableMapping(pk, groupPK);
	}

	/**
	 * Removes the association between the user group and the group. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group
	 * @param group the group
	 */
	@Override
	public void removeGroup(
		long pk, com.liferay.portal.kernel.model.Group group) {

		userGroupToGroupTableMapper.deleteTableMapping(
			pk, group.getPrimaryKey());
	}

	/**
	 * Removes the association between the user group and the groups. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group
	 * @param groupPKs the primary keys of the groups
	 */
	@Override
	public void removeGroups(long pk, long[] groupPKs) {
		userGroupToGroupTableMapper.deleteTableMappings(pk, groupPKs);
	}

	/**
	 * Removes the association between the user group and the groups. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group
	 * @param groups the groups
	 */
	@Override
	public void removeGroups(
		long pk, List<com.liferay.portal.kernel.model.Group> groups) {

		removeGroups(
			pk,
			ListUtil.toLongArray(
				groups,
				com.liferay.portal.kernel.model.Group.GROUP_ID_ACCESSOR));
	}

	/**
	 * Sets the groups associated with the user group, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group
	 * @param groupPKs the primary keys of the groups to be associated with the user group
	 */
	@Override
	public void setGroups(long pk, long[] groupPKs) {
		Set<Long> newGroupPKsSet = SetUtil.fromArray(groupPKs);
		Set<Long> oldGroupPKsSet = SetUtil.fromArray(
			userGroupToGroupTableMapper.getRightPrimaryKeys(pk));

		Set<Long> removeGroupPKsSet = new HashSet<Long>(oldGroupPKsSet);

		removeGroupPKsSet.removeAll(newGroupPKsSet);

		userGroupToGroupTableMapper.deleteTableMappings(
			pk, ArrayUtil.toLongArray(removeGroupPKsSet));

		newGroupPKsSet.removeAll(oldGroupPKsSet);

		long companyId = 0;

		UserGroup userGroup = fetchByPrimaryKey(pk);

		if (userGroup == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = userGroup.getCompanyId();
		}

		userGroupToGroupTableMapper.addTableMappings(
			companyId, pk, ArrayUtil.toLongArray(newGroupPKsSet));
	}

	/**
	 * Sets the groups associated with the user group, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group
	 * @param groups the groups to be associated with the user group
	 */
	@Override
	public void setGroups(
		long pk, List<com.liferay.portal.kernel.model.Group> groups) {

		try {
			long[] groupPKs = new long[groups.size()];

			for (int i = 0; i < groups.size(); i++) {
				com.liferay.portal.kernel.model.Group group = groups.get(i);

				groupPKs[i] = group.getPrimaryKey();
			}

			setGroups(pk, groupPKs);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
	}

	/**
	 * Returns the primaryKeys of teams associated with the user group.
	 *
	 * @param pk the primary key of the user group
	 * @return long[] of the primaryKeys of teams associated with the user group
	 */
	@Override
	public long[] getTeamPrimaryKeys(long pk) {
		long[] pks = userGroupToTeamTableMapper.getRightPrimaryKeys(pk);

		return pks.clone();
	}

	/**
	 * Returns all the teams associated with the user group.
	 *
	 * @param pk the primary key of the user group
	 * @return the teams associated with the user group
	 */
	@Override
	public List<com.liferay.portal.kernel.model.Team> getTeams(long pk) {
		return getTeams(pk, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	/**
	 * Returns a range of all the teams associated with the user group.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the user group
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @return the range of teams associated with the user group
	 */
	@Override
	public List<com.liferay.portal.kernel.model.Team> getTeams(
		long pk, int start, int end) {

		return getTeams(pk, start, end, null);
	}

	/**
	 * Returns an ordered range of all the teams associated with the user group.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the user group
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of teams associated with the user group
	 */
	@Override
	public List<com.liferay.portal.kernel.model.Team> getTeams(
		long pk, int start, int end,
		OrderByComparator<com.liferay.portal.kernel.model.Team>
			orderByComparator) {

		return userGroupToTeamTableMapper.getRightBaseModels(
			pk, start, end, orderByComparator);
	}

	/**
	 * Returns the number of teams associated with the user group.
	 *
	 * @param pk the primary key of the user group
	 * @return the number of teams associated with the user group
	 */
	@Override
	public int getTeamsSize(long pk) {
		long[] pks = userGroupToTeamTableMapper.getRightPrimaryKeys(pk);

		return pks.length;
	}

	/**
	 * Returns <code>true</code> if the team is associated with the user group.
	 *
	 * @param pk the primary key of the user group
	 * @param teamPK the primary key of the team
	 * @return <code>true</code> if the team is associated with the user group; <code>false</code> otherwise
	 */
	@Override
	public boolean containsTeam(long pk, long teamPK) {
		return userGroupToTeamTableMapper.containsTableMapping(pk, teamPK);
	}

	/**
	 * Returns <code>true</code> if the user group has any teams associated with it.
	 *
	 * @param pk the primary key of the user group to check for associations with teams
	 * @return <code>true</code> if the user group has any teams associated with it; <code>false</code> otherwise
	 */
	@Override
	public boolean containsTeams(long pk) {
		if (getTeamsSize(pk) > 0) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Adds an association between the user group and the team. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group
	 * @param teamPK the primary key of the team
	 * @return <code>true</code> if an association between the user group and the team was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addTeam(long pk, long teamPK) {
		UserGroup userGroup = fetchByPrimaryKey(pk);

		if (userGroup == null) {
			return userGroupToTeamTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk, teamPK);
		}
		else {
			return userGroupToTeamTableMapper.addTableMapping(
				userGroup.getCompanyId(), pk, teamPK);
		}
	}

	/**
	 * Adds an association between the user group and the team. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group
	 * @param team the team
	 * @return <code>true</code> if an association between the user group and the team was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addTeam(long pk, com.liferay.portal.kernel.model.Team team) {
		UserGroup userGroup = fetchByPrimaryKey(pk);

		if (userGroup == null) {
			return userGroupToTeamTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk, team.getPrimaryKey());
		}
		else {
			return userGroupToTeamTableMapper.addTableMapping(
				userGroup.getCompanyId(), pk, team.getPrimaryKey());
		}
	}

	/**
	 * Adds an association between the user group and the teams. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group
	 * @param teamPKs the primary keys of the teams
	 * @return <code>true</code> if at least one association between the user group and the teams was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addTeams(long pk, long[] teamPKs) {
		long companyId = 0;

		UserGroup userGroup = fetchByPrimaryKey(pk);

		if (userGroup == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = userGroup.getCompanyId();
		}

		long[] addedKeys = userGroupToTeamTableMapper.addTableMappings(
			companyId, pk, teamPKs);

		if (addedKeys.length > 0) {
			return true;
		}

		return false;
	}

	/**
	 * Adds an association between the user group and the teams. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group
	 * @param teams the teams
	 * @return <code>true</code> if at least one association between the user group and the teams was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addTeams(
		long pk, List<com.liferay.portal.kernel.model.Team> teams) {

		return addTeams(
			pk,
			ListUtil.toLongArray(
				teams, com.liferay.portal.kernel.model.Team.TEAM_ID_ACCESSOR));
	}

	/**
	 * Clears all associations between the user group and its teams. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group to clear the associated teams from
	 */
	@Override
	public void clearTeams(long pk) {
		userGroupToTeamTableMapper.deleteLeftPrimaryKeyTableMappings(pk);
	}

	/**
	 * Removes the association between the user group and the team. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group
	 * @param teamPK the primary key of the team
	 */
	@Override
	public void removeTeam(long pk, long teamPK) {
		userGroupToTeamTableMapper.deleteTableMapping(pk, teamPK);
	}

	/**
	 * Removes the association between the user group and the team. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group
	 * @param team the team
	 */
	@Override
	public void removeTeam(long pk, com.liferay.portal.kernel.model.Team team) {
		userGroupToTeamTableMapper.deleteTableMapping(pk, team.getPrimaryKey());
	}

	/**
	 * Removes the association between the user group and the teams. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group
	 * @param teamPKs the primary keys of the teams
	 */
	@Override
	public void removeTeams(long pk, long[] teamPKs) {
		userGroupToTeamTableMapper.deleteTableMappings(pk, teamPKs);
	}

	/**
	 * Removes the association between the user group and the teams. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group
	 * @param teams the teams
	 */
	@Override
	public void removeTeams(
		long pk, List<com.liferay.portal.kernel.model.Team> teams) {

		removeTeams(
			pk,
			ListUtil.toLongArray(
				teams, com.liferay.portal.kernel.model.Team.TEAM_ID_ACCESSOR));
	}

	/**
	 * Sets the teams associated with the user group, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group
	 * @param teamPKs the primary keys of the teams to be associated with the user group
	 */
	@Override
	public void setTeams(long pk, long[] teamPKs) {
		Set<Long> newTeamPKsSet = SetUtil.fromArray(teamPKs);
		Set<Long> oldTeamPKsSet = SetUtil.fromArray(
			userGroupToTeamTableMapper.getRightPrimaryKeys(pk));

		Set<Long> removeTeamPKsSet = new HashSet<Long>(oldTeamPKsSet);

		removeTeamPKsSet.removeAll(newTeamPKsSet);

		userGroupToTeamTableMapper.deleteTableMappings(
			pk, ArrayUtil.toLongArray(removeTeamPKsSet));

		newTeamPKsSet.removeAll(oldTeamPKsSet);

		long companyId = 0;

		UserGroup userGroup = fetchByPrimaryKey(pk);

		if (userGroup == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = userGroup.getCompanyId();
		}

		userGroupToTeamTableMapper.addTableMappings(
			companyId, pk, ArrayUtil.toLongArray(newTeamPKsSet));
	}

	/**
	 * Sets the teams associated with the user group, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group
	 * @param teams the teams to be associated with the user group
	 */
	@Override
	public void setTeams(
		long pk, List<com.liferay.portal.kernel.model.Team> teams) {

		try {
			long[] teamPKs = new long[teams.size()];

			for (int i = 0; i < teams.size(); i++) {
				com.liferay.portal.kernel.model.Team team = teams.get(i);

				teamPKs[i] = team.getPrimaryKey();
			}

			setTeams(pk, teamPKs);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
	}

	/**
	 * Returns the primaryKeys of users associated with the user group.
	 *
	 * @param pk the primary key of the user group
	 * @return long[] of the primaryKeys of users associated with the user group
	 */
	@Override
	public long[] getUserPrimaryKeys(long pk) {
		long[] pks = userGroupToUserTableMapper.getRightPrimaryKeys(pk);

		return pks.clone();
	}

	/**
	 * Returns all the users associated with the user group.
	 *
	 * @param pk the primary key of the user group
	 * @return the users associated with the user group
	 */
	@Override
	public List<com.liferay.portal.kernel.model.User> getUsers(long pk) {
		return getUsers(pk, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	/**
	 * Returns a range of all the users associated with the user group.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the user group
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @return the range of users associated with the user group
	 */
	@Override
	public List<com.liferay.portal.kernel.model.User> getUsers(
		long pk, int start, int end) {

		return getUsers(pk, start, end, null);
	}

	/**
	 * Returns an ordered range of all the users associated with the user group.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the user group
	 * @param start the lower bound of the range of user groups
	 * @param end the upper bound of the range of user groups (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of users associated with the user group
	 */
	@Override
	public List<com.liferay.portal.kernel.model.User> getUsers(
		long pk, int start, int end,
		OrderByComparator<com.liferay.portal.kernel.model.User>
			orderByComparator) {

		return userGroupToUserTableMapper.getRightBaseModels(
			pk, start, end, orderByComparator);
	}

	/**
	 * Returns the number of users associated with the user group.
	 *
	 * @param pk the primary key of the user group
	 * @return the number of users associated with the user group
	 */
	@Override
	public int getUsersSize(long pk) {
		long[] pks = userGroupToUserTableMapper.getRightPrimaryKeys(pk);

		return pks.length;
	}

	/**
	 * Returns <code>true</code> if the user is associated with the user group.
	 *
	 * @param pk the primary key of the user group
	 * @param userPK the primary key of the user
	 * @return <code>true</code> if the user is associated with the user group; <code>false</code> otherwise
	 */
	@Override
	public boolean containsUser(long pk, long userPK) {
		return userGroupToUserTableMapper.containsTableMapping(pk, userPK);
	}

	/**
	 * Returns <code>true</code> if the user group has any users associated with it.
	 *
	 * @param pk the primary key of the user group to check for associations with users
	 * @return <code>true</code> if the user group has any users associated with it; <code>false</code> otherwise
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
	 * Adds an association between the user group and the user. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group
	 * @param userPK the primary key of the user
	 * @return <code>true</code> if an association between the user group and the user was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addUser(long pk, long userPK) {
		UserGroup userGroup = fetchByPrimaryKey(pk);

		if (userGroup == null) {
			return userGroupToUserTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk, userPK);
		}
		else {
			return userGroupToUserTableMapper.addTableMapping(
				userGroup.getCompanyId(), pk, userPK);
		}
	}

	/**
	 * Adds an association between the user group and the user. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group
	 * @param user the user
	 * @return <code>true</code> if an association between the user group and the user was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addUser(long pk, com.liferay.portal.kernel.model.User user) {
		UserGroup userGroup = fetchByPrimaryKey(pk);

		if (userGroup == null) {
			return userGroupToUserTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk, user.getPrimaryKey());
		}
		else {
			return userGroupToUserTableMapper.addTableMapping(
				userGroup.getCompanyId(), pk, user.getPrimaryKey());
		}
	}

	/**
	 * Adds an association between the user group and the users. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group
	 * @param userPKs the primary keys of the users
	 * @return <code>true</code> if at least one association between the user group and the users was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addUsers(long pk, long[] userPKs) {
		long companyId = 0;

		UserGroup userGroup = fetchByPrimaryKey(pk);

		if (userGroup == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = userGroup.getCompanyId();
		}

		long[] addedKeys = userGroupToUserTableMapper.addTableMappings(
			companyId, pk, userPKs);

		if (addedKeys.length > 0) {
			return true;
		}

		return false;
	}

	/**
	 * Adds an association between the user group and the users. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group
	 * @param users the users
	 * @return <code>true</code> if at least one association between the user group and the users was added; <code>false</code> if they were all already associated
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
	 * Clears all associations between the user group and its users. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group to clear the associated users from
	 */
	@Override
	public void clearUsers(long pk) {
		userGroupToUserTableMapper.deleteLeftPrimaryKeyTableMappings(pk);
	}

	/**
	 * Removes the association between the user group and the user. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group
	 * @param userPK the primary key of the user
	 */
	@Override
	public void removeUser(long pk, long userPK) {
		userGroupToUserTableMapper.deleteTableMapping(pk, userPK);
	}

	/**
	 * Removes the association between the user group and the user. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group
	 * @param user the user
	 */
	@Override
	public void removeUser(long pk, com.liferay.portal.kernel.model.User user) {
		userGroupToUserTableMapper.deleteTableMapping(pk, user.getPrimaryKey());
	}

	/**
	 * Removes the association between the user group and the users. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group
	 * @param userPKs the primary keys of the users
	 */
	@Override
	public void removeUsers(long pk, long[] userPKs) {
		userGroupToUserTableMapper.deleteTableMappings(pk, userPKs);
	}

	/**
	 * Removes the association between the user group and the users. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group
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
	 * Sets the users associated with the user group, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group
	 * @param userPKs the primary keys of the users to be associated with the user group
	 */
	@Override
	public void setUsers(long pk, long[] userPKs) {
		Set<Long> newUserPKsSet = SetUtil.fromArray(userPKs);
		Set<Long> oldUserPKsSet = SetUtil.fromArray(
			userGroupToUserTableMapper.getRightPrimaryKeys(pk));

		Set<Long> removeUserPKsSet = new HashSet<Long>(oldUserPKsSet);

		removeUserPKsSet.removeAll(newUserPKsSet);

		userGroupToUserTableMapper.deleteTableMappings(
			pk, ArrayUtil.toLongArray(removeUserPKsSet));

		newUserPKsSet.removeAll(oldUserPKsSet);

		long companyId = 0;

		UserGroup userGroup = fetchByPrimaryKey(pk);

		if (userGroup == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = userGroup.getCompanyId();
		}

		userGroupToUserTableMapper.addTableMappings(
			companyId, pk, ArrayUtil.toLongArray(newUserPKsSet));
	}

	/**
	 * Sets the users associated with the user group, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the user group
	 * @param users the users to be associated with the user group
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
		return "userGroupId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_USERGROUP;
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
		return UserGroupModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "UserGroup";
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
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("parentUserGroupId");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("description");
		ctMergeColumnNames.add("addedByLDAPImport");
		ctMergeColumnNames.add("status");
		ctMergeColumnNames.add("groups_");
		ctMergeColumnNames.add("teams");
		ctMergeColumnNames.add("users");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("userGroupId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_mappingTableNames.add("Groups_UserGroups");
		_mappingTableNames.add("UserGroups_Teams");
		_mappingTableNames.add("Users_UserGroups");

		_uniqueIndexColumnNames.add(new String[] {"companyId", "name"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "companyId"});
	}

	/**
	 * Initializes the user group persistence.
	 */
	public void afterPropertiesSet() {
		userGroupToGroupTableMapper = TableMapperFactory.getTableMapper(
			"Groups_UserGroups", "companyId", "userGroupId", "groupId", this,
			groupPersistence);

		userGroupToTeamTableMapper = TableMapperFactory.getTableMapper(
			"UserGroups_Teams", "companyId", "userGroupId", "teamId", this,
			teamPersistence);

		userGroupToUserTableMapper = TableMapperFactory.getTableMapper(
			"Users_UserGroups", "companyId", "userGroupId", "userId", this,
			userPersistence);

		_collectionPersistenceFinderByUuid =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid",
					new String[] {
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"uuid_"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid",
					new String[] {String.class.getName()},
					new String[] {"uuid_"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid",
					new String[] {String.class.getName()},
					new String[] {"uuid_"}, 0, 1, false, null),
				_SQL_SELECT_USERGROUP_WHERE, _SQL_COUNT_USERGROUP_WHERE,
				UserGroupModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"userGroup.", "uuid", "uuid_", FinderColumn.Type.STRING,
					"=", true, true, UserGroup::getUuid));

		_collectionPersistenceFinderByUuid_C =
			new FilterCollectionPersistenceFinder<>(
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
				_SQL_SELECT_USERGROUP_WHERE, _SQL_COUNT_USERGROUP_WHERE,
				UserGroupModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"userGroup.", "uuid", "uuid_", FinderColumn.Type.STRING,
					"=", true, true, UserGroup::getUuid),
				new FinderColumn<>(
					"userGroup.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, UserGroup::getCompanyId));

		_collectionPersistenceFinderByCompanyId =
			new FilterCollectionPersistenceFinder<>(
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
				_SQL_SELECT_USERGROUP_WHERE, _SQL_COUNT_USERGROUP_WHERE,
				UserGroupModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"userGroup.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, UserGroup::getCompanyId));

		_collectionPersistenceFinderByC_P =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_P",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId", "parentUserGroupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_P",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"companyId", "parentUserGroupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_P",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"companyId", "parentUserGroupId"}, false),
				_SQL_SELECT_USERGROUP_WHERE, _SQL_COUNT_USERGROUP_WHERE,
				UserGroupModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"userGroup.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, UserGroup::getCompanyId),
				new FinderColumn<>(
					"userGroup.", "parentUserGroupId", FinderColumn.Type.LONG,
					"=", true, true, UserGroup::getParentUserGroupId));

		_uniquePersistenceFinderByC_N = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_N",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "name"}, 2, 2, false,
				UserGroup::getCompanyId,
				convertCaseFunction(UserGroup::getName)),
			_SQL_SELECT_USERGROUP_WHERE, "",
			new FinderColumn<>(
				"userGroup.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, UserGroup::getCompanyId),
			new FinderColumn<>(
				"userGroup.", "name", FinderColumn.Type.STRING, "=", false,
				true, UserGroup::getName));

		_collectionPersistenceFinderByC_LikeN =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_LikeN",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId", "name"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_LikeN",
					new String[] {Long.class.getName(), String.class.getName()},
					new String[] {"companyId", "name"}, false),
				_SQL_SELECT_USERGROUP_WHERE, _SQL_COUNT_USERGROUP_WHERE,
				UserGroupModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"userGroup.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, UserGroup::getCompanyId),
				new FinderColumn<>(
					"userGroup.", "name", FinderColumn.Type.STRING, "LIKE",
					false, true, UserGroup::getName));

		_collectionPersistenceFinderByGtU_C_P =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByGtU_C_P",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"userGroupId", "companyId", "parentUserGroupId"
					},
					true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByGtU_C_P",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName()
					},
					new String[] {
						"userGroupId", "companyId", "parentUserGroupId"
					},
					false),
				_SQL_SELECT_USERGROUP_WHERE, _SQL_COUNT_USERGROUP_WHERE,
				UserGroupModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"userGroup.", "userGroupId", FinderColumn.Type.LONG, ">",
					true, true, UserGroup::getUserGroupId),
				new FinderColumn<>(
					"userGroup.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, UserGroup::getCompanyId),
				new FinderColumn<>(
					"userGroup.", "parentUserGroupId", FinderColumn.Type.LONG,
					"=", true, true, UserGroup::getParentUserGroupId));

		_uniquePersistenceFinderByERC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "companyId"}, 0, 1,
				false, convertNullFunction(UserGroup::getExternalReferenceCode),
				UserGroup::getCompanyId),
			_SQL_SELECT_USERGROUP_WHERE, "",
			new FinderColumn<>(
				"userGroup.", "externalReferenceCode", FinderColumn.Type.STRING,
				"=", true, true, UserGroup::getExternalReferenceCode),
			new FinderColumn<>(
				"userGroup.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, UserGroup::getCompanyId));

		UserGroupUtil.setPersistence(this);
	}

	public void destroy() {
		UserGroupUtil.setPersistence(null);

		EntityCacheUtil.removeCache(UserGroupImpl.class.getName());

		TableMapperFactory.removeTableMapper("Groups_UserGroups");
		TableMapperFactory.removeTableMapper("UserGroups_Teams");
		TableMapperFactory.removeTableMapper("Users_UserGroups");
	}

	@BeanReference(type = GroupPersistence.class)
	protected GroupPersistence groupPersistence;

	protected TableMapper<UserGroup, com.liferay.portal.kernel.model.Group>
		userGroupToGroupTableMapper;

	@BeanReference(type = TeamPersistence.class)
	protected TeamPersistence teamPersistence;

	protected TableMapper<UserGroup, com.liferay.portal.kernel.model.Team>
		userGroupToTeamTableMapper;

	@BeanReference(type = UserPersistence.class)
	protected UserPersistence userPersistence;

	protected TableMapper<UserGroup, com.liferay.portal.kernel.model.User>
		userGroupToUserTableMapper;

	private static final String _ENTITY_ALIAS_PREFIX =
		UserGroupModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_USERGROUP =
		"SELECT userGroup FROM UserGroup userGroup";

	private static final String _SQL_SELECT_USERGROUP_WHERE =
		"SELECT userGroup FROM UserGroup userGroup WHERE ";

	private static final String _SQL_COUNT_USERGROUP_WHERE =
		"SELECT COUNT(userGroup) FROM UserGroup userGroup WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No UserGroup exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		UserGroupPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "groups"});

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-209494962