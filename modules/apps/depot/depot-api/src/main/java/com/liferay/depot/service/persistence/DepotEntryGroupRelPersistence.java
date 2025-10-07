/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.service.persistence;

import com.liferay.depot.exception.NoSuchEntryGroupRelException;
import com.liferay.depot.model.DepotEntryGroupRel;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the depot entry group rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see DepotEntryGroupRelUtil
 * @generated
 */
@ProviderType
public interface DepotEntryGroupRelPersistence
	extends BasePersistence<DepotEntryGroupRel>,
			CTPersistence<DepotEntryGroupRel> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link DepotEntryGroupRelUtil} to access the depot entry group rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns all the depot entry group rels where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching depot entry group rels
	 */
	public java.util.List<DepotEntryGroupRel> findByUuid(String uuid);

	/**
	 * Returns a range of all the depot entry group rels where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @return the range of matching depot entry group rels
	 */
	public java.util.List<DepotEntryGroupRel> findByUuid(
		String uuid, int start, int end);

	/**
	 * Returns an ordered range of all the depot entry group rels where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching depot entry group rels
	 */
	public java.util.List<DepotEntryGroupRel> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DepotEntryGroupRel>
			orderByComparator);

	/**
	 * Returns an ordered range of all the depot entry group rels where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching depot entry group rels
	 */
	public java.util.List<DepotEntryGroupRel> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DepotEntryGroupRel>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first depot entry group rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a matching depot entry group rel could not be found
	 */
	public DepotEntryGroupRel findByUuid_First(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<DepotEntryGroupRel>
				orderByComparator)
		throws NoSuchEntryGroupRelException;

	/**
	 * Returns the first depot entry group rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching depot entry group rel, or <code>null</code> if a matching depot entry group rel could not be found
	 */
	public DepotEntryGroupRel fetchByUuid_First(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<DepotEntryGroupRel>
			orderByComparator);

	/**
	 * Returns the last depot entry group rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a matching depot entry group rel could not be found
	 */
	public DepotEntryGroupRel findByUuid_Last(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<DepotEntryGroupRel>
				orderByComparator)
		throws NoSuchEntryGroupRelException;

	/**
	 * Returns the last depot entry group rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching depot entry group rel, or <code>null</code> if a matching depot entry group rel could not be found
	 */
	public DepotEntryGroupRel fetchByUuid_Last(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<DepotEntryGroupRel>
			orderByComparator);

	/**
	 * Returns the depot entry group rels before and after the current depot entry group rel in the ordered set where uuid = &#63;.
	 *
	 * @param depotEntryGroupRelId the primary key of the current depot entry group rel
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a depot entry group rel with the primary key could not be found
	 */
	public DepotEntryGroupRel[] findByUuid_PrevAndNext(
			long depotEntryGroupRelId, String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<DepotEntryGroupRel>
				orderByComparator)
		throws NoSuchEntryGroupRelException;

	/**
	 * Removes all the depot entry group rels where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public void removeByUuid(String uuid);

	/**
	 * Returns the number of depot entry group rels where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching depot entry group rels
	 */
	public int countByUuid(String uuid);

	/**
	 * Returns the depot entry group rel where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchEntryGroupRelException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a matching depot entry group rel could not be found
	 */
	public DepotEntryGroupRel findByUUID_G(String uuid, long groupId)
		throws NoSuchEntryGroupRelException;

	/**
	 * Returns the depot entry group rel where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching depot entry group rel, or <code>null</code> if a matching depot entry group rel could not be found
	 */
	public DepotEntryGroupRel fetchByUUID_G(String uuid, long groupId);

	/**
	 * Returns the depot entry group rel where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching depot entry group rel, or <code>null</code> if a matching depot entry group rel could not be found
	 */
	public DepotEntryGroupRel fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache);

	/**
	 * Removes the depot entry group rel where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the depot entry group rel that was removed
	 */
	public DepotEntryGroupRel removeByUUID_G(String uuid, long groupId)
		throws NoSuchEntryGroupRelException;

	/**
	 * Returns the number of depot entry group rels where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching depot entry group rels
	 */
	public int countByUUID_G(String uuid, long groupId);

	/**
	 * Returns all the depot entry group rels where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching depot entry group rels
	 */
	public java.util.List<DepotEntryGroupRel> findByUuid_C(
		String uuid, long companyId);

	/**
	 * Returns a range of all the depot entry group rels where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @return the range of matching depot entry group rels
	 */
	public java.util.List<DepotEntryGroupRel> findByUuid_C(
		String uuid, long companyId, int start, int end);

	/**
	 * Returns an ordered range of all the depot entry group rels where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching depot entry group rels
	 */
	public java.util.List<DepotEntryGroupRel> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DepotEntryGroupRel>
			orderByComparator);

	/**
	 * Returns an ordered range of all the depot entry group rels where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching depot entry group rels
	 */
	public java.util.List<DepotEntryGroupRel> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DepotEntryGroupRel>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first depot entry group rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a matching depot entry group rel could not be found
	 */
	public DepotEntryGroupRel findByUuid_C_First(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<DepotEntryGroupRel>
				orderByComparator)
		throws NoSuchEntryGroupRelException;

	/**
	 * Returns the first depot entry group rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching depot entry group rel, or <code>null</code> if a matching depot entry group rel could not be found
	 */
	public DepotEntryGroupRel fetchByUuid_C_First(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<DepotEntryGroupRel>
			orderByComparator);

	/**
	 * Returns the last depot entry group rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a matching depot entry group rel could not be found
	 */
	public DepotEntryGroupRel findByUuid_C_Last(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<DepotEntryGroupRel>
				orderByComparator)
		throws NoSuchEntryGroupRelException;

	/**
	 * Returns the last depot entry group rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching depot entry group rel, or <code>null</code> if a matching depot entry group rel could not be found
	 */
	public DepotEntryGroupRel fetchByUuid_C_Last(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<DepotEntryGroupRel>
			orderByComparator);

	/**
	 * Returns the depot entry group rels before and after the current depot entry group rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param depotEntryGroupRelId the primary key of the current depot entry group rel
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a depot entry group rel with the primary key could not be found
	 */
	public DepotEntryGroupRel[] findByUuid_C_PrevAndNext(
			long depotEntryGroupRelId, String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<DepotEntryGroupRel>
				orderByComparator)
		throws NoSuchEntryGroupRelException;

	/**
	 * Removes all the depot entry group rels where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public void removeByUuid_C(String uuid, long companyId);

	/**
	 * Returns the number of depot entry group rels where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching depot entry group rels
	 */
	public int countByUuid_C(String uuid, long companyId);

	/**
	 * Returns all the depot entry group rels where depotEntryId = &#63;.
	 *
	 * @param depotEntryId the depot entry ID
	 * @return the matching depot entry group rels
	 */
	public java.util.List<DepotEntryGroupRel> findByDepotEntryId(
		long depotEntryId);

	/**
	 * Returns a range of all the depot entry group rels where depotEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param depotEntryId the depot entry ID
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @return the range of matching depot entry group rels
	 */
	public java.util.List<DepotEntryGroupRel> findByDepotEntryId(
		long depotEntryId, int start, int end);

	/**
	 * Returns an ordered range of all the depot entry group rels where depotEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param depotEntryId the depot entry ID
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching depot entry group rels
	 */
	public java.util.List<DepotEntryGroupRel> findByDepotEntryId(
		long depotEntryId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DepotEntryGroupRel>
			orderByComparator);

	/**
	 * Returns an ordered range of all the depot entry group rels where depotEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param depotEntryId the depot entry ID
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching depot entry group rels
	 */
	public java.util.List<DepotEntryGroupRel> findByDepotEntryId(
		long depotEntryId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DepotEntryGroupRel>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first depot entry group rel in the ordered set where depotEntryId = &#63;.
	 *
	 * @param depotEntryId the depot entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a matching depot entry group rel could not be found
	 */
	public DepotEntryGroupRel findByDepotEntryId_First(
			long depotEntryId,
			com.liferay.portal.kernel.util.OrderByComparator<DepotEntryGroupRel>
				orderByComparator)
		throws NoSuchEntryGroupRelException;

	/**
	 * Returns the first depot entry group rel in the ordered set where depotEntryId = &#63;.
	 *
	 * @param depotEntryId the depot entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching depot entry group rel, or <code>null</code> if a matching depot entry group rel could not be found
	 */
	public DepotEntryGroupRel fetchByDepotEntryId_First(
		long depotEntryId,
		com.liferay.portal.kernel.util.OrderByComparator<DepotEntryGroupRel>
			orderByComparator);

	/**
	 * Returns the last depot entry group rel in the ordered set where depotEntryId = &#63;.
	 *
	 * @param depotEntryId the depot entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a matching depot entry group rel could not be found
	 */
	public DepotEntryGroupRel findByDepotEntryId_Last(
			long depotEntryId,
			com.liferay.portal.kernel.util.OrderByComparator<DepotEntryGroupRel>
				orderByComparator)
		throws NoSuchEntryGroupRelException;

	/**
	 * Returns the last depot entry group rel in the ordered set where depotEntryId = &#63;.
	 *
	 * @param depotEntryId the depot entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching depot entry group rel, or <code>null</code> if a matching depot entry group rel could not be found
	 */
	public DepotEntryGroupRel fetchByDepotEntryId_Last(
		long depotEntryId,
		com.liferay.portal.kernel.util.OrderByComparator<DepotEntryGroupRel>
			orderByComparator);

	/**
	 * Returns the depot entry group rels before and after the current depot entry group rel in the ordered set where depotEntryId = &#63;.
	 *
	 * @param depotEntryGroupRelId the primary key of the current depot entry group rel
	 * @param depotEntryId the depot entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a depot entry group rel with the primary key could not be found
	 */
	public DepotEntryGroupRel[] findByDepotEntryId_PrevAndNext(
			long depotEntryGroupRelId, long depotEntryId,
			com.liferay.portal.kernel.util.OrderByComparator<DepotEntryGroupRel>
				orderByComparator)
		throws NoSuchEntryGroupRelException;

	/**
	 * Removes all the depot entry group rels where depotEntryId = &#63; from the database.
	 *
	 * @param depotEntryId the depot entry ID
	 */
	public void removeByDepotEntryId(long depotEntryId);

	/**
	 * Returns the number of depot entry group rels where depotEntryId = &#63;.
	 *
	 * @param depotEntryId the depot entry ID
	 * @return the number of matching depot entry group rels
	 */
	public int countByDepotEntryId(long depotEntryId);

	/**
	 * Returns all the depot entry group rels where toGroupId = &#63;.
	 *
	 * @param toGroupId the to group ID
	 * @return the matching depot entry group rels
	 */
	public java.util.List<DepotEntryGroupRel> findByToGroupId(long toGroupId);

	/**
	 * Returns a range of all the depot entry group rels where toGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param toGroupId the to group ID
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @return the range of matching depot entry group rels
	 */
	public java.util.List<DepotEntryGroupRel> findByToGroupId(
		long toGroupId, int start, int end);

	/**
	 * Returns an ordered range of all the depot entry group rels where toGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param toGroupId the to group ID
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching depot entry group rels
	 */
	public java.util.List<DepotEntryGroupRel> findByToGroupId(
		long toGroupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DepotEntryGroupRel>
			orderByComparator);

	/**
	 * Returns an ordered range of all the depot entry group rels where toGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param toGroupId the to group ID
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching depot entry group rels
	 */
	public java.util.List<DepotEntryGroupRel> findByToGroupId(
		long toGroupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DepotEntryGroupRel>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first depot entry group rel in the ordered set where toGroupId = &#63;.
	 *
	 * @param toGroupId the to group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a matching depot entry group rel could not be found
	 */
	public DepotEntryGroupRel findByToGroupId_First(
			long toGroupId,
			com.liferay.portal.kernel.util.OrderByComparator<DepotEntryGroupRel>
				orderByComparator)
		throws NoSuchEntryGroupRelException;

	/**
	 * Returns the first depot entry group rel in the ordered set where toGroupId = &#63;.
	 *
	 * @param toGroupId the to group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching depot entry group rel, or <code>null</code> if a matching depot entry group rel could not be found
	 */
	public DepotEntryGroupRel fetchByToGroupId_First(
		long toGroupId,
		com.liferay.portal.kernel.util.OrderByComparator<DepotEntryGroupRel>
			orderByComparator);

	/**
	 * Returns the last depot entry group rel in the ordered set where toGroupId = &#63;.
	 *
	 * @param toGroupId the to group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a matching depot entry group rel could not be found
	 */
	public DepotEntryGroupRel findByToGroupId_Last(
			long toGroupId,
			com.liferay.portal.kernel.util.OrderByComparator<DepotEntryGroupRel>
				orderByComparator)
		throws NoSuchEntryGroupRelException;

	/**
	 * Returns the last depot entry group rel in the ordered set where toGroupId = &#63;.
	 *
	 * @param toGroupId the to group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching depot entry group rel, or <code>null</code> if a matching depot entry group rel could not be found
	 */
	public DepotEntryGroupRel fetchByToGroupId_Last(
		long toGroupId,
		com.liferay.portal.kernel.util.OrderByComparator<DepotEntryGroupRel>
			orderByComparator);

	/**
	 * Returns the depot entry group rels before and after the current depot entry group rel in the ordered set where toGroupId = &#63;.
	 *
	 * @param depotEntryGroupRelId the primary key of the current depot entry group rel
	 * @param toGroupId the to group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a depot entry group rel with the primary key could not be found
	 */
	public DepotEntryGroupRel[] findByToGroupId_PrevAndNext(
			long depotEntryGroupRelId, long toGroupId,
			com.liferay.portal.kernel.util.OrderByComparator<DepotEntryGroupRel>
				orderByComparator)
		throws NoSuchEntryGroupRelException;

	/**
	 * Removes all the depot entry group rels where toGroupId = &#63; from the database.
	 *
	 * @param toGroupId the to group ID
	 */
	public void removeByToGroupId(long toGroupId);

	/**
	 * Returns the number of depot entry group rels where toGroupId = &#63;.
	 *
	 * @param toGroupId the to group ID
	 * @return the number of matching depot entry group rels
	 */
	public int countByToGroupId(long toGroupId);

	/**
	 * Returns all the depot entry group rels where ddmStructuresAvailable = &#63; and toGroupId = &#63;.
	 *
	 * @param ddmStructuresAvailable the ddm structures available
	 * @param toGroupId the to group ID
	 * @return the matching depot entry group rels
	 */
	public java.util.List<DepotEntryGroupRel> findByDDMSA_TGI(
		boolean ddmStructuresAvailable, long toGroupId);

	/**
	 * Returns a range of all the depot entry group rels where ddmStructuresAvailable = &#63; and toGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param ddmStructuresAvailable the ddm structures available
	 * @param toGroupId the to group ID
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @return the range of matching depot entry group rels
	 */
	public java.util.List<DepotEntryGroupRel> findByDDMSA_TGI(
		boolean ddmStructuresAvailable, long toGroupId, int start, int end);

	/**
	 * Returns an ordered range of all the depot entry group rels where ddmStructuresAvailable = &#63; and toGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param ddmStructuresAvailable the ddm structures available
	 * @param toGroupId the to group ID
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching depot entry group rels
	 */
	public java.util.List<DepotEntryGroupRel> findByDDMSA_TGI(
		boolean ddmStructuresAvailable, long toGroupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DepotEntryGroupRel>
			orderByComparator);

	/**
	 * Returns an ordered range of all the depot entry group rels where ddmStructuresAvailable = &#63; and toGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param ddmStructuresAvailable the ddm structures available
	 * @param toGroupId the to group ID
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching depot entry group rels
	 */
	public java.util.List<DepotEntryGroupRel> findByDDMSA_TGI(
		boolean ddmStructuresAvailable, long toGroupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DepotEntryGroupRel>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first depot entry group rel in the ordered set where ddmStructuresAvailable = &#63; and toGroupId = &#63;.
	 *
	 * @param ddmStructuresAvailable the ddm structures available
	 * @param toGroupId the to group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a matching depot entry group rel could not be found
	 */
	public DepotEntryGroupRel findByDDMSA_TGI_First(
			boolean ddmStructuresAvailable, long toGroupId,
			com.liferay.portal.kernel.util.OrderByComparator<DepotEntryGroupRel>
				orderByComparator)
		throws NoSuchEntryGroupRelException;

	/**
	 * Returns the first depot entry group rel in the ordered set where ddmStructuresAvailable = &#63; and toGroupId = &#63;.
	 *
	 * @param ddmStructuresAvailable the ddm structures available
	 * @param toGroupId the to group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching depot entry group rel, or <code>null</code> if a matching depot entry group rel could not be found
	 */
	public DepotEntryGroupRel fetchByDDMSA_TGI_First(
		boolean ddmStructuresAvailable, long toGroupId,
		com.liferay.portal.kernel.util.OrderByComparator<DepotEntryGroupRel>
			orderByComparator);

	/**
	 * Returns the last depot entry group rel in the ordered set where ddmStructuresAvailable = &#63; and toGroupId = &#63;.
	 *
	 * @param ddmStructuresAvailable the ddm structures available
	 * @param toGroupId the to group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a matching depot entry group rel could not be found
	 */
	public DepotEntryGroupRel findByDDMSA_TGI_Last(
			boolean ddmStructuresAvailable, long toGroupId,
			com.liferay.portal.kernel.util.OrderByComparator<DepotEntryGroupRel>
				orderByComparator)
		throws NoSuchEntryGroupRelException;

	/**
	 * Returns the last depot entry group rel in the ordered set where ddmStructuresAvailable = &#63; and toGroupId = &#63;.
	 *
	 * @param ddmStructuresAvailable the ddm structures available
	 * @param toGroupId the to group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching depot entry group rel, or <code>null</code> if a matching depot entry group rel could not be found
	 */
	public DepotEntryGroupRel fetchByDDMSA_TGI_Last(
		boolean ddmStructuresAvailable, long toGroupId,
		com.liferay.portal.kernel.util.OrderByComparator<DepotEntryGroupRel>
			orderByComparator);

	/**
	 * Returns the depot entry group rels before and after the current depot entry group rel in the ordered set where ddmStructuresAvailable = &#63; and toGroupId = &#63;.
	 *
	 * @param depotEntryGroupRelId the primary key of the current depot entry group rel
	 * @param ddmStructuresAvailable the ddm structures available
	 * @param toGroupId the to group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a depot entry group rel with the primary key could not be found
	 */
	public DepotEntryGroupRel[] findByDDMSA_TGI_PrevAndNext(
			long depotEntryGroupRelId, boolean ddmStructuresAvailable,
			long toGroupId,
			com.liferay.portal.kernel.util.OrderByComparator<DepotEntryGroupRel>
				orderByComparator)
		throws NoSuchEntryGroupRelException;

	/**
	 * Removes all the depot entry group rels where ddmStructuresAvailable = &#63; and toGroupId = &#63; from the database.
	 *
	 * @param ddmStructuresAvailable the ddm structures available
	 * @param toGroupId the to group ID
	 */
	public void removeByDDMSA_TGI(
		boolean ddmStructuresAvailable, long toGroupId);

	/**
	 * Returns the number of depot entry group rels where ddmStructuresAvailable = &#63; and toGroupId = &#63;.
	 *
	 * @param ddmStructuresAvailable the ddm structures available
	 * @param toGroupId the to group ID
	 * @return the number of matching depot entry group rels
	 */
	public int countByDDMSA_TGI(boolean ddmStructuresAvailable, long toGroupId);

	/**
	 * Returns the depot entry group rel where depotEntryId = &#63; and toGroupId = &#63; or throws a <code>NoSuchEntryGroupRelException</code> if it could not be found.
	 *
	 * @param depotEntryId the depot entry ID
	 * @param toGroupId the to group ID
	 * @return the matching depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a matching depot entry group rel could not be found
	 */
	public DepotEntryGroupRel findByD_TGI(long depotEntryId, long toGroupId)
		throws NoSuchEntryGroupRelException;

	/**
	 * Returns the depot entry group rel where depotEntryId = &#63; and toGroupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param depotEntryId the depot entry ID
	 * @param toGroupId the to group ID
	 * @return the matching depot entry group rel, or <code>null</code> if a matching depot entry group rel could not be found
	 */
	public DepotEntryGroupRel fetchByD_TGI(long depotEntryId, long toGroupId);

	/**
	 * Returns the depot entry group rel where depotEntryId = &#63; and toGroupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param depotEntryId the depot entry ID
	 * @param toGroupId the to group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching depot entry group rel, or <code>null</code> if a matching depot entry group rel could not be found
	 */
	public DepotEntryGroupRel fetchByD_TGI(
		long depotEntryId, long toGroupId, boolean useFinderCache);

	/**
	 * Removes the depot entry group rel where depotEntryId = &#63; and toGroupId = &#63; from the database.
	 *
	 * @param depotEntryId the depot entry ID
	 * @param toGroupId the to group ID
	 * @return the depot entry group rel that was removed
	 */
	public DepotEntryGroupRel removeByD_TGI(long depotEntryId, long toGroupId)
		throws NoSuchEntryGroupRelException;

	/**
	 * Returns the number of depot entry group rels where depotEntryId = &#63; and toGroupId = &#63;.
	 *
	 * @param depotEntryId the depot entry ID
	 * @param toGroupId the to group ID
	 * @return the number of matching depot entry group rels
	 */
	public int countByD_TGI(long depotEntryId, long toGroupId);

	/**
	 * Returns all the depot entry group rels where searchable = &#63; and toGroupId = &#63;.
	 *
	 * @param searchable the searchable
	 * @param toGroupId the to group ID
	 * @return the matching depot entry group rels
	 */
	public java.util.List<DepotEntryGroupRel> findByS_TGI(
		boolean searchable, long toGroupId);

	/**
	 * Returns a range of all the depot entry group rels where searchable = &#63; and toGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param searchable the searchable
	 * @param toGroupId the to group ID
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @return the range of matching depot entry group rels
	 */
	public java.util.List<DepotEntryGroupRel> findByS_TGI(
		boolean searchable, long toGroupId, int start, int end);

	/**
	 * Returns an ordered range of all the depot entry group rels where searchable = &#63; and toGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param searchable the searchable
	 * @param toGroupId the to group ID
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching depot entry group rels
	 */
	public java.util.List<DepotEntryGroupRel> findByS_TGI(
		boolean searchable, long toGroupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DepotEntryGroupRel>
			orderByComparator);

	/**
	 * Returns an ordered range of all the depot entry group rels where searchable = &#63; and toGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param searchable the searchable
	 * @param toGroupId the to group ID
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching depot entry group rels
	 */
	public java.util.List<DepotEntryGroupRel> findByS_TGI(
		boolean searchable, long toGroupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DepotEntryGroupRel>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first depot entry group rel in the ordered set where searchable = &#63; and toGroupId = &#63;.
	 *
	 * @param searchable the searchable
	 * @param toGroupId the to group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a matching depot entry group rel could not be found
	 */
	public DepotEntryGroupRel findByS_TGI_First(
			boolean searchable, long toGroupId,
			com.liferay.portal.kernel.util.OrderByComparator<DepotEntryGroupRel>
				orderByComparator)
		throws NoSuchEntryGroupRelException;

	/**
	 * Returns the first depot entry group rel in the ordered set where searchable = &#63; and toGroupId = &#63;.
	 *
	 * @param searchable the searchable
	 * @param toGroupId the to group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching depot entry group rel, or <code>null</code> if a matching depot entry group rel could not be found
	 */
	public DepotEntryGroupRel fetchByS_TGI_First(
		boolean searchable, long toGroupId,
		com.liferay.portal.kernel.util.OrderByComparator<DepotEntryGroupRel>
			orderByComparator);

	/**
	 * Returns the last depot entry group rel in the ordered set where searchable = &#63; and toGroupId = &#63;.
	 *
	 * @param searchable the searchable
	 * @param toGroupId the to group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a matching depot entry group rel could not be found
	 */
	public DepotEntryGroupRel findByS_TGI_Last(
			boolean searchable, long toGroupId,
			com.liferay.portal.kernel.util.OrderByComparator<DepotEntryGroupRel>
				orderByComparator)
		throws NoSuchEntryGroupRelException;

	/**
	 * Returns the last depot entry group rel in the ordered set where searchable = &#63; and toGroupId = &#63;.
	 *
	 * @param searchable the searchable
	 * @param toGroupId the to group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching depot entry group rel, or <code>null</code> if a matching depot entry group rel could not be found
	 */
	public DepotEntryGroupRel fetchByS_TGI_Last(
		boolean searchable, long toGroupId,
		com.liferay.portal.kernel.util.OrderByComparator<DepotEntryGroupRel>
			orderByComparator);

	/**
	 * Returns the depot entry group rels before and after the current depot entry group rel in the ordered set where searchable = &#63; and toGroupId = &#63;.
	 *
	 * @param depotEntryGroupRelId the primary key of the current depot entry group rel
	 * @param searchable the searchable
	 * @param toGroupId the to group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a depot entry group rel with the primary key could not be found
	 */
	public DepotEntryGroupRel[] findByS_TGI_PrevAndNext(
			long depotEntryGroupRelId, boolean searchable, long toGroupId,
			com.liferay.portal.kernel.util.OrderByComparator<DepotEntryGroupRel>
				orderByComparator)
		throws NoSuchEntryGroupRelException;

	/**
	 * Removes all the depot entry group rels where searchable = &#63; and toGroupId = &#63; from the database.
	 *
	 * @param searchable the searchable
	 * @param toGroupId the to group ID
	 */
	public void removeByS_TGI(boolean searchable, long toGroupId);

	/**
	 * Returns the number of depot entry group rels where searchable = &#63; and toGroupId = &#63;.
	 *
	 * @param searchable the searchable
	 * @param toGroupId the to group ID
	 * @return the number of matching depot entry group rels
	 */
	public int countByS_TGI(boolean searchable, long toGroupId);

	/**
	 * Returns all the depot entry group rels where toGroupId = &#63; and type = &#63;.
	 *
	 * @param toGroupId the to group ID
	 * @param type the type
	 * @return the matching depot entry group rels
	 */
	public java.util.List<DepotEntryGroupRel> findByTGI_T(
		long toGroupId, int type);

	/**
	 * Returns a range of all the depot entry group rels where toGroupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param toGroupId the to group ID
	 * @param type the type
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @return the range of matching depot entry group rels
	 */
	public java.util.List<DepotEntryGroupRel> findByTGI_T(
		long toGroupId, int type, int start, int end);

	/**
	 * Returns an ordered range of all the depot entry group rels where toGroupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param toGroupId the to group ID
	 * @param type the type
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching depot entry group rels
	 */
	public java.util.List<DepotEntryGroupRel> findByTGI_T(
		long toGroupId, int type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DepotEntryGroupRel>
			orderByComparator);

	/**
	 * Returns an ordered range of all the depot entry group rels where toGroupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param toGroupId the to group ID
	 * @param type the type
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching depot entry group rels
	 */
	public java.util.List<DepotEntryGroupRel> findByTGI_T(
		long toGroupId, int type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DepotEntryGroupRel>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first depot entry group rel in the ordered set where toGroupId = &#63; and type = &#63;.
	 *
	 * @param toGroupId the to group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a matching depot entry group rel could not be found
	 */
	public DepotEntryGroupRel findByTGI_T_First(
			long toGroupId, int type,
			com.liferay.portal.kernel.util.OrderByComparator<DepotEntryGroupRel>
				orderByComparator)
		throws NoSuchEntryGroupRelException;

	/**
	 * Returns the first depot entry group rel in the ordered set where toGroupId = &#63; and type = &#63;.
	 *
	 * @param toGroupId the to group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching depot entry group rel, or <code>null</code> if a matching depot entry group rel could not be found
	 */
	public DepotEntryGroupRel fetchByTGI_T_First(
		long toGroupId, int type,
		com.liferay.portal.kernel.util.OrderByComparator<DepotEntryGroupRel>
			orderByComparator);

	/**
	 * Returns the last depot entry group rel in the ordered set where toGroupId = &#63; and type = &#63;.
	 *
	 * @param toGroupId the to group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a matching depot entry group rel could not be found
	 */
	public DepotEntryGroupRel findByTGI_T_Last(
			long toGroupId, int type,
			com.liferay.portal.kernel.util.OrderByComparator<DepotEntryGroupRel>
				orderByComparator)
		throws NoSuchEntryGroupRelException;

	/**
	 * Returns the last depot entry group rel in the ordered set where toGroupId = &#63; and type = &#63;.
	 *
	 * @param toGroupId the to group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching depot entry group rel, or <code>null</code> if a matching depot entry group rel could not be found
	 */
	public DepotEntryGroupRel fetchByTGI_T_Last(
		long toGroupId, int type,
		com.liferay.portal.kernel.util.OrderByComparator<DepotEntryGroupRel>
			orderByComparator);

	/**
	 * Returns the depot entry group rels before and after the current depot entry group rel in the ordered set where toGroupId = &#63; and type = &#63;.
	 *
	 * @param depotEntryGroupRelId the primary key of the current depot entry group rel
	 * @param toGroupId the to group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a depot entry group rel with the primary key could not be found
	 */
	public DepotEntryGroupRel[] findByTGI_T_PrevAndNext(
			long depotEntryGroupRelId, long toGroupId, int type,
			com.liferay.portal.kernel.util.OrderByComparator<DepotEntryGroupRel>
				orderByComparator)
		throws NoSuchEntryGroupRelException;

	/**
	 * Removes all the depot entry group rels where toGroupId = &#63; and type = &#63; from the database.
	 *
	 * @param toGroupId the to group ID
	 * @param type the type
	 */
	public void removeByTGI_T(long toGroupId, int type);

	/**
	 * Returns the number of depot entry group rels where toGroupId = &#63; and type = &#63;.
	 *
	 * @param toGroupId the to group ID
	 * @param type the type
	 * @return the number of matching depot entry group rels
	 */
	public int countByTGI_T(long toGroupId, int type);

	/**
	 * Caches the depot entry group rel in the entity cache if it is enabled.
	 *
	 * @param depotEntryGroupRel the depot entry group rel
	 */
	public void cacheResult(DepotEntryGroupRel depotEntryGroupRel);

	/**
	 * Caches the depot entry group rels in the entity cache if it is enabled.
	 *
	 * @param depotEntryGroupRels the depot entry group rels
	 */
	public void cacheResult(
		java.util.List<DepotEntryGroupRel> depotEntryGroupRels);

	/**
	 * Creates a new depot entry group rel with the primary key. Does not add the depot entry group rel to the database.
	 *
	 * @param depotEntryGroupRelId the primary key for the new depot entry group rel
	 * @return the new depot entry group rel
	 */
	public DepotEntryGroupRel create(long depotEntryGroupRelId);

	/**
	 * Removes the depot entry group rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param depotEntryGroupRelId the primary key of the depot entry group rel
	 * @return the depot entry group rel that was removed
	 * @throws NoSuchEntryGroupRelException if a depot entry group rel with the primary key could not be found
	 */
	public DepotEntryGroupRel remove(long depotEntryGroupRelId)
		throws NoSuchEntryGroupRelException;

	public DepotEntryGroupRel updateImpl(DepotEntryGroupRel depotEntryGroupRel);

	/**
	 * Returns the depot entry group rel with the primary key or throws a <code>NoSuchEntryGroupRelException</code> if it could not be found.
	 *
	 * @param depotEntryGroupRelId the primary key of the depot entry group rel
	 * @return the depot entry group rel
	 * @throws NoSuchEntryGroupRelException if a depot entry group rel with the primary key could not be found
	 */
	public DepotEntryGroupRel findByPrimaryKey(long depotEntryGroupRelId)
		throws NoSuchEntryGroupRelException;

	/**
	 * Returns the depot entry group rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param depotEntryGroupRelId the primary key of the depot entry group rel
	 * @return the depot entry group rel, or <code>null</code> if a depot entry group rel with the primary key could not be found
	 */
	public DepotEntryGroupRel fetchByPrimaryKey(long depotEntryGroupRelId);

	/**
	 * Returns all the depot entry group rels.
	 *
	 * @return the depot entry group rels
	 */
	public java.util.List<DepotEntryGroupRel> findAll();

	/**
	 * Returns a range of all the depot entry group rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @return the range of depot entry group rels
	 */
	public java.util.List<DepotEntryGroupRel> findAll(int start, int end);

	/**
	 * Returns an ordered range of all the depot entry group rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of depot entry group rels
	 */
	public java.util.List<DepotEntryGroupRel> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DepotEntryGroupRel>
			orderByComparator);

	/**
	 * Returns an ordered range of all the depot entry group rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DepotEntryGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of depot entry group rels
	 * @param end the upper bound of the range of depot entry group rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of depot entry group rels
	 */
	public java.util.List<DepotEntryGroupRel> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DepotEntryGroupRel>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the depot entry group rels from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of depot entry group rels.
	 *
	 * @return the number of depot entry group rels
	 */
	public int countAll();

}