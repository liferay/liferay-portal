/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.service.persistence;

import com.liferay.asset.list.exception.NoSuchEntryException;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the asset list entry service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see AssetListEntryUtil
 * @generated
 */
@ProviderType
public interface AssetListEntryPersistence
	extends BasePersistence<AssetListEntry>, CTPersistence<AssetListEntry> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link AssetListEntryUtil} to access the asset list entry persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the asset list entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entries
	 */
	public java.util.List<AssetListEntry> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetListEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first asset list entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry
	 * @throws NoSuchEntryException if a matching asset list entry could not be found
	 */
	public AssetListEntry findByUuid_First(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator<AssetListEntry>
				orderByComparator)
		throws NoSuchEntryException;

	/**
	 * Returns the first asset list entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry, or <code>null</code> if a matching asset list entry could not be found
	 */
	public AssetListEntry fetchByUuid_First(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<AssetListEntry>
			orderByComparator);

	/**
	 * Removes all the asset list entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public void removeByUuid(String uuid);

	/**
	 * Returns the number of asset list entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching asset list entries
	 */
	public int countByUuid(String uuid);

	/**
	 * Returns the asset list entry where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching asset list entry
	 * @throws NoSuchEntryException if a matching asset list entry could not be found
	 */
	public AssetListEntry findByUUID_G(String uuid, long groupId)
		throws NoSuchEntryException;

	/**
	 * Returns the asset list entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset list entry, or <code>null</code> if a matching asset list entry could not be found
	 */
	public AssetListEntry fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache);

	/**
	 * Removes the asset list entry where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the asset list entry that was removed
	 */
	public AssetListEntry removeByUUID_G(String uuid, long groupId)
		throws NoSuchEntryException;

	/**
	 * Returns the number of asset list entries where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching asset list entries
	 */
	public int countByUUID_G(String uuid, long groupId);

	/**
	 * Returns an ordered range of all the asset list entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entries
	 */
	public java.util.List<AssetListEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetListEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first asset list entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry
	 * @throws NoSuchEntryException if a matching asset list entry could not be found
	 */
	public AssetListEntry findByUuid_C_First(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator<AssetListEntry>
				orderByComparator)
		throws NoSuchEntryException;

	/**
	 * Returns the first asset list entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry, or <code>null</code> if a matching asset list entry could not be found
	 */
	public AssetListEntry fetchByUuid_C_First(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<AssetListEntry>
			orderByComparator);

	/**
	 * Removes all the asset list entries where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public void removeByUuid_C(String uuid, long companyId);

	/**
	 * Returns the number of asset list entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching asset list entries
	 */
	public int countByUuid_C(String uuid, long companyId);

	/**
	 * Returns an ordered range of all the asset list entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entries
	 */
	public java.util.List<AssetListEntry> findByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetListEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first asset list entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry
	 * @throws NoSuchEntryException if a matching asset list entry could not be found
	 */
	public AssetListEntry findByGroupId_First(
			long groupId,
			com.liferay.portal.kernel.util.OrderByComparator<AssetListEntry>
				orderByComparator)
		throws NoSuchEntryException;

	/**
	 * Returns the first asset list entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry, or <code>null</code> if a matching asset list entry could not be found
	 */
	public AssetListEntry fetchByGroupId_First(
		long groupId,
		com.liferay.portal.kernel.util.OrderByComparator<AssetListEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the asset list entries that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries that the user has permission to view
	 */
	public java.util.List<AssetListEntry> filterFindByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetListEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the asset list entries that the user has permission to view where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries that the user has permission to view
	 */
	public java.util.List<AssetListEntry> filterFindByGroupId(
		long[] groupIds, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetListEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the asset list entries where groupId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entries
	 */
	public java.util.List<AssetListEntry> findByGroupId(
		long[] groupIds, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetListEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the asset list entries where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public void removeByGroupId(long groupId);

	/**
	 * Returns the number of asset list entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching asset list entries
	 */
	public int countByGroupId(long groupId);

	/**
	 * Returns the number of asset list entries where groupId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @return the number of matching asset list entries
	 */
	public int countByGroupId(long[] groupIds);

	/**
	 * Returns the number of asset list entries that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching asset list entries that the user has permission to view
	 */
	public int filterCountByGroupId(long groupId);

	/**
	 * Returns the number of asset list entries that the user has permission to view where groupId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @return the number of matching asset list entries that the user has permission to view
	 */
	public int filterCountByGroupId(long[] groupIds);

	/**
	 * Returns the asset list entry where groupId = &#63; and assetListEntryKey = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param assetListEntryKey the asset list entry key
	 * @return the matching asset list entry
	 * @throws NoSuchEntryException if a matching asset list entry could not be found
	 */
	public AssetListEntry findByG_ALEK(long groupId, String assetListEntryKey)
		throws NoSuchEntryException;

	/**
	 * Returns the asset list entry where groupId = &#63; and assetListEntryKey = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param assetListEntryKey the asset list entry key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset list entry, or <code>null</code> if a matching asset list entry could not be found
	 */
	public AssetListEntry fetchByG_ALEK(
		long groupId, String assetListEntryKey, boolean useFinderCache);

	/**
	 * Removes the asset list entry where groupId = &#63; and assetListEntryKey = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param assetListEntryKey the asset list entry key
	 * @return the asset list entry that was removed
	 */
	public AssetListEntry removeByG_ALEK(long groupId, String assetListEntryKey)
		throws NoSuchEntryException;

	/**
	 * Returns the number of asset list entries where groupId = &#63; and assetListEntryKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param assetListEntryKey the asset list entry key
	 * @return the number of matching asset list entries
	 */
	public int countByG_ALEK(long groupId, String assetListEntryKey);

	/**
	 * Returns the asset list entry where groupId = &#63; and title = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @return the matching asset list entry
	 * @throws NoSuchEntryException if a matching asset list entry could not be found
	 */
	public AssetListEntry findByG_T(long groupId, String title)
		throws NoSuchEntryException;

	/**
	 * Returns the asset list entry where groupId = &#63; and title = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset list entry, or <code>null</code> if a matching asset list entry could not be found
	 */
	public AssetListEntry fetchByG_T(
		long groupId, String title, boolean useFinderCache);

	/**
	 * Removes the asset list entry where groupId = &#63; and title = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @return the asset list entry that was removed
	 */
	public AssetListEntry removeByG_T(long groupId, String title)
		throws NoSuchEntryException;

	/**
	 * Returns the number of asset list entries where groupId = &#63; and title = &#63;.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @return the number of matching asset list entries
	 */
	public int countByG_T(long groupId, String title);

	/**
	 * Returns all the asset list entries where groupId = &#63; and title LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @return the matching asset list entries
	 */
	public java.util.List<AssetListEntry> findByG_LikeT(
		long groupId, String title);

	/**
	 * Returns a range of all the asset list entries where groupId = &#63; and title LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of matching asset list entries
	 */
	public java.util.List<AssetListEntry> findByG_LikeT(
		long groupId, String title, int start, int end);

	/**
	 * Returns an ordered range of all the asset list entries where groupId = &#63; and title LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries
	 */
	public java.util.List<AssetListEntry> findByG_LikeT(
		long groupId, String title, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetListEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the asset list entries where groupId = &#63; and title LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entries
	 */
	public java.util.List<AssetListEntry> findByG_LikeT(
		long groupId, String title, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetListEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first asset list entry in the ordered set where groupId = &#63; and title LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry
	 * @throws NoSuchEntryException if a matching asset list entry could not be found
	 */
	public AssetListEntry findByG_LikeT_First(
			long groupId, String title,
			com.liferay.portal.kernel.util.OrderByComparator<AssetListEntry>
				orderByComparator)
		throws NoSuchEntryException;

	/**
	 * Returns the first asset list entry in the ordered set where groupId = &#63; and title LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry, or <code>null</code> if a matching asset list entry could not be found
	 */
	public AssetListEntry fetchByG_LikeT_First(
		long groupId, String title,
		com.liferay.portal.kernel.util.OrderByComparator<AssetListEntry>
			orderByComparator);

	/**
	 * Returns all the asset list entries that the user has permission to view where groupId = &#63; and title LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @return the matching asset list entries that the user has permission to view
	 */
	public java.util.List<AssetListEntry> filterFindByG_LikeT(
		long groupId, String title);

	/**
	 * Returns a range of all the asset list entries that the user has permission to view where groupId = &#63; and title LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of matching asset list entries that the user has permission to view
	 */
	public java.util.List<AssetListEntry> filterFindByG_LikeT(
		long groupId, String title, int start, int end);

	/**
	 * Returns an ordered range of all the asset list entries that the user has permissions to view where groupId = &#63; and title LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries that the user has permission to view
	 */
	public java.util.List<AssetListEntry> filterFindByG_LikeT(
		long groupId, String title, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetListEntry>
			orderByComparator);

	/**
	 * Returns all the asset list entries that the user has permission to view where groupId = any &#63; and title LIKE &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @return the matching asset list entries that the user has permission to view
	 */
	public java.util.List<AssetListEntry> filterFindByG_LikeT(
		long[] groupIds, String title);

	/**
	 * Returns a range of all the asset list entries that the user has permission to view where groupId = any &#63; and title LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of matching asset list entries that the user has permission to view
	 */
	public java.util.List<AssetListEntry> filterFindByG_LikeT(
		long[] groupIds, String title, int start, int end);

	/**
	 * Returns an ordered range of all the asset list entries that the user has permission to view where groupId = any &#63; and title LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries that the user has permission to view
	 */
	public java.util.List<AssetListEntry> filterFindByG_LikeT(
		long[] groupIds, String title, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetListEntry>
			orderByComparator);

	/**
	 * Returns all the asset list entries where groupId = any &#63; and title LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @return the matching asset list entries
	 */
	public java.util.List<AssetListEntry> findByG_LikeT(
		long[] groupIds, String title);

	/**
	 * Returns a range of all the asset list entries where groupId = any &#63; and title LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of matching asset list entries
	 */
	public java.util.List<AssetListEntry> findByG_LikeT(
		long[] groupIds, String title, int start, int end);

	/**
	 * Returns an ordered range of all the asset list entries where groupId = any &#63; and title LIKE &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries
	 */
	public java.util.List<AssetListEntry> findByG_LikeT(
		long[] groupIds, String title, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetListEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the asset list entries where groupId = &#63; and title LIKE &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entries
	 */
	public java.util.List<AssetListEntry> findByG_LikeT(
		long[] groupIds, String title, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetListEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the asset list entries where groupId = &#63; and title LIKE &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 */
	public void removeByG_LikeT(long groupId, String title);

	/**
	 * Returns the number of asset list entries where groupId = &#63; and title LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @return the number of matching asset list entries
	 */
	public int countByG_LikeT(long groupId, String title);

	/**
	 * Returns the number of asset list entries where groupId = any &#63; and title LIKE &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @return the number of matching asset list entries
	 */
	public int countByG_LikeT(long[] groupIds, String title);

	/**
	 * Returns the number of asset list entries that the user has permission to view where groupId = &#63; and title LIKE &#63;.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @return the number of matching asset list entries that the user has permission to view
	 */
	public int filterCountByG_LikeT(long groupId, String title);

	/**
	 * Returns the number of asset list entries that the user has permission to view where groupId = any &#63; and title LIKE &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @return the number of matching asset list entries that the user has permission to view
	 */
	public int filterCountByG_LikeT(long[] groupIds, String title);

	/**
	 * Returns an ordered range of all the asset list entries where groupId = &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entries
	 */
	public java.util.List<AssetListEntry> findByG_AET(
		long groupId, String assetEntryType, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetListEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first asset list entry in the ordered set where groupId = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param assetEntryType the asset entry type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry
	 * @throws NoSuchEntryException if a matching asset list entry could not be found
	 */
	public AssetListEntry findByG_AET_First(
			long groupId, String assetEntryType,
			com.liferay.portal.kernel.util.OrderByComparator<AssetListEntry>
				orderByComparator)
		throws NoSuchEntryException;

	/**
	 * Returns the first asset list entry in the ordered set where groupId = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param assetEntryType the asset entry type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry, or <code>null</code> if a matching asset list entry could not be found
	 */
	public AssetListEntry fetchByG_AET_First(
		long groupId, String assetEntryType,
		com.liferay.portal.kernel.util.OrderByComparator<AssetListEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the asset list entries that the user has permissions to view where groupId = &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries that the user has permission to view
	 */
	public java.util.List<AssetListEntry> filterFindByG_AET(
		long groupId, String assetEntryType, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetListEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the asset list entries that the user has permission to view where groupId = any &#63; and assetEntryType = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param assetEntryTypes the asset entry types
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries that the user has permission to view
	 */
	public java.util.List<AssetListEntry> filterFindByG_AET(
		long[] groupIds, String[] assetEntryTypes, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetListEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the asset list entries where groupId = &#63; and assetEntryType = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param assetEntryTypes the asset entry types
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entries
	 */
	public java.util.List<AssetListEntry> findByG_AET(
		long[] groupIds, String[] assetEntryTypes, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetListEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the asset list entries where groupId = &#63; and assetEntryType = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param assetEntryType the asset entry type
	 */
	public void removeByG_AET(long groupId, String assetEntryType);

	/**
	 * Returns the number of asset list entries where groupId = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param assetEntryType the asset entry type
	 * @return the number of matching asset list entries
	 */
	public int countByG_AET(long groupId, String assetEntryType);

	/**
	 * Returns the number of asset list entries where groupId = any &#63; and assetEntryType = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param assetEntryTypes the asset entry types
	 * @return the number of matching asset list entries
	 */
	public int countByG_AET(long[] groupIds, String[] assetEntryTypes);

	/**
	 * Returns the number of asset list entries that the user has permission to view where groupId = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param assetEntryType the asset entry type
	 * @return the number of matching asset list entries that the user has permission to view
	 */
	public int filterCountByG_AET(long groupId, String assetEntryType);

	/**
	 * Returns the number of asset list entries that the user has permission to view where groupId = any &#63; and assetEntryType = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param assetEntryTypes the asset entry types
	 * @return the number of matching asset list entries that the user has permission to view
	 */
	public int filterCountByG_AET(long[] groupIds, String[] assetEntryTypes);

	/**
	 * Returns all the asset list entries where groupId = &#63; and title LIKE &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntryType the asset entry type
	 * @return the matching asset list entries
	 */
	public java.util.List<AssetListEntry> findByG_LikeT_AET(
		long groupId, String title, String assetEntryType);

	/**
	 * Returns a range of all the asset list entries where groupId = &#63; and title LIKE &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of matching asset list entries
	 */
	public java.util.List<AssetListEntry> findByG_LikeT_AET(
		long groupId, String title, String assetEntryType, int start, int end);

	/**
	 * Returns an ordered range of all the asset list entries where groupId = &#63; and title LIKE &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries
	 */
	public java.util.List<AssetListEntry> findByG_LikeT_AET(
		long groupId, String title, String assetEntryType, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetListEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the asset list entries where groupId = &#63; and title LIKE &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entries
	 */
	public java.util.List<AssetListEntry> findByG_LikeT_AET(
		long groupId, String title, String assetEntryType, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetListEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first asset list entry in the ordered set where groupId = &#63; and title LIKE &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntryType the asset entry type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry
	 * @throws NoSuchEntryException if a matching asset list entry could not be found
	 */
	public AssetListEntry findByG_LikeT_AET_First(
			long groupId, String title, String assetEntryType,
			com.liferay.portal.kernel.util.OrderByComparator<AssetListEntry>
				orderByComparator)
		throws NoSuchEntryException;

	/**
	 * Returns the first asset list entry in the ordered set where groupId = &#63; and title LIKE &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntryType the asset entry type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry, or <code>null</code> if a matching asset list entry could not be found
	 */
	public AssetListEntry fetchByG_LikeT_AET_First(
		long groupId, String title, String assetEntryType,
		com.liferay.portal.kernel.util.OrderByComparator<AssetListEntry>
			orderByComparator);

	/**
	 * Returns all the asset list entries that the user has permission to view where groupId = &#63; and title LIKE &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntryType the asset entry type
	 * @return the matching asset list entries that the user has permission to view
	 */
	public java.util.List<AssetListEntry> filterFindByG_LikeT_AET(
		long groupId, String title, String assetEntryType);

	/**
	 * Returns a range of all the asset list entries that the user has permission to view where groupId = &#63; and title LIKE &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of matching asset list entries that the user has permission to view
	 */
	public java.util.List<AssetListEntry> filterFindByG_LikeT_AET(
		long groupId, String title, String assetEntryType, int start, int end);

	/**
	 * Returns an ordered range of all the asset list entries that the user has permissions to view where groupId = &#63; and title LIKE &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries that the user has permission to view
	 */
	public java.util.List<AssetListEntry> filterFindByG_LikeT_AET(
		long groupId, String title, String assetEntryType, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetListEntry>
			orderByComparator);

	/**
	 * Returns all the asset list entries that the user has permission to view where groupId = any &#63; and title LIKE &#63; and assetEntryType = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param assetEntryTypes the asset entry types
	 * @return the matching asset list entries that the user has permission to view
	 */
	public java.util.List<AssetListEntry> filterFindByG_LikeT_AET(
		long[] groupIds, String title, String[] assetEntryTypes);

	/**
	 * Returns a range of all the asset list entries that the user has permission to view where groupId = any &#63; and title LIKE &#63; and assetEntryType = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param assetEntryTypes the asset entry types
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of matching asset list entries that the user has permission to view
	 */
	public java.util.List<AssetListEntry> filterFindByG_LikeT_AET(
		long[] groupIds, String title, String[] assetEntryTypes, int start,
		int end);

	/**
	 * Returns an ordered range of all the asset list entries that the user has permission to view where groupId = any &#63; and title LIKE &#63; and assetEntryType = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param assetEntryTypes the asset entry types
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries that the user has permission to view
	 */
	public java.util.List<AssetListEntry> filterFindByG_LikeT_AET(
		long[] groupIds, String title, String[] assetEntryTypes, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetListEntry>
			orderByComparator);

	/**
	 * Returns all the asset list entries where groupId = any &#63; and title LIKE &#63; and assetEntryType = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param assetEntryTypes the asset entry types
	 * @return the matching asset list entries
	 */
	public java.util.List<AssetListEntry> findByG_LikeT_AET(
		long[] groupIds, String title, String[] assetEntryTypes);

	/**
	 * Returns a range of all the asset list entries where groupId = any &#63; and title LIKE &#63; and assetEntryType = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param assetEntryTypes the asset entry types
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of matching asset list entries
	 */
	public java.util.List<AssetListEntry> findByG_LikeT_AET(
		long[] groupIds, String title, String[] assetEntryTypes, int start,
		int end);

	/**
	 * Returns an ordered range of all the asset list entries where groupId = any &#63; and title LIKE &#63; and assetEntryType = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param assetEntryTypes the asset entry types
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries
	 */
	public java.util.List<AssetListEntry> findByG_LikeT_AET(
		long[] groupIds, String title, String[] assetEntryTypes, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetListEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the asset list entries where groupId = &#63; and title LIKE &#63; and assetEntryType = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param assetEntryTypes the asset entry types
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entries
	 */
	public java.util.List<AssetListEntry> findByG_LikeT_AET(
		long[] groupIds, String title, String[] assetEntryTypes, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetListEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the asset list entries where groupId = &#63; and title LIKE &#63; and assetEntryType = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntryType the asset entry type
	 */
	public void removeByG_LikeT_AET(
		long groupId, String title, String assetEntryType);

	/**
	 * Returns the number of asset list entries where groupId = &#63; and title LIKE &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntryType the asset entry type
	 * @return the number of matching asset list entries
	 */
	public int countByG_LikeT_AET(
		long groupId, String title, String assetEntryType);

	/**
	 * Returns the number of asset list entries where groupId = any &#63; and title LIKE &#63; and assetEntryType = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param assetEntryTypes the asset entry types
	 * @return the number of matching asset list entries
	 */
	public int countByG_LikeT_AET(
		long[] groupIds, String title, String[] assetEntryTypes);

	/**
	 * Returns the number of asset list entries that the user has permission to view where groupId = &#63; and title LIKE &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntryType the asset entry type
	 * @return the number of matching asset list entries that the user has permission to view
	 */
	public int filterCountByG_LikeT_AET(
		long groupId, String title, String assetEntryType);

	/**
	 * Returns the number of asset list entries that the user has permission to view where groupId = any &#63; and title LIKE &#63; and assetEntryType = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param assetEntryTypes the asset entry types
	 * @return the number of matching asset list entries that the user has permission to view
	 */
	public int filterCountByG_LikeT_AET(
		long[] groupIds, String title, String[] assetEntryTypes);

	/**
	 * Returns an ordered range of all the asset list entries where groupId = &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entries
	 */
	public java.util.List<AssetListEntry> findByG_AES_AET(
		long groupId, String assetEntrySubtype, String assetEntryType,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetListEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first asset list entry in the ordered set where groupId = &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry
	 * @throws NoSuchEntryException if a matching asset list entry could not be found
	 */
	public AssetListEntry findByG_AES_AET_First(
			long groupId, String assetEntrySubtype, String assetEntryType,
			com.liferay.portal.kernel.util.OrderByComparator<AssetListEntry>
				orderByComparator)
		throws NoSuchEntryException;

	/**
	 * Returns the first asset list entry in the ordered set where groupId = &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry, or <code>null</code> if a matching asset list entry could not be found
	 */
	public AssetListEntry fetchByG_AES_AET_First(
		long groupId, String assetEntrySubtype, String assetEntryType,
		com.liferay.portal.kernel.util.OrderByComparator<AssetListEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the asset list entries that the user has permissions to view where groupId = &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries that the user has permission to view
	 */
	public java.util.List<AssetListEntry> filterFindByG_AES_AET(
		long groupId, String assetEntrySubtype, String assetEntryType,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetListEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the asset list entries that the user has permission to view where groupId = any &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries that the user has permission to view
	 */
	public java.util.List<AssetListEntry> filterFindByG_AES_AET(
		long[] groupIds, String assetEntrySubtype, String assetEntryType,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetListEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the asset list entries where groupId = &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entries
	 */
	public java.util.List<AssetListEntry> findByG_AES_AET(
		long[] groupIds, String assetEntrySubtype, String assetEntryType,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetListEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the asset list entries where groupId = &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 */
	public void removeByG_AES_AET(
		long groupId, String assetEntrySubtype, String assetEntryType);

	/**
	 * Returns the number of asset list entries where groupId = &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @return the number of matching asset list entries
	 */
	public int countByG_AES_AET(
		long groupId, String assetEntrySubtype, String assetEntryType);

	/**
	 * Returns the number of asset list entries where groupId = any &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @return the number of matching asset list entries
	 */
	public int countByG_AES_AET(
		long[] groupIds, String assetEntrySubtype, String assetEntryType);

	/**
	 * Returns the number of asset list entries that the user has permission to view where groupId = &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @return the number of matching asset list entries that the user has permission to view
	 */
	public int filterCountByG_AES_AET(
		long groupId, String assetEntrySubtype, String assetEntryType);

	/**
	 * Returns the number of asset list entries that the user has permission to view where groupId = any &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @return the number of matching asset list entries that the user has permission to view
	 */
	public int filterCountByG_AES_AET(
		long[] groupIds, String assetEntrySubtype, String assetEntryType);

	/**
	 * Returns all the asset list entries where groupId = &#63; and title LIKE &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @return the matching asset list entries
	 */
	public java.util.List<AssetListEntry> findByG_LikeT_AES_AET(
		long groupId, String title, String assetEntrySubtype,
		String assetEntryType);

	/**
	 * Returns a range of all the asset list entries where groupId = &#63; and title LIKE &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of matching asset list entries
	 */
	public java.util.List<AssetListEntry> findByG_LikeT_AES_AET(
		long groupId, String title, String assetEntrySubtype,
		String assetEntryType, int start, int end);

	/**
	 * Returns an ordered range of all the asset list entries where groupId = &#63; and title LIKE &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries
	 */
	public java.util.List<AssetListEntry> findByG_LikeT_AES_AET(
		long groupId, String title, String assetEntrySubtype,
		String assetEntryType, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetListEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the asset list entries where groupId = &#63; and title LIKE &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entries
	 */
	public java.util.List<AssetListEntry> findByG_LikeT_AES_AET(
		long groupId, String title, String assetEntrySubtype,
		String assetEntryType, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetListEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first asset list entry in the ordered set where groupId = &#63; and title LIKE &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry
	 * @throws NoSuchEntryException if a matching asset list entry could not be found
	 */
	public AssetListEntry findByG_LikeT_AES_AET_First(
			long groupId, String title, String assetEntrySubtype,
			String assetEntryType,
			com.liferay.portal.kernel.util.OrderByComparator<AssetListEntry>
				orderByComparator)
		throws NoSuchEntryException;

	/**
	 * Returns the first asset list entry in the ordered set where groupId = &#63; and title LIKE &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry, or <code>null</code> if a matching asset list entry could not be found
	 */
	public AssetListEntry fetchByG_LikeT_AES_AET_First(
		long groupId, String title, String assetEntrySubtype,
		String assetEntryType,
		com.liferay.portal.kernel.util.OrderByComparator<AssetListEntry>
			orderByComparator);

	/**
	 * Returns all the asset list entries that the user has permission to view where groupId = &#63; and title LIKE &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @return the matching asset list entries that the user has permission to view
	 */
	public java.util.List<AssetListEntry> filterFindByG_LikeT_AES_AET(
		long groupId, String title, String assetEntrySubtype,
		String assetEntryType);

	/**
	 * Returns a range of all the asset list entries that the user has permission to view where groupId = &#63; and title LIKE &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of matching asset list entries that the user has permission to view
	 */
	public java.util.List<AssetListEntry> filterFindByG_LikeT_AES_AET(
		long groupId, String title, String assetEntrySubtype,
		String assetEntryType, int start, int end);

	/**
	 * Returns an ordered range of all the asset list entries that the user has permissions to view where groupId = &#63; and title LIKE &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries that the user has permission to view
	 */
	public java.util.List<AssetListEntry> filterFindByG_LikeT_AES_AET(
		long groupId, String title, String assetEntrySubtype,
		String assetEntryType, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetListEntry>
			orderByComparator);

	/**
	 * Returns all the asset list entries that the user has permission to view where groupId = any &#63; and title LIKE &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @return the matching asset list entries that the user has permission to view
	 */
	public java.util.List<AssetListEntry> filterFindByG_LikeT_AES_AET(
		long[] groupIds, String title, String assetEntrySubtype,
		String assetEntryType);

	/**
	 * Returns a range of all the asset list entries that the user has permission to view where groupId = any &#63; and title LIKE &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of matching asset list entries that the user has permission to view
	 */
	public java.util.List<AssetListEntry> filterFindByG_LikeT_AES_AET(
		long[] groupIds, String title, String assetEntrySubtype,
		String assetEntryType, int start, int end);

	/**
	 * Returns an ordered range of all the asset list entries that the user has permission to view where groupId = any &#63; and title LIKE &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries that the user has permission to view
	 */
	public java.util.List<AssetListEntry> filterFindByG_LikeT_AES_AET(
		long[] groupIds, String title, String assetEntrySubtype,
		String assetEntryType, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetListEntry>
			orderByComparator);

	/**
	 * Returns all the asset list entries where groupId = any &#63; and title LIKE &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @return the matching asset list entries
	 */
	public java.util.List<AssetListEntry> findByG_LikeT_AES_AET(
		long[] groupIds, String title, String assetEntrySubtype,
		String assetEntryType);

	/**
	 * Returns a range of all the asset list entries where groupId = any &#63; and title LIKE &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of matching asset list entries
	 */
	public java.util.List<AssetListEntry> findByG_LikeT_AES_AET(
		long[] groupIds, String title, String assetEntrySubtype,
		String assetEntryType, int start, int end);

	/**
	 * Returns an ordered range of all the asset list entries where groupId = any &#63; and title LIKE &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries
	 */
	public java.util.List<AssetListEntry> findByG_LikeT_AES_AET(
		long[] groupIds, String title, String assetEntrySubtype,
		String assetEntryType, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetListEntry>
			orderByComparator);

	/**
	 * Returns an ordered range of all the asset list entries where groupId = &#63; and title LIKE &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entries
	 */
	public java.util.List<AssetListEntry> findByG_LikeT_AES_AET(
		long[] groupIds, String title, String assetEntrySubtype,
		String assetEntryType, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetListEntry>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the asset list entries where groupId = &#63; and title LIKE &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 */
	public void removeByG_LikeT_AES_AET(
		long groupId, String title, String assetEntrySubtype,
		String assetEntryType);

	/**
	 * Returns the number of asset list entries where groupId = &#63; and title LIKE &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @return the number of matching asset list entries
	 */
	public int countByG_LikeT_AES_AET(
		long groupId, String title, String assetEntrySubtype,
		String assetEntryType);

	/**
	 * Returns the number of asset list entries where groupId = any &#63; and title LIKE &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @return the number of matching asset list entries
	 */
	public int countByG_LikeT_AES_AET(
		long[] groupIds, String title, String assetEntrySubtype,
		String assetEntryType);

	/**
	 * Returns the number of asset list entries that the user has permission to view where groupId = &#63; and title LIKE &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @return the number of matching asset list entries that the user has permission to view
	 */
	public int filterCountByG_LikeT_AES_AET(
		long groupId, String title, String assetEntrySubtype,
		String assetEntryType);

	/**
	 * Returns the number of asset list entries that the user has permission to view where groupId = any &#63; and title LIKE &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param title the title
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @return the number of matching asset list entries that the user has permission to view
	 */
	public int filterCountByG_LikeT_AES_AET(
		long[] groupIds, String title, String assetEntrySubtype,
		String assetEntryType);

	/**
	 * Returns the asset list entry where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching asset list entry
	 * @throws NoSuchEntryException if a matching asset list entry could not be found
	 */
	public AssetListEntry findByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchEntryException;

	/**
	 * Returns the asset list entry where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset list entry, or <code>null</code> if a matching asset list entry could not be found
	 */
	public AssetListEntry fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache);

	/**
	 * Removes the asset list entry where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the asset list entry that was removed
	 */
	public AssetListEntry removeByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchEntryException;

	/**
	 * Returns the number of asset list entries where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching asset list entries
	 */
	public int countByERC_G(String externalReferenceCode, long groupId);

	/**
	 * Creates a new asset list entry with the primary key. Does not add the asset list entry to the database.
	 *
	 * @param assetListEntryId the primary key for the new asset list entry
	 * @return the new asset list entry
	 */
	public AssetListEntry create(long assetListEntryId);

	/**
	 * Removes the asset list entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param assetListEntryId the primary key of the asset list entry
	 * @return the asset list entry that was removed
	 * @throws NoSuchEntryException if a asset list entry with the primary key could not be found
	 */
	public AssetListEntry remove(long assetListEntryId)
		throws NoSuchEntryException;

	public AssetListEntry updateImpl(AssetListEntry assetListEntry);

	/**
	 * Returns the asset list entry with the primary key or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param assetListEntryId the primary key of the asset list entry
	 * @return the asset list entry
	 * @throws NoSuchEntryException if a asset list entry with the primary key could not be found
	 */
	public AssetListEntry findByPrimaryKey(long assetListEntryId)
		throws NoSuchEntryException;

	/**
	 * Returns the asset list entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param assetListEntryId the primary key of the asset list entry
	 * @return the asset list entry, or <code>null</code> if a asset list entry with the primary key could not be found
	 */
	public AssetListEntry fetchByPrimaryKey(long assetListEntryId);

	/**
	 * Returns the asset list entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching asset list entry, or <code>null</code> if a matching asset list entry could not be found
	 */
	public default AssetListEntry fetchByUUID_G(String uuid, long groupId) {
		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns the asset list entry where groupId = &#63; and assetListEntryKey = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param assetListEntryKey the asset list entry key
	 * @return the matching asset list entry, or <code>null</code> if a matching asset list entry could not be found
	 */
	public default AssetListEntry fetchByG_ALEK(
		long groupId, String assetListEntryKey) {

		return fetchByG_ALEK(groupId, assetListEntryKey, true);
	}

	/**
	 * Returns the asset list entry where groupId = &#63; and title = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param title the title
	 * @return the matching asset list entry, or <code>null</code> if a matching asset list entry could not be found
	 */
	public default AssetListEntry fetchByG_T(long groupId, String title) {
		return fetchByG_T(groupId, title, true);
	}

	/**
	 * Returns the asset list entry where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching asset list entry, or <code>null</code> if a matching asset list entry could not be found
	 */
	public default AssetListEntry fetchByERC_G(
		String externalReferenceCode, long groupId) {

		return fetchByERC_G(externalReferenceCode, groupId, true);
	}

	/**
	 * Returns all the asset list entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching asset list entries
	 */
	public default java.util.List<AssetListEntry> findByUuid(String uuid) {
		return findByUuid(
			uuid, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the asset list entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of matching asset list entries
	 */
	public default java.util.List<AssetListEntry> findByUuid(
		String uuid, int start, int end) {

		return findByUuid(uuid, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the asset list entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries
	 */
	public default java.util.List<AssetListEntry> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetListEntry>
			orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the asset list entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching asset list entries
	 */
	public default java.util.List<AssetListEntry> findByUuid_C(
		String uuid, long companyId) {

		return findByUuid_C(
			uuid, companyId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the asset list entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of matching asset list entries
	 */
	public default java.util.List<AssetListEntry> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the asset list entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries
	 */
	public default java.util.List<AssetListEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetListEntry>
			orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the asset list entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching asset list entries
	 */
	public default java.util.List<AssetListEntry> findByGroupId(long groupId) {
		return findByGroupId(
			groupId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the asset list entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of matching asset list entries
	 */
	public default java.util.List<AssetListEntry> findByGroupId(
		long groupId, int start, int end) {

		return findByGroupId(groupId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the asset list entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries
	 */
	public default java.util.List<AssetListEntry> findByGroupId(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetListEntry>
			orderByComparator) {

		return findByGroupId(groupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the asset list entries where groupId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @return the matching asset list entries
	 */
	public default java.util.List<AssetListEntry> findByGroupId(
		long[] groupIds) {

		return findByGroupId(
			groupIds, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the asset list entries where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of matching asset list entries
	 */
	public default java.util.List<AssetListEntry> findByGroupId(
		long[] groupIds, int start, int end) {

		return findByGroupId(groupIds, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the asset list entries where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries
	 */
	public default java.util.List<AssetListEntry> findByGroupId(
		long[] groupIds, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetListEntry>
			orderByComparator) {

		return findByGroupId(groupIds, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the asset list entries that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching asset list entries that the user has permission to view
	 */
	public default java.util.List<AssetListEntry> filterFindByGroupId(
		long groupId) {

		return filterFindByGroupId(
			groupId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the asset list entries that the user has permission to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of matching asset list entries that the user has permission to view
	 */
	public default java.util.List<AssetListEntry> filterFindByGroupId(
		long groupId, int start, int end) {

		return filterFindByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns all the asset list entries that the user has permission to view where groupId = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @return the matching asset list entries that the user has permission to view
	 */
	public default java.util.List<AssetListEntry> filterFindByGroupId(
		long[] groupIds) {

		return filterFindByGroupId(
			groupIds, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the asset list entries that the user has permission to view where groupId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of matching asset list entries that the user has permission to view
	 */
	public default java.util.List<AssetListEntry> filterFindByGroupId(
		long[] groupIds, int start, int end) {

		return filterFindByGroupId(groupIds, start, end, null);
	}

	/**
	 * Returns all the asset list entries where groupId = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param assetEntryType the asset entry type
	 * @return the matching asset list entries
	 */
	public default java.util.List<AssetListEntry> findByG_AET(
		long groupId, String assetEntryType) {

		return findByG_AET(
			groupId, assetEntryType,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the asset list entries where groupId = &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of matching asset list entries
	 */
	public default java.util.List<AssetListEntry> findByG_AET(
		long groupId, String assetEntryType, int start, int end) {

		return findByG_AET(groupId, assetEntryType, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the asset list entries where groupId = &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries
	 */
	public default java.util.List<AssetListEntry> findByG_AET(
		long groupId, String assetEntryType, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetListEntry>
			orderByComparator) {

		return findByG_AET(
			groupId, assetEntryType, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the asset list entries where groupId = any &#63; and assetEntryType = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param assetEntryTypes the asset entry types
	 * @return the matching asset list entries
	 */
	public default java.util.List<AssetListEntry> findByG_AET(
		long[] groupIds, String[] assetEntryTypes) {

		return findByG_AET(
			groupIds, assetEntryTypes,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the asset list entries where groupId = any &#63; and assetEntryType = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param assetEntryTypes the asset entry types
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of matching asset list entries
	 */
	public default java.util.List<AssetListEntry> findByG_AET(
		long[] groupIds, String[] assetEntryTypes, int start, int end) {

		return findByG_AET(groupIds, assetEntryTypes, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the asset list entries where groupId = any &#63; and assetEntryType = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param assetEntryTypes the asset entry types
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries
	 */
	public default java.util.List<AssetListEntry> findByG_AET(
		long[] groupIds, String[] assetEntryTypes, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetListEntry>
			orderByComparator) {

		return findByG_AET(
			groupIds, assetEntryTypes, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the asset list entries that the user has permission to view where groupId = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param assetEntryType the asset entry type
	 * @return the matching asset list entries that the user has permission to view
	 */
	public default java.util.List<AssetListEntry> filterFindByG_AET(
		long groupId, String assetEntryType) {

		return filterFindByG_AET(
			groupId, assetEntryType,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the asset list entries that the user has permission to view where groupId = &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of matching asset list entries that the user has permission to view
	 */
	public default java.util.List<AssetListEntry> filterFindByG_AET(
		long groupId, String assetEntryType, int start, int end) {

		return filterFindByG_AET(groupId, assetEntryType, start, end, null);
	}

	/**
	 * Returns all the asset list entries that the user has permission to view where groupId = any &#63; and assetEntryType = any &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param assetEntryTypes the asset entry types
	 * @return the matching asset list entries that the user has permission to view
	 */
	public default java.util.List<AssetListEntry> filterFindByG_AET(
		long[] groupIds, String[] assetEntryTypes) {

		return filterFindByG_AET(
			groupIds, assetEntryTypes,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the asset list entries that the user has permission to view where groupId = any &#63; and assetEntryType = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param assetEntryTypes the asset entry types
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of matching asset list entries that the user has permission to view
	 */
	public default java.util.List<AssetListEntry> filterFindByG_AET(
		long[] groupIds, String[] assetEntryTypes, int start, int end) {

		return filterFindByG_AET(groupIds, assetEntryTypes, start, end, null);
	}

	/**
	 * Returns all the asset list entries where groupId = &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @return the matching asset list entries
	 */
	public default java.util.List<AssetListEntry> findByG_AES_AET(
		long groupId, String assetEntrySubtype, String assetEntryType) {

		return findByG_AES_AET(
			groupId, assetEntrySubtype, assetEntryType,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the asset list entries where groupId = &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of matching asset list entries
	 */
	public default java.util.List<AssetListEntry> findByG_AES_AET(
		long groupId, String assetEntrySubtype, String assetEntryType,
		int start, int end) {

		return findByG_AES_AET(
			groupId, assetEntrySubtype, assetEntryType, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the asset list entries where groupId = &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries
	 */
	public default java.util.List<AssetListEntry> findByG_AES_AET(
		long groupId, String assetEntrySubtype, String assetEntryType,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetListEntry>
			orderByComparator) {

		return findByG_AES_AET(
			groupId, assetEntrySubtype, assetEntryType, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns all the asset list entries where groupId = any &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @return the matching asset list entries
	 */
	public default java.util.List<AssetListEntry> findByG_AES_AET(
		long[] groupIds, String assetEntrySubtype, String assetEntryType) {

		return findByG_AES_AET(
			groupIds, assetEntrySubtype, assetEntryType,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the asset list entries where groupId = any &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of matching asset list entries
	 */
	public default java.util.List<AssetListEntry> findByG_AES_AET(
		long[] groupIds, String assetEntrySubtype, String assetEntryType,
		int start, int end) {

		return findByG_AES_AET(
			groupIds, assetEntrySubtype, assetEntryType, start, end, null,
			true);
	}

	/**
	 * Returns an ordered range of all the asset list entries where groupId = any &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entries
	 */
	public default java.util.List<AssetListEntry> findByG_AES_AET(
		long[] groupIds, String assetEntrySubtype, String assetEntryType,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetListEntry>
			orderByComparator) {

		return findByG_AES_AET(
			groupIds, assetEntrySubtype, assetEntryType, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns all the asset list entries that the user has permission to view where groupId = &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupId the group ID
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @return the matching asset list entries that the user has permission to view
	 */
	public default java.util.List<AssetListEntry> filterFindByG_AES_AET(
		long groupId, String assetEntrySubtype, String assetEntryType) {

		return filterFindByG_AES_AET(
			groupId, assetEntrySubtype, assetEntryType,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the asset list entries that the user has permission to view where groupId = &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of matching asset list entries that the user has permission to view
	 */
	public default java.util.List<AssetListEntry> filterFindByG_AES_AET(
		long groupId, String assetEntrySubtype, String assetEntryType,
		int start, int end) {

		return filterFindByG_AES_AET(
			groupId, assetEntrySubtype, assetEntryType, start, end, null);
	}

	/**
	 * Returns all the asset list entries that the user has permission to view where groupId = any &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * @param groupIds the group IDs
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @return the matching asset list entries that the user has permission to view
	 */
	public default java.util.List<AssetListEntry> filterFindByG_AES_AET(
		long[] groupIds, String assetEntrySubtype, String assetEntryType) {

		return filterFindByG_AES_AET(
			groupIds, assetEntrySubtype, assetEntryType,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the asset list entries that the user has permission to view where groupId = any &#63; and assetEntrySubtype = &#63; and assetEntryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupIds the group IDs
	 * @param assetEntrySubtype the asset entry subtype
	 * @param assetEntryType the asset entry type
	 * @param start the lower bound of the range of asset list entries
	 * @param end the upper bound of the range of asset list entries (not inclusive)
	 * @return the range of matching asset list entries that the user has permission to view
	 */
	public default java.util.List<AssetListEntry> filterFindByG_AES_AET(
		long[] groupIds, String assetEntrySubtype, String assetEntryType,
		int start, int end) {

		return filterFindByG_AES_AET(
			groupIds, assetEntrySubtype, assetEntryType, start, end, null);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-795881103