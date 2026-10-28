/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.service.persistence;

import com.liferay.asset.list.exception.NoSuchEntryAssetEntryRelException;
import com.liferay.asset.list.model.AssetListEntryAssetEntryRel;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the asset list entry asset entry rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see AssetListEntryAssetEntryRelUtil
 * @generated
 */
@ProviderType
public interface AssetListEntryAssetEntryRelPersistence
	extends BasePersistence<AssetListEntryAssetEntryRel>,
			CTPersistence<AssetListEntryAssetEntryRel> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link AssetListEntryAssetEntryRelUtil} to access the asset list entry asset entry rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the asset list entry asset entry rels where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryAssetEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of asset list entry asset entry rels
	 * @param end the upper bound of the range of asset list entry asset entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entry asset entry rels
	 */
	public java.util.List<AssetListEntryAssetEntryRel> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<AssetListEntryAssetEntryRel> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first asset list entry asset entry rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry asset entry rel
	 * @throws NoSuchEntryAssetEntryRelException if a matching asset list entry asset entry rel could not be found
	 */
	public AssetListEntryAssetEntryRel findByUuid_First(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator
				<AssetListEntryAssetEntryRel> orderByComparator)
		throws NoSuchEntryAssetEntryRelException;

	/**
	 * Returns the first asset list entry asset entry rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry asset entry rel, or <code>null</code> if a matching asset list entry asset entry rel could not be found
	 */
	public AssetListEntryAssetEntryRel fetchByUuid_First(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator
			<AssetListEntryAssetEntryRel> orderByComparator);

	/**
	 * Removes all the asset list entry asset entry rels where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public void removeByUuid(String uuid);

	/**
	 * Returns the number of asset list entry asset entry rels where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching asset list entry asset entry rels
	 */
	public int countByUuid(String uuid);

	/**
	 * Returns the asset list entry asset entry rel where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchEntryAssetEntryRelException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching asset list entry asset entry rel
	 * @throws NoSuchEntryAssetEntryRelException if a matching asset list entry asset entry rel could not be found
	 */
	public AssetListEntryAssetEntryRel findByUUID_G(String uuid, long groupId)
		throws NoSuchEntryAssetEntryRelException;

	/**
	 * Returns the asset list entry asset entry rel where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset list entry asset entry rel, or <code>null</code> if a matching asset list entry asset entry rel could not be found
	 */
	public AssetListEntryAssetEntryRel fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache);

	/**
	 * Removes the asset list entry asset entry rel where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the asset list entry asset entry rel that was removed
	 */
	public AssetListEntryAssetEntryRel removeByUUID_G(String uuid, long groupId)
		throws NoSuchEntryAssetEntryRelException;

	/**
	 * Returns the number of asset list entry asset entry rels where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching asset list entry asset entry rels
	 */
	public int countByUUID_G(String uuid, long groupId);

	/**
	 * Returns an ordered range of all the asset list entry asset entry rels where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryAssetEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of asset list entry asset entry rels
	 * @param end the upper bound of the range of asset list entry asset entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entry asset entry rels
	 */
	public java.util.List<AssetListEntryAssetEntryRel> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<AssetListEntryAssetEntryRel> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first asset list entry asset entry rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry asset entry rel
	 * @throws NoSuchEntryAssetEntryRelException if a matching asset list entry asset entry rel could not be found
	 */
	public AssetListEntryAssetEntryRel findByUuid_C_First(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator
				<AssetListEntryAssetEntryRel> orderByComparator)
		throws NoSuchEntryAssetEntryRelException;

	/**
	 * Returns the first asset list entry asset entry rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry asset entry rel, or <code>null</code> if a matching asset list entry asset entry rel could not be found
	 */
	public AssetListEntryAssetEntryRel fetchByUuid_C_First(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator
			<AssetListEntryAssetEntryRel> orderByComparator);

	/**
	 * Removes all the asset list entry asset entry rels where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public void removeByUuid_C(String uuid, long companyId);

	/**
	 * Returns the number of asset list entry asset entry rels where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching asset list entry asset entry rels
	 */
	public int countByUuid_C(String uuid, long companyId);

	/**
	 * Returns an ordered range of all the asset list entry asset entry rels where assetEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryAssetEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param assetEntryId the asset entry ID
	 * @param start the lower bound of the range of asset list entry asset entry rels
	 * @param end the upper bound of the range of asset list entry asset entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entry asset entry rels
	 */
	public java.util.List<AssetListEntryAssetEntryRel> findByAssetEntryId(
		long assetEntryId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<AssetListEntryAssetEntryRel> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first asset list entry asset entry rel in the ordered set where assetEntryId = &#63;.
	 *
	 * @param assetEntryId the asset entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry asset entry rel
	 * @throws NoSuchEntryAssetEntryRelException if a matching asset list entry asset entry rel could not be found
	 */
	public AssetListEntryAssetEntryRel findByAssetEntryId_First(
			long assetEntryId,
			com.liferay.portal.kernel.util.OrderByComparator
				<AssetListEntryAssetEntryRel> orderByComparator)
		throws NoSuchEntryAssetEntryRelException;

	/**
	 * Returns the first asset list entry asset entry rel in the ordered set where assetEntryId = &#63;.
	 *
	 * @param assetEntryId the asset entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry asset entry rel, or <code>null</code> if a matching asset list entry asset entry rel could not be found
	 */
	public AssetListEntryAssetEntryRel fetchByAssetEntryId_First(
		long assetEntryId,
		com.liferay.portal.kernel.util.OrderByComparator
			<AssetListEntryAssetEntryRel> orderByComparator);

	/**
	 * Removes all the asset list entry asset entry rels where assetEntryId = &#63; from the database.
	 *
	 * @param assetEntryId the asset entry ID
	 */
	public void removeByAssetEntryId(long assetEntryId);

	/**
	 * Returns the number of asset list entry asset entry rels where assetEntryId = &#63;.
	 *
	 * @param assetEntryId the asset entry ID
	 * @return the number of matching asset list entry asset entry rels
	 */
	public int countByAssetEntryId(long assetEntryId);

	/**
	 * Returns an ordered range of all the asset list entry asset entry rels where assetListEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryAssetEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param start the lower bound of the range of asset list entry asset entry rels
	 * @param end the upper bound of the range of asset list entry asset entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entry asset entry rels
	 */
	public java.util.List<AssetListEntryAssetEntryRel> findByAssetListEntryId(
		long assetListEntryId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<AssetListEntryAssetEntryRel> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first asset list entry asset entry rel in the ordered set where assetListEntryId = &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry asset entry rel
	 * @throws NoSuchEntryAssetEntryRelException if a matching asset list entry asset entry rel could not be found
	 */
	public AssetListEntryAssetEntryRel findByAssetListEntryId_First(
			long assetListEntryId,
			com.liferay.portal.kernel.util.OrderByComparator
				<AssetListEntryAssetEntryRel> orderByComparator)
		throws NoSuchEntryAssetEntryRelException;

	/**
	 * Returns the first asset list entry asset entry rel in the ordered set where assetListEntryId = &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry asset entry rel, or <code>null</code> if a matching asset list entry asset entry rel could not be found
	 */
	public AssetListEntryAssetEntryRel fetchByAssetListEntryId_First(
		long assetListEntryId,
		com.liferay.portal.kernel.util.OrderByComparator
			<AssetListEntryAssetEntryRel> orderByComparator);

	/**
	 * Removes all the asset list entry asset entry rels where assetListEntryId = &#63; from the database.
	 *
	 * @param assetListEntryId the asset list entry ID
	 */
	public void removeByAssetListEntryId(long assetListEntryId);

	/**
	 * Returns the number of asset list entry asset entry rels where assetListEntryId = &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @return the number of matching asset list entry asset entry rels
	 */
	public int countByAssetListEntryId(long assetListEntryId);

	/**
	 * Returns an ordered range of all the asset list entry asset entry rels where assetListEntryId = &#63; and audienceEntryERC = &#63; and segmentsEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryAssetEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERC the audience entry erc
	 * @param segmentsEntryId the segments entry ID
	 * @param start the lower bound of the range of asset list entry asset entry rels
	 * @param end the upper bound of the range of asset list entry asset entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entry asset entry rels
	 */
	public java.util.List<AssetListEntryAssetEntryRel> findByA_AEERC_S(
		long assetListEntryId, String audienceEntryERC, long segmentsEntryId,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<AssetListEntryAssetEntryRel> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first asset list entry asset entry rel in the ordered set where assetListEntryId = &#63; and audienceEntryERC = &#63; and segmentsEntryId = &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERC the audience entry erc
	 * @param segmentsEntryId the segments entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry asset entry rel
	 * @throws NoSuchEntryAssetEntryRelException if a matching asset list entry asset entry rel could not be found
	 */
	public AssetListEntryAssetEntryRel findByA_AEERC_S_First(
			long assetListEntryId, String audienceEntryERC,
			long segmentsEntryId,
			com.liferay.portal.kernel.util.OrderByComparator
				<AssetListEntryAssetEntryRel> orderByComparator)
		throws NoSuchEntryAssetEntryRelException;

	/**
	 * Returns the first asset list entry asset entry rel in the ordered set where assetListEntryId = &#63; and audienceEntryERC = &#63; and segmentsEntryId = &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERC the audience entry erc
	 * @param segmentsEntryId the segments entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry asset entry rel, or <code>null</code> if a matching asset list entry asset entry rel could not be found
	 */
	public AssetListEntryAssetEntryRel fetchByA_AEERC_S_First(
		long assetListEntryId, String audienceEntryERC, long segmentsEntryId,
		com.liferay.portal.kernel.util.OrderByComparator
			<AssetListEntryAssetEntryRel> orderByComparator);

	/**
	 * Returns an ordered range of all the asset list entry asset entry rels where assetListEntryId = &#63; and audienceEntryERC = &#63; and segmentsEntryId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryAssetEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERC the audience entry erc
	 * @param segmentsEntryIds the segments entry IDs
	 * @param start the lower bound of the range of asset list entry asset entry rels
	 * @param end the upper bound of the range of asset list entry asset entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entry asset entry rels
	 */
	public java.util.List<AssetListEntryAssetEntryRel> findByA_AEERC_S(
		long assetListEntryId, String audienceEntryERC, long[] segmentsEntryIds,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<AssetListEntryAssetEntryRel> orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the asset list entry asset entry rels where assetListEntryId = &#63; and audienceEntryERC = &#63; and segmentsEntryId = &#63; from the database.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERC the audience entry erc
	 * @param segmentsEntryId the segments entry ID
	 */
	public void removeByA_AEERC_S(
		long assetListEntryId, String audienceEntryERC, long segmentsEntryId);

	/**
	 * Returns the number of asset list entry asset entry rels where assetListEntryId = &#63; and audienceEntryERC = &#63; and segmentsEntryId = &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERC the audience entry erc
	 * @param segmentsEntryId the segments entry ID
	 * @return the number of matching asset list entry asset entry rels
	 */
	public int countByA_AEERC_S(
		long assetListEntryId, String audienceEntryERC, long segmentsEntryId);

	/**
	 * Returns the number of asset list entry asset entry rels where assetListEntryId = &#63; and audienceEntryERC = &#63; and segmentsEntryId = any &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERC the audience entry erc
	 * @param segmentsEntryIds the segments entry IDs
	 * @return the number of matching asset list entry asset entry rels
	 */
	public int countByA_AEERC_S(
		long assetListEntryId, String audienceEntryERC,
		long[] segmentsEntryIds);

	/**
	 * Returns the asset list entry asset entry rel where assetListEntryId = &#63; and audienceEntryERC = &#63; and segmentsEntryId = &#63; and position = &#63; or throws a <code>NoSuchEntryAssetEntryRelException</code> if it could not be found.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERC the audience entry erc
	 * @param segmentsEntryId the segments entry ID
	 * @param position the position
	 * @return the matching asset list entry asset entry rel
	 * @throws NoSuchEntryAssetEntryRelException if a matching asset list entry asset entry rel could not be found
	 */
	public AssetListEntryAssetEntryRel findByA_AEERC_S_P(
			long assetListEntryId, String audienceEntryERC,
			long segmentsEntryId, int position)
		throws NoSuchEntryAssetEntryRelException;

	/**
	 * Returns the asset list entry asset entry rel where assetListEntryId = &#63; and audienceEntryERC = &#63; and segmentsEntryId = &#63; and position = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERC the audience entry erc
	 * @param segmentsEntryId the segments entry ID
	 * @param position the position
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset list entry asset entry rel, or <code>null</code> if a matching asset list entry asset entry rel could not be found
	 */
	public AssetListEntryAssetEntryRel fetchByA_AEERC_S_P(
		long assetListEntryId, String audienceEntryERC, long segmentsEntryId,
		int position, boolean useFinderCache);

	/**
	 * Removes the asset list entry asset entry rel where assetListEntryId = &#63; and audienceEntryERC = &#63; and segmentsEntryId = &#63; and position = &#63; from the database.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERC the audience entry erc
	 * @param segmentsEntryId the segments entry ID
	 * @param position the position
	 * @return the asset list entry asset entry rel that was removed
	 */
	public AssetListEntryAssetEntryRel removeByA_AEERC_S_P(
			long assetListEntryId, String audienceEntryERC,
			long segmentsEntryId, int position)
		throws NoSuchEntryAssetEntryRelException;

	/**
	 * Returns the number of asset list entry asset entry rels where assetListEntryId = &#63; and audienceEntryERC = &#63; and segmentsEntryId = &#63; and position = &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERC the audience entry erc
	 * @param segmentsEntryId the segments entry ID
	 * @param position the position
	 * @return the number of matching asset list entry asset entry rels
	 */
	public int countByA_AEERC_S_P(
		long assetListEntryId, String audienceEntryERC, long segmentsEntryId,
		int position);

	/**
	 * Returns all the asset list entry asset entry rels where assetListEntryId = &#63; and audienceEntryERC = &#63; and segmentsEntryId = &#63; and position &gt; &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERC the audience entry erc
	 * @param segmentsEntryId the segments entry ID
	 * @param position the position
	 * @return the matching asset list entry asset entry rels
	 */
	public java.util.List<AssetListEntryAssetEntryRel> findByA_AEERC_S_GtP(
		long assetListEntryId, String audienceEntryERC, long segmentsEntryId,
		int position);

	/**
	 * Returns a range of all the asset list entry asset entry rels where assetListEntryId = &#63; and audienceEntryERC = &#63; and segmentsEntryId = &#63; and position &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryAssetEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERC the audience entry erc
	 * @param segmentsEntryId the segments entry ID
	 * @param position the position
	 * @param start the lower bound of the range of asset list entry asset entry rels
	 * @param end the upper bound of the range of asset list entry asset entry rels (not inclusive)
	 * @return the range of matching asset list entry asset entry rels
	 */
	public java.util.List<AssetListEntryAssetEntryRel> findByA_AEERC_S_GtP(
		long assetListEntryId, String audienceEntryERC, long segmentsEntryId,
		int position, int start, int end);

	/**
	 * Returns an ordered range of all the asset list entry asset entry rels where assetListEntryId = &#63; and audienceEntryERC = &#63; and segmentsEntryId = &#63; and position &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryAssetEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERC the audience entry erc
	 * @param segmentsEntryId the segments entry ID
	 * @param position the position
	 * @param start the lower bound of the range of asset list entry asset entry rels
	 * @param end the upper bound of the range of asset list entry asset entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entry asset entry rels
	 */
	public java.util.List<AssetListEntryAssetEntryRel> findByA_AEERC_S_GtP(
		long assetListEntryId, String audienceEntryERC, long segmentsEntryId,
		int position, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<AssetListEntryAssetEntryRel> orderByComparator);

	/**
	 * Returns an ordered range of all the asset list entry asset entry rels where assetListEntryId = &#63; and audienceEntryERC = &#63; and segmentsEntryId = &#63; and position &gt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryAssetEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERC the audience entry erc
	 * @param segmentsEntryId the segments entry ID
	 * @param position the position
	 * @param start the lower bound of the range of asset list entry asset entry rels
	 * @param end the upper bound of the range of asset list entry asset entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entry asset entry rels
	 */
	public java.util.List<AssetListEntryAssetEntryRel> findByA_AEERC_S_GtP(
		long assetListEntryId, String audienceEntryERC, long segmentsEntryId,
		int position, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<AssetListEntryAssetEntryRel> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first asset list entry asset entry rel in the ordered set where assetListEntryId = &#63; and audienceEntryERC = &#63; and segmentsEntryId = &#63; and position &gt; &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERC the audience entry erc
	 * @param segmentsEntryId the segments entry ID
	 * @param position the position
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry asset entry rel
	 * @throws NoSuchEntryAssetEntryRelException if a matching asset list entry asset entry rel could not be found
	 */
	public AssetListEntryAssetEntryRel findByA_AEERC_S_GtP_First(
			long assetListEntryId, String audienceEntryERC,
			long segmentsEntryId, int position,
			com.liferay.portal.kernel.util.OrderByComparator
				<AssetListEntryAssetEntryRel> orderByComparator)
		throws NoSuchEntryAssetEntryRelException;

	/**
	 * Returns the first asset list entry asset entry rel in the ordered set where assetListEntryId = &#63; and audienceEntryERC = &#63; and segmentsEntryId = &#63; and position &gt; &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERC the audience entry erc
	 * @param segmentsEntryId the segments entry ID
	 * @param position the position
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry asset entry rel, or <code>null</code> if a matching asset list entry asset entry rel could not be found
	 */
	public AssetListEntryAssetEntryRel fetchByA_AEERC_S_GtP_First(
		long assetListEntryId, String audienceEntryERC, long segmentsEntryId,
		int position,
		com.liferay.portal.kernel.util.OrderByComparator
			<AssetListEntryAssetEntryRel> orderByComparator);

	/**
	 * Removes all the asset list entry asset entry rels where assetListEntryId = &#63; and audienceEntryERC = &#63; and segmentsEntryId = &#63; and position &gt; &#63; from the database.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERC the audience entry erc
	 * @param segmentsEntryId the segments entry ID
	 * @param position the position
	 */
	public void removeByA_AEERC_S_GtP(
		long assetListEntryId, String audienceEntryERC, long segmentsEntryId,
		int position);

	/**
	 * Returns the number of asset list entry asset entry rels where assetListEntryId = &#63; and audienceEntryERC = &#63; and segmentsEntryId = &#63; and position &gt; &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERC the audience entry erc
	 * @param segmentsEntryId the segments entry ID
	 * @param position the position
	 * @return the number of matching asset list entry asset entry rels
	 */
	public int countByA_AEERC_S_GtP(
		long assetListEntryId, String audienceEntryERC, long segmentsEntryId,
		int position);

	/**
	 * Creates a new asset list entry asset entry rel with the primary key. Does not add the asset list entry asset entry rel to the database.
	 *
	 * @param assetListEntryAssetEntryRelId the primary key for the new asset list entry asset entry rel
	 * @return the new asset list entry asset entry rel
	 */
	public AssetListEntryAssetEntryRel create(
		long assetListEntryAssetEntryRelId);

	/**
	 * Removes the asset list entry asset entry rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param assetListEntryAssetEntryRelId the primary key of the asset list entry asset entry rel
	 * @return the asset list entry asset entry rel that was removed
	 * @throws NoSuchEntryAssetEntryRelException if a asset list entry asset entry rel with the primary key could not be found
	 */
	public AssetListEntryAssetEntryRel remove(
			long assetListEntryAssetEntryRelId)
		throws NoSuchEntryAssetEntryRelException;

	public AssetListEntryAssetEntryRel updateImpl(
		AssetListEntryAssetEntryRel assetListEntryAssetEntryRel);

	/**
	 * Returns the asset list entry asset entry rel with the primary key or throws a <code>NoSuchEntryAssetEntryRelException</code> if it could not be found.
	 *
	 * @param assetListEntryAssetEntryRelId the primary key of the asset list entry asset entry rel
	 * @return the asset list entry asset entry rel
	 * @throws NoSuchEntryAssetEntryRelException if a asset list entry asset entry rel with the primary key could not be found
	 */
	public AssetListEntryAssetEntryRel findByPrimaryKey(
			long assetListEntryAssetEntryRelId)
		throws NoSuchEntryAssetEntryRelException;

	/**
	 * Returns the asset list entry asset entry rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param assetListEntryAssetEntryRelId the primary key of the asset list entry asset entry rel
	 * @return the asset list entry asset entry rel, or <code>null</code> if a asset list entry asset entry rel with the primary key could not be found
	 */
	public AssetListEntryAssetEntryRel fetchByPrimaryKey(
		long assetListEntryAssetEntryRelId);

	/**
	 * Returns the asset list entry asset entry rel where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching asset list entry asset entry rel, or <code>null</code> if a matching asset list entry asset entry rel could not be found
	 */
	public default AssetListEntryAssetEntryRel fetchByUUID_G(
		String uuid, long groupId) {

		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns the asset list entry asset entry rel where assetListEntryId = &#63; and audienceEntryERC = &#63; and segmentsEntryId = &#63; and position = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERC the audience entry erc
	 * @param segmentsEntryId the segments entry ID
	 * @param position the position
	 * @return the matching asset list entry asset entry rel, or <code>null</code> if a matching asset list entry asset entry rel could not be found
	 */
	public default AssetListEntryAssetEntryRel fetchByA_AEERC_S_P(
		long assetListEntryId, String audienceEntryERC, long segmentsEntryId,
		int position) {

		return fetchByA_AEERC_S_P(
			assetListEntryId, audienceEntryERC, segmentsEntryId, position,
			true);
	}

	/**
	 * Returns all the asset list entry asset entry rels where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching asset list entry asset entry rels
	 */
	public default java.util.List<AssetListEntryAssetEntryRel> findByUuid(
		String uuid) {

		return findByUuid(
			uuid, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the asset list entry asset entry rels where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryAssetEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of asset list entry asset entry rels
	 * @param end the upper bound of the range of asset list entry asset entry rels (not inclusive)
	 * @return the range of matching asset list entry asset entry rels
	 */
	public default java.util.List<AssetListEntryAssetEntryRel> findByUuid(
		String uuid, int start, int end) {

		return findByUuid(uuid, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the asset list entry asset entry rels where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryAssetEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of asset list entry asset entry rels
	 * @param end the upper bound of the range of asset list entry asset entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entry asset entry rels
	 */
	public default java.util.List<AssetListEntryAssetEntryRel> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<AssetListEntryAssetEntryRel> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the asset list entry asset entry rels where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching asset list entry asset entry rels
	 */
	public default java.util.List<AssetListEntryAssetEntryRel> findByUuid_C(
		String uuid, long companyId) {

		return findByUuid_C(
			uuid, companyId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the asset list entry asset entry rels where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryAssetEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of asset list entry asset entry rels
	 * @param end the upper bound of the range of asset list entry asset entry rels (not inclusive)
	 * @return the range of matching asset list entry asset entry rels
	 */
	public default java.util.List<AssetListEntryAssetEntryRel> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the asset list entry asset entry rels where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryAssetEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of asset list entry asset entry rels
	 * @param end the upper bound of the range of asset list entry asset entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entry asset entry rels
	 */
	public default java.util.List<AssetListEntryAssetEntryRel> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<AssetListEntryAssetEntryRel> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the asset list entry asset entry rels where assetEntryId = &#63;.
	 *
	 * @param assetEntryId the asset entry ID
	 * @return the matching asset list entry asset entry rels
	 */
	public default java.util.List<AssetListEntryAssetEntryRel>
		findByAssetEntryId(long assetEntryId) {

		return findByAssetEntryId(
			assetEntryId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the asset list entry asset entry rels where assetEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryAssetEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param assetEntryId the asset entry ID
	 * @param start the lower bound of the range of asset list entry asset entry rels
	 * @param end the upper bound of the range of asset list entry asset entry rels (not inclusive)
	 * @return the range of matching asset list entry asset entry rels
	 */
	public default java.util.List<AssetListEntryAssetEntryRel>
		findByAssetEntryId(long assetEntryId, int start, int end) {

		return findByAssetEntryId(assetEntryId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the asset list entry asset entry rels where assetEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryAssetEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param assetEntryId the asset entry ID
	 * @param start the lower bound of the range of asset list entry asset entry rels
	 * @param end the upper bound of the range of asset list entry asset entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entry asset entry rels
	 */
	public default java.util.List<AssetListEntryAssetEntryRel>
		findByAssetEntryId(
			long assetEntryId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<AssetListEntryAssetEntryRel> orderByComparator) {

		return findByAssetEntryId(
			assetEntryId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the asset list entry asset entry rels where assetListEntryId = &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @return the matching asset list entry asset entry rels
	 */
	public default java.util.List<AssetListEntryAssetEntryRel>
		findByAssetListEntryId(long assetListEntryId) {

		return findByAssetListEntryId(
			assetListEntryId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the asset list entry asset entry rels where assetListEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryAssetEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param start the lower bound of the range of asset list entry asset entry rels
	 * @param end the upper bound of the range of asset list entry asset entry rels (not inclusive)
	 * @return the range of matching asset list entry asset entry rels
	 */
	public default java.util.List<AssetListEntryAssetEntryRel>
		findByAssetListEntryId(long assetListEntryId, int start, int end) {

		return findByAssetListEntryId(assetListEntryId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the asset list entry asset entry rels where assetListEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryAssetEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param start the lower bound of the range of asset list entry asset entry rels
	 * @param end the upper bound of the range of asset list entry asset entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entry asset entry rels
	 */
	public default java.util.List<AssetListEntryAssetEntryRel>
		findByAssetListEntryId(
			long assetListEntryId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<AssetListEntryAssetEntryRel> orderByComparator) {

		return findByAssetListEntryId(
			assetListEntryId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the asset list entry asset entry rels where assetListEntryId = &#63; and audienceEntryERC = &#63; and segmentsEntryId = &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERC the audience entry erc
	 * @param segmentsEntryId the segments entry ID
	 * @return the matching asset list entry asset entry rels
	 */
	public default java.util.List<AssetListEntryAssetEntryRel> findByA_AEERC_S(
		long assetListEntryId, String audienceEntryERC, long segmentsEntryId) {

		return findByA_AEERC_S(
			assetListEntryId, audienceEntryERC, segmentsEntryId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the asset list entry asset entry rels where assetListEntryId = &#63; and audienceEntryERC = &#63; and segmentsEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryAssetEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERC the audience entry erc
	 * @param segmentsEntryId the segments entry ID
	 * @param start the lower bound of the range of asset list entry asset entry rels
	 * @param end the upper bound of the range of asset list entry asset entry rels (not inclusive)
	 * @return the range of matching asset list entry asset entry rels
	 */
	public default java.util.List<AssetListEntryAssetEntryRel> findByA_AEERC_S(
		long assetListEntryId, String audienceEntryERC, long segmentsEntryId,
		int start, int end) {

		return findByA_AEERC_S(
			assetListEntryId, audienceEntryERC, segmentsEntryId, start, end,
			null, true);
	}

	/**
	 * Returns an ordered range of all the asset list entry asset entry rels where assetListEntryId = &#63; and audienceEntryERC = &#63; and segmentsEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryAssetEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERC the audience entry erc
	 * @param segmentsEntryId the segments entry ID
	 * @param start the lower bound of the range of asset list entry asset entry rels
	 * @param end the upper bound of the range of asset list entry asset entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entry asset entry rels
	 */
	public default java.util.List<AssetListEntryAssetEntryRel> findByA_AEERC_S(
		long assetListEntryId, String audienceEntryERC, long segmentsEntryId,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<AssetListEntryAssetEntryRel> orderByComparator) {

		return findByA_AEERC_S(
			assetListEntryId, audienceEntryERC, segmentsEntryId, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns all the asset list entry asset entry rels where assetListEntryId = &#63; and audienceEntryERC = &#63; and segmentsEntryId = any &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERC the audience entry erc
	 * @param segmentsEntryIds the segments entry IDs
	 * @return the matching asset list entry asset entry rels
	 */
	public default java.util.List<AssetListEntryAssetEntryRel> findByA_AEERC_S(
		long assetListEntryId, String audienceEntryERC,
		long[] segmentsEntryIds) {

		return findByA_AEERC_S(
			assetListEntryId, audienceEntryERC, segmentsEntryIds,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the asset list entry asset entry rels where assetListEntryId = &#63; and audienceEntryERC = &#63; and segmentsEntryId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryAssetEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERC the audience entry erc
	 * @param segmentsEntryIds the segments entry IDs
	 * @param start the lower bound of the range of asset list entry asset entry rels
	 * @param end the upper bound of the range of asset list entry asset entry rels (not inclusive)
	 * @return the range of matching asset list entry asset entry rels
	 */
	public default java.util.List<AssetListEntryAssetEntryRel> findByA_AEERC_S(
		long assetListEntryId, String audienceEntryERC, long[] segmentsEntryIds,
		int start, int end) {

		return findByA_AEERC_S(
			assetListEntryId, audienceEntryERC, segmentsEntryIds, start, end,
			null, true);
	}

	/**
	 * Returns an ordered range of all the asset list entry asset entry rels where assetListEntryId = &#63; and audienceEntryERC = &#63; and segmentsEntryId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryAssetEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERC the audience entry erc
	 * @param segmentsEntryIds the segments entry IDs
	 * @param start the lower bound of the range of asset list entry asset entry rels
	 * @param end the upper bound of the range of asset list entry asset entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entry asset entry rels
	 */
	public default java.util.List<AssetListEntryAssetEntryRel> findByA_AEERC_S(
		long assetListEntryId, String audienceEntryERC, long[] segmentsEntryIds,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<AssetListEntryAssetEntryRel> orderByComparator) {

		return findByA_AEERC_S(
			assetListEntryId, audienceEntryERC, segmentsEntryIds, start, end,
			orderByComparator, true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-596582082