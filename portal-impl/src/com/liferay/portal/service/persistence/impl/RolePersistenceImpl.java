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
import com.liferay.portal.kernel.exception.DuplicateRoleExternalReferenceCodeException;
import com.liferay.portal.kernel.exception.NoSuchRoleException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.RoleTable;
import com.liferay.portal.kernel.sanitizer.Sanitizer;
import com.liferay.portal.kernel.sanitizer.SanitizerException;
import com.liferay.portal.kernel.sanitizer.SanitizerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.GroupPersistence;
import com.liferay.portal.kernel.service.persistence.RolePersistence;
import com.liferay.portal.kernel.service.persistence.RoleUtil;
import com.liferay.portal.kernel.service.persistence.UserPersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelperUtil;
import com.liferay.portal.kernel.service.persistence.impl.ArrayableFinderColumn;
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
import com.liferay.portal.model.impl.RoleImpl;
import com.liferay.portal.model.impl.RoleModelImpl;

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
 * The persistence implementation for the role service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class RolePersistenceImpl
	extends BasePersistenceImpl<Role, NoSuchRoleException>
	implements RolePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>RoleUtil</code> to access the role persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		RoleImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FilterCollectionPersistenceFinder<Role, NoSuchRoleException>
		_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the roles where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RoleModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of roles
	 * @param end the upper bound of the range of roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching roles
	 */
	@Override
	public List<Role> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<Role> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first role in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching role
	 * @throws NoSuchRoleException if a matching role could not be found
	 */
	@Override
	public Role findByUuid_First(
			String uuid, OrderByComparator<Role> orderByComparator)
		throws NoSuchRoleException {

		return _collectionPersistenceFinderByUuid.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid},
			orderByComparator);
	}

	/**
	 * Returns the first role in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching role, or <code>null</code> if a matching role could not be found
	 */
	@Override
	public Role fetchByUuid_First(
		String uuid, OrderByComparator<Role> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the roles that the user has permissions to view where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RoleModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of roles
	 * @param end the upper bound of the range of roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching roles that the user has permission to view
	 */
	@Override
	public List<Role> filterFindByUuid(
		String uuid, int start, int end,
		OrderByComparator<Role> orderByComparator) {

		return _collectionPersistenceFinderByUuid.filterFind(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid}, start, end,
			orderByComparator);
	}

	/**
	 * Removes all the roles where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	/**
	 * Returns the number of roles where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching roles
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	/**
	 * Returns the number of roles that the user has permission to view where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching roles that the user has permission to view
	 */
	@Override
	public int filterCountByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.filterCount(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	private FilterCollectionPersistenceFinder<Role, NoSuchRoleException>
		_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the roles where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RoleModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of roles
	 * @param end the upper bound of the range of roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching roles
	 */
	@Override
	public List<Role> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<Role> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first role in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching role
	 * @throws NoSuchRoleException if a matching role could not be found
	 */
	@Override
	public Role findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<Role> orderByComparator)
		throws NoSuchRoleException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			orderByComparator);
	}

	/**
	 * Returns the first role in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching role, or <code>null</code> if a matching role could not be found
	 */
	@Override
	public Role fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<Role> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the roles that the user has permissions to view where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RoleModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of roles
	 * @param end the upper bound of the range of roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching roles that the user has permission to view
	 */
	@Override
	public List<Role> filterFindByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<Role> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.filterFind(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			start, end, orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the roles where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of roles where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching roles
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId});
	}

	/**
	 * Returns the number of roles that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching roles that the user has permission to view
	 */
	@Override
	public int filterCountByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.filterCount(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			companyId, 0);
	}

	private FilterCollectionPersistenceFinder<Role, NoSuchRoleException>
		_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the roles where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RoleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of roles
	 * @param end the upper bound of the range of roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching roles
	 */
	@Override
	public List<Role> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<Role> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first role in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching role
	 * @throws NoSuchRoleException if a matching role could not be found
	 */
	@Override
	public Role findByCompanyId_First(
			long companyId, OrderByComparator<Role> orderByComparator)
		throws NoSuchRoleException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			orderByComparator);
	}

	/**
	 * Returns the first role in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching role, or <code>null</code> if a matching role could not be found
	 */
	@Override
	public Role fetchByCompanyId_First(
		long companyId, OrderByComparator<Role> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the roles that the user has permissions to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RoleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of roles
	 * @param end the upper bound of the range of roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching roles that the user has permission to view
	 */
	@Override
	public List<Role> filterFindByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<Role> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.filterFind(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId}, start,
			end, orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the roles where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId});
	}

	/**
	 * Returns the number of roles where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching roles
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId});
	}

	/**
	 * Returns the number of roles that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching roles that the user has permission to view
	 */
	@Override
	public int filterCountByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.filterCount(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			companyId, 0);
	}

	private FilterCollectionPersistenceFinder<Role, NoSuchRoleException>
		_collectionPersistenceFinderByName;

	/**
	 * Returns an ordered range of all the roles where name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RoleModelImpl</code>.
	 * </p>
	 *
	 * @param name the name
	 * @param start the lower bound of the range of roles
	 * @param end the upper bound of the range of roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching roles
	 */
	@Override
	public List<Role> findByName(
		String name, int start, int end,
		OrderByComparator<Role> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByName.find(
			FinderCacheUtil.getFinderCache(), new Object[] {name}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first role in the ordered set where name = &#63;.
	 *
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching role
	 * @throws NoSuchRoleException if a matching role could not be found
	 */
	@Override
	public Role findByName_First(
			String name, OrderByComparator<Role> orderByComparator)
		throws NoSuchRoleException {

		return _collectionPersistenceFinderByName.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {name},
			orderByComparator);
	}

	/**
	 * Returns the first role in the ordered set where name = &#63;.
	 *
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching role, or <code>null</code> if a matching role could not be found
	 */
	@Override
	public Role fetchByName_First(
		String name, OrderByComparator<Role> orderByComparator) {

		return _collectionPersistenceFinderByName.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {name},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the roles that the user has permissions to view where name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RoleModelImpl</code>.
	 * </p>
	 *
	 * @param name the name
	 * @param start the lower bound of the range of roles
	 * @param end the upper bound of the range of roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching roles that the user has permission to view
	 */
	@Override
	public List<Role> filterFindByName(
		String name, int start, int end,
		OrderByComparator<Role> orderByComparator) {

		return _collectionPersistenceFinderByName.filterFind(
			FinderCacheUtil.getFinderCache(), new Object[] {name}, start, end,
			orderByComparator);
	}

	/**
	 * Removes all the roles where name = &#63; from the database.
	 *
	 * @param name the name
	 */
	@Override
	public void removeByName(String name) {
		_collectionPersistenceFinderByName.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {name});
	}

	/**
	 * Returns the number of roles where name = &#63;.
	 *
	 * @param name the name
	 * @return the number of matching roles
	 */
	@Override
	public int countByName(String name) {
		return _collectionPersistenceFinderByName.count(
			FinderCacheUtil.getFinderCache(), new Object[] {name});
	}

	/**
	 * Returns the number of roles that the user has permission to view where name = &#63;.
	 *
	 * @param name the name
	 * @return the number of matching roles that the user has permission to view
	 */
	@Override
	public int filterCountByName(String name) {
		return _collectionPersistenceFinderByName.filterCount(
			FinderCacheUtil.getFinderCache(), new Object[] {name});
	}

	private FilterCollectionPersistenceFinder<Role, NoSuchRoleException>
		_collectionPersistenceFinderByType;

	/**
	 * Returns an ordered range of all the roles where type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RoleModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param start the lower bound of the range of roles
	 * @param end the upper bound of the range of roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching roles
	 */
	@Override
	public List<Role> findByType(
		int type, int start, int end, OrderByComparator<Role> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByType.find(
			FinderCacheUtil.getFinderCache(), new Object[] {type}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first role in the ordered set where type = &#63;.
	 *
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching role
	 * @throws NoSuchRoleException if a matching role could not be found
	 */
	@Override
	public Role findByType_First(
			int type, OrderByComparator<Role> orderByComparator)
		throws NoSuchRoleException {

		return _collectionPersistenceFinderByType.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {type},
			orderByComparator);
	}

	/**
	 * Returns the first role in the ordered set where type = &#63;.
	 *
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching role, or <code>null</code> if a matching role could not be found
	 */
	@Override
	public Role fetchByType_First(
		int type, OrderByComparator<Role> orderByComparator) {

		return _collectionPersistenceFinderByType.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {type},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the roles that the user has permissions to view where type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RoleModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param start the lower bound of the range of roles
	 * @param end the upper bound of the range of roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching roles that the user has permission to view
	 */
	@Override
	public List<Role> filterFindByType(
		int type, int start, int end,
		OrderByComparator<Role> orderByComparator) {

		return _collectionPersistenceFinderByType.filterFind(
			FinderCacheUtil.getFinderCache(), new Object[] {type}, start, end,
			orderByComparator);
	}

	/**
	 * Removes all the roles where type = &#63; from the database.
	 *
	 * @param type the type
	 */
	@Override
	public void removeByType(int type) {
		_collectionPersistenceFinderByType.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {type});
	}

	/**
	 * Returns the number of roles where type = &#63;.
	 *
	 * @param type the type
	 * @return the number of matching roles
	 */
	@Override
	public int countByType(int type) {
		return _collectionPersistenceFinderByType.count(
			FinderCacheUtil.getFinderCache(), new Object[] {type});
	}

	/**
	 * Returns the number of roles that the user has permission to view where type = &#63;.
	 *
	 * @param type the type
	 * @return the number of matching roles that the user has permission to view
	 */
	@Override
	public int filterCountByType(int type) {
		return _collectionPersistenceFinderByType.filterCount(
			FinderCacheUtil.getFinderCache(), new Object[] {type});
	}

	private FilterCollectionPersistenceFinder<Role, NoSuchRoleException>
		_collectionPersistenceFinderBySubtype;

	/**
	 * Returns an ordered range of all the roles where subtype = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RoleModelImpl</code>.
	 * </p>
	 *
	 * @param subtype the subtype
	 * @param start the lower bound of the range of roles
	 * @param end the upper bound of the range of roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching roles
	 */
	@Override
	public List<Role> findBySubtype(
		String subtype, int start, int end,
		OrderByComparator<Role> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderBySubtype.find(
			FinderCacheUtil.getFinderCache(), new Object[] {subtype}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first role in the ordered set where subtype = &#63;.
	 *
	 * @param subtype the subtype
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching role
	 * @throws NoSuchRoleException if a matching role could not be found
	 */
	@Override
	public Role findBySubtype_First(
			String subtype, OrderByComparator<Role> orderByComparator)
		throws NoSuchRoleException {

		return _collectionPersistenceFinderBySubtype.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {subtype},
			orderByComparator);
	}

	/**
	 * Returns the first role in the ordered set where subtype = &#63;.
	 *
	 * @param subtype the subtype
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching role, or <code>null</code> if a matching role could not be found
	 */
	@Override
	public Role fetchBySubtype_First(
		String subtype, OrderByComparator<Role> orderByComparator) {

		return _collectionPersistenceFinderBySubtype.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {subtype},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the roles that the user has permissions to view where subtype = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RoleModelImpl</code>.
	 * </p>
	 *
	 * @param subtype the subtype
	 * @param start the lower bound of the range of roles
	 * @param end the upper bound of the range of roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching roles that the user has permission to view
	 */
	@Override
	public List<Role> filterFindBySubtype(
		String subtype, int start, int end,
		OrderByComparator<Role> orderByComparator) {

		return _collectionPersistenceFinderBySubtype.filterFind(
			FinderCacheUtil.getFinderCache(), new Object[] {subtype}, start,
			end, orderByComparator);
	}

	/**
	 * Removes all the roles where subtype = &#63; from the database.
	 *
	 * @param subtype the subtype
	 */
	@Override
	public void removeBySubtype(String subtype) {
		_collectionPersistenceFinderBySubtype.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {subtype});
	}

	/**
	 * Returns the number of roles where subtype = &#63;.
	 *
	 * @param subtype the subtype
	 * @return the number of matching roles
	 */
	@Override
	public int countBySubtype(String subtype) {
		return _collectionPersistenceFinderBySubtype.count(
			FinderCacheUtil.getFinderCache(), new Object[] {subtype});
	}

	/**
	 * Returns the number of roles that the user has permission to view where subtype = &#63;.
	 *
	 * @param subtype the subtype
	 * @return the number of matching roles that the user has permission to view
	 */
	@Override
	public int filterCountBySubtype(String subtype) {
		return _collectionPersistenceFinderBySubtype.filterCount(
			FinderCacheUtil.getFinderCache(), new Object[] {subtype});
	}

	private UniquePersistenceFinder<Role, NoSuchRoleException>
		_uniquePersistenceFinderByC_N;

	/**
	 * Returns the role where companyId = &#63; and name = &#63; or throws a <code>NoSuchRoleException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the matching role
	 * @throws NoSuchRoleException if a matching role could not be found
	 */
	@Override
	public Role findByC_N(long companyId, String name)
		throws NoSuchRoleException {

		return _uniquePersistenceFinderByC_N.find(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, name});
	}

	/**
	 * Returns the role where companyId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching role, or <code>null</code> if a matching role could not be found
	 */
	@Override
	public Role fetchByC_N(
		long companyId, String name, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_N.fetch(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, name},
			useFinderCache);
	}

	/**
	 * Removes the role where companyId = &#63; and name = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the role that was removed
	 */
	@Override
	public Role removeByC_N(long companyId, String name)
		throws NoSuchRoleException {

		Role role = findByC_N(companyId, name);

		return remove(role);
	}

	/**
	 * Returns the number of roles where companyId = &#63; and name = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the number of matching roles
	 */
	@Override
	public int countByC_N(long companyId, String name) {
		return _uniquePersistenceFinderByC_N.count(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, name});
	}

	private FilterCollectionPersistenceFinder<Role, NoSuchRoleException>
		_collectionPersistenceFinderByC_T;

	/**
	 * Returns an ordered range of all the roles where companyId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RoleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @param start the lower bound of the range of roles
	 * @param end the upper bound of the range of roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching roles
	 */
	@Override
	public List<Role> findByC_T(
		long companyId, int type, int start, int end,
		OrderByComparator<Role> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByC_T.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, new int[] {type}}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first role in the ordered set where companyId = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching role
	 * @throws NoSuchRoleException if a matching role could not be found
	 */
	@Override
	public Role findByC_T_First(
			long companyId, int type, OrderByComparator<Role> orderByComparator)
		throws NoSuchRoleException {

		return _collectionPersistenceFinderByC_T.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, new int[] {type}}, orderByComparator);
	}

	/**
	 * Returns the first role in the ordered set where companyId = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching role, or <code>null</code> if a matching role could not be found
	 */
	@Override
	public Role fetchByC_T_First(
		long companyId, int type, OrderByComparator<Role> orderByComparator) {

		return _collectionPersistenceFinderByC_T.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, new int[] {type}}, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the roles that the user has permissions to view where companyId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RoleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @param start the lower bound of the range of roles
	 * @param end the upper bound of the range of roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching roles that the user has permission to view
	 */
	@Override
	public List<Role> filterFindByC_T(
		long companyId, int type, int start, int end,
		OrderByComparator<Role> orderByComparator) {

		return _collectionPersistenceFinderByC_T.filterFind(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, new int[] {type}}, start, end,
			orderByComparator, companyId, 0);
	}

	/**
	 * Returns an ordered range of all the roles that the user has permission to view where companyId = &#63; and type = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RoleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param types the types
	 * @param start the lower bound of the range of roles
	 * @param end the upper bound of the range of roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching roles that the user has permission to view
	 */
	@Override
	public List<Role> filterFindByC_T(
		long companyId, int[] types, int start, int end,
		OrderByComparator<Role> orderByComparator) {

		return _collectionPersistenceFinderByC_T.filterFind(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, ArrayUtil.sortedUnique(types)}, start, end,
			orderByComparator, companyId, 0);
	}

	/**
	 * Returns an ordered range of all the roles where companyId = &#63; and type = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RoleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param types the types
	 * @param start the lower bound of the range of roles
	 * @param end the upper bound of the range of roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching roles
	 */
	@Override
	public List<Role> findByC_T(
		long companyId, int[] types, int start, int end,
		OrderByComparator<Role> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByC_T.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, ArrayUtil.sortedUnique(types)}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the roles where companyId = &#63; and type = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param type the type
	 */
	@Override
	public void removeByC_T(long companyId, int type) {
		_collectionPersistenceFinderByC_T.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, new int[] {type}});
	}

	/**
	 * Returns the number of roles where companyId = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @return the number of matching roles
	 */
	@Override
	public int countByC_T(long companyId, int type) {
		return _collectionPersistenceFinderByC_T.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, new int[] {type}});
	}

	/**
	 * Returns the number of roles where companyId = &#63; and type = any &#63;.
	 *
	 * @param companyId the company ID
	 * @param types the types
	 * @return the number of matching roles
	 */
	@Override
	public int countByC_T(long companyId, int[] types) {
		return _collectionPersistenceFinderByC_T.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, ArrayUtil.sortedUnique(types)});
	}

	/**
	 * Returns the number of roles that the user has permission to view where companyId = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @return the number of matching roles that the user has permission to view
	 */
	@Override
	public int filterCountByC_T(long companyId, int type) {
		return _collectionPersistenceFinderByC_T.filterCount(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, new int[] {type}}, companyId, 0);
	}

	/**
	 * Returns the number of roles that the user has permission to view where companyId = &#63; and type = any &#63;.
	 *
	 * @param companyId the company ID
	 * @param types the types
	 * @return the number of matching roles that the user has permission to view
	 */
	@Override
	public int filterCountByC_T(long companyId, int[] types) {
		return _collectionPersistenceFinderByC_T.filterCount(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, ArrayUtil.sortedUnique(types)}, companyId,
			0);
	}

	private FilterCollectionPersistenceFinder<Role, NoSuchRoleException>
		_collectionPersistenceFinderByT_S;

	/**
	 * Returns an ordered range of all the roles where type = &#63; and subtype = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RoleModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param subtype the subtype
	 * @param start the lower bound of the range of roles
	 * @param end the upper bound of the range of roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching roles
	 */
	@Override
	public List<Role> findByT_S(
		int type, String subtype, int start, int end,
		OrderByComparator<Role> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByT_S.find(
			FinderCacheUtil.getFinderCache(), new Object[] {type, subtype},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first role in the ordered set where type = &#63; and subtype = &#63;.
	 *
	 * @param type the type
	 * @param subtype the subtype
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching role
	 * @throws NoSuchRoleException if a matching role could not be found
	 */
	@Override
	public Role findByT_S_First(
			int type, String subtype, OrderByComparator<Role> orderByComparator)
		throws NoSuchRoleException {

		return _collectionPersistenceFinderByT_S.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {type, subtype},
			orderByComparator);
	}

	/**
	 * Returns the first role in the ordered set where type = &#63; and subtype = &#63;.
	 *
	 * @param type the type
	 * @param subtype the subtype
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching role, or <code>null</code> if a matching role could not be found
	 */
	@Override
	public Role fetchByT_S_First(
		int type, String subtype, OrderByComparator<Role> orderByComparator) {

		return _collectionPersistenceFinderByT_S.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {type, subtype},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the roles that the user has permissions to view where type = &#63; and subtype = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RoleModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param subtype the subtype
	 * @param start the lower bound of the range of roles
	 * @param end the upper bound of the range of roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching roles that the user has permission to view
	 */
	@Override
	public List<Role> filterFindByT_S(
		int type, String subtype, int start, int end,
		OrderByComparator<Role> orderByComparator) {

		return _collectionPersistenceFinderByT_S.filterFind(
			FinderCacheUtil.getFinderCache(), new Object[] {type, subtype},
			start, end, orderByComparator);
	}

	/**
	 * Removes all the roles where type = &#63; and subtype = &#63; from the database.
	 *
	 * @param type the type
	 * @param subtype the subtype
	 */
	@Override
	public void removeByT_S(int type, String subtype) {
		_collectionPersistenceFinderByT_S.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {type, subtype});
	}

	/**
	 * Returns the number of roles where type = &#63; and subtype = &#63;.
	 *
	 * @param type the type
	 * @param subtype the subtype
	 * @return the number of matching roles
	 */
	@Override
	public int countByT_S(int type, String subtype) {
		return _collectionPersistenceFinderByT_S.count(
			FinderCacheUtil.getFinderCache(), new Object[] {type, subtype});
	}

	/**
	 * Returns the number of roles that the user has permission to view where type = &#63; and subtype = &#63;.
	 *
	 * @param type the type
	 * @param subtype the subtype
	 * @return the number of matching roles that the user has permission to view
	 */
	@Override
	public int filterCountByT_S(int type, String subtype) {
		return _collectionPersistenceFinderByT_S.filterCount(
			FinderCacheUtil.getFinderCache(), new Object[] {type, subtype});
	}

	private FilterCollectionPersistenceFinder<Role, NoSuchRoleException>
		_collectionPersistenceFinderByC_C_C;
	private UniquePersistenceFinder<Role, NoSuchRoleException>
		_uniquePersistenceFinderByC_C_C;

	/**
	 * Returns an ordered range of all the roles where companyId = &#63; and classNameId = &#63; and classPK = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RoleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPKs the class pks
	 * @param start the lower bound of the range of roles
	 * @param end the upper bound of the range of roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching roles
	 */
	@Override
	public List<Role> findByC_C_C(
		long companyId, long classNameId, long[] classPKs, int start, int end,
		OrderByComparator<Role> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C_C.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				companyId, classNameId, ArrayUtil.sortedUnique(classPKs)
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the role where companyId = &#63; and classNameId = &#63; and classPK = &#63; or throws a <code>NoSuchRoleException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching role
	 * @throws NoSuchRoleException if a matching role could not be found
	 */
	@Override
	public Role findByC_C_C(long companyId, long classNameId, long classPK)
		throws NoSuchRoleException {

		return _uniquePersistenceFinderByC_C_C.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId, classPK});
	}

	/**
	 * Returns the role where companyId = &#63; and classNameId = &#63; and classPK = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching role, or <code>null</code> if a matching role could not be found
	 */
	@Override
	public Role fetchByC_C_C(
		long companyId, long classNameId, long classPK,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByC_C_C.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId, classPK}, useFinderCache);
	}

	/**
	 * Removes the role where companyId = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the role that was removed
	 */
	@Override
	public Role removeByC_C_C(long companyId, long classNameId, long classPK)
		throws NoSuchRoleException {

		Role role = findByC_C_C(companyId, classNameId, classPK);

		return remove(role);
	}

	/**
	 * Returns the number of roles where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching roles
	 */
	@Override
	public int countByC_C_C(long companyId, long classNameId, long classPK) {
		return _collectionPersistenceFinderByC_C_C.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId, new long[] {classPK}});
	}

	/**
	 * Returns the number of roles where companyId = &#63; and classNameId = &#63; and classPK = any &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPKs the class pks
	 * @return the number of matching roles
	 */
	@Override
	public int countByC_C_C(long companyId, long classNameId, long[] classPKs) {
		return _collectionPersistenceFinderByC_C_C.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				companyId, classNameId, ArrayUtil.sortedUnique(classPKs)
			});
	}

	/**
	 * Returns the number of roles that the user has permission to view where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching roles that the user has permission to view
	 */
	@Override
	public int filterCountByC_C_C(
		long companyId, long classNameId, long classPK) {

		return _collectionPersistenceFinderByC_C_C.filterCount(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId, new long[] {classPK}},
			companyId, 0);
	}

	/**
	 * Returns the number of roles that the user has permission to view where companyId = &#63; and classNameId = &#63; and classPK = any &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPKs the class pks
	 * @return the number of matching roles that the user has permission to view
	 */
	@Override
	public int filterCountByC_C_C(
		long companyId, long classNameId, long[] classPKs) {

		return _collectionPersistenceFinderByC_C_C.filterCount(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				companyId, classNameId, ArrayUtil.sortedUnique(classPKs)
			},
			companyId, 0);
	}

	private FilterCollectionPersistenceFinder<Role, NoSuchRoleException>
		_collectionPersistenceFinderByC_C_C_T;
	private UniquePersistenceFinder<Role, NoSuchRoleException>
		_uniquePersistenceFinderByC_C_C_T;

	/**
	 * Returns an ordered range of all the roles where companyId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RoleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPKs the class pks
	 * @param type the type
	 * @param start the lower bound of the range of roles
	 * @param end the upper bound of the range of roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching roles
	 */
	@Override
	public List<Role> findByC_C_C_T(
		long companyId, long classNameId, long[] classPKs, int type, int start,
		int end, OrderByComparator<Role> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C_C_T.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				companyId, classNameId, ArrayUtil.sortedUnique(classPKs), type
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the role where companyId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; or throws a <code>NoSuchRoleException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @return the matching role
	 * @throws NoSuchRoleException if a matching role could not be found
	 */
	@Override
	public Role findByC_C_C_T(
			long companyId, long classNameId, long classPK, int type)
		throws NoSuchRoleException {

		return _uniquePersistenceFinderByC_C_C_T.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId, classPK, type});
	}

	/**
	 * Returns the role where companyId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching role, or <code>null</code> if a matching role could not be found
	 */
	@Override
	public Role fetchByC_C_C_T(
		long companyId, long classNameId, long classPK, int type,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByC_C_C_T.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId, classPK, type},
			useFinderCache);
	}

	/**
	 * Removes the role where companyId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @return the role that was removed
	 */
	@Override
	public Role removeByC_C_C_T(
			long companyId, long classNameId, long classPK, int type)
		throws NoSuchRoleException {

		Role role = findByC_C_C_T(companyId, classNameId, classPK, type);

		return remove(role);
	}

	/**
	 * Returns the number of roles where companyId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @return the number of matching roles
	 */
	@Override
	public int countByC_C_C_T(
		long companyId, long classNameId, long classPK, int type) {

		return _collectionPersistenceFinderByC_C_C_T.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId, new long[] {classPK}, type});
	}

	/**
	 * Returns the number of roles where companyId = &#63; and classNameId = &#63; and classPK = any &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPKs the class pks
	 * @param type the type
	 * @return the number of matching roles
	 */
	@Override
	public int countByC_C_C_T(
		long companyId, long classNameId, long[] classPKs, int type) {

		return _collectionPersistenceFinderByC_C_C_T.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				companyId, classNameId, ArrayUtil.sortedUnique(classPKs), type
			});
	}

	/**
	 * Returns the number of roles that the user has permission to view where companyId = &#63; and classNameId = &#63; and classPK = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param type the type
	 * @return the number of matching roles that the user has permission to view
	 */
	@Override
	public int filterCountByC_C_C_T(
		long companyId, long classNameId, long classPK, int type) {

		return _collectionPersistenceFinderByC_C_C_T.filterCount(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId, new long[] {classPK}, type},
			companyId, 0);
	}

	/**
	 * Returns the number of roles that the user has permission to view where companyId = &#63; and classNameId = &#63; and classPK = any &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPKs the class pks
	 * @param type the type
	 * @return the number of matching roles that the user has permission to view
	 */
	@Override
	public int filterCountByC_C_C_T(
		long companyId, long classNameId, long[] classPKs, int type) {

		return _collectionPersistenceFinderByC_C_C_T.filterCount(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				companyId, classNameId, ArrayUtil.sortedUnique(classPKs), type
			},
			companyId, 0);
	}

	private UniquePersistenceFinder<Role, NoSuchRoleException>
		_uniquePersistenceFinderByERC_C;

	/**
	 * Returns the role where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchRoleException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching role
	 * @throws NoSuchRoleException if a matching role could not be found
	 */
	@Override
	public Role findByERC_C(String externalReferenceCode, long companyId)
		throws NoSuchRoleException {

		return _uniquePersistenceFinderByERC_C.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {externalReferenceCode, companyId});
	}

	/**
	 * Returns the role where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching role, or <code>null</code> if a matching role could not be found
	 */
	@Override
	public Role fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_C.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {externalReferenceCode, companyId}, useFinderCache);
	}

	/**
	 * Removes the role where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the role that was removed
	 */
	@Override
	public Role removeByERC_C(String externalReferenceCode, long companyId)
		throws NoSuchRoleException {

		Role role = findByERC_C(externalReferenceCode, companyId);

		return remove(role);
	}

	/**
	 * Returns the number of roles where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching roles
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		return _uniquePersistenceFinderByERC_C.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {externalReferenceCode, companyId});
	}

	public RolePersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("type", "type_");
		dbColumnNames.put("groups", "groups_");

		setDBColumnNames(dbColumnNames);

		setModelClass(Role.class);

		setModelImplClass(RoleImpl.class);
		setModelPKClass(long.class);

		setTable(RoleTable.INSTANCE);
	}

	/**
	 * Creates a new role with the primary key. Does not add the role to the database.
	 *
	 * @param roleId the primary key for the new role
	 * @return the new role
	 */
	@Override
	public Role create(long roleId) {
		Role role = new RoleImpl();

		role.setNew(true);
		role.setPrimaryKey(roleId);

		String uuid = PortalUUIDUtil.generate();

		role.setUuid(uuid);

		role.setCompanyId(CompanyThreadLocal.getCompanyId());

		return role;
	}

	/**
	 * Removes the role with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param roleId the primary key of the role
	 * @return the role that was removed
	 * @throws NoSuchRoleException if a role with the primary key could not be found
	 */
	@Override
	public Role remove(long roleId) throws NoSuchRoleException {
		return remove((Serializable)roleId);
	}

	@Override
	protected Role removeImpl(Role role) {
		roleToGroupTableMapper.deleteLeftPrimaryKeyTableMappings(
			role.getPrimaryKey());

		roleToUserTableMapper.deleteLeftPrimaryKeyTableMappings(
			role.getPrimaryKey());

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(role)) {
				role = (Role)session.get(
					RoleImpl.class, role.getPrimaryKeyObj());
			}

			if ((role != null) && CTPersistenceHelperUtil.isRemove(role)) {
				session.delete(role);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (role != null) {
			clearCache(role);
		}

		return role;
	}

	@Override
	public Role updateImpl(Role role) {
		boolean isNew = role.isNew();

		if (!(role instanceof RoleModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(role.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(role);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in role proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom Role implementation " +
					role.getClass());
		}

		RoleModelImpl roleModelImpl = (RoleModelImpl)role;

		if (Validator.isNull(role.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			role.setUuid(uuid);
		}

		if (Validator.isNull(role.getExternalReferenceCode())) {
			role.setExternalReferenceCode(role.getUuid());
		}
		else {
			if (!Objects.equals(
					roleModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					role.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = role.getCompanyId();

					long groupId = 0;

					long classPK = 0;

					if (!isNew) {
						classPK = role.getPrimaryKey();
					}

					try {
						role.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								Role.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								role.getExternalReferenceCode(), null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			Role ercRole = fetchByERC_C(
				role.getExternalReferenceCode(), role.getCompanyId());

			if (isNew) {
				if (ercRole != null) {
					throw new DuplicateRoleExternalReferenceCodeException(
						"Duplicate role with external reference code " +
							role.getExternalReferenceCode() + " and company " +
								role.getCompanyId());
				}
			}
			else {
				if ((ercRole != null) &&
					(role.getRoleId() != ercRole.getRoleId())) {

					throw new DuplicateRoleExternalReferenceCodeException(
						"Duplicate role with external reference code " +
							role.getExternalReferenceCode() + " and company " +
								role.getCompanyId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (role.getCreateDate() == null)) {
			if (serviceContext == null) {
				role.setCreateDate(date);
			}
			else {
				role.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!roleModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				role.setModifiedDate(date);
			}
			else {
				role.setModifiedDate(serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (CTPersistenceHelperUtil.isInsert(role)) {
				if (!isNew) {
					session.evict(RoleImpl.class, role.getPrimaryKeyObj());
				}

				session.save(role);
			}
			else {
				role = (Role)session.merge(role);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(role, false);

		if (isNew) {
			role.setNew(false);
		}

		role.resetOriginalValues();

		return role;
	}

	/**
	 * Returns the role with the primary key or throws a <code>NoSuchRoleException</code> if it could not be found.
	 *
	 * @param roleId the primary key of the role
	 * @return the role
	 * @throws NoSuchRoleException if a role with the primary key could not be found
	 */
	@Override
	public Role findByPrimaryKey(long roleId) throws NoSuchRoleException {
		return findByPrimaryKey((Serializable)roleId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return CTPersistenceHelperUtil.getCTPersistenceHelper();
	}

	/**
	 * Returns the role with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param roleId the primary key of the role
	 * @return the role, or <code>null</code> if a role with the primary key could not be found
	 */
	@Override
	public Role fetchByPrimaryKey(long roleId) {
		return fetchByPrimaryKey((Serializable)roleId);
	}

	/**
	 * Returns the primaryKeys of groups associated with the role.
	 *
	 * @param pk the primary key of the role
	 * @return long[] of the primaryKeys of groups associated with the role
	 */
	@Override
	public long[] getGroupPrimaryKeys(long pk) {
		long[] pks = roleToGroupTableMapper.getRightPrimaryKeys(pk);

		return pks.clone();
	}

	/**
	 * Returns all the groups associated with the role.
	 *
	 * @param pk the primary key of the role
	 * @return the groups associated with the role
	 */
	@Override
	public List<com.liferay.portal.kernel.model.Group> getGroups(long pk) {
		return getGroups(pk, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	/**
	 * Returns a range of all the groups associated with the role.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RoleModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the role
	 * @param start the lower bound of the range of roles
	 * @param end the upper bound of the range of roles (not inclusive)
	 * @return the range of groups associated with the role
	 */
	@Override
	public List<com.liferay.portal.kernel.model.Group> getGroups(
		long pk, int start, int end) {

		return getGroups(pk, start, end, null);
	}

	/**
	 * Returns an ordered range of all the groups associated with the role.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RoleModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the role
	 * @param start the lower bound of the range of roles
	 * @param end the upper bound of the range of roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of groups associated with the role
	 */
	@Override
	public List<com.liferay.portal.kernel.model.Group> getGroups(
		long pk, int start, int end,
		OrderByComparator<com.liferay.portal.kernel.model.Group>
			orderByComparator) {

		return roleToGroupTableMapper.getRightBaseModels(
			pk, start, end, orderByComparator);
	}

	/**
	 * Returns the number of groups associated with the role.
	 *
	 * @param pk the primary key of the role
	 * @return the number of groups associated with the role
	 */
	@Override
	public int getGroupsSize(long pk) {
		long[] pks = roleToGroupTableMapper.getRightPrimaryKeys(pk);

		return pks.length;
	}

	/**
	 * Returns <code>true</code> if the group is associated with the role.
	 *
	 * @param pk the primary key of the role
	 * @param groupPK the primary key of the group
	 * @return <code>true</code> if the group is associated with the role; <code>false</code> otherwise
	 */
	@Override
	public boolean containsGroup(long pk, long groupPK) {
		return roleToGroupTableMapper.containsTableMapping(pk, groupPK);
	}

	/**
	 * Returns <code>true</code> if the role has any groups associated with it.
	 *
	 * @param pk the primary key of the role to check for associations with groups
	 * @return <code>true</code> if the role has any groups associated with it; <code>false</code> otherwise
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
	 * Adds an association between the role and the group. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the role
	 * @param groupPK the primary key of the group
	 * @return <code>true</code> if an association between the role and the group was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addGroup(long pk, long groupPK) {
		Role role = fetchByPrimaryKey(pk);

		if (role == null) {
			return roleToGroupTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk, groupPK);
		}
		else {
			return roleToGroupTableMapper.addTableMapping(
				role.getCompanyId(), pk, groupPK);
		}
	}

	/**
	 * Adds an association between the role and the group. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the role
	 * @param group the group
	 * @return <code>true</code> if an association between the role and the group was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addGroup(
		long pk, com.liferay.portal.kernel.model.Group group) {

		Role role = fetchByPrimaryKey(pk);

		if (role == null) {
			return roleToGroupTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk, group.getPrimaryKey());
		}
		else {
			return roleToGroupTableMapper.addTableMapping(
				role.getCompanyId(), pk, group.getPrimaryKey());
		}
	}

	/**
	 * Adds an association between the role and the groups. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the role
	 * @param groupPKs the primary keys of the groups
	 * @return <code>true</code> if at least one association between the role and the groups was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addGroups(long pk, long[] groupPKs) {
		long companyId = 0;

		Role role = fetchByPrimaryKey(pk);

		if (role == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = role.getCompanyId();
		}

		long[] addedKeys = roleToGroupTableMapper.addTableMappings(
			companyId, pk, groupPKs);

		if (addedKeys.length > 0) {
			return true;
		}

		return false;
	}

	/**
	 * Adds an association between the role and the groups. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the role
	 * @param groups the groups
	 * @return <code>true</code> if at least one association between the role and the groups was added; <code>false</code> if they were all already associated
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
	 * Clears all associations between the role and its groups. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the role to clear the associated groups from
	 */
	@Override
	public void clearGroups(long pk) {
		roleToGroupTableMapper.deleteLeftPrimaryKeyTableMappings(pk);
	}

	/**
	 * Removes the association between the role and the group. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the role
	 * @param groupPK the primary key of the group
	 */
	@Override
	public void removeGroup(long pk, long groupPK) {
		roleToGroupTableMapper.deleteTableMapping(pk, groupPK);
	}

	/**
	 * Removes the association between the role and the group. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the role
	 * @param group the group
	 */
	@Override
	public void removeGroup(
		long pk, com.liferay.portal.kernel.model.Group group) {

		roleToGroupTableMapper.deleteTableMapping(pk, group.getPrimaryKey());
	}

	/**
	 * Removes the association between the role and the groups. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the role
	 * @param groupPKs the primary keys of the groups
	 */
	@Override
	public void removeGroups(long pk, long[] groupPKs) {
		roleToGroupTableMapper.deleteTableMappings(pk, groupPKs);
	}

	/**
	 * Removes the association between the role and the groups. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the role
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
	 * Sets the groups associated with the role, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the role
	 * @param groupPKs the primary keys of the groups to be associated with the role
	 */
	@Override
	public void setGroups(long pk, long[] groupPKs) {
		Set<Long> newGroupPKsSet = SetUtil.fromArray(groupPKs);
		Set<Long> oldGroupPKsSet = SetUtil.fromArray(
			roleToGroupTableMapper.getRightPrimaryKeys(pk));

		Set<Long> removeGroupPKsSet = new HashSet<Long>(oldGroupPKsSet);

		removeGroupPKsSet.removeAll(newGroupPKsSet);

		roleToGroupTableMapper.deleteTableMappings(
			pk, ArrayUtil.toLongArray(removeGroupPKsSet));

		newGroupPKsSet.removeAll(oldGroupPKsSet);

		long companyId = 0;

		Role role = fetchByPrimaryKey(pk);

		if (role == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = role.getCompanyId();
		}

		roleToGroupTableMapper.addTableMappings(
			companyId, pk, ArrayUtil.toLongArray(newGroupPKsSet));
	}

	/**
	 * Sets the groups associated with the role, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the role
	 * @param groups the groups to be associated with the role
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
	 * Returns the primaryKeys of users associated with the role.
	 *
	 * @param pk the primary key of the role
	 * @return long[] of the primaryKeys of users associated with the role
	 */
	@Override
	public long[] getUserPrimaryKeys(long pk) {
		long[] pks = roleToUserTableMapper.getRightPrimaryKeys(pk);

		return pks.clone();
	}

	/**
	 * Returns all the users associated with the role.
	 *
	 * @param pk the primary key of the role
	 * @return the users associated with the role
	 */
	@Override
	public List<com.liferay.portal.kernel.model.User> getUsers(long pk) {
		return getUsers(pk, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	/**
	 * Returns a range of all the users associated with the role.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RoleModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the role
	 * @param start the lower bound of the range of roles
	 * @param end the upper bound of the range of roles (not inclusive)
	 * @return the range of users associated with the role
	 */
	@Override
	public List<com.liferay.portal.kernel.model.User> getUsers(
		long pk, int start, int end) {

		return getUsers(pk, start, end, null);
	}

	/**
	 * Returns an ordered range of all the users associated with the role.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RoleModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the role
	 * @param start the lower bound of the range of roles
	 * @param end the upper bound of the range of roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of users associated with the role
	 */
	@Override
	public List<com.liferay.portal.kernel.model.User> getUsers(
		long pk, int start, int end,
		OrderByComparator<com.liferay.portal.kernel.model.User>
			orderByComparator) {

		return roleToUserTableMapper.getRightBaseModels(
			pk, start, end, orderByComparator);
	}

	/**
	 * Returns the number of users associated with the role.
	 *
	 * @param pk the primary key of the role
	 * @return the number of users associated with the role
	 */
	@Override
	public int getUsersSize(long pk) {
		long[] pks = roleToUserTableMapper.getRightPrimaryKeys(pk);

		return pks.length;
	}

	/**
	 * Returns <code>true</code> if the user is associated with the role.
	 *
	 * @param pk the primary key of the role
	 * @param userPK the primary key of the user
	 * @return <code>true</code> if the user is associated with the role; <code>false</code> otherwise
	 */
	@Override
	public boolean containsUser(long pk, long userPK) {
		return roleToUserTableMapper.containsTableMapping(pk, userPK);
	}

	/**
	 * Returns <code>true</code> if the role has any users associated with it.
	 *
	 * @param pk the primary key of the role to check for associations with users
	 * @return <code>true</code> if the role has any users associated with it; <code>false</code> otherwise
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
	 * Adds an association between the role and the user. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the role
	 * @param userPK the primary key of the user
	 * @return <code>true</code> if an association between the role and the user was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addUser(long pk, long userPK) {
		Role role = fetchByPrimaryKey(pk);

		if (role == null) {
			return roleToUserTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk, userPK);
		}
		else {
			return roleToUserTableMapper.addTableMapping(
				role.getCompanyId(), pk, userPK);
		}
	}

	/**
	 * Adds an association between the role and the user. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the role
	 * @param user the user
	 * @return <code>true</code> if an association between the role and the user was added; <code>false</code> if they were already associated
	 */
	@Override
	public boolean addUser(long pk, com.liferay.portal.kernel.model.User user) {
		Role role = fetchByPrimaryKey(pk);

		if (role == null) {
			return roleToUserTableMapper.addTableMapping(
				CompanyThreadLocal.getCompanyId(), pk, user.getPrimaryKey());
		}
		else {
			return roleToUserTableMapper.addTableMapping(
				role.getCompanyId(), pk, user.getPrimaryKey());
		}
	}

	/**
	 * Adds an association between the role and the users. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the role
	 * @param userPKs the primary keys of the users
	 * @return <code>true</code> if at least one association between the role and the users was added; <code>false</code> if they were all already associated
	 */
	@Override
	public boolean addUsers(long pk, long[] userPKs) {
		long companyId = 0;

		Role role = fetchByPrimaryKey(pk);

		if (role == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = role.getCompanyId();
		}

		long[] addedKeys = roleToUserTableMapper.addTableMappings(
			companyId, pk, userPKs);

		if (addedKeys.length > 0) {
			return true;
		}

		return false;
	}

	/**
	 * Adds an association between the role and the users. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the role
	 * @param users the users
	 * @return <code>true</code> if at least one association between the role and the users was added; <code>false</code> if they were all already associated
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
	 * Clears all associations between the role and its users. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the role to clear the associated users from
	 */
	@Override
	public void clearUsers(long pk) {
		roleToUserTableMapper.deleteLeftPrimaryKeyTableMappings(pk);
	}

	/**
	 * Removes the association between the role and the user. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the role
	 * @param userPK the primary key of the user
	 */
	@Override
	public void removeUser(long pk, long userPK) {
		roleToUserTableMapper.deleteTableMapping(pk, userPK);
	}

	/**
	 * Removes the association between the role and the user. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the role
	 * @param user the user
	 */
	@Override
	public void removeUser(long pk, com.liferay.portal.kernel.model.User user) {
		roleToUserTableMapper.deleteTableMapping(pk, user.getPrimaryKey());
	}

	/**
	 * Removes the association between the role and the users. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the role
	 * @param userPKs the primary keys of the users
	 */
	@Override
	public void removeUsers(long pk, long[] userPKs) {
		roleToUserTableMapper.deleteTableMappings(pk, userPKs);
	}

	/**
	 * Removes the association between the role and the users. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the role
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
	 * Sets the users associated with the role, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the role
	 * @param userPKs the primary keys of the users to be associated with the role
	 */
	@Override
	public void setUsers(long pk, long[] userPKs) {
		Set<Long> newUserPKsSet = SetUtil.fromArray(userPKs);
		Set<Long> oldUserPKsSet = SetUtil.fromArray(
			roleToUserTableMapper.getRightPrimaryKeys(pk));

		Set<Long> removeUserPKsSet = new HashSet<Long>(oldUserPKsSet);

		removeUserPKsSet.removeAll(newUserPKsSet);

		roleToUserTableMapper.deleteTableMappings(
			pk, ArrayUtil.toLongArray(removeUserPKsSet));

		newUserPKsSet.removeAll(oldUserPKsSet);

		long companyId = 0;

		Role role = fetchByPrimaryKey(pk);

		if (role == null) {
			companyId = CompanyThreadLocal.getCompanyId();
		}
		else {
			companyId = role.getCompanyId();
		}

		roleToUserTableMapper.addTableMappings(
			companyId, pk, ArrayUtil.toLongArray(newUserPKsSet));
	}

	/**
	 * Sets the users associated with the role, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the role
	 * @param users the users to be associated with the role
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
		return "roleId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_ROLE_;
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
		return RoleModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "Role_";
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
		ctStrictColumnNames.add("classNameId");
		ctStrictColumnNames.add("classPK");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("title");
		ctMergeColumnNames.add("description");
		ctMergeColumnNames.add("type_");
		ctMergeColumnNames.add("subtype");
		ctMergeColumnNames.add("status");
		ctMergeColumnNames.add("groups_");
		ctMergeColumnNames.add("users");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("roleId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_mappingTableNames.add("Groups_Roles");
		_mappingTableNames.add("Users_Roles");

		_uniqueIndexColumnNames.add(new String[] {"companyId", "name"});

		_uniqueIndexColumnNames.add(
			new String[] {"companyId", "classNameId", "classPK"});

		_uniqueIndexColumnNames.add(
			new String[] {"companyId", "classNameId", "classPK", "type_"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "companyId"});
	}

	/**
	 * Initializes the role persistence.
	 */
	public void afterPropertiesSet() {
		roleToGroupTableMapper = TableMapperFactory.getTableMapper(
			"Groups_Roles", "companyId", "roleId", "groupId", this,
			groupPersistence);

		roleToUserTableMapper = TableMapperFactory.getTableMapper(
			"Users_Roles", "companyId", "roleId", "userId", this,
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
				_SQL_SELECT_ROLE__WHERE, _SQL_COUNT_ROLE__WHERE,
				RoleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"role_.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
					true, true, Role::getUuid));

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
				_SQL_SELECT_ROLE__WHERE, _SQL_COUNT_ROLE__WHERE,
				RoleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"role_.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
					true, true, Role::getUuid),
				new FinderColumn<>(
					"role_.", "companyId", FinderColumn.Type.LONG, "=", true,
					true, Role::getCompanyId));

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
				_SQL_SELECT_ROLE__WHERE, _SQL_COUNT_ROLE__WHERE,
				RoleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"role_.", "companyId", FinderColumn.Type.LONG, "=", true,
					true, Role::getCompanyId));

		_collectionPersistenceFinderByName =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByName",
					new String[] {
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"name"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByName",
					new String[] {String.class.getName()},
					new String[] {"name"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByName",
					new String[] {String.class.getName()},
					new String[] {"name"}, 0, 1, false, null),
				_SQL_SELECT_ROLE__WHERE, _SQL_COUNT_ROLE__WHERE,
				RoleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"role_.", "name", FinderColumn.Type.STRING, "=", true, true,
					Role::getName));

		_collectionPersistenceFinderByType =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByType",
					new String[] {
						Integer.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"type_"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByType",
					new String[] {Integer.class.getName()},
					new String[] {"type_"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByType",
					new String[] {Integer.class.getName()},
					new String[] {"type_"}, false),
				_SQL_SELECT_ROLE__WHERE, _SQL_COUNT_ROLE__WHERE,
				RoleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"role_.", "type", "type_", FinderColumn.Type.INTEGER, "=",
					true, true, Role::getType));

		_collectionPersistenceFinderBySubtype =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findBySubtype",
					new String[] {
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"subtype"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findBySubtype",
					new String[] {String.class.getName()},
					new String[] {"subtype"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countBySubtype",
					new String[] {String.class.getName()},
					new String[] {"subtype"}, 0, 1, false, null),
				_SQL_SELECT_ROLE__WHERE, _SQL_COUNT_ROLE__WHERE,
				RoleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"role_.", "subtype", FinderColumn.Type.STRING, "=", true,
					true, Role::getSubtype));

		_uniquePersistenceFinderByC_N = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_N",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "name"}, 2, 2, true,
				Role::getCompanyId, convertCaseFunction(Role::getName)),
			_SQL_SELECT_ROLE__WHERE, "",
			new FinderColumn<>(
				"role_.", "companyId", FinderColumn.Type.LONG, "=", true, true,
				Role::getCompanyId),
			new FinderColumn<>(
				"role_.", "name", FinderColumn.Type.STRING, "=", false, true,
				Role::getName));

		_collectionPersistenceFinderByC_T =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_T",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId", "type_"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_T",
					new String[] {
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {"companyId", "type_"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_T",
					new String[] {
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {"companyId", "type_"}, false),
				_SQL_SELECT_ROLE__WHERE, _SQL_COUNT_ROLE__WHERE,
				RoleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"role_.", "companyId", FinderColumn.Type.LONG, "=", true,
					true, Role::getCompanyId),
				new ArrayableFinderColumn<>(
					"role_.", "type", "type_", FinderColumn.Type.INTEGER, "=",
					false, true, true, Role::getType));

		_collectionPersistenceFinderByT_S =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByT_S",
					new String[] {
						Integer.class.getName(), String.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"type_", "subtype"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByT_S",
					new String[] {
						Integer.class.getName(), String.class.getName()
					},
					new String[] {"type_", "subtype"}, 0, 2, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByT_S",
					new String[] {
						Integer.class.getName(), String.class.getName()
					},
					new String[] {"type_", "subtype"}, 0, 2, false, null),
				_SQL_SELECT_ROLE__WHERE, _SQL_COUNT_ROLE__WHERE,
				RoleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"role_.", "type", "type_", FinderColumn.Type.INTEGER, "=",
					true, true, Role::getType),
				new FinderColumn<>(
					"role_.", "subtype", FinderColumn.Type.STRING, "=", true,
					true, Role::getSubtype));

		_uniquePersistenceFinderByC_C_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_C_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {"companyId", "classNameId", "classPK"}, 0, 0,
				false, Role::getCompanyId, Role::getClassNameId,
				Role::getClassPK),
			_SQL_SELECT_ROLE__WHERE, "",
			new FinderColumn<>(
				"role_.", "companyId", FinderColumn.Type.LONG, "=", true, true,
				Role::getCompanyId),
			new FinderColumn<>(
				"role_.", "classNameId", FinderColumn.Type.LONG, "=", true,
				true, Role::getClassNameId),
			new FinderColumn<>(
				"role_.", "classPK", FinderColumn.Type.LONG, "=", true, true,
				Role::getClassPK));

		_collectionPersistenceFinderByC_C_C =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C_C",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId", "classNameId", "classPK"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C_C",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName()
					},
					new String[] {"companyId", "classNameId", "classPK"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_C_C",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName()
					},
					new String[] {"companyId", "classNameId", "classPK"},
					false),
				_SQL_SELECT_ROLE__WHERE, _SQL_COUNT_ROLE__WHERE,
				RoleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				_uniquePersistenceFinderByC_C_C,
				new FinderColumn<>(
					"role_.", "companyId", FinderColumn.Type.LONG, "=", true,
					true, Role::getCompanyId),
				new FinderColumn<>(
					"role_.", "classNameId", FinderColumn.Type.LONG, "=", true,
					true, Role::getClassNameId),
				new ArrayableFinderColumn<>(
					"role_.", "classPK", FinderColumn.Type.LONG, "=", false,
					true, true, Role::getClassPK));

		_uniquePersistenceFinderByC_C_C_T = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_C_C_T",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName(), Integer.class.getName()
				},
				new String[] {"companyId", "classNameId", "classPK", "type_"},
				0, 0, false, Role::getCompanyId, Role::getClassNameId,
				Role::getClassPK, Role::getType),
			_SQL_SELECT_ROLE__WHERE, "",
			new FinderColumn<>(
				"role_.", "companyId", FinderColumn.Type.LONG, "=", true, true,
				Role::getCompanyId),
			new FinderColumn<>(
				"role_.", "classNameId", FinderColumn.Type.LONG, "=", true,
				true, Role::getClassNameId),
			new FinderColumn<>(
				"role_.", "classPK", FinderColumn.Type.LONG, "=", true, true,
				Role::getClassPK),
			new FinderColumn<>(
				"role_.", "type", "type_", FinderColumn.Type.INTEGER, "=", true,
				true, Role::getType));

		_collectionPersistenceFinderByC_C_C_T =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C_C_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"companyId", "classNameId", "classPK", "type_"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C_C_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {
						"companyId", "classNameId", "classPK", "type_"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_C_C_T",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Integer.class.getName()
					},
					new String[] {
						"companyId", "classNameId", "classPK", "type_"
					},
					false),
				_SQL_SELECT_ROLE__WHERE, _SQL_COUNT_ROLE__WHERE,
				RoleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				_uniquePersistenceFinderByC_C_C_T,
				new FinderColumn<>(
					"role_.", "companyId", FinderColumn.Type.LONG, "=", true,
					true, Role::getCompanyId),
				new FinderColumn<>(
					"role_.", "classNameId", FinderColumn.Type.LONG, "=", true,
					true, Role::getClassNameId),
				new ArrayableFinderColumn<>(
					"role_.", "classPK", FinderColumn.Type.LONG, "=", false,
					true, true, Role::getClassPK),
				new FinderColumn<>(
					"role_.", "type", "type_", FinderColumn.Type.INTEGER, "=",
					true, true, Role::getType));

		_uniquePersistenceFinderByERC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "companyId"}, 0, 1,
				false, convertNullFunction(Role::getExternalReferenceCode),
				Role::getCompanyId),
			_SQL_SELECT_ROLE__WHERE, "",
			new FinderColumn<>(
				"role_.", "externalReferenceCode", FinderColumn.Type.STRING,
				"=", true, true, Role::getExternalReferenceCode),
			new FinderColumn<>(
				"role_.", "companyId", FinderColumn.Type.LONG, "=", true, true,
				Role::getCompanyId));

		RoleUtil.setPersistence(this);
	}

	public void destroy() {
		RoleUtil.setPersistence(null);

		EntityCacheUtil.removeCache(RoleImpl.class.getName());

		TableMapperFactory.removeTableMapper("Groups_Roles");
		TableMapperFactory.removeTableMapper("Users_Roles");
	}

	@BeanReference(type = GroupPersistence.class)
	protected GroupPersistence groupPersistence;

	protected TableMapper<Role, com.liferay.portal.kernel.model.Group>
		roleToGroupTableMapper;

	@BeanReference(type = UserPersistence.class)
	protected UserPersistence userPersistence;

	protected TableMapper<Role, com.liferay.portal.kernel.model.User>
		roleToUserTableMapper;

	private static final String _ENTITY_ALIAS_PREFIX =
		RoleModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_ROLE_ =
		"SELECT role_ FROM Role role_";

	private static final String _SQL_SELECT_ROLE__WHERE =
		"SELECT role_ FROM Role role_ WHERE ";

	private static final String _SQL_COUNT_ROLE__WHERE =
		"SELECT COUNT(role_) FROM Role role_ WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "type", "groups"});

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1076464501