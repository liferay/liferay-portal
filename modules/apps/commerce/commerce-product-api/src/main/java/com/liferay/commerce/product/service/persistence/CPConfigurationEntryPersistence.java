/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.persistence;

import com.liferay.commerce.product.exception.NoSuchCPConfigurationEntryException;
import com.liferay.commerce.product.model.CPConfigurationEntry;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the cp configuration entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @see CPConfigurationEntryUtil
 * @generated
 */
@ProviderType
public interface CPConfigurationEntryPersistence
	extends BasePersistence<CPConfigurationEntry>,
			CTPersistence<CPConfigurationEntry> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link CPConfigurationEntryUtil} to access the cp configuration entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns all the cp configuration entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching cp configuration entries
	 */
	public java.util.List<CPConfigurationEntry> findByUuid(String uuid);

	/**
	 * Returns a range of all the cp configuration entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPConfigurationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp configuration entries
	 * @param end the upper bound of the range of cp configuration entries (not inclusive)
	 * @return the range of matching cp configuration entries
	 */
	public java.util.List<CPConfigurationEntry> findByUuid(
		String uuid, int start, int end);

	/**
	 * Returns an ordered range of all the cp configuration entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPConfigurationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp configuration entries
	 * @param end the upper bound of the range of cp configuration entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp configuration entries
	 */
	public java.util.List<CPConfigurationEntry> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CPConfigurationEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the cp configuration entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPConfigurationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp configuration entries
	 * @param end the upper bound of the range of cp configuration entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp configuration entries
	 */
	public java.util.List<CPConfigurationEntry> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CPConfigurationEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first cp configuration entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a matching cp configuration entry could not be found
	 */
	public CPConfigurationEntry findByUuid_First(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator
				<CPConfigurationEntry> orderByComparator)
		throws NoSuchCPConfigurationEntryException;

	/**
	 * Returns the first cp configuration entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp configuration entry, or <code>null</code> if a matching cp configuration entry could not be found
	 */
	public CPConfigurationEntry fetchByUuid_First(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<CPConfigurationEntry>
			orderByComparator);

	/**
	 * Returns the last cp configuration entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a matching cp configuration entry could not be found
	 */
	public CPConfigurationEntry findByUuid_Last(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator
				<CPConfigurationEntry> orderByComparator)
		throws NoSuchCPConfigurationEntryException;

	/**
	 * Returns the last cp configuration entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cp configuration entry, or <code>null</code> if a matching cp configuration entry could not be found
	 */
	public CPConfigurationEntry fetchByUuid_Last(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<CPConfigurationEntry>
			orderByComparator);

	/**
	 * Returns the cp configuration entries before and after the current cp configuration entry in the ordered set where uuid = &#63;.
	 *
	 * @param CPConfigurationEntryId the primary key of the current cp configuration entry
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a cp configuration entry with the primary key could not be found
	 */
	public CPConfigurationEntry[] findByUuid_PrevAndNext(
			long CPConfigurationEntryId, String uuid,
			com.liferay.portal.kernel.util.OrderByComparator
				<CPConfigurationEntry> orderByComparator)
		throws NoSuchCPConfigurationEntryException;

	/**
	 * Removes all the cp configuration entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public void removeByUuid(String uuid);

	/**
	 * Returns the number of cp configuration entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching cp configuration entries
	 */
	public int countByUuid(String uuid);

	/**
	 * Returns the cp configuration entry where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchCPConfigurationEntryException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a matching cp configuration entry could not be found
	 */
	public CPConfigurationEntry findByUUID_G(String uuid, long groupId)
		throws NoSuchCPConfigurationEntryException;

	/**
	 * Returns the cp configuration entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching cp configuration entry, or <code>null</code> if a matching cp configuration entry could not be found
	 */
	public CPConfigurationEntry fetchByUUID_G(String uuid, long groupId);

	/**
	 * Returns the cp configuration entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp configuration entry, or <code>null</code> if a matching cp configuration entry could not be found
	 */
	public CPConfigurationEntry fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache);

	/**
	 * Removes the cp configuration entry where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the cp configuration entry that was removed
	 */
	public CPConfigurationEntry removeByUUID_G(String uuid, long groupId)
		throws NoSuchCPConfigurationEntryException;

	/**
	 * Returns the number of cp configuration entries where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching cp configuration entries
	 */
	public int countByUUID_G(String uuid, long groupId);

	/**
	 * Returns all the cp configuration entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching cp configuration entries
	 */
	public java.util.List<CPConfigurationEntry> findByUuid_C(
		String uuid, long companyId);

	/**
	 * Returns a range of all the cp configuration entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPConfigurationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp configuration entries
	 * @param end the upper bound of the range of cp configuration entries (not inclusive)
	 * @return the range of matching cp configuration entries
	 */
	public java.util.List<CPConfigurationEntry> findByUuid_C(
		String uuid, long companyId, int start, int end);

	/**
	 * Returns an ordered range of all the cp configuration entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPConfigurationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp configuration entries
	 * @param end the upper bound of the range of cp configuration entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp configuration entries
	 */
	public java.util.List<CPConfigurationEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CPConfigurationEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the cp configuration entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPConfigurationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp configuration entries
	 * @param end the upper bound of the range of cp configuration entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp configuration entries
	 */
	public java.util.List<CPConfigurationEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CPConfigurationEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first cp configuration entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a matching cp configuration entry could not be found
	 */
	public CPConfigurationEntry findByUuid_C_First(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator
				<CPConfigurationEntry> orderByComparator)
		throws NoSuchCPConfigurationEntryException;

	/**
	 * Returns the first cp configuration entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp configuration entry, or <code>null</code> if a matching cp configuration entry could not be found
	 */
	public CPConfigurationEntry fetchByUuid_C_First(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<CPConfigurationEntry>
			orderByComparator);

	/**
	 * Returns the last cp configuration entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a matching cp configuration entry could not be found
	 */
	public CPConfigurationEntry findByUuid_C_Last(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator
				<CPConfigurationEntry> orderByComparator)
		throws NoSuchCPConfigurationEntryException;

	/**
	 * Returns the last cp configuration entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cp configuration entry, or <code>null</code> if a matching cp configuration entry could not be found
	 */
	public CPConfigurationEntry fetchByUuid_C_Last(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<CPConfigurationEntry>
			orderByComparator);

	/**
	 * Returns the cp configuration entries before and after the current cp configuration entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param CPConfigurationEntryId the primary key of the current cp configuration entry
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a cp configuration entry with the primary key could not be found
	 */
	public CPConfigurationEntry[] findByUuid_C_PrevAndNext(
			long CPConfigurationEntryId, String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator
				<CPConfigurationEntry> orderByComparator)
		throws NoSuchCPConfigurationEntryException;

	/**
	 * Removes all the cp configuration entries where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public void removeByUuid_C(String uuid, long companyId);

	/**
	 * Returns the number of cp configuration entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching cp configuration entries
	 */
	public int countByUuid_C(String uuid, long companyId);

	/**
	 * Returns all the cp configuration entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching cp configuration entries
	 */
	public java.util.List<CPConfigurationEntry> findByCompanyId(long companyId);

	/**
	 * Returns a range of all the cp configuration entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPConfigurationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp configuration entries
	 * @param end the upper bound of the range of cp configuration entries (not inclusive)
	 * @return the range of matching cp configuration entries
	 */
	public java.util.List<CPConfigurationEntry> findByCompanyId(
		long companyId, int start, int end);

	/**
	 * Returns an ordered range of all the cp configuration entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPConfigurationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp configuration entries
	 * @param end the upper bound of the range of cp configuration entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp configuration entries
	 */
	public java.util.List<CPConfigurationEntry> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CPConfigurationEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the cp configuration entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPConfigurationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp configuration entries
	 * @param end the upper bound of the range of cp configuration entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp configuration entries
	 */
	public java.util.List<CPConfigurationEntry> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CPConfigurationEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first cp configuration entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a matching cp configuration entry could not be found
	 */
	public CPConfigurationEntry findByCompanyId_First(
			long companyId,
			com.liferay.portal.kernel.util.OrderByComparator
				<CPConfigurationEntry> orderByComparator)
		throws NoSuchCPConfigurationEntryException;

	/**
	 * Returns the first cp configuration entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp configuration entry, or <code>null</code> if a matching cp configuration entry could not be found
	 */
	public CPConfigurationEntry fetchByCompanyId_First(
		long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<CPConfigurationEntry>
			orderByComparator);

	/**
	 * Returns the last cp configuration entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a matching cp configuration entry could not be found
	 */
	public CPConfigurationEntry findByCompanyId_Last(
			long companyId,
			com.liferay.portal.kernel.util.OrderByComparator
				<CPConfigurationEntry> orderByComparator)
		throws NoSuchCPConfigurationEntryException;

	/**
	 * Returns the last cp configuration entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cp configuration entry, or <code>null</code> if a matching cp configuration entry could not be found
	 */
	public CPConfigurationEntry fetchByCompanyId_Last(
		long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<CPConfigurationEntry>
			orderByComparator);

	/**
	 * Returns the cp configuration entries before and after the current cp configuration entry in the ordered set where companyId = &#63;.
	 *
	 * @param CPConfigurationEntryId the primary key of the current cp configuration entry
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a cp configuration entry with the primary key could not be found
	 */
	public CPConfigurationEntry[] findByCompanyId_PrevAndNext(
			long CPConfigurationEntryId, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator
				<CPConfigurationEntry> orderByComparator)
		throws NoSuchCPConfigurationEntryException;

	/**
	 * Removes all the cp configuration entries where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public void removeByCompanyId(long companyId);

	/**
	 * Returns the number of cp configuration entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching cp configuration entries
	 */
	public int countByCompanyId(long companyId);

	/**
	 * Returns all the cp configuration entries where CPConfigurationListId = &#63;.
	 *
	 * @param CPConfigurationListId the cp configuration list ID
	 * @return the matching cp configuration entries
	 */
	public java.util.List<CPConfigurationEntry> findByCPConfigurationListId(
		long CPConfigurationListId);

	/**
	 * Returns a range of all the cp configuration entries where CPConfigurationListId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPConfigurationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param CPConfigurationListId the cp configuration list ID
	 * @param start the lower bound of the range of cp configuration entries
	 * @param end the upper bound of the range of cp configuration entries (not inclusive)
	 * @return the range of matching cp configuration entries
	 */
	public java.util.List<CPConfigurationEntry> findByCPConfigurationListId(
		long CPConfigurationListId, int start, int end);

	/**
	 * Returns an ordered range of all the cp configuration entries where CPConfigurationListId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPConfigurationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param CPConfigurationListId the cp configuration list ID
	 * @param start the lower bound of the range of cp configuration entries
	 * @param end the upper bound of the range of cp configuration entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp configuration entries
	 */
	public java.util.List<CPConfigurationEntry> findByCPConfigurationListId(
		long CPConfigurationListId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CPConfigurationEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the cp configuration entries where CPConfigurationListId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPConfigurationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param CPConfigurationListId the cp configuration list ID
	 * @param start the lower bound of the range of cp configuration entries
	 * @param end the upper bound of the range of cp configuration entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp configuration entries
	 */
	public java.util.List<CPConfigurationEntry> findByCPConfigurationListId(
		long CPConfigurationListId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CPConfigurationEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first cp configuration entry in the ordered set where CPConfigurationListId = &#63;.
	 *
	 * @param CPConfigurationListId the cp configuration list ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a matching cp configuration entry could not be found
	 */
	public CPConfigurationEntry findByCPConfigurationListId_First(
			long CPConfigurationListId,
			com.liferay.portal.kernel.util.OrderByComparator
				<CPConfigurationEntry> orderByComparator)
		throws NoSuchCPConfigurationEntryException;

	/**
	 * Returns the first cp configuration entry in the ordered set where CPConfigurationListId = &#63;.
	 *
	 * @param CPConfigurationListId the cp configuration list ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp configuration entry, or <code>null</code> if a matching cp configuration entry could not be found
	 */
	public CPConfigurationEntry fetchByCPConfigurationListId_First(
		long CPConfigurationListId,
		com.liferay.portal.kernel.util.OrderByComparator<CPConfigurationEntry>
			orderByComparator);

	/**
	 * Returns the last cp configuration entry in the ordered set where CPConfigurationListId = &#63;.
	 *
	 * @param CPConfigurationListId the cp configuration list ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a matching cp configuration entry could not be found
	 */
	public CPConfigurationEntry findByCPConfigurationListId_Last(
			long CPConfigurationListId,
			com.liferay.portal.kernel.util.OrderByComparator
				<CPConfigurationEntry> orderByComparator)
		throws NoSuchCPConfigurationEntryException;

	/**
	 * Returns the last cp configuration entry in the ordered set where CPConfigurationListId = &#63;.
	 *
	 * @param CPConfigurationListId the cp configuration list ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cp configuration entry, or <code>null</code> if a matching cp configuration entry could not be found
	 */
	public CPConfigurationEntry fetchByCPConfigurationListId_Last(
		long CPConfigurationListId,
		com.liferay.portal.kernel.util.OrderByComparator<CPConfigurationEntry>
			orderByComparator);

	/**
	 * Returns the cp configuration entries before and after the current cp configuration entry in the ordered set where CPConfigurationListId = &#63;.
	 *
	 * @param CPConfigurationEntryId the primary key of the current cp configuration entry
	 * @param CPConfigurationListId the cp configuration list ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a cp configuration entry with the primary key could not be found
	 */
	public CPConfigurationEntry[] findByCPConfigurationListId_PrevAndNext(
			long CPConfigurationEntryId, long CPConfigurationListId,
			com.liferay.portal.kernel.util.OrderByComparator
				<CPConfigurationEntry> orderByComparator)
		throws NoSuchCPConfigurationEntryException;

	/**
	 * Removes all the cp configuration entries where CPConfigurationListId = &#63; from the database.
	 *
	 * @param CPConfigurationListId the cp configuration list ID
	 */
	public void removeByCPConfigurationListId(long CPConfigurationListId);

	/**
	 * Returns the number of cp configuration entries where CPConfigurationListId = &#63;.
	 *
	 * @param CPConfigurationListId the cp configuration list ID
	 * @return the number of matching cp configuration entries
	 */
	public int countByCPConfigurationListId(long CPConfigurationListId);

	/**
	 * Returns all the cp configuration entries where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching cp configuration entries
	 */
	public java.util.List<CPConfigurationEntry> findByC_C(
		long classNameId, long classPK);

	/**
	 * Returns a range of all the cp configuration entries where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPConfigurationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of cp configuration entries
	 * @param end the upper bound of the range of cp configuration entries (not inclusive)
	 * @return the range of matching cp configuration entries
	 */
	public java.util.List<CPConfigurationEntry> findByC_C(
		long classNameId, long classPK, int start, int end);

	/**
	 * Returns an ordered range of all the cp configuration entries where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPConfigurationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of cp configuration entries
	 * @param end the upper bound of the range of cp configuration entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp configuration entries
	 */
	public java.util.List<CPConfigurationEntry> findByC_C(
		long classNameId, long classPK, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CPConfigurationEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the cp configuration entries where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPConfigurationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of cp configuration entries
	 * @param end the upper bound of the range of cp configuration entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp configuration entries
	 */
	public java.util.List<CPConfigurationEntry> findByC_C(
		long classNameId, long classPK, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CPConfigurationEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first cp configuration entry in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a matching cp configuration entry could not be found
	 */
	public CPConfigurationEntry findByC_C_First(
			long classNameId, long classPK,
			com.liferay.portal.kernel.util.OrderByComparator
				<CPConfigurationEntry> orderByComparator)
		throws NoSuchCPConfigurationEntryException;

	/**
	 * Returns the first cp configuration entry in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp configuration entry, or <code>null</code> if a matching cp configuration entry could not be found
	 */
	public CPConfigurationEntry fetchByC_C_First(
		long classNameId, long classPK,
		com.liferay.portal.kernel.util.OrderByComparator<CPConfigurationEntry>
			orderByComparator);

	/**
	 * Returns the last cp configuration entry in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a matching cp configuration entry could not be found
	 */
	public CPConfigurationEntry findByC_C_Last(
			long classNameId, long classPK,
			com.liferay.portal.kernel.util.OrderByComparator
				<CPConfigurationEntry> orderByComparator)
		throws NoSuchCPConfigurationEntryException;

	/**
	 * Returns the last cp configuration entry in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching cp configuration entry, or <code>null</code> if a matching cp configuration entry could not be found
	 */
	public CPConfigurationEntry fetchByC_C_Last(
		long classNameId, long classPK,
		com.liferay.portal.kernel.util.OrderByComparator<CPConfigurationEntry>
			orderByComparator);

	/**
	 * Returns the cp configuration entries before and after the current cp configuration entry in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param CPConfigurationEntryId the primary key of the current cp configuration entry
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a cp configuration entry with the primary key could not be found
	 */
	public CPConfigurationEntry[] findByC_C_PrevAndNext(
			long CPConfigurationEntryId, long classNameId, long classPK,
			com.liferay.portal.kernel.util.OrderByComparator
				<CPConfigurationEntry> orderByComparator)
		throws NoSuchCPConfigurationEntryException;

	/**
	 * Removes all the cp configuration entries where classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	public void removeByC_C(long classNameId, long classPK);

	/**
	 * Returns the number of cp configuration entries where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching cp configuration entries
	 */
	public int countByC_C(long classNameId, long classPK);

	/**
	 * Returns the cp configuration entry where classNameId = &#63; and classPK = &#63; and CPConfigurationListId = &#63; or throws a <code>NoSuchCPConfigurationEntryException</code> if it could not be found.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param CPConfigurationListId the cp configuration list ID
	 * @return the matching cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a matching cp configuration entry could not be found
	 */
	public CPConfigurationEntry findByC_C_C(
			long classNameId, long classPK, long CPConfigurationListId)
		throws NoSuchCPConfigurationEntryException;

	/**
	 * Returns the cp configuration entry where classNameId = &#63; and classPK = &#63; and CPConfigurationListId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param CPConfigurationListId the cp configuration list ID
	 * @return the matching cp configuration entry, or <code>null</code> if a matching cp configuration entry could not be found
	 */
	public CPConfigurationEntry fetchByC_C_C(
		long classNameId, long classPK, long CPConfigurationListId);

	/**
	 * Returns the cp configuration entry where classNameId = &#63; and classPK = &#63; and CPConfigurationListId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param CPConfigurationListId the cp configuration list ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp configuration entry, or <code>null</code> if a matching cp configuration entry could not be found
	 */
	public CPConfigurationEntry fetchByC_C_C(
		long classNameId, long classPK, long CPConfigurationListId,
		boolean useFinderCache);

	/**
	 * Removes the cp configuration entry where classNameId = &#63; and classPK = &#63; and CPConfigurationListId = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param CPConfigurationListId the cp configuration list ID
	 * @return the cp configuration entry that was removed
	 */
	public CPConfigurationEntry removeByC_C_C(
			long classNameId, long classPK, long CPConfigurationListId)
		throws NoSuchCPConfigurationEntryException;

	/**
	 * Returns the number of cp configuration entries where classNameId = &#63; and classPK = &#63; and CPConfigurationListId = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param CPConfigurationListId the cp configuration list ID
	 * @return the number of matching cp configuration entries
	 */
	public int countByC_C_C(
		long classNameId, long classPK, long CPConfigurationListId);

	/**
	 * Returns the cp configuration entry where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchCPConfigurationEntryException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a matching cp configuration entry could not be found
	 */
	public CPConfigurationEntry findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchCPConfigurationEntryException;

	/**
	 * Returns the cp configuration entry where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching cp configuration entry, or <code>null</code> if a matching cp configuration entry could not be found
	 */
	public CPConfigurationEntry fetchByERC_C(
		String externalReferenceCode, long companyId);

	/**
	 * Returns the cp configuration entry where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp configuration entry, or <code>null</code> if a matching cp configuration entry could not be found
	 */
	public CPConfigurationEntry fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache);

	/**
	 * Removes the cp configuration entry where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the cp configuration entry that was removed
	 */
	public CPConfigurationEntry removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchCPConfigurationEntryException;

	/**
	 * Returns the number of cp configuration entries where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching cp configuration entries
	 */
	public int countByERC_C(String externalReferenceCode, long companyId);

	/**
	 * Caches the cp configuration entry in the entity cache if it is enabled.
	 *
	 * @param cpConfigurationEntry the cp configuration entry
	 */
	public void cacheResult(CPConfigurationEntry cpConfigurationEntry);

	/**
	 * Caches the cp configuration entries in the entity cache if it is enabled.
	 *
	 * @param cpConfigurationEntries the cp configuration entries
	 */
	public void cacheResult(
		java.util.List<CPConfigurationEntry> cpConfigurationEntries);

	/**
	 * Creates a new cp configuration entry with the primary key. Does not add the cp configuration entry to the database.
	 *
	 * @param CPConfigurationEntryId the primary key for the new cp configuration entry
	 * @return the new cp configuration entry
	 */
	public CPConfigurationEntry create(long CPConfigurationEntryId);

	/**
	 * Removes the cp configuration entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param CPConfigurationEntryId the primary key of the cp configuration entry
	 * @return the cp configuration entry that was removed
	 * @throws NoSuchCPConfigurationEntryException if a cp configuration entry with the primary key could not be found
	 */
	public CPConfigurationEntry remove(long CPConfigurationEntryId)
		throws NoSuchCPConfigurationEntryException;

	public CPConfigurationEntry updateImpl(
		CPConfigurationEntry cpConfigurationEntry);

	/**
	 * Returns the cp configuration entry with the primary key or throws a <code>NoSuchCPConfigurationEntryException</code> if it could not be found.
	 *
	 * @param CPConfigurationEntryId the primary key of the cp configuration entry
	 * @return the cp configuration entry
	 * @throws NoSuchCPConfigurationEntryException if a cp configuration entry with the primary key could not be found
	 */
	public CPConfigurationEntry findByPrimaryKey(long CPConfigurationEntryId)
		throws NoSuchCPConfigurationEntryException;

	/**
	 * Returns the cp configuration entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param CPConfigurationEntryId the primary key of the cp configuration entry
	 * @return the cp configuration entry, or <code>null</code> if a cp configuration entry with the primary key could not be found
	 */
	public CPConfigurationEntry fetchByPrimaryKey(long CPConfigurationEntryId);

	/**
	 * Returns all the cp configuration entries.
	 *
	 * @return the cp configuration entries
	 */
	public java.util.List<CPConfigurationEntry> findAll();

	/**
	 * Returns a range of all the cp configuration entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPConfigurationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cp configuration entries
	 * @param end the upper bound of the range of cp configuration entries (not inclusive)
	 * @return the range of cp configuration entries
	 */
	public java.util.List<CPConfigurationEntry> findAll(int start, int end);

	/**
	 * Returns an ordered range of all the cp configuration entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPConfigurationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cp configuration entries
	 * @param end the upper bound of the range of cp configuration entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of cp configuration entries
	 */
	public java.util.List<CPConfigurationEntry> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CPConfigurationEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the cp configuration entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CPConfigurationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cp configuration entries
	 * @param end the upper bound of the range of cp configuration entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of cp configuration entries
	 */
	public java.util.List<CPConfigurationEntry> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CPConfigurationEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the cp configuration entries from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of cp configuration entries.
	 *
	 * @return the number of cp configuration entries
	 */
	public int countAll();

}