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
import com.liferay.portal.kernel.exception.NoSuchLayoutPrototypeException;
import com.liferay.portal.kernel.model.LayoutPrototype;
import com.liferay.portal.kernel.model.LayoutPrototypeTable;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.LayoutPrototypePersistence;
import com.liferay.portal.kernel.service.persistence.LayoutPrototypeUtil;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelperUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.FilterCollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.model.impl.LayoutPrototypeImpl;
import com.liferay.portal.model.impl.LayoutPrototypeModelImpl;

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
 * The persistence implementation for the layout prototype service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class LayoutPrototypePersistenceImpl
	extends BasePersistenceImpl<LayoutPrototype, NoSuchLayoutPrototypeException>
	implements LayoutPrototypePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>LayoutPrototypeUtil</code> to access the layout prototype persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		LayoutPrototypeImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FilterCollectionPersistenceFinder
		<LayoutPrototype, NoSuchLayoutPrototypeException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the layout prototypes where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPrototypeModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout prototypes
	 * @param end the upper bound of the range of layout prototypes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout prototypes
	 */
	@Override
	public List<LayoutPrototype> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<LayoutPrototype> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout prototype in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout prototype
	 * @throws NoSuchLayoutPrototypeException if a matching layout prototype could not be found
	 */
	@Override
	public LayoutPrototype findByUuid_First(
			String uuid, OrderByComparator<LayoutPrototype> orderByComparator)
		throws NoSuchLayoutPrototypeException {

		return _collectionPersistenceFinderByUuid.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid},
			orderByComparator);
	}

	/**
	 * Returns the first layout prototype in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout prototype, or <code>null</code> if a matching layout prototype could not be found
	 */
	@Override
	public LayoutPrototype fetchByUuid_First(
		String uuid, OrderByComparator<LayoutPrototype> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout prototypes that the user has permissions to view where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPrototypeModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout prototypes
	 * @param end the upper bound of the range of layout prototypes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout prototypes that the user has permission to view
	 */
	@Override
	public List<LayoutPrototype> filterFindByUuid(
		String uuid, int start, int end,
		OrderByComparator<LayoutPrototype> orderByComparator) {

		return _collectionPersistenceFinderByUuid.filterFind(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid}, start, end,
			orderByComparator);
	}

	/**
	 * Removes all the layout prototypes where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	/**
	 * Returns the number of layout prototypes where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching layout prototypes
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	/**
	 * Returns the number of layout prototypes that the user has permission to view where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching layout prototypes that the user has permission to view
	 */
	@Override
	public int filterCountByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.filterCount(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	private FilterCollectionPersistenceFinder
		<LayoutPrototype, NoSuchLayoutPrototypeException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the layout prototypes where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPrototypeModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout prototypes
	 * @param end the upper bound of the range of layout prototypes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout prototypes
	 */
	@Override
	public List<LayoutPrototype> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<LayoutPrototype> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout prototype in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout prototype
	 * @throws NoSuchLayoutPrototypeException if a matching layout prototype could not be found
	 */
	@Override
	public LayoutPrototype findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<LayoutPrototype> orderByComparator)
		throws NoSuchLayoutPrototypeException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			orderByComparator);
	}

	/**
	 * Returns the first layout prototype in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout prototype, or <code>null</code> if a matching layout prototype could not be found
	 */
	@Override
	public LayoutPrototype fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<LayoutPrototype> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout prototypes that the user has permissions to view where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPrototypeModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout prototypes
	 * @param end the upper bound of the range of layout prototypes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout prototypes that the user has permission to view
	 */
	@Override
	public List<LayoutPrototype> filterFindByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<LayoutPrototype> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.filterFind(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			start, end, orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the layout prototypes where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of layout prototypes where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching layout prototypes
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId});
	}

	/**
	 * Returns the number of layout prototypes that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching layout prototypes that the user has permission to view
	 */
	@Override
	public int filterCountByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.filterCount(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			companyId, 0);
	}

	private FilterCollectionPersistenceFinder
		<LayoutPrototype, NoSuchLayoutPrototypeException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the layout prototypes where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPrototypeModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout prototypes
	 * @param end the upper bound of the range of layout prototypes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout prototypes
	 */
	@Override
	public List<LayoutPrototype> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<LayoutPrototype> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout prototype in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout prototype
	 * @throws NoSuchLayoutPrototypeException if a matching layout prototype could not be found
	 */
	@Override
	public LayoutPrototype findByCompanyId_First(
			long companyId,
			OrderByComparator<LayoutPrototype> orderByComparator)
		throws NoSuchLayoutPrototypeException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			orderByComparator);
	}

	/**
	 * Returns the first layout prototype in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout prototype, or <code>null</code> if a matching layout prototype could not be found
	 */
	@Override
	public LayoutPrototype fetchByCompanyId_First(
		long companyId, OrderByComparator<LayoutPrototype> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout prototypes that the user has permissions to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPrototypeModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout prototypes
	 * @param end the upper bound of the range of layout prototypes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout prototypes that the user has permission to view
	 */
	@Override
	public List<LayoutPrototype> filterFindByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<LayoutPrototype> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.filterFind(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId}, start,
			end, orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the layout prototypes where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId});
	}

	/**
	 * Returns the number of layout prototypes where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching layout prototypes
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId});
	}

	/**
	 * Returns the number of layout prototypes that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching layout prototypes that the user has permission to view
	 */
	@Override
	public int filterCountByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.filterCount(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			companyId, 0);
	}

	private FilterCollectionPersistenceFinder
		<LayoutPrototype, NoSuchLayoutPrototypeException>
			_collectionPersistenceFinderByC_A;

	/**
	 * Returns an ordered range of all the layout prototypes where companyId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPrototypeModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param start the lower bound of the range of layout prototypes
	 * @param end the upper bound of the range of layout prototypes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout prototypes
	 */
	@Override
	public List<LayoutPrototype> findByC_A(
		long companyId, boolean active, int start, int end,
		OrderByComparator<LayoutPrototype> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_A.find(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, active},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout prototype in the ordered set where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout prototype
	 * @throws NoSuchLayoutPrototypeException if a matching layout prototype could not be found
	 */
	@Override
	public LayoutPrototype findByC_A_First(
			long companyId, boolean active,
			OrderByComparator<LayoutPrototype> orderByComparator)
		throws NoSuchLayoutPrototypeException {

		return _collectionPersistenceFinderByC_A.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, active},
			orderByComparator);
	}

	/**
	 * Returns the first layout prototype in the ordered set where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout prototype, or <code>null</code> if a matching layout prototype could not be found
	 */
	@Override
	public LayoutPrototype fetchByC_A_First(
		long companyId, boolean active,
		OrderByComparator<LayoutPrototype> orderByComparator) {

		return _collectionPersistenceFinderByC_A.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, active},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout prototypes that the user has permissions to view where companyId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutPrototypeModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param start the lower bound of the range of layout prototypes
	 * @param end the upper bound of the range of layout prototypes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout prototypes that the user has permission to view
	 */
	@Override
	public List<LayoutPrototype> filterFindByC_A(
		long companyId, boolean active, int start, int end,
		OrderByComparator<LayoutPrototype> orderByComparator) {

		return _collectionPersistenceFinderByC_A.filterFind(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, active},
			start, end, orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the layout prototypes where companyId = &#63; and active = &#63; from the database.
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
	 * Returns the number of layout prototypes where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @return the number of matching layout prototypes
	 */
	@Override
	public int countByC_A(long companyId, boolean active) {
		return _collectionPersistenceFinderByC_A.count(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, active});
	}

	/**
	 * Returns the number of layout prototypes that the user has permission to view where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @return the number of matching layout prototypes that the user has permission to view
	 */
	@Override
	public int filterCountByC_A(long companyId, boolean active) {
		return _collectionPersistenceFinderByC_A.filterCount(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, active},
			companyId, 0);
	}

	public LayoutPrototypePersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("settings", "settings_");
		dbColumnNames.put("active", "active_");

		setDBColumnNames(dbColumnNames);

		setModelClass(LayoutPrototype.class);

		setModelImplClass(LayoutPrototypeImpl.class);
		setModelPKClass(long.class);

		setTable(LayoutPrototypeTable.INSTANCE);
	}

	/**
	 * Creates a new layout prototype with the primary key. Does not add the layout prototype to the database.
	 *
	 * @param layoutPrototypeId the primary key for the new layout prototype
	 * @return the new layout prototype
	 */
	@Override
	public LayoutPrototype create(long layoutPrototypeId) {
		LayoutPrototype layoutPrototype = new LayoutPrototypeImpl();

		layoutPrototype.setNew(true);
		layoutPrototype.setPrimaryKey(layoutPrototypeId);

		String uuid = PortalUUIDUtil.generate();

		layoutPrototype.setUuid(uuid);

		layoutPrototype.setCompanyId(CompanyThreadLocal.getCompanyId());

		return layoutPrototype;
	}

	/**
	 * Removes the layout prototype with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param layoutPrototypeId the primary key of the layout prototype
	 * @return the layout prototype that was removed
	 * @throws NoSuchLayoutPrototypeException if a layout prototype with the primary key could not be found
	 */
	@Override
	public LayoutPrototype remove(long layoutPrototypeId)
		throws NoSuchLayoutPrototypeException {

		return remove((Serializable)layoutPrototypeId);
	}

	@Override
	protected LayoutPrototype removeImpl(LayoutPrototype layoutPrototype) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(layoutPrototype)) {
				layoutPrototype = (LayoutPrototype)session.get(
					LayoutPrototypeImpl.class,
					layoutPrototype.getPrimaryKeyObj());
			}

			if ((layoutPrototype != null) &&
				CTPersistenceHelperUtil.isRemove(layoutPrototype)) {

				session.delete(layoutPrototype);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (layoutPrototype != null) {
			clearCache(layoutPrototype);
		}

		return layoutPrototype;
	}

	@Override
	public LayoutPrototype updateImpl(LayoutPrototype layoutPrototype) {
		boolean isNew = layoutPrototype.isNew();

		if (!(layoutPrototype instanceof LayoutPrototypeModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(layoutPrototype.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					layoutPrototype);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in layoutPrototype proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom LayoutPrototype implementation " +
					layoutPrototype.getClass());
		}

		LayoutPrototypeModelImpl layoutPrototypeModelImpl =
			(LayoutPrototypeModelImpl)layoutPrototype;

		if (Validator.isNull(layoutPrototype.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			layoutPrototype.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (layoutPrototype.getCreateDate() == null)) {
			if (serviceContext == null) {
				layoutPrototype.setCreateDate(date);
			}
			else {
				layoutPrototype.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!layoutPrototypeModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				layoutPrototype.setModifiedDate(date);
			}
			else {
				layoutPrototype.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (CTPersistenceHelperUtil.isInsert(layoutPrototype)) {
				if (!isNew) {
					session.evict(
						LayoutPrototypeImpl.class,
						layoutPrototype.getPrimaryKeyObj());
				}

				session.save(layoutPrototype);
			}
			else {
				layoutPrototype = (LayoutPrototype)session.merge(
					layoutPrototype);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(layoutPrototype, false);

		if (isNew) {
			layoutPrototype.setNew(false);
		}

		layoutPrototype.resetOriginalValues();

		return layoutPrototype;
	}

	/**
	 * Returns the layout prototype with the primary key or throws a <code>NoSuchLayoutPrototypeException</code> if it could not be found.
	 *
	 * @param layoutPrototypeId the primary key of the layout prototype
	 * @return the layout prototype
	 * @throws NoSuchLayoutPrototypeException if a layout prototype with the primary key could not be found
	 */
	@Override
	public LayoutPrototype findByPrimaryKey(long layoutPrototypeId)
		throws NoSuchLayoutPrototypeException {

		return findByPrimaryKey((Serializable)layoutPrototypeId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return CTPersistenceHelperUtil.getCTPersistenceHelper();
	}

	/**
	 * Returns the layout prototype with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param layoutPrototypeId the primary key of the layout prototype
	 * @return the layout prototype, or <code>null</code> if a layout prototype with the primary key could not be found
	 */
	@Override
	public LayoutPrototype fetchByPrimaryKey(long layoutPrototypeId) {
		return fetchByPrimaryKey((Serializable)layoutPrototypeId);
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
		return "layoutPrototypeId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_LAYOUTPROTOTYPE;
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
		return LayoutPrototypeModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "LayoutPrototype";
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
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("description");
		ctMergeColumnNames.add("settings_");
		ctMergeColumnNames.add("active_");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("layoutPrototypeId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);
	}

	/**
	 * Initializes the layout prototype persistence.
	 */
	public void afterPropertiesSet() {
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
				_SQL_SELECT_LAYOUTPROTOTYPE_WHERE,
				_SQL_COUNT_LAYOUTPROTOTYPE_WHERE,
				LayoutPrototypeModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"layoutPrototype.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					LayoutPrototype::getUuid));

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
				_SQL_SELECT_LAYOUTPROTOTYPE_WHERE,
				_SQL_COUNT_LAYOUTPROTOTYPE_WHERE,
				LayoutPrototypeModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"layoutPrototype.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					LayoutPrototype::getUuid),
				new FinderColumn<>(
					"layoutPrototype.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, LayoutPrototype::getCompanyId));

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
				_SQL_SELECT_LAYOUTPROTOTYPE_WHERE,
				_SQL_COUNT_LAYOUTPROTOTYPE_WHERE,
				LayoutPrototypeModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"layoutPrototype.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, LayoutPrototype::getCompanyId));

		_collectionPersistenceFinderByC_A =
			new FilterCollectionPersistenceFinder<>(
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
					new String[] {
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"companyId", "active_"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_A",
					new String[] {
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"companyId", "active_"}, false),
				_SQL_SELECT_LAYOUTPROTOTYPE_WHERE,
				_SQL_COUNT_LAYOUTPROTOTYPE_WHERE,
				LayoutPrototypeModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"layoutPrototype.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, LayoutPrototype::getCompanyId),
				new FinderColumn<>(
					"layoutPrototype.", "active", "active_",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					LayoutPrototype::isActive));

		LayoutPrototypeUtil.setPersistence(this);
	}

	public void destroy() {
		LayoutPrototypeUtil.setPersistence(null);

		EntityCacheUtil.removeCache(LayoutPrototypeImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		LayoutPrototypeModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_LAYOUTPROTOTYPE =
		"SELECT layoutPrototype FROM LayoutPrototype layoutPrototype";

	private static final String _SQL_SELECT_LAYOUTPROTOTYPE_WHERE =
		"SELECT layoutPrototype FROM LayoutPrototype layoutPrototype WHERE ";

	private static final String _SQL_COUNT_LAYOUTPROTOTYPE_WHERE =
		"SELECT COUNT(layoutPrototype) FROM LayoutPrototype layoutPrototype WHERE ";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "settings", "active"});

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1927535448