/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.service.persistence;

import com.liferay.asset.list.exception.NoSuchEntryAudienceEntryRelException;
import com.liferay.asset.list.model.AssetListEntryAudienceEntryRel;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the asset list entry audience entry rel service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see AssetListEntryAudienceEntryRelUtil
 * @generated
 */
@ProviderType
public interface AssetListEntryAudienceEntryRelPersistence
	extends BasePersistence<AssetListEntryAudienceEntryRel>,
			CTPersistence<AssetListEntryAudienceEntryRel> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link AssetListEntryAudienceEntryRelUtil} to access the asset list entry audience entry rel persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the asset list entry audience entry rels where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of asset list entry audience entry rels
	 * @param end the upper bound of the range of asset list entry audience entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entry audience entry rels
	 */
	public java.util.List<AssetListEntryAudienceEntryRel> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<AssetListEntryAudienceEntryRel> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first asset list entry audience entry rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry audience entry rel
	 * @throws NoSuchEntryAudienceEntryRelException if a matching asset list entry audience entry rel could not be found
	 */
	public AssetListEntryAudienceEntryRel findByUuid_First(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator
				<AssetListEntryAudienceEntryRel> orderByComparator)
		throws NoSuchEntryAudienceEntryRelException;

	/**
	 * Returns the first asset list entry audience entry rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry audience entry rel, or <code>null</code> if a matching asset list entry audience entry rel could not be found
	 */
	public AssetListEntryAudienceEntryRel fetchByUuid_First(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator
			<AssetListEntryAudienceEntryRel> orderByComparator);

	/**
	 * Removes all the asset list entry audience entry rels where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public void removeByUuid(String uuid);

	/**
	 * Returns the number of asset list entry audience entry rels where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching asset list entry audience entry rels
	 */
	public int countByUuid(String uuid);

	/**
	 * Returns the asset list entry audience entry rel where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchEntryAudienceEntryRelException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching asset list entry audience entry rel
	 * @throws NoSuchEntryAudienceEntryRelException if a matching asset list entry audience entry rel could not be found
	 */
	public AssetListEntryAudienceEntryRel findByUUID_G(
			String uuid, long groupId)
		throws NoSuchEntryAudienceEntryRelException;

	/**
	 * Returns the asset list entry audience entry rel where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset list entry audience entry rel, or <code>null</code> if a matching asset list entry audience entry rel could not be found
	 */
	public AssetListEntryAudienceEntryRel fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache);

	/**
	 * Removes the asset list entry audience entry rel where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the asset list entry audience entry rel that was removed
	 */
	public AssetListEntryAudienceEntryRel removeByUUID_G(
			String uuid, long groupId)
		throws NoSuchEntryAudienceEntryRelException;

	/**
	 * Returns the number of asset list entry audience entry rels where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching asset list entry audience entry rels
	 */
	public int countByUUID_G(String uuid, long groupId);

	/**
	 * Returns an ordered range of all the asset list entry audience entry rels where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of asset list entry audience entry rels
	 * @param end the upper bound of the range of asset list entry audience entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entry audience entry rels
	 */
	public java.util.List<AssetListEntryAudienceEntryRel> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<AssetListEntryAudienceEntryRel> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first asset list entry audience entry rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry audience entry rel
	 * @throws NoSuchEntryAudienceEntryRelException if a matching asset list entry audience entry rel could not be found
	 */
	public AssetListEntryAudienceEntryRel findByUuid_C_First(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator
				<AssetListEntryAudienceEntryRel> orderByComparator)
		throws NoSuchEntryAudienceEntryRelException;

	/**
	 * Returns the first asset list entry audience entry rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry audience entry rel, or <code>null</code> if a matching asset list entry audience entry rel could not be found
	 */
	public AssetListEntryAudienceEntryRel fetchByUuid_C_First(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator
			<AssetListEntryAudienceEntryRel> orderByComparator);

	/**
	 * Removes all the asset list entry audience entry rels where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public void removeByUuid_C(String uuid, long companyId);

	/**
	 * Returns the number of asset list entry audience entry rels where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching asset list entry audience entry rels
	 */
	public int countByUuid_C(String uuid, long companyId);

	/**
	 * Returns an ordered range of all the asset list entry audience entry rels where assetListEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param start the lower bound of the range of asset list entry audience entry rels
	 * @param end the upper bound of the range of asset list entry audience entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entry audience entry rels
	 */
	public java.util.List<AssetListEntryAudienceEntryRel>
		findByAssetListEntryId(
			long assetListEntryId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<AssetListEntryAudienceEntryRel> orderByComparator,
			boolean useFinderCache);

	/**
	 * Returns the first asset list entry audience entry rel in the ordered set where assetListEntryId = &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry audience entry rel
	 * @throws NoSuchEntryAudienceEntryRelException if a matching asset list entry audience entry rel could not be found
	 */
	public AssetListEntryAudienceEntryRel findByAssetListEntryId_First(
			long assetListEntryId,
			com.liferay.portal.kernel.util.OrderByComparator
				<AssetListEntryAudienceEntryRel> orderByComparator)
		throws NoSuchEntryAudienceEntryRelException;

	/**
	 * Returns the first asset list entry audience entry rel in the ordered set where assetListEntryId = &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry audience entry rel, or <code>null</code> if a matching asset list entry audience entry rel could not be found
	 */
	public AssetListEntryAudienceEntryRel fetchByAssetListEntryId_First(
		long assetListEntryId,
		com.liferay.portal.kernel.util.OrderByComparator
			<AssetListEntryAudienceEntryRel> orderByComparator);

	/**
	 * Removes all the asset list entry audience entry rels where assetListEntryId = &#63; from the database.
	 *
	 * @param assetListEntryId the asset list entry ID
	 */
	public void removeByAssetListEntryId(long assetListEntryId);

	/**
	 * Returns the number of asset list entry audience entry rels where assetListEntryId = &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @return the number of matching asset list entry audience entry rels
	 */
	public int countByAssetListEntryId(long assetListEntryId);

	/**
	 * Returns an ordered range of all the asset list entry audience entry rels where companyId = &#63; and audienceEntryERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @param start the lower bound of the range of asset list entry audience entry rels
	 * @param end the upper bound of the range of asset list entry audience entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entry audience entry rels
	 */
	public java.util.List<AssetListEntryAudienceEntryRel> findByC_AEERC(
		long companyId, String audienceEntryERC, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<AssetListEntryAudienceEntryRel> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first asset list entry audience entry rel in the ordered set where companyId = &#63; and audienceEntryERC = &#63;.
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry audience entry rel
	 * @throws NoSuchEntryAudienceEntryRelException if a matching asset list entry audience entry rel could not be found
	 */
	public AssetListEntryAudienceEntryRel findByC_AEERC_First(
			long companyId, String audienceEntryERC,
			com.liferay.portal.kernel.util.OrderByComparator
				<AssetListEntryAudienceEntryRel> orderByComparator)
		throws NoSuchEntryAudienceEntryRelException;

	/**
	 * Returns the first asset list entry audience entry rel in the ordered set where companyId = &#63; and audienceEntryERC = &#63;.
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry audience entry rel, or <code>null</code> if a matching asset list entry audience entry rel could not be found
	 */
	public AssetListEntryAudienceEntryRel fetchByC_AEERC_First(
		long companyId, String audienceEntryERC,
		com.liferay.portal.kernel.util.OrderByComparator
			<AssetListEntryAudienceEntryRel> orderByComparator);

	/**
	 * Removes all the asset list entry audience entry rels where companyId = &#63; and audienceEntryERC = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 */
	public void removeByC_AEERC(long companyId, String audienceEntryERC);

	/**
	 * Returns the number of asset list entry audience entry rels where companyId = &#63; and audienceEntryERC = &#63;.
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @return the number of matching asset list entry audience entry rels
	 */
	public int countByC_AEERC(long companyId, String audienceEntryERC);

	/**
	 * Returns the asset list entry audience entry rel where assetListEntryId = &#63; and audienceEntryERC = &#63; or throws a <code>NoSuchEntryAudienceEntryRelException</code> if it could not be found.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERC the audience entry erc
	 * @return the matching asset list entry audience entry rel
	 * @throws NoSuchEntryAudienceEntryRelException if a matching asset list entry audience entry rel could not be found
	 */
	public AssetListEntryAudienceEntryRel findByA_AEERC(
			long assetListEntryId, String audienceEntryERC)
		throws NoSuchEntryAudienceEntryRelException;

	/**
	 * Returns the asset list entry audience entry rel where assetListEntryId = &#63; and audienceEntryERC = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERC the audience entry erc
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching asset list entry audience entry rel, or <code>null</code> if a matching asset list entry audience entry rel could not be found
	 */
	public AssetListEntryAudienceEntryRel fetchByA_AEERC(
		long assetListEntryId, String audienceEntryERC, boolean useFinderCache);

	/**
	 * Removes the asset list entry audience entry rel where assetListEntryId = &#63; and audienceEntryERC = &#63; from the database.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERC the audience entry erc
	 * @return the asset list entry audience entry rel that was removed
	 */
	public AssetListEntryAudienceEntryRel removeByA_AEERC(
			long assetListEntryId, String audienceEntryERC)
		throws NoSuchEntryAudienceEntryRelException;

	/**
	 * Returns the number of asset list entry audience entry rels where assetListEntryId = &#63; and audienceEntryERC = &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERC the audience entry erc
	 * @return the number of matching asset list entry audience entry rels
	 */
	public int countByA_AEERC(long assetListEntryId, String audienceEntryERC);

	/**
	 * Returns an ordered range of all the asset list entry audience entry rels where assetListEntryId = &#63; and audienceEntryERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERC the audience entry erc
	 * @param start the lower bound of the range of asset list entry audience entry rels
	 * @param end the upper bound of the range of asset list entry audience entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entry audience entry rels
	 */
	public java.util.List<AssetListEntryAudienceEntryRel> findByA_AEERC_C(
		long assetListEntryId, String audienceEntryERC, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<AssetListEntryAudienceEntryRel> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first asset list entry audience entry rel in the ordered set where assetListEntryId = &#63; and audienceEntryERC = &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERC the audience entry erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry audience entry rel
	 * @throws NoSuchEntryAudienceEntryRelException if a matching asset list entry audience entry rel could not be found
	 */
	public AssetListEntryAudienceEntryRel findByA_AEERC_C_First(
			long assetListEntryId, String audienceEntryERC,
			com.liferay.portal.kernel.util.OrderByComparator
				<AssetListEntryAudienceEntryRel> orderByComparator)
		throws NoSuchEntryAudienceEntryRelException;

	/**
	 * Returns the first asset list entry audience entry rel in the ordered set where assetListEntryId = &#63; and audienceEntryERC = &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERC the audience entry erc
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching asset list entry audience entry rel, or <code>null</code> if a matching asset list entry audience entry rel could not be found
	 */
	public AssetListEntryAudienceEntryRel fetchByA_AEERC_C_First(
		long assetListEntryId, String audienceEntryERC,
		com.liferay.portal.kernel.util.OrderByComparator
			<AssetListEntryAudienceEntryRel> orderByComparator);

	/**
	 * Returns an ordered range of all the asset list entry audience entry rels where assetListEntryId = &#63; and audienceEntryERC = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERCs the audience entry ercs
	 * @param start the lower bound of the range of asset list entry audience entry rels
	 * @param end the upper bound of the range of asset list entry audience entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching asset list entry audience entry rels
	 */
	public java.util.List<AssetListEntryAudienceEntryRel> findByA_AEERC_C(
		long assetListEntryId, String[] audienceEntryERCs, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<AssetListEntryAudienceEntryRel> orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the asset list entry audience entry rels where assetListEntryId = &#63; and audienceEntryERC = &#63; from the database.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERC the audience entry erc
	 */
	public void removeByA_AEERC_C(
		long assetListEntryId, String audienceEntryERC);

	/**
	 * Returns the number of asset list entry audience entry rels where assetListEntryId = &#63; and audienceEntryERC = &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERC the audience entry erc
	 * @return the number of matching asset list entry audience entry rels
	 */
	public int countByA_AEERC_C(long assetListEntryId, String audienceEntryERC);

	/**
	 * Returns the number of asset list entry audience entry rels where assetListEntryId = &#63; and audienceEntryERC = any &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERCs the audience entry ercs
	 * @return the number of matching asset list entry audience entry rels
	 */
	public int countByA_AEERC_C(
		long assetListEntryId, String[] audienceEntryERCs);

	/**
	 * Creates a new asset list entry audience entry rel with the primary key. Does not add the asset list entry audience entry rel to the database.
	 *
	 * @param assetListEntryAudienceEntryRelId the primary key for the new asset list entry audience entry rel
	 * @return the new asset list entry audience entry rel
	 */
	public AssetListEntryAudienceEntryRel create(
		long assetListEntryAudienceEntryRelId);

	/**
	 * Removes the asset list entry audience entry rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param assetListEntryAudienceEntryRelId the primary key of the asset list entry audience entry rel
	 * @return the asset list entry audience entry rel that was removed
	 * @throws NoSuchEntryAudienceEntryRelException if a asset list entry audience entry rel with the primary key could not be found
	 */
	public AssetListEntryAudienceEntryRel remove(
			long assetListEntryAudienceEntryRelId)
		throws NoSuchEntryAudienceEntryRelException;

	public AssetListEntryAudienceEntryRel updateImpl(
		AssetListEntryAudienceEntryRel assetListEntryAudienceEntryRel);

	/**
	 * Returns the asset list entry audience entry rel with the primary key or throws a <code>NoSuchEntryAudienceEntryRelException</code> if it could not be found.
	 *
	 * @param assetListEntryAudienceEntryRelId the primary key of the asset list entry audience entry rel
	 * @return the asset list entry audience entry rel
	 * @throws NoSuchEntryAudienceEntryRelException if a asset list entry audience entry rel with the primary key could not be found
	 */
	public AssetListEntryAudienceEntryRel findByPrimaryKey(
			long assetListEntryAudienceEntryRelId)
		throws NoSuchEntryAudienceEntryRelException;

	/**
	 * Returns the asset list entry audience entry rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param assetListEntryAudienceEntryRelId the primary key of the asset list entry audience entry rel
	 * @return the asset list entry audience entry rel, or <code>null</code> if a asset list entry audience entry rel with the primary key could not be found
	 */
	public AssetListEntryAudienceEntryRel fetchByPrimaryKey(
		long assetListEntryAudienceEntryRelId);

	/**
	 * Returns the asset list entry audience entry rel where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching asset list entry audience entry rel, or <code>null</code> if a matching asset list entry audience entry rel could not be found
	 */
	public default AssetListEntryAudienceEntryRel fetchByUUID_G(
		String uuid, long groupId) {

		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns the asset list entry audience entry rel where assetListEntryId = &#63; and audienceEntryERC = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERC the audience entry erc
	 * @return the matching asset list entry audience entry rel, or <code>null</code> if a matching asset list entry audience entry rel could not be found
	 */
	public default AssetListEntryAudienceEntryRel fetchByA_AEERC(
		long assetListEntryId, String audienceEntryERC) {

		return fetchByA_AEERC(assetListEntryId, audienceEntryERC, true);
	}

	/**
	 * Returns all the asset list entry audience entry rels where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching asset list entry audience entry rels
	 */
	public default java.util.List<AssetListEntryAudienceEntryRel> findByUuid(
		String uuid) {

		return findByUuid(
			uuid, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the asset list entry audience entry rels where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of asset list entry audience entry rels
	 * @param end the upper bound of the range of asset list entry audience entry rels (not inclusive)
	 * @return the range of matching asset list entry audience entry rels
	 */
	public default java.util.List<AssetListEntryAudienceEntryRel> findByUuid(
		String uuid, int start, int end) {

		return findByUuid(uuid, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the asset list entry audience entry rels where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of asset list entry audience entry rels
	 * @param end the upper bound of the range of asset list entry audience entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entry audience entry rels
	 */
	public default java.util.List<AssetListEntryAudienceEntryRel> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<AssetListEntryAudienceEntryRel> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the asset list entry audience entry rels where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching asset list entry audience entry rels
	 */
	public default java.util.List<AssetListEntryAudienceEntryRel> findByUuid_C(
		String uuid, long companyId) {

		return findByUuid_C(
			uuid, companyId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the asset list entry audience entry rels where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of asset list entry audience entry rels
	 * @param end the upper bound of the range of asset list entry audience entry rels (not inclusive)
	 * @return the range of matching asset list entry audience entry rels
	 */
	public default java.util.List<AssetListEntryAudienceEntryRel> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the asset list entry audience entry rels where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of asset list entry audience entry rels
	 * @param end the upper bound of the range of asset list entry audience entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entry audience entry rels
	 */
	public default java.util.List<AssetListEntryAudienceEntryRel> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<AssetListEntryAudienceEntryRel> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the asset list entry audience entry rels where assetListEntryId = &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @return the matching asset list entry audience entry rels
	 */
	public default java.util.List<AssetListEntryAudienceEntryRel>
		findByAssetListEntryId(long assetListEntryId) {

		return findByAssetListEntryId(
			assetListEntryId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the asset list entry audience entry rels where assetListEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param start the lower bound of the range of asset list entry audience entry rels
	 * @param end the upper bound of the range of asset list entry audience entry rels (not inclusive)
	 * @return the range of matching asset list entry audience entry rels
	 */
	public default java.util.List<AssetListEntryAudienceEntryRel>
		findByAssetListEntryId(long assetListEntryId, int start, int end) {

		return findByAssetListEntryId(assetListEntryId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the asset list entry audience entry rels where assetListEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param start the lower bound of the range of asset list entry audience entry rels
	 * @param end the upper bound of the range of asset list entry audience entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entry audience entry rels
	 */
	public default java.util.List<AssetListEntryAudienceEntryRel>
		findByAssetListEntryId(
			long assetListEntryId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<AssetListEntryAudienceEntryRel> orderByComparator) {

		return findByAssetListEntryId(
			assetListEntryId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the asset list entry audience entry rels where companyId = &#63; and audienceEntryERC = &#63;.
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @return the matching asset list entry audience entry rels
	 */
	public default java.util.List<AssetListEntryAudienceEntryRel> findByC_AEERC(
		long companyId, String audienceEntryERC) {

		return findByC_AEERC(
			companyId, audienceEntryERC,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the asset list entry audience entry rels where companyId = &#63; and audienceEntryERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @param start the lower bound of the range of asset list entry audience entry rels
	 * @param end the upper bound of the range of asset list entry audience entry rels (not inclusive)
	 * @return the range of matching asset list entry audience entry rels
	 */
	public default java.util.List<AssetListEntryAudienceEntryRel> findByC_AEERC(
		long companyId, String audienceEntryERC, int start, int end) {

		return findByC_AEERC(
			companyId, audienceEntryERC, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the asset list entry audience entry rels where companyId = &#63; and audienceEntryERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param audienceEntryERC the audience entry erc
	 * @param start the lower bound of the range of asset list entry audience entry rels
	 * @param end the upper bound of the range of asset list entry audience entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entry audience entry rels
	 */
	public default java.util.List<AssetListEntryAudienceEntryRel> findByC_AEERC(
		long companyId, String audienceEntryERC, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<AssetListEntryAudienceEntryRel> orderByComparator) {

		return findByC_AEERC(
			companyId, audienceEntryERC, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the asset list entry audience entry rels where assetListEntryId = &#63; and audienceEntryERC = &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERC the audience entry erc
	 * @return the matching asset list entry audience entry rels
	 */
	public default java.util.List<AssetListEntryAudienceEntryRel>
		findByA_AEERC_C(long assetListEntryId, String audienceEntryERC) {

		return findByA_AEERC_C(
			assetListEntryId, audienceEntryERC,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the asset list entry audience entry rels where assetListEntryId = &#63; and audienceEntryERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERC the audience entry erc
	 * @param start the lower bound of the range of asset list entry audience entry rels
	 * @param end the upper bound of the range of asset list entry audience entry rels (not inclusive)
	 * @return the range of matching asset list entry audience entry rels
	 */
	public default java.util.List<AssetListEntryAudienceEntryRel>
		findByA_AEERC_C(
			long assetListEntryId, String audienceEntryERC, int start,
			int end) {

		return findByA_AEERC_C(
			assetListEntryId, audienceEntryERC, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the asset list entry audience entry rels where assetListEntryId = &#63; and audienceEntryERC = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERC the audience entry erc
	 * @param start the lower bound of the range of asset list entry audience entry rels
	 * @param end the upper bound of the range of asset list entry audience entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entry audience entry rels
	 */
	public default java.util.List<AssetListEntryAudienceEntryRel>
		findByA_AEERC_C(
			long assetListEntryId, String audienceEntryERC, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<AssetListEntryAudienceEntryRel> orderByComparator) {

		return findByA_AEERC_C(
			assetListEntryId, audienceEntryERC, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns all the asset list entry audience entry rels where assetListEntryId = &#63; and audienceEntryERC = any &#63;.
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERCs the audience entry ercs
	 * @return the matching asset list entry audience entry rels
	 */
	public default java.util.List<AssetListEntryAudienceEntryRel>
		findByA_AEERC_C(long assetListEntryId, String[] audienceEntryERCs) {

		return findByA_AEERC_C(
			assetListEntryId, audienceEntryERCs,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the asset list entry audience entry rels where assetListEntryId = &#63; and audienceEntryERC = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERCs the audience entry ercs
	 * @param start the lower bound of the range of asset list entry audience entry rels
	 * @param end the upper bound of the range of asset list entry audience entry rels (not inclusive)
	 * @return the range of matching asset list entry audience entry rels
	 */
	public default java.util.List<AssetListEntryAudienceEntryRel>
		findByA_AEERC_C(
			long assetListEntryId, String[] audienceEntryERCs, int start,
			int end) {

		return findByA_AEERC_C(
			assetListEntryId, audienceEntryERCs, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the asset list entry audience entry rels where assetListEntryId = &#63; and audienceEntryERC = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.list.model.impl.AssetListEntryAudienceEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param assetListEntryId the asset list entry ID
	 * @param audienceEntryERCs the audience entry ercs
	 * @param start the lower bound of the range of asset list entry audience entry rels
	 * @param end the upper bound of the range of asset list entry audience entry rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching asset list entry audience entry rels
	 */
	public default java.util.List<AssetListEntryAudienceEntryRel>
		findByA_AEERC_C(
			long assetListEntryId, String[] audienceEntryERCs, int start,
			int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<AssetListEntryAudienceEntryRel> orderByComparator) {

		return findByA_AEERC_C(
			assetListEntryId, audienceEntryERCs, start, end, orderByComparator,
			true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1358530523