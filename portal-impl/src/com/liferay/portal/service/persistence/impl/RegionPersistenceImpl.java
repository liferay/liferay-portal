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
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.DuplicateRegionExternalReferenceCodeException;
import com.liferay.portal.kernel.exception.NoSuchRegionException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.model.RegionTable;
import com.liferay.portal.kernel.sanitizer.Sanitizer;
import com.liferay.portal.kernel.sanitizer.SanitizerException;
import com.liferay.portal.kernel.sanitizer.SanitizerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.RegionLocalizationPersistence;
import com.liferay.portal.kernel.service.persistence.RegionPersistence;
import com.liferay.portal.kernel.service.persistence.RegionUtil;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelperUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.model.impl.RegionImpl;
import com.liferay.portal.model.impl.RegionModelImpl;

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
 * The persistence implementation for the region service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class RegionPersistenceImpl
	extends BasePersistenceImpl<Region, NoSuchRegionException>
	implements RegionPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>RegionUtil</code> to access the region persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		RegionImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<Region, NoSuchRegionException>
		_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the regions where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RegionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of regions
	 * @param end the upper bound of the range of regions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching regions
	 */
	@Override
	public List<Region> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<Region> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first region in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching region
	 * @throws NoSuchRegionException if a matching region could not be found
	 */
	@Override
	public Region findByUuid_First(
			String uuid, OrderByComparator<Region> orderByComparator)
		throws NoSuchRegionException {

		return _collectionPersistenceFinderByUuid.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid},
			orderByComparator);
	}

	/**
	 * Returns the first region in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching region, or <code>null</code> if a matching region could not be found
	 */
	@Override
	public Region fetchByUuid_First(
		String uuid, OrderByComparator<Region> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid},
			orderByComparator);
	}

	/**
	 * Removes all the regions where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	/**
	 * Returns the number of regions where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching regions
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	private CollectionPersistenceFinder<Region, NoSuchRegionException>
		_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the regions where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RegionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of regions
	 * @param end the upper bound of the range of regions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching regions
	 */
	@Override
	public List<Region> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<Region> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first region in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching region
	 * @throws NoSuchRegionException if a matching region could not be found
	 */
	@Override
	public Region findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<Region> orderByComparator)
		throws NoSuchRegionException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			orderByComparator);
	}

	/**
	 * Returns the first region in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching region, or <code>null</code> if a matching region could not be found
	 */
	@Override
	public Region fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<Region> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			orderByComparator);
	}

	/**
	 * Removes all the regions where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of regions where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching regions
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder<Region, NoSuchRegionException>
		_collectionPersistenceFinderByCountryId;

	/**
	 * Returns an ordered range of all the regions where countryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RegionModelImpl</code>.
	 * </p>
	 *
	 * @param countryId the country ID
	 * @param start the lower bound of the range of regions
	 * @param end the upper bound of the range of regions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching regions
	 */
	@Override
	public List<Region> findByCountryId(
		long countryId, int start, int end,
		OrderByComparator<Region> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByCountryId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {countryId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first region in the ordered set where countryId = &#63;.
	 *
	 * @param countryId the country ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching region
	 * @throws NoSuchRegionException if a matching region could not be found
	 */
	@Override
	public Region findByCountryId_First(
			long countryId, OrderByComparator<Region> orderByComparator)
		throws NoSuchRegionException {

		return _collectionPersistenceFinderByCountryId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {countryId},
			orderByComparator);
	}

	/**
	 * Returns the first region in the ordered set where countryId = &#63;.
	 *
	 * @param countryId the country ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching region, or <code>null</code> if a matching region could not be found
	 */
	@Override
	public Region fetchByCountryId_First(
		long countryId, OrderByComparator<Region> orderByComparator) {

		return _collectionPersistenceFinderByCountryId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {countryId},
			orderByComparator);
	}

	/**
	 * Removes all the regions where countryId = &#63; from the database.
	 *
	 * @param countryId the country ID
	 */
	@Override
	public void removeByCountryId(long countryId) {
		_collectionPersistenceFinderByCountryId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {countryId});
	}

	/**
	 * Returns the number of regions where countryId = &#63;.
	 *
	 * @param countryId the country ID
	 * @return the number of matching regions
	 */
	@Override
	public int countByCountryId(long countryId) {
		return _collectionPersistenceFinderByCountryId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {countryId});
	}

	private CollectionPersistenceFinder<Region, NoSuchRegionException>
		_collectionPersistenceFinderByActive;

	/**
	 * Returns an ordered range of all the regions where active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RegionModelImpl</code>.
	 * </p>
	 *
	 * @param active the active
	 * @param start the lower bound of the range of regions
	 * @param end the upper bound of the range of regions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching regions
	 */
	@Override
	public List<Region> findByActive(
		boolean active, int start, int end,
		OrderByComparator<Region> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByActive.find(
			FinderCacheUtil.getFinderCache(), new Object[] {active}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first region in the ordered set where active = &#63;.
	 *
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching region
	 * @throws NoSuchRegionException if a matching region could not be found
	 */
	@Override
	public Region findByActive_First(
			boolean active, OrderByComparator<Region> orderByComparator)
		throws NoSuchRegionException {

		return _collectionPersistenceFinderByActive.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {active},
			orderByComparator);
	}

	/**
	 * Returns the first region in the ordered set where active = &#63;.
	 *
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching region, or <code>null</code> if a matching region could not be found
	 */
	@Override
	public Region fetchByActive_First(
		boolean active, OrderByComparator<Region> orderByComparator) {

		return _collectionPersistenceFinderByActive.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {active},
			orderByComparator);
	}

	/**
	 * Removes all the regions where active = &#63; from the database.
	 *
	 * @param active the active
	 */
	@Override
	public void removeByActive(boolean active) {
		_collectionPersistenceFinderByActive.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {active});
	}

	/**
	 * Returns the number of regions where active = &#63;.
	 *
	 * @param active the active
	 * @return the number of matching regions
	 */
	@Override
	public int countByActive(boolean active) {
		return _collectionPersistenceFinderByActive.count(
			FinderCacheUtil.getFinderCache(), new Object[] {active});
	}

	private CollectionPersistenceFinder<Region, NoSuchRegionException>
		_collectionPersistenceFinderByC_A;

	/**
	 * Returns an ordered range of all the regions where countryId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>RegionModelImpl</code>.
	 * </p>
	 *
	 * @param countryId the country ID
	 * @param active the active
	 * @param start the lower bound of the range of regions
	 * @param end the upper bound of the range of regions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching regions
	 */
	@Override
	public List<Region> findByC_A(
		long countryId, boolean active, int start, int end,
		OrderByComparator<Region> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByC_A.find(
			FinderCacheUtil.getFinderCache(), new Object[] {countryId, active},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first region in the ordered set where countryId = &#63; and active = &#63;.
	 *
	 * @param countryId the country ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching region
	 * @throws NoSuchRegionException if a matching region could not be found
	 */
	@Override
	public Region findByC_A_First(
			long countryId, boolean active,
			OrderByComparator<Region> orderByComparator)
		throws NoSuchRegionException {

		return _collectionPersistenceFinderByC_A.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {countryId, active},
			orderByComparator);
	}

	/**
	 * Returns the first region in the ordered set where countryId = &#63; and active = &#63;.
	 *
	 * @param countryId the country ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching region, or <code>null</code> if a matching region could not be found
	 */
	@Override
	public Region fetchByC_A_First(
		long countryId, boolean active,
		OrderByComparator<Region> orderByComparator) {

		return _collectionPersistenceFinderByC_A.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {countryId, active},
			orderByComparator);
	}

	/**
	 * Removes all the regions where countryId = &#63; and active = &#63; from the database.
	 *
	 * @param countryId the country ID
	 * @param active the active
	 */
	@Override
	public void removeByC_A(long countryId, boolean active) {
		_collectionPersistenceFinderByC_A.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {countryId, active});
	}

	/**
	 * Returns the number of regions where countryId = &#63; and active = &#63;.
	 *
	 * @param countryId the country ID
	 * @param active the active
	 * @return the number of matching regions
	 */
	@Override
	public int countByC_A(long countryId, boolean active) {
		return _collectionPersistenceFinderByC_A.count(
			FinderCacheUtil.getFinderCache(), new Object[] {countryId, active});
	}

	private UniquePersistenceFinder<Region, NoSuchRegionException>
		_uniquePersistenceFinderByC_R;

	/**
	 * Returns the region where countryId = &#63; and regionCode = &#63; or throws a <code>NoSuchRegionException</code> if it could not be found.
	 *
	 * @param countryId the country ID
	 * @param regionCode the region code
	 * @return the matching region
	 * @throws NoSuchRegionException if a matching region could not be found
	 */
	@Override
	public Region findByC_R(long countryId, String regionCode)
		throws NoSuchRegionException {

		return _uniquePersistenceFinderByC_R.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {countryId, regionCode});
	}

	/**
	 * Returns the region where countryId = &#63; and regionCode = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param countryId the country ID
	 * @param regionCode the region code
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching region, or <code>null</code> if a matching region could not be found
	 */
	@Override
	public Region fetchByC_R(
		long countryId, String regionCode, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_R.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {countryId, regionCode}, useFinderCache);
	}

	/**
	 * Removes the region where countryId = &#63; and regionCode = &#63; from the database.
	 *
	 * @param countryId the country ID
	 * @param regionCode the region code
	 * @return the region that was removed
	 */
	@Override
	public Region removeByC_R(long countryId, String regionCode)
		throws NoSuchRegionException {

		Region region = findByC_R(countryId, regionCode);

		return remove(region);
	}

	/**
	 * Returns the number of regions where countryId = &#63; and regionCode = &#63;.
	 *
	 * @param countryId the country ID
	 * @param regionCode the region code
	 * @return the number of matching regions
	 */
	@Override
	public int countByC_R(long countryId, String regionCode) {
		return _uniquePersistenceFinderByC_R.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {countryId, regionCode});
	}

	private UniquePersistenceFinder<Region, NoSuchRegionException>
		_uniquePersistenceFinderByERC_C;

	/**
	 * Returns the region where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchRegionException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching region
	 * @throws NoSuchRegionException if a matching region could not be found
	 */
	@Override
	public Region findByERC_C(String externalReferenceCode, long companyId)
		throws NoSuchRegionException {

		return _uniquePersistenceFinderByERC_C.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {externalReferenceCode, companyId});
	}

	/**
	 * Returns the region where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching region, or <code>null</code> if a matching region could not be found
	 */
	@Override
	public Region fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_C.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {externalReferenceCode, companyId}, useFinderCache);
	}

	/**
	 * Removes the region where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the region that was removed
	 */
	@Override
	public Region removeByERC_C(String externalReferenceCode, long companyId)
		throws NoSuchRegionException {

		Region region = findByERC_C(externalReferenceCode, companyId);

		return remove(region);
	}

	/**
	 * Returns the number of regions where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching regions
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		return _uniquePersistenceFinderByERC_C.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {externalReferenceCode, companyId});
	}

	public RegionPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("active", "active_");

		setDBColumnNames(dbColumnNames);

		setModelClass(Region.class);

		setModelImplClass(RegionImpl.class);
		setModelPKClass(long.class);

		setTable(RegionTable.INSTANCE);
	}

	/**
	 * Creates a new region with the primary key. Does not add the region to the database.
	 *
	 * @param regionId the primary key for the new region
	 * @return the new region
	 */
	@Override
	public Region create(long regionId) {
		Region region = new RegionImpl();

		region.setNew(true);
		region.setPrimaryKey(regionId);

		String uuid = PortalUUIDUtil.generate();

		region.setUuid(uuid);

		region.setCompanyId(CompanyThreadLocal.getCompanyId());

		return region;
	}

	/**
	 * Removes the region with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param regionId the primary key of the region
	 * @return the region that was removed
	 * @throws NoSuchRegionException if a region with the primary key could not be found
	 */
	@Override
	public Region remove(long regionId) throws NoSuchRegionException {
		return remove((Serializable)regionId);
	}

	@Override
	protected Region removeImpl(Region region) {
		regionLocalizationPersistence.removeByRegionId(region.getRegionId());

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(region)) {
				region = (Region)session.get(
					RegionImpl.class, region.getPrimaryKeyObj());
			}

			if ((region != null) && CTPersistenceHelperUtil.isRemove(region)) {
				session.delete(region);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (region != null) {
			clearCache(region);
		}

		return region;
	}

	@Override
	public Region updateImpl(Region region) {
		boolean isNew = region.isNew();

		if (!(region instanceof RegionModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(region.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(region);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in region proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom Region implementation " +
					region.getClass());
		}

		RegionModelImpl regionModelImpl = (RegionModelImpl)region;

		if (Validator.isNull(region.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			region.setUuid(uuid);
		}

		if (Validator.isNull(region.getExternalReferenceCode())) {
			region.setExternalReferenceCode(region.getUuid());
		}
		else {
			if (!Objects.equals(
					regionModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					region.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = region.getCompanyId();

					long groupId = 0;

					long classPK = 0;

					if (!isNew) {
						classPK = region.getPrimaryKey();
					}

					try {
						region.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								Region.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								region.getExternalReferenceCode(), null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			Region ercRegion = fetchByERC_C(
				region.getExternalReferenceCode(), region.getCompanyId());

			if (isNew) {
				if (ercRegion != null) {
					throw new DuplicateRegionExternalReferenceCodeException(
						"Duplicate region with external reference code " +
							region.getExternalReferenceCode() +
								" and company " + region.getCompanyId());
				}
			}
			else {
				if ((ercRegion != null) &&
					(region.getRegionId() != ercRegion.getRegionId())) {

					throw new DuplicateRegionExternalReferenceCodeException(
						"Duplicate region with external reference code " +
							region.getExternalReferenceCode() +
								" and company " + region.getCompanyId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (region.getCreateDate() == null)) {
			if (serviceContext == null) {
				region.setCreateDate(date);
			}
			else {
				region.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!regionModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				region.setModifiedDate(date);
			}
			else {
				region.setModifiedDate(serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (CTPersistenceHelperUtil.isInsert(region)) {
				if (!isNew) {
					session.evict(RegionImpl.class, region.getPrimaryKeyObj());
				}

				session.save(region);
			}
			else {
				region = (Region)session.merge(region);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(region, false);

		if (isNew) {
			region.setNew(false);
		}

		region.resetOriginalValues();

		return region;
	}

	/**
	 * Returns the region with the primary key or throws a <code>NoSuchRegionException</code> if it could not be found.
	 *
	 * @param regionId the primary key of the region
	 * @return the region
	 * @throws NoSuchRegionException if a region with the primary key could not be found
	 */
	@Override
	public Region findByPrimaryKey(long regionId) throws NoSuchRegionException {
		return findByPrimaryKey((Serializable)regionId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return CTPersistenceHelperUtil.getCTPersistenceHelper();
	}

	/**
	 * Returns the region with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param regionId the primary key of the region
	 * @return the region, or <code>null</code> if a region with the primary key could not be found
	 */
	@Override
	public Region fetchByPrimaryKey(long regionId) {
		return fetchByPrimaryKey((Serializable)regionId);
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
		return "regionId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_REGION;
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
		return RegionModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "Region";
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
		ctStrictColumnNames.add("defaultLanguageId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("countryId");
		ctMergeColumnNames.add("active_");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("position");
		ctMergeColumnNames.add("regionCode");
		ctMergeColumnNames.add("lastPublishDate");
		ctMergeColumnNames.add("status");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("regionId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"countryId", "regionCode"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "companyId"});
	}

	/**
	 * Initializes the region persistence.
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
			_SQL_SELECT_REGION_WHERE, _SQL_COUNT_REGION_WHERE,
			RegionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"region.", "uuid", "uuid_", FinderColumn.Type.STRING, "=", true,
				true, Region::getUuid));

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
				_SQL_SELECT_REGION_WHERE, _SQL_COUNT_REGION_WHERE,
				RegionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"region.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
					true, true, Region::getUuid),
				new FinderColumn<>(
					"region.", "companyId", FinderColumn.Type.LONG, "=", true,
					true, Region::getCompanyId));

		_collectionPersistenceFinderByCountryId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCountryId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"countryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByCountryId", new String[] {Long.class.getName()},
					new String[] {"countryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByCountryId", new String[] {Long.class.getName()},
					new String[] {"countryId"}, false),
				_SQL_SELECT_REGION_WHERE, _SQL_COUNT_REGION_WHERE,
				RegionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"region.", "countryId", FinderColumn.Type.LONG, "=", true,
					true, Region::getCountryId));

		_collectionPersistenceFinderByActive =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByActive",
					new String[] {
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"active_"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByActive",
					new String[] {Boolean.class.getName()},
					new String[] {"active_"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByActive",
					new String[] {Boolean.class.getName()},
					new String[] {"active_"}, false),
				_SQL_SELECT_REGION_WHERE, _SQL_COUNT_REGION_WHERE,
				RegionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"region.", "active", "active_", FinderColumn.Type.BOOLEAN,
					"=", true, true, Region::isActive));

		_collectionPersistenceFinderByC_A = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_A",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"countryId", "active_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_A",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"countryId", "active_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_A",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"countryId", "active_"}, false),
			_SQL_SELECT_REGION_WHERE, _SQL_COUNT_REGION_WHERE,
			RegionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"region.", "countryId", FinderColumn.Type.LONG, "=", true, true,
				Region::getCountryId),
			new FinderColumn<>(
				"region.", "active", "active_", FinderColumn.Type.BOOLEAN, "=",
				true, true, Region::isActive));

		_uniquePersistenceFinderByC_R = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_R",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"countryId", "regionCode"}, 0, 2, false,
				Region::getCountryId,
				convertNullFunction(Region::getRegionCode)),
			_SQL_SELECT_REGION_WHERE, "",
			new FinderColumn<>(
				"region.", "countryId", FinderColumn.Type.LONG, "=", true, true,
				Region::getCountryId),
			new FinderColumn<>(
				"region.", "regionCode", FinderColumn.Type.STRING, "=", true,
				true, Region::getRegionCode));

		_uniquePersistenceFinderByERC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "companyId"}, 0, 1,
				false, convertNullFunction(Region::getExternalReferenceCode),
				Region::getCompanyId),
			_SQL_SELECT_REGION_WHERE, "",
			new FinderColumn<>(
				"region.", "externalReferenceCode", FinderColumn.Type.STRING,
				"=", true, true, Region::getExternalReferenceCode),
			new FinderColumn<>(
				"region.", "companyId", FinderColumn.Type.LONG, "=", true, true,
				Region::getCompanyId));

		RegionUtil.setPersistence(this);
	}

	public void destroy() {
		RegionUtil.setPersistence(null);

		EntityCacheUtil.removeCache(RegionImpl.class.getName());
	}

	@BeanReference(type = RegionLocalizationPersistence.class)
	protected RegionLocalizationPersistence regionLocalizationPersistence;

	private static final String _ENTITY_ALIAS_PREFIX =
		RegionModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_REGION =
		"SELECT region FROM Region region";

	private static final String _SQL_SELECT_REGION_WHERE =
		"SELECT region FROM Region region WHERE ";

	private static final String _SQL_COUNT_REGION_WHERE =
		"SELECT COUNT(region) FROM Region region WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No Region exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		RegionPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "active"});

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:591462364