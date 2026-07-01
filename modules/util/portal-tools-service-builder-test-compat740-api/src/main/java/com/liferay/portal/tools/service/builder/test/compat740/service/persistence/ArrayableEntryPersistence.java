/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.compat740.service.persistence;

import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.tools.service.builder.test.compat740.exception.NoSuchArrayableEntryException;
import com.liferay.portal.tools.service.builder.test.compat740.model.ArrayableEntry;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the arrayable entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see ArrayableEntryUtil
 * @generated
 */
@ProviderType
public interface ArrayableEntryPersistence
	extends BasePersistence<ArrayableEntry> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link ArrayableEntryUtil} to access the arrayable entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the arrayable entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.compat740.model.impl.ArrayableEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of arrayable entries
	 * @param end the upper bound of the range of arrayable entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching arrayable entries
	 */
	public java.util.List<ArrayableEntry> findByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ArrayableEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first arrayable entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching arrayable entry
	 * @throws NoSuchArrayableEntryException if a matching arrayable entry could not be found
	 */
	public ArrayableEntry findByGroupId_First(
			long groupId,
			com.liferay.portal.kernel.util.OrderByComparator<ArrayableEntry>
				orderByComparator)
		throws NoSuchArrayableEntryException;

	/**
	 * Returns the first arrayable entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching arrayable entry, or <code>null</code> if a matching arrayable entry could not be found
	 */
	public ArrayableEntry fetchByGroupId_First(
		long groupId,
		com.liferay.portal.kernel.util.OrderByComparator<ArrayableEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the arrayable entries where groupId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.compat740.model.impl.ArrayableEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of arrayable entries
	 * @param end the upper bound of the range of arrayable entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching arrayable entries
	 */
	public java.util.List<ArrayableEntry> findByGroupId(
		long[] groupIds, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ArrayableEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the arrayable entries where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public void removeByGroupId(long groupId);

	/**
	 * Returns the number of arrayable entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching arrayable entries
	 */
	public int countByGroupId(long groupId);

	/**
	 * Returns the number of arrayable entries where groupId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @return the number of matching arrayable entries
	 */
	public int countByGroupId(long[] groupIds);

	/**
	 * Creates a new arrayable entry with the primary key. Does not add the arrayable entry to the database.
	 *
	 * @param arrayableEntryId the primary key for the new arrayable entry
	 * @return the new arrayable entry
	 */
	public ArrayableEntry create(long arrayableEntryId);

	/**
	 * Removes the arrayable entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param arrayableEntryId the primary key of the arrayable entry
	 * @return the arrayable entry that was removed
	 * @throws NoSuchArrayableEntryException if a arrayable entry with the primary key could not be found
	 */
	public ArrayableEntry remove(long arrayableEntryId)
		throws NoSuchArrayableEntryException;

	public ArrayableEntry updateImpl(ArrayableEntry arrayableEntry);

	/**
	 * Returns the arrayable entry with the primary key or throws a <code>NoSuchArrayableEntryException</code> if it could not be found.
	 *
	 * @param arrayableEntryId the primary key of the arrayable entry
	 * @return the arrayable entry
	 * @throws NoSuchArrayableEntryException if a arrayable entry with the primary key could not be found
	 */
	public ArrayableEntry findByPrimaryKey(long arrayableEntryId)
		throws NoSuchArrayableEntryException;

	/**
	 * Returns the arrayable entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param arrayableEntryId the primary key of the arrayable entry
	 * @return the arrayable entry, or <code>null</code> if a arrayable entry with the primary key could not be found
	 */
	public ArrayableEntry fetchByPrimaryKey(long arrayableEntryId);

	/**
	 * Returns all the arrayable entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching arrayable entries
	 */
	public default java.util.List<ArrayableEntry> findByGroupId(long groupId) {
		return findByGroupId(
			groupId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the arrayable entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.compat740.model.impl.ArrayableEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of arrayable entries
	 * @param end the upper bound of the range of arrayable entries (not inclusive)
	 * @return the range of matching arrayable entries
	 */
	public default java.util.List<ArrayableEntry> findByGroupId(
		long groupId, int start, int end) {

		return findByGroupId(groupId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the arrayable entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.compat740.model.impl.ArrayableEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of arrayable entries
	 * @param end the upper bound of the range of arrayable entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching arrayable entries
	 */
	public default java.util.List<ArrayableEntry> findByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ArrayableEntry>
			orderByComparator) {

		return findByGroupId(groupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the arrayable entries where groupId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @return the matching arrayable entries
	 */
	public default java.util.List<ArrayableEntry> findByGroupId(
		long[] groupIds) {

		return findByGroupId(
			groupIds, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the arrayable entries where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.compat740.model.impl.ArrayableEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of arrayable entries
	 * @param end the upper bound of the range of arrayable entries (not inclusive)
	 * @return the range of matching arrayable entries
	 */
	public default java.util.List<ArrayableEntry> findByGroupId(
		long[] groupIds, int start, int end) {

		return findByGroupId(groupIds, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the arrayable entries where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.tools.service.builder.test.compat740.model.impl.ArrayableEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of arrayable entries
	 * @param end the upper bound of the range of arrayable entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching arrayable entries
	 */
	public default java.util.List<ArrayableEntry> findByGroupId(
		long[] groupIds, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<ArrayableEntry>
			orderByComparator) {

		return findByGroupId(groupIds, start, end, orderByComparator, true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-642175922