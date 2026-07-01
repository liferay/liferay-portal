/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.impl;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.DuplicateAddressExternalReferenceCodeException;
import com.liferay.portal.kernel.exception.NoSuchAddressException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.AddressTable;
import com.liferay.portal.kernel.sanitizer.Sanitizer;
import com.liferay.portal.kernel.sanitizer.SanitizerException;
import com.liferay.portal.kernel.sanitizer.SanitizerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.AddressPersistence;
import com.liferay.portal.kernel.service.persistence.AddressUtil;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelperUtil;
import com.liferay.portal.kernel.service.persistence.impl.ArrayableFinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.model.impl.AddressImpl;
import com.liferay.portal.model.impl.AddressModelImpl;

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
 * The persistence implementation for the address service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class AddressPersistenceImpl
	extends BasePersistenceImpl<Address, NoSuchAddressException>
	implements AddressPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>AddressUtil</code> to access the address persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		AddressImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<Address, NoSuchAddressException>
		_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the addresses where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AddressModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of addresses
	 * @param end the upper bound of the range of addresses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching addresses
	 */
	@Override
	public List<Address> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<Address> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first address in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching address
	 * @throws NoSuchAddressException if a matching address could not be found
	 */
	@Override
	public Address findByUuid_First(
			String uuid, OrderByComparator<Address> orderByComparator)
		throws NoSuchAddressException {

		return _collectionPersistenceFinderByUuid.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid},
			orderByComparator);
	}

	/**
	 * Returns the first address in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching address, or <code>null</code> if a matching address could not be found
	 */
	@Override
	public Address fetchByUuid_First(
		String uuid, OrderByComparator<Address> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid},
			orderByComparator);
	}

	/**
	 * Removes all the addresses where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	/**
	 * Returns the number of addresses where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching addresses
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	private CollectionPersistenceFinder<Address, NoSuchAddressException>
		_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the addresses where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AddressModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of addresses
	 * @param end the upper bound of the range of addresses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching addresses
	 */
	@Override
	public List<Address> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<Address> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first address in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching address
	 * @throws NoSuchAddressException if a matching address could not be found
	 */
	@Override
	public Address findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<Address> orderByComparator)
		throws NoSuchAddressException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			orderByComparator);
	}

	/**
	 * Returns the first address in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching address, or <code>null</code> if a matching address could not be found
	 */
	@Override
	public Address fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<Address> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			orderByComparator);
	}

	/**
	 * Removes all the addresses where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of addresses where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching addresses
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder<Address, NoSuchAddressException>
		_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the addresses where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AddressModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of addresses
	 * @param end the upper bound of the range of addresses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching addresses
	 */
	@Override
	public List<Address> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<Address> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first address in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching address
	 * @throws NoSuchAddressException if a matching address could not be found
	 */
	@Override
	public Address findByCompanyId_First(
			long companyId, OrderByComparator<Address> orderByComparator)
		throws NoSuchAddressException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			orderByComparator);
	}

	/**
	 * Returns the first address in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching address, or <code>null</code> if a matching address could not be found
	 */
	@Override
	public Address fetchByCompanyId_First(
		long companyId, OrderByComparator<Address> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			orderByComparator);
	}

	/**
	 * Removes all the addresses where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId});
	}

	/**
	 * Returns the number of addresses where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching addresses
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId});
	}

	private CollectionPersistenceFinder<Address, NoSuchAddressException>
		_collectionPersistenceFinderByUserId;

	/**
	 * Returns an ordered range of all the addresses where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AddressModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of addresses
	 * @param end the upper bound of the range of addresses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching addresses
	 */
	@Override
	public List<Address> findByUserId(
		long userId, int start, int end,
		OrderByComparator<Address> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByUserId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {userId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first address in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching address
	 * @throws NoSuchAddressException if a matching address could not be found
	 */
	@Override
	public Address findByUserId_First(
			long userId, OrderByComparator<Address> orderByComparator)
		throws NoSuchAddressException {

		return _collectionPersistenceFinderByUserId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {userId},
			orderByComparator);
	}

	/**
	 * Returns the first address in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching address, or <code>null</code> if a matching address could not be found
	 */
	@Override
	public Address fetchByUserId_First(
		long userId, OrderByComparator<Address> orderByComparator) {

		return _collectionPersistenceFinderByUserId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {userId},
			orderByComparator);
	}

	/**
	 * Removes all the addresses where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	@Override
	public void removeByUserId(long userId) {
		_collectionPersistenceFinderByUserId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {userId});
	}

	/**
	 * Returns the number of addresses where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching addresses
	 */
	@Override
	public int countByUserId(long userId) {
		return _collectionPersistenceFinderByUserId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {userId});
	}

	private CollectionPersistenceFinder<Address, NoSuchAddressException>
		_collectionPersistenceFinderByCountryId;

	/**
	 * Returns an ordered range of all the addresses where countryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AddressModelImpl</code>.
	 * </p>
	 *
	 * @param countryId the country ID
	 * @param start the lower bound of the range of addresses
	 * @param end the upper bound of the range of addresses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching addresses
	 */
	@Override
	public List<Address> findByCountryId(
		long countryId, int start, int end,
		OrderByComparator<Address> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByCountryId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {countryId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first address in the ordered set where countryId = &#63;.
	 *
	 * @param countryId the country ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching address
	 * @throws NoSuchAddressException if a matching address could not be found
	 */
	@Override
	public Address findByCountryId_First(
			long countryId, OrderByComparator<Address> orderByComparator)
		throws NoSuchAddressException {

		return _collectionPersistenceFinderByCountryId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {countryId},
			orderByComparator);
	}

	/**
	 * Returns the first address in the ordered set where countryId = &#63;.
	 *
	 * @param countryId the country ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching address, or <code>null</code> if a matching address could not be found
	 */
	@Override
	public Address fetchByCountryId_First(
		long countryId, OrderByComparator<Address> orderByComparator) {

		return _collectionPersistenceFinderByCountryId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {countryId},
			orderByComparator);
	}

	/**
	 * Removes all the addresses where countryId = &#63; from the database.
	 *
	 * @param countryId the country ID
	 */
	@Override
	public void removeByCountryId(long countryId) {
		_collectionPersistenceFinderByCountryId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {countryId});
	}

	/**
	 * Returns the number of addresses where countryId = &#63;.
	 *
	 * @param countryId the country ID
	 * @return the number of matching addresses
	 */
	@Override
	public int countByCountryId(long countryId) {
		return _collectionPersistenceFinderByCountryId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {countryId});
	}

	private CollectionPersistenceFinder<Address, NoSuchAddressException>
		_collectionPersistenceFinderByRegionId;

	/**
	 * Returns an ordered range of all the addresses where regionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AddressModelImpl</code>.
	 * </p>
	 *
	 * @param regionId the region ID
	 * @param start the lower bound of the range of addresses
	 * @param end the upper bound of the range of addresses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching addresses
	 */
	@Override
	public List<Address> findByRegionId(
		long regionId, int start, int end,
		OrderByComparator<Address> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByRegionId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {regionId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first address in the ordered set where regionId = &#63;.
	 *
	 * @param regionId the region ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching address
	 * @throws NoSuchAddressException if a matching address could not be found
	 */
	@Override
	public Address findByRegionId_First(
			long regionId, OrderByComparator<Address> orderByComparator)
		throws NoSuchAddressException {

		return _collectionPersistenceFinderByRegionId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {regionId},
			orderByComparator);
	}

	/**
	 * Returns the first address in the ordered set where regionId = &#63;.
	 *
	 * @param regionId the region ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching address, or <code>null</code> if a matching address could not be found
	 */
	@Override
	public Address fetchByRegionId_First(
		long regionId, OrderByComparator<Address> orderByComparator) {

		return _collectionPersistenceFinderByRegionId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {regionId},
			orderByComparator);
	}

	/**
	 * Removes all the addresses where regionId = &#63; from the database.
	 *
	 * @param regionId the region ID
	 */
	@Override
	public void removeByRegionId(long regionId) {
		_collectionPersistenceFinderByRegionId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {regionId});
	}

	/**
	 * Returns the number of addresses where regionId = &#63;.
	 *
	 * @param regionId the region ID
	 * @return the number of matching addresses
	 */
	@Override
	public int countByRegionId(long regionId) {
		return _collectionPersistenceFinderByRegionId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {regionId});
	}

	private CollectionPersistenceFinder<Address, NoSuchAddressException>
		_collectionPersistenceFinderByC_C;

	/**
	 * Returns an ordered range of all the addresses where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AddressModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of addresses
	 * @param end the upper bound of the range of addresses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching addresses
	 */
	@Override
	public List<Address> findByC_C(
		long companyId, long classNameId, int start, int end,
		OrderByComparator<Address> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first address in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching address
	 * @throws NoSuchAddressException if a matching address could not be found
	 */
	@Override
	public Address findByC_C_First(
			long companyId, long classNameId,
			OrderByComparator<Address> orderByComparator)
		throws NoSuchAddressException {

		return _collectionPersistenceFinderByC_C.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId}, orderByComparator);
	}

	/**
	 * Returns the first address in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching address, or <code>null</code> if a matching address could not be found
	 */
	@Override
	public Address fetchByC_C_First(
		long companyId, long classNameId,
		OrderByComparator<Address> orderByComparator) {

		return _collectionPersistenceFinderByC_C.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId}, orderByComparator);
	}

	/**
	 * Removes all the addresses where companyId = &#63; and classNameId = &#63; from the database.
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
	 * Returns the number of addresses where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the number of matching addresses
	 */
	@Override
	public int countByC_C(long companyId, long classNameId) {
		return _collectionPersistenceFinderByC_C.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId});
	}

	private CollectionPersistenceFinder<Address, NoSuchAddressException>
		_collectionPersistenceFinderByCN_CPK;

	/**
	 * Returns an ordered range of all the addresses where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AddressModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of addresses
	 * @param end the upper bound of the range of addresses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching addresses
	 */
	@Override
	public List<Address> findByCN_CPK(
		long classNameId, long classPK, int start, int end,
		OrderByComparator<Address> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByCN_CPK.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first address in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching address
	 * @throws NoSuchAddressException if a matching address could not be found
	 */
	@Override
	public Address findByCN_CPK_First(
			long classNameId, long classPK,
			OrderByComparator<Address> orderByComparator)
		throws NoSuchAddressException {

		return _collectionPersistenceFinderByCN_CPK.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK}, orderByComparator);
	}

	/**
	 * Returns the first address in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching address, or <code>null</code> if a matching address could not be found
	 */
	@Override
	public Address fetchByCN_CPK_First(
		long classNameId, long classPK,
		OrderByComparator<Address> orderByComparator) {

		return _collectionPersistenceFinderByCN_CPK.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK}, orderByComparator);
	}

	/**
	 * Removes all the addresses where classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	@Override
	public void removeByCN_CPK(long classNameId, long classPK) {
		_collectionPersistenceFinderByCN_CPK.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK});
	}

	/**
	 * Returns the number of addresses where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching addresses
	 */
	@Override
	public int countByCN_CPK(long classNameId, long classPK) {
		return _collectionPersistenceFinderByCN_CPK.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {classNameId, classPK});
	}

	private CollectionPersistenceFinder<Address, NoSuchAddressException>
		_collectionPersistenceFinderByC_C_C;

	/**
	 * Returns an ordered range of all the addresses where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AddressModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of addresses
	 * @param end the upper bound of the range of addresses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching addresses
	 */
	@Override
	public List<Address> findByC_C_C(
		long companyId, long classNameId, long classPK, int start, int end,
		OrderByComparator<Address> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C_C.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId, classPK}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first address in the ordered set where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching address
	 * @throws NoSuchAddressException if a matching address could not be found
	 */
	@Override
	public Address findByC_C_C_First(
			long companyId, long classNameId, long classPK,
			OrderByComparator<Address> orderByComparator)
		throws NoSuchAddressException {

		return _collectionPersistenceFinderByC_C_C.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId, classPK}, orderByComparator);
	}

	/**
	 * Returns the first address in the ordered set where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching address, or <code>null</code> if a matching address could not be found
	 */
	@Override
	public Address fetchByC_C_C_First(
		long companyId, long classNameId, long classPK,
		OrderByComparator<Address> orderByComparator) {

		return _collectionPersistenceFinderByC_C_C.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId, classPK}, orderByComparator);
	}

	/**
	 * Removes all the addresses where companyId = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	@Override
	public void removeByC_C_C(long companyId, long classNameId, long classPK) {
		_collectionPersistenceFinderByC_C_C.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId, classPK});
	}

	/**
	 * Returns the number of addresses where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching addresses
	 */
	@Override
	public int countByC_C_C(long companyId, long classNameId, long classPK) {
		return _collectionPersistenceFinderByC_C_C.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId, classPK});
	}

	private CollectionPersistenceFinder<Address, NoSuchAddressException>
		_collectionPersistenceFinderByC_C_C_L;

	/**
	 * Returns an ordered range of all the addresses where companyId = &#63; and classNameId = &#63; and classPK = &#63; and listTypeId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AddressModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param listTypeId the list type ID
	 * @param start the lower bound of the range of addresses
	 * @param end the upper bound of the range of addresses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching addresses
	 */
	@Override
	public List<Address> findByC_C_C_L(
		long companyId, long classNameId, long classPK, long listTypeId,
		int start, int end, OrderByComparator<Address> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C_C_L.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				companyId, classNameId, classPK, new long[] {listTypeId}
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first address in the ordered set where companyId = &#63; and classNameId = &#63; and classPK = &#63; and listTypeId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param listTypeId the list type ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching address
	 * @throws NoSuchAddressException if a matching address could not be found
	 */
	@Override
	public Address findByC_C_C_L_First(
			long companyId, long classNameId, long classPK, long listTypeId,
			OrderByComparator<Address> orderByComparator)
		throws NoSuchAddressException {

		Address address = fetchByC_C_C_L_First(
			companyId, classNameId, classPK, listTypeId, orderByComparator);

		if (address != null) {
			return address;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", classNameId=");
		sb.append(classNameId);

		sb.append(", classPK=");
		sb.append(classPK);

		sb.append(", listTypeId=");
		sb.append(listTypeId);

		sb.append("}");

		throw new NoSuchAddressException(sb.toString());
	}

	/**
	 * Returns the first address in the ordered set where companyId = &#63; and classNameId = &#63; and classPK = &#63; and listTypeId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param listTypeId the list type ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching address, or <code>null</code> if a matching address could not be found
	 */
	@Override
	public Address fetchByC_C_C_L_First(
		long companyId, long classNameId, long classPK, long listTypeId,
		OrderByComparator<Address> orderByComparator) {

		return _collectionPersistenceFinderByC_C_C_L.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				companyId, classNameId, classPK, new long[] {listTypeId}
			},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the addresses where companyId = &#63; and classNameId = &#63; and classPK = &#63; and listTypeId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AddressModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param listTypeIds the list type IDs
	 * @param start the lower bound of the range of addresses
	 * @param end the upper bound of the range of addresses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching addresses
	 */
	@Override
	public List<Address> findByC_C_C_L(
		long companyId, long classNameId, long classPK, long[] listTypeIds,
		int start, int end, OrderByComparator<Address> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C_C_L.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				companyId, classNameId, classPK,
				ArrayUtil.sortedUnique(listTypeIds)
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the addresses where companyId = &#63; and classNameId = &#63; and classPK = &#63; and listTypeId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param listTypeId the list type ID
	 */
	@Override
	public void removeByC_C_C_L(
		long companyId, long classNameId, long classPK, long listTypeId) {

		_collectionPersistenceFinderByC_C_C_L.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				companyId, classNameId, classPK, new long[] {listTypeId}
			});
	}

	/**
	 * Returns the number of addresses where companyId = &#63; and classNameId = &#63; and classPK = &#63; and listTypeId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param listTypeId the list type ID
	 * @return the number of matching addresses
	 */
	@Override
	public int countByC_C_C_L(
		long companyId, long classNameId, long classPK, long listTypeId) {

		return _collectionPersistenceFinderByC_C_C_L.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				companyId, classNameId, classPK, new long[] {listTypeId}
			});
	}

	/**
	 * Returns the number of addresses where companyId = &#63; and classNameId = &#63; and classPK = &#63; and listTypeId = any &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param listTypeIds the list type IDs
	 * @return the number of matching addresses
	 */
	@Override
	public int countByC_C_C_L(
		long companyId, long classNameId, long classPK, long[] listTypeIds) {

		return _collectionPersistenceFinderByC_C_C_L.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				companyId, classNameId, classPK,
				ArrayUtil.sortedUnique(listTypeIds)
			});
	}

	private CollectionPersistenceFinder<Address, NoSuchAddressException>
		_collectionPersistenceFinderByC_C_C_M;

	/**
	 * Returns an ordered range of all the addresses where companyId = &#63; and classNameId = &#63; and classPK = &#63; and mailing = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AddressModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param mailing the mailing
	 * @param start the lower bound of the range of addresses
	 * @param end the upper bound of the range of addresses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching addresses
	 */
	@Override
	public List<Address> findByC_C_C_M(
		long companyId, long classNameId, long classPK, boolean mailing,
		int start, int end, OrderByComparator<Address> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C_C_M.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId, classPK, mailing}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first address in the ordered set where companyId = &#63; and classNameId = &#63; and classPK = &#63; and mailing = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param mailing the mailing
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching address
	 * @throws NoSuchAddressException if a matching address could not be found
	 */
	@Override
	public Address findByC_C_C_M_First(
			long companyId, long classNameId, long classPK, boolean mailing,
			OrderByComparator<Address> orderByComparator)
		throws NoSuchAddressException {

		return _collectionPersistenceFinderByC_C_C_M.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId, classPK, mailing},
			orderByComparator);
	}

	/**
	 * Returns the first address in the ordered set where companyId = &#63; and classNameId = &#63; and classPK = &#63; and mailing = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param mailing the mailing
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching address, or <code>null</code> if a matching address could not be found
	 */
	@Override
	public Address fetchByC_C_C_M_First(
		long companyId, long classNameId, long classPK, boolean mailing,
		OrderByComparator<Address> orderByComparator) {

		return _collectionPersistenceFinderByC_C_C_M.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId, classPK, mailing},
			orderByComparator);
	}

	/**
	 * Removes all the addresses where companyId = &#63; and classNameId = &#63; and classPK = &#63; and mailing = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param mailing the mailing
	 */
	@Override
	public void removeByC_C_C_M(
		long companyId, long classNameId, long classPK, boolean mailing) {

		_collectionPersistenceFinderByC_C_C_M.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId, classPK, mailing});
	}

	/**
	 * Returns the number of addresses where companyId = &#63; and classNameId = &#63; and classPK = &#63; and mailing = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param mailing the mailing
	 * @return the number of matching addresses
	 */
	@Override
	public int countByC_C_C_M(
		long companyId, long classNameId, long classPK, boolean mailing) {

		return _collectionPersistenceFinderByC_C_C_M.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId, classPK, mailing});
	}

	private CollectionPersistenceFinder<Address, NoSuchAddressException>
		_collectionPersistenceFinderByC_C_C_P;

	/**
	 * Returns an ordered range of all the addresses where companyId = &#63; and classNameId = &#63; and classPK = &#63; and primary = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>AddressModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param primary the primary
	 * @param start the lower bound of the range of addresses
	 * @param end the upper bound of the range of addresses (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching addresses
	 */
	@Override
	public List<Address> findByC_C_C_P(
		long companyId, long classNameId, long classPK, boolean primary,
		int start, int end, OrderByComparator<Address> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C_C_P.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId, classPK, primary}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first address in the ordered set where companyId = &#63; and classNameId = &#63; and classPK = &#63; and primary = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param primary the primary
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching address
	 * @throws NoSuchAddressException if a matching address could not be found
	 */
	@Override
	public Address findByC_C_C_P_First(
			long companyId, long classNameId, long classPK, boolean primary,
			OrderByComparator<Address> orderByComparator)
		throws NoSuchAddressException {

		return _collectionPersistenceFinderByC_C_C_P.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId, classPK, primary},
			orderByComparator);
	}

	/**
	 * Returns the first address in the ordered set where companyId = &#63; and classNameId = &#63; and classPK = &#63; and primary = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param primary the primary
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching address, or <code>null</code> if a matching address could not be found
	 */
	@Override
	public Address fetchByC_C_C_P_First(
		long companyId, long classNameId, long classPK, boolean primary,
		OrderByComparator<Address> orderByComparator) {

		return _collectionPersistenceFinderByC_C_C_P.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId, classPK, primary},
			orderByComparator);
	}

	/**
	 * Removes all the addresses where companyId = &#63; and classNameId = &#63; and classPK = &#63; and primary = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param primary the primary
	 */
	@Override
	public void removeByC_C_C_P(
		long companyId, long classNameId, long classPK, boolean primary) {

		_collectionPersistenceFinderByC_C_C_P.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId, classPK, primary});
	}

	/**
	 * Returns the number of addresses where companyId = &#63; and classNameId = &#63; and classPK = &#63; and primary = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param primary the primary
	 * @return the number of matching addresses
	 */
	@Override
	public int countByC_C_C_P(
		long companyId, long classNameId, long classPK, boolean primary) {

		return _collectionPersistenceFinderByC_C_C_P.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId, classPK, primary});
	}

	private UniquePersistenceFinder<Address, NoSuchAddressException>
		_uniquePersistenceFinderByERC_C;

	/**
	 * Returns the address where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchAddressException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching address
	 * @throws NoSuchAddressException if a matching address could not be found
	 */
	@Override
	public Address findByERC_C(String externalReferenceCode, long companyId)
		throws NoSuchAddressException {

		return _uniquePersistenceFinderByERC_C.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {externalReferenceCode, companyId});
	}

	/**
	 * Returns the address where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching address, or <code>null</code> if a matching address could not be found
	 */
	@Override
	public Address fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_C.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {externalReferenceCode, companyId}, useFinderCache);
	}

	/**
	 * Removes the address where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the address that was removed
	 */
	@Override
	public Address removeByERC_C(String externalReferenceCode, long companyId)
		throws NoSuchAddressException {

		Address address = findByERC_C(externalReferenceCode, companyId);

		return remove(address);
	}

	/**
	 * Returns the number of addresses where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching addresses
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		return _uniquePersistenceFinderByERC_C.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {externalReferenceCode, companyId});
	}

	public AddressPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("primary", "primary_");

		setDBColumnNames(dbColumnNames);

		setModelClass(Address.class);

		setModelImplClass(AddressImpl.class);
		setModelPKClass(long.class);

		setTable(AddressTable.INSTANCE);
	}

	/**
	 * Creates a new address with the primary key. Does not add the address to the database.
	 *
	 * @param addressId the primary key for the new address
	 * @return the new address
	 */
	@Override
	public Address create(long addressId) {
		Address address = new AddressImpl();

		address.setNew(true);
		address.setPrimaryKey(addressId);

		String uuid = PortalUUIDUtil.generate();

		address.setUuid(uuid);

		address.setCompanyId(CompanyThreadLocal.getCompanyId());

		return address;
	}

	/**
	 * Removes the address with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param addressId the primary key of the address
	 * @return the address that was removed
	 * @throws NoSuchAddressException if a address with the primary key could not be found
	 */
	@Override
	public Address remove(long addressId) throws NoSuchAddressException {
		return remove((Serializable)addressId);
	}

	@Override
	protected Address removeImpl(Address address) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(address)) {
				address = (Address)session.get(
					AddressImpl.class, address.getPrimaryKeyObj());
			}

			if ((address != null) &&
				CTPersistenceHelperUtil.isRemove(address)) {

				session.delete(address);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (address != null) {
			clearCache(address);
		}

		return address;
	}

	@Override
	public Address updateImpl(Address address) {
		boolean isNew = address.isNew();

		if (!(address instanceof AddressModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(address.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(address);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in address proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom Address implementation " +
					address.getClass());
		}

		AddressModelImpl addressModelImpl = (AddressModelImpl)address;

		if (Validator.isNull(address.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			address.setUuid(uuid);
		}

		if (Validator.isNull(address.getExternalReferenceCode())) {
			address.setExternalReferenceCode(address.getUuid());
		}
		else {
			if (!Objects.equals(
					addressModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					address.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = address.getCompanyId();

					long groupId = 0;

					long classPK = 0;

					if (!isNew) {
						classPK = address.getPrimaryKey();
					}

					try {
						address.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								Address.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								address.getExternalReferenceCode(), null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			Address ercAddress = fetchByERC_C(
				address.getExternalReferenceCode(), address.getCompanyId());

			if (isNew) {
				if (ercAddress != null) {
					throw new DuplicateAddressExternalReferenceCodeException(
						"Duplicate address with external reference code " +
							address.getExternalReferenceCode() +
								" and company " + address.getCompanyId());
				}
			}
			else {
				if ((ercAddress != null) &&
					(address.getAddressId() != ercAddress.getAddressId())) {

					throw new DuplicateAddressExternalReferenceCodeException(
						"Duplicate address with external reference code " +
							address.getExternalReferenceCode() +
								" and company " + address.getCompanyId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (address.getCreateDate() == null)) {
			if (serviceContext == null) {
				address.setCreateDate(date);
			}
			else {
				address.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!addressModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				address.setModifiedDate(date);
			}
			else {
				address.setModifiedDate(serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (CTPersistenceHelperUtil.isInsert(address)) {
				if (!isNew) {
					session.evict(
						AddressImpl.class, address.getPrimaryKeyObj());
				}

				session.save(address);
			}
			else {
				address = (Address)session.merge(address);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(address, false);

		if (isNew) {
			address.setNew(false);
		}

		address.resetOriginalValues();

		return address;
	}

	/**
	 * Returns the address with the primary key or throws a <code>NoSuchAddressException</code> if it could not be found.
	 *
	 * @param addressId the primary key of the address
	 * @return the address
	 * @throws NoSuchAddressException if a address with the primary key could not be found
	 */
	@Override
	public Address findByPrimaryKey(long addressId)
		throws NoSuchAddressException {

		return findByPrimaryKey((Serializable)addressId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return CTPersistenceHelperUtil.getCTPersistenceHelper();
	}

	/**
	 * Returns the address with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param addressId the primary key of the address
	 * @return the address, or <code>null</code> if a address with the primary key could not be found
	 */
	@Override
	public Address fetchByPrimaryKey(long addressId) {
		return fetchByPrimaryKey((Serializable)addressId);
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
		return "addressId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_ADDRESS;
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
		return AddressModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "Address";
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
		ctMergeColumnNames.add("countryId");
		ctMergeColumnNames.add("listTypeId");
		ctMergeColumnNames.add("regionId");
		ctMergeColumnNames.add("city");
		ctMergeColumnNames.add("description");
		ctMergeColumnNames.add("latitude");
		ctMergeColumnNames.add("longitude");
		ctMergeColumnNames.add("mailing");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("primary_");
		ctMergeColumnNames.add("street1");
		ctMergeColumnNames.add("street2");
		ctMergeColumnNames.add("street3");
		ctMergeColumnNames.add("subtype");
		ctMergeColumnNames.add("validationDate");
		ctMergeColumnNames.add("validationStatus");
		ctMergeColumnNames.add("zip");
		ctMergeColumnNames.add("status");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("addressId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "companyId"});
	}

	/**
	 * Initializes the address persistence.
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
			_SQL_SELECT_ADDRESS_WHERE, _SQL_COUNT_ADDRESS_WHERE,
			AddressModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"address.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
				true, true, Address::getUuid));

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
				_SQL_SELECT_ADDRESS_WHERE, _SQL_COUNT_ADDRESS_WHERE,
				AddressModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"address.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
					true, true, Address::getUuid),
				new FinderColumn<>(
					"address.", "companyId", FinderColumn.Type.LONG, "=", true,
					true, Address::getCompanyId));

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
				_SQL_SELECT_ADDRESS_WHERE, _SQL_COUNT_ADDRESS_WHERE,
				AddressModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"address.", "companyId", FinderColumn.Type.LONG, "=", true,
					true, Address::getCompanyId));

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
				_SQL_SELECT_ADDRESS_WHERE, _SQL_COUNT_ADDRESS_WHERE,
				AddressModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"address.", "userId", FinderColumn.Type.LONG, "=", true,
					true, Address::getUserId));

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
				_SQL_SELECT_ADDRESS_WHERE, _SQL_COUNT_ADDRESS_WHERE,
				AddressModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"address.", "countryId", FinderColumn.Type.LONG, "=", true,
					true, Address::getCountryId));

		_collectionPersistenceFinderByRegionId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByRegionId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"regionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByRegionId",
					new String[] {Long.class.getName()},
					new String[] {"regionId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByRegionId", new String[] {Long.class.getName()},
					new String[] {"regionId"}, false),
				_SQL_SELECT_ADDRESS_WHERE, _SQL_COUNT_ADDRESS_WHERE,
				AddressModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"address.", "regionId", FinderColumn.Type.LONG, "=", true,
					true, Address::getRegionId));

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
			_SQL_SELECT_ADDRESS_WHERE, _SQL_COUNT_ADDRESS_WHERE,
			AddressModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"address.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, Address::getCompanyId),
			new FinderColumn<>(
				"address.", "classNameId", FinderColumn.Type.LONG, "=", true,
				true, Address::getClassNameId));

		_collectionPersistenceFinderByCN_CPK =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCN_CPK",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"classNameId", "classPK"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByCN_CPK",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"classNameId", "classPK"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByCN_CPK",
					new String[] {Long.class.getName(), Long.class.getName()},
					new String[] {"classNameId", "classPK"}, false),
				_SQL_SELECT_ADDRESS_WHERE, _SQL_COUNT_ADDRESS_WHERE,
				AddressModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"address.", "classNameId", FinderColumn.Type.LONG, "=",
					true, true, Address::getClassNameId),
				new FinderColumn<>(
					"address.", "classPK", FinderColumn.Type.LONG, "=", true,
					true, Address::getClassPK));

		_collectionPersistenceFinderByC_C_C = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
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
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {"companyId", "classNameId", "classPK"}, false),
			_SQL_SELECT_ADDRESS_WHERE, _SQL_COUNT_ADDRESS_WHERE,
			AddressModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"address.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, Address::getCompanyId),
			new FinderColumn<>(
				"address.", "classNameId", FinderColumn.Type.LONG, "=", true,
				true, Address::getClassNameId),
			new FinderColumn<>(
				"address.", "classPK", FinderColumn.Type.LONG, "=", true, true,
				Address::getClassPK));

		_collectionPersistenceFinderByC_C_C_L =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C_C_L",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"companyId", "classNameId", "classPK", "listTypeId"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C_C_L",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Long.class.getName()
					},
					new String[] {
						"companyId", "classNameId", "classPK", "listTypeId"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_C_C_L",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Long.class.getName()
					},
					new String[] {
						"companyId", "classNameId", "classPK", "listTypeId"
					},
					false),
				_SQL_SELECT_ADDRESS_WHERE, _SQL_COUNT_ADDRESS_WHERE,
				AddressModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"address.", "companyId", FinderColumn.Type.LONG, "=", true,
					true, Address::getCompanyId),
				new FinderColumn<>(
					"address.", "classNameId", FinderColumn.Type.LONG, "=",
					true, true, Address::getClassNameId),
				new FinderColumn<>(
					"address.", "classPK", FinderColumn.Type.LONG, "=", true,
					true, Address::getClassPK),
				new ArrayableFinderColumn<>(
					"address.", "listTypeId", FinderColumn.Type.LONG, "=",
					false, true, true, Address::getListTypeId));

		_collectionPersistenceFinderByC_C_C_M =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C_C_M",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"companyId", "classNameId", "classPK", "mailing"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C_C_M",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {
						"companyId", "classNameId", "classPK", "mailing"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C_C_M",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {
						"companyId", "classNameId", "classPK", "mailing"
					},
					false),
				_SQL_SELECT_ADDRESS_WHERE, _SQL_COUNT_ADDRESS_WHERE,
				AddressModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"address.", "companyId", FinderColumn.Type.LONG, "=", true,
					true, Address::getCompanyId),
				new FinderColumn<>(
					"address.", "classNameId", FinderColumn.Type.LONG, "=",
					true, true, Address::getClassNameId),
				new FinderColumn<>(
					"address.", "classPK", FinderColumn.Type.LONG, "=", true,
					true, Address::getClassPK),
				new FinderColumn<>(
					"address.", "mailing", FinderColumn.Type.BOOLEAN, "=", true,
					true, Address::isMailing));

		_collectionPersistenceFinderByC_C_C_P =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C_C_P",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"companyId", "classNameId", "classPK", "primary_"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C_C_P",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {
						"companyId", "classNameId", "classPK", "primary_"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C_C_P",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {
						"companyId", "classNameId", "classPK", "primary_"
					},
					false),
				_SQL_SELECT_ADDRESS_WHERE, _SQL_COUNT_ADDRESS_WHERE,
				AddressModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"address.", "companyId", FinderColumn.Type.LONG, "=", true,
					true, Address::getCompanyId),
				new FinderColumn<>(
					"address.", "classNameId", FinderColumn.Type.LONG, "=",
					true, true, Address::getClassNameId),
				new FinderColumn<>(
					"address.", "classPK", FinderColumn.Type.LONG, "=", true,
					true, Address::getClassPK),
				new FinderColumn<>(
					"address.", "primary", "primary_",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					Address::isPrimary));

		_uniquePersistenceFinderByERC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "companyId"}, 0, 1,
				false, convertNullFunction(Address::getExternalReferenceCode),
				Address::getCompanyId),
			_SQL_SELECT_ADDRESS_WHERE, "",
			new FinderColumn<>(
				"address.", "externalReferenceCode", FinderColumn.Type.STRING,
				"=", true, true, Address::getExternalReferenceCode),
			new FinderColumn<>(
				"address.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, Address::getCompanyId));

		AddressUtil.setPersistence(this);
	}

	public void destroy() {
		AddressUtil.setPersistence(null);

		EntityCacheUtil.removeCache(AddressImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		AddressModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_ADDRESS =
		"SELECT address FROM Address address";

	private static final String _SQL_SELECT_ADDRESS_WHERE =
		"SELECT address FROM Address address WHERE ";

	private static final String _SQL_COUNT_ADDRESS_WHERE =
		"SELECT COUNT(address) FROM Address address WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No Address exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		AddressPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "primary"});

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-973066251