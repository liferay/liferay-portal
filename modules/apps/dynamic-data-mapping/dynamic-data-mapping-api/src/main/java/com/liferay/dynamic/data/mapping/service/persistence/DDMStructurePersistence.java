/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.persistence;

import com.liferay.dynamic.data.mapping.exception.NoSuchStructureException;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the ddm structure service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see DDMStructureUtil
 * @generated
 */
@ProviderType
public interface DDMStructurePersistence
	extends BasePersistence<DDMStructure>, CTPersistence<DDMStructure> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link DDMStructureUtil} to access the ddm structure persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns all the ddm structures where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching ddm structures
	 */
	public java.util.List<DDMStructure> findByUuid(String uuid);

	/**
	 * Returns a range of all the ddm structures where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of matching ddm structures
	 */
	public java.util.List<DDMStructure> findByUuid(
		String uuid, int start, int end);

	/**
	 * Returns an ordered range of all the ddm structures where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures
	 */
	public java.util.List<DDMStructure> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator);

	/**
	 * Returns an ordered range of all the ddm structures where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm structures
	 */
	public java.util.List<DDMStructure> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first ddm structure in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	public DDMStructure findByUuid_First(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
				orderByComparator)
		throws NoSuchStructureException;

	/**
	 * Returns the first ddm structure in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	public DDMStructure fetchByUuid_First(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator);

	/**
	 * Returns the last ddm structure in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	public DDMStructure findByUuid_Last(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
				orderByComparator)
		throws NoSuchStructureException;

	/**
	 * Returns the last ddm structure in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	public DDMStructure fetchByUuid_Last(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator);

	/**
	 * Returns the ddm structures before and after the current ddm structure in the ordered set where uuid = &#63;.
	 *
	 * @param structureId the primary key of the current ddm structure
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm structure
	 * @throws NoSuchStructureException if a ddm structure with the primary key could not be found
	 */
	public DDMStructure[] findByUuid_PrevAndNext(
			long structureId, String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
				orderByComparator)
		throws NoSuchStructureException;

	/**
	 * Removes all the ddm structures where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public void removeByUuid(String uuid);

	/**
	 * Returns the number of ddm structures where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching ddm structures
	 */
	public int countByUuid(String uuid);

	/**
	 * Returns the ddm structure where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchStructureException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	public DDMStructure findByUUID_G(String uuid, long groupId)
		throws NoSuchStructureException;

	/**
	 * Returns the ddm structure where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	public DDMStructure fetchByUUID_G(String uuid, long groupId);

	/**
	 * Returns the ddm structure where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	public DDMStructure fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache);

	/**
	 * Removes the ddm structure where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the ddm structure that was removed
	 */
	public DDMStructure removeByUUID_G(String uuid, long groupId)
		throws NoSuchStructureException;

	/**
	 * Returns the number of ddm structures where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching ddm structures
	 */
	public int countByUUID_G(String uuid, long groupId);

	/**
	 * Returns all the ddm structures where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching ddm structures
	 */
	public java.util.List<DDMStructure> findByUuid_C(
		String uuid, long companyId);

	/**
	 * Returns a range of all the ddm structures where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of matching ddm structures
	 */
	public java.util.List<DDMStructure> findByUuid_C(
		String uuid, long companyId, int start, int end);

	/**
	 * Returns an ordered range of all the ddm structures where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures
	 */
	public java.util.List<DDMStructure> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator);

	/**
	 * Returns an ordered range of all the ddm structures where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm structures
	 */
	public java.util.List<DDMStructure> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first ddm structure in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	public DDMStructure findByUuid_C_First(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
				orderByComparator)
		throws NoSuchStructureException;

	/**
	 * Returns the first ddm structure in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	public DDMStructure fetchByUuid_C_First(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator);

	/**
	 * Returns the last ddm structure in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	public DDMStructure findByUuid_C_Last(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
				orderByComparator)
		throws NoSuchStructureException;

	/**
	 * Returns the last ddm structure in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	public DDMStructure fetchByUuid_C_Last(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator);

	/**
	 * Returns the ddm structures before and after the current ddm structure in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param structureId the primary key of the current ddm structure
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm structure
	 * @throws NoSuchStructureException if a ddm structure with the primary key could not be found
	 */
	public DDMStructure[] findByUuid_C_PrevAndNext(
			long structureId, String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
				orderByComparator)
		throws NoSuchStructureException;

	/**
	 * Removes all the ddm structures where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public void removeByUuid_C(String uuid, long companyId);

	/**
	 * Returns the number of ddm structures where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching ddm structures
	 */
	public int countByUuid_C(String uuid, long companyId);

	/**
	 * Returns all the ddm structures where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching ddm structures
	 */
	public java.util.List<DDMStructure> findByGroupId(long groupId);

	/**
	 * Returns a range of all the ddm structures where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of matching ddm structures
	 */
	public java.util.List<DDMStructure> findByGroupId(
		long groupId, int start, int end);

	/**
	 * Returns an ordered range of all the ddm structures where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures
	 */
	public java.util.List<DDMStructure> findByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator);

	/**
	 * Returns an ordered range of all the ddm structures where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm structures
	 */
	public java.util.List<DDMStructure> findByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first ddm structure in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	public DDMStructure findByGroupId_First(
			long groupId,
			com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
				orderByComparator)
		throws NoSuchStructureException;

	/**
	 * Returns the first ddm structure in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	public DDMStructure fetchByGroupId_First(
		long groupId,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator);

	/**
	 * Returns the last ddm structure in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	public DDMStructure findByGroupId_Last(
			long groupId,
			com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
				orderByComparator)
		throws NoSuchStructureException;

	/**
	 * Returns the last ddm structure in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	public DDMStructure fetchByGroupId_Last(
		long groupId,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator);

	/**
	 * Returns the ddm structures before and after the current ddm structure in the ordered set where groupId = &#63;.
	 *
	 * @param structureId the primary key of the current ddm structure
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm structure
	 * @throws NoSuchStructureException if a ddm structure with the primary key could not be found
	 */
	public DDMStructure[] findByGroupId_PrevAndNext(
			long structureId, long groupId,
			com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
				orderByComparator)
		throws NoSuchStructureException;

	/**
	 * Returns all the ddm structures that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching ddm structures that the user has permission to view
	 */
	public java.util.List<DDMStructure> filterFindByGroupId(long groupId);

	/**
	 * Returns a range of all the ddm structures that the user has permission to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of matching ddm structures that the user has permission to view
	 */
	public java.util.List<DDMStructure> filterFindByGroupId(
		long groupId, int start, int end);

	/**
	 * Returns an ordered range of all the ddm structures that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures that the user has permission to view
	 */
	public java.util.List<DDMStructure> filterFindByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator);

	/**
	 * Returns the ddm structures before and after the current ddm structure in the ordered set of ddm structures that the user has permission to view where groupId = &#63;.
	 *
	 * @param structureId the primary key of the current ddm structure
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm structure
	 * @throws NoSuchStructureException if a ddm structure with the primary key could not be found
	 */
	public DDMStructure[] filterFindByGroupId_PrevAndNext(
			long structureId, long groupId,
			com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
				orderByComparator)
		throws NoSuchStructureException;

	/**
	 * Returns all the ddm structures that the user has permission to view where groupId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @return the matching ddm structures that the user has permission to view
	 */
	public java.util.List<DDMStructure> filterFindByGroupId(long[] groupIds);

	/**
	 * Returns a range of all the ddm structures that the user has permission to view where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of matching ddm structures that the user has permission to view
	 */
	public java.util.List<DDMStructure> filterFindByGroupId(
		long[] groupIds, int start, int end);

	/**
	 * Returns an ordered range of all the ddm structures that the user has permission to view where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures that the user has permission to view
	 */
	public java.util.List<DDMStructure> filterFindByGroupId(
		long[] groupIds, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator);

	/**
	 * Returns all the ddm structures where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @return the matching ddm structures
	 */
	public java.util.List<DDMStructure> findByGroupId(long[] groupIds);

	/**
	 * Returns a range of all the ddm structures where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of matching ddm structures
	 */
	public java.util.List<DDMStructure> findByGroupId(
		long[] groupIds, int start, int end);

	/**
	 * Returns an ordered range of all the ddm structures where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures
	 */
	public java.util.List<DDMStructure> findByGroupId(
		long[] groupIds, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator);

	/**
	 * Returns an ordered range of all the ddm structures where groupId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm structures
	 */
	public java.util.List<DDMStructure> findByGroupId(
		long[] groupIds, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the ddm structures where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public void removeByGroupId(long groupId);

	/**
	 * Returns the number of ddm structures where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching ddm structures
	 */
	public int countByGroupId(long groupId);

	/**
	 * Returns the number of ddm structures where groupId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @return the number of matching ddm structures
	 */
	public int countByGroupId(long[] groupIds);

	/**
	 * Returns the number of ddm structures that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching ddm structures that the user has permission to view
	 */
	public int filterCountByGroupId(long groupId);

	/**
	 * Returns the number of ddm structures that the user has permission to view where groupId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @return the number of matching ddm structures that the user has permission to view
	 */
	public int filterCountByGroupId(long[] groupIds);

	/**
	 * Returns all the ddm structures where parentStructureId = &#63;.
	 *
	 * @param parentStructureId the parent structure ID
	 * @return the matching ddm structures
	 */
	public java.util.List<DDMStructure> findByParentStructureId(
		long parentStructureId);

	/**
	 * Returns a range of all the ddm structures where parentStructureId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param parentStructureId the parent structure ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of matching ddm structures
	 */
	public java.util.List<DDMStructure> findByParentStructureId(
		long parentStructureId, int start, int end);

	/**
	 * Returns an ordered range of all the ddm structures where parentStructureId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param parentStructureId the parent structure ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures
	 */
	public java.util.List<DDMStructure> findByParentStructureId(
		long parentStructureId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator);

	/**
	 * Returns an ordered range of all the ddm structures where parentStructureId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param parentStructureId the parent structure ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm structures
	 */
	public java.util.List<DDMStructure> findByParentStructureId(
		long parentStructureId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first ddm structure in the ordered set where parentStructureId = &#63;.
	 *
	 * @param parentStructureId the parent structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	public DDMStructure findByParentStructureId_First(
			long parentStructureId,
			com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
				orderByComparator)
		throws NoSuchStructureException;

	/**
	 * Returns the first ddm structure in the ordered set where parentStructureId = &#63;.
	 *
	 * @param parentStructureId the parent structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	public DDMStructure fetchByParentStructureId_First(
		long parentStructureId,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator);

	/**
	 * Returns the last ddm structure in the ordered set where parentStructureId = &#63;.
	 *
	 * @param parentStructureId the parent structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	public DDMStructure findByParentStructureId_Last(
			long parentStructureId,
			com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
				orderByComparator)
		throws NoSuchStructureException;

	/**
	 * Returns the last ddm structure in the ordered set where parentStructureId = &#63;.
	 *
	 * @param parentStructureId the parent structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	public DDMStructure fetchByParentStructureId_Last(
		long parentStructureId,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator);

	/**
	 * Returns the ddm structures before and after the current ddm structure in the ordered set where parentStructureId = &#63;.
	 *
	 * @param structureId the primary key of the current ddm structure
	 * @param parentStructureId the parent structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm structure
	 * @throws NoSuchStructureException if a ddm structure with the primary key could not be found
	 */
	public DDMStructure[] findByParentStructureId_PrevAndNext(
			long structureId, long parentStructureId,
			com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
				orderByComparator)
		throws NoSuchStructureException;

	/**
	 * Removes all the ddm structures where parentStructureId = &#63; from the database.
	 *
	 * @param parentStructureId the parent structure ID
	 */
	public void removeByParentStructureId(long parentStructureId);

	/**
	 * Returns the number of ddm structures where parentStructureId = &#63;.
	 *
	 * @param parentStructureId the parent structure ID
	 * @return the number of matching ddm structures
	 */
	public int countByParentStructureId(long parentStructureId);

	/**
	 * Returns all the ddm structures where structureKey = &#63;.
	 *
	 * @param structureKey the structure key
	 * @return the matching ddm structures
	 */
	public java.util.List<DDMStructure> findByStructureKey(String structureKey);

	/**
	 * Returns a range of all the ddm structures where structureKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param structureKey the structure key
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of matching ddm structures
	 */
	public java.util.List<DDMStructure> findByStructureKey(
		String structureKey, int start, int end);

	/**
	 * Returns an ordered range of all the ddm structures where structureKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param structureKey the structure key
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures
	 */
	public java.util.List<DDMStructure> findByStructureKey(
		String structureKey, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator);

	/**
	 * Returns an ordered range of all the ddm structures where structureKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param structureKey the structure key
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm structures
	 */
	public java.util.List<DDMStructure> findByStructureKey(
		String structureKey, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first ddm structure in the ordered set where structureKey = &#63;.
	 *
	 * @param structureKey the structure key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	public DDMStructure findByStructureKey_First(
			String structureKey,
			com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
				orderByComparator)
		throws NoSuchStructureException;

	/**
	 * Returns the first ddm structure in the ordered set where structureKey = &#63;.
	 *
	 * @param structureKey the structure key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	public DDMStructure fetchByStructureKey_First(
		String structureKey,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator);

	/**
	 * Returns the last ddm structure in the ordered set where structureKey = &#63;.
	 *
	 * @param structureKey the structure key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	public DDMStructure findByStructureKey_Last(
			String structureKey,
			com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
				orderByComparator)
		throws NoSuchStructureException;

	/**
	 * Returns the last ddm structure in the ordered set where structureKey = &#63;.
	 *
	 * @param structureKey the structure key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	public DDMStructure fetchByStructureKey_Last(
		String structureKey,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator);

	/**
	 * Returns the ddm structures before and after the current ddm structure in the ordered set where structureKey = &#63;.
	 *
	 * @param structureId the primary key of the current ddm structure
	 * @param structureKey the structure key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm structure
	 * @throws NoSuchStructureException if a ddm structure with the primary key could not be found
	 */
	public DDMStructure[] findByStructureKey_PrevAndNext(
			long structureId, String structureKey,
			com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
				orderByComparator)
		throws NoSuchStructureException;

	/**
	 * Removes all the ddm structures where structureKey = &#63; from the database.
	 *
	 * @param structureKey the structure key
	 */
	public void removeByStructureKey(String structureKey);

	/**
	 * Returns the number of ddm structures where structureKey = &#63;.
	 *
	 * @param structureKey the structure key
	 * @return the number of matching ddm structures
	 */
	public int countByStructureKey(String structureKey);

	/**
	 * Returns all the ddm structures where groupId = &#63; and parentStructureId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentStructureId the parent structure ID
	 * @return the matching ddm structures
	 */
	public java.util.List<DDMStructure> findByG_P(
		long groupId, long parentStructureId);

	/**
	 * Returns a range of all the ddm structures where groupId = &#63; and parentStructureId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentStructureId the parent structure ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of matching ddm structures
	 */
	public java.util.List<DDMStructure> findByG_P(
		long groupId, long parentStructureId, int start, int end);

	/**
	 * Returns an ordered range of all the ddm structures where groupId = &#63; and parentStructureId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentStructureId the parent structure ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures
	 */
	public java.util.List<DDMStructure> findByG_P(
		long groupId, long parentStructureId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator);

	/**
	 * Returns an ordered range of all the ddm structures where groupId = &#63; and parentStructureId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentStructureId the parent structure ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm structures
	 */
	public java.util.List<DDMStructure> findByG_P(
		long groupId, long parentStructureId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first ddm structure in the ordered set where groupId = &#63; and parentStructureId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentStructureId the parent structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	public DDMStructure findByG_P_First(
			long groupId, long parentStructureId,
			com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
				orderByComparator)
		throws NoSuchStructureException;

	/**
	 * Returns the first ddm structure in the ordered set where groupId = &#63; and parentStructureId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentStructureId the parent structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	public DDMStructure fetchByG_P_First(
		long groupId, long parentStructureId,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator);

	/**
	 * Returns the last ddm structure in the ordered set where groupId = &#63; and parentStructureId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentStructureId the parent structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	public DDMStructure findByG_P_Last(
			long groupId, long parentStructureId,
			com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
				orderByComparator)
		throws NoSuchStructureException;

	/**
	 * Returns the last ddm structure in the ordered set where groupId = &#63; and parentStructureId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentStructureId the parent structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	public DDMStructure fetchByG_P_Last(
		long groupId, long parentStructureId,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator);

	/**
	 * Returns the ddm structures before and after the current ddm structure in the ordered set where groupId = &#63; and parentStructureId = &#63;.
	 *
	 * @param structureId the primary key of the current ddm structure
	 * @param groupId the group ID
	 * @param parentStructureId the parent structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm structure
	 * @throws NoSuchStructureException if a ddm structure with the primary key could not be found
	 */
	public DDMStructure[] findByG_P_PrevAndNext(
			long structureId, long groupId, long parentStructureId,
			com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
				orderByComparator)
		throws NoSuchStructureException;

	/**
	 * Returns all the ddm structures that the user has permission to view where groupId = &#63; and parentStructureId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentStructureId the parent structure ID
	 * @return the matching ddm structures that the user has permission to view
	 */
	public java.util.List<DDMStructure> filterFindByG_P(
		long groupId, long parentStructureId);

	/**
	 * Returns a range of all the ddm structures that the user has permission to view where groupId = &#63; and parentStructureId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentStructureId the parent structure ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of matching ddm structures that the user has permission to view
	 */
	public java.util.List<DDMStructure> filterFindByG_P(
		long groupId, long parentStructureId, int start, int end);

	/**
	 * Returns an ordered range of all the ddm structures that the user has permissions to view where groupId = &#63; and parentStructureId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentStructureId the parent structure ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures that the user has permission to view
	 */
	public java.util.List<DDMStructure> filterFindByG_P(
		long groupId, long parentStructureId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator);

	/**
	 * Returns the ddm structures before and after the current ddm structure in the ordered set of ddm structures that the user has permission to view where groupId = &#63; and parentStructureId = &#63;.
	 *
	 * @param structureId the primary key of the current ddm structure
	 * @param groupId the group ID
	 * @param parentStructureId the parent structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm structure
	 * @throws NoSuchStructureException if a ddm structure with the primary key could not be found
	 */
	public DDMStructure[] filterFindByG_P_PrevAndNext(
			long structureId, long groupId, long parentStructureId,
			com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
				orderByComparator)
		throws NoSuchStructureException;

	/**
	 * Removes all the ddm structures where groupId = &#63; and parentStructureId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentStructureId the parent structure ID
	 */
	public void removeByG_P(long groupId, long parentStructureId);

	/**
	 * Returns the number of ddm structures where groupId = &#63; and parentStructureId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentStructureId the parent structure ID
	 * @return the number of matching ddm structures
	 */
	public int countByG_P(long groupId, long parentStructureId);

	/**
	 * Returns the number of ddm structures that the user has permission to view where groupId = &#63; and parentStructureId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentStructureId the parent structure ID
	 * @return the number of matching ddm structures that the user has permission to view
	 */
	public int filterCountByG_P(long groupId, long parentStructureId);

	/**
	 * Returns all the ddm structures where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @return the matching ddm structures
	 */
	public java.util.List<DDMStructure> findByG_C(
		long groupId, long classNameId);

	/**
	 * Returns a range of all the ddm structures where groupId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of matching ddm structures
	 */
	public java.util.List<DDMStructure> findByG_C(
		long groupId, long classNameId, int start, int end);

	/**
	 * Returns an ordered range of all the ddm structures where groupId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures
	 */
	public java.util.List<DDMStructure> findByG_C(
		long groupId, long classNameId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator);

	/**
	 * Returns an ordered range of all the ddm structures where groupId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm structures
	 */
	public java.util.List<DDMStructure> findByG_C(
		long groupId, long classNameId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first ddm structure in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	public DDMStructure findByG_C_First(
			long groupId, long classNameId,
			com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
				orderByComparator)
		throws NoSuchStructureException;

	/**
	 * Returns the first ddm structure in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	public DDMStructure fetchByG_C_First(
		long groupId, long classNameId,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator);

	/**
	 * Returns the last ddm structure in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	public DDMStructure findByG_C_Last(
			long groupId, long classNameId,
			com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
				orderByComparator)
		throws NoSuchStructureException;

	/**
	 * Returns the last ddm structure in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	public DDMStructure fetchByG_C_Last(
		long groupId, long classNameId,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator);

	/**
	 * Returns the ddm structures before and after the current ddm structure in the ordered set where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param structureId the primary key of the current ddm structure
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm structure
	 * @throws NoSuchStructureException if a ddm structure with the primary key could not be found
	 */
	public DDMStructure[] findByG_C_PrevAndNext(
			long structureId, long groupId, long classNameId,
			com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
				orderByComparator)
		throws NoSuchStructureException;

	/**
	 * Returns all the ddm structures that the user has permission to view where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @return the matching ddm structures that the user has permission to view
	 */
	public java.util.List<DDMStructure> filterFindByG_C(
		long groupId, long classNameId);

	/**
	 * Returns a range of all the ddm structures that the user has permission to view where groupId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of matching ddm structures that the user has permission to view
	 */
	public java.util.List<DDMStructure> filterFindByG_C(
		long groupId, long classNameId, int start, int end);

	/**
	 * Returns an ordered range of all the ddm structures that the user has permissions to view where groupId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures that the user has permission to view
	 */
	public java.util.List<DDMStructure> filterFindByG_C(
		long groupId, long classNameId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator);

	/**
	 * Returns the ddm structures before and after the current ddm structure in the ordered set of ddm structures that the user has permission to view where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param structureId the primary key of the current ddm structure
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm structure
	 * @throws NoSuchStructureException if a ddm structure with the primary key could not be found
	 */
	public DDMStructure[] filterFindByG_C_PrevAndNext(
			long structureId, long groupId, long classNameId,
			com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
				orderByComparator)
		throws NoSuchStructureException;

	/**
	 * Returns all the ddm structures that the user has permission to view where groupId = any &#63; and classNameId = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @return the matching ddm structures that the user has permission to view
	 */
	public java.util.List<DDMStructure> filterFindByG_C(
		long[] groupIds, long classNameId);

	/**
	 * Returns a range of all the ddm structures that the user has permission to view where groupId = any &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of matching ddm structures that the user has permission to view
	 */
	public java.util.List<DDMStructure> filterFindByG_C(
		long[] groupIds, long classNameId, int start, int end);

	/**
	 * Returns an ordered range of all the ddm structures that the user has permission to view where groupId = any &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures that the user has permission to view
	 */
	public java.util.List<DDMStructure> filterFindByG_C(
		long[] groupIds, long classNameId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator);

	/**
	 * Returns all the ddm structures where groupId = any &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @return the matching ddm structures
	 */
	public java.util.List<DDMStructure> findByG_C(
		long[] groupIds, long classNameId);

	/**
	 * Returns a range of all the ddm structures where groupId = any &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of matching ddm structures
	 */
	public java.util.List<DDMStructure> findByG_C(
		long[] groupIds, long classNameId, int start, int end);

	/**
	 * Returns an ordered range of all the ddm structures where groupId = any &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures
	 */
	public java.util.List<DDMStructure> findByG_C(
		long[] groupIds, long classNameId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator);

	/**
	 * Returns an ordered range of all the ddm structures where groupId = &#63; and classNameId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm structures
	 */
	public java.util.List<DDMStructure> findByG_C(
		long[] groupIds, long classNameId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the ddm structures where groupId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 */
	public void removeByG_C(long groupId, long classNameId);

	/**
	 * Returns the number of ddm structures where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @return the number of matching ddm structures
	 */
	public int countByG_C(long groupId, long classNameId);

	/**
	 * Returns the number of ddm structures where groupId = any &#63; and classNameId = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @return the number of matching ddm structures
	 */
	public int countByG_C(long[] groupIds, long classNameId);

	/**
	 * Returns the number of ddm structures that the user has permission to view where groupId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @return the number of matching ddm structures that the user has permission to view
	 */
	public int filterCountByG_C(long groupId, long classNameId);

	/**
	 * Returns the number of ddm structures that the user has permission to view where groupId = any &#63; and classNameId = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @return the number of matching ddm structures that the user has permission to view
	 */
	public int filterCountByG_C(long[] groupIds, long classNameId);

	/**
	 * Returns all the ddm structures where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the matching ddm structures
	 */
	public java.util.List<DDMStructure> findByC_C(
		long companyId, long classNameId);

	/**
	 * Returns a range of all the ddm structures where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of matching ddm structures
	 */
	public java.util.List<DDMStructure> findByC_C(
		long companyId, long classNameId, int start, int end);

	/**
	 * Returns an ordered range of all the ddm structures where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures
	 */
	public java.util.List<DDMStructure> findByC_C(
		long companyId, long classNameId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator);

	/**
	 * Returns an ordered range of all the ddm structures where companyId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm structures
	 */
	public java.util.List<DDMStructure> findByC_C(
		long companyId, long classNameId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first ddm structure in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	public DDMStructure findByC_C_First(
			long companyId, long classNameId,
			com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
				orderByComparator)
		throws NoSuchStructureException;

	/**
	 * Returns the first ddm structure in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	public DDMStructure fetchByC_C_First(
		long companyId, long classNameId,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator);

	/**
	 * Returns the last ddm structure in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	public DDMStructure findByC_C_Last(
			long companyId, long classNameId,
			com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
				orderByComparator)
		throws NoSuchStructureException;

	/**
	 * Returns the last ddm structure in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	public DDMStructure fetchByC_C_Last(
		long companyId, long classNameId,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator);

	/**
	 * Returns the ddm structures before and after the current ddm structure in the ordered set where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param structureId the primary key of the current ddm structure
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm structure
	 * @throws NoSuchStructureException if a ddm structure with the primary key could not be found
	 */
	public DDMStructure[] findByC_C_PrevAndNext(
			long structureId, long companyId, long classNameId,
			com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
				orderByComparator)
		throws NoSuchStructureException;

	/**
	 * Removes all the ddm structures where companyId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 */
	public void removeByC_C(long companyId, long classNameId);

	/**
	 * Returns the number of ddm structures where companyId = &#63; and classNameId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @return the number of matching ddm structures
	 */
	public int countByC_C(long companyId, long classNameId);

	/**
	 * Returns the ddm structure where externalReferenceCode = &#63; and groupId = &#63; and classNameId = &#63; or throws a <code>NoSuchStructureException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @return the matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	public DDMStructure findByERC_G_C(
			String externalReferenceCode, long groupId, long classNameId)
		throws NoSuchStructureException;

	/**
	 * Returns the ddm structure where externalReferenceCode = &#63; and groupId = &#63; and classNameId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @return the matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	public DDMStructure fetchByERC_G_C(
		String externalReferenceCode, long groupId, long classNameId);

	/**
	 * Returns the ddm structure where externalReferenceCode = &#63; and groupId = &#63; and classNameId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	public DDMStructure fetchByERC_G_C(
		String externalReferenceCode, long groupId, long classNameId,
		boolean useFinderCache);

	/**
	 * Removes the ddm structure where externalReferenceCode = &#63; and groupId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @return the ddm structure that was removed
	 */
	public DDMStructure removeByERC_G_C(
			String externalReferenceCode, long groupId, long classNameId)
		throws NoSuchStructureException;

	/**
	 * Returns the number of ddm structures where externalReferenceCode = &#63; and groupId = &#63; and classNameId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @return the number of matching ddm structures
	 */
	public int countByERC_G_C(
		String externalReferenceCode, long groupId, long classNameId);

	/**
	 * Returns the ddm structure where groupId = &#63; and classNameId = &#63; and structureKey = &#63; or throws a <code>NoSuchStructureException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param structureKey the structure key
	 * @return the matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	public DDMStructure findByG_C_S(
			long groupId, long classNameId, String structureKey)
		throws NoSuchStructureException;

	/**
	 * Returns the ddm structure where groupId = &#63; and classNameId = &#63; and structureKey = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param structureKey the structure key
	 * @return the matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	public DDMStructure fetchByG_C_S(
		long groupId, long classNameId, String structureKey);

	/**
	 * Returns the ddm structure where groupId = &#63; and classNameId = &#63; and structureKey = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param structureKey the structure key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	public DDMStructure fetchByG_C_S(
		long groupId, long classNameId, String structureKey,
		boolean useFinderCache);

	/**
	 * Removes the ddm structure where groupId = &#63; and classNameId = &#63; and structureKey = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param structureKey the structure key
	 * @return the ddm structure that was removed
	 */
	public DDMStructure removeByG_C_S(
			long groupId, long classNameId, String structureKey)
		throws NoSuchStructureException;

	/**
	 * Returns the number of ddm structures where groupId = &#63; and classNameId = &#63; and structureKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param structureKey the structure key
	 * @return the number of matching ddm structures
	 */
	public int countByG_C_S(
		long groupId, long classNameId, String structureKey);

	/**
	 * Returns all the ddm structures where groupId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param description the description
	 * @return the matching ddm structures
	 */
	public java.util.List<DDMStructure> findByG_N_D(
		long groupId, String name, String description);

	/**
	 * Returns a range of all the ddm structures where groupId = &#63; and name = &#63; and description = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param description the description
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of matching ddm structures
	 */
	public java.util.List<DDMStructure> findByG_N_D(
		long groupId, String name, String description, int start, int end);

	/**
	 * Returns an ordered range of all the ddm structures where groupId = &#63; and name = &#63; and description = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param description the description
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures
	 */
	public java.util.List<DDMStructure> findByG_N_D(
		long groupId, String name, String description, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator);

	/**
	 * Returns an ordered range of all the ddm structures where groupId = &#63; and name = &#63; and description = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param description the description
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm structures
	 */
	public java.util.List<DDMStructure> findByG_N_D(
		long groupId, String name, String description, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first ddm structure in the ordered set where groupId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param description the description
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	public DDMStructure findByG_N_D_First(
			long groupId, String name, String description,
			com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
				orderByComparator)
		throws NoSuchStructureException;

	/**
	 * Returns the first ddm structure in the ordered set where groupId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param description the description
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	public DDMStructure fetchByG_N_D_First(
		long groupId, String name, String description,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator);

	/**
	 * Returns the last ddm structure in the ordered set where groupId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param description the description
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	public DDMStructure findByG_N_D_Last(
			long groupId, String name, String description,
			com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
				orderByComparator)
		throws NoSuchStructureException;

	/**
	 * Returns the last ddm structure in the ordered set where groupId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param description the description
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	public DDMStructure fetchByG_N_D_Last(
		long groupId, String name, String description,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator);

	/**
	 * Returns the ddm structures before and after the current ddm structure in the ordered set where groupId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param structureId the primary key of the current ddm structure
	 * @param groupId the group ID
	 * @param name the name
	 * @param description the description
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm structure
	 * @throws NoSuchStructureException if a ddm structure with the primary key could not be found
	 */
	public DDMStructure[] findByG_N_D_PrevAndNext(
			long structureId, long groupId, String name, String description,
			com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
				orderByComparator)
		throws NoSuchStructureException;

	/**
	 * Returns all the ddm structures that the user has permission to view where groupId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param description the description
	 * @return the matching ddm structures that the user has permission to view
	 */
	public java.util.List<DDMStructure> filterFindByG_N_D(
		long groupId, String name, String description);

	/**
	 * Returns a range of all the ddm structures that the user has permission to view where groupId = &#63; and name = &#63; and description = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param description the description
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of matching ddm structures that the user has permission to view
	 */
	public java.util.List<DDMStructure> filterFindByG_N_D(
		long groupId, String name, String description, int start, int end);

	/**
	 * Returns an ordered range of all the ddm structures that the user has permissions to view where groupId = &#63; and name = &#63; and description = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param description the description
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures that the user has permission to view
	 */
	public java.util.List<DDMStructure> filterFindByG_N_D(
		long groupId, String name, String description, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator);

	/**
	 * Returns the ddm structures before and after the current ddm structure in the ordered set of ddm structures that the user has permission to view where groupId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param structureId the primary key of the current ddm structure
	 * @param groupId the group ID
	 * @param name the name
	 * @param description the description
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm structure
	 * @throws NoSuchStructureException if a ddm structure with the primary key could not be found
	 */
	public DDMStructure[] filterFindByG_N_D_PrevAndNext(
			long structureId, long groupId, String name, String description,
			com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
				orderByComparator)
		throws NoSuchStructureException;

	/**
	 * Removes all the ddm structures where groupId = &#63; and name = &#63; and description = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param description the description
	 */
	public void removeByG_N_D(long groupId, String name, String description);

	/**
	 * Returns the number of ddm structures where groupId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param description the description
	 * @return the number of matching ddm structures
	 */
	public int countByG_N_D(long groupId, String name, String description);

	/**
	 * Returns the number of ddm structures that the user has permission to view where groupId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param description the description
	 * @return the number of matching ddm structures that the user has permission to view
	 */
	public int filterCountByG_N_D(
		long groupId, String name, String description);

	/**
	 * Returns all the ddm structures where groupId = &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @return the matching ddm structures
	 */
	public java.util.List<DDMStructure> findByG_C_N_D(
		long groupId, long classNameId, String name, String description);

	/**
	 * Returns a range of all the ddm structures where groupId = &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of matching ddm structures
	 */
	public java.util.List<DDMStructure> findByG_C_N_D(
		long groupId, long classNameId, String name, String description,
		int start, int end);

	/**
	 * Returns an ordered range of all the ddm structures where groupId = &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures
	 */
	public java.util.List<DDMStructure> findByG_C_N_D(
		long groupId, long classNameId, String name, String description,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator);

	/**
	 * Returns an ordered range of all the ddm structures where groupId = &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm structures
	 */
	public java.util.List<DDMStructure> findByG_C_N_D(
		long groupId, long classNameId, String name, String description,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first ddm structure in the ordered set where groupId = &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	public DDMStructure findByG_C_N_D_First(
			long groupId, long classNameId, String name, String description,
			com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
				orderByComparator)
		throws NoSuchStructureException;

	/**
	 * Returns the first ddm structure in the ordered set where groupId = &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	public DDMStructure fetchByG_C_N_D_First(
		long groupId, long classNameId, String name, String description,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator);

	/**
	 * Returns the last ddm structure in the ordered set where groupId = &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm structure
	 * @throws NoSuchStructureException if a matching ddm structure could not be found
	 */
	public DDMStructure findByG_C_N_D_Last(
			long groupId, long classNameId, String name, String description,
			com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
				orderByComparator)
		throws NoSuchStructureException;

	/**
	 * Returns the last ddm structure in the ordered set where groupId = &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching ddm structure, or <code>null</code> if a matching ddm structure could not be found
	 */
	public DDMStructure fetchByG_C_N_D_Last(
		long groupId, long classNameId, String name, String description,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator);

	/**
	 * Returns the ddm structures before and after the current ddm structure in the ordered set where groupId = &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param structureId the primary key of the current ddm structure
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm structure
	 * @throws NoSuchStructureException if a ddm structure with the primary key could not be found
	 */
	public DDMStructure[] findByG_C_N_D_PrevAndNext(
			long structureId, long groupId, long classNameId, String name,
			String description,
			com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
				orderByComparator)
		throws NoSuchStructureException;

	/**
	 * Returns all the ddm structures that the user has permission to view where groupId = &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @return the matching ddm structures that the user has permission to view
	 */
	public java.util.List<DDMStructure> filterFindByG_C_N_D(
		long groupId, long classNameId, String name, String description);

	/**
	 * Returns a range of all the ddm structures that the user has permission to view where groupId = &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of matching ddm structures that the user has permission to view
	 */
	public java.util.List<DDMStructure> filterFindByG_C_N_D(
		long groupId, long classNameId, String name, String description,
		int start, int end);

	/**
	 * Returns an ordered range of all the ddm structures that the user has permissions to view where groupId = &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures that the user has permission to view
	 */
	public java.util.List<DDMStructure> filterFindByG_C_N_D(
		long groupId, long classNameId, String name, String description,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator);

	/**
	 * Returns the ddm structures before and after the current ddm structure in the ordered set of ddm structures that the user has permission to view where groupId = &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param structureId the primary key of the current ddm structure
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next ddm structure
	 * @throws NoSuchStructureException if a ddm structure with the primary key could not be found
	 */
	public DDMStructure[] filterFindByG_C_N_D_PrevAndNext(
			long structureId, long groupId, long classNameId, String name,
			String description,
			com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
				orderByComparator)
		throws NoSuchStructureException;

	/**
	 * Returns all the ddm structures that the user has permission to view where groupId = any &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @return the matching ddm structures that the user has permission to view
	 */
	public java.util.List<DDMStructure> filterFindByG_C_N_D(
		long[] groupIds, long classNameId, String name, String description);

	/**
	 * Returns a range of all the ddm structures that the user has permission to view where groupId = any &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of matching ddm structures that the user has permission to view
	 */
	public java.util.List<DDMStructure> filterFindByG_C_N_D(
		long[] groupIds, long classNameId, String name, String description,
		int start, int end);

	/**
	 * Returns an ordered range of all the ddm structures that the user has permission to view where groupId = any &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures that the user has permission to view
	 */
	public java.util.List<DDMStructure> filterFindByG_C_N_D(
		long[] groupIds, long classNameId, String name, String description,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator);

	/**
	 * Returns all the ddm structures where groupId = any &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @return the matching ddm structures
	 */
	public java.util.List<DDMStructure> findByG_C_N_D(
		long[] groupIds, long classNameId, String name, String description);

	/**
	 * Returns a range of all the ddm structures where groupId = any &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of matching ddm structures
	 */
	public java.util.List<DDMStructure> findByG_C_N_D(
		long[] groupIds, long classNameId, String name, String description,
		int start, int end);

	/**
	 * Returns an ordered range of all the ddm structures where groupId = any &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching ddm structures
	 */
	public java.util.List<DDMStructure> findByG_C_N_D(
		long[] groupIds, long classNameId, String name, String description,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator);

	/**
	 * Returns an ordered range of all the ddm structures where groupId = &#63; and classNameId = &#63; and name = &#63; and description = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching ddm structures
	 */
	public java.util.List<DDMStructure> findByG_C_N_D(
		long[] groupIds, long classNameId, String name, String description,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the ddm structures where groupId = &#63; and classNameId = &#63; and name = &#63; and description = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 */
	public void removeByG_C_N_D(
		long groupId, long classNameId, String name, String description);

	/**
	 * Returns the number of ddm structures where groupId = &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @return the number of matching ddm structures
	 */
	public int countByG_C_N_D(
		long groupId, long classNameId, String name, String description);

	/**
	 * Returns the number of ddm structures where groupId = any &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @return the number of matching ddm structures
	 */
	public int countByG_C_N_D(
		long[] groupIds, long classNameId, String name, String description);

	/**
	 * Returns the number of ddm structures that the user has permission to view where groupId = &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @return the number of matching ddm structures that the user has permission to view
	 */
	public int filterCountByG_C_N_D(
		long groupId, long classNameId, String name, String description);

	/**
	 * Returns the number of ddm structures that the user has permission to view where groupId = any &#63; and classNameId = &#63; and name = &#63; and description = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param classNameId the class name ID
	 * @param name the name
	 * @param description the description
	 * @return the number of matching ddm structures that the user has permission to view
	 */
	public int filterCountByG_C_N_D(
		long[] groupIds, long classNameId, String name, String description);

	/**
	 * Caches the ddm structure in the entity cache if it is enabled.
	 *
	 * @param ddmStructure the ddm structure
	 */
	public void cacheResult(DDMStructure ddmStructure);

	/**
	 * Caches the ddm structures in the entity cache if it is enabled.
	 *
	 * @param ddmStructures the ddm structures
	 */
	public void cacheResult(java.util.List<DDMStructure> ddmStructures);

	/**
	 * Creates a new ddm structure with the primary key. Does not add the ddm structure to the database.
	 *
	 * @param structureId the primary key for the new ddm structure
	 * @return the new ddm structure
	 */
	public DDMStructure create(long structureId);

	/**
	 * Removes the ddm structure with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param structureId the primary key of the ddm structure
	 * @return the ddm structure that was removed
	 * @throws NoSuchStructureException if a ddm structure with the primary key could not be found
	 */
	public DDMStructure remove(long structureId)
		throws NoSuchStructureException;

	public DDMStructure updateImpl(DDMStructure ddmStructure);

	/**
	 * Returns the ddm structure with the primary key or throws a <code>NoSuchStructureException</code> if it could not be found.
	 *
	 * @param structureId the primary key of the ddm structure
	 * @return the ddm structure
	 * @throws NoSuchStructureException if a ddm structure with the primary key could not be found
	 */
	public DDMStructure findByPrimaryKey(long structureId)
		throws NoSuchStructureException;

	/**
	 * Returns the ddm structure with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param structureId the primary key of the ddm structure
	 * @return the ddm structure, or <code>null</code> if a ddm structure with the primary key could not be found
	 */
	public DDMStructure fetchByPrimaryKey(long structureId);

	/**
	 * Returns all the ddm structures.
	 *
	 * @return the ddm structures
	 */
	public java.util.List<DDMStructure> findAll();

	/**
	 * Returns a range of all the ddm structures.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @return the range of ddm structures
	 */
	public java.util.List<DDMStructure> findAll(int start, int end);

	/**
	 * Returns an ordered range of all the ddm structures.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of ddm structures
	 */
	public java.util.List<DDMStructure> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator);

	/**
	 * Returns an ordered range of all the ddm structures.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMStructureModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ddm structures
	 * @param end the upper bound of the range of ddm structures (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of ddm structures
	 */
	public java.util.List<DDMStructure> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMStructure>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the ddm structures from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of ddm structures.
	 *
	 * @return the number of ddm structures
	 */
	public int countAll();

}