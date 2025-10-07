/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence;

import com.liferay.object.exception.NoSuchObjectEntryException;
import com.liferay.object.model.ObjectEntry;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

import java.util.Date;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the object entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @see ObjectEntryUtil
 * @generated
 */
@ProviderType
public interface ObjectEntryPersistence extends BasePersistence<ObjectEntry> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link ObjectEntryUtil} to access the object entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns all the object entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching object entries
	 */
	public java.util.List<ObjectEntry> findByUuid(String uuid);

	/**
	 * Returns a range of all the object entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @return the range of matching object entries
	 */
	public java.util.List<ObjectEntry> findByUuid(
		String uuid, int start, int end);

	/**
	 * Returns an ordered range of all the object entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entries
	 */
	public java.util.List<ObjectEntry> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entries
	 */
	public java.util.List<ObjectEntry> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	public ObjectEntry findByUuid_First(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
				orderByComparator)
		throws NoSuchObjectEntryException;

	/**
	 * Returns the first object entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	public ObjectEntry fetchByUuid_First(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
			orderByComparator);

	/**
	 * Returns the last object entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	public ObjectEntry findByUuid_Last(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
				orderByComparator)
		throws NoSuchObjectEntryException;

	/**
	 * Returns the last object entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	public ObjectEntry fetchByUuid_Last(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
			orderByComparator);

	/**
	 * Returns the object entries before and after the current object entry in the ordered set where uuid = &#63;.
	 *
	 * @param objectEntryId the primary key of the current object entry
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object entry
	 * @throws NoSuchObjectEntryException if a object entry with the primary key could not be found
	 */
	public ObjectEntry[] findByUuid_PrevAndNext(
			long objectEntryId, String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
				orderByComparator)
		throws NoSuchObjectEntryException;

	/**
	 * Removes all the object entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public void removeByUuid(String uuid);

	/**
	 * Returns the number of object entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching object entries
	 */
	public int countByUuid(String uuid);

	/**
	 * Returns the object entry where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchObjectEntryException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	public ObjectEntry findByUUID_G(String uuid, long groupId)
		throws NoSuchObjectEntryException;

	/**
	 * Returns the object entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	public ObjectEntry fetchByUUID_G(String uuid, long groupId);

	/**
	 * Returns the object entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	public ObjectEntry fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache);

	/**
	 * Removes the object entry where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the object entry that was removed
	 */
	public ObjectEntry removeByUUID_G(String uuid, long groupId)
		throws NoSuchObjectEntryException;

	/**
	 * Returns the number of object entries where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching object entries
	 */
	public int countByUUID_G(String uuid, long groupId);

	/**
	 * Returns all the object entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching object entries
	 */
	public java.util.List<ObjectEntry> findByUuid_C(
		String uuid, long companyId);

	/**
	 * Returns a range of all the object entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @return the range of matching object entries
	 */
	public java.util.List<ObjectEntry> findByUuid_C(
		String uuid, long companyId, int start, int end);

	/**
	 * Returns an ordered range of all the object entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entries
	 */
	public java.util.List<ObjectEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entries
	 */
	public java.util.List<ObjectEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	public ObjectEntry findByUuid_C_First(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
				orderByComparator)
		throws NoSuchObjectEntryException;

	/**
	 * Returns the first object entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	public ObjectEntry fetchByUuid_C_First(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
			orderByComparator);

	/**
	 * Returns the last object entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	public ObjectEntry findByUuid_C_Last(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
				orderByComparator)
		throws NoSuchObjectEntryException;

	/**
	 * Returns the last object entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	public ObjectEntry fetchByUuid_C_Last(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
			orderByComparator);

	/**
	 * Returns the object entries before and after the current object entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param objectEntryId the primary key of the current object entry
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object entry
	 * @throws NoSuchObjectEntryException if a object entry with the primary key could not be found
	 */
	public ObjectEntry[] findByUuid_C_PrevAndNext(
			long objectEntryId, String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
				orderByComparator)
		throws NoSuchObjectEntryException;

	/**
	 * Removes all the object entries where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public void removeByUuid_C(String uuid, long companyId);

	/**
	 * Returns the number of object entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching object entries
	 */
	public int countByUuid_C(String uuid, long companyId);

	/**
	 * Returns the object entry where headObjectEntryId = &#63; or throws a <code>NoSuchObjectEntryException</code> if it could not be found.
	 *
	 * @param headObjectEntryId the head object entry ID
	 * @return the matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	public ObjectEntry findByHeadObjectEntryId(long headObjectEntryId)
		throws NoSuchObjectEntryException;

	/**
	 * Returns the object entry where headObjectEntryId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param headObjectEntryId the head object entry ID
	 * @return the matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	public ObjectEntry fetchByHeadObjectEntryId(long headObjectEntryId);

	/**
	 * Returns the object entry where headObjectEntryId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param headObjectEntryId the head object entry ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	public ObjectEntry fetchByHeadObjectEntryId(
		long headObjectEntryId, boolean useFinderCache);

	/**
	 * Removes the object entry where headObjectEntryId = &#63; from the database.
	 *
	 * @param headObjectEntryId the head object entry ID
	 * @return the object entry that was removed
	 */
	public ObjectEntry removeByHeadObjectEntryId(long headObjectEntryId)
		throws NoSuchObjectEntryException;

	/**
	 * Returns the number of object entries where headObjectEntryId = &#63;.
	 *
	 * @param headObjectEntryId the head object entry ID
	 * @return the number of matching object entries
	 */
	public int countByHeadObjectEntryId(long headObjectEntryId);

	/**
	 * Returns all the object entries where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @return the matching object entries
	 */
	public java.util.List<ObjectEntry> findByObjectDefinitionId(
		long objectDefinitionId);

	/**
	 * Returns a range of all the object entries where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @return the range of matching object entries
	 */
	public java.util.List<ObjectEntry> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end);

	/**
	 * Returns an ordered range of all the object entries where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entries
	 */
	public java.util.List<ObjectEntry> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object entries where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entries
	 */
	public java.util.List<ObjectEntry> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object entry in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	public ObjectEntry findByObjectDefinitionId_First(
			long objectDefinitionId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
				orderByComparator)
		throws NoSuchObjectEntryException;

	/**
	 * Returns the first object entry in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	public ObjectEntry fetchByObjectDefinitionId_First(
		long objectDefinitionId,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
			orderByComparator);

	/**
	 * Returns the last object entry in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	public ObjectEntry findByObjectDefinitionId_Last(
			long objectDefinitionId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
				orderByComparator)
		throws NoSuchObjectEntryException;

	/**
	 * Returns the last object entry in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	public ObjectEntry fetchByObjectDefinitionId_Last(
		long objectDefinitionId,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
			orderByComparator);

	/**
	 * Returns the object entries before and after the current object entry in the ordered set where objectDefinitionId = &#63;.
	 *
	 * @param objectEntryId the primary key of the current object entry
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object entry
	 * @throws NoSuchObjectEntryException if a object entry with the primary key could not be found
	 */
	public ObjectEntry[] findByObjectDefinitionId_PrevAndNext(
			long objectEntryId, long objectDefinitionId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
				orderByComparator)
		throws NoSuchObjectEntryException;

	/**
	 * Removes all the object entries where objectDefinitionId = &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 */
	public void removeByObjectDefinitionId(long objectDefinitionId);

	/**
	 * Returns the number of object entries where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @return the number of matching object entries
	 */
	public int countByObjectDefinitionId(long objectDefinitionId);

	/**
	 * Returns all the object entries where groupId = &#63; and objectDefinitionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @return the matching object entries
	 */
	public java.util.List<ObjectEntry> findByG_ODI(
		long groupId, long objectDefinitionId);

	/**
	 * Returns a range of all the object entries where groupId = &#63; and objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @return the range of matching object entries
	 */
	public java.util.List<ObjectEntry> findByG_ODI(
		long groupId, long objectDefinitionId, int start, int end);

	/**
	 * Returns an ordered range of all the object entries where groupId = &#63; and objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entries
	 */
	public java.util.List<ObjectEntry> findByG_ODI(
		long groupId, long objectDefinitionId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object entries where groupId = &#63; and objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entries
	 */
	public java.util.List<ObjectEntry> findByG_ODI(
		long groupId, long objectDefinitionId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object entry in the ordered set where groupId = &#63; and objectDefinitionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	public ObjectEntry findByG_ODI_First(
			long groupId, long objectDefinitionId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
				orderByComparator)
		throws NoSuchObjectEntryException;

	/**
	 * Returns the first object entry in the ordered set where groupId = &#63; and objectDefinitionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	public ObjectEntry fetchByG_ODI_First(
		long groupId, long objectDefinitionId,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
			orderByComparator);

	/**
	 * Returns the last object entry in the ordered set where groupId = &#63; and objectDefinitionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	public ObjectEntry findByG_ODI_Last(
			long groupId, long objectDefinitionId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
				orderByComparator)
		throws NoSuchObjectEntryException;

	/**
	 * Returns the last object entry in the ordered set where groupId = &#63; and objectDefinitionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	public ObjectEntry fetchByG_ODI_Last(
		long groupId, long objectDefinitionId,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
			orderByComparator);

	/**
	 * Returns the object entries before and after the current object entry in the ordered set where groupId = &#63; and objectDefinitionId = &#63;.
	 *
	 * @param objectEntryId the primary key of the current object entry
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object entry
	 * @throws NoSuchObjectEntryException if a object entry with the primary key could not be found
	 */
	public ObjectEntry[] findByG_ODI_PrevAndNext(
			long objectEntryId, long groupId, long objectDefinitionId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
				orderByComparator)
		throws NoSuchObjectEntryException;

	/**
	 * Removes all the object entries where groupId = &#63; and objectDefinitionId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 */
	public void removeByG_ODI(long groupId, long objectDefinitionId);

	/**
	 * Returns the number of object entries where groupId = &#63; and objectDefinitionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @return the number of matching object entries
	 */
	public int countByG_ODI(long groupId, long objectDefinitionId);

	/**
	 * Returns all the object entries where groupId = &#63; and objectEntryFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param objectEntryFolderId the object entry folder ID
	 * @return the matching object entries
	 */
	public java.util.List<ObjectEntry> findByG_OEFI(
		long groupId, long objectEntryFolderId);

	/**
	 * Returns a range of all the object entries where groupId = &#63; and objectEntryFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param objectEntryFolderId the object entry folder ID
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @return the range of matching object entries
	 */
	public java.util.List<ObjectEntry> findByG_OEFI(
		long groupId, long objectEntryFolderId, int start, int end);

	/**
	 * Returns an ordered range of all the object entries where groupId = &#63; and objectEntryFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param objectEntryFolderId the object entry folder ID
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entries
	 */
	public java.util.List<ObjectEntry> findByG_OEFI(
		long groupId, long objectEntryFolderId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object entries where groupId = &#63; and objectEntryFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param objectEntryFolderId the object entry folder ID
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entries
	 */
	public java.util.List<ObjectEntry> findByG_OEFI(
		long groupId, long objectEntryFolderId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object entry in the ordered set where groupId = &#63; and objectEntryFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param objectEntryFolderId the object entry folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	public ObjectEntry findByG_OEFI_First(
			long groupId, long objectEntryFolderId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
				orderByComparator)
		throws NoSuchObjectEntryException;

	/**
	 * Returns the first object entry in the ordered set where groupId = &#63; and objectEntryFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param objectEntryFolderId the object entry folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	public ObjectEntry fetchByG_OEFI_First(
		long groupId, long objectEntryFolderId,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
			orderByComparator);

	/**
	 * Returns the last object entry in the ordered set where groupId = &#63; and objectEntryFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param objectEntryFolderId the object entry folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	public ObjectEntry findByG_OEFI_Last(
			long groupId, long objectEntryFolderId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
				orderByComparator)
		throws NoSuchObjectEntryException;

	/**
	 * Returns the last object entry in the ordered set where groupId = &#63; and objectEntryFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param objectEntryFolderId the object entry folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	public ObjectEntry fetchByG_OEFI_Last(
		long groupId, long objectEntryFolderId,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
			orderByComparator);

	/**
	 * Returns the object entries before and after the current object entry in the ordered set where groupId = &#63; and objectEntryFolderId = &#63;.
	 *
	 * @param objectEntryId the primary key of the current object entry
	 * @param groupId the group ID
	 * @param objectEntryFolderId the object entry folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object entry
	 * @throws NoSuchObjectEntryException if a object entry with the primary key could not be found
	 */
	public ObjectEntry[] findByG_OEFI_PrevAndNext(
			long objectEntryId, long groupId, long objectEntryFolderId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
				orderByComparator)
		throws NoSuchObjectEntryException;

	/**
	 * Removes all the object entries where groupId = &#63; and objectEntryFolderId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param objectEntryFolderId the object entry folder ID
	 */
	public void removeByG_OEFI(long groupId, long objectEntryFolderId);

	/**
	 * Returns the number of object entries where groupId = &#63; and objectEntryFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param objectEntryFolderId the object entry folder ID
	 * @return the number of matching object entries
	 */
	public int countByG_OEFI(long groupId, long objectEntryFolderId);

	/**
	 * Returns all the object entries where userId = &#63; and objectDefinitionId = &#63;.
	 *
	 * @param userId the user ID
	 * @param objectDefinitionId the object definition ID
	 * @return the matching object entries
	 */
	public java.util.List<ObjectEntry> findByU_ODI(
		long userId, long objectDefinitionId);

	/**
	 * Returns a range of all the object entries where userId = &#63; and objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @return the range of matching object entries
	 */
	public java.util.List<ObjectEntry> findByU_ODI(
		long userId, long objectDefinitionId, int start, int end);

	/**
	 * Returns an ordered range of all the object entries where userId = &#63; and objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entries
	 */
	public java.util.List<ObjectEntry> findByU_ODI(
		long userId, long objectDefinitionId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object entries where userId = &#63; and objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entries
	 */
	public java.util.List<ObjectEntry> findByU_ODI(
		long userId, long objectDefinitionId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object entry in the ordered set where userId = &#63; and objectDefinitionId = &#63;.
	 *
	 * @param userId the user ID
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	public ObjectEntry findByU_ODI_First(
			long userId, long objectDefinitionId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
				orderByComparator)
		throws NoSuchObjectEntryException;

	/**
	 * Returns the first object entry in the ordered set where userId = &#63; and objectDefinitionId = &#63;.
	 *
	 * @param userId the user ID
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	public ObjectEntry fetchByU_ODI_First(
		long userId, long objectDefinitionId,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
			orderByComparator);

	/**
	 * Returns the last object entry in the ordered set where userId = &#63; and objectDefinitionId = &#63;.
	 *
	 * @param userId the user ID
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	public ObjectEntry findByU_ODI_Last(
			long userId, long objectDefinitionId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
				orderByComparator)
		throws NoSuchObjectEntryException;

	/**
	 * Returns the last object entry in the ordered set where userId = &#63; and objectDefinitionId = &#63;.
	 *
	 * @param userId the user ID
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	public ObjectEntry fetchByU_ODI_Last(
		long userId, long objectDefinitionId,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
			orderByComparator);

	/**
	 * Returns the object entries before and after the current object entry in the ordered set where userId = &#63; and objectDefinitionId = &#63;.
	 *
	 * @param objectEntryId the primary key of the current object entry
	 * @param userId the user ID
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object entry
	 * @throws NoSuchObjectEntryException if a object entry with the primary key could not be found
	 */
	public ObjectEntry[] findByU_ODI_PrevAndNext(
			long objectEntryId, long userId, long objectDefinitionId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
				orderByComparator)
		throws NoSuchObjectEntryException;

	/**
	 * Removes all the object entries where userId = &#63; and objectDefinitionId = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param objectDefinitionId the object definition ID
	 */
	public void removeByU_ODI(long userId, long objectDefinitionId);

	/**
	 * Returns the number of object entries where userId = &#63; and objectDefinitionId = &#63;.
	 *
	 * @param userId the user ID
	 * @param objectDefinitionId the object definition ID
	 * @return the number of matching object entries
	 */
	public int countByU_ODI(long userId, long objectDefinitionId);

	/**
	 * Returns all the object entries where objectDefinitionId = &#63; and status &ne; &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @return the matching object entries
	 */
	public java.util.List<ObjectEntry> findByODI_NotS(
		long objectDefinitionId, int status);

	/**
	 * Returns a range of all the object entries where objectDefinitionId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @return the range of matching object entries
	 */
	public java.util.List<ObjectEntry> findByODI_NotS(
		long objectDefinitionId, int status, int start, int end);

	/**
	 * Returns an ordered range of all the object entries where objectDefinitionId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entries
	 */
	public java.util.List<ObjectEntry> findByODI_NotS(
		long objectDefinitionId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object entries where objectDefinitionId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entries
	 */
	public java.util.List<ObjectEntry> findByODI_NotS(
		long objectDefinitionId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object entry in the ordered set where objectDefinitionId = &#63; and status &ne; &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	public ObjectEntry findByODI_NotS_First(
			long objectDefinitionId, int status,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
				orderByComparator)
		throws NoSuchObjectEntryException;

	/**
	 * Returns the first object entry in the ordered set where objectDefinitionId = &#63; and status &ne; &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	public ObjectEntry fetchByODI_NotS_First(
		long objectDefinitionId, int status,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
			orderByComparator);

	/**
	 * Returns the last object entry in the ordered set where objectDefinitionId = &#63; and status &ne; &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	public ObjectEntry findByODI_NotS_Last(
			long objectDefinitionId, int status,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
				orderByComparator)
		throws NoSuchObjectEntryException;

	/**
	 * Returns the last object entry in the ordered set where objectDefinitionId = &#63; and status &ne; &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	public ObjectEntry fetchByODI_NotS_Last(
		long objectDefinitionId, int status,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
			orderByComparator);

	/**
	 * Returns the object entries before and after the current object entry in the ordered set where objectDefinitionId = &#63; and status &ne; &#63;.
	 *
	 * @param objectEntryId the primary key of the current object entry
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object entry
	 * @throws NoSuchObjectEntryException if a object entry with the primary key could not be found
	 */
	public ObjectEntry[] findByODI_NotS_PrevAndNext(
			long objectEntryId, long objectDefinitionId, int status,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
				orderByComparator)
		throws NoSuchObjectEntryException;

	/**
	 * Removes all the object entries where objectDefinitionId = &#63; and status &ne; &#63; from the database.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 */
	public void removeByODI_NotS(long objectDefinitionId, int status);

	/**
	 * Returns the number of object entries where objectDefinitionId = &#63; and status &ne; &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @return the number of matching object entries
	 */
	public int countByODI_NotS(long objectDefinitionId, int status);

	/**
	 * Returns all the object entries where rootObjectEntryId = &#63; and status &ne; &#63;.
	 *
	 * @param rootObjectEntryId the root object entry ID
	 * @param status the status
	 * @return the matching object entries
	 */
	public java.util.List<ObjectEntry> findByROEI_NotS(
		long rootObjectEntryId, int status);

	/**
	 * Returns a range of all the object entries where rootObjectEntryId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param rootObjectEntryId the root object entry ID
	 * @param status the status
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @return the range of matching object entries
	 */
	public java.util.List<ObjectEntry> findByROEI_NotS(
		long rootObjectEntryId, int status, int start, int end);

	/**
	 * Returns an ordered range of all the object entries where rootObjectEntryId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param rootObjectEntryId the root object entry ID
	 * @param status the status
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entries
	 */
	public java.util.List<ObjectEntry> findByROEI_NotS(
		long rootObjectEntryId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object entries where rootObjectEntryId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param rootObjectEntryId the root object entry ID
	 * @param status the status
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entries
	 */
	public java.util.List<ObjectEntry> findByROEI_NotS(
		long rootObjectEntryId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object entry in the ordered set where rootObjectEntryId = &#63; and status &ne; &#63;.
	 *
	 * @param rootObjectEntryId the root object entry ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	public ObjectEntry findByROEI_NotS_First(
			long rootObjectEntryId, int status,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
				orderByComparator)
		throws NoSuchObjectEntryException;

	/**
	 * Returns the first object entry in the ordered set where rootObjectEntryId = &#63; and status &ne; &#63;.
	 *
	 * @param rootObjectEntryId the root object entry ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	public ObjectEntry fetchByROEI_NotS_First(
		long rootObjectEntryId, int status,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
			orderByComparator);

	/**
	 * Returns the last object entry in the ordered set where rootObjectEntryId = &#63; and status &ne; &#63;.
	 *
	 * @param rootObjectEntryId the root object entry ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	public ObjectEntry findByROEI_NotS_Last(
			long rootObjectEntryId, int status,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
				orderByComparator)
		throws NoSuchObjectEntryException;

	/**
	 * Returns the last object entry in the ordered set where rootObjectEntryId = &#63; and status &ne; &#63;.
	 *
	 * @param rootObjectEntryId the root object entry ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	public ObjectEntry fetchByROEI_NotS_Last(
		long rootObjectEntryId, int status,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
			orderByComparator);

	/**
	 * Returns the object entries before and after the current object entry in the ordered set where rootObjectEntryId = &#63; and status &ne; &#63;.
	 *
	 * @param objectEntryId the primary key of the current object entry
	 * @param rootObjectEntryId the root object entry ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object entry
	 * @throws NoSuchObjectEntryException if a object entry with the primary key could not be found
	 */
	public ObjectEntry[] findByROEI_NotS_PrevAndNext(
			long objectEntryId, long rootObjectEntryId, int status,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
				orderByComparator)
		throws NoSuchObjectEntryException;

	/**
	 * Removes all the object entries where rootObjectEntryId = &#63; and status &ne; &#63; from the database.
	 *
	 * @param rootObjectEntryId the root object entry ID
	 * @param status the status
	 */
	public void removeByROEI_NotS(long rootObjectEntryId, int status);

	/**
	 * Returns the number of object entries where rootObjectEntryId = &#63; and status &ne; &#63;.
	 *
	 * @param rootObjectEntryId the root object entry ID
	 * @param status the status
	 * @return the number of matching object entries
	 */
	public int countByROEI_NotS(long rootObjectEntryId, int status);

	/**
	 * Returns all the object entries where groupId = &#63; and companyId = &#63; and objectEntryFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param objectEntryFolderId the object entry folder ID
	 * @return the matching object entries
	 */
	public java.util.List<ObjectEntry> findByG_C_OEFI(
		long groupId, long companyId, long objectEntryFolderId);

	/**
	 * Returns a range of all the object entries where groupId = &#63; and companyId = &#63; and objectEntryFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param objectEntryFolderId the object entry folder ID
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @return the range of matching object entries
	 */
	public java.util.List<ObjectEntry> findByG_C_OEFI(
		long groupId, long companyId, long objectEntryFolderId, int start,
		int end);

	/**
	 * Returns an ordered range of all the object entries where groupId = &#63; and companyId = &#63; and objectEntryFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param objectEntryFolderId the object entry folder ID
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entries
	 */
	public java.util.List<ObjectEntry> findByG_C_OEFI(
		long groupId, long companyId, long objectEntryFolderId, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object entries where groupId = &#63; and companyId = &#63; and objectEntryFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param objectEntryFolderId the object entry folder ID
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entries
	 */
	public java.util.List<ObjectEntry> findByG_C_OEFI(
		long groupId, long companyId, long objectEntryFolderId, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object entry in the ordered set where groupId = &#63; and companyId = &#63; and objectEntryFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param objectEntryFolderId the object entry folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	public ObjectEntry findByG_C_OEFI_First(
			long groupId, long companyId, long objectEntryFolderId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
				orderByComparator)
		throws NoSuchObjectEntryException;

	/**
	 * Returns the first object entry in the ordered set where groupId = &#63; and companyId = &#63; and objectEntryFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param objectEntryFolderId the object entry folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	public ObjectEntry fetchByG_C_OEFI_First(
		long groupId, long companyId, long objectEntryFolderId,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
			orderByComparator);

	/**
	 * Returns the last object entry in the ordered set where groupId = &#63; and companyId = &#63; and objectEntryFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param objectEntryFolderId the object entry folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	public ObjectEntry findByG_C_OEFI_Last(
			long groupId, long companyId, long objectEntryFolderId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
				orderByComparator)
		throws NoSuchObjectEntryException;

	/**
	 * Returns the last object entry in the ordered set where groupId = &#63; and companyId = &#63; and objectEntryFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param objectEntryFolderId the object entry folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	public ObjectEntry fetchByG_C_OEFI_Last(
		long groupId, long companyId, long objectEntryFolderId,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
			orderByComparator);

	/**
	 * Returns the object entries before and after the current object entry in the ordered set where groupId = &#63; and companyId = &#63; and objectEntryFolderId = &#63;.
	 *
	 * @param objectEntryId the primary key of the current object entry
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param objectEntryFolderId the object entry folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object entry
	 * @throws NoSuchObjectEntryException if a object entry with the primary key could not be found
	 */
	public ObjectEntry[] findByG_C_OEFI_PrevAndNext(
			long objectEntryId, long groupId, long companyId,
			long objectEntryFolderId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
				orderByComparator)
		throws NoSuchObjectEntryException;

	/**
	 * Removes all the object entries where groupId = &#63; and companyId = &#63; and objectEntryFolderId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param objectEntryFolderId the object entry folder ID
	 */
	public void removeByG_C_OEFI(
		long groupId, long companyId, long objectEntryFolderId);

	/**
	 * Returns the number of object entries where groupId = &#63; and companyId = &#63; and objectEntryFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param objectEntryFolderId the object entry folder ID
	 * @return the number of matching object entries
	 */
	public int countByG_C_OEFI(
		long groupId, long companyId, long objectEntryFolderId);

	/**
	 * Returns all the object entries where groupId = &#63; and objectDefinitionId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @return the matching object entries
	 */
	public java.util.List<ObjectEntry> findByG_ODI_S(
		long groupId, long objectDefinitionId, int status);

	/**
	 * Returns a range of all the object entries where groupId = &#63; and objectDefinitionId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @return the range of matching object entries
	 */
	public java.util.List<ObjectEntry> findByG_ODI_S(
		long groupId, long objectDefinitionId, int status, int start, int end);

	/**
	 * Returns an ordered range of all the object entries where groupId = &#63; and objectDefinitionId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entries
	 */
	public java.util.List<ObjectEntry> findByG_ODI_S(
		long groupId, long objectDefinitionId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object entries where groupId = &#63; and objectDefinitionId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entries
	 */
	public java.util.List<ObjectEntry> findByG_ODI_S(
		long groupId, long objectDefinitionId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object entry in the ordered set where groupId = &#63; and objectDefinitionId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	public ObjectEntry findByG_ODI_S_First(
			long groupId, long objectDefinitionId, int status,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
				orderByComparator)
		throws NoSuchObjectEntryException;

	/**
	 * Returns the first object entry in the ordered set where groupId = &#63; and objectDefinitionId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	public ObjectEntry fetchByG_ODI_S_First(
		long groupId, long objectDefinitionId, int status,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
			orderByComparator);

	/**
	 * Returns the last object entry in the ordered set where groupId = &#63; and objectDefinitionId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	public ObjectEntry findByG_ODI_S_Last(
			long groupId, long objectDefinitionId, int status,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
				orderByComparator)
		throws NoSuchObjectEntryException;

	/**
	 * Returns the last object entry in the ordered set where groupId = &#63; and objectDefinitionId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	public ObjectEntry fetchByG_ODI_S_Last(
		long groupId, long objectDefinitionId, int status,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
			orderByComparator);

	/**
	 * Returns the object entries before and after the current object entry in the ordered set where groupId = &#63; and objectDefinitionId = &#63; and status = &#63;.
	 *
	 * @param objectEntryId the primary key of the current object entry
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object entry
	 * @throws NoSuchObjectEntryException if a object entry with the primary key could not be found
	 */
	public ObjectEntry[] findByG_ODI_S_PrevAndNext(
			long objectEntryId, long groupId, long objectDefinitionId,
			int status,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
				orderByComparator)
		throws NoSuchObjectEntryException;

	/**
	 * Removes all the object entries where groupId = &#63; and objectDefinitionId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 */
	public void removeByG_ODI_S(
		long groupId, long objectDefinitionId, int status);

	/**
	 * Returns the number of object entries where groupId = &#63; and objectDefinitionId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @return the number of matching object entries
	 */
	public int countByG_ODI_S(
		long groupId, long objectDefinitionId, int status);

	/**
	 * Returns all the object entries where userId = &#63; and createDate &gt; &#63; and objectDefinitionId = &#63;.
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param objectDefinitionId the object definition ID
	 * @return the matching object entries
	 */
	public java.util.List<ObjectEntry> findByU_GtCD_ODI(
		long userId, Date createDate, long objectDefinitionId);

	/**
	 * Returns a range of all the object entries where userId = &#63; and createDate &gt; &#63; and objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @return the range of matching object entries
	 */
	public java.util.List<ObjectEntry> findByU_GtCD_ODI(
		long userId, Date createDate, long objectDefinitionId, int start,
		int end);

	/**
	 * Returns an ordered range of all the object entries where userId = &#63; and createDate &gt; &#63; and objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entries
	 */
	public java.util.List<ObjectEntry> findByU_GtCD_ODI(
		long userId, Date createDate, long objectDefinitionId, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object entries where userId = &#63; and createDate &gt; &#63; and objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching object entries
	 */
	public java.util.List<ObjectEntry> findByU_GtCD_ODI(
		long userId, Date createDate, long objectDefinitionId, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object entry in the ordered set where userId = &#63; and createDate &gt; &#63; and objectDefinitionId = &#63;.
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	public ObjectEntry findByU_GtCD_ODI_First(
			long userId, Date createDate, long objectDefinitionId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
				orderByComparator)
		throws NoSuchObjectEntryException;

	/**
	 * Returns the first object entry in the ordered set where userId = &#63; and createDate &gt; &#63; and objectDefinitionId = &#63;.
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	public ObjectEntry fetchByU_GtCD_ODI_First(
		long userId, Date createDate, long objectDefinitionId,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
			orderByComparator);

	/**
	 * Returns the last object entry in the ordered set where userId = &#63; and createDate &gt; &#63; and objectDefinitionId = &#63;.
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	public ObjectEntry findByU_GtCD_ODI_Last(
			long userId, Date createDate, long objectDefinitionId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
				orderByComparator)
		throws NoSuchObjectEntryException;

	/**
	 * Returns the last object entry in the ordered set where userId = &#63; and createDate &gt; &#63; and objectDefinitionId = &#63;.
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	public ObjectEntry fetchByU_GtCD_ODI_Last(
		long userId, Date createDate, long objectDefinitionId,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
			orderByComparator);

	/**
	 * Returns the object entries before and after the current object entry in the ordered set where userId = &#63; and createDate &gt; &#63; and objectDefinitionId = &#63;.
	 *
	 * @param objectEntryId the primary key of the current object entry
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param objectDefinitionId the object definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next object entry
	 * @throws NoSuchObjectEntryException if a object entry with the primary key could not be found
	 */
	public ObjectEntry[] findByU_GtCD_ODI_PrevAndNext(
			long objectEntryId, long userId, Date createDate,
			long objectDefinitionId,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
				orderByComparator)
		throws NoSuchObjectEntryException;

	/**
	 * Removes all the object entries where userId = &#63; and createDate &gt; &#63; and objectDefinitionId = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param objectDefinitionId the object definition ID
	 */
	public void removeByU_GtCD_ODI(
		long userId, Date createDate, long objectDefinitionId);

	/**
	 * Returns the number of object entries where userId = &#63; and createDate &gt; &#63; and objectDefinitionId = &#63;.
	 *
	 * @param userId the user ID
	 * @param createDate the create date
	 * @param objectDefinitionId the object definition ID
	 * @return the number of matching object entries
	 */
	public int countByU_GtCD_ODI(
		long userId, Date createDate, long objectDefinitionId);

	/**
	 * Returns the object entry where externalReferenceCode = &#63; and groupId = &#63; and companyId = &#63; and objectDefinitionId = &#63; or throws a <code>NoSuchObjectEntryException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param objectDefinitionId the object definition ID
	 * @return the matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	public ObjectEntry findByERC_G_C_ODI(
			String externalReferenceCode, long groupId, long companyId,
			long objectDefinitionId)
		throws NoSuchObjectEntryException;

	/**
	 * Returns the object entry where externalReferenceCode = &#63; and groupId = &#63; and companyId = &#63; and objectDefinitionId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param objectDefinitionId the object definition ID
	 * @return the matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	public ObjectEntry fetchByERC_G_C_ODI(
		String externalReferenceCode, long groupId, long companyId,
		long objectDefinitionId);

	/**
	 * Returns the object entry where externalReferenceCode = &#63; and groupId = &#63; and companyId = &#63; and objectDefinitionId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param objectDefinitionId the object definition ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	public ObjectEntry fetchByERC_G_C_ODI(
		String externalReferenceCode, long groupId, long companyId,
		long objectDefinitionId, boolean useFinderCache);

	/**
	 * Removes the object entry where externalReferenceCode = &#63; and groupId = &#63; and companyId = &#63; and objectDefinitionId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param objectDefinitionId the object definition ID
	 * @return the object entry that was removed
	 */
	public ObjectEntry removeByERC_G_C_ODI(
			String externalReferenceCode, long groupId, long companyId,
			long objectDefinitionId)
		throws NoSuchObjectEntryException;

	/**
	 * Returns the number of object entries where externalReferenceCode = &#63; and groupId = &#63; and companyId = &#63; and objectDefinitionId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param objectDefinitionId the object definition ID
	 * @return the number of matching object entries
	 */
	public int countByERC_G_C_ODI(
		String externalReferenceCode, long groupId, long companyId,
		long objectDefinitionId);

	/**
	 * Caches the object entry in the entity cache if it is enabled.
	 *
	 * @param objectEntry the object entry
	 */
	public void cacheResult(ObjectEntry objectEntry);

	/**
	 * Caches the object entries in the entity cache if it is enabled.
	 *
	 * @param objectEntries the object entries
	 */
	public void cacheResult(java.util.List<ObjectEntry> objectEntries);

	/**
	 * Creates a new object entry with the primary key. Does not add the object entry to the database.
	 *
	 * @param objectEntryId the primary key for the new object entry
	 * @return the new object entry
	 */
	public ObjectEntry create(long objectEntryId);

	/**
	 * Removes the object entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param objectEntryId the primary key of the object entry
	 * @return the object entry that was removed
	 * @throws NoSuchObjectEntryException if a object entry with the primary key could not be found
	 */
	public ObjectEntry remove(long objectEntryId)
		throws NoSuchObjectEntryException;

	public ObjectEntry updateImpl(ObjectEntry objectEntry);

	/**
	 * Returns the object entry with the primary key or throws a <code>NoSuchObjectEntryException</code> if it could not be found.
	 *
	 * @param objectEntryId the primary key of the object entry
	 * @return the object entry
	 * @throws NoSuchObjectEntryException if a object entry with the primary key could not be found
	 */
	public ObjectEntry findByPrimaryKey(long objectEntryId)
		throws NoSuchObjectEntryException;

	/**
	 * Returns the object entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param objectEntryId the primary key of the object entry
	 * @return the object entry, or <code>null</code> if a object entry with the primary key could not be found
	 */
	public ObjectEntry fetchByPrimaryKey(long objectEntryId);

	/**
	 * Returns all the object entries.
	 *
	 * @return the object entries
	 */
	public java.util.List<ObjectEntry> findAll();

	/**
	 * Returns a range of all the object entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @return the range of object entries
	 */
	public java.util.List<ObjectEntry> findAll(int start, int end);

	/**
	 * Returns an ordered range of all the object entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of object entries
	 */
	public java.util.List<ObjectEntry> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of object entries
	 */
	public java.util.List<ObjectEntry> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the object entries from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of object entries.
	 *
	 * @return the number of object entries
	 */
	public int countAll();

}