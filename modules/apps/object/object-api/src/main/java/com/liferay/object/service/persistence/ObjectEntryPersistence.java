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
	 * Returns an ordered range of all the object entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryModelImpl</code>.
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
	 * Returns an ordered range of all the object entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryModelImpl</code>.
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
	 * Returns an ordered range of all the object entries where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryModelImpl</code>.
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
	 * Returns an ordered range of all the object entries where groupId = &#63; and objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryModelImpl</code>.
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
	 * Returns an ordered range of all the object entries where groupId = &#63; and objectEntryFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryModelImpl</code>.
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
	 * Returns an ordered range of all the object entries where userId = &#63; and objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryModelImpl</code>.
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
	 * Returns an ordered range of all the object entries where groupId = &#63; and companyId = &#63; and objectEntryFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryModelImpl</code>.
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
	 * Returns an ordered range of all the object entries where groupId = &#63; and objectDefinitionId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryModelImpl</code>.
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
	 * Returns all the object entries where groupId = &#63; and objectDefinitionId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @return the matching object entries
	 */
	public java.util.List<ObjectEntry> findByG_ODI_NotS(
		long groupId, long objectDefinitionId, int status);

	/**
	 * Returns a range of all the object entries where groupId = &#63; and objectDefinitionId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @return the range of matching object entries
	 */
	public java.util.List<ObjectEntry> findByG_ODI_NotS(
		long groupId, long objectDefinitionId, int status, int start, int end);

	/**
	 * Returns an ordered range of all the object entries where groupId = &#63; and objectDefinitionId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryModelImpl</code>.
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
	public java.util.List<ObjectEntry> findByG_ODI_NotS(
		long groupId, long objectDefinitionId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the object entries where groupId = &#63; and objectDefinitionId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryModelImpl</code>.
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
	public java.util.List<ObjectEntry> findByG_ODI_NotS(
		long groupId, long objectDefinitionId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first object entry in the ordered set where groupId = &#63; and objectDefinitionId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry
	 * @throws NoSuchObjectEntryException if a matching object entry could not be found
	 */
	public ObjectEntry findByG_ODI_NotS_First(
			long groupId, long objectDefinitionId, int status,
			com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
				orderByComparator)
		throws NoSuchObjectEntryException;

	/**
	 * Returns the first object entry in the ordered set where groupId = &#63; and objectDefinitionId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	public ObjectEntry fetchByG_ODI_NotS_First(
		long groupId, long objectDefinitionId, int status,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
			orderByComparator);

	/**
	 * Removes all the object entries where groupId = &#63; and objectDefinitionId = &#63; and status &ne; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 */
	public void removeByG_ODI_NotS(
		long groupId, long objectDefinitionId, int status);

	/**
	 * Returns the number of object entries where groupId = &#63; and objectDefinitionId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @return the number of matching object entries
	 */
	public int countByG_ODI_NotS(
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryModelImpl</code>.
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
	 * Returns the object entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	public default ObjectEntry fetchByUUID_G(String uuid, long groupId) {
		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns the object entry where headObjectEntryId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param headObjectEntryId the head object entry ID
	 * @return the matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	public default ObjectEntry fetchByHeadObjectEntryId(
		long headObjectEntryId) {

		return fetchByHeadObjectEntryId(headObjectEntryId, true);
	}

	/**
	 * Returns the object entry where externalReferenceCode = &#63; and groupId = &#63; and companyId = &#63; and objectDefinitionId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param objectDefinitionId the object definition ID
	 * @return the matching object entry, or <code>null</code> if a matching object entry could not be found
	 */
	public default ObjectEntry fetchByERC_G_C_ODI(
		String externalReferenceCode, long groupId, long companyId,
		long objectDefinitionId) {

		return fetchByERC_G_C_ODI(
			externalReferenceCode, groupId, companyId, objectDefinitionId,
			true);
	}

	/**
	 * Returns all the object entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching object entries
	 */
	public default java.util.List<ObjectEntry> findByUuid(String uuid) {
		return findByUuid(
			uuid, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the object entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @return the range of matching object entries
	 */
	public default java.util.List<ObjectEntry> findByUuid(
		String uuid, int start, int end) {

		return findByUuid(uuid, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the object entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entries
	 */
	public default java.util.List<ObjectEntry> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
			orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the object entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching object entries
	 */
	public default java.util.List<ObjectEntry> findByUuid_C(
		String uuid, long companyId) {

		return findByUuid_C(
			uuid, companyId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the object entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @return the range of matching object entries
	 */
	public default java.util.List<ObjectEntry> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the object entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entries
	 */
	public default java.util.List<ObjectEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
			orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the object entries where objectDefinitionId = &#63;.
	 *
	 * @param objectDefinitionId the object definition ID
	 * @return the matching object entries
	 */
	public default java.util.List<ObjectEntry> findByObjectDefinitionId(
		long objectDefinitionId) {

		return findByObjectDefinitionId(
			objectDefinitionId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the object entries where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @return the range of matching object entries
	 */
	public default java.util.List<ObjectEntry> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end) {

		return findByObjectDefinitionId(
			objectDefinitionId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the object entries where objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entries
	 */
	public default java.util.List<ObjectEntry> findByObjectDefinitionId(
		long objectDefinitionId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
			orderByComparator) {

		return findByObjectDefinitionId(
			objectDefinitionId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the object entries where groupId = &#63; and objectDefinitionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @return the matching object entries
	 */
	public default java.util.List<ObjectEntry> findByG_ODI(
		long groupId, long objectDefinitionId) {

		return findByG_ODI(
			groupId, objectDefinitionId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the object entries where groupId = &#63; and objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @return the range of matching object entries
	 */
	public default java.util.List<ObjectEntry> findByG_ODI(
		long groupId, long objectDefinitionId, int start, int end) {

		return findByG_ODI(groupId, objectDefinitionId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the object entries where groupId = &#63; and objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entries
	 */
	public default java.util.List<ObjectEntry> findByG_ODI(
		long groupId, long objectDefinitionId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
			orderByComparator) {

		return findByG_ODI(
			groupId, objectDefinitionId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the object entries where groupId = &#63; and objectEntryFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param objectEntryFolderId the object entry folder ID
	 * @return the matching object entries
	 */
	public default java.util.List<ObjectEntry> findByG_OEFI(
		long groupId, long objectEntryFolderId) {

		return findByG_OEFI(
			groupId, objectEntryFolderId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the object entries where groupId = &#63; and objectEntryFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param objectEntryFolderId the object entry folder ID
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @return the range of matching object entries
	 */
	public default java.util.List<ObjectEntry> findByG_OEFI(
		long groupId, long objectEntryFolderId, int start, int end) {

		return findByG_OEFI(
			groupId, objectEntryFolderId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the object entries where groupId = &#63; and objectEntryFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param objectEntryFolderId the object entry folder ID
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entries
	 */
	public default java.util.List<ObjectEntry> findByG_OEFI(
		long groupId, long objectEntryFolderId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
			orderByComparator) {

		return findByG_OEFI(
			groupId, objectEntryFolderId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the object entries where userId = &#63; and objectDefinitionId = &#63;.
	 *
	 * @param userId the user ID
	 * @param objectDefinitionId the object definition ID
	 * @return the matching object entries
	 */
	public default java.util.List<ObjectEntry> findByU_ODI(
		long userId, long objectDefinitionId) {

		return findByU_ODI(
			userId, objectDefinitionId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the object entries where userId = &#63; and objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @return the range of matching object entries
	 */
	public default java.util.List<ObjectEntry> findByU_ODI(
		long userId, long objectDefinitionId, int start, int end) {

		return findByU_ODI(userId, objectDefinitionId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the object entries where userId = &#63; and objectDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param objectDefinitionId the object definition ID
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching object entries
	 */
	public default java.util.List<ObjectEntry> findByU_ODI(
		long userId, long objectDefinitionId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
			orderByComparator) {

		return findByU_ODI(
			userId, objectDefinitionId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the object entries where groupId = &#63; and companyId = &#63; and objectEntryFolderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param objectEntryFolderId the object entry folder ID
	 * @return the matching object entries
	 */
	public default java.util.List<ObjectEntry> findByG_C_OEFI(
		long groupId, long companyId, long objectEntryFolderId) {

		return findByG_C_OEFI(
			groupId, companyId, objectEntryFolderId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the object entries where groupId = &#63; and companyId = &#63; and objectEntryFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param objectEntryFolderId the object entry folder ID
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @return the range of matching object entries
	 */
	public default java.util.List<ObjectEntry> findByG_C_OEFI(
		long groupId, long companyId, long objectEntryFolderId, int start,
		int end) {

		return findByG_C_OEFI(
			groupId, companyId, objectEntryFolderId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the object entries where groupId = &#63; and companyId = &#63; and objectEntryFolderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryModelImpl</code>.
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
	public default java.util.List<ObjectEntry> findByG_C_OEFI(
		long groupId, long companyId, long objectEntryFolderId, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
			orderByComparator) {

		return findByG_C_OEFI(
			groupId, companyId, objectEntryFolderId, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns all the object entries where groupId = &#63; and objectDefinitionId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @return the matching object entries
	 */
	public default java.util.List<ObjectEntry> findByG_ODI_S(
		long groupId, long objectDefinitionId, int status) {

		return findByG_ODI_S(
			groupId, objectDefinitionId, status,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the object entries where groupId = &#63; and objectDefinitionId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param objectDefinitionId the object definition ID
	 * @param status the status
	 * @param start the lower bound of the range of object entries
	 * @param end the upper bound of the range of object entries (not inclusive)
	 * @return the range of matching object entries
	 */
	public default java.util.List<ObjectEntry> findByG_ODI_S(
		long groupId, long objectDefinitionId, int status, int start, int end) {

		return findByG_ODI_S(
			groupId, objectDefinitionId, status, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the object entries where groupId = &#63; and objectDefinitionId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryModelImpl</code>.
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
	public default java.util.List<ObjectEntry> findByG_ODI_S(
		long groupId, long objectDefinitionId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ObjectEntry>
			orderByComparator) {

		return findByG_ODI_S(
			groupId, objectDefinitionId, status, start, end, orderByComparator,
			true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1253816660