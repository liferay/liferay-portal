/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.service.persistence;

import com.liferay.depot.exception.NoSuchEntryException;
import com.liferay.depot.model.DepotEntry;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the depot entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see DepotEntryUtil
 * @generated
 */
@ProviderType
public interface DepotEntryPersistence
	extends BasePersistence<DepotEntry>, CTPersistence<DepotEntry> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link DepotEntryUtil} to access the depot entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns all the depot entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching depot entries
	 */
	public java.util.List<DepotEntry> findByUuid(String uuid);

	/**
	 * Returns a range of all the depot entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of depot entries
	 * @param end the upper bound of the range of depot entries (not inclusive)
	 * @return the range of matching depot entries
	 */
	public java.util.List<DepotEntry> findByUuid(
		String uuid, int start, int end);

	/**
	 * Returns an ordered range of all the depot entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of depot entries
	 * @param end the upper bound of the range of depot entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching depot entries
	 */
	public java.util.List<DepotEntry> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DepotEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the depot entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of depot entries
	 * @param end the upper bound of the range of depot entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching depot entries
	 */
	public java.util.List<DepotEntry> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DepotEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first depot entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching depot entry
	 * @throws NoSuchEntryException if a matching depot entry could not be found
	 */
	public DepotEntry findByUuid_First(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<DepotEntry>
				orderByComparator)
		throws NoSuchEntryException;

	/**
	 * Returns the first depot entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching depot entry, or <code>null</code> if a matching depot entry could not be found
	 */
	public DepotEntry fetchByUuid_First(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<DepotEntry>
			orderByComparator);

	/**
	 * Returns the last depot entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching depot entry
	 * @throws NoSuchEntryException if a matching depot entry could not be found
	 */
	public DepotEntry findByUuid_Last(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<DepotEntry>
				orderByComparator)
		throws NoSuchEntryException;

	/**
	 * Returns the last depot entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching depot entry, or <code>null</code> if a matching depot entry could not be found
	 */
	public DepotEntry fetchByUuid_Last(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<DepotEntry>
			orderByComparator);

	/**
	 * Returns the depot entries before and after the current depot entry in the ordered set where uuid = &#63;.
	 *
	 * @param depotEntryId the primary key of the current depot entry
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next depot entry
	 * @throws NoSuchEntryException if a depot entry with the primary key could not be found
	 */
	public DepotEntry[] findByUuid_PrevAndNext(
			long depotEntryId, String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<DepotEntry>
				orderByComparator)
		throws NoSuchEntryException;

	/**
	 * Removes all the depot entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public void removeByUuid(String uuid);

	/**
	 * Returns the number of depot entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching depot entries
	 */
	public int countByUuid(String uuid);

	/**
	 * Returns the depot entry where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching depot entry
	 * @throws NoSuchEntryException if a matching depot entry could not be found
	 */
	public DepotEntry findByUUID_G(String uuid, long groupId)
		throws NoSuchEntryException;

	/**
	 * Returns the depot entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching depot entry, or <code>null</code> if a matching depot entry could not be found
	 */
	public DepotEntry fetchByUUID_G(String uuid, long groupId);

	/**
	 * Returns the depot entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching depot entry, or <code>null</code> if a matching depot entry could not be found
	 */
	public DepotEntry fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache);

	/**
	 * Removes the depot entry where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the depot entry that was removed
	 */
	public DepotEntry removeByUUID_G(String uuid, long groupId)
		throws NoSuchEntryException;

	/**
	 * Returns the number of depot entries where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching depot entries
	 */
	public int countByUUID_G(String uuid, long groupId);

	/**
	 * Returns all the depot entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching depot entries
	 */
	public java.util.List<DepotEntry> findByUuid_C(String uuid, long companyId);

	/**
	 * Returns a range of all the depot entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of depot entries
	 * @param end the upper bound of the range of depot entries (not inclusive)
	 * @return the range of matching depot entries
	 */
	public java.util.List<DepotEntry> findByUuid_C(
		String uuid, long companyId, int start, int end);

	/**
	 * Returns an ordered range of all the depot entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of depot entries
	 * @param end the upper bound of the range of depot entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching depot entries
	 */
	public java.util.List<DepotEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DepotEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the depot entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of depot entries
	 * @param end the upper bound of the range of depot entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching depot entries
	 */
	public java.util.List<DepotEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DepotEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first depot entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching depot entry
	 * @throws NoSuchEntryException if a matching depot entry could not be found
	 */
	public DepotEntry findByUuid_C_First(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<DepotEntry>
				orderByComparator)
		throws NoSuchEntryException;

	/**
	 * Returns the first depot entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching depot entry, or <code>null</code> if a matching depot entry could not be found
	 */
	public DepotEntry fetchByUuid_C_First(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<DepotEntry>
			orderByComparator);

	/**
	 * Returns the last depot entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching depot entry
	 * @throws NoSuchEntryException if a matching depot entry could not be found
	 */
	public DepotEntry findByUuid_C_Last(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<DepotEntry>
				orderByComparator)
		throws NoSuchEntryException;

	/**
	 * Returns the last depot entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching depot entry, or <code>null</code> if a matching depot entry could not be found
	 */
	public DepotEntry fetchByUuid_C_Last(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<DepotEntry>
			orderByComparator);

	/**
	 * Returns the depot entries before and after the current depot entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param depotEntryId the primary key of the current depot entry
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next depot entry
	 * @throws NoSuchEntryException if a depot entry with the primary key could not be found
	 */
	public DepotEntry[] findByUuid_C_PrevAndNext(
			long depotEntryId, String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<DepotEntry>
				orderByComparator)
		throws NoSuchEntryException;

	/**
	 * Removes all the depot entries where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public void removeByUuid_C(String uuid, long companyId);

	/**
	 * Returns the number of depot entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching depot entries
	 */
	public int countByUuid_C(String uuid, long companyId);

	/**
	 * Returns the depot entry where groupId = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @return the matching depot entry
	 * @throws NoSuchEntryException if a matching depot entry could not be found
	 */
	public DepotEntry findByGroupId(long groupId) throws NoSuchEntryException;

	/**
	 * Returns the depot entry where groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @return the matching depot entry, or <code>null</code> if a matching depot entry could not be found
	 */
	public DepotEntry fetchByGroupId(long groupId);

	/**
	 * Returns the depot entry where groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching depot entry, or <code>null</code> if a matching depot entry could not be found
	 */
	public DepotEntry fetchByGroupId(long groupId, boolean useFinderCache);

	/**
	 * Removes the depot entry where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @return the depot entry that was removed
	 */
	public DepotEntry removeByGroupId(long groupId) throws NoSuchEntryException;

	/**
	 * Returns the number of depot entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching depot entries
	 */
	public int countByGroupId(long groupId);

	/**
	 * Returns all the depot entries where companyId = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @return the matching depot entries
	 */
	public java.util.List<DepotEntry> findByC_T(long companyId, int type);

	/**
	 * Returns a range of all the depot entries where companyId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @param start the lower bound of the range of depot entries
	 * @param end the upper bound of the range of depot entries (not inclusive)
	 * @return the range of matching depot entries
	 */
	public java.util.List<DepotEntry> findByC_T(
		long companyId, int type, int start, int end);

	/**
	 * Returns an ordered range of all the depot entries where companyId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @param start the lower bound of the range of depot entries
	 * @param end the upper bound of the range of depot entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching depot entries
	 */
	public java.util.List<DepotEntry> findByC_T(
		long companyId, int type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DepotEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the depot entries where companyId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @param start the lower bound of the range of depot entries
	 * @param end the upper bound of the range of depot entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching depot entries
	 */
	public java.util.List<DepotEntry> findByC_T(
		long companyId, int type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DepotEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first depot entry in the ordered set where companyId = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching depot entry
	 * @throws NoSuchEntryException if a matching depot entry could not be found
	 */
	public DepotEntry findByC_T_First(
			long companyId, int type,
			com.liferay.portal.kernel.util.OrderByComparator<DepotEntry>
				orderByComparator)
		throws NoSuchEntryException;

	/**
	 * Returns the first depot entry in the ordered set where companyId = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching depot entry, or <code>null</code> if a matching depot entry could not be found
	 */
	public DepotEntry fetchByC_T_First(
		long companyId, int type,
		com.liferay.portal.kernel.util.OrderByComparator<DepotEntry>
			orderByComparator);

	/**
	 * Returns the last depot entry in the ordered set where companyId = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching depot entry
	 * @throws NoSuchEntryException if a matching depot entry could not be found
	 */
	public DepotEntry findByC_T_Last(
			long companyId, int type,
			com.liferay.portal.kernel.util.OrderByComparator<DepotEntry>
				orderByComparator)
		throws NoSuchEntryException;

	/**
	 * Returns the last depot entry in the ordered set where companyId = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching depot entry, or <code>null</code> if a matching depot entry could not be found
	 */
	public DepotEntry fetchByC_T_Last(
		long companyId, int type,
		com.liferay.portal.kernel.util.OrderByComparator<DepotEntry>
			orderByComparator);

	/**
	 * Returns the depot entries before and after the current depot entry in the ordered set where companyId = &#63; and type = &#63;.
	 *
	 * @param depotEntryId the primary key of the current depot entry
	 * @param companyId the company ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next depot entry
	 * @throws NoSuchEntryException if a depot entry with the primary key could not be found
	 */
	public DepotEntry[] findByC_T_PrevAndNext(
			long depotEntryId, long companyId, int type,
			com.liferay.portal.kernel.util.OrderByComparator<DepotEntry>
				orderByComparator)
		throws NoSuchEntryException;

	/**
	 * Removes all the depot entries where companyId = &#63; and type = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param type the type
	 */
	public void removeByC_T(long companyId, int type);

	/**
	 * Returns the number of depot entries where companyId = &#63; and type = &#63;.
	 *
	 * @param companyId the company ID
	 * @param type the type
	 * @return the number of matching depot entries
	 */
	public int countByC_T(long companyId, int type);

	/**
	 * Caches the depot entry in the entity cache if it is enabled.
	 *
	 * @param depotEntry the depot entry
	 */
	public void cacheResult(DepotEntry depotEntry);

	/**
	 * Caches the depot entries in the entity cache if it is enabled.
	 *
	 * @param depotEntries the depot entries
	 */
	public void cacheResult(java.util.List<DepotEntry> depotEntries);

	/**
	 * Creates a new depot entry with the primary key. Does not add the depot entry to the database.
	 *
	 * @param depotEntryId the primary key for the new depot entry
	 * @return the new depot entry
	 */
	public DepotEntry create(long depotEntryId);

	/**
	 * Removes the depot entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param depotEntryId the primary key of the depot entry
	 * @return the depot entry that was removed
	 * @throws NoSuchEntryException if a depot entry with the primary key could not be found
	 */
	public DepotEntry remove(long depotEntryId) throws NoSuchEntryException;

	public DepotEntry updateImpl(DepotEntry depotEntry);

	/**
	 * Returns the depot entry with the primary key or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param depotEntryId the primary key of the depot entry
	 * @return the depot entry
	 * @throws NoSuchEntryException if a depot entry with the primary key could not be found
	 */
	public DepotEntry findByPrimaryKey(long depotEntryId)
		throws NoSuchEntryException;

	/**
	 * Returns the depot entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param depotEntryId the primary key of the depot entry
	 * @return the depot entry, or <code>null</code> if a depot entry with the primary key could not be found
	 */
	public DepotEntry fetchByPrimaryKey(long depotEntryId);

	/**
	 * Returns all the depot entries.
	 *
	 * @return the depot entries
	 */
	public java.util.List<DepotEntry> findAll();

	/**
	 * Returns a range of all the depot entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of depot entries
	 * @param end the upper bound of the range of depot entries (not inclusive)
	 * @return the range of depot entries
	 */
	public java.util.List<DepotEntry> findAll(int start, int end);

	/**
	 * Returns an ordered range of all the depot entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of depot entries
	 * @param end the upper bound of the range of depot entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of depot entries
	 */
	public java.util.List<DepotEntry> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DepotEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the depot entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of depot entries
	 * @param end the upper bound of the range of depot entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of depot entries
	 */
	public java.util.List<DepotEntry> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DepotEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the depot entries from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of depot entries.
	 *
	 * @return the number of depot entries
	 */
	public int countAll();

}