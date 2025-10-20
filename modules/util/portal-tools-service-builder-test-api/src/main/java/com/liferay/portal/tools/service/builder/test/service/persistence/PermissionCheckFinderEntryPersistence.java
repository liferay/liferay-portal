/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence;

import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchPermissionCheckFinderEntryException;
import com.liferay.portal.tools.service.builder.test.model.PermissionCheckFinderEntry;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the permission check finder entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see PermissionCheckFinderEntryUtil
 * @generated
 */
@ProviderType
public interface PermissionCheckFinderEntryPersistence
	extends BasePersistence<PermissionCheckFinderEntry> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link PermissionCheckFinderEntryUtil} to access the permission check finder entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns all the permission check finder entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching permission check finder entries
	 */
	public java.util.List<PermissionCheckFinderEntry> findByGroupId(
		long groupId);

	/**
	 * Returns a range of all the permission check finder entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PermissionCheckFinderEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of permission check finder entries
	 * @param end the upper bound of the range of permission check finder entries (not inclusive)
	 * @return the range of matching permission check finder entries
	 */
	public java.util.List<PermissionCheckFinderEntry> findByGroupId(
		long groupId, int start, int end);

	/**
	 * Returns an ordered range of all the permission check finder entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PermissionCheckFinderEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of permission check finder entries
	 * @param end the upper bound of the range of permission check finder entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching permission check finder entries
	 */
	public java.util.List<PermissionCheckFinderEntry> findByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<PermissionCheckFinderEntry> orderByComparator);

	/**
	 * Returns an ordered range of all the permission check finder entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PermissionCheckFinderEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of permission check finder entries
	 * @param end the upper bound of the range of permission check finder entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching permission check finder entries
	 */
	public java.util.List<PermissionCheckFinderEntry> findByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<PermissionCheckFinderEntry> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first permission check finder entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching permission check finder entry
	 * @throws NoSuchPermissionCheckFinderEntryException if a matching permission check finder entry could not be found
	 */
	public PermissionCheckFinderEntry findByGroupId_First(
			long groupId,
			com.liferay.portal.kernel.util.OrderByComparator
				<PermissionCheckFinderEntry> orderByComparator)
		throws NoSuchPermissionCheckFinderEntryException;

	/**
	 * Returns the first permission check finder entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching permission check finder entry, or <code>null</code> if a matching permission check finder entry could not be found
	 */
	public PermissionCheckFinderEntry fetchByGroupId_First(
		long groupId,
		com.liferay.portal.kernel.util.OrderByComparator
			<PermissionCheckFinderEntry> orderByComparator);

	/**
	 * Returns the last permission check finder entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching permission check finder entry
	 * @throws NoSuchPermissionCheckFinderEntryException if a matching permission check finder entry could not be found
	 */
	public PermissionCheckFinderEntry findByGroupId_Last(
			long groupId,
			com.liferay.portal.kernel.util.OrderByComparator
				<PermissionCheckFinderEntry> orderByComparator)
		throws NoSuchPermissionCheckFinderEntryException;

	/**
	 * Returns the last permission check finder entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching permission check finder entry, or <code>null</code> if a matching permission check finder entry could not be found
	 */
	public PermissionCheckFinderEntry fetchByGroupId_Last(
		long groupId,
		com.liferay.portal.kernel.util.OrderByComparator
			<PermissionCheckFinderEntry> orderByComparator);

	/**
	 * Returns the permission check finder entries before and after the current permission check finder entry in the ordered set where groupId = &#63;.
	 *
	 * @param permissionCheckFinderEntryId the primary key of the current permission check finder entry
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next permission check finder entry
	 * @throws NoSuchPermissionCheckFinderEntryException if a permission check finder entry with the primary key could not be found
	 */
	public PermissionCheckFinderEntry[] findByGroupId_PrevAndNext(
			long permissionCheckFinderEntryId, long groupId,
			com.liferay.portal.kernel.util.OrderByComparator
				<PermissionCheckFinderEntry> orderByComparator)
		throws NoSuchPermissionCheckFinderEntryException;

	/**
	 * Returns all the permission check finder entries that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching permission check finder entries that the user has permission to view
	 */
	public java.util.List<PermissionCheckFinderEntry> filterFindByGroupId(
		long groupId);

	/**
	 * Returns a range of all the permission check finder entries that the user has permission to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PermissionCheckFinderEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of permission check finder entries
	 * @param end the upper bound of the range of permission check finder entries (not inclusive)
	 * @return the range of matching permission check finder entries that the user has permission to view
	 */
	public java.util.List<PermissionCheckFinderEntry> filterFindByGroupId(
		long groupId, int start, int end);

	/**
	 * Returns an ordered range of all the permission check finder entries that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PermissionCheckFinderEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of permission check finder entries
	 * @param end the upper bound of the range of permission check finder entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching permission check finder entries that the user has permission to view
	 */
	public java.util.List<PermissionCheckFinderEntry> filterFindByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<PermissionCheckFinderEntry> orderByComparator);

	/**
	 * Returns the permission check finder entries before and after the current permission check finder entry in the ordered set of permission check finder entries that the user has permission to view where groupId = &#63;.
	 *
	 * @param permissionCheckFinderEntryId the primary key of the current permission check finder entry
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next permission check finder entry
	 * @throws NoSuchPermissionCheckFinderEntryException if a permission check finder entry with the primary key could not be found
	 */
	public PermissionCheckFinderEntry[] filterFindByGroupId_PrevAndNext(
			long permissionCheckFinderEntryId, long groupId,
			com.liferay.portal.kernel.util.OrderByComparator
				<PermissionCheckFinderEntry> orderByComparator)
		throws NoSuchPermissionCheckFinderEntryException;

	/**
	 * Returns all the permission check finder entries that the user has permission to view where groupId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @return the matching permission check finder entries that the user has permission to view
	 */
	public java.util.List<PermissionCheckFinderEntry> filterFindByGroupId(
		long[] groupIds);

	/**
	 * Returns a range of all the permission check finder entries that the user has permission to view where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PermissionCheckFinderEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of permission check finder entries
	 * @param end the upper bound of the range of permission check finder entries (not inclusive)
	 * @return the range of matching permission check finder entries that the user has permission to view
	 */
	public java.util.List<PermissionCheckFinderEntry> filterFindByGroupId(
		long[] groupIds, int start, int end);

	/**
	 * Returns an ordered range of all the permission check finder entries that the user has permission to view where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PermissionCheckFinderEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of permission check finder entries
	 * @param end the upper bound of the range of permission check finder entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching permission check finder entries that the user has permission to view
	 */
	public java.util.List<PermissionCheckFinderEntry> filterFindByGroupId(
		long[] groupIds, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<PermissionCheckFinderEntry> orderByComparator);

	/**
	 * Returns all the permission check finder entries where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PermissionCheckFinderEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @return the matching permission check finder entries
	 */
	public java.util.List<PermissionCheckFinderEntry> findByGroupId(
		long[] groupIds);

	/**
	 * Returns a range of all the permission check finder entries where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PermissionCheckFinderEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of permission check finder entries
	 * @param end the upper bound of the range of permission check finder entries (not inclusive)
	 * @return the range of matching permission check finder entries
	 */
	public java.util.List<PermissionCheckFinderEntry> findByGroupId(
		long[] groupIds, int start, int end);

	/**
	 * Returns an ordered range of all the permission check finder entries where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PermissionCheckFinderEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of permission check finder entries
	 * @param end the upper bound of the range of permission check finder entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching permission check finder entries
	 */
	public java.util.List<PermissionCheckFinderEntry> findByGroupId(
		long[] groupIds, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<PermissionCheckFinderEntry> orderByComparator);

	/**
	 * Returns an ordered range of all the permission check finder entries where groupId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PermissionCheckFinderEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of permission check finder entries
	 * @param end the upper bound of the range of permission check finder entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching permission check finder entries
	 */
	public java.util.List<PermissionCheckFinderEntry> findByGroupId(
		long[] groupIds, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<PermissionCheckFinderEntry> orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the permission check finder entries where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public void removeByGroupId(long groupId);

	/**
	 * Returns the number of permission check finder entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching permission check finder entries
	 */
	public int countByGroupId(long groupId);

	/**
	 * Returns the number of permission check finder entries where groupId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @return the number of matching permission check finder entries
	 */
	public int countByGroupId(long[] groupIds);

	/**
	 * Returns the number of permission check finder entries that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching permission check finder entries that the user has permission to view
	 */
	public int filterCountByGroupId(long groupId);

	/**
	 * Returns the number of permission check finder entries that the user has permission to view where groupId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @return the number of matching permission check finder entries that the user has permission to view
	 */
	public int filterCountByGroupId(long[] groupIds);

	/**
	 * Caches the permission check finder entry in the entity cache if it is enabled.
	 *
	 * @param permissionCheckFinderEntry the permission check finder entry
	 */
	public void cacheResult(
		PermissionCheckFinderEntry permissionCheckFinderEntry);

	/**
	 * Caches the permission check finder entries in the entity cache if it is enabled.
	 *
	 * @param permissionCheckFinderEntries the permission check finder entries
	 */
	public void cacheResult(
		java.util.List<PermissionCheckFinderEntry>
			permissionCheckFinderEntries);

	/**
	 * Creates a new permission check finder entry with the primary key. Does not add the permission check finder entry to the database.
	 *
	 * @param permissionCheckFinderEntryId the primary key for the new permission check finder entry
	 * @return the new permission check finder entry
	 */
	public PermissionCheckFinderEntry create(long permissionCheckFinderEntryId);

	/**
	 * Removes the permission check finder entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param permissionCheckFinderEntryId the primary key of the permission check finder entry
	 * @return the permission check finder entry that was removed
	 * @throws NoSuchPermissionCheckFinderEntryException if a permission check finder entry with the primary key could not be found
	 */
	public PermissionCheckFinderEntry remove(long permissionCheckFinderEntryId)
		throws NoSuchPermissionCheckFinderEntryException;

	public PermissionCheckFinderEntry updateImpl(
		PermissionCheckFinderEntry permissionCheckFinderEntry);

	/**
	 * Returns the permission check finder entry with the primary key or throws a <code>NoSuchPermissionCheckFinderEntryException</code> if it could not be found.
	 *
	 * @param permissionCheckFinderEntryId the primary key of the permission check finder entry
	 * @return the permission check finder entry
	 * @throws NoSuchPermissionCheckFinderEntryException if a permission check finder entry with the primary key could not be found
	 */
	public PermissionCheckFinderEntry findByPrimaryKey(
			long permissionCheckFinderEntryId)
		throws NoSuchPermissionCheckFinderEntryException;

	/**
	 * Returns the permission check finder entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param permissionCheckFinderEntryId the primary key of the permission check finder entry
	 * @return the permission check finder entry, or <code>null</code> if a permission check finder entry with the primary key could not be found
	 */
	public PermissionCheckFinderEntry fetchByPrimaryKey(
		long permissionCheckFinderEntryId);

	/**
	 * Returns all the permission check finder entries.
	 *
	 * @return the permission check finder entries
	 */
	public java.util.List<PermissionCheckFinderEntry> findAll();

	/**
	 * Returns a range of all the permission check finder entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PermissionCheckFinderEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of permission check finder entries
	 * @param end the upper bound of the range of permission check finder entries (not inclusive)
	 * @return the range of permission check finder entries
	 */
	public java.util.List<PermissionCheckFinderEntry> findAll(
		int start, int end);

	/**
	 * Returns an ordered range of all the permission check finder entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PermissionCheckFinderEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of permission check finder entries
	 * @param end the upper bound of the range of permission check finder entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of permission check finder entries
	 */
	public java.util.List<PermissionCheckFinderEntry> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<PermissionCheckFinderEntry> orderByComparator);

	/**
	 * Returns an ordered range of all the permission check finder entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PermissionCheckFinderEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of permission check finder entries
	 * @param end the upper bound of the range of permission check finder entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of permission check finder entries
	 */
	public java.util.List<PermissionCheckFinderEntry> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<PermissionCheckFinderEntry> orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the permission check finder entries from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of permission check finder entries.
	 *
	 * @return the number of permission check finder entries
	 */
	public int countAll();

}