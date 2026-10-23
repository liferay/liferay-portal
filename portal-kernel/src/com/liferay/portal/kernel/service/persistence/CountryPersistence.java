/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.persistence;

import com.liferay.portal.kernel.exception.NoSuchCountryException;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the country service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see CountryUtil
 * @generated
 */
@ProviderType
public interface CountryPersistence
	extends BasePersistence<Country>, CTPersistence<Country> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link CountryUtil} to access the country persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the countries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.CountryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of countries
	 * @param end the upper bound of the range of countries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching countries
	 */
	public java.util.List<Country> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Country>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first country in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching country
	 * @throws NoSuchCountryException if a matching country could not be found
	 */
	public Country findByUuid_First(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<Country>
				orderByComparator)
		throws NoSuchCountryException;

	/**
	 * Returns the first country in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching country, or <code>null</code> if a matching country could not be found
	 */
	public Country fetchByUuid_First(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<Country>
			orderByComparator);

	/**
	 * Returns an ordered range of all the countries that the user has permissions to view where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.CountryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of countries
	 * @param end the upper bound of the range of countries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching countries that the user has permission to view
	 */
	public java.util.List<Country> filterFindByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Country>
			orderByComparator);

	/**
	 * Removes all the countries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public void removeByUuid(String uuid);

	/**
	 * Returns the number of countries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching countries
	 */
	public int countByUuid(String uuid);

	/**
	 * Returns the number of countries that the user has permission to view where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching countries that the user has permission to view
	 */
	public int filterCountByUuid(String uuid);

	/**
	 * Returns an ordered range of all the countries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.CountryModelImpl</code>.
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
	public java.util.List<Country> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Country>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first country in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching country
	 * @throws NoSuchCountryException if a matching country could not be found
	 */
	public Country findByUuid_C_First(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<Country>
				orderByComparator)
		throws NoSuchCountryException;

	/**
	 * Returns the first country in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching country, or <code>null</code> if a matching country could not be found
	 */
	public Country fetchByUuid_C_First(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<Country>
			orderByComparator);

	/**
	 * Returns an ordered range of all the countries that the user has permissions to view where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.CountryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of countries
	 * @param end the upper bound of the range of countries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching countries that the user has permission to view
	 */
	public java.util.List<Country> filterFindByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Country>
			orderByComparator);

	/**
	 * Removes all the countries where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public void removeByUuid_C(String uuid, long companyId);

	/**
	 * Returns the number of countries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching countries
	 */
	public int countByUuid_C(String uuid, long companyId);

	/**
	 * Returns the number of countries that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching countries that the user has permission to view
	 */
	public int filterCountByUuid_C(String uuid, long companyId);

	/**
	 * Returns an ordered range of all the countries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.CountryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of countries
	 * @param end the upper bound of the range of countries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching countries
	 */
	public java.util.List<Country> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Country>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first country in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching country
	 * @throws NoSuchCountryException if a matching country could not be found
	 */
	public Country findByCompanyId_First(
			long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<Country>
				orderByComparator)
		throws NoSuchCountryException;

	/**
	 * Returns the first country in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching country, or <code>null</code> if a matching country could not be found
	 */
	public Country fetchByCompanyId_First(
		long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<Country>
			orderByComparator);

	/**
	 * Returns an ordered range of all the countries that the user has permissions to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.CountryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of countries
	 * @param end the upper bound of the range of countries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching countries that the user has permission to view
	 */
	public java.util.List<Country> filterFindByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Country>
			orderByComparator);

	/**
	 * Removes all the countries where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public void removeByCompanyId(long companyId);

	/**
	 * Returns the number of countries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching countries
	 */
	public int countByCompanyId(long companyId);

	/**
	 * Returns the number of countries that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching countries that the user has permission to view
	 */
	public int filterCountByCompanyId(long companyId);

	/**
	 * Returns the country where companyId = &#63; and a2 = &#63; or throws a <code>NoSuchCountryException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param a2 the a2
	 * @return the matching country
	 * @throws NoSuchCountryException if a matching country could not be found
	 */
	public Country findByC_A2(long companyId, String a2)
		throws NoSuchCountryException;

	/**
	 * Returns the country where companyId = &#63; and a2 = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param a2 the a2
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching country, or <code>null</code> if a matching country could not be found
	 */
	public Country fetchByC_A2(
		long companyId, String a2, boolean useFinderCache);

	/**
	 * Removes the country where companyId = &#63; and a2 = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param a2 the a2
	 * @return the country that was removed
	 */
	public Country removeByC_A2(long companyId, String a2)
		throws NoSuchCountryException;

	/**
	 * Returns the number of countries where companyId = &#63; and a2 = &#63;.
	 *
	 * @param companyId the company ID
	 * @param a2 the a2
	 * @return the number of matching countries
	 */
	public int countByC_A2(long companyId, String a2);

	/**
	 * Returns the country where companyId = &#63; and a3 = &#63; or throws a <code>NoSuchCountryException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param a3 the a3
	 * @return the matching country
	 * @throws NoSuchCountryException if a matching country could not be found
	 */
	public Country findByC_A3(long companyId, String a3)
		throws NoSuchCountryException;

	/**
	 * Returns the country where companyId = &#63; and a3 = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param a3 the a3
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching country, or <code>null</code> if a matching country could not be found
	 */
	public Country fetchByC_A3(
		long companyId, String a3, boolean useFinderCache);

	/**
	 * Removes the country where companyId = &#63; and a3 = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param a3 the a3
	 * @return the country that was removed
	 */
	public Country removeByC_A3(long companyId, String a3)
		throws NoSuchCountryException;

	/**
	 * Returns the number of countries where companyId = &#63; and a3 = &#63;.
	 *
	 * @param companyId the company ID
	 * @param a3 the a3
	 * @return the number of matching countries
	 */
	public int countByC_A3(long companyId, String a3);

	/**
	 * Returns an ordered range of all the countries where companyId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.CountryModelImpl</code>.
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
	public java.util.List<Country> findByC_Active(
		long companyId, boolean active, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Country>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first country in the ordered set where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching country
	 * @throws NoSuchCountryException if a matching country could not be found
	 */
	public Country findByC_Active_First(
			long companyId, boolean active,
			com.liferay.portal.kernel.util.OrderByComparator<Country>
				orderByComparator)
		throws NoSuchCountryException;

	/**
	 * Returns the first country in the ordered set where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching country, or <code>null</code> if a matching country could not be found
	 */
	public Country fetchByC_Active_First(
		long companyId, boolean active,
		com.liferay.portal.kernel.util.OrderByComparator<Country>
			orderByComparator);

	/**
	 * Returns an ordered range of all the countries that the user has permissions to view where companyId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.CountryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param start the lower bound of the range of countries
	 * @param end the upper bound of the range of countries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching countries that the user has permission to view
	 */
	public java.util.List<Country> filterFindByC_Active(
		long companyId, boolean active, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Country>
			orderByComparator);

	/**
	 * Removes all the countries where companyId = &#63; and active = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 */
	public void removeByC_Active(long companyId, boolean active);

	/**
	 * Returns the number of countries where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @return the number of matching countries
	 */
	public int countByC_Active(long companyId, boolean active);

	/**
	 * Returns the number of countries that the user has permission to view where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @return the number of matching countries that the user has permission to view
	 */
	public int filterCountByC_Active(long companyId, boolean active);

	/**
	 * Returns the country where companyId = &#63; and name = &#63; or throws a <code>NoSuchCountryException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the matching country
	 * @throws NoSuchCountryException if a matching country could not be found
	 */
	public Country findByC_Name(long companyId, String name)
		throws NoSuchCountryException;

	/**
	 * Returns the country where companyId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching country, or <code>null</code> if a matching country could not be found
	 */
	public Country fetchByC_Name(
		long companyId, String name, boolean useFinderCache);

	/**
	 * Removes the country where companyId = &#63; and name = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the country that was removed
	 */
	public Country removeByC_Name(long companyId, String name)
		throws NoSuchCountryException;

	/**
	 * Returns the number of countries where companyId = &#63; and name = &#63;.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the number of matching countries
	 */
	public int countByC_Name(long companyId, String name);

	/**
	 * Returns the country where companyId = &#63; and number = &#63; or throws a <code>NoSuchCountryException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param number the number
	 * @return the matching country
	 * @throws NoSuchCountryException if a matching country could not be found
	 */
	public Country findByC_Number(long companyId, String number)
		throws NoSuchCountryException;

	/**
	 * Returns the country where companyId = &#63; and number = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param number the number
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching country, or <code>null</code> if a matching country could not be found
	 */
	public Country fetchByC_Number(
		long companyId, String number, boolean useFinderCache);

	/**
	 * Removes the country where companyId = &#63; and number = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param number the number
	 * @return the country that was removed
	 */
	public Country removeByC_Number(long companyId, String number)
		throws NoSuchCountryException;

	/**
	 * Returns the number of countries where companyId = &#63; and number = &#63;.
	 *
	 * @param companyId the company ID
	 * @param number the number
	 * @return the number of matching countries
	 */
	public int countByC_Number(long companyId, String number);

	/**
	 * Returns an ordered range of all the countries where companyId = &#63; and active = &#63; and billingAllowed = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.CountryModelImpl</code>.
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
	public java.util.List<Country> findByC_A_B(
		long companyId, boolean active, boolean billingAllowed, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<Country>
			orderByComparator,
		boolean useFinderCache);

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
	public Country findByC_A_B_First(
			long companyId, boolean active, boolean billingAllowed,
			com.liferay.portal.kernel.util.OrderByComparator<Country>
				orderByComparator)
		throws NoSuchCountryException;

	/**
	 * Returns the first country in the ordered set where companyId = &#63; and active = &#63; and billingAllowed = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param billingAllowed the billing allowed
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching country, or <code>null</code> if a matching country could not be found
	 */
	public Country fetchByC_A_B_First(
		long companyId, boolean active, boolean billingAllowed,
		com.liferay.portal.kernel.util.OrderByComparator<Country>
			orderByComparator);

	/**
	 * Returns an ordered range of all the countries that the user has permissions to view where companyId = &#63; and active = &#63; and billingAllowed = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.CountryModelImpl</code>.
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
	public java.util.List<Country> filterFindByC_A_B(
		long companyId, boolean active, boolean billingAllowed, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<Country>
			orderByComparator);

	/**
	 * Removes all the countries where companyId = &#63; and active = &#63; and billingAllowed = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param billingAllowed the billing allowed
	 */
	public void removeByC_A_B(
		long companyId, boolean active, boolean billingAllowed);

	/**
	 * Returns the number of countries where companyId = &#63; and active = &#63; and billingAllowed = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param billingAllowed the billing allowed
	 * @return the number of matching countries
	 */
	public int countByC_A_B(
		long companyId, boolean active, boolean billingAllowed);

	/**
	 * Returns the number of countries that the user has permission to view where companyId = &#63; and active = &#63; and billingAllowed = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param billingAllowed the billing allowed
	 * @return the number of matching countries that the user has permission to view
	 */
	public int filterCountByC_A_B(
		long companyId, boolean active, boolean billingAllowed);

	/**
	 * Returns an ordered range of all the countries where companyId = &#63; and active = &#63; and shippingAllowed = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.CountryModelImpl</code>.
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
	public java.util.List<Country> findByC_A_S(
		long companyId, boolean active, boolean shippingAllowed, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<Country>
			orderByComparator,
		boolean useFinderCache);

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
	public Country findByC_A_S_First(
			long companyId, boolean active, boolean shippingAllowed,
			com.liferay.portal.kernel.util.OrderByComparator<Country>
				orderByComparator)
		throws NoSuchCountryException;

	/**
	 * Returns the first country in the ordered set where companyId = &#63; and active = &#63; and shippingAllowed = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param shippingAllowed the shipping allowed
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching country, or <code>null</code> if a matching country could not be found
	 */
	public Country fetchByC_A_S_First(
		long companyId, boolean active, boolean shippingAllowed,
		com.liferay.portal.kernel.util.OrderByComparator<Country>
			orderByComparator);

	/**
	 * Returns an ordered range of all the countries that the user has permissions to view where companyId = &#63; and active = &#63; and shippingAllowed = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.CountryModelImpl</code>.
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
	public java.util.List<Country> filterFindByC_A_S(
		long companyId, boolean active, boolean shippingAllowed, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<Country>
			orderByComparator);

	/**
	 * Removes all the countries where companyId = &#63; and active = &#63; and shippingAllowed = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param shippingAllowed the shipping allowed
	 */
	public void removeByC_A_S(
		long companyId, boolean active, boolean shippingAllowed);

	/**
	 * Returns the number of countries where companyId = &#63; and active = &#63; and shippingAllowed = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param shippingAllowed the shipping allowed
	 * @return the number of matching countries
	 */
	public int countByC_A_S(
		long companyId, boolean active, boolean shippingAllowed);

	/**
	 * Returns the number of countries that the user has permission to view where companyId = &#63; and active = &#63; and shippingAllowed = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param shippingAllowed the shipping allowed
	 * @return the number of matching countries that the user has permission to view
	 */
	public int filterCountByC_A_S(
		long companyId, boolean active, boolean shippingAllowed);

	/**
	 * Returns the country where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchCountryException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching country
	 * @throws NoSuchCountryException if a matching country could not be found
	 */
	public Country findByERC_C(String externalReferenceCode, long companyId)
		throws NoSuchCountryException;

	/**
	 * Returns the country where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching country, or <code>null</code> if a matching country could not be found
	 */
	public Country fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache);

	/**
	 * Removes the country where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the country that was removed
	 */
	public Country removeByERC_C(String externalReferenceCode, long companyId)
		throws NoSuchCountryException;

	/**
	 * Returns the number of countries where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching countries
	 */
	public int countByERC_C(String externalReferenceCode, long companyId);

	/**
	 * Creates a new country with the primary key. Does not add the country to the database.
	 *
	 * @param countryId the primary key for the new country
	 * @return the new country
	 */
	public Country create(long countryId);

	/**
	 * Removes the country with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param countryId the primary key of the country
	 * @return the country that was removed
	 * @throws NoSuchCountryException if a country with the primary key could not be found
	 */
	public Country remove(long countryId) throws NoSuchCountryException;

	public Country updateImpl(Country country);

	/**
	 * Returns the country with the primary key or throws a <code>NoSuchCountryException</code> if it could not be found.
	 *
	 * @param countryId the primary key of the country
	 * @return the country
	 * @throws NoSuchCountryException if a country with the primary key could not be found
	 */
	public Country findByPrimaryKey(long countryId)
		throws NoSuchCountryException;

	/**
	 * Returns the country with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param countryId the primary key of the country
	 * @return the country, or <code>null</code> if a country with the primary key could not be found
	 */
	public Country fetchByPrimaryKey(long countryId);

	/**
	 * Returns the country where companyId = &#63; and a2 = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param a2 the a2
	 * @return the matching country, or <code>null</code> if a matching country could not be found
	 */
	public default Country fetchByC_A2(long companyId, String a2) {
		return fetchByC_A2(companyId, a2, true);
	}

	/**
	 * Returns the country where companyId = &#63; and a3 = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param a3 the a3
	 * @return the matching country, or <code>null</code> if a matching country could not be found
	 */
	public default Country fetchByC_A3(long companyId, String a3) {
		return fetchByC_A3(companyId, a3, true);
	}

	/**
	 * Returns the country where companyId = &#63; and name = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param name the name
	 * @return the matching country, or <code>null</code> if a matching country could not be found
	 */
	public default Country fetchByC_Name(long companyId, String name) {
		return fetchByC_Name(companyId, name, true);
	}

	/**
	 * Returns the country where companyId = &#63; and number = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param number the number
	 * @return the matching country, or <code>null</code> if a matching country could not be found
	 */
	public default Country fetchByC_Number(long companyId, String number) {
		return fetchByC_Number(companyId, number, true);
	}

	/**
	 * Returns the country where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching country, or <code>null</code> if a matching country could not be found
	 */
	public default Country fetchByERC_C(
		String externalReferenceCode, long companyId) {

		return fetchByERC_C(externalReferenceCode, companyId, true);
	}

	/**
	 * Returns all the countries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching countries
	 */
	public default java.util.List<Country> findByUuid(String uuid) {
		return findByUuid(
			uuid, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the countries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.CountryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of countries
	 * @param end the upper bound of the range of countries (not inclusive)
	 * @return the range of matching countries
	 */
	public default java.util.List<Country> findByUuid(
		String uuid, int start, int end) {

		return findByUuid(uuid, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the countries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.CountryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of countries
	 * @param end the upper bound of the range of countries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching countries
	 */
	public default java.util.List<Country> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Country>
			orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the countries that the user has permission to view where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching countries that the user has permission to view
	 */
	public default java.util.List<Country> filterFindByUuid(String uuid) {
		return filterFindByUuid(
			uuid, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the countries that the user has permission to view where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.CountryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of countries
	 * @param end the upper bound of the range of countries (not inclusive)
	 * @return the range of matching countries that the user has permission to view
	 */
	public default java.util.List<Country> filterFindByUuid(
		String uuid, int start, int end) {

		return filterFindByUuid(uuid, start, end, null);
	}

	/**
	 * Returns all the countries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching countries
	 */
	public default java.util.List<Country> findByUuid_C(
		String uuid, long companyId) {

		return findByUuid_C(
			uuid, companyId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the countries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.CountryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of countries
	 * @param end the upper bound of the range of countries (not inclusive)
	 * @return the range of matching countries
	 */
	public default java.util.List<Country> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the countries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.CountryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of countries
	 * @param end the upper bound of the range of countries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching countries
	 */
	public default java.util.List<Country> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Country>
			orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the countries that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching countries that the user has permission to view
	 */
	public default java.util.List<Country> filterFindByUuid_C(
		String uuid, long companyId) {

		return filterFindByUuid_C(
			uuid, companyId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the countries that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.CountryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of countries
	 * @param end the upper bound of the range of countries (not inclusive)
	 * @return the range of matching countries that the user has permission to view
	 */
	public default java.util.List<Country> filterFindByUuid_C(
		String uuid, long companyId, int start, int end) {

		return filterFindByUuid_C(uuid, companyId, start, end, null);
	}

	/**
	 * Returns all the countries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching countries
	 */
	public default java.util.List<Country> findByCompanyId(long companyId) {
		return findByCompanyId(
			companyId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the countries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.CountryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of countries
	 * @param end the upper bound of the range of countries (not inclusive)
	 * @return the range of matching countries
	 */
	public default java.util.List<Country> findByCompanyId(
		long companyId, int start, int end) {

		return findByCompanyId(companyId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the countries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.CountryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of countries
	 * @param end the upper bound of the range of countries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching countries
	 */
	public default java.util.List<Country> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Country>
			orderByComparator) {

		return findByCompanyId(companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the countries that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching countries that the user has permission to view
	 */
	public default java.util.List<Country> filterFindByCompanyId(
		long companyId) {

		return filterFindByCompanyId(
			companyId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the countries that the user has permission to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.CountryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of countries
	 * @param end the upper bound of the range of countries (not inclusive)
	 * @return the range of matching countries that the user has permission to view
	 */
	public default java.util.List<Country> filterFindByCompanyId(
		long companyId, int start, int end) {

		return filterFindByCompanyId(companyId, start, end, null);
	}

	/**
	 * Returns all the countries where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @return the matching countries
	 */
	public default java.util.List<Country> findByC_Active(
		long companyId, boolean active) {

		return findByC_Active(
			companyId, active,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the countries where companyId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.CountryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param start the lower bound of the range of countries
	 * @param end the upper bound of the range of countries (not inclusive)
	 * @return the range of matching countries
	 */
	public default java.util.List<Country> findByC_Active(
		long companyId, boolean active, int start, int end) {

		return findByC_Active(companyId, active, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the countries where companyId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.CountryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param start the lower bound of the range of countries
	 * @param end the upper bound of the range of countries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching countries
	 */
	public default java.util.List<Country> findByC_Active(
		long companyId, boolean active, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Country>
			orderByComparator) {

		return findByC_Active(
			companyId, active, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the countries that the user has permission to view where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @return the matching countries that the user has permission to view
	 */
	public default java.util.List<Country> filterFindByC_Active(
		long companyId, boolean active) {

		return filterFindByC_Active(
			companyId, active,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the countries that the user has permission to view where companyId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.CountryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param start the lower bound of the range of countries
	 * @param end the upper bound of the range of countries (not inclusive)
	 * @return the range of matching countries that the user has permission to view
	 */
	public default java.util.List<Country> filterFindByC_Active(
		long companyId, boolean active, int start, int end) {

		return filterFindByC_Active(companyId, active, start, end, null);
	}

	/**
	 * Returns all the countries where companyId = &#63; and active = &#63; and billingAllowed = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param billingAllowed the billing allowed
	 * @return the matching countries
	 */
	public default java.util.List<Country> findByC_A_B(
		long companyId, boolean active, boolean billingAllowed) {

		return findByC_A_B(
			companyId, active, billingAllowed,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the countries where companyId = &#63; and active = &#63; and billingAllowed = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.CountryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param billingAllowed the billing allowed
	 * @param start the lower bound of the range of countries
	 * @param end the upper bound of the range of countries (not inclusive)
	 * @return the range of matching countries
	 */
	public default java.util.List<Country> findByC_A_B(
		long companyId, boolean active, boolean billingAllowed, int start,
		int end) {

		return findByC_A_B(
			companyId, active, billingAllowed, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the countries where companyId = &#63; and active = &#63; and billingAllowed = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.CountryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param billingAllowed the billing allowed
	 * @param start the lower bound of the range of countries
	 * @param end the upper bound of the range of countries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching countries
	 */
	public default java.util.List<Country> findByC_A_B(
		long companyId, boolean active, boolean billingAllowed, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<Country>
			orderByComparator) {

		return findByC_A_B(
			companyId, active, billingAllowed, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns all the countries that the user has permission to view where companyId = &#63; and active = &#63; and billingAllowed = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param billingAllowed the billing allowed
	 * @return the matching countries that the user has permission to view
	 */
	public default java.util.List<Country> filterFindByC_A_B(
		long companyId, boolean active, boolean billingAllowed) {

		return filterFindByC_A_B(
			companyId, active, billingAllowed,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the countries that the user has permission to view where companyId = &#63; and active = &#63; and billingAllowed = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.CountryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param billingAllowed the billing allowed
	 * @param start the lower bound of the range of countries
	 * @param end the upper bound of the range of countries (not inclusive)
	 * @return the range of matching countries that the user has permission to view
	 */
	public default java.util.List<Country> filterFindByC_A_B(
		long companyId, boolean active, boolean billingAllowed, int start,
		int end) {

		return filterFindByC_A_B(
			companyId, active, billingAllowed, start, end, null);
	}

	/**
	 * Returns all the countries where companyId = &#63; and active = &#63; and shippingAllowed = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param shippingAllowed the shipping allowed
	 * @return the matching countries
	 */
	public default java.util.List<Country> findByC_A_S(
		long companyId, boolean active, boolean shippingAllowed) {

		return findByC_A_S(
			companyId, active, shippingAllowed,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the countries where companyId = &#63; and active = &#63; and shippingAllowed = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.CountryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param shippingAllowed the shipping allowed
	 * @param start the lower bound of the range of countries
	 * @param end the upper bound of the range of countries (not inclusive)
	 * @return the range of matching countries
	 */
	public default java.util.List<Country> findByC_A_S(
		long companyId, boolean active, boolean shippingAllowed, int start,
		int end) {

		return findByC_A_S(
			companyId, active, shippingAllowed, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the countries where companyId = &#63; and active = &#63; and shippingAllowed = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.CountryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param shippingAllowed the shipping allowed
	 * @param start the lower bound of the range of countries
	 * @param end the upper bound of the range of countries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching countries
	 */
	public default java.util.List<Country> findByC_A_S(
		long companyId, boolean active, boolean shippingAllowed, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<Country>
			orderByComparator) {

		return findByC_A_S(
			companyId, active, shippingAllowed, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns all the countries that the user has permission to view where companyId = &#63; and active = &#63; and shippingAllowed = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param shippingAllowed the shipping allowed
	 * @return the matching countries that the user has permission to view
	 */
	public default java.util.List<Country> filterFindByC_A_S(
		long companyId, boolean active, boolean shippingAllowed) {

		return filterFindByC_A_S(
			companyId, active, shippingAllowed,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the countries that the user has permission to view where companyId = &#63; and active = &#63; and shippingAllowed = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.CountryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param shippingAllowed the shipping allowed
	 * @param start the lower bound of the range of countries
	 * @param end the upper bound of the range of countries (not inclusive)
	 * @return the range of matching countries that the user has permission to view
	 */
	public default java.util.List<Country> filterFindByC_A_S(
		long companyId, boolean active, boolean shippingAllowed, int start,
		int end) {

		return filterFindByC_A_S(
			companyId, active, shippingAllowed, start, end, null);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-788969001