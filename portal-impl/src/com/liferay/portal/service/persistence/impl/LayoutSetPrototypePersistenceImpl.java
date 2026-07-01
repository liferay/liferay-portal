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
import com.liferay.portal.kernel.exception.NoSuchLayoutSetPrototypeException;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.model.LayoutSetPrototypeTable;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.LayoutSetPrototypePersistence;
import com.liferay.portal.kernel.service.persistence.LayoutSetPrototypeUtil;
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
import com.liferay.portal.model.impl.LayoutSetPrototypeImpl;
import com.liferay.portal.model.impl.LayoutSetPrototypeModelImpl;

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
 * The persistence implementation for the layout set prototype service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class LayoutSetPrototypePersistenceImpl
	extends BasePersistenceImpl
		<LayoutSetPrototype, NoSuchLayoutSetPrototypeException>
	implements LayoutSetPrototypePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>LayoutSetPrototypeUtil</code> to access the layout set prototype persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		LayoutSetPrototypeImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FilterCollectionPersistenceFinder
		<LayoutSetPrototype, NoSuchLayoutSetPrototypeException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the layout set prototypes where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutSetPrototypeModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout set prototypes
	 * @param end the upper bound of the range of layout set prototypes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout set prototypes
	 */
	@Override
	public List<LayoutSetPrototype> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<LayoutSetPrototype> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout set prototype in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout set prototype
	 * @throws NoSuchLayoutSetPrototypeException if a matching layout set prototype could not be found
	 */
	@Override
	public LayoutSetPrototype findByUuid_First(
			String uuid,
			OrderByComparator<LayoutSetPrototype> orderByComparator)
		throws NoSuchLayoutSetPrototypeException {

		return _collectionPersistenceFinderByUuid.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid},
			orderByComparator);
	}

	/**
	 * Returns the first layout set prototype in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout set prototype, or <code>null</code> if a matching layout set prototype could not be found
	 */
	@Override
	public LayoutSetPrototype fetchByUuid_First(
		String uuid, OrderByComparator<LayoutSetPrototype> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout set prototypes that the user has permissions to view where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutSetPrototypeModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout set prototypes
	 * @param end the upper bound of the range of layout set prototypes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout set prototypes that the user has permission to view
	 */
	@Override
	public List<LayoutSetPrototype> filterFindByUuid(
		String uuid, int start, int end,
		OrderByComparator<LayoutSetPrototype> orderByComparator) {

		return _collectionPersistenceFinderByUuid.filterFind(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid}, start, end,
			orderByComparator);
	}

	/**
	 * Removes all the layout set prototypes where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	/**
	 * Returns the number of layout set prototypes where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching layout set prototypes
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	/**
	 * Returns the number of layout set prototypes that the user has permission to view where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching layout set prototypes that the user has permission to view
	 */
	@Override
	public int filterCountByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.filterCount(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	private FilterCollectionPersistenceFinder
		<LayoutSetPrototype, NoSuchLayoutSetPrototypeException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the layout set prototypes where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutSetPrototypeModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout set prototypes
	 * @param end the upper bound of the range of layout set prototypes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout set prototypes
	 */
	@Override
	public List<LayoutSetPrototype> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<LayoutSetPrototype> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout set prototype in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout set prototype
	 * @throws NoSuchLayoutSetPrototypeException if a matching layout set prototype could not be found
	 */
	@Override
	public LayoutSetPrototype findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<LayoutSetPrototype> orderByComparator)
		throws NoSuchLayoutSetPrototypeException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			orderByComparator);
	}

	/**
	 * Returns the first layout set prototype in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout set prototype, or <code>null</code> if a matching layout set prototype could not be found
	 */
	@Override
	public LayoutSetPrototype fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<LayoutSetPrototype> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout set prototypes that the user has permissions to view where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutSetPrototypeModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout set prototypes
	 * @param end the upper bound of the range of layout set prototypes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout set prototypes that the user has permission to view
	 */
	@Override
	public List<LayoutSetPrototype> filterFindByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<LayoutSetPrototype> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.filterFind(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			start, end, orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the layout set prototypes where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of layout set prototypes where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching layout set prototypes
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId});
	}

	/**
	 * Returns the number of layout set prototypes that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching layout set prototypes that the user has permission to view
	 */
	@Override
	public int filterCountByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.filterCount(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			companyId, 0);
	}

	private FilterCollectionPersistenceFinder
		<LayoutSetPrototype, NoSuchLayoutSetPrototypeException>
			_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the layout set prototypes where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutSetPrototypeModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout set prototypes
	 * @param end the upper bound of the range of layout set prototypes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout set prototypes
	 */
	@Override
	public List<LayoutSetPrototype> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<LayoutSetPrototype> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout set prototype in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout set prototype
	 * @throws NoSuchLayoutSetPrototypeException if a matching layout set prototype could not be found
	 */
	@Override
	public LayoutSetPrototype findByCompanyId_First(
			long companyId,
			OrderByComparator<LayoutSetPrototype> orderByComparator)
		throws NoSuchLayoutSetPrototypeException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			orderByComparator);
	}

	/**
	 * Returns the first layout set prototype in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout set prototype, or <code>null</code> if a matching layout set prototype could not be found
	 */
	@Override
	public LayoutSetPrototype fetchByCompanyId_First(
		long companyId,
		OrderByComparator<LayoutSetPrototype> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout set prototypes that the user has permissions to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutSetPrototypeModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout set prototypes
	 * @param end the upper bound of the range of layout set prototypes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout set prototypes that the user has permission to view
	 */
	@Override
	public List<LayoutSetPrototype> filterFindByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<LayoutSetPrototype> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.filterFind(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId}, start,
			end, orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the layout set prototypes where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId});
	}

	/**
	 * Returns the number of layout set prototypes where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching layout set prototypes
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId});
	}

	/**
	 * Returns the number of layout set prototypes that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching layout set prototypes that the user has permission to view
	 */
	@Override
	public int filterCountByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.filterCount(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			companyId, 0);
	}

	private FilterCollectionPersistenceFinder
		<LayoutSetPrototype, NoSuchLayoutSetPrototypeException>
			_collectionPersistenceFinderByC_A;

	/**
	 * Returns an ordered range of all the layout set prototypes where companyId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutSetPrototypeModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param start the lower bound of the range of layout set prototypes
	 * @param end the upper bound of the range of layout set prototypes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout set prototypes
	 */
	@Override
	public List<LayoutSetPrototype> findByC_A(
		long companyId, boolean active, int start, int end,
		OrderByComparator<LayoutSetPrototype> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_A.find(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, active},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout set prototype in the ordered set where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout set prototype
	 * @throws NoSuchLayoutSetPrototypeException if a matching layout set prototype could not be found
	 */
	@Override
	public LayoutSetPrototype findByC_A_First(
			long companyId, boolean active,
			OrderByComparator<LayoutSetPrototype> orderByComparator)
		throws NoSuchLayoutSetPrototypeException {

		return _collectionPersistenceFinderByC_A.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, active},
			orderByComparator);
	}

	/**
	 * Returns the first layout set prototype in the ordered set where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout set prototype, or <code>null</code> if a matching layout set prototype could not be found
	 */
	@Override
	public LayoutSetPrototype fetchByC_A_First(
		long companyId, boolean active,
		OrderByComparator<LayoutSetPrototype> orderByComparator) {

		return _collectionPersistenceFinderByC_A.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, active},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout set prototypes that the user has permissions to view where companyId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LayoutSetPrototypeModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param start the lower bound of the range of layout set prototypes
	 * @param end the upper bound of the range of layout set prototypes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout set prototypes that the user has permission to view
	 */
	@Override
	public List<LayoutSetPrototype> filterFindByC_A(
		long companyId, boolean active, int start, int end,
		OrderByComparator<LayoutSetPrototype> orderByComparator) {

		return _collectionPersistenceFinderByC_A.filterFind(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, active},
			start, end, orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the layout set prototypes where companyId = &#63; and active = &#63; from the database.
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
	 * Returns the number of layout set prototypes where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @return the number of matching layout set prototypes
	 */
	@Override
	public int countByC_A(long companyId, boolean active) {
		return _collectionPersistenceFinderByC_A.count(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, active});
	}

	/**
	 * Returns the number of layout set prototypes that the user has permission to view where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @return the number of matching layout set prototypes that the user has permission to view
	 */
	@Override
	public int filterCountByC_A(long companyId, boolean active) {
		return _collectionPersistenceFinderByC_A.filterCount(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, active},
			companyId, 0);
	}

	public LayoutSetPrototypePersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("settings", "settings_");
		dbColumnNames.put("active", "active_");

		setDBColumnNames(dbColumnNames);

		setModelClass(LayoutSetPrototype.class);

		setModelImplClass(LayoutSetPrototypeImpl.class);
		setModelPKClass(long.class);

		setTable(LayoutSetPrototypeTable.INSTANCE);
	}

	/**
	 * Creates a new layout set prototype with the primary key. Does not add the layout set prototype to the database.
	 *
	 * @param layoutSetPrototypeId the primary key for the new layout set prototype
	 * @return the new layout set prototype
	 */
	@Override
	public LayoutSetPrototype create(long layoutSetPrototypeId) {
		LayoutSetPrototype layoutSetPrototype = new LayoutSetPrototypeImpl();

		layoutSetPrototype.setNew(true);
		layoutSetPrototype.setPrimaryKey(layoutSetPrototypeId);

		String uuid = PortalUUIDUtil.generate();

		layoutSetPrototype.setUuid(uuid);

		layoutSetPrototype.setCompanyId(CompanyThreadLocal.getCompanyId());

		return layoutSetPrototype;
	}

	/**
	 * Removes the layout set prototype with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param layoutSetPrototypeId the primary key of the layout set prototype
	 * @return the layout set prototype that was removed
	 * @throws NoSuchLayoutSetPrototypeException if a layout set prototype with the primary key could not be found
	 */
	@Override
	public LayoutSetPrototype remove(long layoutSetPrototypeId)
		throws NoSuchLayoutSetPrototypeException {

		return remove((Serializable)layoutSetPrototypeId);
	}

	@Override
	protected LayoutSetPrototype removeImpl(
		LayoutSetPrototype layoutSetPrototype) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(layoutSetPrototype)) {
				layoutSetPrototype = (LayoutSetPrototype)session.get(
					LayoutSetPrototypeImpl.class,
					layoutSetPrototype.getPrimaryKeyObj());
			}

			if ((layoutSetPrototype != null) &&
				CTPersistenceHelperUtil.isRemove(layoutSetPrototype)) {

				session.delete(layoutSetPrototype);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (layoutSetPrototype != null) {
			clearCache(layoutSetPrototype);
		}

		return layoutSetPrototype;
	}

	@Override
	public LayoutSetPrototype updateImpl(
		LayoutSetPrototype layoutSetPrototype) {

		boolean isNew = layoutSetPrototype.isNew();

		if (!(layoutSetPrototype instanceof LayoutSetPrototypeModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(layoutSetPrototype.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					layoutSetPrototype);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in layoutSetPrototype proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom LayoutSetPrototype implementation " +
					layoutSetPrototype.getClass());
		}

		LayoutSetPrototypeModelImpl layoutSetPrototypeModelImpl =
			(LayoutSetPrototypeModelImpl)layoutSetPrototype;

		if (Validator.isNull(layoutSetPrototype.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			layoutSetPrototype.setUuid(uuid);
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (layoutSetPrototype.getCreateDate() == null)) {
			if (serviceContext == null) {
				layoutSetPrototype.setCreateDate(date);
			}
			else {
				layoutSetPrototype.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!layoutSetPrototypeModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				layoutSetPrototype.setModifiedDate(date);
			}
			else {
				layoutSetPrototype.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (CTPersistenceHelperUtil.isInsert(layoutSetPrototype)) {
				if (!isNew) {
					session.evict(
						LayoutSetPrototypeImpl.class,
						layoutSetPrototype.getPrimaryKeyObj());
				}

				session.save(layoutSetPrototype);
			}
			else {
				layoutSetPrototype = (LayoutSetPrototype)session.merge(
					layoutSetPrototype);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(layoutSetPrototype, false);

		if (isNew) {
			layoutSetPrototype.setNew(false);
		}

		layoutSetPrototype.resetOriginalValues();

		return layoutSetPrototype;
	}

	/**
	 * Returns the layout set prototype with the primary key or throws a <code>NoSuchLayoutSetPrototypeException</code> if it could not be found.
	 *
	 * @param layoutSetPrototypeId the primary key of the layout set prototype
	 * @return the layout set prototype
	 * @throws NoSuchLayoutSetPrototypeException if a layout set prototype with the primary key could not be found
	 */
	@Override
	public LayoutSetPrototype findByPrimaryKey(long layoutSetPrototypeId)
		throws NoSuchLayoutSetPrototypeException {

		return findByPrimaryKey((Serializable)layoutSetPrototypeId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return CTPersistenceHelperUtil.getCTPersistenceHelper();
	}

	/**
	 * Returns the layout set prototype with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param layoutSetPrototypeId the primary key of the layout set prototype
	 * @return the layout set prototype, or <code>null</code> if a layout set prototype with the primary key could not be found
	 */
	@Override
	public LayoutSetPrototype fetchByPrimaryKey(long layoutSetPrototypeId) {
		return fetchByPrimaryKey((Serializable)layoutSetPrototypeId);
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
		return "layoutSetPrototypeId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_LAYOUTSETPROTOTYPE;
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
		return LayoutSetPrototypeModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "LayoutSetPrototype";
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
			Collections.singleton("layoutSetPrototypeId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);
	}

	/**
	 * Initializes the layout set prototype persistence.
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
				_SQL_SELECT_LAYOUTSETPROTOTYPE_WHERE,
				_SQL_COUNT_LAYOUTSETPROTOTYPE_WHERE,
				LayoutSetPrototypeModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"layoutSetPrototype.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					LayoutSetPrototype::getUuid));

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
				_SQL_SELECT_LAYOUTSETPROTOTYPE_WHERE,
				_SQL_COUNT_LAYOUTSETPROTOTYPE_WHERE,
				LayoutSetPrototypeModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"layoutSetPrototype.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					LayoutSetPrototype::getUuid),
				new FinderColumn<>(
					"layoutSetPrototype.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, LayoutSetPrototype::getCompanyId));

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
				_SQL_SELECT_LAYOUTSETPROTOTYPE_WHERE,
				_SQL_COUNT_LAYOUTSETPROTOTYPE_WHERE,
				LayoutSetPrototypeModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"layoutSetPrototype.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, LayoutSetPrototype::getCompanyId));

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
				_SQL_SELECT_LAYOUTSETPROTOTYPE_WHERE,
				_SQL_COUNT_LAYOUTSETPROTOTYPE_WHERE,
				LayoutSetPrototypeModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "",
				new FinderColumn<>(
					"layoutSetPrototype.", "companyId", FinderColumn.Type.LONG,
					"=", true, true, LayoutSetPrototype::getCompanyId),
				new FinderColumn<>(
					"layoutSetPrototype.", "active", "active_",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					LayoutSetPrototype::isActive));

		LayoutSetPrototypeUtil.setPersistence(this);
	}

	public void destroy() {
		LayoutSetPrototypeUtil.setPersistence(null);

		EntityCacheUtil.removeCache(LayoutSetPrototypeImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		LayoutSetPrototypeModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_LAYOUTSETPROTOTYPE =
		"SELECT layoutSetPrototype FROM LayoutSetPrototype layoutSetPrototype";

	private static final String _SQL_SELECT_LAYOUTSETPROTOTYPE_WHERE =
		"SELECT layoutSetPrototype FROM LayoutSetPrototype layoutSetPrototype WHERE ";

	private static final String _SQL_COUNT_LAYOUTSETPROTOTYPE_WHERE =
		"SELECT COUNT(layoutSetPrototype) FROM LayoutSetPrototype layoutSetPrototype WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No LayoutSetPrototype exists with the key {";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "settings", "active"});

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1343155345