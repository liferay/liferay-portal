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
import com.liferay.portal.kernel.exception.DuplicateCountryExternalReferenceCodeException;
import com.liferay.portal.kernel.exception.NoSuchCountryException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.CountryTable;
import com.liferay.portal.kernel.sanitizer.Sanitizer;
import com.liferay.portal.kernel.sanitizer.SanitizerException;
import com.liferay.portal.kernel.sanitizer.SanitizerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.CountryLocalizationPersistence;
import com.liferay.portal.kernel.service.persistence.CountryPersistence;
import com.liferay.portal.kernel.service.persistence.CountryUtil;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelperUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.FilterCollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.model.impl.CountryImpl;
import com.liferay.portal.model.impl.CountryModelImpl;

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
 * The persistence implementation for the country service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class CountryPersistenceImpl
	extends BasePersistenceImpl<Country, NoSuchCountryException>
	implements CountryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>CountryUtil</code> to access the country persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		CountryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FilterCollectionPersistenceFinder<Country, NoSuchCountryException>
		_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the countries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CountryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of countries
	 * @param end the upper bound of the range of countries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching countries
	 */
	@Override
	public List<Country> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<Country> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first country in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching country
	 * @throws NoSuchCountryException if a matching country could not be found
	 */
	@Override
	public Country findByUuid_First(
			String uuid, OrderByComparator<Country> orderByComparator)
		throws NoSuchCountryException {

		return _collectionPersistenceFinderByUuid.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid},
			orderByComparator);
	}

	/**
	 * Returns the first country in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching country, or <code>null</code> if a matching country could not be found
	 */
	@Override
	public Country fetchByUuid_First(
		String uuid, OrderByComparator<Country> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the countries that the user has permissions to view where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CountryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of countries
	 * @param end the upper bound of the range of countries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching countries that the user has permission to view
	 */
	@Override
	public List<Country> filterFindByUuid(
		String uuid, int start, int end,
		OrderByComparator<Country> orderByComparator) {

		return _collectionPersistenceFinderByUuid.filterFind(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid}, start, end,
			orderByComparator);
	}

	/**
	 * Removes all the countries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	/**
	 * Returns the number of countries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching countries
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	/**
	 * Returns the number of countries that the user has permission to view where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching countries that the user has permission to view
	 */
	@Override
	public int filterCountByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.filterCount(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	private FilterCollectionPersistenceFinder<Country, NoSuchCountryException>
		_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the countries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CountryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of countries
	 * @param end the upper bound of the range of countries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching countries
	 */
	@Override
	public List<Country> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<Country> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first country in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching country
	 * @throws NoSuchCountryException if a matching country could not be found
	 */
	@Override
	public Country findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<Country> orderByComparator)
		throws NoSuchCountryException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			orderByComparator);
	}

	/**
	 * Returns the first country in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching country, or <code>null</code> if a matching country could not be found
	 */
	@Override
	public Country fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<Country> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the countries that the user has permissions to view where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CountryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of countries
	 * @param end the upper bound of the range of countries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching countries that the user has permission to view
	 */
	@Override
	public List<Country> filterFindByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<Country> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.filterFind(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			start, end, orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the countries where uuid = &#63; and companyId = &#63; from the database.
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
	 * Returns the number of countries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching countries
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId});
	}

	/**
	 * Returns the number of countries that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching countries that the user has permission to view
	 */
	@Override
	public int filterCountByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.filterCount(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			companyId, 0);
	}

	private FilterCollectionPersistenceFinder<Country, NoSuchCountryException>
		_collectionPersistenceFinderByCompanyId;

	/**
	 * Returns an ordered range of all the countries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CountryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of countries
	 * @param end the upper bound of the range of countries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching countries
	 */
	@Override
	public List<Country> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<Country> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByCompanyId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first country in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching country
	 * @throws NoSuchCountryException if a matching country could not be found
	 */
	@Override
	public Country findByCompanyId_First(
			long companyId, OrderByComparator<Country> orderByComparator)
		throws NoSuchCountryException {

		return _collectionPersistenceFinderByCompanyId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			orderByComparator);
	}

	/**
	 * Returns the first country in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching country, or <code>null</code> if a matching country could not be found
	 */
	@Override
	public Country fetchByCompanyId_First(
		long companyId, OrderByComparator<Country> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the countries that the user has permissions to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CountryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of countries
	 * @param end the upper bound of the range of countries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching countries that the user has permission to view
	 */
	@Override
	public List<Country> filterFindByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<Country> orderByComparator) {

		return _collectionPersistenceFinderByCompanyId.filterFind(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId}, start,
			end, orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the countries where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		_collectionPersistenceFinderByCompanyId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId});
	}

	/**
	 * Returns the number of countries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching countries
	 */
	@Override
	public int countByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId});
	}

	/**
	 * Returns the number of countries that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching countries that the user has permission to view
	 */
	@Override
	public int filterCountByCompanyId(long companyId) {
		return _collectionPersistenceFinderByCompanyId.filterCount(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId},
			companyId, 0);
	}

	private FilterCollectionPersistenceFinder<Country, NoSuchCountryException>
		_collectionPersistenceFinderByActive;

	/**
	 * Returns an ordered range of all the countries where active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CountryModelImpl</code>.
	 * </p>
	 *
	 * @param active the active
	 * @param start the lower bound of the range of countries
	 * @param end the upper bound of the range of countries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching countries
	 */
	@Override
	public List<Country> findByActive(
		boolean active, int start, int end,
		OrderByComparator<Country> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByActive.find(
			FinderCacheUtil.getFinderCache(), new Object[] {active}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first country in the ordered set where active = &#63;.
	 *
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching country
	 * @throws NoSuchCountryException if a matching country could not be found
	 */
	@Override
	public Country findByActive_First(
			boolean active, OrderByComparator<Country> orderByComparator)
		throws NoSuchCountryException {

		return _collectionPersistenceFinderByActive.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {active},
			orderByComparator);
	}

	/**
	 * Returns the first country in the ordered set where active = &#63;.
	 *
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching country, or <code>null</code> if a matching country could not be found
	 */
	@Override
	public Country fetchByActive_First(
		boolean active, OrderByComparator<Country> orderByComparator) {

		return _collectionPersistenceFinderByActive.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {active},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the countries that the user has permissions to view where active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CountryModelImpl</code>.
	 * </p>
	 *
	 * @param active the active
	 * @param start the lower bound of the range of countries
	 * @param end the upper bound of the range of countries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching countries that the user has permission to view
	 */
	@Override
	public List<Country> filterFindByActive(
		boolean active, int start, int end,
		OrderByComparator<Country> orderByComparator) {

		return _collectionPersistenceFinderByActive.filterFind(
			FinderCacheUtil.getFinderCache(), new Object[] {active}, start, end,
			orderByComparator);
	}

	/**
	 * Removes all the countries where active = &#63; from the database.
	 *
	 * @param active the active
	 */
	@Override
	public void removeByActive(boolean active) {
		_collectionPersistenceFinderByActive.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {active});
	}

	/**
	 * Returns the number of countries where active = &#63;.
	 *
	 * @param active the active
	 * @return the number of matching countries
	 */
	@Override
	public int countByActive(boolean active) {
		return _collectionPersistenceFinderByActive.count(
			FinderCacheUtil.getFinderCache(), new Object[] {active});
	}

	/**
	 * Returns the number of countries that the user has permission to view where active = &#63;.
	 *
	 * @param active the active
	 * @return the number of matching countries that the user has permission to view
	 */
	@Override
	public int filterCountByActive(boolean active) {
		return _collectionPersistenceFinderByActive.filterCount(
			FinderCacheUtil.getFinderCache(), new Object[] {active});
	}

	private UniquePersistenceFinder<Country, NoSuchCountryException>
		_uniquePersistenceFinderByC_A2;

	/**
	 * Returns the country where companyId = &#63; and a2 = &#63; or throws a <code>NoSuchCountryException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param a2 the a2
	 * @return the matching country
	 * @throws NoSuchCountryException if a matching country could not be found
	 */
	@Override
	public Country findByC_A2(long companyId, String a2)
		throws NoSuchCountryException {

		return _uniquePersistenceFinderByC_A2.find(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, a2});
	}

	/**
	 * Returns the country where companyId = &#63; and a2 = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param a2 the a2
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching country, or <code>null</code> if a matching country could not be found
	 */
	@Override
	public Country fetchByC_A2(
		long companyId, String a2, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_A2.fetch(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, a2},
			useFinderCache);
	}

	/**
	 * Removes the country where companyId = &#63; and a2 = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param a2 the a2
	 * @return the country that was removed
	 */
	@Override
	public Country removeByC_A2(long companyId, String a2)
		throws NoSuchCountryException {

		Country country = findByC_A2(companyId, a2);

		return remove(country);
	}

	/**
	 * Returns the number of countries where companyId = &#63; and a2 = &#63;.
	 *
	 * @param companyId the company ID
	 * @param a2 the a2
	 * @return the number of matching countries
	 */
	@Override
	public int countByC_A2(long companyId, String a2) {
		return _uniquePersistenceFinderByC_A2.count(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, a2});
	}

	private UniquePersistenceFinder<Country, NoSuchCountryException>
		_uniquePersistenceFinderByC_A3;

	/**
	 * Returns the country where companyId = &#63; and a3 = &#63; or throws a <code>NoSuchCountryException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param a3 the a3
	 * @return the matching country
	 * @throws NoSuchCountryException if a matching country could not be found
	 */
	@Override
	public Country findByC_A3(long companyId, String a3)
		throws NoSuchCountryException {

		return _uniquePersistenceFinderByC_A3.find(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, a3});
	}

	/**
	 * Returns the country where companyId = &#63; and a3 = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param a3 the a3
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching country, or <code>null</code> if a matching country could not be found
	 */
	@Override
	public Country fetchByC_A3(
		long companyId, String a3, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_A3.fetch(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, a3},
			useFinderCache);
	}

	/**
	 * Removes the country where companyId = &#63; and a3 = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param a3 the a3
	 * @return the country that was removed
	 */
	@Override
	public Country removeByC_A3(long companyId, String a3)
		throws NoSuchCountryException {

		Country country = findByC_A3(companyId, a3);

		return remove(country);
	}

	/**
	 * Returns the number of countries where companyId = &#63; and a3 = &#63;.
	 *
	 * @param companyId the company ID
	 * @param a3 the a3
	 * @return the number of matching countries
	 */
	@Override
	public int countByC_A3(long companyId, String a3) {
		return _uniquePersistenceFinderByC_A3.count(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, a3});
	}

	private FilterCollectionPersistenceFinder<Country, NoSuchCountryException>
		_collectionPersistenceFinderByC_Active;

	/**
	 * Returns an ordered range of all the countries where companyId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CountryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param start the lower bound of the range of countries
	 * @param end the upper bound of the range of countries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching countries
	 */
	@Override
	public List<Country> findByC_Active(
		long companyId, boolean active, int start, int end,
		OrderByComparator<Country> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByC_Active.find(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, active},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first country in the ordered set where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching country
	 * @throws NoSuchCountryException if a matching country could not be found
	 */
	@Override
	public Country findByC_Active_First(
			long companyId, boolean active,
			OrderByComparator<Country> orderByComparator)
		throws NoSuchCountryException {

		return _collectionPersistenceFinderByC_Active.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, active},
			orderByComparator);
	}

	/**
	 * Returns the first country in the ordered set where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching country, or <code>null</code> if a matching country could not be found
	 */
	@Override
	public Country fetchByC_Active_First(
		long companyId, boolean active,
		OrderByComparator<Country> orderByComparator) {

		return _collectionPersistenceFinderByC_Active.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, active},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the countries that the user has permissions to view where companyId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CountryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param start the lower bound of the range of countries
	 * @param end the upper bound of the range of countries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching countries that the user has permission to view
	 */
	@Override
	public List<Country> filterFindByC_Active(
		long companyId, boolean active, int start, int end,
		OrderByComparator<Country> orderByComparator) {

		return _collectionPersistenceFinderByC_Active.filterFind(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, active},
			start, end, orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the countries where companyId = &#63; and active = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 */
	@Override
	public void removeByC_Active(long companyId, boolean active) {
		_collectionPersistenceFinderByC_Active.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, active});
	}

	/**
	 * Returns the number of countries where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @return the number of matching countries
	 */
	@Override
	public int countByC_Active(long companyId, boolean active) {
		return _collectionPersistenceFinderByC_Active.count(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, active});
	}

	/**
	 * Returns the number of countries that the user has permission to view where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @return the number of matching countries that the user has permission to view
	 */
	@Override
	public int filterCountByC_Active(long companyId, boolean active) {
		return _collectionPersistenceFinderByC_Active.filterCount(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, active},
			companyId, 0);
	}

	private UniquePersistenceFinder<Country, NoSuchCountryException>
		_uniquePersistenceFinderByC_Name;

	/**
	 * Returns the country where companyId = &#63; and name = &#63; or throws a <code>NoSuchCountryException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the matching country
	 * @throws NoSuchCountryException if a matching country could not be found
	 */
	@Override
	public Country findByC_Name(long companyId, String name)
		throws NoSuchCountryException {

		return _uniquePersistenceFinderByC_Name.find(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, name});
	}

	/**
	 * Returns the country where companyId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching country, or <code>null</code> if a matching country could not be found
	 */
	@Override
	public Country fetchByC_Name(
		long companyId, String name, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_Name.fetch(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, name},
			useFinderCache);
	}

	/**
	 * Removes the country where companyId = &#63; and name = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the country that was removed
	 */
	@Override
	public Country removeByC_Name(long companyId, String name)
		throws NoSuchCountryException {

		Country country = findByC_Name(companyId, name);

		return remove(country);
	}

	/**
	 * Returns the number of countries where companyId = &#63; and name = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the number of matching countries
	 */
	@Override
	public int countByC_Name(long companyId, String name) {
		return _uniquePersistenceFinderByC_Name.count(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, name});
	}

	private UniquePersistenceFinder<Country, NoSuchCountryException>
		_uniquePersistenceFinderByC_Number;

	/**
	 * Returns the country where companyId = &#63; and number = &#63; or throws a <code>NoSuchCountryException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param number the number
	 * @return the matching country
	 * @throws NoSuchCountryException if a matching country could not be found
	 */
	@Override
	public Country findByC_Number(long companyId, String number)
		throws NoSuchCountryException {

		return _uniquePersistenceFinderByC_Number.find(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, number});
	}

	/**
	 * Returns the country where companyId = &#63; and number = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param number the number
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching country, or <code>null</code> if a matching country could not be found
	 */
	@Override
	public Country fetchByC_Number(
		long companyId, String number, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_Number.fetch(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, number},
			useFinderCache);
	}

	/**
	 * Removes the country where companyId = &#63; and number = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param number the number
	 * @return the country that was removed
	 */
	@Override
	public Country removeByC_Number(long companyId, String number)
		throws NoSuchCountryException {

		Country country = findByC_Number(companyId, number);

		return remove(country);
	}

	/**
	 * Returns the number of countries where companyId = &#63; and number = &#63;.
	 *
	 * @param companyId the company ID
	 * @param number the number
	 * @return the number of matching countries
	 */
	@Override
	public int countByC_Number(long companyId, String number) {
		return _uniquePersistenceFinderByC_Number.count(
			FinderCacheUtil.getFinderCache(), new Object[] {companyId, number});
	}

	private FilterCollectionPersistenceFinder<Country, NoSuchCountryException>
		_collectionPersistenceFinderByC_A_B;

	/**
	 * Returns an ordered range of all the countries where companyId = &#63; and active = &#63; and billingAllowed = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CountryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param billingAllowed the billing allowed
	 * @param start the lower bound of the range of countries
	 * @param end the upper bound of the range of countries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching countries
	 */
	@Override
	public List<Country> findByC_A_B(
		long companyId, boolean active, boolean billingAllowed, int start,
		int end, OrderByComparator<Country> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_A_B.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, active, billingAllowed}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first country in the ordered set where companyId = &#63; and active = &#63; and billingAllowed = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param billingAllowed the billing allowed
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching country
	 * @throws NoSuchCountryException if a matching country could not be found
	 */
	@Override
	public Country findByC_A_B_First(
			long companyId, boolean active, boolean billingAllowed,
			OrderByComparator<Country> orderByComparator)
		throws NoSuchCountryException {

		return _collectionPersistenceFinderByC_A_B.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, active, billingAllowed},
			orderByComparator);
	}

	/**
	 * Returns the first country in the ordered set where companyId = &#63; and active = &#63; and billingAllowed = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param billingAllowed the billing allowed
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching country, or <code>null</code> if a matching country could not be found
	 */
	@Override
	public Country fetchByC_A_B_First(
		long companyId, boolean active, boolean billingAllowed,
		OrderByComparator<Country> orderByComparator) {

		return _collectionPersistenceFinderByC_A_B.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, active, billingAllowed},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the countries that the user has permissions to view where companyId = &#63; and active = &#63; and billingAllowed = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CountryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param billingAllowed the billing allowed
	 * @param start the lower bound of the range of countries
	 * @param end the upper bound of the range of countries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching countries that the user has permission to view
	 */
	@Override
	public List<Country> filterFindByC_A_B(
		long companyId, boolean active, boolean billingAllowed, int start,
		int end, OrderByComparator<Country> orderByComparator) {

		return _collectionPersistenceFinderByC_A_B.filterFind(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, active, billingAllowed}, start, end,
			orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the countries where companyId = &#63; and active = &#63; and billingAllowed = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param billingAllowed the billing allowed
	 */
	@Override
	public void removeByC_A_B(
		long companyId, boolean active, boolean billingAllowed) {

		_collectionPersistenceFinderByC_A_B.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, active, billingAllowed});
	}

	/**
	 * Returns the number of countries where companyId = &#63; and active = &#63; and billingAllowed = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param billingAllowed the billing allowed
	 * @return the number of matching countries
	 */
	@Override
	public int countByC_A_B(
		long companyId, boolean active, boolean billingAllowed) {

		return _collectionPersistenceFinderByC_A_B.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, active, billingAllowed});
	}

	/**
	 * Returns the number of countries that the user has permission to view where companyId = &#63; and active = &#63; and billingAllowed = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param billingAllowed the billing allowed
	 * @return the number of matching countries that the user has permission to view
	 */
	@Override
	public int filterCountByC_A_B(
		long companyId, boolean active, boolean billingAllowed) {

		return _collectionPersistenceFinderByC_A_B.filterCount(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, active, billingAllowed}, companyId, 0);
	}

	private FilterCollectionPersistenceFinder<Country, NoSuchCountryException>
		_collectionPersistenceFinderByC_A_S;

	/**
	 * Returns an ordered range of all the countries where companyId = &#63; and active = &#63; and shippingAllowed = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CountryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param shippingAllowed the shipping allowed
	 * @param start the lower bound of the range of countries
	 * @param end the upper bound of the range of countries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching countries
	 */
	@Override
	public List<Country> findByC_A_S(
		long companyId, boolean active, boolean shippingAllowed, int start,
		int end, OrderByComparator<Country> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_A_S.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, active, shippingAllowed}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first country in the ordered set where companyId = &#63; and active = &#63; and shippingAllowed = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param shippingAllowed the shipping allowed
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching country
	 * @throws NoSuchCountryException if a matching country could not be found
	 */
	@Override
	public Country findByC_A_S_First(
			long companyId, boolean active, boolean shippingAllowed,
			OrderByComparator<Country> orderByComparator)
		throws NoSuchCountryException {

		return _collectionPersistenceFinderByC_A_S.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, active, shippingAllowed},
			orderByComparator);
	}

	/**
	 * Returns the first country in the ordered set where companyId = &#63; and active = &#63; and shippingAllowed = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param shippingAllowed the shipping allowed
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching country, or <code>null</code> if a matching country could not be found
	 */
	@Override
	public Country fetchByC_A_S_First(
		long companyId, boolean active, boolean shippingAllowed,
		OrderByComparator<Country> orderByComparator) {

		return _collectionPersistenceFinderByC_A_S.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, active, shippingAllowed},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the countries that the user has permissions to view where companyId = &#63; and active = &#63; and shippingAllowed = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CountryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param shippingAllowed the shipping allowed
	 * @param start the lower bound of the range of countries
	 * @param end the upper bound of the range of countries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching countries that the user has permission to view
	 */
	@Override
	public List<Country> filterFindByC_A_S(
		long companyId, boolean active, boolean shippingAllowed, int start,
		int end, OrderByComparator<Country> orderByComparator) {

		return _collectionPersistenceFinderByC_A_S.filterFind(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, active, shippingAllowed}, start, end,
			orderByComparator, companyId, 0);
	}

	/**
	 * Removes all the countries where companyId = &#63; and active = &#63; and shippingAllowed = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param shippingAllowed the shipping allowed
	 */
	@Override
	public void removeByC_A_S(
		long companyId, boolean active, boolean shippingAllowed) {

		_collectionPersistenceFinderByC_A_S.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, active, shippingAllowed});
	}

	/**
	 * Returns the number of countries where companyId = &#63; and active = &#63; and shippingAllowed = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param shippingAllowed the shipping allowed
	 * @return the number of matching countries
	 */
	@Override
	public int countByC_A_S(
		long companyId, boolean active, boolean shippingAllowed) {

		return _collectionPersistenceFinderByC_A_S.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, active, shippingAllowed});
	}

	/**
	 * Returns the number of countries that the user has permission to view where companyId = &#63; and active = &#63; and shippingAllowed = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param shippingAllowed the shipping allowed
	 * @return the number of matching countries that the user has permission to view
	 */
	@Override
	public int filterCountByC_A_S(
		long companyId, boolean active, boolean shippingAllowed) {

		return _collectionPersistenceFinderByC_A_S.filterCount(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, active, shippingAllowed}, companyId, 0);
	}

	private FilterCollectionPersistenceFinder<Country, NoSuchCountryException>
		_collectionPersistenceFinderByC_A_B_G;

	/**
	 * Returns an ordered range of all the countries where countryId = &#63; and active = &#63; and billingAllowed = &#63; and groupFilterEnabled = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CountryModelImpl</code>.
	 * </p>
	 *
	 * @param countryId the country ID
	 * @param active the active
	 * @param billingAllowed the billing allowed
	 * @param groupFilterEnabled the group filter enabled
	 * @param start the lower bound of the range of countries
	 * @param end the upper bound of the range of countries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching countries
	 */
	@Override
	public List<Country> findByC_A_B_G(
		long countryId, boolean active, boolean billingAllowed,
		boolean groupFilterEnabled, int start, int end,
		OrderByComparator<Country> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByC_A_B_G.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				countryId, active, billingAllowed, groupFilterEnabled
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first country in the ordered set where countryId = &#63; and active = &#63; and billingAllowed = &#63; and groupFilterEnabled = &#63;.
	 *
	 * @param countryId the country ID
	 * @param active the active
	 * @param billingAllowed the billing allowed
	 * @param groupFilterEnabled the group filter enabled
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching country
	 * @throws NoSuchCountryException if a matching country could not be found
	 */
	@Override
	public Country findByC_A_B_G_First(
			long countryId, boolean active, boolean billingAllowed,
			boolean groupFilterEnabled,
			OrderByComparator<Country> orderByComparator)
		throws NoSuchCountryException {

		return _collectionPersistenceFinderByC_A_B_G.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				countryId, active, billingAllowed, groupFilterEnabled
			},
			orderByComparator);
	}

	/**
	 * Returns the first country in the ordered set where countryId = &#63; and active = &#63; and billingAllowed = &#63; and groupFilterEnabled = &#63;.
	 *
	 * @param countryId the country ID
	 * @param active the active
	 * @param billingAllowed the billing allowed
	 * @param groupFilterEnabled the group filter enabled
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching country, or <code>null</code> if a matching country could not be found
	 */
	@Override
	public Country fetchByC_A_B_G_First(
		long countryId, boolean active, boolean billingAllowed,
		boolean groupFilterEnabled,
		OrderByComparator<Country> orderByComparator) {

		return _collectionPersistenceFinderByC_A_B_G.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				countryId, active, billingAllowed, groupFilterEnabled
			},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the countries that the user has permissions to view where countryId = &#63; and active = &#63; and billingAllowed = &#63; and groupFilterEnabled = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CountryModelImpl</code>.
	 * </p>
	 *
	 * @param countryId the country ID
	 * @param active the active
	 * @param billingAllowed the billing allowed
	 * @param groupFilterEnabled the group filter enabled
	 * @param start the lower bound of the range of countries
	 * @param end the upper bound of the range of countries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching countries that the user has permission to view
	 */
	@Override
	public List<Country> filterFindByC_A_B_G(
		long countryId, boolean active, boolean billingAllowed,
		boolean groupFilterEnabled, int start, int end,
		OrderByComparator<Country> orderByComparator) {

		return _collectionPersistenceFinderByC_A_B_G.filterFind(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				countryId, active, billingAllowed, groupFilterEnabled
			},
			start, end, orderByComparator);
	}

	/**
	 * Removes all the countries where countryId = &#63; and active = &#63; and billingAllowed = &#63; and groupFilterEnabled = &#63; from the database.
	 *
	 * @param countryId the country ID
	 * @param active the active
	 * @param billingAllowed the billing allowed
	 * @param groupFilterEnabled the group filter enabled
	 */
	@Override
	public void removeByC_A_B_G(
		long countryId, boolean active, boolean billingAllowed,
		boolean groupFilterEnabled) {

		_collectionPersistenceFinderByC_A_B_G.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				countryId, active, billingAllowed, groupFilterEnabled
			});
	}

	/**
	 * Returns the number of countries where countryId = &#63; and active = &#63; and billingAllowed = &#63; and groupFilterEnabled = &#63;.
	 *
	 * @param countryId the country ID
	 * @param active the active
	 * @param billingAllowed the billing allowed
	 * @param groupFilterEnabled the group filter enabled
	 * @return the number of matching countries
	 */
	@Override
	public int countByC_A_B_G(
		long countryId, boolean active, boolean billingAllowed,
		boolean groupFilterEnabled) {

		return _collectionPersistenceFinderByC_A_B_G.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				countryId, active, billingAllowed, groupFilterEnabled
			});
	}

	/**
	 * Returns the number of countries that the user has permission to view where countryId = &#63; and active = &#63; and billingAllowed = &#63; and groupFilterEnabled = &#63;.
	 *
	 * @param countryId the country ID
	 * @param active the active
	 * @param billingAllowed the billing allowed
	 * @param groupFilterEnabled the group filter enabled
	 * @return the number of matching countries that the user has permission to view
	 */
	@Override
	public int filterCountByC_A_B_G(
		long countryId, boolean active, boolean billingAllowed,
		boolean groupFilterEnabled) {

		return _collectionPersistenceFinderByC_A_B_G.filterCount(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				countryId, active, billingAllowed, groupFilterEnabled
			});
	}

	private FilterCollectionPersistenceFinder<Country, NoSuchCountryException>
		_collectionPersistenceFinderByC_A_G_S;

	/**
	 * Returns an ordered range of all the countries where countryId = &#63; and active = &#63; and groupFilterEnabled = &#63; and shippingAllowed = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CountryModelImpl</code>.
	 * </p>
	 *
	 * @param countryId the country ID
	 * @param active the active
	 * @param groupFilterEnabled the group filter enabled
	 * @param shippingAllowed the shipping allowed
	 * @param start the lower bound of the range of countries
	 * @param end the upper bound of the range of countries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching countries
	 */
	@Override
	public List<Country> findByC_A_G_S(
		long countryId, boolean active, boolean groupFilterEnabled,
		boolean shippingAllowed, int start, int end,
		OrderByComparator<Country> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByC_A_G_S.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				countryId, active, groupFilterEnabled, shippingAllowed
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first country in the ordered set where countryId = &#63; and active = &#63; and groupFilterEnabled = &#63; and shippingAllowed = &#63;.
	 *
	 * @param countryId the country ID
	 * @param active the active
	 * @param groupFilterEnabled the group filter enabled
	 * @param shippingAllowed the shipping allowed
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching country
	 * @throws NoSuchCountryException if a matching country could not be found
	 */
	@Override
	public Country findByC_A_G_S_First(
			long countryId, boolean active, boolean groupFilterEnabled,
			boolean shippingAllowed,
			OrderByComparator<Country> orderByComparator)
		throws NoSuchCountryException {

		return _collectionPersistenceFinderByC_A_G_S.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				countryId, active, groupFilterEnabled, shippingAllowed
			},
			orderByComparator);
	}

	/**
	 * Returns the first country in the ordered set where countryId = &#63; and active = &#63; and groupFilterEnabled = &#63; and shippingAllowed = &#63;.
	 *
	 * @param countryId the country ID
	 * @param active the active
	 * @param groupFilterEnabled the group filter enabled
	 * @param shippingAllowed the shipping allowed
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching country, or <code>null</code> if a matching country could not be found
	 */
	@Override
	public Country fetchByC_A_G_S_First(
		long countryId, boolean active, boolean groupFilterEnabled,
		boolean shippingAllowed, OrderByComparator<Country> orderByComparator) {

		return _collectionPersistenceFinderByC_A_G_S.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				countryId, active, groupFilterEnabled, shippingAllowed
			},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the countries that the user has permissions to view where countryId = &#63; and active = &#63; and groupFilterEnabled = &#63; and shippingAllowed = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CountryModelImpl</code>.
	 * </p>
	 *
	 * @param countryId the country ID
	 * @param active the active
	 * @param groupFilterEnabled the group filter enabled
	 * @param shippingAllowed the shipping allowed
	 * @param start the lower bound of the range of countries
	 * @param end the upper bound of the range of countries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching countries that the user has permission to view
	 */
	@Override
	public List<Country> filterFindByC_A_G_S(
		long countryId, boolean active, boolean groupFilterEnabled,
		boolean shippingAllowed, int start, int end,
		OrderByComparator<Country> orderByComparator) {

		return _collectionPersistenceFinderByC_A_G_S.filterFind(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				countryId, active, groupFilterEnabled, shippingAllowed
			},
			start, end, orderByComparator);
	}

	/**
	 * Removes all the countries where countryId = &#63; and active = &#63; and groupFilterEnabled = &#63; and shippingAllowed = &#63; from the database.
	 *
	 * @param countryId the country ID
	 * @param active the active
	 * @param groupFilterEnabled the group filter enabled
	 * @param shippingAllowed the shipping allowed
	 */
	@Override
	public void removeByC_A_G_S(
		long countryId, boolean active, boolean groupFilterEnabled,
		boolean shippingAllowed) {

		_collectionPersistenceFinderByC_A_G_S.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				countryId, active, groupFilterEnabled, shippingAllowed
			});
	}

	/**
	 * Returns the number of countries where countryId = &#63; and active = &#63; and groupFilterEnabled = &#63; and shippingAllowed = &#63;.
	 *
	 * @param countryId the country ID
	 * @param active the active
	 * @param groupFilterEnabled the group filter enabled
	 * @param shippingAllowed the shipping allowed
	 * @return the number of matching countries
	 */
	@Override
	public int countByC_A_G_S(
		long countryId, boolean active, boolean groupFilterEnabled,
		boolean shippingAllowed) {

		return _collectionPersistenceFinderByC_A_G_S.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				countryId, active, groupFilterEnabled, shippingAllowed
			});
	}

	/**
	 * Returns the number of countries that the user has permission to view where countryId = &#63; and active = &#63; and groupFilterEnabled = &#63; and shippingAllowed = &#63;.
	 *
	 * @param countryId the country ID
	 * @param active the active
	 * @param groupFilterEnabled the group filter enabled
	 * @param shippingAllowed the shipping allowed
	 * @return the number of matching countries that the user has permission to view
	 */
	@Override
	public int filterCountByC_A_G_S(
		long countryId, boolean active, boolean groupFilterEnabled,
		boolean shippingAllowed) {

		return _collectionPersistenceFinderByC_A_G_S.filterCount(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				countryId, active, groupFilterEnabled, shippingAllowed
			});
	}

	private FilterCollectionPersistenceFinder<Country, NoSuchCountryException>
		_collectionPersistenceFinderByC_A_B_G_S;

	/**
	 * Returns an ordered range of all the countries where countryId = &#63; and active = &#63; and billingAllowed = &#63; and groupFilterEnabled = &#63; and shippingAllowed = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CountryModelImpl</code>.
	 * </p>
	 *
	 * @param countryId the country ID
	 * @param active the active
	 * @param billingAllowed the billing allowed
	 * @param groupFilterEnabled the group filter enabled
	 * @param shippingAllowed the shipping allowed
	 * @param start the lower bound of the range of countries
	 * @param end the upper bound of the range of countries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching countries
	 */
	@Override
	public List<Country> findByC_A_B_G_S(
		long countryId, boolean active, boolean billingAllowed,
		boolean groupFilterEnabled, boolean shippingAllowed, int start, int end,
		OrderByComparator<Country> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByC_A_B_G_S.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				countryId, active, billingAllowed, groupFilterEnabled,
				shippingAllowed
			},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first country in the ordered set where countryId = &#63; and active = &#63; and billingAllowed = &#63; and groupFilterEnabled = &#63; and shippingAllowed = &#63;.
	 *
	 * @param countryId the country ID
	 * @param active the active
	 * @param billingAllowed the billing allowed
	 * @param groupFilterEnabled the group filter enabled
	 * @param shippingAllowed the shipping allowed
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching country
	 * @throws NoSuchCountryException if a matching country could not be found
	 */
	@Override
	public Country findByC_A_B_G_S_First(
			long countryId, boolean active, boolean billingAllowed,
			boolean groupFilterEnabled, boolean shippingAllowed,
			OrderByComparator<Country> orderByComparator)
		throws NoSuchCountryException {

		return _collectionPersistenceFinderByC_A_B_G_S.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				countryId, active, billingAllowed, groupFilterEnabled,
				shippingAllowed
			},
			orderByComparator);
	}

	/**
	 * Returns the first country in the ordered set where countryId = &#63; and active = &#63; and billingAllowed = &#63; and groupFilterEnabled = &#63; and shippingAllowed = &#63;.
	 *
	 * @param countryId the country ID
	 * @param active the active
	 * @param billingAllowed the billing allowed
	 * @param groupFilterEnabled the group filter enabled
	 * @param shippingAllowed the shipping allowed
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching country, or <code>null</code> if a matching country could not be found
	 */
	@Override
	public Country fetchByC_A_B_G_S_First(
		long countryId, boolean active, boolean billingAllowed,
		boolean groupFilterEnabled, boolean shippingAllowed,
		OrderByComparator<Country> orderByComparator) {

		return _collectionPersistenceFinderByC_A_B_G_S.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				countryId, active, billingAllowed, groupFilterEnabled,
				shippingAllowed
			},
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the countries that the user has permissions to view where countryId = &#63; and active = &#63; and billingAllowed = &#63; and groupFilterEnabled = &#63; and shippingAllowed = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CountryModelImpl</code>.
	 * </p>
	 *
	 * @param countryId the country ID
	 * @param active the active
	 * @param billingAllowed the billing allowed
	 * @param groupFilterEnabled the group filter enabled
	 * @param shippingAllowed the shipping allowed
	 * @param start the lower bound of the range of countries
	 * @param end the upper bound of the range of countries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching countries that the user has permission to view
	 */
	@Override
	public List<Country> filterFindByC_A_B_G_S(
		long countryId, boolean active, boolean billingAllowed,
		boolean groupFilterEnabled, boolean shippingAllowed, int start, int end,
		OrderByComparator<Country> orderByComparator) {

		return _collectionPersistenceFinderByC_A_B_G_S.filterFind(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				countryId, active, billingAllowed, groupFilterEnabled,
				shippingAllowed
			},
			start, end, orderByComparator);
	}

	/**
	 * Removes all the countries where countryId = &#63; and active = &#63; and billingAllowed = &#63; and groupFilterEnabled = &#63; and shippingAllowed = &#63; from the database.
	 *
	 * @param countryId the country ID
	 * @param active the active
	 * @param billingAllowed the billing allowed
	 * @param groupFilterEnabled the group filter enabled
	 * @param shippingAllowed the shipping allowed
	 */
	@Override
	public void removeByC_A_B_G_S(
		long countryId, boolean active, boolean billingAllowed,
		boolean groupFilterEnabled, boolean shippingAllowed) {

		_collectionPersistenceFinderByC_A_B_G_S.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				countryId, active, billingAllowed, groupFilterEnabled,
				shippingAllowed
			});
	}

	/**
	 * Returns the number of countries where countryId = &#63; and active = &#63; and billingAllowed = &#63; and groupFilterEnabled = &#63; and shippingAllowed = &#63;.
	 *
	 * @param countryId the country ID
	 * @param active the active
	 * @param billingAllowed the billing allowed
	 * @param groupFilterEnabled the group filter enabled
	 * @param shippingAllowed the shipping allowed
	 * @return the number of matching countries
	 */
	@Override
	public int countByC_A_B_G_S(
		long countryId, boolean active, boolean billingAllowed,
		boolean groupFilterEnabled, boolean shippingAllowed) {

		return _collectionPersistenceFinderByC_A_B_G_S.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				countryId, active, billingAllowed, groupFilterEnabled,
				shippingAllowed
			});
	}

	/**
	 * Returns the number of countries that the user has permission to view where countryId = &#63; and active = &#63; and billingAllowed = &#63; and groupFilterEnabled = &#63; and shippingAllowed = &#63;.
	 *
	 * @param countryId the country ID
	 * @param active the active
	 * @param billingAllowed the billing allowed
	 * @param groupFilterEnabled the group filter enabled
	 * @param shippingAllowed the shipping allowed
	 * @return the number of matching countries that the user has permission to view
	 */
	@Override
	public int filterCountByC_A_B_G_S(
		long countryId, boolean active, boolean billingAllowed,
		boolean groupFilterEnabled, boolean shippingAllowed) {

		return _collectionPersistenceFinderByC_A_B_G_S.filterCount(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				countryId, active, billingAllowed, groupFilterEnabled,
				shippingAllowed
			});
	}

	private UniquePersistenceFinder<Country, NoSuchCountryException>
		_uniquePersistenceFinderByERC_C;

	/**
	 * Returns the country where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchCountryException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching country
	 * @throws NoSuchCountryException if a matching country could not be found
	 */
	@Override
	public Country findByERC_C(String externalReferenceCode, long companyId)
		throws NoSuchCountryException {

		return _uniquePersistenceFinderByERC_C.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {externalReferenceCode, companyId});
	}

	/**
	 * Returns the country where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching country, or <code>null</code> if a matching country could not be found
	 */
	@Override
	public Country fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return _uniquePersistenceFinderByERC_C.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {externalReferenceCode, companyId}, useFinderCache);
	}

	/**
	 * Removes the country where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the country that was removed
	 */
	@Override
	public Country removeByERC_C(String externalReferenceCode, long companyId)
		throws NoSuchCountryException {

		Country country = findByERC_C(externalReferenceCode, companyId);

		return remove(country);
	}

	/**
	 * Returns the number of countries where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching countries
	 */
	@Override
	public int countByERC_C(String externalReferenceCode, long companyId) {
		return _uniquePersistenceFinderByERC_C.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {externalReferenceCode, companyId});
	}

	public CountryPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("active", "active_");
		dbColumnNames.put("idd", "idd_");
		dbColumnNames.put("number", "number_");

		setDBColumnNames(dbColumnNames);

		setModelClass(Country.class);

		setModelImplClass(CountryImpl.class);
		setModelPKClass(long.class);

		setTable(CountryTable.INSTANCE);
	}

	/**
	 * Creates a new country with the primary key. Does not add the country to the database.
	 *
	 * @param countryId the primary key for the new country
	 * @return the new country
	 */
	@Override
	public Country create(long countryId) {
		Country country = new CountryImpl();

		country.setNew(true);
		country.setPrimaryKey(countryId);

		String uuid = PortalUUIDUtil.generate();

		country.setUuid(uuid);

		country.setCompanyId(CompanyThreadLocal.getCompanyId());

		return country;
	}

	/**
	 * Removes the country with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param countryId the primary key of the country
	 * @return the country that was removed
	 * @throws NoSuchCountryException if a country with the primary key could not be found
	 */
	@Override
	public Country remove(long countryId) throws NoSuchCountryException {
		return remove((Serializable)countryId);
	}

	@Override
	protected Country removeImpl(Country country) {
		countryLocalizationPersistence.removeByCountryId(
			country.getCountryId());

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(country)) {
				country = (Country)session.get(
					CountryImpl.class, country.getPrimaryKeyObj());
			}

			if ((country != null) &&
				CTPersistenceHelperUtil.isRemove(country)) {

				session.delete(country);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (country != null) {
			clearCache(country);
		}

		return country;
	}

	@Override
	public Country updateImpl(Country country) {
		boolean isNew = country.isNew();

		if (!(country instanceof CountryModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(country.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(country);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in country proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom Country implementation " +
					country.getClass());
		}

		CountryModelImpl countryModelImpl = (CountryModelImpl)country;

		if (Validator.isNull(country.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			country.setUuid(uuid);
		}

		if (Validator.isNull(country.getExternalReferenceCode())) {
			country.setExternalReferenceCode(country.getUuid());
		}
		else {
			if (!Objects.equals(
					countryModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					country.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = country.getCompanyId();

					long groupId = 0;

					long classPK = 0;

					if (!isNew) {
						classPK = country.getPrimaryKey();
					}

					try {
						country.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								Country.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								country.getExternalReferenceCode(), null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			Country ercCountry = fetchByERC_C(
				country.getExternalReferenceCode(), country.getCompanyId());

			if (isNew) {
				if (ercCountry != null) {
					throw new DuplicateCountryExternalReferenceCodeException(
						"Duplicate country with external reference code " +
							country.getExternalReferenceCode() +
								" and company " + country.getCompanyId());
				}
			}
			else {
				if ((ercCountry != null) &&
					(country.getCountryId() != ercCountry.getCountryId())) {

					throw new DuplicateCountryExternalReferenceCodeException(
						"Duplicate country with external reference code " +
							country.getExternalReferenceCode() +
								" and company " + country.getCompanyId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (country.getCreateDate() == null)) {
			if (serviceContext == null) {
				country.setCreateDate(date);
			}
			else {
				country.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!countryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				country.setModifiedDate(date);
			}
			else {
				country.setModifiedDate(serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (CTPersistenceHelperUtil.isInsert(country)) {
				if (!isNew) {
					session.evict(
						CountryImpl.class, country.getPrimaryKeyObj());
				}

				session.save(country);
			}
			else {
				country = (Country)session.merge(country);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(country, false);

		if (isNew) {
			country.setNew(false);
		}

		country.resetOriginalValues();

		return country;
	}

	/**
	 * Returns the country with the primary key or throws a <code>NoSuchCountryException</code> if it could not be found.
	 *
	 * @param countryId the primary key of the country
	 * @return the country
	 * @throws NoSuchCountryException if a country with the primary key could not be found
	 */
	@Override
	public Country findByPrimaryKey(long countryId)
		throws NoSuchCountryException {

		return findByPrimaryKey((Serializable)countryId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return CTPersistenceHelperUtil.getCTPersistenceHelper();
	}

	/**
	 * Returns the country with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param countryId the primary key of the country
	 * @return the country, or <code>null</code> if a country with the primary key could not be found
	 */
	@Override
	public Country fetchByPrimaryKey(long countryId) {
		return fetchByPrimaryKey((Serializable)countryId);
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
		return "countryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_COUNTRY;
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
		return CountryModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "Country";
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
		ctMergeColumnNames.add("a2");
		ctMergeColumnNames.add("a3");
		ctMergeColumnNames.add("active_");
		ctMergeColumnNames.add("billingAllowed");
		ctMergeColumnNames.add("groupFilterEnabled");
		ctMergeColumnNames.add("idd_");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("number_");
		ctMergeColumnNames.add("position");
		ctMergeColumnNames.add("shippingAllowed");
		ctMergeColumnNames.add("subjectToVAT");
		ctMergeColumnNames.add("zipRequired");
		ctMergeColumnNames.add("lastPublishDate");
		ctMergeColumnNames.add("status");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK, Collections.singleton("countryId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"companyId", "a2"});

		_uniqueIndexColumnNames.add(new String[] {"companyId", "a3"});

		_uniqueIndexColumnNames.add(new String[] {"companyId", "name"});

		_uniqueIndexColumnNames.add(new String[] {"companyId", "number_"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "companyId"});
	}

	/**
	 * Initializes the country persistence.
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
				_SQL_SELECT_COUNTRY_WHERE, _SQL_COUNT_COUNTRY_WHERE,
				CountryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"country.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
					true, true, Country::getUuid));

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
				_SQL_SELECT_COUNTRY_WHERE, _SQL_COUNT_COUNTRY_WHERE,
				CountryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"country.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
					true, true, Country::getUuid),
				new FinderColumn<>(
					"country.", "companyId", FinderColumn.Type.LONG, "=", true,
					true, Country::getCompanyId));

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
				_SQL_SELECT_COUNTRY_WHERE, _SQL_COUNT_COUNTRY_WHERE,
				CountryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"country.", "companyId", FinderColumn.Type.LONG, "=", true,
					true, Country::getCompanyId));

		_collectionPersistenceFinderByActive =
			new FilterCollectionPersistenceFinder<>(
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
				_SQL_SELECT_COUNTRY_WHERE, _SQL_COUNT_COUNTRY_WHERE,
				CountryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"country.", "active", "active_", FinderColumn.Type.BOOLEAN,
					"=", true, true, Country::isActive));

		_uniquePersistenceFinderByC_A2 = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_A2",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "a2"}, 0, 2, false,
				Country::getCompanyId, convertNullFunction(Country::getA2)),
			_SQL_SELECT_COUNTRY_WHERE, "",
			new FinderColumn<>(
				"country.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, Country::getCompanyId),
			new FinderColumn<>(
				"country.", "a2", FinderColumn.Type.STRING, "=", true, true,
				Country::getA2));

		_uniquePersistenceFinderByC_A3 = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_A3",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "a3"}, 0, 2, false,
				Country::getCompanyId, convertNullFunction(Country::getA3)),
			_SQL_SELECT_COUNTRY_WHERE, "",
			new FinderColumn<>(
				"country.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, Country::getCompanyId),
			new FinderColumn<>(
				"country.", "a3", FinderColumn.Type.STRING, "=", true, true,
				Country::getA3));

		_collectionPersistenceFinderByC_Active =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_Active",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId", "active_"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_Active",
					new String[] {
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"companyId", "active_"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByC_Active",
					new String[] {
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"companyId", "active_"}, false),
				_SQL_SELECT_COUNTRY_WHERE, _SQL_COUNT_COUNTRY_WHERE,
				CountryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"country.", "companyId", FinderColumn.Type.LONG, "=", true,
					true, Country::getCompanyId),
				new FinderColumn<>(
					"country.", "active", "active_", FinderColumn.Type.BOOLEAN,
					"=", true, true, Country::isActive));

		_uniquePersistenceFinderByC_Name = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_Name",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "name"}, 0, 2, false,
				Country::getCompanyId, convertNullFunction(Country::getName)),
			_SQL_SELECT_COUNTRY_WHERE, "",
			new FinderColumn<>(
				"country.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, Country::getCompanyId),
			new FinderColumn<>(
				"country.", "name", FinderColumn.Type.STRING, "=", true, true,
				Country::getName));

		_uniquePersistenceFinderByC_Number = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_Number",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "number_"}, 0, 2, false,
				Country::getCompanyId, convertNullFunction(Country::getNumber)),
			_SQL_SELECT_COUNTRY_WHERE, "",
			new FinderColumn<>(
				"country.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, Country::getCompanyId),
			new FinderColumn<>(
				"country.", "number", "number_", FinderColumn.Type.STRING, "=",
				true, true, Country::getNumber));

		_collectionPersistenceFinderByC_A_B =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_A_B",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId", "active_", "billingAllowed"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_A_B",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Boolean.class.getName()
					},
					new String[] {"companyId", "active_", "billingAllowed"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_A_B",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Boolean.class.getName()
					},
					new String[] {"companyId", "active_", "billingAllowed"},
					false),
				_SQL_SELECT_COUNTRY_WHERE, _SQL_COUNT_COUNTRY_WHERE,
				CountryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"country.", "companyId", FinderColumn.Type.LONG, "=", true,
					true, Country::getCompanyId),
				new FinderColumn<>(
					"country.", "active", "active_", FinderColumn.Type.BOOLEAN,
					"=", true, true, Country::isActive),
				new FinderColumn<>(
					"country.", "billingAllowed", FinderColumn.Type.BOOLEAN,
					"=", true, true, Country::isBillingAllowed));

		_collectionPersistenceFinderByC_A_S =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_A_S",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"companyId", "active_", "shippingAllowed"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_A_S",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Boolean.class.getName()
					},
					new String[] {"companyId", "active_", "shippingAllowed"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_A_S",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Boolean.class.getName()
					},
					new String[] {"companyId", "active_", "shippingAllowed"},
					false),
				_SQL_SELECT_COUNTRY_WHERE, _SQL_COUNT_COUNTRY_WHERE,
				CountryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"country.", "companyId", FinderColumn.Type.LONG, "=", true,
					true, Country::getCompanyId),
				new FinderColumn<>(
					"country.", "active", "active_", FinderColumn.Type.BOOLEAN,
					"=", true, true, Country::isActive),
				new FinderColumn<>(
					"country.", "shippingAllowed", FinderColumn.Type.BOOLEAN,
					"=", true, true, Country::isShippingAllowed));

		_collectionPersistenceFinderByC_A_B_G =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_A_B_G",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Boolean.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"countryId", "active_", "billingAllowed",
						"groupFilterEnabled"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_A_B_G",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Boolean.class.getName(), Boolean.class.getName()
					},
					new String[] {
						"countryId", "active_", "billingAllowed",
						"groupFilterEnabled"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_A_B_G",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Boolean.class.getName(), Boolean.class.getName()
					},
					new String[] {
						"countryId", "active_", "billingAllowed",
						"groupFilterEnabled"
					},
					false),
				_SQL_SELECT_COUNTRY_WHERE, _SQL_COUNT_COUNTRY_WHERE,
				CountryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"country.", "countryId", FinderColumn.Type.LONG, "=", true,
					true, Country::getCountryId),
				new FinderColumn<>(
					"country.", "active", "active_", FinderColumn.Type.BOOLEAN,
					"=", true, true, Country::isActive),
				new FinderColumn<>(
					"country.", "billingAllowed", FinderColumn.Type.BOOLEAN,
					"=", true, true, Country::isBillingAllowed),
				new FinderColumn<>(
					"country.", "groupFilterEnabled", FinderColumn.Type.BOOLEAN,
					"=", true, true, Country::isGroupFilterEnabled));

		_collectionPersistenceFinderByC_A_G_S =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_A_G_S",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Boolean.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"countryId", "active_", "groupFilterEnabled",
						"shippingAllowed"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_A_G_S",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Boolean.class.getName(), Boolean.class.getName()
					},
					new String[] {
						"countryId", "active_", "groupFilterEnabled",
						"shippingAllowed"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_A_G_S",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Boolean.class.getName(), Boolean.class.getName()
					},
					new String[] {
						"countryId", "active_", "groupFilterEnabled",
						"shippingAllowed"
					},
					false),
				_SQL_SELECT_COUNTRY_WHERE, _SQL_COUNT_COUNTRY_WHERE,
				CountryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"country.", "countryId", FinderColumn.Type.LONG, "=", true,
					true, Country::getCountryId),
				new FinderColumn<>(
					"country.", "active", "active_", FinderColumn.Type.BOOLEAN,
					"=", true, true, Country::isActive),
				new FinderColumn<>(
					"country.", "groupFilterEnabled", FinderColumn.Type.BOOLEAN,
					"=", true, true, Country::isGroupFilterEnabled),
				new FinderColumn<>(
					"country.", "shippingAllowed", FinderColumn.Type.BOOLEAN,
					"=", true, true, Country::isShippingAllowed));

		_collectionPersistenceFinderByC_A_B_G_S =
			new FilterCollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_A_B_G_S",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Boolean.class.getName(), Boolean.class.getName(),
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"countryId", "active_", "billingAllowed",
						"groupFilterEnabled", "shippingAllowed"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByC_A_B_G_S",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Boolean.class.getName(), Boolean.class.getName(),
						Boolean.class.getName()
					},
					new String[] {
						"countryId", "active_", "billingAllowed",
						"groupFilterEnabled", "shippingAllowed"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByC_A_B_G_S",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Boolean.class.getName(), Boolean.class.getName(),
						Boolean.class.getName()
					},
					new String[] {
						"countryId", "active_", "billingAllowed",
						"groupFilterEnabled", "shippingAllowed"
					},
					false),
				_SQL_SELECT_COUNTRY_WHERE, _SQL_COUNT_COUNTRY_WHERE,
				CountryModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"country.", "countryId", FinderColumn.Type.LONG, "=", true,
					true, Country::getCountryId),
				new FinderColumn<>(
					"country.", "active", "active_", FinderColumn.Type.BOOLEAN,
					"=", true, true, Country::isActive),
				new FinderColumn<>(
					"country.", "billingAllowed", FinderColumn.Type.BOOLEAN,
					"=", true, true, Country::isBillingAllowed),
				new FinderColumn<>(
					"country.", "groupFilterEnabled", FinderColumn.Type.BOOLEAN,
					"=", true, true, Country::isGroupFilterEnabled),
				new FinderColumn<>(
					"country.", "shippingAllowed", FinderColumn.Type.BOOLEAN,
					"=", true, true, Country::isShippingAllowed));

		_uniquePersistenceFinderByERC_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByERC_C",
				new String[] {String.class.getName(), Long.class.getName()},
				new String[] {"externalReferenceCode", "companyId"}, 0, 1,
				false, convertNullFunction(Country::getExternalReferenceCode),
				Country::getCompanyId),
			_SQL_SELECT_COUNTRY_WHERE, "",
			new FinderColumn<>(
				"country.", "externalReferenceCode", FinderColumn.Type.STRING,
				"=", true, true, Country::getExternalReferenceCode),
			new FinderColumn<>(
				"country.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, Country::getCompanyId));

		CountryUtil.setPersistence(this);
	}

	public void destroy() {
		CountryUtil.setPersistence(null);

		EntityCacheUtil.removeCache(CountryImpl.class.getName());
	}

	@BeanReference(type = CountryLocalizationPersistence.class)
	protected CountryLocalizationPersistence countryLocalizationPersistence;

	private static final String _ENTITY_ALIAS_PREFIX =
		CountryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_COUNTRY =
		"SELECT country FROM Country country";

	private static final String _SQL_SELECT_COUNTRY_WHERE =
		"SELECT country FROM Country country WHERE ";

	private static final String _SQL_COUNT_COUNTRY_WHERE =
		"SELECT COUNT(country) FROM Country country WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No Country exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		CountryPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "active", "idd", "number"});

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1700873815